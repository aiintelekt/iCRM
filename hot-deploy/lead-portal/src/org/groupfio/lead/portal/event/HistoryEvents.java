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

package org.groupfio.lead.portal.event;

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
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.StatusUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif Ul Islam
 * 
 */
public final class HistoryEvents {

    private HistoryEvents() { }

    private static final String MODULE = HistoryEvents.class.getName();
    
    public static String searchLeadHistorys(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map < String, Object > context = UtilHttp.getCombinedMap(request);

		String partyId = request.getParameter("partyId");
		String externalLoginKey = request.getParameter("externalLoginKey");

        String orderByColumn = (String) context.get("orderByColumn");
		
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		
		long start = System.currentTimeMillis();
		
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
				
				int viewIndex = 0;
				int highIndex = 0;
				int lowIndex = 0;
				int resultListSize = 0;
				int viewSize = 0;
				
				Map<String, Object> callCtxt = FastMap.newInstance();
	            Map<String, Object> callResult = FastMap.newInstance();
	            
	            Map<String, Object> requestContext = new LinkedHashMap<>();
				requestContext.putAll(context);
	            
	            //callCtxt.put("domainEntityType", domainEntityType);
	            //callCtxt.put("domainEntityId", domainEntityId);
				
				callCtxt.put("partyId", partyId);
	            
	            callCtxt.put("userLogin", userLogin);
	            
	            requestContext.put("totalGridFetch",
						org.groupfio.common.portal.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));
	            
	            callCtxt.put("requestContext", requestContext);
				
	            callResult = dispatcher.runSync("lead.findLeadHistory", callCtxt);

