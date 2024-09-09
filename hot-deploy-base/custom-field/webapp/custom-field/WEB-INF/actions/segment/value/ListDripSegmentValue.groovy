import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;
import org.ofbiz.base.util.UtilDateTime;
import java.util.Date;
import java.util.ArrayList;
import org.groupfio.custom.field.util.DataUtil;
import javolution.util.FastList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.fio.campaign.util.LoginFilterUtil;
import org.fio.homeapps.util.ResponseUtils;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);
HttpSession session = request.getSession();
userLogin = request.getAttribute("userLogin");
userLoginId = userLogin.getString("partyId");
List < String > accountList = new ArrayList < String > ();
if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId)) {
    Map < String, Object > dataSecurityMetaInfo = (Map < String, Object > ) session.getAttribute("dataSecurityMetaInfo");
    if (ResponseUtils.isSuccess(dataSecurityMetaInfo)) {
        List < String > lowerPositionPartyIds = (List < String > ) dataSecurityMetaInfo.get("lowerPositionPartyIds");
        accountList = LoginFilterUtil.getCampaignsAccountList(delegator, lowerPositionPartyIds);
    }
}

groupId = request.getParameter("groupId");
customFieldGroup = context.get("customFieldGroup");
marketingCampaignId = request.getParameter("marketingCampaignId");
context.put("marketingCampaignId", marketingCampaignId);
salesTriggerTypeNew = EntityUtil.getFirst(delegator.findByAnd("MarketingCampaignContactList", UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "contactListId", "RMSales_Trigger_Monday_EveryMonth"), null, false));
context.put("salesTriggerTypeNew", salesTriggerTypeNew);
responseCountNew = context.get("responseCountJSONObj");
context.put("responseCountNew", responseCountNew);
println("context segmentCodeId>>>> " + context.get("segmentCodeId"));
if (UtilValidate.isEmpty(groupId)) {
    groupId = context.get("segmentCodeId");
    if (UtilValidate.isNotEmpty(groupId) && UtilValidate.isEmpty(customFieldGroup)) {
        customFieldGroup = EntityUtil.getFirst(delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false));
    }
}

context.put("customFieldGroup", UtilValidate.isNotEmpty(customFieldGroup) ? customFieldGroup : new HashMap());
context.put("groupId", groupId);
context.put("userLoginId", userLoginId);

condition = UtilMisc.toMap("groupType", GroupType.SEGMENTATION);
if (UtilValidate.isNotEmpty(groupId)) {
    condition.put("groupId", groupId);
}

cond = EntityCondition.makeCondition(condition);
println("cond>>>> " + cond);
customFieldList = delegator.findList("CustomFieldSummary", cond, null, ["sequenceNumber"], null, false);

JSONArray fieldList = new JSONArray();

customFieldList.each {
    customField ->

        JSONObject field = new JSONObject();

    groupId = customField.getString("groupId");
    customFieldId = customField.getString("customFieldId");

    field.putAll(customField);

    campaignConfig = EntityUtil.getFirst(delegator.findByAnd("CustomFieldCampaignConfig", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId), null, false));
    if (UtilValidate.isNotEmpty(campaignConfig)) {
        field.put("campaignConfigType", campaignConfig.getString("configType"));
        field.put("isCouponSegment", campaignConfig.getString("isCouponSegment"));

        campaignConfigAssocList = delegator.findByAnd("CustomFieldCampaignConfigAssoc", UtilMisc.toMap("customFieldId", customFieldId), UtilMisc.toList("sequenceNumber"), false);
        if (UtilValidate.isNotEmpty(campaignConfigAssocList)) {
            String configurationSummary = "";
            List < String > configSummaryList = new ArrayList < String > ();
            campaignConfigAssocList.each {
                configAssoc ->
                    if (campaignConfig.getString("configType").equals("BATCH")) {
                        marketingCampaign = configAssoc.getRelatedOne("MarketingCampaign", false);
                        if (campaignConfig.getString("configBatchType").equals("SPEC_DATE")) {

                            String dateString = UtilDateTime.toDateString(new Date(configAssoc.getTimestamp("specificDate").getTime()), "dd-MM-yyyy");
                            String configSummary = dateString + " / " + marketingCampaign.getString("campaignName") + " (" + marketingCampaign.getString("marketingCampaignId") + ")";
                            configurationSummary += configSummary;
                            configSummaryList.add(configSummary);
                        } else if (campaignConfig.getString("configBatchType").equals("DAY_SINCE")) {
                            String configSummary = configAssoc.get("daySince") + " / " + marketingCampaign.getString("campaignName") + " (" + marketingCampaign.getString("marketingCampaignId") + ")";
                            configurationSummary += configSummary;
                            configSummaryList.add(configSummary);
                        }
                    } else if (campaignConfig.getString("configType").equals("TRIGGER")) {
                    String configSummary = configAssoc.get("triggerUrl") + " / " + marketingCampaign.getString("campaignName") + " (" + marketingCampaign.getString("marketingCampaignId") + ")";
                    configurationSummary += configSummary;
                    configSummaryList.add(configSummary);
                }
            }
            field.put("configSummaryList", configSummaryList);
            field.put("configurationSummary", configurationSummary);
        }
    }

    String segmentationValueAssociatedEntityName = DataUtil.getSegmentationValueAssociatedEntityName(delegator, groupId);
    conditionsList = FastList.newInstance();
    segmentCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId),
        EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, customFieldId)
    ], EntityOperator.AND);
    conditionsList.add(segmentCondition);

    if (UtilValidate.isNotEmpty(accountList) && accountList.size() != 0) {
        EntityCondition accountCondition = EntityCondition.makeCondition("partyId", EntityOperator.IN, accountList);
        conditionsList.add(accountCondition);
    }
    EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
    int coustomerCount = delegator.findCountByCondition(segmentationValueAssociatedEntityName, mainConditons, null, null);
    field.put("coustomerCount", coustomerCount);

    fieldList.add(field);
}

context.put("customFieldList", fieldList);

