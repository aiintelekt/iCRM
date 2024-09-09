import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

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

appStatusList = UtilMisc.toMap("ACTIVATED", uiLabelMap.get("activated"), "DEACTIVATED", uiLabelMap.get("deActivated"));
context.put("appStatusList", appStatusList);

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);