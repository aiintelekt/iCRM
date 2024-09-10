/**
 * 
 */
package org.groupfio.lead.portal.service;

import java.util.HashMap;
import java.util.Map;

import org.groupfio.lead.service.util.LeadServiceUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class LeadServices {

	private static final String MODULE = LeadServices.class.getName();
	
	public static Map createLead(DispatchContext dctx, Map<String, Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		context.put("accountName", context.get("leadName"));
    		context.put("accountOrLead", "Y");
    		context.put("securityMtxEntity", "Lead");
    		context.put("statusId", context.get("statusId"));
    		context.remove("leadName");
    		callResult = dispatcher.runSync("crmsfa.createAccount", context);
    		result.putAll(callResult);
    		
    		if (ServiceUtil.isSuccess(callResult)) {
    			String partyId = (String) callResult.get("partyId");
    			// track the lead history [start]
    			callCtxt = FastMap.newInstance();
    			callCtxt.put("partyId", partyId);
    			callCtxt.put("userLogin", userLogin);
    			
    			callResult = dispatcher.runSync("lead.createLeadHistory", callCtxt);
    			if (ServiceUtil.isSuccess(callResult)) {
    				Debug.logInfo("successfully create lead history", MODULE);
    			}
    			
    			LeadServiceUtil.storeLeadEvent(UtilMisc.toMap("dispatcher", dispatcher, "partyId", partyId, "eventTypeId", "LEH_LD_CREATED", "userLogin", userLogin));
    			// track the lead history [end]
    		}
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	return result;
    }
	
	public static Map updateLead(DispatchContext dctx, Map context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		context.put("accountName", context.get("leadName"));
    		context.put("securityMtxEntity", "Lead");
    		context.remove("leadName");
    		
    		callResult = dispatcher.runSync("crmsfa.updateAccount", context);
    		result.putAll(callResult);
    		
    		if (ServiceUtil.isSuccess(callResult)) {
    			// track the lead history [start]
    			callCtxt = FastMap.newInstance();
    			callCtxt.put("partyId", context.get("partyId"));
    			callCtxt.put("userLogin", userLogin);
    			
    			callResult = dispatcher.runSync("lead.createLeadHistory", callCtxt);
    			if (ServiceUtil.isSuccess(callResult)) {
    				Debug.logInfo("successfully create lead history", MODULE);
    			}
    			
    			LeadServiceUtil.storeLeadEvent(UtilMisc.toMap("dispatcher", dispatcher, "partyId", context.get("partyId"), "eventTypeId", "LEH_LD_UPDATED", "userLogin", userLogin));
    			// track the lead history [end]
    		}
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	return result;
    }
	
	public static Map convertLead(DispatchContext dctx, Map context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		callResult = dispatcher.runSync("lead.convertLead", context);
    		result.putAll(callResult);
    		
    		if (ServiceUtil.isSuccess(callResult)) {
    			String partyId = (String) callResult.get("partyId");
    			// track the lead history [start]
    			callCtxt = FastMap.newInstance();
    			callCtxt.put("partyId", partyId);
    			callCtxt.put("userLogin", userLogin);
    			
    			callResult = dispatcher.runSync("lead.createLeadHistory", callCtxt);
    			if (ServiceUtil.isSuccess(callResult)) {
    				Debug.logInfo("successfully create lead history", MODULE);
    			}
    			
    			LeadServiceUtil.storeLeadEvent(UtilMisc.toMap("dispatcher", dispatcher, "partyId", partyId, "eventTypeId", "LEH_LD_CONVERTED", "userLogin", userLogin));
    			// track the lead history [end]
    		}
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	return result;
    }
	
	public static Map updateLeadStatus(DispatchContext dctx, Map context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String partyId = (String) context.get("partyId");
    	String statusId = (String) context.get("statusId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		callCtxt.put("partyId", partyId);
    		callCtxt.put("statusId", statusId);
    		callCtxt.put("userLogin", userLogin);
    		callResult = dispatcher.runSync("setPartyStatus", context);
    		result.putAll(callResult);
    		
    		if (ServiceUtil.isSuccess(callResult)) {
    			// track the lead history [start]
    			callCtxt = FastMap.newInstance();
    			callCtxt.put("partyId", context.get("partyId"));
    			callCtxt.put("userLogin", userLogin);
    			
    			callResult = dispatcher.runSync("lead.createLeadHistory", callCtxt);
    			if (ServiceUtil.isSuccess(callResult)) {
    				Debug.logInfo("successfully create lead history", MODULE);
    			}
    			
    			String statusCode = statusId.split("_")[1];
    			LeadServiceUtil.storeLeadEvent(UtilMisc.toMap("dispatcher", dispatcher, "partyId", context.get("partyId"), "eventTypeId", "LEH_LD_"+statusCode, "userLogin", userLogin));
    			// track the lead history [end]
    		}
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	return result;
    }
	
}
