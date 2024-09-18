import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.groupfio.custom.field.util.DataHelper;
import org.ofbiz.entity.condition.EntityCondition;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

String isView = context.get("isView");
println('isView: '+isView);

inputContext = new LinkedHashMap<String, Object>();

groupId = request.getParameter("groupId");
if (UtilValidate.isNotEmpty(groupId)) {
	customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false) );
	inputContext.putAll(customFieldGroup.getAllFields());
	
	if(UtilValidate.isNotEmpty(isView) && "Y".equals(isView)) {
		inputContext.put("groupingCode", org.groupfio.common.portal.util.UtilAttribute.getGroupingCodeDescByIds(delegator, customFieldGroup.getString("groupingCode")));
	}
}
context.put("groupId", groupId);

context.put("customFieldGroup", inputContext);

appStatus = new HashMap();
context.put("appStatus", appStatus);

appStatusList = UtilMisc.toMap("ACTIVATED", uiLabelMap.get("activated"), "DEACTIVATED", uiLabelMap.get("deActivated"));
context.put("appStatusList", appStatusList);

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);

roleTypeList = delegator.findAll("CustomFieldRoleType", true);
context.put("roleTypeList", org.groupfio.custom.field.util.DataHelper.getDropDownOptions(roleTypeList, "roleTypeId", "description"));

groupingCodeList = delegator.findList("CustomFieldGroupingCode", EntityCondition.makeCondition(UtilMisc.toMap("groupType", GroupType.CUSTOM_FIELD)), null, ["sequenceNumber"], null, false);
context.put("groupingCodeList", DataHelper.getDropDownOptions(groupingCodeList, "customFieldGroupingCodeId", "description"));

