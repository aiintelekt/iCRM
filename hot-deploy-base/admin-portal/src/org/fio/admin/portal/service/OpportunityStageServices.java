package org.fio.admin.portal.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class OpportunityStageServices {

	private static final String MODULE = OpportunityStageServices.class.getName();
    
    public static Map createOppoStage(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();		
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String description = (String) context.get("description");
    	BigDecimal defaultProbability = (BigDecimal) context.get("defaultProbability");
    	Long sequenceNum = (Long) context.get("sequenceNum");
    	String opportunityStatusId = (String) context.get("opportunityStatusId");
    	String enable = (String) context.get("enable");
    	
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		
    		List conditionList = FastList.newInstance();
    		
    		conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.EQUALS, EntityFunction.UPPER(description)));
    		conditionList.add(EntityCondition.makeCondition("opportunityStatusId", EntityOperator.EQUALS, opportunityStatusId));
    		
    		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		
    		GenericValue oppoStage = EntityUtil.getFirst( delegator.findList("SalesOpportunityStage", mainConditons, null, null, null, false) );
    		if (UtilValidate.isNotEmpty(oppoStage)) {
    			result.putAll(ServiceUtil.returnError("Opportunity Stage already exists!"));
    			return result;
    		}
    		
    		String opportunityStageId = delegator.getNextSeqId("SalesOpportunityStage");
    		oppoStage = delegator.makeValue("SalesOpportunityStage");
			
    		oppoStage.put("opportunityStageId", opportunityStageId);
    		oppoStage.put("description", description);
    		oppoStage.put("defaultProbability", defaultProbability);
    		oppoStage.put("sequenceNum", sequenceNum);
    		oppoStage.put("opportunityStatusId", opportunityStatusId);
    		oppoStage.put("enable", enable);
    		oppoStage.put("createdByUserLogin", userLogin.getString("userLoginId"));
    		
    		oppoStage.create();
			
			result.put("opportunityStageId", opportunityStageId);
			
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created opportunity stage.."));
    	
    	return result;
    	
    }
    
    public static Map updateOppoStage(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String opportunityStageId = (String) context.get("opportunityStageId");
    	
    	String description = (String) context.get("description");
    	BigDecimal defaultProbability = (BigDecimal) context.get("defaultProbability");
    	Long sequenceNum = (Long) context.get("sequenceNum");
    	String opportunityStatusId = (String) context.get("opportunityStatusId");
    	String enable = (String) context.get("enable");
    	
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		
    		result.put("opportunityStageId", opportunityStageId);
    		
    		List conditionList = FastList.newInstance();
    		
    		conditionList.add(EntityCondition.makeCondition("opportunityStageId", EntityOperator.EQUALS, opportunityStageId));
    		
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue oppoStage = EntityUtil.getFirst( delegator.findList("SalesOpportunityStage", mainConditons, null, null, null, false) );
			
    		if (UtilValidate.isEmpty(oppoStage)) {
    			result.putAll(ServiceUtil.returnError("Opportunity Stage not exists!"));
    			return result;
    		}
    		
    		oppoStage.put("description", description);
    		oppoStage.put("defaultProbability", defaultProbability);
    		oppoStage.put("sequenceNum", sequenceNum);
    		oppoStage.put("opportunityStatusId", opportunityStatusId);
    		oppoStage.put("enable", enable);
    		oppoStage.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
			
			oppoStage.store();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated opportunity stage.."));
    	
    	return result;
    	
    }
    
}
