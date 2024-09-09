package org.fio.crm.contacts;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.fio.crm.constants.CrmConstants;
import org.fio.crm.party.PartyHelper;
import org.fio.crm.util.LoginFilterUtil;
import org.fio.crm.util.UtilMessage;
import org.fio.homeapps.util.EnumUtil;
import org.groupfio.common.portal.util.DataUtil;
import org.ofbiz.base.util.Debug;
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


/**
 * Contacts services.
 * 
 * @author
 */
public final class ContactsServices {

	private ContactsServices() {
	}

	private static final String MODULE = ContactsServices.class.getName();
	public static final String resource = "crmUiLabels";

	public static Map<String, Object> createContact(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String tabId = (String) context.get("tabId");
		String accountPartyId = (String) context.get("accountPartyId");
		String leadPartyId = (String) context.get("leadPartyId");
		
		
		// if (!security.hasPermission("CRMSFA_CONTACT_CREATE", userLogin)) {
		// return
		// UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied",
		// locale, MODULE);
		// }

		// the net result of creating an contact is the generation of a Contact
		// partyId
		String contactPartyId = (String) context.get("partyId");
		try {
			
			GenericValue partyy = EntityQuery.use(delegator).select("partyId","roleTypeId").from("Party").where("partyId", accountPartyId).cache().queryOne();
			if (UtilValidate.isNotEmpty(partyy) && UtilValidate.isNotEmpty(partyy.getString("roleTypeId"))) { 
				if("LEAD".equals(partyy.getString("roleTypeId"))){
					leadPartyId = accountPartyId;
					accountPartyId = null;
				}
			}
			
			// make sure user has the right crm roles defined. otherwise the
			// contact will be created as deactivated.
			if (UtilValidate.isEmpty(PartyHelper.getFirstValidTeamMemberRoleTypeId(userLogin.getString("partyId"), delegator))) {
				return UtilMessage.createAndLogServiceError("CrmError_NoRoleForCreateParty", UtilMisc.toMap("userPartyName",PartyHelper.getPartyName(delegator,userLogin.getString("partyId"), false),
								"requiredRoleTypes",
								PartyHelper.TEAM_MEMBER_ROLES), locale, MODULE);
			}
			// if we're given the partyId to create, then verify it is free to
			// use
			if (contactPartyId != null) {
				Map<String, Object> findMap = UtilMisc.<String, Object> toMap(
						"partyId", contactPartyId);
				// GenericValue
				// etlGroupingGV=delegator.findByPrimaryKey("EtlGrouping",UtilMisc.toMap("groupId",
				// groupId));
				GenericValue party = EntityQuery.use(delegator).from("Party")
						.where("partyId", contactPartyId).cache().queryOne();
				// GenericValue party = delegator.findByPrimaryKey("Party",
				// findMap);
				if (party != null) {
					return UtilMessage.createAndLogServiceError(
							"person.create.person_exists", findMap, locale,
							MODULE);
				}
			}
			// create the Party and Person, which results in a partyId
			Map<String, Object> input = UtilMisc.toMap("firstName",context.get("firstName"), "lastName",context.get("lastName"));
			if (contactPartyId != null) {
				input.put("partyId", contactPartyId);
			}
			input.put("firstNameLocal", context.get("firstNameLocal"));
			input.put("lastNameLocal", context.get("lastNameLocal"));
			input.put("personalTitle", context.get("personalTitle"));
			input.put("preferredCurrencyUomId",
					context.get("preferredCurrencyUomId"));
			input.put("description", context.get("description"));
			input.put("birthDate", context.get("birthDate"));
			input.put("gender", context.get("gender"));
			Map<String, Object> serviceResults = dispatcher.runSync("createPerson", input);
			if (ServiceUtil.isError(serviceResults)) {
				return UtilMessage.createAndLogServiceError(serviceResults,
						"CrmErrorCreateContactFail", locale, MODULE);
			}
			contactPartyId = (String) serviceResults.get("partyId");
				
			
			String designation = (String) context.get("designation");
			if(UtilValidate.isNotEmpty(designation)){				
				GenericValue personData = EntityQuery.use(delegator).from("Person").where("partyId", contactPartyId).queryOne();
				if(UtilValidate.isNotEmpty(personData)){
					personData.put("designation", designation);
					personData.store();
				}
			}
			
			// create a PartyRole for the resulting Contact partyId with
			// roleTypeId = CONTACT
			serviceResults = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", contactPartyId, "roleTypeId", "CONTACT","userLogin", userLogin));
			if (ServiceUtil.isError(serviceResults)) {
				return UtilMessage.createAndLogServiceError(serviceResults,"CrmErrorCreateContactFail", locale, MODULE);
			}
			
			//added code to store the time zone and roleTypeId in party table
			String timeZoneDesc = (String) context.get("timeZoneDesc");
			GenericValue partyDetails = delegator.findOne("Party", UtilMisc.toMap("partyId", contactPartyId), false);
			if(UtilValidate.isNotEmpty(timeZoneDesc)){				
				if(UtilValidate.isNotEmpty(partyDetails)){
					partyDetails.set("timeZoneDesc", timeZoneDesc);
				}
			}
			partyDetails.set("roleTypeId", "CONTACT");
			partyDetails.store();
			// create PartySupplementalData
			GenericValue partyData = delegator.makeValue("PartySupplementalData",UtilMisc.toMap("partyId", contactPartyId));
			partyData.setNonPKFields(context);
			partyData.create();

			// create a party relationship between the userLogin and the Contact
			// with partyRelationshipTypeId RESPONSIBLE_FOR
			createResponsibleContactRelationshipForParty(userLogin.getString("partyId"), contactPartyId, userLogin,delegator, dispatcher);

			// if initial marketing campaign is provided, add it
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			if (marketingCampaignId != null) {
				serviceResults = dispatcher.runSync("createMarketingCampaignRole", UtilMisc.toMap("partyId", contactPartyId, "roleTypeId","CONTACT", "marketingCampaignId",marketingCampaignId, "userLogin", userLogin));
				if (ServiceUtil.isError(serviceResults)) {
					return UtilMessage.createAndLogServiceError(serviceResults,"CrmErrorCreateContactFail", locale, MODULE);
				}
			}

			// create basic contact info
			ModelService service = dctx
					.getModelService("crmsfa.createBasicContactInfoForParty");
			input = service.makeValid(context, "IN");
			input.put("partyId", contactPartyId);
			String primaryPhoneCountryCode = (String) context.get("primaryPhoneCountryCode");
			String primaryPhoneNumber = (String) context.get("primaryPhoneNumber");
			if(UtilValidate.isNotEmpty(primaryPhoneCountryCode)) {
				input.put("primaryPhoneNumber", primaryPhoneCountryCode+primaryPhoneNumber);
				input.put("primaryPhoneCountryCode",null);
			}
			serviceResults = dispatcher.runSync(service.name, input);
			if (ServiceUtil.isError(serviceResults)) {
				return UtilMessage.createAndLogServiceError(serviceResults,
						"CrmErrorCreateContactFail", locale, MODULE);
			}
			// create party classifications        
            String gender = (String) context.get("gender"); 
			if(UtilValidate.isNotEmpty(gender))
			{
				List<GenericValue> customFieldPartyClassificationList = delegator.findByAnd("CustomFieldPartyClassification", UtilMisc.toMap("groupId","GENDER","partyId",contactPartyId), null, false);
				if(customFieldPartyClassificationList != null && customFieldPartyClassificationList.size() > 0) {
					delegator.removeAll(customFieldPartyClassificationList);
				} else {
					GenericValue genderSeg = delegator.makeValue("CustomFieldPartyClassification", UtilMisc.toMap("groupId","GENDER","partyId",contactPartyId));
					genderSeg.set("customFieldId", gender);
					genderSeg.create();
				}
			}	
			//Debug.log("==accountPartyId=="+accountPartyId+"==tabId=="+tabId+"===contactPartyId=="+contactPartyId);
            //if (UtilValidate.isNotEmpty(tabId)) {
                if (contactPartyId != null && accountPartyId != null) {
                    serviceResults = dispatcher.runSync("crmsfa.assignContactToAccount", UtilMisc.toMap("contactPartyId", contactPartyId, "accountPartyId", accountPartyId, "userLogin", userLogin));
                    if (ServiceUtil.isSuccess(serviceResults)) {
                        String partyRelAssocId = (String) serviceResults.get("partyRelAssocId");
                        if (UtilValidate.isNotEmpty(partyRelAssocId)) {
                            List < GenericValue > partyContactMechs = EntityQuery.use(delegator).from("PartyContactMech").where("partyId", contactPartyId).filterByDate().queryList();

                            Set < String > findOptions = UtilMisc.toSet("contactMechId");
                            List < String > orderBy = UtilMisc.toList("createdStamp DESC");

                            EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId);
                            EntityCondition condition2 = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(partyContactMechs, "contactMechId", true));


                            EntityCondition primaryPhoneConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE"), EntityUtil.getFilterByDateExpr()));
                            GenericValue primaryPhone = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", primaryPhoneConditions, findOptions, null, null, true));
                            if (primaryPhone != null && primaryPhone.size() > 0) {
                                String phoneContactMechId = primaryPhone.getString("contactMechId");
                                if (UtilValidate.isNotEmpty(phoneContactMechId)) {
                                    List < GenericValue > pcmpPhone = EntityQuery.use(delegator).from("PartyContactMechPurpose").where("partyId", contactPartyId, "contactMechId", phoneContactMechId).filterByDate().queryList();
                                    if (pcmpPhone != null && pcmpPhone.size() > 0) {
                                        for (GenericValue pcmpPhoneGV: pcmpPhone) {
                                            pcmpPhoneGV.set("partyRelAssocId", partyRelAssocId);
                                        }
                                        delegator.storeAll(pcmpPhone);
                                        
                                        GenericValue partyRelAssocPhone = EntityUtil.getFirst(delegator.findByAnd("PartyRelationshipAssoc", UtilMisc.toMap("partyRelAssocId", partyRelAssocId, "assocTypeId", "PHONE", "assocId", phoneContactMechId), null, false));
                                        if (UtilValidate.isEmpty(partyRelAssocPhone)) {
                                            partyRelAssocPhone = delegator.makeValue("PartyRelationshipAssoc");
                                            partyRelAssocPhone.put("partyRelAssocId", partyRelAssocId);
                                            partyRelAssocPhone.put("assocSeqId", delegator.getNextSeqIdLong("PartyRelationshipAssoc"));
                                            partyRelAssocPhone.put("assocTypeId", "PHONE");
                                            partyRelAssocPhone.put("assocId", phoneContactMechId);
                                            partyRelAssocPhone.put("solicitationStatus", "Y");
                                            partyRelAssocPhone.create();
                                        }
                                    }
                                }
                            }

                            EntityCondition primaryEmailaddressConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL"), EntityUtil.getFilterByDateExpr()));
                            GenericValue emailAddress = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", primaryEmailaddressConditions, findOptions, null, null, true));
                            if (emailAddress != null && emailAddress.size() > 0) {
                                String emailContactMechId = emailAddress.getString("contactMechId");
                                if (UtilValidate.isNotEmpty(emailContactMechId)) {

                                    List < GenericValue > pcmpEmail = EntityQuery.use(delegator).from("PartyContactMechPurpose").where("partyId", contactPartyId, "contactMechId", emailContactMechId).filterByDate().queryList();
                                    if (pcmpEmail != null && pcmpEmail.size() > 0) {
                                        for (GenericValue pcmpEmailGV: pcmpEmail) {
                                            pcmpEmailGV.set("partyRelAssocId", partyRelAssocId);
                                        }
                                        delegator.storeAll(pcmpEmail);

                                        GenericValue partyRelAssocEmail = EntityUtil.getFirst(delegator.findByAnd("PartyRelationshipAssoc", UtilMisc.toMap("partyRelAssocId", partyRelAssocId, "assocTypeId", "EMAIL", "assocId", emailContactMechId), null, false));
                                        if (UtilValidate.isEmpty(partyRelAssocEmail)) {
                                            partyRelAssocEmail = delegator.makeValue("PartyRelationshipAssoc");
                                            partyRelAssocEmail.put("partyRelAssocId", partyRelAssocId);
                                            partyRelAssocEmail.put("assocSeqId", delegator.getNextSeqIdLong("PartyRelationshipAssoc"));
                                            partyRelAssocEmail.put("assocTypeId", "EMAIL");
                                            partyRelAssocEmail.put("assocId", emailContactMechId);
                                            partyRelAssocEmail.put("solicitationStatus", "Y");
                                            partyRelAssocEmail.create();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (contactPartyId != null && leadPartyId != null) {
                    serviceResults = dispatcher.runSync("crmsfa.assignContactToLead", UtilMisc.toMap("contactPartyId", contactPartyId, "leadPartyId", leadPartyId, "userLogin", userLogin));
                    if (ServiceUtil.isSuccess(serviceResults)) {
                        String partyRelAssocId = (String) serviceResults.get("partyRelAssocId");
                        if (UtilValidate.isNotEmpty(partyRelAssocId)) {
                            List < GenericValue > partyContactMechs = EntityQuery.use(delegator).from("PartyContactMech").where("partyId", contactPartyId).filterByDate().queryList();

                            Set < String > findOptions = UtilMisc.toSet("contactMechId");
                            List < String > orderBy = UtilMisc.toList("createdStamp DESC");

                            EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId);
                            EntityCondition condition2 = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(partyContactMechs, "contactMechId", true));


                            EntityCondition primaryPhoneConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE"), EntityUtil.getFilterByDateExpr()));
                            GenericValue primaryPhone = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", primaryPhoneConditions, findOptions, null, null, true));
                            if (primaryPhone != null && primaryPhone.size() > 0) {
                                String phoneContactMechId = primaryPhone.getString("contactMechId");
                                if (UtilValidate.isNotEmpty(phoneContactMechId)) {
                                    List < GenericValue > pcmpPhone = EntityQuery.use(delegator).from("PartyContactMechPurpose").where("partyId", contactPartyId, "contactMechId", phoneContactMechId).filterByDate().queryList();
                                    if (pcmpPhone != null && pcmpPhone.size() > 0) {
                                        for (GenericValue pcmpPhoneGV: pcmpPhone) {
                                            pcmpPhoneGV.set("partyRelAssocId", partyRelAssocId);
                                        }
                                        delegator.storeAll(pcmpPhone);
                                        
                                        GenericValue partyRelAssocPhone = EntityUtil.getFirst(delegator.findByAnd("PartyRelationshipAssoc", UtilMisc.toMap("partyRelAssocId", partyRelAssocId, "assocTypeId", "PHONE", "assocId", phoneContactMechId), null, false));
                                        if (UtilValidate.isEmpty(partyRelAssocPhone)) {
                                            partyRelAssocPhone = delegator.makeValue("PartyRelationshipAssoc");
                                            partyRelAssocPhone.put("partyRelAssocId", partyRelAssocId);
                                            partyRelAssocPhone.put("assocSeqId", delegator.getNextSeqIdLong("PartyRelationshipAssoc"));
                                            partyRelAssocPhone.put("assocTypeId", "PHONE");
                                            partyRelAssocPhone.put("assocId", phoneContactMechId);
                                            partyRelAssocPhone.put("solicitationStatus", "Y");
                                            partyRelAssocPhone.create();
                                        }
                                    }
                                }
                            }

                            EntityCondition primaryEmailaddressConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL"), EntityUtil.getFilterByDateExpr()));
                            GenericValue emailAddress = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", primaryEmailaddressConditions, findOptions, null, null, true));
                            if (emailAddress != null && emailAddress.size() > 0) {
                                String emailContactMechId = emailAddress.getString("contactMechId");
                                if (UtilValidate.isNotEmpty(emailContactMechId)) {

                                    List < GenericValue > pcmpEmail = EntityQuery.use(delegator).from("PartyContactMechPurpose").where("partyId", contactPartyId, "contactMechId", emailContactMechId).filterByDate().queryList();
                                    if (pcmpEmail != null && pcmpEmail.size() > 0) {
                                        for (GenericValue pcmpEmailGV: pcmpEmail) {
                                            pcmpEmailGV.set("partyRelAssocId", partyRelAssocId);
                                        }
                                        delegator.storeAll(pcmpEmail);

                                        GenericValue partyRelAssocEmail = EntityUtil.getFirst(delegator.findByAnd("PartyRelationshipAssoc", UtilMisc.toMap("partyRelAssocId", partyRelAssocId, "assocTypeId", "EMAIL", "assocId", emailContactMechId), null, false));
                                        if (UtilValidate.isEmpty(partyRelAssocEmail)) {
                                            partyRelAssocEmail = delegator.makeValue("PartyRelationshipAssoc");
                                            partyRelAssocEmail.put("partyRelAssocId", partyRelAssocId);
                                            partyRelAssocEmail.put("assocSeqId", delegator.getNextSeqIdLong("PartyRelationshipAssoc"));
                                            partyRelAssocEmail.put("assocTypeId", "EMAIL");
                                            partyRelAssocEmail.put("assocId", emailContactMechId);
                                            partyRelAssocEmail.put("solicitationStatus", "Y");
                                            partyRelAssocEmail.create();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            //}

		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e,
					"CrmErrorCreateContactFail", locale, MODULE);
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e,
					"CrmErrorCreateContactFail", locale, MODULE);
		}

		// return the partyId of the newly created Contact
		Map<String, Object> results = ServiceUtil.returnSuccess();
		if(UtilValidate.isNotEmpty(tabId) && "account".equals(tabId) ) {
			results = ServiceUtil.returnSuccess("Contact has created and associated with account");
			results.put("partyId", accountPartyId);
		}else if(UtilValidate.isNotEmpty(tabId) && "lead".equals(tabId)){
			results = ServiceUtil.returnSuccess("Contact has created and associated with lead");
			results.put("partyId", leadPartyId);
		}else{
			results = ServiceUtil.returnSuccess("Contact created Successfully");
			results.put("partyId", contactPartyId);
		}
		results.put("contactPartyId", contactPartyId);
		return results;
	}
	
	public static Map<String, Object> updateContact(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String contactPartyId = (String) context.get("partyId");

        // make sure userLogin has CRMSFA_CONTACT_UPDATE permission for this contact
        /*if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_CONTACT", "_UPDATE", userLogin, contactPartyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
        }*/
        try {
            // update the Party and Person
            Map<String, Object> input = UtilMisc.<String, Object>toMap("partyId", contactPartyId, "firstName", context.get("firstName"), "lastName", context.get("lastName"));
            input.put("firstNameLocal", context.get("firstNameLocal"));
            input.put("lastNameLocal", context.get("lastNameLocal"));
            input.put("personalTitle", context.get("personalTitle"));
            input.put("preferredCurrencyUomId", context.get("preferredCurrencyUomId"));
            input.put("description", context.get("description"));
            input.put("birthDate", context.get("birthDate"));
            input.put("gender", context.get("gender"));
            input.put("userLogin", userLogin);
            Map<String, Object> serviceResults = dispatcher.runSync("updatePerson", input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorUpdateContactFail", locale, MODULE);
            }
            GenericValue partyData;
            try {
            	partyData = EntityQuery.use(delegator).from("PartySupplementalData").where("partyId", contactPartyId).queryOne();
            } catch (GenericEntityException ex) {
                return ServiceUtil.returnError(ex.getMessage());
            }
            // update PartySupplementalData
            //GenericValue partyData = EntityQuery.use(delegator).from("PartySupplementalData").where("partyId", contactPartyId).cache().queryOne();
            //GenericValue partyData = delegator.findByPrimaryKey("PartySupplementalData", UtilMisc.toMap("partyId", contactPartyId));
            if (partyData == null) {
                // create a new one
                partyData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", contactPartyId));
                partyData.create();
            }
            partyData.setNonPKFields(context);
            partyData.store();
            
          //updating timezone
            if(UtilValidate.isNotEmpty(context.get("timeZoneDesc"))){
            	GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", contactPartyId), null, false) );
            	party.put("lastModifiedDate", UtilDateTime.nowTimestamp());
            	party.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
            	if(UtilValidate.isNotEmpty(context.get("timeZoneDesc"))){
            		party.put("timeZoneDesc", context.get("timeZoneDesc"));
            	}
            	party.store();
            }
			
            //updating designation

            String designation = (String) context.get("designation");
			if(UtilValidate.isNotEmpty(designation)){				
				GenericValue personData = EntityQuery.use(delegator).from("Person").where("partyId", contactPartyId).queryOne();
				if(UtilValidate.isNotEmpty(personData)){
					personData.put("designation", designation);
					personData.store();
				}
				
			}
		
			// update party classifications 
            String gender = (String) context.get("gender"); 
			if(UtilValidate.isNotEmpty(gender))
			{
				List<GenericValue> customFieldPartyClassificationList = delegator.findByAnd("CustomFieldPartyClassification", UtilMisc.toMap("groupId","GENDER","partyId",contactPartyId,"customFieldId",gender), null, false);
				if(customFieldPartyClassificationList == null || customFieldPartyClassificationList.size() == 0) {
					List<GenericValue> customFieldPartyClassificationList1 = delegator.findByAnd("CustomFieldPartyClassification", UtilMisc.toMap("groupId","GENDER","partyId",contactPartyId), null, false);
					if(customFieldPartyClassificationList1 != null && customFieldPartyClassificationList1.size() > 0) {
						delegator.removeAll(customFieldPartyClassificationList1);
					}
					// create a new segment
					GenericValue genderSeg = delegator.makeValue("CustomFieldPartyClassification", UtilMisc.toMap("groupId","GENDER","partyId",contactPartyId));
					genderSeg.set("customFieldId", gender);
					genderSeg.create();
				} 
			}
			
			
            /*String partyClassificationGroupId = (String) context.get("partyClassificationGroupId");             
			GenericValue partyClassification = null;   
			GenericValue removePartyClassification = null;  
            if(UtilValidate.isNotEmpty(partyClassificationGroupId))
			{			
				      
				Map<String, Object> partyClassificationMap = UtilMisc.<String, Object>toMap("partyId", contactPartyId);
				partyClassification = EntityUtil.getFirst(delegator.findByAnd("PartyClassification", partyClassificationMap));
				
				if(UtilValidate.isNotEmpty(partyClassification)){
				removePartyClassification=EntityUtil.getFirst(delegator.findByAnd("PartyClassification", UtilMisc.<String, Object>toMap("partyId", contactPartyId)));
				removePartyClassification.remove(); 
				partyClassificationMap = UtilMisc.<String, Object>toMap("partyId", contactPartyId,"partyClassificationGroupId", partyClassificationGroupId,"fromDate", UtilDateTime.nowTimestamp());
				partyClassification = delegator.makeValue("PartyClassification", partyClassificationMap);
				delegator.create(partyClassification);
				}
				else
				{
					partyClassificationMap = UtilMisc.<String, Object>toMap("partyId", contactPartyId,"partyClassificationGroupId", partyClassificationGroupId,"fromDate", UtilDateTime.nowTimestamp());
					partyClassification = delegator.makeValue("PartyClassification", partyClassificationMap);
					delegator.create(partyClassification); 
				}
			}
			else
			{
				removePartyClassification=EntityUtil.getFirst(delegator.findByAnd("PartyClassification", UtilMisc.<String, Object>toMap("partyId", contactPartyId)));
				if(UtilValidate.isNotEmpty(removePartyClassification))
				{
					removePartyClassification.remove();
				}
			}
			// end update party classifications
		 */

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorUpdateContactFail", locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorUpdateContactFail", locale, MODULE);
        }
        return ServiceUtil.returnSuccess();
    }

	/**
	 * Contacts services - Eca-service triggered by crmsfa.createContact to store account-contact relation
	 * 
	 * @author
	 */
	public static Map<String, Object> assignContactToAccount(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		String contactPartyId = (String) context.get("contactPartyId");
		String accountPartyId = (String) context.get("accountPartyId");
		context.put("partyId",accountPartyId);
		String party = (String) context.get("party");
		Map<String, Object> results = ServiceUtil.returnSuccess();
		try {
			// check if this contact is already a contact of this account
			EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, contactPartyId),
					EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, accountPartyId),
					EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
					EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
					EntityUtil.getFilterByDateExpr());
			
			//TransactionUtil.begin();
			List<GenericValue> existingRelationships = delegator.findList("PartyRelationship", searchConditions,null, null, null, false);
			//TransactionUtil.commit();
			
			if (existingRelationships.size() > 0) {
				String errMsg = null;
				if("ACCOUNT".equalsIgnoreCase(party)) {
				  errMsg = UtilProperties.getMessage(resource,"CrmErrorAcountAlreadyAssociatedToContact", locale);
				}else {
					errMsg = UtilProperties.getMessage(resource,"CrmErrorContactAlreadyAssociatedToAccount", locale);
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
			boolean newCreated = PartyHelper.createNewPartyToRelationship(accountPartyId, contactPartyId, "CONTACT", "CONTACT_REL_INV",
					null, UtilMisc.toList("ACCOUNT"), false, userLogin, delegator, dispatcher);
			try{
				//adding 1s delay so that created PartyRelationship updated in the db FS20-570 (20/01/2020)
				Thread.sleep(1000);
			}catch(InterruptedException e){
				Debug.logError("----------InterruptedException--------"+e,MODULE);
			}
			Debug.logInfo("----------newAssocCreated--------"+newCreated,MODULE);
			Debug.logInfo("----------accountPartyId--------"+accountPartyId,MODULE);
			Debug.logInfo("----------contactPartyId--------"+contactPartyId,MODULE);
			EntityCondition searchConditions1 = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, contactPartyId),
					EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, accountPartyId),
					EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()),
	                EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp())),
					EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
					EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"));
			GenericValue partyRelationship = EntityUtil.getFirst( delegator.findList("PartyRelationship", searchConditions1,null, null, null, false) );
			Debug.logInfo("----------partyRelationship--------"+partyRelationship,MODULE);
			if (UtilValidate.isNotEmpty(partyRelationship)) {
				String partyRelAssocId = delegator.getNextSeqId("PartyRelationshipAssoc");
				partyRelationship.put("partyRelAssocId", partyRelAssocId);
				partyRelationship.store();
				results.put("partyRelAssocId", partyRelAssocId);
				Debug.logInfo("----------partyRelAssocId--------"+partyRelAssocId,MODULE);
				
				// Add relation in PartyRelationshipAssoc 
				DataUtil.relatedPartyContactAssociation(delegator, accountPartyId, contactPartyId, "EMAIL", "PRIMARY_EMAIL", partyRelAssocId);
				DataUtil.relatedPartyContactAssociation(delegator, accountPartyId, contactPartyId, "PHONE", "PRIMARY_PHONE", partyRelAssocId);
				
				GenericValue person = EntityUtil.getFirst( delegator.findByAnd("Person", UtilMisc.toMap("partyId", contactPartyId), null, false) );
				if (UtilValidate.isNotEmpty(person) && UtilValidate.isNotEmpty(person.getString("designation"))) {
					String contactMechId = person.getString("designation");
					GenericValue designationAssoc = EntityUtil.getFirst(delegator.findByAnd("ContactDesignationAssoc", UtilMisc.toMap("partyId", accountPartyId, "contactId", contactPartyId,"designationEnumId", contactMechId,"partyRelAssocId", partyRelAssocId), null, false));
					
					if (UtilValidate.isEmpty(designationAssoc)) {
	                	designationAssoc = delegator.makeValue("ContactDesignationAssoc", UtilMisc.toMap("partyId", accountPartyId, "contactId", contactPartyId, "designationEnumId", contactMechId, "partyRelAssocId", partyRelAssocId));
	                	String contactDesignationAssocId = delegator.getNextSeqId("ContactDesignationAssoc");
	                	
	                	designationAssoc.put("contactDesignationAssocId", contactDesignationAssocId);
	                	
	                	//designationAssoc.put("sequenceNumber", new Long(seqId));
	                	designationAssoc.put("designationName", EnumUtil.getEnumDescription(delegator, contactMechId, "DESIGNATION"));
	                	
	                	designationAssoc.create();
					}
				}

			}
			
		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignContactToAccountFail", locale, MODULE);
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignContactToAccountFail", locale, MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> assignContactToLead(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		String contactPartyId = (String) context.get("contactPartyId");
		String leadPartyId = (String) context.get("leadPartyId");
		context.put("partyId",leadPartyId);
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
			
			//TransactionUtil.begin();
			List<GenericValue> existingRelationships = delegator.findList("PartyRelationship", searchConditions,null, null, null, false);
			
			if (existingRelationships.size() > 0) {
				String errMsg = null;
				if("LEAD".equalsIgnoreCase(party)) {
				  errMsg = UtilProperties.getMessage(resource,"CrmErrorLeadAlreadyAssociatedToContact", locale);
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
			// create the party relationship between the Contact and the Lead
			boolean newCreated = PartyHelper.createNewPartyToRelationship(leadPartyId, contactPartyId, "CONTACT", "CONTACT_REL_INV",
					null, UtilMisc.toList("LEAD"), false, userLogin, delegator, dispatcher);
			//TransactionUtil.commit();
			try{
				//adding 1s delay so that created PartyRelationship updated in the db FS20-570 (20/01/2020)
				Thread.sleep(1000);
			}catch(InterruptedException e){
				Debug.logError("----------InterruptedException--------"+e,MODULE);
			}
			Debug.logInfo("----------newAssocCreated--------"+newCreated,MODULE);
			Debug.logInfo("----------leadPartyId--------"+leadPartyId,MODULE);
			Debug.logInfo("----------contactPartyId--------"+contactPartyId,MODULE);
			EntityCondition searchConditions1 = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, contactPartyId),
					EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, leadPartyId),
					EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()),
	                EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp())),
					EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
					EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "LEAD"),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"));
			GenericValue partyRelationship = EntityUtil.getFirst( delegator.findList("PartyRelationship", searchConditions1,null, null, null, false) );
			Debug.logInfo("----------partyRelationship--------"+partyRelationship,MODULE);
			if (UtilValidate.isNotEmpty(partyRelationship)) {
				String partyRelAssocId = delegator.getNextSeqId("PartyRelationshipAssoc");
				partyRelationship.put("partyRelAssocId", partyRelAssocId);
				partyRelationship.store();
				results.put("partyRelAssocId", partyRelAssocId);
				Debug.logInfo("----------partyRelAssocId--------"+partyRelAssocId,MODULE);
				
				// Add relation in PartyRelationshipAssoc 
				DataUtil.relatedPartyContactAssociation(delegator, leadPartyId, contactPartyId, "EMAIL", "PRIMARY_EMAIL", partyRelAssocId);
				DataUtil.relatedPartyContactAssociation(delegator, leadPartyId, contactPartyId, "PHONE", "PRIMARY_PHONE", partyRelAssocId);
				
				GenericValue person = EntityUtil.getFirst( delegator.findByAnd("Person", UtilMisc.toMap("partyId", contactPartyId), null, false) );
				if (UtilValidate.isNotEmpty(person) && UtilValidate.isNotEmpty(person.getString("designation"))) {
					String contactMechId = person.getString("designation");
					GenericValue designationAssoc = EntityUtil.getFirst(delegator.findByAnd("ContactDesignationAssoc", UtilMisc.toMap("partyId", leadPartyId, "contactId", contactPartyId,"designationEnumId", contactMechId,"partyRelAssocId", partyRelAssocId), null, false));
					
					if (UtilValidate.isEmpty(designationAssoc)) {
	                	designationAssoc = delegator.makeValue("ContactDesignationAssoc", UtilMisc.toMap("partyId", leadPartyId, "contactId", contactPartyId, "designationEnumId", contactMechId, "partyRelAssocId", partyRelAssocId));
	                	String contactDesignationAssocId = delegator.getNextSeqId("ContactDesignationAssoc");
	                	
	                	designationAssoc.put("contactDesignationAssocId", contactDesignationAssocId);
	                	
	                	//designationAssoc.put("sequenceNumber", new Long(seqId));
	                	designationAssoc.put("designationName", EnumUtil.getEnumDescription(delegator, contactMechId, "DESIGNATION"));
	                	
	                	designationAssoc.create();
					}
				}
			}
			
		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignContactToLeadFail", locale, MODULE);
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignContactToLeadFail", locale, MODULE);
		}
		return results;
	}


	/**************************************************************************/
	/** Helper Methods ***/
	/**************************************************************************/

	/**
	 * Creates an contact relationship of a given type for the given party and
	 * removes all previous relationships of that type. This method helps avoid
	 * semantic mistakes and typos from the repeated use of this code pattern.
	 */
	public static boolean createResponsibleContactRelationshipForParty(
			String partyId, String contactPartyId, GenericValue userLogin,
			Delegator delegator, LocalDispatcher dispatcher)
					throws GenericServiceException, GenericEntityException {
		return PartyHelper.createNewPartyToRelationship(partyId,
				contactPartyId, "CONTACT", "RESPONSIBLE_FOR", "CONTACT_OWNER",
				PartyHelper.TEAM_MEMBER_ROLES, true, userLogin, delegator,
				dispatcher);
	}
	
	public static Map<String, Object> removeContactFromAccount(DispatchContext dctx, Map<String, Object> context) {
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Security security = dctx.getSecurity();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Locale locale = (Locale)context.get("locale");

	        String contactPartyId = (String) context.get("contactPartyId");
	        String accountPartyId = (String) context.get("accountPartyId");

	        // ensure update permission on account
	        if (!security.hasPermission(CrmConstants.SecurityPermissions.CRMSFA_ACCOUNT_UPDATE, userLogin)) {
	            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
	        }
	        try {
	            // find and expire all contact relationships between the contact and account
	            List<GenericValue> relations = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", accountPartyId,
	                        "partyIdFrom", contactPartyId, "partyRelationshipTypeId", CrmConstants.PartyRelationshipTypeConstants.CONTACT_REL_INV),null,false);
	            PartyHelper.expirePartyRelationships(relations, UtilDateTime.nowTimestamp(), dispatcher, userLogin);
	        } catch (GenericServiceException e) {
	            return UtilMessage.createAndLogServiceError(e, "CrmErrorRemoveContactFail", locale, MODULE);
	        } catch (GenericEntityException e) {
	            return UtilMessage.createAndLogServiceError(e, "CrmErrorRemoveContactFail", locale, MODULE);
	        }
	        return ServiceUtil.returnSuccess();
	    }
}
