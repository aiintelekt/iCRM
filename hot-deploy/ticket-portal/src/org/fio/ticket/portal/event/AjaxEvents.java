package org.fio.ticket.portal.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.fio.admin.portal.util.DataUtil;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.CommonDataHelper;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.SrDataHelper;
import org.fio.homeapps.util.StatusUtil;
import org.fio.ticket.portal.DataHelper;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
import org.groupfio.common.portal.util.SrUtil;
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
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceContainer;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.website.WebSiteWorker;

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
    private AjaxEvents() {}

    private static final String MODULE = AjaxEvents.class.getName();
    private static final String RESOURCE = "SrPortalUiLabels";
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
            Debug.logError(e, "Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(), MODULE);
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

    public static String getServiceHome(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	List < Map<String, Object>> results = new ArrayList<>();
    	String start = request.getParameter("start");
    	String length = request.getParameter("length");
    	Map<String, Object> inMap = FastMap.newInstance();
    	Map outMap = FastMap.newInstance();
    	GenericValue userLogin=getUserLogin(request);
    	try{
    		if(UtilValidate.isNotEmpty(userLogin)) {
				inMap.put("userLogin", userLogin);
			}
    		if(UtilValidate.isNotEmpty(start)) {
				inMap.put("start", start);
			}
    		if(UtilValidate.isNotEmpty(length)) {
				inMap.put("length", length);
			}
			outMap = dispatcher.runSync("ticket.getServiceHomeData", inMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Service Home Metrics data");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<Map<String,Object>>)outMap.get("results")))
				   results =  (List<Map<String,Object>>)outMap.get("results");
			}
    	} catch (Exception e) {
    		Debug.logError(e, "Problem While Fetching Service Home Metrics data : " + e.getMessage(), MODULE);
    		return doJSONResponse(response, e.getMessage());
    	}
    	return doJSONResponse(response, results);
    }
    
    public static String getCustRequestSrSummary(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	List < Map<String, Object>> results = new ArrayList<>();
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Map<String, Object> context = UtilHttp.getCombinedMap(request);
    	GenericValue userLogin=getUserLogin(request);
    	String userLoginPartyId = userLogin.getString("partyId");
    	String externalId = (String) context.get("externalId");
    	String start = request.getParameter("start");
    	String length = request.getParameter("length");
    	String customerName = (String) context.get("customerName");
    	String cinNumber = (String) context.get("cinNumber");
    	String srTypeId = (String) context.get("srTypeId");
    	String srStatusId = (String) context.get("srStatusId");
    	String buPartyId = (String) context.get("buPartyId");
    	String dueDate = (String) context.get("dueDate");
    	String ownerUserLoginId = (String) context.get("ownerUserLoginId");
    	String srCategoryId = (String) context.get("srCategoryId");
    	String srSubStatus = (String) context.get("srSubStatus");
    	String open = (String) context.get("open");
    	String closed = (String) context.get("closed");
    	String slaAtRisk = (String) context.get("slaAtRisk");
    	String slaExpired = (String) context.get("slaExpired");
    	String createdBy = (String) context.get("createdBy");
    	String srSubCategoryId = (String) context.get("srSubCategoryId");
    	String createdOn = (String) context.get("createdOn");
    	String MyTeamServiceRequests = (String) context.get("MyTeamServiceRequestStr");
    	String systemViewFilter = (String) context.get("systemViewFilter");
    	Map<String, Object> inMap = FastMap.newInstance();
    	Map outMap = FastMap.newInstance();
    	try {
    		
    		if(UtilValidate.isNotEmpty(userLogin)) {
				inMap.put("userLogin", userLogin);
			}
    		if(UtilValidate.isNotEmpty(userLoginPartyId)) {
				inMap.put("userLoginPartyId", userLoginPartyId);
			}
			if(UtilValidate.isNotEmpty(systemViewFilter)) {
				inMap.put("systemViewFilter", systemViewFilter);
			}
			if(UtilValidate.isNotEmpty(MyTeamServiceRequests)) {
				inMap.put("MyTeamServiceRequests", MyTeamServiceRequests);			
			}
			if(UtilValidate.isNotEmpty(srTypeId)) {
				inMap.put("srTypeId", srTypeId);
			}
			if(UtilValidate.isNotEmpty(srStatusId)) {
				inMap.put("srStatusId", srStatusId);
			}
			if(UtilValidate.isNotEmpty(externalId)) {
				inMap.put("externalId", externalId);
			}
			if(UtilValidate.isNotEmpty(start)) {
				inMap.put("start", start);
			}
			if(UtilValidate.isNotEmpty(length)) {
				inMap.put("length", length);
			}
			if(UtilValidate.isNotEmpty(customerName)) {
				inMap.put("customerName", customerName);
			}
			if(UtilValidate.isNotEmpty(cinNumber)) {
				inMap.put("cinNumber", cinNumber);
			}	
			if (UtilValidate.isNotEmpty(buPartyId)) {
				inMap.put("buPartyId", buPartyId);
			}
			if (UtilValidate.isNotEmpty(dueDate)) {
				inMap.put("dueDate", dueDate);
			}
			if (UtilValidate.isNotEmpty(srCategoryId)) {
				inMap.put("srCategoryId", srCategoryId);
			}
			if (UtilValidate.isNotEmpty(srSubStatus)) {
				inMap.put("srSubStatus", srSubStatus);
			}
			if (UtilValidate.isNotEmpty(srSubCategoryId)) {
				inMap.put("srSubCategoryId", srSubCategoryId);
			}
			if (UtilValidate.isNotEmpty(open)) {
				inMap.put("open", open);
			}
			if (UtilValidate.isNotEmpty(closed)) {
				inMap.put("closed", closed);
			}
			if (UtilValidate.isNotEmpty(slaAtRisk)) {
				inMap.put("slaAtRisk", slaAtRisk);
			}
			if (UtilValidate.isNotEmpty(ownerUserLoginId)) {
				inMap.put("ownerUserLoginId", ownerUserLoginId);
			}
			if (UtilValidate.isNotEmpty(slaExpired)) {
				inMap.put("slaExpired", slaExpired);
			}
			if (UtilValidate.isNotEmpty(createdBy)) {
				inMap.put("createdBy", createdBy);
			}
			if (UtilValidate.isNotEmpty(createdOn)) {
				inMap.put("createdOn", createdOn);
			}
			outMap = dispatcher.runSync("ticket.getCustRequestSrSummary", inMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Service Request Summary");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<Map<String,Object>>)outMap.get("results")))
				   results =  (List<Map<String,Object>>)outMap.get("results");
			}
    	} catch (Exception e) {
    		Debug.logError(e, "Problem While Fetching Service Request Summary : " + e.getMessage(), MODULE);
    		return doJSONResponse(response, e.getMessage());
    	}
    	return doJSONResponse(response, results);
    }
    public static String getactivityHome(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	List < Map<String, Object>> results = new ArrayList<>();
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Map<String, Object> context = UtilHttp.getCombinedMap(request);
    	GenericValue userLogin=getUserLogin(request);
    	String start = request.getParameter("start");
    	String length = request.getParameter("length");
    	String ownerUserLoginId = (String) context.get("ownerUserLoginId");
    	String systemViewFilter = (String) context.get("systemViewFilter");
    	String userLoginPartyId = userLogin.getString("partyId");
    	String lastMonthsDateStr = (String) context.get("lastMonthsDate");
    	String isRequestFromViewCalendar = (String) context.get("isRequestFromViewCalendar");
    	String workEffortId = (String) context.get("workEffortId");
    	String businessUnitName = (String) context.get("businessUnitName");
    	String createdByUserLogin = (String) context.get("createdByUserLogin");
    	String workEffortServiceType = (String) context.get("workEffortServiceType");
    	String workEffortSubServiceType = (String) context.get("workEffortSubServiceType");
    	String currentStatusId = (String) context.get("currentStatusId");
    	String actualStartDateStr = (String) context.get("actualStartDate");
    	String actualEndDateStr = (String) context.get("actualEndDate");
    	String statusopen = (String) context.get("statusopen");
    	String statuscompleted = (String) context.get("statuscompleted");
    	Map<String, Object> inMap = FastMap.newInstance();
    	Map outMap = FastMap.newInstance();
        
    	try {
    		if(UtilValidate.isNotEmpty(userLogin)) {
				inMap.put("userLogin", userLogin);
			}
    		if(UtilValidate.isNotEmpty(lastMonthsDateStr)) {
        		inMap.put("lastMonthsDateStr", lastMonthsDateStr);
        	}
        	if(UtilValidate.isNotEmpty(actualStartDateStr)) {
        		inMap.put("actualStartDateStr", actualStartDateStr);
        	}
        	if(UtilValidate.isNotEmpty(actualEndDateStr)) {
        		inMap.put("actualEndDateStr", actualEndDateStr);
        	}
    		if(UtilValidate.isNotEmpty(userLoginPartyId)) {
				inMap.put("userLoginPartyId", userLoginPartyId);
			}
			if(UtilValidate.isNotEmpty(systemViewFilter)) {
				inMap.put("systemViewFilter", systemViewFilter);
			}
			if(UtilValidate.isNotEmpty(isRequestFromViewCalendar)) {
				inMap.put("isRequestFromViewCalendar", isRequestFromViewCalendar);			
			}
			if(UtilValidate.isNotEmpty(workEffortServiceType)) {
				inMap.put("workEffortServiceType", workEffortServiceType);
			}
			if(UtilValidate.isNotEmpty(workEffortSubServiceType)) {
				inMap.put("workEffortSubServiceType", workEffortSubServiceType);
			}
			if(UtilValidate.isNotEmpty(currentStatusId)) {
				inMap.put("currentStatusId", currentStatusId);
			}
			if(UtilValidate.isNotEmpty(workEffortId)) {
				inMap.put("workEffortId", workEffortId);
			}
			if(UtilValidate.isNotEmpty(start)) {
				inMap.put("start", start);
			}
			if(UtilValidate.isNotEmpty(length)) {
				inMap.put("length", length);
			}
			if (UtilValidate.isNotEmpty(statusopen)) {
				inMap.put("statusopen", statusopen);
			}
			if (UtilValidate.isNotEmpty(statuscompleted)) {
				inMap.put("statuscompleted", statuscompleted);
			}
			if (UtilValidate.isNotEmpty(ownerUserLoginId)) {
				inMap.put("ownerUserLoginId", ownerUserLoginId);
			}
			if (UtilValidate.isNotEmpty(businessUnitName)) {
				inMap.put("businessUnitName", businessUnitName);
			}
			if (UtilValidate.isNotEmpty(createdByUserLogin)) {
				inMap.put("createdByUserLogin", createdByUserLogin);
			}
			outMap = dispatcher.runSync("ticket.getActivityHomeData", inMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Service Home Metrics data");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<Map<String,Object>>)outMap.get("results")))
				   results =  (List<Map<String,Object>>)outMap.get("results");
			}
    	}
    	catch (Exception e) {
    		Debug.logError(e, "Problem While Fetching Service Home Metrics data : " + e.getMessage(), MODULE);
    		return doJSONResponse(response, e.getMessage());
    	}
    	return doJSONResponse(response, results);
    }

    public static String getActivity(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	List < Map<String, Object>> results = new ArrayList<>();
    	Map<String, Object> context = UtilHttp.getCombinedMap(request);
    	String workEffortId = (String) context.get("workEffortId");
    	String businessUnitName = (String) context.get("businessUnitName");
    	String createdByUserLogin = (String) context.get("createdByUserLogin");
    	String workEffortServiceType = (String) context.get("workEffortServiceType");
    	String workEffortSubServiceType = (String) context.get("workEffortSubServiceType");
    	String currentStatusId = (String) context.get("currentStatusId");
    	String actualStartDateStr = (String) context.get("actualStartDate");
    	String actualEndDateStr = (String) context.get("actualEndDate");
    	String statusopen = (String) context.get("statusopen");
    	String statuscompleted = (String) context.get("statuscompleted");
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    	Timestamp date = null;

    	List<GenericValue> wecsList = null;
    	try {
    		List<EntityCondition> conditions = new ArrayList<>();
    		conditions.add(EntityCondition.makeCondition("workEffortId",EntityOperator.NOT_EQUAL,null));

    		if (UtilValidate.isNotEmpty(workEffortId))
    			conditions.add(EntityCondition.makeCondition("workEffortId",EntityOperator.EQUALS,workEffortId));
    		if (UtilValidate.isNotEmpty(businessUnitName))
    			conditions.add(EntityCondition.makeCondition("businessUnitName",EntityOperator.LIKE,"%"+businessUnitName+"%"));
    		if (UtilValidate.isNotEmpty(createdByUserLogin))
    			conditions.add(EntityCondition.makeCondition("createdByUserLogin",EntityOperator.EQUALS,createdByUserLogin));
    		if (UtilValidate.isNotEmpty(workEffortServiceType))
    			conditions.add(EntityCondition.makeCondition("workEffortServiceType",EntityOperator.EQUALS,workEffortServiceType));
    		if (UtilValidate.isNotEmpty(workEffortSubServiceType))
    			conditions.add(EntityCondition.makeCondition("workEffortSubServiceType",EntityOperator.EQUALS,workEffortSubServiceType));
    		if (UtilValidate.isNotEmpty(currentStatusId))
    			conditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.EQUALS,currentStatusId));

    		if (UtilValidate.isNotEmpty(actualStartDateStr)) {
    			date = new Timestamp(sdf.parse(actualStartDateStr).getTime());
    			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("actualStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(date))));
    		}
    		if (UtilValidate.isNotEmpty(actualEndDateStr)) {
    			date = new Timestamp(sdf.parse(actualEndDateStr).getTime());
    			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("actualEndDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(date))));
    		}
    		List<EntityCondition> checkBoxconditions = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(statusopen)) {
    			checkBoxconditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.NOT_IN,UtilMisc.toList("IA_MCOMPLETED","IA_CANCELLED")));
    		}
    		if(UtilValidate.isNotEmpty(statuscompleted)) {
    			checkBoxconditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.EQUALS,"IA_MCOMPLETED"));
    		}
    		EntityCondition condition;
    		if(UtilValidate.isNotEmpty(checkBoxconditions) && (UtilValidate.isNotEmpty(conditions))) {
    			condition=EntityCondition.makeCondition(EntityCondition.makeCondition(conditions,EntityOperator.AND),EntityOperator.AND, EntityCondition.makeCondition(checkBoxconditions,EntityOperator.OR));
    		}else {
    			condition=EntityCondition.makeCondition(conditions,EntityOperator.AND);
    		}
    		List <GenericValue> enumListData;
    		Map<String, Object> enumIdMapDesc = new HashMap<>();
    		Map<String, Object> statusIdMapDesc = new HashMap<>();
    		List<EntityCondition> conditionlist1 = FastList.newInstance();
    		conditionlist1.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "IA_TYPE"));

    		EntityCondition condition1 = EntityCondition.makeCondition(conditionlist1, EntityOperator.AND);
    		enumListData = delegator.findList("Enumeration", condition1,null, null, null, false);
    		for (GenericValue Enum: enumListData) {	 
    			String enumId=Enum.getString("enumId");
    			String description=Enum.getString("description");
    			enumIdMapDesc.put(enumId,description);

    		}
    		List<EntityCondition> conditionlist2 = FastList.newInstance();
    		conditionlist2.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "IA_STATUS_ID"));
    		EntityCondition statusDescCondition = EntityCondition.makeCondition(conditionlist2, EntityOperator.AND);
    		enumListData = EntityQuery.use(delegator).from("StatusItem").where(statusDescCondition)
    				.queryList();
    		for (GenericValue statusItem: enumListData) {	 
    			String statusId=statusItem.getString("statusId");
    			String description=statusItem.getString("description");
    			statusIdMapDesc.put(statusId,description);
    		}
    		EntityFindOptions efo = new EntityFindOptions();
    		efo.setDistinct(true);
    		efo.setOffset(0);
    		efo.setLimit(1000);
    		efo.setMaxRows(1000);

    		wecsList = delegator.findList("WorkEffortCallSummary", condition, null,UtilMisc.toList("-createdStamp"), efo, false);

    		List<String> workEffortIdlst = new ArrayList<>();
    		for(GenericValue wrkEffort: wecsList) {
    			workEffortIdlst.add(wrkEffort.getString("workEffortId"));
    		}

    		List<EntityCondition> conditionlist3 = FastList.newInstance();
    		conditionlist3.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIdlst));
    		EntityCondition workEffortCondition = EntityCondition.makeCondition(conditionlist3, EntityOperator.AND);
			enumListData = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(workEffortCondition)
				.queryList();
    		List<String> workEffortPartyAssgnlst = new ArrayList<>();
    		Map<String,String> workEffortPartyAssgnmap = new HashMap<>();
    		Map<String,String> workEffortPartyAssgnRoleTypemap = new HashMap<>();
    		for(GenericValue wrkEffortParty: enumListData) {
    			workEffortPartyAssgnlst.add(wrkEffortParty.getString("partyId"));
    			workEffortPartyAssgnmap.put(wrkEffortParty.getString("workEffortId"), wrkEffortParty.getString("partyId"));
    			workEffortPartyAssgnRoleTypemap.put(wrkEffortParty.getString("workEffortId"),wrkEffortParty.getString("roleTypeId"));
    		}

    		List<EntityCondition> conditionlist4 = FastList.newInstance();
    		conditionlist4.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, workEffortPartyAssgnlst));
    		EntityCondition partyAssignmentCondition = EntityCondition.makeCondition(conditionlist4, EntityOperator.AND);
    		enumListData = EntityQuery.use(delegator).from("PartyIdentification").where(partyAssignmentCondition)
    				.queryList();
    		Map<String,String> customerpartyRelationMap = new HashMap<>();
    		for(GenericValue customerDtls: enumListData) {
    			customerpartyRelationMap.put(customerDtls.getString("partyId"), customerDtls.getString("idValue"));
    		}
    		Map<String,String> personDetailsmap = new HashMap<>();
    		
    		if(UtilValidate.isNotEmpty(workEffortPartyAssgnlst) && !workEffortPartyAssgnlst.isEmpty()) {
    			String personName;
    			List<EntityCondition> conditionlist5 = FastList.newInstance();
    			conditionlist5.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, workEffortPartyAssgnlst));
    			EntityCondition personCondition = EntityCondition.makeCondition(conditionlist5, EntityOperator.AND);
    			enumListData = EntityQuery.use(delegator).from("Person").where(personCondition)
    					.queryList();
    			for(GenericValue personDtls: enumListData) {
    				personName = "";
    				if(UtilValidate.isNotEmpty(personDtls.getString("firstName"))){
    					personName = personName + personDtls.getString("firstName") + " ";
    				}
    				if(UtilValidate.isNotEmpty(personDtls.getString("middleName"))){
    					personName = personName + personDtls.getString("middleName") + " ";
    				}
    				if(UtilValidate.isNotEmpty(personDtls.getString("lastName"))){
    					personName = personName + personDtls.getString("lastName");
    				}
    				personDetailsmap.put(personDtls.getString("partyId"), personName);
    			}
    		}

    		for (GenericValue groupGv: wecsList) {
    			Map<String, Object> data = new HashMap<>();
    			workEffortId = groupGv.getString("workEffortId");
    			data.put("workEffortId", workEffortId);
    			data.put("workEffortId", groupGv.getString("workEffortId"));
    			data.put("currentStatusId", UtilValidate.isNotEmpty(groupGv.getString("currentStatusId"))?
    					groupGv.getString("currentStatusId"): "");
    			data.put("statusIdDesc", UtilValidate.isNotEmpty(statusIdMapDesc.get(groupGv.getString("currentStatusId")))?
    					statusIdMapDesc.get(groupGv.getString("currentStatusId").toUpperCase()) : "");
    			data.put("workEffortParentId", groupGv.getString("workEffortParentId"));
    			workEffortServiceType = groupGv.getString("workEffortServiceTypeValue");
    			workEffortSubServiceType = groupGv.getString("workEffortSubServiceTypeValue");
    			data.put("workEffortServiceTypeDescription", workEffortServiceType);
    			data.put("workEffortSubServiceTypeDescription", workEffortSubServiceType);
    			data.put("Subject", groupGv.getString("workEffortName"));
    			data.put("accountNumber", groupGv.getString("accountNumber"));
    			data.put("businessUnitName", groupGv.getString("businessUnitName"));
    			data.put("estimatedStartDate", groupGv.getString("estimatedStartDate"));
    			data.put("estimatedCompletionDate", groupGv.getString("estimatedCompletionDate"));
    			data.put("actualCompletionDate", groupGv.getString("actualCompletionDate"));
    			data.put("primOwnerId", groupGv.getString("primOwnerId"));
    			data.put("businessUnitName", groupGv.getString("businessUnitName"));
    			data.put("businessUnitId", groupGv.getString("businessUnitId"));
    			data.put("emplTeamId", groupGv.getString("emplTeamId"));
    			data.put("wfOnceDone", groupGv.getString("wfOnceDone"));
    			data.put("createdByUserLogin", groupGv.getString("createdByUserLogin"));
    			data.put("lastModifiedByUserLogin", groupGv.getString("lastModifiedByUserLogin"));
    			data.put("createdDate", groupGv.getString("createdDate"));
    			data.put("lastModifiedDate", groupGv.getString("lastModifiedDate"));
    			data.put("resolution", groupGv.getString("resolution"));
    			data.put("actualStartDate", groupGv.getString("actualStartDate"));
    			data.put("overDue", groupGv.getString("overDue"));
    			data.put("CIFID", UtilValidate.isNotEmpty(groupGv.getString("workEffortId"))? customerpartyRelationMap.get(workEffortPartyAssgnmap.get(groupGv.getString("workEffortId"))): "");
    			data.put("CustomerName", UtilValidate.isNotEmpty(groupGv.getString("workEffortId"))? personDetailsmap.get(workEffortPartyAssgnmap.get(groupGv.getString("workEffortId"))): "");
    			data.put("CustomerType", UtilValidate.isNotEmpty(groupGv.getString("workEffortId"))? workEffortPartyAssgnRoleTypemap.get(groupGv.getString("workEffortId")): "");
    			data.put("Campaigncode", groupGv.getString("campaignCode"));
    			data.put("Productname", groupGv.getString("productName"));
    			data.put("Comments", groupGv.getString("description"));
    			data.put("actualStart", groupGv.getString("actualStartDate"));
    			results.add(data);
    		}
    	} catch (Exception e) {
    		Debug.logError(e, "Exception :  "+e.getMessage(), MODULE);
    		Map<String, Object> data = new HashMap<>();
    		data.put("errorMessage", e.getMessage());
    		data.put("errorResult", new ArrayList<Map<String, Object>>());
    		results.add(data);
    	}
    	return doJSONResponse(response, results);
    }

   /* public static String updateSRReopen(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	List<String> results = new ArrayList<String>();
    	Map<String, Object> context = UtilHttp.getCombinedMap(request);
    	String externalId = (String) context.get("externalId");
    	String statusId = (String) context.get("statusId");
    	String subStatusId = (String) context.get("subStatusId");
    	String srStatus = (String) context.get("srStatus");

    	try {
    		GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("CustRequest", UtilMisc.toMap("externalId", externalId), null, false));
    		if (UtilValidate.isNotEmpty(updateConfigRecords)) {
    			if (UtilValidate.isNotEmpty(statusId)) {
    				updateConfigRecords.put("statusId",statusId);
    			}
    			if (UtilValidate.isNotEmpty(subStatusId)) {
    				updateConfigRecords.put("subStatusId",subStatusId);
    			}
    			if (UtilValidate.isNotEmpty(srStatus)) {
    				updateConfigRecords.put("statusId",srStatus);
    			}
    			updateConfigRecords.store();
    			request.setAttribute("_EVENT_MESSAGE_", "Data updated succesfully with user");
    			String data = null;;
    			data= "Data updated succesfully with user";
    			results.add(data);
    		}
    	} catch (Exception e) {
    		String errMsg = "" + e.toString();
    		Debug.logError(e, errMsg, MODULE);
    		String data = null;;
    		data= errMsg;
    		results.add(data);
    		return AjaxEvents.doJSONResponse(response,results);

    	}
    	return AjaxEvents.doJSONResponse(response,results);
    }
    
    public static String updateSRResolveStatus(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
    	List<String> results = new ArrayList<String>();
    	Map<String, Object> context = UtilHttp.getCombinedMap(request);
    	String externalId = (String) context.get("externalId");
    	String srSubStatus = (String) context.get("srSubStatus");
    	String statusId = (String) context.get("statusId");
    	String subStatusId = (String) context.get("subStatusId");

    	try {
    		GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("CustRequest",
    				UtilMisc.toMap("externalId", externalId), null, false));
    		if (UtilValidate.isNotEmpty(updateConfigRecords)) {
    			if (UtilValidate.isNotEmpty(statusId)) {
    				updateConfigRecords.put("statusId", statusId);
    			}
    			if (UtilValidate.isNotEmpty(subStatusId)) {
    				updateConfigRecords.put("subStatusId", subStatusId);
    			}
    			if (UtilValidate.isNotEmpty(srSubStatus)) {
    				updateConfigRecords.put("subStatusId", srSubStatus);
    			}
    			if (UtilValidate.isNotEmpty(userLogin.getString("partyId"))) {
    				updateConfigRecords.put("closedByUserLogin", userLogin.getString("partyId"));
    			}
    			Timestamp closedByDate = UtilDateTime.nowTimestamp();
    			updateConfigRecords.put("closedByDate",closedByDate);
    			updateConfigRecords.store();
    			request.setAttribute("_EVENT_MESSAGE_", "Data updated succesfully with user");
    			String data = null;;
    			data= "Data updated succesfully with user";
    			results.add(data);
    		}
    		if (UtilValidate.isNotEmpty(updateConfigRecords)) {
    			Map<String, Object> custRequestContext = new HashMap<String, Object>();
    			Map<String, Object> supplementoryContext = new HashMap<String, Object>();

    			if(UtilValidate.isNotEmpty(externalId)){
    				custRequestContext.put("externalId", externalId);
    				custRequestContext.put("statusId", "SR_CLOSED");
    				custRequestContext.put("isAttachment", "N");
    			}
    			Map<String, Object> inputMap = new HashMap<String, Object>();
    			inputMap.put("custRequestContext", custRequestContext);
    			inputMap.put("supplementoryContext", supplementoryContext);
    			inputMap.put("userLogin", userLogin);

    			Map<String, Object> outMap = dispatcher.runSync("crmPortal.updateServiceRequest", inputMap);

    			if(!ServiceUtil.isSuccess(outMap)) {
    				request.setAttribute("_ERROR_MESSAGE_", "Problem While Updating Service Request");
    				return "error";
    			}
    		}
    	} catch (Exception e) {
    		String errMsg = "" + e.toString();
    		Debug.logError(e, errMsg, MODULE);
    		String data = null;;
    		data= errMsg;
    		results.add(data);
    		return AjaxEvents.doJSONResponse(response,results);

    	}
    	return AjaxEvents.doJSONResponse(response,results);
    }
			
    public static String UpdateReasignSR(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	List<String> results = new ArrayList<String>();
    	Map<String, Object> context = UtilHttp.getCombinedMap(request);
    	String ownerUserLoginId = (String) context.get("ownerUserLoginId");
    	String externalId = (String) context.get("externalId");
    	String emplTeamId = (String) context.get("emplTeamId");

    	try {
    		if (UtilValidate.isNotEmpty(ownerUserLoginId)) {
    			GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("CustRequest",
    					UtilMisc.toMap("externalId", externalId), null, false));
    			if (UtilValidate.isNotEmpty(updateConfigRecords)) {
    				GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", ownerUserLoginId).queryList());
    				if (UtilValidate.isNotEmpty(emplTeam)) {    
    					String primOwnerTeamId = emplTeam.getString("emplTeamId");
    					if (UtilValidate.isNotEmpty(primOwnerTeamId)) {   
    						updateConfigRecords.put("emplTeamId", primOwnerTeamId);
    					}
    					String ownerBusinessUnitId = emplTeam.getString("businessUnit");
    					if (UtilValidate.isNotEmpty(ownerBusinessUnitId)) {   
    						updateConfigRecords.put("ownerBu", ownerBusinessUnitId);
    					}
    				}
    				updateConfigRecords.put("responsiblePerson", ownerUserLoginId);
    				updateConfigRecords.store();
    				request.setAttribute("_EVENT_MESSAGE_", "Data updated succesfully with user");
    				String data = null;;
    				data= "Data updated succesfully with user";
    				results.add(data);
    			}
    		}

    		if (UtilValidate.isNotEmpty(emplTeamId)) {
    			GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("CustRequest",
    					UtilMisc.toMap("externalId", externalId), null, false));
    			if (UtilValidate.isNotEmpty(updateConfigRecords)) {
    				GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("emplTeamId", emplTeamId).queryList());
    				if (UtilValidate.isNotEmpty(emplTeam)) {
    					String ownerBusinessUnitId = emplTeam.getString("businessUnit");
    					if (UtilValidate.isNotEmpty(ownerBusinessUnitId)) {   
    						updateConfigRecords.put("ownerBu", ownerBusinessUnitId);
    					}
    				}
    				updateConfigRecords.put("responsiblePerson", "");
    				updateConfigRecords.put("emplTeamId", emplTeamId);
    				updateConfigRecords.store();
    				String data = null;;
    				data= "Data updated succesfully with team";
    				results.add(data);
    			}
    		}
    	} catch (Exception e) {
    		String errMsg = "" + e.toString();
    		Debug.logError(e, errMsg, MODULE);
    		String data = null;;
    		data= errMsg;
    		results.add(data);
    		return AjaxEvents.doJSONResponse(response,results);

    	}
    	return AjaxEvents.doJSONResponse(response,results);
    }

*/
    public static String UpdateReasignActivity(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	List<String> results = new ArrayList<>();
    	Map<String, Object> context = UtilHttp.getCombinedMap(request);
    	String primOwnerId = (String) context.get("primOwnerId");
    	String workEffortId = (String) context.get("workEffortId");
    	String emplTeamId = (String) context.get("emplTeamId");
    	Map<String, Object> inMap = FastMap.newInstance();
    	Map outMap = FastMap.newInstance();
    	GenericValue userLogin=getUserLogin(request);

    	try {
    		
    		if(UtilValidate.isNotEmpty(userLogin)) {
				inMap.put("userLogin", userLogin);
			}
    		if(UtilValidate.isNotEmpty(primOwnerId)) {
				inMap.put("primOwnerId", primOwnerId);
			}
    		if(UtilValidate.isNotEmpty(workEffortId)) {
				inMap.put("workEffortId", workEffortId);
			}
    		if(UtilValidate.isNotEmpty(emplTeamId)) {
				inMap.put("emplTeamId", emplTeamId);
			}
			outMap = dispatcher.runSync("ticket.UpdateReasignActivity", inMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While reassigning Service Activity");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<String>)outMap.get("results")))
				   results =  (List<String>)outMap.get("results");
			}
    	}catch (Exception e) {
			Debug.logError(e, "Problem While reassigning Service Activity: " + e.getMessage(), MODULE);
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
    	return AjaxEvents.doJSONResponse(response,results);
    }

    public static String getSrCategory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	List < Map < String, Object >> results = new ArrayList <> ();
    	Map < String, Object > context = UtilHttp.getCombinedMap(request);
    	String srTypeId = (String) context.get("srTypeId");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	GenericValue userLogin=getUserLogin(request);
    	Map<String, Object> inMap = FastMap.newInstance();
    	try {
    		if(UtilValidate.isNotEmpty(srTypeId)){
				inMap.put("srTypeId", srTypeId);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.getSrCategory", inMap);

			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching sr category");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<Map<String,Object>>)outMap.get("results")))
					   results =  (List<Map<String,Object>>)outMap.get("results");
			}
    	} catch (Exception e) {
    		Debug.logError(e, "Problem While Fetching sr category : " + e.getMessage(), MODULE);
    		return AjaxEvents.doJSONResponse(response, e.getMessage());
    	}
    	return AjaxEvents.doJSONResponse(response, results);
    }
    
    public static String downloadNotesAttachments(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	String result = "success";
    	String url = "";
    	Map<String, Object> data = new HashMap<>();
    	Map<String, Object> context = UtilHttp.getCombinedMap(request);
    	String documentId = (String) context.get("documentId");
    	String documentName = (String) context.get("documentName");
    	Debug.log("documentId : "+documentId+ ", documentName : "+documentName);
    	try {
    		GenericValue userLogin=getUserLogin(request);
    		String loggedInUser = userLogin.getString("userLoginId");

    		if(documentId != null && !documentId.isEmpty()) {
    			url = populateImageOnViewer(documentId, "ACS", true, true, false, true, false, delegator, loggedInUser);
    			Debug.log("Final URL received: Timestamp: " + new Date());
    		}

    		data.put("result", result);	
    		data.put("fileNetUrl", url);
    	} catch (Exception e) {
    		Debug.logError(e, "Exception :  "+e.getMessage(), MODULE);
    		result = "error";
    		data.put("result", result);
    		data.put("errorMessage", e.getMessage());
    		Debug.logError(e, "Failed to get response writer", MODULE);
    		return AjaxEvents.doJSONResponse(response, data);
    	}

    	return AjaxEvents.doJSONResponse(response, data);
    }
    
    private static String populateImageOnViewer(String docId, String objectStore, boolean isAnnotableWritable, boolean isAnnotableDeletable, boolean isPrintable,boolean isDownloadable,boolean isLoadFromCache, Delegator delegator, String... userName){ 

    	String uvSGURL = EntityUtilProperties.getPropertyValue("general", "uv.sg.URL", delegator);
    	String uvSGAppURL = EntityUtilProperties.getPropertyValue("general", "uv.sg.AppURL", delegator);
    	String uvSGViewerURL = EntityUtilProperties.getPropertyValue("general", "uv.sg.ViewerURL", delegator);	    
    	String uvfileRepository = EntityUtilProperties.getPropertyValue("general", "uv.fileRepository", delegator);;
    	String uvobjectStore= EntityUtilProperties.getPropertyValue("general", "uv.objectStore", delegator);

    	if (UtilValidate.isNotEmpty(uvSGURL) && UtilValidate.isNotEmpty(uvSGViewerURL)) {
    		uvSGAppURL =  (uvSGAppURL == null) || uvSGAppURL.length() == 0 ? "/uv/rest/init/initDocument" : uvSGAppURL;
    		uvfileRepository =  (uvfileRepository == null) || uvfileRepository.length() == 0 ? "FileNet" : uvfileRepository;
    		uvobjectStore =  (uvobjectStore == null) || uvobjectStore.length() == 0 ? "FNOS03" : uvobjectStore;
    		objectStore = uvobjectStore;

    		String payLoad = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><request xmlns=\"http://www.dbs.com/documentrequest\"><repository>"+ uvfileRepository +"</repository><fileNetToken>FILENET_TOKEN</fileNetToken><documentId>" + docId + "</documentId><fileNetObjectStoreName>" + objectStore + "</fileNetObjectStoreName><isAnnotationsWritable>" + isAnnotableWritable + "</isAnnotationsWritable><isAnnotationsDeletable>" + isAnnotableDeletable + "</isAnnotationsDeletable><isPrintable>" + isPrintable + "</isPrintable><downloadable>" + isDownloadable + "</downloadable><isLazyLookupEnabled>" + isLoadFromCache + "</isLazyLookupEnabled><additionalStamps><stamp>dateTime</stamp><stamp>approved</stamp><stamp>draft</stamp><stamp>filed</stamp><stamp>received</stamp><stamp>rejected</stamp><stamp>reviewed</stamp><stamp>urgent</stamp></additionalStamps><isSignatureCropEnabled>true</isSignatureCropEnabled><directDownloadMimeTypes><mimeType>application/vnd.ms-excel</mimeType><mimeType>application/vnd.openxmlformats-officedocument.spreadsheetml.sheet</mimeType><mimeType>application/msword</mimeType><mimeType>application/vnd.openxmlformats-officedocument.wordprocessingml.document</mimeType><mimeType>application/vnd.ms-powerpoint</mimeType><mimeType>application/vnd.openxmlformats-officedocument.presentationml.presentation</mimeType></directDownloadMimeTypes>";
    		if ((null != userName) && (userName.length > 0)) {
    			payLoad = payLoad.concat("<userName>" + userName[0] + "</userName>");
    		}
    		payLoad = payLoad.concat("</request>");

    		Debug.log("Payload is : " + payLoad);

    		CloseableHttpClient httpClient = null;

    		String webTicket = ""; 
    		String line = null;
    		// HttpClient httpClient = null;
    		HttpPost postRequest = new HttpPost(uvSGURL + uvSGAppURL);
    		postRequest.setEntity(new StringEntity(payLoad, ContentType.create("application/xml")));
    		try
    		{       
    			httpClient = HttpClients.createDefault();		     
    			HttpResponse response = httpClient.execute(postRequest);		     
    			HttpEntity entity = response.getEntity();
    			BufferedReader webticketReader = new BufferedReader(new InputStreamReader(entity.getContent()));
    			while ((line = webticketReader.readLine()) != null) {
    				webTicket = line;
    			}
    			webticketReader.close();
    			Debug.log("Line is : " + webTicket);
    			if (webTicket != null)
    			{
    				String finalURL = uvSGViewerURL + "/uv/DocViewer.html?documentRequestId=" + webTicket;
    				Debug.log("finalURL::" + finalURL);
    				return finalURL;
    			}
    		}  catch (IOException e) {
    			Debug.logError(e, "Exception occured while getting image. " + e.getMessage(), MODULE);
    		}  catch (Exception e) {
    			Debug.logError(e, "Exception :  " + e.getMessage(), MODULE);
    		} finally {
    			if(null != httpClient){
    				try {
    					httpClient.close();
    				} catch (IOException e) {
    					Debug.logError(e, "Exception :  " + e.getMessage(), MODULE);
    				}
    			}
    		}
    	}    
    	return "";	  
    }
	
	public static String getSrSubCategory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		List < Map < String, Object >> results = new ArrayList <> ();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map < String, Object > context = UtilHttp.getCombinedMap(request);
		String srTypeId = (String) context.get("srTypeId");
		String srCategoryId = (String) context.get("srCategoryId");
		GenericValue userLogin=getUserLogin(request);
		Map<String, Object> inMap = FastMap.newInstance();
		try {
			if(UtilValidate.isNotEmpty(srTypeId)){
				inMap.put("srTypeId", srTypeId);
			}
			if(UtilValidate.isNotEmpty(srCategoryId)){
				inMap.put("srCategoryId", srCategoryId);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.getSrSubCategory", inMap);

			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching sr sub category");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<Map<String,Object>>)outMap.get("results")))
					   results =  (List<Map<String,Object>>)outMap.get("results");
			}
		} catch (Exception e) {
			Debug.logError(e, "Problem While Fetching sr sub category: " + e.getMessage(), MODULE);
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, results);
	}

	public static String getActivityCounts(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String userLoginPartyId = userLogin.getString("partyId");
		Map<String, Object> data = new HashMap<>();

		if(UtilValidate.isEmpty(userLogin)){
			data.put("error", "No user login details found!");
			return doJSONResponse(response, data);
		}
		try{
			List<EntityCondition> conditions = new ArrayList<>();
			conditions.add(EntityCondition.makeCondition("primOwnerId",EntityOperator.EQUALS, userLogin.getString("partyId")));
			conditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.NOT_IN,UtilMisc.toList("IA_CANCELLED","IA_CLOSED")));
			long myActivities = delegator.findCountByCondition("WorkEffortCallSummary", EntityCondition.makeCondition(conditions, EntityOperator.AND), null,null);
			data.put("myActivities", String.valueOf(myActivities));
			conditions.clear();
			GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", userLoginPartyId).cache(true).queryList());
			if (UtilValidate.isNotEmpty(userLoginPartyId)&&UtilValidate.isNotEmpty(emplTeam)) {    
				String emplTeamId = emplTeam.getString("emplTeamId");
				if(UtilValidate.isNotEmpty(emplTeamId)) {
					conditions.add(EntityCondition.makeCondition("emplTeamId",EntityOperator.EQUALS,emplTeamId));
				}
			}
			conditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.NOT_IN,UtilMisc.toList("IA_CANCELLED","IA_CLOSED")));
			long myTeamActivities = delegator.findCountByCondition("WorkEffortCallSummary", EntityCondition.makeCondition(conditions, EntityOperator.AND), null,null);
			data.put("myTeamActivities", String.valueOf(myTeamActivities));

			conditions.clear();
			conditions.add(EntityCondition.makeCondition("primOwnerId",EntityOperator.EQUALS, userLogin.getString("partyId")));
			conditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.IN, UtilMisc.toList("IA_COMPLETED","IA_MCOMPLETED")));
			long completedActivities = delegator.findCountByCondition("WorkEffortCallSummary", EntityCondition.makeCondition(conditions, EntityOperator.AND), null,null);
			data.put("completedActivities", String.valueOf(completedActivities));

			conditions.clear();
			conditions.add(EntityCondition.makeCondition("estimatedCompletionDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
			long overDueActivities = delegator.findCountByCondition("WorkEffortCallSummary", EntityCondition.makeCondition(conditions, EntityOperator.AND), null,null);
			data.put("overDueActivities", String.valueOf(overDueActivities));
		}
		catch (Exception e) {
			Debug.logError(e, "Exception :  " + e.getMessage(), MODULE);
			data.put("errorMessage", e.getMessage());
			data.put("errorResult", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, data);
	}
	public static String getSrOverDueSummary(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		List < Map<String, Object>> results = new ArrayList<>();
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		String businessUnit = request.getParameter("businessUnit");
		Map<String,Object> inMap = FastMap.newInstance();
		GenericValue userLogin=getUserLogin(request);
		try{
			if(UtilValidate.isNotEmpty(start)){
				inMap.put("start", start);
			}
			if(UtilValidate.isNotEmpty(length)){
				inMap.put("length", length);
			}
			if(UtilValidate.isNotEmpty(businessUnit)){
				inMap.put("businessUnit", businessUnit);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.getSrOverDueSummary", inMap);

			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching OverDue Service Request");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<Map<String,Object>>)outMap.get("results")))
					   results =  (List<Map<String,Object>>)outMap.get("results");
			}
		} catch (Exception e) {
			Debug.logError(e, "Problem While Fetching OverDue Service Request : " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	
	public static String loadTemplate(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	List < Map < String, Object >> results = new ArrayList <> ();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String templateId = (String) context.get("templateId");
		request.setAttribute("templateId", templateId);
		Map<String, Object> inMap = FastMap.newInstance();
    	Map outMap = FastMap.newInstance();
    	GenericValue userLogin=getUserLogin(request);
    	try{
    		if(UtilValidate.isNotEmpty(userLogin)) {
				inMap.put("userLogin", userLogin);
			}
    		if(UtilValidate.isNotEmpty(templateId)) {
				inMap.put("templateId", templateId);
			}
			outMap = dispatcher.runSync("ticket.loadTemplate", inMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Service Template");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<Map<String,Object>>)outMap.get("results")))
					   results =  (List<Map<String,Object>>)outMap.get("results");
			}
    	}
		catch (Exception e) {
			Debug.logError(e, "Problem While Fetching Service Template : " + e.getMessage(), MODULE);
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
    
	public static String getContactDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		List < Map < String, Object >> results = new ArrayList <> ();
		Map < String, Object > context = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String,Object> inMap = FastMap.newInstance();
		String srNumber = (String) context.get("srNumber");
		GenericValue userLogin=getUserLogin(request);
		try {
				if(UtilValidate.isNotEmpty(srNumber)){
					inMap.put("externalId", srNumber);
				}
				if(UtilValidate.isNotEmpty(userLogin)){
					inMap.put("userLogin", userLogin);
				}
				Map<String, Object> outMap = dispatcher.runSync("ticket.getContactDetails", inMap);

				if(!ServiceUtil.isSuccess(outMap)) {
					request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Service Request Contact Details");
					return "error";
				}else{
					if(UtilValidate.isNotEmpty((List<Map<String,Object>>)outMap.get("results")))
						   results =  (List<Map<String,Object>>)outMap.get("results");
				}
		} catch (Exception e) {
			Debug.logError(e, "Problem While Fetching Service Request Contact Details : " + e.getMessage(), MODULE);
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	public static String pwebRelatedDetailsResult(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {    		
		Map<String,Map<String, Object>> results = new HashMap<>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String,Object> inMap = FastMap.newInstance();
		String srNumber = request.getParameter("srNumber");

		if (UtilValidate.isEmpty(userLogin)) {
			Map<String, Object> data = new HashMap<>();
			data.put("error", "No user login details found!");
			return doJSONResponse(response, data);
		}

		try {
			if(UtilValidate.isNotEmpty(srNumber)){
				inMap.put("externalId", srNumber);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.pwebRelatedDetailsResult", inMap);

			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Service Request pweb Details");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((Map<String,Map<String, Object>>)outMap.get("results")))
					results =  (Map<String,Map<String, Object>>)outMap.get("results");
			}
		} catch (Exception e) {
				Debug.logError(e, "Problem While Fetching Service Request pweb Details : " + e.getMessage(), MODULE);
				return doJSONResponse(response, e.getMessage());
			}

		return doJSONResponse(response, results);
	} 
    
	public static String getNotesAttachments(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		List<Map<String, Object>> results = new ArrayList<>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String,Object> inMap = FastMap.newInstance();
		String srNumber = request.getParameter("srNumber");
		if (UtilValidate.isEmpty(userLogin)) {
			Map<String, Object> data = new HashMap<>();
			data.put("error", "No user login details found!");
			return doJSONResponse(response, data);
		}
		try {

			if(UtilValidate.isNotEmpty(srNumber)){
				inMap.put("externalId", srNumber);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.getNotesAttachments", inMap);

			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Service Request Notes Details");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<Map<String, Object>>)outMap.get("results")))
					results =  (List<Map<String, Object>>)outMap.get("results");
			}
		} catch (Exception e) {
			Debug.logError(e, "Problem While Fetching Service Request Notes Details : " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
    
	public static String getActivityData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		List<Map<String, Object>> results = new ArrayList<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String,Object> inMap = FastMap.newInstance();
		String srNumber = request.getParameter("srNumber");
		String salesOpportunityId =(String) context.get("salesOpportunityId");
		GenericValue userLogin=getUserLogin(request);
		
		if (UtilValidate.isEmpty(userLogin)) {
			Map<String, Object> data = new HashMap<>();
			data.put("error", "No user login details found!");
			return doJSONResponse(response, data);
		}
		try {

			if(UtilValidate.isNotEmpty(salesOpportunityId)){
				inMap.put("salesOpportunityId", salesOpportunityId);
			}
			if(UtilValidate.isNotEmpty(srNumber)){
				inMap.put("externalId", srNumber);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.getActivityData", inMap);

			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Service Request Activity Details");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<Map<String, Object>>)outMap.get("results")))
					results =  (List<Map<String, Object>>)outMap.get("results");
			}
		} catch (Exception e) {
			Debug.logError(e, "Problem While Fetching Service Request Activity Details : " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getSrActivityData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String,Object> inMap = FastMap.newInstance();
		String custRequestId = (String) context.get("custRequestId");

		try {
			if (UtilValidate.isEmpty(userLogin)) {
				Map<String, Object> data = new HashMap<>();
				data.put("error", "No user login details found!");
				return doJSONResponse(response, data);
			}
			if(UtilValidate.isNotEmpty(custRequestId)){
				inMap.put("custRequestId", custRequestId);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.getSrActivityData", inMap);

			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Service Request Activity Details");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<Map<String, Object>>)outMap.get("results")))
					results =  (List<Map<String, Object>>)outMap.get("results");
			}
		} catch (Exception e) {
			Debug.logError(e, "Problem While Fetching Service Request Activity Details : " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getServiceDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<>();
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		String externalId = request.getParameter("srNumber");
		LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(), delegator);
		GenericValue userLogin = getUserLogin(request);
		Map<String, Object> inputMap = new HashMap<>();
		try {
			if(UtilValidate.isNotEmpty(start)){
				inputMap.put("start", start);
			}
			if(UtilValidate.isNotEmpty(length)){
				inputMap.put("length", length);
			}
			if(UtilValidate.isNotEmpty(externalId)){
				inputMap.put("externalId", externalId);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inputMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.getServiceDetails", inputMap);

			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Service Request");
				return "error";
			}else{
				
				results=(List)outMap.get("results");
			}
		} catch (Exception e) {
			Debug.logError(e, "Problem While Fetching Service Request : " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
    
	public static String getNoteData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(), delegator);
		String noteId = request.getParameter("noteId");
		Map<String,Object> inMap = FastMap.newInstance();
		try {
			if(UtilValidate.isNotEmpty(noteId)) {
				inMap.put("noteId", noteId);
				}
			if(UtilValidate.isNotEmpty(userLogin)) {
				inMap.put("userLogin", userLogin);
				}
			Map<String, Object> outMap = dispatcher.runSync("ticket.getNoteData", inMap);

			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Service Request Note Data");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<Map<String,Object>>)outMap.get("results")))
					   results =  (List<Map<String,Object>>)outMap.get("results");
			}	
				
		} catch (Exception e) {
			Debug.logError(e, "Problem While Fetching Service Request Note Data : " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
    
	@SuppressWarnings("unchecked")
	public static String getCustomerCommunicationInfo(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<>();
		LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(), delegator);
		Map<String,Object> inMap = FastMap.newInstance();
		String externalId = request.getParameter("srNumber");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		
		try {
			if (UtilValidate.isNotEmpty(externalId)) {
				inMap.put("externalId", externalId);
			}
			if (UtilValidate.isNotEmpty(userLogin)) {
				inMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.getCustomerCommunicationInfo", inMap);

			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Service Request Communication Pref Data");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((List<Map<String,Object>>)outMap.get("results")))
					   results =  (List<Map<String,Object>>)outMap.get("results");
			}	
		} catch (Exception e) {
			Debug.logError(e, "Problem While Fetching Service Request Communication Pref Data : " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
    
	public static List< Map<String, Object>> getSlaForSR(HttpServletRequest request, HttpServletResponse response)throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String srType = (String) context.get("srTypeId");
		String srCategory = (String) context.get("srCategoryId");
		String srSubCategory = (String) context.get("srSubCategoryId");
		try {
			List<EntityCondition> conditionList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(srType)) {
				conditionList.add( EntityCondition.makeCondition("srTypeId", EntityOperator.EQUALS, srType) );
			}
			if (UtilValidate.isNotEmpty(srCategory)) {
				conditionList.add( EntityCondition.makeCondition("srCategoryId", EntityOperator.EQUALS, srCategory) );
			}
			if (UtilValidate.isNotEmpty(srSubCategory)) {
				conditionList.add( EntityCondition.makeCondition("srSubCategoryId", EntityOperator.EQUALS, srSubCategory) );
			}
			if((UtilValidate.isNotEmpty(srSubCategory)) || UtilValidate.isNotEmpty(srCategory) || UtilValidate.isNotEmpty(srType)) {
				conditionList.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ACTIVE"));
			}	
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue slaConfig = EntityUtil.getFirst( delegator.findList("SrSlaConfig", mainConditons, null, null, null, false) );
			if (UtilValidate.isNotEmpty(slaConfig)) {
				String slaPeriodLvl = slaConfig.getString("slaPeriodLvl");
				String srPeriodUnit = slaConfig.getString("srPeriodUnit");
				Map<String, Object> dateContext = new LinkedHashMap<>();
				dateContext.put("srPeriodUnit", srPeriodUnit);
				dateContext.put("slaPeriodLvl", slaPeriodLvl);
				results.add(dateContext);
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception :  " + e.getMessage(), MODULE);
			results = (List<Map<String, Object>>) ServiceUtil.returnError("Error : "+e.getMessage());
		}
		return results;
	}

	public static String sendSrEmail(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String defaultScreenLocation = "component://ecommerce/widget/EmailProductScreens.xml#TellFriend";

		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String websiteId = (String) paramMap.get("websiteId");
		if (UtilValidate.isEmpty(websiteId)) {
			websiteId = WebSiteWorker.getWebSiteId(request);
		}
		paramMap.put("locale", UtilHttp.getLocale(request));
		paramMap.put("userLogin", session.getAttribute("userLogin"));

		Map<String, Object> context = new HashMap<>();
		context.put("bodyScreenUri", defaultScreenLocation);
		context.put("bodyParameters", paramMap);
		context.put("sendTo", paramMap.get("sendTo"));
		context.put("contentType", paramMap.get("contentType"));
		context.put("sendFrom", paramMap.get("sendFrom"));
		context.put("sendCc", paramMap.get("sendCC"));
		context.put("sendBcc", paramMap.get("sendBCC"));
		context.put("subject", paramMap.get("subject"));
		context.put("webSiteId", websiteId);

		try {
			dispatcher.runAsync("sendMailFromScreen", context);
		} catch (GenericServiceException e) {
			String errMsg = "Problem sending mail: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}
     
	public static String saveSrReview(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(), delegator);
		GenericValue userLogin=getUserLogin(request);
		Map<String,Object> inMap = FastMap.newInstance();
		String srNumber = request.getParameter("srNumber");
		String srTypeId = request.getParameter("srTypeId");
		String srCategoryId = request.getParameter("srCategoryId");
		String SRSubCategory = request.getParameter("SRSubCategory");
		String priorityStr = request.getParameter("priority");

		try {	
			if(UtilValidate.isNotEmpty(srNumber)) {
				inMap.put("custRequestId", srNumber);
			}
			if(UtilValidate.isNotEmpty(srTypeId)) {
				inMap.put("srTypeId", srTypeId);
			}
			if(UtilValidate.isNotEmpty(srCategoryId)) {
				inMap.put("srCategoryId", srCategoryId);

			}
			if(UtilValidate.isNotEmpty(SRSubCategory)) {
				inMap.put("SRSubCategory", SRSubCategory);
			}
			if(UtilValidate.isNotEmpty(priorityStr)){
				inMap.put("priority", priorityStr);
			}
			if (UtilValidate.isNotEmpty(userLogin)) {
				inMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.saveSrReview", inMap);

			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Saving Service Request Review Data");
				return "error";
			}else{
				if(UtilValidate.isNotEmpty((String)outMap.get("results")))
					request.setAttribute("_EVENT_MESSAGE_", (String)outMap.get("results"));
				return "success";
			}	
		} catch (Exception e) {
			String errMsg = "Problem While Saving SR Review Details " + e.toString();
			Debug.logError(e, errMsg, MODULE);
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
	}
	
	public static String findSRCustomers(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		List<Map<String, Object>> results = new ArrayList<>();
		String roleTypeId = request.getParameter("roleTypeId");
		String cinNumber = request.getParameter("cinNumber");
		String name = request.getParameter("name");
		String dob = request.getParameter("dob");
		String uid = request.getParameter("uid");
		String cName = request.getParameter("cName");
		String email = request.getParameter("email");
		String account = request.getParameter("account");
		String apNo = request.getParameter("apNo");
		String phone = request.getParameter("phone");
		Map<String,Object> inMap = FastMap.newInstance();
		GenericValue userLogin=getUserLogin(request);
		if (UtilValidate.isEmpty(userLogin)) {
			Map<String, Object> data = new HashMap<>();
			data.put("error", "No user login details found!");
			return doJSONResponse(response, data);
		}
		if(UtilValidate.isEmpty(roleTypeId)){
			roleTypeId="CUSTOMER";
		}
		try{

			if(UtilValidate.isNotEmpty(cinNumber)) {
				inMap.put("cinNumber", cinNumber);
			}
			if(UtilValidate.isNotEmpty(roleTypeId) && "ALL".equals(roleTypeId)) {
				inMap.put("roleTypeId", "ALL");
			}else{
				inMap.put("roleTypeId", roleTypeId);
			}
			if(UtilValidate.isNotEmpty(name)) {
				inMap.put("name", name);
			}
			if(UtilValidate.isNotEmpty(uid)) {
				inMap.put("uid", uid);
			}
			if(UtilValidate.isNotEmpty(cName)) {
				inMap.put("cName", cName);
			}
			if(UtilValidate.isNotEmpty(apNo)) {
				inMap.put("apNo", apNo);
			}
			if(UtilValidate.isNotEmpty(email)) {
				inMap.put("email", email);
			}
			if(UtilValidate.isNotEmpty(phone)) {
				inMap.put("phone", phone);
			}
			if(UtilValidate.isNotEmpty(account)) {
				inMap.put("account", account);
			}
			if(UtilValidate.isNotEmpty(dob)) {
				inMap.put("dob", dob);
			}
			if(UtilValidate.isNotEmpty(userLogin)) {
				inMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.findSRCustomers", inMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While find SRCustomers");
				return "error";
			}else{
				
				results=(List<Map<String, Object>>)outMap.get("results");
			}

		}
		catch (Exception e) {
			Debug.logError(e, "Problem While finding SRCustomers : " + e.getMessage(), MODULE);
		}
		return doJSONResponse(response, results);
	}
    
	public static String reassignSr(HttpServletRequest request, HttpServletResponse response)throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<String> results = new ArrayList<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String ownerId = (String) context.get("ownerId");
		String ownerUserLoginId = (String) context.get("ownerUserLoginId");
		String externalId = (String) context.get("externalId");
		String emplTeamId = (String) context.get("emplTeamId");
		Map<String, Object> inputMap = new HashMap<>();
		try {
			if(UtilValidate.isNotEmpty(ownerId)){
				inputMap.put("ownerId", ownerId);
			}
			if(UtilValidate.isNotEmpty(ownerUserLoginId)){
				inputMap.put("ownerUserLoginId", ownerUserLoginId);
			}
			if(UtilValidate.isNotEmpty(externalId)){
				inputMap.put("externalId", externalId);
			}
			if(UtilValidate.isNotEmpty(emplTeamId)){
				inputMap.put("emplTeamId", emplTeamId);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inputMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.reassignSr", inputMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Reassign Service Request");
				return "error";
			}else{
				
				results=(List<String>)outMap.get("results");
			}

		} catch (Exception e) {
			String errMsg = "" + e.toString();
			Debug.logError(e, errMsg, MODULE);
			String data = "";
			data= errMsg;
			results.add(data);
			return doJSONResponse(response,results);
		}
		return doJSONResponse(response,results);
	}
	
	@SuppressWarnings("unchecked")
	public static String resolveServiceRequest(HttpServletRequest request, HttpServletResponse response)throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List results = new ArrayList<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String srSubStatusId = (String) context.get("srSubStatusId1");
		String resolution = (String) context.get("resolution");
		String externalId = (String) context.get("externalId");
		List<String> externalIds = new ArrayList<>();
		String srStatusId = (String) context.get("srStatusId1");
		String srSubStatus = (String) context.get("srSubStatus");
    	String statusId = (String) context.get("statusId");
    	String subStatusId = (String) context.get("subStatusId");
    	String description = (String) context.get("description");
    	String custRequestName = (String) context.get("srName");
    	String resolutionFlag="resolution";
    	String resolveActivityflag="closeActivity";
    	String resolvesuccessflag="resolveSuccess";
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> result = new HashMap<>();
		List<GenericValue> workEffortIdList= new LinkedList<>();
			 
			 String isReqResolution = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_REQUIRED_RESOLUTION", "Y");
		
			 if(UtilValidate.isEmpty(resolution) && (UtilValidate.isEmpty(isReqResolution) || isReqResolution.equalsIgnoreCase("Y"))){
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
					result.put("resolutionFlag", resolutionFlag);
					results.add(result);
					return doJSONResponse(response,results);
				}
			 
				List<GenericValue> custRequestWorkEffort = EntityQuery.use(delegator).from("CustRequestWorkEffort").where("custRequestId", externalId).orderBy("-createdStamp").queryList();
				if (UtilValidate.isNotEmpty(custRequestWorkEffort)) {
					workEffortIdList = EntityUtil.getFieldListFromEntityList(custRequestWorkEffort, "workEffortId", true);
				}

				if (UtilValidate.isNotEmpty(workEffortIdList)) {
					List<EntityCondition> workEffortConditionList = FastList.newInstance();
					workEffortConditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN,workEffortIdList));
					workEffortConditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.IN,UtilMisc.toList("IA_OPEN")));
					EntityCondition wecondition = EntityCondition.makeCondition(workEffortConditionList, EntityOperator.AND);
					List <GenericValue> workEffortEntityData = EntityQuery.use(delegator).select("workEffortId","currentStatusId").from("WorkEffort").where(wecondition).orderBy("-createdStamp").queryList();
					if (UtilValidate.isNotEmpty(workEffortEntityData)) {
						result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
						result.put("resolveActivityflag", resolveActivityflag);
						results.add(result);
						return doJSONResponse(response,results);
					}
				}
		try {
			if(UtilValidate.isNotEmpty(context.get("externalIds"))){
				externalIds = (List)context.get("externalIds");
				inputMap.put("externalIds", externalIds);
			}
			if(UtilValidate.isNotEmpty(externalId)){
				inputMap.put("externalId", externalId);
			}
			if(UtilValidate.isNotEmpty(srSubStatusId)){
				inputMap.put("srSubStatusId", srSubStatusId);
			}
			if(UtilValidate.isNotEmpty(srStatusId)){
				inputMap.put("srStatusId", srStatusId);
			}
			if (UtilValidate.isNotEmpty(statusId)) {
				inputMap.put("statusId", statusId);
			}
			if (UtilValidate.isNotEmpty(subStatusId)) {
				inputMap.put("subStatusId", subStatusId);
			}
			if (UtilValidate.isNotEmpty(srSubStatus)) {
				inputMap.put("subStatusId", srSubStatus);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inputMap.put("userLogin", userLogin);
			}if(UtilValidate.isNotEmpty(resolution)){
				inputMap.put("resolution", Base64.getEncoder().encodeToString(resolution.getBytes("utf-8")));
			}if(UtilValidate.isNotEmpty(description)){
				inputMap.put("description", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.resolveServiceRequest", inputMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Resolving Service Request");
				return "error";
			}else{
				result.put("resolvesuccessflag", resolvesuccessflag);
				results.add(result);
				results=(List)outMap.get("results");
				
				String cNo = (String) context.get("cNo");
				String fromPartyId = (String) context.get("fromPartyId");
				Debug.log("== UPDATE SR owner =="+fromPartyId);

				if(UtilValidate.isNotEmpty(cNo)){
					fromPartyId = cNo;
				}
				
				String nsender = (String) context.get("fromEmailId");
				if (UtilValidate.isEmpty(nsender)) {
					nsender = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FROM_EMAIL_ID");
				}
				
				//Send survey email to the primary person of the ticket
				String surveyTemplateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_SURVEY_TMPL_SERVICE");
				String surveyEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FEEDBACK_SURVEY_ENABLED","N");
				
				String guestCustomerId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_DEFAULT_CUSTOMER_ID");
				
				GenericValue surveyEmailTemlateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",surveyTemplateId), false);
				
				GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", externalId).queryOne();
				String responsiblePerson =  UtilValidate.isNotEmpty(custRequest)?custRequest.getString("responsiblePerson"):"";
				GenericValue ownerUserLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", responsiblePerson).queryOne();
				String ownerPartyId = UtilValidate.isNotEmpty(ownerUserLogin)?ownerUserLogin.getString("partyId"):"";
				
				if(UtilValidate.isNotEmpty(surveyEmailTemlateData) && UtilValidate.isNotEmpty(nsender) 
						&& surveyEnabled.equalsIgnoreCase("Y") && "SR_CLOSED".equals(statusId) && UtilValidate.isNotEmpty(ownerPartyId) && !ownerPartyId.equalsIgnoreCase(userLogin.getString("partyId"))) {
					
					String surveyEmailContent = "";
				

					Map<String, String> ntoContactInformation1 = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,ownerPartyId,UtilMisc.toMap("isRetriveEmail", true),true);
					String nto = UtilValidate.isNotEmpty(ntoContactInformation1) ? ntoContactInformation1.get("EmailAddress") : "";
					String surveyTemplateFormContent = surveyEmailTemlateData.getString("templateFormContent");
					if (UtilValidate.isNotEmpty(surveyTemplateFormContent)) {
						if (org.apache.commons.codec.binary.Base64.isBase64(surveyTemplateFormContent)) {
							surveyTemplateFormContent = org.ofbiz.base.util.Base64.base64Decode(surveyTemplateFormContent);
						}
					}
					String subject ="Ticket Closure Survey - We value your feedback";
					if(UtilValidate.isNotEmpty(surveyEmailTemlateData.getString("subject"))) {
						if (UtilValidate.isNotEmpty(custRequestName)){
							subject = "SR# "+externalId+" ("+custRequestName+") - "+surveyEmailTemlateData.getString("subject");
						}else {
							subject = "SR# "+externalId+" - "+surveyEmailTemlateData.getString("subject");
						}
						
					}
					Debug.log("==nto =="+nto);
					if (UtilValidate.isNotEmpty(nto)) {
						// prepare email content [start]
						Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
						extractContext.put("delegator", delegator);
						extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
						extractContext.put("fromEmail", nsender);
						extractContext.put("toEmail", nto);
						extractContext.put("partyId", userLogin.getString("partyId"));
						extractContext.put("custRequestId", externalId);
						extractContext.put("emailContent", surveyTemplateFormContent);
						extractContext.put("templateId", surveyTemplateId);

						Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
						surveyEmailContent = (String) extractResultContext.get("emailContent");
						Map callCtxt = FastMap.newInstance();
						Map callResult = FastMap.newInstance();
						Map requestContext = FastMap.newInstance();

						requestContext.put("nsender", nsender);
						requestContext.put("nto", nto);
						requestContext.put("subject", subject);
						requestContext.put("emailContent", surveyEmailContent);
						requestContext.put("templateId", surveyTemplateId);
						
						callCtxt.put("requestContext", requestContext);
						callCtxt.put("userLogin", userLogin);

						Debug.log("==== Update SR sendEmail for survey ===="+callCtxt);

						callResult = dispatcher.runSync("common.sendEmail", callCtxt);
						if (ServiceUtil.isError(callResult)) {
							String errMsg = "Survey email send failed: "+ServiceUtil.getErrorMessage(callResult);
							request.setAttribute("_ERROR_MESSAGE_", errMsg);
							return "error";
						}
					}
				}
			}
			 
		} catch (Exception e) {
			String errMsg = "" + e.toString();
			Debug.logError(e, errMsg, MODULE);
			String data = "";
			data= errMsg;
			results.add(data);
			return doJSONResponse(response,results);
		}
		result.put("resolvesuccessflag", resolvesuccessflag);
		results.add(result);
		return doJSONResponse(response,results);
	}
	
	public static String saveServiceRequest(HttpServletRequest request, HttpServletResponse response)throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<String> results = new ArrayList<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String srSubStatusId = (String) context.get("srSubStatusId1");
		String externalId = (String) context.get("externalId");
		List<String> externalIds = new ArrayList<>();
		String srStatusId = (String) context.get("srStatusId1");
		if(UtilValidate.isEmpty(srStatusId))
		 srStatusId = (String) context.get("srStatusId");
		String statusId = (String) context.get("statusId");
    	String subStatusId = (String) context.get("subStatusId");
		Map<String, Object> inputMap = new HashMap<>();
		try {
			if(UtilValidate.isNotEmpty(context.get("externalIds"))){
				externalIds = (List<String>)context.get("externalIds");
				inputMap.put("externalIds", externalIds);
			}
			if(UtilValidate.isNotEmpty(externalId)){
				inputMap.put("externalId", externalId);
			}
			if(UtilValidate.isNotEmpty(srSubStatusId)){
				inputMap.put("srSubStatusId", srSubStatusId);
			}
			if(UtilValidate.isNotEmpty(srStatusId)){
				inputMap.put("srStatusId", srStatusId);
			}
			if (UtilValidate.isNotEmpty(statusId)) {
				inputMap.put("statusId",statusId);
			}
			if (UtilValidate.isNotEmpty(subStatusId)) {
				inputMap.put("subStatusId",subStatusId);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inputMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.saveServiceRequest", inputMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Saving Service Request");
				return "error";
			}else{
				
				results=(List)outMap.get("results");
			}
		} catch (Exception e) {
			String errMsg = "" + e.toString();
			Debug.logError(e, errMsg, MODULE);
			String data = "";
			data= errMsg;
			results.add(data);
			return doJSONResponse(response,results);
		}
		return doJSONResponse(response,results);
	}
	
	public static String getCustomerInfoForAddServiceRequest(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {	
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<>();
		String cinNumber = request.getParameter("cinNumber");
		String phoneNumber="";
		String emailAddr="";
		String Address="";
		String emailSolicitation="";
		String phoneSolicitation="";
		String addressSolicitation="";
		int operSrCount = 0;
		int opportunitiesCount = 0;
		Map<String, Object> data = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(cinNumber)) {
				
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, cinNumber),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, cinNumber)
						);
				
				GenericValue partyIdentification = EntityUtil.getFirst(delegator.findList("PartyAndPartyIdentification", condition, null, UtilMisc.toList("-createdStamp"), null, false));
				if (UtilValidate.isNotEmpty(partyIdentification)) {
					String partyId = partyIdentification.getString("partyId");
					GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", partyId).queryOne();
					data.put("partyId", partyId);
					String custName = "";
					if (UtilValidate.isNotEmpty(person)) {
						
						/*if (UtilValidate.isNotEmpty(person.getString("firstName"))) {
							String firstName = person.getString("firstName");
							if (UtilValidate.isNotEmpty(firstName)) {
								data.put("firstName", firstName);
								custName = firstName;
							}
						}
						if (UtilValidate.isNotEmpty(person.getString("middleName"))) {
							String middleName = person.getString("middleName");
							if (UtilValidate.isNotEmpty(middleName)) {
								data.put("middleName", middleName);
								custName = custName + " " + middleName;
							}
						}
						if (UtilValidate.isNotEmpty(person.getString("lastName"))) {
							String lastName = person.getString("lastName");
							if (UtilValidate.isNotEmpty(lastName)) {
								data.put("lastName", lastName);
								custName = custName + " " + lastName;
							}
						}*/
						
						
						if (UtilValidate.isNotEmpty(person.getString("nationalId"))) {
							String nationalNo = person.getString("nationalId");
							if (UtilValidate.isNotEmpty(nationalNo)) {
								data.put("nationalNo", nationalNo);
							}
						}
					}
					
					custName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyId, false);
					
					List conditionsList = FastList.newInstance();
					if (UtilValidate.isNotEmpty(partyId)) {
						
						Map<String, String> primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
						
						phoneSolicitation = primaryContactInformation.get("phoneSolicitation");
						emailSolicitation = primaryContactInformation.get("emailSolicitation");
						//addressSolicitation = primaryContactInformation.get("addressSolicitation");
						
						emailAddr = primaryContactInformation.get("EmailAddress");
						phoneNumber = primaryContactInformation.get("PrimaryPhone");
						//Address = primaryContactInformation.get("");
						
						/*Set < String > fieldsToSelect = new TreeSet < String > ();
						fieldsToSelect.add("contactMechId");
						fieldsToSelect.add("partyId");
						fieldsToSelect.add("allowSolicitation");

						List<GenericValue> partyContactMechList = EntityQuery.use(delegator).select(fieldsToSelect).from("PartyContactMech").where("partyId", partyId).queryList();

						if (UtilValidate.isNotEmpty(partyContactMechList)) {
							List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(partyContactMechList, "contactMechId", true);
							if (UtilValidate.isNotEmpty(contactMechIds)) {
								conditionsList.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds));
							}
						}

						if (UtilValidate.isNotEmpty(conditionsList)) {
							EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
							List < GenericValue > ContactMechList = delegator.findList("ContactMech", mainConditons, null, null, null, false);
							if (UtilValidate.isNotEmpty(ContactMechList)) {
								for (GenericValue eachContactMech: ContactMechList) {
									String contactMechTypeId = eachContactMech.getString("contactMechTypeId");
									if (contactMechTypeId.equals("EMAIL_ADDRESS")) {
										emailAddr = eachContactMech.getString("infoString");
										GenericValue partyEmailMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",eachContactMech.getString("contactMechId")), null, false));
										if(partyEmailMech != null && partyEmailMech.size() > 0) {
											emailSolicitation = UtilValidate.isNotEmpty(partyEmailMech.getString("allowSolicitation"))? partyEmailMech.getString("allowSolicitation") : "N";
										}
									}
									if (contactMechTypeId.equals("TELECOM_NUMBER")) {
										phoneNumber = eachContactMech.getString("infoString");
										GenericValue partyPhoneMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",eachContactMech.getString("contactMechId")), null, false));
										if(partyPhoneMech != null && partyPhoneMech.size() >0) {
											phoneSolicitation = UtilValidate.isNotEmpty(partyPhoneMech.getString("allowSolicitation"))? partyPhoneMech.getString("allowSolicitation") : "N";
										}
									}
									if (contactMechTypeId.equals("POSTAL_ADDRESS")) {
										Address = eachContactMech.getString("infoString");
										GenericValue partyAddressMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",eachContactMech.getString("contactMechId")), null, false));
										if(partyAddressMech != null && partyAddressMech.size() >0) {
											addressSolicitation = UtilValidate.isNotEmpty(partyAddressMech.getString("allowSolicitation"))? partyAddressMech.getString("allowSolicitation") : "N";
										}
									}
								}
							}
						}*/
						
						
						GenericValue roleIdentification = EntityUtil
								.getFirst(delegator.findList("RoleTypeAndParty", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, false));
						String customerType = "";
						String customerTypeId = "";
						String prospectNo = "";
						String vPlusNo = "";
						String cifNo = "";
						if (UtilValidate.isNotEmpty(roleIdentification)) {
							customerType = roleIdentification.getString("description");
							customerTypeId = roleIdentification.getString("roleTypeId");
							if(UtilValidate.isNotEmpty(customerTypeId) && customerTypeId.equals("NON_CRM")){
								vPlusNo = cinNumber;
							}
							if(UtilValidate.isNotEmpty(customerTypeId) && customerTypeId.equals("PROSPECT")){
								prospectNo = cinNumber;
							}
							if(UtilValidate.isNotEmpty(customerTypeId) && customerTypeId.equals("CUSTOMER")){
								cifNo = cinNumber;
							}
						}
						conditionsList.clear();
						conditionsList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId));
						conditionsList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_OPEN"));
						EntityCondition mainCondition = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
						List < GenericValue > custRequest = EntityQuery.use(delegator).from("CustRequest").select("custRequestId").where(mainCondition).queryList();
						if(UtilValidate.isNotEmpty(custRequest)){
							operSrCount=custRequest.size();
						}
						conditionsList.clear();
						conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
						EntityCondition roleCnd = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
						List < GenericValue > salesOpportunityRoleList = EntityQuery.use(delegator).from("SalesOpportunityRole").select("salesOpportunityId").where(roleCnd).queryList();
						if(UtilValidate.isNotEmpty(salesOpportunityRoleList)){
							opportunitiesCount=salesOpportunityRoleList.size();
						}
						data.put("opportunitiesCount", opportunitiesCount);
						data.put("operSrCount", operSrCount);
						data.put("custName", custName);
						data.put("cifNo", cifNo);
						data.put("prospectNo", prospectNo);
						data.put("vPlusNo", vPlusNo);
						data.put("customerType", customerType);
						data.put("customerTypeId", customerTypeId);
						data.put("phoneNumber", phoneNumber);
						data.put("emailAddr", emailAddr);
						data.put("Address", Address);
						data.put("phoneSolicitation", phoneSolicitation);
						data.put("emailSolicitation", emailSolicitation);
						data.put("addressSolicitation", addressSolicitation);   
						results.add(data);
					}
				}
			}

		} catch (Exception e) {
			Debug.logError(e, "Exception :  " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
    
	@SuppressWarnings("unchecked")
	public static String viewServiceActivityDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List < Map<String, Object>> results = new ArrayList<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String workEffortId = (String) context.get("workEffortId");
		Map<String, Object> inputMap = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(workEffortId)) {
				inputMap.put("workEffortId",workEffortId);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inputMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("ticket.viewServiceActivityDetails", inputMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching View Activity Details");
				return "error";
			}else{
				
				results=(List < Map<String, Object>>)outMap.get("results");
			}
		} catch (Exception e) {
			Debug.logError(e, "Problem While Fetching View Activity Details : " + e.getMessage(), MODULE);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("_ERROR_MESSAGE_", e.getMessage());
			results.add(data);
		}
		return doJSONResponse(response, results);
	}
    
	public static Map<String, Object> updateServiceActivityDetails(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Locale locale = (Locale) context.get("locale");
		Map<String, Object> results =ServiceUtil.returnSuccess();

		String workEffortId = (String) context.get("workEffortId");
		String currentStatusId = (String) context.get("currentStatusId");
		try {
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("workEffortId");
			fieldsToSelect.add("currentStatusId");
			GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("WorkEffort",
					UtilMisc.toMap("workEffortId", workEffortId), null, false));
			if (UtilValidate.isNotEmpty(updateConfigRecords)) {
				updateConfigRecords.put("workEffortId", workEffortId);
				updateConfigRecords.put("currentStatusId", currentStatusId);
				updateConfigRecords.store();
				String responseMessage = UtilProperties.getMessage(RESOURCE, "ServiceViewActivitySuccessfullyUpdated", locale);
				results = ServiceUtil.returnSuccess(responseMessage);
			}

		} catch (Exception e) {
			Debug.logError(e, "Exception :  " + e.getMessage(), MODULE);
		}
		return results;
	}

	public static Map<String, Object> closedServiceActivityDetails(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Locale locale = (Locale) context.get("locale");
		Map<String, Object> results = ServiceUtil.returnSuccess();
		String workEffortId = (String) context.get("workEffortId");

		try {
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("workEffortId");
			GenericValue updateConfigRecords = EntityUtil.getFirst(
					delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), null, false));
			if (UtilValidate.isNotEmpty(updateConfigRecords)) {
				updateConfigRecords.put("workEffortId", workEffortId);
				updateConfigRecords.put("currentStatusId", "IA_MCOMPLETED");
				updateConfigRecords.store();
				String responseMessage = UtilProperties.getMessage(RESOURCE, "ServiceViewActivitySuccessfullyClosed",
						locale);
				results = ServiceUtil.returnSuccess(responseMessage);
			}

		} catch (Exception e) {
			Debug.logError(e, "Exception :  " + e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static String getPartyRoleTypeId(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		
		List < Map<String, Object>> results = new ArrayList<>();
		String validRoleTypeId="";
		
		try{
			validRoleTypeId = org.fio.homeapps.util.PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("ACCOUNT","LEAD"), delegator);
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("roleTypeId", validRoleTypeId);
			results.add(result);
		} catch (Exception e) {
			Debug.logError(e, "Exception :  " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	
	public static String getAllSrStatuses(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<>();
		
		try {
			List<GenericValue> srStatusItemList = EntityQuery.use(delegator).select("statusId","description").from("StatusItem").where("statusTypeId", "SR_STATUS_ID").queryList();
			
			for (GenericValue eachStatusItem: srStatusItemList) {
				Map<String, Object> data = new HashMap<>();
				data.put("statusId", eachStatusItem.getString("statusId"));
				data.put("description", eachStatusItem.getString("description"));
				results.add(data);
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception :  " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	
	}
	
	public static String updateServiceRequest(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<String> results = new ArrayList<>();
		String custRequestId = request.getParameter("srNumber");
		String description = request.getParameter("description");
		String resolution = request.getParameter("resolution");
		
		try {
			GenericValue userLogin=getUserLogin(request);
    		String userLoginId = userLogin.getString("userLoginId");
    		
			GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryFirst();
			
			if (UtilValidate.isNotEmpty(custRequest)) {
				custRequest.put("description", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
				custRequest.put("resolution", Base64.getEncoder().encodeToString(resolution.getBytes("utf-8")));
				custRequest.put("lastModifiedDate", UtilDateTime.nowTimestamp());
				custRequest.put("lastModifiedByUserLogin", userLoginId);
				custRequest.store();
				request.setAttribute("custRequestId", custRequestId);
				request.setAttribute("_EVENT_MESSAGE_", "SR Updated Successfully");
				return "success";
			}
			
		} catch (Exception e) {
			Debug.logError(e, "Problem While Updating SR : " + e.getMessage(), MODULE);
			request.setAttribute("_ERROR_MESSAGE_", "Problem While Updating SR ");
			return "error";
		}
		return doJSONResponse(response, results);
	}
	
	public static String updateSrStatus(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<String> results = new ArrayList<>();
		String custRequestId = request.getParameter("srNumber");
		String statusId = request.getParameter("srStatusId");
		String resolution = request.getParameter("resolution");
		String description = request.getParameter("description");

		try {
			GenericValue userLogin=getUserLogin(request);
			String userLoginId = userLogin.getString("userLoginId");

			GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryFirst();

			if (UtilValidate.isNotEmpty(custRequest)) {

				if(UtilValidate.isNotEmpty(statusId)){
					custRequest.put("statusId", statusId);
					custRequest.put("resolution", resolution);
					custRequest.put("description", description);

					if("SR_CLOSED".equals(statusId) || "SR_CANCELLED".equals(statusId)){

						List<GenericValue> custRequestWorkEffortList = EntityQuery.use(delegator).select("workEffortId").from("CustRequestWorkEffort").where("custRequestId", custRequestId).queryList();

						if(UtilValidate.isNotEmpty(custRequestWorkEffortList)){
							List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList, "workEffortId", true);


							List<EntityCondition> conditionlist1 = FastList.newInstance();
							conditionlist1.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
							conditionlist1.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_IN, UtilMisc.toList("IA_COMPLETED", "IA_MCOMPLETED")));

							EntityCondition condition1 = EntityCondition.makeCondition(conditionlist1, EntityOperator.AND);
							List<GenericValue> workEffortList = delegator.findList("WorkEffort", condition1,null, null, null, false);
							if(UtilValidate.isNotEmpty(workEffortList)){
								request.setAttribute("custRequestId", custRequestId);
								request.setAttribute("_ERROR_MESSAGE_", "There are Open Activities tagged to this " +custRequestId+ ". Please close the Open Activities before closing/cancelling the SR");
								Map<String, Object> data = FastMap.newInstance();
								data.put("error", "There are Open Activities tagged to this " +custRequestId+ ". Please close the Open Activities before closing/cancelling the SR");
								results.add("error");
								return doJSONResponse(response, results);
								//return "error";
							}

						}
					}
					
					String result = CommonEvents.updateServiceRequestEvent(request, response);
					
					if(UtilValidate.isNotEmpty(result) && "success".equals(result)) {
						request.setAttribute("custRequestId", custRequestId);
						request.setAttribute("_EVENT_MESSAGE_", "SR Updated Successfully"+": "+custRequestId);
		        	}
					request.setAttribute("_ERROR_MESSAGE_", "Problem While Updating Service Request");
					return "error";
				}


			}				


		} catch (Exception e) {
			Debug.logError(e, "Problem While Updating SR Status : " + e.getMessage(), MODULE);
			request.setAttribute("_ERROR_MESSAGE_", "Problem While Updating SR Status");
			return "error";
		}
		return doJSONResponse(response, results);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	 public static String redirectSRParty(HttpServletRequest request, HttpServletResponse response) throws GenericServiceException {

		 Delegator delegator = (Delegator) request.getAttribute("delegator");
		 HttpSession session = request.getSession();
		 GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		 String requestUri = request.getParameter("requestUri");
		 Map<String, Object> context = UtilHttp.getCombinedMap(request);

		 String srNumber = (String) context.get("srNumber");
		 String externalKey = (String) context.get("externalLoginKey");

		 List<Map<String, Object>> dataList = new ArrayList<>();
		 String redirectUrl = null;

		 try {

			 if (UtilValidate.isNotEmpty(srNumber)) {

				 List conditionsList = FastList.newInstance();
				 conditionsList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, srNumber));
				 EntityCondition roleConditions = EntityCondition.makeCondition(EntityOperator.OR,
						 EntityCondition.makeCondition("customerRelatedType", EntityOperator.EQUALS, "ACCOUNT"),
						 EntityCondition.makeCondition("customerRelatedType", EntityOperator.EQUALS, "LEAD"),
						 EntityCondition.makeCondition("customerRelatedType", EntityOperator.EQUALS, "CONTACT")
						 );
				 conditionsList.add(roleConditions);

				 EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
				 GenericValue custRequestDet = EntityUtil.getFirst( delegator.findList("CustRequest", mainConditons, UtilMisc.toSet("fromPartyId"), null, null, false) );
				 if (UtilValidate.isNotEmpty(custRequestDet) &&  custRequestDet != null) {
					 String custPartyId = custRequestDet.getString("fromPartyId");
					 GenericValue partyDetails = EntityQuery.use(delegator).from("Party").where("partyId", custPartyId).queryOne();
					 if (UtilValidate.isNotEmpty(partyDetails)) {
						 String roleTypeId = partyDetails.getString("roleTypeId");
						 if(UtilValidate.isNotEmpty(roleTypeId) && roleTypeId.equals("ACCOUNT")){
							 redirectUrl = "/account-portal/control/viewAccount?partyId="+custPartyId+"&externalLoginKey="+externalKey;
							 // redirect
							 Debug.logInfo("redirectUrl = " + redirectUrl, MODULE);
							 response.sendRedirect(redirectUrl);
						 }else if(UtilValidate.isNotEmpty(roleTypeId) && roleTypeId.equals("LEAD")){
							 redirectUrl = "/lead-portal/control/viewLead?partyId="+custPartyId+"&externalLoginKey="+externalKey;
							 // redirect
							 Debug.logInfo("redirectUrl = " + redirectUrl, MODULE);
							 response.sendRedirect(redirectUrl);
						 } 
						 else if(UtilValidate.isNotEmpty(roleTypeId) && roleTypeId.equals("CONTACT")){
							 redirectUrl = "/contact-portal/control/viewContact?partyId="+custPartyId+"&externalLoginKey="+externalKey;
							 // redirect
							 Debug.logInfo("redirectUrl = " + redirectUrl, MODULE);
							 response.sendRedirect(redirectUrl);
						 } 

					 }

				 }

			 }

		 } catch (Exception e) {
			 Debug.logError(e, "Exception :  " + e.getMessage(), MODULE);
			 return doJSONResponse(response, e.getMessage());
		 }
		 return redirectUrl;
	 }
	
	public static String searchSRHistorys(HttpServletRequest request, HttpServletResponse response) {
		
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String custRequestId = (String) context.get("custRequestId");
		String clientPortal = (String) context.get("clientPortal");
		String externalId = (String) context.get("externalId");
		String srNumber = (String) context.get("srNumber");
		
		if (UtilValidate.isNotEmpty(externalId)){
			custRequestId = externalId;
		}
		if (UtilValidate.isNotEmpty(srNumber)){
			custRequestId = srNumber;
		}		
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        
        try {
        	
			EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
            efo.setOffset(0);
            efo.setLimit(1000);
			
			List<GenericValue> custRequestHistoryList = delegator.findList("CustRequestHistory", EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId), null, UtilMisc.toList("-createdStamp"), efo, false);
			
			if (UtilValidate.isNotEmpty(custRequestHistoryList)) {
				
				for (GenericValue history : custRequestHistoryList) {
					
					Map<String, Object> data = new HashMap<String, Object>();
					
					data.put("srHistoryId", history.getString("custRequestHistoryId"));
					data.put("srNumber", history.getString("custRequestId"));
					data.put("description", history.getString("description"));
					data.put("resolution", history.getString("resolution"));
					data.put("srName", history.getString("custRequestName"));
					data.put("srType", org.fio.homeapps.util.DataUtil.getCustRequestTypeDesc(delegator, history.getString("custRequestTypeId")));
					data.put("srCategory", org.fio.homeapps.util.DataUtil.getCustRequestCategoryDesc(delegator, history.getString("custRequestCategoryId")));
					data.put("srSubCategory", org.fio.homeapps.util.DataUtil.getCustRequestCategoryDesc(delegator, history.getString("custRequestSubCategoryId")));
					data.put("srPriority", EnumUtil.getEnumDescriptionByEnumId(delegator, history.getString("priority")));
					
					String srStatusId = history.getString("statusId");
					
					if (UtilValidate.isNotEmpty(clientPortal) && "clientPortal".equals(clientPortal)){
						Set<String> clientOpenStatus = new HashSet<String>();
						clientOpenStatus.add("SR_OPEN");
						clientOpenStatus.add("SR_ASSIGNED");
						clientOpenStatus.add("SR_IN_PROGRESS");
						clientOpenStatus.add("SR_FEED_PROVIDED");
						
						if (clientOpenStatus.contains(srStatusId)) {
							data.put("srStatus", org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, "SR_OPEN"));
						}else{
							data.put("srStatus", org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, srStatusId));
						}
						
					}else{
						data.put("srStatus", org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, srStatusId));
					}
					
					data.put("srStatusId", history.getString("statusId"));
					data.put("srSource", history.getString("custReqSrSource"));
					data.put("orderId", history.getString("custOrderId"));
					data.put("owner", history.getString("ownerId"));
					data.put("srPrimaryContact", history.getString("custReqPrimaryContact"));
					
					data.put("createdBy", history.getString("createdByUserLogin"));
					data.put("modifiedBy", history.getString("lastModifiedByUserLogin"));
					data.put("closedBy", history.getString("closedByUserLogin"));
					
					data.put("createdDate", UtilValidate.isNotEmpty(history.get("createdDate")) ? UtilDateTime.timeStampToString(history.getTimestamp("createdDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
					data.put("modifiedDate", UtilValidate.isNotEmpty(history.get("lastModifiedDate")) ? UtilDateTime.timeStampToString(history.getTimestamp("lastModifiedDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
					data.put("closedDate", UtilValidate.isNotEmpty(history.get("closedByDate")) ? UtilDateTime.timeStampToString(history.getTimestamp("closedByDate"), "dd/MM/yyyy", TimeZone.getDefault(), null) : "");
					
					dataList.add(data);
                }
            }

        } catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
            return doJSONResponse(response, e.getMessage());
        }
        
        return doJSONResponse(response, dataList);
	}
	
	public static String getSrDashboardDataList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<GenericValue> resultList = null;
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		String filterBy = (String) context.get("filterBy");
		String filterType = (String) context.get("filterType");
		String srLocation = (String) context.get("srLocation");
		String partyId = (String) context.get("partyId");
		String clientPortal = (String) context.get("clientPortal");
		String externalLoginKey = (String) context.get("externalLoginKey");
		String custRequestDomainType = (String) context.get("custRequestDomainType");
		
		List<Map<String, Object>> dataList = new ArrayList<>();
		long start = System.currentTimeMillis();
		Timestamp systemTime = UtilDateTime.nowTimestamp();
		Map<String, Object> callCtxt = FastMap.newInstance();
        Map<String, Object> callResult = FastMap.newInstance();
		try {
			
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			List<String> statusIds = new ArrayList<>();
			String userLoginId = userLogin.getString("userLoginId");
			if((UtilValidate.isNotEmpty(filterType) || UtilValidate.isNotEmpty(filterBy))  && UtilValidate.isNotEmpty(userLoginId)) {
				Set<String> fieldsToSelect = new LinkedHashSet<String>();
				
				fieldsToSelect.add("custRequestId");fieldsToSelect.add("custRequestName");fieldsToSelect.add("fromPartyId");fieldsToSelect.add("statusId");fieldsToSelect.add("custRequestTypeId");
	            fieldsToSelect.add("custRequestCategoryId");fieldsToSelect.add("custRequestSubCategoryId");fieldsToSelect.add("createdDate");fieldsToSelect.add("responsiblePerson");fieldsToSelect.add("emplTeamId");fieldsToSelect.add("openDateTime");
	            fieldsToSelect.add("priority");fieldsToSelect.add("ownerBu");fieldsToSelect.add("custOrderId");fieldsToSelect.add("custReqSrSource");fieldsToSelect.add("custReqOnceDone");
	            fieldsToSelect.add("externalId");fieldsToSelect.add("createdByUserLogin");fieldsToSelect.add("lastModifiedDate");fieldsToSelect.add("lastModifiedByUserLogin");
	            fieldsToSelect.add("closedByDate");fieldsToSelect.add("closedByUserLogin");fieldsToSelect.add("commitDate");fieldsToSelect.add("preEscalationDate");
	            
	            fieldsToSelect.add("homePhoneNumber");fieldsToSelect.add("offPhoneNumber");fieldsToSelect.add("mobileNumber");
	            fieldsToSelect.add("contractorEmail");fieldsToSelect.add("contractorHomePhone");fieldsToSelect.add("contractorOffPhone");fieldsToSelect.add("contractorMobilePhone");
	            //fieldsToSelect.add("crpPartyId");
				//fieldsToSelect.add("roleTypeId");
				//fieldsToSelect.add("crpThruDate");
	            fieldsToSelect.add("custReqDocumentNum");
	            //fieldsToSelect.add("primAttrName");fieldsToSelect.add("primAttrValue");
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				
				dynamicView.addMemberEntity("CR", "CustRequest");
				dynamicView.addAlias("CR", "custRequestId","custRequestId", null, Boolean.FALSE, Boolean.TRUE, null);
				dynamicView.addAlias("CR", "custRequestName");
				dynamicView.addAlias("CR", "fromPartyId");
				dynamicView.addAlias("CR", "statusId");
				dynamicView.addAlias("CR", "custRequestTypeId");
				dynamicView.addAlias("CR", "custRequestCategoryId");
				dynamicView.addAlias("CR", "custRequestSubCategoryId");
				dynamicView.addAlias("CR", "openDateTime");
				dynamicView.addAlias("CR", "createdDate");
				dynamicView.addAlias("CR", "responsiblePerson");
				dynamicView.addAlias("CR", "emplTeamId");
				dynamicView.addAlias("CR", "priority");
				dynamicView.addAlias("CR", "ownerBu");
				dynamicView.addAlias("CR", "custOrderId");
				dynamicView.addAlias("CR", "custReqSrSource");
				dynamicView.addAlias("CR", "custReqOnceDone");
				dynamicView.addAlias("CR", "externalId");
				dynamicView.addAlias("CR", "createdByUserLogin");
				dynamicView.addAlias("CR", "lastModifiedDate");
				dynamicView.addAlias("CR", "lastModifiedByUserLogin");
				dynamicView.addAlias("CR", "closedByDate");
				dynamicView.addAlias("CR", "closedByUserLogin");
				dynamicView.addAlias("CR", "lastUpdatedTxStamp");
				dynamicView.addAlias("CR", "custReqDocumentNum");
				dynamicView.addAlias("CR", "custRequestDomainType");
				
				dynamicView.addMemberEntity("CRS", "CustRequestSupplementory");
				dynamicView.addAlias("CRS", "commitDate");
				dynamicView.addAlias("CRS", "preEscalationDate");
				dynamicView.addAlias("CRS", "pstlPostalCode");
				dynamicView.addAlias("CRS", "pstlPostalCodeExt");
				dynamicView.addAlias("CRS", "pstlPostalCity");
				dynamicView.addAlias("CRS", "pstlStateProvinceGeoId");
				dynamicView.addAlias("CRS", "pstlCountryGeoId");
				dynamicView.addAlias("CRS", "pstlCountyGeoId");
				dynamicView.addAlias("CRS", "homePhoneNumber");
				dynamicView.addAlias("CRS", "offPhoneNumber");
				dynamicView.addAlias("CRS", "mobileNumber");
				dynamicView.addAlias("CRS", "contractorEmail");
				dynamicView.addAlias("CRS", "contractorHomePhone");
				dynamicView.addAlias("CRS", "contractorOffPhone");
				dynamicView.addAlias("CRS", "contractorMobilePhone");
				dynamicView.addAlias("CRS", "purchaseOrder");
				
				dynamicView.addAlias("CRS", "descriptionRawTxt");
				dynamicView.addAlias("CRS", "resolutionRawTxt");
				
				dynamicView.addViewLink("CR", "CRS", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
				
				/*
				dynamicView.addMemberEntity("CRA1", "CustRequestAttribute");
				dynamicView.addAlias("CRA1", "primAttrName", "attrName",null,Boolean.FALSE,Boolean.FALSE,null);
				dynamicView.addAlias("CRA1", "primAttrValue", "attrValue",null,Boolean.FALSE,Boolean.FALSE,null);
				dynamicView.addViewLink("CR", "CRA1", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
				*/
				
				
				String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				
				String loggedUserRole = DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
				
				List loggedUserSecurityRoles = FastList.newInstance();
				String parentRoleId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
		    	if(UtilValidate.isNotEmpty(parentRoleId)) {
		    		List<GenericValue> partyRoleList = DataUtil.getPartyRoles(delegator, userLoginPartyId, parentRoleId);
		    		loggedUserSecurityRoles = EntityUtil.getFieldListFromEntityList(partyRoleList, "roleTypeId", true);
		    	}
		    	
				if("my-sr".equals(filterType)) {
					if("clientPortal".equals(clientPortal)) {

						List<String> technicianRoles = new ArrayList<>();
						String techRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "TECHNICIAN_ROLE", "TECHNICIAN");
						if(UtilValidate.isNotEmpty(techRole) && techRole.contains(",")) {
							technicianRoles = org.fio.admin.portal.util.DataUtil.stringToList(techRole, ",");
						} else if(UtilValidate.isNotEmpty(techRole)) {
							technicianRoles.add(techRole);
						}
						if(technicianRoles.contains(loggedUserRole)) {
							dynamicView.addMemberEntity("CRWE", "CustRequestWorkEffort");
							dynamicView.addAlias("CRWE", "workEffortId");
							dynamicView.addViewLink("CR", "CRWE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("custRequestId"));

							dynamicView.addMemberEntity("WE", "WorkEffort");
							dynamicView.addAlias("WE", "currentStatusId");
							dynamicView.addViewLink("CRWE", "WE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));

							dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
							dynamicView.addAlias("WEPA", "techPartyId", "partyId", "", null, null, "");
							dynamicView.addAlias("WEPA", "techRoleTypeId", "roleTypeId", "", null, null, "");
							dynamicView.addAlias("WEPA", "ownerFromDate", "fromDate", "", null, null, "");
							dynamicView.addAlias("WEPA", "ownerThruDate", "thruDate", "", null, null, "");
							dynamicView.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));

							conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_IN, UtilMisc.toList("IA_MCOMPLETED","IA_COMPLETED")),
									EntityCondition.makeCondition("techPartyId", EntityOperator.EQUALS, userLoginPartyId),
									EntityCondition.makeCondition("techRoleTypeId", EntityOperator.IN, UtilMisc.toList(loggedUserRole)),
									EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr("ownerFromDate", "ownerThruDate"))
									));



						} else {
							dynamicView.addMemberEntity("CRP", "CustRequestParty");
							dynamicView.addAlias("CRP", "partyId");
							dynamicView.addAlias("CRP", "roleTypeId");
							dynamicView.addAlias("CRP", "fromDate");
							dynamicView.addAlias("CRP", "thruDate");
							dynamicView.addViewLink("CR", "CRP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("custRequestId"));

							String isSuperiorContact = DataHelper.isSuperiorDealerContact(delegator, userLoginPartyId, "IS_SUPERIOR_DLR_CONTACT");

							if("Y".equals(isSuperiorContact)) {
								List<String> allDealerIds = DataHelper.getAllDealerByContact(delegator, userLoginPartyId);
								if(UtilValidate.isNotEmpty(allDealerIds)) {
									conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("partyId", EntityOperator.IN, allDealerIds),
											EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, "ACCOUNT"),
											EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr())
											));
								} else {
									conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginPartyId),
											EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, loggedUserSecurityRoles),
											EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr())
											));
								}
							} else {
								
								if("sr-open".equals(filterBy)) {
									conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginPartyId),
											EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, loggedUserSecurityRoles),
											EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr())
											));
								}
								if("my-company-request".equals(filterBy)) {
									List<String> allDealerIds = DataHelper.getAllDealerByContact(delegator, userLoginPartyId);
									conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("partyId", EntityOperator.IN, allDealerIds),
											EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT"),
											EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr())
											));
									//filterBy = "sr-open";
								} else {
									conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginPartyId),
											EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, loggedUserSecurityRoles),
											EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr())
											));
								}
							}
						}
						
						
					} else {
						List<String> coordinatorRoles = new ArrayList<>();
						String csrRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "COORDINATOR_ROLE", "CUST_SERVICE_REP");
						if(UtilValidate.isNotEmpty(csrRole) && csrRole.contains(",")) {
							coordinatorRoles = org.fio.admin.portal.util.DataUtil.stringToList(csrRole, ",");
						} else if(UtilValidate.isNotEmpty(csrRole)) {
							coordinatorRoles.add(csrRole);
						}
						
						List<String> tsmRoles = new ArrayList<>();
						String tsmRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "TSM_ROLE", "SALES_REP");
						if(UtilValidate.isNotEmpty(tsmRole) && tsmRole.contains(",")) {
							tsmRoles = org.fio.admin.portal.util.DataUtil.stringToList(tsmRole, ",");
						} else if(UtilValidate.isNotEmpty(tsmRole)) {
							tsmRoles.add(tsmRole);
						}
						if(coordinatorRoles.contains(loggedUserRole))
							conditions.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, userLoginId));
						else if(tsmRoles.contains(loggedUserRole)) {
							dynamicView.addMemberEntity("CRP1", "CustRequestAttribute");
							dynamicView.addAlias("CRP1", "tsmAttrName", "attrName", "", null, null, "");
							dynamicView.addAlias("CRP1", "tsmAttrValue", "attrValue", "", null, null, "");					
							dynamicView.addViewLink("CR", "CRP1", Boolean.FALSE, ModelKeyMap.makeKeyMapList("custRequestId"));
							
							String customFieldName = "ANR_"+loggedUserRole;
							String customFieldId = DataHelper.getCustomFieldId(delegator, "ANCHOR_ROLES", customFieldName);
							if(UtilValidate.isNotEmpty(customFieldId)) {
								conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
										EntityCondition.makeCondition("tsmAttrName", EntityOperator.EQUALS, customFieldId),
										EntityCondition.makeCondition("tsmAttrValue", EntityOperator.EQUALS, userLoginPartyId)
										));
							} else {
								result.put("list", new ArrayList<Map<String, Object>>());
								result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
								result.put(ModelService.ERROR_MESSAGE, customFieldName + "not found!");
								return doJSONResponse(response, result);
							}
						} else
							conditions.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, userLoginId));
					}
					
				} else if("my-teams-sr".equals(filterType)) {
					List<GenericValue> emplPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("partyId",userLoginPartyId).filterByDate().queryList();
					List<String> teams = UtilValidate.isNotEmpty(emplPositionFulfillments) ? EntityUtil.getFieldListFromEntityList(emplPositionFulfillments, "emplTeamId", true) : new ArrayList<>();
					conditions.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, teams));
				} else if("my-bu-sr".equals(filterType)) {
					/*
					Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, userLoginPartyId);
					String businessUnit = (String) buTeamData.get("businessUnit");
					if(UtilValidate.isNotEmpty(businessUnit)) {
						List<String> buList = new ArrayList<String>();
	                    DataUtil.getHierarchyBu(delegator, UtilMisc.toList(businessUnit), buList);
	                    buList.add(businessUnit);
	                    Debug.logInfo("Bu list : "+buList, MODULE);
	                    EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
	                    		EntityCondition.makeCondition("businessUnit", EntityOperator.IN, buList),
	                    		EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, "Y")
	                    		);
						List<GenericValue> emplTeamList = EntityQuery.use(delegator).from("EmplTeam").where(condition).queryList();
						List<String> teams = UtilValidate.isNotEmpty(emplTeamList) ? EntityUtil.getFieldListFromEntityList(emplTeamList, "emplTeamId", true) : new ArrayList<>();
						
						conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, teams),
								EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, ""),
								EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, null)
								));
					}
					*/
				}
				
				if(UtilValidate.isNotEmpty(srLocation)) {
					dynamicView.addMemberEntity("CRA", "CustRequestAttribute");
					dynamicView.addAlias("CRA", "attrName");
					dynamicView.addAlias("CRA", "attrValue");
					dynamicView.addViewLink("CR", "CRA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("custRequestId"));
					
					String locationCustomFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);
					conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, locationCustomFieldId),
								EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, srLocation)
							));
				}
				if(UtilValidate.isNotEmpty(custRequestDomainType)) {
					conditions.add(EntityCondition.makeCondition("custRequestDomainType", EntityOperator.EQUALS, custRequestDomainType));
				}
				
				//conditions.add(EntityCondition.makeCondition("crpPartyId", EntityOperator.EQUALS, userLoginPartyId));
				List<String> notOpenStatusList = UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_PENDING","SR_WRK_COMPL");
				/*
				 * if("clientPortal".equals(clientPortal)) notOpenStatusList =
				 * UtilMisc.toList("SR_CLOSED","SR_CANCELLED");
				 */
				if("sr-open".equals(filterBy)) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, notOpenStatusList));
				} else if("my-company-request".equals(filterBy)) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, notOpenStatusList));
				} else if("sr-inprogress".equals(filterBy)) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_TO_BE_SCHLD","SR_CALL_TO_SCHLD")));
				} else if("sr-pending".equals(filterBy)) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_OPEN")));
				} else if("sr-cancelled".equals(filterBy)) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_CLOSED","SR_CANCELLED")));
					LocalDateTime lastMonthStart = LocalDateTime.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
					LocalDateTime lastMonthEnd = LocalDateTime.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
					conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("closedByDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(lastMonthStart))),
							EntityCondition.makeCondition("closedByDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(Timestamp.valueOf(lastMonthEnd)))
							));
					
					
				} else if("sr-at-risk".equals(filterBy)) {
					dynamicView.addMemberEntity("CRWE", "CustRequestWorkEffort");
					dynamicView.addAlias("CRWE", "workEffortId");
					dynamicView.addViewLink("CR", "CRWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
					
					dynamicView.addMemberEntity("WE", "WorkEffort");
					dynamicView.addAlias("WE", "weLastModifiedDate","lastModifiedDate",null,Boolean.FALSE,Boolean.FALSE,null);
					dynamicView.addViewLink("CRWE", "WE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					
					dynamicView.addMemberEntity("CRC", "CustRequestContent");
					dynamicView.addAlias("CRC", "contentLastUpdated","lastUpdatedTxStamp",null,Boolean.FALSE,Boolean.FALSE,null);
					dynamicView.addViewLink("CR", "CRC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
					
					dynamicView.addMemberEntity("CRN", "CustRequestNote");
					dynamicView.addAlias("CRN", "noteLastUpdated","lastUpdatedTxStamp",null,Boolean.FALSE,Boolean.FALSE,null);
					dynamicView.addViewLink("CR", "CRN", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
					
					
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_WRK_COMPL")));
					LocalDateTime last_3_week = LocalDateTime.now().minusWeeks(3);
					/*
					conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition( "lastUpdatedTxStamp", EntityOperator.LESS_THAN, UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week))),
							EntityCondition.makeCondition("lastModifiedDate", EntityOperator.LESS_THAN, UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week))),
							EntityCondition.makeCondition("weLastModifiedDate", EntityOperator.LESS_THAN, UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week))),
							EntityCondition.makeCondition("contentLastUpdated", EntityOperator.LESS_THAN, UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week))),
							EntityCondition.makeCondition("noteLastUpdated", EntityOperator.LESS_THAN, UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week)))
							));
					*/
					conditions.add(EntityWhereString.makeConditionWhere("(IFNULL(CR.LAST_UPDATED_TX_STAMP, '"+UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week.minusDays(1)))+"') < '"+UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week))+"'"
							+ " OR IFNULL(CR.LAST_MODIFIED_DATE, IFNULL(CR.LAST_UPDATED_TX_STAMP, '"+UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week.minusDays(1)))+"')) < '"+UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week))+"')"
							+ " AND IFNULL(WE.LAST_MODIFIED_DATE, '"+UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week.minusDays(1)))+"') < '"+UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week))+"'"
							+ " AND IFNULL(CRC.LAST_UPDATED_TX_STAMP, '"+UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week.minusDays(1)))+"') < '"+UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week))+"'"
							+ " AND IFNULL(CRN.LAST_UPDATED_TX_STAMP, '"+UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week.minusDays(1)))+"') < '"+UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week))+"'"
							));
					
					
				} else if("sr-over-due".equals(filterBy)) {
					LocalDateTime startOfLastWeek = LocalDateTime.now().minusWeeks(1).with(DayOfWeek.MONDAY);
					LocalDateTime endOfLastWeek = startOfLastWeek.plusDays(6);
					
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_CLOSED", "SR_CANCELLED", "SR_WRK_COMPL")));
					conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("closedByDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(startOfLastWeek))),
							EntityCondition.makeCondition("closedByDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(Timestamp.valueOf(endOfLastWeek)))
							));
				} else if("sr-high-priority".equals(filterBy)) {
					String srHighPriority = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_HIGH_PRIORITY");
					dynamicView.addMemberEntity("EN", "Enumeration");
					dynamicView.addAlias("EN", "enumId");
					dynamicView.addAlias("EN", "enumCode");
					dynamicView.addAlias("EN", "enumTypeId");
					dynamicView.addAlias("EN", "description");
					dynamicView.addViewLink("CR", "EN", Boolean.FALSE, ModelKeyMap.makeKeyMapList("priority","enumId"));
					
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_WRK_COMPL")));
					conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PRIORITY_LEVEL"),
								EntityCondition.makeCondition(EntityOperator.OR,
										EntityCondition.makeCondition("enumId", EntityOperator.EQUALS, srHighPriority),
										EntityCondition.makeCondition("enumCode", EntityOperator.EQUALS, srHighPriority),
										EntityCondition.makeCondition("description", EntityOperator.EQUALS, srHighPriority)
								)
							));
				} else if("sent-tech-sched".equals(filterBy)) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_SNT_TO_SRTECH_FOR_SCHLD")));
				} else if("sr-scheduled".equals(filterBy)) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_ASSIGNED")));
				} else if("sr-closed".equals(filterBy)) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_CLOSED","SR_CANCELLED")));
				} else if("sr-feed-back".equals(filterBy)) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_PENDING")));
				}
				
				// Get Approval Statistics [start]
				String isEnabledApproval = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_APVL_ENABLE");
				if (UtilValidate.isEmpty(isEnabledApproval) || isEnabledApproval.equals("Y")) {
					if("pending-approvals".equals(filterBy) || "pending-reviews".equals(filterBy)) {
						List<String> pendingApprovals = new ArrayList<>();
						List<String> pendingReviews = new ArrayList<>();
						callCtxt = FastMap.newInstance();
						callResult = FastMap.newInstance();
						
						Map<String, Object> requestContext = FastMap.newInstance();
						requestContext.put("domainEntityType", DomainEntityType.SERVICE_REQUEST);
						requestContext.put("approvalCategoryId", "APVL_CAT_3PL_INV");
						if("pending-approvals".equals(filterBy)) {
							requestContext.put("decisionStatusId", "DECISION_NO");
						} else if("pending-reviews".equals(filterBy)) {
							requestContext.put("decisionStatusId", "DECISION_REVIEW");
						}
						
						if("my-sr".equals(filterType)) {
							requestContext.put("partyId", userLoginPartyId);
						} else if("my-teams-sr".equals(filterType)) {
							List<GenericValue> emplPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("partyId",userLoginPartyId).filterByDate().queryList();
							List<String> teams = UtilValidate.isNotEmpty(emplPositionFulfillments) ? EntityUtil.getFieldListFromEntityList(emplPositionFulfillments, "emplTeamId", true) : new ArrayList<>();
							requestContext.put("emplTeamIds", teams);
						}
						
						callCtxt.put("requestContext", requestContext);
						callCtxt.put("userLogin", userLogin);
						callResult = dispatcher.runSync("approval.getApprovalStatistics", callCtxt);
						if (ServiceUtil.isSuccess(callResult)) {
							conditions.clear();
							
							Map<String, Object> responseContext = (Map<String, Object>) callResult.get("responseContext");
							pendingApprovals = (List) responseContext.get("pendingApprovals");
							pendingReviews = (List) responseContext.get("pendingReviews");
							
							if("pending-approvals".equals(filterBy)) {
								List conditionList = FastList.newInstance();
								conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, pendingApprovals));
								EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
								List<GenericValue> approvalPendingList = EntityQuery.use(delegator).select("domainEntityId").from("WorkEffortApproval").where(mainConditons).distinct().queryList();
								pendingApprovals.clear();
								pendingApprovals = approvalPendingList.stream().map(x->x.getString("domainEntityId")).distinct().collect(Collectors.toList());
								conditions.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, pendingApprovals));
							} else if("pending-reviews".equals(filterBy)) {
								List conditionList = FastList.newInstance();
								conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, pendingReviews));
								EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
								List<GenericValue> approvalReviewList = EntityQuery.use(delegator).select("domainEntityId").from("WorkEffortApproval").where(mainConditons).distinct().queryList();
								pendingReviews.clear();
								pendingReviews = approvalReviewList.stream().map(x->x.getString("domainEntityId")).distinct().collect(Collectors.toList());
								conditions.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, pendingReviews));
							}
						}
					}
				}
				// Get Approval Statistics [end]
				
				//conditions.add(EntityCondition.makeCondition("primAttrName", EntityOperator.EQUALS, "PRIMARY"));
				
				if(UtilValidate.isNotEmpty(partyId)) {
					conditions.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId));
				}
				EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				
				Debug.logInfo("Dashboard condition : "+condition, MODULE);
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
	                        .from(dynamicView)
	                        .where(condition)
	                        .orderBy("commitDate ASC")
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
	            
	            if(UtilValidate.isNotEmpty(resultList)) {
	            	
	            	Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					Map<String, Object> srSourceList = EnumUtil.getEnumList(delegator, resultList, "custReqSrSource", "CASE_ORIGIN_CODE");
					Map<String, Object> priorityList = EnumUtil.getEnumList(delegator, resultList, "priority", "PRIORITY_LEVEL");
					Map<String, Object> statusList = StatusUtil.getStatusList(delegator, resultList, "statusId", "SR_STATUS_ID");
					
					Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, resultList, "responsiblePerson");
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, resultList, "createdByUserLogin");
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, resultList, "lastModifiedByUserLogin");
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, resultList, "closedByUserLogin");
					PartyHelper.getPartyNameByPartyIds(delegator, partyNames, resultList, "fromPartyId");
					
					Map<String, Object> srTypeNames = SrDataHelper.getSrTypeNames(delegator, resultList, "custRequestTypeId");
					Map<String, Object> srCategoryNames = SrDataHelper.getSrCategoryNames(delegator, resultList, "custRequestCategoryId");
					Map<String, Object> srSubCategoryNames = SrDataHelper.getSrCategoryNames(delegator, resultList, "custRequestSubCategoryId");
					
					Map<String, Object> businessUnitNames = CommonDataHelper.getBusinessUnitNames(delegator, resultList, "ownerBu");
					Map<String, Object> customFieldGroupNames = CommonDataHelper.getCustomFieldGroupNames(delegator, UtilMisc.toMap("groupType","SEGMENTATION"));
					
					Map<String, Object> storeNames = SrDataHelper.getProductStoreNames(delegator);
					
					Map<String, Object> workOrderSchList = SrDataHelper.getScheduledDate(delegator, resultList, "custRequestId");

					String locationCustomFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties",
							"location.customFieldId", delegator);
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);

					String soldByLocation = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SOLD_BY_LOCATION", "Sold By Location");
					String sblCustomFieldId = DataHelper.getCustomFieldId(delegator, "CUSTOMER_GRP", soldByLocation);
					
					String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
					String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
					// int count = 0;
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
		            
		            long start1 = System.currentTimeMillis();
	            	for(GenericValue serviceRequest : resultList) {
						
						Map<String, Object> data = new HashMap<String, Object>();
						
						String custRequestId = serviceRequest.getString("custRequestId");

						//Map<String, Object> assocPartyNames = SrDataHelper.getSrAssocPartyNames(delegator, custRequestId);
						Map<String, Map<String, Object>> assocPartys = SrDataHelper.getSrAssocPartys(delegator, custRequestId);
						String contractorPersonName = UtilValidate.isNotEmpty(assocPartys.get("CONTRACTOR")) ? (String) assocPartys.get("CONTRACTOR").get("partyName") : ""; 
						String salePersonName = UtilValidate.isNotEmpty(assocPartys.get("SALES_REP")) ? (String) assocPartys.get("SALES_REP").get("partyName") : ""; 
						String primaryContactName = UtilValidate.isNotEmpty(assocPartys.get("CONTACT")) ? (String) assocPartys.get("CONTACT").get("partyName") : ""; 
						String homeOwnerName = UtilValidate.isNotEmpty(assocPartys.get("CUSTOMER")) ? (String) assocPartys.get("CUSTOMER").get("partyName") : ""; 
						
						data.put("contractorPersonName", contractorPersonName);
						data.put("salePersonName", salePersonName);
						data.put("primaryPerson", org.groupfio.common.portal.util.DataUtil.getCustRequestAttribute(delegator, custRequestId, "PRIMARY"));
						data.put("srNumber", serviceRequest.getString("externalId"));
						data.put("sourceDocumentId",
								UtilValidate.isNotEmpty(serviceRequest.getString("custReqDocumentNum"))
										? serviceRequest.getString("custReqDocumentNum") : custRequestId);
						
						String status = serviceRequest.getString("statusId");
						data.put("srStatus", statusList.get(serviceRequest.getString("statusId")));

						String atRisk = "No";
						if (!UtilMisc.toList("SR_CLOSED", "SR_CANCELLED").contains(status)) {
							Timestamp dueDateTimeStamp = serviceRequest.getTimestamp("commitDate");
							Timestamp preEscalationTimeStamp = serviceRequest.getTimestamp("preEscalationDate");
							Timestamp now = UtilDateTime.nowTimestamp();
							if (UtilValidate.isNotEmpty(preEscalationTimeStamp)
									&& UtilValidate.isNotEmpty(dueDateTimeStamp) && now.after(preEscalationTimeStamp)
									&& now.before(dueDateTimeStamp)) {
								atRisk = "Yes";
							}
						}
						data.put("slaRisk", atRisk);

						String overDue = "No";
						if (!UtilMisc.toList("SR_CLOSED", "SR_CANCELLED").contains(status)) {
							Timestamp dueDateTimeStamp = serviceRequest.getTimestamp("commitDate");
							Timestamp now = UtilDateTime.nowTimestamp();
							if (UtilValidate.isNotEmpty(dueDateTimeStamp) && now.after(dueDateTimeStamp)) {
								overDue = "Yes";
							}
						}
						data.put("overDue", overDue);
						
						data.put("srSource", srSourceList.get(serviceRequest.getString("custReqSrSource")));
						data.put("ownerBU", businessUnitNames.get(serviceRequest.getString("ownerBu")));
						data.put("owner", serviceRequest.getString("responsiblePerson"));
						data.put("ownerName", partyNames.get(serviceRequest.getString("responsiblePerson")));

						String modifiedOn = "";
						if (UtilValidate.isNotEmpty(serviceRequest.getString("lastModifiedDate"))) {
							modifiedOn = org.fio.homeapps.util.UtilDateTime.timeStampToString(
									serviceRequest.getTimestamp("lastModifiedDate"), globalDateTimeFormat,
									TimeZone.getDefault(), null);
						}

						data.put("modifiedOn", modifiedOn);
						data.put("modifiedBy", serviceRequest.getString("lastModifiedByUserLogin"));
						data.put("modifiedByName", partyNames.get(serviceRequest.getString("lastModifiedByUserLogin")));
						
						String dateDue = "";
						if (UtilValidate.isNotEmpty(serviceRequest.getString("commitDate"))) {
							dateDue = org.fio.homeapps.util.UtilDateTime.timeStampToString(
									serviceRequest.getTimestamp("commitDate"), globalDateTimeFormat, TimeZone.getDefault(),
									null);
						}
						data.put("dueDate", dateDue);

						String closedByDate = "";
						if (UtilValidate.isNotEmpty(serviceRequest.getString("closedByDate"))) {
							closedByDate = org.fio.homeapps.util.UtilDateTime.timeStampToString(
									serviceRequest.getTimestamp("closedByDate"), globalDateTimeFormat,
									TimeZone.getDefault(), null);
						}

						data.put("dateClosed", closedByDate);
						data.put("closedBy", serviceRequest.getString("closedByUserLogin"));
						data.put("closedByName", partyNames.get(serviceRequest.getString("closedByUserLogin")));
						
						data.put("createdBy", serviceRequest.getString("createdByUserLogin"));
						data.put("createdByName", partyNames.get(serviceRequest.getString("createdByUserLogin")));
						String createdDate = "";
						if (UtilValidate.isNotEmpty(serviceRequest.getString("createdDate"))) {
							createdDate = org.fio.homeapps.util.UtilDateTime.timeStampToString(
									serviceRequest.getTimestamp("createdDate"), globalDateTimeFormat, TimeZone.getDefault(),
									null);
						}
						data.put("openDate", createdDate);
						data.put("createdOn", createdDate);
						
						String openDate = "";
						if (UtilValidate.isNotEmpty(serviceRequest.getString("openDateTime"))) {
							openDate = org.fio.homeapps.util.UtilDateTime.timeStampToString(
									serviceRequest.getTimestamp("openDateTime"), globalDateTimeFormat,
									TimeZone.getDefault(), null);
						}
						
						data.put("createdByFromIserve", "");
						data.put("custRequestId", custRequestId);
						data.put("partyId", serviceRequest.getString("fromPartyId"));
						data.put("orderId", serviceRequest.getString("custOrderId"));
						data.put("srName", serviceRequest.getString("custRequestName"));
						data.put("purchaseOrder", UtilValidate.isNotEmpty(serviceRequest.getString("purchaseOrder")) ? SrUtil.getSrOrderIds(delegator, serviceRequest.getString("purchaseOrder"), "ORIGINAL") : "");
						
						if (UtilValidate.isNotEmpty(serviceRequest.getString("custReqOnceDone"))) {
							if ("Y".equals(serviceRequest.getString("custReqOnceDone")))
								data.put("onceAndDone", "Yes");
							if ("N".equals(serviceRequest.getString("custReqOnceDone")))
								data.put("onceAndDone", "No");
						}
						
						data.put("srType", srTypeNames.get(serviceRequest.getString("custRequestTypeId")));
						data.put("srCategory", srCategoryNames.get(serviceRequest.getString("custRequestCategoryId")));
						data.put("srSubCategory", srSubCategoryNames.get(serviceRequest.getString("custRequestSubCategoryId")));
						data.put("srPriority", priorityList.get(serviceRequest.getString("priority")));
						
						data.put("customerName", partyNames.get(serviceRequest.getString("fromPartyId")));
						data.put("contractorEmail", serviceRequest.getString("contractorEmail"));
						
						data.put("primaryContactName", primaryContactName);
						data.put("homeOwnerName", homeOwnerName);
						
						data.put("activityOwnerName", org.fio.homeapps.util.DataUtil.getSrActivityOwnersName(delegator, custRequestId));

						data.put("homePhoneNumber", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("homePhoneNumber")));
						data.put("offPhoneNumber", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("offPhoneNumber")));
						data.put("mobileNumber", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("mobileNumber")));
						data.put("contractorOffPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("contractorOffPhone")));
						data.put("contractorMobilePhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("contractorMobilePhone")));
						data.put("contractorHomePhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("contractorHomePhone")));
						
						data.put("homeOwnerPartyId", UtilValidate.isNotEmpty(assocPartys.get("CUSTOMER")) ? (String) assocPartys.get("CUSTOMER").get("partyId") : "");
						data.put("contractorPartyId", UtilValidate.isNotEmpty(assocPartys.get("CONTRACTOR")) ? (String) assocPartys.get("CONTRACTOR").get("partyId") : "");
						data.put("salePersonPartyId", UtilValidate.isNotEmpty(assocPartys.get("SALES_REP")) ? (String) assocPartys.get("SALES_REP").get("partyId") : "");
						data.put("primaryContactPartyId", UtilValidate.isNotEmpty(assocPartys.get("CONTACT")) ? (String) assocPartys.get("CONTACT").get("partyId") : "");
						
						String locationId = SrUtil.getCustRequestAttrValue(delegator, locationCustomFieldId, custRequestId);
						if (UtilValidate.isNotEmpty(locationId)) {
							data.put("location", storeNames.get(locationId));
						}
						data.put("dealerRefNo", SrUtil.getCustRequestAttrValue(delegator, "DEALER_REF_NO", custRequestId));
						
						String soldByLocationId = SrUtil.getCustRequestAttrValue(delegator, sblCustomFieldId, custRequestId);
						if (UtilValidate.isNotEmpty(soldByLocationId)) {
							data.put("soldBy", storeNames.get(soldByLocationId));
						}
						
						String finishType = SrUtil.getCustRequestAttrValue(delegator, "FSR_FINISH_TYPE", custRequestId);
						if (UtilValidate.isNotEmpty(finishType)) {
							data.put("finishType", customFieldGroupNames.get(finishType));
						}
						
						data.put("domainEntityId", custRequestId);
						data.put("domainEntityType", DomainEntityType.SERVICE_REQUEST);
						data.put("domainEntityTypeDesc", org.groupfio.common.portal.util.DataHelper.convertToLabel( DomainEntityType.SERVICE_REQUEST ));
						data.put("externalLoginKey", externalLoginKey);	
						data.put("scheduledDate", workOrderSchList.get(custRequestId));
						dataList.add(data);
					}
	            	
	            	long end1 = System.currentTimeMillis();
	        		Debug.logInfo("timeElapsed for construction --->"+(end1-start1) / 1000f, MODULE);
	        		
	            	result.put("highIndex", Integer.valueOf(highIndex));
			        result.put("lowIndex", Integer.valueOf(lowIndex));
	            }
	            result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
				result.put("totalRecords", nf.format(resultListSize));
				result.put("recordCount", resultListSize);
				result.put("chunkSize", viewSize);   
			}
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}
	
	
	public static String getSrDashboardDataCountList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<GenericValue> resultList = null;
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		String filterBy = (String) context.get("filterBy");
		String filterType = (String) context.get("filterType");
		String srLocation = (String) context.get("srLocation");
		String partyId = (String) context.get("partyId");
		String clientPortal = (String) context.get("clientPortal");
		String custRequestDomainType = (String) context.get("custRequestDomainType");
		List<Map<String, Object>> dataList = new ArrayList<>();
		long start = System.currentTimeMillis();
		Timestamp systemTime = UtilDateTime.nowTimestamp();
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		try {
			String userLoginId = userLogin.getString("userLoginId");
			String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				
			String loggedUserRole = DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
			List loggedUserSecurityRoles = FastList.newInstance();
			String parentRoleId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
	    	if(UtilValidate.isNotEmpty(parentRoleId)) {
	    		List<GenericValue> partyRoleList = DataUtil.getPartyRoles(delegator, userLoginPartyId, parentRoleId);
	    		loggedUserSecurityRoles = EntityUtil.getFieldListFromEntityList(partyRoleList, "roleTypeId", true);
	    	}
	    	String loggedUserSecurityRoleStr = DataUtil.toList(loggedUserSecurityRoles,"");
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			List<String> statusIds = new ArrayList<>();
			
			if((UtilValidate.isNotEmpty(filterType) || UtilValidate.isNotEmpty(filterBy))  && UtilValidate.isNotEmpty(userLoginId)) {
				SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
				Connection con = (Connection)sqlProcessor.getConnection();
				CallableStatement cstmt = null;
				
				String barType = "";
				String count = "";
				srLocation = UtilValidate.isNotEmpty(srLocation) ? srLocation : "ALL";
				if("my-sr".equals(filterType)) {
					List<String> notOpenStatusList = UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_PENDING","SR_WRK_COMPL");
					if("clientPortal".equals(clientPortal)){
						//notOpenStatusList = UtilMisc.toList("SR_CLOSED","SR_CANCELLED");
						String sqlQuery = "";
						String schedSqlQuery = "";
						String myCompReqSqlQuery = "";	
						String sentToTechSchld = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SENT_TO_TECH_SCHLD", "SR_SNT_TO_SRTECH_FOR_SCHLD");
						
						List<String> technicianRoles = new ArrayList<>();
						String techRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "TECHNICIAN_ROLE", "TECHNICIAN");
						if(UtilValidate.isNotEmpty(techRole) && techRole.contains(",")) {
							technicianRoles = org.fio.admin.portal.util.DataUtil.stringToList(techRole, ",");
						} else if(UtilValidate.isNotEmpty(techRole)) {
							technicianRoles.add(techRole);
						}
						if(technicianRoles.contains(loggedUserRole)) {
							sqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'Open'"
									+ " FROM CUST_REQUEST CR "
									+ " LEFT OUTER JOIN CUST_REQUEST_SUPPLEMENTORY CRS ON CR.CUST_REQUEST_ID = CRS.CUST_REQUEST_ID"
									+ " INNER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON CR.CUST_REQUEST_ID = CRWE.CUST_REQUEST_ID"
									+ " INNER JOIN WORK_EFFORT WE ON CRWE.WORK_EFFORT_ID = WE.WORK_EFFORT_ID"
									+ " INNER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID"
									+ " WHERE WE.CURRENT_STATUS_ID NOT IN ('IA_MCOMPLETED', 'IA_COMPLETED') AND WEPA.PARTY_ID = '"+userLoginPartyId+"'"
									+ " AND WEPA.ROLE_TYPE_ID IN ("+loggedUserSecurityRoleStr+") AND (WEPA.THRU_DATE IS NULL OR WEPA.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
									+ " AND (WEPA.FROM_DATE IS NULL OR WEPA.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')"
									+ " AND CR.STATUS_ID NOT IN ("+DataUtil.toList(notOpenStatusList,"")+")";

							schedSqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'SentTechSched'"
									+ " FROM CUST_REQUEST CR "
									+ " LEFT OUTER JOIN CUST_REQUEST_SUPPLEMENTORY CRS ON CR.CUST_REQUEST_ID = CRS.CUST_REQUEST_ID"
									+ " INNER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON CR.CUST_REQUEST_ID = CRWE.CUST_REQUEST_ID"
									+ " INNER JOIN WORK_EFFORT WE ON CRWE.WORK_EFFORT_ID = WE.WORK_EFFORT_ID"
									+ " INNER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID"
									+ " WHERE WE.CURRENT_STATUS_ID NOT IN ('IA_MCOMPLETED', 'IA_COMPLETED') AND WEPA.PARTY_ID = '"+userLoginPartyId+"'"
									+ " AND WEPA.ROLE_TYPE_ID IN ("+loggedUserSecurityRoleStr+") AND (WEPA.THRU_DATE IS NULL OR WEPA.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
									+ " AND (WEPA.FROM_DATE IS NULL OR WEPA.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')"
									+ " AND CR.STATUS_ID IN ('"+sentToTechSchld+"')";
						} else {
							String isSuperiorContact = DataHelper.isSuperiorDealerContact(delegator, userLoginPartyId, "IS_SUPERIOR_DLR_CONTACT");

							if("Y".equals(isSuperiorContact)) {
								List<String> allDealerIds = DataHelper.getAllDealerByContact(delegator, userLoginPartyId);
								if(UtilValidate.isNotEmpty(allDealerIds)) {

									sqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'Open' FROM cust_request CR "
											+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
											+" WHERE CRP.party_id IN "+DataUtil.toList(allDealerIds,"")+" AND CRP.ROLE_TYPE_ID IN ('ACCOUNT')" 
											+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
											+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
											+" AND status_id NOT IN ("+DataUtil.toList(notOpenStatusList,"")+")";

									schedSqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'SentTechSched' FROM cust_request CR "
											+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
											+" WHERE CRP.party_id IN "+DataUtil.toList(allDealerIds,"")+" AND CRP.ROLE_TYPE_ID IN ('ACCOUNT')" 
											+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
											+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
											+" AND status_id IN ('"+sentToTechSchld+"')";
									
									myCompReqSqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'My_Company_Open' FROM cust_request CR "
											+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
											+" WHERE CRP.party_id IN ("+DataUtil.toList(allDealerIds,"")+") AND CRP.ROLE_TYPE_ID IN ('ACCOUNT')" 
											+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
											+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
											+" AND status_id NOT IN ("+DataUtil.toList(notOpenStatusList,"")+")";

								} else {

									sqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'Open' FROM cust_request CR "
											+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
											+" WHERE CRP.party_id='"+userLoginPartyId+"' AND CRP.ROLE_TYPE_ID IN ("+loggedUserSecurityRoleStr+") " 
											+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
											+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
											+" AND status_id NOT IN ("+DataUtil.toList(notOpenStatusList,"")+")";

									schedSqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'SentTechSched' FROM cust_request CR "
											+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
											+" WHERE CRP.party_id='"+userLoginPartyId+"' AND CRP.ROLE_TYPE_ID IN ("+loggedUserSecurityRoleStr+") " 
											+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
											+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
											+" AND status_id IN ('"+sentToTechSchld+"')";
									
									myCompReqSqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'My_Company_Open' FROM cust_request CR "
											+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
											+" WHERE CRP.party_id='"+userLoginPartyId+"' AND CRP.ROLE_TYPE_ID IN ("+loggedUserSecurityRoleStr+") " 
											+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
											+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
											+" AND status_id NOT IN ("+DataUtil.toList(notOpenStatusList,"")+")";
								}
							} else {
								
								sqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'Open' FROM cust_request CR "
										+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
										+" WHERE CRP.party_id='"+userLoginPartyId+"' AND CRP.ROLE_TYPE_ID IN ("+loggedUserSecurityRoleStr+") " 
										+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
										+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
										+" AND status_id NOT IN ("+DataUtil.toList(notOpenStatusList,"")+")";

								schedSqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'SentTechSched' FROM cust_request CR "
										+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
										+" WHERE CRP.party_id='"+userLoginPartyId+"' AND CRP.ROLE_TYPE_ID IN ("+loggedUserSecurityRoleStr+") " 
										+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
										+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
										+" AND status_id IN ('"+sentToTechSchld+"')";
								
								List<String> allDealerIds = DataHelper.getAllDealerByContact(delegator, userLoginPartyId);
								if(UtilValidate.isNotEmpty(allDealerIds)) {
									myCompReqSqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'My_Company_Open' FROM cust_request CR "
											+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
											+" WHERE CRP.party_id IN ("+DataUtil.toList(allDealerIds,"")+") AND CRP.ROLE_TYPE_ID IN ('ACCOUNT')" 
											+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
											+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
											+" AND status_id NOT IN ("+DataUtil.toList(notOpenStatusList,"")+")";
								}
								
							}
						}
						if (UtilValidate.isNotEmpty(sqlQuery) && UtilValidate.isNotEmpty(custRequestDomainType)) {
							sqlQuery = sqlQuery+ " AND CR.CUST_REQUEST_DOMAIN_TYPE = '"+custRequestDomainType+"' ";
						}	
						if (UtilValidate.isNotEmpty(schedSqlQuery) && UtilValidate.isNotEmpty(custRequestDomainType)) {
							schedSqlQuery = schedSqlQuery+ " AND CR.CUST_REQUEST_DOMAIN_TYPE = '"+custRequestDomainType+"' ";
						}	
						if (UtilValidate.isNotEmpty(myCompReqSqlQuery) && UtilValidate.isNotEmpty(custRequestDomainType)) {
							myCompReqSqlQuery = myCompReqSqlQuery+ " AND CR.CUST_REQUEST_DOMAIN_TYPE = '"+custRequestDomainType+"' ";
						}	
						rs = sqlProcessor.executeQuery(sqlQuery);
						rs1 = sqlProcessor.executeQuery(schedSqlQuery);
						if(UtilValidate.isNotEmpty(myCompReqSqlQuery)) {
							rs2 = sqlProcessor.executeQuery(myCompReqSqlQuery);
						}
					} else
						cstmt = con.prepareCall("{call kpi_user_level(?,?)}");
				} else if("my-teams-sr".equals(filterType)) {
					cstmt = con.prepareCall("{call kpi_team_level(?,?)}");
				} else if("my-bu-sr".equals(filterType)) {
					cstmt = con.prepareCall("{call kpi_bu_level(?,?)}");
				}
				if("clientPortal".equals(clientPortal)){
					if(rs !=null){
						try{ 
							int i = 0;
							while (rs.next()) {
								Map<String, Object> data = new HashMap<String, Object>();
								count = rs.getString(1);
								barType = rs.getString(2);
								data.put("barId", barType);
								data.put("count", count);
								dataList.add(data);
							}

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if(rs !=null)
								rs.close();
						}
					}
					if(rs1 !=null){
						try{ 
							int i = 0;
							while (rs1.next()) {
								Map<String, Object> data = new HashMap<String, Object>();
								count = rs1.getString(1);
								barType = rs1.getString(2);
								data.put("barId", barType);
								data.put("count", count);
								dataList.add(data);
							}

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if(rs1 !=null)
								rs1.close();
						}
					}
					if(rs2 !=null){
						try{ 
							int i = 0;
							while (rs2.next()) {
								Map<String, Object> data = new HashMap<String, Object>();
								count = rs2.getString(1);
								barType = rs2.getString(2);
								data.put("barId", barType);
								data.put("count", count);
								dataList.add(data);
							}

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if(rs2 !=null)
								rs2.close();
						}
					}
					
				} else{
					cstmt.setString(1, userLoginId);
					cstmt.setString(2, srLocation);
					rs = cstmt.executeQuery();
					
					if(rs !=null){
						try{ 
							int i = 0;
							while (rs.next()) {
								Map<String, Object> data = new HashMap<String, Object>();
								count = rs.getString(1);
								barType = rs.getString(2);
								data.put("barId", barType);
								data.put("count", count);
								dataList.add(data);
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					cstmt.close();
					
					// Get Approval Statistics [start]
					int pendingApprovalCount = 0;
					int pendingReviewCount = 0;
					List<String> pendingApprovals = new ArrayList<>();
					List<String> pendingReviews = new ArrayList<>();
					
					Map<String, Object> callCtxt = FastMap.newInstance();
					Map<String, Object> callResult = FastMap.newInstance();
					
					Map<String, Object> requestContext = FastMap.newInstance();
					
					requestContext.put("domainEntityType", DomainEntityType.SERVICE_REQUEST);
					requestContext.put("approvalCategoryId", "APVL_CAT_3PL_INV");
					
					if("my-sr".equals(filterType)) {
						requestContext.put("partyId", userLoginPartyId);
					} else if("my-teams-sr".equals(filterType)) {
						List<GenericValue> emplPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("partyId",userLoginPartyId).filterByDate().queryList();
						List<String> teams = UtilValidate.isNotEmpty(emplPositionFulfillments) ? EntityUtil.getFieldListFromEntityList(emplPositionFulfillments, "emplTeamId", true) : new ArrayList<>();
						requestContext.put("emplTeamIds", teams);
					}
					
					callCtxt.put("requestContext", requestContext);
					callCtxt.put("userLogin", userLogin);
					
					String isEnabledApproval = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_APVL_ENABLE");
					if (UtilValidate.isEmpty(isEnabledApproval) || isEnabledApproval.equals("Y")) {
						callResult = dispatcher.runSync("approval.getApprovalStatistics", callCtxt);
						if (ServiceUtil.isSuccess(callResult)) {
							Map<String, Object> responseContext = (Map<String, Object>) callResult.get("responseContext");
							pendingApprovalCount = (int) responseContext.get("pendingApprovalCount");
							pendingReviewCount = (int) responseContext.get("pendingReviewCount");
							pendingApprovals = (List) responseContext.get("pendingApprovals");
							pendingReviews = (List) responseContext.get("pendingReviews");
							
							List conditionList = FastList.newInstance();
							conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, pendingApprovals));
							EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							List<GenericValue> approvalPendingList = EntityQuery.use(delegator).select("domainEntityId").from("WorkEffortApproval").where(mainConditons).distinct().queryList();
							pendingApprovalCount = approvalPendingList.size();
							
							conditionList = FastList.newInstance();
							conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, pendingReviews));
							mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							List<GenericValue> approvalReviewList = EntityQuery.use(delegator).select("domainEntityId").from("WorkEffortApproval").where(mainConditons).distinct().queryList();
							pendingReviewCount = approvalReviewList.size();
						}
					}
					
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("barId", "pending_approvals");
					data.put("count", pendingApprovalCount);
					dataList.add(data);
					
					data = new HashMap<String, Object>();
					data.put("barId", "pending_reviews");
					data.put("count", pendingReviewCount);
					dataList.add(data);
					// Get Approval Statistics [end]
				}
			}
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}

}
