package org.fio.homeapps.rest.validator;

import java.util.LinkedHashMap;
import java.util.Map;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.rest.response.Response;
import org.fio.homeapps.rest.validator.Validator;
import org.fio.homeapps.util.ParamUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

/**
 * @author Sharif
 *
 */
public class ServiceExecutorDataValidator extends Validator {

	private static String MODULE = ServiceExecutorDataValidator.class.getName();
	
	private static ServiceExecutorDataValidator instance;
	
	public static synchronized ServiceExecutorDataValidator getInstance(){
        if(instance == null) {
            instance = new ServiceExecutorDataValidator();
        }
        return instance;
    }
	
	@Override
	public Map<String, Object> doValidate(Map<String, Object> context) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		Map<String, Object> data = (Map<String, Object>) context.get("data");
		Map<String, Object> validationMessage = new LinkedHashMap<String, Object>();
		
		try {
			boolean validate = true;
			
			Delegator delegator = (Delegator) context.get("delegator");
			response.put("delegator", delegator);
			
			String message = null;
			
			AbstractValidatorFactory validatorFactory = (AbstractValidatorFactory) data.get("validatorFactory");
			String validatorType = ParamUtil.getString(data, "validatorType");
			
			String restServiceName = ParamUtil.getString(data, "restServiceName");
			Response restResponse = (Response) data.get("response");
			Map<String, Object> request = (Map) data.get("request");
			
			String authorization = ParamUtil.getString(data, "authorization");
			
			Map<String, Object> serviceContext = (Map<String, Object>) data.get("serviceContext");
			String serviceName = ParamUtil.getString(data, "serviceName");
			
			String isApiKeyAuth = ParamUtil.getString(data, "isApiKeyAuth");
			
			if (UtilValidate.isEmpty(validatorFactory)) {
				validate = false;
				message = "E1002";
				validationMessage.put("validatorFactory", message);
			}
			
			if (UtilValidate.isEmpty(validatorType)) {
				validate = false;
				message = "E1003";
				validationMessage.put("validatorType", message);
			}
			
			if (UtilValidate.isEmpty(restServiceName)) {
				validate = false;
				message = "E1004";
				validationMessage.put("restServiceName", message);
			}
			
			if (UtilValidate.isEmpty(restResponse)) {
				validate = false;
				message = "E1005";
				validationMessage.put("restResponse", message);
			}
			
			if (UtilValidate.isEmpty(serviceContext)) {
				validate = false;
				message = "E1006";
				validationMessage.put("serviceContext", message);
			}
			
			if (UtilValidate.isEmpty(serviceName)) {
				validate = false;
				message = "E1007";
				validationMessage.put("serviceName", message);
			}
			
			if(UtilValidate.isNotEmpty(isApiKeyAuth) && "Y".equals(isApiKeyAuth)) {
				String apiUser = (String) data.get("apiUser");
				if (UtilValidate.isEmpty(apiUser)) {
					validate = false;
					message = "E1002";
					validationMessage.put("apiUser", message);
				}
				String apiKey = (String) data.get("apiKey");
				if (UtilValidate.isEmpty(apiKey)) {
					validate = false;
					message = "E1002";
					validationMessage.put("apiKey", message);
				}
			}
			
			if (!validate) {
				response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(GlobalConstants.RESPONSE_MESSAGE, "ServiceExecutor Data Validation Failed...!");
			} else {
				response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
			
			response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(GlobalConstants.RESPONSE_MESSAGE, "ServiceExecutor Data Validation Failed...!");
			
			return response;
		}
		response.put("validationMessage", validationMessage);
		return response;
	}
	
}

