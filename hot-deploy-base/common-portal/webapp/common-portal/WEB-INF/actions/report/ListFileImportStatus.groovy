import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.ofbiz.entity.condition.EntityCondition;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("DataImporterUiLabels", locale);


/*
cond = EntityCondition.makeCondition(UtilMisc.toMap("groupType", GroupType.ECONOMIC_METRIC));
customFieldGroupList = delegator.findList("CustomFieldGroup", cond, null, ["sequence"], null, false);
context.put("customFieldGroupList", customFieldGroupList);
*/