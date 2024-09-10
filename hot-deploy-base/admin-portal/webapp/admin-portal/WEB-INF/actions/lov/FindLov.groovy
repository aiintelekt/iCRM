import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("AdminPortalUiLabels", locale);

yesNoOptions = UtilMisc.toMap("Y", uiLabelMap.get("yes"), "N", uiLabelMap.get("no"));
context.put("yesNoOptions", yesNoOptions);


//conditionsList = [];
//conditionsList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "DIGITAL_GOOD"));
//mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
lovTypeList = delegator.findList("LovEntityType", null, null, null, null, false);
context.put("lovTypeList", org.fio.homeapps.util.DataHelper.getDropDownOptions(lovTypeList, "lovEntityTypeId", "description"));
