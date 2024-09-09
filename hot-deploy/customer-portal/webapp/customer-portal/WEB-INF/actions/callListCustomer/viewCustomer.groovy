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

yesNoMap = ["Y":"Yes", "N":"No"];
context.put("yesNoOptions", yesNoMap);

inputContext = new LinkedHashMap<String, Object>();

partyId = parameters.get("partyId");
haveDataPermission = "Y";
String userLoginId = userLogin.getString("partyId");

if (locale == null) locale = UtilHttp.getLocale(request);

println("haveDataPermission>>> " + haveDataPermission);
context.put("haveDataPermission", haveDataPermission);

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

Map<String, Object> actionBarContext = new HashMap<String, Object>();
postalAddress = null;
countValue=null;
if(partyId!=null){
	partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
	context.put("contactMeches", partyContactMechValueMaps);
	testCount = org.groupfio.customer.portal.event.AjaxEvents.getNonPrimaryPhoneNumberCount(delegator, partyId);
	if(UtilValidate.isNotEmpty(testCount)) {
		countValue=testCount.get("count");
	}
	actionBarContext.put("phone",UtilValidate.isNotEmpty(countValue)?countValue:"0");
	
	primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
	if(UtilValidate.isNotEmpty(primaryContactInformation)) {
		actionBarContext.put("primaryEmail",primaryContactInformation.get("EmailAddress"));
		actionBarContext.put("emailSolicitation",primaryContactInformation.get("emailSolicitation"));
		//actionBarContext.put("primaryPhone", org.groupfio.common.portal.util.DataHelper.preparePhoneNumber(delegator, primaryContactInformation.get("PrimaryPhone")));
		actionBarContext.put("phoneSolicitation",primaryContactInformation.get("phoneSolicitation"));

		inputContext.put("emailSolicitation",primaryContactInformation.get("emailSolicitation"));
		inputContext.put("phoneSolicitation",primaryContactInformation.get("phoneSolicitation"));
		inputContext.put("partyPrimaryPhone", primaryContactInformation.get("PrimaryPhone"));
        inputContext.put("primaryPhoneNumber", primaryContactInformation.get("PrimaryPhone"));
		inputContext.put("partyPrimaryEmail", primaryContactInformation.get("EmailAddress"));
		inputContext.put("primaryEmail", primaryContactInformation.get("EmailAddress"));
		
		context.put("phoneNumber", primaryContactInformation.get("primaryContactNumber"));
		context.put("phoneCountryCode", primaryContactInformation.get("primaryContactCountryCode"));
	}
	
	primaryPhoneInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMapsExt(delegator, partyId);
	inputContext.put("primaryTelecomNumber", primaryPhoneInformation.get("TelecomNumber"));
	actionBarContext.put("primaryPhone", primaryPhoneInformation.get("PrimaryPhone"));
	inputContext.put("primaryContactCountryCode", primaryPhoneInformation.get("primaryContactCountryCode"));
	inputContext.put("primaryContactAreaCode", primaryPhoneInformation.get("primaryContactAreaCode"));
	//get postal address
	postalAddress = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryPostal(delegator,partyId);
	if (UtilValidate.isNotEmpty(postalAddress)) {
		String wrapPostal = "";
		if (UtilValidate.isNotEmpty(postalAddress.getString("address1"))) {
			wrapPostal += postalAddress.getString("address1")+", ";
			context.put("address1", postalAddress.getString("address1"));
			inputContext.put("address1", postalAddress.getString("address1"))
		}
		if (UtilValidate.isNotEmpty(postalAddress.getString("address2"))) {
			wrapPostal += postalAddress.getString("address2")+", ";
			context.put("address2", postalAddress.getString("address2"));
			inputContext.put("address2", postalAddress.getString("address2"))
		}
		if (UtilValidate.isNotEmpty(postalAddress.getString("city"))) {
			wrapPostal += postalAddress.getString("city")+", ";
			context.put("city", postalAddress.getString("city"));
			inputContext.put("city", postalAddress.getString("city"))
		}
		if (UtilValidate.isNotEmpty(postalAddress.getString("stateProvinceGeoId"))) {
            String state = org.fio.admin.portal.util.DataUtil.getGeoName(delegator, postalAddress.getString("stateProvinceGeoId"), "STATE");
			context.put("stateGeoId", postalAddress.getString("stateProvinceGeoId"));
            if (UtilValidate.isNotEmpty(postalAddress.getString("stateProvinceGeoId"))) {
                wrapPostal += postalAddress.getString("stateProvinceGeoId") + ", ";
			}
		}
		if (UtilValidate.isNotEmpty(postalAddress.getString("postalCode"))) {
			wrapPostal += postalAddress.getString("postalCode");
			context.put("postalCode", postalAddress.getString("postalCode"));
			inputContext.put("postalCode", postalAddress.getString("postalCode"));
			if (UtilValidate.isNotEmpty(postalAddress.getString("postalCodeExt"))) {
				wrapPostal += "-"+postalAddress.getString("postalCodeExt");
				inputContext.put("postalCodeExt", postalAddress.getString("postalCodeExt"));
			}
		}
		if (UtilValidate.isNotEmpty(postalAddress.getString("countryGeoId"))) {
			wrapPostal += " ("+postalAddress.getString("countryGeoId")+")";
			context.put("generalCountryGeoId", postalAddress.getString("countryGeoId"));
		}
        inputContext.put("generalAddress", UtilValidate.isNotEmpty(wrapPostal) ? wrapPostal : "");
		inputContext.put("countryGeoId", postalAddress.getString("countryGeoId"));
		inputContext.put("editStateGeoId", postalAddress.getString("stateProvinceGeoId"));
		context.put("postalContactMechId", postalAddress.getString("contactMechId"));
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
}

//get PATIENT_ID
//make sure that the partyId is actually an CONTACT before trying to display it as once
delegator = request.getAttribute("delegator");
// set this flag to allow contact mechs to be shown
request.setAttribute("displayContactMechs", "Y");
context.put("displayContactMechs", "Y");
// contact summary data
partySummary = org.fio.homeapps.util.PartyHelper.getPartySummary(delegator, UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER", "primaryPostal", postalAddress));
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

	if (UtilValidate.isNotEmpty(partySummary.get("localTimeZone"))) {
		timeZone = partySummary.get("localTimeZone");
		GenericValue tzEnumeration = EntityQuery.use(delegator).from("Enumeration").where("enumId", timeZone, "enumTypeId","TIME_ZONE").queryFirst();
		if(UtilValidate.isEmpty(tzEnumeration)) {
			tzEnumeration = EntityQuery.use(delegator).from("Enumeration").where("enumCode", timeZone, "enumTypeId","TIME_ZONE").queryFirst();
		}
		String timeZoneId = UtilValidate.isNotEmpty(tzEnumeration) ? tzEnumeration.getString("enumCode") : "";
		String clientTime = "";
		if(UtilValidate.isNotEmpty(timeZoneId)) {
			try {
				Date Cdate = new Date();
				DateFormat dft = new SimpleDateFormat("hh:mm a");
				dft.setTimeZone(TimeZone.getTimeZone(timeZoneId));
				clientTime = dft.format(Cdate);

				if(clientTime !=""){
					String Ctime = clientTime.substring(0,1);
					if(Ctime.contains("0")){
						clientTime = clientTime.substring(1);
					}
				}
			} catch(Exception e) {
				clientTime = "";
			}
		}
		inputContext.put("clientTime",clientTime);
	} else {
		inputContext.put("timeZoneDesc_desc", "N/A");
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
	Date currentDate = new Date();
	String currentTimeWithAMPM = sdf.format(currentDate);
	inputContext.put("myTime",currentTimeWithAMPM);
	actionBarContext.put("myTime",currentTimeWithAMPM);
	
	//ended
    String gender = partySummary.get("gender");
    String maritalStatus = partySummary.get("maritalStatus");
    if (request.getRequestURI().contains("viewCallListCustomer")) {
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
	String contactName = partySummary.get("firstName");
	if(UtilValidate.isNotEmpty(partySummary.get("lastName")))
		contactName = contactName + " " +partySummary.get("lastName");
	actionBarContext.put("name", contactName);
	context.put("name", contactName);
	inputContext.put("partyName", contactName);
	
	// fillup administration info
    inputContext.put("createdOn", UtilValidate.isNotEmpty(partySummary.get("createdDate")) ? UtilDateTime.timeStampToString((Timestamp) partySummary.get("createdDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
    inputContext.put("modifiedOn", UtilValidate.isNotEmpty(partySummary.get("lastModifiedDate")) ? UtilDateTime.timeStampToString((Timestamp) partySummary.get("lastModifiedDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
	inputContext.put("createdBy", partySummary.get("createdByUserLogin"));
	inputContext.put("modifiedBy", partySummary.get("lastModifiedByUserLogin"));
}

inputContext.put("primaryEmail", actionBarContext.get("primaryEmail"));

println("inputContext> "+inputContext);

relationManager = org.fio.homeapps.util.PartyHelper.getCurrentResponsibleParty(partyId, "CUSTOMER", delegator);
String rMId=UtilValidate.isNotEmpty(relationManager)?relationManager.partyId:null;
context.put("selectedRMId", rMId);
context.put("partyRoleTypeId", "CUSTOMER");

if(UtilValidate.isNotEmpty(relationManager)) {
	String relationManagerName = org.fio.homeapps.util.PartyHelper.getPersonName(delegator, relationManager.getString("partyId"), false);
	inputContext.put("personResponsible", relationManagerName);
	context.put("responsibleName", relationManagerName);
}
if (UtilValidate.isEmpty(inputContext.get("personResponsible"))) {
    inputContext.put("personResponsible", "N/A");
}
context.put("domainEntityId", partyId);
context.put("domainEntityType", "CUSTOMER");
context.put("requestURI", "viewCallListCustomer");
context.put("partyId", partyId);
String externalLoginKey = request.getParameter("externalLoginKey");
if(externalLoginKey==null) {
	externalLoginKey=request.getAttribute("externalLoginKey");
}

context.put("externalLoginKey", externalLoginKey);
context.put("actionBarContext", actionBarContext);

ResultSet rs = null;
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Map<String, Object> kpiBarContext = new LinkedHashMap<String, Object>();

String callBackDate = "";
String clv = "";
String ytd = "";
String lytd = "";
String firstShopDate="";
String lastShopDate="";

if(UtilValidate.isNotEmpty(partyId)) {

	String entityName = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUSTOMER_RTM_ITM");
	if(UtilValidate.isEmpty(entityName))
		entityName = "InvoiceTransactionMaster";
	context.put("entityName", entityName);

	String clvYtdLytdTableName = ModelUtil.javaNameToDbName(entityName);
	if(UtilValidate.isEmpty(clvYtdLytdTableName))
		clvYtdLytdTableName = "INVOICE_TRANSACTION_MASTER";
	String clvSql = null;
	String ytdSql = null;
	String lytdSql = null;
	String firstShoppedDateSql = null;
	String lastShoppedDateSql = null;
	String id = null;
	String date = null;
	if("RMS_TRANSACTION_MASTER".equals(clvYtdLytdTableName)) {
		id = "order_id";
		date = "order_date";
	}else if("INVOICE_TRANSACTION_MASTER".equals(clvYtdLytdTableName)) {
		date = "invoice_date";
		id = "invoice_id";
	}
    String clvDataLoad = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CLV_DATA_LOAD", "");
    if ("MASTER_TABLE".equals(clvDataLoad)) {
	clvSql ="SELECT IFNULL(SUM(total_sales_amount),0) as 'clv' FROM (SELECT total_sales_amount FROM "+clvYtdLytdTableName+" WHERE bill_to_party_id='"+partyId+"' GROUP BY "+id+") clv LIMIT 1";
	ytdSql ="SELECT IFNULL(SUM(total_sales_amount),0) as 'ytd'  FROM (SELECT total_sales_amount FROM "+clvYtdLytdTableName+" WHERE bill_to_party_id='"+partyId+"' AND YEAR("+date+") =YEAR(now()) GROUP BY "+id+" ) ytd LIMIT 1";
	lytdSql = "SELECT IFNULL(SUM(total_sales_amount),0) as 'lytd' FROM (SELECT total_sales_amount FROM "+clvYtdLytdTableName+" WHERE bill_to_party_id='"+partyId+"' AND YEAR("+date+")=(YEAR(NOW())-1) GROUP BY "+id+") lytd LIMIT 1";
    } else {
        clvSql = "SELECT `PROPERTY_VALUE` as 'clv' FROM `party_metric_indicator` WHERE `CUSTOM_FIELD_ID`='TOTAL_PURCHASED' AND `PARTY_ID`='" + partyId + "'";
        ytdSql = "SELECT `DATA_VALUE` as 'ytd' FROM `party_transaction_data` WHERE `DATA_TYPE_ID`='TOTAL_PURCHASED_AMOUNT' AND `YEAR`=YEAR(NOW()) AND `MONTH`='ALL' AND `PARTY_ID`='" + partyId + "'";
        lytdSql = "SELECT `DATA_VALUE` as 'lytd' FROM `party_transaction_data` WHERE `DATA_TYPE_ID`='TOTAL_PURCHASED_AMOUNT' AND `YEAR`= (YEAR(NOW()) -1) AND `MONTH`='ALL' AND `PARTY_ID`='" + partyId + "'";
    }
	firstShoppedDateSql = "SELECT DATE("+date+") AS 'first_shopped_date' FROM "+clvYtdLytdTableName+" WHERE  bill_to_party_id ='"+partyId+"' GROUP BY "+date+" ORDER BY "+date+" ASC LIMIT 1";
	lastShoppedDateSql = "SELECT DATE("+date+") AS 'last_shopped_date' FROM "+clvYtdLytdTableName+" WHERE  bill_to_party_id ='"+partyId+"' GROUP BY "+date+" ORDER BY "+date+" DESC LIMIT 1";
	try {
		rs = sqlProcessor.executeQuery(clvSql);
		if (rs != null) {
			while (rs.next()) {
				clv= rs.getString("clv");
			}
		}

		rs = sqlProcessor.executeQuery(ytdSql);
		if (rs != null) {
			while (rs.next()) {
				ytd= rs.getString("ytd");
			}
		}

		rs = sqlProcessor.executeQuery(lytdSql);
		if (rs != null) {
			while (rs.next()) {
				lytd= rs.getString("lytd");
			}
		}

		rs = sqlProcessor.executeQuery(firstShoppedDateSql);
		if (rs != null) {
			while (rs.next()) {
				firstShopDate= rs.getString("first_shopped_date");
                if (UtilValidate.isNotEmpty(firstShopDate)) {
                    Date firstShopDateDate = dbDate.parse(firstShopDate);
                    firstShopDate = usDate.format(firstShopDateDate);
			}
		}
        }

		rs = sqlProcessor.executeQuery(lastShoppedDateSql);
		if (rs != null) {
			while (rs.next()) {
				lastShopDate= rs.getString("last_shopped_date");
                if (UtilValidate.isNotEmpty(lastShopDate)) {
                    Date lastShopDateDate = dbDate.parse(lastShopDate);
                    lastShopDate = usDate.format(lastShopDateDate);
			}
		}
        }
	}catch (Exception e){
		Debug.log("Error Message"+e);
	}
}
BigDecimal clvDecimal = UtilValidate.isNotEmpty(clv) ? BigDecimal.valueOf(Double.valueOf(clv)).setScale(0, BigDecimal.ROUND_HALF_EVEN) : BigDecimal.ZERO;
clv = clvDecimal.toString();
BigDecimal ytdDecimal = UtilValidate.isNotEmpty(ytd) ? BigDecimal.valueOf(Double.valueOf(ytd)).setScale(0, BigDecimal.ROUND_HALF_EVEN) : BigDecimal.ZERO;
ytd = ytdDecimal.toString();
BigDecimal lytdDecimal = UtilValidate.isNotEmpty(lytd) ? BigDecimal.valueOf(Double.valueOf(lytd)).setScale(0, BigDecimal.ROUND_HALF_EVEN) : BigDecimal.ZERO;
lytd = lytdDecimal.toString();
kpiBarContext.put("call-back-date", callBackDate);
kpiBarContext.put("clv", org.groupfio.common.portal.util.DataUtil.numberFormatter(locale, clv));
kpiBarContext.put("ytd", org.groupfio.common.portal.util.DataUtil.numberFormatter(locale, ytd));
kpiBarContext.put("lytd", org.groupfio.common.portal.util.DataUtil.numberFormatter(locale, lytd));
kpiBarContext.put("first_shopped_date", firstShopDate);
kpiBarContext.put("last_shopped_date", lastShopDate);

String isEnableCustomerSummary = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUST_SUMMARY_VIEW");
context.put("isCustomerSummaryView", isEnableCustomerSummary);

println ("kpiBarContext--->"+kpiBarContext);
context.put("inputContext", inputContext);

GenericValue custUserLogin = EntityQuery.use(delegator).from("UserLogin").where("partyId", partyId).queryFirst();
if (UtilValidate.isNotEmpty(custUserLogin)) {
    String isEnabled = custUserLogin.getString("enabled");
    context.put("isUserEnabled", UtilValidate.isNotEmpty(isEnabled) ? isEnabled : "Y");
    context.put("isUserLoginExists", "Y");
} else {
    context.put("isUserLoginExists", "N");
}

String partyIdentification = org.fio.homeapps.util.DataUtil.getPartyIdentificationValue(delegator, partyId, "LOYALTY_ID");
if (UtilValidate.isNotEmpty(partyIdentification)) {
	inputContext.put("loyaltyId", partyIdentification);
}
if (UtilValidate.isEmpty(inputContext.get("loyaltyId"))) {
	inputContext.put("loyaltyId", "N/A");
}
//to display special occasion
String segmentGroupValue = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SPECIAL_OCCASION_SEGMENTS");
if(UtilValidate.isNotEmpty(partyId)&& UtilValidate.isNotEmpty(segmentGroupValue)) {
	List<String> groupList = new ArrayList<>();
	if (segmentGroupValue.contains(",")) {
		groupList = org.fio.admin.portal.util.DataUtil.stringToList(segmentGroupValue, ",");
	} else if (UtilValidate.isNotEmpty(segmentGroupValue)) {
		groupList.add(segmentGroupValue);
	}
	EntityCondition segmentMainCondition = EntityCondition.makeCondition(EntityOperator.AND,EntityCondition.makeCondition(EntityOperator.OR,EntityCondition.makeCondition("groupId",EntityOperator.IN,groupList)),EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
	List<GenericValue> getCustomFieldList = EntityQuery.use(delegator).select("customFieldId").from("CustomFieldPartyClassification").where(segmentMainCondition).queryList();
	if(UtilValidate.isNotEmpty(getCustomFieldList)) {
		List<String> groupIdLists = EntityUtil.getFieldListFromEntityList(getCustomFieldList, "customFieldId", false);
		List<GenericValue> getCustomFieldNameList = EntityQuery.use(delegator).select("customFieldName").from("CustomField").where(EntityCondition.makeCondition(EntityOperator.OR,EntityCondition.makeCondition("customFieldId",EntityOperator.IN,groupIdLists))).queryList();
		List<String> groupNameLists = new ArrayList<>();
		if(UtilValidate.isNotEmpty(getCustomFieldNameList)) {
			groupNameLists = EntityUtil.getFieldListFromEntityList(getCustomFieldNameList, "customFieldName", false);
		}
        if (UtilValidate.isNotEmpty(getCustomFieldNameList)) {
			inputContext.put("specialOccasion",  StringUtil.join(groupNameLists, ", "))
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
	coupons =  EntityQuery.use(delegator).select("productPromoCodeId").from("ProductPromoCodeParty").where("partyId",  partyId).queryList();
	productPromoCodeId = EntityUtil.getFieldListFromEntityList(coupons, "productPromoCodeId", true);
	List<EntityExpr> availableStatusCondition = UtilMisc.toList(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN,UtilDateTime.nowTimestamp()));
	availableStatusCondition.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	EntityCondition scannedDateCondition = EntityCondition.makeCondition("scannedDate", EntityOperator.EQUALS, null);
	EntityCondition availableStatusMainCondition = EntityCondition.makeCondition(availableStatusCondition, EntityOperator.OR);
	EntityCondition couponCountCondition = EntityCondition.makeCondition("productPromoCodeId",EntityOperator.IN,productPromoCodeId);
	EntityCondition couponCountMainCondition = EntityCondition.makeCondition(UtilMisc.toList(couponCountCondition,availableStatusMainCondition,scannedDateCondition), EntityOperator.AND);
	thruDateCount = delegator.findCountByCondition("ProductPromoCode", couponCountMainCondition ,null,null);
}
String partyNoteId = "";
GenericValue partyNote = EntityUtil.getFirst(delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", partyId), UtilMisc.toList("noteId DESC"), false));
if (UtilValidate.isNotEmpty(partyNote)) {
    partyNoteId = partyNote.getString("noteId");
}
context.put("partyNoteId", partyNoteId);

callStatusCond = EntityCondition.makeCondition(EntityOperator.AND,
		EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"CALL_STATUS"),
		EntityCondition.makeCondition(EntityOperator.OR,
		EntityCondition.makeCondition("isEnabled",EntityOperator.EQUALS,"Y"),
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

if (UtilValidate.isNotEmpty(defalutMktCampaignId) && UtilValidate.isEmpty(marketingCampaignId) && UtilValidate.isEmpty(contactListId)) {
	GenericValue defaultMktCampaignContactList = EntityUtil.getFirst(delegator.findByAnd("MarketingCampaignContactList", UtilMisc.toMap("marketingCampaignId", defalutMktCampaignId), UtilMisc.toList("lastUpdatedStamp DESC"), false));
	if (UtilValidate.isNotEmpty(defaultMktCampaignContactList)) {
		defaultCampaignListId = defaultMktCampaignContactList.getString("campaignListId");
		marketingCampaignId = defaultMktCampaignContactList.getString("marketingCampaignId");
		contactListId = defaultMktCampaignContactList.getString("contactListId");
	}
}
GenericValue campaign = EntityQuery.use(delegator).select("campaignName").from("MarketingCampaign").where("marketingCampaignId", marketingCampaignId).queryOne();
if (UtilValidate.isNotEmpty(campaign)) {
	marketingCampaignIdName = org.groupfio.common.portal.util.DataUtil.combineValueKey(campaign.getString("campaignName"), marketingCampaignId);
}
inputContext.put("marketingCampaignIdName", marketingCampaignIdName);
context.put("marketingCampaignId",marketingCampaignId);
context.put("contactListId", contactListId);
EntityCondition mktCondition = EntityCondition.makeCondition(EntityOperator.AND,
		EntityCondition.makeCondition("marketingCampaignId", EntityOperator.EQUALS, marketingCampaignId),
		EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "MKTG_CAMP_PUBLISHED"),
		EntityCondition.makeCondition("campaignTypeId", EntityOperator.EQUALS, "PHONE_CALL"),
		EntityCondition.makeCondition(EntityCondition.makeConditionDate("startDate", "endDate")));
	
		marketingCampaignList = EntityQuery.use(delegator).from("MarketingCampaign").where(mktCondition).queryFirst();
		
		if(UtilValidate.isNotEmpty(marketingCampaignList)) {
			context.put("marketingCampaignList",marketingCampaignList);
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

Calendar calcCal = Calendar.getInstance();
calcCal.setTimeInMillis(System.currentTimeMillis());
calcCal.add(Calendar.YEAR, -1);
Timestamp last12Months = new Timestamp(calcCal.getTimeInMillis());
SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
Timestamp now = UtilDateTime.nowTimestamp();
String lastYearDate = simpleDateFormat.format(last12Months);

Timestamp lastYearDateTs = UtilDateTime.stringToTimeStamp(lastYearDate, "yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
lastYearDateTs = org.ofbiz.base.util.UtilDateTime.getDayStart(lastYearDateTs);

EntityFindOptions findOptns = new EntityFindOptions();
findOptns.setDistinct(true);
List conditionList = FastList.newInstance();
conditionList.add(EntityCondition.makeCondition("billToPartyId", EntityOperator.EQUALS, partyId));
conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(lastYearDateTs, now)));
List orderCount = delegator.findList("RmsTransactionMaster", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("orderId"), UtilMisc.toList("orderDate DESC"), findOptns, false);
String last12MonthOrderCount = orderCount.size();

context.put("last12MonthOrderCount", last12MonthOrderCount);
inputContext.put("last12MonthOrderCount", UtilValidate.isNotEmpty(last12MonthOrderCount) ? last12MonthOrderCount : "N/A");


//CurrentCampaignsDisplay
listOfCurrentCamp = FastList.newInstance();
duplicateCampaigns = FastList.newInstance();
EntityFindOptions findOptnsNew = new EntityFindOptions();
findOptnsNew.setDistinct(true);
EntityCondition conditionListCamp = EntityCondition.makeCondition(
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));

List currentCampMaster = delegator.findList("CallRecordMaster", conditionListCamp, null, UtilMisc.toList("lastUpdatedStamp DESC"), findOptnsNew, false);

if(UtilValidate.isNotEmpty(currentCampMaster)){
	for(GenericValue currentCampaigns : currentCampMaster){
		//context.put("callRecordMaster", currentCampaigns);	
		Map mktCurrentCampaigns = new HashMap();
		String callFinished = currentCampaigns.getString("callFinished");
		String currentMktCamp = currentCampaigns.getString("marketingCampaignId");

		if(UtilValidate.isEmpty(duplicateCampaigns) || !duplicateCampaigns.contains(currentMktCamp)){
			duplicateCampaigns.add(currentMktCamp);

			EntityCondition searchconditionMKT = EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("endDate",EntityOperator.GREATER_THAN,UtilDateTime.nowTimestamp()),
					EntityCondition.makeCondition("endDate",EntityOperator.EQUALS,null));
			List < EntityCondition > campaignList = new ArrayList < EntityCondition > ();
			campaignList.add(EntityCondition.makeCondition("campaignTypeId", EntityOperator.EQUALS, "PHONE_CALL"));
			campaignList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "MKTG_CAMP_PUBLISHED"));
			campaignList.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.EQUALS, currentMktCamp));
			campaignList.add(EntityCondition.makeConditionDate("fromDate", "thruDate"));
			campaignList.add(EntityCondition.makeCondition(EntityOperator.AND,searchconditionMKT));
			if(UtilValidate.isNotEmpty(defalutMktCampaignId)) {
				campaignList.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.NOT_EQUAL, defalutMktCampaignId));
			}
			EntityCondition campaignCondition = EntityCondition.makeCondition(campaignList, EntityOperator.AND);
				
			mktCurrentCampaignsLi = delegator.findList("MarketingCampaign", campaignCondition, null, null, findOptns, false);
			if (mktCurrentCampaignsLi != null && mktCurrentCampaignsLi.size() > 0) {
				for(GenericValue cuttentLi : mktCurrentCampaignsLi){
					String mcampaignId=cuttentLi.getString("marketingCampaignId");
                    GenericValue marketinCampaignContactList = EntityQuery.use(delegator).from("MarketingCampaignContactList").where("marketingCampaignId", mcampaignId, "contactPurposeType", "LIVE").orderBy("fromDate DESC").cache(false).queryFirst();
					if(UtilValidate.isNotEmpty(marketinCampaignContactList)) {
						campListId=marketinCampaignContactList.getString("campaignListId");
						mktCurrentCampaigns.put("campListId",campListId);
                        mktCurrentCampaigns.put("contactListId", marketinCampaignContactList.getString("contactListId"));
					}
					mktCurrentCampaigns.put("marketingCampaignId",cuttentLi.getString("marketingCampaignId"));
					mktCurrentCampaigns.put("campaignName",cuttentLi.getString("campaignName"));
					mktCurrentCampaigns.put("callFinished",callFinished);
					listOfCurrentCamp.add(mktCurrentCampaigns)
				}
			}
		}
	}
}
context.put("CurrentCampaignList",listOfCurrentCamp);
nowTimestamp = org.ofbiz.base.util.UtilDateTime.nowTimestamp();
context.put("nowTimestamp", nowTimestamp);

frequencyValue = "";
recencyValue = "";
spedingValue = "";
String frequencyGroupId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FREQUENCY_GROUP_ID", "RFS_FREQUENCY");
String recencyGroupId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "RECENCY_GROUP_ID", "RFS_RECENCY");
String spendRangeGroupId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SPEND_RANGE_GROUP_ID", "RFS_SPEND_RANGE");
Map < String, Object > frequencyMap = DataHelper.getPartySegmentByGroup(delegator, frequencyGroupId, partyId);
if (UtilValidate.isNotEmpty(frequencyMap)) {
    frequencyValue = frequencyMap.get("sequenceNumber");
}
Map < String, Object > recencyMap = DataHelper.getPartySegmentByGroup(delegator, recencyGroupId, partyId);
if (UtilValidate.isNotEmpty(recencyMap)) {
    recencyValue = recencyMap.get("sequenceNumber");
}
Map < String, Object > spendRangeMap = DataHelper.getPartySegmentByGroup(delegator, spendRangeGroupId, partyId);
if (UtilValidate.isNotEmpty(spendRangeMap)) {
    spedingValue = spendRangeMap.get("sequenceNumber");
}

kpiBarContext.put("recencyValue", UtilValidate.isNotEmpty(recencyValue) ? recencyValue : "");
kpiBarContext.put("frequencyValue", UtilValidate.isNotEmpty(frequencyValue) ? frequencyValue : "");
kpiBarContext.put("spedingValue", UtilValidate.isNotEmpty(spedingValue) ? spedingValue : "");
kpiBarContext.put("campaign", UtilValidate.isNotEmpty(marketingCampaignIdName) ? marketingCampaignIdName : "");
context.put("kpiBarContext", kpiBarContext);
userLoginIdSession = request.getAttribute("userLogin");
if (userLoginIdSession != null) {
    userLoginIdSession = userLoginIdSession.getString("userLoginId");
}
roleCondition = [
    EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["CUST_SERVICE_REP", "ACCOUNT_MANAGER"]),
    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, [partyId])
];
cond = EntityCondition.makeCondition(roleCondition);
roleConditionCheck = delegator.findList("PartyRole", cond, null, null, null, true);
condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginIdSession),
    EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "FULLADMIN"),
    EntityUtil.getFilterByDateExpr()), EntityOperator.AND);
userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup").where(condition).queryList();
if (UtilValidate.isNotEmpty(userLoginSecurityGroup)) {
    context.put("enableCSRReassignButton", "Y");
}
if (userLoginSecurityGroup != null && userLoginSecurityGroup.size() > 0 && UtilValidate.isNotEmpty(roleConditionCheck)) {
    context.put("enableCSRReassignButton", "Y");
}
context.put("campaignListId", campaignListId);
findOptns.setMaxRows(3);
findOptns.setDistinct(true);
//ProductPromoCodeParty
List < Map < String, Object >> openCouponAssigned = new LinkedList < Map < String, Object >> ();
List couponCond = UtilMisc.toList(
    EntityCondition.makeCondition("productPromoCodeStatusId", EntityOperator.EQUALS, "CREATED"),
    EntityCondition.makeCondition("productPromoCodePartyId", EntityOperator.EQUALS, partyId)
);
EntityCondition thruDateCondition = EntityCondition.makeCondition(EntityOperator.OR,
    EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
    EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp()),
    EntityCondition.makeCondition("thruDate", EntityOperator.BETWEEN, UtilMisc.toList(
        new Timestamp(System.currentTimeMillis()),
        new Timestamp(System.currentTimeMillis() + 86399000))));
couponCond.add(thruDateCondition);
EntityCondition checkCouponCondition = EntityCondition.makeCondition(couponCond, EntityOperator.AND);
List openCouponList = delegator.findList("ProductPromoCodePartyView", checkCouponCondition, null, UtilMisc.toList("thruDate ASC"), findOptns, false);
if (UtilValidate.isNotEmpty(openCouponList)) {
    for (GenericValue openCoupon: openCouponList) {
        Map openCoupons = new HashMap();
        String coupon = openCoupon.getString("productPromoCodeId");
        String description = openCoupon.getString("description");
        String thruDate = openCoupon.getString("thruDate");
        if (UtilValidate.isNotEmpty(thruDate)) {
            Date expireDate = new SimpleDateFormat("yyyy-MM-dd").parse(thruDate);
            SimpleDateFormat formatter1 = new SimpleDateFormat("MM/dd/yy");
            thruDate = formatter1.format(expireDate);
            openCoupons.put("thruDate", thruDate);
        }
        openCoupons.put("coupon", org.groupfio.common.portal.util.DataUtil.combineValueKey(coupon, description));
        if (openCouponAssigned.size() < 3)
            openCouponAssigned.add(openCoupons);
    }
}
if (UtilValidate.isNotEmpty(isPhoneCampaignEnabled) && isPhoneCampaignEnabled.equals("Y")) {
        context.put("openCouponAssigned", openCouponAssigned);
}

context.put("activeMessType", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACTIVE_MESS_TYPE"));
context.put("activeRcMessAPI", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "RC_MSG_API", "MVP"));
List < String > roles = new ArrayList < > ();
String globalConfig = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUST_OWNER");
if (UtilValidate.isNotEmpty(globalConfig) && globalConfig.contains(",")) {
	roles = org.fio.admin.portal.util.DataUtil.stringToList(globalConfig, ",");
} else if (UtilValidate.isNotEmpty(globalConfig)) {
	roles.add(globalConfig);
}
List < GenericValue > reassignOwners = EntityQuery.use(delegator).select("roleTypeId", "description").from("RoleType").where(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roles)).queryList();
context.put("reassignOwnerList", UtilValidate.isNotEmpty(reassignOwners) ? org.fio.admin.portal.util.DataUtil.getMapFromGeneric(reassignOwners, "roleTypeId", "description", false) : new HashMap < String, Object > ());
context.put("isEnableDashboardButton",org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_VIEW_DASHBOARD_BTN_ENABLED", "Y"));
