import org.fio.homeapps.util.DataUtil
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil

String srNumber = request.getParameter("srNumber");

if(UtilValidate.isNotEmpty(srNumber)) {
	
	DynamicViewEntity dynamicEntity = new DynamicViewEntity();
	dynamicEntity.addMemberEntity("CRWE", "CustRequestWorkEffort");
	dynamicEntity.addAlias("CRWE", "custRequestId");
	dynamicEntity.addAlias("CRWE", "workEffortId");
	
	dynamicEntity.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
	dynamicEntity.addAlias("WEPA", "roleTypeId");
	dynamicEntity.addAlias("WEPA", "fromDate");
	dynamicEntity.addAlias("WEPA", "thruDate");
	
	dynamicEntity.addViewLink("CRWE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
	
	List<GenericValue> custRequestWorkEfforts = EntityQuery.use(delegator).from(dynamicEntity).where("custRequestId", srNumber,"roleTypeId", "TECHNICIAN").filterByDate().cache(true).queryList();
	List<String> workEffortIds = UtilValidate.isNotEmpty(custRequestWorkEfforts) ? EntityUtil.getFieldListFromEntityList(custRequestWorkEfforts, "workEffortId", true) : new ArrayList<String>();
	
	if(UtilValidate.isNotEmpty(workEffortIds)) {
		List<GenericValue> workEffortList = EntityQuery.use(delegator).select("workEffortId","workEffortName").from("WorkEffort").where(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds)).cache(true).queryList();
		if(UtilValidate.isNotEmpty(workEffortList)) {
			Map<String, Object> workEffortMap = DataUtil.getMapFromGeneric(workEffortList, "workEffortId", "workEffortName", false);
			context.put("workEffortList", workEffortMap);
		}
	}
	
	List<GenericValue> rateTypes = EntityQuery.use(delegator).select("rateTypeId","description").from("RateType").where("parentTypeId","ACTIVITY").queryList();
	Map<String, Object> rateMap = new HashMap<>();
	if(UtilValidate.isNotEmpty(rateTypes)) {
		rateMap = DataUtil.getMapFromGeneric(rateTypes, "rateTypeId", "description", false);
		context.put("rateTypeList", rateMap);
	}
}