import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.groupfio.custom.field.util.DataHelper;
import org.ofbiz.entity.condition.EntityCondition;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

customFieldGroup = new HashMap();

customFieldGroup.put("serviceTypeId", "INTERNAL");
customFieldGroup.put("valueCapture", "SINGLE");

context.put("customFieldGroup", customFieldGroup);

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

groupingCodeList = delegator.findList("CustomFieldGroupingCode", EntityCondition.makeCondition(UtilMisc.toMap("groupType", GroupType.ECONOMIC_METRIC)), null, ["sequenceNumber"], null, false);
context.put("groupingCodeList", DataHelper.getDropDownOptions(groupingCodeList, "customFieldGroupingCodeId", "groupingCode"));

roleConfig = new HashMap();
context.put("roleConfig", roleConfig);
