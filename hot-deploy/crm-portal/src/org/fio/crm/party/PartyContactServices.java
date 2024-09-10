
package org.fio.crm.party;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

import org.fio.crm.util.DataUtil;
import org.fio.crm.util.UtilMessage;
import org.groupfio.common.portal.util.DataHelper;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityQuery;
/**
 * PartyContact services. The service documentation is in services_party.xml.
 */
public class PartyContactServices {

	public static final String MODULE = PartyContactServices.class.getName();
	public static final String crmResource = "crmUiLabels";
	public static Map < String, Object > createBasicContactInfoForParty(DispatchContext dctx, Map < String, ? > context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map < String, Object > serviceResults = null; // for collecting service results
		Map < String, Object > results = ServiceUtil.returnSuccess(); // for returning the contact mech IDs when finished

		// security
		/*if (!security.hasEntityPermission("PARTYMGR", "_PCM_CREATE", userLogin)) {
		   return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
		  }*/

		// input
		String partyId = (String) context.get("partyId");
		String primaryEmail = (String) context.get("primaryEmail");
		String primaryWebUrl = (String) context.get("primaryWebUrl");
		String primaryPhoneCountryCode = (String) context.get("primaryPhoneCountryCode");
		String primaryPhoneAreaCode = (String) context.get("primaryPhoneAreaCode");
		String primaryPhoneNumber = (String) context.get("primaryPhoneNumber");
		String primaryPhoneExtension = (String) context.get("primaryPhoneExtension");
		String primaryPhoneAskForName = (String) context.get("primaryPhoneAskForName");
		
		String generalToName = (String) context.get("generalToName");
		String generalAttnName = (String) context.get("generalAttnName");
		String generalAddress1 = (String) context.get("generalAddress1");
		String generalAddress2 = (String) context.get("generalAddress2");
		String generalCity = (String) context.get("generalCity");
		String generalStateProvinceGeoId = (String) context.get("generalStateProvinceGeoId");
		String generalPostalCode = (String) context.get("generalPostalCode");
		String generalPostalCodeExt = (String) context.get("generalPostalCodeExt");
		String generalCountryGeoId = (String) context.get("generalCountryGeoId");
		
		String county = (String) context.get("countyGeoId");
		String isBusiness = (String) context.get("isBusiness");
		String isVacant = (String) context.get("isVacant");
		String isUspsAddrVerified = (String) context.get("isUspsAddrVerified");

		String allowSolicitation = (String) context.get("allowSolicitation");
		String brandCode = (String) context.get("brandCode");
		
		String postalSolicitation = UtilValidate.isNotEmpty((String) context.get("postalSolicitation")) ? (String) context.get("postalSolicitation") : "Y";
		String emailSolicitation = UtilValidate.isNotEmpty((String) context.get("emailSolicitation")) ? (String) context.get("emailSolicitation") : "Y";
		String phoneSolicitation = UtilValidate.isNotEmpty((String) context.get("phoneSolicitation")) ? (String) context.get("phoneSolicitation") : "Y";
		
		String postalPurposeTypeId = UtilValidate.isNotEmpty((String) context.get("postalPurposeTypeId")) ? (String) context.get("postalPurposeTypeId") : "PRIMARY_LOCATION";
		String emailPurposeTypeId = UtilValidate.isNotEmpty((String) context.get("emailPurposeTypeId")) ? (String) context.get("emailPurposeTypeId") : "PRIMARY_EMAIL";
		String phonePurposeTypeId = UtilValidate.isNotEmpty((String) context.get("phonePurposeTypeId")) ? (String) context.get("phonePurposeTypeId") : "PRIMARY_PHONE";

		try {
			// create primary email
			if ((primaryEmail != null) && !primaryEmail.equals("")) {
				serviceResults = dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin,
						"contactMechPurposeTypeId", emailPurposeTypeId, "emailAddress", primaryEmail, "allowSolicitation", emailSolicitation, "brandCode", brandCode));
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
				results.put("primaryEmailContactMechId", serviceResults.get("contactMechId"));
			}

