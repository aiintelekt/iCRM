import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.opentaps.common.util.UtilCommon;
import java.util.HashMap;

delegator = request.getAttribute("delegator");

screenLayoutId = request.getParameter("screenLayoutId");
if (UtilValidate.isNotEmpty(screenLayoutId)) {
	screenLayout = delegator.findByPrimaryKey("ScreenLayoutTest", UtilMisc.toMap("screenLayoutId", screenLayoutId));
} else {
	screenLayout = new HashMap();
}
context.put("screenLayout", screenLayout);

dropdownOptions = UtilMisc.toMap("option_1", "Option 1", "option_2", "Option 2", "option_3", "Option 3", "option_4", "Option 4");
context.put("dropdownOptions", dropdownOptions);