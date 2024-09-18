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
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.entity.GenericValue;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("dyna-screenUiLabels", locale);

String dynaConfigId = request.getParameter("dynaConfigId");
context.put("dynaConfigId", dynaConfigId);

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);

String dynaFieldId = request.getParameter("dynaFieldId");
context.put("dynaFieldId", dynaFieldId);

inputContext = new LinkedHashMap<String, Object>();

inputContext.put("dynaConfigId", dynaConfigId);

if (UtilValidate.isNotEmpty(dynaConfigId) && UtilValidate.isNotEmpty(dynaFieldId)) {
	field = EntityUtil.getFirst( delegator.findByAnd("DynaScreenConfigField", UtilMisc.toMap("dynaConfigId", dynaConfigId, "dynaFieldId", dynaFieldId), null, false) );
	
	inputContext.putAll(field.getAllFields());
	
	String fieldType = field.getString("fieldType");
	
	if (UtilValidate.isNotEmpty(fieldType)) {
		String dynaInstanceId = EntityUtilProperties.getPropertyValue("dyna-screen.properties", "dyna.screen."+fieldType+".configId", delegator)
		context.put("dynaInstanceId", dynaInstanceId);
	}
	
	fieldAttrList = delegator.findByAnd("DynaScreenConfigFieldAttribute", UtilMisc.toMap("dynaConfigId", dynaConfigId, "dynaFieldId", dynaFieldId), null, false);
	if (UtilValidate.isNotEmpty(fieldAttrList)) {
		for (GenericValue fieldAttr : fieldAttrList) {
			inputContext.put(fieldAttr.getString("attrName"), fieldAttr.getString("attrValue"));
		}
	}
	
}

context.put("inputContext", inputContext);
