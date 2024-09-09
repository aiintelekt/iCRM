package org.fio.admin.portal.service;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.fio.admin.portal.constant.AdminPortalConstant.BusinessUnitConstant;
import org.fio.homeapps.constants.GlobalConstants.ModeOfAction;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.util.Locale;
import org.ofbiz.base.util.UtilProperties;

/**
 * 
 * @author Prabhakar
 * @since 21-01-2020
 * 
 */
public class OpportunityConfigServices {
	private OpportunityConfigServices() {
	}

	private static final String MODULE = OpportunityConfigServices.class.getName();
	public static final String RESOURCE = "AdminPortalUiLabels";

	public static Map<String, Object> createOpportunityConfig(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> results = ServiceUtil.returnSuccess();
		String enumId = (String) context.get("enumId");
		String enumType = (String) context.get("typeId");
		String description = (String) context.get("description");
		String parentEnumId = (String) context.get("parentEnumId");
		Locale locale = (Locale) context.get("locale");

		try {
			GenericValue enumerationTypeValue = EntityQuery.use(delegator).from("EnumerationType")
					.where("enumTypeId", enumType).queryOne();
			if (UtilValidate.isEmpty(enumerationTypeValue)) {
				GenericValue enumerationType = delegator.makeValue("EnumerationType");
				enumerationType.put("enumId", enumId);
				enumerationType.put("enumTypeId", enumType);
				enumerationType.put("description", enumType);
				enumerationType.create();
			}
			GenericValue addOpportunityConfig = delegator.makeValue("Enumeration");
			 enumId = delegator.getNextSeqId("Enumeration");
			addOpportunityConfig.put("enumId", enumId);
			addOpportunityConfig.put("enumTypeId", enumType);
			addOpportunityConfig.put("enumCode", enumId);
			addOpportunityConfig.put("description", description);
			addOpportunityConfig.put("parentEnumId", parentEnumId);
			addOpportunityConfig.put("isMultiLingual", "Y");
			addOpportunityConfig.create();

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			results = ServiceUtil.returnError("Error : " + e.getMessage());
			return results;
		}
		results = ServiceUtil
				.returnSuccess(UtilProperties.getMessage(RESOURCE, "Opportunity Config Data created successfully", locale));
		return results;
	}
	
	
	public static Map<String, Object> createOppoConfigReason(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> results = ServiceUtil.returnSuccess();
		String enumId = (String) context.get("enumId");
		String enumType = (String) context.get("typeId");
		String description = (String) context.get("description");
		String parentEnumId = (String) context.get("parentEnumId");
		Locale locale = (Locale) context.get("locale");
		String responseTypeId = (String) context.get("responseTypeId");

		try {
			GenericValue enumerationTypeValue = EntityQuery.use(delegator).from("EnumerationType")
					.where("enumTypeId", enumType).queryOne();
			if (UtilValidate.isEmpty(enumerationTypeValue)) {
				GenericValue enumerationType = delegator.makeValue("EnumerationType");
				//enumerationType.put("enumId", enumId);
				enumerationType.put("enumTypeId", enumType);
				enumerationType.put("description", enumType);
				enumerationType.create();
			}
			GenericValue addOpportunityConfig = delegator.makeValue("Enumeration");
			 enumId = delegator.getNextSeqId("Enumeration");
			addOpportunityConfig.put("enumId", enumId);
			addOpportunityConfig.put("enumTypeId", enumType);
			addOpportunityConfig.put("enumCode", enumId);
			addOpportunityConfig.put("description", description);
			addOpportunityConfig.put("parentEnumId", parentEnumId);
			addOpportunityConfig.put("isMultiLingual", "Y");
			addOpportunityConfig.put("parentEnumId", responseTypeId);
			addOpportunityConfig.create();

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			results = ServiceUtil.returnError("Error : " + e.getMessage());
			return results;
		}
		results = ServiceUtil
				.returnSuccess(UtilProperties.getMessage(RESOURCE, "Opportunity Config Data created successfully", locale));
		return results;
	}

