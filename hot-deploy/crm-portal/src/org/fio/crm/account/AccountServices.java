package org.fio.crm.account;
import org.ofbiz.security.Security;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.fio.crm.constants.CrmConstants;
import org.fio.crm.party.PartyHelper;
import org.fio.crm.util.LoginFilterUtil;
import org.fio.crm.util.UtilMessage;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.util.DataUtil;
import org.fio.crm.leads.LeadsServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import java.util.stream.Collectors;

public class AccountServices {
	private AccountServices(){ }

	private static final String MODULE = AccountServices.class.getName();
	public static final String resource = "crmUiLabels";
	// create account
	public static Map<String, Object> createAccount(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String userLoginId = userLogin.getString("userLoginId");
		String accountName = (String) context.get("accountName");
		String accountOrLead = (String) context.get("accountOrLead");
		// the field that flag if force complete to create contact even existing same name already
		String forceComplete = context.get("forceComplete") == null ? "N" : (String) context.get("forceComplete");
		if (!security.hasPermission(CrmConstants.SecurityPermissions.CRMSFA_ACCOUNT_CREATE, userLogin)) {
			return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
		}
		// the net result of creating an account is the generation of an Account partyId
		String accountPartyId = (String) context.get("partyId");
		String statusId = (String) context.get("statusId");
		String currencyUomId = (String) context.get("currencyUomId");
		String resoponsibleUserLoginId =null;
		String responsibleParty = (String)context.get("personResponsible");
		if(UtilValidate.isNotEmpty(responsibleParty)){
			resoponsibleUserLoginId = DataUtil.getPartyUserLoginId(delegator, responsibleParty);
		}
		String ownerId = UtilValidate.isNotEmpty(resoponsibleUserLoginId) ? resoponsibleUserLoginId : UtilValidate.isNotEmpty(context.get("owenerId")) ? (String) context.get("ownerId") : userLoginId;
		try {
			String entityName = "Account";
			if(UtilValidate.isNotEmpty(context.get("securityMtxEntity"))) {
				entityName = (String) context.get("securityMtxEntity");
			}
			String accessLevel = "Y";
			String nativeBusinessUnit = null;
			String nativeTeamId = null;

			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
				Map<String, Object> buTeamData = DataUtil.getUserBuTeam(delegator, userLoginPartyId);
				nativeBusinessUnit = (String) buTeamData.get("businessUnit");
				nativeTeamId = (String) buTeamData.get("emplTeamId");
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", nativeBusinessUnit);
				accessMatrixMap.put("modeOfOp", "Create");
				accessMatrixMap.put("entityName", entityName);
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
				//validate the common team and access for the assignment
				String currentPartyId = userLoginPartyId;
				if(UtilValidate.isEmpty(ownerId)) {
					ownerId = nativeTeamId;
				} else {
					currentPartyId = DataUtil.getUserLoginPartyId(delegator, ownerId);
					buTeamData = DataUtil.getUserBuTeam(delegator, currentPartyId);
					nativeBusinessUnit = (String) buTeamData.get("businessUnit");
					nativeTeamId = (String) buTeamData.get("emplTeamId");
				}
				//check both users are the same team or Bu or not (need clarification) 
				//check with ownerId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					if(!ownerIds.contains(nativeTeamId)) accessLevel = null;
				}

				//check with emplTeamId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					if(!emplTeamIds.contains(nativeTeamId)) accessLevel = null;
				}
			}
			
			//accessLevel = "Y";
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				// make sure user has the right crmsfa roles defined.  otherwise the account will be created as deactivated.
				if (UtilValidate.isEmpty(PartyHelper.getFirstValidTeamMemberRoleTypeId(userLogin.getString("partyId"), delegator))) {
					return UtilMessage.createAndLogServiceError("CrmError_NoRoleForCreateParty", UtilMisc.toMap("userPartyName", PartyHelper.getPartyName(delegator, userLogin.getString("partyId"), false), "requiredRoleTypes", PartyHelper.TEAM_MEMBER_ROLES), locale, MODULE);
				}
	            // Check Valid account or not
	            if (UtilValidate.isNotEmpty(context.get("parentPartyId"))) {
	                Boolean accountValidation = LeadsServices.accountValidation(delegator, (String) context.get("parentPartyId"));
	                if (!accountValidation) {
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "invalidAccount", locale));
	                }
	            }
				// if we're given the partyId to create, then verify it is free to use
				if (accountPartyId != null) {
					Map<String, Object> findMap =  UtilMisc.<String, Object>toMap("partyId", accountPartyId);
					GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", accountPartyId),false);
					if (party != null) {
						// TODO maybe a more specific message such as "Account already exists"
						return UtilMessage.createAndLogServiceError("person.create.person_exists", findMap, locale, MODULE);
					}
				}

				// verify account name is use already
				if (!"Y".equals(forceComplete)) {
					Set<GenericValue> duplicateAccountsWithName;
					try {
						duplicateAccountsWithName = PartyHelper.getPartyGroupByGroupNameAndRoleType(delegator,accountName, "ACCOUNT");
						// if existing the account which have same account name, then return the conflict account and error message
						if (duplicateAccountsWithName.size() > 0 && !"Y".equals(forceComplete)) {
							GenericValue partyGroup = duplicateAccountsWithName.iterator().next();
							Map results = ServiceUtil.returnError(UtilMessage.expandLabel("CrmCreateAccountDuplicateCheckFail", UtilMisc.toMap("partyId", partyGroup.getString("partyId")), locale));
							results.put("duplicateAccountsWithName", duplicateAccountsWithName);
							return results;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
			    		Debug.logError(e.getMessage(), MODULE);

					}
				}

				// create the Party and PartyGroup, which results in a partyId
				Map<String, Object> input = UtilMisc.toMap("groupName", context.get("accountName"), "groupNameLocal", context.get("groupNameLocal"),
						"officeSiteName", context.get("officeSiteName"), "description", context.get("description"), "partyId", accountPartyId,"gstnNo",context.get("gstnNo"),"statusId",statusId);
				input.put("preferredCurrencyUomId", currencyUomId);
				Map<String, Object> serviceResults = dispatcher.runSync("createPartyGroup", input);
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
				accountPartyId = (String) serviceResults.get("partyId");
				
				String roleTypeId = "ACCOUNT";
				if(UtilValidate.isNotEmpty(accountOrLead) && "Y".equals(accountOrLead)) {
					roleTypeId = "LEAD";
				}

				// create a PartyRole for the resulting Account partyId with roleTypeId = ACCOUNT
				if(UtilValidate.isNotEmpty(accountOrLead) && "Y".equals(accountOrLead))
				{
					serviceResults = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", accountPartyId, "roleTypeId", "LEAD", "userLogin", userLogin));
				}else{
					serviceResults = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", accountPartyId, "roleTypeId", "ACCOUNT", "userLogin", userLogin));
				}
				
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}

				String timeZoneDesc = (String) context.get("timeZoneDesc");	
				Debug.log("timeZoneDesc==********=="+timeZoneDesc);
				GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", accountPartyId), null, false) );
				if(UtilValidate.isNotEmpty(party)){
					party.put("roleTypeId", roleTypeId);
					party.put("createdDate", UtilDateTime.nowTimestamp());
					party.put("createdByUserLogin", userLogin.getString("userLoginId"));
					party.put("timeZoneDesc", timeZoneDesc);
					party.store();
				}
				
				//update owner roletype/ownerbu/empteam in  party table
				dispatcher.runSync("ap.updateSecurityPartyInfo", UtilMisc.toMap("userLoginId", userLoginId, "ownerId",ownerId, "roleTypeId", roleTypeId, "partyId", accountPartyId ));
				

				// create PartySupplementalData
				GenericValue partyData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", accountPartyId));
				partyData.setNonPKFields(context);
				partyData.create();

				// create a unique party relationship between the userLogin and the Account with partyRelationshipTypeId RESPONSIBLE_FOR
				if(UtilValidate.isNotEmpty(accountOrLead) && "Y".equals(accountOrLead))
				{
					if (UtilValidate.isNotEmpty(context.get("personResponsible"))) {
						PartyHelper.createNewPartyToRelationship((String) context.get("personResponsible"), accountPartyId,
								"LEAD", "RESPONSIBLE_FOR", "ACCOUNT_OWNER", UtilMisc.toList("ACCOUNT_MANAGER"), true,
								userLogin, delegator, dispatcher);
					} else {
						createResponsibleAccountLeadRelationshipForParty(userLogin.getString("partyId"), accountPartyId,
								userLogin, delegator, dispatcher);
					}
				} else {			
					if (UtilValidate.isNotEmpty(context.get("personResponsible"))) {
						PartyHelper.createNewPartyToRelationship((String) context.get("personResponsible"), accountPartyId,
								"ACCOUNT", "RESPONSIBLE_FOR", "ACCOUNT_OWNER", UtilMisc.toList("ACCOUNT_MANAGER"), true,
								userLogin, delegator, dispatcher);
					} else{
						createResponsibleAccountRelationshipForParty(userLogin.getString("partyId"), accountPartyId, userLogin, delegator, dispatcher);
					}
				}
				
				// if initial data source is provided, add it
				String dataSourceId = (String) context.get("dataSourceId");
				if (dataSourceId != null) {
					serviceResults = dispatcher.runSync("crmsfa.addAccountDataSource",
							UtilMisc.toMap("partyId", accountPartyId, "dataSourceId", dataSourceId, "userLogin", userLogin));
					if (ServiceUtil.isError(serviceResults)) {
						return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorCreateAccountFail", locale, MODULE);
					}
				}

				// if initial marketing campaign is provided, add it
				String marketingCampaignId = (String) context.get("marketingCampaignId");
				if (marketingCampaignId != null) {
					serviceResults = dispatcher.runSync("crmsfa.addMarketingCampaign",
							UtilMisc.toMap("partyId", accountPartyId, "roleTypeId", roleTypeId, "marketingCampaignId", marketingCampaignId, "userLogin", userLogin));
					if (ServiceUtil.isError(serviceResults)) {
						return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorCreateAccountFail", locale, MODULE);
					}
				}

				// if there's an initialTeamPartyId, assign the team to the account
				String initialTeamPartyId = (String) context.get("initialTeamPartyId");
				if (initialTeamPartyId != null) {
					serviceResults = dispatcher.runSync("crmsfa.assignTeamToAccount", UtilMisc.toMap("accountPartyId", accountPartyId,
							"teamPartyId", initialTeamPartyId, "userLogin", userLogin));
					if (ServiceUtil.isError(serviceResults)) {
						return serviceResults;
					}
				}
				
				// create primary contact [start]
				
				Map<String, Object> callCtxt = FastMap.newInstance();
				Map<String, Object> callResult = FastMap.newInstance();
				
				if (UtilValidate.isNotEmpty(context.get("firstName")) && UtilValidate.isNotEmpty(context.get("lastName"))) {
					callCtxt = FastMap.newInstance();
					callCtxt.put("firstName", context.get("firstName"));
					callCtxt.put("lastName", context.get("lastName"));
					callCtxt.put("primaryEmail", context.get("primaryEmail"));
					callCtxt.put("primaryPhoneNumber", context.get("primaryPhoneNumber"));
					callCtxt.put("primaryWebUrl", context.get("primaryWebUrl"));
					callCtxt.put("primaryPhoneAskForName", context.get("primaryPhoneAskForName"));
					callCtxt.put("accountPartyId", accountPartyId);
					callCtxt.put("userLogin", userLogin);
					
					callResult = dispatcher.runSync("crmsfa.createContact", callCtxt);
					if (ServiceUtil.isSuccess(callResult)) {
						String contactPartyId = (String) callResult.get("contactPartyId");
						Debug.logInfo("contact created for#" + roleTypeId+", contactId#" + contactPartyId, MODULE);
						/*if (contactPartyId != null) {
							dispatcher.runSync("crmsfa.assignContactToAccount", UtilMisc.toMap("contactPartyId", contactPartyId, "accountPartyId", accountPartyId, "userLogin", userLogin));
						}*/
					}
				}
				
				// create primary contact [end]
				
				// create postal address [start]
				
				callCtxt = FastMap.newInstance();
				callCtxt.put("toName", context.get("generalToName"));
				callCtxt.put("attnName", context.get("generalAttnName"));
				callCtxt.put("address1", context.get("generalAddress1"));
				callCtxt.put("address2", context.get("generalAddress2"));
				callCtxt.put("city", context.get("generalCity"));
				callCtxt.put("stateProvinceGeoId", context.get("generalStateProvinceGeoId"));
				callCtxt.put("postalCode", context.get("generalPostalCode"));
				callCtxt.put("postalCodeExt", context.get("generalPostalCodeExt"));
				callCtxt.put("countryGeoId", context.get("generalCountryGeoId"));
				callCtxt.put("county", context.get("countyGeoId"));
				callCtxt.put("isBusiness", context.get("isBusiness"));
				callCtxt.put("isVacant", context.get("isVacant"));
				callCtxt.put("isUspsAddrVerified", context.get("isUspsAddrVerified"));
				callCtxt.put("brandCode", context.get("brandCode"));
				callCtxt.put("partyId", accountPartyId);
				callCtxt.put("userLogin", userLogin);
				
				callResult = dispatcher.runSync("crmsfa.createPartyPostalAddress", callCtxt);
				if (ServiceUtil.isSuccess(callResult)) {
					Debug.logInfo("Postal address created for#" + roleTypeId+", partyId#" + accountPartyId, MODULE);
				}
				
				// create postal address [end]

				// create contact information for main entity
				if (UtilValidate.isNotEmpty(context.get("basePrimaryEmail")) || UtilValidate.isNotEmpty(context.get("basePrimaryPhoneNumber"))) {
					callCtxt = FastMap.newInstance();
					callCtxt.put("partyId", accountPartyId);
					callCtxt.put("primaryEmail", context.get("basePrimaryEmail"));
					callCtxt.put("primaryPhoneNumber", context.get("basePrimaryPhoneNumber"));
					callCtxt.put("userLogin", userLogin);
					
					callResult = dispatcher.runSync("crmsfa.createBasicContactInfoForParty", callCtxt);
					if (ServiceUtil.isSuccess(callResult)) {
						Debug.logInfo("Contact info [email, phone] created for#" + roleTypeId+", partyId#" + accountPartyId, MODULE);
					}
				}
				
				
				/*ModelService service = dctx.getModelService("crmsfa.createBasicContactInfoForParty");
				input = service.makeValid(context, "IN");
				input.put("partyId", accountPartyId);
				serviceResults = dispatcher.runSync(service.name, input);*/
				
				String partyClassificationGroupId = (String) context.get("partyClassificationGroupId"); 
				if(UtilValidate.isNotEmpty(partyClassificationGroupId)) {
					createPartyClassification(delegator, accountPartyId, partyClassificationGroupId);
				}
				
				String isExempt = (String) context.get("isExempt");
				String partyTaxId = (String) context.get("partyTaxId");
				//if(UtilValidate.isNotEmpty(isExempt) && UtilValidate.isNotEmpty(partyTaxId)) {
					Map inputMap = UtilMisc.toMap("partyTaxId",partyTaxId,"isExempt",isExempt,"partyId",accountPartyId,"userLogin",userLogin);
					dispatcher.runSync("createPartyTaxExcemption",inputMap);
				//}

				if(UtilValidate.isNotEmpty(accountOrLead) && "Y".equals(accountOrLead)) {
					createPartyClassification(delegator,accountPartyId,"ACCT_LEAD_CLASS");
				}
			} else {
				String errMsg = "";
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errMsg = accessMatrixRes.get("errorMessage").toString();
				} else {
					errMsg = "Access Denied";
				}
				return UtilMessage.createAndLogServiceError(errMsg, locale, MODULE);
			}
		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorCreateAccountFail", locale, MODULE);
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorCreateAccountFail", locale, MODULE);
		}
		
		// return the partyId of the newly created Account
		Map<String, Object> results = ServiceUtil.returnSuccess("Successfully Created");
		results.put("partyId", accountPartyId);
		return results;
	}
	
	public static boolean createResponsibleAccountLeadRelationshipForParty(String partyId, String accountPartyId,
			GenericValue userLogin, Delegator delegator, LocalDispatcher dispatcher)
					throws GenericServiceException, GenericEntityException {
		return PartyHelper.createNewPartyToRelationship(partyId, accountPartyId, "LEAD", "RESPONSIBLE_FOR",
				"ACCOUNT_OWNER", PartyHelper.TEAM_MEMBER_ROLES, true, userLogin, delegator, dispatcher);
	}
	public static boolean createResponsibleAccountRelationshipForParty(String partyId, String accountPartyId,
			GenericValue userLogin, Delegator delegator, LocalDispatcher dispatcher)
					throws GenericServiceException, GenericEntityException {
		return PartyHelper.createNewPartyToRelationship(partyId, accountPartyId, "ACCOUNT", "RESPONSIBLE_FOR",
				"ACCOUNT_OWNER", PartyHelper.TEAM_MEMBER_ROLES, true, userLogin, delegator, dispatcher);
	}
	public static boolean createPartyClassification(Delegator delegator,String partyId,String partyClassifcationGroupId)
	{
		try{
			List<GenericValue> partyClassifications = EntityQuery.use(delegator).from("CustomFieldPartyClassification").where("partyId", partyId,"customFieldId",partyClassifcationGroupId,"groupId","LEAD_CLASSIFICATION").queryList();
			if(UtilValidate.isEmpty(partyClassifications)) {
				GenericValue partyClassification = delegator.makeValue("CustomFieldPartyClassification");
				partyClassification.put("partyId", partyId);
				partyClassification.put("customFieldId", partyClassifcationGroupId);
				partyClassification.put("groupId", "LEAD_CLASSIFICATION");
				partyClassification.create();
			}else {
				return false;
			}
		}catch(Exception e)
		{
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return false;
		}
		return true;
	}

	public static Map<String, Object> updateAccount(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		String accountPartyId = (String) context.get("partyId");
		String currencyUomId = (String) context.get("currencyUomId");
		String userLoginId = userLogin.getString("userLoginId");

		// make sure userLogin has CRMSFA_ACCOUNT_UPDATE permission for this account
		String partyId = userLogin.getString("partyId");
		if(LoginFilterUtil.checkEmployeePosition(delegator, partyId) && !security.hasPermission(CrmConstants.SecurityPermissions.CRMSFA_ACCOUNT_UPDATE, userLogin)) {
			return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
		}
		try {
			String entityName = "Account";
			if(UtilValidate.isNotEmpty(context.get("securityMtxEntity"))) {
				entityName = (String) context.get("securityMtxEntity");
			}
			
			String accessLevel = "Y";
			String businessUnit = null;
			String teamId = "";

			//to associate and update relational manager or person responsible
			String resoponsibleUserLoginId =null;
			String responsibleParty = (String)context.get("personResponsible");
			if(UtilValidate.isNotEmpty(responsibleParty)){
				resoponsibleUserLoginId = DataUtil.getPartyUserLoginId(delegator, responsibleParty);
				GenericValue currentPersonResponsible = EntityUtil.getFirst(delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", accountPartyId, "partyIdTo", responsibleParty,"roleTypeIdFrom","ACCOUNT"), UtilMisc.toList("fromDate DESC"), false));
				List<GenericValue> associatedParties = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", accountPartyId,"roleTypeIdFrom","ACCOUNT"), null, false);
				if(UtilValidate.isEmpty(associatedParties)) {
				PartyHelper.expirePartyRelationships(associatedParties,UtilDateTime.nowTimestamp(), dispatcher, userLogin);
				}
				if(UtilValidate.isEmpty(currentPersonResponsible)) {
					createResponsibleAccountRelationshipForParty(responsibleParty, accountPartyId, userLogin, delegator, dispatcher);
				}else {
					currentPersonResponsible.setNonPKFields(UtilMisc.toMap("thruDate", null));
					delegator.store(currentPersonResponsible);
				}
			}
			String ownerId = UtilValidate.isNotEmpty(resoponsibleUserLoginId) ? resoponsibleUserLoginId : UtilValidate.isNotEmpty(context.get("owenerId")) ? (String) context.get("ownerId") : userLoginId;

			Map<String, Object> entityData = org.fio.homeapps.util.DataUtil.getExistEntityDetails(delegator,"Party", UtilMisc.toMap("partyId",accountPartyId));
			if(UtilValidate.isNotEmpty(entityData)) {
				businessUnit = UtilValidate.isNotEmpty(entityData.get("businessUnit")) ? (String) entityData.get("businessUnit") : "";
				teamId = UtilValidate.isNotEmpty(entityData.get("teamId")) ? (String) entityData.get("teamId") : "";
			}
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				if(UtilValidate.isEmpty(businessUnit))
					businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("teamId", teamId);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "Update");
				accessMatrixMap.put("entityName", entityName);
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
				//validate the common team and access for the assignment
				String currentPartyId = userLoginPartyId;
				if(UtilValidate.isEmpty(ownerId)) {
					ownerId = teamId;
				} else {
					currentPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, ownerId);
					Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, currentPartyId);
					businessUnit = (String) buTeamData.get("businessUnit");
					teamId = (String) buTeamData.get("emplTeamId");
				}
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				//change the access in the create 
				//check with ownerId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					if(!ownerIds.contains(teamId)) accessLevel = null;
					conditionList.add(EntityCondition.makeCondition("ownerId", EntityOperator.IN, ownerIds));
					//custRequestContext.put("ownerIds", ownerIds);
				}

				//check with emplTeamId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					if(!emplTeamIds.contains(teamId)) accessLevel = null;
					conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
					//custRequestContext.put("emplTeamIds", emplTeamIds);
				}

				if (UtilValidate.isNotEmpty(accountPartyId)) {
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, accountPartyId));
				}

				EntityCondition mainCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				GenericValue party = EntityUtil
						.getFirst(delegator.findList("Party", mainCondition, null, null, null, false));

				if(UtilValidate.isEmpty(party)) accessLevel=null;

			}
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				// Check Valid account or not
	            if (UtilValidate.isNotEmpty(context.get("parentPartyId"))) {
	                Boolean accountValidation = LeadsServices.accountValidation(delegator, (String) context.get("parentPartyId"));
	                if (!accountValidation) {
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "invalidAccount", locale));
	                }
	            }
				// update the Party and PartyGroup
				Map<String, Object> input = UtilMisc.toMap("groupName", context.get("accountName"), "groupNameLocal", context.get("groupNameLocal"),
						"officeSiteName", context.get("officeSiteName"), "description", context.get("description"));
				input.put("partyId", accountPartyId);
				input.put("preferredCurrencyUomId", currencyUomId);
				input.put("userLogin", userLogin);
				input.put("gstnNo",context.get("gstnNo"));
				Map<String, Object> serviceResults = dispatcher.runSync("updatePartyGroup", input);
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}

				// update PartySupplementalData
				GenericValue partyData = delegator.findOne("PartySupplementalData", UtilMisc.toMap("partyId", accountPartyId),false);
				if (partyData == null) {
					// create a new one
					partyData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", accountPartyId));
					partyData.create();
				}
				partyData.setNonPKFields(context);
				partyData.store();
				
				GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", accountPartyId), null, false) );
				party.put("lastModifiedDate", UtilDateTime.nowTimestamp());
				party.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
				if(UtilValidate.isNotEmpty(context.get("timeZoneDesc"))){
					party.put("timeZoneDesc", context.get("timeZoneDesc"));
				}
				party.store();
				
				//update owner roletype/ownerbu/empteam in  party table
				dispatcher.runSync("ap.updateSecurityPartyInfo", UtilMisc.toMap("userLoginId", userLoginId, "ownerId",ownerId, "partyId", accountPartyId ));
				
				
				// if there's an initialTeamPartyId, assign the team to the account
				String initialTeamPartyId = (String) context.get("initialTeamPartyId");
				if (initialTeamPartyId != null) {
					serviceResults = dispatcher.runSync("crmsfa.assignTeamToAccount", UtilMisc.toMap("accountPartyId", accountPartyId,
							"teamPartyId", initialTeamPartyId, "userLogin", userLogin));
					if (ServiceUtil.isError(serviceResults)) {
						return serviceResults;
					}
				}
				
				// if initial data source is provided, add it
				String dataSourceId = (String) context.get("dataSourceId");
				if (dataSourceId != null) {
					TransactionUtil.begin();
					delegator.removeByCondition("PartyDataSource", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, accountPartyId));
					TransactionUtil.commit();
					
					GenericValue partyDataSource = delegator.makeValue("PartyDataSource", UtilMisc.toMap("partyId", accountPartyId, "dataSourceId", dataSourceId, "fromDate", UtilDateTime.nowTimestamp()));
					partyDataSource.create();
				
				}
				
				String partyClassificationGroupId = (String) context.get("partyClassificationGroupId"); 

				if(UtilValidate.isNotEmpty(partyClassificationGroupId)) {
					
					List conditionsList = FastList.newInstance();
					
					conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, accountPartyId));				
					
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
					
					
					List<GenericValue> customFieldClassification = delegator.findList("CustomFieldPartyClassification", mainConditons, null, null, null, false);
					if (UtilValidate.isNotEmpty(customFieldClassification)) {
						for (GenericValue customFieldClass : customFieldClassification) {
							customFieldClass.remove();
						}
					}
					
					GenericValue partyClassificationData = delegator.findOne("CustomFieldPartyClassification", UtilMisc.toMap("partyId", accountPartyId,"customFieldId",partyClassificationGroupId,"groupId","LEAD_CLASSIFICATION"),false);
					if(UtilValidate.isEmpty(partyClassificationData)){
						
						GenericValue partyClassification = delegator.makeValue("CustomFieldPartyClassification");
						partyClassification.put("partyId", accountPartyId);
						partyClassification.put("customFieldId", partyClassificationGroupId);
						partyClassification.put("groupId", "LEAD_CLASSIFICATION");
						Debug.log("partyClassification==="+partyClassification);
						partyClassification.create();
					}
					
				}
				String isExempt = (String) context.get("isExempt");
				String partyTaxId = (String) context.get("partyTaxId");
				//if(UtilValidate.isNotEmpty(isExempt) && UtilValidate.isNotEmpty(partyTaxId)) {
					Map<String, Object> inputMap = UtilMisc.toMap("partyTaxId",partyTaxId,"isExempt",isExempt,"partyId",accountPartyId,"userLogin",userLogin);
					dispatcher.runSync("updatePartyTaxExcemption",inputMap);
				//}
			} else {
				String errMsg = "";
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errMsg = accessMatrixRes.get("errorMessage").toString();
				} else {
					errMsg = "Access Denied";
				}
				return UtilMessage.createAndLogServiceError(errMsg, locale, MODULE);
			}
		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorUpdateAccountFail", locale, MODULE);
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorUpdateAccountFail", locale, MODULE);
		}
		return ServiceUtil.returnSuccess("Successfully Updated");
	}
	
	public static Map<String, Object> deactivateAccount(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale)context.get("locale");

        // what account we're expiring
        String accountPartyId = (String) context.get("partyId");

        // check that userLogin has CRMSFA_ACCOUNT_DEACTIVATE permission for this account
        String userLoginId = userLogin.getString("partyId");
        if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId) && !security.hasPermission(CrmConstants.SecurityPermissions.CRMSFA_ACCOUNT_DEACTIVATE, userLogin)){
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
        }

        // when to expire the account
        Timestamp expireDate = (Timestamp) context.get("expireDate");
        if (expireDate == null) {
            expireDate = UtilDateTime.nowTimestamp();
        }

        // in order to deactivate an account, we expire all party relationships on the expire date
        try {
        	//List of Opportunity  
        	EntityCondition myOppCondition = EntityCondition.makeCondition(EntityOperator.AND,
					  EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,accountPartyId),
					  EntityCondition.makeCondition("opportunityStatusId",EntityOperator.NOT_EQUAL,"OPPO_CLOSED")
					);
        	 List<GenericValue> patyOppList = EntityQuery.use(delegator).from("SalesOpportunity")
     				.where(myOppCondition).queryList();
        	
        	 //List of SR  
        	 List<GenericValue> myOpenSRList = EntityQuery.use(delegator).from("CustRequest").where("fromPartyId", accountPartyId, "statusId","SR_OPEN").cache(true).queryList();
             
             //List of ACTIVITY 
             EntityCondition myActCondition = EntityCondition.makeCondition(EntityOperator.AND,
             								  EntityCondition.makeCondition("cif",EntityOperator.EQUALS,accountPartyId),
             								  EntityCondition.makeCondition("currentStatusId",EntityOperator.NOT_EQUAL,"IA_MCOMPLETED")
             								);
             List<GenericValue> partyActivitiesList = EntityQuery.use(delegator).from("WorkEffort").where(myActCondition).cache(true).queryList();
        	
        	 if(patyOppList.size()>0 && myOpenSRList.size()>0 && partyActivitiesList.size()>0) {
            	 return (ServiceUtil.returnError(" To continue deactivation please close all open requests. "));
             }
        	 if(patyOppList.size()>0 && myOpenSRList.size()>0 ) {
            	 return (ServiceUtil.returnError(" To continue deactivation please close all Open requests. "));
             }
        	 if(patyOppList.size()>0 && partyActivitiesList.size()>0 ) {
            	 return (ServiceUtil.returnError(" To continue deactivation please close all Open requests. "));
             }
        	 if(myOpenSRList.size()>0 && partyActivitiesList.size()>0 ) {
            	 return (ServiceUtil.returnError(" To continue deactivation please close all Open requests. "));
             }
        	 if(myOpenSRList.size()>0 && patyOppList.size()>0 ) {
            	 return (ServiceUtil.returnError(" To continue deactivation please close all Open requests. "));
             }
        	 
             if(myOpenSRList.size()>0) {
            	 return (ServiceUtil.returnError(" To continue deactivation please close all open SR Requests. "));
             }

             if(patyOppList.size()>0) {
            	 return (ServiceUtil.returnError(" To continue deactivation please close all open Opportunities. "));
             }
             if(partyActivitiesList.size()>0) {
            	 return (ServiceUtil.returnError(" To continue deactivation please close all open Activites. "));
             }
             //ended
        	
            List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", accountPartyId, "roleTypeIdFrom", "ACCOUNT"),null,false);
            PartyHelper.expirePartyRelationships(partyRelationships, expireDate, dispatcher, userLogin);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorDeactivateAccountFail", locale, MODULE);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorDeactivateAccountFail", locale, MODULE);
        }

        // set the account party statusId to PARTY_DISABLED and register PartyDeactivation
        // TODO: improve this to support disabling on a future expireDate
        try {
            GenericValue accountParty = delegator.findOne("Party", UtilMisc.toMap("partyId", accountPartyId),false);
            accountParty.put("statusId", "PARTY_DISABLED");
            accountParty.store();

            delegator.create("PartyDeactivation", UtilMisc.toMap("partyId", accountPartyId, "deactivationTimestamp", expireDate));
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorDeactivateAccountFail", locale, MODULE);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> reassignAccountResponsibleParty(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String accountPartyId = (String) context.get("accountPartyId");
        String newPartyId = (String) context.get("newPartyId");

        // ensure reassign permission on this account
        String userLoginId = userLogin.getString("partyId");
        if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId) && !security.hasPermission(CrmConstants.SecurityPermissions.CRMSFA_ACCOUNT_REASSIGN, userLogin)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
        }
        try {
            // reassign relationship using a helper method
            boolean result = createResponsibleAccountRelationshipForParty(newPartyId, accountPartyId, userLogin, delegator, dispatcher);
            if (!result) {
                return UtilMessage.createAndLogServiceError("CrmErrorReassignFail", locale, MODULE);
            }
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorReassignFail", locale, MODULE);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorReassignFail", locale, MODULE);
        }
        return ServiceUtil.returnSuccess();
    }

}
