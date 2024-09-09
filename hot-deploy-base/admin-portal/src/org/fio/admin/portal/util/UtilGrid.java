/**
 * 
 */
package org.fio.admin.portal.util;

import java.util.Map;

import org.fio.homeapps.util.CacheUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif
 *
 */
public class UtilGrid {

	private static final String MODULE = UtilGrid.class.getName();
	
	public static void populateGridInstance(Delegator delegator, Map<String, Object> filter) {
		try {
			String instanceId = (String) filter.get("instanceId");
			String userId = (String) filter.get("userId");
			String role = (String) filter.get("role");
			
			if (UtilValidate.isNotEmpty(instanceId) && UtilValidate.isNotEmpty(userId)) {
				
				if (UtilValidate.isNotEmpty(role)) {
					GenericValue gridUserState = EntityQuery.use(delegator).from("GridUserPreferences")
							.where("instanceId", instanceId, "userId", userId, "role", role)
							.queryFirst();
					CacheUtil.getInstance().put("GRID_"+instanceId+"_"+"USER", gridUserState);
				} else {
					GenericValue gridAdminState = EntityQuery.use(delegator).from("GridUserPreferences")
							.where("instanceId", instanceId, "role", "ADMIN").queryFirst();
					CacheUtil.getInstance().put("GRID_"+instanceId+"_"+"ADMIN", gridAdminState);
					
					GenericValue gridUserState = EntityQuery.use(delegator).from("GridUserPreferences")
							.where("instanceId", instanceId, "userId", userId, "role", "USER")
							.queryFirst();
					CacheUtil.getInstance().put("GRID_"+instanceId+"_"+"USER", gridUserState);
				}
			}
		} catch (Exception e) {			
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);	
		}
	}
	
	public static GenericValue getGridAdminInstance(Delegator delegator, String instanceId, String userId) {
		try {
			if (UtilValidate.isNotEmpty(instanceId) && UtilValidate.isNotEmpty(userId)) {
				GenericValue gridAdminState = (GenericValue) CacheUtil.getInstance().get("GRID_"+instanceId+"_"+"ADMIN");
				if (UtilValidate.isEmpty(gridAdminState)) {
					gridAdminState = EntityQuery.use(delegator).from("GridUserPreferences")
							.where("instanceId", instanceId, "role", "ADMIN").queryFirst();
					CacheUtil.getInstance().put("GRID_"+instanceId+"_"+"ADMIN", gridAdminState);
				}
				return gridAdminState;
			}
		} catch (Exception e) {			
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);	
		}
		return null;
	}
	
	public static GenericValue getGridUserInstance(Delegator delegator, String instanceId, String userId) {
		try {
			if (UtilValidate.isNotEmpty(instanceId) && UtilValidate.isNotEmpty(userId)) {
				GenericValue gridUserState = (GenericValue) CacheUtil.getInstance().get("GRID_"+instanceId+"_"+"USER");
				if (UtilValidate.isNotEmpty(gridUserState)) {
					gridUserState = EntityQuery.use(delegator).from("GridUserPreferences")
							.where("instanceId", instanceId, "userId", userId, "role", "USER")
							.queryFirst();
					CacheUtil.getInstance().put("GRID_"+instanceId+"_"+"USER", gridUserState);
				}
				return gridUserState;
			}
		} catch (Exception e) {			
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);	
		}
		return null;
	}
	
}
