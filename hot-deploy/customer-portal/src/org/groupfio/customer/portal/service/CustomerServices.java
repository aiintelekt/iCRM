package org.groupfio.customer.portal.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.admin.portal.util.DataUtil;
import org.fio.crm.party.PartyHelper;
import org.groupfio.common.portal.util.LoyaltyUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
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
	GenericValue userLogin = (GenericValue) context.get("userLogin");
	Locale locale = (Locale) context.get("locale");
	Map<String, Object> results = ServiceUtil.returnSuccess();
	String isContractor = (String) context.get("isContractor");
	String firstName = (String) context.get("firstName");
	String lastName = (String) context.get("lastName");
 	String loyaltyIdStatus = (String) context.get("loyaltyEnableStatus");
	String generalAddress1 = (String) context.get("generalAddress1");
	String generalCity = (String) context.get("generalCity");
	String generalCountryGeoId = (String) context.get("generalCountryGeoId");
	String generalStateProvinceGeoId = (String) context.get("generalStateProvinceGeoId");
	String generalPostalCode = (String) context.get("generalPostalCode");
	String primaryPhoneNumber = (String) context.get("primaryPhoneNumber");
	String primaryEmail = (String) context.get("primaryEmail");
	String birthDateStr = (String) context.get("birthDate");
	boolean GenerateLoyaltyNumber = false;
	try {
		// create new person
		// automatically set the parameters
		if(UtilValidate.isNotEmpty(birthDateStr)){
			Date birthDate = DataUtil.convertDateTimestamp(birthDateStr, null, DateTimeTypeConstant.DATE, DateTimeTypeConstant.SQL_DATE);
			context.put("birthDate", birthDate);
		}
		String localTimeZone = (String) context.get("timeZoneDesc");	
		context.put("localTimeZone", UtilValidate.isNotEmpty(localTimeZone)?localTimeZone:null);

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
		
		String timeZoneDesc = (String) context.get("timeZoneDesc");	
		Debug.log("timeZoneDesc==********=="+timeZoneDesc);
		GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
		if(UtilValidate.isNotEmpty(party)){
			party.put("roleTypeId", roleTypeId);
			party.put("createdDate", UtilDateTime.nowTimestamp());
			party.put("createdByUserLogin", userLogin.getString("userLoginId"));
			party.put("timeZoneDesc", timeZoneDesc);
			party.store();
		}
		
		// if initial data source is provided, add it
		String dataSourceId = (String) context.get("dataSourceId");
		if (UtilValidate.isNotEmpty(dataSourceId)) {
			Map<String, Object> serviceResults = dispatcher.runSync("crmsfa.addAccountDataSource",
					UtilMisc.toMap("partyId", partyId, "dataSourceId", dataSourceId, "userLogin", userLogin));
			if (ServiceUtil.isError(serviceResults)) {
				return ServiceUtil.returnError("CrmErrorCreateCustomerFail : "); 
			}
		}
		
		// create PartySupplementalData
		GenericValue partyData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", partyId));
		partyData.setNonPKFields(context);
		partyData.create();
		
		// create a party relationship between the userLogin and the Customer
		// with partyRelationshipTypeId RESPONSIBLE_FOR
		createResponsibleCustomerRelationshipForParty(userLogin.getString("partyId"), partyId, userLogin,delegator, dispatcher);

		// create basic contact information
		ModelService service = dctx.getModelService("crmsfa.createBasicContactInfoForParty");
		input = service.makeValid(context, "IN");
		input.put("partyId", partyId);
		Map<String, Object> contactInfoResult = dispatcher.runSync(service.name, input);
		if (ServiceUtil.isError(contactInfoResult) || ServiceUtil.isFailure(contactInfoResult)) {
            return contactInfoResult;
        }
		Debug.logInfo("---create contact info service ------"+contactInfoResult,MODULE);
		
		// check for contractor[start]
		if (UtilValidate.isNotEmpty(isContractor) && isContractor.equals("Y")) {
			GenericValue partyRole = delegator.makeValue("PartyRole");
			partyRole.put("partyId", partyId);
			partyRole.put("roleTypeId", "CONTRACTOR");
			delegator.createOrStore(partyRole);
			
			partyData.put("supplementalPartyTypeId", "CONTRACTOR");
			partyData.store();
		}
		// check for contractor[end]
		
		//add party attribute to store company name
		String partyIdentification=org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, (String)context.get("companyName"), "COMPANY_NAME");
		String loyaltyCustomerPermission = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LOYALTY_CUSTOMER_VALIDATION");
		if (UtilValidate.isNotEmpty(loyaltyCustomerPermission) && loyaltyCustomerPermission.equals("Y")) {
			if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(loyaltyIdStatus) && loyaltyIdStatus.equals("Y") && UtilValidate.isNotEmpty(firstName)&&
					UtilValidate.isNotEmpty(lastName) && UtilValidate.isNotEmpty(primaryEmail)) {
				GenerateLoyaltyNumber = true;
			}
		}
		/*String loyaltyCustAddressValidation = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LOYALTY_CUST_POSTAL_ADDR_VALIDATION");
		if (UtilValidate.isNotEmpty(loyaltyCustAddressValidation) && loyaltyCustAddressValidation.equals("Y")) {
			if(UtilValidate.isNotEmpty(partyId) && loyaltyIdStatus.equals("Y") && UtilValidate.isNotEmpty(generalAddress1)&&
					UtilValidate.isNotEmpty(generalCity)&& UtilValidate.isNotEmpty(generalCountryGeoId)&& UtilValidate.isNotEmpty(generalStateProvinceGeoId)&& 
					UtilValidate.isNotEmpty(generalPostalCode)) {
				GenerateLoyaltyNumber = true;
			}
		}*/
		if(UtilValidate.isNotEmpty(loyaltyCustomerPermission) && loyaltyCustomerPermission.equals("N")){
			if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(loyaltyIdStatus) && loyaltyIdStatus.equals("Y"))
				GenerateLoyaltyNumber = true;
		}
		if(GenerateLoyaltyNumber == true)
			LoyaltyUtil.assignLoyaltyId(partyId, delegator);
		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "CustomerCreatedSuccessfully", locale));
		results.put("partyId", partyId);
	} catch (Exception e) {
		Debug.logError(e.getMessage(), MODULE);
		results = ServiceUtil.returnError("Error : "+e.getMessage());
	}
	return results;
}

