import java.sql.ResultSet;

import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("activity-portalUiLabels", locale);

userLogin = request.getAttribute("userLogin");

activityTypeList = new LinkedHashMap<String, Object>();

activityTypeList.put("TASK", "Task");
activityTypeList.put("APPOINTMENT", "Appointment");

context.put("activityTypeList", activityTypeList);

exportFileTypes = new LinkedHashMap<String, Object>();

exportFileTypes.put("IMPORT_FILE", "Import file");
exportFileTypes.put("ERROR_FILE", "Error file");

context.put("exportFileTypes", exportFileTypes);

loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserPartyId", loggedUserPartyId);
context.put("loggedUserId", userLogin.getString("userLoginId"));

String defaultActType = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_ACT_TYPE", "TASK");
context.put("defaultActType",defaultActType);


ResultSet rs = null;
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Map<String, Object> dashboardBarContext = new LinkedHashMap<String, Object>();

long openActivities = 0;
long completedActivities = 0;

if(UtilValidate.isNotEmpty(userLogin)) {
	String loginPartyId = userLogin.getString("partyId");
	
	String actTypeCond = " AND (WORK_EFFORT_TYPE_ID='TASK' OR WORK_EFFORT_TYPE_ID='APPOINTMENT')"; 
	
	String openActivitiesSql = "SELECT COUNT(1) as 'openActivities' FROM (SELECT COUNT(we.WORK_EFFORT_ID) FROM work_effort we LEFT OUTER JOIN work_effort_party_assignment wepa ON we.WORK_EFFORT_ID=wepa.WORK_EFFORT_ID WHERE `CURRENT_STATUS_ID`='IA_OPEN' AND (wepa.party_id='"+loggedUserPartyId+"' AND wepa.role_type_id='CAL_OWNER')" + actTypeCond + " GROUP BY we.WORK_EFFORT_ID) TEMP_NAME";
	String completedActivitiesSql = "SELECT COUNT(1) as 'completedActivities' FROM (SELECT COUNT(we.WORK_EFFORT_ID) FROM work_effort we LEFT OUTER JOIN work_effort_party_assignment wepa ON we.WORK_EFFORT_ID=wepa.WORK_EFFORT_ID WHERE `CURRENT_STATUS_ID`='IA_MCOMPLETED' AND (wepa.party_id='"+loggedUserPartyId+"' AND wepa.role_type_id='CAL_OWNER')" + actTypeCond + " GROUP BY we.WORK_EFFORT_ID) TEMP_NAME";
	rs = sqlProcessor.executeQuery(openActivitiesSql);
	if (rs != null) {
		while (rs.next()) {
			openActivities = rs.getLong("openActivities");
		}
	}
	rs = sqlProcessor.executeQuery(completedActivitiesSql);
	if (rs != null) {
		while (rs.next()) {
			completedActivities = rs.getLong("completedActivities");
		}
	}
	
}
dashboardBarContext.put("activity-open", openActivities);
dashboardBarContext.put("activity-completed", completedActivities);
context.put("dashboardBarContext", dashboardBarContext);


List<EntityCondition> conditions = new ArrayList<EntityCondition>();
conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
		EntityCondition.makeCondition("isDemoStore",EntityOperator.EQUALS,"N"),
		EntityCondition.makeCondition("isDemoStore",EntityOperator.EQUALS,null))
		);

//String roleTypeId = DataUtil.getPartySecurityRole(delegator, loggedUserPartyId)
List<GenericValue> partyRolesDet = DataUtil.getPartyRoles(delegator, loggedUserPartyId);
List<String> partyRolesList = new ArrayList<>();
partyRolesList = EntityUtil.getFieldListFromEntityList(partyRolesDet, "roleTypeId", true);
List<String> csrRoles = new ArrayList<>();
String csrRolesList = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CSR_ROLES", "CUST_SERVICE_REP");
if (UtilValidate.isNotEmpty(csrRolesList)) {
	if (UtilValidate.isNotEmpty(csrRolesList) && csrRolesList.contains(",")) {
		csrRoles = org.fio.admin.portal.util.DataUtil.stringToList(csrRolesList, ",");
	} else {
		csrRoles.add(csrRolesList);
	}	
}

boolean isFsr = false;
for (String element : csrRoles) {
	if (partyRolesList.contains(element)) {
		isFsr = true;
		break;
	}
}
if(!isFsr) {
	Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, loggedUserPartyId);
	String businessUnit = (String) buTeamData.get("businessUnit");
	if(UtilValidate.isNotEmpty(businessUnit)) {
		conditions.add(EntityCondition.makeCondition("primaryStoreGroupId", EntityOperator.EQUALS, businessUnit));
		EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		println ("condition----1----->"+condition);
		List<GenericValue> locationList = EntityQuery.use(delegator).select("productStoreId","storeName").from("ProductStore").where(condition).queryList();
		context.put("locationList", UtilValidate.isNotEmpty(locationList) ? locationList : new ArrayList());
	}
	
} else {
	EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	println ("condition----2----->"+condition);
	List<GenericValue> locationList = EntityQuery.use(delegator).select("productStoreId","storeName").from("ProductStore").where(condition).queryList();
	context.put("locationList", UtilValidate.isNotEmpty(locationList) ? locationList : new ArrayList());
}
context.put("isFsr",isFsr);

if(UtilValidate.isNotEmpty(userLogin)) {
	String userLoginPartyId = userLogin.get("partyId");
	context.ownerUserLoginId=userLoginPartyId;
	
	if(UtilValidate.isNotEmpty(userLoginPartyId)) {
		GenericValue backupConfig = EntityQuery.use(delegator).from("PartyAttribute").where("attrName","BACKUP_COORDINATOR","attrValue", userLoginPartyId).queryFirst();
		if(UtilValidate.isNotEmpty(backupConfig)) {
			String realCoordPartyId = backupConfig.getString("partyId");
			String realCoordinator = UtilValidate.isNotEmpty(realCoordPartyId) ? org.fio.homeapps.util.DataUtil.getPartyUserLoginId(delegator, realCoordPartyId) : "";
			context.put("realCoordinator", realCoordinator);
			context.put("realCoordPartyId", realCoordPartyId);
		}
	}
}