import org.fio.crm.ajax.AjaxEvents
import org.fio.crm.constants.CrmConstants
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
import org.groupfio.custom.field.util.DataUtil;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastList;

groupId = parameters.get("groupId");
customFieldId = parameters.get("customFieldId");

searchPartyId = parameters.get("searchPartyId");
searchFirstName = parameters.get("searchFirstName");
//searchCompanyName = parameters.get("searchCompanyName");
searchEmailId = parameters.get("searchEmailId");
searchPhoneNum = parameters.get("searchPhoneNum");
searchRoleTypeId = parameters.get("searchRoleTypeId");

String draw = request.getParameter("draw");
String start = request.getParameter("start");
String length = request.getParameter("length");

delegator = request.getAttribute("delegator");
conditionsList = FastList.newInstance();

//EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"));
//EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CUSTOMER"));


roleConditionList = FastList.newInstance();
roleConfigs = delegator.findByAnd("CustomFieldRoleConfig", UtilMisc.toMap("groupId", groupId), UtilMisc.toList("sequenceNumber"), false);
roleConfigs.each { roleConfig ->
    roleConditionList.add( EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleConfig.roleTypeId) );
}
if (UtilValidate.isNotEmpty(roleConditionList)) {
	roleCondition = EntityCondition.makeCondition(roleConditionList, EntityOperator.OR);
	conditionsList.add(roleCondition);
}

if (UtilValidate.isNotEmpty(searchRoleTypeId)) {
	conditionsList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, searchRoleTypeId));
}

EntityCondition partyStatusCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
	EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)
], EntityOperator.OR);

conditionsList.add(partyStatusCondition);
conditionsList.add(EntityUtil.getFilterByDateExpr());

if (UtilValidate.isNotEmpty(searchPartyId)) {
	
	EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, searchPartyId);
	conditionsList.add(partyCondition);
}

if (UtilValidate.isNotEmpty(searchFirstName)) {
	EntityCondition nameCondition = EntityCondition.makeCondition([EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("firstName")), EntityOperator.LIKE, "%"+searchFirstName.toUpperCase()+"%"),
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("groupName")), EntityOperator.LIKE, "%"+searchFirstName.toUpperCase()+"%")
	], EntityOperator.OR);
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

EntityFindOptions efo = new EntityFindOptions();
efo.setDistinct(true);
println("mainConditons>>> "+mainConditons);

long count = 0;
EntityFindOptions efoNum= new EntityFindOptions();
efoNum.setDistinct(true);
efoNum.getDistinct();
efoNum.setFetchSize(1000);

count = delegator.findCountByCondition("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons, null, UtilMisc.toSet("partyId"), efoNum);

/*
partyList = delegator.findList("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons, UtilMisc.toSet("partyId"), UtilMisc.toList("createdDate"), efo, false);
int count = partyList.size();
*/

int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0
efo.setOffset(startInx);
efo.setLimit(endInx);

/*if(UtilValidate.isEmpty(searchPartyId) && UtilValidate.isEmpty(searchFirstName) && UtilValidate.isEmpty(searchEmailId) && UtilValidate.isEmpty(searchPhoneNum)) {
	int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
	int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0
	efo.setOffset(startInx);
	efo.setLimit(endInx);
}*/

partyList = delegator.findList("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons, UtilMisc.toSet("partyId"), UtilMisc.toList("createdDate"), efo, false);
String segmentationValueAssociatedEntityName = DataUtil.getSegmentationValueAssociatedEntityName(delegator, groupId);
println("segmentationValueAssociatedEntityName>>> "+segmentationValueAssociatedEntityName+", count>> "+count);
JSONObject obj = new JSONObject();
JSONArray dataMap = new JSONArray();

long recordsFiltered = count;
long recordsTotal = count;

if (partyList != null && partyList.size() > 0) {
	
	int id = 1;
	for (GenericValue party : partyList) {

		partyId = party.partyId;
		valueAssociation = EntityUtil.getFirst(delegator.findByAnd(segmentationValueAssociatedEntityName, ["groupId": groupId, "customFieldId": customFieldId, "partyId": partyId], null, false));
		if (UtilValidate.isNotEmpty(valueAssociation)) {
			continue;
		}
		
		partySummary = delegator.findOne("PartySummaryDetailsView", ["partyId": partyId], false);
		if (partySummary != null && partySummary.size() > 0) {
			JSONObject list = new JSONObject();
			String groupName = partySummary.getString("groupName");
			String statusId = partySummary.getString("statusId");
			String statusItemDesc = "";
			String name = partySummary.getString("firstName")+" "+partySummary.getString("lastName");
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
			partyContactInfo = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
			phoneNumber = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("PrimaryPhone") : "";
			infoString = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("EmailAddress") : "";
			
			GenericValue postalAddress = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryPostal(delegator, partyId);
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
			
			name = PartyHelper.getPartyName(delegator, partyId, false);
			
			id = id+1;
			list.put("id",id+"");
			list.put("partyId",partyId);
			list.put("name",name);
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
		
	return AjaxEvents.doJSONResponse(response, obj);
	
} else {

	obj.put("data", dataMap);
	obj.put("draw", draw);
	obj.put("recordsTotal", 0);
	obj.put("recordsFiltered", 0);
	
	return AjaxEvents.doJSONResponse(response, obj);
}

 
