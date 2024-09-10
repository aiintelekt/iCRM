/**
 * 
 */
package org.fio.dataimport.util;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class DataUtil {
	
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
			e.printStackTrace();
		}
		
		return null;
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
			e.printStackTrace();
		}
		
		return partyContactMechPurpose;
	}
	
	public static GenericValue findPartyByEmail(Delegator delegator, String emailAddress) {
		
		try {
			
			GenericValue contactMech = EntityUtil.getFirst(delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS", "infoString", emailAddress), null,false));
			if (UtilValidate.isNotEmpty(contactMech)) {
				
				EntityCondition mainCond = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMech.getString("contactMechId")),
						EntityUtil.getFilterByDateExpr()
						);
				
				GenericValue partyContactMech = EntityUtil.getFirst( delegator.findList("PartyContactMech", mainCond, null, null, null, false) );
				if (UtilValidate.isNotEmpty(partyContactMech)) {
					GenericValue party = EntityUtil.getFirst(delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyContactMech.getString("partyId")), null,false));
					return party;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static GenericValue findPartyByLogin(Delegator delegator, String oneBankId) {
		
		try {
			
			GenericValue userLoginId = EntityUtil.getFirst(delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId",  oneBankId), null,false));
			if (UtilValidate.isNotEmpty(userLoginId)) {
				GenericValue party = EntityUtil.getFirst(delegator.findByAnd("Party", UtilMisc.toMap("partyId", userLoginId.getString("partyId")), null,false));
				return party;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static GenericValue getFirstDbsRole(Delegator delegator, String partyId) {
		
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "DBS_%"),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)
						), EntityOperator.AND);
				List<GenericValue> dbsRoleList = delegator.findList("PartyRole", condition, null, null, null, false);
				if (UtilValidate.isNotEmpty(dbsRoleList)) {
					return dbsRoleList.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getLeadId(String prefix, String sequenceNumber){
		
		String formattedPostCode = "";
		if (UtilValidate.isNotEmpty(prefix)) {
			formattedPostCode = prefix;
		}
		
		if(UtilValidate.isNotEmpty(sequenceNumber)){
			int length = sequenceNumber.length();
			if (length==1) {
				formattedPostCode += "0000" + (sequenceNumber);
	        }
			else if (length==2) {
				formattedPostCode += "000" + (sequenceNumber);
	        }
			else if (length==3) {
				formattedPostCode += "00" + (sequenceNumber);
	        }
			else if (length==4) {
				formattedPostCode += "0" + (sequenceNumber);
			}
	        else{
	        	formattedPostCode += (sequenceNumber);
	        }
		}
		
		return formattedPostCode;
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

}
