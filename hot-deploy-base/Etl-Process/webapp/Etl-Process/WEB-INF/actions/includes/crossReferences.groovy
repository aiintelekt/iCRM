import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.Debug;

import javolution.util.FastList;

import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilValidate;

partyIdentificationTypeId = request.getParameter("partyIdentificationTypeId");
if(UtilValidate.isNotEmpty(partyIdentificationTypeId))
{
	GenericValue partyIdentificationTypes = EntityQuery.use(delegator).from("PartyIdentificationType").where("partyIdentificationTypeId", partyIdentificationTypeId).queryOne();
	if(UtilValidate.isNotEmpty(partyIdentificationTypes))
	{
		context.put("partyIdentificationTypeId", partyIdentificationTypes.getString("partyIdentificationTypeId"));
		context.put("description", partyIdentificationTypes.getString("description"));
		context.put("isEnabled", partyIdentificationTypes.getString("isEnabled"));
	}
}

partyIdentificationType = delegator.findAll("PartyIdentificationType", false);
if(UtilValidate.isNotEmpty(partyIdentificationType)){
	context.put("partyIdentificationType", partyIdentificationType);
}
