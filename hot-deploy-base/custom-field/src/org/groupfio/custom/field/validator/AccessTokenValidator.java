package org.groupfio.custom.field.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.custom.field.ResponseCodes;
import org.groupfio.custom.field.constants.CustomFieldConstants;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class AccessTokenValidator implements Validator {

	private static String MODULE = AccessTokenValidator.class.getName();
	
	/* (non-Javadoc)
	 * @see org.groupfio.etl.process.validator.Validator#validate(java.util.Map)
	 */
	@Override
	public Map<String, Object> validate(Map<String, Object> context) {
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			
			Delegator delegator = (Delegator) context.get("delegator");
			response.put("delegator", delegator);
			
			//String modelName = ParamUtil.getString(context, "modelName");
			List<String> authorization = (List<String>) context.get("authorization");
			
			boolean isValidate = true;
			String errorSummary = "";
			
			if (UtilValidate.isEmpty(authorization) || !authorization.get(0).startsWith("Bearer ")) {
				
				isValidate = false;
				errorSummary = "E108";
				
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
			
			if (!isValidate) {
				
				response.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(CustomFieldConstants.RESPONSE_MESSAGE, errorSummary);
				
				return response;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
			
			response.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(CustomFieldConstants.RESPONSE_MESSAGE, "E108");
			
			return response;
		}
		
		response.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;
	}

}
