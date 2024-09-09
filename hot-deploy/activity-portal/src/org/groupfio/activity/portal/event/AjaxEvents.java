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

package org.groupfio.activity.portal.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.constants.GlobalConstants.Channels;
import org.fio.homeapps.constants.GlobalConstants.DateTimeTypeConstant;
import org.fio.homeapps.util.ActivityDataHelper;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.QueryUtil;
import org.fio.homeapps.util.SrDataHelper;
import org.fio.homeapps.util.StatusUtil;
import org.fio.homeapps.util.UtilActivity;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.common.portal.util.SrUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Sharif Ul Islam
 * 
 */
public final class AjaxEvents {
	private static final String MODULE = AjaxEvents.class.getName();
	private static final String SUCCESS = "success";
	private static final String ERROR = "error";
	
	private AjaxEvents() { }


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

	public static String searchActivitys(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map < String, Object > context = UtilHttp.getCombinedMap(request);

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String externalLoginKey = request.getParameter("externalLoginKey");

		//String partyId = (String) context.get("partyId");
		String owner = UtilValidate.isNotEmpty(context.get("owner")) ? (String) context.get("owner") : (String) context.get("partyId");

		String activityNo = (String) context.get("activityNo");
		List defaultActivityTypes = (List) context.get("defaultActivityTypes");

		String activitySubType = (String) context.get("activitySubType");
		String createdBy = (String) context.get("createdBy");
		String open = (String) context.get("open");
		String closed = (String) context.get("closed");
		Timestamp systemTime = UtilDateTime.nowTimestamp();
		String startDate = (String) context.get("startDate");
		String endDate = (String) context.get("endDate");
		ArrayList<String> statuses = new ArrayList<String>();
		
		Object statusId = context.get("statusId");
		Object activityType = context.get("activityType");
		Object activityWorkType = context.get("activityWorkType");

		String searchType = request.getParameter("searchType");
		String isSrActivityOnly = request.getParameter("isSrActivityOnly");
		String requiredSrInfo = request.getParameter("requiredSrInfo");
		Object srTypeId = context.get("srType");
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
			String businessUnit = null;
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
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
			}

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
	            
	            callCtxt.put("activityNo", activityNo);
	            callCtxt.put("activityType", activityType);
	            callCtxt.put("defaultActivityTypes", defaultActivityTypes);
	            callCtxt.put("activitySubType", activitySubType);
	            callCtxt.put("createdBy", createdBy);
	            callCtxt.put("statusId", statusId);
	            callCtxt.put("open", open);
	            callCtxt.put("closed", closed);
	            callCtxt.put("searchType", searchType);
	            callCtxt.put("domainEntityType", domainEntityType);
	            callCtxt.put("srTypeId", srTypeId);
	            callCtxt.put("domainEntityId", domainEntityId);
	            callCtxt.put("scheduledStartDate", request.getParameter("scheduledDate_from"));
	            callCtxt.put("scheduledEndDate", request.getParameter("scheduledDate_to"));
	            callCtxt.put("startDate", request.getParameter("actualDate_from"));
	            callCtxt.put("endDate", request.getParameter("actualDate_to"));
	            callCtxt.put("owner", context.get("technician"));
	            
	            isSrActivityOnly = UtilValidate.isEmpty(isSrActivityOnly) ? "Y" : isSrActivityOnly;
	            callCtxt.put("isSrActivityOnly", isSrActivityOnly);
	            
	            requiredSrInfo = UtilValidate.isEmpty(requiredSrInfo) ? "Y" : requiredSrInfo;
	            callCtxt.put("requiredSrInfo", requiredSrInfo);

	            callCtxt.put("userLogin", userLogin);
	            
