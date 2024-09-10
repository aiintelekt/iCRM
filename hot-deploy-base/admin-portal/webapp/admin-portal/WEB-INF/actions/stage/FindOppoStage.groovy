import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "OPPO_STATUS"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
oppoStatusList = delegator.findList("Enumeration", mainConditons, null, null, null, false);
context.put("oppoStatusList", org.fio.homeapps.util.DataHelper.getDropDownOptions(oppoStatusList, "enumId", "description"));
