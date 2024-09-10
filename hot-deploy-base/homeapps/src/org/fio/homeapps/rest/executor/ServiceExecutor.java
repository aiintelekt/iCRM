/**
 * 
 */
package org.fio.homeapps.rest.executor;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.fio.homeapps.rest.authentication.AuthHelper;
import org.fio.homeapps.rest.response.EmptyResponse;
import org.fio.homeapps.rest.response.Response;
import org.fio.homeapps.rest.validator.AbstractValidatorFactory;
import org.fio.homeapps.rest.validator.Validator;
import org.fio.homeapps.rest.validator.ValidatorConstants.ValidatorType;
import org.fio.homeapps.rest.validator.ValidatorFactory;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.ResponseUtils;
import org.fio.homeapps.writer.WriterUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceContainer;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Sharif
 *
 */
public class ServiceExecutor extends Executor {
	
	private static ServiceExecutor instance;
	
	public static synchronized ServiceExecutor getInstance(){
        if(instance == null) {
            instance = new ServiceExecutor();
        }
        return instance;
    }

	@Override
	protected Response doExecute(Map<String, Object> context) throws Exception {
	    Timestamp requestedTime = UtilDateTime.nowTimestamp();
	    Timestamp responsedTime = UtilDateTime.nowTimestamp();

	    Delegator delegator = (Delegator) DelegatorFactory.getDelegator("default");
	    LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(), delegator);

	    AbstractValidatorFactory validatorFactory = (AbstractValidatorFactory) context.get("validatorFactory");
	    String validatorType = ParamUtil.getString(context, "validatorType");

	    String restServiceName = ParamUtil.getString(context, "restServiceName");
	    String version = ParamUtil.getString(context, "version");
	    Response response = (Response) context.get("response");
	    Map<String, Object> request = (Map) context.get("request");

	    String authorization = ParamUtil.getString(context, "authorization");
	    MultivaluedMap<String, String> requestHeaders = (MultivaluedMap<String, String>) context.get("requestHeaders");

	    Map<String, Object> serviceContext = (Map<String, Object>) context.get("serviceContext");
	    String serviceName = ParamUtil.getString(context, "serviceName");

	    String jsonReq = ParamUtil.mapToJson(request);
	    String msguid = null;
	    String systemName = null;
	    boolean isError = false;
	    String clientRegistryId = null;
	    String responseCode = null;

	    String isApiKeyAuth = ParamUtil.getString(context, "isApiKeyAuth");
	    if(UtilValidate.isEmpty(isApiKeyAuth))
		isApiKeyAuth = EntityUtilProperties.getPropertyValue("general", "api-key-auth", delegator);
	    
	    String apiUser = null;
	    String apiKey = null;
	    if("Y".equals(isApiKeyAuth)) {
		apiUser = (UtilValidate.isNotEmpty(requestHeaders) && UtilValidate.isNotEmpty(requestHeaders.get("apiUser"))) ? requestHeaders.get("apiUser").get(0) : "";
		apiKey = (UtilValidate.isNotEmpty(requestHeaders) && UtilValidate.isNotEmpty(requestHeaders.get("apiKey"))) ? requestHeaders.get("apiKey").get(0) : "";
		context.put("apiUser", apiUser);
		context.put("apiKey", apiKey);
	    }
	    context.put("isApiKeyAuth", isApiKeyAuth);

	    Validator validator = ValidatorFactory.getValidator(ValidatorType.SERVICE_EXECUTOR_VALIDATOR);
	    Map<String, Object> validatorContext = new HashMap<String, Object>();
	    validatorContext.put("delegator", delegator);
	    validatorContext.put("data", context);

	    Map<String, Object> validatorResponse = validator.validate(validatorContext);
	    validatorResponse.put("request", request);
	    validatorResponse.put("customContext", context.get("customContext"));

	    if (ResponseUtils.isError(validatorResponse)) {
		validatorResponse.put("response_time", UtilDateTime.nowTimestamp());
		response.build(validatorResponse);
		String ofbizApiLogId = WriterUtil.writeLog(delegator, restServiceName, version, (String) request.get("clientRecordRefId"), jsonReq, response, response.getResponse_code(), ResponseUtils.getResponseStatus(response.getResponse_code()), clientRegistryId, requestedTime, responsedTime, msguid, systemName);
		response.setResponse_ref_id(ofbizApiLogId);
		return response;
	    }

