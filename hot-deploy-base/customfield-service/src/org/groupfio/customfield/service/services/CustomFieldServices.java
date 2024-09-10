/**
 * 
 */
package org.groupfio.customfield.service.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.customfield.service.CustomfieldServiceConstants.AttributeFieldType;
import org.groupfio.customfield.service.CustomfieldServiceConstants.GroupType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Sharif
 *
 */
public class CustomFieldServices {

	private static final String MODULE = CustomFieldServices.class.getName();
    
    public static Map createCustomField(DispatchContext dctx, Map context) {
    	
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
    	String paramDisplayType = (String) context.get("paramDisplayType");
    	String productPromoCodeGroupId = (String) context.get("productPromoCodeGroupId");
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
    		result.put("groupId", groupId);
    		Map<String, Object> conds = UtilMisc.toMap("customFieldName", customFieldName);
    		if (UtilValidate.isNotEmpty(groupId)) {
    			conds.put("groupId", groupId);
    		}
    		
    		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", conds, null, false) );
    		
    		if (UtilValidate.isNotEmpty(customField)) {
    			result.putAll(ServiceUtil.returnError("Attribute field already exists!"));
    			return result;
    		}
    		
    		customField = delegator.makeValue("CustomField");
    		
    		if (UtilValidate.isEmpty(customFieldId)) {
    			customFieldId = delegator.getNextSeqId("CustomField");
    		}
    		
    		customField.put("customFieldId", customFieldId);
    		//customField.put("roleTypeId", roleTypeId);
    		
    		customField.put("groupType", GroupType.CUSTOM_FIELD);
    		
    		customField.put("customFieldFormat", customFieldFormat);
    		customField.put("customFieldName", customFieldName);
    		customField.put("customFieldType", customFieldType);
    		customField.put("hide", UtilValidate.isNotEmpty(hide) ? hide : "N");
    		customField.put("paramDisplayType", paramDisplayType);
    		customField.put("productPromoCodeGroupId", productPromoCodeGroupId);
    		
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
    		
    		//result.put("groupId", groupId);
    		result.put("customFieldId", customFieldId);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created Attribute field .."));
    	
    	return result;
    	
    }
    
    public static Map updateCustomField(DispatchContext dctx, Map context) {
    	
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
    	String paramDisplayType = (String) context.get("paramDisplayType");
    	String productPromoCodeGroupId = (String) context.get("productPromoCodeGroupId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupId", groupId);
		result.put("customFieldId", customFieldId);
    	
    	try {
        	
    		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
    		
    		if (UtilValidate.isEmpty(customField)) {
    			result.putAll(ServiceUtil.returnError("Attribute field not exists!"));
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
				result.putAll(ServiceUtil.returnError("Attribute field already exists!"));
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
    		customField.put("paramDisplayType", paramDisplayType);
    		customField.put("productPromoCodeGroupId", productPromoCodeGroupId);
    		
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
	    		} else {
					roleContext.put("customFieldId", customFieldId);
					roleContext.put("roleTypeId", roleTypeId);
					roleContext.put("sequenceNumber", "1");
					SegmentServices.createRoleConfig(delegator, roleContext);
	    		}
			}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated Attribute field .."));
    	
    	return result;
    	
    }
    
