/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.groupfio.common.portal.event;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.util.UtilAttribute;
import org.groupfio.common.portal.util.UtilCampaign;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
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
public final class ProductEvents {

    private static final String MODULE = ProductEvents.class.getName();
    
    public static String searchProductStores(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String externalLoginKey = request.getParameter("externalLoginKey");
		
		String isStoreReceipt = request.getParameter("isStoreReceipt");
		String isFullLoad = request.getParameter("isFullLoad");
		
		String searchText = request.getParameter("searchText");
		String orderByColumn = (String) context.get("orderByColumn");
		
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
			String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);
			
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
	            boolean isActiveSearch = true;
	            
	            Map<String, Object> requestContext = new LinkedHashMap<>();
				requestContext.putAll(context);
				
				if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals("CAMPAIGN")) {
					if (UtilValidate.isNotEmpty(isStoreReceipt) && isStoreReceipt.equals("Y") && (UtilValidate.isEmpty(isFullLoad) || isFullLoad.equals("N"))) {
						List<String> storeIds = UtilCampaign.getStoreIds(delegator, UtilMisc.toMap("campaignId", domainEntityId, "productStoreType", "STORE_RECEIPT"));
						if (UtilValidate.isNotEmpty(storeIds)) {
							requestContext.put("productStoreId", StringUtil.join(storeIds, ","));
						} else {
							isActiveSearch = false;
						}
					}
				}
				
	            callCtxt.put("userLogin", userLogin);
	            
	            requestContext.put("totalGridFetch",
						org.groupfio.common.portal.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));
	            if (UtilValidate.isEmpty(orderByColumn)) {
	            	requestContext.put("orderByColumn", orderByColumn);
	            }
	            
	            callCtxt.put("requestContext", requestContext);
				
	            if (isActiveSearch) {
	            	callResult = dispatcher.runSync("prod.findProductStore", callCtxt);
	            }
	            
				if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("dataList"))) {

					List<GenericValue> storeList = (List<GenericValue>) callResult.get("dataList");
					
					Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					// TODO prepare preloaded data
					
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					viewIndex = (int) callResult.get("viewIndex");
					highIndex = (int) callResult.get("highIndex");
					lowIndex = (int) callResult.get("lowIndex");
					resultListSize = (int) callResult.get("resultListSize");
					viewSize = (int) callResult.get("viewSize");
					
					int count = 0;
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					for (GenericValue store : storeList) {
						//Debug.logInfo("data "+(count++)+": "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						Map<String, Object> data = new HashMap<String, Object>();
						
						String productStoreId = store.getString("productStoreId");
						String storeName = store.getString("storeName");
						
						data.put("productStoreId", productStoreId);
						data.put("primaryStoreGroupId", store.getString("primaryStoreGroupId"));
						data.put("companyName", store.getString("companyName"));
						data.put("title", store.getString("title"));
						data.put("isDemoStore", store.getString("isDemoStore"));
						data.put("stateProvinceGeoId", store.getString("stateProvinceGeoId"));
						
						if (UtilValidate.isNotEmpty(isStoreReceipt) && isStoreReceipt.equals("Y")) {
							storeName = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "storeName");
						}
						data.put("storeName", storeName);
						
						String createdOn = "";
						if (UtilValidate.isNotEmpty(store.get("createdStamp"))) {
							createdOn = UtilDateTime.timeStampToString(store.getTimestamp("createdStamp"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("createdOn", createdOn);
						
						String modifiedOn = "";
						if (UtilValidate.isNotEmpty(store.getString("lastUpdatedStamp"))) {
							modifiedOn = UtilDateTime.timeStampToString(store.getTimestamp("lastUpdatedStamp"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("modifiedOn", modifiedOn);
						
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
    
    public static String addStoreReceipt(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String selectedProductStoreIds = request.getParameter("selectedProductStoreIds");

		Map<String, Object> result = FastMap.newInstance();
		try {
			if (UtilValidate.isNotEmpty(selectedProductStoreIds)) {
				List<String> alreadyRelatedOppList = new ArrayList<String>();
				List<String> newRelatedOppList = new ArrayList<String>();
				StringTokenizer st = new StringTokenizer(selectedProductStoreIds, ",");

				while (st.hasMoreTokens()) {
					if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals("CAMPAIGN")) {
						String productStoreId = st.nextToken();

						List conditionsList = FastList.newInstance();

						conditionsList.add(EntityCondition.makeCondition("campaignId", EntityOperator.EQUALS, domainEntityId));
						conditionsList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
						conditionsList.add(EntityCondition.makeCondition("productStoreType", EntityOperator.EQUALS, "STORE_RECEIPT"));
						conditionsList.add(EntityUtil.getFilterByDateExpr());
						EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

						GenericValue storeAssoc = EntityUtil.getFirst(
								delegator.findList("CampaignStoreAssoc", mainConditons, null, null, null, false));
						if (UtilValidate.isNotEmpty(storeAssoc)) {
							alreadyRelatedOppList.add(productStoreId);
						} else {
							storeAssoc = delegator.makeValue("CampaignStoreAssoc");
							storeAssoc.put("campaignId", domainEntityId);
							storeAssoc.put("productStoreId", productStoreId);
							storeAssoc.put("productStoreType", "STORE_RECEIPT");
							storeAssoc.put("fromDate", UtilDateTime.nowTimestamp());
							storeAssoc.put("createdByUserLogin", userLogin.getString("userLoginId"));
							storeAssoc.create();

							newRelatedOppList.add(productStoreId);
						}
					}
				}

				String message = "";
				if (UtilValidate.isNotEmpty(alreadyRelatedOppList)) {
					message += "Already add store receipt# " + StringUtil.join(alreadyRelatedOppList, ", ");
				}
				if (UtilValidate.isNotEmpty(newRelatedOppList)) {
					message += "Successfully add store receipt# " + StringUtil.join(newRelatedOppList, ", ");
				}
				
				result.put(GlobalConstants.RESPONSE_MESSAGE, message);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
    
    public static String removeStoreReceipt(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String selectedProductStoreIds = request.getParameter("selectedProductStoreIds");

		Map<String, Object> result = FastMap.newInstance();
		try {
			if (UtilValidate.isNotEmpty(selectedProductStoreIds)) {
				List<String> removedStoreReceiptList = new ArrayList<String>();
				StringTokenizer st = new StringTokenizer(selectedProductStoreIds, ",");

				while (st.hasMoreTokens()) {
					if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals("CAMPAIGN")) {
						String productStoreId = st.nextToken();

						List conditionsList = FastList.newInstance();

						conditionsList.add(EntityCondition.makeCondition("campaignId", EntityOperator.EQUALS, domainEntityId));
						conditionsList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
						conditionsList.add(EntityCondition.makeCondition("productStoreType", EntityOperator.EQUALS, "STORE_RECEIPT"));
						conditionsList.add(EntityUtil.getFilterByDateExpr());
						EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

						GenericValue storeAssoc = EntityUtil.getFirst(
								delegator.findList("CampaignStoreAssoc", mainConditons, null, null, null, false));
						if (UtilValidate.isNotEmpty(storeAssoc)) {
							removedStoreReceiptList.add(productStoreId);
							storeAssoc.put("thruDate", UtilDateTime.nowTimestamp());
							storeAssoc.put("modifiedByUserLogin", userLogin.getString("userLoginId"));
							storeAssoc.store();
						}
					}
				}

				String message = "";
				if (UtilValidate.isNotEmpty(removedStoreReceiptList)) {
					message += "Successfully remove store receipt# " + StringUtil.join(removedStoreReceiptList, ", ");
				}
				
				result.put(GlobalConstants.RESPONSE_MESSAGE, message);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
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
