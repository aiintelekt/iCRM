package org.fio.appbar.portal.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.fio.appbar.portal.constant.AppBarConstants.AppBarDataLevel;
import org.fio.appbar.portal.util.AppBarConfigureUtil;
import org.fio.appbar.portal.util.DataHelper;
import org.fio.appbar.portal.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.ModelUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class AppBarConfigureServices {
	private static final String MODULE = AppBarConfigureServices.class.getName();
	private static String RESOURCE = "AppbarPortalUiLabels";
	public static Map<String, Object> updateAppBarData(DispatchContext dctx, Map<String, Object> context ) {
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        /*Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale"); */
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String appBarId = (String) context.get("appBarId");
        String appBarTypeId = (String) context.get("appBarTypeId");
        String geoId = (String) context.get("geoId");
		try {
			if(UtilValidate.isNotEmpty(appBarId) && UtilValidate.isNotEmpty(appBarTypeId)) {
				List<EntityCondition> appBarCond = new ArrayList<EntityCondition>();
				
				appBarCond.add(EntityCondition.makeCondition("appBarId",EntityOperator.EQUALS,appBarId));
				appBarCond.add(EntityCondition.makeCondition("appBarTypeId",EntityOperator.EQUALS,appBarTypeId));
				if(UtilValidate.isNotEmpty(geoId)) {
					appBarCond.add(EntityCondition.makeCondition("geoId",EntityOperator.EQUALS,geoId));
				}
				//AppBar
				appBarCond.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("appBarStatus",EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("appBarStatus",EntityOperator.EQUALS, "ENABLED")
						));
				
				GenericValue appBar = EntityQuery.use(delegator).from("AppBar").where(EntityCondition.makeCondition(appBarCond,EntityOperator.AND)).queryFirst();
				if(UtilValidate.isNotEmpty(appBar)) {
					String appBarIsServiceBased = appBar.getString("appBarIsServiceBased");
					if("Y".equalsIgnoreCase(appBarIsServiceBased)) {
						String appBarService = appBar.getString("appBarService");
						ModelService service = dctx.getModelService(appBarService);
			            if(UtilValidate.isNotEmpty(service)) {
			            	Map<String, Object> input = service.makeValid(context, "IN");
				            dispatcher.runAsync(appBarService, input,null,true,300,true);
			            } else {
			            	service.maxRetry = 1;
			            	result = ServiceUtil.returnError("Could not found the service by name (" + appBarService + ")");
			            	return result;
			            } 
					} else {
		            	result = ServiceUtil.returnError("Error : "+appBarId +" is not service based");
		            	return result;
		            } 
				} else {
	            	result = ServiceUtil.returnError("Error : Please check the app bar configuration for "+appBarId);
	            	return result;
	            } 
			} else {
            	result = ServiceUtil.returnError("Error : App Bar Id/App Bar Type Id is empty");
            	return result;
            } 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> updateDashBoardBar(DispatchContext dctx, Map<String, Object> context ) {
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        /*Security security = dctx.getSecurity();
        Locale locale = (Locale) context.get("locale"); */
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String appBarId = (String) context.get("appBarId");
        String appBarTypeId = (String) context.get("appBarTypeId");
        String geoId = (String) context.get("geoId");
        String jsonData = (String) context.get("elementDataJson");
        try {
        	if(UtilValidate.isNotEmpty(appBarId) && UtilValidate.isNotEmpty(appBarTypeId)) {
        		Map<String, Object> jsonMap = new HashMap<String, Object>();
        		if(UtilValidate.isNotEmpty(jsonData)) {
        			jsonMap = DataUtil.convertToMap(jsonData);
				}
        		List<EntityCondition> commonConditions = new ArrayList<EntityCondition>();
				
				commonConditions.add(EntityCondition.makeCondition("appBarId",EntityOperator.EQUALS,appBarId));
				commonConditions.add(EntityCondition.makeCondition("appBarTypeId",EntityOperator.EQUALS,appBarTypeId));
				
				Set<String> elements = jsonMap.keySet();
				//AppBar	
				List<EntityCondition> appBarCond = new ArrayList<EntityCondition>();
				appBarCond.addAll(commonConditions);
				appBarCond.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("appBarStatus",EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("appBarStatus",EntityOperator.EQUALS, "ENABLED")
						));
				if(UtilValidate.isNotEmpty(geoId)) {
					appBarCond.add(EntityCondition.makeCondition("geoId",EntityOperator.EQUALS,geoId));
				}
				
				
				GenericValue appBar = EntityQuery
									  .use(delegator)
									  .from("AppBar")
									  .where(EntityCondition.makeCondition(appBarCond,EntityOperator.AND))
									  .queryFirst();
				if(UtilValidate.isNotEmpty(appBar)) {
					String appBarDataLevel = appBar.getString("appBarDataLevel");
					//Get the element json data
					String userLoginId = userLogin.getString("userLoginId");
					Map<String, Object> userDetails = DataHelper.getLoginUserDetails(delegator, userLoginId);
					
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
					
					GenericValue appBarData = EntityQuery
											  .use(delegator)
											  .from("AppBarData")
											  .where(EntityCondition.makeCondition(appBarDataCond,EntityOperator.AND))
											  .filterByDate()
											  .orderBy("-lastUpdatedTxStamp")
											  .queryFirst();
					
					if(UtilValidate.isNotEmpty(appBarData)) {
						String jsonResponse = null;
						if(UtilValidate.isNotEmpty(appBarData)) {
							jsonResponse = appBarData.getString("jsonResponse");
						}
						Map<String, Object> appBarJsonEleMap = DataUtil.convertToMap(jsonResponse);
						
						List<EntityCondition> appBarElementCond = new ArrayList<EntityCondition>();
						appBarElementCond.addAll(commonConditions);
						if(UtilValidate.isNotEmpty(elements)) {
							appBarElementCond.add(EntityCondition.makeCondition("appBarElementId",EntityOperator.IN,elements));
						}
						Set<String> elementFields = new TreeSet<String>();
						elementFields.add("appBarElementId");
						elementFields.add("appBarElementServiceName");
						List<GenericValue> appBarElements = EntityQuery
															.use(delegator)
															.select(elementFields)
															.from("AppBarElements")
															.where(EntityCondition.makeCondition(appBarElementCond,EntityOperator.AND))
															.queryList();
						if(UtilValidate.isNotEmpty(appBarElements)) {
							for(GenericValue appBarElement : appBarElements) {
								String appBarElementId = appBarElement.getString("appBarElementId");
								String appBarElementService = appBarElement.getString("appBarElementServiceName");
								@SuppressWarnings("unchecked")
								Map<String, Object> elementMap = (Map<String, Object>) jsonMap.get(appBarElementId);
								String isServiceBased = elementMap.containsKey("isServiceBased") ? (String) elementMap.get("isServiceBased") : "";
								String elementValue = "";
								if("Y".equals(isServiceBased)) {
									if(UtilValidate.isNotEmpty(appBarElementService)) {
										ModelService service = dctx.getModelService(appBarElementService);
							            if(UtilValidate.isNotEmpty(service)) {
							            	Map<String, Object> input = new HashMap<String, Object>();
							            	input.put("appBarElementId", appBarElementId);
							            	input.put("elementJsonData", DataUtil.convertToJson(elementMap));
								            result = dispatcher.runSync(appBarElementService, input);
								            if(ServiceUtil.isSuccess(result)) {
								            	elementValue = result.containsKey("elementValue") ? (String) result.get("elementValue") : "";
								            }
							            }  else {
							            	result = ServiceUtil.returnError("Could not found the service by name (" + appBarElementService + ")");
							            	return result;
							            } 
									} else {
						            	result = ServiceUtil.returnError("Element service is empty.");
						            	return result;
						            }
								} else {
									elementValue = elementMap.containsKey("elementValue") ? (String) elementMap.get("elementValue") : "";
								}
								
								appBarJsonEleMap.put(appBarElementId, elementValue);
								
							}
						}
						appBarData.set("jsonResponse", DataUtil.convertToJson(appBarJsonEleMap));
						appBarData.store();
					} else {
						result = ServiceUtil.returnError("App Bar data is empty.");
		            	return result;
					}
				} else {
					result = ServiceUtil.returnError("App Bar is empty.");
	            	return result;
				}
        	}
        	
        } catch (Exception e) {
        	e.printStackTrace();
		}
        return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAppBarConfiguration(DispatchContext dctx, Map<String, Object> context ) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		/*Security security = dctx.getSecurity();
        Locale locale = (Locale) context.get("locale"); */
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		String appBarId = (String) context.get("appBarId");
		String appBarTypeId = (String) context.get("appBarTypeId");
		String geoId = (String) context.get("geoId");
		String hasUserPreference = (String) context.get("userPreference");
		HttpSession  session = (HttpSession) context.get("session");
		HttpServletRequest request = (HttpServletRequest) context.get("request");
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
				List<String> groupIds = UtilValidate.isNotEmpty(session.getAttribute("userLoginSecurityGroups")) ? (List<String>) session.getAttribute("userLoginSecurityGroups") : UtilValidate.isNotEmpty(request.getAttribute("userLoginSecurityGroups")) ? (List<String>) request.getAttribute("userLoginSecurityGroups") : new LinkedList<>();
				if(UtilValidate.isEmpty(groupIds)) {
					 String userLoginPartyId = userLogin.getString("partyId");
					 Map<String, Object> userData =  org.fio.homeapps.util.DataHelper.getUserRoleGroup(delegator, userLoginPartyId);
					 groupIds = UtilValidate.isNotEmpty(userData) ? (List<String>) userData.get("userLoginSecurityGroups") : new LinkedList<>();
					 List<String> userLoginRoles = UtilValidate.isNotEmpty(userData) ? (List<String>) userData.get("userLoginRoles") : new LinkedList<>();
					 
					 session.setAttribute("userLoginSecurityGroups", groupIds);
					 request.setAttribute("userLoginSecurityGroups", groupIds);
					 session.setAttribute("userLoginRoles", userLoginRoles);
					 request.setAttribute("userLoginRoles", userLoginRoles);
				 }
				Debug.logInfo("groupIds--->"+groupIds, MODULE);
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
					int barMaxElements = UtilValidate.isNotEmpty(appBar.getInteger("appBarMaxElements")) ? appBar.getInteger("appBarMaxElements") :5;
					String appBarAccessLevel = appBar.getString("appBarAccessLevel");
					
					/*
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

					Map<String, Object> appBarJsonEleMap = new HashMap<String, Object>();
					GenericValue appBarData = EntityQuery.use(delegator).select("appBarDataId","jsonResponse").from("AppBarData").where(EntityCondition.makeCondition(appBarDataCond,EntityOperator.AND)).filterByDate().queryFirst();
					if(UtilValidate.isNotEmpty(appBarData)) {
						String jsonData = appBarData.getString("jsonResponse");

						appBarJsonEleMap = DataUtil.convertToMap(jsonData);
					} */

					//get appbar element
					List<EntityCondition> appBarElementCond = new ArrayList<EntityCondition>();
					/*
						Set<String> elements = appBarJsonEleMap.keySet();
						if(UtilValidate.isNotEmpty(elements))
							appBarElementCond.add(EntityCondition.makeCondition("appBarElementId",EntityOperator.IN,elements)); */

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
					elementFields.add("elementKey");

					List<GenericValue> appBarElementList = EntityQuery.use(delegator).select(elementFields).from("AppBarElements").where(EntityCondition.makeCondition(appBarElementCond,EntityOperator.AND)).orderBy("appBarElementSeqNum ASC").distinct().queryList();
					if(UtilValidate.isNotEmpty(appBarElementList)) {
						if("Y".equals(hasUserPreference)) {
							List<EntityCondition> userPrefAppBarCond = new ArrayList<EntityCondition>();
							userPrefAppBarCond.addAll(commonConditions);
							userPrefAppBarCond.add(EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId));
							List<GenericValue> appBarUserPreferenceList = EntityQuery.use(delegator).from("AppBarUserPreference").where(EntityCondition.makeCondition(userPrefAppBarCond,EntityOperator.AND)).orderBy("appBarElementSeqNum ASC").distinct().maxRows(barMaxElements).queryList();
							if(UtilValidate.isNotEmpty(appBarUserPreferenceList)) {
								appBarElementList  = AppBarConfigureUtil.getUserPreferenceAppBar(appBarElementList, appBarUserPreferenceList);
							}
							if(appBarElementList.size() < barMaxElements)
								barMaxElements = appBarElementList.size();
							appBarElementList = appBarElementList.subList(0, barMaxElements);
						} else {
							if(appBarElementList.size() < barMaxElements)
								barMaxElements = appBarElementList.size();
							appBarElementList = appBarElementList.subList(0, barMaxElements);
						}
						
						boolean hasFullAccess = DataHelper.hasFullPermission(delegator, userLoginId);
						Debug.logInfo("Appbar element exists" +appBarId+" - "+appBarTypeId, MODULE);
						
						for(GenericValue appBarElement : appBarElementList) {
							Map<String, Object> elementMap = new HashMap<String, Object>();
							String appBarElementId = appBarElement.getString("appBarElementId");
							String elementKey = UtilValidate.isNotEmpty(appBarElement.getString("elementKey")) ? appBarElement.getString("elementKey") : "";
							String securityPermissionId = appBarElement.getString("securityPermissionId");
							boolean hasPermission = true;
							if(!hasFullAccess) {
								hasPermission = DataHelper.validateSecurityPermission(delegator, groupIds, securityPermissionId);
							}
							if(hasPermission) {
								// to get the element value by the configuration script
								elementMap.putAll(appBarElement);
								Map<String, Object> result1 = dispatcher.runSync("ab.getAppBarElementData", UtilMisc.toMap("userLogin", userLogin, "appBarId",appBarId, "appBarTypeId", appBarTypeId, "appBarElementId", appBarElementId));
								if(ServiceUtil.isSuccess(result1)) {
									Map<String, Object> dataMap = (Map<String, Object>) result1.get("dataMap");
									elementMap.put("dataMap", UtilValidate.isNotEmpty(dataMap) ? dataMap : "");
								}
								
								elementConfigMap.put(appBarElementId, elementMap);

								//elementDataMap.put(appBarElementId, elementKey);
							}
						}
						//if(UtilValidate.isNotEmpty(elementConfigMap) && UtilValidate.isNotEmpty(elementDataMap)) {
						if(UtilValidate.isNotEmpty(elementConfigMap)) {
							Map<String, Object> elementMap = new HashMap<String, Object>();	
							elementMap.put("configuration", elementConfigMap);
							elementMap.put("dataList", elementDataMap);

							elementMap.put("appBarAccessLevel", appBarAccessLevel);
							elementMap.put("appBarDirection", appBar.getString("appBarDirection"));
							elementMap.put("appBarHeight", appBar.getString("appBarHeight"));
							elementMap.put("appBarWidth", appBar.getString("appBarWidth"));
							elementMap.put("appBarIsServiceBased", appBar.getString("appBarIsServiceBased"));
							elementMap.put("appBarRefreshType", appBar.getString("appBarRefreshType"));
							elementMap.put("geoId", appBar.getString("geoId"));
							elementMap.put("defaultMessage", appBar.getString("defaultMessage"));
							result.put("appBarElementData", elementMap);
							
						} else {
							Map<String, Object> elementMap = new HashMap<String, Object>();	
							elementMap.put("defaultMessage", appBar.getString("defaultMessage"));
							result.putAll(ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AppBarSecurityGroupNotMatch", locale)));
							result.put("appBarElementData", elementMap);
							result.put("responseCode", "EA104");
						}
						
					} else {
						//result = ServiceUtil.returnError("App Bar Elements is empty");
						Debug.logError("App Bar Elements is empty", MODULE);
						result.putAll(ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AppBarElementIsEmpty", locale)));
						result.put("responseCode", "EA100");
						return result;
						//return result;
					}
					/*
					} else {
						//result = ServiceUtil.returnError("App Bar data is empty");
						Debug.logError("App Bar data is empty", MODULE);
						result.putAll(ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AppBarDataIsEmpty", locale)));
	        			result.put("responseCode", "EA101");
	        			return result;
						//return result;
					}*/
				} else {
					//result = ServiceUtil.returnError("App Bar is empty");
					Debug.logError("App Bar is empty", MODULE);
					result.putAll(ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AppBarIsEmpty", locale)));
					result.put("responseCode", "EA102");
					return result;
					//return result;
				}
			} else {
				//result = ServiceUtil.returnError("AppBarId/AppBarTypeId is empty");
				Debug.logError("AppBarId/AppBarTypeId is empty", MODULE);
				result.putAll(ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "RequiredParameterMissed", locale)));
				result.put("responseCode", "EA103");
				return result;
				//return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.put("responseCode", "SA200");
		result.putAll(ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AppBarElementloaded!", locale)));

		return result;
	}
	
	public static Map<String, Object> createAppBar(DispatchContext dctx, Map<String, Object> context ) {
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String appBarId = (String) context.get("appBarId");
        String appBarTypeId = (String) context.get("appBarTypeId");
		try {
			if(UtilValidate.isEmpty(appBarId) && UtilValidate.isEmpty(appBarTypeId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "ParameterMissed", locale));
			}
			
			if(appBarId.length() > 15)
				appBarId = appBarId.substring(0, 16);
			
			GenericValue appBar = EntityQuery
									.use(delegator)
									.from("AppBar")
									.where("appBarId",appBarId,"appBarTypeId",appBarTypeId)
									.queryFirst();
			if(UtilValidate.isEmpty(appBar)) {
				String securityGroupId = appBarId+"_"+appBarTypeId+"_GRP";
				
				//create security group for the particular app bar
				GenericValue securityGroup = EntityQuery.use(delegator).from("SecurityGroup").where("groupId", securityGroupId).queryFirst();
				if(UtilValidate.isEmpty(securityGroup)) {
					securityGroup = delegator.makeValue("SecurityGroup");
					securityGroup.set("groupId", securityGroupId);
					securityGroup.set("description", (String) context.get("appBarName")+" Security Group");
					securityGroup.create();
				}
				
				appBar = delegator.makeValue("AppBar");
				appBar.setPKFields(context, true);
				appBar.setNonPKFields(context);
				appBar.set("appBarSecurityGroup", securityGroupId);
				appBar.set("createdBy", userLogin.getString("userLoginId"));
				appBar.set("modifiedBy", userLogin.getString("userLoginId"));
				appBar.set("createdOn", UtilDateTime.nowTimestamp());
				appBar.set("modifiedOn", UtilDateTime.nowTimestamp());
				appBar.create();
			} else {
				result = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "RecordExists", locale));
				result.put("appBarId", appBarId);
				result.put("appBarTypeId", appBarTypeId);
				return result;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.putAll(ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AppBarCreatedSuccessfully", locale)));
		result.put("appBarId", appBarId);
		result.put("appBarTypeId", appBarTypeId);
		return result;
	}
	
	public static Map<String, Object> updateAppBar(DispatchContext dctx, Map<String, Object> context ) {
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String appBarId = (String) context.get("appBarId");
        String appBarTypeId = (String) context.get("appBarTypeId");
		try {
			if(UtilValidate.isEmpty(appBarId) && UtilValidate.isEmpty(appBarTypeId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "ParameterMissed", locale));
			}

			GenericValue appBar = EntityQuery
									.use(delegator)
									.from("AppBar")
									.where("appBarId",appBarId,"appBarTypeId",appBarTypeId)
									.queryFirst();
			if(UtilValidate.isNotEmpty(appBar)) {
				//appBar = delegator.makeValue("AppBar");
				appBar.setNonPKFields(context);
				appBar.set("modifiedBy", userLogin.getString("userLoginId"));
				appBar.set("modifiedOn", UtilDateTime.nowTimestamp());
				appBar.store();
			} else {
				result = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "RecordNotExists", locale));
				result.put("appBarId", appBarId);
				result.put("appBarTypeId", appBarTypeId);
				return result;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.putAll(ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AppBarUpdatedSuccessfully", locale)));
		result.put("appBarId", appBarId);
		result.put("appBarTypeId", appBarTypeId);
		return result;
	}
	
	public static Map<String, Object> createAppBarElement(DispatchContext dctx, Map<String, Object> context ) {
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String appBarId = (String) context.get("appBarId");
        String appBarTypeId = (String) context.get("appBarTypeId");
        String appBarElementId = (String) context.get("appBarElementId");
        String elementKey = (String) context.get("elementKey");
		try {
			if(UtilValidate.isEmpty(appBarId) && UtilValidate.isEmpty(appBarTypeId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "ParameterMissed", locale));
			}
			if(UtilValidate.isEmpty(appBarElementId)) {
				appBarElementId = delegator.getNextSeqId("AppBarElements");
				context.put("appBarElementId", appBarElementId);
			}
			/*
			GenericValue appBar = EntityQuery.use(delegator).from("AppBar").where("appBarId",appBarId,"appBarTypeId",appBarTypeId).queryFirst();
			if(UtilValidate.isNotEmpty(appBar)) {
				long maxElement = appBar.getInteger("appBarMaxElements");
				long totalElement = EntityQuery.use(delegator).from("AppBarElements").where("appBarId",appBarId,"appBarTypeId",appBarTypeId).queryCount();
				if(totalElement >= maxElement) {
					result = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "ElementCountExceeding", locale));
					result.put("appBarId", appBarId);
					result.put("appBarTypeId", appBarTypeId);
					result.put("appBarElementId", appBarElementId);
					return result;
				}
			}
			*/
			GenericValue appBarElements = EntityQuery
									.use(delegator)
									.from("AppBarElements")
									.where("appBarId",appBarId,"appBarTypeId",appBarTypeId,"appBarElementId",appBarElementId)
									.queryFirst();
			if(UtilValidate.isEmpty(appBarElements)) {
				//create security permission for the particular app bar
				String permissionId = appBarId+"_"+appBarElementId+"_PERM";
				GenericValue securityPermission = EntityQuery.use(delegator).from("SecurityPermission").where("permissionId", permissionId).queryFirst();
				if(UtilValidate.isEmpty(securityPermission)) {
					securityPermission = delegator.makeValue("SecurityPermission");
					securityPermission.set("permissionId", permissionId);
					securityPermission.set("description", (String) context.get("appBarElementName")+" Security Permission");
					securityPermission.create();
				}
				
				String securityGroupId = appBarId+"_"+appBarTypeId+"_GRP";
				GenericValue securityGroup = EntityQuery.use(delegator).from("SecurityGroup").where("groupId", securityGroupId).queryFirst();
				if(UtilValidate.isNotEmpty(securityGroup)) {
					GenericValue securityGroupPermission = delegator.makeValue("SecurityGroupPermission");
					securityGroupPermission.set("groupId", securityGroupId);
					securityGroupPermission.set("permissionId", permissionId);
					securityGroupPermission.create();
				}
				appBarElements = delegator.makeValue("AppBarElements");
				appBarElements.setPKFields(context, true);
				appBarElements.setNonPKFields(context);
				appBarElements.set("securityPermissionId", permissionId);
				appBarElements.set("createdBy", userLogin.getString("userLoginId"));
				appBarElements.set("modifiedBy", userLogin.getString("userLoginId"));
				appBarElements.set("createdOn", UtilDateTime.nowTimestamp());
				appBarElements.set("modifiedOn", UtilDateTime.nowTimestamp());
				appBarElements.create();
			} else {
				result = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "RecordExists", locale));
				result.put("appBarId", appBarId);
				result.put("appBarTypeId", appBarTypeId);
				result.put("appBarElementId", appBarElementId);
				return result;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.putAll(ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AppBarElementCreatedSuccessfully", locale)));
		result.put("appBarId", appBarId);
		result.put("appBarTypeId", appBarTypeId);
		result.put("appBarElementId", appBarElementId);
		return result;
	}
	
	public static Map<String, Object> updateAppBarElement(DispatchContext dctx, Map<String, Object> context ) {
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String appBarId = (String) context.get("appBarId");
        String appBarTypeId = (String) context.get("appBarTypeId");
        String appBarElementId = (String) context.get("appBarElementId");
		try {
			if(UtilValidate.isEmpty(appBarId) && UtilValidate.isEmpty(appBarTypeId) && UtilValidate.isEmpty(appBarElementId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "ParameterMissed", locale));
			}

			GenericValue appBarElement = EntityQuery
									.use(delegator)
									.from("AppBarElements")
									.where("appBarId",appBarId,"appBarTypeId",appBarTypeId,"appBarElementId",appBarElementId)
									.queryFirst();
			if(UtilValidate.isNotEmpty(appBarElement)) {
				//appBarElement = delegator.makeValue("AppBarElements");
				appBarElement.setNonPKFields(context);
				appBarElement.set("modifiedBy", userLogin.getString("userLoginId"));
				appBarElement.set("modifiedOn", UtilDateTime.nowTimestamp());
				appBarElement.store();
			} else {
				result = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "RecordNotExists", locale));
				result.put("appBarId", appBarId);
				result.put("appBarTypeId", appBarTypeId);
				result.put("appBarElementId", appBarElementId);
				return result;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.putAll(ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AppBarElementUpdatedSuccessfully", locale)));
		result.put("appBarId", appBarId);
		result.put("appBarTypeId", appBarTypeId);
		result.put("appBarElementId", appBarElementId);
		return result;
	}
	
	public static Map<String, Object> getAppBarElementData(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String appBarElementId = (String) context.get("appBarElementId");
        String appBarId = (String) context.get("appBarId");
        String appBarTypeId = (String) context.get("appBarTypeId");
        
        try {
        	GenericValue appBarElement = EntityQuery.use(delegator).from("AppBarElements")
        			.where("appBarId",appBarId,"appBarTypeId",appBarTypeId,"appBarElementId",appBarElementId)
        			.queryFirst();
        	if(UtilValidate.isNotEmpty(appBarElement)) {
        		String appBarElementSqlScript = appBarElement.getString("appBarElementSqlScript");
        		
        		SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
				Connection con = (Connection)sqlProcessor.getConnection();
				ResultSet rs = null;
				if(UtilValidate.isNotEmpty(appBarElementSqlScript)) {
					rs = sqlProcessor.executeQuery(appBarElementSqlScript);

	                ResultSetMetaData rsMetaData = rs.getMetaData();
	                List<String> columnList = new ArrayList<String>();
	                //Retrieving the list of column names
	                int count = rsMetaData.getColumnCount();
	                for(int i = 1; i<=count; i++) {
	                	columnList.add(rsMetaData.getColumnName(i));
	                }
	                
					if (rs != null) {
						while (rs.next()) {
							Map<String, Object> data = new HashMap<String, Object>();
							for(String columName : columnList) {
								String fieldName = ModelUtil.dbNameToVarName(columName);
								String fieldValue = rs.getString(columName);
								if(org.fio.admin.portal.util.DataUtil.isDigits(fieldValue))
									fieldValue = org.fio.admin.portal.util.DataUtil.getFormattedNumValue(delegator, fieldValue);
								data.put(fieldName, fieldValue);
							}
							result.put("dataMap", data);
						}
					}
				}
        	}
        } catch (Exception e) {
        	e.printStackTrace();
		}
        return result;
	}
	
}
