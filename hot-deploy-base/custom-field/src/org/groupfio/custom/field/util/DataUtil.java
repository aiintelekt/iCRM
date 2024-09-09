/**
 * 
 */
package org.groupfio.custom.field.util;

import java.util.Map;

import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;
import org.groupfio.custom.field.constants.CustomFieldConstants.SourceInvoked;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
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
	
}
