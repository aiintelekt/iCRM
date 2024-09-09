import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

groupId = request.getParameter("groupId");
customFieldGroup = context.get("customFieldGroup");
if (UtilValidate.isEmpty(groupId)) {
	groupId = context.get("segmentCodeId");
	if (UtilValidate.isNotEmpty(groupId) && UtilValidate.isEmpty(customFieldGroup)) {
		customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
	} 
}

context.put("customFieldGroup", UtilValidate.isNotEmpty(customFieldGroup) ? customFieldGroup : new HashMap());
context.put("groupId", groupId);

condition = UtilMisc.toMap("groupType", GroupType.SEGMENTATION);
if (UtilValidate.isNotEmpty(groupId)) {
	condition.put("groupId", groupId);
}

cond = EntityCondition.makeCondition(condition);
println("cond>>>> "+cond);
customFieldList = delegator.findList("CustomFieldSummary", cond, null, ["sequenceNumber"], null, false);
context.put("customFieldList", customFieldList);

