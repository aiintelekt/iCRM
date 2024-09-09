package org.fio.appbar.portal.util;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fio.appbar.portal.constant.AppBarConstants.AppBarDataLevel;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

public class AppBarConfigureUtil {
	private static String MODULE = AppBarConfigureUtil.class.getName();
	private static String RESOURCE = "AppbarPortalUiLabels";
	
	public static Map <String, Object> getAppBarConfiguration (Delegator delegator, GenericValue userLogin, String appBarId, String appBarTypeId) {
		return getAppBarConfiguration(delegator, userLogin, appBarId, appBarTypeId, null,true);
	}
	public static Map <String, Object> getAppBarConfiguration (Delegator delegator, GenericValue userLogin, String appBarId, String appBarTypeId, String geoId) {
		return getAppBarConfiguration(delegator, userLogin, appBarId, appBarTypeId, geoId, true);
	}
	public static Map <String, Object> getAppBarConfiguration (Delegator delegator, GenericValue userLogin, String appBarId, String appBarTypeId, boolean hasUserPreference) {
		return getAppBarConfiguration(delegator, userLogin, appBarId, appBarTypeId, null, hasUserPreference);
	}
	private static Map <String, Object> getAppBarConfiguration (Delegator delegator, GenericValue userLogin, String appBarId, String appBarTypeId, String geoId, boolean hasUserPreference ) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(UtilValidate.isEmpty(userLogin)) {
				Debug.logError("User Login is empty", MODULE);
				return null;
			}
			if(UtilValidate.isNotEmpty(appBarId) && UtilValidate.isNotEmpty(appBarTypeId)) {
				List<EntityCondition> commonConditions = new ArrayList<EntityCondition>();
				
				commonConditions.add(EntityCondition.makeCondition("appBarId",EntityOperator.EQUALS,appBarId));
				commonConditions.add(EntityCondition.makeCondition("appBarTypeId",EntityOperator.EQUALS,appBarTypeId));
				
				//AppBar
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("appBarStatus",EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("appBarStatus",EntityOperator.EQUALS, "ENABLED")
						);
				/*Set<String> appBarFields = new TreeSet<String>();
				appBarFields.add("appBarNameUilabel"); appBarFields.add("appBarService");
				appBarFields.add("appBarDirection"); appBarFields.add("appBarHeight");
				appBarFields.add("appBarWidth"); appBarFields.add("appBarMaxElements");
				appBarFields.add("appBarAccessLevel"); appBarFields.add("appBarDataLevel");
				appBarFields.add("appBarIsServiceBased"); appBarFields.add("appBarRefreshType");
				appBarFields.add("appBarRefreshMinRate"); appBarFields.add("defaultMessage");
				*/
				List<EntityCondition> appBarCond = new ArrayList<EntityCondition>();
				appBarCond.add(condition);
				appBarCond.addAll(commonConditions);
				if(UtilValidate.isNotEmpty(geoId)) {
					appBarCond.add(EntityCondition.makeCondition("geoId",EntityOperator.EQUALS,geoId));
				}
				GenericValue appBar = EntityQuery.use(delegator).from("AppBar").where(EntityCondition.makeCondition(appBarCond,EntityOperator.AND)).queryFirst();
				
				String userLoginId = userLogin.getString("userLoginId");
				if(UtilValidate.isNotEmpty(appBar)) {
					Map<String, Object> userDetails = DataHelper.getLoginUserDetails(delegator, userLoginId);
					int barMaxElements = appBar.getInteger("appBarMaxElements");
					String appBarAccessLevel = appBar.getString("appBarAccessLevel");
					String appBarDataLevel = appBar.getString("appBarDataLevel");
					List<EntityCondition> appBarDataCond = new ArrayList<EntityCondition>();
					appBarDataCond.addAll(commonConditions);
					if(AppBarDataLevel.USER_LEVEL.equalsIgnoreCase(appBarDataLevel)) {
					 	appBarDataCond.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("appBarOwner",EntityOperator.EQUALS,userLoginId),
								EntityCondition.makeCondition("emplTeamId",EntityOperator.EQUALS,null),
								EntityCondition.makeCondition("businessUnitId",EntityOperator.EQUALS,null)
								));			
					} else if(AppBarDataLevel.TEAM_LEVEL.equalsIgnoreCase(appBarDataLevel)) {
						String emplTeamId = (String) userDetails.get("emplTeamId");
						appBarDataCond.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("appBarOwner",EntityOperator.EQUALS,null),
								EntityCondition.makeCondition("emplTeamId",EntityOperator.EQUALS,emplTeamId),
								EntityCondition.makeCondition("businessUnitId",EntityOperator.EQUALS,null)
								));			
					} else if(AppBarDataLevel.BU_LEVEL.equalsIgnoreCase(appBarDataLevel)) {
						String businessUnit = (String) userDetails.get("businessUnit");
						appBarDataCond.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("appBarOwner",EntityOperator.EQUALS,null),
								EntityCondition.makeCondition("emplTeamId",EntityOperator.EQUALS,null),
								EntityCondition.makeCondition("businessUnitId",EntityOperator.EQUALS,businessUnit)
								));			
					} else {
						appBarDataCond.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("appBarOwner",EntityOperator.EQUALS,null),
								EntityCondition.makeCondition("emplTeamId",EntityOperator.EQUALS,null),
								EntityCondition.makeCondition("businessUnitId",EntityOperator.EQUALS,null)
								));			
					}
					
					GenericValue appBarData = EntityQuery.use(delegator).select("appBarDataId","jsonResponse").from("AppBarData").where(EntityCondition.makeCondition(appBarDataCond,EntityOperator.AND)).filterByDate().queryFirst();
					if(UtilValidate.isNotEmpty(appBarData)) {
						String jsonData = appBarData.getString("jsonResponse");

						Map<String, Object> appBarJsonEleMap = DataUtil.convertToMap(jsonData);
						
						//get appbarelement
						List<EntityCondition> appBarElementCond = new ArrayList<EntityCondition>();
						Set<String> elements = appBarJsonEleMap.keySet();
						if(UtilValidate.isNotEmpty(elements))
							appBarElementCond.add(EntityCondition.makeCondition("appBarElementId",EntityOperator.IN,elements));
						
						appBarElementCond.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("appBarElementActive",EntityOperator.EQUALS,null),
								EntityCondition.makeCondition("appBarElementActive",EntityOperator.EQUALS,"Y")
								));
						
						appBarElementCond.addAll(commonConditions);
						
						
						Map<String, Object> elementConfigMap = new LinkedHashMap<String, Object>();
						Map<String, Object> elementDataMap = new LinkedHashMap<String, Object>();
						Set<String> elementFields = new TreeSet<String>();
						elementFields.add("appBarId");elementFields.add("appBarTypeId");
						elementFields.add("appBarElementId");elementFields.add("appBarElementSeqNum");
						elementFields.add("appBarElementName");elementFields.add("appBarElementUilabel");
						elementFields.add("appBarElementServiceName");elementFields.add("appBarElementNolabel");
						elementFields.add("appBarElementIconUrl");elementFields.add("appBarElementFavIcon");
						elementFields.add("appBarElementColor");elementFields.add("appBarElementMaxLen");
						elementFields.add("appBarElementPosition");elementFields.add("appBarElementType");
						elementFields.add("appBarElementTargetUrl");elementFields.add("appBarElementCustomClass");
						elementFields.add("securityPermissionId");elementFields.add("isDisplayElementData");
						List<GenericValue> appBarElementList = EntityQuery.use(delegator).select(elementFields).from("AppBarElements").where(EntityCondition.makeCondition(appBarElementCond,EntityOperator.AND)).orderBy("appBarElementSeqNum ASC").distinct().maxRows(barMaxElements).queryList();
						if(UtilValidate.isNotEmpty(appBarElementList)) {
							if(hasUserPreference) {
								List<EntityCondition> userPrefAppBarCond = new ArrayList<EntityCondition>();
								userPrefAppBarCond.addAll(commonConditions);
								userPrefAppBarCond.add(EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId));
								List<GenericValue> appBarUserPreferenceList = EntityQuery.use(delegator).from("AppBarUserPreference").where(EntityCondition.makeCondition(userPrefAppBarCond,EntityOperator.AND)).orderBy("appBarElementSeqNum ASC").distinct().maxRows(barMaxElements).queryList();
								if(UtilValidate.isNotEmpty(appBarUserPreferenceList)) {
									appBarElementList  = getUserPreferenceAppBar(appBarElementList, appBarUserPreferenceList);
								}
							}
							boolean hasFullAccess = DataHelper.hasFullPermission(delegator, userLoginId);
							Debug.logInfo("Appbar element exists" +appBarId+" - "+appBarTypeId, MODULE);
							for(GenericValue appBarElement : appBarElementList) {
								Map<String, Object> elementMap = new HashMap<String, Object>();
								String appBarElementId = appBarElement.getString("appBarElementId");
								String securityPermissionId = appBarElement.getString("securityPermissionId");
								boolean hasPermission = true;
								if(!hasFullAccess)
									 hasPermission = DataHelper.validatePermission(delegator, userLoginId, securityPermissionId);
								if(hasPermission) {
									elementMap.putAll(appBarElement);
									elementConfigMap.put(appBarElementId, elementMap);
									
									elementDataMap.put(appBarElementId, appBarJsonEleMap.get(appBarElementId));
								}
							}
							if(UtilValidate.isNotEmpty(elementConfigMap) && UtilValidate.isNotEmpty(elementDataMap)) {
								result.put("configuration", elementConfigMap);
								result.put("dataList", elementDataMap);
								
								result.put("appBarDirection", appBar.getString("appBarDirection"));
								result.put("appBarHeight", appBar.getString("appBarHeight"));
								result.put("appBarWidth", appBar.getString("appBarWidth"));
								result.put("appBarIsServiceBased", appBar.getString("appBarIsServiceBased"));
								result.put("appBarRefreshType", appBar.getString("appBarRefreshType"));
								result.put("geoId", appBar.getString("geoId"));
							}
						} else {
							Debug.logError("App Bar Elements is empty", MODULE);
						}
					} else {
						Debug.logError("App Bar data is empty", MODULE);
					}
				} else {
					Debug.logError("App Bar is empty", MODULE);
				}
			} else {
				Debug.logError("AppBarId/AppBarTypeId is empty", MODULE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<GenericValue> getUserPreferenceAppBar(List<GenericValue> appBarElementList, List<GenericValue> appBarUserPreferenceList) {
		List<GenericValue> finalList = new LinkedList<GenericValue>();
		try {
			for(GenericValue userPreference : appBarUserPreferenceList) {
				String elementId = userPreference.getString("appBarElementId");
				for(GenericValue appBarElement : appBarElementList) {
					String appBarElementId = appBarElement.getString("appBarElementId");
					if(elementId.equals(appBarElementId)) {
						finalList.add(appBarElement);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError("Error : "+e.getMessage(), MODULE);
		}
		return finalList;
	}
	
	public static Map<String, Object> getRequestContext(String appBarElementTargetUrl, Map<String, Object> requestParameter, Map<String, Object> context) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		if(UtilValidate.isNotEmpty(appBarElementTargetUrl)) {
			if(appBarElementTargetUrl.contains("?")) {
				try {
					//Debug.logInfo("context------->"+context, MODULE);
					//Debug.logInfo("requestParameterMap------->"+requestParameter, MODULE);
					String requestURI = appBarElementTargetUrl.substring(0, appBarElementTargetUrl.lastIndexOf("?"));
					String queryString = appBarElementTargetUrl.substring(appBarElementTargetUrl.lastIndexOf("?")+1);
					if(UtilValidate.isNotEmpty(requestURI)) {
						//result.put("requestURI", requestURI);
						String[] queryParams = queryString.split("&");
						Map<String, Object> params = new LinkedHashMap<String, Object>();
						for (String queryParam : queryParams) {
							String paramValue = "";
							if(queryParam.contains("=")) {
								int idx = queryParam.indexOf('=');
								String key = queryParam.substring(0, idx);
								key = URLDecoder.decode(key, "UTF-8");
								String value = null;
								if (idx > 0) {
									value = URLDecoder.decode(queryParam.substring(idx + 1), "UTF-8");
									if((value !=null && value.length() >0) && value.startsWith("${") && value.endsWith("}")) {
										value = value.substring(value.indexOf("{")+1, value.indexOf("}"));
										paramValue = UtilValidate.isNotEmpty(requestParameter.get(value)) ? (String)requestParameter.get(value) : UtilValidate.isNotEmpty(context.get(value)) ? (String)context.get(value) : "";
									} 
								}
								params.put(key, paramValue);
							}
						}
						String queryStr = params.entrySet()
			                     .stream()
			                     .map(Object::toString)
			                     .collect(Collectors.joining("&"));
						result.put("params", queryStr);
						if(UtilValidate.isNotEmpty(queryStr))
							result.put("requestURI", requestURI+"?"+queryStr);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} else {
				result.put("requestURI", appBarElementTargetUrl);
			}
		}
		return result;
	}
	
}
