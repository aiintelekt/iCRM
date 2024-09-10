import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);

String partyId = request.getParameter("partyId");
loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserId", userLogin.getString("userLoginId"));

attachmentTypes = UtilMisc.toMap("PUBLIC", 'Public', "PRIVATE", 'Private');
context.put("attachmentTypes", attachmentTypes);

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "THIRDPTY_CONTENT_CLASS"));
conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
contentClassificationList = delegator.findList("Enumeration", mainConditons, null, UtilMisc.toList("sequenceId ASC"), null, false);
context.put("contentClassificationList", org.fio.homeapps.util.DataHelper.getDropDownOptions(contentClassificationList, "enumId", "description"));

String globalFileSize = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ATTACHMENT_SIZE");
context.put('globalFileSize', globalFileSize);
