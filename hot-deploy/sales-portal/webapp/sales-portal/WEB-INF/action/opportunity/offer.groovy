import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilMisc;

salesOpportunity = EntityQuery.use(delegator).select("marketingCampaignId").from("SalesOpportunity").where("salesOpportunityId", parameters.salesOpportunityId).queryOne();

if (salesOpportunity && salesOpportunity.getString("marketingCampaignId")) {
    offerCodeGroupAssocList = EntityQuery.use(delegator).from("OfferCodeGroupAssoc").where("marketingCampaignId", salesOpportunity.getString("marketingCampaignId")).queryList();
    offerCodeGroupIds = EntityUtil.getFieldListFromEntityList(offerCodeGroupAssocList, "offerCodeGroupId", true);
    offerCodeGroups = EntityQuery.use(delegator).from("OfferCodeGroup").where(EntityCondition.makeCondition("offerCodeGroupId", EntityOperator.IN, offerCodeGroupIds)).queryList();

    for (GenericValue offerCodeGroup : offerCodeGroups) {
        if ("CUST_PL_OFFER".equalsIgnoreCase(offerCodeGroup.offerCodePurposeTypeId)) {
            offerCodeGroupTransConfig = EntityQuery.use(delegator).select("fieldIdSeqNum", "fieldDescription").from("OfferCodeGroupTransConfig").where("offerCodeGroupId", offerCodeGroup.getString("offerCodeGroupId")).orderBy("fieldIdSeqNum").queryList();

            fieldDescriptionList = EntityUtil.getFieldListFromEntityList(offerCodeGroupTransConfig, "fieldDescription", false);
            OfferCodeGroupTransData = [];
            if (offerCodeGroupTransConfig) {
                offerCodeGroupTransDataList = EntityQuery.use(delegator).from("OfferCodeGroupTransData").where("offerCodeGroupId", offerCodeGroup.getString("offerCodeGroupId")).queryList();
                for (GenericValue offerCodeGroupTransData : offerCodeGroupTransDataList) {
                    dataList = [];
                    for (GenericValue offerCodeGroupData : offerCodeGroupTransConfig) {
                        if (offerCodeGroupTransData && offerCodeGroupData.getString("fieldIdSeqNum") && offerCodeGroupTransData.getString("fieldValue" + offerCodeGroupData.getString("fieldIdSeqNum"))) {
                            dataList.add(offerCodeGroupTransData.getString("fieldValue" + offerCodeGroupData.getString("fieldIdSeqNum")));
                        } else {
                            dataList.add("");
                        }
                    }
                    OfferCodeGroupTransData.add(dataList);
                }

            }
            context.headerList = fieldDescriptionList;
            context.fieldList = OfferCodeGroupTransData;
        } else if ("CUST_BT_OFFER".equalsIgnoreCase(offerCodeGroup.offerCodePurposeTypeId)) {
            offerCodeGroupTransConfig = EntityQuery.use(delegator).select("fieldIdSeqNum", "fieldDescription").from("OfferCodeGroupTransConfig").where("offerCodeGroupId", offerCodeGroup.getString("offerCodeGroupId")).orderBy("fieldIdSeqNum").queryList();

            fieldDescriptionList = EntityUtil.getFieldListFromEntityList(offerCodeGroupTransConfig, "fieldDescription", false);
            OfferCodeGroupTransData = [];
            if (offerCodeGroupTransConfig) {
                offerCodeGroupTransDataList = EntityQuery.use(delegator).from("OfferCodeGroupTransData").where("offerCodeGroupId", offerCodeGroup.getString("offerCodeGroupId")).queryList();
                for (GenericValue offerCodeGroupTransData : offerCodeGroupTransDataList) {
                    dataList = [];
                    for (GenericValue offerCodeGroupData : offerCodeGroupTransConfig) {
                        if (offerCodeGroupTransData && offerCodeGroupData.getString("fieldIdSeqNum") && offerCodeGroupTransData.getString("fieldValue" + offerCodeGroupData.getString("fieldIdSeqNum"))) {
                            dataList.add(offerCodeGroupTransData.getString("fieldValue" + offerCodeGroupData.getString("fieldIdSeqNum")));
                        } else {
                            dataList.add("");
                        }
                    }
                    OfferCodeGroupTransData.add(dataList);
                }

            }
            context.balancedTransferHeaders = fieldDescriptionList;
            context.balancedTransferData = OfferCodeGroupTransData;
        } else if ("PL_TIER_GROUP".equalsIgnoreCase(offerCodeGroup.offerCodePurposeTypeId)) {
            offerCodeGroupTransData = EntityQuery.use(delegator).from("OfferCodeGroupTransData").where("offerCodeGroupId", offerCodeGroup.offerCodeGroupId).queryFirst();
            if (offerCodeGroupTransData && offerCodeGroupTransData.fieldValue1) {
                context.plTierGroupData = offerCodeGroupTransData.getString("fieldValue1");
            }
        } else if ("BTL_TIER_GROUP".equalsIgnoreCase(offerCodeGroup.offerCodePurposeTypeId)) {
            offerCodeGroupTransData = EntityQuery.use(delegator).from("OfferCodeGroupTransData").where("offerCodeGroupId", offerCodeGroup.offerCodeGroupId).queryFirst();
            if (offerCodeGroupTransData && offerCodeGroupTransData.fieldValue1) {
                context.btlTierGroupData = offerCodeGroupTransData.getString("fieldValue1");
            }
        } else if ("DCP_TIER_GROUP".equalsIgnoreCase(offerCodeGroup.offerCodePurposeTypeId)) {
            offerCodeGroupTransData = EntityQuery.use(delegator).from("OfferCodeGroupTransData").where("offerCodeGroupId", offerCodeGroup.offerCodeGroupId).queryFirst();
            if (offerCodeGroupTransData && offerCodeGroupTransData.fieldValue1) {
                context.dcpTierGroupData = offerCodeGroupTransData.getString("fieldValue1");
            }
        }
    }
}