/**
 * 
 */
package org.groupfio.customfield.service.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.customfield.service.CustomfieldServiceConstants.GroupType;
import org.groupfio.customfield.service.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Sharif
 *
 */
public class SegmentValueServices {

	private static final String MODULE = SegmentValueServices.class.getName();
    
    public static Map createSegmentValue(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	
    	String customFieldId = (String) context.get("customFieldId");
    	String customFieldName = (String) context.get("customFieldName");
    	String sequenceNumber = (String) context.get("sequenceNumber");
    	String customFieldType = (String) context.get("customFieldType");
    	String isEnabled = (String) context.get("isEnabled");
    	String isDefault = (String) context.get("isDefault");
    	
    	String valueCapture = (String) context.get("valueCapture");
    	String valueMin = (String) context.get("valueMin");
    	String valueMax = (String) context.get("valueMax");
    	String valueData = (String) context.get("valueData");
    	String productPromoCodeGroupId = (String) context.get("productPromoCodeGroupId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupId", groupId);
    	
    	try {
        	
    		Map<String, Object> conds = UtilMisc.toMap("customFieldName", customFieldName);
    		if (UtilValidate.isNotEmpty(groupId)) {
    			conds.put("groupId", groupId);
    		}
    		
    		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap(conds), null, false) );
    		
    		if (UtilValidate.isNotEmpty(customField)) {
    			result.putAll(ServiceUtil.returnError("Segment Value already exists!"));
    			return result;
    		}
    		
