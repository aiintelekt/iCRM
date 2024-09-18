import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;

import org.fio.crm.party.PartyHelper;
import org.fio.crm.util.DataHelper;
import org.fio.crm.util.ResponseUtils;
import javolution.util.FastList;
import org.fio.crm.util.EnumUtil;
import org.fio.crm.constants.CrmConstants;
import org.fio.crm.util.PermissionUtil;
import org.fio.crm.util.VirtualTeamUtil;
import org.fio.crm.util.DataUtil;

partyId = parameters.get("partyId");
String userLoginId = userLogin.getString("userLoginId");
yesNoMap = ["Y":"Yes", "N":"No"];
context.put("yesNoOptions", yesNoMap);
activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

//make sure that the partyId is actually a LEAD (i.e., a lead) before trying to display it as once
delegator = request.getAttribute("delegator");
validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("LEAD"), delegator);
if ((validRoleTypeId == null) || !"LEAD".equals(validRoleTypeId)) {
    context.put("validView", false);
    return;
}
// lead summary data
partySummary = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", partyId), false);
if (partySummary == null) {
    context.put("validView", false);
    return;
}
context.put("partySummary", partySummary);

String leadName = PartyHelper.getPartyName(delegator, partyId, false);
/*
if (UtilValidate.isNotEmpty(partySummary.get("firstName"))) {
	leadName = partySummary.getString("firstName").concat( UtilValidate.isNotEmpty(partySummary.get("lastName")) ? " " + partySummary.get("lastName") : "" );
} else if (UtilValidate.isNotEmpty(partySummary.get("firstName"))) {
	leadName = partySummary.get("lastName");
}
if (UtilValidate.isEmpty(leadName)) {
	leadName = partySummary.get("groupName");
}
*/
//context.put("leadName", leadName);

//if the lead has already been converted, then just put this in there and nothing
if (partySummary.get("statusId") != null && "LEAD_CONVERTED".equals(partySummary.getString("statusId"))) {
	context.put("hasBeenConverted", true);
	context.put("validView", true);
	return;
}

// so we can view activities, etc.
context.put("validView", true);

// set this flag to allow contact mechs to be shown
request.setAttribute("displayContactMechs", "Y");

// Provide current PartyClassificationGroups as a list and a string
groups = PartyHelper.getClassificationGroupsForParty(partyId, delegator);
context.put("partyClassificationGroups", groups);
descriptions = EntityUtil.getFieldListFromEntityList(groups, "description", false);
context.put("partyClassificationGroupsDisplay", StringUtil.join(descriptions, ", "));

partyClassification = EntityUtil.getFirst(delegator.findByAnd("CustomFieldPartyClassification", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("createdStamp"), false));
if (partyClassification != null) {
	context.put("partyClassificationGroupId", partyClassification.getString("customFieldId"));
}
// lead data sources
sources = FastList.newInstance();
sourcesList = delegator.findByAnd("PartyDataSource", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("fromDate DESC"), false);
context.put("dataSources", sourcesList);

partyRelationship = EntityUtil.getFirst(delegator.findByAnd("PartyRelationship",UtilMisc.toMap("partyIdFrom",partyId),UtilMisc.toList("thruDate"), false));
responsibleName = null;
if(partyRelationship!=null){
	partyItTo = partyRelationship.getString("partyIdTo");
	personGen = delegator.findOne("Person", UtilMisc.toMap("partyId", partyItTo), false);
	if(UtilValidate.isNotEmpty(personGen))
	{
		responsibleName = personGen.getString("firstName") + " "+personGen.getString("lastName");
	}else{
		partyGroupGen = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyItTo), false);
		if(UtilValidate.isNotEmpty(partyGroupGen))
		{
			responsibleName = partyGroupGen.getString("groupName");
		
		}
	
	}
	context.put("responsibleName",responsibleName);
}

// lead marketing campaigns
campaignRoles = delegator.findByAnd("MarketingCampaignRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "LEAD"), null, false);
campaigns = EntityUtil.getRelated("MarketingCampaign", campaignRoles);
context.put("marketingCampaigns", campaigns);
if ((campaignsList = EntityUtil.getFieldListFromEntityList(campaigns, "campaignName", false)) != null) {
	marketingCampaignsAsString = StringUtil.join(campaignsList, ", ");
	if (marketingCampaignsAsString != null && marketingCampaignsAsString.length() > 2) {
		context.put("marketingCampaignsAsString", marketingCampaignsAsString);
	}
}
//data source list
dataSourceconditions = EntityCondition.makeCondition([
EntityCondition.makeCondition("disable", EntityOperator.NOT_EQUAL, "Y"),
EntityCondition.makeCondition("dataSourceTypeId", EntityOperator.EQUALS, "LEAD_GENERATION")],
EntityOperator.AND);
dataSourceList = delegator.findList("DataSource", dataSourceconditions, null, UtilMisc.toList("description ASC"), null, false);
context.put("dataSourceList", DataHelper.getDropDownOptions(dataSourceList, "dataSourceId", "description"));

ownershipList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "PARTY_OWNERSHIP"), UtilMisc.toList("sequenceId ASC"), false);
context.put("ownershipList", DataHelper.getDropDownOptions(ownershipList, "enumId", "description"));

