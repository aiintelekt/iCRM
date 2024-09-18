/**
 * 
 */
package org.groupfio.customfield.service.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.customfield.service.CustomfieldServiceConstants.GroupType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
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
import org.ofbiz.service.ServiceUtil;

/**
 * @author Sharif
 *
 */
public class CustomFieldGroupServices {

	private static final String MODULE = CustomFieldGroupServices.class.getName();
    
    public static Map createCustomFieldGroup(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	String groupName = (String) context.get("groupName");
    	List groupingCodeList = (List) context.get("groupingCode");
    	String sequence = (String) context.get("sequence");
    	String hide = (String) context.get("hide");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
    		
    		if (UtilValidate.isNotEmpty(groupId)) {
    			EntityCondition existCondition = EntityCondition.makeCondition(
        				UtilMisc.toList(
        						EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId)
        						), EntityOperator.OR);
        		GenericValue group = EntityUtil.getFirst( delegator.findList("CustomFieldGroup", existCondition, null, null, null, false) );
        		if (UtilValidate.isNotEmpty(group)) {
        			result.putAll(ServiceUtil.returnError("Attribute field group already exists base on Group ID!"));
        			return result;
        		}
    		}
    		
    		GenericValue group = delegator.makeValue("CustomFieldGroup");
    		if (UtilValidate.isEmpty(groupId)) {
    			groupId = delegator.getNextSeqId("CustomFieldGroup");
    		}
    		
    		group.put("groupType", GroupType.CUSTOM_FIELD);
    		
    		group.put("groupId", groupId);
    		group.put("groupName", groupName);
    		if (UtilValidate.isNotEmpty(groupingCodeList)) {
    			group.put("groupingCode", StringUtil.join(groupingCodeList, ","));
    		}
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
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created Attribute field group.."));
    	
