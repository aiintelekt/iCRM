import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;

import org.fio.homeapps.util.PartyHelper;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("AdminPortalUiLabels", locale);
/*
versions = new LinkedHashMap<String, Object>();

versions.put("1.0", "1.0");
versions.put("2.0", "2.0");

context.put("versions", versions);
*/

loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserPartyId", loggedUserPartyId);
context.put("loggedUserId", userLogin.getString("userLoginId"));

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "RESAVAIL_REASON"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
reasonList = delegator.findList("Enumeration", mainConditons, null, null, null, false);
context.put("reasonList", org.fio.homeapps.util.DataHelper.getDropDownOptions(reasonList, "enumId", "description"));
