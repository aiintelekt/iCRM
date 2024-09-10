/**
 * 
 */
package org.groupfio.etl.process.util;

import java.util.Map;

import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.ofbiz.base.util.UtilValidate;

/**
 * @author Group Fio
 *
 */
public class ResponseUtils {

	public static boolean isSuccess(Map<String, Object> response) {
		if (Integer.valueOf(String.valueOf(response.get(EtlConstants.RESPONSE_CODE))) == (ResponseCodes.SUCCESS_CODE) ) {
			return true;
		}
		return false;
	}
	
	public static boolean isError(Map<String, Object> response) {
		if (Integer.valueOf(String.valueOf(response.get(EtlConstants.RESPONSE_CODE))) != (ResponseCodes.SUCCESS_CODE) ) {
			return true;
		}
		return false;
	}
	
	public static String getResponseMessage(Map<String, Object> response) {
		return (String) response.get(EtlConstants.RESPONSE_MESSAGE);
	}
	
	public static int getResponseCode(Map<String, Object> response) {
		if (UtilValidate.isNotEmpty(response.get(EtlConstants.RESPONSE_CODE))) {
			return (Integer) response.get(EtlConstants.RESPONSE_CODE);
		}
		return 0;
	}
	
}
