import java.sql.Timestamp
import java.text.SimpleDateFormat

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.Debug;

import javolution.util.FastList;
import java.text.DecimalFormat;

import org.fio.homeapps.util.PartyHelper;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("AdminPortalUiLabels", locale);

loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserId", userLogin.getString("userLoginId"));

String isView = context.get("isView");

context.put("haveDataPermission", "Y");
	
inputContext = new LinkedHashMap<String, Object>();

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

println("activeTab>>> "+activeTab);

yesNoMap = ["Y":"Yes", "N":"No"];
context.put("yesNoOptions", yesNoMap);

entryId = request.getParameter("entryId");

// account summary data
availablitySummary = from("ResourceAvailability").where("entryId", entryId).queryOne();

println("availablitySummary------------>"+availablitySummary);

if(UtilValidate.isNotEmpty(availablitySummary)){
	context.put("availablitySummary", availablitySummary);
	
	inputContext.putAll(availablitySummary.getAllFields());
	
	//userLogin = select("partyId").from("UserLogin").where("userLoginId", availablitySummary.getString("primOwnerId")).queryFirst();
	//inputContext.put("owner", org.fio.homeapps.util.PartyHelper.getPartyName(delegator, userLogin.get("partyId"), false));
	
	if(UtilValidate.isNotEmpty(isView) && "Y".equals(isView)){
		inputContext.put("partyId", org.fio.homeapps.util.PartyHelper.getPartyName(delegator, availablitySummary.getString("partyId"), false))
		inputContext.put("fromDate", UtilValidate.isNotEmpty(availablitySummary.get("fromDate")) ? UtilDateTime.timeStampToString(availablitySummary.getTimestamp("fromDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		inputContext.put("thruDate", UtilValidate.isNotEmpty(availablitySummary.get("thruDate")) ? UtilDateTime.timeStampToString(availablitySummary.getTimestamp("thruDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	}	
	
	// fillup administration info
	inputContext.put("createdOn", UtilValidate.isNotEmpty(availablitySummary.get("createdStamp")) ? UtilDateTime.timeStampToString(availablitySummary.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	inputContext.put("modifiedOn", UtilValidate.isNotEmpty(availablitySummary.get("lastUpdatedStamp")) ? UtilDateTime.timeStampToString(availablitySummary.getTimestamp("lastUpdatedStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	inputContext.put("createdBy", availablitySummary.get("createdByUserLogin"));
	inputContext.put("modifiedBy", availablitySummary.get("modifiedByUserLogin"));
	
}

String resAvailRoles = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "RES_AVAIL_ROLES", "ACT_OWNER");
context.put("resAvailRoles", resAvailRoles);

println("inputContext> "+inputContext);
context.put("inputContext", inputContext);

context.put("domainEntityId", entryId);
context.put("domainEntityType", "RES_AVAIL");
context.put("requestURI", "viewResAvail");
