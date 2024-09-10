package org.fio.crm.activities;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.fio.crm.opportunities.UtilOpportunity;
import org.fio.crm.party.PartyHelper;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.fio.crm.util.UtilCommon;
import org.fio.crm.util.UtilMessage;

/**
 * Activities services. The service documentation is in services_activities.xml.
 */
public final class ActivitiesServices {

    private ActivitiesServices() {}

    private static final String MODULE = ActivitiesServices.class.getName();
    public static final String resource = "CRMSFAUiLabels";
    public static final String crmsfaProperties = "crm";

    public static Map < String, Object > sendActivityEmail(DispatchContext dctx, Map < String, Object > context) {
        return sendOrSaveEmailHelper(dctx, context, true, "CrmErrorSendEmailFail");
    }

    /**
     * Saving and sending are very complex services that are nearly identical in most ways.
     * There are four things that break the identity in minor ways that can be handled with
     * booleans. The four things are: Send new email, send existing email, save new email,
     * and send existing email. Instead of creating four separate methods several hundred
     * lines each, we do everything here.
     */
    @SuppressWarnings("unchecked")
    private static Map < String, Object > sendOrSaveEmailHelper(DispatchContext dctx, Map < String, Object > context, boolean sending, String errorLabel) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        // use the toEmail and internalPartyId to find the contactMechIdTo
        String toEmail = (String) context.get("toEmail");
        // if the email exists already, these will be set
        String communicationEventId = (String) context.get("communicationEventId");
        String workEffortId = (String) context.get("workEffortId");
        boolean existing = ((communicationEventId == null) || communicationEventId.equals("") ? false : true);
        String origCommEventId = (String) context.get("origCommEventId");
        String partyId = (String) context.get("partyId");
        String action = (String) context.get("action");

