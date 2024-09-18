import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.Debug;

import javolution.util.FastList;
import java.text.DecimalFormat;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("AdminPortalUiLabels", locale);

inputContext = new LinkedHashMap<String, Object>();
partyId = request.getParameter("partyId");
if(UtilValidate.isNotEmpty(partyId)) {
	partyGroup = EntityQuery.use(delegator).from("PartyGroup").select("groupName").where("partyId",partyId).queryOne();
	if(UtilValidate.isNotEmpty(partyGroup)&&UtilValidate.isNotEmpty(partyGroup.getString("groupName"))) {
		String groupName = partyGroup.getString("groupName");
		context.groupName=groupName;
	}
}
context.put("tokenKey", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "BOLDBI_TOKEN_KEY"));
context.put("inputContext", inputContext);
