package org.groupfio.etl.process.client;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.client.parser.WalletParser;
import org.groupfio.etl.process.client.response.WalletAccount;
import org.json.simple.parser.JSONParser;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

/**
 * @author Group Fio
 * Client for ewallet, based on REST service.
 */
public class WalletWalletClient extends WalletClient {

	private static final String MODULE = WalletWalletClient.class.getName();

	private String partyId;
	private String emailId;
	
	public WalletWalletClient(Delegator delegator, GenericValue channelAccess,
			String accessType) {
		super(delegator, channelAccess, accessType);
	}
	
	public Map<String, Object> createWalletAccount(String jsonRequest) {
    	
		Map<String, Object> res = new HashMap<String, Object>();
		
    	String https_url = channelAccessUrl + "/createWalletAccount";
		String authStr = accessToken;
		String authStringEnc = Base64.base64Encode(authStr);
		//String authStringEnc = new String(Base64.encode(authStr));
		
		WalletAccount walletAccount = null;
		URL url;
		String responseJson = null;
		try {
			
			setRequestedTime(UtilDateTime.nowTimestamp());
			
			HttpURLConnection con = null;
			
			url = new URL(https_url);
			con = (HttpURLConnection) url.openConnection();
			
			con.setRequestProperty("Authorization", "Bearer " + authStringEnc);
			con.setRequestMethod("POST");
			con.setRequestProperty("user-agent","Mozilla/5.0");
			con.setRequestProperty("Content-Type","application/json");
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(jsonRequest);
			wr.flush();
			wr.close();

			// dump all the content
			responseJson = printContent(con);
			
			JSONParser jsonParser = new JSONParser();
			org.json.simple.JSONObject response = (org.json.simple.JSONObject) jsonParser.parse(responseJson);
			
			setResponsedTime(UtilDateTime.nowTimestamp());
			
			if (isSuccessResponse(con.getResponseCode())) {
				
				walletAccount = WalletParser.parseWalletAccount(response);
				
				res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				res.put("walletAccount", walletAccount);
			} else {
				responseJson = (String) response.get("responseCode");
				Debug.logError("getToken ERROR: "+responseJson, MODULE);
			}
			
			res.put(EtlConstants.RESPONSE_MESSAGE, responseJson);
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError("getToken ERROR: "+e.getMessage(), MODULE);
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, "createWalletAccount Failed...! "+e.getMessage());
		}
    	
		return res;
    }
	
	public Map<String, Object> expireWalletAccount(String jsonRequest) {
    	
		Map<String, Object> res = new HashMap<String, Object>();
		
    	String https_url = channelAccessUrl + "/expireWalletAccount";
		String authStr = accessToken;
		String authStringEnc = Base64.base64Encode(authStr);
		//String authStringEnc = new String(Base64.encode(authStr));
		
		WalletAccount walletAccount = null;
		URL url;
		String responseJson = null;
		try {
			
			setRequestedTime(UtilDateTime.nowTimestamp());
			
			HttpURLConnection con = null;
			
			url = new URL(https_url);
			con = (HttpURLConnection) url.openConnection();
			
			con.setRequestProperty("Authorization", "Bearer " + authStringEnc);
			con.setRequestMethod("POST");
			con.setRequestProperty("user-agent","Mozilla/5.0");
			con.setRequestProperty("Content-Type","application/json");
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(jsonRequest);
			wr.flush();
			wr.close();

			// dump all the content
			responseJson = printContent(con);
			
			JSONParser jsonParser = new JSONParser();
			org.json.simple.JSONObject response = (org.json.simple.JSONObject) jsonParser.parse(responseJson);
			
			setResponsedTime(UtilDateTime.nowTimestamp());
			
			if (isSuccessResponse(con.getResponseCode())) {
				
				walletAccount = WalletParser.parseWalletAccount(response);
				
				res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				res.put("walletAccount", walletAccount);
			} else {
				responseJson = (String) response.get("responseCode");
				Debug.logError("getToken ERROR: "+responseJson, MODULE);
			}
			
			res.put(EtlConstants.RESPONSE_MESSAGE, responseJson);
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError("getToken ERROR: "+e.getMessage(), MODULE);
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, "expireWalletAccount Failed...! "+e.getMessage());
		}
    	
		return res;
    }

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
}
