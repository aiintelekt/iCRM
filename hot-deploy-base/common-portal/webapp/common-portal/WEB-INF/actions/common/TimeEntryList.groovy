import org.fio.homeapps.util.DataUtil
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil

String workEffortId = request.getParameter("workEffortId");
if(UtilValidate.isNotEmpty(workEffortId)) {
	GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryFirst();
	
	if(UtilValidate.isNotEmpty(workEffort)) {
		context.put("actualStartDate", UtilValidate.isNotEmpty(workEffort.getTimestamp("actualStartDate")) ? workEffort.getTimestamp("actualStartDate") : UtilDateTime.nowTimestamp());
		context.put("actualCompletionDate", UtilValidate.isNotEmpty(workEffort.getTimestamp("actualCompletionDate")) ? workEffort.getTimestamp("actualCompletionDate") : UtilDateTime.nowTimestamp());
		
		context.put("activityStatus", workEffort.getString("currentStatusId"));
	}
	List<GenericValue> workEffortPartyAssignment = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where("workEffortId", workEffortId,"roleTypeId","TECHNICIAN").filterByDate().queryList();
	
	List<String> partyIds = UtilValidate.isNotEmpty(workEffortPartyAssignment) ? EntityUtil.getFieldListFromEntityList(workEffortPartyAssignment, "partyId", true) : new ArrayList<String>();
	if(UtilValidate.isNotEmpty(partyIds)) {
		Map<String, Object> technicianList = new HashMap<String, Object>();
		for(String partyId : partyIds) {
			technicianList.put(partyId, DataUtil.getUserLoginName(delegator, partyId));
		}
		println ("technicianList--------->"+technicianList);
		context.put("technicianList", technicianList);
	}
	
	List<GenericValue> rateTypes = EntityQuery.use(delegator).select("rateTypeId","description").from("RateType").where("parentTypeId","ACTIVITY").queryList();
	Map<String, Object> rateMap = new HashMap<>();
	if(UtilValidate.isNotEmpty(rateTypes)) {
		rateMap = DataUtil.getMapFromGeneric(rateTypes, "rateTypeId", "description", false);
		context.put("rateTypeList", rateMap);
	}
	
	
	
}