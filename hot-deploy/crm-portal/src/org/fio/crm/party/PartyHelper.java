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
/* Copyright (c) Open Source Strategies, Inc. */

/*
 *  $Id:$
 *
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.fio.crm.party;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.fio.crm.util.FreemarkerUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericEntityNotFoundException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;

import freemarker.template.TemplateException;

/**
 * Party Helper methods which are designed to provide a consistent set of APIs that can be reused by
 * higher level services.
 *
 * Many of the methods have been migrated to org.opentaps.common.party.PartyHelper.  However, this class also has a lot of
 * CRMSFA specific functionality, so the code inside of these methods have been replaced to reference the common PartyHelper,
 * but we will keep this class and its methods.
 */
public final class PartyHelper {

    private PartyHelper() { }

    private static final String MODULE = PartyHelper.class.getName();
    public static final List<String> TEAM_MEMBER_ROLES = UtilMisc.toList("EMPLOYEE", "ACCOUNT_REP", "CUST_SERVICE_REP", "OWNER","SALES_REP");
    public static final List<String> TA_TEAM_MEMBER_ROLES = UtilMisc.toList("MANF_USER");// Added By Sabari Sri Desc : T & A User
    public static final List<String> CLIENT_PARTY_ROLES = UtilMisc.toList("ACCOUNT", "CONTACT", "CUSTOMER", "PROSPECT", "PARTNER", "LEAD");
    protected static final List<String> FIND_PARTY_FIELDS = Arrays.asList(new String[]{"firstName", "lastName", "groupName", "partyId", "companyName", "primaryEmailId", "primaryPostalAddressId", "primaryTelecomNumberId", "primaryCity", "primaryStateProvinceGeoId", "primaryCountryGeoId", "primaryEmail", "primaryCountryCode", "primaryAreaCode", "primaryContactNumber"});
    
    public static String getPartyName(GenericValue partyObject) {
        return getPartyName(partyObject, false);
    }

    public static String getPartyName(Delegator delegator, String partyId, boolean lastNameFirst) {
        GenericValue partyObject = null;
        try {
            partyObject = EntityQuery.use(delegator).from("PartyNameView").where("partyId", partyId).queryOne();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error finding PartyNameView in getPartyName", MODULE);
        }
        if (partyObject == null) {
            return partyId;
        } else {
            return formatPartyNameObject(partyObject, lastNameFirst);
        }
    }

    public static String getPartyName(GenericValue partyObject, boolean lastNameFirst) {
        if (partyObject == null) {
            return "";
        }
        if ("PartyGroup".equals(partyObject.getEntityName()) || "Person".equals(partyObject.getEntityName())) {
            return formatPartyNameObject(partyObject, lastNameFirst);
        } else {
            String partyId = null;
            try {
                partyId = partyObject.getString("partyId");
            } catch (IllegalArgumentException e) {
                Debug.logError(e, "Party object does not contain a party ID", MODULE);
            }

            if (partyId == null) {
                Debug.logWarning("No party ID found; cannot get name based on entity: " + partyObject.getEntityName(), MODULE);
                return "";
            } else {
                return getPartyName(partyObject.getDelegator(), partyId, lastNameFirst);
            }
        }
    }
    
    public static GenericValue getLeadAssignment(String leadId, Delegator delegator) throws GenericEntityException {
    	// setting party rel assoc to exisitng party and contact
    				EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
    						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId),
//    						EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, leadId),
    						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"),
    						EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
    						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"));
    				GenericValue partyRelationship = EntityUtil
    						.getFirst(delegator.findList("PartyRelationship", searchConditions, null, UtilMisc.toList("fromDate DESC"), null, false));

