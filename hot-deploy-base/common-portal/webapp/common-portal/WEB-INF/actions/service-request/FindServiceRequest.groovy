import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);

String partyId = request.getParameter("partyId");
inputContext = new LinkedHashMap<String, Object>();

String dynaConfigId = request.getParameter("dynaConfigId");

loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserId", userLogin.getString("userLoginId"));

String locationCustomFieldId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LOC_CF_ID");
if (UtilValidate.isEmpty(locationCustomFieldId)) {
	locationCustomFieldId = org.ofbiz.entity.util.EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);
}
defaultLocationId = org.fio.homeapps.util.DataUtil.getPartyAttrValue(delegator, loggedUserPartyId, "LOCATION");
context.put("defaultLocationId", defaultLocationId);
context.put("locationCustomFieldId", locationCustomFieldId);

inputContext.put("dynaConfigId", dynaConfigId);
inputContext.put("startDate", "");
inputContext.put("endDate", "");
if (UtilValidate.isNotEmpty(dynaConfigId)) {
	dynaScreenConfig = EntityUtil.getFirst( delegator.findByAnd("DynaScreenConfig", UtilMisc.toMap("dynaConfigId", dynaConfigId), null, false) );
	
	if (UtilValidate.isNotEmpty(dynaScreenConfig)) {
		inputContext.put("screenDisplayName", dynaScreenConfig.screenDisplayName);
	}
}

context.put("inputContext", inputContext);
