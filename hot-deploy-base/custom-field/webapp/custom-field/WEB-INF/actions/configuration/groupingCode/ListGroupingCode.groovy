import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

//condition = UtilMisc.toMap("groupType", GroupType.ECONOMIC_METRIC);
condition = UtilMisc.toMap();

groupType = request.getParameter("groupType");

if (UtilValidate.isNotEmpty(groupType)) {
	condition.put("groupType", groupType);
} else if (context.get("groupType")) {
	condition.put("groupType", context.get("groupType"));
}
context.put("groupType", groupType);

cond = EntityCondition.makeCondition(condition);
println("cond>>>> "+cond);
groupingCodeList = delegator.findList("CustomFieldGroupingCode", cond, null, ["-createdStamp"], null, false);
context.put("groupingCodeList", groupingCodeList);

