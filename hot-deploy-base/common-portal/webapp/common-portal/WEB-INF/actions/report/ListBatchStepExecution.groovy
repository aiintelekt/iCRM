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
import org.fio.homeapps.util.ParamUtil;

import java.util.HashMap;
import java.util.List;

import org.ofbiz.entity.condition.EntityExpr;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;

import javolution.util.FastList;

jobExecutionId = parameters.get("jobExecutionId");

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
	orderField = "createdStamp";
}

delegator = request.getAttribute("delegator");

conditionsList = FastList.newInstance();

if (UtilValidate.isNotEmpty(jobExecutionId)) {
	conditionsList.add(EntityCondition.makeCondition("jobExecutionId", EntityOperator.EQUALS, jobExecutionId));
}

EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

EntityFindOptions efo = new EntityFindOptions();
efo.setDistinct(true);

println("mainConditons>>> "+mainConditons);

long count = 0;
EntityFindOptions efoNum= new EntityFindOptions();
efoNum.setDistinct(true);
efoNum.getDistinct();
efoNum.setFetchSize(1000);

count = delegator.findCountByCondition("BatchStepExecution", mainConditons, null, UtilMisc.toSet("stepExecutionId"), efoNum);

/*
stepList = delegator.findList("BatchStepExecution", mainConditons, UtilMisc.toSet("stepExecutionId"), UtilMisc.toList("lastUpdated"), efo, false);
int count = stepList.size();
*/

int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0
efo.setOffset(startInx);
efo.setLimit(endInx);

stepList = delegator.findList("BatchStepExecution", mainConditons, null, UtilMisc.toList(orderField+ " " + sortDir), efo, false);

long recordsFiltered = count;
long recordsTotal = count;

JSONObject grid = new JSONObject();

JSONArray results = new JSONArray();
stepList.each{step ->
	JSONObject result = new JSONObject();
	result.putAll(step);
	
	errorCount = delegator.findCountByCondition("BatchStepErrorLog", EntityCondition.makeCondition("stepExecutionId", EntityOperator.EQUALS, step.getString("stepExecutionId") ), null, null);
	result.put("errorCount", errorCount);
	
	if (UtilValidate.isEmpty(step.get("actualWriteCount"))) {
		result.put("actualWriteCount", step.get("writeCount"));
	}
	
	/*
	long actualWriteCount = ParamUtil.getLong(result, "actualWriteCount");
	long writeCount = ParamUtil.getLong(result, "writeCount");
	long processSkipCount = ParamUtil.getLong(result, "processSkipCount");
	long duplicateCount = (writeCount - processSkipCount) - actualWriteCount;
	*/
	
	condition = EntityCondition.makeCondition([
			EntityCondition.makeCondition("stepExecutionId", EntityOperator.EQUALS, step.getString("stepExecutionId")),
			EntityCondition.makeCondition("errorType", EntityOperator.EQUALS, "DUPLICATE")
		], EntityOperator.AND);
	duplicateCount = delegator.findCountByCondition("BatchStepErrorLog", condition, null, null);		
	result.put("duplicateCount", duplicateCount);
	
	condition = EntityCondition.makeCondition([
			EntityCondition.makeCondition("stepExecutionId", EntityOperator.EQUALS, step.getString("stepExecutionId")),
			EntityCondition.makeCondition("errorType", EntityOperator.EQUALS, "IGNORE")
		], EntityOperator.AND);
	ignoreCount = delegator.findCountByCondition("BatchStepErrorLog", condition, null, null);		
	ignoreCount = Math.abs(step.getInteger("readSkipCount") - ignoreCount);
	result.put("readSkipCount", ignoreCount);
		
	result.put("startTime", UtilValidate.isNotEmpty(step.get("startTime")) ? UtilDateTime.timeStampToString(step.getTimestamp("startTime"), "dd/MM/yyyy HH:mm:ss", TimeZone.getDefault(), null) : "");
	result.put("endTime",  UtilValidate.isNotEmpty(step.get("endTime")) ? UtilDateTime.timeStampToString(step.getTimestamp("endTime"), "dd/MM/yyyy HH:mm:ss", TimeZone.getDefault(), null) : "");
	result.put("lastUpdated", UtilValidate.isNotEmpty(step.get("lastUpdated")) ? UtilDateTime.timeStampToString(step.getTimestamp("lastUpdated"), "dd/MM/yyyy HH:mm:ss", TimeZone.getDefault(), null) : "");
	
	results.add(result);
}

grid.put("data", results);
grid.put("draw", draw);
grid.put("recordsTotal", recordsTotal);
grid.put("recordsFiltered", recordsFiltered);
	
return AjaxEvents.doJSONResponse(response, grid);