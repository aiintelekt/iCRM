package org.groupfio.lead.service.services.v1;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.fio.homeapps.util.PartyHelper;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.groupfio.lead.service.LeadServiceConstants.EmailVerifyStatus;
import org.groupfio.lead.service.LeadServiceConstants.VerifyMode;
import org.groupfio.lead.service.util.DataHelper;
import org.groupfio.lead.service.util.DataUtil;
import org.groupfio.lead.service.util.LeadServiceUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/***
 * 
 * @author Mahendran T
 *
 */

public class LeadServices {


	private static final String MODULE = LeadServices.class.getName();
	public static final String resource = "CustomerServiceUiLabels";
	public static final String SECURITY_RESOURCE = "SecurityextUiLabels";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createLead(DispatchContext dctx, Map<String, Object> context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		Map<String, Object> result = ServiceUtil.returnSuccess();

		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
		String leadName = (String) requestContext.get("leadName");
		String leadFullName = (String) requestContext.get("leadFullName");
		String leadFirstName = (String) requestContext.get("leadFirstName");
		String leadLastName = (String) requestContext.get("leadLastName");
		String leadPrimaryPhone = (String) requestContext.get("leadPrimPhone");
		String leadPrimaryEmail = (String) requestContext.get("leadPrimEmail"); 
		String dataSourceId = (String) requestContext.get("source");
		String leadCountry = (String) requestContext.get("leadCountry");
		if(UtilValidate.isNotEmpty(leadFullName)) {
			leadFirstName = leadFullName; 
			leadLastName ="";
		}

		boolean beganTransaction = false;

		try {

			if(UtilValidate.isEmpty(dataSourceId))
				dataSourceId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FREEMIUM_DATA_SOURCE_ID");

			Map<String, Object> context1 = new HashMap<>();
			context1.put("leadName", leadName);
			context1.put("firstName", leadFirstName);
			context1.put("lastName", leadLastName);
			context1.put("primaryEmail", leadPrimaryEmail);
			context1.put("primaryPhoneNumber", leadPrimaryPhone);
			context1.put("generalCountryGeoId", leadCountry);
			context1.put("userLogin",  userLogin);
			context1.put("personResponsible",  "admin");
			if(UtilValidate.isNotEmpty(dataSourceId))
				context1.put("dataSourceId", dataSourceId);

			// call the service ledportal.createLead
			Map<String, Object> result1 = FastMap.newInstance();
			if(!LeadServiceUtil.isEmailExist(leadPrimaryEmail, delegator)) {
				result1 = dispatcher.runSync("ledportal.createLead", context1);
				if(!ServiceUtil.isSuccess(result1)) {
					Debug.logError("Error : "+ ServiceUtil.getErrorMessage(result1), MODULE);
				}else {
					String isOtpEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_LEAD_OTP_PROCESS_ENABLED", "N");
					String partyId = (String) result1.get("partyId");

					if("Y".equals(isOtpEnabled) && UtilValidate.isNotEmpty(partyId)) {
						Map<String, Object> verifyResult = dispatcher.runSync("ls.verifyLeadV1", UtilMisc.toMap("userLogin",  userLogin, "emailId", leadPrimaryEmail, "mode", "VERIFICATION", "hashCode", "", "partyId", partyId ));
						if(!ServiceUtil.isSuccess(verifyResult)) {
							Debug.logError("Error : "+ ServiceUtil.getErrorMessage(verifyResult), MODULE);
						}
					}

					//add the lead to the FREEMIUM SEGMENTATION
					String groupId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FREEMIUM_LEAD_SEG_GROUP","FREEMIUM_CUSTOMER");
					String customFieldId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FREEMIUM_LEAD_CUST_FIELD", "FREEMIUM_CUST_INFO");
					if(UtilValidate.isNotEmpty(groupId)&& UtilValidate.isNotEmpty(customFieldId)) {
						GenericValue addHHSegmentElg=delegator.makeValue("CustomFieldPartyClassification",UtilMisc.toMap("partyId",partyId,"customFieldId",customFieldId,"groupId",groupId ));
						addHHSegmentElg.put("inceptionDate", UtilDateTime.nowTimestamp());
						addHHSegmentElg.create();
					}
				}
			}else {
				if(LeadServiceUtil.isEmailExist(leadPrimaryEmail, delegator) && LeadServiceUtil.isEmailAlreadyExistsAndEditable(leadPrimaryEmail, delegator)) {
					result1 = dispatcher.runSync("ls.updateLeadV1", UtilMisc.toMap("requestContext", requestContext, "userLogin", userLogin));
				}else {
					Debug.logError("Email already exist "+ leadPrimaryEmail, MODULE);
					return ServiceUtil.returnError("Email already exist "+ leadPrimaryEmail);
				}
			}
			TransactionUtil.commit(beganTransaction);
		} catch (Exception e) {
			String errMsg = "Error handling customer service";
			Debug.logError(e, errMsg, MODULE);
			try {
				TransactionUtil.rollback(beganTransaction, errMsg, e);
			} catch (GenericTransactionException gte2) {
				Debug.logError(gte2, "Unable to rollback transaction", MODULE);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateLead(DispatchContext dctx, Map<String, Object> context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		Map<String, Object> result = ServiceUtil.returnSuccess();

		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
		String leadName = (String) requestContext.get("leadName");
		String leadFullName = (String) requestContext.get("leadFullName");
		String leadFirstName = (String) requestContext.get("leadFirstName");
		String leadLastName = (String) requestContext.get("leadLastName");
		String leadPrimaryPhone = (String) requestContext.get("leadPrimPhone");
		String leadPrimaryEmail = (String) requestContext.get("leadPrimEmail"); 
		String leadOldEmail = (String) requestContext.get("leadOldEmail"); 
		String dataSourceId = (String) requestContext.get("source");
		String leadCountry = (String) requestContext.get("leadCountry");
		if(UtilValidate.isNotEmpty(leadFullName)) {
			leadFirstName = leadFullName; 
			leadLastName ="";
		}
		Map<String, Object> serviceResults = new HashMap<String, Object>();

		boolean beganTransaction = false;

		try {

			if(UtilValidate.isEmpty(dataSourceId))
				dataSourceId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FREEMIUM_DATA_SOURCE_ID");
			String contactPartyId = null;
			if(UtilValidate.isNotEmpty(leadOldEmail))
				contactPartyId =  DataHelper.getPartyIdByEmail(delegator, leadOldEmail);
			if(UtilValidate.isEmpty(contactPartyId) && LeadServiceUtil.isEmailExist(leadPrimaryEmail, delegator) && LeadServiceUtil.isEmailAlreadyExistsAndEditable(leadPrimaryEmail, delegator)) {
				contactPartyId =  DataHelper.getPartyIdByEmail(delegator, leadPrimaryEmail);
			}
			if(UtilValidate.isNotEmpty(contactPartyId)) {
				//get lead party id
				String partyId = LeadServiceUtil.getLeadPartyIdByContact(contactPartyId, delegator);
				if(UtilValidate.isNotEmpty(partyId)) {
					Map<String, Object> context1 = new HashMap<>();
					context1.put("leadName", leadName);
					context1.put("partyId", partyId);
					context1.put("userLogin",  userLogin);
					if(UtilValidate.isNotEmpty(dataSourceId))
						context1.put("dataSourceId", dataSourceId);

					// call the service ledportal.updateLead partyId,leadName
					Map<String, Object> result1 = dispatcher.runSync("ledportal.updateLead", context1);
					if(!ServiceUtil.isSuccess(result1)) {
						Debug.logError("Error : "+ ServiceUtil.getErrorMessage(result1), MODULE);
					}else {

						// call the service crmsfa.updateContact partyId,firstName,lastName
						context1.clear();
						context1.put("partyId", contactPartyId);
						context1.put("firstName", leadFirstName);
						context1.put("lastName", leadLastName);
						context1.put("userLogin",  userLogin);
						result1 = dispatcher.runSync("crmsfa.updateContact", context1);
						if(!ServiceUtil.isSuccess(result1)) {
							Debug.logError("Error : "+ ServiceUtil.getErrorMessage(result1), MODULE);
						}else {

							List<GenericValue> partyPrimLocList = DataHelper.getPrimaryContactInfoByType(delegator, partyId, "PRIMARY_LOCATION");
							List<String> primLocContactMechList = EntityUtil.getFieldListFromEntityList(partyPrimLocList, "contactMechId", true);
							//create primary contact 
							String primartyContactMechId = "";
							if(UtilValidate.isNotEmpty(leadCountry)) {
								EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
										EntityCondition.makeCondition("contactMechId", EntityOperator.IN, primLocContactMechList),
										EntityCondition.makeCondition("countryGeoId", EntityOperator.EQUALS, leadCountry)
										);
								GenericValue primartylocation = EntityQuery.use(delegator).from("PostalAddress").where(condition).queryFirst();
								if(UtilValidate.isNotEmpty(primartylocation)) {
									primartyContactMechId = primartylocation.getString("contactMechId");
								} else {
									if(UtilValidate.isNotEmpty(primLocContactMechList)) {
										for(String primLocConId : primLocContactMechList) {
											Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",primLocConId,"userLogin",userLogin));
											if(ServiceUtil.isSuccess(deletePartyContactMech)){
												Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
											}
										}
									}
								}
								Map<String, Object> postalAddress = UtilMisc.toMap("partyId", partyId, "userLogin", (Object) userLogin, "contactMechTypeId", "POSTAL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_LOCATION");
								postalAddress.put("countryGeoId", leadCountry);

								if(UtilValidate.isEmpty(primartyContactMechId)) {
									serviceResults = dispatcher.runSync("createPartyPostalAddress", postalAddress);
								} else {
									postalAddress.remove("contactMechPurposeTypeId");
									postalAddress.remove("contactMechTypeId");
									postalAddress.put("contactMechId", primartyContactMechId);
									serviceResults = dispatcher.runSync("updatePartyPostalAddress", postalAddress);
								}

								if (ServiceUtil.isError(serviceResults)) {
									return serviceResults;
								}
							} else {
								if(UtilValidate.isNotEmpty(primLocContactMechList)) {
									for(String primLocConId : primLocContactMechList) {
										Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",primLocConId,"userLogin",userLogin));
										if(ServiceUtil.isSuccess(deletePartyContactMech)){
											Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
										}
									}
								}
							}
						}
					}

					List<GenericValue> partyPrimPhoneList = DataHelper.getPrimaryContactInfoByType(delegator, contactPartyId, "PRIMARY_PHONE");
					List<String> primPhonContactMechList = EntityUtil.getFieldListFromEntityList(partyPrimPhoneList, "contactMechId", true);
					//create primary contact 
					String primartyContactMechId = "";
					if(UtilValidate.isNotEmpty(leadPrimaryPhone)) {
						leadPrimaryPhone = leadPrimaryPhone.replaceAll("-", "");
						EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("contactMechId", EntityOperator.IN, primPhonContactMechList),
								EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, leadPrimaryPhone)
								);
						GenericValue primartyPhone = EntityQuery.use(delegator).from("TelecomNumber").where(condition).queryFirst();

						if(UtilValidate.isNotEmpty(primartyPhone)) {
							primartyContactMechId = primartyPhone.getString("contactMechId");
						} else {
							if(UtilValidate.isNotEmpty(primPhonContactMechList)) {
								for(String primPhoConId : primPhonContactMechList) {
									Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",contactPartyId,"contactMechId",primPhoConId,"userLogin",userLogin));
									if(ServiceUtil.isSuccess(deletePartyContactMech)){
										Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
									}
								}
							}
						}

						Map<String, Object> telecomNumber = UtilMisc.toMap("partyId", contactPartyId, "userLogin", (Object) userLogin, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PRIMARY_PHONE");
						telecomNumber.put("contactNumber", leadPrimaryPhone);

						if(UtilValidate.isEmpty(primartyContactMechId)) {
							serviceResults = dispatcher.runSync("createPartyTelecomNumber", telecomNumber);
						} else {
							telecomNumber.remove("contactMechPurposeTypeId");
							telecomNumber.remove("contactMechTypeId");
							telecomNumber.put("contactMechId", primartyContactMechId);
							serviceResults = dispatcher.runSync("updatePartyTelecomNumber", telecomNumber);
						}

						if (ServiceUtil.isError(serviceResults)) {
							return serviceResults;
						}
					} else {
						if(UtilValidate.isNotEmpty(primPhonContactMechList)) {
							for(String primPhoConId : primPhonContactMechList) {
								Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",contactPartyId,"contactMechId",primPhoConId,"userLogin",userLogin));
								if(ServiceUtil.isSuccess(deletePartyContactMech)){
									Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
								}
							}
						}
					}

					//primary email
					List<GenericValue> partyPrimEmailList = DataHelper.getPrimaryContactInfoByType(delegator, contactPartyId, "PRIMARY_EMAIL");
					List<String> primEmailContactMechList = EntityUtil.getFieldListFromEntityList(partyPrimEmailList, "contactMechId", true);

					//create primary contact 
					String primartyEmailContactMechId = "";
					if(UtilValidate.isNotEmpty(leadPrimaryEmail)) {

						EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("contactMechId", EntityOperator.IN, primEmailContactMechList),
								EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, leadPrimaryEmail)
								);
						GenericValue primaryEmail = EntityQuery.use(delegator).from("ContactMech").where(condition).queryFirst();

						if(UtilValidate.isNotEmpty(primaryEmail)) {
							primartyEmailContactMechId = primaryEmail.getString("contactMechId");
						} else {
							if(UtilValidate.isNotEmpty(primEmailContactMechList)) {
								for(String primEamilConId : primEmailContactMechList) {
									Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",contactPartyId,"contactMechId",primEamilConId,"userLogin",userLogin));
									if(ServiceUtil.isSuccess(deletePartyContactMech)){
										Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
									}
								}
							}
						}

						Map<String, Object> emailAddress = UtilMisc.toMap("partyId", contactPartyId, "userLogin", (Object) userLogin, "contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL");
						emailAddress.put("emailAddress", leadPrimaryEmail);

						if(UtilValidate.isEmpty(primartyEmailContactMechId)) {
							serviceResults = dispatcher.runSync("createPartyEmailAddress", emailAddress);
						} else {
							emailAddress.remove("contactMechPurposeTypeId");
							emailAddress.remove("contactMechTypeId");
							emailAddress.put("contactMechId", primartyEmailContactMechId);
							serviceResults = dispatcher.runSync("updatePartyEmailAddress", emailAddress);
						}

						if (ServiceUtil.isError(serviceResults)) {
							return serviceResults;
						}
					} else {
						if(UtilValidate.isNotEmpty(primEmailContactMechList)) {
							for(String primEamilConId : primEmailContactMechList) {
								Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",contactPartyId,"contactMechId",primEamilConId,"userLogin",userLogin));
								if(ServiceUtil.isSuccess(deletePartyContactMech)){
									Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
								}
							}
						}
					}

					try {

						Map<String, Object> assocResult = dispatcher.runSync("common.syncRelatedPartyAssoc", UtilMisc.toMap("userLogin",  userLogin,"partyId",partyId,"partyIdFrom", contactPartyId ));
						if (ServiceUtil.isError(assocResult)) {
							Debug.logError("Error occured when association", MODULE);
						}
					}catch(Exception e) {
						e.printStackTrace();
					}

					String isOtpEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_LEAD_OTP_PROCESS_ENABLED", "N");
					//String partyId = (String) result1.get("partyId");

					if("Y".equals(isOtpEnabled) && UtilValidate.isNotEmpty(contactPartyId)) {
						Map<String, Object> verifyResult = dispatcher.runSync("ls.verifyLeadV1", UtilMisc.toMap("userLogin",  userLogin, "emailId", leadPrimaryEmail, "mode", "VERIFICATION", "hashCode", "", "partyId", contactPartyId ));
						if(!ServiceUtil.isSuccess(verifyResult)) {
							Debug.logError("Error : "+ ServiceUtil.getErrorMessage(verifyResult), MODULE);
						}
					}else {
						GenericValue party =  EntityQuery.use(delegator).from("Party").where("partyId", contactPartyId).queryFirst();
						if(UtilValidate.isNotEmpty(party)) {
							party.set("statusId", "PARTY_ENABLED");
							party.store();
							
							LeadServiceUtil.enableLeadByContact(contactPartyId, delegator);
						}
					}
				}
			}else {
				
				if(!LeadServiceUtil.isEmailExist(leadPrimaryEmail, delegator)) {
					//call create lead
					Map<String, Object> createResult = dispatcher.runSync("ls.createLeadV1",UtilMisc.toMap("requestContext",requestContext,"userLogin", userLogin));
					if(!ServiceUtil.isSuccess(createResult)) {
						Debug.logError("Error : "+ ServiceUtil.getErrorMessage(createResult), MODULE);
						return createResult;
					}
				}else {
					Debug.logError("Email already exist "+ leadPrimaryEmail, MODULE);
					return ServiceUtil.returnError("Email already exist "+ leadPrimaryEmail);
				}
			}
			TransactionUtil.commit(beganTransaction);
		} catch (Exception e) {
			String errMsg = "Error handling customer service";
			Debug.logError(e, errMsg, MODULE);
			try {
				TransactionUtil.rollback(beganTransaction, errMsg, e);
			} catch (GenericTransactionException gte2) {
				Debug.logError(gte2, "Unable to rollback transaction", MODULE);
			}
		}
		return result;
	}

	public static Map<String, Object> verifyLead(DispatchContext dctx, Map<String, Object> context){

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> result = new HashMap<>();

		String emailId = (String) context.get("emailId");
		String mode = (String) context.get("mode");
		String hashCode = (String) context.get("hashCode");
		String otp = (String) context.get("otp");
		try {
			String _customer_secret_key = EntityUtilProperties.getPropertyValue("customer", "customer.email.verify.key", delegator);
			String _separator_ = EntityUtilProperties.getPropertyValue("customer", "enccrypt.separator", delegator);
			_separator_ = UtilValidate.isNotEmpty(_separator_) ? _separator_ : "&";
			if(VerifyMode.VERIFICATION.equals(mode)) {
				// needs to send email with hash value (partyId,timestamp)

				String partyId =  DataHelper.getPartyIdByEmail(delegator, emailId);
				if(UtilValidate.isEmpty(partyId))
					partyId = (String) context.get("partyId");

				String partyName = PartyHelper.getPartyName(delegator, partyId, false);
				Debug.logInfo("==========partyName==== "+partyName,MODULE);

				try {
					Map<String, Object> sendMap = new HashMap<>();
					String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FREEMIUM_LEAD_OTP_TPL");

					Debug.log("userLogin==== "+userLogin);
					if(UtilValidate.isNotEmpty(templateId)) {
						GenericValue template = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",templateId), false);
						if(UtilValidate.isNotEmpty(template)) {

							GenericValue leadOtp = null;
							String _6_digit_code = DataUtil.randomNumRecursive(delegator);
							if(UtilValidate.isNotEmpty(_6_digit_code)) {
								leadOtp = EntityQuery.use(delegator).from("SecurityTracking").where(UtilMisc.toMap("partyId", partyId, "trackingTypeId", "EMAIL_OTP")).queryFirst();
								if(UtilValidate.isEmpty(leadOtp))
									leadOtp = delegator.makeValue("SecurityTracking",UtilMisc.toMap("partyId", partyId, "trackingTypeId", "EMAIL_OTP"));
								leadOtp.set("hashCode", "");
								leadOtp.set("statusId", EmailVerifyStatus.SENT);
								leadOtp.set("value", _6_digit_code);
								leadOtp.set("fromDate", UtilDateTime.nowTimestamp());
								leadOtp.set("thruDate", null);
								delegator.createOrStore(leadOtp);

								Map<String,Object> resultMap = sendEmail(templateId, partyId, emailId, userLogin, true, _6_digit_code, delegator, dispatcher);
								if(ServiceUtil.isSuccess(resultMap)) {
									GenericValue party =  EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryFirst();
									if(UtilValidate.isNotEmpty(party)) {
										party.set("statusId", "PARTY_DISABLED");
										party.store();
										
										LeadServiceUtil.diableLeadByContact(partyId, delegator);
									}
								}else {
									Debug.logError("Error Occurred in sending OTP "+ServiceUtil.getErrorMessage(resultMap), mode);
									return resultMap;
								}
							}else {
								Debug.logInfo("OTP not gnerated", MODULE);
							}
						} else {
							Debug.logInfo("OTP not generated", MODULE);
						}
					}else {
						Debug.logInfo("Service Error : No Template Found in product store email setting to send mail.", MODULE);
						return ServiceUtil.returnError("No Template Found in product store email setting to send mail");
					}
				}
				catch (Exception e) {
					Debug.logError(e,"Service error", MODULE);
					return ServiceUtil.returnError("Service error :" + e.getMessage());
				}
				result = ServiceUtil.returnSuccess();
				result.put("partyId", partyId);
				result.put("hashCode", hashCode);

			} else if(VerifyMode.VALIDATION.equals(mode)) {
				// validate the code and enable the party
				String partyId =  DataHelper.getPartyIdByEmail(delegator, emailId);

				GenericValue leadOtp = EntityQuery.use(delegator).from("SecurityTracking").where("value", otp,"partyId", partyId, "trackingTypeId","EMAIL_OTP").filterByDate().queryFirst();
				if(UtilValidate.isNotEmpty(leadOtp)) {
					hashCode = UtilValidate.isNotEmpty(hashCode) ? hashCode : leadOtp.getString("hashCode");
					partyId = leadOtp.getString("partyId");
					GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryFirst();
					if(UtilValidate.isNotEmpty(party)) {
						party.set("statusId", "PARTY_ENABLED");
						party.store();
						
						LeadServiceUtil.enableLeadByContact(partyId, delegator);

						leadOtp.set("thruDate", UtilDateTime.nowTimestamp());
						leadOtp.set("statusId", EmailVerifyStatus.VERIFIED);
						leadOtp.store();

						String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FREEMIUM_LEAD_ACK_TPL");

						Debug.log("userLogin==== "+userLogin);
						if(UtilValidate.isNotEmpty(templateId)) {
							GenericValue template = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",templateId), false);
							if(UtilValidate.isNotEmpty(template)) {

								Map<String,Object> resultMap = sendEmail(templateId, partyId, emailId, userLogin, false, null, delegator, dispatcher);
								if(!ServiceUtil.isSuccess(resultMap)) {
									Debug.logError("Error Occurred in sending email "+ServiceUtil.getErrorMessage(resultMap), mode);
									return resultMap;
								}
							}else {
								Debug.logInfo("Service Error : No Template Found in product store email setting to send mail.", MODULE);
								return ServiceUtil.returnError("No Template Found in product store email setting to send mail");
							}
						}else {
							Debug.logInfo("Service Error : No Template Found in product store email setting to send mail.", MODULE);
							return ServiceUtil.returnError("No Template Found in product store email setting to send mail");
						}
					}
				} else {
					return ServiceUtil.returnError("Invalid OTP!");
				}
				result = ServiceUtil.returnSuccess();
				result.put("partyId", partyId);
				result.put("hashCode", hashCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String,Object> sendEmail(String templateId, String partyId, String emailId, GenericValue userLogin, boolean isOtp,String _6_digit_code, Delegator delegator, LocalDispatcher dispatcher){
		try {
			if(UtilValidate.isNotEmpty(templateId)) {
				GenericValue template = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",templateId), false);
				if(UtilValidate.isNotEmpty(template)) {
					String templateFormContent = template.getString("templateFormContent");
					if (UtilValidate.isNotEmpty(templateFormContent)) {
						if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
							templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
						}
					}
					String partyName = PartyHelper.getPartyName(delegator, partyId, false);

					String nsender = "";
					//String nto = "";
					String ccAddresses = "";
					String subject = "";
					String emailContent = "";

					if(UtilValidate.isNotEmpty(template.getString("subject")))
						subject = template.getString("subject");
					nsender = template.getString("senderEmail");

					// prepare email content [start]
					Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
					extractContext.put("delegator", delegator);
					extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
					extractContext.put("fromEmail", nsender);
					extractContext.put("toEmail", emailId);
					extractContext.put("partyId", partyId);
					if(isOtp)
						extractContext.put("OTP", _6_digit_code);
					extractContext.put("partyName", partyName);
					extractContext.put("emailContent", templateFormContent);
					extractContext.put("templateId", templateId);

					Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
					emailContent = (String) extractResultContext.get("emailContent");

					Map<String, Object> callCtxt = FastMap.newInstance();
					Map<String, Object> requestContext = FastMap.newInstance();

					requestContext.put("nsender", nsender);
					requestContext.put("nto", emailId);
					requestContext.put("subject", subject);
					requestContext.put("emailContent", emailContent);
					requestContext.put("templateId", templateId);
					requestContext.put("nbcc", ccAddresses);

					callCtxt.put("requestContext", requestContext);
					callCtxt.put("userLogin", userLogin);

					Map<String,Object> sendResp = dispatcher.runSync("common.sendEmail", callCtxt);

					if(!ServiceUtil.isSuccess(sendResp)) {
						Debug.logInfo("Service Error : ", MODULE);
						return ServiceUtil.returnError("Service error : sendMailFromScreen "+ServiceUtil.getErrorMessage(sendResp));
					}

				}else {
					Debug.logInfo("Service Error : No Template Found in product store email setting to send mail.", MODULE);
					return ServiceUtil.returnError("No Template Found in product store email setting to send mail");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}

}
