package org.fio.sr.portal.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
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

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.fio.admin.portal.event.CommonEvents;
import org.fio.admin.portal.util.DataUtil;
import org.fio.admin.portal.util.ResAvailUtil;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.constants.GlobalConstants.DateTimeTypeConstant;
import org.fio.homeapps.util.CommonDataHelper;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.ResponseUtils;
import org.fio.homeapps.util.SrDataHelper;
import org.fio.homeapps.util.StatusUtil;
import org.fio.sr.portal.DataHelper;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.groupfio.common.portal.invoice.InvoiceWorker;
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
import org.groupfio.common.portal.util.SrUtil;
import org.groupfio.common.portal.util.UtilAttribute;
import org.groupfio.crm.service.resolver.Resolver;
import org.groupfio.crm.service.resolver.ResolverFactory;
import org.groupfio.crm.service.resolver.SlaTatResolver;
import org.groupfio.crm.service.resolver.ResolverConstants.ResolverType;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
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
 * @author Mahendran
 * @author Sharif
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
			outMap = dispatcher.runSync("srPortal.getServiceHomeData", inMap);
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
			outMap = dispatcher.runSync("srPortal.getCustRequestSrSummary", inMap);
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
			outMap = dispatcher.runSync("srPortal.getActivityHomeData", inMap);
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
			outMap = dispatcher.runSync("srPortal.UpdateReasignActivity", inMap);
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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.getSrCategory", inMap);

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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.getSrSubCategory", inMap);

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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.getSrOverDueSummary", inMap);

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
			outMap = dispatcher.runSync("srPortal.loadTemplate", inMap);
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
				Map<String, Object> outMap = dispatcher.runSync("srPortal.getContactDetails", inMap);

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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.pwebRelatedDetailsResult", inMap);

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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.getNotesAttachments", inMap);

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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.getActivityData", inMap);

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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.getSrActivityData", inMap);

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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.getServiceDetails", inputMap);

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
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String noteId = (String) context.get("noteId");
		Map<String,Object> inMap = FastMap.newInstance();
		String externalLoginKey = (String) context.get("externalLoginKey");
		try {
			if(UtilValidate.isNotEmpty(noteId)) {
				inMap.put("noteId", noteId);
				}
			if(UtilValidate.isNotEmpty(userLogin)) {
				inMap.put("userLogin", userLogin);
				}
			inMap.put("externalLoginKey", externalLoginKey);
			Map<String, Object> outMap = dispatcher.runSync("srPortal.getNoteData", inMap);

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
	public static String getLocationByStateAndCounty(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(), delegator);
		String state = request.getParameter("state");
		String county = request.getParameter("county");
		Map<String,Object> inMap = FastMap.newInstance();
		try {
			if(UtilValidate.isNotEmpty(state) && UtilValidate.isNotEmpty(county)) {
				GenericValue prodStoreTechAssoc = EntityQuery.use(delegator).from("ProductStoreTechAssoc").where("state", state,"county",county).queryOne();
				if(UtilValidate.isNotEmpty(prodStoreTechAssoc)){				
					inMap.put("productStoreId", prodStoreTechAssoc.getString("productStoreId"));
					inMap.put("productStoreName", prodStoreTechAssoc.getString("productStoreName"));
				}
				//inMap.put("productStores", prodStoreList);
				results.add(inMap);
			}
		}catch (Exception e) {
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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.getCustomerCommunicationInfo", inMap);

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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.saveSrReview", inMap);

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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.findSRCustomers", inMap);
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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.reassignSr", inputMap);
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
		String custRequestId = (String) context.get("custRequestId");
		List<String> externalIds = new ArrayList<>();
		String srStatusId = (String) context.get("srStatusId1");
		String srSubStatus = (String) context.get("srSubStatus");
		String statusId = (String) context.get("statusId");
		String subStatusId = (String) context.get("subStatusId");
		String description = (String) context.get("description");
		String resolutionFlag="resolution";
		String resolveActivityflag="closeActivity";
		String resolvesuccessflag="resolveSuccess";
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> result = new HashMap<>();
		List<GenericValue> workEffortIdList= new LinkedList<>();

		if(UtilValidate.isEmpty(resolution)){
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			result.put("resolutionFlag", resolutionFlag);
			results.add(result);
			return doJSONResponse(response,results);
		}
		
		GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryFirst();
		srStatusId = UtilValidate.isNotEmpty(custRequest) && UtilValidate.isEmpty(srStatusId) ? custRequest.getString("statusId") : "";

		List<GenericValue> custRequestWorkEffort = EntityQuery.use(delegator).from("CustRequestWorkEffort").where("custRequestId", custRequestId).orderBy("-createdStamp").queryList();
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
			if(UtilValidate.isNotEmpty(custRequestId)){
				inputMap.put("custRequestId", custRequestId);
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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.resolveServiceRequest", inputMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Resolving Service Request");
				return "error";
			}else{
				result.put("resolvesuccessflag", resolvesuccessflag);
				results.add(result);
				results=(List)outMap.get("results");

				List<String> optionalAttendees = null;
				String optionalAttendeesClassName = "";
				String optionalAttendeesEmailIds = "";

				if (UtilValidate.isNotEmpty(context.get("optionalAttendees"))) {
					optionalAttendeesClassName = context.get("optionalAttendees").getClass().getName();
				}

				if (UtilValidate.isNotEmpty(optionalAttendeesClassName)
						&& "java.lang.String".equals(optionalAttendeesClassName)) {
					optionalAttendees = UtilMisc.toList((String) context.get("optionalAttendees"));
				} else if (UtilValidate.isNotEmpty(optionalAttendeesClassName)
						&& "java.util.LinkedList".equals(optionalAttendeesClassName)) {
					optionalAttendees = (List<String>) context.get("optionalAttendees");
				}

				if (UtilValidate.isNotEmpty(optionalAttendees)) {
					for (String eachOptionalAttendee : optionalAttendees) {
						Map<String, String> optionalAttendeeContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, eachOptionalAttendee);
						if (UtilValidate.isNotEmpty(optionalAttendeeContactInformation) && UtilValidate.isNotEmpty(optionalAttendeeContactInformation.get("EmailAddress"))){
							String optionalAttendeeEmailAddress = optionalAttendeeContactInformation.get("EmailAddress");
							optionalAttendeesEmailIds = optionalAttendeesEmailIds+optionalAttendeeEmailAddress+",";
						}
					}
					if (UtilValidate.isNotEmpty(optionalAttendeesEmailIds)) {
						optionalAttendeesEmailIds = optionalAttendeesEmailIds.substring(0, optionalAttendeesEmailIds.length()-1);
					}
				}

				String cNo = (String) context.get("cNo");
				String owner = (String) context.get("owner");
				String custRequestName = (String) context.get("srName");
				String primary = (String) context.get("primary");
				String fromPartyId = (String) context.get("fromPartyId");
				Debug.log("== UPDATE SR owner =="+owner);

				if(UtilValidate.isNotEmpty(cNo)){
					fromPartyId = cNo;
				}

				String nsender = "";
				String nto = "";
				String ccAddresses = "";
				String ccAdd ="";
				String subject = "";
				String ownerDesc = "";
				String partyDesc = "";
				String srStatusDesc = "";
				String signName = "";
				String enableEmailTrigger = "N";
				String subjectDesc = "An update has been made to the support SR:";

				if (UtilValidate.isNotEmpty(owner) /*&& UtilValidate.isNotEmpty(previousOwnerId) && !previousOwnerId.equals(owner)*/) {
					enableEmailTrigger = "Y";
					if (UtilValidate.isEmpty(cNo) && UtilValidate.isNotEmpty(context.get("selFromPartyId"))){
						cNo = (String) context.get("selFromPartyId");
					}
					if (UtilValidate.isEmpty(description) && UtilValidate.isNotEmpty(context.get("selDescription"))){
						description = (String) context.get("selDescription");
					}
					partyDesc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, cNo, false);
					ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, owner, false);
					srStatusDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId);


					nsender = (String) context.get("fromEmailId");

					GenericValue userLoginPerson = EntityUtil.getFirst(EntityQuery.use(delegator).select("userLoginId","partyId","firstName").from("UserLoginPerson").where("userLoginId", owner).queryList());

					Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLoginPerson.getString("partyId"));
					nto = ntoContactInformation.get("EmailAddress");

					if(UtilValidate.isNotEmpty(userLoginPerson)) {
						Map<String, String> backupCoordinatorEmail = SrUtil.getBackupCoordinatorInfo(delegator, userLoginPerson.getString("partyId"));
						if(UtilValidate.isNotEmpty(backupCoordinatorEmail)) {
							ccAdd = backupCoordinatorEmail.get("EmailAddress");
						}
					}
					
					//Map<String, String> previousOwnerContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,previousOwnerId);
					List<String> srAssocPartiesEmailIds = SrUtil.getSrAssocPartyEmailIds(delegator, custRequestId, statusId, primary);
					if(UtilValidate.isNotEmpty(srAssocPartiesEmailIds)){
						for(String eachAssocEmailId :srAssocPartiesEmailIds ){
							ccAddresses = ccAddresses+eachAssocEmailId+",";
						}
						ccAddresses = ccAddresses.substring(0,ccAddresses.length()-1);
						if(UtilValidate.isNotEmpty(optionalAttendeesEmailIds)){
							ccAddresses = ccAddresses+","+optionalAttendeesEmailIds;
						}
					}else{
						ccAddresses = optionalAttendeesEmailIds;
					}

					signName = "CRM Administrator.";
					GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);

					if(UtilValidate.isNotEmpty(personGv)){
						signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
					}
				}

				Debug.log("==== UPDATE SR nsender ===="+nsender);
				Debug.log("==== UPDATE SR nto ===="+nto);
				Debug.log("==== UPDATE SR enableEmailTrigger ===="+enableEmailTrigger);
				Debug.log("==== UPDATE SR ccAddresses ===="+ccAddresses);

				if (UtilValidate.isNotEmpty(nsender) && UtilValidate.isNotEmpty(nto) && "Y".equals(enableEmailTrigger)){

					Map<String, Object> srStatusesMap = org.fio.homeapps.util.DataUtil.getSrStatusList(delegator, "SR_STATUS_ID");

					//String srStatusId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_EMAIL_NOTIFICATION_STAGE");

					if(UtilValidate.isNotEmpty(srStatusId) && UtilValidate.isNotEmpty(statusId)){
						int globalSrStatusSeq = Integer.parseInt((String)srStatusesMap.get(srStatusId));

						int srStatusSeqParam = Integer.parseInt((String)srStatusesMap.get(statusId));

						if(UtilValidate.isNotEmpty(globalSrStatusSeq) && UtilValidate.isNotEmpty(srStatusSeqParam) && srStatusSeqParam >= globalSrStatusSeq) {

							String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_TEMPLATE");
							if(UtilValidate.isNotEmpty(templateId)) {
								GenericValue emailTemlateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",templateId), false);
								String emailContent = "";
								//String templateFormContent = emailTemlateData.getString("textContent");
								String templateFormContent = emailTemlateData.getString("templateFormContent");
								if (UtilValidate.isNotEmpty(templateFormContent)) {
									if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
										templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
									}
								}
								if(UtilValidate.isNotEmpty(emailTemlateData.getString("subject")))
									subject = emailTemlateData.getString("subject");

								// prepare email content [start]
								Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
								extractContext.put("delegator", delegator);
								extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
								extractContext.put("fromEmail", nsender);
								extractContext.put("toEmail", nto);
								extractContext.put("partyId", fromPartyId);
								extractContext.put("custRequestId", custRequestId);
								extractContext.put("emailContent", templateFormContent);
								extractContext.put("templateId", templateId);

								Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
								emailContent = (String) extractResultContext.get("emailContent");

								Map<String, Object> callCtxt = FastMap.newInstance();
								Map<String, Object> callResult = FastMap.newInstance();
								Map<String, Object> requestContext = FastMap.newInstance();

								requestContext.put("nsender", nsender);
								requestContext.put("nto", nto);
								requestContext.put("subject", subject);
								requestContext.put("emailContent", emailContent);
								requestContext.put("templateId", templateId);
								requestContext.put("ccAddresses", ccAdd);
								requestContext.put("nbcc", ccAddresses);

								callCtxt.put("requestContext", requestContext);
								callCtxt.put("userLogin", userLogin);

								Debug.log("==== Update SR sendEmail ===="+callCtxt);

								callResult = dispatcher.runSync("common.sendEmail", callCtxt);
								if (ServiceUtil.isError(callResult)) {
									String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
									request.setAttribute("_ERROR_MESSAGE_", errMsg);
									return "error";
								}
								
								
								//Send survey email to the primary person of the SR
								String surveyTemplateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_SURVEY_TEMPLATE");
								if(UtilValidate.isNotEmpty(surveyTemplateId) && "SR_CLOSED".equals(statusId)) {
									GenericValue surveyEmailTemlateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",surveyTemplateId), false);
									String surveyEmailContent = "";
									String primaryPartyId = org.groupfio.common.portal.util.DataHelper.getPrimaryPerson(delegator, custRequestId);

									Map<String, String> ntoContactInformation1 = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,primaryPartyId);
									nto = UtilValidate.isNotEmpty(ntoContactInformation1) ? ntoContactInformation1.get("EmailAddress") : "";
									
									String surveyTemplateFormContent = surveyEmailTemlateData.getString("templateFormContent");
									if (UtilValidate.isNotEmpty(surveyTemplateFormContent)) {
										if (org.apache.commons.codec.binary.Base64.isBase64(surveyTemplateFormContent)) {
											surveyTemplateFormContent = org.ofbiz.base.util.Base64.base64Decode(surveyTemplateFormContent);
										}
									}
									if(UtilValidate.isNotEmpty(surveyEmailTemlateData.getString("subject"))) {
										subject = "FSR# "+custRequestId+" ("+custRequestName+") - "+surveyEmailTemlateData.getString("subject");
									}

									// prepare email content [start]
									extractContext = new LinkedHashMap<String, Object>();
									extractContext.put("delegator", delegator);
									extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
									extractContext.put("fromEmail", nsender);
									extractContext.put("toEmail", nto);
									extractContext.put("partyId", fromPartyId);
									extractContext.put("custRequestId", custRequestId);
									extractContext.put("emailContent", surveyTemplateFormContent);
									extractContext.put("templateId", surveyTemplateId);

									extractResultContext = ExtractFacade.extractData(extractContext);
									surveyEmailContent = (String) extractResultContext.get("emailContent");

									callCtxt = FastMap.newInstance();
									callResult = FastMap.newInstance();
									requestContext = FastMap.newInstance();

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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.saveServiceRequest", inputMap);
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
						
						Map<String, String> primaryContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId);
						
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
			Map<String, Object> outMap = dispatcher.runSync("srPortal.viewServiceActivityDetails", inputMap);
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
		Timestamp curentTime = UtilDateTime.nowTimestamp();
		try {
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("workEffortId");
			GenericValue updateConfigRecords = EntityUtil.getFirst(
					delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), null, false));
			if (UtilValidate.isNotEmpty(updateConfigRecords)) {
				updateConfigRecords.put("workEffortId", workEffortId);
				updateConfigRecords.put("currentStatusId", "IA_MCOMPLETED");
				updateConfigRecords.put("lastModifiedDate", curentTime);
        		updateConfigRecords.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
				updateConfigRecords.put("closedDateTime", curentTime);
        		updateConfigRecords.put("closedByUserLogin", userLogin.getString("userLoginId"));
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
		String actualResolution = request.getParameter("actualResolution");
		String coordinatorDesc = request.getParameter("coordinatorDesc");
		
		try {
			GenericValue userLogin=getUserLogin(request);
    		String userLoginId = userLogin.getString("userLoginId");
    		
			GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryFirst();
			if (UtilValidate.isNotEmpty(custRequest)) {
				custRequest.put("description", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
				custRequest.put("resolution", Base64.getEncoder().encodeToString(resolution.getBytes("utf-8")));
				if(UtilValidate.isNotEmpty(actualResolution))
					custRequest.put("actualResolution",Base64.getEncoder().encodeToString(actualResolution.getBytes("utf-8")));
				custRequest.put("lastModifiedDate", UtilDateTime.nowTimestamp());
				custRequest.put("lastModifiedByUserLogin", userLoginId);
				custRequest.store();
				
				GenericValue supplementory = EntityQuery.use(delegator).from("CustRequestSupplementory").where("custRequestId", custRequestId).queryFirst();
				if (UtilValidate.isNotEmpty(supplementory)) {
					supplementory.put("descriptionRawTxt", description);
					supplementory.put("resolutionRawTxt", resolution);
					supplementory.store();
				}
				if (UtilValidate.isNotEmpty(coordinatorDesc)) {
					coordinatorDesc = Base64.getEncoder().encodeToString(coordinatorDesc.getBytes("utf-8"));
					GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","CSR_DESC"), false);
					if(UtilValidate.isNotEmpty(custAttGv)){
						custAttGv.put("attrValue", coordinatorDesc);
						custAttGv.store();
					}else{
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "CSR_DESC");
						custRequestAttrbute.set("attrValue", coordinatorDesc);
						custRequestAttrbute.create();
					}
				}
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
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		List<String> results = new ArrayList<>();
		String custRequestId = request.getParameter("srNumber");
		String statusId = request.getParameter("srStatusId");
		String resolution = request.getParameter("resolution");
		String description = request.getParameter("description");
		String owner = request.getParameter("owner");

		try {
			GenericValue userLogin=getUserLogin(request);
			String userLoginId = userLogin.getString("userLoginId");
			
			String accessLevel = "Y";
			String businessUnit = null;
			String teamId = null;
			String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
			Map<String, Object> custReqData = org.fio.homeapps.util.DataUtil.getCustRequestDetail(delegator, custRequestId);
			if(UtilValidate.isNotEmpty(custReqData)) {
				businessUnit = UtilValidate.isNotEmpty(custReqData.get("businessUnit")) ? (String) custReqData.get("businessUnit") : "";
				teamId = UtilValidate.isNotEmpty(custReqData.get("teamId")) ? (String) custReqData.get("teamId") : "";
			}
			
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId)) {
				if(UtilValidate.isEmpty(businessUnit))
					businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("teamId", teamId);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "Update");
				accessMatrixMap.put("entityName", "CustRequest");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
				//validate the common team and access for the assignment
				String currentPartyId = userLoginPartyId;
				if(UtilValidate.isEmpty(owner)) {
					owner = teamId;
				} else {
					currentPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, owner);
					Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, currentPartyId);
					businessUnit = (String) buTeamData.get("businessUnit");
					teamId = (String) buTeamData.get("emplTeamId");
				}
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				//change the access in the create 
				//check with ownerId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					if(!ownerIds.contains(teamId)) accessLevel = null;
					conditionList.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.IN, ownerIds));
					//custRequestContext.put("ownerIds", ownerIds);
				}
				
				//check with emplTeamId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					if(!emplTeamIds.contains(teamId)) accessLevel = null;
					conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
					//custRequestContext.put("emplTeamIds", emplTeamIds);
				}

				if(UtilValidate.isNotEmpty(custRequestId)){
					conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				}

				EntityCondition mainCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				GenericValue custRequest1 = EntityUtil
						.getFirst(delegator.findList("CustRequest", mainCondition, null, null, null, false));
				
				if(UtilValidate.isEmpty(custRequest1)) accessLevel=null;
			}
			
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryFirst();
				String allowToCloseSR = allowToCloseSR(delegator, userLogin.getString("partyId"), custRequestId,statusId);
				Debug.log("allowToCloseSR======="+allowToCloseSR);
				if("N".equals(allowToCloseSR)){
					Debug.log("allowToCloseSR==inside====="+allowToCloseSR);
					request.setAttribute("_ERROR_MESSAGE_", "You are not allowed to close SR's");				
					Map<String, Object> errorData = FastMap.newInstance();
					errorData.put("errMsg", "You are not allowed to close SR's");	
					results.add("errMsg");
					return doJSONResponse(response, results);	

				}
				List<String> srScheduledStatusesList = SrUtil.getSrScheduledStatusList(delegator, custRequestId);
				if(UtilValidate.isNotEmpty(srScheduledStatusesList) && srScheduledStatusesList.contains(statusId)){
					String allowToUpdateStatusToSchedule = org.fio.sr.portal.event.AjaxEvents.allowToUpdateSrStatusToSchedule(delegator, srScheduledStatusesList, custRequestId);
					if(UtilValidate.isNotEmpty(allowToUpdateStatusToSchedule) && "N".equals(allowToUpdateStatusToSchedule)){
						request.setAttribute("_ERROR_MESSAGE_", "Please Schedule Activities before changing the SR Status to Scheduled");				
						Map<String, Object> errorData = FastMap.newInstance();
						errorData.put("errSchMsg", "Please Schedule Activities before changing the SR Status to Scheduled");	
						results.add("errSchMsg");
						return doJSONResponse(response, results);
					}
				}
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
								conditionlist1.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_IN, UtilMisc.toList("IA_COMPLETED", "IA_MCOMPLETED","IA_CANCEL")));

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

						String result = org.fio.sr.portal.event.CommonEvents.updateServiceRequestEvent(request, response);

						if(UtilValidate.isNotEmpty(result) && "success".equals(result)) {
							request.setAttribute("custRequestId", custRequestId);
							request.setAttribute("_EVENT_MESSAGE_", "SR Status Updated Successfully"+": "+custRequestId);
						} else {
							request.setAttribute("_ERROR_MESSAGE_", "Problem While Updating Service Request");
							return "error";
						}
						
					}


				}
			}else {
				String errMsg = "";
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errMsg = accessMatrixRes.get("errorMessage").toString();
				} else {
					errMsg = "Access Denied";
				}
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				Map<String, Object> errorData = FastMap.newInstance();
				errorData.put("errAccessMsg", "Access Denied");	
				results.add("errAccessMsg");
				return doJSONResponse(response, results);
			}


		} catch (Exception e) {
			Debug.logError(e, "Problem While Updating SR Status : " + e.getMessage(), MODULE);
			request.setAttribute("_ERROR_MESSAGE_", "Problem While Updating SR Status");
			return "error";
		}
		return doJSONResponse(response, results);
	}

	public static String allowToCloseSR(Delegator delegator, String userLoginParty, String custRequestId, String statusId)
			throws GenericEntityException {
		GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryFirst();
		String allowToCloseSR = "Y";
		String srClosedRolesList = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_CLOSED_ROLES");
		if(UtilValidate.isNotEmpty(srClosedRolesList) && "SR_CLOSED".equals(statusId)) {
			List<String> srClosedRoles = new ArrayList<>();
			if(UtilValidate.isNotEmpty(srClosedRolesList) && srClosedRolesList.contains(",")) {
				srClosedRoles = org.fio.admin.portal.util.DataUtil.stringToList(srClosedRolesList, ",");
			}else{
				srClosedRoles.add(srClosedRolesList);
			}
			Debug.log("srClosedRoles======"+srClosedRoles);
			String srClosedTypesList = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_CLOSED_TYPES");
			Debug.log("srClosedTypesList======="+srClosedTypesList);
			if(UtilValidate.isNotEmpty(srClosedTypesList)) {
				List<String> srClosedTypes = new ArrayList<>();
				if(UtilValidate.isNotEmpty(srClosedTypesList) && srClosedTypesList.contains(",")) {
					srClosedTypes = org.fio.admin.portal.util.DataUtil.stringToList(srClosedTypesList, ",");
				}else{
					srClosedTypes.add(srClosedTypesList);
				}
				if(UtilValidate.isNotEmpty(srClosedRoles)){
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, srClosedRoles));
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginParty));
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					List<GenericValue> partyRoles = delegator.findList("PartyRole", mainConditons, null, null, null, false);
					Debug.log("partyRoles======"+partyRoles);
					Debug.log("SR type======"+custRequest.getString("custRequestTypeId"));
					if(UtilValidate.isEmpty(partyRoles) && srClosedTypes.contains(custRequest.getString("custRequestTypeId"))){
						allowToCloseSR="N";
					}
				}
				
			}
			
		}
		
		return allowToCloseSR;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	 public static String redirectSRParty(HttpServletRequest request, HttpServletResponse response) throws GenericServiceException {

		 Delegator delegator = (Delegator) request.getAttribute("delegator");
		 HttpSession session = request.getSession();
		 GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		 String requestUri = request.getParameter("requestUri");
		 Map<String, Object> context = UtilHttp.getCombinedMap(request);

		 String srNumber = (String) context.get("srNumber");
		 String partyId = (String) context.get("partyId");
		 String externalKey = (String) context.get("externalLoginKey");

		 List<Map<String, Object>> dataList = new ArrayList<>();
		 String redirectUrl = null;

		 try {		

				 if (UtilValidate.isNotEmpty(srNumber) &&  UtilValidate.isNotEmpty(partyId)) {
					 GenericValue partyDetails = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryOne();
					 if (UtilValidate.isNotEmpty(partyDetails)) {
						 String roleTypeId = partyDetails.getString("roleTypeId");
						 if(UtilValidate.isNotEmpty(roleTypeId) && roleTypeId.equals("ACCOUNT")){
							 redirectUrl = "/account-portal/control/viewAccount?partyId="+partyId+"&externalLoginKey="+externalKey;
							 // redirect
							 Debug.logInfo("redirectUrl = " + redirectUrl, MODULE);
							 response.sendRedirect(redirectUrl);
						 }else if(UtilValidate.isNotEmpty(roleTypeId) && roleTypeId.equals("LEAD")){
							 redirectUrl = "/lead-portal/control/viewLead?partyId="+partyId+"&externalLoginKey="+externalKey;
							 // redirect
							 Debug.logInfo("redirectUrl = " + redirectUrl, MODULE);
							 response.sendRedirect(redirectUrl);
						 } 
						 else if(UtilValidate.isNotEmpty(roleTypeId) && roleTypeId.equals("CONTACT")){
							 redirectUrl = "/contact-portal/control/viewContact?partyId="+partyId+"&externalLoginKey="+externalKey;
							 // redirect
							 Debug.logInfo("redirectUrl = " + redirectUrl, MODULE);
							 response.sendRedirect(redirectUrl);
						 }else if(UtilValidate.isNotEmpty(roleTypeId) && (roleTypeId.equals("CUSTOMER") || roleTypeId.equals("CONTRACTOR"))){
							 redirectUrl = "/customer-portal/control/viewCustomer?partyId="+partyId+"&externalLoginKey="+externalKey;
							 // redirect
							 Debug.logInfo("redirectUrl = " + redirectUrl, MODULE);
							 response.sendRedirect(redirectUrl);
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
        	String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
        	String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
        	
			EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
            efo.setOffset(0);
            efo.setLimit(1000);
			
			List<GenericValue> custRequestHistoryList = delegator.findList("CustRequestHistory", EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId), null, UtilMisc.toList("-createdStamp"), efo, false);
			if (UtilValidate.isNotEmpty(custRequestHistoryList)) {
				int slaTat = 0;
				Map tatDaysByHistory= FastMap.newInstance();
				List noTatDayRecord  = FastList.newInstance();
				String isEnabledStaTat = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_ENABLE","N");
				if (isEnabledStaTat.equalsIgnoreCase("Y")) {
					Map<String,Object> pausedDates= org.groupfio.common.portal.util.DataHelper.getSrTatCountByHst(UtilMisc.toMap("custRequestId",custRequestId,"delegator",delegator));
					
					if (UtilValidate.isNotEmpty(pausedDates)) {
						tatDaysByHistory = (Map) pausedDates.get("tatDaysByHistory");
					}
				}
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
					String custOrderId = history.getString("custOrderId");
					if (UtilValidate.isNotEmpty(custOrderId)) {
						List<GenericValue> entityOrderLineAssoc = EntityQuery.use(delegator).from("EntityOrderLineAssoc").where("externalId", custOrderId, "domainEntityId", custRequestId).orderBy("lineItemNumber").queryList();
						if (UtilValidate.isNotEmpty(entityOrderLineAssoc)) {
							StringBuilder orderIdString = new StringBuilder();
							for (GenericValue entityOrderLineAssocs : entityOrderLineAssoc) {
								String sequenceId = entityOrderLineAssocs.getString("lineItemNumber");
								if (UtilValidate.isNotEmpty(sequenceId)) {
									orderIdString.append(custOrderId).append("_").append(sequenceId).append(", ");
								}
							}
							if (orderIdString.length() > 0) {
								orderIdString.setLength(orderIdString.length() - 2);
							}
							data.put("orderIdSequence", orderIdString.toString());
						}
					}
					data.put("orderId", history.getString("custOrderId"));
					data.put("owner", history.getString("ownerId"));
					data.put("srPrimaryContact", history.getString("custReqPrimaryContact"));
					
					data.put("createdBy", history.getString("createdByUserLogin"));
					data.put("modifiedBy", history.getString("lastModifiedByUserLogin"));
					data.put("closedBy", history.getString("closedByUserLogin"));
					
					data.put("createdDate", UtilValidate.isNotEmpty(history.get("createdDate")) ? UtilDateTime.timeStampToString(history.getTimestamp("createdDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
					data.put("modifiedDate", UtilValidate.isNotEmpty(history.get("lastModifiedDate")) ? UtilDateTime.timeStampToString(history.getTimestamp("lastModifiedDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
					data.put("closedDate", UtilValidate.isNotEmpty(history.get("closedByDate")) ? UtilDateTime.timeStampToString(history.getTimestamp("closedByDate"), globalDateFormat, TimeZone.getDefault(), null) : "");
					data.put("historyDate", UtilValidate.isNotEmpty(history.get("createdStamp")) ? UtilDateTime.timeStampToString(history.getTimestamp("createdStamp"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
					
					Timestamp createdStamp = UtilDateTime.getDayStart(history.getTimestamp("createdStamp"));
					if (UtilValidate.isNotEmpty(tatDaysByHistory) && tatDaysByHistory.containsKey(history.getString("custRequestHistoryId"))) {
						data.put("slaTat", tatDaysByHistory.get(history.getString("custRequestHistoryId")));
					}else {
						data.put("slaTat", 0);
					}
					
					data.put("comment", history.getString("comment"));
					
					dataList.add(data);
                }
            }

        } catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
            return doJSONResponse(response, e.getMessage());
        }
        
        return doJSONResponse(response, dataList);
	}
	
	public static String getSrActivityTimeEntries(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String custRequestId = (String) context.get("srNumber");
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			
			List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			if(UtilValidate.isNotEmpty(custRequestId)) {
				List<GenericValue> statusItem = EntityQuery.use(delegator).from("StatusItem").where("statusTypeId","IA_STATUS_ID").orderBy("sequenceId").queryList();
				Map<String, Object> statusList = new HashMap<>();
				if(UtilValidate.isNotEmpty(statusItem)) {
					statusList = DataUtil.getMapFromGeneric(statusItem, "statusId", "description", false);
				}
				List<GenericValue> rateTypeList = EntityQuery.use(delegator).select("rateTypeId","description").from("RateType").where("parentTypeId","ACTIVITY").distinct().queryList();
				Map<String, Object> rateTypes = org.fio.admin.portal.util.DataUtil.getMapFromGeneric(rateTypeList, "rateTypeId", "description", false);
				
				List < EntityCondition > conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "SERVICE_REQUEST"));
				conditions.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, custRequestId));
				
				EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				
				DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
				dynamicViewEntity.addMemberEntity("WE", "WorkEffort");
				//dynamicViewEntity.addAlias("WE", "workEffortId","workEffortId", null,Boolean.FALSE,Boolean.TRUE,null);
				dynamicViewEntity.addAlias("WE", "workEffortId");
				dynamicViewEntity.addAlias("WE", "workEffortTypeId");
				dynamicViewEntity.addAlias("WE", "currentStatusId");
				dynamicViewEntity.addAlias("WE", "workEffortName");
				dynamicViewEntity.addAlias("WE", "description");
				dynamicViewEntity.addAlias("WE", "actualStartDate");
				dynamicViewEntity.addAlias("WE", "actualCompletionDate");
				dynamicViewEntity.addAlias("WE", "domainEntityType");
				dynamicViewEntity.addAlias("WE", "domainEntityId");
				
				dynamicViewEntity.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
				dynamicViewEntity.addAlias("WEPA", "partyId");
				dynamicViewEntity.addAlias("WEPA", "roleTypeId");
				//dynamicViewEntity.addAlias("WEPA", "fromDate");
				//dynamicViewEntity.addAlias("WEPA", "thruDate");
				dynamicViewEntity.addAlias("WEPA", "statusId");
				dynamicViewEntity.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
				
				dynamicViewEntity.addMemberEntity("PER", "Person");
				dynamicViewEntity.addAlias("PER", "firstName");
				dynamicViewEntity.addAlias("PER", "lastName");
				dynamicViewEntity.addViewLink("WEPA", "PER", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
				
				dynamicViewEntity.addMemberEntity("TE", "TimeEntry");
				//dynamicViewEntity.addAlias("TE", "ownerId", "partyId",null,Boolean.FALSE,Boolean.TRUE,null);
				//dynamicViewEntity.addAlias("TE", "hours", "hours",null,Boolean.FALSE,Boolean.FALSE,"sum");
				//dynamicViewEntity.addAlias("TE", "cost", "cost",null,Boolean.FALSE,Boolean.FALSE,"sum");
				//dynamicViewEntity.addAlias("TE", "rateTypeId", "rateTypeId",null,Boolean.FALSE,Boolean.TRUE,null);
				dynamicViewEntity.addAlias("TE", "ownerId", "partyId",null,Boolean.FALSE,Boolean.FALSE,null);
				dynamicViewEntity.addAlias("TE", "timeEntryId");
				dynamicViewEntity.addAlias("TE", "hours");
				dynamicViewEntity.addAlias("TE", "cost");
				dynamicViewEntity.addAlias("TE", "rateTypeId");
				dynamicViewEntity.addAlias("TE", "fromDate");
				dynamicViewEntity.addAlias("TE", "thruDate");
				dynamicViewEntity.addAlias("TE", "comments");
				dynamicViewEntity.addAlias("TE", "timeEntryDate");
				dynamicViewEntity.addAlias("TE", "lastUpdatedTxStamp");
				dynamicViewEntity.addAlias("TE", "createdTxStamp");
				dynamicViewEntity.addViewLink("WEPA", "TE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId","workEffortId","partyId","partyId"));
				
				List<GenericValue> workEffortList = EntityQuery.use(delegator).from(dynamicViewEntity).where(condition).orderBy("createdTxStamp DESC","lastUpdatedTxStamp DESC").queryList();
				
				if(UtilValidate.isNotEmpty(workEffortList)) {
					for(GenericValue workEffort : workEffortList) {
						Map<String, Object> data = new HashMap<>();
						data.putAll(DataUtil.convertGenericValueToMap(delegator, workEffort));
						data.put("activityStatus", UtilValidate.isNotEmpty(statusList) && UtilValidate.isNotEmpty(workEffort.getString("currentStatusId")) ? statusList.get(workEffort.getString("currentStatusId")) : "" );
						data.put("dateOfService", UtilValidate.isNotEmpty(workEffort.getString("timeEntryDate")) ? (org.fio.admin.portal.util.DataUtil.convertDateTimestamp(workEffort.getString("timeEntryDate"), new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING)) : "");
						data.put("purpose", UtilValidate.isNotEmpty(rateTypes) && UtilValidate.isNotEmpty(rateTypes.get(workEffort.getString("rateTypeId"))) ?rateTypes.get(workEffort.getString("rateTypeId")) :"");
						dataList.add(data);
					}
				}
				/*
				Set<String> selectFields = new LinkedHashSet<String>();
				selectFields.add("workEffortId");
				selectFields.add("workEffortTypeId");
				selectFields.add("currentStatusId");
				selectFields.add("workEffortName");
				selectFields.add("description");
				selectFields.add("actualStartDate");
				selectFields.add("actualCompletionDate");
				selectFields.add("domainEntityType");
				selectFields.add("domainEntityId");
				List < GenericValue > workEfforts = EntityQuery.use(delegator).select(selectFields).from("WorkEffort").where(condition).queryList();
				if(UtilValidate.isNotEmpty(workEfforts)) {
					for(GenericValue workEffort : workEfforts) {
						String workEffortId = workEffort.getString("workEffortId");
						Map<String, Object> data = new HashMap<>();
						data.putAll(DataUtil.convertGenericValueToMap(delegator, workEffort));
						
						String activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
						GenericValue workEffortParty = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where("workEffortId", workEffortId,"roleTypeId",activityOwnerRole).filterByDate().queryFirst();
						if(UtilValidate.isNotEmpty(workEffortParty)) {
							String ownerPartyId = workEffortParty.getString("partyId");
							GenericValue person1 =  EntityQuery.use(delegator).from("Person").where("partyId",ownerPartyId).queryFirst();
							if(UtilValidate.isNotEmpty(person1)) {
								data.put("ownerName", person1.getString("firstName")+ (UtilValidate.isNotEmpty(person1.getString("lastName")) ? " "+person1.getString("lastName") : "" ));
							}
						}
						List<GenericValue> timeEntryList =  EntityQuery.use(delegator).from("TimeEntry").where("workEffortId", workEffortId).queryList();
						double totalHours =0d;
						double totalCost =0d;
						data.put("status", UtilValidate.isNotEmpty(statusList) && UtilValidate.isNotEmpty(workEffort.getString("currentStatusId")) ? statusList.get(workEffort.getString("currentStatusId")) : "" );
						for (GenericValue timeEntry: timeEntryList) {
							totalHours = totalHours + (UtilValidate.isNotEmpty(timeEntry.getDouble("hours")) ? timeEntry.getDouble("hours") : 0d);
							totalCost = totalCost + (UtilValidate.isNotEmpty(timeEntry.getDouble("cost")) ? timeEntry.getDouble("cost") : 0d);
						}
						data.put("totalHours", totalHours);
						data.put("totalCost", totalCost);
						dataList.add(data);
					}
				}*/
			}
			result.put("list", dataList);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			//e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String getSrActivityIssueMaterials(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String custRequestId = (String) context.get("srNumber");
		try {
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			
			List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			if(UtilValidate.isNotEmpty(custRequestId)) {
				List<GenericValue> statusItem = EntityQuery.use(delegator).from("StatusItem").where("statusTypeId","IA_STATUS_ID").orderBy("sequenceId").queryList();
				Map<String, Object> statusList = new HashMap<>();
				if(UtilValidate.isNotEmpty(statusItem)) {
					statusList = DataUtil.getMapFromGeneric(statusItem, "statusId", "description", false);
				}
				List<GenericValue> enumeration = EntityQuery.use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","ISSUE_MAT_CODE").orderBy("sequenceId").queryList();
				Map<String, Object> purposeList = new HashMap<>();
				if(UtilValidate.isNotEmpty(enumeration)) {
					purposeList = DataUtil.getMapFromGeneric(enumeration, "enumId", "description", false);
				}
				
				List < EntityCondition > conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "SERVICE_REQUEST"));
				conditions.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, custRequestId));

				EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				
				DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
				dynamicViewEntity.addMemberEntity("WE", "WorkEffort");
				//dynamicViewEntity.addAlias("WE", "workEffortId","workEffortId", null,Boolean.FALSE,Boolean.TRUE,null);
				dynamicViewEntity.addAlias("WE", "workEffortId");
				dynamicViewEntity.addAlias("WE", "workEffortTypeId");
				dynamicViewEntity.addAlias("WE", "currentStatusId");
				dynamicViewEntity.addAlias("WE", "workEffortName");
				dynamicViewEntity.addAlias("WE", "description");
				dynamicViewEntity.addAlias("WE", "actualStartDate");
				dynamicViewEntity.addAlias("WE", "actualCompletionDate");
				dynamicViewEntity.addAlias("WE", "domainEntityType");
				dynamicViewEntity.addAlias("WE", "domainEntityId");
				
				dynamicViewEntity.addMemberEntity("IM", "IssueMaterial");
				//dynamicViewEntity.addAlias("IM", "partyId", "partyId",null,Boolean.FALSE,Boolean.TRUE,null);
				//dynamicViewEntity.addAlias("IM", "productId", "productId",null,Boolean.FALSE,Boolean.TRUE,null);
				//dynamicViewEntity.addAlias("IM", "quantity", "quantity",null,Boolean.FALSE,Boolean.FALSE,"sum");
				dynamicViewEntity.addAlias("IM", "partyId");
				dynamicViewEntity.addAlias("IM", "productId");
				dynamicViewEntity.addAlias("IM", "quantity");
				dynamicViewEntity.addAlias("IM", "description");
				dynamicViewEntity.addAlias("IM", "productName");
				dynamicViewEntity.addAlias("IM", "addedDate");
				dynamicViewEntity.addAlias("IM", "issuedType");
				dynamicViewEntity.addAlias("IM", "lastUpdatedTxStamp");
				dynamicViewEntity.addViewLink("WE", "IM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
				
				dynamicViewEntity.addMemberEntity("PER", "Person");
				dynamicViewEntity.addAlias("PER", "firstName");
				dynamicViewEntity.addAlias("PER", "lastName");
				dynamicViewEntity.addViewLink("IM", "PER", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
				
				dynamicViewEntity.addMemberEntity("PRD", "Product");
				//dynamicViewEntity.addAlias("PRD", "productName");
				dynamicViewEntity.addAlias("PRD", "internalName");
				dynamicViewEntity.addAlias("PRD", "smallImageUrl");
				dynamicViewEntity.addAlias("PRD", "mediumImageUrl");
				dynamicViewEntity.addViewLink("IM", "PRD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));
			
				
				List<GenericValue> issueMaterialList = EntityQuery.use(delegator).from(dynamicViewEntity).where(condition).orderBy("lastUpdatedTxStamp DESC").queryList();
				
				if(UtilValidate.isNotEmpty(issueMaterialList)) {
					String defaultImageRoot = ComponentConfig.getRootLocation("bootstrap");
					defaultImageRoot = defaultImageRoot + "webapp"+File.separator+"bootstrap"+File.separator+"images"+File.separator+"default-product-img.png";
					File image = new File(defaultImageRoot);
					String encodedImage = "";
					if(image.exists()) {
						byte[] fileContent = Files.readAllBytes(image.toPath());
						encodedImage = java.util.Base64.getEncoder().encodeToString(fileContent);
					}
					
					for(GenericValue issueMaterial : issueMaterialList) {
						Map<String, Object> data = new HashMap<>();
						String issuedType = issueMaterial.getString("issuedType");
						data.putAll(DataUtil.convertGenericValueToMap(delegator, issueMaterial));
						data.put("activityStatus", UtilValidate.isNotEmpty(statusList) && UtilValidate.isNotEmpty(issueMaterial.getString("currentStatusId")) ? statusList.get(issueMaterial.getString("currentStatusId")) : "" );
						data.put("createdDate", org.fio.admin.portal.util.DataUtil.convertDateTimestamp(issueMaterial.getString("addedDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING));
						data.put("purpose", UtilValidate.isNotEmpty(purposeList)&& UtilValidate.isNotEmpty(issueMaterial.getString("description")) ? purposeList.get(issueMaterial.getString("description")) : "" );
						data.put("productDefaultImage", UtilValidate.isNotEmpty(encodedImage) ? "data:image/png;base64,"+encodedImage : "");
						data.put("materialSource", EnumUtil.getEnumDescriptionByEnumId(delegator, issuedType));
						dataList.add(data);
					}
				}
				
				/*
				Set<String> selectFields = new LinkedHashSet<String>();
				selectFields.add("workEffortId");
				selectFields.add("workEffortTypeId");
				selectFields.add("currentStatusId");
				selectFields.add("workEffortName");
				selectFields.add("description");
				selectFields.add("actualStartDate");
				selectFields.add("actualCompletionDate");
				selectFields.add("domainEntityType");
				selectFields.add("domainEntityId");
				List < GenericValue > workEfforts = EntityQuery.use(delegator).select(selectFields).from("WorkEffort").where(condition).queryList();
				List<String> workEffortIds = new LinkedList<String>();
				if(UtilValidate.isNotEmpty(workEfforts)) {
					workEffortIds = EntityUtil.getFieldListFromEntityList(workEfforts, "workEffortId", true);
					
					for(GenericValue workEffort : workEfforts) {
						String workEffortId = workEffort.getString("workEffortId");
						Map<String, Object> data = new HashMap<>();
						data.putAll(DataUtil.convertGenericValueToMap(delegator, workEffort));
						
						String activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
						GenericValue workEffortParty = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where("workEffortId", workEffortId,"roleTypeId",activityOwnerRole).filterByDate().queryFirst();
						if(UtilValidate.isNotEmpty(workEffortParty)) {
							String ownerPartyId = workEffortParty.getString("partyId");
							GenericValue person1 =  EntityQuery.use(delegator).from("Person").where("partyId",ownerPartyId).queryFirst();
							if(UtilValidate.isNotEmpty(person1)) {
								data.put("ownerName", person1.getString("firstName")+ (UtilValidate.isNotEmpty(person1.getString("lastName")) ? " "+person1.getString("lastName") : "" ));
							}
						}
						
						data.put("status", UtilValidate.isNotEmpty(statusList) && UtilValidate.isNotEmpty(workEffort.getString("currentStatusId")) ? statusList.get(workEffort.getString("currentStatusId")) : "" );
						List<GenericValue> issueMaterialList =  EntityQuery.use(delegator).from("IssueMaterial").where("workEffortId", workEffortId).queryList();
						int totalQuantity = 0;

						for (GenericValue issueMaterial: issueMaterialList) {
							String quantity = issueMaterial.getString("quantity");
							if(UtilValidate.isNotEmpty(quantity))
								totalQuantity = totalQuantity+Integer.parseInt(quantity);
						}
						data.put("totalQuantity", totalQuantity);
						dataList.add(data);
					}
				}
				*/
				
				
			}
			result.put("list", dataList);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			//e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String getPartyList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		List < Map<String, Object>> results = new ArrayList<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String roleTypeId = (String) context.get("roleTypeId");
		try {
			if(UtilValidate.isNotEmpty(roleTypeId)) {
				List<GenericValue> partyRoleList = EntityQuery.use(delegator).from("PartyRole").where(EntityCondition.makeCondition("roleTypeId", roleTypeId)).queryList();
				if(UtilValidate.isEmpty(partyRoleList))
					return doJSONResponse(response, results);
				
				List<String> partyIdList = EntityUtil.getFieldListFromEntityList(partyRoleList, "partyId", true);
				for(String partyId : partyIdList) {
					Map<String, Object> data = new HashMap<>();
					String customerName = "";
					GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", partyId).queryFirst();
					if(UtilValidate.isEmpty(person)) {
						person = EntityQuery.use(delegator).from("PartyGroup").where("partyId",partyId).queryFirst();
						customerName = UtilValidate.isNotEmpty(person) ? person.getString("groupName") : "";
					} else {
						customerName = person.getString("firstName")+ (UtilValidate.isNotEmpty(person.getString("lastName")) ? " "+person.getString("lastName") : "" );
					}
					if(UtilValidate.isNotEmpty(customerName)) {
						data.put("partyId", partyId);
						data.put("customerName", customerName);
					}
					results.add(data);
				}
			}
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			return doJSONResponse(response, results);
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	
	public static String createSrAttribute(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String srNumber = request.getParameter("srNumber");
		
		String attrName = request.getParameter("attrName");
		String attrValue = request.getParameter("attrValue");
		String channelId = request.getParameter("channelId");
		String sequenceNumber = request.getParameter("sequenceNumber");

		Map<String, Object> result = FastMap.newInstance();

		try {

			if (UtilValidate.isNotEmpty(srNumber)) {

				List conditionList = FastList.newInstance();

				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, srNumber));
				conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, attrName));

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue attr = EntityUtil.getFirst( delegator.findList("CustRequestAttribute", mainConditons, null, null, null, false) );

				if (UtilValidate.isNotEmpty(attr)) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Attribute already exists!");
					return AjaxEvents.doJSONResponse(response, result);
				}
				
				attr = delegator.makeValue("CustRequestAttribute");
				
				attr.put("custRequestId", srNumber);
				attr.put("attrName", attrName);
				attr.put("attrValue", attrValue);
				attr.put("channelId", channelId);
				attr.put("sequenceNumber", sequenceNumber);
				
				attr.create();

				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully created attribute");
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String removeSrAttribute(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String custRequestId = request.getParameter("custRequestId");
		String attrName = request.getParameter("attrName");
		
		Map<String, Object> result = FastMap.newInstance();

		try {

			if (UtilValidate.isNotEmpty(custRequestId)) {

				List conditionList = FastList.newInstance();

				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, attrName));

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue attr = EntityUtil.getFirst( delegator.findList("CustRequestAttribute", mainConditons, null, null, null, false) );

				if (UtilValidate.isEmpty(attr)) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Attribute not exists!");
					return AjaxEvents.doJSONResponse(response, result);
				}
				
				attr.remove();
				
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully removed attribute");
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String getSrAssocParties(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String custRequestId = (String) context.get("srNumber");
		try {
			List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			if(UtilValidate.isNotEmpty(custRequestId)) {
				
				dataList = SrUtil.getSrAssocParties(delegator, custRequestId);
				
			}
			result.put("list", dataList);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			//e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String removeSrAssociatedParty(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {	
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		String requestData = DataUtil.getJsonStrBody(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String custRequestId = "";
		String responseMessage = "Association removed successfully";
		
		try {
			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			List<GenericValue> toBeRemove = new LinkedList<GenericValue>();
			for(Map<String, Object> data : dataList) {
				custRequestId = (String) data.get("custRequestId");
				String partyId = (String) data.get("partyId");
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)
						);
				GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where(condition).queryFirst();
				
				if(UtilValidate.isNotEmpty(custRequestParty)) {
					toBeRemove.add(custRequestParty);
				}
			}
			if(toBeRemove.size() > 0){
				delegator.removeAll(toBeRemove);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("srNumber", custRequestId);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
            return CommonEvents.doJSONResponse(response, result);
		}
		request.setAttribute("srNumber", custRequestId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
        return CommonEvents.doJSONResponse(response, result);
	
	}
	
	public static String addPartyToSr(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> results = new HashMap<String, Object>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String requestData = DataUtil.getJsonStrBody(request);
		String userLoginId = request.getParameter("userLoginId");
		String custRequestId = request.getParameter("custRequestId");
		String selectedRows = request.getParameter("selecteddRows");
		String activeTab =  request.getParameter("activeTab");
    	
    	try {
    		if(UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(selectedRows)) {
    			request.setAttribute("srNumber", custRequestId);
    			request.setAttribute("activeTab", activeTab);
    			
    			List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();
    			if(UtilValidate.isNotEmpty(selectedRows))
    				requestMapList = DataUtil.convertToListMap(selectedRows);
    			if(UtilValidate.isNotEmpty(requestMapList)) {
    				List<GenericValue> toBeStore = new LinkedList<GenericValue>();
    				for(Map<String, Object> requestMap : requestMapList) {
    					Map<String,Object> inputMap = FastMap.newInstance();
    					String roleTypeId = (String) requestMap.get("roleTypeId");
    					String partyId = (String) requestMap.get("partyId");
    					inputMap.put("custRequestId", custRequestId);
    					inputMap.put("partyId", partyId);
    					inputMap.put("roleTypeId", roleTypeId);
    					inputMap.put("fromDate", UtilDateTime.nowTimestamp());
    					
    					GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where(inputMap).filterByDate().queryFirst();
    					if(UtilValidate.isEmpty(custRequestParty)) {
    						custRequestParty = delegator.makeValue("CustRequestParty", inputMap);
    						toBeStore.add(custRequestParty);
    					}
    				}
    				if(toBeStore.size() > 0)
    					delegator.storeAll(toBeStore);
    				
					request.setAttribute("_EVENT_MESSAGE_", "Party Associated to the SR: "+custRequestId);
    			}
    		}
    		Debug.log("Results : "+results, MODULE);
    	} catch (Exception e) {
    		String errMsg = "Problem while associating party" + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
    	}
    	return "success";
	}
	
	public static String getSortedSrNotesList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> results = new HashMap<String, Object>();
		String srNumber = request.getParameter("srNumber");
		String selectedRows = request.getParameter("selectedRows");
		String workEffortId = request.getParameter("workEffortId");
		
		try {
			if(UtilValidate.isNotEmpty(srNumber)) {
				request.setAttribute("srNumber", srNumber);
			}
			if(UtilValidate.isNotEmpty(workEffortId)) {
				request.setAttribute("workEffortId", workEffortId);
			}
			List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();
			List<String> noteIdsList = new ArrayList<String>();
			
			if(UtilValidate.isNotEmpty(selectedRows)){
				requestMapList = org.fio.admin.portal.util.DataUtil.convertToListMap(selectedRows);

				if(UtilValidate.isNotEmpty(requestMapList)) {
					for(Map<String, Object> requestMap : requestMapList) {
						String noteId = (String) requestMap.get("noteId");
						noteIdsList.add(noteId);
					}
				}
			}
			
			request.setAttribute("noteIdsList", noteIdsList);
		}catch (Exception e) {
			String errMsg = "Problem While Fetching Note Ids " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}

	public static String getNotesList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> results = new HashMap<String, Object>();
		String srNumber = request.getParameter("srNumber");
		String selectedRows = request.getParameter("selectedRows");
		String workEffortId = request.getParameter("workEffortId");
		
		try {
			if(UtilValidate.isNotEmpty(srNumber)) {
				request.setAttribute("srNumber", srNumber);
			}
			if(UtilValidate.isNotEmpty(workEffortId)) {
				request.setAttribute("workEffortId", workEffortId);
				GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId, "domainEntityType", "SERVICE_REQUEST").queryFirst();
				request.setAttribute("srNumber", UtilValidate.isNotEmpty(workEffort) ? workEffort.getString("domainEntityId") : "");
			}
			List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();
			List<String> noteIdsList = new ArrayList<String>();
			
			if(UtilValidate.isNotEmpty(selectedRows)){
				requestMapList = org.fio.admin.portal.util.DataUtil.convertToListMap(selectedRows);

				if(UtilValidate.isNotEmpty(requestMapList)) {
					for(Map<String, Object> requestMap : requestMapList) {
						String noteId = (String) requestMap.get("noteId");
						noteIdsList.add(noteId);
					}
				}
			}
			
			request.setAttribute("noteIdsList", noteIdsList);
		}catch (Exception e) {
			String errMsg = "Problem While Fetching Note Ids " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}
	
	public static String getSrDashboardList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
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
		
		List<Map<String, Object>> dataList = new ArrayList<>();
		long start = System.currentTimeMillis();
		Timestamp systemTime = UtilDateTime.nowTimestamp();
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
				if("my-sr".equals(filterType)) {
					//if("clientPortal".equals(clientPortal)) {
						dynamicView.addMemberEntity("CRP", "CustRequestParty");
						dynamicView.addAlias("CRP", "partyId");
						dynamicView.addAlias("CRP", "roleTypeId");
						dynamicView.addAlias("CRP", "fromDate");
						dynamicView.addAlias("CRP", "thruDate");
						dynamicView.addViewLink("CR", "CRP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("custRequestId"));
						
						conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginPartyId),
								EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList(loggedUserRole)),
								EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr())
								));
						
						
					//} else
					//	conditions.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, userLoginId));
					
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
					dynamicView.addViewLink("CR", "CRA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
					
					String locationCustomFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);
					conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, locationCustomFieldId),
								EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, srLocation)
							));
				}
				
				//conditions.add(EntityCondition.makeCondition("crpPartyId", EntityOperator.EQUALS, userLoginPartyId));
				if("sr-open".equals(filterBy)) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED","SR_CANCELLED")));
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
					dynamicView.addViewLink("CR", "CRWE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("custRequestId"));
					
					dynamicView.addMemberEntity("WE", "WorkEffort");
					dynamicView.addAlias("WE", "weLastModifiedDate","lastModifiedDate",null,Boolean.FALSE,Boolean.FALSE,null);
					dynamicView.addViewLink("CRWE", "WE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
					
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_WRK_COMPL")));
					LocalDateTime last_3_week = LocalDateTime.now().minusWeeks(3);
					conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("lastModifiedDate", EntityOperator.LESS_THAN, UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week))),
							EntityCondition.makeCondition("weLastModifiedDate", EntityOperator.LESS_THAN, UtilDateTime.getDayStart(Timestamp.valueOf(last_3_week)))
							));
					
				} else if("sr-over-due".equals(filterBy)) {
					LocalDateTime startOfLastWeek = LocalDateTime.now().minusWeeks(1).with(DayOfWeek.MONDAY);
					LocalDateTime endOfLastWeek = startOfLastWeek.plusDays(6);
					
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_CLOSED", "SR_CANCELLED", "SR_WRK_COMPL")));
					conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("closedByDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayEnd(Timestamp.valueOf(startOfLastWeek))),
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
				}
				
				
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
					
					Map<String, Object> storeNames = SrDataHelper.getProductStoreNames(delegator);
					
					String locationCustomFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties",
							"location.customFieldId", delegator);
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);

					
					String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
					String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
					// int count = 0;
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
		            
		            long start1 = System.currentTimeMillis();
	            	for(GenericValue serviceRequest : resultList) {
						
						Map<String, Object> data = new HashMap<String, Object>();
						
						String custRequestId = serviceRequest.getString("custRequestId");

						Map<String, Object> assocPartyNames = SrDataHelper.getSrAssocPartyNames(delegator, custRequestId);
						
						data.put("contractorPersonName", assocPartyNames.get("CONTRACTOR"));
						data.put("salePersonName", assocPartyNames.get("SALES_REP"));
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
						
						data.put("primaryContactName", assocPartyNames.get("CONTACT"));
						data.put("homeOwnerName", assocPartyNames.get("CUSTOMER"));
						
						data.put("activityOwnerName", org.fio.homeapps.util.DataUtil.getSrActivityOwnersName(delegator, custRequestId));

						data.put("homePhoneNumber", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("homePhoneNumber")));
						data.put("offPhoneNumber", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("offPhoneNumber")));
						data.put("mobileNumber", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("mobileNumber")));
						data.put("contractorOffPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("contractorOffPhone")));
						data.put("contractorMobilePhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("contractorMobilePhone")));
						data.put("contractorHomePhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(serviceRequest.getString("contractorHomePhone")));
						
						
						String locationId = SrUtil.getCustRequestAttrValue(delegator, locationCustomFieldId, custRequestId);
						if (UtilValidate.isNotEmpty(locationId)) {
							data.put("location", storeNames.get(locationId));
						}
						
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
	
	
	public static String getSrDashboardCountList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
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
		List<Map<String, Object>> dataList = new ArrayList<>();
		long start = System.currentTimeMillis();
		Timestamp systemTime = UtilDateTime.nowTimestamp();
		try {
			String userLoginId = userLogin.getString("userLoginId");
			String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				
			String loggedUserRole = DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			List<String> statusIds = new ArrayList<>();
			
			if((UtilValidate.isNotEmpty(filterType) || UtilValidate.isNotEmpty(filterBy))  && UtilValidate.isNotEmpty(userLoginId)) {
				SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
				Connection con = (Connection)sqlProcessor.getConnection();
				CallableStatement cstmt = null;
				ResultSet rs = null;
				String barType = "";
				String count = "";
				srLocation = UtilValidate.isNotEmpty(srLocation) ? srLocation : "ALL";
				if("my-sr".equals(filterType)) {
					if("clientPortal".equals(clientPortal)){
						String sqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'Open' FROM cust_request CR "
										+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
										+" WHERE CRP.party_id='"+userLoginPartyId+"'AND CRP.ROLE_TYPE_ID IN ('"+loggedUserRole+"')" 
										+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
										+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
										+" AND status_id NOT IN ('SR_CLOSED','SR_CANCELLED')";
										
						rs = sqlProcessor.executeQuery(sqlQuery);
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
				}
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
	
	public static String createTechnicianLocation(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String state = (String) context.get("generalStateProvinceGeoId");
		String county = (String) context.get("countyGeoId");
		String isTechInspection = (String) context.get("isTechInspection");
		String productStoreId = (String) context.get("productStoreId");
		
		String technician1 = (String) context.get("technician1");
		String technician2 = (String) context.get("technician2");
		String technician3 = (String) context.get("technician3");
		String technician4 = (String) context.get("technician4");
		String coordinator = (String) context.get("coordinator");
		
		try {

			if(UtilValidate.isNotEmpty(state) && UtilValidate.isNotEmpty(county)){
				GenericValue prodStoreGv = delegator.findOne("ProductStoreTechAssoc",UtilMisc.toMap("county", county, "state", state), false);
				if(UtilValidate.isNotEmpty(prodStoreGv)){
					request.setAttribute("_ERROR_MESSAGE_", "Location and Technicians Already Configured");
					return "error";
				}

				GenericValue productStoreTechAssoc = delegator.makeValue("ProductStoreTechAssoc");

				productStoreTechAssoc.set("county", county);
				productStoreTechAssoc.set("state", state);
				productStoreTechAssoc.set("isTechInspection", isTechInspection);

				if(UtilValidate.isNotEmpty(productStoreId)){
					productStoreTechAssoc.set("productStoreId", productStoreId);
					GenericValue productStoreInfo = EntityQuery.use(delegator).select("productStoreId","storeName").from("ProductStore").where("productStoreId", productStoreId).queryOne();
					if (UtilValidate.isNotEmpty(productStoreInfo) && UtilValidate.isNotEmpty(productStoreInfo.getString("storeName"))) {
						productStoreTechAssoc.set("productStoreName", productStoreInfo.getString("storeName"));
					}
				}
				
				if(UtilValidate.isNotEmpty(technician1)){
					productStoreTechAssoc.set("technicianId01", technician1);
					productStoreTechAssoc.set("technicianName01", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, technician1, false)) ? PartyHelper.getPartyName(delegator, technician1, false) : "");
				}
				if(UtilValidate.isNotEmpty(technician2)){
					productStoreTechAssoc.set("technicianId02", technician2);
					productStoreTechAssoc.set("technicianName02", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, technician2, false)) ? PartyHelper.getPartyName(delegator, technician2, false) : "");
				}
				if(UtilValidate.isNotEmpty(technician3)){
					productStoreTechAssoc.set("technicianId03", technician3);
					productStoreTechAssoc.set("technicianName03", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, technician3, false)) ? PartyHelper.getPartyName(delegator, technician3, false) : "");
				}
				if(UtilValidate.isNotEmpty(technician4)){
					productStoreTechAssoc.set("technicianId04", technician4);
					productStoreTechAssoc.set("technicianName04", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, technician3, false)) ? PartyHelper.getPartyName(delegator, technician4, false) : "");
				}
				if(UtilValidate.isNotEmpty(coordinator)) {
					String coordinatorName = PartyHelper.getPartyName(delegator, coordinator, false) ;
					productStoreTechAssoc.set("coordinator", coordinator);
					productStoreTechAssoc.set("coordinatorName", UtilValidate.isNotEmpty(coordinatorName) ? coordinatorName : "");
				}
				productStoreTechAssoc.create();
				request.setAttribute("stateId",state);
	        	request.setAttribute("countyId",county);
				request.setAttribute("productStoreId",productStoreId);
				request.setAttribute("_EVENT_MESSAGE_", "Location and Technicians Added Successfully");
			}
			
		}catch (Exception e) {
			String errMsg = "Problem While Mapping Location and Technicians" + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}
		
	public static String updateTechnicianLocation(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	Map<String, Object> context = UtilHttp.getCombinedMap(request);

    	String county = (String) context.get("countyGeoId");
    	String state = (String) context.get("generalStateProvinceGeoId");
    	String isTechInspection = (String) context.get("isTechInspection");
    	String productStoreId = (String) context.get("productStoreId");

    	String technician1 = (String) context.get("technician1");
    	String technician2 = (String) context.get("technician2");
    	String technician3 = (String) context.get("technician3");
    	String technician4 = (String) context.get("technician4");
    	String coordinator = (String) context.get("coordinator");
    	
    	try {

    		if(UtilValidate.isNotEmpty(county) && UtilValidate.isNotEmpty(state)){
    			GenericValue productStoreTechAssoc = delegator.findOne("ProductStoreTechAssoc",UtilMisc.toMap("county", county, "state", state), false);
				if(UtilValidate.isNotEmpty(productStoreId)){
					productStoreTechAssoc.set("productStoreId", productStoreId);
					GenericValue productStoreInfo = EntityQuery.use(delegator).select("productStoreId","storeName").from("ProductStore").where("productStoreId", productStoreId).queryOne();
					if (UtilValidate.isNotEmpty(productStoreInfo) && UtilValidate.isNotEmpty(productStoreInfo.getString("storeName"))) {
						productStoreTechAssoc.set("productStoreName", productStoreInfo.getString("storeName"));
					}
				}
				
				productStoreTechAssoc.set("technicianId01", technician1);
				productStoreTechAssoc.set("technicianName01", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, technician1, false)) ? PartyHelper.getPartyName(delegator, technician1, false) : "");
				
				productStoreTechAssoc.set("technicianId02", technician2);
				productStoreTechAssoc.set("technicianName02", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, technician2, false)) ? PartyHelper.getPartyName(delegator, technician2, false) : "");
				
				productStoreTechAssoc.set("technicianId03", technician3);
				productStoreTechAssoc.set("technicianName03", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, technician3, false)) ? PartyHelper.getPartyName(delegator, technician3, false) : "");
				
				productStoreTechAssoc.set("technicianId04", technician4);
				productStoreTechAssoc.set("technicianName04", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, technician3, false)) ? PartyHelper.getPartyName(delegator, technician4, false) : "");
				
				if(UtilValidate.isNotEmpty(coordinator)) {
					String coordinatorName = PartyHelper.getPartyName(delegator, coordinator, false) ;
					productStoreTechAssoc.set("coordinator", coordinator);
					productStoreTechAssoc.set("coordinatorName", UtilValidate.isNotEmpty(coordinatorName) ? coordinatorName : "");
				}
				
				productStoreTechAssoc.store();
				
				request.setAttribute("productStoreId",productStoreId);
				request.setAttribute("_EVENT_MESSAGE_", "Location and Technicians Updated Successfully");

    		} else {
    			String errMsg = "Required parameter missed!";
        		request.setAttribute("_ERROR_MESSAGE_", errMsg);
        		request.setAttribute("stateId",state);
            	request.setAttribute("countyId",county);
        		return "error";
    		}
    	}catch (Exception e) {
    		String errMsg = "Problem While Updating Location and Technicians" + e.toString();
    		request.setAttribute("_ERROR_MESSAGE_", errMsg);
    		request.setAttribute("stateId",state);
        	request.setAttribute("countyId",county);
    		return "error";
    	}
    	request.setAttribute("stateId",state);
    	request.setAttribute("countyId",county);
    	return "success";
	}
	
	
	public static String getTechnicianLocation(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String county = (String) context.get("countyGeoId");
    	String state = (String) context.get("stateGeoId");
    	String isTechInspection = (String) context.get("isTechInspection");
    	String productStoreId = (String) context.get("productStoreId");
    	
    	try{
    		
    		List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(county)) {
				conditions.add(EntityCondition.makeCondition("county", EntityOperator.EQUALS, county));
			}
			if (UtilValidate.isNotEmpty(state)) {
				conditions.add(EntityCondition.makeCondition("state", EntityOperator.EQUALS, state));
			}
			if (UtilValidate.isNotEmpty(isTechInspection)) {
				conditions.add(EntityCondition.makeCondition("isTechInspection", EntityOperator.EQUALS, isTechInspection));
			}
			if (UtilValidate.isNotEmpty(productStoreId)) {
				conditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
			}
			
			EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			List < GenericValue > productStoreTechAssocList = EntityQuery.use(delegator).from("ProductStoreTechAssoc").where(mainCondition).orderBy("-createdStamp").queryList();
			
			int uniqueValue = 1;
			for (GenericValue eachProductStore : productStoreTechAssocList) {
				Map<String, Object> data = new HashMap<String, Object>();
				
				state = eachProductStore.getString("state");
				county = eachProductStore.getString("county");
				isTechInspection = eachProductStore.getString("isTechInspection");
				productStoreId = eachProductStore.getString("productStoreId");
				String productStoreName = eachProductStore.getString("productStoreName");
				
				String technician1 = eachProductStore.getString("technicianId01");
				String technician2 = eachProductStore.getString("technicianId02");
				String technician3 = eachProductStore.getString("technicianId03");
				String technician4 = eachProductStore.getString("technicianId04");
				String coordinator = eachProductStore.getString("coordinator");
				
				String technicianName1 = eachProductStore.getString("technicianName01");
				String technicianName2 = eachProductStore.getString("technicianName02");
				String technicianName3 = eachProductStore.getString("technicianName03");
				String technicianName4 = eachProductStore.getString("technicianName04");
				String coordinatorName = eachProductStore.getString("coordinatorName");
				
				if (UtilValidate.isNotEmpty(state)) {
					String stateName = DataUtil.getGeoName(delegator, state, "STATE");
					if (UtilValidate.isNotEmpty(stateName)) {
						data.put("stateName", stateName);
					}
				}
				if (UtilValidate.isNotEmpty(county)) {
					String countryName = DataUtil.getGeoName(delegator, county, "COUNTRY");
					if (UtilValidate.isNotEmpty(countryName)) {
						data.put("countryName", countryName);
					}
				}
				
				data.put("stateGeoId", state);
				data.put("countyGeoId", county);
				data.put("isTechInspection", isTechInspection);
				data.put("productStoreId", productStoreId);
				data.put("storeName", productStoreName);
				
				data.put("technician1", UtilValidate.isNotEmpty(technician1) ? technician1 : "");
				data.put("technician2", UtilValidate.isNotEmpty(technician2) ? technician2 : "");
				data.put("technician3", UtilValidate.isNotEmpty(technician3) ? technician3 : "");
				data.put("technician4", UtilValidate.isNotEmpty(technician4) ? technician4 : "");
				data.put("coordinator", UtilValidate.isNotEmpty(coordinator) ? coordinator : "");
				
				data.put("technicianName1", UtilValidate.isNotEmpty(technicianName1) ? technicianName1 : "");
				data.put("technicianName2", UtilValidate.isNotEmpty(technicianName2) ? technicianName2 : "");
				data.put("technicianName3", UtilValidate.isNotEmpty(technicianName3) ? technicianName3 : "");
				data.put("technicianName4", UtilValidate.isNotEmpty(technicianName4) ? technicianName4 : "");
				data.put("coordinatorName", UtilValidate.isNotEmpty(coordinatorName) ? coordinatorName : "");
				data.put("uniqueId", uniqueValue);
				uniqueValue = uniqueValue+1;
				
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
				String state = (String) data.get("stateGeoId");
				String country = (String) data.get("countyGeoId");
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
	
	public static String allowToUpdateSrStatusToSchedule(Delegator delegator, List<String> scheduledStatuses, String custRequestId)
			throws GenericEntityException {
		String allowToUpdateStatusToSchedule = "N";
		
		List<GenericValue> workEffortList = null;
		List<GenericValue> custRequestWorkEffortList = EntityQuery.use(delegator).select("workEffortId").from("CustRequestWorkEffort").where("custRequestId", custRequestId).queryList();
		
		if(UtilValidate.isNotEmpty(custRequestWorkEffortList)){
			List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList, "workEffortId", true);

			List<EntityCondition> conditionlist = FastList.newInstance();
			conditionlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
			conditionlist.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "IA_MSCHEDULED"));

			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			workEffortList = delegator.findList("WorkEffort", condition,null, null, null, false);
		} 
		if(UtilValidate.isNotEmpty(workEffortList)){
			allowToUpdateStatusToSchedule = "Y";
			return allowToUpdateStatusToSchedule;
		}
		return allowToUpdateStatusToSchedule;
	}
	
	public static String updateSrJustifiation(HttpServletRequest request, HttpServletResponse response) {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String srNumber = request.getParameter("srNumber");
		
		String justificationOldProd = request.getParameter("justificationOldProd");
		
		Map<String, Object> result = FastMap.newInstance();

		try {

			if (UtilValidate.isNotEmpty(srNumber)) {
				GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("groupId","SERVICE_GROUP","customFieldName","Justification for FSR on Old Product").queryFirst();
				if(UtilValidate.isNotEmpty(customField)) {
					String customFieldId = customField.getString("customFieldId");
					GenericValue custRequestAttr = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", srNumber, "attrName", customFieldId).queryFirst();
					
					if(UtilValidate.isNotEmpty(custRequestAttr)) {
						custRequestAttr.put("attrValue", justificationOldProd);
						custRequestAttr.store();
					} else {
						custRequestAttr = delegator.makeValue("CustRequestAttribute");
						
						custRequestAttr.put("custRequestId", srNumber);
						custRequestAttr.put("attrName", customFieldId);
						custRequestAttr.put("attrValue", justificationOldProd);
						custRequestAttr.create();
					}
				}
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully created attribute");
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String updateTimeEntry(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String requestData = DataUtil.getJsonStrBody(request);
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		String selectedRows = request.getParameter("selectedRows");
		String activeTab =  request.getParameter("activeTab");
		
    	
    	try {
    		List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();
			if(UtilValidate.isNotEmpty(selectedRows)) {
				requestMapList = DataUtil.convertToListMap(selectedRows);
			}
			if(UtilValidate.isEmpty(requestMapList)) {
				Map<String, Object> reqData = new HashMap<String, Object>();
				if(UtilValidate.isNotEmpty(request.getParameter("timeEntryId"))) {
					reqData.put("timeEntryId", request.getParameter("timeEntryId"));
					reqData.put("hours", request.getParameter("hours"));
					reqData.put("minutes", request.getParameter("minutes"));
					reqData.put("partyId", request.getParameter("partyId"));
					reqData.put("rateTypeId", request.getParameter("rateTypeId"));
					reqData.put("cost", request.getParameter("cost"));
					reqData.put("timeEntryDate", request.getParameter("timeEntryDate"));
					reqData.put("comments", request.getParameter("comments"));
					
					requestMapList.add(reqData);
    			}
			}
			if(UtilValidate.isNotEmpty(requestMapList)) {
				List<GenericValue> toBeStore = new LinkedList<GenericValue>();
				for(Map<String, Object> requestMap : requestMapList) {
					Map<String,Object> inputMap = FastMap.newInstance();
					String roleTypeId = (String) requestMap.get("roleTypeId");
					String partyId = (String) requestMap.get("partyId");
					String timeEntryId = (String) requestMap.get("timeEntryId");
					String timeEntryDate = (String) requestMap.get("timeEntryDate");
					String modifiedHour = "";
					if(UtilValidate.isNotEmpty(requestMap.get("hours")) && requestMap.get("hours") instanceof Number)
						modifiedHour = String.valueOf(requestMap.get("hours"));
					else if (UtilValidate.isNotEmpty(requestMap.get("hours")) && requestMap.get("hours") instanceof String)
						modifiedHour = (String) requestMap.get("hours");
					
					String modifiedMinutes = "";
					if(UtilValidate.isNotEmpty(requestMap.get("minutes")) && requestMap.get("minutes") instanceof Number)
						modifiedMinutes = String.valueOf(requestMap.get("minutes"));
					else if (UtilValidate.isNotEmpty(requestMap.get("minutes")) && requestMap.get("minutes") instanceof String)
						modifiedMinutes = (String) requestMap.get("minutes");
					
					String modifiedCost = "";
					if(UtilValidate.isNotEmpty(requestMap.get("cost")) && requestMap.get("cost") instanceof Number)
						modifiedCost = String.valueOf(requestMap.get("cost"));
					else if (UtilValidate.isNotEmpty(requestMap.get("cost")) && requestMap.get("cost") instanceof String)
						modifiedCost = (String) requestMap.get("cost");
					
					BigDecimal cost = BigDecimal.ZERO;
					double timeEntered = 0.0d;
					double rate= 0.0d; 
					String rateTypeId = (String) requestMap.get("rateTypeId");
					
					if("TOLL_CHARGE".equals(rateTypeId) || "ANCILLARY_COST".equals(rateTypeId) || "LEGACY_TOLL_CHARGE".equals(rateTypeId) || "LEGACY_ANCILLARY_COST".equals(rateTypeId) ){
						cost = new BigDecimal(modifiedCost);
					} else {
						int hours = 0;
						int minute = 0;
						/*
						if(UtilValidate.isNotEmpty(modifiedHour) && modifiedHour.contains(".")) {
							hours = Integer.parseInt(modifiedHour.substring(0, modifiedHour.indexOf(".")));
							String min = modifiedHour.substring(modifiedHour.indexOf(".")+1);
							min = StringUtils.rightPad(min, 2, "0");
							minute = Integer.parseInt(min);
						} else {
							hours = Integer.parseInt(modifiedHour);
						}
						*/
						if(UtilValidate.isNotEmpty(modifiedHour)) hours = Integer.parseInt(modifiedHour);
						if(UtilValidate.isNotEmpty(modifiedMinutes)) minute = Integer.parseInt(modifiedMinutes);
						
						//Create TimeEntry
						
						if(minute>=60) {
							int hour = (int) (minute/60);
							int remainMinutes = (int) (minute % 60);
							hours = hours + hour;
							minute = remainMinutes;
						}
						
						
						double minHr = (double)minute/(double)60;
						BigDecimal bd = new BigDecimal(hours).add(new BigDecimal(minHr)).setScale(2, RoundingMode.HALF_UP);
						//double timeEntered = Double.parseDouble(hours+"."+minHr);
				        timeEntered = bd.doubleValue();
						
						//BigDecimal cost = BigDecimal.ZERO;
						
						Map<String, Object> rateResult = org.groupfio.common.portal.util.DataUtil.getPartyRate(delegator, UtilMisc.toMap("partyId", partyId, "rateTypeId", rateTypeId));
						if(UtilValidate.isNotEmpty(rateResult) && UtilValidate.isNotEmpty(rateResult.get("rate")))
							rate = (double) rateResult.get("rate");
						
						cost = new BigDecimal((timeEntered * rate), MathContext.DECIMAL64);
					}
						
					
					GenericValue timeEntry = EntityQuery.use(delegator).from("TimeEntry").where("timeEntryId", timeEntryId).filterByDate().queryFirst();
					if(UtilValidate.isNotEmpty(timeEntry)) {
						timeEntry.setNonPKFields(requestMap);
						timeEntry.put("hours", timeEntered);
						timeEntry.put("cost", cost);
						timeEntry.put("ratePerHour", new BigDecimal(rate));
						timeEntry.put("timeEntryDate", UtilValidate.isNotEmpty(timeEntryDate) ? Timestamp.valueOf(df1.format(df.parse(timeEntryDate))+" 00:00:00") : UtilDateTime.nowTimestamp() );
						timeEntry.store();
						//toBeStore.add(timeEntry);
					}
				}
				/*
				 * if(toBeStore.size() > 0) delegator.storeAll(toBeStore);
				 */
			}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e, MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
    	}
    	result.put(GlobalConstants.RESPONSE_MESSAGE, "Time entry successfully updated");
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
    	return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String getTimeEntry(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String requestData = DataUtil.getJsonStrBody(request);
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String timeEntryId = request.getParameter("timeEntryId");
		
    	try {
			if(UtilValidate.isNotEmpty(timeEntryId)) {
				
				GenericValue timeEntry = EntityQuery.use(delegator).from("TimeEntry").where("timeEntryId", timeEntryId).filterByDate().queryFirst();
				if(UtilValidate.isNotEmpty(timeEntry)) {
					String hourStr = timeEntry.getString("hours");
					if(UtilValidate.isNotEmpty(hourStr) && hourStr.contains(".")) {
						int hours = Integer.parseInt(hourStr.substring(0, hourStr.indexOf(".")));
						String min = hourStr.substring(hourStr.indexOf(".")+1);
						min = StringUtils.rightPad(min, 2, "0");
						float minute = Float.parseFloat("0."+min);
						
						int minVal = (int) (minute*60);
						
						
						result.put("logHour", hours);
						result.put("logMinute", minVal);
					}
					
					result.putAll(DataUtil.convertGenericValueToMap(delegator, timeEntry));
					result.put("technicianName", org.fio.homeapps.util.DataUtil.getPartyName(delegator, timeEntry.getString("partyId")));
					
					GenericValue workEffortGv = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", timeEntry.getString("workEffortId")).queryFirst();
					
					result.put("actualStartDate", UtilValidate.isNotEmpty(workEffortGv.getTimestamp("actualStartDate")) ? df.format(workEffortGv.getTimestamp("actualStartDate")) : df.format(UtilDateTime.nowTimestamp()));
					result.put("actualCompletionDate", UtilValidate.isNotEmpty(workEffortGv.getTimestamp("actualCompletionDate")) ? df.format(workEffortGv.getTimestamp("actualCompletionDate")) : df.format(UtilDateTime.nowTimestamp()));
						
					result.put("activityName", UtilValidate.isNotEmpty(workEffortGv) ? workEffortGv.getString("workEffortName") : "");
					
					result.put("timeEntredDate", df.format(df1.parse(timeEntry.getString("timeEntryDate"))));
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				}
			}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e, MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
    	}
    	result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully created attribute");
    	return AjaxEvents.doJSONResponse(response, result);
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
		String realCoordinator = (String) context.get("realCoordinator");
		String storeGroupId = (String) context.get("storeGroupId");
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
				
				if (UtilValidate.isNotEmpty(storeGroupId)) {
					srLocation = "999999";
					List<String> locationIdList = SrUtil.getStoreLocationIds(delegator, storeGroupId);
					if (UtilValidate.isNotEmpty(locationIdList)) {
						srLocation = StringUtil.join(locationIdList, ",");
					}
				}
				
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
							
							dynamicView.addMemberEntity("CRP", "CustRequestParty");
							dynamicView.addAlias("CRP", "partyId");
							dynamicView.addAlias("CRP", "roleTypeId");
							dynamicView.addAlias("CRP", "fromDate");
							dynamicView.addAlias("CRP", "thruDate");
							dynamicView.addViewLink("CR", "CRP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
							
							EntityCondition cond1 = EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginPartyId),
									EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList(loggedUserRole)),
									EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr())
									);
							dynamicView.addMemberEntity("CRWE", "CustRequestWorkEffort");
							dynamicView.addAlias("CRWE", "workEffortId");
							dynamicView.addViewLink("CR", "CRWE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

							dynamicView.addMemberEntity("WE", "WorkEffort");
							dynamicView.addAlias("WE", "currentStatusId");
							dynamicView.addViewLink("CRWE", "WE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));

							dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
							dynamicView.addAlias("WEPA", "techPartyId", "partyId", "", null, null, "");
							dynamicView.addAlias("WEPA", "techRoleTypeId", "roleTypeId", "", null, null, "");
							dynamicView.addAlias("WEPA", "ownerFromDate", "fromDate", "", null, null, "");
							dynamicView.addAlias("WEPA", "ownerThruDate", "thruDate", "", null, null, "");
							dynamicView.addViewLink("WE", "WEPA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));

							EntityCondition cond2 = EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_IN, UtilMisc.toList("IA_MCOMPLETED","IA_COMPLETED")),
									EntityCondition.makeCondition("techPartyId", EntityOperator.EQUALS, userLoginPartyId),
									EntityCondition.makeCondition("techRoleTypeId", EntityOperator.IN, UtilMisc.toList(loggedUserRole)),
									EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr("ownerFromDate", "ownerThruDate"))
									);
							conditions.add(EntityCondition.makeCondition(UtilMisc.toList(cond1,cond2),EntityOperator.OR));
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
											EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList(loggedUserRole)),
											EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr())
											));
								}
							} else {
								
								if("sr-open".equals(filterBy)) {
									conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginPartyId),
											EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList(loggedUserRole)),
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
											EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList(loggedUserRole)),
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
				} else if("my-backup-sr".equals(filterType)) {
					
					if(UtilValidate.isNotEmpty(realCoordinator)) {
						String realCoordinatorPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
						String realCoordinatorRole = DataUtil.getPartySecurityRole(delegator, realCoordinatorPartyId);
						List<String> coordinatorRoles = new ArrayList<>();
						String csrRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "COORDINATOR_ROLE", "CUST_SERVICE_REP");
						if(UtilValidate.isNotEmpty(csrRole) && csrRole.contains(",")) {
							coordinatorRoles = org.fio.admin.portal.util.DataUtil.stringToList(csrRole, ",");
						} else if(UtilValidate.isNotEmpty(csrRole)) {
							coordinatorRoles.add(csrRole);
						}
						if(coordinatorRoles.contains(realCoordinatorRole))
							conditions.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, realCoordinator));
						else
							conditions.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, realCoordinator));	
					}
				} 
				
				if(UtilValidate.isNotEmpty(srLocation)) {
					dynamicView.addMemberEntity("CRA", "CustRequestAttribute");
					dynamicView.addAlias("CRA", "attrName");
					dynamicView.addAlias("CRA", "attrValue");
					dynamicView.addViewLink("CR", "CRA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("custRequestId"));
					
					String locationCustomFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);
					conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, locationCustomFieldId),
								EntityCondition.makeCondition("attrValue", EntityOperator.IN, StringUtil.split(srLocation, ","))
							));
				}
				
				//conditions.add(EntityCondition.makeCondition("crpPartyId", EntityOperator.EQUALS, userLoginPartyId));
				List<String> notOpenStatusList = UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_WRK_COMPL","SR_WRK_COMPL_REEB_TECH");
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
				} else if("sr-on-hold".equals(filterBy)) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_HOLD_PER_SR_PRVDER","SR_HOLD_PER_TSM","SR_HOLD_UNTL_SPRNG","SR_PENDING","SR_HOLD_SPPLY_CHAIN")));
				} else if("sr-received".equals(filterBy)) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_RECEIVED")));
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
						} else if("my-backup-sr".equals(filterType)) {
							String realCoordinatorPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, realCoordinator);
							requestContext.put("partyId", realCoordinatorPartyId);
							
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
					
					Map<String, Object> stateMap = org.fio.admin.portal.util.DataUtil.getGeoNameList(delegator, "STATE/PROVINCE");
					
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
					String primaryTechCustomFieldId = DataHelper.getCustomFieldId(delegator, "ANCHOR_ROLES", "ANR_TECHNICIAN");
					
					List custRequestIds = EntityUtil.getFieldListFromEntityList(resultList, "custRequestId", true);
					
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("custRequestId",EntityOperator.IN,custRequestIds));
					conditionList.add(EntityCondition.makeCondition("custRequestTypeId","REASON_CODE"));
					EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					List<GenericValue> custRequestResolution = delegator.findList("CustRequestResolution", cond,
							UtilMisc.toSet("custRequestId","custRequestTypeId","description"), null, null, false);
					
					Map <String, Object> reasonCodeMap= org.fio.homeapps.util.DataUtil.getMapFromGeneric(custRequestResolution, "custRequestId", "description", false);
					
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("custRequestId",EntityOperator.IN,custRequestIds));
					conditionList.add(EntityCondition.makeCondition("custRequestTypeId","CAUSE_CATEGORY"));
					cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					custRequestResolution = delegator.findList("CustRequestResolution", cond,
							UtilMisc.toSet("custRequestId","custRequestTypeId","description"), null, null, false);
					
					Map <String, Object> causeCaategoryMap= org.fio.homeapps.util.DataUtil.getMapFromGeneric(custRequestResolution, "custRequestId", "description", false);
					
					String isEnabledStaTat = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_ENABLE","N");
					String slaTatStopStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_STOP_STATUS");
					String slaTatPauseStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_PAUSE_STATUS");
					
					List holidays = FastList.newInstance();
					conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition("status", EntityOperator.EQUALS, null),
									EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ACTIVE"))
			                	);
					List<GenericValue> holidayConfigList = EntityQuery.use(delegator).from("TechDataHolidayConfig").where(EntityCondition.makeCondition(conditionList, EntityOperator.AND)).distinct(true).cache(true).queryList();
			    	if(UtilValidate.isNotEmpty(holidayConfigList)) {
			    		for(GenericValue holidayConfig : holidayConfigList) {
			    			java.sql.Date holidayDate = holidayConfig.getDate("holidayDate");
			    			holidays.add(new Timestamp(holidayDate.getTime()));
			    		}
			    	}
					
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
						
						String primAttrValue = SrUtil.getCustRequestAttrValue(delegator, primaryTechCustomFieldId, custRequestId);
						String primAttrValueName = partyNames.containsKey(primAttrValue) ? (String) partyNames.get(primAttrValue) : PartyHelper.getPersonName(delegator, primAttrValue, false);
						primAttrValueName = UtilValidate.isNotEmpty(primAttrValueName)?primAttrValueName:primAttrValue;
						data.put("primaryTechnicain", primAttrValueName);
						
						data.put("domainEntityId", custRequestId);
						data.put("domainEntityType", DomainEntityType.SERVICE_REQUEST);
						data.put("domainEntityTypeDesc", org.groupfio.common.portal.util.DataHelper.convertToLabel( DomainEntityType.SERVICE_REQUEST ));
						data.put("externalLoginKey", externalLoginKey);	
						data.put("scheduledDate", workOrderSchList.get(custRequestId));
						
						data.put("reasonCode", UtilValidate.isNotEmpty(reasonCodeMap)?reasonCodeMap.get(custRequestId):"");
						data.put("causeCategory", UtilValidate.isNotEmpty(causeCaategoryMap)?causeCaategoryMap.get(custRequestId):"");
						
						int slaTat =0;
						if (isEnabledStaTat.equals("Y")) {
							/*List slaTatStopStatusList = org.fio.homeapps.util.DataUtil.stringToList(slaTatStopStatus, ",");
							if (UtilValidate.isNotEmpty(serviceRequest.getString("statusId")) && slaTatStopStatusList.contains(serviceRequest.getString("statusId"))) {
								GenericValue slaTatInfo = EntityQuery.use(delegator).from("CustRequestAttribute").where("attrName", "SLA_TAT", "custRequestId", custRequestId).queryFirst();
								if (UtilValidate.isNotEmpty(slaTatInfo) && UtilValidate.isNotEmpty(slaTatInfo.getString("attrValue"))) {
									double slaTatValue = Double.parseDouble(slaTatInfo.getString("attrValue"));
									slaTat = (int) slaTatValue;
								}
							}else {
								Map inpCxt = UtilMisc.toMap("delegator",delegator,"closedDate",org.fio.homeapps.util.UtilDateTime.nowTimestamp(),"custRequestId", custRequestId,"srStatuId",serviceRequest.getString("statusId"));
								inpCxt.put("slaTatPauseStatus", slaTatPauseStatus);
								inpCxt.put("slaTatStopStatus", slaTatStopStatus);
								inpCxt.put("holidays", holidays);
								int tatDays = org.groupfio.common.portal.util.DataHelper.prepareTatToDate(inpCxt);
								slaTat = tatDays;
							}*/
							
							String srTatCount = org.groupfio.common.portal.util.DataHelper.getSrTatCount(delegator, custRequestId, holidays);
							if (UtilValidate.isNotEmpty(srTatCount)) {
								slaTat = Integer.parseInt(srTatCount);
							}
						}
						data.put("slaTat", slaTat);
						String pstlPostalCity = serviceRequest.getString("pstlPostalCity");
						data.put("city", pstlPostalCity);
						
						String pstlStateProvinceGeoId = serviceRequest.getString("pstlStateProvinceGeoId");
						String pstlStateProvinceGeoName = "";
						if (UtilValidate.isNotEmpty(pstlStateProvinceGeoId)) {
							//pstlStateProvinceGeoName = org.fio.homeapps.util.DataUtil.getGeoName(delegator, pstlStateProvinceGeoId, "STATE");
							pstlStateProvinceGeoName = (String) stateMap.get(pstlStateProvinceGeoId);
						}
						data.put("state", UtilValidate.isNotEmpty(pstlStateProvinceGeoName)?pstlStateProvinceGeoName:pstlStateProvinceGeoId);
						dataList.add(data);
					}
	            	Debug.logInfo("prepare actual data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
	            	
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
		String storeGroupId = (String) context.get("storeGroupId");
		String partyId = (String) context.get("partyId");
		String clientPortal = (String) context.get("clientPortal");
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
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			List<String> statusIds = new ArrayList<>();
			
			if((UtilValidate.isNotEmpty(filterType) || UtilValidate.isNotEmpty(filterBy))  && UtilValidate.isNotEmpty(userLoginId)) {
				SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
				Connection con = (Connection)sqlProcessor.getConnection();
				CallableStatement cstmt = null;
				
				String barType = "";
				String count = "";
				srLocation = UtilValidate.isNotEmpty(srLocation) ? srLocation : "ALL";
				if (UtilValidate.isNotEmpty(storeGroupId)) {
					srLocation = "999999";
					List<String> locationIdList = SrUtil.getStoreLocationIds(delegator, storeGroupId);
					if (UtilValidate.isNotEmpty(locationIdList)) {
						srLocation = StringUtil.join(locationIdList, ",");
					}
				}
				
				if("my-sr".equals(filterType)) {
					List<String> notOpenStatusList = UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_WRK_COMPL","SR_WRK_COMPL_REEB_TECH");
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
							/*sqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'Open'"
									+ " FROM CUST_REQUEST CR "
									+ " LEFT OUTER JOIN CUST_REQUEST_SUPPLEMENTORY CRS ON CR.CUST_REQUEST_ID = CRS.CUST_REQUEST_ID"
									+ " INNER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON CR.CUST_REQUEST_ID = CRWE.CUST_REQUEST_ID"
									+ " INNER JOIN WORK_EFFORT WE ON CRWE.WORK_EFFORT_ID = WE.WORK_EFFORT_ID"
									+ " INNER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID"
									+ " WHERE WE.CURRENT_STATUS_ID NOT IN ('IA_MCOMPLETED', 'IA_COMPLETED') AND WEPA.PARTY_ID = '"+userLoginPartyId+"'"
									+ " AND WEPA.ROLE_TYPE_ID IN ('"+loggedUserRole+"') AND (WEPA.THRU_DATE IS NULL OR WEPA.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
									+ " AND (WEPA.FROM_DATE IS NULL OR WEPA.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')"
									+ " AND CR.STATUS_ID NOT IN ("+DataUtil.toList(notOpenStatusList,"")+")";*/
							
							// RC-1993 By Nishanth
							sqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'Open'"
									+ " FROM CUST_REQUEST CR "
									+  " LEFT JOIN cust_request_party CRP ON CRP.CUST_REQUEST_ID = CR.CUST_REQUEST_ID AND CRP.ROLE_TYPE_ID ='TECHNICIAN'AND CRP.PARTY_ID = '"+userLoginPartyId+"'"
									+  " AND (CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"') "
									+ " LEFT OUTER JOIN CUST_REQUEST_SUPPLEMENTORY CRS ON CR.CUST_REQUEST_ID = CRS.CUST_REQUEST_ID"
									+ " LEFT JOIN CUST_REQUEST_WORK_EFFORT CRWE ON CR.CUST_REQUEST_ID = CRWE.CUST_REQUEST_ID"
									+ " LEFT JOIN WORK_EFFORT WE ON CRWE.WORK_EFFORT_ID = WE.WORK_EFFORT_ID AND WE.CURRENT_STATUS_ID NOT IN ('IA_MCOMPLETED', 'IA_COMPLETED')"
									+ " LEFT JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID AND WEPA.PARTY_ID = '"+userLoginPartyId+"'"
									+ " AND WEPA.ROLE_TYPE_ID IN ('"+loggedUserRole+"') AND (WEPA.THRU_DATE IS NULL OR WEPA.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
									+ " AND (WEPA.FROM_DATE IS NULL OR WEPA.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')"
									+ " WHERE "
									+ " CR.STATUS_ID NOT IN ("+DataUtil.toList(notOpenStatusList,"")+")"
									+ " AND (CRP.CUST_REQUEST_ID IS NOT NULL OR WEPA.work_effort_id IS NOT NULL)";
							Debug.log("-----sqlQuery--------"+sqlQuery);
						/*	schedSqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'SentTechSched'"
									+ " FROM CUST_REQUEST CR "
									+ " LEFT OUTER JOIN CUST_REQUEST_SUPPLEMENTORY CRS ON CR.CUST_REQUEST_ID = CRS.CUST_REQUEST_ID"
									+ " INNER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON CR.CUST_REQUEST_ID = CRWE.CUST_REQUEST_ID"
									+ " INNER JOIN WORK_EFFORT WE ON CRWE.WORK_EFFORT_ID = WE.WORK_EFFORT_ID"
									+ " INNER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID"
									+ " WHERE WE.CURRENT_STATUS_ID NOT IN ('IA_MCOMPLETED', 'IA_COMPLETED') AND WEPA.PARTY_ID = '"+userLoginPartyId+"'"
									+ " AND WEPA.ROLE_TYPE_ID IN ('"+loggedUserRole+"') AND (WEPA.THRU_DATE IS NULL OR WEPA.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
									+ " AND (WEPA.FROM_DATE IS NULL OR WEPA.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')"
									+ " AND CR.STATUS_ID IN ('"+sentToTechSchld+"')";*/
							
							// RC-1993 By Nishanth
							schedSqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'SentTechSched'"
									+ " FROM CUST_REQUEST CR "
									+  " LEFT JOIN cust_request_party CRP ON CRP.CUST_REQUEST_ID = CR.CUST_REQUEST_ID AND CRP.ROLE_TYPE_ID ='TECHNICIAN'AND CRP.PARTY_ID = '"+userLoginPartyId+"'"
									+  " AND (CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"') "
									+ " LEFT OUTER JOIN CUST_REQUEST_SUPPLEMENTORY CRS ON CR.CUST_REQUEST_ID = CRS.CUST_REQUEST_ID"
									+ " LEFT JOIN CUST_REQUEST_WORK_EFFORT CRWE ON CR.CUST_REQUEST_ID = CRWE.CUST_REQUEST_ID"
									+ " LEFT JOIN WORK_EFFORT WE ON CRWE.WORK_EFFORT_ID = WE.WORK_EFFORT_ID AND WE.CURRENT_STATUS_ID NOT IN ('IA_MCOMPLETED', 'IA_COMPLETED') "
									+ " LEFT JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID"
									+ " AND WEPA.PARTY_ID = '"+userLoginPartyId+"'"
									+ " AND WEPA.ROLE_TYPE_ID IN ('"+loggedUserRole+"') AND (WEPA.THRU_DATE IS NULL OR WEPA.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
									+ " AND (WEPA.FROM_DATE IS NULL OR WEPA.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')"
									+ " WHERE CR.STATUS_ID IN ('"+sentToTechSchld+"')"
									+ " AND (CRP.CUST_REQUEST_ID IS NOT NULL OR WEPA.work_effort_id IS NOT NULL)";
							Debug.log("-----schedSqlQuery--------"+schedSqlQuery);
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
											+" WHERE CRP.party_id='"+userLoginPartyId+"' AND CRP.ROLE_TYPE_ID IN ('"+loggedUserRole+"')" 
											+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
											+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
											+" AND status_id NOT IN ("+DataUtil.toList(notOpenStatusList,"")+")";

									schedSqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'SentTechSched' FROM cust_request CR "
											+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
											+" WHERE CRP.party_id='"+userLoginPartyId+"' AND CRP.ROLE_TYPE_ID IN ('"+loggedUserRole+"')" 
											+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
											+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
											+" AND status_id IN ('"+sentToTechSchld+"')";
									
									myCompReqSqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'My_Company_Open' FROM cust_request CR "
											+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
											+" WHERE CRP.party_id='"+userLoginPartyId+"' AND CRP.ROLE_TYPE_ID IN ('"+loggedUserRole+"')" 
											+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
											+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
											+" AND status_id NOT IN ("+DataUtil.toList(notOpenStatusList,"")+")";
								}
							} else {
								
								sqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'Open' FROM cust_request CR "
										+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
										+" WHERE CRP.party_id='"+userLoginPartyId+"' AND CRP.ROLE_TYPE_ID IN ('"+loggedUserRole+"')" 
										+" AND ((CRP.THRU_DATE IS NULL OR CRP.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"')"
										+" AND (CRP.FROM_DATE IS NULL OR CRP.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"')) "
										+" AND status_id NOT IN ("+DataUtil.toList(notOpenStatusList,"")+")";

								schedSqlQuery = "SELECT COUNT(DISTINCT CR.CUST_REQUEST_ID),'SentTechSched' FROM cust_request CR "
										+" INNER JOIN cust_request_party CRP ON CR.CUST_REQUEST_ID = CRP.CUST_REQUEST_ID "
										+" WHERE CRP.party_id='"+userLoginPartyId+"' AND CRP.ROLE_TYPE_ID IN ('"+loggedUserRole+"')" 
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
				} else if("my-backup-sr".equals(filterType)) {
					cstmt = con.prepareCall("{call kpi_backup_request_level(?,?)}");
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
					String loginId = userLoginId;
					if("my-backup-sr".equals(filterType)) {
						String realCoordinator = (String) context.get("realCoordinator");
						loginId = UtilValidate.isNotEmpty(realCoordinator) ? realCoordinator :"";
					}
					
					Map<String, Object> kpiCount = new LinkedHashMap<>();
					
					for (String locationId : srLocation.split(",")) {
						cstmt.setString(1, loginId);
						cstmt.setString(2, locationId);
						rs = cstmt.executeQuery();
						
						if(rs !=null){
							try{ 
								int i = 0;
								while (rs.next()) {
									count = rs.getString(1);
									barType = rs.getString(2);
									
									long finalCount = Long.parseLong(count);
									if (kpiCount.containsKey(barType)) {
										finalCount += Long.parseLong(""+kpiCount.get(barType));
									}
									kpiCount.put(barType, ""+finalCount);
								}
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					if (UtilValidate.isNotEmpty(kpiCount)) {
						for (String bt : kpiCount.keySet()) {
							Map<String, Object> data = new HashMap<String, Object>();
							data.put("barId", bt);
							data.put("count", kpiCount.get(bt));
							dataList.add(data);
						}
					}
					
					/*cstmt.setString(1, loginId);
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
					}*/
					
					
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
	
	public static String updateSrClosedDate(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> results = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String custRequestId = (String) context.get("custRequestId");
		String srClosedDate = (String) context.get("srClosedDate");
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
    	Timestamp closedByDate = null;
    	SimpleDateFormat sdf = new SimpleDateFormat(globalDateFormat);
		try {
			if (UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(srClosedDate)) {
				closedByDate = new Timestamp(sdf.parse(srClosedDate).getTime());
				GenericValue custRequest = EntityUtil.getFirst(delegator.findByAnd("CustRequest",
						UtilMisc.toMap("custRequestId", custRequestId), null, false));

				if (UtilValidate.isNotEmpty(custRequest)) {
					custRequest.put("closedByDate", closedByDate);
					custRequest.put("closedByUserLogin", userLogin.getString("userLoginId"));
					custRequest.put("lastModifiedDate", UtilDateTime.nowTimestamp());
					custRequest.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
					custRequest.store();
					
					
					
					Map inpCxt = FastMap.newInstance();
					Map callCxt = FastMap.newInstance();
					callCxt.put("userLogin", userLogin);
					callCxt.put("custRequestId", custRequestId);
					String loggedUserName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, userLogin.getString("partyId"), false);
					loggedUserName = UtilValidate.isNotEmpty(loggedUserName)?loggedUserName:userLogin.getString("partyId");
					java.sql.Date today = new java.sql.Date(UtilDateTime.nowTimestamp().getTime());
					String comment = "Closed date is changed by "+loggedUserName+" on "+today+"";
					inpCxt.put("comment", comment);
					callCxt.put("contextMap", inpCxt);
					Map<String, Object> hstRes =  dispatcher.runSync("srPortal.createSrHistory", callCxt);
					if (ServiceUtil.isError(hstRes)) {
						results.put("status", "error");
						results.put("message", "Error updating sr history "+hstRes.get("errorMessage"));
						//return doJSONResponse(response, results);
					}
					
					String isEnabledStaTat = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_ENABLE","N");
					String slaTatStopStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_STOP_STATUS", "SR_CLOSED");
					List slaTatStopList = DataUtil.stringToList(slaTatStopStatus, ",");
					if (isEnabledStaTat.equalsIgnoreCase("Y") && 
							UtilValidate.isNotEmpty(custRequest.getString("statusId")) && slaTatStopList.contains(custRequest.getString("statusId"))) {
						
						Map<String, Object> tatContext = new LinkedHashMap<String, Object>();
						tatContext.put("delegator", delegator);
						tatContext.put("custRequestId", custRequestId);
						tatContext.put("tatCalc", isEnabledStaTat);
						tatContext.put("businessUnit", custRequest.getString("ownerBu"));
						Timestamp createdDate = custRequest.getTimestamp("createdDate");
						tatContext.put("createdDate", createdDate);
						
						Debug.log("closedDate before========" + closedByDate);
						tatContext.put("closedDate", UtilValidate.isNotEmpty(closedByDate) ? closedByDate : UtilDateTime.nowTimestamp());
						Debug.log("closedDate after========" + tatContext.get("closedDate"));
						tatContext.put("statusId", custRequest.getString("statusId"));
						tatContext.put("isTatReComputeByClosedDate", "Y");
						
						custRequest.put("closedByDate", tatContext.get("closedDate"));
						
						Resolver resolver = ResolverFactory.getResolver(ResolverType.SLA_TAT_RESOLVER);
						Map<String, Object> tatResult = resolver.resolve(tatContext);
						Long slaTatDays = new Long(0);
						if (ResponseUtils.isSuccess(tatResult)) {
							slaTatDays = UtilValidate.isNotEmpty(ParamUtil.getBigDecimal(tatResult, "tatDays"))?ParamUtil.getLong(tatResult, "tatDays"):new Long(0);
							Debug.logInfo("updateSrClosedDate slaTatDays> "+slaTatDays, MODULE);
							UtilAttribute.storeServiceAttrValue(delegator, custRequestId, "SLA_TAT", ""+slaTatDays);
						}else {
							Debug.log("Error Sla Tat calculation=="+ResponseUtils.isError(tatResult));
						}
					}
					
				}
			}else {
				results.put("status", "error");
				results.put("message", "Cust request id or closed date missing");
				return doJSONResponse(response, results);
			}
			results.put("status", "success");
			results.put("message", "Closed date updated successfully");
		} catch (Exception e) {
			Debug.logError(e, "Exception :  " + e.getMessage(), MODULE);
			results.put("status", "error");
			results.put("message", e.getMessage());
		}
		return doJSONResponse(response, results);
	}
	
	public static String findSrPaymentsList(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> results = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String srNumber = (String) context.get("srNumber");
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
    	Timestamp closedByDate = null;
    	SimpleDateFormat sdf = new SimpleDateFormat(globalDateFormat);
    	List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			if (UtilValidate.isNotEmpty(srNumber)) {
				List<GenericValue> issueMaterial = delegator.findByAnd("IssueMaterial",
						UtilMisc.toMap("custRequestId", srNumber), null, false);

				if (UtilValidate.isNotEmpty(issueMaterial)) {
					List invoices = EntityUtil.getFieldListFromEntityList(issueMaterial, "invoiceId", true);
					if (UtilValidate.isNotEmpty(invoices)) {
						List condList = FastList.newInstance();
						
						DynamicViewEntity dynamicEntity = new DynamicViewEntity();
						dynamicEntity.addMemberEntity("INV", "Invoice");
						dynamicEntity.addAlias("INV", "invoiceId");
						dynamicEntity.addAlias("INV", "invoiceTypeId");
						dynamicEntity.addAlias("INV", "partyIdFrom");
						dynamicEntity.addAlias("INV", "partyId");
						dynamicEntity.addAlias("INV", "invoiceStatus","statusId",null, false,false,null);
						dynamicEntity.addAlias("INV", "invoiceDate");
						dynamicEntity.addAlias("INV", "createdStamp");
						
						dynamicEntity.addMemberEntity("PA", "PaymentApplication");
						dynamicEntity.addAlias("PA", "paymentId");
						dynamicEntity.addViewLink("INV", "PA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("invoiceId"));
						
						
						dynamicEntity.addMemberEntity("PMT", "Payment");
						dynamicEntity.addAlias("PMT", "paymentTypeId");
						dynamicEntity.addAlias("PMT", "paymentMethodTypeId");
						dynamicEntity.addAlias("PMT", "paymentMethodId");
						dynamicEntity.addAlias("PMT", "paymentPartyIdFrom","partyIdFrom",null, false,false,null);
						dynamicEntity.addAlias("PMT", "partyIdTo");
						dynamicEntity.addAlias("PMT", "paymentStatus","statusId",null, false,false,null);
						dynamicEntity.addAlias("PMT", "paidAmount","amount",null, false,false,null);
						dynamicEntity.addAlias("PMT", "paymentRefNum");
						dynamicEntity.addAlias("PMT", "effectiveDate");
						
						dynamicEntity.addViewLink("PA", "PMT", Boolean.FALSE, ModelKeyMap.makeKeyMapList("paymentId"));
						
						condList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,invoices));
						EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
						
						
						Set<String> fieldsToSelect = new TreeSet<String>();
						
						fieldsToSelect.add("invoiceId");
						fieldsToSelect.add("invoiceTypeId");
						fieldsToSelect.add("partyIdFrom");
						fieldsToSelect.add("partyId");
						fieldsToSelect.add("invoiceStatus");
						fieldsToSelect.add("invoiceDate");
						fieldsToSelect.add("createdStamp");
						fieldsToSelect.add("paymentId");
						fieldsToSelect.add("paymentTypeId");
						fieldsToSelect.add("paymentMethodTypeId");
						fieldsToSelect.add("paymentMethodId");
						fieldsToSelect.add("paymentPartyIdFrom");
						fieldsToSelect.add("partyIdTo");
						
						fieldsToSelect.add("paymentStatus");
						fieldsToSelect.add("paidAmount");
						fieldsToSelect.add("paymentRefNum");
						fieldsToSelect.add("effectiveDate");
						List<GenericValue> paymentInvoiceList = EntityQuery.use(delegator).select(fieldsToSelect).from(dynamicEntity).where(cond).orderBy("createdStamp DESC").queryList();
						if (UtilValidate.isNotEmpty(paymentInvoiceList)) {
							List<GenericValue> paymentTypes = delegator.findList("PaymentType", null, UtilMisc.toSet("paymentTypeId","description"), null, null, true);
							Map paymentTypesMap = org.fio.admin.portal.util.DataUtil.getMapFromGeneric(paymentTypes, "paymentTypeId", "description", false);
							
							for (GenericValue invInfo : paymentInvoiceList) {
								Map<String, Object> data = new HashMap<String, Object>();
								data.put("invoiceId", invInfo.getString("invoiceId"));
								data.put("paymentId", invInfo.getString("paymentId"));
								
								String paymentTypeId = invInfo.getString("paymentTypeId");
								paymentTypeId = UtilValidate.isNotEmpty(paymentTypesMap) && UtilValidate.isNotEmpty(paymentTypesMap.get(paymentTypeId))
										?(String) paymentTypesMap.get(paymentTypeId):paymentTypeId;
								data.put("paymentType", paymentTypeId);
								
								data.put("customerId", invInfo.getString("paymentPartyIdFrom"));
								data.put("customerName", PartyHelper.getPartyName(delegator, invInfo.getString("paymentPartyIdFrom"), false));
								data.put("refNumber", invInfo.getString("paymentRefNum"));
								data.put("invoiceAmount", InvoiceWorker.getInvoiceTotal(delegator, invInfo.getString("invoiceId")));
								data.put("paymentAmount", invInfo.getString("paidAmount"));
								
								String pmtStatusItemDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, invInfo.getString("invoiceStatus"),"PMNT_STATUS");
								String paymentStatus = UtilValidate.isNotEmpty(pmtStatusItemDesc)?pmtStatusItemDesc:invInfo.getString("invoiceStatus");
								
								data.put("paymentStatus", paymentStatus);
								
								String invStatusItemDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, invInfo.getString("invoiceStatus"),"INVOICE_STATUS");
								String invoiceStatus = UtilValidate.isNotEmpty(invStatusItemDesc)?invStatusItemDesc:invInfo.getString("invoiceStatus");
								
								data.put("invoiceStatus", invoiceStatus);
								
								dataList.add(data);
							}
						}
					}
					
				}
			}
		} catch (Exception e) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("errorMessage", e.getMessage());
			data.put("errorResult", new ArrayList<Map<String, Object>>());
			dataList.add(data);
		}
		return doJSONResponse(response, dataList);
}
}