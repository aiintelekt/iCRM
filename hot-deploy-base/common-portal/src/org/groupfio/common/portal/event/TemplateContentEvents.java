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

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
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
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.common.portal.util.UtilCampaign;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
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
public final class TemplateContentEvents {

    private static final String MODULE = TemplateContentEvents.class.getName();
    
    public static String createTemplateContent(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String templateId = request.getParameter("contentTplId");
		String tagId = request.getParameter("tagId");
		String contentTitle = request.getParameter("contentTitle");
		String contentText = request.getParameter("contentText");
		String fromDate = request.getParameter("contentFromDate");
		String thruDate = request.getParameter("contentThruDate");
		String sequenceId = request.getParameter("sequenceId");
		
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String nativeBusinessUnit = null;
			String nativeTeamId = null;
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
			conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
			conditionList.add(EntityCondition.makeCondition("templateId", EntityOperator.EQUALS, templateId));
			conditionList.add(EntityCondition.makeCondition("tagId", EntityOperator.EQUALS, tagId));
			conditionList.add(EntityCondition.makeCondition("contentTitle", EntityOperator.LIKE, EntityFunction.UPPER("%" + contentTitle + "%")));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue templateContent = EntityUtil.getFirst( delegator.findList("TemplateContent", mainConditons, null, null, null, false) );
			if (UtilValidate.isNotEmpty(templateContent)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Template Content exists!");
				return AjaxEvents.doJSONResponse(response, result);
			}
			
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			Map<String, Object> contentContext = FastMap.newInstance();
			
			contentContext.put("templateId", templateId);
			contentContext.put("tagId", tagId);
			contentContext.put("contentTitle", contentTitle);
			contentContext.put("contentText", contentText);
			contentContext.put("sequenceId", sequenceId);
			contentContext.put("fromDate", UtilValidate.isNotEmpty(fromDate) ? UtilDateTime.stringToTimeStamp(fromDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null);
			contentContext.put("thruDate", UtilValidate.isNotEmpty(thruDate) ? UtilDateTime.stringToTimeStamp(thruDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null);
			
			contentContext.put("domainEntityType", domainEntityType);
			contentContext.put("domainEntityId", domainEntityId);
			
			callCtxt.put("contentContext", contentContext);

			callCtxt.put("userLogin", userLogin);

			Debug.log("inMap==============" + callCtxt);

			callResult = dispatcher.runSync("tplcontent.createTemplateContent", callCtxt);
			if (ServiceUtil.isSuccess(callResult)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully created template content");
			} else {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(callResult));
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
    
    public static String updateTemplateContent(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String templateContentId = request.getParameter("templateContentId");
		
		String templateId = request.getParameter("contentTplId");
		String tagId = request.getParameter("tagId");
		String contentTitle = request.getParameter("contentTitle");
		String contentText = request.getParameter("contentText");
		String fromDate = request.getParameter("contentFromDate");
		String thruDate = request.getParameter("contentThruDate");
		String sequenceId = request.getParameter("sequenceId");
		
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String nativeBusinessUnit = null;
			String nativeTeamId = null;
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("templateContentId", EntityOperator.EQUALS, templateContentId));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue templateContent = EntityUtil.getFirst( delegator.findList("TemplateContent", mainConditons, null, null, null, false) );
			if (UtilValidate.isEmpty(templateContent)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Template Content not exists!");
				return AjaxEvents.doJSONResponse(response, result);
			}
			
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			Map<String, Object> contentContext = FastMap.newInstance();
			
			contentContext.put("templateContentId", templateContentId);
			contentContext.put("templateId", templateId);
			contentContext.put("tagId", tagId);
			contentContext.put("contentTitle", contentTitle);
			contentContext.put("contentText", contentText);
			contentContext.put("sequenceId", sequenceId);
			contentContext.put("fromDate", UtilValidate.isNotEmpty(fromDate) ? UtilDateTime.stringToTimeStamp(fromDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null);
			contentContext.put("thruDate", UtilValidate.isNotEmpty(thruDate) ? UtilDateTime.stringToTimeStamp(thruDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null);
			
			contentContext.put("domainEntityType", domainEntityType);
			contentContext.put("domainEntityId", domainEntityId);
			
			callCtxt.put("contentContext", contentContext);

			callCtxt.put("userLogin", userLogin);

			Debug.log("inMap==============" + callCtxt);

			callResult = dispatcher.runSync("tplcontent.updateTemplateContent", callCtxt);
			if (ServiceUtil.isSuccess(callResult)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully updated template content");
			} else {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(callResult));
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
    
    public static String bulkUpdateTemplateContent(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String templateContentIdList[] = request.getParameterValues("templateContentId");
		String sequenceIdList[] = request.getParameterValues("sequenceId");
		String contentTextList[] = request.getParameterValues("contentText");
		
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			
			if (UtilValidate.isNotEmpty(templateContentIdList)) {
				for (int i = 0; i < templateContentIdList.length; i++) {
					String templateContentId = templateContentIdList[i];
					//String sequenceId = sequenceIdList[i];
					String sequenceId = ""+(1001+i);
					String contentText = contentTextList[i];
					
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("templateContentId", EntityOperator.EQUALS, templateContentId));
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					GenericValue templateContent = EntityUtil.getFirst( delegator.findList("TemplateContent", mainConditons, null, null, null, false) );
					if (UtilValidate.isNotEmpty(templateContent)) {
						templateContent.put("sequenceId", sequenceId);
						templateContent.put("contentText", contentText);
						templateContent.store();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	        return "error";
		}
		request.setAttribute("_EVENT_MESSAGE_", "Successfully updated template contents");
		return "success";
    }
    
    public static String getTemplateContentData(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String templateContentId = request.getParameter("templateContentId");
		
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
			String nativeBusinessUnit = null;
			String nativeTeamId = null;
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("templateContentId", EntityOperator.EQUALS, templateContentId));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue templateContent = EntityUtil.getFirst( delegator.findList("TemplateContent", mainConditons, null, null, null, false) );
			if (UtilValidate.isEmpty(templateContent)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Template Content not exists!");
				return AjaxEvents.doJSONResponse(response, result);
			}
			
			Map<String, Object> data = FastMap.newInstance();
			
			data.put("templateContentId", templateContent.get("templateContentId"));
			data.put("contentTplId", templateContent.get("templateId"));
			data.put("tagId", templateContent.get("tagId"));
			data.put("tagName", UtilCampaign.getTemplateTagName(delegator, templateContent.getString("tagId")));
			data.put("contentTitle", templateContent.get("contentTitle"));
			data.put("contentText", templateContent.get("contentText"));
			data.put("sequenceId", templateContent.get("sequenceId"));
			data.put("isDefault", templateContent.get("isDefault"));
			data.put("createdByUserLogin", templateContent.get("createdByUserLogin"));
			data.put("modifiedByUserLogin", templateContent.get("modifiedByUserLogin"));
			
			String fromDate = "";
			if (UtilValidate.isNotEmpty(templateContent.get("fromDate"))) {
				fromDate = UtilDateTime.timeStampToString(templateContent.getTimestamp("fromDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
			}
			String thruDate = "";
			if (UtilValidate.isNotEmpty(templateContent.get("thruDate"))) {
				thruDate = UtilDateTime.timeStampToString(templateContent.getTimestamp("thruDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
			}
			String createdOn = "";
			if (UtilValidate.isNotEmpty(templateContent.get("createdOn"))) {
				createdOn = UtilDateTime.timeStampToString(templateContent.getTimestamp("createdOn"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
			}
			String modifiedOn = "";
			if (UtilValidate.isNotEmpty(templateContent.get("modifiedOn"))) {
				modifiedOn = UtilDateTime.timeStampToString(templateContent.getTimestamp("modifiedOn"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
			}
			
			data.put("contentFromDate", fromDate);
			data.put("contentThruDate", thruDate);
			data.put("createdOn", createdOn);
			data.put("modifiedOn", modifiedOn);
			
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
    
    public static String removeTemplateContent(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String selectedItemIds = request.getParameter("selectedItemIds");
		
		Map<String, Object> result = FastMap.newInstance();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		try {
			if (UtilValidate.isNotEmpty(selectedItemIds)) {
				List<GenericValue> tobeStore = new ArrayList<>();
				for (String templateContentId : selectedItemIds.split(",")) {
					GenericValue templateContent = EntityQuery.use(delegator).from("TemplateContent").where("templateContentId", templateContentId).queryFirst();
					if (UtilValidate.isNotEmpty(templateContent)) {
						templateContent.put("thruDate", nowTimestamp);
						templateContent.put("modifiedOn", nowTimestamp);
						templateContent.put("modifiedByUserLogin", userLogin.getString("userLoginId"));
						tobeStore.add(templateContent);
					}
					//delegator.removeByAnd("WorkEffortApproval", UtilMisc.toMap("workEffortId", workEffortId));
				}
				
				if (UtilValidate.isNotEmpty(tobeStore)) {
					delegator.storeAll(tobeStore);
				}

				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(ModelService.SUCCESS_MESSAGE, "Template Content Removed Successfully.");
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
    
    public static String searchTemplateContents(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map < String, Object > context = UtilHttp.getCombinedMap(request);

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
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
	            
	            Map<String, Object> requestContext = new LinkedHashMap<>();
				requestContext.putAll(context);
	            
	            callCtxt.put("domainEntityType", domainEntityType);
	            callCtxt.put("domainEntityId", domainEntityId);
	            
	            callCtxt.put("userLogin", userLogin);
	            
	            requestContext.put("totalGridFetch",
						org.groupfio.common.portal.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));
	            if (UtilValidate.isEmpty(orderByColumn)) {
	            	requestContext.put("orderByColumn", orderByColumn);
	            }
	            
	            callCtxt.put("requestContext", requestContext);
				
	            callResult = dispatcher.runSync("tplcontent.findTemplateContent", callCtxt);

				if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("dataList"))) {

					List<GenericValue> templateContentList = (List<GenericValue>) callResult.get("dataList");
					
					Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, templateContentList, "createdByUserLogin");
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, templateContentList, "modifiedByUserLogin");
					
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					viewIndex = (int) callResult.get("viewIndex");
					highIndex = (int) callResult.get("highIndex");
					lowIndex = (int) callResult.get("lowIndex");
					resultListSize = (int) callResult.get("resultListSize");
					viewSize = (int) callResult.get("viewSize");
					
					int count = 0;
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					for (GenericValue templateContent : templateContentList) {
						//Debug.logInfo("data "+(count++)+": "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						Map<String, Object> data = new HashMap<String, Object>();
						
						data.put("templateContentId", templateContent.get("templateContentId"));
						data.put("templateId", templateContent.get("templateId"));
						data.put("templateName", UtilCampaign.getTemplateName(delegator, templateContent.getString("templateId")));
						data.put("tagId", templateContent.get("tagId"));
						data.put("tagName", UtilCampaign.getTemplateTagName(delegator, templateContent.getString("tagId")));
						data.put("contentTitle", templateContent.get("contentTitle"));
						data.put("contentText", templateContent.get("contentText"));
						data.put("sequenceId", templateContent.get("sequenceId"));
						data.put("isDefault", templateContent.get("isDefault"));
						
						data.put("fromDate", UtilDateTime.timeStampToString(templateContent.getTimestamp("fromDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
						data.put("thruDate", UtilDateTime.timeStampToString(templateContent.getTimestamp("thruDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
						
						String templateContentStatus = "Active";
						if (UtilValidate.isNotEmpty(templateContent.getTimestamp("thruDate")) 
								&& UtilDateTime.nowTimestamp().after(templateContent.getTimestamp("thruDate"))) {
							templateContentStatus = "Expired";
						}
						data.put("templateContentStatus", templateContentStatus);
						
						String createdOn = "";
						if (UtilValidate.isNotEmpty(templateContent.get("createdOn"))) {
							createdOn = UtilDateTime.timeStampToString(templateContent.getTimestamp("createdOn"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("createdOn", createdOn);
						data.put("createdByName", partyNames.get(templateContent.getString("createdByUserLogin")));
						
						String modifiedOn = "";
						if (UtilValidate.isNotEmpty(templateContent.getString("modifiedOn"))) {
							modifiedOn = UtilDateTime.timeStampToString(templateContent.getTimestamp("modifiedOn"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("modifiedOn", modifiedOn);
						data.put("modifiedByName", partyNames.get(templateContent.getString("modifiedByUserLogin")));
						
						data.put("domainEntityId", templateContent.getString("domainEntityId"));
						data.put("domainEntityType", templateContent.getString("domainEntityType"));
						data.put("domainEntityTypeDesc", DataHelper.convertToLabel( templateContent.getString("domainEntityType") ));
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
    
    public static String getExtTemplateList(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String emailEngine = request.getParameter("emailEngine");
		
		Map<String, Object> result = FastMap.newInstance();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		try {
			String sendgridEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SENDGRID_ENABLED");
			if (UtilValidate.isNotEmpty(sendgridEnabled) && sendgridEnabled.equals("Y")) {
				Map<String, Object> callCtxt = FastMap.newInstance();
	            Map<String, Object> callResult = FastMap.newInstance();
	            
	            callCtxt.put("userLogin", userLogin);
	            
	            callResult = dispatcher.runSync("sendgrid.findExternalTemplate", callCtxt);
	            if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("dataList"))) {
	            	result.put("dataList", callResult.get("dataList"));
	            }
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(ModelService.SUCCESS_MESSAGE, "External Template Retrieved Successfully.");
			} else {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.EXPECTATION_FAILED_CODE);
				result.put(ModelService.SUCCESS_MESSAGE, "Email engine not configured to get external template");
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
    
    public static String downloadFile(HttpServletRequest request, HttpServletResponse response)
			throws ComponentException {
    	String result = "success";
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		FileInputStream fis = null;
		Map<String, Object> resp = new HashMap<String, Object>();
		
		String resourceName = request.getParameter("resourceName");
		String componentName = request.getParameter("componentName");
		String fileName = request.getParameter("fileName");
		String isAbsoluteTplLoc = request.getParameter("isAbsoluteTplLoc");
		
		String filePath = ComponentConfig.getRootLocation(componentName) + "webapp/" + resourceName + "/template/";
		if (UtilValidate.isNotEmpty(isAbsoluteTplLoc) && isAbsoluteTplLoc.equals("Y")) {
			filePath = isAbsoluteTplLoc;
		}
		String rootPath = filePath;
		OutputStream os = null;
		try {
			File file = new File(filePath + fileName);
			if (file.getCanonicalPath().startsWith(new File(rootPath).getCanonicalPath())) {
				fis = new FileInputStream(file);
				byte b[];
				int x = fis.available();
				b = new byte[x];
				Debug.log(" b size" + b.length);
				fis.read(b);

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

				os = response.getOutputStream();
				os.write(b);
				os.flush();
			}
			
			resp.put("fields", "Success");
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			resp.put("fields", "Failed");
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				Debug.logError(e.getMessage(), MODULE);
			}
		}
		return result;
	}
    
}
