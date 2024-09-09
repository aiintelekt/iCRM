/**
 * 
 */
package org.groupfio.crm.service.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.crm.service.CrmServiceConstants.SourceInvoked;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class DataUtil {
	
	private static String MODULE = DataUtil.class.getName();
	
	/*public static final Map<String, String> BASED_ON_CONFIG =
			Collections.unmodifiableMap(new HashMap<String, String>() {
				{
					put("TRANS_VOL", "RA_ON_1001");
					put("GIRO_TRANS_VOL", "RA_ON_1002");
					put("TRANS_AMT_LCL_CURR", "RA_ON_1000");
					put("TRANS_AMT_REMIT_CURR", "RA_ON_1003");
					put("COPY_CREATE", "RA_ON_1004");
				}
			});*/
	
	public static final Map<String, String> MANDATORY_FIELD_CONFIG =
			Collections.unmodifiableMap(new HashMap<String, String>() {
				{
					put("uomId", "Currency Code");
					put("uomIdTo", "To Currency");
					
					put("ratesViewdate", "Date");
					put("location", "Location");
					put("benchmarkName", "Benchmark");
					put("rscurrency", "Currency");
					
					put("currency", "Currency");
					put("effectiveDate", "Effective Date");
					put("productId", "Product Id");
					put("customerId", "Customer Id");
					put("counterCustomerId", "Counter Customer Id");
					put("customerName", "Customer Name");
					put("counterCustomerName", "Counter Customer Name");
					
				}
			});
	
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
	
	public static void reAssignWorkEffortParty (LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String workEffortId, String fromPartyId, String roleTypeId) {
		try {
			
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			List<EntityCondition> conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
					
					EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("CUSTOMER", "CARD_CUST")),
					
					EntityUtil.getFilterByDateExpr()
	                ));
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> partyAssignmentList = delegator.findList("WorkEffortPartyAssignment", mainConditons, null, null, null, false);
			if (UtilValidate.isNotEmpty(partyAssignmentList)) {
				for (GenericValue partyAssignment : partyAssignmentList) {
					partyAssignment.put("thruDate", UtilDateTime.nowTimestamp());
					partyAssignment.store();
				}
			}
			
			conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
					
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, fromPartyId),
					
					EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("CUSTOMER", "CARD_CUST")),
					
					EntityUtil.getFilterByDateExpr()
	                ));
			
			mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue partyAssignment = EntityUtil.getFirst( delegator.findList("WorkEffortPartyAssignment", mainConditons, UtilMisc.toSet("partyId"), null, null, false) );
			if (UtilValidate.isEmpty(partyAssignment)) {
				callCtxt = UtilMisc.toMap("partyId", fromPartyId, "workEffortId", workEffortId, "roleTypeId", roleTypeId, "statusId", "PRTYASGN_ASSIGNED", "userLogin", userLogin);
				callCtxt.put("assignedByUserLoginId", userLogin.getString("userLoginId"));
				callResult = dispatcher.runSync("assignPartyToWorkEffort", callCtxt);
	            if (ServiceUtil.isError(callResult)) {
	            	Debug.logError(ServiceUtil.getErrorMessage(callResult), MODULE);
	            }
			}
			
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
	}
	
	public static void reAssignOwnerWorkEffortParty (LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String workEffortId, String fromPartyId) {
		reAssignOwnerWorkEffortParty(dispatcher, delegator, userLogin,workEffortId, fromPartyId, null);
	}
	public static void reAssignOwnerWorkEffortParty (LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String workEffortId, String fromPartyId, String roleTypeId) {
		try {
			roleTypeId = UtilValidate.isNotEmpty(roleTypeId) ? roleTypeId : "CAL_OWNER";
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			List<EntityCondition> conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
					EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, fromPartyId),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId),
					EntityUtil.getFilterByDateExpr()
	                ));
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> partyAssignmentList = delegator.findList("WorkEffortPartyAssignment", mainConditons, null, null, null, false);
			if (UtilValidate.isNotEmpty(partyAssignmentList)) {
				for (GenericValue partyAssignment : partyAssignmentList) {
					partyAssignment.put("thruDate", UtilDateTime.nowTimestamp());
					partyAssignment.store();
				}
			}
			
			conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, fromPartyId),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId),
					
					EntityUtil.getFilterByDateExpr()
	                ));
			
			mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue partyAssignment = EntityUtil.getFirst( delegator.findList("WorkEffortPartyAssignment", mainConditons, UtilMisc.toSet("partyId"), null, null, false) );
			if (UtilValidate.isEmpty(partyAssignment)) {
				callCtxt = UtilMisc.toMap("partyId", fromPartyId, "workEffortId", workEffortId, "roleTypeId", roleTypeId, "statusId", "PRTYASGN_ASSIGNED", "userLogin", userLogin);
				callCtxt.put("assignedByUserLoginId", userLogin.getString("userLoginId"));
				callResult = dispatcher.runSync("assignPartyToWorkEffort", callCtxt);
	            if (ServiceUtil.isError(callResult)) {
	            	Debug.logError(ServiceUtil.getErrorMessage(callResult), MODULE);
	            }
			}
			
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
	}
	
	public static void reAssignOwnerWorkEffortParty (LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String workEffortId, List<String> ownerList) {
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
			List<EntityCondition> conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
					EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, ownerPartyIds),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ownerRoles),
					EntityUtil.getFilterByDateExpr()
	                ));
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> partyAssignmentList = delegator.findList("WorkEffortPartyAssignment", mainConditons, null, null, null, false);
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
    					EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
    					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
    					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ownerPartyId1),
    					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, ownerRoleTypeId),
    					
    					EntityUtil.getFilterByDateExpr()
    	                ));
    			
    			mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			GenericValue partyAssignment = EntityUtil.getFirst( delegator.findList("WorkEffortPartyAssignment", mainConditons, UtilMisc.toSet("partyId"), null, null, false) );
    			if (UtilValidate.isEmpty(partyAssignment)) {
    				Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, ownerPartyId1);
					String businessUnit = (String) buTeamData.get("businessUnit");
					String teamId = (String) buTeamData.get("emplTeamId");
    				callCtxt = UtilMisc.toMap("partyId", ownerPartyId1, "workEffortId", workEffortId, "roleTypeId", ownerRoleTypeId, "statusId", "PRTYASGN_ASSIGNED", "userLogin", userLogin);
    				callCtxt.put("assignedByUserLoginId", userLogin.getString("userLoginId"));
    				callCtxt.put("ownerId", owner);
    				callCtxt.put("emplTeamId", teamId);
    				callCtxt.put("businessUnit", businessUnit);
    				
    				callResult = dispatcher.runSync("assignPartyToWorkEffort", callCtxt);
    	            if (ServiceUtil.isError(callResult)) {
    	            	Debug.logError(ServiceUtil.getErrorMessage(callResult), MODULE);
    	            }
    			}
			}
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
	}
	
	public static EntityCondition prepareStatusCondition (String statusId, String columnName, String statusPrefix) {
		
		EntityCondition condition = null;
		
		try {
			
			if (statusId.equals(statusPrefix + "OPEN")) {
				List<String> statusIds = new ArrayList<String>();
				statusIds.add(statusPrefix + "OPEN");
				statusIds.add(statusPrefix + "PENDING");
				statusIds.add(statusPrefix + "ASSIGNED");
				statusIds.add(statusPrefix + "INFO_PROV");
				statusIds.add(statusPrefix + "OVER_DUE");
				statusIds.add(statusPrefix + "ESCALATED");
				statusIds.add(statusPrefix + "MDRAFT");
				condition = EntityCondition.makeCondition(columnName, EntityOperator.IN, statusIds);
			} else if (statusId.equals(statusPrefix + "CLOSED")) {
				List<String> statusIds = new ArrayList<String>();
				statusIds.add(statusPrefix + "CLOSED");
				statusIds.add(statusPrefix + "CANCELLED");
				condition = EntityCondition.makeCondition(columnName, EntityOperator.IN, statusIds);
			} else {
				condition = EntityCondition.makeCondition(columnName, EntityOperator.EQUALS, statusId);
			}
			
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		
		return condition;
	}
	
	public static boolean isInteger(String s) {
        boolean isValidInteger = false;
        try {
            Integer.parseInt(s.trim());
            isValidInteger = true;
        } catch (NumberFormatException ex) {}
        return isValidInteger;
    }
}
