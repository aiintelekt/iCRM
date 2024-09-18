import org.fio.crm.constants.CrmConstants
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.GenericValue;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastList;

import org.fio.crm.contactmech.PartyPrimaryContactMechWorker;

partyId = parameters.get("partyId");
firstName = parameters.get("firstName");
lastName = parameters.get("lastName");
emailAddress = parameters.get("emailAddress");
contactNumber = parameters.get("contactNumber");

delegator = request.getAttribute("delegator");
context.put("partyId", partyId);
context.put("firstName", firstName);
context.put("lastName", lastName);
context.put("emailAddress", emailAddress);
context.put("contactNumber", contactNumber);
/*conditionsList = FastList.newInstance();
List findList = [];
EntityCondition Contactconsition = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT")
], EntityOperator.AND);
conditionsList.add(Contactconsition);

EntityCondition partyStatusCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
	EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)
], EntityOperator.OR);
conditionsList.add(partyStatusCondition);
conditionsList.add(EntityUtil.getFilterByDateExpr());
if (UtilValidate.isNotEmpty(partyId)) {
	context.put("partyId", partyId);
	EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
	conditionsList.add(partyCondition);
}

if (UtilValidate.isNotEmpty(firstName)) {
	context.put("firstName", firstName);
	EntityCondition nameCondition = EntityCondition.makeCondition("firstName", EntityOperator.LIKE, firstName+"%");
	conditionsList.add(nameCondition);
}

if (UtilValidate.isNotEmpty(lastName)) {
    context.put("lastName", lastName);
    EntityCondition lastNameCondition = EntityCondition.makeCondition("lastName", EntityOperator.LIKE, lastName+"%");
    conditionsList.add(lastNameCondition);
}
List eventExprs = [];

if (UtilValidate.isNotEmpty(emailAddress) || UtilValidate.isNotEmpty(contactNumber)) {
	context.put("emailAddress", emailAddress);
	context.put("contactNumber", contactNumber);

	if (UtilValidate.isNotEmpty(emailAddress)) {
		EntityCondition emailCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, emailAddress),
			EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS")
		], EntityOperator.AND);
		eventExprs.add(emailCondition);
	}

	if (UtilValidate.isNotEmpty(contactNumber)) {
		EntityCondition phoneCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, contactNumber),
			EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "TELECOM_NUMBER")
		], EntityOperator.AND);
		eventExprs.add(phoneCondition);
	}

	conditionsList.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
}
EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
//println("contact mainConditons> "+mainConditons);
EntityFindOptions efo = new EntityFindOptions();
efo.setDistinct(true);
efo.setLimit(1000);
partyId = delegator.findList("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons, UtilMisc.toSet("partyId"), UtilMisc.toList("createdDate DESC"), efo, false);

if (partyId != null && partyId.size() > 0) {
	List < String > partyIdList = EntityUtil.getFieldListFromEntityList(partyId, "partyId", true);
	if (partyIdList != null && partyIdList.size() > 0) {
		for (String leadId: partyIdList) {
			partySummaryCRMView = delegator.findOne("PartySummaryDetailsView", ["partyId": leadId], false);
			if (partySummaryCRMView != null && partySummaryCRMView.size() > 0) {
				Map partyDetails = new HashMap();
				String callBackDate = partySummaryCRMView.getString("callBackDate");
				String companyName = partySummaryCRMView.getString("companyName");
				String statusId = partySummaryCRMView.getString("statusId");
				String generalProfTitle = partySummaryCRMView.getString("generalProfTitle");
				String statusItemDesc = "";
				String name = partySummaryCRMView.getString("firstName");
				if(UtilValidate.isNotEmpty(partySummaryCRMView.getString("lastName"))) {
					name = name+" "+partySummaryCRMView.getString("lastName");
				}
				if (UtilValidate.isNotEmpty(statusId)) {
					statusItem = delegator.findOne("StatusItem", ["statusId": statusId], false);
					if (statusItem != null && statusItem.size() > 0) {
						statusItemDesc = statusItem.getString("description");
					}
				}
				String dataSourceDesc = ""
				partyDataSource = EntityUtil.getFirst(delegator.findByAnd("PartyDataSource", ["partyId": leadId], ["-fromDate"], false));
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
				List < GenericValue > partyContactMechs = delegator.findByAnd("PartyContactMech", ["partyId": leadId, "allowSolicitation": "Y"], null, false);
				if (partyContactMechs != null && partyContactMechs.size() > 0) {
					partyContactMechs = EntityUtil.filterByDate(partyContactMechs);
					if (partyContactMechs != null && partyContactMechs.size() > 0) {
						partyContactMechs = EntityUtil.getFieldListFromEntityList(partyContactMechs, "contactMechId", true);
					}
					if (partyContactMechs != null && partyContactMechs.size() > 0) {
						Set < String > findOptions = UtilMisc.toSet("contactMechId");
						List < String > orderBy = UtilMisc.toList("createdStamp DESC");

						EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadId);
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
				List custFAndExprs1 = [];
				custFAndExprs1.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId));
				custFAndExprs1.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"));
				custFAndExprs1.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"));
				List custFLst1 = from("PartyRelationship").where(custFAndExprs1).orderBy("fromDate DESC").queryList();
				for(GenericValue gps :custFLst1){
				companyId = gps.getString("partyIdTo")
					cmpanyName = from("PartyGroup").where("partyId",companyId).queryOne();
					partyDetails.put("companyName", cmpanyName.getString("groupName"))
					
				}
				partyDetails.put("partyId", leadId);
				partyDetails.put("name", name);
				partyDetails.put("generalProfTitle", generalProfTitle);
				partyDetails.put("callBackDate", callBackDate);
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
}
*/



