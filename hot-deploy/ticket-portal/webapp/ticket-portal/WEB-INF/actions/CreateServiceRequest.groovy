import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("TicketPortalUiLabels", locale);

inputContext = new LinkedHashMap<String, Object>();

context.put("domainEntityType", "SERVICE_REQUEST");

context.put("inputContext", inputContext);
String srNumber = request.getParameter("srNumber");
if(UtilValidate.isNotEmpty(srNumber)) {
	List<GenericValue> custRequestAttList = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", srNumber).queryList();
	
	if(UtilValidate.isNotEmpty(custRequestAttList)){
		for(int i=0 ; i < custRequestAttList.size() ; i++){
			custRequest=custRequestAttList.get(i);
			if("ESTIMATED".equals(custRequest.get("attrName"))){
				inputContext.put("estimated", custRequest.get("attrValue"));
			}else if("CUST_APPROVAL_STATUS".equals(custRequest.get("attrName"))){
				inputContext.put("custApprovalStatus", custRequest.get("attrValue"));
			} else if("BILLED".equals(custRequest.get("attrName"))){
				inputContext.put("billed", custRequest.get("attrValue"));
			}
		}
	}
	
}

context.put("enableCustomCategory",org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_SR_TRIPLET_ENABLED", "Y"));