				if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("dataList"))) {

					List<GenericValue> historyList = (List<GenericValue>) callResult.get("dataList");
					
					Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					Map<String, Object> statusList = StatusUtil.getStatusList(delegator, historyList, "statusId", null);
					
					Map<String, Object> timeZoneList = EnumUtil.getEnumList(delegator, historyList, "timeZoneId", "TIME_ZONE");
					Map<String, Object> ownershipList = EnumUtil.getEnumList(delegator, historyList, "ownershipEnumId", "PARTY_OWNERSHIP");
					Map<String, Object> industryList = EnumUtil.getEnumList(delegator, historyList, "industryEnumId", "PARTY_INDUSTRY");
					
					Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, historyList, "createdByUserLogin");
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, historyList, "lastModifiedByUserLogin");
					PartyHelper.getPartyNameByPartyIds(delegator, partyNames, historyList, "personResponsibleId");
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					viewIndex = (int) callResult.get("viewIndex");
					highIndex = (int) callResult.get("highIndex");
					lowIndex = (int) callResult.get("lowIndex");
					resultListSize = (int) callResult.get("resultListSize");
					viewSize = (int) callResult.get("viewSize");
					
					int count = 0;
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					for (GenericValue history : historyList) {
						//Debug.logInfo("data "+(count++)+": "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						Map<String, Object> data = new HashMap<String, Object>();
						
						String uomDesc = DataUtil.getUomDescription(delegator, history.getString("currencyUomId"), "CURRENCY_MEASURE");
						
						partyId = history.getString("partyId");
						data.put("partyId", history.getString("partyId"));
						
						data.put("leadName", history.getString("leadName"));
						data.put("personResponsibleId", history.getString("personResponsibleId"));
						data.put("personResponsibleName", partyNames.get(history.getString("personResponsibleId")));
						data.put("groupNameLocal", history.getString("groupNameLocal"));
						data.put("industryEnumId", history.getString("industryEnumId"));
						data.put("industryEnumDesc", industryList.get(history.getString("industryEnumId")));
						data.put("sicCode", history.getString("sicCode"));
						data.put("ownershipEnumId", history.getString("ownershipEnumId"));
						data.put("ownershipEnumDesc", ownershipList.get(history.getString("ownershipEnumId")));
						data.put("timeZoneId", history.getString("timeZoneId"));
						data.put("timeZoneDesc", timeZoneList.get(history.getString("timeZoneId")));
						data.put("annualRevenue", history.getString("annualRevenue"));
						data.put("numberEmployees", history.getString("numberEmployees"));
						data.put("currencyUomId", history.getString("currencyUomId"));
						data.put("currencyUomDesc", uomDesc);
						data.put("tickerSymbol", history.getString("tickerSymbol"));
						data.put("description", history.getString("description"));
						data.put("statusId", history.getString("statusId"));
						data.put("statusDescription", statusList.get(history.getString("statusId")));
						
						data.put("createdOn", UtilValidate.isNotEmpty(history.getTimestamp("createdDate")) ? UtilDateTime.timeStampToString(history.getTimestamp("createdDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()) : "");
						data.put("createdByName", partyNames.get(history.getString("createdByUserLogin")));
						data.put("modifiedOn", UtilValidate.isNotEmpty(history.getTimestamp("lastModifiedDate")) ? UtilDateTime.timeStampToString(history.getTimestamp("lastModifiedDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()) : "");
						data.put("modifiedByName", partyNames.get(history.getString("lastModifiedByUserLogin")));
						
						dataList.add(data);
					}
					Debug.logInfo("prepare actual data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					result.put("highIndex", Integer.valueOf(highIndex));
					result.put("lowIndex", Integer.valueOf(lowIndex));

					result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
					result.put("totalRecords", nf.format(resultListSize));
					result.put("recordCount", resultListSize);
					result.put("chunkSize", viewSize);

					result.put("viewSize", viewSize);
					result.put("viewIndex", viewIndex);
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
		
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
		result.put("timeTaken", (end - start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return AjaxEvents.doJSONResponse(response, result);
	}
    
    public static String getLeadEventHistory(HttpServletRequest request, HttpServletResponse response) {
	    Delegator delegator = (Delegator) request.getAttribute("delegator");
	    LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

	    Map<String, Object> context = UtilHttp.getCombinedMap(request);

	    String partyId = (String) context.get("partyId");
	    String externalLoginKey = request.getParameter("externalLoginKey");

	    Timestamp now = UtilDateTime.nowTimestamp();
	    Locale locale = UtilHttp.getLocale(request);
	    TimeZone tz = UtilHttp.getTimeZone(request);

	    List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>> ();
	    Map<String, Object> result = new HashMap<String, Object>();
	    
	    try {
	        if (UtilValidate.isNotEmpty(partyId)) {
	        	List condList = FastList.newInstance();
	    	    List orderByFields = FastList.newInstance();
	            orderByFields.add("historyId");
	            orderByFields.add("eventDate");
	            orderByFields.add("createdStamp");
	            condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	            EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	            List<GenericValue> eventHistoryList = delegator.findList("LeadEventHistory", cond, null, orderByFields, null, false);

	            if (UtilValidate.isNotEmpty(eventHistoryList)) {
	            	
	            	Map<String, Object> eventTypeList = EnumUtil.getEnumList(delegator, eventHistoryList, "eventTypeId", "LEAD_EVT_HST");

	                for (GenericValue historyList: eventHistoryList) {
	                    Map<String, Object> data = new HashMap<String, Object>();
	                    
	                    String historyId = historyList.getString("historyId");
	                    String eventTypeId = historyList.getString("eventTypeId");
	                    String eventDate = historyList.getString("eventDate");
	                    String createdByUserLogin = historyList.getString("createdByUserLogin");
	                    String ipAddress = historyList.getString("ipAddress");
	                    String createdByEmailAddress = historyList.getString("createdByEmailAddress");
	                    String description = historyList.getString("description");
	                    String createdTxStamp = historyList.getString("createdTxStamp");
	                    partyId = historyList.getString("partyId");
	                    String partyName = "";
	                    if (UtilValidate.isNotEmpty(partyId)) {
	                        partyName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyId, false);
	                    }
	                    String eventTypeDesc = UtilValidate.isNotEmpty(eventTypeList.get(eventTypeId)) ? (String) eventTypeList.get(eventTypeId) : eventTypeId;
	                    data.put("historyId", historyId);
	                    data.put("eventTypeId", eventTypeId);
	                    data.put("eventTypeDesc", eventTypeDesc);
	                    data.put("eventDate", eventDate);
	                    data.put("createdByUserLogin", UtilValidate.isNotEmpty(createdByUserLogin) ? createdByUserLogin : "");
	                    data.put("ipAddress", UtilValidate.isNotEmpty(ipAddress) ? ipAddress : "");
	                    data.put("createdByEmailAddress", UtilValidate.isNotEmpty(createdByEmailAddress) ? createdByEmailAddress : "");
	                    data.put("description", description);
	                    data.put("createdTxStamp", createdTxStamp);
	                    data.put("partyId", partyId);
	                    data.put("partyName", partyName);
	                    
	                    data.put("externalLoginKey", externalLoginKey);

	                    dataList.add(data);
	                }
	            }
	            result.put("dataList", dataList);
	            result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
	        }
	    } catch (Exception e) {
	        // TODO: handle exception
	        result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
	    }
	    return AjaxEvents.doJSONResponse(response, result);
	}
    
}
