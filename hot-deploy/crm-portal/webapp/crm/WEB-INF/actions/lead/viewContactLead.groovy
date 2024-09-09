import org.fio.crm.constants.CrmConstants
import org.fio.crm.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;
import org.fio.crm.util.LoginFilterUtil
import org.fio.crm.util.PermissionUtil;

import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;


partyId = request.getParameter("partyId");

Map<String, Object> leadAcctMap = new HashMap<String, Object>();
leadAcctMap.put("partyIdFrom", partyId);
Map<String, Object> result = dispatcher.runSync("getLeadAndAccountAssoc", leadAcctMap);
if(ServiceUtil.isSuccess(result)){
	context.leadContactAssocList = result.leadContactAssoc;
}