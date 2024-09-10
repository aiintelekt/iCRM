/**
 * 
 */
package org.fio.homeapps.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class StatusUtil {

	private static String MODULE = StatusUtil.class.getName();
	
	public static String getStatusId(Delegator delegator, String value) {
		return getStatusId(delegator, value, null);
	}
    
    public static boolean isValidStatusId(Delegator delegator, String value) {
		return UtilValidate.isNotEmpty(getStatusId(delegator, value, null)); 
	}
	
	public static boolean isValidStatusId(Delegator delegator, String value, String statusTypeId) {
		return UtilValidate.isNotEmpty(getStatusId(delegator, value, statusTypeId)); 
	}
	
	public static String getStatusId(Delegator delegator, String value, String statusTypeId) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("statusId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("statusCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);    
				
				if (UtilValidate.isNotEmpty(statusTypeId)) {
					condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, statusTypeId),
	               			condition
	               			);       
				}
				
				GenericValue statusEntity = EntityQuery.use(delegator).select("statusId").from("StatusItem").where(condition).queryFirst();
				if (UtilValidate.isNotEmpty(statusEntity)) {
					return statusEntity.getString("statusId");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getStatusDescription(Delegator delegator, String value) {
		return getStatusDescription(delegator, value, null);
	}
	
	public static String getStatusDescription(Delegator delegator, String value, String statusTypeId) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("statusId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("statusCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);    
				
				if (UtilValidate.isNotEmpty(statusTypeId)) {
					condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, statusTypeId),
	               			condition
	               			);       
				}
				
				GenericValue statusEntity = EntityQuery.use(delegator).select("description").from("StatusItem").where(condition).cache().queryFirst();
				if (UtilValidate.isNotEmpty(statusEntity)) {
					return statusEntity.getString("description");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static boolean isValidMsdStatusId(Delegator delegator, String workEffortTypeId, String attributeName, String attributeValue) {
		return UtilValidate.isNotEmpty(getMsdStatusId(delegator, workEffortTypeId, attributeName, attributeValue)); 
	}
	
	public static String getMsdStatusId(Delegator delegator, String workEffortTypeId, String attributeName, String attributeValue) {
		try {
			if (UtilValidate.isNotEmpty(workEffortTypeId) && UtilValidate.isNotEmpty(attributeName) && UtilValidate.isNotEmpty(attributeValue)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, workEffortTypeId),
						EntityCondition.makeCondition("attributeName", EntityOperator.EQUALS, attributeName),
						EntityCondition.makeCondition("attributeValue", EntityOperator.EQUALS, attributeValue)
               			);    
				
				GenericValue statusEntity = EntityQuery.use(delegator).select("ofbizCode").from("ActivityTypeStatusMap").where(condition).queryFirst();
				if (UtilValidate.isNotEmpty(statusEntity)) {
					
					String statusTypeId = null;
					if (UtilValidate.isNotEmpty(attributeName) && attributeName.equals("statuscode")) {
						statusTypeId = "IA_STATUS_ID";
					} else if (UtilValidate.isNotEmpty(attributeName) && attributeName.equals("statecode")) {
						statusTypeId = "IA_SUB_STATUS_ID";
					}
					
					String ofbizCode = statusEntity.getString("ofbizCode");
					String statusId = getStatusId(delegator, ofbizCode, statusTypeId);
					
					return statusId;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static Map<String, Object> getStatusList(Delegator delegator, List<GenericValue> dataList, String fieldId, String statusTypeId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> statusIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				results = getStatusList(delegator, statusIds, statusTypeId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	public static Map<String, Object> getStatusList(Delegator delegator, List<String> statusIds, String statusTypeId) {
		Map<String, Object> results = new HashMap<>();
		results = statusIds.stream().collect(Collectors.toMap(x -> (String) x,
				x -> {
					String val = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, x, statusTypeId);
					if (UtilValidate.isEmpty(val)) {
						val = "";
					}
					return UtilValidate.isNotEmpty(x) ? val : "";
				},
				(attr1, attr2) -> {
					return attr2;
				}));
		return results;
	}
	
}
