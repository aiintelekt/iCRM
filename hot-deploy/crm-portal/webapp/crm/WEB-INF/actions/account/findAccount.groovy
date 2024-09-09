import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.GenericValue;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityExpr;

accountSearchPartyId = parameters.get("accountSearchPartyId");
searchGroupName = parameters.get("searchGroupName");
searchCompanyName = parameters.get("searchCompanyName");
searchEmailId = parameters.get("searchEmailId");
searchPhoneNum = parameters.get("searchPhoneNum");

delegator = request.getAttribute("delegator");

context.put("accountSearchPartyId", accountSearchPartyId);
context.put("searchGroupName", searchGroupName);
context.put("searchEmailId", searchEmailId);
context.put("searchPhoneNum", searchPhoneNum);

/*conditionsList = FastList.newInstance();
List findList = [];

List<String> partyRelationshipTypeIds = Arrays.asList("RESPONSIBLE_FOR");

EntityCondition roleTypeCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
	EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, partyRelationshipTypeIds)
], EntityOperator.AND);
EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"));
conditionsList.add(roleTypeCondition);


EntityCondition partyStatusCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
	EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)
], EntityOperator.OR);

conditionsList.add(partyStatusCondition);
conditionsList.add(EntityUtil.getFilterByDateExpr());
if (UtilValidate.isNotEmpty(accountSearchPartyId)) {
	context.put("accountSearchPartyId", accountSearchPartyId);
	EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, accountSearchPartyId);
	conditionsList.add(partyCondition);
}

if (UtilValidate.isNotEmpty(searchGroupName)) {
	context.put("searchGroupName", searchGroupName);
	EntityCondition nameCondition = EntityCondition.makeCondition("groupName", EntityOperator.LIKE, searchGroupName+"%");
	conditionsList.add(nameCondition);
}

if (UtilValidate.isNotEmpty(searchCompanyName)) {
	context.put("searchCompanyName", searchCompanyName);
	EntityCondition companyNameCondition = EntityCondition.makeCondition("companyName", EntityOperator.LIKE, searchCompanyName+"%");
	conditionsList.add(companyNameCondition);
} 
List eventExprs = [];

if (UtilValidate.isNotEmpty(searchEmailId) || UtilValidate.isNotEmpty(searchPhoneNum)) {
	context.put("searchEmailId", searchEmailId);
	context.put("searchPhoneNum", searchPhoneNum);

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
efo.setLimit(1000);
parties = delegator.findList("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons, UtilMisc.toSet("partyId"), UtilMisc.toList("createdDate DESC"), efo, false);

if (parties != null && parties.size() > 0) {
	List < String > partyIdList = EntityUtil.getFieldListFromEntityList(parties, "partyId", true);
	if (partyIdList != null && partyIdList.size() > 0) {
		for (String partyId: partyIdList) {
			PartySummaryDetailsViewGv = delegator.findOne("PartySummaryDetailsView", ["partyId": partyId], false);
			if (PartySummaryDetailsViewGv != null && PartySummaryDetailsViewGv.size() > 0) {
				Map partyDetails = new HashMap();
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
				String phoneNumber = "";
				String infoString = "";
				String city = "";
				String state = "";
				List < GenericValue > partyContactMechs = delegator.findByAnd("PartyContactMech", ["partyId": partyId, "allowSolicitation": "Y"], null, false);
				if (partyContactMechs != null && partyContactMechs.size() > 0) {
					partyContactMechs = EntityUtil.filterByDate(partyContactMechs);
					if (partyContactMechs != null && partyContactMechs.size() > 0) {
						partyContactMechs = EntityUtil.getFieldListFromEntityList(partyContactMechs, "contactMechId", true);
					}
					if (partyContactMechs != null && partyContactMechs.size() > 0) {
						Set < String > findOptions = UtilMisc.toSet("contactMechId");
						List < String > orderBy = UtilMisc.toList("createdStamp DESC");

						EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
						EntityCondition condition2 = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, partyContactMechs);

						EntityCondition primaryPhoneConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE")));
						List < GenericValue > primaryPhones = delegator.findList("PartyContactMechPurpose", primaryPhoneConditions, findOptions, orderBy, null, false);
						if (primaryPhones != null && primaryPhones.size() > 0) {
							GenericValue primaryPhone = EntityUtil.getFirst(EntityUtil.filterByDate(primaryPhones));
							if (UtilValidate.isNotEmpty(primaryPhone)) {
								GenericValue primaryPhoneNumber = delegator.findOne("TelecomNumber", ["contactMechId": primaryPhone.getString("contactMechId")], false);
								if (UtilValidate.isNotEmpty(primaryPhoneNumber)) {
									phoneNumber = primaryPhoneNumber.getString("contactNumber");
								}
							}
						}

						EntityCondition primaryEmailConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL")));
						List < GenericValue > primaryEmails = delegator.findList("PartyContactMechPurpose", primaryEmailConditions, findOptions, orderBy, null, false);
						if (primaryEmails != null && primaryEmails.size() > 0) {
							GenericValue primaryEmail = EntityUtil.getFirst(EntityUtil.filterByDate(primaryEmails));
							if (UtilValidate.isNotEmpty(primaryEmail)) {
								GenericValue primaryInfoString = delegator.findOne("ContactMech", ["contactMechId": primaryEmail.getString("contactMechId")], false);
								if (UtilValidate.isNotEmpty(primaryInfoString)) {
									infoString = primaryInfoString.getString("infoString");
								}
							}
						}

						EntityCondition postalAddressConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION")));
						List < GenericValue > primaryAddressList = delegator.findList("PartyContactMechPurpose", postalAddressConditions, findOptions, orderBy, null, false);
						if (primaryAddressList != null && primaryAddressList.size() > 0) {
							GenericValue primaryAddress = EntityUtil.getFirst(EntityUtil.filterByDate(primaryAddressList));
							if (UtilValidate.isNotEmpty(primaryAddress)) {
								GenericValue postalAddress = delegator.findOne("PostalAddress", ["contactMechId": primaryAddress.getString("contactMechId")], false);
								if (UtilValidate.isNotEmpty(postalAddress)) {
									city = postalAddress.getString("city");
									stateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
									if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
										GenericValue geo = delegator.findOne("Geo", ["geoId": stateProvinceGeoId], false);
										if (UtilValidate.isNotEmpty(geo)) {
											state = geo.getString("geoName");
										}
									}
								}
							}
						}
					}
				}
				partyDetails.put("partyId", partyId);
				partyDetails.put("groupName", groupName);
				partyDetails.put("statusItemDesc", statusItemDesc);
				partyDetails.put("dataSourceDesc", dataSourceDesc);
				partyDetails.put("contactNumber", phoneNumber);
				partyDetails.put("emailAddress", infoString);
				partyDetails.put("city", city);
				partyDetails.put("state", state);
				findList.add(partyDetails);
			}
		}
	}
	context.put("findList", findList);
	//println("findList=========" + findList);
}*/
//added Attribute Field on List
List custFLst = [];
List custFAndExprs = [];
custFAndExprs.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "FINDLISTGROUP"));
custFAndExprs.add(EntityCondition.makeCondition("hide", EntityOperator.NOT_EQUAL, "Y"));

List custF = from("CustomField").where(custFAndExprs).orderBy("sequenceNumber").queryList()
for(GenericValue gp :custF){
	Map partyDetails = new HashMap();
	
	customFieldName = gp.getString("customFieldName");
	customFieldId = gp.getString("customFieldId");
	partyDetails.put("customFieldName", customFieldName);
	partyDetails.put("customFieldId", customFieldId);
	custFLst.add(partyDetails);

}

context.put("groupList",custFLst);

