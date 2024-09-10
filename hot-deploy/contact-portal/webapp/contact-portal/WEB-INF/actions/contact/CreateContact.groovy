import org.ofbiz.base.util.*;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("contact-portalUiLabels", locale);

inputContext = new LinkedHashMap<String, Object>();

String accountPartyId = request.getParameter("accountPartyId");

context.put("isActUspsAddrVal", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_USPS_ADDRACT", "N"));

String accountPartyName ="";

if(UtilValidate.isNotEmpty(accountPartyId)) {
	accountPartyName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, accountPartyId, false);
	inputContext.put("accountPartyId_desc", accountPartyName);
	inputContext.put("accountPartyId", accountPartyId);
}

context.put("inputContext", inputContext);