//Get the list of contact based on the account

/*partyIdTo = parameters.get("partyId");

if(UtilValidate.isNotEmpty(partyIdTo)) {
	conditions= [];
	
	//fields = {"partyIdTo","partyIdFrom","firstName","lastName","city","primaryEmail","primaryContactNumber"};
	Set <String> findOptions = UtilMisc.toSet("partyIdTo");
	findOptions.add("partyIdFrom");
	findOptions.add("firstName");
	findOptions.add("lastName");
	findOptions.add("city");
	findOptions.add("infoString");
	findOptions.add("contactNumber");
	
	EntityCondition roleTypeCondition1 = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"),
		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, CrmConstants.PartyRelationshipTypeConstants.CONTACT_REL_INV)
	], EntityOperator.AND);

	conditions.add(roleTypeCondition1);
	
	
	EntityCondition partyStatusCondition1 = EntityCondition.makeCondition([EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
		EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)
	], EntityOperator.OR);
	
	conditions.add(partyStatusCondition1);
	
	if (UtilValidate.isNotEmpty(partyIdTo)) {
		conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo));
	}
	
	EntityFindOptions efo1 = new EntityFindOptions();
	efo1.setDistinct(true);
    contactList = [];
    conditions.add(EntityUtil.getFilterByDateExpr());
    partyFromReln = delegator.findList("PartyFromByRelnAndContactInfoAndPartyClassification", EntityCondition.makeCondition(conditions, EntityOperator.AND), findOptions, UtilMisc.toList("createdDate"), efo1, false);
    if (partyFromReln != null && partyFromReln.size() > 0) {
        partyFromRelnString = EntityUtil.getFieldListFromEntityList(partyFromReln, "partyIdFrom", true);
        if (partyFromRelnString != null && partyFromRelnString.size() > 0) {
            for (String partyIdFrom: partyFromRelnString) {
                if (UtilValidate.isNotEmpty(partyIdFrom)) {
                    Map contactMap = new HashMap();
                    String contactName = "";
                    String city = "";
                    String phoneNumber = "";
                    String infoString = "";
                    String partyIdTo = "";
                    String roleTypeIdFrom = "";
                    String roleTypeIdTo = "";
	                String statusId = "";
                    GenericValue person = delegator.findOne("Person", ["partyId": partyIdFrom], false);
                    if (person != null && person.size() > 0) {
                        contactName = person.getString("firstName") + " " + person.getString("lastName");
                    }
                    Map < String, String > partyContactInfo = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, partyIdFrom);
                    phoneNumber = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("PrimaryPhone") : "";
                    infoString = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("EmailAddress") : "";

                    GenericValue postalAddress = PartyPrimaryContactMechWorker.getPartyPrimaryPostal(delegator, partyIdFrom);
                    city = UtilValidate.isNotEmpty(postalAddress) ? postalAddress.get("city") : "";
                    paramCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                    conditions1 = EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom);
                    conditions2 = EntityCondition.makeCondition([conditions1, paramCond], EntityOperator.AND);
                    partyRelationshipGV = EntityQuery.use(delegator).from("PartyRelationship")
                                        .where(conditions2).orderBy("fromDate DESC").queryFirst();
                    if(partyRelationshipGV != null && partyRelationshipGV.size() > 0) {
                        partyIdTo = partyRelationshipGV.getString("partyIdTo");
                        roleTypeIdFrom = partyRelationshipGV.getString("roleTypeIdFrom");
                        roleTypeIdTo = partyRelationshipGV.getString("roleTypeIdTo");
                        statusId = partyRelationshipGV.getString("statusId");
                    }
                    contactMap.put("partyIdFrom", partyIdFrom);
                    contactMap.put("contactName", contactName);
                    contactMap.put("city", city);
                    contactMap.put("phoneNumber", phoneNumber);
                    contactMap.put("infoString", infoString);
                    contactMap.put("partyIdTo", partyIdTo);
                    contactMap.put("roleTypeIdFrom", roleTypeIdFrom);
                    contactMap.put("roleTypeIdTo", roleTypeIdTo);
                    contactMap.put("statusId", statusId);
                    contactList.add(contactMap);
                }
            }
        }
    }
    context.put("contactList", contactList);
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
