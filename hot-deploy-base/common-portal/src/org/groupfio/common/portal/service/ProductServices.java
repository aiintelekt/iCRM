/**
 * 
 */
package org.groupfio.common.portal.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class ProductServices {

	private static final String MODULE = ProductServices.class.getName();
	
	public static Map findProductStore(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map requestContext = (Map) context.get("requestContext");
        
        String productStoreId = (String) requestContext.get("productStoreId");
        String isStoreReceipt = (String) requestContext.get("isStoreReceipt");
        
		String domainEntityId = (String) context.get("domainEntityId");
		String domainEntityType = (String) context.get("domainEntityType");
		
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
            	
            	if (UtilValidate.isNotEmpty(productStoreId)) {
					conditionList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, Arrays.asList(productStoreId.split(","))));
				}
            	
            	if (UtilValidate.isNotEmpty(isStoreReceipt) && isStoreReceipt.equals("Y")) {
    				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
    						EntityCondition.makeCondition("attrName1", EntityOperator.EQUALS, "productStoreId"),
    						EntityCondition.makeCondition("attrValue1", EntityOperator.NOT_EQUAL, null)
    						));
    			}
            	
	            EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            Debug.logInfo("findProductStore condition: "+mainConditons, MODULE);
	            
	            String orderBy = "createdStamp DESC";
	            if (UtilValidate.isNotEmpty(orderByColumn)) {
	            	orderBy = orderByColumn;
	            }
	            
	            DynamicViewEntity dynamicView = new DynamicViewEntity();
	            
	            dynamicView.addMemberEntity("PS", "ProductStore");
				dynamicView.addAlias("PS", "productStoreId", null, null, null, true, null);
				
				dynamicView.addAlias("PS", "primaryStoreGroupId");
				dynamicView.addAlias("PS", "storeName");
				dynamicView.addAlias("PS", "companyName");
				dynamicView.addAlias("PS", "title");
				dynamicView.addAlias("PS", "subtitle");
				dynamicView.addAlias("PS", "isDemoStore");
				dynamicView.addAlias("PS", "storePrefix");
				dynamicView.addAlias("PS", "orderNumberPrefix");
				dynamicView.addAlias("PS", "wwLocationId");
				dynamicView.addAlias("PS", "stateProvinceGeoId");
				
				dynamicView.addAlias("PS", "createdStamp");
				dynamicView.addAlias("PS", "lastUpdatedStamp");
				
				if (UtilValidate.isNotEmpty(isStoreReceipt) && isStoreReceipt.equals("Y")) {
					dynamicView.addMemberEntity("ATTR1", "StoreAttribute");
					dynamicView.addAlias("ATTR1", "attrName1","attrName",null, false, false, null);
					dynamicView.addAlias("ATTR1", "attrValue1","attrValue",null, false, false, null);
					
					dynamicView.addViewLink("PS", "ATTR1", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productStoreId"));
				}
				
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
		        
		        Debug.logInfo("findProductStore start: "+UtilDateTime.nowTimestamp(), MODULE);
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
	            
	            Debug.logInfo("findProductStore end: "+UtilDateTime.nowTimestamp(), MODULE);
	            Debug.logInfo("findProductStore count: "+resultList.size(), MODULE);
	            
	            result.put("dataList", resultList);
            }
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        result.putAll(ServiceUtil.returnSuccess("Successfully find product store.."));
        return result;
    }
	
}
