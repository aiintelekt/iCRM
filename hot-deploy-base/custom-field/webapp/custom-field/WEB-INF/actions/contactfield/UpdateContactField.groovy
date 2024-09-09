import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.groupfio.custom.field.util.DataHelper;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

customField = new HashMap();

customFieldId = request.getParameter("customFieldId");
if (UtilValidate.isNotEmpty(customFieldId)) {
	customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
	context.put("groupId", customField.getString("groupId"));
}
context.put("customFieldId", customFieldId);

context.put("customField", customField);

customFieldGroup = new HashMap();
if (UtilValidate.isNotEmpty(context.get("groupId"))) {
	customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", context.get("groupId")), null, false) );
}
context.put("customFieldGroup", customFieldGroup);

context.put("showCustomSearch", true);

appStatus = new HashMap();
context.put("appStatus", appStatus);

appStatusList = UtilMisc.toMap("ACTIVATED", uiLabelMap.get("activated"), "DEACTIVATED", uiLabelMap.get("deActivated"));
context.put("appStatusList", appStatusList);

roleTypeList = delegator.findAll("CustomFieldRoleType", true);
context.put("roleTypeList", DataHelper.getDropDownOptions(roleTypeList, "roleTypeId", "description"));

groupList = delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupType", GroupType.CONTACT_FIELD), null, false);
context.put("groupList", DataHelper.getDropDownOptions(groupList, "groupId", "groupName"));

fieldTypeList = UtilMisc.toMap("SINGLE", uiLabelMap.get("single"), "MULTIPLE", uiLabelMap.get("multiple"));
context.put("fieldTypeList", fieldTypeList);

fieldFormatList = UtilMisc.toMap("TEXT", uiLabelMap.get("text"), "DATE", uiLabelMap.get("date"), "NUMERIC", uiLabelMap.get("numeric"), "BOOLEAN", uiLabelMap.get("boolean"), "TEXT_AREA", uiLabelMap.get("textArea"), "CHECK_BOX", uiLabelMap.get("checkBox"), "DROP_DOWN", uiLabelMap.get("dropDown"), "RADIO", uiLabelMap.get("radio"), "LABEL_TEXT", uiLabelMap.get("labelText"));
context.put("fieldFormatList", fieldFormatList);

fieldLengthList = new HashMap();
1.upto(20, {
   fieldLengthList.put("${it}", "${it}");
})
context.put("fieldLengthList", DataHelper.getFieldLengthOptions());

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);

roleConfig = new HashMap();
roleConfigId=null;
roleConfigIdList=delegator.findByAnd("CustomFieldRoleConfig", UtilMisc.toMap("customFieldId", customFieldId), null, false);
if(roleConfigIdList!=null && roleConfigIdList.size > 0){
   roleConfigId=roleConfigIdList.get(0).getString("roleTypeId");   
}
context.put("roleConfig", roleConfigId);