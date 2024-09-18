package org.fio.homeapps.rest.validator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class AccessTokenValidator extends Validator {

	private static String MODULE = AccessTokenValidator.class.getName();
	
	private static AccessTokenValidator instance;
	
	public static synchronized AccessTokenValidator getInstance(){
        if(instance == null) {
            instance = new AccessTokenValidator();
        }
        return instance;
    }
	
	@Override
	public Map<String, Object> doValidate(Map<String, Object> context) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		
		try {
			Delegator delegator = (Delegator) context.get("delegator");
			response.put("delegator", delegator);
			
			//String modelName = ParamUtil.getString(context, "modelName");
			List<String> authorization = (List<String>) context.get("authorization");
			boolean isValidate = true;
			String errorSummary = "";
            if (UtilValidate.isEmpty(authorization)) {
                isValidate = false;
                errorSummary = "E108";
            } else {
                if (!authorization.get(0).startsWith("Bearer ")) {
                    isValidate = false;
                    errorSummary = "E101";
                } else {
                    String encodeString = authorization.get(0);
                    encodeString = encodeString.substring(7);
                    String secretCode = Base64.base64Decode(encodeString);
                    //System.out.println("decodedStr: "+decodedStr);
                    GenericValue appStatus = EntityUtil.getFirst( delegator.findByAnd("ClientApplicationStatus", UtilMisc.toMap("secretCode", secretCode), null, false) );
                    if (UtilValidate.isEmpty(appStatus)) {
                        isValidate = false;
                        errorSummary = "E101";
                    } else if (!UtilValidate.isDateAfterNow(appStatus.getTimestamp("thruDate"))) {
                        isValidate = false;
                        errorSummary = "E100";
                    } else {
                        response.put("clientRegistryId", appStatus.get("clientRegistryId"));
                    }
                }
            }
			if (!isValidate) {
				response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(GlobalConstants.RESPONSE_MESSAGE, errorSummary);
				return response;
			}
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			
			response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(GlobalConstants.RESPONSE_MESSAGE, "E101");
			return response;
		}
		response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return response;
	}

}

