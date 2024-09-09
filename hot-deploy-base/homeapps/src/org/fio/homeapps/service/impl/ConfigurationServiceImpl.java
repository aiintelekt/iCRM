package org.fio.homeapps.service.impl;

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
 * @author Group Fio
 *
 */
public class ConfigurationServiceImpl {

	private static final String MODULE = ConfigurationServiceImpl.class.getName();
    
    public static Map createServiceConfig(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String serviceName = (String) context.get("serviceName");
    	String componentName = (String) context.get("componentName");
    	String isPublic = (String) context.get("isPublic");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue serviceConfig = EntityUtil.getFirst( delegator.findByAnd("ServiceConfig",UtilMisc.toMap("serviceName", serviceName, "componentName", componentName), null, false) );
    		
    		if (UtilValidate.isNotEmpty(serviceConfig)) {
    			result.putAll(ServiceUtil.returnError("Service Configuration already exists!"));
    			return result;
    		}
    		
    		serviceConfig = delegator.makeValue("ServiceConfig");
    		
    		String serviceConfigId = delegator.getNextSeqId("ServiceConfig");
    		
    		serviceConfig.put("serviceConfigId", serviceConfigId);
    		serviceConfig.put("serviceName", serviceName);
    		serviceConfig.put("componentName", componentName);
    		
    		serviceConfig.put("isPublic", isPublic);
    		
    		serviceConfig.create();
    		
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created Service Config.."));
    	
    	return result;
    	
    }
    
    public static Map updateServiceConfig(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String serviceConfigId = (String) context.get("serviceConfigId");
    	
    	String serviceName = (String) context.get("serviceName");
    	String componentName = (String) context.get("componentName");
    	String isPublic = (String) context.get("isPublic");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue serviceConfig = EntityUtil.getFirst( delegator.findByAnd("ServiceConfig",UtilMisc.toMap("serviceConfigId", serviceConfigId), null, false) );
    		
    		if (UtilValidate.isEmpty(serviceConfig)) {
    			result.putAll(ServiceUtil.returnError("Service Configuration not exists!"));
    			return result;
    		}
    		
    		serviceConfig.put("serviceName", serviceName);
    		
    		serviceConfig.put("isPublic", isPublic);
    		
    		serviceConfig.store();
    		
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated App registry.."));
    	
    	return result;
    	
    }
    
    public static Map deleteServiceConfig(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String serviceConfigId = (String) context.get("serviceConfigId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue serviceConfig = EntityUtil.getFirst( delegator.findByAnd("ServiceConfig",UtilMisc.toMap("serviceConfigId", serviceConfigId), null, false) );
    		
    		if (UtilValidate.isEmpty(serviceConfig)) {
    			result.putAll(ServiceUtil.returnError("Service Configuration not exists!"));
    			return result;
    		}
    		
    		serviceConfig.remove();
    		
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully removed Service Config.."));
    	
    	return result;
    	
    }
    
}
