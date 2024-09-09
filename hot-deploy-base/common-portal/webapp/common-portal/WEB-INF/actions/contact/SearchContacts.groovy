import org.fio.crm.ajax.AjaxEvents
import org.fio.crm.constants.CrmConstants
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
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.GenericValue;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastList;


contactSearchPartyId = parameters.get("contactSearchPartyId");
searchFirstName = parameters.get("searchFirstName");
searchLastName = parameters.get("searchLastName");

//searchCompanyName = parameters.get("searchCompanyName");
searchEmailId = parameters.get("searchEmailId");
searchPhoneNum = parameters.get("searchPhoneNum");

String draw = request.getParameter("draw");
String start = request.getParameter("start");
String length = request.getParameter("length");

delegator = request.getAttribute("delegator");
conditionsList = FastList.newInstance();
srFromPartyId = parameters.get("srFromPartyId");


if (UtilValidate.isNotEmpty(srFromPartyId)) {
EntityCondition roleTypeCondition1 = EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, srFromPartyId));
conditionsList.add(roleTypeCondition1);
}
EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"));
conditionsList.add(roleTypeCondition);


EntityCondition partyStatusCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
	EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)
], EntityOperator.OR);

conditionsList.add(partyStatusCondition);
conditionsList.add(EntityUtil.getFilterByDateExpr());
if (UtilValidate.isNotEmpty(contactSearchPartyId)) {
	
	EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.LIKE, "%"+contactSearchPartyId+"%");
	conditionsList.add(partyCondition);
}

if (UtilValidate.isNotEmpty(searchFirstName)) {
	EntityCondition nameCondition =  EntityCondition.makeCondition("firstName", EntityOperator.LIKE, "%"+searchFirstName+"%");
               // EntityCondition.makeCondition("lastName", EntityOperator.LIKE, "%"+searchFirstName+"%")), EntityOperator.OR);
	conditionsList.add(nameCondition);
}
if (UtilValidate.isNotEmpty(searchLastName)) {
	EntityCondition nameCondition1 =  EntityCondition.makeCondition("lastName", EntityOperator.LIKE, "%"+searchLastName+"%");
               // EntityCondition.makeCondition("lastName", EntityOperator.LIKE, "%"+searchLastName+"%")), EntityOperator.OR);
	conditionsList.add(nameCondition1);
}

List eventExprs = [];

if (UtilValidate.isNotEmpty(searchEmailId) || UtilValidate.isNotEmpty(searchPhoneNum)) {

	if (UtilValidate.isNotEmpty(searchEmailId)) {
		EntityCondition emailCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("infoString", EntityOperator.LIKE, "%"+searchEmailId+"%"),
			EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS")
		], EntityOperator.AND);
		eventExprs.add(emailCondition);
	}

	if (UtilValidate.isNotEmpty(searchPhoneNum)) {
		EntityCondition phoneCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("contactNumber", EntityOperator.LIKE, "%"+searchPhoneNum+"%"),
			EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "TELECOM_NUMBER")
		], EntityOperator.AND);
		eventExprs.add(phoneCondition);
	}

	conditionsList.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
}
EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);


EntityFindOptions efo = new EntityFindOptions();
efo.setDistinct(true);

long count = 0;
EntityFindOptions efoNum= new EntityFindOptions();
efoNum.setDistinct(true);
efoNum.getDistinct();
efoNum.setFetchSize(1000);

//count = delegator.findCountByCondition("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons, null, UtilMisc.toSet("partyId"), efoNum);

/*
partyList = delegator.findList("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons, UtilMisc.toSet("partyId"), null, efo, false);
int count = partyList.size();
*/

if(UtilValidate.isEmpty(contactSearchPartyId) && UtilValidate.isEmpty(searchFirstName) && UtilValidate.isEmpty(searchEmailId) && UtilValidate.isEmpty(searchPhoneNum)) {
	int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
	int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 100;
	efo.setOffset(startInx);
	efo.setLimit(endInx);
}

//parties = delegator.findList("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons, UtilMisc.toSet("partyId"), UtilMisc.toList("createdDate"), efo, false);
resultList=delegator.find("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons, null, UtilMisc.toSet("partyId"), null, efo);
parties=resultList.getPartialList(0, 100);
resultList.close();

JSONObject obj = new JSONObject();
JSONArray dataMap = new JSONArray();

long recordsFiltered = count;
long recordsTotal = count;

if (parties != null && parties.size() > 0) {
	List < String > partyIdList = EntityUtil.getFieldListFromEntityList(parties, "partyId", true);
	
	summaryConditionList = FastList.newInstance();
	summaryConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdList));
	
	EntityCondition summaryConditons = EntityCondition.makeCondition(summaryConditionList, EntityOperator.AND);

	summaryDetails = delegator.find("PartySummaryDetailsView", summaryConditons, null,  UtilMisc.toSet("partyId","groupName","statusId","firstName","lastName"), null, null);
	summaryDetailsList=summaryDetails.getCompleteList();
	summaryDetails.close();
	List < String > statusIds = EntityUtil.getFieldListFromEntityList(summaryDetailsList, "statusId", true);
	
	statusDetails = delegator.find("StatusItem", EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIds), null,  UtilMisc.toSet("statusId","description","statusId"), null, null);
	statusList=statusDetails.getCompleteList();
	statusDetails.close();
	 recordsFiltered = partyIdList.size();
	 recordsTotal = partyIdList.size();
	if (partyIdList != null && partyIdList.size() > 0) {
		int id = 1;
		for (String partyId: partyIdList) {
			partySummaryDetailsViewGv = EntityUtil.filterByCondition(summaryDetailsList, EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
			//PartySummaryDetailsViewGv = delegator.findOne("PartySummaryDetailsView", ["partyId": partyId], false);
			if (partySummaryDetailsViewGv != null && partySummaryDetailsViewGv.size() > 0) {
				JSONObject list = new JSONObject();
				String groupName = partySummaryDetailsViewGv.get(0).getString("groupName");
				String statusId = partySummaryDetailsViewGv.get(0).getString("statusId");
				String statusItemDesc = "";
				String name = partySummaryDetailsViewGv.get(0).getString("firstName")+" "+partySummaryDetailsViewGv.get(0).getString("lastName");
				if (UtilValidate.isNotEmpty(statusId)) {
					//statusItem = delegator.findOne("StatusItem", ["statusId": statusId], false);
					statusItem = EntityUtil.filterByCondition(statusList, EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,statusId));
					if (statusItem != null && statusItem.size() > 0) {
						statusItemDesc = statusItem.get(0).getString("description");
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
	}
	
	return AjaxEvents.doJSONResponse(response, obj);
} else {
	obj.put("data", dataMap);
	obj.put("draw", draw);
	obj.put("recordsTotal", 0);
	obj.put("recordsFiltered", 0);
	return AjaxEvents.doJSONResponse(response, obj);
}

 
