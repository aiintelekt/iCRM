import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.fio.crm.party.PartyHelper;

inputContext = context.get("inputContext");

//Person Responsible for
String personResponsible = "";
String personResponsibleAssignBy = "";

partyId = parameters.get("partyId");

if (UtilValidate.isNotEmpty(partyId)) {
    conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
    	EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
        EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD", "CONTACT")),
        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
        EntityUtil.getFilterByDateExpr()), EntityOperator.AND);

	responsibleFor = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).queryFirst();
	
	if (UtilValidate.isNotEmpty(responsibleFor)) {
	    String partyIdTo = responsibleFor.getString("partyIdTo");
	    personResponsible = PartyHelper.getPartyName(delegator, partyIdTo, false);
	    if (UtilValidate.isNotEmpty(responsibleFor.getString("createdByUserLoginId"))) {
	    	createdByUserLogin = from("UserLogin").where("userLoginId", responsibleFor.getString("createdByUserLoginId")).queryFirst();
	    	if (UtilValidate.isNotEmpty(createdByUserLogin)) {
	    		personResponsibleAssignBy = PartyHelper.getPartyName(delegator, createdByUserLogin.getString("partyId"), false);
	    	}
	    }
	}
}
context.put("personResponsible", personResponsible);
context.put("personResponsibleAssignBy", personResponsibleAssignBy);
context.put("responsibleName", personResponsible);

if (UtilValidate.isNotEmpty(inputContext)) {
	inputContext.put("personResponsible", personResponsible);
	inputContext.put("personResponsibleAssignBy", personResponsibleAssignBy);
}

context.put("inputContext", inputContext);