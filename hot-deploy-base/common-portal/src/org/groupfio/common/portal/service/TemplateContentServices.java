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
public class TemplateContentServices {

	private static final String MODULE = TemplateContentServices.class.getName();
	
	public static Map createTemplateContent(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map contentContext = (Map) context.get("contentContext");

		String templateId = (String) contentContext.get("templateId");
		String tagId = (String) contentContext.get("tagId");
		String contentTitle = (String) contentContext.get("contentTitle");
		String contentText = (String) contentContext.get("contentText");
		String sequenceId = (String) contentContext.get("sequenceId");
		String isDefault = (String) contentContext.get("isDefault");
		
		Timestamp fromDate = (Timestamp) contentContext.get("fromDate");
		Timestamp thruDate = (Timestamp) contentContext.get("thruDate");
		
		String domainEntityType = (String) contentContext.get("domainEntityType");
		String domainEntityId = (String) contentContext.get("domainEntityId");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			GenericValue templateContent = delegator.makeValue("TemplateContent");
			String templateContentId = delegator.getNextSeqId("TemplateContent");
					
			templateContent.put("templateContentId", templateContentId);
			templateContent.put("templateId", templateId);
			templateContent.put("tagId", tagId);
			templateContent.put("contentTitle", contentTitle);
			templateContent.put("contentText", contentText);	
			templateContent.put("sequenceId", sequenceId);	
			templateContent.put("isDefault", isDefault);	
			
			templateContent.put("fromDate", fromDate);
			templateContent.put("thruDate", thruDate);
			
			templateContent.put("domainEntityType", domainEntityType);
			templateContent.put("domainEntityId", domainEntityId);
			
			templateContent.put("createdOn", nowTimestamp);
			templateContent.put("createdByUserLogin", userLogin.getString("userLoginId"));
			
			templateContent.create();
			
			Debug.log("inMap=============="+callCtxt);

			result.put("templateContentId", templateContentId);
		} catch (Exception e) {
			e.printStackTrace();
			
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.putAll(ServiceUtil.returnSuccess("Successfully created Template Content.."));
		return result;
	}
	
	public static Map updateTemplateContent(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map contentContext = (Map) context.get("contentContext");

		String templateContentId = (String) contentContext.get("templateContentId");
		
		String templateId = (String) contentContext.get("templateId");
		String tagId = (String) contentContext.get("tagId");
		String contentTitle = (String) contentContext.get("contentTitle");
		String contentText = (String) contentContext.get("contentText");
		String sequenceId = (String) contentContext.get("sequenceId");
		String isDefault = (String) contentContext.get("isDefault");
		
		Timestamp fromDate = (Timestamp) contentContext.get("fromDate");
		Timestamp thruDate = (Timestamp) contentContext.get("thruDate");
		
		String domainEntityType = (String) contentContext.get("domainEntityType");
		String domainEntityId = (String) contentContext.get("domainEntityId");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			Timestamp currentTime = UtilDateTime.nowTimestamp();
			List<String> ownerList = new ArrayList<>();
			
			result.put("templateContentId", templateContentId);
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("templateContentId", EntityOperator.EQUALS, templateContentId));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue templateContent = EntityUtil.getFirst( delegator.findList("TemplateContent", mainConditons, null, null, null, false) );
			if (UtilValidate.isEmpty(templateContent)) {
				result.putAll(ServiceUtil.returnError("Template Content not exists!"));
				return result;
			}
			
			if (UtilValidate.isEmpty(sequenceId)) {
				sequenceId = "1000";
			}
			
			templateContent.put("templateId", templateId);
			templateContent.put("tagId", tagId);
			templateContent.put("contentTitle", contentTitle);
			templateContent.put("contentText", contentText);	
			templateContent.put("sequenceId", sequenceId);	
			templateContent.put("isDefault", isDefault);	
			
			templateContent.put("fromDate", fromDate);
			templateContent.put("thruDate", thruDate);
			
			templateContent.put("domainEntityType", domainEntityType);
			templateContent.put("domainEntityId", domainEntityId);
			
			templateContent.put("modifiedOn", nowTimestamp);
			templateContent.put("modifiedByUserLogin", userLogin.getString("userLoginId"));
			
