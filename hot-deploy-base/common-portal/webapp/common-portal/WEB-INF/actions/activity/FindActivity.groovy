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

import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.base.util.UtilDateTime;

partyId = parameters.get("partyId");
groupingCodeId = parameters.get("groupingCodeId");

inputContext = context.get("inputContext");
println("from getAttribute, inputContext:"+inputContext);

context.put("isEnableProgramAct", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_PROG_ACT", "N"));

if (UtilValidate.isNotEmpty(inputContext) && UtilValidate.isNotEmpty(inputContext.get("activityId"))) {
	context.put("isProgAct", org.fio.homeapps.util.UtilActivity.getActivityAttrValue(delegator, inputContext.activityId, "IS_PROG_ACT"));	
}
	
if(UtilValidate.isNotEmpty(context.get("isProgAct")) && context.get("isProgAct").equals("Y")) {
	Map<String, Object> fieldConfig = org.groupfio.common.portal.util.UtilAttribute.getConfiguredFields (delegator, UtilMisc.toMap("groupList", groupList, "srNumber", inputContext.domainEntityId1, "activityDate", inputContext.estimatedStartDate));
	context.put("fieldConfig", fieldConfig);
	context.put("groupConfig", org.groupfio.common.portal.util.UtilAttribute.getConfiguredGroups (delegator, UtilMisc.toMap("groupList", groupList, "srNumber", inputContext.domainEntityId1)));
}