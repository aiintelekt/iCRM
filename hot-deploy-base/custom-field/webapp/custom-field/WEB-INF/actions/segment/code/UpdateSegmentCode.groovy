import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.groupfio.custom.field.util.DataHelper;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

customFieldGroup = new HashMap();

groupId = request.getParameter("groupId");
if (UtilValidate.isNotEmpty(groupId)) {
	customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false) );
}
context.put("groupId", groupId);

context.put("customFieldGroup", customFieldGroup);

appStatus = new HashMap();
context.put("appStatus", appStatus);

serviceTypeList = UtilMisc.toMap("INTERNAL", uiLabelMap.get("internal"), "WEBHOOK_PUSH", uiLabelMap.get("webhookPush"));
context.put("serviceTypeList", serviceTypeList);

roleTypeList = delegator.findAll("CustomFieldRoleType", true);
context.put("roleTypeList", DataHelper.getDropDownOptions(roleTypeList, "roleTypeId", "description"));

microServiceList = delegator.findAll("CustomFieldMicroServiceConfig", true);
context.put("microServiceList", DataHelper.getDropDownOptions(microServiceList, "customFieldMicroServiceConfigId", "serviceName"));

webhookList = delegator.findAll("CustomFieldWebhookConfig", true);
context.put("webhookList", DataHelper.getDropDownOptions(webhookList, "customFieldWebhookConfigId", "serviceName"));

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);

valueCaptureList = UtilMisc.toMap("SINGLE", uiLabelMap.get("single"), "MULTIPLE", uiLabelMap.get("multiple"), "RANGE", uiLabelMap.get("range"));
context.put("valueCaptureList", valueCaptureList);

classTypeList = UtilMisc.toMap("SEGMENT", uiLabelMap.get("segment"), "ATTRIBUTE", uiLabelMap.get("attribute"));
context.put("classTypeList", classTypeList);

typeList = UtilMisc.toMap("PRIMARY_AUTO", uiLabelMap.get("primaryAuto"), "PRIMARY_MULTI_AUTO", uiLabelMap.get("primaryMultiAuto"), "PREDEFINED", uiLabelMap.get("predefined"), "PRIMARY_STATIC", uiLabelMap.get("Static Segment Value"));
context.put("typeList", typeList);

groupingCodeList = delegator.findList("CustomFieldGroupingCode", EntityCondition.makeCondition(UtilMisc.toMap("groupType", GroupType.SEGMENTATION)), null, ["sequenceNumber"], null, false);
context.put("groupingCodeList", DataHelper.getDropDownOptions(groupingCodeList, "customFieldGroupingCodeId", "groupingCode"));

roleConfig = new HashMap();
roleConfigId=null;
roleConfigIdList=delegator.findByAnd("CustomFieldRoleConfig", UtilMisc.toMap("groupId", groupId), null, false);
if(roleConfigIdList!=null && roleConfigIdList.size > 0){
   roleConfigId=roleConfigIdList.get(0).getString("roleTypeId");   
}
context.put("roleConfig", roleConfigId);

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CF_VAL_OVER"));
conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
refreshTypeList = delegator.findList("Enumeration", mainConditons, null, UtilMisc.toList("sequenceId ASC"), null, false);
context.put("overrideTypeList", org.fio.homeapps.util.DataHelper.getDropDownOptions(refreshTypeList, "enumId", "description"));
