package org.groupfio.contact.portal.contactmech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

public class PartyPrimaryContactMechWorker {
	private static final String module = PartyPrimaryContactMechWorker.class.getName();
	public static Map<String, String> getPartyPrimaryContactMechValueMaps(Delegator delegator, String partyId){
    	Map<String, String> partyPrimayContactMechValues=new HashMap<String, String>();
    	
    	List<String> partyContactMechIds= new ArrayList<String>();
    	try{    		
    		List<GenericValue> partyContactMechs = delegator.findByAnd("PartyContactMech",UtilMisc.toMap("partyId",partyId),null,false);
    		partyContactMechs = EntityUtil.filterByDate(partyContactMechs);
    		
    		for(GenericValue partyContactMech:partyContactMechs){
    			if(UtilValidate.isNotEmpty(partyContactMech.getString("contactMechId"))){
    				partyContactMechIds.add(partyContactMech.getString("contactMechId"));
    			}
    		}
    		
    		Set<String> findOptions = UtilMisc.toSet("contactMechId");
    		List<String> orderBy = UtilMisc.toList("createdStamp DESC");
    		
    		EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
    		EntityCondition condition2 = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, partyContactMechIds);
    		
    		
    		EntityCondition primaryPhoneConditions= EntityCondition.makeCondition(UtilMisc.toList(condition1,condition2,EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE")));
    		List<GenericValue> primaryPhones = delegator.findList("PartyContactMechPurpose", primaryPhoneConditions, findOptions, null, null, true);
    		if(UtilValidate.isNotEmpty(primaryPhones)){
    			GenericValue primaryPhone = EntityUtil.getFirst(primaryPhones);
    			GenericValue primaryPhoneNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId",primaryPhone.getString("contactMechId")), false);
    			String phoneNumber ="";
    			String dndStatus = "N";
    			if(UtilValidate.isNotEmpty(primaryPhoneNumber)){
	    			String countryCode = primaryPhoneNumber.getString("countryCode");
	    			String areaCode = primaryPhoneNumber.getString("areaCode");
	    			String contactNumber = primaryPhoneNumber.getString("contactNumber");
	    			
	    			if(UtilValidate.isNotEmpty(primaryPhoneNumber.getString("dndStatus"))) {
	    				dndStatus = primaryPhoneNumber.getString("dndStatus");
	    			}
	    			if(countryCode!=null && areaCode!=null && contactNumber!=null){
	    				phoneNumber+=countryCode+"-"+areaCode+"-"+contactNumber;
	    		     }
	    		     else if(areaCode!=null && contactNumber!=null){
	    		    	 phoneNumber+=areaCode+"-"+contactNumber; 
	    		     }
	    		     else if(contactNumber !=null){
	    		    	 phoneNumber=contactNumber;
	    		     }
	    			GenericValue partyPhoneMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",primaryPhone.getString("contactMechId")), null, false));
	    			if(partyPhoneMech != null && partyPhoneMech.size() >0) {
	    				String phoneSolicitation = UtilValidate.isNotEmpty(partyPhoneMech.getString("allowSolicitation"))? partyPhoneMech.getString("allowSolicitation") : "N";
		    			partyPrimayContactMechValues.put("phoneSolicitation", phoneSolicitation);
	    			}
	    			
    			}
    			partyPrimayContactMechValues.put("dndStatus", dndStatus);
    			partyPrimayContactMechValues.put("PrimaryPhone", phoneNumber);
    		}
    		
    		EntityCondition mobilePhoneConditions= EntityCondition.makeCondition(UtilMisc.toList(condition1,condition2,EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PHONE_MOBILE")));
    		List<GenericValue> mobilePhones = delegator.findList("PartyContactMechPurpose", mobilePhoneConditions, findOptions, null, null, false);
    		if(UtilValidate.isNotEmpty(mobilePhones)){
    			GenericValue mobilePhone = EntityUtil.getFirst(mobilePhones);
    			GenericValue mobilePhoneNumber = delegator.findOne("TelecomNumber",UtilMisc.toMap("contactMechId",mobilePhone.getString("contactMechId")),false);
    			String phoneNumber ="";
    			if(UtilValidate.isNotEmpty(mobilePhoneNumber)){
	    			String countryCode = mobilePhoneNumber.getString("countryCode");
	    			String areaCode = mobilePhoneNumber.getString("areaCode");
	    			String contactNumber = mobilePhoneNumber.getString("contactNumber");
	    			if(countryCode!=null && areaCode!=null && contactNumber!=null){
	    				phoneNumber+=countryCode+"-"+areaCode+"-"+contactNumber;
	    		     }
	    		     else if(areaCode!=null && contactNumber!=null){
	    		    	 phoneNumber+=areaCode+"-"+contactNumber; 
	    		     }
	    			GenericValue partyMobileMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",mobilePhone.getString("contactMechId")), null, false));
	    			if(partyMobileMech != null && partyMobileMech.size() >0) {
	    				String mobileSolicitation = UtilValidate.isNotEmpty(partyMobileMech.getString("allowSolicitation"))? partyMobileMech.getString("allowSolicitation") : "N";
		    			partyPrimayContactMechValues.put("mobileSolicitation", mobileSolicitation);
	    			}
	    			
	    			partyPrimayContactMechValues.put("MobilePhone", phoneNumber);
    			}
    			
    		}
    		
    		EntityCondition secondaryPhoneConditions= EntityCondition.makeCondition(UtilMisc.toList(condition1,condition2,EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PHONE_WORK_SEC")));
    		List<GenericValue> secondaryPhones = delegator.findList("PartyContactMechPurpose", secondaryPhoneConditions, findOptions, null, null, false);
    		if(UtilValidate.isNotEmpty(secondaryPhones)){
    			GenericValue secondaryPhone = EntityUtil.getFirst(secondaryPhones);
    			GenericValue secondaryPhoneNumber = delegator.findOne("TelecomNumber",UtilMisc.toMap("contactMechId",secondaryPhone.getString("contactMechId")),false);
    			String phoneNumber ="";
    			if(UtilValidate.isNotEmpty(secondaryPhoneNumber)){
	    			String countryCode = secondaryPhoneNumber.getString("countryCode");
	    			String areaCode = secondaryPhoneNumber.getString("areaCode");
	    			String contactNumber = secondaryPhoneNumber.getString("contactNumber");
	    			
	    			if(countryCode!=null && areaCode!=null && contactNumber!=null){
	    				phoneNumber+=countryCode+"-"+areaCode+"-"+contactNumber;
	    		     }
	    		     else if(areaCode!=null && contactNumber!=null){
	    		    	 phoneNumber+=areaCode+"-"+contactNumber; 
	    		     }
	    		     else if(contactNumber !=null){
	    		    	 phoneNumber=contactNumber;
	    		     }
	    			GenericValue partySecPhoneMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",secondaryPhone.getString("contactMechId")), null, false));
	    			if(partySecPhoneMech != null && partySecPhoneMech.size() >0) {
	    				String secondaryPhoneSolicitation = UtilValidate.isNotEmpty(partySecPhoneMech.getString("allowSolicitation"))? partySecPhoneMech.getString("allowSolicitation") : "N";
		    			partyPrimayContactMechValues.put("secondaryPhoneSolicitation", secondaryPhoneSolicitation);
	    			}
	    			partyPrimayContactMechValues.put("SecondaryPhone", phoneNumber);
    			}
    			
    		}
    		
    		EntityCondition primaryEmailaddressConditions= EntityCondition.makeCondition(UtilMisc.toList(condition1,condition2,EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL")));
    		List<GenericValue> emailAddresses = delegator.findList("PartyContactMechPurpose", primaryEmailaddressConditions, findOptions, null, null, false);
    		if(UtilValidate.isNotEmpty(emailAddresses)){
    			GenericValue emailAddress = EntityUtil.getFirst(emailAddresses);
    			GenericValue emailAddressValue = delegator.findOne("ContactMech",UtilMisc.toMap("contactMechId",emailAddress.getString("contactMechId")),false);
    			String emailAddressId = "";
    			if(UtilValidate.isNotEmpty(emailAddressValue) && UtilValidate.isNotEmpty(emailAddressValue.getString("infoString"))){
    				emailAddressId=emailAddressValue.getString("infoString");
    			}
    			
    			partyPrimayContactMechValues.put("EmailAddress", emailAddressId);
    			
    			GenericValue partyEmailMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",emailAddress.getString("contactMechId")), null, false));
    			if(partyEmailMech != null && partyEmailMech.size() > 0) {
	    			String emailSolicitation = UtilValidate.isNotEmpty(partyEmailMech.getString("allowSolicitation"))? partyEmailMech.getString("allowSolicitation") : "N";
	    			partyPrimayContactMechValues.put("emailSolicitation", emailSolicitation);
    			}
    		}
    		EntityCondition skypeConditions= EntityCondition.makeCondition(UtilMisc.toList(condition1,condition2,EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_SKYPE")));
    		List<GenericValue> skypes = delegator.findList("PartyContactMechPurpose", skypeConditions, findOptions, null, null, false);
    		if(UtilValidate.isNotEmpty(skypes)){
    			GenericValue skype = EntityUtil.getFirst(skypes);
    			GenericValue skypeValue = delegator.findOne("ContactMech",UtilMisc.toMap("contactMechId",skype.getString("contactMechId")),false);
    			String skypeId = "";
    			if(UtilValidate.isNotEmpty(skypeValue) && UtilValidate.isNotEmpty(skypeValue.getString("infoString"))){
    					skypeId=skypeValue.getString("infoString");
    			}
    			partyPrimayContactMechValues.put("SkypeId", skypeId);
    			
    			GenericValue partySkypeMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",skype.getString("contactMechId")), null, false));
    			if(partySkypeMech != null && partySkypeMech.size() > 0) {
    				String skypeSolicitation = UtilValidate.isNotEmpty(partySkypeMech.getString("allowSolicitation"))? partySkypeMech.getString("allowSolicitation") : "N";
        			partyPrimayContactMechValues.put("skypeSolicitation", skypeSolicitation);
    			}
    		}
    		
    		EntityCondition webUrlConditions= EntityCondition.makeCondition(UtilMisc.toList(condition1,condition2,EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_WEB_URL")));
    		List<GenericValue> webUrls = delegator.findList("PartyContactMechPurpose", webUrlConditions, findOptions, null, null, false);
    		if(UtilValidate.isNotEmpty(webUrls)){
    			GenericValue webUrl = EntityUtil.getFirst(webUrls);
    			GenericValue webUrlValue = delegator.findOne("ContactMech",UtilMisc.toMap("contactMechId",webUrl.getString("contactMechId")),false);
    			String webURL = "";
    			if(UtilValidate.isNotEmpty(webUrlValue) && UtilValidate.isNotEmpty(webUrlValue.getString("infoString"))){
    					webURL=webUrlValue.getString("infoString");
    			}
    			partyPrimayContactMechValues.put("webURL", webURL);
    			
    			GenericValue partyWebURLeMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",webUrl.getString("contactMechId")), null, false));
    			if(partyWebURLeMech != null && partyWebURLeMech.size() >0) {
    				String webURLSolicitation = UtilValidate.isNotEmpty(partyWebURLeMech.getString("allowSolicitation"))? partyWebURLeMech.getString("allowSolicitation") : "N";
        			partyPrimayContactMechValues.put("webURLSolicitation", webURLSolicitation);
    			}	
    		}
    		
    		
    	}catch(GenericEntityException ge){
    		Debug.logInfo("Error: "+ge.getMessage(), module);
    	}
    	
    	return partyPrimayContactMechValues;
    }
	
	public static GenericValue getPartyPrimaryPostal(Delegator delegator, String partyId) {
		List<String> partyContactMechIds= new ArrayList<String>();
		GenericValue postalAddress = null;
		try{
			List<GenericValue> partyContactMechs = delegator.findByAnd("PartyContactMech",UtilMisc.toMap("partyId",partyId),null,true);
			partyContactMechs = EntityUtil.filterByDate(partyContactMechs);

			for(GenericValue partyContactMech:partyContactMechs){
				if(UtilValidate.isNotEmpty(partyContactMech.getString("contactMechId"))){
					partyContactMechIds.add(partyContactMech.getString("contactMechId"));
				}
			}

			Set<String> findOptions = UtilMisc.toSet("contactMechId");
			List<String> orderBy = UtilMisc.toList("createdStamp DESC");

			EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
			EntityCondition condition2 = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, partyContactMechIds);

			EntityCondition postalAddressConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION")));
			List < GenericValue > primaryAddressList = delegator.findList("PartyContactMechPurpose", postalAddressConditions, findOptions, null, null, false);
			if (primaryAddressList != null && primaryAddressList.size() > 0) {
				GenericValue primaryAddress = EntityUtil.getFirst(EntityUtil.filterByDate(primaryAddressList));
				if (UtilValidate.isNotEmpty(primaryAddress)) {
					postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", primaryAddress.getString("contactMechId")), false);
				}
			}
		} catch (GenericEntityException e) {
			Debug.logInfo("Error: "+e.getMessage(), module);
		}
		return postalAddress;
	}
	
	
}
