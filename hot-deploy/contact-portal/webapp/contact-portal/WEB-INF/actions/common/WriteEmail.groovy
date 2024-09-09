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

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.fio.crm.party.PartyHelper;
import org.fio.crm.util.UtilCommon;
import org.ofbiz.entity.GenericValue;
import org.fio.crm.contactmech.PartyPrimaryContactMechWorker;

userLogin = request.getAttribute("userLogin");

//csr details
csr = delegator.findOne("Person", UtilMisc.toMap("partyId", userLogin.getString("partyId")) ,false);
context.put("csr", csr);
csrContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, userLogin.getString("partyId"));
context.put("csrContactInformation", csrContactInformation);
try {
    ptiConditionList = UtilMisc.toList(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "RECEIVE_EMAIL_OWNER"));
    ptiConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, "admin"));
    ptiConditionList.add(EntityUtil.getFilterByDateExpr());
    ptiCondition = EntityCondition.makeCondition(ptiConditionList, EntityOperator.AND);

	partyContactMechPurpose = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose").where(mainConditon).queryFirst();
    if (UtilValidate.isNotEmpty(partyContactMechPurpose)) {
        contactMech = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", partyContactMechPurpose.getString("contactMechId")).queryFirst();
        context.put("contactMech", contactMech);
    }


} catch (Exception e) {
  Debug.log("Exception in Write Email groovy"+e.getMessage());
}

userEmailAddresses = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, userLogin.getString("partyId"), null);
if (UtilValidate.isNotEmpty(userEmailAddresses)) {
    context.put("userEmailAddresses", userEmailAddresses);
}

communicationEvent = null;

// communicationEvent from parameter
if (parameters.containsKey("communicationEventId")) {
    communicationEventId = parameters.get("communicationEventId");
    workEffortId = parameters.get("workEffortId");

    if (UtilValidate.isNotEmpty(workEffortId) && UtilValidate.isNotEmpty(communicationEventId)) {

        communicationEvent = delegator.findOne("CommunicationEvent", UtilMisc.toMap("communicationEventId", communicationEventId), true);

        workEffortEntity = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), true);
        workEffort = workEffortEntity.getAllFields();

        // get the associated opportunity
        opportunities = workEffortEntity.getRelated("SalesOpportunityWorkEffort");
        if (opportunities.size() > 0) {
            salesOpportunityId = opportunities.get(0).getString("salesOpportunityId");
            workEffort.put("salesOpportunityId", salesOpportunityId);
        }
        // get the associated case
        cases = workEffortEntity.getRelated("CustRequestWorkEffort");
        if (cases.size() > 0) {
            custRequestId = cases.get(0).getString("custRequestId");
            workEffort.put("custRequestId", custRequestId);
        }
    }
} else {
    // if the communicationEvent exists, then we were passed a pending email (check for pending should already have been done to render this page)
    communicationEvent = context.get("communicationEvent");
    workEffort = context.get("workEffort");
}

