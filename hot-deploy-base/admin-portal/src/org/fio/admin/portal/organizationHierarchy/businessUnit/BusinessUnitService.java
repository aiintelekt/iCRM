package org.fio.admin.portal.organizationHierarchy.businessUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;

import org.ofbiz.base.util.Debug;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.admin.portal.constant.AdminPortalConstant.BusinessUnitConstant;
import org.fio.admin.portal.util.DataUtil;
import org.fio.homeapps.util.PartyHelper;

public class BusinessUnitService {
    public static final String MODULE = BusinessUnitService.class.getName();
    public static final String RESOURCE = "AdminPortalUiLabels";

    /* for creating business unit */
    public static Map < String, Object > createBusinessUnit(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside createBusinessUnit------ " + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
        Map < String, Object > results = ServiceUtil.returnSuccess();
        String buName = (String) context.get("buName");
        String buId = (String) context.get("buId");
        String parentBuId = (String) context.get("parentBuId");
        String buType = (String) context.get("buType");
        /*String sequenceNumber = (String) context.get("sequenceNumber");*/
        String status = (String) context.get("status");
        String address1 = (String) context.get("address1");
        String address2 = (String) context.get("address2");
        String address3 = (String) context.get("address3");
        String city = (String) context.get("city");
        String stateOrProvince = (String) context.get("stateOrProvince");
        String zipOrPostalCode = (String) context.get("zipOrPostalCode");
        String countryOrRegion = (String) context.get("countryOrRegion");
        
        String url1 = (String) context.get("url1");
        String url2 = (String) context.get("url2");
        String url3 = (String) context.get("url3");
        String url4 = (String) context.get("url4");
        String url5 = (String) context.get("url5");
        
        String addressContactMechId = null;
        String phone = (String) context.get("phone");
        String phoneContactMechId = null;
        String mobile = (String) context.get("mobile");
        String mobileContactMechId = null;
        String email = (String) context.get("email");
        String emailContactMechId = null;
        String website = (String) context.get("website");
        String websiteContactMechId = null;
        String userLoginId = null;
        try {
        	
        	 GenericValue businessunitDetails = EntityUtil.getFirst(delegator.findByAnd("ProductStoreGroup",
                     UtilMisc.toMap("productStoreGroupName", buName), null, false));
        	 if(UtilValidate.isEmpty(businessunitDetails))
        	 { 
	            GenericValue businessUnit = delegator.makeValue("ProductStoreGroup");
	            String productStoreGroupId = delegator.getNextSeqId("ProductStoreGroup");
	            businessUnit.put("productStoreGroupId", productStoreGroupId);
	            if (UtilValidate.isNotEmpty(buId)) {
	                businessUnit.put("externalId", buId);
	            } else {
	                businessUnit.put("externalId", productStoreGroupId);
	            }
	            businessUnit.put("productStoreGroupName", buName);
	            businessUnit.put("primaryParentGroupId", parentBuId);
	            businessUnit.put("productStoreGroupTypeId", buType);
	            /*businessUnit.put("seqNum", sequenceNumber);*/
	            businessUnit.put("status", status);
	            businessUnit.put("createdOn", UtilDateTime.nowTimestamp());
	            userLoginId = userLogin.getString("userLoginId");
	            businessUnit.put("createdByUserLogin", userLoginId);
	            businessUnit.put("createdByUserLoginId", userLoginId);
	            
	            //added for url
	            businessUnit.put("url1", url1);
	            businessUnit.put("url2", url2);
	            businessUnit.put("url3", url3);
	            businessUnit.put("url4", url4);
	            businessUnit.put("url5", url5);
	            //ended
	            
	            if(UtilValidate.isNotEmpty(parentBuId)){		
	            	GenericValue parentlevelDetails = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", parentBuId).queryOne();
	            	if (UtilValidate.isNotEmpty(parentlevelDetails)) {
	            		String level = parentlevelDetails.getString("productStoreGroupLevel");
	            		if (UtilValidate.isNotEmpty(level)) {
	            			int levels = Integer.parseInt(level) + 1;
	            			level = Integer.toString(levels);
	            			if (UtilValidate.isNotEmpty(level)) {
	            				businessUnit.put("productStoreGroupLevel", level);
	            			}
            			}
            		}
        		}
	            businessUnit.create();
	            Map < String, Object > inMap = new HashMap < String, Object > ();
	            if ((UtilValidate.isNotEmpty(address1)) || (UtilValidate.isNotEmpty(address2)) ||
	                (UtilValidate.isNotEmpty(city)) || (UtilValidate.isNotEmpty(zipOrPostalCode))) {
	                inMap.put("address1", context.get("address1"));
	                inMap.put("address2", context.get("address2"));
	                inMap.put("city", context.get("city"));
	                inMap.put("postalCode", context.get("zipOrPostalCode"));
	                inMap.put("countryGeoId", context.get("countryOrRegion"));
	                inMap.put("stateProvinceGeoId", context.get("stateOrProvince"));
	                inMap.put("userLogin", userLogin);
	                Map < String, Object > outMap = dispatcher.runSync("createPostalAddress", inMap);
	                addressContactMechId = (String) outMap.get("contactMechId");
	                if (UtilValidate.isNotEmpty(address3)) {
	                    inMap.clear();
	                    inMap.put("contactMechId", addressContactMechId);
	                    inMap.put("value", context.get("address3"));
	                    dispatcher.runSync("ap.contactAddress", inMap);
	                }
	            }
	            if (UtilValidate.isNotEmpty(email)) {
	                inMap.clear();
	                inMap.put("emailAddress", email);
	                inMap.put("userLogin", userLogin);
	                inMap.put("contactMechTypeId", "EMAIL_ADDRESS");
	                Map < String, Object > emailMap = dispatcher.runSync("createEmailAddress", inMap);
	                emailContactMechId = (String) emailMap.get("contactMechId");
	            }
	            if (UtilValidate.isNotEmpty(website)) {
	                GenericValue webContactMech = delegator.makeValue("ContactMech",
	                    UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
	                        "WEB_ADDRESS", "infoString", website));
	                delegator.create(webContactMech);
	                websiteContactMechId = webContactMech.getString("contactMechId");
	            }
	            if (UtilValidate.isNotEmpty(phone)) {
	                inMap.clear();
	                inMap.put("contactNumber", phone);
	                inMap.put("userLogin", userLogin);
	                Map < String, Object > phoneMap = dispatcher.runSync("createTelecomNumber", inMap);
	                phoneContactMechId = (String) phoneMap.get("contactMechId");
	            }
	            if (UtilValidate.isNotEmpty(mobile)) {
	                inMap.clear();
	                inMap.put("contactNumber", mobile);
	                inMap.put("userLogin", userLogin);
	                Map < String, Object > mobileMap = dispatcher.runSync("createTelecomNumber", inMap);
	                mobileContactMechId = (String) mobileMap.get("contactMechId");
	            }
	            GenericValue addressDetails = EntityQuery.use(delegator).from("ProductStoreGroup")
	                .where("productStoreGroupId", productStoreGroupId).queryOne();
	            if (UtilValidate.isNotEmpty(addressDetails)) {
	                addressDetails.put("postalContactMechId", addressContactMechId);
	                addressDetails.put("telecomContactMechId", mobileContactMechId);
	                addressDetails.put("phoneContactMechId", phoneContactMechId);
	                addressDetails.put("websiteId", websiteContactMechId);
	                addressDetails.put("emailId", emailContactMechId);
	                addressDetails.store();
	            }
	            results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "BuCreatedSuccessfully", locale));
	            results.put("productStoreGroupId", productStoreGroupId);
	          
        	 }else {
        		 return ServiceUtil.returnError("BU Name already exists");
        	 }
        } catch (GeneralException e) {
            Debug.log("==error in createBusinessUnits===" + e.getMessage());
        }
        return results;
       
    }
    /* for updating Business unit */
    public static Map < String, Object > updateBusinessUnit(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside updateBusinessUnit------ " + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map < String, Object > results = ServiceUtil.returnSuccess();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        String parentBuId = (String) context.get("parentBuId");
        String parentBuDesc = (String) context.get("parentBuDesc");
        String buType = (String) context.get("buType");
        String buName = (String) context.get("buName");
        /*String sequenceNumber = (String) context.get("sequenceNumber");*/
        String status = (String) context.get("status");
        String userLoginId = null;
        boolean user = true;
        try {
            GenericValue businessUnit = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", productStoreGroupId).queryOne();
            if (UtilValidate.isNotEmpty(businessUnit)) {
            	
            			if (UtilValidate.isNotEmpty(parentBuId))
            			{
            				businessUnit.put("primaryParentGroupId", parentBuId);
            				GenericValue parentlevelDetails = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", parentBuId).queryOne();
    			            if (UtilValidate.isNotEmpty(parentlevelDetails)) {
    			                String level = parentlevelDetails.getString("productStoreGroupLevel");
    			                if (UtilValidate.isNotEmpty(level)) { 
        			                int levels = Integer.parseInt(level) + 1;
        			                level = Integer.toString(levels);
        			                if (UtilValidate.isNotEmpty(level)) {
        			                    businessUnit.put("productStoreGroupLevel", level);
        			                }
    			                }
    			            }
            			}
            			else
            			{
            				if (UtilValidate.isNotEmpty(parentBuDesc)) {
	            				 GenericValue parentDescDet =  EntityUtil.getFirst(delegator.findByAnd("ProductStoreGroup", UtilMisc.toMap("productStoreGroupName", parentBuDesc), null, false));
	            				 if(UtilValidate.isNotEmpty(parentDescDet))
	            				 { 
		            				 String parent=parentDescDet.getString("productStoreGroupId");
		            				 if(UtilValidate.isNotEmpty(parent)) {
		            				 businessUnit.put("primaryParentGroupId", parent);
		            				 String level = parentDescDet.getString("productStoreGroupLevel");
		    			                if (UtilValidate.isNotEmpty(level)) { 
		        			                int levels = Integer.parseInt(level) + 1;
		        			                level = Integer.toString(levels);
		        			                if (UtilValidate.isNotEmpty(level)) {
		        			                    businessUnit.put("productStoreGroupLevel", level);
		        			                }
		    			                }
		            				 }
	            				 }
            				}
            			}
			            businessUnit.put("productStoreGroupTypeId", buType);
			           /* businessUnit.put("seqNum", sequenceNumber);*/
			           // Debug.log("status========="+status);
			            if(UtilValidate.isNotEmpty(status)&&status.equalsIgnoreCase("IN_ACTIVE")) 
			            {
			            	List<GenericValue> getBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("primaryParentGroupId", productStoreGroupId).queryList();
			            	//Debug.log("getBu========="+getBu);
			            	if(UtilValidate.isNotEmpty(getBu))
			            	{
				            	 for(GenericValue bu :getBu) {
				            		 String childBuStatus= bu.getString("status");
			            		     if(UtilValidate.isNotEmpty(childBuStatus)&&childBuStatus.equalsIgnoreCase("ACTIVE"))
									 {
			            		    	  user = false;
			            		    	  break;
									 }
				            	 }
			            	}
			            }
			            
			            businessUnit.put("status", status);
			            if (UtilValidate.isNotEmpty(buName)) {
			            	businessUnit.put("productStoreGroupName", buName);
			            }
			            
			            businessUnit.put("modifiedOn", UtilDateTime.nowTimestamp());
			            userLoginId = userLogin.getString("userLoginId");
			            businessUnit.put("modifiedByUserLogin", userLoginId);
			            businessUnit.put("modifiedUserLoginId", userLoginId);
			            businessUnit.store();
			            
			            if ( user == false) {
			            	//results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "StatusWarning", locale));
			            	//results = ServiceUtil.returnMessage(RESOURCE, "StatusWarning"); 
			            	 results = ServiceUtil.returnFailure( "BU status updated as inactive.This BU have active child BUs.Please assign them to active BUs");
			            }
			            else if(user == true) {
			            	results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "BUUpdatedSuccessfully", locale));
			            }
			           
            }
            results.put("productStoreGroupId", productStoreGroupId);
        } catch (GeneralException e) {
            Debug.log("==error in updateBusinessUnit===" + e.getMessage());
        }
        return results;
    }
    /* for updating phone number */
    public static Map < String, Object > updatePhoneNumber(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside updatePhoneNumber------ " + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map < String, Object > results = ServiceUtil.returnSuccess();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > inMap = new HashMap < String, Object > ();
        String phoneMechId = (String) context.get("phoneMechId");
        String phone = (String) context.get("phone");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        String phoneContactMechId = null;
        try {
            if (UtilValidate.isNotEmpty(phoneMechId)) {
                GenericValue phoneDetails = EntityQuery.use(delegator).from("TelecomNumber")
                    .where("contactMechId", phoneMechId).queryOne();
                if (UtilValidate.isNotEmpty(phoneDetails)) {
                    phoneDetails.put("contactNumber", phone);
                    phoneDetails.store();
                    results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "PhoneNumberUpdatedSuccessfully", locale));
                }
            } else {
                inMap.clear();
                inMap.put("contactNumber", phone);
                inMap.put("userLogin", userLogin);
                Map < String, Object > phoneMap = dispatcher.runSync("createTelecomNumber", inMap);
                phoneContactMechId = (String) phoneMap.get("contactMechId");
                GenericValue businessUnitDetails = EntityQuery.use(delegator).from("ProductStoreGroup")
                    .where("productStoreGroupId", productStoreGroupId).queryOne();
                if (UtilValidate.isNotEmpty(businessUnitDetails)) {
                    businessUnitDetails.put("phoneContactMechId", phoneContactMechId);
                    businessUnitDetails.store();
                    results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "PhoneNumberAddedSuccessfully", locale));
                }
            }
            results.put("productStoreGroupId", productStoreGroupId);
        } catch (Exception e) {
            // TODO: handle exception
            Debug.log("==error in updatePhoneNumber===" + e.getMessage());
        }
        return results;
    }
    /* for updating mobile number */
    public static Map < String, Object > updateMobileNumber(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside updateMobileNumber------ " + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map < String, Object > results = ServiceUtil.returnSuccess();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > inMap = new HashMap < String, Object > ();
        String mobileMechId = (String) context.get("mobileMechId");
        String mobile = (String) context.get("mobile");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        String telecomContactMechId = null;
        try {
            if (UtilValidate.isNotEmpty(mobileMechId)) {
                GenericValue mobileDetails = EntityQuery.use(delegator).from("TelecomNumber")
                    .where("contactMechId", mobileMechId).queryOne();
                if (UtilValidate.isNotEmpty(mobileDetails)) {
                    mobileDetails.put("contactNumber", mobile);
                    mobileDetails.store();
                    results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "MobileNumberUpdatedSuccessfully", locale));
                }
            } else {
                inMap.clear();
                inMap.put("contactNumber", mobile);
                inMap.put("userLogin", userLogin);
                Map < String, Object > mobileMap = dispatcher.runSync("createTelecomNumber", inMap);
                telecomContactMechId = (String) mobileMap.get("contactMechId");
                GenericValue businessUnitDetails = EntityQuery.use(delegator).from("ProductStoreGroup")
                    .where("productStoreGroupId", productStoreGroupId).queryOne();
                if (UtilValidate.isNotEmpty(businessUnitDetails)) {
                    businessUnitDetails.put("telecomContactMechId", telecomContactMechId);
                    businessUnitDetails.store();
                    results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "MobileNumberAddedSuccessfully", locale));
                }
            }
            results.put("productStoreGroupId", productStoreGroupId);
        } catch (Exception e) {
            // TODO: handle exception
            Debug.log("==error in updateMobileNumber===" + e.getMessage());
        }
        return results;
    }
    /* for updating email */
    public static Map < String, Object > updateEmail(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside updateEmail------ " + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map < String, Object > results = ServiceUtil.returnSuccess();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > inMap = new HashMap < String, Object > ();
        String emailId = (String) context.get("emailId");
        String email = (String) context.get("email");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        String emailContactMechId = null;
        try {
            if (UtilValidate.isNotEmpty(emailId)) {
                GenericValue emailDetails = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", emailId)
                    .queryOne();
                if (UtilValidate.isNotEmpty(emailDetails)) {
                    emailDetails.put("infoString", email);
                    emailDetails.store();
                    results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "EmailUpdatedSuccessfully", locale));
                }
            } else {
                inMap.clear();
                inMap.put("emailAddress", email);
                inMap.put("userLogin", userLogin);
                inMap.put("contactMechTypeId", "EMAIL_ADDRESS");
                Map < String, Object > emailMap = dispatcher.runSync("createEmailAddress", inMap);
                emailContactMechId = (String) emailMap.get("contactMechId");
                GenericValue businessUnitDetails = EntityQuery.use(delegator).from("ProductStoreGroup")
                    .where("productStoreGroupId", productStoreGroupId).queryOne();
                if (UtilValidate.isNotEmpty(businessUnitDetails)) {
                    businessUnitDetails.put("emailId", emailContactMechId);
                    businessUnitDetails.store();
                    results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "EmailAddedSuccessfully", locale));
                }
            }
            results.put("productStoreGroupId", productStoreGroupId);
        } catch (Exception e) {
            // TODO: handle exception
            Debug.log("==error in updateEmail===" + e.getMessage());
        }
        return results;
    }
    /* for updating website */
    public static Map < String, Object > updateWeb(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside updateWeb------ " + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map < String, Object > results = ServiceUtil.returnSuccess();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > inMap = new HashMap < String, Object > ();
        String webId = (String) context.get("webId");
        String web = (String) context.get("web");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        String websiteId = null;
        try {
            if (UtilValidate.isNotEmpty(webId)) {
                GenericValue webDetails = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", webId)
                    .queryOne();
                if (UtilValidate.isNotEmpty(webDetails)) {
                    webDetails.put("infoString", web);
                    webDetails.store();
                    results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "WebUpdatedSuccessfully", locale));
                }
            } else {
                GenericValue webContactMech = delegator.makeValue("ContactMech",
                    UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
                        "WEB_ADDRESS", "infoString", web));
                delegator.create(webContactMech);
                websiteId = webContactMech.getString("contactMechId");
                GenericValue businessUnitDetails = EntityQuery.use(delegator).from("ProductStoreGroup")
                    .where("productStoreGroupId", productStoreGroupId).queryOne();
                if (UtilValidate.isNotEmpty(businessUnitDetails)) {
                    businessUnitDetails.put("websiteId", websiteId);
                    businessUnitDetails.store();
                    results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "WebAddedSuccessfully", locale));
                }
            }
            results.put("productStoreGroupId", productStoreGroupId);
        } catch (Exception e) {
            // TODO: handle exception
            Debug.log("==error in updateWeb===" + e.getMessage());
        }
        return results;
    }
    /* for updating address */
    public static Map < String, Object > updateAddress(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside updateAddress------ " + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map < String, Object > results = ServiceUtil.returnSuccess();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > inMap = new HashMap < String, Object > ();
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        String postalId = (String) context.get("postalId");
        String address1 = (String) context.get("address1");
        String address2 = (String) context.get("address2");
        String address3 = (String) context.get("address3");
        String city = (String) context.get("city");
        String stateOrProvince = (String) context.get("stateOrProvince");
        String zipOrPostalCode = (String) context.get("zipOrPostalCode");
        String countryOrRegion = (String) context.get("countryOrRegion");
        String addressContactMechId = null;
        try {
            if (UtilValidate.isNotEmpty(postalId)) {
                GenericValue addressDetails = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId", postalId).queryOne();
                if (UtilValidate.isNotEmpty(addressDetails)) {
                    addressDetails.put("address1", address1);
                    addressDetails.put("address2", address2);
                    addressDetails.put("address3", address3);
                    addressDetails.put("city", city);
                    addressDetails.put("stateProvinceGeoId", stateOrProvince);
                    addressDetails.put("postalCode", zipOrPostalCode);
                    addressDetails.put("countryGeoId", countryOrRegion);
                    addressDetails.store();
                    results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AddressUpdatedSuccessfully", locale));
                }
            } else {
                inMap.put("address1", address1);
                inMap.put("address2", address2);
                inMap.put("city", city);
                inMap.put("postalCode", zipOrPostalCode);
                inMap.put("countryGeoId", countryOrRegion);
                inMap.put("stateProvinceGeoId", stateOrProvince);
                inMap.put("userLogin", userLogin);
                Map < String, Object > outMap = dispatcher.runSync("createPostalAddress", inMap);
                addressContactMechId = (String) outMap.get("contactMechId");
                if (UtilValidate.isNotEmpty(address3)) {
                    inMap.clear();
                    inMap.put("contactMechId", addressContactMechId);
                    inMap.put("value", address3);
                    dispatcher.runSync("ap.contactAddress", inMap);
                }
                GenericValue businessUnitDetails = EntityQuery.use(delegator).from("ProductStoreGroup")
                    .where("productStoreGroupId", productStoreGroupId).queryOne();
                if (UtilValidate.isNotEmpty(businessUnitDetails)) {
                    businessUnitDetails.put("postalContactMechId", addressContactMechId);
                    businessUnitDetails.store();
                    results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AddressAddedSuccessfully", locale));
                }
            }
            results.put("productStoreGroupId", productStoreGroupId);
        } catch (Exception e) { // TODO: handle exception
            Debug.log("==error in updateAddress===" + e.getMessage());
        }
        return results;
    }
 
    /* for removine Phone Number */
    public static Map < String, Object > removePhoneNumber(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside removePhoneNumber------ " + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map < String, Object > results = ServiceUtil.returnSuccess();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String phoneMechId = (String) context.get("phoneMechId");
        String phone = (String) context.get("phone");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        try {
            GenericValue getPhone = EntityQuery.use(delegator).from("TelecomNumber").where("contactMechId", phoneMechId).queryOne();
            if (UtilValidate.isNotEmpty(getPhone)) {
                getPhone.remove();}
            GenericValue getContactMech = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", phoneMechId).queryOne();
            if (UtilValidate.isNotEmpty(getContactMech)) {
                getContactMech.remove();}
            GenericValue getBusiness = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", productStoreGroupId).queryOne();
            if (UtilValidate.isNotEmpty(getBusiness)) {
                getBusiness.put("phoneContactMechId", null);
                getBusiness.store();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "PhoneRemovedSuccessfully", locale));
            }
            results.put("productStoreGroupId", productStoreGroupId);
        } catch (Exception e) {
            // TODO: handle exception
            Debug.log("==error in removePhoneNumber===" + e.getMessage());
        }
        return results;
    }
    /* for removing Mobile */
    public static Map < String, Object > removeMobile(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside removeMobile------ " + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map < String, Object > results = ServiceUtil.returnSuccess();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String mobileId = (String) context.get("mobileId");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        try {
            GenericValue getMobile = EntityQuery.use(delegator).from("TelecomNumber").where("contactMechId", mobileId).queryOne();
            if (UtilValidate.isNotEmpty(getMobile)) {
                getMobile.remove();
            }
            GenericValue getContactMech = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", mobileId).queryOne();
            if (UtilValidate.isNotEmpty(getContactMech)) {
                getContactMech.remove();}
            GenericValue getBusiness = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", productStoreGroupId).queryOne();
            if (UtilValidate.isNotEmpty(getBusiness)) {
                getBusiness.put("telecomContactMechId", null);
                getBusiness.store();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "MobileRemovedSuccessfully", locale));
            }
            results.put("productStoreGroupId", productStoreGroupId);
        } catch (Exception e) {
            // TODO: handle exception
            Debug.log("==error in removeMobile===" + e.getMessage());
        }
        return results;
    }
    /* for removing Email */
    public static Map < String, Object > removeEmail(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside removeEmail------ " + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map < String, Object > results = ServiceUtil.returnSuccess();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String emailId = (String) context.get("emailId");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        try {
            GenericValue getEmail = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", emailId).queryOne();
            if (UtilValidate.isNotEmpty(getEmail)) {
                getEmail.remove();}
            GenericValue getBusiness = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", productStoreGroupId).queryOne();
            if (UtilValidate.isNotEmpty(getBusiness)) {
                getBusiness.put("emailId", null);
                getBusiness.store();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "EmailRemovedSuccessfully", locale));
            }
            results.put("productStoreGroupId", productStoreGroupId);
        } catch (Exception e) {
            // TODO: handle exception
            Debug.log("==error in removeEmail===" + e.getMessage());
        }
        return results;
    }
    /* for removing Website */
    public static Map < String, Object > removeWeb(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside removeWeb------ " + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map < String, Object > results = ServiceUtil.returnSuccess();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String websiteId = (String) context.get("websiteId");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        try {
            GenericValue getWeb = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", websiteId).queryOne();
            if (UtilValidate.isNotEmpty(getWeb)) {
                getWeb.remove();}
            GenericValue getBusiness = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", productStoreGroupId).queryOne();
            if (UtilValidate.isNotEmpty(getBusiness)) {
                getBusiness.put("websiteId", null);
                getBusiness.store();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "WebRemovedSuccessfully", locale));
            }
            results.put("productStoreGroupId", productStoreGroupId);
        } catch (Exception e) {
            // TODO: handle exception
            Debug.log("==error in removeWeb===" + e.getMessage());
        }
        return results;
    }
    /* for removing Address */
    public static Map < String, Object > removeAddress(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside removeAddress------ " + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map < String, Object > results = ServiceUtil.returnSuccess();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String postalId = (String) context.get("postalId");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        try {
            GenericValue getPostal = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId", postalId).queryOne();
            if (UtilValidate.isNotEmpty(getPostal)) {
                getPostal.remove();}
            GenericValue getAddress = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", postalId).queryOne();
            if (UtilValidate.isNotEmpty(getAddress)) {
                getAddress.remove();}
            GenericValue getBusiness = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", productStoreGroupId).queryOne();
            if (UtilValidate.isNotEmpty(getBusiness)) {
                getBusiness.put("postalContactMechId", null);
                getBusiness.store();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AddressRemovedSuccessfully", locale));
            }
            results.put("productStoreGroupId", productStoreGroupId);
        } catch (Exception e) {
            // TODO: handle exception
            Debug.log("==error in removeAddress===" + e.getMessage());
        }
        return results;
    }
    /* for updating url */
    public static Map < String, Object > updateUrl(DispatchContext dctx, Map < String, Object > context) {
    	Debug.logInfo("------inside updateUrl------ " + context, MODULE);
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map < String, Object > results = ServiceUtil.returnSuccess();
    	Security security = dctx.getSecurity();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Map < String, Object > inMap = new HashMap < String, Object > ();
    	String url1 = (String) context.get("url1");
    	String url2 = (String) context.get("url2");
    	String url3 = (String) context.get("url3");
    	String url4 = (String) context.get("url4");
    	String url5 = (String) context.get("url5");
    	String productStoreGroupId = (String) context.get("productStoreGroupId");
    	try {
    		GenericValue businessUnitDetails = EntityQuery.use(delegator).from("ProductStoreGroup")
    				.where("productStoreGroupId", productStoreGroupId).queryOne();
    		if (UtilValidate.isNotEmpty(businessUnitDetails)) {
    			if (UtilValidate.isNotEmpty(url1)) {
    				businessUnitDetails.put("url1", url1);
    				businessUnitDetails.store();
    			}
    			if (UtilValidate.isNotEmpty(url2)) {
    				businessUnitDetails.put("url2", url2);
    				businessUnitDetails.store();
    			}
    			if (UtilValidate.isNotEmpty(url3)) {
    				businessUnitDetails.put("url3", url3);
    				businessUnitDetails.store();
    			}
    			if (UtilValidate.isNotEmpty(url4)) {
    				businessUnitDetails.put("url4", url4);
    				businessUnitDetails.store();
    			}
    			if (UtilValidate.isNotEmpty(url5)) {
    				businessUnitDetails.put("url5", url5);
    				businessUnitDetails.store();
    			}
    			results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "URL updated successfully", locale));
    		}
    		results.put("productStoreGroupId", productStoreGroupId);
    	} catch (Exception e) {
    		// TODO: handle exception
    		Debug.log("==error in updateUrl===" + e.getMessage());
    	}
    	return results;
    }
    /* for updating url */
    public static Map < String, Object > removeUrl(DispatchContext dctx, Map < String, Object > context) {
    	Debug.logInfo("------inside updateUrl------ " + context, MODULE);
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map < String, Object > results = ServiceUtil.returnSuccess();
    	Security security = dctx.getSecurity();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Map < String, Object > inMap = new HashMap < String, Object > ();
    	String toRemove = (String) context.get("toRemove");
    	String productStoreGroupId = (String) context.get("productStoreGroupId");
    	try {
    		GenericValue businessUnitDetails = EntityQuery.use(delegator).from("ProductStoreGroup")
    				.where("productStoreGroupId", productStoreGroupId).queryOne();
    		if (UtilValidate.isNotEmpty(businessUnitDetails)) {
    			if (toRemove.equals("url1")) {
    				businessUnitDetails.put("url1", "");
    				businessUnitDetails.store();
    			}
    			if (toRemove.equals("url2")) {
    				businessUnitDetails.put("url2", "");
    				businessUnitDetails.store();
    			}
    			if (toRemove.equals("url3")) {
    				businessUnitDetails.put("url2", "");
    				businessUnitDetails.store();
    			}
    			if (toRemove.equals("url4")) {
    				businessUnitDetails.put("url4", "");
    				businessUnitDetails.store();
    			}
    			if (toRemove.equals("url5")) {
    				businessUnitDetails.put("url5", "");
    				businessUnitDetails.store();
    				
    			}
    			results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "Url Removed Successfully", locale));
    		}
    		results.put("productStoreGroupId", productStoreGroupId);
    	} catch (Exception e) {
    		// TODO: handle exception
    		Debug.log("==error in updateUrl===" + e.getMessage());
    	}
    	return results;
    }
    
    public static String createTechnicianLocation(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String country = (String) context.get("generalCountryGeoId");
		String state = (String) context.get("generalStateProvinceGeoId");
		String isTechInspection = (String) context.get("isTechInspection");
		String productStoreId = (String) context.get("productStoreId");
		
		String technician1 = (String) context.get("technician1");
		String technician2 = (String) context.get("technician2");
		String technician3 = (String) context.get("technician3");
		String technician4 = (String) context.get("technician4");
		
		try {
			
			if(UtilValidate.isNotEmpty(country) && UtilValidate.isNotEmpty(state)){
				GenericValue prodStoreGv = delegator.findOne("ProductStoreTechAssoc",UtilMisc.toMap("county", country, "state", state), false);
				if(UtilValidate.isNotEmpty(prodStoreGv)){
					request.setAttribute("_ERROR_MESSAGE_", "Location and Technicians Already Configured");
					return "error";
				}
				
				GenericValue productStoreTechAssoc = delegator.makeValue("ProductStoreTechAssoc");
				
				productStoreTechAssoc.set("county", country);
				productStoreTechAssoc.set("state", state);
				productStoreTechAssoc.set("isTechInspection", isTechInspection);
				
				if(UtilValidate.isNotEmpty(productStoreId)){
					productStoreTechAssoc.set("productStoreId", productStoreId);
					GenericValue productStoreInfo = EntityQuery.use(delegator).select("productStoreId","storeName").from("ProductStore").where("productStoreId", productStoreId).queryOne();
					if (UtilValidate.isNotEmpty(productStoreInfo) && UtilValidate.isNotEmpty(productStoreInfo.getString("storeName"))) {
						productStoreTechAssoc.set("productStoreName", productStoreInfo.getString("storeName"));
					}
				}
				
				if(UtilValidate.isNotEmpty(technician1)){
					productStoreTechAssoc.set("technicianId01", technician1);
					productStoreTechAssoc.set("technicianName01", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, technician1, false)) ? PartyHelper.getPartyName(delegator, technician1, false) : "");
				}
				if(UtilValidate.isNotEmpty(technician2)){
					productStoreTechAssoc.set("technicianId02", technician2);
					productStoreTechAssoc.set("technicianName02", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, technician2, false)) ? PartyHelper.getPartyName(delegator, technician2, false) : "");
				}
				if(UtilValidate.isNotEmpty(technician3)){
					productStoreTechAssoc.set("technicianId03", technician3);
					productStoreTechAssoc.set("technicianName03", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, technician3, false)) ? PartyHelper.getPartyName(delegator, technician3, false) : "");
				}
				if(UtilValidate.isNotEmpty(technician4)){
					productStoreTechAssoc.set("technicianId04", technician4);
					productStoreTechAssoc.set("technicianName04", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, technician3, false)) ? PartyHelper.getPartyName(delegator, technician4, false) : "");
				}
				
				productStoreTechAssoc.create();
				
				request.setAttribute("productStoreId",productStoreId);
				request.setAttribute("_EVENT_MESSAGE_", "Location and Technicians Added Successfully");
			}
			
		}catch (Exception e) {
			String errMsg = "Problem While Mapping Location and Technicians" + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
    }
    
		
}