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
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilActivity;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.activity.portal.ActivityPortalConstants.ActivitySearchType;
import org.groupfio.activity.portal.util.DataHelper;
import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.util.UtilAttribute;
import org.groupfio.common.portal.util.UtilCommon;
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
public class ProgramServices {

	private static final String MODULE = ProgramServices.class.getName();

	public static Map generateProgAct(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");

		String srNumber = (String) requestContext.get("srNumber");
		String actFrom = (String) requestContext.get("actFromDate");
		String actTo = (String) requestContext.get("actToDate");
		String groupingCodeId = (String) requestContext.get("groupingCodeId");
		String numberOfDays = (String) requestContext.get("numberOfDays");
		
		String ownerPartyId = (String) requestContext.get("ownerPartyId");
		String ownerRoleTypeId = (String) requestContext.get("ownerRoleTypeId");
		String ownerUserLoginId = (String) requestContext.get("ownerUserLoginId");
		
		String emplTeamId = (String) requestContext.get("emplTeamId");
		String businessUnit = (String) requestContext.get("businessUnit");
		String domainEntityType = (String) requestContext.get("domainEntityType");
		String domainEntityId = (String) requestContext.get("domainEntityId");
		
		String activityNamePrefix = (String) context.get("activityNamePrefix");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			
			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String nativeBusinessUnit = null;
			String nativeTeamId = null;
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();

			/*
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

			}*/
			
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				
				Timestamp actFromDate = UtilDateTime.stringToTimeStamp(actFrom, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
				Timestamp actToDate = null;
				if (UtilValidate.isNotEmpty(actTo)) {
					actToDate = UtilDateTime.stringToTimeStamp(actTo, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
				} else if (UtilValidate.isNotEmpty(numberOfDays)) {
					actToDate = UtilDateTime.addDaysToTimestamp(actFromDate, Integer.parseInt(numberOfDays));
				}
				
				int totalDays = UtilDateTime.getIntervalInDays(actFromDate, actToDate);
				
				if (UtilValidate.isEmpty(activityNamePrefix)) {
					activityNamePrefix = "Checklist";
				}
				
				int count = 0;
				while(count < totalDays) {
					callCtxt = FastMap.newInstance();
					
					String workEffortName = (count+1) + " of " + totalDays + " Days Checklist";
					String description = activityNamePrefix + " task for# "+PartyHelper.getPartyName(delegator, ownerPartyId, false)+" ("+ownerPartyId+")";
					description += UtilCommon.getDomainName(delegator, UtilMisc.toMap("domainEntityType", domainEntityType, "domainEntityId", domainEntityId, "domainIndicatorDes", "Program#"));
					
					callCtxt.put("workEffortTypeId", "TASK");
					callCtxt.put("currentStatusId", "IA_OPEN");
					callCtxt.put("workEffortName", workEffortName);
					callCtxt.put("estimatedStartDate", actFromDate);
					callCtxt.put("estimatedCompletionDate", UtilDateTime.getDayEnd(actFromDate));
					//callCtxt.put("wftLocation", location);
					callCtxt.put("workEffortPurposeTypeId", "10010"); // other type
					callCtxt.put("createdDate", nowTimestamp);
					
					if (UtilValidate.isNotEmpty(description)) {
						description = Base64.getEncoder().encodeToString(description.getBytes());
						callCtxt.put("description", description);
					}
					
					//callCtxt.put("wftMsdduration", duration);
					//callCtxt.put("priority", Long.valueOf(priority));
					
					callCtxt.put("partyId", ownerPartyId);
					callCtxt.put("roleTypeId", ownerRoleTypeId);
					callCtxt.put("partyUserLoginId", ownerUserLoginId);
					
					callCtxt.put("channelId", Channels.PROGRAM);
					callCtxt.put("emplTeamId", emplTeamId);
					callCtxt.put("businessUnitId", businessUnit);
					callCtxt.put("wftMsdbusinessunit", businessUnit);
					callCtxt.put("businessUnitId", businessUnit);
					
					callCtxt.put("endPointType", "OFCRM");
					callCtxt.put("domainEntityType", domainEntityType);
					callCtxt.put("domainEntityId", domainEntityId);
					
					callCtxt.put("userLogin", userLogin);
					
					callResult = dispatcher.runSync("crmPortal.createInteractiveActivity", callCtxt);
					if (ServiceUtil.isSuccess(callResult)) {
						String workEffortId = (String) callResult.get("workEffortId");
						UtilActivity.storeActivityAttribute(delegator, workEffortId, "ASSIGN_ATTR_GCODE", groupingCodeId);
						UtilActivity.storeActivityAttribute(delegator, workEffortId, "IS_PROG_ACT", "Y");
					}
					actFromDate = UtilDateTime.addDaysToTimestamp(actFromDate, 1);
					count++;
				}
				
				//result.put("workEffortId", callResult.get("workEffortId"));
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
	
	public static Map associateProgramActivities(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
		
		String programTemplateId = (String) requestContext.get("programTemplateId");
		String ownerPartyId = (String) requestContext.get("ownerPartyId");
		String ownerRoleTypeId = (String) requestContext.get("ownerRoleTypeId");
		
		String domainEntityType = (String) requestContext.get("domainEntityType");
		String domainEntityId = (String) requestContext.get("domainEntityId");
		String emplTeamId = (String) requestContext.get("emplTeamId");
		String businessUnit = (String) requestContext.get("businessUnit");
		
		String activityNamePrefix = (String) context.get("activityNamePrefix");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			
			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String nativeBusinessUnit = null;
			String nativeTeamId = null;
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();

			/*
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

			}*/
			
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				
				if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
					if (UtilValidate.isEmpty(activityNamePrefix)) {
						activityNamePrefix = "Checklist";
					}
					
					List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        			conditions.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, programTemplateId));
                	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                	
                	List<GenericValue> weList = EntityQuery.use(delegator).from("CustRequestWorkEffort").where(mainConditon).queryList();
                	if (UtilValidate.isNotEmpty(weList)) {
                		for (GenericValue we : weList) {
                			String workEffortId = we.getString("workEffortId");
                			String isProgAct = UtilActivity.getActivityAttrValue(delegator, workEffortId, "IS_PROG_ACT");
                			
                			if (UtilValidate.isNotEmpty(isProgAct) && isProgAct.equals("Y")) {
                				GenericValue activity = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryFirst();
                				if (UtilValidate.isEmpty(ownerRoleTypeId)) {
                					ownerRoleTypeId = "CUSTOMER";
                				}
                				if (UtilValidate.isEmpty(ownerPartyId)) {
                					GenericValue crParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId", domainEntityId, "roleTypeId", ownerRoleTypeId).filterByDate().queryFirst();
                        			if (UtilValidate.isNotEmpty(crParty)) {
                        				ownerPartyId = crParty.getString("partyId");
                        			}
                				}
                				
                    			callCtxt = FastMap.newInstance();
            					
            					String workEffortName = activity.getString("workEffortName");
            					String description = activityNamePrefix + " task for# "+PartyHelper.getPartyName(delegator, ownerPartyId, false)+" ("+ownerPartyId+")";
            					description += UtilCommon.getDomainName(delegator, UtilMisc.toMap("domainEntityType", domainEntityType, "domainEntityId", domainEntityId));
            					
            					callCtxt.put("workEffortTypeId", "TASK");
            					callCtxt.put("currentStatusId", "IA_OPEN");
            					callCtxt.put("workEffortName", workEffortName);
            					callCtxt.put("estimatedStartDate", activity.getTimestamp("estimatedStartDate"));
            					callCtxt.put("estimatedCompletionDate", activity.getTimestamp("estimatedCompletionDate"));
            					//callCtxt.put("wftLocation", location);
            					callCtxt.put("workEffortPurposeTypeId", "10010"); // other type
            					callCtxt.put("createdDate", nowTimestamp);
            					
            					if (UtilValidate.isNotEmpty(description)) {
            						description = Base64.getEncoder().encodeToString(description.getBytes());
            						callCtxt.put("description", description);
            					}
            					
            					//callCtxt.put("wftMsdduration", duration);
            					//callCtxt.put("priority", Long.valueOf(priority));
            					
            					callCtxt.put("partyId", ownerPartyId);
            					callCtxt.put("roleTypeId", ownerRoleTypeId);
            					callCtxt.put("partyUserLoginId", null);
            					
            					callCtxt.put("emplTeamId", emplTeamId);
            					callCtxt.put("businessUnitId", businessUnit);
            					callCtxt.put("wftMsdbusinessunit", businessUnit);
            					callCtxt.put("businessUnitId", businessUnit);
            					
            					callCtxt.put("endPointType", "OFCRM");
            					callCtxt.put("domainEntityType", domainEntityType);
            					callCtxt.put("domainEntityId", domainEntityId);
            					
            					callCtxt.put("userLogin", userLogin);
            					
            					callResult = dispatcher.runSync("crmPortal.createInteractiveActivity", callCtxt);
            					if (ServiceUtil.isSuccess(callResult)) {
            						String activityId = (String) callResult.get("workEffortId");
            						List<GenericValue> weAttrList = EntityQuery.use(delegator).from("WorkEffortAttribute").where("workEffortId", workEffortId).queryList();
                        			if (UtilValidate.isNotEmpty(weAttrList)) {
                        				for (GenericValue weAttr : weAttrList) {
                        					UtilActivity.storeActivityAttribute(delegator, activityId, weAttr.getString("attrName"), weAttr.getString("attrValue"));
                        				}
                        			}
            					}
                			}
                			
                		}
                	}
				}
				
				//result.put("workEffortId", callResult.get("workEffortId"));
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
		result.putAll(ServiceUtil.returnSuccess("Successfully associated activities with newly created Program.."));
		return result;
	}
	
}
