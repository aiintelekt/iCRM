import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("dyna-screenUiLabels", locale);

// String dynaConfigId = request.getParameter("dynaConfigId");

inputContext = new LinkedHashMap<String, Object>();

context.put("inputContext", inputContext);

uploadModes = new LinkedHashMap<String, Object>();

uploadModes.put("DEL_LOAD", "Delete and load");
uploadModes.put("OVERIDE", "Overide");
uploadModes.put("REFRESH", "Refresh");

context.put("uploadModes", uploadModes);

