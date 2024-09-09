package org.fio.admin.portal.event;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.admin.portal.constant.AdminPortalConstant.AlertCategoryConstant;
import org.fio.admin.portal.constant.AdminPortalConstant.BusinessUnitConstant;
import org.fio.admin.portal.constant.AdminPortalConstant.CustRequestAssocConstants;
import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.admin.portal.constant.AdminPortalConstant.EventReturn;
import org.fio.admin.portal.constant.AdminPortalConstant.ParamUnitConstant;
import org.fio.admin.portal.constant.ResponseConstants;
import org.fio.admin.portal.util.DataHelper;
import org.fio.admin.portal.util.DataUtil;
import org.fio.admin.portal.util.ResAvailUtil;
import org.fio.admin.portal.util.SqlUtil;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.export.ExporterFacade;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.QueryUtil;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelFieldTypeReader;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.entity.model.ModelUtil;
import org.ofbiz.entity.util.Converters;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author Mahendran
 * @since 26-Jul-2019
 *
 */

public class AjaxEvents {

	private static final String MODULE = AjaxEvents.class.getName();
	private static final String RESOURCE = "AdminPortalUiLabels";
	private static final String SUCCESS = "success";
	private static final String ERROR = "error";
	public static final String SUCCESS_MESSAGE = "successMessage";
	private AjaxEvents() {}

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

