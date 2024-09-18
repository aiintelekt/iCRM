import org.ofbiz.base.util.*;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("account-portalUiLabels", locale);

exportTypeList = new LinkedHashMap<String, Object>();

exportTypeList.put("CSV", "CSV");
exportTypeList.put("EXCEL", "Excel");

context.put("exportTypeList", exportTypeList);

exportFileTypes = new LinkedHashMap<String, Object>();

exportFileTypes.put("IMPORT_FILE", "Import file");
exportFileTypes.put("ERROR_FILE", "Error file");

context.put("exportFileTypes", exportFileTypes);

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
usersList = delegator.findList("UserLoginPerson", mainConditons, null, null, null, false);
context.put("usersList", org.fio.homeapps.util.DataHelper.getDropDownOptions(usersList, "userLoginId", "firstName"));

context.put("callOutcomeList", org.fio.homeapps.util.DataHelper.getDropDownOptions(org.fio.homeapps.util.EnumUtil.getEnums(delegator, "CALL_OUT_COME"), "enumId", "description"));

context.put("salesChannelList", org.fio.homeapps.util.DataHelper.getDropDownOptions(org.fio.homeapps.util.EnumUtil.getEnums(delegator, "SALES_CHANNEL_ID"), "enumId", "description"));

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "MKTG_CAMP_INPROGRESS"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
campaignList = delegator.findList("MarketingCampaign", mainConditons, null, null, null, false);
context.put("campaignList", org.fio.homeapps.util.DataHelper.getDropDownOptions(campaignList, "marketingCampaignId", "campaignName"));



