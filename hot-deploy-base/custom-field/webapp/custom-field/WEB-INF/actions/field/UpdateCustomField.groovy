import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;
import org.groupfio.custom.field.util.DataHelper;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.*;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

customField = new HashMap();

customFieldId = request.getParameter("customFieldId");
if (UtilValidate.isNotEmpty(customFieldId)) {
	customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
	if (UtilValidate.isNotEmpty(customField)) {
		context.put("groupId", customField.getString("groupId"));
	}
}
context.put("customFieldId", customFieldId);

context.put("customField", customField);
String productPromoCodeGroupId = (UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("productPromoCodeGroupId"))) ? customField.getString("productPromoCodeGroupId") :"";
if(UtilValidate.isNotEmpty(productPromoCodeGroupId)) {
    GenericValue productPromoCodeGroup = EntityQuery.use(delegator).from("ProductPromoCodeGroup").where("productPromoCodeGroupId", productPromoCodeGroupId).queryFirst();
    context.put("productPromoCodeGroupId_desc", UtilValidate.isNotEmpty(productPromoCodeGroup) ? productPromoCodeGroup.getString("description") : "");
}

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

groupList = delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupType", GroupType.CUSTOM_FIELD), null, false);
context.put("groupList", DataHelper.getDropDownOptions(groupList, "groupId", "groupName"));

fieldTypeList = UtilMisc.toMap("SINGLE", uiLabelMap.get("single"), "MULTIPLE", uiLabelMap.get("multiple"));
context.put("fieldTypeList", fieldTypeList);

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CF_FLD_FORMAT"));
conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
fieldFormatList = delegator.findList("Enumeration", mainConditons, null, null, null, false);
context.put("fieldFormatList", org.fio.homeapps.util.DataHelper.getDropDownOptions(fieldFormatList, "enumId", "description"));

fieldLengthList = new HashMap();
1.upto(20, {
   fieldLengthList.put("${it}", "${it}");
})
context.put("fieldLengthList", DataHelper.getFieldLengthOptions());

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);

paramDisplayTypes = UtilMisc.toMap("INLINE", uiLabelMap.get("inline"), "LINK", uiLabelMap.get("link"));
context.put("paramDisplayTypes", paramDisplayTypes);

roleConfig = new HashMap();
roleConfigId=null;
roleConfigIdList=delegator.findByAnd("CustomFieldRoleConfig", UtilMisc.toMap("customFieldId", customFieldId), null, false);
if(roleConfigIdList!=null && roleConfigIdList.size > 0){
   roleConfigId=roleConfigIdList.get(0).getString("roleTypeId");   
}
context.put("roleConfig", roleConfigId);