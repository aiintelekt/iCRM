import org.fio.admin.portal.util.DataUtil

import org.fio.homeapps.util.DataUtil

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import net.sf.json.JSONObject;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.party.contact.ContactMechWorker;
import org.fio.admin.portal.util.DataHelper;
import org.fio.admin.portal.util.DataUtil;
import org.fio.crm.constants.CrmConstants;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.ModelUtil
import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;
import java.text.DecimalFormat;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import org.fio.homeapps.util.UtilActivity;
import org.groupfio.common.portal.util.UtilCampaign
import org.fio.homeapps.util.EnumUtil;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("contact-portalUiLabels", locale);

String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
String defaultTimeZoneDispalyNA = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DFLT_TM_ZN_DSPL_NA", "N");

SimpleDateFormat dbDate = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat usDate = new SimpleDateFormat("MM/dd/yy");

String partyId = request.getParameter("partyId");

yesNoMap = ["Y": "Yes", "N": "No"];
context.put("yesNoOptions", yesNoMap);

inputContext = new LinkedHashMap < String, Object > ();

partyId = parameters.get("partyId");

haveDataPermission = "Y";
String userLoginId = userLogin.getString("partyId");

if (locale == null) locale = UtilHttp.getLocale(request);

println("haveDataPermission>>> " + haveDataPermission);
context.put("haveDataPermission", haveDataPermission);

context.put("isEnableBasicBar", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_ENABLE_BASIC_BAR", "N"));
context.put("isPretailParamEnabledForRfm", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "RFM_METRICS_ENABLED"));
context.put("isEnableEconMetric", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_ENABLE_ECON_MATRIC", "Y"));

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

Map < String, Object > actionBarContext = new HashMap < String, Object > ();
postalAddress = null;
countValue = null;
if (partyId != null) {
    partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
    context.put("contactMeches", partyContactMechValueMaps);

    primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, partyId, UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true), true);
    if (UtilValidate.isNotEmpty(primaryContactInformation)) {
        actionBarContext.put("primaryEmail", primaryContactInformation.get("EmailAddress"));
        actionBarContext.put("emailSolicitation", primaryContactInformation.get("emailSolicitation"));
        //actionBarContext.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(primaryContactInformation.get("PrimaryPhone")));
        //actionBarContext.put("primaryPhone", org.groupfio.common.portal.util.DataHelper.preparePhoneNumber(delegator, primaryContactInformation.get("PrimaryPhone")));
        actionBarContext.put("phoneSolicitation", primaryContactInformation.get("phoneSolicitation"));

        inputContext.put("emailSolicitation", primaryContactInformation.get("emailSolicitation"));
        inputContext.put("phoneSolicitation", primaryContactInformation.get("phoneSolicitation"));
        inputContext.put("partyPrimaryPhone", primaryContactInformation.get("PrimaryPhone"));
        //inputContext.put("primaryPhoneNumber", primaryContactInformation.get("TelecomNumber"));
        inputContext.put("partyPrimaryEmail", primaryContactInformation.get("EmailAddress"));
        inputContext.put("primaryEmail", primaryContactInformation.get("EmailAddress"));

        context.put("phoneNumber", primaryContactInformation.get("primaryContactNumber"));
        context.put("phoneCountryCode", primaryContactInformation.get("primaryContactCountryCode"));
    }
	primaryPhoneInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMapsExt(delegator, partyId);
	actionBarContext.put("primaryPhone", primaryPhoneInformation.get("PrimaryPhone"));
	inputContext.put("primaryPhoneNumber", primaryPhoneInformation.get("TelecomNumber"));


    //get postal address
    postalAddress = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryPostal(delegator, partyId);
    if (UtilValidate.isNotEmpty(postalAddress)) {
    	if (UtilValidate.isNotEmpty(postalAddress.getString("postalCode"))) {
            context.put("postalCode", postalAddress.getString("postalCode"));
            inputContext.put("postalCode", postalAddress.getString("postalCode"));
        }

        inputContext.put("generalAttnName", postalAddress.getString("attnName"));
        inputContext.put("generalAddress1", postalAddress.getString("address1"));
        inputContext.put("generalAddress2", postalAddress.getString("address2"));
        inputContext.put("generalPostalCode", postalAddress.getString("postalCode"));
        inputContext.put("generalPostalCodeExt", postalAddress.getString("postalCodeExt"));
        inputContext.put("generalCity", postalAddress.getString("city"));

        String state = "";
        if (UtilValidate.isNotEmpty(postalAddress.getString("stateProvinceGeoId"))) {
            state = org.fio.admin.portal.util.DataUtil.getGeoName(delegator, postalAddress.getString("stateProvinceGeoId"), "STATE");
        }
        inputContext.put("stateGeoId", postalAddress.getString("stateProvinceGeoId"));
        inputContext.put("state", state);
        inputContext.put("generalCountryGeoId", postalAddress.getString("countryGeoId"));
        inputContext.put("editStateGeoId", postalAddress.getString("stateProvinceGeoId"));
        inputContext.put("editCountryGeoId", postalAddress.getString("countryGeoId"));
        inputContext.put("postalContactMechId", postalAddress.getString("contactMechId"));
    }

    inputContext.put("emailContactMechId", primaryContactInformation.get("emailContactMechId"));
    inputContext.put("contactNumberContactMechId", primaryContactInformation.get("telephoneContactMechId"));

    String partyIdentification = org.fio.homeapps.util.DataUtil.getPartyIdentificationValue(delegator, partyId, "COMPANY_NAME", true);
    if (UtilValidate.isNotEmpty(partyIdentification)) {
        inputContext.put("companyName", partyIdentification);
    }

    String isEnableSurvey = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_SURVEY_TAB", "N");
    if (UtilValidate.isNotEmpty(isEnableSurvey)) {
        context.put("domainEntityType", "CUSTOMER");
        context.put("domainEntityId", partyId);
        context.put("isEnableSurvey", isEnableSurvey);
    }

}

