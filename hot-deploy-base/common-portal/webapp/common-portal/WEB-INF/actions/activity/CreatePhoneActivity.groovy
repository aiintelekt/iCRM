import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityExpr;
import java.util.*;
import org.ofbiz.entity.util.EntityQuery;

import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.base.util.UtilDateTime;

partyId = parameters.get("partyId");

inputContext = context.get("inputContext");
println("from getAttribute, inputContext:"+inputContext);

context.put("activeMessType", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACTIVE_MESS_TYPE"));
context.put("activeRcMessAPI", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "RC_MSG_API", "MVP"));

String defalutMktCampaignId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DIRECT_CALL");
if(UtilValidate.isNotEmpty(defalutMktCampaignId) && UtilValidate.isNotEmpty(partyId)) {
	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
	conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	conditions.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.EQUALS, defalutMktCampaignId));
	conditions.add(EntityUtil.getFilterByDateExpr("startDate", "endDate"));
	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
   	GenericValue callMaster = EntityQuery.use(delegator).from("CallRecordMaster").where(mainConditon).queryFirst();
   	if(UtilValidate.isNotEmpty(callMaster)) {
   		context.put("callRecordMaster", callMaster);
   		context.put("callRecordId", callMaster.get("callRecordId"));
   	}         			
}



