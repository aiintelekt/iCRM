import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;

import org.fio.homeapps.util.PartyHelper;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("activity-portalUiLabels", locale);

activityTypeList = new LinkedHashMap<String, Object>();

activityTypeList.put("TASK", "Task");
activityTypeList.put("APPOINTMENT", "Appointment");
activityTypeList.put("EMAIL", "E-Mail");
activityTypeList.put("PHONE", "Phone Call");
//activityTypeList.put("WORK_FLOW", "Approval");

context.put("activityTypeList", activityTypeList);

versions = new LinkedHashMap<String, Object>();

versions.put("1.0", "1.0");
versions.put("2.0", "2.0");

context.put("versions", versions);

loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserPartyId", loggedUserPartyId);
context.put("loggedUserId", userLogin.getString("userLoginId"));

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "IA_STATUS_ID"));
conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
statusList = delegator.findList("StatusItem", mainConditons, null, null, null, false);
context.put("statusList", org.fio.homeapps.util.DataHelper.getDropDownOptions(statusList, "statusId", "description"));

Map<String, Object> domainEntityTypes = new LinkedHashMap<>();
domainEntityTypes.put("SERVICE_REQUEST", "Service Request");
domainEntityTypes.put("CUSTOMER", "Customer");
context.put("domainEntityTypeList",domainEntityTypes);

inputContext = new LinkedHashMap<String, Object>();

context.put("inputContext", inputContext);

context.put("isEnableProgramAct", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_PROG_ACT", "N"));