/**
 * 
 */
package org.fio.homeapps.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.fio.homeapps.constants.UserAuditConstants.ApprovalStatus;
import org.fio.homeapps.util.UtilUserAudit;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.google.gson.Gson;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class UserAuditServiceImpl {
	
	private static final String MODULE = UserAuditServiceImpl.class.getName();

	public static Map createUserAuditRequest(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String serviceRequestType = (String) context.get("serviceRequestType");
    	String makerPartyId = (String) context.get("makerPartyId");
    	String modeOfAction = (String) context.get("modeOfAction");
    	String statusId = "PENDING";
    	String remarks = (String) context.get("remarks");
    	String userAuditRequestId = (String) context.get("userAuditRequestId");
    	String oldContextMap = (String) context.get("oldContextMap");
    	Map contextMap = (Map) context.get("contextMap");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue auditPref = EntityUtil.getFirst( delegator.findByAnd("UserAuditPref", UtilMisc.toMap("userAuditPrefId", serviceRequestType), null, false) );
    		
    		if (UtilValidate.isEmpty(auditPref)) {
    			return ServiceUtil.returnError("invalid UserAuditPref with userAuditPrefId: "+serviceRequestType);
    		}
    		
    		Timestamp currentTime = UtilDateTime.nowTimestamp();
    		Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
    		
    		if (UtilValidate.isNotEmpty(userAuditRequestId)) {
    			callCtxt = FastMap.newInstance();
    			
    			callCtxt.put("userAuditRequestId", userAuditRequestId);
    			callCtxt.put("userLogin", userLogin);
    			
    			callResult = dispatcher.runSync("homeapps.expireUserAuditRequest", callCtxt);
    		}
    		
    		// prepare data [start]
    		
    		contextMap.remove("userLogin");
    		contextMap.remove("timeZone");
    		contextMap.remove("locale");
    		contextMap.remove("oldContextMap");
    		
    		contextMap.remove("isPerformUserAudit");
    		contextMap.remove("userAuditRequestId");
    		
    		Gson gson = new Gson();
    		String jsonContext = gson.toJson(contextMap);
    		
    		String requestUri = UtilUserAudit.getRequestUri(delegator, contextMap, auditPref.getString("modeOfAction"), auditPref.getString("requestUri"), auditPref.getString("loadKeys"));
    		
    		// prepare data [end]
    		
    		GenericValue auditRequest = delegator.makeValue("UserAuditRequest");
    		
    		auditRequest.put("parentId", userAuditRequestId);
    		
    		userAuditRequestId = delegator.getNextSeqId("UserAuditRequest");
    		
    		auditRequest.put("userAuditRequestId", userAuditRequestId);
    		
    		auditRequest.put("serviceRequestType", serviceRequestType);
    		auditRequest.put("makerPartyId", makerPartyId);
    		auditRequest.put("modeOfAction", modeOfAction);
    		auditRequest.put("requestUri", requestUri);
    		
    		auditRequest.put("statusId", statusId);
    		auditRequest.put("statusDate", currentTime);
    		
    		auditRequest.put("fromDate", currentTime);
    		
    		auditRequest.put("remarks", remarks);
    		auditRequest.put("oldContextMap", oldContextMap);
    		auditRequest.put("contextMap", jsonContext);
    		
    		auditRequest.create();
    		
    		result.put("userAuditRequestId", userAuditRequestId);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Request sent for audit process"));
    	result.put("responseMessage", "audit-success");
    	
    	return result;
    	
    }
	
	public static Map expireUserAuditRequest(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String userAuditRequestId = (String) context.get("userAuditRequestId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	try {
        	
    		GenericValue auditRequest = EntityUtil.getFirst( delegator.findByAnd("UserAuditRequest",UtilMisc.toMap("userAuditRequestId", userAuditRequestId), null, false) );
    		
    		if (UtilValidate.isEmpty(auditRequest)) {
    			result.putAll(ServiceUtil.returnError("UserAuditRequest not exists!"));
    			return result;
    		}
    		
    		Timestamp currentTime = UtilDateTime.nowTimestamp();
    		
    		auditRequest.put("statusId", ApprovalStatus.EXPIRED);
    		auditRequest.put("statusDate", currentTime);
    		
    		auditRequest.put("thruDate", currentTime);
    		
    		auditRequest.store();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully expired UserAuditRequest"));
    	
    	return result;
    	
    }
	
}
