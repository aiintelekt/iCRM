package org.fio.admin.portal.event;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif Ul Islam
 * 
 */
public final class SqlGroupEvents {

    private static final String MODULE = SqlGroupEvents.class.getName();
    
    public static String searchSqlGroups(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map < String, Object > context = UtilHttp.getCombinedMap(request);

		String externalLoginKey = request.getParameter("externalLoginKey");
		
		String searchText = request.getParameter("searchText");
		String orderByColumn = (String) context.get("orderByColumn");

		//String partyId = (String) context.get("partyId");
		
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		
		long start = System.currentTimeMillis();
		int viewIndex = 0;
		int highIndex = 0;
		int lowIndex = 0;
		int resultListSize = 0;
		int viewSize = 0;
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
			
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
				
				Map<String, Object> callCtxt = FastMap.newInstance();
	            Map<String, Object> callResult = FastMap.newInstance();
	            
	            Map<String, Object> requestContext = new LinkedHashMap<>();
				requestContext.putAll(context);
	            
	            callCtxt.put("userLogin", userLogin);
	            
	            requestContext.put("totalGridFetch",
						org.fio.homeapps.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));
	            if (UtilValidate.isEmpty(orderByColumn)) {
	            	requestContext.put("orderByColumn", orderByColumn);
	            }
	            
	            callCtxt.put("requestContext", requestContext);
				
	            callResult = dispatcher.runSync("admin.findSqlGroup", callCtxt);