	            requestContext.put("totalGridFetch",
						org.groupfio.common.portal.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));
	            requestContext.put("isActiveAssoc", "Y");
	            
	            callCtxt.put("requestContext", requestContext);
				
	            callResult = dispatcher.runSync("activity.findInteractiveActivity", callCtxt);

				if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("workEffortList"))) {
					
					String isApprovalEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPROVAL_ENABLED");

					List<GenericValue> interactiveActivities = (List<GenericValue>) callResult.get("workEffortList");
					Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					Map<String, Object> purposeTypes = ActivityDataHelper.getWorkEffortPurposeTypes(delegator, interactiveActivities);
					Map<String, Object> activityTypes = ActivityDataHelper.getWorkEffortTypes(delegator, interactiveActivities);
					Map<String, Object> serviceTypes = EnumUtil.getEnumList(delegator, interactiveActivities, "workEffortServiceType", "IA_TYPE");
					Map<String, Object> subServiceTypes = EnumUtil.getEnumList(delegator, interactiveActivities, "workEffortSubServiceType", "IA_TYPE");
					Map<String, Object> prioritys = EnumUtil.getEnumList(delegator, interactiveActivities, "priority", "PRIORITY_LEVEL");
					Map<String, Object> directions = EnumUtil.getEnumList(delegator, interactiveActivities, "direction", "PH_DIRECTIONCODE");
					Map<String, Object> statusList = StatusUtil.getStatusList(delegator, interactiveActivities, "currentStatusId", "IA_STATUS_ID");
					
					Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, interactiveActivities, "closedByUserLogin");
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, interactiveActivities, "createdByUserLogin");
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, interactiveActivities, "lastModifiedByUserLogin");
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, interactiveActivities, "completedBy");
					Map<String, Object> srType = new HashMap<String, Object>();
					List<GenericValue> srTypeList = EntityQuery.use(delegator).select("code","value").from("CustRequestAssoc").where(UtilMisc.toMap("type", "SRTYPE", "active", "Y")).queryList();
					if(UtilValidate.isNotEmpty(srTypeList)) {
						srType = DataUtil.getMapFromGeneric(srTypeList, "code", "value", false);
					}
					Map<String, Object> storeNames = SrDataHelper.getProductStoreNames(delegator);
					
					String activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
					String locationCustomFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties",
							"location.customFieldId", delegator);
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					viewIndex = (int) callResult.get("viewIndex");
					highIndex = (int) callResult.get("highIndex");
					lowIndex = (int) callResult.get("lowIndex");
					resultListSize = (int) callResult.get("resultListSize");
					viewSize = (int) callResult.get("viewSize");
					
					int count = 0;
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					for (GenericValue interactiveActivity : interactiveActivities) {
						//Debug.logInfo("data "+(count++)+": "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						Map<String, Object> data = new HashMap<String, Object>();
						
						String workEffortId = interactiveActivity.getString("workEffortId");
						String typeDesc = (String) serviceTypes.get(interactiveActivity.getString("workEffortServiceType"));
						String subTypeDesc = (String) subServiceTypes.get(interactiveActivity.getString("workEffortSubServiceType"));
						
						String workType = (String) purposeTypes.get(interactiveActivity.getString("workEffortPurposeTypeId"));
						data.put("workType", workType);
						
						data.put("iaNumber", interactiveActivity.getString("workEffortId"));
						data.put("workEffortTypeId", interactiveActivity.getString("workEffortTypeId"));
						
						data.put("priority", (String) prioritys.get(interactiveActivity.getString("priority")));
						data.put("direction", (String) directions.get(interactiveActivity.getString("direction")));
						
						if (UtilValidate.isEmpty(typeDesc)) {
							typeDesc = (String) activityTypes.get(interactiveActivity.getString("workEffortTypeId"));
						}
						data.put("activityType", typeDesc);
						
						data.put("activitySubType", subTypeDesc);
						data.put("businessUnit", UtilValidate.isNotEmpty(interactiveActivity.getString("businessUnitName")) ? interactiveActivity.getString("businessUnitName") : "");
						
						data.put("ownerName", org.fio.homeapps.util.UtilActivity.getActivityOwnerName(delegator, activityOwnerRole, workEffortId, true));
						data.put("contactName", org.fio.homeapps.util.UtilActivity.getActivityContactName(delegator, workEffortId));
						
						data.put("phone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(interactiveActivity.getString("phoneNumber")));
						
						data.put("status", statusList.get(interactiveActivity.getString("currentStatusId")));
						
						data.put("subject", interactiveActivity.getString("workEffortName"));
						data.put("description", interactiveActivity.getString("description"));
						data.put("accountNo", "");
						data.put("businessUnit", "");
						
						if (UtilValidate.isNotEmpty(requiredSrInfo) && requiredSrInfo.equals("Y")) {
							data.put("custRequestTypeId", UtilValidate.isNotEmpty(interactiveActivity.getString("custRequestTypeId")) && UtilValidate.isNotEmpty(srType)? srType.get(interactiveActivity.getString("custRequestTypeId")) :"");
						}
						
						String dateDue = "";
						if (UtilValidate.isNotEmpty(interactiveActivity.getString("estimatedCompletionDate"))) {
							dateDue = DataUtil.convertDateTimestamp(interactiveActivity.getString("estimatedCompletionDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("plannedDueDate", dateDue);
						String plannedStartDate = "";
						if (UtilValidate.isNotEmpty(interactiveActivity.getString("estimatedStartDate"))) {
							plannedStartDate = DataUtil.convertDateTimestamp(interactiveActivity.getString("estimatedStartDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("plannedStartDate", plannedStartDate);
						String actualCompletion = "";
						if (UtilValidate.isNotEmpty(interactiveActivity.getString("actualCompletionDate"))) {
							actualCompletion = DataUtil.convertDateTimestamp(interactiveActivity.getString("actualCompletionDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("actualCompletion", actualCompletion);
						if(UtilValidate.isNotEmpty(interactiveActivity.getString("wfOnceDone")) && "Y".equals(interactiveActivity.getString("wfOnceDone"))){
							data.put("onceDone", interactiveActivity.getString("wfOnceDone"));
						}else{
							data.put("onceDone", "");
						}
						data.put("productName", "");

						data.put("comments", interactiveActivity.getString("description"));
						String actualStart = "";
						if (UtilValidate.isNotEmpty(interactiveActivity.getString("actualStartDate"))) {
							actualStart = DataUtil.convertDateTimestamp(interactiveActivity.getString("actualStartDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("actualStart", actualStart);
						
						if(UtilValidate.isNotEmpty(interactiveActivity.getString("duration"))){
							data.put("duration", interactiveActivity.getString("duration")+"Min" );
						}
						if(UtilValidate.isNotEmpty(interactiveActivity.getString("duration"))){
							String durationUntiType = "hr";
							if (Float.parseFloat(interactiveActivity.getString("duration")) > 1) {
								durationUntiType = "hrs";
							}
							data.put("wftMsdduration", interactiveActivity.getString("duration")+" "+durationUntiType);
						}
						
						String overDue = "";                        
						if(UtilValidate.isNotEmpty(interactiveActivity.getTimestamp("estimatedCompletionDate")) && systemTime.after(interactiveActivity.getTimestamp("estimatedCompletionDate"))) {
							overDue = "Y";                            
						}
						String plannedEndDate = "";
						if(UtilValidate.isNotEmpty(interactiveActivity.getTimestamp("estimatedCompletionDate"))){
							plannedEndDate = DataUtil.convertDateTimestamp(interactiveActivity.getString("estimatedCompletionDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("plannedEndDate", plannedEndDate);
						data.put("overDue", overDue);
						
						String closedDate = "";
						if (UtilValidate.isNotEmpty(interactiveActivity.getString("closedDateTime"))) {
							closedDate = UtilDateTime.timeStampToString(interactiveActivity.getTimestamp("closedDateTime"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("closedDate", closedDate);
						data.put("closedByName", partyNames.get(interactiveActivity.getString("closedByUserLogin")));
						
						String createdDate = "";
						if (UtilValidate.isNotEmpty(interactiveActivity.getString("createdDate"))) {
							createdDate = UtilDateTime.timeStampToString(interactiveActivity.getTimestamp("createdDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("createdOn", createdDate);
						data.put("createdByName", partyNames.get(interactiveActivity.getString("createdByUserLogin")));
						
						String modifiedDate = "";
						if (UtilValidate.isNotEmpty(interactiveActivity.getString("lastModifiedDate"))) {
							modifiedDate = UtilDateTime.timeStampToString(interactiveActivity.getTimestamp("lastModifiedDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("modifiedOn", modifiedDate);
						data.put("modifiedByName", partyNames.get(interactiveActivity.getString("lastModifiedByUserLogin")));
						
						data.put("completedByName", partyNames.get(interactiveActivity.getString("completedBy")));
						
						if (UtilValidate.isNotEmpty(workEffortId) && UtilValidate.isNotEmpty(domainEntityType)
								&& domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
							GenericValue salesOpportunityWorkEffort = EntityQuery.use(delegator)
									.select("salesOpportunityId")
									.from("SalesOpportunityWorkEffort")
									.where("workEffortId", workEffortId)
									.queryFirst();
							if (UtilValidate.isNotEmpty(salesOpportunityWorkEffort)) {
								String salesOpportunityId = salesOpportunityWorkEffort.getString("salesOpportunityId");
								if (UtilValidate.isNotEmpty(salesOpportunityId)) {
									data.put("salesOpportunityId", salesOpportunityId);
								}
							}
						}
						
						String arrivalWindow = UtilActivity.getActivityAttrValue(delegator, workEffortId, "TECH_ARRIVAL_WINDOW");
						data.put("arrivalWindow", UtilValidate.isNotEmpty(arrivalWindow) ? arrivalWindow+"hr" : "" );
						
						String isSchedulingRequired = UtilActivity.getActivityAttrValue(delegator, workEffortId, "IS_SCHEDULING_REQUIRED");
						data.put("isScheduled", UtilValidate.isNotEmpty(isSchedulingRequired) ? "N".equals(isSchedulingRequired) ? "No" : "Y".equals(isSchedulingRequired) ? "Yes" :"" : "");
						
						// approval data [start]
						if(UtilValidate.isEmpty(isApprovalEnabled) || isApprovalEnabled.equals("Y")){
							if (UtilValidate.isNotEmpty(interactiveActivity.getString("workEffortTypeId")) && interactiveActivity.getString("workEffortTypeId").equals("TASK")) {
								GenericValue approval = EntityQuery.use(delegator).from("WorkEffortApproval").where("workEffortId", interactiveActivity.getString("workEffortId")).queryFirst();
								if (UtilValidate.isNotEmpty(approval)) {
									data.put("approvalCategoryId", approval.getString("approvalCategoryId"));
								}
							}
						}
						// approval data [end]
						
						data.put("sourceIdLink", org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom((String)interactiveActivity.get("domainEntityId"), (String)interactiveActivity.get("domainEntityType"), externalLoginKey));
						data.put("sourceComponent", org.groupfio.common.portal.util.DataHelper.convertToLabel((String)interactiveActivity.get("domainEntityType")));
						
						data.put("domainEntityId", interactiveActivity.getString("domainEntityId"));
						data.put("domainEntityType", interactiveActivity.getString("domainEntityType"));
						data.put("domainEntityTypeDesc", DataHelper.convertToLabel( interactiveActivity.getString("domainEntityType") ));
						data.put("externalLoginKey", externalLoginKey);
						if (interactiveActivity.containsKey("fromPartyId")) {
							data.put("partyName", UtilValidate.isNotEmpty(interactiveActivity.getString("fromPartyId")) ? PartyHelper.getPartyName(delegator,interactiveActivity.getString("fromPartyId"),false) : "");
						}
						// SR related [start]
						if (interactiveActivity.containsKey("custRequestId")) {
							data.put("srId", interactiveActivity.getString("custRequestId"));
							data.put("srName", interactiveActivity.getString("srName"));
							
							String locationId = SrUtil.getCustRequestAttrValue(delegator, locationCustomFieldId, interactiveActivity.getString("custRequestId"));
							if (UtilValidate.isNotEmpty(locationId)) {
								data.put("location", storeNames.get(locationId));
							}
						}
						// SR related [end]
						
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
			return doJSONResponse(response, e.getMessage());
		}
		
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
		result.put("timeTaken", (end - start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}
	
	public static String updateActivityEvent(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String workEffortTypeId = (String) requestParameters.get("workEffortTypeId");
		String workEffortId = (String) requestParameters.get("workEffortId");
		String srTypeId = (String) requestParameters.get("srTypeId");
		String srSubTypeId = (String) requestParameters.get("srSubTypeId");
		String actualStartDateStr = (String) requestParameters.get("actualStartDate");
		String actualCompletionDateStr = (String) requestParameters.get("actualCompletionDate");
		String estimatedStartDateStr = (String) requestParameters.get("estimatedStartDate");
		String estimatedCompletionDateStr = (String) requestParameters.get("estimatedCompletionDate");
		String callDateTime = (String) requestParameters.get("callDateTime");
		String subject = (String) requestParameters.get("subject");
		String resolution = (String) requestParameters.get("resolution");
		String linkedFrom = (String) requestParameters.get("linkedFrom");
		String productId = (String) requestParameters.get("productId");
		String account = (String) requestParameters.get("account");
		String accountProduct = (String) requestParameters.get("accountProduct");
		String emailFormContent = (String) requestParameters.get("emailFormContent");
		String onceDone = (String) requestParameters.get("onceDone");
		String messages = (String) requestParameters.get("messages");
		String location = (String) requestParameters.get("location");
		String startTimeStr = (String) requestParameters.get("startTime");
		String duration = (String) requestParameters.get("duration");
		String salesOpportunityId = (String) requestParameters.get("salesOpportunityId");
		//String owner = (String) requestParameters.get("owner");
		String emplTeamId = (String) request.getParameter("emplTeamId");
		String currentStatusId = (String) request.getParameter("statusId");
		String entityTimeZoneId = (String) requestParameters.get("timeZoneDesc");
		String ownerBu = (String) requestParameters.get("ownerBu");
		String customerCIN = (String) requestParameters.get("cNo");
		String priority = (String) requestParameters.get("priority");
		String direction = (String) requestParameters.get("direction");
		String phoneNumber = (String) requestParameters.get("phoneNumber");
		String nsender = (String) requestParameters.get("nsender");
		String nto = (String) requestParameters.get("nto");
		String ncc = (String) requestParameters.get("ncc");
		String nbcc = (String) requestParameters.get("nbcc");
		String template = (String) requestParameters.get("template");
		String workEffortPurposeTypeId = (String) requestParameters.get("workEffortPurposeTypeId");
		String ownerBookedCalSlots = (String) requestParameters.get("ownerBookedCalSlots");
		//String optionalAttendees = (String) requestParameters.get("optionalAttendees");
		//String requiredAttendees = (String) requestParameters.get("requiredAttendees");
		List<String> optionalAttendees = null;
		List<String> requiredAttendees = null;

		String optionalAttendeesClassName= "";
		String requiredAttendeesClassName = "";

		if (UtilValidate.isNotEmpty(requestParameters.get("optionalAttendees"))){
			optionalAttendeesClassName = requestParameters.get("optionalAttendees").getClass().getName();
		}
		if (UtilValidate.isNotEmpty(requestParameters.get("requiredAttendees"))){
			requiredAttendeesClassName = requestParameters.get("requiredAttendees").getClass().getName();
		}

		if (UtilValidate.isNotEmpty(optionalAttendeesClassName) && "java.lang.String".equals(optionalAttendeesClassName)) {
			optionalAttendees = UtilMisc.toList((String) requestParameters.get("optionalAttendees"));
		}else if(UtilValidate.isNotEmpty(optionalAttendeesClassName) && "java.util.LinkedList".equals(optionalAttendeesClassName) ){
			optionalAttendees = (List<String>) requestParameters.get("optionalAttendees");
		}

		if (UtilValidate.isNotEmpty(requiredAttendeesClassName) && "java.lang.String".equals(requiredAttendeesClassName)) {
			requiredAttendees = UtilMisc.toList((String) requestParameters.get("requiredAttendees"));
		}else if(UtilValidate.isNotEmpty(requiredAttendeesClassName) && "java.util.LinkedList".equals(requiredAttendeesClassName) ){
			requiredAttendees = (List<String>) requestParameters.get("requiredAttendees");
		}

		String nrecepient = (String) requestParameters.get("nrecepient");
		String norganizer = (String) requestParameters.get("norganizer");
		String isPhoneCall = (String) requestParameters.get("isPhoneCall");
		String domainEntityType = (String) requestParameters.get("domainEntityType");
		String domainEntityId = (String) requestParameters.get("domainEntityId");
		String communicationEventId = (String) requestParameters.get("communicationEventId");
		String emailContent = (String) requestParameters.get("emailContent");
		boolean existing = ((communicationEventId == null) || communicationEventId.equals("") ? false : true);
		String contactId = (String) request.getParameter("contactId");
		
		String estStartDate = (String) requestParameters.get("estimatedStartDate_date");
		String estCompletionDate = (String) requestParameters.get("estimatedCompletionDate_date");
		String actStartDate = (String) requestParameters.get("actualStartDate_date");
		String actCompletionDate = (String) requestParameters.get("actualCompletionDate_date");
		
		String estimatedStartTime = (String) requestParameters.get("estimatedStartDate_time");
		String estimatedCompletionTime = (String) requestParameters.get("estimatedCompletionDate_time");
		String actualStartTime = (String) requestParameters.get("actualStartDate_time");
		String actualCompletionTime = (String) requestParameters.get("actualCompletionDate_time");
		List<String> ownerList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(requestParameters.get("owner")) && requestParameters.get("owner") instanceof String) {
			String owner1 = (String) requestParameters.get("owner");
			if(UtilValidate.isNotEmpty(owner1)) ownerList.add(owner1);
		} else if(UtilValidate.isNotEmpty(requestParameters.get("owner")) && requestParameters.get("owner") instanceof List<?>) {
			ownerList = new ArrayList<>(
				      new HashSet<>((List<String>) requestParameters.get("owner")));
		}
		String arrivalWindow = (String) requestParameters.get("arrivalWindow");
		
		try {
			Map<String, Object> inMap = new HashMap<String, Object>();
			if(UtilValidate.isNotEmpty(workEffortId)){
				
				String userLoginId = userLogin.getString("userLoginId");
				String accessLevel = "Y";
				String businessUnit = null;
				String teamId = "";

				Map<String, Object> workEffortData = DataUtil.getWorkEffortDetail(delegator, workEffortId);
				if(UtilValidate.isNotEmpty(workEffortData)) {
					businessUnit = UtilValidate.isNotEmpty(workEffortData.get("businessUnit")) ? (String) workEffortData.get("businessUnit") : "";
					teamId = UtilValidate.isNotEmpty(workEffortData.get("teamId")) ? (String) workEffortData.get("teamId") : "";
				}
				Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
				if (UtilValidate.isNotEmpty(userLoginId)) {
					String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
					if(UtilValidate.isEmpty(businessUnit))
						businessUnit = DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
					Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
					accessMatrixMap.put("delegator", delegator);
					accessMatrixMap.put("dispatcher", dispatcher);
					accessMatrixMap.put("teamId", teamId);
					accessMatrixMap.put("businessUnit", businessUnit);
					accessMatrixMap.put("modeOfOp", "Update");
					accessMatrixMap.put("entityName", "WorkEffort");
					accessMatrixMap.put("userLoginId", userLoginId);
					accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
					if (UtilValidate.isNotEmpty(accessMatrixRes)) {
						accessLevel = (String) accessMatrixRes.get("accessLevel");
					} else {
						accessLevel = null;
					}
					
					//validate the common team and access for the assignment
					String currentPartyId = userLoginPartyId;
					if(UtilValidate.isEmpty(ownerList)) {
						ownerList.add(userLoginId);
					} else {
						for(String owner : ownerList) {

							currentPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
							Map<String, Object> buTeamData = DataUtil.getUserBuTeam(delegator, currentPartyId);
							businessUnit = (String) buTeamData.get("businessUnit");
							teamId = (String) buTeamData.get("emplTeamId");

							List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
							//change the access in the create 
							//check with ownerId
							if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
								@SuppressWarnings("unchecked")
								List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
								if(!ownerIds.contains(teamId)) accessLevel = null;
								conditionList.add(EntityCondition.makeCondition("ownerId", EntityOperator.IN, ownerIds));
							}

							//check with emplTeamId
							if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
								@SuppressWarnings("unchecked")
								List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
								if(!emplTeamIds.contains(teamId)) accessLevel = null;
								conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
							}

							conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
							DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
							dynamicViewEntity.addMemberEntity("WE", "WorkEffort");
							dynamicViewEntity.addAlias("WE", "workEffortId");
							dynamicViewEntity.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
							dynamicViewEntity.addAlias("WEPA", "ownerId");
							dynamicViewEntity.addAlias("WEPA", "emplTeamId");
							dynamicViewEntity.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));

							EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							GenericValue activity = EntityQuery.use(delegator).from(dynamicViewEntity).where(mainConditons).queryFirst();
							if(UtilValidate.isEmpty(activity)) accessLevel=null;

							if(accessLevel == null) break;
						}
					}
					
				}
				if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
					Timestamp estimatedStartDateTime = ParamUtil.getTimestamp(estStartDate, estimatedStartTime, "yyyy-MM-dd HH:mm");
					Timestamp estimatedCompletionDateTime = ParamUtil.getTimestamp(estCompletionDate, estimatedCompletionTime, "yyyy-MM-dd HH:mm");
					Timestamp actualStartDateTime = ParamUtil.getTimestamp(actStartDate, actualStartTime, "yyyy-MM-dd HH:mm");
					Timestamp actualCompletionDateTime = ParamUtil.getTimestamp(actCompletionDate, actualCompletionTime, "yyyy-MM-dd HH:mm");
					
					if (UtilValidate.isEmpty(estimatedStartDateTime) && UtilValidate.isNotEmpty(estimatedStartDateStr)) {
						estimatedStartDateTime = UtilDateTime.stringToTimeStamp(estimatedStartDateStr, "yyyy-MM-dd HH:mm", TimeZone.getDefault(), null);
					}
					if (UtilValidate.isEmpty(estimatedCompletionDateTime) && UtilValidate.isNotEmpty(estimatedCompletionDateStr)) {
						estimatedCompletionDateTime = UtilDateTime.stringToTimeStamp(estimatedCompletionDateStr, "yyyy-MM-dd HH:mm", TimeZone.getDefault(), null);
					}
					
					if (UtilValidate.isNotEmpty(estimatedStartDateTime)) {
						estimatedStartDateStr = UtilDateTime.timeStampToString(estimatedStartDateTime, "MM/dd/yyyy HH:mm", TimeZone.getDefault(), null);
					}
					if (UtilValidate.isNotEmpty(estimatedCompletionDateTime)) {
						estimatedCompletionDateStr = UtilDateTime.timeStampToString(estimatedCompletionDateTime, "MM/dd/yyyy HH:mm", TimeZone.getDefault(), null);
					}
					
					if (UtilValidate.isNotEmpty(actualStartDateTime)) {
						actualStartDateStr = UtilDateTime.timeStampToString(actualStartDateTime, "MM/dd/yyyy HH:mm", TimeZone.getDefault(), null);
					}
					if (UtilValidate.isNotEmpty(actualCompletionDateTime)) {
						actualCompletionDateStr = UtilDateTime.timeStampToString(actualCompletionDateTime, "MM/dd/yyyy HH:mm", TimeZone.getDefault(), null);
					}
					
					Timestamp date = null;
					/*if(UtilValidate.isNotEmpty(dateStr)){
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						date = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
					}*/
					if(UtilValidate.isNotEmpty(startTimeStr)){
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
						date = new java.sql.Timestamp(sdf.parse(startTimeStr).getTime());
					}

					Timestamp callDate = null;
					if(UtilValidate.isNotEmpty(callDateTime)){
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
						callDate = new java.sql.Timestamp(sdf.parse(callDateTime).getTime());
					}
					Timestamp actualStartDate = null;
					if(UtilValidate.isNotEmpty(actualStartDateStr)){
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
						actualStartDate = new java.sql.Timestamp(sdf.parse(actualStartDateStr).getTime());
					}
					Timestamp actualCompletionDate = null;
					if(UtilValidate.isNotEmpty(actualCompletionDateStr)){
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
						actualCompletionDate = new java.sql.Timestamp(sdf.parse(actualCompletionDateStr).getTime());
					}
					Timestamp estimatedStartDate = null;
					if(UtilValidate.isNotEmpty(estimatedStartDateStr)){
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
						estimatedStartDate = new java.sql.Timestamp(sdf.parse(estimatedStartDateStr).getTime());
					}
					Timestamp estimatedCompletionDate = null;
					if(UtilValidate.isNotEmpty(estimatedCompletionDateStr)){
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
						estimatedCompletionDate = new java.sql.Timestamp(sdf.parse(estimatedCompletionDateStr).getTime());
					}
					if (UtilValidate.isEmpty(messages) || messages == null) {
						if (UtilValidate.isNotEmpty(emailFormContent)) {
							try {
								messages = Base64.getEncoder().encodeToString(emailFormContent.getBytes("utf-8"));
								inMap.put("description", messages);
							} catch (UnsupportedEncodingException e) {
								Debug.logError(e, e.getMessage(), MODULE);
							}
						}
					} else {
						try {
							messages = Base64.getEncoder().encodeToString(messages.getBytes("utf-8"));
							inMap.put("description", messages);
						} catch (UnsupportedEncodingException e) {
							Debug.logError(e, e.getMessage(), MODULE);
						}
					}
					/*
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", owner).queryList());
					if (UtilValidate.isNotEmpty(owner)&&UtilValidate.isNotEmpty(emplTeam)) { 
						if (UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId")))
							emplTeamId = emplTeam.getString("emplTeamId");
					}*/
					
					if (UtilValidate.isNotEmpty(workEffortTypeId)) {
						List<EntityCondition> conditionlist = FastList.newInstance();
						conditionlist.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS, workEffortTypeId));
						EntityCondition workTypeCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
						GenericValue workTypeIdentification = EntityQuery.use(delegator).from("WorkEffortType").where(workTypeCondition).queryFirst();
						if(UtilValidate.isNotEmpty(workTypeIdentification)) {
							workEffortTypeId = workTypeIdentification.getString("workEffortTypeId");
							inMap.put("workEffortTypeId", workEffortTypeId);			
						}
					}

					EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND, EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, customerCIN));

					GenericValue partyIdentification = EntityUtil.getFirst(delegator.findList("PartyIdentification", condition, null, UtilMisc.toList("-createdStamp"), null, false));

					inMap.put("userLogin",userLogin);
					
					if(UtilValidate.isNotEmpty(emplTeamId)) {
						inMap.put("emplTeamId", emplTeamId);
					}
					if(UtilValidate.isNotEmpty(workEffortId)) {
						inMap.put("externalId", workEffortId);
					}
					if(UtilValidate.isNotEmpty(srTypeId)) {
						inMap.put("workEffortServiceType", srTypeId);
					}
					if(UtilValidate.isNotEmpty(srSubTypeId)) {
						inMap.put("workEffortSubServiceType", srSubTypeId);
					}
					if (UtilValidate.isNotEmpty(direction)) {
						inMap.put("direction", direction);
					}
					if(UtilValidate.isNotEmpty(subject)) {
						inMap.put("workEffortName", subject);
					}
					if(UtilValidate.isNotEmpty(currentStatusId)) {
						inMap.put("currentStatusId", currentStatusId);
					}
					if(UtilValidate.isNotEmpty(date)) {
						inMap.put("estimatedStartDate", date);
					}
					if(UtilValidate.isNotEmpty(actualCompletionDate)) {
						inMap.put("actualCompletionDate", actualCompletionDate);
					}
					if(UtilValidate.isNotEmpty(estimatedCompletionDate)) {
						inMap.put("estimatedCompletionDate", estimatedCompletionDate);
					}
					if(UtilValidate.isNotEmpty(estimatedStartDate)) {
						inMap.put("estimatedStartDate", estimatedStartDate);
					}
					if(UtilValidate.isNotEmpty(actualStartDate)) {
						inMap.put("actualStartDate", actualStartDate);
					}
					if (UtilValidate.isNotEmpty(optionalAttendees)) {
						inMap.put("noptional", UtilMisc.toList(optionalAttendees));
					}
					if (UtilValidate.isNotEmpty(requiredAttendees)) {
						inMap.put("nrequired", UtilMisc.toList(requiredAttendees));
					}
					if (UtilValidate.isNotEmpty(norganizer)) {
						inMap.put("norganizer", UtilMisc.toList(norganizer));
					}
					if (UtilValidate.isNotEmpty(nrecepient)) {
						inMap.put("nrecipient", UtilMisc.toList(nrecepient));
					}
					//inMap.put("isPhoneCall", isPhoneCall);
					if (UtilValidate.isNotEmpty(phoneNumber)) {
						inMap.put("phoneNumber", phoneNumber);
					}
					if(UtilValidate.isNotEmpty(resolution)) {
						inMap.put("resolution", resolution);
					}
					if (UtilValidate.isNotEmpty(priority)) {
						inMap.put("priority", Long.valueOf(priority));
					}
					if(UtilValidate.isNotEmpty(onceDone)) {
						inMap.put("wfOnceDone", onceDone);
					}
					if (UtilValidate.isNotEmpty(nsender)) {
						inMap.put("nsender", UtilMisc.toList(nsender));
					}
					if (UtilValidate.isNotEmpty(nto)) {
						inMap.put("nto", UtilMisc.toList(nto));
					}
					if (UtilValidate.isNotEmpty(ncc)) {
						inMap.put("ncc", UtilMisc.toList(ncc));
					}
					if (UtilValidate.isNotEmpty(nbcc)) {
						inMap.put("nbcc", UtilMisc.toList(nbcc));
					}
					if (UtilValidate.isNotEmpty(template)) {
						inMap.put("wftMsdsubjecttemplate", template);
					}
					if(UtilValidate.isNotEmpty(messages)) {
						inMap.put("message", messages);
					}
					/*
					if (UtilValidate.isNotEmpty(owner)) {
						inMap.put("primOwnerId", owner);
					} */
					if (UtilValidate.isNotEmpty(ownerBu)) {
						inMap.put("wftMsdbusinessunit", ownerBu);
						inMap.put("businessUnitId", ownerBu);
					}
					if(UtilValidate.isNotEmpty(location)){
						inMap.put("wftLocation",location);
					}
					if(UtilValidate.isNotEmpty(duration)){
						inMap.put("wftMsdduration",duration);
					}
					if(UtilValidate.isNotEmpty(entityTimeZoneId)){
						inMap.put("entityTimeZoneId",entityTimeZoneId);
					}
					inMap.put("endPointType", "OFCRM");
					String partyId = null;
					if (UtilValidate.isNotEmpty(partyIdentification)) {
						partyId = partyIdentification.getString("partyId");
					}
					if(UtilValidate.isNotEmpty(customerCIN)){
						inMap.put("partyId", customerCIN);
						if (UtilValidate.isEmpty(partyId)) {
							partyId = customerCIN;
						}
					}else if (UtilValidate.isNotEmpty(partyId)) {
						inMap.put("partyId", partyId);
					}
					if (UtilValidate.isNotEmpty(salesOpportunityId)) {
						inMap.put("salesOpportunityId", salesOpportunityId);
					}

					/*EntityCondition rolecondition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
							EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "_NA_"));

					GenericValue roleIdentification = EntityUtil.getFirst(delegator.findList("PartyRole", rolecondition, null, null, null, false));
					String roleTypeId = null;
					if (UtilValidate.isNotEmpty(roleIdentification)) {
						roleTypeId = roleIdentification.getString("roleTypeId");
					}*/

					GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
					String roleTypeId = party.getString("roleTypeId");

					if (UtilValidate.isNotEmpty(roleTypeId)) {
						if ("CUSTOMER".equalsIgnoreCase(roleTypeId)) {
							inMap.put("roleTypeId", "01");
						}else if ("PROSPECT".equalsIgnoreCase(roleTypeId)) {
							inMap.put("roleTypeId", "02");
						}else if ("NON_CRM".equalsIgnoreCase(roleTypeId)) {
							inMap.put("roleTypeId", "07");
						} else {
							inMap.put("roleTypeId", roleTypeId);
						}
					}

					inMap.put("endPointType", "OFCRM");
					inMap.put("domainEntityType", domainEntityType);
					inMap.put("domainEntityId", domainEntityId);
					if(UtilValidate.isNotEmpty(onceDone) && "Y".equals(onceDone)){
						inMap.put("currentStatusId", "IA_MCOMPLETED");
					}
					/*
					String activityOwnerRole = DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
					if(UtilValidate.isNotEmpty(activityOwnerRole)) {
						List<String> ownerRoles = new ArrayList<>();
						if(UtilValidate.isNotEmpty(owner) && UtilValidate.isNotEmpty(activityOwnerRole) && activityOwnerRole.contains(",")) {
							ownerRoles = org.fio.admin.portal.util.DataUtil.stringToList(activityOwnerRole, ",");
						} else
							ownerRoles.add(activityOwnerRole);

						String owenerPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, owner);

						EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,owenerPartyId),
								EntityCondition.makeCondition("roleTypeId", EntityOperator.IN,ownerRoles)
								);
						GenericValue partyRole = EntityQuery.use(delegator).from("PartyRole").where(condition1).queryFirst();
						if(UtilValidate.isNotEmpty(partyRole)) {
							activityOwnerRole = partyRole.getString("roleTypeId");
						} else {
							activityOwnerRole = "";
						}
						inMap.put("ownerRoleTypeId", activityOwnerRole);
					} 
					*/
					inMap.put("ownerList", ownerList);
					if(UtilValidate.isNotEmpty(workEffortPurposeTypeId)){
						inMap.put("workEffortPurposeTypeId", workEffortPurposeTypeId);
					}
					inMap.put("ownerBookedCalSlots", ownerBookedCalSlots);
					
					Map<String, Object> res = dispatcher.runSync("crmPortal.updateInteractiveActivity", inMap); //dispatcher.runSync("srPortal.UpdateActivity", inputMap);
					
					Debug.log("res----------"+res);
					Map<String, Object> tmpResult = null;
					String extContactId="";
					if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("workEffortId"))) {

						GenericValue updateContactRecords = EntityUtil.getFirst(delegator.findByAnd("WorkEffortContact", UtilMisc.toMap("workEffortId", workEffortId,"thruDate",null), null, false));
						if(UtilValidate.isNotEmpty(updateContactRecords))
							extContactId=updateContactRecords.getString("partyId");

						Debug.log("updateContactRecords============="+updateContactRecords+contactId);
						if(UtilValidate.isNotEmpty(extContactId)&&UtilValidate.isNotEmpty(contactId)&& !extContactId.equals(contactId)){
							updateContactRecords.put("thruDate",UtilDateTime.nowTimestamp());
							updateContactRecords.store();
							GenericValue workEffortContact = delegator.makeValue("WorkEffortContact");

							workEffortContact.set("workEffortId",workEffortId);
							workEffortContact.set("partyId", contactId);
							workEffortContact.set("roleTypeId", "CONTACT");
							workEffortContact.set("fromDate", UtilDateTime.nowTimestamp());
							workEffortContact.set("createdByUserLogin", userLogin.getString("userLoginId"));
							workEffortContact.set("thruDate",null);
							delegator.create(workEffortContact);
							Debug.log("updateContactRecords============="+workEffortContact+contactId);
						}
						
						//associate the activity owner with SR
						if(UtilValidate.isNotEmpty(ownerList)) {
							GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId,"domainEntityType","SERVICE_REQUEST").queryFirst();
							String custRequestId = UtilValidate.isNotEmpty(workEffort)? workEffort.getString("domainEntityId") : "";
							if(UtilValidate.isNotEmpty(custRequestId)) {
								org.groupfio.activity.portal.util.DataHelper.reAssignOwnerCustRequestParty(delegator, userLogin, custRequestId, ownerList, workEffortId);
							}
						}
						
						//add workeffortattribute
						if(UtilValidate.isNotEmpty(arrivalWindow)) {
							GenericValue workEffortAttr = EntityQuery.use(delegator).from("WorkEffortAttribute").where("workEffortId",workEffortId,"attrName","TECH_ARRIVAL_WINDOW").queryFirst();
							if(UtilValidate.isNotEmpty(workEffortAttr)) {
								workEffortAttr.set("attrValue", arrivalWindow);
								workEffortAttr.store();
							} else {
								workEffortAttr = delegator.makeValue("WorkEffortAttribute");
								workEffortAttr.set("workEffortId", workEffortId);
								workEffortAttr.set("attrName","TECH_ARRIVAL_WINDOW");
								workEffortAttr.set("attrValue",arrivalWindow);
								workEffortAttr.create();
							}
						}
						
						
						request.setAttribute("_EVENT_MESSAGE_", res.get("_EVENT_MESSAGE_"));
						request.setAttribute("workEffortId", res.get("workEffortId"));
					}
					else {
						request.setAttribute("_ERROR_MESSAGE_", res.get("_EVENT_MESSAGE_"));
						return ERROR;

					}
				} else {
					String errMsg = "";
					if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
						errMsg = accessMatrixRes.get("errorMessage").toString();
					} else {
						errMsg = "Access Denied";
					}
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					return ERROR;
				}
			}else{
				String errMsg = "Please check Activity id   is Missing in Dyna";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return ERROR;

			}
		} catch (Exception e) {
			String errMsg = "Problem While Updating Activity " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			Debug.logError(e, e.getMessage(), MODULE);
			return ERROR;
		}
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public static String getActivityDashboardData(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		List<Object> values = new ArrayList<>();
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String externalLoginKey = (String) context.get("externalLoginKey");
		//List<GenericValue> resultList = null;
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		String filterBy = (String) context.get("filterBy");
		String filterType = (String) context.get("filterType");
		String defaultActType = (String) context.get("defaultActType");
		List<String> defaultActivityTypes = new ArrayList<>();
		if(UtilValidate.isNotEmpty(context.get("defaultActivityTypes")) && context.get("defaultActivityTypes") instanceof String) {
			String defaultActivityType = (String) context.get("defaultActivityTypes");
			if(UtilValidate.isNotEmpty(defaultActivityType)) defaultActivityTypes.add(defaultActivityType);
		} else if(UtilValidate.isNotEmpty(context.get("defaultActivityTypes")) && context.get("defaultActivityTypes") instanceof List<?>) {
			defaultActivityTypes = (List<String>) context.get("defaultActivityTypes");
		}

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		
		String realCoordinator = (String) context.get("realCoordinator");
		
		String isChecklistActivity = (String) context.get("isChecklistActivity");
		
		List<Map<String, Object>> dataList = new ArrayList<>();
		long start = System.currentTimeMillis();
		Timestamp systemTime = UtilDateTime.nowTimestamp();
		
		try {
			String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);
			Map<String, Object> storeNames = SrDataHelper.getProductStoreNames(delegator);
			String userLoginId = userLogin.getString("userLoginId");
			
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			int highIndex = 0;
            int lowIndex = 0;
            int resultListSize = 0;
            int viewSize = 0;
			if((UtilValidate.isNotEmpty(filterType) || UtilValidate.isNotEmpty(filterBy))  && UtilValidate.isNotEmpty(userLoginId)) {
				/*
				Set<String> fieldsToSelect = new LinkedHashSet<String>();
				
				fieldsToSelect.add("workEffortId"); fieldsToSelect.add("workEffortServiceType");fieldsToSelect.add("workEffortSubServiceType");
				fieldsToSelect.add("workEffortPurposeTypeId"); fieldsToSelect.add("phoneNumber");fieldsToSelect.add("workEffortName");
				fieldsToSelect.add("estimatedCompletionDate");fieldsToSelect.add("estimatedStartDate");fieldsToSelect.add("duration");
				fieldsToSelect.add("actualCompletionDate");fieldsToSelect.add("wfOnceDone");fieldsToSelect.add("sourceReferenceId");
				fieldsToSelect.add("description");fieldsToSelect.add("currentStatusId");fieldsToSelect.add("actualStartDate");
				fieldsToSelect.add("workEffortTypeId");fieldsToSelect.add("completedBy");fieldsToSelect.add("closedDateTime");
				fieldsToSelect.add("closedByUserLogin");fieldsToSelect.add("lastModifiedDate");fieldsToSelect.add("lastModifiedByUserLogin");
				fieldsToSelect.add("createdByUserLogin");fieldsToSelect.add("partyId");
				fieldsToSelect.add("createdDate");fieldsToSelect.add("wfNationalId");
				fieldsToSelect.add("scopeEnumId");fieldsToSelect.add("wfVplusId");
				fieldsToSelect.add("lastUpdatedStamp");fieldsToSelect.add("primOwnerId");
				fieldsToSelect.add("businessUnitName");fieldsToSelect.add("priority");
				fieldsToSelect.add("direction");fieldsToSelect.add("ownerPartyId");
				fieldsToSelect.add("emplTeamId");fieldsToSelect.add("ownerId");
				 */
				
				String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, userLoginPartyId);
				String businessUnit = (String) buTeamData.get("businessUnit");
				/*
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				
				dynamicView.addMemberEntity("WE", "WorkEffort");
				dynamicView.addAlias("WE", "workEffortId","workEffortId", null, Boolean.FALSE, Boolean.TRUE, null);
				dynamicView.addAlias("WE", "externalId");
				dynamicView.addAlias("WE", "workEffortTypeId");
				dynamicView.addAlias("WE", "workEffortPurposeTypeId");
				dynamicView.addAlias("WE", "workEffortServiceType");
				dynamicView.addAlias("WE", "workEffortSubServiceType");
				dynamicView.addAlias("WE", "scopeEnumId");
				dynamicView.addAlias("WE", "currentStatusId");
				dynamicView.addAlias("WE", "currentSubStatusId");
				dynamicView.addAlias("WE", "workEffortName");
				dynamicView.addAlias("WE", "description");
				dynamicView.addAlias("WE", "phoneNumber");
				dynamicView.addAlias("WE", "wfNationalId");
				dynamicView.addAlias("WE", "wfVplusId");
				dynamicView.addAlias("WE", "estimatedStartDate");
				dynamicView.addAlias("WE", "estimatedCompletionDate");
				dynamicView.addAlias("WE", "actualStartDate");
				dynamicView.addAlias("WE", "actualCompletionDate");
				dynamicView.addAlias("WE", "duration");
				dynamicView.addAlias("WE", "lastModifiedByUserLogin");
				dynamicView.addAlias("WE", "channelId");
				dynamicView.addAlias("WE", "wfOnceDone");
				dynamicView.addAlias("WE", "lastUpdatedTxStamp");
				dynamicView.addAlias("WE", "createdByUserLogin");
				dynamicView.addAlias("WE", "primOwnerId");
				dynamicView.addAlias("WE", "createdDate");
				dynamicView.addAlias("WE", "lastModifiedDate");
				dynamicView.addAlias("WE", "sourceReferenceId");
				dynamicView.addAlias("WE", "cif");
				dynamicView.addAlias("WE", "businessUnitName");
				dynamicView.addAlias("WE", "priority");
				dynamicView.addAlias("WE", "direction");
				dynamicView.addAlias("WE", "ownerPartyId");
				dynamicView.addAlias("WE", "locationDesc");
				dynamicView.addAlias("WE", "domainEntityId");
				dynamicView.addAlias("WE", "domainEntityType");
				dynamicView.addAlias("WE", "completedBy");
				dynamicView.addAlias("WE", "closedDateTime");
				dynamicView.addAlias("WE", "closedByUserLogin");
				
				dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
				dynamicView.addAlias("WEPA", "partyId");
				dynamicView.addAlias("WEPA", "roleTypeId");
				dynamicView.addAlias("WEPA", "fromDate");
				dynamicView.addAlias("WEPA", "thruDate");
				dynamicView.addAlias("WEPA", "statusId");
				dynamicView.addAlias("WEPA", "statusDateTime");
				dynamicView.addAlias("WEPA", "callOutCome");
				dynamicView.addAlias("WEPA", "assignedByUserLoginId");
				dynamicView.addAlias("WEPA", "facilityId");
				dynamicView.addAlias("WEPA", "ownerId");
				dynamicView.addAlias("WEPA", "emplTeamId");
				dynamicView.addAlias("WEPA", "businessUnit");
				
				dynamicView.addViewLink("WE", "WEPA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
				*/
				String _where_condition = "";
				List<String> activityTypes = new ArrayList<>();
				if(UtilValidate.isNotEmpty(defaultActType) && defaultActType.contains(",")) {
					activityTypes = org.fio.admin.portal.util.DataUtil.stringToList(defaultActType, ",");
				} else if(UtilValidate.isNotEmpty(defaultActType)) {
					activityTypes.add(defaultActType);
				}
				if (UtilValidate.isNotEmpty(activityTypes)) {
					_where_condition = _where_condition + "WE.work_effort_type_id IN ("+org.fio.admin.portal.util.DataUtil.toList(activityTypes, "")+")";
				} else {
					//conditions.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.IN, defaultActivityTypes));
					_where_condition = _where_condition + "WE.work_effort_type_id IN ("+org.fio.admin.portal.util.DataUtil.toList(defaultActivityTypes, "")+")";
				}
				
				// TODO its temporary solution, need to find out better ways
				if (!org.fio.homeapps.util.DataUtil.isPartyRoleExists(delegator, userLogin.getString("partyId"), "REBATE")) {
					_where_condition = _where_condition + " AND WE.work_effort_name NOT LIKE '%Approval task%'";
				}
				
				if("my-activities".equals(filterType)) {
					//conditions.add(EntityCondition.makeCondition("ownerId", EntityOperator.EQUALS, userLoginId));
					if(UtilValidate.isNotEmpty(_where_condition)) {
						_where_condition = _where_condition +" AND WEPA.owner_id = ? ";
						values.add(userLoginId);
					}else {
						_where_condition = _where_condition +" WEPA.owner_id = ? ";
						values.add(userLoginId);
					}
					
				} else if("my-teams-activities".equals(filterType)) {
					List<GenericValue> emplPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("partyId",userLoginPartyId).filterByDate().queryList();
					List<String> teams = UtilValidate.isNotEmpty(emplPositionFulfillments) ? EntityUtil.getFieldListFromEntityList(emplPositionFulfillments, "emplTeamId", true) : new ArrayList<>();
					//conditions.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, teams));
					if(UtilValidate.isNotEmpty(_where_condition))
						_where_condition = _where_condition +" AND WEPA.empl_team_id IN ( "+org.fio.admin.portal.util.DataUtil.toList(teams, "")+")";
					else
						_where_condition = _where_condition +" WEPA.empl_team_id IN ("+org.fio.admin.portal.util.DataUtil.toList(teams, "")+")";
					
				} else if("my-bu-activities".equals(filterType)) {
					
					//conditions.add(EntityCondition.makeCondition("businessUnit", EntityOperator.EQUALS, businessUnit));
					/*
					if(UtilValidate.isNotEmpty(_where_condition))
						_where_condition = _where_condition +" AND WEPA.business_unit = '"+businessUnit+"'";
					else
						_where_condition = _where_condition +" WEPA.business_unit = '"+businessUnit+"'";
					
					*/
					if (UtilValidate.isNotEmpty(isChecklistActivity) && isChecklistActivity.equalsIgnoreCase("N")) {
						String channel = Channels.PROGRAM;
						_where_condition = _where_condition + " AND (WE.channel_id !=? OR WE.channel_id is NULL) ";
						values.add(channel);
					}
				} else if("my-backup-activities".equals(filterType)) {
					if(UtilValidate.isNotEmpty(realCoordinator)) {
						if(UtilValidate.isNotEmpty(_where_condition)) {
							_where_condition = _where_condition +" AND WEPA.owner_id = ? ";
							values.add(realCoordinator);
						}else {
							_where_condition = _where_condition +" WEPA.owner_id = ? ";	
							values.add(realCoordinator);
						}
					}
				}
				
				if("overdue".equals(filterBy)) {
					/*
					EntityCondition overdue = EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_EQUAL, "IA_MCOMPLETED"),
									//EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.BETWEEN, UtilMisc.toList(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()), UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp())))
									EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.LESS_THAN, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()))
									),
							EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "IA_MSCHEDULED"),
											EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN, UtilDateTime.nowTimestamp())										 
									),
									EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "IA_INPROGRESS"),
											EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.LESS_THAN, UtilDateTime.nowTimestamp())											 
									)
							)
					);
					
					conditions.add(overdue);
					*/
					String overdueCondition = " ("
							+ "(IFNULL(WEA1.ATTR_VALUE,'N') = 'N' AND WE.CURRENT_STATUS_ID <> 'IA_MCOMPLETED' AND DATE(NOW()) > DATE(ESTIMATED_COMPLETION_DATE))"
							+ " OR "
								+ "((IFNULL(WEA1.ATTR_VALUE,'N') = 'Y' AND WE.CURRENT_STATUS_ID = 'IA_MSCHEDULED' AND NOW() > DATE_ADD(we.ESTIMATED_START_DATE,INTERVAL IFNULL(wea.ATTR_VALUE,0) HOUR))"
								+ " OR "
								+ "(WE.CURRENT_STATUS_ID = 'IA_INPROGRESS' AND NOW() > WE.ESTIMATED_COMPLETION_DATE))"
							+ ")";
					if(UtilValidate.isNotEmpty(_where_condition))
						_where_condition = _where_condition +" AND "+ overdueCondition;
					else
						_where_condition = _where_condition + overdueCondition;
					
					
				} else if("due-today".equals(filterBy)) {
					/*
					@SuppressWarnings("unchecked")
					EntityCondition dueToday = EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_EQUAL, "IA_MCOMPLETED"),
									EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.BETWEEN, UtilMisc.toList(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()), UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp())))
								),
							 EntityCondition.makeCondition(EntityOperator.OR,
									 EntityCondition.makeCondition(EntityOperator.AND,
										 EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "IA_MSCHEDULED"),
										 EntityCondition.makeCondition("estimatedStartDate", EntityOperator.BETWEEN, UtilMisc.toList(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()), UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp())))											 
										 ),
									 EntityCondition.makeCondition(EntityOperator.AND,
											 EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "IA_INPROGRESS"),
											 EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.BETWEEN, UtilMisc.toList(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()), UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp())))											 
										)
								 )
							 );
					
					
					conditions.add(dueToday);
					*/
					String dueTodayCondition = " ("
							+ "(IFNULL(WEA1.ATTR_VALUE,'N') = 'N' AND WE.CURRENT_STATUS_ID <> 'IA_MCOMPLETED' AND DATE(ESTIMATED_COMPLETION_DATE) = DATE(NOW()))"
							+ " OR "
								+ "((IFNULL(WEA1.ATTR_VALUE,'N') = 'Y' AND WE.CURRENT_STATUS_ID = 'IA_MSCHEDULED' AND DATE(we.ESTIMATED_START_DATE) = DATE(NOW()))"
								+ " OR "
								+ "(WE.CURRENT_STATUS_ID = 'IA_INPROGRESS' AND DATE(WE.ESTIMATED_COMPLETION_DATE) = DATE(NOW())))"
							+ ")";
					
					if(UtilValidate.isNotEmpty(_where_condition))
						_where_condition = _where_condition +" AND "+dueTodayCondition;
					else
						_where_condition = _where_condition + dueTodayCondition;
					
				} else if("due-tomorrow".equals(filterBy)) {
					Map<String, Object> validateMap = new HashMap<String, Object>();
					LocalDateTime startDateTime = UtilDateTime.nowTimestamp().toLocalDateTime();
					LocalDateTime tomorrowDateTime = startDateTime.plusDays(1);
					String workStartHour = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_START_TIME");
					String workEndHour = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_END_TIME");
					LocalTime startTime = LocalTime.parse(UtilValidate.isNotEmpty(workStartHour)? workStartHour : "09:00");
					LocalTime endTime = LocalTime.parse(UtilValidate.isNotEmpty(workEndHour) ? workEndHour : "18:00");
					
		            validateMap.put("delegator", delegator);
		            validateMap.put("startDateTime", tomorrowDateTime);
		            validateMap.put("businessStartTime", startTime);
		            validateMap.put("businessEndTime", endTime);
		            validateMap.put("businessUnit", businessUnit);
		            
		            List<LocalDate> holidayList = org.fio.admin.portal.util.DataHelper.getHolidays(validateMap);
		            validateMap.put("holidayList", holidayList);
		            Map<String, Object>  businessDate = org.fio.admin.portal.util.DataHelper.getBusinessDate(validateMap);
		            if(businessDate != null) {
		            	tomorrowDateTime =  (LocalDateTime) businessDate.get("startDateTime");
		            }
		            /*
					@SuppressWarnings("unchecked")
					EntityCondition dueTomorrow = EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_EQUAL, "IA_MCOMPLETED"),
									EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.BETWEEN, UtilMisc.toList(UtilDateTime.getDayStart(Timestamp.valueOf(tomorrowDateTime)), UtilDateTime.getDayEnd(Timestamp.valueOf(tomorrowDateTime))))
								),
							 EntityCondition.makeCondition(EntityOperator.OR,
									 EntityCondition.makeCondition(EntityOperator.AND,
										 EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "IA_MSCHEDULED"),
										 EntityCondition.makeCondition("estimatedStartDate", EntityOperator.BETWEEN, UtilMisc.toList(UtilDateTime.getDayStart(Timestamp.valueOf(tomorrowDateTime)), UtilDateTime.getDayEnd(Timestamp.valueOf(tomorrowDateTime))))											 
										 ),
									 EntityCondition.makeCondition(EntityOperator.AND,
											 EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "IA_INPROGRESS"),
											 EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.BETWEEN, UtilMisc.toList(UtilDateTime.getDayStart(Timestamp.valueOf(tomorrowDateTime)), UtilDateTime.getDayEnd(Timestamp.valueOf(tomorrowDateTime))))											 
										)
								 )
							 );
					
					
					conditions.add(dueTomorrow);
					*/
		            String dueTomorrowCondition = " ("
							+ "(IFNULL(WEA1.ATTR_VALUE,'N') = 'N' AND WE.CURRENT_STATUS_ID <> 'IA_MCOMPLETED' AND DATE(ESTIMATED_COMPLETION_DATE) = DATE(?) )"
							+ " OR "
							+ "((IFNULL(WEA1.ATTR_VALUE,'N') = 'Y' AND WE.CURRENT_STATUS_ID = 'IA_MSCHEDULED' AND DATE(we.ESTIMATED_START_DATE) = DATE(?) )"
							+ " OR "
							+ "(WE.CURRENT_STATUS_ID = 'IA_INPROGRESS' AND DATE(WE.ESTIMATED_COMPLETION_DATE) = DATE(?)))"
							+ ")";
		            	values.add(tomorrowDateTime.toString());
		            	values.add(tomorrowDateTime.toString());
		            	values.add(tomorrowDateTime.toString());
					if(UtilValidate.isNotEmpty(_where_condition))
						_where_condition = _where_condition +" AND " + dueTomorrowCondition;
					else
						_where_condition = _where_condition + dueTomorrowCondition;
				}
				
				if (UtilValidate.isNotEmpty(domainEntityType)) {
					_where_condition = _where_condition + " AND WE.domain_entity_type=? ";
		            values.add(domainEntityType);
				}
				if (UtilValidate.isNotEmpty(domainEntityId)) {
					_where_condition = _where_condition + " AND WE.domain_entity_id=? ";
		            values.add(domainEntityId);
				}
				
				List<String> locationList = new ArrayList<>();
				if(UtilValidate.isNotEmpty(context.get("location")) && context.get("location") instanceof String) {
					String locationId = (String) context.get("location");
					if(UtilValidate.isNotEmpty(locationId)) locationList.add(locationId);
				} else if(UtilValidate.isNotEmpty(context.get("location")) && context.get("location") instanceof List<?>) {
					locationList = (List<String>) context.get("location");
				}
				if (UtilValidate.isNotEmpty(locationList)) {
					_where_condition = _where_condition + " AND ATTR.ATTR_VALUE IN ("+org.fio.admin.portal.util.DataUtil.toList(locationList,"")+")";
				
				}
				
				// Get Approval Statistics [start]
				String isEnabledApproval = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_APVL_ENABLE");
				if (UtilValidate.isEmpty(isEnabledApproval) || isEnabledApproval.equals("Y")) {
					if("pending-approvals".equals(filterBy) || "pending-reviews".equals(filterBy)) {
						List<String> pendingApprovals = new ArrayList<>();
						List<String> pendingReviews = new ArrayList<>();
						
						Map<String, Object> callCtxt = FastMap.newInstance();
						Map<String, Object> callResult = FastMap.newInstance();
						
						Map<String, Object> requestContext = FastMap.newInstance();
						if("pending-approvals".equals(filterBy)) {
							requestContext.put("decisionStatusId", "DECISION_NO");
						} else if("pending-reviews".equals(filterBy)) {
							requestContext.put("decisionStatusId", "DECISION_REVIEW");
						}
						
						if("my-activities".equals(filterType)) {
							requestContext.put("partyId", userLoginPartyId);
						} else if("my-teams-activities".equals(filterType)) {
							List<GenericValue> emplPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("partyId",userLoginPartyId).filterByDate().queryList();
							List<String> teams = UtilValidate.isNotEmpty(emplPositionFulfillments) ? EntityUtil.getFieldListFromEntityList(emplPositionFulfillments, "emplTeamId", true) : new ArrayList<>();
							requestContext.put("emplTeamIds", teams);
						} else if("my-backup-activities".equals(filterType)) {
							String realCoordinatorPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, realCoordinator);
							requestContext.put("partyId", realCoordinatorPartyId);
							
						} 
						
						callCtxt.put("requestContext", requestContext);
						callCtxt.put("userLogin", userLogin);
						
						callResult = dispatcher.runSync("approval.getApprovalStatistics", callCtxt);
						if (ServiceUtil.isSuccess(callResult)) {
							Map<String, Object> responseContext = (Map<String, Object>) callResult.get("responseContext");
							pendingApprovals = (List) responseContext.get("pendingApprovals");
							pendingReviews = (List) responseContext.get("pendingReviews");
						}
						
						if("pending-approvals".equals(filterBy)) {
							if (UtilValidate.isNotEmpty(pendingApprovals)) {
								_where_condition = _where_condition + " AND WE.work_effort_id IN ("+org.fio.admin.portal.util.DataUtil.toList(pendingApprovals, "")+")";
							} else {
								_where_condition = _where_condition + " AND WE.work_effort_id IS NULL ";
							}
						} else if("pending-reviews".equals(filterBy)) {
							if (UtilValidate.isNotEmpty(pendingReviews)) {
								_where_condition = _where_condition + " AND WE.work_effort_id IN ("+org.fio.admin.portal.util.DataUtil.toList(pendingReviews, "")+")";
							} else {
								_where_condition = _where_condition + " AND WE.work_effort_id IS NULL ";
							}
						}
					}
				}
				// Get Approval Statistics [end]
				
				//check and adding skip sms activity
				String hideSmsActivity = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_HIDE_SMS", "N");
	        	boolean hideSMSActivity = false;
	        	if(UtilValidate.isNotEmpty(hideSmsActivity) && "Y".equals(hideSmsActivity)) {
	        		hideSMSActivity = true;
	        	}
	        	if(hideSMSActivity) {
	        		String smsSkipCondition = " (WE.work_effort_purpose_type_id NOT IN ('SMS') OR WE.work_effort_purpose_type_id IS NULL)";
	        		if(UtilValidate.isNotEmpty(_where_condition))
						_where_condition = _where_condition +" AND " + smsSkipCondition;
					else
						_where_condition = smsSkipCondition;
	        	}
				
				//EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				
				//get the default general grid fetch limit
				GenericValue systemProperty = EntityQuery.use(delegator)
												.select("systemPropertyValue")
												.from("SystemProperty")
												.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
												.queryFirst();
				// set the page parameters
		        int viewIndex = 0;
		        try {
		            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
		        } catch (Exception e) {
		            viewIndex = 0;
		        }
		        result.put("viewIndex", Integer.valueOf(viewIndex));

		        int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;
		        
		        viewSize = fioGridFetch;
		        try {
		            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
		        } catch (Exception e) {
		            viewSize = fioGridFetch;
		        }
		        result.put("viewSize", Integer.valueOf(viewSize));
		        
	            try {
	            	String customFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);
	            	SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
	                ResultSet rs = null;
	                Debug.logInfo("getActivityDashboardData _where_condition: "+_where_condition, MODULE);
	            	String _sql_query_count = "SELECT COUNT(DISTINCT WE.WORK_EFFORT_ID) as count"
							+ " FROM WORK_EFFORT WE"
							+ " LEFT OUTER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID "
							+ " LEFT OUTER JOIN WORK_EFFORT_ATTRIBUTE WEA ON WE.WORK_EFFORT_ID = WEA.WORK_EFFORT_ID AND WEA.ATTR_NAME='TECH_ARRIVAL_WINDOW'"
							+ " LEFT OUTER JOIN WORK_EFFORT_ATTRIBUTE WEA1 ON WE.WORK_EFFORT_ID=WEA1.WORK_EFFORT_ID AND WEA1.ATTR_NAME='IS_SCHEDULING_REQUIRED'"
							+ " LEFT OUTER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON WE.WORK_EFFORT_ID=CRWE.WORK_EFFORT_ID "
							+ " LEFT OUTER JOIN CUST_REQUEST_ATTRIBUTE ATTR ON CRWE.CUST_REQUEST_ID=ATTR.CUST_REQUEST_ID AND ATTR.ATTR_NAME=? "
							+ " WHERE " + _where_condition;
	            	
		            values.add(customFieldId);
		            rs = QueryUtil.getResultSet(_sql_query_count, values, delegator);

	            	if (rs != null) {
	            		while (rs.next()) {
	            			resultListSize = rs.getInt("count");
	            		}
	            	}
	                // get the indexes for the partial list
	            	lowIndex = viewIndex * viewSize + 1;
	                highIndex = (viewIndex + 1) * viewSize;
	                int limit = viewIndex * viewSize;
	                String _sql_query = "SELECT DISTINCT WE.WORK_EFFORT_ID, WE.EXTERNAL_ID, WE.WORK_EFFORT_TYPE_ID, WE.WORK_EFFORT_PURPOSE_TYPE_ID, "
							+ "WE.WORK_EFFORT_SERVICE_TYPE, WE.WORK_EFFORT_SUB_SERVICE_TYPE, WE.SCOPE_ENUM_ID, WE.CURRENT_STATUS_ID, WE.CURRENT_SUB_STATUS_ID, "
							+ "WE.WORK_EFFORT_NAME, WE.DESCRIPTION, WE.PHONE_NUMBER, WE.WF_NATIONAL_ID, WE.WF_VPLUS_ID, WE.ESTIMATED_START_DATE, "
							+ "WE.ESTIMATED_COMPLETION_DATE, WE.ACTUAL_START_DATE, WE.ACTUAL_COMPLETION_DATE, WE.DURATION, WE.LAST_MODIFIED_BY_USER_LOGIN, "
							+ "WE.CHANNEL_ID, WE.WF_ONCE_DONE, WE.LAST_UPDATED_TX_STAMP, WE.CREATED_BY_USER_LOGIN, WE.PRIM_OWNER_ID, WE.CREATED_DATE, "
							+ "WE.LAST_MODIFIED_DATE, WE.SOURCE_REFERENCE_ID, WE.CIF, WE.BUSINESS_UNIT_NAME, WE.PRIORITY, WE.DIRECTION, WE.OWNER_PARTY_ID, "
							+ "WE.LOCATION_DESC, WE.DOMAIN_ENTITY_ID, WE.DOMAIN_ENTITY_TYPE, WE.COMPLETED_BY, WE.CLOSED_DATE_TIME, WE.CLOSED_BY_USER_LOGIN, "
							+ "WEPA.PARTY_ID, WEPA.ROLE_TYPE_ID, WEPA.FROM_DATE, WEPA.THRU_DATE, WEPA.STATUS_ID, WEPA.STATUS_DATE_TIME, WEPA.CALL_OUT_COME, "
							+ "WEPA.ASSIGNED_BY_USER_LOGIN_ID, WEPA.FACILITY_ID, WEPA.OWNER_ID, WEPA.EMPL_TEAM_ID, WEPA.BUSINESS_UNIT, "
							+ " CASE WHEN wea1.ATTR_VALUE = 'N' THEN 'No' WHEN wea1.ATTR_VALUE = 'Y' THEN 'Yes' ELSE 'NA' END AS 'IS_SCHEDULED',"
							+ " ATTR.ATTR_VALUE AS 'LOCATION', CR.CUST_REQUEST_ID, CR.CUST_REQUEST_NAME"
							+ " FROM WORK_EFFORT WE"
							+ " LEFT OUTER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID "
							+ " LEFT OUTER JOIN WORK_EFFORT_ATTRIBUTE WEA ON WE.WORK_EFFORT_ID = WEA.WORK_EFFORT_ID AND WEA.ATTR_NAME='TECH_ARRIVAL_WINDOW'"
							+ " LEFT OUTER JOIN WORK_EFFORT_ATTRIBUTE WEA1 ON WE.WORK_EFFORT_ID=WEA1.WORK_EFFORT_ID AND WEA1.ATTR_NAME='IS_SCHEDULING_REQUIRED'"
							+ " LEFT OUTER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON WE.WORK_EFFORT_ID=CRWE.WORK_EFFORT_ID "
							+ " LEFT OUTER JOIN CUST_REQUEST CR ON CRWE.CUST_REQUEST_ID=CR.CUST_REQUEST_ID "
							+ " LEFT OUTER JOIN CUST_REQUEST_ATTRIBUTE ATTR ON CRWE.CUST_REQUEST_ID=ATTR.CUST_REQUEST_ID AND ATTR.ATTR_NAME=? "
							+ " WHERE " + _where_condition +" GROUP BY WE.WORK_EFFORT_ID ORDER BY WE.LAST_UPDATED_TX_STAMP DESC"; // LIMIT "+limit+", "+ viewSize;	
	                
	                /*
	                // set distinct on so we only get one row per order
	                // using list iterator
	                EntityListIterator pli = EntityQuery.use(delegator)
	                        .from(dynamicView)
	                        .where(condition)
	                        .orderBy("lastUpdatedTxStamp ASC")
	                        .cursorScrollInsensitive()
	                        .fetchSize(highIndex)
	                        .distinct()
	                        .cache(true)
	                        .queryIterator();
	                // get the partial list for this page
	                resultList = pli.getPartialList(lowIndex, viewSize);

	                // attempt to get the full size
	                resultListSize = pli.getResultsSizeAfterPartialList();
	                // close the list iterator
	                pli.close();
	                */
		            rs = QueryUtil.getResultSet(_sql_query, values, delegator);
	                ResultSetMetaData rsMetaData = rs.getMetaData();
	                List<String> columnList = new ArrayList<String>();
	                //Retrieving the list of column names
	                int count = rsMetaData.getColumnCount();
	                for(int i = 1; i<=count; i++) {
	                	columnList.add(rsMetaData.getColumnName(i));
	                }
	                
					if (rs != null) {
						while (rs.next()) {
							Map<String, Object> data = new HashMap<String, Object>();
							for(String columName : columnList) {
								String fieldName = ModelUtil.dbNameToVarName(columName);
								data.put(fieldName, rs.getString(columName));
							}
							if(UtilValidate.isNotEmpty(data))
								resultList.add(data);
						}
					}
	            } catch (Exception e) {
	                String errMsg = "Error: " + e.toString();
	                Debug.logError(e, errMsg, MODULE);
	            }
	            
				if(UtilValidate.isNotEmpty(resultList)) {
					
					String isApprovalEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPROVAL_ENABLED");
					
					List<String> toScheldTypes = new ArrayList<>();
					String scheduleTaskType = DataUtil.getGlobalValue(delegator, "SCHEDULE_TASK_TYPE", "SCHEDULE_TASK");
					if(UtilValidate.isNotEmpty(scheduleTaskType) && scheduleTaskType.contains(",")) {
						toScheldTypes = org.fio.admin.portal.util.DataUtil.stringToList(scheduleTaskType, ",");
					} else if(UtilValidate.isNotEmpty(scheduleTaskType)) {
						toScheldTypes.add(scheduleTaskType);
					}
					
					String partyName = "";
					List<GenericValue> workTypeList = EntityQuery.use(delegator).select("workEffortPurposeTypeId","description").from("WorkEffortPurposeType").where("parentTypeId","ACTIVITY_WORK_TYPE").cache(true).queryList();
		            Map<String, Object> workTypeMap = DataUtil.getMapFromGeneric(workTypeList, "workEffortPurposeTypeId", "description", false);
		            
					String activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");

					for (Map<String, Object> interactiveActivity : resultList) {

						Map<String, Object> data = new HashMap<String, Object>();

						String workEffortId = (String) interactiveActivity.get("workEffortId");
						String typeDesc = EnumUtil.getEnumDescription(delegator, (String) interactiveActivity.get("workEffortServiceType"), "IA_TYPE");
						String subTypeDesc = EnumUtil.getEnumDescription(delegator, (String) interactiveActivity.get("workEffortSubServiceType"), "IA_TYPE");
						String workEffortTypeId = (String) interactiveActivity.get("workEffortTypeId");
						String isSchedulingRequired = org.fio.homeapps.util.UtilActivity.getActivityAttrValue(delegator, workEffortId, "IS_SCHEDULING_REQUIRED");
						String workEffortPurposeTypeId = (String) interactiveActivity.get("workEffortPurposeTypeId"); 
						String currentStatusId = (String) interactiveActivity.get("currentStatusId");
						
						/*
						String actPartyId = (String) interactiveActivity.get("partyId");
						data.put("partyId", actPartyId);
						if (UtilValidate.isNotEmpty(actPartyId)) {
							partyName = org.fio.homeapps.util.DataUtil.getUserLoginName(delegator, actPartyId);
						}
						data.put("partyName", partyName);
						*/
						data.put("workType", UtilValidate.isNotEmpty(workTypeMap) ? workTypeMap.get((String) interactiveActivity.get("workEffortPurposeTypeId")) : "");
						data.put("workEffortTypeId", workEffortTypeId);

						data.put("iaNumber", (String) interactiveActivity.get("workEffortId"));
						//data.put("partyId", actPartyId);
						data.put("priority", EnumUtil.getEnumDescription(delegator, (String) interactiveActivity.get("priority"), "PRIORITY_LEVEL"));
						data.put("direction", EnumUtil.getEnumDescription(delegator, (String) interactiveActivity.get("direction"), "PH_DIRECTIONCODE"));

						data.put("activityType", UtilValidate.isNotEmpty(typeDesc) ? typeDesc : DataUtil.getWorkEffortTypeDescription(delegator, workEffortTypeId));
						data.put("activitySubType", subTypeDesc);
						data.put("businessUnit", UtilValidate.isNotEmpty((String) interactiveActivity.get("businessUnitName")) ? (String) interactiveActivity.get("businessUnitName") : "");

						data.put("ownerName", UtilActivity.getActivityOwnerName(delegator, activityOwnerRole, workEffortId, true));
						data.put("contactName", org.fio.homeapps.util.UtilActivity.getActivityContactName(delegator, workEffortId));

						data.put("phone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber((String) interactiveActivity.get("phoneNumber")));
						String status = Objects.toString(org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, (String) interactiveActivity.get("currentStatusId")), "IA_STATUS_ID");
						data.put("currentStatusId", interactiveActivity.get("currentStatusId"));
						data.put("status", status);
						data.put("subject", (String) interactiveActivity.get("workEffortName"));
						data.put("description", interactiveActivity.get("description"));
						data.put("accountNo", "");
						data.put("businessUnit", "");
						
						String dateDue = "";
						if (UtilValidate.isNotEmpty((String) interactiveActivity.get("estimatedCompletionDate"))) {
							dateDue = DataUtil.convertDateTimestamp((String) interactiveActivity.get("estimatedCompletionDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("plannedDueDate", dateDue);
						String plannedStartDate = "";
						if (UtilValidate.isNotEmpty((String) interactiveActivity.get("estimatedStartDate"))) {
							plannedStartDate = DataUtil.convertDateTimestamp((String) interactiveActivity.get("estimatedStartDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("plannedStartDate", plannedStartDate);
						String actualCompletion = "";
						if (UtilValidate.isNotEmpty((String) interactiveActivity.get("actualCompletionDate"))) {
							actualCompletion = DataUtil.convertDateTimestamp((String) interactiveActivity.get("actualCompletionDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("actualCompletion", actualCompletion);
						if(UtilValidate.isNotEmpty((String) interactiveActivity.get("wfOnceDone")) && "Y".equals((String) interactiveActivity.get("wfOnceDone"))){
							data.put("onceDone", (String) interactiveActivity.get("wfOnceDone"));
						}else{
							data.put("onceDone", "");
						}
						data.put("productName", "");

						data.put("comments", (String) interactiveActivity.get("description"));
						String actualStart = "";
						if (UtilValidate.isNotEmpty((String) interactiveActivity.get("actualStartDate"))) {
							actualStart = DataUtil.convertDateTimestamp((String) interactiveActivity.get("actualStartDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("actualStart", actualStart);
						
						if (UtilValidate.isNotEmpty((String) interactiveActivity.get("duration"))) {
							data.put("duration", (String) interactiveActivity.get("duration") + "Min");
						}
						if(UtilValidate.isNotEmpty(interactiveActivity.get("duration"))){
							String durationUntiType = "hr";
							if (Float.parseFloat(interactiveActivity.get("duration").toString()) > 1) {
								durationUntiType = "hrs";
							}
							data.put("wftMsdduration", interactiveActivity.get("duration")+" "+durationUntiType);
						}
						
						String overDue = "";
						if (UtilValidate.isNotEmpty(interactiveActivity.get("estimatedCompletionDate"))
								&& systemTime.after(Timestamp.valueOf((String) interactiveActivity.get("estimatedCompletionDate")))) {
							overDue = "Y";
						}
						String plannedEndDate = "";
						if(UtilValidate.isNotEmpty(interactiveActivity.get("estimatedCompletionDate"))){
							plannedEndDate = DataUtil.convertDateTimestamp((String) interactiveActivity.get("estimatedCompletionDate"), new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("plannedEndDate", plannedEndDate);
						data.put("overDue", overDue);
						
						String closedDate = "";
						if (UtilValidate.isNotEmpty((String) interactiveActivity.get("closedDateTime"))) {
							closedDate = UtilDateTime.timeStampToString(Timestamp.valueOf((String) interactiveActivity.get("closedDateTime")), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("closedDate", closedDate);
						data.put("closedByName", org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, (String) interactiveActivity.get("closedByUserLogin"), false));
						
						String createdDate = "";
						if (UtilValidate.isNotEmpty((String) interactiveActivity.get("createdDate"))) {
							createdDate = UtilDateTime.timeStampToString(Timestamp.valueOf((String) interactiveActivity.get("createdDate")), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("createdOn", createdDate);
						data.put("createdByName", org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, (String) interactiveActivity.get("createdByUserLogin"), false));
						
						String modifiedDate = "";
						if (UtilValidate.isNotEmpty((String) interactiveActivity.get("lastModifiedDate"))) {
							modifiedDate = UtilDateTime.timeStampToString(Timestamp.valueOf((String) interactiveActivity.get("lastModifiedDate")), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						data.put("modifiedOn", modifiedDate);
						data.put("modifiedByName", org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, (String) interactiveActivity.get("lastModifiedByUserLogin"), false));
						
						data.put("completedByName", org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, (String) interactiveActivity.get("completedBy"), false));

						// approval data [start]
						if(UtilValidate.isEmpty(isApprovalEnabled) || isApprovalEnabled.equals("Y")){
							if (UtilValidate.isNotEmpty(workEffortTypeId) && workEffortTypeId.equals("TASK")) {
								GenericValue approval = EntityQuery.use(delegator).from("WorkEffortApproval").where("workEffortId", workEffortId).queryFirst();
								if (UtilValidate.isNotEmpty(approval)) {
									data.put("approvalCategoryId", approval.getString("approvalCategoryId"));
								}
							}
						}
						// approval data [end]
						
						data.put("workEffortPurposeTypeId", workEffortPurposeTypeId);
						data.put("isSchedulingRequired", isSchedulingRequired);
						if("IA_OPEN".equals(currentStatusId) && toScheldTypes.contains(workEffortPurposeTypeId)) {
							data.put("isScheduleTask","Y");
						} else {
							data.put("isScheduleTask","N");
						}
						
						data.put("sourceIdLink", org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom((String)interactiveActivity.get("domainEntityId"), (String)interactiveActivity.get("domainEntityType"), externalLoginKey));
						data.put("sourceComponent", org.groupfio.common.portal.util.DataHelper.convertToLabel((String)interactiveActivity.get("domainEntityType")));
						
						GenericValue workEffortAttr = EntityQuery.use(delegator).from("WorkEffortAttribute").where("workEffortId",workEffortId,"attrName","TECH_ARRIVAL_WINDOW").queryFirst();
						data.put("arrivalWindow", UtilValidate.isNotEmpty(workEffortAttr) && UtilValidate.isNotEmpty(workEffortAttr.get("attrValue")) ? workEffortAttr.get("attrValue")+"hr" : "" );
						data.put("isScheduled", (String) interactiveActivity.get("isScheduled"));
						data.put("domainEntityId", (String) interactiveActivity.get("domainEntityId"));
						data.put("domainEntityType", (String) interactiveActivity.get("domainEntityType"));
						data.put("domainEntityTypeDesc", DataHelper.convertToLabel( (String) interactiveActivity.get("domainEntityType") ));
						data.put("externalLoginKey", externalLoginKey);
						
						data.put("srId", interactiveActivity.get("custRequestId"));
						data.put("srName", interactiveActivity.get("custRequestName"));
						
						String locationId = (String) interactiveActivity.get("location");
						if (UtilValidate.isNotEmpty(locationId)) {
							data.put("location", storeNames.get(locationId));
						}
						
						dataList.add(data);
					}
					
					result.put("highIndex", Integer.valueOf(highIndex));
			        result.put("lowIndex", Integer.valueOf(lowIndex));
					
					
				}
			}
			result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			result.put("totalRecords", nf.format(resultListSize));
			result.put("recordCount", resultListSize);
			result.put("chunkSize", viewSize);
			
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}
	
	@SuppressWarnings("unchecked")
	public static String getActivityDashboardCountList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<GenericValue> resultList = null;
		List<Object> values = new ArrayList<>();
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		String filterBy = (String) context.get("filterBy");
		String filterType = (String) context.get("filterType");
		String defaultActType = (String) context.get("defaultActType");
		
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		
		String isChecklistActivity = request.getParameter("isChecklistActivity");
		
		String realCoordinator = (String) context.get("realCoordinator");
		
		List<Map<String, Object>> dataList = new ArrayList<>();
		long start = System.currentTimeMillis();
		Timestamp systemTime = UtilDateTime.nowTimestamp();
		try {

			String userLoginId = userLogin.getString("userLoginId");
			String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
			Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, userLoginPartyId);
			String businessUnit = (String) buTeamData.get("businessUnit");

			List<String> statusIds = new ArrayList<>();
			String hideDashPageFilter = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "HIDE_ACT_DASH_FILTER");
			boolean allowFilter = false;
			if (UtilValidate.isNotEmpty(hideDashPageFilter) && hideDashPageFilter.equalsIgnoreCase("Y")) {
				allowFilter = true;
			}
			if((UtilValidate.isNotEmpty(filterType) || UtilValidate.isNotEmpty(filterBy) || allowFilter)  && UtilValidate.isNotEmpty(userLoginId)) {
				String customFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);
				SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
				Connection con = (Connection)sqlProcessor.getConnection();
				CallableStatement cstmt = null;
				ResultSet rs = null;

				String _where_condition = "";
				
				List<String> activityTypes = new ArrayList<>();
				if(UtilValidate.isNotEmpty(defaultActType) && defaultActType.contains(",")) {
					activityTypes = org.fio.admin.portal.util.DataUtil.stringToList(defaultActType, ",");
				} else if(UtilValidate.isNotEmpty(defaultActType)) {
					activityTypes.add(defaultActType);
				}
				if (UtilValidate.isNotEmpty(activityTypes)) {
					_where_condition = _where_condition + "WE.work_effort_type_id IN ("+org.fio.admin.portal.util.DataUtil.toList(activityTypes, "")+")";
				}

				if("my-activities".equals(filterType)) {
					if(UtilValidate.isNotEmpty(_where_condition)) {
						_where_condition = _where_condition +" AND WEPA.owner_id = ? ";
						values.add(userLoginId);
					}else {
						_where_condition = _where_condition +" WEPA.owner_id = ? ";
						values.add(userLoginId);
					}
				} else if("my-teams-activities".equals(filterType)) {
					List<GenericValue> emplPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("partyId",userLoginPartyId).filterByDate().queryList();
					List<String> teams = UtilValidate.isNotEmpty(emplPositionFulfillments) ? EntityUtil.getFieldListFromEntityList(emplPositionFulfillments, "emplTeamId", true) : new ArrayList<>();

					if(UtilValidate.isNotEmpty(_where_condition))
						_where_condition = _where_condition +" AND WEPA.empl_team_id IN ( "+org.fio.admin.portal.util.DataUtil.toList(teams, "")+")";
					else
						_where_condition = _where_condition +" WEPA.empl_team_id IN ("+org.fio.admin.portal.util.DataUtil.toList(teams, "")+")";

				} else if("my-bu-activities".equals(filterType)) {
					/*
					if(UtilValidate.isNotEmpty(_where_condition))
						_where_condition = _where_condition +" AND WEPA.business_unit = '"+businessUnit+"'";
					else
						_where_condition = _where_condition +" WEPA.business_unit = '"+businessUnit+"'";
					*/
					if (UtilValidate.isNotEmpty(isChecklistActivity) && isChecklistActivity.equalsIgnoreCase("N")) {
						String channel = Channels.PROGRAM;
						_where_condition = _where_condition + " AND (WE.channel_id !=? OR WE.channel_id is NULL) ";
						values.add(channel);
					} 
				} if("my-backup-activities".equals(filterType)) {
					if(UtilValidate.isNotEmpty(realCoordinator)) {
						if(UtilValidate.isNotEmpty(_where_condition)) {
							_where_condition = _where_condition +" AND WEPA.owner_id = ? ";
							values.add(realCoordinator);
						}else {
							_where_condition = _where_condition +" WEPA.owner_id = ? ";
							values.add(realCoordinator);
						}
					}
					
				}
				
				// TODO its temporary solution, need to find out better ways
				if (!org.fio.homeapps.util.DataUtil.isPartyRoleExists(delegator, userLogin.getString("partyId"), "REBATE")) {
					_where_condition = _where_condition + " AND WE.work_effort_name NOT LIKE '%Approval task%'";
				}
				
				if (UtilValidate.isNotEmpty(domainEntityType)) {
					_where_condition = _where_condition + " AND WE.domain_entity_type=? ";
					values.add(domainEntityId);
				}
				if (UtilValidate.isNotEmpty(domainEntityId)) {
					_where_condition = _where_condition + " AND WE.domain_entity_id=? ";
					values.add(domainEntityId);
				}
				List<String> locationList = new ArrayList<>();
				if(UtilValidate.isNotEmpty(context.get("location")) && context.get("location") instanceof String) {
					String locationId = (String) context.get("location");
					if(UtilValidate.isNotEmpty(locationId)) locationList.add(locationId);
				} else if(UtilValidate.isNotEmpty(context.get("location")) && context.get("location") instanceof List<?>) {
					locationList = (List<String>) context.get("location");
				}
				if (UtilValidate.isNotEmpty(locationList)) {
					_where_condition = _where_condition + " AND ATTR.ATTR_VALUE IN ("+org.fio.admin.portal.util.DataUtil.toList(locationList,"")+")";
				}
				
				//check and adding skip sms activity
				String hideSmsActivity = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_HIDE_SMS", "N");
	        	boolean hideSMSActivity = false;
	        	if(UtilValidate.isNotEmpty(hideSmsActivity) && "Y".equals(hideSmsActivity)) {
	        		hideSMSActivity = true;
	        	}
	        	if(hideSMSActivity) {
	        		String smsSkipCondition = " (WE.work_effort_purpose_type_id NOT IN ('SMS') OR WE.work_effort_purpose_type_id IS NULL)";
	        		if(UtilValidate.isNotEmpty(_where_condition))
						_where_condition = _where_condition +" AND " + smsSkipCondition;
					else
						_where_condition = smsSkipCondition;
	        	}
				
				// to get overdue count
				String overdueCondition = " ("
						+ "(IFNULL(WEA1.ATTR_VALUE,'N') = 'N' AND WE.CURRENT_STATUS_ID <> 'IA_MCOMPLETED' AND DATE(NOW()) > DATE(ESTIMATED_COMPLETION_DATE))"
						+ " OR "
						+ "((IFNULL(WEA1.ATTR_VALUE,'N') = 'Y' AND WE.CURRENT_STATUS_ID = 'IA_MSCHEDULED' AND NOW() > DATE_ADD(we.ESTIMATED_START_DATE,INTERVAL IFNULL(wea.ATTR_VALUE,0) HOUR))"
						+ " OR "
						+ "(WE.CURRENT_STATUS_ID = 'IA_INPROGRESS' AND NOW() > WE.ESTIMATED_COMPLETION_DATE))"
						+ ")";

				if(UtilValidate.isNotEmpty(_where_condition))
					overdueCondition = _where_condition +" AND " + overdueCondition;
				else
					overdueCondition = _where_condition + overdueCondition;

				String overdueSql = "SELECT COUNT(DISTINCT WE.WORK_EFFORT_ID) as overdueCount"
						+ " FROM WORK_EFFORT WE"
						+ " LEFT OUTER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID "
						+ " LEFT OUTER JOIN WORK_EFFORT_ATTRIBUTE WEA ON WE.WORK_EFFORT_ID = WEA.WORK_EFFORT_ID AND WEA.ATTR_NAME='TECH_ARRIVAL_WINDOW'"
						+ " LEFT OUTER JOIN WORK_EFFORT_ATTRIBUTE WEA1 ON WE.WORK_EFFORT_ID=WEA1.WORK_EFFORT_ID AND WEA1.ATTR_NAME='IS_SCHEDULING_REQUIRED'"
						+ " LEFT OUTER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON WE.WORK_EFFORT_ID=CRWE.WORK_EFFORT_ID "
						+ " LEFT OUTER JOIN CUST_REQUEST_ATTRIBUTE ATTR ON CRWE.CUST_REQUEST_ID=ATTR.CUST_REQUEST_ID AND ATTR.ATTR_NAME=? "
						+ " WHERE " + overdueCondition;
				values.add(customFieldId);
				rs = QueryUtil.getResultSet(overdueSql, values, delegator);
				
				if (rs != null) {
            		while (rs.next()) {
            			Map<String, Object> data = new HashMap<String, Object>();
						int count = rs.getInt("overdueCount");
						data.put("barId", "overdue");
						data.put("count", count);
						dataList.add(data);
            		}
            	}


				// due today count
				String dueTodayCondition = " ("
						+ "(IFNULL(WEA1.ATTR_VALUE,'N') = 'N' AND WE.CURRENT_STATUS_ID <> 'IA_MCOMPLETED' AND DATE(ESTIMATED_COMPLETION_DATE) = DATE(NOW()))"
						+ " OR "
						+ "((IFNULL(WEA1.ATTR_VALUE,'N') = 'Y' AND WE.CURRENT_STATUS_ID = 'IA_MSCHEDULED' AND DATE(we.ESTIMATED_START_DATE) = DATE(NOW()))"
						+ " OR "
						+ "(WE.CURRENT_STATUS_ID = 'IA_INPROGRESS' AND DATE(WE.ESTIMATED_COMPLETION_DATE) = DATE(NOW())))"
						+ ")";

				if(UtilValidate.isNotEmpty(_where_condition))
					dueTodayCondition = _where_condition +" AND "+dueTodayCondition;
				else
					dueTodayCondition = _where_condition + dueTodayCondition;

				String dueTodaySql = "SELECT COUNT(DISTINCT WE.WORK_EFFORT_ID) as dueTodayCount"
						+ " FROM WORK_EFFORT WE"
						+ " LEFT OUTER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID "
						+ " LEFT OUTER JOIN WORK_EFFORT_ATTRIBUTE WEA ON WE.WORK_EFFORT_ID = WEA.WORK_EFFORT_ID AND WEA.ATTR_NAME='TECH_ARRIVAL_WINDOW'"
						+ " LEFT OUTER JOIN WORK_EFFORT_ATTRIBUTE WEA1 ON WE.WORK_EFFORT_ID=WEA1.WORK_EFFORT_ID AND WEA1.ATTR_NAME='IS_SCHEDULING_REQUIRED'"
						+ " LEFT OUTER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON WE.WORK_EFFORT_ID=CRWE.WORK_EFFORT_ID "
						+ " LEFT OUTER JOIN CUST_REQUEST_ATTRIBUTE ATTR ON CRWE.CUST_REQUEST_ID=ATTR.CUST_REQUEST_ID AND ATTR.ATTR_NAME=? "
						+ " WHERE " + dueTodayCondition;
				//values.add(customFieldId);
				rs = QueryUtil.getResultSet(dueTodaySql, values, delegator);
				
				if (rs != null) {
            		while (rs.next()) {
            			Map<String, Object> data = new HashMap<String, Object>();
						int count = rs.getInt("dueTodayCount");
						data.put("barId", "due_today");
						data.put("count", count);
						dataList.add(data);
            		}
            	}

				// due tomorrow count 
				Map<String, Object> validateMap = new HashMap<String, Object>();
				LocalDateTime startDateTime = UtilDateTime.nowTimestamp().toLocalDateTime();
				LocalDateTime tomorrowDateTime = startDateTime.plusDays(1);
				String workStartHour = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_START_TIME");
				String workEndHour = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_END_TIME");
				LocalTime startTime = LocalTime.parse(UtilValidate.isNotEmpty(workStartHour)? workStartHour : "09:00");
				LocalTime endTime = LocalTime.parse(UtilValidate.isNotEmpty(workEndHour) ? workEndHour : "18:00");

				validateMap.put("delegator", delegator);
				validateMap.put("startDateTime", tomorrowDateTime);
				validateMap.put("businessStartTime", startTime);
				validateMap.put("businessEndTime", endTime);
				validateMap.put("businessUnit", businessUnit);

				List<LocalDate> holidayList = org.fio.admin.portal.util.DataHelper.getHolidays(validateMap);
				validateMap.put("holidayList", holidayList);
				Map<String, Object>  businessDate = org.fio.admin.portal.util.DataHelper.getBusinessDate(validateMap);
				if(businessDate != null) {
					tomorrowDateTime =  (LocalDateTime) businessDate.get("startDateTime");
				}
				String dueTomorrowCondition = " ("
						+ "(IFNULL(WEA1.ATTR_VALUE,'N') = 'N' AND WE.CURRENT_STATUS_ID <> 'IA_MCOMPLETED' AND DATE(ESTIMATED_COMPLETION_DATE) = DATE(?) )"
						+ " OR "
						+ "((IFNULL(WEA1.ATTR_VALUE,'N') = 'Y' AND WE.CURRENT_STATUS_ID = 'IA_MSCHEDULED' AND DATE(we.ESTIMATED_START_DATE) = DATE(?) )"
						+ " OR "
						+ "(WE.CURRENT_STATUS_ID = 'IA_INPROGRESS' AND DATE(WE.ESTIMATED_COMPLETION_DATE) = DATE(?)))"
						+ ")";
				values.add(tomorrowDateTime.toString());
				values.add(tomorrowDateTime.toString());
				values.add(tomorrowDateTime.toString());
				if(UtilValidate.isNotEmpty(_where_condition))
					dueTomorrowCondition = _where_condition +" AND " + dueTomorrowCondition;
				else
					dueTomorrowCondition = _where_condition + dueTomorrowCondition;

				String dueTomorrowSql = "SELECT COUNT(DISTINCT WE.WORK_EFFORT_ID) as dueTomorrowCount"
						+ " FROM WORK_EFFORT WE"
						+ " LEFT OUTER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WEPA ON WE.WORK_EFFORT_ID = WEPA.WORK_EFFORT_ID "
						+ " LEFT OUTER JOIN WORK_EFFORT_ATTRIBUTE WEA ON WE.WORK_EFFORT_ID = WEA.WORK_EFFORT_ID AND WEA.ATTR_NAME='TECH_ARRIVAL_WINDOW'"
						+ " LEFT OUTER JOIN WORK_EFFORT_ATTRIBUTE WEA1 ON WE.WORK_EFFORT_ID=WEA1.WORK_EFFORT_ID AND WEA1.ATTR_NAME='IS_SCHEDULING_REQUIRED'"
						+ " LEFT OUTER JOIN CUST_REQUEST_WORK_EFFORT CRWE ON WE.WORK_EFFORT_ID=CRWE.WORK_EFFORT_ID "
						+ " LEFT OUTER JOIN CUST_REQUEST_ATTRIBUTE ATTR ON CRWE.CUST_REQUEST_ID=ATTR.CUST_REQUEST_ID AND ATTR.ATTR_NAME=? "
						+ " WHERE " + dueTomorrowCondition;

				//values.add(customFieldId);
				rs = QueryUtil.getResultSet(dueTomorrowSql, values, delegator);
				
				if (rs != null) {
            		while (rs.next()) {
            			Map<String, Object> data = new HashMap<String, Object>();
						int count = rs.getInt("dueTomorrowCount");
						data.put("barId", "due_tomorrow");
						data.put("count", count);
						dataList.add(data);
            		}
				}
				
				// Get Approval Statistics [start]
				int pendingApprovalCount = 0;
				int pendingReviewCount = 0;
				
				Map<String, Object> callCtxt = FastMap.newInstance();
				Map<String, Object> callResult = FastMap.newInstance();
				
				Map<String, Object> requestContext = FastMap.newInstance();
				
				if("my-activities".equals(filterType)) {
					requestContext.put("partyId", userLoginPartyId);
				} else if("my-teams-activities".equals(filterType)) {
					List<GenericValue> emplPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("partyId",userLoginPartyId).filterByDate().queryList();
					List<String> teams = UtilValidate.isNotEmpty(emplPositionFulfillments) ? EntityUtil.getFieldListFromEntityList(emplPositionFulfillments, "emplTeamId", true) : new ArrayList<>();
					requestContext.put("emplTeamIds", teams);
				} else if("my-backup-activities".equals(filterType)) {
					String realCoordinatorPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, realCoordinator);
					requestContext.put("partyId", realCoordinatorPartyId);
					
				} 
				
				callCtxt.put("requestContext", requestContext);
				callCtxt.put("userLogin", userLogin);
				
				String isEnabledApproval = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_APVL_ENABLE");
				if (UtilValidate.isEmpty(isEnabledApproval) || isEnabledApproval.equals("Y")) {
					callResult = dispatcher.runSync("approval.getApprovalStatistics", callCtxt);
					if (ServiceUtil.isSuccess(callResult)) {
						Map<String, Object> responseContext = (Map<String, Object>) callResult.get("responseContext");
						pendingApprovalCount = (int) responseContext.get("pendingApprovalCount");
						pendingReviewCount = (int) responseContext.get("pendingReviewCount");
					}
				}
				
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("barId", "pending_approvals");
				data.put("count", pendingApprovalCount);
				dataList.add(data);
				
				data = new HashMap<String, Object>();
				data.put("barId", "pending_reviews");
				data.put("count", pendingReviewCount);
				dataList.add(data);
				// Get Approval Statistics [end]
			
		}

	} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}

}
