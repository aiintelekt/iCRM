import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.opentaps.common.util.UtilCommon;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.party.party.PartyHelper;

delegator = request.getAttribute("delegator");
loggedUseRoleTypeId = null;
partyId = userLogin.getString("partyId");
/*
try {

	partyRoleAssoc = EntityUtil.getFirst( delegator.findByAnd("PartyRoleAssoc", UtilMisc.toMap("partyId", partyId)) );
	if (UtilValidate.isNotEmpty(partyRoleAssoc)) {
		loggedUseRoleTypeId = partyRoleAssoc.getString("roleTypeId");
		context.put("loggedUseRoleTypeId", partyRoleAssoc.getString("roleTypeId"));
	}

} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
*/

loggedPartyName = PartyHelper.getPartyName(delegator, userLogin.partyId, true);
context.put("loggedPartyName", loggedPartyName);

emailAddress = PartyWorker.findPartyLatestContactMech(partyId, "EMAIL_ADDRESS", delegator);
if (UtilValidate.isNotEmpty(emailAddress)) {
	context.put("loggedPartyEmail", emailAddress.infoString);
}