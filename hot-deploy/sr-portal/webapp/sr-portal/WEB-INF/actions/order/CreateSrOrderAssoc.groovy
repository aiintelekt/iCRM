import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.fio.admin.portal.util.DataUtil
import org.fio.homeapps.util.PartyHelper;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("SrPortalUiLabels", locale);

String srNumber = request.getParameter("srNumber");
String orderId =  request.getParameter("orderId");
context.put("srNumber", srNumber);

println ("orderId----------->"+orderId);
inputContext = new LinkedHashMap<String, Object>();

loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserId", userLogin.getString("userLoginId"));

inputContext.put("srNumber", srNumber);
inputContext.put("srNumber_link", org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom(srNumber, "SERVICE_REQUEST", externalLoginKey));

GenericValue custRequest = from("CustRequest").where("custRequestId", srNumber).queryOne();
srStatusId = custRequest.statusId;

context.put("srStatusId", srStatusId);
context.put("srTypeId", UtilValidate.isNotEmpty(custRequest) ? custRequest.getString("custRequestTypeId"): "");

inputContext.put("orderPartyId", custRequest.getString("fromPartyId"));
inputContext.put("orderPartyId_desc", PartyHelper.getPartyName(delegator, custRequest.getString("fromPartyId"), false));

inputContext.put("custRequestName", custRequest.getString("custRequestName"));

context.put("inputContext", inputContext);

context.put("domainEntityId", srNumber);
context.put("domainEntityType", "SERVICE_REQUEST");

GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("groupId","SERVICE_GROUP","customFieldName","Justification for FSR on Old Product").queryFirst();
if(UtilValidate.isNotEmpty(customField)) {
	String customFieldId = customField.getString("customFieldId");
	
	List<GenericValue> customFieldMultiValues = EntityQuery.use(delegator).select("fieldValue","description").from("CustomFieldMultiValue").where("customFieldId", customFieldId, "hide", "N").orderBy("sequenceNumber").queryList();
	List<Map<String, Object>> justificationList = new LinkedList<Map<String, Object>>();
	for(GenericValue customFieldMultiValue : customFieldMultiValues) {
		justificationList.addAll(DataUtil.convertGenericValueToMap(delegator, customFieldMultiValue));
	}
	context.put("justificationList",justificationList);
	
	GenericValue custRequestAttr = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", srNumber, "attrName", customFieldId).queryFirst();
	context.put("justificationOldProd", UtilValidate.isNotEmpty(custRequestAttr) ? custRequestAttr.getString("attrValue") : "");
}

String isEnableSrorderSync = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_SRORDER_SYNC");
if(UtilValidate.isNotEmpty(isEnableSrorderSync)){
	context.put("isEnableSrorderSync", isEnableSrorderSync);
}

String locationCustomFieldId = org.ofbiz.entity.util.EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);

List<GenericValue> locationList = EntityQuery.use(delegator).select("fieldValue","description").from("CustomFieldMultiValue").where("customFieldId", locationCustomFieldId, "hide", "N").queryList();
context.put("locationList", org.fio.homeapps.util.DataHelper.getDropDownOptions(locationList, "fieldValue", "description"));

defaultLocationId = org.groupfio.common.portal.util.SrUtil.getCustRequestAttrValue(delegator, locationCustomFieldId, srNumber);
context.put("defaultLocationId", defaultLocationId);
