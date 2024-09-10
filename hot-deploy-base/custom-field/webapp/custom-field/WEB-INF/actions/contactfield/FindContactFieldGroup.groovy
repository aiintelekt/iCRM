import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);

groupId = request.getParameter("groupId");
groupName = request.getParameter("groupName");
hide = request.getParameter("hide");

customFieldGroup = new HashMap();

customFieldGroup.put("groupId", groupId);
customFieldGroup.put("groupName", groupName);
customFieldGroup.put("hide", hide);

context.put("customFieldGroup", customFieldGroup);

condition = UtilMisc.toMap("groupType", GroupType.CONTACT_FIELD);

/*if (UtilValidate.isNotEmpty(groupId)) {
	condition.put("groupId", groupId);
}*/
if (UtilValidate.isNotEmpty(hide)) {
	condition.put("hide", hide);
}

cond = EntityCondition.makeCondition(condition);

if (UtilValidate.isNotEmpty(groupId)) {
	EntityCondition idCondition = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("groupId")), EntityOperator.LIKE, "%"+groupId.toUpperCase()+"%");
	cond = EntityCondition.makeCondition([cond,
		idCondition
	], EntityOperator.AND);
}

if (UtilValidate.isNotEmpty(groupName)) {
	EntityCondition nameCondition = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("groupName")), EntityOperator.LIKE, "%"+groupName.toUpperCase()+"%");
	cond = EntityCondition.makeCondition([cond,
		nameCondition
	], EntityOperator.AND);
}

customFieldGroupList = delegator.findList("CustomFieldGroup", cond, null, ["sequence"], null, false);
context.put("customFieldGroupList", customFieldGroupList);