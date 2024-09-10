package org.fio.homeapps.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.AppStatus;
import org.fio.homeapps.util.AppUtil;
import org.fio.homeapps.util.CommonUtils;
import org.fio.homeapps.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Group Fio
 *
 */
public class ApplicationServiceImpl {

	private static final String MODULE = ApplicationServiceImpl.class.getName();
    
    public static Map createAppRegistry(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String clientName = (String) context.get("clientName");
    	String clientDomain = (String) context.get("clientDomain");
    	String comments = (String) context.get("comments");
    	String appStatusId = (String) context.get("appStatusId");
    	String externalReferenceId = (String) context.get("externalReferenceId");
    	String sourceInvoked = (String) context.get("sourceInvoked");
    	String clientAppPassword = (String) context.get("clientAppPassword");
    	
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue registry = EntityUtil.getFirst( delegator.findByAnd("ClientApplicationRegistry",UtilMisc.toMap("clientDomain", clientDomain), null, false) );
    		
    		if (UtilValidate.isNotEmpty(registry)) {
    			result.putAll(ServiceUtil.returnError("Application Registry already exists!"));
    			return result;
    		}
    		
    		registry = delegator.makeValue("ClientApplicationRegistry");
    		
    		String clientRegistryId = delegator.getNextSeqId("ClientApplicationRegistry");
    		
    		if (UtilValidate.isEmpty(clientAppPassword)) {
    			clientAppPassword = CommonUtils.getRandomString(Integer.parseInt(UtilProperties.getPropertyValue(GlobalConstants.configResource, "client.app.password.length")));
    		}
    		
    		registry.put("clientRegistryId", clientRegistryId);
    		registry.put("clientName", clientName);
    		registry.put("clientDomain", clientDomain);
    		registry.put("comments", comments);
    		registry.put("clientAppPassword", clientAppPassword);
    		registry.put("appStatusId", appStatusId);
    		registry.put("externalReferenceId", externalReferenceId);
    		
    		registry.create();
    		
    		if (UtilValidate.isNotEmpty(appStatusId) && appStatusId.equals(AppStatus.ACTIVATED)) {
    			
    			Map<String, Object> appStatusContext = new HashMap<String, Object>();
    			
    			appStatusContext.put("fromDate", fromDate);
    			appStatusContext.put("thruDate", thruDate);
    			appStatusContext.put("clientRegistryId", clientRegistryId);
    			appStatusContext.put("userLogin", userLogin);
    			appStatusContext.put("sourceInvoked", sourceInvoked);
    			
    			Map<String, Object> appStatusResult = dispatcher.runSync("ewallet.createAppStatus", appStatusContext);
				
				if (ServiceUtil.isError(appStatusResult)) {
					Debug.logError("Generate Access Token failed for App Registry# "+clientRegistryId, MODULE);
				} else {
					result.put("secretCode", appStatusResult.get("secretCode"));
				}
    			
    		}
    		
    		result.put("clientRegistryId", clientRegistryId);
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created App registry.."));
    	
    	return result;
    	
    }
    
    public static Map updateAppRegistry(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String clientRegistryId = (String) context.get("clientRegistryId");
    	
    	String clientName = (String) context.get("clientName");
    	String clientDomain = (String) context.get("clientDomain");
    	String comments = (String) context.get("comments");
    	String appStatusId = (String) context.get("appStatusId");
    	String externalReferenceId = (String) context.get("externalReferenceId");
    	String clientAppPassword = (String) context.get("clientAppPassword");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue registry = AppUtil.getClientApplicationRegistry(delegator, clientRegistryId, externalReferenceId);
    		
    		if (UtilValidate.isEmpty(registry)) {
    			result.putAll(ServiceUtil.returnError("Application Registry not exists!"));
    			result.put("clientRegistryId", clientRegistryId);
    			return result;
    		}
    		
    		GenericValue existingRegistry = EntityUtil.getFirst( delegator.findByAnd("ClientApplicationRegistry",UtilMisc.toMap("clientDomain", clientDomain), null, false) );
    		if (UtilValidate.isNotEmpty(existingRegistry) && !clientRegistryId.equalsIgnoreCase(existingRegistry.getString("clientRegistryId"))) {
    			result.putAll(ServiceUtil.returnError("Application Registry already exists!"));
    			result.put("clientRegistryId", clientRegistryId);
    			return result;
    		}
    		
    		if(registry != null){
    		clientRegistryId = registry.getString("clientRegistryId");
    		
    		//registry.put("clientRegistryId", clientRegistryId);
    		registry.put("clientName", clientName);
    		registry.put("clientDomain", clientDomain);
    		registry.put("comments", comments);
    		registry.put("externalReferenceId", externalReferenceId);
    		
    		if (UtilValidate.isNotEmpty(appStatusId)) {
    			registry.put("appStatusId", appStatusId);
    		}
    		if (UtilValidate.isNotEmpty(clientAppPassword)) {
    			registry.put("clientAppPassword", clientAppPassword);
    		}
    		
    		registry.store();
    		}
    		
    		GenericValue appStatus = AppUtil.getActiveAppStatus(delegator, clientRegistryId);
    		if (appStatus != null) {
    			String secretCode = appStatus.getString("secretCode");
	    			if(UtilValidate.isNotEmpty(secretCode))
	    			result.put("secretCode", appStatus.getString("secretCode"));
    		}
    		
    		result.put("clientRegistryId", clientRegistryId);
    		if (registry != null) {
    		    result.put("appStatusId", registry.getString("appStatusId"));
    		}
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated App registry.."));
    	
    	return result;
    	
    }
    
