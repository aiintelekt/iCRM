import org.fio.homeapps.util.DataUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

Delegator delegator = request.getAttribute("delegator");

String userLoginId = request.getParameter("userLoginId");
if(UtilValidate.isNotEmpty(userLoginId)) {
	String userLoginPartyId = DataUtil.getPartyIdByUserLoginId(delegator, userLoginId);
	context.put("userLoginPartyId", userLoginPartyId);
	
	context.put("agentId", org.groupfio.common.portal.util.UtilAttribute.getPartyAttribute(delegator, userLoginPartyId, "RC_AGENT_ID"));
	context.put("stationId", org.groupfio.common.portal.util.UtilAttribute.getPartyAttribute(delegator, userLoginPartyId, "RC_STATION_ID"));
}

context.put("activeMessType", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACTIVE_MESS_TYPE"));
context.put("activeRcMessAPI", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "RC_MSG_API"));
