/**
 * 
 */
package org.groupfio.common.portal.service;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class ExportServices {
	
	private static final String MODULE = ExportServices.class.getName();

	public static Map findExportFile(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map requestContext = (Map) context.get("requestContext");
        
        String exportType = (String) requestContext.get("exportType");
        
		String startDate = (String) requestContext.get("startDate");
		String endDate = (String) requestContext.get("endDate");
		String statusId = (String) requestContext.get("statusId");
		
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
            	
            	if (UtilValidate.isNotEmpty(exportType)) {
					conditionList.add(EntityCondition.makeCondition("exportType", EntityOperator.EQUALS, exportType));
				}
            	if (UtilValidate.isNotEmpty(statusId)) {
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
				}
            	
				if(UtilValidate.isNotEmpty(startDate)) {
					Timestamp sd = UtilDateTime.stringToTimeStamp(startDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					conditionList.add(EntityCondition.makeCondition("startDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(sd)));
				}
				if (UtilValidate.isNotEmpty(endDate)) {
					Timestamp ed = UtilDateTime.stringToTimeStamp(endDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					conditionList.add(EntityCondition.makeCondition("finishDateTime", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(ed)));
				}
				
	            EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            Debug.logInfo("findExportFile condition: "+mainConditons, MODULE);
	            
	            String orderBy = "jobId DESC";
	            if (UtilValidate.isNotEmpty(orderByColumn)) {
	            	orderBy = orderByColumn;
	            }
	            
	            DynamicViewEntity dynamicView = new DynamicViewEntity();
	            
	            dynamicView.addMemberEntity("EJS", "ExportJobSandbox");
				dynamicView.addAlias("EJS", "jobId", null, null, null, true, null);
				
				dynamicView.addAlias("EJS", "jobName");
				dynamicView.addAlias("EJS", "startDateTime");
				dynamicView.addAlias("EJS", "finishDateTime");
				dynamicView.addAlias("EJS", "cancelDateTime");
				dynamicView.addAlias("EJS", "errorMessage");
				dynamicView.addAlias("EJS", "exportType");
				dynamicView.addAlias("EJS", "reportType");
				dynamicView.addAlias("EJS", "genFileName");
				dynamicView.addAlias("EJS", "isEmailSend");
				dynamicView.addAlias("EJS", "reportCount");
				dynamicView.addAlias("EJS", "statusId");
				dynamicView.addAlias("EJS", "delimiter");
				dynamicView.addAlias("EJS", "fileTemplateId");
				dynamicView.addAlias("EJS", "createdByUserLogin");
				dynamicView.addAlias("EJS", "maxRetry");
				
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
		        
		        Debug.logInfo("findExportFile start: "+UtilDateTime.nowTimestamp(), MODULE);
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
	            
	            Debug.logInfo("findExportFile end: "+UtilDateTime.nowTimestamp(), MODULE);
	            Debug.logInfo("findExportFile count: "+resultList.size(), MODULE);
	            
	            result.put("dataList", resultList);
            }
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        
        result.putAll(ServiceUtil.returnSuccess("Successfully find export file.."));
        return result;
    }
	
}
