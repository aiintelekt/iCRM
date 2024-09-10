package org.fio.homeapps.rest.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.fio.homeapps.rest.client.ClientConstants.ChannelAccessType;
import org.fio.homeapps.rest.client.model.Token;
import org.fio.homeapps.rest.client.parser.TokenParser;
import org.fio.homeapps.rest.client.request.Authentication;
import org.fio.homeapps.rest.util.ConfigurationParam;
import org.ofbiz.base.conversion.JSONConverters.JSONToMap;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.google.gson.Gson;

/**
 * @author sharif
 *
 */
public class ApiClient {
	
	private static final String MODULE = ApiClient.class.getName();
	
	protected Delegator delegator;
	protected ConfigurationParam configurationParam;
	
    protected String channelAccessUrl;
    protected String apikey;
    protected String password;
    protected String channelAccessId;
    protected String authAccessUrl;
    protected String httpsProtocol;
    
    public String merchantIdentifier;
    protected String channelAccessHost;
    
    protected String applicationName;
    protected String applicationVersion;
    protected String systemName;
    
    protected String accessType;
    protected String accessToken;
    
    public String responsedMessage;
    public int responseCode;
    
    protected String serviceName;
    
    public GenericValue channelAccess;
    
    protected Map<String, Object> responseMap;
    
    protected Timestamp requestedTime;
    protected Timestamp responsedTime;
    
    public boolean isJsonResponse = true;
    
    /**
     * Creates a new <code>ShopifyClient</code> instance.
     *
     * @param apikey a <code>String</code> value
     * @param password a <code>String</code> value
     */
    public ApiClient(Delegator delegator, ConfigurationParam configurationParam, GenericValue channelAccess, String accessType) {
        try {
        	
        	this.delegator = delegator;
        	this.configurationParam = configurationParam;
        	this.channelAccess = channelAccess;
        	
        	apikey = channelAccess.getString("userName");
        	password = channelAccess.getString("password");
        	//applicationName = channelAccess.getString("applicationName");
        	//applicationVersion = channelAccess.getString("applicationVersion");
        	channelAccessUrl = channelAccess.getString("channelAccessUrl");
        	channelAccessId = channelAccess.getString("channelAccessId");
        	accessToken = channelAccess.getString("accessToken");
        	merchantIdentifier = channelAccess.getString("merchantIdentifier");
        	authAccessUrl = channelAccess.getString("authAccessUrl");
        	httpsProtocol = channelAccess.getString("httpsProtocol");
        	
        	if (UtilValidate.isNotEmpty(channelAccessUrl)) {
        		channelAccessHost = channelAccessUrl.replace("http://", "").replace("https://", "");
        	}
        	
        	this.accessType = accessType;
        	
        } catch (Exception e) {
        	Debug.logError(e.getMessage(), MODULE);
        } 
    }
    
