import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import net.sf.json.JSONObject;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.fio.crm.constants.CrmConstants
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
//import org.fio.crm.util.LoginFilterUtil
//import org.fio.crm.util.PermissionUtil;

import java.util.*;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import java.text.DecimalFormat;
import org.fio.homeapps.util.UtilActivity;
import org.fio.homeapps.util.EnumUtil;
delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("lead-portalUiLabels", locale);

String partyId = request.getParameter("partyId");
String isView = context.get("isView");

String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

inputContext = new LinkedHashMap<String, Object>();

context.put("isEnableBasicBar", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_ENABLE_BASIC_BAR", "N"));

inputContext.put("partyId", partyId);
inputContext.put("accountType", "Lead");

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

//println("activeTab>>> "+activeTab);

yesNoMap = ["Y":"Yes", "N":"No"];
context.put("yesNoOptions", yesNoMap);

leadConvertOptions = ["ACCOUNT":"Account", "CUSTOMER":"Customer"];
context.put("leadConvertOptions", leadConvertOptions);

partyId = request.getParameter("partyId");

haveDataPermission = "Y";
String userLoginId = userLogin.getString("partyId");

Map<String, Object> appBarContext = new HashMap<String, Object>();
if(partyId!=null){
	
}

//println("haveDataPermission>>> "+haveDataPermission);
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
//println("PartyTaxAuthInfoList>>> "+PartyTaxAuthInfoList);
if(UtilValidate.isNotEmpty(PartyTaxAuthInfoList)){
	PartyTaxAuthInfo = PartyTaxAuthInfoList.get(PartyTaxAuthInfoList.size()-1);
	inputContext.put("partyTaxId",PartyTaxAuthInfo.get("partyTaxId"));
	inputContext.put("isExempt",PartyTaxAuthInfo.get("isExempt"));
	context.put("PartyTaxAuth",PartyTaxAuthInfo);
}
// make sure that the partyId is actually an LEAD before trying to display it as once
delegator = request.getAttribute("delegator");
validRoleTypeId = org.fio.homeapps.util.PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("LEAD"), delegator);

// if not, return right away (otherwise we get spaghetti code)
if ((validRoleTypeId == null) || (!validRoleTypeId.equals("LEAD")))  {
    context.put("validView", false);
    return;
}

// is the account still active?
accountActive = org.fio.homeapps.util.PartyHelper.isActive(partyId, delegator);
dispatcher = request.getAttribute("dispatcher");

// lead summary data
partySummary = from("PartySummaryDetailsView").where("partyId", partyId,"partyTypeId","PARTY_GROUP").queryOne();
//System.out.println("partySummary------------>"+partySummary);

