import org.fio.homeapps.util.DataUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.*;

uiLabelMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);

String techArrivalWindows = DataUtil.getGlobalValue(delegator, "TECH_ARRIVAL_WINDOWS");
context.put("techArrivalWindows",techArrivalWindows);

inputContext = new LinkedHashMap<String, Object>();
String srNumber = request.getParameter("domainEntityId");
if(UtilValidate.isEmpty(srNumber)) {
	srNumber = request.getParameter("srNumber");
	if(UtilValidate.isNotEmpty(request.getParameter("custRequestId"))){
		srNumber = request.getParameter("custRequestId");
	}
}
if(UtilValidate.isNotEmpty(request.getParameter("domainEntityType"))) {
	//inputContext.put("domainEntityType", request.getParameter("domainEntityType"));
	inputContext.put("domainEntityType1", org.groupfio.common.portal.util.DataHelper.convertToLabel(request.getParameter("domainEntityType")));
}
if(UtilValidate.isNotEmpty(request.getParameter("domainEntityId")))
	inputContext.put("domainEntityId1", request.getParameter("domainEntityId"));
	
GenericValue custRequest = from("CustRequest").where("custRequestId", srNumber).queryOne();
if (UtilValidate.isNotEmpty(custRequest)){
	if(UtilValidate.isNotEmpty(custRequest.get("priority")))
		inputContext.put("priority", custRequest.get("priority"));
	
	partyName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, custRequest.get("fromPartyId"), false);
	partySummaryDetails = from("PartySummaryDetailsView").where("partyId", custRequest.get("fromPartyId")).queryOne();
	inputContext.put("cNo_desc", partyName);
	inputContext.put("partyId_desc", partyName);
	inputContext.put("partyName", partyName);
	inputContext.put("partyId", custRequest.get("fromPartyId"));
	inputContext.put("domainName", custRequest.getString("custRequestName"));
	
}
context.put("inputContext", inputContext);

String workStartTime = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_START_TIME");
String workEndTime = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_END_TIME");
context.put("workStartTime", workStartTime);
context.put("workEndTime", workEndTime);

techPriorityTypeList = new LinkedHashMap<String, Object>();
techPriorityTypeList.put("", uiLabelMap.TechType);
techPriorityTypeList.put("REEB-ASSIGNED", uiLabelMap.ReebAssignedTech);
techPriorityTypeList.put("REEB-RECOMMENDED", uiLabelMap.ReebRecommendedTech);
techPriorityTypeList.put("REEB-OTHER", uiLabelMap.ReebOtherStateTech);
techPriorityTypeList.put("CONTRACTOR", uiLabelMap.ReebContractorTech);
techPriorityTypeList.put("OTHER", uiLabelMap.ReebOtherTech);
context.put("techPriorityTypeList", techPriorityTypeList);

String inspActWorkTypeIds = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "INSP_ACT_WT");
context.put("inspActWorkTypeIds", inspActWorkTypeIds);


String scheduleTaskType = DataUtil.getGlobalValue(delegator, "SCHEDULE_TASK_TYPE", "SCHEDULE_TASK");
context.put("scheduleTaskTypes", scheduleTaskType);

String scheduleTechRole = DataUtil.getGlobalValue(delegator, "SCHEDULE_TECH_ROLE", "");
context.put("thridPartyContractor", scheduleTechRole);
