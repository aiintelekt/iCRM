import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.groupfio.custom.field.util.DataHelper;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);

groupingCode = new HashMap();

groupingCodeId = request.getParameter("groupingCodeId");
if (UtilValidate.isNotEmpty(groupingCodeId)) {
	groupingCode = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroupingCode",UtilMisc.toMap("customFieldGroupingCodeId", groupingCodeId), null, false) );
	context.put("groupType", groupingCode.getString("groupType"));
}
context.put("groupingCodeId", groupingCodeId);
context.put("groupingCode", groupingCode);