if(partySummary!=null && partySummary.size()>0){
	context.put("partySummary", partySummary);
	
	inputContext.putAll(partySummary.getAllFields());
		
	if (UtilValidate.isNotEmpty(isView) && "Y".equals(isView)) {
		if (UtilValidate.isNotEmpty(partySummary.get("annualRevenue"))) {
			DecimalFormat myFormatter = new DecimalFormat("###,###.###");
			String formattedAnnualRevenue = myFormatter.format(partySummary.get("annualRevenue"));
			inputContext.put("annualRevenue", formattedAnnualRevenue);
		}
	}
	
	if (partySummary!=null && partySummary.get("parentPartyId")!=null) {
	    parentParty = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", partySummary.get("parentPartyId")),false);
	    context.put("parentParty", parentParty);
	    
	    inputContext.put("parentPartyId_desc", parentParty.getString("groupName"));
	    if (request.getRequestURI().contains("view")) {
	    	inputContext.put("parentPartyId", parentParty.getString("groupName"));
	    }
	}
		
	inputContext.put("leadName", partySummary.get("groupName"));
	inputContext.put("currencyUomId", partySummary.get("preferredCurrencyUomId"));
	//inputContext.put("email", partySummary.get("primaryEmail"));
	//inputContext.put("contactNumber", partySummary.get("primaryContactNumber"));
	
	appBarContext.put("name", partySummary.get("groupName"));
	
	// fillup administration info
	inputContext.put("createdOn", UtilValidate.isNotEmpty(partySummary.get("createdDate")) ? UtilDateTime.timeStampToString(partySummary.getTimestamp("createdDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
	inputContext.put("modifiedOn", UtilValidate.isNotEmpty(partySummary.get("lastModifiedDate")) ? UtilDateTime.timeStampToString(partySummary.getTimestamp("lastModifiedDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
	inputContext.put("createdBy", partySummary.get("createdByUserLogin"));
	inputContext.put("modifiedBy", partySummary.get("lastModifiedByUserLogin"));	
	
}

// gather data that should only be available for active lead
if (accountActive) {
	// set this flag to allow contact mechs to be shown
    request.setAttribute("displayContactMechs", "Y");
	context.put("displayContactMechs", "Y");
    
    // Provide current PartyClassificationGroups as a list and a string
    groups = org.fio.homeapps.util.PartyHelper.getClassificationGroupsForParty(partyId, delegator);
    context.put("partyClassificationGroups", groups);
    descriptions = EntityUtil.getFieldListFromEntityList(groups, "customFieldName", false);    
    inputContext.put("segment", StringUtil.join(descriptions, ", "));
    if(UtilValidate.isNotEmpty(groups)){
    	 customIds = EntityUtil.getFieldListFromEntityList(groups, "customFieldId", false);
    	inputContext.put("partyClassificationGroupId", customIds.get(0));
    }
    
    //get the team list
    conditions = EntityCondition.makeCondition([
    //EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
	EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
	EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_TEAM")],
	EntityOperator.AND);
	teamList = delegator.findList("PartyAndRoleAndGroup", conditions, null, UtilMisc.toList("groupName ASC"), null, false);
	
	if ((teamNameList = EntityUtil.getFieldListFromEntityList(teamList, "groupName", false)) != null) {
        teamNames = StringUtil.join(teamNameList, ", ");
        if (UtilValidate.isNotEmpty(teamNames)) {
        	inputContext.put("teamId", teamNames);
        }
    }
	
	//if (CrmsfaSecurity.hasPartyRelationSecurity(, "CRMSFA_ACCOUNT", "_DEACTIVATE", request.getAttribute("userLogin"), partyId)) {
	Security security = request.getAttribute("security")
	if (security.hasPermission(CrmConstants.SecurityPermissions.CRMSFA_ACCOUNT_DEACTIVATE, userLogin)) {
        context.put("hasDeactivatePermission", true);
    }
	if (security.hasPermission(CrmConstants.SecurityPermissions.CRMSFA_ACCOUNT_REASSIGN, userLogin)) {
        context.put("hasReassignPermission", true);
    }
	
	curStatusItem = from("StatusItem").where("statusId", partySummary.get("statusId")).queryOne();
	if(UtilValidate.isNotEmpty(curStatusItem)){
		inputContext.put("leadStatus", curStatusItem.description);
	}
    
} else {
    leadDeactivationDate = org.fio.homeapps.util.PartyHelper.getDeactivationDate(partyId, delegator);
    context.put("leadDeactivated", true);
    context.put("leadDeactivatedDate", leadDeactivationDate);
    context.put("validView", true);  // can still view history of deactivated contacts
    
    inputContext.put("leadStatus", "Disable");
}
//get Enum list with isenable null and "Y"
String enumTypeId = "CONTENT_CLASS";
enumList = EnumUtil.getEnableEnums(delegator, enumTypeId);
inputContext.put("enumValues", enumList);
Map<String, Object> primaryContact = org.groupfio.common.portal.util.DataUtil.getPrimaryContact(delegator, partyId, "LEAD");
if (UtilValidate.isNotEmpty(primaryContact)) {
	primaryContactId = (String) primaryContact.get("contactId");
	primaryContactName = (String) primaryContact.get("contactName");
	//primaryContactEmail = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, primaryContactId, "PRIMARY_EMAIL");
	//primaryContactPhone = org.fio.homeapps.util.PartyHelper.getContactNumber(delegator, primaryContactId, "PRIMARY_PHONE");
	
	primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,primaryContactId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
	if(UtilValidate.isNotEmpty(primaryContactInformation)) {
		inputContext.put("PrimaryContact", primaryContactName);
		inputContext.put("PrimaryContactEmail", primaryContactInformation.get("EmailAddress"));
		inputContext.put("PrimaryContactPhone", primaryContactInformation.get("PrimaryPhone"));
		
		appBarContext.put("primaryEmail", primaryContactInformation.get("EmailAddress"));
		appBarContext.put("emailSolicitation", primaryContactInformation.get("emailSolicitation"));
		//appBarContext.put("primaryPhone", org.groupfio.common.portal.util.DataHelper.preparePhoneNumber(delegator, primaryContactInformation.get("PrimaryPhone")));
		appBarContext.put("phoneSolicitation", primaryContactInformation.get("phoneSolicitation"));
	}
	primaryPhoneInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMapsExt(delegator, partyId);
	appBarContext.put("primaryPhone", primaryPhoneInformation.get("PrimaryPhone"));
	
	//added for getting current time of primary contact
	if(UtilValidate.isNotEmpty(primaryContact.get("timeZoneDesc"))){
	  	currentTimeForTimezone = UtilActivity.getCurrentTimeFromTimeZone(delegator, primaryContact.get("timeZoneDesc"));
	  	inputContext.put("currentTimeForTimezone", currentTimeForTimezone);
	}
		
}
context.put("appBarContext", appBarContext);

Map<String, Object> contactAcctMap = new HashMap<String, Object>();
contactAcctMap.put("partyIdTo", partyId);
contactAcctMap.put("partyRoleTypeId", "LEAD");
Map<String, Object> result = dispatcher.runSync("common.getContactAndPartyAssoc", contactAcctMap);
if(ServiceUtil.isSuccess(result)){
    context.partyContactAssocList = result.partyContactAssoc;
}

String relationshipManager = org.fio.homeapps.util.PartyHelper.getCurrentResponsiblePartyName(partyId, "LEAD", delegator);
inputContext.put("personResponsible", relationshipManager);

relationManager =org.fio.homeapps.util.PartyHelper.getCurrentResponsibleParty(partyId, "LEAD", delegator);
String rMId=UtilValidate.isNotEmpty(relationManager)?relationManager.partyId:null;
context.put("selectedRMId", rMId);
context.put("partyRoleTypeId", "LEAD");

inputContext.put("domainEntityId1", partyId);
inputContext.put("domainEntityType1", "LEAD");

println('inputContext> '+inputContext);
context.put("inputContext", inputContext);

context.put("domainEntityId", partyId);
context.put("domainEntityType", "LEAD");
context.put("requestURI", "viewLead");
context.put("partyId", partyId);

String externalLoginKey = request.getParameter("externalLoginKey");
if(externalLoginKey==null)
	externalLoginKey=request.getAttribute("externalLoginKey");
context.put("externalLoginKey", externalLoginKey);

//println("externalLoginKey-------"+externalLoginKey);

List<String> roles = new ArrayList<>();
String globalConfig = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LEAD_OWNER");
if (UtilValidate.isNotEmpty(globalConfig) && globalConfig.contains(",")) {
	roles = org.fio.admin.portal.util.DataUtil.stringToList(globalConfig, ",");
} else if (UtilValidate.isNotEmpty(globalConfig)) {
	roles.add(globalConfig);
}
List<GenericValue> reassignOwners = EntityQuery.use(delegator).select("roleTypeId","description").from("RoleType").where(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roles)).queryList();
context.put("reassignOwnerList", UtilValidate.isNotEmpty(reassignOwners) ? org.fio.admin.portal.util.DataUtil.getMapFromGeneric(reassignOwners, "roleTypeId", "description", false) : new HashMap<String, Object>());

ResultSet rs = null;
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Map<String, Object> kpiBarContext = new LinkedHashMap<String, Object>();

String leadSince = "";
String leadStatus = "";
String mostRecentCamp = "";
String noOfOppoExpire = "";
String noOfOpenOppo = "";
String noOfOpenActivity = "";
String noOfActiveCamp = "";
String callBackDate = "";
String lastClickedDate = "";
String lastOpenDate = "";
String leadStatusId = "";
if(UtilValidate.isNotEmpty(partyId)) {
	
	String sinceDateSql = "SELECT DATE(p.created_date) AS lead_since FROM party p INNER JOIN party_role pr ON p.party_id=pr.party_id AND pr.role_type_id='LEAD' AND p.party_id='"+partyId+"'";
	rs = sqlProcessor.executeQuery(sinceDateSql);
	if (rs != null) {
		while (rs.next()) {
			leadSince = rs.getString("lead_since");
		}
	}
	
	String leadStatusSql = "SELECT p.STATUS_ID as 'lead_status' FROM PARTY P INNER JOIN PARTY_ROLE PR ON P.PARTY_ID=PR.PARTY_ID AND PR.ROLE_TYPE_ID='LEAD' AND pr.party_id='"+partyId+"'";
	rs = sqlProcessor.executeQuery(leadStatusSql);
	if (rs != null) {
		while (rs.next()) {
			leadStatusId = rs.getString("lead_status");
		}
		if(UtilValidate.isNotEmpty(leadStatusId)) {
			String statusItemSql = "SELECT description FROM status_item WHERE status_id='"+leadStatusId+"'";
			rs = sqlProcessor.executeQuery(statusItemSql);
			if (rs != null) {
				while (rs.next()) {
					leadStatus = rs.getString("description");
				}
			}
		}
	}
	
	String mostRecentCampSql = "SELECT MC.CAMPAIGN_NAME as 'most_recent_camp' FROM MARKETING_CAMPAIGN MC INNER JOIN `marketing_campaign_contact_list` CL ON MC.MARKETING_CAMPAIGN_ID=CL.MARKETING_CAMPAIGN_ID INNER JOIN CONTACT_LIST_PARTY CLP ON CLP.CONTACT_LIST_ID=CL.CONTACT_LIST_ID INNER JOIN PARTY_ROLE PR ON PR.PARTY_ID=CLP.PARTY_ID WHERE MC.STATUS_ID IN ('MKTG_CAMP_CREATED','MKTG_CAMP_INPROGRESS','MKTG_CAMP_SCHEDULED') AND PR.ROLE_TYPE_ID='LEAD' AND CLP.PARTY_ID='"+partyId+"' ORDER BY  mc.last_updated_tx_stamp DESC";
	rs = sqlProcessor.executeQuery(mostRecentCampSql);
	if (rs != null) {
		while (rs.next()) {
			mostRecentCamp = rs.getString("most_recent_camp");
		}
	}
	
	
	String noOfOppoExpireSql = "SELECT COUNT(DISTINCT SO.SALES_OPPORTUNITY_ID) as 'oppo_expire' FROM SALES_OPPORTUNITY SO INNER JOIN `sales_opportunity_role` SOR ON SO.SALES_OPPORTUNITY_ID=SOR.SALES_OPPORTUNITY_ID AND SOR.ROLE_TYPE_ID='LEAD' AND SO.OPPORTUNITY_STATUS_ID='OPPO_OPEN' AND  DATEDIFF((SO.ESTIMATED_CLOSE_DATE),DATE(NOW()))<=7 AND DATEDIFF((SO.ESTIMATED_CLOSE_DATE),DATE(NOW()))>=0 AND sor.party_id='"+partyId+"'";
	rs = sqlProcessor.executeQuery(noOfOppoExpireSql);
	if (rs != null) {
		while (rs.next()) {
			noOfOppoExpire = rs.getString("oppo_expire");
		}
	}
	
	String noOfOpenOppoSql = "SELECT COUNT(DISTINCT S.SALES_OPPORTUNITY_ID) as 'open_opportunities' FROM `sales_opportunity` S INNER  JOIN SALES_OPPORTUNITY_ROLE SR ON S.SALES_OPPORTUNITY_ID=SR.SALES_OPPORTUNITY_ID AND S.OPPORTUNITY_STATUS_ID='OPPO_OPEN' AND SR.ROLE_TYPE_ID='LEAD' AND sr.party_id='"+partyId+"'";
	rs = sqlProcessor.executeQuery(noOfOpenOppoSql);
	if (rs != null) {
		while (rs.next()) {
			noOfOpenOppo = rs.getString("open_opportunities");
		}
	}
	
	String noOfOpenActivitySql = "SELECT COUNT(*) as 'open_activities' FROM work_effort w INNER JOIN work_Effort_party_assignment we ON w.work_effort_id=we.work_effort_id AND w.current_status_id='IA_OPEN' AND we.role_type_id='LEAD' AND we.party_id='"+partyId+"'";
	rs = sqlProcessor.executeQuery(noOfOpenActivitySql);
	if (rs != null) {
		while (rs.next()) {
			noOfOpenActivity = rs.getString("open_activities");
		}
	}
	
	String noOfActiveCampSql = "SELECT COUNT(DISTINCT MC.`MARKETING_CAMPAIGN_ID`) as 'active_campaign' FROM MARKETING_CAMPAIGN MC INNER JOIN `marketing_campaign_contact_list` CL ON MC.MARKETING_CAMPAIGN_ID=CL.MARKETING_CAMPAIGN_ID INNER JOIN CONTACT_LIST_PARTY CLP ON CLP.CONTACT_LIST_ID=CL.CONTACT_LIST_ID INNER JOIN PARTY_ROLE PR ON PR.PARTY_ID=CLP.PARTY_ID WHERE MC.STATUS_ID IN ('MKTG_CAMP_CREATED','MKTG_CAMP_INPROGRESS','MKTG_CAMP_SCHEDULED') AND PR.ROLE_TYPE_ID='LEAD' AND CLP.PARTY_ID='"+partyId+"'";
	rs = sqlProcessor.executeQuery(noOfActiveCampSql);
	if (rs != null) {
		while (rs.next()) {
			noOfActiveCamp = rs.getString("active_campaign");
		}
	}
	
	//String callBackDateSql = "SELECT DATE(call_back_date) as 'call_back_date' FROM sales_opportunity a INNER JOIN sales_opportunity_role b ON a.SALES_OPPORTUNITY_ID=b.SALES_OPPORTUNITY_ID WHERE ROLE_TYPE_ID='LEAD'  AND call_back_date IS NOT NULL AND b.party_id='"+partyId+"' ORDER BY call_back_date DESC LIMIT 1";
	String callBackDateSql = "SELECT DATE(call_back_date) as 'call_back_date' FROM `sales_opportunity` a INNER JOIN `sales_opportunity_role` b ON a.`SALES_OPPORTUNITY_ID`=b.`SALES_OPPORTUNITY_ID` WHERE `ROLE_TYPE_ID`='LEAD' AND call_back_date IS NOT NULL AND call_back_date>=DATE(NOW()) AND b.party_id='"+partyId+"' ORDER BY call_back_date ASC LIMIT 1";
	rs = sqlProcessor.executeQuery(callBackDateSql);
	if (rs != null) {
		while (rs.next()) {
			callBackDate = rs.getString("call_back_date");
		}
	}
	
	String lastClickedDateSql = "SELECT DATE(MC.`CLICK_DATE`) as 'last_clicked' FROM MARKETING_CAMPAIGN_CLICKED_DETAILS MC INNER JOIN PARTY_ROLE PR ON MC.PARTY_ID=PR.PARTY_ID AND PR.ROLE_TYPE_ID='LEAD' AND pr.party_id='"+partyId+"' ORDER BY MC.`CLICK_DATE` DESC LIMIT 1";
	rs = sqlProcessor.executeQuery(lastClickedDateSql);
	if (rs != null) {
		while (rs.next()) {
			lastClickedDate = rs.getString("last_clicked");
		}
	}
	
	String lastOpenDateSql = "SELECT DATE(MC.`open_time`) as 'last_open_date' FROM MARKETING_CAMPAIGN_OPEN_DETAILS MC INNER JOIN PARTY_ROLE PR ON MC.PARTY_ID=PR.party_id AND PR.ROLE_TYPE_ID='LEAD' AND pr.party_id='"+partyId+"' ORDER BY MC.`open_time` DESC LIMIT 1";
	rs = sqlProcessor.executeQuery(lastOpenDateSql);
	if (rs != null) {
		while (rs.next()) {
			lastOpenDate= rs.getString("last_open_date");
		}
	}
	sqlProcessor.close();
}

//To allow user to change status from qualified to other statuses for the same day if any opportunities/services are not exists for the lead
partyStatusconditions = EntityCondition.makeCondition([
EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "LEAD_QUALIFIED"),EntityCondition.makeCondition("statusDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp())),
EntityCondition.makeCondition("statusDate", EntityOperator.LESS_THAN, UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()))],
EntityOperator.AND);
partyStatusVal = delegator.findList("PartyStatus", partyStatusconditions, null, null, null, false);

//get service requests for lead
partySRconditions = EntityCondition.makeCondition([
EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId)],
EntityOperator.AND);
leadServiceRequest = delegator.findList("CustRequest", partySRconditions, null, null, null, false);

//get opportunities for lead
partyOppconditions = EntityCondition.makeCondition([
EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)],
EntityOperator.AND);
leadOppRequest = delegator.findList("SalesOpportunityRole", partyOppconditions, null, null, null, false);

isLeadStatusEnable="N";
if(UtilValidate.isNotEmpty(partyStatusVal)&& UtilValidate.isEmpty(leadServiceRequest) && UtilValidate.isEmpty(leadOppRequest)){
	isLeadStatusEnable="Y"
}
context.put("isLeadStatusEnable",isLeadStatusEnable);
context.put("leadStatusId",leadStatusId);

if (UtilValidate.isNotEmpty(leadSince)) {
	leadSince = UtilDateTime.timeStampToString(UtilDateTime.stringToTimeStamp(leadSince, "yyyy-MM-dd", TimeZone.getDefault(), null), globalDateFormat, TimeZone.getDefault(), null);
}
if (UtilValidate.isNotEmpty(callBackDate)) {
	callBackDate = UtilDateTime.timeStampToString(UtilDateTime.stringToTimeStamp(callBackDate, "yyyy-MM-dd", TimeZone.getDefault(), null), globalDateFormat, TimeZone.getDefault(), null);
}
if (UtilValidate.isNotEmpty(lastClickedDate)) {
	lastClickedDate = UtilDateTime.timeStampToString(UtilDateTime.stringToTimeStamp(lastClickedDate, "yyyy-MM-dd", TimeZone.getDefault(), null), globalDateFormat, TimeZone.getDefault(), null);
}
if (UtilValidate.isNotEmpty(lastOpenDate)) {
	lastOpenDate = UtilDateTime.timeStampToString(UtilDateTime.stringToTimeStamp(lastOpenDate, "yyyy-MM-dd", TimeZone.getDefault(), null), globalDateFormat, TimeZone.getDefault(), null);
}
	
kpiBarContext.put("lead-since", leadSince);
kpiBarContext.put("lead-status", leadStatus);
kpiBarContext.put("recent-campaign", mostRecentCamp);
kpiBarContext.put("opportunity-expire-count", noOfOppoExpire);
kpiBarContext.put("open-opportunity-count", noOfOpenOppo);
kpiBarContext.put("open-activity-count", noOfOpenActivity);
kpiBarContext.put("active-campaign-count", noOfActiveCamp);
kpiBarContext.put("call-back-date", callBackDate);
kpiBarContext.put("last-clicked-date", lastClickedDate);
kpiBarContext.put("last-open-date", lastOpenDate);
context.put("kpiBarContext", kpiBarContext);
context.put("isEnableDashboardButton",org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_VIEW_DASHBOARD_BTN_ENABLED", "Y"));
