import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("AdminPortalUiLabels", locale);

exportTypeList = new LinkedHashMap<String, Object>();

exportTypeList.put("CSV", "CSV");
exportTypeList.put("EXCEL", "Excel");

context.put("exportTypeList", exportTypeList);

statusOptions = UtilMisc.toMap("ACTIVE", "Active", "IN_ACTIVE", "Inactive");
context.put("statusOptions", statusOptions);

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, "SRTYPE"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
srTypeList = delegator.findList("CustRequestAssoc", mainConditons, null, null, null, false);
context.put("srTypeList", org.fio.homeapps.util.DataHelper.getDropDownOptions(srTypeList, "code", "value"));

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, "SRCategory"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
srCategoryList = delegator.findList("CustRequestAssoc", mainConditons, null, null, null, false);
context.put("srCategoryList", org.fio.homeapps.util.DataHelper.getDropDownOptions(srCategoryList, "code", "value"));

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, "SRSubCategory"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
srSubCategoryList = delegator.findList("CustRequestAssoc", mainConditons, null, null, null, false);
context.put("srSubCategoryList", org.fio.homeapps.util.DataHelper.getDropDownOptions(srSubCategoryList, "code", "value"));





