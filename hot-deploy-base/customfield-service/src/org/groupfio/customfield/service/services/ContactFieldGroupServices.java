/**
 * 
 */
package org.groupfio.customfield.service.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.customfield.service.CustomfieldServiceConstants.AttributeFieldType;
import org.groupfio.customfield.service.CustomfieldServiceConstants.GroupType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Sharif
 *
 */
public class ContactFieldGroupServices {

	private static final String MODULE = ContactFieldGroupServices.class.getName();
    
    public static Map createContactFieldGroup(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	String groupName = (String) context.get("groupName");
    	String sequence = (String) context.get("sequence");
    	String hide = (String) context.get("hide");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
    		
    		EntityCondition existCondition = EntityCondition.makeCondition(
    				UtilMisc.toList(
    						EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId),
    		                EntityCondition.makeCondition("groupName", EntityOperator.EQUALS, groupName)
    						), EntityOperator.OR);
        	
    		GenericValue group = EntityUtil.getFirst( delegator.findList("CustomFieldGroup", existCondition, null, null, null, false) );
    		
    		if (UtilValidate.isNotEmpty(group)) {
    			result.putAll(ServiceUtil.returnError("Contact field group already exists!"));
    			return result;
    		}
    		
    		group = delegator.makeValue("CustomFieldGroup");
    		
    		group.put("groupType", GroupType.CONTACT_FIELD);
    		
    		group.put("groupId", groupId);
    		group.put("groupName", groupName);
    		group.put("hide", UtilValidate.isNotEmpty(hide) ? hide : "N");
    		
    		group.put("sequence", UtilValidate.isNotEmpty(sequence) ? Long.parseLong(sequence) : new Long(1));
    		
    		group.create();
    		
    		result.put("groupId", groupId);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created contact field group."));
    	
