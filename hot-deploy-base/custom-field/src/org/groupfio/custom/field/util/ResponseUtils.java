/**
 * 
 */
package org.groupfio.custom.field.util;

import java.util.HashMap;
import java.util.Map;

import org.groupfio.custom.field.ResponseCodes;
import org.groupfio.custom.field.constants.CustomFieldConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

/**
 * @author Sharif
 *
 */
public class ResponseUtils {
	
	private static final String MODULE = ResponseUtils.class.getName();

	public static boolean isSuccess(Map<String, Object> response) {
		if (Integer.valueOf(String.valueOf(response.get(CustomFieldConstants.RESPONSE_CODE))) == (ResponseCodes.SUCCESS_CODE) ) {
			return true;
		}
		return false;
	}
	
	public static boolean isError(Map<String, Object> response) {
		if (Integer.valueOf(String.valueOf(response.get(CustomFieldConstants.RESPONSE_CODE))) != (ResponseCodes.SUCCESS_CODE) ) {
			return true;
		}
		return false;
	}
	
	public static String getResponseMessage(Map<String, Object> response) {
		return (String) response.get(CustomFieldConstants.RESPONSE_MESSAGE);
	}
	
	public static int getResponseCode(Map<String, Object> response) {
		if (UtilValidate.isNotEmpty(response.get(CustomFieldConstants.RESPONSE_CODE))) {
			return (Integer) response.get(CustomFieldConstants.RESPONSE_CODE);
		}
		return 0;
	}
	
	public static String getResponseStatus(String responseCode) {
    	if (isSuccessResponse(responseCode)) {
    		return "SUCCESS";
    	}
    	return "FAILED";
	}
	
	public static boolean isSuccessResponse(String responseCode) {
    	if (UtilValidate.isNotEmpty(responseCode) && responseCode.equals("S200")) {
    		return true;
    	}
    	return false;
    }
	
}
