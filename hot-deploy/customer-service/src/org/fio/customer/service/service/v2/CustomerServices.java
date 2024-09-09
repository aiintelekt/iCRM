package org.fio.customer.service.service.v2;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.fio.admin.portal.util.DataUtil;
import org.fio.customer.service.constant.CustomerConstants.EmailVerifyStatus;
import org.fio.customer.service.constant.CustomerConstants.VerifyMode;
import org.fio.customer.service.util.DataHelper;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilAttribute;
import org.fio.homeapps.util.UtilMessage;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.EntityCryptoException;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class CustomerServices {


	private static final String MODULE = CustomerServices.class.getName();
	public static final String resource = "crmUiLabels";
	
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
		String custName = (String) requestContext.get("custName");
		String custPrimaryPhone = (String) requestContext.get("custPrimPhone");
		String custLocName = (String) requestContext.get("custLocName");
		String custSourceDateStr = (String) requestContext.get("custSourceDate");
		String roleTypeId = (String) requestContext.get("custRoleType");
		String custPrimaryEmail = (String) requestContext.get("custPrimEmail"); 
		
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
				String firstName = "";
				String lastName = "";
				String middleName = "";
				if(UtilValidate.isNotEmpty(custName)) {
					String[] names = custName.split(" ");
					if(UtilValidate.isNotEmpty(names) && names.length == 1) {
						firstName = names[0];
					} else if(UtilValidate.isNotEmpty(names) && names.length == 2) {
						firstName = names[0];
						lastName = names[1]; 
					} else if(UtilValidate.isNotEmpty(names) && names.length == 3) {
						firstName = names[0];
						middleName = names[1];
						lastName = names[2];
					} else if(UtilValidate.isNotEmpty(names) && names.length > 3) {
						firstName = names[0];
						middleName = names[1];
						lastName = names[2];
					}
				}
				input = UtilMisc.toMap("firstName", firstName,"middleName", middleName,"lastName",lastName, "partyId", partyId,"preferredCurrencyUomId","USD");
				serviceResults = dispatcher.runSync("createPerson", input);
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
				partyId = (String) serviceResults.get("partyId");
			} else if("ACCOUNT".equals(roleTypeId)) {
				// create the Party and PartyGroup, which results in a partyId
				input = UtilMisc.toMap("groupName", custName, "partyId", partyId,"preferredCurrencyUomId","USD");
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
				party.put("statusId", "PARTY_DISABLED");
				party.put("roleTypeId", roleTypeId);
				party.put("createdDate", UtilValidate.isNotEmpty(custSourceDate) ? Timestamp.valueOf(custSourceDate): UtilDateTime.nowTimestamp());
				party.put("createdByUserLogin", userLogin.getString("userLoginId"));
				party.put("timeZoneDesc", timeZoneDesc);
				party.put("externalId", custId);
				party.store();
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
            		result.put("_ERROR_MESSAGE_", "Problem While creating Phone Data");
            		return result;
            	}
			}
			if(UtilValidate.isNotEmpty(custPrimaryEmail)) {
				Map<String, Object> emailAddress = UtilMisc.toMap("partyId", partyId, "userLogin", (Object) userLogin, "contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL");
				emailAddress.put("emailAddress", custPrimaryEmail);
            	
            	Map<String, Object> serviceResultss = dispatcher.runSync("createPartyEmailAddress", emailAddress);
            	
            	if (ServiceUtil.isError(serviceResultss)) {
            		result.put("_ERROR_MESSAGE_", "Problem While creating Phone Data");
            		return result;
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
						break;
					
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
			result.put("result", dataList);
			if(UtilValidate.isNotEmpty(partyId)) {
				Map<String, Object> result1 = dispatcher.runSync("cs.verifyCustomerV2", UtilMisc.toMap("userLogin",  userLogin, "emailId", custPrimaryEmail, "mode", "VERIFICATION", "hashCode", "", "partyId", partyId));
				if(!ServiceUtil.isSuccess(result)) {
					Debug.logError("Error : "+ ServiceUtil.getErrorMessage(result1), MODULE);
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
		String custName = (String) requestContext.get("custName");
		String custPrimaryPhone = (String) requestContext.get("custPrimPhone");
		String custLocName = (String) requestContext.get("custLocName");
		String custSourceDateStr = (String) requestContext.get("custSourceDate");
		String roleTypeId = (String) requestContext.get("custRoleType");
		String custPrimaryEmail = (String) requestContext.get("custPrimEmail");
		
		
		
		boolean beganTransaction = false;
		
		try {
			String ownerId = UtilValidate.isNotEmpty(context.get("owenerId")) ? (String) context.get("ownerId") : userLogin.getString("userLoginId");
			LocalDateTime custSourceDate = LocalDateTime.now();
			if(UtilValidate.isNotEmpty(custSourceDateStr) && DataUtil.isDate(custSourceDateStr, "timestamp"))
				custSourceDate = LocalDateTime.parse(custSourceDateStr);
			
			beganTransaction = TransactionUtil.begin();
			
			//Create Customer(ACCOUNT)
			String partyId = "";
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
				Map<String, Object> serviceResults = new HashMap<String, Object>();
				if("CUSTOMER".equals(roleTypeId)){
					Map<String, Object> input = UtilMisc.toMap("firstName", custName, "partyId", partyId);
					input.put("userLogin", userLogin);
					serviceResults = dispatcher.runSync("updatePerson", input);
					if (ServiceUtil.isError(serviceResults)) {
						return serviceResults;
					}
				}
				else if(!"CUSTOMER".equals(roleTypeId)) {
					Map<String, Object> input = UtilMisc.toMap("groupName", custName, "partyId", partyId);
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
				party.put("createdDate", UtilValidate.isNotEmpty(custSourceDate) ? Timestamp.valueOf(custSourceDate): UtilDateTime.nowTimestamp());
				party.put("externalId", custId);
				party.store();
				
				//update owner roletype/ownerbu/empteam in  party table
				dispatcher.runSync("ap.updateSecurityPartyInfo", UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId"), "ownerId",ownerId, "partyId", partyId ));
				
				// create party attribute
				if(UtilValidate.isNotEmpty(custLocName))
					//DataHelper.createPartyAttribute(delegator, UtilMisc.toMap("partyId", partyId, "attrName", "CUST_LOC_NAME", "attrValue", custLocName));
				org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, custLocName, "CUST_LOC_NAME");

				
				List<GenericValue> partyPrimPhoneList = EntityQuery.use(delegator).from("PartyContactMechPurpose").where("partyId", partyId,"contactMechPurposeTypeId","PRIMARY_PHONE").queryList();
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
	            		result.put("_ERROR_MESSAGE_", "Problem While creating Phone Data");
	            		return result;
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
				List<GenericValue> partyPrimEmailList = EntityQuery.use(delegator).from("PartyContactMechPurpose").where("partyId", partyId,"contactMechPurposeTypeId","PRIMARY_EMAIL").queryList();
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
	            		result.put("_ERROR_MESSAGE_", "Problem While creating Phone Data");
	            		return result;
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
			            		result.put("_ERROR_MESSAGE_", "Problem While creating Phone Data");
			            		return result;
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
			            		result.put("_ERROR_MESSAGE_", "Problem While creating Phone Data");
			            		return result;
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
							break;
						
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
			result.put("result", dataList);
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
			String custName = (String) requestContext.get("custName");
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
			
			
			
			if(UtilValidate.isNotEmpty(custName)) {
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("groupName", EntityOperator.EQUALS, custName),
						EntityCondition.makeCondition("firstName", EntityOperator.EQUALS, custName)
						));
			}
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
				Map<String, Object> custPartyAttributes = DataHelper.getPartyAttributeList(delegator, resultList, "partyId", "CUST_LOC_NAME");
				
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
            result.put("totalPage", totalPage);
            result.put("totalRecord", totalRecord);
            result.put("currPage", currPage);
			
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
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			
			GenericValue customer = EntityQuery.use(delegator).from(dynamicView).where(condition).queryFirst();
			if(UtilValidate.isNotEmpty(customer)) {
				Map<String, Object> data = new LinkedHashMap<String, Object>();
				String partyId = customer.getString("partyId");
				String name = "";
				if("ACCOUNT".equals(roleTypeId)) {
					name = customer.getString("groupName");
				} else {
					name = customer.getString("firstName") + (UtilValidate.isNotEmpty(customer.getString("lastName")) ? " " + customer.getString("lastName") : "");
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
				List<Map<String, Object>> custCustomDataList = DataHelper.getCustCustomData(delegator, partyId, "CUST_CUSTOM_DATA");
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
				//result.put("hashCode", hashCode);
				
			} else if(VerifyMode.VALIDATION.equals(mode)) {
				// validate the code and enable the party
				String partyId = DataHelper.getPartyIdByEmail(delegator, emailId);
				String offerPage = "";
				
				GenericValue customerOtp = EntityQuery.use(delegator).from("SecurityTracking").where("value", otp, "partyId", partyId,"trackingTypeId","EMAIL_OTP").filterByDate().queryFirst();
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
						
						// get offer page
						offerPage = org.groupfio.common.portal.util.UtilAttribute.getAttrFieldValue(delegator, UtilMisc.toMap("partyId",partyId,"domainEntityType","CUSTOMER","customFieldId", "OFFER_PAGE"));

						if(UtilValidate.isNotEmpty(partyId)) {
							String dataSourceId = UtilValidate.isNotEmpty(party) ? party.getString("dataSourceId") : "";
							if(UtilValidate.isNotEmpty(dataSourceId))
								offerPage = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, dataSourceId, "");
						}
						if(UtilValidate.isNotEmpty(offerPage)) {
							String encryptStr = partyId+"&"+UtilDateTime.nowTimestamp();
							String hashCode1 = Base64.getEncoder().encodeToString(encryptStr.getBytes("utf-8"));
							offerPage = offerPage+"#"+hashCode1;
						}
						
					}
		        } else {
		        	return ServiceUtil.returnError("OTP not valid!");
		        }
				
				result = ServiceUtil.returnSuccess();
				result.put("partyId", partyId);
				result.put("offerPage", offerPage);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		
		return result;
		
	}
}
