import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.party.PartyHelper;
import org.fio.homeapps.event.AjaxEvents;

import java.util.HashMap;
import java.util.List;

import org.ofbiz.entity.condition.EntityExpr;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;

import javolution.util.FastList;
import org.fio.homeapps.constants.UserAuditConstants;

String statusId = request.getParameter("statusId");
String modeOfAction = request.getParameter("modeOfAction");
String makerPartyId = request.getParameter("makerPartyId");
String chekerPartyId = request.getParameter("chekerPartyId");
String fromDate = request.getParameter("fromDate");
String thruDate = request.getParameter("thruDate");
String requestType = request.getParameter("requestType");

searchCondition = parameters.get("searchCondition");
searchConditionOpertor = parameters.get("searchConditionOpertor");
searchValue = parameters.get("searchValue");

String draw = request.getParameter("draw");
String start = request.getParameter("start");
String length = request.getParameter("length");
String viewType = request.getParameter("viewType");
userLogin = session.getAttribute("userLogin");
userLoginId = "";
if (userLogin != null) {
    userLoginId = userLogin.getString("userLoginId");
}

Debug.logInfo("viewType>>> "+viewType, "viewType");

String sortDir = "desc";
String orderField = "";
String orderColumnId = request.getParameter("order[0][column]");
if(UtilValidate.isNotEmpty(orderColumnId)) {
	int sortColumnId = Integer.parseInt(orderColumnId);
	String sortColumnName = request.getParameter("columns["+sortColumnId+"][data]");
	sortDir = request.getParameter("order[0][dir]").toUpperCase();
	orderField = sortColumnName;
} else {
	orderField = "createdStamp";
}

sortDir = "";
orderField = "createdStamp";

Debug.logInfo("orderField>>> "+orderField, "ListPendingRequest");

security = request.getAttribute("security");

delegator = request.getAttribute("delegator");

isMaker = security.hasPermission("DBS_ADMPR_MAKER", userLogin);
isCheker = security.hasPermission("DBS_ADMPR_CHEKER", userLogin);

println("isMaker> "+isMaker);
println("isCheker> "+isCheker);

conditionsList = FastList.newInstance();

//conditionsList.add(EntityUtil.getFilterByDateExpr());

if (UtilValidate.isNotEmpty(statusId)) {
	EntityCondition partyCondition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId);
	conditionsList.add(partyCondition);
} else {
	EntityCondition statusCondition = EntityCondition.makeCondition([
	           EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null),
	           EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, UserAuditConstants.ApprovalStatus.PENDING)
	           ], EntityOperator.OR);

	conditionsList.add(statusCondition);
}

if (isMaker && !isCheker) {
	conditionsList.add(
				EntityCondition.makeCondition([
        	           EntityCondition.makeCondition("makerPartyId", EntityOperator.EQUALS, userLoginId)
        	           ], EntityOperator.OR)
			);
} else if (!isMaker && !isCheker) {
	conditionsList.add(
				EntityCondition.makeCondition([
        	           EntityCondition.makeCondition("makerPartyId", EntityOperator.EQUALS, "999999")
        	           ], EntityOperator.OR)
			);
}	

SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS");
if (UtilValidate.isNotEmpty(fromDate)) {
	fromdateVal=fromDate+" "+"00:00:00.0";
	Date parsedDateFrom = dateFormat.parse(fromdateVal);
	Timestamp fromTimestamp = new java.sql.Timestamp(parsedDateFrom.getTime());	
	conditionsList.addAll( UtilMisc.toList( new EntityExpr( "createdStamp" , EntityOperator.NOT_EQUAL , null ) , new EntityExpr( "createdStamp" , EntityOperator.GREATER_THAN_EQUAL_TO , fromTimestamp ) ) );
}
if (UtilValidate.isNotEmpty(thruDate)) { 
	thruDateVal=thruDate+" "+"23:23:59.0";
	Date parsedDateThru = dateFormat.parse(thruDateVal);
	Timestamp thruDateTimestamp = new java.sql.Timestamp(parsedDateThru.getTime());	
	conditionsList.addAll( UtilMisc.toList( new EntityExpr( "createdStamp" , EntityOperator.NOT_EQUAL , null ) , new EntityExpr( "createdStamp" , EntityOperator.LESS_THAN_EQUAL_TO , thruDateTimestamp ) ) );
}

