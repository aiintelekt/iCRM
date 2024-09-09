/**
 * 
 */
package org.groupfio.contact.portal.service;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class ContactServices {

	private static final String MODULE = ContactServices.class.getName();
	
	public static Map createContact(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		
    		callResult = dispatcher.runSync("crmsfa.createContact", context);
    		
    		result.putAll(callResult);
    		
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	
    	return result;
    	
    }
	
	public static Map updateContact(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		
    		callResult = dispatcher.runSync("crmsfa.updateContact", context);
    		
    		result.putAll(callResult);
    		
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	
    	return result;
    	
    }
    
}
