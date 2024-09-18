import org.ofbiz.base.util.*;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("homeappsUiLabels", locale);

security = request.getAttribute("security");
println("DBS_ADMPR_MAKER> "+security.hasPermission("DBS_ADMPR_MAKER", userLogin));
println("DBS_ADMPR_CHEKER> "+security.hasPermission("DBS_ADMPR_CHEKER", userLogin));
context.put("isMaker", security.hasPermission("DBS_ADMPR_MAKER", userLogin));
context.put("isChecker", security.hasPermission("DBS_ADMPR_CHEKER", userLogin));
