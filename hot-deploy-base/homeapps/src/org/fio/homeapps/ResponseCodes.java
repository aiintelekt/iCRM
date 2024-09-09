/**
 * 
 */
package org.fio.homeapps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sharif
 *
 */
public class ResponseCodes {
	/**
	 * 
	 */
	public static final int SUCCESS_CODE = 200;
	public static final String SUCCESS_MESSAGE ="Your request done successfully.";
	/**
	 * 
	 */
	public static final int SERVICE_UNAVAILABLE_CODE = 503;
	public static final String SERVICE_UNAVAILABLE_MESSAGE ="The request failed due to a connection error.";
	/**
	 * 
	 */
	public static final int INTERNAL_SERVER_ERROR_CODE = 500;
	public static final String INTERNAL_SERVER_ERROR_MESSAGE ="The request failed due to an internal error.";
	/**
	 * 
	 */
	public static final int EXPECTATION_FAILED_CODE = 417;
	public static final String EXPECTATION_FAILED_MESSAGE ="A client expectation cannot be met by the server.";
	/**
	 * 
	 */
	public static final int PRECONDITION_FAILED_CODE = 412;
	public static final String PRECONDITION_FAILED_MESSAGE ="Condition Not Met";
	/**
	 * 
	 */
	public static final int CONFLICT_CODE = 409;
	public static final String CONFLICT_MESSAGE ="The requested operation failed because it tried to create a resource that already exists.";
	/**
	 * 
	 */
	public static final int UNAUTHORIZED_CODE = 401;
	public static final String UNAUTHORIZED_MESSAGE ="You do not have read permission.";
	
	/**
	 * 
	 */
	public static final int UNAUTHORIZED_CODE_01 = 4011;
	public static final String UNAUTHORIZED_MESSAGE_01 ="You do not have read permission.";
	/**
	 * 
	 */
	public static final int UNAUTHORIZED_CODE_02 = 4012;
	public static final String UNAUTHORIZED_MESSAGE_02 ="You do not have write permission.";
	/**
	 * 
	 */
	public static final int UNAUTHORIZED_CODE_03 = 4013;
	public static final String UNAUTHORIZED_MESSAGE_03 ="You do not have admin permission.";
	/**
	 * 
	 */
	public static final int UNPROCESSABLE_REQUEST_CODE = 422;
	public static final String UNPROCESSABLE_REQUEST_MESSAGE="Unprocessable Request";
	/**
	 * 
	 */
	public static final int NO_AUTHORIZER_FOUND_CODE = 8888;
	public static final String NO_AUTHORIZER_FOUND_MESSAGE="Can not be shared because No Authorizer is available to approve";
	/**
	 * 
	 */
	public static final int INVALID_SUBSCRIPTION_CODE = 5000;
	public static final String INVALID_SUBSCRIPTION_CODE_MESSAGE="Invalid subscription";
	/**
	 * 
	 */
	public static final int NOT_FOUND_CODE=404;
	
	public static final int BAD_REQUEST = 400;
	
	public static final Map<String, String> HTTP_STATUS_SUCCESS_CODE = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("200", "200");
	    	        put("201", "201");
	    	        put("204", "204");
	    	        put("202", "202");
	   		 }});

}
