import org.fio.crm.ajax.AjaxEvents
import org.fio.crm.contactmech.PartyPrimaryContactMechWorker
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.GenericValue;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastList;


accountSearchPartyId = parameters.get("accountSearchPartyId");
searchGroupName = parameters.get("searchGroupName");
//searchCompanyName = parameters.get("searchCompanyName");
searchEmailId = parameters.get("searchEmailId");
searchPhoneNum = parameters.get("searchPhoneNum");
String draw = request.getParameter("draw");
String start = request.getParameter("start");
String length = request.getParameter("length");

delegator = request.getAttribute("delegator");
conditionsList = FastList.newInstance();

List<String> partyRelationshipTypeIds = Arrays.asList("RESPONSIBLE_FOR");

/*EntityCondition Contactconsition = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
	EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, partyRelationshipTypeIds)
], EntityOperator.AND);
conditionsList.add(Contactconsition);*/
EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"));
conditionsList.add(roleTypeCondition);

EntityCondition partyStatusCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
	EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)
], EntityOperator.OR);

conditionsList.add(partyStatusCondition);
conditionsList.add(EntityUtil.getFilterByDateExpr());
if (UtilValidate.isNotEmpty(accountSearchPartyId)) {
	
	EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, accountSearchPartyId);
	conditionsList.add(partyCondition);
}

if (UtilValidate.isNotEmpty(searchGroupName)) {
	EntityCondition nameCondition = EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%"+searchGroupName+"%");
	conditionsList.add(nameCondition);
}

List eventExprs = [];

if (UtilValidate.isNotEmpty(searchEmailId) || UtilValidate.isNotEmpty(searchPhoneNum)) {

	if (UtilValidate.isNotEmpty(searchEmailId)) {
		EntityCondition emailCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("infoString", EntityOperator.LIKE, searchEmailId+"%"),
			EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS")
		], EntityOperator.AND);
		eventExprs.add(emailCondition);
	}

	if (UtilValidate.isNotEmpty(searchPhoneNum)) {
		EntityCondition phoneCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, searchPhoneNum),
			EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "TELECOM_NUMBER")
		], EntityOperator.AND);
		eventExprs.add(phoneCondition);
	}

	conditionsList.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
}
EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

/*List orderBy = FastList.newInstance();
String orderColumnId = request.getParameter("order[0][column]");
if(UtilValidate.isNotEmpty(orderColumnId)) {
	int sortColumnId = Integer.parseInt(orderColumnId);
	String sortColumnName = request.getParameter("columns["+sortColumnId+"][data]");
	println ("sortColumnName------>"+sortColumnName);
	String sortDir = request.getParameter("order[0][dir]");
	sortColumnName = sortColumnName+" "+sortDir;
	orderBy.add(sortColumnName);
}*/

EntityFindOptions efo = new EntityFindOptions();
efo.setDistinct(true);

long count = 0;
EntityFindOptions  efoNum= new EntityFindOptions();
efoNum.setDistinct(true);
efoNum.getDistinct();
efoNum.setFetchSize(1000);

count = delegator.findCountByCondition("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons, null, UtilMisc.toSet("partyId"), efoNum);

/*
partyList = delegator.findList("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons, UtilMisc.toSet("partyId"), null, efo, false);
int count = partyList.size();
*/

if(UtilValidate.isEmpty(accountSearchPartyId) && UtilValidate.isEmpty(searchGroupName) && UtilValidate.isEmpty(searchEmailId) && UtilValidate.isEmpty(searchPhoneNum)) {
	int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
	int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0;
	efo.setOffset(startInx);
	efo.setLimit(endInx);
}
parties = delegator.findList("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons, UtilMisc.toSet("partyId"), UtilMisc.toList("createdDate"), efo, false);

JSONObject obj = new JSONObject();
JSONArray dataMap = new JSONArray();

long recordsFiltered = count;
long recordsTotal = count;

if (parties != null && parties.size() > 0) {
	List < String > partyIdList = EntityUtil.getFieldListFromEntityList(parties, "partyId", true);
	if (partyIdList != null && partyIdList.size() > 0) {
		int id = 1;
		for (String partyId: partyIdList) {
			PartySummaryDetailsViewGv = delegator.findOne("PartySummaryDetailsView", ["partyId": partyId], false);
			if (PartySummaryDetailsViewGv != null && PartySummaryDetailsViewGv.size() > 0) {
				JSONObject list = new JSONObject();
				String groupName = PartySummaryDetailsViewGv.getString("groupName");
				String statusId = PartySummaryDetailsViewGv.getString("statusId");
				String statusItemDesc = "";
				
				if (UtilValidate.isNotEmpty(statusId)) {
					statusItem = delegator.findOne("StatusItem", ["statusId": statusId], false);
					if (statusItem != null && statusItem.size() > 0) {
						statusItemDesc = statusItem.getString("description");
					}
				}
				String dataSourceDesc = ""
				partyDataSource = EntityUtil.getFirst(delegator.findByAnd("PartyDataSource", ["partyId": partyId], ["-fromDate"], false));
				if (partyDataSource != null && partyDataSource.size() > 0) {
					String dataSourceId = partyDataSource.getString("dataSourceId");
					if (UtilValidate.isNotEmpty(dataSourceId)) {
						dataSource = delegator.findOne("DataSource", ["dataSourceId": dataSourceId], false);
						if (dataSource != null && dataSource.size() > 0) {
							dataSourceDesc = dataSource.getString("description");
						}
					}
				}
				
				
				String city = "";
				String state = "";
				String phoneNumber = "";
				String infoString = "";
				Map<String,String> partyContactInfo = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, partyId);
				phoneNumber = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("PrimaryPhone") : "";
				infoString = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("EmailAddress") : "";
				
				GenericValue postalAddress = PartyPrimaryContactMechWorker.getPartyPrimaryPostal(delegator, partyId);
				if(UtilValidate.isNotEmpty(postalAddress)) {
					city = postalAddress.getString("city");
					
					String stateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
					if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
						GenericValue geo = delegator.findOne("Geo", ["geoId": stateProvinceGeoId], false);
						if (UtilValidate.isNotEmpty(geo)) {
							state = geo.getString("geoName");
						}
					}
				}
				id = id+1;
				list.put("id",id+"");
				list.put("partyId",partyId);
				list.put("groupName",groupName);
				list.put("statusId",statusItemDesc);
				list.put("phoneNumber",phoneNumber);
				list.put("infoString",infoString);
				list.put("city",city);
				list.put("state",state);
				dataMap.add(list);
			}
		}
		obj.put("data", dataMap);
		obj.put("draw", draw);
		obj.put("recordsTotal", recordsTotal);
		obj.put("recordsFiltered", recordsFiltered);
	}
	return AjaxEvents.doJSONResponse(response, obj);
} else {
	obj.put("data", dataMap);
	obj.put("draw", draw);
	obj.put("recordsTotal", 0);
	obj.put("recordsFiltered", 0);
	return AjaxEvents.doJSONResponse(response, obj);
}
