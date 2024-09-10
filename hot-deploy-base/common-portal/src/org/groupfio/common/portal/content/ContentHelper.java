package org.groupfio.common.portal.content;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * Content Helper methods which are designed to provide a consistent set of APIs that can be reused by
 * higher level services.
 *
 * @author     <a href="mailto:leon@opensourcestrategies.com">Leon Torres</a>
 */
public final class ContentHelper {

    private ContentHelper() { }

    private static final String MODULE = ContentHelper.class.getName();

    /**
     * Gets all active content metadata for a given CRMSFA party with the specified role and purpose.
     * This is useful for listing the content associated with the party.  By default, the
     * contentPurposeEnumId is PTYCNT_CRMSFA.  Return values are a List of ContentAndRole.
     * @param partyId the Party Id
     * @param roleTypeId the Party role type
     * @param contentPurposeEnumId a <code>String</code> value
     * @param delegator a <code>Delegator</code> value
     * @return a <code>List</code> value
     * @exception GenericEntityException if an error occurs
     */
    public static List<GenericValue> getContentInfoForParty(String partyId, String roleTypeId, String contentPurposeEnumId, Delegator delegator) throws GenericEntityException {

        // First get the PartyContent with the desired purpose and build a list of contentIds from it
        //List<GenericValue> contents = delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId", partyId, "contentPurposeEnumId", contentPurposeEnumId),null,false);
        List<GenericValue> contents = delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId", partyId),null,false);

        if (contents.size() == 0) {
            return FastList.newInstance();
        }

        Set<String> contentIds = new HashSet<String>();
        for (GenericValue content : contents) {
            contentIds.add(content.getString("contentId"));
        }

        // get the unexpired contents for the party in the given role
        EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("contentId", EntityOperator.IN, contentIds),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId),
                    EntityUtil.getFilterByDateExpr());
        return delegator.findList("ContentAndRole", conditions, null, null,null,false);
    }

    /**
     * As above but specifically the default PTYCNT_CRMSFA content.
     * @param partyId the Party Id
     * @param roleTypeId the Party role type
     * @param delegator a <code>Delegator</code> value
     * @return list of entities
     * @throws GenericEntityException if an error occurs
     */
    public static List<GenericValue> getContentInfoForParty(String partyId, String roleTypeId, Delegator delegator) throws GenericEntityException {
        return getContentInfoForParty(partyId, roleTypeId, "PTYCNT_CRMSFA", delegator);
    }

    /**
     * Gets all active content metadata for a given Case.
     * @param custRequestId Id of the Case
     * @param delegator a <code>Delegator</code> value
     * @return list of entities
     * @throws GenericEntityException if an error occurs
    */
    /*public static List<GenericValue> getContentInfoForCase(String custRequestId, Delegator delegator) throws GenericEntityException {
        return delegator.findByAnd("ContentAndCustRequest", Arrays.asList(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId), EntityUtil.getFilterByDateExpr()),null,false);
               delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId", partyId, "contentPurposeEnumId", contentPurposeEnumId),null,false);

    }
*/
    /**
     * Gets all active content metadata for a given Opportunity.
     * @param salesOpportunityId Id of the Opportunity
     * @param delegator a <code>Delegator</code> value
     * @return list of entities
     * @throws GenericEntityException if an error occurs
    */
    /*public static List<GenericValue> getContentInfoForOpportunity(String salesOpportunityId, Delegator delegator) throws GenericEntityException {
        return delegator.findByAnd("ContentAndSalesOpportunity", Arrays.asList(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, salesOpportunityId), EntityUtil.getFilterByDateExpr()));
    }*/


   
}
