package org.groupfio.etl.process.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.groupfio.etl.process.service.GeneralService;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Group Fio
 *
 */
public class CustomServiceImpl implements GeneralService {
	
	private static String MODULE = CustomServiceImpl.class.getName();
	
	public static Map<String, Object> testFilterService(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			
			Delegator delegator = dctx.getDelegator();
			
			Map<String, Object> rowValue = (Map<String, Object>) context.get("rowValue");
			
			String modelName = (String) context.get("modelName");
			String elementName = (String) context.get("elementName");
			
			
			// TODO write the business logic here
			
			
			
			
			response.put("elementValue", "testElementValue001");
			
		} catch (Exception e) {
			Debug.logError("createEtlPreProcessor ERROR: "+e.getMessage(), MODULE);
			return ServiceUtil.returnError(e.toString());
		}
		
		response.putAll( ServiceUtil.returnSuccess() );
		
		return response;
	}
	
}