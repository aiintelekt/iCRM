/**
 * 
 */
package org.groupfio.etl.process.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.sql.Timestamp;

import org.groupfio.etl.process.client.ClientConstants.ChannelAccessType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Group Fio
 *
 */
public class WalletClient {
	
	private static final String MODULE = WalletClient.class.getName();
	
	protected Delegator delegator;
	
    protected String channelAccessUrl;
    protected String apikey;
    protected String password;
    protected String applicationName;
    protected String applicationVersion;
    protected String channelAccessId;
    protected String clientName;
    
    protected String accessType;
    protected String accessToken;
    
    public String responsedMessage;
    public int responseCode;
    protected Timestamp requestedTime;
    protected Timestamp responsedTime;
    
    /**
     * Creates a new <code>WalletClient</code> instance.
     *
     * @param apikey a <code>String</code> value
     * @param password a <code>String</code> value
     */
    public WalletClient(Delegator delegator, GenericValue channelAccess, String accessType) {
        try {
        	
        	this.delegator = delegator;
        	
        	apikey = channelAccess.getString("apiKey");
        	password = channelAccess.getString("password");
        	applicationName = channelAccess.getString("applicationName");
        	applicationVersion = channelAccess.getString("applicationVersion");
        	channelAccessUrl = channelAccess.getString("channelAccessUrl");
        	channelAccessId = channelAccess.getString("channelAccessId");
        	accessToken = channelAccess.getString("accessToken");
        	
        	this.accessType = accessType;
        	
        	if (UtilValidate.isNotEmpty(accessType) && accessType.equals( ChannelAccessType.ACCESS_TYPE_PARTY )){
        		channelAccessUrl = channelAccessUrl.concat( "/party" );
        	} else if (UtilValidate.isNotEmpty(accessType) && accessType.equals( ChannelAccessType.ACCESS_TYPE_WALLET )){
        		channelAccessUrl = channelAccessUrl.concat( "/wallet" );
        	}
        	
        } catch (Exception e) {
            Debug.logError(e, MODULE);
        } 
    }
    
    protected String printContent(HttpURLConnection con) {
		if (con != null) {
			try {

				int responseCode = con.getResponseCode();
				Debug.log("Response Code : " + responseCode);
				
				InputStream inputStream = null;
				if (isSuccessResponse(responseCode)) {
					inputStream = con.getInputStream();
				} else {
					inputStream = con.getErrorStream();
				}
				
				Debug.log("****** Content of the URL ********");
				try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))){

				String input;
				String response = "";

				while ((input = br.readLine()) != null) {
					Debug.log(input);
					response += input;
				}
				//br.close();
				
				setResponsedMessage(response);
				
				return response;
				}catch(Exception e1){
					Debug.log("Error in printContent"+e1.getMessage());
				}
						
			} catch (IOException e) {
				Debug.log("Exception in printContent==="+e.getMessage());
			}
			
		}
		
		return null;

	}
    
    public static GenericValue getWalletChannelAccess (Delegator delegator) {
    	
    	try {
			GenericValue channelAccess = EntityUtil.getFirst( delegator.findByAnd("ChannelAccess", UtilMisc.toMap("channelAccessType", "WALLET"), null, false) );
			return channelAccess;
		} catch (Exception e) {
			Debug.log("Exception in getWalletChannelAccess======"+e.getMessage());
		}
    	
    	return null;
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

	public Delegator getDelegator() {
		return delegator;
	}

	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}

	public String getChannelAccessUrl() {
		return channelAccessUrl;
	}

	public void setChannelAccessUrl(String channelAccessUrl) {
		this.channelAccessUrl = channelAccessUrl;
	}

	public String getApikey() {
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	public String getChannelAccessId() {
		return channelAccessId;
	}

	public void setChannelAccessId(String channelAccessId) {
		this.channelAccessId = channelAccessId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
}
