package org.groupfio.crm.service.service.impl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.UtilImport;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.authentication.api.AuthenticatorException;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

/**
 * @author Raja
 * @author Sharif
 *
 */
public class StaffProfileServiceImpl {

	private static final String MODULE = StaffProfileServiceImpl.class.getName();
    
	public static Map createStaffProfile(DispatchContext dctx, Map context) throws AuthenticatorException {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String staffId = (String) context.get("staffId");
		String staffName = (String) context.get("staffName");
		String firstName = (String) context.get("firstName");
		String lastName = (String) context.get("lastName");
		String authorisedBranches = (String) context.get("authorisedBranches");
		String userLoginId = (String) context.get("userLoginId");
		String securityGroupId = (String) context.get("securityGroupId");
		String emailId = (String) context.get("emailId");
		String dateFrom = (String) context.get("dateFrom");
		String dateTo = (String) context.get("dateTo");
		String password = UtilProperties.getPropertyValue("rate-portal", "defaultPassword");
		
		String partyId = null;
		Map<String, Object> result = new HashMap<String, Object>();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		if(UtilValidate.isNotEmpty(userLoginId) && UtilValidate.isNotEmpty(staffId)) {
			try {
				GenericValue party = EntityQuery.use(delegator).from("Party")
						.where("externalId", staffId).queryOne();
				if(party != null && party.size() > 0) {
					result.putAll(ServiceUtil.returnError("Staff ID already exists!"));
					return result;
				}

				GenericValue userLoginValidation = EntityQuery.use(delegator).from("UserLogin")
						.where("userLoginId", userLoginId).queryOne();
				if(userLoginValidation != null && userLoginValidation.size() > 0) {
					result.putAll(ServiceUtil.returnError("1bankId already exists!"));
					return result;
				}

				String statusId = "PARTY_ENABLED";
				String enabled = "Y";
				if(UtilValidate.isNotEmpty(dateTo)) {
					Timestamp dateToTs = UtilDateTime.stringToTimeStamp(dateTo, "dd-MM-yyyy", TimeZone.getDefault(), Locale.getDefault());
					if(UtilValidate.isNotEmpty(dateToTs) && nowTimestamp.after(dateToTs)) {
						statusId = "PARTY_DISABLED";
						enabled = "N";
					}
				}
				
				GenericValue system = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "admin").queryOne();
				// create person + userLogin
				Map<String, Serializable> createPersonUlMap = new HashMap<String, Serializable>();

				createPersonUlMap.put("firstName", firstName);
				createPersonUlMap.put("lastName", lastName);
				createPersonUlMap.put("nickname", staffName);
				createPersonUlMap.put("memberId", "DBS");
				createPersonUlMap.put("externalId", staffId);
				createPersonUlMap.put("statusId", statusId);
				
				createPersonUlMap.put("userLoginId", userLoginId);
				createPersonUlMap.put("currentPassword", password);
				createPersonUlMap.put("currentPasswordVerify", password);
				createPersonUlMap.put("userLogin", system);
				createPersonUlMap.put("enabled", enabled);
				Map<String, Object> createPersonResult;
				try {
					createPersonResult = dispatcher.runSync("createPersonAndUserLogin", createPersonUlMap);
				} catch (GenericServiceException e) {
					throw new AuthenticatorException(e.getMessage(), e);
				}
				if (ServiceUtil.isError(createPersonResult)) {
					throw new AuthenticatorException(ServiceUtil.getErrorMessage(createPersonResult));
				}
				partyId = (String) createPersonResult.get("partyId");

				// give this person a role of CUSTOMER
				GenericValue partyRole = delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER"));
				try {
					delegator.create(partyRole);
				} catch (GenericEntityException e) {
					Debug.logError(e, MODULE);
					throw new AuthenticatorException(e.getMessage(), e);
				}
				
				// create Authorised Branches
				if(UtilValidate.isNotEmpty(authorisedBranches)) {
					try {
						
						String partyidentification=org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, authorisedBranches, "AUTHORISED_BRANCHES");
					} catch (Exception e) {
						Debug.logError(e, MODULE);
						throw new AuthenticatorException(e.getMessage(), e);
					}
				}
				
				// create date from
				if(UtilValidate.isNotEmpty(dateFrom)) {
					try {
						String partyidentification=org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, dateFrom, "DATE_FROM");

					} catch (Exception e) {
						Debug.logError(e, MODULE);
						throw new AuthenticatorException(e.getMessage(), e);
					}
				}
				
