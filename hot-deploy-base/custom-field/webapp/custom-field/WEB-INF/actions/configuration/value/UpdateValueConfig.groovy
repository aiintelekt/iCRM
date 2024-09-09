import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.groupfio.custom.field.util.DataHelper;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

valueConfig = new HashMap();

groupId = request.getParameter("groupId");
customFieldId = request.getParameter("customFieldId");
valueCapture = request.getParameter("valueCapture");
valueSeqNum = request.getParameter("valueSeqNum");
if (UtilValidate.isNotEmpty(groupId) && UtilValidate.isNotEmpty(customFieldId) && UtilValidate.isNotEmpty(valueCapture) && UtilValidate.isNotEmpty(valueSeqNum)) {
	valueConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldValueConfig",UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "valueCapture", valueCapture, "valueSeqNum", Long.parseLong(valueSeqNum)), null, false) );
}
context.put("groupId", groupId);
context.put("customFieldId", customFieldId);
context.put("valueCapture", valueCapture);
context.put("valueSeqNum", valueSeqNum);

context.put("valueConfig", valueConfig);

valueCaptureList = UtilMisc.toMap("SINGLE", uiLabelMap.get("single"), "MULTIPLE", uiLabelMap.get("multiple"), "RANGE", uiLabelMap.get("range"));
context.put("valueCaptureList", valueCaptureList);