if (UtilValidate.isNotEmpty(modeOfAction)) {
	EntityCondition partyCondition = EntityCondition.makeCondition("modeOfAction", EntityOperator.EQUALS, modeOfAction);
	conditionsList.add(partyCondition);
}

if (UtilValidate.isNotEmpty(requestType)) {
	EntityCondition partyCondition = EntityCondition.makeCondition("serviceRequestType", EntityOperator.EQUALS, requestType);
	conditionsList.add(partyCondition);
}

if (UtilValidate.isNotEmpty(makerPartyId)) {
	EntityCondition partyCondition = EntityCondition.makeCondition("makerPartyId", EntityOperator.EQUALS, makerPartyId);
	conditionsList.add(partyCondition);
}

if (UtilValidate.isNotEmpty(chekerPartyId)) {
	EntityCondition partyCondition = EntityCondition.makeCondition("chekerPartyId", EntityOperator.EQUALS, chekerPartyId);
	conditionsList.add(partyCondition);
}

EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

EntityFindOptions efo = new EntityFindOptions();
efo.setDistinct(true);

Debug.logInfo("mainConditons>>> "+mainConditons,"ListPendingRequest");

long count = 0;
EntityFindOptions efoNum= new EntityFindOptions();
efoNum.setFetchSize(1000);

count = delegator.findCountByCondition("UserAuditRequest", mainConditons, null, UtilMisc.toSet("userAuditRequestId"), efoNum);

int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0
		efo.setOffset(startInx);
efo.setLimit(endInx);

entryList = delegator.findList("UserAuditRequest", mainConditons, null, UtilMisc.toList(orderField+ " " + sortDir), efo, false);

int recordsFiltered = count;
int recordsTotal = count;


JSONObject grid = new JSONObject();

JSONArray results = new JSONArray();

entryList.each{entry ->
	
	JSONObject result = new JSONObject();
	
	String userAuditRequestId = entry.getString("userAuditRequestId");
	
	result.put("userAuditRequestId", userAuditRequestId);
	result.put("serviceRequestType", entry.getString("serviceRequestType"));
	result.put("makerPartyId", entry.getString("makerPartyId"));
	result.put("chekerPartyId", entry.getString("chekerPartyId"));
	result.put("statusId", entry.getString("statusId"));
	result.put("requestUri", entry.getString("requestUri"));
	result.put("remarks", entry.getString("remarks"));
	result.put("changesMade", "");  
	result.put("mandatoryFields", entry.getString("mandatoryFields"));
	result.put("modeOfAction", entry.getString("modeOfAction"));
	
	result.put("oldValue", entry.getString("oldContextMap"));
	result.put("newValue", entry.getString("contextMap"));
	
	//if ("maker_checker_audit".equals(viewType)) {
	if (security.hasPermission("DBS_ADMPR_CHEKER", userLogin)) { 
		result.put("approvePermission", "Y");
	} else {
	   result.put("approvePermission", "N");
	}
	
	auditEntityConfig = EntityQuery.use(delegator).from("UserAuditPref")
						.where("userAuditPrefId", entry.getString("serviceRequestType")).queryOne();
	if(UtilValidate.isNotEmpty(auditEntityConfig)) {
		result.put("description", auditEntityConfig.getString("description"));
	}
	
	valueCompare = org.fio.homeapps.util.UtilUserAudit.prepareValueCompare(delegator, entry.oldContextMap, entry.contextMap, entry.serviceRequestType);
	result.put("totalChanged", valueCompare.totalChanged);
	
	result.put("createdStamp", UtilValidate.isNotEmpty(entry.get("createdStamp")) ? UtilDateTime.timeStampToString(entry.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm:ss", TimeZone.getDefault(), null) : "");
	
	results.add(result);
}
println("results> "+results);
grid.put("data", results);
grid.put("draw", draw);
grid.put("recordsTotal", recordsTotal);
grid.put("recordsFiltered", recordsFiltered);

return AjaxEvents.doJSONResponse(response, grid);