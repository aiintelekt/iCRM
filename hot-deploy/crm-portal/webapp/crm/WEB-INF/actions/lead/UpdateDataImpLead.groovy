import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.fio.crm.util.DataHelper;
import org.fio.crm.util.EnumUtil;
import org.fio.crm.util.PermissionUtil;
import org.fio.crm.party.PartyHelper;
import org.fio.crm.constants.CrmConstants;
import org.fio.crm.util.VirtualTeamUtil;
import org.fio.crm.ajax.AjaxEvents;

import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import org.fio.crm.util.ResponseUtils;
import javolution.util.FastList;
import org.fio.crm.util.DataUtil;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("crmUiLabels", locale);

actionType = context.get("actionType");
context.put("actionType", actionType);

dataImportLead = new HashMap();

haveDataPermission = "Y";

leadId = UtilValidate.isNotEmpty(request.getParameter("leadId")) ? request.getParameter("leadId") : request.getParameter("partyId");
if (UtilValidate.isNotEmpty(leadId)) {

	cond = EntityCondition.makeCondition([
		EntityCondition.makeCondition("leadId", EntityOperator.EQUALS, leadId),
		EntityCondition.makeCondition("primaryPartyId", EntityOperator.EQUALS, leadId)
	], EntityOperator.OR);

	dataImportLead = EntityUtil.getFirst( delegator.findList("DataImportLead", cond, null, null, null, false) );
	
	//Login Based lead Filter
	String userLoginId = userLogin.getString("userLoginId");
	 userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup")
			.where("userLoginId", userLoginId, "groupId", "FULLADMIN").filterByDate().queryList();
	if ((userLoginSecurityGroup == null || userLoginSecurityGroup.size() < 1) && !PermissionUtil.havePartyViewPermission(delegator, session, leadId)) {
		haveDataPermission = "N";
	}
	
}
context.put("leadId", leadId);

context.put("dataImportLead", dataImportLead);
println("dataImportLead>>> "+dataImportLead);
context.put("haveDataPermission", haveDataPermission);

String leadName = PartyHelper.getPartyName(delegator, leadId, false);
if (UtilValidate.isEmpty(leadName)) {
	if (UtilValidate.isNotEmpty(dataImportLead.get("firstName"))) {
		leadName = dataImportLead.getString("firstName").concat( UtilValidate.isNotEmpty(dataImportLead.get("lastName")) ? " " + dataImportLead.get("lastName") : "" );
	} else if (UtilValidate.isNotEmpty(dataImportLead.get("firstName"))) {
		leadName = dataImportLead.get("lastName");
	}
}
context.put("leadName", leadName);
	
appStatus = new HashMap();
context.put("appStatus", appStatus);

appStatusList = UtilMisc.toMap("ACTIVATED", uiLabelMap.get("activated"), "DEACTIVATED", uiLabelMap.get("deActivated"));
context.put("appStatusList", appStatusList);

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);

customerTradingTypeList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "customerTradingType");
context.put("customerTradingTypeList", DataHelper.getDropDownOptions(customerTradingTypeList, "enumId", "description"));

tallyUserTypeList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "tallyUserType");
context.put("tallyUserTypeList", DataHelper.getDropDownOptions(tallyUserTypeList, "enumId", "description"));

liabOrAssetList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "liabOrAsset");
context.put("liabOrAssetList", DataHelper.getDropDownOptions(liabOrAssetList, "enumId", "description"));

tcpNameList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "tcpName");
context.put("tcpNameList", DataHelper.getDropDownOptions(tcpNameList, "enumId", "description"));

codeList = new LinkedHashMap<String, Object>();
if (dataImportLead != null && UtilValidate.isNotEmpty(dataImportLead.get("errorCodes"))) {
	
	errorCodeList = StringUtil.split(dataImportLead.getString("errorCodes"), ",");
	errorCodeList.each{ errorCode ->
		
		code = EntityUtil.getFirst( delegator.findByAnd("ErrorCode", UtilMisc.toMap("errorCodeId", errorCode, "ErrorCodeType", "LEAD_IMPORT"), null, false) );
		if (UtilValidate.isNotEmpty(code)) {
			
			toolTip = code.get("codeDescription");
			
			codeList.put(errorCode, toolTip);
		}
	}
	
}
context.put("codeList", codeList);

industryCatList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "industryCat");
context.put("industryCatList", DataHelper.getDropDownOptions(industryCatList, "enumId", "description"));

industryList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "industry");
industryOptions = new LinkedHashMap<String, Object>();
industryList.each{ industry ->
	industryOptions.put(industry.getString("enumId"), "("+industry.getString("enumCode")+") "+industry.getString("description"));
}
context.put("industryList", industryOptions);

