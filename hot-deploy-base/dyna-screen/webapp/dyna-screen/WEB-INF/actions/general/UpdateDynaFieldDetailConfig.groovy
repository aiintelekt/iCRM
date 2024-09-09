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

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("isHide", EntityOperator.EQUALS, "N"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
componentList = delegator.findList("OfbizComponentAccess", mainConditons, null, null, null, false);
context.put("componentList", org.fio.homeapps.util.DataHelper.getDropDownOptions(componentList, "componentName", "uiLabels"));

String dynaFieldId = request.getParameter("dynaFieldId");
context.put("dynaFieldId", dynaFieldId);

inputContext = new LinkedHashMap<String, Object>();

inputContext.put("dynaConfigId", dynaConfigId);

if (UtilValidate.isNotEmpty(dynaConfigId) && UtilValidate.isNotEmpty(dynaFieldId)) {
	field = EntityUtil.getFirst( delegator.findByAnd("DynaScreenConfigField", UtilMisc.toMap("dynaConfigId", dynaConfigId, "dynaFieldId", dynaFieldId), null, false) );
	
	inputContext.putAll(field.getAllFields());
	
	inputContext.put("fieldName_desc", inputContext.get("fieldName"));
	
}
println("inputContext> "+inputContext);
context.put("inputContext", inputContext);