    protected String printContent(HttpURLConnection con) {
		if (con != null) {

			try {

				int responseCode = con.getResponseCode();
				System.out.println("Response Code : " + responseCode);
				
				InputStream inputStream = null;
				if (isSuccessResponse(responseCode)) {
					inputStream = con.getInputStream();
				} else {
					inputStream = con.getErrorStream();
				}
				
				//System.out.println("****** Content of the URL ********");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						inputStream));

				String input;
				String response = "";

				while ((input = br.readLine()) != null) {
					response += input;
				}
				br.close();
				
				setResponsedMessage(response);
				setResponseCode(responseCode);
				
				JSON jsonFeed = JSON.from(response);
        		
                JSONToMap jsonMap = new JSONToMap();
                responseMap = jsonMap.convert(jsonFeed);
				
				System.out.println("\tResponse Payload :\n" + response.toString());
				return response;
						
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return null;

	}
    
    protected void refreshToken() {
    	try {
    		ConfigurationParam configurationParam = new ConfigurationParam();
            
            configurationParam.setDelegator(delegator);
            configurationParam.setChannelAccessId(channelAccessId);
    		
			TokenClient client = ClientUtil.getTokenClient(delegator, configurationParam, channelAccessId);
			
			Map<String, Object> clientRequest = new LinkedHashMap<String, Object>();
            Map<String, Object> clientResponse = new LinkedHashMap<String, Object>();
            
            /*AuthenticationMvp authentication = new AuthenticationMvp();
            authentication.setGrant_type(configurationParam.getValue("GRANT_TYPE"));
            authentication.setAssertion(configurationParam.getValue("JWT_TOKEN"));
            
            Gson gson = new Gson();
    		String payload = gson.toJson(authentication);*/
            
            //String payload = "grant_type="+configurationParam.getValue("GRANT_TYPE")+"&assertion="+configurationParam.getValue("JWT_TOKEN");
            String payload = "grant_type=client_credentials";
            String authorization = "Basic "+Base64.base64Encode(client.getApikey()+":"+client.getPassword());
            
    		clientRequest.put("contentType", "application/x-www-form-urlencoded");
    		clientRequest.put("authorization", authorization);
    		clientRequest.put("accessUrl", authAccessUrl);
            clientRequest.put("payload", payload);
            
            client.submitRequest(clientRequest, clientResponse);
			
			Token token = client.getToken();
			if (UtilValidate.isNotEmpty(token)) {
				channelAccess.put("accessToken", token.getAccessToken());
				channelAccess.put("tokenType", token.getTokenType());
				channelAccess.put("refreshToken", token.getRefreshToken());
				
				delegator.store(channelAccess);
				
				accessToken = token.getAccessToken();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /*protected void refreshToken() {
    	try {
    		ConfigurationParam configurationParam = new ConfigurationParam();
            
            configurationParam.setDelegator(delegator);
            configurationParam.setChannelAccessId(ChannelAccessType.ACCESS_TYPE_REST);
    		
			TokenClient client = ClientUtil.getTokenClient(delegator, configurationParam, ChannelAccessType.ACCESS_TYPE_REST);
			
			Map<String, Object> clientRequest = new LinkedHashMap<String, Object>();
            Map<String, Object> clientResponse = new LinkedHashMap<String, Object>();
            
            Authentication authentication = new Authentication.AuthenticationBuilder()
            		.setAccountId(merchantIdentifier)
            		.setClientId(apikey)
            		.setClientSecret(password)
            		.setGrantType("client_credentials")
            		.setScope("email_read email_write email_send")
            		.build();
            
            Gson gson = new Gson();
    		String payload = gson.toJson(authentication);
    		
            clientRequest.put("payload", payload);
            
            client.submitRequest(clientRequest, clientResponse);
			
			Token token = client.getToken();
			if (UtilValidate.isNotEmpty(token)) {
				
				channelAccess.put("accessToken", token.getAccessToken());
				channelAccess.put("tokenType", token.getTokenType());
				
				delegator.store(channelAccess);
				
				accessToken = token.getAccessToken();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }*/
    
    protected Token getToken() {
    	
    	Token token = null;
    	
    	try {
			if (isSuccessResponse(getResponseCode())) {
				
				 String responsePayload = getResponsedMessage();
			     if (UtilValidate.isNotEmpty(responsePayload)) {
			     	JSON jsonFeed = JSON.from(responsePayload);
			        JSONToMap jsonMap = new JSONToMap();
			        Map<String, Object> response = jsonMap.convert(jsonFeed);
			     	token = TokenParser.parseToken(response);
			     }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return token;
    	
    }
    
    protected boolean isSuccessResponse(int responseCode) {
    	this.responseCode = responseCode;
    	if (responseCode >= 200 && responseCode <= 300) {
    		return true;
    	}
    	return false;
    }
    
    public String getResponseStatus() {
    	if (isSuccessResponse(responseCode)) {
    		return "SUCCESS";
    	}
    	return "FAILED";
	}
    
	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public Timestamp getRequestedTime() {
		return requestedTime;
	}

	public void setRequestedTime(Timestamp requestedTime) {
		this.requestedTime = requestedTime;
	}

	public Timestamp getResponsedTime() {
		return responsedTime;
	}

	public void setResponsedTime(Timestamp responsedTime) {
		this.responsedTime = responsedTime;
	}

	public String getResponsedMessage() {
		return responsedMessage;
	}

	public void setResponsedMessage(String responsedMessage) {
		this.responsedMessage = responsedMessage;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	
	public String getApikey() {
		return apikey;
	}

	public String getPassword() {
		return password;
	}

	protected String printContent(HttpsURLConnection con) {
		if (con != null) {

			try {

				int responseCode = con.getResponseCode();
				System.out.println("Response Code : " + responseCode);
				
				InputStream inputStream = null;
				if (isSuccessResponse(responseCode)) {
					inputStream = con.getInputStream();
				} else {
					inputStream = con.getErrorStream();
				}
				
				//System.out.println("****** Content of the URL ********");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						inputStream));

				String input;
				String response = "";

				while ((input = br.readLine()) != null) {
					response += input;
				}
				br.close();
				
				setResponsedMessage(response);
				setResponseCode(responseCode);
				
				if (isJsonResponse) {
					JSON jsonFeed = JSON.from(response);
	        		
	                JSONToMap jsonMap = new JSONToMap();
	                responseMap = jsonMap.convert(jsonFeed);
				}
				
				System.out.println("\tResponse Payload :\n" + response.toString());
				return response;
						
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return null;

	}
	
}
