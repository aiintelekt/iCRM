/**
 * 
 */
package org.groupfio.activity.portal.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.constants.GlobalConstants.Channels;
import org.fio.homeapps.util.CommonUtils;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.activity.portal.ActivityPortalConstants.ActivitySearchType;
import org.groupfio.activity.portal.util.DataHelper;
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
import org.ofbiz.entity.util.EntityFindOptions;
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
public class ActivityServices {

	private static final String MODULE = ActivityServices.class.getName();

	public static Map createActivity(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String subject = (String) context.get("workEffortName");
		String workEffortTypeId = (String) context.get("workEffortTypeId");
		String priority = (String) context.get("priority");
		String owner = (String) context.get("owner");
		String statusId = (String) context.get("currentStatusId");
		String description = (String) context.get("description");
		String ownerBu = (String) context.get("businessUnitId");
		String timeZoneId = (String) context.get("entityTimeZoneId");
		String location = (String) context.get("location");

		String domainEntityType = (String) context.get("domainEntityType");
		String domainEntityId = (String) context.get("domainEntityId");

		String estimatedStartDate = (String) context.get("estimatedStartDate_date");
		String estimatedCompletionDate = (String) context.get("estimatedCompletionDate_date");
		String actualStartDate = (String) context.get("actualStartDate_date");
		String actualCompletionDate = (String) context.get("actualCompletionDate_date");

		String estimatedStartTime = (String) context.get("estimatedStartDate_time");
		String estimatedCompletionTime = (String) context.get("estimatedCompletionDate_time");
		String actualStartTime = (String) context.get("actualStartDate_time");
		String actualCompletionTime = (String) context.get("actualCompletionDate_time");

		String requiredAttendees = (String) context.get("requiredAttendees");
		String optionalAttendees = (String) context.get("optionalAttendees");

		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String nativeBusinessUnit = null;
			String nativeTeamId = null;

			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
				Map<String, Object> buTeamData = DataUtil.getUserBuTeam(delegator, userLoginPartyId);
				nativeBusinessUnit = (String) buTeamData.get("businessUnit");
				nativeTeamId = (String) buTeamData.get("emplTeamId");
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", nativeBusinessUnit);
				accessMatrixMap.put("modeOfOp", "Create");
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
				if(UtilValidate.isEmpty(owner)) {
					owner = nativeTeamId;
				} else {
					currentPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
					buTeamData = DataUtil.getUserBuTeam(delegator, currentPartyId);
					nativeBusinessUnit = (String) buTeamData.get("businessUnit");
					nativeTeamId = (String) buTeamData.get("emplTeamId");
				}
				//check both users are the same team or Bu or not (need clarification) 
				//check with ownerId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					if(!ownerIds.contains(nativeTeamId)) accessLevel = null;
				}