				if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("dataList"))) {

					List<GenericValue> templateList = (List<GenericValue>) callResult.get("dataList");
					
					Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					//Map<String, Object> categoryList = EnumUtil.getEnumList(delegator, templateList, "approvalCategoryId", "APPROVAL_CATEGORY");
					
					Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, templateList, "createdByUserLogin");
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, templateList, "lastModifiedByUserLogin");
					
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					viewIndex = (int) callResult.get("viewIndex");
					highIndex = (int) callResult.get("highIndex");
					lowIndex = (int) callResult.get("lowIndex");
					resultListSize = (int) callResult.get("resultListSize");
					viewSize = (int) callResult.get("viewSize");
					
					int count = 0;
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					for (GenericValue template : templateList) {
						//Debug.logInfo("data "+(count++)+": "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						Map<String, Object> data = new HashMap<String, Object>();
						
						data.put("sqlGroupId", template.getString("sqlGroupId"));
						data.put("sqlGroupName", template.getString("sqlGroupName"));
						data.put("description", template.getString("description"));
						data.put("isEnabled", template.getString("isEnabled"));
						
						String createdDate = "";
						if (UtilValidate.isNotEmpty(template.getString("createdTxStamp"))) {
							createdDate = UtilDateTime.timeStampToString(template.getTimestamp("createdTxStamp"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("createdOn", createdDate);
						data.put("createdByName", partyNames.get(template.getString("createdByUserLogin")));
						
						String modifiedDate = "";
						if (UtilValidate.isNotEmpty(template.getString("lastUpdatedTxStamp"))) {
							modifiedDate = UtilDateTime.timeStampToString(template.getTimestamp("lastUpdatedTxStamp"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("modifiedOn", modifiedDate);
						data.put("modifiedByName", partyNames.get(template.getString("lastModifiedByUserLogin")));
						
						data.put("externalLoginKey", externalLoginKey);
						
						dataList.add(data);
						
					}
					Debug.logInfo("prepare actual data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
				}
			} else {
				Debug.log("error==");
				Map<String, Object> data = new HashMap<String, Object>();
				if(UtilValidate.isNotEmpty(accessMatrixRes) && !ServiceUtil.isSuccess(accessMatrixRes)) {
					data.put("errorMessage", accessMatrixRes.get("errorMessage").toString());
				} else {
					data.put("errorMessage", "Access Denied");
				}
				dataList.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		
		if (UtilValidate.isNotEmpty(searchText)) {
			return AjaxEvents.doJSONResponse(response, dataList);
		}
		result.put("highIndex", Integer.valueOf(highIndex));
		result.put("lowIndex", Integer.valueOf(lowIndex));
		result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
		result.put("totalRecords", nf.format(resultListSize));
		result.put("recordCount", resultListSize);
		result.put("chunkSize", viewSize);
		result.put("viewSize", viewSize);
		result.put("viewIndex", viewIndex);
		
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
		result.put("timeTaken", (end - start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return AjaxEvents.doJSONResponse(response, result);
	}
    
    public static String searchSqlGroupItems(HttpServletRequest request, HttpServletResponse response) {
    	
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map < String, Object > context = UtilHttp.getCombinedMap(request);

		String externalLoginKey = request.getParameter("externalLoginKey");
		
		String searchText = request.getParameter("searchText");
		String orderByColumn = (String) context.get("orderByColumn");

		String sqlGroupId = (String) context.get("sqlGroupId");
		
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		
		long start = System.currentTimeMillis();
		int viewIndex = 0;
		int highIndex = 0;
		int lowIndex = 0;
		int resultListSize = 0;
		int viewSize = 0;
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
			
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
				
				Map<String, Object> callCtxt = FastMap.newInstance();
	            Map<String, Object> callResult = FastMap.newInstance();
	            
	            Map<String, Object> requestContext = new LinkedHashMap<>();
				requestContext.putAll(context);
	            
	            callCtxt.put("userLogin", userLogin);
	            
	            requestContext.put("totalGridFetch",
						org.fio.homeapps.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));
	            if (UtilValidate.isEmpty(orderByColumn)) {
	            	requestContext.put("orderByColumn", orderByColumn);
	            }
	            
	            callCtxt.put("requestContext", requestContext);
				
	            callResult = dispatcher.runSync("admin.findSqlGroupItem", callCtxt);

				if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("dataList"))) {

					List<GenericValue> templateList = (List<GenericValue>) callResult.get("dataList");
					
					Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					//Map<String, Object> categoryList = EnumUtil.getEnumList(delegator, templateList, "approvalCategoryId", "APPROVAL_CATEGORY");
					
					Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, templateList, "createdByUserLogin");
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, templateList, "lastModifiedByUserLogin");
					
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					viewIndex = (int) callResult.get("viewIndex");
					highIndex = (int) callResult.get("highIndex");
					lowIndex = (int) callResult.get("lowIndex");
					resultListSize = (int) callResult.get("resultListSize");
					viewSize = (int) callResult.get("viewSize");
					
					int count = 0;
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					for (GenericValue template : templateList) {
						//Debug.logInfo("data "+(count++)+": "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						Map<String, Object> data = new HashMap<String, Object>();
						
						data.put("sqlGroupId", template.getString("sqlGroupId"));
						data.put("itemId", template.getString("itemId"));
						data.put("sequenceNum", template.getString("sequenceNum"));
						data.put("sqlFileName", template.getString("sqlFileName"));
						data.put("path", template.getString("path"));
						data.put("description", template.getString("description"));
						data.put("isEnabled", template.getString("isEnabled"));
						
						String createdDate = "";
						if (UtilValidate.isNotEmpty(template.getString("createdTxStamp"))) {
							createdDate = UtilDateTime.timeStampToString(template.getTimestamp("createdTxStamp"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("createdOn", createdDate);
						data.put("createdByName", partyNames.get(template.getString("createdByUserLogin")));
						
						String modifiedDate = "";
						if (UtilValidate.isNotEmpty(template.getString("lastUpdatedTxStamp"))) {
							modifiedDate = UtilDateTime.timeStampToString(template.getTimestamp("lastUpdatedTxStamp"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("modifiedOn", modifiedDate);
						data.put("modifiedByName", partyNames.get(template.getString("lastModifiedByUserLogin")));
						
						data.put("externalLoginKey", externalLoginKey);
						
						dataList.add(data);
						
					}
					Debug.logInfo("prepare actual data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
				}
			} else {
				Debug.log("error==");
				Map<String, Object> data = new HashMap<String, Object>();
				if(UtilValidate.isNotEmpty(accessMatrixRes) && !ServiceUtil.isSuccess(accessMatrixRes)) {
					data.put("errorMessage", accessMatrixRes.get("errorMessage").toString());
				} else {
					data.put("errorMessage", "Access Denied");
				}
				dataList.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		
		if (UtilValidate.isNotEmpty(searchText)) {
			return AjaxEvents.doJSONResponse(response, dataList);
		}
		result.put("highIndex", Integer.valueOf(highIndex));
		result.put("lowIndex", Integer.valueOf(lowIndex));
		result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
		result.put("totalRecords", nf.format(resultListSize));
		result.put("recordCount", resultListSize);
		result.put("chunkSize", viewSize);
		result.put("viewSize", viewSize);
		result.put("viewIndex", viewIndex);
		
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
		result.put("timeTaken", (end - start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return AjaxEvents.doJSONResponse(response, result);
	}

    public static String createSqlGrpItem(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String sqlGroupId = request.getParameter("sqlGroupId");
		
		String path = request.getParameter("path");
		String description = request.getParameter("description");
		String isEnabled = request.getParameter("isEnabled");
		String sequenceNum = request.getParameter("sequenceNum");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String nativeBusinessUnit = null;
			String nativeTeamId = null;
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("sqlGroupId", EntityOperator.EQUALS, sqlGroupId));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue sqlGroup = EntityQuery.use(delegator).from("SqlGroup").where(mainConditons).queryFirst();
			if (UtilValidate.isEmpty(sqlGroup)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "SqlGroup not exists!");
				return AjaxEvents.doJSONResponse(response, result);
			}
			
			GenericValue sqlGroupItem = delegator.makeValue("SqlGroupItem");
			String itemId = delegator.getNextSeqId("SqlGroupItem");
			
			sqlGroupItem.put("sqlGroupId", sqlGroupId);
			sqlGroupItem.put("itemId", itemId);
			sqlGroupItem.put("path", path);
			sqlGroupItem.put("description", description);
			sqlGroupItem.put("isEnabled", isEnabled);
			sqlGroupItem.put("sequenceNum", sequenceNum);
			
			sqlGroupItem.put("createdByUserLogin", userLogin.getString("userLoginId"));
			
			sqlGroupItem.create();
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully created Item");
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
    
    public static String updateSqlGrpItem(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String sqlGroupId = request.getParameter("sqlGroupId");
		String itemId = request.getParameter("itemId");
		
		String path = request.getParameter("path");
		String description = request.getParameter("description");
		String isEnabled = request.getParameter("isEnabled");
		String sequenceNum = request.getParameter("sequenceNum");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String nativeBusinessUnit = null;
			String nativeTeamId = null;
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("sqlGroupId", EntityOperator.EQUALS, sqlGroupId));
			conditionList.add(EntityCondition.makeCondition("itemId", EntityOperator.EQUALS, itemId));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue sqlGroupItem = EntityQuery.use(delegator).from("SqlGroupItem").where(mainConditons).queryFirst();
			if (UtilValidate.isEmpty(sqlGroupItem)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "SqlGroup Item not exists!");
				return AjaxEvents.doJSONResponse(response, result);
			}
			
			sqlGroupItem.put("path", path);
			sqlGroupItem.put("description", description);
			sqlGroupItem.put("isEnabled", isEnabled);
			sqlGroupItem.put("sequenceNum", sequenceNum);
			
			sqlGroupItem.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
			
			sqlGroupItem.store();
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully updated Item");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
    
    public static String getSqlGrpItemData(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String sqlGroupId = request.getParameter("sqlGroupId");
		String itemId = request.getParameter("itemId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("sqlGroupId", EntityOperator.EQUALS, sqlGroupId));
			conditionList.add(EntityCondition.makeCondition("itemId", EntityOperator.EQUALS, itemId));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue sqlGroupItem = EntityQuery.use(delegator).from("SqlGroupItem").where(mainConditons).queryFirst();
			if (UtilValidate.isEmpty(sqlGroupItem)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Item not exists!");
				return AjaxEvents.doJSONResponse(response, result);
			}
			
			Map<String, Object> data = FastMap.newInstance();
			
			data.putAll(sqlGroupItem.getAllFields());
			
			result.put("data", data);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
    
    public static String removeSqlGrpItem(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String selectedItemIds = request.getParameter("selectedItemIds");
		
		Map<String, Object> result = FastMap.newInstance();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		try {
			if (UtilValidate.isNotEmpty(selectedItemIds)) {
				List<GenericValue> tobeRemove = new ArrayList<>();
				for (String itemId : selectedItemIds.split(",")) {
					GenericValue item = EntityQuery.use(delegator).from("SqlGroupItem").where("itemId", itemId).queryFirst();
					if (UtilValidate.isNotEmpty(item)) {
						tobeRemove.add(item);
					}
				}
				
				if (UtilValidate.isNotEmpty(tobeRemove)) {
					delegator.removeAll(tobeRemove);
				}

				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(ModelService.SUCCESS_MESSAGE, "Items removed successfully.");
			}
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
    
    public static String executeSqlGrpItemSingle(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String sqlGroupId = request.getParameter("sqlGroupId");
		String itemId = request.getParameter("itemId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("sqlGroupId", EntityOperator.EQUALS, sqlGroupId));
			conditionList.add(EntityCondition.makeCondition("itemId", EntityOperator.EQUALS, itemId));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue sqlGroupItem = EntityQuery.use(delegator).from("SqlGroupItem").where(mainConditons).queryFirst();
			if (UtilValidate.isEmpty(sqlGroupItem)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Item not exists!");
				return AjaxEvents.doJSONResponse(response, result);
			}
			
			
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
    
    public static String executeSqlGrpItemAll(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String sqlGroupId = request.getParameter("sqlGroupId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("sqlGroupId", EntityOperator.EQUALS, sqlGroupId));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue sqlGroup = EntityQuery.use(delegator).from("SqlGroup").where(mainConditons).queryFirst();
			if (UtilValidate.isEmpty(sqlGroup)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "SqlGroup not exists!");
				return AjaxEvents.doJSONResponse(response, result);
			}
			
			conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("sqlGroupId", EntityOperator.EQUALS, sqlGroupId));
			conditionList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
			mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> items = EntityQuery.use(delegator).from("SqlGroupItem").where(mainConditons).orderBy("sequenceNum").queryList();
			if (UtilValidate.isEmpty(items)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "No active SqlGroup items!");
				return AjaxEvents.doJSONResponse(response, result);
			}
			
			
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
    
    public static String executeSqlGrpItemSelected(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String selectedItemIds = request.getParameter("selectedItemIds");
		
		Map<String, Object> result = FastMap.newInstance();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		try {
			if (UtilValidate.isNotEmpty(selectedItemIds)) {
				GenericHelperInfo ghi = delegator.getGroupHelperInfo("org.ofbiz");
	            SQLProcessor sqlProcessor = new SQLProcessor(delegator, ghi);
				ResultSet rs = null;
				
				for (String itemId : selectedItemIds.split(",")) {
					GenericValue item = EntityQuery.use(delegator).from("SqlGroupItem").where("itemId", itemId).queryFirst();
					if (UtilValidate.isNotEmpty(item)) {
						File sqlFile = new File(item.getString("path"));
						if (UtilValidate.isNotEmpty(sqlFile)) {
							String sqlQeury = FileUtil.readString("UTF-8", sqlFile);
							sqlProcessor.executeUpdate(sqlQeury);
							
						}
					}
				}
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(ModelService.SUCCESS_MESSAGE, "Items executed successfully.");
			}
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
    
}
