/**
 * 
 */
package org.groupfio.customfield.service.services;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Sharif
 *
 */
public class WebhookConfigServices {

	private static final String MODULE = WebhookConfigServices.class.getName();
    
    public static Map createWebhookConfig(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String webhookUrl = (String) context.get("webhookUrl");
    	String authKey = (String) context.get("authKey");
    	String webhookSeqNum = (String) context.get("webhookSeqNum");
    	String serviceName = (String) context.get("serviceName");
    	String isEnabled = (String) context.get("isEnabled");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue microServiceConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldWebhookConfig", UtilMisc.toMap("authKey", authKey), null, false) );
    		
    		if (UtilValidate.isNotEmpty(microServiceConfig)) {
    			result.putAll(ServiceUtil.returnError("Webhook Config already exists!"));
    			return result;
    		}
    		
    		microServiceConfig = delegator.makeValue("CustomFieldWebhookConfig");
    		
    		String configId = delegator.getNextSeqId("CustomFieldWebhookConfig");
    		
    		microServiceConfig.put("customFieldWebhookConfigId", configId);
    		
    		microServiceConfig.put("webhookUrl", webhookUrl);
    		microServiceConfig.put("authKey", authKey);
    		microServiceConfig.put("serviceName", serviceName);
    		microServiceConfig.put("isEnabled", isEnabled);
    		
    		if (UtilValidate.isNotEmpty(webhookSeqNum)) {
    			microServiceConfig.put("webhookSeqNum", Long.parseLong(webhookSeqNum));
    		}
    		
    		microServiceConfig.create();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created Webhook Config.."));
    	
    	return result;
    	
    }
    
    public static Map updateWebhookConfig(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String configId = (String) context.get("configId");
    	
    	String webhookUrl = (String) context.get("webhookUrl");
    	String authKey = (String) context.get("authKey");
    	String webhookSeqNum = (String) context.get("webhookSeqNum");
    	String serviceName = (String) context.get("serviceName");
    	String isEnabled = (String) context.get("isEnabled");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue microServiceConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldWebhookConfig",UtilMisc.toMap("customFieldWebhookConfigId", configId), null, false) );
    		
    		if (UtilValidate.isEmpty(microServiceConfig)) {
    			result.putAll(ServiceUtil.returnError("Webhook Config not exists!"));
    			return result;
    		}
    		
    		microServiceConfig.put("webhookUrl", webhookUrl);
    		microServiceConfig.put("authKey", authKey);
    		microServiceConfig.put("serviceName", serviceName);
    		microServiceConfig.put("isEnabled", isEnabled);
    		
    		if (UtilValidate.isNotEmpty(webhookSeqNum)) {
    			microServiceConfig.put("webhookSeqNum", Long.parseLong(webhookSeqNum));
    		}
    		
    		microServiceConfig.store();
    		
    		result.put("configId", configId);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated Webhook Config.."));
    	
    	return result;
    	
    }
    
    public static Map deleteWebhookConfig(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String configId = (String) context.get("configId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue microServiceConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldWebhookConfig",UtilMisc.toMap("customFieldWebhookConfigId", configId), null, false) );
    		
    		if (UtilValidate.isEmpty(microServiceConfig)) {
    			result.putAll(ServiceUtil.returnError("Webhook Config not exists!"));
    			return result;
    		}
    		
    		microServiceConfig.remove();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted Webhook Config.."));
    	
    	return result;
    	
    }
    
}
