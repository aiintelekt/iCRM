import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import net.sf.json.JSONObject;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.fio.admin.portal.util.DataHelper
import org.fio.crm.constants.CrmConstants
import org.fio.crm.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;

import java.util.*;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import org.fio.homeapps.util.UtilActivity;
import org.fio.homeapps.util.EnumUtil;

println("start ViewContact controller: "+UtilDateTime.nowTimestamp());

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("contact-portalUiLabels", locale);

String partyId = request.getParameter("partyId");

yesNoMap = ["Y":"Yes", "N":"No"];
context.put("yesNoOptions", yesNoMap);

inputContext = new LinkedHashMap<String, Object>();

partyId = parameters.get("partyId");

context.put("isEnableBasicBar", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_ENABLE_BASIC_BAR", "N"));

haveDataPermission = "Y";
String userLoginId = userLogin.getString("partyId");

println("haveDataPermission>>> "+haveDataPermission);
context.put("haveDataPermission", haveDataPermission);

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

Map<String, Object> appBarContext = new HashMap<String, Object>();
if(partyId!=null){
	primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
	if(UtilValidate.isNotEmpty(primaryContactInformation)) {
		appBarContext.put("primaryEmail",primaryContactInformation.get("EmailAddress"));
		appBarContext.put("emailSolicitation",primaryContactInformation.get("emailSolicitation"));
		//appBarContext.put("primaryPhone", org.groupfio.common.portal.util.DataHelper.preparePhoneNumber(delegator, primaryContactInformation.get("PrimaryPhone")));
		//appBarContext.put("primaryPhone",org.groupfio.common.portal.util.DataHelper.preparePhoneNumber(delegator, primaryContactInformation.get("PrimaryPhone")));
		appBarContext.put("phoneSolicitation",primaryContactInformation.get("phoneSolicitation"));
		
		inputContext.put("partyPrimaryPhone", primaryContactInformation.get("PrimaryPhone"));
		inputContext.put("partyPrimaryEmail", primaryContactInformation.get("EmailAddress"));
	}
	inputContext.put("contactId", partyId);
	partyName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyId, false);
	inputContext.put("contactId_desc", UtilValidate.isNotEmpty(partyName)?partyName:partyId);
	primaryPhoneInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMapsExt(delegator, partyId);
	appBarContext.put("primaryPhone", primaryPhoneInformation.get("PrimaryPhone"));
}


