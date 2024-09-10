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
import java.util.stream.Collector
import java.util.stream.Collectors

import javolution.util.FastList;

uiLabelMap = UtilProperties.getResourceBundleMap("crmUiLabels", locale);

importStatusId = parameters.get("importStatusId");
leadId = parameters.get("leadId");
batchId = parameters.get("batchId");
firstName = parameters.get("firstName");
lastName = parameters.get("lastName");
uploadedByUserLoginId = parameters.get("uploadedByUserLoginId");
errorCodeId = parameters.get("errorCodeId");

delegator = request.getAttribute("delegator");

boolean isApprover = false;
conditionsList = FastList.newInstance();

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

leadList = delegator.findList("DataImportLeadSummary", mainConditons, null, null, efo, false).stream().collect(Collectors.toList());

List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
leadList.each{lead ->
	JSONObject result = new JSONObject();
	result.putAll(lead);
	
	String leadName = "";
	if (UtilValidate.isNotEmpty(lead.get("firstName"))) {
		leadName = lead.getString("firstName").concat( UtilValidate.isNotEmpty(lead.get("lastName")) ? " " + lead.get("lastName") : "" );
	} else if (UtilValidate.isNotEmpty(lead.get("firstName"))) {
		leadName = lead.get("lastName");
	}
	result.put("leadName", leadName);
	
	boolean isDisalbed = false;
	String disableReason = "";
	if (UtilValidate.isNotEmpty(lead.getString("primaryPartyId"))
		//&& UtilValidate.isNotEmpty(lead.get("importStatusId")) && lead.get("importStatusId").equals("DATAIMP_IMPORTED")
		) {
		party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", lead.getString("primaryPartyId")), null, false) );
		if (UtilValidate.isNotEmpty(party) && UtilValidate.isNotEmpty(party.getString("statusId")) && party.getString("statusId").equals("PARTY_DISABLED")) {
			isDisalbed = true;
			if (UtilValidate.isNotEmpty(party.getString("statusChangeReason"))) {
				reasonEnum = EntityUtil.getFirst( delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", party.getString("statusChangeReason")), null, false) );
				if (UtilValidate.isNotEmpty(reasonEnum)) {
					disableReason = reasonEnum.getString("description");
				}
			}
		}
	}
	result.put("isDisalbed", isDisalbed);
	result.put("disableReason", disableReason);
	
	String importStatusName = "";
	if (UtilValidate.isNotEmpty(lead.get("importStatusId"))) {
		if (isDisalbed) { 
			importStatusName = uiLabelMap.get("disabled");
		} else if (lead.get("importStatusId").equals("DATAIMP_APPROVED")) {
			importStatusName = uiLabelMap.get("dataimpApproved");
		} else if (lead.get("importStatusId").equals("DATAIMP_NOT_APPROVED")) {
			importStatusName = uiLabelMap.get("dataimpNotApproved");
		} else if (lead.get("importStatusId").equals("DATAIMP_REJECTED")) {
			importStatusName = uiLabelMap.get("dataimpRejected");
		} else if (lead.get("importStatusId").equals("DATAIMP_IMPORTED")) {
			importStatusName = uiLabelMap.get("dataimpImported");
		} else if (lead.get("importStatusId").equals("DATAIMP_ERROR")) {
			importStatusName = uiLabelMap.get("dataimpError");
		} else if (lead.get("importStatusId").equals("DATAIMP_FAILED")) {
			importStatusName = uiLabelMap.get("dataimpError");
		}
	}
	result.put("importStatusName", importStatusName);
	 
	result.put("lastUpdatedStamp", UtilValidate.isNotEmpty(lead.get("lastUpdatedStamp")) ? UtilDateTime.timeStampToString(lead.getTimestamp("lastUpdatedStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	result.put("createdStamp", UtilValidate.isNotEmpty(lead.get("createdStamp")) ? UtilDateTime.timeStampToString(lead.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	
	codeList = new LinkedHashMap<String, Object>();
	if (UtilValidate.isNotEmpty(lead.get("errorCodes"))) {
		
		errorCodeList = StringUtil.split(lead.getString("errorCodes"), ",");
		errorCodeList.each{ errorCode ->
			
			code = EntityUtil.getFirst( delegator.findByAnd("ErrorCode", UtilMisc.toMap("errorCodeId", errorCode, "ErrorCodeType", "LEAD_IMPORT"), null, false) );
			if (UtilValidate.isNotEmpty(code)) {
				
				toolTip = code.get("codeDescription");
				
				codeList.put(errorCode, toolTip);
			}
		}
		
	}
	result.put("codeList", codeList);
	
	auditCount = delegator.findCountByCondition("ValidationAuditLog", EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("pkCombinedValueText")), EntityOperator.LIKE, "%"+lead.get("leadId").toUpperCase()+"%"), null, null);
	result.put("auditCount", auditCount);
		
	results.add(result);
}	
return AjaxEvents.doJSONResponse(response, results);