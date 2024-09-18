package org.fio.homeapps.rest.client;

import java.util.Map;

import org.fio.homeapps.rest.util.ConfigurationParam;
import org.fio.homeapps.util.ParamUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

/**
 * @author Sharif
 */
public class TokenClient extends RestClient {
	
	private static final String MODULE = TokenClient.class.getName();
	
	public TokenClient(Delegator delegator, ConfigurationParam configurationParam, GenericValue channelAccess,
			String accessType) {
		super(delegator, configurationParam, channelAccess, accessType);
	}
	
	public void submitRequest(Map<String, Object> request, Map<String, Object> response) {
    	String accessUrl = channelAccessUrl;
    	
    	if (UtilValidate.isNotEmpty(request.get("accessUrl"))) {
    		accessUrl = ParamUtil.getString(request, "accessUrl");
    	} else {
    		accessUrl += "/v2/token";
    	}
    	
    	request.put("accessUrl", accessUrl);
    	request.put("requestMethod", "token");
    	
    	request.put("isTokenRequest", true);
    	
    	super.submitRequest(request, response);
    }
	
}