//get PATIENT_ID
String patientId = org.fio.homeapps.util.DataUtil.getPartyIdentificationValue(delegator, partyId, "PATIENT_ID", true);
inputContext.put("patientId", patientId);
//get CUST_ALT_ID
String custAltId = org.fio.homeapps.util.DataUtil.getPartyIdentificationValue(delegator, partyId, "CUST_ALT_ID", true);
inputContext.put("custAltId", custAltId);

//make sure that the partyId is actually an CONTACT before trying to display it as once
delegator = request.getAttribute("delegator");
// set this flag to allow contact mechs to be shown
request.setAttribute("displayContactMechs", "Y");
context.put("displayContactMechs", "Y");

// contact summary data
//partySummary = EntityQuery.use(delegator).from("PartySummaryDetailsView").where("partyId", partyId).cache(false).queryFirst();
partySummary = org.fio.homeapps.util.PartyHelper.getPartySummary(delegator, UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER", "primaryPostal", postalAddress));
//println("partySummary> "+partySummary);
if (partySummary != null) {
    context.put("partySummary", partySummary);
    context.put("currencyUomId", partySummary.get("preferredCurrencyUomId"));

    inputContext.putAll(partySummary);
	
	//code to get current time for timezone
	if (UtilValidate.isNotEmpty(partySummary.get("localTimeZone"))) {
		String enumId = EnumUtil.getEnumId(delegator, (String) partySummary.get("localTimeZone"), "TIME_ZONE");
		inputContext.put("timeZoneDesc",enumId);
		inputContext.put("timeZoneDesc_desc",EnumUtil.getEnumDescription(delegator, (String) partySummary.get("localTimeZone"), "TIME_ZONE"));
		currentTimeForTimezone = UtilActivity.getCurrentTimeFromTimeZone(delegator, partySummary.get("localTimeZone"));
		inputContext.put("currentTimeForTimezone", currentTimeForTimezone);
		context.put("currentTimeForTimezone", currentTimeForTimezone);
	}
	if (defaultTimeZoneDispalyNA == "Y" && UtilValidate.isEmpty(partySummary.get("localTimeZone")))
		inputContext.put("timeZoneDesc", "N/A");

	if (UtilValidate.isEmpty(partySummary.get("localTimeZone"))) {
		inputContext.put("timeZoneDesc_desc", "N/A");
	}

    //ended
    String gender = partySummary.get("gender");
    String maritalStatus = partySummary.get("maritalStatus");

    if (request.getRequestURI().contains("updateCustomer") || request.getRequestURI().contains("createCustomer")) {
        inputContext.put("gender", UtilValidate.isNotEmpty(gender) ? gender : "");
    }
    if (request.getRequestURI().contains("viewCustomer")) {
        inputContext.put("gender", UtilValidate.isNotEmpty(gender) ? org.fio.homeapps.util.EnumUtil.getEnumDescription(delegator, gender, "GENDER") : "N/A");
        if (UtilValidate.isEmpty(gender))
            inputContext.put("gender_desc", "N/A");
    }
    if (UtilValidate.isEmpty(maritalStatus))
        inputContext.put("maritalStatus_desc", "N/A");

    partySummary1 = EntityQuery.use(delegator).from("PartySummaryDetailsView").where("partyId", partyId).cache(false).queryFirst();
    if (partySummary1 != null) {
        inputContext.put("birthDate", partySummary1.getString("birthDate"));
        context.put("birthDate", partySummary1.getString("birthDate"));
    }
    String birthdayFormat = "";
    if (UtilValidate.isNotEmpty(partySummary.get("birthDate"))) {
        birthdayFormat = org.fio.customer.service.util.DataUtil.getFormatedDate("MMM dd, yyyy", partySummary.get("birthDate"));
        if (UtilValidate.isEmpty(birthdayFormat)) {
            birthdayFormat = "N/A";
        }
    } else {
        birthdayFormat = "N/A";
    }
    inputContext.put("birthdayFormat", birthdayFormat);
    inputContext.put("externalId", UtilValidate.isNotEmpty(partySummary.get("externalId")) ? partySummary.get("externalId") : "N/A");

    String contactName = partySummary.get("firstName");
    if (UtilValidate.isNotEmpty(partySummary.get("lastName")))
        contactName = contactName + " " + partySummary.get("lastName");

    actionBarContext.put("name", contactName);
    context.put("name", contactName);
    inputContext.put("partyName", contactName);

    context.put("partyStatusId", partySummary.get("statusId"));

    // fillup administration info
    inputContext.put("createdOn", UtilValidate.isNotEmpty(partySummary.get("createdDate")) ? UtilDateTime.timeStampToString((Timestamp) partySummary.get("createdDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
    inputContext.put("modifiedOn", UtilValidate.isNotEmpty(partySummary.get("lastModifiedDate")) ? UtilDateTime.timeStampToString((Timestamp) partySummary.get("lastModifiedDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
    inputContext.put("createdBy", partySummary.get("createdByUserLogin"));
    inputContext.put("modifiedBy", partySummary.get("lastModifiedByUserLogin"));
}

inputContext.put("primaryEmail", actionBarContext.get("primaryEmail"));

println("inputContext> " + inputContext);

//Provide current PartyClassificationGroups as a list and a string
groups = org.fio.homeapps.util.PartyHelper.getClassificationGroupsForParty(partyId, delegator);
context.put("partyClassificationGroups", groups);
descriptions = EntityUtil.getFieldListFromEntityList(groups, "customFieldName", false);
context.put("partyClassificationGroupsDisplay", StringUtil.join(descriptions, ", "));

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

//get Enum list with isenable null and "Y"
String enumTypeId = "CONTENT_CLASS";
enumList = EnumUtil.getEnableEnums(delegator, enumTypeId);
inputContext.put("enumValues", enumList);

dataSourceId = org.fio.homeapps.util.DataUtil.getPartyDataSourceId(delegator, partyId);
inputContext.put("dataSourceId", dataSourceId);
String dataSourceDesc = org.fio.homeapps.util.DataUtil.getPartyDataSource(delegator, partyId, dataSourceId);
inputContext.put("dataSourceDesc", dataSourceDesc);

relationManager = org.fio.homeapps.util.PartyHelper.getCurrentResponsibleParty(partyId, "CUSTOMER", delegator);
String rMId = UtilValidate.isNotEmpty(relationManager) ? relationManager.partyId : null;
context.put("selectedRMId", rMId);
context.put("partyRoleTypeId", "CUSTOMER");

if (UtilValidate.isNotEmpty(relationManager)) {
    String relationManagerName = org.fio.homeapps.util.PartyHelper.getPersonName(delegator, relationManager.getString("partyId"), false);
    inputContext.put("personResponsible", relationManagerName);
    context.put("responsibleName", relationManagerName);
}
if (UtilValidate.isEmpty(inputContext.get("personResponsible"))) {
    inputContext.put("personResponsible", "N/A");
}
context.put("domainEntityId", partyId);
context.put("domainEntityType", "CUSTOMER");
context.put("requestURI", "viewCustomer");
context.put("partyId", partyId);
String externalLoginKey = request.getParameter("externalLoginKey");
if (externalLoginKey == null) {
    externalLoginKey = request.getAttribute("externalLoginKey");
}

context.put("externalLoginKey", externalLoginKey);
//println("externalLoginKey-------"+externalLoginKey);

isMarketable = inputContext.get("emailSolicitation");
actionBarContext.put("isMarketable", isMarketable);

context.put("actionBarContext", actionBarContext);

ResultSet rs = null;
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Map < String, Object > kpiBarContext = new LinkedHashMap < String, Object > ();

String customerSince = "";
String customerStatus = "";
String mostRecentCamp = "";
String noOfOpenSrInSLA = "";
String noOfOpenSrSLA = "";
String noOfOpenActivity = "";
String noOfOpensr = "";
String callBackDate = "";
String lastClickedDate = "";
String lastOpenDate = "";
String noOfOpenOrders = "";
String lastEngagedDate = "";
String clv = "";
String ytd = "";
String lytd = "";
String firstShopDate = "";
String lastShopDate = "";
String noOfActiveCamp = "";

if (UtilValidate.isNotEmpty(partyId)) {

    String sinceDateSql = "SELECT DATE(p.created_date) AS 'customer_since', p.STATUS_ID as 'customer_status' FROM party p INNER JOIN party_role pr ON p.party_id=pr.party_id AND pr.role_type_id='CUSTOMER' AND p.party_id='" + partyId + "' LIMIT 1";
    rs = sqlProcessor.executeQuery(sinceDateSql);
    if (rs != null) {
        while (rs.next()) {
            customerSince = rs.getString("customer_since");

            customerStatusId = rs.getString("customer_status");
            if (UtilValidate.isNotEmpty(customerStatusId)) {
                String statusItemSql = "SELECT description FROM status_item WHERE status_id='" + customerStatusId + "'";
                rs = sqlProcessor.executeQuery(statusItemSql);
                if (rs != null) {
                    while (rs.next()) {
                        customerStatus = rs.getString("description");
                    }
                }
            }
        }
    }

    String mostRecentCampSql = "SELECT CAMPAIGN_NAME as 'most_recent_camp' FROM marketing_campaign mc INNER JOIN `marketing_campaign_contact_list` mcl ON mcl.MARKETING_CAMPAIGN_ID = mc.MARKETING_CAMPAIGN_ID INNER JOIN campaign_contact_list_party clp ON clp.CONTACT_LIST_ID = mcl.CONTACT_LIST_ID INNER JOIN party_role pr ON pr.PARTY_ID=clp.PARTY_ID WHERE  mcl.MARKETING_CAMPAIGN_ID = mc.MARKETING_CAMPAIGN_ID AND  pr.ROLE_TYPE_ID='CUSTOMER'	AND clp.PARTY_ID ='" + partyId + "'	ORDER BY mc.CREATED_DATE DESC LIMIT 1";
    rs = sqlProcessor.executeQuery(mostRecentCampSql);
    if (rs != null) {
        while (rs.next()) {
            mostRecentCamp = rs.getString("most_recent_camp");
        }
    }

    //count of active campaigns
    String noOfActiveCampSql = "SELECT COUNT(DISTINCT mc.`MARKETING_CAMPAIGN_ID`) as 'active_campaign' FROM marketing_campaign mc INNER JOIN marketing_campaign_contact_list mcl ON mcl.MARKETING_CAMPAIGN_ID = mc.MARKETING_CAMPAIGN_ID INNER JOIN campaign_contact_list_party clp ON clp.CONTACT_LIST_ID = mcl.CONTACT_LIST_ID INNER JOIN party_role pr ON pr.PARTY_ID=clp.PARTY_ID WHERE  mcl.MARKETING_CAMPAIGN_ID = mc.MARKETING_CAMPAIGN_ID AND  pr.ROLE_TYPE_ID='CUSTOMER' AND clp.PARTY_ID ='" + partyId + "' AND (mc.END_DATE IS NULL OR mc.END_DATE > NOW()) GROUP BY mc.MARKETING_CAMPAIGN_ID";
    rs = sqlProcessor.executeQuery(noOfActiveCampSql);
    if (rs != null) {
        while (rs.next()) {
            noOfActiveCamp = rs.getString("active_campaign");
        }
    }

    //openSR with in SLA
    String openSRSqlInSLA = "SELECT COUNT(*) as 'open_sr_count_in_sla' FROM cust_request cr 	INNER JOIN cust_request_sr_summary csr ON csr.CUST_REQUEST_ID = cr.CUST_REQUEST_ID  INNER JOIN  party_role crp ON  cr.FROM_PARTY_ID=crp.PARTY_ID  WHERE cr.status_id IN ('SR_OPEN','SR_ASSIGNED','SR_IN_PROGRESS','SR_PENDING') AND csr.DUE_DATE > NOW() AND crp.role_type_id='CUSTOMER' AND cr.FROM_PARTY_ID='" + partyId + "'";
    rs = sqlProcessor.executeQuery(openSRSqlInSLA);
    if (rs != null) {
        while (rs.next()) {
            noOfOpenSrInSLA = rs.getString("open_sr_count_in_sla");
        }
    }
    //open SR with out SLA
    String openSRSql = "SELECT COUNT(*) as 'open_sr_count_sla' FROM cust_request cr INNER JOIN cust_request_sr_summary csr ON csr.CUST_REQUEST_ID = cr.CUST_REQUEST_ID INNER JOIN  party_role crp ON  cr.FROM_PARTY_ID=crp.PARTY_ID WHERE cr.status_id IN ('SR_OPEN','SR_ASSIGNED','SR_IN_PROGRESS','SR_PENDING') AND csr.DUE_DATE < NOW() AND crp.role_type_id='CUSTOMER' AND cr.FROM_PARTY_ID='" + partyId + "'";
    rs = sqlProcessor.executeQuery(openSRSql);
    if (rs != null) {
        while (rs.next()) {
            noOfOpenSrSLA = rs.getString("open_sr_count_sla");
        }
    }

    String noOfOpenActivitySql = "SELECT COUNT(*) as 'open_activities' FROM work_effort we  INNER JOIN work_effort_party_assignment wpa ON wpa.WORK_EFFORT_ID=we.WORK_EFFORT_ID  INNER JOIN party p ON p.PARTY_ID = wpa.PARTY_ID  INNER JOIN party_role pr ON pr.PARTY_ID = p.PARTY_ID  WHERE we.CURRENT_STATUS_ID IN ('IA_OPEN')  AND pr.role_type_id='CUSTOMER' AND wpa.PARTY_ID='" + partyId + "'";
    rs = sqlProcessor.executeQuery(noOfOpenActivitySql);
    if (rs != null) {
        while (rs.next()) {
            noOfOpenActivity = rs.getString("open_activities");
        }
    }

    //open SR
    String noOfOpensrSql = "SELECT COUNT(*) as 'open_sr' FROM cust_request cr INNER JOIN  party_role pr ON  cr.FROM_PARTY_ID=pr.PARTY_ID 	WHERE cr.status_id IN ('SR_OPEN','SR_ASSIGNED','SR_IN_PROGRESS','SR_PENDING') AND pr.role_type_id='CUSTOMER' AND cr.FROM_PARTY_ID='" + partyId + "'";
    rs = sqlProcessor.executeQuery(noOfOpensrSql);
    if (rs != null) {
        while (rs.next()) {
            noOfOpensr = rs.getString("open_sr");
        }
    }

    //open orders
    String noOfOpenOrdersSql = "SELECT COUNT(DISTINCT order_id) as 'open_orders' FROM  rms_transaction_master rtm INNER JOIN party p ON p.PARTY_ID = rtm.BILL_TO_PARTY_ID	INNER JOIN party_role pr ON pr.PARTY_ID = p.PARTY_ID WHERE rtm.HEADER_STATUS IN ('ORDER_CREATED','ORDER_APPROVED')AND pr.ROLE_TYPE_ID ='CUSTOMER' AND rtm.BILL_TO_PARTY_ID ='" + partyId + "'";
    rs = sqlProcessor.executeQuery(noOfOpenOrdersSql);
    if (rs != null) {
        while (rs.next()) {
            noOfOpenOrders = rs.getString("open_orders");
        }
    }

    //Last Clicked Date
    String lastClickedDateSql = "SELECT DATE(crc.CLICKED_TIMESTAMP) as 'last_clicked' FROM campaign_report_clicked crc INNER JOIN party p ON p.PARTY_ID = crc.PARTY_ID INNER JOIN party_role pr ON pr.PARTY_ID = p.PARTY_ID WHERE pr.ROLE_TYPE_ID ='CUSTOMER' AND crc.PARTY_ID = '" + partyId + "' ORDER BY crc.CLICKED_TIMESTAMP DESC LIMIT 1";
    rs = sqlProcessor.executeQuery(lastClickedDateSql);
    if (rs != null) {
        while (rs.next()) {
            lastClickedDate = rs.getString("last_clicked");
        }
    }

    //last opened date
    String lastOpenDateSql = "SELECT DATE(cro.OPENED_TIMESTAMP) as  'last_open_date' FROM campaign_report_opened cro INNER JOIN party p ON p.PARTY_ID = cro.PARTY_ID INNER JOIN party_role pr ON pr.PARTY_ID = p.PARTY_ID WHERE pr.ROLE_TYPE_ID ='CUSTOMER' AND cro.PARTY_ID = '" + partyId + "' ORDER BY cro.OPENED_TIMESTAMP DESC LIMIT 1";
    rs = sqlProcessor.executeQuery(lastOpenDateSql);
    if (rs != null) {
        while (rs.next()) {
            lastOpenDate = rs.getString("last_open_date");
        }
    }

    //Last Engaged Date
    String lastEngagedDateSql = "SELECT wt.DESCRIPTION,DATE(w.LAST_UPDATED_TX_STAMP) as 'last_engaged_date' FROM work_effort w LEFT JOIN work_Effort_party_assignment we	ON w.work_effort_id=we.work_effort_id	LEFT JOIN work_effort_contact wc ON w.work_effort_id=wc.work_effort_id	INNER JOIN  work_effort_type wt ON wt.WORK_EFFORT_TYPE_ID=w.WORK_EFFORT_TYPE_ID	WHERE we.role_type_id='CUSTOMER' AND we.party_id='" + partyId + "' OR wc.role_type_id='CUSTOMER' AND wc.party_id='" + partyId + "' AND	w.current_status_id IN('IA_OPEN','IA_CLOSED') ORDER BY w.LAST_UPDATED_TX_STAMP DESC LIMIT 1";
    rs = sqlProcessor.executeQuery(lastEngagedDateSql);
    if (rs != null) {
        while (rs.next()) {
            lastEngagedDate = rs.getString("last_engaged_date");
        }
    }

    String entityName = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUSTOMER_RTM_ITM");
    if (UtilValidate.isEmpty(entityName))
        entityName = "InvoiceTransactionMaster";
    context.put("entityName", entityName);

    String clvYtdLytdTableName = ModelUtil.javaNameToDbName(entityName);
    if (UtilValidate.isEmpty(clvYtdLytdTableName))
        clvYtdLytdTableName = "INVOICE_TRANSACTION_MASTER";
    String clvSql = null;
    String ytdSql = null;
    String lytdSql = null;
    String firstShoppedDateSql = null;
    String lastShoppedDateSql = null;
    String id = null;
    String date = null;
    if ("RMS_TRANSACTION_MASTER".equals(clvYtdLytdTableName)) {
        id = "order_id";
        date = "order_date";
    } else if ("INVOICE_TRANSACTION_MASTER".equals(clvYtdLytdTableName)) {
        date = "invoice_date";
        id = "invoice_id";
    }
    String clvDataLoad = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CLV_DATA_LOAD", "");
    if ("MASTER_TABLE".equals(clvDataLoad)) {
        clvSql = "SELECT IFNULL(SUM(total_sales_amount),0) as 'clv' FROM (SELECT total_sales_amount FROM " + clvYtdLytdTableName + " WHERE bill_to_party_id='" + partyId + "' GROUP BY " + id + ") clv LIMIT 1";
        ytdSql = "SELECT IFNULL(SUM(total_sales_amount),0) as 'ytd'  FROM (SELECT total_sales_amount FROM " + clvYtdLytdTableName + " WHERE bill_to_party_id='" + partyId + "' AND YEAR(" + date + ") =YEAR(now()) GROUP BY " + id + " ) ytd LIMIT 1";
        lytdSql = "SELECT IFNULL(SUM(total_sales_amount),0) as 'lytd' FROM (SELECT total_sales_amount FROM " + clvYtdLytdTableName + " WHERE bill_to_party_id='" + partyId + "' AND YEAR(" + date + ")=(YEAR(NOW())-1) GROUP BY " + id + ") lytd LIMIT 1";
    } else {
        clvSql = "SELECT `PROPERTY_VALUE` as 'clv' FROM `party_metric_indicator` WHERE `CUSTOM_FIELD_ID`='TOTAL_PURCHASED' AND `PARTY_ID`='" + partyId + "'";
        ytdSql = "SELECT `DATA_VALUE` as 'ytd' FROM `party_transaction_data` WHERE `DATA_TYPE_ID`='TOTAL_PURCHASED_AMOUNT' AND `YEAR`=YEAR(NOW()) AND `MONTH`='ALL' AND `PARTY_ID`='" + partyId + "'";
        lytdSql = "SELECT `DATA_VALUE` as 'lytd' FROM `party_transaction_data` WHERE `DATA_TYPE_ID`='TOTAL_PURCHASED_AMOUNT' AND `YEAR`= (YEAR(NOW()) -1) AND `MONTH`='ALL' AND `PARTY_ID`='" + partyId + "'";
    }
    firstShoppedDateSql = "SELECT DATE(" + date + ") AS 'first_shopped_date' FROM " + clvYtdLytdTableName + " WHERE  bill_to_party_id ='" + partyId + "' GROUP BY " + date + " ORDER BY " + date + " ASC LIMIT 1";
    lastShoppedDateSql = "SELECT DATE(" + date + ") AS 'last_shopped_date' FROM " + clvYtdLytdTableName + " WHERE  bill_to_party_id ='" + partyId + "' GROUP BY " + date + " ORDER BY " + date + " DESC LIMIT 1";
    try {
        rs = sqlProcessor.executeQuery(clvSql);
        if (rs != null) {
            while (rs.next()) {
                clv = rs.getString("clv");
            }
        }

        rs = sqlProcessor.executeQuery(ytdSql);
        if (rs != null) {
            while (rs.next()) {
                ytd = rs.getString("ytd");
            }
        }

        rs = sqlProcessor.executeQuery(lytdSql);
        if (rs != null) {
            while (rs.next()) {
                lytd = rs.getString("lytd");
            }
        }

        rs = sqlProcessor.executeQuery(firstShoppedDateSql);
        if (rs != null) {
            while (rs.next()) {
                firstShopDate = rs.getString("first_shopped_date");
                if (UtilValidate.isNotEmpty(firstShopDate)) {
                    Date firstShopDateDate = dbDate.parse(firstShopDate);
                    firstShopDate = usDate.format(firstShopDateDate);
                }
            }
        }

        rs = sqlProcessor.executeQuery(lastShoppedDateSql);
        if (rs != null) {
            while (rs.next()) {
                lastShopDate = rs.getString("last_shopped_date");
                if (UtilValidate.isNotEmpty(lastShopDate)) {
                    Date lastShopDateDate = dbDate.parse(lastShopDate);
                    lastShopDate = usDate.format(lastShopDateDate);
                }
            }
        }
    } catch (Exception e) {
        Debug.log("Error Message" + e);
    }
}

BigDecimal clvDecimal = UtilValidate.isNotEmpty(clv) ? BigDecimal.valueOf(Double.valueOf(clv)).setScale(0, BigDecimal.ROUND_HALF_EVEN) : BigDecimal.ZERO;
clv = clvDecimal.toString();

BigDecimal ytdDecimal = UtilValidate.isNotEmpty(ytd) ? BigDecimal.valueOf(Double.valueOf(ytd)).setScale(0, BigDecimal.ROUND_HALF_EVEN) : BigDecimal.ZERO;
ytd = ytdDecimal.toString();

BigDecimal lytdDecimal = UtilValidate.isNotEmpty(lytd) ? BigDecimal.valueOf(Double.valueOf(lytd)).setScale(0, BigDecimal.ROUND_HALF_EVEN) : BigDecimal.ZERO;
lytd = lytdDecimal.toString();

kpiBarContext.put("customer-since", customerSince);
kpiBarContext.put("customer-status", customerStatus);
kpiBarContext.put("recent-campaign", mostRecentCamp);
kpiBarContext.put("open-sr-in-sla", noOfOpenSrInSLA);
kpiBarContext.put("open-sr-in-sla", noOfOpenSrInSLA);
kpiBarContext.put("open-activity-count", noOfOpenActivity);
kpiBarContext.put("active-campaign-count", noOfActiveCamp);
kpiBarContext.put("open-sr-count", noOfOpensr);
kpiBarContext.put("open-order-count", noOfOpenOrders);
kpiBarContext.put("call-back-date", callBackDate);
kpiBarContext.put("last-clicked-date", lastClickedDate);
kpiBarContext.put("last-open-date", lastOpenDate);
kpiBarContext.put("last-engaged-date", lastEngagedDate);
kpiBarContext.put("clv", org.groupfio.common.portal.util.DataUtil.numberFormatter(locale, clv));
kpiBarContext.put("ytd", org.groupfio.common.portal.util.DataUtil.numberFormatter(locale, ytd));
kpiBarContext.put("lytd", org.groupfio.common.portal.util.DataUtil.numberFormatter(locale, lytd));
kpiBarContext.put("first_shopped_date", firstShopDate);
kpiBarContext.put("last_shopped_date", lastShopDate);

String isEnableCustReports = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUSTOMER_PERSONAL_REPORT", "N");
context.put("isEnableCustReports", isEnableCustReports);
String isEnableCustomerSummary = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUST_SUMMARY_VIEW");
context.put("isCustomerSummaryView", isEnableCustomerSummary);

kpiBarContext.put("earned-value", UtilValidate.isNotEmpty(partySummary.get("balancePoints")) ? new BigDecimal(partySummary.get("balancePoints").toString()) : "0");
/*
GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId": partyId), false);
if(person != null && person.size() > 0) {
	kpiBarContext.put("earned-value",UtilValidate.isNotEmpty(person.getBigDecimal("balancePoints"))? person.getBigDecimal("balancePoints"): "0");
}
*/

println("kpiBarContext--->" + kpiBarContext);

inputContext.put("statusIdDesc", customerStatus);

if (UtilValidate.isNotEmpty(inputContext.supplementalPartyTypeId) && inputContext.supplementalPartyTypeId.equals("CONTRACTOR")) {
    inputContext.put("isContractor", "Y");
} else {
    inputContext.put("isContractor", "N");
}

context.put("inputContext", inputContext);

String isEnableRebateModule = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_REBATE_MODULE");
if (UtilValidate.isNotEmpty(isEnableRebateModule)) {
    context.put("isEnableRebateModule", isEnableRebateModule);
}

List < String > roles = new ArrayList < > ();
String globalConfig = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUST_OWNER");
if (UtilValidate.isNotEmpty(globalConfig) && globalConfig.contains(",")) {
    roles = org.fio.admin.portal.util.DataUtil.stringToList(globalConfig, ",");
} else if (UtilValidate.isNotEmpty(globalConfig)) {
    roles.add(globalConfig);
}
List < GenericValue > reassignOwners = EntityQuery.use(delegator).select("roleTypeId", "description").from("RoleType").where(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roles)).queryList();
context.put("reassignOwnerList", UtilValidate.isNotEmpty(reassignOwners) ? org.fio.admin.portal.util.DataUtil.getMapFromGeneric(reassignOwners, "roleTypeId", "description", false) : new HashMap < String, Object > ());

GenericValue custUserLogin = EntityQuery.use(delegator).from("UserLogin").where("partyId", partyId).queryFirst();
if (UtilValidate.isNotEmpty(custUserLogin)) {
    String isEnabled = custUserLogin.getString("enabled");
    context.put("isUserEnabled", UtilValidate.isNotEmpty(isEnabled) ? isEnabled : "Y");
    context.put("isUserLoginExists", "Y");
} else {
    context.put("isUserLoginExists", "N");
}

context.put("isEnableProgramAct", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_PROG_ACT", "N"));
context.put("isEnableInviteUser", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_INVITE_USER", "N"));
String isEnableInvoiceModule = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_INVOICE_MODULE");
if (UtilValidate.isNotEmpty(isEnableInvoiceModule)) {
    context.put("isEnableInvoiceModule", isEnableInvoiceModule);
}
String isEnabledOrderModule = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_ORDER_MODULE", "Y");
if (UtilValidate.isNotEmpty(isEnabledOrderModule)) {
    context.put("isEnabledOrderModule", isEnabledOrderModule);
}
String isEnableCouponModule = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_COUPON_MODULE", "Y");
if (UtilValidate.isNotEmpty(isEnableCouponModule)) {
    context.put("isEnableCouponModule", isEnableCouponModule);
}
String isEnabledReceiptModule = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_RECEIPT_MODULE", "Y");
if (UtilValidate.isNotEmpty(isEnabledReceiptModule)) {
    context.put("isEnabledReceiptModule", isEnabledReceiptModule);
}
String isEnabledEarnedValueModule = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_EARNED_VALUE_MODULE", "Y");
if (UtilValidate.isNotEmpty(isEnabledEarnedValueModule)) {
    context.put("isEnabledEarnedValueModule", isEnabledEarnedValueModule);
}

String loyaltyPoints = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LOYALTY_POINTS");
if (UtilValidate.isNotEmpty(loyaltyPoints)) {
    context.put("loyaltyPoints", loyaltyPoints);
}

String partyIdentification = org.fio.homeapps.util.DataUtil.getPartyIdentificationValue(delegator, partyId, "LOYALTY_ID");
if (UtilValidate.isNotEmpty(partyIdentification)) {
    inputContext.put("loyaltyId", partyIdentification);
}
if (UtilValidate.isEmpty(inputContext.get("loyaltyId"))) {
    inputContext.put("loyaltyId", "N/A");
}
GenericValue productStore = null;
if (person != null) {
    String assignedStore = partySummary.get("assignedStore");
    if (UtilValidate.isNotEmpty(assignedStore)) {
        context.put("assignedStoreId", assignedStore);
        productStore = EntityQuery.use(delegator).select("storeName").from("ProductStore").where("productStoreId", assignedStore).queryFirst();
        if (UtilValidate.isNotEmpty(productStore)) {
            String assignedStoreName = productStore.getString("storeName");
            if (UtilValidate.isNotEmpty(assignedStoreName)) {
                assignedStore = assignedStore + "(" + assignedStoreName + ")";
            }
        }
        context.put("assignedStore", assignedStore);
    } else {
        assignedStore = "N/A";
    }
    inputContext.put("signUpStore", assignedStore);

    String localStorePreference = partySummary.get("localStorePreference");
    if (UtilValidate.isNotEmpty(localStorePreference)) {
        context.put("localStorePreferenceId", localStorePreference);
        productStore = EntityQuery.use(delegator).select("storeName").from("ProductStore").where("productStoreId", localStorePreference).queryFirst();
        if (UtilValidate.isNotEmpty(productStore)) {
            String localStorePreferenceName = productStore.getString("storeName");
            if (UtilValidate.isNotEmpty(localStorePreferenceName)) {
                localStorePreference = localStorePreference + "(" + localStorePreferenceName + ")";
            }
        }
        context.put("localStorePreference", localStorePreference);
    } else {
        localStorePreference = "N/A";
    }
    inputContext.put("storePreference", localStorePreference);

    String loyaltyStoreId = partySummary.get("loyaltyStoreId");
    if (UtilValidate.isNotEmpty(loyaltyStoreId)) {
        context.put("loyaltyStoreId", loyaltyStoreId);
        productStore = EntityQuery.use(delegator).select("storeName").from("ProductStore").where("productStoreId", loyaltyStoreId).queryFirst();
        if (UtilValidate.isNotEmpty(productStore)) {
            String loyaltyStoreIdName = productStore.getString("storeName");
            if (UtilValidate.isNotEmpty(loyaltyStoreIdName)) {
                loyaltyStoreId = loyaltyStoreId + "(" + loyaltyStoreIdName + ")";
            }
        }
        context.put("loyaltyStoreName", loyaltyStoreId);
    } else {
        loyaltyStoreId = "N/A";
    }
    inputContext.put("loyaltyStore", loyaltyStoreId);
}

if (UtilValidate.isNotEmpty(person) && UtilValidate.isNotEmpty(partySummary.get("isLoyaltyEnabled")) && "Y".equals(partySummary.get("isLoyaltyEnabled")))
    inputContext.put("isLoyaltyEnabled", partySummary.get("isLoyaltyEnabled"));
else
    inputContext.put("isLoyaltyEnabled", "N");

//to display special occasion
println("display special occasion start> " + UtilDateTime.nowTimestamp());
String specialOccasion = "";
String segmentGroupValue = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SPECIAL_OCCASION_SEGMENTS");
if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(segmentGroupValue)) {
    List < String > groupList = new ArrayList < > ();
    if (segmentGroupValue.contains(",")) {
        groupList = org.fio.admin.portal.util.DataUtil.stringToList(segmentGroupValue, ",");
    } else if (UtilValidate.isNotEmpty(segmentGroupValue)) {
        groupList.add(segmentGroupValue);
    }
    EntityCondition segmentMainCondition = EntityCondition.makeCondition(EntityOperator.AND, EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition("groupId", EntityOperator.IN, groupList)), EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
    List < GenericValue > getCustomFieldList = EntityQuery.use(delegator).select("customFieldId").from("CustomFieldPartyClassification").where(segmentMainCondition).queryList();
    if (UtilValidate.isNotEmpty(getCustomFieldList)) {
        List < String > groupIdLists = EntityUtil.getFieldListFromEntityList(getCustomFieldList, "customFieldId", false);
        List < GenericValue > getCustomFieldNameList = EntityQuery.use(delegator).select("customFieldName").from("CustomField").where(EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition("customFieldId", EntityOperator.IN, groupIdLists))).queryList();
        List < String > groupNameLists = new ArrayList < > ();
        if (UtilValidate.isNotEmpty(getCustomFieldNameList)) {
            groupNameLists = EntityUtil.getFieldListFromEntityList(getCustomFieldNameList, "customFieldName", false);
        }
        if (UtilValidate.isNotEmpty(getCustomFieldNameList)) {
            inputContext.put("specialOccasion", StringUtil.join(groupNameLists, ", "))
        }
    }
}
if (UtilValidate.isEmpty(inputContext.get("specialOccasion"))) {
    specialOccasion = "N/A";
    inputContext.put("specialOccasion", specialOccasion);
}
println("display special occasion end> " + UtilDateTime.nowTimestamp());
String thruDateCount = "";
String isLoyaltyEnable = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_LOYALTY_ENABLE", "N");
context.put("isLoyaltyEnable", isLoyaltyEnable);
if (UtilValidate.isNotEmpty(isLoyaltyEnable) && isLoyaltyEnable.equals("Y")) {
    coupons = EntityQuery.use(delegator).select("productPromoCodeId").from("ProductPromoCodeParty").where("partyId", partyId).queryList();
    productPromoCodeId = EntityUtil.getFieldListFromEntityList(coupons, "productPromoCodeId", true);
    List < EntityExpr > availableStatusCondition = UtilMisc.toList(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp()));
    availableStatusCondition.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
    EntityCondition scannedDateCondition = EntityCondition.makeCondition("scannedDate", EntityOperator.EQUALS, null);
    EntityCondition availableStatusMainCondition = EntityCondition.makeCondition(availableStatusCondition, EntityOperator.OR);
    EntityCondition couponCountCondition = EntityCondition.makeCondition("productPromoCodeId", EntityOperator.IN, productPromoCodeId);
    EntityCondition couponCountMainCondition = EntityCondition.makeCondition(UtilMisc.toList(couponCountCondition, availableStatusMainCondition, scannedDateCondition), EntityOperator.AND);
    thruDateCount = delegator.findCountByCondition("ProductPromoCode", couponCountMainCondition, null, null);
}
if (UtilValidate.isNotEmpty(partyId)) {
    String isEreceiptEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_ERCPT_ENABLED", "N");
    if (UtilValidate.isNotEmpty(isEreceiptEnabled) && isEreceiptEnabled.equals("Y")) {
        String isPartyAttrEreceiptEnabled = org.groupfio.common.portal.util.UtilAttribute.getPartyAttribute(delegator, partyId, "IS_ERCPT_ENABLED");
        isPartyAttrEreceiptEnabled = UtilValidate.isNotEmpty(isPartyAttrEreceiptEnabled) ? isPartyAttrEreceiptEnabled : "N";
        context.put("isEreceiptEnabled", isPartyAttrEreceiptEnabled);
        inputContext.put("isEreceiptEnabled", isPartyAttrEreceiptEnabled);
    }
}
if (UtilValidate.isEmpty(inputContext.get("isEreceiptEnabled"))) {
    inputContext.put("isEreceiptEnabled", "N/A");
}
String partyNoteId = "";
GenericValue partyNote = EntityUtil.getFirst(delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", partyId), UtilMisc.toList("noteId DESC"), false));
if (UtilValidate.isNotEmpty(partyNote)) {
    partyNoteId = partyNote.getString("noteId");
}
context.put("partyNoteId", partyNoteId);

callStatusCond = EntityCondition.makeCondition(EntityOperator.AND,
    EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CALL_STATUS"),
    EntityCondition.makeCondition(EntityOperator.OR,
        EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"),
        EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, null)));
enumerationList = EntityQuery.use(delegator).from("Enumeration").where(callStatusCond).queryList();
context.put("enumerationList", DataHelper.getDropDownOptions(enumerationList, "enumId", "description"));
String isPhoneCampaignEnabled = org.fio.homeapps.util.DataUtil.isPhoneCampaignEnabled(delegator);
context.put("isPhoneCampaignEnabled", org.fio.homeapps.util.DataUtil.isPhoneCampaignEnabled(delegator));

campaignListId = request.getParameter("campaignListId");
marketingCampaignId = request.getParameter("marketingCampaignId");
contactListId = request.getParameter("contactListId");

String defaultCampaignListId = "";
String marketingCampaignIdName = "";
String defalutMktCampaignId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DIRECT_CALL");
context.put("defalutMktCampaignId", defalutMktCampaignId);

if (UtilValidate.isEmpty(marketingCampaignId) && UtilValidate.isNotEmpty((String) session.getAttribute("setCampaignPartyId_" + partyId))) {
    marketingCampaignId = (String) session.getAttribute("setMarketingCampaignId_" + partyId);
    contactListId = (String) session.getAttribute("setContactListId_" + partyId);
}
GenericValue campaign = EntityQuery.use(delegator).select("campaignName").from("MarketingCampaign").where("marketingCampaignId", marketingCampaignId).queryOne();
if (UtilValidate.isNotEmpty(campaign)) {
    marketingCampaignIdName = org.groupfio.common.portal.util.DataUtil.combineValueKey(campaign.getString("campaignName"), marketingCampaignId);
}
inputContext.put("marketingCampaignIdName", marketingCampaignIdName);
context.put("marketingCampaignId", marketingCampaignId);
context.put("contactListId", contactListId);

EntityCondition mktCondition = EntityCondition.makeCondition(EntityOperator.AND,
	EntityCondition.makeCondition("marketingCampaignId", EntityOperator.EQUALS, marketingCampaignId),
	EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "MKTG_CAMP_PUBLISHED"),
	EntityCondition.makeCondition("campaignTypeId", EntityOperator.EQUALS, "PHONE_CALL"),
	EntityCondition.makeCondition(EntityCondition.makeConditionDate("startDate", "endDate")));
marketingCampaignList = EntityQuery.use(delegator).from("MarketingCampaign").where(mktCondition).queryFirst();
if (UtilValidate.isNotEmpty(marketingCampaignList)) {
	context.put("marketingCampaignList", marketingCampaignList);
	callRecordMaster = UtilCampaign.getLatestCallRecordMaster(delegator, marketingCampaignId, contactListId, partyId, true);
	if (UtilValidate.isNotEmpty(callRecordMaster)) {
		context.put("callRecordId", callRecordMaster.getString("callRecordId"));
	}
	callBackDateLastCallStatus = UtilCampaign.getCallBackDateLastCallStatus(delegator, marketingCampaignId, contactListId, partyId, true);
	if (UtilValidate.isNotEmpty(callBackDateLastCallStatus)) {
		callBackDate = (String)callBackDateLastCallStatus.get("callBackDate");
		if(UtilValidate.isNotEmpty(callBackDate)) {
			Date callBackDate1 = new SimpleDateFormat("yyyy-MM-dd").parse(callBackDate);
			SimpleDateFormat formatter1 = new SimpleDateFormat("MM-dd-yyyy");
			callBackDate = formatter1.format(callBackDate1);
			context.put("callBackDate", callBackDate);
		}
		context.put("callStatus", (String) callBackDateLastCallStatus.get("callStatusId"));
	}
}
def formatValues(value) {
    DecimalFormat df = new DecimalFormat("#,###.##");
    double number = Double.parseDouble(value);
    String formatted = df.format(number);
    return formatted
}

userId = userLogin.getString("userLoginId");
partyGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup").where("userLoginId", userId, "groupId", "VIEW_CUST_BTN_HIDE").queryOne();
permissionToEnable = "Y";
if (UtilValidate.isNotEmpty(partyGroup))
    permissionToEnable = "N";
context.put("isEnableSinglePageEdit", permissionToEnable);
context.put("isEnableReassign", permissionToEnable);
context.put("isEnableDeactivate", permissionToEnable);

nowTimestamp = org.ofbiz.base.util.UtilDateTime.nowTimestamp();
context.put("nowTimestamp", nowTimestamp);

context.put("kpiBarContext", kpiBarContext);

userLoginIdSession = request.getAttribute("userLogin");
if (userLoginIdSession != null) {
    userLoginIdSession = userLoginIdSession.getString("userLoginId");
}
context.put("campaignListId", campaignListId);

if (UtilValidate.isNotEmpty(isPhoneCampaignEnabled) && isPhoneCampaignEnabled.equals("Y")) {
	condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userId),
		EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "FULLADMIN"),
		EntityUtil.getFilterByDateExpr()), EntityOperator.AND);
	userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup").where(condition).queryList();
	if (UtilValidate.isNotEmpty(userLoginSecurityGroup)) {
		context.put("isEnableReassign", "Y");
	}else {
		context.put("isEnableReassign", "N");
	}
	inputContext.put("couponCount", UtilValidate.isNotEmpty(thruDateCount) ? thruDateCount : "N/A");
}
context.put("activeMessType", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACTIVE_MESS_TYPE"));
context.put("activeRcMessAPI", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "RC_MSG_API", "MVP"));
context.put("isEnableDashboardButton",org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_VIEW_DASHBOARD_BTN_ENABLED", "Y"));
