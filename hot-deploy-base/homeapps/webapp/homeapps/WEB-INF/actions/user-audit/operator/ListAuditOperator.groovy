
import org.fio.homeapps.event.AjaxEvents
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;

import javolution.util.FastList;

operatorType = parameters.get("operatorType");
userStatus = parameters.get("userStatus");
userLoginId = parameters.get("userLoginId");

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
	orderField = "lastUpdatedStamp";
}

delegator = request.getAttribute("delegator");

conditionsList = FastList.newInstance();

//conditionsList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "DIGITAL_GOOD"));

if(UtilValidate.isNotEmpty(operatorType)) {
	if (operatorType.equals("M")) {
		conditionsList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "DBS_ADMPR_MAKER"));
	} else if (operatorType.equals("C")) {
		conditionsList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "DBS_ADMPR_CHEKER"));
	}
}

if(UtilValidate.isNotEmpty(userStatus)) {
	if (userStatus.equals("Y")) {
		EntityCondition condition = EntityCondition.makeCondition([
		    EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"),
		    EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, null),
		    ], EntityOperator.OR);
		conditionsList.add(condition);
	} else if (userStatus.equals("N")) {
		conditionsList.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "N"));
	}
}

if(UtilValidate.isNotEmpty(userLoginId)) {
	conditionsList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId));
}	

/*
if(UtilValidate.isNotEmpty(chargeType)) {
	EntityCondition condition = EntityCondition.makeCondition([
	    EntityCondition.makeCondition("attrType", EntityOperator.EQUALS, "CHARGE_CODE"),
	    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "CHARGE_TYPE"),
	    EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("attrValue")), EntityOperator.EQUALS, chargeType.toUpperCase())
	    ], EntityOperator.AND);
	conditionsList.add(condition);
}
*/

EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

EntityFindOptions efo = new EntityFindOptions();
efo.setDistinct(true);

Debug.logInfo("mainConditons>>> "+mainConditons, "ListMakerCheker");

Debug.logInfo("ListMakerCheker start count>>> "+UtilDateTime.nowTimestamp(), "ListMakerCheker");

long count = 0;
EntityFindOptions efoNum= new EntityFindOptions();
efoNum.setFetchSize(1000);

count = delegator.findCountByCondition("UserLoginAndSecuritySummary", mainConditons, null, null, efoNum);
//count = delegator.findCountByCondition("ProductSummary", mainConditons, null, efoNum);

Debug.logInfo("ListMakerCheker end count>>> "+UtilDateTime.nowTimestamp()+", count: "+count,"ListMakerCheker");

int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0
efo.setOffset(startInx);
efo.setLimit(endInx);

Debug.logInfo("ListMakerCheker start list>>> "+UtilDateTime.nowTimestamp(),"ListMakerCheker");
entryList = delegator.findList("UserLoginAndSecuritySummary", mainConditons, null, UtilMisc.toList(orderField+ " " + sortDir), efo, false);
Debug.logInfo("entryList.size(): "+entryList.size(),"ProductSummary");

Debug.logInfo("ListMakerCheker end list>>> "+UtilDateTime.nowTimestamp(),"ListMakerCheker");
int recordsFiltered = count;
int recordsTotal = count;

JSONObject grid = new JSONObject();

JSONArray results = new JSONArray();
entryList.each{entry ->
	JSONObject result = new JSONObject();
	result.putAll(entry);
	
	result.put("operatorType", org.fio.homeapps.util.UtilUserAudit.getUserAuditOperatorType(delegator, entry.userLoginId));
	
	/*
	String serviceCode = "";
	EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
	    						EntityCondition.makeCondition("productId", EntityOperator.EQUALS, entry.getString("productId")),
	    						//EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, serviceCode),
	    						EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "PRODUCT_COMPONENT"),
	    						EntityUtil.getFilterByDateExpr()
	    					);
	GenericValue productAssoc = EntityUtil.getFirst(delegator.findList("ProductAssoc", condition, null, null, null, false));
	if (UtilValidate.isNotEmpty(productAssoc)) {
		serviceProd = EntityUtil.getFirst( delegator.findByAnd("Product", UtilMisc.toMap("productId", productAssoc.getString("productIdTo")), null, false) );
		if (UtilValidate.isNotEmpty(serviceProd)) {
			serviceCode = serviceProd.getString("productIdAlt")+" - "+serviceProd.getString("internalName");
		}
	}    
	result.put("serviceCode", serviceCode);				
	
	
	String chargeType = org.fio.homeapps.util.ProductUtil.getProductAttrValue(delegator, entry.getString("productId"), "CHARGE_CODE", "CHARGE_TYPE");
	if (UtilValidate.isNotEmpty(chargeType)) {
		profileConfiguration = EntityUtil.getFirst( delegator.findByAnd("ProfileConfiguration", UtilMisc.toMap("profileCode", chargeType, "profileTypeId", "CHARGE_TYPE"), null, false) );
		if(UtilValidate.isNotEmpty(profileConfiguration)) {
			chargeType = profileConfiguration.getString("profileDescription");
		}
	}
	result.put("chargeType", chargeType);
	*/
	
	result.put("createdStamp", UtilValidate.isNotEmpty(entry.get("createdStamp")) ? UtilDateTime.timeStampToString(entry.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm:ss", TimeZone.getDefault(), null) : "");
	result.put("lastUpdatedStamp", UtilValidate.isNotEmpty(entry.get("lastUpdatedStamp")) ? UtilDateTime.timeStampToString(entry.getTimestamp("lastUpdatedStamp"), "dd/MM/yyyy HH:mm:ss", TimeZone.getDefault(), null) : "");
	
	results.add(result);
}

grid.put("data", results);
grid.put("draw", draw);
grid.put("recordsTotal", recordsTotal);
grid.put("recordsFiltered", recordsFiltered);
	
return AjaxEvents.doJSONResponse(response, grid);