        try {

            String serviceName = (existing ? "updateCommunicationEvent" : "createCommunicationEvent");
            ModelService service = dctx.getModelService(serviceName);
            Map < String, Object > input = service.makeValid(context, "IN");
            
            Map results = ServiceUtil.returnSuccess();
            results.put("partyId", partyId);
            
            // validate the associations
            /*Map < String, Object > serviceResults = validateWorkEffortAssociations(dctx, context);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, errorLabel, locale, MODULE);
            }*/

            // Retrieve, validate and parse the To addresses (assumed to be comma-delimited)
            String validToAddresses = null;
            Set < String > toAddresses = UtilCommon.getValidEmailAddressesFromString(toEmail);
            if (UtilValidate.isNotEmpty(toAddresses)) {
                validToAddresses = StringUtil.join(UtilMisc.toList(toAddresses), ",");
                input.put("toString", validToAddresses);
            } else {
                if (UtilValidate.isNotEmpty(toEmail)) {
                    Debug.logError("No valid email addresses could be found from: [" + toEmail + "]", MODULE);
                }
            }

            // Search for contactMechIdTo using the passed in To email addresses - use the first found
            EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND, EntityCondition.makeCondition("infoString", EntityOperator.IN, UtilMisc.toList(toAddresses)),
                EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, "_NA_"));
            GenericValue partyContactMechTo = EntityUtil.getFirst(EntityUtil.filterByDate(delegator.findList("PartyAndContactMech", conditions, null, UtilMisc.toList("fromDate DESC"), null, false)));
            if (UtilValidate.isNotEmpty(partyContactMechTo)) {
                input.put("contactMechIdTo", partyContactMechTo.getString("contactMechId"));
                input.put("partyIdTo", partyContactMechTo.getString("partyId"));
                input.put("roleTypeIdTo", partyContactMechTo.getString("roleTypeId"));
            }

            /*
             * We're done with validation, now we begin the complex task of creating comm events, workefforts, and updating them
             * so that the email is sent or saved properly. The most verbose way of doing this requires four different methods with
             * a lot of redundant code. But here we use two booleans "existing" and "sending" to decide what to do. This is much
             * more compact but can be prone to subtle state errors. On the other hand, changes to the form input require only one
             * change here instead of four changes.
             */

            // create a PENDING comm event or update the existing one; set the comm event data with the form input
            if (existing) {
                input.put("communicationEventId", communicationEventId);
            } else {
                input.put("entryDate", UtilDateTime.nowTimestamp());
            }
            input.put("contactMechTypeId", "EMAIL_ADDRESS");
            input.put("communicationEventTypeId", "EMAIL_COMMUNICATION");
            input.put("statusId", "COM_PENDING");
            input.put("partyIdFrom", userLogin.getString("partyId"));
            input.put("roleTypeIdFrom", PartyHelper.getFirstValidRoleTypeId(userLogin.getString("partyId"), PartyHelper.TEAM_MEMBER_ROLES, delegator));
            // check if an original CommunicationEvent Id was given
            if (UtilValidate.isNotEmpty(origCommEventId)) {
                input.put("origCommEventId", origCommEventId);
            }

            // Retrieve, validate and parse the CC and BCC addresses (assumed to be comma-delimited)
            String validCCAddresses = null;
            Set < String > ccAddresses = UtilCommon.getValidEmailAddressesFromString((String) context.get("ccEmail"));
            if (UtilValidate.isNotEmpty(ccAddresses)) {
                validCCAddresses = StringUtil.join(UtilMisc.toList(ccAddresses), ",");
                input.put("ccString", validCCAddresses);
            }
            String validBCCAddresses = null;
            Set < String > bccAddresses = UtilCommon.getValidEmailAddressesFromString((String) context.get("bccEmail"));
            if (UtilValidate.isNotEmpty(bccAddresses)) {
                validBCCAddresses = StringUtil.join(UtilMisc.toList(bccAddresses), ",");
                input.put("bccString", validBCCAddresses);
            }

            Map < String, Object > serviceResults = dispatcher.runSync(serviceName, input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, errorLabel, locale, MODULE);
            }

            // get the communication event id if the comm event was created
            if (!existing) {
                communicationEventId = (String) serviceResults.get("communicationEventId");
            }

            // Create the content etc. for each email attachment
            // multiPartMap is populated by the ServiceEventHandler with (we hope) the following keys for each uploaded file: uploadedFile_#; _uploadedFile_0_contentType; _uploadedFile_0_fileName
            Map < String, Object > multiPartMap = (Map < String, Object > ) context.get("multiPartMap");
            int fileCounter = 1;
            if (UtilValidate.isNotEmpty(multiPartMap)) {
                Iterator < String > mpit = multiPartMap.keySet().iterator();
                while (mpit.hasNext()) {
                    String key = mpit.next();

                    // Since the ServiceEventHandler adds all form inputs to the map, just deal with the ones matching the correct input name (eg. 'uploadedFile_0', 'uploadedFile_1', etc)
                    if (!key.startsWith("uploadedFile")) {
                        continue;
                    }

                    // Some browsers will submit an empty string for an empty input type="file", so ignore the ones that are empty
                    if (UtilValidate.isEmpty(multiPartMap.get(key))) {
                        continue;
                    }

                    ByteBuffer uploadedFile = (ByteBuffer) multiPartMap.get(key);
                    String uploadedFileName = (String) multiPartMap.get("_" + key + "_fileName");
                    String uploadedFileContentType = (String) multiPartMap.get("_" + key + "_contentType");

                    // Check to see that we have everything
                    if (UtilValidate.isEmpty(uploadedFileName)) {
                        continue; // not really a file if there is no name
                    } else if (UtilValidate.isEmpty(uploadedFile) || UtilValidate.isEmpty(uploadedFileContentType)) {
                        return UtilMessage.createAndLogServiceError("CrmErrorSendEmailMissingFileUploadData", locale, MODULE);
                    }

                    // Populate the context for the DataResource/Content/CommEventContentAssoc creation service
                    Map < String, Object > createContentContext = new HashMap < String, Object > ();
                    try {
                        createContentContext.put("userLogin", userLogin);
                        createContentContext.put("contentName", uploadedFileName);//fileName
                        createContentContext.put("uploadedFile", uploadedFile);
                        createContentContext.put("_uploadedFile_fileName", uploadedFileName);//fileName
                        createContentContext.put("_uploadedFile_contentType", uploadedFileContentType);//img/png

                        Map < String, Object > tmpResult = dispatcher.runSync("uploadFile", createContentContext);
                        if (ServiceUtil.isError(tmpResult)) {
                            return UtilMessage.createAndLogServiceError(tmpResult, "CrmErrorCreateContentFail", locale, MODULE);
                        }
                        String contentId = (String) tmpResult.get("contentId");
                        if (UtilValidate.isNotEmpty(contentId)) {
                            tmpResult = dispatcher.runSync("createCommEventContentAssoc", UtilMisc.toMap("contentId", contentId, "communicationEventId", communicationEventId,
                                "sequenceNum", new Long(fileCounter), "userLogin", userLogin));
                            if (ServiceUtil.isError(tmpResult)) {
                                return UtilMessage.createAndLogServiceError(tmpResult, "CrmErrorCreateContentFail", locale, MODULE);
                            }
                        } else {
                            return ServiceUtil.returnError("Upload file ran successfully for [" + uploadedFileName + "] but no contentId was returned");
                        }

                    } catch (GenericServiceException e) {
                        return UtilMessage.createAndLogServiceError(e, "CrmErrorCreateContentFail", locale, MODULE);
                    }
                    fileCounter++;
                }
            }

            if (sending) {

                Map < String, Object > sendMailContext = new HashMap < String, Object > ();
                sendMailContext.put("subject", context.get("subject"));
                String contactMechIdFrom = (String) context.get("contactMechIdFrom");
                
                GenericValue contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", contactMechIdFrom), false);
                String emailAddr = contactMech.getString("infoString");
                sendMailContext.put("sendFrom", emailAddr);
                sendMailContext.put("partyId", context.get("partyId"));
                sendMailContext.put("contentType", context.get("contentMimeTypeId"));

                String addCaseIdToSubject = UtilProperties.getPropertyValue(crmsfaProperties, "crmsfa.case.addCaseNumberToOutgoingEmails", "false");
                if ("true".equals(addCaseIdToSubject) || "Y".equals(addCaseIdToSubject)) {
                    String custRequestId = (String) context.get("custRequestId");
                    String subject = (String) context.get("subject");

                    Debug.logInfo("-----context---writeMail-----" + context, MODULE);

                    sendMailContext.put("subject", subject);
                }

                // Assemble the body and the attachments into a list of body parts
                List < Map < String, Object >> attachments = new ArrayList < Map < String, Object >> ();
                List < GenericValue > commEventContentDataResources = delegator.findByAnd("CommEventContentDataResource", UtilMisc.toMap("communicationEventId", communicationEventId), null, false);
                commEventContentDataResources = EntityUtil.filterByDate(commEventContentDataResources);
                Iterator < GenericValue > cecait = commEventContentDataResources.iterator();
                while (cecait.hasNext()) {
                    GenericValue commEventContentDataResource = cecait.next();
                    String dataResourceId = commEventContentDataResource.getString("dataResourceId");
                    String mimeTypeId = commEventContentDataResource.getString("drMimeTypeId");
                    String fileName = commEventContentDataResource.getString("drDataResourceName");
                    Map < String, Object > attachment = UtilMisc. < String, Object > toMap("type", mimeTypeId, "filename", fileName);
                    try {
                        ByteWrapper byteWrapper = UtilCommon.getContentAsByteWrapper(delegator, dataResourceId, null, null, locale, null);
                        attachment.put("content", byteWrapper.getBytes());
                    } catch (IOException e) {
                        return UtilMessage.createAndLogServiceError("CrmErrorSendEmailUnableToGetDataResource", UtilMisc.toMap("dataResourceId", dataResourceId), locale, MODULE);
                    } catch (GeneralException e) {
                        return UtilMessage.createAndLogServiceError("CrmErrorSendEmailUnableToGetDataResource", UtilMisc.toMap("dataResourceId", dataResourceId), locale, MODULE);
                    }
                    attachments.add(attachment);
                }

                // include the attachment for forward email [start]

                if (UtilValidate.isNotEmpty(action) && action.equals("forward") && UtilValidate.isNotEmpty(origCommEventId)) {
                    commEventContentDataResources = delegator.findByAnd("CommEventContentDataResource", UtilMisc.toMap("communicationEventId", origCommEventId), null, false);
                    commEventContentDataResources = EntityUtil.filterByDate(commEventContentDataResources);
                    cecait = commEventContentDataResources.iterator();
                    while (cecait.hasNext()) {
                        GenericValue commEventContentDataResource = cecait.next();
                        String dataResourceId = commEventContentDataResource.getString("dataResourceId");
                        String mimeTypeId = commEventContentDataResource.getString("drMimeTypeId");
                        String fileName = commEventContentDataResource.getString("drDataResourceName");
                        Map < String, Object > attachment = UtilMisc. < String, Object > toMap("type", mimeTypeId, "filename", fileName);
                        try {
                            ByteWrapper byteWrapper = UtilCommon.getContentAsByteWrapper(delegator, dataResourceId, null, null, locale, null);
                            attachment.put("content", byteWrapper.getBytes());
                        } catch (IOException e) {
                            return UtilMessage.createAndLogServiceError("CrmErrorSendEmailUnableToGetDataResource", UtilMisc.toMap("dataResourceId", dataResourceId), locale, MODULE);
                        } catch (GeneralException e) {
                            return UtilMessage.createAndLogServiceError("CrmErrorSendEmailUnableToGetDataResource", UtilMisc.toMap("dataResourceId", dataResourceId), locale, MODULE);
                        }
                        attachments.add(attachment);
                    }
                }

                // include the attachment for forward email [end]

                if (UtilValidate.isNotEmpty(validToAddresses)) {
                    sendMailContext.put("sendTo", validToAddresses);
                }
                if (UtilValidate.isNotEmpty(validCCAddresses)) {
                    sendMailContext.put("sendCc", validCCAddresses);
                }
                if (UtilValidate.isNotEmpty(validBCCAddresses)) {
                    sendMailContext.put("sendBcc", validBCCAddresses);
                }

                String emailServiceName = "sendMail";

                if (UtilValidate.isEmpty(attachments)) {
                    sendMailContext.put("body", context.get("content"));
                } else {
                    // Construct the list of parts so that the message body is first, just in case some email clients break
                    List < Map < String, Object >> bodyParts = UtilMisc.toList(UtilMisc.toMap("content", context.get("content"), "type", context.get("contentMimeTypeId")));

                    bodyParts.addAll(attachments);
                    sendMailContext.put("bodyParts", bodyParts);
                    emailServiceName = "sendMailMultiPart";
                }

                // Send the email synchronously
                Map < String, Object > sendMailResult = dispatcher.runSync(emailServiceName, sendMailContext);
                if (ServiceUtil.isError(sendMailResult)) {
                    return UtilMessage.createAndLogServiceError(sendMailResult, errorLabel, locale, MODULE);
                }

                // Update communication event to status COM_COMPLETE, and to update the subject if it's changed
                input = UtilMisc.toMap("communicationEventId", communicationEventId, "userLogin", userLogin);
                input.put("statusId", "COM_COMPLETE");
                input.put("datetimeEnded", UtilDateTime.nowTimestamp());
                input.put("subject", sendMailContext.get("subject"));
                serviceResults = dispatcher.runSync("updateCommunicationEvent", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, errorLabel, locale, MODULE);
                }

                //Author:Srikanth Reddy Complete the incoming Email when replyed or forwarded
                input = UtilMisc.toMap("workEffortTypeId", "TASK", "currentStatusId", "TASK_COMPLETED", "userLogin", userLogin);
                if (("reply".equals(action) || "forward".equals(action)) && UtilValidate.isNotEmpty(workEffortId)) {
                    Map < String, Object > updateWorkEffortInput = new HashMap < String, Object > (input);
                    updateWorkEffortInput.put("workEffortId", workEffortId);
                    updateWorkEffortInput.put("actualStartDate", context.get("datetimeStarted"));
                    if (UtilValidate.isEmpty(updateWorkEffortInput.get("actualStartDate"))) {
                        updateWorkEffortInput.put("actualStartDate", UtilDateTime.nowTimestamp());
                    }
                    updateWorkEffortInput.put("actualCompletionDate", UtilDateTime.nowTimestamp());
                    updateWorkEffortInput.put("workEffortName", context.get("subject"));
                    updateWorkEffortInput.put("workEffortPurposeTypeId", "WEPT_TASK_EMAIL");
                    serviceName = "updateWorkEffort";
                    serviceResults = dispatcher.runSync(serviceName, updateWorkEffortInput);
                    if (ServiceUtil.isError(serviceResults)) {
                        return UtilMessage.createAndLogServiceError(serviceResults, errorLabel, locale, MODULE);
                    }
                }
                // now update or create a work effort to record this email as a completed task
                if (existing) {
                    input.put("workEffortId", workEffortId);
                }
                input.put("actualStartDate", context.get("datetimeStarted"));
                if (UtilValidate.isEmpty(input.get("actualStartDate"))) {
                    input.put("actualStartDate", UtilDateTime.nowTimestamp());
                }
                input.put("actualCompletionDate", UtilDateTime.nowTimestamp());
                input.put("workEffortName", context.get("subject"));
                input.put("workEffortPurposeTypeId", "WEPT_TASK_EMAIL");
                serviceName = (existing ? "updateWorkEffort" : "createWorkEffort");
                serviceResults = dispatcher.runSync(serviceName, input);
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, errorLabel, locale, MODULE);
                }

            } else {
                // Create or update a scheduled (TASK_STARTED) TASK WorkEffort to save this email
                input = UtilMisc.toMap("workEffortTypeId", "TASK", "currentStatusId", "TASK_STARTED", "userLogin", userLogin);
                if (existing) {
                    input.put("workEffortId", workEffortId);
                }
                input.put("actualStartDate", context.get("datetimeStarted"));
                if (UtilValidate.isEmpty(input.get("actualStartDate"))) {
                    input.put("actualStartDate", UtilDateTime.nowTimestamp());
                }
                input.put("workEffortName", context.get("subject"));
                input.put("workEffortPurposeTypeId", "WEPT_TASK_EMAIL");
                serviceResults = dispatcher.runSync(existing ? "updateWorkEffort" : "createWorkEffort", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, errorLabel, locale, MODULE);
                }
            }

            // get the work effort ID from the serviceResults if a workEffort was created (note that the last service run in this case is always createWorkEffort)
            if (!existing) {
                workEffortId = (String) serviceResults.get("workEffortId");
            }

            // create an association between the task and comm event (safe even if existing)
            input = UtilMisc.toMap("userLogin", userLogin, "communicationEventId", communicationEventId, "workEffortId", workEffortId);
            serviceResults = dispatcher.runSync("createCommunicationEventWorkEff", input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, errorLabel, locale, MODULE);
            }

            // Create separate lists for the to, CC and BCC addresses
            List < GenericValue > partyAndContactMechsTo = findPartyAndContactMechsForEmailAddress(toAddresses, delegator);
            List < GenericValue > partyAndContactMechsCC = findPartyAndContactMechsForEmailAddress(ccAddresses, delegator);
            List < GenericValue > partyAndContactMechsBCC = findPartyAndContactMechsForEmailAddress(bccAddresses, delegator);
            associateCommunicationEventWorkEffortAndParties(partyAndContactMechsTo, communicationEventId, "EMAIL_RECIPIENT_TO", workEffortId, delegator, dispatcher, userLogin);
            associateCommunicationEventWorkEffortAndParties(partyAndContactMechsCC, communicationEventId, "EMAIL_RECIPIENT_CC", workEffortId, delegator, dispatcher, userLogin);
            associateCommunicationEventWorkEffortAndParties(partyAndContactMechsBCC, communicationEventId, "EMAIL_RECIPIENT_BCC", workEffortId, delegator, dispatcher, userLogin);

            // pass in List of all email addresses
            List < String > allEmailAddresses = new ArrayList < String > ();
            allEmailAddresses.addAll(toAddresses);
            allEmailAddresses.addAll(ccAddresses);
            // don't think BCC addresses is needed: this variable is intended for creating owners for incoming emails, and those don't come with BCC

            // create note for replying mails
            String custRequestId = (String) context.get("custRequestId");
            String note = (String) context.get("content");
            Debug.logInfo("-----createNoteWriteMail-------" + context, MODULE);
            if (UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(note)) {
                Map < String, Object > inputNote = FastMap.newInstance();
                inputNote.put("custRequestId", custRequestId);
                inputNote.put("note", note);
                inputNote.put("noteType", "EXTERNAL");
                inputNote.put("userLogin", userLogin);
                Map serviceResult = null;

                try {
                    serviceResult = dispatcher.runSync("createCustRequestNote", inputNote);
                    Debug.logInfo("-----createContentForNote---serviceResult-" + serviceResult, MODULE);
                    if (ServiceUtil.isError(serviceResult)) {
                        return serviceResult;
                    }
                } catch (GenericServiceException e) {
                    return serviceResult;
                }
            }


            results.put("workEffortId", workEffortId);
            return results;

        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, errorLabel, locale, MODULE);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, errorLabel, locale, MODULE);
        }
    }
    
    
    /**
     * Finds the list of <code>PartyAndContactMech</code> matching the given list of email addresses.
     * This method respect the email address case insensitivity setting "general.properties", "mail.address.caseInsensitive".
     * @param addresses the list of addresses
     * @param delegator the delegator
     * @return the list of <code>PartyAndContactMech</code>
     * @throws GenericEntityException if an error occurs
     */
    public static List<GenericValue> findPartyAndContactMechsForEmailAddress(Collection<String> addresses, Delegator delegator) throws GenericEntityException {
        // option for matching email addresses and parties
    	String caseInsensitiveEmail = EntityUtilProperties.getPropertyValue("general.properties", "mail.address.caseInsensitive", delegator);
        boolean ci = "Y".equals(caseInsensitiveEmail);

        List partyAndContactMechs;

        // case insensitive condition does not work with the IN operator in the delegator
        if (ci) {
            partyAndContactMechs = new ArrayList<GenericValue>();
            for (String address : addresses) {
                List<GenericValue> partyAndContactMechPartial = delegator.findList("PartyAndContactMech", EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("infoString"), EntityOperator.EQUALS, EntityFunction.UPPER(address)), null, UtilMisc.toList("fromDate"), null, false);
                partyAndContactMechs.addAll(partyAndContactMechPartial);
            }
        } else {
            partyAndContactMechs = delegator.findList("PartyAndContactMech", EntityCondition.makeCondition("infoString", EntityOperator.IN, addresses), null, UtilMisc.toList("fromDate"), null, false);
        }

        partyAndContactMechs = EntityUtil.filterByDate(partyAndContactMechs, true);
        return partyAndContactMechs;
    }
    
    /**
     * Creates a CommunicationEventRole and WorkeffortPartyAssignment (if the party has a CRM role) for each party in the list
     * */
    private static void associateCommunicationEventWorkEffortAndParties(List<GenericValue> partyAndContactMechs, String communicationEventId, String roleTypeId, String workEffortId, Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin)
                   throws GenericEntityException, GenericServiceException {
    	
    	////EmailLogServices.processIncomingEmailLog("ACWPINFO1","progress",partyAndContactMechs.size()+"communicationEventId:"+communicationEventId+"roleTypeId:"+roleTypeId+"workEffortId:"+workEffortId );
    	
        if (UtilValidate.isNotEmpty(partyAndContactMechs)) {

        	////EmailLogServices.processIncomingEmailLog("ACWPINFO2","progress",partyAndContactMechs.toString()+"communicationEventId:"+communicationEventId+"roleTypeId:"+roleTypeId+"workEffortId:"+workEffortId );

            Map<String, Object> serviceResults = null;
            Map<String, Object> input = null;

            List<String> validRoleTypeIds = new ArrayList<String>(PartyHelper.TEAM_MEMBER_ROLES);
            validRoleTypeIds.addAll(PartyHelper.CLIENT_PARTY_ROLES);

            ////EmailLogServices.processIncomingEmailLog("ACWPINFO1","progress",validRoleTypeIds.toString() );
            Set<String> partyIds = new HashSet<String>(EntityUtil.<String>getFieldListFromEntityList(partyAndContactMechs, "partyId", true));
            Set<String> emailAddresses = new HashSet<String>(EntityUtil.<String>getFieldListFromEntityList(partyAndContactMechs, "infoString", true));      // for looking for the owner of this activity against an email

            ////EmailLogServices.processIncomingEmailLog("ACWPINFO3","progress","partyIds:"+partyIds.toString()+"emailAddresses:"+emailAddresses.toString() );
            
            for (String partyId : partyIds) {

            	////EmailLogServices.processIncomingEmailLog("ACWPINFO3","progress","partyId:"+partyId );

                // Add a CommunicationEventRole for the party, if one doesn't already exist
                EntityCondition codeCondition = EntityCondition.makeCondition(EntityOperator.AND,
                        EntityCondition.makeCondition("communicationEventId", EntityOperator.EQUALS,communicationEventId),
                        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
                long commEventRoles = delegator.findCountByCondition("CommunicationEventRole", codeCondition, null,null);
                if (commEventRoles == 0) {
                    ////EmailLogServices.processIncomingEmailLog("ACWPINFO4","progress","partyId:"+partyId+"roleTypeId:"+roleTypeId );                	
                    serviceResults = dispatcher.runSync("ensurePartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId, "userLogin", userLogin));
                    if (ServiceUtil.isError(serviceResults)) {
                    	////EmailLogServices.processIncomingEmailLog("ACWPERROR1","failed","ensurePartyRole--service::::partyId:"+partyId+"roleTypeId:"+roleTypeId+"serviceResults:"+serviceResults.toString() );   
                        Debug.logError(ServiceUtil.getErrorMessage(serviceResults), MODULE);
                        throw new GenericServiceException(ServiceUtil.getErrorMessage(serviceResults));
                    }

                    ////EmailLogServices.processIncomingEmailLog("ACWPINFO5","progress","partyId:"+partyId+"roleTypeId:"+roleTypeId );

                    // Use the first PartyAndContactMech for that partyId in the partyAndContactMech list
                    EntityCondition filterConditions = EntityCondition.makeCondition(EntityOperator.AND, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityCondition.makeCondition("contactMechId", EntityOperator.NOT_EQUAL, null));
                    GenericValue partyAndContactMech = EntityUtil.getFirst(EntityUtil.filterByCondition(partyAndContactMechs, filterConditions));

                    ////EmailLogServices.processIncomingEmailLog("ACWPINFO6","progress","partyId:"+partyId+"roleTypeId:"+roleTypeId+"partyAndContactMech:"+partyAndContactMech.getString("contactMechId") );
                    
                    // Create the communicationEventRole
                    serviceResults = dispatcher.runSync("createCommunicationEventRole", UtilMisc.toMap("communicationEventId", communicationEventId, "partyId", partyId, "roleTypeId", roleTypeId, "contactMechId", partyAndContactMech.getString("contactMechId"), "userLogin", userLogin));
                    if (ServiceUtil.isError(serviceResults)) {
                    	//EmailLogServices.processIncomingEmailLog("ACWPERROR2","failed","createCommunicationEventRole--service:::::partyId:"+partyId+"roleTypeId:"+roleTypeId+"partyAndContactMech-contactMechId:"+partyAndContactMech.getString("contactMechId")+"serviceResults:"+serviceResults.toString() );
                        Debug.logError(ServiceUtil.getErrorMessage(serviceResults), MODULE);
                        throw new GenericServiceException(ServiceUtil.getErrorMessage(serviceResults));
                    }
                }

                //EmailLogServices.processIncomingEmailLog("ACWPINFO7","progress","partyId:"+partyId+"workEffortId:"+workEffortId );

                if (UtilValidate.isNotEmpty(workEffortId)) {

                    // Assign the party to the workeffort if they have a CRM role, and if they aren't already assigned
                    List<GenericValue> workEffortPartyAssignments = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("partyId", partyId, "workEffortId", workEffortId), null, false);
                    workEffortPartyAssignments = EntityUtil.filterByDate(workEffortPartyAssignments);

                    //EmailLogServices.processIncomingEmailLog("ACWPINFO8","progress","partyId:"+partyId+"workEffortId:"+workEffortId+"workEffortPartyAssignments:"+workEffortPartyAssignments.size() );
                    Debug.logInfo("===sri====workEffortPartyAssignments=="+workEffortPartyAssignments, MODULE);
                    if (UtilValidate.isEmpty(workEffortPartyAssignments)) {
                    	
                    	//EmailLogServices.processIncomingEmailLog("ACWPINFO9","progress","workEffortPartyAssignments-not-null::::partyId:"+partyId+"workEffortId:"+workEffortId );
                    	
                        String crmRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, validRoleTypeIds, delegator);
                        //EmailLogServices.processIncomingEmailLog("ACWPINF10","progress","crmRoleTypeId:::"+crmRoleTypeId );
                        if (crmRoleTypeId == null) {
                            Debug.logWarning("No valid roles found for partyId [" + partyId + "], so it will not be assigned to activity " + workEffortId, MODULE);
                        } else {
                        	//EmailLogServices.processIncomingEmailLog("ACWPINFO11","progress","crmRoleTypeId:::"+crmRoleTypeId );                        	
                            // if this party is an internal party (crmsfa user), the activity does not have an owner yet, and
                            // this current party is associated with any of the email addresses as "Owner of Received Emails", then
                            // the party is the owner
                            // note that this means the activity can only have one owner at a time
                            if (PartyHelper.TEAM_MEMBER_ROLES.contains(crmRoleTypeId) && (UtilValidate.isEmpty(UtilActivity.getActivityOwner(workEffortId, delegator)))) {
                                //EmailLogServices.processIncomingEmailLog("ACWPINFO12","progress","crmRoleTypeId:::"+crmRoleTypeId+"partyId:"+partyId +"workEffortId:"+workEffortId + "]" );
                                if (UtilValidate.isNotEmpty(PartyHelper.getCurrentContactMechsForParty(partyId, "EMAIL_ADDRESS", "RECEIVE_EMAIL_OWNER",
                                        UtilMisc.toList(EntityCondition.makeCondition("infoString", EntityOperator.IN, emailAddresses)), delegator))) {
                                    crmRoleTypeId = "CAL_OWNER";
                                    Debug.logInfo("Will be assigning [" + partyId + "] as owner of [" + workEffortId + "]", MODULE);
                                    //EmailLogServices.processIncomingEmailLog("ACWPINFO13","progress","Will be assigning [" + partyId + "] as owner of [" + workEffortId + "]" );
                                }
                            }
                            //EmailLogServices.processIncomingEmailLog("ACWPINFO15","progress","partyId:"+partyId+"workEffortId:"+workEffortId+"roleTypeId:"+crmRoleTypeId+"statusId:PRTYASGN_ASSIGNED" );	
                            input = UtilMisc.toMap("partyId", partyId, "workEffortId", workEffortId, "roleTypeId", crmRoleTypeId, "statusId", "PRTYASGN_ASSIGNED", "userLogin", userLogin);
                            serviceResults = dispatcher.runSync("assignPartyToWorkEffort", input);
                            if (ServiceUtil.isError(serviceResults)) {
                            	//EmailLogServices.processIncomingEmailLog("ACWPERROR3","failed","assignPartyToWorkEffort--service:::::partyId:"+partyId+"roleTypeId:"+crmRoleTypeId+"serviceResults:"+serviceResults.toString() );
                                Debug.logError(ServiceUtil.getErrorMessage(serviceResults), MODULE);
                                throw new GenericServiceException(ServiceUtil.getErrorMessage(serviceResults));
                            }
                            //EmailLogServices.processIncomingEmailLog("ACWPINFO15","progress","associateCommunicationEventWorkEffortAndParties-----finished" );
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Helper method to create WorkEffort associations for an internal party (account, contact or lead), a case and an opportunity.
     * If you need to remove existing ones, use the method removeAllAssociationsForWorkEffort() first.
     *
     * @param   reassign    Whether the CAL_OWNER should be overwritten by the userLogin or not
     * @return  If an error occurs, returns service error which can be tested with ServiceUtil.isError(), otherwise a service success
     */
    private static Map<String, Object> createWorkEffortPartyAssociations(DispatchContext dctx, Map<String, Object> context, String workEffortId, String errorLabel, boolean reassign)
        throws GenericEntityException, GenericServiceException {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> input = null;
        Map<String, Object> serviceResults = null;

        // association IDs
        String internalPartyId = (String) context.get("internalPartyId");
        String salesOpportunityId = (String) context.get("salesOpportunityId");
        //String custRequestId = (String) context.get("custRequestId");

        /*
         * The first step is to collect all the ACCOUNT, CONTACT or PROSPECT parties that we should associate with the workEffort.
         * This includes those ACCOUNTS and CONTACTS that are associated with the case or opportunity that was specified in the input.
         * Then we find the first valid role type for each, which is required for the work effort association. If any of these parties
         * has no valid role types, then a bad ID was passed in. This serves to validate the association input.
         */
        List<String> partyAssociationIds = new ArrayList<String>();
        if (internalPartyId != null) {
            partyAssociationIds.add(internalPartyId);
        }
        if (salesOpportunityId != null) {
            partyAssociationIds.addAll(UtilOpportunity.getOpportunityAccountPartyIds(delegator, salesOpportunityId));
            partyAssociationIds.addAll(UtilOpportunity.getOpportunityContactPartyIds(delegator, salesOpportunityId));
        }
        /*if (custRequestId != null) {
            List<GenericValue> parties = UtilCase.getCaseAccountsAndContacts(delegator, custRequestId);
            for (Iterator<GenericValue> iter = parties.iterator(); iter.hasNext();) {
                partyAssociationIds.add(iter.next().getString("partyId"));
            }
        }*/
        // now get the roles
        List<String> partyAssocRoleTypeIds = new ArrayList<String>();
        for (Iterator<String> iter = partyAssociationIds.iterator(); iter.hasNext();) {
            String partyId = iter.next();
            String roleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, PartyHelper.CLIENT_PARTY_ROLES, delegator);
            if (roleTypeId == null) {
                roleTypeId = "_NA_"; // this permits non-crmsfa parties to be associated
            }
            partyAssocRoleTypeIds.add(roleTypeId);
        }

        /*
         * The remaining task is to create the associations to work effort.
         */

        if (reassign) {
            // expire all associations of type CAL_OWNER for this work effort
            List<GenericValue> oldOwners = EntityUtil.filterByDate(delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("workEffortId", workEffortId, "roleTypeId", "CAL_OWNER"), null, false));
            for (Iterator<GenericValue> iter = oldOwners.iterator(); iter.hasNext();) {
                GenericValue old = iter.next();
                old.set("thruDate", UtilDateTime.nowTimestamp());
                old.store();
            }

            // first make sure the userlogin has a role CAL_OWNER
            input = UtilMisc.<String, Object>toMap("partyId", userLogin.getString("partyId"), "roleTypeId", "CAL_OWNER");
            List<GenericValue> partyRoles = delegator.findByAnd("PartyRole", input, null, false);
            if (partyRoles.size() == 0)  {
                input.put("userLogin", userLogin);
                serviceResults = dispatcher.runSync("createPartyRole", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, errorLabel, locale, MODULE);
                }
            }

            // then create the assignment
            input.put("workEffortId", workEffortId);
            input.put("userLogin", userLogin);
            input.put("roleTypeId", "CAL_OWNER");
            input.put("statusId", "PRTYASGN_ASSIGNED");
            input.put("availabilityStatusId", context.get("availabilityStatusId")); // add our availability status
            serviceResults = dispatcher.runSync("assignPartyToWorkEffort", input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, errorLabel, locale, MODULE);
            }
        }

        // associate the opportunity with the work effort if it is not associated yet
        if (salesOpportunityId != null) {
            input = UtilMisc.<String, Object>toMap("salesOpportunityId", salesOpportunityId, "workEffortId", workEffortId);
            GenericValue map = delegator.findOne("SalesOpportunityWorkEffort", input, false);
            if (map == null) {
                map = delegator.makeValue("SalesOpportunityWorkEffort", input);
                // TODO: created by hand because we don't have a service for this yet
                map.create();
            }
        }

        // associate the case with the work effort if it is not associated yet
        /*if (custRequestId != null) {
            if (UtilValidate.isEmpty(delegator.findOne("CustRequestWorkEffort", UtilMisc.toMap("workEffortId", workEffortId, "custRequestId", custRequestId), true))) {
                serviceResults = dispatcher.runSync("createWorkEffortRequest",
                    UtilMisc.toMap("workEffortId", workEffortId, "custRequestId", custRequestId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, errorLabel, locale, MODULE);
                }
            }
        }*/

        // now for each party association, assign the party and its role to the work effort
        if (partyAssociationIds != null) {
            Iterator<String> roleIter = partyAssocRoleTypeIds.iterator();
            Iterator<String> partyIter = partyAssociationIds.iterator();
            while (partyIter.hasNext()) {
                String partyId = partyIter.next();
                String roleTypeId = roleIter.next();

                // if an unexpired existing relationship exists, then skip (this is to avoid duplicates)
                List<GenericValue> oldAssocs = EntityUtil.filterByDate(delegator.findByAnd("WorkEffortPartyAssignment",
                            UtilMisc.toMap("workEffortId", workEffortId, "roleTypeId", roleTypeId, "partyId", partyId), null, false));
                if (oldAssocs.size() > 0) {
                    continue;
                }

                // now create the new one
                input = UtilMisc.<String, Object>toMap("workEffortId", workEffortId, "partyId", partyId, "roleTypeId", roleTypeId, "statusId", "PRTYASGN_ASSIGNED");
                input.put("userLogin", userLogin);
                serviceResults = dispatcher.runSync("assignPartyToWorkEffort", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, errorLabel, locale, MODULE);
                }
            }
        }
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> logTask(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        TimeZone timeZone = UtilCommon.getTimeZone(context);
        String workEffortPurposeTypeId = (String) context.get("workEffortPurposeTypeId");
        Map<String,Object> results = new HashMap<String, Object>();

        try {
            // get the actual completion date from the duration (default to now and 1 hour)
            Timestamp actualStartDate = (Timestamp) context.get("actualStartDate");
            if (actualStartDate == null) {
                actualStartDate = UtilDateTime.nowTimestamp();
            }
            Timestamp actualCompletionDate = (Timestamp) context.get("actualCompletionDate");
            // if actualCompletionDate is not given, use the duration instead
            if (actualCompletionDate == null) {
                actualCompletionDate = UtilCommon.getEndTimestamp(actualStartDate, (String) context.get("duration"), locale, timeZone);
            }

            // validate the associations
            /*Map<String, Object> serviceResults = validateWorkEffortAssociations(dctx, context);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorLogTaskFail", locale, module);
            }*/

            // create the workeffort from the context data, which results in a workEffortId
            ModelService service = dctx.getModelService("createWorkEffort");
            Map<String, Object> input = service.makeValid(context, "IN");
            input.put("actualCompletionDate", actualCompletionDate);
            input.put("workEffortTypeId", "TASK");
            input.put("currentStatusId", "TASK_COMPLETED");
            Map<String, Object> serviceResults = dispatcher.runSync("createWorkEffort", input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorLogTaskFail", locale, MODULE);
            }
            String workEffortId = (String) serviceResults.get("workEffortId");

            // create the associations
            serviceResults = createWorkEffortPartyAssociations(dctx, context, workEffortId, "CrmErrorLogTaskFail", true);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorLogTaskFail", locale, MODULE);
            }

            // assumne inbound
            boolean outbound = false;
            String partyIdTo = userLogin.getString("partyId");
            String partyIdFrom = (String) context.get("internalPartyId");

            // then change if it's actually outbound
            if ("Y".equals(context.get("outbound"))) {
                outbound = true;
                partyIdTo = (String) context.get("internalPartyId");
                partyIdFrom = userLogin.getString("partyId");
            }

            // create a completed comm event with as much information as we have
            service = dctx.getModelService("createCommunicationEvent");
            input = service.makeValid(context, "IN");
            input.put("subject", context.get("workEffortName"));
            input.put("entryDate", UtilDateTime.nowTimestamp());
            input.put("datetimeStarted", actualStartDate);
            input.put("datetimeEnded", actualCompletionDate);
            if ("WEPT_TASK_EMAIL".equals(workEffortPurposeTypeId)) {
                input.put("contactMechTypeId", "EMAIL_ADDRESS");
                input.put("communicationEventTypeId", "EMAIL_COMMUNICATION");
                results.putAll(ServiceUtil.returnSuccess("Email Log Created Successfully."));
            } else if ("WEPT_TASK_PHONE_CALL".equals(workEffortPurposeTypeId)) {
                input.put("contactMechTypeId", "TELECOM_NUMBER");
                input.put("communicationEventTypeId", "PHONE_COMMUNICATION");
                results.putAll(ServiceUtil.returnSuccess("Call Log  Created Successfully."));
            }else if ("WEPT_TASK_CHAT".equals(workEffortPurposeTypeId)) {
                input.put("contactMechTypeId", "EMAIL_ADDRESS");
                input.put("communicationEventTypeId", "CHAT");
            } else {
                Debug.logWarning("Work effort purpose type [" + workEffortPurposeTypeId + "] not known, not able to set communication event and contact mech types", MODULE);
            }
            input.put("statusId", "COM_COMPLETE");
            input.put("partyIdTo", partyIdTo);
            input.put("partyIdFrom", partyIdFrom);
            if (outbound) {
                if (partyIdTo != null) {
                    input.put("roleTypeIdTo", PartyHelper.getFirstValidInternalPartyRoleTypeId(partyIdTo, delegator));
                }
                input.put("roleTypeIdFrom", PartyHelper.getFirstValidTeamMemberRoleTypeId(partyIdFrom, delegator));
            } else {
                if (partyIdFrom != null) {
                    input.put("roleTypeIdFrom", PartyHelper.getFirstValidInternalPartyRoleTypeId(partyIdFrom, delegator));
                }
                input.put("roleTypeIdTo", PartyHelper.getFirstValidTeamMemberRoleTypeId(partyIdTo, delegator));
            }
            serviceResults = dispatcher.runSync("createCommunicationEvent", input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorLogTaskFail", locale, MODULE);
            }
            String communicationEventId = (String) serviceResults.get("communicationEventId");

            // create an association between the task and comm event (safe even if existing)
            input = UtilMisc.toMap("userLogin", userLogin, "communicationEventId", communicationEventId, "workEffortId", workEffortId);
            serviceResults = dispatcher.runSync("createCommunicationEventWorkEff", input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorLogTaskFail", locale, MODULE);
            }

            
            results.put("workEffortId", workEffortId);
            return results;
        } catch (IllegalArgumentException | GenericEntityException | GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorLogTaskFail", locale, MODULE);
        }
    }
    
    private static Map<String, Object> deleteActivityCommEventAndDataResource(String workEffortId, String communicationEventId, String delContentDataResource, GenericValue userLogin, LocalDispatcher dispatcher) throws GenericServiceException {
        Map<String, Object> deleteCommunicationEventWorkEffResult = dispatcher.runSync("deleteCommunicationEventWorkEff", UtilMisc.toMap("workEffortId", workEffortId, "communicationEventId", communicationEventId, "userLogin", userLogin));
        if (ServiceUtil.isError(deleteCommunicationEventWorkEffResult)) {
            return deleteCommunicationEventWorkEffResult;
        }

        // Call the deleteCommunicationEvent service
        Map<String, Object> deleteCommunicationEventResult = dispatcher.runSync("deleteCommunicationEvent", UtilMisc.toMap("communicationEventId", communicationEventId, "delContentDataResource", delContentDataResource, "userLogin", userLogin));
        if (ServiceUtil.isError(deleteCommunicationEventResult)) {
            return deleteCommunicationEventResult;
        }

        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> deleteCallLog(DispatchContext dctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String communicationEventId = (String) context.get("communicationEventId");
        String workEffortId = (String) context.get("workEffortId");
        String delContentDataResourceStr = (String) context.get("delContentDataResource");
        String donePage = (String) context.get("donePage");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Security security = dctx.getSecurity();

        // by default delete the attachments
        String delContentDataResource = ("false".equalsIgnoreCase(delContentDataResourceStr) || "N".equalsIgnoreCase(delContentDataResourceStr)) ? "false" : "true";

        // Check if userLogin can update this work effort
        /*if (!CrmsfaSecurity.hasActivityPermission(security, "_UPDATE", userLogin, workEffortId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
        }*/
        Map<String,Object> results = new HashMap<String, Object>();
        
        try {

            /*DomainsLoader domainLoader = new DomainsLoader(new Infrastructure(dispatcher), new User(userLogin));
            ActivityRepositoryInterface activityRepository = domainLoader.getDomainsDirectory().getActivitiesDomain().getActivityRepository();
            ActivityFactRepositoryInterface activityFactRepository = domainLoader.getDomainsDirectory().getActivitiesDomain().getActivityFactRepository();

            // Get Activity
            Activity activity = activityRepository.getActivityById(workEffortId);
            List<Party> parties = activity.getParticipants();*/

            GenericValue workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
            if (UtilValidate.isEmpty(workEffort)) {
                return ServiceUtil.returnError("No activity found with work effort ID [" + workEffortId + "]");
            }

            if (communicationEventId != null) {
                // Remove any existing associations to CommunicationEventOrder
                List<String> eventOrderIds = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("CommunicationEventOrder", UtilMisc.toMap("communicationEventId", communicationEventId), null, false), "orderId", true);
                for (String orderIdToRemove : eventOrderIds) {
                    Map<String, Object> deleteOHWEResult = dispatcher.runSync("removeCommunicationEventOrder", UtilMisc.toMap("communicationEventId", communicationEventId, "orderId", orderIdToRemove, "userLogin", userLogin));
                    if (ServiceUtil.isError(deleteOHWEResult)) {
                        return deleteOHWEResult;
                    }
                }

                // delete just this particular communicationEventId
                results = deleteActivityCommEventAndDataResource(workEffortId, communicationEventId, delContentDataResource, userLogin, dispatcher);
            } else {
                List<GenericValue> communicationEvents = workEffort.getRelated("CommunicationEventWorkEff");
                for (GenericValue communicationEvent: communicationEvents) {
                    results = deleteActivityCommEventAndDataResource(workEffortId, communicationEvent.getString("communicationEventId"), delContentDataResource, userLogin, dispatcher);
                }
            }

            // Remove any existing associations to OrderHeaderWorkEffort
            /*List<String> orderIds = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("OrderHeaderWorkEffort", UtilMisc.toMap("workEffortId", workEffortId), null, false), "orderId", true);
            for (String orderIdToRemove : orderIds) {
                Map<String, Object> deleteOHWEResult = dispatcher.runSync("deleteOrderHeaderWorkEffort", UtilMisc.toMap("workEffortId", workEffortId, "orderId", orderIdToRemove, "userLogin", userLogin));
                if (ServiceUtil.isError(deleteOHWEResult)) {
                    return deleteOHWEResult;
                }
            }*/

            // Call the deleteWorkEffort service
            Map<String, Object> deleteWorkEffortResult = dispatcher.runSync("deleteWorkEffort", UtilMisc.toMap("workEffortId", workEffortId, "userLogin", userLogin));
            if (ServiceUtil.isError(deleteWorkEffortResult)) {
                return deleteWorkEffortResult;
            }

            // Transform to ActivityFact with negative counter equals -1
            //activityFactRepository.transformToActivityFacts(activity, parties, COUNT);

        } catch (GenericServiceException | GenericEntityException ex) {
            return UtilMessage.createAndLogServiceError(ex, locale, MODULE);
        }

        results.put("donePage", donePage);
        results.putAll(ServiceUtil.returnSuccess("Call Log Deleted Successfully."));
        return results;
    }
}