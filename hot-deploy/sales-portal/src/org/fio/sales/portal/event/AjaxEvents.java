package org.fio.sales.portal.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.party.party.PartyHelper;
import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.fio.admin.portal.constant.AdminPortalConstant;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class AjaxEvents {
	private AjaxEvents() {
	}

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
			Debug.logWarning(
					"Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(),
					MODULE);
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

	@SuppressWarnings("unchecked")
	public static String getActivityData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String salesOpportunityId = (String) context.get("salesOpportunityId");

		try {
			if (UtilValidate.isNotEmpty(salesOpportunityId)) {
				Map<String, Object> inputMap = new HashMap<String, Object>();
				inputMap.put("salesOpportunityId", salesOpportunityId);
				inputMap.put("userLogin", userLogin);
				Map<String, Object> res = dispatcher.runSync("salesPortal.getActivityData", inputMap);

				if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
					Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
					results = (List<Map<String, Object>>) resultMap.get("result");
				}else{
					String errMsg = "Problem While Fetching Activity Data..";
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					return "error";
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	
	
	@SuppressWarnings("unchecked")
	public static String getActivityDataForOpportunity(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("salesOpportunityId", salesOpportunityId);
			inputMap.put("userLogin", userLogin);
			Map<String, Object> res = dispatcher.runSync("salesPortal.getActivityDataForOpportunity", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Opportunity Related Activity Details..";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	

	@SuppressWarnings("unchecked")
	public static String getRelatedOpportunityData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String statusInputOpen = (String) context.get("statusOpen");
		String statusInputComplete = (String) context.get("statusCompleted");
		String currentDate = (String) context.get("currentDate");
		String numberOfDays = (String) context.get("numberOfDays");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("salesOpportunityId", salesOpportunityId);
			inputMap.put("statusOpen", statusInputOpen);
			inputMap.put("statusCompleted", statusInputComplete);
			inputMap.put("currentDate", currentDate);
			inputMap.put("numberOfDays", numberOfDays);
			inputMap.put("userLogin", userLogin);
			Map<String, Object> res = dispatcher.runSync("salesPortal.getRelatedOpportunityData", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Opportunity Related Opportunities Details..";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		return doJSONResponse(response, results);
	}

	
	@SuppressWarnings("rawtypes")
	public static String getProspect(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		String firstName = request.getParameter("firstName");
		String status = request.getParameter("status");
		String sourceId = request.getParameter("sourceId");
		String prodLineInterest = request.getParameter("prodLineInterest");
		String segment = request.getParameter("segment");
		String createdOn = request.getParameter("createdOn");

		JSONObject obj = null;

		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("firstName", firstName);
			inputMap.put("status", status);
			inputMap.put("sourceId", sourceId);
			inputMap.put("prodLineInterest", prodLineInterest);
			inputMap.put("segment", segment);
			inputMap.put("createdOn", createdOn);
			inputMap.put("userLogin", userLogin);

			Map<String, Object> res = dispatcher.runSync("salesPortal.getProspect", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("salesProspectSummaryMap"))) {
				Map jsonMap = (Map) res.get("salesProspectSummaryMap");
				obj = (JSONObject) jsonMap.get("jsonResult");
			}else{
				String errMsg = "Problem While Fetching Prospect Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return doJSONResponse(response, obj);
	}


	@SuppressWarnings("rawtypes")
	public static String getTeleSales(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String marketingCampaignName = request.getParameter("marketingCampaignName");
		String callOutCome = request.getParameter("callOutCome");
		String totalCallsByCamp = request.getParameter("totalCallsByCamp");
		String callBackDate = request.getParameter("callBackDate");
		String lastContactDays = request.getParameter("lastContactDays");
		String responseType = request.getParameter("responseType");
		String customerCIN = request.getParameter("customerCIN");
		String statusOpen = (String) context.get("statusOpen");
		String statusCallBack = (String) context.get("statusCallBack");
		String statusWon = (String) context.get("statusWon");
		String statusNew = (String) context.get("statusNew");
		String statusLost = (String) context.get("statusLost");
		String statusPending = (String) context.get("statusPending");
		String searchData = request.getParameter("searchData");

		JSONObject obj = null;

		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("marketingCampaignName", marketingCampaignName);
			inputMap.put("callOutCome", callOutCome);
			inputMap.put("totalCallsByCamp", totalCallsByCamp);
			inputMap.put("callBackDate", callBackDate);
			inputMap.put("lastContactDays", lastContactDays);
			inputMap.put("responseType", responseType);
			inputMap.put("customerCIN", customerCIN);
			inputMap.put("statusOpen", statusOpen);
			inputMap.put("statusCallBack", statusCallBack);
			inputMap.put("statusWon", statusWon);
			inputMap.put("statusNew", statusNew);
			inputMap.put("statusLost", statusLost);
			inputMap.put("statusPending", statusPending);
			inputMap.put("searchData", searchData);
			inputMap.put("userLogin", userLogin);

			Map<String, Object> res = dispatcher.runSync("salesPortal.getTeleSales", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("callRecordMasterSummaryMap"))) {
				Map jsonMap = (Map) res.get("callRecordMasterSummaryMap");
				obj = (JSONObject) jsonMap.get("jsonResult");
			}else{
				String errMsg = "Problem While Fetching Tele Sales Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return doJSONResponse(response, obj);
	}
	
	
	@SuppressWarnings("rawtypes")
	public static String getOpportunity(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String salesEmailAddress = (String) context.get("salesEmailAddress");
		String opportunityName = (String) context.get("opportunityName");
		String salesPhone = (String) context.get("salesPhone");
		String statusOpen = (String) context.get("statusOpen");
		String statusClosed = (String) context.get("statusClosed");
		String statusWon = (String) context.get("statusWon");
		String statusNew = (String) context.get("statusNew");
		String statusLost = (String) context.get("statusLost");
		String statusProgress = (String) context.get("statusProgress");
		String statusContact = (String) context.get("statusContact");
		String statusNotContact = (String) context.get("statusNotContact");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		String responseTypeId = (String) context.get("responseTypeId");
		String callOutCome = (String) context.get("callOutCome");
		String salesChannelId = (String) context.get("salesChannelId");

		JSONObject obj = null;

		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("salesOpportunityId", salesOpportunityId);
			inputMap.put("salesEmailAddress", salesEmailAddress);
			inputMap.put("opportunityName", opportunityName);
			inputMap.put("salesPhone", salesPhone);
			inputMap.put("statusOpen", statusOpen);
			inputMap.put("statusClosed", statusClosed);
			inputMap.put("statusWon", statusWon);
			inputMap.put("statusNew", statusNew);
			inputMap.put("statusLost", statusLost);
			inputMap.put("statusProgress", statusProgress);
			inputMap.put("statusContact", statusContact);
			inputMap.put("statusNotContact", statusNotContact);
			inputMap.put("marketingCampaignId", marketingCampaignId);
			inputMap.put("responseTypeId", responseTypeId);
			inputMap.put("callOutCome", callOutCome);
			inputMap.put("salesChannelId", salesChannelId);
			inputMap.put("userLogin", userLogin);

			Map<String, Object> res = dispatcher.runSync("salesPortal.getOpportunity", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("salesOpportunitySummaryMap"))) {
				Map jsonMap = (Map) res.get("salesOpportunitySummaryMap");
				obj = (JSONObject) jsonMap.get("jsonResult");
			}else{
				String errMsg = "Problem While Fetching Sales Opportunity Summary Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return doJSONResponse(response, obj);
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getPersonalizedFields(Delegator delegator, LocalDispatcher dispatcher,String salesOpportunityId, GenericValue userLogin) {
		Map<String, String> data = new HashMap<String, String>();
		try {
			if (UtilValidate.isNotEmpty(delegator) && UtilValidate.isNotEmpty(userLogin)) {
				if (UtilValidate.isNotEmpty(salesOpportunityId)) {
					Map<String, Object> inputMap = new HashMap<String, Object>();
					inputMap.put("salesOpportunityId", salesOpportunityId);
					inputMap.put("userLogin", userLogin);

					Map<String, Object> res = dispatcher.runSync("salesPortal.getPersonalizedFields", inputMap);

					if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
						data = (Map<String, String>) res.get("resultMap");
					}
				}
			}
		} catch (Exception e) {
		}
		return data;
	}

	
	@SuppressWarnings("rawtypes")
	public static String getActivity(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String workEffortId = (String) context.get("workEffortId");
		String primOwnerId = (String) context.get("primOwnerId");
		String createdByUserLogin = (String) context.get("createdByUserLogin");
		String workEffortServiceType = (String) context.get("workEffortServiceType");
		String workEffortSubServiceType = (String) context.get("workEffortSubServiceType");
		String statusopen = (String) context.get("statusopen");
		String statuscompleted = (String) context.get("statuscompleted");
		String currentStatusId = (String) context.get("currentStatusId");

		JSONObject obj = null;

		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("workEffortId", workEffortId);
			inputMap.put("primOwnerId", primOwnerId);
			inputMap.put("createdByUserLogin", createdByUserLogin);
			inputMap.put("workEffortServiceType", workEffortServiceType);
			inputMap.put("workEffortSubServiceType", workEffortSubServiceType);
			inputMap.put("statusopen", statusopen);
			inputMap.put("statuscompleted", statuscompleted);
			inputMap.put("currentStatusId", currentStatusId);
			inputMap.put("userLogin", userLogin);

			Map<String, Object> res = dispatcher.runSync("salesPortal.getActivity", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("workEffortCallSummaryMap"))) {
				Map jsonMap = (Map) res.get("workEffortCallSummaryMap");
				obj = (JSONObject) jsonMap.get("jsonResult");
			}else{
				String errMsg = "Problem While Fetching Prospect Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return doJSONResponse(response, obj);
	}

	
	@SuppressWarnings({ "unchecked", "null" })
	public static String getMyCall(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		
		Map<String, Object> data = null;

		if (UtilValidate.isEmpty(userLogin)) {
			data.put("error", "No user login details found!");
			return doJSONResponse(response, data);
		}
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("userLogin", userLogin);
			Map<String, Object> res = dispatcher.runSync("salesPortal.getMyCall", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("CallRecordMasterSummaryMap"))) {
				data = (Map<String, Object>) res.get("CallRecordMasterSummaryMap");
			}else{
				String errMsg = "Problem While Fetching Prospect Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return doJSONResponse(response, data);
	}
	
	
	@SuppressWarnings("unchecked")
	public static String getDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("salesOpportunityId", salesOpportunityId);
			inputMap.put("userLogin", userLogin);
			
			Map<String, Object> res = dispatcher.runSync("salesPortal.getDetails", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Opportunity Details..";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	

	@SuppressWarnings("unchecked")
	public static String getviewopp(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		String salesOppId = request.getParameter("salesOppId");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("salesOppId", salesOppId);
			inputMap.put("userLogin", userLogin);
			Map<String, Object> res = dispatcher.runSync("salesPortal.getviewopp", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Call Record Details..";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	
	@SuppressWarnings("unchecked")
	public static String getCallDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		String salesOpportunityId = (String)context.get("salesOpportunityId");
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("salesOpportunityId", salesOpportunityId);
			inputMap.put("userLogin", userLogin);
			Map<String, Object> res = dispatcher.runSync("salesPortal.getCallDetails", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Call Record Details..";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	
	
	@SuppressWarnings("unchecked")
	public static String getNotesAttachments(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("salesOpportunityId", salesOpportunityId);
			inputMap.put("userLogin", userLogin);
			Map<String, Object> res = dispatcher.runSync("salesPortal.getNotesAttachments", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Notes And Attachments Details..";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	
	@SuppressWarnings("unchecked")
	public static String getAlertCategoryData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		String alertTypeId = (String) context.get("alertTypeId");
		String alertCategoryId = (String) context.get("alertCategoryId");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("alertTypeId", alertTypeId);
			inputMap.put("alertCategoryId", alertCategoryId);
			inputMap.put("userLogin", userLogin);
			
			Map<String, Object> res = dispatcher.runSync("salesPortal.getAlertCategoryData", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Owner Related Users/Team Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

    		return doJSONResponse(response, e.getMessage());
    	}
    	return doJSONResponse(response, results);
    }
	

	@SuppressWarnings("unchecked")
	public static String getCustomerAlertDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		String alertEntityReferenceId = (String) context.get("alertEntityReferenceId");
		String alertPriority = (String) context.get("alertPriority");
		String alertTrackingId = (String) context.get("alertTrackingId");
		String customerCin = (String) context.get("customerCin");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("alertEntityReferenceId", alertEntityReferenceId);
			inputMap.put("alertPriority", alertPriority);
			inputMap.put("alertTrackingId", alertTrackingId);
			inputMap.put("customerCin", customerCin);
			inputMap.put("userLogin", userLogin);
			Map<String, Object> res = dispatcher.runSync("salesPortal.getCustomerAlertDetails", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Customer Alert Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

    		return doJSONResponse(response, e.getMessage());
    	}
    	return doJSONResponse(response, results);
    }
	
	
	@SuppressWarnings("unchecked")
	public static String getAlertExpiryData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		String unitTypeId = (String) context.get("unitTypeId");
		String alertAutoClosureDuration = (String) context.get("alertAutoClosureDuration");
		String alertStartDate = (String) context.get("alertStartDate");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("unitTypeId", unitTypeId);
			inputMap.put("alertAutoClosureDuration", alertAutoClosureDuration);
			inputMap.put("alertStartDate", alertStartDate);
			inputMap.put("userLogin", userLogin);
			
			Map<String, Object> res = dispatcher.runSync("salesPortal.getAlertExpiryData", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Alert Expiry Date..";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

    		return AjaxEvents.doJSONResponse(response, e.getMessage());
    	}
    	return AjaxEvents.doJSONResponse(response, results);
    }
	
	
	@SuppressWarnings("unchecked")
	public static String getOpportunityCommunicationInfo(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		String partyId = request.getParameter("partyId");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("partyId", partyId);
			inputMap.put("userLogin", userLogin);
			Map<String, Object> res = dispatcher.runSync("salesPortal.getOpportunityCommunicationInfo", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Owner Related Users/Team Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

    		return doJSONResponse(response, e.getMessage());
    	}
    	return doJSONResponse(response, results);
    }
	
	
	@SuppressWarnings("unchecked")
	public static String getActivityCommunicationInfo(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		String workEffortId = (String) context.get("workEffortId");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("workEffortId", workEffortId);
			inputMap.put("userLogin", userLogin);
			Map<String, Object> res = dispatcher.runSync("salesPortal.getActivityCommunicationInfo", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Owner Related Users/Team Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

    		return doJSONResponse(response, e.getMessage());
    	}
    	return doJSONResponse(response, results);
    }
	
	
	@SuppressWarnings("unchecked")
	public static String getProductDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		String prodCatalogId = (String) context.get("prodCatalogId");
		String productCategoryId = (String) context.get("productCategoryId");
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("prodCatalogId", prodCatalogId);
			inputMap.put("productCategoryId", productCategoryId);
			inputMap.put("userLogin", userLogin);
			Map<String, Object> res = dispatcher.runSync("salesPortal.getProductDetails", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Owner Related Users/Team Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	
	
	@SuppressWarnings("unchecked")
	public static String getDataSourceDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		String dataSourceId = request.getParameter("dataSourceId");
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("dataSourceId", dataSourceId);
			inputMap.put("userLogin", userLogin);
			Map<String, Object> res = dispatcher.runSync("salesPortal.getDataSourceDetails", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching Owner Related Users/Team Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	
	
	 @SuppressWarnings("unchecked")
	 public static String getEmplTeam(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		 LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		 GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		 Map<String, Object> context = UtilHttp.getCombinedMap(request);
		 List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

		 String emplTeamId = (String) context.get("emplTeamId");
		 String businessUnitId = (String) context.get("businessUnitId");

		 try {
			 Map<String, Object> inputMap = new HashMap<String, Object>();
			 inputMap.put("emplTeamId", emplTeamId);
			 inputMap.put("businessUnitId", businessUnitId);
			 inputMap.put("userLogin", userLogin);

			 Map<String, Object> res = dispatcher.runSync("salesPortal.getEmplTeam", inputMap);

			 if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				 Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				 results = (List<Map<String, Object>>) resultMap.get("result");
			 }else{
				 String errMsg = "Problem While Fetching Empl Team Details ";
				 request.setAttribute("_ERROR_MESSAGE_", errMsg);
				 return "error";
			 }
		 }catch (Exception e) {
			// e.printStackTrace();
	    		Debug.logError(e.getMessage(), MODULE);

			 return AjaxEvents.doJSONResponse(response, e.getMessage());
		 }
		 return AjaxEvents.doJSONResponse(response, results);
	 }
	 
	 
	 public static String UpdateReasignActivity(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		 LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		 GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		 Map<String, Object> context = UtilHttp.getCombinedMap(request);

		 String primOwnerId = (String) context.get("primOwnerId");
		 String workEffortId = (String) context.get("workEffortId");
		 String emplTeamId = (String) context.get("emplTeamId");

		 try {
			 Map<String, Object> inputMap = new HashMap<String, Object>();
			 inputMap.put("primOwnerId", primOwnerId);
			 inputMap.put("workEffortId", workEffortId);
			 inputMap.put("emplTeamId", emplTeamId);
			 inputMap.put("userLogin", userLogin);

			 Map<String, Object> res = dispatcher.runSync("salesPortal.UpdateReasignActivity", inputMap);

			 if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMessage")) && "Y".equals(res.get("resultMessage"))) {
				 request.setAttribute("_EVENT_MESSAGE_", "Activity ReAssigned Successfully : "+res.get("workEffortId"));
			 }else{
				 String errMsg = "Problem While ReAssigning Activity.";
				 request.setAttribute("_ERROR_MESSAGE_", errMsg);
				 return "error";
			 }
		 } catch (Exception e) {
			 Debug.logInfo("Error-" + e.getMessage(), MODULE);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	
	public static String UpdateReasignForOpportunity(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String ownerUserLoginId = (String) context.get("ownerUserLoginId");
		String salesOppId = (String) context.get("salesOppId");
		String emplTeamId = (String) context.get("emplTeamId");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("ownerUserLoginId", ownerUserLoginId);
			inputMap.put("salesOppId", salesOppId);
			inputMap.put("emplTeamId", emplTeamId);
			inputMap.put("userLogin", userLogin);

			Map<String, Object> res = dispatcher.runSync("salesPortal.UpdateReasignForOpportunity", inputMap);
			
			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMessage")) && "Y".equals(res.get("resultMessage"))) {
				request.setAttribute("_EVENT_MESSAGE_", "Opportunity ReAssigned Successfully : "+res.get("salesOppId"));
			}else{
				String errMsg = "Problem While ReAssigning Opportunity.";
            	request.setAttribute("_ERROR_MESSAGE_", errMsg);
            	return "error";
			}
		} catch (Exception e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
        	return "error";
		}
		return "success";
	}
	
	public static String createCustomerAlert(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String alertCategoryName = (String) context.get("alertCategoryName");
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String alertTypeId = (String) context.get("alertTypeId");
		String alertPriority = (String) context.get("alertPriority");
		String alertStatusId = (String) context.get("alertStatusId");
		String alertAutoClosure = (String) context.get("alertAutoClosure");
		String alertAutoClosureDuration = (String) context.get("alertAutoClosureDuration");
		String remarks = (String) context.get("remarks");
		String alertEntityName = (String) context.get("alertEntityName");
		String alertEntityReferenceId = (String) context.get("alertEntityReferenceId");
		String alertInfo = (String) context.get("alertInfo");
		String alertStartDate = (String) context.get("alertStartDate");
		String alertCategoryId = (String) context.get("alertCategoryId");
		String alertEndDate = (String) context.get("alertEndDate");
		String srNumber = (String) context.get("srNumber");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("alertCategoryName", alertCategoryName);
			inputMap.put("salesOpportunityId", salesOpportunityId);
			inputMap.put("alertTypeId", alertTypeId);
			inputMap.put("alertPriority", alertPriority);
			inputMap.put("alertStatusId", alertStatusId);
			inputMap.put("alertAutoClosure", alertAutoClosure);
			inputMap.put("alertAutoClosureDuration", alertAutoClosureDuration);
			inputMap.put("remarks", remarks);
			inputMap.put("alertEntityName", alertEntityName);
			inputMap.put("alertEntityReferenceId", alertEntityReferenceId);
			inputMap.put("alertInfo", alertInfo);
			inputMap.put("alertStartDate", alertStartDate);
			inputMap.put("alertCategoryId", alertCategoryId);
			inputMap.put("alertEndDate", alertEndDate);
			inputMap.put("srNumber", srNumber);
			inputMap.put("userLogin", userLogin);
			
			Map<String, Object> res = dispatcher.runSync("salesPortal.createCustomerAlert", inputMap);
			
			if (ServiceUtil.isSuccess(res)){
				if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("alertTrackingId"))) {
					request.setAttribute("alertTrackingId", (String) res.get("alertTrackingId"));
				}
				if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("salesOpportunityId"))) {
					request.setAttribute("salesOpportunityId", (String) res.get("salesOpportunityId"));
				}
				if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("srNumber"))) {
					request.setAttribute("srNumber", (String) res.get("srNumber"));
				}
				request.setAttribute("_EVENT_MESSAGE_", "SR Created Successfully");
			}else{
				String errMsg = "Problem While Creating Customer Alert..";
            	request.setAttribute("_ERROR_MESSAGE_", errMsg);
            	return "error";
			}
		} catch (Exception e) {
    		String errMsg = "Customer Alert Creation Failed " + e.toString();
        	request.setAttribute("_ERROR_MESSAGE_", errMsg);
        	return "error";
    	}
		return "success";
    }
	
	public static String createSalesOpportunityDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String opportunityName = (String) context.get("opportunityName");
		String typeEnumId = (String) context.get("typeEnumId");
		BigDecimal estimatedAmount = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(context.get("estimatedAmount"))) {
			 estimatedAmount = new BigDecimal((String)context.get("estimatedAmount"));
		}
		String remarks = (String) context.get("remarks");
		String dataSourceId = (String) context.get("dataSourceId");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		String dataSourceDataId = (String) context.get("dataSourceDataId");
		String productId = (String) context.get("productId");
		String partyId = (String) context.get("partyId");
		String roleTypeId = (String) context.get("roleTypeId");
		String opportunityState = (String) context.get("opportunityState");
		String cNo = (String) context.get("cNo");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("opportunityName", opportunityName);
			inputMap.put("typeEnumId", typeEnumId);
			inputMap.put("estimatedAmount", estimatedAmount);
			inputMap.put("remarks", remarks);
			inputMap.put("dataSourceId", dataSourceId);
			inputMap.put("marketingCampaignId", marketingCampaignId);
			inputMap.put("dataSourceDataId", dataSourceDataId);
			inputMap.put("productId", productId);
			inputMap.put("partyId", partyId);
			inputMap.put("roleTypeId", roleTypeId);
			inputMap.put("opportunityState", opportunityState);
			inputMap.put("cNo", cNo);
			inputMap.put("userLogin", userLogin);
			
			Map<String, Object> res = dispatcher.runSync("salesPortal.createSalesOpportunityDetails", inputMap);
				
			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("salesOpportunityId"))) {
				request.setAttribute("salesOpportunityId", (String) res.get("salesOpportunityId"));
				request.setAttribute("_EVENT_MESSAGE_", "Sales Opportunity Created Successfully");
			}else{
				String errMsg = "Problem While Creating Sales Opportunity..";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
    		String errMsg = "Sales Opportunity Creation Failed " + e.toString();
        	request.setAttribute("_ERROR_MESSAGE_", errMsg);
        	return "error";
    	}
		return "success";
    }
	
	
	public static String updateSalesOpportunityDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String responseTypeId = (String) context.get("responseTypeId");
        String opportunityStatusId = (String) context.get("opportunityStatusId");
        String callOutCome = (String) context.get("callOutcome");
        String responseReasonId = (String) context.get("responseReasonId");
        String salesOpportunityId = (String) context.get("salesOpportunityId");
        String callBackDate = (String) context.get("callBackDate");
        String opportunityStageId = (String) context.get("opportunityStageId");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("responseTypeId", responseTypeId);
			inputMap.put("opportunityStatusId", opportunityStatusId);
			inputMap.put("callOutcome", callOutCome);
			inputMap.put("responseReasonId", responseReasonId);
			inputMap.put("salesOpportunityId", salesOpportunityId);
			inputMap.put("callBackDate", callBackDate);
			inputMap.put("opportunityStageId", opportunityStageId);
			inputMap.put("userLogin", userLogin);
			
			Map<String, Object> res = dispatcher.runSync("salesPortal.updateSalesOpportunityDetails", inputMap);
				
			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("salesOpportunityId"))) {
				request.setAttribute("salesOpportunityId", (String) res.get("salesOpportunityId"));
				request.setAttribute("_EVENT_MESSAGE_", "Sales Opportunity Created Successfully");
			}else{
				String errMsg = "Problem While Creating Sales Opportunity..";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
    		String errMsg = "Sales Opportunity Creation Failed " + e.toString();
        	request.setAttribute("_ERROR_MESSAGE_", errMsg);
        	return "error";
    	}
		return "success";
    }
	
	
	public static String updateServiceActivityDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String workEffortId = (String) context.get("workEffortId");
		String currentStatusId = (String) context.get("currentStatusId");

		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("workEffortId", workEffortId);
			inputMap.put("currentStatusId", currentStatusId);
			inputMap.put("userLogin", userLogin);

			Map<String, Object> res = dispatcher.runSync("salesPortal.updateServiceActivityDetails", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("workEffortId"))) {
				request.setAttribute("_EVENT_MESSAGE_", "Service Activity Updated Successfully for : "+workEffortId);
			}else{
				String errMsg = "Problem While Updating Service Activity..";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
			String errMsg = "Service Activity Updating Failed " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}
	
	
	public static String closedServiceActivityDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String workEffortId = (String) context.get("workEffortId");
		String currentStatusId = (String) context.get("currentStatusId");
		
		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(workEffortId)) {
				inputMap.put("workEffortId", workEffortId);
			}
			if (UtilValidate.isNotEmpty(currentStatusId)) {
				inputMap.put("currentStatusId", currentStatusId);
			}
			inputMap.put("userLogin", userLogin);
			
			Map<String, Object> res = dispatcher.runSync("salesPortal.closedServiceActivityDetails", inputMap);
				
			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("workEffortId"))) {
				request.setAttribute("_EVENT_MESSAGE_", "Activity Closed Successfully for : "+workEffortId);
			}else{
				String errMsg = "Problem While Closing Activity..";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		} catch (Exception e) {
    		String errMsg = "Activity Closing Failed " + e.toString();
        	request.setAttribute("_ERROR_MESSAGE_", errMsg);
        	return "error";
    	}
		return "success";
    }
	
	
	@SuppressWarnings("unchecked")
	public static String getUserOrTeam(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

		String parameterType = (String) context.get("parameterType");
		String salesOpportunityId = (String) context.get("salesOpportunityId");

		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("parameterType", parameterType);
			inputMap.put("salesOpportunityId", salesOpportunityId);
			inputMap.put("userLogin", userLogin);

			Map<String, Object> res = dispatcher.runSync("salesPortal.getUserOrTeam", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching User/Team Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	
	
	@SuppressWarnings("unchecked")
	public static String viewSalesActivityDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

		String workEffortId = (String) context.get("workEffortId");

		try {
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("workEffortId", workEffortId);
			inputMap.put("userLogin", userLogin);

			Map<String, Object> res = dispatcher.runSync("salesPortal.viewSalesActivityDetails", inputMap);

			if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
				Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
				results = (List<Map<String, Object>>) resultMap.get("result");
			}else{
				String errMsg = "Problem While Fetching User/Team Details ";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return "error";
			}
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	
	
}	