			// create primary web url
			if ((primaryWebUrl != null) && !primaryWebUrl.equals("")) {
				serviceResults = dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin,
						"contactMechTypeId", "WEB_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_WEB_URL", "infoString", primaryWebUrl));
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
				results.put("primaryWebUrlContactMechId", serviceResults.get("contactMechId"));
			}

			// create primary telecom number
			if (UtilValidate.isNotEmpty(primaryPhoneNumber)) {
				
				Map < String, Object > input = UtilMisc. < String, Object > toMap("partyId", partyId, "userLogin", userLogin, "contactMechPurposeTypeId", phonePurposeTypeId, "allowSolicitation", phoneSolicitation);
				input.put("countryCode", primaryPhoneCountryCode);
				input.put("areaCode", primaryPhoneAreaCode);
				input.put("contactNumber", primaryPhoneNumber);
				input.put("extension", primaryPhoneExtension);
				input.put("askForName", primaryPhoneAskForName);
				if (UtilValidate.isNotEmpty(brandCode))
					input.put("brandCode", brandCode);
				serviceResults = dispatcher.runSync("createPartyTelecomNumber", input);
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
				results.put("primaryPhoneContactMechId", serviceResults.get("contactMechId"));
			}

			// create general correspondence postal address
			if (UtilValidate.isNotEmpty(generalToName) || UtilValidate.isNotEmpty(generalAttnName) || UtilValidate.isNotEmpty(generalAddress1) 
					|| UtilValidate.isNotEmpty(generalAddress2) || UtilValidate.isNotEmpty(generalCity) || UtilValidate.isNotEmpty(generalStateProvinceGeoId) 
					|| UtilValidate.isNotEmpty(generalCountryGeoId) || UtilValidate.isNotEmpty(generalPostalCode) || UtilValidate.isNotEmpty(generalPostalCodeExt)) {
				
				Map < String, Object > input = UtilMisc. < String, Object > toMap("partyId", partyId, "userLogin", userLogin, "contactMechPurposeTypeId", "GENERAL_LOCATION");
				input.put("toName", generalToName);
				input.put("attnName", generalAttnName);
				input.put("address1", generalAddress1);
				input.put("address2", generalAddress2);
				input.put("city", generalCity);
				input.put("stateProvinceGeoId", generalStateProvinceGeoId);
				if (UtilValidate.isNotEmpty(generalPostalCode)) {
				    generalPostalCode = generalPostalCode.toUpperCase();
				}
				input.put("postalCode", generalPostalCode);
				if (UtilValidate.isNotEmpty(generalPostalCodeExt)) {
					generalPostalCodeExt = generalPostalCodeExt.toUpperCase();
				}
				input.put("postalCodeExt", generalPostalCodeExt);
				input.put("countryGeoId", generalCountryGeoId);
				input.put("allowSolicitation", postalSolicitation);
				if (UtilValidate.isNotEmpty(brandCode)) {
					input.put("brandCode", brandCode);
				}
				
				input.put("county", county);
				input.put("isBusiness", isBusiness);
				input.put("isVacant", isVacant);
				input.put("isUspsAddrVerified", isUspsAddrVerified);
				
				String isUspsRequired = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_USPS_ADDRACT");
				if(UtilValidate.isNotEmpty(isUspsRequired) && isUspsRequired.equals("Y")) {
					Map<String, Object> corodinate = DataHelper.getGeoCoordinateByGoogleApi(UtilMisc.toMap("delegator", delegator, "dispatcher", dispatcher, "userLogin", userLogin, "zip5", generalPostalCode, "zip4", generalPostalCodeExt, "city", generalCity, "state", generalStateProvinceGeoId, "county", county, "address1", generalAddress1, "address2", generalAddress2, "country", generalCountryGeoId));
					input.put("latitude", corodinate.get("latitude"));
					input.put("longitude", corodinate.get("longitude"));
				}
				
				serviceResults = dispatcher.runSync("createPartyPostalAddress", input);
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
				String contactMechId = (String) serviceResults.get("contactMechId");
				results.put("generalAddressContactMechId", contactMechId);

				// also make this address the SHIP	PING_LOCATION
				// UN-COMMENTED by Prabhu
				/*input = UtilMisc. < String, Object > toMap("partyId", partyId, "userLogin", userLogin, "contactMechId", contactMechId, "contactMechPurposeTypeId", "SHIPPING_LOCATION");
				serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);*/
				//end
				input = UtilMisc. < String, Object > toMap("partyId", partyId, "userLogin", userLogin, "contactMechId", contactMechId, "contactMechPurposeTypeId", postalPurposeTypeId);
				serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
			}

		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorCreateBasicContactInfoFail", locale, MODULE);
		}
		return results;
	}
	
    public static Map < String, Object > createPartyPostalAddress(DispatchContext dctx, Map < String, ? > context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > serviceResults = null;
        Map < String, Object > results = ServiceUtil.returnSuccess();
        
        String partyId = (String) context.get("partyId");
        String toName = (String) context.get("toName");
        String attnName = (String) context.get("attnName");
        String address1 = (String) context.get("address1");
        String address2 = (String) context.get("address2");
        String city = (String) context.get("city");
        String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
        String postalCode = (String) context.get("postalCode");
        String postalCodeExt = (String) context.get("postalCodeExt");
        String countryGeoId = (String) context.get("countryGeoId");
        String county = (String) context.get("county");
        
		String isBusiness = (String) context.get("isBusiness");
		String isVacant = (String) context.get("isVacant");
		String isUspsAddrVerified = (String) context.get("isUspsAddrVerified");
        
        String postalPurposeTypeId = UtilValidate.isNotEmpty((String) context.get("postalPurposeTypeId")) ? (String) context.get("postalPurposeTypeId") : "PRIMARY_LOCATION";
        
        if (UtilValidate.isNotEmpty(toName) || UtilValidate.isNotEmpty(attnName) || UtilValidate.isNotEmpty(address1) ||
            UtilValidate.isNotEmpty(address2) || UtilValidate.isNotEmpty(city) || UtilValidate.isNotEmpty(stateProvinceGeoId) ||
            UtilValidate.isNotEmpty(countryGeoId) || UtilValidate.isNotEmpty(postalCode) || UtilValidate.isNotEmpty(postalCodeExt)) {
            try {
            	
            	Map<String, Object> callCtxt = FastMap.newInstance();
            	callCtxt.putAll(context);
            	
            	callCtxt.put("contactMechPurposeTypeId", postalPurposeTypeId);
            	
            	callCtxt.put("county", county);
            	callCtxt.put("isBusiness", isBusiness);
            	callCtxt.put("isVacant", isVacant);
            	callCtxt.put("isUspsAddrVerified", isUspsAddrVerified);
            	
            	String isUspsRequired = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_USPS_ADDRACT");
				if(UtilValidate.isNotEmpty(isUspsRequired) && isUspsRequired.equals("Y")) {
					Map<String, Object> corodinate = DataHelper.getGeoCoordinateByGoogleApi(UtilMisc.toMap("delegator", delegator, "dispatcher", dispatcher, "userLogin", userLogin, "zip5", postalCode, "zip4", postalCodeExt, "city", city, "state", stateProvinceGeoId, "county", county, "address1", address1, "address2", address2, "country", countryGeoId));
					callCtxt.put("latitude", corodinate.get("latitude"));
					callCtxt.put("longitude", corodinate.get("longitude"));
				}
            	
                serviceResults = dispatcher.runSync("createPartyPostalAddress", callCtxt);
                if (ServiceUtil.isError(serviceResults)) {
                    return serviceResults;
                }
                results = serviceResults;
                String contactMechId = (String) serviceResults.get("contactMechId");
                
                /*callCtxt = UtilMisc. < String, Object > toMap("partyId", partyId, "userLogin", userLogin, "contactMechId", contactMechId, "contactMechPurposeTypeId", postalPurposeTypeId);
				serviceResults = dispatcher.runSync("createPartyContactMechPurpose", callCtxt);
				if (ServiceUtil.isError(serviceResults)) {
					Debug.logInfo("Postal purpose type associated, postalPurposeTypeId# "+postalPurposeTypeId+", partyId#" + partyId, MODULE);
				}*/
            } catch (GenericServiceException e) {
                // TODO Auto-generated catch block
                Debug.log("Exception in create postal address " + e.getMessage());
            }
        }
        return results;
    }
    
    public static Map < String, Object > updatePostalAddressData(DispatchContext dctx, Map < String, ? > context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > serviceResults = null;
        Map < String, Object > results = ServiceUtil.returnSuccess();
        String partyId = (String) context.get("partyId");
        String contactMechId = (String) context.get("contactMechId");
        String city = (String) context.get("city");
        String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
        String address1 = (String) context.get("address1");
        String address2 = (String) context.get("address2");
        String countryGeoId = (String) context.get("countryGeoId");
        String postalCode = (String) context.get("postalCode");
        String postalCodeExt = (String) context.get("postalCodeExt");
        List<GenericValue> partyContactMechPurpose = null;
        List<String> contactMechPurposeTypeIds = null;
        try {
          if(UtilValidate.isNotEmpty(contactMechId)) {
              //partyContactMechPurpose = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId,"contactMechId",contactMechId), null, false));
              partyContactMechPurpose = EntityQuery.use(delegator).from("PartyContactMechPurpose")
                      .where(EntityCondition.makeCondition("partyId",partyId), 
                      EntityCondition.makeCondition("contactMechId", contactMechId))
                      .cache().filterByDate().queryList();
              contactMechPurposeTypeIds = EntityUtil.getFieldListFromEntityList(partyContactMechPurpose, "contactMechPurposeTypeId", true);
          }
          Debug.log("===contactMechPurposeTypeIds===="+contactMechPurposeTypeIds);
          if(UtilValidate.isNotEmpty(contactMechPurposeTypeIds) && contactMechPurposeTypeIds.contains("PRIMARY_LOCATION")) {
            GenericValue partySupplementalData = delegator.findOne("PartySupplementalData", UtilMisc.toMap("partyId", partyId),false);
            if(UtilValidate.isNotEmpty(partySupplementalData)) {
                partySupplementalData.set("stateProvinceGeoId", stateProvinceGeoId);
                partySupplementalData.set("address1", address1);
                partySupplementalData.set("address2", address2);
                partySupplementalData.set("city", city);
                partySupplementalData.set("postalCode", postalCode);
                partySupplementalData.store();
            }
            GenericValue dataImportLead = EntityUtil.getFirst(delegator.findByAnd("DataImportLead", UtilMisc.toMap("primaryPartyId", partyId), null, false));
            if(UtilValidate.isNotEmpty(dataImportLead)) {
                dataImportLead.set("stateProvinceGeoId", stateProvinceGeoId);
                dataImportLead.set("address1", address1);
                dataImportLead.set("address2", address2);
                dataImportLead.set("city", city);
                dataImportLead.set("postalCode", postalCode);
                dataImportLead.set("postalCodeExt", postalCodeExt);
                dataImportLead.store();
            }
          }
          serviceResults = dispatcher.runSync("updatePartyPostalAddress", context);
          if (ServiceUtil.isError(serviceResults)) {
              return serviceResults;
          }
          results = serviceResults;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Debug.log("Exception in Update postal address " + e.getMessage());
        }
        results.put("contactMechId", contactMechId);
        return results;
    }
    
    public static Map < String, Object > createPartyTNExtForDndValidation(DispatchContext dctx, Map < String, ? > context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map < String, Object > results = ServiceUtil.returnSuccess();
        Map < String, Object > serviceResults = null;
        String partyId = (String) context.get("partyId");
        String contactNumber = (String) context.get("contactNumber");
        try {
        if(UtilValidate.isNotEmpty(contactNumber)) {
            Map<String, Object> dndStatusMp = DataUtil.getDndStatus(delegator, contactNumber);
            String dndIndicator = (String) dndStatusMp.get("dndIndicator");
            String dndStatus = (String) dndStatusMp.get("dndStatus");
            String dndSeqId = (String) dndStatusMp.get("dndSeqId");
            String solicitationStatus = (String) dndStatusMp.get("solicitationStatus");
            if(UtilValidate.isNotEmpty(contactNumber)){
                
                Map < String, Object > input = UtilMisc. < String, Object > toMap();
                input.putAll(context);
                input.put("dndStatus", dndStatus);
                if("Y".equals(dndStatus)) {
                    input.put("allowSolicitation", solicitationStatus);
                }
                serviceResults = dispatcher.runSync("createPartyTelecomNumber", input);

                if (ServiceUtil.isError(serviceResults)) {
                    return serviceResults;
                }
                results = serviceResults;
                Boolean validateDndAuditLogDetails = DataUtil.validateDndAuditLogDetails(delegator, contactNumber, partyId, dndIndicator);
                if(UtilValidate.isNotEmpty(dndSeqId) && UtilValidate.isNotEmpty(dndIndicator) && validateDndAuditLogDetails) {
                    delegator.create(DataUtil.makeDndAuditLogDetails(dndSeqId, partyId, "CREATE", contactNumber, dndIndicator, UtilDateTime.nowTimestamp(), delegator));
                }
            }
            
        }
        } catch (Exception e) {
            Debug.log("Exception in createPartyTNExtForDndValidation " + e.getMessage());
        }
        
        return results;
    }
    
    public static Map < String, Object > updatePartyTNExtForDndValidation(DispatchContext dctx, Map < String, ? > context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map < String, Object > results = new HashMap<String, Object>();
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > serviceResults = null;
        String partyId = (String) context.get("partyId");
        String contactMechId = (String) context.get("contactMechId");
        String contactNumber = (String) context.get("contactNumber");
        String allowSolicitation = (String) context.get("allowSolicitation");
        try {
        if(UtilValidate.isNotEmpty(contactNumber)) {
            Map<String, Object> dndStatusMp = DataUtil.getDndStatus(delegator, contactNumber);
            String dndIndicator = (String) dndStatusMp.get("dndIndicator");
            String dndStatus = (String) dndStatusMp.get("dndStatus");
            String dndSeqId = (String) dndStatusMp.get("dndSeqId");
            if("Y".equals(dndStatus) && "Y".equals(allowSolicitation)) {
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(crmResource,
                        "cannotChangeDndSolicitationStatus", locale));
                results.put("contactMechId", contactMechId);
                return results;
            } else {
                Map < String, Object > input = UtilMisc. < String, Object > toMap();
                input.putAll(context);
                input.put("dndStatus", dndStatus);
                serviceResults = dispatcher.runSync("updatePartyTelecomNumber", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return serviceResults;
                }
                results = serviceResults;
                Boolean validateDndAuditLogDetails = DataUtil.validateDndAuditLogDetails(delegator, contactNumber, partyId, dndIndicator);
                if(UtilValidate.isNotEmpty(dndSeqId) && UtilValidate.isNotEmpty(dndIndicator) && validateDndAuditLogDetails) {
                    delegator.create(DataUtil.makeDndAuditLogDetails(dndSeqId, partyId, "UPDATE", contactNumber, dndIndicator, UtilDateTime.nowTimestamp(), delegator));
                }
            }
            
        }
        } catch (Exception e) {
            Debug.log("Exception in updatePartyTNExtForDndValidation " + e.getMessage());
        }
        
        return results;
    }
    
}