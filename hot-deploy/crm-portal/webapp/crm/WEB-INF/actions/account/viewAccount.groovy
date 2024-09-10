
import org.fio.crm.constants.CrmConstants
import org.fio.crm.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;
import org.fio.crm.util.LoginFilterUtil
import org.fio.crm.util.PermissionUtil;

import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

println("activeTab>>> "+activeTab);

yesNoMap = ["Y":"Yes", "N":"No"];
context.put("yesNoOptions", yesNoMap);

partyId = request.getParameter("partyId");

haveDataPermission = "Y";
String userLoginId = userLogin.getString("partyId");
if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId) && (!PermissionUtil.havePartyViewPermission(delegator, session, partyId))) {
	haveDataPermission = "N";
}
println("haveDataPermission>>> "+haveDataPermission);
context.put("haveDataPermission", haveDataPermission);

//data source list
dataSourceconditions = EntityCondition.makeCondition([
EntityCondition.makeCondition("disable", EntityOperator.NOT_EQUAL, "Y"),
EntityCondition.makeCondition("dataSourceTypeId", EntityOperator.EQUALS, "LEAD_GENERATION")],
EntityOperator.AND);
dataSourceList = delegator.findList("DataSource", dataSourceconditions, null, UtilMisc.toList("description ASC"), null, false);
context.put("dataSourceList",dataSourceList);

EntityCondition thrudateCon1 = EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
EntityCondition thrudateCon2 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp());
EntityCondition dateCondition = EntityCondition.makeCondition(thrudateCon1,EntityOperator.OR,thrudateCon2);
List<EntityCondition> conList = FastList.newInstance();
conList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
conList.add(dateCondition);

//Tax Auth Info
List PartyTaxAuthInfoList = delegator.findList("PartyTaxAuthInfo",EntityCondition.makeCondition(conList,EntityOperator.AND),null,null,null,false);
context.put("PartyTaxAuthInfoList",PartyTaxAuthInfoList);
if(UtilValidate.isNotEmpty(PartyTaxAuthInfoList)){
	PartyTaxAuthInfo = PartyTaxAuthInfoList.get(PartyTaxAuthInfoList.size()-1);
	context.put("partyTaxId",PartyTaxAuthInfo.get("partyTaxId"));
	context.put("isExempt",PartyTaxAuthInfo.get("isExempt"));
	context.put("PartyTaxAuth",PartyTaxAuthInfo);
}
// make sure that the partyId is actually an ACCOUNT before trying to display it as once
delegator = request.getAttribute("delegator");
validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("ACCOUNT"), delegator);

// if not, return right away (otherwise we get spaghetti code)
if ((validRoleTypeId == null) || (!validRoleTypeId.equals("ACCOUNT")))  {
    context.put("validView", false);
    return;
}

// is the account still active?
accountActive = PartyHelper.isActive(partyId, delegator);
dispatcher = request.getAttribute("dispatcher");

// account summary data
partySummary = from("PartySummaryDetailsView").where("partyId", partyId,"partyTypeId","PARTY_GROUP").queryOne();

//System.out.println("partySummary------------>"+partySummary);