if (communicationEvent != null) {
    // fill in communicationEvent fields if the parameter is empty
    map = communicationEvent.getAllFields();
    for (iter = map.keySet().iterator(); iter.hasNext();) {
        key = iter.next();
        value = parameters.get(key);
        if (UtilValidate.isEmpty(value)) {
            parameters.put(key, map.get(key));
            if (key == "subject") {
                caseIdParse = (String) map.get(key);
                if (caseIdParse != null && caseIdParse != "" && caseIdParse.lastIndexOf("[") > 0 && caseIdParse.lastIndexOf("]") > 0) {
                    caseIdParse = caseIdParse.substring(caseIdParse.lastIndexOf("[") + 1, caseIdParse.lastIndexOf("]"));
                    parameters.put("custRequestId", caseIdParse);
                }
            }

        }
    }

    // check if we are replying or forwarding the email
    isReply = false;
    isForward = false;
    if (parameters.containsKey("action")) {
        if (parameters.get("action").equals("reply")) {
            isReply = true;
        } else if (parameters.get("action").equals("forward")) {
            isForward = true;
        }
        // need to clear communicationEventId workEffortId from parameters as they are used for saved emails
        parameters.put("communicationEventId", "");
        parameters.put("workEffortId", "");
        parameters.put("origCommEventId", communicationEventId);
    }

    // remaining fields we might have to reload
    fromEmail = parameters.get("fromEmail");
    toEmail = parameters.get("toEmail");
    ccEmail = parameters.get("ccEmail");
    bccEmail = parameters.get("bccEmail");
    salesOpportunityId = parameters.get("salesOpportunityId");
    custRequestId = parameters.get("custRequestId");
    orderId = parameters.get("orderId");

    // we also need the from and to emails
    if (UtilValidate.isEmpty(fromEmail)) {
        fromContactMech = communicationEvent.getRelatedOne("FromContactMech");
        if (fromContactMech != null) {
            fromEmail = fromContactMech.getString("infoString");
        } else if (UtilValidate.isNotEmpty(communicationEvent.getString("fromString"))) {
            fromEmail = communicationEvent.getString("fromString");
        }
        // if fromEmail is given from the form then it cannot be empty and then we also have toEmail
        toContactMech = communicationEvent.getRelatedOne("ToContactMech");
        if (toContactMech != null) {
            toEmail = toContactMech.getString("infoString");
        } else if (UtilValidate.isNotEmpty(communicationEvent.getString("toString"))) {
            toEmail = communicationEvent.getString("toString");
        }
    }

    if (isReply) {
        parameters.put("toEmail", fromEmail);
        parameters.put("fromEmail", toEmail);
    } else {
        parameters.put("fromEmail", fromEmail);
        if (!isForward) {
            parameters.put("toEmail", toEmail);
        }
    }

    // set the association IDs if the field is empty
    if (workEffort != null) {
        if (UtilValidate.isEmpty(salesOpportunityId) && workEffort.containsKey("salesOpportunityId")) {
            parameters.put("salesOpportunityId", workEffort.get("salesOpportunityId"));
        }
        if (UtilValidate.isEmpty(custRequestId) && workEffort.containsKey("custRequestId")) {
            parameters.put("custRequestId", workEffort.get("custRequestId"));
        }
        if (UtilValidate.isEmpty(orderId) && workEffort.containsKey("orderId")) {
            parameters.put("orderId", workEffort.get("orderId"));
        }
    }

    // change the subject | email body if is it a reply or a forward
    subject = parameters.get("subject");
    if (isReply) {
        prefix = UtilProperties.getMessage("CRMSFAUiLabels", "CrmEmailRe", locale) + " ";
        subjectTemp = subject.toLowerCase();
        if (!subjectTemp.matches("^" + prefix.toLowerCase() + ".*")) {
            if (subject.indexOf("Ticket ID") < 0 && workEffort.get("custRequestId") != null)
                parameters.put("subject", prefix + subject + " - Ticket ID [" + workEffort.get("custRequestId") + "]");
            else
                parameters.put("subject", prefix + subject);
        }
    } else if (isForward) {
        prefix = UtilProperties.getMessage("crmUiLabels", "CrmEmailFwd", locale) + " ";
        subjectTemp = subject.toLowerCase();
        if (!subjectTemp.matches("^" + prefix.toLowerCase() + ".*")) {
            if (subject.indexOf("Ticket ID") < 0 && workEffort.get("custRequestId") != null)
                parameters.put("subject", prefix + subject + " - Ticket ID [" + workEffort.get("custRequestId") + "]");
            else
                parameters.put("subject", prefix + subject);
        }
    }
    // change the email body - happily FCKEditor seems to deal with the text wrapping without help
    if (isReply || isForward) {
        lineSep = "<br/>";
        content = parameters.get("content");
        if (UtilValidate.isNotEmpty(content)) {
            // quoting
            //content = content.replaceAll("(?m)^", UtilProperties.getMessage("CRMSFAUiLabels", "CrmEmailQuotedLinePrefix", locale));
            content = content.replaceAll("[\\n\\r]+", lineSep);
            // add original sender name and address
            wroteLabel = UtilProperties.getMessage("crmUiLabels", "CrmWroteEmail", locale);
            content = UtilProperties.getMessage("crmUiLabels", "CrmEmailQuotedHeader", UtilMisc.toMap("fromEmail", fromEmail, "wroteLabel", wroteLabel, "lineSeparator", lineSep), locale) + content;
            parameters.put("content", content);
            // the contentMimeTypeId of the communication event is usually text/plain, but the default setting for the editor is text/html,
            // so we override the contentMimeTypeId here, otherwise the recepient will get a text/plain email containing HTML
            parameters.put("contentMimeTypeId", "text/html");
        }
    }

    context.put("ccAddresses", communicationEvent.getString("ccString"));
    context.put("bccAddresses", communicationEvent.getString("bccString"));

    // Get all the attachments of the email
    attachments = delegator.findByAnd("CommEventContentDataResource", UtilMisc.toMap("communicationEventId", communicationEvent.getString("communicationEventId")));
    attachments = EntityUtil.filterByDate(attachments);
    context.put("attachments", attachments);

    return; // we're done
}

