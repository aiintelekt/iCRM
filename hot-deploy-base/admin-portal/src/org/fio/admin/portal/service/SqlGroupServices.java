package org.fio.admin.portal.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
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
public class SqlGroupServices {

	private static final String MODULE = SqlGroupServices.class.getName();
	
	public static Map createSqlGroup(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String sqlGroupName = (String) context.get("sqlGroupName");
		String description = (String) context.get("description");
		String isEnabled = (String) context.get("isEnabled");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			Timestamp currentTime = UtilDateTime.nowTimestamp();
			
			/*if (UtilValidate.isNotEmpty(parentApprovalId) && UtilValidate.isNotEmpty( UtilValidate.isNotEmpty(partyId) ? partyId : ownerPartyId )) {
				String party = UtilValidate.isNotEmpty(partyId) ? partyId : ownerPartyId;
				//GenericValue item = EntityUtil.getFirst( delegator.findByAnd("WorkEffortApproval", UtilMisc.toMap("parentWorkEffortId", parentApprovalId, "partyId", party), null, false) );
				GenericValue item = EntityQuery.use(delegator).select("workEffortId").from("WorkEffortApproval").where("parentWorkEffortId", parentApprovalId, "partyId", party).filterByDate("startDate", "endDate").queryFirst();
				if (UtilValidate.isNotEmpty(item)) {
					result.putAll(ServiceUtil.returnSuccess("Already associated with approval process! partyId: "+party+", Name: "+PartyHelper.getPartyName(delegator, party, false)));
					return result;
				}
			}*/ 
			
			GenericValue sqlGroup = delegator.makeValue("SqlGroup");
			String sqlGroupId = delegator.getNextSeqId("SqlGroup");
			
			sqlGroup.put("sqlGroupId", sqlGroupId);
			sqlGroup.put("sqlGroupName", sqlGroupName);
			sqlGroup.put("description", description);
			sqlGroup.put("isEnabled", isEnabled);
			
			sqlGroup.put("createdByUserLogin", userLogin.getString("userLoginId"));
			
			sqlGroup.create();
			
			result.put("sqlGroupId", sqlGroupId);
		} catch (Exception e) {
			e.printStackTrace();
			
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully created SqlGroup.."));
		return result;
	}
	
	public static Map updateSqlGroup(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String sqlGroupId = (String) context.get("sqlGroupId");
		String sqlGroupName = (String) context.get("sqlGroupName");
		String description = (String) context.get("description");
		String isEnabled = (String) context.get("isEnabled");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			Timestamp currentTime = UtilDateTime.nowTimestamp();
			
			result.put("sqlGroupId", sqlGroupId);

			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("sqlGroupId", EntityOperator.EQUALS, sqlGroupId));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue sqlGroup = EntityUtil.getFirst( delegator.findList("SqlGroup", mainConditons, null, null, null, false) );
			if (UtilValidate.isEmpty(sqlGroup)) {
				result.putAll(ServiceUtil.returnError("SqlGroup not exists!"));
				return result;
			}
			
			sqlGroup.put("sqlGroupName", sqlGroupName);
			sqlGroup.put("description", description);
			sqlGroup.put("isEnabled", isEnabled);
			
			sqlGroup.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
			
