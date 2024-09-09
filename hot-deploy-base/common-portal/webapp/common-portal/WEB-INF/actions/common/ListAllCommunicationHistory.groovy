import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;

delegator = request.getAttribute("delegator");
String externalLoginKey = request.getParameter("externalLoginKey");
if (externalLoginKey == null) {
	externalLoginKey = request.getAttribute("externalLoginKey");
}
String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

String domainEntityType = request.getParameter("domainEntityType");
String domainEntityId = request.getParameter("domainEntityId");

context.put("domainEntityType", domainEntityType);
context.put("domainEntityId", domainEntityId);

String domainEntityTypeDesc = "FSR";
if (UtilValidate.isNotEmpty(domainEntityType) && !domainEntityType.equals("SERVICE_REQUEST")) {
    domainEntityTypeDesc = org.groupfio.common.portal.util.DataHelper.convertToLabel(domainEntityType);
}
context.put("domainEntityTypeDesc", domainEntityTypeDesc);
context.put("domainEntityLink", org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom(domainEntityId, domainEntityType, externalLoginKey));
context.put("domainEntityName", org.groupfio.common.portal.util.DataHelper.getDomainEntityName(delegator, domainEntityId, domainEntityType));
