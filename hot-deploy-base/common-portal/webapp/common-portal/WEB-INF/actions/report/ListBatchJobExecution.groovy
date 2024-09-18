import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.party.PartyHelper;
import org.fio.admin.portal.event.AjaxEvents;

import java.util.HashMap;
import java.util.List;

import org.ofbiz.entity.condition.EntityExpr;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;

import javolution.util.FastList;

searchPartyId = parameters.get("searchPartyId");
statusType = parameters.get("statusType");

String draw = request.getParameter("draw");
String start = request.getParameter("start");
String length = request.getParameter("length");

String sortDir = "desc";
String orderField = "";
String orderColumnId = request.getParameter("order[0][column]");
if(UtilValidate.isNotEmpty(orderColumnId)) {
	int sortColumnId = Integer.parseInt(orderColumnId);
	String sortColumnName = request.getParameter("columns["+sortColumnId+"][data]");
	sortDir = request.getParameter("order[0][dir]").toUpperCase();
	orderField = sortColumnName;
} else {
	orderField = "lastUpdated";
}

Debug.logInfo("orderField>>> "+orderField,"BatchJobExecutionSummary");

delegator = request.getAttribute("delegator");

conditionsList = FastList.newInstance();

if (UtilValidate.isNotEmpty(statusType) && statusType.equals("MICRO_SERVICE")) {
	EntityCondition jobSkipCondition = EntityCondition.makeCondition([
		EntityCondition.makeCondition("batchJobType", EntityOperator.EQUALS, "MICRO_SERVICE")
	], EntityOperator.AND);
	conditionsList.add(jobSkipCondition);
} else if (UtilValidate.isNotEmpty(statusType) && statusType.equals("FILE_IMPORT")) {
	EntityCondition jobSkipCondition = EntityCondition.makeCondition([
		EntityCondition.makeCondition("batchJobType", EntityOperator.EQUALS, "HADOOP")
	], EntityOperator.AND);
	conditionsList.add(jobSkipCondition);
} else if (UtilValidate.isNotEmpty(statusType) && statusType.equals("OFBIZ_DATA")) {
	EntityCondition jobSkipCondition = EntityCondition.makeCondition([
		EntityCondition.makeCondition("batchJobType", EntityOperator.EQUALS, "OFBIZ_DATA")
	], EntityOperator.AND);
	conditionsList.add(jobSkipCondition);
}

fromDate = parameters.get("fromDate");
thruDate = parameters.get("thruDate");
SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS");
if (UtilValidate.isNotEmpty(fromDate)) {
	fromdateVal=fromDate+" "+"00:00:00.0";
	Date parsedDateFrom = dateFormat.parse(fromdateVal);
	Timestamp fromTimestamp = new java.sql.Timestamp(parsedDateFrom.getTime());	
	conditionsList.addAll( UtilMisc.toList( new EntityExpr( "lastUpdated" , EntityOperator.NOT_EQUAL , null ) , new EntityExpr( "lastUpdated" , EntityOperator.GREATER_THAN_EQUAL_TO , fromTimestamp ) ) );
}
if (UtilValidate.isNotEmpty(thruDate)) { 
	thruDateVal=thruDate+" "+"23:23:59.0";
	Date parsedDateThru = dateFormat.parse(thruDateVal);
	Timestamp thruDateTimestamp = new java.sql.Timestamp(parsedDateThru.getTime());	
	conditionsList.addAll( UtilMisc.toList( new EntityExpr( "lastUpdated" , EntityOperator.NOT_EQUAL , null ) , new EntityExpr( "lastUpdated" , EntityOperator.LESS_THAN_EQUAL_TO , thruDateTimestamp ) ) );
}

EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

EntityFindOptions efo = new EntityFindOptions();
efo.setDistinct(true);

Debug.logInfo("mainConditons>>> "+mainConditons,"BatchJobExecutionSummary");

