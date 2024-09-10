package org.fio.crm.leads;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.crm.constants.CrmConstants;
import org.fio.crm.constants.CrmConstants.ValidationAuditType;
import org.fio.crm.party.PartyHelper;
import org.fio.crm.util.DataHelper;
import org.fio.crm.util.DataUtil;
import org.fio.crm.util.ParamUtil;
import org.fio.crm.util.UtilImport;
import org.fio.crm.util.UtilMessage;
import org.fio.crm.util.VirtualTeamUtil;
import org.fio.crm.writer.WriterUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class LeadsServices {

    private LeadsServices() { }

    private static final String MODULE = LeadsServices.class.getName();
    public static final String resource = "crmUiLabels";
    public static final List<String> TEAM_MEMBER_ROLES = UtilMisc.toList("EMPLOYEE", "ACCOUNT_REP", "CUST_SERVICE_REP", "OWNER");
    
    public static Map<String, ?> createLead(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Security security = dctx.getSecurity();
        String errMsg = null;
        
        if (!security.hasPermission(CrmConstants.SecurityPermissionConstants.CRMSFA_LEAD_CREATE, userLogin)) {
            errMsg = UtilProperties.getMessage(resource,"CrmErrorPermissionDenied", locale);
            return ServiceUtil.returnError(errMsg);
        }
        
        // Check Valid account or not
        if (UtilValidate.isNotEmpty(context.get("parentPartyId"))) {
            Boolean accountValidation = accountValidation(delegator, (String) context.get("parentPartyId"));
            if (!accountValidation) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "invalidAccount", locale));
            }
        }

        // the net result of creating an lead is the generation of a Lead partyId
        String leadPartyId = null;
        try {
            // make sure user has the right crm roles defined.  otherwise the lead could be created but then once converted the account will be deactivated.
            if (UtilValidate.isEmpty(PartyHelper.getFirstValidRoleTypeId(userLogin.getString("partyId"), TEAM_MEMBER_ROLES, delegator))) {
                errMsg = UtilProperties.getMessage(resource,
                        "CrmError_NoRoleForCreateParty", 
                        UtilMisc.toMap("userPartyName", PartyHelper.getPartyName(delegator, userLogin.getString("partyId"), false), "requiredRoleTypes", PartyHelper.TEAM_MEMBER_ROLES), locale);
                return ServiceUtil.returnError(errMsg);
            }

            // set statusId is LEAD_ASSIGNED, because we are assigning to the user down below.
            // perhaps a better alternative is to create the lead as NEW, call the reassignLeadResponsibleParty service below, and have it update it to ASSIGNED if not already so.
            String statusId = CrmConstants.PartyLeadStatus.LEAD_ASSIGNED;

            // create the Party and Person, which results in a partyId
            Map<String, Object> input = UtilMisc.toMap("firstName", context.get("firstName"), "lastName", context.get("lastName"));
            input.put("firstNameLocal", context.get("firstNameLocal"));
            input.put("lastNameLocal", context.get("lastNameLocal"));
            input.put("personalTitle", context.get("personalTitle"));
            input.put("preferredCurrencyUomId", context.get("currencyUomId"));
            input.put("description", context.get("description"));
            input.put("birthDate", context.get("birthDate"));
            input.put("callBackDate",context.get("callBackDate"));
            input.put("statusId", statusId); // initial status
            Map<String, Object> serviceResults = dispatcher.runSync("createPerson", input);
            if (ServiceUtil.isError(serviceResults)) {
                errMsg = UtilProperties.getMessage(resource, "CrmErrorCreateLeadFail", locale);
                return ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
            }
            leadPartyId = (String) serviceResults.get("partyId");
            // create PartySupplementalData
            GenericValue partyData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", leadPartyId/*, "partyTypeId", "PARTY_GROUP", "isLead", "Y", "leadOwner", userLogin.getString("partyId")*/));
            partyData.setNonPKFields(context);
            partyData.create();

            // create a PartyRole for the resulting Lead partyId with roleTypeId = LEAD
            serviceResults = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", leadPartyId, "roleTypeId", CrmConstants.RoleTypeConstants.LEAD, "userLogin", userLogin));
            if (ServiceUtil.isError(serviceResults)) {
                errMsg = UtilProperties.getMessage(resource, "CrmErrorCreateLeadFail", locale);
                return ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
            }
            
            //storing TimeZone
            if(UtilValidate.isNotEmpty(context.get("timeZoneDesc"))){
	            GenericValue partyDataVal = delegator.findOne("Party", UtilMisc.toMap("partyId", leadPartyId), false);
	            partyDataVal.put("timeZoneDesc",context.get("timeZoneDesc"));
	            partyDataVal.store();
            }
            

            // create a party relationship between the userLogin and the Lead with partyRelationshipTypeId RESPONSIBLE_FOR
            input = UtilMisc.<String, Object>toMap("partyIdFrom",leadPartyId, "partyIdTo", userLogin.getString("partyId"), "roleTypeIdFrom", CrmConstants.RoleTypeConstants.LEAD, "roleTypeIdTo", CrmConstants.RoleTypeConstants.OWNER,"partyRelationshipTypeId", CrmConstants.PartyRelationshipTypeConstants.RESPONSIBLE_FOR);
            input.put("securityGroupId", CrmConstants.SecurityGroupConstants.LEAD_OWNER);
            input.put("userLogin", userLogin);
            serviceResults = dispatcher.runSync("createPartyRelationship", input);
            
            // if initial data source is provided, add it
            String dataSourceId = (String) context.get("dataSourceId");
            if (dataSourceId != null) {
                serviceResults = dispatcher.runSync("crmsfa.addLeadDataSource",
                         UtilMisc.toMap("partyId", leadPartyId, "dataSourceId", dataSourceId, "userLogin", userLogin));

                if (ServiceUtil.isError(serviceResults)) {
                    errMsg = UtilProperties.getMessage(resource, "CrmErrorCreateLeadFail", locale);
                    return ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
                }
            }
            
            // if initial marketing campaign is provided, add it
            String marketingCampaignId = (String) context.get("marketingCampaignId");
            if (marketingCampaignId != null) {
                serviceResults = dispatcher.runSync("crmsfa.addLeadMarketingCampaign",
                        UtilMisc.toMap("partyId", leadPartyId, "marketingCampaignId", marketingCampaignId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    errMsg = UtilProperties.getMessage(resource, "CrmErrorCreateLeadFail", locale);
                    return ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
                }
            }
            
            // create basic contact info
            ModelService service = dctx.getModelService("crmsfa.createBasicContactInfoForParty");
            input = service.makeValid(context, "IN");
            input.put("partyId", leadPartyId);
            serviceResults = dispatcher.runSync(service.name, input);

            if (ServiceUtil.isError(serviceResults)) {
                errMsg = UtilProperties.getMessage(resource, "CrmErrorCreateLeadFail", locale);
                return ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
            }
            
            String partyClassificationGroupId = (String) context.get("partyClassificationGroupId"); 
            
            GenericValue customFieldPartyClassification = null;
            Map<String, Object> partyClassificationMap = UtilMisc.<String, Object>toMap("partyId", leadPartyId,"customFieldId", partyClassificationGroupId,"groupId", "LEAD_CLASSIFICATION");
            if(partyClassificationGroupId!=null) {
            	customFieldPartyClassification = delegator.makeValue("CustomFieldPartyClassification", partyClassificationMap);
                delegator.create(customFieldPartyClassification);
                
            }
            
            String note = (String) context.get("importantNote");
            if(UtilValidate.isNotEmpty(note)) {
                input = UtilMisc.toMap("partyId", leadPartyId, "note", note, "userLogin", userLogin);
                serviceResults = dispatcher.runSync("createPartyNote", input);
            }

        } catch (GenericServiceException e) {
            errMsg = UtilProperties.getMessage(resource, "CrmErrorCreateLeadFail", locale);
            return ServiceUtil.returnError(errMsg);
        } catch (GenericEntityException e) {
            errMsg = UtilProperties.getMessage(resource, "CrmErrorCreateLeadFail", locale);
            return ServiceUtil.returnError(errMsg);
        }

        // return the partyId of the newly created Lead
        Map<String, Object> results = ServiceUtil.returnSuccess();
        results.put("partyId", leadPartyId);
        return results;
    }
    
    
    public static Map<String, ?> updateLead(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String errMsg = null;
        String leadPartyId = (String) context.get("partyId");
        Map<String, Object> results = ServiceUtil.returnSuccess();
        // Check Valid account or not
        if (UtilValidate.isNotEmpty(context.get("parentPartyId"))) {
            Boolean accountValidation = accountValidation(delegator, (String) context.get("parentPartyId"));
            if (!accountValidation) {
                errMsg = UtilProperties.getMessage(resource, "invalidAccount", locale);
                results = ServiceUtil.returnError(errMsg);
                results.put("partyId", leadPartyId);
                return results;
            }
        }

        try {
            // get the party
            GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", leadPartyId), false);
            if (party == null) {
                errMsg = UtilProperties.getMessage(resource, "CrmErrorUpdateLeadFail", locale);
                results = ServiceUtil.returnError(errMsg);
                results.put("partyId", leadPartyId);
                return results;
            }

            // change status if passed in statusId is different
            String statusId = (String) context.get("statusId");
            if ((statusId != null) && (!statusId.equals(party.getString("statusId")))) {
                Map<String, Object> serviceResults = dispatcher.runSync("setPartyStatus", UtilMisc.toMap("partyId", leadPartyId, "statusId", statusId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    errMsg = UtilProperties.getMessage(resource, "CrmErrorUpdateLeadFail", locale);
                    results = ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
                    results.put("partyId", leadPartyId);
                    return results;
                }
            }

            // update PartySupplementalData
            GenericValue partyData = delegator.findOne("PartySupplementalData", UtilMisc.toMap("partyId", leadPartyId), false);
            if (partyData == null) {
                // create a new one
                partyData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", leadPartyId));
                partyData.create();
            }
            partyData.setNonPKFields(context);
            partyData.store();

            // update the Party and Person
            Map<String, Object> input = UtilMisc.toMap("partyId", leadPartyId, "firstName", context.get("firstName"), "lastName", context.get("lastName"));
            input.put("firstNameLocal", context.get("firstNameLocal"));
            input.put("lastNameLocal", context.get("lastNameLocal"));
            input.put("personalTitle", context.get("personalTitle"));
            input.put("preferredCurrencyUomId", context.get("currencyUomId"));
            input.put("description", context.get("description"));
            input.put("birthDate", context.get("birthDate"));
            input.put("callBackDate", context.get("callBackDate"));
            input.put("userLogin", userLogin);
            Map<String, Object> serviceResults = dispatcher.runSync("updatePerson", input);
            if (ServiceUtil.isError(serviceResults)) {
                errMsg = UtilProperties.getMessage(resource, "CrmErrorUpdateLeadFail", locale);
                results = ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
                results.put("partyId", leadPartyId);
                return results;
            }
            
            //Updating TimeZone
            if(UtilValidate.isNotEmpty(context.get("timeZoneDesc"))){
	            GenericValue partyDataVal = delegator.findOne("Party", UtilMisc.toMap("partyId", leadPartyId), false);
	            partyDataVal.put("timeZoneDesc",context.get("timeZoneDesc"));
	            partyDataVal.store();
            }

        } catch (GenericServiceException e) {
            errMsg = UtilProperties.getMessage(resource, "CrmErrorUpdateLeadFail", locale);
            results = ServiceUtil.returnError(errMsg);
            results.put("partyId", leadPartyId);
            return results;
        } catch (GenericEntityException e) {
            errMsg = UtilProperties.getMessage(resource, "CrmErrorUpdateLeadFail", locale);
            results = ServiceUtil.returnError(errMsg);
            results.put("partyId", leadPartyId);
            return results;
        }
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> updateLeadContactStatus(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String leadContactHistoryId = delegator.getNextSeqId("LeadContactHistory");	
        String partyId = (String) context.get("leadPartyId");
        String leadContactStatusId = (String) context.get("leadContactStatusId");
        Map<String, Object> result = ServiceUtil.returnSuccess();

        try {
            String leadContactStageIdFrom="";
            GenericValue leadContactHistoryGen = EntityUtil.getFirst(delegator.findByAnd("LeadContactHistory", UtilMisc.toMap("partyId", partyId),UtilMisc.toList("-createdStamp"), false));
            if(UtilValidate.isNotEmpty(leadContactHistoryGen)){
                leadContactStageIdFrom = leadContactHistoryGen.getString("leadContactStageIdTo");
            }
            
            GenericValue leadContactHistory = delegator.makeValue("LeadContactHistory");
            leadContactHistory.put("leadContactHistoryId", leadContactHistoryId);
            leadContactHistory.put("partyId", partyId);
            leadContactHistory.put("leadContactStageIdFrom", leadContactStageIdFrom);
            leadContactHistory.put("leadContactStageIdTo", leadContactStatusId);
            leadContactHistory.put("modifiedBy", userLogin.getString("userLoginId"));
            leadContactHistory.create();
            
            GenericValue supplementalData = EntityUtil.getFirst(delegator.findByAnd("PartySupplementalData", UtilMisc.toMap("partyId", partyId), null, false));
            if (UtilValidate.isNotEmpty(supplementalData)) {
            	supplementalData.put("leadStatus", leadContactStatusId);
            	supplementalData.store();
            }
            
        } catch (Exception e) {
            Debug.logError("Exception in updateLeadContactStatus"+e.getMessage(), MODULE);
        }
        result.put("partyId", partyId);
        return result; 
    }
    
    public static Map<String, ?> convertLead(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
       /* Security security = dctx.getSecurity();*/
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String errMsg= null;
        String leadPartyId = (String) context.get("leadPartyId");
        String accountPartyId = (String) context.get("accountPartyId");
        Map<String, Object> results = ServiceUtil.returnSuccess();
        
        // make sure userLogin has CRMSFA_LEAD_UPDATE permission for this lead
        /*if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_LEAD", "_UPDATE", userLogin, leadPartyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
        }*/

        Map<String, Object> input = null;  // used later for service inputs
        try {
            Boolean accountValidation = accountValidation(delegator, accountPartyId);
            if (!accountValidation) {
                errMsg = UtilProperties.getMessage(resource, "invalidAccount", locale);
                results = ServiceUtil.returnError(errMsg);
                results.put("partyId", leadPartyId);
                return results;
            }
            GenericValue lead = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", leadPartyId), false);

            // create a PartyRole of type CONTACT for the lead
            Map<String, Object> serviceResults = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", leadPartyId, "roleTypeId", CrmConstants.RoleTypeConstants.CONTACT, "userLogin", userLogin));
            if (ServiceUtil.isError(serviceResults)) {
                errMsg = UtilProperties.getMessage(resource, "CrmErrorConvertLeadFail", locale);
                results = ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
                results.put("partyId", leadPartyId);
                return results;
            }

            GenericValue customFieldPartyClassification = EntityUtil.getFirst(delegator.findByAnd("CustomFieldPartyClassification",UtilMisc.toMap("partyId",leadPartyId),UtilMisc.toList("-createdStamp"), false));
            String partyClassificationGroupId = null;
            if(UtilValidate.isNotEmpty(customFieldPartyClassification)){
                partyClassificationGroupId = customFieldPartyClassification.getString("customFieldId");
            }
            
            // Validate Account Name Exists or Not
            List<GenericValue> partyGroupList = delegator.findByAnd("PartyGroup", UtilMisc.toMap("groupName", lead.getString("companyName")), null, false);
            if(partyGroupList != null && partyGroupList.size() > 0) {
                List<String> partyGroupListId = EntityUtil.getFieldListFromEntityList(partyGroupList, "partyId", true);
                if(partyGroupListId != null && partyGroupListId.size() > 0) {
                    List<GenericValue> partyRoleList = EntityQuery.use(delegator).from("PartyRole")
                            .where(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyGroupListId),
                            EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT")
                            ).queryList();
                    if(partyRoleList != null && partyRoleList.size() > 0) {
                        List<String> partyRoleListId = EntityUtil.getFieldListFromEntityList(partyRoleList, "partyId", true);
                        if(partyRoleListId != null && partyRoleListId.size() > 0) {
                            EntityCondition statusCondition = EntityCondition.makeCondition(
                                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"), EntityOperator.OR,
                                EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null));
                            statusCondition = EntityCondition.makeCondition(statusCondition, EntityOperator.AND,
                                EntityCondition.makeCondition("partyId", EntityOperator.IN, partyRoleListId));
                            GenericValue party = EntityUtil.getFirst(delegator.findList("Party", statusCondition, null, UtilMisc.toList("createdDate DESC"), null, false));
                            if( party != null && party.size() > 0) {
                                accountPartyId = party.getString("partyId");
                            }
                        }
                    }
                }
            }

            // if no account was given, then create an account based on the PartySupplementalData of the lead
            if (accountPartyId == null) {
                input = UtilMisc.toMap("accountName", lead.getString("companyName"), "description", lead.getString("description"), "userLogin", userLogin);
                input.put("parentPartyId", lead.getString("parentPartyId"));
                input.put("annualRevenue", lead.getBigDecimal("annualRevenue"));
                input.put("currencyUomId", lead.getString("currencyUomId"));
                input.put("numberEmployees", lead.getLong("numberEmployees"));
                input.put("industryEnumId", lead.getString("industryEnumId"));
                input.put("ownershipEnumId", lead.getString("ownershipEnumId"));
                input.put("importantNote", lead.getString("importantNote")); // The important note will be stored for account and contact
                input.put("sicCode", lead.getString("sicCode"));
                input.put("tickerSymbol", lead.getString("tickerSymbol"));
                input.put("partyClassificationGroupId", partyClassificationGroupId);
                serviceResults = dispatcher.runSync("crmsfa.createAccount", input);
                if (ServiceUtil.isError(serviceResults)) {
                    results = serviceResults;
                    results.put("partyId", leadPartyId);
                    return results;
                }
                accountPartyId = (String) serviceResults.get("partyId");

                // copy all the marketing campaigns over to the new account
                List<GenericValue> marketingCampaigns = delegator.findByAnd("MarketingCampaignRole", UtilMisc.toMap("partyId", leadPartyId, "roleTypeId", CrmConstants.RoleTypeConstants.LEAD), null, false);
                for (GenericValue marketingCampaign : marketingCampaigns) {
                    serviceResults = dispatcher.runSync("crmsfa.addAccountMarketingCampaign", UtilMisc.toMap("partyId", accountPartyId,
                            "marketingCampaignId", marketingCampaign.getString("marketingCampaignId"), "userLogin", userLogin));
                    if (ServiceUtil.isError(serviceResults)) {
                        errMsg = UtilProperties.getMessage(resource, "CrmErrorConvertLeadFail", locale);
                        results = ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
                        return results;
                    }
                }


                // copy all the contact mechs to the account
                serviceResults = dispatcher.runSync("copyPartyContactMechs", UtilMisc.toMap("partyIdFrom", leadPartyId, "partyIdTo", accountPartyId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    errMsg = UtilProperties.getMessage(resource, "CrmErrorConvertLeadFail", locale);
                    results = ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
                    results.put("partyId", leadPartyId);
                    return results;
                }

            }
            // copy all the datasources over to account
            List<GenericValue> dataSources = delegator.findByAnd("PartyDataSource", UtilMisc.toMap("partyId", leadPartyId), null, false);
            for (GenericValue dataSource : dataSources) {
                ModelService service = dctx.getModelService("crmsfa.addAccountDataSource");
                input = service.makeValid(dataSource, "IN");
                input.put("userLogin", userLogin);
                input.put("partyId", accountPartyId);
                serviceResults = dispatcher.runSync("crmsfa.addAccountDataSource", input);

                if (ServiceUtil.isError(serviceResults)) {
                    errMsg = UtilProperties.getMessage(resource, "CrmErrorConvertLeadFail", locale);
                    results = ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
                    results.put("partyId", leadPartyId);
                    return results;
                }
            }

            // copy all the notes to account
            List<GenericValue> notes = delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", leadPartyId), null, false);
            for (GenericValue note : notes) {
                String importantNote = (String) context.get("importantNote");
                if(UtilValidate.isNotEmpty(importantNote)) {
                    input = UtilMisc.toMap("partyId", leadPartyId, "note", importantNote, "userLogin", userLogin);
                    serviceResults = dispatcher.runSync("createPartyNote", input);
                }
            }

            // erase (null out) the PartySupplementalData fields from the lead
            GenericValue leadSupplementalData = delegator.findOne("PartySupplementalData", UtilMisc.toMap("partyId", leadPartyId), false);
            leadSupplementalData.set("parentPartyId", null);
            leadSupplementalData.set("annualRevenue", null);
            leadSupplementalData.set("currencyUomId", null);
            leadSupplementalData.set("numberEmployees", null);
            leadSupplementalData.set("industryEnumId", null);
            leadSupplementalData.set("ownershipEnumId", null);
            leadSupplementalData.set("sicCode", null);
            leadSupplementalData.set("tickerSymbol", null);
            leadSupplementalData.store();

            // assign the lead, who is now a contact, to the account
            input = UtilMisc.toMap("contactPartyId", leadPartyId, "accountPartyId", accountPartyId, "userLogin", userLogin);
            serviceResults = dispatcher.runSync("crmsfa.assignContactToAccount", input);
            if (ServiceUtil.isError(serviceResults)) {
                results = serviceResults;
                results.put("partyId", leadPartyId);
                return results;
            }

            // expire all lead party relationships (roleTypeIdFrom = PROSPECT)
            List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", leadPartyId, "roleTypeIdFrom", CrmConstants.RoleTypeConstants.LEAD), null, false);
            PartyHelper.expirePartyRelationships(partyRelationships, UtilDateTime.nowTimestamp(), dispatcher, userLogin);

            // make the userLogin a RESPONSIBLE_FOR CONTACT_OWNER of the CONTACT
            PartyHelper.createNewPartyToRelationship(userLogin.getString("partyId"), leadPartyId, CrmConstants.RoleTypeConstants.CONTACT, CrmConstants.PartyRelationshipTypeConstants.RESPONSIBLE_FOR, CrmConstants.SecurityGroupConstants.CONTACT_OWNER, PartyHelper.TEAM_MEMBER_ROLES, true, userLogin, delegator, dispatcher);

            // now we need to assign the account and contact to the lead's work efforts and expire all the lead ones
            List<GenericValue> associations = EntityUtil.filterByDate(delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("partyId", leadPartyId), null, false));
            for (GenericValue wepa : associations) {
                ModelService service = dctx.getModelService("assignPartyToWorkEffort");
                input = service.makeValid(wepa, "IN");
                input.put("userLogin", userLogin);

                // expire the current lead association (done by hand because service is suspect)
                wepa.set("thruDate", UtilDateTime.nowTimestamp());
                wepa.store();

                // assign the account to the work effort
                input.put("partyId", accountPartyId);
                input.put("fromDate", null);
                input.put("thruDate", null);
                input.put("roleTypeId", CrmConstants.RoleTypeConstants.ACCOUNT);
                serviceResults = dispatcher.runSync("assignPartyToWorkEffort", input);
                if (ServiceUtil.isError(serviceResults)) {
                    errMsg = UtilProperties.getMessage(resource, "CrmErrorConvertLeadFail", locale);
                    results = ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
                    results.put("partyId", leadPartyId);
                    return results;
                }

                // assign the contact to the work effort
                input.put("partyId", leadPartyId);
                input.put("fromDate", null);
                input.put("thruDate", null);
                input.put("roleTypeId", CrmConstants.RoleTypeConstants.CONTACT);
                serviceResults = dispatcher.runSync("assignPartyToWorkEffort", input);
                if (ServiceUtil.isError(serviceResults)) {
                    errMsg = UtilProperties.getMessage(resource, "CrmErrorConvertLeadFail", locale);
                    results = ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
                    results.put("partyId", leadPartyId);
                    return results;
                }
            }

            // opportunities assigned to the lead have to be updated to refer to both contact and account
            List<GenericValue> oppRoles = delegator.findByAnd("SalesOpportunityRole", UtilMisc.toMap("partyId", leadPartyId, "roleTypeId", CrmConstants.RoleTypeConstants.LEAD), null, false);
            for (GenericValue oppRole : oppRoles) {
                // create a CONTACT role using the leadPartyId
                input = UtilMisc.toMap("partyId", leadPartyId, "salesOpportunityId", oppRole.get("salesOpportunityId"), "roleTypeId", CrmConstants.RoleTypeConstants.CONTACT);
                GenericValue contactOppRole = delegator.makeValue("SalesOpportunityRole", input);
                contactOppRole.create();

                // create an ACCOUNT role for the new accountPartyId
                input = UtilMisc.toMap("partyId", accountPartyId, "salesOpportunityId", oppRole.get("salesOpportunityId"), "roleTypeId", CrmConstants.RoleTypeConstants.ACCOUNT);
                GenericValue accountOppRole = delegator.makeValue("SalesOpportunityRole", input);
                accountOppRole.create();

                // delete the PROSPECT role
                oppRole.remove();
            }

            // associate any lead files and bookmarks with both account and contact
            List<EntityCondition> conditions = UtilMisc.toList(
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadPartyId),
                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, CrmConstants.RoleTypeConstants.LEAD),
                    EntityUtil.getFilterByDateExpr()
            );
            List<GenericValue> contentRoles = delegator.findList("ContentRole", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
            for (GenericValue contentRole : contentRoles) {
                contentRole.set("thruDate", UtilDateTime.nowTimestamp());
                contentRole.store();

                GenericValue contactContentRole = delegator.makeValue("ContentRole");
                contactContentRole.set("partyId", leadPartyId);
                contactContentRole.set("contentId", contentRole.get("contentId"));
                contactContentRole.set("roleTypeId", CrmConstants.RoleTypeConstants.CONTACT);
                contactContentRole.set("fromDate", UtilDateTime.nowTimestamp());
                contactContentRole.create();

                GenericValue accountContent = delegator.makeValue("PartyContent");
                accountContent.set("partyId", accountPartyId);
                accountContent.set("contentId", contentRole.get("contentId"));
                accountContent.set("contentPurposeEnumId", "CNT_CRMSFA");
                accountContent.set("partyContentTypeId", "USERDEF");
                accountContent.set("fromDate", UtilDateTime.nowTimestamp());
                accountContent.create();

                GenericValue accountContentRole = delegator.makeValue("ContentRole");
                accountContentRole.set("partyId", accountPartyId);
                accountContentRole.set("contentId", contentRole.get("contentId"));
                accountContentRole.set("roleTypeId", CrmConstants.RoleTypeConstants.ACCOUNT);
                accountContentRole.set("fromDate", UtilDateTime.nowTimestamp());
                accountContentRole.create();
            }

            // set the status of the lead to PTYLEAD_CONVERTED
            serviceResults = dispatcher.runSync("setPartyStatus", UtilMisc.toMap("partyId", leadPartyId, "statusId", CrmConstants.PartyLeadStatus.LEAD_CONVERTED, "userLogin", userLogin));
            if (ServiceUtil.isError(serviceResults)) {
                errMsg = UtilProperties.getMessage(resource, "CrmErrorConvertLeadFail", locale);
                results = ServiceUtil.returnError(errMsg+" "+ServiceUtil.getErrorMessage(serviceResults));
                results.put("partyId", leadPartyId);
                return results;
            }
        } catch (GenericServiceException e) {
            errMsg = UtilProperties.getMessage(resource, "CrmErrorConvertLeadFail", locale);
            results = ServiceUtil.returnError(errMsg);
            results.put("partyId", leadPartyId);
            return results;
        } catch (GenericEntityException e) {
            errMsg = UtilProperties.getMessage(resource, "CrmErrorConvertLeadFail", locale);
            results = ServiceUtil.returnError(errMsg);
            results.put("partyId", leadPartyId);
            return results;
        }
        // put leadPartyId as partyId
        results.put("partyId", leadPartyId);
        results.put("accountPartyId", accountPartyId);
        return results;
    }
    
    /**
     * Delete a "new" lead. A new lead has status PTYLEAD_NEW, PTYLEAD_ASSIGNED or PTYLEAD_QUALIFIED.
     * This will physically remove the lead from the Party entity and related entities.
     * If the party was successfully deleted, the method will return a service success, otherwise it
     * will return a service error with the reason.
     */
    public static Map<String, ?> deleteLead(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String errMsg = null;
        String leadPartyId = (String) context.get("leadPartyId");

        // ensure delete permission on this lead
        /*if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_LEAD", "_DELETE", userLogin, leadPartyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
        }*/

        try {
            // first ensure the lead is "new" (note that there's no need to check for role because only leads can have these statuses)
            GenericValue lead = delegator.findOne("Party", UtilMisc.toMap("partyId", leadPartyId), false);
            if (lead == null) {
                errMsg = UtilProperties.getMessage(resource,
                        "CrmErrorLeadNotFound", 
                        UtilMisc.toMap("leadPartyId", context.get("parentPartyId")), locale);
                return ServiceUtil.returnError(errMsg);
            }
            String statusId = lead.getString("statusId");
            if (statusId == null || !(CrmConstants.PartyLeadStatus.LEAD_ASSIGNED.equals(statusId) || CrmConstants.PartyLeadStatus.LEAD_ACTIVE.equals(statusId) || CrmConstants.PartyLeadStatus.LEAD_BOOKING.equals(statusId) || CrmConstants.PartyLeadStatus.LEAD_SCHEDULED.equals(statusId) || CrmConstants.PartyLeadStatus.LEAD_QUALIFIED.equals(statusId))) {
                errMsg = UtilProperties.getMessage(resource, "CrmErrorDeleteLeadFail", locale);
                errMsg = errMsg+" "+UtilProperties.getMessage(resource, "CrmErrorLeadCannotDeleteFail", locale);
                return ServiceUtil.returnError(errMsg);
            }
            
            GenericValue leadParty = delegator.findOne("Party", UtilMisc.toMap("partyId", leadPartyId),false);
            leadParty.put("statusId", "PARTY_DISABLED");
            leadParty.store();
            
            // record deletion (note this entity has no primary key on partyId)
            delegator.create("PartyDeactivation", UtilMisc.toMap("partyId", leadPartyId, "deactivationTimestamp", UtilDateTime.nowTimestamp()));

            // delete!
            //PartyHelper.deleteCrmParty(leadPartyId, delegator);

        } catch (GenericEntityException e) {
            errMsg = UtilProperties.getMessage(resource, "CrmErrorDeleteLeadFail", locale);
            return ServiceUtil.returnError(errMsg);
        }
        return ServiceUtil.returnSuccess();
    }
    public static Boolean accountValidation(Delegator delegator, String partyId) {
        Boolean validAccount = false;
        if (UtilValidate.isNotEmpty(partyId)) {
            List < EntityCondition > accountConditions = new ArrayList < EntityCondition > ();
            accountConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                EntityUtil.getFilterByDateExpr()));

            // remove disabled parties
            accountConditions.add(EntityCondition.makeCondition(EntityOperator.OR,
                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
                EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)));

            List < GenericValue > partyFromReln;
            try {
                partyFromReln = delegator.findList("PartyAndRel", EntityCondition.makeCondition(accountConditions, EntityOperator.AND), UtilMisc.toSet("partyId"), UtilMisc.toList("createdDate"), null, false);
                if (partyFromReln != null && partyFromReln.size() > 0) {
                    validAccount = true;
                }
            } catch (GenericEntityException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
	    		Debug.logError(e.getMessage(), MODULE);

            }

        }
        return validAccount;
    }
    
    public static Map updateDataImpLead(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String actionType = (String) context.get("actionType");
    	String source = (String) context.get("source");
    	String backUrl = (String) context.get("backUrl");
    	
    	String leadId = (String) context.get("leadId");
    	String partyId = null;
    	
    	String firstName = (String) context.get("firstName");
    	String lastName = (String) context.get("lastName");
    	String address1 = (String) context.get("address1");
    	String primaryPhoneCountryCode = (String) context.get("primaryPhoneCountryCode");
    	String primaryPhoneNumber = (String) context.get("primaryPhoneNumber");
    	String secondaryPhoneCountryCode = (String) context.get("secondaryPhoneCountryCode");
    	String secondaryPhoneNumber = (String) context.get("secondaryPhoneNumber");
    	String attnName = (String) context.get("attnName");
    	String address2 = (String) context.get("address2");
    	String emailAddress = (String) context.get("emailAddress");
    	String webAddress = (String) context.get("webAddress");
    	String note = (String) context.get("note");
    	String city = (String) context.get("city");
    	String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
    	
    	String companyName = (String) context.get("companyName");
    	String parentCoDetails = (String) context.get("parentCoDetails");
    	BigDecimal salesTurnover = (BigDecimal) context.get("salesTurnover");
    	String dateOfIncorporation = (String) context.get("dateOfIncorporation");
    	String constitution = (String) context.get("constitution");
    	String industryCat = (String) context.get("industryCat");
    	String industry = (String) context.get("industry");
    	String customerTradingType = (String) context.get("customerTradingType");
    	String tallyUserType = (String) context.get("tallyUserType");
    	String tcpName = (String) context.get("tcpName");
    	String keyContactPerson1 = (String) context.get("keyContactPerson1");
    	String keyContactPerson2 = (String) context.get("keyContactPerson2");
    	String permanentAcccountNumber = (String) context.get("permanentAcccountNumber");
    	String businessRegNo = (String) context.get("businessRegNo");
    	String otherBankName = (String) context.get("otherBankName");
    	BigDecimal otherBankBalance = (BigDecimal) context.get("otherBankBalance");
    	String productsHeldInOthBank = (String) context.get("productsHeldInOthBank");
    	BigDecimal productsValueInOthBank = (BigDecimal) context.get("productsValueInOthBank");
    	BigDecimal paidupCapital = (BigDecimal) context.get("paidupCapital");
    	BigDecimal authorisedCap = (BigDecimal) context.get("authorisedCap");
    	String leadAssignTo = (String) context.get("leadAssignTo");
    	String leadAssignBy = (String) context.get("leadAssignBy");
    	String segment = (String) context.get("segment");
    	String liabOrAsset = (String) context.get("liabOrAsset");
    	
    	String teleCallingStatus = (String) context.get("teleCallingStatus");
    	String teleCallingSubStatus = (String) context.get("teleCallingSubStatus");
    	String teleCallingRemarks = (String) context.get("teleCallingRemarks");
    	
    	String rmCallingStatus = (String) context.get("rmCallingStatus");
    	String rmCallingSubStatus = (String) context.get("rmCallingSubStatus");
    	String rmCallingRemarks = (String) context.get("rmCallingRemarks");
    	
    	String title = (String) context.get("title");
    	Long noOfAttempt = (Long) context.get("noOfAttempt");
    	String postalCode = (String) context.get("postalCode");
    	String finacleId = (String) context.get("finacleId");
    	String placeOfIncorporation = (String) context.get("placeOfIncorporation");
    	Long noOfEmployees = (Long) context.get("noOfEmployees");
    	String designation = (String) context.get("designation");
    	String leadShortForm = (String) context.get("leadShortForm");
    	String leadShortFromMobile = (String) context.get("leadShortFromMobile");
    	String leadShortFromDesktop = (String) context.get("leadShortFromDesktop");
    	
    	String isNotDuplicate = (String) context.get("isNotDuplicate");
    	
    	String jobFamily = (String) context.get("jobFamily");
    	String leadScore = (String) context.get("leadScore"); // need to change the name
    	String virtualTeamId = (String) context.get("virtualTeamId");
    	
    	String primaryPartyId = null;
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("leadId", leadId);
    	result.put("partyId", leadId);
    	result.put("backUrl",backUrl);
    	
    	try {
        	
    		GenericValue lead = null;
    		boolean isTeleCallStatusChange = false;
    		boolean isRmCallStatusChange = false;
    		
    		if (UtilValidate.isNotEmpty(actionType) && actionType.equals("CREATE")) {
    			lead = delegator.makeValue("DataImportLead");
    		} else {
    			
    			EntityCondition existCondition = EntityCondition.makeCondition(
        				UtilMisc.toList(
        						EntityCondition.makeCondition("leadId", EntityOperator.EQUALS, leadId),
        						EntityCondition.makeCondition("primaryPartyId", EntityOperator.EQUALS, leadId)
        						), EntityOperator.OR);
    			
    			lead = EntityUtil.getFirst( delegator.findList("DataImportLead", existCondition, null, null, null, false) );
    			
        		if (UtilValidate.isEmpty(lead)) {
        			//result.putAll(ServiceUtil.returnError("Lead not exists!"));
        			result.put(ModelService.ERROR_MESSAGE, "Lead not exists!");
        			result.putAll(DataHelper.prepareImportLeadResult(actionType, "ERROR", backUrl));
        			return result;
        		}
        		
        		lead.put("leadId", lead.getString("leadId"));
        		partyId = lead.getString("primaryPartyId");
    		}
    		
    		/*EntityCondition existCondition = EntityCondition.makeCondition(
    				UtilMisc.toList(
    						EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, groupId),
    		                EntityCondition.makeCondition("groupName", EntityOperator.EQUALS, groupName)
    						), EntityOperator.AND);
        	
    		GenericValue existGroup = EntityUtil.getFirst( delegator.findList("CustomFieldGroup", existCondition, null, null, null, false) );
    		if (UtilValidate.isNotEmpty(existGroup)) {
    			result.putAll(ServiceUtil.returnError("Attribute field group name already exists exists!"));
    			return result;
    		}*/
    		
    		//lead.put("leadId", leadId);
    		
    		String employeePositionType = DataHelper.getEmployeePositionType(delegator, userLogin.getString("partyId"), userLogin.getString("countryGeoId"));
    		
    		if (UtilValidate.isEmpty(firstName) &&  UtilValidate.isNotEmpty(keyContactPerson1)) {
    			firstName = keyContactPerson1;
    		}
    		
    		/*if(UtilValidate.isNotEmpty(city)) {
    			
    			GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", city), false);
    			if(UtilValidate.isNotEmpty(enumeration)) {
    				city = enumeration.getString("description");
    			}
    		}*/
    		
    		lead.put("firstName", firstName);
    		lead.put("lastName", lastName);
    		lead.put("keyContactPerson1", keyContactPerson1);
    		
    		lead.put("address1", address1);
    		lead.put("attnName", attnName);
    		lead.put("address2", address2);
    		lead.put("emailAddress", emailAddress);
    		lead.put("webAddress", webAddress);
    		lead.put("note", note);
    		lead.put("primaryPhoneCountryCode", primaryPhoneCountryCode);
    		lead.put("primaryPhoneNumber", primaryPhoneNumber);
    		lead.put("secondaryPhoneCountryCode", secondaryPhoneCountryCode);
    		lead.put("secondaryPhoneNumber", secondaryPhoneNumber);
    		lead.put("city", city);
    		lead.put("stateProvinceGeoId", stateProvinceGeoId);
    		
    		lead.put("companyName", companyName);
    		lead.put("parentCoDetails", parentCoDetails);
    		lead.put("salesTurnover", salesTurnover);
    		lead.put("dateOfIncorporation", dateOfIncorporation);
    		lead.put("constitution", constitution);
    		lead.put("industryCat", industryCat);
    		lead.put("industry", industry);
    		lead.put("customerTradingType", customerTradingType);
    		lead.put("tallyUserType", tallyUserType);
    		lead.put("tcpName", tcpName);
    		
    		lead.put("keyContactPerson2", keyContactPerson2);
    		lead.put("permanentAcccountNumber", permanentAcccountNumber);
    		lead.put("businessRegNo", businessRegNo);
    		lead.put("otherBankName", otherBankName);
    		lead.put("otherBankBalance", otherBankBalance);
    		lead.put("productsHeldInOthBank", productsHeldInOthBank);
    		lead.put("productsValueInOthBank", productsValueInOthBank);
    		lead.put("paidupCapital", paidupCapital);
    		lead.put("authorisedCap", authorisedCap);
    		
			if ("Y".equals(leadShortForm)) {
				lead.put("leadAssignTo", userLogin.getString("partyId"));
				lead.put("leadAssignBy", userLogin.getString("partyId"));
			} else {
				lead.put("leadAssignTo", leadAssignTo);
				if (UtilValidate.isNotEmpty(leadAssignTo)) {
					lead.put("leadAssignBy", userLogin.getString("partyId"));
				}
			}
    		
    		lead.put("segment", segment);
    		lead.put("liabOrAsset", liabOrAsset);
    		
    		if (UtilValidate.isNotEmpty(teleCallingStatus) && (!teleCallingStatus.equals(lead.getString("teleCallingStatus")) || !teleCallingSubStatus.equals(lead.getString("teleCallingSubStatus")))) {
				isTeleCallStatusChange = true;
			}
    		if (UtilValidate.isNotEmpty(rmCallingStatus) && (!rmCallingStatus.equals(lead.getString("rmCallingStatus")) || !rmCallingSubStatus.equals(lead.getString("rmCallingSubStatus")))) {
				isRmCallStatusChange = true;
			}
    		
    		if (UtilValidate.isNotEmpty(employeePositionType) && employeePositionType.equals("DBS_TC")) {
    			lead.put("teleCallingStatus", teleCallingStatus);
        		lead.put("teleCallingSubStatus", teleCallingSubStatus);
        		lead.put("teleCallingRemarks", teleCallingRemarks);
    		} else if (UtilValidate.isNotEmpty(employeePositionType) && DataHelper.getFirstValidRoleTypeId(employeePositionType, CrmConstants.RM_ROLES)) {
	    		lead.put("rmCallingStatus", rmCallingStatus);
	    		lead.put("rmCallingSubStatus", rmCallingSubStatus);
	    		lead.put("rmCallingRemarks", rmCallingRemarks);
    		} else if ( UtilValidate.isEmpty(employeePositionType) || ( UtilValidate.isNotEmpty(employeePositionType) && (!employeePositionType.equals("DBS_TC") && !DataHelper.getFirstValidRoleTypeId(employeePositionType, CrmConstants.RM_ROLES)) ) ) {
    			lead.put("teleCallingStatus", teleCallingStatus);
        		lead.put("teleCallingSubStatus", teleCallingSubStatus);
        		lead.put("teleCallingRemarks", teleCallingRemarks);
        		
        		lead.put("rmCallingStatus", rmCallingStatus);
	    		lead.put("rmCallingSubStatus", rmCallingSubStatus);
	    		lead.put("rmCallingRemarks", rmCallingRemarks);
    		}
    		
    		/*if (UtilValidate.isEmpty(virtualTeamId)) {
    			Map<String, Object> virtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, userLogin.getString("partyId"));
    			virtualTeamId = ParamUtil.getString(virtualTeam, "virtualTeamId");
    		}*/
    		Debug.log("keyContactPerson1======"+keyContactPerson1);
	        Debug.log("keyContactPerson2======"+keyContactPerson2);
    		
    		lead.put("title", title);
    		lead.put("noOfAttempt", noOfAttempt);
    		lead.put("postalCode", postalCode);
    		lead.put("finacleId", finacleId);
    		lead.put("placeOfIncorporation", placeOfIncorporation);
    		lead.put("noOfEmployees", noOfEmployees);
    		lead.put("jobFamily", jobFamily);
    		lead.put("leadScore", UtilValidate.isNotEmpty(leadScore)? leadScore : "LEAD_SCORE_HOT");
    		lead.put("designation", designation);
    		lead.put("virtualTeamId", virtualTeamId);
    		
    		//if (UtilValidate.isNotEmpty(actionType) && actionType.equals("CREATE")) {
    			lead.put("source", source);
    		//}
    		
    		Map<String, Object> reqContext = FastMap.newInstance();
			
			reqContext.put("data", lead.getAllFields());
			reqContext.put("userLogin", userLogin);
			
			reqContext.put("batchId", lead.getString("batchId"));
			reqContext.put("taskName", "LEAD");
			reqContext.put("leadShortForm", leadShortForm);
			reqContext.put("tableName", "DataImportLead");
			
			reqContext.put("isNotDuplicate", isNotDuplicate);
			
			Debug.log("--leadShortForm---"+leadShortForm);
			
			Map<String, Object> validationResult = dispatcher.runSync("validator.validateLeadData", reqContext);
			
			if (UtilValidate.isEmpty(validationResult.get("leadId"))) {
				result.put(ModelService.ERROR_MESSAGE, "leadId not found!! Internal server error");
    			result.putAll(DataHelper.prepareImportLeadResult(actionType, "ERROR", backUrl));
    			return result;
			}
			
			if (!ServiceUtil.isError(validationResult)) {
				
				leadId = (String) validationResult.get("leadId");
				result.put("leadId", leadId);
		    	result.put("partyId", partyId);
		    	
				lead.put("leadId", validationResult.get("leadId"));
				lead.put("errorCodes", validationResult.get("errorCodes"));
				
				TransactionUtil.begin();
				
				if (UtilValidate.isEmpty(validationResult.get("errorCodes"))) {
					lead.put("importStatusId", "DATAIMP_APPROVED");
				} else {
					lead.put("importStatusId", "DATAIMP_ERROR");
				}
				
				/*
				if (UtilValidate.isEmpty(validationResult.get("errorCodes")) && (UtilValidate.isNotEmpty(actionType) && actionType.equals("UPDATE"))) {
					lead.put("importStatusId", "DATAIMP_APPROVED");
				} else if (UtilValidate.isEmpty(validationResult.get("errorCodes"))) {
					lead.put("importStatusId", "DATAIMP_NOT_APPROVED");
				} else {
					lead.put("importStatusId", "DATAIMP_ERROR");
				}
				*/
				
				String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
				
				if (UtilValidate.isNotEmpty(actionType) && actionType.equals("CREATE")) {
					
					if (UtilValidate.isNotEmpty(lead.get("leadId"))) {
						GenericValue entity = EntityUtil.getFirst(delegator.findByAnd("DataImportLead", UtilMisc.toMap("leadId", lead.get("leadId")), null, false));
						if (UtilValidate.isNotEmpty(entity)) {
							entity.remove();
						}
					}
					
					lead.put("batchId", batchId);
					lead.put("uploadedByUserLoginId", userLogin.get("userLoginId"));
					
					lead.put("leadStatus", "LEAD_PROSPECTING");
					
					lead.create();
					
					GenericValue LeadStatus = delegator.makeValue("LeadStatus");
					LeadStatus.put("seqId", delegator.getNextSeqId("LeadStatus"));
					LeadStatus.put("leadId", leadId);
					LeadStatus.put("statusId", "NEW");
					LeadStatus.create();
					
					String accessType = UtilProperties.getPropertyValue("Etl-Process", "UPLOAD_TYPE");
					String defaultModelId = UtilProperties.getPropertyValue("Etl-Process", "lead.import.default.modelId");
					String etlLeadTableName = UtilProperties.getPropertyValue("Etl-Process", "LEAD_TABLE");
					String defaultModelName = null;
					
					if (UtilValidate.isNotEmpty(defaultModelId)) {
						GenericValue etlModel = EntityUtil.getFirst(delegator.findByAnd("EtlModel", UtilMisc.toMap("modelId", defaultModelId), null, false));
						if (UtilValidate.isNotEmpty(etlModel)) {
							defaultModelName = etlModel.getString("modelName");
						}
					}
					
					if (UtilValidate.isNotEmpty(defaultModelName)) {
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", defaultModelName);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlLeadTableName);
						
						inputNew.put("taskName", "LEAD");
						inputNew.put("isExecuteModelProcess", false);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
					}
					
	    		} else {
	    			lead.store();
	    		}
				
				TransactionUtil.commit();
				
				if (UtilValidate.isNotEmpty(validationResult.get("errorCodes"))) {
					Map<String, Object> validatorResponse = (Map<String, Object>) validationResult.get("validatorResponse");
					Map<String, Object> validationMessage = (Map<String, Object>) validatorResponse.get("validationMessage");
					
					//result.putAll(ServiceUtil.returnError( "Validation failed!! Please check error codes" ));
					
					result.put(ModelService.ERROR_MESSAGE, "Validation failed!! Please check error codes");
	    			result.putAll(DataHelper.prepareImportLeadResult(actionType, "ERROR", backUrl));
					
					//result.putAll(ServiceUtil.returnError( StringUtil.mapToStr(validationMessage) ));
					//result.putAll(ServiceUtil.returnError( "<\n>sharif<\n>sumon" ));
					
					return result;
				}
				
				if (UtilValidate.isEmpty(validationResult.get("errorCodes")) && (UtilValidate.isNotEmpty(actionType) && 
						(actionType.equals("UPDATE") || actionType.equals("CREATE") || actionType.equals("STAGING"))
						)) {
					
					List<GenericValue> importDatas = new ArrayList<GenericValue>();
					importDatas.add(lead);
					
					Map<String, Object> importContext = new HashMap<String, Object>();
					importContext.put("userLogin", userLogin);
					importContext.put("importDatas", importDatas);
					
					importContext.put("employeePositionType", employeePositionType);
					importContext.put("batchId", batchId);
					importContext.put("isTeleCallStatusChange", isTeleCallStatusChange);
					importContext.put("isRmCallStatusChange", isRmCallStatusChange);
					
					Map<String, Object> importResult = dispatcher.runSync("crmsfa.importLeads", importContext);
					if (ServiceUtil.isSuccess(importResult)) {
						GenericValue entity = EntityUtil.getFirst(delegator.findByAnd("DataImportLead", UtilMisc.toMap("leadId", lead.get("leadId")), null, false));
						primaryPartyId = entity.getString("primaryPartyId");
						result.put("partyId", entity.getString("primaryPartyId"));
						
						if (actionType.equals("CREATE")) {
							Map<String, Object> statusChangeContext = new HashMap<String, Object>();
							statusChangeContext.put("leadPartyId", leadId);
							statusChangeContext.put("leadContactStatusId", lead.getString("leadStatus"));
							statusChangeContext.put("userLogin", userLogin);
							
							Map<String, Object> statusChangeResult = dispatcher.runSync("crmsfa.updateLeadContactStatus", statusChangeContext);
							
							if (!ServiceUtil.isError(statusChangeResult)) {
								Debug.logInfo("Successfully Lead Status Change, fromStatusId=null, toStatusId="+lead.getString("leadStatus"), MODULE);
							}
						}
						
					}
					
				}
				
			}
			
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		//result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put(ModelService.ERROR_MESSAGE, e.getMessage());
    		result.putAll(DataHelper.prepareImportLeadResult(actionType, "ERROR", backUrl));
			return result;
		}
    	
    	String actionMessage = "updated";
    	if (UtilValidate.isNotEmpty(actionType) && actionType.equals("CREATE")) {
    		actionMessage = "created";
    	}
    	Debug.log("==leadShortFromMobile=="+leadShortFromMobile);
    	//result.putAll(ServiceUtil.returnSuccess("Successfully updated lead.."));
        if("Y".equals(leadShortForm) && !"Y".equalsIgnoreCase(leadShortFromMobile) && !"Y".equalsIgnoreCase(leadShortFromDesktop)) {
            result.put(ModelService.SUCCESS_MESSAGE, "Lead Successfully "+actionMessage+", the Lead ID is "+primaryPartyId);
        }else {
            if( !"Y".equalsIgnoreCase(leadShortFromMobile) && !"Y".equalsIgnoreCase(leadShortFromDesktop)) {
              result.put(ModelService.SUCCESS_MESSAGE, "Successfully "+actionMessage+" lead..");
        	}
    	}
    	
    	result.putAll(DataHelper.prepareImportLeadResult(actionType, "SUCCESS", backUrl));
    	
    	return result;
    	
    }
    
    public static Map importLeads(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	List<GenericValue> importDatas = (List<GenericValue>) context.get("importDatas");
    	
    	Boolean isTeleCallStatusChange = (Boolean) context.get("isTeleCallStatusChange");
    	Boolean isRmCallStatusChange = (Boolean) context.get("isRmCallStatusChange");
    	
    	String employeePositionType = (String) context.get("employeePositionType");
    	String batchId = (String) context.get("batchId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	//result.put("leadId", leadId);
    	
    	try {
    		Debug.logInfo("importDatas=="+importDatas, MODULE);
			Map<String, Object> inputNew = new HashMap<String, Object>();
			inputNew.put("organizationPartyId", "Company");
			inputNew.put("userLogin", userLogin);
			inputNew.put("importDatas", importDatas);
			
			result = dispatcher.runSync("importLeads", inputNew);
			if (ServiceUtil.isSuccess(result)) {
				int processCount = 0;
				if (UtilValidate.isNotEmpty(result.get("importedRecords"))) {
					processCount = Integer.parseInt(result.get("importedRecords").toString());
				}
				Debug.logInfo("total processCount: "+processCount, MODULE);
				
				GenericValue job = EntityUtil.getFirst(delegator.findByAnd("EtlPreProcessor", UtilMisc.toMap("batchId", batchId), null, false));
				if (UtilValidate.isNotEmpty(job)) {
					job.put("statusId", "FINISHED");
					job.put("processedCount", String.valueOf(processCount));
					job.store();
				}
				
				List<GenericValue> importedDataList = (List<GenericValue>) result.get("importedDataList");
				if (UtilValidate.isNotEmpty(importedDataList)) {
					
					List<String> leadIds = EntityUtil.getFieldListFromEntityList(importedDataList, "leadId", true);
					
					String initialResponsiblePartyId = "admin";
			    	String initialResponsibleRoleTypeId = "EMPLOYEE";
			    	Timestamp importTimestamp = UtilDateTime.nowTimestamp();
			    	
					Map<String, Object> partyRelFilterContext = new HashMap<String, Object>();
					partyRelFilterContext.put("roleTypeIdFrom", "CONTACT");
					partyRelFilterContext.put("roleTypeIdTo", "LEAD");
					partyRelFilterContext.put("partyRelationshipTypeId", "CONTACT_REL_INV");
					
					for (GenericValue importedData : importedDataList) {
						
						// assign responsible for [start]
						String leadId = importedData.getString("primaryPartyId");
						String existingResponsibleForId = DataHelper.getResponsibleForParty(delegator, leadId);
						String responsibleForId = null;
						GenericValue partySupplementalData = EntityUtil.getFirst( delegator.findByAnd("PartySupplementalData", UtilMisc.toMap("partyId", leadId), null, false) );
						if (UtilValidate.isNotEmpty(importedData.getString("leadAssignTo"))/* && DataHelper.isResponsibleForParty(delegator, importedData.getString("leadAssignTo"))*/) {
							GenericValue isOneBankId = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", importedData.getString("leadAssignTo")), true);
							if(UtilValidate.isNotEmpty(isOneBankId)) {
								responsibleForId = isOneBankId.getString("partyId");
							}else {
								responsibleForId = importedData.getString("leadAssignTo");
							}
						} else {
							if(UtilValidate.isNotEmpty(leadId) && UtilValidate.isEmpty(existingResponsibleForId)) {
								responsibleForId = userLogin.getString("partyId");
							}else if(UtilValidate.isEmpty(leadId)) {
								responsibleForId = userLogin.getString("partyId");
							}
						}
						String primaryPhoneNumber = importedData.getString("primaryPhoneNumber");
						String secondaryPhoneNumber = importedData.getString("secondaryPhoneNumber");
						String tertiaryPhoneNumber = importedData.getString("tertiaryPhoneNumber");
						String quaternaryPhoneNumber = importedData.getString("quaternaryPhoneNumber");
						String quinaryPhoneNumber = importedData.getString("quinaryPhoneNumber");
						String cin = importedData.getString("companyCin");
						
						//Adding data in Party Attribute table
						String gstn = importedData.getString("gstn");
						String iecCode = importedData.getString("iecCode");
						List<GenericValue> aoPartyAtttrs = new ArrayList<GenericValue>();

						GenericValue gstnData = delegator.makeValue("PartyIdentification");
						gstnData.put("partyId", leadId);
						gstnData.put("partyIdentificationTypeId", "gstn");			
						gstnData.put("idValue", UtilValidate.isNotEmpty(gstn)? gstn : "");
						aoPartyAtttrs.add(gstnData);
						
						GenericValue iecData = delegator.makeValue("PartyIdentification");
						iecData.put("partyId", leadId);
						iecData.put("partyIdentificationTypeId", "iecCode");			
						iecData.put("idValue", UtilValidate.isNotEmpty(iecCode)? iecCode : "");
						aoPartyAtttrs.add(iecData);
						
						if(UtilValidate.isNotEmpty(cin) && (cin.length() == 8 || cin.length() == 21)) {
							Debug.log("=====cin length==="+cin.length());
						}

						GenericValue cinData = delegator.makeValue("PartyIdentification");
						cinData.put("partyId", leadId);
						cinData.put("partyIdentificationTypeId", "cin");			
						cinData.put("idValue", UtilValidate.isNotEmpty(cin)? cin : "");
						aoPartyAtttrs.add(cinData);
						delegator.storeAll(aoPartyAtttrs);
						//End
						
						/*if (UtilValidate.isNotEmpty(DataHelper.getResponsibleForParty(delegator, leadId))) {
							continue;
						}*/
						
						String countryGeoId = "IND";
						if (UtilValidate.isNotEmpty( userLogin.getString("countryGeoId") )) {
							countryGeoId = userLogin.getString("countryGeoId");
						}
						
						/*if (UtilValidate.isNotEmpty(importedData.getString("leadAssignTo")) && DataHelper.isResponsibleForParty(delegator, importedData.getString("leadAssignTo"))) {
							responsibleForId = importedData.getString("leadAssignTo");
						} Bypasing this assignment logic*/
						/*else {
							responsibleForId = DataHelper.getResponsibleForParty(delegator, importedData.getString("jobFamily"), countryGeoId, importedData.getString("city"));
						}*/
						
						/*if (UtilValidate.isEmpty(responsibleForId) && UtilValidate.isNotEmpty(importedData.getString("uploadedByUserLoginId"))) {
							GenericValue uploadedByUserLogin = EntityUtil.getFirst( delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", importedData.getString("uploadedByUserLoginId")), null, false) );
							if (DataHelper.isResponsibleForParty(delegator, uploadedByUserLogin.getString("partyId"))) {
								responsibleForId = uploadedByUserLogin.getString("partyId");
							}
						}*/
						Boolean personResponsibleForValidation = true;
				        if(UtilValidate.isNotEmpty(primaryPhoneNumber) && UtilValidate.isNotEmpty(secondaryPhoneNumber)) {
				            Map<String, Object> dndStatusPrimaryPhoneMp = DataUtil.getDndStatus(delegator, primaryPhoneNumber);
				            Map<String, Object> dndStatusSecondaryPhoneMp = DataUtil.getDndStatus(delegator, secondaryPhoneNumber);
				            if("Y".equals(dndStatusPrimaryPhoneMp.get("dndStatus")) && "Y".equals(dndStatusSecondaryPhoneMp.get("dndStatus"))) {
				            	personResponsibleForValidation = false;
				            	EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
										EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId),
							            EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
							            EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("LEAD")),
							            EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
							            EntityUtil.getFilterByDateExpr()
							            ), EntityOperator.AND);

								List<GenericValue> responsibleFor = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).orderBy("fromDate DESC").queryList();
								if (responsibleFor != null && responsibleFor.size() > 0) {
									for(GenericValue responsibleForGV: responsibleFor) {
										responsibleForGV.put("thruDate", UtilDateTime.nowTimestamp());
										responsibleForGV.store();
									}
								}
				            }
				        }
						/*if ( (UtilValidate.isEmpty(existingResponsibleForId) && UtilValidate.isNotEmpty(responsibleForId) && personResponsibleForValidation) 
								|| (UtilValidate.isNotEmpty(existingResponsibleForId) && UtilValidate.isNotEmpty(responsibleForId) && !existingResponsibleForId.equals(responsibleForId) && personResponsibleForValidation)
								) {*/
				        if (UtilValidate.isNotEmpty(responsibleForId) ) {
							Map<String, Object> associationContext = new HashMap<String, Object>();
							associationContext.put("partyId", leadId);
							associationContext.put("roleTypeIdFrom", "LEAD");
							associationContext.put("accountPartyId", responsibleForId);
							associationContext.put("userLogin", userLogin);
							
							Map<String, Object> associationResult = dispatcher.runSync("crmsfa.updatePersonResponsibleFor", associationContext);
							
							if (!ServiceUtil.isError(associationResult)) {
								Debug.logInfo("Successfully Changed Account Responsible For, leadPartyId="+leadId+", responsiblePartyId="+responsibleForId, MODULE);
								
								List<Map<String, Object>> validationAuditLogList = new ArrayList<Map<String, Object>>();
                                validationAuditLogList.add(WriterUtil.prepareValidationAudit(null, "rmPartyId", existingResponsibleForId, responsibleForId, userLogin.getString("userLoginId"), ValidationAuditType.VAT_RM_REASSIGN, "Reassign "));
                                String pkCombinedValueText = leadId + "::" + leadId;
                                WriterUtil.writeValidationAudit(delegator, pkCombinedValueText, validationAuditLogList);
								
							}
							
						} else if (UtilValidate.isEmpty(responsibleForId) /*&& personResponsibleForValidation*/) {
							
							EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
									EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId),
						            EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
						            EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD", "CONTACT")),
						            EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
						            EntityUtil.getFilterByDateExpr()
						            ), EntityOperator.AND);

							GenericValue responsibleFor = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).orderBy("fromDate DESC").queryFirst();
							if (UtilValidate.isNotEmpty(responsibleFor)) {
								responsibleFor.put("thruDate", UtilDateTime.nowTimestamp());
								responsibleFor.store();
							}
							
						}
						
						// assign responsible for [end]
						
						// call status change [start]
						
						if (UtilValidate.isNotEmpty(employeePositionType) && employeePositionType.equals("DBS_TC") && isTeleCallStatusChange) {
							storeCallStatusChangeHistory(dispatcher, userLogin, leadId, importedData.getString("teleCallingStatus"), importedData.getString("teleCallingSubStatus"));
						} else if (UtilValidate.isNotEmpty(employeePositionType) && DataHelper.getFirstValidRoleTypeId(employeePositionType, CrmConstants.RM_ROLES) && isRmCallStatusChange) {
							storeCallStatusChangeHistory(dispatcher, userLogin, leadId, importedData.getString("rmCallingStatus"), importedData.getString("rmCallingSubStatus"));
						} else if ( UtilValidate.isEmpty(employeePositionType) || ( UtilValidate.isNotEmpty(employeePositionType) && (!employeePositionType.equals("DBS_TC") && !DataHelper.getFirstValidRoleTypeId(employeePositionType, CrmConstants.RM_ROLES)) ) ) {
							
							if (isTeleCallStatusChange) {
								storeCallStatusChangeHistory(dispatcher, userLogin, leadId, importedData.getString("teleCallingStatus"), importedData.getString("teleCallingSubStatus"));
							}
							
							if (isRmCallStatusChange) {
								storeCallStatusChangeHistory(dispatcher, userLogin, leadId, importedData.getString("rmCallingStatus"), importedData.getString("rmCallingSubStatus"));
							}
							
						}
						
						// call status change [end]
						// import key contact person 1 [start]
				        
				        List<GenericValue> toBeStored = FastList.newInstance();
				        if(UtilValidate.isNotEmpty(importedData.getString("firstName1")) || UtilValidate.isNotEmpty(importedData.getString("lastName1"))) {
				        	toBeStored = FastList.newInstance();
				        	
			        		/*Map<String, Object> input = FastMap.newInstance();
			            	input.put("telno", primaryPhoneNumber);
			            	input.put("userLogin", userLogin);
			            	result = dispatcher.runSync("findPartyFromTelephoneComplete", input);*/ 
			        		
			            	String keyContactPerson1PartyId = PartyHelper.findPartyFromNameExt(delegator, importedData.getString("firstName1"),importedData.getString("lastName1"));
			            	
		            		// TODO need to add into LeadDataValidator
		            		if (UtilValidate.isNotEmpty(keyContactPerson1PartyId)) {
		            			if (UtilValidate.isEmpty( PartyHelper.getFirstValidRoleTypeId(keyContactPerson1PartyId, UtilMisc.toList("CONTACT"), delegator) )) {
		            				keyContactPerson1PartyId = null;
		            				Debug.logInfo("403, keyContactPerson1PartyId="+keyContactPerson1PartyId, MODULE);
		            			}
		            		}
		            		
		            		if (UtilValidate.isEmpty(keyContactPerson1PartyId) && (UtilValidate.isNotEmpty(importedData.getString("firstName1")) || UtilValidate.isNotEmpty(importedData.getString("lastName1"))) ) {
		            			keyContactPerson1PartyId = delegator.getNextSeqId("Party");
		            			String firstName = importedData.getString("firstName1");
		            			String lastName = importedData.getString("lastName1");
		            			toBeStored.addAll(UtilImport.makePartyWithRolesExt(keyContactPerson1PartyId, "PERSON", null,  UtilMisc.toList("CUSTOMER", "BILL_TO_CUSTOMER"), delegator));
		                        GenericValue person = delegator.makeValue("Person", UtilMisc.toMap("partyId", keyContactPerson1PartyId, "firstName", firstName, "lastName", lastName,"designation",importedData.getString("designation1") ,"uniqueIDNumber" ,importedData.getString("uniqueIDNumber1"), "dinNumber" , importedData.getString("dinNumber1")));
		                        toBeStored.add(person);
		                        
		                    	toBeStored.addAll(UtilImport.makePartyWithRolesExt(keyContactPerson1PartyId, "PERSON", null,  UtilMisc.toList("CONTACT"), delegator));
		                    	Map<String, Object> partyRelationship = UtilMisc.toMap("partyIdTo", initialResponsiblePartyId, "roleTypeIdTo", initialResponsibleRoleTypeId, "partyIdFrom", keyContactPerson1PartyId, "roleTypeIdFrom", "CONTACT", "partyRelationshipTypeId", "RESPONSIBLE_FOR", "fromDate", importTimestamp);
		                        partyRelationship.put("securityGroupId", "CONTACT_OWNER");
		                        Debug.logInfo("418, partyRelationship="+partyRelationship, MODULE);
		                        toBeStored.add(delegator.makeValue("PartyRelationship", partyRelationship));
		            		}
		            		
		            		if (UtilValidate.isNotEmpty(keyContactPerson1PartyId) && (UtilValidate.isNotEmpty(importedData.getString("firstName1")) || UtilValidate.isNotEmpty(importedData.getString("lastName1"))) ) {
		            			GenericValue person = EntityUtil.getFirst(delegator.findByAnd("Person",  UtilMisc.toMap("partyId", keyContactPerson1PartyId), null, false));
		            			Debug.logInfo("424, person exists", MODULE);
		            			if (UtilValidate.isNotEmpty(person)) {
		            				person.put("firstName", importedData.getString("firstName1"));
		            				person.put("lastName", importedData.getString("lastName1"));
		            				toBeStored.add(person);
		            			}
		            		}
		            		
		            		delegator.storeAll(toBeStored);
		            		Debug.log("keyContactPerson1PartyId=========="+keyContactPerson1PartyId);
		            		Debug.log("keyContactPerson1=========="+importedData.getString("firstName1"));
		            		if (UtilValidate.isNotEmpty(keyContactPerson1PartyId)) {
		            			toBeStored = FastList.newInstance();
		            			partySupplementalData.put("keyContactPerson1PartyId", keyContactPerson1PartyId);
		            			
		            			partyRelFilterContext.put("partyIdFrom", keyContactPerson1PartyId);
								partyRelFilterContext.put("partyIdTo", leadId);
		            			
		            			Map<String, Object> associationContext = new HashMap<String, Object>();
								associationContext.put("leadPartyId", leadId);
								associationContext.put("contactPartyId", keyContactPerson1PartyId);
								associationContext.put("userLogin", userLogin);
								
								Map<String, Object> associationResult = dispatcher.runSync("crmsfa.assignContactToLead", associationContext);
								if (!ServiceUtil.isError(associationResult)) {
									Debug.logInfo("446, associationResult"+associationResult, MODULE);
									Debug.logInfo("Successfully Lead and Contact associated, leadPartyId="+leadId+", contactPartyId="+keyContactPerson1PartyId, MODULE);
								}
								
								String partyRelAssocId = DataUtil.getPartyRelAssocId(delegator, partyRelFilterContext);
								Debug.logInfo("451, accountPartyId="+partyRelAssocId, MODULE);
								// update PRIMARY mobile [start]
					         	
								String partyRelAssocContactMechId = "";
					         	GenericValue mobilePurpose = DataUtil.getActivePartyContactMechPurpose(delegator, keyContactPerson1PartyId, "PRIMARY_PHONE", partyRelAssocId);
					         	if(UtilValidate.isNotEmpty(mobilePurpose)){
					         		//GenericValue mobileContactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", mobilePurpose.getString("contactMechId")));
					         		String contactMechId = mobilePurpose.getString("contactMechId");
					        		
					        		GenericValue phoneContactMech = EntityUtil.getFirst(delegator.findByAnd("TelecomNumber",  UtilMisc.toMap("contactMechId", contactMechId), null, false));
					        		if (UtilValidate.isNotEmpty(phoneContactMech) && DataUtil.isContactPhoneChange(delegator, keyContactPerson1PartyId, partyRelAssocId, primaryPhoneNumber, "PRIMARY_PHONE")) {
					        			//phoneContactMech.put("contactMechId", contactMechId);
						        		//phoneContactMech.put("countryCode", entry.getString("primaryPhoneCountryCode"));
						        		//phoneContactMech.put("areaCode", entry.getString("primaryPhoneAreaCode"));
						        		phoneContactMech.put("contactNumber", primaryPhoneNumber);
						        		toBeStored.add(phoneContactMech);
					        		}
					        		partyRelAssocContactMechId = contactMechId;
					         	} else if (!UtilValidate.isEmpty(primaryPhoneNumber)) {
					                // make the mobile no
					                GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));
					                
					                GenericValue primaryNumber = UtilImport.makeTelecomNumber(contactMech, null, null, primaryPhoneNumber, delegator);
					                primaryNumber.put("phoneValidInd", "Y");
					                
					                toBeStored.add(contactMech);
					                toBeStored.add(primaryNumber);
					                
				                    toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", contactMech.get("contactMechId"), "partyId", keyContactPerson1PartyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
				                    toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_PHONE", primaryNumber, keyContactPerson1PartyId, importTimestamp, delegator, partyRelAssocId));
				                    partyRelAssocContactMechId = contactMech.getString("contactMechId");
					            }
					         	if (UtilValidate.isNotEmpty(partyRelAssocId) && UtilValidate.isNotEmpty(partyRelAssocContactMechId)) {
			                    	GenericValue partyRelAssoc = EntityUtil.getFirst(delegator.findByAnd("PartyRelationshipAssoc", UtilMisc.toMap("partyRelAssocId", partyRelAssocId, "assocTypeId", "PHONE", "assocId", partyRelAssocContactMechId), null, false));
			                    	if (UtilValidate.isEmpty(partyRelAssoc)) {
			                    		partyRelAssoc = delegator.makeValue("PartyRelationshipAssoc");
			                    		partyRelAssoc.put("partyRelAssocId", partyRelAssocId);
			                    		partyRelAssoc.put("assocSeqId", delegator.getNextSeqIdLong("PartyRelationshipAssoc"));
			                    		partyRelAssoc.put("assocTypeId", "PHONE");
			                    		partyRelAssoc.put("assocId", partyRelAssocContactMechId);
			                    		partyRelAssoc.put("solicitationStatus", "Y");
			                    		toBeStored.add(partyRelAssoc);
			                    	}
					         	}
					         	Debug.logInfo("495, toBeStored="+toBeStored, MODULE);
					         	delegator.storeAll(toBeStored);
					         	
					         	// update PRIMARY mobile [end]
					            // update PRIMARY EMAIL [start]
					          	if ((importedData.getString("emailAddress1") != null) && !importedData.getString("emailAddress1").equals("")) {
					         		Map<String, Object>  serviceResults = dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("partyId", keyContactPerson1PartyId, "userLogin", userLogin,
											"contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL", "emailAddress", importedData.getString("emailAddress1")));
									if (ServiceUtil.isError(serviceResults)) {
										Debug.logInfo("serviceResults"+serviceResults, MODULE);
									}
									Debug.logInfo("====primaryEmailContactMechId===="+serviceResults.get("contactMechId"), MODULE);
									//results.put("primaryEmailContactMechId", serviceResults.get("contactMechId"));
								}
					         	//END
		            		}
			        	}
				        
				        // import key contact person1 [end]
				        
				        // import key contact person 2 [start]
				        Debug.log("keyContactPerson2=========="+importedData.getString("firstName2"));
				        toBeStored = FastList.newInstance();
				        if(UtilValidate.isNotEmpty(importedData.getString("firstName2")) || UtilValidate.isNotEmpty(importedData.getString("lastName2"))) {
				        	toBeStored = FastList.newInstance();
				        	
			        		/*Map<String, Object> input = FastMap.newInstance();
			            	input.put("telno", secondaryPhoneNumber);
			            	input.put("userLogin", userLogin);
			            	result = dispatcher.runSync("findPartyFromTelephoneComplete", input); */
			        		
			            	String keyContactPerson2PartyId = PartyHelper.findPartyFromNameExt(delegator, importedData.getString("firstName2"),importedData.getString("lastName2"));
			            	
		            		// TODO need to add into LeadDataValidator
		            		if (UtilValidate.isNotEmpty(keyContactPerson2PartyId)) {
		            			if (UtilValidate.isEmpty( PartyHelper.getFirstValidRoleTypeId(keyContactPerson2PartyId, UtilMisc.toList("CONTACT"), delegator) )) {
		            				keyContactPerson2PartyId = null;
		            			}
		            		}
		            		
		            		if (UtilValidate.isEmpty(keyContactPerson2PartyId) && (UtilValidate.isNotEmpty(importedData.getString("firstName2")) || UtilValidate.isNotEmpty(importedData.getString("lastName2"))) ) {
		            			keyContactPerson2PartyId = delegator.getNextSeqId("Party");
		            			String firstName = importedData.getString("firstName2");
		            			String lastName = importedData.getString("lastName2");
		            			toBeStored.addAll(UtilImport.makePartyWithRolesExt(keyContactPerson2PartyId, "PERSON", null,  UtilMisc.toList("CUSTOMER", "BILL_TO_CUSTOMER"), delegator));
		                        GenericValue person = delegator.makeValue("Person", UtilMisc.toMap("partyId", keyContactPerson2PartyId, "firstName", firstName, "lastName", lastName,"designation",importedData.getString("designation2") ,"uniqueIDNumber" ,importedData.getString("uniqueIDNumber2"), "dinNumber" , importedData.getString("dinNumber2")));
		                        toBeStored.add(person);
		                        
		                    	toBeStored.addAll(UtilImport.makePartyWithRolesExt(keyContactPerson2PartyId, "PERSON", null,  UtilMisc.toList("CONTACT"), delegator));
		                    	Map<String, Object> partyRelationship = UtilMisc.toMap("partyIdTo", initialResponsiblePartyId, "roleTypeIdTo", initialResponsibleRoleTypeId, "partyIdFrom", keyContactPerson2PartyId, "roleTypeIdFrom", "CONTACT", "partyRelationshipTypeId", "RESPONSIBLE_FOR", "fromDate", importTimestamp);
		                        partyRelationship.put("securityGroupId", "CONTACT_OWNER");
		                        toBeStored.add(delegator.makeValue("PartyRelationship", partyRelationship));
		            		}
		            		Debug.logInfo("537, keyContactPerson2PartyId="+keyContactPerson2PartyId, MODULE);
		            		if (UtilValidate.isNotEmpty(keyContactPerson2PartyId) && (UtilValidate.isNotEmpty(importedData.getString("firstName2")) || UtilValidate.isNotEmpty(importedData.getString("lastName2"))) ) {
		            			GenericValue person = EntityUtil.getFirst(delegator.findByAnd("Person",  UtilMisc.toMap("partyId", keyContactPerson2PartyId), null, false));
		            			if (UtilValidate.isNotEmpty(person)) {
		            				person.put("firstName", importedData.getString("firstName2")); // need to change
		            				person.put("lastName", importedData.getString("lastName2"));
		            				toBeStored.add(person);
		            			}
		            		}
		            		
		            		delegator.storeAll(toBeStored);
		            		Debug.log("keyContactPerson2PartyId=========="+keyContactPerson2PartyId);
		            		if (UtilValidate.isNotEmpty(keyContactPerson2PartyId)) {
		            			toBeStored = FastList.newInstance();
		            			partySupplementalData.put("keyContactPerson2PartyId", keyContactPerson2PartyId);
		            			
		            			partyRelFilterContext.put("partyIdFrom", keyContactPerson2PartyId);
								partyRelFilterContext.put("partyIdTo", leadId);
		            			
		            			Map<String, Object> associationContext = new HashMap<String, Object>();
								associationContext.put("leadPartyId", leadId);
								associationContext.put("contactPartyId", keyContactPerson2PartyId);
								associationContext.put("userLogin", userLogin);
								
								Map<String, Object> associationResult = dispatcher.runSync("crmsfa.assignContactToLead", associationContext);
								if (!ServiceUtil.isError(associationResult)) {
									Debug.logInfo("Successfully Lead and Contact associated, leadPartyId="+leadId+", contactPartyId="+keyContactPerson2PartyId, MODULE);
								}
								
								String partyRelAssocId = DataUtil.getPartyRelAssocId(delegator, partyRelFilterContext);
		            		
								// update PRIMARY mobile [start]
					         	
								String partyRelAssocContactMechId = "";
					         	GenericValue mobilePurpose = DataUtil.getActivePartyContactMechPurpose(delegator, keyContactPerson2PartyId, "PRIMARY_PHONE", partyRelAssocId);
					         	if(UtilValidate.isNotEmpty(mobilePurpose)){
					         		//GenericValue mobileContactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", mobilePurpose.getString("contactMechId")));
					         		String contactMechId = mobilePurpose.getString("contactMechId");
					        		
					        		GenericValue phoneContactMech = EntityUtil.getFirst(delegator.findByAnd("TelecomNumber",  UtilMisc.toMap("contactMechId", contactMechId), null, false));
					        		if (UtilValidate.isNotEmpty(phoneContactMech) && DataUtil.isContactPhoneChange(delegator, keyContactPerson2PartyId, partyRelAssocId, secondaryPhoneNumber, "PRIMARY_PHONE")) {
					        			//phoneContactMech.put("contactMechId", contactMechId);
						        		//phoneContactMech.put("countryCode", entry.getString("primaryPhoneCountryCode"));
						        		//phoneContactMech.put("areaCode", entry.getString("primaryPhoneAreaCode"));
						        		phoneContactMech.put("contactNumber", secondaryPhoneNumber);
						        		toBeStored.add(phoneContactMech);
					        		}
					        		partyRelAssocContactMechId = contactMechId;
					         	} else if (!UtilValidate.isEmpty(secondaryPhoneNumber)) {
					                // make the mobile no
					                GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));
					                
					                GenericValue primaryNumber = UtilImport.makeTelecomNumber(contactMech, null, null, secondaryPhoneNumber, delegator);
					                primaryNumber.put("phoneValidInd", "Y");
					                
					                toBeStored.add(contactMech);
					                toBeStored.add(primaryNumber);
					                
				                    toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", contactMech.get("contactMechId"), "partyId", keyContactPerson2PartyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
				                    toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_PHONE", primaryNumber, keyContactPerson2PartyId, importTimestamp, delegator, partyRelAssocId));
				                    partyRelAssocContactMechId = contactMech.getString("contactMechId");
					            }
					         	if (UtilValidate.isNotEmpty(partyRelAssocId) && UtilValidate.isNotEmpty(partyRelAssocContactMechId)) {
			                    	GenericValue partyRelAssoc = EntityUtil.getFirst(delegator.findByAnd("PartyRelationshipAssoc", UtilMisc.toMap("partyRelAssocId", partyRelAssocId, "assocTypeId", "PHONE", "assocId", partyRelAssocContactMechId), null, false));
			                    	if (UtilValidate.isEmpty(partyRelAssoc)) {
			                    		partyRelAssoc = delegator.makeValue("PartyRelationshipAssoc");
			                    		partyRelAssoc.put("partyRelAssocId", partyRelAssocId);
			                    		partyRelAssoc.put("assocSeqId", delegator.getNextSeqIdLong("PartyRelationshipAssoc"));
			                    		partyRelAssoc.put("assocTypeId", "PHONE");
			                    		partyRelAssoc.put("assocId", partyRelAssocContactMechId);
			                    		partyRelAssoc.put("solicitationStatus", "Y");
			                    		toBeStored.add(partyRelAssoc);
			                    	}
					         	}
					         	
					         	delegator.storeAll(toBeStored);
					         	
					         	// update PRIMARY mobile [end]
					            // update PRIMARY EMAIL [start]
					         	if ((importedData.getString("emailAddress2") != null) && !importedData.getString("emailAddress2").equals("")) {
					         		Map<String, Object>  serviceResults = dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("partyId", keyContactPerson2PartyId, "userLogin", userLogin,
											"contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL", "emailAddress", importedData.getString("emailAddress2")));
									if (ServiceUtil.isError(serviceResults)) {
										Debug.logInfo("serviceResults"+serviceResults, MODULE);
									}
									Debug.logInfo("====primaryEmailContactMechId===="+serviceResults.get("contactMechId"), MODULE);
									//results.put("primaryEmailContactMechId", serviceResults.get("contactMechId"));
								}
					         	//END
		            		}
			        	}
				        
				        // import key contact person2 [end]
						
				     // import key contact person 3 [start]
				        Debug.log("keyContactPerson3=========="+importedData.getString("firstName3"));
				        toBeStored = FastList.newInstance();
				        if(UtilValidate.isNotEmpty(importedData.getString("firstName3")) || UtilValidate.isNotEmpty(importedData.getString("lastName3"))) {
				        	toBeStored = FastList.newInstance();
				        	
			        		/*Map<String, Object> input = FastMap.newInstance();
			            	input.put("telno", secondaryPhoneNumber);
			            	input.put("userLogin", userLogin);
			            	result = dispatcher.runSync("findPartyFromTelephoneComplete", input); */
			        		
			            	String keyContactPerson3PartyId = PartyHelper.findPartyFromNameExt(delegator, importedData.getString("firstName3"),importedData.getString("lastName3"));
			            	
		            		// TODO need to add into LeadDataValidator
		            		if (UtilValidate.isNotEmpty(keyContactPerson3PartyId)) {
		            			if (UtilValidate.isEmpty( PartyHelper.getFirstValidRoleTypeId(keyContactPerson3PartyId, UtilMisc.toList("CONTACT"), delegator) )) {
		            				keyContactPerson3PartyId = null;
		            			}
		            		}
		            		
		            		if (UtilValidate.isEmpty(keyContactPerson3PartyId) && (UtilValidate.isNotEmpty(importedData.getString("firstName3")) || UtilValidate.isNotEmpty(importedData.getString("lastName3"))) ) {
		            			keyContactPerson3PartyId = delegator.getNextSeqId("Party");
		            			String firstName = importedData.getString("firstName3");
		            			String lastName = importedData.getString("lastName3");
		            			toBeStored.addAll(UtilImport.makePartyWithRolesExt(keyContactPerson3PartyId, "PERSON", null,  UtilMisc.toList("CUSTOMER", "BILL_TO_CUSTOMER"), delegator));
		                        GenericValue person = delegator.makeValue("Person", UtilMisc.toMap("partyId", keyContactPerson3PartyId, "firstName", firstName, "lastName", lastName,"designation",importedData.getString("designation3") ,"uniqueIDNumber" ,importedData.getString("uniqueIDNumber3"), "dinNumber" , importedData.getString("dinNumber3")));
		                        toBeStored.add(person);
		                        
		                    	toBeStored.addAll(UtilImport.makePartyWithRolesExt(keyContactPerson3PartyId, "PERSON", null,  UtilMisc.toList("CONTACT"), delegator));
		                    	Map<String, Object> partyRelationship = UtilMisc.toMap("partyIdTo", initialResponsiblePartyId, "roleTypeIdTo", initialResponsibleRoleTypeId, "partyIdFrom", keyContactPerson3PartyId, "roleTypeIdFrom", "CONTACT", "partyRelationshipTypeId", "RESPONSIBLE_FOR", "fromDate", importTimestamp);
		                        partyRelationship.put("securityGroupId", "CONTACT_OWNER");
		                        toBeStored.add(delegator.makeValue("PartyRelationship", partyRelationship));
		            		}
		            		Debug.logInfo("537, keyContactPerson2PartyId="+keyContactPerson3PartyId, MODULE);
		            		if (UtilValidate.isNotEmpty(keyContactPerson3PartyId) && (UtilValidate.isNotEmpty(importedData.getString("firstName3")) || UtilValidate.isNotEmpty(importedData.getString("lastName3"))) ) {
		            			GenericValue person = EntityUtil.getFirst(delegator.findByAnd("Person",  UtilMisc.toMap("partyId", keyContactPerson3PartyId), null, false));
		            			if (UtilValidate.isNotEmpty(person)) {
		            				person.put("firstName", importedData.getString("firstName3")); // need to change
		            				person.put("lastName", importedData.getString("lastName3"));
		            				toBeStored.add(person);
		            			}
		            		}
		            		
		            		delegator.storeAll(toBeStored);
		            		Debug.log("keyContactPerson3PartyId=========="+keyContactPerson3PartyId);
		            		if (UtilValidate.isNotEmpty(keyContactPerson3PartyId)) {
		            			toBeStored = FastList.newInstance();
		            			partySupplementalData.put("keyContactPerson2PartyId", keyContactPerson3PartyId);
		            			
		            			partyRelFilterContext.put("partyIdFrom", keyContactPerson3PartyId);
								partyRelFilterContext.put("partyIdTo", leadId);
		            			
		            			Map<String, Object> associationContext = new HashMap<String, Object>();
								associationContext.put("leadPartyId", leadId);
								associationContext.put("contactPartyId", keyContactPerson3PartyId);
								associationContext.put("userLogin", userLogin);
								
								Map<String, Object> associationResult = dispatcher.runSync("crmsfa.assignContactToLead", associationContext);
								if (!ServiceUtil.isError(associationResult)) {
									Debug.logInfo("Successfully Lead and Contact associated, leadPartyId="+leadId+", contactPartyId="+keyContactPerson3PartyId, MODULE);
								}
								
								String partyRelAssocId = DataUtil.getPartyRelAssocId(delegator, partyRelFilterContext);
		            		
								// update PRIMARY mobile [start]
					         	
								String partyRelAssocContactMechId = "";
					         	GenericValue mobilePurpose = DataUtil.getActivePartyContactMechPurpose(delegator, keyContactPerson3PartyId, "PRIMARY_PHONE", partyRelAssocId);
					         	if(UtilValidate.isNotEmpty(mobilePurpose)){
					         		//GenericValue mobileContactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", mobilePurpose.getString("contactMechId")));
					         		String contactMechId = mobilePurpose.getString("contactMechId");
					        		
					        		GenericValue phoneContactMech = EntityUtil.getFirst(delegator.findByAnd("TelecomNumber",  UtilMisc.toMap("contactMechId", contactMechId), null, false));
					        		if (UtilValidate.isNotEmpty(phoneContactMech) && DataUtil.isContactPhoneChange(delegator, keyContactPerson3PartyId, partyRelAssocId, tertiaryPhoneNumber, "PRIMARY_PHONE")) {
					        			//phoneContactMech.put("contactMechId", contactMechId);
						        		//phoneContactMech.put("countryCode", entry.getString("primaryPhoneCountryCode"));
						        		//phoneContactMech.put("areaCode", entry.getString("primaryPhoneAreaCode"));
						        		phoneContactMech.put("contactNumber", tertiaryPhoneNumber);
						        		toBeStored.add(phoneContactMech);
					        		}
					        		partyRelAssocContactMechId = contactMechId;
					         	} else if (!UtilValidate.isEmpty(tertiaryPhoneNumber)) {
					                // make the mobile no
					                GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));
					                
					                GenericValue primaryNumber = UtilImport.makeTelecomNumber(contactMech, null, null, tertiaryPhoneNumber, delegator);
					                primaryNumber.put("phoneValidInd", "Y");
					                
					                toBeStored.add(contactMech);
					                toBeStored.add(primaryNumber);
					                
				                    toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", contactMech.get("contactMechId"), "partyId", keyContactPerson3PartyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
				                    toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_PHONE", primaryNumber, keyContactPerson3PartyId, importTimestamp, delegator, partyRelAssocId));
				                    partyRelAssocContactMechId = contactMech.getString("contactMechId");
					            }
					         	if (UtilValidate.isNotEmpty(partyRelAssocId) && UtilValidate.isNotEmpty(partyRelAssocContactMechId)) {
			                    	GenericValue partyRelAssoc = EntityUtil.getFirst(delegator.findByAnd("PartyRelationshipAssoc", UtilMisc.toMap("partyRelAssocId", partyRelAssocId, "assocTypeId", "PHONE", "assocId", partyRelAssocContactMechId), null, false));
			                    	if (UtilValidate.isEmpty(partyRelAssoc)) {
			                    		partyRelAssoc = delegator.makeValue("PartyRelationshipAssoc");
			                    		partyRelAssoc.put("partyRelAssocId", partyRelAssocId);
			                    		partyRelAssoc.put("assocSeqId", delegator.getNextSeqIdLong("PartyRelationshipAssoc"));
			                    		partyRelAssoc.put("assocTypeId", "PHONE");
			                    		partyRelAssoc.put("assocId", partyRelAssocContactMechId);
			                    		partyRelAssoc.put("solicitationStatus", "Y");
			                    		toBeStored.add(partyRelAssoc);
			                    	}
					         	}
					         	
					         	delegator.storeAll(toBeStored);
					         	
					         	// update PRIMARY mobile [end]
					            // update PRIMARY EMAIL [start]
					         	if ((importedData.getString("emailAddress3") != null) && !importedData.getString("emailAddress3").equals("")) {
					         		Map<String, Object>  serviceResults = dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("partyId", keyContactPerson3PartyId, "userLogin", userLogin,
											"contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL", "emailAddress", importedData.getString("emailAddress3")));
									if (ServiceUtil.isError(serviceResults)) {
										Debug.logInfo("serviceResults"+serviceResults, MODULE);
									}
									Debug.logInfo("====primaryEmailContactMechId===="+serviceResults.get("contactMechId"), MODULE);
									//results.put("primaryEmailContactMechId", serviceResults.get("contactMechId"));
								}
					         	//END
		            		}
			        	}
				        
				        // import key contact person3 [end]
				        
				     // import key contact person 4 [start]
				        Debug.log("keyContactPerson4=========="+importedData.getString("firstName4"));
				        toBeStored = FastList.newInstance();
				        if(UtilValidate.isNotEmpty(importedData.getString("firstName4")) || UtilValidate.isNotEmpty(importedData.getString("lastName4"))) {
				        	toBeStored = FastList.newInstance();
				        	
			        		/*Map<String, Object> input = FastMap.newInstance();
			            	input.put("telno", secondaryPhoneNumber);
			            	input.put("userLogin", userLogin);
			            	result = dispatcher.runSync("findPartyFromTelephoneComplete", input); */
			        		
			            	String keyContactPerson4PartyId = PartyHelper.findPartyFromNameExt(delegator, importedData.getString("firstName4"),importedData.getString("lastName4"));
			            	
		            		// TODO need to add into LeadDataValidator
		            		if (UtilValidate.isNotEmpty(keyContactPerson4PartyId)) {
		            			if (UtilValidate.isEmpty( PartyHelper.getFirstValidRoleTypeId(keyContactPerson4PartyId, UtilMisc.toList("CONTACT"), delegator) )) {
		            				keyContactPerson4PartyId = null;
		            			}
		            		}
		            		
		            		if (UtilValidate.isEmpty(keyContactPerson4PartyId) && (UtilValidate.isNotEmpty(importedData.getString("firstName4")) || UtilValidate.isNotEmpty(importedData.getString("lastName4"))) ) {
		            			keyContactPerson4PartyId = delegator.getNextSeqId("Party");
		            			String firstName = importedData.getString("firstName4");
		            			String lastName = importedData.getString("lastName4");
		            			toBeStored.addAll(UtilImport.makePartyWithRolesExt(keyContactPerson4PartyId, "PERSON", null,  UtilMisc.toList("CUSTOMER", "BILL_TO_CUSTOMER"), delegator));
		                        GenericValue person = delegator.makeValue("Person", UtilMisc.toMap("partyId", keyContactPerson4PartyId, "firstName", firstName, "lastName", lastName,"designation",importedData.getString("designation4") ,"uniqueIDNumber" ,importedData.getString("uniqueIDNumber4"), "dinNumber" , importedData.getString("dinNumber4")));
		                        toBeStored.add(person);
		                        
		                    	toBeStored.addAll(UtilImport.makePartyWithRolesExt(keyContactPerson4PartyId, "PERSON", null,  UtilMisc.toList("CONTACT"), delegator));
		                    	Map<String, Object> partyRelationship = UtilMisc.toMap("partyIdTo", initialResponsiblePartyId, "roleTypeIdTo", initialResponsibleRoleTypeId, "partyIdFrom", keyContactPerson4PartyId, "roleTypeIdFrom", "CONTACT", "partyRelationshipTypeId", "RESPONSIBLE_FOR", "fromDate", importTimestamp);
		                        partyRelationship.put("securityGroupId", "CONTACT_OWNER");
		                        toBeStored.add(delegator.makeValue("PartyRelationship", partyRelationship));
		            		}
		            		Debug.logInfo("537, keyContactPerson2PartyId="+keyContactPerson4PartyId, MODULE);
		            		if (UtilValidate.isNotEmpty(keyContactPerson4PartyId) && (UtilValidate.isNotEmpty(importedData.getString("firstName4")) || UtilValidate.isNotEmpty(importedData.getString("lastName4"))) ) {
		            			GenericValue person = EntityUtil.getFirst(delegator.findByAnd("Person",  UtilMisc.toMap("partyId", keyContactPerson4PartyId), null, false));
		            			if (UtilValidate.isNotEmpty(person)) {
		            				person.put("firstName", importedData.getString("firstName4"));
		            				person.put("lastName", importedData.getString("lastName4"));
		            				toBeStored.add(person);
		            			}
		            		}
		            		
		            		delegator.storeAll(toBeStored);
		            		Debug.log("keyContactPerson4PartyId=========="+keyContactPerson4PartyId);
		            		if (UtilValidate.isNotEmpty(keyContactPerson4PartyId)) {
		            			toBeStored = FastList.newInstance();
		            			partySupplementalData.put("keyContactPerson2PartyId", keyContactPerson4PartyId);
		            			
		            			partyRelFilterContext.put("partyIdFrom", keyContactPerson4PartyId);
								partyRelFilterContext.put("partyIdTo", leadId);
		            			
		            			Map<String, Object> associationContext = new HashMap<String, Object>();
								associationContext.put("leadPartyId", leadId);
								associationContext.put("contactPartyId", keyContactPerson4PartyId);
								associationContext.put("userLogin", userLogin);
								
								Map<String, Object> associationResult = dispatcher.runSync("crmsfa.assignContactToLead", associationContext);
								if (!ServiceUtil.isError(associationResult)) {
									Debug.logInfo("Successfully Lead and Contact associated, leadPartyId="+leadId+", contactPartyId="+keyContactPerson4PartyId, MODULE);
								}
								
								String partyRelAssocId = DataUtil.getPartyRelAssocId(delegator, partyRelFilterContext);
		            		
								// update PRIMARY mobile [start]
					         	
								String partyRelAssocContactMechId = "";
					         	GenericValue mobilePurpose = DataUtil.getActivePartyContactMechPurpose(delegator, keyContactPerson4PartyId, "PRIMARY_PHONE", partyRelAssocId);
					         	if(UtilValidate.isNotEmpty(mobilePurpose)){
					         		//GenericValue mobileContactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", mobilePurpose.getString("contactMechId")));
					         		String contactMechId = mobilePurpose.getString("contactMechId");
					        		
					        		GenericValue phoneContactMech = EntityUtil.getFirst(delegator.findByAnd("TelecomNumber",  UtilMisc.toMap("contactMechId", contactMechId), null, false));
					        		if (UtilValidate.isNotEmpty(phoneContactMech) && DataUtil.isContactPhoneChange(delegator, keyContactPerson4PartyId, partyRelAssocId, quaternaryPhoneNumber, "PRIMARY_PHONE")) {
					        			//phoneContactMech.put("contactMechId", contactMechId);
						        		//phoneContactMech.put("countryCode", entry.getString("primaryPhoneCountryCode"));
						        		//phoneContactMech.put("areaCode", entry.getString("primaryPhoneAreaCode"));
						        		phoneContactMech.put("contactNumber", quaternaryPhoneNumber);
						        		toBeStored.add(phoneContactMech);
					        		}
					        		partyRelAssocContactMechId = contactMechId;
					         	} else if (!UtilValidate.isEmpty(quaternaryPhoneNumber)) {
					                // make the mobile no
					                GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));
					                
					                GenericValue primaryNumber = UtilImport.makeTelecomNumber(contactMech, null, null, quaternaryPhoneNumber, delegator);
					                primaryNumber.put("phoneValidInd", "Y");
					                
					                toBeStored.add(contactMech);
					                toBeStored.add(primaryNumber);
					                
				                    toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", contactMech.get("contactMechId"), "partyId", keyContactPerson4PartyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
				                    toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_PHONE", primaryNumber, keyContactPerson4PartyId, importTimestamp, delegator, partyRelAssocId));
				                    partyRelAssocContactMechId = contactMech.getString("contactMechId");
					            }
					         	if (UtilValidate.isNotEmpty(partyRelAssocId) && UtilValidate.isNotEmpty(partyRelAssocContactMechId)) {
			                    	GenericValue partyRelAssoc = EntityUtil.getFirst(delegator.findByAnd("PartyRelationshipAssoc", UtilMisc.toMap("partyRelAssocId", partyRelAssocId, "assocTypeId", "PHONE", "assocId", partyRelAssocContactMechId), null, false));
			                    	if (UtilValidate.isEmpty(partyRelAssoc)) {
			                    		partyRelAssoc = delegator.makeValue("PartyRelationshipAssoc");
			                    		partyRelAssoc.put("partyRelAssocId", partyRelAssocId);
			                    		partyRelAssoc.put("assocSeqId", delegator.getNextSeqIdLong("PartyRelationshipAssoc"));
			                    		partyRelAssoc.put("assocTypeId", "PHONE");
			                    		partyRelAssoc.put("assocId", partyRelAssocContactMechId);
			                    		partyRelAssoc.put("solicitationStatus", "Y");
			                    		toBeStored.add(partyRelAssoc);
			                    	}
					         	}
					         	
					         	delegator.storeAll(toBeStored);
					         	
					         	// update PRIMARY mobile [end]
					            // update PRIMARY EMAIL [start]
					         	if ((importedData.getString("emailAddress4") != null) && !importedData.getString("emailAddress4").equals("")) {
					         		Map<String, Object>  serviceResults = dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("partyId", keyContactPerson4PartyId, "userLogin", userLogin,
											"contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL", "emailAddress", importedData.getString("emailAddress4")));
									if (ServiceUtil.isError(serviceResults)) {
										Debug.logInfo("serviceResults"+serviceResults, MODULE);
									}
									Debug.logInfo("====primaryEmailContactMechId===="+serviceResults.get("contactMechId"), MODULE);
									//results.put("primaryEmailContactMechId", serviceResults.get("contactMechId"));
								}
					         	//END
		            		}
			        	}
				        
				        // import key contact person4 [end]
				     // import key contact person 5 [start]
				        Debug.log("keyContactPerson5=========="+importedData.getString("firstName5"));
				        toBeStored = FastList.newInstance();
				        if(UtilValidate.isNotEmpty(importedData.getString("firstName5")) || UtilValidate.isNotEmpty(importedData.getString("lastName5"))) {
				        	toBeStored = FastList.newInstance();
				        	
			        		/*Map<String, Object> input = FastMap.newInstance();
			            	input.put("telno", secondaryPhoneNumber);
			            	input.put("userLogin", userLogin);
			            	result = dispatcher.runSync("findPartyFromTelephoneComplete", input); */
			        		
			            	String keyContactPerson5PartyId = PartyHelper.findPartyFromNameExt(delegator, importedData.getString("firstName5"),importedData.getString("lastName5"));
			            	
		            		// TODO need to add into LeadDataValidator
		            		if (UtilValidate.isNotEmpty(keyContactPerson5PartyId)) {
		            			if (UtilValidate.isEmpty( PartyHelper.getFirstValidRoleTypeId(keyContactPerson5PartyId, UtilMisc.toList("CONTACT"), delegator) )) {
		            				keyContactPerson5PartyId = null;
		            			}
		            		}
		            		
		            		if (UtilValidate.isEmpty(keyContactPerson5PartyId) && (UtilValidate.isNotEmpty(importedData.getString("firstName5")) || UtilValidate.isNotEmpty(importedData.getString("lastName5"))) ) {
		            			keyContactPerson5PartyId = delegator.getNextSeqId("Party");
		            			String firstName = importedData.getString("firstName5");
		            			String lastName = importedData.getString("lastName5");
		            			toBeStored.addAll(UtilImport.makePartyWithRolesExt(keyContactPerson5PartyId, "PERSON", null,  UtilMisc.toList("CUSTOMER", "BILL_TO_CUSTOMER"), delegator));
		                        GenericValue person = delegator.makeValue("Person", UtilMisc.toMap("partyId", keyContactPerson5PartyId, "firstName", firstName, "lastName", lastName,"designation",importedData.getString("designation5") ,"uniqueIDNumber" ,importedData.getString("uniqueIDNumber5"), "dinNumber" , importedData.getString("dinNumber5")));
		                        toBeStored.add(person);
		                        
		                    	toBeStored.addAll(UtilImport.makePartyWithRolesExt(keyContactPerson5PartyId, "PERSON", null,  UtilMisc.toList("CONTACT"), delegator));
		                    	Map<String, Object> partyRelationship = UtilMisc.toMap("partyIdTo", initialResponsiblePartyId, "roleTypeIdTo", initialResponsibleRoleTypeId, "partyIdFrom", keyContactPerson5PartyId, "roleTypeIdFrom", "CONTACT", "partyRelationshipTypeId", "RESPONSIBLE_FOR", "fromDate", importTimestamp);
		                        partyRelationship.put("securityGroupId", "CONTACT_OWNER");
		                        toBeStored.add(delegator.makeValue("PartyRelationship", partyRelationship));
		            		}
		            		Debug.logInfo(" keyContactPerson5PartyId="+keyContactPerson5PartyId, MODULE);
		            		if (UtilValidate.isNotEmpty(keyContactPerson5PartyId) && (UtilValidate.isNotEmpty(importedData.getString("firstName5")) || UtilValidate.isNotEmpty(importedData.getString("lastName5"))) ) {
		            			GenericValue person = EntityUtil.getFirst(delegator.findByAnd("Person",  UtilMisc.toMap("partyId", keyContactPerson5PartyId), null, false));
		            			if (UtilValidate.isNotEmpty(person)) {
		            				person.put("firstName", importedData.getString("firstName5"));
		            				person.put("lastName", importedData.getString("lastName5"));
		            				toBeStored.add(person);
		            			}
		            		}
		            		
		            		delegator.storeAll(toBeStored);
		            		Debug.log("keyContactPerson5PartyId=========="+keyContactPerson5PartyId);
		            		if (UtilValidate.isNotEmpty(keyContactPerson5PartyId)) {
		            			toBeStored = FastList.newInstance();
		            			partySupplementalData.put("keyContactPerson2PartyId", keyContactPerson5PartyId);
		            			
		            			partyRelFilterContext.put("partyIdFrom", keyContactPerson5PartyId);
								partyRelFilterContext.put("partyIdTo", leadId);
		            			
		            			Map<String, Object> associationContext = new HashMap<String, Object>();
								associationContext.put("leadPartyId", leadId);
								associationContext.put("contactPartyId", keyContactPerson5PartyId);
								associationContext.put("userLogin", userLogin);
								
								Map<String, Object> associationResult = dispatcher.runSync("crmsfa.assignContactToLead", associationContext);
								if (!ServiceUtil.isError(associationResult)) {
									Debug.logInfo("Successfully Lead and Contact associated, leadPartyId="+leadId+", contactPartyId="+keyContactPerson5PartyId, MODULE);
								}
								
								String partyRelAssocId = DataUtil.getPartyRelAssocId(delegator, partyRelFilterContext);
		            		
								// update PRIMARY mobile [start]
					         	
								String partyRelAssocContactMechId = "";
					         	GenericValue mobilePurpose = DataUtil.getActivePartyContactMechPurpose(delegator, keyContactPerson5PartyId, "PRIMARY_PHONE", partyRelAssocId);
					         	if(UtilValidate.isNotEmpty(mobilePurpose)){
					         		//GenericValue mobileContactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", mobilePurpose.getString("contactMechId")));
					         		String contactMechId = mobilePurpose.getString("contactMechId");
					        		
					        		GenericValue phoneContactMech = EntityUtil.getFirst(delegator.findByAnd("TelecomNumber",  UtilMisc.toMap("contactMechId", contactMechId), null, false));
					        		if (UtilValidate.isNotEmpty(phoneContactMech) && DataUtil.isContactPhoneChange(delegator, keyContactPerson5PartyId, partyRelAssocId, quinaryPhoneNumber, "PRIMARY_PHONE")) {
					        			//phoneContactMech.put("contactMechId", contactMechId);
						        		//phoneContactMech.put("countryCode", entry.getString("primaryPhoneCountryCode"));
						        		//phoneContactMech.put("areaCode", entry.getString("primaryPhoneAreaCode"));
						        		phoneContactMech.put("contactNumber", quinaryPhoneNumber);
						        		toBeStored.add(phoneContactMech);
					        		}
					        		partyRelAssocContactMechId = contactMechId;
					         	} else if (!UtilValidate.isEmpty(quinaryPhoneNumber)) {
					                // make the mobile no
					                GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));
					                
					                GenericValue primaryNumber = UtilImport.makeTelecomNumber(contactMech, null, null, quinaryPhoneNumber, delegator);
					                primaryNumber.put("phoneValidInd", "Y");
					                
					                toBeStored.add(contactMech);
					                toBeStored.add(primaryNumber);
					                
				                    toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", contactMech.get("contactMechId"), "partyId", keyContactPerson5PartyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
				                    toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_PHONE", primaryNumber, keyContactPerson5PartyId, importTimestamp, delegator, partyRelAssocId));
				                    partyRelAssocContactMechId = contactMech.getString("contactMechId");
					            }
					         	if (UtilValidate.isNotEmpty(partyRelAssocId) && UtilValidate.isNotEmpty(partyRelAssocContactMechId)) {
			                    	GenericValue partyRelAssoc = EntityUtil.getFirst(delegator.findByAnd("PartyRelationshipAssoc", UtilMisc.toMap("partyRelAssocId", partyRelAssocId, "assocTypeId", "PHONE", "assocId", partyRelAssocContactMechId), null, false));
			                    	if (UtilValidate.isEmpty(partyRelAssoc)) {
			                    		partyRelAssoc = delegator.makeValue("PartyRelationshipAssoc");
			                    		partyRelAssoc.put("partyRelAssocId", partyRelAssocId);
			                    		partyRelAssoc.put("assocSeqId", delegator.getNextSeqIdLong("PartyRelationshipAssoc"));
			                    		partyRelAssoc.put("assocTypeId", "PHONE");
			                    		partyRelAssoc.put("assocId", partyRelAssocContactMechId);
			                    		partyRelAssoc.put("solicitationStatus", "Y");
			                    		toBeStored.add(partyRelAssoc);
			                    	}
					         	}
					         	
					         	delegator.storeAll(toBeStored);
					         	
					         	// update PRIMARY mobile [end]
					            // update PRIMARY EMAIL [start]
					         	if ((importedData.getString("emailAddress5") != null) && !importedData.getString("emailAddress5").equals("")) {
					         		Map<String, Object>  serviceResults = dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("partyId", keyContactPerson5PartyId, "userLogin", userLogin,
											"contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL", "emailAddress", importedData.getString("emailAddress5")));
									if (ServiceUtil.isError(serviceResults)) {
										Debug.logInfo("serviceResults"+serviceResults, MODULE);
									}
									Debug.logInfo("====primaryEmailContactMechId===="+serviceResults.get("contactMechId"), MODULE);
									//results.put("primaryEmailContactMechId", serviceResults.get("contactMechId"));
								}
					         	//END
		            		}
			        	}
				        
				        // import key contact person5 [end]
						// Remove Data enrich segment if value updated
						
						String dedupAutoSegmentValueId = UtilProperties.getPropertyValue("crm", "dedup.auto.segmentValueId");
			        	if (UtilValidate.isNotEmpty(dedupAutoSegmentValueId)) {
			        		
			        		GenericValue segmentValue = delegator.findOne("CustomField", UtilMisc.toMap("customFieldId", dedupAutoSegmentValueId), false);
			        		if (UtilValidate.isNotEmpty(segmentValue)) {
			        			
			        			String partyId = leadId;
								GenericValue partyDedupSegment = delegator.findOne("CustomFieldPartyClassification", UtilMisc.toMap("groupId", segmentValue.getString("groupId"), "customFieldId", dedupAutoSegmentValueId, "partyId", partyId), false);
								if (UtilValidate.isNotEmpty(partyDedupSegment)) {
									
									EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
											EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)
											);      

									GenericValue partyContactMech = EntityUtil.getFirst( delegator.findList("PartyContactMech", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
									if (UtilValidate.isNotEmpty(partyContactMech)) {
										partyDedupSegment.remove();
										
									}
									
									
								}
			        		}
			        	}
			        	// call and call log creation [start]
			        	String contactId = importedData.getString("contactedMobileNumber");
			        	String contactMechId = null;
			        	String howdDidTheCallGo = importedData.getString("howdDidTheCallGo");
			        	String reasonOfLeadDrop = importedData.getString("reasonOfLeadDrop");
			        	String reasonOfLeadDropNote = importedData.getString("reasonOfLeadDropNote");
			        	String callNotes = importedData.getString("callNotes");
			        	if(UtilValidate.isNotEmpty(contactId)) {
			        		Map<String, Object> callInput = UtilMisc.<String, Object>toMap("leadId",  leadId, "contactId", contactId, "contactMechId", contactMechId);
				        	Debug.logInfo("====Call create===="+callInput, MODULE);
				        	Map<String, Object>  callServiceResults = dispatcher.runSync("createImpCall", callInput);
				        	if(ServiceUtil.isSuccess(callServiceResults)) {
				        		String callLogId = callServiceResults.get("callLogId").toString();
				        		Debug.logInfo("====Call createdd callLogId is===="+callLogId, MODULE);
				        	}
			        	}
			        	
			        	if(UtilValidate.isNotEmpty(howdDidTheCallGo) && UtilValidate.isNotEmpty(reasonOfLeadDrop)) {
			        		Map<String, Object> callLogInput = UtilMisc.<String, Object>toMap("callAction",  howdDidTheCallGo, "leadId", leadId, "reasonId", reasonOfLeadDrop, "reasonNote" , reasonOfLeadDropNote , "callNote" ,callNotes);
			        		Map<String, Object>  callLogServiceResults = dispatcher.runSync("createLmsCallLog", callLogInput);
			        		if(ServiceUtil.isSuccess(callLogServiceResults)) {
			        			String callLogId = callLogServiceResults.get("callLogId").toString();
			        			Debug.logInfo("====Call Log createdd callLogId is===="+callLogId, MODULE);
			        		}
			        	}
			        	
			        	//call and call log creation [end]
					}
					
					inputNew = new HashMap<String, Object>();
					inputNew.put("userLogin", userLogin);
					inputNew.put("leadIds", StringUtil.join(leadIds, ","));
					
					Map<String, Object> leadAssocsResult = dispatcher.runSync("dataimporter.importLeadAssocs", inputNew);
					if (!ServiceUtil.isError(leadAssocsResult)) {
						Debug.log("Successfully import lead associations....");
					}
					
				}
				
			}
			
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully imported lead data.."));
    	
    	return result;
    	
    }
    
    private static void storeCallStatusChangeHistory(LocalDispatcher dispatcher, GenericValue userLogin, String partyId, String callStatus, String callSubStatus) {
    	
    	try {
			String defaultLeadMarketingCampaignId = UtilProperties.getPropertyValue("crm", "default.lead.marketingCampaignId");
			String defaultLeadContactListId = UtilProperties.getPropertyValue("crm", "default.lead.contactListId");
			
			Map<String, Object> callStatusUpdateContext = new HashMap<String, Object>();
			callStatusUpdateContext.put("partyId", partyId);
			callStatusUpdateContext.put("callStatus", callStatus);
			callStatusUpdateContext.put("callSubStatus", callSubStatus);
			callStatusUpdateContext.put("callBackDate", UtilDateTime.nowDateString("dd-MM-yyyy"));
			callStatusUpdateContext.put("marketingCampaignId", defaultLeadMarketingCampaignId);
			callStatusUpdateContext.put("contactListId", defaultLeadContactListId);
			callStatusUpdateContext.put("userLogin", userLogin);
			
			Map<String, Object> callStatusUpdateResult = dispatcher.runSync("callListStatus", callStatusUpdateContext);
			
			if (!ServiceUtil.isError(callStatusUpdateResult)) {
				Debug.logInfo("Successfully store call status history, partyId: "+partyId+", callStatus: "+callStatus, MODULE);
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
    }
    
    public static Map disableLead(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String partyId = (String) context.get("partyId");
    	String statusId = (String) context.get("statusId");
    	String disableReason = (String) context.get("disableReason");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("partyId", partyId);
    	try {
        	
			Map<String, Object> partyStatusContext = new HashMap<String, Object>();
			partyStatusContext.put("partyId", partyId);
			partyStatusContext.put("statusId", statusId);
			partyStatusContext.put("userLogin", userLogin);
			
			Map<String, Object> partyStatusResult = dispatcher.runSync("setPartyStatus", partyStatusContext);
			if (ServiceUtil.isSuccess(partyStatusResult)) {
				
				GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
				party.put("statusChangeReason", disableReason);
				party.store();
				
			}
			
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully disabled lead #"+partyId));
    	
    	return result;
    	
    }
    
    public static Map<String, Object> assignContactToLead(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		String contactPartyId = (String) context.get("contactPartyId");
		String leadPartyId = (String) context.get("leadPartyId");
		String party = (String) context.get("party");
		Map<String, Object> results = ServiceUtil.returnSuccess();
		try {
			// check if this contact is already a contact of this account
			EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, contactPartyId),
					EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, leadPartyId),
					EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
					EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "LEAD"),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
					EntityUtil.getFilterByDateExpr());
			
			TransactionUtil.begin();
			List<GenericValue> existingRelationships = delegator.findList("PartyRelationship", searchConditions,null, null, null, false);
			TransactionUtil.commit();
			if (existingRelationships.size() > 0) {
				String errMsg = null;
				if("ACCOUNT".equalsIgnoreCase(party)) {
				  errMsg = UtilProperties.getMessage(resource,"CrmErrorAcountAlreadyAssociatedToContact", locale);
				}else {
					errMsg = UtilProperties.getMessage(resource,"CrmErrorContactAlreadyAssociatedToLead", locale);
				}
	            return ServiceUtil.returnError(errMsg);
				//return UtilMessage.createAndLogServiceError("CrmErrorContactAlreadyAssociatedToAccount", locale, MODULE);
			}

			// check if userLogin has CRMSFA_ACCOUNT_UPDATE permission for this account
			/*String userLoginId = userLogin.getString("partyId");
			if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId) && !security.hasPermission(CrmConstants.SecurityPermissions.CRMSFA_ACCOUNT_UPDATE, userLogin)) {
                return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
            }*/
			// create the party relationship between the Contact and the Account
			PartyHelper.createNewPartyToRelationship(leadPartyId, contactPartyId, "CONTACT", "CONTACT_REL_INV",
					null, UtilMisc.toList("LEAD"), false, userLogin, delegator, dispatcher);

			GenericValue partyRelationship = EntityUtil.getFirst( delegator.findList("PartyRelationship", searchConditions,null, null, null, false) );
			if (UtilValidate.isNotEmpty(partyRelationship)) {
				String partyRelAssocId = delegator.getNextSeqId("PartyRelationshipAssoc");
				partyRelationship.put("partyRelAssocId", partyRelAssocId);
				partyRelationship.store();
				results.put("partyRelAssocId", partyRelAssocId);
			}
			
		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignContactToAccountFail", locale, MODULE);
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignContactToAccountFail", locale, MODULE);
		}
		return results;
	}
    
}
