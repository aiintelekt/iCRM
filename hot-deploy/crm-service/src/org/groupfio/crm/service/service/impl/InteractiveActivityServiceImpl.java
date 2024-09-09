/**
 * 
 */
package org.groupfio.crm.service.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.SourceInvoked;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.ResponseUtils;
import org.fio.homeapps.util.UtilDateTime;
import org.fio.homeapps.util.UtilGenerator;
import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.crm.service.CrmServiceConstants.DomainEntityType;
import org.groupfio.crm.service.resolver.Resolver;
import org.groupfio.crm.service.resolver.ResolverFactory;
import org.groupfio.crm.service.resolver.ResolverConstants.ResolverType;
import org.groupfio.crm.service.util.DataHelper;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;

import java.util.Base64;
import java.util.Collections;

/**
 * @author Sharif
 *
 */
public class InteractiveActivityServiceImpl {

    private static final String MODULE = InteractiveActivityServiceImpl.class.getName();
    
    public static Map createInteractiveActivity(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String workEffortName = (String) context.get("workEffortName");
        String workEffortTypeId = (String) context.get("workEffortTypeId");
        String scopeEnumId = (String) context.get("scopeEnumId");
        String workEffortServiceType = (String) context.get("workEffortServiceType");
        String workEffortSubServiceType = (String) context.get("workEffortSubServiceType");
        String workEffortPurposeTypeId = (String) context.get("workEffortPurposeTypeId");
        String description = (String) context.get("description");
        String direction = (String) context.get("direction");
        String phoneNumber = (String) context.get("phoneNumber");
        Long priority = (Long) context.get("priority");
        Double estimatedMilliSeconds = (Double) context.get("estimatedMilliSeconds");
        Timestamp estimatedCompletionDate = (Timestamp) context.get("estimatedCompletionDate");
        Timestamp estimatedStartDate = (Timestamp) context.get("estimatedStartDate");
        Timestamp actualStartDate = (Timestamp) context.get("actualStartDate");
        Timestamp actualCompletionDate = (Timestamp) context.get("actualCompletionDate");
        String currentStatusId = (String) context.get("currentStatusId");
        String channelId = (String) context.get("channelId");
        String wfUserLoginId = (String) context.get("wfUserLoginId");
        String currentSubStatusId = (String) context.get("currentSubStatusId");
        String wfOnceDone = (String) context.get("wfOnceDone");
        String wfNationalId = (String) context.get("wfNationalId");
        String wfVplusId = (String) context.get("wfVplusId");
        String externalId = (String) context.get("externalId");
        String businessUnitId = (String) context.get("businessUnitId");
        Timestamp lastModifiedDate = (Timestamp) context.get("lastModifiedDate");
        String lastModifiedByUserLogin = (String) context.get("lastModifiedByUserLogin");
        
        String roleTypeId = (String) context.get("roleTypeId");
        String fromPartyId = (String) context.get("partyId");
        String partyUserLoginId = (String) context.get("partyUserLoginId");
        String callOutCome = (String) context.get("callOutCome");
        String ownerPartyId = (String) context.get("ownerPartyId");
        
        String wftLocation = (String) context.get("wftLocation");
        String wftAddress = (String) context.get("wftAddress");
        String wftRegardId = (String) context.get("wftRegardId");
        String wftSessionId = (String) context.get("wftSessionId");
        String wftNonFirstName = (String) context.get("wftNonFirstName");
        String wftNonLastName = (String) context.get("wftNonLastName");
        String wftMsdduration = (String) context.get("wftMsdduration");
        String wftMsdpercentcomplete = (String) context.get("wftMsdpercentcomplete");
        String wftMsdapplicationname = (String) context.get("wftMsdapplicationname");
        String wftMsdcifid = (String) context.get("wftMsdcifid");
        String wftMsdisclosedbybatch = (String) context.get("wftMsdisclosedbybatch");
        String wftMsdphonenumber = (String) context.get("wftMsdphonenumber");
        String wftMsdsenderrmcode = (String) context.get("wftMsdsenderrmcode");
        String wftMsdsequencenumber = (String) context.get("wftMsdsequencenumber");
        String wftMsdsmsuuid = (String) context.get("wftMsdsmsuuid");
        String wftMsdsubjecttemplate = (String) context.get("wftMsdsubjecttemplate");
        String wftMsdisbatch = (String) context.get("wftMsdisbatch");
        String wftMsdbusinessunit = (String) context.get("wftMsdbusinessunit");
        String wftMsdsubject = (String) context.get("wftMsdsubject");
        String wftMsdcreatedon = (String) context.get("wftMsdcreatedon");
        String wftMsdmodifiedon = (String) context.get("wftMsdmodifiedon");
        String wftCoverpageName = (String) context.get("wftCoverpageName");
        String wftCreatedConvert = (String) context.get("wftCreatedConvert");
        String wftCrmeditAccessFlag = (String) context.get("wftCrmeditAccessFlag");
        String emplTeamId = (String) context.get("emplTeamId");
        String primOwnerId = (String) context.get("primOwnerId");
        String trackingToken = (String) context.get("trackingToken");
        String objectName = (String) context.get("objectName");
        String appointmentType = (String) context.get("appointmentType");
        String compression = (String) context.get("compression");
        String customerCode = (String) context.get("customerCode");
        String faxNumber = (String) context.get("faxNumber");
        String isActivity = (String) context.get("isActivity");
        String message = (String) context.get("message");
        String noAttempts = (String) context.get("noAttempts");
        String submittedBy = (String) context.get("submittedBy");
        String msdDisplayDuration = (String) context.get("msdDisplayDuration");
        String wftAlertAlertlink = (String) context.get("wftAlertAlertlink");
        String wftAlertAlldayevent = (String) context.get("wftAlertAlldayevent");
        String resolution = (String) context.get("resolution");
        Long billableTime = (Long) context.get("billableTime");
        
        String smsCustPhoneNumber = (String) context.get("smsCustPhoneNumber");
        String smsType = (String) context.get("smsType");
        String smsSendStatus = (String) context.get("smsSendStatus");
        String smsFailReason = (String) context.get("smsFailReason");
        String smsSentTime = (String) context.get("smsSentTime");
        
        String systemName = (String) context.get("systemName");
        String endPointType = (String) context.get("endPointType");
        String domainEntityType = (String) context.get("domainEntityType");
        String domainEntityId = (String) context.get("domainEntityId");
        String entityTimeZoneId = (String) context.get("entityTimeZoneId");
        String ownerBookedCalSlots = (String) context.get("ownerBookedCalSlots");
        List<Map<String, Object>> documents = (List<Map<String, Object>>) context.get("documents");
        
        List<String> nsender = (List<String>) context.get("nsender");
        List<String> nto = (List<String>) context.get("nto");
        List<String> nbcc = (List<String>) context.get("nbcc");
        List<String> ncc = (List<String>) context.get("ncc");
        List<String> nrecipient = (List<String>) context.get("nrecipient");
        List<String> norganizer = (List<String>) context.get("norganizer");
        List<String> noptional = (List<String>) context.get("noptional");
        List<String> nrequired = (List<String>) context.get("nrequired");
        List<String> ownerList = (List<String>) context.get("ownerList");
        
        String objectType = (String) context.get("objectType");
        String objectId = (String) context.get("objectId");
        
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            
            if (UtilValidate.isNotEmpty(externalId)) {
                EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                        EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, externalId)
                        );
                
