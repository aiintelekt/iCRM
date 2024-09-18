package org.fio.crm.activities;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.fio.crm.party.PartyHelper;

/**
 * Activities helper methods.
 */
public final class ActivitiesHelper {

    private ActivitiesHelper() { }

    private static final String MODULE = ActivitiesHelper.class.getName();
    public static final List<String> ACTIVITY_WORKEFFORT_IDS = UtilMisc.toList("TASK", "EVENT");
    public static final String crmsfaProperties = "crmsfa";

    /**
     * Retrieve the internal partyIds involved with a workEffort.
     * @param workEffortId
     * @param delegator
     * @return List of partyIds
     */
    public static List<String> findInternalWorkeffortPartyIds(String workEffortId, Delegator delegator) {
        List<String> workEffortRoles = UtilMisc.toList("CAL_OWNER", "CAL_ATTENDEE");
        List<String> internalPartyIds = new ArrayList<String>();
        try {
            EntityCondition conditionsWorkEfft = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
                    EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, workEffortRoles),
                    EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
                    EntityUtil.getFilterByDateExpr());
            
            List<GenericValue> assignedParties = delegator.findList("WorkEffortPartyAssignment", conditionsWorkEfft, null, null, null, false);
            List<String> assignedPartyIds = EntityUtil.getFieldListFromEntityList(assignedParties, "partyId", true);
            if (assignedPartyIds != null) {
                for (String partyId : assignedPartyIds) {
                    EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
                                                       EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                                                       EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, PartyHelper.TEAM_MEMBER_ROLES));
                    List<GenericValue> roles = delegator.findList("PartyRole", conditions, null, null, null, false);
                    if (roles != null && roles.size() > 0) {
                        internalPartyIds.add(partyId);
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to retrieve internal workEffort roles for workEffort: " + workEffortId, MODULE);
        }
        return internalPartyIds;
    }

    public static String getEmailSubjectCaseFormatRegExp() {
        String emailSubjectCaseFormatRegExp = UtilProperties.getPropertyValue(crmsfaProperties, "crmsfa.case.emailSubjectCaseFormat.regExp", "\\\\[Case:(.*?)\\\\]");
        return emailSubjectCaseFormatRegExp.replaceAll("\\\\\\\\", "\\");
    }

    public static String getEmailSubjectOrderFormatRegExp() {
        String emailSubjectOrderFormatRegExp = UtilProperties.getPropertyValue(crmsfaProperties, "crmsfa.order.emailSubjectOrderFormat.regExp", "\\\\[Order:(.*?)\\\\]");
        return emailSubjectOrderFormatRegExp.replaceAll("\\\\\\\\", "\\");
    }

    public static String getEmailSubjectCaseString(String caseId) {
        String emailSubjectCaseString = UtilProperties.getPropertyValue(crmsfaProperties, "crmsfa.case.emailSubjectCaseFormat", "[Case:${caseId}]");
        return emailSubjectCaseString.replaceAll("\\$\\{caseId\\}", caseId);
    }

    public static String getEmailSubjectOrderString(String orderId) {
        String emailSubjectOrderString = UtilProperties.getPropertyValue(crmsfaProperties, "crmsfa.order.emailSubjectOrderFormat", "[Order:${orderId}]");
        return emailSubjectOrderString.replaceAll("\\$\\{orderId\\}", orderId);
    }

    public static List<String> getCustRequestIdsFromCommEvent(GenericValue communicationEvent, Delegator delegator) throws GenericEntityException {
        return getCustRequestIdsFromString(communicationEvent.getString("subject"), delegator);
    }

    public static List<String> getCustRequestIdsFromString(String parseString, Delegator delegator) throws GenericEntityException {
        String getEmailSubjectCaseFormatRegExp = getEmailSubjectCaseFormatRegExp();
        Set<String> custRequestIds = new TreeSet<String>();

        if (UtilValidate.isNotEmpty(parseString)) {
            Pattern pattern = Pattern.compile(getEmailSubjectCaseFormatRegExp);
            Matcher matcher = pattern.matcher(parseString);
            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    custRequestIds.add(matcher.group(1));
                }
            }
        }

        if (UtilValidate.isEmpty(custRequestIds)) {
            return new ArrayList<String>();
        }

        // Filter the retrieved custRequestIds against existing CustRequest entities
        List<GenericValue> custRequests = delegator.findList("CustRequest", EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestIds), null, null, null, false);
        List<String> validCustRequestIds = EntityUtil.getFieldListFromEntityList(custRequests, "custRequestId", true);

        if (UtilValidate.isEmpty(custRequests)) {
            return new ArrayList<String>();
        }

        return validCustRequestIds;
    }
}
