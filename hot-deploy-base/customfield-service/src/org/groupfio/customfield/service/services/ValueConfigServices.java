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
public class ValueConfigServices {

	private static final String MODULE = ValueConfigServices.class.getName();
    
    public static Map createValueConfig(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	String customFieldId = (String) context.get("customFieldId");
    	String valueCapture = (String) context.get("valueCapture");
    	String valueSeqNum = (String) context.get("valueSeqNum");
    	
    	String valueMin = (String) context.get("valueMin");
    	String valueMax = (String) context.get("valueMax");
    	String valueData = (String) context.get("valueData");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue valueConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldValueConfig", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "valueCapture", valueCapture, "valueSeqNum", Long.parseLong(valueSeqNum)), null, false) );
    		
    		if (UtilValidate.isNotEmpty(valueConfig)) {
    			result.putAll(ServiceUtil.returnError("Value Config already exists!"));
    			return result;
    		}
    		
    		valueConfig = delegator.makeValue("CustomFieldValueConfig");
    		
    		valueConfig.put("groupId", groupId);
    		valueConfig.put("customFieldId", customFieldId);
    		valueConfig.put("valueCapture", valueCapture);
    		valueConfig.put("valueMin", valueMin);
    		valueConfig.put("valueMax", valueMax);
    		valueConfig.put("valueData", valueData);
    		
    		if (UtilValidate.isNotEmpty(valueSeqNum)) {
    			valueConfig.put("valueSeqNum", Long.parseLong(valueSeqNum));
    		}
    		
    		valueConfig.create();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created Value Config.."));
    	
    	return result;
    	
    }
    
    public static Map updateValueConfig(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	String customFieldId = (String) context.get("customFieldId");
    	String valueCapture = (String) context.get("valueCapture");
    	String valueSeqNum = (String) context.get("valueSeqNum");
    	
    	String valueMin = (String) context.get("valueMin");
    	String valueMax = (String) context.get("valueMax");
    	String valueData = (String) context.get("valueData");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupId", groupId);
		result.put("customFieldId", customFieldId);
		result.put("valueCapture", valueCapture);
		result.put("valueSeqNum", valueSeqNum);
    	
    	try {
        	
    		GenericValue valueConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldValueConfig",UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "valueCapture", valueCapture, "valueSeqNum", Long.parseLong(valueSeqNum)), null, false) );
    		
    		if (UtilValidate.isEmpty(valueConfig)) {
    			result.putAll(ServiceUtil.returnError("Value Config not exists!"));
    			return result;
    		}
    		
    		valueConfig.put("valueMin", valueMin);
    		valueConfig.put("valueMax", valueMax);
    		valueConfig.put("valueData", valueData);
    		
    		valueConfig.store();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated Value Config.."));
    	
    	return result;
    	
    }
    
    public static Map deleteValueConfig(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	String customFieldId = (String) context.get("customFieldId");
    	
    	String valueCapture = (String) context.get("valueCapture");
    	String valueSeqNum = (String) context.get("valueSeqNum");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue valueConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldValueConfig",UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "valueCapture", valueCapture, "valueSeqNum", Long.parseLong(valueSeqNum)), null, false) );
    		
    		if (UtilValidate.isEmpty(valueConfig)) {
    			result.putAll(ServiceUtil.returnError("Value Config not exists!"));
    			return result;
    		}
    		
    		valueConfig.remove();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted Value Config.."));
    	
    	return result;
    	
    }
    
}
