import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;
import org.groupfio.custom.field.util.DataHelper;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

roleTypeList = delegator.findAll("CustomFieldRoleType", true);
context.put("roleTypeList", DataHelper.getDropDownOptions(roleTypeList, "roleTypeId", "description"));

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);

groupList = delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupType", GroupType.CUSTOM_FIELD), null, false);
context.put("groupList", DataHelper.getDropDownOptions(groupList, "groupId", "groupName"));

fieldTypeList = UtilMisc.toMap("SINGLE", uiLabelMap.get("single"), "MULTIPLE", uiLabelMap.get("multiple"));
context.put("fieldTypeList", fieldTypeList);

fieldFormatList = UtilMisc.toMap("TEXT", uiLabelMap.get("text"), "DATE", uiLabelMap.get("date"), "NUMERIC", uiLabelMap.get("numeric"), "BOOLEAN", uiLabelMap.get("boolean"), "TEXT_AREA", uiLabelMap.get("textArea"), "CHECK_BOX", uiLabelMap.get("checkBox"), "DROP_DOWN", uiLabelMap.get("dropDown"), "RADIO", uiLabelMap.get("radio"), "LABEL_TEXT", uiLabelMap.get("labelText"));
context.put("fieldFormatList", fieldFormatList);

fieldLengthList = new HashMap();
1.upto(20, {
   fieldLengthList.put("${it}", "${it}");
})
context.put("fieldLengthList", DataHelper.getFieldLengthOptions());

groupId = request.getParameter("groupId");
roleTypeId = request.getParameter("roleTypeId");
customFieldName = request.getParameter("customFieldName");
customFieldType = request.getParameter("customFieldType");
customFieldFormat = request.getParameter("customFieldFormat");
customFieldLength = request.getParameter("customFieldLength");
hide = request.getParameter("hide");

customField = new HashMap();

customField.put("groupId", groupId);
customField.put("roleTypeId", roleTypeId);
customField.put("customFieldName", customFieldName);
customField.put("customFieldType", customFieldType);
customField.put("customFieldFormat", customFieldFormat);
customField.put("customFieldLength", customFieldLength);
customField.put("hide", hide);

context.put("customField", customField);

condition = UtilMisc.toMap("groupType", GroupType.CUSTOM_FIELD);

if (UtilValidate.isNotEmpty(groupId)) {
	condition.put("groupId", groupId);
}
if (UtilValidate.isNotEmpty(roleTypeId)) {
	condition.put("roleTypeId", roleTypeId);
}
if (UtilValidate.isNotEmpty(customFieldType)) {
	condition.put("customFieldType", customFieldType);
}
if (UtilValidate.isNotEmpty(customFieldFormat)) {
	condition.put("customFieldFormat", customFieldFormat);
}
if (UtilValidate.isNotEmpty(customFieldLength)) {
	condition.put("customFieldLength", Long.parseLong(customFieldLength));
}
if (UtilValidate.isNotEmpty(hide)) {
	condition.put("hide", hide);
}

cond = EntityCondition.makeCondition(condition);

if (UtilValidate.isNotEmpty(customFieldName)) {
	EntityCondition nameCondition = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("customFieldName")), EntityOperator.LIKE, "%"+customFieldName.toUpperCase()+"%");
	cond = EntityCondition.makeCondition([cond,
		nameCondition
	], EntityOperator.AND);
}

customFieldList = delegator.findList("CustomFieldSummary", cond, null, ["sequenceNumber"], null, false);
context.put("customFieldList", customFieldList);