    	return partyRelationship;
    }

    public static String formatPartyNameObject(GenericValue partyValue, boolean lastNameFirst) {
        if (partyValue == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        ModelEntity modelEntity = partyValue.getModelEntity();
        if (modelEntity.isField("firstName") && modelEntity.isField("middleName") && modelEntity.isField("lastName")) {
            if (lastNameFirst) {
                if (UtilFormatOut.checkNull(partyValue.getString("lastName")) != null) {
                    result.append(UtilFormatOut.checkNull(partyValue.getString("lastName")));
                    if (partyValue.getString("firstName") != null) {
                        result.append(", ");
                    }
                }
                result.append(UtilFormatOut.checkNull(partyValue.getString("firstName")));
            } else {
                result.append(UtilFormatOut.ifNotEmpty(partyValue.getString("firstName"), "", " "));
                result.append(UtilFormatOut.ifNotEmpty(partyValue.getString("middleName"), "", " "));
                result.append(UtilFormatOut.checkNull(partyValue.getString("lastName")));
            }
        }
        if (modelEntity.isField("groupName") && partyValue.get("groupName") != null) {
            result.append(partyValue.getString("groupName"));
        }
        return result.toString();
    }
    /**
     * Check if the party has been deactivated.
     * @param partyId
     * @param delegator
     * @return is active
     * @throws GenericEntityNotFoundException
     */
    public static boolean isActive(String partyId, Delegator delegator) throws GenericEntityException {
        GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
        if (party == null) {
            throw new GenericEntityNotFoundException("No Party found with ID: " + partyId);
        }
        return (!"PARTY_DISABLED".equals(party.getString("statusId")));
    }
    
    /**
     * A helper method for creating a PartyRelationship entity from partyIdTo to partyIdFrom with specified partyRelationshipTypeId, roleTypeIdFrom,
     * a List of valid roles for the to-party, and a flag to expire any existing relationships between the two parties of the same
     * type.   The idea is that several services would do validation and then use this method to do all the work.
     *
     * @param partyIdTo the party id to of the PartyRelationship to create
     * @param partyIdFrom the party id from of the PartyRelationship to create
     * @param roleTypeIdFrom the role type id from of the PartyRelationship to create
     * @param partyRelationshipTypeId the partyRelationshipTypeId of the PartyRelationship to create
     * @param securityGroupId the securityGroupId of the PartyRelationship to create
     * @param validToPartyRoles a List of roleTypeIds which are valid for the partyIdTo in the create relationship.  It will cycle
     * through until the first of these roles is actually associated with partyIdTo and then create a PartyRelationship using that
     * roleTypeId.  If none of these are associated with partyIdTo, then it will return false
     * @param fromDate the from date of the PartyRelationship to create
     * @param expireExistingRelationships  If set to true, will look for all existing PartyRelationships of partyIdFrom, partyRelationshipTypeId
     * and expire all of them as of the passed in fromDate
     * @param userLogin a <code>GenericValue</code> value
     * @param delegator a <code>Delegator</code> value
     * @param dispatcher a <code>LocalDispatcher</code> value
     * @return <code>false</code> if no relationship was created or <code>true</code> if operation succeeds
     * @throws GenericEntityException if an error occurs
     * @throws GenericServiceException if an error occurs
     */
    public static boolean createNewPartyToRelationship(String partyIdTo, String partyIdFrom, String roleTypeIdFrom, String partyRelationshipTypeId, String securityGroupId, List<String> validToPartyRoles, Timestamp fromDate, boolean expireExistingRelationships, GenericValue userLogin, Delegator delegator, LocalDispatcher dispatcher) throws GenericEntityException, GenericServiceException {
    	
        // get the first valid roleTypeIdTo from a list of possible roles for the partyIdTo
        // this will be the role we use as roleTypeIdTo in PartyRelationship.
        String roleTypeIdTo = getFirstValidRoleTypeId(partyIdTo, validToPartyRoles, delegator);
        Debug.log("roleTypeIdTo 213 party helper===="+roleTypeIdTo+"===partyIdTo===="+validToPartyRoles);
        // if no matching roles were found, then no relationship created
        if (roleTypeIdTo == null) {
            return false;
        }

        /*
         * if expireExistingRelationships is true, then find all existing PartyRelationships with partyIdFrom and partyRelationshipTypeId which
         * are not expired on the fromDate and then expire them
         */
        if (expireExistingRelationships) {
            List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyRelationshipTypeId", partyRelationshipTypeId),null,false);
            expirePartyRelationships(partyRelationships, fromDate, dispatcher, userLogin);
        }

        // call createPartyRelationship service to create PartyRelationship using parameters and the role we just found
        Map<String, Object> input = UtilMisc.<String, Object>toMap("partyIdTo", partyIdTo, "roleTypeIdTo", roleTypeIdTo, "partyIdFrom", partyIdFrom, "roleTypeIdFrom", roleTypeIdFrom);
        input.put("partyRelationshipTypeId", partyRelationshipTypeId);
        input.put("priorityTypeId", "CSR_MANUAL");
        input.put("securityGroupId", securityGroupId);
        input.put("fromDate", fromDate);
        input.put("userLogin", userLogin);
        Map<String, Object> serviceResult = dispatcher.runSync("createPartyRelationship", input);

        if (ServiceUtil.isError(serviceResult)) {
            return false;
        }

        // on success return true
        return true;
    }
    
    /**
     * Expires a list of PartyRelationships that are still active on expireDate.
     * @param partyRelationships a <code>List</code> of <code>PartyRelationship</code> to expire
     * @param expireDate the expiration date to set
     * @param dispatcher a <code>LocalDispatcher</code> value
     * @param userLogin a <code>GenericValue</code> value
     * @exception GenericServiceException if an error occurs
     */
    public static void expirePartyRelationships(List<GenericValue> partyRelationships, Timestamp expireDate, LocalDispatcher dispatcher, GenericValue userLogin) throws GenericServiceException {
        List<GenericValue> relationsActiveOnFromDate = EntityUtil.filterByDate(partyRelationships, expireDate);
        // to expire on expireDate, set the thruDate to the expireDate in the parameter and call updatePartyRelationship service
        for (GenericValue partyRelationship : relationsActiveOnFromDate) {
            Map<String, Object> input = UtilMisc.<String, Object>toMap("partyIdTo", partyRelationship.getString("partyIdTo"), "roleTypeIdTo", partyRelationship.getString("roleTypeIdTo"),
                    "partyIdFrom", partyRelationship.getString("partyIdFrom"), "roleTypeIdFrom", partyRelationship.getString("roleTypeIdFrom"));
            input.put("fromDate", partyRelationship.getTimestamp("fromDate"));
            input.put("userLogin", userLogin);
            input.put("thruDate", expireDate);
            Map<String, Object> serviceResult = dispatcher.runSync("updatePartyRelationship", input);
            if (ServiceUtil.isError(serviceResult)) {
                throw new GenericServiceException("Failed to expire PartyRelationship with values: " + input.toString());
            }
        }
    }
    public static boolean createNewPartyToRelationship(String partyIdTo, String partyIdFrom, String roleTypeIdFrom,
            String partyRelationshipTypeId, String securityGroupId, List<String> validToPartyRoles,
            boolean expireExistingRelationships, GenericValue userLogin, Delegator delegator, LocalDispatcher dispatcher)
            throws GenericEntityException, GenericServiceException {
        return createNewPartyToRelationship(partyIdTo, partyIdFrom, roleTypeIdFrom,
                partyRelationshipTypeId, securityGroupId, validToPartyRoles, UtilDateTime.nowTimestamp(),
                expireExistingRelationships, userLogin, delegator, dispatcher);
    }
    /**
     * Method to get the current non-expired party responsible for the given account/contact/lead.
     *
     * @param   partyIdFrom     The partyId of the account/contact/lead
     * @param   roleTypeIdFrom  The role of the account/contact/lead (e.g., ACCOUNT, CONTACT, LEAD)
     * @return  First non-expired PartySummaryDetailsView or null if none found
     */
    public static GenericValue getCurrentResponsibleParty(String partyIdFrom, String roleTypeIdFrom, Delegator delegator) throws GenericEntityException {
        return getActivePartyByRole("RESPONSIBLE_FOR", partyIdFrom, roleTypeIdFrom, UtilDateTime.nowTimestamp(), delegator);
    }
    /** As above but without security group Id specified */
    public static GenericValue getActivePartyByRole(String partyRelationshipTypeId, String partyIdFrom, String roleTypeIdFrom,
            Timestamp activeDate, Delegator delegator)
            throws GenericEntityException {
        return getActivePartyByRole(partyRelationshipTypeId, partyIdFrom, roleTypeIdFrom, null, activeDate, delegator);
    }
    /**
     * Common method used by getCurrentlyResponsibleParty and related methods. This method will obtain the first PartyRelationship found with the given criteria
     * and return the PartySummaryDetailsView with partyId = partyRelationship.partyIdTo.
     *
     * @param   partyRelationshipTypeId         The party relationship (e.g., reps that are RESPONSIBLE_FOR an account)
     * @param   partyIdFrom                     The partyId of the account/contact/lead
     * @param   roleTypeIdFrom                  The role of the account/contact/lead (e.g., ACCOUNT, CONTACT, LEAD)
     * @param   securityGroupId                 Optional securityGroupId of the relationship
     * @param   activeDate                      Check only for active relationships as of this timestamp
     * @param   delegator a <code>Delegator</code> value
     * @return  First non-expired <code>PartySummaryDetailsView</code> or <code>null</code> if none found
     * @exception GenericEntityException if an error occurs
     */
    public static GenericValue getActivePartyByRole(String partyRelationshipTypeId, String partyIdFrom, String roleTypeIdFrom, String securityGroupId, Timestamp activeDate, Delegator delegator) throws GenericEntityException {

        Map<String, Object> input = UtilMisc.<String, Object>toMap("partyRelationshipTypeId", partyRelationshipTypeId, "partyIdFrom", partyIdFrom, "roleTypeIdFrom", roleTypeIdFrom);
        if (securityGroupId != null) {
            input.put("securityGroupId", securityGroupId);
        }
        List<GenericValue> relationships = delegator.findByAnd("PartyRelationship", input,null,true);
        List<GenericValue> activeRelationships = EntityUtil.filterByDate(relationships, activeDate);

        // if none are found, log a message about this and return null
        if (activeRelationships.size() == 0) {
            Debug.logInfo("No active PartyRelationships found with relationship [" + partyRelationshipTypeId + "] for party [" + partyIdFrom + "] in role [" + roleTypeIdFrom + "]", MODULE);
            return null;
        }

        // return the related party with partyId = partyRelationship.partyIdTo
        GenericValue partyRelationship = (GenericValue) activeRelationships.get(0);
        return EntityQuery.use(delegator).from("PartySummaryDetailsView").where("partyId", partyRelationship.getString("partyIdTo")).queryOne();
        //return delegator.findByPrimaryKey("PartySummaryDetailsView", UtilMisc.toMap("partyId", partyRelationship.getString("partyIdTo")));
    }
    /**
     * A helper method which finds the first valid roleTypeId for a partyId, using a List of possible roleTypeIds.
     *
     * @param partyId the party id
     * @param possibleRoleTypeIds a List of roleTypeIds
     * @param delegator a <code>Delegator</code>
     * @return the first roleTypeId from possibleRoleTypeIds which is actually found in PartyRole for the given partyId
     * @throws GenericEntityException if an error occurs
     */
    public static String getFirstValidRoleTypeId(String partyId, List<String> possibleRoleTypeIds, Delegator delegator) throws GenericEntityException {

        List<GenericValue> partyRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", partyId),null,true);

        // iterate across all possible roleTypeIds from the parameter
        for (String possibleRoleTypeId : possibleRoleTypeIds) {
            // try to look for each one in the list of PartyRoles
            for (GenericValue partyRole : partyRoles) {
                if (possibleRoleTypeId.equals(partyRole.getString("roleTypeId")))  {
                    return possibleRoleTypeId;
                }
            }
        }
        return null;
    }
    public static List<GenericValue> getClassificationGroupsForParty(String partyId, Delegator delegator) {
        List<GenericValue> groups = new ArrayList<GenericValue>();
        try {
            List<GenericValue> classifications = EntityQuery.use(delegator).from("CustomFieldPartyClassification").where("partyId", partyId).queryList();
            if (UtilValidate.isNotEmpty(classifications)) {
            	List<String> customFieldIds = classifications.stream().map(e->e.getString("customFieldId")).collect(Collectors.toList());
                List<GenericValue> partyClassificationGroups = delegator.findList("CustomField", EntityCondition.makeCondition("customFieldId", EntityOperator.IN, customFieldIds), null, UtilMisc.toList("customFieldName"), null, true);
                if (UtilValidate.isNotEmpty(partyClassificationGroups)) {
                    groups.addAll(partyClassificationGroups);
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to retrieve party classification groups for partyId: " + partyId, MODULE);
        }
        Debug.log("groups=="+groups);
        return groups;
    }
    
    /**
     * As above, but pass in the list of team member roles such as ACCOUNT_REP, etc.
     */
    public static String getFirstValidTeamMemberRoleTypeId(String partyId, Delegator delegator) throws GenericEntityException {
        return getFirstValidRoleTypeId(partyId, TEAM_MEMBER_ROLES, delegator);
    }

    /** Find the first valid role of the party, whether it be a team member or client party. */
    public static String getFirstValidCrmsfaPartyRoleTypeId(String partyId, Delegator delegator) throws GenericEntityException {
        String roleTypeId = getFirstValidRoleTypeId(partyId, TEAM_MEMBER_ROLES, delegator);
        if (roleTypeId == null) {
            roleTypeId = getFirstValidRoleTypeId(partyId, CLIENT_PARTY_ROLES, delegator);
        }
        return roleTypeId;
    }
    
    
    public static Set<GenericValue> getPartyGroupByGroupNameAndRoleType(Delegator delegator,String groupName, String roleTypeId) throws GenericEntityException {
        Set<GenericValue> resultSet = new FastSet<GenericValue>();
        try {
        	Set<String> fieldToSelect = new HashSet<String>();
        	fieldToSelect.add("partyId");
        	fieldToSelect.add("statusId");
        	fieldToSelect.add("groupName");
        	fieldToSelect.add("roleTypeId");
            // prepare the HQL to get Party
            EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
            		EntityCondition.makeCondition("groupName",EntityOperator.LIKE,groupName),
            		EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"PARTY_DISABLED"),
            		EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,roleTypeId));
            List<GenericValue> partyGroups = delegator.findList("PartyRoleStatusAndPartyDetail", condition, fieldToSelect, null, null, false);
            resultSet.addAll(partyGroups);
        } catch (GenericEntityException e) {
        	Debug.logInfo("Error-->"+e.getMessage(), MODULE);
        }
        return resultSet;
    }
    
    public static void copyToPartyRelationships(String partyIdFrom, String roleTypeIdFrom, String newPartyIdFrom, String newRoleTypeIdFrom,
            GenericValue userLogin, Delegator delegator, LocalDispatcher dispatcher)
            throws GenericEntityException, GenericServiceException {

        copyToPartyRelationships(partyIdFrom, roleTypeIdFrom, null, newPartyIdFrom, newRoleTypeIdFrom, userLogin, delegator, dispatcher);
    }
    
    
    public static void copyToPartyRelationships(String partyIdFrom, String roleTypeIdFrom, String partyRelationshipTypeId, String newPartyIdFrom, String newRoleTypeIdFrom, GenericValue userLogin, Delegator delegator, LocalDispatcher dispatcher) throws GenericEntityException, GenericServiceException {

        // hardcoded activeDate
        Timestamp activeDate = UtilDateTime.nowTimestamp();

        // first get the unexpired relationships for the From party
        Map<String, Object> input = UtilMisc.<String, Object>toMap("partyIdFrom", partyIdFrom, "roleTypeIdFrom", roleTypeIdFrom);
        if (partyRelationshipTypeId != null) {
            input.put("partyRelationshipTypeId", partyRelationshipTypeId);
        }
        List<GenericValue> relationships = delegator.findByAnd("PartyRelationship", input, null, false);
        List<GenericValue> activeRelationships = EntityUtil.filterByDate(relationships, activeDate);

        for (GenericValue relationship : activeRelationships) {
            input = UtilMisc.<String, Object>toMap("partyIdTo", relationship.getString("partyIdTo"), "roleTypeIdTo", relationship.getString("roleTypeIdTo"));
            input.put("partyIdFrom", newPartyIdFrom);
            input.put("roleTypeIdFrom", newRoleTypeIdFrom);
            input.put("fromDate", activeDate);

            // if relationship already exists, continue
            GenericValue check = delegator.findOne("PartyRelationship", input,false);
            if (check != null) {
                continue;
            }

            // create the relationship
            input.put("partyRelationshipTypeId", relationship.getString("partyRelationshipTypeId"));
            input.put("securityGroupId", relationship.getString("securityGroupId"));
            input.put("statusId", relationship.getString("statusId"));
            input.put("priorityTypeId", relationship.getString("priorityTypeId"));
            input.put("comments", relationship.getString("comments"));
            input.put("userLogin", userLogin);
            Map<String, Object> serviceResult = dispatcher.runSync("createPartyRelationship", input);
            if (ServiceUtil.isError(serviceResult)) {
                throw new GenericServiceException(ServiceUtil.getErrorMessage(serviceResult));
            }
        }
    }
    
    /**
     * This array determines the entities in which to delete the party and the order of deletion.
     * The second element in each row denotes the partyId field to check.
     * XXX Note: We are deleting historical data. For instance, activity records
     * involving the partyId will be gone forever!
     */
    private static String[][] CRM_PARTY_DELETE_CASCADE = {
        {"CustRequestRole", "partyId"},
        {"PartyNote", "partyId"},
        {"PartyDataSource", "partyId"},
        {"WorkEffortPartyAssignment", "partyId"},
        {"PartyContactMechPurpose", "partyId"},
        {"PartyContactMech", "partyId"},
        {"PartySupplementalData", "partyId"},
        {"PartyNameHistory", "partyId"},
        {"PartyGroup", "partyId"},
        {"PartyRelationship", "partyIdFrom"},
        {"PartyRelationship", "partyIdTo"},
        {"CustomFieldPartyClassification", "partyId"},
        {"PartyIdentification", "partyId"},
        {"SalesOpportunityRole", "partyId"},
        {"ProdCatalogRole", "partyId"},
        {"Person", "partyId"},
        {"CommunicationEventRole", "partyId"},
        {"ContentRole", "partyId"},
        {"FacilityParty", "partyId"},
        {"MarketingCampaignRole", "partyId"},
        {"PartyRole", "partyId"},
        {"PartyContent", "partyId"},
        {"PartyStatus", "partyId"},
        {"PartyIdentification", "partyId"},
        {"ContactListParty", "partyId"},
        {"CampaignContactListParty", "partyId"},
        {"CallRecordMaster", "partyId"},
        {"CallRecordDetails", "partyId"}
    };
    
    /**
     * Performs a cascade delete on a party.
     *
     * One reason this method can fail is that there were relationships with entities that are not being deleted.
     * If a party is not being deleted like it should, the developer should take a look at the exception thrown
     * by this method to see if any relations were violated. If there were violations, consider adding
     * the entities to the CASCADE array above.
     *
     * XXX Warning, this method is very brittle. It is essentially emulating the ON DELETE CASCADE functionality
     * of well featured databases, but very poorly. As the datamodel evolves, this method would have to be updated.
     */
    public static void deleteCrmParty(String partyId, Delegator delegator) throws GenericEntityException {
        // remove related entities from constant list
        for (int i = 0; i < CRM_PARTY_DELETE_CASCADE.length; i++) {
            String entityName = CRM_PARTY_DELETE_CASCADE[i][0];
            String fieldName = CRM_PARTY_DELETE_CASCADE[i][1];

            Map<String, Object> input = UtilMisc.<String, Object>toMap(fieldName, partyId);
            delegator.removeByAnd(entityName, input);
        }

        // remove communication events
        GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
        List<GenericValue> commEvnts = FastList.<GenericValue>newInstance();
        commEvnts.addAll(party.getRelated("ToCommunicationEvent"));
        commEvnts.addAll(party.getRelated("FromCommunicationEvent"));
        for (GenericValue commEvnt : commEvnts) {
            commEvnt.removeRelated("CommunicationEventRole");
            commEvnt.removeRelated("CommunicationEventWorkEff");
            commEvnt.removeRelated("CommEventContentAssoc");
            delegator.removeValue(commEvnt);
        }
        // finally remove party
        delegator.removeValue(party);
    }
    
    public static Timestamp getDeactivationDate(String partyId, Delegator delegator) throws GenericEntityException {
        // check party current status:
        if (isActive(partyId, delegator)) {
            return null;
        }
        // party is currently deactivated, get the deactivation date
        try {

            List<GenericValue> deactivationDates = delegator.findByAnd("PartyDeactivation", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("-deactivationTimestamp"),false);
            if (UtilValidate.isNotEmpty(deactivationDates)) {
                return (Timestamp) deactivationDates.get(0).get("deactivationTimestamp");
            } else {
                Debug.logWarning("The party [" + partyId + "] status is disabled but there is no registered deactivation date.", MODULE);
            }

        } catch (GenericEntityException e) {
            Debug.logError(e, MODULE);
        }
        return null;
    }
    
    /**
     * Retrieves all contact mechs for a party meeting these criteria, oldest one (by purpose date) first.
     * @param partyId the party to find the <code>ContachMech</code> for
     * @param contactMechTypeId the type of <code>ContachMech</code> to find
     * @param contactMechPurposeTypeId the purpose of <code>ContachMech</code> to find
     * @param additionalConditions other conditions on the <code>ContachMech</code> to find
     * @param delegator a <code>Delegator</code> value
     * @return the <code>List</code> of <code>ContachMech</code>
     * @throws GenericEntityException if an error occurs
     */
    public static List<GenericValue> getCurrentContactMechsForParty(String partyId, String contactMechTypeId, String contactMechPurposeTypeId, List<? extends EntityCondition> additionalConditions, Delegator delegator) throws GenericEntityException {
        Timestamp now = UtilDateTime.nowTimestamp();
        List<EntityCondition> conditions = UtilMisc.<EntityCondition>toList(
                EntityCondition.makeCondition("partyId", partyId),
                EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId),
                EntityCondition.makeCondition("contactMechTypeId", contactMechTypeId));
        if ("EMAIL_ADDRESS".equals(contactMechTypeId)) {
            conditions.add(EntityCondition.makeCondition("infoString", EntityOperator.NOT_EQUAL, null));
        }
        if (UtilValidate.isNotEmpty(additionalConditions)) {
            conditions.addAll(additionalConditions);
        }

        // TODO: Put the filter by dates in the conditions list
        List<GenericValue> contactMechs = delegator.findList("PartyContactWithPurpose", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("-purposeFromDate"), null, false);
        contactMechs = EntityUtil.filterByDate(contactMechs, now, "contactFromDate", "contactThruDate", true);
        contactMechs = EntityUtil.filterByDate(contactMechs, now, "purposeFromDate", "purposeThruDate", true);

        return contactMechs;
    }
    
    public static Map<String, String> mergePartyWithForm(Delegator delegator, String mergeFormId, String partyId, String orderId, String shipGroupSeqId, String shipmentId, Locale locale, boolean leaveTags, TimeZone timeZone) throws GenericEntityException {
        return mergePartyWithForm(delegator, mergeFormId, partyId, orderId, shipGroupSeqId, shipmentId, locale, leaveTags, timeZone, true);
    }

    public static Map<String, String> mergePartyWithForm(Delegator delegator, String mergeFormId, String partyId, String orderId, String shipGroupSeqId, String shipmentId, Locale locale, boolean leaveTags, TimeZone timeZone, boolean highlightTags) throws GenericEntityException {
        Map<String, Object> mergeContext = PartyHelper.assembleCrmsfaFormMergeContext(delegator, locale, partyId, orderId, shipGroupSeqId, shipmentId, timeZone);
        GenericValue mergeForm = delegator.findOne("MergeForm", UtilMisc.toMap("mergeFormId", mergeFormId), false);
        if (mergeForm == null) return null;
        String mergeFormText = mergeForm.getString("mergeFormText");
        String mergeFormSubject = mergeForm.getString("subject");
        Writer wr = new StringWriter();
        Map<String, String> output = new HashMap<String, String>();
        try {
            FreemarkerUtil.renderTemplateWithTags("MergeForm", mergeFormText, mergeContext, wr, leaveTags, highlightTags);
            output.put("mergeFormText", wr.toString());
            wr = new StringWriter();
            if (UtilValidate.isNotEmpty(mergeForm.getString("subject"))) {
                FreemarkerUtil.renderTemplateWithTags("MergeForm", mergeFormSubject, mergeContext, wr, leaveTags, false);
                output.put("subject", wr.toString());
            } else {
                output.put("subject", mergeForm.getString("mergeFormName"));
            }
        } catch (TemplateException e) {
            Debug.logError(e, MODULE);
            return null;
        } catch (IOException e) {
            Debug.logError(e, MODULE);
            return null;
        }
        return output;
    }
    
    public static Map<String, Object> assembleCrmsfaFormMergeContext(Delegator delegator, Locale locale, String partyId, String orderId, String shipGroupSeqId, String shipmentId, TimeZone timeZone) {
        Map<String, Object> templateContext = assembleCrmsfaGenericFormMergeContext(timeZone, locale);
        templateContext.putAll(assembleCrmsfaPartyFormMergeContext(delegator, partyId));
        /*templateContext.putAll(assembleCrmsfaOrderFormMergeContext(delegator, orderId));
        templateContext.putAll(assembleCrmsfaShipmentFormMergeContext(delegator, orderId, shipGroupSeqId, shipmentId, locale));*/
        return templateContext;
    }
    
    public static Map<String, Object> assembleCrmsfaGenericFormMergeContext(TimeZone timeZone, Locale locale) {
        Map<String, Object> templateContext = new HashMap<String, Object>();

        Calendar now = Calendar.getInstance(timeZone, locale);
        String mmddyyyy = new java.text.SimpleDateFormat("MM/dd/yyyy").format(now.getTime());
        String mmddyyyy2 = new java.text.SimpleDateFormat("MM-dd-yyyy").format(now.getTime());
        String yyyymmdd = new java.text.SimpleDateFormat("yyyy/MM/dd").format(now.getTime());
        String yyyymmdd2 = new java.text.SimpleDateFormat("yyyy-MM-dd").format(now.getTime());
        Integer month = Integer.valueOf(now.get(Calendar.MONTH));
        month++;
        String monthStr = month.toString();
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }
        //TODO: oandreyev. Test this code more carefully.
        ArrayList<String> monthNames = (ArrayList<String>)UtilDateTime.getMonthNames(locale);
        String monthName = monthNames.get(month - 1);
        templateContext.put("mmddyyyy", mmddyyyy);
        templateContext.put("mmddyyyy2", mmddyyyy2);
        templateContext.put("yyyymmdd", yyyymmdd);
        templateContext.put("yyyymmdd2", yyyymmdd2);
        templateContext.put("month", monthStr);
        templateContext.put("monthName", monthName);
        templateContext.put("day", new Integer(now.get(Calendar.DAY_OF_MONTH)).toString());
        templateContext.put("year", new Integer(now.get(Calendar.YEAR)).toString());

        return templateContext;
    }
    
    public static Map<String, Object> assembleCrmsfaPartyFormMergeContext(Delegator delegator, String partyId) {
        Map<String, Object> templateContext = new HashMap<String, Object>();
        if (UtilValidate.isNotEmpty(partyId)) {
            try {
                String email = PartyContactHelper.getElectronicAddressByPurpose(partyId, "EMAIL_ADDRESS", "PRIMARY_EMAIL", delegator);
                if (UtilValidate.isNotEmpty(email)) {
                    templateContext.put("email", email);
                }
                GenericValue address = PartyContactHelper.getPostalAddressValueByPurpose(partyId, "PRIMARY_LOCATION", true, delegator);
                if (UtilValidate.isNotEmpty(address)) {
                    templateContext.put("attnName", address.get("attnName"));
                    templateContext.put("toName", address.get("toName"));
                    templateContext.put("address1", address.get("address1"));
                    templateContext.put("address2", address.get("address2"));
                    templateContext.put("city", address.get("city"));
                    templateContext.put("zip", address.get("postalCode"));

                    GenericValue stateProvGeo = address.getRelatedOne("StateProvinceGeo");
                    if (UtilValidate.isNotEmpty(stateProvGeo)) {
                        templateContext.put("state", stateProvGeo.get("geoName") );
                    }
                    GenericValue countryGeo = address.getRelatedOne("CountryGeo");
                    if (UtilValidate.isNotEmpty(countryGeo)) {
                        templateContext.put("country", countryGeo.get("geoName") );
                    }
                }
                GenericValue party = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", partyId), false);
                Map<String, Object> partyMap = party.getAllFields();
                if (UtilValidate.isNotEmpty(partyMap)) {
                    Iterator<String> pmf = partyMap.keySet().iterator();
                    while (pmf.hasNext()) {
                        String fieldName = pmf.next();
                        Object value = partyMap.get(fieldName);
                        if (UtilValidate.isNotEmpty(value)) {
                            templateContext.put(fieldName, value);
                        }
                    }
                }

                templateContext.put("fullName", PartyHelper.getPartyName(party, false));

            } catch (GenericEntityException ge) {
                Debug.logError(ge, MODULE);
            }
        }
        return templateContext;
    }
    
    /**
     * As above, but pass in the list of internal party roles, such as ACCOUNT, CONTACT, PROSPECT.
     */
    public static String getFirstValidInternalPartyRoleTypeId(String partyId, Delegator delegator) throws GenericEntityException {
        return getFirstValidRoleTypeId(partyId, CLIENT_PARTY_ROLES, delegator);
    }
    
    public static String findPartyFromName(Delegator delegator, String name) {
		
		try {
			if (UtilValidate.isNotEmpty(name)) {
				
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition("firstName", EntityOperator.EQUALS, name), 
               			EntityCondition.makeCondition("lastName", EntityOperator.EQUALS, name)
               			//EntityUtil.getFilterByDateExpr()
               			);
				
				GenericValue person = EntityUtil.getFirst( delegator.findList("Person", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
				
				if (UtilValidate.isNotEmpty(person)) {
					
					return person.getString("partyId");
					
				}
			}
		} catch (Exception e) {
		}
		
		return null;
	}
    
    public static String findPartyFromNameExt(Delegator delegator, String fname , String lname) {
		
		try {
			if (UtilValidate.isNotEmpty(fname) || UtilValidate.isNotEmpty(lname)) {
				
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition("firstName", EntityOperator.EQUALS, fname), 
               			EntityCondition.makeCondition("lastName", EntityOperator.EQUALS, lname)
               			//EntityUtil.getFilterByDateExpr()
               			);
				
				GenericValue person = EntityUtil.getFirst( delegator.findList("Person", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
				
				if (UtilValidate.isNotEmpty(person)) {
					
					return person.getString("partyId");
					
				}
			}
		} catch (Exception e) {
		}
		
		return null;
	}
    public static String getContactIdFormcontactNumber(Delegator delegator, String contactNumber) {
    	try {
			if (UtilValidate.isNotEmpty(contactNumber)) {
				GenericValue telecom = EntityUtil.getFirst(delegator.findByAnd("TelecomNumber", UtilMisc.<String, Object>toMap("contactNumber",contactNumber), null, false));
				if (UtilValidate.isNotEmpty(telecom)) {
					EntityCondition mainCond = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, telecom.getString("contactMechId")),
							EntityUtil.getFilterByDateExpr()
							);
					GenericValue partyContactMech = EntityUtil.getFirst( delegator.findList("PartyContactMech", mainCond, null, null, null, true) );
					return partyContactMech.getString("partyId");
					
				}
			}
		} catch (Exception e) {
		}
		
		return null;
    }
}
