/**
 * 
 */
package org.fio.homeapps.rest.authentication;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.rest.response.Response;
import org.fio.homeapps.rest.validator.Validator;
import org.fio.homeapps.rest.validator.ValidatorConstants.ValidatorType;
import org.fio.homeapps.rest.validator.ValidatorFactory;
import org.fio.homeapps.util.AppUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.ResponseUtils;
import org.fio.homeapps.util.UtilAuth;
import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.util.EntityCrypto;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;

/**
 * @author Sharif
 *
 */
public class AuthHelper {

	private static final String MODULE = AuthHelper.class.getName();
	
	public static Map<String, Object> authenticate(Delegator delegator, Map<String, Object> authContext) {
		Map<String, Object> authResponse = new LinkedHashMap<String, Object>();
		
		boolean isError = false;
		String clientRegistryId = null;
		Response response = (Response) authContext.get("response");
		
		try {
			Object jsonReq = (Object) authContext.get("jsonReq");
			
			String authorization = ParamUtil.getString(authContext, "authorization");
			String serviceName = ParamUtil.getString(authContext, "serviceName");
			
			String isApiKeyAuth = ParamUtil.getString(authContext, "isApiKeyAuth");
			String apiUser = ParamUtil.getString(authContext, "apiUser");
			String apiKey = ParamUtil.getString(authContext, "apiKey");
			
			if(UtilValidate.isNotEmpty(isApiKeyAuth) && "Y".equals(isApiKeyAuth)) {
				boolean isValidUser = UtilAuth.apiUserValidation(delegator, apiUser, apiKey);
				if(!isValidUser) {
					response.setResponse_code("E101");
					isError = true;
				}
			} else if (AppUtil.isServiceRestricted(delegator, serviceName)) {
				
				Validator tokenValidator = ValidatorFactory.getValidator(ValidatorType.ACCESS_TOKEN_VALIDATOR);
				
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				
				validatorContext.put("delegator", delegator);
				validatorContext.put("authorization", authorization);
				
				Map<String, Object> validatorResponse = tokenValidator.validate(validatorContext);
				
				if (ResponseUtils.isError(validatorResponse)) {
					response.setResponse_code(ParamUtil.getString(validatorResponse, GlobalConstants.RESPONSE_MESSAGE));
					//response.setResponse_code_desc(ResponseUtils.getResponseCodeDesc(delegator, UtilMisc.toMap("code", response.getResponse_code())));
					isError = true;
				}
				clientRegistryId = (String) validatorResponse.get("clientRegistryId");
			}
			
			if (UtilValidate.isEmpty(jsonReq)) {
				response.setResponse_code("E1000");
				//response.setResponse_code_desc(ResponseUtils.getResponseCodeDesc(delegator, UtilMisc.toMap("code", "E1000")));
				isError = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		authResponse.put("isError", isError);
		authResponse.put("clientRegistryId", clientRegistryId);
		authResponse.put("response", response);
		authResponse.put("responseCode", response.getResponse_code());
		return authResponse;
	}
	
}
