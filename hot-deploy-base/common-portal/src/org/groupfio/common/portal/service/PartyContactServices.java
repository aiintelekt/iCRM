
package org.groupfio.common.portal.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilMessage;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.common.portal.util.DataUtil;
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
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
/**
 * PartyContact services. The service documentation is in services_party.xml.
 */
public class PartyContactServices {

	public static final String MODULE = PartyContactServices.class.getName();
	public static final String crmResource = "crmUiLabels";
	public static Map < String, Object > createBasicContactInfoForParty(DispatchContext dctx, Map < String, ? > context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
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
				if (UtilValidate.isNotEmpty(brandCode))
					input.put("brandCode", brandCode);

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
    public static Map < String, Object > createPartyPostalAddress(DispatchContext dctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > serviceResults = null;
        Map < String, Object > results = ServiceUtil.returnSuccess();
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
        String solicitationDateStr = (String) context.get("solicitChangeDt_date");
        String solicitationTimeStr = (String) context.get("solicitChangeDt_time");
        
        if (UtilValidate.isNotEmpty(toName) || UtilValidate.isNotEmpty(attnName) || UtilValidate.isNotEmpty(address1) ||
            UtilValidate.isNotEmpty(address2) || UtilValidate.isNotEmpty(city) || UtilValidate.isNotEmpty(stateProvinceGeoId) ||
            UtilValidate.isNotEmpty(countryGeoId) || UtilValidate.isNotEmpty(postalCode) || UtilValidate.isNotEmpty(postalCodeExt)) {
            try {
            	
            	String isUspsRequired = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_USPS_ADDRACT");
				if(UtilValidate.isNotEmpty(isUspsRequired) && isUspsRequired.equals("Y")) {
					Map<String, Object> corodinate = DataHelper.getGeoCoordinateByGoogleApi(UtilMisc.toMap("delegator", delegator, "dispatcher", dispatcher, "userLogin", userLogin, "zip5", postalCode, "zip4", postalCodeExt, "city", city, "state", stateProvinceGeoId, "county", county, "address1", address1, "address2", address2, "country", countryGeoId));
					context.put("latitude", corodinate.get("latitude"));
					context.put("longitude", corodinate.get("longitude"));
				}
            	
				Timestamp solicitationDate = ParamUtil.getTimestamp(solicitationDateStr, solicitationTimeStr, "MM/dd/yyyy HH:mm");
				if(UtilValidate.isNotEmpty(solicitationDate)) {
					context.put("solicitChangeDt", solicitationDate);
				}
                serviceResults = dispatcher.runSync("createPartyPostalAddress", context);
                if (ServiceUtil.isError(serviceResults)) {
                    return serviceResults;
                }
                results = serviceResults;
            } catch (GenericServiceException e) {
                // TODO Auto-generated catch block
                Debug.log("Exception in create postal address " + e.getMessage());
            }
        }
        return results;
    }
    public static Map < String, Object > updatePostalAddressData(DispatchContext dctx, Map context) {
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
        String county = (String) context.get("county");
        //added for toname and attn name in edit address
        String toName = (String) context.get("toName");
        String attnName = (String) context.get("attnName");
        /*String isBusiness = (String) context.get("isBusiness");
        String isVacant = (String) context.get("isVacant");
        String isUspsAddrVerified = (String) context.get("isUspsAddrVerified");*/
        
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
          
          Map<String, Object> corodinate = DataHelper.getGeoCoordinate(delegator, UtilMisc.toMap("zip5", postalCode, "zip4", postalCodeExt, "city", city, "state", stateProvinceGeoId, "county", county));
          context.put("latitude", corodinate.get("latitude"));
          context.put("longitude", corodinate.get("longitude"));
          
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
    
    public static Map syncRelatedPartyAssoc(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Security security = dctx.getSecurity();
    	
    	String partyId = (String) context.get("partyId");
    	String partyRoleTypeId = (String) context.get("partyRoleTypeId");
    	
    	String partyIdFrom = (String) context.get("partyIdFrom");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		
    		if (UtilValidate.isNotEmpty(partyId)) {
    			
    			result.put("partyId", partyId);
    			
    			if (UtilValidate.isEmpty(partyRoleTypeId)) {
    				GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryOne();
    				if (UtilValidate.isNotEmpty(party)) {
    					partyRoleTypeId = party.getString("roleTypeId");
    				}
    			}
    			
    			List conditions = FastList.newInstance();
    			
				conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
				EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(
                		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
                		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
                		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, partyRoleTypeId)
                		));
                conditions.add(roleTypeCondition);
                
                //conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));
                    
                conditions.add(EntityUtil.getFilterByDateExpr());
                
                conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_DEFAULT"));
                
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                
                Set<String> fieldToSelect = UtilMisc.toSet("partyIdFrom", "partyIdTo", "roleTypeIdFrom", "roleTypeIdTo", "fromDate"); 
                fieldToSelect.add("statusId");
                fieldToSelect.add("partyRelAssocId");
                
                List<GenericValue> assocContactList = delegator.findList("PartyRelationship", mainConditons, fieldToSelect, null, null, false);
    			if (UtilValidate.isNotEmpty(assocContactList)) {
    				
    				GenericValue selectedAssocContact = null;
    				
    				// deselect previous primary contact [start]
    				for (GenericValue assocContact : assocContactList) {
    					if (!assocContact.getString("partyIdFrom").equals(partyIdFrom) 
    							&& (UtilValidate.isNotEmpty(assocContact.getString("statusId")) && assocContact.getString("statusId").equals("PARTY_DEFAULT"))) {
    						assocContact.put("statusId", null);
    						assocContact.store();
    					}
    					
    					if (assocContact.getString("partyIdFrom").equals(partyIdFrom)){
    						selectedAssocContact = assocContact;
    					}
    				}
    				// deselect previous primary contact [end]
    				
    				if (UtilValidate.isNotEmpty(selectedAssocContact)) {
    					String contactPartyId = selectedAssocContact.getString("partyIdFrom");
    					String partyRelAssocId = selectedAssocContact.getString("partyRelAssocId");
    					
    					DataUtil.relatedPartyContactAssociation(delegator, partyId, contactPartyId, "EMAIL", "PRIMARY_EMAIL", partyRelAssocId);
    					
    					DataUtil.relatedPartyContactAssociation(delegator, partyId, contactPartyId, "PHONE", "PRIMARY_PHONE", partyRelAssocId);
    					
    					// check for designation
    					
    					List conditionList = FastList.newInstance();
                    	
                    	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
                    	conditionList.add(EntityCondition.makeCondition("contactId", EntityOperator.EQUALS, contactPartyId));
                    	conditionList.add(EntityCondition.makeCondition("partyRelAssocId", EntityOperator.EQUALS, partyRelAssocId));
                    	
                    	mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        				List<GenericValue> contactDesignationList = delegator.findList("ContactDesignationAssoc", mainConditons, UtilMisc.toSet("designationName", "contactDesignationAssocId"), null, null, false);
        				if (UtilValidate.isEmpty(contactDesignationList)) {
        					GenericValue person = EntityUtil.getFirst( delegator.findByAnd("Person", UtilMisc.toMap("partyId", contactPartyId), null, false) );
        					if (UtilValidate.isNotEmpty(person) && UtilValidate.isNotEmpty(person.getString("designation"))) {
        						String contactMechId = person.getString("designation");
        						GenericValue designationAssoc = delegator.makeValue("ContactDesignationAssoc", UtilMisc.toMap("partyId", partyId, "contactId", contactPartyId, "designationEnumId", contactMechId, "partyRelAssocId", partyRelAssocId));
                            	String contactDesignationAssocId = delegator.getNextSeqId("ContactDesignationAssoc");
                            	
                            	designationAssoc.put("contactDesignationAssocId", contactDesignationAssocId);
                            	
                            	//designationAssoc.put("sequenceNumber", new Long(seqId));
                            	designationAssoc.put("designationName", EnumUtil.getEnumDescription(delegator, contactMechId, "DESIGNATION"));
                            	
                            	designationAssoc.create();
        					}
        				}
    					
    				}
    			}
    			
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	
    	return result;
    	
    }
    
    public static Map syncDefaultParty(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Security security = dctx.getSecurity();
    	
    	String partyId = (String) context.get("partyId");
    	String partyRoleTypeId = (String) context.get("partyRoleTypeId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		
    		if (UtilValidate.isNotEmpty(partyId)) {
    			
    			result.put("partyId", partyId);
    			
    			if (UtilValidate.isEmpty(partyRoleTypeId)) {
    				GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryOne();
    				if (UtilValidate.isNotEmpty(party)) {
    					partyRoleTypeId = party.getString("roleTypeId");
    				}
    			}
    			
    			List conditions = FastList.newInstance();
				
				conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
				EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(
                		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
                		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
                		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, partyRoleTypeId)
                		));
                conditions.add(roleTypeCondition);
                
                //conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));
                    
                conditions.add(EntityUtil.getFilterByDateExpr());
                
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                List<GenericValue> assocContactList = delegator.findList("PartyRelationship", mainConditons, UtilMisc.toSet("partyIdFrom", "roleTypeIdFrom", "statusId"), null, null, false);
    			if (UtilValidate.isNotEmpty(assocContactList) && assocContactList.size() == 1) {
    				GenericValue primaryContact = null;
    				for (GenericValue assocContact : assocContactList) {
    					String contactId = assocContact.getString("partyIdFrom");
    					String relationshipStatusId = assocContact.getString("statusId");
    					if (UtilValidate.isNotEmpty(relationshipStatusId) && relationshipStatusId.equals("PARTY_DEFAULT")) {
    						primaryContact = assocContact;
    						break;
    					}
    				}
    				
    				if (UtilValidate.isEmpty(primaryContact)) {
    					GenericValue assocContact = assocContactList.get(0);
    					Map<String, Object> callContext = new LinkedHashMap<String, Object>();
                		callContext.put("partyId", partyId);
                		callContext.put("roleTypeIdTo", partyRoleTypeId);
                		callContext.put("partyIdFrom", assocContact.getString("partyIdFrom"));
                		callContext.put("roleTypeIdFrom", assocContact.getString("roleTypeIdFrom"));
                		callContext.put("statusId", "PARTY_DEFAULT");
                		callContext.put("isMarketable", "N");
                		callContext.put("userLogin", userLogin);
                		callResult = dispatcher.runSync("crmsfa.updateDefaultContact", callContext);
        			}
    			}
    			
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	
    	return result;
    	
    }
    public static Map < String, Object > updatePostalAddressWithPurpose(DispatchContext dctx, Map < String, ? > context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Security security = dctx.getSecurity();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Map < String, Object > serviceResults = null;
    	Map<String, Object> result = new HashMap<String, Object>();
    	String contactMechId = (String) context.get("contactMechId");
    	String deleteCMPurposeTypeIds = (String) context.get("deleteCMPurposeTypeIds_DPA");
    	try {
    		Map<String, Object> inputMap = new HashMap<String, Object>();
    		inputMap.put("partyId", context.get("partyId"));
    		inputMap.put("contactMechId", contactMechId);
    		inputMap.put("deleteCMPurposeTypeIds", deleteCMPurposeTypeIds);
    		inputMap.put("contactMechPurposeTypeId",  context.get("contactMechPurposeTypeId_APA"));
    		inputMap.put("fromDate", UtilDateTime.nowTimestamp());
    		inputMap.put("userLogin", userLogin);
    		Map<String, Object> res = dispatcher.runSync("common.updateContactMechIdWithPurpose", inputMap);
    		if (ServiceUtil.isSuccess(res)) {
    			Map<String, Object> postalAddressMap = new HashMap<String, Object>();
    			postalAddressMap.put("partyId", context.get("partyId"));
    			postalAddressMap.put("contactMechId", contactMechId);
    			postalAddressMap.put("city", context.get("city"));
    			postalAddressMap.put("county", context.get("county"));
    			postalAddressMap.put("address1",  context.get("address1"));
    			postalAddressMap.put("address2", context.get("address2"));
    			postalAddressMap.put("countryGeoId", context.get("countryGeoId"));
    			postalAddressMap.put("stateProvinceGeoId", context.get("stateProvinceGeoId"));
    			postalAddressMap.put("postalCode", context.get("postalCode"));
    			postalAddressMap.put("postalCodeExt",context.get("postalCodeExt"));
    			postalAddressMap.put("allowSolicitation", context.get("allowSolicitation"));
    			postalAddressMap.put("addressValidInd", context.get("addressValidInd"));
    			postalAddressMap.put("isBusiness", context.get("isBusiness"));
    			postalAddressMap.put("isVacant", context.get("isVacant"));
    			postalAddressMap.put("isUspsAddrVerified", context.get("isUspsAddrVerified"));
    			postalAddressMap.put("userLogin", userLogin);
    			// added for editing the toname and attnname in edit field
    			postalAddressMap.put("toName", context.get("toName"));
    			postalAddressMap.put("attnName", context.get("attnName"));
    			
    			postalAddressMap.put("ip", UtilValidate.isNotEmpty(context.get("ip")) ? (String) context.get("ip") : "");
    	        postalAddressMap.put("device", UtilValidate.isNotEmpty(context.get("device")) ? (String) context.get("device") : "");
    	        String solicitationDateStr = (String) context.get("solicitChangeDt_date");
		        String solicitationTimeStr = (String) context.get("solicitChangeDt_time");
				Timestamp solicitationDate = ParamUtil.getTimestamp(solicitationDateStr, solicitationTimeStr, "MM/dd/yyyy HH:mm");
				if(UtilValidate.isNotEmpty(solicitationDate)) {
					postalAddressMap.put("solicitChangeDt", solicitationDate);
				}
				
    			//ended
    			Map<String, Object> resPostalAddressMap = dispatcher.runSync("common.updatePostalAddressData", postalAddressMap);	
    		}else{
    			String errMsg = "Problem in updating contactMech type for Party ";
    			result.put("errMsg", errMsg);
    			return result;
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
    		return result;
    	}
    	result.put("contactMechId", contactMechId);
    	result.put(ModelService.SUCCESS_MESSAGE, "Postal Address Successfully Updated");
    	return result;
    } 
     
    public static Map < String, Object > updateEmailWithPurpose(DispatchContext dctx, Map < String, ? > context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Security security = dctx.getSecurity();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> result = new HashMap<String, Object>();
    	String contactMechId = (String) context.get("contactMechId");
    	Map<String, Object> resEmailMap = new HashMap<String, Object>();
    	String deleteCMPurposeTypeIds = (String) context.get("deleteCMPurposeTypeIds_DPA");
    	try {
    		Map<String, Object> inputMap = new HashMap<String, Object>();
    		inputMap.put("partyId", context.get("partyId"));
    		inputMap.put("contactMechId", contactMechId);
    		inputMap.put("deleteCMPurposeTypeIds", deleteCMPurposeTypeIds);
    		inputMap.put("contactMechPurposeTypeId",  context.get("contactMechPurposeTypeId_APA"));
    		inputMap.put("fromDate", UtilDateTime.nowTimestamp());
    		inputMap.put("userLogin", userLogin);
    		Map<String, Object> res = dispatcher.runSync("common.updateContactMechIdWithPurpose", inputMap);
    		if (ServiceUtil.isSuccess(res)) {
    			Map<String, Object> emailUpdateMap = new HashMap<String, Object>();
    			emailUpdateMap.put("partyId", context.get("partyId"));
    			emailUpdateMap.put("contactMechId", contactMechId);
    			emailUpdateMap.put("emailAddress", context.get("emailAddress"));
    			emailUpdateMap.put("allowSolicitation", context.get("allowSolicitation"));
    			emailUpdateMap.put("userLogin", userLogin);
    			resEmailMap = dispatcher.runSync("updatePartyEmailAddress", emailUpdateMap);	
    		}else{
    			String errMsg = "Problem in updating contactMech type for Party ";
    			result.put("errMsg", errMsg);
    			return result;
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
    		return result;
    	}
    	result.put("contactMechId", contactMechId);
    	result.put("oldContactMechId", resEmailMap.get("oldContactMechId"));
    	result.put(ModelService.SUCCESS_MESSAGE, "Email Address Successfully Updated");
    	return result;
    } 
    public static Map < String, Object > updateTelecomNumberWithPurpose(DispatchContext dctx, Map < String, ? > context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Security security = dctx.getSecurity();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> result = new HashMap<String, Object>();
    	String contactMechId = (String) context.get("contactMechId");
    	String deleteCMPurposeTypeIds = (String) context.get("deleteCMPurposeTypeIds_DPA");
    	Debug.log("context in parent====="+context);
    	try {
    		Map<String, Object> inputMap = new HashMap<String, Object>();
    		inputMap.put("partyId", context.get("partyId"));
    		inputMap.put("contactMechId", contactMechId);
    		inputMap.put("deleteCMPurposeTypeIds", deleteCMPurposeTypeIds);
    		inputMap.put("contactMechPurposeTypeId",  context.get("contactMechPurposeTypeId_APA"));
    		inputMap.put("fromDate", UtilDateTime.nowTimestamp());
    		inputMap.put("userLogin", userLogin);
    		Map<String, Object> res = dispatcher.runSync("common.updateContactMechIdWithPurpose", inputMap);
    		if (ServiceUtil.isSuccess(res)) {
    			Map<String, Object> telecomUpdateMap = new HashMap<String, Object>();
    			telecomUpdateMap.put("partyId", context.get("partyId"));
    			telecomUpdateMap.put("contactMechId", contactMechId);
    			telecomUpdateMap.put("contactNumber", context.get("contactNumber"));
    			telecomUpdateMap.put("extension", context.get("extension"));
    			if(UtilValidate.isNotEmpty(context.get("countryCode")))
    				telecomUpdateMap.put("countryCode", context.get("countryCode"));
    			if(UtilValidate.isNotEmpty(context.get("areaCode")))
    				telecomUpdateMap.put("areaCode", context.get("areaCode"));
    			telecomUpdateMap.put("allowSolicitation", context.get("allowSolicitation"));
    			telecomUpdateMap.put("phoneValidInd", context.get("phoneValidInd"));
    			if(UtilValidate.isNotEmpty(context.get("dndStatus"))) {
    				telecomUpdateMap.put("dndStatus", context.get("dndStatus"));
    			}
    			telecomUpdateMap.put("userLogin", userLogin);
    			Map<String, Object> resTelecomMap = dispatcher.runSync("common.updatePartyTNExtForDndValidation", telecomUpdateMap);	
    		}else{
    			String errMsg = "Problem in updating contactMech type for Party ";
    			result.put("errMsg", errMsg);
    			return result;
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
    		return result;
    	}
    	result.put("contactMechId", contactMechId);
    	result.put(ModelService.SUCCESS_MESSAGE, "Telecom Number Successfully Updated");
    	return result;
    }
    
    public static Map < String, Object > updateWebContactMechWithPurpose(DispatchContext dctx, Map < String, ? > context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Security security = dctx.getSecurity();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> result = new HashMap<String, Object>();
    	String contactMechId = (String) context.get("contactMechId");
    	String deleteCMPurposeTypeIds = (String) context.get("deleteCMPurposeTypeIds_DPA");
    	try {
    		Map<String, Object> inputMap = new HashMap<String, Object>();
    		inputMap.put("partyId", context.get("partyId"));
    		inputMap.put("contactMechId", contactMechId);
    		inputMap.put("deleteCMPurposeTypeIds", deleteCMPurposeTypeIds);
    		inputMap.put("contactMechPurposeTypeId",  context.get("contactMechPurposeTypeId_APA"));
    		inputMap.put("fromDate", UtilDateTime.nowTimestamp());
    		inputMap.put("userLogin", userLogin);
    		Map<String, Object> res = dispatcher.runSync("common.updateContactMechIdWithPurpose", inputMap);
    		if (ServiceUtil.isSuccess(res)) {
    			Map<String, Object> webUrlUpdateMap = new HashMap<String, Object>();
    			webUrlUpdateMap.put("partyId", context.get("partyId"));
    			webUrlUpdateMap.put("contactMechId", contactMechId);
    			webUrlUpdateMap.put("contactMechTypeId", context.get("contactMechTypeId"));
    			webUrlUpdateMap.put("allowSolicitation", context.get("allowSolicitation"));
    			webUrlUpdateMap.put("userLogin", userLogin);
    			Map<String, Object> resWebUrlMap = dispatcher.runSync("updatePartyContactMech", webUrlUpdateMap);	
    		}else{
    			String errMsg = "Problem in updating contactMech type for Party ";
    			result.put("errMsg", errMsg);
    			return result;
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
    		return result;
    	}
    	result.put("contactMechId", contactMechId);
    	result.put(ModelService.SUCCESS_MESSAGE, "Web Url Successfully Updated");
    	return result;
    } 
    
    public static Map < String, Object > updateSocialMediaTypeWebmWithPurpose(DispatchContext dctx, Map < String, ? > context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Security security = dctx.getSecurity();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> resWebUrlMap = new HashMap<String, Object>();
    	String contactMechId = (String) context.get("contactMechId");
    	String deleteCMPurposeTypeIds = (String) context.get("deleteCMPurposeTypeIds_DPA");
    	String oldContactMechId = null;
    	try {
    		Map<String, Object> inputMap = new HashMap<String, Object>();
    		inputMap.put("partyId", context.get("partyId"));
    		inputMap.put("contactMechId", contactMechId);
    		inputMap.put("deleteCMPurposeTypeIds", deleteCMPurposeTypeIds);
    		inputMap.put("contactMechPurposeTypeId",  context.get("contactMechPurposeTypeId_APA"));
    		inputMap.put("fromDate", UtilDateTime.nowTimestamp());
    		inputMap.put("userLogin", userLogin);
    		Map<String, Object> res = dispatcher.runSync("common.updateContactMechIdWithPurpose", inputMap);
    		if (ServiceUtil.isSuccess(res)) {
    			Map<String, Object> socialMediaUpdateMap = new HashMap<String, Object>();
    			socialMediaUpdateMap.put("partyId", context.get("partyId"));
    			socialMediaUpdateMap.put("contactMechId", contactMechId);
    			socialMediaUpdateMap.put("socialMediaId", context.get("socialMediaId"));
    			socialMediaUpdateMap.put("allowSolicitation", context.get("allowSolicitation"));
    			socialMediaUpdateMap.put("userLogin", userLogin);
    			resWebUrlMap = dispatcher.runSync("updatePartySocialMediaType", socialMediaUpdateMap);	
    			
    		}else{
    			String errMsg = "Problem in updating contactMech type for Party ";
    			result.put("errMsg", errMsg);
    			return result;
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
    		return result;
    	}
    	result.put("contactMechId", contactMechId);
    	result.put("oldContactMechId", resWebUrlMap.get("oldContactMechId"));
    	result.put(ModelService.SUCCESS_MESSAGE, "Web Url Successfully Updated");
    	return result;
    } 
    
    // Method to update contactmechId with purposes
    public static Map < String, Object > updateContactMechIdWithPurpose(DispatchContext dctx, Map < String, ? > context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Security security = dctx.getSecurity();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Map < String, Object > serviceResults = null;
    	Map < String, Object > results = ServiceUtil.returnSuccess();
    	Map<String, Object> res = null;
    	Map<String, Object> result = new HashMap<String, Object>();
    	String contactMechId = (String) context.get("contactMechId");
    	String deleteCMPurposeTypeIds = (String) context.get("deleteCMPurposeTypeIds");
    	String addContactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");
    	GenericValue pcmp = null;
    	try {
    		//delete selected contactmech Ids
    		Map<String, Object> inputMap = new HashMap<String, Object>();
    		Map<String, Object> addInputMap = new HashMap<String, Object>();
    		inputMap.put("partyId", context.get("partyId"));
    		inputMap.put("contactMechId", contactMechId);
    		inputMap.put("userLogin", userLogin);
    		if(UtilValidate.isNotEmpty(deleteCMPurposeTypeIds)) {
    			List<String> partyDeleteContactMechIds = Arrays.asList(deleteCMPurposeTypeIds.split("\\s*,\\s*"));
    			for(int i=0;i<partyDeleteContactMechIds.size();i++) {
    				String deleteContactMechPurposeTypeId = partyDeleteContactMechIds.get(i);
    				pcmp = EntityQuery.use(delegator).from("PartyContactMechPurpose")
    						.where("partyId", context.get("partyId"), "contactMechPurposeTypeId", deleteContactMechPurposeTypeId, "contactMechId", contactMechId)
    						.filterByDate("fromDate","thruDate")
    						.queryFirst();
    				inputMap.put("contactMechPurposeTypeId", deleteContactMechPurposeTypeId);
    				inputMap.put("fromDate", pcmp.get("fromDate"));
    				res = dispatcher.runSync("deletePartyContactMechPurpose", inputMap);
    			}
    		}
    		//Add contactmech purpose to contactMechId
    		GenericValue tempVal = null;

    		if(UtilValidate.isNotEmpty(addContactMechPurposeTypeId)) {

    			tempVal = EntityQuery.use(delegator).from("PartyContactWithPurpose")
    					.where("partyId", context.get("partyId"), "contactMechId", contactMechId, "contactMechPurposeTypeId", addContactMechPurposeTypeId)
    					.filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
    					.queryFirst();
    			//make result success if already there is purpose for this contactMechId
    			if (tempVal != null) {
    				results.put("contactMechId", contactMechId);
    			} 
    			else {
    				addInputMap.put("partyId", context.get("partyId"));
    				addInputMap.put("contactMechId", contactMechId);
    				addInputMap.put("userLogin", userLogin);
    				addInputMap.put("contactMechPurposeTypeId", addContactMechPurposeTypeId);
    				addInputMap.put("fromDate", context.get("fromDate"));
    				res = dispatcher.runSync("createPartyContactMechPurpose", addInputMap);
    				if (ServiceUtil.isSuccess(res)) {
    					result = (Map<String, Object>) res.get("result");
    				}else{
    					String errMsg = "Problem in create contactMech type for Party!";
    					result.put("errMsg", errMsg);
    					return result;
    				}
    			}
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
    		return result;
    	}
    	results.put("contactMechId", contactMechId);
    	return results;
    } 
 // Method to update contactmechId with purposes
    public static Map < String, Object > editCustomer(DispatchContext dctx, Map < String, Object > context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Security security = dctx.getSecurity();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Map < String, Object > serviceResults = null;
    	Map < String, Object > results = ServiceUtil.returnSuccess();
    	Map<String, Object> res = null;
    	Map<String, Object> result = new HashMap<String, Object>();
    	String contactMechId = (String) context.get("contactMechId");
    	String partyId = (String) context.get("partyId");
    	String city = (String) context.get("generalCity");
    	String stateProvinceGeoId = (String) context.get("editStateProvinceGeoId");
    	String address1 = (String) context.get("generalAddress1");
    	String address2 = (String) context.get("generalAddress2");
    	String countryGeoId = (String) context.get("editCountryGeoId"); 
    	String postalCode = (String) context.get("postalCode");
    	String postalCodeExt = (String) context.get("generalPostalCodeExt");
    	String phoneNumber=(String)context.get("primaryPhoneNumber");
    	String email=(String)context.get("primaryEmail");
    	String emailMechId=(String)context.get("emailContactMechId");
    	String telePhoneMechId=(String)context.get("contactNumberContactMechId");
    	String emailSolicitation=(String)context.get("emailSolicitation");
    	String phoneSolicitation=(String)context.get("phoneSolicitation");
    	String isContractor = (String) context.get("isContractor");
    	String birthDateStr = (String) context.get("birthDate");
    	try {
    		if(UtilValidate.isNotEmpty(birthDateStr)){
    			Date birthDate = DataUtil.convertDateTimestamp(birthDateStr, null, DateTimeTypeConstant.DATE, DateTimeTypeConstant.SQL_DATE);
    			context.put("birthDate", birthDate);
    		}
    		String localTimeZone = (String) context.get("timeZoneDesc");	
    		context.put("localTimeZone", UtilValidate.isNotEmpty(localTimeZone)?localTimeZone:null);
    		
    		ModelService createPersonService = dctx.getModelService("updatePerson");
    		Map<String, Object> personContext = createPersonService.makeValid(context, ModelService.IN_PARAM);

    		Map<String, Object> createPersonResult = dispatcher.runSync("updatePerson", personContext);
    		if (ServiceUtil.isError(createPersonResult) || ServiceUtil.isFailure(createPersonResult)) {
    			return createPersonResult;
    		}
    		// create PartySupplementalData
    		GenericValue partyData = EntityQuery.use(delegator).from("PartySupplementalData").where("partyId", partyId).queryOne();
    		if(UtilValidate.isNotEmpty(partyData)) {
    			partyData.setNonPKFields(context);
    			partyData.put("supplementalPartyTypeId", null);
    			partyData.store();
    		}

    		// update party classifications 
    		String gender = (String) context.get("gender"); 
    		if(UtilValidate.isNotEmpty(gender))
    		{
    			List<GenericValue> customFieldPartyClassificationList = delegator.findByAnd("CustomFieldPartyClassification", UtilMisc.toMap("groupId","GENDER","partyId",partyId,"customFieldId",gender), null, false);
    			if(customFieldPartyClassificationList == null || customFieldPartyClassificationList.size() == 0) {
    				List<GenericValue> customFieldPartyClassificationList1 = delegator.findByAnd("CustomFieldPartyClassification", UtilMisc.toMap("groupId","GENDER","partyId",partyId), null, false);
    				if(customFieldPartyClassificationList1 != null && customFieldPartyClassificationList1.size() > 0) {
    					delegator.removeAll(customFieldPartyClassificationList1);
    				}
    				// create a new segment
    				GenericValue genderSeg = delegator.makeValue("CustomFieldPartyClassification", UtilMisc.toMap("groupId","GENDER","partyId",partyId));
    				genderSeg.set("customFieldId", gender);
    				genderSeg.create();
    			} 
    		}

    		//updating timezone
    		String timeZoneDesc = null;
    		if(UtilValidate.isNotEmpty(context.get("timeZoneDesc"))){
    			timeZoneDesc = (String)context.get("timeZoneDesc");
    		}
    		GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
    		if(UtilValidate.isNotEmpty(party)) {
    			party.put("lastModifiedDate", UtilDateTime.nowTimestamp());
    			party.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
    			party.put("timeZoneDesc", timeZoneDesc);
    			party.store();
    		}

    		//update postal address
    		if(UtilValidate.isNotEmpty(contactMechId)){
    			Map<String, Object> postalDetails = null;

    			postalDetails = dispatcher.runSync("updatePostalAddressData", UtilMisc.toMap("partyId", partyId,
    					"contactMechId", contactMechId, "userLogin", userLogin, "locale", locale, 
    					"city", city,"address1", address1,"address2", address2,"postalCode", postalCode,"postalCodeExt", postalCodeExt,"countryGeoId", countryGeoId,"stateProvinceGeoId",stateProvinceGeoId

    					));
    			if (ServiceUtil.isError(postalDetails)) {
    				return postalDetails;
    			}	 
    		}
    		//Update email
    		if(UtilValidate.isNotEmpty(emailMechId)){
    			if(UtilValidate.isNotEmpty(email)) {
    				Map<String, Object> resEmailMap = new HashMap<String, Object>();
    				Map<String, Object> emailUpdateMap = new HashMap<String, Object>();
    				emailUpdateMap.put("partyId", partyId);
    				emailUpdateMap.put("contactMechId", emailMechId);
    				emailUpdateMap.put("emailAddress", email);
    				emailUpdateMap.put("userLogin", userLogin);
    				emailUpdateMap.put("allowSolicitation", emailSolicitation);
    				resEmailMap = dispatcher.runSync("updatePartyEmailAddress", emailUpdateMap);
    				if (ServiceUtil.isError(resEmailMap)) {
    					return resEmailMap;
    				}
    			}else {
    				Map < String, Object > deleteEmail = UtilMisc.toMap("userLogin", userLogin,"partyId", partyId, "contactMechId", emailMechId);
    				Map < String, Object > serviceResultsEmail = dispatcher.runSync("deletePartyContactMech", deleteEmail);
    				if (ServiceUtil.isError(serviceResultsEmail)) {
    					return serviceResultsEmail;
    				}
    			}
    		}else{
    			if(UtilValidate.isNotEmpty(email)){
    				Map < String, Object > inputEmail = UtilMisc.toMap("userLogin", userLogin, "emailAddress", email, "partyId", partyId, "contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL", "allowSolicitation", emailSolicitation);
    				Map < String, Object > serviceResultsEmail = dispatcher.runSync("createPartyEmailAddress", inputEmail);
    				if (ServiceUtil.isError(serviceResultsEmail)) {
    					return serviceResultsEmail;
    				}
    			}
    		}
    		//Updating phone number
    		if(UtilValidate.isNotEmpty(telePhoneMechId)){
    			Map < String, Object > input = UtilMisc. < String, Object > toMap();
    			input.put("partyId", partyId);
    			input.put("contactNumber", phoneNumber);
    			input.put("userLogin", userLogin);
    			input.put("contactMechId", telePhoneMechId);
    			input.put("allowSolicitation", phoneSolicitation);
    			Map < String, Object > teleserviceResults = null;
    			teleserviceResults = dispatcher.runSync("updatePartyTelecomNumber", input);
    			if (ServiceUtil.isError(teleserviceResults)) {
    				return teleserviceResults;
    			}
    		}else{
    			if(UtilValidate.isNotEmpty(phoneNumber)){
    				Map < String, Object > inputPhone = UtilMisc.toMap("userLogin", userLogin, "contactNumber", phoneNumber, "partyId", partyId, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PRIMARY_PHONE", "allowSolicitation", phoneSolicitation);
    				Map < String, Object > serviceResultsPhone = dispatcher.runSync("createPartyTelecomNumber", inputPhone);
    				if (ServiceUtil.isError(serviceResultsPhone)) {
    					return serviceResultsPhone;
    				}
    			}
    		}
    		
    		// check for contractor[start]
        	//delegator.removeByAnd("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CONTRACTOR"));
    		if (UtilValidate.isNotEmpty(isContractor) && "Y".equals(isContractor)) {
    			//delegator.removeByAnd("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CONTRACTOR"));
    			long count = EntityQuery.use(delegator).from("PartyRole").where("partyId", partyId, "roleTypeId", "CONTRACTOR").queryCount();
    			if(count == 0) {
	    			GenericValue partyRole = delegator.makeValue("PartyRole");
	    			partyRole.put("partyId", partyId);
	    			partyRole.put("roleTypeId", "CONTRACTOR");
	    			delegator.createOrStore(partyRole);
    			}
    			partyData.put("supplementalPartyTypeId", "CONTRACTOR");
    			partyData.store();
    			
    		}
    		// check for contractor[end]
    		
    		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(crmResource, "CustomerUpdatedSuccessfully", locale));
    		results.put("partyId", partyId);
    	}catch (Exception e) {
    		e.printStackTrace();
    		return result;
    	}
    	results.put("partyId", partyId);
    	return results;
    } 
    
}