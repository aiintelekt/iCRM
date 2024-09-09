/**
 * 
 */
package org.groupfio.common.portal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.common.portal.CommonPortalConstants;
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

/**
 * @author Sharif
 *
 */
public class UtilAttribute {
	
	private static final String MODULE = UtilAttribute.class.getName();

	public static String getAttrFieldValue(Delegator delegator, Map<String, Object> context) {
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get("customFieldId"))) {
				String domainEntityType = (String) context.get("domainEntityType");
		        String domainEntityId = (String) context.get("domainEntityId");
		        
		        String customFieldId = (String) context.get("customFieldId");
		        String partyId = (String) context.get("partyId");
		        
		        GenericValue attrEntityAssoc = null;
		        if (UtilValidate.isNotEmpty(context.get("attrEntityAssoc"))) {
		        	attrEntityAssoc = (GenericValue) context.get("attrEntityAssoc");
		        }
				
		        GenericValue fieldValue = null;
		        if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
		        	fieldValue = EntityQuery.use(delegator).select("fieldValue").from("CustomFieldValue").where("customFieldId", customFieldId, "partyId", partyId).queryFirst();
		        	return UtilValidate.isNotEmpty(fieldValue) ? fieldValue.getString("fieldValue") : null;
		        } else if (UtilValidate.isNotEmpty(attrEntityAssoc)) {
		        	fieldValue = EntityQuery.use(delegator).select(attrEntityAssoc.getString("fieldValueColumn")).from(attrEntityAssoc.getString("entityName")).where(attrEntityAssoc.getString("customFieldIdColumn"), customFieldId, attrEntityAssoc.getString("domainEntityIdColumn"), domainEntityId).queryFirst();
		        	return UtilValidate.isNotEmpty(fieldValue) ? fieldValue.getString(attrEntityAssoc.getString("fieldValueColumn")) : null;
		        } else {
		        	fieldValue = EntityQuery.use(delegator).select("fieldValue").from("AttributeValueAssoc").where("customFieldId", customFieldId, "domainEntityId", domainEntityId, "domainEntityType", domainEntityType).queryFirst();
		        	return UtilValidate.isNotEmpty(fieldValue) ? fieldValue.getString("fieldValue") : null;
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void storeAttribute(Delegator delegator, Map<String, Object> context) {
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get("customFieldId"))) {
				String domainEntityType = (String) context.get("domainEntityType");
		        String domainEntityId = (String) context.get("domainEntityId");
		        
		        String customFieldId = (String) context.get("customFieldId");
		        String partyId = (String) context.get("partyId");
		        String value = (String) context.get("value");
		        
		        GenericValue attrEntityAssoc = null;
		        if (UtilValidate.isNotEmpty(context.get("attrEntityAssoc"))) {
		        	attrEntityAssoc = (GenericValue) context.get("attrEntityAssoc");
		        }
				
		        GenericValue fieldValue = null;
		        if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
		        	fieldValue = delegator.makeValue("CustomFieldValue", UtilMisc.toMap("customFieldId", customFieldId, "partyId", partyId));
		        	fieldValue.put("fieldValue", value);
		        } else if (UtilValidate.isNotEmpty(attrEntityAssoc)) {
		        	fieldValue = delegator.makeValue(attrEntityAssoc.getString("entityName"), UtilMisc.toMap(attrEntityAssoc.getString("customFieldIdColumn"), customFieldId, attrEntityAssoc.getString("domainEntityIdColumn"), domainEntityId));
		        	fieldValue.put(attrEntityAssoc.getString("fieldValueColumn"), value);
		        } else {
		        	fieldValue = delegator.makeValue("AttributeValueAssoc", UtilMisc.toMap("customFieldId", customFieldId, "domainEntityId", domainEntityId, "domainEntityType", domainEntityType));
		        	fieldValue.put("fieldValue", value);
		        }
		        
		        delegator.createOrStore(fieldValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
	}
	
	public static String getAgreementAttrValue(Delegator delegator, String agreementId, String attrName) {
		try {
			if (UtilValidate.isNotEmpty(agreementId)) {
				GenericValue entity = EntityQuery.use(delegator).from("AgreementAttribute").where("agreementId", agreementId, "attrName", attrName).cache(false).queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
					return entity.getString("attrValue");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getAgreementItemAttrValue(Delegator delegator, String agreementId, String agreementItemSeqId, String attrName) {
		try {
			if (UtilValidate.isNotEmpty(agreementId) && UtilValidate.isNotEmpty(agreementItemSeqId)) {
				GenericValue entity = EntityQuery.use(delegator).from("AgreementItemAttribute").where("agreementId", agreementId, "agreementItemSeqId", agreementItemSeqId,"attrName", attrName).cache(false).queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
					return entity.getString("attrValue");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getTemplateAttrValue(Delegator delegator, String templateId, String attrName) {
		try {
			if (UtilValidate.isNotEmpty(templateId)) {
				GenericValue entity = EntityQuery.use(delegator).from("TemplateAttribute").where("templateId", templateId, "attrName", attrName).cache(false).queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
					return entity.getString("attrValue");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static void storeTemplateAttrValue(Delegator delegator, String templateId, String attrName, String attrValue) {
		try {
			if (UtilValidate.isNotEmpty(templateId) && UtilValidate.isNotEmpty(attrName)) {
				GenericValue attr = delegator.makeValue("TemplateAttribute");
				attr.put("templateId", templateId);
				attr.put("attrName", attrName);
				attr.put("attrValue", attrValue);
				delegator.createOrStore(attr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
	}
	
	public static void storeServiceAttrValue(Delegator delegator, String custRequestId, String attrName, String attrValue) {
		try {
			if (UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(attrName)) {
				GenericValue attr = delegator.makeValue("CustRequestAttribute");
				attr.put("custRequestId", custRequestId);
				attr.put("attrName", attrName);
				attr.put("attrValue", attrValue);
				delegator.createOrStore(attr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
	}
	
	public static String getServiceAttrValue(Delegator delegator, String custRequestId, String attrName) {
		try {
			if (UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(attrName)) {
				GenericValue entity = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId, "attrName", attrName).cache(false).queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
					return entity.getString("attrValue");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getGroupingCodeDescByIds(Delegator delegator, String groupCodeIds) {
		String descriptions = "";
		try {
			if (UtilValidate.isNotEmpty(groupCodeIds)) {
				for (String groupCodeId : groupCodeIds.split(",")) {
					GenericValue entity = EntityQuery.use(delegator).select("description").from("CustomFieldGroupingCode").where("customFieldGroupingCodeId", groupCodeId).queryFirst();
					if (UtilValidate.isNotEmpty(entity)) {
						descriptions += entity.getString("description") + ", ";
					}
				}
				if (UtilValidate.isNotEmpty(descriptions)) {
					descriptions = descriptions.substring(0, descriptions.length()-2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return descriptions;
	}
	
	public static String getGroupingCodeByIds(Delegator delegator, String groupCodeIds) {
		String descriptions = "";
		try {
			if (UtilValidate.isNotEmpty(groupCodeIds)) {
				for (String groupCodeId : groupCodeIds.split(",")) {
					GenericValue entity = EntityQuery.use(delegator).select("groupingCode").from("CustomFieldGroupingCode").where("customFieldGroupingCodeId", groupCodeId).queryFirst();
					if (UtilValidate.isNotEmpty(entity)) {
						descriptions += entity.getString("groupingCode") + ", ";
					}
				}
				if (UtilValidate.isNotEmpty(descriptions)) {
					descriptions = descriptions.substring(0, descriptions.length()-2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return descriptions;
	}
	
	public static Map<String, Object> getCustomFieldGroupDetail (Delegator delegator, Map<String, Object> filter) {
		Map<String, Object> detail = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(filter)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				if (UtilValidate.isNotEmpty(filter.get("groupId"))) {
					conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, filter.get("groupId")));
				}
				if (UtilValidate.isNotEmpty(filter.get("groupType"))) {
					conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, filter.get("groupType")));
				}
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue group = EntityQuery.use(delegator).from("CustomFieldGroup").where(mainConditon).queryFirst();
				if (UtilValidate.isNotEmpty(group)) {
					detail.put("groupId", group.getString("groupId"));
					detail.put("groupName", group.getString("groupName"));
					
					List<Map<String, Object>> fields = new ArrayList<>();
					
					conditions = new ArrayList<EntityCondition>();
					conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, group.getString("groupId")));
					conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
							EntityCondition.makeCondition("hide", EntityOperator.EQUALS, "N"),
							EntityCondition.makeCondition("hide", EntityOperator.EQUALS, null)
							));
					mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					List<GenericValue> fieldList = EntityQuery.use(delegator).from("CustomField").where(mainConditon).queryList();
					if (UtilValidate.isNotEmpty(fieldList)) {
						for (GenericValue field : fieldList) {
							Map<String, Object> fieldData = new LinkedHashMap<>();
							fieldData.put("customFieldId", field.getString("customFieldId"));
							fieldData.put("customFieldName", field.getString("customFieldName"));
							
							Map<String, Object> checkedDetail = new LinkedHashMap<>();
							if (UtilValidate.isNotEmpty(filter.get("chkConfigFieldDetail"))) {
								List<Map<String, Object>> chkConfigFieldDetails = (List<Map<String, Object>>) filter.get("chkConfigFieldDetail");
								for (Map chkConfigFieldDetail : chkConfigFieldDetails) {
									String isChecked = "Y";
									String activityDate = (String) chkConfigFieldDetail.get("activityDate");
									List<Map<String, Object>> chkConfigFields = (List<Map<String, Object>>) chkConfigFieldDetail.get("fields");
									long count = chkConfigFields.stream().filter(x->x.get("customFieldId").equals(field.getString("customFieldId"))).count();
									if (count == 0) {
										isChecked = "N";
									}
									checkedDetail.put(activityDate, isChecked);
								}
							}
							fieldData.put("checkedDetail", checkedDetail);
							
							fields.add(fieldData);
						}
					}
					detail.put("fields", fields);
				}
			}
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		return detail;
	}
	
	public static Map<String, Object> getConfiguredFields (Delegator delegator, Map<String, Object> filter) {
		Map<String, Object> result = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(filter)) {
				List<Map<String, Object>> groupList = (List<Map<String, Object>>) filter.get("groupList");
				String activityDate = (String) filter.get("activityDate");
				String srNumber = (String) filter.get("srNumber");
				
				String programConfiguration = org.groupfio.common.portal.util.SrUtil.getCustRequestAttrValue(delegator, "PROG_CONFIG", srNumber);
				
				if (UtilValidate.isNotEmpty(groupList) && UtilValidate.isNotEmpty(activityDate) && UtilValidate.isNotEmpty(programConfiguration)) {
					activityDate = activityDate.contains(" ") ? activityDate.split(" ")[0] : activityDate;
					List<Map> chkConfigList = org.fio.homeapps.util.ParamUtil.jsonToList(programConfiguration);
					for (Map group : groupList) {
						String groupId = (String) group.get("groupId");
						
						Map<String, Object> chkConfig = chkConfigList.stream().filter(x->x.get("groupId").equals(groupId)).findFirst().orElse(null);
						if (UtilValidate.isNotEmpty(chkConfig)) {
							Map<String, Object> groupData = org.groupfio.common.portal.util.UtilAttribute.getCustomFieldGroupDetail(delegator, UtilMisc.toMap("groupId", groupId, "chkConfigFieldDetail", chkConfig.get("fieldDetail")));
							if (UtilValidate.isNotEmpty(groupData.get("fields"))) {
								List<Map> fields = (List<Map>) groupData.get("fields");
								for (Map field : fields) {
									String customFieldId = (String) field.get("customFieldId");
									String customFieldName = (String) field.get("customFieldName");
									Map<String, Object> checkedDetail = (Map) field.get("checkedDetail");
									if (checkedDetail.containsKey(activityDate) 
											&& UtilValidate.isNotEmpty(checkedDetail.get(activityDate)) && checkedDetail.get(activityDate).equals("Y")) {
										result.put(customFieldId, customFieldName);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		return result;
	}
	public static Map<String, Object> getConfiguredGroups (Delegator delegator, Map<String, Object> filter) {
		Map<String, Object> result = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(filter)) {
				List<Map<String, Object>> groupList = (List<Map<String, Object>>) filter.get("groupList");
				String srNumber = (String) filter.get("srNumber");
				
				String programConfiguration = org.groupfio.common.portal.util.SrUtil.getCustRequestAttrValue(delegator, "PROG_CONFIG", srNumber);
				
				if (UtilValidate.isNotEmpty(groupList) && UtilValidate.isNotEmpty(programConfiguration)) {
					List<Map> chkConfigList = org.fio.homeapps.util.ParamUtil.jsonToList(programConfiguration);
					for (Map group : groupList) {
						String groupId = (String) group.get("groupId");
						
						Map<String, Object> chkConfig = chkConfigList.stream().filter(x->x.get("groupId").equals(groupId)).findFirst().orElse(null);
						if (UtilValidate.isNotEmpty(chkConfig)) {
							result.put(groupId, group.get("groupName"));
						}
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		return result;
	}
	
	public static boolean removeGroupingCodeAssociation (Delegator delegator, String groupingCodeId) {
		try {
			if (UtilValidate.isNotEmpty(groupingCodeId)) {
				List<GenericValue> tobeStore = new ArrayList<>();
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    			conditions.add(EntityCondition.makeCondition("groupingCode", EntityOperator.LIKE, "%"+groupingCodeId+"%"));
    			conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "CUSTOM_FIELD"));
            	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
            	List<GenericValue> groupList = EntityQuery.use(delegator).from("CustomFieldGroup").where(mainConditon).queryList();
            	if (UtilValidate.isNotEmpty(groupList)) {
            		for (GenericValue group : groupList) {
            			List<String> gcList = Arrays.asList(group.getString("groupingCode").split(","));
            			List<String> newGCList = new ArrayList<>();
            			for (String gc : gcList) {
            				if (UtilValidate.isNotEmpty(gc) && !gc.equals(groupingCodeId)) {
            					newGCList.add(gc);
            				}
            			}
            			group.set("groupingCode", StringUtil.join(newGCList, ","));
            			tobeStore.add(group);
            		}
            		delegator.storeAll(tobeStore);
            	}
			}
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		return false;
	}
	
	public static void storeContentAttrValue(Delegator delegator, String contentId, String attrName, String attrValue) {
		try {
			if (UtilValidate.isNotEmpty(contentId) && UtilValidate.isNotEmpty(attrName)) {
				GenericValue attr = delegator.makeValue("ContentAttribute");
				attr.put("contentId", contentId);
				attr.put("attrName", attrName);
				attr.put("attrValue", attrValue);
				delegator.createOrStore(attr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
	}
	public static String getContentAttrValue(Delegator delegator, String contentId, String attrName) {
		try {
			if (UtilValidate.isNotEmpty(contentId)) {
				GenericValue entity = EntityQuery.use(delegator).from("ContentAttribute").where("contentId", contentId, "attrName", attrName).cache(false).queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
					return entity.getString("attrValue");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static void storeAttrValue(Delegator delegator, String entityName, String primaryColumnName, String primaryColumnValue, String attrName, String attrValue) {
		try {
			if (UtilValidate.isNotEmpty(entityName) && UtilValidate.isNotEmpty(primaryColumnName) && UtilValidate.isNotEmpty(primaryColumnValue) && UtilValidate.isNotEmpty(attrName)) {
				GenericValue attr = delegator.makeValue(entityName);
				attr.put(primaryColumnName, primaryColumnValue);
				attr.put("attrName", attrName);
				attr.put("attrValue", attrValue);
				delegator.createOrStore(attr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
	}
	
	public static String getAttrValue(Delegator delegator, String entityName, String primaryColumnName, String primaryColumnValue, String attrName) {
		return getAttrValue(delegator, entityName, primaryColumnName, primaryColumnValue, attrName, false);
	}
	public static String getAttrValue(Delegator delegator, String entityName, String primaryColumnName, String primaryColumnValue, String attrName, boolean useCache) {
		try {
			if (UtilValidate.isNotEmpty(entityName) && UtilValidate.isNotEmpty(primaryColumnName) && UtilValidate.isNotEmpty(primaryColumnValue) && UtilValidate.isNotEmpty(attrName)) {
				GenericValue entity = EntityQuery.use(delegator).from(entityName).where(primaryColumnName, primaryColumnValue, "attrName", attrName).cache(useCache).queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
					return entity.getString("attrValue");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static GenericValue createOrStorePartyAttribute(Delegator delegator,String partyId, String attrName, String attrValue) {
		try {
			GenericValue partyAttribute = delegator.makeValue("PartyAttribute");
			partyAttribute.put("partyId", partyId);
			partyAttribute.put("attrName", attrName);
			partyAttribute.put("attrValue", attrValue);
			return delegator.createOrStore(partyAttribute);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getPartyAttribute(Delegator delegator, String partyId, String attrName) {
		String attrValue = "";
		try {
			GenericValue partyAttribute = EntityQuery.use(delegator).from("PartyAttribute").where("partyId", partyId, "attrName", attrName).queryFirst();
			if(UtilValidate.isNotEmpty(partyAttribute))
				attrValue = partyAttribute.getString("attrValue");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attrValue;
	}
	
	public static String getChannelAttribute(Delegator delegator, String channelAccessId, String attrName) {
		String attrValue = "";
		try {
			GenericValue attr = EntityUtil.getFirst( delegator.findByAnd("ChannelAccessAttribute", UtilMisc.toMap("channelAccessId", channelAccessId, "attrName", attrName), null, false) );
			if(UtilValidate.isNotEmpty(attr)) {
				attrValue = attr.getString("attrValue");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attrValue;
	}
	
}
