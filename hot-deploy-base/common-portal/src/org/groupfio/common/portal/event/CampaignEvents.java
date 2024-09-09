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

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.fio.admin.portal.barcode.BarCodeGenerator;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.constants.GlobalConstants.EmailEngine;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.fio.homeapps.util.UtilHttp;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.common.portal.util.UtilAttribute;
import org.groupfio.common.portal.util.UtilCampaign;
import org.groupfio.common.portal.util.UtilOrder;
import org.groupfio.common.portal.util.UtilProduct;
import org.groupfio.common.portal.util.UtilTemplate;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import au.com.bytecode.opencsv.CSVReader;
import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif Ul Islam
 * 
 */
public final class CampaignEvents {

    private static final String MODULE = CampaignEvents.class.getName();
    
    public static String searchCampaigns(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String externalLoginKey = request.getParameter("externalLoginKey");
		String isCampaignForParty = request.getParameter("isCampaignForParty");
		
		String searchText = request.getParameter("searchText");
		String orderByColumn = (String) context.get("orderByColumn");
		
		String partyId = (String) context.get("partyId");
		
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
				List<String> campaignIds = UtilCampaign.getCampaignIds(delegator, UtilMisc.toMap("partyId", partyId));
				if (UtilValidate.isEmpty(isCampaignForParty) || UtilValidate.isNotEmpty(campaignIds)) {
					Map<String, Object> callCtxt = FastMap.newInstance();
		            Map<String, Object> callResult = FastMap.newInstance();
		            
		            Map<String, Object> requestContext = new LinkedHashMap<>();
					requestContext.putAll(context);
					
					requestContext.put("campaignIds", StringUtil.join(campaignIds, ","));
		            
		            callCtxt.put("userLogin", userLogin);
		            
		            requestContext.put("totalGridFetch",
							org.groupfio.common.portal.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));
		            if (UtilValidate.isEmpty(orderByColumn)) {
		            	requestContext.put("orderByColumn", orderByColumn);
		            }
		            
		            callCtxt.put("requestContext", requestContext);
					
		            callResult = dispatcher.runSync("common.findCampaign", callCtxt);

					if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("dataList"))) {

						List<GenericValue> campaignList = (List<GenericValue>) callResult.get("dataList");
						
						Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						
						Map<String, Object> partyNames = new HashMap<>();
						PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, campaignList, "createdByUserLogin");
						PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, campaignList, "lastModifiedByUserLogin");
						
						String roleTypeId = PartyHelper.getPartyRoleTypeId(partyId, delegator);
						String contactName = null;
						if (UtilValidate.isNotEmpty(partyId)) {
							if (UtilValidate.isNotEmpty(roleTypeId) && (roleTypeId.equals("ACCOUNT") || roleTypeId.equals("LEAD"))) {
								Map<String, Object> primaryContact = org.groupfio.common.portal.util.DataUtil.getPrimaryContact(delegator, partyId, roleTypeId);
								if (UtilValidate.isNotEmpty(primaryContact)) {
									contactName = (String) primaryContact.get("contactName");
								}
							} else {
								contactName = PartyHelper.getPartyName(delegator, partyId, false);
							}
						}
						
						Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						
						viewIndex = (int) callResult.get("viewIndex");
						highIndex = (int) callResult.get("highIndex");
						lowIndex = (int) callResult.get("lowIndex");
						resultListSize = (int) callResult.get("resultListSize");
						viewSize = (int) callResult.get("viewSize");
						
						int count = 0;
						Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						for (GenericValue campaign : campaignList) {
							//Debug.logInfo("data "+(count++)+": "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
							Map<String, Object> data = new HashMap<String, Object>();
							
							String marketingCampaignId = campaign.getString("marketingCampaignId");
							
							data.put("campaignCode", campaign.getString("campaignCode"));
							data.put("campaignName", campaign.getString("campaignName"));
							data.put("marketingCampaignId", campaign.getString("marketingCampaignId"));
							data.put("contactName", contactName);
							
							data.put("marketingCampaignIdName", marketingCampaignId+" ["+campaign.getString("campaignName")+"]");
							
							long openedCount = org.groupfio.common.portal.util.UtilCampaign.getDripCampaignCount(delegator, UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyId", partyId, "customFieldName", "opened"));
							long clickedCount = org.groupfio.common.portal.util.UtilCampaign.getDripCampaignCount(delegator, UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyId", partyId, "customFieldName", "clicked"));
							long notOpenCount = org.groupfio.common.portal.util.UtilCampaign.getDripCampaignCount(delegator, UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyId", partyId, "customFieldName", "not_open"));
							long bouncedCount = org.groupfio.common.portal.util.UtilCampaign.getDripCampaignCount(delegator, UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyId", partyId, "customFieldName", "bounced"));
							long unsubscribeCount = org.groupfio.common.portal.util.UtilCampaign.getDripCampaignCount(delegator, UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyId", partyId, "customFieldName", "unsubscribe"));
							long convertedCount = org.groupfio.common.portal.util.UtilCampaign.getDripCampaignCount(delegator, UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyId", partyId, "customFieldName", "converted"));
							long sentCount = org.groupfio.common.portal.util.UtilCampaign.getDripCampaignCount(delegator, UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyId", partyId, "customFieldName", "sent"));
							
							data.put("opened", openedCount);
							data.put("clicked", clickedCount);
							data.put("notOpened", notOpenCount);
							data.put("bounced", bouncedCount);
							data.put("unSubscribed", unsubscribeCount);
							data.put("sent", sentCount);
							data.put("converted", convertedCount);
							
							if (UtilValidate.isNotEmpty(campaign.getString("statusId"))) {
								GenericValue statusItem = delegator.findOne("StatusItem",UtilMisc.toMap("statusId", campaign.getString("statusId")), false);
								data.put("status", statusItem.getString("description"));
							}
							if (UtilValidate.isNotEmpty(campaign.getString("campaignTypeId"))) {
								GenericValue campaignType = delegator.findOne("CampaignType",UtilMisc.toMap("campaignTypeId", campaign.getString("campaignTypeId")), false);
								data.put("campaignType", UtilValidate.isNotEmpty(campaignType)?campaignType.getString("description"):campaign.getString("campaignTypeId"));
							}
							data.put("startDate", UtilDateTime.timeStampToString(campaign.getTimestamp("startDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
							data.put("endDate", UtilDateTime.timeStampToString(campaign.getTimestamp("endDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
							
							data.put("fromDate", UtilDateTime.timeStampToString(campaign.getTimestamp("fromDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
							data.put("thruDate", UtilDateTime.timeStampToString(campaign.getTimestamp("thruDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
							
							String createdOn = "";
							if (UtilValidate.isNotEmpty(campaign.get("createdDate"))) {
								createdOn = UtilDateTime.timeStampToString(campaign.getTimestamp("createdDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
							}
							data.put("createdOn", createdOn);
							data.put("createdByName", partyNames.get(campaign.getString("createdByUserLogin")));
							
							String modifiedOn = "";
							if (UtilValidate.isNotEmpty(campaign.getString("lastModifiedDate"))) {
								modifiedOn = UtilDateTime.timeStampToString(campaign.getTimestamp("lastModifiedDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
							}
							data.put("modifiedOn", modifiedOn);
							data.put("modifiedByName", partyNames.get(campaign.getString("lastModifiedByUserLogin")));
							
							data.put("externalLoginKey", externalLoginKey);
							
							if (UtilValidate.isNotEmpty(campaign.getString("remainder")))
								data.put("remainder", campaign.getString("remainder"));
							
							dataList.add(data);
						}
						Debug.logInfo("prepare actual data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					}
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
    
    public static String searchOutBoundCalls(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String externalLoginKey = request.getParameter("externalLoginKey");
		
		//String externalLoginKey = (String) session.getAttribute("externalLoginKey");
		
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
		
		String marketingCampaignIdName="";
		String statusDesc ="";
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
		SimpleDateFormat sqlformatter = new SimpleDateFormat("yyyy-MM-dd");
		String noOfDateSinceLastCall = "";
		String ltdValue = "";
		
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
				
	            callCtxt.put("userLogin", userLogin);
	            
	            requestContext.put("totalGridFetch",
						org.groupfio.common.portal.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));
	            if (UtilValidate.isEmpty(orderByColumn)) {
	            	requestContext.put("orderByColumn", orderByColumn);
	            }
	            
	            callCtxt.put("requestContext", requestContext);
				
	            callResult = dispatcher.runSync("common.findOutBoundCall", callCtxt);

				if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("dataList"))) {

					List<GenericValue> callList = (List<GenericValue>) callResult.get("dataList");
					
					Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					Map<String, Object> partyNames = new HashMap<>();
					//PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, callList, "createdByUserLogin");
					//PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, callList, "lastModifiedByUserLogin");
					
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					viewIndex = (int) callResult.get("viewIndex");
					highIndex = (int) callResult.get("highIndex");
					lowIndex = (int) callResult.get("lowIndex");
					resultListSize = (int) callResult.get("resultListSize");
					viewSize = (int) callResult.get("viewSize");
					
					int count = 0;
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					for (GenericValue call : callList) {
						//Debug.logInfo("data "+(count++)+": "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						
						Map<String, Object> data = org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, call);

						String contactListId =UtilValidate.isNotEmpty(call.getString("contactListId"))? call.getString("contactListId"): "";
						String crmMarketingCampaignId =UtilValidate.isNotEmpty(call.getString("crmMarketingCampaignId"))? call.getString("crmMarketingCampaignId"): "";
						String lastContactDate =UtilValidate.isNotEmpty(call.getString("lastContactDate"))? call.getString("lastContactDate"): "";
						if(UtilValidate.isNotEmpty(lastContactDate)) {
							String currentDate = formatter.format(new Date());
							Date d1 = sqlformatter.parse(lastContactDate);
							Date d2 = formatter.parse(currentDate);
							long diff = d2.getTime() - d1.getTime();
							long diffDay = diff / (24 * 60 * 60 * 1000);
							noOfDateSinceLastCall = String.valueOf(diffDay);
						}
						String campaignListId="";
						if(UtilValidate.isNotEmpty(contactListId) && UtilValidate.isNotEmpty(crmMarketingCampaignId)){
							List<GenericValue> marketingCampaignContactList = EntityQuery.use(delegator).from("MarketingCampaignContactList").where("contactListId",contactListId,"marketingCampaignId",crmMarketingCampaignId).orderBy("lastUpdatedStamp DESC").queryList();
							if(UtilValidate.isNotEmpty(marketingCampaignContactList)){
								Debug.log("marketingCampaignContactList"+marketingCampaignContactList);
								for(GenericValue mc :marketingCampaignContactList) {
									campaignListId = mc.getString("campaignListId");
								}
							}
						}
						if(UtilValidate.isNotEmpty(call.getString("crmMarketingCampaignId"))) {
							GenericValue campaign = EntityQuery.use(delegator).select("campaignName").from("MarketingCampaign").where("marketingCampaignId",call.getString("crmMarketingCampaignId")).queryOne();
							marketingCampaignIdName =org.groupfio.common.portal.util.DataUtil.combineValueKey(campaign.getString("campaignName"), call.getString("crmMarketingCampaignId"));
						}
						if(UtilValidate.isNotEmpty(call.getString("crmPartyId"))) {
							String partyId = call.getString("crmPartyId");
							String clvSql = null;
							ResultSet rs = null;
							SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
							clvSql ="SELECT IFNULL(SUM(total_sales_amount),0) as 'clv' FROM (SELECT total_sales_amount FROM rms_transaction_master WHERE bill_to_party_id='"+partyId+"' GROUP BY order_id) clv LIMIT 1";
							rs = sqlProcessor.executeQuery(clvSql);
							if (rs != null) {
								while (rs.next()) {
									ltdValue= rs.getString("clv");
								}
							}
							List<GenericValue> partyNoteViewList = delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", partyId, "isImportant", "Y"), null, false);
							if( partyNoteViewList != null && partyNoteViewList.size()>0){
								data.put("domainEntityId", partyId);
								data.put("domainEntityType", "CUSTOMER");
								data.put("isImportant", "Y");
							}
						}
						data.put("ltdValue", ltdValue);
						data.put("campaignListId", campaignListId);
						data.put("externalLoginKey", externalLoginKey);
						data.put("partyName", UtilValidate.isNotEmpty(call.getString("csr1PartyId"))?org.groupfio.common.portal.util.DataUtil.combineValueKey(PartyHelper.getPartyName(delegator,call.getString("csr1PartyId"), false), call.getString("csr1PartyId")):"");
						data.put("customerName", UtilValidate.isNotEmpty(call.getString("crmPartyId"))?org.groupfio.common.portal.util.DataUtil.combineValueKey(PartyHelper.getPartyName(delegator,call.getString("crmPartyId"), false), call.getString("crmPartyId")):"");
						data.put("noOfDateSinceLastCall",UtilValidate.isNotEmpty(noOfDateSinceLastCall)?noOfDateSinceLastCall:"");
						data.put("marketingCampaignIdName",UtilValidate.isNotEmpty(marketingCampaignIdName)?marketingCampaignIdName:"");
						data.put("statusId",UtilValidate.isNotEmpty(call.getString("lastCallStatusId"))?EnumUtil.getEnumDescription(delegator, call.getString("lastCallStatusId"), "CALL_STATUS"):"");
						data.put("firstContactDate", UtilValidate.isNotEmpty(call.getDate("firstContactDate"))? UtilDateTime.toDateString(call.getDate("firstContactDate"), globalDateFormat): "");
						data.put("lastContactDate", UtilValidate.isNotEmpty(call.getDate("lastContactDate"))? UtilDateTime.toDateString(call.getDate("lastContactDate"), globalDateFormat): "");
						data.put("startDate", UtilValidate.isNotEmpty(call.getTimestamp("startDate"))? UtilDateTime.timeStampToString(call.getTimestamp("startDate"),globalDateTimeFormat, TimeZone.getDefault(), null): "");
						data.put("endDate", UtilValidate.isNotEmpty(call.getTimestamp("endDate"))? UtilDateTime.timeStampToString(call.getTimestamp("endDate"),globalDateTimeFormat, TimeZone.getDefault(), null): "");
						data.put("callBackDate", UtilValidate.isNotEmpty(call.getDate("callBackDate"))? UtilDateTime.toDateString(call.getDate("callBackDate"), globalDateFormat): "");
						data.put("localTimeZone",UtilValidate.isNotEmpty(call.getString("localTimeZone"))?EnumUtil.getEnumDescription(delegator, call.getString("localTimeZone"), "TIME_ZONE"):"");
						
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
    
    public static String sendEmailPreview(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map<String, Object> context = UtilHttp.getMultiplartParameterMap(request);
		
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		String emails = (String) context.get("emails");
		String orderId = (String) context.get("orderId");
		
		Map<String, Object> result = FastMap.newInstance();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
		
		try {
			if (UtilValidate.isNotEmpty(marketingCampaignId)) {
				
				// TODO need to check why userLogin empty
				if (UtilValidate.isEmpty(userLogin)) {
					userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "admin").queryFirst();
				}
				
				String maxFileSizeMB = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ATTACHMENT_SIZE");
				long maxFileSize = DataHelper.convertMBtoBytes( maxFileSizeMB );
				
				if (UtilValidate.isEmpty(emails) && UtilValidate.isEmpty(context.get("multiPartMap"))) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.NOT_FOUND_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Emails OR Contact List File required!");
					return AjaxEvents.doJSONResponse(response, result);
				}
				
				GenericValue campaign = EntityQuery.use(delegator).from("MarketingCampaign").where("marketingCampaignId", marketingCampaignId).queryFirst();
				if (UtilValidate.isEmpty(campaign)) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.NOT_FOUND_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Campaign not found!");
					return AjaxEvents.doJSONResponse(response, result);
				}
				
				List conditionList = FastList.newInstance();
				
				GenericValue template = EntityQuery.use(delegator).from("TemplateMaster").where("templateId", campaign.getString("campaignTemplateId")).queryFirst();
				if (UtilValidate.isEmpty(template)) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Template not associated!");
					return AjaxEvents.doJSONResponse(response, result);
				}
				
				String extTplId = null;
				if (UtilValidate.isNotEmpty(template.getString("emailEngine")) && GlobalConstants.EXTERNAL_EMAIL_ENGINE.containsKey(template.getString("emailEngine"))) {
					extTplId = UtilAttribute.getTemplateAttrValue(delegator, template.getString("templateId"), "EXT_TPL_ID");
					if (UtilValidate.isEmpty(extTplId)) {
						result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						result.put(GlobalConstants.RESPONSE_MESSAGE, "External Template not associated!");
						return AjaxEvents.doJSONResponse(response, result);
					}
				}
				
				List<Map<String, Object>> contactList = new ArrayList<>();
				Set<String> emailList = new LinkedHashSet<>();
				if (UtilValidate.isNotEmpty(emails)) {
					for (String toEmail : emails.split(",")) {
						if (UtilValidate.isNotEmpty(toEmail) && !emailList.contains(toEmail)) {
							String partyId = org.fio.homeapps.util.DataUtil.getPartyIdByEmail(delegator, toEmail);
							
							Map<String, Object> contact = new LinkedHashMap<>();
							contact.put("partyId", partyId);
							contact.put("email", toEmail);
							contactList.add(contact);
							emailList.add(toEmail);
						}
					}
				}
				
				// Contact list file [start]
				Map < String, Object > multiPartMap = (Map < String, Object > ) context.get("multiPartMap");
				if (UtilValidate.isNotEmpty(multiPartMap)) {
	                Iterator < String > mpit = multiPartMap.keySet().iterator();
	                while (mpit.hasNext()) {
	                    String key = mpit.next();

	                    // Since the ServiceEventHandler adds all form inputs to the map, just deal with the ones matching the correct input name (eg. 'uploadedFile_0', 'uploadedFile_1', etc)
	                    if (!key.startsWith("uploadedFile")) {
	                        continue;
	                    }
	                    // Some browsers will submit an empty string for an empty input type="file", so ignore the ones that are empty
	                    if (UtilValidate.isEmpty(multiPartMap.get(key))) {
	                        continue;
	                    }
	                    
	                    FileItem item = (FileItem) multiPartMap.get(key);
	                    System.out.println("file size> "+item.getSize());
	                    if (item.getSize() > maxFileSize) {
	                    	String msg = "File# "+item.getName()+", Size# "+DataHelper.convertBytestoMB(item.getSize())+"MB, exceed MAX size# "+maxFileSizeMB+"MB";
	                    	Debug.logError(msg, MODULE);
			    			continue;
	                    }
	                    
	                    ByteBuffer uploadedFile = ByteBuffer.allocate(item.get().length);
	                    uploadedFile.put(item.get());
	                    String uploadedFileName = item.getName();
	                    
	                    // Check to see that we have everything
	                    if (UtilValidate.isEmpty(uploadedFileName)) {
	                        continue; // not really a file if there is no name
	                    } else if (UtilValidate.isEmpty(uploadedFile)) {
	                        String errMsg = "Missing file upload data: "+uploadedFileName;
							Debug.logError(errMsg, MODULE);
							continue;
	                    }
	                    
	                    Debug.log("Name from Item [File name]"+item.getName()+" ");
	                    
	                    File tempFile = File.createTempFile("contactlist-"+UtilDateTime.nowTimestamp().getTime(), ".tmp");
	                    item.write(tempFile);
	                    
	                    CSVReader reader = new CSVReader(new java.io.FileReader(tempFile));
	                    String[] nextLine;
	                    reader.readNext();
	                    while ((nextLine = reader.readNext()) != null) {
	                    	if (nextLine.length > 0) {
        						String partyId = nextLine[0];
        						String emailId = nextLine[1];
        						if (UtilValidate.isEmpty(partyId) && UtilValidate.isEmpty(emailId)) {
        							continue;
        						}
        						if (UtilValidate.isEmpty(partyId) && UtilValidate.isNotEmpty(emailId)) {
        							partyId = org.fio.homeapps.util.DataUtil.getPartyIdByEmail(delegator, emailId);
        						}
        						if (UtilValidate.isEmpty(emailId) && UtilValidate.isNotEmpty(partyId)) {
        							emailId = PartyHelper.getEmailAddress(delegator, partyId, "PRIMARY_EMAIL");
        						}
        						if (UtilValidate.isEmpty(emailId)) {
        							continue;
        						}
        						
        						System.out.println("partyId: "+partyId+", emailId: "+emailId);
	        					
        						if (!emailList.contains(emailId)) {
	        						Map<String, Object> contact = new LinkedHashMap<>();
	        						contact.put("partyId", partyId);
	        						contact.put("email", emailId);
	        						contactList.add(contact);
	        						emailList.add(emailId);
        						}
	                    	}
	                    }
	                    
	                    tempFile.delete();
	                }
	            }
				// Contact list file [end]
				
				String clientName = PartyHelper.getPartyName(delegator, "Company", false);
				String subject = template.getString("subject");
				String description = template.getString("description");
				Timestamp publishDate = (Timestamp) campaign.get("startDate");
				String campaignId = campaign.getString("marketingCampaignId");
				String fromEmail = template.getString("senderEmail");
				String templateId = template.getString("templateId");
				
				String emailContent = "";
				String templateFormContent = template.getString("templateFormContent");
				if (UtilValidate.isNotEmpty(templateFormContent)) {
					if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
						templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
					}
				}
				
				String productStoreId = null;
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    			conditions.add(EntityCondition.makeCondition("campaignId", EntityOperator.EQUALS, campaignId));
    			conditions.add(EntityCondition.makeCondition("productStoreType", EntityOperator.EQUALS, "STORE_RECEIPT"));
    			conditions.add(EntityUtil.getFilterByDateExpr());
            	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
    			GenericValue storeAssoc = EntityQuery.use(delegator).select("productStoreId").from("CampaignStoreAssoc").where(mainConditon).orderBy("fromDate DESC").queryFirst();
        		if (UtilValidate.isNotEmpty(storeAssoc)) {
        			productStoreId = storeAssoc.getString("productStoreId");
        		}
        		
        		String emailEngine = null;
        		if (UtilValidate.isNotEmpty(template.getString("emailEngine"))) {
        			emailEngine = template.getString("emailEngine").replace("_ENGINE", "");
        		}
        		
        		List<Object> personalizedTagList = new ArrayList<>();
        		
        		if (UtilValidate.isNotEmpty(orderId)) {
        			List<Map<String, Object>> itmList = new ArrayList<>();
            		List<GenericValue> itms = EntityQuery.use(delegator).from("InvoiceTransactionMaster").where("transactionNumber", orderId).queryList();
            		if (UtilValidate.isNotEmpty(itms)) {
            			GenericValue itm = itms.get(0);
            			for (GenericValue tran : itms) {
            				String productImageUrl = null;
            				String brandName = null;
            				String strainType = null;
            				String productId = UtilProduct.getProductIdByIdentification(delegator, tran.getString("productId"), tran.getString("productIdentificationTypeId"));
            				if (UtilValidate.isNotEmpty(productId)) {
            					Map<String, Object> prod = UtilProduct.getProductDetails(delegator, productId);
            					productImageUrl = ParamUtil.getString(prod, "smallImageUrl");
            					brandName = ParamUtil.getString(prod, "brandName");
            					strainType = ParamUtil.getString(prod, "strainType");
            				}
            				Map<String, Object> line = tran.getAllFields();
        					line.put("productName", tran.getString("skuDescription"));
        					line.put("productImageUrl", productImageUrl);
        					line.put("brandName", brandName);
        					line.put("strainType", strainType);
        					line.put("categoryName", tran.getString("productCategoryName1"));
        					itmList.add(line);
            			}
            			
            			Map<String, Object> oh = new LinkedHashMap<String, Object>();
                		
                		if (UtilValidate.isNotEmpty(itmList)) {
                			BigDecimal grandTotal = itm.getBigDecimal("totalSalesAmount");
                			if (UtilValidate.isNotEmpty(itm.getString("itemStatus")) && itm.getString("itemStatus").equals("RETURN")) {
                				grandTotal = new BigDecimal(0);
                			}
                			
                			oh.put("subTotal", itm.getBigDecimal("totalSalesAmount"));
                			oh.put("orderTax", itm.getBigDecimal("totalTaxAmount"));
                			oh.put("grandTotal", grandTotal);
        				}
                		
                		List<GenericValue> adjList = EntityQuery.use(delegator).from("InvoiceTransactionAdjustments").where("invoiceId", itm.getString("invoiceId")).queryList();
                		List<GenericValue> taxAdjList = EntityQuery.use(delegator).from("InvoiceTransactionTaxAdjustments").where("invoiceId", itm.getString("invoiceId")).queryList();
                		
                		Map<String, Object> tag = new LinkedHashMap<>();
                		tag.put("TAG_NAME", "TRANS_ID");
                		tag.put("TAG_VALUE", orderId);
                		personalizedTagList.add(tag);
                		
                		// prepare barcode [start]
                		BufferedImage prodBarCode = BarCodeGenerator.generateBarCode(orderId);
                		if (UtilValidate.isNotEmpty(prodBarCode)) {
                			String encodedString = BarCodeGenerator.getImageBase64EncodeData(prodBarCode, "jpg");
                			tag = new LinkedHashMap<>();
                			tag.put("TAG_NAME", "PROD_BAR_CODE");
                			tag.put("TAG_VALUE", encodedString);
                			personalizedTagList.add(tag);
                		}
                		// prepare barcode [end]
                		
                		// Populate order html tags [start]
        				List<Map<String, Object>> orderTplTags = UtilTemplate.getInnerTemplateTags(delegator, UtilMisc.toMap("templateId", templateId, "templateCategoryId", "ORDER_TPL"));
        				if (UtilValidate.isNotEmpty(orderTplTags)) {
        					for (Map<String, Object> templateTag : orderTplTags) {
        						String tagId = (String) templateTag.get("tagId");
        						String innerTemplateId = (String) templateTag.get("innerTemplateId");
        						
        						Map<String, Object> orderHtmlContext = new LinkedHashMap<String, Object>();
            					orderHtmlContext.put("orderId", orderId);
            					orderHtmlContext.put("erOrdhtmlTplId", innerTemplateId);
            					orderHtmlContext.put("orderHeader", oh);
            					orderHtmlContext.put("itmList", itmList);
            					orderHtmlContext.put("adjList", adjList);
        						orderHtmlContext.put("taxAdjList", taxAdjList);
            					orderHtmlContext.put("cashierNumber", itm.getString("cashierNumber"));
            					orderHtmlContext.put("storeNumber", itm.getString("storeNumber"));
            					
            					String orderHtml = UtilOrder.generateOrderHtml(delegator, orderHtmlContext);
            					
            					tag = new LinkedHashMap<>();
                        		tag.put("TAG_NAME", tagId);
                        		tag.put("TAG_VALUE", orderHtml);
                        		personalizedTagList.add(tag);
        					}
        				}
        				// Populate order html tags [end]
            		}
        		}
        		
        		String personalizationTags = ParamUtil.toJson(personalizedTagList);
				
				for (Map<String, Object> contact : contactList) {
					String partyId = (String) contact.get("partyId");
					String toEmail = (String) contact.get("email");
					
					// String partyId = org.fio.homeapps.util.DataUtil.getPartyIdByEmail(delegator, toEmail);
					
					Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
					extractContext.put("delegator", delegator);
					extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
					extractContext.put("templateId", templateId);
					extractContext.put("campaignId", campaignId);
					extractContext.put("productStoreId", productStoreId);
					extractContext.put("orderId", orderId);
	    			extractContext.put("emailContent", templateFormContent);
	    			extractContext.put("templateId", templateId);
	    			extractContext.put("personalizationTags", personalizationTags);
	    			extractContext.put("partyId", partyId);
	    			extractContext.put("emailEngine", emailEngine);
	    			
	    			Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
	    			personalizationTags = (String) extractResultContext.get("personalizationTags");
	    			emailContent = (String) extractResultContext.get("emailContent");
	    			
	    			Map<String, Object> rc = FastMap.newInstance();

					rc.put("nsender", fromEmail);
					rc.put("nto", toEmail);
					rc.put("subject", template.getString("subject"));
					rc.put("emailContent", emailContent);
					rc.put("templateId", templateId);
					rc.put("personalizationTags", personalizationTags);
					rc.put("emailEngine", emailEngine);
					rc.put("referenceId", campaignId);
					rc.put("referenceType", "CAMPAIGN");
					rc.put("templateId", templateId);
					rc.put("clientName", clientName);
					rc.put("partyId", partyId);
					rc.put("emailPurposeTypeId", "CMP_EMAIL_PREVIEW");

					callCtxt.put("requestContext", rc);
					callCtxt.put("userLogin", userLogin);

					dispatcher.runAsync("common.sendEmail", callCtxt);
				}
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				
			} else {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.EXPECTATION_FAILED_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "MarketingCampaignId required!");
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