if(partySummary!=null && partySummary.size()>0){
	context.put("partySummary", partySummary);
	if (partySummary!=null && partySummary.get("parentPartyId")!=null) {
	    parentParty = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", partySummary.get("parentPartyId")),false);
	    context.put("parentParty", parentParty);
	}
}
// gather data that should only be available for active accounts
if (accountActive) {
	// set this flag to allow contact mechs to be shown
    request.setAttribute("displayContactMechs", "Y");
    // who is currently responsible for account
    responsibleParty = PartyHelper.getCurrentResponsibleParty(partyId, "ACCOUNT", delegator);
    context.put("responsibleParty", responsibleParty);

    // account data sources
    sources = delegator.findByAnd("PartyDataSource", UtilMisc.toMap("partyId", partyId),UtilMisc.toList("fromDate DESC"),true);
    context.put("dataSources", sources);
    dataSourcesAsString = new StringBuffer();
    for (GenericValue ds : sources) {
        dataSource = ds.getRelatedOne("DataSource");
        if (dataSource != null) {
            dataSourcesAsString.append(dataSource.get("description", locale));
            dataSourcesAsString.append(", ");
        }
    }
    if (dataSourcesAsString != null && dataSourcesAsString.length() > 2) 
        context.put("dataSourcesAsString", dataSourcesAsString.toString().substring(0, dataSourcesAsString.length()-2));

    // account marketing campaigns TODO: create MarketingCampaignAndRole entity, then use peformFind service so that we can paginate
    campaignRoles = delegator.findByAnd("MarketingCampaignRole",UtilMisc.toMap("partyId", partyId, "roleTypeId", "ACCOUNT"),null,true);
    campaigns = EntityUtil.getRelated("MarketingCampaign", campaignRoles);
    context.put("marketingCampaigns", campaigns);
    if ((campaignsList = EntityUtil.getFieldListFromEntityList(campaigns, "campaignName", false)) != null) {
        marketingCampaignsAsString = StringUtil.join(campaignsList, ", ");
        if (marketingCampaignsAsString != null && marketingCampaignsAsString.length() > 2)
            context.put("marketingCampaignsAsString", marketingCampaignsAsString);
    }

    // account notes
    results = delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", partyId), UtilMisc.toList("noteDateTime DESC"),true);
    context.put("notesList", results);

    // account team members
    accountTeamMembers = delegator.findByAnd("PartyToSummaryByRelationship", UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", "ACCOUNT", "partyRelationshipTypeId", "ASSIGNED_TO"),null,true);
	accountTeamMembers = EntityUtil.filterByDate(accountTeamMembers);
    context.put("accountTeamMembers", accountTeamMembers);

    // Provide current PartyClassificationGroups as a list and a string
    groups = PartyHelper.getClassificationGroupsForParty(partyId, delegator);
    context.put("partyClassificationGroups", groups);
    descriptions = EntityUtil.getFieldListFromEntityList(groups, "description", false);
    context.put("partyClassificationGroupsDisplay", StringUtil.join(descriptions, ", "));
    
    //get the team list
    conditions = EntityCondition.makeCondition([
    //EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
	EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
	EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_TEAM")],
	EntityOperator.AND);
	teamList = delegator.findList("PartyRoleStatusAndPartyDetail", conditions, null, UtilMisc.toList("groupName ASC"), null, false);
	
	//if (CrmsfaSecurity.hasPartyRelationSecurity(, "CRMSFA_ACCOUNT", "_DEACTIVATE", request.getAttribute("userLogin"), partyId)) {
	Security security = request.getAttribute("security")
	if (security.hasPermission(CrmConstants.SecurityPermissions.CRMSFA_ACCOUNT_DEACTIVATE, userLogin)) {
        context.put("hasDeactivatePermission", true);
    }
	if (security.hasPermission(CrmConstants.SecurityPermissions.CRMSFA_ACCOUNT_REASSIGN, userLogin)) {
        context.put("hasReassignPermission", true);
    }
} else {
    accountDeactivationDate = PartyHelper.getDeactivationDate(partyId, delegator);
    context.put("accountDeactivated", true);
    context.put("accountDeactivatedDate", accountDeactivationDate);
    context.put("validView", true);  // can still view history of deactivated contacts
}

crossReferenceList = delegator.findByAnd("CrossReference",UtilMisc.toMap("crossReferenceTypeId","CUSTOMER_REFERENCE"),null,false);
if(UtilValidate.isNotEmpty(crossReferenceList)){
    context.put("crossReferenceList",crossReferenceList);
}

Map<String, Object> contactAcctMap = new HashMap<String, Object>();
contactAcctMap.put("partyIdTo", partyId);
Map<String, Object> result = dispatcher.runSync("getContactAndAccountAssoc", contactAcctMap);
if(ServiceUtil.isSuccess(result)){
    context.accountContactAssocList = result.accountContactAssoc;
}

/*
hdpCustomerSme = select ("customerFullname","lcin", "gcin", "dateOfRegistration","dbsIndustryName","constitution","rm","seg","segment","team","cifId","acrr","smeWatchlistInd").from("HdpCustomerSme").where("lcin", partyId,).queryOne();
if(UtilValidate.isNotEmpty(hdpCustomerSme)){
	context.put("hdpCustomerSme",hdpCustomerSme);
}*/