public static boolean createResponsibleCustomerRelationshipForParty(
		String partyId, String customerPartyId, GenericValue userLogin,
		Delegator delegator, LocalDispatcher dispatcher)
				throws GenericServiceException, GenericEntityException {
	List<String> roleList=new ArrayList<String>();
	roleList.add("EMPLOYEE");
	return PartyHelper.createNewPartyToRelationship(partyId,
			customerPartyId, "CUSTOMER", "RESPONSIBLE_FOR", "CONTACT_OWNER",
			roleList, true, userLogin, delegator,
			dispatcher);
}

public static Map<String, Object> updateCustomer(DispatchContext dctx, Map<String, Object> context) {
	Delegator delegator = dctx.getDelegator();
	LocalDispatcher dispatcher = dctx.getDispatcher();
	GenericValue userLogin = (GenericValue) context.get("userLogin");
	Locale locale = (Locale) context.get("locale");
	Map<String, Object> results = ServiceUtil.returnSuccess();
	String customerId= (String) context.get("partyId");
	String isContractor = (String) context.get("isContractor");
	String birthDateStr = (String) context.get("birthDate");
	try {
		
		if(UtilValidate.isNotEmpty(birthDateStr)){
			Date birthDate = DataUtil.convertDateTimestamp(birthDateStr, null, DateTimeTypeConstant.DATE, DateTimeTypeConstant.SQL_DATE);
			context.put("birthDate", birthDate);
		}
		String localTimeZone = (String) context.get("timeZoneDesc");	
		context.put("localTimeZone", UtilValidate.isNotEmpty(localTimeZone)?localTimeZone:null);

       ModelService createPersonService = dctx.getModelService("updatePerson");
        Map<String, Object> personContext = createPersonService.makeValid(context, ModelService.IN_PARAM);
        
        Map<String, Object> createPersonResult = dispatcher.runSync("updatePerson", personContext);
        if (ServiceUtil.isError(createPersonResult) || ServiceUtil.isFailure(createPersonResult)) {
            return createPersonResult;
        }
		// create PartySupplementalData
		GenericValue partyData = EntityQuery.use(delegator).from("PartySupplementalData").where("partyId", customerId).queryOne();
		if(UtilValidate.isNotEmpty(partyData)) {
			partyData.setNonPKFields(context);
			partyData.put("supplementalPartyTypeId", null);
			partyData.store();
		}
		
		String dataSourceId = (String) context.get("dataSourceId");
		GenericValue partyDataSource = null;
		if (UtilValidate.isNotEmpty(dataSourceId)) {
			delegator.removeByCondition("PartyDataSource", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, customerId));
			Map<String, Object> serviceResults = dispatcher.runSync("crmsfa.addAccountDataSource",
					UtilMisc.toMap("partyId", customerId, "dataSourceId", dataSourceId, "userLogin", userLogin));
			if (ServiceUtil.isError(serviceResults)) {
				return ServiceUtil.returnError("CrmErrorCreateCustomerFail : "); 
			}
		}
		
		// update party classifications 
        String gender = (String) context.get("gender"); 
		if(UtilValidate.isNotEmpty(gender))
		{
			List<GenericValue> customFieldPartyClassificationList = delegator.findByAnd("CustomFieldPartyClassification", UtilMisc.toMap("groupId","GENDER","partyId",customerId,"customFieldId",gender), null, false);
			if(customFieldPartyClassificationList == null || customFieldPartyClassificationList.size() == 0) {
				List<GenericValue> customFieldPartyClassificationList1 = delegator.findByAnd("CustomFieldPartyClassification", UtilMisc.toMap("groupId","GENDER","partyId",customerId), null, false);
				if(customFieldPartyClassificationList1 != null && customFieldPartyClassificationList1.size() > 0) {
					delegator.removeAll(customFieldPartyClassificationList1);
				}
				// create a new segment
				GenericValue genderSeg = delegator.makeValue("CustomFieldPartyClassification", UtilMisc.toMap("groupId","GENDER","partyId",customerId));
				genderSeg.set("customFieldId", gender);
				genderSeg.create();
			} 
		}
		
		//updating timezone
		String timeZoneDesc = null;
        if(UtilValidate.isNotEmpty(context.get("timeZoneDesc"))){
    		timeZoneDesc = (String)context.get("timeZoneDesc");
    	}
    	GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", customerId), null, false) );
    	if(UtilValidate.isNotEmpty(party)) {
    		party.put("lastModifiedDate", UtilDateTime.nowTimestamp());
    		party.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
    		party.put("timeZoneDesc", timeZoneDesc);
    		party.store();
    	}
    	
    	// check for contractor[start]
    	//delegator.removeByAnd("PartyRole", UtilMisc.toMap("partyId", customerId, "roleTypeId", "CONTRACTOR"));
		if (UtilValidate.isNotEmpty(isContractor) && "Y".equals(isContractor)) {
			long count = EntityQuery.use(delegator).from("PartyRole").where("partyId", customerId, "roleTypeId", "CONTRACTOR").queryCount();
			if(count == 0) {
    			GenericValue partyRole = delegator.makeValue("PartyRole");
    			partyRole.put("partyId", customerId);
    			partyRole.put("roleTypeId", "CONTRACTOR");
    			delegator.createOrStore(partyRole);
			}
			partyData.put("supplementalPartyTypeId", "CONTRACTOR");
			partyData.store();
			
		}
		// check for contractor[end]
		
		//update party attribute to store company name
		
		if(UtilValidate.isNotEmpty(context.get("companyName"))) {
			String partyIdentification=org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, customerId, (String)context.get("companyName"), "COMPANY_NAME");
		}
		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "CustomerUpdatedSuccessfully", locale));
		results.put("partyId", customerId);
	} catch (Exception e) {
		Debug.logError(e.getMessage(), MODULE);

		results = ServiceUtil.returnError("Error : "+e.getMessage());
	}
	return results;
}
}
