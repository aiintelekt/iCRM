package org.fio.customer.service.service.v3;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.fio.admin.portal.util.DataUtil;
import org.fio.customer.service.constant.CustomerConstants.EmailVerifyStatus;
import org.fio.customer.service.constant.CustomerConstants.UserAccountMode;
import org.fio.customer.service.constant.CustomerConstants.VerifyMode;
import org.fio.customer.service.util.DataHelper;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilMessage;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.common.authentication.AuthHelper;
import org.ofbiz.common.authentication.api.AuthenticatorException;
import org.ofbiz.common.login.LoginServices;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityCrypto;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.LoginWorker;

import javolution.util.FastMap;

/***
 * 
 * @author Mahendran T
 *
 */

public class CustomerServices {


	private static final String MODULE = CustomerServices.class.getName();
	public static final String resource = "CustomerServiceUiLabels";
	public static final String SECURITY_RESOURCE = "SecurityextUiLabels";
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createCustomer(DispatchContext dctx, Map<String, Object> context) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	
    	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		NumberFormat nf = NumberFormat.getInstance(locale);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
		String custId = (String) requestContext.get("custId");
		String custFullName = (String) requestContext.get("custFullName");
		String custFirstName = (String) requestContext.get("custFirstName");
		String custLastName = (String) requestContext.get("custLastName");
		String custPrimaryPhone = (String) requestContext.get("custPrimPhone");
		String custLocName = (String) requestContext.get("custLocName");
		String custSourceDateStr = (String) requestContext.get("custSourceDate");
		String roleTypeId = (String) requestContext.get("custRoleType");
		String custPrimaryEmail = (String) requestContext.get("custPrimEmail"); 
		String dataSourceId = (String) requestContext.get("source");
		if(UtilValidate.isNotEmpty(custFullName)) {
			custFirstName = custFullName; 
			custLastName ="";
		}
		
		boolean beganTransaction = false;
		