indiaStateList = delegator.findByAnd("GeoAssocSummary", UtilMisc.toMap("geoId", userLogin.getString("countryGeoId"), "geoAssocTypeId", "REGIONS"), null, false);
context.put("indiaStateList", DataHelper.getDropDownOptions(indiaStateList, "geoIdTo", "geoName"));


callingStatusList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "callingStatus");
context.put("callingStatusList", DataHelper.getDropDownOptions(callingStatusList, "enumId", "description"));

rmCallStatusList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "rmCallStatus");
context.put("rmCallStatusList", DataHelper.getDropDownOptions(rmCallStatusList, "enumId", "description"));

teleCallStatusList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "teleCallStatus");
context.put("teleCallStatusList", DataHelper.getDropDownOptions(teleCallStatusList, "enumId", "description"));


constitutionList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "constitution");
context.put("constitutionList", DataHelper.getDropDownOptions(constitutionList, "enumId", "description"));

incorporationPlaceList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "incorporationPlace");
context.put("incorporationPlaceList", DataHelper.getDropDownOptions(incorporationPlaceList, "enumId", "description"));

prodPhobList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "prodPhob");
context.put("prodPhobList", DataHelper.getDropDownOptions(prodPhobList, "enumId", "description"));

existingBankList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "existingBank");
context.put("existingBankList", DataHelper.getDropDownOptions(existingBankList, "enumId", "description"));

designationList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "leadDesignation");
context.put("designationList", DataHelper.getDropDownOptions(designationList, "enumId", "description"));

leadScoreList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "leadScore");
context.put("leadScoreList", DataHelper.getDropDownOptions(leadScoreList, "enumId", "description"));

backUrl = request.getParameter("backUrl");
if (UtilValidate.isEmpty(backUrl) && (UtilValidate.isNotEmpty(actionType) && actionType.equals("UPDATE"))) {
	backUrl = "viewLead?partyId="+leadId;
}
context.put("backUrl", backUrl);

leadSourceList = new LinkedHashMap<String, Object>();

sourceList = delegator.findByAnd("PartyIdentificationType", UtilMisc.toMap("parentTypeId", "LEAD_SOURCE"), UtilMisc.toList("partyIdentificationTypeId"), false);
sourceList.each{ source ->
	leadSourceList.put(source.getString("partyIdentificationTypeId"), "("+source.getString("partyIdentificationTypeId")+") "+source.getString("description"));
}
context.put("leadSourceList", leadSourceList);

segmentList = new LinkedHashMap<String, Object>();

segmentList.put("IBG3", "IBG3");
segmentList.put("IBG4", "IBG4");

context.put("segmentList", segmentList);

jobFamilyList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "jobFamily");
context.put("jobFamilyList", DataHelper.getDropDownOptions(jobFamilyList, "enumId", "description"));

sourceList = delegator.findByAnd("PartyIdentificationType", UtilMisc.toMap("parentTypeId", "LEAD_SOURCE"), UtilMisc.toList("partyIdentificationTypeId"), false);
sourceList.each{ source ->
	leadSourceList.put(source.getString("partyIdentificationTypeId"), "("+source.getString("partyIdentificationTypeId")+") "+source.getString("description"));
}
context.put("leadSourceList", leadSourceList);
/*
cityList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "city");
context.put("cityList", DataHelper.getDropDownOptions(cityList, "enumId", "description"));
*/
/*
condition = UtilMisc.toMap("groupType", "SEGMENTATION");
condition.put("roleTypeId", "LEAD");
cond = EntityCondition.makeCondition(condition);
println("cond>>>> "+cond);
segmentValueList = delegator.findList("CustomFieldGroupSummary", cond, null, ["sequenceNumber"], null, false);
context.put("segmentValueList", DataHelper.getDropDownOptions(segmentValueList, "customFieldId", "customFieldName"));
*/

titles = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "SALUTATION"), UtilMisc.toList("sequenceId"), false);
context.put("titleList", DataHelper.getDropDownOptions(titles,"enumId", "description"));

context.put("otherUserPositionType", true);
loggedUserPositionType = DataHelper.getEmployeePositionType(delegator, userLogin.getString("partyId"), userLogin.getString("countryGeoId"));
context.put("loggedUserPositionType", loggedUserPositionType);
if (UtilValidate.isNotEmpty(loggedUserPositionType) && (loggedUserPositionType.equals("DBS_TC") || DataHelper.getFirstValidRoleTypeId(loggedUserPositionType, CrmConstants.RM_ROLES))) {
	context.put("otherUserPositionType", false);
}

