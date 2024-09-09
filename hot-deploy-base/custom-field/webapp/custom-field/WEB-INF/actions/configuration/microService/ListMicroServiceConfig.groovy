import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

microServiceList = delegator.findList("CustomFieldMicroServiceConfig", null, null, ["-createdStamp"], null, false);
context.put("microServiceList", microServiceList);