//make sure that the partyId is actually an CONTACT before trying to display it as once
delegator = request.getAttribute("delegator");
// set this flag to allow contact mechs to be shown
request.setAttribute("displayContactMechs", "Y");
context.put("displayContactMechs", "Y");
// contact summary data
partySummary = EntityQuery.use(delegator).from("PartySummaryDetailsView").where("partyId", partyId).cache(false).queryOne();
//println("partySummary> "+partySummary);
if (partySummary != null) {
    context.put("partySummary", partySummary);
    context.put("currencyUomId", partySummary.getString("preferredCurrencyUomId"));
    
    inputContext.putAll(partySummary.getAllFields());
    
    //code to get current time for timezone
	if(UtilValidate.isNotEmpty(partySummary.getString("timeZoneDesc"))){
	   currentTimeForTimezone = UtilActivity.getCurrentTimeFromTimeZone(delegator,partySummary.getString("timeZoneDesc"));
	   inputContext.put("currentTimeForTimezone", currentTimeForTimezone);
	}
	//ended
    
    inputContext.put("birthDate", partySummary.getString("birthDate"));
	inputContext.put("externalId", partySummary.getString("externalId"));
    context.put("partyStatusId", partySummary.getString("statusId"));
    
	String contactName = partySummary.get("firstName");
	if(UtilValidate.isNotEmpty(partySummary.get("lastName")))
		contactName = contactName + " " +partySummary.get("lastName");
	appBarContext.put("name", contactName);
	context.put("name", contactName);
	// fillup administration info
	inputContext.put("createdOn", UtilValidate.isNotEmpty(partySummary.get("createdDate")) ? UtilDateTime.timeStampToString(partySummary.getTimestamp("createdDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	inputContext.put("modifiedOn", UtilValidate.isNotEmpty(partySummary.get("lastModifiedDate")) ? UtilDateTime.timeStampToString(partySummary.getTimestamp("lastModifiedDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	inputContext.put("createdBy", partySummary.get("createdByUserLogin"));
	inputContext.put("modifiedBy", partySummary.get("lastModifiedByUserLogin"));
	
}

context.put("appBarContext", appBarContext);

println("inputContext> "+inputContext);

/*
partyRelationship = org.fio.homeapps.util.PartyHelper.getActivePartyRelationshipByRole("RESPONSIBLE_FOR", partyId, "CONTACT", null, UtilDateTime.nowTimestamp(), delegator);
responsibleName = null;
if (partyRelationship != null) {
	responsibleName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyRelationship.getString("partyIdTo"), false);
    context.put("responsibleName", responsibleName);
}
*/

//Provide current PartyClassificationGroups as a list and a string
groups = PartyHelper.getClassificationGroupsForParty(partyId, delegator);
context.put("partyClassificationGroups", groups);
descriptions = EntityUtil.getFieldListFromEntityList(groups, "customFieldName", false);
context.put("partyClassificationGroupsDisplay", StringUtil.join(descriptions, ", "));

println("start CustomFieldPartyClassification: "+UtilDateTime.nowTimestamp());
partyClassification = EntityQuery.use(delegator).select("customFieldId").from("CustomFieldPartyClassification").where("partyId", partyId).cache().queryFirst();
if (partyClassification != null) {
	context.put("partyClassificationGroupId", partyClassification.getString("customFieldId"));
}
println("end CustomFieldPartyClassification: "+UtilDateTime.nowTimestamp());

// contact marketing campaigns TODO: create MarketingCampaignAndRole entity
campaignRoles = delegator.findByAnd("MarketingCampaignRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CONTACT"), null, false);
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

Map<String, Object> contactAcctMap = new HashMap<String, Object>();
contactAcctMap.put("partyIdFrom", partyId);
//contactAcctMap.put("partyRoleTypeId", "ACCOUNT");
Map<String, Object> result = dispatcher.runSync("common.getContactAndPartyAssoc", contactAcctMap);
if(ServiceUtil.isSuccess(result)){
    context.partyContactAssocList = result.partyContactAssoc;
}
//get Enum list with isenable null and "Y"
String enumTypeId = "CONTENT_CLASS";
enumList = EnumUtil.getEnableEnums(delegator, enumTypeId);
inputContext.put("enumValues", enumList);

campaignListId = request.getParameter("campaignListId");
if(UtilValidate.isNotEmpty(campaignListId)) {
    GenericValue marketingCampContList = delegator.findOne("MarketingCampaignContactList", UtilMisc.toMap("campaignListId": campaignListId), false);
    if(marketingCampContList != null && marketingCampContList.size() > 0) {
        context.put("marketingCampContList", marketingCampContList);
    }
}

context.put("partyRoleTypeId", "CONTACT"); 

partyDesignation = DataHelper.getPartyDesignation(partyId,delegator);
inputContext.put("designation_desc", UtilValidate.isNotEmpty(partyDesignation) ? partyDesignation:"N_A");

context.put("inputContext", inputContext);

context.put("domainEntityId", partyId);
context.put("domainEntityType", "CONTACT");
context.put("requestURI", "viewContact");
context.put("partyId", partyId);
String externalLoginKey = request.getParameter("externalLoginKey");
if(externalLoginKey==null)
externalLoginKey=request.getAttribute("externalLoginKey");
context.put("externalLoginKey", externalLoginKey);
//println("externalLoginKey-------"+externalLoginKey);


ResultSet rs = null;
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Map<String, Object> kpiBarContext = new LinkedHashMap<String, Object>();

String contactSince = "";
String contactStatus = "";
String noOfOpenActivity = "";
String callBackDate = "";
String lastClickedDate = "";
String lastOpenDate = "";

if(UtilValidate.isNotEmpty(partyId)) {
	
	String sinceDateSql = "SELECT DATE(p.created_date) AS 'contact_since', p.STATUS_ID as 'contact_status' FROM party p INNER JOIN party_role pr ON p.party_id=pr.party_id AND pr.role_type_id='CONTACT' AND p.party_id='"+partyId+"' LIMIT 1";
	rs = sqlProcessor.executeQuery(sinceDateSql);
	if (rs != null) {
		while (rs.next()) {
			contactSince = rs.getString("contact_since");
			
			contactStatusId = rs.getString("contact_status");
			if(UtilValidate.isNotEmpty(contactStatusId)) {
				String statusItemSql = "SELECT description FROM status_item WHERE status_id='"+contactStatusId+"'";
				rs = sqlProcessor.executeQuery(statusItemSql);
				if (rs != null) {
					while (rs.next()) {
						contactStatus = rs.getString("description");
					}
				}
			}
		}
	}
	
	//String noOfOpenActivitySql = "SELECT COUNT(*) as 'open_activities' FROM work_effort w INNER JOIN work_Effort_party_assignment we ON w.work_effort_id=we.work_effort_id AND w.current_status_id='IA_OPEN' AND we.role_type_id='CONTACT' AND we.party_id='"+partyId+"'";
	String noOfOpenActivitySql = "SELECT COUNT(DISTINCT w.work_effort_id) as 'open_activities' FROM work_effort w INNER JOIN work_Effort_party_assignment we ON w.work_effort_id=we.work_effort_id 	INNER JOIN work_effort_contact wc ON w.work_effort_id=wc.work_effort_id WHERE ((we.role_type_id='CONTACT' AND we.party_id='"+partyId+"') OR (wc.role_type_id='CONTACT' AND wc.party_id='"+partyId+"')) AND w.current_status_id IN('IA_OPEN','IA_MIN_PROGRESS','IA_MSCHEDULED') LIMIT 1";
	rs = sqlProcessor.executeQuery(noOfOpenActivitySql);
	if (rs != null) {
		while (rs.next()) {
			noOfOpenActivity = rs.getString("open_activities");
		}
	}
	
	//String callBackDateSql = "SELECT DATE(P.CALL_BACK_DATE) as 'call_back_date' FROM PERSON P INNER JOIN PARTY_ROLE PR ON P.PARTY_ID=PR.PARTY_ID AND PR.ROLE_TYPE_ID='CONTACT' AND P.PARTY_ID='"+partyId+"' ORDER BY P.CALL_BACK_DATE DESC ";
	String callBackDateSql = "SELECT DATE(call_back_date) as 'call_back_date' FROM `sales_opportunity` a INNER JOIN `sales_opportunity_role` b ON a.`SALES_OPPORTUNITY_ID`=b.`SALES_OPPORTUNITY_ID` WHERE `ROLE_TYPE_ID`='CONTACT' AND call_back_date IS NOT NULL AND call_back_date>=DATE(NOW()) AND b.party_id='"+partyId+"' ORDER BY call_back_date ASC LIMIT 1";
	rs = sqlProcessor.executeQuery(callBackDateSql);
	if (rs != null) {
		while (rs.next()) {
			callBackDate = rs.getString("call_back_date");
		}
	}
	
	String lastClickedDateSql = "SELECT DATE(CR.clicked_timestamp) as 'last_clicked' FROM clicked_report_summary CR INNER JOIN PARTY_ROLE PR ON CR.PARTY_ID=PR.PARTY_ID AND PR.ROLE_TYPE_ID='CONTACT' AND pr.party_id='"+partyId+"' ORDER BY CR.clicked_timestamp DESC LIMIT 1";
	rs = sqlProcessor.executeQuery(lastClickedDateSql);
	if (rs != null) {
		while (rs.next()) {
			lastClickedDate = rs.getString("last_clicked");
		}
	}
	
	String lastOpenDateSql = "SELECT DATE(CO.OPENED_TIMESTAMP) as 'last_open_date' FROM open_report_summary CO INNER JOIN PARTY_ROLE PR ON CO.PARTY_ID=PR.party_id AND PR.ROLE_TYPE_ID='CONTACT' AND pr.party_id='"+partyId+"' ORDER BY CO.OPENED_TIMESTAMP DESC LIMIT 1";
	rs = sqlProcessor.executeQuery(lastOpenDateSql);
	if (rs != null) {
		while (rs.next()) {
			lastOpenDate= rs.getString("last_open_date");
		}
	}
	
	sqlProcessor.close();
}

kpiBarContext.put("contact-since", contactSince);
kpiBarContext.put("contact-status", contactStatus);
kpiBarContext.put("open-activity-count", noOfOpenActivity);
kpiBarContext.put("call-back-date", callBackDate);
kpiBarContext.put("last-clicked-date", lastClickedDate);
kpiBarContext.put("last-open-date", lastOpenDate);
context.put("kpiBarContext", kpiBarContext);

println("end ViewContact controller: "+UtilDateTime.nowTimestamp());
context.put("isEnableDashboardButton",org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_VIEW_DASHBOARD_BTN_ENABLED", "Y"));

