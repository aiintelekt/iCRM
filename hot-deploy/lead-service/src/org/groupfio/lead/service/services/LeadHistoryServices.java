/**
 * 
 */
package org.groupfio.lead.service.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class LeadHistoryServices {

	private static final String MODULE = LeadHistoryServices.class.getName();

	public static Map createLeadHistory(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String partyId = (String) context.get("partyId");
		
		Map historyContext = (Map) context.get("historyContext");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			
			EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			GenericValue party = EntityQuery.use(delegator).from("Party").where(mainConditon).queryFirst();
			
			if (UtilValidate.isEmpty(party)) {
				result.putAll(ServiceUtil.returnError("Lean history creation failed, not found any Lead# "+partyId));
				return result;
			}
			
			GenericValue partyGroup = EntityQuery.use(delegator).from("PartyGroup").where(mainConditon).queryFirst();
			GenericValue supplementalData = EntityQuery.use(delegator).from("PartySupplementalData").where(mainConditon).queryFirst();
			GenericValue personResponsible = PartyHelper.getCurrentResponsibleParty(partyId, "LEAD", delegator);
			
			GenericValue leadHistory = delegator.makeValue("LeadHistory");
			String leadHistoryId = delegator.getNextSeqId("LeadHistory");
			
			leadHistory.put("leadHistoryId", leadHistoryId);
			leadHistory.put("partyId", partyId);
			
			leadHistory.put("leadName", partyGroup.get("groupName"));
			leadHistory.put("groupNameLocal", partyGroup.get("groupNameLocal"));
			leadHistory.put("description", party.get("description"));
			leadHistory.put("statusId", party.get("statusId"));
			leadHistory.put("timeZoneId", party.get("timeZoneDesc"));
			leadHistory.put("currencyUomId", party.get("preferredCurrencyUomId"));
			
			if (UtilValidate.isNotEmpty(personResponsible)) {
				leadHistory.put("personResponsibleId", personResponsible.get("partyId"));
			}
			
			if (UtilValidate.isNotEmpty(supplementalData)) {
				leadHistory.put("industryEnumId", supplementalData.get("industryEnumId"));
				leadHistory.put("sicCode", supplementalData.get("sicCode"));
				leadHistory.put("ownershipEnumId", supplementalData.get("ownershipEnumId"));
				leadHistory.put("annualRevenue", supplementalData.get("annualRevenue"));
				leadHistory.put("numberEmployees", supplementalData.get("numberEmployees"));
				leadHistory.put("tickerSymbol", supplementalData.get("tickerSymbol"));
			}
			
			leadHistory.put("createdDate", party.get("createdDate"));
			leadHistory.put("createdByUserLogin", party.get("createdByUserLogin"));
			leadHistory.put("lastModifiedDate", party.get("lastModifiedDate"));
			leadHistory.put("lastModifiedByUserLogin", party.get("lastModifiedByUserLogin"));
			
			leadHistory.create();
			
			result.put("leadHistoryId", leadHistoryId);
		} catch (Exception e) {
			//e.printStackTrace();
			
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.putAll(ServiceUtil.returnSuccess("Successfully created Lead History.."));
		return result;
	}
	
	public static Map findLeadHistory(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map requestContext = (Map) context.get("requestContext");
        
        String partyId = (String) context.get("partyId");
        
        String orderByColumn = (String) requestContext.get("orderByColumn");
        
        List<GenericValue> resultList = new ArrayList<>();
        Map<String, Object> result = new HashMap<String, Object>();
        
        try {
        	String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
            
            Map<String, Object> callCtxt = FastMap.newInstance();
            Map<String, Object> callResult = FastMap.newInstance();
            
            String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			
			/*String businessUnit = null;
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "Read");
				accessMatrixMap.put("entityName", "WorkEffort");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
			}*/
            
            if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
            
            	List<EntityCondition> conditionList = FastList.newInstance();
            	  
				if (UtilValidate.isNotEmpty(partyId)) {
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE,"%"+partyId + "%"));
				}
				
	            EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            Debug.logInfo("findLeadHistory condition: "+mainConditons, MODULE);
	            
	            String orderBy = "createdStamp";
	            if (UtilValidate.isNotEmpty(orderByColumn)) {
	            	orderBy = orderByColumn;
	            }
	            
	            DynamicViewEntity dynamicView = new DynamicViewEntity();
	            
	            dynamicView.addMemberEntity("LH", "LeadHistory");
	            
	            dynamicView.addAlias("LH", "leadHistoryId");
				dynamicView.addAlias("LH", "partyId", null, null, null, false, null);
				
				dynamicView.addAlias("LH", "leadName");
				dynamicView.addAlias("LH", "personResponsibleId");
				dynamicView.addAlias("LH", "groupNameLocal");
				dynamicView.addAlias("LH", "industryEnumId");
				dynamicView.addAlias("LH", "sicCode");
				dynamicView.addAlias("LH", "ownershipEnumId");
				dynamicView.addAlias("LH", "timeZoneId");
				dynamicView.addAlias("LH", "annualRevenue");
				dynamicView.addAlias("LH", "numberEmployees");
				dynamicView.addAlias("LH", "currencyUomId");
				dynamicView.addAlias("LH", "tickerSymbol");
				dynamicView.addAlias("LH", "description");
				dynamicView.addAlias("LH", "statusId");
				
				dynamicView.addAlias("LH", "createdDate");
				dynamicView.addAlias("LH", "lastModifiedDate");
				dynamicView.addAlias("LH", "createdByUserLogin");
				dynamicView.addAlias("LH", "lastModifiedByUserLogin");
				dynamicView.addAlias("LH", "createdStamp");
				
	            int viewIndex = 0;
		        try {
		            viewIndex = Integer.parseInt((String) requestContext.get("VIEW_INDEX"));
		        } catch (Exception e) {
		            viewIndex = 0;
		        }
		        
		        int fioGridFetch = UtilValidate.isNotEmpty(requestContext.get("totalGridFetch")) ? Integer.parseInt((String) requestContext.get("totalGridFetch")) : 1000;
		        
		        int viewSize = fioGridFetch;
		        try {
		            viewSize = Integer.parseInt((String) requestContext.get("VIEW_SIZE"));
		        } catch (Exception e) {
		            viewSize = fioGridFetch;
		        }
		        
		        Debug.logInfo("findLeadHistory start: "+UtilDateTime.nowTimestamp(), MODULE);
				int highIndex = 0;
	            int lowIndex = 0;
	            int resultListSize = 0;
	            try {
	                // get the indexes for the partial list
	            	lowIndex = viewIndex * viewSize + 1;
	                highIndex = (viewIndex + 1) * viewSize;
	                
	                // set distinct on so we only get one row per order
	                // using list iterator
	                EntityListIterator pli = EntityQuery.use(delegator)
	                		//.select(fieldsToSelect)
	                        .from(dynamicView)
	                        .where(mainConditons)
	                        .orderBy(UtilMisc.toList(orderBy+" DESC"))
	                        .cursorScrollInsensitive()
	                        .fetchSize(highIndex)
	                        //.distinct()
	                        .cache(false)
	                        .queryIterator();
	                // get the partial list for this page
	                resultList = pli.getPartialList(lowIndex, viewSize);

	                // attempt to get the full size
	                resultListSize = pli.getResultsSizeAfterPartialList();
	                // close the list iterator
	                pli.close();
	                
	                result.put("viewIndex", Integer.valueOf(viewIndex));
	                result.put("highIndex", Integer.valueOf(highIndex));
			        result.put("lowIndex", Integer.valueOf(lowIndex));
			        result.put("viewSize", viewSize);
			        result.put("resultListSize", resultListSize);
			        
	            } catch (GenericEntityException e) {
	                String errMsg = "Error: " + e.toString();
	                Debug.logError(e, errMsg, MODULE);
	            }
	            
	            Debug.logInfo("findLeadHistory end: "+UtilDateTime.nowTimestamp(), MODULE);
	            Debug.logInfo("findLeadHistory count: "+resultList.size(), MODULE);
	            
	            result.put("dataList", resultList);
            }
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        
        result.putAll(ServiceUtil.returnSuccess("Successfully find rebate history.."));
        return result;
    }
	
	public static Map createLeadEventHistory(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Map historyContext = (Map) context.get("requestContext");
		try {
			GenericValue leadEventHistory = delegator.makeValue("LeadEventHistory");
			String leadEventHistoryId = delegator.getNextSeqId("LeadEventHistory");
	
			leadEventHistory.set("historyId", leadEventHistoryId);
			leadEventHistory.setNonPKFields(historyContext);
	
			Debug.log("leadEventHistory==== "+leadEventHistory);
	
			leadEventHistory.create();
		}catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		//result.put("historyId", agmtEventHistoryId);
		result.putAll(ServiceUtil.returnSuccess("Successfully Created Lead Event History.."));
		return result;
	}
	
}
