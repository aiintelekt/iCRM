import org.fio.admin.portal.util.DataUtil
import org.fio.homeapps.util.DataHelper
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("SrPortalUiLabels", locale);

String srNumber = request.getParameter("srNumber");

context.put("domainEntityId", srNumber);
context.put("domainEntityType", "SERVICE_REQUEST");
// For client portal , create is not required
context.put("isAllowCreate","Y");
if(request.getRequestURI().contains("client-portal")){
	context.put("isAllowCreate","N");
}

List<GenericValue> sticketStatusList = EntityQuery.use(delegator).from("Enumeration").where("enumTypeId", "STKT_STATUS_ID").orderBy("sequenceId").queryList();
if(UtilValidate.isNotEmpty(sticketStatusList)) {
	Map<String, Object> inspectStatusMap = new LinkedHashMap<String, Object>();
	inspectStatusMap.put("", "");
	inspectStatusMap.putAll(DataHelper.getDropDownOptions(sticketStatusList, "enumId", "description"));
	
	context.put("inspectStatusList",DataUtil.convertToJson(inspectStatusMap));
}