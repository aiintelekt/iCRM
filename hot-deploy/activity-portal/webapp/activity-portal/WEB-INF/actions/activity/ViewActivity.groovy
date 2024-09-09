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
uiLabelMap = UtilProperties.getResourceBundleMap("activity-portalUiLabels", locale);

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

activityId = request.getParameter("workEffortId");

// account summary data
activitySummary = from("WorkEffort").where("workEffortId", activityId).queryOne();

println("activitySummary------------>"+activitySummary);

if(UtilValidate.isNotEmpty(activitySummary)){
	context.put("activitySummary", activitySummary);
	
	inputContext.putAll(activitySummary.getAllFields());
	
	GenericValue getBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", activitySummary.getString("businessUnitId")).queryOne();
	if (UtilValidate.isNotEmpty(getBu)) {
		inputContext.put("businessUnitId",getBu.getString("productStoreGroupName"));
	}
	
	if (UtilValidate.isNotEmpty(activitySummary.getString("workEffortTypeId"))) {
		type = EntityQuery.use(delegator).from("WorkEffortType").where("workEffortTypeId", activitySummary.getString("workEffortTypeId")).queryOne();
		if (UtilValidate.isNotEmpty(type)) {
			inputContext.put("typeDesc", type.getString("description"));
		}
	}
	
	String ownerName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, userLogin.get("partyId"), false);
	userLogin = select("partyId").from("UserLogin").where("userLoginId", activitySummary.getString("primOwnerId")).queryFirst();
	inputContext.put("owner", ownerName);
	
	workEffortSupp = EntityUtil.getFirst(from("WorkEffortSupplementory").where("workEffortId", activityId,).queryList());
	if(UtilValidate.isNotEmpty(workEffortSupp)){
		duration=workEffortSupp.getString("wftMsdduration")?workEffortSupp.getString("wftMsdduration"):"";
		location=workEffortSupp.getString("wftLocation")?workEffortSupp.getString("wftLocation"):"";
		template=workEffortSupp.getString("wftMsdsubjecttemplate")?workEffortSupp.getString("wftMsdsubjecttemplate"):"";
		inputContext.put("emailTemplate",template);
		context.put("template",template);
		inputContext.put("location",location);
		inputContext.put("duration",duration);
	}
		
	attendeesconditions = EntityCondition.makeCondition([
		EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, activityId),
		EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
		EntityCondition.makeCondition("expectationEnumId", EntityOperator.IN, ["WEE_REQUIRE","WEE_REQUEST"]),
		EntityUtil.getFilterByDateExpr()
		],
	EntityOperator.AND);
	WorkEffortPartyAssignmentList = delegator.findList("WorkEffortPartyAssignment", attendeesconditions, null, null, null, false);
	requireAttendeeslist = EntityUtil.filterByCondition(WorkEffortPartyAssignmentList, EntityCondition.makeCondition("expectationEnumId",EntityOperator.EQUALS,"WEE_REQUIRE"));
	optionalAttendeeslist = EntityUtil.filterByCondition(WorkEffortPartyAssignmentList, EntityCondition.makeCondition("expectationEnumId",EntityOperator.EQUALS,"WEE_REQUEST"));

	requiredAttendeeParties = EntityUtil.getFieldListFromEntityList(requireAttendeeslist, "partyId", false);
	optionalAttendeeParties = EntityUtil.getFieldListFromEntityList(optionalAttendeeslist, "partyId", false);
	
	println("requiredAttendeeParties> "+requiredAttendeeParties);
	println("optionalAttendeeParties> "+optionalAttendeeParties);
	
	requiredAttendees = org.ofbiz.base.util.StringUtil.join(requiredAttendeeParties, ",");
	optionalAttendees = org.ofbiz.base.util.StringUtil.join(optionalAttendeeParties, ",");
	
	inputContext.put("requiredAttendees", requiredAttendees);
	inputContext.put("optionalAttendees", optionalAttendees);
	
	requiredAttendeesDesc="";
	optionalAttendeesDesc = "";
	
	if(UtilValidate.isNotEmpty(isView) && "Y".equals(isView)){
		inputContext.put("estimatedStartDate", UtilValidate.isNotEmpty(activitySummary.get("estimatedStartDate")) ? UtilDateTime.timeStampToString(activitySummary.getTimestamp("estimatedStartDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		inputContext.put("estimatedCompletionDate", UtilValidate.isNotEmpty(activitySummary.get("estimatedCompletionDate")) ? UtilDateTime.timeStampToString(activitySummary.getTimestamp("estimatedCompletionDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		inputContext.put("actualStartDate", UtilValidate.isNotEmpty(activitySummary.get("actualStartDate")) ? UtilDateTime.timeStampToString(activitySummary.getTimestamp("actualStartDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		inputContext.put("actualCompletionDate", UtilValidate.isNotEmpty(activitySummary.get("actualCompletionDate")) ? UtilDateTime.timeStampToString(activitySummary.getTimestamp("actualCompletionDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		
		if(UtilValidate.isNotEmpty(requiredAttendeeParties)){
			requiredAttendeeParties.each { eachReqId ->
				reqAttndName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, eachReqId, false);
				requiredAttendeesDesc += reqAttndName+",";
			}
			inputContext.put("requiredAttendees", requiredAttendeesDesc.substring(0, requiredAttendeesDesc.length()-1));
		}
		
		if(UtilValidate.isNotEmpty(optionalAttendeeParties)){
			optionalAttendeeParties.each { eachOptId ->
				optAttndName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, eachOptId, false);
				optionalAttendeesDesc += optAttndName+",";
			}
			inputContext.put("optionalAttendees", optionalAttendeesDesc.substring(0, optionalAttendeesDesc.length()-1));
		}
	}	
	
	// fillup administration info
	inputContext.put("createdOn", UtilValidate.isNotEmpty(activitySummary.get("createdStamp")) ? UtilDateTime.timeStampToString(activitySummary.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	inputContext.put("modifiedOn", UtilValidate.isNotEmpty(activitySummary.get("lastUpdatedStamp")) ? UtilDateTime.timeStampToString(activitySummary.getTimestamp("lastUpdatedStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	inputContext.put("createdBy", activitySummary.get("createdByUserLogin"));
	inputContext.put("modifiedBy", activitySummary.get("lastModifiedByUserLogin"));
	
	if (activitySummary.getString("workEffortTypeId")=="TASK") {
		context.put("instanceId", "ACTIVITY_TASK_BASE");
	} else if (activitySummary.getString("workEffortTypeId")=="APPOINTMENT") {
		context.put("instanceId", "ACTIVITY_APNT_BASE");
	}
}
println("inputContext> "+inputContext);
context.put("inputContext", inputContext);

context.put("domainEntityId", activityId);
context.put("domainEntityType", "ACTIVITY");
context.put("requestURI", "viewActivity");

context.put("actAttrGcode", org.fio.homeapps.util.UtilActivity.getActivityAttrValue(delegator, activityId, "ASSIGN_ATTR_GCODE"));
context.put("isProgAct", org.fio.homeapps.util.UtilActivity.getActivityAttrValue(delegator, activityId, "IS_PROG_ACT"));


