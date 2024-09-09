package org.groupfio.account.portal.contactmech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	
	public static Map<String, String> getPartyPrimaryContactMechValueMaps(Delegator delegator, String partyId) {
		return getPartyPrimaryContactMechValueMaps(delegator, partyId, null);
	}
	
	public static Map<String, String> getPartyPrimaryContactMechValueMaps(Delegator delegator, String partyId, Map<String, Object> context, boolean isRetriveMinimized) {
		if (UtilValidate.isEmpty(context)) {
			context = new HashMap<>();
		}

		if (isRetriveMinimized) {
			context.put("isRetrivePhone", context.containsKey("isRetrivePhone") ? context.get("isRetrivePhone") : false);
			context.put("isRetriveMobile", context.containsKey("isRetriveMobile") ? context.get("isRetriveMobile") : false);
			context.put("isRetriveWorkPhone", context.containsKey("isRetriveWorkPhone") ? context.get("isRetriveWorkPhone") : false);
			context.put("isRetriveEmail", context.containsKey("isRetriveEmail") ? context.get("isRetriveEmail") : false);
			context.put("isRetriveSkype", context.containsKey("isRetriveSkype") ? context.get("isRetriveSkype") : false);
			context.put("isRetriveWebAddress", context.containsKey("isRetriveWebAddress") ? context.get("isRetriveWebAddress") : false);
		}
		return getPartyPrimaryContactMechValueMaps(delegator, partyId, context);
	}
	
	public static Map<String, String> getPartyPrimaryContactMechValueMaps(Delegator delegator, String partyId, Map<String, Object> context) {
		
    	Map<String, String> partyPrimayContactMechValues=new HashMap<String, String>();
    	
    	List<String> partyContactMechIds= new ArrayList<String>();
    	try{    	
    		
    		boolean isRetrivePhone = true;
    		boolean isRetriveMobile = true;
    		boolean isRetriveWorkPhone = true;
    		boolean isRetriveEmail = true;
    		boolean isRetriveSkype = true;
    		boolean isRetriveWebAddress = true;
    		
    		if (UtilValidate.isNotEmpty(context)) {
    			isRetrivePhone = context.containsKey("isRetrivePhone") ? Boolean.valueOf(context.get("isRetrivePhone").toString()) : true;
        		isRetriveMobile = context.containsKey("isRetriveMobile") ? Boolean.valueOf(context.get("isRetriveMobile").toString()) : true;
        		isRetriveWorkPhone = context.containsKey("isRetriveWorkPhone") ? Boolean.valueOf(context.get("isRetriveWorkPhone").toString()) : true;
        		isRetriveEmail = context.containsKey("isRetriveEmail") ? Boolean.valueOf(context.get("isRetriveEmail").toString()) : true;
        		isRetriveSkype = context.containsKey("isRetriveSkype") ? Boolean.valueOf(context.get("isRetriveSkype").toString()) : true;
        		isRetriveWebAddress = context.containsKey("isRetriveWebAddress") ? Boolean.valueOf(context.get("isRetriveWebAddress").toString()) : true;
    		}
    		
    		List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
			
        	conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        	conditionsList.add(EntityUtil.getFilterByDateExpr());

        	EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
        	List<GenericValue> partyContactMechs = delegator.findList("PartyContactMech", mainConditons, UtilMisc.toSet("contactMechId"), null, null, false);
    		
        	if (UtilValidate.isNotEmpty(partyContactMechs)) {
        		partyContactMechIds = partyContactMechs.stream().map(x->x.getString("contactMechId")).collect(Collectors.toList());
        	}
        	
    		EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
    		EntityCondition condition2 = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, partyContactMechIds);
    		
    		// Phone Number [start]
    		if (isRetrivePhone) {
    			conditionsList = new ArrayList<EntityCondition>();
    			
            	conditionsList.add(condition1);
            	conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE"));
            	conditionsList.add(condition2);
            	conditionsList.add(EntityUtil.getFilterByDateExpr());

            	mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            	GenericValue primaryPhone = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", mainConditons, UtilMisc.toSet("contactMechId"), null, null, false));
        		
        		if(UtilValidate.isNotEmpty(primaryPhone)){
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
    	    			if(UtilValidate.isNotEmpty(partyPhoneMech)) {
    	    				String phoneSolicitation = UtilValidate.isNotEmpty(partyPhoneMech.getString("allowSolicitation"))? partyPhoneMech.getString("allowSolicitation") : "N";
    		    			partyPrimayContactMechValues.put("phoneSolicitation", phoneSolicitation);
    	    			}
        			}
        			partyPrimayContactMechValues.put("dndStatus", dndStatus);
        			partyPrimayContactMechValues.put("PrimaryPhone", phoneNumber);
        			partyPrimayContactMechValues.put("telephoneContactMechId", primaryPhone.getString("contactMechId"));
        		}
    		}
    		// Phone Number [end]
    		
    		// Mobile Number [start]
    		if (isRetriveMobile) {
    			conditionsList = new ArrayList<EntityCondition>();
    			
            	conditionsList.add(condition1);
            	conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PHONE_MOBILE"));
            	conditionsList.add(condition2);
            	conditionsList.add(EntityUtil.getFilterByDateExpr());

            	mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            	GenericValue mobilePhone = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", mainConditons, UtilMisc.toSet("contactMechId"), null, null, false));
            	
    			if (UtilValidate.isNotEmpty(mobilePhone)) {
    				GenericValue mobilePhoneNumber = delegator.findOne("TelecomNumber",
    						UtilMisc.toMap("contactMechId", mobilePhone.getString("contactMechId")), false);
    				String phoneNumber = "";
    				if (UtilValidate.isNotEmpty(mobilePhoneNumber)) {
    					String countryCode = mobilePhoneNumber.getString("countryCode");
    					String areaCode = mobilePhoneNumber.getString("areaCode");
    					String contactNumber = mobilePhoneNumber.getString("contactNumber");
    					if (countryCode != null && areaCode != null && contactNumber != null) {
    						phoneNumber += countryCode + "-" + areaCode + "-" + contactNumber;
    					} else if (areaCode != null && contactNumber != null) {
    						phoneNumber += areaCode + "-" + contactNumber;
    					}
    					GenericValue partyMobileMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech",
    							UtilMisc.toMap("partyId", partyId, "contactMechId", mobilePhone.getString("contactMechId")),
    							null, false));
    					if (partyMobileMech != null && partyMobileMech.size() > 0) {
    						String mobileSolicitation = UtilValidate
    								.isNotEmpty(partyMobileMech.getString("allowSolicitation"))
    										? partyMobileMech.getString("allowSolicitation") : "N";
    						partyPrimayContactMechValues.put("mobileSolicitation", mobileSolicitation);
    					}

    					partyPrimayContactMechValues.put("MobilePhone", phoneNumber);
    				}
    			}
    		}
			// Mobile Number [end]
    		
			// Work Phone [start]
    		if (isRetriveWorkPhone) {
    			conditionsList = new ArrayList<EntityCondition>();
    			
            	conditionsList.add(condition1);
            	conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PHONE_WORK_SEC"));
            	conditionsList.add(condition2);
            	conditionsList.add(EntityUtil.getFilterByDateExpr());

            	mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            	GenericValue secondaryPhone = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", mainConditons, UtilMisc.toSet("contactMechId"), null, null, false));
            	
        		if(UtilValidate.isNotEmpty(secondaryPhone)){
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
    		}
    		// Work Phone [end]
    		
    		// Primary email [start]
    		if (isRetriveEmail) {
    			conditionsList = new ArrayList<EntityCondition>();
    			
            	conditionsList.add(condition1);
            	conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL"));
            	conditionsList.add(condition2);
            	conditionsList.add(EntityUtil.getFilterByDateExpr());

            	mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            	GenericValue emailAddress = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", mainConditons, UtilMisc.toSet("contactMechId"), null, null, false));
        		
        		if(UtilValidate.isNotEmpty(emailAddress)){
        			GenericValue emailAddressValue = delegator.findOne("ContactMech",UtilMisc.toMap("contactMechId",emailAddress.getString("contactMechId")),false);
        			String emailAddressId = "";
        			if(UtilValidate.isNotEmpty(emailAddressValue) && UtilValidate.isNotEmpty(emailAddressValue.getString("infoString"))){
        				emailAddressId=emailAddressValue.getString("infoString");
        			}
        			partyPrimayContactMechValues.put("EmailAddress", emailAddressId);
        			partyPrimayContactMechValues.put("emailContactMechId", UtilValidate.isNotEmpty(emailAddressValue) ? emailAddressValue.getString("contactMechId") : null);
        			GenericValue partyEmailMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",emailAddress.getString("contactMechId")), null, false));
        			if(partyEmailMech != null && partyEmailMech.size() > 0) {
    	    			String emailSolicitation = UtilValidate.isNotEmpty(partyEmailMech.getString("allowSolicitation"))? partyEmailMech.getString("allowSolicitation") : "N";
    	    			partyPrimayContactMechValues.put("emailSolicitation", emailSolicitation);
        			}
        		}
    		}
    		// Primary email [end]
    		
    		// Primary skype [start]
    		if (isRetriveSkype) {
    			conditionsList = new ArrayList<EntityCondition>();
    			
            	conditionsList.add(condition1);
            	conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_SKYPE"));
            	conditionsList.add(condition2);
            	conditionsList.add(EntityUtil.getFilterByDateExpr());

            	mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            	GenericValue skype = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", mainConditons, UtilMisc.toSet("contactMechId"), null, null, false));
            	
        		if(UtilValidate.isNotEmpty(skype)){
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
    		}
    		// Primary skype [end]
    		
    		// Web Address [start]
    		if (isRetriveWebAddress) {
    			conditionsList = new ArrayList<EntityCondition>();
    			
            	conditionsList.add(condition1);
            	conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_WEB_URL"));
            	conditionsList.add(condition2);
            	conditionsList.add(EntityUtil.getFilterByDateExpr());

            	mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
            	GenericValue webUrl = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", mainConditons, UtilMisc.toSet("contactMechId"), null, null, false));
            	
        		if(UtilValidate.isNotEmpty(webUrl)){
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
    		}
    		// Web Address [end]
    		
    	} catch(GenericEntityException ge){
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

			EntityCondition postalAddressConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION"), condition2));
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