//city
cityList = delegator.findByAnd("GeoAssocSummary", UtilMisc.toMap("geoTypeId", "CITY"), null, false);
context.put("cityList", DataHelper.getDropDownOptions(cityList, "geoIdTo", "geoName"));
//Constitution
constitutionList = EnumUtil.getEnums(delegator, userLogin.getString("countryGeoId"), "constitution");
context.put("constitutionList", DataHelper.getDropDownOptions(constitutionList, "enumId", "description"));

if (UtilValidate.isNotEmpty(dataImportLead)){

	cond = EntityCondition.makeCondition([
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("pkCombinedValueText")), EntityOperator.LIKE, "%"+dataImportLead.get("leadId").toUpperCase()+"%"),
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
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("pkCombinedValueText")), EntityOperator.LIKE, "%"+dataImportLead.get("leadId").toUpperCase()+"%"),
		EntityCondition.makeCondition("validationAuditType", EntityOperator.EQUALS, "VAT_LEAD_DEDUP")
	], EntityOperator.AND);
	dedupAuditCount = delegator.findCountByCondition("ValidationAuditLog", cond, null, null);
	context.put("dedupAuditCount", dedupAuditCount);
	dedupAuditLogTitle = "View Dedup Audit Messages";
	if (dedupAuditCount == 0) {
		dedupAuditLogTitle = "No Dedup Audit Log";
	}
	context.put("dedupAuditLogTitle", dedupAuditLogTitle);
	
}

context.put("virtualTeamList", DataHelper.getDropDownOptions(VirtualTeamUtil.getVirtualTeamList(delegator, userLogin.getString("countryGeoId"), userLogin.getString("partyId")), "partyId", "groupName"));

context.put("loggedUserVirtualTeamId", VirtualTeamUtil.getVirtualTeamId(delegator, userLogin.getString("partyId")));
def city= "";
def country= "IND";

state = AjaxEvents.getInstance(request);
rmList = state.getRMs(delegator, city, country);
LinkedList descFields = ["firstName", "lastName"];
context.rmList =  DataHelper.getDropDownOptionsFromMultiDesField(rmList, "partyId", descFields);

if(UtilValidate.isNotEmpty(actionType) && actionType.equals("UPDATE")) {
String userLoginId = userLogin.getString("userLoginId");

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

//make sure that the partyId is actually a LEAD (i.e., a lead) before trying to display it as once
delegator = request.getAttribute("delegator");
validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(leadId, UtilMisc.toList("LEAD"), delegator);
if ((validRoleTypeId == null) || !"LEAD".equals(validRoleTypeId)) {
    context.put("validView", false);
    return;
}
// lead summary data
partySummary = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", leadId), false);
if (partySummary == null) {
    context.put("validView", false);
    return;
}
context.put("partySummary", partySummary);

 leadName = PartyHelper.getPartyName(delegator, leadId, false);
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
context.put("leadName", leadName);

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
groups = PartyHelper.getClassificationGroupsForParty(leadId, delegator);
context.put("partyClassificationGroups", groups);
descriptions = EntityUtil.getFieldListFromEntityList(groups, "description", false);
context.put("partyClassificationGroupsDisplay", StringUtil.join(descriptions, ", "));

partyClassification = EntityUtil.getFirst(delegator.findByAnd("CustomFieldPartyClassification", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("createdStamp"), false));
if (partyClassification != null) {
	context.put("partyClassificationGroupId", partyClassification.getString("customFieldId"));
}
// lead data sources
sources = FastList.newInstance();
sourcesList = delegator.findByAnd("PartyDataSource", UtilMisc.toMap("partyId", leadId), UtilMisc.toList("fromDate DESC"), false);
context.put("dataSources", sourcesList);

partyRelationship = EntityUtil.getFirst(delegator.findByAnd("PartyRelationship",UtilMisc.toMap("partyIdFrom",leadId),UtilMisc.toList("thruDate"), false));
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
campaignRoles = delegator.findByAnd("MarketingCampaignRole", UtilMisc.toMap("partyId", leadId, "roleTypeId", "LEAD"), null, false);
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
//partySupplementalData = EntityUtil.getFirst( delegator.findByAnd("PartyFromSummaryByRelationship", UtilMisc.toMap("partyId", leadId), null, false) );

conditionsList = FastList.newInstance();
conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadId));

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
    if(userLoginSecurityGroup == null || userLoginSecurityGroup.size() < 1 && !PermissionUtil.havePartyViewPermission(delegator, session, leadId)){
        haveDataPermission = "N";
    }
}

context.put("dataImportLead", dataImportLead);

}