import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

clientRegistryId = request.getParameter("clientRegistryId");
if (UtilValidate.isNotEmpty(clientRegistryId)) {
	appStatusList = delegator.findByAnd("ClientApplicationStatus", UtilMisc.toMap("clientRegistryId", clientRegistryId), ["-createdStamp"], false);
	context.put("appStatusList", appStatusList);
}

