import org.ofbiz.base.util.*;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("account-portalUiLabels", locale);

inputContext = new LinkedHashMap<String, Object>();

context.put("isActUspsAddrVal", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_USPS_ADDRACT", "N"));

context.put("inputContext", inputContext);
