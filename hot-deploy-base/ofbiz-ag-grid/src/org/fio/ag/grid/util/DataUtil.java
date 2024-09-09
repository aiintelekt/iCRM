package org.fio.ag.grid.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;

public class DataUtil {
	public static Map<String, Object> hasGridAccess(Delegator delegator, HttpServletRequest request, String instanceId, String userId) {
		Map<String, Object> result = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		try {			
			if(UtilValidate.isNotEmpty(userId)) {
				/*
				List<GenericValue> groupIdList = EntityQuery.use(delegator).select("groupId").from("UserLoginSecurityGroup").where("userLoginId",userId).filterByDate().orderBy("-lastUpdatedTxStamp").queryList();
				if(UtilValidate.isNotEmpty(groupIdList)) {
					GenericValue gridAccess = null;
					for(GenericValue userLoginSecurityGroup:groupIdList) {
						String groupId = userLoginSecurityGroup.getString("groupId");
						gridAccess = getGridAccess(delegator, instanceId, groupId);
						if(UtilValidate.isNotEmpty(gridAccess))
							break;
					}
					
					if(UtilValidate.isNotEmpty(gridAccess)) {
						String optionsJson = gridAccess.getString("optionsJson");
						if(UtilValidate.isNotEmpty(optionsJson))
							result.putAll(org.fio.admin.portal.util.DataUtil.convertToMap(optionsJson));
					}
				} */
				
				boolean hasFullAccess = false;
				if (UtilValidate.isNotEmpty(userLogin)) {
					hasFullAccess = hasFullPermission(delegator, userLogin.getString("userLoginId"));
				}
				
				if (!hasFullAccess) {
					List<String> groupIds = UtilValidate.isNotEmpty(session.getAttribute("userLoginSecurityGroups")) ? (List<String>) session.getAttribute("userLoginSecurityGroups") : UtilValidate.isNotEmpty(request.getAttribute("userLoginSecurityGroups")) ? (List<String>) request.getAttribute("userLoginSecurityGroups") : new LinkedList<>();
					if(UtilValidate.isNotEmpty(groupIds)) {
						GenericValue gridAccess = null;
						for(String groupId:groupIds) {
							gridAccess = getGridAccess(delegator, instanceId, groupId);
							if(UtilValidate.isNotEmpty(gridAccess))
								break;
						}
						
						if(UtilValidate.isNotEmpty(gridAccess)) {
							String optionsJson = gridAccess.getString("optionsJson");
							if(UtilValidate.isNotEmpty(optionsJson))
								result.putAll(org.fio.admin.portal.util.DataUtil.convertToMap(optionsJson));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static GenericValue getGridAccess(Delegator delegator, String instanceId, String groupId) {
		GenericValue gridAccess = null;
		try {
			if(UtilValidate.isNotEmpty(instanceId) && UtilValidate.isNotEmpty(groupId)) {
				gridAccess = EntityQuery.use(delegator).from("AgGridAccess").where("instanceId", instanceId, "groupId",groupId).queryFirst();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return gridAccess;
		}
		return gridAccess;
	}
	
	public static Map<String, Object> getColumns(Delegator delegator, String instanceId,String userId){
		return getColumns(delegator, instanceId, userId, "USER");
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getColumns(Delegator delegator, String instanceId,String userId, String role){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			GenericValue gridPref = EntityQuery.use(delegator)
									.from("GridUserPreferences")
									.where("instanceId",instanceId,"role",role,"userId",userId)
									.queryFirst();
			if(UtilValidate.isNotEmpty(gridPref)) {
				String json = gridPref.getString("gridOptionsJsString");
				try {
					JSONObject jsonobj = new JSONObject(json);
					String columnDefStr = jsonobj.get("columnDefs").toString();
					
					Map<String, Object> gridJsonMap = org.fio.admin.portal.util.DataUtil.convertToMap(jsonobj.toString());
					//List<Map<String, Object>> columnDefList = org.fio.admin.portal.util.DataUtil.convertToListMap(columnDefStr);
					List<Map<String, Object>> columnDefList =  (List<Map<String, Object>>) gridJsonMap.get("columnDefs");
					if(UtilValidate.isNotEmpty(columnDefList)) {
						Map<String, Object> hideInfoMap = new LinkedHashMap<String, Object>();
						List<String> columns = new LinkedList<String>();
						List<Map<String, Object>> columnDefs = new LinkedList<Map<String, Object>>();
						/* Map<String, Object> columnDefs = new LinkedHashMap<String, Object>(); */
						for(Map<String, Object> columnDef : columnDefList) {
							String field = UtilValidate.isNotEmpty(columnDef.get("field")) ? (String) columnDef.get("field") : "";
							if(UtilValidate.isNotEmpty(field)) {
								boolean hide = UtilValidate.isNotEmpty(columnDef.get("hide")) ? columnDef.get("hide") instanceof String ? Boolean.parseBoolean((String) columnDef.get("hide")) : (boolean) columnDef.get("hide") :false;
								hideInfoMap.put(field, hide);
							}
							columns.add(field);
							columnDefs.add(columnDef);
						}
						result.put("columns", columns);
						result.put("columnDefs", columnDefs);
						result.put("hideInfoMap", hideInfoMap);
						result.put("gridJsonMap", gridJsonMap);
						
					}
					Debug.log("grid json --->"+result, DataUtil.class.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
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
	
}
