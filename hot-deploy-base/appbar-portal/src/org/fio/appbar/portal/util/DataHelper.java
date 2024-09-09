package org.fio.appbar.portal.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;

public class DataHelper {
	
	public static Map<String, Object> getLoginUserDetails(Delegator delegator, String userLoginId){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			GenericValue userDetails = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId",userLoginId).queryFirst();
			if(UtilValidate.isNotEmpty(userDetails)) {
				result.put("userLoginId", userLoginId);
				result.put("partyId", userDetails.getString("partyId"));
				result.put("emplTeamId", userDetails.getString("emplTeamId"));
				result.put("businessUnit", userDetails.getString("businessUnit"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static boolean validatePermission(Delegator delegator, String userLoginId, String permissionId) {
		boolean hasPermission = false;
		try {
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId),
					EntityCondition.makeCondition("permissionId",EntityOperator.EQUALS,permissionId));
			GenericValue userLoginSecurityGroupPermission = EntityQuery.use(delegator).from("UserLoginSecurityGroupPermission").where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(userLoginSecurityGroupPermission)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return hasPermission;
		
	}
	public static boolean hasFullPermission(Delegator delegator, String userLoginId) {
		boolean hasFullPermission = false;
		try {
			String fullAccessGroupId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "full.access.security.group", delegator);
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId),
					EntityCondition.makeCondition("groupId",EntityOperator.EQUALS,fullAccessGroupId));
			GenericValue userLoginSecurityGroupPermission = EntityQuery.use(delegator).from("UserLoginSecurityGroup").where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(userLoginSecurityGroupPermission)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return hasFullPermission;
		
	}
	public static boolean validateSecurityPermission(Delegator delegator, List<String> groupIds, String permissionId) {
		boolean hasPermission = false;
		try {
			if(UtilValidate.isNotEmpty(groupIds) && UtilValidate.isNotEmpty(permissionId)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("groupId",EntityOperator.IN,groupIds),
						EntityCondition.makeCondition("permissionId",EntityOperator.EQUALS,permissionId));
				GenericValue userLoginSecurityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission").where(condition).queryFirst();
				if(UtilValidate.isNotEmpty(userLoginSecurityGroupPermission)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return hasPermission;
		
	}
}