                GenericValue activity = EntityUtil.getFirst(delegator.findList("WorkEffort", mainCondition, null, null, null, false));
                if (UtilValidate.isNotEmpty(activity)) {
                    result.putAll(ServiceUtil.returnError("IA already exists!"));
                    return result;
                }
            }
            
            boolean isDummyCustomer = false;
            String cifPartyId = null;
            
            if (UtilValidate.isEmpty(GlobalConstants.CUSTOMER_ROLE_TYPE_BY_EXTERNALID.get(roleTypeId))) {
            	cifPartyId = fromPartyId;
            } else {
            	roleTypeId = GlobalConstants.CUSTOMER_ROLE_TYPE_BY_EXTERNALID.get(roleTypeId);
                isDummyCustomer = UtilValidate.isNotEmpty(fromPartyId)
                        && !DataUtil.isValidPartyIdentificationParty(delegator, fromPartyId, "CIF");
                
                if (UtilValidate.isNotEmpty(fromPartyId)) {
                    cifPartyId = DataUtil.getPartyIdentificationPartyId(delegator, fromPartyId, "CIF");
                }
            }
            
            if(UtilValidate.isNotEmpty(estimatedMilliSeconds)) {
                estimatedMilliSeconds = (double) ParamUtil.minutesToMillis(estimatedMilliSeconds);
            }
            
			if( (UtilValidate.isEmpty(endPointType) || !endPointType.equals("MSD") ) && UtilValidate.isNotEmpty(description) ) {
			    byte[] base64decodedBytes = Base64.getDecoder().decode(description);
			    description = new String(base64decodedBytes, "utf-8");
			}
            
            String apiServiceName = "createInteractiveActivity";
            
            Map<String, Object> callCtxt = FastMap.newInstance();
            Map<String, Object> callResult = FastMap.newInstance();
            
            GenericValue activity = delegator.makeValue("WorkEffort");
            
            String workEffortId = delegator.getNextSeqId("WorkEffort");
            
            //workEffortId = UtilGenerator.getIaNumber(delegator, workEffortId);
            /*if (UtilValidate.isNotEmpty(endPointType) && endPointType.equals("MSD")) {
            	workEffortId = UtilGenerator.getMsdIaNumber(delegator, workEffortId, workEffortTypeId);
            } else {
            	workEffortId = UtilGenerator.getIaNumber(delegator, workEffortId);
            }*/
            if(workEffortId!=null){
            	workEffortId = UtilGenerator.getIaNumber(delegator, workEffortId);
            }
            activity.put("workEffortId", workEffortId);
            
            activity.put("workEffortName", workEffortName);
            activity.put("workEffortTypeId", workEffortTypeId);
            activity.put("scopeEnumId", scopeEnumId);
            activity.put("workEffortServiceType", workEffortServiceType);
            activity.put("workEffortSubServiceType", workEffortSubServiceType);
            activity.put("workEffortPurposeTypeId", workEffortPurposeTypeId);
            activity.put("description", description);
            activity.put("direction", direction);
            activity.put("duration", wftMsdduration);
            activity.put("phoneNumber", phoneNumber);
            activity.put("priority", String.valueOf(priority));
            activity.put("estimatedMilliSeconds", estimatedMilliSeconds);
            activity.put("estimatedStartDate", estimatedStartDate);
            activity.put("estimatedCompletionDate", estimatedCompletionDate);
            activity.put("actualStartDate", actualStartDate);
            activity.put("actualCompletionDate", actualCompletionDate);
            activity.put("currentStatusId", currentStatusId);
            activity.put("channelId", channelId);
            activity.put("wfUserLoginId", wfUserLoginId);
            activity.put("currentSubStatusId", currentSubStatusId);
            activity.put("wfOnceDone", wfOnceDone);
            activity.put("wfNationalId", wfNationalId);
            activity.put("wfVplusId", wfVplusId);
            activity.put("externalId", externalId);
            activity.put("emplTeamId", emplTeamId);
            activity.put("businessUnitId", businessUnitId);
            activity.put("primOwnerId", primOwnerId);
            activity.put("domainEntityId", domainEntityId);
            activity.put("domainEntityType", domainEntityType);
            
            if (UtilValidate.isNotEmpty(wftMsdbusinessunit)) {
            	activity.put("businessUnitId", wftMsdbusinessunit);
            	GenericValue productStoreGroup = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", wftMsdbusinessunit).queryOne();
            	if (UtilValidate.isNotEmpty(productStoreGroup)) {
    				activity.put("businessUnitName", productStoreGroup.getString("productStoreGroupName"));
    			}
            }
            
            if (UtilValidate.isEmpty(activity.getString("externalId"))) {
                activity.put("externalId", workEffortId);
            }
            
            if (UtilValidate.isEmpty(activity.getString("currentStatusId"))) {
                activity.put("currentStatusId", "IA_OPEN");
                activity.put("currentSubStatusId", "IA_ACTIVE");
            }
            
            if (UtilValidate.isNotEmpty(activity.get("actualStartDate"))) {
                activity.put("createdDate", activity.get("actualStartDate"));
            } else if (UtilValidate.isNotEmpty(context.get("createdDate"))) {
                activity.put("createdDate", context.get("createdDate"));
            } else {
                Timestamp createdDate = UtilDateTime.nowTimestamp();
                activity.put("createdDate", createdDate);
                //activity.put("actualStartDate", createdDate);
            }
			
			if(UtilValidate.isEmpty(estimatedStartDate) && !activity.getString("currentStatusId").equals("IA_OPEN")) {
				if(UtilValidate.isNotEmpty(actualStartDate)) {
					activity.put("estimatedStartDate", actualStartDate);
				}else{
					activity.put("estimatedStartDate", UtilDateTime.nowTimestamp());
				}
            }
            
            if (UtilValidate.isEmpty(lastModifiedByUserLogin)) {
                lastModifiedByUserLogin = lastModifiedByUserLogin;
            }
            
            activity.put("createdByUserLogin", UtilValidate.isNotEmpty(wfUserLoginId) ? wfUserLoginId : userLogin.getString("userLoginId"));
            if (UtilValidate.isNotEmpty(endPointType) && endPointType.equals("MSD")) {
                apiServiceName = "createInteractiveActivityMsd";
                if (UtilValidate.isEmpty(wfVplusId)) {
                    String wftReqVplusId = DataUtil.getGlobalValue(delegator, "MSD_VPLUS_ID");
                    activity.put("wfVplusId", wftReqVplusId);
                }
            }
            if (UtilValidate.isNotEmpty(entityTimeZoneId)) {
                activity.put("entityTimeZoneId", entityTimeZoneId);
            }
            activity.put("cif", fromPartyId);
            activity.put("ownerPartyId", ownerPartyId);
            if(UtilValidate.isNotEmpty(lastModifiedDate)){
             activity.put("lastModifiedDate",  lastModifiedDate);
            }
            if (UtilValidate.isNotEmpty(currentStatusId) && "IA_MCOMPLETED".equals(currentStatusId)) {
            	activity.put("closedDateTime", UtilDateTime.nowTimestamp());
            	activity.put("closedByUserLogin", userLogin.getString("userLoginId"));
            }
            activity.create();
            
            // party assignment [start]
            
            if (UtilValidate.isNotEmpty(roleTypeId) && UtilValidate.isNotEmpty(cifPartyId)) {
                String partyId = cifPartyId;
                Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, partyId);
				String businessUnit = (String) buTeamData.get("businessUnit");
				String teamId = (String) buTeamData.get("emplTeamId");    
                callCtxt = UtilMisc.toMap("partyId", partyId, "workEffortId", workEffortId, "roleTypeId", roleTypeId, "statusId", "PRTYASGN_ASSIGNED", "userLogin", userLogin);
                callCtxt.put("assignedByUserLoginId", userLogin.getString("userLoginId"));
                callCtxt.put("ownerId", partyUserLoginId);
                callCtxt.put("emplTeamId", teamId);
				callCtxt.put("businessUnit", businessUnit);
                callResult = dispatcher.runSync("assignPartyToWorkEffort", callCtxt);
                if (ServiceUtil.isError(callResult)) {
                    result.putAll(ServiceUtil.returnError("Invalid Relative Type Id"));
                    return result;
                }
            }
            
            
            if(UtilValidate.isNotEmpty(ownerList)) {
            	for(String owner : ownerList) {
            		if (UtilValidate.isEmpty(owner)) {
            			continue;
            		}
            		String ownerPartyId1 = DataUtil.getUserLoginPartyId(delegator, owner);
            		String ownerRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, ownerPartyId1);
            		Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, ownerPartyId1);
					String businessUnit = (String) buTeamData.get("businessUnit");
					String teamId = (String) buTeamData.get("emplTeamId");    
                    ownerRoleTypeId = UtilValidate.isNotEmpty(ownerRoleTypeId) ? (String) ownerRoleTypeId : "CAL_OWNER";
	                //userLoginPartyId = ownerParty.getString("partyId");
	                callCtxt = UtilMisc.toMap("partyId", ownerPartyId1, "workEffortId", workEffortId, "roleTypeId", ownerRoleTypeId, "statusId", "PRTYASGN_ASSIGNED", "userLogin", userLogin);
	                callCtxt.put("assignedByUserLoginId", userLogin.getString("userLoginId"));
	                callCtxt.put("ownerId", owner);
	                callCtxt.put("emplTeamId", teamId);
    				callCtxt.put("businessUnit", businessUnit);
	                callResult = dispatcher.runSync("assignPartyToWorkEffort", callCtxt);
	                if (ServiceUtil.isError(callResult)) {
	                    result.putAll(ServiceUtil.returnError("Invalid Owner Party Id"));
	                    return result;
	                } else {
	                    List<EntityCondition> conditionList = FastList.newInstance();
	                    conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
	                            EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
	                            EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
	                            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ownerPartyId1),
	                            EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, ownerRoleTypeId),
	                            EntityUtil.getFilterByDateExpr()
	                            ));
	                    EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	                    GenericValue partyAssignment = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(mainConditons).filterByDate().queryFirst();
	                    if (UtilValidate.isNotEmpty(callOutCome) && UtilValidate.isNotEmpty(partyAssignment)) {
	                        partyAssignment.put("callOutCome", callOutCome);
	                        partyAssignment.store();
	                    }
	                }
                    
            	}
            }
            
            /*
            GenericValue ownerParty = org.fio.homeapps.util.DataUtil.findPartyByLogin(delegator, ownerPartyId);
            if (UtilValidate.isNotEmpty(ownerParty)) {
            	String ownerRoleTypeId = UtilValidate.isNotEmpty(context.get("ownerRoleTypeId")) ? (String) context.get("ownerRoleTypeId") : "CAL_OWNER";
                ownerPartyId = ownerParty.getString("partyId");
                callCtxt = UtilMisc.toMap("partyId", ownerPartyId, "workEffortId", workEffortId, "roleTypeId", ownerRoleTypeId, "statusId", "PRTYASGN_ASSIGNED", "userLogin", userLogin);
                callCtxt.put("assignedByUserLoginId", userLogin.getString("userLoginId"));
                callResult = dispatcher.runSync("assignPartyToWorkEffort", callCtxt);
                if (ServiceUtil.isError(callResult)) {
                    result.putAll(ServiceUtil.returnError("Invalid Owner Party Id"));
                    return result;
                } else {
                    List conditionList = FastList.newInstance();
                    conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
                            EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
                            EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
                            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ownerPartyId),
                            EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CAL_OWNER"),
                            EntityUtil.getFilterByDateExpr()
                            ));
                    EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
                    GenericValue partyAssignment = EntityUtil.getFirst( delegator.findList("WorkEffortPartyAssignment", mainConditons, null, null, null, false) );
                    if (UtilValidate.isNotEmpty(partyAssignment)) {
                        partyAssignment.put("callOutCome", callOutCome);
                        partyAssignment.store();
                    }
                }
            } */
            
            // party assignment [end]
            
            // supplementory [start]
            
            GenericValue supplementory = delegator.makeValue("WorkEffortSupplementory");
            
            supplementory.put("workEffortId", workEffortId);
            
            supplementory.put("wftLocation", wftLocation);
            supplementory.put("wftAddress", wftAddress);
            supplementory.put("wftRegardId", wftRegardId);
            supplementory.put("wftSessionId", wftSessionId);
            supplementory.put("wftNonFirstName", wftNonFirstName);
            supplementory.put("wftNonLastName", wftNonLastName);
            supplementory.put("wftMsdduration", wftMsdduration);
            supplementory.put("wftMsdpercentcomplete", wftMsdpercentcomplete);
            supplementory.put("wftMsdapplicationname", wftMsdapplicationname);
            supplementory.put("wftMsdcifid", wftMsdcifid);
            supplementory.put("wftMsdisclosedbybatch", wftMsdisclosedbybatch);
            supplementory.put("wftMsdphonenumber", wftMsdphonenumber);
            supplementory.put("wftMsdsenderrmcode", wftMsdsenderrmcode);
            supplementory.put("wftMsdsequencenumber", wftMsdsequencenumber);
            supplementory.put("wftMsdsmsuuid", wftMsdsmsuuid);
            supplementory.put("wftMsdsubjecttemplate", wftMsdsubjecttemplate);
            supplementory.put("wftMsdisbatch", wftMsdisbatch);
            supplementory.put("wftMsdbusinessunit", wftMsdbusinessunit);
            supplementory.put("wftMsdsubject", wftMsdsubject);
            supplementory.put("wftMsdcreatedon", wftMsdcreatedon);
            supplementory.put("wftMsdmodifiedon", wftMsdmodifiedon);
            supplementory.put("wftCoverpageName", wftCoverpageName);
            supplementory.put("wftCreatedConvert", wftCreatedConvert);
            supplementory.put("wftCrmeditAccessFlag", wftCrmeditAccessFlag);
            supplementory.put("trackingToken", trackingToken);
            supplementory.put("objectName", objectName);
            supplementory.put("appointmentType", appointmentType);
            supplementory.put("compression", compression);
            supplementory.put("customerCode", customerCode);
            supplementory.put("faxNumber", faxNumber);
            supplementory.put("isActivity", isActivity);
            supplementory.put("message", message);
            supplementory.put("noAttempts", noAttempts);
            supplementory.put("submittedBy", submittedBy);
            supplementory.put("msdDisplayDuration", msdDisplayDuration);
            supplementory.put("wftAlertAlertlink", wftAlertAlertlink);
            supplementory.put("wftAlertAlldayevent", wftAlertAlldayevent);
            supplementory.put("resolution", resolution);
            supplementory.put("billableTime", billableTime);
            
            supplementory.put("systemName", systemName);
            supplementory.put("sourceInvoked", SourceInvoked.API);
            
            supplementory.create();
            
            // supplementory [end]
            
            // Update escalation dates [start]
            if( (UtilValidate.isEmpty(endPointType) || !endPointType.equals("MSD")) && "IA_OPEN".equals(activity.getString("currentStatusId")) ) {
            	Timestamp dueDate = null;
            	if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
            		GenericValue custReqSupp = EntityQuery.use(delegator).from("CustRequestSupplementory").where("custRequestId",domainEntityId).queryFirst();
            		dueDate = UtilValidate.isNotEmpty(custReqSupp) ? custReqSupp.getTimestamp("commitDate") : null;
            	}
            	
				Map<String, Object> escalationContext = new LinkedHashMap<String, Object>();
				escalationContext.put("delegator", delegator);
				escalationContext.put("workEffortTypeId", activity.getString("workEffortTypeId"));
				escalationContext.put("createdDate", activity.getTimestamp("createdDate"));
				escalationContext.put("businessUnit", activity.getString("businessUnitId"));
				escalationContext.put("domainEntityType", activity.getString("domainEntityType"));
				Resolver resolver = ResolverFactory.getResolver(ResolverType.ACTIVITY_ESCALATION_RESOLVER);
				Map<String, Object> escalationResult = resolver.resolve(escalationContext);
				if (UtilValidate.isNotEmpty(escalationResult.get("commitDate"))) {
					if(UtilValidate.isNotEmpty(dueDate)) {
						supplementory.put("commitDate", !dueDate.before((Timestamp) escalationResult.get("commitDate")) ? escalationResult.get("commitDate") : dueDate);
						
						if(UtilValidate.isNotEmpty(escalationResult.get("_pre_escalation_date"))) {
							supplementory.put("preEscalationDate", !dueDate.before((Timestamp) escalationResult.get("_pre_escalation_date")) ? escalationResult.get("_pre_escalation_date") : dueDate);
						} else {
							supplementory.put("preEscalationDate", null);
						} 
						if(UtilValidate.isNotEmpty(escalationResult.get("_escalation_date_1"))) {
							supplementory.put("escalationDate1", !dueDate.before((Timestamp) escalationResult.get("_escalation_date_1")) ? escalationResult.get("_escalation_date_1") : dueDate);
						} else {
							supplementory.put("escalationDate1", null);
						}
						if(UtilValidate.isNotEmpty(escalationResult.get("_escalation_date_2"))) {
							supplementory.put("escalationDate2", !dueDate.before((Timestamp) escalationResult.get("_escalation_date_2")) ? escalationResult.get("_escalation_date_2") : dueDate);
						} else {
							supplementory.put("escalationDate2", null);
						}
						if(UtilValidate.isNotEmpty(escalationResult.get("_escalation_date_3"))) {
							supplementory.put("escalationDate3", !dueDate.before((Timestamp) escalationResult.get("_escalation_date_3")) ? escalationResult.get("_escalation_date_3") : dueDate);
						} else {
							supplementory.put("escalationDate3", null);
						}
						supplementory.store();
					}
				} else {
					supplementory.put("commitDate", null);
					supplementory.put("preEscalationDate", null);
					supplementory.put("escalationDate1", null);
					supplementory.put("escalationDate2", null);
					supplementory.put("escalationDate3", null);
					supplementory.store();
				}
			}
            // Update escalation dates [end]
            
         // prepare TAT [start]
			Timestamp closedDateTime = (Timestamp) activity.get("closedDateTime");
			if (UtilValidate.isNotEmpty(activity.getString("currentStatusId"))
					&& "IA_MCOMPLETED".equals(activity.getString("currentStatusId"))) {
				
				Debug.log("TAT ACTIVITY ID# " + activity.getString("externalId"));
				Debug.log("TAT ACTIVITY status========" + activity.getString("currentStatusId"));
				
				Map<String, Object> tatContext = new LinkedHashMap<String, Object>();
				tatContext.put("delegator", delegator);
				//tatContext.put("tatCalc", activity.getString("tatCalc"));
				tatContext.put("businessUnitId", activity.getString("businessUnitId"));
				tatContext.put("createdDate", activity.getTimestamp("createdDate"));
				
				Debug.log("closedDate before========" + closedDateTime);
				tatContext.put("closedDate", UtilValidate.isNotEmpty(closedDateTime) ? closedDateTime : UtilDateTime.nowTimestamp());
				Debug.log("closedDate after========" + tatContext.get("closedDate"));
				
				activity.put("closedDateTime", tatContext.get("closedDate"));
				int closedHour = UtilDateTime.getHour((Timestamp) tatContext.get("closedDate"),
						TimeZone.getDefault(), Locale.getDefault());
				Debug.log("closedHour========" +closedHour);
				
				Resolver resolver = ResolverFactory.getResolver(ResolverType.ACTIVITY_TAT_RESOLVER);
				Map<String, Object> tatResult = resolver.resolve(tatContext);
				
				if (ResponseUtils.isSuccess(tatResult)) {
					activity.put("tatDays", ParamUtil.getBigDecimal(tatResult, "tatDays"));
					activity.put("tatHours", ParamUtil.getBigDecimal(tatResult, "tatHours"));
					activity.put("tatMins", ParamUtil.getBigDecimal(tatResult, "tatMins"));
				}else {
					Debug.log("Error Tat calculation=="+ResponseUtils.isError(tatResult));
				}
				
				activity.store();
			}
			// prepare TAT [end]
            
            // communication event [start]
            
            if (UtilValidate.isNotEmpty(smsCustPhoneNumber)) {
                GenericValue commEvent = delegator.makeValue("CommunicationEvent");
                
                String communicationEventId = delegator.getNextSeqId("CommunicationEvent");
                
                commEvent.put("communicationEventId", communicationEventId);
                commEvent.put("communicationEventTypeId", "SMS_COMMUNICATION");
                
                commEvent.put("toData", smsCustPhoneNumber);
                //commEvent.put("smsType", smsType);
                commEvent.put("msgSendStatus", smsSendStatus);
                commEvent.put("msgErrorDesc", smsFailReason);
                commEvent.put("msgSentTime", smsSentTime);
                
                commEvent.create();
                
                GenericValue commEventWorkEff = delegator.makeValue("CommunicationEventWorkEff");
                
                commEventWorkEff.put("workEffortId", workEffortId);
                commEventWorkEff.put("communicationEventId", communicationEventId);
                commEventWorkEff.put("description", null);
                
                commEventWorkEff.create();
            }
            
            // communication event [end]
            
            // communication extension [start]
            
            DataHelper.createWorkEffortCommExtension(delegator, nsender, workEffortId, "SENDER", "sender_type", "sender", "IA_OBJ_TYPE", false);
            
            DataHelper.createWorkEffortCommExtension(delegator, nto, workEffortId, "TO", "to_type", "to", "IA_OBJ_TYPE", false);
            
            DataHelper.createWorkEffortCommExtension(delegator, ncc, workEffortId, "CC", "cc_type", "cc", "IA_OBJ_TYPE", false);
            
            DataHelper.createWorkEffortCommExtension(delegator, nbcc, workEffortId, "BCC", "bcc_type", "bcc", "IA_OBJ_TYPE", false);
            
            DataHelper.createWorkEffortCommExtension(delegator, nrecipient, workEffortId, "RECIPIENT", "recipient_type", "recipient", "IA_OBJ_TYPE", false);
            
            DataHelper.createWorkEffortCommExtension(delegator, norganizer, workEffortId, "ORGANIZER", "organizer_type", "organizer", "IA_OBJ_TYPE", false);
            
            //DataHelper.createWorkEffortCommExtension(delegator, noptional, workEffortId, "OPTIONAL", "optional_type", "optional", "IA_OBJ_TYPE", false);
            
            //DataHelper.createWorkEffortCommExtension(delegator, nrequired, workEffortId, "REQUIRED", "required_type", "required", "IA_OBJ_TYPE", false);
            
            DataHelper.createWorkEffortAttendees(delegator, noptional, workEffortId, "OPTIONAL", "WEE_REQUEST");
            
            DataHelper.createWorkEffortAttendees(delegator, nrequired, workEffortId, "REQUIRED", "WEE_REQUIRE");
            
            if (UtilValidate.isNotEmpty(objectType) && UtilValidate.isNotEmpty(objectId)) {
                GenericValue commExtension = delegator.makeValue("WorkEffortCommExtension");
                
                commExtension.put("workEffortId", workEffortId);
                commExtension.put("workExtSeqNum", new Long(1));
                
                commExtension.put("workExtName", "OBJECT_ID");
                commExtension.put("wftExtType", objectType);
                commExtension.put("wftExtValue", objectId);
                
                commExtension.create();
            }
            
            // communication extension [end]
            
            // content [start]
            
            if (UtilValidate.isNotEmpty(documents)) {
                for (Map<String, Object> document : documents) {
                    
                    String fileName = ParamUtil.getString(document, "file_name");
                    String annotationId = ParamUtil.getString(document, "annotationid");
                    
                    if (UtilValidate.isNotEmpty(fileName) || UtilValidate.isNotEmpty(annotationId)) {
                        
                        Timestamp createdDate = UtilDateTime.nowTimestamp();
                        String cntCreatedDate = ParamUtil.getString(document, "cnt_created_date");
                        if (UtilValidate.isNotEmpty(cntCreatedDate)) {
                            createdDate = ParamUtil.getDateTime(document, "cnt_created_date");
                        }
                        
                        GenericValue content = delegator.makeValue("Content");
                        
                        String contentId = delegator.getNextSeqId("Content");
                        
                        content.put("contentId", contentId);
                        content.put("contentTypeId", "DOCUMENT");
                        
                        content.put("localeString", null);
                        content.put("createdDate", createdDate);
                        content.put("createdByUserLogin", ParamUtil.getString(document, "cnt_created_by_user_login"));
                        content.put("description", ParamUtil.getString(document, "cnt_description"));
                        content.put("contentName", ParamUtil.getString(document, "file_name"));
                        content.put("documentRefNum", ParamUtil.getString(document, "document_ref_num"));
                        content.put("annotationId", ParamUtil.getString(document, "annotationid"));
                        
                        content.create();
                        
                        GenericValue wfContent = delegator.makeValue("WorkEffortContent");
                        
                        wfContent.put("workEffortId", workEffortId);
                        wfContent.put("contentId", contentId);
                        wfContent.put("workEffortContentTypeId", "PROJECT_SPEC");
                        wfContent.put("fromDate", createdDate);
                        
                        wfContent.create();
                        
                    }
                    
                }
            }
            
            // content [end]
            
            // UserLoginHistory [start]
            
            if (UtilValidate.isNotEmpty(wfUserLoginId)) {
                
                GenericValue loginParty = DataUtil.findPartyByLogin(delegator, wfUserLoginId);
                
                GenericValue loginHistory = delegator.makeValue("UserLoginHistoryCustom");
                
                loginHistory.put("userLoginHistoryCustomId", org.fio.homeapps.util.UtilGenerator.getNextSeqId());
                
                loginHistory.put("userLoginId", wfUserLoginId);
                loginHistory.put("fromDate", UtilDateTime.nowTimestamp());
                loginHistory.put("partyId", loginParty.getString("partyId"));
                
                loginHistory.put("serviceName", apiServiceName);
                
                loginHistory.create();
            }
            
            // UserLoginHistory [end]
            
            // dummy customer [start]
            
            if (isDummyCustomer) {
                GenericValue attribute = delegator.makeValue("WorkEffortAttribute");
                
                attribute.put("workEffortId", workEffortId);
                
                attribute.put("attrName", "CIF_REFERENCE");
                attribute.put("attrValue", fromPartyId);
                //attribute.put("attrLocal", ParamUtil.getString(dyna, "field_local_name"));
                
                attribute.create();
                
                /*activity.put("fromPartyId", "99999");
                activity.store();*/
                
                org.groupfio.crm.service.util.DataUtil.reAssignWorkEffortParty(dispatcher, delegator, userLogin, workEffortId, "99999", roleTypeId);
            }
            
            // dummy customer [end]
            
            // create NON CRM customer [start]
            
            if (UtilValidate.isEmpty(fromPartyId)
                    && (UtilValidate.isNotEmpty(roleTypeId))
                    ) {
                
                fromPartyId = null;
                
                if (UtilValidate.isNotEmpty(wfVplusId)) {
                    
                	  EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                              EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "VPLUS_NUMBER"),
  	                        EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, wfVplusId)
  	                        );

                    
                GenericValue attribute = EntityUtil.getFirst(delegator.findList("PartyIdentification", mainCondition, null, null, null, false));
  	                if (UtilValidate.isNotEmpty(attribute)) {
  	                    fromPartyId = attribute.getString("partyId");
  	                    if (UtilValidate.isNotEmpty(fromPartyId)) {
  	                    	org.groupfio.crm.service.util.DataUtil.reAssignWorkEffortParty(dispatcher, delegator, userLogin, workEffortId, fromPartyId, roleTypeId);
  	                    }
  	                } 
                }
                
                if (UtilValidate.isEmpty(fromPartyId)) {
                    callCtxt = FastMap.newInstance();
                    callCtxt.put("roleTypeId", roleTypeId);
                    callCtxt.put("isNonCrm", "Y");
                    callCtxt.put("vplusNumber", wfVplusId);
                    callCtxt.put("firstName", wftNonFirstName);
                    callCtxt.put("lastName", wftNonLastName);
                    callCtxt.put("nationalId", wfNationalId);
                    
                    callCtxt.put("userLogin", userLogin);
                    callResult = dispatcher.runSync("crmPortal.createCustomer", callCtxt);
                    
                    if (ServiceUtil.isSuccess(callResult)) {
                        
                        fromPartyId = ParamUtil.getString(callResult, "partyId");
                        
                        org.groupfio.crm.service.util.DataUtil.reAssignWorkEffortParty(dispatcher, delegator, userLogin, workEffortId, fromPartyId, roleTypeId);
                    }
                }
                
            }
            
            // create NON CRM customer [end]
            
            if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
            	GenericValue opportunityWorkEffort = delegator.makeValue("SalesOpportunityWorkEffort");
                
            	opportunityWorkEffort.put("salesOpportunityId", domainEntityId);
            	opportunityWorkEffort.put("workEffortId", workEffortId);
                
            	opportunityWorkEffort.create();
                
            }
            
            if (UtilValidate.isNotEmpty(domainEntityType) && (CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType) || domainEntityType.equals(DomainEntityType.CLIENT_SERVICE_REQUEST))) {
            	GenericValue custRequestWorkEffort = delegator.makeValue("CustRequestWorkEffort");
                
            	custRequestWorkEffort.put("custRequestId", domainEntityId);
            	custRequestWorkEffort.put("workEffortId", workEffortId);
                
            	custRequestWorkEffort.create();
                
            }
            
            if (UtilValidate.isNotEmpty(ownerBookedCalSlots)) {
            	Map<String, Object> bookedCalSlot = ParamUtil.toMap(JSONObject.fromObject(ownerBookedCalSlots));
            	for (String userLoginId : bookedCalSlot.keySet()) {
            		String partyId = DataUtil.getPartyIdByUserLoginId(delegator, userLoginId);
            		Map<String, Object> calSlot = (Map<String, Object>) bookedCalSlot.get(userLoginId);
            		if (UtilValidate.isNotEmpty(calSlot) && UtilValidate.isNotEmpty(partyId)) {
            			//Timestamp startTime = UtilDateTime.stringToTimeStamp(ParamUtil.getString(calSlot, "startTime"), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
            			//Timestamp endTime = UtilDateTime.stringToTimeStamp(ParamUtil.getString(calSlot, "endTime"), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
            			
            			String startTimeInput = ParamUtil.getString(calSlot, "startTime");
            			String endTimeInput = ParamUtil.getString(calSlot, "endTime");
            			
            			if (UtilValidate.isNotEmpty(wftMsdduration)) {
            				//Timestamp startTime = UtilDateTime.addHoursToTimestamp(estimatedStartDate, Integer.parseInt(wftMsdduration));
            				//Timestamp startTime = UtilDateTime.stringToTimeStamp(ParamUtil.getString(calSlot, "startTime"), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
            				
            				startTimeInput = UtilDateTime.timeStampToString(estimatedStartDate, "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
            				Timestamp endTime = UtilDateTime.addValueToTimestamp(estimatedStartDate, wftMsdduration);
            				//Timestamp endTime = UtilDateTime.addHoursToTimestamp(estimatedStartDate, Integer.parseInt(wftMsdduration));
            				Timestamp slotEndTime = UtilDateTime.stringToTimeStamp(endTimeInput, "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
            				if (endTime.after(slotEndTime)) {
            					endTime = slotEndTime;
            				}
            				
            				endTimeInput = UtilDateTime.timeStampToString(endTime, "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
            			}
            			
            			String[] startTime = startTimeInput.split(" ");
            			String[] endTime = endTimeInput.split(" ");
            			
            			callCtxt = UtilMisc.toMap("partyId", partyId);
            			callCtxt.put("reasonId", "RES_AR_SCHEDULED");
            			callCtxt.put("availabilityTypeId", "RESA_TYP_NON_AVAIL");
            			
            			callCtxt.put("fromDate_date", startTime[0]);
            			callCtxt.put("fromDate_time", startTime[1]);
            			callCtxt.put("thruDate_date", endTime[0]);
            			callCtxt.put("thruDate_time", endTime[1]);
            			
            			callCtxt.put("domainEntityType", "ACTIVITY");
            			callCtxt.put("domainEntityId", workEffortId);
            			
            			callCtxt.put("userLogin", userLogin);
            			
            			callResult = dispatcher.runSync("admin.createResAvail", callCtxt);
            		}
            	}
            }
            
            result.put("workEffortId", workEffortId);
            
        } catch (Exception e) {
            //e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        
        result.putAll(ServiceUtil.returnSuccess("Successfully created activity.."));
        
        return result;
        
    }
    
    public static Map updateInteractiveActivity(DispatchContext dctx, Map context) {
        
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String workEffortName = (String) context.get("workEffortName");
        String workEffortTypeId = (String) context.get("workEffortTypeId");
        String scopeEnumId = (String) context.get("scopeEnumId");
        String workEffortServiceType = (String) context.get("workEffortServiceType");
        String workEffortSubServiceType = (String) context.get("workEffortSubServiceType");
        String workEffortPurposeTypeId = (String) context.get("workEffortPurposeTypeId");
        Debug.log("workEffortPurposeTypeId=========="+workEffortPurposeTypeId);
        String description = (String) context.get("description");
        String direction = (String) context.get("direction");
        String phoneNumber = (String) context.get("phoneNumber");
        Long priority = (Long) context.get("priority");
        Double estimatedMilliSeconds = (Double) context.get("estimatedMilliSeconds");
        Timestamp estimatedCompletionDate = (Timestamp) context.get("estimatedCompletionDate");
        Timestamp estimatedStartDate = (Timestamp) context.get("estimatedStartDate");
        Timestamp actualStartDate = (Timestamp) context.get("actualStartDate");
        Timestamp actualCompletionDate = (Timestamp) context.get("actualCompletionDate");
        String currentStatusId = (String) context.get("currentStatusId");
        String channelId = (String) context.get("channelId");
        String wfUserLoginId = (String) context.get("wfUserLoginId");
        String currentSubStatusId = (String) context.get("currentSubStatusId");
        String wfOnceDone = (String) context.get("wfOnceDone");
        String wfNationalId = (String) context.get("wfNationalId");
        String wfVplusId = (String) context.get("wfVplusId");
        String externalId = (String) context.get("externalId");
        String wftLocation = (String) context.get("wftLocation");
        Timestamp lastModifiedDate = (Timestamp) context.get("lastModifiedDate");
        String lastModifiedByUserLogin = (String) context.get("lastModifiedByUserLogin");
        String roleTypeId = (String) context.get("roleTypeId");
        String fromPartyId = (String) context.get("partyId");
        String callOutCome = (String) context.get("callOutCome");
        //String ownerPartyId = (String) context.get("ownerPartyId");
        String emplTeamId = (String) context.get("emplTeamId");
        String primOwnerId = (String) context.get("primOwnerId");
        String businessUnitId = (String) context.get("businessUnitId");
        String trackingToken = (String) context.get("trackingToken");
        String objectName = (String) context.get("objectName");
        String appointmentType = (String) context.get("appointmentType");
        String compression = (String) context.get("compression");
        String customerCode = (String) context.get("customerCode");
        String faxNumber = (String) context.get("faxNumber");
        String isActivity = (String) context.get("isActivity");
        String message = (String) context.get("message");
        String noAttempts = (String) context.get("noAttempts");
        String submittedBy = (String) context.get("submittedBy");
        String msdDisplayDuration = (String) context.get("msdDisplayDuration");
        String wftAlertAlertlink = (String) context.get("wftAlertAlertlink");
        String wftAlertAlldayevent = (String) context.get("wftAlertAlldayevent");
        String salesOpportunityId = (String) context.get("salesOpportunityId");
        String wftNonFirstName = (String) context.get("wftNonFirstName");
        String wftNonLastName = (String) context.get("wftNonLastName");
        String wftMsdduration = (String) context.get("wftMsdduration");
        String wftMsdpercentcomplete = (String) context.get("wftMsdpercentcomplete");
        String wftMsdapplicationname = (String) context.get("wftMsdapplicationname");
        String wftMsdcifid = (String) context.get("wftMsdcifid");
        String wftMsdisclosedbybatch = (String) context.get("wftMsdisclosedbybatch");
        String wftMsdphonenumber = (String) context.get("wftMsdphonenumber");
        String wftMsdsenderrmcode = (String) context.get("wftMsdsenderrmcode");
        String wftMsdsequencenumber = (String) context.get("wftMsdsequencenumber");
        String wftMsdsmsuuid = (String) context.get("wftMsdsmsuuid");
        String wftMsdsubjecttemplate = (String) context.get("wftMsdsubjecttemplate");
        String wftMsdisbatch = (String) context.get("wftMsdisbatch");
        String wftMsdbusinessunit = (String) context.get("wftMsdbusinessunit");
        String wftMsdsubject = (String) context.get("wftMsdsubject");
        String wftMsdcreatedon = (String) context.get("wftMsdcreatedon");
        String wftMsdmodifiedon = (String) context.get("wftMsdmodifiedon");
        String wftCoverpageName = (String) context.get("wftCoverpageName");
        String wftCreatedConvert = (String) context.get("wftCreatedConvert");
        String wftCrmeditAccessFlag = (String) context.get("wftCrmeditAccessFlag");
        
        String systemName = (String) context.get("systemName");
        String endPointType = (String) context.get("endPointType");
        String domainEntityType = (String) context.get("domainEntityType");
        String domainEntityId = (String) context.get("domainEntityId");
        
        String smsCustPhoneNumber = (String) context.get("smsCustPhoneNumber");
        String smsType = (String) context.get("smsType");
        String smsSendStatus = (String) context.get("smsSendStatus");
        String smsFailReason = (String) context.get("smsFailReason");
        String smsSentTime = (String) context.get("smsSentTime");
        String isAttachment = (String) context.get("isAttachment");
        String entityTimeZoneId = (String) context.get("entityTimeZoneId");
        String ownerBookedCalSlots = (String) context.get("ownerBookedCalSlots");
        String isSchedulingRequired = (String) context.get("isSchedulingRequired");
        List<Map<String, Object>> documents = (List<Map<String, Object>>) context.get("documents");
        
        List<String> nsender = (List<String>) context.get("nsender");
        List<String> nto = (List<String>) context.get("nto");
        List<String> nbcc = (List<String>) context.get("nbcc");
        List<String> ncc = (List<String>) context.get("ncc");
        List<String> nrecipient = (List<String>) context.get("nrecipient");
        List<String> norganizer = (List<String>) context.get("norganizer");
        List<String> noptional = (List<String>) context.get("noptional");
        List<String> nrequired = (List<String>) context.get("nrequired");
        List<String> ownerList = (List<String>) context.get("ownerList");
        String objectType = (String) context.get("objectType");
        String objectId = (String) context.get("objectId");
        
        String ownerRoleTypeId = (String) context.get("ownerRoleTypeId");
        
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            
            if (UtilValidate.isEmpty(externalId)) {
                result.putAll(ServiceUtil.returnError("external_id cant be empty!"));
                return result;
            }
            
            EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.OR,
                    EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, externalId),
                    EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, externalId)
                    );
            
            GenericValue activity = EntityUtil.getFirst(delegator.findList("WorkEffort", mainCondition, null, null, null, false));
            if (UtilValidate.isEmpty(activity)) {
                result.putAll(ServiceUtil.returnError("IA not exists!"));
                return result;
            }
            
            roleTypeId = GlobalConstants.CUSTOMER_ROLE_TYPE_BY_EXTERNALID.get(roleTypeId);
            String workEffortId = activity.getString("workEffortId");
            if(!"Y".equals(isAttachment)) {
                String apiServiceName = "updateInteractiveActivity";
                if (UtilValidate.isNotEmpty(endPointType) && endPointType.equals("MSD")) {
                    apiServiceName = "updateInteractiveActivityMsd";
                }
                
                String cifPartyId = null;
                if (UtilValidate.isNotEmpty(fromPartyId)) {
                    cifPartyId = DataUtil.getPartyIdentificationPartyId(delegator, fromPartyId, "CIF");
                }
                if(UtilValidate.isNotEmpty(estimatedMilliSeconds)) {
                    estimatedMilliSeconds = (double) ParamUtil.minutesToMillis(estimatedMilliSeconds);
                }
                
                if( (UtilValidate.isEmpty(endPointType) || !endPointType.equals("MSD") ) && UtilValidate.isNotEmpty(description) ) {
                    byte[] base64decodedBytes = Base64.getDecoder().decode(description);
                    description = new String(base64decodedBytes, "utf-8");
                }
                
                Map<String, Object> callCtxt = FastMap.newInstance();
                Map<String, Object> callResult = FastMap.newInstance();
                activity.put("workEffortName", UtilValidate.isNotEmpty(workEffortName) ? workEffortName : activity.getString("workEffortName"));
                activity.put("workEffortTypeId", UtilValidate.isNotEmpty(workEffortTypeId) ? workEffortTypeId : activity.getString("workEffortTypeId"));
                activity.put("workEffortPurposeTypeId", UtilValidate.isNotEmpty(workEffortPurposeTypeId) ? workEffortPurposeTypeId : activity.getString("workEffortPurposeTypeId"));
                activity.put("scopeEnumId", UtilValidate.isNotEmpty(scopeEnumId) ? scopeEnumId : activity.getString("scopeEnumId"));
                activity.put("workEffortServiceType", UtilValidate.isNotEmpty(workEffortServiceType) ? workEffortServiceType : activity.getString("workEffortServiceType"));
                activity.put("workEffortSubServiceType", UtilValidate.isNotEmpty(workEffortSubServiceType) ? workEffortSubServiceType : activity.getString("workEffortSubServiceType"));
                activity.put("description", UtilValidate.isNotEmpty(description) ? description : activity.getString("description"));
                activity.put("direction", UtilValidate.isNotEmpty(direction) ? direction  : activity.getString("direction") );
                activity.put("phoneNumber", UtilValidate.isNotEmpty(phoneNumber) ? phoneNumber : activity.getString("phoneNumber"));
                activity.put("priority", UtilValidate.isNotEmpty(priority) ? String.valueOf(priority) : activity.getString("priority"));
                activity.put("estimatedMilliSeconds", UtilValidate.isNotEmpty(estimatedMilliSeconds) ? estimatedMilliSeconds : activity.getDouble("estimatedMilliSeconds"));
                activity.put("estimatedCompletionDate", UtilValidate.isNotEmpty(estimatedCompletionDate) ? estimatedCompletionDate : null);
                activity.put("estimatedStartDate", UtilValidate.isNotEmpty(estimatedStartDate) ? estimatedStartDate : null);
                activity.put("actualStartDate", UtilValidate.isNotEmpty(actualStartDate) ? actualStartDate : null);
                activity.put("actualCompletionDate", UtilValidate.isNotEmpty(actualCompletionDate) ? actualCompletionDate : null);
                activity.put("channelId", UtilValidate.isNotEmpty(channelId) ? channelId  : activity.getString("channelId"));
                activity.put("wfUserLoginId", UtilValidate.isNotEmpty(wfUserLoginId) ? wfUserLoginId : activity.getString("wfUserLoginId"));
                activity.put("wfOnceDone", UtilValidate.isNotEmpty(wfOnceDone) ? wfOnceDone : activity.getString("wfOnceDone"));
                activity.put("wfNationalId", UtilValidate.isNotEmpty(wfNationalId) ? wfNationalId : activity.getString("wfNationalId"));
                activity.put("wfVplusId", UtilValidate.isNotEmpty(wfVplusId) ? wfVplusId : activity.getString("wfVplusId"));
                //activity.put("externalId", UtilValidate.isNotEmpty(externalId) ? externalId : activity.getString("externalId"));
                activity.put("createdDate", UtilValidate.isNotEmpty(context.get("createdDate")) ? context.get("createdDate") : activity.get("createdDate"));
                activity.put("lastModifiedDate", UtilValidate.isNotEmpty(lastModifiedDate) ? lastModifiedDate : activity.get("createdDate"));
                if (UtilValidate.isNotEmpty(entityTimeZoneId)) {
                    activity.put("entityTimeZoneId", entityTimeZoneId);
                }
                if (UtilValidate.isNotEmpty(businessUnitId)) {
                    activity.put("businessUnitId", businessUnitId);
                }
                if (UtilValidate.isNotEmpty(wftMsdduration)) {
                    activity.put("duration", wftMsdduration);
                }
                if (UtilValidate.isNotEmpty(currentStatusId)) {
                    activity.put("currentStatusId", currentStatusId);
                }else {
                    activity.put("currentStatusId", UtilValidate.isNotEmpty(currentStatusId) ? currentStatusId : activity.getString("currentStatusId"));
                }
                
                if (UtilValidate.isNotEmpty(activity.get("currentSubStatusId")) && UtilValidate.isNotEmpty(currentSubStatusId)) {
                    activity.put("currentSubStatusId", currentSubStatusId);
                }else {
                    activity.put("currentSubStatusId", UtilValidate.isNotEmpty(currentSubStatusId) ? currentSubStatusId : activity.getString("currentSubStatusId"));
                }
                if (UtilValidate.isEmpty(lastModifiedByUserLogin)) {
                    lastModifiedByUserLogin = UtilValidate.isNotEmpty(wfUserLoginId) ? wfUserLoginId : userLogin.getString("userLoginId");
                }
                
                activity.put("lastModifiedByUserLogin", UtilValidate.isNotEmpty(wfUserLoginId) ? wfUserLoginId : userLogin.getString("userLoginId"));
                activity.put("lastModifiedDate", UtilDateTime.nowTimestamp());
                activity.put("primOwnerId", primOwnerId);
                activity.put("emplTeamId", emplTeamId);
                activity.put("cif", fromPartyId);
                //activity.put("ownerPartyId", ownerPartyId);
                if (UtilValidate.isNotEmpty(currentStatusId) && "IA_MCOMPLETED".equals(currentStatusId)) {
                	activity.put("closedDateTime", UtilDateTime.nowTimestamp());
                	activity.put("closedByUserLogin", userLogin.getString("userLoginId"));
                }
                
                if (UtilValidate.isNotEmpty(isSchedulingRequired) && isSchedulingRequired.equals("N")) {
                	activity.put("duration", null);
                }
                
                activity.store();
                
                // party assignment [start]
                
                if (UtilValidate.isNotEmpty(roleTypeId) && UtilValidate.isNotEmpty(cifPartyId)) {
                    String partyId = cifPartyId;
                    org.groupfio.crm.service.util.DataUtil.reAssignWorkEffortParty(dispatcher, delegator, userLogin, workEffortId, partyId, roleTypeId);
                }
                if(UtilValidate.isNotEmpty(ownerList)) {
                	org.groupfio.crm.service.util.DataUtil.reAssignOwnerWorkEffortParty(dispatcher, delegator, userLogin, workEffortId, ownerList);
                }
                /*
                GenericValue ownerParty = org.fio.homeapps.util.DataUtil.findPartyByLogin(delegator, ownerPartyId);
                if (UtilValidate.isNotEmpty(ownerParty)) {
                    ownerPartyId = ownerParty.getString("partyId");
                    org.groupfio.crm.service.util.DataUtil.reAssignOwnerWorkEffortParty(dispatcher, delegator, userLogin, workEffortId, ownerPartyId, ownerRoleTypeId);
                    
                } else {
                    ownerPartyId = DataUtil.getUserLoginPartyId(delegator, primOwnerId);
                    org.groupfio.crm.service.util.DataUtil.reAssignOwnerWorkEffortParty(dispatcher, delegator, userLogin, workEffortId, ownerPartyId, ownerRoleTypeId);    
                }
                */
                
                // party assignment [end]
                
                // supplementory [start]
                
                GenericValue supplementory = EntityUtil.getFirst( delegator.findByAnd("WorkEffortSupplementory", UtilMisc.toMap("workEffortId", workEffortId), null, false) );
                
                if (UtilValidate.isNotEmpty(supplementory)) {
                    supplementory.put("wftNonFirstName", UtilValidate.isEmpty(wftNonFirstName) ? wftNonFirstName : supplementory.getString("wftNonFirstName"));
                    supplementory.put("wftNonLastName", UtilValidate.isNotEmpty(wftNonLastName) ? wftNonLastName : supplementory.getString("wftNonFirstName"));
                    supplementory.put("wftMsdduration", UtilValidate.isNotEmpty(wftMsdduration) ? wftMsdduration : supplementory.getString("wftMsdduration"));
                    supplementory.put("wftLocation", UtilValidate.isNotEmpty(wftLocation) ? wftLocation : supplementory.getString("wftLocation"));
                    supplementory.put("wftMsdpercentcomplete", UtilValidate.isNotEmpty(wftMsdpercentcomplete) ? wftMsdpercentcomplete : supplementory.getString("wftMsdpercentcomplete"));
                    supplementory.put("wftMsdapplicationname", UtilValidate.isNotEmpty(wftMsdapplicationname) ? wftMsdapplicationname : supplementory.getString("wftMsdapplicationname"));
                    supplementory.put("wftMsdcifid", UtilValidate.isNotEmpty(wftMsdcifid) ? wftMsdcifid : supplementory.getString("wftMsdcifid"));
                    supplementory.put("wftMsdisclosedbybatch", UtilValidate.isNotEmpty(wftMsdisclosedbybatch) ? wftMsdisclosedbybatch : supplementory.getString("wftMsdisclosedbybatch"));
                    supplementory.put("wftMsdphonenumber", UtilValidate.isNotEmpty(wftMsdphonenumber) ? wftMsdphonenumber : supplementory.getString("wftMsdphonenumber"));
                    supplementory.put("wftMsdsenderrmcode", UtilValidate.isNotEmpty(wftMsdsenderrmcode) ? wftMsdsenderrmcode : supplementory.getString("wftMsdsenderrmcode"));
                    supplementory.put("wftMsdsequencenumber", UtilValidate.isNotEmpty(wftMsdsequencenumber) ? wftMsdsequencenumber : supplementory.getString("wftMsdsequencenumber"));
                    supplementory.put("wftMsdsmsuuid", UtilValidate.isNotEmpty(wftMsdsmsuuid) ? wftMsdsmsuuid : supplementory.getString("wftMsdsmsuuid"));
                    supplementory.put("wftMsdsubjecttemplate", UtilValidate.isNotEmpty(wftMsdsubjecttemplate) ? wftMsdsubjecttemplate : supplementory.getString("wftMsdsubjecttemplate"));
                    supplementory.put("wftMsdisbatch", UtilValidate.isNotEmpty(wftMsdisbatch) ? wftMsdisbatch : supplementory.getString("wftMsdisbatch"));
                    supplementory.put("wftMsdbusinessunit", UtilValidate.isNotEmpty(wftMsdbusinessunit) ? wftMsdbusinessunit : supplementory.getString("wftMsdbusinessunit"));
                    supplementory.put("wftMsdsubject", UtilValidate.isNotEmpty(wftMsdsubject) ? wftMsdsubject : supplementory.getString("wftMsdsubject"));
                    supplementory.put("wftMsdcreatedon", UtilValidate.isNotEmpty(wftMsdcreatedon) ? wftMsdcreatedon : supplementory.getString("wftMsdcreatedon"));
                    supplementory.put("wftMsdmodifiedon", UtilValidate.isNotEmpty(wftMsdmodifiedon) ? wftMsdmodifiedon : supplementory.getString("wftMsdmodifiedon"));
                    supplementory.put("wftCoverpageName", UtilValidate.isNotEmpty(wftCoverpageName) ? wftCoverpageName : supplementory.getString("wftCoverpageName"));
                    supplementory.put("wftCreatedConvert", UtilValidate.isNotEmpty(wftCreatedConvert) ? wftCreatedConvert : supplementory.getString("wftCreatedConvert"));
                    supplementory.put("wftCrmeditAccessFlag", UtilValidate.isNotEmpty(wftCrmeditAccessFlag) ? wftCrmeditAccessFlag : supplementory.getString("wftCrmeditAccessFlag"));
                    
                    supplementory.put("trackingToken", UtilValidate.isNotEmpty(trackingToken) ? trackingToken : supplementory.getString("trackingToken"));
                    supplementory.put("objectName", UtilValidate.isNotEmpty(objectName) ? objectName : supplementory.getString("objectName"));
                    supplementory.put("appointmentType", UtilValidate.isNotEmpty(appointmentType) ? appointmentType : supplementory.getString("appointmentType"));
                    supplementory.put("compression", UtilValidate.isNotEmpty(compression) ? compression : supplementory.getString("compression"));
                    supplementory.put("customerCode", UtilValidate.isNotEmpty(customerCode) ? customerCode : supplementory.getString("customerCode"));
                    supplementory.put("faxNumber", UtilValidate.isNotEmpty(faxNumber) ? faxNumber : supplementory.getString("faxNumber"));
                    supplementory.put("isActivity", UtilValidate.isNotEmpty(isActivity) ? isActivity : supplementory.getString("isActivity"));
                    supplementory.put("message", UtilValidate.isNotEmpty(message) ? message : supplementory.getString("message"));
                    supplementory.put("noAttempts", UtilValidate.isNotEmpty(noAttempts) ? noAttempts : supplementory.getString("noAttempts"));
                    supplementory.put("submittedBy", UtilValidate.isNotEmpty(submittedBy) ? submittedBy : supplementory.getString("submittedBy"));
                    supplementory.put("msdDisplayDuration", UtilValidate.isNotEmpty(msdDisplayDuration) ? msdDisplayDuration : supplementory.getString("msdDisplayDuration"));
                    supplementory.put("wftAlertAlertlink", UtilValidate.isNotEmpty(wftAlertAlertlink) ? wftAlertAlertlink : supplementory.getString("wftAlertAlertlink"));
                    supplementory.put("wftAlertAlldayevent", UtilValidate.isNotEmpty(wftAlertAlldayevent) ? wftAlertAlldayevent : supplementory.getString("wftAlertAlldayevent"));
                    
                    if (UtilValidate.isNotEmpty(isSchedulingRequired) && isSchedulingRequired.equals("N")) {
                    	supplementory.put("wftMsdduration", null);
                    }
                    
                    supplementory.put("systemName", systemName);
                    
                    supplementory.store();
                }
                
                // supplementory [end]
                
                if( (UtilValidate.isEmpty(endPointType) || !endPointType.equals("MSD")) && "IA_OPEN".equals(activity.getString("currentStatusId")) ) {
                	Timestamp dueDate = null;
                	if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
                		GenericValue custReqSupp = EntityQuery.use(delegator).from("CustRequestSupplementory").where("custRequestId",domainEntityId).queryFirst();
                		dueDate = UtilValidate.isNotEmpty(custReqSupp) ? custReqSupp.getTimestamp("commitDate") : null;
                	}
                	
    				Map<String, Object> escalationContext = new LinkedHashMap<String, Object>();
    				escalationContext.put("delegator", delegator);
    				escalationContext.put("workEffortTypeId", activity.getString("workEffortTypeId"));
    				escalationContext.put("createdDate", activity.getTimestamp("createdDate"));
    				escalationContext.put("businessUnit", activity.getString("businessUnitId"));
    				escalationContext.put("domainEntityType", activity.getString("domainEntityType"));
    				Resolver resolver = ResolverFactory.getResolver(ResolverType.ACTIVITY_ESCALATION_RESOLVER);
    				Map<String, Object> escalationResult = resolver.resolve(escalationContext);
    				if (UtilValidate.isNotEmpty(escalationResult.get("commitDate"))) {
    					if(UtilValidate.isNotEmpty(dueDate)) {
    						supplementory.put("commitDate", !dueDate.before((Timestamp) escalationResult.get("commitDate")) ? escalationResult.get("commitDate") : dueDate);
    						
    						if(UtilValidate.isNotEmpty(escalationResult.get("_pre_escalation_date"))) {
    							supplementory.put("preEscalationDate", !dueDate.before((Timestamp) escalationResult.get("_pre_escalation_date")) ? escalationResult.get("_pre_escalation_date") : dueDate);
    						} else {
    							supplementory.put("preEscalationDate", null);
    						} 
    						if(UtilValidate.isNotEmpty(escalationResult.get("_escalation_date_1"))) {
    							supplementory.put("escalationDate1", !dueDate.before((Timestamp) escalationResult.get("_escalation_date_1")) ? escalationResult.get("_escalation_date_1") : dueDate);
    						} else {
    							supplementory.put("escalationDate1", null);
    						}
    						if(UtilValidate.isNotEmpty(escalationResult.get("_escalation_date_2"))) {
    							supplementory.put("escalationDate2", !dueDate.before((Timestamp) escalationResult.get("_escalation_date_2")) ? escalationResult.get("_escalation_date_2") : dueDate);
    						} else {
    							supplementory.put("escalationDate2", null);
    						}
    						if(UtilValidate.isNotEmpty(escalationResult.get("_escalation_date_3"))) {
    							supplementory.put("escalationDate3", !dueDate.before((Timestamp) escalationResult.get("_escalation_date_3")) ? escalationResult.get("_escalation_date_3") : dueDate);
    						} else {
    							supplementory.put("escalationDate3", null);
    						}
    						supplementory.store();
    					}
    				} else {
    					supplementory.put("commitDate", null);
    					supplementory.put("preEscalationDate", null);
    					supplementory.put("escalationDate1", null);
    					supplementory.put("escalationDate2", null);
    					supplementory.put("escalationDate3", null);
    					supplementory.store();
    				}
    			}
                
                // prepare TAT [start]
    			Timestamp closedDateTime = (Timestamp) activity.get("closedDateTime");
    			if (UtilValidate.isNotEmpty(activity.getString("currentStatusId"))
    					&& "IA_MCOMPLETED".equals(activity.getString("currentStatusId"))) {
    				
    				Debug.log("TAT ACTIVITY ID# " + activity.getString("externalId"));
    				Debug.log("TAT ACTIVITY status========" + activity.getString("currentStatusId"));
    				
    				Map<String, Object> tatContext = new LinkedHashMap<String, Object>();
    				tatContext.put("delegator", delegator);
    				//tatContext.put("tatCalc", activity.getString("tatCalc"));
    				tatContext.put("businessUnitId", activity.getString("businessUnitId"));
    				tatContext.put("createdDate", activity.getTimestamp("createdDate"));
    				
    				Debug.log("closedDate before========" + closedDateTime);
    				tatContext.put("closedDate", UtilValidate.isNotEmpty(closedDateTime) ? closedDateTime : UtilDateTime.nowTimestamp());
    				Debug.log("closedDate after========" + tatContext.get("closedDate"));
    				
    				activity.put("closedDateTime", tatContext.get("closedDate"));
    				int closedHour = UtilDateTime.getHour((Timestamp) tatContext.get("closedDate"),
    						TimeZone.getDefault(), Locale.getDefault());
    				Debug.log("closedHour========" +closedHour);
    				
    				Resolver resolver = ResolverFactory.getResolver(ResolverType.ACTIVITY_TAT_RESOLVER);
    				Map<String, Object> tatResult = resolver.resolve(tatContext);
    				
    				if (ResponseUtils.isSuccess(tatResult)) {
    					activity.put("tatDays", ParamUtil.getBigDecimal(tatResult, "tatDays"));
    					activity.put("tatHours", ParamUtil.getBigDecimal(tatResult, "tatHours"));
    					activity.put("tatMins", ParamUtil.getBigDecimal(tatResult, "tatMins"));
    				}else {
    					Debug.log("Error Tat calculation=="+ResponseUtils.isError(tatResult));
    				}
    				
    				activity.store();
    			}
    			// prepare TAT [end]
                
                // communication event [start]
                
                GenericValue commEventWorkEff = EntityUtil.getFirst( delegator.findByAnd("CommunicationEventWorkEff", UtilMisc.toMap("workEffortId", workEffortId), null, false) );
                
                if (UtilValidate.isNotEmpty(commEventWorkEff)) {
                    
                    String communicationEventId = commEventWorkEff.getString("communicationEventId");
                    GenericValue commEvent = EntityUtil.getFirst( delegator.findByAnd("CommunicationEvent", UtilMisc.toMap("communicationEventId", communicationEventId), null, false) );
                    if (UtilValidate.isNotEmpty(commEvent)) {
                        commEvent.put("toData", smsCustPhoneNumber);
                        //commEvent.put("smsType", smsType);
                        commEvent.put("msgSendStatus", smsSendStatus);
                        commEvent.put("msgErrorDesc", smsFailReason);
                        commEvent.put("msgSentTime", smsSentTime);
                        
                        commEvent.store();
                    }
                    
                }
                
                // communication event [end]
                
                // communication extension [start]
                
                DataHelper.createWorkEffortCommExtension(delegator, nsender, workEffortId, "SENDER", "sender_type", "sender", "IA_OBJ_TYPE", true);
                
                DataHelper.createWorkEffortCommExtension(delegator, nto, workEffortId, "TO", "to_type", "to", "IA_OBJ_TYPE", true);
                
                DataHelper.createWorkEffortCommExtension(delegator, ncc, workEffortId, "CC", "cc_type", "cc", "IA_OBJ_TYPE", true);
                
                DataHelper.createWorkEffortCommExtension(delegator, nbcc, workEffortId, "BCC", "bcc_type", "bcc", "IA_OBJ_TYPE", true);
                
                DataHelper.createWorkEffortCommExtension(delegator, nrecipient, workEffortId, "RECIPIENT", "recipient_type", "recipient", "IA_OBJ_TYPE", true);
                
                DataHelper.createWorkEffortCommExtension(delegator, norganizer, workEffortId, "ORGANIZER", "organizer_type", "organizer", "IA_OBJ_TYPE", true);
                
               // DataHelper.createWorkEffortCommExtension(delegator, noptional, workEffortId, "OPTIONAL", "optional_type", "optional", "IA_OBJ_TYPE", true);
                
               // DataHelper.createWorkEffortCommExtension(delegator, nrequired, workEffortId, "REQUIRED", "required_type", "required", "IA_OBJ_TYPE", true);
                DataHelper.updateWorkEffortAttendees(delegator, noptional, workEffortId, "OPTIONAL", "WEE_REQUEST");
                
                DataHelper.updateWorkEffortAttendees(delegator, nrequired, workEffortId, "REQUIRED", "WEE_REQUIRE");             
                if (UtilValidate.isNotEmpty(objectType) && UtilValidate.isNotEmpty(objectId)) {
                    
                    GenericValue commExtension = EntityUtil.getFirst( delegator.findByAnd("WorkEffortCommExtension", UtilMisc.toMap("workEffortId", workEffortId, "workExtName", "OBJECT_ID"), null, false) );
                    
                    if (UtilValidate.isEmpty(commExtension)) {
                        commExtension = delegator.makeValue("WorkEffortCommExtension");
                        
                        commExtension.put("workEffortId", workEffortId);
                        commExtension.put("workExtSeqNum", new Long(1));
                        
                        commExtension.put("workExtName", "OBJECT_ID");
                        commExtension.put("wftExtType", objectType);
                        commExtension.put("wftExtValue", objectId);
                        
                        commExtension.create();
                    }
                    
                }
                
                // communication extension [end]
                
                // UserLoginHistory [start]
                
                if (UtilValidate.isNotEmpty(wfUserLoginId)) {
                    
                    GenericValue loginParty = DataUtil.findPartyByLogin(delegator, wfUserLoginId);
                    
                    GenericValue loginHistory = delegator.makeValue("UserLoginHistoryCustom");
                    
                    loginHistory.put("userLoginHistoryCustomId", org.fio.homeapps.util.UtilGenerator.getNextSeqId());
                    
                    loginHistory.put("userLoginId", wfUserLoginId);
                    loginHistory.put("fromDate", UtilDateTime.nowTimestamp());
                    loginHistory.put("partyId", loginParty.getString("partyId"));
                    
                    loginHistory.put("serviceName", apiServiceName);
                    
                    loginHistory.create();
                }
                
                // UserLoginHistory [end]
            }
            
            // content [start]
            if (UtilValidate.isNotEmpty(documents)) {
                for (Map<String, Object> document : documents) {
                    
                    String documentRefNum = ParamUtil.getString(document, "document_ref_num");
                    String annotationid = ParamUtil.getString(document, "annotationid");
                    
                    if (UtilValidate.isNotEmpty(documentRefNum) || UtilValidate.isNotEmpty(annotationid)) {
                        
                        mainCondition = EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId);
                        
                        if (UtilValidate.isNotEmpty(endPointType) && endPointType.equals("MSD")) {
                        	mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                        			mainCondition,
                                    EntityCondition.makeCondition("annotationId", EntityOperator.EQUALS, annotationid)
                                );
                        } else {
                        	mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                        			mainCondition,
                                    EntityCondition.makeCondition("documentRefNum", EntityOperator.EQUALS, documentRefNum)
                                );
                        }
                        
                        GenericValue contentSummary = EntityUtil.getFirst(delegator.findList("WorkEffortContentSummary", mainCondition, null, null, null, false));
                        
                        if (UtilValidate.isNotEmpty(contentSummary)
                        		&& (UtilValidate.isEmpty(endPointType) || !endPointType.equals("MSD"))
                        		) {
                            Debug.logError("Document already exists! documentRefNum: "+documentRefNum, MODULE);
                            continue;
                        }
                        
                        if (UtilValidate.isNotEmpty(contentSummary) && UtilValidate.isNotEmpty(endPointType) && endPointType.equals("MSD")) {
							
							GenericValue content = EntityUtil.getFirst( delegator.findByAnd("Content", UtilMisc.toMap("contentId", contentSummary.getString("contentId")), null, false) );
							if (UtilValidate.isNotEmpty(content)) {
								content.put("createdByUserLogin", ParamUtil.getString(document, "cnt_created_by_user_login"));
		                        content.put("description", ParamUtil.getString(document, "cnt_description"));
		                        content.put("contentName", ParamUtil.getString(document, "file_name"));
								
								content.store();
							}
							
						} else {
							
							Timestamp createdDate = UtilDateTime.nowTimestamp();
	                        String cntCreatedDate = ParamUtil.getString(document, "cnt_created_date");
	                        
	                        if (UtilValidate.isNotEmpty(cntCreatedDate)) {
	                            createdDate = ParamUtil.getDateTime(document, "cnt_created_date");
	                        }
	                        
	                        GenericValue content = delegator.makeValue("Content");
	                        
	                        String contentId = delegator.getNextSeqId("Content");
	                        
	                        content.put("contentId", contentId);
	                        content.put("contentTypeId", "DOCUMENT");
	                        
	                        content.put("localeString", null);
	                        content.put("createdDate", createdDate);
	                        content.put("createdByUserLogin", ParamUtil.getString(document, "cnt_created_by_user_login"));
	                        content.put("description", ParamUtil.getString(document, "cnt_description"));
	                        content.put("contentName", ParamUtil.getString(document, "file_name"));
	                        content.put("documentRefNum", ParamUtil.getString(document, "document_ref_num"));
	                        
	                        content.create();
	                        
	                        GenericValue crContent = delegator.makeValue("WorkEffortContent");
	                        
	                        crContent.put("workEffortId", workEffortId);
	                        crContent.put("contentId", contentId);
	                        crContent.put("workEffortContentTypeId", "PROJECT_SPEC");
	                        crContent.put("fromDate", createdDate);
	                        
	                        crContent.create();
						}
                        
                    }
                    
                }
            }
            // content [end]
            
            if (UtilValidate.isNotEmpty(ownerBookedCalSlots)) {
            	// empty calendar book slots
            	
            	List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
            	conditionsList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "ACTIVITY"));
            	conditionsList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, workEffortId));
            	conditionsList.add(EntityCondition.makeCondition("startDate", EntityOperator.EQUALS, null));

            	EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            	delegator.removeByCondition("ResourceAvailability", mainConditons);
            	
            	//delegator.removeByAnd("ResourceAvailability", UtilMisc.toMap("domainEntityType", "ACTIVITY", "domainEntityId", workEffortId));
            	
            	Map<String, Object> bookedCalSlot = ParamUtil.toMap(JSONObject.fromObject(ownerBookedCalSlots));
            	for (String userLoginId : bookedCalSlot.keySet()) {
            		String partyId = DataUtil.getPartyIdByUserLoginId(delegator, userLoginId);
            		Map<String, Object> calSlot = (Map<String, Object>) bookedCalSlot.get(userLoginId);
            		if (UtilValidate.isNotEmpty(calSlot) && UtilValidate.isNotEmpty(partyId)) {
            			//Timestamp startTime = UtilDateTime.stringToTimeStamp(ParamUtil.getString(calSlot, "startTime"), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
            			//Timestamp endTime = UtilDateTime.stringToTimeStamp(ParamUtil.getString(calSlot, "endTime"), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
            			
            			conditionsList = new ArrayList<EntityCondition>();
        				
                    	conditionsList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "ACTIVITY"));
                    	conditionsList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, workEffortId));
                    	conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
                    	conditionsList.add(EntityCondition.makeCondition("startDate", EntityOperator.NOT_EQUAL, null));

                    	mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            			long count = delegator.findCountByCondition("ResourceAvailability", mainConditons, null, null);
            			if (count > 0) {
            				System.out.println("technician already started his activity, partyId# "+partyId);
            				continue;
            			}
            			
            			String startTimeInput = ParamUtil.getString(calSlot, "startTime");
            			String endTimeInput = ParamUtil.getString(calSlot, "endTime");
            			
            			if (UtilValidate.isNotEmpty(wftMsdduration)) {
            				//Timestamp startTime = UtilDateTime.addHoursToTimestamp(estimatedStartDate, Integer.parseInt(wftMsdduration));
            				//Timestamp startTime = UtilDateTime.stringToTimeStamp(ParamUtil.getString(calSlot, "startTime"), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
            				
            				startTimeInput = UtilDateTime.timeStampToString(estimatedStartDate, "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
            				Timestamp endTime = UtilDateTime.addValueToTimestamp(estimatedStartDate, wftMsdduration);
            				Timestamp slotEndTime = UtilDateTime.stringToTimeStamp(endTimeInput, "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
            				if (endTime.after(slotEndTime)) {
            					endTime = slotEndTime;
            				}
            				
            				endTimeInput = UtilDateTime.timeStampToString(endTime, "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault());
            			}
            			
            			String[] startTime = startTimeInput.split(" ");
            			String[] endTime = endTimeInput.split(" ");
            			
            			Map callCtxt = UtilMisc.toMap("partyId", partyId);
            			callCtxt.put("reasonId", "RES_AR_SCHEDULED");
            			callCtxt.put("availabilityTypeId", "RESA_TYP_NON_AVAIL");
            			
            			callCtxt.put("fromDate_date", startTime[0]);
            			callCtxt.put("fromDate_time", startTime[1]);
            			callCtxt.put("thruDate_date", endTime[0]);
            			callCtxt.put("thruDate_time", endTime[1]);
            			
            			callCtxt.put("domainEntityType", "ACTIVITY");
            			callCtxt.put("domainEntityId", workEffortId);
            			
            			callCtxt.put("userLogin", userLogin);
            			
            			Map callResult = dispatcher.runSync("admin.createResAvail", callCtxt);
            		}
            	}
            }
            
            result.put("workEffortId", workEffortId);
        } catch (Exception e) {
        	e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        result.putAll(ServiceUtil.returnSuccess("Successfully UPDATED IA.."));
        return result;
    }
    
    public static Map detailInteractiveActivity(DispatchContext dctx, Map context) {
        
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String workEffortId = (String) context.get("workEffortId");
        String externalId = (String) context.get("externalId");
        
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            
            if (UtilValidate.isEmpty(workEffortId) && UtilValidate.isEmpty(externalId)) {
                result.putAll(ServiceUtil.returnError("IA ID cant be empty!"));
                return result;
            }
            
            List conditionList = FastList.newInstance();
            
            if (UtilValidate.isNotEmpty(workEffortId)) {
                conditionList.add( EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId) );
            }
            
            if (UtilValidate.isNotEmpty(externalId)) {
                conditionList.add( EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, externalId) );
            }
            
            EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            GenericValue activity = EntityUtil.getFirst( delegator.findList("WorkEffort", mainConditons, null, null, null, false) );
            if (UtilValidate.isEmpty(activity)) {
                result.putAll(ServiceUtil.returnError("IA not exists!"));
                return result;
            }
            
            workEffortId = activity.getString("workEffortId");
            
            Map<String, Object> callCtxt = FastMap.newInstance();
            Map<String, Object> callResult = FastMap.newInstance();
            
            result.put("activity", activity);
            
            conditionList = FastList.newInstance();
            
            conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
                    EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
                    EntityCondition.makeCondition(EntityOperator.OR,
	                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CAL_OWNER")
                    ),
                    EntityUtil.getFilterByDateExpr()
	                ));
            
            mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            GenericValue partyAssignment = EntityUtil.getFirst( delegator.findList("WorkEffortPartyAssignment", mainConditons, null, null, null, false) );
            if (UtilValidate.isNotEmpty(partyAssignment)) {
                //result.put("partyId", partyAssignment.getString("partyId"));
                //result.put("roleTypeId", partyAssignment.getString("roleTypeId"));
                result.put("callOutCome", partyAssignment.getString("callOutCome"));
            }
            
            conditionList = FastList.newInstance();
            conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
                    EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
                    
                    //EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, fromPartyId),
                    
                    EntityCondition.makeCondition(EntityOperator.OR,
                            EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER"),
                            EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CARD_CUST")
                            ),
                    
                    EntityUtil.getFilterByDateExpr()
	                ));
            
            mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            partyAssignment = EntityUtil.getFirst( delegator.findList("WorkEffortPartyAssignment", mainConditons, null, null, null, false) );
            if (UtilValidate.isNotEmpty(partyAssignment)) {
                result.put("partyId", partyAssignment.getString("partyId"));
                result.put("roleTypeId", partyAssignment.getString("roleTypeId"));
            }
            
            // supplementory [start]
            
            GenericValue supplementory = EntityUtil.getFirst( delegator.findByAnd("WorkEffortSupplementory", UtilMisc.toMap("workEffortId", workEffortId), null, false) );
            
            result.put("systemName", supplementory.getString("systemName"));
            
            result.put("supplementory", supplementory);
            
            // supplementory [end]
            
            // communication event [start]
            
            GenericValue commEventWorkEff = EntityUtil.getFirst( delegator.findByAnd("CommunicationEventWorkEff", UtilMisc.toMap("workEffortId", workEffortId), null, false) );
            
            if (UtilValidate.isNotEmpty(commEventWorkEff)) {
                
                String communicationEventId = commEventWorkEff.getString("communicationEventId");
                GenericValue commEvent = EntityUtil.getFirst( delegator.findByAnd("CommunicationEvent", UtilMisc.toMap("communicationEventId", communicationEventId), null, false) );
                result.put("smsCommEvent", commEvent);
                
            }
            
            // communication event [end]
            
            // communication extension [start]
            
            result.put("nsender", DataHelper.loadWorkEffortCommExtension(delegator, workEffortId, "SENDER", "sender_type", "sender", "IA_OBJ_TYPE"));
            
            result.put("nto", DataHelper.loadWorkEffortCommExtension(delegator, workEffortId, "TO", "to_type", "to", "IA_OBJ_TYPE"));
            
            result.put("ncc", DataHelper.loadWorkEffortCommExtension(delegator, workEffortId, "CC", "cc_type", "cc", "IA_OBJ_TYPE"));
            
            result.put("nbcc", DataHelper.loadWorkEffortCommExtension(delegator, workEffortId, "BCC", "bcc_type", "bcc", "IA_OBJ_TYPE"));
            
            result.put("nrecipient", DataHelper.loadWorkEffortCommExtension(delegator, workEffortId, "RECIPIENT", "recipient_type", "recipient", "IA_OBJ_TYPE"));
            
            result.put("norganizer", DataHelper.loadWorkEffortCommExtension(delegator, workEffortId, "ORGANIZER", "organizer_type", "organizer", "IA_OBJ_TYPE"));
            
            result.put("noptional", DataHelper.loadWorkEffortCommExtension(delegator, workEffortId, "OPTIONAL", "optional_type", "optional", "IA_OBJ_TYPE"));
            
            result.put("nrequired", DataHelper.loadWorkEffortCommExtension(delegator, workEffortId, "REQUIRED", "required_type", "required", "IA_OBJ_TYPE"));
            
            GenericValue commExtension = EntityUtil.getFirst( delegator.findByAnd("WorkEffortCommExtension", UtilMisc.toMap("workEffortId", workEffortId, "workExtName", "OBJECT_ID"), null, false) );
            if (UtilValidate.isNotEmpty(commExtension)) {
                
                result.put("objectType", commExtension.getString("wftExtType"));
                result.put("objectId", commExtension.getString("wftExtValue"));
                
            }
            
            // communication extension [end]
            
            // content [start]
            
            List<LinkedHashMap> documentList = new ArrayList<LinkedHashMap>();
            
            EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId)
                    );
            
            /* List<GenericValue> contentSummaryList = delegator.findList("WorkEffortContentSummary", mainCondition, null, UtilMisc.toList("-fromDate"), null, false);
            
            if (UtilValidate.isNotEmpty(contentSummaryList)) {
                for (GenericValue contentSummary : contentSummaryList) {
                    
                    LinkedHashMap<String, Object> document = new LinkedHashMap<String, Object>();
                    
                    document.put("document_ref_num", UtilValidate.isNotEmpty(contentSummary.getString("documentRefNum")) ? contentSummary.getString("documentRefNum") : contentSummary.getString("annotationId"));
                    document.put("cnt_created_by_user_login", contentSummary.getString("createdByUserLogin"));
                    document.put("cnt_created_date", ParamUtil.getDateTimeStr(contentSummary, "createdDate"));
                    document.put("cnt_description", contentSummary.getString("description"));
                    document.put("file_name", contentSummary.getString("contentName"));
                    
                    documentList.add(document);
                }
            }*/
            List < GenericValue > workEffortContents = delegator.findByAnd("WorkEffortContent",UtilMisc.toMap("workEffortId", workEffortId), null, true);
            
            List<String> contentIds = EntityUtil.getFieldListFromEntityList(workEffortContents, "contentId", true);
            
            EntityCondition contentCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("contentId", EntityOperator.IN, contentIds));
            List < GenericValue > contentSummaryList = EntityQuery.use(delegator).from("Content").where(contentCondition).orderBy("-contentId").queryList();
            if (UtilValidate.isNotEmpty(contentSummaryList)) {
                for (GenericValue contentSummary : contentSummaryList) {
                    LinkedHashMap<String, Object> document = new LinkedHashMap<String, Object>();
                    document.put("document_ref_num", UtilValidate.isNotEmpty(contentSummary.getString("documentRefNum")) ? contentSummary.getString("documentRefNum") : contentSummary.getString("annotationId"));
                    document.put("cnt_created_by_user_login", contentSummary.getString("createdByUserLogin"));
                    document.put("cnt_created_date", ParamUtil.getDateTimeStr(contentSummary, "createdDate"));
                    document.put("cnt_description", contentSummary.getString("description"));
                    document.put("file_name", contentSummary.getString("contentName"));
                    
                    documentList.add(document);
                }
            }
            result.put("documents", documentList);
            
            // content [end]
            
            result.put("workEffortId", workEffortId);
            
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        
        result.putAll(ServiceUtil.returnSuccess("Successfully detailed activity.."));
        
        return result;
        
    }
    
    public static Map findInteractiveActivity(DispatchContext dctx, Map context) {
        
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String customerRelatedType = (String) context.get("customerRelatedType");
        String fromPartyId = (String) context.get("fromPartyId");
        String createdBy = (String) context.get("createdBy");
        String wfPartyId = (String) context.get("wfPartyId");
        Timestamp createdDateRangeStart = (Timestamp) context.get("createdDateRangeStart");
        Timestamp createdDateRangeEnd = (Timestamp) context.get("createdDateRangeEnd");
        String statusId = (String) context.get("statusId");
        String subStatusId = (String) context.get("subStatusId");
        Timestamp dueDateRangeStart = (Timestamp) context.get("dueDateRangeStart");
        Timestamp dueDateRangeEnd = (Timestamp) context.get("dueDateRangeEnd");
        String workEffortTypeId = (String) context.get("workEffortTypeId");
        String wfNationalId = (String) context.get("wfNationalId");
        String wfVplusId = (String) context.get("wfVplusId");
        List<String> primOwnerIdLoginIds = (List<String>) context.get("primOwnerIdLoginIds");
        List<String> emplLoginIds = (List<String>) context.get("emplLoginIds");
        
        String nextPageNum = (String) context.get("nextPageNum");
        
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            
            Map<String, Object> callCtxt = FastMap.newInstance();
            Map<String, Object> callResult = FastMap.newInstance();
            
            String viewEntityName = "WorkEffortAndPartyAssignCustom";
            
            EntityCondition partyCondition = EntityCondition.makeCondition("cif", EntityOperator.EQUALS, fromPartyId);
            
            List conditionList = FastList.newInstance();
            
            if (UtilValidate.isNotEmpty(fromPartyId) && UtilValidate.isNotEmpty(wfVplusId) && UtilValidate.isNotEmpty(wfNationalId) 
                    && ( UtilValidate.isNotEmpty(statusId) && (statusId.equals("IA_OPEN") || statusId.equals("IA_CLOSED")) )
                    ) {
            	conditionList.add(
                        EntityCondition.makeCondition(EntityOperator.OR,
                                EntityCondition.makeCondition(EntityOperator.AND,
                                		partyCondition,
                                        org.groupfio.crm.service.util.DataUtil.prepareStatusCondition(statusId, "currentStatusId", "IA_")
                                        ),
                                EntityCondition.makeCondition(EntityOperator.AND,
                                		EntityCondition.makeCondition("wfVplusId", EntityOperator.EQUALS, wfVplusId),
                                		org.groupfio.crm.service.util.DataUtil.prepareStatusCondition(statusId, "currentStatusId", "IA_")
                                        ),
                                EntityCondition.makeCondition(EntityOperator.AND,
                                		partyCondition,
                                		EntityCondition.makeCondition("wfVplusId", EntityOperator.EQUALS, wfVplusId),
                                        org.groupfio.crm.service.util.DataUtil.prepareStatusCondition(statusId, "currentStatusId", "IA_")
                                        )
                                )
                                
                        );
                
            } else {
                
                if (UtilValidate.isNotEmpty(fromPartyId)) {
                    conditionList.add(
                    		partyCondition
                            );
                }
                
                if (UtilValidate.isNotEmpty(wfVplusId)) {
                    conditionList.add(EntityCondition.makeCondition("wfVplusId", EntityOperator.EQUALS, wfVplusId));
                }
                
                if (UtilValidate.isNotEmpty(wfNationalId)) {
                    conditionList.add(EntityCondition.makeCondition("wfNationalId", EntityOperator.EQUALS, wfNationalId));
                }
                
                if (UtilValidate.isNotEmpty(statusId)) {
                    conditionList.add(org.groupfio.crm.service.util.DataUtil.prepareStatusCondition(statusId, "currentStatusId", "IA_"));
                }
                
            }
            
            if (UtilValidate.isNotEmpty(customerRelatedType)) {
                if ("05".equals(customerRelatedType) || "06".equals(customerRelatedType)) {
                    String objectType = EnumUtil.getEnumId(delegator, customerRelatedType, "IA_OBJ_TYPE");
                    List<GenericValue> commExtensions = delegator.findByAnd("WorkEffortCommExtension", UtilMisc.toMap("wftExtType", objectType, "workExtName", "OBJECT_ID"), null, false);
                    List<String> workEffortId = EntityUtil.getFieldListFromEntityList(commExtensions, "workEffortId", true);
                    conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortId));
                } else if (UtilValidate.isNotEmpty(customerRelatedType) && !"CARD_AND_BANK".equals(customerRelatedType)) {
                    conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, customerRelatedType));
                }
                if("CUSTOMER".equals(customerRelatedType)) {
                    result.put("custRelType", "01");
                }else if ("CARD_CUST".equals(customerRelatedType)) {
                    result.put("custRelType", "03");
                }
            }
            
            if (UtilValidate.isNotEmpty(createdBy)) {
                conditionList.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS, createdBy));
            }
            
            if (UtilValidate.isNotEmpty(wfPartyId)) {
            	wfPartyId = DataUtil.getUserLoginPartyId(delegator, wfPartyId);
                conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
                        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, wfPartyId),
                        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CAL_OWNER"),
                        EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)
	                    ));
            }
            
            if (UtilValidate.isNotEmpty(createdDateRangeStart)) {
                conditionList.add(
                        EntityCondition.makeCondition(
                                EntityCondition.makeCondition("createdDate", EntityOperator.EQUALS, null),
                                EntityOperator.OR,
                                EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, createdDateRangeStart)
                           )
                        );
            }
            if (UtilValidate.isNotEmpty(createdDateRangeEnd)) {
                conditionList.add(
                        EntityCondition.makeCondition(
                                EntityCondition.makeCondition("createdDate", EntityOperator.EQUALS, null),
                                EntityOperator.OR,
                                EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, createdDateRangeEnd)
                           )
                        );
            }
            
            if (UtilValidate.isNotEmpty(subStatusId)) {
                conditionList.add(EntityCondition.makeCondition("currentSubStatusId", EntityOperator.EQUALS, subStatusId));
            }
            
            if (UtilValidate.isNotEmpty(dueDateRangeStart)) {
                conditionList.add(
                        EntityCondition.makeCondition(
                                EntityCondition.makeCondition("estimatedStartDate", EntityOperator.EQUALS, null),
                                EntityOperator.OR,
                                EntityCondition.makeCondition("estimatedStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, dueDateRangeStart)
                           )
                        );
            }
            if (UtilValidate.isNotEmpty(dueDateRangeEnd)) {
                conditionList.add(
                        EntityCondition.makeCondition(
                                EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.EQUALS, null),
                                EntityOperator.OR,
                                EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.LESS_THAN_EQUAL_TO, dueDateRangeEnd)
                           )
                        );
            }
            
            if (UtilValidate.isNotEmpty(workEffortTypeId)) {
                conditionList.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, workEffortTypeId));
            }
            
            if(UtilValidate.isNotEmpty(primOwnerIdLoginIds)) {
                conditionList.add(EntityCondition.makeCondition("primOwnerId", EntityOperator.IN, primOwnerIdLoginIds));
            }
            if(UtilValidate.isNotEmpty(emplLoginIds)) {
                conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplLoginIds));
            }
            
            EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            
            Long count = new Long(0);
            
            EntityFindOptions  efoNum= new EntityFindOptions();
            efoNum.setFetchSize(1000);
            
            Debug.logInfo("count 1 start: "+UtilDateTime.nowTimestamp(), MODULE);
            count = delegator.findCountByCondition(viewEntityName, mainConditons, null, null, efoNum);
            Debug.logInfo("count 2 end: "+UtilDateTime.nowTimestamp(), MODULE);
            
            EntityFindOptions efo = new EntityFindOptions();
            
            if (UtilValidate.isNotEmpty(nextPageNum) && count > 0) {
                long npn = Long.parseLong(nextPageNum) - 1;
                
                int startInx = (int) (npn * GlobalConstants.DEFAULT_PER_PAGE_COUNT);
                int endInx = count.intValue() < GlobalConstants.DEFAULT_PER_PAGE_COUNT ? count.intValue() : GlobalConstants.DEFAULT_PER_PAGE_COUNT;        
                
                efo.setOffset(startInx);
                efo.setLimit(endInx);
            }
            Debug.log("==mainConditons=="+mainConditons);
            // UtilMisc.toList("createdDate DESC")
            List<GenericValue> workEffortList = new ArrayList<GenericValue>();
            if (count > 0) {
                workEffortList = delegator.findList(viewEntityName, mainConditons, null, UtilMisc.toList("workEffortId DESC"), efo, false);
            }
            
            result.put("workEffortList", workEffortList);
            result.put("totalCount", count);
            
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
