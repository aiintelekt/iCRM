package org.fio.crm.opportunities;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.*;

/**
 * Opportunity utility methods.
 */
public final class UtilOpportunity {

    private UtilOpportunity() { }

    private static final String MODULE = UtilOpportunity.class.getName();
    
    /**
     * Helper method to get the principal account for an opportunity. This is a simplification of the
     * datamodel and should only be calld for non-critical uses. Returns null if no account was found,
     * which would be the case if there were a lead party Id instead.
     */
    public static String getOpportunityAccountPartyId(GenericValue opportunity) throws GenericEntityException {
        List<GenericValue> candidates = opportunity.getRelated("SalesOpportunityRole", UtilMisc.toMap("roleTypeId", "ACCOUNT"), null, false);
        if (candidates.size() == 0) {
            return null;
        }
        // we have two out of three primary keys, so the result is guaranteed to be the one with our partyId
        GenericValue salesOpportunityRole = candidates.get(0);
        return salesOpportunityRole.getString("partyId");
    }

    /**
     * Helper method to get the principal lead for an opportunity. This is a simplification of the
     * datamodel and should only be calld for non-critical uses. Returns null if no lead was found,
     * which would be the case if there were an account party Id instead.
     */
    public static String getOpportunityLeadPartyId(GenericValue opportunity) throws GenericEntityException {
        List<GenericValue> candidates = opportunity.getRelated("SalesOpportunityRole", UtilMisc.toMap("roleTypeId", "PROSPECT"), null, false);
        if (candidates.size() == 0) {
            return null;
        }
        // we have two out of three primary keys, so the result is guaranteed to be the one with our partyId
        GenericValue salesOpportunityRole = candidates.get(0);
        return salesOpportunityRole.getString("partyId");
    }

    
    /**
     * Helper method to get all account party Id's for an opportunity. This is a more serious version of the above
     * for use in critical logic, such as security or in complex methods that should use the whole list from the beginning.
     */
    public static List<String> getOpportunityAccountPartyIds(Delegator delegator, String salesOpportunityId) throws GenericEntityException {
        return getOpportunityPartiesByRole(delegator, salesOpportunityId, "ACCOUNT");
    }

    /** Helper method to get all lead party Id's for an opportunity. See comments for getOpportunityAccountPartyIds(). */
    public static List<String> getOpportunityLeadPartyIds(Delegator delegator, String salesOpportunityId) throws GenericEntityException {
        return getOpportunityPartiesByRole(delegator, salesOpportunityId, "PROSPECT");
    }

    /** Helper method to get all contact party Id's for an opportunity.  */
    public static List<String> getOpportunityContactPartyIds(Delegator delegator, String salesOpportunityId) throws GenericEntityException {
        return getOpportunityPartiesByRole(delegator, salesOpportunityId, "CONTACT");
    }

    /** Helper method to get all party Id's of a given role for an opportunity. It's better to use one of the more specific methods above. */
    public static List<String> getOpportunityPartiesByRole(Delegator delegator, String salesOpportunityId, String roleTypeId) throws GenericEntityException {
        List<GenericValue> maps = delegator.findByAnd("SalesOpportunityRole", UtilMisc.toMap("roleTypeId", roleTypeId, "salesOpportunityId", salesOpportunityId), null, false);
        List<String> results = new ArrayList<String>();
        for (Iterator<GenericValue> iter = maps.iterator(); iter.hasNext();) {
            GenericValue map = iter.next();
            results.add(map.getString("partyId"));
        }
        return results;
    }

}

