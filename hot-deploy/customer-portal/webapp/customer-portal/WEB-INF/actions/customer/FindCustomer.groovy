import org.ofbiz.base.util.*;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("customer-portalUiLabels", locale);

exportTypeList = new LinkedHashMap<String, Object>();

exportTypeList.put("CSV", "CSV");
exportTypeList.put("EXCEL", "Excel");

context.put("exportTypeList", exportTypeList);

exportFileTypes = new LinkedHashMap<String, Object>();

exportFileTypes.put("IMPORT_FILE", "Import file");
exportFileTypes.put("ERROR_FILE", "Error file");

context.put("exportFileTypes", exportFileTypes);

String isPhoneCampaignEnabled=org.fio.homeapps.util.DataUtil.isPhoneCampaignEnabled(delegator);
context.put("isPhoneCampaignEnabled", isPhoneCampaignEnabled);
