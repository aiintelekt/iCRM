package org.fio.customer.service.service.v5;

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

import org.fio.admin.portal.util.DataUtil;

import org.fio.customer.service.util.DataHelper;
import org.fio.homeapps.util.UtilMessage;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/***
 * 
 * @author Mahendran T
 *
 */

public class GravityCustomerServices {


	private static final String MODULE = GravityCustomerServices.class.getName();
	public static final String resource = "CustomerServiceUiLabels";
	public static final String SECURITY_RESOURCE = "SecurityextUiLabels";
		
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createGravityCustomer(DispatchContext dctx, Map<String, Object> context) {
		 Debug.log("enter through gravity forms====apr 21" + context);

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
		String contactListId = (String) requestContext.get("contactListId"); 
        String primcontactMechId = null;
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
            	Debug.log(serviceResultss + "===serviceResultss apr23");
            	primcontactMechId = (String) serviceResultss.get("contactMechId");
            	Debug.log(primcontactMechId + "===primcontactMechId apr23");

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
				}
			}
			String gravityCustomFields = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "GRAVITY_CUST_FIELDS");
		    List segmentFields = DataUtil.stringToList(gravityCustomFields, ",");
             
			Debug.log(partyId + "====CustomFieldPartyClassification====");
			if(UtilValidate.isNotEmpty(segmentFields)) {
			for(int i = 0;i < segmentFields.size();i++) {
			    	System.out.println(segmentFields.get(i) + "segmentFields==");
                    GenericValue addHHSegmentElg=delegator.makeValue("CustomFieldPartyClassification");
	                addHHSegmentElg.put("partyId",partyId);
	                addHHSegmentElg.put("customFieldId",segmentFields.get(i));
	                addHHSegmentElg.put("groupId","CUSTOMER_INFO_DEDUP" );
	                addHHSegmentElg.put("inceptionDate", UtilDateTime.nowTimestamp());
	                addHHSegmentElg.create();
	 		    	    
			}
			}
			   if(UtilValidate.isNotEmpty(contactListId)) {
               GenericValue contactListParty=delegator.makeValue("ContactListParty");
               contactListParty.put("partyId",partyId );
               contactListParty.put("contactListId",contactListId);
               contactListParty.put("fromDate",UtilDateTime.nowTimestamp() );
               contactListParty.put("contactListSeqId", delegator.getNextSeqId("ContactListParty"));
               contactListParty.put("statusId", "CLPT_ACCEPTED");
               contactListParty.put("preferredContactMechId",primcontactMechId);
               contactListParty.create();
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
}
