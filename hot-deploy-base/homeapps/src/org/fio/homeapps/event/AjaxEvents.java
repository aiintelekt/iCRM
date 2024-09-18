/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fio.homeapps.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.UserAuditConstants;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilUserAudit;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Sharif Ul Islam
 * 
 */
public final class AjaxEvents {

    private AjaxEvents() { }

    private static final String MODULE = AjaxEvents.class.getName();

    public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
        return doJSONResponse(response, jsonObject.toString());
    }

    public static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
        return doJSONResponse(response, JSONArray.fromObject(collection).toString());
    }

    public static String doJSONResponse(HttpServletResponse response, Map map) {
        return doJSONResponse(response, JSONObject.fromObject(map));
    }

    public static String doJSONResponse(HttpServletResponse response, String jsonString) {
        String result = "success";

        response.setContentType("application/x-json");
        try {
            response.setContentLength(jsonString.getBytes("UTF-8").length);
        } catch (UnsupportedEncodingException e) {
            Debug.logWarning("Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(), MODULE);
            response.setContentLength(jsonString.length());
        }

        Writer out;
        try {
            out = response.getWriter();
            out.write(jsonString);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, "Failed to get response writer", MODULE);
            result = "error";
        }
        return result;
    }
    
    public static GenericValue getUserLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (GenericValue) session.getAttribute("userLogin");
    }

    /*************************************************************************/
    /**                                                                     **/
    /**                      Common JSON Requests                           **/
    /**                                                                     **/
    /*************************************************************************/
    
    @SuppressWarnings("unchecked")
    public static String processUserAuditRequest(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        HttpSession session = request.getSession(true);
        
        String approveType = request.getParameter("approveType");
    	String userAuditRequestId = request.getParameter("userAuditRequestId");
    	String partyCheckerId = userLogin.getString("userLoginId");
    	String remarks = request.getParameter("remarks");
        
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	Timestamp currentTime = UtilDateTime.nowTimestamp();
        	
        	if (UtilValidate.isNotEmpty(approveType) && UtilValidate.isNotEmpty(userAuditRequestId)) {
        		
        		List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("userAuditRequestId", EntityOperator.EQUALS, userAuditRequestId)
						)
						);
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue auditRequest = EntityUtil.getFirst( delegator.findList("UserAuditRequest", mainConditons, null, null, null, false) );
				
				if (UtilValidate.isNotEmpty(auditRequest)) {
					
	    			String serviceRequestType = auditRequest.getString("serviceRequestType");
					String jsonContext = auditRequest.getString("contextMap");
					
					JSONObject jsonContextObj = JSONObject.fromObject(jsonContext);
					Map<String, ?> contextMap = ParamUtil.jsonToMap(jsonContextObj);
					
					// execute service [start]
					
					GenericValue auditPref = EntityUtil.getFirst( delegator.findByAnd("UserAuditPref", UtilMisc.toMap("userAuditPrefId", serviceRequestType), null, false) );
					if (UtilValidate.isNotEmpty(auditPref)) {
						
						if (UserAuditConstants.ApprovalStatus.APPROVED.equals(approveType)) {
							String serviceName = auditPref.getString("serviceName");
							
							Map<String, Object> reqContext = new HashMap<String, Object>();
							
							reqContext.putAll(contextMap);
	    					reqContext.put("userLogin", userLogin);
	    					
	    					Map<String, Object> reqResult = dispatcher.runSync(serviceName, reqContext);
	    					if (ServiceUtil.isSuccess(reqResult)) {
	    						auditRequest.put("statusId", approveType);
	    		    			auditRequest.put("chekerPartyId", partyCheckerId);
	    		    			auditRequest.put("statusDate", currentTime);
	    		    			auditRequest.put("remarks", remarks);
	    		    			
	    		    			auditRequest.store();
	    					}
	    					
						} else {
							
							auditRequest.put("statusId", approveType);
    		    			auditRequest.put("chekerPartyId", partyCheckerId);
    		    			auditRequest.put("statusDate", currentTime);
    		    			auditRequest.put("remarks", remarks);
    		    			
    		    			auditRequest.store();
							
						}
						
					}
					
					// execute service [end]
					
				}
        		
        	}
        	
        	resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
        	
        } catch (Exception e) {
        	e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            
            resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
            return doJSONResponse(response, resp);
        }
        
        return doJSONResponse(response, resp);
    }
    
    @SuppressWarnings("unchecked")
    public static String getUserAuditConfigurations(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

    	Locale locale = UtilHttp.getLocale(request);
    	HttpSession session = request.getSession(true);

    	String screenProfile = request.getParameter("screenProfile");
    	String securityGroupId = request.getParameter("securityGroupId");

    	Map<String, Object> operatorList = new HashMap<String, Object>();
    	Map<String, Object> resp = new HashMap<String, Object>();

    	JSONArray datas = new JSONArray();

    	try {

    		if (UtilValidate.isNotEmpty(screenProfile) || UtilValidate.isNotEmpty(securityGroupId)) {

    			List<Map<String, Object>> securityGroupPermissionList = new LinkedList<Map<String, Object>>();
    			Map<String, Object> securityGroupPermissionMp = new HashMap<String, Object>();

    			if (UtilValidate.isNotEmpty(screenProfile) && UtilValidate.isEmpty(securityGroupId)) {
    				List<GenericValue> securityGroup = EntityQuery.use(delegator).from("SecurityGroup")
    						.where("customSecurityGroupType", "Y").queryList();
    				if(UtilValidate.isNotEmpty(securityGroup)) {
    					for(GenericValue securityGroupGv : securityGroup) {
    						securityGroupPermissionMp = new HashMap<String, Object>();
    						securityGroupPermissionMp.put("permissionId", screenProfile);
    						securityGroupPermissionMp.put("groupId", securityGroupGv.getString("groupId"));
    						securityGroupPermissionList.add(securityGroupPermissionMp);
    					}
    				}
    			} else if (UtilValidate.isEmpty(screenProfile) && UtilValidate.isNotEmpty(securityGroupId)) {
    				List<GenericValue> securityPermission = EntityQuery.use(delegator).from("SecurityPermission")
    						.where("actionType", "PARENT").queryList();
    				if(UtilValidate.isNotEmpty(securityPermission)) {
    					for(GenericValue securityPermissionGv : securityPermission) {
    						securityGroupPermissionMp = new HashMap<String, Object>();
    						securityGroupPermissionMp.put("permissionId", securityPermissionGv.getString("permissionId"));
    						securityGroupPermissionMp.put("groupId", securityGroupId);
    						securityGroupPermissionList.add(securityGroupPermissionMp);
    					}
    				}
    			} else if (UtilValidate.isNotEmpty(screenProfile) && UtilValidate.isNotEmpty(securityGroupId)) {
    				securityGroupPermissionMp = new HashMap<String, Object>();
					securityGroupPermissionMp.put("permissionId", screenProfile);
					securityGroupPermissionMp.put("groupId", securityGroupId);
					securityGroupPermissionList.add(securityGroupPermissionMp);
    			}


    			if (UtilValidate.isNotEmpty(securityGroupPermissionList)) {
    				int id = 0;
    				
    				for (Map<String,Object> securityGroupPermission : securityGroupPermissionList) {

    					String createPermissionId = "", viewPermissionId = "", editPermissionId = "", auditPermissionId = "", description = "";
    					String createPermissionChecked = "N", viewPermissionChecked = "N", editPermissionChecked = "N", auditPermissionChecked = "N";

    					JSONObject data = new JSONObject();
    					data.putAll(securityGroupPermission);
    					
    					GenericValue securityPermission = EntityQuery.use(delegator).from("SecurityPermission")
    							.where("permissionId", (String) securityGroupPermission.get("permissionId")).queryOne();
    					if(UtilValidate.isNotEmpty(securityPermission)) {
    						description = securityPermission.getString("description");
    					}
    					
    					List<GenericValue> securityPermissionList = EntityQuery.use(delegator).from("SecurityPermission")
    							.where("parentPermissionId", (String) securityGroupPermission.get("permissionId")).queryList();
    					if(UtilValidate.isNotEmpty(securityPermissionList)) {
    						for(GenericValue securityPermissionGv : securityPermissionList) {
    							if(UtilValidate.isNotEmpty(securityPermissionGv.getString("actionType")) && "CREATE".equals(securityPermissionGv.getString("actionType"))) {
    								createPermissionId = securityPermissionGv.getString("permissionId");
    								GenericValue securityGroupPerm = EntityQuery.use(delegator).from("SecurityGroupPermission")
    										.where("permissionId", createPermissionId, "groupId", (String) securityGroupPermission.get("groupId")).queryOne();
    								if(UtilValidate.isNotEmpty(securityGroupPerm)) {
    									createPermissionChecked = "Y";
    								}
    							} else if(UtilValidate.isNotEmpty(securityPermissionGv.getString("actionType")) && "VIEW".equals(securityPermissionGv.getString("actionType"))) {
    								viewPermissionId = securityPermissionGv.getString("permissionId");
    								GenericValue securityGroupPerm = EntityQuery.use(delegator).from("SecurityGroupPermission")
    										.where("permissionId", viewPermissionId, "groupId", (String) securityGroupPermission.get("groupId")).queryOne();
    								if(UtilValidate.isNotEmpty(securityGroupPerm)) {
    									viewPermissionChecked = "Y";
    								}
    							} else if(UtilValidate.isNotEmpty(securityPermissionGv.getString("actionType")) && "EDIT".equals(securityPermissionGv.getString("actionType"))) {
    								editPermissionId = securityPermissionGv.getString("permissionId");
    								GenericValue securityGroupPerm = EntityQuery.use(delegator).from("SecurityGroupPermission")
    										.where("permissionId", editPermissionId, "groupId", (String) securityGroupPermission.get("groupId")).queryOne();
    								if(UtilValidate.isNotEmpty(securityGroupPerm)) {
    									editPermissionChecked = "Y";
    								}
    							} else if(UtilValidate.isNotEmpty(securityPermissionGv.getString("actionType")) && "AUDIT".equals(securityPermissionGv.getString("actionType"))) {
    								auditPermissionId = securityPermissionGv.getString("permissionId");
    								GenericValue securityGroupPerm = EntityQuery.use(delegator).from("SecurityGroupPermission")
    										.where("permissionId", auditPermissionId, "groupId", (String) securityGroupPermission.get("groupId")).queryOne();
    								if(UtilValidate.isNotEmpty(securityGroupPerm)) {
    									auditPermissionChecked = "Y";
    								}
    							}
    						}
    						data.put("description", description);
    						data.put("createPermissionId", createPermissionId);
    						data.put("viewPermissionId", viewPermissionId);
    						data.put("editPermissionId", editPermissionId);
    						data.put("auditPermissionId", auditPermissionId);
    						data.put("createPermissionChecked", createPermissionChecked);
    						data.put("viewPermissionChecked", viewPermissionChecked);
    						data.put("editPermissionChecked", editPermissionChecked);
    						data.put("auditPermissionChecked", auditPermissionChecked);
    						data.put("id", id);
    						datas.add(data);
    						id++;
    					}
    				}
    			}

    		}

    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    	}

    	resp.put("data", datas);

    	return doJSONResponse(response, resp);
    }
    
    @SuppressWarnings("unchecked")
    public static String updateUserAuditConfiguration(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        HttpSession session = request.getSession(true);
        
        String screenProfile = request.getParameter("screenProfile");
        String securityGroupId = request.getParameter("securityGroupId");
        
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
    		
        	String permissionIdlist[] = request.getParameterValues("permissionId");
        	String groupIdlist[] = request.getParameterValues("groupId");
        	
        	Set<String> groupIdSet = new HashSet<String>();
        	if (UtilValidate.isNotEmpty(screenProfile) || UtilValidate.isNotEmpty(securityGroupId) && permissionIdlist != null && permissionIdlist.length > 0) {

        		for (int i = 0; i < permissionIdlist.length; i++) {
        		
        			String permissionId = permissionIdlist[i];
        			String groupId = groupIdlist[i];
        			String createPermissionId = request.getParameter("createPermissionId_"+i);
        			String viewPermissionId = request.getParameter("viewPermissionId_"+i);
        			String editPermissionId = request.getParameter("editPermissionId_"+i);
        			String auditPermissionId = request.getParameter("auditPermissionId_"+i);
        			String createPermissionChecked = request.getParameter("createPermissionChecked_"+i);
        			String viewPermissionChecked = request.getParameter("viewPermissionChecked_"+i);
        			String editPermissionChecked = request.getParameter("editPermissionChecked_"+i);
        			String auditPermissionChecked = request.getParameter("auditPermissionChecked_"+i);
        			
        			groupIdSet.add(groupId);

        			if(UtilValidate.isEmpty(createPermissionChecked) && UtilValidate.isEmpty(viewPermissionChecked) && UtilValidate.isEmpty(editPermissionChecked) && UtilValidate.isEmpty(auditPermissionChecked)) {
        				GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
        						.where("groupId", groupId, "permissionId", permissionId).queryOne();
        				if(UtilValidate.isNotEmpty(securityGroupPermission)) {
        					securityGroupPermission.remove();
        				}
        			} else {
        				GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
        						.where("groupId", groupId, "permissionId", permissionId).queryOne();
        				if(UtilValidate.isEmpty(securityGroupPermission)) {
        					securityGroupPermission = delegator.makeValue("SecurityGroupPermission", UtilMisc.toMap("permissionId", permissionId));
        					securityGroupPermission.put("groupId", groupId);
        					securityGroupPermission.create();
        				}
        			}
        			
        			//create permission
        			if(UtilValidate.isNotEmpty(createPermissionId) && UtilValidate.isNotEmpty(createPermissionChecked) && "Y".equals(createPermissionChecked)) {
        				GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
        						.where("groupId", groupId, "permissionId", createPermissionId).queryOne();
        				if(UtilValidate.isEmpty(securityGroupPermission)) {
        					securityGroupPermission = delegator.makeValue("SecurityGroupPermission", UtilMisc.toMap("permissionId", createPermissionId));
        					securityGroupPermission.put("groupId", groupId);
        					securityGroupPermission.create();
        				}
        			} else if(UtilValidate.isNotEmpty(createPermissionId)) {
        				GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
        						.where("groupId", groupId, "permissionId", createPermissionId).queryOne();
        				if(UtilValidate.isNotEmpty(securityGroupPermission)) {
        					securityGroupPermission.remove();
        				}
        			}
        			
        			//view permission
        			if(UtilValidate.isNotEmpty(viewPermissionId) && UtilValidate.isNotEmpty(viewPermissionChecked) && "Y".equals(viewPermissionChecked)) {
        				GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
        						.where("groupId", groupId, "permissionId", viewPermissionId).queryOne();
        				if(UtilValidate.isEmpty(securityGroupPermission)) {
        					securityGroupPermission = delegator.makeValue("SecurityGroupPermission", UtilMisc.toMap("permissionId", viewPermissionId));
        					securityGroupPermission.put("groupId", groupId);
        					securityGroupPermission.create();
        				}
        			} else if(UtilValidate.isNotEmpty(viewPermissionId)) {
        				GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
        						.where("groupId", groupId, "permissionId", viewPermissionId).queryOne();
        				if(UtilValidate.isNotEmpty(securityGroupPermission)) {
        					securityGroupPermission.remove();
        				}
        			}
        			
        			//edit permission
        			if(UtilValidate.isNotEmpty(editPermissionId) && UtilValidate.isNotEmpty(editPermissionChecked) && "Y".equals(editPermissionChecked)) {
        				GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
        						.where("groupId", groupId, "permissionId", editPermissionId).queryOne();
        				if(UtilValidate.isEmpty(securityGroupPermission)) {
        					securityGroupPermission = delegator.makeValue("SecurityGroupPermission", UtilMisc.toMap("permissionId", editPermissionId));
        					securityGroupPermission.put("groupId", groupId);
        					securityGroupPermission.create();
        				}
        			} else if(UtilValidate.isNotEmpty(editPermissionId)) {
        				GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
        						.where("groupId", groupId, "permissionId", editPermissionId).queryOne();
        				if(UtilValidate.isNotEmpty(securityGroupPermission)) {
        					securityGroupPermission.remove();
        				}
        			}
        			
        			//maker and checker permission
        			if(UtilValidate.isNotEmpty(auditPermissionId) && UtilValidate.isNotEmpty(auditPermissionChecked) && "Y".equals(auditPermissionChecked)) {
        				GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
        						.where("groupId", groupId, "permissionId", auditPermissionId).queryOne();
        				if(UtilValidate.isEmpty(securityGroupPermission)) {
        					securityGroupPermission = delegator.makeValue("SecurityGroupPermission", UtilMisc.toMap("permissionId", auditPermissionId));
        					securityGroupPermission.put("groupId", groupId);
        					securityGroupPermission.create();
        				}
        			} else if(UtilValidate.isNotEmpty(auditPermissionId)) {
        				GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
        						.where("groupId", groupId, "permissionId", auditPermissionId).queryOne();
        				if(UtilValidate.isNotEmpty(securityGroupPermission)) {
        					securityGroupPermission.remove();
        				}
        			}
        			
        		}
        		
        		if(groupIdSet != null && groupIdSet.size() > 0) {
        			for(String groupId : groupIdSet) {
        				List<GenericValue> securityPermissionMC = EntityQuery.use(delegator).from("SecurityPermission")
        						.where(EntityCondition.makeCondition("permissionId", EntityOperator.LIKE, "VND_%"),
        								EntityCondition.makeCondition("actionType", EntityOperator.IN, UtilMisc.toList("MENU", "COMPONENT"))
        								).queryList();
        				if(UtilValidate.isNotEmpty(securityPermissionMC)) {
        					for(GenericValue securityPermissionGvMC : securityPermissionMC) {
        						GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
        								.where("groupId", groupId, "permissionId", securityPermissionGvMC.getString("permissionId")).queryOne();
        						if(UtilValidate.isEmpty(securityGroupPermission)) {
        							securityGroupPermission = delegator.makeValue("SecurityGroupPermission", UtilMisc.toMap("permissionId", securityPermissionGvMC.getString("permissionId")));
        							securityGroupPermission.put("groupId", groupId);
        							securityGroupPermission.create();
        						}
        					}
        				}
        			}
        		}
        		
        		resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
            	resp.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully Updated User Audit Configuration!!");
        	} else {
        		resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            	resp.put(GlobalConstants.RESPONSE_MESSAGE, "No data available!!");
        	}
        	
        } catch (Exception e) {
        	e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            
            resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
            return doJSONResponse(response, resp);
        }
        
        return doJSONResponse(response, resp);
    }
    
    @SuppressWarnings("unchecked")
	public static String applyUserAuditOperator(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String rowsSelectedMaker[] = request.getParameterValues("rowsSelectedMaker[]");
		String rowsSelectedCheker[] = request.getParameterValues("rowsSelectedCheker[]");

		Map<String, Object> resp = new HashMap<String, Object>();

		int successCount = 0;
		
		try {
			
			if (UtilValidate.isNotEmpty(rowsSelectedMaker)) {
				
				for (int i = 0; i < rowsSelectedMaker.length; i++) {
					String userLoginId = rowsSelectedMaker[i];
					
					List<EntityCondition> conditions = new ArrayList <EntityCondition>();
					
					conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId),
							EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "DBS_ADMPR_MAKER"),
							EntityUtil.getFilterByDateExpr()
							)
							);
					
					EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					GenericValue loginSecurityGroup = EntityUtil.getFirst( delegator.findList("UserLoginSecurityGroup", mainConditons, null, null, null, false) );
					if (UtilValidate.isEmpty(loginSecurityGroup)) {
						
						loginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup");
						
						loginSecurityGroup.put("userLoginId", userLoginId);
						loginSecurityGroup.put("groupId", "DBS_ADMPR_MAKER");
						loginSecurityGroup.put("fromDate", UtilDateTime.nowTimestamp());
						
						loginSecurityGroup.create();
						successCount++;
					}
					
				}
				
			}
			
			if (UtilValidate.isNotEmpty(rowsSelectedCheker)) {
				
				for (int i = 0; i < rowsSelectedCheker.length; i++) {
					String userLoginId = rowsSelectedCheker[i];
					
					List<EntityCondition> conditions = new ArrayList <EntityCondition>();
					
					conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId),
							EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "DBS_ADMPR_CHEKER"),
							EntityUtil.getFilterByDateExpr()
							)
							);
					
					EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					GenericValue loginSecurityGroup = EntityUtil.getFirst( delegator.findList("UserLoginSecurityGroup", mainConditons, null, null, null, false) );
					if (UtilValidate.isEmpty(loginSecurityGroup)) {
						
						loginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup");
						
						loginSecurityGroup.put("userLoginId", userLoginId);
						loginSecurityGroup.put("groupId", "DBS_ADMPR_CHEKER");
						loginSecurityGroup.put("fromDate", UtilDateTime.nowTimestamp());
						
						loginSecurityGroup.create();
						successCount++;
					}
					
				}
				
			}
			
			resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
			resp.put("successCount", successCount);
			
		} catch (Exception e) {
			
			resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(GlobalConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
	public static String removeUserAuditOperator(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String rowsSelectedMaker[] = request.getParameterValues("rowsSelectedMaker[]");
		String rowsSelectedCheker[] = request.getParameterValues("rowsSelectedCheker[]");

		Map<String, Object> resp = new HashMap<String, Object>();

		int successCount = 0;
		
		try {
			
			if (UtilValidate.isNotEmpty(rowsSelectedMaker)) {
				
				for (int i = 0; i < rowsSelectedMaker.length; i++) {
					String userLoginId = rowsSelectedMaker[i];
					
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
						
						loginSecurityGroup.remove();
						successCount++;
					}
					
				}
				
			}
			
			if (UtilValidate.isNotEmpty(rowsSelectedCheker)) {
				
				for (int i = 0; i < rowsSelectedCheker.length; i++) {
					String userLoginId = rowsSelectedCheker[i];
					
					List<EntityCondition> conditions = new ArrayList <EntityCondition>();
					
					conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId),
							EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "DBS_ADMPR_CHEKER"),
							EntityUtil.getFilterByDateExpr()
							)
							);
					
					EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					GenericValue loginSecurityGroup = EntityUtil.getFirst( delegator.findList("UserLoginSecurityGroup", mainConditons, null, null, null, false) );
					if (UtilValidate.isNotEmpty(loginSecurityGroup)) {
						
						loginSecurityGroup.remove();
						successCount++;
					}
					
				}
				
			}
			
			resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
			resp.put("successCount", successCount);
			
		} catch (Exception e) {
			
			resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(GlobalConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
	public static String prepareAuditValueCompare(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);
		
		String userAuditRequestId = request.getParameter("userAuditRequestId");
		
		Map<String, Object> resp = new HashMap<String, Object>();

		try {
			
			if (UtilValidate.isNotEmpty(userAuditRequestId)) {
				
				List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("userAuditRequestId", EntityOperator.EQUALS, userAuditRequestId)						
						));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue auditRequest = EntityUtil.getFirst( delegator.findList("UserAuditRequest", mainConditons, null, null, null, false) );
				if (UtilValidate.isNotEmpty(auditRequest)) {
					resp.putAll(UtilUserAudit.prepareValueCompare(delegator, auditRequest.getString("oldContextMap"), auditRequest.getString("contextMap"), auditRequest.getString("serviceRequestType")));
				}
				
			}
			
			resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
		} catch (Exception e) {
			
			resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(GlobalConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
}
