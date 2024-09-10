package org.groupfio.crm.service.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fio.homeapps.constants.GlobalConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * 
 * @author Arshiya
 *
 */
public class ServiceHistoryServiceImpl {
    public ServiceHistoryServiceImpl() {}

    private static final String MODULE = ServiceHistoryServiceImpl.class.getName();

    //Description Creating Service request History
    public static Map createServiceRequestHistory(DispatchContext dctx, Map context) {

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String externalId = (String) context.get("externalId");
        String userId = (String) context.get("userId");
        String stepName = (String) context.get("stepName");
        String stepDescription = (String) context.get("stepDescription");
        String relAction = (String) context.get("relAction");
        String remark = (String) context.get("remark");
        Timestamp remarkTime = (Timestamp) context.get("remarkTime");
        String rejectReason = (String) context.get("rejectReason");
        String role = (String) context.get("role");
        String type = (String) context.get("type");

        Timestamp startDateTime = (Timestamp) context.get("startDateTime");
        Timestamp endDateTime = (Timestamp) context.get("endDateTime");
        String message = (String) context.get("message");
        String jobId = (String) context.get("jobId");
        String jobStepCode = (String) context.get("jobStepCode");
        String status = (String) context.get("status");
        String recordType = (String) context.get("recordType");
        String msgUId = (String) context.get("msgUId");
        Timestamp requestDateTime = (Timestamp) context.get("requestDateTime");
        String systemName = (String) context.get("systemName");
        String srHistoryLogId = null;
        Map<String, Object> result = new HashMap<String, Object>();
        
        try {
            if (UtilValidate.isNotEmpty(externalId)) {
                GenericValue serviceHistory = delegator.makeValue("ServiceHistory");

                srHistoryLogId = delegator.getNextSeqId("ServiceHistory");
                
                Debug.log("srHistoryLogId====" + srHistoryLogId);
                
                serviceHistory.put("srHistoryLogId", srHistoryLogId);
                serviceHistory.put("externalId", externalId);
                serviceHistory.put("userId", userId);
                serviceHistory.put("stepName", stepName);
                serviceHistory.put("stepDescription", stepDescription);
                serviceHistory.put("relAction", relAction);
                serviceHistory.put("remark", remark);
                serviceHistory.put("remarkTime", remarkTime);
                serviceHistory.put("rejectReason", rejectReason);
                serviceHistory.put("role", role);
                serviceHistory.put("type", type);
                serviceHistory.put("startDateTime", startDateTime);
                serviceHistory.put("endDateTime", endDateTime);
                serviceHistory.put("message", message);
                serviceHistory.put("jobId", jobId);
                serviceHistory.put("jobStepCode", jobStepCode);
                serviceHistory.put("status", status);
                serviceHistory.put("recordType", recordType);
                serviceHistory.put("msgUId", msgUId);
                serviceHistory.put("systemName", systemName);
                serviceHistory.put("requestDateTime", requestDateTime);

                serviceHistory.put("createdByUserLogin", userLogin.getString("userLoginId"));
                serviceHistory.put("createdDate", UtilDateTime.nowTimestamp());
                
                serviceHistory.create();
                
            }
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        result.put("srHistoryLogId", srHistoryLogId);
        result.putAll(ServiceUtil.returnSuccess("Successfully created SR History.."));

        return result;
    }
    
    public static Map detailServiceHistory(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String workEffortId = (String) context.get("workEffortId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
        	
    		EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId)
                    );
			
			GenericValue activity = EntityUtil.getFirst(delegator.findList("WorkEffort", mainCondition, null, null, null, false));
			if (UtilValidate.isEmpty(activity)) {
    			result.putAll(ServiceUtil.returnError("activity not exists!"));
    			return result;
    		}
    		
    		Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
    		
			result.put("activity", activity);
			
			List conditionList = FastList.newInstance();
			
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
					EntityCondition.makeCondition(EntityOperator.OR,
	                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CAL_OWNER")
		            ),
					EntityUtil.getFilterByDateExpr()
	                ));
			
			/*conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
				EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
				EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
				EntityCondition.makeCondition(EntityOperator.OR,
                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER"),
                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "PROSPECT")
	            ),
				EntityUtil.getFilterByDateExpr()
                ));*/
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue partyAssignment = EntityUtil.getFirst( delegator.findList("WorkEffortPartyAssignment", mainConditons, null, null, null, false) );
			if (UtilValidate.isNotEmpty(partyAssignment)) {
				result.put("partyId", partyAssignment.getString("partyId"));
				result.put("roleTypeId", partyAssignment.getString("roleTypeId"));
				result.put("callOutCome", partyAssignment.getString("callOutCome"));
			}
			
			result.put("workEffortId", workEffortId);
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated activity.."));
    	
    	return result;
    	
    }