    public static Map deleteCustomField(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String customFieldId = (String) context.get("customFieldId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
    		
    		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField",UtilMisc.toMap("customFieldId", customFieldId), null, false) );
    		
    		if (UtilValidate.isEmpty(customField)) {
    			result.putAll(ServiceUtil.returnError("Attribute field not exists!"));
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
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted Attribute field.."));
    	
    	return result;
    	
    }
    
    public static Map < String, Object > createUpdateCustomValueMulti(DispatchContext dctx, Map < String, Object > context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        String customFieldId = (String) context.get("customFieldId");
        String customFieldValue = (String) context.get("customFieldValue");
        String customFieldParamValue = (String) context.get("customFieldParamValue");
        String partyId = (String) context.get("partyId");
        String action = (String) context.get("action");
        
        try {
            String serviceName = null;
            if ("CREATE".equals(action)) {
                serviceName = "customfield.createFieldValue";
            } else if ("UPDATE".equals(action)) {
                serviceName = "customfield.updateFieldValue";
            }
            
            if (UtilValidate.isNotEmpty(customFieldParamValue)) {
            	customFieldValue = customFieldParamValue;
            }
            
            if (UtilValidate.isNotEmpty(customFieldId) && UtilValidate.isNotEmpty(serviceName)) {
            	GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldId", customFieldId).queryOne();
            	if (UtilValidate.isNotEmpty(customField)) {
            		
            		if (UtilValidate.isNotEmpty(customField.getString("customFieldFormat")) && customField.getString("customFieldFormat").equals("LABEL_TEXT")) {
            			return ServiceUtil.returnSuccess("Label type field not required for create/update");
            		}
            		
            		if (UtilValidate.isNotEmpty(customFieldParamValue) || (UtilValidate.isNotEmpty(customField.getString("customFieldFormat")) && customField.getString("customFieldFormat").equals("CHECK_BOX"))) {
            			if (UtilValidate.isNotEmpty(customFieldValue) && new String(""+customFieldValue.charAt(0)).equals("[")) {
            				customFieldValue = customFieldValue.substring(1, customFieldValue.length()-1);
            			} else if (UtilValidate.isEmpty(customFieldValue)) {
            				customFieldValue = "N";
            			}
            		}
            		if (UtilValidate.isNotEmpty(customFieldParamValue)) {
            			customFieldValue = "{"+customFieldValue+"}";
            		}
            		
            		ModelService createUpdateField = dctx.getModelService(serviceName);
                    Map<String, Object> input = createUpdateField.makeValid(context, ModelService.IN_PARAM);
                    
                    input.put("customFieldValue", customFieldValue);

                    Map<String, Object> serviceResults = dispatcher.runSync(serviceName, input);
            	}
            }
        } catch (Exception ex) {
            Debug.logInfo("==========================ERROR======================" + ex.toString(), "");
            Debug.logError(ex.getMessage(), MODULE);
        }

        Map < String, Object > result = ServiceUtil.returnSuccess("Successfully updated attributes");
        return result;
    }

    public static Map < String, Object > createFieldValue(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        String customFieldId = (String) context.get("customFieldId");
        String customFieldValue = (String) context.get("customFieldValue");
        String customFieldParamValue = (String) context.get("customFieldParamValue");
        String partyId = (String) context.get("partyId");
        String groupId = (String) context.get("groupId");

        String domainEntityType = (String) context.get("domainEntityType");
        String domainEntityId = (String) context.get("domainEntityId");

        try {

            GenericValue fieldValue = null;
            if (UtilValidate.isNotEmpty(domainEntityType) && !CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
                GenericValue attrEntityAssoc = EntityUtil.getFirst(delegator.findByAnd("AttrEntityAssoc", UtilMisc.toMap("domainEntityType", domainEntityType), null, true));
                if (UtilValidate.isNotEmpty(attrEntityAssoc)) {
                    fieldValue = delegator.makeValue(attrEntityAssoc.getString("entityName"), UtilMisc.toMap(attrEntityAssoc.getString("customFieldIdColumn"), customFieldId, attrEntityAssoc.getString("domainEntityIdColumn"), domainEntityId, attrEntityAssoc.getString("fieldValueColumn"), customFieldValue));
                    if (UtilValidate.isNotEmpty(attrEntityAssoc.getString("groupIdColumn"))) {
                        fieldValue.put(attrEntityAssoc.getString("groupIdColumn"), groupId);
                    }
                } else {
                    fieldValue = delegator.makeValue("AttributeValueAssoc", UtilMisc.toMap("customFieldId", customFieldId, "domainEntityId", domainEntityId, "domainEntityType", domainEntityType, "fieldValue", customFieldValue));
                }
            } else {
                fieldValue = delegator.makeValue("CustomFieldValue", UtilMisc.toMap("customFieldId", customFieldId, "partyId", partyId, "fieldValue", customFieldValue));
            }

            fieldValue.create();
        } catch (GeneralException e) {
            Debug.logError(e, MODULE);
            return ServiceUtil.returnError("ErrorwithService: " + e.toString());
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map < String, Object > updateFieldValue(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        String customFieldId = (String) context.get("customFieldId");
        String customFieldValue = (String) context.get("customFieldValue");
        String customFieldParamValue = (String) context.get("customFieldParamValue");
        String partyId = (String) context.get("partyId");
        String groupId = (String) context.get("groupId");

        String domainEntityType = (String) context.get("domainEntityType");
        String domainEntityId = (String) context.get("domainEntityId");

        try {

            GenericValue fieldValue = null;
            if (UtilValidate.isNotEmpty(domainEntityType) && !CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
                GenericValue attrEntityAssoc = EntityUtil.getFirst(delegator.findByAnd("AttrEntityAssoc", UtilMisc.toMap("domainEntityType", domainEntityType), null, true));
                if (UtilValidate.isNotEmpty(attrEntityAssoc)) {
                    fieldValue = delegator.makeValue(attrEntityAssoc.getString("entityName"), UtilMisc.toMap(attrEntityAssoc.getString("customFieldIdColumn"), customFieldId, attrEntityAssoc.getString("domainEntityIdColumn"), domainEntityId, attrEntityAssoc.getString("fieldValueColumn"), customFieldValue));
                    if (UtilValidate.isNotEmpty(attrEntityAssoc.getString("groupIdColumn"))) {
                        fieldValue.put(attrEntityAssoc.getString("groupIdColumn"), groupId);
                    }
                } else {
                    fieldValue = delegator.makeValue("AttributeValueAssoc", UtilMisc.toMap("customFieldId", customFieldId, "domainEntityId", domainEntityId, "domainEntityType", domainEntityType, "fieldValue", customFieldValue));
                }
            } else {
                fieldValue = delegator.makeValue("CustomFieldValue", UtilMisc.toMap("customFieldId", customFieldId, "partyId", partyId, "fieldValue", customFieldValue));
            }
            delegator.createOrStore(fieldValue);
        } catch (GeneralException e) {
            Debug.logError(e, MODULE);
        }

        Map returnMap = ServiceUtil.returnSuccess();
        return returnMap;
    }
    
}
