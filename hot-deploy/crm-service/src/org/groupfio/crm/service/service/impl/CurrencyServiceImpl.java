/**
 * 
 */
package org.groupfio.crm.service.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.ProductUtil;
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
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class CurrencyServiceImpl {

	private static final String MODULE = CurrencyServiceImpl.class.getName();
    
    public static Map createCurrency(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String currencyCode = (String) context.get("currencyCode");
    	String abbreviation = (String) context.get("abbreviation");
    	String description = (String) context.get("description");
    	String uomTypeId = (String) context.get("uomTypeId");
    	String isEnabled = (String) context.get("isEnabled");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	String productId = null;
    	try {
        	
    		EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, uomTypeId),
                    EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("uomId")), EntityOperator.EQUALS, currencyCode.toUpperCase())
                    );
			
			GenericValue uom = EntityUtil.getFirst(delegator.findList("Uom", mainCondition, null, Arrays.asList("uomId DESC"), null, false));
			if (UtilValidate.isNotEmpty(uom)) {
    			result.putAll(ServiceUtil.returnError("Currency already exists!"));
    			return result;
    		}
    		
    		Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
    		
    		uom = delegator.makeValue("Uom");
    		
    		uom.put("uomId", currencyCode);
    		uom.put("uomTypeId", uomTypeId);
    		uom.put("abbreviation", abbreviation);
    		uom.put("description", description);
    		uom.put("isEnabled", UtilValidate.isNotEmpty(isEnabled) ? isEnabled : "Y");
    		
    		uom.create();
    		
    		result.put("currencyCode", currencyCode);
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created Currency.."));
    	
    	return result;
    	
    }
    
    public static Map updateCurrency(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String currencyCode = (String) context.get("currencyCode");
    	String abbreviation = (String) context.get("abbreviation");
    	String description = (String) context.get("description");
    	String uomTypeId = (String) context.get("uomTypeId");
    	String isEnabled = (String) context.get("isEnabled");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	String productId = null;
    	try {
        	
    		EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, uomTypeId),
                    EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("uomId")), EntityOperator.EQUALS, currencyCode.toUpperCase())
                    );
			
			GenericValue uom = EntityUtil.getFirst(delegator.findList("Uom", mainCondition, null, Arrays.asList("uomId DESC"), null, false));
			if (UtilValidate.isEmpty(uom)) {
    			result.putAll(ServiceUtil.returnError("Currency not exists!"));
    			return result;
    		}
    		
    		Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
    		
    		uom.put("abbreviation", abbreviation);
    		uom.put("description", description);
    		uom.put("isEnabled", isEnabled);
    		
    		uom.store();
    		
    		result.put("currencyCode", currencyCode);
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated Currency.."));
    	
    	return result;
    	
    }
    
}
