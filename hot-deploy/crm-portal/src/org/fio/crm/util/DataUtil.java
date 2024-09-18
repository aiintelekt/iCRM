/**
 * 
 */
package org.fio.crm.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class DataUtil {
	
	private static String MODULE = DataUtil.class.getName();

	public static List<GenericValue> getLatestVersionHeaderConfigs (Delegator delegator, String hdrFileType) {
		
		List<GenericValue> headerConfigs = new ArrayList<GenericValue>();
		
		try {
			
			List conditionsList = FastList.newInstance();
			
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			
			if (UtilValidate.isNotEmpty(hdrFileType)) {
				conditionsList.add(EntityCondition.makeCondition("hdrFileType", EntityOperator.EQUALS, hdrFileType));
			}
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			
			List<GenericValue> hdrIdList = delegator.findList("HadoopHdrMaster", mainConditons, UtilMisc.toSet("hdrId"), UtilMisc.toList("hdrRmSeqNum"), efo, false);
			
			if (UtilValidate.isNotEmpty(hdrIdList)) {
				String hdrId = hdrIdList.get(0).getString("hdrId");
				
				headerConfigs = delegator.findByAnd("HadoopHdrMaster", UtilMisc.toMap("hdrId", hdrId, "hdrFileType", hdrFileType), null, false);
				
			}
			
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		
		return headerConfigs;
		
	}
	
	public static String getPartyRelAssocId(Delegator delegator, Map<String, Object> context) {
		
		String partyIdFrom = (String) context.get("partyIdFrom");
		String partyIdTo = (String) context.get("partyIdTo");
		String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
		String roleTypeIdTo = (String) context.get("roleTypeIdTo");
		String partyRelationshipTypeId = (String) context.get("partyRelationshipTypeId");
		
		try {
			if (UtilValidate.isNotEmpty(partyIdFrom) && UtilValidate.isNotEmpty(partyIdTo) && UtilValidate.isNotEmpty(roleTypeIdFrom) && UtilValidate.isNotEmpty(roleTypeIdTo) && UtilValidate.isNotEmpty(partyRelationshipTypeId)) {
				EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom),
						EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo),
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
						EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, partyRelationshipTypeId),
						EntityUtil.getFilterByDateExpr());
				
				GenericValue existingRelationship = EntityUtil.getFirst( delegator.findList("PartyRelationship", searchConditions,null, null, null, false) );
				if (UtilValidate.isNotEmpty(existingRelationship)) {
					return existingRelationship.getString("partyRelAssocId");
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return null;
	}
	
	public static Map<String, Object> getDndStatus(Delegator delegator, String telecomNumber) {
		String dndStatus = "N";
		String solicitationStatus = "Y";
		Map<String, Object> rsponseMap = new HashMap<String, Object>();
		try {
		GenericValue dndMaster = EntityQuery.use(delegator).from("DndMaster").where("number", telecomNumber).orderBy("lastUpdatedStamp DESC").queryFirst();
		if(dndMaster != null && dndMaster.size() > 0) {
			String dndIndicator = dndMaster.getString("indicator");
			String dndSeqId = dndMaster.getString("seqId");
			if(UtilValidate.isNotEmpty(dndIndicator)) {
				rsponseMap.put("dndIndicator", dndIndicator);
				rsponseMap.put("dndSeqId", dndSeqId);
				if("A".equalsIgnoreCase(dndIndicator)) {
					solicitationStatus = "N";
					dndStatus = "Y";
				} else if("D".equalsIgnoreCase(dndIndicator)) {
					solicitationStatus = "Y";
					dndStatus = "N";
				}
			} 
		}
		} catch (GenericEntityException ex) {
			Debug.log("Exception in getDndStatus method: " +ex.getMessage());
		}
		rsponseMap.put("dndStatus", dndStatus);
		rsponseMap.put("solicitationStatus", solicitationStatus);
		return rsponseMap;
	}
	
	public static boolean validateDndAuditLogDetails(Delegator delegator, String telecomNumber, String partyId, String dndIndicator) {
		Boolean dndValidation = false;
		try {
		GenericValue dndAuditLogDetails = EntityQuery.use(delegator).from("DndAuditLogDetails")
				.where("partyId", partyId, "dndNumber", telecomNumber, "dndIndicator", dndIndicator)
				.queryFirst();
		if(dndAuditLogDetails == null || dndAuditLogDetails.size() < 1) {
			dndValidation = true;
		}
		} catch (GenericEntityException ex) {
			Debug.log("Exception in validateDndAuditLogDetails method: " +ex.getMessage());
		}
		
		return dndValidation;
	}
	
    @SuppressWarnings("unchecked")
    public static GenericValue makeDndAuditLogDetails(String dndSeqId, String partyId, String changeStatus, String dndNumber, String dndIndicator, Timestamp now, Delegator delegator) {
        Map<String, Object> dndAuditLogDetails = FastMap.newInstance();
        dndAuditLogDetails.put("seqId", delegator.getNextSeqId("DndAuditLogDetails"));
        dndAuditLogDetails.put("partyId", partyId);
        dndAuditLogDetails.put("dndSeqId", dndSeqId);
        dndAuditLogDetails.put("changeStatus", changeStatus);
        dndAuditLogDetails.put("dndNumber", dndNumber);
        dndAuditLogDetails.put("dndIndicator", dndIndicator);
        dndAuditLogDetails.put("changeDate", now);
        return delegator.makeValue("DndAuditLogDetails", dndAuditLogDetails);
    }
    
    public static <T> List<T> getFieldListFromMapList(List<Map<String, Object>> genericValueList, String fieldName, boolean distinct) {
        if (genericValueList == null || fieldName == null) {
            return null;
        }
        List<T> fieldList = new LinkedList<T>();
        Set<T> distinctSet = null;
        if (distinct) {
            distinctSet = new HashSet<T>();
        }

        for (Map<String, Object> value: genericValueList) {
            T fieldValue = UtilGenerics.<T>cast(value.get(fieldName));
            if (fieldValue != null) {
                if (distinct) {
                    if (!distinctSet.contains(fieldValue)) {
                        fieldList.add(fieldValue);
                        distinctSet.add(fieldValue);
                    }
                } else {
                    fieldList.add(fieldValue);
                }
            }
        }

        return fieldList;
    }
    public static GenericValue getActivePartyContactMechPurpose(Delegator delegator, String partyId, String contactMechPurposeTypeId, String partyRelAssocId) {
		GenericValue partyContactMechPurpose = null;
		try {
			if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(contactMechPurposeTypeId)) {
				EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
						EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId),
						EntityUtil.getFilterByDateExpr());
				
				if (UtilValidate.isNotEmpty(partyRelAssocId)) {
					searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("partyRelAssocId", EntityOperator.EQUALS, partyRelAssocId),
							searchConditions);
				}
				
				partyContactMechPurpose = EntityUtil.getFirst( delegator.findList("PartyContactMechPurpose", searchConditions,null, null, null, false) );
				return partyContactMechPurpose;
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return partyContactMechPurpose;
	}
    public static boolean isContactPhoneChange(Delegator delegator, String contactId, String partyRelAssocId, String phoneNumber, String contactMechPurposeTypeId) {
		
		try {
			if (UtilValidate.isNotEmpty(phoneNumber)) {
				GenericValue mobilePurpose = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", contactId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "partyRelAssocId", partyRelAssocId), null, false));
	         	if(UtilValidate.isNotEmpty(mobilePurpose)) {
	         		String contactMechId = mobilePurpose.getString("contactMechId");
	        		
	        		GenericValue phoneContactMech = EntityUtil.getFirst(delegator.findByAnd("TelecomNumber",  UtilMisc.toMap("contactMechId", contactMechId), null, false));
	        		if (UtilValidate.isNotEmpty(phoneContactMech.getString("contactNumber")) && !phoneNumber.equals(phoneContactMech.getString("contactNumber"))) {
	        			return true;
	        		}
	         	}
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return false;
	}
    /**
     * Gets the login role. for DBS_TL or DBS_RM
     *
     * @param delegator the delegator
     * @param loginId the login id
     * @return the login role
     */
	public static List<String> getLoginRole(Delegator delegator, String loginPartyId, String teamId) {
		List<String> roles = new ArrayList<String>();
		Map<String,Object> teamMember = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, teamId, loginPartyId);
		String securityGroup = (String)teamMember.get("securityGroupId");
		roles.add(securityGroup);
		return roles;
	}
	public static boolean isValidContentIdOrDataResourceId(String contentId) {
		boolean isValidContentId = true;
		if(UtilValidate.isEmpty(contentId)) {
			isValidContentId = false;
		}else {
			String regex = "^[A-Za-z]*-?\\d+[A-Za-z]*$";
			try {
				new URL(contentId);
				isValidContentId = false;
			} catch (MalformedURLException e) {
				if (!contentId.matches(regex)) {
					isValidContentId = false;
				}
			}
		}
		return isValidContentId;
	}
}
