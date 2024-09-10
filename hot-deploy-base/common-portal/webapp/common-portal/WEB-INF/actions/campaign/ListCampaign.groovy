import org.ofbiz.base.util.UtilValidate;

campaignTypeList = delegator.findAll("CampaignType",true);
context.campaignTypeList = campaignTypeList;
showCampaignTabs = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SHOW_CAMPAIGN_TABS");
context.showCampaignTabs = UtilValidate.isNotEmpty(showCampaignTabs)?showCampaignTabs:"N";
