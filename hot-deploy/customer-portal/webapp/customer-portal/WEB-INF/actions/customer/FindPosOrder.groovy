import org.ofbiz.base.util.*;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("contact-portalUiLabels", locale);

inputContext = new LinkedHashMap<String, Object>();

context.put("inputContext", inputContext);
