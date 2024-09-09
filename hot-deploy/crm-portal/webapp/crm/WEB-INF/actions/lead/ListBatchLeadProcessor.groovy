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

uiLabelMap = UtilProperties.getResourceBundleMap("crmUiLabels", locale);

String defaultModelId = UtilProperties.getPropertyValue("Etl-Process", "lead.import.default.modelId");

importStatusId = parameters.get("importStatusId");
leadId = parameters.get("leadId");
batchId = parameters.get("batchId");
firstName = parameters.get("firstName");
lastName = parameters.get("lastName");
uploadedByUserLoginId = parameters.get("uploadedByUserLoginId");
errorCodeId = parameters.get("errorCodeId");

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

boolean isApprover = false;
/*
security = request.getAttribute("security");
if (security.hasPermission("ETL-IMPDAT-APPROVER", userLogin)) {
    isApprover = true;
}
*/
conditionsList = FastList.newInstance();

if(UtilValidate.isNotEmpty(defaultModelId)) {
	conditionsList.add(EntityCondition.makeCondition("modelId", EntityOperator.EQUALS, defaultModelId));
}

if (UtilValidate.isNotEmpty(importStatusId)) {
	if (importStatusId.equals("DISABLED")) {
		conditionsList.add(EntityCondition.makeCondition("partyStatusId", EntityOperator.EQUALS, "PARTY_DISABLED"));	
	} else if (importStatusId.equals("DATAIMP_ERROR")) {
		EntityCondition statusCondition = EntityCondition.makeCondition([
			EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_ERROR"),
			EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED")
		], EntityOperator.OR);
		conditionsList.add(statusCondition);		
	} else {
		EntityCondition statusCondition = EntityCondition.makeCondition([
			EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, importStatusId)
		], EntityOperator.AND);
		conditionsList.add(statusCondition);	
	}
} else {
	/*EntityCondition statusCondition = EntityCondition.makeCondition([
		EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_NOT_APPROVED")
	], EntityOperator.AND);
	conditionsList.add(statusCondition);
	*/
}

fromDate = parameters.get("fromDate");
thruDate = parameters.get("thruDate");
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

if (UtilValidate.isNotEmpty(leadId)) {
	EntityCondition condition = EntityCondition.makeCondition([
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("leadId")), EntityOperator.LIKE, "%"+leadId.toUpperCase()+"%"),
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("primaryPartyId")), EntityOperator.LIKE, "%"+leadId.toUpperCase()+"%")
	], EntityOperator.OR);
	conditionsList.add(condition);
}

if (UtilValidate.isNotEmpty(batchId)) {
	EntityCondition condition = EntityCondition.makeCondition([
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("batchId")), EntityOperator.LIKE, "%"+batchId.toUpperCase()+"%")
	], EntityOperator.OR);
	conditionsList.add(condition);
}

if (UtilValidate.isNotEmpty(firstName)) {
	EntityCondition condition = EntityCondition.makeCondition([
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("firstName")), EntityOperator.LIKE, "%"+firstName.toUpperCase()+"%")
	], EntityOperator.OR);
	conditionsList.add(condition);
}
if (UtilValidate.isNotEmpty(lastName)) {
	EntityCondition condition = EntityCondition.makeCondition([
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("lastName")), EntityOperator.LIKE, "%"+lastName.toUpperCase()+"%")
	], EntityOperator.OR);
	conditionsList.add(condition);
}

if (UtilValidate.isNotEmpty(errorCodeId)) {
	EntityCondition condition = EntityCondition.makeCondition([
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("errorCodes")), EntityOperator.LIKE, "%"+errorCodeId.toUpperCase()+"%")
	], EntityOperator.OR);
	conditionsList.add(condition);
}

if (UtilValidate.isNotEmpty(uploadedByUserLoginId)) {
	conditionsList.add(EntityCondition.makeCondition("uploadedByUserLoginId", EntityOperator.EQUALS, uploadedByUserLoginId));
}

if (!isApprover) {
	conditionsList.add(EntityCondition.makeCondition("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")));
}

EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

EntityFindOptions efo = new EntityFindOptions();
efo.setDistinct(true);
println("isApprover>>> "+isApprover);
println("mainConditons>>> "+mainConditons);

long count = 0;
EntityFindOptions efoNum= new EntityFindOptions();
efoNum.setDistinct(true);
efoNum.getDistinct();
efoNum.setFetchSize(1000);

count = delegator.findCountByCondition("EtlProcessorLeadSummary", mainConditons, null, UtilMisc.toSet("batchId"), efoNum);

/*
leadList = delegator.findList("EtlProcessorLeadSummary", mainConditons, UtilMisc.toSet("batchId"), UtilMisc.toList("-createdStamp"), null, false);
int count = leadList.size();
*/

int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0
efo.setOffset(startInx);
efo.setLimit(endInx);

println("startInx: "+startInx+", endInx:"+endInx);
println("orderField: "+orderField+", sortDir:"+sortDir);
println("count: "+count);

leadList = delegator.findList("EtlProcessorLeadSummary", mainConditons, null, UtilMisc.toList(orderField+ " " + sortDir), efo, false);

long recordsFiltered = count;
long recordsTotal = count;

JSONObject grid = new JSONObject();

JSONArray results = new JSONArray();
leadList.each{lead ->
	JSONObject result = new JSONObject();
	result.putAll(lead);
	
	result.put("createdStamp", UtilValidate.isNotEmpty(lead.get("createdStamp")) ? UtilDateTime.timeStampToString(lead.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	
	condition = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, lead.get("batchId")),
			EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_IMPORTED")
			);
	
	long importedCount = delegator.findCountByCondition("DataImportLead", condition, null, null);
	result.put("importedCount", importedCount);			
	
	condition = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, lead.get("batchId")),
			EntityCondition.makeCondition([
					EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_ERROR"),
					EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED")
				], EntityOperator.OR)
			);
	
	long errorCount = delegator.findCountByCondition("DataImportLead", condition, null, null);
	result.put("errorCount", errorCount);	
	/*
	condition = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, lead.get("batchId")),
			EntityCondition.makeCondition([
					EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "")
				], EntityOperator.OR)
			);
	
	long notProcessedCount = delegator.findCountByCondition("DataImportLead", condition, null, null);
	result.put("notProcessedCount", notProcessedCount);	
	*/
	results.add(result);
}

grid.put("data", results);
grid.put("draw", draw);
grid.put("recordsTotal", recordsTotal);
grid.put("recordsFiltered", recordsFiltered);
	
return AjaxEvents.doJSONResponse(response, grid);