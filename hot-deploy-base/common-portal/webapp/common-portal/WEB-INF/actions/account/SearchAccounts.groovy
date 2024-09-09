import org.fio.crm.ajax.AjaxEvents;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityListIterator;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastList;

partyId = parameters.get("accountSearchPartyId");
name = parameters.get("searchGroupName");
email = parameters.get("searchEmailId");
phone = parameters.get("searchPhoneNum");

String draw = request.getParameter("draw");
String start = request.getParameter("start");
String length = request.getParameter("length");

delegator = request.getAttribute("delegator");


conditionList = FastList.newInstance();

conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT"));

if (UtilValidate.isNotEmpty(partyId)) {
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
}

if (UtilValidate.isNotEmpty(name)) {
	EntityCondition nameCondition = EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + name + "%");
	conditionList.add(nameCondition);
}

List<EntityCondition> eventExprs = new LinkedList<EntityCondition>();
if (UtilValidate.isNotEmpty(email) || UtilValidate.isNotEmpty(phone)) {

	if (UtilValidate.isNotEmpty(email)) {
		eventExprs.add(EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, email));
	}

	if (UtilValidate.isNotEmpty(phone)) {
		eventExprs.add(EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, phone));
	}

	conditionList.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
	conditionList.add(EntityUtil.getFilterByDateExpr("pcmFromDate", "pcmThruDate"));
}

EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
println("findAccount mainConditons: "+mainConditons);

DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
dynamicViewEntity.addMemberEntity("P", "Party");
dynamicViewEntity.addAlias("P", "partyId", "partyId", null, false, true, null);
dynamicViewEntity.addAlias("P", "statusId");
dynamicViewEntity.addAlias("P", "roleTypeId");
dynamicViewEntity.addAlias("P", "preferredCurrencyUomId");
dynamicViewEntity.addAlias("P", "timeZoneDesc");
dynamicViewEntity.addAlias("P", "emplTeamId");
dynamicViewEntity.addAlias("P", "ownerId");
dynamicViewEntity.addAlias("P", "externalId");
dynamicViewEntity.addAlias("P", "createdStamp");
dynamicViewEntity.addAlias("P", "createdTxStamp");

