import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("homeappsUiLabels", locale);

filterContext = new HashMap();
context.put("filterContext", filterContext);

operatorTypes = UtilMisc.toMap("M", uiLabelMap.get("isMaker"), "C", uiLabelMap.get("isChecker"));
context.put("operatorTypes", operatorTypes);

userStatusList = UtilMisc.toMap("Y", uiLabelMap.get("Active"), "N", uiLabelMap.get("InActive"));
context.put("userStatusList", userStatusList);