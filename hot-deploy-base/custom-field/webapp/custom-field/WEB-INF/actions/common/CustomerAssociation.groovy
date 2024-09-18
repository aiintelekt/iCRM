import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.groupfio.custom.field.util.DataHelper;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

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

valueConfig = new HashMap();
customFieldGroup = new HashMap();
if (UtilValidate.isNotEmpty(context.get("groupId")) && UtilValidate.isNotEmpty(customFieldId)) {
	customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", context.get("groupId")), null, false) );
}
context.put("customFieldGroup", customFieldGroup);

roleTypeList = delegator.findAll("CustomFieldRoleType", true);
context.put("roleTypeList", DataHelper.getDropDownOptions(roleTypeList, "roleTypeId", "description"));
