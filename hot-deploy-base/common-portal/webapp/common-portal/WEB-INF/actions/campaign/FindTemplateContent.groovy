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
context.put("loggedUserPartyId", loggedUserPartyId);

// found domainEntityType from context
println('domainEntityType from FindTemplateContent..'+context.get('domainEntityType'));

templateList = context.get('templateList');
if (UtilValidate.isEmpty(templateList)) {
	conditionsList = [];
	conditionsList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
	conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
	mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
	templateList = delegator.findList("TemplateContentDomainAssoc", mainConditons, null, UtilMisc.toList("sequenceId DESC"), null, false);
	context.put("templateList", org.fio.homeapps.util.DataHelper.getDropDownOptions(templateList, "templateId", "templateName"));
}
