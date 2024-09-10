import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;
import org.groupfio.custom.field.util.DataHelper;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.util.EntityUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

condition = UtilMisc.toMap("groupType", "ECONOMIC_METRIC"); 
cond = EntityCondition.makeCondition(condition);
groupingCodeList = delegator.findList("CustomFieldGroupingCode", cond, null, ["sequenceNumber"], null, false);
context.put("groupingCodeList", DataHelper.getDropDownOptions(groupingCodeList, "groupingCode", "groupingCode"));

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);

groupList = delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupType", GroupType.ECONOMIC_METRIC), null, false);
context.put("groupList", DataHelper.getDropDownOptions(groupList, "groupId", "groupName"));

groupId = request.getParameter("groupId");
context.put("groupId", groupId);

condition = UtilMisc.toMap("groupType", GroupType.ECONOMIC_METRIC);
if (UtilValidate.isNotEmpty(groupId)) {
	condition.put("groupId", groupId);
}

customFieldGroup = new HashMap();

customFieldGroup.put("groupId", groupId);

context.put("customFieldGroup", customFieldGroup);

customFieldName = request.getParameter("customFieldName");
isEnabled = request.getParameter("isEnabled");
groupingCode = request.getParameter("groupingCode");
customFieldId = request.getParameter("customFieldId");

customField = new HashMap();

customField.put("groupId", groupId);
customField.put("customFieldName", customFieldName);
customField.put("isEnabled", isEnabled);
customField.put("groupingCode", groupingCode);
customField.put("customFieldId", customFieldId);

context.put("customField", customField);

if (UtilValidate.isNotEmpty(customFieldId)) {
	condition.put("customFieldId", customFieldId);
}
if (UtilValidate.isNotEmpty(isEnabled)) {
	condition.put("isEnabled", isEnabled);
}
if (UtilValidate.isNotEmpty(groupingCode)) {
	cfgc = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroupingCode", UtilMisc.toMap("groupingCode", groupingCode), null, false) );
	condition.put("groupingCode", cfgc.getString("customFieldGroupingCodeId"));
}

cond = EntityCondition.makeCondition(condition);

if (UtilValidate.isNotEmpty(customFieldName)) {
	EntityCondition nameCondition = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("customFieldName")), EntityOperator.LIKE, "%"+customFieldName.toUpperCase()+"%");
	cond = EntityCondition.makeCondition([cond,
		nameCondition
	], EntityOperator.AND);
}

customFieldList = delegator.findList("CustomFieldSummary", cond, null, ["sequenceNumber"], null, false);
JSONArray results = new JSONArray();
customFieldList.each{customField ->
	JSONObject result = new JSONObject();
	result.putAll(customField);
	
	if (UtilValidate.isNotEmpty(customField.groupingCode)) {
		code = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroupingCode",UtilMisc.toMap("customFieldGroupingCodeId", customField.groupingCode), null, false) );
		if (UtilValidate.isNotEmpty(code)) {
			result.put("groupingCodeName", code.getString("groupingCode"));
		} 
	}
	results.add(result);
}
context.put("customFieldList", results);

roleConfig = new HashMap();
roleConfigId=null;
roleConfigIdList=delegator.findByAnd("CustomFieldRoleConfig", UtilMisc.toMap("groupId", groupId), null, false);
if(roleConfigIdList!=null && roleConfigIdList.size > 0){
   roleConfigId=roleConfigIdList.get(0).getString("roleTypeId");   
}
context.put("roleConfig", roleConfigId);