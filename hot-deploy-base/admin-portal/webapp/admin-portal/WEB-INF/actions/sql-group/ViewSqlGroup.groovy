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

String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

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

sqlGroupId = request.getParameter("sqlGroupId");

// account summary data
sqlGroupSummary = from("SqlGroup").where("sqlGroupId", sqlGroupId).queryOne();

println("sqlGroupSummary------------>"+sqlGroupSummary);

if(UtilValidate.isNotEmpty(sqlGroupSummary)){
	context.put("sqlGroupSummary", sqlGroupSummary);
	
	inputContext.putAll(sqlGroupSummary.getAllFields());
	
	if(UtilValidate.isNotEmpty(isView) && "Y".equals(isView)){
		//inputContext.put("estimatedStartDate", UtilValidate.isNotEmpty(sqlGroupSummary.get("estimatedStartDate")) ? UtilDateTime.timeStampToString(sqlGroupSummary.getTimestamp("estimatedStartDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
	}
	
	// fillup administration info
	inputContext.put("createdOn", UtilValidate.isNotEmpty(sqlGroupSummary.get("createdTxStamp")) ? UtilDateTime.timeStampToString(sqlGroupSummary.getTimestamp("createdTxStamp"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
	inputContext.put("modifiedOn", UtilValidate.isNotEmpty(sqlGroupSummary.get("lastUpdatedTxStamp")) ? UtilDateTime.timeStampToString(sqlGroupSummary.getTimestamp("lastUpdatedTxStamp"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
	inputContext.put("createdBy", sqlGroupSummary.get("createdByUserLogin"));
	inputContext.put("modifiedBy", sqlGroupSummary.get("lastModifiedByUserLogin"));
}

println("inputContext> "+inputContext);
context.put("inputContext", inputContext);

context.put("domainEntityId", sqlGroupId);
context.put("domainEntityType", "SQL_GRP");
context.put("requestURI", "viewSqlGroup");