    		// initiate default
    		if (UtilValidate.isNotEmpty(isDefault) && isDefault.equals("Y")) {
    			List<GenericValue> tobeStore = new ArrayList<>();
    			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    			conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
    			conditions.add(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"));
            	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> fieldList = EntityQuery.use(delegator).from("CustomField").where(mainConditon).queryList();
				if (UtilValidate.isNotEmpty(fieldList)) {
					for (GenericValue field : fieldList) {
						field.put("isDefault", "N");
						tobeStore.add(field);
					}
					delegator.storeAll(tobeStore);
				}
    		}
    		
    		customField = delegator.makeValue("CustomField");
    		
    		//String customFieldId = delegator.getNextSeqId("CustomField");
    		
    		customField.put("customFieldId", customFieldId);
    		
    		customField.put("groupType", GroupType.SEGMENTATION);
    		
    		customField.put("isEnabled", UtilValidate.isNotEmpty(isEnabled) ? isEnabled : "Y");
    		customField.put("isDefault", UtilValidate.isNotEmpty(isDefault) ? isDefault : "N");
    		customField.put("customFieldName", customFieldName);
    		customField.put("customFieldType", customFieldType);
    		customField.put("productPromoCodeGroupId", productPromoCodeGroupId);
    		
    		customField.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1));
    		
    		if (UtilValidate.isNotEmpty(groupId)) {
    			GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    			if (UtilValidate.isNotEmpty(group)) {
    				customField.put("groupName", group.getString("groupName"));
    				customField.put("groupId", groupId);
    				customField.put("groupType", group.getString("groupType"));
    				valueCapture = group.getString("valueCapture");
    			}
    		}
    		
    		customField.create();
    		
    		// store value configuration [start]
    		if ( UtilValidate.isNotEmpty(groupId) && (UtilValidate.isNotEmpty(valueCapture) && !valueCapture.equals("MULTIPLE")) ) {
    			
	    		String valueSeqNum = "1";
	    		Map<String, Object> valueConfigContext = new HashMap<String, Object>();
	    		
	    		valueConfigContext.put("groupId", groupId);
	    		valueConfigContext.put("customFieldId", customFieldId);
	    		valueConfigContext.put("valueCapture", valueCapture);
	    		valueConfigContext.put("valueSeqNum", valueSeqNum);
	    		valueConfigContext.put("valueMin", valueMin);
	    		valueConfigContext.put("valueMax", valueMax);
	    		valueConfigContext.put("valueData", valueData);
	    		valueConfigContext.put("userLogin", userLogin);
	    		
	    		Map<String, Object> valueConfigResult = dispatcher.runSync("segment.createValueConfig", valueConfigContext);
	    		
	    		if (ServiceUtil.isSuccess(valueConfigResult)) {
	    			Debug.log("Successfully created segment value configuration : " + valueConfigContext);
	    		}
	    		
    		}
    		// store value configuration [end]
    		
    		result.put("customFieldId", customFieldId);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created Segment Value .."));
    	
    	return result;
    	
    }
    
    public static Map updateSegmentValue(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String customFieldId = (String) context.get("customFieldId");
    	
    	String groupId = (String) context.get("groupId");
    	
    	String customFieldName = (String) context.get("customFieldName");
    	String sequenceNumber = (String) context.get("sequenceNumber");
    	String customFieldType = (String) context.get("customFieldType");
    	String isEnabled = (String) context.get("isEnabled");
    	String isDefault = (String) context.get("isDefault");
    	
    	String valueCapture = (String) context.get("valueCapture");
    	String valueMin = (String) context.get("valueMin");
    	String valueMax = (String) context.get("valueMax");
    	String valueData = (String) context.get("valueData");
    	String productPromoCodeGroupId = (String) context.get("productPromoCodeGroupId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupId", groupId);
		result.put("customFieldId", customFieldId);
    	
    	try {
        	
    		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
    		
    		if (UtilValidate.isEmpty(customField)) {
    			result.putAll(ServiceUtil.returnError("Segment Value not exists!"));
    			return result;
    		}
    		
    		EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId),
					EntityCondition.makeCondition("customFieldName", EntityOperator.EQUALS, customFieldName),
					EntityCondition.makeCondition("customFieldId", EntityOperator.NOT_EQUAL, customFieldId)
					);
			
			List<GenericValue> customFields = delegator.findList("CustomField", condition, null, null, null, false);
			if (UtilValidate.isNotEmpty(customFields)) {
				result.putAll(ServiceUtil.returnError("Segment Value already exists!"));
    			return result;
			}
			
			// initiate default
    		if (UtilValidate.isNotEmpty(isDefault) && isDefault.equals("Y")) {
    			List<GenericValue> tobeStore = new ArrayList<>();
    			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    			conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
    			conditions.add(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"));
            	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> fieldList = EntityQuery.use(delegator).from("CustomField").where(mainConditon).queryList();
				if (UtilValidate.isNotEmpty(fieldList)) {
					for (GenericValue field : fieldList) {
						field.put("isDefault", "N");
						tobeStore.add(field);
					}
					delegator.storeAll(tobeStore);
				}
    		}
			
    		customField.put("isEnabled", UtilValidate.isNotEmpty(isEnabled) ? isEnabled : "Y");
    		customField.put("isDefault", UtilValidate.isNotEmpty(isDefault) ? isDefault : "N");
    		customField.put("customFieldName", customFieldName);
    		customField.put("productPromoCodeGroupId", productPromoCodeGroupId);
    		
    		customField.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1));
    		customField.put("customFieldType", customFieldType);
    		if (UtilValidate.isNotEmpty(groupId)) {
    			GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    			if (UtilValidate.isNotEmpty(group)) {
    				customField.put("groupName", group.getString("groupName"));
    				customField.put("groupId", groupId);
    				customField.put("groupType", group.getString("groupType"));
    				valueCapture = group.getString("valueCapture");
    			}
    		}
    		
    		customField.store();
    		
    		// store value configuration [start]
    		
    		if (UtilValidate.isNotEmpty(groupId)) {
    		
	    		String valueSeqNum = "1";
	    		Map<String, Object> valueConfigContext = new HashMap<String, Object>();
	    		
	    		valueConfigContext.put("groupId", groupId);
	    		valueConfigContext.put("customFieldId", customFieldId);
	    		valueConfigContext.put("valueCapture", valueCapture);
	    		valueConfigContext.put("valueSeqNum", valueSeqNum);
	    		valueConfigContext.put("valueMin", valueMin);
	    		valueConfigContext.put("valueMax", valueMax);
	    		valueConfigContext.put("valueData", valueData);
	    		valueConfigContext.put("userLogin", userLogin);
	    		
	    		GenericValue valueConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldValueConfig", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "valueCapture", valueCapture, "valueSeqNum", Long.parseLong(valueSeqNum)), null, false) );
	    		String valueConfigService = "segment.updateValueConfig";
	    		if (UtilValidate.isEmpty(valueConfig)) {
	    			valueConfigService = "segment.createValueConfig";
	    		}
	    		
	    		Map<String, Object> valueConfigResult = dispatcher.runSync(valueConfigService, valueConfigContext);
	    		
	    		if (ServiceUtil.isSuccess(valueConfigResult)) {
	    			Debug.log("Successfully "+valueConfigService+" segment value configuration : " + valueConfigContext);
	    		}
    		
    		}
    		
    		// store value configuration [end]
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated Segment Value .."));
    	
    	return result;
    	
    }
    
    public static Map deleteSegmentValue(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String customFieldId = (String) context.get("customFieldId");
    	
    	String valueCapture = (String) context.get("valueCapture");
    	String valueSeqNum = (String) context.get("valueSeqNum");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
    		
    		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField",UtilMisc.toMap("customFieldId", customFieldId), null, false) );
    		if (UtilValidate.isEmpty(customField)) {
    			result.putAll(ServiceUtil.returnError("Segment Value not exists!"));
    			return result;
    		}
    		String groupId = customField.getString("groupId");
    		
    		if (UtilValidate.isNotEmpty(groupId)) {
    			GenericValue customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
        		valueCapture = customFieldGroup.getString("valueCapture");
        		valueSeqNum = "1";
        		
        		//delegator.removeAll( delegator.findByAnd("CustomFieldValue",UtilMisc.toMap("customFieldId", customFieldId), null, false) );
        		
    			Map<String, Object> valueConfigContext = new HashMap<String, Object>();
        		valueConfigContext.put("groupId", groupId);
        		valueConfigContext.put("customFieldId", customFieldId);
        		
        		valueConfigContext.put("valueCapture", valueCapture);
        		valueConfigContext.put("valueSeqNum", valueSeqNum);
        		
        		valueConfigContext.put("userLogin", userLogin);
        		
    			Map<String, Object> valueConfigResult = dispatcher.runSync("segment.deleteValueConfig", valueConfigContext);
        		if (ServiceUtil.isSuccess(valueConfigResult)) {
        			Debug.log("Successfully deleted segment value configuration : " + valueConfigContext);
        		}
    		}
    		
    		delegator.removeAll( delegator.findByAnd("CustomFieldRoleConfig", UtilMisc.toMap("groupId", groupId), null, false) );
    		
    		String segmentationValueAssociatedEntityName = DataUtil.getSegmentationValueAssociatedEntityName(delegator, groupId);
    		delegator.removeAll( delegator.findByAnd(segmentationValueAssociatedEntityName, UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId), null, false) );
    		
    		customField.remove();
    		
    		result.put("groupId", groupId);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted Segment Value.."));
    	
    	return result;
    	
    }
    
}
