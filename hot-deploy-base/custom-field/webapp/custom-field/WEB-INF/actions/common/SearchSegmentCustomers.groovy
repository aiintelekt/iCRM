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
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.GenericValue;
import org.groupfio.custom.field.util.DataUtil;
import org.ofbiz.party.party.PartyHelper;

import org.fio.campaign.util.LoginFilterUtil;
import org.fio.homeapps.util.ResponseUtils;

import java.util.HashMap;
import java.util.List;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;
import java.util.ArrayList;
import javolution.util.FastList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

HttpSession session = request.getSession();
userLoginId = userLogin.getAt('partyId');
List<String> accountList = new ArrayList<String>();

externalLoginKey=request.getAttribute("externalLoginKey");
println("externalLoginKey> "+externalLoginKey);

if(LoginFilterUtil.checkEmployeePosition(delegator,userLoginId) && UtilValidate.isNotEmpty(session.getAttribute("dataSecurityMetaInfo"))){
	Map<String, Object> dataSecurityMetaInfo = (Map<String, Object>) session.getAttribute("dataSecurityMetaInfo");
	if (ResponseUtils.isSuccess(dataSecurityMetaInfo)) {	
		List<String> lowerPositionPartyIds = (List<String>) dataSecurityMetaInfo.get("lowerPositionPartyIds");
		accountList = LoginFilterUtil.getCampaignsAccountList(delegator, lowerPositionPartyIds);
	}
}

String groupId = request.getParameter("groupId");
String customFieldId = request.getParameter("customFieldId");

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

  if (UtilValidate.isNotEmpty(accountList) && accountList.size() != 0 ){
	EntityCondition accountCondition = EntityCondition.makeCondition("partyId", EntityOperator.IN, accountList);
	conditionsList.add(accountCondition);
	}
	
String groupType = "";
if (UtilValidate.isNotEmpty(groupId)) {
	customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
	groupType = customFieldGroup.getString("groupType");
}
context.put("groupType", groupType);

if (UtilValidate.isNotEmpty(searchRoleTypeId)) {
	println("searchRoleTypeId>> "+searchRoleTypeId);
	conditionsList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, searchRoleTypeId));
}

if (UtilValidate.isNotEmpty(searchPartyId)) {
	EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.LIKE, searchPartyId+"%");
	conditionsList.add(partyCondition);
}

if (UtilValidate.isNotEmpty(searchFirstName)) {
	EntityCondition nameCondition = EntityCondition.makeCondition([
		
		EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, searchFirstName.toUpperCase()+"%"),
		//EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("lastName")), EntityOperator.LIKE, searchFirstName.toUpperCase()+"%"),
		
		EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, searchFirstName.toUpperCase()+"%")
	], EntityOperator.OR);
	conditionsList.add(nameCondition);
}

if (UtilValidate.isNotEmpty(searchEmailId)) {
	conditionsList.add(EntityCondition.makeCondition("primaryEmail", EntityOperator.LIKE, searchEmailId+"%"));
}

if (UtilValidate.isNotEmpty(searchPhoneNum)) {
	conditionsList.add(EntityCondition.makeCondition("primaryContactNumber", EntityOperator.LIKE, searchPhoneNum+"%"));
}

EntityCondition segmentCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId),
	EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, customFieldId)
], EntityOperator.AND);
conditionsList.add(segmentCondition);

String segmentationValueAssociatedEntityName = "PartyClassificationSummaryTwo";
if (UtilValidate.isNotEmpty(groupType) && groupType.equals("ECONOMIC_METRIC")) {
	segmentationValueAssociatedEntityName = "PartyMetricSummaryTwo";
}

EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

EntityFindOptions efo = new EntityFindOptions();
efo.setDistinct(true);

count = delegator.findCountByCondition(segmentationValueAssociatedEntityName, mainConditons, null, UtilMisc.toSet("partyId"), null);

int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 20;
efo.setOffset(startInx);
efo.setLimit(endInx);
println("mainConditons> "+mainConditons);
println("segmentationValueAssociatedEntityName> "+segmentationValueAssociatedEntityName);
partyList = delegator.findList(segmentationValueAssociatedEntityName, mainConditons, null, null, efo, false);
println("size> "+partyList.size());

JSONObject obj = new JSONObject();
JSONArray dataList = new JSONArray();

long recordsFiltered = count;
long recordsTotal = count;

if (partyList != null && partyList.size() > 0) {
	int id = 1;
	for (GenericValue party : partyList) {
	
		JSONObject data = new JSONObject();
	
		partyId = party.partyId;
		roleTypeId = party.roleTypeId;
	
		name = PartyHelper.getPartyName(delegator, partyId, false);
		
		String nameWithId = name + " (" + partyId + ")";
        String nameWithIdLink = "";
        String partyLink = org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom(partyId, roleTypeId, externalLoginKey);
		nameWithIdLink = '<a target="_blank" href="'+partyLink+'">' + nameWithId + '</a>';
		
		String statusItemDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, party.getString("statusId"), "PARTY_STATUS");
				
		state = party.getString("primaryStateProvinceGeoId");
		if (UtilValidate.isNotEmpty(state)) {
			state = org.fio.homeapps.util.DataUtil.getGeoName(delegator, state, "STATE");
		}		
				
		id = id+1;
			
		data.put("id",id+"");
		
		data.put("partyId",partyId);
		data.put("name",name);
		data.put("nameWithId", nameWithId);
		data.put("nameWithIdLink", nameWithIdLink);
		
		data.put("statusId",statusItemDesc);
		data.put("phoneNumber",party.getString("primaryContactNumber"));
		data.put("infoString",party.getString("primaryEmail"));
		data.put("city",party.getString("primaryCity"));
		data.put("state",state);
		
		if (UtilValidate.isNotEmpty(groupType) && groupType.equals("SEGMENTATION")) {
			data.put("groupActualValue", party.groupActualValue);
			data.put("inceptionDate", party.inceptionDate);
		} 
		
		if (UtilValidate.isNotEmpty(groupType) && groupType.equals("ECONOMIC_METRIC")) {
			data.put("propertyValue", party.propertyValue);
		} 
		
		data.put("entryDate", UtilDateTime.timeStampToString(party.getTimestamp("createdStamp"), "dd/MM/yyyy", TimeZone.getDefault(), null));
					
		dataList.add(data);
		
	}
	
	obj.put("data", dataList);
	obj.put("draw", draw);
	obj.put("recordsTotal", recordsTotal);
	obj.put("recordsFiltered", recordsFiltered);
	
	return AjaxEvents.doJSONResponse(response, obj);
} else {

	obj.put("data", dataList);
	obj.put("draw", draw);
	obj.put("recordsTotal", 0);
	obj.put("recordsFiltered", 0);
	
	return AjaxEvents.doJSONResponse(response, obj);
}