// otherwise we're creating a fresh email

// if a contactMechIdTo is passed in, this is the email we're writing to
contactMechIdTo = parameters.get("contactMechIdTo");
if (UtilValidate.isNotEmpty(contactMechIdTo)) {
    contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", contactMechIdTo), false);
    if (contactMech != null) {
        parameters.put("contactMechIdTo", contactMechIdTo);
        parameters.put("toEmail", contactMech.getString("infoString"));
    }
} else {
    // if there's a toEmail in the parameter but no contactMechIdTo, put it back in the context (happens when user types in an email and then switches input mode)
    parameters.put("toEmail", parameters.get("toEmail"));
}

// If a mergeFormId (template) parameter is provided, merge the form with the party (if any) and provide the result to the context
partyIdTo = parameters.get("partyIdTo");
if (UtilValidate.isEmpty(partyIdTo)) partyIdTo = parameters.get("internalPartyId");
orderId = parameters.get("orderId");
shipGroupSeqId = parameters.get("shipGroupSeqId");
shipmentId = parameters.get("shipmentId");
mergeFormId = parameters.get("mergeFormId");
if (UtilValidate.isNotEmpty(mergeFormId)) {
    mergedOutput = PartyHelper.mergePartyWithForm(delegator, mergeFormId, partyIdTo, orderId, shipGroupSeqId, shipmentId, UtilMisc.ensureLocale(UtilHttp.getLocale(request)), true, UtilCommon.getTimeZone(request));
    context.put("templateText", mergedOutput.get("mergeFormText"));
    if (UtilValidate.isEmpty(parameters.get("subject"))) parameters.put("subject", mergedOutput.get("subject"));
}

// if a internalPartyId or partyId is passed in, put it back in the context
partyId = parameters.get("internalPartyId");
if (UtilValidate.isEmpty(partyId)) {
    partyId = parameters.get("partyId");
}
context.put("partyId", partyId);

// determine if marketing campaign templates should be displayed
boolean displayTemplates = false;
if (security.hasEntityPermission("CRMSFA_FORMLTR", "_VIEW", userLogin)) {
    displayTemplates = true;
}
context.put("displayTemplates", displayTemplates);

actionFrom = (String) parameters.get("actionFrom");

custRequestId = (String) parameters.get("custRequestId");
if (UtilValidate.isNotEmpty(custRequestId)) {
    custRequestGen = delegator.findByPrimaryKey("CustRequest", UtilMisc.toMap("custRequestId", custRequestId));
    if (UtilValidate.isNotEmpty(custRequestGen)) {

        if (actionFrom != null && actionFrom.equals("reply"))
            prefix = UtilProperties.getMessage("crmUiLabels", "CrmEmailRe", locale) + " ";
        else if (actionFrom != null && actionFrom.equals("forward"))
            prefix = UtilProperties.getMessage("crmUiLabels", "CrmEmailFwd", locale) + " ";
        else
            prefix = "";

        String subject1 = custRequestGen.getString("custRequestName");
        if (subject1.indexOf("Ticket ID") < 0)
            subject = prefix + subject1 + " - Ticket ID [" + custRequestId + "]";
        else
            subject = subject1;

        parameters.put("subject", subject);
    }

}

