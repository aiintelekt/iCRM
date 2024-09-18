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
context.put("groupId", groupId);

condition = UtilMisc.toMap("groupType", GroupType.CUSTOM_FIELD);
if (UtilValidate.isNotEmpty(groupId)) {
	condition.put("groupId", groupId);
}
roleTypeId = request.getParameter("searchRoleTypeId");
if (UtilValidate.isNotEmpty(roleTypeId)) {
	condition.put("roleTypeId", roleTypeId);
}
context.put("searchRoleTypeId", roleTypeId);
println("roleType.........."+ request.getParameter("searchRoleTypeId"));

EntityFindOptions efo = new EntityFindOptions();
efo.setDistinct(true);

cond = EntityCondition.makeCondition(condition);
customFieldList = delegator.findList("CustomFieldSummary", cond, null, ["sequenceNumber"], efo, false);
context.put("customFieldList", customFieldList);

