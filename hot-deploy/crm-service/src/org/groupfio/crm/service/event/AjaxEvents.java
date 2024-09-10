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

package org.groupfio.crm.service.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections4.map.LinkedMap;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.crm.service.CrmServiceConstants;
import org.groupfio.crm.service.CrmServiceConstants.CustomerPricingPrefConstats;
import org.groupfio.crm.service.constants.MakerCheckerConstants;
import org.groupfio.crm.service.util.DataHelper;
import org.groupfio.crm.service.util.DataUtil;
import org.groupfio.crm.service.util.MakerCheckerUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * Utility class for making Ajax JSON responses.
 * @author Sharif Ul Islam
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
	public static String getGeoAssocList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String geoId = request.getParameter("geoId");
		String geoAssocTypeId = request.getParameter("geoAssocTypeId");
		Timestamp statusDate = UtilDateTime.nowTimestamp();

		Map<String, Object> resp = new HashMap<String, Object>();

		try {
			
			if (UtilValidate.isNotEmpty(geoId) && UtilValidate.isNotEmpty(geoAssocTypeId)) {
				
				List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
				
				List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("geoId", EntityOperator.EQUALS, geoId),
						EntityCondition.makeCondition("geoAssocTypeId", EntityOperator.EQUALS, geoAssocTypeId)));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> geoAssocList = delegator.findList("GeoAssocSummary", mainConditons, null, null, null, false);
				if (UtilValidate.isNotEmpty(geoAssocList)) {
					for (GenericValue geoAssoc : geoAssocList) {
						
						Map<String, Object> result = new HashMap<String, Object>();
						
						result.put("geoId", geoAssoc.getString("geoId"));
						result.put("geoIdTo", geoAssoc.getString("geoIdTo"));
						result.put("geoName", geoAssoc.getString("geoName"));
						result.put("geoAssocTypeId", geoAssoc.getString("geoAssocTypeId"));
						
						results.add(result);
					}
				}
				
				resp.put("results", results);

				resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				
			} else {
				resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
	            resp.put(GlobalConstants.RESPONSE_MESSAGE, "Provide required parameters as geoId, geoAssocTypeId..");
			}
			
		} catch (Exception e) {
			
			resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(GlobalConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
    //getCurrencyList
    public static String getCurrencyList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String benchMark = request.getParameter("benchMark");
		String profileTypeId = request.getParameter("profileTypeId");
		String country = request.getParameter("country");
		Timestamp statusDate = UtilDateTime.nowTimestamp();
		Debug.log("==benchMark==="+benchMark);

		Map<String, Object> resp = new HashMap<String, Object>();

		try {
			
			if (UtilValidate.isNotEmpty(benchMark)) {
				
				List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
				
				List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("parentId", EntityOperator.EQUALS, benchMark)));
				conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("profileTypeId", EntityOperator.EQUALS, profileTypeId)));
				if(UtilValidate.isNotEmpty(country)) {
					conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("country", EntityOperator.EQUALS, country)));
				}
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> currencyList = delegator.findList("ProfileConfiguration", mainConditons, null, null, null, false);
				Debug.log("==currencyList==="+currencyList);
				if (UtilValidate.isNotEmpty(currencyList)) {
					for (GenericValue currency : currencyList) {
						Map<String, Object> result = new HashMap<String, Object>();
						result.put("profileCode", currency.getString("profileCode"));
						String profileDescription = currency.getString("profileCode") + "-" + currency.getString("profileDescription");
						if (UtilValidate.isNotEmpty(profileDescription) && profileDescription.length() > 25) {
							profileDescription = profileDescription.substring(0, 25) + "..";
						}
						result.put("profileDescription", profileDescription);
						results.add(result);
					}
				}
				Debug.log("==results==="+results);
				resp.put("results", results);

				resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				
			} else {
				resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			}
			
		} catch (Exception e) {
			
			resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(GlobalConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
    public static String processCheckerRequest(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
    	Map<String, Object> resp = new HashMap<String, Object>();
    	
    	Locale locale = UtilHttp.getLocale(request);
    	HttpSession session = request.getSession(true);

    	String approveType = request.getParameter("approveType");
    	String serviceRequestId = request.getParameter("serviceRequestId");
    	String partyCheckerId = userLogin.getString("userLoginId");
    	String remarks = request.getParameter("remarks");
    	java.util.TimeZone timeZone = java.util.TimeZone.getDefault();
    	JSONArray datas = new JSONArray();
    	try {

    		if (UtilValidate.isNotEmpty(approveType) && UtilValidate.isNotEmpty(serviceRequestId)) {

    			List<Map<String, Object>> securityGroupPermissionList = new LinkedList<Map<String, Object>>();
    			Map<String, Object> securityGroupPermissionMp = new HashMap<String, Object>();
    			GenericValue userAuditRequest = EntityQuery.use(delegator).from("UserAuditRequest").where("serviceRequestId", serviceRequestId).queryOne();
    			String partyMakerId = userAuditRequest.getString("partyMakerId");
    			String modeOfAction = userAuditRequest.getString("modeOfAction");
    			String requestUri = userAuditRequest.getString("requestUri");
    			if(UtilValidate.isNotEmpty(userAuditRequest)) {
    				
    				userAuditRequest.put("statusId", approveType);
    				userAuditRequest.put("partyCheckerId", partyCheckerId);
    				userAuditRequest.put("statusDate", UtilDateTime.nowTimestamp());
    				userAuditRequest.put("modifiedDate", UtilDateTime.nowTimestamp());
    				userAuditRequest.put("remarks", remarks);
    			}
    			String serviceRequestType = userAuditRequest.getString("serviceRequestType");
				String gsonStringForContext = userAuditRequest.getString("contextMap");
				
    			GenericValue auditConfig = EntityQuery.use(delegator).from("AuditEntityConfig").where("configId", serviceRequestType).queryOne();
				String serviceName = auditConfig.getString("serviceName");
				String entityAuditExtName = auditConfig.getString("entityAuditExtName");
				JSONObject contextJson = JSONObject.fromObject(gsonStringForContext);
				Map jsonToMap = MakerCheckerUtil.jsonToMap(contextJson);
				if (jsonToMap.containsKey("timeZone")) {
					jsonToMap.remove("timeZone");
				}
				
    			if (MakerCheckerConstants.ApprovalStatus.APPROVED.equals(approveType)) {
    				    				
    				//convert string to map -- use the existing json object method
    				
    				//call the utility method that convert the json object to map
    				
    				jsonToMap.put("auditMakerChecker", "N");
    				Map<String, Object> storeDataToService;
        			try {
        				
        				if ("CREATE_BASE_RATE".equals(serviceRequestType) || "UPDATE_BASE_RATE".equals(serviceRequestType)) {
        					
        					List<GenericValue> auditTireList = delegator.findByAnd("FtpRateConsolidateTierEntityAuditExt", UtilMisc.toMap("serviceRequestId", serviceRequestId), null, false);
        					for (GenericValue auditTire : auditTireList) {
        						
        						GenericValue consolidate = delegator.makeValue("FtpRateConsolidate");
        						
        						auditTire.remove("serviceRequestId");
        						auditTire.remove("recordSequence");
        						
        						consolidate.putAll(auditTire.getAllFields());
        						
        						if (UtilValidate.isEmpty(consolidate.getString("ftpRateConsolidateId"))) {
        							consolidate.put("ftpRateConsolidateId", delegator.getNextSeqId("FtpRateConsolidate"));
                				}
        						
        						delegator.createOrStore(consolidate);
        						
        					}
        					
        				} else if ("CREATE_PRODUCT_PREF".equals(serviceRequestType)) {
        					
        					Map<String, Object> reqContext = new HashMap<String, Object>();
        					
        					reqContext.putAll(jsonToMap);
        					reqContext.put("userLogin", userLogin);
        					
        					reqContext.remove("currency");
        					
        					GenericValue customerProductPref = EntityUtil.getFirst( delegator.findByAnd("CustomerProductPreferenceEntityAuditExt", UtilMisc.toMap("serviceRequestId", serviceRequestId), null, false) );
        					if (UtilValidate.isNotEmpty(customerProductPref) && UtilValidate.isNotEmpty(customerProductPref.get("effectiveDate"))) {
        						reqContext.put("effectiveDate", UtilDateTime.timeStampToString(customerProductPref.getTimestamp("effectiveDate"), "dd-MM-yyyy", timeZone, locale) );
        					}
        					
        					GenericValue fixedProductPref = EntityUtil.getFirst( delegator.findByAnd("ProductRatePreferenceEntityAuditExt", UtilMisc.toMap("serviceRequestId", serviceRequestId, "preferenceRateType", "FIXED_RATE"), null, false) );
        					if (UtilValidate.isNotEmpty(fixedProductPref)) {
        						reqContext.put("fixedIntRateType", fixedProductPref.get("intRateType"));
        						reqContext.put("fixedSpread", fixedProductPref.get("spread"));
        						reqContext.put("fixedSpreadType", fixedProductPref.get("spreadType"));
        					}
        					
        					GenericValue floatProductPref = EntityUtil.getFirst( delegator.findByAnd("ProductRatePreferenceEntityAuditExt", UtilMisc.toMap("serviceRequestId", serviceRequestId, "preferenceRateType", "FLOAT_RATE"), null, false) );
        					if (UtilValidate.isNotEmpty(fixedProductPref)) {
        						reqContext.put("floatIntRateType", floatProductPref.get("intRateType"));
        						reqContext.put("floatSpread", floatProductPref.get("spread"));
        						reqContext.put("floatSpreadType", floatProductPref.get("spreadType"));
        					}
        					
        					Map<String, Object> reqResult = dispatcher.runSync("productPrefer.create", reqContext);
        					if (ServiceUtil.isSuccess(reqResult)) {
        						
        					}
        					
        				} else if ("UPDATE_PRODUCT_PREF".equals(serviceRequestType)) {
        					
        					Map<String, Object> reqContext = new HashMap<String, Object>();
        					
        					reqContext.putAll(jsonToMap);
        					reqContext.put("userLogin", userLogin);
        					
        					reqContext.remove("productPreferenceId");
        					reqContext.remove("currency");
        					
        					GenericValue customerProductPref = EntityUtil.getFirst( delegator.findByAnd("CustomerProductPreferenceEntityAuditExt", UtilMisc.toMap("serviceRequestId", serviceRequestId), null, false) );
        					if (UtilValidate.isNotEmpty(customerProductPref)) {
        						
        						if (UtilValidate.isNotEmpty(customerProductPref.get("effectiveDate"))) {
        							reqContext.put("effectiveDate", UtilDateTime.timeStampToString(customerProductPref.getTimestamp("effectiveDate"), "dd-MM-yyyy", timeZone, locale) );
        						}
        						
        						reqContext.put("proPrefId", customerProductPref.get("productPreferenceId"));
        					}
        					
        					GenericValue fixedProductPref = EntityUtil.getFirst( delegator.findByAnd("ProductRatePreferenceEntityAuditExt", UtilMisc.toMap("serviceRequestId", serviceRequestId, "preferenceRateType", "FIXED_RATE"), null, false) );
        					if (UtilValidate.isNotEmpty(fixedProductPref)) {
        						reqContext.put("fixedIntRateType", fixedProductPref.get("intRateType"));
        						reqContext.put("fixedSpread", fixedProductPref.get("spread"));
        						reqContext.put("fixedSpreadType", fixedProductPref.get("spreadType"));
        					}
        					
        					GenericValue floatProductPref = EntityUtil.getFirst( delegator.findByAnd("ProductRatePreferenceEntityAuditExt", UtilMisc.toMap("serviceRequestId", serviceRequestId, "preferenceRateType", "FLOAT_RATE"), null, false) );
        					if (UtilValidate.isNotEmpty(fixedProductPref)) {
        						reqContext.put("floatIntRateType", floatProductPref.get("intRateType"));
        						reqContext.put("floatSpread", floatProductPref.get("spread"));
        						reqContext.put("floatSpreadType", floatProductPref.get("spreadType"));
        					}
        					
        					Map<String, Object> reqResult = dispatcher.runSync("productPrefer.update", reqContext);
        					if (ServiceUtil.isSuccess(reqResult)) {
        						
        					}
        					
        				} else if ("CREATE_CUSTOMER_PREF".equals(serviceRequestType) || "UPDATE_CUSTOMER_PREF".equals(serviceRequestType)) {
        					
        					Map<String, Object> reqContext = new HashMap<String, Object>();
        					
        					reqContext.putAll(jsonToMap);
        					reqContext.put("userLogin", userLogin);
        					
        					GenericValue customerProductPref = EntityUtil.getFirst( delegator.findByAnd("CustomerPricingPreferenceEntityAuditExt", UtilMisc.toMap("serviceRequestId", serviceRequestId), null, false) );
        					if (UtilValidate.isNotEmpty(customerProductPref)) {
        						//reqContext.put("effectiveDate", UtilDateTime.timeStampToString(customerProductPref.getTimestamp("effectiveDate"), "dd-MM-yyyy", timeZone, locale) );
        					}
        					
        					Map<String, Object> reqResult = dispatcher.runSync("ratePortal.createCustomerPricingPreference", reqContext);
        					if (ServiceUtil.isSuccess(reqResult)) {
        						
        					}
        					
        				}
        				
        			} catch (Exception e) {
        				return "error";
        			}
        			
    				List<GenericValue> userEntityAuditList = EntityQuery.use(delegator).from("UserEntityAudit")
    						.where("serviceRequestId", serviceRequestId).queryList();
    				if (UtilValidate.isNotEmpty(userEntityAuditList)) {
    					for(GenericValue userEntityAudit : userEntityAuditList) {
    						userEntityAudit.put("modifiedDate", UtilDateTime.nowTimestamp());
    						userEntityAudit.put("userLoginIdChecker", partyCheckerId );
    						userEntityAudit.put("approvalStatus", approveType);
    						userEntityAudit.put("aprovedDate", UtilDateTime.nowTimestamp());
    						userEntityAudit.put("remarks", remarks);
    						userEntityAudit.store();
    					}
    				}
    				userAuditRequest.store();
    			} else if (MakerCheckerConstants.ApprovalStatus.REJECTED.equals(approveType)) {
    				
    				Map toProcessContextMap = new HashMap<>();
    				List<GenericValue> userEntityAuditList = EntityQuery.use(delegator).from("UserEntityAudit")
    						.where("serviceRequestId", serviceRequestId).queryList();
    				
    				if (UtilValidate.isNotEmpty(userEntityAuditList)) {
    					for(GenericValue userEntityAudit : userEntityAuditList) {
    						userEntityAudit.put("modifiedDate", UtilDateTime.nowTimestamp());
    						userEntityAudit.put("userLoginIdChecker", partyCheckerId );
    						userEntityAudit.put("approvalStatus", approveType);
    						userEntityAudit.put("aprovedDate", UtilDateTime.nowTimestamp());
    						userEntityAudit.put("remarks", remarks);
    						userEntityAudit.store();
    					}
    				}
    				
    				userAuditRequest.put("statusId" ,MakerCheckerConstants.ApprovalStatus.EXPIRED);
    				userAuditRequest.store();
    				
    			} else if (MakerCheckerConstants.ApprovalStatus.IGNORED.equals(approveType)) {
    				
    				List<GenericValue> userEntityAuditList = EntityQuery.use(delegator).from("UserEntityAudit")
    						.where("serviceRequestId", serviceRequestId).queryList();
    				if (UtilValidate.isNotEmpty(userEntityAuditList)) {
    					for(GenericValue userEntityAudit : userEntityAuditList) {
    						userEntityAudit.put("modifiedDate", UtilDateTime.nowTimestamp());
    						userEntityAudit.put("userLoginIdChecker", partyCheckerId );
    						userEntityAudit.put("approvalStatus", approveType);
    						userEntityAudit.put("aprovedDate", UtilDateTime.nowTimestamp());
    						userEntityAudit.put("expiredDate", UtilDateTime.nowTimestamp());
    						userEntityAudit.put("remarks", remarks);
    						userEntityAudit.store();
    					}
    				}
    				
    				userAuditRequest.store();
    				
    			}
    			
    		}

    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    	}

    	resp.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
        resp.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully Process Checker Request..");

    	return doJSONResponse(response, resp);
    }
    
}
