import org.ofbiz.base.util.*;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("AdminPortalUiLabels", locale);

inputContext = new LinkedHashMap<String, Object>();

context.put("inputContext", inputContext);
