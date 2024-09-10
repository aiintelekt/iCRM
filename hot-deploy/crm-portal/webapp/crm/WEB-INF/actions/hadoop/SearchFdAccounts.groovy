import org.fio.crm.ajax.AjaxEvents
import org.fio.crm.constants.CrmConstants
import org.fio.crm.contactmech.PartyPrimaryContactMechWorker
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

import org.ofbiz.entity.condition.EntityExpr;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastList;

partyId = parameters.get("partyId");

searchPartyId = parameters.get("searchPartyId");

String draw = request.getParameter("draw");
String start = request.getParameter("start");
String length = request.getParameter("length");

delegator = request.getAttribute("delegator");
conditionsList = FastList.newInstance();

fromDate = parameters.get("fromDate");
thruDate = parameters.get("thruDate");
SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS");
if (UtilValidate.isNotEmpty(fromDate)) {
	fromdateVal=fromDate+" "+"00:00:00.0";
	Date parsedDateFrom = dateFormat.parse(fromdateVal);
	Timestamp fromTimestamp = new java.sql.Timestamp(parsedDateFrom.getTime());	
	conditionsList.addAll( UtilMisc.toList( new EntityExpr( "processedTimestamp" , EntityOperator.NOT_EQUAL , null ) , new EntityExpr( "processedTimestamp" , EntityOperator.GREATER_THAN_EQUAL_TO , fromTimestamp ) ) );
}
if (UtilValidate.isNotEmpty(thruDate)) { 
	thruDateVal=thruDate+" "+"23:23:59.0";
	Date parsedDateThru = dateFormat.parse(thruDateVal);
	Timestamp thruDateTimestamp = new java.sql.Timestamp(parsedDateThru.getTime());	
	conditionsList.addAll( UtilMisc.toList( new EntityExpr( "processedTimestamp" , EntityOperator.NOT_EQUAL , null ) , new EntityExpr( "processedTimestamp" , EntityOperator.LESS_THAN_EQUAL_TO , thruDateTimestamp ) ) );
}

if (UtilValidate.isNotEmpty(searchPartyId)) {
	//EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, searchPartyId);
	//conditionsList.add(partyCondition);
}
/*
if (UtilValidate.isNotEmpty(searchFirstName)) {
	EntityCondition nameCondition = EntityCondition.makeCondition([EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("firstName")), EntityOperator.LIKE, searchFirstName.toUpperCase()+"%"),
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("groupName")), EntityOperator.LIKE, searchFirstName.toUpperCase()+"%")
	], EntityOperator.OR);
	conditionsList.add(nameCondition);
}
*/

if (UtilValidate.isNotEmpty(searchPartyId)) {
	EntityCondition partyCondition = EntityCondition.makeCondition("lcin", EntityOperator.EQUALS, searchPartyId);
	conditionsList.add(partyCondition);
}

/*
GenericValue identification = EntityUtil.getFirst( delegator.findByAnd("PartyIdentification", UtilMisc.toMap("partyIdentificationTypeId", "EXT_PARTY_ID", "partyId", searchPartyId), null, false) );
if (UtilValidate.isNotEmpty(identification)) {
	EntityCondition partyCondition = EntityCondition.makeCondition("lcin", EntityOperator.EQUALS, identification.getString("idValue"));
	conditionsList.add(partyCondition);
} else {
	EntityCondition partyCondition = EntityCondition.makeCondition("lcin", EntityOperator.EQUALS, null);
	conditionsList.add(partyCondition);
}
*/
EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

EntityFindOptions efo = new EntityFindOptions();
efo.setDistinct(true);

long count = 0;
EntityFindOptions efoNum= new EntityFindOptions();
efoNum.setDistinct(true);
efoNum.getDistinct();
efoNum.setFetchSize(1000);

count = delegator.findCountByCondition("HdpFdAccount", mainConditons, null, UtilMisc.toSet("lcin"), efoNum);

/*
entityList = delegator.findList("HdpFdAccount", mainConditons, UtilMisc.toSet("lcin"), null, efo, false);
int count = entityList.size();
*/

if(UtilValidate.isEmpty(searchPartyId)) {
	int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
	int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0
	efo.setOffset(startInx);
	efo.setLimit(endInx);
}
println("mainConditons> "+mainConditons);
entityList = delegator.findList("HdpFdAccount", mainConditons, null, null, efo, false);

JSONObject grid = new JSONObject();
JSONArray dataList = new JSONArray();

long recordsFiltered = count;
long recordsTotal = count;

if (UtilValidate.isNotEmpty(entityList)) {
	
	for (GenericValue entity : entityList) {
		
		JSONObject data = new JSONObject();
		
		data.putAll(entity);
		
		data.put("processedTime", UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), "dd/MM/yyyy", TimeZone.getDefault(), null));
		
		dataList.add(data);
		
	}
	
	grid.put("data", dataList);
	grid.put("draw", draw);
	grid.put("recordsTotal", recordsTotal);
	grid.put("recordsFiltered", recordsFiltered);
		
	return AjaxEvents.doJSONResponse(response, grid);
	
} else {

	grid.put("data", dataList);
	grid.put("draw", draw);
	grid.put("recordsTotal", 0);
	grid.put("recordsFiltered", 0);
	
	return AjaxEvents.doJSONResponse(response, grid);
}

 
