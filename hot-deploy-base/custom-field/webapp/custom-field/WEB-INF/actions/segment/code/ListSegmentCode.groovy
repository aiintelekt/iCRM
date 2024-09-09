import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

cond = EntityCondition.makeCondition(UtilMisc.toMap("groupType", GroupType.SEGMENTATION));
customFieldGroupList = delegator.findList("CustomFieldGroup", cond, null, ["sequence"], null, false);
context.put("customFieldGroupList", customFieldGroupList);