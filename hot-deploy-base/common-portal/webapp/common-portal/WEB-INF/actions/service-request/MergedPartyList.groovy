import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.EntityCondition;
import javolution.util.FastList;
import javolution.util.FastMap;

partyId = request.getParameter("partyId");

if (UtilValidate.isNotEmpty(partyId)) {
	condition = UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "PREDECESSOR_FOR"); 
	cond = EntityCondition.makeCondition(condition);
	predecessorList = delegator.findList("PartyRelationship", cond, null, null, null, false);
	context.putAt("predecessorList", predecessorList);
	
	condition = UtilMisc.toMap("partyIdFrom", partyId, "partyRelationshipTypeId", "PREDECESSOR_FOR");
	cond = EntityCondition.makeCondition(condition);
	successorList = delegator.findList("PartyRelationship", cond, null, null, null, false);
	context.putAt("successorList", successorList)
}