	    //////////////////////////////////

	    Debug.log("=========="+restServiceName+" jsonReq========"+jsonReq);

	    Map<String, Object> authResponse = AuthHelper.authenticate(delegator, UtilMisc.toMap("response", response, "jsonReq", jsonReq, "authorization", authorization, "serviceName", restServiceName, "isApiKeyAuth", isApiKeyAuth, "apiUser", apiUser, "apiKey", apiKey));
	    isError = ParamUtil.getBoolean(authResponse, "isError");
	    clientRegistryId = ParamUtil.getString(authResponse, "clientRegistryId");
	    responseCode = (String) authResponse.get("responseCode");

	    if (isError) {
		response = new EmptyResponse();
		response.setResponse_code(responseCode);
		responsedTime = UtilDateTime.nowTimestamp();
		ResponseUtils.prepareResponse(delegator, response, responsedTime);
	    }

	    if (!isError) {
		try {
		    String userId = UtilValidate.isNotEmpty(apiUser) ? apiUser : "system";
		    GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userId).queryOne();

		    msguid = ParamUtil.getString(request, "msguid");
		    systemName = EnumUtil.getEnumId(delegator, ParamUtil.getString(request, "system_name"), "SYSTEM_NAME");
		    requestedTime = UtilValidate.isNotEmpty(request.get("requested_time")) ? ParamUtil.getDateTime(request, "requested_time") : requestedTime;

		    // REQUEST VALIDATION [START]

		    validator = validatorFactory.getValidator(validatorType);
		    validatorContext = new HashMap<String, Object>();
		    validatorContext.put("delegator", delegator);
		    validatorContext.put("data", request);
		    validatorContext.put("context", context); // TODO should be removed
		    validatorContext.put("customContext", context.get("customContext"));

		    validatorResponse = validator.validate(validatorContext);
		    validatorResponse.put("request", request);
		    validatorResponse.put("customContext", context.get("customContext"));

		    // REQUEST VALIDATION [END]

		    if (!ResponseUtils.isError(validatorResponse)) {

			// OFBIZ SERVICE CALL [START]

			if(UtilValidate.isEmpty(serviceContext.get("userLogin")))
			    serviceContext.put("userLogin", userLogin);

			Map<String, Object> res = dispatcher.runSync(serviceName, serviceContext);
			res.put("delegator", delegator);
			res.put("request", request);
			res.put("customContext", context.get("customContext"));

			if (ServiceUtil.isSuccess(res) && (UtilValidate.isEmpty(res.get("result")) || !("error".equals(res.get("result")))) ) {
			    res.put("responseCode", "S200");
			} else if(UtilValidate.isNotEmpty(res.get("result")) && "error".equals(res.get("result"))) {
			    res.put("responseCode", "E1002");
			    res.put("responseCodeDesc", res.get(ModelService.ERROR_MESSAGE));
			}
			else {
			    res.put("responseCode", "E1002");
			    res.put("responseCodeDesc", ServiceUtil.getErrorMessage(res));
			}

			// OFBIZ SERVICE CALL [END]

			responsedTime = UtilDateTime.nowTimestamp();
			res.put("response_time", responsedTime);

			response.build(res);
		    } else {
			responsedTime = UtilDateTime.nowTimestamp();
			validatorResponse.put("response_time", responsedTime);
			response.build(validatorResponse);
		    } 
		} catch (Exception e) {
		    e.printStackTrace();
		    Debug.log("Error in "+restServiceName+" "+e);
		    response.setResponse_code("E1002");
		    response.setResponse_code_desc(e.getMessage());
		    responsedTime = UtilDateTime.nowTimestamp();
		    ResponseUtils.prepareResponse(delegator, response, responsedTime);
		}
	    }

	    String ofbizApiLogId = WriterUtil.writeLog(delegator, restServiceName, version, (String) request.get("clientRecordRefId"), jsonReq, response, response.getResponse_code(), ResponseUtils.getResponseStatus(response.getResponse_code()), clientRegistryId, requestedTime, responsedTime, msguid, systemName);
	    response.setResponse_ref_id(ofbizApiLogId);

	    return response;

	}

}