Debug.logInfo("BatchJobExecutionSummary start count>>> "+UtilDateTime.nowTimestamp(),"BatchJobExecutionSummary");

long count = 0;
EntityFindOptions efoNum= new EntityFindOptions();
efoNum.setDistinct(true);
efoNum.getDistinct();
efoNum.setFetchSize(1000);

count = delegator.findCountByCondition("BatchJobExecutionSummary", mainConditons, null, UtilMisc.toSet("jobExecutionId"), efoNum);

/*
jobList = delegator.findList("BatchJobExecutionSummary", mainConditons, UtilMisc.toSet("jobExecutionId"), UtilMisc.toList("-lastUpdated"), efo, false);
int count = jobList.size();
*/

Debug.logInfo("BatchJobExecutionSummary end count>>> "+UtilDateTime.nowTimestamp()+", count: "+count,"BatchJobExecutionSummary");

int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0
efo.setOffset(startInx);
efo.setLimit(endInx);

Debug.logInfo("BatchJobExecutionSummary start list>>> "+UtilDateTime.nowTimestamp(),"BatchJobExecutionSummary");
jobList = delegator.findList("BatchJobExecutionSummary", mainConditons, null, UtilMisc.toList(orderField+ " " + sortDir), efo, false);
Debug.logInfo("jobList.size(): "+jobList.size(),"BatchJobExecutionSummary");
Debug.logInfo("BatchJobExecutionSummary end list>>> "+UtilDateTime.nowTimestamp(),"BatchJobExecutionSummary");
int recordsFiltered = count;
int recordsTotal = count;

JSONObject grid = new JSONObject();

JSONArray results = new JSONArray();
jobList.each{job ->
	JSONObject result = new JSONObject();
	result.putAll(job);
	
	String jobName = "";
	//jobInstance = EntityUtil.getFirst( delegator.findByAnd("BatchJobInstance", UtilMisc.toMap("jobInstanceId", job.getLong("jobInstanceId")), null, false) );
	jobInstance = EntityUtil.getFirst( delegator.findByAnd("BatchJobInstance", UtilMisc.toMap("jobInstanceId", job.getString("jobInstanceId")), null, false) );
	if (UtilValidate.isNotEmpty(jobInstance)) {
		jobName = jobInstance.getString("jobName");
	}
	
	result.put("jobName", jobName);
	
	errorCount = delegator.findCountByCondition("BatchStepErrorLog", EntityCondition.makeCondition("jobExecutionId", EntityOperator.EQUALS, job.getString("jobExecutionId") ), null, null);
	result.put("errorCount", errorCount);
	 
	result.put("createTime", UtilValidate.isNotEmpty(job.get("createTime")) ? UtilDateTime.timeStampToString(job.getTimestamp("createTime"), "dd/MM/yyyy HH:mm:ss", TimeZone.getDefault(), null) : "");
	result.put("startTime", UtilValidate.isNotEmpty(job.get("startTime")) ? UtilDateTime.timeStampToString(job.getTimestamp("startTime"), "dd/MM/yyyy HH:mm:ss", TimeZone.getDefault(), null) : "");
	result.put("endTime",  UtilValidate.isNotEmpty(job.get("endTime")) ? UtilDateTime.timeStampToString(job.getTimestamp("endTime"), "dd/MM/yyyy HH:mm:ss", TimeZone.getDefault(), null) : "");
	result.put("lastUpdated", UtilValidate.isNotEmpty(job.get("lastUpdated")) ? UtilDateTime.timeStampToString(job.getTimestamp("lastUpdated"), "dd/MM/yyyy HH:mm:ss", TimeZone.getDefault(), null) : "");
	
	results.add(result);
}

grid.put("data", results);
grid.put("draw", draw);
grid.put("recordsTotal", recordsTotal);
grid.put("recordsFiltered", recordsFiltered);
	
return AjaxEvents.doJSONResponse(response, grid);