	public static Map<String, Object> updateOpportunityConfig(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> results = ServiceUtil.returnSuccess();
		String enumType = (String) context.get("typeId");
		String enumId = (String) context.get("enumId");
		String parentEnumId = (String) context.get("parentEnumId");
		String description = (String) context.get("description");

		try {

			List<EntityCondition> conditionlist = FastList.newInstance();
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("enumId");
			fieldsToSelect.add("isMultiLingual");
			fieldsToSelect.add("description");
			fieldsToSelect.add("enumTypeId");
			fieldsToSelect.add("parentEnumId");
			GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
					UtilMisc.toMap("enumId", enumId, "enumTypeId", enumType), null, false));
			if (UtilValidate.isNotEmpty(updateConfigRecords)) {
				String desc = updateConfigRecords.getString("description");
				if (UtilValidate.isNotEmpty(desc) && UtilValidate.isNotEmpty(description)) {
					if (description.equalsIgnoreCase(desc)) {
						updateConfigRecords.put("description", description);
						updateConfigRecords.put("parentEnumId", parentEnumId);
						updateConfigRecords.put("isMultiLingual", "Y");
						updateConfigRecords.store();
						results = ServiceUtil.returnSuccess(
								UtilProperties.getMessage(RESOURCE, "Opportunity Data Updated Successfully", locale));
					} else {
						conditionlist.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumType));
						conditionlist
								.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS, description));
						EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);

						List<GenericValue> checkConfigData = EntityQuery.use(delegator).select(fieldsToSelect)
								.from("Enumeration").where(condition).maxRows(100).orderBy("-lastUpdatedTxStamp")
								.queryList();

						if (UtilValidate.isNotEmpty(checkConfigData)) {
							return ServiceUtil.returnError("Opportunity Config Data already exists");
						} else {
							updateConfigRecords.put("description", description);
							updateConfigRecords.put("parentEnumId", parentEnumId);
							updateConfigRecords.put("isMultiLingual", "Y");
							updateConfigRecords.store();
							results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE,
									"Opportunity Config Data Updated Successfully", locale));
						}
					}
				}
			}
			results.put("enumId", enumId);
		} catch (GeneralException e) {
			Debug.log("==error in updation===" + e.getMessage());
		}

		return results;
	}
	
	public static Map<String, Object> updateOppoResReasonConfig(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> results = ServiceUtil.returnSuccess();
		String enumType = (String) context.get("typeId");
		String enumId = (String) context.get("enumId");
		String parentEnumId = (String) context.get("parentEnumId");
		String description = (String) context.get("description");
		String responseTypeId = (String) context.get("responseTypeId");
		try {

			List<EntityCondition> conditionlist = FastList.newInstance();
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("enumId");
			fieldsToSelect.add("isMultiLingual");
			fieldsToSelect.add("description");
			fieldsToSelect.add("enumTypeId");
			fieldsToSelect.add("parentEnumId");
			GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
					UtilMisc.toMap("enumId", enumId, "enumTypeId", enumType), null, false));
			if (UtilValidate.isNotEmpty(updateConfigRecords)) {
				String desc = updateConfigRecords.getString("description");
				if (UtilValidate.isNotEmpty(desc) && UtilValidate.isNotEmpty(description)) {
					if (description.equalsIgnoreCase(desc)) {
						updateConfigRecords.put("description", description);
						updateConfigRecords.put("parentEnumId", responseTypeId);
						updateConfigRecords.put("isMultiLingual", "Y");
						updateConfigRecords.store();
						results = ServiceUtil.returnSuccess(
								UtilProperties.getMessage(RESOURCE, "Opportunity Data Updated Successfully", locale));
					} else {

							updateConfigRecords.put("description", description);
							updateConfigRecords.put("parentEnumId", responseTypeId);
							updateConfigRecords.put("isMultiLingual", "Y");
							updateConfigRecords.store();
							results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE,
									"Opportunity Config Data Updated Successfully", locale));
						
					}
				}
			}
			results.put("enumId", enumId);
		} catch (GeneralException e) {
			Debug.log("==error in updation===" + e.getMessage());
		}

		return results;
	}
	
	public static Map<String, Object> globalMessageCreation(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> results = ServiceUtil.returnSuccess();
		String componentId = (String) context.get("componentId");
		String roleTypeId = (String) context.get("roleTypeId");
		String partyId = (String) context.get("partyId");
		String description = (String) context.get("description");
		String fromDateStr = (String) context.get("fromDate");
		String thruDateStr = (String) context.get("thruDate");
		Locale locale = (Locale) context.get("locale");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
        java.sql.Timestamp fromDate = null;
        java.sql.Timestamp thruDate = null;
        try {
        	fromDate = new java.sql.Timestamp(sdf.parse(fromDateStr).getTime());
        } catch (Exception e) {
            Debug.logError(e, "Cannot parse date string: " +  e.getMessage(), MODULE);
            fromDate = UtilDateTime.nowTimestamp();
        } 
        //fromDate = UtilDateTime.getDayStart(fromDate);
        if(UtilValidate.isNotEmpty(thruDateStr)){
        	try {
        		thruDate = new java.sql.Timestamp(sdf.parse(thruDateStr).getTime());
        	} catch (Exception e) {
        		Debug.logError(e, "Cannot parse date string: " +  e.getMessage(), MODULE);
        		thruDate = UtilDateTime.nowTimestamp();
        	} 
        }
        Timestamp previousDate = UtilDateTime.addDaysToTimestamp(fromDate, -1);
        
        Timestamp previousThruDate = previousDate;//UtilDateTime.getDayEnd(previousDate);
        
        List<EntityCondition> conditionlist = FastList.newInstance();
        
        Set<String> fieldsToSelect = new TreeSet<String>();
		fieldsToSelect.add("componentId");
		fieldsToSelect.add("partyId");
		fieldsToSelect.add("fromDate");
		fieldsToSelect.add("description");
		fieldsToSelect.add("roleTypeId");
		fieldsToSelect.add("thruDate");
		
		boolean enableRow=true;
		
        try {
        	conditionlist.add(EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId));
        	if(UtilValidate.isNotEmpty(partyId) && !partyId.equals("ALL")){
        		conditionlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        	}
        	conditionlist.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, fromDate));
        	if(UtilValidate.isNotEmpty(thruDate)){
        		conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, thruDate));
        	}
			conditionlist
					.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
            List < GenericValue > globalMessageConfigurationDetailsList = EntityQuery.use(delegator).select(fieldsToSelect).from("GlobalMessageConfig").where(condition).queryList();

            for (GenericValue globalMsgConfig: globalMessageConfigurationDetailsList) {
                Map<String, Object> data = new HashMap<String, Object>();
                Timestamp existedFromDateValue = (Timestamp)globalMsgConfig.get("fromDate");
                Timestamp existedthruDateValue = (Timestamp)globalMsgConfig.get("thruDate");

                if (existedFromDateValue.compareTo(fromDate) < 0){
                	globalMsgConfig.put("thruDate", previousThruDate);
                	globalMsgConfig.store();
                }
                if (existedFromDateValue.compareTo(fromDate) == 0){
                	globalMsgConfig.put("description", description);
                	globalMsgConfig.store();
                	enableRow=false;
                }
                
            }
        	
        }catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			results = ServiceUtil.returnError("Error : " + e.getMessage());
			return results;
		}
        if(enableRow) {
        	try {
        		GenericValue globalMsgConfig = delegator.makeValue("GlobalMessageConfig");
        		globalMsgConfig.put("roleTypeId", roleTypeId);
        		globalMsgConfig.put("description", description);
        		globalMsgConfig.put("componentId", componentId);
        		if(UtilValidate.isNotEmpty(partyId) && !partyId.equals("ALL")){
        			globalMsgConfig.put("partyId", partyId);
        		}
        		globalMsgConfig.put("fromDate", fromDate);
        		if(UtilValidate.isNotEmpty(thruDate)){
        			globalMsgConfig.put("thruDate", thruDate);
        		}
        		globalMsgConfig.put("isEnabled", "Y");
        		globalMsgConfig.create();
        	} catch (Exception e) {
        		e.printStackTrace();
        		Debug.logError(e.getMessage(), MODULE);
        		results = ServiceUtil.returnError("Error : " + e.getMessage());
        		return results;
        	}
        }
		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "Global message created successfully", locale));
		return results;
	}
	
	
	public static Map<String, Object> processGlobalMessageUpdate(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> results = ServiceUtil.returnSuccess();
		String componentId = (String) context.get("componentId");
		String partyId = (String) context.get("partyId");
		String roleTypeId = (String) context.get("roleTypeId");
		String description = (String) context.get("description");
		String fromDateStr = (String) context.get("fromDate");
		String thruDateStr = (String) context.get("thruDate");
		String isEnabled = (String) context.get("isEnabled");
		Locale locale = (Locale) context.get("locale");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
        java.sql.Timestamp fromDate = null;
        try {
        	fromDate = new java.sql.Timestamp(sdf.parse(fromDateStr).getTime());
        } catch (Exception e) {
            Debug.logError(e, "Cannot parse date string: " +  e.getMessage(), MODULE);
           // fromDate = UtilDateTime.nowTimestamp();
        } 
         //fromDate = UtilDateTime.getDayStart(fromDate);
        
        java.sql.Timestamp thruDate = null;
        if(UtilValidate.isNotEmpty(thruDateStr)){
        	try {
        		thruDate = new java.sql.Timestamp(sdf.parse(thruDateStr).getTime());
        	} catch (Exception e) {
        		Debug.logError(e, "Cannot parse date string: " +  e.getMessage(), MODULE);
        	} 
        	thruDate = UtilDateTime.getDayEnd(thruDate);
        }
        
        Timestamp previousDate = UtilDateTime.addDaysToTimestamp(fromDate, -1);
        
        Timestamp previousThruDate = UtilDateTime.getDayEnd(previousDate);
        
        List<EntityCondition> conditionlist = FastList.newInstance();
        
        Set<String> fieldsToSelect = new TreeSet<String>();
		fieldsToSelect.add("componentId");
		fieldsToSelect.add("partyId");
		fieldsToSelect.add("fromDate");
		fieldsToSelect.add("description");
		fieldsToSelect.add("roleTypeId");
		fieldsToSelect.add("thruDate");
		fieldsToSelect.add("isEnabled");
		
		boolean enableRow=true;
		
        try {
        	conditionlist.add(EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId));
        	//conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
			if(UtilValidate.isNotEmpty(partyId)&& !partyId.equals("ALL")){
				conditionlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			}
			if(UtilValidate.isNotEmpty(isEnabled)){
				conditionlist.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, isEnabled));
			}
        	conditionlist.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)));
        	conditionlist
					.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			//Debug.log("condition===="+condition);
			List < GenericValue > globalMessageConfigurationDetailsList = delegator.findList("GlobalMessageConfig",condition, fieldsToSelect,null,null, false);
					//EntityQuery.use(delegator).select(fieldsToSelect).from("GlobalMessageConfig").where(condition).queryList();
			//Debug.log("globalMessageConfigurationDetailsList===="+globalMessageConfigurationDetailsList);
            for (GenericValue globalMsgConfig: globalMessageConfigurationDetailsList) {
                Map<String, Object> data = new HashMap<String, Object>();
                Timestamp existedFromDateValue = (Timestamp)globalMsgConfig.get("fromDate");
                
                if (existedFromDateValue.compareTo(fromDate) == 0){
                	
                	if (UtilValidate.isNotEmpty(thruDate)) {
                    	globalMsgConfig.put("thruDate", thruDate);
                    }
                	if (UtilValidate.isNotEmpty(isEnabled)) {
                    	globalMsgConfig.put("isEnabled", isEnabled);
                    }
                	globalMsgConfig.put("description", description);
                	globalMsgConfig.store();
                	//Debug.log("globalMsgConfig===="+globalMsgConfig);
                }
                
            }
        	
        }catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			results = ServiceUtil.returnError("Error : " + e.getMessage());
			return results;
		}
        
        
		
		results = ServiceUtil
				.returnSuccess(UtilProperties.getMessage(RESOURCE, "Global message updated successfully", locale));
		return results;
	}

}