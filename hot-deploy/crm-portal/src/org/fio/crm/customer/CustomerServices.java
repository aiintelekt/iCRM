package org.fio.crm.customer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.admin.portal.util.DataUtil;
import org.fio.crm.util.UtilMessage;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class CustomerServices {
	private static final String MODULE = CustomerServices.class.getName();
	public static final String RESOURCE = "crmUiLabels";
	
	public static Map<String, Object> createCustomer(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> results = ServiceUtil.returnSuccess();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			/*
			if (!security.hasPermission("CRM_CUSTOMER_CREATE", userLogin)) {
				return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
			} */
			
			// create new person
			// automatically set the parameters
			if(UtilValidate.isNotEmpty(context.get("birthDate")))
				context.put("birthDate", DataUtil.convertDateTimestamp((String) context.get("birthDate"), df, DateTimeTypeConstant.DATE, DateTimeTypeConstant.SQL_DATE));
            ModelService createPersonService = dctx.getModelService("createPerson");
            Map<String, Object> personContext = createPersonService.makeValid(context, ModelService.IN_PARAM);
            Map<String, Object> createPersonResult = dispatcher.runSync("createPerson", personContext);
            if (ServiceUtil.isError(createPersonResult) || ServiceUtil.isFailure(createPersonResult)) {
                return createPersonResult;
            }
			String partyId = (String) createPersonResult.get("partyId");
			
			Map<String, Object> input = new HashMap<String, Object>();
			// create party role
			String roleTypeId = UtilValidate.isNotEmpty(context.get("roleTypeId")) ? (String) context.get("roleTypeId") : "CUSTOMER";
			
			Map<String, Object>  partyRoleContext = new HashMap<String, Object>();
			partyRoleContext.put("partyId", partyId);
			partyRoleContext.put("roleTypeId", roleTypeId);
			partyRoleContext.put("userLogin", userLogin);
			Map<String, Object> partyRoleResult =  dispatcher.runSync("createPartyRole", partyRoleContext);
			if (ServiceUtil.isError(partyRoleResult) || ServiceUtil.isFailure(partyRoleResult)) {
                return partyRoleResult;
            }
						
			// create PartySupplementalData
			GenericValue partyData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", partyId));
			partyData.setNonPKFields(context);
			partyData.create();
			
			// create basic contact information
			ModelService service = dctx.getModelService("crmsfa.createBasicContactInfoForParty");
			input = service.makeValid(context, "IN");
			input.put("partyId", partyId);
			Map<String, Object> contactInfoResult = dispatcher.runSync(service.name, input);
			if (ServiceUtil.isError(contactInfoResult) || ServiceUtil.isFailure(contactInfoResult)) {
                return contactInfoResult;
            }
			Debug.logInfo("---create contact info service ------"+contactInfoResult,MODULE);
			
			results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "CustomerCreatedSuccessfully", locale));
			results.put("partyId", partyId);
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
			results = ServiceUtil.returnError("Error : "+e.getMessage());
		}
		return results;
	}
	
	public static Map<String, Object> updateCustomer(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> results = ServiceUtil.returnSuccess();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			// create new person
			// automatically set the parameters
			if(UtilValidate.isNotEmpty(context.get("birthDate")))
				context.put("birthDate", DataUtil.convertDateTimestamp((String) context.get("birthDate"), df, DateTimeTypeConstant.DATE, DateTimeTypeConstant.SQL_DATE));
			
            ModelService createPersonService = dctx.getModelService("updatePerson");
            Map<String, Object> personContext = createPersonService.makeValid(context, ModelService.IN_PARAM);
            
            Map<String, Object> createPersonResult = dispatcher.runSync("updatePerson", personContext);
            if (ServiceUtil.isError(createPersonResult) || ServiceUtil.isFailure(createPersonResult)) {
                return createPersonResult;
            }
			String partyId = (String) createPersonResult.get("partyId");
						
			// create PartySupplementalData
			GenericValue partyData = EntityQuery.use(delegator).from("PartySupplementalData").where("partyId", partyId).queryOne();
			if(UtilValidate.isNotEmpty(partyData)) {
				partyData.setNonPKFields(context);
				partyData.store();
			}
			
			
			results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "CustomerUpdatedSuccessfully", locale));
			results.put("partyId", partyId);
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			results = ServiceUtil.returnError("Error : "+e.getMessage());
		}
		return results;
	}
	
	public static Map<String, Object> findCustomers(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> results = ServiceUtil.returnSuccess();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String customerId = (String) context.get("partyId");
		String firstName = (String) context.get("firstName");
		String lastName = (String) context.get("lastName");
		String emailAddress = (String) context.get("infoString");
		String contactNumber = (String) context.get("contactNumber");
		String countryCode = (String) context.get("countryCode");
		String areaCode = (String) context.get("areaCode");
		String roleTypeId = UtilValidate.isNotEmpty(context.get("roleTypeId")) ? (String) context.get("roleTypeId") :"CUSTOMER";
		try {
			Set<String> fieldsToSelect = new TreeSet<String>();
    		fieldsToSelect.add("partyId");
    		fieldsToSelect.add("personalTitle");
    		fieldsToSelect.add("firstName");
    		fieldsToSelect.add("lastName");
    		fieldsToSelect.add("birthDate");
    		fieldsToSelect.add("description");
    		fieldsToSelect.add("statusId");
    		fieldsToSelect.add("createdDate");
    		fieldsToSelect.add("preferredCurrencyUomId");
    		
			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
    		dynamicViewEntity.addMemberEntity("PER", "Person");
    		dynamicViewEntity.addAlias("PER", "partyId");
    		dynamicViewEntity.addAlias("PER", "personalTitle");
    		dynamicViewEntity.addAlias("PER", "firstName");
    		dynamicViewEntity.addAlias("PER", "lastName");
    		dynamicViewEntity.addAlias("PER", "birthDate");
    		
    		dynamicViewEntity.addMemberEntity("PTY", "Party");
    		dynamicViewEntity.addAlias("PTY", "description");
    		dynamicViewEntity.addAlias("PTY", "statusId");
    		dynamicViewEntity.addAlias("PTY", "createdDate");
    		dynamicViewEntity.addAlias("PTY", "preferredCurrencyUomId");
    		dynamicViewEntity.addViewLink("PER", "PTY", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
    		
    		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    		if(UtilValidate.isNotEmpty(customerId))
    			conditions.add(EntityCondition.makeCondition("partyId",EntityOperator.LIKE,customerId+"%"));
    		if(UtilValidate.isNotEmpty(firstName))
    			conditions.add(EntityCondition.makeCondition("firstName",EntityOperator.LIKE,"%"+firstName+"%"));
    		if(UtilValidate.isNotEmpty(lastName))
    			conditions.add(EntityCondition.makeCondition("lastName",EntityOperator.LIKE,"%"+lastName+"%"));
    		
                if (UtilValidate.isNotEmpty(roleTypeId)) {
                    GenericValue currentRole = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId),false);
                    if(UtilValidate.isEmpty(currentRole)) {
                    	return ServiceUtil.returnError("Customer Role not exists.");
                    }
                }
                
    		
    		if(UtilValidate.isNotEmpty(roleTypeId)) {
    			dynamicViewEntity.addMemberEntity("PR", "PartyRole");
    			dynamicViewEntity.addAlias("PR", "roleTypeId");
    			dynamicViewEntity.addViewLink("PTY", "PR", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
    			conditions.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,roleTypeId));
    		}
    		List<String> partyIds = new ArrayList<String>();
    		if(UtilValidate.isNotEmpty(emailAddress)) {
    			List<String> emailPartyIds = new ArrayList<String>();
    			DynamicViewEntity dynamicView = new DynamicViewEntity();
    			dynamicView.addMemberEntity("CM", "ContactMech");
    			dynamicView.addMemberEntity("PCM", "PartyContactMech");
    			dynamicView.addAlias("CM", "infoString");
    			dynamicView.addAlias("PCM", "partyId");
    			dynamicView.addAlias("PCM", "fromDate");
    			dynamicView.addAlias("PCM", "thruDate");
    			dynamicView.addViewLink("CM", "PCM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
        		
    			List<GenericValue> emailPartyList = EntityQuery.use(delegator).select("partyId").from(dynamicView).where(EntityCondition.makeCondition("infoString",EntityOperator.LIKE,emailAddress+"%")).filterByDate().queryList();
    			if(UtilValidate.isNotEmpty(emailPartyList)) {
    				emailPartyIds = EntityUtil.getFieldListFromEntityList(emailPartyList, "partyId", true);
    				partyIds.addAll(DataUtil.retainAll(partyIds, emailPartyIds));
    			}
    		}
    		
    		if(UtilValidate.isNotEmpty(contactNumber) || UtilValidate.isNotEmpty(countryCode) || UtilValidate.isNotEmpty(areaCode)) {
    			List<String> phonePartyIds = new ArrayList<String>();
    			DynamicViewEntity dynamicView1 = new DynamicViewEntity();
    			dynamicView1.addMemberEntity("TM", "TelecomNumber");
    			dynamicView1.addMemberEntity("PCM", "PartyContactMech");
    			dynamicView1.addAlias("PCM", "partyId");
    			dynamicView1.addAlias("PCM", "thruDate");
    			dynamicView1.addAlias("PCM", "fromDate");
    			dynamicView1.addAlias("TM", "countryCode");
    			dynamicView1.addAlias("TM", "areaCode");
    			dynamicView1.addAlias("TM", "contactNumber");
    			dynamicView1.addViewLink("PCM", "TM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
    			List<EntityCondition> phoneCondition = new ArrayList<EntityCondition>();
        		if(UtilValidate.isNotEmpty(contactNumber))
        			phoneCondition.add(EntityCondition.makeCondition("contactNumber",EntityOperator.LIKE,"%"+contactNumber+"%"));
        		if(UtilValidate.isNotEmpty(countryCode))
        			phoneCondition.add(EntityCondition.makeCondition("countryCode",EntityOperator.LIKE,countryCode+"%"));
        		if(UtilValidate.isNotEmpty(areaCode))
        			phoneCondition.add(EntityCondition.makeCondition("areaCode",EntityOperator.LIKE,areaCode+"%"));
        		
        		List<GenericValue> emailPartyList = EntityQuery.use(delegator).select("partyId").from(dynamicView1).where(phoneCondition).filterByDate().queryList();
    			if(UtilValidate.isNotEmpty(emailPartyList)) {
    				phonePartyIds = EntityUtil.getFieldListFromEntityList(emailPartyList, "partyId", true);
    				partyIds.addAll(DataUtil.retainAll(partyIds, phonePartyIds));
    			}
    		}
    		
    		int maxRows = 0;
    		if(UtilValidate.isNotEmpty(partyIds))
    			conditions.add(EntityCondition.makeCondition("partyId",EntityOperator.IN, partyIds));
    		//if(UtilValidate.isEmpty(conditions))
    			maxRows = 3000;
    		System.out.println("fieldsToSelect-->"+fieldsToSelect);
    		List<GenericValue> partyList = EntityQuery.use(delegator).select(fieldsToSelect).from(dynamicViewEntity).where(EntityCondition.makeCondition(conditions,EntityOperator.AND)).distinct().maxRows(maxRows).queryList();
			//if(UtilValidate.isNotEmpty(partyList)) {				
				results.put("partyList", partyList);
			//}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			results = ServiceUtil.returnError("Error : "+e.getMessage());
		}
		return results;
	}
	
}
