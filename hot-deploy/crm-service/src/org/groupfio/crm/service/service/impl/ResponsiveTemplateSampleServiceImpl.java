/**
 * 
 */
package org.groupfio.crm.service.service.impl;

import java.util.HashMap;
import java.util.List;
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
public class ResponsiveTemplateSampleServiceImpl {

	private static final String MODULE = ResponsiveTemplateSampleServiceImpl.class.getName();
    
    public static Map createCustomFieldGroup(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	String groupName = (String) context.get("groupName");
    	String sequence = (String) context.get("sequence");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue group = EntityUtil.getFirst( delegator.findByAnd("SampleTable",UtilMisc.toMap("groupId", groupId), null, false) );
    		
    		if (UtilValidate.isNotEmpty(group)) {
    			result.putAll(ServiceUtil.returnError("Custom field group already exists!"));
    			return result;
    		}
    		
    		group = delegator.makeValue("SampleTable");
    		
    		group.put("groupId", groupId);
    		group.put("groupName", groupName);
    		
    		if (UtilValidate.isNotEmpty(sequence)) {
    			group.put("sequence", Long.parseLong(sequence));
    		}
    		
    		group.create();
    		
    		result.put("groupId", groupId);
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created Custom field group.."));
    	
    	return result;
    	
    }
    
    public static Map updateCustomFieldGroup(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	
    	String groupName = (String) context.get("groupName");
    	String sequence = (String) context.get("sequence");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue group = EntityUtil.getFirst( delegator.findByAnd("SampleTable",UtilMisc.toMap("groupId", groupId), null, false) );
    		
    		if (UtilValidate.isEmpty(group)) {
    			result.putAll(ServiceUtil.returnError("Custom field group not exists!"));
    			return result;
    		}
    		
    		group.put("groupName", groupName);
    		
    		if (UtilValidate.isNotEmpty(sequence)) {
    			group.put("sequence", Long.parseLong(sequence));
    		}
    		
    		group.store();
    		
    		result.put("groupId", groupId);
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated Custom field group.."));
    	
    	return result;
    	
    }
    
    public static Map deleteCustomFieldGroup(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue group = EntityUtil.getFirst( delegator.findByAnd("SampleTable",UtilMisc.toMap("groupId", groupId), null, false) );
    		
    		if (UtilValidate.isEmpty(group)) {
    			result.putAll(ServiceUtil.returnError("Custom field group not exists!"));
    			return result;
    		}
    		
    		group.remove();
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted Custom field group.."));
    	
    	return result;
    	
    }
    
}
