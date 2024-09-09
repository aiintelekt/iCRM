import java.util.List

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.ServiceUtil
import org.fio.crm.constants.CrmConstants
import org.fio.crm.contactmech.PartyPrimaryContactMechWorker;
import org.fio.crm.party.PartyHelper;
import org.fio.crm.util.LoginFilterUtil
import org.fio.crm.util.PermissionUtil;

salutationList = new LinkedHashMap();
salutationList.put("Mr.", "Mr.");
salutationList.put("Ms.", "Ms.");
salutationList.put("Mrs.", "Mrs.");
salutationList.put("Dr.", "Dr.");
salutationList.put("Madam", "Madam");
context.put("salutationList", salutationList);


genderList = new LinkedHashMap();
genderList.put("MALE", "Male");
genderList.put("FEMALE", "Female");
context.put("genderList", genderList);

yesNoMap = ["Y":"Yes", "N":"No"];
context.put("yesNoOptions", yesNoMap);
/* finds all the information relevant to this contact and puts them in the context, so the various forms
and FTLs of this screen can display them correctly */

partyId = parameters.get("partyId");

haveDataPermission = "Y";
String userLoginId = userLogin.getString("partyId");
if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId) && (!PermissionUtil.haveContactViewPermission(delegator, session, partyId))) {
	haveDataPermission = "N";
}
println("haveDataPermission>>> "+haveDataPermission);
context.put("haveDataPermission", haveDataPermission);

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

//make sure that the partyId is actually an CONTACT before trying to display it as once
delegator = request.getAttribute("delegator");
// set this flag to allow contact mechs to be shown
request.setAttribute("displayContactMechs", "Y");
// contact summary data
partySummary = EntityQuery.use(delegator).from("PartySummaryDetailsView").where("partyId", partyId).cache(false).queryOne();
if (partySummary != null) {
    context.put("partySummary", partySummary);
    context.put("currencyUomId", partySummary.getString("preferredCurrencyUomId"));
}
partyRelationship = EntityUtil.getFirst(delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyId), UtilMisc.toList("thruDate"), false));
responsibleName = null;
if (partyRelationship != null) {
    partyItTo = partyRelationship.getString("partyIdTo");
    personGen = delegator.findOne("Person", UtilMisc.toMap("partyId", partyItTo), false);
    if (UtilValidate.isNotEmpty(personGen)) {
        responsibleName = personGen.getString("firstName") + " " + personGen.getString("lastName");
    } else {
        partyGroupGen = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyItTo), false);
        if (UtilValidate.isNotEmpty(partyGroupGen)) {
            responsibleName = partyGroupGen.getString("groupName");

        }

    }
    context.put("responsibleName", responsibleName);
}

//Provide current PartyClassificationGroups as a list and a string
groups = PartyHelper.getClassificationGroupsForParty(partyId, delegator);
context.put("partyClassificationGroups", groups);
descriptions = EntityUtil.getFieldListFromEntityList(groups, "description", false);
context.put("partyClassificationGroupsDisplay", StringUtil.join(descriptions, ", "));

partyClassification = EntityUtil.getFirst(delegator.findByAnd("CustomFieldPartyClassification", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("createdStamp"), false));
if (partyClassification != null) {
	context.put("partyClassificationGroupId", partyClassification.getString("customFieldId"));
}
emaililser = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, partyId)
if (emaililser.PrimaryPhone != null) {
    context.put("partyPrimaryPhone", emaililser.PrimaryPhone);
}
if (emaililser.EmailAddress != null) {
    context.put("partyEmailAddress", emaililser.EmailAddress);
}
// contact marketing campaigns TODO: create MarketingCampaignAndRole entity
campaignRoles = delegator.findByAnd("MarketingCampaignRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER"), null, false);
campaigns = EntityUtil.getRelated("MarketingCampaign", campaignRoles);
context.put("marketingCampaigns", campaigns);
if ((campaignsList = EntityUtil.getFieldListFromEntityList(campaigns, "campaignName", false)) != null) {
	marketingCampaignsAsString = StringUtil.join(campaignsList, ", ");
	if (marketingCampaignsAsString != null && marketingCampaignsAsString.length() > 2) {
		context.put("marketingCampaignsAsString", marketingCampaignsAsString);
	}
}
//Account List Associated with contact
List accountList = [];
/*String partyIdTo = null;
EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
    EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
    EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
    EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"),
    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
    EntityUtil.getFilterByDateExpr());
List < GenericValue > existingRelationships = delegator.findList("PartyRelationship", searchConditions, null, null, null, false);
if (UtilValidate.isNotEmpty(existingRelationships)) {
    for (GenericValue partyRelationship: existingRelationships) {
        Map accountDetails = new HashMap();
        partyIdTo = partyRelationship.getString("partyIdTo");
        accountDetails.put("accountId", partyIdTo)
        emaililser = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, partyIdTo);
        if(emaililser != null && emaililser.size() > 0) {
           accountDetails.put("primaryPhone", emaililser.PrimaryPhone);
           accountDetails.put("emailAddress", emaililser.EmailAddress);
        }
        primaryPostalAddress = PartyPrimaryContactMechWorker.getPartyPrimaryPostal(delegator, partyIdTo);
        if(primaryPostalAddress != null && primaryPostalAddress.size() > 0) {
           accountDetails.put("city", primaryPostalAddress.getString("city"));
        }
        accountList.add(accountDetails);
    }
    context.put("accountList", accountList);
}*/

Map<String, Object> contactAcctMap = new HashMap<String, Object>();
contactAcctMap.put("partyIdFrom", partyId);
Map<String, Object> result = dispatcher.runSync("getContactAndAccountAssoc", contactAcctMap);
if(ServiceUtil.isSuccess(result)){
    context.accountContactAssocList = result.accountContactAssoc;
}

campaignListId = request.getParameter("campaignListId");
if(UtilValidate.isNotEmpty(campaignListId)) {
    GenericValue marketingCampContList = delegator.findOne("MarketingCampaignContactList", UtilMisc.toMap("campaignListId": campaignListId), false);
    if(marketingCampContList != null && marketingCampContList.size() > 0) {
        context.put("marketingCampContList", marketingCampContList);
    }
}
/*accuntConditions = [];
accuntConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT"));
accuntConditions.add(EntityCondition.makeCondition(EntityOperator.OR,
    EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, CrmConstants.PartyStatus.PARTY_DISABLED),
    EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)));

List < GenericValue > partyToAccountByRole = delegator.findList("PartyToSummaryByRole", EntityCondition.makeCondition(accuntConditions, EntityOperator.AND), null, null, null, false);
context.put("partyToAccountByRole", partyToAccountByRole);*/