/**
 * 
 */
package org.fio.admin.portal.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
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
import org.fio.admin.portal.event.AjaxEvents;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Sharif
 *
 */
public class IndicaAjaxEvents {

	private IndicaAjaxEvents() { }

	private static final String MODULE = IndicaAjaxEvents.class.getName();

	public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
		return doJSONResponse(response, jsonObject.toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
		return doJSONResponse(response, JSONArray.fromObject(collection).toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Map map) {
		return doJSONResponse(response, JSONObject.fromObject(map));
	}

	public static String doJSONResponse(HttpServletResponse response, String jsonString) {
		String result = "success";

		response.setContentType("application/x-json");
		try {
			response.setContentLength(jsonString.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
			Debug.logWarning("Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(), MODULE);
			response.setContentLength(jsonString.length());
		}

		Writer out;
		try {
			out = response.getWriter();
			out.write(jsonString);
			out.flush();
		} catch (IOException e) {
			Debug.logError(e, "Failed to get response writer", MODULE);
			result = "error";
		}
		return result;
	}

	public static GenericValue getUserLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (GenericValue) session.getAttribute("userLogin");
	}

	/*************************************************************************/
	/**                                                                     **/
	/**                      Common JSON Requests                           **/
	/**                                                                     **/
	/*************************************************************************/

	public static String searchCustGrpUpdates(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map < String, Object > context = UtilHttp.getCombinedMap(request);

		//String domainEntityType = request.getParameter("domainEntityType");
		String externalLoginKey = request.getParameter("externalLoginKey");
		String isChargeInvMap = request.getParameter("isChargeInvMap");
		
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
	            
	            //callCtxt.put("domainEntityType", domainEntityType);
				
	            callCtxt.put("userLogin", userLogin);
	            
	            requestContext.put("totalGridFetch",
	            		org.fio.homeapps.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));
	            if (UtilValidate.isEmpty(orderByColumn)) {
	            	requestContext.put("orderByColumn", orderByColumn);
	            }
	            
	            if (UtilValidate.isNotEmpty(isChargeInvMap) && isChargeInvMap.equals("Y")) {
	            	requestContext.put("isProcessed", "N");
		            requestContext.put("status", "succeeded");
	            }
	            
	            callCtxt.put("requestContext", requestContext);
				
	            callResult = dispatcher.runSync("indica.findCustGrpUpdate", callCtxt);

				if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("dataList"))) {

					List<GenericValue> groupUpdateList = (List<GenericValue>) callResult.get("dataList");
					
					Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					//Map<String, Object> categoryList = EnumUtil.getEnumList(delegator, templateList, "approvalCategoryId", "APPROVAL_CATEGORY");
					
					Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByPartyIds(delegator, partyNames, groupUpdateList, "partyId");
					
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					viewIndex = (int) callResult.get("viewIndex");
					highIndex = (int) callResult.get("highIndex");
					lowIndex = (int) callResult.get("lowIndex");
					resultListSize = (int) callResult.get("resultListSize");
					viewSize = (int) callResult.get("viewSize");
					
					int count = 0;
					String repost = "";
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					for (GenericValue gu : groupUpdateList) {
						//Debug.logInfo("data "+(count++)+": "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						Map<String, Object> data = new HashMap<String, Object>();
						String partyId = gu.getString("partyId");
						
						data.putAll(gu.getAllFields());
						
						if (UtilValidate.isNotEmpty(gu.getString("isRepost")) && gu.getString("isRepost").equals("D")) {
							repost = "Yes";
						} else if (UtilValidate.isNotEmpty(gu.getString("isRepost")) && gu.getString("isRepost").equals("R")) {
							repost = "Ready";
						} else {
							repost = "No";
						}
						
						data.put("customerName", partyNames.get(partyId));
						data.put("isRepost", repost);
						data.put("errorMessage", UtilValidate.isNotEmpty(gu.getString("status")) && gu.getString("status").equals("FAILED") ? gu.getString("description") : "");
						
						data.remove("description");
						data.remove("prvDescription");
						
						data.put("createdStamp", UtilDateTime.timeStampToString(gu.getTimestamp("createdStamp"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));
						data.put("lastUpdatedStamp", UtilDateTime.timeStampToString(gu.getTimestamp("lastUpdatedStamp"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));
						
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
	
	public static String repostCustGrpUpdate(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String selectedSeqIds = request.getParameter("selectedSeqIds");
		
		String userLoginId = userLogin.getString("userLoginId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			if (UtilValidate.isNotEmpty(selectedSeqIds)) {
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("seqId", EntityOperator.IN, StringUtil.split(selectedSeqIds, ",")));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				delegator.storeByCondition("CustomerGroupUpdateStatus", UtilMisc.toMap("isRepost", "R"), mainConditons);
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
	
	public static String getCustGrpBatchSummary(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String seqId = request.getParameter("seqId");
		String batchId = request.getParameter("batchId");
		
		String userLoginId = userLogin.getString("userLoginId");
		
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> summary = new LinkedHashMap<>();
		try {
			
			if (UtilValidate.isNotEmpty(batchId)) {
				GenericValue custGrpUpd = EntityQuery.use(delegator).from("CustomerGroupUpdateStatus").where("seqId", seqId).queryFirst();
				
				summary.put("groupId", UtilValidate.isNotEmpty(custGrpUpd.getString("groupId")) ? custGrpUpd.getString("groupId") : "N/A" );
				summary.put("prvGroupId", UtilValidate.isNotEmpty(custGrpUpd.getString("prvGroupId")) ? custGrpUpd.getString("prvGroupId") : "N/A" );
				
				long successCount = 0;
				long failedCount = 0;
				long repostCount = 0;
				
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, "SUCCESS"));
				conditionList.add(EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				successCount = delegator.findCountByCondition("CustomerGroupUpdateStatus", mainConditons, null, null);
				
				conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, "FAILED"));
				conditionList.add(EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId));
				mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				failedCount = delegator.findCountByCondition("CustomerGroupUpdateStatus", mainConditons, null, null);
				
				conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("isRepost", EntityOperator.EQUALS, "D"));
				conditionList.add(EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId));
				mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				repostCount = delegator.findCountByCondition("CustomerGroupUpdateStatus", mainConditons, null, null);
				
				summary.put("successCount", successCount);
				summary.put("failedCount", failedCount);
				summary.put("repostCount", repostCount);
			}
			
			result.put("summary", summary);
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
