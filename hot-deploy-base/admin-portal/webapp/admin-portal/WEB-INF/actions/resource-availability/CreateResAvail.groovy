import org.ofbiz.base.util.*;

import org.fio.homeapps.util.PartyHelper;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("AdminPortalUiLabels", locale);

inputContext = new LinkedHashMap<String, Object>();

loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserId", userLogin.getString("userLoginId"));

//triplet = from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Task", "active", "Y").queryOne();
//context.put("srTypeId", triplet.get("code"));
context.put("workEffortTypeId", "TASK");

context.put("inputContext", inputContext);

String resAvailRoles = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "RES_AVAIL_ROLES", "ACT_OWNER");
context.put("resAvailRoles", resAvailRoles);

String workStartTime = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_START_TIME");
String workEndTime = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_END_TIME");
context.put("workStartTime", workStartTime);
context.put("workEndTime", workEndTime);

context.put("domainEntityType", "RES_AVAIL");