    public static Map createAppStatus(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String clientRegistryId = (String) context.get("clientRegistryId");
    	
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("clientRegistryId", clientRegistryId);
    	
    	try {
        	
    		GenericValue registry = EntityUtil.getFirst( delegator.findByAnd("ClientApplicationRegistry", UtilMisc.toMap("clientRegistryId", clientRegistryId), null, false) );
    		
    		if (UtilValidate.isEmpty(registry)) {
    			result.putAll(ServiceUtil.returnError("Application Registry not exists!"));
    			return result;
    		}
    		
    		DataUtil.prepareAppStatusData(context);
    		
    		String sourceInvoked = (String) context.get("sourceInvoked");
    		
    		AppUtil.expireAllActiveAppStatus(delegator, clientRegistryId);
    		
			GenericValue applicationStatus = delegator.makeValue("ClientApplicationStatus");
			
			String securedSecretCode = CommonUtils.getSecureRandomString(Integer.parseInt(UtilProperties.getPropertyValue(GlobalConstants.configResource, "access.token.length")));
			
			applicationStatus.put("clientRegistryId", clientRegistryId);
			applicationStatus.put("clientStatusId", delegator.getNextSeqId("ClientApplicationStatus"));
			
			applicationStatus.put("secretCode", securedSecretCode);
			applicationStatus.put("generatedByUserLogin", userLogin.getString("userLoginId"));
			applicationStatus.put("sourceInvoked", sourceInvoked);

			if (UtilValidate.isEmpty(fromDate)) {
				applicationStatus.put("fromDate", UtilDateTime.nowTimestamp());
			} else {
				applicationStatus.put("fromDate", fromDate);
			}
			
			if (UtilValidate.isNotEmpty(thruDate)) {
				applicationStatus.put("thruDate", thruDate);
			} else {
				applicationStatus.put("thruDate", UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), 8));
			}
			
			applicationStatus.create();
			
			result.put("secretCode", securedSecretCode);
    			
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created App Status.."));
    	
    	return result;
    	
    }
    
    public static Map deleteAppStatus(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String clientRegistryId = (String) context.get("clientRegistryId");
    	String clientStatusId = (String) context.get("clientStatusId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("clientRegistryId", clientRegistryId);
    	
    	try {
        	
    		GenericValue appStatus = EntityUtil.getFirst( delegator.findByAnd("ClientApplicationStatus", UtilMisc.toMap("clientRegistryId", clientRegistryId, "clientStatusId", clientStatusId), null, false) );
    		
    		if (UtilValidate.isEmpty(appStatus)) {
    			result.putAll(ServiceUtil.returnError("Application Status not exists!"));
    			return result;
    		}
    		
    		appStatus.remove();
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted App Status.."));
    	
    	return result;
    	
    }
    
    public static Map expireAppStatus(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String clientRegistryId = (String) context.get("clientRegistryId");
    	String clientStatusId = (String) context.get("clientStatusId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("clientRegistryId", clientRegistryId);
    	
    	try {
        	
    		GenericValue appStatus = EntityUtil.getFirst( delegator.findByAnd("ClientApplicationStatus", UtilMisc.toMap("clientRegistryId", clientRegistryId, "clientStatusId", clientStatusId), null, false) );
    		
    		if (UtilValidate.isEmpty(appStatus)) {
    			result.putAll(ServiceUtil.returnError("Application Status not exists!"));
    			return result;
    		}
    		
    		if (UtilValidate.isEmpty(appStatus.get("fromDate"))) {
				appStatus.put("fromDate", UtilDateTime.nowTimestamp());
			}
			
			appStatus.put("thruDate", UtilDateTime.nowTimestamp());
    		
    		appStatus.store();
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully expired App Status.."));
    	
    	return result;
    	
    }
	
}