			templateContent.store();
			
			Debug.log("inMap=============="+callCtxt);

			result.put("templateContentId", templateContentId);
		} catch (Exception e) {
			e.printStackTrace();
			
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.putAll(ServiceUtil.returnSuccess("Successfully updated Template Content.."));
		return result;
	}
	
	public static Map findTemplateContent(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map requestContext = (Map) context.get("requestContext");
        
        String templateContentId = (String) requestContext.get("templateContentId");
        String templateId = (String) requestContext.get("templateId");
        String tagId = (String) requestContext.get("tagId");
        String isDefault = (String) requestContext.get("isDefault");
        String isActivieRecord = (String) requestContext.get("isActivieRecord");
        
		String fromDate = (String) requestContext.get("fromDate");
		String thruDate = (String) requestContext.get("thruDate");
		
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
            	
            	if (UtilValidate.isNotEmpty(templateContentId)) {
					conditionList.add(EntityCondition.makeCondition("templateContentId", EntityOperator.EQUALS, templateContentId));
				}
            	
				if (UtilValidate.isNotEmpty(templateId)) {
					conditionList.add(EntityCondition.makeCondition("templateId", EntityOperator.EQUALS, templateId));
				}
				
				if (UtilValidate.isNotEmpty(tagId)) {
					conditionList.add(EntityCondition.makeCondition("tagId", EntityOperator.EQUALS, tagId));
				}
				
				if (UtilValidate.isNotEmpty(isDefault)) {
					conditionList.add(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, isDefault));
				}
				
				if(UtilValidate.isNotEmpty(fromDate)) {
					Timestamp sd = UtilDateTime.stringToTimeStamp(fromDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(sd)));
				}
				if (UtilValidate.isNotEmpty(thruDate)) {
					Timestamp ed = UtilDateTime.stringToTimeStamp(thruDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(ed)));
				}
				if (UtilValidate.isNotEmpty(isActivieRecord) && isActivieRecord.equals("Y")) {
					conditionList.add(EntityUtil.getFilterByDateExpr("fromDate", "thruDate"));
				}
				
				if (UtilValidate.isNotEmpty(domainEntityId)) {
					conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
				}
				if (UtilValidate.isNotEmpty(domainEntityType)) {
					conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
				}
				
	            EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            Debug.logInfo("findTemplateContent condition: "+mainConditons, MODULE);
	            
	            String orderBy = "createdOn DESC";
	            if (UtilValidate.isNotEmpty(orderByColumn)) {
	            	orderBy = orderByColumn;
	            }
	            
	            DynamicViewEntity dynamicView = new DynamicViewEntity();
	            
	            dynamicView.addMemberEntity("TC", "TemplateContent");
				dynamicView.addAlias("TC", "templateContentId", null, null, null, true, null);
				
				dynamicView.addAlias("TC", "templateId");
				dynamicView.addAlias("TC", "tagId");
				dynamicView.addAlias("TC", "contentTitle");
				dynamicView.addAlias("TC", "contentText");
				dynamicView.addAlias("TC", "sequenceId");
				dynamicView.addAlias("TC", "isDefault");
				dynamicView.addAlias("TC", "fromDate");
				dynamicView.addAlias("TC", "thruDate");
				
				dynamicView.addAlias("TC", "domainEntityId");
				dynamicView.addAlias("TC", "domainEntityType");
				
				dynamicView.addAlias("TC", "createdOn");
				dynamicView.addAlias("TC", "modifiedOn");
				dynamicView.addAlias("TC", "createdByUserLogin");
				dynamicView.addAlias("TC", "modifiedByUserLogin");
				
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
		        
		        Debug.logInfo("findTemplateContent start: "+UtilDateTime.nowTimestamp(), MODULE);
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
	            
	            Debug.logInfo("findTemplateContent end: "+UtilDateTime.nowTimestamp(), MODULE);
	            Debug.logInfo("findTemplateContent count: "+resultList.size(), MODULE);
	            
	            result.put("dataList", resultList);
            }
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        result.putAll(ServiceUtil.returnSuccess("Successfully find template content.."));
        return result;
    }
	
}
