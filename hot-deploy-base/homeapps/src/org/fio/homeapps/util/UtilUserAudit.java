/**
 * 
 */
package org.fio.homeapps.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.constants.GlobalConstants.ModeOfAction;
import org.fio.homeapps.constants.UserAuditConstants.ApprovalStatus;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import net.sf.json.JSONObject;

/**
 * @author Sharif
 *
 */
public class UtilUserAudit {

	private static String MODULE = UtilUserAudit.class.getName();
	
	public static String getRequestUri(Delegator delegator, Map contextMap, String modeOfAction, String requestUri, String loadKeys) {
		
		String preparedRequestUri = null;
		
		try {
			
			if (UtilValidate.isNotEmpty(modeOfAction) && UtilValidate.isNotEmpty(requestUri)) {
				
				preparedRequestUri = requestUri;
				preparedRequestUri += !preparedRequestUri.contains("?") ? "?" : preparedRequestUri;
				
				if (modeOfAction.equals(ModeOfAction.UPDATE)) {
					if (UtilValidate.isNotEmpty(loadKeys)) {
						String[] keys = loadKeys.split(",");
						int count = 0;
						for (String key : keys) {
							if (contextMap.containsKey(key)) {
								preparedRequestUri += (count != 0) ? "&" : "";
								preparedRequestUri += key + "=" + contextMap.get(key);
							}
							count++;
						}
					}
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return preparedRequestUri;
	}
	
	public static Map<String, Object> prepareInputContext(GenericValue auditRequest) {
		
		Map<String, Object> inputContext = new LinkedHashMap<String, Object>();
		
		try {
			
			if (UtilValidate.isNotEmpty(auditRequest)) {
				
				String jsonContext = auditRequest.getString("contextMap");
				
				JSONObject jsonContextObj = JSONObject.fromObject(jsonContext);
				Map contextMap = ParamUtil.jsonToMap(jsonContextObj);
				
				inputContext.putAll(contextMap);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return inputContext;
	}
	
	public static Map<String, Object> prepareInputAuditTrack(GenericValue auditRequest) {
		
		Map<String, Object> auditTrackList = new LinkedHashMap<String, Object>();
		
		try {
			
			if (UtilValidate.isNotEmpty(auditRequest) && UtilValidate.isNotEmpty(auditRequest.getString("contextMap"))
					&& UtilValidate.isNotEmpty(auditRequest.getString("oldContextMap"))
					) {
				
				String jsonContext = auditRequest.getString("contextMap");
				
				JSONObject jsonContextObj = JSONObject.fromObject(jsonContext);
				Map<String ,Object> contextMap = ParamUtil.jsonToMap(jsonContextObj);
				
				Map<String ,Object> oldContextMap = ParamUtil.jsonToMap(JSONObject.fromObject(auditRequest.getString("oldContextMap")));
				
				for (String key : contextMap.keySet()) {
					if (oldContextMap.containsKey(key) && !((String) contextMap.get(key)).equalsIgnoreCase((String) oldContextMap.get(key))) {
						auditTrackList.put(key, true);
					} else {
						auditTrackList.put(key, false);
					}
				}
				
				//auditTrackList.putAll(contextMap);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return auditTrackList;
	}
	
	public static String isValidAction(Delegator delegator, GenericValue userLogin, String serviceRequestType, String userAuditRequestId, String activeApp, String actionPermissionId) {
		
		Map<String, Object> contextMap = new HashMap<String, Object>();
		
		contextMap.put("serviceRequestType", serviceRequestType);
		contextMap.put("userAuditRequestId", userAuditRequestId);
		contextMap.put("activeApp", activeApp);
		contextMap.put("actionPermissionId", actionPermissionId);
		contextMap.put("userLogin", userLogin);
		
		return isValidAction(delegator, contextMap) ? "Y" : "N";
	}
	
	public static boolean isValidAction(Delegator delegator, Map<String, Object> contextMap) {
		
		String serviceRequestType = (String) contextMap.get("serviceRequestType");
		String userAuditRequestId = (String) contextMap.get("userAuditRequestId");
		String activeApp = (String) contextMap.get("activeApp");
		String actionPermissionId = (String) contextMap.get("actionPermissionId");
		
		GenericValue userLogin = (GenericValue) contextMap.get("userLogin");
		
		boolean validAction = false;
		
		try {
			
			if (UtilValidate.isNotEmpty(actionPermissionId)) {
				
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")),
						EntityCondition.makeCondition("permissionId", EntityOperator.EQUALS, actionPermissionId)
	                    ));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue sgp = EntityUtil.getFirst( delegator.findList("UserLoginSecurityGroupPermission", mainConditons, null, null, null, false) );
				if (UtilValidate.isNotEmpty(sgp)) {
					validAction = true;
				}
				
				if (validAction && UtilValidate.isNotEmpty(userAuditRequestId)) {
					conditionList = FastList.newInstance();
					
					conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("userAuditRequestId", EntityOperator.EQUALS, userAuditRequestId),
							EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, ApprovalStatus.PENDING)
		                    ));
					
					mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					GenericValue auditRequest = EntityUtil.getFirst( delegator.findList("UserAuditRequest", mainConditons, null, null, null, false) );
					if (UtilValidate.isEmpty(auditRequest) 
							|| !auditRequest.getString("makerPartyId").equalsIgnoreCase(userLogin.getString("userLoginId"))
							) {
						validAction = false;
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return validAction;

	}
	
	public static boolean isValidScreenAction(Delegator delegator, GenericValue userLogin, String activeApp, String pageId) {
		
    	boolean validAction = false;
    	
    	try {
    		
    		if (UtilValidate.isNotEmpty(userLogin) && UtilValidate.isNotEmpty(activeApp) && UtilValidate.isNotEmpty(pageId)) {
    			
    			GenericValue ofbizComponentAccess = EntityQuery.use(delegator).from("OfbizComponentAccess")
    					.where("componentName", activeApp).queryFirst();
    			
    			if(UtilValidate.isNotEmpty(ofbizComponentAccess)) {
    				
    				if(DataUtil.hasFullPermission(delegator, userLogin.getString("userLoginId"))) {
    					return true;
    				}

    				GenericValue ofbizTabSecurityShortcut = EntityQuery.use(delegator).from("OfbizTabSecurityShortcut")
    						.where("componentId", ofbizComponentAccess.getString("componentId"), "pageType", "SHORTCUT",
    								"pageId", pageId).queryFirst();
    				
    				if(UtilValidate.isNotEmpty(ofbizTabSecurityShortcut) && UtilValidate.isNotEmpty(ofbizTabSecurityShortcut.getString("parentShortcutId"))) {

    					List conditionList = FastList.newInstance();
    					
    					conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
    							EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")),
    							EntityCondition.makeCondition("permissionId", EntityOperator.EQUALS, ofbizTabSecurityShortcut.getString("permissionId"))
    		                    ));
    					
    					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    					GenericValue sgp = EntityUtil.getFirst( delegator.findList("UserLoginSecurityGroupPermission", mainConditons, null, null, null, false) );
    					if (UtilValidate.isNotEmpty(sgp)) {
    						GenericValue sg = EntityUtil.getFirst(delegator.findByAnd("SecurityGroup", UtilMisc.toMap("groupId", sgp.get("groupId")), null,false));
    		    			if (UtilValidate.isNotEmpty(sg)) {
    		    				if (UtilValidate.isNotEmpty(sg.getString("customSecurityGroupType")) && sg.getString("customSecurityGroupType").equals("Y")) {
    		    					validAction = true;
    		    				}
    		    			}
    					}
    					
    				}
    			}

    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	return validAction;
    }
	
	public static boolean isFullAccessUser(Delegator delegator, GenericValue userLogin, String activeApp) {
		
    	boolean fullAccess = false;

    	try {
    		
    		if (UtilValidate.isNotEmpty(activeApp)) {
    			
    			GenericValue systemProperty = EntityUtil.getFirst(delegator.findByAnd("SystemProperty", UtilMisc.toMap("systemResourceId", activeApp, "systemPropertyValue", "FULL_ACCESS"), null,false));
    			if (UtilValidate.isNotEmpty(systemProperty)) {
    				List conditionList = FastList.newInstance();
    				
    				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
    						EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")),
    						EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, systemProperty.getString("systemPropertyId"))
    	                    ));
    				
    				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    				GenericValue securityGroup = EntityUtil.getFirst( delegator.findList("UserLoginSecurityGroup", mainConditons, null, null, null, false) );
    				if (UtilValidate.isNotEmpty(securityGroup)) {
    					fullAccess = true;
    				}
    				
    			}	
    			
    		}
    		
    	} catch(Exception e) {
    		e.printStackTrace();
    	}

    	return fullAccess;
    }
	
	public static String isPerformUserAudit(Delegator delegator, GenericValue userLogin, String auditPermissionId) {
		
    	try {
    		
    		if (UtilValidate.isNotEmpty(auditPermissionId)) {
    			
    			List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")),
						EntityCondition.makeCondition("permissionId", EntityOperator.EQUALS, auditPermissionId)
	                    ));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue securityGroupPermission = EntityUtil.getFirst( delegator.findList("UserLoginSecurityGroupPermission", mainConditons, null, null, null, false) );
				if (UtilValidate.isNotEmpty(securityGroupPermission)) {
					return "Y";
				}
    			
    		}
    		
    	} catch(Exception e) {
    		e.printStackTrace();
    	}

    	return "N";
    }
	
	public static String getUserAuditOperatorType(Delegator delegator, String userLoginId) {
		
		String operatorType = null;

    	try {
    		
    		if (UtilValidate.isNotEmpty(userLoginId)) {
    			
    			boolean isMaker = false;
    			boolean isCheker = false;
    			
    			List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId),
						EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "DBS_ADMPR_MAKER"),
						EntityUtil.getFilterByDateExpr()
						)
						);
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue loginSecurityGroup = EntityUtil.getFirst( delegator.findList("UserLoginSecurityGroup", mainConditons, null, null, null, false) );
				if (UtilValidate.isNotEmpty(loginSecurityGroup)) {
					isMaker = true;
				}
				
				conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId),
						EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "DBS_ADMPR_CHEKER"),
						EntityUtil.getFilterByDateExpr()
						)
						);
				
				mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				loginSecurityGroup = EntityUtil.getFirst( delegator.findList("UserLoginSecurityGroup", mainConditons, null, null, null, false) );
				if (UtilValidate.isNotEmpty(loginSecurityGroup)) {
					isCheker = true;
				}
				
				if (isMaker && isCheker) {
					operatorType = "Maker and Checker";
				} else if (isMaker) {
					operatorType = "Maker";
				} else if (isCheker) {
					operatorType = "Checker";
				}
    			
    		}
    		
    	} catch(Exception e) {
    		e.printStackTrace();
    	}

    	return operatorType;
    }
	
	public static List<GenericValue> getUserLoginList(Delegator delegator, String securityGroupId) {
		
		List<GenericValue> userLoginList = new ArrayList<GenericValue>();
		
		try {
			
			List conditionList = FastList.newInstance();
			
			conditionList.add(
					EntityUtil.getFilterByDateExpr()
                    );
			
			if (UtilValidate.isNotEmpty(securityGroupId)) {
				conditionList.add(
						EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, securityGroupId)
						);
			}
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			userLoginList = delegator.findList("UserLoginAndSecurityGroup", mainConditons, null, null, null, false);
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return userLoginList;

	}
	
	public static Map<String, Object> prepareValueCompare(Delegator delegator, String ov, String nv, String serviceRequestType) {
		
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		List<Map<String, Object>> compareList = new ArrayList<Map<String, Object>>();
		int totalChanged = 0;
		String modeOfAction = null;
		
		try {
			
			GenericValue auditPref = null;
			
			if (UtilValidate.isNotEmpty(serviceRequestType)) {
				auditPref = EntityUtil.getFirst( delegator.findByAnd("UserAuditPref", UtilMisc.toMap("userAuditPrefId", serviceRequestType), null, false) );
				modeOfAction = auditPref.getString("modeOfAction");
			}
			
			if (UtilValidate.isNotEmpty(ov) && UtilValidate.isNotEmpty(nv) && UtilValidate.isNotEmpty(auditPref)) {
				
				String loadKeys = auditPref.getString("loadKeys");
				
				JSONObject oldContext = JSONObject.fromObject(ov);
				JSONObject newContext = JSONObject.fromObject(nv);
				
				for (Object key : newContext.keySet()) {
					
					if (loadKeys.contains(key.toString())) {
						continue;
					}
					
					String newValue = (String) newContext.get(key);
			        String oldValue = (String) oldContext.get(key);
			        
			        Map<String, Object> compare = new LinkedHashMap<String, Object>();
			        
			        compare.put("propName", key);
			        compare.put("propLabel", DataHelper.javaPropToLabelProp(""+key));
			        compare.put("oldValue", oldValue);
			        compare.put("newValue", newValue);
			        
			        boolean isChanged = !newValue.equalsIgnoreCase(oldValue);
			        compare.put("isChanged", isChanged);
			        
			        if (isChanged) {
			        	totalChanged++;
			        }
			        
			        compareList.add(compare);
			    }
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		String totalChangedValue = "" + totalChanged;
		if (UtilValidate.isNotEmpty(modeOfAction) && modeOfAction.equals("CREATE")) {
			totalChangedValue = "C";
		}
		
		result.put("compareList", compareList);
		result.put("totalChanged", totalChangedValue);
		
		return result;

	}
	
}
