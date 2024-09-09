import java.text.SimpleDateFormat

import org.fio.homeapps.util.DataUtil
import org.fio.sr.portal.event.AjaxEvents
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil

String workEffortId =  request.getParameter("workEffortId");
Map<String, Object> data = new HashMap<String, Object>();
if(UtilValidate.isNotEmpty(workEffortId)) {

	SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryFirst();
	
	if(UtilValidate.isNotEmpty(workEffort)) {
		data.put("actualStartDate", UtilValidate.isNotEmpty(workEffort.getTimestamp("actualStartDate")) ? df.format(workEffort.getTimestamp("actualStartDate")) : df.format(UtilDateTime.nowTimestamp()));
		data.put("actualCompletionDate", UtilValidate.isNotEmpty(workEffort.getTimestamp("actualCompletionDate")) ? df.format(workEffort.getTimestamp("actualCompletionDate")) : df.format(UtilDateTime.nowTimestamp()));
		
		data.put("activityStatus", workEffort.getString("currentStatusId"));
	}
	List<GenericValue> workEffortPartyAssignment = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where("workEffortId", workEffortId,"roleTypeId","TECHNICIAN").filterByDate().queryList();
	
	List<String> partyIds = UtilValidate.isNotEmpty(workEffortPartyAssignment) ? EntityUtil.getFieldListFromEntityList(workEffortPartyAssignment, "partyId", true) : new ArrayList<String>();
	if(UtilValidate.isNotEmpty(partyIds)) {
		Map<String, Object> technicianList = new HashMap<String, Object>();
		for(String partyId : partyIds) {
			technicianList.put(partyId, DataUtil.getUserLoginName(delegator, partyId));
		}
		data.put("technicianList", technicianList);
	}
}
return AjaxEvents.doJSONResponse(response, data);