				//check with emplTeamId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					if(!emplTeamIds.contains(nativeTeamId)) accessLevel = null;
				}

			}
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				Timestamp estimatedStartDateTime = ParamUtil.getTimestamp(estimatedStartDate, estimatedStartTime, "yyyy-MM-dd hh:mm");
				Timestamp estimatedCompletionDateTime = ParamUtil.getTimestamp(estimatedCompletionDate, estimatedCompletionTime, "yyyy-MM-dd hh:mm");
				Timestamp actualStartDateTime = ParamUtil.getTimestamp(actualStartDate, actualStartTime, "yyyy-MM-dd hh:mm");
				Timestamp actualCompletionDateTime = ParamUtil.getTimestamp(actualCompletionDate, actualCompletionTime, "yyyy-MM-dd hh:mm");

				callCtxt.put("description", description);
				if (UtilValidate.isNotEmpty(description)) {
					description = Base64.getEncoder().encodeToString(description.getBytes("utf-8"));
					callCtxt.put("description", description);
				}
				/*
				String emplTeamId = null;
				GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", owner).queryList());
				if (UtilValidate.isNotEmpty(emplTeam)) { 
					if (UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId"))) {
						emplTeamId = emplTeam.getString("emplTeamId");
					}
				}
				 */

				callCtxt.put("workEffortTypeId", workEffortTypeId);	
				callCtxt.put("workEffortServiceType", workEffortTypeId);	

				callCtxt.put("emplTeamId", nativeTeamId);
				callCtxt.put("currentStatusId", statusId);
				callCtxt.put("workEffortName", subject);
				callCtxt.put("estimatedStartDate", estimatedStartDateTime);
				callCtxt.put("estimatedCompletionDate", estimatedCompletionDateTime);
				callCtxt.put("actualStartDate", actualStartDateTime);
				callCtxt.put("actualCompletionDate", actualCompletionDateTime);
				callCtxt.put("wftLocation", location);

				callCtxt.put("primOwnerId", owner);
				callCtxt.put("ownerPartyId", owner);

				if (UtilValidate.isNotEmpty(priority)) {
					callCtxt.put("priority", Long.valueOf(priority));
				}

				if (UtilValidate.isEmpty(ownerBu)) {
					GenericValue userLoginPerson = EntityQuery.use(delegator).from("UserLoginPerson")
							.where("userLoginId", owner).queryOne();
					if (UtilValidate.isNotEmpty(userLoginPerson)) {
						ownerBu = userLoginPerson.getString("businessUnit");
					}
				}

				if (UtilValidate.isNotEmpty(ownerBu)) {
					callCtxt.put("wftMsdbusinessunit", ownerBu);
					callCtxt.put("businessUnitId", ownerBu);
				}

				if (UtilValidate.isNotEmpty(requiredAttendees)) {
					requiredAttendees = requiredAttendees.replace("[", "").replace("]", "").replace(" ", "");
					callCtxt.put("nrequired", UtilMisc.toList(UtilMisc.toSetArray(requiredAttendees.split(","))));
				}
				if (UtilValidate.isNotEmpty(optionalAttendees)) {
					optionalAttendees = optionalAttendees.replace("[", "").replace("]", "").replace(" ", "");
					callCtxt.put("noptional", UtilMisc.toList(UtilMisc.toSetArray(optionalAttendees.split(","))));
				}

				callCtxt.put("endPointType", "OFCRM");

				callCtxt.put("domainEntityType", domainEntityType);
				callCtxt.put("domainEntityId", domainEntityId);
				callCtxt.put("entityTimeZoneId", timeZoneId);

				callCtxt.put("userLogin", userLogin);

				Debug.log("inMap=============="+callCtxt);

				callResult = dispatcher.runSync("crmPortal.createInteractiveActivity", callCtxt);
				if (ServiceUtil.isError(callResult)) {
					result.putAll(ServiceUtil.returnError(ServiceUtil.getErrorMessage(callResult)));
					return result;
				}

				result.put("workEffortId", callResult.get("workEffortId"));
			} else {
				String errMsg = "";
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errMsg = accessMatrixRes.get("errorMessage").toString();
				} else {
					errMsg = "Access Denied";
				}
				result.putAll(ServiceUtil.returnError(errMsg));
				return result;
			}

		} catch (Exception e) {
			//e.printStackTrace();
			
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully created activity.."));

		return result;

	}

	public static Map updateActivity(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String workEffortId = (String) context.get("workEffortId");

		String subject = (String) context.get("workEffortName");
		String workEffortTypeId = (String) context.get("workEffortTypeId");
		String priority = (String) context.get("priority");
		String owner = (String) context.get("owner");
		String statusId = (String) context.get("currentStatusId");
		String description = (String) context.get("description");
		String ownerBu = (String) context.get("businessUnitId");
		String timeZoneId = (String) context.get("entityTimeZoneId");
		String location = (String) context.get("location");

		String domainEntityType = (String) context.get("domainEntityType");
		String domainEntityId = (String) context.get("domainEntityId");

		String estimatedStartDate = (String) context.get("estimatedStartDate_date");
		String estimatedCompletionDate = (String) context.get("estimatedCompletionDate_date");
		String actualStartDate = (String) context.get("actualStartDate_date");
		String actualCompletionDate = (String) context.get("actualCompletionDate_date");

		String estimatedStartTime = (String) context.get("estimatedStartDate_time");
		String estimatedCompletionTime = (String) context.get("estimatedCompletionDate_time");
		String actualStartTime = (String) context.get("actualStartDate_time");
		String actualCompletionTime = (String) context.get("actualCompletionDate_time");

		String requiredAttendees = (String) context.get("requiredAttendees");
		String optionalAttendees = (String) context.get("optionalAttendees");

		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
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
				if(UtilValidate.isEmpty(owner)) {
					owner = teamId;
				} else {
					currentPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
					Map<String, Object> buTeamData = DataUtil.getUserBuTeam(delegator, currentPartyId);
					businessUnit = (String) buTeamData.get("businessUnit");
					teamId = (String) buTeamData.get("emplTeamId");
				}
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				//change the access in the create 
				//check with ownerId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					if(!ownerIds.contains(teamId)) accessLevel = null;
					conditionList.add(EntityCondition.makeCondition("primOwnerId", EntityOperator.IN, ownerIds));
				}

				//check with emplTeamId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					if(!emplTeamIds.contains(teamId)) accessLevel = null;
					conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
				}


				conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue activity = EntityQuery.use(delegator).from("WorkEffort").where(mainConditons).queryFirst();
				if(UtilValidate.isEmpty(activity)) accessLevel=null;

			}
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				result.put("workEffortId", workEffortId);

				List conditionList = FastList.newInstance();

				conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue activity = EntityUtil.getFirst( delegator.findList("WorkEffort", mainConditons, null, null, null, false) );

				if (UtilValidate.isEmpty(activity)) {
					result.putAll(ServiceUtil.returnSuccess("Activity not exists!"));
					return result;
				}

				Timestamp estimatedStartDateTime = ParamUtil.getTimestamp(estimatedStartDate, estimatedStartTime, "yyyy-MM-dd hh:mm");
				Timestamp estimatedCompletionDateTime = ParamUtil.getTimestamp(estimatedCompletionDate, estimatedCompletionTime, "yyyy-MM-dd hh:mm");
				Timestamp actualStartDateTime = ParamUtil.getTimestamp(actualStartDate, actualStartTime, "yyyy-MM-dd hh:mm");
				Timestamp actualCompletionDateTime = ParamUtil.getTimestamp(actualCompletionDate, actualCompletionTime, "yyyy-MM-dd hh:mm");

				callCtxt.put("description", description);
				if (UtilValidate.isNotEmpty(description)) {
					description = Base64.getEncoder().encodeToString(description.getBytes("utf-8"));
					callCtxt.put("description", description);
				}

				String emplTeamId = null;
				GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", owner).queryList());
				if (UtilValidate.isNotEmpty(emplTeam)) { 
					if (UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId"))) {
						emplTeamId = emplTeam.getString("emplTeamId");
					}
				}

				callCtxt.put("externalId", activity.getString("externalId"));	
				//callCtxt.put("workEffortTypeId", workEffortTypeId);	
				//callCtxt.put("workEffortServiceType", workEffortTypeId);	

				callCtxt.put("emplTeamId", emplTeamId);
				callCtxt.put("currentStatusId", statusId);
				callCtxt.put("workEffortName", subject);
				callCtxt.put("estimatedStartDate", estimatedStartDateTime);
				callCtxt.put("estimatedCompletionDate", estimatedCompletionDateTime);
				callCtxt.put("actualStartDate", actualStartDateTime);
				callCtxt.put("actualCompletionDate", actualCompletionDateTime);
				callCtxt.put("wftLocation", location);

				callCtxt.put("primOwnerId", owner);
				callCtxt.put("ownerPartyId", owner);

				if (UtilValidate.isNotEmpty(priority)) {
					callCtxt.put("priority", Long.valueOf(priority));
				}

				if (UtilValidate.isEmpty(ownerBu)) {
					GenericValue userLoginPerson = EntityQuery.use(delegator).from("UserLoginPerson")
							.where("userLoginId", owner).queryOne();
					if (UtilValidate.isNotEmpty(userLoginPerson)) {
						ownerBu = userLoginPerson.getString("businessUnit");
					}
				}

				if (UtilValidate.isNotEmpty(ownerBu)) {
					callCtxt.put("wftMsdbusinessunit", ownerBu);
					callCtxt.put("businessUnitId", ownerBu);
				}

				if (UtilValidate.isNotEmpty(requiredAttendees)) {
					requiredAttendees = requiredAttendees.replace("[", "").replace("]", "").replace(" ", "");
					callCtxt.put("nrequired", UtilMisc.toList(UtilMisc.toSetArray(requiredAttendees.split(","))));
				}
				if (UtilValidate.isNotEmpty(optionalAttendees)) {
					optionalAttendees = optionalAttendees.replace("[", "").replace("]", "").replace(" ", "");
					callCtxt.put("noptional", UtilMisc.toList(UtilMisc.toSetArray(optionalAttendees.split(","))));
				}

				callCtxt.put("endPointType", "OFCRM");

				callCtxt.put("domainEntityType", domainEntityType);
				callCtxt.put("domainEntityId", domainEntityId);
				callCtxt.put("entityTimeZoneId", timeZoneId);

				callCtxt.put("userLogin", userLogin);

				Debug.log("inMap=============="+callCtxt);

				callResult = dispatcher.runSync("crmPortal.updateInteractiveActivity", callCtxt);
				if (ServiceUtil.isError(callResult)) {
					result.putAll(ServiceUtil.returnError(ServiceUtil.getErrorMessage(callResult)));
					return result;
				}
			} else {
				String errMsg = "";
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errMsg = accessMatrixRes.get("errorMessage").toString();
				} else {
					errMsg = "Access Denied";
				}
				result.putAll(ServiceUtil.returnSuccess(errMsg));
				return result;
			}
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully updated activity.."));

		return result;

	}
	
	public static Map findInteractiveActivity(DispatchContext dctx, Map context) {
        
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map requestContext = (Map) context.get("requestContext");
        
        String activityNo = (String) context.get("activityNo");
		List defaultActivityTypes = (List) context.get("defaultActivityTypes");
		List defaultStatusIds = (List) context.get("defaultStatusIds");
		List statusNotInIds = (List) context.get("statusNotInIds");
		List activityIdList = (List) context.get("activityNoList");
		
		String activitySubType = (String) context.get("activitySubType");
		String createdBy = (String) context.get("createdBy");
		String open = (String) context.get("open");
		String closed = (String) context.get("closed");
		Timestamp systemTime = UtilDateTime.nowTimestamp();
		String startDate = (String) context.get("startDate");
		String endDate = (String) context.get("endDate");
		
		String activityName = (String) requestContext.get("activityName");
		String isChecklistActivity = (String) requestContext.get("isChecklistActivity");
		
		String scheduledStartDate = (String) context.get("scheduledStartDate");
		String scheduledEndDate = (String) context.get("scheduledEndDate");
        
		String searchType = (String) context.get("searchType");
        String nextPageNum = (String) context.get("nextPageNum");
        
        String isSrActivityOnly = (String) context.get("isSrActivityOnly");
        String custRequestId = (String) context.get("custRequestId");
        String isPostalCodeRequired = (String) context.get("isPostalCodeRequired");
        String orderByColumn = (String) context.get("orderByColumn");
        String orderByDirection = (String) context.get("orderByDirection");
        String requiredSrInfo = (String) context.get("requiredSrInfo");
        Object srTypeId = context.get("srTypeId");
        Object owner = context.get("owner");
        Object activityType = context.get("activityType");
        Object activityWorkType = requestContext.get("activityWorkType");
        Object statusId = context.get("statusId");
        Object location = (Object) requestContext.get("location");
        
        String domainEntityType = (String) context.get("domainEntityType");
        String domainEntityId = (String) context.get("domainEntityId");
        
        String salesPerson = (String) requestContext.get("salesPerson");
    	String customerId = (String) requestContext.get("customerId");
    	String contractorId = (String) requestContext.get("contractorId");
    	String status = (String) context.get("status");
    	
    	String srNo = (String) requestContext.get("srNo");
		String srPrimaryContactId = (String) requestContext.get("srPrimaryContactId");
		String srPartyId = (String) requestContext.get("srPartyId");
		
        ArrayList<String> statuses = new ArrayList<String>();
        List<GenericValue> resultList = new ArrayList<>();
        Map<String, Object> result = new HashMap<String, Object>();
        
        try {
        	
        	String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
        	String hideSmsActivity = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_HIDE_SMS", "N");
        	boolean hideSMSActivity = false;
        	if(UtilValidate.isNotEmpty(hideSmsActivity) && "Y".equals(hideSmsActivity)) {
        		hideSMSActivity = true;
        	}
            
            Map<String, Object> callCtxt = FastMap.newInstance();
            Map<String, Object> callResult = FastMap.newInstance();
            
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
            
            	List<EntityCondition> conditionList = FastList.newInstance();
            	
            	if (UtilValidate.isNotEmpty(isSrActivityOnly) && isSrActivityOnly.equals("Y")) {
            		conditionList.addAll(DataHelper.prepareSrCondition(delegator, requestContext));
            	}

				//check with ownerId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					conditionList.add(EntityCondition.makeCondition("ownerId", EntityOperator.IN, ownerIds));
				}

				//check with emplTeamId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
				}

				if(UtilValidate.isNotEmpty(status)) {
					conditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, status));
				}
				if (UtilValidate.isNotEmpty(activityNo)) {
					conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.LIKE,"%"+activityNo + "%"));
				}
				if (UtilValidate.isNotEmpty(activityName)) {
					conditionList.add(EntityCondition.makeCondition("workEffortName", EntityOperator.LIKE,"%"+activityName + "%"));
				}
				
				if(UtilValidate.isNotEmpty(activityIdList)) {
					conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, activityIdList));
				}

				if (UtilValidate.isNotEmpty(activityType)) {
					if (!(activityType instanceof List)) activityType = UtilMisc.toList(""+activityType);
					conditionList.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.IN, activityType));
				} else {
					conditionList.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.IN, defaultActivityTypes));
				}
				
				if(!hideSMSActivity) {
					if (UtilValidate.isNotEmpty(activityWorkType)) {
						if (!(activityWorkType instanceof List)) activityWorkType = UtilMisc.toList(""+activityWorkType);
						conditionList.add(EntityCondition.makeCondition("workEffortPurposeTypeId", EntityOperator.IN, activityWorkType));
					}
				}else {
					if (UtilValidate.isNotEmpty(activityWorkType)) {
						if (!(activityWorkType instanceof List)) activityWorkType = UtilMisc.toList(""+activityWorkType);
						conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("workEffortPurposeTypeId", EntityOperator.IN, activityWorkType),
								EntityCondition.makeCondition("workEffortPurposeTypeId", EntityOperator.NOT_IN, UtilMisc.toList("SMS"))
								));
					}else {
						conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("workEffortPurposeTypeId", EntityOperator.EQUALS, null),
								EntityCondition.makeCondition("workEffortPurposeTypeId", EntityOperator.NOT_IN, UtilMisc.toList("SMS"))
								));
					}
				}
				
				if (UtilValidate.isNotEmpty(owner)) {
					if (!(owner instanceof List)) owner = UtilMisc.toList(""+owner);
					conditionList.add(EntityCondition.makeCondition("ownerId", EntityOperator.IN, owner));
					if (UtilValidate.isNotEmpty(requestContext.get("isActiveAssoc")) && requestContext.get("isActiveAssoc").equals("Y")) {
						conditionList.add(EntityUtil.getFilterByDateExpr());
					}
				}
				
				if (UtilValidate.isNotEmpty(activitySubType)) {
					conditionList.add(EntityCondition.makeCondition("workEffortSubServiceType", EntityOperator.EQUALS, activitySubType));
				}
				if (UtilValidate.isNotEmpty(createdBy)) {
					conditionList.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.LIKE,"%"+createdBy + "%"));
				}
				
				if(UtilValidate.isNotEmpty(scheduledStartDate)) {
					Timestamp sd = UtilDateTime.stringToTimeStamp(scheduledStartDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					conditionList.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(sd)));
				}
				if (UtilValidate.isNotEmpty(scheduledEndDate)) {
					Timestamp ed = UtilDateTime.stringToTimeStamp(scheduledEndDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					conditionList.add(EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(ed)));
				}
				
				if (UtilValidate.isNotEmpty(startDate)) {
					Timestamp sd = UtilDateTime.stringToTimeStamp(startDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					conditionList.add(EntityCondition.makeCondition("actualStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(sd)));
				}
				if (UtilValidate.isNotEmpty(endDate)) {
					Timestamp ed = UtilDateTime.stringToTimeStamp(endDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					conditionList.add(EntityCondition.makeCondition("actualCompletionDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(ed)));
				}

				if (UtilValidate.isNotEmpty(open)) {
					statuses.add(open);
				}
				if (UtilValidate.isNotEmpty(closed)) {
					statuses.add(closed);
				}
				if (UtilValidate.isNotEmpty(statusId)) {
					if (statusId instanceof List) statuses.addAll((List)statusId);
					else statuses.add(""+statusId);
				}

				if (UtilValidate.isNotEmpty(searchType)) {
					if (searchType.equals(ActivitySearchType.MY_ACTIVITY)) {
						statuses.add("IA_MCOMPLETED");
						statuses.add("IA_OPEN");
					} else if (searchType.equals(ActivitySearchType.MY_OPEN_ACTIVITY)) {
						statuses.add("IA_OPEN");
					} else if (searchType.equals(ActivitySearchType.MY_COMPLETED_ACTIVITY)) {
						statuses.add("IA_MCOMPLETED");
					}
				}
				
				if (UtilValidate.isNotEmpty(defaultStatusIds)) {
					statuses.addAll(defaultStatusIds);
				}

				if (UtilValidate.isNotEmpty(statuses)) {
					conditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.IN, statuses));
				}
				
				if (UtilValidate.isNotEmpty(statusNotInIds)) {
					conditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_IN, statusNotInIds));
				}
				
				if (UtilValidate.isNotEmpty(custRequestId)) {
					conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				}
				
				if (UtilValidate.isNotEmpty(isPostalCodeRequired) && isPostalCodeRequired.equals("Y")) {
					conditionList.add(EntityCondition.makeCondition("pstlPostalCode", EntityOperator.NOT_EQUAL, null));
				}
				
				if (UtilValidate.isNotEmpty(location)) {
					if (!(location instanceof List)) location = UtilMisc.toList(""+location);
					String customFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);
					conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, customFieldId),
							EntityCondition.makeCondition("attrValue", EntityOperator.IN, location)
							));
				} 
				
				if(UtilValidate.isNotEmpty(isChecklistActivity)) {
					if (isChecklistActivity.equals("Y")) {
						conditionList.add(EntityCondition.makeCondition("channelId", EntityOperator.EQUALS, Channels.PROGRAM));
					} else {
						conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("channelId", EntityOperator.EQUALS, null),
								EntityCondition.makeCondition("channelId", EntityOperator.NOT_EQUAL, Channels.PROGRAM)
								));
					}
				}
				
				if (UtilValidate.isNotEmpty(domainEntityId)) {
					conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
				}
				if (UtilValidate.isNotEmpty(domainEntityType)) {
					conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
				}
				
				if (UtilValidate.isNotEmpty(isSrActivityOnly) && isSrActivityOnly.equals("N")) {
					List<String> domainEntityTypes = CommonUtils.getAssessibleDomainEntityTypes(delegator, userLoginId);
					if (UtilValidate.isNotEmpty(domainEntityTypes)) {
						Debug.logInfo("Assessible domains: "+domainEntityTypes, MODULE);
						conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.IN, domainEntityTypes));
					}
				}
				
	            EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            Debug.logInfo("findInteractiveActivity condition: "+mainConditons, MODULE);
	            
	            String orderBy = "workEffortId";
	            if (UtilValidate.isNotEmpty(orderByColumn)) {
	            	orderBy = orderByColumn;
	            }
	            String orderDirection = "DESC";
	            if (UtilValidate.isNotEmpty(orderByDirection)) {
	            	orderDirection = orderByDirection;
	            }
	            
	            DynamicViewEntity dynamicView = new DynamicViewEntity();
	            boolean isSrAdded = false;
	            if (UtilValidate.isNotEmpty(isSrActivityOnly) && isSrActivityOnly.equals("Y")) {
	            	isSrAdded= true;
		            dynamicView.addMemberEntity("CRWE", "CustRequestWorkEffort");
		            dynamicView.addAlias("CRWE", "custRequestId");
		            
		            dynamicView.addMemberEntity("CR", "CustRequest");
		            dynamicView.addAlias("CR", "srStatus", "statusId", null, null, null, null);
		            dynamicView.addAlias("CR", "srType", "custRequestTypeId", null, null, null, null);
		            dynamicView.addAlias("CR", "srName", "custRequestName", null, null, null, null);
		            dynamicView.addAlias("CR", "fromPartyId", "fromPartyId", null, null, null, null);
		            dynamicView.addAlias("CR", "custRequestTypeId", "custRequestTypeId", null, null, null, null);
		            dynamicView.addAlias("CR", "custRequestCategoryId", "custRequestCategoryId", null, null, null, null);
		            dynamicView.addAlias("CR", "custRequestSubCategoryId", "custRequestSubCategoryId", null, null, null, null);
		            dynamicView.addAlias("CR", "priority", "priority", null, null, null, null);
		            dynamicView.addAlias("CR", "custRequestName", "custRequestName", null, null, null, null);
		            dynamicView.addAlias("CR", "responsiblePerson", "responsiblePerson", null, null, null, null);
		            dynamicView.addAlias("CR", "statusId", "statusId", null, null, null, null);
		            
		            dynamicView.addMemberEntity("CRS", "CustRequestSupplementory");
		            dynamicView.addAlias("CRS", "pstlAttnName");
		            dynamicView.addAlias("CRS", "pstlAddress1");
		            dynamicView.addAlias("CRS", "pstlAddress2");
		            dynamicView.addAlias("CRS", "pstlPostalCity");
		            dynamicView.addAlias("CRS", "pstlCountyGeoId");
		            dynamicView.addAlias("CRS", "pstlPostalCode");
		            dynamicView.addAlias("CRS", "pstlPostalCodeExt");
		            dynamicView.addAlias("CRS", "pstlCountryGeoId");
		            dynamicView.addAlias("CRS", "pstlStateProvinceGeoId");
		            dynamicView.addAlias("CRS", "latitude");
		            dynamicView.addAlias("CRS", "longitude");
		            dynamicView.addAlias("CRS", "isUspsAddrVerified");
		            dynamicView.addAlias("CRS", "commitDate");
		            dynamicView.addAlias("CRS", "preEscalationDate");
		            dynamicView.addAlias("CRS", "homePhoneNumber");
		            dynamicView.addAlias("CRS", "offPhoneNumber");
		            dynamicView.addAlias("CRS", "mobileNumber");
		            dynamicView.addAlias("CRS", "contractorEmail");
		            dynamicView.addAlias("CRS", "contractorOffPhone");
		            dynamicView.addAlias("CRS", "contractorMobilePhone");
		            
		            dynamicView.addAlias("CRS", "purchaseOrder");
		            
		            if (UtilValidate.isNotEmpty(location)) {
						dynamicView.addMemberEntity("ATTR", "CustRequestAttribute");
						dynamicView.addAlias("ATTR", "attrName");
						dynamicView.addAlias("ATTR", "attrValue");
					}
		            
		            if( UtilValidate.isNotEmpty(customerId)
							|| UtilValidate.isNotEmpty(salesPerson)
							|| UtilValidate.isNotEmpty(contractorId)
							) {
		            	dynamicView.addMemberEntity("CRP", "CustRequestParty");
						dynamicView.addAlias("CRP", "crpPartyId","partyId",null,Boolean.FALSE,Boolean.FALSE,null);
						dynamicView.addAlias("CRP", "roleTypeId");
						dynamicView.addAlias("CRP", "crpFromDate","fromDate",null,Boolean.FALSE,Boolean.FALSE,null);
						dynamicView.addAlias("CRP", "crpThruDate","thruDate",null,Boolean.FALSE,Boolean.FALSE,null);
						
						List<String> crpPartyIds = new ArrayList<>();
						List<String> roleTypeIds = new ArrayList<>();
						String crpPartyId = null;
						String roleTypeId = null;
						
						if (UtilValidate.isNotEmpty(customerId)) {
							crpPartyId = customerId;
							roleTypeId = "CUSTOMER";
							crpPartyIds.add(crpPartyId);
							roleTypeIds.add(roleTypeId);
						} 
						if (UtilValidate.isNotEmpty(salesPerson)) {
							crpPartyId = salesPerson;
							roleTypeId = "SALES_REP";
							crpPartyIds.add(crpPartyId);
							roleTypeIds.add(roleTypeId);
						}
						if (UtilValidate.isNotEmpty(contractorId)) {
							crpPartyId = contractorId;
							roleTypeId = "CONTRACTOR";
							crpPartyIds.add(crpPartyId);
							roleTypeIds.add(roleTypeId);
						}
						
						conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
				    			EntityCondition.makeCondition("crpPartyId", EntityOperator.IN, crpPartyIds),
				    			EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeIds),
				    			EntityCondition.makeCondition("crpThruDate", EntityOperator.EQUALS, null)
				    			));
		            }
	            }
	            if (UtilValidate.isNotEmpty(srPartyId)) {
	            	conditionList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, srPartyId));
	            }
	            if (UtilValidate.isNotEmpty(srNo)) {
	            	conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.LIKE, "%"+srNo + "%"));
	            }
	            if(UtilValidate.isNotEmpty(requiredSrInfo) && "Y".equals(requiredSrInfo) && !isSrAdded) {
	            	dynamicView.addMemberEntity("CRWE", "CustRequestWorkEffort");
		            dynamicView.addAlias("CRWE", "custRequestId");
		            
		            dynamicView.addMemberEntity("CR", "CustRequest");
		            //dynamicView.addAlias("CR", "srStatus", "statusId", null, null, null, null);
		            //dynamicView.addAlias("CR", "srType", "custRequestTypeId", null, null, null, null);
		            dynamicView.addAlias("CR", "srName", "custRequestName", null, null, null, null);
		            dynamicView.addAlias("CR", "fromPartyId", "fromPartyId", null, null, null, null);
		            dynamicView.addAlias("CR", "custRequestTypeId", "custRequestTypeId", null, null, null, null);
		            //dynamicView.addAlias("CR", "custRequestCategoryId", "custRequestCategoryId", null, null, null, null);
		            //dynamicView.addAlias("CR", "custRequestSubCategoryId", "custRequestSubCategoryId", null, null, null, null);
		            //dynamicView.addAlias("CR", "priority", "priority", null, null, null, null);
		            dynamicView.addAlias("CR", "custRequestName", "custRequestName", null, null, null, null);
		            //dynamicView.addAlias("CR", "responsiblePerson", "responsiblePerson", null, null, null, null);
		            //dynamicView.addAlias("CR", "statusId", "statusId", null, null, null, null);

		            dynamicView.addMemberEntity("CRC", "CustRequestContact");
		            dynamicView.addAlias("CRC", "srPrimaryContactId","partyId", null, null, null, null);
		            dynamicView.addAlias("CRC", "fromDate","srPrimaryContactFromDate", null, null, null, null);
		            dynamicView.addAlias("CRC", "thruDate","srPrimaryContactThruDate", null, null, null, null);
		            dynamicView.addAlias("CRC", "isPrimary");
		            if(UtilValidate.isNotEmpty(srPrimaryContactId)) {
		            	conditionList.add(EntityCondition.makeCondition("srPrimaryContactId", EntityOperator.EQUALS, srPrimaryContactId));
		            	conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
		            }
		            if (UtilValidate.isNotEmpty(srTypeId)) {
						if (!(srTypeId instanceof List)) srTypeId = UtilMisc.toList(""+srTypeId);
						conditionList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.IN, srTypeId));
					}
	            }
	            
	            dynamicView.addMemberEntity("WE", "WorkEffort");
				dynamicView.addAlias("WE", "workEffortId", null, null, null, true, null);
				
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
				dynamicView.addAlias("WE", "lastUpdatedStamp");
				dynamicView.addAlias("WE", "createdByUserLogin");
				//dynamicView.addAlias("WE", "primOwnerId");
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

				if (UtilValidate.isNotEmpty(owner)) {
					dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
					dynamicView.addAlias("WEPA", "partyId");
					dynamicView.addAlias("WEPA", "roleTypeId");
					dynamicView.addAlias("WEPA", "fromDate");
					dynamicView.addAlias("WEPA", "thruDate");
					dynamicView.addAlias("WEPA", "statusId");
					dynamicView.addAlias("WEPA", "statusDateTime");
					dynamicView.addAlias("WEPA", "callOutCome");
					dynamicView.addAlias("WEPA", "assignedByUserLoginId");
					dynamicView.addAlias("WEPA", "partyAssignFacilityId", "facilityId", null, null, null, null);
					dynamicView.addAlias("WEPA", "ownerId");
					dynamicView.addAlias("WEPA", "emplTeamId");
					dynamicView.addAlias("WEPA", "businessUnit");
				}
				
				/*dynamicView.addMemberEntity("CRM", "CallRecordMaster");
				dynamicView.addAlias("CRM", "callBackDate");*/
					
				if (UtilValidate.isNotEmpty(isSrActivityOnly) && isSrActivityOnly.equals("Y")) {
					dynamicView.addViewLink("CRWE", "CR", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
					dynamicView.addViewLink("CRWE", "WE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					dynamicView.addViewLink("CR", "CRS", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
					
					if (UtilValidate.isNotEmpty(location)) {
						dynamicView.addViewLink("CR", "ATTR", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
					}
					
					if( UtilValidate.isNotEmpty(customerId)
							|| UtilValidate.isNotEmpty(salesPerson)
							|| UtilValidate.isNotEmpty(contractorId)
							) {
						dynamicView.addViewLink("CR", "CRP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
					}
				}
				if(UtilValidate.isNotEmpty(requiredSrInfo) && "Y".equals(requiredSrInfo) && !isSrAdded) {
	            	dynamicView.addViewLink("CRWE", "CR", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
					dynamicView.addViewLink("CRWE", "WE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					dynamicView.addViewLink("CRWE", "CRC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
				}
				
				if (UtilValidate.isNotEmpty(owner)) {
					dynamicView.addViewLink("WE", "WEPA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
				}
				//dynamicView.addViewLink("WE","CRM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));

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
		        
		        Debug.logInfo("findInteractiveActivity start: "+UtilDateTime.nowTimestamp(), MODULE);
				int highIndex = 0;
	            int lowIndex = 0;
	            int resultListSize = 0;
	            try {
	                // get the indexes for the partial list
	            	lowIndex = viewIndex * viewSize + 1;
	                highIndex = (viewIndex + 1) * viewSize;
	                
	                // set distinct on so we only get one row per order
	                // using list iterator
	                EntityListIterator pli = EntityQuery.use(delegator)
	                		//.select(fieldsToSelect)
	                        .from(dynamicView)
	                        .where(mainConditons)
	                        .orderBy(UtilMisc.toList(orderBy+" "+orderDirection))
	                        .cursorScrollInsensitive()
	                        .fetchSize(highIndex)
	                        //.distinct()
	                        .cache(true)
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
	            Debug.logInfo("findInteractiveActivity end: "+UtilDateTime.nowTimestamp(), MODULE);
	            Debug.logInfo("findInteractiveActivity count: "+resultList.size(), MODULE);
	            result.put("workEffortList", resultList);
	            
	            /////////////////////////////////////////////////////////////////////////////////
	            
	            /*Long count = new Long(0);
	            
	            EntityFindOptions efoNum= new EntityFindOptions();
	            efoNum.setFetchSize(1000);
	            
	            Debug.logInfo("count 1 start: "+UtilDateTime.nowTimestamp(), MODULE);
	            count = delegator.findCountByCondition(viewEntityName, mainConditons, null, null, efoNum);
	            Debug.logInfo("count 2 end: "+UtilDateTime.nowTimestamp(), MODULE);
	            Debug.logInfo("findInteractiveActivity count: "+count, MODULE);
	            EntityFindOptions efo = new EntityFindOptions();
	            
	            if (UtilValidate.isNotEmpty(nextPageNum) && count > 0) {
	                long npn = Long.parseLong(nextPageNum) - 1;
	                
	                int startInx = (int) (npn * GlobalConstants.DEFAULT_PER_PAGE_COUNT);
	                int endInx = count.intValue() < GlobalConstants.DEFAULT_PER_PAGE_COUNT ? count.intValue() : GlobalConstants.DEFAULT_PER_PAGE_COUNT;        
	                
	                efo.setOffset(startInx);
	                efo.setLimit(endInx);
	            }
	            
	            String orderBy = "workEffortId";
	            if (UtilValidate.isNotEmpty(orderByColumn)) {
	            	orderBy = orderByColumn;
	            }
	            
	            // UtilMisc.toList("createdDate DESC")
	            List<GenericValue> workEffortList = new ArrayList<GenericValue>();
	            if (count > 0) {
	                workEffortList = delegator.findList(viewEntityName, mainConditons, null, UtilMisc.toList(orderBy+" DESC"), efo, false);
	            }
	            
	            result.put("workEffortList", resultList);
	            result.put("totalCount", count);*/
	            
            }
            
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        
        result.putAll(ServiceUtil.returnSuccess("Successfully find activity.."));
        
        return result;
        
    }
	
}