		try {
			String ownerId = UtilValidate.isNotEmpty(context.get("owenerId")) ? (String) context.get("ownerId") : userLogin.getString("userLoginId");
			LocalDateTime custSourceDate = LocalDateTime.now();
			if(UtilValidate.isNotEmpty(custSourceDateStr) && DataUtil.isDate(custSourceDateStr, "timestamp"))
				custSourceDate = LocalDateTime.parse(custSourceDateStr);
			
			beganTransaction = TransactionUtil.begin();
			
			//Create Customer(ACCOUNT)
			String partyId = "";
			
			// if we're given the partyId to create, then verify it is free to use
			if (partyId != null) {
				Map<String, Object> findMap =  UtilMisc.<String, Object>toMap("partyId", partyId);
				GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId),false);
				if (party != null) {
					// TODO maybe a more specific message such as "Account already exists"
					return UtilMessage.createAndLogServiceError("person.create.person_exists", findMap, locale, MODULE);
				}
			}

			roleTypeId = UtilValidate.isNotEmpty(roleTypeId) ? roleTypeId.toUpperCase() : "CUSTOMER";
			Map<String, Object> serviceResults = new HashMap<String, Object>();
			Map<String, Object> input = new HashMap<String, Object>();
			if("CUSTOMER".equals(roleTypeId)) {
				input = UtilMisc.toMap("firstName", custFirstName, "partyId", partyId,"preferredCurrencyUomId","USD");
				if(UtilValidate.isNotEmpty(custLastName))
					input.put("lastName", custLastName);
				
				serviceResults = dispatcher.runSync("createPerson", input);
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
				partyId = (String) serviceResults.get("partyId");
			} else if("ACCOUNT".equals(roleTypeId)) {
				// create the Party and PartyGroup, which results in a partyId
				input = UtilMisc.toMap("groupName", custFirstName, "partyId", partyId,"preferredCurrencyUomId","USD");
				serviceResults = dispatcher.runSync("createPartyGroup", input);
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
				partyId = (String) serviceResults.get("partyId");
			}
			
			if(UtilValidate.isEmpty(custId))
				custId = partyId;
			
			//create Party Identification
			DataHelper.createPartyIdentification(delegator, UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", "WW_CUST", "idValue", custId));
			
			// create a PartyRole for the resulting Account partyId with roleTypeId = ACCOUNT
			serviceResults = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId, "userLogin", userLogin));
			
			
			if (ServiceUtil.isError(serviceResults)) {
				return serviceResults;
			}

			String timeZoneDesc = (String) context.get("timeZoneDesc");	
			Debug.log("timeZoneDesc==********=="+timeZoneDesc);
			GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
			if(UtilValidate.isNotEmpty(party)){
				party.put("statusId", "PARTY_ENABLED");
				party.put("roleTypeId", roleTypeId);
				party.put("createdDate", UtilValidate.isNotEmpty(custSourceDate) ? Timestamp.valueOf(custSourceDate): UtilDateTime.nowTimestamp());
				party.put("createdByUserLogin", userLogin.getString("userLoginId"));
				party.put("timeZoneDesc", timeZoneDesc);
				party.put("externalId", custId);
				if(UtilValidate.isNotEmpty(dataSourceId))
					party.put("dataSourceId", dataSourceId);
				party.store();
			}
			
			if(UtilValidate.isNotEmpty(dataSourceId)) {
				EntityCondition dataSrcCondition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
							EntityCondition.makeCondition("dataSourceId", EntityOperator.EQUALS, dataSourceId)
						);
				GenericValue partyDataSource = EntityQuery.use(delegator).from("PartyDataSource").where(dataSrcCondition).queryFirst();
				if(UtilValidate.isEmpty(partyDataSource)) {
					partyDataSource = delegator.makeValue("PartyDataSource", UtilMisc.toMap("partyId", partyId, "dataSourceId", dataSourceId, "fromDate", UtilDateTime.nowTimestamp()));
					partyDataSource.create();
				}
				
			}
			
			//update owner roletype/ownerbu/empteam in  party table
			dispatcher.runSync("ap.updateSecurityPartyInfo", UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId"), "ownerId",ownerId, "roleTypeId", roleTypeId, "partyId", partyId ));
			
			// create party attribute
			if(UtilValidate.isNotEmpty(custLocName))
				//DataHelper.createPartyAttribute(delegator, UtilMisc.toMap("partyId", partyId, "attrName", "CUST_LOC_NAME", "attrValue", custLocName));
			org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, custLocName, "CUST_LOC_NAME");

			if(!"CUSTOMER".equals(roleTypeId)) {
				// create PartySupplementalData
				GenericValue partyData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", partyId));
				partyData.setNonPKFields(context);
				partyData.create();
			}
			
			//create primary contact
			if(UtilValidate.isNotEmpty(custPrimaryPhone)) {
				custPrimaryPhone = custPrimaryPhone.replaceAll("-", "");
				Map<String, Object> telecomNumber = UtilMisc.toMap("partyId", partyId, "userLogin", (Object) userLogin, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PRIMARY_PHONE");
            	telecomNumber.put("contactNumber", custPrimaryPhone);
            	
            	Map<String, Object> serviceResultss = dispatcher.runSync("createPartyTelecomNumber", telecomNumber);
            	
            	if (ServiceUtil.isError(serviceResultss)) {
            		return serviceResults;
            	}
			}
			if(UtilValidate.isNotEmpty(custPrimaryEmail)) {
				Map<String, Object> emailAddress = UtilMisc.toMap("partyId", partyId, "userLogin", (Object) userLogin, "contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL");
				emailAddress.put("emailAddress", custPrimaryEmail);
            	
            	Map<String, Object> serviceResultss = dispatcher.runSync("createPartyEmailAddress", emailAddress);
            	
            	if (ServiceUtil.isError(serviceResultss)) {
            		return serviceResults;
            	}
			}
			
			//create cust reference
			List<Map<String, Object>> custRefList = (List<Map<String, Object>>) requestContext.get("custRef");
			if(UtilValidate.isNotEmpty(custRefList)) {
				for(Map<String, Object> custRef : custRefList) {
					String altRefName = (String) custRef.get("alt_ref_name");
					String altRefId = (String) custRef.get("alt_ref_id");
					
					DataHelper.createPartyIdentification(delegator, UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", altRefName, "idValue", altRefId));
				}
			}
			
			
			// create ship contact info
			List<Map<String, Object>> custShipToList = (List<Map<String, Object>>) requestContext.get("custShipTo");
			if(UtilValidate.isNotEmpty(custShipToList)) {
				String requiredFields = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUST_API_SHIP_ADDR_MANDATORY", "cust_addr1,cust_state,cust_zip");
				for(Map<String, Object> custShipTo : custShipToList) {
					if(!DataHelper.validateCustAddressData(DataUtil.stringToList(requiredFields, ","), custShipTo)) 
						break;
					
					String primaryPhoneNumber = (String) custShipTo.get("cust_phone");
					if(UtilValidate.isNotEmpty(primaryPhoneNumber))
						primaryPhoneNumber = primaryPhoneNumber.replaceAll("-", "");
					String generalAddress1 = (String) custShipTo.get("cust_addr1");
					String generalAddress2 = (String) custShipTo.get("cust_addr2");
					String generalCity = (String) custShipTo.get("cust_city");
					String generalStateProvinceGeoId = (String) custShipTo.get("cust_state");
					String generalPostalCode = (String) custShipTo.get("cust_zip");
					String generalCountryGeoId = (String) custShipTo.get("cust_country");
					
					Map<String, Object> context1 = new HashMap<String, Object>();
					
					if(UtilValidate.isNotEmpty(primaryPhoneNumber)) {
						context1.put("primaryPhoneNumber", primaryPhoneNumber);
						context1.put("phonePurposeTypeId", "PHONE_SHIPPING");
					}
					
					context1.put("generalAddress1", UtilValidate.isNotEmpty(generalAddress1) ? generalAddress1 : "");
					context1.put("generalAddress2", UtilValidate.isNotEmpty(generalAddress2) ? generalAddress2 : "");
					context1.put("generalCity", UtilValidate.isNotEmpty(generalCity) ? generalCity : "");
					context1.put("generalStateProvinceGeoId", UtilValidate.isNotEmpty(generalStateProvinceGeoId) ? generalStateProvinceGeoId : "");
					context1.put("generalPostalCode", UtilValidate.isNotEmpty(generalPostalCode) ? generalPostalCode : "");
					context1.put("generalCountryGeoId", UtilValidate.isNotEmpty(generalCountryGeoId) ? generalCountryGeoId : "USA");
					context1.put("postalPurposeTypeId", "SHIPPING_LOCATION");
					context1.put("userLogin", userLogin);
					
					ModelService service = dctx.getModelService("crmsfa.createBasicContactInfoForParty");
					input = service.makeValid(context1, "IN");
					input.put("partyId", partyId);
					serviceResults = dispatcher.runSync(service.name, input);
					
					if(!ServiceUtil.isSuccess(serviceResults)) {
						return serviceResults;
					}
					String phoneContactMechId = (String) serviceResults.get("primaryPhoneContactMechId");
					if(UtilValidate.isNotEmpty(phoneContactMechId)) {
						String addressContactMechId = (String) serviceResults.get("generalAddressContactMechId");
						GenericValue postalAdd = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId", addressContactMechId).queryFirst();
						if(UtilValidate.isNotEmpty(postalAdd)) {
							postalAdd.set("phoneContactMechId", phoneContactMechId);
							postalAdd.store();
						}
					}
				}
			}
			
			// create bill contact info
			List<Map<String, Object>> custBillToList = (List<Map<String, Object>>) requestContext.get("custBillTo");
			if(UtilValidate.isNotEmpty(custBillToList)) {
				String requiredFields = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUST_API_BILL_ADDR_MANDATORY", "cust_addr1,cust_state,cust_zip");
				for(Map<String, Object> custBillTo : custBillToList) {
					if(!DataHelper.validateCustAddressData(DataUtil.stringToList(requiredFields, ","), custBillTo)) 
						break;
					String primaryPhoneNumber = (String) custBillTo.get("cust_phone");
					if(UtilValidate.isNotEmpty(primaryPhoneNumber))
						primaryPhoneNumber = primaryPhoneNumber.replaceAll("-", "");
					String generalAddress1 = (String) custBillTo.get("cust_addr1");
					String generalAddress2 = (String) custBillTo.get("cust_addr2");
					String generalCity = (String) custBillTo.get("cust_city");
					String generalStateProvinceGeoId = (String) custBillTo.get("cust_state");
					String generalPostalCode = (String) custBillTo.get("cust_zip");
					String addressValidInd = (String) custBillTo.get("cust_addr_valid");
					String generalCountryGeoId = (String) custBillTo.get("cust_country");
					
					Map<String, Object> context1 = new HashMap<String, Object>();
					
					if(UtilValidate.isNotEmpty(primaryPhoneNumber)) {
						context1.put("primaryPhoneNumber", primaryPhoneNumber);
						context1.put("phonePurposeTypeId", "PHONE_BILLING");
					}
					context1.put("generalAddress1", generalAddress1);
					context1.put("generalAddress2", UtilValidate.isNotEmpty(generalAddress2) ? generalAddress2 : "");
					context1.put("generalCity", generalCity);
					context1.put("generalStateProvinceGeoId", generalStateProvinceGeoId);
					context1.put("generalPostalCode", generalPostalCode);
					context1.put("generalCountryGeoId", UtilValidate.isNotEmpty(generalCountryGeoId) ? generalCountryGeoId : "USA");
					context1.put("addressValidInd", addressValidInd);
					context1.put("postalPurposeTypeId", "BILLING_LOCATION");
					context1.put("userLogin", userLogin);
					
					ModelService service = dctx.getModelService("crmsfa.createBasicContactInfoForParty");
					input = service.makeValid(context1, "IN");
					input.put("partyId", partyId);
					serviceResults = dispatcher.runSync(service.name, input);
					
					if(!ServiceUtil.isSuccess(serviceResults)) {
						return serviceResults;
					} else {
						String generalAddressContactMechId = (String) serviceResults.get("generalAddressContactMechId");
						
						String phoneContactMechId = (String) serviceResults.get("primaryPhoneContactMechId");
						if(UtilValidate.isNotEmpty(phoneContactMechId)) {
							GenericValue postalAdd = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId", generalAddressContactMechId).queryFirst();
							if(UtilValidate.isNotEmpty(postalAdd)) {
								postalAdd.set("phoneContactMechId", phoneContactMechId);
								postalAdd.store();
							}
						}
						input = UtilMisc. < String, Object > toMap("partyId", partyId, "userLogin", userLogin, "contactMechId", generalAddressContactMechId, "contactMechPurposeTypeId", "PRIMARY_LOCATION");
						serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);
						if (ServiceUtil.isError(serviceResults)) {
							return serviceResults;
						}
					}
				}
			}
			
			List<Map<String, Object>> custCustomDataList = UtilValidate.isNotEmpty(requestContext.get("custCustomData")) ? (List<Map<String, Object>>) requestContext.get("custCustomData") : new ArrayList<Map<String,Object>>();
			if(UtilValidate.isNotEmpty(custCustomDataList)) {
				Optional<Map<String, Object>> isexists = custCustomDataList.stream().filter(m -> m.get("param_name") != null && "OFFER_PAGE".equals(m.get("param_name"))).findFirst();
				if(!isexists.isPresent()) {
					String offerUrl = EntityUtilProperties.getPropertyValue("offer", "offer-parge-url", delegator);
					if(UtilValidate.isNotEmpty(offerUrl)) {
						Map<String, Object> offerPage = new HashMap<String, Object>();
						offerPage.put("param_name", "OFFER_PAGE");
						offerPage.put("param_value", offerUrl);
						custCustomDataList.add(offerPage);
					}
				}
			} else {
				String offerUrl = EntityUtilProperties.getPropertyValue("offer", "offer-parge-url", delegator);
				if(UtilValidate.isNotEmpty(offerUrl)) {
					Map<String, Object> offerPage = new HashMap<String, Object>();
					offerPage.put("param_name", "OFFER_PAGE");
					offerPage.put("param_value", offerUrl);
					custCustomDataList.add(offerPage);
				}
			}
			if(UtilValidate.isNotEmpty(custCustomDataList)) {
				for(Map<String, Object> custCustomData: custCustomDataList) {
					String paramName = (String) custCustomData.get("param_name");
					String paramValue = (String) custCustomData.get("param_value");
					
					if("OFFER_PAGE".equals(paramName) && UtilValidate.isEmpty(paramValue)) {
						String offerUrl = EntityUtilProperties.getPropertyValue("offer", "offer-parge-url", delegator);
						paramValue = offerUrl;
					}
						
					if(UtilValidate.isEmpty(paramValue))
						continue;
					
					GenericValue customFieldValueGv = EntityQuery.use(delegator).from("CustomFieldValue").where("customFieldId", paramName, "partyId", partyId).queryFirst();
					if(UtilValidate.isNotEmpty(customFieldValueGv)) {
						customFieldValueGv.set("fieldValue", paramValue);
						customFieldValueGv.store();
					} else {
						customFieldValueGv = delegator.makeValue("CustomFieldValue", UtilMisc.toMap("customFieldId", paramName, "partyId", partyId));
						customFieldValueGv.set("fieldValue", paramValue);
						customFieldValueGv.set("fromDate", UtilDateTime.nowTimestamp());
						customFieldValueGv.create();
					}
					
				}
			}
			
			
			List<Map<String, Object>> custSocialRefList = (List<Map<String, Object>>) requestContext.get("custSocialRef");
			if(UtilValidate.isNotEmpty(custSocialRefList)) {
				
				for(Map<String, Object> custSocialRef: custSocialRefList) {
					String refName = (String) custSocialRef.get("ref_name");
					String refValue = (String) custSocialRef.get("ref_val");
					Map<String, Object> context1 = new HashMap<String, Object>();
					context1.put("userLogin", userLogin);
					context1.put("partyId", partyId);
					context1.put("contactMechTypeId", "SOCIAL_MEDIA_TYPE");
					EntityCondition contactMechCond = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "SOCIAL_MEDIA_TYPE"),
							EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.LIKE, EntityFunction.UPPER("%_" + refName))
							);
					GenericValue contactMechTypePurpose = EntityQuery.use(delegator).from("ContactMechTypePurpose").where(contactMechCond).queryFirst();
					if(UtilValidate.isNotEmpty(contactMechTypePurpose)) {
						String contactMechPurposeTypeId = contactMechTypePurpose.getString("contactMechPurposeTypeId");
						context1.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
						context1.put("socialMediaId", refValue);
						context1.put("allowSolicitation", "Y");
						serviceResults = dispatcher.runSync("createPartySocialMediaType", context1);
					}
				}
			}
			
			
			
			List<GenericValue> partyIdentifyList = EntityQuery.use(delegator).from("PartyIdentification").where("partyId", partyId).queryList();
			List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
			if(UtilValidate.isNotEmpty(partyIdentifyList)) {
				for(GenericValue partyIdentify : partyIdentifyList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("alt_ref_name", partyIdentify.getString("partyIdentificationTypeId"));
					data.put("alt_ref_id", partyIdentify.getString("idValue"));
					dataList.add(data);
				}
			}
			result.put("partyId", partyId);
			result.put("custRef", dataList);
			
			String isOtpEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "is-otp-process-enabled", "N");
			if("Y".equals(isOtpEnabled) && UtilValidate.isNotEmpty(partyId)) {
				if(UtilValidate.isNotEmpty(party)){
					party.put("statusId", "PARTY_DISABLED");
					party.store();
				}
				
				Map<String, Object> result1 = dispatcher.runSync("cs.verifyCustomerV3", UtilMisc.toMap("userLogin",  userLogin, "emailId", custPrimaryEmail, "mode", "VERIFICATION", "hashCode", "", "partyId", partyId ));
				if(!ServiceUtil.isSuccess(result1)) {
					Debug.logError("Error : "+ ServiceUtil.getErrorMessage(result1), MODULE);
				}
			}
			String isRequiredWelcomeEmail = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "REQUIRED_CUSTOMER_WELCOME_EMAIL", "N");
			if("Y".equals(isRequiredWelcomeEmail) && UtilValidate.isNotEmpty(partyId)) {
				String emailTemplateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUSTOMER_ACK_EMAIL");
				if(UtilValidate.isNotEmpty(emailTemplateId)) {
					Map<String, Object> result1 = dispatcher.runSync("cs.sendEmailWithTemplate", UtilMisc.toMap("userLogin", userLogin, "requestContext", UtilMisc.toMap("partyId", partyId,"emailId", custPrimaryEmail, "partyIdTo", partyId,"emailTemplateId", emailTemplateId)));
					
					if(!ServiceUtil.isSuccess(result1)) {
						Debug.logError("Error in sendEmailWithTemplate : "+ ServiceUtil.getErrorMessage(result1), MODULE);
					}
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
	public static Map<String, Object> updateCustomer(DispatchContext dctx, Map<String, Object> context) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	
    	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		NumberFormat nf = NumberFormat.getInstance(locale);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
		String custId = (String) requestContext.get("custId");

		String custFullName = (String) requestContext.get("custFullName");
		String custFirstName = (String) requestContext.get("custFirstName");
		String custLastName = (String) requestContext.get("custLastName");
		
		String custPrimaryPhone = (String) requestContext.get("custPrimPhone");
		String custLocName = (String) requestContext.get("custLocName");
		String custSourceDateStr = (String) requestContext.get("custSourceDate");
		String roleTypeId = (String) requestContext.get("custRoleType");
		String custPrimaryEmail = (String) requestContext.get("custPrimEmail"); 
		String dataSourceId = (String) requestContext.get("source"); 
		
		boolean beganTransaction = false;
		
		try {
			String ownerId = UtilValidate.isNotEmpty(context.get("owenerId")) ? (String) context.get("ownerId") : userLogin.getString("userLoginId");
			LocalDateTime custSourceDate = LocalDateTime.now();
			if(UtilValidate.isNotEmpty(custSourceDateStr) && DataUtil.isDate(custSourceDateStr, "timestamp"))
				custSourceDate = LocalDateTime.parse(custSourceDateStr);
			
			if(UtilValidate.isNotEmpty(custFullName)) {
				custFirstName = custFullName; 
				custLastName ="";
			}
			
			beganTransaction = TransactionUtil.begin();
			
			//Create Customer(ACCOUNT)
			String partyId = UtilValidate.isNotEmpty(requestContext.get("partyId")) ? (String) requestContext.get("partyId") : "" ;
			if(UtilValidate.isNotEmpty(custId)) {
				GenericValue partyIdentificationGv = EntityQuery.use(delegator).from("PartyIdentification").where("partyIdentificationTypeId", "WW_CUST","idValue",custId).queryFirst();
				if(UtilValidate.isNotEmpty(partyIdentificationGv)) {
					partyId = partyIdentificationGv.getString("partyId");
				}
			}
			
			EntityCondition partyCondition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
						EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"),
									EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)
								)
					);
			
			GenericValue party1 = EntityQuery.use(delegator).from("Party").where(partyCondition).queryFirst();
			if(UtilValidate.isEmpty(party1)) {
				partyId = "";
			}
			// update the Party and PartyGroup
			
			if(UtilValidate.isNotEmpty(partyId)) {
				roleTypeId = UtilValidate.isNotEmpty(roleTypeId) ? roleTypeId : UtilValidate.isNotEmpty(party1.getString("roleTypeId")) ? party1.getString("roleTypeId") : "";
				Map<String, Object> serviceResults = new HashMap<String, Object>();
				if("CUSTOMER".equals(roleTypeId)){
					Map<String, Object> input = UtilMisc.toMap("firstName", custFirstName, "partyId", partyId);
					if(UtilValidate.isNotEmpty(custLastName))
						input.put("lastName", custLastName);
					
					input.put("userLogin", userLogin);
					serviceResults = dispatcher.runSync("updatePerson", input);
					if (ServiceUtil.isError(serviceResults)) {
						return serviceResults;
					}
				}
				else if("ACCOUNT".equals(roleTypeId)) {
					Map<String, Object> input = UtilMisc.toMap("groupName", custFirstName, "partyId", partyId);
					input.put("userLogin", userLogin);
					serviceResults = dispatcher.runSync("updatePartyGroup", input);
					if (ServiceUtil.isError(serviceResults)) {
						return serviceResults;
					}
					
					// update PartySupplementalData
					GenericValue partyData = delegator.findOne("PartySupplementalData", UtilMisc.toMap("partyId", partyId),false);
					if (partyData == null) {
						// create a new one
						partyData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", partyId));
						partyData.create();
					}
					partyData.setNonPKFields(context);
					partyData.store();
				}
				
				GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
				party.put("lastModifiedDate", UtilDateTime.nowTimestamp());
				party.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
				if(UtilValidate.isNotEmpty(context.get("timeZoneDesc"))){
					party.put("timeZoneDesc", context.get("timeZoneDesc"));
				}
				if(UtilValidate.isNotEmpty(dataSourceId))
					party.put("dataSourceId", dataSourceId);
				party.put("createdDate", UtilValidate.isNotEmpty(custSourceDate) ? Timestamp.valueOf(custSourceDate): UtilDateTime.nowTimestamp());
				party.put("externalId", custId);
				party.store();
				
				if(UtilValidate.isNotEmpty(dataSourceId)) {
					EntityCondition dataSrcCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
								EntityCondition.makeCondition("dataSourceId", EntityOperator.EQUALS, dataSourceId)
							);
					GenericValue partyDataSource = EntityQuery.use(delegator).from("PartyDataSource").where(dataSrcCondition).queryFirst();
					if(UtilValidate.isEmpty(partyDataSource)) {
						partyDataSource = delegator.makeValue("PartyDataSource", UtilMisc.toMap("partyId", partyId, "dataSourceId", dataSourceId, "fromDate", UtilDateTime.nowTimestamp()));
						partyDataSource.create();
					}
					
				}
				
				//update owner roletype/ownerbu/empteam in  party table
				dispatcher.runSync("ap.updateSecurityPartyInfo", UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId"), "ownerId",ownerId, "partyId", partyId ));
				
				// create party attribute
				if(UtilValidate.isNotEmpty(custLocName))
					//DataHelper.createPartyAttribute(delegator, UtilMisc.toMap("partyId", partyId, "attrName", "CUST_LOC_NAME", "attrValue", custLocName));
				org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, custLocName, "CUST_LOC_NAME");

				
				List<GenericValue> partyPrimPhoneList = DataHelper.getPrimaryContactInfoByType(delegator, partyId, "PRIMARY_PHONE");
				List<String> primPhonContactMechList = EntityUtil.getFieldListFromEntityList(partyPrimPhoneList, "contactMechId", true);
				//create primary contact 
				String primartyContactMechId = "";
				if(UtilValidate.isNotEmpty(custPrimaryPhone)) {
					custPrimaryPhone = custPrimaryPhone.replaceAll("-", "");
					EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("contactMechId", EntityOperator.IN, primPhonContactMechList),
							EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, custPrimaryPhone)
							);
					GenericValue primartyPhone = EntityQuery.use(delegator).from("TelecomNumber").where(condition).queryFirst();
					
					if(UtilValidate.isNotEmpty(primartyPhone)) {
						primartyContactMechId = primartyPhone.getString("contactMechId");
					} else {
						if(UtilValidate.isNotEmpty(primPhonContactMechList)) {
	            			for(String primPhoConId : primPhonContactMechList) {
								Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",primPhoConId,"userLogin",userLogin));
								if(ServiceUtil.isSuccess(deletePartyContactMech)){
									Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
								}
							}
	            		}
					}
					
					Map<String, Object> telecomNumber = UtilMisc.toMap("partyId", partyId, "userLogin", (Object) userLogin, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PRIMARY_PHONE");
	            	telecomNumber.put("contactNumber", custPrimaryPhone);
	            	
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
							Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",primPhoConId,"userLogin",userLogin));
							if(ServiceUtil.isSuccess(deletePartyContactMech)){
								Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
							}
						}
					}
				}
				
				//primary email
				List<GenericValue> partyPrimEmailList = DataHelper.getPrimaryContactInfoByType(delegator, partyId, "PRIMARY_EMAIL");
				List<String> primEmailContactMechList = EntityUtil.getFieldListFromEntityList(partyPrimEmailList, "contactMechId", true);
				
				//create primary contact 
				String primartyEmailContactMechId = "";
				if(UtilValidate.isNotEmpty(custPrimaryEmail)) {
					
					EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("contactMechId", EntityOperator.IN, primEmailContactMechList),
							EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, custPrimaryEmail)
							);
					GenericValue primaryEmail = EntityQuery.use(delegator).from("ContactMech").where(condition).queryFirst();
					
					if(UtilValidate.isNotEmpty(primaryEmail)) {
						primartyEmailContactMechId = primaryEmail.getString("contactMechId");
					} else {
						if(UtilValidate.isNotEmpty(primEmailContactMechList)) {
	            			for(String primEamilConId : primEmailContactMechList) {
								Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",primEamilConId,"userLogin",userLogin));
								if(ServiceUtil.isSuccess(deletePartyContactMech)){
									Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
								}
							}
	            		}
					}
					
					Map<String, Object> emailAddress = UtilMisc.toMap("partyId", partyId, "userLogin", (Object) userLogin, "contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL");
					emailAddress.put("emailAddress", custPrimaryEmail);
	            	
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
							Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",primEamilConId,"userLogin",userLogin));
							if(ServiceUtil.isSuccess(deletePartyContactMech)){
								Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
							}
						}
					}
				}
				
				
				
				//create cust reference
				List<Map<String, Object>> custRefList = (List<Map<String, Object>>) requestContext.get("custRef");
				if(UtilValidate.isNotEmpty(custRefList)) {
					for(Map<String, Object> custRef : custRefList) {
						String altRefName = (String) custRef.get("alt_ref_name");
						String altRefId = (String) custRef.get("alt_ref_id");
						
						DataHelper.createPartyIdentification(delegator, UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", altRefName, "idValue", altRefId));
					}
				}
				
				
				// create ship contact info
				List<Map<String, Object>> custShipToList = (List<Map<String, Object>>) requestContext.get("custShipTo");
				if(UtilValidate.isNotEmpty(custShipToList)) {
					List<GenericValue> partyShipPhoneList = EntityQuery.use(delegator).from("PartyContactWithPurpose").where("partyId", partyId,"contactMechPurposeTypeId","PHONE_SHIPPING").filterByDate("contactFromDate","contactThruDate","purposeFromDate","purposeThruDate").queryList();
					List<String> shipPhonContactMechList = EntityUtil.getFieldListFromEntityList(partyShipPhoneList, "contactMechId", true);
					
					List<GenericValue> partyShipLocList = EntityQuery.use(delegator).from("PartyContactWithPurpose").where("partyId", partyId,"contactMechPurposeTypeId","SHIPPING_LOCATION").filterByDate("contactFromDate","contactThruDate","purposeFromDate","purposeThruDate").queryList();
					List<String> shipLocContactMechList = EntityUtil.getFieldListFromEntityList(partyShipLocList, "contactMechId", true);
					String requiredFields = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUST_API_SHIP_ADDR_MANDATORY", "cust_addr1,cust_state,cust_zip");
					for(Map<String, Object> custShipTo : custShipToList) {
						if(!DataHelper.validateCustAddressData(DataUtil.stringToList(requiredFields, ","), custShipTo)) 
							break;
												
						String primaryPhoneNumber = (String) custShipTo.get("cust_phone");
						if(UtilValidate.isNotEmpty(primaryPhoneNumber))
							primaryPhoneNumber = primaryPhoneNumber.replaceAll("-", "");
						String generalAddress1 = (String) custShipTo.get("cust_addr1");
						String generalAddress2 = (String) custShipTo.get("cust_addr2");
						String generalCity = (String) custShipTo.get("cust_city");
						String generalStateProvinceGeoId = (String) custShipTo.get("cust_state");
						String generalPostalCode = (String) custShipTo.get("cust_zip");
						String generalCountryGeoId = (String) custShipTo.get("cust_country");
						
						EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("contactMechId", EntityOperator.IN, shipPhonContactMechList),
								EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, primaryPhoneNumber)
								);
						GenericValue shipPhone = EntityQuery.use(delegator).from("TelecomNumber").where(condition).queryFirst();
						String phoneContactMechId = "";
						if(UtilValidate.isNotEmpty(shipPhone)) {
							phoneContactMechId = shipPhone.getString("contactMechId");
						}
						Map<String, Object> telecomNumber = UtilMisc.toMap("partyId", partyId, "userLogin", (Object) userLogin, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PHONE_SHIPPING");
		            	telecomNumber.put("contactNumber", primaryPhoneNumber);
		            	
		            	if(UtilValidate.isEmpty(primaryPhoneNumber) && UtilValidate.isNotEmpty(phoneContactMechId)) {
		            		Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",phoneContactMechId,"userLogin",userLogin));
							if(ServiceUtil.isSuccess(deletePartyContactMech)){
								Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
							}
		            	} else if(UtilValidate.isNotEmpty(primaryPhoneNumber)) {
		            		if(UtilValidate.isEmpty(phoneContactMechId)) {
		            			if(UtilValidate.isNotEmpty(shipPhonContactMechList)) {
			            			for(String shipPhonContactMechId : shipPhonContactMechList) {
										Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",shipPhonContactMechId,"userLogin",userLogin));
										if(ServiceUtil.isSuccess(deletePartyContactMech)){
											Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
										}
									}
			            		}
			            		serviceResults = dispatcher.runSync("createPartyTelecomNumber", telecomNumber);
			            	} else {
			            		telecomNumber.remove("contactMechPurposeTypeId");
			            		telecomNumber.remove("contactMechTypeId");
			            		telecomNumber.put("contactMechId", phoneContactMechId);
			            		serviceResults = dispatcher.runSync("updatePartyTelecomNumber", telecomNumber);
			            	}
			            		
			            	if (ServiceUtil.isError(serviceResults)) {
			            		return serviceResults;
			            	}
			            	phoneContactMechId = (String) serviceResults.get("contactMechId");
		            	}
		            	
						
		            	EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("contactMechId", EntityOperator.IN, shipLocContactMechList),
								EntityCondition.makeCondition("address1", EntityOperator.EQUALS, generalAddress1),
								EntityCondition.makeCondition("postalCode", EntityOperator.EQUALS, generalPostalCode),
								EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, generalStateProvinceGeoId)
								);
						GenericValue shipLoc = EntityQuery.use(delegator).from("PostalAddress").where(condition1).queryFirst();
						String locContactMechId = "";
						if(UtilValidate.isNotEmpty(shipLoc)) {
							locContactMechId = shipLoc.getString("contactMechId");
						}
						Map<String, Object> context1 = new HashMap<String, Object>();
						context1.put("partyId", partyId);
						context1.put("address1", generalAddress1);
						context1.put("address2", generalAddress2);
						context1.put("city", generalCity);
						context1.put("stateProvinceGeoId", generalStateProvinceGeoId);
						context1.put("postalCode", generalPostalCode);
						context1.put("contactMechPurposeTypeId", "SHIPPING_LOCATION");
						context1.put("userLogin", userLogin);
		            	
		            	if(UtilValidate.isEmpty(locContactMechId)) {
		            		if (UtilValidate.isNotEmpty(shipLocContactMechList)) {
		            			for(String shipLocContactMechId : shipLocContactMechList) {
									Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",shipLocContactMechId,"userLogin",userLogin));
									if(ServiceUtil.isSuccess(deletePartyContactMech)){
										Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
									}
								}
		            		}
		            		serviceResults = dispatcher.runSync("createPartyPostalAddress", context1);
		            	} else {
		            		context1.remove("contactMechPurposeTypeId");
		            		context1.put("contactMechId", locContactMechId);
		            		serviceResults = dispatcher.runSync("updatePartyPostalAddress", context1);
		            	}
						if(!ServiceUtil.isSuccess(serviceResults)) {
							return serviceResults;
						}
						if(UtilValidate.isNotEmpty(phoneContactMechId)) {
							locContactMechId = (String) serviceResults.get("contactMechId");
							GenericValue postalAdd = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId", locContactMechId).queryFirst();
							if(UtilValidate.isNotEmpty(postalAdd)) {
								postalAdd.set("phoneContactMechId", phoneContactMechId);
								postalAdd.store();
							}
						}
					}
				}
				
				// create bill contact info
				List<Map<String, Object>> custBillToList = (List<Map<String, Object>>) requestContext.get("custBillTo");
				if(UtilValidate.isNotEmpty(custBillToList)) {
					List<GenericValue> partyBillPhoneList = EntityQuery.use(delegator).from("PartyContactWithPurpose").where("partyId", partyId,"contactMechPurposeTypeId","PHONE_BILLING").filterByDate("contactFromDate","contactThruDate","purposeFromDate","purposeThruDate").queryList();
					List<String> billPhoneContactMechList = EntityUtil.getFieldListFromEntityList(partyBillPhoneList, "contactMechId", true);
					List<GenericValue> partyBillLocList = EntityQuery.use(delegator).from("PartyContactWithPurpose").where("partyId", partyId,"contactMechPurposeTypeId","BILLING_LOCATION").filterByDate("contactFromDate","contactThruDate","purposeFromDate","purposeThruDate").queryList();
					List<String> billLocContactMechList = EntityUtil.getFieldListFromEntityList(partyBillLocList, "contactMechId", true);
					String requiredFields = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUST_API_BILL_ADDR_MANDATORY", "cust_addr1,cust_state,cust_zip");
					for(Map<String, Object> custBillTo : custBillToList) {
						if(!DataHelper.validateCustAddressData(DataUtil.stringToList(requiredFields, ","), custBillTo)) 
							break;
												
						String primaryPhoneNumber = (String) custBillTo.get("cust_phone");
						if(UtilValidate.isNotEmpty(primaryPhoneNumber))
							primaryPhoneNumber = primaryPhoneNumber.replaceAll("-", "");
						String generalAddress1 = (String) custBillTo.get("cust_addr1");
						String generalAddress2 = (String) custBillTo.get("cust_addr2");
						String generalCity = (String) custBillTo.get("cust_city");
						String generalStateProvinceGeoId = (String) custBillTo.get("cust_state");
						String generalPostalCode = (String) custBillTo.get("cust_zip");
						String addressValidInd = (String) custBillTo.get("cust_addr_valid");
						String generalCountryGeoId = (String) custBillTo.get("cust_country");
						
						
						EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("contactMechId", EntityOperator.IN, billPhoneContactMechList),
								EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, primaryPhoneNumber)
								);
						GenericValue billPhone = EntityQuery.use(delegator).from("TelecomNumber").where(condition).queryFirst();
						String phoneContactMechId = "";
						if(UtilValidate.isNotEmpty(billPhone)) {
							phoneContactMechId = billPhone.getString("contactMechId");
						}
						Map<String, Object> telecomNumber = UtilMisc.toMap("partyId", partyId, "userLogin", (Object) userLogin, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PHONE_BILLING");
		            	telecomNumber.put("contactNumber", primaryPhoneNumber);
		            	
		            	if(UtilValidate.isEmpty(primaryPhoneNumber) && UtilValidate.isNotEmpty(phoneContactMechId)) {
		            		Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",phoneContactMechId,"userLogin",userLogin));
							if(ServiceUtil.isSuccess(deletePartyContactMech)){
								Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
							}
		            	} else if(UtilValidate.isNotEmpty(primaryPhoneNumber)) {
		            		if(UtilValidate.isEmpty(phoneContactMechId)) {
			            		serviceResults = dispatcher.runSync("createPartyTelecomNumber", telecomNumber);
			            	} else {
			            		telecomNumber.remove("contactMechPurposeTypeId");
			            		telecomNumber.remove("contactMechTypeId");
			            		telecomNumber.put("contactMechId", phoneContactMechId);
			            		serviceResults = dispatcher.runSync("updatePartyTelecomNumber", telecomNumber);
			            	}
			            		
			            	if (ServiceUtil.isError(serviceResults)) {
			            		return serviceResults;
			            	}
			            	phoneContactMechId = (String) serviceResults.get("contactMechId");
			            }
		            	
		            	EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("contactMechId", EntityOperator.IN, billLocContactMechList),
								EntityCondition.makeCondition("address1", EntityOperator.EQUALS, generalAddress1),
								EntityCondition.makeCondition("postalCode", EntityOperator.EQUALS, generalPostalCode),
								EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, generalStateProvinceGeoId)
								);
						GenericValue billLoc = EntityQuery.use(delegator).from("PostalAddress").where(condition1).queryFirst();
						String locContactMechId = "";
						if(UtilValidate.isNotEmpty(billLoc)) {
							locContactMechId = billLoc.getString("contactMechId");
						}
						Map<String, Object> context1 = new HashMap<String, Object>();
						context1.put("partyId", partyId);
						context1.put("address1", generalAddress1);
						context1.put("address2", generalAddress2);
						context1.put("city", generalCity);
						context1.put("stateProvinceGeoId", generalStateProvinceGeoId);
						context1.put("postalCode", generalPostalCode);
						context1.put("contactMechPurposeTypeId", "BILLING_LOCATION");
						context1.put("addressValidInd", addressValidInd);
						context1.put("userLogin", userLogin);
		            	
		            	if(UtilValidate.isEmpty(locContactMechId)) {
		            		if (UtilValidate.isNotEmpty(billLocContactMechList)) {
		            			for(String billLocContactMechId : billLocContactMechList) {
									Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",billLocContactMechId,"userLogin",userLogin));
									if(ServiceUtil.isSuccess(deletePartyContactMech)){
										Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
									}
								}
		            		}
		            		serviceResults = dispatcher.runSync("createPartyPostalAddress", context1);
		            		if(!ServiceUtil.isSuccess(serviceResults)) {
								return serviceResults;
							} else {
								String generalAddressContactMechId = (String) serviceResults.get("contactMechId");
								Map<String, Object> input = UtilMisc. < String, Object > toMap("partyId", partyId, "userLogin", userLogin, "contactMechId", generalAddressContactMechId, "contactMechPurposeTypeId", "PRIMARY_LOCATION");
								serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);
								if (ServiceUtil.isError(serviceResults)) {
									return serviceResults;
								}
							}
		            	} else {
		            		context1.remove("contactMechPurposeTypeId");
		            		context1.put("contactMechId", locContactMechId);
		            		if(UtilValidate.isNotEmpty(addressValidInd) && "N".equals(addressValidInd))
		            			context1.put("thruDate", UtilDateTime.nowTimestamp());
		            		serviceResults = dispatcher.runSync("updatePartyPostalAddress", context1);
		            	}
		            	
						if(!ServiceUtil.isSuccess(serviceResults)) {
							return serviceResults;
						}
						
						if(UtilValidate.isNotEmpty(phoneContactMechId)) {
							locContactMechId = (String) serviceResults.get("contactMechId");
							GenericValue postalAdd = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId", locContactMechId).queryFirst();
							if(UtilValidate.isNotEmpty(postalAdd)) {
								postalAdd.set("phoneContactMechId", phoneContactMechId);
								postalAdd.store();
							}
						}
						
					}
				}
				
				List<Map<String, Object>> custCustomDataList = UtilValidate.isNotEmpty(requestContext.get("custCustomData")) ? (List<Map<String, Object>>) requestContext.get("custCustomData") : new ArrayList<Map<String,Object>>();
				if(UtilValidate.isNotEmpty(custCustomDataList)) {
					Optional<Map<String, Object>> isexists = custCustomDataList.stream().filter(m -> m.get("param_name") != null && "OFFER_PAGE".equals(m.get("param_name"))).findFirst();
					if(!isexists.isPresent()) {
						String offerUrl = EntityUtilProperties.getPropertyValue("offer", "offer-parge-url", delegator);
						if(UtilValidate.isNotEmpty(offerUrl)) {
							Map<String, Object> offerPage = new HashMap<String, Object>();
							offerPage.put("param_name", "OFFER_PAGE");
							offerPage.put("param_value", offerUrl);
							custCustomDataList.add(offerPage);
						}
					}
				} else {
					String offerUrl = EntityUtilProperties.getPropertyValue("offer", "offer-parge-url", delegator);
					if(UtilValidate.isNotEmpty(offerUrl)) {
						Map<String, Object> offerPage = new HashMap<String, Object>();
						offerPage.put("param_name", "OFFER_PAGE");
						offerPage.put("param_value", offerUrl);
						custCustomDataList.add(offerPage);
					}
				}
				if(UtilValidate.isNotEmpty(custCustomDataList)) {
					for(Map<String, Object> custCustomData: custCustomDataList) {
						String paramName = (String) custCustomData.get("param_name");
						String paramValue = (String) custCustomData.get("param_value");
						
						if("OFFER_PAGE".equals(paramName) && UtilValidate.isEmpty(paramValue)) {
							String offerUrl = EntityUtilProperties.getPropertyValue("offer", "offer-parge-url", delegator);
							paramValue = offerUrl;
						}
							
						if(UtilValidate.isEmpty(paramValue))
							continue;
						
						GenericValue customFieldValueGv = EntityQuery.use(delegator).from("CustomFieldValue").where("customFieldId", paramName, "partyId", partyId).queryFirst();
						if(UtilValidate.isNotEmpty(customFieldValueGv)) {
							customFieldValueGv.set("fieldValue", paramValue);
							customFieldValueGv.store();
						} else {
							customFieldValueGv = delegator.makeValue("CustomFieldValue", UtilMisc.toMap("customFieldId", paramName, "partyId", partyId));
							customFieldValueGv.set("fieldValue", paramValue);
							customFieldValueGv.set("fromDate", UtilDateTime.nowTimestamp());
							customFieldValueGv.create();
						}
						
					}
				}
				
				List<Map<String, Object>> custSocialRefList = (List<Map<String, Object>>) requestContext.get("custSocialRef");
				if(UtilValidate.isNotEmpty(custSocialRefList)) {
					
					for(Map<String, Object> custSocialRef: custSocialRefList) {
						String refName = (String) custSocialRef.get("ref_name");
						String refValue = (String) custSocialRef.get("ref_val");
						if(UtilValidate.isNotEmpty(refName)) {
							EntityCondition socialContactCond = EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
									EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.LIKE, EntityFunction.UPPER("SOCIAL_" + refName)));
							
							GenericValue socialContactList = EntityQuery.use(delegator).from("PartyContactWithPurpose").where(socialContactCond).filterByDate("contactFromDate","contactThruDate","purposeFromDate","purposeThruDate").queryFirst();
							if(UtilValidate.isNotEmpty(socialContactList)) {
								String socialContactMechId = socialContactList.getString("contactMechId");
								Map<String, Object> socialMediaUpdateMap = new HashMap<String, Object>();
				    			socialMediaUpdateMap.put("partyId", partyId);
				    			socialMediaUpdateMap.put("contactMechId", socialContactMechId);
				    			socialMediaUpdateMap.put("socialMediaId", refValue);
				    			socialMediaUpdateMap.put("allowSolicitation", "Y");
				    			socialMediaUpdateMap.put("userLogin", userLogin);
				    			serviceResults = dispatcher.runSync("updatePartySocialMediaType", socialMediaUpdateMap);	
								
							} else {
								Map<String, Object> context1 = new HashMap<String, Object>();
								context1.put("userLogin", userLogin);
								context1.put("partyId", partyId);
								context1.put("contactMechTypeId", "SOCIAL_MEDIA_TYPE");
								EntityCondition contactMechCond = EntityCondition.makeCondition(EntityOperator.AND,
										EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "SOCIAL_MEDIA_TYPE"),
										EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.LIKE, EntityFunction.UPPER("SOCIAL_" + refName))
										);
								GenericValue contactMechTypePurpose = EntityQuery.use(delegator).from("ContactMechTypePurpose").where(contactMechCond).queryFirst();
								if(UtilValidate.isNotEmpty(contactMechTypePurpose)) {
									String contactMechPurposeTypeId = contactMechTypePurpose.getString("contactMechPurposeTypeId");
									context1.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
									context1.put("socialMediaId", refValue);
									context1.put("allowSolicitation", "Y");
									serviceResults = dispatcher.runSync("createPartySocialMediaType", context1);
								}
							}
						}
					}
				}
			}
			
			TransactionUtil.commit(beganTransaction);
			
			List<GenericValue> partyIdentifyList = EntityQuery.use(delegator).from("PartyIdentification").where("partyId", partyId).queryList();
			List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
			if(UtilValidate.isNotEmpty(partyIdentifyList)) {
				for(GenericValue partyIdentify : partyIdentifyList) {
					Map<String, Object> custRef = new HashMap<String, Object>();
					custRef.put("alt_ref_name" ,partyIdentify.getString("partyIdentificationTypeId"));
					custRef.put("alt_ref_id", partyIdentify.getString("idValue"));
					dataList.add(custRef);
				}
			}
			result.put("custRef", dataList);
			
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
	public static Map<String, Object> findCustomer(DispatchContext dctx, Map<String, Object> context) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	
    	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		NumberFormat nf = NumberFormat.getInstance(locale);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		boolean beganTransaction = false;
		
		try {
			
			Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
			String custId = (String) requestContext.get("custId");
			String custFullName = (String) requestContext.get("custFullName");
			String custFirstName = (String) requestContext.get("custFirstName");
			String custLastName = (String) requestContext.get("custLastName");
			
			String custPrimaryPhone = (String) requestContext.get("custPrimPhone");
			String custLocName = (String) requestContext.get("custLocName");
			String custSourceStDateStr = (String) requestContext.get("custSourceStDate");
			String custSourceEdDateStr = (String) requestContext.get("custSourceEdDate");
			String custPrimaryEmail = (String) requestContext.get("custPrimEmail");
			
			String pageSizeStr = ""; //findCustomer.getPaginationPageSize();
			String nextPageNumStr = (String) requestContext.get("nextPageNum");
			List<Map<String, Object>> custRefList = (List<Map<String, Object>>) requestContext.get("custRef");
			
			int pageSize = UtilValidate.isNotEmpty(pageSizeStr) && DataUtil.isInteger(pageSizeStr) ? Integer.parseInt(pageSizeStr) : 5;
			int nextPageNum = UtilValidate.isNotEmpty(nextPageNumStr) && DataUtil.isInteger(nextPageNumStr) ? Integer.parseInt(nextPageNumStr) : 1;
			
			
			String ownerId = UtilValidate.isNotEmpty(context.get("owenerId")) ? (String) context.get("ownerId") : userLogin.getString("userLoginId");
			
			beganTransaction = TransactionUtil.begin();
			
			List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
			
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PI", "PartyIdentification");
			dynamicView.addAlias("PI", "partyId","partyId", "", false, true, null);
			dynamicView.addAlias("PI", "custId","idValue", "", false, true, null);
			dynamicView.addAlias("PI", "identificationTypeId","partyIdentificationTypeId", "", false, true, null);
			dynamicView.addMemberEntity("P", "Party");
			dynamicView.addAlias("P", "statusId");
			dynamicView.addAlias("P", "createdDate");
			dynamicView.addViewLink("PI", "P", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
			dynamicView.addMemberEntity("PG", "PartyGroup");
			dynamicView.addAlias("PG", "groupName");
			dynamicView.addViewLink("PI", "PG", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));

			dynamicView.addMemberEntity("PER", "Person");
			dynamicView.addAlias("PER", "firstName");
			dynamicView.addAlias("PER", "middleName");
			dynamicView.addAlias("PER", "lastName");
			dynamicView.addViewLink("PI", "PER", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
			
			conditionList.add(EntityCondition.makeCondition("identificationTypeId", EntityOperator.EQUALS, "WW_CUST"));
			conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)
					));
			if(UtilValidate.isNotEmpty(custId)) {
				conditionList.add(EntityCondition.makeCondition("custId", EntityOperator.EQUALS, custId));
			} 
			
			if(UtilValidate.isNotEmpty(custRefList)) {
				int count = 0;
				for(Map<String, Object> custRef : custRefList) {
					String altRefName = (String) custRef.get("alt_ref_name");
					String altRefId = (String) custRef.get("alt_ref_id");
					if(UtilValidate.isNotEmpty(altRefName) && UtilValidate.isNotEmpty(altRefId)) {
						dynamicView.addMemberEntity("PI"+count, "PartyIdentification");
						//dynamicView.addAlias("PI"+count, "partyId","partyId", "", false, false, null);
						dynamicView.addAlias("PI"+count, "idValue"+count,"idValue", "", false, false, null);
						dynamicView.addAlias("PI"+count, "identificationTypeId"+count,"partyIdentificationTypeId", "", false, false, null);
						dynamicView.addViewLink("PI", "PI"+count, Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
						
						conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("identificationTypeId"+count, EntityOperator.EQUALS, altRefName),
								EntityCondition.makeCondition("idValue"+count, EntityOperator.EQUALS, altRefId)
								));
					}
					count = count+1;
				}
			}
			
			
			
			if(UtilValidate.isNotEmpty(custFullName)) {
				conditionList.add(EntityCondition.makeCondition("groupName", EntityOperator.EQUALS, custFullName));
			}
			if(UtilValidate.isNotEmpty(custFirstName))
				conditionList.add(EntityCondition.makeCondition("firstName", EntityOperator.EQUALS, custFirstName));
			
			if(UtilValidate.isNotEmpty(custLastName))
				conditionList.add(EntityCondition.makeCondition("lastName", EntityOperator.EQUALS, custLastName));
			
			
			if(UtilValidate.isNotEmpty(custSourceStDateStr)  && DataUtil.isDate(custSourceEdDateStr, "timestamp")) {
				LocalDateTime custSrcStartDate = LocalDateTime.parse(custSourceStDateStr);
				conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, Timestamp.valueOf(custSrcStartDate)));
				if(UtilValidate.isNotEmpty(custSourceEdDateStr) && DataUtil.isDate(custSourceEdDateStr, "timestamp")) {
					LocalDateTime custSrcEndDate = LocalDateTime.parse(custSourceEdDateStr);
					conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, Timestamp.valueOf(custSrcEndDate)));
				}
				
			} else {
				if(UtilValidate.isNotEmpty(custSourceEdDateStr) && DataUtil.isDate(custSourceEdDateStr, "timestamp")) {
					LocalDateTime custSrcEndDate = LocalDateTime.parse(custSourceEdDateStr);
					conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, Timestamp.valueOf(custSrcEndDate)));
				}
			}
			
			
			if(UtilValidate.isNotEmpty(custLocName)) {
				dynamicView.addMemberEntity("PI", "PartyIdentification");
				dynamicView.addAlias("PI", "partyIdentificationTypeId");
				dynamicView.addAlias("PI", "idValue");
				dynamicView.addViewLink("P", "PI", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CUST_LOC_NAME"),
						EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, custLocName)
						));
			}
			
			if(UtilValidate.isNotEmpty(custPrimaryPhone)) {
				custPrimaryPhone = custPrimaryPhone.replaceAll("-", "");
				dynamicView.addMemberEntity("PCM", "PartyContactMech");
				dynamicView.addAlias("PCM", "contactMechId");
				dynamicView.addAlias("PCM", "pcmFromDate", "fromDate","",false, false, null);
				dynamicView.addAlias("PCM", "pcmThruDate", "thruDate","",false, false, null);
				dynamicView.addViewLink("PI", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
				dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
				dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
				dynamicView.addAlias("PCMP", "pcmpFromDate", "fromDate","",false, false, null);
				dynamicView.addAlias("PCMP", "pcmpThruDate", "thruDate","",false, false, null);
				dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
				dynamicView.addMemberEntity("TN", "TelecomNumber");
				dynamicView.addAlias("TN", "contactNumber");
				dynamicView.addViewLink("PCM", "TN", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE"),
						EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, custPrimaryPhone),
						EntityUtil.getFilterByDateExpr("pcmFromDate", "pcmThruDate")
						));
			}
			if(UtilValidate.isNotEmpty(custPrimaryEmail)) {
				dynamicView.addMemberEntity("PCM1", "PartyContactMech");
				dynamicView.addAlias("PCM1", "contactMechId");
				dynamicView.addAlias("PCM1", "pcmFromDate", "fromDate","",false, false, null);
				dynamicView.addAlias("PCM1", "pcmThruDate", "thruDate","",false, false, null);
				dynamicView.addViewLink("PI", "PCM1", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
				dynamicView.addMemberEntity("PCMP1", "PartyContactMechPurpose");
				dynamicView.addAlias("PCMP1", "contactMechPurposeTypeId");
				dynamicView.addAlias("PCMP1", "pcmpFromDate", "fromDate","",false, false, null);
				dynamicView.addAlias("PCMP1", "pcmpThruDate", "thruDate","",false, false, null);
				dynamicView.addViewLink("PCM1", "PCMP1", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
				dynamicView.addMemberEntity("CM1", "ContactMech");
				dynamicView.addAlias("CM1", "infoString");
				dynamicView.addViewLink("PCM1", "CM1", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL"),
						EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, custPrimaryEmail),
						EntityUtil.getFilterByDateExpr("pcmFromDate", "pcmThruDate")
						));
			}
			
			
			
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			
			List<GenericValue> resultList = new ArrayList<>();	
			int highIndex = 0;
            int lowIndex = 0;
            int currentPage = 0;
            int resultListSize = 0;
            try {
            	currentPage = nextPageNum>0 ? nextPageNum-1 : 0;
                // get the indexes for the partial list
            	lowIndex = (currentPage * pageSize) + 1;
                highIndex = (currentPage + 1) * pageSize;
                
                // set distinct on so we only get one row per order
                // using list iterator
                EntityListIterator pli = EntityQuery.use(delegator)
                        .from(dynamicView)
                        .where(condition)
                        .cursorScrollInsensitive()
                        .fetchSize(highIndex)
                        //.distinct()
                        .cache(true)
                        .queryIterator();
                // get the partial list for this page
                resultList = pli.getPartialList(lowIndex, pageSize);

                // attempt to get the full size
                resultListSize = pli.getResultsSizeAfterPartialList();
                // close the list iterator
                pli.close();
		        
            } catch (GenericEntityException e) {
                String errMsg = "Error: " + e.toString();
                Debug.logError(e, errMsg, MODULE);
            }
            int totalPage = 0;
			int totalRecord = 0;
			int currPage = 0;
            List<Map<String, Object>> result1 = new LinkedList<Map<String,Object>>();
			if(UtilValidate.isNotEmpty(resultList)) {
				Map<String, Object> custParentNames = DataHelper.getCustParentNameList(delegator, resultList, "partyId");
				Map<String, Object> custPrimPhones = DataHelper.getCustPrimPhoneList(delegator, resultList, "partyId");
				Map<String, Object> custPartyAttributes = DataHelper.getPartyIdentificationValueList(delegator, resultList, "partyId", "CUST_LOC_NAME");
				
				totalPage = resultListSize/pageSize;
				totalRecord = resultListSize;
				currPage = currentPage;
				
				for (GenericValue customer : resultList) {
	            	Map<String, Object> data = new HashMap<String, Object>();
	            	String partyId = customer.getString("partyId");
	            	String customerId = customer.getString("custId");
	            	String customerName = customer.getString("groupName");;
					if(UtilValidate.isEmpty(customerName)) {
						customerName = customer.getString("firstName") + (UtilValidate.isNotEmpty(customer.getString("lastName")) ? " " + customer.getString("lastName") : "");
					}
	            	//String parentCustName = UtilValidate.isNotEmpty(custParentNames) && custParentNames.containsKey(partyId) ? (String) custParentNames.get(partyId) :"";
	            	Timestamp createdDate = customer.getTimestamp("createdDate");
	            	String customerLocName = UtilValidate.isNotEmpty(custPartyAttributes) && custPartyAttributes.containsKey(partyId) ? (String) custPartyAttributes.get(partyId) :"";
	            	String customerPrimPhone = UtilValidate.isNotEmpty(custPrimPhones) && custPrimPhones.containsKey(partyId) ? (String) custPrimPhones.get(partyId) :"";
	            	
	            	data.put("cust_id", customerId);
	            	data.put("cust_name", customerName.trim());
	            	//data.put("cust_parent_name", parentCustName);
	            	data.put("cust_prim_phone", UtilValidate.isNotEmpty(custPrimPhones) ? DataUtil.formatPhoneNumber(customerPrimPhone, "$1-$2-$3") : "" );
	            	data.put("cust_loc_name", customerLocName);
	            	data.put("cust_source_date", UtilValidate.isNotEmpty(createdDate) ? createdDate.toLocalDateTime().toString() : "");
	            	data.put("cust_prim_email", DataHelper.getCustPrimEmail(delegator, partyId));
	            	result1.add(data);
	            }
			}
            result.put("result", result1);
            result.put("totalPage", totalPage+"");
            result.put("totalRecord", totalRecord+"");
            result.put("currPage", currPage+"");
			
			TransactionUtil.commit();
			
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
	
	public Map<String, Object> getCustomerDetails(DispatchContext dctx, Map<String, Object> context) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	
    	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		NumberFormat nf = NumberFormat.getInstance(locale);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String customerId = (String) context.get("customerId");
		try {
			List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
			GenericValue partyIdentification = EntityQuery.use(delegator).from("PartyIdentification").where("idValue", customerId).queryFirst();
			if(UtilValidate.isEmpty(partyIdentification)) {
				return ServiceUtil.returnError("Customer not found!");
			}
			String partyId1 = partyIdentification.getString("partyId");
			GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", partyId1).queryFirst();
			String roleTypeId = UtilValidate.isNotEmpty(party) ? party.getString("roleTypeId") : "";
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PI", "PartyIdentification");
			dynamicView.addAlias("PI", "partyId","partyId", "", false, true, null);
			dynamicView.addAlias("PI", "idValue");
			dynamicView.addAlias("PI", "partyIdentificationTypeId");
			
			dynamicView.addMemberEntity("P", "Party");
			dynamicView.addAlias("P", "createdDate");
			dynamicView.addAlias("P", "statusId");
			dynamicView.addViewLink("PI", "P", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
			
			if("ACCOUNT".equals(roleTypeId)) {
				dynamicView.addMemberEntity("PG", "PartyGroup");
				dynamicView.addAlias("PG", "groupName");
				dynamicView.addViewLink("PI", "PG", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
			}
			
			if("CUSTOMER".equals(roleTypeId)) {
				dynamicView.addMemberEntity("PER", "Person");
				dynamicView.addAlias("PER", "firstName");
				dynamicView.addAlias("PER", "middleName");
				dynamicView.addAlias("PER", "lastName");
				dynamicView.addViewLink("PI", "PER", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
			}
			
			conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "WW_CUST"));
			if(UtilValidate.isNotEmpty(customerId)) {
				conditionList.add(EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, customerId));
			}
			conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)
					));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			
			GenericValue customer = EntityQuery.use(delegator).from(dynamicView).where(condition).queryFirst();
			if(UtilValidate.isNotEmpty(customer)) {
				Map<String, Object> data = new LinkedHashMap<String, Object>();
				String partyId = customer.getString("partyId");
				String name = "";
				if("ACCOUNT".equals(roleTypeId)) {
					name = customer.getString("groupName");
				} else {
					name = customer.getString("firstName") + (UtilValidate.isNotEmpty(customer.getString("lastName")) ? " "+ customer.getString("lastName") : "");
				}
				String custParentName = DataHelper.getParentCustName(delegator, partyId);
				String custPrimPhone = DataHelper.getCustPrimPhone(delegator, partyId);
				String custLocation = org.fio.homeapps.util.DataUtil.getPartyIdentificationValue(delegator, partyId, "CUST_LOC_NAME");

				data.put("cust_id", customerId);
				data.put("cust_name", name);
				data.put("cust_parent_name", custParentName);
				data.put("cust_prim_phone", UtilValidate.isNotEmpty(custPrimPhone) ? DataUtil.formatPhoneNumber(custPrimPhone, "$1-$2-$3") : "");
				data.put("cust_prim_email", DataHelper.getCustPrimEmail(delegator, partyId));
				data.put("cust_loc_name", custLocation);
				Timestamp createdDate =  customer.getTimestamp("createdDate");
				data.put("cust_source_date", UtilValidate.isNotEmpty(createdDate) ? createdDate.toLocalDateTime() : "");
				
				List<Map<String, Object>> custRefList = DataHelper.getCustomerRefList(delegator, partyId,"WW_CUST");
				data.put("cust_ref", custRefList);
				List<Map<String, Object>> socialRefList = DataHelper.getSocialRefList(delegator, partyId, "SOCIAL_MEDIA_TYPE");
				data.put("cust_social_ref", socialRefList);
				List<Map<String, Object>> shipToList = DataHelper.getAddressInfo(delegator, partyId, "SHIPPING_LOCATION");
				data.put("cust_ship_to", shipToList);
				List<Map<String, Object>> billToList = DataHelper.getAddressInfo(delegator, partyId, "BILLING_LOCATION");
				data.put("cust_bill_to", billToList);
				List<Map<String, Object>> custCntList = DataHelper.getCustContactInfo(delegator, partyId, "");
				data.put("cust_cnt", custCntList);
				List<Map<String, Object>> custCustomDataList = DataHelper.getCustCustomData(delegator, partyId, "");
				data.put("cust_custom_data", custCustomDataList);
				
				//Map<String, Object> custEconomicData = DataHelper.getCustEconomicData(delegator, partyId, "BAL_METRICS");
				//data.putAll(custEconomicData);
				result.put("result", data);
			}
		}catch (Exception e) {
			result = ServiceUtil.returnError(e.getMessage());
		}
		
		return result;
	}
	
	public static Map<String, Object> verifyCustomer(DispatchContext dctx, Map<String, Object> context){

		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	
    	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		NumberFormat nf = NumberFormat.getInstance(locale);
		Map<String, Object> result = new HashMap<String, Object>();
		
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
				
				GenericValue productStoreEmail = null;
				try {
					String _6_digit_code = org.fio.customer.service.util.DataUtil.randomNumRecursive(delegator);
					Map<String, Object> sendMap = new HashMap<String, Object>();
					productStoreEmail = EntityQuery.use(delegator).from("ProductStoreEmailSetting").where("productStoreId", "9000", "emailType", "CUSTOMER_ACTIVATE_MAIL").queryFirst();
					Debug.log("productStoreEmail==== "+productStoreEmail);
					Debug.log("userLogin==== "+userLogin);
					 if(UtilValidate.isNotEmpty(productStoreEmail)) {
						//Store OTP against customer
						 GenericValue customerOtp = null;
						if(UtilValidate.isNotEmpty(_6_digit_code)) {
							customerOtp = delegator.makeValue("SecurityTracking");
							customerOtp.set("partyId", partyId);
							customerOtp.set("trackingTypeId", "EMAIL_OTP");
							customerOtp.set("hashCode", "");
							customerOtp.set("statusId", EmailVerifyStatus.SENT);
							customerOtp.set("value", _6_digit_code);
							customerOtp.set("fromDate", UtilDateTime.nowTimestamp());	
							customerOtp.set("thruDate", null);	
							
							String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
							sendMap.put("bodyScreenUri", bodyScreenLocation);
							String subjectString = productStoreEmail.getString("subject");
							sendMap.put("subject", subjectString);
							sendMap.put("sendFrom", productStoreEmail.get("fromAddress"));
							sendMap.put("sendTo", emailId);
							Map<String, Object> bodyParameters = UtilMisc.<String, Object>toMap("code", _6_digit_code,"partyName", partyName);
							sendMap.put("bodyParameters", bodyParameters);
							sendMap.put("userLogin",userLogin);

							Map<String, Object> sendResp = dispatcher.runSync("sendMailFromScreen", sendMap);
							
							if(!ServiceUtil.isSuccess(sendResp)) {
								Debug.logInfo("Service Error : ", MODULE);
								return ServiceUtil.returnError("Service error : sendMailFromScreen "+ServiceUtil.getErrorMessage(sendResp));
							}
							delegator.createOrStore(customerOtp);
							
							GenericValue party =  EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryFirst();
							if(UtilValidate.isNotEmpty(party)) {
								party.set("statusId", "PARTY_DISABLED");
								party.store();
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
				
		        GenericValue customerOtp = EntityQuery.use(delegator).from("SecurityTracking").where("value", otp,"partyId", partyId, "trackingTypeId","EMAIL_OTP").filterByDate().queryFirst();
		        if(UtilValidate.isNotEmpty(customerOtp)) {
		        	hashCode = UtilValidate.isNotEmpty(hashCode) ? hashCode : customerOtp.getString("hashCode");
		        	partyId = customerOtp.getString("partyId");
		        	GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryFirst();
					if(UtilValidate.isNotEmpty(party)) {
						party.set("statusId", "PARTY_ENABLED");
						party.store();
						
						
						customerOtp.set("thruDate", UtilDateTime.nowTimestamp());
						customerOtp.set("statusId", EmailVerifyStatus.VERIFIED);
						customerOtp.store();
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
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateUserData(DispatchContext dctx, Map<String, Object> context) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	
    	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		NumberFormat nf = NumberFormat.getInstance(locale);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
		String mode = UtilValidate.isNotEmpty(requestContext) ? (String) requestContext.get("mode") : "";
		String user_id = UtilValidate.isNotEmpty(requestContext) ? (String) requestContext.get("user_id") : "";
		String newUserId = UtilValidate.isNotEmpty(requestContext) ? (String) requestContext.get("new_user_id") : "";
		String password = UtilValidate.isNotEmpty(requestContext) ? (String) requestContext.get("password") : "";
		String newPassword = UtilValidate.isNotEmpty(requestContext) ? (String) requestContext.get("new_password") : "";
		String confirmPassword = UtilValidate.isNotEmpty(requestContext) ? (String) requestContext.get("confirm_password") : "";
		
		try {
			Map<String, Object> serviceResult = new HashMap<String, Object>();
			if(UserAccountMode.FORGET_PASSWORD.equals(mode)) {
				serviceResult = dispatcher.runSync("cs.forgetUserPasswordV3", UtilMisc.toMap("userLogin",userLogin,"userLoginId", user_id, "newPassword", newPassword, "newPasswordVerify", confirmPassword));
			} else if(UserAccountMode.UPDATE_PASSWORD.equals(mode)) {
				serviceResult = dispatcher.runSync("cs.updateUserPasswordV3", UtilMisc.toMap("userLogin",userLogin,"userLoginId", user_id, "currentPassword", password, "newPassword", newPassword, "newPasswordVerify", confirmPassword));
				
			} else if(UserAccountMode.CHANGE_USER_ID.equals(mode)) {
				serviceResult = dispatcher.runSync("cs.updateUserIdV3", UtilMisc.toMap("userLogin",userLogin,"userId", user_id, "newUserId",newUserId, "currentPassword", password));
			}
			if(!ServiceUtil.isSuccess(serviceResult)) {
				result = ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
			} else
				result = ServiceUtil.returnSuccess();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> updateUserPassword(DispatchContext ctx, Map<String, ?> context) {
        Delegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue loggedInUserLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage(SECURITY_RESOURCE, "loginevents.password_was_changed_with_success", locale));

        // load the external auth modules -- note: this will only run once and cache the objects
        if (!AuthHelper.authenticatorsLoaded()) {
            AuthHelper.loadAuthenticators(ctx.getDispatcher());
        }

        boolean useEncryption = "true".equals(EntityUtilProperties.getPropertyValue("security", "password.encrypt", delegator));
        boolean adminUser = true;

        String userLoginId = (String) context.get("userLoginId");
        String errMsg = null;

        if (UtilValidate.isEmpty(userLoginId)) {
            userLoginId = loggedInUserLogin.getString("userLoginId");
        }
        
        String currentPassword = (String) context.get("currentPassword");
        String newPassword = (String) context.get("newPassword");
        String newPasswordVerify = (String) context.get("newPasswordVerify");
        String passwordHint = (String) context.get("passwordHint");

        GenericValue userLoginToUpdate = null;

        try {
        	EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
    				EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId),
    				EntityCondition.makeCondition(EntityOperator.OR,
    						EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"),
    						EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, null)
    						)
    				);
            userLoginToUpdate = EntityQuery.use(delegator).from("UserLogin").where(condition).queryOne();
        } catch (GenericEntityException e) {
            Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.could_not_change_password_read_failure", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }

        if (userLoginToUpdate == null) {
            // this may be a full external authenticator; first try authenticating
            boolean authenticated = false;
            try {
                authenticated = AuthHelper.authenticate(userLoginId, currentPassword, true);
            } catch (AuthenticatorException e) {
                // safe to ingore this; but we'll log it just in case
                Debug.logWarning(e, e.getMessage(), MODULE);
            }

            // call update password if auth passed
            if (authenticated) {
                try {
                    AuthHelper.updatePassword(userLoginId, currentPassword, newPassword);
                } catch (AuthenticatorException e) {
                    Debug.logError(e, e.getMessage(), MODULE);
                    Map<String, String> messageMap = UtilMisc.toMap("userLoginId", userLoginId);
                    errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.could_not_change_password_userlogin_with_id_not_exist", messageMap, locale);
                    return ServiceUtil.returnError(errMsg);
                }
                //result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                result.put("updatedUserLogin", userLoginToUpdate);
                return result;
            } else {
                Map<String, String> messageMap = UtilMisc.toMap("userLoginId", userLoginId);
                errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.could_not_change_password_userlogin_with_id_not_exist", messageMap, locale);
                return ServiceUtil.returnError(errMsg);
            }
        }

        if ("true".equals(EntityUtilProperties.getPropertyValue("security", "password.lowercase", delegator))) {
            currentPassword = currentPassword.toLowerCase();
            newPassword = newPassword.toLowerCase();
            newPasswordVerify = newPasswordVerify.toLowerCase();
        }

        List<String> errorMessageList = new LinkedList<String>();
        if (newPassword != null) {
        	LoginServices.checkNewPassword(userLoginToUpdate, currentPassword, newPassword, newPasswordVerify,
                passwordHint, errorMessageList, adminUser, locale);
        }

        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }

        String externalAuthId = userLoginToUpdate.getString("externalAuthId");
        if (UtilValidate.isNotEmpty(externalAuthId)) {
            // external auth is set; don't update the database record
            try {
                AuthHelper.updatePassword(externalAuthId, currentPassword, newPassword);
            } catch (AuthenticatorException e) {
                Debug.logError(e, e.getMessage(), MODULE);
                Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
                errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.could_not_change_password_write_failure", messageMap, locale);
                return ServiceUtil.returnError(errMsg);
            }
        } else {
            userLoginToUpdate.set("currentPassword", useEncryption ? HashCrypt.cryptUTF8(LoginServices.getHashType(), null, newPassword) : newPassword, false);
            userLoginToUpdate.set("passwordHint", passwordHint, false);
            userLoginToUpdate.set("requirePasswordChange", "N");

            try {
                userLoginToUpdate.store();
                LoginServices.createUserLoginPasswordHistory(delegator,userLoginId, newPassword);
            } catch (GenericEntityException e) {
                Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
                errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.could_not_change_password_write_failure", messageMap, locale);
                return ServiceUtil.returnError(errMsg);
            }
        }
        result.put("updatedUserLogin", userLoginToUpdate);
        return result;
    }
	
	public static Map<String, Object> updateUserId(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
        Map<String, Object> result =  new LinkedHashMap<String, Object>();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<String> errorMessageList = new LinkedList<String>();
        Locale locale = (Locale) context.get("locale");

        //boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security", "password.encrypt"));
        result = ServiceUtil.returnSuccess();
        String userId = (String) context.get("userId");
        String newUserId = (String) context.get("newUserId");
        String currentPassword = (String) context.get("currentPassword");
        String errMsg = null;
        try {
        	if ((userId != null) && ("true".equals(EntityUtilProperties.getPropertyValue("security", "username.lowercase", delegator)))) {
        		userId = userId.toLowerCase();
            }	
            
        	EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
    				EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userId),
    				EntityCondition.makeCondition(EntityOperator.OR,
    						EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"),
    						EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, null)
    						)
    				);
            GenericValue userLoginGv = EntityQuery.use(delegator).from("UserLogin").where(condition).queryFirst();
            if(UtilValidate.isNotEmpty(userLoginGv)) {
            	String partyId = userLoginGv.getString("partyId");
                String password = userLoginGv.getString("currentPassword");
                String passwordHint = userLoginGv.getString("passwordHint");
                if(!(DataHelper.checkPassword(password, true, currentPassword))) {
                	return ServiceUtil.returnError("User not found!");
				}
             // security: don't create a user login if the specified partyId (if not empty) already exists
                // unless the logged in user has permission to do so (same partyId or PARTYMGR_CREATE)
                if (UtilValidate.isNotEmpty(partyId)) {
                    //GenericValue party = null;
                    //try {
                    //    party = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryOne();
                    //} catch (GenericEntityException e) {
                    //    Debug.logWarning(e, "", module);
                    //}

                    if (!userLoginGv.isEmpty()) {
                        // security check: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
                        if (!partyId.equals(userLoginGv.getString("partyId"))) {
                            errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.party_with_party_id_exists_not_permission_create_user_login", locale);
                            errorMessageList.add(errMsg);
                        }
                    } else {
                        errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.must_logged_in_have_permission_create_user_login_exists", locale);
                        errorMessageList.add(errMsg);
                    }
                }

                GenericValue newUserLogin = null;
                boolean doCreate = true;

                // check to see if there's a matching login and use it if it's for the same party
                try {
                    newUserLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", newUserId).queryOne();
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, "", MODULE);
                    Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
                    errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.could_not_create_login_user_read_failure", messageMap, locale);
                    errorMessageList.add(errMsg);
                }

                if (newUserLogin != null) {
                    if (!newUserLogin.get("partyId").equals(partyId)) {
                        Map<String, String> messageMap = UtilMisc.toMap("userLoginId", newUserId);
                        errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.could_not_create_login_user_with_ID_exists", messageMap, locale);
                        errorMessageList.add(errMsg);
                    } else {
                        doCreate = false;
                    }
                } else {
                    newUserLogin = delegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", newUserId));
                }

                newUserLogin.set("passwordHint", passwordHint);
                newUserLogin.set("partyId", partyId);
                newUserLogin.set("currentPassword", password);
                newUserLogin.set("enabled", "Y");
                newUserLogin.set("disabledDateTime", null);

                if (errorMessageList.size() > 0) {
                    return ServiceUtil.returnError(errorMessageList);
                }

                try {
                    if (doCreate) {
                        newUserLogin.create();
                    } else {
                        newUserLogin.store();
                    }
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, "", MODULE);
                    Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
                    errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.could_not_create_login_user_write_failure", messageMap, locale);
                    return ServiceUtil.returnError(errMsg);
                }

                // Deactivate 'old' UserLogin and do not set disabledDateTime here, otherwise the 'old' UserLogin would be reenabled by next login
                userLoginGv.set("enabled", "N");
                userLoginGv.set("disabledDateTime", null);

                try {
                    userLoginGv.store();
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, "", MODULE);
                    Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
                    errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.could_not_disable_old_login_user_write_failure", messageMap, locale);
                    return ServiceUtil.returnError(errMsg);
                }
                result.put("newUserLogin", newUserLogin);
            }
    		
        } catch (Exception e) {
        	e.printStackTrace();
        	result = ServiceUtil.returnError("Error : "+ e.getMessage());
		}
        return result;
    }
	
	
	@SuppressWarnings("deprecation")
	public static Map<String, Object> forgetUserPassword(DispatchContext ctx, Map<String, ?> context) {
        Delegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue loggedInUserLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage(SECURITY_RESOURCE, "loginevents.password_was_changed_with_success", locale));

        // load the external auth modules -- note: this will only run once and cache the objects
        if (!AuthHelper.authenticatorsLoaded()) {
            AuthHelper.loadAuthenticators(ctx.getDispatcher());
        }

        boolean useEncryption = "true".equals(EntityUtilProperties.getPropertyValue("security", "password.encrypt", delegator));
        boolean adminUser = true;

        String userLoginId = (String) context.get("userLoginId");
        String errMsg = null;

        if (UtilValidate.isEmpty(userLoginId)) {
            userLoginId = loggedInUserLogin.getString("userLoginId");
        }
        
        String newPassword = (String) context.get("newPassword");
        String newPasswordVerify = (String) context.get("newPasswordVerify");
        String passwordHint = (String) context.get("passwordHint");

        GenericValue userLoginToUpdate = null;

        try {
        	EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
    				EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId),
    				EntityCondition.makeCondition(EntityOperator.OR,
    						EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"),
    						EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, null)
    						)
    				);
            userLoginToUpdate = EntityQuery.use(delegator).from("UserLogin").where(condition).queryOne();
        } catch (GenericEntityException e) {
            Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.could_not_change_password_read_failure", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }

        if (userLoginToUpdate == null) {
        	
        }

        if ("true".equals(EntityUtilProperties.getPropertyValue("security", "password.lowercase", delegator))) {
            newPassword = newPassword.toLowerCase();
            newPasswordVerify = newPasswordVerify.toLowerCase();
        }

        List<String> errorMessageList = new LinkedList<String>();
        if (newPassword != null) {
        	LoginServices.checkNewPassword(userLoginToUpdate, "", newPassword, newPasswordVerify,
                passwordHint, errorMessageList, adminUser, locale);
        }

        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }
        
        userLoginToUpdate.set("currentPassword", useEncryption ? org.ofbiz.base.crypto.HashCrypt.getDigestHash(newPassword) : newPassword, false);
        userLoginToUpdate.set("passwordHint", passwordHint, false);
        userLoginToUpdate.set("requirePasswordChange", "N");

        try {
            userLoginToUpdate.store();
            LoginServices.createUserLoginPasswordHistory(delegator,userLoginId, newPassword);
        } catch (GenericEntityException e) {
            Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(SECURITY_RESOURCE,"loginservices.could_not_change_password_write_failure", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }
        
        result.put("updatedUserLogin", userLoginToUpdate);
        return result;
    }
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static Map<String, Object> sendPassword(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Security security = ctx.getSecurity();
        
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage(SECURITY_RESOURCE, "loginevents.password_was_changed_with_success", locale));
        String userLoginId = "";
		String emailId = "";
		String partyId = "";
		String email_template_id = "";
		try {
        	Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
        	emailId = (String) requestContext.get("email_id");
        	email_template_id = (String) requestContext.get("email_template_id");
    		//partyId = org.fio.homeapps.util.DataUtil.getPartyIdByPrimaryEmail(delegator, emailId);
    		EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
    				EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, emailId),
    				EntityCondition.makeCondition(EntityOperator.OR,
    						EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"),
    						EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, null)
    						)
    				);
    		GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where(condition).queryFirst();
    		if(UtilValidate.isNotEmpty(userLogin)) {
    			userLoginId = UtilValidate.isNotEmpty(userLogin) ? userLogin.getString("userLoginId") : "";
    			partyId = UtilValidate.isNotEmpty(userLogin) ? userLogin.getString("partyId") : "";
    		}
    		
        } catch (Exception e) {
			e.printStackTrace();
		}

        boolean useEncryption = "true".equals(EntityUtilProperties.getPropertyValue("security", "password.encrypt", delegator));
        String errMsg = null;
        String defaultScreenLocation = "component://admin-portal/widget/user-mgmt/UserMgmtScreens.xml#TempPasswordEmail";
        
        if ((userLoginId != null) && ("true".equals(EntityUtilProperties.getPropertyValue("security", "username.lowercase", delegator)))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
            errMsg = UtilProperties.getMessage(SECURITY_RESOURCE, "loginevents.username_was_empty_reenter", locale);
            return ServiceUtil.returnError(errMsg);
        }
        if (!UtilValidate.isNotEmpty(partyId)) {
            errMsg = "User login party is empty.";
            return ServiceUtil.returnError(errMsg);
        }
        

        GenericValue supposedUserLogin = null;
        String passwordToSend = null;
        String autoPassword = null;
        try {
        	String keyValue = UtilProperties.getPropertyValue(LoginWorker.securityProperties, "login.secret_key_string");
            supposedUserLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userLoginId).queryOne();
            if (supposedUserLogin == null) {
                // the Username was not found
                errMsg = UtilProperties.getMessage(SECURITY_RESOURCE, "loginevents.username_not_found_reenter", locale);
                return ServiceUtil.returnError(errMsg);
            }
            if (useEncryption) {
                // password encrypted, can't send, generate new password and email to user
                //passwordToSend = RandomStringUtils.randomAlphanumeric(Integer.parseInt(org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "temp.password.length.min", "7")));
                passwordToSend = org.fio.homeapps.util.DataUtil.generateSecurePassword(Integer.parseInt(org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "temp.password.length.min", "7")));
                if ("true".equals(EntityUtilProperties.getPropertyValue("security", "password.lowercase", delegator))){
                    passwordToSend=passwordToSend.toLowerCase();
                }
                //autoPassword = RandomStringUtils.randomAlphanumeric(Integer.parseInt(org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "temp.password.length.min", "7")));
               /*
                EntityCrypto entityCrypto = new EntityCrypto(delegator,null); 
                try {
                    passwordToSend = entityCrypto.encrypt(keyValue, ModelField.EncryptMethod.TRUE, (Object) autoPassword);
                } catch (GeneralException e) {
                    Debug.logWarning(e, "Problem in encryption", MODULE);
                }
                */
                autoPassword = passwordToSend;
                supposedUserLogin.set("currentPassword", HashCrypt.cryptUTF8(LoginServices.getHashType(), null, autoPassword));
                supposedUserLogin.set("passwordHint", "Auto-Generated Password");
                if ("true".equals(EntityUtilProperties.getPropertyValue("security", "password.email_password.require_password_change", delegator))){
                    supposedUserLogin.set("requirePasswordChange", "Y");
                }
            } else {
                passwordToSend = supposedUserLogin.getString("currentPassword");
            }
            /* Its a Base64 string, it can contain + and this + will be converted to space after decoding the url.
               For example: passwordToSend "DGb1s2wgUQmwOBK9FK+fvQ==" will be converted to "DGb1s2wgUQmwOBK9FK fvQ=="
               So to fix it, done Url encoding of passwordToSend.
            */
            //passwordToSend = URLEncoder.encode(passwordToSend, "UTF-8");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", MODULE);
            Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.toString());
            errMsg = UtilProperties.getMessage(SECURITY_RESOURCE, "loginevents.error_accessing_password", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }
        
        if (!UtilValidate.isNotEmpty(emailId)) {
            // the Username was not found
            errMsg = UtilProperties.getMessage(SECURITY_RESOURCE, "loginevents.no_primary_email_address_set_contact_customer_service", locale);
            return ServiceUtil.returnError(errMsg);
        }

        // get the ProductStore email settings
        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = EntityQuery.use(delegator).from("ProductStoreEmailSetting").where("productStoreId", "9000", "emailType", "TEMP_USER_PWD").queryOne();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStoreEmailSetting", MODULE);
        }

        String bodyScreenLocation = null;
        GenericValue emailTemplate = null;
        if(UtilValidate.isNotEmpty(email_template_id)) {
        	emailTemplate = EntityQuery.use(delegator).from("EmailTemplateSetting").where("emailTemplateSettingId", email_template_id).queryFirst();
        	bodyScreenLocation = emailTemplate.getString("bodyScreenLocation");
        	productStoreEmail = null;
        }
        	
        
        if (UtilValidate.isEmpty(bodyScreenLocation) && productStoreEmail != null) {
            bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        }
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
            bodyScreenLocation = defaultScreenLocation;
        }
        
        String partyName = PartyHelper.getPartyName(delegator, partyId, false);
        // set the needed variables in new context
	        Map<String, Object> bodyParameters = new HashMap<String, Object>();
	        bodyParameters.put("useEncryption", Boolean.valueOf(useEncryption));
	        bodyParameters.put("password", UtilFormatOut.checkNull(passwordToSend));  
	        bodyParameters.put("locale", locale);
	        bodyParameters.put("username", userLoginId);
	        bodyParameters.put("name", partyName);
	        
	        Map<String, Object> serviceContext = new HashMap<String, Object>();
	        serviceContext.put("bodyScreenUri", bodyScreenLocation);
	        serviceContext.put("bodyParameters", bodyParameters);
	        if (productStoreEmail != null) {
	            serviceContext.put("subject", productStoreEmail.getString("subject"));
	            serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
	            serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
	            serviceContext.put("sendBcc", productStoreEmail.get("bccAddress"));
	            serviceContext.put("contentType", productStoreEmail.get("contentType"));
	            serviceContext.put("senderName", productStoreEmail.get("senderName"));
	        } else {
	            if (emailTemplate != null) {
	                String subject = emailTemplate.getString("subject");
	                subject = FlexibleStringExpander.expandString(subject, UtilMisc.toMap("userLoginId", userLoginId));
	                serviceContext.put("subject", subject);
	                serviceContext.put("sendFrom", emailTemplate.get("fromAddress"));
	                serviceContext.put("sendCc", emailTemplate.get("ccAddress"));
	                serviceContext.put("sendBcc", emailTemplate.get("bccAddress"));
	                serviceContext.put("contentType", emailTemplate.get("contentType"));
	            } else {
	                serviceContext.put("subject", UtilProperties.getMessage(SECURITY_RESOURCE, "loginservices.password_reminder_subject", UtilMisc.toMap("userLoginId", userLoginId), locale));
	                serviceContext.put("sendFrom", EntityUtilProperties.getPropertyValue("general", "defaultFromEmailAddress", delegator));
	            }            
	        }
	        serviceContext.put("sendTo", emailId.toString());
	        serviceContext.put("partyId", partyId);
	
	        try {
	           Map<String, Object> serResult = dispatcher.runSync("sendMailHiddenInLogFromScreen", serviceContext);
	
	            if (ModelService.RESPOND_ERROR.equals(serResult.get(ModelService.RESPONSE_MESSAGE))) {
	                Map<String, Object> messageMap = UtilMisc.toMap("errorMessage", serResult.get(ModelService.ERROR_MESSAGE));
	                errMsg = UtilProperties.getMessage(SECURITY_RESOURCE, "loginevents.error_unable_email_password_contact_customer_service_errorwas", messageMap, locale);
	                return ServiceUtil.returnError(errMsg);
	            }
	        } catch (GenericServiceException e) {
	            Debug.logWarning(e, "", MODULE);
	            errMsg = UtilProperties.getMessage(SECURITY_RESOURCE, "loginevents.error_unable_email_password_contact_customer_service", locale);
	            return ServiceUtil.returnError(errMsg);
	        }

        // don't save password until after it has been sent
        if (useEncryption) {
            try {
            	supposedUserLogin.set("enabled", "Y");
            	supposedUserLogin.set("requirePasswordChange", "N");
				supposedUserLogin.set("disabledDateTime", null);
                supposedUserLogin.store();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "", MODULE);
                Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.toString());
                errMsg = UtilProperties.getMessage(SECURITY_RESOURCE, "loginevents.error_saving_new_password_email_not_correct_password", messageMap, locale);
                return ServiceUtil.returnError(errMsg);
            }
        }
        
        result.put("userId", userLoginId);
        result.put("emailId", emailId);
        return result;
    }
	
	public static Map<String, Object> createUser(DispatchContext dctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = ServiceUtil.returnSuccess("User has been created successfully.");
        try {
        	String userName = (String) context.get("userId");
        	String partyId = (String) context.get("partyId");
        	String userStatus = (String) context.get("userStatus");
        	String emailId = DataHelper.getCustPrimEmail(delegator, partyId);
        	
        	List<String> erroList =  new ArrayList<String>();
			Map<String, Object> userLoginContext = new HashMap<String, Object>();
        	GenericValue newUserLogin = null;
			if(UtilValidate.isNotEmpty(userName)) {
				String sendTempPwd = "Y";
				userLoginContext.put("userLoginId", userName);
				userLoginContext.put("currentPassword", (String) context.get("password"));
				userLoginContext.put("currentPasswordVerify", context.get("confirmPassword") !=null ? (String) context.get("confirmPassword") : context.get("password"));
				userLoginContext.put("passwordHint", (String) context.get("passwordHint"));
				GenericValue userLogin1 = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId",userName), false);
				
				if(UtilValidate.isNotEmpty(userLogin1)) {
					newUserLogin = userLogin1;
					Debug.logInfo("User Id already exists.", MODULE);
					//return ServiceUtil.returnError("Username in use, please choose another");
				} else
					newUserLogin = delegator.makeValue("UserLogin");
				
				newUserLogin.setPKFields(userLoginContext);
				newUserLogin.setNonPKFields(userLoginContext);
				
				//valid the password
				String password = (String) userLoginContext.get("currentPassword");
				if(UtilValidate.isNotEmpty(password)) {
					sendTempPwd = "N";
					String confirmPassword = (String) userLoginContext.get("currentPasswordVerify");
	                String passwordHint = (String) userLoginContext.get("passwordHint");
	                LoginServices.checkNewPassword(newUserLogin, null, password, confirmPassword, passwordHint, erroList, true, locale);
	                if(erroList!=null && erroList.size() > 0) {
	                	return ServiceUtil.returnError(erroList);
	                }
				}
              //create user login and invoke createUserLoginPasswordHistory to track 
    			if(UtilValidate.isNotEmpty(newUserLogin)) {
    				
    				newUserLogin.set("partyId", partyId);
    				 boolean useEncryption = "true".equals(EntityUtilProperties.getPropertyValue("security", "password.encrypt", delegator));
    	                if (useEncryption) { newUserLogin.set("currentPassword", org.ofbiz.base.crypto.HashCrypt.getDigestHash((String) newUserLogin.get("currentPassword"))); }
    				newUserLogin.set("isLdapUser", (String) context.get("isLdapUser"));
    				newUserLogin.set("enabled", userStatus);
    				delegator.createOrStore(newUserLogin);
    			}			
    			result.put("isTempPassword", sendTempPwd);
			}
			
			result.put("userId", userName);
			result.put("emailId", emailId);
        } catch (Exception e) {
        	Debug.logWarning(e, "", MODULE);
            return ServiceUtil.returnError("Exception : "+e.getMessage());
		}
        
        return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> sendEmailWithTemplate(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = ServiceUtil.returnSuccess("Email has been sent successfuly.");
        
		Map<String, Object> requestContext =  UtilValidate.isNotEmpty(context.get("requestContext")) ? (Map<String, Object>) context.get("requestContext") : new HashMap<String, Object>();
		String partyId = (String) requestContext.get("partyId");
		String custRequestId =  (String) requestContext.get("custRequestId");
		String workEffortId = (String) requestContext.get("workEffortId");
		String salesOpportunityId = (String) requestContext.get("salesOpportunityId");
		String emailId = (String) requestContext.get("emailId");
		String senderEmail = (String) requestContext.get("senderEmail");
		String emailTemplateId = (String) requestContext.get("emailTemplateId");
		String partyIdTo = (String) requestContext.get("partyIdTo");
		String contactMechIdTo = "";
		if(UtilValidate.isEmpty(emailId)) {
			GenericValue primaryContactMailGv = EntityQuery.use(delegator).from("PartyContactWithPurpose")
					.where("partyId", partyIdTo, "contactMechPurposeTypeId", "PRIMARY_EMAIL",
							"contactMechTypeId", "EMAIL_ADDRESS")
					.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
					.queryFirst();

			if (UtilValidate.isEmpty(primaryContactMailGv)) {
				primaryContactMailGv = EntityQuery.use(delegator).from("PartyContactWithPurpose")
						.where("partyId", partyIdTo, "contactMechTypeId", "EMAIL_ADDRESS")
						.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
						.queryFirst();
			}
			if (UtilValidate.isNotEmpty(primaryContactMailGv)) {
				emailId = primaryContactMailGv.getString("infoString");
				contactMechIdTo = primaryContactMailGv.getString("contactMechId");

			}
		} else {
			GenericValue contactMech =  EntityQuery.use(delegator).from("ContactMech").where("infoString", emailId).queryFirst();
			contactMechIdTo = UtilValidate.isNotEmpty(contactMech) ? contactMech.getString("contactMechId") : "";
		}
		if (UtilValidate.isNotEmpty(emailTemplateId)) {
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> requestContext1 = FastMap.newInstance();
			Map<String, Object> commEventMap = new HashMap<String, Object>();
			Timestamp now = UtilDateTime.nowTimestamp();
			GenericValue emailTemlateData = null;
			String senderName = "";
			try {
				Debug.log("templateId===" + emailTemplateId);
				emailTemlateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId", emailTemplateId), false);
				Debug.log("subjct" + emailTemlateData.getString("subject"));
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				Debug.logError(e1, MODULE);
			}
			commEventMap.put("communicationEventTypeId", "EMAIL_COMMUNICATION");
			commEventMap.put("contactMechTypeId", "EMAIL_ADDRESS");
			commEventMap.put("contactMechIdTo", contactMechIdTo);
			commEventMap.put("statusId", "COM_PENDING");
			commEventMap.put("partyIdFrom", partyId);
			commEventMap.put("partyIdTo", partyIdTo);
			commEventMap.put("datetimeStarted", now);
			commEventMap.put("entryDate", now);
			commEventMap.put("subject", emailTemlateData.getString("subject"));
			commEventMap.put("userLogin", userLogin);
			Map<String, Object> createResult = new HashMap<String, Object>();
			try {
				createResult = dispatcher.runSync("createCommunicationEvent", commEventMap);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("Problem Creating In Communication Event");
			}
			if (ServiceUtil.isError(createResult)) {
				return ServiceUtil.returnError("Problem Creating In Communication Event");
			}
			String communicationEventId = (String) createResult.get("communicationEventId");
			if(UtilValidate.isEmpty(senderEmail))
				senderEmail = emailTemlateData.getString("senderEmail");
			String emailContent = "";
			// String templateFormContent =
			// emailTemlateData.getString("textContent");
			String templateFormContent = emailTemlateData.getString("templateFormContent");
			if (UtilValidate.isNotEmpty(templateFormContent)) {
				if (Base64.isBase64(templateFormContent)) {
					templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
				}
			}
			senderName = UtilValidate.isNotEmpty(emailTemlateData)?emailTemlateData.getString("senderName"):"";
			// prepare email content [start]
			Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
			extractContext.put("delegator", delegator);
			extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
			extractContext.put("fromEmail", senderEmail);
			extractContext.put("toEmail", emailId);
			extractContext.put("partyId", partyId);
			extractContext.put("custRequestId", custRequestId);
			extractContext.put("workEffortId", workEffortId);
			extractContext.put("salesOpportunityId", salesOpportunityId);
			extractContext.put("emailContent", templateFormContent);
			extractContext.put("templateId", emailTemplateId);

			Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
			emailContent = (String) extractResultContext.get("emailContent");
			// prepare email content [end]
			requestContext1.put("communicationEventId", communicationEventId);
			requestContext1.put("emailContent", emailContent);
			requestContext.put("templateId", emailTemplateId);
			requestContext1.put("partyId", partyId);
			requestContext1.put("subject", emailTemlateData.getString("subject"));
			requestContext1.put("nto", emailId);
			requestContext1.put("nsender", senderEmail);
			requestContext1.put("senderName", senderName);
			callCtxt.put("requestContext", requestContext1);
			callCtxt.put("userLogin", userLogin);
			try {
				dispatcher.runSync("common.sendEmail", callCtxt);
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				Debug.logError("Email send failed: ", MODULE);
			}
		}
		return result;
	}
	
}
