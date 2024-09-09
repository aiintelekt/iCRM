/**
 * 
 */
package org.groupfio.customfield.service.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.customfield.service.CustomfieldServiceConstants.GroupType;
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
public class MicroServiceConfigServices {

	private static final String MODULE = MicroServiceConfigServices.class.getName();
    
    public static Map createMicroServiceConfig(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String microUrl = (String) context.get("microUrl");
    	String authKey = (String) context.get("authKey");
    	String microSeqNum = (String) context.get("microSeqNum");
    	String serviceName = (String) context.get("serviceName");
    	String isEnabled = (String) context.get("isEnabled");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue microServiceConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldMicroServiceConfig", UtilMisc.toMap("authKey", authKey), null, false) );
    		
    		if (UtilValidate.isNotEmpty(microServiceConfig)) {
    			result.putAll(ServiceUtil.returnError("MicroService Config already exists!"));
    			return result;
    		}
    		
    		microServiceConfig = delegator.makeValue("CustomFieldMicroServiceConfig");
    		
    		String configId = delegator.getNextSeqId("CustomFieldMicroServiceConfig");
    		
    		microServiceConfig.put("customFieldMicroServiceConfigId", configId);
    		
    		microServiceConfig.put("microUrl", microUrl);
    		microServiceConfig.put("authKey", authKey);
    		microServiceConfig.put("serviceName", serviceName);
    		microServiceConfig.put("isEnabled", isEnabled);
    		
    		if (UtilValidate.isNotEmpty(microSeqNum)) {
    			microServiceConfig.put("microSeqNum", Long.parseLong(microSeqNum));
    		}
    		
    		microServiceConfig.create();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created MicroService Config.."));
    	
    	return result;
    	
    }
    
    public static Map updateMicroServiceConfig(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String configId = (String) context.get("configId");
    	
    	String microUrl = (String) context.get("microUrl");
    	String authKey = (String) context.get("authKey");
    	String microSeqNum = (String) context.get("microSeqNum");
    	String serviceName = (String) context.get("serviceName");
    	String isEnabled = (String) context.get("isEnabled");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("configId", configId);
    	
    	try {
        	
    		GenericValue microServiceConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldMicroServiceConfig",UtilMisc.toMap("customFieldMicroServiceConfigId", configId), null, false) );
    		
    		if (UtilValidate.isEmpty(microServiceConfig)) {
    			result.putAll(ServiceUtil.returnError("MicroService Config not exists!"));
    			return result;
    		}
    		
    		microServiceConfig.put("microUrl", microUrl);
    		microServiceConfig.put("authKey", authKey);
    		microServiceConfig.put("serviceName", serviceName);
    		microServiceConfig.put("isEnabled", isEnabled);
    		
    		if (UtilValidate.isNotEmpty(microSeqNum)) {
    			microServiceConfig.put("microSeqNum", Long.parseLong(microSeqNum));
    		}
    		
    		microServiceConfig.store();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated MicroService Config.."));
    	
    	return result;
    	
    }
    
    public static Map deleteMicroServiceConfig(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String configId = (String) context.get("configId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue microServiceConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldMicroServiceConfig",UtilMisc.toMap("customFieldMicroServiceConfigId", configId), null, false) );
    		
    		if (UtilValidate.isEmpty(microServiceConfig)) {
    			result.putAll(ServiceUtil.returnError("MicroService Config not exists!"));
    			return result;
    		}
    		
    		microServiceConfig.remove();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted MicroService Config.."));
    	
    	return result;
    	
    }
    
}