				// create date to
				if(UtilValidate.isNotEmpty(dateTo)) {
					try {
						String partyidentification=org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, dateTo, "DATE_TO");

					} catch (Exception e) {
						Debug.logError(e, MODULE);
						throw new AuthenticatorException(e.getMessage(), e);
					}
				}
				
				// create email
				if (UtilValidate.isNotEmpty(emailId)) {
					Map<String, Serializable> createEmailMap = new HashMap<String, Serializable>();
					createEmailMap.put("emailAddress", emailId);
					createEmailMap.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
					createEmailMap.put("partyId", partyId);
					createEmailMap.put("userLogin", system);
					Map<String, Object> createEmailResult;
					try {
						createEmailResult = dispatcher.runSync("createPartyEmailAddress", createEmailMap);
					} catch (GenericServiceException e) {
						throw new AuthenticatorException(e.getMessage(), e);
					}
					if (ServiceUtil.isError(createEmailResult)) {
						throw new AuthenticatorException(ServiceUtil.getErrorMessage(createEmailResult));
					}
				}

				// create security group(s)
				Timestamp now = UtilDateTime.nowTimestamp();

				// check and make sure the security group exists
				GenericValue secGroup = null;
				try {
					secGroup = delegator.findOne("SecurityGroup", UtilMisc.toMap("groupId", securityGroupId), true);
				} catch (GenericEntityException e) {
					Debug.logError(e, e.getMessage(), MODULE);
				}

				// add it to the user if it exists
				if (secGroup != null) {
					Map<String, Serializable> createSecGrpMap = new HashMap<String, Serializable>();
					createSecGrpMap.put("userLoginId", userLoginId);
					createSecGrpMap.put("groupId", securityGroupId);
					createSecGrpMap.put("fromDate", now);
					createSecGrpMap.put("userLogin", system);

					Map<String, Object> createSecGrpResult;
					try {
						createSecGrpResult = dispatcher.runSync("addUserLoginToSecurityGroup", createSecGrpMap);
					} catch (GenericServiceException e) {
						throw new AuthenticatorException(e.getMessage(), e);
					}
					if (ServiceUtil.isError(createSecGrpResult)) {
						throw new AuthenticatorException(ServiceUtil.getErrorMessage(createSecGrpResult));
					}
				}


				result.putAll(ServiceUtil.returnSuccess("Staff profile created successfully!"));
			} catch (GenericEntityException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		result.put("partyId", partyId);
		return result;
	}
	
	public static Map updateStaffProfile(DispatchContext dctx, Map context) throws AuthenticatorException {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String staffId = (String) context.get("staffId");
		String staffName = (String) context.get("staffName");
		String firstName = (String) context.get("firstName");
		String lastName = (String) context.get("lastName");
		String authorisedBranches = (String) context.get("authorisedBranches");
		String userLoginId = (String) context.get("userLoginId");
		String securityGroupId = (String) context.get("securityGroupId");
		String emailId = (String) context.get("emailId");
		String dateFrom = (String) context.get("dateFrom");
		String dateTo = (String) context.get("dateTo");
		String partyId = (String) context.get("partyId");
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<GenericValue> toBeStored = FastList.newInstance();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if(UtilValidate.isNotEmpty(userLoginId) && UtilValidate.isNotEmpty(staffId)) {
			try {
				GenericValue party = EntityQuery.use(delegator).from("Party")
						.where("partyId", partyId).queryOne();
				if(party == null || party.size() < 1) {
					result.putAll(ServiceUtil.returnError("Invalid Staff Profile!"));
					return result;
				}

				GenericValue userLoginValidation = EntityQuery.use(delegator).from("UserLogin")
						.where("userLoginId", userLoginId).queryOne();
				if(userLoginValidation == null || userLoginValidation.size() < 1) {
					result.putAll(ServiceUtil.returnError("Invalid 1bankId!"));
					return result;
				}
				
				String statusId = "PARTY_ENABLED";
				String enabled = "Y";
				if(UtilValidate.isNotEmpty(dateTo)) {
					Timestamp dateToTs = UtilDateTime.stringToTimeStamp(dateTo, "dd-MM-yyyy", TimeZone.getDefault(), Locale.getDefault());
					if(UtilValidate.isNotEmpty(dateToTs) && nowTimestamp.after(dateToTs)) {
						statusId = "PARTY_DISABLED";
						enabled = "N";
					}
				}
				party.put("statusId", statusId);
				toBeStored.add(party);
				
				userLoginValidation.put("enabled", enabled);
				toBeStored.add(userLoginValidation);
	        	
				GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", partyId).queryOne();
				if(UtilValidate.isNotEmpty(person)) {
					person.put("firstName", firstName);
					person.put("lastName", lastName);
					person.put("nickname", staffName);
					toBeStored.add(person);
				}
				
				GenericValue emailPurpose = DataUtil.getActivePartyContactMechPurpose(delegator, partyId, "PRIMARY_EMAIL", null);
	         	if(UtilValidate.isNotEmpty(emailPurpose)){
	         		GenericValue emailContactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId",emailPurpose.getString("contactMechId")));
	             	emailContactMech.put("infoString", emailId);
	             	toBeStored.add(emailContactMech);
	         	} else if(!UtilValidate.isEmpty(emailId)){
	         		GenericValue emailContactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "EMAIL_ADDRESS", "infoString", emailId));
		            String emailContactMechId = emailContactMech.getString("contactMechId");
		            toBeStored.add(emailContactMech);

		            toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", emailContactMechId, "partyId", partyId, "fromDate", nowTimestamp,"allowSolicitation","Y")));
		            toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_EMAIL", emailContactMech, partyId, nowTimestamp, delegator));
	         	}

	         	GenericValue partyIdentification = EntityQuery.use(delegator).from("PartyIdentification")
	         			.where("partyId", partyId, "partyIdentificationTypeId", "AUTHORISED_BRANCHES").queryOne();
				if(UtilValidate.isNotEmpty(partyIdentification)) {
					partyIdentification.put("idValue", authorisedBranches);
					toBeStored.add(partyIdentification);
				} else if(UtilValidate.isNotEmpty(authorisedBranches)) {
					partyIdentification = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", "AUTHORISED_BRANCHES"));
					partyIdentification.put("idValue", authorisedBranches);
					toBeStored.add(partyIdentification);
				}
				
				partyIdentification = EntityQuery.use(delegator).from("PartyIdentification")
	         			.where("partyId", partyId, "partyIdentificationTypeId", "DATE_FROM").queryOne();
				if(UtilValidate.isNotEmpty(partyIdentification)) {
					partyIdentification.put("idValue", dateFrom);
					toBeStored.add(partyIdentification);
				} else if(UtilValidate.isNotEmpty(dateFrom)) {
					partyIdentification = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", "DATE_FROM"));
					partyIdentification.put("idValue", dateFrom);
					toBeStored.add(partyIdentification);
				}
				
				partyIdentification = EntityQuery.use(delegator).from("PartyIdentification")
	         			.where("partyId", partyId, "partyIdentificationTypeId", "DATE_TO").queryOne();
				if(UtilValidate.isNotEmpty(partyIdentification)) {
					partyIdentification.put("idValue", dateTo);
					toBeStored.add(partyIdentification);
				} else if(UtilValidate.isNotEmpty(dateTo)) {
					partyIdentification = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", "DATE_TO"));
					partyIdentification.put("idValue", dateTo);
					toBeStored.add(partyIdentification);
				}
				
				if(UtilValidate.isNotEmpty(securityGroupId)) {
					
					EntityCondition securityGroupCondition = EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId),
							EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, securityGroupId)), EntityOperator.AND);
					
					List<GenericValue> userLoginSecurityGroupList = EntityQuery.use(delegator).from("UserLoginSecurityGroup")
							.where(securityGroupCondition).filterByDate().queryList();
					if(userLoginSecurityGroupList != null && userLoginSecurityGroupList.size() > 0) {
						for(GenericValue userLoginSecurityGroupGv : userLoginSecurityGroupList) {
							GenericValue securityGroup = EntityQuery.use(delegator).from("SecurityGroup")
									.where("groupId", userLoginSecurityGroupGv.getString("groupId"), "customSecurityGroupType", "Y").queryFirst();
							if(UtilValidate.isNotEmpty(securityGroup)) {
								userLoginSecurityGroupGv.put("thruDate", nowTimestamp);
								toBeStored.add(userLoginSecurityGroupGv);
							}
						}
					}
					
					GenericValue userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup")
							.where("userLoginId", userLoginId, "groupId", securityGroupId).filterByDate().queryFirst();
					if(userLoginSecurityGroup == null || userLoginSecurityGroup.size() < 1) {
						userLoginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId));
						userLoginSecurityGroup.put("groupId", securityGroupId);
						userLoginSecurityGroup.put("fromDate", nowTimestamp);
						toBeStored.add(userLoginSecurityGroup);
					}
				}

				delegator.storeAll(toBeStored);
				
				result.putAll(ServiceUtil.returnSuccess("Staff profile updated successfully!"));
			} catch (GenericEntityException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		result.put("partyId", partyId);
		return result;
	}
}
