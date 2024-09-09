package org.groupfio.common.portal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fio.admin.portal.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
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
            	GenericValue primaryPhone = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose").where(mainConditons).orderBy("fromDate DESC").queryFirst();
        		if(UtilValidate.isNotEmpty(primaryPhone)){
        			GenericValue primaryPhoneNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId",primaryPhone.getString("contactMechId")), false);
        			String phoneNumber ="";
        			String dndStatus = "N";
        			String contactNumber = "";
        			String countryCode = "";
        			if(UtilValidate.isNotEmpty(primaryPhoneNumber)){
    	    			countryCode = primaryPhoneNumber.getString("countryCode");
    	    			countryCode = UtilValidate.isNotEmpty(countryCode)?countryCode.replace("+", ""):countryCode;
    	    			String areaCode = primaryPhoneNumber.getString("areaCode");
    	    			contactNumber = primaryPhoneNumber.getString("contactNumber");
    	    			
    	    			if(UtilValidate.isNotEmpty(primaryPhoneNumber.getString("dndStatus"))) {
    	    				dndStatus = primaryPhoneNumber.getString("dndStatus");
    	    			}
    	    			if(countryCode!=null && areaCode!=null && contactNumber!=null){
    	    				phoneNumber+=countryCode+"-"+areaCode+"-"+contactNumber;
    	    		     }
    	    			 else if(countryCode!=null && contactNumber!=null){
    	    				 phoneNumber+="+"+countryCode+"-"+contactNumber; 
    	    		     }
    	    		     else if(areaCode!=null && contactNumber!=null){
    	    		    	 phoneNumber+=areaCode+"-"+contactNumber; 
    	    		     }
    	    		     else if(contactNumber !=null){
    	    		    	 phoneNumber=contactNumber;
    	    		     }
    	    			GenericValue partyPhoneMech = EntityQuery.use(delegator).select("allowSolicitation").from("PartyContactMech").where("partyId",partyId,"contactMechId",primaryPhone.getString("contactMechId")).queryFirst();
    	    			if(UtilValidate.isNotEmpty(partyPhoneMech)) {
    	    				String phoneSolicitation = UtilValidate.isNotEmpty(partyPhoneMech.getString("allowSolicitation"))? partyPhoneMech.getString("allowSolicitation") : "N";
    		    			partyPrimayContactMechValues.put("phoneSolicitation", phoneSolicitation);
    	    			}
        			}
        			partyPrimayContactMechValues.put("dndStatus", dndStatus);
        			partyPrimayContactMechValues.put("PrimaryPhone", phoneNumber);
        			partyPrimayContactMechValues.put("primaryContactNumber", contactNumber);
        			partyPrimayContactMechValues.put("primaryContactCountryCode", countryCode);
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
            	GenericValue mobilePhone = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose").where(mainConditons).queryFirst();
            	
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
    					GenericValue partyMobileMech = EntityQuery.use(delegator).from("PartyContactMech").where("partyId", partyId, "contactMechId", mobilePhone.getString("contactMechId")).queryFirst();
    					if (partyMobileMech != null && partyMobileMech.size() > 0) {
    						String mobileSolicitation = UtilValidate
    								.isNotEmpty(partyMobileMech.getString("allowSolicitation"))
    										? partyMobileMech.getString("allowSolicitation") : "Y";
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
            	GenericValue secondaryPhone = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose").where(mainConditons).queryFirst();
            	
        		if(UtilValidate.isNotEmpty(secondaryPhone)){
        			GenericValue secondaryPhoneNumber = delegator.findOne("TelecomNumber",UtilMisc.toMap("contactMechId",secondaryPhone.getString("contactMechId")),false);
        			String phoneNumber ="";
        			if(UtilValidate.isNotEmpty(secondaryPhoneNumber)){
    	    			String countryCode = secondaryPhoneNumber.getString("countryCode");
    	    			countryCode = UtilValidate.isNotEmpty(countryCode)?countryCode.replace("+", ""):countryCode;
    	    			String areaCode = secondaryPhoneNumber.getString("areaCode");
    	    			String contactNumber = secondaryPhoneNumber.getString("contactNumber");
    	    			
    	    			if(countryCode!=null && areaCode!=null && contactNumber!=null){
    	    				phoneNumber+="+"+countryCode+"-"+areaCode+"-"+contactNumber;
    	    		     }
    	    			 else if(countryCode!=null && contactNumber!=null){
    	    		    	 phoneNumber+="+"+countryCode+"-"+contactNumber; 
     	    		     }
    	    		     else if(areaCode!=null && contactNumber!=null){
    	    		    	 phoneNumber+=areaCode+"-"+contactNumber; 
    	    		     }
    	    		     else if(contactNumber !=null){
    	    		    	 phoneNumber=contactNumber;
    	    		     }
    	    			GenericValue partySecPhoneMech = EntityQuery.use(delegator).select("allowSolicitation").from("PartyContactMech").where("partyId",partyId,"contactMechId",secondaryPhone.getString("contactMechId")).queryFirst();
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
            	GenericValue emailAddress = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose").where(mainConditons).queryFirst();
        		
        		if(UtilValidate.isNotEmpty(emailAddress)){
        			GenericValue emailAddressValue = delegator.findOne("ContactMech",UtilMisc.toMap("contactMechId",emailAddress.getString("contactMechId")),false);
        			if (UtilValidate.isNotEmpty(emailAddressValue)) {
        				String emailAddressId = "";
            			if(UtilValidate.isNotEmpty(emailAddressValue) && UtilValidate.isNotEmpty(emailAddressValue.getString("infoString"))){
            				emailAddressId=emailAddressValue.getString("infoString");
            			}
            			partyPrimayContactMechValues.put("EmailAddress", emailAddressId);
            			partyPrimayContactMechValues.put("emailContactMechId", emailAddressValue.getString("contactMechId"));
            			GenericValue partyEmailMech = EntityQuery.use(delegator).select("allowSolicitation").from("PartyContactMech").where("partyId",partyId,"contactMechId",emailAddress.getString("contactMechId")).queryFirst();
            			if(partyEmailMech != null && partyEmailMech.size() > 0) {
        	    			String emailSolicitation = UtilValidate.isNotEmpty(partyEmailMech.getString("allowSolicitation"))? partyEmailMech.getString("allowSolicitation") : "Y";
        	    			partyPrimayContactMechValues.put("emailSolicitation", emailSolicitation);
            			}
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
            	GenericValue skype = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose").where(mainConditons).queryFirst();
            	
        		if(UtilValidate.isNotEmpty(skype)){
        			GenericValue skypeValue = delegator.findOne("ContactMech",UtilMisc.toMap("contactMechId",skype.getString("contactMechId")),false);
        			String skypeId = "";
        			if(UtilValidate.isNotEmpty(skypeValue) && UtilValidate.isNotEmpty(skypeValue.getString("infoString"))){
        					skypeId=skypeValue.getString("infoString");
        			}
        			partyPrimayContactMechValues.put("SkypeId", skypeId);
        			
        			GenericValue partySkypeMech = EntityQuery.use(delegator).select("allowSolicitation").from("PartyContactMech").where("partyId",partyId,"contactMechId",skype.getString("contactMechId")).queryFirst();
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
            	GenericValue webUrl = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose").where(mainConditons).queryFirst();
            	
        		if(UtilValidate.isNotEmpty(webUrl)){
        			GenericValue webUrlValue = delegator.findOne("ContactMech",UtilMisc.toMap("contactMechId",webUrl.getString("contactMechId")),false);
        			String webURL = "";
        			if(UtilValidate.isNotEmpty(webUrlValue) && UtilValidate.isNotEmpty(webUrlValue.getString("infoString"))){
        					webURL=webUrlValue.getString("infoString");
        			}
        			partyPrimayContactMechValues.put("webURL", webURL);
        			
        			GenericValue partyWebURLeMech = EntityQuery.use(delegator).select("allowSolicitation").from("PartyContactMech").where("partyId",partyId,"contactMechId",webUrl.getString("contactMechId")).queryFirst();
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
		GenericValue postalAddress = null;
		
		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION"));
			conditions.add(EntityUtil.getFilterByDateExpr());
        	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
        	GenericValue pcmp = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose").where(mainConditon).queryFirst();
			if (UtilValidate.isNotEmpty(pcmp)) {
				String contactMechId = pcmp.getString("contactMechId");
				if (UtilValidate.isNotEmpty(contactMechId)) {
					postalAddress = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId", contactMechId).queryFirst();
				}
			}
		} catch (GenericEntityException e) {
			Debug.logInfo("Error: "+e.getMessage(), module);
		}
		return postalAddress;
	}
	
	public static Map getPartyPrimaryPostalAddressList(Delegator delegator, String partyId) {
		List<String> partyContactMechIds= new ArrayList<String>();
		
		Map<String, Object> finalPostalAddMap=new HashMap<>();
		Map<String, Object> postalAddress = new HashMap<>();
		List<String> contactMechIds = new ArrayList<>();
		try{
			
			List<EntityCondition> conditions = FastList.newInstance();
			
			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION"));
			
			conditions.add(EntityUtil.getFilterByDateExpr());
			
            EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
            List<GenericValue> mechPurposeList = delegator.findList("PartyContactMechPurpose", mainConditons, null, null, null, false);
			if (UtilValidate.isNotEmpty(mechPurposeList)) {
				contactMechIds = EntityUtil.getFieldListFromEntityList(mechPurposeList, "contactMechId", true);
				for (GenericValue mechPurpose : mechPurposeList) {
					
					String contactMechId = mechPurpose.getString("contactMechId");
					
					conditions = FastList.newInstance();
					conditions.add(EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMechId));
					
					mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	                GenericValue pa = EntityQuery.use(delegator).from("PostalAddress").where(mainConditons).queryFirst();
	    			
	                if (UtilValidate.isNotEmpty(pa)) {
	                	Map<String, Object> postalMap = new HashMap<String, Object>();
	                	postalMap.putAll(DataUtil.convertGenericValueToMap(delegator, pa));
	    				String stateGeoId = pa.getString("stateProvinceGeoId");
	    				if(UtilValidate.isNotEmpty(stateGeoId))
	    					postalMap.put("state", DataUtil.getGeoName(delegator, stateGeoId, "STATE/PROVINCE"));
	    				postalAddress.put(contactMechId, postalMap);
	    			}
	    			
				}
			}
			
			finalPostalAddMap.put("postalAddressList", postalAddress);
			finalPostalAddMap.put("contactMechIds", contactMechIds);
			
		} catch (GenericEntityException e) {
			Debug.logInfo("Error: "+e.getMessage(), module);
		}
		return finalPostalAddMap;
	}
	
	public static Map<String, String> getPartyPrimaryContactMechValueMapsExt(Delegator delegator, String partyId) {
		Map<String, String> partyPrimayContactMechValues=new HashMap<String, String>();
    	List<String> partyContactMechIds= new ArrayList<String>();
    	try{
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
			conditionsList = new ArrayList<EntityCondition>();
        	conditionsList.add(condition1);
        	conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE"));
        	conditionsList.add(condition2);
        	conditionsList.add(EntityUtil.getFilterByDateExpr());

        	mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
        	GenericValue primaryPhone = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose").where(mainConditons).orderBy("fromDate DESC").queryFirst();
    		if(UtilValidate.isNotEmpty(primaryPhone)){
    			GenericValue primaryPhoneNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId",primaryPhone.getString("contactMechId")), false);
    			String phoneNumber ="";
    			String contactNumber = "";
    			String countryCode = "";
    			String telecomNumber = "";
    			String areaCode = "";
    			if(UtilValidate.isNotEmpty(primaryPhoneNumber)){
	    			countryCode = primaryPhoneNumber.getString("countryCode");
	    			countryCode = UtilValidate.isNotEmpty(countryCode)?countryCode.replace("+", ""):countryCode;
	    			areaCode = primaryPhoneNumber.getString("areaCode");
	    			contactNumber = primaryPhoneNumber.getString("contactNumber");
	    			if(contactNumber !=null) {
	    				telecomNumber = contactNumber;
	    			}
	    			if(areaCode!=null && contactNumber!=null){
	    		    	 phoneNumber+=areaCode+contactNumber; 
	    		     }
	    		     else if(contactNumber !=null){
	    		    	 phoneNumber=contactNumber;
	    		     }
	    			phoneNumber = DataHelper.preparePhoneNumber(delegator, phoneNumber);
					phoneNumber = UtilValidate.isNotEmpty(countryCode) ? "+"+countryCode+"-"+ phoneNumber : phoneNumber;
    			}
    			partyPrimayContactMechValues.put("TelecomNumber", telecomNumber);
    			partyPrimayContactMechValues.put("PrimaryPhone", phoneNumber);
    			partyPrimayContactMechValues.put("primaryContactNumber", contactNumber);
    			partyPrimayContactMechValues.put("primaryContactCountryCode", countryCode);
    			partyPrimayContactMechValues.put("primaryContactAreaCode", areaCode);
        		}
    		} catch(GenericEntityException ge){
    		Debug.logInfo("Error: "+ge.getMessage(), module);
    	}
    	return partyPrimayContactMechValues;
	}
	
}
