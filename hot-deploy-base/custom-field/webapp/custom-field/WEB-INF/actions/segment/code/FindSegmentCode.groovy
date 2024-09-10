import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;
import org.groupfio.custom.field.util.DataHelper;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

groupList = delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupType", GroupType.SEGMENTATION), null, false);
context.put("groupList", DataHelper.getDropDownOptions(groupList, "groupId", "groupName"));

groupId = request.getParameter("groupId");
groupName = request.getParameter("groupName");
groupingCode = request.getParameter("groupingCode");
valueCapture = request.getParameter("valueCapture");
isCampaignUse = request.getParameter("isCampaignUse");
type = request.getParameter("type");

println("groupId: "+ groupId + ", groupName: "+groupName+", groupingCode: "+groupingCode+", valueCapture: "+valueCapture+", isCampaignUse: "+isCampaignUse+", type: "+type);

customFieldGroup = new HashMap();

customFieldGroup.put("groupId", groupId);
customFieldGroup.put("groupName", groupName);
customFieldGroup.put("groupingCode", groupingCode);
customFieldGroup.put("valueCapture", valueCapture);
customFieldGroup.put("isCampaignUse", isCampaignUse);
customFieldGroup.put("type", type);

context.put("customFieldGroup", customFieldGroup);

condition = UtilMisc.toMap("groupType", GroupType.SEGMENTATION);

if (UtilValidate.isNotEmpty(groupId)) {
	condition.put("groupId", groupId);
}
if (UtilValidate.isNotEmpty(groupingCode)) {
	cfgc = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroupingCode", UtilMisc.toMap("groupingCode", groupingCode), null, false) );
	if (UtilValidate.isNotEmpty(cfgc)) {
		condition.put("groupingCode", cfgc.getString("customFieldGroupingCodeId"));
	}
}
if (UtilValidate.isNotEmpty(valueCapture)) {
	condition.put("valueCapture", valueCapture);
}
if (UtilValidate.isNotEmpty(isCampaignUse)) {
	condition.put("isCampaignUse", isCampaignUse);
}
if (UtilValidate.isNotEmpty(type)) {
	condition.put("type", type);
}

cond = EntityCondition.makeCondition(condition);

if (UtilValidate.isNotEmpty(groupName)) {
	EntityCondition nameCondition = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("groupName")), EntityOperator.LIKE,"%"+groupName.toUpperCase()+"%");
	cond = EntityCondition.makeCondition([cond,
		nameCondition
	], EntityOperator.AND);
}

customFieldGroupList = delegator.findList("CustomFieldGroup", cond, null, ["sequence"], null, false);
context.put("customFieldGroupList", customFieldGroupList);

groupingCodeList = delegator.findList("CustomFieldGroupingCode", EntityCondition.makeCondition(UtilMisc.toMap("groupType", GroupType.SEGMENTATION)), null, ["sequenceNumber"], null, false);
context.put("groupingCodeList", DataHelper.getDropDownOptions(groupingCodeList, "customFieldGroupingCodeId", "description"));

valueCaptureList = UtilMisc.toMap("SINGLE", uiLabelMap.get("single"), "MULTIPLE", uiLabelMap.get("multiple"), "RANGE", uiLabelMap.get("range"));
context.put("valueCaptureList", valueCaptureList);

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);

typeList = UtilMisc.toMap("PRIMARY_AUTO", uiLabelMap.get("primaryAuto"), "PRIMARY_MULTI_AUTO", uiLabelMap.get("primaryMultiAuto"), "PREDEFINED", uiLabelMap.get("predefined"), "PRIMARY_STATIC", uiLabelMap.get("Static Segment Value"));
context.put("typeList", typeList);