			sqlGroup.store();
			
		} catch (Exception e) {
			e.printStackTrace();
			
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully updated SqlGroup.."));
		return result;
	}
	
	public static Map findSqlGroup(DispatchContext dctx, Map context) {
        
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map requestContext = (Map) context.get("requestContext");
        
        String sqlGroupId = (String) requestContext.get("sqlGroupId");
        String sqlGroupName = (String) requestContext.get("sqlGroupName");
        String isEnabled = (String) requestContext.get("isEnabled");
		
        String filterBy = (String) context.get("filterBy");
		String filterType = (String) context.get("filterType");
        
        String orderByColumn = (String) requestContext.get("orderByColumn");
        
        String searchText = (String)requestContext.get("searchText");
        
        List<GenericValue> resultList = new ArrayList<>();
        Map<String, Object> result = new HashMap<String, Object>();
        
        try {
        	String userLoginId = userLogin.getString("userLoginId");
        	String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
        	
        	String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
            
            Map<String, Object> callCtxt = FastMap.newInstance();
            Map<String, Object> callResult = FastMap.newInstance();
            
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
            	
            	if (UtilValidate.isNotEmpty(sqlGroupId)) {
					conditionList.add(EntityCondition.makeCondition("sqlGroupId", EntityOperator.EQUALS, sqlGroupId));
				}
            	
				if (UtilValidate.isNotEmpty(sqlGroupName)) {
					conditionList.add(EntityCondition.makeCondition("sqlGroupName", EntityOperator.LIKE,"%"+sqlGroupName + "%"));
				} else if (UtilValidate.isNotEmpty(searchText)) {
					conditionList.add(EntityCondition.makeCondition("sqlGroupName", EntityOperator.LIKE,"%"+searchText + "%"));
				}
				
				if (UtilValidate.isNotEmpty(isEnabled)) {
					conditionList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, isEnabled));
				}
				
	            EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            Debug.logInfo("findSqlGroup condition: "+mainConditons, MODULE);
	            
	            String orderBy = "createdTxStamp DESC";
	            if (UtilValidate.isNotEmpty(orderByColumn)) {
	            	orderBy = orderByColumn;
	            }
	            
	            DynamicViewEntity dynamicView = new DynamicViewEntity();
	            
	            dynamicView.addMemberEntity("SG", "SqlGroup");
				//dynamicView.addAlias("SG", "sqlGroupId", null, null, null, true, null);
	            dynamicView.addAlias("SG", "sqlGroupId");
				
				dynamicView.addAlias("SG", "sqlGroupName");
				dynamicView.addAlias("SG", "description");
				dynamicView.addAlias("SG", "isEnabled");
				
				dynamicView.addAlias("SG", "createdTxStamp");
				dynamicView.addAlias("SG", "lastUpdatedTxStamp");
				dynamicView.addAlias("SG", "createdByUserLogin");
				dynamicView.addAlias("SG", "lastModifiedByUserLogin");
				
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
		        
		        Debug.logInfo("findSqlGroup start: "+UtilDateTime.nowTimestamp(), MODULE);
				int highIndex = 0;
	            int lowIndex = 0;
	            int resultListSize = 0;
	            try {
	                // get the indexes for the partial list
	            	lowIndex = viewIndex * viewSize + 1;
	                highIndex = (viewIndex + 1) * viewSize;
	                
	                // set distinct on so we only get one row per order
	                // using list iterator
	                EntityListIterator pli=null;
	                
	                pli = EntityQuery.use(delegator)
	                		//.select(fieldsToSelect)
	                        .from(dynamicView)
	                        .where(mainConditons)
	                        .orderBy(UtilMisc.toList(orderBy))
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
	            
	            Debug.logInfo("findSqlGroup end: "+UtilDateTime.nowTimestamp(), MODULE);
	            Debug.logInfo("findSqlGroup count: "+resultList.size(), MODULE);
	            
	            result.put("dataList", resultList);
            }
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        
        result.putAll(ServiceUtil.returnSuccess("Successfully find sql group.."));
        return result;
    }
	
	public static Map findSqlGroupItem(DispatchContext dctx, Map context) {
        
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map requestContext = (Map) context.get("requestContext");
        
        String sqlGroupId = (String) requestContext.get("sqlGroupId");
        String sqlFileName = (String) requestContext.get("sqlFileName");
        String isEnabled = (String) requestContext.get("isEnabled");
		
        String filterBy = (String) context.get("filterBy");
		String filterType = (String) context.get("filterType");
        
        String orderByColumn = (String) requestContext.get("orderByColumn");
        
        String searchText = (String)requestContext.get("searchText");
        
        List<GenericValue> resultList = new ArrayList<>();
        Map<String, Object> result = new HashMap<String, Object>();
        
        try {
        	String userLoginId = userLogin.getString("userLoginId");
        	String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
        	
        	String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
            
            Map<String, Object> callCtxt = FastMap.newInstance();
            Map<String, Object> callResult = FastMap.newInstance();
            
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
            	
            	if (UtilValidate.isNotEmpty(sqlGroupId)) {
					conditionList.add(EntityCondition.makeCondition("sqlGroupId", EntityOperator.EQUALS, sqlGroupId));
				}
            	
				if (UtilValidate.isNotEmpty(sqlFileName)) {
					conditionList.add(EntityCondition.makeCondition("sqlFileName", EntityOperator.LIKE,"%"+sqlFileName + "%"));
				} else if (UtilValidate.isNotEmpty(searchText)) {
					conditionList.add(EntityCondition.makeCondition("sqlFileName", EntityOperator.LIKE,"%"+searchText + "%"));
				}
				
				if (UtilValidate.isNotEmpty(isEnabled)) {
					conditionList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, isEnabled));
				}
				
	            EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            Debug.logInfo("findSqlGroupItem condition: "+mainConditons, MODULE);
	            
	            String orderBy = "createdTxStamp DESC";
	            if (UtilValidate.isNotEmpty(orderByColumn)) {
	            	orderBy = orderByColumn;
	            }
	            
	            DynamicViewEntity dynamicView = new DynamicViewEntity();
	            
	            dynamicView.addMemberEntity("SG", "SqlGroupItem");
				//dynamicView.addAlias("SG", "sqlGroupId", null, null, null, true, null);
	            dynamicView.addAlias("SG", "sqlGroupId");
	            dynamicView.addAlias("SG", "itemId");
				
				dynamicView.addAlias("SG", "sequenceNum");
				dynamicView.addAlias("SG", "sqlFileName");
				dynamicView.addAlias("SG", "path");
				dynamicView.addAlias("SG", "description");
				dynamicView.addAlias("SG", "isEnabled");
				
				dynamicView.addAlias("SG", "createdTxStamp");
				dynamicView.addAlias("SG", "lastUpdatedTxStamp");
				dynamicView.addAlias("SG", "createdByUserLogin");
				dynamicView.addAlias("SG", "lastModifiedByUserLogin");
				
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
		        
		        Debug.logInfo("findSqlGroupItem start: "+UtilDateTime.nowTimestamp(), MODULE);
				int highIndex = 0;
	            int lowIndex = 0;
	            int resultListSize = 0;
	            try {
	                // get the indexes for the partial list
	            	lowIndex = viewIndex * viewSize + 1;
	                highIndex = (viewIndex + 1) * viewSize;
	                
	                // set distinct on so we only get one row per order
	                // using list iterator
	                EntityListIterator pli=null;
	                
	                pli = EntityQuery.use(delegator)
	                		//.select(fieldsToSelect)
	                        .from(dynamicView)
	                        .where(mainConditons)
	                        .orderBy(UtilMisc.toList(orderBy))
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
	            
	            Debug.logInfo("findSqlGroupItem end: "+UtilDateTime.nowTimestamp(), MODULE);
	            Debug.logInfo("findSqlGroupItem count: "+resultList.size(), MODULE);
	            
	            result.put("dataList", resultList);
            }
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        
        result.putAll(ServiceUtil.returnSuccess("Successfully find sql group item.."));
        return result;
    }
	
}
