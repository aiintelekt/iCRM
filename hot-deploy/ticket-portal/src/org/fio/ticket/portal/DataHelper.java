package org.fio.ticket.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.fio.admin.portal.util.EnumUtil;
import org.fio.ticket.portal.constant.SrPortalConstant.SrResolutionConstant;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class DataHelper {
	private static final String MODULE = DataHelper.class.getName();
	
	public static void createCustRequestParty( Delegator delegator, String custRequestId, String partyId, String roleTypeId) {
    	try {
    		GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", roleTypeId,"partyId",partyId).queryFirst();
    		if(UtilValidate.isEmpty(custRequestParty)) {
    			if(UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(roleTypeId)) {
    				custRequestParty = delegator.makeValue("CustRequestParty");
    				custRequestParty.set("custRequestId", custRequestId);
    				custRequestParty.set("partyId", partyId);
    				custRequestParty.set("roleTypeId", roleTypeId);
    				custRequestParty.set("fromDate", UtilDateTime.nowTimestamp());
    				custRequestParty.create();
    			}
    		} else {
    			custRequestParty.set("lastUpdatedTxStamp", UtilDateTime.nowTimestamp());
    			custRequestParty.set("thruDate", null);
				custRequestParty.store();
    		}
    	} catch (Exception e) {
			
		}
    }
	
	public static void createCustReqResolution(Delegator delegator, Map<String, Object> context) {
		String reasonId = (String) context.get("reasonId");
		String causeCategoryId = (String) context.get("causeCategoryId");
		String custRequestId = (String) context.get("custRequestId");
		String custRequestTypeId = (String) context.get("custRequestTypeId");
		String enumId = "";
		String enumTypeId = "";
		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();	
			conditions.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
			if(SrResolutionConstant.REASON_CODE.equals(custRequestTypeId)) {
				enumId = reasonId;
				enumTypeId ="REASON_TYPE";
				conditions.add(EntityCondition.makeCondition("reasonId", EntityOperator.EQUALS, reasonId));
				conditions.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, SrResolutionConstant.REASON_CODE));
			} else if(SrResolutionConstant.CAUSE_CATEGORY.equals(custRequestTypeId)) {
				enumId = causeCategoryId;
				enumTypeId ="CAUSE_CATAGORY_TYPE";
				conditions.add(EntityCondition.makeCondition("causeCategoryId", EntityOperator.EQUALS, causeCategoryId));
				conditions.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, SrResolutionConstant.CAUSE_CATEGORY));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			GenericValue custReqResolution = EntityQuery.use(delegator).from("CustRequestResolution").where(condition).queryFirst();
			if(UtilValidate.isNotEmpty(custReqResolution)) {
				custReqResolution.setNonPKFields(context);
				custReqResolution.store();
			} else {
				String description = EnumUtil.getEnumDescription(delegator, enumTypeId, enumId);
				String custReqResolutionId= delegator.getNextSeqId("CustRequestResolution");
				custReqResolution = delegator.makeValue("CustRequestResolution");
				custReqResolution.set("custRequestResolutionId", custReqResolutionId);
				custReqResolution.set("custRequestId", custRequestId);
				custReqResolution.set("description", description);
				custReqResolution.setNonPKFields(context);
				custReqResolution.create();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void reAssignOwnerCustRequestParty (Delegator delegator, GenericValue userLogin, String custRequestId, String fromPartyId, String roleTypeId) {
		try {
			roleTypeId = UtilValidate.isNotEmpty(roleTypeId) ? roleTypeId : "CAL_OWNER";
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			List<EntityCondition> conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
					EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, fromPartyId),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId)
	                ));
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> partyAssignmentList = EntityQuery.use(delegator).from("CustRequestParty").where(mainConditons).filterByDate().queryList();
			if (UtilValidate.isNotEmpty(partyAssignmentList)) {
				for (GenericValue partyAssignment : partyAssignmentList) {
					partyAssignment.put("thruDate", UtilDateTime.nowTimestamp());
					partyAssignment.store();
				}
			}
			
			conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, fromPartyId),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId)
	                ));
			
			mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue partyAssignment = EntityQuery.use(delegator).from("CustRequestParty").where(mainConditons).filterByDate().queryFirst();
					//EntityUtil.getFirst( delegator.findList("CustRequestParty", mainConditons, UtilMisc.toSet("partyId"), null, null, false) );
			if (UtilValidate.isEmpty(partyAssignment)) {
				partyAssignment = delegator.makeValue("CustRequestParty");
				partyAssignment.set("custRequestId", custRequestId);
				partyAssignment.set("partyId", fromPartyId);
				partyAssignment.set("roleTypeId", roleTypeId);
				partyAssignment.set("fromDate", UtilDateTime.nowTimestamp());
				partyAssignment.create();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void reAssignOwnerCustRequestParty (Delegator delegator, GenericValue userLogin, String custRequestId, List<String> ownerList) {
		reAssignOwnerCustRequestParty(delegator, userLogin, custRequestId, ownerList, "");
	}
	public static void reAssignOwnerCustRequestParty (Delegator delegator, GenericValue userLogin, String custRequestId, List<String> ownerList, String workEffortId) {
		try {
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			List<String> ownerRoles = new ArrayList<>();
			String activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
			if(UtilValidate.isNotEmpty(activityOwnerRole)) {
				if(UtilValidate.isNotEmpty(activityOwnerRole) && activityOwnerRole.contains(",")) {
					ownerRoles = org.fio.admin.portal.util.DataUtil.stringToList(activityOwnerRole, ",");
				} else
					ownerRoles.add(activityOwnerRole);
			}
			if(UtilValidate.isEmpty(ownerRoles)) ownerRoles.add("CAL_OWNER");
			List<String> ownerPartyIds = new ArrayList<String>();
			for(String owner : ownerList) {
				ownerPartyIds.add(org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, owner));
			}
			List<String> actParties = getCustRequestActivityParties(delegator, custRequestId, workEffortId);
			if(UtilValidate.isNotEmpty(actParties)) {
				ownerPartyIds.addAll(actParties);
			}
			
			Map<String, Object> anchorPartyMap = getCustRequestAnchorParties(delegator, custRequestId);
			if(UtilValidate.isNotEmpty(anchorPartyMap)) {
				List<String> anchorPartyIds = anchorPartyMap
												.values()
												.stream()
												.map(String::valueOf)
												.collect(Collectors.toList());
				
				if(UtilValidate.isNotEmpty(anchorPartyIds)) {
					ownerPartyIds.addAll(anchorPartyIds);
				}
			}
			List<String> distinctPartyIds = ownerPartyIds.stream()
                    .distinct()
                    .collect(Collectors.toList());
			List<EntityCondition> conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
					EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, distinctPartyIds),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ownerRoles),
					EntityUtil.getFilterByDateExpr()
	                ));
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> partyAssignmentList = EntityQuery.use(delegator).from("CustRequestParty").where(mainConditons).queryList();
			if (UtilValidate.isNotEmpty(partyAssignmentList)) {
				for (GenericValue partyAssignment : partyAssignmentList) {
					partyAssignment.put("thruDate", UtilDateTime.nowTimestamp());
					partyAssignment.store();
				}
			}
			
			for(String owner : ownerList) {
        		String ownerPartyId1 = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, owner);
        		String ownerRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, ownerPartyId1);
        		
        		conditionList = FastList.newInstance();
    			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
    					EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
    					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ownerPartyId1),
    					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, ownerRoleTypeId)
    	                ));
    			
    			mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			GenericValue partyAssignment = EntityQuery.use(delegator).from("CustRequestParty").where(mainConditons).filterByDate().queryFirst();
    					//EntityUtil.getFirst( delegator.findList("CustRequestParty", mainConditons, UtilMisc.toSet("partyId"), null, null, false) );
    			if (UtilValidate.isEmpty(partyAssignment)) {
    				partyAssignment = delegator.makeValue("CustRequestParty");
    				partyAssignment.set("custRequestId", custRequestId);
    				partyAssignment.set("partyId", ownerPartyId1);
    				partyAssignment.set("roleTypeId", ownerRoleTypeId);
    				partyAssignment.set("fromDate", UtilDateTime.nowTimestamp());
    				partyAssignment.create();
    			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getCustomFieldId(Delegator delegator, String groupId, String customFieldName) {
		String customFieldId = "";
		try {
			if(UtilValidate.isNotEmpty(customFieldName)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("customFieldName", EntityOperator.EQUALS, customFieldName));
				if(UtilValidate.isNotEmpty(groupId))
					conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
				GenericValue customField = EntityQuery.use(delegator).from("CustomField").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).queryFirst();
				customFieldId = UtilValidate.isNotEmpty(customField) ? customField.getString("customFieldId") : "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customFieldId;
	}

	public static void createCustRequestAnchorParty(Delegator delegator, String custRequestId, String partyId, String roleTypeId) {
		try {
			String customFieldName = "ANR_"+roleTypeId;
			//String partyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
			//String partySecurityRole = DataUtil.getPartySecurityRole(delegator, partyId);
			String customFieldId = getCustomFieldId(delegator, "ANCHOR_ROLES", customFieldName);
			if(UtilValidate.isNotEmpty(customFieldId)) {
				GenericValue custRequestAttr = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId, "attrName", customFieldId).queryFirst();
				if(UtilValidate.isNotEmpty(custRequestAttr)) {
					custRequestAttr.set("attrValue", partyId);
					custRequestAttr.store();
				} else {
					custRequestAttr = delegator.makeValue("CustRequestAttribute");
					custRequestAttr.set("custRequestId", custRequestId);
					custRequestAttr.set("attrName", customFieldId);
					custRequestAttr.set("attrValue", partyId);
					custRequestAttr.set("channelId", "ANCHOR_ROLES");
					custRequestAttr.create();
				}
			} else
				Debug.logInfo("Please configure attribute field with following customFieldId : "+customFieldName, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void removeCustRequestAnchorParty(Delegator delegator, String custRequestId, String partyId, String roleTypeId) {
		try {
			String customFieldName = "ANR_"+roleTypeId;
			String customFieldId = getCustomFieldId(delegator, "ANCHOR_ROLES", customFieldName);
			if(UtilValidate.isNotEmpty(customFieldId)) {
				GenericValue custRequestAttr = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId, "attrName", customFieldId, "attrValue", partyId).queryFirst();
				if(UtilValidate.isNotEmpty(custRequestAttr)) {
					custRequestAttr.remove();
				}
			} else
				Debug.logInfo("Please configure attribute field with following customFieldId : "+customFieldName, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getCustRequestAnchorParty(Delegator delegator, String custRequestId, String roleTypeId) {
		String attrValue = "";
		try {
			//String attrName = roleTypeId;
			if(UtilValidate.isNotEmpty(custRequestId)) {
				Map<String, Object> result = getCustRequestAnchorParties(delegator, custRequestId);
				attrValue = UtilValidate.isNotEmpty(result) ? (String) result.get(roleTypeId) : "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attrValue;
	}
	
	
	public static Map<String, Object> getCustRequestAnchorParties(Delegator delegator, String custRequestId){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<GenericValue> custRequestAttrList = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId,  "channelId", "ANCHOR_ROLES").queryList();
			if(UtilValidate.isNotEmpty(custRequestAttrList)) {
				for(GenericValue custRequestAttr : custRequestAttrList) {
					String customFieldId = custRequestAttr.getString("attrName");
					GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldId", customFieldId, "groupId", "ANCHOR_ROLES").queryFirst();
					if(UtilValidate.isNotEmpty(customField)) {
						String customFieldName = customField.getString("customFieldName");
						customFieldName = UtilValidate.isNotEmpty(customFieldName) ? customFieldName.substring(customFieldName.indexOf("_")+1) : "";
						result.put(customFieldName, custRequestAttr.getString("attrValue"));
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String isSuperiorDealerContact(Delegator delegator, String partyId, String attrName) {
		String isSuperior = "N";
		try {
			GenericValue partyAttribute = EntityQuery.use(delegator).from("PartyAttribute").where("partyId", partyId, "attrName",attrName).queryFirst();
			isSuperior = UtilValidate.isNotEmpty(partyAttribute) ? partyAttribute.getString("attrValue") : "N";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSuperior;
	}
	
	public static List<String> getAllDealerByContact(Delegator delegator, String contactPartyId){
		List<String> partyIds = new ArrayList<String>();
		try {
			if(UtilValidate.isNotEmpty(contactPartyId)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, contactPartyId),
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
						EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV")
						);
				List<GenericValue> partyRelationshipList = EntityQuery.use(delegator).select("partyIdTo").from("PartyRelationship").where(condition).filterByDate().queryList();
				if(UtilValidate.isNotEmpty(partyRelationshipList))
					partyIds = EntityUtil.getFieldListFromEntityList(partyRelationshipList, "partyIdTo", true);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partyIds;
	}
	
	public static List<String> getCustRequestActivityParties(Delegator delegator, String custRequestId, String workEffortId){
		List<String> partyIds = new ArrayList<String>();
		try {
			List<GenericValue> custRequestWorkEffortList = EntityQuery.use(delegator).from("CustRequestWorkEffort").where("custRequestId", custRequestId).queryList();
			List<String> workEffortIds = UtilValidate.isNotEmpty(custRequestWorkEffortList) ? EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList, "workEffortId", true) : new ArrayList<String>();
			if(UtilValidate.isNotEmpty(workEffortIds)) {
				workEffortIds.remove(workEffortId);
				EntityCondition workEffCondition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds),
						EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_IN, UtilMisc.toList("IA_COMPLETED", "IA_MCOMPLETED"))
						);
				List<GenericValue> workEffortList = EntityQuery.use(delegator).from("WorkEffort").where(workEffCondition).queryList();
				List<String> workEffIds = UtilValidate.isNotEmpty(workEffortList) ? EntityUtil.getFieldListFromEntityList(workEffortList, "workEffortId", true) : new ArrayList<String>();
				if(UtilValidate.isNotEmpty(workEffIds)) {
					
					List<EntityCondition> conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffIds),
							EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
							EntityUtil.getFilterByDateExpr()
			                ));
					
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					List<GenericValue> workEffortPartyList = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(mainConditons).queryList();
					partyIds = UtilValidate.isNotEmpty(workEffortPartyList) ? EntityUtil.getFieldListFromEntityList(workEffortPartyList, "partyId", true) : new ArrayList<String>();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partyIds;
	}
}
