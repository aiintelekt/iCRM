import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.*;
import java.util.*;

import org.fio.homeapps.util.PartyHelper;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("rebate-portalUiLabels", locale);

loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserPartyId", loggedUserPartyId);
context.put("loggedUserId", userLogin.getString("userLoginId"));

String domainEntityId = request.getParameter("domainEntityId");
String domainEntityType = request.getParameter("domainEntityType");
String templateId = request.getParameter("templateId");

context.put("domainEntityId", domainEntityId);
context.put("domainEntityType", domainEntityType);
context.put("backLink", org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom(domainEntityId, domainEntityType, externalLoginKey));
inputContext = new LinkedHashMap<String, Object>();

rebateSummary = from("Agreement").where("agreementId", domainEntityId).queryOne();
inputContext.put("domainEntityName", rebateSummary.getString("description"));

String isContentEditable = "Y";
if (UtilValidate.isNotEmpty(rebateSummary.getString("statusId")) && !rebateSummary.getString("statusId").equals("AGRMS_DRAFT")) {
	isContentEditable = "N";
}
inputContext.put("isContentEditable", isContentEditable);

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
templateList = delegator.findList("TemplateContentDomainAssoc", mainConditons, null, UtilMisc.toList("sequenceId DESC"), null, false);
context.put("templateList", org.fio.homeapps.util.DataHelper.getDropDownOptions(templateList, "templateId", "templateName"));

if (UtilValidate.isEmpty(templateId) && UtilValidate.isNotEmpty(templateList)) {
	templateId = templateList.get(0).templateId;
}
context.put("templateId", templateId);

if (UtilValidate.isNotEmpty(templateId)) {
List<GenericValue> contentList = EntityQuery.use(delegator).from("TemplateContent")
						.where(
								EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType),
								EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId),
								EntityCondition.makeCondition("templateId", EntityOperator.EQUALS, templateId)
								).filterByDate().orderBy("sequenceId").queryList();
context.put("contentList", contentList);
}

is2gAgmt = org.groupfio.rebate.service.util.AgreementUtil.getAgreementAttrValue(delegator, domainEntityId, "IS_2G_AGRMT");
inputContext.put("is2g", is2gAgmt);

context.put("inputContext", inputContext);