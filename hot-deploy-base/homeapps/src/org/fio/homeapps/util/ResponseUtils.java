/**
 * 
 */
package org.fio.homeapps.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.rest.response.Response;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif
 *
 */
public class ResponseUtils {
	
	private static final String MODULE = ResponseUtils.class.getName();

	public static boolean isSuccess(Map<String, Object> response) {
		if (UtilValidate.isNotEmpty(response) && Integer.valueOf(String.valueOf(response.get(GlobalConstants.RESPONSE_CODE))) == (ResponseCodes.SUCCESS_CODE) ) {
			return true;
		}
		return false;
	}
	
	public static boolean isError(Map<String, Object> response) {
		if (UtilValidate.isNotEmpty(response) && Integer.valueOf(String.valueOf(response.get(GlobalConstants.RESPONSE_CODE))) != (ResponseCodes.SUCCESS_CODE) ) {
			return true;
		}
		return false;
	}
	
	public static String getResponseMessage(Map<String, Object> response) {
		return (String) response.get(GlobalConstants.RESPONSE_MESSAGE);
	}
	
	public static int getResponseCode(Map<String, Object> response) {
		if (UtilValidate.isNotEmpty(response.get(GlobalConstants.RESPONSE_CODE))) {
			return (Integer) response.get(GlobalConstants.RESPONSE_CODE);
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
        if ( UtilValidate.isNotEmpty(responseCode) && (responseCode.equalsIgnoreCase("S200") || responseCode.equalsIgnoreCase("S100") || responseCode.equalsIgnoreCase("S300") || ResponseCodes.HTTP_STATUS_SUCCESS_CODE.containsKey(responseCode)) ) {
            return true;
        }
        return false;
    }
	
	public static void prepareResponse (Delegator delegator, Response response) {
		prepareResponse(delegator, response, null);
	}
	
	public static void prepareResponse (Delegator delegator, Response response, Timestamp responsedTime) {
		try {
			//if (UtilValidate.isEmpty( response.getResponseCodeDesc() )) {
				
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("responseCode", response.getResponse_code());
				context.put("responseCodeDesc", response.getResponse_code_desc());
				context.put("responseRefId", response.getResponse_ref_id());
				context.put("delegator", delegator);
				context.put("response_time", responsedTime);
				
				response.prepareContext(context);
			//}
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
	}
	
	public static String getResponseCodeDesc(Delegator delegator, Map<String, Object> filter) {
		try {
			if (UtilValidate.isNotEmpty(filter) && UtilValidate.isNotEmpty(filter.get("code"))) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
						EntityCondition.makeCondition("errorCodeId", EntityOperator.EQUALS, filter.get("code")),
						EntityCondition.makeCondition("code", EntityOperator.EQUALS, filter.get("code")),
						EntityCondition.makeCondition("codeDescription", EntityOperator.EQUALS, filter.get("code"))
						));
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue entity = EntityQuery.use(delegator).from("OfbizApiErrorCode").where(mainConditon).queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
					return UtilValidate.isNotEmpty(entity.getString("solutionDescription")) ? entity.getString("solutionDescription") : entity.getString("codeDescription");
				}
			}
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		return null;
	}
	
}
