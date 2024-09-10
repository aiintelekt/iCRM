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

//layoutTypeList = new LinkedHashMap<String, Object>();
//layoutTypeList.put("1C", uiLabelMap.get("oneColumn"));
//layoutTypeList.put("2C", uiLabelMap.get("twoColumn"));
//layoutTypeList.put("3C", uiLabelMap.get("threeColumn"));
//context.put("layoutTypeList", layoutTypeList);
//
//yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
//context.put("yesNoOptions", yesNoOptions);
//
//conditionsList = [];
//conditionsList.add(EntityCondition.makeCondition("isHide", EntityOperator.EQUALS, "N"));
//mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
//componentList = delegator.findList("OfbizComponentAccess", mainConditons, null, null, null, false);
//context.put("componentList", org.fio.homeapps.util.DataHelper.getDropDownOptions(componentList, "componentName", "uiLabels"));
