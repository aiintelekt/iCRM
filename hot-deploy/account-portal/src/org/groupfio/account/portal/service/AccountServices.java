/**
 * 
 */
package org.groupfio.account.portal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
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
public class AccountServices {

	private static final String MODULE = AccountServices.class.getName();
	
	public static Map<String, Object> createAccount(DispatchContext dctx, Map<String, Object> context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		
    		callResult = dispatcher.runSync("crmsfa.createAccount", context);
    		
    		result.putAll(callResult);
    		
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	
    	return result;
    	
    }
	
	public static Map<String, Object> updateAccount(DispatchContext dctx, Map<String, Object> context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		
    		callResult = dispatcher.runSync("crmsfa.updateAccount", context);
    		
    		result.putAll(callResult);
    		
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	
    	return result;
    	
    }
    
    public static Map<String, Object> getAccountBasicInformation(DispatchContext dctx, Map<String, Object> context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	
    	String accountPartyId = (String) context.get("accountPartyId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	
    	try {
    		
    		List<EntityCondition> conditionList = FastList.newInstance();
    		
    		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, accountPartyId));
    		
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue partySummary = EntityUtil.getFirst( delegator.findList("PartySummaryDetailsView", mainConditons, null, null, null, false) );
			
    		if (UtilValidate.isEmpty(partySummary)) {
    			result.putAll(ServiceUtil.returnSuccess("Account not exists!"));
    			return result;
    		}
    		
    		result.put("accountType", null);
    		result.put("accountId", DataUtil.getPartyIdentificationValue(delegator, accountPartyId, "CIF"));
    		result.put("accountName", partySummary.getString("groupName"));
    		result.put("segment", null);
    		result.put("rmName", null);
    		result.put("sicCode", null);
    		result.put("postalCode", partySummary.getString("primaryPostalCode"));
    		
    		result.put("accountStatus", partySummary.getString("statusId"));
    		result.put("localName", partySummary.getString("companyNameLocal"));
    		result.put("noOfEmployees", partySummary.getString("numEmployees"));
    		
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully retrieve Dyna Screen Render Detail.."));
    	
    	return result;
    	
    }
    
}
