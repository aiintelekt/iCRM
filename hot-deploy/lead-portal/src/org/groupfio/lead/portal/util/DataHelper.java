/**
 * 
 */
package org.groupfio.lead.portal.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif
 */
public class DataHelper {
	
	private static String MODULE = DataHelper.class.getName();
	
	public static Map<String, Object> prepareHomeKpiInfo(Delegator delegator, GenericValue userLogin) {
		
		Map<String, Object> kpiMetric = new LinkedHashMap<String, Object>();
		
		try {
			if (UtilValidate.isNotEmpty(userLogin)) {
				
				String userLoginId = userLogin.getString("userLoginId");
				String userLoginPartyId = userLogin.getString("partyId");
				String emplTeamId = org.groupfio.common.portal.util.DataUtil.getEmplTeamId(delegator, userLoginPartyId);
				
				long myOpportunity = org.groupfio.common.portal.util.DataHelper.getMyOpportunityCount(delegator, userLoginId);
				long myTeamOpportunity = org.groupfio.common.portal.util.DataHelper.getMyTeamOpportunityCount(delegator, emplTeamId);
				
				kpiMetric.put("myOpportunity", myOpportunity);
				kpiMetric.put("myTeamOpportunity", myTeamOpportunity);				
				
				
			}
		} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
		}
		
		return kpiMetric;
		
	}
	
	//Get the status for the lead for the logged user
	public static Map<String, Object> getLeadDashboardContext(Delegator delegator, GenericValue userLogin) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(UtilValidate.isNotEmpty(userLogin)) {
				String partyId = userLogin.getString("partyId");
				
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
                
                //To get Universe Count
                long universeCount = EntityQuery.use(delegator).select(UtilMisc.toSet(fieldsToSelect))
					                .from(dynamicView)
					                .where("partyIdTo",partyId, "roleTypeIdFrom","LEAD","partyRelationshipTypeId","RESPONSIBLE_FOR","statusId","LEAD_UNIVERSE")
					                .filterByDate()
					                .cursorScrollInsensitive()
					                .distinct()
					                .cache(true)
					                .queryCount();
                
              //To get Suspect Count
                long suspectCount = EntityQuery.use(delegator).select(UtilMisc.toSet(fieldsToSelect))
					                .from(dynamicView)
					                .where("partyIdTo",partyId, "roleTypeIdFrom","LEAD","partyRelationshipTypeId","RESPONSIBLE_FOR","statusId","LEAD_SUSPECT")
					                .filterByDate()
					                .cursorScrollInsensitive()
					                .distinct()
					                .cache(true)
					                .queryCount();
                
              //To get Prospect Count
                long prospectCount = EntityQuery.use(delegator).select(UtilMisc.toSet(fieldsToSelect))
					                .from(dynamicView)
					                .where("partyIdTo",partyId, "roleTypeIdFrom","LEAD","partyRelationshipTypeId","RESPONSIBLE_FOR","statusId","LEAD_PROSPECT")
					                .filterByDate()
					                .cursorScrollInsensitive()
					                .distinct()
					                .cache(true)
					                .queryCount();
                
              //To get Target Count
                long targetCount = EntityQuery.use(delegator).select(UtilMisc.toSet(fieldsToSelect))
					                .from(dynamicView)
					                .where("partyIdTo",partyId, "roleTypeIdFrom","LEAD","partyRelationshipTypeId","RESPONSIBLE_FOR","statusId","LEAD_TARGET")
					                .filterByDate()
					                .cursorScrollInsensitive()
					                .distinct()
					                .cache(true)
					                .queryCount();
                
              //To get Qualified Count
                long qualifiedCount = EntityQuery.use(delegator).select(UtilMisc.toSet(fieldsToSelect))
					                .from(dynamicView)
					                .where("partyIdTo",partyId, "roleTypeIdFrom","LEAD","partyRelationshipTypeId","RESPONSIBLE_FOR","statusId","LEAD_QUALIFIED")
					                .filterByDate()
					                .cursorScrollInsensitive()
					                .distinct()
					                .cache(true)
					                .queryCount();
				
				
                result.put("lead-universe", universeCount);
                result.put("lead-suspect", suspectCount);
                result.put("lead-prospect", prospectCount);
                result.put("lead-target", targetCount);
                result.put("lead-qualified", qualifiedCount);
                Debug.log("lead-dashboard context : "+result, MODULE);
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		return result;
	}
	
	
}
