/**
 * 
 */
package org.groupfio.crm.service.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class CustomerServiceImpl {

	private static final String MODULE = CustomerServiceImpl.class.getName();
    
	public static Map createCustomer(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String roleTypeId = (String) context.get("roleTypeId");
    	String vplusNumber = (String) context.get("vplusNumber");
    	String isNonCrm = (String) context.get("isNonCrm");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
        	
    		Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			Map<String, Object> input = UtilMisc.toMap("firstName", context.get("firstName"), "lastName", context.get("lastName"));
            input.put("firstNameLocal", context.get("firstNameLocal"));
            input.put("lastNameLocal", context.get("lastNameLocal"));
            input.put("personalTitle", context.get("personalTitle"));
            input.put("description", context.get("description"));
            input.put("nationalId", context.get("nationalId"));
            //input.put("statusId", statusId); // initial status
            Map<String, Object> serviceResults = dispatcher.runSync("createPerson", input);
            if (ServiceUtil.isError(serviceResults)) {
            	result.putAll(ServiceUtil.returnError("NON CRM customer creation failed!"));
    			return result;
            }
            
            String partyId = (String) serviceResults.get("partyId");
			
            if (UtilValidate.isNotEmpty(isNonCrm) && isNonCrm.equals("Y")) {
            	serviceResults = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "NON_CRM", "userLogin", userLogin));
            }
            
            // create a PartyRole for the resulting NON CRM partyId with roleTypeId = NON_CRM
            serviceResults = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId, "userLogin", userLogin));
            if (ServiceUtil.isError(serviceResults)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResults));
            }
            
            callCtxt = FastMap.newInstance();
//            callCtxt.put("partyId", partyId);
//            callCtxt.put("attrName", "VPLUS_NUMBER");
//            callCtxt.put("attrValue", vplusNumber);
//            callCtxt.put("userLogin", userLogin);
//            callResult = dispatcher.runSync("createPartyAttribute", callCtxt);
			org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, vplusNumber, "VPLUS_NUMBER");

    		result.put("partyId", partyId);
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created customer.."));
    	
    	return result;
    	
    }
    
}
