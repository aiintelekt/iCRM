import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.groupfio.custom.field.util.DataHelper;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

webhook = new HashMap();

configId = request.getParameter("configId");
if (UtilValidate.isNotEmpty(configId)) {
	webhook = EntityUtil.getFirst( delegator.findByAnd("CustomFieldWebhookConfig",UtilMisc.toMap("customFieldWebhookConfigId", configId), null, false) );
}
context.put("configId", configId);

context.put("webhook", webhook);

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);
