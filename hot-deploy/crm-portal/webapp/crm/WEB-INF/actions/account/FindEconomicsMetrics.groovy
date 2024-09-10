import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.fio.crm.util.DataHelper;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.party.party.PartyHelper;
import org.fio.crm.util.DataHelper;
import org.ofbiz.base.conversion.JSONConverters.ListToJSON;
import org.ofbiz.base.lang.JSON;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("crmUiLabels", locale);

/*
groupingCodeList = UtilMisc.toMap("REVENUE", uiLabelMap.get("revenue"), "BALANCES", uiLabelMap.get("balances"));
context.put("groupingCodeList", groupingCodeList);
*/
condition = UtilMisc.toMap("groupType", "ECONOMIC_METRIC"); 
cond = EntityCondition.makeCondition(condition);
groupingCodeList = delegator.findList("CustomFieldGroupingCode", cond, null, ["sequenceNumber"], null, false);
context.put("groupingCodeList", DataHelper.getDropDownOptions(groupingCodeList, "groupingCode", "groupingCode"));

condition = UtilMisc.toMap();

partyId = request.getParameter("partyId");
groupingCode = request.getParameter("groupingCode");
economicCodeId = request.getParameter("economicCodeId");

metricIndicator = new HashMap();

metricIndicator.put("groupingCode", groupingCode);
metricIndicator.put("economicCodeId", economicCodeId);

context.put("metricIndicator", metricIndicator);

if (UtilValidate.isNotEmpty(partyId)) {
	condition.put("partyId", partyId);
}
if (UtilValidate.isNotEmpty(groupingCode)) {
	condition.put("groupingCode", groupingCode);
}
if (UtilValidate.isNotEmpty(economicCodeId)) {
	condition.put("groupId", economicCodeId);
}

cond = EntityCondition.makeCondition(condition);
metricIndicatorList = delegator.findList("PartyMetricIndicator", cond, null, ["sequenceNumber"], null, false);
/*
JSONArray results = new JSONArray();
for (GenericValue metricIndicator : metricIndicatorList) {

	partyId = metricIndicator.getString("partyId");
	//partySummary = delegator.findOne("PartySummaryDetailsView", ["partyId": partyId], false);
	
	JSONObject result = new JSONObject();
	
	name = PartyHelper.getPartyName(delegator, partyId, false);
	
	result.put("partyName", name);
	result.put("groupingCode", metricIndicator.getString("groupingCode"));
	result.put("propertyName", metricIndicator.getString("propertyName"));
	result.put("propertyValue", metricIndicator.getString("propertyValue"));

	results.add(result);
}
*/
context.put("metricIndicatorList", metricIndicatorList);
ListToJSON listToJSON = new ListToJSON();
JSON json = listToJSON.convert(metricIndicatorList);
context.put("metricIndicatorListStr", json.toString());

