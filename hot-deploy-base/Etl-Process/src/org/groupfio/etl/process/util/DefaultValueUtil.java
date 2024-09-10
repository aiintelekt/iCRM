/**
 * 
 */
package org.groupfio.etl.process.util;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.EtlConstants.DelimiterValue;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class DefaultValueUtil {

	public static String getModelDefaultValue (String modelName, String propertyName, Delegator delegator) {
		
		try {
			
			GenericValue modelDefault = EntityUtil.getFirst( delegator.findByAnd("EtlModelDefaults", UtilMisc.toMap("modelName", modelName, "propertyName", propertyName), null, false) );
			if (UtilValidate.isNotEmpty(modelDefault)) {
				return modelDefault.getString("propertyValue");
			}
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getModelElementDefaultValue (String modelName, String elementName, String propertyName, Delegator delegator) {
		
		try {
			
			GenericValue elementDefault = EntityUtil.getFirst( delegator.findByAnd("EtlModelElementDefault", UtilMisc.toMap("modelName", modelName, "elementName", elementName, "propertyName", propertyName), null, false) );
			if (UtilValidate.isNotEmpty(elementDefault)) {
				return elementDefault.getString("propertyValue");
			}
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getTextDelimiterValue (String modelName, Delegator delegator) {
		return DelimiterValue.valueOf( getTextDelimiter(modelName, delegator) ).getValue();
	}
	
	public static String getTextDelimiter (String modelName, Delegator delegator) {
		String delimiter = getModelDefaultValue(modelName, "delimiter", delegator);
		return UtilValidate.isNotEmpty(delimiter) ? delimiter : EtlConstants.DEFAULT_DELIMITER;
	}
	
	public static boolean validateTotalCount (Delegator delegator, long parseCount, String modelName) {
		String recordCount = getModelDefaultValue(modelName, "recordCount", delegator);
		if (!StringUtils.isEmpty(recordCount) && recordCount.length() > 0) {
			if (parseCount > Integer.parseInt(recordCount)) {
				return false;
			}
		}
		/*if (!StringUtils.isEmpty(recordCount) && recordCount.length() > 0) {
			if (Integer.parseInt(recordCount) == parseCount) {
				return true;
			}
		}*/
		return true;
	}
	
	public static void storeModelElementDefaultValues(HttpServletRequest request, String modelName, String elementName) throws Exception {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		
		Enumeration params = request.getParameterNames();
    	
    	while(params.hasMoreElements()) {
    		String paramName = (String) params.nextElement();
    		String paramValue = request.getParameter(paramName);
    		//System.out.println("Parameter Name - "+paramName+", Value - "+paramValue);
    		Debug.log("Parameter Name - "+paramName+", Value - "+paramValue);
    		
    		if (UtilValidate.isNotEmpty(paramName) && paramName.startsWith("etl_param")) {
    			
    			String propertyName = ParamUtil.getParameterName(paramName);
    			
    			GenericValue modelElementDefault = EntityUtil.getFirst( delegator.findByAnd("EtlModelElementDefault", UtilMisc.toMap("modelName", modelName, "elementName", elementName, "propertyName", propertyName), null, false) );
    			
    			if (UtilValidate.isNotEmpty(modelElementDefault)) {
    				
    				modelElementDefault.put("propertyValue", paramValue);
    				modelElementDefault.store();
    				
    			} else {
    				
    				modelElementDefault = delegator.makeValue("EtlModelElementDefault", UtilMisc.toMap("modelName", modelName, "elementName", elementName, "propertyName", propertyName));
    				modelElementDefault.put("propertyValue", paramValue);
    				modelElementDefault.create();
    			}
    			
    		}
    		
    	}
	}
	
}
