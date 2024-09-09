package org.fio.homeapps.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
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
    public static List<String> TEAM_MEMBER_ROLES = UtilMisc.toList("EMPLOYEE", "ACCOUNT_REP", "CUST_SERVICE_REP", "OWNER");
    public static List<String> TA_TEAM_MEMBER_ROLES = UtilMisc.toList("MANF_USER");// Added By Sabari Sri Desc : T & A User
    public static List<String> CLIENT_PARTY_ROLES = UtilMisc.toList("ACCOUNT", "CONTACT", "CUSTOMER", "PROSPECT", "PARTNER", "LEAD");
    public static List<String> FIND_PARTY_FIELDS = Arrays.asList(new String[]{"firstName", "lastName", "groupName", "partyId", "companyName", "primaryEmailId", "primaryPostalAddressId", "primaryTelecomNumberId", "primaryCity", "primaryStateProvinceGeoId", "primaryCountryGeoId", "primaryEmail", "primaryCountryCode", "primaryAreaCode", "primaryContactNumber"});
    
    public static String getPartyName(GenericValue partyObject) {
        return getPartyName(partyObject, false);
    }

    public static String getPartyName(Delegator delegator, String partyId, boolean lastNameFirst) {
        if (UtilValidate.isEmpty(partyId)) {
        	return null;
        }
    	GenericValue partyObject = null;
        try {
            partyObject = EntityQuery.use(delegator).from("PartyNameView").where("partyId", partyId).cache(true).queryOne();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error finding PartyNameView in getPartyName", MODULE);
        }
        if (partyObject == null) {
            return partyId;
        } else {
            return formatPartyNameObject(partyObject, lastNameFirst);
        }
    }
    
    public static String getPersonName(Delegator delegator, String partyId, boolean lastNameFirst) {
        try {
            if (UtilValidate.isNotEmpty(partyId)) {
            	EntityCondition condition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
            	
            	GenericValue entity = EntityQuery.use(delegator).select("firstName", "middleName", "lastName").from("Person").where(condition).cache(true).queryFirst();
                if (UtilValidate.isNotEmpty(entity)) {
                	StringBuilder result = new StringBuilder();
                    ModelEntity modelEntity = entity.getModelEntity();
                    if (modelEntity.isField("firstName") && modelEntity.isField("middleName") && modelEntity.isField("lastName")) {
                        if (lastNameFirst) {
                            if (UtilFormatOut.checkNull(entity.getString("lastName")) != null) {
                                result.append(UtilFormatOut.checkNull(entity.getString("lastName")));
                                if (entity.getString("firstName") != null) {
                                    result.append(", ");
                                }
                            }
                            result.append(UtilFormatOut.checkNull(entity.getString("firstName")));
                        } else {
                            result.append(UtilFormatOut.ifNotEmpty(entity.getString("firstName"), "", " "));
                            result.append(UtilFormatOut.ifNotEmpty(entity.getString("middleName"), "", " "));
                            result.append(UtilFormatOut.checkNull(entity.getString("lastName")));
                        }
                    }
                    return result.toString();
                }
            }
        } catch (Exception e) {}
        return "";
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
        if (result.length() == 0 && modelEntity.isField("groupName") && partyValue.get("groupName") != null) {
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
    	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
		conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, partyRelationshipTypeId));
		if (UtilValidate.isNotEmpty(securityGroupId)) {
			conditions.add(EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, securityGroupId));
		}
		conditions.add(EntityUtil.getFilterByDateExpr());
    	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		
    	GenericValue partyRelationship = EntityQuery.use(delegator).from("PartyRelationship").where(mainConditon).queryFirst();
    	if (UtilValidate.isEmpty(partyRelationship)) {
            Debug.logInfo("No active PartyRelationships found with relationship [" + partyRelationshipTypeId + "] for party [" + partyIdFrom + "] in role [" + roleTypeIdFrom + "]", MODULE);
            return null;
        }
    	
        return EntityQuery.use(delegator).from("PartySummaryDetailsView").where("partyId", partyRelationship.getString("partyIdTo")).queryOne();
    }
    
    public static GenericValue getActivePartyRelationshipByRole(String partyRelationshipTypeId, String partyIdFrom, String roleTypeIdFrom, String securityGroupId, Timestamp activeDate, Delegator delegator) throws GenericEntityException {
    	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
		conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, partyRelationshipTypeId));
		if (UtilValidate.isNotEmpty(securityGroupId)) {
			conditions.add(EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, securityGroupId));
		}
		conditions.add(EntityUtil.getFilterByDateExpr());
    	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		
    	GenericValue partyRelationship = EntityQuery.use(delegator).from("PartyRelationship").where(mainConditon).queryFirst();
    	if (UtilValidate.isEmpty(partyRelationship)) {
            Debug.logInfo("No active PartyRelationships found with relationship [" + partyRelationshipTypeId + "] for party [" + partyIdFrom + "] in role [" + roleTypeIdFrom + "]", MODULE);
            return null;
        }
    	
        return partyRelationship;
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
                List<GenericValue> partyClassificationGroups = delegator.findList("CustomField", EntityCondition.makeCondition("customFieldId", EntityOperator.IN, customFieldIds), null, null, null, true);
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
    
    
    public static Set<GenericValue> getPartyGroupByGroupNameAndRoleType(Delegator delegator,String groupName, String roleTypeId) throws Exception {
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
        } catch (Exception e) {
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
    
    /**
     * As above, but pass in the list of internal party roles, such as ACCOUNT, CONTACT, PROSPECT.
     */
    public static String getFirstValidInternalPartyRoleTypeId(String partyId, Delegator delegator) throws GenericEntityException {
        return getFirstValidRoleTypeId(partyId, CLIENT_PARTY_ROLES, delegator);
    }
    
    public static String getCurrentResponsiblePartyName(String partyIdFrom, String roleTypeIdFrom, Delegator delegator) {
		GenericValue partyRelationship = null;
		try {
			List<EntityCondition> relationConditionList = FastList.newInstance();
			relationConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
			relationConditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom));
			relationConditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"));
			relationConditionList.add(EntityUtil.getFilterByDateExpr());
			
			EntityCondition partyRelationshipCondition = EntityCondition.makeCondition(relationConditionList, EntityOperator.AND);
			
			partyRelationship = EntityQuery.use(delegator).select("partyIdTo").from("PartyRelationship").where(partyRelationshipCondition).queryFirst();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return UtilValidate.isNotEmpty(partyRelationship) ? PartyHelper.getPartyName(delegator, partyRelationship.getString("partyIdTo"), false) : null;
    }
    public static Map<String, Object> getResponsibleParty(Delegator delegator, List<GenericValue> dataList, String fieldId, String roleTypeIdFrom){
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		if (UtilValidate.isNotEmpty(dataList)) {
    			List<String> roleTypeIdFromList = new ArrayList<String>();
    			if(UtilValidate.isEmpty(roleTypeIdFrom)) {
    				roleTypeIdFromList.addAll(UtilMisc.toList("ACCOUNT","LEAD","CUSTOMER","CONTACT"));
    			} else
    				roleTypeIdFromList.add(roleTypeIdFrom);
    				
				List<String> fieldIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, fieldIds),
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, roleTypeIdFromList),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
						EntityUtil.getFilterByDateExpr()
						);
				
				List<GenericValue> partyRelList = EntityQuery.use(delegator)
						.select("partyIdFrom","partyIdTo")
						.from("PartyRelationship")
						.where(condition)
						.cache(false)
						.queryList();
				if(UtilValidate.isNotEmpty(partyRelList)) {
					result = partyRelList.parallelStream().collect(Collectors.toMap(s -> s.getString("partyIdFrom"), s -> {
						String partyIdTo = UtilValidate.isNotEmpty(s.getString("partyIdTo"))? s.getString("partyIdTo") : "";
						if(UtilValidate.isNotEmpty(partyIdTo)) {
							String name = DataUtil.getPartyName(delegator, partyIdTo);
							return UtilValidate.isNotEmpty(name) ? name : "";
						}
						else
							return "";
					}, (oldValue, newValue) -> newValue, HashMap::new));
					
					
				}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	
    	return result;
    }
    public static Map<String, Object> getPartiesName(Delegator delegator, List<GenericValue> dataList, String fieldId){
    	Map<String, Object> respPartyMap = new HashMap<String, Object>();
    	try {
    		if (UtilValidate.isNotEmpty(dataList)) {
				List<String> fieldIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				respPartyMap = fieldIds.parallelStream().collect(Collectors.toMap(x -> (String) x,
						x -> {
							String val = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, x, false);
							if (UtilValidate.isEmpty(val)) {
								val = "";
							}
							return UtilValidate.isNotEmpty(x) ? val : "";
						},
						(attr1, attr2) -> {
							return attr2;
						}));
			}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return respPartyMap;
    }
   
    public static String getContactNumber(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
    	return getContactNumber(delegator, partyId, contactMechPurposeTypeId,"N");
    }
    
    public static String getContactNumber(Delegator delegator, String partyId, String contactMechPurposeTypeId, String includeCountryCode) {
    	return getContactNumber(delegator, partyId, contactMechPurposeTypeId, includeCountryCode, true);
    }
    
    public static String getContactNumber(Delegator delegator, String partyId, String contactMechPurposeTypeId, String includeCountryCode, boolean formatNumber) {

        try {
            if (UtilValidate.isNotEmpty(partyId)) {
            	List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
            	conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
            	conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId));
            	conditionsList.add(EntityUtil.getFilterByDateExpr());

            	EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            	List<GenericValue> contactMechPurposeList = delegator.findList("PartyContactMechPurpose", mainConditons, UtilMisc.toSet("contactMechId"), null, null, false);
            	if (UtilValidate.isNotEmpty(contactMechPurposeList)) {
            		List<String> contactMechIds = contactMechPurposeList.stream().map(x->x.getString("contactMechId")).collect(Collectors.toList());
            		
            		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        			conditions.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds));
        			conditions.add(EntityUtil.getFilterByDateExpr());
                	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                	GenericValue pcm = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMech").where(mainConditon).queryFirst();
            		
                	if (UtilValidate.isNotEmpty(pcm)) {
            			GenericValue primaryPhoneNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", pcm.getString("contactMechId")), false);
                		if (UtilValidate.isNotEmpty(primaryPhoneNumber)) {
                			String phoneNumber = primaryPhoneNumber.getString("contactNumber");
                			String countryCode = primaryPhoneNumber.getString("countryCode");
                			if(formatNumber) {
                				phoneNumber = DataUtil.preparePhoneNumber(delegator, phoneNumber);
                				if("Y".equals(includeCountryCode) && UtilValidate.isNotEmpty(countryCode) && UtilValidate.isNotEmpty(phoneNumber)) {
                					if(!(countryCode.startsWith("+")))	
                						countryCode= "+"+countryCode;
                					phoneNumber = countryCode+"-"+ phoneNumber;
                				}
                			}else {
                				if("Y".equals(includeCountryCode) && UtilValidate.isNotEmpty(countryCode) && UtilValidate.isNotEmpty(phoneNumber)) {
                					if(!(countryCode.startsWith("+")))	
                						countryCode= "+"+countryCode;
                					phoneNumber = countryCode + phoneNumber;
                				}
                			}
                			return phoneNumber;
                		}
            		}
            	}
            }
        } catch (Exception e) {}
        return "";
    }
    public static Map<String, Object> getContactNumberList(Delegator delegator, String partyId, String contactMechPurposeTypeId, String includeCountryCode) {
    	Map<String, Object> contactNumberList = new LinkedHashMap<>();
    	try {
            if (UtilValidate.isNotEmpty(partyId)) {
            	List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
            	conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, Arrays.asList(partyId.split(","))));
            	conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId));
            	conditionsList.add(EntityUtil.getFilterByDateExpr());

            	EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            	List<GenericValue> contactMechPurposeList = delegator.findList("PartyContactMechPurpose", mainConditons, UtilMisc.toSet("contactMechId"), null, null, false);
            	if (UtilValidate.isNotEmpty(contactMechPurposeList)) {
            		List<String> contactMechIds = contactMechPurposeList.stream().map(x->x.getString("contactMechId")).collect(Collectors.toList());
            		
            		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        			//conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        			conditions.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds));
        			conditions.add(EntityUtil.getFilterByDateExpr());
                	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                	List<GenericValue> pcmList = EntityQuery.use(delegator).select("partyId", "contactMechId").from("PartyContactMech").where(mainConditon).queryList();
            		
                	if (UtilValidate.isNotEmpty(pcmList)) {
            			for (GenericValue pcm : pcmList) {
            				GenericValue primaryPhoneNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", pcm.getString("contactMechId")), true);
                    		if (UtilValidate.isNotEmpty(primaryPhoneNumber)) {
                    			String phoneNumber = primaryPhoneNumber.getString("contactNumber");
                    			String countryCode = primaryPhoneNumber.getString("countryCode");
                    			if("Y".equals(includeCountryCode) && UtilValidate.isNotEmpty(countryCode) && UtilValidate.isNotEmpty(phoneNumber)) {
                    				if(!(countryCode.startsWith("+")))	
                    					countryCode= "+"+countryCode;
                    				
                    				phoneNumber = countryCode + phoneNumber;
                    			}
                    			contactNumberList.put(pcm.getString("partyId"), phoneNumber);
                    		}
            			}
            		}
            	}
            }
        } catch (Exception e) {}

        return contactNumberList;
    }
    
    public static String getEmailAddress(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
        try {
            if (UtilValidate.isNotEmpty(partyId)) {
            	List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
            	conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
            	conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId));
            	conditionsList.add(EntityUtil.getFilterByDateExpr());

            	EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            	List<GenericValue> contactMechPurposeList = delegator.findList("PartyContactMechPurpose", mainConditons, UtilMisc.toSet("contactMechId"), null, null, false);
            	if (UtilValidate.isNotEmpty(contactMechPurposeList)) {
            		List<String> contactMechIds = contactMechPurposeList.stream().map(x->x.getString("contactMechId")).collect(Collectors.toList());
            		
            		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        			conditions.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds));
        			conditions.add(EntityUtil.getFilterByDateExpr());
                	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                	GenericValue pcm = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMech").where(mainConditon).queryFirst();
                	
            		if (UtilValidate.isNotEmpty(pcm)) {
            			GenericValue contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", pcm.getString("contactMechId")), false);
                		if (UtilValidate.isNotEmpty(contactMech)) {
                			return contactMech.getString("infoString");
                		}
            		}
            	}
            }
        } catch (Exception e) {}

        return "";
    }
    
    public static Map<String, Object> getEmailAddressList(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
        Map<String, Object> emailAddressList = new LinkedHashMap<>();
    	try {
            if (UtilValidate.isNotEmpty(partyId)) {
            	List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
            	conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, Arrays.asList(partyId.split(","))));
            	conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId));
            	conditionsList.add(EntityUtil.getFilterByDateExpr());

            	EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            	List<GenericValue> contactMechPurposeList = delegator.findList("PartyContactMechPurpose", mainConditons, UtilMisc.toSet("contactMechId"), null, null, false);
            	if (UtilValidate.isNotEmpty(contactMechPurposeList)) {
            		List<String> contactMechIds = contactMechPurposeList.stream().map(x->x.getString("contactMechId")).collect(Collectors.toList());
            		
            		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        			//conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        			conditions.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds));
        			conditions.add(EntityUtil.getFilterByDateExpr());
                	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                	List<GenericValue> pcmList = EntityQuery.use(delegator).select("partyId", "contactMechId").from("PartyContactMech").where(mainConditon).queryList();
                	
            		if (UtilValidate.isNotEmpty(pcmList)) {
            			for (GenericValue pcm : pcmList) {
            				GenericValue contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", pcm.getString("contactMechId")), true);
                    		if (UtilValidate.isNotEmpty(contactMech)) {
                    			emailAddressList.put(pcm.getString("partyId"), contactMech.getString("infoString"));
                    			//return contactMech.getString("infoString");
                    		}
            			}
            		}
            	}
            }
        } catch (Exception e) {}
        return emailAddressList;
    }
    
    public static String getUserLoginName(Delegator delegator, String userLoginId, boolean lastNameFirst) {
    	try {
    		GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userLoginId).queryOne();
    		if (UtilValidate.isNotEmpty(userLogin) && UtilValidate.isNotEmpty(userLogin.getString("partyId"))) {
    			return getPartyName(delegator, userLogin.getString("partyId"), lastNameFirst);
    		}
    	}catch (GenericEntityException e) {
            Debug.logError(e, "Unable to retrieve partyId for userLoginId: " + userLoginId, MODULE);
        }
    	return "";
    }
    
    public static String getPartyRoleTypeId(String partyId, Delegator delegator) throws GenericEntityException {
    	String roleTypeId = "";
    	try {
    		if(UtilValidate.isNotEmpty(partyId)) {
    			GenericValue party = EntityQuery.use(delegator).select("roleTypeId").from("Party").where("partyId",partyId).queryFirst();
    			if(UtilValidate.isNotEmpty(party) && UtilValidate.isNotEmpty(party.getString("roleTypeId"))) {
    				roleTypeId = party.getString("roleTypeId");
    			}
    		}
    	} catch (Exception e) {
    		Debug.logError(e, "Error finding Party role", MODULE);
    	}
    	return roleTypeId;
    }
    public static String isPrimaryContact( Delegator delegator,String partyIdFrom,String partyIdTo) throws GenericEntityException {
    	String isPrimary = "N";
    	try {
    		if(UtilValidate.isNotEmpty(partyIdFrom) && UtilValidate.isNotEmpty(partyIdTo)) {
    			GenericValue party = EntityQuery.use(delegator).select("relationshipStatusId").from("PartyFromRelnAndParty").where("partyIdFrom",partyIdFrom,"partyRelationshipTypeId","CONTACT_REL_INV","relationshipStatusId","PARTY_DEFAULT","partyIdTo",partyIdTo).queryFirst();
    			if(UtilValidate.isNotEmpty(party)){
    				isPrimary = "Y";
    			}
    		}
    	} catch (Exception e) {
    		Debug.logError(e, "Error finding isPrimaryContact", MODULE);
    	}
    	return isPrimary;
    }
    public static String getPartyDesignation(String partyId, Delegator delegator) throws GenericEntityException {
    	String partyDesignation = "";
    	try {
    		if(UtilValidate.isNotEmpty(partyId)) {
    			GenericValue party = EntityQuery.use(delegator).select("designation").from("Person").where("partyId",partyId).queryOne();
    			if(UtilValidate.isNotEmpty(party) && UtilValidate.isNotEmpty(party.getString("designation"))) {
    				partyDesignation = EnumUtil.getEnumDescription(delegator, party.getString("designation"), "DBS_LD_DESIGNATION");
    			}
    		}
    	} catch (Exception e) {
    		Debug.logError(e, "Error finding PartyDesignation", MODULE);
    	}
    	return partyDesignation;
    }
    
    public static List<Map<String, Object>> getActivePartyUserLogin(Delegator delegator, List<String> partyIds, List<String> roleTypeIds) {
    	List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        try {
            if (UtilValidate.isNotEmpty(roleTypeIds)) {
            	List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
            	if (UtilValidate.isNotEmpty(partyIds)) {
            		conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
            	}
            	
            	conditionsList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeIds));
            	conditionsList.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));

            	EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            	List<GenericValue> partyList = delegator.findList("PartyAndRoleAndUserLogin", mainConditons, null, null, null, false);
            	if (UtilValidate.isNotEmpty(partyList)) {
            		for (GenericValue party : partyList) {
            			Map<String, Object> data = new HashMap<String, Object>();
            			
            			String partyId = party.getString("partyId");
        				String partyRoleTypeId = party.getString("roleTypeId");
            			
            			data.put("userLoginId", party.getString("userLoginId"));
    					data.put("partyId", partyId);
    					data.put("partyName", getPartyName(delegator, partyId, false));
    					data.put("roleTypeId", partyRoleTypeId);
    					data.put("roleDesc", org.fio.homeapps.util.DataUtil.getRoleTypeDesc(delegator, partyRoleTypeId));
    					dataList.add(data);
            		}
            	}
            }
        } catch (Exception e) {}

        return dataList;
    }
    
    public static Map<String, Object> getPartyNameByUserLoginIds(Delegator delegator, Map<String, Object> results, List<GenericValue> dataList, String fieldId) {
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> userLoginIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				for (String userLoginId : userLoginIds) {
					if (!results.containsKey(userLoginId)) {
						results.put(userLoginId, org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, userLoginId, false));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
    
    public static Map<String, Object> getPartyNameByPartyIds(Delegator delegator, Map<String, Object> results, List<GenericValue> dataList, String fieldId) {
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				for (String partyId : partyIds) {
					if (!results.containsKey(partyId)) {
						results.put(partyId, org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyId, false));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
    
    public static List<String> getPartyNames(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> nameList = dataList.stream().map(e->org.fio.homeapps.util.PartyHelper.getPartyName(delegator, e.getString(fieldId), false)).collect(Collectors.toList());
				return nameList;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
    
    public static String getPartyIdentificationTypeId(Delegator delegator, Map<String, Object> filter) {
		try {
			if (UtilValidate.isNotEmpty(filter)) {
				String partyId = (String) filter.get("partyId");
				if (UtilValidate.isNotEmpty(partyId)) {
					List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
                	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                	List<GenericValue> partyRoleList = EntityQuery.use(delegator).from("PartyRole").where(mainConditon).queryList();
                	if (UtilValidate.isNotEmpty(partyRoleList)) {
                		//GenericValue partyRole = partyRoleList.stream().filter(x->x.getString("roleTypeId").equals("DEALER")).findFirst().get();
                		if (UtilValidate.isNotEmpty(partyRoleList.stream().filter(x->x.getString("roleTypeId").equals("DEALER")).findFirst().orElse(null))) {
                			return "REL_SALES_MANAGER_DEALER";
                		} else if (UtilValidate.isNotEmpty(partyRoleList.stream().filter(x->x.getString("roleTypeId").equals("PARENT_ACCOUNT")).findFirst().orElse(null))) {
                			return "REL_SALES_MGR_PARENT";
                		} else if (UtilValidate.isNotEmpty(partyRoleList.stream().filter(x->x.getString("roleTypeId").equals("SUB_PARENT_ACCOUNT")).findFirst().orElse(null))) {
                			return "REL_SALES_MGR_SUB_PARENT";
                		}
                	}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
    
    public static Map<String, Object> getUserLoginIdByPartyIds(Delegator delegator, Map<String, Object> results, List<GenericValue> dataList, String fieldId) {
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				for (String partyId : partyIds) {
					if (!results.containsKey(partyId)) {
						results.put(partyId, org.fio.homeapps.util.DataUtil.getActiveUserLoginIdByPartyId(delegator, partyId));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
    
    public static Map<String, Object> getResponsiblePartyByPartyIds(Delegator delegator, List<String> partyIds, String roleTypeIdFrom){
    	return getResponsiblePartyByPartyIds(delegator, partyIds, roleTypeIdFrom, "RESPONSIBLE_FOR");
    }
    public static Map<String, Object> getResponsiblePartyByPartyIds(Delegator delegator, List<String> partyIds, String roleTypeIdFrom, String partyRelationshipTypeId){
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		if (UtilValidate.isNotEmpty(partyIds)) {
    			
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyIds),
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, partyRelationshipTypeId),
						EntityUtil.getFilterByDateExpr()
						);
				
				List<GenericValue> partyRelList = EntityQuery.use(delegator)
						.select("partyIdFrom","partyIdTo")
						.from("PartyRelationship")
						.where(condition)
						.cache(false)
						.queryList();
				if(UtilValidate.isNotEmpty(partyRelList)) {
					result = partyRelList.parallelStream().collect(Collectors.toMap(s -> s.getString("partyIdFrom"), s -> {
						String partyIdTo = UtilValidate.isNotEmpty(s.getString("partyIdTo"))? s.getString("partyIdTo") : "";
						if(UtilValidate.isNotEmpty(partyIdTo)) {
							String name = DataUtil.getPartyName(delegator, partyIdTo);
							return UtilValidate.isNotEmpty(name) ? name : "";
						}
						else
							return "";
					}, (oldValue, newValue) -> newValue, HashMap::new));
				}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	return result;
    }
    public static Map<String, Object> getResponsiblePartyDetailByPartyIds(Delegator delegator, List<String> partyIds, String roleTypeIdFrom, String partyRelationshipTypeId){
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		if (UtilValidate.isNotEmpty(partyIds)) {
    			
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyIds),
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, partyRelationshipTypeId),
						EntityUtil.getFilterByDateExpr()
						);
				
				List<GenericValue> partyRelList = EntityQuery.use(delegator)
						.select("partyIdFrom","partyIdTo")
						.from("PartyRelationship")
						.where(condition)
						.cache(false)
						.queryList();
				if(UtilValidate.isNotEmpty(partyRelList)) {
					result = partyRelList.parallelStream().collect(Collectors.toMap(s -> s.getString("partyIdFrom"), s -> {
						String partyIdTo = UtilValidate.isNotEmpty(s.getString("partyIdTo"))? s.getString("partyIdTo") : "";
						if(UtilValidate.isNotEmpty(partyIdTo)) {
							String name = DataUtil.getPartyName(delegator, partyIdTo);
							Map<String, Object> data = new LinkedHashMap<>();
							data.put("partyId", partyIdTo);
							data.put("partyName", name);
							return data;
						}
						else
							return "";
					}, (oldValue, newValue) -> newValue, HashMap::new));
				}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	return result;
    }
    
    public static GenericValue getPrimaryPostalAddress(Delegator delegator, String contactMechId) {
    	return getPrimaryPostalAddress(delegator, contactMechId, false);
    }
    public static GenericValue getPrimaryPostalAddress(Delegator delegator, String contactMechId, boolean useCache) {
    	GenericValue postalAddress = null;
    	try {
    		postalAddress = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId", contactMechId).cache(useCache).queryFirst();
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return postalAddress;
    }
    
    public static GenericValue getPrimaryEmailAddress(Delegator delegator, String contactMechId) {
    	return getPrimaryEmailAddress(delegator, contactMechId, false);
    }
    public static GenericValue getPrimaryEmailAddress(Delegator delegator, String contactMechId, boolean useCache) {
    	GenericValue emailAddress = null;
    	try {
    		emailAddress = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", contactMechId).cache(useCache).queryFirst();
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return emailAddress;
    }
    
    public static GenericValue getPrimaryTelecomNumber(Delegator delegator, String contactMechId) {
    	return getPrimaryTelecomNumber(delegator, contactMechId, false);
    }
    public static GenericValue getPrimaryTelecomNumber(Delegator delegator, String contactMechId, boolean useCache) {
    	GenericValue telecomNumber = null;
    	try {
    		telecomNumber = EntityQuery.use(delegator).from("TelecomNumber").where("contactMechId", contactMechId).cache(useCache).queryFirst();
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return telecomNumber;
    }
    
    public static Map<String, Object> getPartySummary(Delegator delegator, Map<String, Object> filter) {
    	Map<String, Object> partySummary = new LinkedHashMap<>();
        try {
        	if (UtilValidate.isNotEmpty(filter)) {
        		String partyId = (String) filter.get("partyId");
        		String roleTypeId = (String) filter.get("roleTypeId");
        		
        		GenericValue primaryPostal = (GenericValue) filter.get("primaryPostal");
        		
        		GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryFirst();
        		
        		partySummary.putAll(party.getAllFields());
        		
        		if (UtilValidate.isNotEmpty(roleTypeId)) {
        			if (roleTypeId.equals("CUSTOMER")) {
        				Set<String> fieldsToSelect = new LinkedHashSet<>();
        				fieldsToSelect.add("salutation");
        				fieldsToSelect.add("firstName");
        				fieldsToSelect.add("middleName");
        				fieldsToSelect.add("lastName");
        				fieldsToSelect.add("personalTitle");
        				fieldsToSelect.add("gender");
        				fieldsToSelect.add("birthDate");
        				fieldsToSelect.add("maritalStatus");
        				fieldsToSelect.add("occupation");
        				fieldsToSelect.add("isLoyaltyEnabled");
        				fieldsToSelect.add("loyaltyId");
        				fieldsToSelect.add("nationality");
        				fieldsToSelect.add("businessUnit");
        				fieldsToSelect.add("nationalId");
        				fieldsToSelect.add("designation");
        				fieldsToSelect.add("emplTeamId");
        				fieldsToSelect.add("balancePoints");
        				fieldsToSelect.add("localStorePreference");
        				fieldsToSelect.add("loyaltyStoreId");
        				fieldsToSelect.add("assignedStore");
        				fieldsToSelect.add("marketingStore");
        				fieldsToSelect.add("loyaltyAcquiredStore");
        				fieldsToSelect.add("localTimeZone");
        				GenericValue person = EntityQuery.use(delegator).select(fieldsToSelect).from("Person").where("partyId", partyId).queryFirst();
        				partySummary.putAll(person.getAllFields());
        			}
        		}
        		
        		Set<String> fieldsToSelect = new LinkedHashSet<>();
				fieldsToSelect.add("supplementalPartyTypeId");
				fieldsToSelect.add("departmentName");
				fieldsToSelect.add("importantNote");
				fieldsToSelect.add("companyName");
				GenericValue psd = EntityQuery.use(delegator).select(fieldsToSelect).from("PartySupplementalData").where("partyId", partyId).queryFirst();
				if (UtilValidate.isNotEmpty(psd)) {
					partySummary.putAll(psd.getAllFields());
				}
				
        		if (UtilValidate.isEmpty(primaryPostal)) {
        			List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
    				
    				conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
    				conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION"));
    				conditionsList.add(EntityUtil.getFilterByDateExpr());

    				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
    				GenericValue contactMechPurpose = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechSummary").where(mainConditons).queryFirst();
    				if (UtilValidate.isNotEmpty(contactMechPurpose)) {
    					primaryPostal = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId", contactMechPurpose.getString("contactMechId")).queryFirst();
    					if (UtilValidate.isNotEmpty(primaryPostal)) {
    						partySummary.put("primaryToName", primaryPostal.getString("toName"));
    						partySummary.put("primaryAttnName", primaryPostal.getString("attnName"));
    						partySummary.put("primaryAddress1", primaryPostal.getString("address1"));
    						partySummary.put("primaryAddress2", primaryPostal.getString("address2"));
    						partySummary.put("primaryDirections", primaryPostal.getString("directions"));
    						partySummary.put("primaryCity", primaryPostal.getString("city"));
    						partySummary.put("primaryPostalCode", primaryPostal.getString("postalCode"));
    						partySummary.put("primaryPostalCodeExt", primaryPostal.getString("postalCodeExt"));
    						partySummary.put("primaryCountryGeoId", primaryPostal.getString("countryGeoId"));
    						partySummary.put("primaryStateProvinceGeoId", primaryPostal.getString("stateProvinceGeoId"));
    						partySummary.put("primaryCountyGeoId", primaryPostal.getString("countyGeoId"));
    						partySummary.put("primaryPostalCodeGeoId", primaryPostal.getString("postalCodeGeoId"));
    					}
    				}
        		}
        		
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        	Debug.logError(e, MODULE);
        }
        return partySummary;
    }
}