workEffortPurposeTypeId = (String) parameters.get("workEffortPurposeTypeId");

if (UtilValidate.isNotEmpty(custRequestId)) {
    custRequestGen = delegator.findByPrimaryKey("CustRequest", UtilMisc.toMap("custRequestId", custRequestId));
    if (UtilValidate.isNotEmpty(custRequestGen) && ((actionFrom != null && actionFrom.equals("reply")) || (workEffortPurposeTypeId != null))) {
        String partyId = (String) custRequestGen.getString("fromPartyId");
        String emailTo = null;
        String contactMechId = null;
        Map sendResp = null;
        try {

            List PartyContactMechPurpose = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"), null, false);
            if ((PartyContactMechPurpose != null) && (PartyContactMechPurpose.size() > 0)) {
                Iterator obj = PartyContactMechPurpose.iterator();
                while (obj.hasNext()) {
                    GenericValue gv = (GenericValue) obj.next();
                    contactMechId = gv.getString("contactMechId");
                    break;
                }

            }

            Debug.logInfo("contactMechIdcontactMechId" + contactMechId, "");
            if (contactMechId != null && contactMechId != "") {
                GenericValue ContactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", contactMechId), false);
                if (ContactMech != null) {
                    toEmail = ContactMech.getString("infoString");
                    parameters.put("toEmail", toEmail);
                    Debug.logInfo("----toEmail-------" + toEmail, "");
                    context.put("toEmail", toEmail);
                }
            }

        } catch (Exception e) {}
    }

}

partyId = parameters.get("partyId");
if (UtilValidate.isNotEmpty(partyId)) {
    String emailTo = null;
    String contactMechId = null;
    Map sendResp = null;
    try {

        List PartyContactMechPurpose = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"), null, false);
        if ((PartyContactMechPurpose != null) && (PartyContactMechPurpose.size() > 0)) {
            Iterator obj = PartyContactMechPurpose.iterator();
            while (obj.hasNext()) {
                GenericValue gv = (GenericValue) obj.next();
                contactMechId = gv.getString("contactMechId");
                break;
            }

        }

        Debug.logInfo("contactMechIdcontactMechId" + contactMechId, "");
        if (contactMechId != null && contactMechId != "") {
            GenericValue ContactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", contactMechId), false);
            if (ContactMech != null) {
                toEmail = ContactMech.getString("infoString");
                parameters.put("toEmail", toEmail);
                Debug.logInfo("----toEmail-------" + toEmail, "");
                context.put("toEmail", toEmail);
            }
        }

    } catch (Exception e) {}
    context.put("partyId", partyId);
}


try {
    ptiConditionList = UtilMisc.toList(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "RECEIVE_EMAIL_OWNER"));
    ptiConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, "admin"));
    ptiCondition = EntityCondition.makeCondition(ptiConditionList, EntityOperator.AND);

    partyContactMechPurpose = EntityUtil.getFirst(EntityUtil.filterByDate(delegator.findList("PartyContactMechPurpose", ptiCondition, null, null, null, false), true));

    if (UtilValidate.isNotEmpty(partyContactMechPurpose)) {
        contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", partyContactMechPurpose.getString("contactMechId")), false);
        context.put("contactMech", contactMech);
    }



} catch (Exception e) {}



attrName = "PARTY_SIGNATURE";
userLogin = request.getAttribute("userLogin");
if (userLogin != null) {
    partyId1 = userLogin.getString("partyId");
}
if (UtilValidate.isNotEmpty(partyId)) {
   String partyIdentification = org.fio.homeapps.util.DataUtil.getPartyIdentificationValue(delegator, partyId1, attrName);
	 if (UtilValidate.isNotEmpty(partyIdentification)) {
        context.put("attrvalue", partyIdentification);
    }
}