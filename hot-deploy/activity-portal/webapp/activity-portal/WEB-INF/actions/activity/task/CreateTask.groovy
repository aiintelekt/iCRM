import org.ofbiz.base.util.*;

import org.fio.homeapps.util.PartyHelper;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("activity-portalUiLabels", locale);

inputContext = new LinkedHashMap<String, Object>();

loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserId", userLogin.getString("userLoginId"));

//triplet = from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Task", "active", "Y").queryOne();
//context.put("srTypeId", triplet.get("code"));
context.put("workEffortTypeId", "TASK");

context.put("inputContext", inputContext);

context.put("domainEntityType", "ACTIVITY");
