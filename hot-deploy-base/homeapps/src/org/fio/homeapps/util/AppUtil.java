package org.fio.homeapps.util;

import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class AppUtil {
	
	private static final String MODULE = AppUtil.class.getName();

	public static void expireAllActiveAppStatus (Delegator delegator, String clientRegistryId) {
		
		try {
			EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("clientRegistryId", EntityOperator.EQUALS, clientRegistryId),
					
					EntityCondition.makeConditionDate("fromDate", "thruDate")
					);
			
			List<GenericValue> activeAppStatusList = delegator.findList("ClientApplicationStatus", conditions, null, null, null, false);
			for (GenericValue appStatus : activeAppStatusList) {
				
				if (UtilValidate.isEmpty(appStatus.get("fromDate"))) {
					appStatus.put("fromDate", UtilDateTime.nowTimestamp());
				}
				
				appStatus.put("thruDate", UtilDateTime.nowTimestamp());
				
				appStatus.store();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
	}
	
	public static GenericValue getActiveAppStatus (Delegator delegator, String clientRegistryId) {
		
		try {
			EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("clientRegistryId", EntityOperator.EQUALS, clientRegistryId),
					
					EntityCondition.makeConditionDate("fromDate", "thruDate")
					);
			
			GenericValue activeAppStatus = EntityUtil.getFirst( delegator.findList("ClientApplicationStatus", conditions, null, null, null, false) );
			return activeAppStatus;
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static GenericValue getClientApplicationRegistry (Delegator delegator, String clientRegistryId, String externalReferenceId) {
		
		try {
			
			GenericValue registry = null;
			
			if (UtilValidate.isNotEmpty(clientRegistryId)) {
				registry = EntityUtil.getFirst( delegator.findByAnd("ClientApplicationRegistry", UtilMisc.toMap("clientRegistryId", clientRegistryId), null, false) );
			} else if (UtilValidate.isNotEmpty(externalReferenceId)) {
				registry = EntityUtil.getFirst( delegator.findByAnd("ClientApplicationRegistry", UtilMisc.toMap("externalReferenceId", externalReferenceId), null, false) );
			} 
			
			return registry;
			
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static boolean isServiceRestricted (Delegator delegator, String serviceName) {
		try {
			
			if (UtilValidate.isEmpty(serviceName)) {
				return true;
			}
			
			GenericValue serviceConfig = EntityUtil.getFirst( delegator.findByAnd("ServiceConfig",UtilMisc.toMap("serviceName", serviceName, "componentName", "ewallet-app"), null, false) );
			
			if (UtilValidate.isEmpty(serviceConfig)) {
				return false;
			}
			
			return !serviceConfig.getBoolean("isPublic");
			
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return true;
	}
	
}
