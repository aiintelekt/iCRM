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
import org.fio.crm.ajax.AjaxEvents;

import java.util.HashMap;
import java.util.List;

import org.ofbiz.entity.condition.EntityExpr;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;

import javolution.util.FastList;

pkCombinedValueText = parameters.get("pkCombinedValueText");
validationAuditType = parameters.get("validationAuditType");

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

if (UtilValidate.isNotEmpty(pkCombinedValueText)) {
	EntityCondition pkCondition = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("pkCombinedValueText")), EntityOperator.LIKE, "%"+pkCombinedValueText.toUpperCase()+"%");
	conditionsList.add(pkCondition);
}

if (UtilValidate.isNotEmpty(validationAuditType)) {
	conditionsList.add(EntityCondition.makeCondition("validationAuditType", EntityOperator.EQUALS, validationAuditType));
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

count = delegator.findCountByCondition("ValidationAuditLog", mainConditons, null, UtilMisc.toSet("validationAuditLogId"), efoNum);

/*
logList = delegator.findList("ValidationAuditLog", mainConditons, UtilMisc.toSet("validationAuditLogId"), UtilMisc.toList("createdStamp"), efo, false);
int count = logList.size();
*/

int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0
efo.setOffset(startInx);
efo.setLimit(endInx);

errorList = delegator.findList("ValidationAuditLog", mainConditons, null, UtilMisc.toList(orderField+ " " + sortDir), efo, false);

long recordsFiltered = count;
long recordsTotal = count;

JSONObject grid = new JSONObject();

JSONArray results = new JSONArray();
errorList.each{error ->
	JSONObject result = new JSONObject();
	result.putAll(error);
		
	result.put("createdStamp", UtilValidate.isNotEmpty(error.get("createdStamp")) ? UtilDateTime.timeStampToString(error.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	
	results.add(result);
}

grid.put("data", results);
grid.put("draw", draw);
grid.put("recordsTotal", recordsTotal);
grid.put("recordsFiltered", recordsFiltered);
	
return AjaxEvents.doJSONResponse(response, grid);