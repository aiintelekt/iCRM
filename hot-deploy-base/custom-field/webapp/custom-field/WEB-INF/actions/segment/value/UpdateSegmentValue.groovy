import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;
import org.groupfio.custom.field.util.DataHelper;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.*;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

marketingCampaignId = request.getParameter("marketingCampaignId");
context.put("marketingCampaignId", marketingCampaignId);

customField = new HashMap();

customFieldId = request.getParameter("customFieldId");

if (UtilValidate.isNotEmpty(customFieldId)) {
	customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
	if (UtilValidate.isNotEmpty(customFieldId)) {
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

valueConfig = new HashMap();
customFieldGroup = new HashMap();
if (UtilValidate.isNotEmpty(context.get("groupId")) && UtilValidate.isNotEmpty(customFieldId)) {
	valueConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldValueConfig",UtilMisc.toMap("groupId", context.get("groupId"), "customFieldId", customFieldId), null, false) );
	if (UtilValidate.isEmpty(valueConfig)) {
		valueConfig = new HashMap();
	}
	customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", context.get("groupId")), null, false) );
	
}
context.put("customFieldGroup", customFieldGroup);
context.put("valueConfig", valueConfig);

roleTypeList = delegator.findAll("CustomFieldRoleType", true);
context.put("roleTypeList", DataHelper.getDropDownOptions(roleTypeList, "roleTypeId", "description"));

groupList = delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupType", GroupType.SEGMENTATION, "isActive", "Y"), null, false);
context.put("groupList", DataHelper.getDropDownOptions(groupList, "groupId", "groupName"));

fieldTypeList = UtilMisc.toMap("SINGLE", uiLabelMap.get("single"), "MULTIPLE", uiLabelMap.get("multiple"));
context.put("fieldTypeList", fieldTypeList);

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);

valueCaptureList = UtilMisc.toMap("SINGLE", uiLabelMap.get("single"), "MULTIPLE", uiLabelMap.get("multiple"), "RANGE", uiLabelMap.get("range"));
context.put("valueCaptureList", valueCaptureList);