/**
 * 
 */
package org.groupfio.customfield.service.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.customfield.service.CustomfieldServiceConstants.GroupType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Sharif
 *
 */
public class EconomicMetricServices {

	private static final String MODULE = EconomicMetricServices.class.getName();
    
    public static Map createEconomicMetric(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	String groupName = (String) context.get("groupName");
    	String sequence = (String) context.get("sequence");
    	
    	String isActive = (String) context.get("isActive");
    	
    	String groupingCode = (String) context.get("groupingCode");
    	String serviceName = (String) context.get("serviceName");
    	String serviceTypeId = (String) context.get("serviceTypeId");
    	String serviceConfigId = (String) context.get("serviceConfigId");
    	String historicalCapture = (String) context.get("historicalCapture");
    	String valueCapture = (String) context.get("valueCapture");
    	String isCampaignUse = (String) context.get("isCampaignUse");
    	String classType = (String) context.get("classType");
    	String type = (String) context.get("type");
    	String isUseDynamicEntity = (String) context.get("isUseDynamicEntity");
    	String roleTypeId = (String) context.get("roleTypeId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupId", groupId);
    	
    	try {
        	
    		GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    		
    		if (UtilValidate.isNotEmpty(group)) {
    			result.putAll(ServiceUtil.returnError("Economic Code already exists!"));
    			return result;
    		}
    		
    		group = delegator.makeValue("CustomFieldGroup");
    		
    		group.put("groupType", GroupType.ECONOMIC_METRIC);
    		
    		group.put("groupId", groupId);
    		group.put("groupName", groupName);
    		
    		group.put("groupingCode", groupingCode);
    		group.put("serviceName", serviceName);
    		group.put("serviceTypeId", serviceTypeId);
    		group.put("serviceConfigId", serviceConfigId);
    		group.put("historicalCapture", UtilValidate.isEmpty(historicalCapture) ? "N" : historicalCapture);
    		group.put("valueCapture", valueCapture);
    		group.put("isCampaignUse", isCampaignUse);
    		group.put("classType", classType);
    		group.put("type", type);
    		group.put("isActive", UtilValidate.isEmpty(isActive) ? "Y" : isActive);
    		group.put("isUseDynamicEntity", UtilValidate.isEmpty(isUseDynamicEntity) ? "N" : isUseDynamicEntity );
    		
    		group.put("sequence", UtilValidate.isNotEmpty(sequence) ? Long.parseLong(sequence) : new Long(1));
    		
    		group.create();
    		
    		Map<String, Object> roleContext = new HashMap<String, Object>();
			if(UtilValidate.isNotEmpty(roleTypeId)){
				roleContext.put("groupId", groupId);
				roleContext.put("roleTypeId", roleTypeId);
				roleContext.put("sequenceNumber", "1");
				SegmentServices.createRoleConfig(delegator, roleContext);
			}
    		// Data Source
    		
    		String dataSourceTable = "PartyMetricIndicator";
    		String dataSourceTrackTable = "";
    		
    		GenericValue dataSource = delegator.makeValue("CustomFieldDataSource");
    		
    		String customFieldDataSourceId = delegator.getNextSeqId("CustomFieldDataSource");
    		
    		dataSource.put("customFieldDataSourceId", customFieldDataSourceId);
    		dataSource.put("dataSourceTable", dataSourceTable);
    		dataSource.put("dataSourceTrackTable", (UtilValidate.isNotEmpty(historicalCapture) && historicalCapture.equals("Y")) ? dataSourceTrackTable : null);
    		dataSource.put("groupId", groupId);
    		
    		dataSource.create();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created Economic Code.."));
    	
    	return result;
    	
    }
    
    public static Map updateEconomicMetric(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	
    	String groupName = (String) context.get("groupName");
    	String sequence = (String) context.get("sequence");
    	
    	String groupingCode = (String) context.get("groupingCode");
    	String serviceName = (String) context.get("serviceName");
    	String serviceTypeId = (String) context.get("serviceTypeId");
    	String serviceConfigId = (String) context.get("serviceConfigId");
    	String historicalCapture = (String) context.get("historicalCapture");
    	String valueCapture = (String) context.get("valueCapture");
    	String isCampaignUse = (String) context.get("isCampaignUse");
    	String classType = (String) context.get("classType");
    	String type = (String) context.get("type");
    	//String isActive = (String) context.get("isActive");
    	String isUseDynamicEntity = (String) context.get("isUseDynamicEntity");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupId", groupId);
    	
    	try {
        	
    		GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    		
    		if (UtilValidate.isEmpty(group)) {
    			result.putAll(ServiceUtil.returnError("Economic Code not exists!"));
    			return result;
    		}
    		
    		group.put("groupName", groupName);
    		
    		group.put("groupingCode", groupingCode);
    		group.put("serviceName", serviceName);
    		group.put("serviceTypeId", serviceTypeId);
    		group.put("serviceConfigId", serviceConfigId);
    		group.put("historicalCapture", UtilValidate.isEmpty(historicalCapture) ? "N" : historicalCapture);
    		group.put("valueCapture", valueCapture);
    		group.put("isCampaignUse", isCampaignUse);
    		group.put("classType", classType);
    		group.put("type", type);
    		//group.put("isActive", isActive);
    		group.put("isUseDynamicEntity", UtilValidate.isEmpty(isUseDynamicEntity) ? "N" : isUseDynamicEntity);
    		
    		group.put("sequence", UtilValidate.isNotEmpty(sequence) ? Long.parseLong(sequence) : new Long(1));
    		
    		group.store();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated Economic Code.."));
    	
    	return result;
    	
    }
    
    public static Map deleteEconomicMetric(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    		
    		if (UtilValidate.isEmpty(group)) {
    			result.putAll(ServiceUtil.returnError("Economic Code not exists!"));
    			return result;
    		}
    		
    		List<GenericValue> customFields = delegator.findByAnd("CustomField", UtilMisc.toMap("groupId", groupId), null, false);
    		for (GenericValue customField : customFields) {
    			Map<String, Object> customFieldRemoveContext = new HashMap<String, Object>();
        		
    			customFieldRemoveContext.put("customFieldId", customField.getString("customFieldId"));
    			customFieldRemoveContext.put("userLogin", userLogin);
        		
        		Map<String, Object> customFieldRemoveResult = dispatcher.runSync("customfield.deleteCustomField", customFieldRemoveContext);
    			
    		}
    		
    		group.remove();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted Economic Code.."));
    	
    	return result;
    	
    }
    
}