dynamicViewEntity.addMemberEntity("PG", "PartyGroup");
dynamicViewEntity.addAlias("PG", "groupName");
dynamicViewEntity.addAlias("PG", "gstnNo");
dynamicViewEntity.addAlias("PG", "localName", "groupNameLocal", null, false, true, null);
dynamicViewEntity.addViewLink("P", "PG", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

if (UtilValidate.isNotEmpty(email) || UtilValidate.isNotEmpty(phone)) {
	dynamicViewEntity.addMemberEntity("PSD", "PartySupplementalData");
    dynamicViewEntity.addAlias("PSD", "uploadedByUserLoginId");
    dynamicViewEntity.addAlias("PSD", "departmentName");
    dynamicViewEntity.addAlias("PSD", "ownershipEnumId");
    dynamicViewEntity.addAlias("PSD", "industryEnumId");
    dynamicViewEntity.addAlias("PSD", "annualRevenue");
    dynamicViewEntity.addAlias("PSD", "sicCode");
    dynamicViewEntity.addAlias("PSD", "numberEmployees");
    dynamicViewEntity.addAlias("PSD", "supplementalPartyTypeId");
    dynamicViewEntity.addViewLink("P", "PSD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));

	dynamicViewEntity.addMemberEntity("TN", "TelecomNumber");
	dynamicViewEntity.addAlias("TN", "countryCode");
	dynamicViewEntity.addAlias("TN", "areaCode");
	dynamicViewEntity.addAlias("TN", "contactNumber");
	dynamicViewEntity.addAlias("TN", "askForName");
	dynamicViewEntity.addViewLink("PSD", "TN", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryTelecomNumberId", "contactMechId"));
	
	dynamicViewEntity.addMemberEntity("CM", "ContactMech");
	dynamicViewEntity.addAlias("CM", "infoString");
	dynamicViewEntity.addViewLink("PSD", "CM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryEmailId", "contactMechId"));
		            
	dynamicViewEntity.addMemberEntity("PCM", "PartyContactMech");
	dynamicViewEntity.addAlias("PCM", "pcmFromDate", "fromDate", null, false, false, null);
	dynamicViewEntity.addAlias("PCM", "pcmThruDate", "thruDate", null, false, false, null);
	dynamicViewEntity.addViewLink("CM", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("contactMechId"));
	
	dynamicViewEntity.addMemberEntity("PCMP", "PartyContactMechPurpose");
	dynamicViewEntity.addAlias("PCMP", "pcmpFromDate", "fromDate", null, false, false, null);
	dynamicViewEntity.addAlias("PCMP", "pcmpThruDate", "thruDate", null, false, false, null);
	dynamicViewEntity.addViewLink("CM", "PCMP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("contactMechId"));
}

viewSize = 500;

int viewIndex = 0;
int highIndex = 0;
int lowIndex = 0;
int resultListSize = 0;
// get the indexes for the partial list
lowIndex = viewIndex * viewSize + 1;
highIndex = (viewIndex + 1) * viewSize;

EntityListIterator pli = EntityQuery.use(delegator)
                		//.select(fieldsToSelect)
                        .from(dynamicViewEntity)
                        .where(mainConditons)
                        //.orderBy("-partyId")
                        .cursorScrollInsensitive()
                        .fetchSize(highIndex)
                        .cache(true)
                        .queryIterator();

resultList = pli.getPartialList(lowIndex, viewSize);
               
resultListSize = pli.getResultsSizeAfterPartialList();

Map<String, Object> statusMap = org.fio.homeapps.util.StatusUtil.getStatusList(delegator, resultList, "statusId", "PARTY_STATUS");
Map<String, Object> stateMap = org.fio.admin.portal.util.DataUtil.getGeoNameList(delegator, "STATE/PROVINCE");

JSONObject result = new JSONObject();
JSONArray dataList = new JSONArray();
long recordsFiltered = 0;
long recordsTotal = 0;

println("resultList SearchAccounts.groovy, line:145 > " + resultList.size());
recordsFiltered = resultList.size();
recordsTotal = resultListSize;

if (UtilValidate.isNotEmpty(resultList)) {
	int id = 1;
	for (GenericValue party : resultList) {
	
		JSONObject data = new JSONObject();
		
		id = id + 1;
		String partyId = party.getString("partyId");
		String statusId = party.getString("statusId");
		String phoneNumber = "";
		String infoString = "";
		if (UtilValidate.isNotEmpty(email) || UtilValidate.isNotEmpty(phone)) {
			phoneNumber = party.getString("contactNumber");
			infoString = party.getString("infoString");
		} else {
			phoneNumber = org.fio.homeapps.util.PartyHelper.getContactNumber(delegator, partyId, "PRIMARY_PHONE");
			infoString = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, partyId, "PRIMARY_EMAIL");
		}
		
		String genCity = "";
		String genStateProvinceGeoId = "";
		GenericValue postalAddress = org.groupfio.common.portal.util.UtilContactMech.getPartyPostal(delegator, partyId, null);
		if(UtilValidate.isNotEmpty(postalAddress)) {
			address1 = postalAddress.getString("address1");
			address2 = postalAddress.getString("address2");
			genCity = postalAddress.getString("city");
			postalCode = postalAddress.getString("postalCode");
			genCountryGeoId = postalAddress.getString("countryGeoId");
			genStateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
		}
		
		data.put("id", id + "");
	    data.put("partyId", partyId);
	    data.put("groupName", party.getString("groupName"));
	    data.put("statusId", UtilValidate.isNotEmpty(statusMap) && UtilValidate.isNotEmpty(statusId) ? statusMap.get(statusId) : "");
	    data.put("phoneNumber", phoneNumber);
	    data.put("infoString", infoString);
	    data.put("city", genCity);
	    data.put("state", UtilValidate.isNotEmpty(stateMap) && UtilValidate.isNotEmpty(genStateProvinceGeoId) ? stateMap.get(genStateProvinceGeoId) : "");
	    dataList.add(data);
	}
	
	result.put("data", dataList);
    result.put("draw", draw);
    result.put("recordsTotal", recordsTotal);
    result.put("recordsFiltered", recordsFiltered);
	return AjaxEvents.doJSONResponse(response, result);
} else {
    result.put("data", dataList);
    result.put("draw", draw);
    result.put("recordsTotal", 0);
    result.put("recordsFiltered", 0);
    return AjaxEvents.doJSONResponse(response, result);
}



