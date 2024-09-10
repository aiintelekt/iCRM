/**
 * 
 */
package org.groupfio.customfield.service.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.customfield.service.CustomfieldServiceConstants.GroupType;
import org.groupfio.customfield.service.CustomfieldServiceConstants.SourceInvoked;
import org.ofbiz.base.util.Debug;
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
public class DataUtil {
	
	private static String MODULE = DataUtil.class.getName();

	public static void prepareAppStatusData(Map<String, Object> data) {
		if (UtilValidate.isEmpty(data.get("sourceInvoked"))) {
			data.put("sourceInvoked", SourceInvoked.UNKNOWN);
		}
	}
	
	public static String getStatusId (Delegator delegator, String statusCode) {
		try {
			GenericValue statusItem = EntityUtil.getFirst( delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "CUSTOM_FIELD_STATUS", "statusCode", statusCode), null, false) );
			if (UtilValidate.isNotEmpty(statusItem)) {
				return statusItem.getString("statusId");
			}
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		return null;
	}
	
	public static String getFormatedValue (String value) {
		
		if (UtilValidate.isNotEmpty(value)) {
			value = value.toLowerCase().trim();
			value = value.substring(0, 1).toUpperCase() + value.substring(1);
			
			System.out.println(value);
		}
		return value;
	}
	
	public static String getSegmentationValueAssociatedEntityName (Delegator delegator, String groupId) {
		
		String entityName = "CustomFieldPartyClassification";
		
		try {
			if (UtilValidate.isNotEmpty(groupId)) {
				
				GenericValue customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false) );
				if (UtilValidate.isNotEmpty(customFieldGroup) && (UtilValidate.isNotEmpty(customFieldGroup.getString("isUseDynamicEntity")) && customFieldGroup.getString("isUseDynamicEntity").equals("Y"))) {
					groupId = DataUtil.getFormatedValue(groupId);
					entityName = "CustomFieldSeg" + groupId;
				}
				
				if (UtilValidate.isNotEmpty(customFieldGroup) && customFieldGroup.getString("groupType").equals(GroupType.ECONOMIC_METRIC)) {
					entityName = "PartyMetricIndicator";
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return entityName;
	}
	
	public static String getSegmentationValueAssociatedTrkEntityName (Delegator delegator, String groupId) {
		
		String entityName = "CustomFieldPartyClassificationTrk";
		
		try {
			if (UtilValidate.isNotEmpty(groupId)) {
				
				GenericValue customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false) );
				if (UtilValidate.isNotEmpty(customFieldGroup) && (UtilValidate.isNotEmpty(customFieldGroup.getString("isUseDynamicEntity")) && customFieldGroup.getString("isUseDynamicEntity").equals("Y"))) {
					groupId = DataUtil.getFormatedValue(groupId);
					entityName = "CustomFieldSegTrk" + groupId;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return entityName;
	}
	
	public static GenericValue getCampaign (Delegator delegator, String groupId) {
		try {
			if (UtilValidate.isNotEmpty(groupId)) {
				String[] arrOfStr = groupId.split("_", 2);
				if (UtilValidate.isNotEmpty(arrOfStr) && arrOfStr.length > 1) {
					String marketingCampaignId = arrOfStr[1];
					GenericValue campaign = EntityUtil.getFirst( delegator.findByAnd("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", marketingCampaignId), null, false) );
					return campaign;
				}
			}
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		return null;
	}
	
	public static GenericValue getAttrFieldValue(Delegator delegator, Map<String, Object> context) {
		try {
			if (UtilValidate.isNotEmpty(context)) {
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
		        	fieldValue = EntityUtil.getFirst( delegator.findByAnd("CustomFieldValue", UtilMisc.toMap("customFieldId", customFieldId, "partyId", partyId), null, false) );
		        } else if (UtilValidate.isNotEmpty(attrEntityAssoc)) {
		        	fieldValue = EntityUtil.getFirst( delegator.findByAnd(attrEntityAssoc.getString("entityName"), UtilMisc.toMap(attrEntityAssoc.getString("customFieldIdColumn"), customFieldId, attrEntityAssoc.getString("domainEntityIdColumn"), domainEntityId), null, false) );
		        } else {
		        	fieldValue = EntityUtil.getFirst( delegator.findByAnd("AttributeValueAssoc", UtilMisc.toMap("customFieldId", customFieldId, "domainEntityId", domainEntityId, "domainEntityType", domainEntityType), null, false) );
		        }
		        return fieldValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static GenericValue getCustomFieldGroup (Delegator delegator, Map<String, Object> filter) {
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
				GenericValue entity = EntityQuery.use(delegator).from("CustomFieldGroup").where(mainConditon).queryFirst();
				return entity;
			}
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		return null;
	}
	
	public static GenericValue getCustomField (Delegator delegator, Map<String, Object> filter) {
		try {
			if (UtilValidate.isNotEmpty(filter)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				if (UtilValidate.isNotEmpty(filter.get("groupId"))) {
					conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, filter.get("groupId")));
				}
				if (UtilValidate.isNotEmpty(filter.get("customFieldId"))) {
					conditions.add(EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, filter.get("customFieldId")));
				}
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue entity = EntityQuery.use(delegator).from("CustomField").where(mainConditon).queryFirst();
				return entity;
			}
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		return null;
	}
	
	public static GenericValue getCustomFieldGroupingCode(Delegator delegator, Map<String, Object> filter) {
		try {
			if (UtilValidate.isNotEmpty(filter) && UtilValidate.isNotEmpty(filter.get("groupingCode"))) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
						EntityCondition.makeCondition("customFieldGroupingCodeId", EntityOperator.EQUALS, filter.get("groupingCode")),
						EntityCondition.makeCondition("groupingCode", EntityOperator.EQUALS, filter.get("groupingCode")),
						EntityCondition.makeCondition("description", EntityOperator.EQUALS, filter.get("groupingCode"))
						));
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue entity = EntityQuery.use(delegator).from("CustomFieldGroupingCode").where(mainConditon).queryFirst();
				return entity;
			}
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		return null;
	}
	
}
