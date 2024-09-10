package org.groupfio.custom.field.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;

/**
 * @author Sharif
 *
 */
public class ParamUtil {

	public static long getLong (Map<String, Object> context, String key) {
		if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
			return Long.valueOf(""+context.get(key));
		}
		return 0;
	}
	
	public static BigDecimal getBigDecimal (Map<String, Object> context, String key) {
		if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
			return BigDecimal.valueOf(Double.valueOf(""+context.get(key)));
		}
		return null;
	}
	
	public static boolean getBoolean (Map<String, Object> context, String key) {
		if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
			return Boolean.valueOf(""+context.get(key));
		}
		return false;
	}
	
	public static String getString (Map<String, Object> context, String key) {
		if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
			return String.valueOf(""+context.get(key));
		}
		return null;
	}
	
	public static int getInteger (Map<String, Object> context, String key) {
		if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
			return Integer.valueOf(""+context.get(key));
		}
		return 0;
	}
	
	public static Map<String, Object> prepareObjectParams (Map<String, Object> context) {
		Map<String, Object> params = new HashMap<String, Object>();
		
		for (String key : context.keySet()) {
			if (UtilValidate.isNotEmpty(context.get(key))) {
				params.put(key, context.get(key));
			}
		}
		return params;
	} 
	
	public static Map<String, String> prepareStringParams (Map<String, String> context) {
		Map<String, String> params = new HashMap<String, String>();
		
		for (String key : context.keySet()) {
			if (UtilValidate.isNotEmpty(context.get(key))) {
				params.put(key, context.get(key));
			}
		}
		return params;
	} 
	
	public static String getParameterName (String paramName) {
		
		if (UtilValidate.isNotEmpty(paramName)) {
			return paramName.replaceFirst("etl_param_", "");
		}
		
		return null;
	}
	
}
