/**
 * 
 */
package org.groupfio.account.portal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif
 *
 */
public class DataHelper {
	
	private static String MODULE = DataHelper.class.getName();
	
	public static String getLatestCampaign(Delegator delegator, String partyId) {
		String recentCampaign = "";
		if(UtilValidate.isNotEmpty(partyId)) {
			try {
				List<GenericValue> contactListParty = EntityQuery.use(delegator).from("ContactListParty").select("contactListId").where("partyId",partyId).filterByDate().orderBy("lastUpdatedTxStamp DESC").queryList();
				if(UtilValidate.isNotEmpty(contactListParty)) {
					String contactListId = contactListParty.get(0).getString("contactListId");
					List<GenericValue> campaignContactList = EntityQuery.use(delegator).from("MarketingCampaignContactList").select("marketingCampaignId").where("contactListId",contactListId).filterByDate().orderBy("lastUpdatedTxStamp DESC").queryList();
					if(UtilValidate.isNotEmpty(campaignContactList)) {
						String marketingCampaignId = campaignContactList.get(0).getString("marketingCampaignId");
						GenericValue marketingCampaign = EntityQuery.use(delegator).select("marketingCampaignId","campaignName").from("MarketingCampaign").where("marketingCampaignId",marketingCampaignId).queryFirst();
						if(UtilValidate.isNotEmpty(marketingCampaign)) {
							recentCampaign = marketingCampaign.getString("campaignName")+"("+marketingCampaignId+")";
						}
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
	    		Debug.logError(e.getMessage(), MODULE);

			}	
		}
		return recentCampaign;
	} 
	
	public static Map<String, Object> prepareHomeKpiInfo(Delegator delegator, GenericValue userLogin) {
		
		Map<String, Object> kpiMetric = new LinkedHashMap<String, Object>();
		
		try {
			if (UtilValidate.isNotEmpty(userLogin)) {
				
				String userLoginId = userLogin.getString("userLoginId");
				String userLoginPartyId = userLogin.getString("partyId");
				//String emplTeamId = DataUtil.getEmplTeamId(delegator, userLogin.getString("partyId"));
				String emplTeamId = org.groupfio.common.portal.util.DataUtil.getEmplTeamId(delegator, userLoginPartyId);
				
				long myOpportunity = org.groupfio.common.portal.util.DataHelper.getMyOpportunityCount(delegator, userLoginId);
				long myTeamOpportunity = org.groupfio.common.portal.util.DataHelper.getMyTeamOpportunityCount(delegator, emplTeamId);
				
				kpiMetric.put("myOpportunity", myOpportunity);
				kpiMetric.put("myTeamOpportunity", myTeamOpportunity);
				
				/*
				EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)
						//EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeIdFrom),
						//EntityUtil.getFilterByDateExpr()
						);
				
				long myOrderCount = delegator.findCountByCondition("OrderHeader", searchConditions, null, UtilMisc.toSet("orderId"), null);
				*/
				
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return kpiMetric;
		
	}
	
	public static Map<String, Object> getAccountDashboardContext(Delegator delegator, GenericValue userLogin) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String userLoginId = userLogin.getString("userLoginId");
			String partyId = userLogin.getString("partyId");
			String emplTeamId = org.groupfio.common.portal.util.DataUtil.getEmplTeamId(delegator, partyId);
			
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PR", "PartyRelationship");
            dynamicView.addAlias("PR", "partyIdTo");
            dynamicView.addAlias("PR", "partyIdFrom");
            dynamicView.addAlias("PR", "roleTypeIdFrom");
            dynamicView.addAlias("PR", "partyRelationshipTypeId");
            dynamicView.addAlias("PR", "fromDate");
            dynamicView.addAlias("PR", "thruDate");
            
            dynamicView.addMemberEntity("PTY", "Party");
            dynamicView.addAlias("PTY", "partyId");
            dynamicView.addAlias("PTY", "statusId");
            //dynamicView.addAlias("PTY", "partyTypeId");
            dynamicView.addAlias("PTY", "createdDate");
            dynamicView.addAlias("PTY", "lastModifiedDate");
            dynamicView.addViewLink("PR", "PTY", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyIdFrom", "partyId"));
            
            List<String> fieldsToSelect = new LinkedList<String>();
            // fields we need to select; will be used to set distinct
            fieldsToSelect.add("partyId");
            fieldsToSelect.add("statusId");
            
            
          //To get Assigned Count
            long activeCount = EntityQuery.use(delegator).select(UtilMisc.toSet(fieldsToSelect))
				                .from(dynamicView)
				                .where("partyIdTo",partyId, "roleTypeIdFrom","ACCOUNT","partyRelationshipTypeId","RESPONSIBLE_FOR","statusId","PARTY_ENABLED")
				                .filterByDate()
				                .distinct()
				                .cache(true)
				                .queryCount();
            
          //To get Active Count
            long inactiveCount = EntityQuery.use(delegator).select(UtilMisc.toSet(fieldsToSelect))
				                .from(dynamicView)
				                .where("partyIdTo",partyId, "roleTypeIdFrom","ACCOUNT","partyRelationshipTypeId","RESPONSIBLE_FOR","statusId","PARTY_DISABLED")
				                .filterByDate()
				                .distinct()
				                .cache(true)
				                .queryCount();
			
            
            long myOpenOpportunityCount = EntityQuery.use(delegator).from("SalesOpportunity").where("ownerId", userLoginId, "opportunityStageId","SOSTG_OPEN").cache(true).queryCount();
            
            long myTeamOpportunityCount = EntityQuery.use(delegator).from("SalesOpportunity").where("emplTeamId", emplTeamId, "opportunityStageId","SOSTG_OPEN").cache(true).queryCount();
            /*
            long myOpenSRCount = EntityQuery.use(delegator).from("CustRequest").where("ownerId", partyId, "statusId","SR_OPEN").cache(true).queryCount();
             
            long myTeamOpenSRCount = EntityQuery.use(delegator).from("CustRequest").where("ownerId", partyId, "statusId","SR_OPEN").cache(true).queryCount();
            */
            long myOrderCount = EntityQuery.use(delegator).from("OrderHeader").where("billToPartyId", partyId).cache(true).queryCount();
            
            long myTeamOrderCount = EntityQuery.use(delegator).from("OrderHeader").where("billToPartyId", partyId).cache(true).queryCount();
            
            //List of ACTIVITY type
            List<GenericValue> workEffortType = EntityQuery.use(delegator).select("workEffortTypeId").from("WorkEffortType").where("parentTypeId","ACTIVITY").queryList(); 
            List<String> typeIds = workEffortType.stream().map(map -> map.get("workEffortTypeId").toString()).collect(Collectors.toList());
            
            EntityCondition myActCondition = EntityCondition.makeCondition(EntityOperator.AND,
            								  EntityCondition.makeCondition("cif",EntityOperator.EQUALS,partyId),
            								  EntityCondition.makeCondition("workEffortTypeId",EntityOperator.IN,typeIds)
            								);
            long myActivitiesCount = EntityQuery.use(delegator).from("WorkEffort").where(myActCondition).cache(true).queryCount();
            
            
            EntityCondition myTeamActCondition = EntityCondition.makeCondition(EntityOperator.AND,
												  EntityCondition.makeCondition("emplTeamId",EntityOperator.EQUALS,emplTeamId),
												  EntityCondition.makeCondition("workEffortTypeId",EntityOperator.IN,typeIds)
												);
            long myTeamActivitiesCount = EntityQuery.use(delegator).from("WorkEffort").where(myTeamActCondition).cache(true).queryCount();
            
            result.put("acct-active", activeCount);
            result.put("acct-inactive", inactiveCount);
            result.put("acct-op-oppo", myOpenOpportunityCount);
            result.put("acct-tm-op-oppo", myTeamOpportunityCount);
           // result.put("acct-op-sr", myOpenSRCount);
           // result.put("acct-tm-op-sr", myTeamOpenSRCount);
            result.put("acct-order", myOrderCount);
            result.put("acct-tm-order", myTeamOrderCount);
            result.put("acct-activity", myActivitiesCount);
            result.put("acct-tm-activity", myTeamActivitiesCount);
			
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		return result;
	}
}
