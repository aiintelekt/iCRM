package org.fio.crm.party;

import java.util.*;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * This class is a series of convenience methods to help extract particular types of contact information, so it's
 * easier to get the Primary Phone Number, Email Address, Postal Address of a party without having to work with the
 * highly normalized OFBIZ contact data model.  Mostly to help out with form widgets.
 *
 * @author sichen@opensourcestrategies.com
 *
 */
public final class PartyContactHelper {

    private PartyContactHelper() { }

    private static final String MODULE = PartyContactHelper.class.getName();

    /**
     * Same as above but only returns active electronic addresses.
     * The point of this method is to get the ContactMech.infoString which is the value for any ContactMech of the type of the electronic address.
     * For example, fetching the contact mech type WEB_ADDRESS with purpose PRIMARY_WEB_URL might result in "http://example.domain"
     * Returns the infoString from the first one of specified type/purpose
     * @param partyId the party ID
     * @param contactMechTypeId the contact mech type ID
     * @param contactMechPurposeTypeId purpose of electronic address
     * @param delegator a <code>Delegator</code> value
     * @return the first matching electronic address string
     * @throws GenericEntityException if an error occurs
     */
    public static String getElectronicAddressByPurpose(String partyId, String contactMechTypeId, String contactMechPurposeTypeId, Delegator delegator) throws GenericEntityException {
        return getElectronicAddressByPurpose(partyId, contactMechTypeId, contactMechPurposeTypeId, true, delegator);
    }
    
    /**
     * The point of this method is to get the ContactMech.infoString which is the value for any ContactMech of the type of the electronic address.
     * For example, fetching the contact mech type WEB_ADDRESS with purpose PRIMARY_WEB_URL might result in "http://example.domain"
     * Returns the infoString from the first one of specified type/purpose
     * @param partyId the party ID
     * @param contactMechTypeId the contact mech type ID
     * @param contactMechPurposeTypeId purpose of electronic address
     * @param getActiveOnly flag to return only the currently active electronic addresses
     * @param delegator a <code>Delegator</code> value
     * @return the first matching electronic address string
     * @throws GenericEntityException if an error occurs
     */
    public static String getElectronicAddressByPurpose(String partyId, String contactMechTypeId, String contactMechPurposeTypeId, boolean getActiveOnly, Delegator delegator) throws GenericEntityException {

        List<GenericValue> possibleAddresses = getContactMechsByPurpose(partyId, contactMechTypeId, contactMechPurposeTypeId, getActiveOnly, delegator);
        if ((possibleAddresses != null) && (possibleAddresses.size() > 0)) {
            GenericValue contactMech = possibleAddresses.get(0);
            if (contactMech != null) {
                return contactMech.getString("infoString");
            } else {
                Debug.log("No [" + contactMechTypeId + "] related to partyId [" + partyId + "] with purpose [" + contactMechPurposeTypeId + "] and getActiveOnly = [" + getActiveOnly + "]");
            }
        }

        return null;
    }
    
    /**
     * This method returns a GenericValue rather than a String because a PostalAddress is fairly complicated, and the user may want to
     * format it himself in a FTL page.
     * @param partyId the party ID
     * @param contactMechPurposeTypeId purpose of postal address
     * @param getActiveOnly flag to return only the currently active electronic addresses
     * @param delegator a <code>Delegator</code> value
     * @return First PostalAddress of the specified contactMechPurposeTypeId
     * @throws GenericEntityException if an error occurs
     */
    public static GenericValue getPostalAddressValueByPurpose(String partyId, String contactMechPurposeTypeId, boolean getActiveOnly, Delegator delegator) throws GenericEntityException {
        List<GenericValue> possibleAddresses = getContactMechsByPurpose(partyId, "POSTAL_ADDRESS", contactMechPurposeTypeId, getActiveOnly, delegator);

        if ((possibleAddresses != null) && (possibleAddresses.size() > 0)) {
            GenericValue contactMech = possibleAddresses.get(0).getRelatedOne("ContactMech");
            if (contactMech != null) {
                return contactMech.getRelatedOne("PostalAddress");
            } else {
                Debug.log("No Postal Address related to partyId [" + partyId + "] with purpose [" + contactMechPurposeTypeId + "] and getActiveOnly = [" + getActiveOnly + "]");
            }
        }
        return null;
    }
    
    /**
     * This is the base method and returns a List of PartyContactWithPurpose for the chosen parameters.  getActiveOnly
     * @param partyId the party ID
     * @param contactMechTypeId the contact mech type ID
     * @param contactMechPurposeTypeId will be used if not null
     * @param getActiveOnly get only active ones (filter out expired contacts and expired contact purposes)
     * @param delegator a <code>Delegator</code> value
     * @return the list of contact mech <code>GenericValue</code> for the given party id matching the given purpose ID
     * @throws GenericEntityException if an error occurs
     */
    public static List<GenericValue> getContactMechsByPurpose(String partyId, String contactMechTypeId, String contactMechPurposeTypeId, boolean getActiveOnly, Delegator delegator) throws GenericEntityException {
        List<EntityCondition> conditions = UtilMisc.<EntityCondition>toList(EntityCondition.makeCondition("partyId", partyId));
        if (contactMechTypeId != null) {
            conditions.add(EntityCondition.makeCondition("contactMechTypeId", contactMechTypeId));
        }
        if (contactMechPurposeTypeId != null) {
            conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId));
        }
        if (getActiveOnly) {
            conditions.add(EntityUtil.getFilterByDateExpr("contactFromDate", "contactThruDate"));
            conditions.add(EntityUtil.getFilterByDateExpr("purposeFromDate", "purposeThruDate"));
        }
        List<GenericValue> potentialContactMechs = delegator.findList("PartyContactWithPurpose", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("contactFromDate DESC"), null, false);

        return potentialContactMechs;
    }
    
    /**
     * Provides a list of partyIds matching any email addresses in the input string. The input string is split by the provided delimiter, if provided.
     * @param delegator a <code>Delegator</code> value
     * @param possibleEmailString a list of email addresses separated by the given delimiter
     * @param delimiter to split the given possibleEmailString
     * @return a list of partyIds matching any email addresses in the input string
     * @throws GenericEntityException if an error occurs
     */
    public static List<String> getPartyIdsMatchingEmailsInString(Delegator delegator, String possibleEmailString, String delimiter) throws GenericEntityException {
        Set<String> partyIds = new LinkedHashSet<String>();
        String[] possibleEmails = {possibleEmailString};
        if (delimiter != null) {
            possibleEmails = possibleEmailString.split("\\s*" + delimiter + "\\s*");
        }
        for (String possibleEmail : possibleEmails) {
            EntityCondition filterConditions = EntityCondition.makeCondition(EntityOperator.AND,
                  EntityCondition.makeCondition("infoString", possibleEmail),
                  EntityUtil.getFilterByDateExpr());
            List<GenericValue> pcms = delegator.findList("PartyAndContactMech", filterConditions, null, Arrays.asList("fromDate DESC"), null, false);
            if (pcms != null) {
                partyIds.addAll(EntityUtil.<String>getFieldListFromEntityList(pcms, "partyId", false));
            }
        }
        return UtilMisc.toList(partyIds);
    }
}
