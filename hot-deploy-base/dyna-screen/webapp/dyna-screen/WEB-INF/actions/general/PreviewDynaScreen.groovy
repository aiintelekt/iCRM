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

String dynaConfigId = request.getParameter("dynaConfigId");

inputContext = new LinkedHashMap<String, Object>();

inputContext.put("dynaConfigId", dynaConfigId);

if (UtilValidate.isNotEmpty(dynaConfigId)) {
	dynaScreenConfig = EntityUtil.getFirst( delegator.findByAnd("DynaScreenConfig", UtilMisc.toMap("dynaConfigId", dynaConfigId), null, false) );
	
	if (UtilValidate.isNotEmpty(dynaScreenConfig)) {
		inputContext.put("screenDisplayName", dynaScreenConfig.screenDisplayName);
	}
}

context.put("inputContext", inputContext);

previewModes = new LinkedHashMap<String, Object>();

previewModes.put("CREATE", "Create Mode");
previewModes.put("VIEW", "View Mode");
previewModes.put("UPDATE", "Update Mode");

context.put("previewModes", previewModes);