public static Map findServiceHistory(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String srNumber = (String) context.get("srNumber");
    	String createdBy = (String) context.get("createdBy");
    	String responsiblePerson = (String) context.get("responsiblePerson");
    	String caseId = (String) context.get("caseId");
    	Timestamp createdDateRangeStart = (Timestamp) context.get("createdDateRangeStart");
    	Timestamp createdDateRangeEnd = (Timestamp) context.get("createdDateRangeEnd");
    	String statusId = (String) context.get("statusId");
    	String subStatusId = (String) context.get("subStatusId");
    	String msguid = (String) context.get("msguid");
    	Timestamp requestedTime = (Timestamp) context.get("requestedTime");
    	String systemName = (String) context.get("systemName");
    	
    	String nextPageNum = (String) context.get("nextPageNum");
    	
    	List<EntityCondition> searchConditions = FastList.newInstance();
    	List cifPartyList = FastList.newInstance();
    	List cifCustReqs = FastList.newInstance();
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
        	
    		Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			List conditionList = FastList.newInstance();
			List<EntityCondition> serviceRequestConditions = new ArrayList<EntityCondition>();
			
			
			if (UtilValidate.isNotEmpty(srNumber)) {
				serviceRequestConditions.add(EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, srNumber));
			}
			if (UtilValidate.isNotEmpty(createdBy)) {
				serviceRequestConditions.add(EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, createdBy));
			}
			
			if (UtilValidate.isNotEmpty(responsiblePerson)) {
				serviceRequestConditions.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, responsiblePerson));
			}
			
			if (UtilValidate.isNotEmpty(caseId)) {
				serviceRequestConditions.add(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId));
			}
			
			if (UtilValidate.isNotEmpty(createdDateRangeStart)) {
				serviceRequestConditions.add(
						EntityCondition.makeCondition(
				                EntityCondition.makeCondition("createdDate", EntityOperator.EQUALS, null),
				                EntityOperator.OR,
				                EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, createdDateRangeStart)
				           )
						);
			}
			if (UtilValidate.isNotEmpty(createdDateRangeEnd)) {
				serviceRequestConditions.add(
						EntityCondition.makeCondition(
				                EntityCondition.makeCondition("createdDate", EntityOperator.EQUALS, null),
				                EntityOperator.OR,
				                EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, createdDateRangeEnd)
				           )
						);
			}
			if (UtilValidate.isNotEmpty(statusId)) {
				serviceRequestConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
			}
			
			if (UtilValidate.isNotEmpty(subStatusId)) {
				serviceRequestConditions.add(EntityCondition.makeCondition("subStatusId", EntityOperator.EQUALS, subStatusId));
			}
			Set<String> fieldsToSelect= UtilMisc.toSet("externalId");
			EntityCondition srCondition = EntityCondition.makeCondition(serviceRequestConditions, EntityOperator.AND);
			
			cifPartyList=delegator.findList("CustRequest", srCondition, fieldsToSelect,UtilMisc.toList("externalId"),null,false);	
			
			cifCustReqs = EntityUtil.getFieldListFromEntityList(cifPartyList, "custRequestId", true);
			
			if (cifCustReqs != null && cifCustReqs.size() > 0) {
				searchConditions.add(EntityCondition.makeCondition("externalId", EntityOperator.IN, cifCustReqs));
			}else {
				searchConditions.add(EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, srNumber));
			}
			EntityCondition mainConditons = EntityCondition.makeCondition(searchConditions, EntityOperator.AND);
			
			long count = 0;
			
			EntityFindOptions  efoNum= new EntityFindOptions();
			
			efoNum.setFetchSize(1000);
			
			Debug.logInfo("count 1 start: "+UtilDateTime.nowTimestamp(), MODULE);
			count = delegator.findCountByCondition("ServiceHistory", mainConditons, null, null, efoNum);
			Debug.logInfo("count 2 end: "+UtilDateTime.nowTimestamp(), MODULE);
			
			EntityFindOptions efo = new EntityFindOptions();
			
			if (UtilValidate.isNotEmpty(nextPageNum)) {
				
				long npn = Long.parseLong(nextPageNum) - 1;
				
				int startInx = (int) (npn * GlobalConstants.SR_HISTORY_PAGE_COUNT);
				int endInx = GlobalConstants.SR_HISTORY_PAGE_COUNT;		
				
				efo.setOffset(startInx);
				efo.setLimit(endInx);
			}
			Debug.log("mainConditons==="+mainConditons);
			List<GenericValue> serviceRequestHistoryList = delegator.findList("ServiceHistory", mainConditons, null, UtilMisc.toList("startDateTime DESC"), efo, false);
			Debug.log("serviceRequestHistoryList==="+serviceRequestHistoryList);
			result.put("serviceRequestHistoryList", serviceRequestHistoryList);
			result.put("totalCount", count);
			
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully find Service Request History.."));
    	
    	return result;
    	
    }
}