import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.fio.admin.portal.util.DataUtil
import org.fio.crm.party.PartyHelper;
import org.groupfio.account.portal.util.DataHelper;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.Debug;
import javolution.util.FastList;
import org.fio.homeapps.util.UtilActivity;
import org.fio.homeapps.util.EnumUtil;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("account-portalUiLabels", locale);

String partyId = request.getParameter("partyId");
String isView = context.get("isView");

inputContext = new LinkedHashMap < String, Object > ();

inputContext.put("partyId", partyId);
inputContext.put("accountType", "Account");

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

println("activeTab>>> " + activeTab);

yesNoMap = ["Y": "Yes", "N": "No"];
context.put("yesNoOptions", yesNoMap);

partyId = request.getParameter("partyId");

haveDataPermission = "Y";
String userLoginId = userLogin.getString("partyId");

String contactNumber = "";
String email = "";

context.put("isEnableBasicBar", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_ENABLE_BASIC_BAR", "N"));
context.put("isEnableActBasicBar", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_ENABLE_ACTIVITY_BASIC_BAR", "N"));

Map < String, Object > actionBarContext = new HashMap < String, Object > ();
//Map<String, Object> kpiBarContext = new HashMap<String, Object>();
if (partyId != null) {
    primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, partyId, UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true, "isRetriveWebAddress", true), true);

    if (UtilValidate.isNotEmpty(primaryContactInformation)) {
        //actionBarContext.put("primaryEmail",primaryContactInformation.get("EmailAddress"));
        //actionBarContext.put("emailSolicitation",primaryContactInformation.get("emailSolicitation"));

        //actionBarContext.put("primaryPhone",org.fio.admin.portal.util.DataUtil.formatPhoneNumber(primaryContactInformation.get("PrimaryPhone")));

        //actionBarContext.put("primaryPhone",org.groupfio.common.portal.util.DataHelper.preparePhoneNumber(delegator, primaryContactInformation.get("PrimaryPhone")));
        //actionBarContext.put("phoneSolicitation",primaryContactInformation.get("phoneSolicitation"));

        inputContext.put("partyPrimaryPhone", primaryContactInformation.get("PrimaryPhone"));
        inputContext.put("partyPrimaryEmail", primaryContactInformation.get("EmailAddress"));

        contactNumber = org.fio.admin.portal.util.DataUtil.formatPhoneNumber(primaryContactInformation.get("PrimaryPhone"));
        email = primaryContactInformation.get("EmailAddress");
    }

    //ww cust id
    String partyExternalId = "";
    GenericValue partyIdentify = EntityQuery.use(delegator).from("PartyIdentification").where("partyId", partyId, "partyIdentificationTypeId", "WW_CUST").queryFirst();
    if (UtilValidate.isNotEmpty(partyIdentify)) {
        partyExternalId = partyIdentify.getString("idValue");
    }
    inputContext.put("externalId", partyExternalId);
    context.partyExternalId = partyExternalId;

    println("haveDataPermission>>> " + haveDataPermission);
    context.put("haveDataPermission", haveDataPermission);

    //data source list
    dataSourceconditions = EntityCondition.makeCondition([
            EntityCondition.makeCondition("disable", EntityOperator.NOT_EQUAL, "Y"),
            EntityCondition.makeCondition("dataSourceTypeId", EntityOperator.EQUALS, "LEAD_GENERATION")
        ],
        EntityOperator.AND);
    dataSourceList = delegator.findList("DataSource", dataSourceconditions, null, UtilMisc.toList("description ASC"), null, false);
    context.put("dataSourceList", dataSourceList);

    // make sure that the partyId is actually an ACCOUNT before trying to display it as once
    delegator = request.getAttribute("delegator");
    validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("ACCOUNT"), delegator);

    // if not, return right away (otherwise we get spaghetti code)
    if ((validRoleTypeId == null) || (!validRoleTypeId.equals("ACCOUNT"))) {
        context.put("validView", false);
        return;
    }

    // is the account still active?
    accountActive = PartyHelper.isActive(partyId, delegator);
    dispatcher = request.getAttribute("dispatcher");

    // account summary data
    partySummary = from("PartySummaryDetailsView").where("partyId", partyId, "partyTypeId", "PARTY_GROUP").queryOne();

    println("partySummary------------>" + partySummary);

    if (partySummary != null && partySummary.size() > 0) {
        context.put("partySummary", partySummary);

        inputContext.putAll(partySummary.getAllFields());

        if (UtilValidate.isNotEmpty(isView) && "Y".equals(isView)) {
            if (UtilValidate.isNotEmpty(partySummary.get("annualRevenue"))) {
                DecimalFormat myFormatter = new DecimalFormat("###,###.###");
                String formattedAnnualRevenue = myFormatter.format(partySummary.get("annualRevenue"));
                inputContext.put("annualRevenue", formattedAnnualRevenue);
            }
        }

        if (partySummary != null && partySummary.get("parentPartyId") != null) {
            //parentParty = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", partySummary.get("parentPartyId")),false);
            //context.put("parentParty", parentParty);

            String parentPartyName = org.fio.homeapps.util.DataUtil.getPartyName(delegator, salesRepGv.getString("partyIdTo"));

            inputContext.put("parentPartyId_desc", parentPartyName);
            if (request.getRequestURI().contains("view")) {
                inputContext.put("parentPartyId", parentPartyName);
            }
        }

        context.put("partyStatusId", partySummary.getString("statusId"));

        inputContext.put("accountName", partySummary.get("groupName"));
        inputContext.put("currencyUomId", partySummary.get("preferredCurrencyUomId"));

        //set the appbar context value
        actionBarContext.put("name", partySummary.get("groupName"));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
        String createdDate = partySummary.get("createdDate");
        if (UtilValidate.isNotEmpty(createdDate)) {
            createdDate = df1.format(df.parse(createdDate));
        }
        //kpiBarContext.put("accountSince", createdDate);

        // fillup administration info
        inputContext.put("createdOn", UtilValidate.isNotEmpty(partySummary.get("createdDate")) ? UtilDateTime.timeStampToString(partySummary.getTimestamp("createdDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
        inputContext.put("modifiedOn", UtilValidate.isNotEmpty(partySummary.get("lastModifiedDate")) ? UtilDateTime.timeStampToString(partySummary.getTimestamp("lastModifiedDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
        inputContext.put("createdBy", partySummary.get("createdByUserLogin"));
        inputContext.put("modifiedBy", partySummary.get("lastModifiedByUserLogin"));

    }

    EntityCondition thrudateCon1 = EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
    EntityCondition thrudateCon2 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp());
    EntityCondition dateCondition = EntityCondition.makeCondition(thrudateCon1, EntityOperator.OR, thrudateCon2);
    List < EntityCondition > conList = FastList.newInstance();
    conList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
    conList.add(dateCondition);

    //Tax Auth Info
    List PartyTaxAuthInfoList = delegator.findList("PartyTaxAuthInfo", EntityCondition.makeCondition(conList, EntityOperator.AND), null, null, null, false);
    context.put("PartyTaxAuthInfoList", PartyTaxAuthInfoList);
    println("PartyTaxAuthInfoList>>> " + PartyTaxAuthInfoList);
    if (UtilValidate.isNotEmpty(PartyTaxAuthInfoList)) {
        PartyTaxAuthInfo = PartyTaxAuthInfoList.get(PartyTaxAuthInfoList.size() - 1);
        inputContext.put("partyTaxId", PartyTaxAuthInfo.get("partyTaxId"));
        inputContext.put("isExempt", PartyTaxAuthInfo.get("isExempt"));
        context.put("PartyTaxAuth", PartyTaxAuthInfo);
    }

    //get Enum list with isenable null and "Y"
    String enumTypeId = "CONTENT_CLASS";
    enumList = EnumUtil.getEnableEnums(delegator, enumTypeId);
    inputContext.put("enumValues", enumList);

    Timestamp now = UtilDateTime.nowTimestamp();

    // gather data that should only be available for active accounts
    if (accountActive) {
        // set this flag to allow contact mechs to be shown
        context.put("displayContactMechs", "Y");
        // who is currently responsible for account
        responsibleParty = PartyHelper.getCurrentResponsibleParty(partyId, "ACCOUNT", delegator);
        context.put("responsibleParty", responsibleParty);

        List < EntityCondition > conditionsList = new ArrayList < EntityCondition > ();
        conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        conditionsList.add(EntityUtil.getFilterByDateExpr());
        EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
        List < GenericValue > partyDataSourceList = EntityQuery.use(delegator).from("PartyDataSource").where(mainConditons).queryList();

        if (UtilValidate.isNotEmpty(partyDataSourceList)) {
            List < String > dataSourceIds = EntityUtil.getFieldListFromEntityList(partyDataSourceList, "dataSourceId", true);

            if (request.getRequestURI().contains("updateAccount")) {
                inputContext.put("dataSourceId", UtilValidate.isNotEmpty(dataSourceIds) ? org.fio.admin.portal.util.DataUtil.listToString(dataSourceIds) : "");
            }
            if (request.getRequestURI().contains("viewAccount")) {
                List < GenericValue > dataSourceList = EntityQuery.use(delegator).from("DataSource").where(EntityCondition.makeCondition("dataSourceId", EntityOperator.IN, dataSourceIds)).queryList();
                if (UtilValidate.isNotEmpty(dataSourceList)) {
                    List < String > descriptions = EntityUtil.getFieldListFromEntityList(dataSourceList, "description", true);
                    inputContext.put("dataSourcesName", UtilValidate.isNotEmpty(descriptions) ? org.fio.admin.portal.util.DataUtil.listToString(descriptions) : "");
                }
            }
            inputContext.put("dataSourceIds", UtilValidate.isNotEmpty(dataSourceIds) ? org.fio.admin.portal.util.DataUtil.listToString(dataSourceIds) : "");
        }

        // account marketing campaigns TODO: create MarketingCampaignAndRole entity, then use peformFind service so that we can paginate
        campaignRoles = delegator.findByAnd("MarketingCampaignRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "ACCOUNT"), null, true);
        campaigns = EntityUtil.getRelated("MarketingCampaign", campaignRoles);
        context.put("marketingCampaigns", campaigns);
        if ((campaignsList = EntityUtil.getFieldListFromEntityList(campaigns, "campaignName", false)) != null) {
            marketingCampaignsAsString = StringUtil.join(campaignsList, ", ");
            if (marketingCampaignsAsString != null && marketingCampaignsAsString.length() > 2)
                context.put("marketingCampaignsAsString", marketingCampaignsAsString);
        }

        // account notes
        /*results = delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", partyId), UtilMisc.toList("noteDateTime DESC"),true);
        context.put("notesList", results);*/

        // account team members
        accountTeamMembers = delegator.findByAnd("PartyToSummaryByRelationship", UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", "ACCOUNT", "partyRelationshipTypeId", "ASSIGNED_TO"), null, true);
        accountTeamMembers = EntityUtil.filterByDate(accountTeamMembers);
        context.put("accountTeamMembers", accountTeamMembers);

        // Provide current PartyClassificationGroups as a list and a string
        groups = PartyHelper.getClassificationGroupsForParty(partyId, delegator);
        context.put("partyClassificationGroups", groups);
        descriptions = EntityUtil.getFieldListFromEntityList(groups, "customFieldName", false);
        inputContext.put("segment", StringUtil.join(descriptions, ", "));
        if (UtilValidate.isNotEmpty(groups)) {
            customIds = EntityUtil.getFieldListFromEntityList(groups, "customFieldId", false);
            inputContext.put("partyClassificationGroupId", customIds.get(0));
        }

        //get the team list
        conditions = EntityCondition.makeCondition([
                //EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_TEAM")
            ],
            EntityOperator.AND);
        teamList = delegator.findList("PartyAndRole", conditions, null, null, null, false);
        if (UtilValidate.isNotEmpty(teamList)) {
            teamNames = org.fio.homeapps.util.PartyHelper.getPartyNames(delegator, teamList, "partyId");
            if (UtilValidate.isNotEmpty(teamNames)) {
                teamNames = StringUtil.join(teamNames, ", ");
                inputContext.put("teamId", teamNames);
            }
        }

        //if (CrmsfaSecurity.hasPartyRelationSecurity(, "CRMSFA_ACCOUNT", "_DEACTIVATE", request.getAttribute("userLogin"), partyId)) {
        Security security = request.getAttribute("security")
        if (security.hasPermission("CRMSFA_ACCOUNT_DEACTIVATE", userLogin)) {
            context.put("hasDeactivatePermission", true);
        }
        if (security.hasPermission("CRMSFA_ACCOUNT_REASSIGN", userLogin)) {
            context.put("hasReassignPermission", true);
        }

        party = from("Party").where("partyId", partyId).queryOne();
        if (UtilValidate.isNotEmpty(party)) {
            partyStatus = party.statusId;
        }
        if (UtilValidate.isNotEmpty(partyStatus)) {
            curStatus = from("StatusItem").where("statusId", partyStatus).queryOne();
            statusDecription = "";
            if (UtilValidate.isNotEmpty(curStatus)) {
                statusDecription = curStatus.description;
            }
            inputContext.put("accountStatus", statusDecription);
        }
    } else {
        accountDeactivationDate = PartyHelper.getDeactivationDate(partyId, delegator);
        context.put("accountDeactivated", true);
        context.put("accountDeactivatedDate", accountDeactivationDate);
        context.put("validView", true); // can still view history of deactivated contacts

        party = from("Party").where("partyId", partyId).queryOne();
        if (UtilValidate.isNotEmpty(party)) {
            partyStatus = party.statusId;
        }
        if (UtilValidate.isNotEmpty(partyStatus)) {
            curStatus = from("StatusItem").where("statusId", partyStatus).queryOne();
            if (UtilValidate.isNotEmpty(curStatus)) {
                statusDecription = curStatus.description;
            }
            statusDecription = "";
            inputContext.put("accountStatus", statusDecription);
        }

    }

    crossReferenceList = delegator.findByAnd("CrossReference", UtilMisc.toMap("crossReferenceTypeId", "CUSTOMER_REFERENCE"), null, false);
    if (UtilValidate.isNotEmpty(crossReferenceList)) {
        context.put("crossReferenceList", crossReferenceList);
    }

    Map < String, Object > contactAcctMap = new HashMap < String, Object > ();
    contactAcctMap.put("partyIdTo", partyId);
    contactAcctMap.put("partyRoleTypeId", "ACCOUNT");
    Map < String, Object > result = dispatcher.runSync("common.getContactAndPartyAssoc", contactAcctMap);
    if (ServiceUtil.isSuccess(result)) {
        context.partyContactAssocList = result.partyContactAssoc;
        if (UtilValidate.isNotEmpty(result)) {
            List primaryContactsList = new ArrayList();
            primaryContactsList = result.partyContactAssoc;
            String primaryContactName = "";
            String contactId = "";
            String primaryCId = "";
            for (int i = 0; i < primaryContactsList.size(); i++) {
                Map < String, Object > partyContactMap = new HashMap < String, Object > ();
                partyContactMap = (Map < String, Object > ) primaryContactsList.get(i);
                if (i == 0) {
                    primaryContactName = (String) partyContactMap.get("name");
                    contactId = (String) partyContactMap.get("contactId");
                }
                String primaryContactStatusId = partyContactMap.get("statusId");
                if ("PARTY_DEFAULT".equals(primaryContactStatusId)) {
                    primaryContactName = (String) partyContactMap.get("name");
                    contactId = (String) partyContactMap.get("contactId");
                    primaryCId = (String) partyContactMap.get("contactId");
                }
            }
            inputContext.put("PrimaryContact", primaryContactName);
            if (contactId != null) {
                primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, contactId, UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true), true);

                if (UtilValidate.isNotEmpty(primaryContactInformation)) {
                    inputContext.put("PrimaryContactEmail", primaryContactInformation.get("EmailAddress"));
                    String PrimaryContactPhone = primaryContactInformation.get("PrimaryPhone");
                    PrimaryContactPhone = UtilValidate.isNotEmpty(PrimaryContactPhone) ? PrimaryContactPhone.replace("+", "") : PrimaryContactPhone;
                    inputContext.put("PrimaryContactPhone", PrimaryContactPhone);

                    //added for getting current time of primary contact
                    if (UtilValidate.isNotEmpty(contactId)) {
                        primaryContactTZ = from("PartySummaryDetailsView").where("partyId", contactId).queryOne();
                        if (UtilValidate.isNotEmpty(primaryContactTZ.getString("timeZoneDesc"))) {
                            currentTimeForTimezone = UtilActivity.getCurrentTimeFromTimeZone(delegator, primaryContactTZ.getString("timeZoneDesc"));
                            inputContext.put("currentTimeForTimezone", currentTimeForTimezone);
                        }
                    }
                    //ended
                    actionBarContext.put("primaryEmail", primaryContactInformation.get("EmailAddress"));
                    actionBarContext.put("emailSolicitation", primaryContactInformation.get("emailSolicitation"));
                    //actionBarContext.put("primaryPhone", org.groupfio.common.portal.util.DataHelper.preparePhoneNumber(delegator, primaryContactInformation.get("PrimaryPhone")));
                    actionBarContext.put("phoneSolicitation", primaryContactInformation.get("phoneSolicitation"));

                }
				primaryPhoneInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMapsExt(delegator, partyId);
				actionBarContext.put("primaryPhone", primaryPhoneInformation.get("PrimaryPhone"));
				
            }
        }
    }
    context.put("actionBarContext", actionBarContext);

    inputContext.put("primaryWebUrl", primaryContactInformation.get("webURL"));

    inputContext.put("personResponsible", org.fio.homeapps.util.PartyHelper.getCurrentResponsiblePartyName(partyId, "ACCOUNT", delegator));
    List < EntityCondition > relationConditionList = FastList.newInstance();
    relationConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
    relationConditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"));
    relationConditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"));
    relationConditionList.add(EntityUtil.getFilterByDateExpr());

    EntityCondition partyRelationshipCondition = EntityCondition.makeCondition(relationConditionList, EntityOperator.AND);

    GenericValue partyRelationship = EntityUtil.getFirst(delegator.findList("PartyRelationship", partyRelationshipCondition, UtilMisc.toSet("partyIdTo"), null, null, false));
    if (UtilValidate.isNotEmpty(partyRelationship)) {
        context.put("responsiblePersonPartyId", UtilValidate.isNotEmpty(partyRelationship.getString("partyIdTo")) ? partyRelationship.getString("partyIdTo") : "")
    	//relationManager = org.fio.homeapps.util.PartyHelper.getCurrentResponsibleParty(partyId, "ACCOUNT", delegator);
	    context.put("selectedRMId", context.get("responsiblePersonPartyId"));
    }

    kpiMetric = org.groupfio.account.portal.util.DataHelper.prepareHomeKpiInfo(delegator, userLogin);
    context.put("kpiMetric", kpiMetric);

    if (UtilValidate.isNotEmpty(request.getParameter("domainEntityType"))) {
        inputContext.put("domainEntityType1", org.groupfio.common.portal.util.DataHelper.convertToLabel(request.getParameter("domainEntityType")));
    }
    if (UtilValidate.isNotEmpty(request.getParameter("domainEntityId"))) {
        inputContext.put("domainEntityId1", request.getParameter("domainEntityId"));
    }

    context.put("partyRoleTypeId", "ACCOUNT");
	    
    context.put("domainEntityId", partyId);
    context.put("domainEntityType", "ACCOUNT");
    context.put("requestURI", "viewAccount");
    context.put("partyId", partyId);

    String externalLoginKey = request.getParameter("externalLoginKey");
    if (externalLoginKey == null)
        externalLoginKey = request.getAttribute("externalLoginKey");
    context.put("externalLoginKey", externalLoginKey);
    //println("externalLoginKey-------"+externalLoginKey);

    ResultSet rs = null;
    SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
    Map < String, Object > kpiBarContext = new LinkedHashMap < String, Object > ();

    String accountSince = "";
    String accountStatus = "";
    String mostRecentCamp = "";
    String noOfOppoExpire = "";
    String noOfOpenOppo = "";
    String noOfOpenActivity = "";
    String noOfActiveCamp = "";
    String noOfOpenSr = "";
    String lastClickedDate = "";
    String lastOpenDate = "";
    String ytd = "";
    String lytd = "";
    String clv = "";
    String callBackDate = "";

    if (UtilValidate.isNotEmpty(partyId)) {

        String sinceDateSql = "SELECT DATE_FORMAT(attr_value,'%m/%d/%Y') AS account_since FROM party_attribute WHERE PARTY_ID = '" + partyId + "' AND ATTR_NAME = 'CUST_SETUP_DT'";
        rs = sqlProcessor.executeQuery(sinceDateSql);
        if (rs != null) {
            while (rs.next()) {
                accountSince = rs.getString("account_since");
            }
        }
        if (UtilValidate.isEmpty(accountSince)) {
            String sinceDateSql1 = "SELECT DATE_FORMAT(DATE(p.created_date),'%m/%d/%Y') AS account_since FROM party p INNER JOIN party_role pr ON p.party_id=pr.party_id AND pr.role_type_id='ACCOUNT' AND p.party_id='" + partyId + "' LIMIT 1";
            rs = sqlProcessor.executeQuery(sinceDateSql1);
            if (rs != null) {
                while (rs.next()) {
                    accountSince = rs.getString("account_since");
                }
            }
        }

        String mostRecentCampSql = "SELECT MC.CAMPAIGN_NAME as 'most_recent_camp' FROM MARKETING_CAMPAIGN MC INNER JOIN   `marketing_campaign_contact_list`  CL ON MC.MARKETING_CAMPAIGN_ID=CL.MARKETING_CAMPAIGN_ID INNER JOIN CONTACT_LIST_PARTY CLP ON CLP.CONTACT_LIST_ID=CL.CONTACT_LIST_ID INNER JOIN PARTY_ROLE PR ON (PR.PARTY_ID=CLP.ACCT_PARTY_ID OR PR.PARTY_ID=CLP.PARTY_ID)  WHERE MC.STATUS_ID IN ('MKTG_CAMP_CREATED','MKTG_CAMP_INPROGRESS','MKTG_CAMP_SCHEDULED') AND PR.ROLE_TYPE_ID='ACCOUNT' AND ( CLP.ACCT_PARTY_ID ='" + partyId + "' OR CLP.PARTY_ID = '" + partyId + "' ) ORDER BY  mc.last_updated_tx_stamp DESC LIMIT 1";
        rs = sqlProcessor.executeQuery(mostRecentCampSql);
        if (rs != null) {
            while (rs.next()) {
                mostRecentCamp = rs.getString("most_recent_camp");
            }
        }

        String noOfOppoExpireSql = "SELECT COUNT(*) as 'expire_opportunity' FROM `sales_opportunity` SO INNER JOIN `sales_opportunity_role` SOR ON SO.SALES_OPPORTUNITY_ID=SOR.SALES_OPPORTUNITY_ID AND SoR.ROLE_TYPE_ID='ACCOUNT' AND SO.OPPORTUNITY_STATUS_ID='OPPO_OPEN' AND  DATEDIFF((SO.ESTIMATED_CLOSE_DATE),DATE(NOW()))<=7 AND DATEDIFF((SO.ESTIMATED_CLOSE_DATE),DATE(NOW()))>=0 AND sor.party_id='" + partyId + "'";
        rs = sqlProcessor.executeQuery(noOfOppoExpireSql);
        if (rs != null) {
            while (rs.next()) {
                noOfOppoExpire = rs.getString("expire_opportunity");
            }
        }

        String noOfOpenOppoSql = "SELECT COUNT(*) as 'open_opportunities' FROM `sales_opportunity` S INNER  JOIN `sales_opportunity_role` SR ON S.SALES_OPPORTUNITY_ID=SR.SALES_OPPORTUNITY_ID AND S.OPPORTUNITY_STATUS_ID='OPPO_OPEN' AND SR.ROLE_TYPE_ID='ACCOUNT' AND sr.party_id='" + partyId + "'";
        rs = sqlProcessor.executeQuery(noOfOpenOppoSql);
        if (rs != null) {
            while (rs.next()) {
                noOfOpenOppo = rs.getString("open_opportunities");
            }
        }

        String noOfOpenActivitySql = "SELECT COUNT(*) as 'open_activities' FROM work_effort w INNER JOIN work_Effort_party_assignment we ON w.work_effort_id=we.work_effort_id AND w.current_status_id='IA_OPEN' AND we.role_type_id='ACCOUNT' AND we.party_id='" + partyId + "'";
        rs = sqlProcessor.executeQuery(noOfOpenActivitySql);
        if (rs != null) {
            while (rs.next()) {
                noOfOpenActivity = rs.getString("open_activities");
            }
        }

        String noOfActiveCampSql = "SELECT  COUNT(DISTINCT MC.MARKETING_CAMPAIGN_ID) as 'active_campaign' FROM MARKETING_CAMPAIGN MC INNER JOIN marketing_campaign_contact_list CL ON MC.MARKETING_CAMPAIGN_ID=CL.MARKETING_CAMPAIGN_ID INNER JOIN CONTACT_LIST_PARTY CLP ON CLP.CONTACT_LIST_ID=CL.CONTACT_LIST_ID INNER JOIN PARTY_ROLE PR ON (PR.PARTY_ID =CLP.PARTY_ID OR PR.PARTY_ID = CLP.ACCT_PARTY_ID) WHERE PR.ROLE_TYPE_ID='ACCOUNT' AND MC.END_DATE >= (DATE(NOW())) AND (CLP.PARTY_ID='" + partyId + "' OR CLP.ACCT_PARTY_ID ='" + partyId + "')";

        rs = sqlProcessor.executeQuery(noOfActiveCampSql);
        if (rs != null) {
            while (rs.next()) {
                noOfActiveCamp = rs.getString("active_campaign");
            }
        }

        String openSRSql = "SELECT COUNT(*) as 'open_sr_count' FROM cust_request cr INNER JOIN party_role crp ON cr.FROM_PARTY_ID=crp.PARTY_ID WHERE cr.status_id IN ('SR_OPEN','SR_ASSIGNED','SR_IN_PROGRESS','SR_PENDING') AND crp.role_type_id='ACCOUNT' AND cr.FROM_PARTY_ID='" + partyId + "'";
        rs = sqlProcessor.executeQuery(openSRSql);
        if (rs != null) {
            while (rs.next()) {
                noOfOpenSr = rs.getString("open_sr_count");
            }
        }

        String lastClickedDateSql = "SELECT DATE(CR.clicked_timestamp) as 'last_clicked' FROM clicked_report_summary CR INNER JOIN PARTY_ROLE PR ON CR.PARTY_ID=PR.PARTY_ID AND PR.ROLE_TYPE_ID='ACCOUNT' AND pr.party_id='" + partyId + "' ORDER BY CR.clicked_timestamp DESC LIMIT 1";
        rs = sqlProcessor.executeQuery(lastClickedDateSql);
        if (rs != null) {
            while (rs.next()) {
                lastClickedDate = rs.getString("last_clicked");
            }
        }

        String lastOpenDateSql = "SELECT DATE(CO.OPENED_TIMESTAMP) as 'last_open_date' FROM open_report_summary CO INNER JOIN PARTY_ROLE PR ON CO.PARTY_ID=PR.party_id AND PR.ROLE_TYPE_ID='ACCOUNT' AND pr.party_id='" + partyId + "' ORDER BY CO.OPENED_TIMESTAMP DESC LIMIT 1";
        rs = sqlProcessor.executeQuery(lastOpenDateSql);
        if (rs != null) {
            while (rs.next()) {
                lastOpenDate = rs.getString("last_open_date");
            }
        }

        GenericValue clvYtdLytdTableGv = delegator.findOne("SystemProperty", [systemResourceId: "party", systemPropertyId: "clvYtdLytdTableName"], false);
        if (clvYtdLytdTableGv != null)
            clvYtdLytdTableName = clvYtdLytdTableGv.getString("systemPropertyValue");
        else
            clvYtdLytdTableName = "invoice_transaction_master";

        // CLV
        String clvSql = "SELECT IFNULL(SUM(total_sales_amount),0) as 'clv' FROM (SELECT total_sales_amount FROM " + clvYtdLytdTableName + " WHERE bill_to_party_id='" + partyId + "' GROUP BY invoice_id) clv";
        rs = sqlProcessor.executeQuery(clvSql);
        if (rs != null) {
            while (rs.next()) {
                clv = rs.getString("clv");
            }
        }
        //YTD
        String ytdSql = "SELECT IFNULL(SUM(total_sales_amount),0) as 'ytd'  FROM (SELECT total_sales_amount FROM " + clvYtdLytdTableName + " WHERE bill_to_party_id='" + partyId + "' AND YEAR(invoice_date) =YEAR(now()) GROUP BY invoice_id ) ytd";
        rs = sqlProcessor.executeQuery(ytdSql);
        if (rs != null) {
            while (rs.next()) {
                ytd = rs.getString("ytd");
            }
        }
        //LYTD
        String lytdSql = "SELECT IFNULL(SUM(total_sales_amount),0) as 'lytd' FROM (SELECT total_sales_amount FROM " + clvYtdLytdTableName + " WHERE bill_to_party_id='" + partyId + "' AND YEAR(invoice_date)=(YEAR(NOW())-1) GROUP BY invoice_id) lytd";
        rs = sqlProcessor.executeQuery(lytdSql);
        if (rs != null) {
            while (rs.next()) {
                lytd = rs.getString("lytd");
            }
        }

        //String callBackDateSql = "SELECT DATE(call_back_date) as 'call_back_date' FROM sales_opportunity a INNER JOIN sales_opportunity_role b ON a.SALES_OPPORTUNITY_ID=b.SALES_OPPORTUNITY_ID WHERE ROLE_TYPE_ID='ACCOUNT'  AND call_back_date IS NOT NULL AND b.party_id='"+partyId+"' ORDER BY call_back_date DESC LIMIT 1";
        String callBackDateSql = "SELECT DATE(call_back_date) as 'call_back_date' FROM `sales_opportunity` a INNER JOIN `sales_opportunity_role` b ON a.`SALES_OPPORTUNITY_ID`=b.`SALES_OPPORTUNITY_ID` WHERE `ROLE_TYPE_ID`='ACCOUNT' AND call_back_date IS NOT NULL AND call_back_date>=DATE(NOW()) AND b.party_id='" + partyId + "' ORDER BY call_back_date ASC LIMIT 1";
        rs = sqlProcessor.executeQuery(callBackDateSql);
        if (rs != null) {
            while (rs.next()) {
                callBackDate = rs.getString("call_back_date");
            }
        }

        sqlProcessor.close();

        // get economic matric
        EntityCondition partyMertricCon = EntityCondition.makeCondition(EntityOperator.AND,
            EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "BAL_METRICS"),
            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
            EntityCondition.makeCondition("groupingCode", EntityOperator.EQUALS, "CUSTOMER_BAL_METRICS"),
        );
        List < GenericValue > economicMatrics = EntityQuery.use(delegator).from("PartyMetricIndicator").where(partyMertricCon).queryList();
        Map < String, Object > economicMap = DataUtil.getMapFromGeneric(economicMatrics, "customFieldId", "propertyValue", false);
        kpiBarContext.putAll(economicMap);
        //location from attr
        String partyLoc = org.fio.homeapps.util.DataUtil.getPartyAttrValue(delegator, partyId, "CUST_LOC_NAME");
        kpiBarContext.put("party_location", partyLoc);
        inputContext.put("party_location", partyLoc);
        // sale rep
        EntityCondition salesRepCond = EntityCondition.makeCondition(EntityOperator.AND,
            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
            EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
            EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "SALES_REP"),
            EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, "SALES_MANAGER"),
            EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ASSIGNED_TO")
        );
        GenericValue salesRepGv = EntityQuery.use(delegator).from("PartyRelationship").where(salesRepCond).filterByDate().queryFirst();
        if (UtilValidate.isNotEmpty(salesRepGv)) {
            String saleRep = org.fio.homeapps.util.DataUtil.getPartyName(delegator, salesRepGv.getString("partyIdTo"));
            kpiBarContext.put("sales_rep", saleRep);
            inputContext.put("sales_rep", saleRep);
        }

        // parent account
        String isParentAccount = "N";
        EntityCondition parentAcctCond = EntityCondition.makeCondition(EntityOperator.AND,
            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
            EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
            EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "REL_PARENT_ACCOUNT")
        );
        GenericValue parentAcctGv = EntityQuery.use(delegator).from("PartyRelationship").where(parentAcctCond).filterByDate().queryFirst();
        if (UtilValidate.isNotEmpty(parentAcctGv)) {
            String parentAcctName = org.fio.homeapps.util.DataUtil.getPartyName(delegator, parentAcctGv.getString("partyIdTo"));
            kpiBarContext.put("parent_acc", parentAcctName);
            inputContext.put("parent_acc", parentAcctName);
        }
        List < GenericValue > partyRoleDet = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "PARENT_ACCOUNT"), null, false);
        if (UtilValidate.isNotEmpty(partyRoleDet)) {
            isParentAccount = "Y";
        }
        inputContext.put("isParentAccount", isParentAccount);
        // order count
        EntityCondition orderCondition = EntityCondition.makeCondition(EntityOperator.AND,
            EntityCondition.makeCondition("billToPartyId", EntityOperator.EQUALS, partyId)
        );
        long orderCount = EntityQuery.use(delegator).select("orderId").from("RmsTransactionMaster").where(orderCondition).distinct().queryList().size();
        kpiBarContext.put("no_of_order", orderCount);
    }

    kpiBarContext.put("account-since", accountSince);
    kpiBarContext.put("expire-opportunity-count", noOfOppoExpire);
    kpiBarContext.put("recent-campaign", mostRecentCamp);
    kpiBarContext.put("open-opportunity-count", noOfOpenOppo);
    kpiBarContext.put("open-activity-count", noOfOpenActivity);
    kpiBarContext.put("active-campaign-count", noOfActiveCamp);
    kpiBarContext.put("open-sr-count", noOfOpenSr);
    kpiBarContext.put("last-clicked-date", lastClickedDate);
    kpiBarContext.put("last-open-date", lastOpenDate);
    kpiBarContext.put("ytd", thousandSeparator(ytd));
    kpiBarContext.put("clv", thousandSeparator(clv));
    kpiBarContext.put("lytd", thousandSeparator(lytd));
    kpiBarContext.put("call-back-date", callBackDate);

    context.put("kpiBarContext", kpiBarContext);

    String isEnableRebateModule = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_REBATE_MODULE");
    if (UtilValidate.isNotEmpty(isEnableRebateModule)) {
        context.put("isEnableRebateModule", isEnableRebateModule);
    }

    String isEnableInvoiceModule = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_INVOICE_MODULE");
    if (UtilValidate.isNotEmpty(isEnableRebateModule)) {
        context.put("isEnableInvoiceModule", isEnableInvoiceModule);
    }

    String isEnableSurvey = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_SURVEY_TAB", "N");
    if (UtilValidate.isNotEmpty(isEnableSurvey)) {
        context.put("isEnableSurvey", isEnableSurvey);
    }
}

context.put("inputContext", inputContext);

static thousandSeparator(String number) {
    if (UtilValidate.isNotEmpty(number)) {
        NumberFormat usFormatter = NumberFormat.getInstance(new Locale("en", "US"));
        double num = number.toDouble();
        number = usFormatter.format(num);
    }
    return number;
}

String isEnableIUCInt = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IUC_INT_ENABLED", "N");
context.put("isEnableIUCInt", isEnableIUCInt);

context.put("isBoldBIReportEnabled", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_BOLDBI_REPORT_ENABLED", "N"));
context.put("isEnableDashboardButton",org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_VIEW_DASHBOARD_BTN_ENABLED", "Y"));
