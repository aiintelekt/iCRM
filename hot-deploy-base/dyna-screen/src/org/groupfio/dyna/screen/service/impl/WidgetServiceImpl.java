/**
 * 
 */
package org.groupfio.dyna.screen.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.util.DataHelper;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.dyna.screen.DynaScreenConstants.LayoutType;
import org.groupfio.dyna.screen.DynaScreenConstants.LookupType;
import org.groupfio.dyna.screen.filter.FilterDynaField;
import org.groupfio.dyna.screen.filter.FilterDynaFieldData;
import org.groupfio.dyna.screen.util.DynaScreenUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class WidgetServiceImpl {

	private static final String MODULE = WidgetServiceImpl.class.getName();
    
    public static Map createDynaScreenStep1(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Security security = dctx.getSecurity();
    	
    	String dynaConfigId = (String) context.get("dynaConfigId");
    	Map requestContext = (Map) context.get("requestContext");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
        	
    		GenericValue dynaScreenConfig = EntityUtil.getFirst( delegator.findByAnd("DynaScreenConfig", UtilMisc.toMap("dynaConfigId", dynaConfigId), null, false) );
    		
    		if (UtilValidate.isEmpty(dynaScreenConfig)) {
    			result.putAll(ServiceUtil.returnError("Dyna screen config not exists!"));
    			return result;
    		}
    		
    		
    		
    		//result.put("screenConfig", screenConfig);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully retrieve Dyna Screen Render Detail.."));
    	
    	return result;
    	
    }
    
}
