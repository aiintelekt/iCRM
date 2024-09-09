/**
 * 
 */
package org.groupfio.common.portal.util;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class UtilApproval {
	
	private static final String MODULE = UtilApproval.class.getName();

	public static Map<String, Object> updateApprovalActivity(Delegator delegator, Map<String, Object> context) {
		Map<String, Object> response = new HashMap<>();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			
			String approvalId = (String) context.get("approvalId");
			String activityStatusId = (String) context.get("activityStatusId");
			String decisionStatusId = (String) context.get("decisionStatusId");
			Timestamp actualStartDate = (Timestamp) context.get("actualStartDate");
			Timestamp actualCompletionDate = (Timestamp) context.get("actualCompletionDate");
			
			Timestamp curentTime = UtilDateTime.nowTimestamp();
			
			if (UtilValidate.isNotEmpty(approvalId)) {
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, approvalId));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue activity = EntityUtil.getFirst( delegator.findList("WorkEffort", mainConditons, null, null, null, false) );
				if (UtilValidate.isNotEmpty(activity)) {
					if (UtilValidate.isNotEmpty(decisionStatusId) && decisionStatusId.equals("DECISION_REVIEW")) {
						activityStatusId = "IA_MIN_PROGRESS";
					}
					
					activity.put("currentStatusId", UtilValidate.isNotEmpty(activityStatusId) ? activityStatusId : activity.getString("currentStatusId"));
					activity.put("actualStartDate", UtilValidate.isNotEmpty(actualStartDate) ? actualStartDate : activity.get("actualStartDate"));
	                activity.put("actualCompletionDate", UtilValidate.isNotEmpty(actualCompletionDate) ? actualCompletionDate : null);
					
					activity.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
	                activity.put("lastModifiedDate", curentTime);
	                
	                if (UtilValidate.isNotEmpty(activityStatusId) && activityStatusId.equals("IA_MCOMPLETED")) {
	                	activity.put("closedDateTime", curentTime);
	                	activity.put("closedByUserLogin", userLogin.getString("userLoginId"));
	                }
	                
	                activity.store();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return response;
	}
	
	public static Map<String, Object> isEsignCompleted(Delegator delegator, Map<String, Object> context) {
		Map<String, Object> response = new HashMap<>();
		String isEsignCompleted = "N";
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			
			String agreementId = (String) context.get("agreementId");
			GenericValue esignature = null;
			GenericValue SMesignature = null;
			if (UtilValidate.isNotEmpty(agreementId)) {
				esignature = EntityQuery.use(delegator).from("WorkEffortSignatureHistory").where("workEffortId", agreementId, "roleTypeId", "CUSTOMER")
							.filterByDate()
							.queryFirst();
				SMesignature = EntityQuery.use(delegator).from("WorkEffortSignatureHistory").where("workEffortId", agreementId, "roleTypeId", "SALES_MANAGER")
							   .filterByDate()
							   .queryFirst();
				if (UtilValidate.isNotEmpty(esignature) && UtilValidate.isNotEmpty(SMesignature)) {
					isEsignCompleted = "Y";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		response.put("isEsignCompleted", isEsignCompleted);
		return response;
	}
	
	public static Map<String, Object> getActivieApprovalDetail(Delegator delegator, Map<String, Object> context) {
		Map<String, Object> result = new HashMap<>();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			
			String approvalCategoryId = (String) context.get("approvalCategoryId");
			String parentWorkEffortId = (String) context.get("parentWorkEffortId");
			
			String domainEntityType = (String) context.get("domainEntityType");
			String domainEntityId = (String) context.get("domainEntityId");
			
			List<String> approvalCategoryIds = Arrays.asList(approvalCategoryId.split(","));
			
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
			conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
			conditionList.add(EntityCondition.makeCondition("parentWorkEffortId", EntityOperator.EQUALS, null));
			if (UtilValidate.isNotEmpty(parentWorkEffortId)) {
				conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, parentWorkEffortId));
			}
			conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("decisionStatusId", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("decisionStatusId", EntityOperator.EQUALS, "DECISION_REVIEW")
			));
			conditionList.add(EntityCondition.makeCondition("approvalCategoryId", EntityOperator.IN, approvalCategoryIds));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue approval = EntityUtil.getFirst( delegator.findList("WorkEffortApproval", mainConditons, null, null, null, false) );
			if (UtilValidate.isNotEmpty(approval)) {
				result.put("approval", approval);
				result.put("approvalId", approval.getString("workEffortId"));
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Found active approval process");
			} else {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.NOT_FOUND_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Not Found active approval process");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
		}
		return result;
	}
	
}
