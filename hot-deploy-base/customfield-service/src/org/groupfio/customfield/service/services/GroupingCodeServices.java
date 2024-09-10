/**
 * 
 */
package org.groupfio.customfield.service.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
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
public class GroupingCodeServices {

	private static final String MODULE = GroupingCodeServices.class.getName();
    
    public static Map createGroupingCode(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupingCode = (String) context.get("groupingCode");
    	String description = (String) context.get("description");
    	String sequenceNumber = (String) context.get("sequenceNumber");
    	String groupType = (String) context.get("groupType");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue code = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroupingCode", UtilMisc.toMap("groupingCode", groupingCode), null, false) );
    		
    		if (UtilValidate.isNotEmpty(code)) {
    			result.putAll(ServiceUtil.returnError("Grouping Code already exists!"));
    			result.put("groupType", groupType);
    			return result;
    		}
    		
    		code = delegator.makeValue("CustomFieldGroupingCode");
    		
    		String customFieldGroupingCodeId = delegator.getNextSeqId("CustomFieldGroupingCode");
    		
    		code.put("customFieldGroupingCodeId", customFieldGroupingCodeId);
    		code.put("groupingCode", groupingCode);
    		code.put("description", description);
    		
    		code.put("groupType", groupType);
    		
    		code.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1000));
    		
    		code.create();
    		
    		result.put("groupingCodeId", customFieldGroupingCodeId);
    		result.put("groupType", groupType);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created Grouping Code.."));
    	
    	return result;
    	
    }
    
    public static Map updateGroupingCode(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupingCodeId = (String) context.get("groupingCodeId");
    	
    	String groupingCode = (String) context.get("groupingCode");
    	String description = (String) context.get("description");
    	String sequenceNumber = (String) context.get("sequenceNumber");
    	String groupType = (String) context.get("groupType");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupingCodeId", groupingCodeId);
    	result.put("groupType", groupType);
    	
    	try {
        	
    		GenericValue code = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroupingCode",UtilMisc.toMap("customFieldGroupingCodeId", groupingCodeId), null, false) );
    		
    		if (UtilValidate.isEmpty(code)) {
    			result.putAll(ServiceUtil.returnError("Grouping Code not exists!"));
    			return result;
    		}
    		
    		code.put("groupingCode", groupingCode);
    		code.put("description", description);
    		
    		code.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1000));
    		
    		code.store();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated Grouping Code.."));
    	
    	return result;
    	
    }
    
    public static Map deleteGroupingCode(DispatchContext dctx, Map context) {
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupingCodeId = (String) context.get("groupingCodeId");
    	String groupType = (String) context.get("groupType");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue code = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroupingCode",UtilMisc.toMap("customFieldGroupingCodeId", groupingCodeId), null, false) );
    		
    		if (UtilValidate.isEmpty(code)) {
    			result.putAll(ServiceUtil.returnError("Grouping Code not exists!"));
    			return result;
    		}
    		
    		if (UtilValidate.isEmpty(groupType)) {
    			groupType = code.getString("groupType");
    		}
    		result.put("groupType", groupType);
    		
    		List<GenericValue> segmentCodeList = delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupingCode", code.getString("groupingCode")), null, false);
    		if (UtilValidate.isNotEmpty(segmentCodeList)) {
    			for (GenericValue segmentCode : segmentCodeList) {
        			segmentCode.put("groupingCode", null);
        		}
    			delegator.storeAll(segmentCodeList);
    		}
    		
    		code.remove();
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	result.putAll(ServiceUtil.returnSuccess("Successfully deleted Grouping Code.."));
    	return result;
    }
    
    public static Map copyGroupingCode(DispatchContext dctx, Map context) {
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String parentGroupingCodeId = (String) context.get("parentGroupingCodeId");
    	String groupingCode = (String) context.get("groupingCode");
    	String description = (String) context.get("description");
    	String sequenceNumber = (String) context.get("sequenceNumber");
    	
    	String groupType = (String) context.get("groupType");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("groupingCodeId", parentGroupingCodeId);
    	
    	try {
    		GenericValue code = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroupingCode",UtilMisc.toMap("customFieldGroupingCodeId", parentGroupingCodeId), null, false) );
    		if (UtilValidate.isEmpty(code)) {
    			result.putAll(ServiceUtil.returnError("Parent Grouping Code not exists!"));
    			return result;
    		}
    		
    		List<GenericValue> tobeStore = new ArrayList<>();
    		String groupingCodeId = delegator.getNextSeqId("CustomFieldGroupingCode");
    		GenericValue gc = delegator.makeValue("CustomFieldGroupingCode");
    		gc.put("customFieldGroupingCodeId", groupingCodeId);
    		gc.put("groupingCode", groupingCode);
    		gc.put("description", description);
    		gc.put("groupType", groupType);
    		gc.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1000));
    		tobeStore.add(gc);
    		
    		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("groupingCode", EntityOperator.LIKE, "%"+parentGroupingCodeId+"%"));
			conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
        	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
        	List<GenericValue> groupList = EntityQuery.use(delegator).from("CustomFieldGroup").where(mainConditon).queryList();
        	if (UtilValidate.isNotEmpty(groupList)) {
        		for (GenericValue group : groupList) {
        			GenericValue gr = delegator.makeValue("CustomFieldGroup");
        			String groupId = delegator.getNextSeqId("CustomFieldGroup");
        			gr.putAll(group.getAllFields());
        			gr.put("groupId", groupId);
        			gr.put("groupingCode", groupingCodeId);
        			tobeStore.add(gr);
        			
        			List<GenericValue> roleConfigList = EntityQuery.use(delegator).from("CustomFieldRoleConfig").where("groupId", group.getString("groupId")).queryList();
        			if (UtilValidate.isNotEmpty(roleConfigList)) {
        				for (GenericValue roleConfig : roleConfigList) {
        					GenericValue rc = delegator.makeValue("CustomFieldRoleConfig");
        					String customFieldRoleConfigId = delegator.getNextSeqId("CustomFieldRoleConfig");
        					rc.putAll(roleConfig.getAllFields());
        					rc.put("groupId", groupId);
        					rc.put("customFieldRoleConfigId", customFieldRoleConfigId);
        					tobeStore.add(rc);
        				}
        			}
        			
        			List<GenericValue> fieldList = EntityQuery.use(delegator).from("CustomField").where("groupId", group.getString("groupId")).queryList();
        			if (UtilValidate.isNotEmpty(fieldList)) {
        				for (GenericValue field : fieldList) {
        					GenericValue fld = delegator.makeValue("CustomField");
        					String customFieldId = delegator.getNextSeqId("CustomField");
        					fld.putAll(field.getAllFields());
        					fld.put("groupId", groupId);
        					fld.put("customFieldId", customFieldId);
        					tobeStore.add(fld);
        				}
        			}
        		}
        	}
        	
        	delegator.storeAll(tobeStore);
    		
    		result.put("groupingCodeId", groupingCodeId);
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	result.putAll(ServiceUtil.returnSuccess("Successfully Copied the Grouping code"));
    	return result;
    }
    
    public static Map getConfigByGroupCode(DispatchContext dctx, Map context) {
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupingCodeId = (String) context.get("groupingCodeId");
    	String groupType = (String) context.get("groupType");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> responseContext = new HashMap<String, Object>();
    	
    	try {
    		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
					EntityCondition.makeCondition("customFieldGroupingCodeId", EntityOperator.EQUALS, groupingCodeId),
					EntityCondition.makeCondition("groupingCode", EntityOperator.EQUALS, groupingCodeId),
					EntityCondition.makeCondition("description", EntityOperator.EQUALS, groupingCodeId)
					));
			if (UtilValidate.isNotEmpty(groupType)) {
				conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
			}
        	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			GenericValue code = EntityQuery.use(delegator).from("CustomFieldGroupingCode").where(mainConditon).queryFirst();
    		if (UtilValidate.isEmpty(code)) {
    			result.putAll(ServiceUtil.returnError("Grouping Code not exists!"));
    			return result;
    		}
    		responseContext.put("code", code);
    		
    		conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("groupingCode", EntityOperator.EQUALS, code.getString("groupingCode")));
			if (UtilValidate.isNotEmpty(groupType)) {
				conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
			}
			mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> groupList = EntityQuery.use(delegator).from("CustomFieldGroup").where(mainConditon).queryList();
			responseContext.put("groupList", groupList);
			
			Map<String, Object> groupFieldList = new LinkedHashMap<String, Object>();
			if (UtilValidate.isNotEmpty(groupList)) {
				for (GenericValue group : groupList) {
					conditions = new ArrayList<EntityCondition>();
					conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, group.getString("groupId")));
					if (UtilValidate.isNotEmpty(groupType)) {
						conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
					}
					mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					List<GenericValue> fieldList = EntityQuery.use(delegator).from("CustomField").where(mainConditon).queryList();
					groupFieldList.put(group.getString("groupId"), fieldList);
				}
			}
			responseContext.put("groupFieldList", groupFieldList);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	result.put("responseContext", responseContext);
    	result.putAll(ServiceUtil.returnSuccess("Successfully retrived configuration by grouping code.."));
    	return result;
    }
    
    public static Map getAttributeGroupCodeData(DispatchContext dctx, Map context) {
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String groupingCodeId = (String) context.get("groupingCodeId");
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
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
					EntityCondition.makeCondition("customFieldGroupingCodeId", EntityOperator.EQUALS, groupingCodeId),
					EntityCondition.makeCondition("groupingCode", EntityOperator.EQUALS, groupingCodeId),
					EntityCondition.makeCondition("description", EntityOperator.EQUALS, groupingCodeId)
					));
			if (UtilValidate.isNotEmpty(groupType)) {
				conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
			}
        	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			GenericValue code = EntityQuery.use(delegator).from("CustomFieldGroupingCode").where(mainConditon).queryFirst();
    		if (UtilValidate.isEmpty(code)) {
    			result.putAll(ServiceUtil.returnError("Grouping Code not exists!"));
    			return result;
    		}
    		responseContext.put("code", code);
    		
    		conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleType));
			conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "CUSTOM_FIELD"));
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
					EntityCondition.makeCondition("customFieldGroupingCodeId", EntityOperator.EQUALS, groupingCodeId),
					EntityCondition.makeCondition("cfgcGroupingCode", EntityOperator.EQUALS, groupingCodeId),
					EntityCondition.makeCondition("cfgcDescription", EntityOperator.EQUALS, groupingCodeId)
					));
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, "Y")
                    ));
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("hide", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("hide", EntityOperator.EQUALS, "N")
                    ));
			
        	mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> fieldRoleConfigList = EntityQuery.use(delegator).from("CustomFieldRoleConfigSummary").where(mainConditon).queryList();
			List<String> groupIdLst = EntityUtil.getFieldListFromEntityList(fieldRoleConfigList, "groupId", true);
			
			if (UtilValidate.isNotEmpty(groupIdLst)) {
				conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.IN, groupIdLst));
				conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "CUSTOM_FIELD"));
				mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> groupList = EntityQuery.use(delegator).from("CustomFieldGroup").where(mainConditon).queryList();
				responseContext.put("groupList", groupList);
				
				Map<String, Object> groupFieldList = new LinkedHashMap<String, Object>();
				Map<String, Object> fieldValueList = new LinkedHashMap<String, Object>();
				if (UtilValidate.isNotEmpty(groupList)) {
					for (GenericValue group : groupList) {
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
    	result.putAll(ServiceUtil.returnSuccess("Successfully retrived attribute grouping code data.."));
    	return result;
    }
    
}