	public static String getVisitorLoginHistory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String visitId = (String) context.get("visitId");
		String userId = (String) context.get("userId");
		String module = (String) context.get("module");
		String clientIpAddress = (String) context.get("clientIpAddress");
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		try {
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(visitId)) {
				conditions.add(EntityCondition.makeCondition("visitId", EntityOperator.EQUALS, visitId));
			}
			if (UtilValidate.isNotEmpty(userId)) {
				conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userId));
			}
			if (UtilValidate.isNotEmpty(module)) {
				conditions.add(EntityCondition.makeCondition("webappName", EntityOperator.EQUALS, module));
			}
			if (UtilValidate.isNotEmpty(clientIpAddress)) {
				conditions.add(EntityCondition.makeCondition("clientIpAddress", EntityOperator.EQUALS, clientIpAddress));
			}
			if (UtilValidate.isNotEmpty(fromDate)) {
				fromDate = df1.format(df2.parse(fromDate));
				conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(fromDate))));
			}
			if (UtilValidate.isNotEmpty(toDate)) {
				toDate = df1.format(df2.parse(toDate));
				conditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(Timestamp.valueOf(toDate))));
			}
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("visitId");
			fieldsToSelect.add("partyId");
			fieldsToSelect.add("userLoginId");
			fieldsToSelect.add("webappName");
			fieldsToSelect.add("clientIpAddress");
			fieldsToSelect.add("fromDate");
			fieldsToSelect.add("thruDate");
			fieldsToSelect.add("lastUpdatedTxStamp");
			fieldsToSelect.add("createdTxStamp");
			EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < GenericValue > visitorLoginHistories = EntityQuery.use(delegator).select(fieldsToSelect).from("Visit").where(mainCondition).maxRows(100).orderBy("-lastUpdatedTxStamp").queryList();
			if (visitorLoginHistories != null && visitorLoginHistories.size() > 0) {
				for (GenericValue visitor: visitorLoginHistories) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("visitId", visitor.getString("visitId"));
					data.put("partyId", visitor.getString("partyId"));
					data.put("userLoginId", visitor.getString("userLoginId"));
					data.put("webappName", visitor.getString("webappName"));
					data.put("clientIpAddress", visitor.getString("clientIpAddress"));
					String fromDate1 = visitor.getString("fromDate");
					if (UtilValidate.isNotEmpty(fromDate1))
						fromDate1 = DataUtil.convertDateTimestamp(fromDate1, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					data.put("fromDate", fromDate1);
					String thruDate = visitor.getString("thruDate");
					if (UtilValidate.isNotEmpty(thruDate))
						thruDate = DataUtil.convertDateTimestamp(thruDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					data.put("thruDate", thruDate);
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getUserVisitServerHit(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		String visitId = request.getParameter("visitId");
		try {
			if (UtilValidate.isNotEmpty(visitId)) {
				Set < String > fieldsToSelect = new TreeSet < String > ();
				fieldsToSelect.add("visitId");
				fieldsToSelect.add("contentId");
				fieldsToSelect.add("hitStartDateTime");
				fieldsToSelect.add("hitTypeId");
				fieldsToSelect.add("numOfBytes");
				fieldsToSelect.add("requestUrl");
				fieldsToSelect.add("lastUpdatedTxStamp");
				fieldsToSelect.add("createdTxStamp");
				List < GenericValue > serverHits = EntityQuery.use(delegator).select(fieldsToSelect).from("ServerHit").maxRows(100).orderBy("-lastUpdatedTxStamp").queryList();
				if (serverHits != null && serverHits.size() > 0) {
					for (GenericValue serverHit: serverHits) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("visitId", serverHit.getString("visitId"));
						data.put("contentId", serverHit.getString("contentId"));
						data.put("startDate", serverHit.getString("hitStartDateTime"));
						data.put("type", serverHit.getString("hitTypeId"));
						data.put("size", serverHit.getString("numOfBytes"));
						data.put("url", serverHit.getString("requestUrl"));
						results.add(data);
					}
				}
			} else {
				request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(RESOURCE, "VisitIdNotFound", locale));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	//Author : Arshiya S, Description : Getting List external API logs
	public static String getApiLogs(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		//Set<String> addStatues = new HashSet<String>();
		/*SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:MM");
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");*/

		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String ofbizApiLogId = (String) context.get("logId");
		String serviceName = (String) context.get("serviceName");
		String systemName = (String) context.get("systemName");
		String channelId = (String) context.get("channelId");
		String responseCode = (String) context.get("responseCode");
		String lastUpdatedTxStampFromDate = (String) context.get("lastUpdatedTxStampFrom_date");
		String lastUpdatedTxStampFromTime = (String) context.get("lastUpdatedTxStampFrom_time");
		String lastUpdatedTxStampToDate = (String) context.get("lastUpdatedTxStampTo_date");
		String lastUpdatedTxStampToTime = (String) context.get("lastUpdatedTxStampTo_time");
		Timestamp lastUpdateTxStampFromDateTime= null;
		Timestamp lastUpdateTxStampToDateTime= null;

		List < EntityCondition > conditionlist = FastList.newInstance();
		if (UtilValidate.isNotEmpty(ofbizApiLogId)) {
			//conditionlist.add(EntityCondition.makeCondition("ofbizApiLogId", EntityOperator.EQUALS, ofbizApiLogId));
			conditionlist.add(EntityCondition.makeCondition("ofbizApiLogId", EntityOperator.LIKE,"%"+ofbizApiLogId + "%"));
		}
		if (UtilValidate.isNotEmpty(serviceName)) {
			conditionlist.add(EntityCondition.makeCondition("serviceName", EntityOperator.EQUALS, serviceName));
			//conditionlist.add(EntityCondition.makeCondition("serviceName", EntityOperator.LIKE,"%"+serviceName + "%"));
		}
		if (UtilValidate.isNotEmpty(systemName)) {
			conditionlist.add(EntityCondition.makeCondition("systemName", EntityOperator.EQUALS, systemName));
		}
		if (UtilValidate.isNotEmpty(responseCode)) {
			conditionlist.add(EntityCondition.makeCondition("responseCode", EntityOperator.EQUALS, responseCode));
		}
		if (UtilValidate.isNotEmpty(channelId)) {
			conditionlist.add(EntityCondition.makeCondition("channelId", EntityOperator.EQUALS, channelId));
		}
		if (UtilValidate.isNotEmpty(lastUpdatedTxStampFromDate)) {
			if(UtilValidate.isNotEmpty(lastUpdatedTxStampFromTime)) {
				lastUpdateTxStampFromDateTime = ParamUtil.getTimestamp(lastUpdatedTxStampFromDate, lastUpdatedTxStampFromTime,globalDateTimeFormat);
				conditionlist.add(EntityCondition.makeCondition("lastUpdatedTxStamp", EntityOperator.GREATER_THAN_EQUAL_TO,lastUpdateTxStampFromDateTime));
			}else {
				lastUpdateTxStampFromDateTime = UtilDateTime.stringToTimeStamp(lastUpdatedTxStampFromDate, globalDateFormat, TimeZone.getDefault(),Locale.getDefault());
				conditionlist.add(EntityCondition.makeCondition("lastUpdatedTxStamp", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(lastUpdateTxStampFromDateTime)));
			}
		}
		if (UtilValidate.isNotEmpty(lastUpdatedTxStampToDate)) {
			if(UtilValidate.isNotEmpty(lastUpdatedTxStampToTime)) {
				lastUpdateTxStampToDateTime = ParamUtil.getTimestamp(lastUpdatedTxStampToDate, lastUpdatedTxStampToTime,globalDateTimeFormat);
				conditionlist.add(EntityCondition.makeCondition("lastUpdatedTxStamp", EntityOperator.LESS_THAN_EQUAL_TO,lastUpdateTxStampToDateTime));
			}else {
				lastUpdateTxStampToDateTime = UtilDateTime.stringToTimeStamp(lastUpdatedTxStampToDate, globalDateFormat, TimeZone.getDefault(),Locale.getDefault());
				conditionlist.add(EntityCondition.makeCondition("lastUpdatedTxStamp", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(lastUpdateTxStampToDateTime)));
			}
		}
		EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);

		try {
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("ofbizApiLogId");
			fieldsToSelect.add("requestedData");
			fieldsToSelect.add("responsedData");
			fieldsToSelect.add("clientApiLogId");
			fieldsToSelect.add("serviceName");
			fieldsToSelect.add("systemName");
			fieldsToSelect.add("orgId");
			fieldsToSelect.add("msgId");
			fieldsToSelect.add("requestedTime");
			fieldsToSelect.add("responsedTime");
			fieldsToSelect.add("responseStatus");
			fieldsToSelect.add("responseCode");
			EntityFindOptions efo = new EntityFindOptions();
			efo.setOffset(0);
			efo.setLimit(1000);
				List < GenericValue > ofbizApiLogs = EntityQuery.use(delegator).select(fieldsToSelect).from("OfbizApiLog").where(condition).maxRows(1000).orderBy("-lastUpdatedTxStamp").queryList();
				if (ofbizApiLogs != null && ofbizApiLogs.size() > 0) {
					for (GenericValue apiLog: ofbizApiLogs) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("ofbizApiLogId", apiLog.getString("ofbizApiLogId"));
						data.put("requestJson", ResponseConstants.Json);// apiLog.getString("requestedData")
						data.put("responseJson", ResponseConstants.Json);//apiLog.getString("responsedData")
						data.put("channelId", apiLog.getString("channelId"));
						data.put("serviceName", apiLog.getString("serviceName"));
						data.put("systemName", apiLog.getString("systemName"));
						data.put("orgId", apiLog.getString("orgId"));
						data.put("msgId", apiLog.getString("msgId"));
						data.put("responseCode", apiLog.getString("responseCode"));
						String requestedTime = null;
						if(UtilValidate.isNotEmpty(apiLog.getString("requestedTime"))) {
							requestedTime = DataUtil.convertDateTimestamp(apiLog.getString("requestedTime"), new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						String responsedTime = null;
						if(UtilValidate.isNotEmpty(apiLog.getString("responsedTime"))) {
							responsedTime = DataUtil.convertDateTimestamp(apiLog.getString("responsedTime"), new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("requestedTime", requestedTime);
						data.put("responsedTime", responsedTime);
						//addStatues.add(apiLog.getString("responseStatus"));
						data.put("status", apiLog.getString("responseStatus"));
						results.add(data);
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	//Author : Arshiya S, Description : Getting List external API logs
	public static String getJsonData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String ofbizApiLogId = request.getParameter("logId");
		String type = request.getParameter("type");
		Map<String, Object> resp = new HashMap<String, Object>();
		String json = null;
		try {
			GenericValue jsonData = EntityQuery.use(delegator).from("OfbizApiLog").where("ofbizApiLogId", ofbizApiLogId).queryOne();
			if (UtilValidate.isNotEmpty(jsonData)) {
				if (ResponseConstants.REQUEST.equals(type)) {
					json = jsonData.getString("requestedData");
				} else if (ResponseConstants.RESPONSE.equals(type)) {
					json = jsonData.getString("responsedData");
				}
			}
			if (UtilValidate.isNotEmpty(json)) {
				resp.put("json", json);
			}
			//Debug.log("results====" + resp);
			resp.put(ResponseConstants.RESPONSE_CODE, ResponseConstants.SUCCESS_CODE);
		} catch (Exception e) {
			Debug.logError("Exception in getJsonData " + e.getMessage(), MODULE);
			resp.put(ResponseConstants.RESPONSE_CODE, ResponseConstants.INTERNAL_SERVER_ERROR_CODE);
			return doJSONResponse(response, resp);
		}
		return doJSONResponse(response, resp);
	}
	//Author : Arshiya S, Description : Getting List external API logs
	public static String getAccessSetup(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		//Set<String> addStatues = new HashSet<String>();
		/*SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:MM");*/
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String systemName = (String) context.get("systemName");
		String authMethod = (String) context.get("authMethod");
		String status = (String) context.get("status");
		List < EntityCondition > conditionlist = FastList.newInstance();
		if (UtilValidate.isNotEmpty(systemName)) {
			conditionlist.add(EntityCondition.makeCondition("applicationName", EntityOperator.EQUALS, systemName));
		}
		if (UtilValidate.isNotEmpty(authMethod)) {
			conditionlist.add(EntityCondition.makeCondition("channelAccessType", EntityOperator.EQUALS, authMethod));
		}
		/*if (UtilValidate.isNotEmpty(status)) {
            conditionlist.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, status));
        }*/
		EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
		try {
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("applicationName");
			fieldsToSelect.add("channelAccessUrl");
			fieldsToSelect.add("clientName");
			fieldsToSelect.add("password");
			fieldsToSelect.add("channelAccessType");
			fieldsToSelect.add("description");
			fieldsToSelect.add("lastUpdatedTxStamp");
			//fieldsToSelect.add("status");
			List < GenericValue > channelAccesses = EntityQuery.use(delegator).select(fieldsToSelect).from("ChannelAccess").where(condition).maxRows(100).orderBy("-lastUpdatedTxStamp").queryList();
			if (channelAccesses != null && channelAccesses.size() > 0) {
				for (GenericValue channelAccess: channelAccesses) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("systemName", channelAccess.getString("applicationName"));
					data.put("urlAccess", channelAccess.getString("channelAccessUrl"));
					data.put("userId", channelAccess.getString("clientName"));
					data.put("password", channelAccess.getString("password"));
					data.put("authMethod", channelAccess.getString("channelAccessType"));
					data.put("description", channelAccess.getString("description"));
					String lastModified = null;
					if(UtilValidate.isNotEmpty(channelAccess.getString("lastUpdatedTxStamp"))) {
						lastModified = DataUtil.convertDateTimestamp(channelAccess.getString("lastUpdatedTxStamp"), new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					}
					data.put("lastModified", lastModified);
					//addStatues.add(apiLog.getString("responseStatus"));
					data.put("status", "");
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getUsers(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String userId = (String) context.get("userId");
		String businessUnit = (String) context.get("businessUnit");
		String status = (String) context.get("status");
		String roleTypeId = (String) context.get("roleTypeId");
		String emplTeamId = (String) context.get("emplTeamId");
		String fName = (String) context.get("firstName");
		String lName = (String) context.get("lastName");
		String searchText = (String) context.get("searchText");
		String partyId = (String) context.get("partyId");
		
		List<String> roleTypeIds = new ArrayList<>();
		if (UtilValidate.isNotEmpty(roleTypeId)) {
			if(roleTypeId.contains(",")) {
				roleTypeIds = org.fio.admin.portal.util.DataUtil.stringToList(roleTypeId, ",");
			} else {
				roleTypeIds.add(roleTypeId);
			}
		}
		try {
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(userId)){
				conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userId));
			}
			if (UtilValidate.isNotEmpty(businessUnit)){
				conditions.add(EntityCondition.makeCondition("businessUnit", EntityOperator.EQUALS, businessUnit));
			}
			if (UtilValidate.isNotEmpty(status)){
				conditions.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, status));
			}
			if (UtilValidate.isNotEmpty(fName)){
				conditions.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE, "%"+fName+"%"));
			}
			if (UtilValidate.isNotEmpty(lName)){
				conditions.add(EntityCondition.makeCondition("lastName", EntityOperator.LIKE, "%"+lName+"%"));
			}
			if (UtilValidate.isNotEmpty(searchText)){

				List<String> names = Arrays.asList(searchText.split(" "));
				String firstName = names.get(0);
				String middleName = names.size() >= 3 ? names.get(1) : null;
				String lastName = null;
				if (names.size() == 2) {
					// middleName = names.get(1);
					lastName = names.get(1);
				} else if (names.size() >= 3){
					lastName = StringUtil.join(names.subList(2, names.size() - 1), " ");
				}
				List entityConditionList = FastList.newInstance();
				if (UtilValidate.isNotEmpty(firstName)) {
					entityConditionList.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE, firstName + "%"));
				}
				if (UtilValidate.isNotEmpty(middleName)) {
					entityConditionList.add(EntityCondition.makeCondition("middleName", EntityOperator.LIKE, middleName + "%"));
				}
				if (UtilValidate.isNotEmpty(lastName)) {
					entityConditionList.add(EntityCondition.makeCondition("lastName", EntityOperator.LIKE, lastName + "%"));
				}
				EntityCondition nameCondition = EntityCondition.makeCondition(EntityCondition.makeCondition(entityConditionList, EntityOperator.AND));
				conditions.add(nameCondition);
			}
			if (UtilValidate.isNotEmpty(partyId)) {
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
			}
			List<String> skipRoleTypeIds = new ArrayList<>();
			String skipRoleTypeId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SKIP_ROLE_TYPE_ID");
			if(UtilValidate.isNotEmpty(skipRoleTypeId) && skipRoleTypeId.contains(",")) {
				skipRoleTypeIds = org.fio.admin.portal.util.DataUtil.stringToList(skipRoleTypeId, ",");
			} else if(UtilValidate.isNotEmpty(skipRoleTypeId)) {
				skipRoleTypeIds.add(skipRoleTypeId);
			}
			if(UtilValidate.isNotEmpty(skipRoleTypeIds)) {
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, ""),
						EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_IN, skipRoleTypeIds)
						));
			}

			if (UtilValidate.isNotEmpty(emplTeamId)) {
				List<GenericValue> emplPositionFulfillment = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("emplTeamId", emplTeamId), null, false);
				List<String> teamMemberPartyIds = EntityUtil.getFieldListFromEntityList(emplPositionFulfillment, "partyId", true);

				if (UtilValidate.isNotEmpty(teamMemberPartyIds)) {
					List conditionsList = FastList.newInstance();
					conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, teamMemberPartyIds));

					if (UtilValidate.isNotEmpty(roleTypeIds)) {
						conditionsList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeIds));
					}

					EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList,EntityOperator.AND);

					List<GenericValue> partyRoleList = delegator.findList("PartyRole", mainConditons, null, null, null, false);
					List<String> internalPartyIds = EntityUtil.getFieldListFromEntityList(partyRoleList, "partyId", true);
					if (UtilValidate.isNotEmpty(internalPartyIds)) {
						conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, internalPartyIds));
					}
				}
			}

			if (UtilValidate.isNotEmpty(roleTypeIds)) {
				List conditionsList = FastList.newInstance();
				conditionsList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeIds));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList,EntityOperator.AND);
				List<GenericValue> partyRoleList = delegator.findList("PartyRole", mainConditons, null, null, null, false);
				List<String> internalPartyIds = EntityUtil.getFieldListFromEntityList(partyRoleList, "partyId", true);
				if (UtilValidate.isNotEmpty(internalPartyIds)) {
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, internalPartyIds));
				} else {
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "0000"));
				}
			}

			EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			Debug.logInfo("getUsers mainCondition: "+mainCondition, MODULE);

			Set < String > fieldToSelect = new TreeSet < String > ();
			fieldToSelect.add("partyId");
			fieldToSelect.add("userLoginId");
			fieldToSelect.add("enabled");
			fieldToSelect.add("firstName");
			fieldToSelect.add("lastName");
			fieldToSelect.add("businessUnit");
			int maxRows=2500;
			try {
				maxRows = Integer.parseInt(org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FIND_USER_ROW_COUNT","2500"));
			}catch(Exception e) {
				maxRows = 2500;
			}
			if(UtilValidate.isNotEmpty(searchText)) {
				maxRows=20;
			}
			List < GenericValue > userList = EntityQuery.use(delegator).select(fieldToSelect).from("UserLoginPerson").where(mainCondition).maxRows(maxRows).queryList();
			for (GenericValue user: userList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String userPartyId = user.getString("partyId");
				String enabled = user.getString("enabled");
				String statusId = "Active";
				if("N".equals(enabled))statusId = "Inactive";
				//else if("Y".equals(enabled))statusId = "Active";
				String firstName = user.getString("firstName");
				String lastName = user.getString("lastName");
				String userName = firstName + (UtilValidate.isNotEmpty(lastName) ? " " + lastName : "");
				data.put("partyId", userPartyId);
				data.put("userName", userName);
				data.put("oneBankId", UtilValidate.isNotEmpty(user.getString("userLoginId")) ? user.getString("userLoginId") : "");
				data.put("businessUnit", UtilValidate.isNotEmpty(user.getString("businessUnit")) ? user.getString("businessUnit") : "");
				data.put("userStatus", statusId);
				data.put("firstName", UtilValidate.isNotEmpty(user.getString("firstName")) ? user.getString("firstName") : "");
				data.put("lastName", UtilValidate.isNotEmpty(user.getString("lastName")) ? user.getString("lastName") : "");
				if (UtilValidate.isNotEmpty(searchText)) {
					data.put("partyName", UtilValidate.isNotEmpty(PartyHelper.getPersonName(delegator, user.getString("partyId"), false))
						? org.fio.homeapps.util.DataUtil.combineValueKey(PartyHelper.getPersonName(delegator, user.getString("partyId"), false), user.getString("partyId")) : "");
				}
				String businessunitId = (String) data.get("businessUnit");
				String businessunitDes = "";
				if(UtilValidate.isNotEmpty(businessunitId)) {
					GenericValue getBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", businessunitId).queryOne();
					if (UtilValidate.isNotEmpty(getBu)) {
						businessunitDes = getBu.getString("productStoreGroupName");
					}
				}
				data.put("businessUnitDesc", businessunitDes);

				results.add(data);

			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getRoleUsers(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String parentTypeId = (String) context.get("parentTypeId");
		String roleTypeId = (String) context.get("roleTypeId");
		try {
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);

			List < GenericValue > userList = EntityQuery.use(delegator).select("partyId").from("PartyRole").where(condition).queryList();
			for (GenericValue user: userList) {
				String partyId = user.getString("partyId");
				Set < String > fieldToSelect = new TreeSet < String > ();
				fieldToSelect.add("partyId");
				fieldToSelect.add("userLoginId");
				fieldToSelect.add("enabled");
				fieldToSelect.add("firstName");
				fieldToSelect.add("lastName");
				fieldToSelect.add("businessUnit");
				List < GenericValue > userLoginPerson = EntityQuery.use(delegator).select(fieldToSelect).from("UserLoginPerson").where("partyId", partyId).queryList();
				if (UtilValidate.isNotEmpty(userLoginPerson)) {
					for (GenericValue userLoginDetails: userLoginPerson) {
						Map<String, Object> data = new HashMap<String, Object>();
						String enabled = userLoginDetails.getString("enabled");
						String firstName = userLoginDetails.getString("firstName");
						String lastName = userLoginDetails.getString("lastName");
						String userName = firstName + (UtilValidate.isNotEmpty(lastName) ? " " + lastName : "");
						data.put("partyId", partyId);
						data.put("userName", userName);
						data.put("userLoginId", UtilValidate.isNotEmpty(userLoginDetails.getString("userLoginId")) ? userLoginDetails.getString("userLoginId") : "");
						data.put("businessUnit", UtilValidate.isNotEmpty(userLoginDetails.getString("businessUnit")) ? userLoginDetails.getString("businessUnit") : "");
						data.put("userStatus", enabled == "N" ? "Inactive" : "Active");
						results.add(data);
					}
				}
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getSecurityPermissions(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String permissionId = (String) context.get("permissionId");
		String groupId = (String) context.get("groupId");
		try {
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(permissionId)) {
				EntityCondition permissionCondition = EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("permissionId", EntityOperator.LIKE, "%" + permissionId + "%"),
						EntityCondition.makeCondition("description", EntityOperator.LIKE, "%" + permissionId + "%")
						);
				conditions.add(permissionCondition);
			}
			if (UtilValidate.isNotEmpty(groupId)) {
				List < GenericValue > securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission").where("groupId", groupId).queryList();
				if (UtilValidate.isNotEmpty(securityGroupPermission)) {
					List < String > permissionList = EntityUtil.getFieldListFromEntityList(securityGroupPermission, "permissionId", true);
					conditions.add(EntityCondition.makeCondition("permissionId", EntityOperator.NOT_IN, permissionList));
				}
			}
			List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < GenericValue > permissionList = EntityQuery.use(delegator).from("SecurityPermission").where(condition).queryList();
			for (GenericValue permissionGv: permissionList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String permission = permissionGv.getString("permissionId");
				String description = permissionGv.getString("description");
				data.put("permissionId", permission);
				data.put("description", description);
				dataList.add(data);
			}
			result.put("list", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return doJSONResponse(response, result);
	}

	public static String getSecurityGroups(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String groupId = (String) context.get("groupId");
		String roleTypeId = (String) context.get("roleTypeId");
		try {
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(groupId)) {
				EntityCondition groupCondition = EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("groupId", EntityOperator.LIKE, "%" + groupId + "%"),
						EntityCondition.makeCondition("description", EntityOperator.LIKE, "%" + groupId + "%")
						);
				conditions.add(groupCondition);
			}
			EntityCondition securityTypeCondition = EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, "N"),
					EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, null)
					);
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				List < GenericValue > securityGroupRoleList = EntityQuery.use(delegator).select("groupId").from("SecurityGroupRoleTypeAssoc").where("roleTypeId", roleTypeId).queryList();
				if (UtilValidate.isNotEmpty(securityGroupRoleList)) {
					List < String > assignedSecurities = EntityUtil.getFieldListFromEntityList(securityGroupRoleList, "groupId", true);
					conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.NOT_IN, assignedSecurities));
				}
			}
			List < GenericValue > securityTypeList = EntityQuery.use(delegator).from("SecurityType").where(securityTypeCondition).queryList();
			Map<String, Object> securityTypeMap = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(securityTypeList)) {
				securityTypeMap = DataUtil.getMapFromGeneric(securityTypeList, "securityTypeId", "description", false);
				if (UtilValidate.isNotEmpty(securityTypeMap)) {
					List < String > typeList = new ArrayList < String > ();
					for (String key: securityTypeMap.keySet()) {
						typeList.add(key);
					}
					conditions.add(EntityCondition.makeCondition("securityTypeId", EntityOperator.IN, typeList));
				}
			}
			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			List < GenericValue > groupList = EntityQuery.use(delegator).from("SecurityGroup").where(condition).queryList();
			for (GenericValue groupGv: groupList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String group = groupGv.getString("groupId");
				String description = groupGv.getString("description");
				String securityTypeId = groupGv.getString("securityTypeId");
				data.put("groupId", group);
				data.put("securityType", UtilValidate.isNotEmpty(securityTypeId) ? securityTypeMap.get(securityTypeId) : "");
				data.put("description", description);
				dataList.add(data);
			}
			result.put("list", dataList);
			Debug.log("Results : " + result, MODULE);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return doJSONResponse(response, result);
	}
	public static String getPermissionsForSecurityGroups(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String groupId = (String) context.get("groupId");
		try {
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(groupId)) {
				conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
			}

			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);

			List < GenericValue > groupList = EntityQuery.use(delegator).from("SecurityGroupPermission").where(condition).queryList();
			for (GenericValue groupGv: groupList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String permissionId = groupGv.getString("permissionId");
				GenericValue permissionGv = EntityQuery.use(delegator).from("SecurityPermission").where("permissionId", permissionId).queryFirst();
				String description = "";
				if (UtilValidate.isNotEmpty(permissionGv)) {
					description = permissionGv.getString("description");
				}
				data.put("groupId", groupId);
				data.put("permissionId", permissionId);
				data.put("description", description);
				results.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getRoles(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String parentTypeId = (String) context.get("parentTypeId");
		String roleTypeId = (String) context.get("roleTypeId");
		String partyId = (String) context.get("partyId");
		try {
			if (UtilValidate.isEmpty(parentTypeId))
				parentTypeId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(partyId)) {
				List < GenericValue > existRoleList = DataUtil.getPartyRoles(delegator, partyId, parentTypeId);
				if (UtilValidate.isNotEmpty(existRoleList)) {
					List < String > existRole = EntityUtil.getFieldListFromEntityList(existRoleList, "roleTypeId", true);
					if (UtilValidate.isNotEmpty(existRole)) {
						conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_IN, existRole));
					}
				}
			}
			if (UtilValidate.isNotEmpty(parentTypeId)) {
				conditions.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, parentTypeId));
			}
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				/*
				 * EntityCondition roleCondition =
				 * EntityCondition.makeCondition(EntityOperator.OR,
				 * EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "%" +
				 * roleTypeId + "%"), EntityCondition.makeCondition("description",
				 * EntityOperator.LIKE, "%" + roleTypeId + "%") );
				 */
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
			}
			EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < GenericValue > roleList = EntityQuery.use(delegator).select("roleTypeId", "parentTypeId", "description").from("RoleType").where(mainCondition).orderBy("-lastUpdatedTxStamp").queryList();
			for (GenericValue role: roleList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String roleTypeId1 = role.getString("roleTypeId");
				String parentTypeId1 = role.getString("parentTypeId");
				String description = role.getString("description");
				data.put("parentTypeId", parentTypeId1);
				data.put("roleTypeId", roleTypeId1);
				data.put("description", description);
				results.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getUserRoles(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		String partyId = request.getParameter("partyId");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				String securityParentRole = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
				List < GenericValue > roleList = DataUtil.getPartyRoles(delegator, partyId, securityParentRole);
				for (GenericValue partyRole: roleList) {
					String roleTypeId = partyRole.getString("roleTypeId");
					GenericValue roleType = EntityQuery.use(delegator).select("description").from("RoleType").where("roleTypeId", roleTypeId).orderBy("-lastUpdatedTxStamp").queryFirst();
					Map<String, Object> data = new HashMap<String, Object>();
					String createdOn = partyRole.getString("createdOn");
					if (UtilValidate.isNotEmpty(createdOn))
						createdOn = DataUtil.convertDateTimestamp(createdOn, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					String createdBy = partyRole.getString("createdBy");
					String description = roleType.getString("description");
					data.put("partyId", partyId);
					data.put("roleTypeId", roleTypeId);
					data.put("createdOn", createdOn);
					data.put("createdBy", createdBy);
					data.put("description", description);
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	/*
	public static String getUserTeams(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		String partyId = request.getParameter("partyId");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				String securityParentRole = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
				List < GenericValue > roleList = DataUtil.getPartyRoles(delegator, partyId, securityParentRole);
				for (GenericValue partyRole: roleList) {
					String roleTypeId = partyRole.getString("roleTypeId");
					GenericValue roleType = EntityQuery.use(delegator).select("description").from("RoleType").where("roleTypeId", roleTypeId).orderBy("-lastUpdatedTxStamp").queryFirst();
					Map<String, Object> data = new HashMap<String, Object>();
					String createdOn = partyRole.getString("createdOn");
					if (UtilValidate.isNotEmpty(createdOn))
						createdOn = DataUtil.convertDateTimestamp(createdOn, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					String createdBy = partyRole.getString("createdBy");
					String description = roleType.getString("description");
					data.put("partyId", partyId);
					data.put("roleTypeId", roleTypeId);
					data.put("createdOn", createdOn);
					data.put("createdBy", createdBy);
					data.put("description", description);
					results.add(data);
				}
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	 */
	public static String getRoleSecurity(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		String parentTypeId = (String) context.get("parentTypeId");
		String roleTypeId = (String) context.get("roleTypeId");
		try {
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				List < EntityCondition > conditions = new ArrayList<EntityCondition>();
				if (UtilValidate.isNotEmpty(parentTypeId)) {
					conditions.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, parentTypeId));
				}
				if (UtilValidate.isNotEmpty(roleTypeId)) {
					conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
				}
				EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List < GenericValue > securityGroupRoleList = EntityQuery.use(delegator).select("groupId").from("SecurityGroupRoleTypeAssoc").where(mainCondition).maxRows(100).queryList();
				if (UtilValidate.isNotEmpty(securityGroupRoleList)) {
					for (GenericValue securityGroupRole: securityGroupRoleList) {
						Map<String, Object> data = new HashMap<String, Object>();
						String groupId = securityGroupRole.getString("groupId");
						GenericValue securityGroup = EntityQuery.use(delegator).from("SecurityGroup").where("groupId", groupId).queryOne();
						if (UtilValidate.isNotEmpty(securityGroup)) {
							String securityTypeId = securityGroup.getString("securityTypeId");
							EntityCondition securityTypeCondition = EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("securityTypeId", EntityOperator.EQUALS, securityTypeId),
									EntityCondition.makeCondition(EntityOperator.OR,
											EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, "N"),
											EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, null)
											));
							GenericValue securityType = EntityQuery.use(delegator).from("SecurityType").where(securityTypeCondition).orderBy("-lastUpdatedTxStamp").queryFirst();
							data.put("groupId", securityGroup.getString("groupId"));
							data.put("securityType", UtilValidate.isNotEmpty(securityType) ? securityType.getString("description") : "");
							data.put("description", securityGroup.getString("description"));
							results.add(data);
						}
					}
				}
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	public static String getDerivedSecurity(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		String parentTypeId = (String) context.get("parentTypeId");
		String partyId = (String) context.get("partyId");
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				if (UtilValidate.isEmpty(parentTypeId)) {
					parentTypeId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
				}
				List < GenericValue > roleList = DataUtil.getPartyRoles(delegator, partyId, parentTypeId);
				if (UtilValidate.isNotEmpty(roleList)) {
					List < String > roles = EntityUtil.getFieldListFromEntityList(roleList, "roleTypeId", true);
					EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roles),
							EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, "N"),
									EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, null)
									)
							);
					List < GenericValue > securityGroupRoleList = EntityQuery.use(delegator).select("groupId").from("SecurityGroupRoleTypeAssoc").where(condition).maxRows(100).queryList();
					if (UtilValidate.isNotEmpty(securityGroupRoleList)) {
						for (GenericValue securityGroupRole: securityGroupRoleList) {
							Map<String, Object> data = new HashMap<String, Object>();
							String groupId = securityGroupRole.getString("groupId");
							GenericValue securityGroup = EntityQuery.use(delegator).from("SecurityGroup").where("groupId", groupId).queryOne();
							if (UtilValidate.isNotEmpty(securityGroup)) {
								String securityTypeId = securityGroup.getString("securityTypeId");
								EntityCondition securityTypeCondition = EntityCondition.makeCondition(EntityOperator.AND,
										EntityCondition.makeCondition("securityTypeId", EntityOperator.EQUALS, securityTypeId),
										EntityCondition.makeCondition(EntityOperator.OR,
												EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, "N"),
												EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, null)
												));
								GenericValue securityType = EntityQuery.use(delegator).from("SecurityType").where(securityTypeCondition).orderBy("-lastUpdatedTxStamp").queryFirst();
								data.put("groupId", securityGroup.getString("groupId"));
								data.put("securityType", UtilValidate.isNotEmpty(securityType) ? securityType.getString("description") : "");
								data.put("description", securityGroup.getString("description"));
								results.add(data);
							}
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getCustomSecurity(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		String partyId = (String) context.get("partyId");
		String userLoginId = (String) context.get("userLoginId");
		try {
			if (UtilValidate.isNotEmpty(userLoginId)) {
				List < EntityCondition > conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId));

				EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List < GenericValue > userLoginSecurityGroupList = EntityQuery.use(delegator).select("groupId").from("UserLoginSecurityGroup").where(mainCondition).queryList();
				if (UtilValidate.isNotEmpty(userLoginSecurityGroupList)) {
					/*EntityCondition securityTypeCondition = EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, "N"),
									EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, null)
									);*/
					List <GenericValue> securityTypeList = EntityQuery.use(delegator).from("SecurityType").queryList();
					Map<String, Object> securityTypeMap = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(securityTypeList)) {
						securityTypeMap = DataUtil.getMapFromGeneric(securityTypeList, "securityTypeId", "description", false);
					}
					for (GenericValue userLoginSecurityGroup: userLoginSecurityGroupList) {
						Map<String, Object> data = new HashMap<String, Object>();
						String groupId = userLoginSecurityGroup.getString("groupId");
						GenericValue securityGroup = EntityQuery.use(delegator).from("SecurityGroup").where("groupId", groupId).queryOne();
						if (UtilValidate.isNotEmpty(securityGroup)) {
							String securityTypeId = securityGroup.getString("securityTypeId");
							data.put("groupId", securityGroup.getString("groupId"));
							data.put("securityType", UtilValidate.isNotEmpty(securityTypeId) && UtilValidate.isNotEmpty(securityTypeMap) ? securityTypeMap.get(securityTypeId) : "");
							data.put("description", securityGroup.getString("description"));
							results.add(data);
						}
					}
				}
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getConfiguredEntities(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			String sqlQuery = "SELECT entity_name AS entityName, entity_alias_name AS entityAliasName, entity_Type as entityType, role_type_id as roleTypeId, created_on AS createdOn, created_by AS createdBy FROM `entity_operation_config` GROUP BY entity_name,entity_alias_name";
			List < Map<String, Object>> entityConfigList = SqlUtil.executeQuery(delegator, sqlQuery);
			if (entityConfigList != null && entityConfigList.size() > 0) {
				for (Map<String, Object> map: entityConfigList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("entityName", map.get("entityName"));
					data.put("entityAliasName", map.get("entityAliasName"));
					data.put("entityType", map.get("entityType"));
					data.put("roleTypeId", map.get("roleTypeId"));
					String createdOn = "";
					if (UtilValidate.isNotEmpty(map.get("createdOn"))) {
						createdOn = df.format(map.get("createdOn"));
					}
					data.put("createdOn", createdOn);
					data.put("createdBy", map.get("createdBy"));
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getEntitiesSecurityPermission(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String entityName = (String) context.get("entityName");
		String roleTypeId = (String) context.get("roleTypeId");
		try {
			if(UtilValidate.isNotEmpty(roleTypeId)) {
				GenericValue entityOperationConfig = EntityQuery.use(delegator).from("EntityOperationConfig").where("entityName",entityName,"roleTypeId",roleTypeId,"entityType","PARTY_ENTITY").queryFirst();
				if(UtilValidate.isNotEmpty(entityOperationConfig)) {
					entityName = entityOperationConfig.getString("entityAliasName");
				}
			}

			EntityCondition condition = EntityCondition.makeCondition("permissionId", EntityOperator.LIKE, entityName + "\\_%");
			List < GenericValue > entitySecurityPermissionList = EntityQuery.use(delegator).from("SecurityPermission").where(condition).orderBy("createdTxStamp").queryList();
			if (UtilValidate.isNotEmpty(entitySecurityPermissionList)) {
				for (GenericValue entitySecurityPermission: entitySecurityPermissionList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("permissionId", entitySecurityPermission.getString("permissionId"));
					data.put("description", entitySecurityPermission.getString("description"));
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	public static void validateUniqueId(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String entityName = (String) context.get("entityName");
		String fieldValue = (String) context.get("fieldValue");
		String fieldName = (String) context.get("fieldName");
		try {
			PrintWriter out = response.getWriter();
			if (UtilValidate.isNotEmpty(fieldValue) && UtilValidate.isNotEmpty(fieldName) && UtilValidate.isNotEmpty(entityName)) {
				String hasFilter = (String) context.get("hasFilter");
				GenericValue genericValues = null;
				if ("Y".equals(hasFilter)) {
					genericValues = EntityQuery.use(delegator).from(entityName).where(fieldName, fieldValue).orderBy("-lastUpdatedTxStamp").filterByDate().queryFirst();
				} else {
					genericValues = EntityQuery.use(delegator).from(entityName).where(fieldName, fieldValue).orderBy("-lastUpdatedTxStamp").queryFirst();
				}
				if (UtilValidate.isEmpty(genericValues)) {
					out.print("N");
				} else {
					out.print("Y");
				}
			} else {
				out.print("Please pass required parameter for validate");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Author : Arshiya S, Description : Getting List SLA Setups
	public static String getSlaSetups(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String srTypeId = (String) context.get("srTypeId");
		String srCategoryId = (String) context.get("srCategoryId");
		String srSubCategoryId = (String) context.get("srSubCategoryId");
		String statusId = (String) context.get("status");
		String srPriority = (String) context.get("srPriority");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(srTypeId)) {
				conditionlist.add(EntityCondition.makeCondition("srTypeId", EntityOperator.EQUALS, srTypeId));
			}
			if (UtilValidate.isNotEmpty(srCategoryId)) {
				conditionlist.add(EntityCondition.makeCondition("srCategoryId", EntityOperator.EQUALS, srCategoryId));
			}
			if (UtilValidate.isNotEmpty(srSubCategoryId)) {
				conditionlist.add(EntityCondition.makeCondition("srSubCategoryId", EntityOperator.EQUALS, srSubCategoryId));
			}
			if (UtilValidate.isNotEmpty(srPriority)) {
				conditionlist.add(EntityCondition.makeCondition("srPriority", EntityOperator.EQUALS, srPriority));
			}
			if (UtilValidate.isNotEmpty(statusId)) {
				conditionlist.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, statusId));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("srTypeId");
			fieldsToSelect.add("srCategoryId");
			fieldsToSelect.add("srSubCategoryId");
			fieldsToSelect.add("srPriority");
			fieldsToSelect.add("status");
			fieldsToSelect.add("slaPeriodLvl");
			fieldsToSelect.add("srPeriodUnit");
			fieldsToSelect.add("slaPeriodLvl1");
			fieldsToSelect.add("slaPeriodLvl2");
			fieldsToSelect.add("slaPeriodLvl3");
			fieldsToSelect.add("slaEscPeriodHrsLvl1");
			fieldsToSelect.add("slaEscPeriodHrsLvl2");
			fieldsToSelect.add("slaEscPeriodHrsLvl3");
			fieldsToSelect.add("slaPreEscLvl");
			fieldsToSelect.add("slaPreEscPeriod");
			fieldsToSelect.add("createdDate");
			fieldsToSelect.add("createdBy");
			fieldsToSelect.add("modifiedDate");
			fieldsToSelect.add("modifiedBy");
			fieldsToSelect.add("slaConfigId");
			fieldsToSelect.add("isSlaRequired");
			List < GenericValue > srSlaSetups = EntityQuery.use(delegator).select(fieldsToSelect).from("SrSlaConfig").where(condition).maxRows(100).orderBy("-lastUpdatedTxStamp").queryList();
			if (srSlaSetups != null && srSlaSetups.size() > 0) {
				for (GenericValue slaSetup: srSlaSetups) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("srTypeId", DataHelper.getCustRequestAssocDesc(delegator, CustRequestAssocConstants.SR_TYPE, slaSetup.getString("srTypeId")));
					String categoryId = DataHelper.getCustRequestAssocDesc(delegator, CustRequestAssocConstants.SR_Category, slaSetup.getString("srCategoryId"));
					data.put("srCategoryId", UtilValidate.isNotEmpty(categoryId) ? categoryId : "NA");
					String subCategoryId = DataHelper.getCustRequestAssocDesc(delegator, CustRequestAssocConstants.SR_SubCategory, slaSetup.getString("srSubCategoryId"));
					data.put("srSubCategoryId", UtilValidate.isNotEmpty(subCategoryId) ? subCategoryId : "NA");
					/*data.put("srTypeId", slaSetup.getString("srTypeId"));
                    data.put("srCategoryId", slaSetup.getString("srCategoryId"));
                    data.put("srSubCategoryId", slaSetup.getString("srSubCategoryId"));*/
					data.put("srPriority", EnumUtil.getEnumDescription(delegator, slaSetup.getString("srPriority"), "PRIORITY_LEVEL"));
					data.put("status", "ACTIVE".equals(slaSetup.getString("status")) ? "Active" : "Inactive");//EnumUtil.getEnumDescription(delegator, slaSetup.getString("status"), "STATUS_ID")
					data.put("slaPeriodLvl", slaSetup.getString("slaPeriodLvl"));
					data.put("slaPeriodUnit", slaSetup.getString("srPeriodUnit"));
					data.put("slaPeriodLvl1", slaSetup.getString("slaPeriodLvl1"));
					data.put("slaPeriodLvl2", slaSetup.getString("slaPeriodLvl2"));
					data.put("slaPeriodLvl3", slaSetup.getString("slaPeriodLvl3"));
					data.put("slaEscPeriodHrsLvl1", slaSetup.getInteger("slaEscPeriodHrsLvl1"));
					data.put("slaEscPeriodHrsLvl2", slaSetup.getInteger("slaEscPeriodHrsLvl2"));
					data.put("slaEscPeriodHrsLvl3", slaSetup.getInteger("slaEscPeriodHrsLvl3"));
					data.put("slaPreEscLvl", slaSetup.getString("slaPreEscLvl"));
					data.put("slaPreEscPeriod", slaSetup.getString("slaPreEscPeriod"));
					String createdDate = slaSetup.getString("createdDate");
					if (UtilValidate.isNotEmpty(createdDate))
						createdDate = DataUtil.convertDateTimestamp(createdDate, new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					data.put("createdDate", createdDate);
					data.put("createdBy", slaSetup.getString("createdBy"));
					String modifiedDate = null;
					if(UtilValidate.isNotEmpty(slaSetup.getString("modifiedDate"))) {
						modifiedDate = DataUtil.convertDateTimestamp(slaSetup.getString("modifiedDate"), new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					}
					data.put("modifiedDate", modifiedDate);
					data.put("modifiedBy", slaSetup.getString("modifiedBy"));
					data.put("slaConfigId", slaSetup.getString("slaConfigId"));

					data.put("isSlaRequired", slaSetup.getString("isSlaRequired"));

					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	//Desc : Non-Working Days Grid. Author : Arshiya S
	public static String getCalenderHolidayList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String nonWorkingDate = (String) context.get("nonWorkingDate");
		String status = (String) context.get("status");
		List < EntityCondition > conditionlist = FastList.newInstance();
		try {
			if(UtilValidate.isNotEmpty(nonWorkingDate)) {
				nonWorkingDate = df1.format(df.parse(nonWorkingDate));
				conditionlist.add(EntityCondition.makeCondition("holidayDate", EntityOperator.EQUALS, java.sql.Date.valueOf(nonWorkingDate)));
			}
			if (UtilValidate.isNotEmpty(status)) {
				conditionlist.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, status));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("holidayDate");
			fieldsToSelect.add("holidayConfigId");
			fieldsToSelect.add("holidayDescription");
			fieldsToSelect.add("type");
			fieldsToSelect.add("status");
			fieldsToSelect.add("createdDate");
			fieldsToSelect.add("createdBy");
			fieldsToSelect.add("modifiedDate");
			fieldsToSelect.add("modifiedBy");
			Debug.log(">>>>>condition>>>>>"+condition);
			List < GenericValue > holidayConfigLists = EntityQuery.use(delegator).select(fieldsToSelect).from("TechDataHolidayConfig").where(condition).maxRows(100).orderBy("-lastUpdatedTxStamp").queryList();
			if (holidayConfigLists != null && holidayConfigLists.size() > 0) {
				for (GenericValue holidayConfigList: holidayConfigLists) {
					Map<String, Object> data = new HashMap<String, Object>();
					String holidayDate = holidayConfigList.getString("holidayDate");
					if(UtilValidate.isNotEmpty(holidayDate)) {
						holidayDate = DataUtil.convertDateTimestamp(holidayDate, df1, DateTimeTypeConstant.DATE, DateTimeTypeConstant.STRING);
					}
					data.put("nonWorkingDate", holidayDate);
					data.put("holidayConfigId", holidayConfigList.getString("holidayConfigId"));
					data.put("holidayDescription", holidayConfigList.getString("holidayDescription"));

					String type = "";
					if (UtilValidate.isNotEmpty(holidayConfigList.getString("type"))) {
						switch (holidayConfigList.getString("type")) {
						case "HOLIDAY":
							type = "Holiday";
							break;
						case "WEEKEND":
							type = "Weekend";
							break;
						case "NWD":
							type = "Non Working Days";
							break;
						default:
							break;
						}
					}
					data.put("type", type);

					data.put("status", "ACTIVE".equals(holidayConfigList.getString("status")) ? "Active" : "In Active");//EnumUtil.getEnumDescription(delegator, holidayConfigList.getString("status"), "STATUS_ID")
					String createdDate = holidayConfigList.getString("createdDate");
					if (UtilValidate.isNotEmpty(createdDate)) {
						createdDate = DataUtil.convertDateTimestamp(createdDate, new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					}
					String modifiedDate = holidayConfigList.getString("modifiedDate");
					if (UtilValidate.isNotEmpty(modifiedDate)) {
						modifiedDate = DataUtil.convertDateTimestamp(modifiedDate, new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					}
					data.put("createdDate", createdDate);
					data.put("createdBy", holidayConfigList.getString("createdBy"));
					data.put("modifiedBy", holidayConfigList.getString("modifiedBy"));
					data.put("modifiedDate", modifiedDate);
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	public static String getParentBusinessUnit(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		try {
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("productStoreGroupId");
			fieldsToSelect.add("productStoreGroupName");
			fieldsToSelect.add("description");
			fieldsToSelect.add("externalId");
			List < GenericValue > productStoreGroups = EntityQuery.use(delegator).select(fieldsToSelect).from("ProductStoreGroup").where("status", BusinessUnitConstant.ACTIVE).orderBy("-lastUpdatedTxStamp").queryList();
			if (productStoreGroups != null && productStoreGroups.size() > 0) {
				for (GenericValue productStoreGroup: productStoreGroups) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("productStoreGroupId", productStoreGroup.getString("productStoreGroupId"));
					data.put("productStoreGroupName", productStoreGroup.getString("productStoreGroupName"));
					data.put("description", productStoreGroup.getString("description"));
					data.put("externalId", productStoreGroup.getString("externalId"));
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getParentBusinessUnitInUpdate(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String buId = (String) context.get("productStoreGroupId");
		String productStoreGroupName = (String) context.get("productStoreGroupName");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(buId)) {
				conditionlist.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.NOT_EQUAL, buId));
			}
				conditionlist.add(EntityCondition.makeCondition("status", BusinessUnitConstant.ACTIVE));
			if (UtilValidate.isNotEmpty(productStoreGroupName)) {
				conditionlist.add(EntityCondition.makeCondition("productStoreGroupName",  EntityOperator.LIKE, "%"+productStoreGroupName+"%"));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("productStoreGroupId");
			fieldsToSelect.add("productStoreGroupName");
			fieldsToSelect.add("description");
			fieldsToSelect.add("externalId");
			List < GenericValue > productStoreGroups = EntityQuery.use(delegator).select(fieldsToSelect).from("ProductStoreGroup").where(condition).orderBy("-lastUpdatedTxStamp").queryList();

			if (productStoreGroups != null && productStoreGroups.size() > 0) {
				for (GenericValue productStoreGroup: productStoreGroups) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("productStoreGroupId", productStoreGroup.getString("productStoreGroupId"));
					data.put("productStoreGroupName", productStoreGroup.getString("productStoreGroupName"));
					data.put("description", productStoreGroup.getString("description"));
					data.put("externalId", productStoreGroup.getString("externalId"));
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	//Author : Anagha B P, Description : Getting List of Business Unit
	public static String getBusinessUnit(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		// SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String buName = (String) context.get("buName");
		String parentBuName = (String) context.get("parentBuName");
		String buType = (String) context.get("buType");
		String buStatus = (String) context.get("buStatus");
		try {

			List <String> parentBuIds = null;
			List<GenericValue> parentProductStoreGroups = delegator.findList("ProductStoreGroup", EntityCondition.makeCondition("primaryParentGroupId", EntityOperator.NOT_EQUAL, null), null, UtilMisc.toList("primaryParentGroupId"), null, false);

			if (UtilValidate.isNotEmpty(parentProductStoreGroups)) {

				parentBuIds = EntityUtil.getFieldListFromEntityList(parentProductStoreGroups, "primaryParentGroupId", true);
			}

			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(buName)) {
				conditionlist.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, buName));
			}

			if (UtilValidate.isNotEmpty(parentBuName)) {

				conditionlist.add(EntityCondition.makeCondition("primaryParentGroupId", EntityOperator.EQUALS, parentBuName));
			}else if(UtilValidate.isNotEmpty(parentBuIds)){
				conditionlist.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.NOT_IN, parentBuIds));

			}

			if (UtilValidate.isNotEmpty(buType)) {
				conditionlist.add(EntityCondition.makeCondition("productStoreGroupTypeId", EntityOperator.EQUALS, buType));
			}
			if (UtilValidate.isNotEmpty(buStatus)) {
				conditionlist.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, buStatus));
			}

			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);

			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("productStoreGroupId");
			fieldsToSelect.add("productStoreGroupName");
			fieldsToSelect.add("primaryParentGroupId");
			fieldsToSelect.add("externalId");
			fieldsToSelect.add("productStoreGroupTypeId");
			fieldsToSelect.add("seqNum");
			fieldsToSelect.add("status");
			fieldsToSelect.add("websiteId");
			fieldsToSelect.add("postalContactMechId");
			fieldsToSelect.add("createdByUserLogin");
			fieldsToSelect.add("createdOn");
			fieldsToSelect.add("modifiedByUserLogin");
			fieldsToSelect.add("modifiedOn");
			fieldsToSelect.add("lastUpdatedTxStamp");
			String productStoreGroupId = null;
			String productStoreGroupTypeId = null;
			String status = null;
			String postalContactMechId = null;
			String websiteId = null;
			String state = null;
			String country = null;
			String createdOn = null;
			String modifiedOn = null;
			Debug.logInfo("getBusinessUnit condition> "+condition, MODULE);
			List < GenericValue > productStoreGroups = EntityQuery.use(delegator).select(fieldsToSelect).from("ProductStoreGroup").where(condition).maxRows(100).orderBy("-lastUpdatedTxStamp").queryList();
			Debug.logInfo("getBusinessUnit productStoreGroups> "+productStoreGroups, MODULE);
			if (productStoreGroups != null && productStoreGroups.size() > 0) {
				for (GenericValue productStoreGroup: productStoreGroups) {
					Map<String, Object> data = new HashMap<String, Object>();
					String productStoreGroupIdVal = (String)productStoreGroup.getString("productStoreGroupId");

					data.put("productStoreGroupName", productStoreGroup.getString("productStoreGroupName"));
					data.put("externalId", productStoreGroup.getString("externalId"));
					productStoreGroupId = productStoreGroup.getString("primaryParentGroupId");
					if (UtilValidate.isNotEmpty(productStoreGroupId)) {
						GenericValue getParentBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", productStoreGroupId).queryOne();
						if (UtilValidate.isNotEmpty(getParentBu)) {
							data.put("parentBu", getParentBu.getString("productStoreGroupName"));
						}
					}
					productStoreGroupTypeId = productStoreGroup.getString("productStoreGroupTypeId");
					if (UtilValidate.isNotEmpty(productStoreGroupTypeId)) {
						GenericValue getBuType = EntityQuery.use(delegator).from("ProductStoreGroupType").where("productStoreGroupTypeId", productStoreGroupTypeId).queryOne();
						if (UtilValidate.isNotEmpty(getBuType)) {
							data.put("productStoreGroupTypeId", getBuType.getString("description"));
						}
					}
					status = productStoreGroup.getString("status");
					if (UtilValidate.isNotEmpty(status)) {
						GenericValue getBuStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", status, "enumTypeId", BusinessUnitConstant.STATUS_ID), null, false));
						if (UtilValidate.isNotEmpty(getBuStatus)) {
							data.put("status", getBuStatus.getString("description"));
						}
					}
					postalContactMechId = productStoreGroup.getString("postalContactMechId");
					if (UtilValidate.isNotEmpty(postalContactMechId)) {
						GenericValue getPostalAddress = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId", postalContactMechId).queryOne();
						if (UtilValidate.isNotEmpty(getPostalAddress)) {
							state = getPostalAddress.getString("stateProvinceGeoId");
							country = getPostalAddress.getString("countryGeoId");
							if (UtilValidate.isNotEmpty(state)) {
								GenericValue getState = EntityQuery.use(delegator).from("Geo").where("geoId", state).queryOne();
								if (UtilValidate.isNotEmpty(getState)) {
									data.put("state", getState.getString("geoName"));
								}
							}
							if (UtilValidate.isNotEmpty(country)) {
								GenericValue getCountry = EntityQuery.use(delegator).from("Geo").where("geoId", country).queryOne();
								if (UtilValidate.isNotEmpty(getCountry)) {
									data.put("country", getCountry.getString("geoName"));
								}
							}
							data.put("city", getPostalAddress.getString("city"));
							data.put("postalContactMechId", postalContactMechId);

						}
					}
					websiteId = productStoreGroup.getString("websiteId");
					if (UtilValidate.isNotEmpty(websiteId)) {
						GenericValue getWebsite = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", websiteId).queryOne();
						if (UtilValidate.isNotEmpty(getWebsite)) {
							data.put("website", getWebsite.getString("infoString"));
							data.put("websiteId", websiteId);

						}
					}
					String createdDate = productStoreGroup.getString("createdOn");
					if (UtilValidate.isNotEmpty(createdDate)) {
						createdDate = DataUtil.convertDateTimestamp(createdDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("createdOn", createdDate);
					}
					String modifiedDate = productStoreGroup.getString("modifiedOn");
					if (UtilValidate.isNotEmpty(modifiedDate)) {
						modifiedDate = DataUtil.convertDateTimestamp(modifiedDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("modifiedOn", modifiedDate);
					}
					data.put("createdByUserLogin", productStoreGroup.getString("createdByUserLogin"));
					data.put("modifiedByUserLogin", productStoreGroup.getString("modifiedByUserLogin"));
					data.put("productStoreGroupId", productStoreGroup.getString("productStoreGroupId"));

					results.add(data);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logInfo("getBusinessUnit error> "+e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	//Author : Anagha B P, Description : Getting List of Teams in Business Unit
	public static String getBuTeams(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String productStoreGroupId = (String) context.get("productStoreGroupId");
		String status = null;
		try {
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("emplTeamId");
			fieldsToSelect.add("teamName");
			fieldsToSelect.add("isActive");
			fieldsToSelect.add("businessUnit");
			fieldsToSelect.add("lastUpdatedTxStamp");
			List < GenericValue > getTeam = EntityQuery.use(delegator).select(fieldsToSelect).from("EmplTeam").where("businessUnit", productStoreGroupId).orderBy("-lastUpdatedTxStamp").queryList();
			if (getTeam != null && getTeam.size() > 0) {
				for (GenericValue Team: getTeam) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("teamName", Team.getString("teamName"));
					data.put("emplTeamId", Team.getString("emplTeamId"));
					data.put("businessUnit", Team.getString("businessUnit"));
					status = Team.getString("isActive");
					if (UtilValidate.isNotEmpty(status)) {
						GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status, "enumTypeId", BusinessUnitConstant.STATUS_ID), null, false));
						if (UtilValidate.isNotEmpty(getStatus)) {
							data.put("teamStatus", getStatus.getString("description"));
						}
					}
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}


	public static String getTeamMembers(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String teamId = (String) context.get("emplTeamId");
		String businessunitDes=null;
		String businessunitId=null;
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<GenericValue> resultList = null;
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		long start = System.currentTimeMillis();
		try {

			//get the default general grid fetch limit
			GenericValue systemProperty = EntityQuery.use(delegator)
					.select("systemPropertyValue")
					.from("SystemProperty")
					.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
					.queryFirst();
			// set the page parameters
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}
			result.put("viewIndex", Integer.valueOf(viewIndex));

			int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;

			int viewSize = fioGridFetch;
			try {
				viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
			} catch (Exception e) {
				viewSize = fioGridFetch;
			}
			result.put("viewSize", Integer.valueOf(viewSize));


			List < EntityCondition > partyconditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(teamId)) {
				partyconditions.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, teamId));
			}
			partyconditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
			EntityCondition usercondition = EntityCondition.makeCondition(partyconditions, EntityOperator.AND);

			int highIndex = 0;
			int lowIndex = 0;
			int resultListSize = 0;
			try {
				// get the indexes for the partial list
				lowIndex = viewIndex * viewSize + 1;
				highIndex = (viewIndex + 1) * viewSize;

				// set distinct on so we only get one row per order
				// using list iterator
				EntityListIterator pli = EntityQuery.use(delegator)
						.from("EmplPositionFulfillment")
						.where(usercondition)
						.orderBy("-lastUpdatedTxStamp")
						.cursorScrollInsensitive()
						.fetchSize(highIndex)
						.distinct()
						.cache(true)
						.queryIterator();
				// get the partial list for this page
				resultList = pli.getPartialList(lowIndex, viewSize);

				// attempt to get the full size
				resultListSize = pli.getResultsSizeAfterPartialList();
				// close the list iterator
				pli.close();
			} catch (GenericEntityException e) {
				String errMsg = "Error: " + e.toString();
				Debug.logError(e, errMsg, MODULE);
			}

			//Debug.log("getTeamEmplPosition=="+getTeamEmplPosition);
			if (UtilValidate.isNotEmpty(resultList)) {
				for (GenericValue userTeam: resultList) {
					String partyId = userTeam.getString("partyId");
					//GenericValue userList = EntityQuery.use(delegator).from("UserLoginPerson").where("partyId", partyId).queryOne();
					GenericValue user = EntityUtil.getFirst( delegator.findByAnd("UserLoginPerson", UtilMisc.toMap("partyId", partyId), null, false) );
					if(UtilValidate.isNotEmpty(user)) {
						Map<String, Object> data = new HashMap<String, Object>();
						String userPartyId = user.getString("partyId");
						String enabled = user.getString("enabled");
						String firstName = user.getString("firstName");
						String lastName = user.getString("lastName");
						String userName = firstName + (UtilValidate.isNotEmpty(lastName) ? " " + lastName : "");
						data.put("newPartyId", userPartyId);
						data.put("partyId", userPartyId);
						data.put("userName", userName);
						data.put("oneBankId", UtilValidate.isNotEmpty(user.getString("userLoginId")) ? user.getString("userLoginId") : "");
						data.put("emplTeamId", teamId);
						businessunitId=user.getString("businessUnit");
						if(UtilValidate.isNotEmpty(businessunitId)) {
							GenericValue getBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", businessunitId).queryOne();
							if (UtilValidate.isNotEmpty(getBu)) {
								businessunitDes=getBu.getString("productStoreGroupName");
								if (UtilValidate.isNotEmpty(businessunitDes)) {
									data.put("businessUnit",businessunitDes );
								}
							}
						}
						//data.put("businessUnit", UtilValidate.isNotEmpty(userList.getString("businessUnit")) ? userList.getString("businessUnit") : "");
						String isLead = userTeam.getString("isTeamLead");
						if ((UtilValidate.isNotEmpty(isLead) && isLead.equalsIgnoreCase("y"))) {
							data.put("teamRole", "Leader");
							data.put("teamId", "Y");
						} else if ((UtilValidate.isNotEmpty(isLead) && isLead.equalsIgnoreCase("n"))) {
							data.put("teamRole", "Member");
							data.put("teamId", "N");
						}
						dataList.add(data);
					}
				}
				result.put("highIndex", Integer.valueOf(highIndex));
				result.put("lowIndex", Integer.valueOf(lowIndex));
			}
			result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			result.put("totalRecords", nf.format(resultListSize));
			result.put("recordCount", resultListSize);
			result.put("chunkSize", viewSize);
			//Debug.log("Results : "+results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}

	public static String getTeam(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String teamName = (String) context.get("teamName");
		String buName = (String) context.get("buName");
		String teamStatus = (String) context.get("teamStatus");
		String partyId = (String) context.get("partyId");
		String isNative = request.getParameter("isNative");

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<GenericValue> resultList = null;
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		long start = System.currentTimeMillis();

		try {
			//get the default general grid fetch limit
			GenericValue systemProperty = EntityQuery.use(delegator)
					.select("systemPropertyValue")
					.from("SystemProperty")
					.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
					.queryFirst();
			// set the page parameters
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}
			result.put("viewIndex", Integer.valueOf(viewIndex));

			int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;

			int viewSize = fioGridFetch;
			try {
				viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
			} catch (Exception e) {
				viewSize = fioGridFetch;
			}
			result.put("viewSize", Integer.valueOf(viewSize));

			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(teamName)) {
				conditionlist.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, teamName));
			}
			if (UtilValidate.isNotEmpty(buName)) {
				conditionlist.add(EntityCondition.makeCondition("businessUnit", EntityOperator.EQUALS, buName));
			}
			if (UtilValidate.isNotEmpty(teamStatus)) {
				conditionlist.add(EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, teamStatus));
			}
			if(UtilValidate.isNotEmpty(partyId) && !"Y".equals(isNative)) {
				List<GenericValue> emplFulfillment = EntityQuery.use(delegator).select("emplTeamId").from("EmplPositionFulfillment").where("partyId", partyId).queryList();
				if(UtilValidate.isNotEmpty(emplFulfillment)) {
					conditionlist.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.NOT_IN, EntityUtil.getFieldListFromEntityList(emplFulfillment, "emplTeamId", true)));
				}
			}

			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);

			Set < String > fieldsToSelect = new LinkedHashSet<String>();
			fieldsToSelect.add("emplTeamId");
			fieldsToSelect.add("teamName");
			fieldsToSelect.add("businessUnit");
			fieldsToSelect.add("isActive");
			fieldsToSelect.add("createdOn");
			fieldsToSelect.add("createdBy");
			fieldsToSelect.add("modifiedOn");
			fieldsToSelect.add("modifiedBy");
			int highIndex = 0;
			int lowIndex = 0;
			int resultListSize = 0;
			try {
				// get the indexes for the partial list
				lowIndex = viewIndex * viewSize + 1;
				highIndex = (viewIndex + 1) * viewSize;

				// set distinct on so we only get one row per order
				// using list iterator
				EntityListIterator pli = EntityQuery.use(delegator)
						.select(fieldsToSelect)
						.from("EmplTeam")
						.where(condition)
						.orderBy("-createdTxStamp")
						.cursorScrollInsensitive()
						.fetchSize(highIndex)
						.distinct()
						.cache(true)
						.queryIterator();
				// get the partial list for this page
				resultList = pli.getPartialList(lowIndex, viewSize);

				// attempt to get the full size
				resultListSize = pli.getResultsSizeAfterPartialList();
				// close the list iterator
				pli.close();
			} catch (GenericEntityException e) {
				String errMsg = "Error: " + e.toString();
				Debug.logError(e, errMsg, MODULE);
			}

			if (UtilValidate.isNotEmpty(resultList)) {
				for (GenericValue emplTeam: resultList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(DataUtil.convertGenericValueToMap(delegator, emplTeam));
					//data.put("teamName", emplTeam.getString("teamName"));
					//data.put("emplTeamId", emplTeam.getString("emplTeamId"));

					String createdOn = emplTeam.getString("createdOn");
					if (UtilValidate.isNotEmpty(createdOn)) {
						createdOn = DataUtil.convertDateTimestamp(createdOn, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("createdDate", createdOn);
					}

					String modifiedOn = emplTeam.getString("modifiedOn");
					if (UtilValidate.isNotEmpty(modifiedOn)) {
						modifiedOn = DataUtil.convertDateTimestamp(modifiedOn, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("modifiedDate", modifiedOn);
					}

					data.put("created", emplTeam.getString("createdBy"));
					data.put("modified", emplTeam.getString("modifiedBy"));
					if (UtilValidate.isNotEmpty(emplTeam.getString("createdBy"))){
						data.put("createdBy", PartyHelper.getUserLoginName(delegator, emplTeam.getString("createdBy"), false));
					}
					if (UtilValidate.isNotEmpty(emplTeam.getString("modifiedBy"))){
						data.put("modifiedBy", PartyHelper.getUserLoginName(delegator, emplTeam.getString("modifiedBy"), false));
					}
					if (UtilValidate.isNotEmpty(emplTeam.getString("businessUnit"))) {
						GenericValue getBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", emplTeam.getString("businessUnit")).queryOne();
						if (UtilValidate.isNotEmpty(getBu)) {
							data.put("buName", getBu.getString("productStoreGroupName"));
						}
					}
					if (UtilValidate.isNotEmpty(emplTeam.getString("isActive"))) {
						GenericValue getStatus = EntityQuery.use(delegator).from("Enumeration").where("enumCode", emplTeam.getString("isActive"), "enumTypeId", BusinessUnitConstant.STATUS_ID).queryOne();
						if (UtilValidate.isNotEmpty(getStatus)) {
							data.put("status", getStatus.getString("description"));
						}
					}
					dataList.add(data);
				}
				result.put("highIndex", Integer.valueOf(highIndex));
				result.put("lowIndex", Integer.valueOf(lowIndex));
			}
			result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			result.put("totalRecords", nf.format(resultListSize));
			result.put("recordCount", resultListSize);
			result.put("chunkSize", viewSize);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}

	public static String getUserTeams(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = (String) context.get("partyId");

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<GenericValue> resultList = null;
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		long start = System.currentTimeMillis();

		try {
			//get the default general grid fetch limit
			GenericValue systemProperty = EntityQuery.use(delegator)
					.select("systemPropertyValue")
					.from("SystemProperty")
					.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
					.queryFirst();
			// set the page parameters
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}
			result.put("viewIndex", Integer.valueOf(viewIndex));

			int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;

			int viewSize = fioGridFetch;
			try {
				viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
			} catch (Exception e) {
				viewSize = fioGridFetch;
			}
			result.put("viewSize", Integer.valueOf(viewSize));

			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(partyId)) {
				conditionlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));

				EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);


				int highIndex = 0;
				int lowIndex = 0;
				int resultListSize = 0;
				try {
					// get the indexes for the partial list
					lowIndex = viewIndex * viewSize + 1;
					highIndex = (viewIndex + 1) * viewSize;

					// set distinct on so we only get one row per order
					// using list iterator
					EntityListIterator pli = EntityQuery.use(delegator)
							.select("emplTeamId")
							.from("EmplPositionFulfillment")
							.where(condition)
							.orderBy("-createdTxStamp")
							.cursorScrollInsensitive()
							.fetchSize(highIndex)
							.filterByDate()
							.distinct()
							.cache(true)
							.queryIterator();
					// get the partial list for this page
					resultList = pli.getPartialList(lowIndex, viewSize);

					// attempt to get the full size
					resultListSize = pli.getResultsSizeAfterPartialList();
					// close the list iterator
					pli.close();
				} catch (GenericEntityException e) {
					String errMsg = "Error: " + e.toString();
					Debug.logError(e, errMsg, MODULE);
				}

				if (UtilValidate.isNotEmpty(resultList)) {
					Set < String > fieldsToSelect = new LinkedHashSet<String>();
					fieldsToSelect.add("emplTeamId");
					fieldsToSelect.add("teamName");
					fieldsToSelect.add("businessUnit");
					fieldsToSelect.add("isActive");
					fieldsToSelect.add("createdOn");
					fieldsToSelect.add("createdBy");
					fieldsToSelect.add("modifiedOn");
					fieldsToSelect.add("modifiedBy");
					for (GenericValue emplPositionFulfillment: resultList) {
						Map<String, Object> data = new HashMap<String, Object>();
						String emplTeamId = emplPositionFulfillment.getString("emplTeamId");
						if(UtilValidate.isEmpty(emplTeamId))
							continue;
						GenericValue emplTeam  = EntityQuery.use(delegator).select(fieldsToSelect).from("EmplTeam").where("emplTeamId",emplTeamId,"isActive","Y").queryFirst();
						if(UtilValidate.isNotEmpty(emplTeam)) {
							String teamId = emplTeam.getString("emplTeamId");
							data.put("partyId", partyId);
							data.put("teamName", emplTeam.getString("teamName"));
							data.put("emplTeamId", teamId);

							String createdOn = emplTeam.getString("createdOn");
							if (UtilValidate.isNotEmpty(createdOn)) {
								createdOn = DataUtil.convertDateTimestamp(createdOn, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
								data.put("createdDate", createdOn);
							}

							String modifiedOn = emplTeam.getString("modifiedOn");
							if (UtilValidate.isNotEmpty(modifiedOn)) {
								modifiedOn = DataUtil.convertDateTimestamp(modifiedOn, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
								data.put("modifiedDate", modifiedOn);
							}

							data.put("created", emplTeam.getString("createdBy"));
							data.put("modified", emplTeam.getString("modifiedBy"));
							if (UtilValidate.isNotEmpty(emplTeam.getString("businessUnit"))) {
								GenericValue getBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", emplTeam.getString("businessUnit")).queryOne();
								if (UtilValidate.isNotEmpty(getBu)) {
									data.put("buName", getBu.getString("productStoreGroupName"));
								}
							}
							if (UtilValidate.isNotEmpty(emplTeam.getString("isActive"))) {
								GenericValue getStatus = EntityQuery.use(delegator).from("Enumeration").where("enumCode", emplTeam.getString("isActive"), "enumTypeId", BusinessUnitConstant.STATUS_ID).queryOne();
								if (UtilValidate.isNotEmpty(getStatus)) {
									data.put("status", getStatus.getString("description"));
								}
							}

							GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", partyId, "emplTeamId",teamId).queryOne();
							if(UtilValidate.isNotEmpty(person))
								data.put("isNativeTeam", "Yes");
							else
								data.put("isNativeTeam", "No");

							dataList.add(data);
						}
					}
					result.put("highIndex", Integer.valueOf(highIndex));
					result.put("lowIndex", Integer.valueOf(lowIndex));
				}
				result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
				result.put("totalRecords", nf.format(resultListSize));
				result.put("recordCount", resultListSize);
				result.put("chunkSize", viewSize);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}

	//Author : Anagha B P, Description : Getting List of Alert Categories 
	public static String getAlertCategory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String alertName = (String) context.get("alertCategoryName");
		String alertType = (String) context.get("alertType");
		String alertPriority = (String) context.get("alertPriority");

		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(alertName)) {
				conditionlist.add(EntityCondition.makeCondition("alertCategoryId", EntityOperator.EQUALS, alertName));
			}
			if (UtilValidate.isNotEmpty(alertType)) {
				conditionlist.add(EntityCondition.makeCondition("alertTypeId", EntityOperator.EQUALS, alertType));
			}
			if (UtilValidate.isNotEmpty(alertPriority)) {
				conditionlist.add(EntityCondition.makeCondition("alertPriority", EntityOperator.EQUALS, alertPriority));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("alertCategoryId");
			fieldsToSelect.add("alertCategoryName");
			fieldsToSelect.add("alertTypeId");
			fieldsToSelect.add("alertPriority");
			fieldsToSelect.add("alertAutoClosure");
			fieldsToSelect.add("alertAutoClosureDuration");
			fieldsToSelect.add("modifiedUserLoginId");
			fieldsToSelect.add("createdByUserLoginId");
			fieldsToSelect.add("remarks");
			fieldsToSelect.add("isActive");
			fieldsToSelect.add("seqNum");
			fieldsToSelect.add("createdOn");
			fieldsToSelect.add("modifiedOn");
			fieldsToSelect.add("lastUpdatedTxStamp");
			String priority = null;
			String type = null;
			String status = null;

			List < GenericValue > alertCategories = EntityQuery.use(delegator).select(fieldsToSelect).from("AlertCategory").where(condition).maxRows(100).orderBy("-lastUpdatedTxStamp").queryList();
			if (alertCategories != null && alertCategories.size() > 0) {
				for (GenericValue alertCategory: alertCategories) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("alert", alertCategory.getString("alertCategoryName"));
					data.put("closure", alertCategory.getString("alertAutoClosure"));
					data.put("duration", alertCategory.getString("alertAutoClosureDuration"));
					data.put("sequence", alertCategory.getString("seqNum"));
					data.put("remark", alertCategory.getString("remarks"));

					String createdOn = alertCategory.getString("createdOn");
					if (UtilValidate.isNotEmpty(createdOn)) {
						createdOn = DataUtil.convertDateTimestamp(createdOn, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("createdOn", createdOn);
					}
					String modifiedOn = alertCategory.getString("modifiedOn");
					if (UtilValidate.isNotEmpty(modifiedOn)) {
						modifiedOn = DataUtil.convertDateTimestamp(modifiedOn, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("modifiedOn", modifiedOn);
					}

					data.put("createdBy", alertCategory.getString("createdByUserLoginId"));

					data.put("modifiedBy", alertCategory.getString("modifiedUserLoginId"));
					type = alertCategory.getString("alertTypeId");
					if (UtilValidate.isNotEmpty(type)) {
						GenericValue getType = EntityQuery.use(delegator).from("AlertType").where("alertTypeId", type).queryOne();
						if (UtilValidate.isNotEmpty(getType)) {
							data.put("type", getType.getString("alertTypeDescription"));
						}
					}
					priority = alertCategory.getString("alertPriority");
					if (UtilValidate.isNotEmpty(priority)) {
						GenericValue getPriority = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", priority, "enumTypeId", AlertCategoryConstant.PRIORITY), null, false));
						if (UtilValidate.isNotEmpty(getPriority)) {
							data.put("priority", getPriority.getString("description"));
						}
					}
					status = alertCategory.getString("isActive");
					if (UtilValidate.isNotEmpty(status)) {
						GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status, "enumTypeId", AlertCategoryConstant.STATUS_ID), null, false));
						if (UtilValidate.isNotEmpty(getStatus)) {
							data.put("status", getStatus.getString("description"));
						}
					}

					data.put("alertCategoryId", alertCategory.getString("alertCategoryId"));
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	//Author : Golda Mary Jose, Description : Getting SR Type
	public static String getSrType(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<Object, Object>> results = new ArrayList<Map<Object, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String srTypeId = (String) context.get("srTypeId");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(srTypeId)) {
				conditionlist.add(EntityCondition.makeCondition("code", EntityOperator.EQUALS, srTypeId));
			}
			conditionlist.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, ParamUnitConstant.SR_TYPE));

			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("value");
			fieldsToSelect.add("code");
			fieldsToSelect.add("parentValue");
			fieldsToSelect.add("parentCode");
			fieldsToSelect.add("active");
			fieldsToSelect.add("sequenceNumber");
			fieldsToSelect.add("createdOn");
			fieldsToSelect.add("createdBy");
			fieldsToSelect.add("modifiedOn");
			fieldsToSelect.add("modifiedBy");
			String status = "";


			List < GenericValue > srTypes = EntityQuery.use(delegator).select(fieldsToSelect).from("CustRequestAssoc").where(condition).maxRows(100).orderBy("sequenceNumber").queryList();

			if (srTypes != null && srTypes.size() > 0) {
				for (GenericValue srType: srTypes) {
					Map data = new HashMap();
					data.put("custRequestTypeId", srType.getString("code"));
					data.put("description", srType.getString("value"));

					data.put("seqNo", srType.getString("sequenceNumber"));
					status = srType.getString("active");

					if (UtilValidate.isNotEmpty(status)) {
						GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status, "enumTypeId", AlertCategoryConstant.STATUS_ID), null, false));
						if (UtilValidate.isNotEmpty(getStatus)) {
							data.put("status", getStatus.getString("description"));
						}
					}

					String createdDate = srType.getString("createdOn");
					if (UtilValidate.isNotEmpty(createdDate)) {
						createdDate = DataUtil.convertDateTimestamp(createdDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("createdOn", createdDate);
					}

					String modifiedDate = srType.getString("modifiedOn");
					if (UtilValidate.isNotEmpty(modifiedDate)) {
						modifiedDate = DataUtil.convertDateTimestamp(modifiedDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("modifiedOn", modifiedDate);
					}
					data.put("createdBy", srType.getString("createdBy"));
					data.put("modifiedBy", srType.getString("modifiedBy"));
					results.add(data);

				}
				results = UtilMisc.sortMaps(results ,UtilMisc.toList("seqNo"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	//Author : Golda Mary Jose, Description : Getting SR Category
	public static String getSrArea(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<Object, Object>> results = new ArrayList<Map<Object, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String srTypeId = (String) context.get("srType");
		String srCategoryId = (String) context.get("srCategory");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(srTypeId)) {
				conditionlist.add(EntityCondition.makeCondition("parentCode", EntityOperator.EQUALS, srTypeId));
			}
			if (UtilValidate.isNotEmpty(srCategoryId)) {
				conditionlist.add(EntityCondition.makeCondition("code", EntityOperator.EQUALS, srCategoryId));
			}

			conditionlist.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, ParamUnitConstant.SR_CATEGORY));

			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("value");
			fieldsToSelect.add("code");
			fieldsToSelect.add("parentValue");
			fieldsToSelect.add("parentCode");
			fieldsToSelect.add("active");
			fieldsToSelect.add("sequenceNumber");
			fieldsToSelect.add("createdOn");
			fieldsToSelect.add("createdBy");
			fieldsToSelect.add("modifiedOn");
			fieldsToSelect.add("modifiedBy");
			String status = "";

			List < GenericValue > srCategories = EntityQuery.use(delegator).select(fieldsToSelect).from("CustRequestAssoc").where(condition).maxRows(100).orderBy("sequenceNumber").queryList();
			//Debug.log("==++++++++++=="+srCategories);
			if (srCategories != null && srCategories.size() > 0) {
				for (GenericValue srCategory: srCategories) {
					Map data = new HashMap();
					data.put("parentCode", srCategory.getString("parentCode"));
					data.put("parentValue", srCategory.getString("parentValue"));
					data.put("code", srCategory.getString("code"));
					data.put("value", srCategory.getString("value"));
					data.put("seqNo", srCategory.getString("sequenceNumber"));
					status = srCategory.getString("active");

					if (UtilValidate.isNotEmpty(status)) {
						GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status, "enumTypeId", AlertCategoryConstant.STATUS_ID), null, false));
						if (UtilValidate.isNotEmpty(getStatus)) {
							data.put("status", getStatus.getString("description"));
						}
					}
					String createdDate = srCategory.getString("createdOn");
					if (UtilValidate.isNotEmpty(createdDate)) {
						createdDate = DataUtil.convertDateTimestamp(createdDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("createdOn", createdDate);
					}
					String modifiedDate = srCategory.getString("modifiedOn");
					if (UtilValidate.isNotEmpty(modifiedDate)) {
						modifiedDate = DataUtil.convertDateTimestamp(modifiedDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("modifiedOn", modifiedDate);
					}
					data.put("createdBy", srCategory.getString("createdBy"));
					data.put("modifiedBy", srCategory.getString("modifiedBy"));
					results.add(data);
				}
				results = UtilMisc.sortMaps(results ,UtilMisc.toList("seqNo"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}


	//Author : Golda Mary Jose, Description : Getting SR Sub Category
	public static String getSrSubCategory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<Object, Object>> results = new ArrayList<Map<Object, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String srTypeId = (String) context.get("srType");
		String srCategoryId = (String) context.get("srCategory");
		String srSubCategoryId = (String) context.get("srSubCategory");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			/*if (UtilValidate.isNotEmpty(srTypeId)) {
				conditionlist.add(EntityCondition.makeCondition("grandparentCode", EntityOperator.EQUALS, srTypeId));
			}*/
			if (UtilValidate.isNotEmpty(srCategoryId)) {
				conditionlist.add(EntityCondition.makeCondition("parentCode", EntityOperator.EQUALS, srCategoryId));
			}
			if (UtilValidate.isNotEmpty(srSubCategoryId)) {
				conditionlist.add(EntityCondition.makeCondition("code", EntityOperator.EQUALS, srSubCategoryId));
			}

			conditionlist.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, ParamUnitConstant.SR_SUB_CATEGORY));


			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("value");
			fieldsToSelect.add("code");
			fieldsToSelect.add("parentValue");
			fieldsToSelect.add("parentCode");
			fieldsToSelect.add("grandparentValue");
			fieldsToSelect.add("grandparentCode");
			fieldsToSelect.add("active");
			fieldsToSelect.add("sequenceNumber");
			fieldsToSelect.add("createdOn");
			fieldsToSelect.add("createdBy");
			fieldsToSelect.add("modifiedOn");
			fieldsToSelect.add("modifiedBy");
			String status = "";
			List < GenericValue > srSubCategories = EntityQuery.use(delegator).select(fieldsToSelect).from("CustRequestAssoc").where(condition).maxRows(100).orderBy("sequenceNumber").queryList();
			Debug.log("srSubCategories----------"+srSubCategories);
			//Debug.log("==*************=="+srSubAreas);
			if (srSubCategories != null && srSubCategories.size() > 0) {
				for (GenericValue srSubCategory: srSubCategories) {
					Map data = new HashMap();
					data.put("custRequestCategoryId", srSubCategory.getString("code"));
					data.put("value", srSubCategory.getString("value"));
					data.put("parentCustRequestCategoryId", srSubCategory.getString("parentCode"));
					data.put("parentValue", srSubCategory.getString("parentValue"));
					//data.put("custRequestTypeId", srSubCategory.getString("grandparentCode"));
					data.put("grandparentValue", srSubCategory.getString("grandparentValue"));
					data.put("seqNo", srSubCategory.getString("sequenceNumber"));

					String createdDate = srSubCategory.getString("createdOn");
					if (UtilValidate.isNotEmpty(createdDate)) {
						createdDate = DataUtil.convertDateTimestamp(createdDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("createdOn", createdDate);
					}
					String modifiedDate = srSubCategory.getString("modifiedOn");
					if (UtilValidate.isNotEmpty(modifiedDate)) {
						modifiedDate = DataUtil.convertDateTimestamp(modifiedDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("modifiedOn", modifiedDate);
					}
					data.put("createdBy", srSubCategory.getString("createdBy"));
					data.put("modifiedBy", srSubCategory.getString("modifiedBy"));
					status = srSubCategory.getString("active");

					if (UtilValidate.isNotEmpty(status)) {
						GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status, "enumTypeId", AlertCategoryConstant.STATUS_ID), null, false));
						if (UtilValidate.isNotEmpty(getStatus)) {
							data.put("status", getStatus.getString("description"));

						}
					}
					results.add(data);
				}
				results = UtilMisc.sortMaps(results ,UtilMisc.toList("seqNo"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	//Author : Golda Mary Jose, Description : Getting Activity Parent
	public static String getActivityParent(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String parentId = (String) context.get("activityParent");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(parentId)) {
				conditionlist.add(EntityCondition.makeCondition("enumId", EntityOperator.EQUALS, parentId));
			}

			conditionlist.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, ParamUnitConstant.IA_TYPE));

			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("enumId");
			fieldsToSelect.add("description");
			//fieldsToSelect.add("isEnabled");
			fieldsToSelect.add("sequenceId");
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			List < GenericValue > parentActivitys = EntityQuery.use(delegator).select(fieldsToSelect).from("Enumeration").where(condition).maxRows(100).orderBy("-lastUpdatedTxStamp").queryList();
			// Debug.log("==parentActivitys=="+parentActivitys);
			if (parentActivitys != null && parentActivitys.size() > 0) {
				for (GenericValue parentActivity: parentActivitys) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("enumId", parentActivity.getString("enumId"));
					data.put("activityParentDesc", parentActivity.getString("description"));

					data.put("sequenceId", parentActivity.getString("sequenceId"));
					String createdOn = "";
					String createdBy = "";
					String modifiedOn = "";
					String modifiedBy = "";
					if (UtilValidate.isNotEmpty(parentActivity.getString("enumId"))) {
						GenericValue getParentActivity = EntityUtil.getFirst(delegator.findByAnd("WorkEffortAssocTriplet", UtilMisc.toMap("code", parentActivity.getString("enumId"), "type", ParamUnitConstant.RELATED_TO), null, false));
						if (UtilValidate.isNotEmpty(getParentActivity)) {
							createdOn = getParentActivity.getString("createdOn");
							createdBy = getParentActivity.getString("createdBy");
							modifiedOn = getParentActivity.getString("modifiedOn");
							modifiedBy = getParentActivity.getString("modifiedBy");
							String status = getParentActivity.getString("active");
							String statusDesc = "";
							GenericValue statusDetails = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status, "enumTypeId", ParamUnitConstant.STATUS_ID), null, false));
							if (statusDetails != null) {
								statusDesc = statusDetails.getString("description");
							}
							data.put("statusDesc", statusDesc);
							data.put("createdOn", createdOn);
							data.put("createdBy", createdBy);
							data.put("modifiedOn", modifiedOn);
							data.put("modifiedBy", modifiedBy);
							results.add(data);
							//Debug.log("==results=="+results);
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	//Author : Golda Mary Jose, Description : Getting Activity Type
	public static String getActivityType(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String parentId = (String) context.get("activityParent");
		String typeId = (String) context.get("activityType");

		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(typeId)) {
				conditionlist.add(EntityCondition.makeCondition("code", EntityOperator.EQUALS, typeId));
			}
			if (UtilValidate.isNotEmpty(parentId)) {
				conditionlist.add(EntityCondition.makeCondition("parentCode", EntityOperator.EQUALS, parentId));
			}

			conditionlist.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, ParamUnitConstant.TYPE));

			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("code");
			fieldsToSelect.add("value");
			fieldsToSelect.add("parentCode");
			fieldsToSelect.add("parentValue");
			fieldsToSelect.add("active");
			fieldsToSelect.add("sequenceNumber");
			fieldsToSelect.add("createdOn");
			fieldsToSelect.add("createdBy");
			fieldsToSelect.add("modifiedOn");
			fieldsToSelect.add("modifiedBy");
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			List < GenericValue > parentActivitys = EntityQuery.use(delegator).select(fieldsToSelect).from("WorkEffortAssocTriplet").where(condition).maxRows(100).orderBy("-lastUpdatedTxStamp").queryList();
			// Debug.log("==parentActivitys=="+parentActivitys);
			if (parentActivitys != null && parentActivitys.size() > 0) {
				for (GenericValue parentActivity: parentActivitys) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("enumId", parentActivity.getString("code"));
					data.put("parentEnumId", parentActivity.getString("parentCode"));
					data.put("activityTypeDesc", parentActivity.getString("value"));
					String status = parentActivity.getString("active");
					String statusDesc = "";
					GenericValue statusDetails = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status, "enumTypeId", ParamUnitConstant.STATUS_ID), null, false));
					if (statusDetails != null) {
						statusDesc = statusDetails.getString("description");
					}
					data.put("statusDesc", statusDesc);
					data.put("sequenceId", parentActivity.getString("sequenceNumber"));

					data.put("parentValue", parentActivity.getString("parentValue"));
					data.put("createdOn", parentActivity.getString("createdOn"));
					data.put("createdBy", parentActivity.getString("createdBy"));
					data.put("modifiedOn", parentActivity.getString("modifiedOn"));
					data.put("modifiedBy", parentActivity.getString("modifiedBy"));
					results.add(data);
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	//Author : Golda Mary Jose, Description : Getting Activity Sub Type
	public static String getActivitySubType(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String parentId = (String) context.get("activityParent");
		String typeId = (String) context.get("activityType");
		String subTypeId = (String) context.get("activitySubType");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(parentId)) {
				conditionlist.add(EntityCondition.makeCondition("grandparentCode", EntityOperator.EQUALS, parentId));

			}
			if (UtilValidate.isNotEmpty(subTypeId)) {
				conditionlist.add(EntityCondition.makeCondition("code", EntityOperator.EQUALS, subTypeId));
			}

			if (UtilValidate.isNotEmpty(typeId)) {
				conditionlist.add(EntityCondition.makeCondition("parentCode", EntityOperator.EQUALS, typeId));
			}


			conditionlist.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, ParamUnitConstant.SUB_TYPE));

			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("code");
			fieldsToSelect.add("value");
			fieldsToSelect.add("parentCode");
			fieldsToSelect.add("parentValue");
			fieldsToSelect.add("active");
			fieldsToSelect.add("sequenceNumber");
			fieldsToSelect.add("createdOn");
			fieldsToSelect.add("createdBy");
			fieldsToSelect.add("modifiedOn");
			fieldsToSelect.add("modifiedBy");
			fieldsToSelect.add("grandparentValue");
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			List < GenericValue > parentActivitys = EntityQuery.use(delegator).select(fieldsToSelect).from("WorkEffortAssocTriplet").where(condition).maxRows(100).orderBy("-lastUpdatedTxStamp").queryList();
			// Debug.log("==parentActivitys=="+parentActivitys);
			if (parentActivitys != null && parentActivitys.size() > 0) {
				for (GenericValue parentActivity: parentActivitys) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("enumId", parentActivity.getString("code"));
					data.put("parentEnumId", parentActivity.getString("parentCode"));
					data.put("activitySubTypeDesc", parentActivity.getString("value"));
					String status = parentActivity.getString("active");

					String statusDesc = "";
					GenericValue statusDetails = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status, "enumTypeId", ParamUnitConstant.STATUS_ID), null, false));
					if (statusDetails != null) {
						statusDesc = statusDetails.getString("description");
						data.put("statusDesc", statusDesc);
					}

					data.put("sequenceId", parentActivity.getString("sequenceNumber"));

					data.put("parentValue", parentActivity.getString("parentValue"));
					data.put("grandparentValue", parentActivity.getString("grandparentValue"));
					data.put("createdOn", parentActivity.getString("createdOn"));
					data.put("createdBy", parentActivity.getString("createdBy"));
					data.put("modifiedOn", parentActivity.getString("modifiedOn"));
					data.put("modifiedBy", parentActivity.getString("modifiedBy"));
					results.add(data);
					//Debug.log("==results=="+results);
				}
			}
			//Debug.log("==results=="+results);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	//Author : Golda Mary Jose, Description : Drop down heirarchy.
	public static String getCategoryList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		//String srTypeId=request.getParameter("typeId");
		//String srType = srTypeId.substring(srTypeId.indexOf("(")+1, srTypeId.indexOf(")"));
		//String custRequestTypeId = srTypeId.substring(0, srTypeId.indexOf("("));
		String custRequestTypeId = request.getParameter("typeId");
		Debug.log("custRequestTypeId===" + custRequestTypeId);
		if (UtilValidate.isNotEmpty(custRequestTypeId)) {
			try {
				List < GenericValue > srcategories = delegator.findByAnd("CustRequestAssoc", UtilMisc.toMap("parentCode", custRequestTypeId, "type", AdminPortalConstant.ParamUnitConstant.SR_CATEGORY, "active", "Y"), null, false);
				if (UtilValidate.isNotEmpty(srcategories)) {
					for (GenericValue category: srcategories) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("code", category.getString("code"));
						data.put("description", category.getString("value"));
						results.add(data);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return doJSONResponse(response, e.getMessage());
			}
		}
		return doJSONResponse(response, results);
	}
	public static String getsubCategoryList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		//String srTypeId=request.getParameter("typeId");
		// String srType = srTypeId.substring(srTypeId.indexOf("(")+1, srTypeId.indexOf(")"));
		// String custRequestTypeId = srTypeId.substring(0, srTypeId.indexOf("("));
		String custRequestTypeId = request.getParameter("typeId");
		Debug.log("custRequestTypeId===" + custRequestTypeId);
		if (UtilValidate.isNotEmpty(custRequestTypeId)) {
			try {
				List < GenericValue > srcategories = delegator.findByAnd("CustRequestAssoc", UtilMisc.toMap("parentCode", custRequestTypeId, "type", AdminPortalConstant.ParamUnitConstant.SR_CATEGORY, "active", "Y"), null, false);
				if (UtilValidate.isNotEmpty(srcategories)) {
					for (GenericValue category: srcategories) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("code", category.getString("code"));
						data.put("description", category.getString("value"));
						results.add(data);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return doJSONResponse(response, e.getMessage());
			}
		}
		return doJSONResponse(response, results);
	}
	//Author : Anagha B P, Description : Getting List of Product Master 
	/*public static String getProductMaster(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
        String productCode = (String) context.get("productCode");
        String productName = (String) context.get("productName");
        String productSubCategory = (String) context.get("productSubCategory");
        try {
        	 List < EntityCondition > conditionlist = FastList.newInstance();
             if (UtilValidate.isNotEmpty(productCode)) {
                 conditionlist.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productCode));
             }
             if (UtilValidate.isNotEmpty(productName)) {
                 conditionlist.add(EntityCondition.makeCondition("productName", EntityOperator.EQUALS, productName));
             }
             if (UtilValidate.isNotEmpty(productSubCategory)) {
                 conditionlist.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, productSubCategory));
             }
            EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
            Set<String> fieldsToSelect = new TreeSet<String>();
            fieldsToSelect.add("productId");
            fieldsToSelect.add("productName");
            fieldsToSelect.add("primaryProductCategoryId");
            fieldsToSelect.add("productType");
            fieldsToSelect.add("productSourceSystem");
            fieldsToSelect.add("schemeCode");
            fieldsToSelect.add("isActive");
            fieldsToSelect.add("seqNum");
            fieldsToSelect.add("createdOn");
            fieldsToSelect.add("modifiedOn");
            fieldsToSelect.add("createdByUserLoginId");
            fieldsToSelect.add("modifiedUserLoginId");
            fieldsToSelect.add("lastUpdatedTxStamp");
            String category=null;
            String status=null;
            List<GenericValue> productMasters = EntityQuery.use(delegator).select(fieldsToSelect).from("Product").where(condition).orderBy("-lastUpdatedTxStamp").queryList();
            if(productMasters != null && productMasters.size() > 0)
            {
                for(GenericValue productMaster : productMasters) {
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("productCode", productMaster.getString("productId"));
                    Debug.log("product id===="+productMaster.getString("productId"));
                    data.put("productsName", productMaster.getString("productName"));
                    data.put("levelOne", productMaster.getString("productType"));
                    data.put("sequenceNumber", productMaster.getString("seqNum"));
                    data.put("createdOn", productMaster.getString("createdOn"));
                    data.put("createdBy", productMaster.getString("createdByUserLoginId"));
					data.put("modifiedOn", productMaster.getString("modifiedOn"));
					data.put("modifiedBy", productMaster.getString("modifiedUserLoginId"));
					data.put("source", productMaster.getString("productSourceSystem"));
					data.put("scheme", productMaster.getString("schemeCode"));
					category=productMaster.getString("primaryProductCategoryId");
                    if(UtilValidate.isNotEmpty(category)){
                        GenericValue getCategory = EntityQuery.use(delegator).from("ProductCategory").where("productCategoryId", category).queryOne();
                        if(UtilValidate.isNotEmpty(getCategory)){
                        	data.put("categoryId", getCategory.getString("categoryName"));
                        }
                    }
                    status=productMaster.getString("isActive");
                    if(UtilValidate.isNotEmpty(status)){
                        GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status,"enumTypeId",AlertCategoryConstant.STATUS_ID), null, false));
                        if (UtilValidate.isNotEmpty(getStatus)){
                            data.put("status",getStatus.getString("description"));
                        }
                    }

                    data.put("productId", productMaster.getString("productId"));
                    results.add(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return doJSONResponse(response, e.getMessage());
        }
        return doJSONResponse(response, results);
    }*/
	//Author : Anagha B P, Description : Getting List of Members
	public static String getMembers(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> requestParameters = UtilHttp.getCombinedMap(request);
		String teamId = (String) requestParameters.get("emplTeamId");
		String businessid = null;
		try {
			List < EntityCondition > partyconditions = new ArrayList<EntityCondition>();
			GenericValue getBudesc = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId", teamId).queryOne();
			if (UtilValidate.isNotEmpty(getBudesc)) {
				businessid = getBudesc.getString("businessUnit");
				//Debug.log("businessid==="+businessid);
				//if(UtilValidate.isNotEmpty(businessid)) {
				//	partyconditions.add(EntityCondition.makeCondition("businessUnit",EntityOperator.EQUALS,businessid));
				if (UtilValidate.isNotEmpty(businessid)) //} 
				{
					Set < String > fieldToSelect = new TreeSet < String > ();
					fieldToSelect.add("partyId");
					fieldToSelect.add("userLoginId");
					fieldToSelect.add("enabled");
					fieldToSelect.add("firstName");
					fieldToSelect.add("lastName");
					fieldToSelect.add("businessUnit");
					//EntityCondition usercondition = EntityCondition.makeCondition(partyconditions, EntityOperator.AND);
					List < GenericValue > userList = EntityQuery.use(delegator).select(fieldToSelect).from("UserLoginPerson").where("businessUnit", businessid).queryList();
					for (GenericValue user: userList) {
						Map<String, Object> data = new HashMap<String, Object>();
						String userPartyId = user.getString("partyId");
						String enabled = user.getString("enabled");
						String firstName = user.getString("firstName");
						String lastName = user.getString("lastName");
						String userName = firstName + (UtilValidate.isNotEmpty(lastName) ? " " + lastName : "");
						data.put("partyId", userPartyId);
						data.put("userName", userName);
						data.put("oneBankId", UtilValidate.isNotEmpty(user.getString("userLoginId")) ? user.getString("userLoginId") : "");
						data.put("businessUnit", UtilValidate.isNotEmpty(user.getString("businessUnit")) ? user.getString("businessUnit") : "");
						results.add(data);
					}
				}
			}
			//Debug.log("Results : "+results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	public static String getTypeList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		//String srTypeId=request.getParameter("typeId");
		//String srType = srTypeId.substring(srTypeId.indexOf("(")+1, srTypeId.indexOf(")"));
		//String custRequestTypeId = srTypeId.substring(0, srTypeId.indexOf("("));
		String parentId = request.getParameter("activityParent");
		if (UtilValidate.isNotEmpty(parentId)) {
			try {
				List < GenericValue > types = delegator.findByAnd("WorkEffortAssocTriplet", UtilMisc.toMap("parentCode", parentId, "type", AdminPortalConstant.ParamUnitConstant.TYPE,"active", "Y"), null, false);
				if (UtilValidate.isNotEmpty(types)) {
					for (GenericValue type: types) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("code", type.getString("code"));
						data.put("description", type.getString("value"));
						results.add(data);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return doJSONResponse(response, e.getMessage());
			}
		}
		return doJSONResponse(response, results);
	}
	public static String getSubTypeList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		//String srTypeId=request.getParameter("typeId");
		// String srType = srTypeId.substring(srTypeId.indexOf("(")+1, srTypeId.indexOf(")"));
		// String custRequestTypeId = srTypeId.substring(0, srTypeId.indexOf("("));
		String activityParent = request.getParameter("activityParent");
		if (UtilValidate.isNotEmpty(activityParent)) {
			try {
				List < GenericValue > srtypes = delegator.findByAnd("WorkEffortAssocTriplet", UtilMisc.toMap("parentCode", activityParent, "type", AdminPortalConstant.ParamUnitConstant.TYPE, "active", "Y"), null, false);
				if (UtilValidate.isNotEmpty(srtypes)) {
					for (GenericValue type: srtypes) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("code", type.getString("code"));
						data.put("description", type.getString("value"));
						results.add(data);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return doJSONResponse(response, e.getMessage());
			}
		}
		return doJSONResponse(response, results);
	}
	
	//Author : Golda Mary Jose, Description : Entity Audit Logs
	public static String getAuditConfigurations(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		//List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> results = new HashMap<String, Object>();

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat df3 = new SimpleDateFormat("dd/MM/yyyy HH:mm");


		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String entityId = (String) context.get("entity");
		String fieldId = (String) context.get("field");
		String startDateId = (String) context.get("startDate");
		String endDateId = (String) context.get("endDate");
		try {

			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(entityId)) {
				conditionlist.add(EntityCondition.makeCondition("changedEntityName", EntityOperator.EQUALS, entityId));

			}
			if (UtilValidate.isNotEmpty(fieldId)) {
				conditionlist.add(EntityCondition.makeCondition("changedFieldName", EntityOperator.EQUALS, fieldId));
			}

			if(UtilValidate.isNotEmpty(startDateId)) {
				startDateId = df1.format(df2.parse(startDateId));
				conditionlist.add(EntityCondition.makeCondition("changedDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayEnd(Timestamp.valueOf(startDateId))));
			}

			if (UtilValidate.isNotEmpty(endDateId)) {
				endDateId = df1.format(df2.parse(endDateId));
				conditionlist.add(EntityCondition.makeCondition("changedDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(Timestamp.valueOf(endDateId))));
			}
			
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("auditHistorySeqId");
			fieldsToSelect.add("changedEntityName");
			fieldsToSelect.add("changedFieldName");
			fieldsToSelect.add("oldValueText");
			fieldsToSelect.add("newValueText");
			fieldsToSelect.add("changedDate");
			fieldsToSelect.add("changedByInfo");
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			//if(UtilValidate.isNotEmpty(condition)) {

				String totalGridFetch = org.fio.homeapps.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit");
				int fioGridFetch = UtilValidate.isNotEmpty(totalGridFetch) ? Integer.parseInt(totalGridFetch) : 1000;

				List < GenericValue > auditLogs = EntityQuery.use(delegator).select(fieldsToSelect).from("EntityAuditLog").where(condition).limit(fioGridFetch).orderBy("-auditHistorySeqId").queryList();
				if (auditLogs != null && auditLogs.size() > 0) {
					for (GenericValue auditLog: auditLogs) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("auditHistorySeqId", auditLog.getString("auditHistorySeqId"));
						data.put("changedEntityName", auditLog.getString("changedEntityName"));
						data.put("changedFieldName", auditLog.getString("changedFieldName"));
						data.put("oldValueText", auditLog.getString("oldValueText"));
						data.put("newValueText", auditLog.getString("newValueText"));
						String modifiedDate = auditLog.getString("changedDate");
						if (UtilValidate.isNotEmpty(modifiedDate)) {
							modifiedDate = DataUtil.convertDateTimestamp(modifiedDate, df3, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
							data.put("changedDate", modifiedDate);
						}
						data.put("changedByInfo", auditLog.getString("changedByInfo"));
						dataList.add(data);
						//results.add(data);
						//Debug.log("==results=="+results);
					}
				}
				results.put("data", dataList);
			//}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	public static String getScreenConfigsThroughLS(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String clsId = (String) context.get("clsId");
		String mountPoint = (String) context.get("mountPoint");
		String layout = (String) context.get("layout");
		String screen = (String) context.get("screen");

		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(clsId))
				conditions.add(EntityCondition.makeCondition("clsId",EntityOperator.EQUALS,clsId));
			if (UtilValidate.isNotEmpty(mountPoint))
				conditions.add(EntityCondition.makeCondition("mountPoint",EntityOperator.EQUALS,mountPoint));
			if (UtilValidate.isNotEmpty(layout))
				conditions.add(EntityCondition.makeCondition("layout",EntityOperator.EQUALS,layout));
			if (UtilValidate.isNotEmpty(screen))
				conditions.add(EntityCondition.makeCondition("screen",EntityOperator.EQUALS,screen));

			GenericValue ComponentLayoutScreen = EntityUtil.getFirst(EntityQuery.use(delegator).from("ComponentLayoutScreen").
					where(EntityCondition.makeCondition(conditions,EntityOperator.AND)).queryList());

			/*GenericValue ComponentLayoutScreen = EntityUtil.getFirst(delegator.findByAnd(
                    "ComponentLayoutScreen", UtilMisc.toMap("mountPoint", mountPoint,
                            "layout", layout,
                            "screen", screen),null, false));*/
			if (UtilValidate.isNotEmpty(ComponentLayoutScreen)) {
				clsId = ComponentLayoutScreen.getString("clsId");
			}
			List<GenericValue> screenSpecifications = delegator.findByAnd("ScreenSpecification",
					UtilMisc.toMap("clsId", clsId), null, false);

			for (GenericValue Entry: screenSpecifications) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("clsId", Entry.getString("clsId"));
				data.put("fieldId", Entry.getString("fieldId"));
				data.put("dataType", Entry.getString("dataType"));
				data.put("sequenceNum", Entry.getString("sequenceNum"));
				data.put("isDisabled", Entry.getString("isDisabled"));
				data.put("isMandatory", Entry.getString("isMandatory"));
				data.put("fieldName", Entry.getString("fieldName"));
				data.put("isCreate", Entry.getString("isCreate"));
				data.put("isView", Entry.getString("isView"));
				data.put("isEdit", Entry.getString("isEdit"));
				data.put("fieldService", Entry.getString("fieldService"));
				results.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getOpportunityConfigData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String enumTypeId = (String) context.get("enumTypeId");
		String enumId = (String) context.get("enumId");
		String parentEnumId = (String) context.get("parentEnumId");
		String oppoResponseReasonId = (String) context.get("oppoResponseReasonId");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(enumTypeId)) {
				conditions.add(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,enumTypeId));
			}else {
				conditions.add(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,null));
			}
			if (UtilValidate.isNotEmpty(enumId)) {
				conditions.add(EntityCondition.makeCondition("enumId",EntityOperator.EQUALS,enumId));
			}
			if (UtilValidate.isNotEmpty(oppoResponseReasonId)) {
				conditions.add(EntityCondition.makeCondition("enumId",EntityOperator.EQUALS,oppoResponseReasonId));
			}
			Set < String > fieldToSelect = new TreeSet < String > ();
			fieldToSelect.add("enumId");
			fieldToSelect.add("enumTypeId");
			fieldToSelect.add("description");
			fieldToSelect.add("sequenceId");
			fieldToSelect.add("isMultiLingual");
			fieldToSelect.add("createdStamp");
			fieldToSelect.add("lastUpdatedStamp");
			fieldToSelect.add("isEnabled");
			fieldToSelect.add("parentEnumId");

			EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < GenericValue > enumList = EntityQuery.use(delegator).select(fieldToSelect).from("Enumeration").where(mainCondition).maxRows(2000).orderBy("-enumId").queryList();
			for (GenericValue eachValue: enumList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String enumIdValue = eachValue.getString("enumId");
				String enumTypeIdValue = eachValue.getString("enumTypeId");
				String description = eachValue.getString("description");
				String sequenceId = eachValue.getString("sequenceId");
				String isMultiLingual = eachValue.getString("isMultiLingual");
				String status = eachValue.getString("isEnabled");
				String responseTypeParentId = eachValue.getString("parentEnumId");
				if(UtilValidate.isNotEmpty(responseTypeParentId)) {
					GenericValue callOutComes = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", responseTypeParentId), true);
					String outComeId = callOutComes.getString("parentEnumId");
					data.put("outComeId", outComeId);
					GenericValue enumDescOutCome = EntityQuery.use(delegator).from("Enumeration").where("enumId", outComeId).queryOne();
					if (UtilValidate.isNotEmpty(enumDescOutCome)) {
						if (UtilValidate.isNotEmpty(enumDescOutCome.get("description"))) {
							data.put("callOutcomeDescription", enumDescOutCome.get("description"));
						}
					}

				}

				String createdDate = eachValue.getString("createdStamp");
				if (UtilValidate.isNotEmpty(createdDate)) {
					createdDate = DataUtil.convertDateTimestamp(createdDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					data.put("createdOn", createdDate);
				}

				String lastUpdatedDate = eachValue.getString("lastUpdatedStamp");
				if (UtilValidate.isNotEmpty(lastUpdatedDate)) {
					lastUpdatedDate = DataUtil.convertDateTimestamp(lastUpdatedDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					data.put("modifiedOn", lastUpdatedDate);
				}

				GenericValue enumDesc = EntityQuery.use(delegator).from("Enumeration").where("enumId", responseTypeParentId).queryOne();

				if (UtilValidate.isNotEmpty(enumDesc)) {
					if (UtilValidate.isNotEmpty(enumDesc.get("description"))) {
						data.put("responseTypeDescription", enumDesc.get("description"));
					}
				}
				data.put("enumId", enumIdValue);
				data.put("parentEnumId", responseTypeParentId);
				data.put("enumTypeId", enumTypeIdValue);
				data.put("description", description);
				data.put("sequenceId", sequenceId);
				data.put("isMultiLingual", isMultiLingual);
				data.put("status", status);
				results.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	public static String getOpportunityConfigDataResponseType(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String enumTypeId = (String) context.get("enumTypeId");
		String enumId = (String) context.get("enumId");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(enumTypeId)) {
				conditions.add(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,enumTypeId));
			}else {
				conditions.add(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,null));
			}
			if (UtilValidate.isNotEmpty(enumId)) {
				conditions.add(EntityCondition.makeCondition("enumId",EntityOperator.EQUALS,enumId));
			}
			Set < String > fieldToSelect = new TreeSet < String > ();
			fieldToSelect.add("enumId");
			fieldToSelect.add("enumTypeId");
			fieldToSelect.add("description");
			fieldToSelect.add("sequenceId");
			fieldToSelect.add("isMultiLingual");
			fieldToSelect.add("createdStamp");
			fieldToSelect.add("lastUpdatedStamp");
			fieldToSelect.add("isEnabled");
			fieldToSelect.add("parentEnumId");
			EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < GenericValue > enumList = EntityQuery.use(delegator).select(fieldToSelect).from("Enumeration").where(mainCondition).maxRows(2000).orderBy("-enumId").queryList();
			for (GenericValue eachValue: enumList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String enumIdValue = eachValue.getString("enumId");
				String enumTypeIdValue = eachValue.getString("enumTypeId");
				String description = eachValue.getString("description");
				String sequenceId = eachValue.getString("sequenceId");
				String isMultiLingual = eachValue.getString("isMultiLingual");
				String status = eachValue.getString("isEnabled");
				String parentEnumId = eachValue.getString("parentEnumId");
				String createdDate = eachValue.getString("createdStamp");
				if (UtilValidate.isNotEmpty(createdDate)) {
					createdDate = DataUtil.convertDateTimestamp(createdDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					data.put("createdOn", createdDate);
				}
				String lastUpdatedDate = eachValue.getString("lastUpdatedStamp");
				if (UtilValidate.isNotEmpty(lastUpdatedDate)) {
					lastUpdatedDate = DataUtil.convertDateTimestamp(lastUpdatedDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
					data.put("modifiedOn", lastUpdatedDate);
				}
				GenericValue enum2 = EntityQuery.use(delegator).from("Enumeration").where("enumId", parentEnumId).queryOne();
				if (UtilValidate.isNotEmpty(enum2)) {
					if (UtilValidate.isNotEmpty(enum2.get("description"))) {
						data.put("callOutcomeDescription", enum2.get("description"));
						data.put("parentEnumId", enum2.get("enumId"));
					}
				}
				data.put("enumId", enumIdValue);
				data.put("enumTypeId", enumTypeIdValue);
				data.put("description", description);
				results.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	public static String createOpportunityResponseType(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map < String, Object >> results = new ArrayList < Map < String, Object >> ();
		Map < String, Object > context = UtilHttp.getCombinedMap(request);
		String parentEnumId = (String) context.get("responseTypeId");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(parentEnumId)) {
				conditionlist.add(EntityCondition.makeCondition("parentEnumId", EntityOperator.EQUALS, parentEnumId));
			}
			conditionlist.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "RESPONSE_REASON_ID"));

			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("description");
			fieldsToSelect.add("enumId");
			List < GenericValue > oppResponseReasons = EntityQuery.use(delegator).select(fieldsToSelect).from("Enumeration").where(condition).queryList();
			if (UtilValidate.isNotEmpty(oppResponseReasons)) {
				for (GenericValue oppResponseReason: oppResponseReasons) {
					Map < String, Object > data = new HashMap < String, Object > ();
					data.put("description",oppResponseReason.getString("description"));
					data.put("enumId",oppResponseReason.getString("enumId"));
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	public static String getOpportunityResponseType(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map < String, Object >> results = new ArrayList < Map < String, Object >> ();
		Map < String, Object > context = UtilHttp.getCombinedMap(request);
		String enumId = (String) context.get("enumId");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(enumId)) {
				conditionlist.add(EntityCondition.makeCondition("parentEnumId", EntityOperator.EQUALS, enumId));
			}
			conditionlist.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "OPP_RESPONSE_TYPE"));
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("description");
			fieldsToSelect.add("enumId");
			List < GenericValue > oppResponseTypes = EntityQuery.use(delegator).select(fieldsToSelect).from("Enumeration").where(condition).queryList();
			if (UtilValidate.isNotEmpty(oppResponseTypes)) {
				for (GenericValue oppResponseType: oppResponseTypes) {
					Map < String, Object > data = new HashMap < String, Object > ();
					data.put("description",oppResponseType.getString("description"));
					data.put("enumId",oppResponseType.getString("enumId"));

					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	public static String getOppoResponseReasonType(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map < String, Object >> results = new ArrayList < Map < String, Object >> ();
		Map < String, Object > context = UtilHttp.getCombinedMap(request);
		String enumId = (String) context.get("oppoCallOutcome");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(enumId)) {
				conditionlist.add(EntityCondition.makeCondition("parentEnumId", EntityOperator.EQUALS, enumId));
			}
			conditionlist.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "OPP_RESPONSE_TYPE"));
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("description");
			fieldsToSelect.add("enumId");
			List < GenericValue > oppResponseTypes = EntityQuery.use(delegator).select(fieldsToSelect).from("Enumeration").where(condition).queryList();
			if (UtilValidate.isNotEmpty(oppResponseTypes)) {
				for (GenericValue oppResponseType: oppResponseTypes) {
					Map < String, Object > data = new HashMap < String, Object > ();
					data.put("description",oppResponseType.getString("description"));
					data.put("enumId",oppResponseType.getString("enumId"));

					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, results);
	}



	public static String getOpportunityResponseReason(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map < String, Object >> results = new ArrayList < Map < String, Object >> ();
		Map < String, Object > context = UtilHttp.getCombinedMap(request);
		String parentEnumId = (String) context.get("responseTypeId");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(parentEnumId)) {
				conditionlist.add(EntityCondition.makeCondition("parentEnumId", EntityOperator.EQUALS, parentEnumId));
			}
			conditionlist.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "RESPONSE_REASON_ID"));

			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("description");
			fieldsToSelect.add("enumId");
			List < GenericValue > oppResponseReasons = EntityQuery.use(delegator).select(fieldsToSelect).from("Enumeration").where(condition).queryList();
			if (UtilValidate.isNotEmpty(oppResponseReasons)) {
				for (GenericValue oppResponseReason: oppResponseReasons) {
					Map < String, Object > data = new HashMap < String, Object > ();
					data.put("description",oppResponseReason.getString("description"));
					data.put("enumId",oppResponseReason.getString("enumId"));
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	public static String getGlobalMessageConfigData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String roleTypeId = (String) context.get("roleTypeId");
		String partyId = (String) context.get("partyId");
		String componentId = (String) context.get("componentId");


		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				conditions.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,roleTypeId));
			}
			if (UtilValidate.isNotEmpty(partyId)) {
				conditions.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
			}
			if (UtilValidate.isNotEmpty(componentId)) {
				conditions.add(EntityCondition.makeCondition("componentId",EntityOperator.EQUALS,componentId));
			}
			Timestamp thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)));
			Set < String > fieldToSelect = new TreeSet < String > ();
			fieldToSelect.add("roleTypeId");
			fieldToSelect.add("partyId");
			fieldToSelect.add("description");
			fieldToSelect.add("componentId");
			fieldToSelect.add("fromDate");
			fieldToSelect.add("thruDate");
			fieldToSelect.add("isEnabled");

			EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < GenericValue > globalMessageConfigurationDetailsList = EntityQuery.use(delegator).select(fieldToSelect).from("GlobalMessageConfig").where(mainCondition).queryList();

			for (GenericValue globalMsgConfig: globalMessageConfigurationDetailsList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String componentIdValue = globalMsgConfig.getString("componentId");
				Timestamp fromDateValue = (Timestamp)globalMsgConfig.get("fromDate");
				String description = globalMsgConfig.getString("description");
				String roleTypeIdValue = globalMsgConfig.getString("roleTypeId");
				String eachPartyId = globalMsgConfig.getString("partyId");
				String isEnabled = globalMsgConfig.getString("isEnabled");

				if(UtilValidate.isNotEmpty(componentIdValue)) {
					data.put("componentId", componentIdValue);

					GenericValue OfbizComponentAccessData = EntityQuery.use(delegator).from("OfbizComponentAccess").where("componentId", componentIdValue).queryOne();
					if (UtilValidate.isNotEmpty(OfbizComponentAccessData)) {
						String componentName= OfbizComponentAccessData.getString("componentName");
						data.put("componentName", componentName);
					}

				}

				if(UtilValidate.isNotEmpty(isEnabled)) {
					data.put("isEnabled", isEnabled);

				}
				if(UtilValidate.isNotEmpty(eachPartyId)) {
					data.put("partyId", eachPartyId);
					String partyName = PartyHelper.getPartyName(delegator, eachPartyId, true);
					data.put("partyName", partyName);
				}

				String fromDateStr=UtilDateTime.toDateString(fromDateValue,"dd/MM/yyyy");

				if (UtilValidate.isNotEmpty(fromDateValue)) {
					data.put("fromDate", fromDateStr);
				}

				Timestamp thruDateValue = (Timestamp)globalMsgConfig.get("thruDate");
				if (UtilValidate.isNotEmpty(thruDateValue)) {
					String thruDateStr=UtilDateTime.toDateString(thruDateValue,"dd/MM/yyyy");
					data.put("thruDate", thruDateStr);
				}

				if (UtilValidate.isNotEmpty(description)) {
					data.put("description", description);
				}

				if (UtilValidate.isNotEmpty(roleTypeIdValue)) {
					data.put("roleTypeId", roleTypeIdValue);
					GenericValue roleTypeData = EntityQuery.use(delegator).from("Enumeration").where("enumId", roleTypeIdValue).queryOne();
					if (UtilValidate.isNotEmpty(roleTypeData)) {
						String roleTypeIdDescription= roleTypeData.getString("description");
						data.put("roleTypeIdDescription", roleTypeIdDescription);
					}
				} 


				results.add(data);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}


	public static String getPerson(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		// SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String name = (String) context.get("name");
		String partyId = (String) context.get("partyId");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(name)) {
				conditionlist.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE, name+"%"));
			}

			if (UtilValidate.isNotEmpty(partyId)) {
				conditionlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			}

			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);


			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("partyId");
			fieldsToSelect.add("firstName");
			fieldsToSelect.add("lastName");
			fieldsToSelect.add("gender");
			fieldsToSelect.add("occupation");
			fieldsToSelect.add("isAgeVerified");
			fieldsToSelect.add("createSource");
			fieldsToSelect.add("assignedStore");
			fieldsToSelect.add("primaryRole");
			fieldsToSelect.add("loyaltyStoreId");
			fieldsToSelect.add("isLoyaltyEnabled");
			fieldsToSelect.add("balancePoints");
			fieldsToSelect.add("loyaltyId");
			fieldsToSelect.add("nationality");
			fieldsToSelect.add("language");
			fieldsToSelect.add("createdByUserLogin");
			fieldsToSelect.add("emplTeamId");
			fieldsToSelect.add("primContactNumber");
			fieldsToSelect.add("primEmail");
			fieldsToSelect.add("birthDate");
			fieldsToSelect.add("businessUnit");
			fieldsToSelect.add("lastUpdatedTxStamp");
			fieldsToSelect.add("createdTxStamp");
			List < GenericValue > personList = EntityQuery
					.use(delegator)
					.select(fieldsToSelect)
					.from("PersonInfo").where(condition)
					//.maxRows(100)
					.orderBy("-lastUpdatedTxStamp")
					.queryList();
			if(UtilValidate.isNotEmpty(personList)) {
				for(GenericValue person : personList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(DataUtil.convertGenericValueToMap(delegator, person));
					//data.put("partyId", person.getString("partyId"));
					//data.put("firstName", person.getString("firstName"));
					//data.put("lastName", person.getString("lastName"));
					data.put("birthDate", person.getString("birthDate"));
					//data.put("businessUnit", person.getString("businessUnit"));
					data.put("lastUpdatedTxStamp", person.getString("lastUpdatedTxStamp"));
					data.put("createdTxStamp", person.getString("createdTxStamp"));
					results.add(data);
				}
			}
			System.out.println("results--------------------->"+results);
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("errorMessage", e.getMessage());
			data.put("errorResult", new ArrayList<Map<String, Object>>());
			results.add(data);
		}
		return doJSONResponse(response, results);
	}

	public static String createPersonInfo(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> results = new HashMap<String, Object>();
		String requestData = DataUtil.getJsonStrBody(request);
		String serviceName = request.getParameter("entity-auto");
		if(UtilValidate.isEmpty(serviceName))
			serviceName = request.getParameter("service-name");

		String tableName = request.getParameter("tableName");
		if(UtilValidate.isEmpty(tableName))
			tableName = "PersonInfo";
		try {
			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			ModelEntity modelEntity = delegator.getModelEntity(tableName);
			ModelFieldTypeReader modelFieldTypeReader = ModelFieldTypeReader
					.getModelFieldTypeReader(delegator.getGroupHelperInfo("org.ofbiz").getHelperBaseName());
			List<GenericValue> toBeStore = new ArrayList<GenericValue>();
			for(Map<String, Object> data : dataList) {
				String partyId = (String) data.get("partyId");
				if(UtilValidate.isEmpty(partyId)) {
					partyId = delegator.getNextSeqId("PersonInfo");
					data.put("partyId", partyId);
				}
				GenericValue personInfo = delegator.findOne("PersonInfo", UtilMisc.toMap("partyId",partyId), false);
				if(UtilValidate.isEmpty(personInfo)) {
					personInfo = delegator.makeValue("PersonInfo");
					Set<String> fields = data.keySet();
					List<String> entityFields = modelEntity.getAllFieldNames();
					for(String key : fields ) {
						String value = (String) data.get(key);
						if(!entityFields.contains(key))
							continue;
						if(UtilValidate.isEmpty(value))
							continue;
						ModelField field = modelEntity.getField(key);
						ModelFieldType type = modelFieldTypeReader.getModelFieldType(field.getType());
						String javaType = type.getJavaType();
						DataUtil.prepareGenericData(key,value,javaType,personInfo);
					}
					toBeStore.add(personInfo);
				} else {
					results.put(ModelService.RESPOND_ERROR, "error");
					results.put("responseMessage", "Info already Exists.");
					return doJSONResponse(response, results);
				}
				if(toBeStore.size() > 0)
					delegator.storeAll(toBeStore);
			}
		} catch (Exception e) {
			e.printStackTrace();
			results.put(ModelService.RESPOND_ERROR, "error");
			results.put("responseMessage", e.getMessage());
			return doJSONResponse(response, results);
		}
		results.put(ModelService.RESPOND_SUCCESS, "success");
		results.put("responseMessage", "Data saved successfully.");
		return doJSONResponse(response, results);
	}

	public static String updatePersonInfo(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> results = new HashMap<String, Object>();
		String requestData = DataUtil.getJsonStrBody(request);
		String serviceName = request.getParameter("entity-auto");
		if(UtilValidate.isEmpty(serviceName))
			serviceName = request.getParameter("service-name");
		String tableName = request.getParameter("tableName");
		if(UtilValidate.isEmpty(tableName))
			tableName = "PersonInfo";
		try {
			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			ModelEntity modelEntity = delegator.getModelEntity(tableName);
			ModelFieldTypeReader modelFieldTypeReader = ModelFieldTypeReader
					.getModelFieldTypeReader(delegator.getGroupHelperInfo("org.ofbiz").getHelperBaseName());
			List<GenericValue> toBeStore = new ArrayList<GenericValue>();
			for(Map<String, Object> data : dataList) {
				String partyId = (String) data.get("partyId");
				if(UtilValidate.isEmpty(partyId))
					partyId = delegator.getNextSeqId("PersonInfo");
				GenericValue personInfo = delegator.findOne("PersonInfo", UtilMisc.toMap("partyId",partyId), false);
				if(UtilValidate.isNotEmpty(personInfo)) {
					//personInfo = delegator.makeValue("PersonInfo");
					Set<String> fields = data.keySet();
					List<String> entityFields = modelEntity.getAllFieldNames();
					for(String key : fields ) {
						String value = (String) data.get(key);
						if(!entityFields.contains(key))
							continue;
						if(UtilValidate.isEmpty(value))
							continue;
						ModelField field = modelEntity.getField(key);
						ModelFieldType type = modelFieldTypeReader.getModelFieldType(field.getType());
						String javaType = type.getJavaType();
						DataUtil.prepareGenericData(key,value,javaType,personInfo);
					}
					toBeStore.add(personInfo);
				} else {
					return doJSONResponse(response, "Info Not Exists.");
				}
				if(toBeStore.size() > 0)
					delegator.storeAll(toBeStore);
			}
		} catch (Exception e) {
			e.printStackTrace();
			results.put(ModelService.RESPOND_ERROR, "error");
			results.put("responseMessage", e.getMessage());
			return doJSONResponse(response, results);
		}
		results.put(ModelService.RESPOND_SUCCESS, "success");
		results.put("responseMessage", "Data updated successfully.");
		return doJSONResponse(response, results);
	}

	public static String removePersonInfo(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> results = new HashMap<String, Object>();
		String requestData = DataUtil.getJsonStrBody(request);
		String serviceName = request.getParameter("entity-auto");
		if(UtilValidate.isEmpty(serviceName))
			serviceName = request.getParameter("service-name");
		String tableName = request.getParameter("tableName");
		if(UtilValidate.isEmpty(tableName))
			tableName = "PersonInfo";
		try {
			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			List<GenericValue> toBeRemove = new ArrayList<GenericValue>();
			for(Map<String, Object> data : dataList) {
				String partyId = (String) data.get("partyId");
				if(UtilValidate.isEmpty(partyId))
					partyId = delegator.getNextSeqId("PersonInfo");
				GenericValue personInfo = delegator.findOne("PersonInfo", UtilMisc.toMap("partyId",partyId), false);
				if(UtilValidate.isNotEmpty(personInfo)) {
					toBeRemove.add(personInfo);
				} else {
					return doJSONResponse(response, "Info Not Exists.");
				}
				if(toBeRemove.size() > 0)
					delegator.removeAll(toBeRemove);
			}

		} catch (Exception e) {
			e.printStackTrace();
			results.put(ModelService.RESPOND_ERROR, "error");
			results.put("responseMessage", e.getMessage());
			return doJSONResponse(response, results);
		}
		results.put(ModelService.RESPOND_SUCCESS, "success");
		results.put("responseMessage", "Data removed successfully.");
		return doJSONResponse(response, results);
	}

	public static String getParty(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = (String) context.get("partyId");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(partyId)) {
				conditionlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			}

			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);


			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("partyId");
			fieldsToSelect.add("partyTypeId");
			fieldsToSelect.add("statusId");
			fieldsToSelect.add("createdByUserLogin");
			fieldsToSelect.add("description");
			fieldsToSelect.add("lastUpdatedTxStamp");
			fieldsToSelect.add("createdTxStamp");
			List <GenericValue> partyList = EntityQuery
					.use(delegator)
					.select(fieldsToSelect)
					.from("Party").where(condition)
					//.maxRows(100)
					.orderBy("-lastUpdatedTxStamp")
					.queryList();

			if(UtilValidate.isNotEmpty(partyList)) {
				for(GenericValue party : partyList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(DataUtil.convertGenericValueToMap(delegator, party));
					data.put("lastUpdatedTxStamp", party.getString("lastUpdatedTxStamp"));
					data.put("createdTxStamp", party.getString("createdTxStamp"));
					results.add(data);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("errorMessage", e.getMessage());
			data.put("errorResult", new ArrayList<Map<String, Object>>());
			results.add(data);
		}
		return doJSONResponse(response, results);
	}

	public static String createBusinessUnit(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> results = new HashMap<String, Object>();
		String requestData = DataUtil.getJsonStrBody(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		List<String> errorList = new LinkedList<String>();

		try {
			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			ModelEntity modelEntity = delegator.getModelEntity("ProductStoreGroup");
			ModelFieldTypeReader modelFieldTypeReader = ModelFieldTypeReader
					.getModelFieldTypeReader(delegator.getGroupHelperInfo("org.ofbiz").getHelperBaseName());
			List<GenericValue> toBeStore = new ArrayList<GenericValue>();

			for(Map<String, Object> data : dataList) {
				List<String> errors = new LinkedList<String>();
				String rowId = (String) data.get("rowId");
				String productStoreGroupId = (String) data.get("productStoreGroupId");
				if(UtilValidate.isEmpty(productStoreGroupId)) {
					productStoreGroupId = delegator.getNextSeqId("ProductStoreGroup");
					data.put("productStoreGroupId", productStoreGroupId);
				}
				GenericValue productStoreGroup = delegator.findOne("ProductStoreGroup", UtilMisc.toMap("productStoreGroupId",productStoreGroupId), false);
				if(UtilValidate.isEmpty(productStoreGroup)) {
					productStoreGroup = delegator.makeValue("ProductStoreGroup");
					Set<String> fields = data.keySet();
					List<String> entityFields = modelEntity.getAllFieldNames();
					for(String key : fields ) {
						String value = (String) data.get(key);
						if(!entityFields.contains(key))
							continue;
						if(UtilValidate.isEmpty(value))
							continue;
						ModelField field = modelEntity.getField(key);
						ModelFieldType type = modelFieldTypeReader.getModelFieldType(field.getType());
						String javaType = type.getJavaType();
						DataUtil.prepareGenericData(key,value,javaType,productStoreGroup);
					}

					String city = (String) data.get("city");
					String state = (String) data.get("state");
					String country = (String) data.get("country");
					Map<String, Object> postalContext = new HashMap<String, Object>();

					if(UtilValidate.isNotEmpty(city))
						postalContext.put("city", city);
					if(UtilValidate.isNotEmpty(state)) {
						EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("geoId",EntityOperator.EQUALS,state),
								EntityCondition.makeCondition("geoTypeId",EntityOperator.IN,UtilMisc.toList("STATE","PROVINCE"))
								);
						GenericValue stateGeo = EntityQuery.use(delegator).from("Geo").where(condition).queryFirst();
						if(UtilValidate.isNotEmpty(stateGeo))
							postalContext.put("stateProvinceGeoId", state);
						else {
							errors.add("Row "+rowId+" : "+UtilProperties.getMessage(RESOURCE, "InvalidState", locale));
						}

					}
					if(UtilValidate.isNotEmpty(country)) {
						GenericValue countryGeo = EntityQuery.use(delegator).from("Geo").where("geoId",country,"geoTypeId","COUNTRY").queryFirst();
						if(UtilValidate.isNotEmpty(countryGeo))
							postalContext.put("countryGeoId", country);
						else {
							errors.add("Row "+rowId+" : "+UtilProperties.getMessage(RESOURCE, "InvalidCountry", locale));
						}
					}
					if(UtilValidate.isNotEmpty(postalContext) && errors.size() == 0) {
						postalContext.put("userLogin", userLogin);
						Map<String, Object> result = dispatcher.runSync("createPostalAddress", postalContext);
						if(ServiceUtil.isSuccess(result)) {
							productStoreGroup.set("postalContactMechId", result.get("contactMechId"));
						}
					}
					String website = (String) data.get("website");
					if(UtilValidate.isNotEmpty(website)) {
						if (UtilValidate.isNotEmpty(website)) {
							GenericValue webContactMech = delegator.makeValue("ContactMech",
									UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
											"WEB_ADDRESS", "infoString", website));
							delegator.create(webContactMech);
							productStoreGroup.set("websiteId", webContactMech.getString("contactMechId"));
						}
					}
					String createdBy = (String) data.get("createdByUserLogin");
					String modifiedBy = (String) data.get("modifiedByUserLogin");
					if(UtilValidate.isNotEmpty(createdBy)) {
						long createUser= EntityQuery.use(delegator).from("UserLogin").where("userLoginId",createdBy).queryCount();
						if(createUser == 0) 
							errors.add("Row "+rowId+" : "+ UtilProperties.getMessage(RESOURCE, "InvalidCreatedByUserLogin", locale));
					}
					if(UtilValidate.isNotEmpty(modifiedBy)) {
						long modifyUser= EntityQuery.use(delegator).from("UserLogin").where("userLoginId",modifiedBy).queryCount();
						if(modifyUser == 0) 
							errors.add("Row "+rowId+" : "+ UtilProperties.getMessage(RESOURCE, "InvalidModifiedByUserLogin", locale));
					}
					if (errors.size() == 0) 
						toBeStore.add(productStoreGroup);
					else
						errorList.addAll(errors);
				} else {
					results.put(ModelService.RESPOND_ERROR, "error");
					results.put("responseMessage", UtilProperties.getMessage(RESOURCE, "RecordAlreadyExists", locale));
					return doJSONResponse(response, results);
				}

			}

			String errorMsg = "";
			if(toBeStore.size() > 0) {
				delegator.storeAll(toBeStore);
				if(errorList.size() > 0) {
					errorMsg = UtilProperties.getMessage(RESOURCE, "DataPartiallySaved", locale);
					errorMsg = errorMsg +"\n"+ errorList.stream().map(Object::toString).collect(Collectors.joining("\n"));
					results.put("responseMessage", errorMsg);
					results.put(ModelService.RESPOND_ERROR, "error");
					return doJSONResponse(response, results);
				}
			} else if (errorList.size() > 0) {
				errorMsg = UtilProperties.getMessage(RESOURCE, "FollowingErrorOccurred", locale);
				errorMsg = errorMsg +"\n"+ errorList.stream().map(Object::toString).collect(Collectors.joining("\n"));
				results.put("responseMessage", errorMsg);
				results.put(ModelService.RESPOND_ERROR, "error");
				return doJSONResponse(response, results);
			}

		} catch (Exception e) {
			e.printStackTrace();
			results.put(ModelService.RESPOND_ERROR, "error");
			results.put("responseMessage", e.getMessage());
			return doJSONResponse(response, results);
		}
		results.put(ModelService.RESPOND_SUCCESS, "success");
		results.put("responseMessage", UtilProperties.getMessage(RESOURCE, "DataSaveSuccessfully", locale));
		return doJSONResponse(response, results);
	}

	public static String updateBusinessUnit(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> results = new HashMap<String, Object>();
		String requestData = DataUtil.getJsonStrBody(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		List<String> errorList = new LinkedList<String>();
		try {
			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			ModelEntity modelEntity = delegator.getModelEntity("ProductStoreGroup");
			ModelFieldTypeReader modelFieldTypeReader = ModelFieldTypeReader
					.getModelFieldTypeReader(delegator.getGroupHelperInfo("org.ofbiz").getHelperBaseName());
			List<GenericValue> toBeStore = new ArrayList<GenericValue>();
			for(Map<String, Object> data : dataList) {
				List<String> errors = new LinkedList<String>();
				String rowId = (String) data.get("rowId");
				String productStoreGroupId = (String) data.get("productStoreGroupId");
				if(UtilValidate.isEmpty(productStoreGroupId)) {
					productStoreGroupId = delegator.getNextSeqId("ProductStoreGroup");
					data.put("productStoreGroupId", productStoreGroupId);
				}
				GenericValue productStoreGroup = delegator.findOne("ProductStoreGroup", UtilMisc.toMap("productStoreGroupId",productStoreGroupId), false);
				if(UtilValidate.isNotEmpty(productStoreGroup)) {
					productStoreGroup = delegator.makeValue("ProductStoreGroup");
					Set<String> fields = data.keySet();
					List<String> entityFields = modelEntity.getAllFieldNames();
					for(String key : fields ) {
						String value = (String) data.get(key);
						if(!entityFields.contains(key))
							continue;
						if(UtilValidate.isEmpty(value))
							continue;
						ModelField field = modelEntity.getField(key);
						ModelFieldType type = modelFieldTypeReader.getModelFieldType(field.getType());
						String javaType = type.getJavaType();
						DataUtil.prepareGenericData(key,value,javaType,productStoreGroup);
					}

					String city = (String) data.get("city");
					String state = (String) data.get("state");
					String country = (String) data.get("country");
					String postalContactMechId = (String) data.get("postalContactMechId");

					Map<String, Object> postalContext = new HashMap<String, Object>();
					if(UtilValidate.isNotEmpty(city))
						postalContext.put("city", city);
					if(UtilValidate.isNotEmpty(state)) {
						EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("geoId",EntityOperator.EQUALS,state),
								EntityCondition.makeCondition("geoTypeId",EntityOperator.IN,UtilMisc.toList("STATE","PROVINCE"))
								);
						GenericValue stateGeo = EntityQuery.use(delegator).from("Geo").where(condition).queryFirst();
						if(UtilValidate.isNotEmpty(stateGeo))
							postalContext.put("stateProvinceGeoId", state);
						else {
							errors.add("Row "+rowId+" : "+UtilProperties.getMessage(RESOURCE, "InvalidState", locale));
						}

					}
					if(UtilValidate.isNotEmpty(country)) {
						GenericValue countryGeo = EntityQuery.use(delegator).from("Geo").where("geoId",country,"geoTypeId","COUNTRY").queryFirst();
						if(UtilValidate.isNotEmpty(countryGeo))
							postalContext.put("countryGeoId", country);
						else {
							errors.add("Row "+rowId+" : "+UtilProperties.getMessage(RESOURCE, "InvalidCountry", locale));
						}
					}
					if(errors.size() == 0 ) {
						if(UtilValidate.isNotEmpty(postalContext) && UtilValidate.isNotEmpty(postalContactMechId)) {
							postalContext.put("userLogin", userLogin);
							postalContext.put("contactMechId", postalContactMechId);
							Map<String, Object> result = dispatcher.runSync("updatePostalAddress",postalContext);
							if(ServiceUtil.isSuccess(result)) {
								productStoreGroup.set("postalContactMechId", result.get("contactMechId"));
							}
						} else if(UtilValidate.isNotEmpty(postalContext)) {
							postalContext.put("userLogin", userLogin);
							Map<String, Object> result = dispatcher.runSync("createPostalAddress", postalContext);
							if(ServiceUtil.isSuccess(result)) {
								productStoreGroup.set("postalContactMechId", result.get("contactMechId"));
							}
						}
					}

					String website = (String) data.get("website");
					String websiteId = (String) data.get("websiteId");

					if(UtilValidate.isNotEmpty(website)) {
						if(UtilValidate.isNotEmpty(websiteId)) {
							GenericValue webContactMech = EntityQuery.use(delegator).from("ContactMech")
									.where("contactMechId", websiteId, "contactMechTypeId","WEB_ADDRESS")
									.orderBy("-lastUpdatedTxStamp")
									.queryFirst();
							if(UtilValidate.isNotEmpty(webContactMech)) {
								webContactMech.set("infoString", website);
								webContactMech.store();
							}
						} else {
							GenericValue webContactMech = delegator.makeValue("ContactMech",
									UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
											"WEB_ADDRESS", "infoString", website));
							delegator.create(webContactMech);
							productStoreGroup.set("websiteId", webContactMech.getString("contactMechId"));
						}
					}

					String createdBy = (String) data.get("createdByUserLogin");
					String modifiedBy = (String) data.get("modifiedByUserLogin");
					if(UtilValidate.isNotEmpty(createdBy)) {
						long createUser= EntityQuery.use(delegator).from("UserLogin").where("userLoginId",createdBy).queryCount();
						if(createUser == 0) 
							errors.add("Row "+rowId+" : "+ UtilProperties.getMessage(RESOURCE, "InvalidCreatedByUserLogin", locale));
					}
					if(UtilValidate.isNotEmpty(modifiedBy)) {
						long modifyUser= EntityQuery.use(delegator).from("UserLogin").where("userLoginId",modifiedBy).queryCount();
						if(modifyUser == 0) 
							errors.add("Row "+rowId+" : "+ UtilProperties.getMessage(RESOURCE, "InvalidModifiedByUserLogin", locale));
					}
					if (errors.size() == 0) 
						toBeStore.add(productStoreGroup);
					else
						errorList.addAll(errors);

				} else {
					results.put(ModelService.RESPOND_ERROR, "error");
					results.put("responseMessage",UtilProperties.getMessage(RESOURCE, "RecordNotFound", locale));
					return doJSONResponse(response, results);
				}

			}

			String errorMsg = "";
			if(toBeStore.size() > 0) {
				delegator.storeAll(toBeStore);
				if(errorList.size() > 0) {
					errorMsg = UtilProperties.getMessage(RESOURCE, "DataPartiallyUpdated", locale);
					errorMsg = errorMsg +"\n"+ errorList.stream().map(Object::toString).collect(Collectors.joining("\n"));
					results.put("responseMessage", errorMsg);
					results.put(ModelService.RESPOND_ERROR, "error");
					return doJSONResponse(response, results);
				}
			} else if (errorList.size() > 0) {
				errorMsg = UtilProperties.getMessage(RESOURCE, "FollowingErrorOccurred", locale);
				errorMsg = errorMsg +"\n"+ errorList.stream().map(Object::toString).collect(Collectors.joining("\n"));
				results.put("responseMessage", errorMsg);
				results.put(ModelService.RESPOND_ERROR, "error");
				return doJSONResponse(response, results);
			}
		} catch (Exception e) {
			e.printStackTrace();
			results.put(ModelService.RESPOND_ERROR, "error");
			results.put("responseMessage", e.getMessage());
			return doJSONResponse(response, results);
		}
		results.put(ModelService.RESPOND_SUCCESS, "success");
		results.put("responseMessage", UtilProperties.getMessage(RESOURCE, "DataUpdatedSuccessfully", locale));
		return doJSONResponse(response, results);
	}

	public static String removeBusinessUnit(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> results = new HashMap<String, Object>();
		String requestData = DataUtil.getJsonStrBody(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String productStoreGroupId = (String)request.getParameter("productStoreGroupId");

		try {
			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			List<GenericValue> toBeRemove = new LinkedList<GenericValue>();
			 if(UtilValidate.isEmpty(dataList)) {
					if(UtilValidate.isEmpty(productStoreGroupId)) {
						results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						results.put(ModelService.ERROR_MESSAGE, "Data is empty");
						return doJSONResponse(response, results);
					}
					String[] productStoreGroupIdValues = productStoreGroupId.split(",");
					for (String value : productStoreGroupIdValues) {
						if (UtilValidate.isNotEmpty(value.trim())) {
							Map<String, Object> result = new HashMap<>();
							result.put("productStoreGroupId", value.trim());
							dataList.add(result);
						}
					}
			}
			if(UtilValidate.isNotEmpty(dataList)) {

				for(Map<String, Object> data : dataList) {
					productStoreGroupId = (String) data.get("productStoreGroupId");

					GenericValue productStoreGroup = delegator.findOne("ProductStoreGroup", UtilMisc.toMap("productStoreGroupId",productStoreGroupId), false);
					if(UtilValidate.isNotEmpty(productStoreGroup)) {

						String postalContactMechId = productStoreGroup.getString("postalContactMechId");
						if(UtilValidate.isNotEmpty(postalContactMechId)) {
							GenericValue postalAddress = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId", postalContactMechId).queryOne();
							if (UtilValidate.isNotEmpty(postalAddress))
								toBeRemove.add(postalAddress);
							GenericValue postalContactMech = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", postalContactMechId).queryOne();
							if (UtilValidate.isNotEmpty(postalContactMech))
								toBeRemove.add(postalContactMech);
						}

						String websiteId = productStoreGroup.getString("websiteId");
						if(UtilValidate.isNotEmpty(websiteId)) {
							GenericValue emailContactMech = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", websiteId).queryOne();
							if (UtilValidate.isNotEmpty(emailContactMech))
								toBeRemove.add(emailContactMech);
						}
						toBeRemove.add(productStoreGroup);
					} else {
						return doJSONResponse(response, "Record Not Found.");
					}
					if(toBeRemove.size() > 0){
						delegator.removeAll(toBeRemove);

						results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
						results.put(ModelService.SUCCESS_MESSAGE, "Business Unit removed successfully.");

					}
				}

			}else{
				results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				results.put(ModelService.ERROR_MESSAGE, "This action cannot be performed without a row first being selected!");

			}

		} catch (Exception e) {
			e.printStackTrace();
			results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			results.put(ModelService.ERROR_MESSAGE, e.getMessage());

			return doJSONResponse(response, results);
		}

		return doJSONResponse(response, results);
	}

	public static String removeUserRole(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		String requestData = DataUtil.getJsonStrBody(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String roleTypeId = (String) request.getParameter("roleTypeId");
		String partyId = (String) request.getParameter("partyId");
		try {
			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			if(UtilValidate.isEmpty(dataList)) {
				Map<String, Object> results = new HashMap<>();
				if(UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(roleTypeId)) {
					result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					result.put(ModelService.ERROR_MESSAGE, "Role can not be removed .");
					return doJSONResponse(response, result);
				}
				results.put("partyId", partyId);
				results.put("roleTypeId", roleTypeId);
				dataList.add(results);
			}
			List<GenericValue> toBeRemove = new LinkedList<GenericValue>();
			for(Map<String, Object> data : dataList) {
				roleTypeId = (String) data.get("roleTypeId");
				partyId = (String) data.get("partyId");
				GenericValue partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId), false);
				if(UtilValidate.isNotEmpty(partyRole)) {
					toBeRemove.add(partyRole);
				}
			}
			if(toBeRemove.size() > 0)
				delegator.removeAll(toBeRemove);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, "Role can not be removed .");
			return doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Role removed successfully.");
		//request.setAttribute("_EVENT_MESSAGE_", "Role removed successfully.");
		return doJSONResponse(response, result);
	}

	public static String getProductCatalog(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String productCataLog = (String) context.get("productCataLog");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(productCataLog)) {
				conditionlist.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, productCataLog));
			}

			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("prodCatalogId");
			fieldsToSelect.add("catalogName");
			fieldsToSelect.add("createdStamp");
			fieldsToSelect.add("lastUpdatedStamp");
			fieldsToSelect.add("sequenceNumber");
			fieldsToSelect.add("isEnable");

			List < GenericValue > productCatalogs = EntityQuery.use(delegator).select(fieldsToSelect).from("ProdCatalog").where(condition).orderBy("-lastUpdatedTxStamp").queryList();
			if (productCatalogs != null && productCatalogs.size() > 0) {
				for (GenericValue productCatalog: productCatalogs) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("prodCatalogId", productCatalog.getString("prodCatalogId"));
					data.put("catalogName", productCatalog.getString("catalogName"));
					data.put("sequenceNumber", productCatalog.getString("sequenceNumber"));

					if (UtilValidate.isNotEmpty(productCatalog.getString("isEnable"))){
						if(productCatalog.getString("isEnable").equals("Y"))
							data.put("isEnable", "Yes");
						else
							data.put("isEnable", "No");
					}
					String createdDate = productCatalog.getString("createdStamp");
					if (UtilValidate.isNotEmpty(createdDate)) {
						createdDate = DataUtil.convertDateTimestamp(createdDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("createdOn", createdDate);
					}

					String modifiedDate = productCatalog.getString("lastUpdatedStamp");
					if (UtilValidate.isNotEmpty(modifiedDate)) {
						modifiedDate = DataUtil.convertDateTimestamp(modifiedDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						data.put("modifiedOn", modifiedDate);
					}
					results.add(data);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getProductCategory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String prodCatalogId = (String) context.get("prodCatalogId");
		String productCategoryId = (String) context.get("productCategoryId");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(prodCatalogId)) {
				conditionlist.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, prodCatalogId));
			}
			if (UtilValidate.isNotEmpty(productCategoryId)) {
				conditionlist.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
			}
			//conditionlist.add(EntityUtil.getFilterByDateExpr());
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("prodCatalogId");
			fieldsToSelect.add("productCategoryId");
			fieldsToSelect.add("createdStamp");
			fieldsToSelect.add("lastUpdatedStamp");
			fieldsToSelect.add("sequenceNum");
			fieldsToSelect.add("thruDate");

			List < GenericValue > productCatalogs = EntityQuery.use(delegator).select(fieldsToSelect).distinct().from("ProdCatalogCategory").where(condition).orderBy("fromDate DESC").queryList();
			if (productCatalogs != null && productCatalogs.size() > 0) {
				Set<String> categorySet = new HashSet<String>();
				for (GenericValue productCatalog: productCatalogs) {
					if(!(categorySet.contains(productCatalog.getString("productCategoryId")))) {
						categorySet.add(productCatalog.getString("productCategoryId"));
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("prodCatalogId", productCatalog.getString("prodCatalogId"));
						if (UtilValidate.isNotEmpty(productCatalog.getString("prodCatalogId"))) {
							GenericValue catalog = EntityQuery.use(delegator).from("ProdCatalog").where("prodCatalogId",productCatalog.getString("prodCatalogId")).queryOne();
							if (catalog != null) {
								String catalogName = catalog.getString("catalogName");
								data.put("catalogName", catalogName);
							}
						}
						data.put("productCategoryId", productCatalog.getString("productCategoryId"));
						if (UtilValidate.isNotEmpty(productCatalog.getString("productCategoryId"))) {
							GenericValue category = EntityQuery.use(delegator).from("ProductCategory").where("productCategoryId",productCatalog.getString("productCategoryId")).queryOne();
							if (category != null) {
								String categoryName = category.getString("categoryName");
								data.put("categoryName", categoryName);
							}
						}
						String createdDate = productCatalog.getString("createdStamp");
						if (UtilValidate.isNotEmpty(createdDate)) {
							createdDate = DataUtil.convertDateTimestamp(createdDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
							data.put("createdOn", createdDate);
						}

						String modifiedDate = productCatalog.getString("lastUpdatedStamp");
						if (UtilValidate.isNotEmpty(modifiedDate)) {
							modifiedDate = DataUtil.convertDateTimestamp(modifiedDate, df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
							data.put("modifiedOn", modifiedDate);
						}
						data.put("sequenceNumber", productCatalog.getString("sequenceNum"));
						data.put("isEnable", UtilValidate.isEmpty(productCatalog.getString("thruDate")) ? "YES" : "NO");

						results.add(data);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getProductSubCategory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String prodCatalogId = (String) context.get("prodCatalogId");
		String productCategoryId = (String) context.get("productCategoryId");
		String productSubCategoryId = (String) context.get("productSubCategoryId");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(prodCatalogId)) {
				conditionlist.add(EntityCondition.makeCondition("catalogId", EntityOperator.EQUALS, prodCatalogId));
			}
			if (UtilValidate.isNotEmpty(productCategoryId)) {
				conditionlist.add(EntityCondition.makeCondition("categoryId", EntityOperator.EQUALS, productCategoryId));
			}
			if (UtilValidate.isNotEmpty(productSubCategoryId)) {
				conditionlist.add(EntityCondition.makeCondition("subCategoryId", EntityOperator.EQUALS, productSubCategoryId));
			}
			conditionlist.add(EntityCondition.makeCondition("subCategoryId", EntityOperator.NOT_EQUAL, null));
			//conditionlist.add(EntityUtil.getFilterByDateExpr("subCategoryFromDate","subCategoryThruDate"));
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("catalogId");
			fieldsToSelect.add("catalogName");
			fieldsToSelect.add("categoryId");
			fieldsToSelect.add("categoryName");
			fieldsToSelect.add("subCategoryName");
			fieldsToSelect.add("subCategoryId");
			fieldsToSelect.add("subCategoryThruDate");
			fieldsToSelect.add("subCategorySeqNum");

			List < GenericValue > productSubCategories = EntityQuery.use(delegator).select(fieldsToSelect).distinct().from("ProductCategoryCatalogAssoc")
					.where(condition).orderBy("subCategoryFromDate DESC").queryList();
			if (productSubCategories != null && productSubCategories.size() > 0) {
				Set<String> subCategorySet = new HashSet<String>();
				for (GenericValue productSubCategory: productSubCategories) {
					if(!(subCategorySet.contains(productSubCategory.getString("subCategoryId")))) {
						subCategorySet.add(productSubCategory.getString("subCategoryId"));
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("prodCatalogId", productSubCategory.getString("catalogId"));
						data.put("catalogName", productSubCategory.getString("catalogName"));
						data.put("productCategoryId", productSubCategory.getString("categoryId"));
						data.put("categoryName", productSubCategory.getString("categoryName"));
						data.put("subCategoryId", productSubCategory.getString("subCategoryId"));
						data.put("subCategoryName", productSubCategory.getString("subCategoryName"));
						data.put("sequenceNumber", productSubCategory.getString("subCategorySeqNum"));
						data.put("isEnable", UtilValidate.isEmpty(productSubCategory.getString("subCategoryThruDate")) ? "YES" : "NO");

						results.add(data);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getProductCategoryList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String associated = "N";
		String prodCatalogId = request.getParameter("prodCatalogId");

		if (UtilValidate.isNotEmpty(prodCatalogId)) {
			try {
				List < GenericValue > categories = EntityQuery.use(delegator).from("ProdCatalogCategory").where("prodCatalogId", prodCatalogId, "thruDate", null).orderBy("sequenceNum").queryList();
				if (UtilValidate.isNotEmpty(categories)) {
					for (GenericValue category: categories) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("productCategoryId", category.getString("productCategoryId"));

						GenericValue productCategorDetail = EntityQuery.use(delegator).from("ProductCategoryContent").where("productCategoryId",category.getString("productCategoryId")).queryFirst();
						if(UtilValidate.isNotEmpty(productCategorDetail))
						{
							GenericValue content = EntityQuery.use(delegator).from("Content").where("contentId",productCategorDetail.getString("contentId")).queryFirst();
							if(UtilValidate.isNotEmpty(content)) {
								GenericValue contentResource = EntityQuery.use(delegator).from("ElectronicText").where("dataResourceId",content.getString("dataResourceId")).queryFirst();
								if(UtilValidate.isNotEmpty(contentResource)) {
									data.put("categoryName", contentResource.getString("textData"));
									contentResource.store();
								}
							}

						}
						results.add(data);

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				return doJSONResponse(response, e.getMessage());
			}
		}

		return doJSONResponse(response, results);
	}

	public static String getProduct(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String prodCatalogId = (String) context.get("prodCatalogId");
		String productCategoryId = (String) context.get("productCategoryId");
		String productSubCategoryId = (String) context.get("productSubCategoryId");
		String productId = (String) context.get("productId");
		try {
			List < EntityCondition > conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(prodCatalogId)) {
				conditionlist.add(EntityCondition.makeCondition("catalogId", EntityOperator.EQUALS, prodCatalogId));
			}
			if (UtilValidate.isNotEmpty(productCategoryId)) {
				conditionlist.add(EntityCondition.makeCondition("categoryId", EntityOperator.EQUALS, productCategoryId));
			}
			if (UtilValidate.isNotEmpty(productSubCategoryId)) {
				conditionlist.add(EntityCondition.makeCondition("subCategoryId", EntityOperator.EQUALS, productSubCategoryId));
			}
			if (UtilValidate.isNotEmpty(productId)) {
				conditionlist.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			}
			conditionlist.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
			//conditionlist.add(EntityUtil.getFilterByDateExpr("productFromDate","productThruDate"));
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("catalogId");
			fieldsToSelect.add("catalogName");
			fieldsToSelect.add("categoryId");
			fieldsToSelect.add("categoryName");
			fieldsToSelect.add("subCategoryName");
			fieldsToSelect.add("subCategoryId");
			fieldsToSelect.add("productName");
			fieldsToSelect.add("productId");
			fieldsToSelect.add("productSeqNum");
			fieldsToSelect.add("productThruDate");

			List < GenericValue > products = EntityQuery.use(delegator).select(fieldsToSelect).distinct().from("ProductCategoryCatalogAssoc")
					.where(condition).orderBy("productFromDate DESC").queryList();
			if (products != null && products.size() > 0) {
				Set<String> productSet = new HashSet<String>();
				for (GenericValue product: products) {
					if(!(productSet.contains(product.getString("productId")))) {
						productSet.add(product.getString("productId"));
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("prodCatalogId", product.getString("catalogId"));
						data.put("catalogName", product.getString("catalogName"));
						data.put("productCategoryId", product.getString("categoryId"));
						data.put("categoryName", product.getString("categoryName"));
						data.put("subCategoryId", product.getString("subCategoryId"));
						data.put("subCategoryName", product.getString("subCategoryName"));
						data.put("productId", product.getString("productId"));
						data.put("productName", product.getString("productName"));
						data.put("sequenceNumber", product.getString("productSeqNum"));
						data.put("isEnable", UtilValidate.isEmpty(product.getString("productThruDate")) ? "YES" : "NO");
						results.add(data);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getSubCategoryList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String associated = "N";
		String productCategoryId = request.getParameter("productCategoryId");

		if (UtilValidate.isNotEmpty(productCategoryId)) {
			try {
				List < GenericValue > categories = EntityQuery.use(delegator).from("ProductCategoryRollup").where("parentProductCategoryId", productCategoryId, "thruDate", null).orderBy("sequenceNum").queryList();

				if (UtilValidate.isNotEmpty(categories)) {
					for (GenericValue category: categories) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("subCategoryId", category.getString("productCategoryId"));

						GenericValue productCategorDetail = EntityQuery.use(delegator).from("ProductCategoryContent").where("productCategoryId",category.getString("productCategoryId")).queryFirst();
						if(UtilValidate.isNotEmpty(productCategorDetail))
						{
							GenericValue content = EntityQuery.use(delegator).from("Content").where("contentId",productCategorDetail.getString("contentId")).queryFirst();
							if(UtilValidate.isNotEmpty(content)) {
								GenericValue contentResource = EntityQuery.use(delegator).from("ElectronicText").where("dataResourceId",content.getString("dataResourceId")).queryFirst();
								if(UtilValidate.isNotEmpty(contentResource)) {
									data.put("subCategoryName", contentResource.getString("textData"));
									contentResource.store();
								}
							}

						}
						results.add(data);

					}
				}

			}
			catch (Exception e) {
				e.printStackTrace();
				return doJSONResponse(response, e.getMessage());
			}
		}

		return doJSONResponse(response, results);
	}

	public static String addMembersToTeam(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> results = new HashMap<String, Object>();
		String requestData = DataUtil.getJsonStrBody(request);
		String emplTeamId = request.getParameter("emplTeamId");
		String selectedRows = request.getParameter("selectedRows");
		String teamLeadFlag = request.getParameter("teamLeadFlag");
		String teamPartyId = "";
		boolean isActiveTeam = false;
		try {

			if(UtilValidate.isNotEmpty(emplTeamId) && UtilValidate.isNotEmpty(selectedRows) && UtilValidate.isNotEmpty(teamLeadFlag)) {
				List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();
				if(UtilValidate.isNotEmpty(selectedRows))
					requestMapList = DataUtil.convertToListMap(selectedRows);
				if(UtilValidate.isNotEmpty(requestMapList)) {
					boolean isTeamUpdatePartyRelationship = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_TEAM_UPD_PTY_RLTNSP","N").equals("Y");
					GenericValue team = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId", emplTeamId).queryOne();
					if(UtilValidate.isNotEmpty(team)) {
						teamPartyId = team.getString("partyId");
						if(UtilValidate.isNotEmpty(team.getString("isActive")) && "Y".equals(team.getString("isActive"))) {
							isActiveTeam = true;
						}
					}
					for(Map<String, Object> requestMap : requestMapList) {
						boolean isLeader = false;
						String partyId = (String) requestMap.get("partyId");
						String businessUnit = (String) requestMap.get("businessUnit");

						GenericValue teamPos = delegator.makeValue("EmplPosition");
						String posId = delegator.getNextSeqId("EmplPosition");
						teamPos.put("emplPositionId", posId);
						delegator.create(teamPos);

						GenericValue teamEmp = delegator.makeValue("EmplPositionFulfillment");
						teamEmp.put("emplPositionId", posId);
						teamEmp.put("partyId", partyId);
						teamEmp.put("fromDate", UtilDateTime.nowTimestamp());
						teamEmp.put("emplTeamId", emplTeamId);
						teamEmp.put("businessUnit", businessUnit);

						isLeader = "Y".equals(teamLeadFlag);
						if(isLeader)
							teamEmp.put("isTeamLead", "Y");
						else
							teamEmp.put("isTeamLead", "N");
						
						if(isTeamUpdatePartyRelationship && isActiveTeam) {
							DataHelper.addTeamMemberPartyRelationship(partyId, teamPartyId, isLeader ,delegator, userLogin, dispatcher);
						}

						delegator.create(teamEmp);
					}
				}
				if("N".equals(teamLeadFlag)){
					request.setAttribute("emplTeamId", emplTeamId);
					request.setAttribute("_EVENT_MESSAGE_", "Team Members Added Successfully to Team");
				}
				if("Y".equals(teamLeadFlag)){
					request.setAttribute("emplTeamId", emplTeamId);
					request.setAttribute("_EVENT_MESSAGE_", "Team Lead Added Successfully to Team");
				}
			}
		} catch (Exception e) {
			String errMsg = "Problem While Creating Adding Members to Team " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}

	public static String removeMembersFromTeam(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		String requestData = DataUtil.getJsonStrBody(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String emplTeamId = "";
		String responseMessage = "Team Members has been successfully Removed.";

		try {
			boolean isTeamUpdatePartyRelationship = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_TEAM_UPD_PTY_RLTNSP","N").equals("Y");

			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			List<GenericValue> toBeRemove = new LinkedList<GenericValue>();
			for(Map<String, Object> data : dataList) {
				String teamPartyId = null;
				emplTeamId = (String) data.get("emplTeamId");
				String partyId = (String) data.get("newPartyId");
				GenericValue team = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId", emplTeamId).queryOne();
				if(UtilValidate.isNotEmpty(team)) {
					teamPartyId = team.getString("partyId");
				}
				GenericValue emplPositionFulfillment = EntityUtil.getFirst( delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("emplTeamId", emplTeamId, "partyId", partyId), null, false) );

				if(UtilValidate.isNotEmpty(emplPositionFulfillment)) {
					toBeRemove.add(emplPositionFulfillment);
				}
				
				if(isTeamUpdatePartyRelationship) {
					DataHelper.expireTeamMemberPartyRelationship(partyId, teamPartyId ,delegator, userLogin, dispatcher);
				}
			}
			if(toBeRemove.size() > 0){
				delegator.removeAll(toBeRemove);

				request.setAttribute("emplTeamId", emplTeamId);
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
				return CommonEvents.doJSONResponse(response, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return CommonEvents.doJSONResponse(response, result);
		}
		request.setAttribute("emplTeamId", emplTeamId);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
		return CommonEvents.doJSONResponse(response, result);

	}

	public static String getRolesList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String parentTypeId = (String) context.get("parentTypeId");
		String roleTypeId = (String) context.get("roleTypeId");
		String partyId = (String) context.get("partyId");
		try {
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(partyId)) {
				List < GenericValue > existRoleList = DataUtil.getPartyRoles(delegator, partyId, parentTypeId);
				if (UtilValidate.isNotEmpty(existRoleList)) {
					List < String > existRole = EntityUtil.getFieldListFromEntityList(existRoleList, "roleTypeId", true);
					if (UtilValidate.isNotEmpty(existRole)) {
						conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_IN, existRole));
					}
				}
			}
			if (UtilValidate.isEmpty(parentTypeId))
				parentTypeId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);

			if (UtilValidate.isNotEmpty(parentTypeId)){
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("parentTypeId", EntityOperator.NOT_EQUAL, parentTypeId),
						EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "")
						));
			}
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
			}
			EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < GenericValue > roleList = EntityQuery.use(delegator).select("roleTypeId", "parentTypeId", "description").from("RoleType").where(mainCondition).orderBy("-lastUpdatedTxStamp").queryList();
			for (GenericValue role: roleList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String roleTypeId1 = role.getString("roleTypeId");
				String parentTypeId1 = role.getString("parentTypeId");
				String description = role.getString("description");
				data.put("parentTypeId", parentTypeId1);
				data.put("roleTypeId", roleTypeId1);
				data.put("description", description);
				results.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getUserRolesList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		String partyId = request.getParameter("partyId");
		String parentTypeId = request.getParameter("parentTypeId");

		try {
			if (UtilValidate.isEmpty(parentTypeId))
				parentTypeId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);

			if (UtilValidate.isNotEmpty(partyId)) {
				List < GenericValue > roleList = DataUtil.getPartyRoles(delegator, partyId, null);
				for (GenericValue partyRole: roleList) {
					String roleTypeId = partyRole.getString("roleTypeId");

					List < EntityCondition > conditions = new ArrayList<EntityCondition>();

					if (UtilValidate.isNotEmpty(parentTypeId)){
						conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("parentTypeId", EntityOperator.NOT_EQUAL, parentTypeId),
								EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, null),
								EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "")
								));
					}
					if (UtilValidate.isNotEmpty(roleTypeId)) {
						conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
					}
					EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					GenericValue roleType = EntityQuery.use(delegator).select("roleTypeId", "parentTypeId", "description").from("RoleType").where(mainCondition).orderBy("-lastUpdatedTxStamp").queryFirst();

					if (UtilValidate.isNotEmpty(roleType)) {
						Map<String, Object> data = new HashMap<String, Object>();
						String createdBy = partyRole.getString("createdBy");
						String description = roleType.getString("description");
						String createdOn = partyRole.getString("createdOn");
						data.put("partyId", partyId);
						data.put("roleTypeId", roleTypeId);
						data.put("createdOn", createdOn);
						data.put("createdBy", createdBy);
						data.put("description", description);
						results.add(data);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String addTeam(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> results = new HashMap<String, Object>();
		String requestData = DataUtil.getJsonStrBody(request);
		String partyId = request.getParameter("partyId");
		String selectedRows = request.getParameter("selectedRows");
		String isNative = request.getParameter("isNative");
		String userLoginId = request.getParameter("userLoginId");
		String activeTab = request.getParameter("activeTab");
		try {

			if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(selectedRows)) {
				List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();
				if(UtilValidate.isNotEmpty(selectedRows))
					requestMapList = DataUtil.convertToListMap(selectedRows);
				if(UtilValidate.isNotEmpty(requestMapList)) {
					for(Map<String, Object> requestMap : requestMapList) {
						String emplTeamId = (String) requestMap.get("emplTeamId");
						String businessUnit = (String) requestMap.get("businessUnit");
						GenericValue emplfulfillment = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("partyId", partyId,"emplTeamId",emplTeamId).filterByDate().queryFirst();

						if(UtilValidate.isEmpty(emplfulfillment)) {
							GenericValue emplPosition = EntityQuery.use(delegator).from("EmplPosition").where("partyId", partyId,"statusId","EMPL_POS_ACTIVE").queryFirst();
							if(UtilValidate.isEmpty(emplPosition)) {
								emplPosition = delegator.makeValue("EmplPosition");
								emplPosition.set("emplPositionId", delegator.getNextSeqId("EmplPosition"));
								emplPosition.set("statusId", "EMPL_POS_ACTIVE");
								emplPosition.set("partyId", partyId);
								emplPosition.create();
							} 
							emplfulfillment = delegator.makeValue("EmplPositionFulfillment");
							emplfulfillment.set("emplPositionId", emplPosition.getString("emplPositionId"));
							emplfulfillment.set("partyId", partyId);
							emplfulfillment.set("fromDate", UtilDateTime.nowTimestamp());
							emplfulfillment.set("emplTeamId", emplTeamId);
							emplfulfillment.set("businessUnit", businessUnit);
							emplfulfillment.create();
						}
						if("Y".equals(isNative)) {
							GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", partyId).queryOne();
							if(UtilValidate.isNotEmpty(person)) {
								person.set("businessUnit", businessUnit);
								person.set("emplTeamId", emplTeamId);
								person.store();
							}
						}
					}
				}
			}
			request.setAttribute("_EVENT_MESSAGE_", "Team added successfully");
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(),MODULE);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return EventReturn.ERROR;
		}
		request.setAttribute("activeTab", activeTab);
		request.setAttribute("userLoginId", userLoginId);
		return EventReturn.SUCCESS;
	}

	public static String removeTeamsFromUser(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		String requestData = DataUtil.getJsonStrBody(request);
		String partyId = (String)request.getParameter("partyId");
		String emplTeamId = (String)request.getParameter("emplTeamId");

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		try {
			boolean isTeamUpdatePartyRelationship = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_TEAM_UPD_PTY_RLTNSP","N").equals("Y");
			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			List<GenericValue> toBeRemove = new LinkedList<GenericValue>();
			if(UtilValidate.isEmpty(dataList)) {
				Map<String, Object> results = new HashMap<>();
				if(UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(emplTeamId)) {
					result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					result.put(ModelService.ERROR_MESSAGE, "Data is empty");
					return doJSONResponse(response, result);
				}else if(partyId.contains(",") && UtilValidate.isNotEmpty(emplTeamId)) {
					List<String> partyIds = Stream.of(partyId.split(",")).map(e -> new String(e)).collect(Collectors.toList());
					for(String partyIdSplited : partyIds) {
						dataList.add(UtilMisc.toMap("partyId", partyIdSplited, "emplTeamId", emplTeamId));
					}
				}else {
					results.put("partyId", partyId);
					results.put("emplTeamId", emplTeamId);
					dataList.add(results);
				}
			}
			if(UtilValidate.isNotEmpty(dataList)){
				for(Map<String, Object> data : dataList) {
					String teamPartyId = null;
					partyId = (String) data.get("partyId");
					emplTeamId = (String) data.get("emplTeamId");
					GenericValue team = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId", emplTeamId).queryOne();
					if(UtilValidate.isNotEmpty(team)) {
						teamPartyId = team.getString("partyId");
					}
					GenericValue emplfulfillment = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("partyId", partyId,"emplTeamId",emplTeamId).filterByDate().queryFirst();
					if(UtilValidate.isNotEmpty(emplfulfillment)) {
						toBeRemove.add(emplfulfillment);
					}
					if(isTeamUpdatePartyRelationship) {
						DataHelper.expireTeamMemberPartyRelationship(partyId, teamPartyId ,delegator, userLogin, dispatcher);
					}
				}
				if(toBeRemove.size() > 0)
					delegator.removeAll(toBeRemove);
				request.setAttribute("_EVENT_MESSAGE_", "Data removed successfully");
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, "Data removed successfully");

			}else{
				result.put(ModelService.ERROR_MESSAGE, "Please select atleast one row to remove");

			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return doJSONResponse(response, result);
	}

	public static String getRoleSecurityAssoc(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		try {
			Map<String, Object> roleMap = new HashMap<>();
			List<GenericValue> roleList = EntityQuery.use(delegator).from("RoleType").queryList();
			if(UtilValidate.isNotEmpty(roleList)) {
				roleMap = DataUtil.getMapFromGeneric(roleList, "roleTypeId", "description", false);
			}

			Map<String, Object> groupMap = new HashMap<>();
			List<GenericValue> securityGroupList = EntityQuery.use(delegator).from("SecurityGroup").queryList();
			if(UtilValidate.isNotEmpty(securityGroupList)) {
				groupMap = DataUtil.getMapFromGeneric(securityGroupList, "groupId", "description", false);
			}
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < GenericValue > roleSecurityAssocList = EntityQuery.use(delegator).from("SecurityGroupRoleTypeAssoc").where(condition).orderBy("lastUpdatedTxStamp DESC").queryList();
			for (GenericValue roleSecurityAssoc : roleSecurityAssocList) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.putAll(DataUtil.convertGenericValueToMap(delegator, roleSecurityAssoc));
				String groupId = roleSecurityAssoc.getString("groupId");
				data.put("roleDesc", UtilValidate.isNotEmpty(roleMap) ? roleMap.get(roleSecurityAssoc.getString("roleTypeId")) : "");
				data.put("groupDesc", UtilValidate.isNotEmpty(groupMap) ? groupMap.get(roleSecurityAssoc.getString("groupId")) : "");
				if (UtilValidate.isNotEmpty(roleSecurityAssoc.getString("lastUpdatedTxStamp"))) {
					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					data.put("lastUpdatedTxStamp", DataUtil.convertDateTimestamp(roleSecurityAssoc.getString("lastUpdatedTxStamp"), df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING));
				}
				dataList.add(data);
			}
			result.put("list", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return doJSONResponse(response, result);
	}
	public static String getActivityWorkTypes(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		try {
			List < GenericValue > workEffortList = EntityQuery.use(delegator).from("WorkEffortPurposeType").where("parentTypeId", "ACTIVITY_WORK_TYPE").orderBy("-createdStamp").queryList();

			for (GenericValue eachWorkEffrt : workEffortList) {
				Map<String, Object> data = new HashMap<String, Object>();

				String workEffortPurposeTypeId = eachWorkEffrt.getString("workEffortPurposeTypeId");
				String description = eachWorkEffrt.getString("description");
				data.put("workEffortPurposeTypeId", workEffortPurposeTypeId);
				data.put("description", description);
				results.add(data);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	public static String removeActivityWorkTypes(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		String requestData = DataUtil.getJsonStrBody(request);
		String responseMessage = "Activity Work Type removed successfully";
		try {
			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			List<GenericValue> toBeRemove = new LinkedList<GenericValue>();
			List < GenericValue > existingPurposeTypeList = null;
			for(Map<String, Object> data : dataList) {
				String workEffortPurposeTypeId = (String) data.get("workEffortPurposeTypeId");
				existingPurposeTypeList=EntityQuery.use(delegator).from("WorkEffort").where("workEffortPurposeTypeId", workEffortPurposeTypeId).queryList();
				GenericValue activityWorkType = EntityQuery.use(delegator).from("WorkEffortPurposeType").where("workEffortPurposeTypeId", workEffortPurposeTypeId).queryOne();
				if(UtilValidate.isNotEmpty(activityWorkType)) {
					toBeRemove.add(activityWorkType);
				}
			}
			if(UtilValidate.isEmpty(existingPurposeTypeList)){
				if(toBeRemove.size() > 0){
					delegator.removeAll(toBeRemove);
				}
			}else{
				responseMessage = "Activity work type is already used, not allowed to remove";
			}
		}catch (Exception e) {
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return CommonEvents.doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
		return CommonEvents.doJSONResponse(response, result);
	}
	public static String getTechniciansRates(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String partyId = (String) context.get("partyId");
		String rateTypeId = (String) context.get("rateTypeId");
		String currencyUomId = (String) context.get("currencyUomId");
		String defaultRate = (String) context.get("defaultRate");
		BigDecimal rateAmount = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(context.get("rate"))) {
			rateAmount = new BigDecimal((String)context.get("rate"));
		}

		String fromDate = (String) context.get("fromDate");
		String thruDate = (String) context.get("thruDate");

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");

		try {
			Timestamp fromDateTime = null;
			Timestamp thruDateTime = null;

			if (UtilValidate.isNotEmpty(fromDate)) {
				fromDate = df1.format(df2.parse(fromDate));
				fromDateTime = UtilDateTime.getDayStart(Timestamp.valueOf(fromDate));
			}
			if (UtilValidate.isNotEmpty(thruDate)) {
				thruDate = df1.format(df2.parse(thruDate));
				thruDateTime = UtilDateTime.getDayEnd(Timestamp.valueOf(thruDate));
			}
			List<EntityCondition> conditions = new ArrayList<>();
			if(UtilValidate.isNotEmpty(partyId))
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			if(UtilValidate.isNotEmpty(rateTypeId))
				conditions.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
			if(UtilValidate.isNotEmpty(currencyUomId))
				conditions.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId));
			if(UtilValidate.isNotEmpty(defaultRate))
				conditions.add(EntityCondition.makeCondition("defaultRate", EntityOperator.EQUALS, defaultRate));
			if(UtilValidate.isNotEmpty(rateAmount) && rateAmount != BigDecimal.ZERO)
				conditions.add(EntityCondition.makeCondition("rate", EntityOperator.EQUALS, rateAmount));
			if(UtilValidate.isNotEmpty(fromDateTime))
				conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateTime));
			if(UtilValidate.isNotEmpty(thruDateTime))
				conditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateTime));

			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);

			List < GenericValue > partyRateList = EntityQuery.use(delegator).from("PartyRate").where(condition).orderBy("-createdStamp").queryList();

			for (GenericValue eachPartyRate : partyRateList) {
				Map<String, Object> data = new HashMap<String, Object>();

				String partyId1 = eachPartyRate.getString("partyId");
				String rateTypeId1 = eachPartyRate.getString("rateTypeId");
				String currencyUomId1 = eachPartyRate.getString("currencyUomId");
				String defaultRate1 = eachPartyRate.getString("defaultRate");
				String partyName = PartyHelper.getPartyName(delegator, partyId1, false);
				String rateType = DataUtil.getRateType(delegator, rateTypeId1);
				String fromDate1 = "";
				if (UtilValidate.isNotEmpty(eachPartyRate.getString("fromDate"))) {
					fromDate1 = DataUtil.convertDateTimestamp(eachPartyRate.getString("fromDate"), new SimpleDateFormat("MM/dd/yyyy"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
				}
				String thruDate1 = "";
				if (UtilValidate.isNotEmpty(eachPartyRate.getString("thruDate"))) {
					thruDate1 = DataUtil.convertDateTimestamp(eachPartyRate.getString("thruDate"), new SimpleDateFormat("MM/dd/yyyy"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
				}

				BigDecimal rate=(BigDecimal)eachPartyRate.getBigDecimal("rate");
				/*if(UtilValidate.isNotEmpty(rate)){
					data.put("rate", Integer.valueOf(rate.intValue()));
				}*/

				if (UtilValidate.isNotEmpty(defaultRate1) && "Y".equals(defaultRate1)) {
					partyName = "Standard Rates";
				}
				data.put("partyId", partyId1);
				data.put("partyName", partyName);
				data.put("rateTypeId", rateTypeId1);
				data.put("rateType", rateType);
				data.put("currencyUomId", currencyUomId1);
				data.put("defaultRate", defaultRate1);
				data.put("rate", rate);
				data.put("fromDate", fromDate1);
				data.put("thruDate", thruDate1);
				results.add(data);
			}
			Debug.log("Results : " + results, MODULE);
		}catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getTechnicianLastUpdatedDate(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String partyId = (String) context.get("partyId");
		String rateTypeId = (String) context.get("rateTypeId");
		String currencyUomId = (String) context.get("currencyUomId");
		String defaultRate = (String) context.get("defaultRate");

		try {

			EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId),
					EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId),
					EntityCondition.makeCondition("defaultRate", EntityOperator.EQUALS, defaultRate));

			GenericValue partyRate = EntityUtil.getFirst(EntityQuery.use(delegator).from("PartyRate").where(conditions).orderBy("-thruDate").queryList());

			if(UtilValidate.isNotEmpty(partyRate)) {
				Map<String, Object> data = new HashMap<String, Object>();
				partyId = partyRate.getString("partyId");
				rateTypeId = partyRate.getString("rateTypeId");
				currencyUomId = partyRate.getString("currencyUomId");
				defaultRate = partyRate.getString("defaultRate");
				String fromDate = partyRate.getString("fromDate");
				String thruDate = partyRate.getString("thruDate");
				BigDecimal rate=(BigDecimal)partyRate.getBigDecimal("rate");

				data.put("partyId", partyId);
				data.put("rateTypeId", rateTypeId);
				data.put("currencyUomId", currencyUomId);
				data.put("defaultRate", defaultRate);
				data.put("rate", rate);
				data.put("fromDate", UtilDateTime.timeStampToString(partyRate.getTimestamp("fromDate"), "yyyy-MM-dd", TimeZone.getDefault(), null));
				data.put("thruDate", UtilDateTime.timeStampToString(partyRate.getTimestamp("thruDate"), "yyyy-MM-dd", TimeZone.getDefault(), null));
				results.add(data);
			}

		}catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String removeTechnicianRates(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		String requestData = DataUtil.getJsonStrBody(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String responseMessage = "Technician Rate removed successfully";
		String partyId = (String) request.getParameter("partyId");
		String rateTypeId = (String) request.getParameter("rateTypeId");
		String currencyUomId = (String)request.getParameter("currencyUomId");
		String fromDate = (String)request.getParameter("fromDate");
		
		try {
			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			if(UtilValidate.isEmpty(dataList)) {
				Map<String, Object> results = new HashMap<>();
				if(UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(rateTypeId) || UtilValidate.isEmpty(currencyUomId) ||UtilValidate.isEmpty(fromDate)) {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, "Data is empty");
				return AjaxEvents.doJSONResponse(response, result);
				}
				results.put("partyId", partyId);
				results.put("rateTypeId", rateTypeId);
				results.put("currencyUomId", currencyUomId);
				results.put("fromDate", fromDate);
				dataList.add(results);
			}
			List<GenericValue> toBeRemove = new LinkedList<GenericValue>();
			for(Map<String, Object> data : dataList) {
				 partyId = (String) data.get("partyId");
				 rateTypeId = (String) data.get("rateTypeId");
				 currencyUomId = (String) data.get("currencyUomId");
				 fromDate = (String) data.get("fromDate");

				SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");

				List < EntityCondition > conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditions.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
				conditions.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId));

				if (UtilValidate.isNotEmpty(fromDate)) {
					fromDate = df1.format(df2.parse(fromDate));
					Timestamp fromDateStamp = UtilDateTime.getDayStart(Timestamp.valueOf(fromDate));
					conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate",EntityOperator.EQUALS,fromDateStamp)));
				}

				EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue partyRate = EntityQuery.use(delegator).from("PartyRate").where(condition).queryFirst();

				if(UtilValidate.isNotEmpty(partyRate)) {
					toBeRemove.add(partyRate);
				}
			}
			if(toBeRemove.size() > 0){
				delegator.removeAll(toBeRemove);
			}
		}catch (Exception e) {
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return CommonEvents.doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
		return CommonEvents.doJSONResponse(response, result);
	}

	public static String getTechnicianLocation(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		try {

			List < GenericValue > productStoreTechAssocList = EntityQuery.use(delegator).from("ProductStoreTechAssoc").orderBy("-createdStamp").queryList();

			for (GenericValue eachProductStore : productStoreTechAssocList) {
				Map<String, Object> data = new HashMap<String, Object>();

				String state = eachProductStore.getString("state");
				String country = eachProductStore.getString("county");
				String isTechInspection = eachProductStore.getString("isTechInspection");
				String productStoreId = eachProductStore.getString("productStoreId");
				String productStoreName = eachProductStore.getString("productStoreName");

				String technician1 = eachProductStore.getString("technicianId01");
				String technician2 = eachProductStore.getString("technicianId02");
				String technician3 = eachProductStore.getString("technicianId03");
				String technician4 = eachProductStore.getString("technicianId04");

				String technicianName1 = eachProductStore.getString("technicianName01");
				String technicianName2 = eachProductStore.getString("technicianName02");
				String technicianName3 = eachProductStore.getString("technicianName03");
				String technicianName4 = eachProductStore.getString("technicianName04");

				if (UtilValidate.isNotEmpty(state)) {
					state = DataUtil.getGeoName(delegator, state, "STATE/PROVINCE");
				}
				if (UtilValidate.isNotEmpty(country)) {
					country = DataUtil.getGeoName(delegator, country, "COUNTRY");
				}

				data.put("state", state);
				data.put("country", country);
				data.put("isTechInspection", isTechInspection);
				data.put("storeId", productStoreId);
				data.put("storeName", productStoreName);

				data.put("technician1", UtilValidate.isNotEmpty(technician1) ? technician1 : "");
				data.put("technician2", UtilValidate.isNotEmpty(technician2) ? technician2 : "");
				data.put("technician3", UtilValidate.isNotEmpty(technician3) ? technician3 : "");
				data.put("technician4", UtilValidate.isNotEmpty(technician4) ? technician4 : "");

				data.put("technicianName1", UtilValidate.isNotEmpty(technicianName1) ? technicianName1 : "");
				data.put("technicianName2", UtilValidate.isNotEmpty(technicianName2) ? technicianName2 : "");
				data.put("technicianName3", UtilValidate.isNotEmpty(technicianName3) ? technicianName3 : "");
				data.put("technicianName4", UtilValidate.isNotEmpty(technicianName4) ? technicianName4 : "");

				results.add(data);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String removeTechnicianLocation(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		String requestData = DataUtil.getJsonStrBody(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String responseMessage = "Location and Technicians Removed Successfully";

		try{

			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			List<GenericValue> toBeRemove = new LinkedList<GenericValue>();
			for(Map<String, Object> data : dataList) {
				String country = (String) data.get("country");
				String state = (String) data.get("state");
				String isTechInspection = (String) data.get("isTechInspection");

				List < EntityCondition > conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("county", EntityOperator.EQUALS, country));
				conditions.add(EntityCondition.makeCondition("state", EntityOperator.EQUALS, state));
				conditions.add(EntityCondition.makeCondition("isTechInspection", EntityOperator.EQUALS, isTechInspection));

				EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue productStoreTechAssoc = EntityQuery.use(delegator).from("ProductStoreTechAssoc").where(condition).queryFirst();

				if(UtilValidate.isNotEmpty(productStoreTechAssoc)) {
					toBeRemove.add(productStoreTechAssoc);
				}
			}
			if(toBeRemove.size() > 0){
				delegator.removeAll(toBeRemove);
			}
		}catch (Exception e) {
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return CommonEvents.doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
		return CommonEvents.doJSONResponse(response, result);

	}

	public static String getContractorTechnicians(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		try{

			Map<String, Map<String, Object>> techList = new LinkedHashMap<String, Map<String, Object>>();

			techList = ResAvailUtil.getContractorTechList(delegator, techList, UtilMisc.toMap("state", null, "county", null), "CONTRACTOR");

			if (UtilValidate.isNotEmpty(techList)) {
				for (String technicianId : techList.keySet()) {
					Map<String, Object> data = new HashMap<String, Object>();
					Map<String, Object> tech = techList.get(technicianId);
					data.put("technicianId", technicianId);
					data.put("name", tech.get("name"));
					results.add(data);
				}
			}

			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "TECHNICIAN"));
			conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ASSIGNED_TO"));

			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

			Set<String> fieldToSelect = new LinkedHashSet<>();
			fieldToSelect.add("partyIdTo");
			fieldToSelect.add("roleTypeIdTo");

			List<GenericValue> techAssocList = delegator.findList("PartyRelationship", mainConditons, fieldToSelect, null, null, false);

			if (UtilValidate.isNotEmpty(techAssocList)) {
				for (GenericValue techAssoc : techAssocList) {
					Map<String, Object> data = new LinkedHashMap<String, Object>();
					String partyId = techAssoc.getString("partyIdTo");
					String name = PartyHelper.getPartyName(delegator, partyId, false);
					data.put("technicianId", partyId);
					data.put("name", PartyHelper.getPartyName(delegator, partyId, false));
					results.add(data);
				}
			}

		}catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getProductStores(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		try{

			List<GenericValue> productStoreList = EntityQuery.use(delegator).select("productStoreId","storeName").from("ProductStore").queryList();
			if (UtilValidate.isNotEmpty(productStoreList)) {
				for (GenericValue eachStore : productStoreList) {
					Map<String, Object> data = new LinkedHashMap<String, Object>();
					String storeId = eachStore.getString("productStoreId");
					String storeName = eachStore.getString("storeName");
					data.put("storeId", storeId);
					data.put("storeName", storeName);
					results.add(data);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String sendMfaCode(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator =  (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		String sendTo = request.getParameter("sendTo");
		try {
			/*
			if(UtilValidate.isNotEmpty(sendTo) && "MFA_ADMIN".equals(sendTo)) {
				String adminUserLoginId = EntityUtilProperties.getPropertyValue("mfa", "mfa.admin.userloginId", "admin", delegator);
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, adminUserLoginId),
						EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"),
								EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, ""),
								EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, null)
								)
						);
				GenericValue adminUserLogin = EntityQuery.use(delegator).from("UserLogin").where(condition).queryFirst();
				if(UtilValidate.isNotEmpty(adminUserLogin)) {
					userLogin = adminUserLogin;
				} else {
					result.put("response", "error");
    				result.put("message", "Please configure admin userID");
    				return doJSONResponse(response, result);
				}
			}
			 */
			String mfaEnabled = EntityUtilProperties.getPropertyValue("mfa", "mfa.enabled", "false", delegator);

			if(UtilValidate.isNotEmpty(mfaEnabled) && Boolean.parseBoolean(mfaEnabled)) {
				try {
					Map<String, Object> sendEmailMfaCodeRes = dispatcher.runSync("sendEmailMfaCode", UtilMisc.toMap("userLogin",userLogin,"resendFlag", "N"));
					Debug.log("sendEmailMfaCodeRes===== "+sendEmailMfaCodeRes);
					if(ServiceUtil.isSuccess(sendEmailMfaCodeRes)) {
						request.setAttribute("_EVENT_MESSAGE_", "A Verification code has been sent to your email address");
						request.setAttribute("getMfaMailCode", true);
						result.put("response", "success");
						result.put("message", "A Verification code has been sent");

					}else {
						Debug.logError("MFA Service error", MODULE);
						result.put("response", "error");
						result.put("message", "MFA Service error");
					}

				} catch (GenericServiceException e) {
					Debug.logError(e, "Error calling sendEmailMfaCode service", MODULE);
					result.put("response", "error");
					result.put("message", "MFA Service error");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, result);
	}

	public static String verifyMfaCode(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator =  (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String emailMfaCode = request.getParameter("mfaCode");
		Map<String, Object> result = new HashMap<String, Object>();
		String sendTo = request.getParameter("sendTo");
		try {
			/*
			if(UtilValidate.isNotEmpty(sendTo) && "MFA_ADMIN".equals(sendTo)) {
				String adminUserLoginId = EntityUtilProperties.getPropertyValue("mfa", "mfa.admin.userloginId", "admin", delegator);
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, adminUserLoginId),
						EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"),
								EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, ""),
								EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, null)
								)
						);
				GenericValue adminUserLogin = EntityQuery.use(delegator).from("UserLogin").where(condition).queryFirst();
				if(UtilValidate.isNotEmpty(adminUserLogin)) {
					userLogin = adminUserLogin;
				} else {
					result.put("response", "error");
    				result.put("message", "Please configure admin userID");
    				return doJSONResponse(response, result);
				}
			}
			 */
			String partyId = userLogin.getString("partyId");
			String mfaEnabled = EntityUtilProperties.getPropertyValue("mfa", "mfa.enabled", "false", delegator);

			if(UtilValidate.isNotEmpty(mfaEnabled) && Boolean.parseBoolean(mfaEnabled)) {
				try {
					Map<String, Object> verifyMfaMailCodeRes = dispatcher.runSync("verifyMfaMailCode", 
							UtilMisc.toMap("partyId",partyId,"code",emailMfaCode));

					result.put("response", "success");
					result.put("message", "Code verified successfully.");

					if(ServiceUtil.isError(verifyMfaMailCodeRes)) {
						request.setAttribute("_ERROR_MESSAGE_", "Service error : verifyMfaMailCode");
						result.put("response", "error");
						result.put("message", "Service error : verifyMfaMailCode");
					}

					Boolean verified  = (Boolean) verifyMfaMailCodeRes.get("verified");

					if(!verified) {
						request.setAttribute("_ERROR_MESSAGE_", "Authentication code has expired/invalid code");
						result.put("response", "error");
						result.put("message", "Authentication code has expired/invalid code");

					}
				} catch (GenericServiceException e) {
					Debug.logError(e, "Error calling sendEmailMfaCode service", MODULE);
					result.put("response", "error");
					result.put("message", "Error calling verifyMfaMailCode service");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("response", "error");
			result.put("message", "Error : "+ e.getMessage());
			return doJSONResponse(response, result);
		}
		return doJSONResponse(response, result);
	}

	public static String addOrViewComment(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();

		String parameterId = request.getParameter("parameterId");
		result.put("parameterId", parameterId);
		try {
			GenericValue commentValue = delegator.findOne("PretailLoyaltyGlobalParameters", false,
					UtilMisc.toMap("parameterId", parameterId));
			if (UtilValidate.isNotEmpty(commentValue)) {
				String comment = commentValue.getString("comments");
				if (UtilValidate.isNotEmpty(comment)) {
					result.put("comment", comment);
				}
			}

		} catch (GenericEntityException e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			result.put("errorMessage", e.getMessage());
			result.put("responseMessage", "error");
			result.put("data", new ArrayList<Map<String, Object>>());
		}
		result.put("responseMessage", "success");
		result.put("successMessage", "Data Loaded successfully.");
		return doJSONResponse(response, result);
	}
	public static String addComment(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		String comment = (String) request.getParameter("comment");
		Map<String, Object> results = FastMap.newInstance();
		String parameterId = (String) request.getParameter("parameterId");
		if(UtilValidate.isNotEmpty(comment))
		{
			try {
				GenericValue parameterCommentFromTable =delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", parameterId), false);
				if(UtilValidate.isNotEmpty(parameterCommentFromTable))
				{
					String commentFromTable = (String) parameterCommentFromTable.get("comments");
					if(commentFromTable == null)
					{
						parameterCommentFromTable.put("comments", comment);
						parameterCommentFromTable.store();
						results.put(SUCCESS, "success");
						results.put(SUCCESS_MESSAGE, "Comments created successfully");
					}
					else
					{
						parameterCommentFromTable.put("comments", comment);
						parameterCommentFromTable.store();
						results.put(SUCCESS, "success");
						results.put(SUCCESS_MESSAGE, "Comments updated successfully");
					}
				}
			} catch (GenericEntityException e1) {
				results.put(ERROR, "error");
				return doJSONResponse(response, results);
			}
		}
		return doJSONResponse(response, results);
	}
	public static String searchGlobalParameters(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String[] sectionNames = request.getParameterValues("globalParamSection");
		Map<String,List<GenericValue>> mapToSend =new LinkedHashMap<>();
		List<GenericValue> emptyList = FastList.newInstance();
		List<EntityCondition> serviceConditionList = FastList.newInstance();

		Set < String > fieldsToSelect = new TreeSet < String > ();
		fieldsToSelect.add("parameterId");
		fieldsToSelect.add("description");
		fieldsToSelect.add("value");
		fieldsToSelect.add("storeId");
		fieldsToSelect.add("comments");

		String description = request.getParameter("parameterName");
		if(UtilValidate.isNotEmpty(description))
		{
			List<GenericValue> sectionDetails = delegator.findByAnd("Enumeration",
					UtilMisc.toMap("enumTypeId", AdminPortalConstant.GlobalParameter.GLOBAL_PARAMS),UtilMisc.toList("sequenceId ASC"), false);
			if(UtilValidate.isNotEmpty(sectionDetails))
			{
				for(GenericValue section :sectionDetails )
				{
					String enumId = (String) section.get("enumId");
					String parameterName = (String) section.get("description");
					EntityCondition storeIdCondition =EntityCondition.makeCondition("storeId", EntityOperator.EQUALS, enumId);
					EntityCondition parameterCondition =EntityCondition.makeCondition("description", EntityOperator.LIKE, "%"+description+"%");
					EntityCondition mainCondition = EntityCondition.makeCondition(storeIdCondition,EntityOperator.AND,parameterCondition);
					List<GenericValue> globalParameterValue = delegator.findList("PretailLoyaltyGlobalParameters", mainCondition, fieldsToSelect, UtilMisc.toList("lastUpdatedTxStamp DESC"), null, false);
					if(UtilValidate.isNotEmpty(globalParameterValue)) {
						mapToSend.put(parameterName, globalParameterValue);
					}
				}
			}
			return doJSONResponse(response, mapToSend);
		}
		List<GenericValue> sectionDetails = delegator.findByAnd("Enumeration",
				UtilMisc.toMap("enumTypeId", AdminPortalConstant.GlobalParameter.GLOBAL_PARAMS),UtilMisc.toList("sequenceId ASC"), false);
		if (UtilValidate.isNotEmpty(sectionNames)) {
			for (String sectionName : sectionNames) {
				if (UtilValidate.isNotEmpty(sectionDetails)) {
					for (GenericValue sectionDetail : sectionDetails) {
						String section = (String) sectionDetail.get("enumId");
						if (section.equals(sectionName)) {
							String parameterName = (String) sectionDetail.get("description");
							EntityCondition findService =EntityCondition.makeCondition("storeId", EntityOperator.EQUALS, section);
							List<GenericValue> globalParameterValue = delegator.findList("PretailLoyaltyGlobalParameters", findService, fieldsToSelect, UtilMisc.toList("lastUpdatedTxStamp DESC"), null, false);
							if(UtilValidate.isNotEmpty(globalParameterValue)) {
								mapToSend.put(parameterName, globalParameterValue);
							}
							else
							{
								GenericValue forNoParamSection =delegator.findOne("Enumeration", UtilMisc.toMap("enumId", section), false);
								mapToSend.put(parameterName, globalParameterValue);
							}
						}
					}
				}
			}
			return doJSONResponse(response, mapToSend);
		}
		else
		{
			List<GenericValue> allSections = delegator.findByAnd("Enumeration",
					UtilMisc.toMap("enumTypeId", AdminPortalConstant.GlobalParameter.GLOBAL_PARAMS), UtilMisc.toList("sequenceId ASC"), false);
			if (UtilValidate.isNotEmpty(allSections)) {
				for (GenericValue aSection : allSections) {
					if (UtilValidate.isNotEmpty(aSection)) {
						String enumId = (String) aSection.get("enumId");
						String sectionName = (String) aSection.get("description");
						EntityCondition findServiceCondition =EntityCondition.makeCondition("storeId", EntityOperator.EQUALS, enumId);
						List<GenericValue> globalParameterValue = delegator.findList("PretailLoyaltyGlobalParameters", findServiceCondition, fieldsToSelect, UtilMisc.toList("lastUpdatedTxStamp DESC"), null, false);
						mapToSend.put(sectionName, globalParameterValue);
					}
				}
			}
			return doJSONResponse(response, mapToSend);
		}
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @author Nirmal Kumar P.
	 * @since 26-10-2022
	 * @return ComponentList
	 * @throws GenericEntityException
	 */
	public static String componentList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String componentId = (String) context.get("componentId");
		String isHide = (String) context.get("isHide");
		try {
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(componentId)) {
				conditions.add(EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId));
			}
			if(UtilValidate.isNotEmpty(isHide)){
				conditions.add(EntityCondition.makeCondition("isHide",EntityOperator.EQUALS, isHide));
			} 
			EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < GenericValue > componentList = EntityQuery.use(delegator).select("componentId","uiLabels","isHide","requestUri").from("OfbizComponentAccess").where(mainCondition).orderBy("-lastUpdatedTxStamp").queryList();
			for (GenericValue component: componentList) {
				Map<String, Object> data = new HashMap<String, Object>();
				String uiLabels = component.getString("uiLabels");
				isHide = component.getString("isHide");
				String requestUri = component.getString("requestUri");
				componentId = component.getString("componentId");
				data.put("uiLabels", uiLabels);
				data.put("isHide", isHide);
				data.put("requestUri",requestUri);
				data.put("componentId",componentId);
				results.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @author Nirmal Kumar P.
	 * @since 26-10-2022
	 * @return Tab Shortcut List
	 * @throws GenericEntityException
	 */
	public static String tabShortcut(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String tabId = (String) context.get("tabId");
		String isDisabled = (String) context.get("isDisabled");
		String componentId = request.getParameter("componentId");
		try {
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(componentId)){
				conditions.add(EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId));
			}
			if (UtilValidate.isNotEmpty(tabId)) {
				conditions.add(EntityCondition.makeCondition("tabId", EntityOperator.EQUALS, tabId));
			}

			if(UtilValidate.isNotEmpty(isDisabled)){
				conditions.add(EntityCondition.makeCondition("isDisabled",EntityOperator.EQUALS, isDisabled));
			}
			EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < GenericValue > tabShortcuts = EntityQuery.use(delegator).select("componentId","tabId","pageId","permissionId","uiLabels","requestUri","isDisabled").from("OfbizPageSecurity").where(conditions).orderBy("-lastUpdatedTxStamp").queryList();
			for (GenericValue tab: tabShortcuts) {
				Map<String, Object> data = new HashMap<String, Object>();
				String uiLabels = tab.getString("uiLabels");
				String requestUri = tab.getString("requestUri");
				isDisabled = tab.getString("isDisabled");
				tabId = tab.getString("tabId");
				String pageId = tab.getString("pageId");
				String permissionId=tab.getString("permissionId");
				componentId =tab.getString("componentId");

				data.put("uiLabels",uiLabels);
				data.put("requestUri",requestUri);
				data.put("isDisabled", isDisabled);
				data.put("tabId", tabId);
				data.put("pageId",pageId);
				data.put("permissionId", permissionId);
				data.put("componentId", componentId);
				results.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @author Nirmal Kumar P.
	 * @since 26-10-2022
	 * @return Sub Menu List
	 * @throws GenericEntityException
	 */
	public static String subMenu(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String componentId = request.getParameter("componentId");
		String tabId = request.getParameter("tabId");
		try{
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(tabId)) {
				conditions.add(EntityCondition.makeCondition("tabId", EntityOperator.EQUALS, tabId));
			}
			if (UtilValidate.isNotEmpty(componentId)) {
				conditions.add(EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId));
			}
			EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> productStoreList = EntityQuery.use(delegator).select("tabId","uiLabels","componentId","pageId","permissionId","shortcutId","isDisabled","requestUri").from("OfbizTabSecurityShortcut").orderBy("-lastUpdatedTxStamp").where(mainCondition).queryList();
			if (UtilValidate.isNotEmpty(productStoreList)) {
				for (GenericValue eachStore : productStoreList) {
					Map<String, Object> data = new LinkedHashMap<String, Object>();
					componentId =eachStore.getString("componentId");
					tabId= eachStore.getString("tabId");
					String uiLabels = eachStore.getString("uiLabels");
					String isDisabled = eachStore.getString("isDisabled");
					String permissionId=eachStore.getString("permissionId");
					String pageId = eachStore.getString("pageId");
					String shortcutId = eachStore.getString("shortcutId");
					String requestUri = eachStore.getString("requestUri");

					data.put("uiLabels", uiLabels);
					data.put("permissionId", permissionId);
					data.put("componentId", componentId);
					data.put("tabId", tabId);
					data.put("pageId", pageId);
					data.put("shortcutId", shortcutId);
					data.put("isDisabled", isDisabled);
					data.put("requestUri", requestUri);
					results.add(data);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	public static String srFieldsValidation (HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		Map<String, Object> results = new LinkedHashMap<String, Object>();
		String typeId = (String) context.get("typeId");
		String srCategory = (String) context.get("category");
		String srTypeName = (String) context.get("srType");
		String srSubCategoryName = (String) context.get("srSubArea");
		String categoryId = (String) context.get("srCategoryId");
		String tableName = "";
		try {
			boolean isAvailable = false;
			EntityCondition entityCondition = null;
			if(UtilValidate.isNotEmpty(typeId) && UtilValidate.isNotEmpty(srCategory)) {
				entityCondition = EntityCondition.makeCondition(EntityOperator.AND,EntityCondition.makeCondition("parentCustRequestCategoryId",typeId),EntityCondition.makeCondition("description",srCategory));
				tableName = "CustRequestCategory";
			}
			if(UtilValidate.isNotEmpty(srTypeName)) {
				entityCondition = EntityCondition.makeCondition("description",srTypeName);
				tableName = "CustRequestType";
			}
			if(UtilValidate.isNotEmpty(srSubCategoryName) && UtilValidate.isNotEmpty(categoryId)) {
				entityCondition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("value",srSubCategoryName),
						EntityCondition.makeCondition("parentCode",categoryId),
						EntityCondition.makeCondition("type","SRSubCategory")),
						EntityOperator.AND);
				tableName = "CustRequestAssoc";
			}
			if(UtilValidate.isNotEmpty(tableName))
				isAvailable = EntityQuery.use(delegator).from(tableName).where(entityCondition).queryCount() > 0 ;
				results.put("isAvailable", isAvailable ? "Y":"N");
		}catch (GenericEntityException e) {
			e.printStackTrace();
		}
		results.put(SUCCESS, "success");
		return doJSONResponse(response, results);
	}

	public static String getNavigationTabList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		String componentId = request.getParameter("searchComponentId");
		String tabConfigId = request.getParameter("tabConfigId");
		String tabId = request.getParameter("tabId");
		String isEnabled = request.getParameter("isEnabled");
		Locale locale = UtilHttp.getLocale(request);
		try{

			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("componentId");
			fieldsToSelect.add("tabConfigId");
			fieldsToSelect.add("tabId");
			fieldsToSelect.add("tabName");
			fieldsToSelect.add("permissionId");
			fieldsToSelect.add("isEnabled");
			fieldsToSelect.add("favIcon");
			fieldsToSelect.add("tabContent");
			fieldsToSelect.add("sequenceNo");
			Map<String, Object> data = new HashMap<String, Object>();
			List<GenericValue> componentList = EntityQuery.use(delegator).select("componentName","description").from("OfbizComponentAccess").where(EntityCondition.makeCondition("isHide",EntityOperator.EQUALS,"N")).queryList();
			if(UtilValidate.isNotEmpty(componentList)) {
				for(GenericValue componentMap : componentList) {
					String componentName = componentMap.getString("componentName");
					componentName = UtilValidate.isNotEmpty(componentName)?componentName.replace("-", "_").toUpperCase():"";
					data.put(componentName, componentMap.getString("description"));
				}
			}
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if(UtilValidate.isNotEmpty(componentId)) {
				componentId = componentId.replace("-", "_").toUpperCase();
				conditions.add(EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId));
			}
			if(UtilValidate.isNotEmpty(tabConfigId)){
				conditions.add(EntityCondition.makeCondition("tabConfigId",EntityOperator.LIKE, "%"+tabConfigId+"%"));
			}
			if(UtilValidate.isNotEmpty(tabId)){
				conditions.add(EntityCondition.makeCondition("tabId",EntityOperator.LIKE, "%"+tabId+"%"));
			}
			if(UtilValidate.isNotEmpty(isEnabled)){
				conditions.add(EntityCondition.makeCondition("isEnabled",EntityOperator.EQUALS,isEnabled));
			}
			EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> navTabList = EntityQuery.use(delegator).select(fieldsToSelect).from("NavTabsConfig").where(mainCondition).queryList();
			if (UtilValidate.isNotEmpty(navTabList)) {
				for(GenericValue navTabGv : navTabList) {
					Map<String, Object> resultData = new LinkedHashMap<String, Object>();
					resultData.put("componentId", UtilValidate.isNotEmpty(navTabGv.getString("componentId"))?navTabGv.getString("componentId"):"");
					resultData.put("tabConfigId", UtilValidate.isNotEmpty(navTabGv.getString("tabConfigId"))?navTabGv.getString("tabConfigId"):"");
					resultData.put("tabId", UtilValidate.isNotEmpty(navTabGv.getString("tabId"))?navTabGv.getString("tabId"):"");
					resultData.put("tabName", UtilValidate.isNotEmpty(navTabGv.getString("tabName"))?navTabGv.getString("tabName"):"");
					resultData.put("permissionId", UtilValidate.isNotEmpty(navTabGv.getString("permissionId"))?navTabGv.getString("permissionId"):"");
					resultData.put("isEnabled", UtilValidate.isNotEmpty(navTabGv.getString("isEnabled"))?navTabGv.getString("isEnabled"):"");
					resultData.put("favIcon", UtilValidate.isNotEmpty(navTabGv.getString("favIcon"))?navTabGv.getString("favIcon"):"");
					resultData.put("tabContent", UtilValidate.isNotEmpty(navTabGv.getString("tabContent"))?navTabGv.getString("tabContent"):"");
					resultData.put("sequenceNo", UtilValidate.isNotEmpty(navTabGv.getString("sequenceNo"))?navTabGv.getString("sequenceNo"):"");
					resultData.put("description", UtilValidate.isNotEmpty(navTabGv.getString("componentId"))?data.get(navTabGv.getString("componentId")):"");
					results.add(resultData);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getDetailsForEditNavPopup(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String componentId = request.getParameter("componentId");
		String tabConfigId = request.getParameter("tabConfigId");
		String tabId = request.getParameter("tabId");
		Map<String, Object> data = new HashMap<String, Object>();
		List<EntityCondition> navTabConditions = new ArrayList<EntityCondition>();
		if (UtilValidate.isNotEmpty(componentId) && UtilValidate.isNotEmpty(tabConfigId) && UtilValidate.isNotEmpty(tabId)) {
			navTabConditions.add(EntityCondition.makeCondition("componentId",EntityOperator.EQUALS, componentId));
			navTabConditions.add(EntityCondition.makeCondition("tabConfigId",EntityOperator.EQUALS, tabConfigId));
			navTabConditions.add(EntityCondition.makeCondition("tabId",EntityOperator.EQUALS, tabId));
		}
		EntityCondition navTabMainCondition = EntityCondition.makeCondition(navTabConditions, EntityOperator.AND);
		try{

			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("tabName");
			fieldsToSelect.add("permissionId");
			fieldsToSelect.add("isEnabled");
			fieldsToSelect.add("favIcon");
			fieldsToSelect.add("tabContent");
			fieldsToSelect.add("sequenceNo");
			GenericValue navTabGv = EntityQuery.use(delegator).select(fieldsToSelect).from("NavTabsConfig").where(navTabMainCondition).queryOne();
			if (UtilValidate.isNotEmpty(navTabGv)) {
				componentId = UtilValidate.isNotEmpty(componentId)?componentId.replace("_","-").toLowerCase():"";
				GenericValue componentDescription = EntityQuery.use(delegator).select("description").from("OfbizComponentAccess").where(UtilMisc.toMap("componentName", componentId)).queryFirst();
				data.put("description", UtilValidate.isNotEmpty(componentDescription.getString("description")) ? componentDescription.getString("description") : "");
				data.put("tabName", UtilValidate.isNotEmpty(navTabGv.getString("tabName")) ? navTabGv.getString("tabName") : "");
				data.put("permissionId", UtilValidate.isNotEmpty(navTabGv.getString("permissionId")) ? navTabGv.getString("permissionId") : "");
				data.put("isEnabled", UtilValidate.isNotEmpty(navTabGv.getString("isEnabled")) ? navTabGv.getString("isEnabled") : "");
				data.put("favIcon", UtilValidate.isNotEmpty(navTabGv.getString("favIcon")) ? navTabGv.getString("favIcon") : "");
				data.put("tabContent", UtilValidate.isNotEmpty(navTabGv.getString("tabContent")) ? navTabGv.getString("tabContent") : "");
				data.put("sequenceNo", UtilValidate.isNotEmpty(navTabGv.getString("sequenceNo")) ? navTabGv.getString("sequenceNo") : "");
				data.put("success", "success");
			}
		}catch (Exception e) {
			e.printStackTrace();
			data.put("error", "error");
			data.put("errorMessage", e.getMessage());
			return doJSONResponse(response, data);
		}
		return doJSONResponse(response, data);
	}
	public static String getSendGridConfig(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> results = new HashMap<String, Object>();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		List<Map<String, Object>> dataList = FastList.newInstance();
		String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
		try {
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}
			results.put("viewIndex", Integer.valueOf(viewIndex));
			int viewSize = 0;
			try {
				viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
			}catch (Exception e) {
				try {
					viewSize = Integer.parseInt(org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FIO_GRID_FETCH_LIMIT"));
				}catch(Exception ex) {
					viewSize=1000;
				}
			}
			results.put("viewSize", Integer.valueOf(viewSize));
			int highIndex = 0;
			int lowIndex = 0;
			int resultListSize = 0;
			lowIndex = viewIndex * viewSize + 1;
			highIndex = (viewIndex + 1) * viewSize;
			List<GenericValue> sendGridConfig;
			try {
				sendGridConfig = EntityQuery.use(delegator).from("SendGridConfig").orderBy("lastUpdatedStamp").maxRows(highIndex).queryList();
				if (sendGridConfig != null && sendGridConfig.size() > 0) {
					for (GenericValue sendGridConfigValues : sendGridConfig) {
						Map<String, Object> data = org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, sendGridConfigValues);
						data.put("lastUpdatedTxStamp",UtilValidate.isNotEmpty(sendGridConfigValues .getTimestamp("lastUpdatedTxStamp"))?
								UtilDateTime.timeStampToString(sendGridConfigValues .getTimestamp("lastUpdatedTxStamp"),globalDateTimeFormat, TimeZone.getDefault(), null): "");
						data.put("createdTxStamp",UtilValidate.isNotEmpty(sendGridConfigValues .getTimestamp("createdTxStamp"))?
								UtilDateTime.timeStampToString(sendGridConfigValues .getTimestamp("createdTxStamp"),globalDateTimeFormat, TimeZone.getDefault(), null): "");
						dataList.add(data);
					}
					results.put("data", dataList);
					results.put("responseMessage", "success");
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				results.put("errorMessage", e.getMessage());
				results.put("responseMessage", "error");
				results.put("data", new ArrayList<Map<String, Object>>());
			}
			results.put("highIndex", Integer.valueOf(highIndex));
			results.put("lowIndex", Integer.valueOf(lowIndex));
			results.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			results.put("totalRecords", nf.format(resultListSize));
			results.put("recordCount", resultListSize);
			results.put("chunkSize", viewSize);
		} catch (Exception e) {
			e.printStackTrace();
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("data", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, results);
	}
	public static String createOrUpdateSendGridConfig(HttpServletRequest request, HttpServletResponse response)throws ParseException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Map<String, Object> results = new LinkedHashMap<String, Object>();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		try {
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			Map<String, Object> requestContext = new LinkedHashMap<>();
			String externalLoginKey=(String)context.get("externalLoginKey");
			requestContext.putAll(context);
			callCtxt.put("userLogin", userLogin);
			callCtxt.put("requestContext", requestContext);
			callResult = dispatcher.runSync("ap.createOrUpdateSendGridConfig", callCtxt);
			if (ServiceUtil.isSuccess(callResult)) {
				request.setAttribute("_EVENT_MESSAGE_", "Send Grid Created Successfully");
				request.setAttribute("externalLoginKey", externalLoginKey);
			} else if (ServiceUtil.isError(callResult) || ServiceUtil.isFailure(callResult)) {
				return ERROR;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	public static String editSendGridConfig(HttpServletRequest request, HttpServletResponse response)
			throws ParseException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Map<String, Object> results = new LinkedHashMap<String, Object>();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		try {
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			Map<String, Object> requestContext = new LinkedHashMap<>();
			requestContext.putAll(context);
			callCtxt.put("userLogin", userLogin);
			callCtxt.put("requestContext", requestContext);
			callResult = dispatcher.runSync("ap.createOrUpdateSendGridConfig", callCtxt);
			if (ServiceUtil.isSuccess(callResult)) {
				results.put("responseMessage", callResult.get("responseMessage"));
				results.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				results.put(GlobalConstants.RESPONSE_MESSAGE, "success");
			} else if (ServiceUtil.isError(callResult) || ServiceUtil.isFailure(callResult)) {
				results.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				results.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(callResult));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, "error while calling the service");
		}
		return doJSONResponse(response, results);
	}
	public static String viewEmailTracking(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> results = new HashMap<String, Object>();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		List<Map<String, Object>> dataList = FastList.newInstance();
		String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		String createdDatefrom = (String) context.get("createdDate_from");
		String createdDateTo = (String) context.get("createdDate_to");
		String campaignId = (String) context.get("campaignId");
		String statusId = (String) context.get("status");
		
		String campaignName = (String) context.get("campaignName");
		String subject = (String) context.get("subject");
		String publishedDate = (String) context.get("publishedDate");
		String emailEngine = (String) context.get("emailEngine");
		try {
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}
			results.put("viewIndex", Integer.valueOf(viewIndex));
			int viewSize = 0;
			try {
				viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
			} catch (Exception e) {
				try {
					viewSize = Integer.parseInt(org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FIO_GRID_FETCH_LIMIT"));
				}catch(Exception ex) {
					viewSize=1000;
				}
			}
			results.put("viewSize", Integer.valueOf(viewSize));
			int highIndex = 0;
			int lowIndex = 0;
			int resultListSize = 0;
			lowIndex = viewIndex * viewSize + 1;
			highIndex = (viewIndex + 1) * viewSize;
			List<GenericValue> emailData;
			List<EntityCondition> mcConditions = new ArrayList<EntityCondition>();
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("subject");
			fieldsToSelect.add("description");
			fieldsToSelect.add("fromEmail");
			fieldsToSelect.add("toEmail");
			fieldsToSelect.add("ccEmail");
			fieldsToSelect.add("publishDate");
			fieldsToSelect.add("referenceId");
			fieldsToSelect.add("intTplId");
			fieldsToSelect.add("statusId");
			fieldsToSelect.add("extTplId");
			fieldsToSelect.add("emailEngine");
			fieldsToSelect.add("requestedTime");
			fieldsToSelect.add("responsedTime");
			fieldsToSelect.add("responseCode");
			fieldsToSelect.add("createdOn");
			fieldsToSelect.add("createdByUserLogin");
			fieldsToSelect.add("responsedHeaders");
			fieldsToSelect.add("intContactListId");
			fieldsToSelect.add("clientName");
			fieldsToSelect.add("partyId");
			fieldsToSelect.add("personalizationTags");
			fieldsToSelect.add("lastUpdatedTxStamp");
			fieldsToSelect.add("createdTxStamp");
			try {
				if (UtilValidate.isNotEmpty(campaignId))
					mcConditions.add(EntityCondition.makeCondition("referenceId", EntityOperator.EQUALS,campaignId));
				if (UtilValidate.isNotEmpty(createdDatefrom)&&UtilValidate.isEmpty(createdDateTo)) {
					Timestamp createdDatefromTs = UtilDateTime.stringToTimeStamp(createdDatefrom, globalDateFormat, TimeZone.getDefault(),Locale.getDefault());
					mcConditions.add(EntityCondition.makeCondition("createdTxStamp",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(createdDatefromTs)));
				}
				if (UtilValidate.isNotEmpty(createdDateTo)&&UtilValidate.isEmpty(createdDatefrom)) {
					Timestamp createdDateToTs = UtilDateTime.stringToTimeStamp(createdDateTo, globalDateFormat, TimeZone.getDefault(),Locale.getDefault());
					mcConditions.add(EntityCondition.makeCondition("createdTxStamp",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayStart(createdDateToTs)));
				}
				if (UtilValidate.isNotEmpty(createdDatefrom)&& UtilValidate.isNotEmpty(createdDateTo)) {
					Timestamp createdDatefromTs = UtilDateTime.stringToTimeStamp(createdDatefrom, globalDateFormat, TimeZone.getDefault(),Locale.getDefault());
					Timestamp createdDateToTs = UtilDateTime.stringToTimeStamp(createdDateTo, globalDateFormat, TimeZone.getDefault(),Locale.getDefault());
					mcConditions.add(EntityCondition.makeCondition("createdTxStamp",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(createdDatefromTs)));
					mcConditions.add(EntityCondition.makeCondition("createdTxStamp",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayStart(createdDateToTs)));
				}
				if (UtilValidate.isNotEmpty(statusId)) {
					mcConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
				}
				if (UtilValidate.isNotEmpty(publishedDate)) {
					Timestamp publishedDateTs = UtilDateTime.stringToTimeStamp(publishedDate, globalDateFormat, TimeZone.getDefault(),Locale.getDefault());
					mcConditions.add(EntityCondition.makeCondition("publishDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(publishedDateTs)));
					mcConditions.add(EntityCondition.makeCondition("publishDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(publishedDateTs)));
				}
				if (UtilValidate.isNotEmpty(subject)) {
					mcConditions.add(EntityCondition.makeCondition("subject",EntityOperator.LIKE, "%"+subject+"%"));
				}
				if (UtilValidate.isNotEmpty(emailEngine)) {
					mcConditions.add(EntityCondition.makeCondition("emailEngine", EntityOperator.EQUALS, emailEngine));
				}
				if (UtilValidate.isNotEmpty(campaignName)) {
					mcConditions.add(EntityCondition.makeCondition("description",EntityOperator.LIKE, "%"+campaignName+"%"));
				}
				
				emailData = EntityQuery.use(delegator).select(fieldsToSelect).from("NotifyEmailData").where(mcConditions).maxRows(highIndex).queryList();
				if (emailData != null && emailData.size() > 0) {
					for (GenericValue emailDataValues : emailData) {
						Map<String, Object> data = org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, emailDataValues);
						data.put("requestedTime",UtilValidate.isNotEmpty(emailDataValues.getTimestamp("requestedTime"))?
								UtilDateTime.timeStampToString(emailDataValues.getTimestamp("requestedTime"),globalDateTimeFormat, TimeZone.getDefault(), null): "");
						data.put("responsedTime",UtilValidate.isNotEmpty(emailDataValues.getTimestamp("responsedTime"))?
								UtilDateTime.timeStampToString(emailDataValues .getTimestamp("responsedTime"),globalDateTimeFormat, TimeZone.getDefault(), null): "");
						data.put("lastUpdatedTxStamp",UtilValidate.isNotEmpty(emailDataValues.getTimestamp("lastUpdatedTxStamp"))?
								UtilDateTime.timeStampToString(emailDataValues .getTimestamp("lastUpdatedTxStamp"),globalDateTimeFormat, TimeZone.getDefault(), null): "");
						data.put("createdTxStamp",UtilValidate.isNotEmpty(emailDataValues.getTimestamp("createdTxStamp"))?
								UtilDateTime.timeStampToString(emailDataValues.getTimestamp("createdTxStamp"),globalDateTimeFormat, TimeZone.getDefault(), null): "");
						if(UtilValidate.isNotEmpty(emailDataValues.getString("statusId"))) {
							String status = EnumUtil.getEnumDescription(delegator,emailDataValues.getString("statusId"));
							data.put("statusId", UtilValidate.isNotEmpty(status)?status:"");
						}
						data.put("publishDate",UtilValidate.isNotEmpty(emailDataValues.getTimestamp("publishDate"))?
								UtilDateTime.timeStampToString(emailDataValues.getTimestamp("publishDate"),globalDateTimeFormat, TimeZone.getDefault(), null): "");
						dataList.add(data);
					}
					results.put("data", dataList);
					results.put("responseMessage", SUCCESS);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				results.put("errorMessage", e.getMessage());
				results.put("responseMessage", ERROR);
				results.put("data", new ArrayList<Map<String, Object>>());
			} catch (ParseException e) {
				results.put("errorMessage", e.getMessage());
				results.put("responseMessage", "date error");
				results.put("data", new ArrayList<Map<String, Object>>());
				e.printStackTrace();
			}
			results.put("highIndex", Integer.valueOf(highIndex));
			results.put("lowIndex", Integer.valueOf(lowIndex));
			results.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			results.put("totalRecords", nf.format(resultListSize));
			results.put("recordCount", resultListSize);
			results.put("chunkSize", viewSize);
		} catch (Exception e) {
			e.printStackTrace();
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", ERROR);
			results.put("data", new ArrayList<Map<String, Object>>());
		} 
		return doJSONResponse(response, results);
	}
	
	public static String getParentBuNames(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		try {
			Set < String > fieldsToSelect = new TreeSet < String > ();
			List < GenericValue > primaryParentGroupIds = EntityQuery.use(delegator).select("primaryParentGroupId").distinct().from("ProductStoreGroup").queryList();
			List<String> primaryParentGroupIdList = EntityUtil.getFieldListFromEntityList(primaryParentGroupIds, "primaryParentGroupId", true);
			fieldsToSelect.add("productStoreGroupId");
			fieldsToSelect.add("productStoreGroupName");
			fieldsToSelect.add("primaryParentGroupId");
			List < GenericValue > productStoreGroups = EntityQuery.use(delegator).select(fieldsToSelect).from("ProductStoreGroup").where(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.IN, primaryParentGroupIdList)).queryList();
			if (productStoreGroups != null && productStoreGroups.size() > 0) {
				for (GenericValue productStoreGroup: productStoreGroups) {
					Map<String, Object> data = new HashMap<String, Object>();
						data.put("primaryParentGroupId", UtilValidate.isNotEmpty(productStoreGroup.getString("productStoreGroupId")) ? productStoreGroup.getString("productStoreGroupId"): "");
						data.put("productStoreGroupName", UtilValidate.isNotEmpty(productStoreGroup.getString("productStoreGroupName")) ? productStoreGroup.getString("productStoreGroupName"): "");
						results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	
	public static String getBuNames(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		try {
			Set < String > fieldsToSelect = new TreeSet < String > ();
			List < GenericValue > primaryParentGroupIds = EntityQuery.use(delegator).select("primaryParentGroupId").distinct().from("ProductStoreGroup").queryList();
			List<String> primaryParentGroupIdList = EntityUtil.getFieldListFromEntityList(primaryParentGroupIds, "primaryParentGroupId", true);
			fieldsToSelect.add("productStoreGroupId");
			fieldsToSelect.add("productStoreGroupName");
			fieldsToSelect.add("primaryParentGroupId");
			List < GenericValue > productStoreGroups = EntityQuery.use(delegator).select(fieldsToSelect).from("ProductStoreGroup").where(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.NOT_IN, primaryParentGroupIdList)).queryList();
			if (productStoreGroups != null && productStoreGroups.size() > 0) {
				for (GenericValue productStoreGroup: productStoreGroups) {
					Map<String, Object> data = DataUtil.convertGenericValueToMap(delegator, productStoreGroup);
						results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	
	public static String getBackConfigurationList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> results = new HashMap<String, Object>();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		List<Map<String, Object>> dataList = FastList.newInstance();
		String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
		try {
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}
			results.put("viewIndex", Integer.valueOf(viewIndex));
			int viewSize = 0;
			try {
				viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
			}catch (Exception e) {
				try {
					viewSize = Integer.parseInt(org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FIO_GRID_FETCH_LIMIT"));
				}catch(Exception ex) {
					viewSize=1000;
				}
			}
			results.put("viewSize", Integer.valueOf(viewSize));
			int highIndex = 0;
			int lowIndex = 0;
			int resultListSize = 0;
			lowIndex = viewIndex * viewSize + 1;
			highIndex = (viewIndex + 1) * viewSize;
			
			try {
				EntityCondition condition = EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "BACKUP_COORDINATOR");
				
				List<GenericValue> backupConfigurationList = EntityQuery.use(delegator).from("PartyAttribute").where(condition).maxRows(highIndex).queryList();
				if (UtilValidate.isNotEmpty(backupConfigurationList)) {
					for (GenericValue backupConfiguration : backupConfigurationList) {
						Map<String, Object> data = org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, backupConfiguration);
						data.put("coordinatorName", org.fio.homeapps.util.DataUtil.getPartyName(delegator, backupConfiguration.getString("partyId")));
						data.put("backCoordinatorName", org.fio.homeapps.util.DataUtil.getPartyName(delegator, backupConfiguration.getString("attrValue")));
						data.put("lastUpdatedTxStamp",UtilValidate.isNotEmpty(backupConfiguration .getTimestamp("lastUpdatedTxStamp"))?
								UtilDateTime.timeStampToString(backupConfiguration .getTimestamp("lastUpdatedTxStamp"),globalDateTimeFormat, TimeZone.getDefault(), null): "");
						data.put("createdTxStamp",UtilValidate.isNotEmpty(backupConfiguration .getTimestamp("createdTxStamp"))?
								UtilDateTime.timeStampToString(backupConfiguration .getTimestamp("createdTxStamp"),globalDateTimeFormat, TimeZone.getDefault(), null): "");
						dataList.add(data);
					}
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				results.put("errorMessage", e.getMessage());
				results.put("responseMessage", "error");
				results.put("list", new ArrayList<Map<String, Object>>());
			}
			results.put("list", dataList);
			results.put("responseMessage", "success");
			results.put("highIndex", Integer.valueOf(highIndex));
			results.put("lowIndex", Integer.valueOf(lowIndex));
			results.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			results.put("totalRecords", nf.format(resultListSize));
			results.put("recordCount", resultListSize);
			results.put("chunkSize", viewSize);
		} catch (Exception e) {
			e.printStackTrace();
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("list", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, results);
	}
	public static String getEntityFields(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
		String entityName = (String) context.get("targetTable");
		if(UtilValidate.isEmpty(entityName)) {
			entityName = (String) context.get("targetPickTable");
		}
		try {
			if(UtilValidate.isNotEmpty(entityName)) {
				//entityName = ModelUtil.dbNameToClassName(entityName);
				ModelReader reader = delegator.getModelReader();
				/*
				ModelEntity entity = reader.getModelEntity(entityName);
				
				List<String> fields = entity.getAllFieldNames();
				//fields.removeAll(UtilMisc.toList("createdStamp","createdTxStamp","lastUpdatedStamp","lastUpdatedTxStamp"));
				fields.removeAll(entity.getAutomaticFieldNames());
				for(String filed : fields) {
					Map<String, Object> data = new HashMap<String, Object>();
					ModelField modelField = entity.getField(filed);
					data.put("id", modelField.getColName());
					data.put("value", filed);
					data.put("label", ModelUtil.upperFirstChar(filed));
					result.add(data);
				}
				*/
				ModelEntity modelEntity = reader.getModelEntity(entityName);
				Iterator fieldIterator = modelEntity.getFieldsIterator();
				while (fieldIterator.hasNext()) {
					Map<String, Object> fieldMap = new LinkedHashMap<String, Object>();
					ModelField field = (ModelField) fieldIterator.next();
					if(!field.getIsAutoCreatedInternal()) {
						String fval = field.getName();
						fieldMap.put("id", field.getColName());
						fieldMap.put("value", fval);
						fieldMap.put("label", ModelUtil.upperFirstChar(fval));
						result.add(fieldMap);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doJSONResponse(response, result);
	}
	
	public static String exportXmlData(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
		String exportEntityName = (String) context.get("exportEntityName");
		String exportFileName = UtilValidate.isNotEmpty(context.get("exportFileName")) ? (String) context.get("exportFileName") : exportEntityName+"_XML_Data";
		try {
			List<GenericValue> dataList = new ArrayList<GenericValue>();
			
			if(UtilValidate.isNotEmpty(exportEntityName)) {
				ModelReader reader = delegator.getModelReader();
				ModelEntity entity = reader.getModelEntity(exportEntityName);
				
				List<String> fields = entity.getAllFieldNames();
				fields.removeAll(entity.getAutomaticFieldNames());
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				for(String field : fields) {
					String paramValue = request.getParameter(field);
					if(UtilValidate.isNotEmpty(paramValue)) {
						conditionList.add(EntityCondition.makeCondition(field, EntityOperator.EQUALS, paramValue));
					}
				}
				if(UtilValidate.isNotEmpty(conditionList)) {
					EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					Set<String> fieldToSelect = fields.stream().collect(Collectors.toSet());
					dataList = EntityQuery.use(delegator).select(fieldToSelect).from(exportEntityName).where(condition).queryList();
				}
				
				String fileName = exportFileName + "_" +new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"."+"xml";
				String exportType = "XML";
				
				Document xmlDoc = GenericValue.makeXmlDocument(dataList);
				
				String xmlContent = UtilXml.writeXmlDocument(xmlDoc);
				
				String componentPath = ComponentConfig.getRootLocation("admin-portal");
				String path = "/webapp/admin-portal-resource/file/";
				String dirPath = componentPath+path;
				File file1 = new File(dirPath);
				if(!file1.exists()) {
					file1.mkdirs();
				}

				String rootPath = dirPath;
				String filePath = dirPath+File.separatorChar+fileName;
				File file = new File(filePath);
				FileUtils.writeStringToFile(file, xmlContent, "UTF-8");
				
				Thread.sleep(1000);
				
				ExporterFacade.downloadReport(request, response, filePath, exportType);
				
				boolean isdelete = true;
				if(isdelete) {
					if (file.getCanonicalPath().startsWith(new File(rootPath).getCanonicalPath())) {
						file.delete();
					}
				}
			
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "success";
	}
	public static String exportXmlDataBySqlQuery(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		String sql_script = (String) context.get("sql_script");
		String exportFileName = UtilValidate.isNotEmpty(context.get("exportFileName")) ? (String) context.get("exportFileName") : "XML_Data";
		try {
			List<GenericValue> dataList = new ArrayList<GenericValue>();
			String sanitizedQuery = ParamUtil.sanitizeSqlSelectQuery(sql_script);
			if(UtilValidate.isNotEmpty(sanitizedQuery)) {

				SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
				
				String tableName = "";//SqlUtil.getSelectQueryTableName(sql_script);
				tableName = SqlUtil.extractTableName(sanitizedQuery);
				
				String delegatorName = delegator.getDelegatorName();
				ResultSet rs = sqlProcessor.executeQuery(sanitizedQuery);
				
				Converters.JSONToGenericValue jsonToGv = new Converters.JSONToGenericValue();
				
				ResultSetMetaData rsMetaData = rs.getMetaData();
                List<String> columnList = new ArrayList<String>();
                //Retrieving the list of column names
                int count = rsMetaData.getColumnCount();
                for(int i = 1; i<=count; i++) {
                	columnList.add(rsMetaData.getColumnName(i));
                }
                
				if (rs != null && UtilValidate.isNotEmpty(tableName) && UtilValidate.isNotEmpty(delegatorName)) {
					tableName = tableName.toLowerCase();
					String tblName = ModelUtil.dbNameToClassName(tableName);
					exportFileName = tblName;
					ModelReader reader = delegator.getModelReader();
					ModelEntity entity = reader.getModelEntity(tblName);
					List<String> fields = entity.getAllFieldNames();
					fields.removeAll(entity.getAutomaticFieldNames());
					while (rs.next()) {
						Map<String, Object> data = new HashMap<String, Object>();
						for(String columName : columnList) {
							String fieldName = ModelUtil.dbNameToVarName(columName);
							if(fields.contains(fieldName))
								data.put(fieldName, rs.getObject(columName));
						}
						data.put("_DELEGATOR_NAME_", delegatorName);
						data.put("_ENTITY_NAME_", tblName);
						JSON json = JSON.from(data);
						if(jsonToGv.canConvert(JSON.class, GenericValue.class)) {
							GenericValue gv = jsonToGv.convert(json);
							dataList.add(gv);
						}
					}
				}
				
				String fileName = exportFileName + "_" +new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"."+"xml";
				String exportType = "XML";
				
				Document xmlDoc = GenericValue.makeXmlDocument(dataList);
				
				String xmlContent = UtilXml.writeXmlDocument(xmlDoc);
				
				String componentPath = ComponentConfig.getRootLocation("admin-portal");
				String path = "/webapp/admin-portal-resource/file/";
				String dirPath = componentPath+path;
				File file1 = new File(dirPath);
				if(!file1.exists()) {
					file1.mkdirs();
				}

				String rootPath = dirPath;
				String filePath = dirPath+File.separatorChar+fileName;
				File file = new File(filePath);
				if (file.getCanonicalPath().startsWith(new File(rootPath).getCanonicalPath())) {
					FileUtils.writeStringToFile(file, xmlContent, "UTF-8");
				}
				
				Thread.sleep(1000);
				
				ExporterFacade.downloadReport(request, response, filePath, exportType);
				
				boolean isdelete = true;
				if(isdelete) {
					if (file.getCanonicalPath().startsWith(new File(rootPath).getCanonicalPath())) {
						file.delete();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "success";
	}
	public static String getTabConfigIdList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List <Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		String componentId = (String) context.get("componentId");
		List<EntityCondition> tabConfigIdConditions = new ArrayList<EntityCondition>();
		try {
			if (UtilValidate.isNotEmpty(componentId)) {
				tabConfigIdConditions.add(EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, UtilValidate.isNotEmpty(componentId)?componentId.replace("-", "_").toUpperCase():""));
			}
			EntityCondition mainCondition = EntityCondition.makeCondition(tabConfigIdConditions, EntityOperator.AND);
			List < GenericValue > tabConfigIdList = EntityQuery.use(delegator).select("tabConfigId").from("NavTabsConfig").where(mainCondition).distinct().queryList();
			if (UtilValidate.isNotEmpty(tabConfigIdList)) {
				for(GenericValue tabConfigIdGv : tabConfigIdList) {
					results.add(org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, tabConfigIdGv));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	public static String getTabIdList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List <Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		String componentId = (String) context.get("componentId");
		String tabConfigId = (String) context.get("tabConfigId");
		try {
			List<EntityCondition> tabIdConditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(componentId)) {
				tabIdConditions.add(EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, UtilValidate.isNotEmpty(componentId)?componentId.replace("-", "_").toUpperCase():""));
			}
			if (UtilValidate.isNotEmpty(tabConfigId)) {
				tabIdConditions.add(EntityCondition.makeCondition("tabConfigId", EntityOperator.EQUALS, tabConfigId));
			}
			EntityCondition mainCondition = EntityCondition.makeCondition(tabIdConditions, EntityOperator.AND);
			List < GenericValue > tabIdList = EntityQuery.use(delegator).select("tabId","tabName").from("NavTabsConfig").where(mainCondition).queryList();
			if (UtilValidate.isNotEmpty(tabIdList)) {
				for(GenericValue tabIdGv : tabIdList) {
					results.add(org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, tabIdGv));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	public static String createAccessSetup(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
		String successStr = "UpdateAccessSetupSuccessMessage";
		String errorStr = "UpdateAccessSetupErrorMessage";
		String channelId = (String) requestParameters.get("channelAccessId");
		GenericValue checkExisting = delegator.findOne("ChannelAccess",UtilMisc.toMap("channelAccessId", channelId), false);
		if(UtilValidate.isEmpty(checkExisting)) {
			successStr = "CreateAccessSetupSuccessMessage";
			errorStr = "CreateAccessSetupErrorMessage";
		}
		String responseMessage = UtilProperties.getMessage(RESOURCE, successStr, locale);
		String errorMessage = UtilProperties.getMessage(RESOURCE, errorStr , locale);
		try {
			String fromDate = (String) requestParameters.get("fromDate");
			String thruDate = (String) requestParameters.get("thruDate");
			Timestamp fromDateTs = null;
			Timestamp thruDateTs = null;
			if(UtilValidate.isNotEmpty(fromDate)){
				fromDateTs = UtilDateTime.stringToTimeStamp(fromDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
				fromDateTs = org.ofbiz.base.util.UtilDateTime.getDayStart(fromDateTs);
				requestParameters.put("fromDate", fromDateTs);
			} else{
				requestParameters.put("fromDate", nowTimestamp);
			}
			if (UtilValidate.isNotEmpty(thruDate)) {
				thruDateTs = UtilDateTime.stringToTimeStamp(thruDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
				thruDateTs = org.ofbiz.base.util.UtilDateTime.getDayEnd(thruDateTs);
				requestParameters.put("thruDate", thruDateTs);
			}
			//Access Setup creation and updation
			GenericValue accessSetup = delegator.makeValue("ChannelAccess",UtilMisc.toMap("channelAccessId", channelId));
			accessSetup.setPKFields(requestParameters);
			accessSetup.setNonPKFields(requestParameters);
			delegator.createOrStore(accessSetup);
		} catch (Exception e) {
			e.printStackTrace();
			return CommonEvents.returnError(request, errorMessage);
		}
		return CommonEvents.returnSuccess(request, responseMessage);
	}
	public static String getLovSequenceNum(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String lovTypeId = request.getParameter("lovTypeId");
		String screenType = request.getParameter("type");
		int sequenceId = 0;
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			if(UtilValidate.isNotEmpty(lovTypeId)) {
				GenericValue sequenceNum = null;
				String sequenceColumn = "sequenceId";
				if(UtilValidate.isNotEmpty(screenType)) {
					if(screenType.equals("ADD_LOV")) {
						GenericValue lovEntityAssoc = delegator.findOne("LovEntityAssoc", UtilMisc.toMap("lovEntityTypeId", lovTypeId), false);
						if(UtilValidate.isNotEmpty(lovEntityAssoc)) {
							String entityName = lovEntityAssoc.getString("entityName");
							sequenceColumn = lovEntityAssoc.getString("sequenceColumn");
							String filterValue = lovEntityAssoc.getString("filterValue");
							List conditionList = FastList.newInstance();
							if (UtilValidate.isNotEmpty(filterValue)) {
								JSONObject filterObj = JSONObject.fromObject(filterValue);
								Map<String, Object> filter = ParamUtil.jsonToMap(filterObj);
								QueryUtil.makeCondition(conditionList, filter);
							}
							EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							sequenceNum = EntityUtil.getFirst(EntityQuery.use(delegator).select(sequenceColumn).from(entityName).where(mainConditons).orderBy(sequenceColumn+" "+"DESC").queryList());
						}
					}else if(screenType.equals("TEMP_CAT_SETUP")) {
						sequenceNum = EntityUtil.getFirst(EntityQuery.use(delegator).select(sequenceColumn).from("TemplateCategory").where("parentTemplateCategoryId", lovTypeId).orderBy(sequenceColumn+" "+"DESC").queryList());
					}
				}else {
					sequenceNum = EntityUtil.getFirst(EntityQuery.use(delegator).select(sequenceColumn).from("Enumeration").where("enumTypeId", lovTypeId).orderBy(sequenceColumn+" "+"DESC").queryList());
				}
				if(UtilValidate.isNotEmpty(sequenceNum)) {
					sequenceId = Integer.parseInt(sequenceNum.getString("sequenceId"));
					sequenceId = sequenceId+1;
				}else {
					sequenceId = 1000;
				}
			}
			resp.put("sequenceNum",sequenceId);
			resp.put(ResponseConstants.RESPONSE_CODE, ResponseConstants.SUCCESS_CODE);
		} catch (Exception e) {
			Debug.logError("Exception in getLovSequenceNum " + e.getMessage(), MODULE);
			resp.put(ResponseConstants.RESPONSE_CODE, ResponseConstants.INTERNAL_SERVER_ERROR_CODE);
			return doJSONResponse(response, resp);
		}
		return doJSONResponse(response, resp);
	}
	
	public static String subscribeNotification(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		String customFieldIds = request.getParameter("customFieldIds");
		String partyId = request.getParameter("partyId");
		
		try {
			if(UtilValidate.isNotEmpty(partyId)) {
				List < String > customFieldIdList = Stream.of(customFieldIds.split(",")).map(Object::toString).collect(Collectors.toCollection(LinkedList::new));
				
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS,"Y"),
						EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS,""),
						EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS,null)
					);
					
				List<GenericValue> notificationEventTypeList = EntityQuery.use(delegator).from("NotificationEventType").where(condition).queryList();
				
				if(UtilValidate.isNotEmpty(notificationEventTypeList)) {
					for(GenericValue notificationEventType : notificationEventTypeList) {
						String customFieldId = notificationEventType.getString("customFieldId");

						GenericValue customFieldValue = EntityQuery.use(delegator).from("CustomFieldValue").where("customFieldId", customFieldId,"partyId", partyId).queryFirst();
						
						if(customFieldIdList.contains(customFieldId)) {
							if(UtilValidate.isEmpty(customFieldValue)) {
								customFieldValue = delegator.makeValue("CustomFieldValue");
								customFieldValue.set("customFieldId", customFieldId);
								customFieldValue.set("partyId", partyId);
								customFieldValue.set("fromDate", UtilDateTime.nowTimestamp());
								customFieldValue.create();
							}
						} else {
							if(UtilValidate.isNotEmpty(customFieldValue))
								customFieldValue.remove();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Notification subscription has been updated successfully.");
		return doJSONResponse(response, result);
	}
	public static String getComponentMenus(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<>();
		List < Map<String, Object>> results = new ArrayList<>();
		String componentId = request.getParameter("componentId");
		String tabId = request.getParameter("tabId");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		try {
			List<GenericValue> ofbiztables=null;
			if(UtilValidate.isNotEmpty(componentId) && UtilValidate.isNotEmpty(tabId)) {
				String[] tabIdValues = tabId.split(",");
				if (tabIdValues.length >= 2) {
					tabId = tabIdValues[0].trim();
				}
				ofbiztables = EntityQuery.use(delegator).from("OfbizTabSecurityShortcut").where("componentId", componentId,"tabId",tabId).queryList();
			}else {
				ofbiztables = EntityQuery.use(delegator).from("OfbizPageSecurity").where("componentId", componentId).queryList();
			}
			if(UtilValidate.isNotEmpty(ofbiztables)) {
				for(GenericValue menus : ofbiztables) {
					Map < String, Object > data = new HashMap < String, Object > ();
					if(UtilValidate.isNotEmpty(tabId)) {
						String pageAndshortcutId=menus.getString("shortcutId")+","+menus.getString("pageId");
						data.put("shortcutId",pageAndshortcutId);
					}else {
						String tabAndPageId=menus.getString("tabId")+","+menus.getString("pageId");
						data.put("tabId",tabAndPageId);	
					}
					data.put("description",UtilValidate.isNotEmpty(menus.getString("uiLabels"))?menus.getString("uiLabels"):"");
					results.add(data);
				}
				result.put("results", results);
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.put( "_EVENT_MESSAGE_",e.getMessage());
			return doJSONResponse(response, result);
		}
		return doJSONResponse(response, result);
	}

	public static String enableOrDisableComponents(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		HttpSession session = request.getSession();
		String componentId = request.getParameter("componentId");
		String tabId = request.getParameter("tabId");
		String shortcutId = request.getParameter("shortcutId");
		String isDisabled = request.getParameter("isDisabled");

		try {
			if (UtilValidate.isEmpty(componentId) && UtilValidate.isEmpty(isDisabled)) {
				request.setAttribute("_ERROR_MESSAGE_", "Error occur - mandatory field is empty");
				return "error";
			}
			String entityName;
			String fieldName = "isHide";
			Map<String, Object> fields = new HashMap<>();

			if (UtilValidate.isNotEmpty(tabId)) {
				String[] tabIdValues = tabId.split(",");
				entityName = (UtilValidate.isNotEmpty(shortcutId)) ? "OfbizTabSecurityShortcut" : "OfbizPageSecurity";
				fieldName = "isDisabled";
				String pageId = (tabIdValues.length >= 2) ? tabIdValues[1].trim() : null;
				tabId = tabIdValues[0].trim();
				fields.put("tabId", tabId);
				if (UtilValidate.isNotEmpty(shortcutId)) {
					String[] shortcutIdValues = shortcutId.split(",");
					if (shortcutIdValues.length >= 2) {
						pageId = shortcutIdValues[1].trim();
						shortcutId = shortcutIdValues[0].trim();
					}
					fields.put("shortcutId", shortcutId);
				}
				fields.put("pageId", pageId);
			} else {
				entityName = "OfbizComponentAccess";
			}
			fields.put("componentId", componentId);
			List<GenericValue> entities = EntityQuery.use(delegator).from(entityName).where(fields).queryList();
			if(UtilValidate.isNotEmpty(entities)) {
				for (GenericValue entity : entities) {
					entity.set(fieldName, UtilValidate.isNotEmpty(isDisabled) ? isDisabled : "N");
					entity.store();
				}
				UtilCache.clearAllCaches();
				session.setAttribute("webAppMenus", null);
				request.setAttribute("webAppMenus", null);
				request.setAttribute("_EVENT_MESSAGE_", "Successfully updated.");
			}else {
				request.setAttribute("_ERROR_MESSAGE_", "ERROR: Data Not Found");
				return "error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", "ERROR: " + e.getMessage());
			return "error";
		}
		return "success";
	}
	
	public static String findGlobalParamters(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> results = new HashMap<String, Object>();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> dataList = FastList.newInstance();
		String searchParameterKey = (String) context.get("searchParameterKey");
		String searchKeyword = (String) context.get("searchKeyword");
		String searchText = (String) context.get("searchText");
		String storeId = (String) context.get("storeId");
		int autoCompleteLimit = DataUtil.getDefaultAutoCompleteMaxRows(delegator);
		int maxRows= DataUtil.getDefaultMaxRowsCount(delegator);
		
		if (UtilValidate.isEmpty(searchKeyword) && UtilValidate.isNotEmpty(searchParameterKey)) {
			searchKeyword = searchParameterKey;
			maxRows=autoCompleteLimit;
		}
		if (UtilValidate.isEmpty(searchKeyword) && UtilValidate.isNotEmpty(searchText)) {
			searchKeyword = searchText;
			maxRows=autoCompleteLimit;
		}
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		if (UtilValidate.isNotEmpty(searchKeyword)) {
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("parameterId",EntityOperator.LIKE,"%" + searchKeyword + "%"),
					EntityCondition.makeCondition("description", EntityOperator.LIKE,"%" + searchKeyword + "%")
					));
		}
		if(UtilValidate.isNotEmpty(storeId)) {
			conditions.add(EntityCondition.makeCondition("storeId", EntityOperator.EQUALS, storeId));
		}
		EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		Set<String> fieldsToSelect = new TreeSet<String>();
		fieldsToSelect.add("parameterId");
		fieldsToSelect.add("value");
		fieldsToSelect.add("description");
		fieldsToSelect.add("storeId");
		
		try {
			List<GenericValue> parameterList = EntityQuery.use(delegator).select(fieldsToSelect).from("PretailLoyaltyGlobalParameters").where(condition).maxRows(maxRows).orderBy("-lastUpdatedTxStamp").queryList();
			if (UtilValidate.isNotEmpty(parameterList)) {
				for (GenericValue parameter : parameterList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("parameterId",parameter.getString("parameterId"));
					data.put("value", parameter.getString("value"));
					data.put("description", parameter.getString("description"));
					data.put("storeId", parameter.getString("storeId"));
					dataList.add(data);
				}
				results.put("data", dataList);
				results.put("responseMessage", "success");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("data", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, results);
	}
	
	public static String getGlobalParamter(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> results = new HashMap<String, Object>();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> dataList = FastList.newInstance();
		String parameterId = (String) context.get("parameterId");
		
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		if (UtilValidate.isNotEmpty(parameterId)) {
			conditions.add(EntityCondition.makeCondition("parameterId", EntityOperator.EQUALS, parameterId));
			
			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.OR);
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("parameterId");
			fieldsToSelect.add("value");
			fieldsToSelect.add("description");
			fieldsToSelect.add("storeId");
			
			try {
				GenericValue parameterGv = EntityQuery.use(delegator).select(fieldsToSelect).from("PretailLoyaltyGlobalParameters").where(condition).queryFirst();
				if (UtilValidate.isNotEmpty(parameterGv)) {
					
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("parameterId",parameterGv.getString("parameterId"));
					data.put("value", parameterGv.getString("value"));
					data.put("description", parameterGv.getString("description"));
					data.put("storeId", parameterGv.getString("storeId"));
					//dataList.add(data);
					
					results.put("data", data);
					results.put("responseMessage", "success");
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				results.put("errorMessage", e.getMessage());
				results.put("responseMessage", "error");
				results.put("data", new ArrayList<Map<String, Object>>());
			}
		}
		
		return doJSONResponse(response, results);
	}
	
	public static String getComponents(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> results = new HashMap<String, Object>();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<String> dataList = FastList.newInstance();
		try {
	        Collection<ComponentConfig> componentConfigs = ComponentConfig.getAllComponents();
	        for(ComponentConfig c : componentConfigs) {
	        	dataList.add(c.getComponentName());
	        }
			results.put("responseMessage", "success");
			results.put("data", dataList);
		}catch (Exception e) {
			e.printStackTrace();
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("data", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response,results);
	}
}
