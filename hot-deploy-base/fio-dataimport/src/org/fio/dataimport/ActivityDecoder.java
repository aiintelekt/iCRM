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
package org.fio.dataimport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fio.homeapps.util.ParamUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastList;
import org.fio.homeapps.util.UtilGenerator;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.constants.GlobalConstants.SourceInvoked;
import java.util.Base64;
import java.io.UnsupportedEncodingException;
import org.fio.homeapps.util.EnumUtil;

/**
 * @author Sharif
 *
 */
public class ActivityDecoder implements ImportDecoder {
	
    private static final String module = ActivityDecoder.class.getName();
    
    protected GenericValue userLogin;
    
    public ActivityDecoder(Map<String, ?> context) throws GeneralException {
        this.userLogin = (GenericValue) context.get("userLogin");
    }

    public List<GenericValue> decode(GenericValue entry, Timestamp importTimestamp, Delegator delegator, LocalDispatcher dispatcher, Object... args) throws Exception {
    	List<String> matchPartyList = FastList.newInstance();
    	List<GenericValue> toBeStored = FastList.newInstance();
    	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    	List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
    	String workEffortId = null;
    	String ownerPartyId = null;
    	String externalId = null;
    	String ownerRoleTypeId = null;
    	String businessUnit = null;
    	String teamId = null;
    	String userLoginId = userLogin.getString("userLoginId");
        String owner = entry.getString("owner");
        if (UtilValidate.isNotEmpty(owner)) {
        	ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
        	ownerRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, ownerPartyId);
    		Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, ownerPartyId);
    		businessUnit = (String) buTeamData.get("businessUnit");
    		teamId = (String) buTeamData.get("emplTeamId");
			ownerRoleTypeId = UtilValidate.isNotEmpty(ownerRoleTypeId) ? (String) ownerRoleTypeId : "CAL_OWNER";
        }
        String duration = entry.getString("duration");
        int durationInt = Integer.valueOf(duration);
        Timestamp estimatedCompletionDate = entry.getTimestamp("estimatedCompletionDate");
        estimatedCompletionDate = org.fio.homeapps.util.UtilDateTime.addHoursToTimestamp(entry.getTimestamp("estimatedStartDate"),durationInt);
        
		conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
				EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, entry.getString("activityId")),
				EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, entry.getString("activityId"))
				));
    	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
    	GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where(mainConditon).queryFirst();
    	
		if (UtilValidate.isNotEmpty(workEffort)) {
			workEffortId = workEffort.getString("workEffortId");
		}
        
		//update work effort
    	if(UtilValidate.isNotEmpty(workEffortId)) {
        	GenericValue activity = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId",workEffortId).queryFirst();
        	activity.put("workEffortName", entry.getString("workEffortName"));
            activity.put("workEffortTypeId", entry.getString("workEffortTypeId"));
            activity.put("domainEntityId", entry.getString("domainEntityId"));
            activity.put("workEffortPurposeTypeId", entry.getString("workEffortPurposeTypeId"));
            String priority = EnumUtil.getEnumId(delegator, entry.getString("priority"), "PRIORITY_LEVEL", "Activities");
            if (UtilValidate.isNotEmpty(priority)) {
            	activity.put("priority", priority);
            }else {
            	GenericValue custRequst = EntityQuery.use(delegator).from("CustRequest").where(entry.getString("domainEntityId")).queryFirst();
            	activity.put("priority", (UtilValidate.isNotEmpty(custRequst.getString("priority")) ? custRequst.getString("priority") : null));
            }
            GenericValue workEffortAssocTriplet = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Task", "active", "Y").queryFirst();
            activity.put("workEffortServiceType", (UtilValidate.isNotEmpty(workEffortAssocTriplet.getString("code")) ? workEffortAssocTriplet.getString("code") : null));
            activity.put("createdByUserLogin", userLogin.getString("userLoginId"));
            activity.put("lastModifiedDate", UtilDateTime.nowTimestamp());
            activity.put("duration", duration);
            activity.put("cif", ownerPartyId);
            activity.put("domainEntityType", "SERVICE_REQUEST");
            activity.put("currentStatusId", entry.getString("currentStatusId"));
            activity.put("estimatedStartDate", entry.getTimestamp("estimatedStartDate"));
            activity.put("estimatedCompletionDate", estimatedCompletionDate);
            activity.put("workEffortTypeId", entry.getString("workEffortTypeId"));
            activity.put("createdDate", UtilDateTime.nowTimestamp());
        	activity.put("externalId", entry.getString("activityId"));
        	activity.put("lastModifiedByUserLogin", userLoginId);
        	activity.put("description", entry.getString("messages"));
        	
        	toBeStored.add(activity);
        	
        	//update work effort attribute
        	GenericValue workEffortAttr = EntityQuery.use(delegator).from("WorkEffortAttribute").where("workEffortId",workEffortId,"attrName","TECH_ARRIVAL_WINDOW").queryFirst();
        	workEffortAttr.put("attrValue",entry.getString("arrivalWindow"));
			toBeStored.add(workEffortAttr);
			
			//update work effort party assignment
        	GenericValue activityPartyAssignment = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(UtilMisc.toMap("workEffortId", workEffortId,"partyId", ownerPartyId,
					"roleTypeId", ownerRoleTypeId)).queryFirst();
        	if(UtilValidate.isEmpty(activityPartyAssignment)) {
				activityPartyAssignment = delegator.makeValue("WorkEffortPartyAssignment",UtilMisc.toMap("workEffortId", workEffortId,"partyId", ownerPartyId,
						"roleTypeId", ownerRoleTypeId,"fromDate", UtilDateTime.nowTimestamp()));
				if(UtilValidate.isNotEmpty(ownerPartyId)) {
					activityPartyAssignment.put("statusId", "PRTYASGN_ASSIGNED");
					activityPartyAssignment.put("assignedByUserLoginId", userLoginId);
					activityPartyAssignment.put("statusDateTime", UtilDateTime.nowTimestamp());
					activityPartyAssignment.put("ownerId", owner);
					activityPartyAssignment.put("emplTeamId", teamId);
					activityPartyAssignment.put("businessUnit", businessUnit);
					toBeStored.add(activityPartyAssignment);
				}
        	}else {
        		activityPartyAssignment.put("assignedByUserLoginId", userLoginId);
        		activityPartyAssignment.put("statusDateTime", UtilDateTime.nowTimestamp());
        		activityPartyAssignment.put("ownerId", owner);
				activityPartyAssignment.put("emplTeamId", teamId);
				activityPartyAssignment.put("businessUnit", businessUnit);
				toBeStored.add(activityPartyAssignment);
        	}
            //update work effort supplementory
        	GenericValue activitySupplementory = EntityQuery.use(delegator).from("WorkEffortSupplementory").where("workEffortId",workEffortId).queryFirst();
        	if (UtilValidate.isNotEmpty(activityPartyAssignment)) {
	        	activitySupplementory.put("wftMsdduration", duration);
	            activitySupplementory.put("sourceInvoked", SourceInvoked.API);
	            String messages = entry.getString("messages");
	            if (UtilValidate.isNotEmpty(messages)) {
	            	try {
	    				messages = Base64.getEncoder().encodeToString(messages.getBytes("utf-8"));
	    		        activitySupplementory.put("message", messages);
	    			} catch (UnsupportedEncodingException e) {
	    				Debug.log(e.getMessage());
	    			}
	            }
	            toBeStored.add(activitySupplementory);
        	}
            
            //update cust request work effort
        	GenericValue custRequestWorkEffort = EntityQuery.use(delegator).from("CustRequestWorkEffort").where(UtilMisc.toMap("workEffortId",workEffortId,
        			"custRequestId",entry.getString("domainEntityId"))).queryFirst();
        	if(UtilValidate.isEmpty(custRequestWorkEffort)) {
        		toBeStored.add(delegator.makeValue("CustRequestWorkEffort",UtilMisc.toMap("workEffortId",workEffortId,"custRequestId",entry.getString("domainEntityId"))));
        	}
            //update work effort contact
        	GenericValue workEffortContact = EntityQuery.use(delegator).from("WorkEffortContact").where(UtilMisc.toMap("workEffortId", workEffortId,"partyId", entry.getString("contactId"),
					"roleTypeId", "CONTACT")).queryFirst();
        	if(UtilValidate.isEmpty(workEffortContact)) {
	            workEffortContact = delegator.makeValue("WorkEffortContact",UtilMisc.toMap("workEffortId", workEffortId,"partyId", entry.getString("contactId"),
						"roleTypeId", "CONTACT","fromDate", UtilDateTime.nowTimestamp()));
	    		workEffortContact.put("createdByUserLogin", userLogin.getString("userLoginId"));
	    		workEffortContact.put("thruDate",null);
	    		toBeStored.add(workEffortContact);
        	}

            return toBeStored;
    	}
        
        
    	/**
    	 * 
    	 * Create activity
    	 * 
    	 */
    	
        workEffortId = delegator.getNextSeqId("WorkEffort");
        if(workEffortId!=null){
        	workEffortId = UtilGenerator.getIaNumber(delegator, workEffortId);
        }
        externalId = entry.getString("activityId");
		if (UtilValidate.isEmpty(externalId)) {
			externalId = workEffortId;
		}
		GenericValue activity = delegator.makeValue("WorkEffort", UtilMisc.toMap("workEffortId",workEffortId));
		activity.put("workEffortName", entry.getString("workEffortName"));
        activity.put("workEffortTypeId", entry.getString("workEffortTypeId"));
        activity.put("domainEntityId", entry.getString("domainEntityId"));
        activity.put("workEffortPurposeTypeId", entry.getString("workEffortPurposeTypeId"));
        String priority = EnumUtil.getEnumId(delegator, entry.getString("priority"), "PRIORITY_LEVEL", "Activities");
        if (UtilValidate.isNotEmpty(priority)) {
        	activity.put("priority", priority);
        }else {
        	GenericValue custRequst = EntityQuery.use(delegator).from("CustRequest").where("custRequestId",entry.getString("domainEntityId")).queryFirst();
        	activity.put("priority", (UtilValidate.isNotEmpty(custRequst.getString("priority")) ? custRequst.getString("priority") : null));
        }
        GenericValue workEffortAssocTriplet = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Task", "active", "Y").queryFirst();
        activity.put("workEffortServiceType", (UtilValidate.isNotEmpty(workEffortAssocTriplet.getString("code")) ? workEffortAssocTriplet.getString("code") : null));
        activity.put("createdByUserLogin", userLogin.getString("userLoginId"));
        activity.put("lastModifiedDate", UtilDateTime.nowTimestamp());
        activity.put("duration", duration);
        activity.put("cif", ownerPartyId);
        activity.put("domainEntityType", "SERVICE_REQUEST");
        activity.put("currentStatusId", entry.getString("currentStatusId"));
        activity.put("estimatedStartDate", entry.getTimestamp("estimatedStartDate"));
        activity.put("estimatedCompletionDate", estimatedCompletionDate);
        activity.put("workEffortTypeId", entry.getString("workEffortTypeId"));
        activity.put("createdDate", UtilDateTime.nowTimestamp());
        activity.put("externalId", externalId);
        activity.put("description", entry.getString("messages"));

        toBeStored.add(activity);
        
        GenericValue workEffortAttr = delegator.makeValue("WorkEffortAttribute",UtilMisc.toMap("workEffortId",workEffortId,"attrName","IS_SCHEDULING_REQUIRED"));
        if (UtilValidate.isNotEmpty(entry.getString("isSchedulingRequired"))) {
			workEffortAttr.put("attrValue",entry.getString("isSchedulingRequired"));
			toBeStored.add(workEffortAttr);
        }
        workEffortAttr = delegator.makeValue("WorkEffortAttribute",UtilMisc.toMap("workEffortId",workEffortId,"attrName","TECH_ARRIVAL_WINDOW"));
        if (UtilValidate.isNotEmpty(entry.getString("arrivalWindow"))) {
			workEffortAttr.put("attrValue",entry.getString("arrivalWindow"));
			toBeStored.add(workEffortAttr);
        }
        
        if(UtilValidate.isNotEmpty(ownerPartyId)) {
        	GenericValue activityPartyAssignment = delegator.makeValue("WorkEffortPartyAssignment",UtilMisc.toMap("workEffortId",workEffortId,"partyId", ownerPartyId,
        			"roleTypeId", ownerRoleTypeId,"fromDate", UtilDateTime.nowTimestamp()));
			activityPartyAssignment.put("statusId", "PRTYASGN_ASSIGNED");
			activityPartyAssignment.put("assignedByUserLoginId", userLoginId);
			activityPartyAssignment.put("statusDateTime", UtilDateTime.nowTimestamp());
			activityPartyAssignment.put("ownerId", owner);
			activityPartyAssignment.put("emplTeamId", teamId);
			activityPartyAssignment.put("businessUnit", businessUnit);
			toBeStored.add(activityPartyAssignment);
		}
        
        GenericValue activitySupplementory = delegator.makeValue("WorkEffortSupplementory", UtilMisc.toMap("workEffortId",workEffortId));
        activitySupplementory.put("wftMsdduration", duration);
        activitySupplementory.put("sourceInvoked", SourceInvoked.API);
        String messages = entry.getString("messages");
        if (UtilValidate.isNotEmpty(messages)) {
        	try {
				messages = Base64.getEncoder().encodeToString(messages.getBytes("utf-8"));
		        activitySupplementory.put("message", messages);
			} catch (UnsupportedEncodingException e) {
				Debug.log(e.getMessage());
			}
        }
        toBeStored.add(activitySupplementory);
        
        toBeStored.add(delegator.makeValue("CustRequestWorkEffort",UtilMisc.toMap("workEffortId",workEffortId,"custRequestId",entry.getString("domainEntityId"))));
        
        GenericValue workEffortContact = delegator.makeValue("WorkEffortContact", UtilMisc.toMap("workEffortId",workEffortId,"partyId", entry.getString("contactId"),
        		"roleTypeId", "CONTACT","fromDate", UtilDateTime.nowTimestamp()));
		workEffortContact.put("createdByUserLogin", userLogin.getString("userLoginId"));
		workEffortContact.put("thruDate",null);
		toBeStored.add(workEffortContact);
        
		return toBeStored;
    }

}