industryEnumList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "PARTY_INDUSTRY"), UtilMisc.toList("sequenceId ASC"), false);
context.put("industryEnumList", DataHelper.getDropDownOptions(industryEnumList, "enumId", "description"));

//PartyFromSummaryByRelationship
//partySupplementalData = EntityUtil.getFirst( delegator.findByAnd("PartyFromSummaryByRelationship", UtilMisc.toMap("partyId", partyId), null, false) );

conditionsList = FastList.newInstance();
conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));

Map<String, Object> dataSecurityMetaInfo = (Map<String, Object>) session.getAttribute("dataSecurityMetaInfo");
if (ResponseUtils.isSuccess(dataSecurityMetaInfo)) {
	String virtualTeamId = null;
	List<String> lowerPositionPartyIds = (List<String>) dataSecurityMetaInfo.get("lowerPositionPartyIds");
	if (UtilValidate.isNotEmpty(lowerPositionPartyIds)) {
					
		List < EntityCondition > securityConditions = new ArrayList < EntityCondition > ();
		
		Map<String, Object> virtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, null, userLogin.getString("partyId"));
		
		if (UtilValidate.isEmpty(virtualTeam.get("virtualTeamId"))) {
			securityConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, lowerPositionPartyIds),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
					EntityUtil.getFilterByDateExpr()
					));
		}
		
		if (UtilValidate.isNotEmpty(userLogin)) {
			securityConditions.add(EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId"))
					//securityConditions
				), EntityOperator.OR));
		}
		
		// virtual team [start]
		
		virtualTeamId = UtilValidate.isNotEmpty(virtualTeamId) ? virtualTeamId : (String) virtualTeam.get("virtualTeamId");
		//String loggedUserVirtualTeamId = (String) dataSecurityMetaInfo.get("loggedUserVirtualTeamId");
		//virtualTeamId = UtilValidate.isNotEmpty(virtualTeamId) ? virtualTeamId : loggedUserVirtualTeamId;
		List<Map<String, Object>> virtualTeamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, null, userLogin.getString("partyId"));
		if (UtilValidate.isNotEmpty(virtualTeamMemberList)) {
			
			Set<String> virtualTeamIdAsLeadList = VirtualTeamUtil.getVirtualTeamIds(virtualTeamMemberList, "VT_SG_TL", true);
			if (UtilValidate.isNotEmpty(virtualTeamIdAsLeadList)) {
				securityConditions.add(EntityCondition.makeCondition("virtualTeamId", EntityOperator.IN, virtualTeamIdAsLeadList));
				Set<String> virtualTeamMemberPartyIdList = new HashSet<String>();
				for (String vtId : virtualTeamIdAsLeadList) {
					List<Map<String, Object>> teamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, vtId, null);
					virtualTeamMemberPartyIdList.addAll( DataUtil.getFieldListFromMapList(teamMemberList, "virtualTeamMemberId", true) );
				}
				
				if (UtilValidate.isNotEmpty(virtualTeamMemberPartyIdList)) {
					securityConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, virtualTeamMemberPartyIdList),
							EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
							EntityUtil.getFilterByDateExpr()
							));
				}
				
			}
			
			Set<String> virtualTeamIdAsMemberList = VirtualTeamUtil.getVirtualTeamIds(virtualTeamMemberList, "VT_SG_TL", false);
			if (UtilValidate.isNotEmpty(virtualTeamIdAsMemberList)) {
				securityConditions.add(EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
						//EntityCondition.makeCondition("virtualTeamId", EntityOperator.IN, virtualTeamIdAsMemberList),
						EntityUtil.getFilterByDateExpr()
					), EntityOperator.AND));
				
			}
			
		}
		
		// virtual team [end]

		EntityCondition securityCondition = EntityCondition.makeCondition(UtilMisc.toList(
				securityConditions
				), EntityOperator.OR);
		
		conditionsList.add(securityCondition);
	}
	
	println("lowerPositionPartyIds> "+lowerPositionPartyIds);
	
}

accessConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
println("accessConditons>>>> "+accessConditons);
partySupplementalData = EntityUtil.getFirst( delegator.findList("PartyFromSummaryByRelationship", accessConditons, null, null, null, false) );
println("partySupplementalData 22222>>>> "+partySupplementalData);
if(UtilValidate.isNotEmpty(partySupplementalData)) {
	context.put("leadName", partySupplementalData.getString("companyName"));
}
dataImportLead = new HashMap();

haveDataPermission = "Y";
if (UtilValidate.isNotEmpty(partySupplementalData)) {
	dataImportLead.putAll(partySupplementalData);
	
	if (UtilValidate.isNotEmpty(partySupplementalData.get("virtualTeamId"))) {
		virtualTeamName = PartyHelper.getPartyName(delegator, partySupplementalData.getString("virtualTeamId"), false);
		context.put("virtualTeamName", virtualTeamName);
	}
	
} else {
	//Login Based lead Filter
	List<GenericValue> userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup")
			.where("userLoginId", userLoginId, "groupId", "FULLADMIN").filterByDate().queryList();
	if(userLoginSecurityGroup == null || userLoginSecurityGroup.size() < 1 && !PermissionUtil.havePartyViewPermission(delegator, session, partyId)){
	    haveDataPermission = "N";
	}
}

context.put("dataImportLead", dataImportLead);

context.put("haveDataPermission", haveDataPermission);

if (UtilValidate.isNotEmpty(dataImportLead.get("parentCoDetails"))) {
	parentParty = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", dataImportLead.get("parentCoDetails")),false);
	context.put("parentParty", parentParty);
}

defaultLeadMarketingCampaignId = UtilProperties.getPropertyValue("crm", "default.lead.marketingCampaignId");
defaultLeadContactListId = UtilProperties.getPropertyValue("crm", "default.lead.contactListId");
	
context.put("defaultLeadMarketingCampaignId", defaultLeadMarketingCampaignId);
context.put("defaultLeadContactListId", defaultLeadContactListId);

callStatusList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "callStatus");
context.put("callStatusList", DataHelper.getDropDownOptions(callStatusList, "enumId", "description"));

disableReasonList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "leadDisableReason");
context.put("disableReasonList", DataHelper.getDropDownOptions(disableReasonList, "enumId", "description"));

context.put("otherUserPositionType", true);
loggedUserPositionType = DataHelper.getEmployeePositionType(delegator, userLogin.getString("partyId"), userLogin.getString("countryGeoId"));
context.put("loggedUserPositionType", loggedUserPositionType);
if (UtilValidate.isNotEmpty(loggedUserPositionType) && (loggedUserPositionType.equals("DBS_TC") || DataHelper.getFirstValidRoleTypeId(loggedUserPositionType, CrmConstants.RM_ROLES))) {
	context.put("otherUserPositionType", false);
}

context.put("dataImportLeadId", null);
checkSource = EntityUtil.getFirst( delegator.findByAnd("PartyIdentification", UtilMisc.toMap("partyId", partyId), null, false) );
if (UtilValidate.isNotEmpty(checkSource)){
	
	cond = EntityCondition.makeCondition([
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("pkCombinedValueText")), EntityOperator.LIKE, "%"+checkSource.get("idValue").toUpperCase()+"%"),
		EntityCondition.makeCondition("validationAuditType", EntityOperator.EQUALS, "VAT_LEAD_IMPORT")
	], EntityOperator.AND);
	importAuditCount = delegator.findCountByCondition("ValidationAuditLog", cond, null, null);
	context.put("importAuditCount", importAuditCount);
	importAuditLogTitle = "View Import Audit Messages";
	if (importAuditCount == 0) {
		importAuditLogTitle = "No Import Audit Log";
	}
	context.put("importAuditLogTitle", importAuditLogTitle);
	
	cond = EntityCondition.makeCondition([
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("pkCombinedValueText")), EntityOperator.LIKE, "%"+checkSource.get("idValue").toUpperCase()+"%"),
		EntityCondition.makeCondition("validationAuditType", EntityOperator.EQUALS, "VAT_LEAD_DEDUP")
	], EntityOperator.AND);
	dedupAuditCount = delegator.findCountByCondition("ValidationAuditLog", cond, null, null);
	context.put("dedupAuditCount", dedupAuditCount);
	dedupAuditLogTitle = "View Dedup Audit Messages";
	if (dedupAuditCount == 0) {
		dedupAuditLogTitle = "No Dedup Audit Log";
	}
	context.put("dedupAuditLogTitle", dedupAuditLogTitle);
	
	context.put("dataImportLeadId", checkSource.get("idValue"));
}