    	return result;
    	
    }
    
    public static Map updateContactFieldGroup(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	
    	String groupName = (String) context.get("groupName");
    	String sequence = (String) context.get("sequence");
    	String hide = (String) context.get("hide");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupId", groupId);
    	
    	try {
        	
    		GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    		
    		if (UtilValidate.isEmpty(group)) {
    			result.putAll(ServiceUtil.returnError("Contact field group not exists!"));
    			return result;
    		}
    		
    		EntityCondition existCondition = EntityCondition.makeCondition(
    				UtilMisc.toList(
    						EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, groupId),
    		                EntityCondition.makeCondition("groupName", EntityOperator.EQUALS, groupName)
    						), EntityOperator.AND);
        	
    		GenericValue existGroup = EntityUtil.getFirst( delegator.findList("CustomFieldGroup", existCondition, null, null, null, false) );
    		if (UtilValidate.isNotEmpty(existGroup)) {
    			result.putAll(ServiceUtil.returnError("Contact field group name already exists!"));
    			return result;
    		}
    		
    		group.put("groupName", groupName);
    		group.put("hide", UtilValidate.isNotEmpty(hide) ? hide : "N");
    		
    		group.put("sequence", UtilValidate.isNotEmpty(sequence) ? Long.parseLong(sequence) : new Long(1));
    		
    		group.store();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated Contact field group.."));
    	
    	return result;
    	
    }
    
    public static Map deleteContactFieldGroup(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    		
    		if (UtilValidate.isEmpty(group)) {
    			result.putAll(ServiceUtil.returnError("Contact field group not exists!"));
    			return result;
    		}
    		
    		List<GenericValue> customFields = delegator.findByAnd("CustomField", UtilMisc.toMap("groupId", groupId), null, false);
    		for (GenericValue customField : customFields) {
    			
    			Map<String, Object> customFieldRemoveContext = new HashMap<String, Object>();
        		
    			customFieldRemoveContext.put("customFieldId", customField.getString("customFieldId"));
    			customFieldRemoveContext.put("userLogin", userLogin);
        		
        		Map<String, Object> customFieldRemoveResult = dispatcher.runSync("customfield.deleteCustomField", customFieldRemoveContext);
    			
    		}
    		
    		group.remove();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted Contact field group."));
    	
    	return result;
    	
    }
    
    public static Map createContactField(DispatchContext dctx, Map context) {    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String roleTypeId = (String) context.get("roleTypeId");
    	String groupId = (String) context.get("groupId");
    	String customFieldId = (String) context.get("customFieldId");
    	
    	String customFieldFormat = (String) context.get("customFieldFormat");
    	String customFieldName = (String) context.get("customFieldName");
    	String sequenceNumber = (String) context.get("sequenceNumber");
    	String customFieldType = (String) context.get("customFieldType");
    	String customFieldLength = (String) context.get("customFieldLength");
    	String hide = (String) context.get("hide");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		Map<String, Object> conds = UtilMisc.toMap("customFieldName", customFieldName);
    		if (UtilValidate.isNotEmpty(groupId)) {
    			conds.put("groupId", groupId);
    		}
    		
    		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", conds, null, false) );
    		
    		if (UtilValidate.isNotEmpty(customField)) {
    			result.putAll(ServiceUtil.returnError("Contact field already exists!"));
    			return result;
    		}
    		
    		customField = delegator.makeValue("CustomField");
    		
    		if (UtilValidate.isEmpty(customFieldId)) {
    			customFieldId = delegator.getNextSeqId("CustomField");
    		}
    		
    		customField.put("customFieldId", customFieldId);
    		//customField.put("roleTypeId", roleTypeId);
    		
    		customField.put("groupType", GroupType.CONTACT_FIELD);
    		
    		customField.put("customFieldFormat", customFieldFormat);
    		customField.put("customFieldName", customFieldName);
    		customField.put("customFieldType", customFieldType);
    		customField.put("hide", UtilValidate.isNotEmpty(hide) ? hide : "N");
    		
    		if (UtilValidate.isNotEmpty(customFieldLength)) {
    			customField.put("customFieldLength", Long.parseLong(customFieldLength));
    		}
    			
    		customField.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1));
    		
    		if (UtilValidate.isNotEmpty(groupId)) {
    			GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    			if (UtilValidate.isNotEmpty(group)) {
    				customField.put("groupName", group.getString("groupName"));
    				customField.put("groupId", groupId);
    			}
    		}
    		
    		customField.create();
    		Debug.log(groupId+" "+roleTypeId+" "+customFieldId);
    		Map<String, Object> roleContext = new HashMap<String, Object>();
			if(UtilValidate.isNotEmpty(roleTypeId)){
				//roleContext.put("groupId", groupId);
				roleContext.put("roleTypeId", roleTypeId);
				roleContext.put("customFieldId", customFieldId);
				roleContext.put("sequenceNumber", "1");
				SegmentServices.createRoleConfig(delegator, roleContext);
			}
    		
    		result.put("groupId", groupId);
    		result.put("customFieldId", customFieldId);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created Contact field."));
    	
    	return result;
    	
    }
    
    public static Map updateContactField(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String customFieldId = (String) context.get("customFieldId");
    	
    	String roleTypeId = (String) context.get("roleTypeId");
    	String groupId = (String) context.get("groupId");
    	
    	String customFieldFormat = (String) context.get("customFieldFormat");
    	String customFieldName = (String) context.get("customFieldName");
    	String sequenceNumber = (String) context.get("sequenceNumber");
    	String customFieldType = (String) context.get("customFieldType");
    	String customFieldLength = (String) context.get("customFieldLength");
    	String hide = (String) context.get("hide");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupId", groupId);
		result.put("customFieldId", customFieldId);
    	
    	try {
        	
    		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
    		
    		if (UtilValidate.isEmpty(customField)) {
    			result.putAll(ServiceUtil.returnError("Contact field not exists!"));
    			return result;
    		}
    		
    		EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
					//EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId),
					EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId),
					EntityCondition.makeCondition("customFieldName", EntityOperator.EQUALS, customFieldName),
					EntityCondition.makeCondition("customFieldId", EntityOperator.NOT_EQUAL, customFieldId)
					);
			
			List<GenericValue> customFields = delegator.findList("CustomField", conditions, null, null, null, false);
			if (UtilValidate.isNotEmpty(customFields)) {
				result.putAll(ServiceUtil.returnError("Contact field already exists!"));
    			return result;
			}
			
			if (customField.getString("customFieldType").equals(AttributeFieldType.MULTIPLE) && customFieldType.equals(AttributeFieldType.SINGLE)) {
				delegator.removeByAnd("CustomFieldMultiValue", UtilMisc.toMap("customFieldId", customFieldId));
			}
			
			//customField.put("roleTypeId", roleTypeId);
    		
    		customField.put("customFieldFormat", customFieldFormat);
    		customField.put("customFieldName", customFieldName);
    		customField.put("customFieldType", customFieldType);
    		customField.put("hide", UtilValidate.isNotEmpty(hide) ? hide : "N");
    		
    		if (UtilValidate.isNotEmpty(customFieldLength)) {
    			customField.put("customFieldLength", Long.parseLong(customFieldLength));
    		}
    		
    		customField.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1));
    		
    		if (UtilValidate.isNotEmpty(groupId)) {
    			GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    			if (UtilValidate.isNotEmpty(group)) {
    				customField.put("groupName", group.getString("groupName"));
    				customField.put("groupId", groupId);
    			}
    		}
    		
    		customField.store();
    		Map<String, Object> roleContext = new HashMap<String, Object>();
			if(UtilValidate.isNotEmpty(roleTypeId)){
				GenericValue roleTypeStore = EntityUtil.getFirst( delegator.findByAnd("CustomFieldRoleConfig",UtilMisc.toMap("customFieldId",customFieldId), null, false) );
	    		
	    		if (UtilValidate.isNotEmpty(roleTypeStore)) {
	    			roleTypeStore.put("roleTypeId", roleTypeId);
	    			roleTypeStore.store();
	    		}
				
	    		/*Debug.log(groupId+" "+roleTypeId+" "+customFieldId);
				roleContext.put("groupId", groupId);
				roleContext.put("customFieldId", customFieldId);
				roleContext.put("roleTypeId", roleTypeId);
				roleContext.put("sequenceNumber", "1");
				SegmentService.createRoleConfig(delegator, roleContext);*/
			}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated Contact field."));
    	
    	return result;
    	
    }
    
    public static Map deleteContactField(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String customFieldId = (String) context.get("customFieldId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
    		
    		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField",UtilMisc.toMap("customFieldId", customFieldId), null, false) );
    		
    		if (UtilValidate.isEmpty(customField)) {
    			result.putAll(ServiceUtil.returnError("Contact field not exists!"));
    			return result;
    		}
    		String groupId = customField.getString("groupId");
    		
    		delegator.removeAll( delegator.findByAnd("CustomFieldValue",UtilMisc.toMap("customFieldId", customFieldId), null, false) );
    		delegator.removeAll( delegator.findByAnd("CustomFieldMultiValue",UtilMisc.toMap("customFieldId", customFieldId), null, false) );
    		delegator.removeAll( delegator.findByAnd("CustomFieldRoleConfig", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
    		
    		customField.remove();
    		
    		result.put("groupId", groupId);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted Contact field.."));
    	
    	return result;
    	
    }
    
}