    	return result;
    	
    }
    
    public static Map updateCustomFieldGroup(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	
    	String groupName = (String) context.get("groupName");
    	List groupingCodeList = (List) context.get("groupingCode");
    	String sequence = (String) context.get("sequence");
    	String hide = (String) context.get("hide");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupId", groupId);
    	
    	try {
        	
    		GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    		if (UtilValidate.isEmpty(group)) {
    			result.putAll(ServiceUtil.returnError("Custom field group not exists!"));
    			return result;
    		}
    		
    		EntityCondition existCondition = EntityCondition.makeCondition(
    				UtilMisc.toList(
    						EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, groupId),
    		                EntityCondition.makeCondition("groupName", EntityOperator.EQUALS, groupName)
    						), EntityOperator.AND);
        	
    		GenericValue existGroup = EntityUtil.getFirst( delegator.findList("CustomFieldGroup", existCondition, null, null, null, false) );
    		if (UtilValidate.isNotEmpty(existGroup)) {
    			result.putAll(ServiceUtil.returnError("Attribute field group name already exists exists!"));
    			return result;
    		}
    		
    		group.put("groupName", groupName);
    		group.put("groupingCode", StringUtil.join(groupingCodeList, ","));
    		group.put("hide", UtilValidate.isNotEmpty(hide) ? hide : "N");
    		
    		group.put("sequence", UtilValidate.isNotEmpty(sequence) ? Long.parseLong(sequence) : new Long(1));
    		
    		group.store();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated Attribute field group.."));
    	
    	return result;
    	
    }
    
    public static Map deleteCustomFieldGroup(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue group = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup",UtilMisc.toMap("groupId", groupId), null, false) );
    		
    		if (UtilValidate.isEmpty(group)) {
    			result.putAll(ServiceUtil.returnError("Attribute field group not exists!"));
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
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted Attribute field group.."));
    	
    	return result;
    	
    }
    
    public static Map getAttributeGroupData(DispatchContext dctx, Map context) {
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	String roleType = (String) context.get("roleType");
    	String domainEntityType = (String) context.get("domainEntityType");
    	String domainEntityId = (String) context.get("domainEntityId");
    	String groupType = "CUSTOM_FIELD";
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> responseContext = new HashMap<String, Object>();
    	
    	try {
    		
    		responseContext.put("domainEntityType", domainEntityType);
    		responseContext.put("domainEntityId", domainEntityId);
    		
    		GenericValue attrEntityAssoc = EntityUtil.getFirst( delegator.findByAnd("AttrEntityAssoc", UtilMisc.toMap("domainEntityType", domainEntityType), null, true) );
    		
    		if (UtilValidate.isEmpty(roleType)) {
    			roleType = domainEntityType;
    		}
    		
    		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleType));
			conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
			conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, "Y")
                    ));
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("hide", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("hide", EntityOperator.EQUALS, "N")
                    ));
			
			EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> fieldRoleConfigList = EntityQuery.use(delegator).from("CustomFieldRoleConfigSummary").where(mainConditon).queryList();
			List<String> groupIdLst = EntityUtil.getFieldListFromEntityList(fieldRoleConfigList, "groupId", true);
			
			if (UtilValidate.isNotEmpty(groupIdLst)) {
				conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.IN, groupIdLst));
				conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "CUSTOM_FIELD"));
				mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue group = EntityQuery.use(delegator).from("CustomFieldGroup").where(mainConditon).queryFirst();
				responseContext.put("group", group);
				
				if (UtilValidate.isNotEmpty(group.getString("groupingCode"))) {
					conditions = new ArrayList<EntityCondition>();
					conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
							EntityCondition.makeCondition("customFieldGroupingCodeId", EntityOperator.EQUALS, group.getString("groupingCode")),
							EntityCondition.makeCondition("groupingCode", EntityOperator.EQUALS, group.getString("groupingCode")),
							EntityCondition.makeCondition("description", EntityOperator.EQUALS, group.getString("groupingCode"))
							));
					if (UtilValidate.isNotEmpty(groupType)) {
						conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
					}
		        	mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		        	GenericValue code = EntityQuery.use(delegator).from("CustomFieldGroupingCode").where(mainConditon).queryFirst();
					responseContext.put("code", code);
				} 
				
				Map<String, Object> groupFieldList = new LinkedHashMap<String, Object>();
				Map<String, Object> fieldValueList = new LinkedHashMap<String, Object>();
				if (UtilValidate.isNotEmpty(group)) {
					conditions = new ArrayList<EntityCondition>();
					conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, group.getString("groupId")));
					if (UtilValidate.isNotEmpty(groupType)) {
						conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
					}
					mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					List<GenericValue> fieldList = EntityQuery.use(delegator).from("CustomField").where(mainConditon).queryList();
					groupFieldList.put(group.getString("groupId"), fieldList);
					
					for (GenericValue field : fieldList) {
						String fieldKey = group.getString("groupId")+"-"+field.getString("customFieldId");
						String fieldValue = org.groupfio.common.portal.util.UtilAttribute.getAttrFieldValue(delegator, UtilMisc.toMap("customFieldId", field.getString("customFieldId"), "domainEntityType", domainEntityType, "domainEntityId", domainEntityId, "attrEntityAssoc", attrEntityAssoc));
						fieldValueList.put(fieldKey, fieldValue);
					}
				}
				responseContext.put("groupFieldList", groupFieldList);
				responseContext.put("fieldValueList", fieldValueList);
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	result.put("responseContext", responseContext);
    	result.putAll(ServiceUtil.returnSuccess("Successfully retrived attribute group data.."));
    	return result;
    }
    
    public static Map updateAttributeGroupData(DispatchContext dctx, Map context) {
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupId = (String) context.get("groupId");
    	String roleType = (String) context.get("roleType");
    	String domainEntityType = (String) context.get("domainEntityType");
    	String domainEntityId = (String) context.get("domainEntityId");
    	String groupType = "CUSTOM_FIELD";
    	Map<String, Object> groupData = (Map<String, Object>) context.get("groupData");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> responseContext = new HashMap<String, Object>();
    	
    	try {
    		responseContext.put("domainEntityType", domainEntityType);
    		responseContext.put("domainEntityId", domainEntityId);
    		
    		GenericValue attrEntityAssoc = EntityUtil.getFirst( delegator.findByAnd("AttrEntityAssoc", UtilMisc.toMap("domainEntityType", domainEntityType), null, true) );
    		
    		if (UtilValidate.isEmpty(roleType)) {
    			roleType = domainEntityType;
    		}
    		
    		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleType));
			conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
			conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, "Y")
                    ));
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("hide", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("hide", EntityOperator.EQUALS, "N")
                    ));
			
			EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> fieldRoleConfigList = EntityQuery.use(delegator).from("CustomFieldRoleConfigSummary").where(mainConditon).queryList();
			List<String> groupIdLst = EntityUtil.getFieldListFromEntityList(fieldRoleConfigList, "groupId", true);
			
			if (UtilValidate.isNotEmpty(groupIdLst)) {
				conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.IN, groupIdLst));
				conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "CUSTOM_FIELD"));
				mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue group = EntityQuery.use(delegator).from("CustomFieldGroup").where(mainConditon).queryFirst();
				
				if (UtilValidate.isNotEmpty(groupData)) {
					if (UtilValidate.isNotEmpty(groupData.get("grouping_code"))) {
						conditions = new ArrayList<EntityCondition>();
						conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
								EntityCondition.makeCondition("customFieldGroupingCodeId", EntityOperator.EQUALS, groupData.get("grouping_code")),
								EntityCondition.makeCondition("groupingCode", EntityOperator.EQUALS, groupData.get("grouping_code")),
								EntityCondition.makeCondition("description", EntityOperator.EQUALS, groupData.get("grouping_code"))
								));
						if (UtilValidate.isNotEmpty(groupType)) {
							conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
						}
			        	mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			        	GenericValue code = EntityQuery.use(delegator).from("CustomFieldGroupingCode").where(mainConditon).queryFirst();
			        	if (UtilValidate.isNotEmpty(code)) {
			        		group.put("groupingCode", code.get("customFieldGroupingCodeId"));
			        	}
					}
					if (UtilValidate.isNotEmpty(groupData.get("sequence"))) {
						group.put("sequence", Long.parseLong(groupData.get("sequence").toString()));
					}
					if (UtilValidate.isNotEmpty(groupData.get("group_name"))) {
						group.put("groupName", groupData.get("group_name"));
					}
					
					group.store();
					responseContext.put("group", group);
					
					conditions = new ArrayList<EntityCondition>();
					conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, group.getString("groupId")));
					if (UtilValidate.isNotEmpty(groupType)) {
						conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
					}
					mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					List<GenericValue> fieldList = EntityQuery.use(delegator).from("CustomField").where(mainConditon).queryList();
					
					if (UtilValidate.isNotEmpty(groupData.get("group_name"))) {
						for (GenericValue field : fieldList) {
							field.put("groupName", groupData.get("group_name"));
							field.store();
						}
					}
					
					List<Map<String, Object>> flList = (List<Map<String, Object>>) groupData.get("field_list");
					if (UtilValidate.isNotEmpty(flList)) {
						for (Map<String, Object> fl : flList) {
							GenericValue field = fieldList.stream().filter(x-> x.getString("customFieldId").equals(fl.get("field_id"))).findFirst().get();
							if (UtilValidate.isNotEmpty(field)) {
								if (UtilValidate.isNotEmpty(fl.get("field_name"))) {
									field.put("customFieldName", fl.get("field_name"));
								}
								if (UtilValidate.isNotEmpty(groupData.get("sequence"))) {
									field.put("sequenceNumber", Long.parseLong(fl.get("sequence").toString()));
								}
								if (UtilValidate.isNotEmpty(fl.get("param_data"))) {
									field.put("paramData", fl.get("param_data"));
								}
								
								if (UtilValidate.isNotEmpty(fl.get("field_value"))) {
									Map<String, Object> attrContext = UtilMisc.toMap("attrEntityAssoc", attrEntityAssoc, 
											"customFieldId", field.getString("customFieldId"), "domainEntityId", domainEntityId, "domainEntityType", domainEntityType, "value", fl.get("field_value"));
									org.groupfio.common.portal.util.UtilAttribute.storeAttribute(delegator, attrContext);
								}
								
								field.store();
							}
						}
					}
				}
				
				if (UtilValidate.isNotEmpty(group.getString("groupingCode"))) {
					conditions = new ArrayList<EntityCondition>();
					conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
							EntityCondition.makeCondition("customFieldGroupingCodeId", EntityOperator.EQUALS, group.getString("groupingCode")),
							EntityCondition.makeCondition("groupingCode", EntityOperator.EQUALS, group.getString("groupingCode")),
							EntityCondition.makeCondition("description", EntityOperator.EQUALS, group.getString("groupingCode"))
							));
					if (UtilValidate.isNotEmpty(groupType)) {
						conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
					}
		        	mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		        	GenericValue code = EntityQuery.use(delegator).from("CustomFieldGroupingCode").where(mainConditon).queryFirst();
					responseContext.put("code", code);
				}
				
				Map<String, Object> groupFieldList = new LinkedHashMap<String, Object>();
				Map<String, Object> fieldValueList = new LinkedHashMap<String, Object>();
				if (UtilValidate.isNotEmpty(group)) {
					conditions = new ArrayList<EntityCondition>();
					conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, group.getString("groupId")));
					if (UtilValidate.isNotEmpty(groupType)) {
						conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
					}
					mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					List<GenericValue> fieldList = EntityQuery.use(delegator).from("CustomField").where(mainConditon).queryList();
					groupFieldList.put(group.getString("groupId"), fieldList);
					
					for (GenericValue field : fieldList) {
						String fieldKey = group.getString("groupId")+"-"+field.getString("customFieldId");
						String fieldValue = org.groupfio.common.portal.util.UtilAttribute.getAttrFieldValue(delegator, UtilMisc.toMap("customFieldId", field.getString("customFieldId"), "domainEntityType", domainEntityType, "domainEntityId", domainEntityId, "attrEntityAssoc", attrEntityAssoc));
						fieldValueList.put(fieldKey, fieldValue);
					}
				}
				responseContext.put("groupFieldList", groupFieldList);
				responseContext.put("fieldValueList", fieldValueList);
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	result.put("responseContext", responseContext);
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated attribute group data.."));
    	return result;
    }
    
}
