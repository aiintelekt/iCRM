/**
 * @author Group Fio
 *
 */
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.opentaps.base.constants.StatusItemConstants;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.util.EntityUtil;

format = new SimpleDateFormat("dd/MM/yyyy");

selectedDate = parameters.query;
if (UtilValidate.isNotEmpty(selectedDate)) {
	context.put("selectedDate", selectedDate);
} else {
	context.put("selectedDate", format.format(new Date()));
}

context.put("currentDate", format.parse(context.get("selectedDate")) );

partyId = userLogin.getString("partyId");
context.put("partyId", partyId);
userLoginId = userLogin.getString("userLoginId");

loggedPartyName = PartyHelper.getPartyName(delegator, partyId, true);
context.put("loggedPartyName", loggedPartyName);
