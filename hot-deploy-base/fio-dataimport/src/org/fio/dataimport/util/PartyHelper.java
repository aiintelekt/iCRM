package org.fio.dataimport.util;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

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
    
}
