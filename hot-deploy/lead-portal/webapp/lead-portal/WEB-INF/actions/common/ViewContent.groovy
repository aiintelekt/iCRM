import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import org.fio.crm.content.ContentHelper;


delegator = request.getAttribute("delegator");
partyId = parameters.get("partyId");
currentRole = context.get("currentRole")
// get the generic content metadata for account
context.put("content", ContentHelper.getContentInfoForParty(partyId, "CONTACT", delegator));
