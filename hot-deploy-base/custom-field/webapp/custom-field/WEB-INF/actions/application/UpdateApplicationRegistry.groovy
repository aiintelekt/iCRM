import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

applicationRegistry = new HashMap();

clientRegistryId = request.getParameter("clientRegistryId");
if (UtilValidate.isNotEmpty(clientRegistryId)) {
	applicationRegistry = EntityUtil.getFirst( delegator.findByAnd("ClientApplicationRegistry", UtilMisc.toMap("clientRegistryId", clientRegistryId), null, false) );
}
context.put("clientRegistryId", clientRegistryId);

context.put("applicationRegistry", applicationRegistry);

appStatus = new HashMap();
context.put("appStatus", appStatus);

appStatusList = UtilMisc.toMap("ACTIVATED", uiLabelMap.get("activated"), "DEACTIVATED", uiLabelMap.get("deActivated"));
context.put("appStatusList", appStatusList);