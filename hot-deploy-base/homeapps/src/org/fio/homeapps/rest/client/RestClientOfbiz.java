package org.fio.homeapps.rest.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.rest.util.ConfigurationParam;
import org.fio.homeapps.rest.util.RestUtil;
import org.fio.homeapps.util.LogUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.writer.WriterUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import org.ofbiz.base.util.HttpClient;

/**
 * @author Sharif Ul Islam
 */
public class RestClientOfbiz extends ApiClient implements javax.net.ssl.X509TrustManager {
	
	private static final String MODULE = RestClient.class.getName();
	
	private int countToGenerateAccessToken = 0;
	public String  payload = null;
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	public RestClientOfbiz(Delegator delegator, ConfigurationParam configurationParam, GenericValue channelAccess,
			String accessType) {
		super(delegator, configurationParam, channelAccess, accessType);
	}
	
	public void submitRequest(Map<String, Object> request, Map<String, Object> response) {
    	String accessUrl = ParamUtil.getString(request, "accessUrl");
    	String requestMethod = ParamUtil.getString(request, "requestMethod"); 
    	String assocId = ParamUtil.getString(request, "assocId"); 
    	String httpMethod = ParamUtil.getString(request, "httpMethod"); 
    	String contentType = ParamUtil.getString(request, "contentType"); 
    	String authorization = ParamUtil.getString(request, "authorization");
    	String doRefreshToken = ParamUtil.getString(request, "doRefreshToken");
    	
    	//List<NameValuePair> parameters = (List<NameValuePair>) request.get("parameters");
    	Map<String, Object> parameters = (Map<String, Object>) request.get("parameters");
    	
    	Map<String, Object> authParams = (Map<String, Object>) request.get("authParams");
    	
		URL url;
		String responseJson = null;
		
		try {
			payload = (String) request.get("payload");
			//System.out.println("\tRequest Payload :\n" + payload.toString());
			
			if (UtilValidate.isEmpty(httpMethod)) {
				httpMethod = "POST";
			}
			if (UtilValidate.isEmpty(contentType)) {
				contentType = "application/json";
			}
			
			Boolean isTokenRequest = ParamUtil.getBoolean(request, "isTokenRequest");
			Boolean isNoAuthRequest = ParamUtil.getBoolean(request, "isNoAuthRequest");
			
			setRequestedTime(UtilDateTime.nowTimestamp());
			
			//URI uri = new URIBuilder(accessUrl).build();

			/*HttpRequestBase httpRequest = null;
			
			if (UtilValidate.isNotEmpty(httpMethod) && httpMethod.equals("DELETE")) {
				httpRequest = new HttpDelete(accessUrl);
			} else if (UtilValidate.isNotEmpty(httpMethod) && httpMethod.equals("GET")) {
				httpRequest = new HttpGet(accessUrl);
			} else {
				httpRequest = new HttpPost(accessUrl);
				
				// set body
				if (UtilValidate.isNotEmpty(getPayload())) {
					StringEntity body = new StringEntity(getPayload().toString());
					ParamUtil.setFieldValue(httpRequest, "entity", body);
					//httpRequest.setEntity(body);
				}
				
				if (UtilValidate.isNotEmpty(parameters)) {
					ParamUtil.setFieldValue(httpRequest, "entity", new UrlEncodedFormEntity(parameters,"utf-8"));
					//httpRequest.setEntity(new UrlEncodedFormEntity(parameters,"utf-8"));
				}
			}
			
			//CloseableHttpClient httpClient = HttpClients.custom().build();
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			// set headers
			httpRequest.setHeader("Content-Type", contentType);
			if (UtilValidate.isNotEmpty(authorization)) {
				httpRequest.setHeader("Authorization", authorization);
			}
			
			if ( ((UtilValidate.isEmpty(isTokenRequest) || !isTokenRequest) && !isNoAuthRequest) && UtilValidate.isNotEmpty(accessToken)) {
				httpRequest.setHeader("Authorization", "Bearer " + accessToken);
			}
			httpRequest.setHeader("Accept", "*//*");
			
			if (UtilValidate.isNotEmpty(authParams)) {
				for (String authKey : authParams.keySet()) {
					String authValue = (String) authParams.get(authKey);
					if (UtilValidate.isNotEmpty(authValue)) {
						httpRequest.setHeader(authKey, authValue);
					}
				}
			}
			
			// execute request
			//CloseableHttpResponse httpClientResponse = httpClient.execute(httpPost);
			//HttpResponse httpClientResponse = httpClient.execute(httpPost);
			
			HttpResponse httpClientResponse = httpClient.execute(httpRequest);
			responseJson = EntityUtils.toString(httpClientResponse.getEntity());
			*/
			/*
			HttpResponse httpClientResponse = httpClient.execute(httpRequest);
			try {
				responseJson = new BasicResponseHandler().handleResponse(httpClientResponse);
			} catch (Exception e) {
				//e.printStackTrace();
				responseJson = e.getMessage();
			}
			*/
			/*responseCode = httpClientResponse.getStatusLine().getStatusCode();
			responsedMessage = responseJson;*/
			
			int timeout = 60;
			
			HttpClient http = new HttpClient(accessUrl);
			http.setTimeout(timeout * 1000);
			http.setAllowUntrusted(true);
			
			if (UtilValidate.isNotEmpty(getPayload())) {
				http.setRawStream(payload);
			}
			if (UtilValidate.isNotEmpty(parameters)) {
				http.setParameters(parameters);
			}
			
			// set headers
			http.setHeader("Content-Type", contentType);
			if (UtilValidate.isNotEmpty(authorization)) {
				http.setHeader("Authorization", authorization);
			}
			
			if (UtilValidate.isNotEmpty(authParams)) {
				for (String authKey : authParams.keySet()) {
					String authValue = (String) authParams.get(authKey);
					if (UtilValidate.isNotEmpty(authValue)) {
						http.setHeader(authKey, authValue);
					}
				}
			}
			
			if ( ((UtilValidate.isEmpty(isTokenRequest) || !isTokenRequest) && !isNoAuthRequest) && UtilValidate.isNotEmpty(accessToken)) {
				http.setHeader("Authorization", "Bearer " + accessToken);
			}
			http.setHeader("Accept", "*/*");
			
			try {
				if (UtilValidate.isNotEmpty(httpMethod) && httpMethod.equals("DELETE")) {
					responseJson = http.sendHttpRequest("delete");
				} else if (UtilValidate.isNotEmpty(httpMethod) && httpMethod.equals("GET")) {
					responseJson = http.sendHttpRequest("get");
				} else {
					responseJson = http.sendHttpRequest("post");
				}
			} catch (Exception e) {
				//e.printStackTrace();
				responseJson = e.getMessage();
			}
			
			responseCode = http.getResponseCode();
			responsedMessage = responseJson;
			
			System.out.println(responseJson);
			System.out.println(responseCode);
			
			setResponsedTime(UtilDateTime.nowTimestamp());
			
			//String responseRefNum = con.getHeaderField("x-mashery-message-id");
			String responseRefNum = "";
	        System.out.println("\n -- RequestURL -- ");
	        System.out.println("\tURL : " + accessUrl);
	        System.out.println("\n -- HTTP Headers -- ");
	        System.out.println("\tContent-Type : " + contentType);
	        System.out.println("\tv-c-merchant-id : " + merchantIdentifier);
	        System.out.println("\tHost : " + channelAccessHost);
	        System.out.println("\n -- Response Message -- " );
	        System.out.println("\tResponse Code :" + responseCode);
	        System.out.println("\tx-mashery-message-id :" + responseRefNum);
	        
	        WriterUtil.writeLog(delegator, requestMethod, responseRefNum, payload, responseJson, ""+responseCode, getResponseStatus(), assocId, requestedTime, responsedTime, responseRefNum, systemName);
	        
	        if (isSuccessResponse(getResponseCode())) {
				response.put(GlobalConstants.RESPONSE_MESSAGE, responseJson);
				response.put(GlobalConstants.RESPONSE_CODE, responseCode);
				
				response.put("responseRefNum", responseRefNum);
		        response.put("responsePayload", responsedMessage);
		        response.put("responseMap", responseMap);
		        response.put("isSuccess", isSuccessResponse(responseCode));
		        
			} else if ( (getResponseCode() == ResponseCodes.UNAUTHORIZED_CODE) && !isTokenRequest && (UtilValidate.isEmpty(doRefreshToken) || doRefreshToken.equals("Y"))) {
				if (countToGenerateAccessToken < 2) {
					countToGenerateAccessToken++;
					response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
					refreshToken();
					submitRequest(request, response);
				}
			} else {
				Debug.logError("REST Client ERROR: "+responseJson, MODULE);
				response.put(GlobalConstants.RESPONSE_CODE, responseCode);
				response.put(GlobalConstants.RESPONSE_MESSAGE, responseJson);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError("REST Client ERROR: "+LogUtil.getPrintStackTrace(e), MODULE);
			LogUtil.saveLogError("REST Client ERROR: "+e.getMessage(), GlobalConstants.REST_API_LOG_FILE);
    		
			response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
		}
    }
	
	/*public void submitRequest(Map<String, Object> request, Map<String, Object> response) {
    	
    	String accessUrl = ParamUtil.getString(request, "accessUrl");
    	String requestMethod = ParamUtil.getString(request, "requestMethod"); 
    	String assocId = ParamUtil.getString(request, "assocId"); 
    	
		URL url;
		String responseJson = null;
		
		try {
			
			payload = (String) request.get("payload");
			//System.out.println("\tRequest Payload :\n" + payload.toString());
			
			Boolean isTokenRequest = ParamUtil.getBoolean(request, "isTokenRequest");
			Boolean isNoAuthRequest = ParamUtil.getBoolean(request, "isNoAuthRequest");
			
			setRequestedTime(UtilDateTime.nowTimestamp());
			System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,TLSv1.3,SSLv3");
			
			url = new URL(accessUrl);
			
			SSLContext sc = SSLContext.getInstance("SSLv3");
			TrustManager[] tma = { this };
			sc.init(null, tma, null);
			SSLSocketFactory ssf = sc.getSocketFactory();
			
			//HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
			//HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setSSLSocketFactory(ssf);
			
			con.setConnectTimeout(95000);
			
			if ( ((UtilValidate.isEmpty(isTokenRequest) || !isTokenRequest) && !isNoAuthRequest) && UtilValidate.isNotEmpty(accessToken)) {
				con.setRequestProperty("Authorization", "Bearer " + accessToken);
			}
			
	        con.setRequestMethod("POST");
	        
	        con.setRequestProperty("User-Agent", USER_AGENT);
	        con.setRequestProperty("Content-Type", "application/json");
	        con.setHostnameVerifier(RestUtil.getHostnameVerifier());
	        
	        // Send POST request
	        con.setDoOutput(true);
	        con.setDoInput(true);
			
	        if (UtilValidate.isNotEmpty(getPayload())) {
	        	DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		        wr.write(getPayload().getBytes("UTF-8"));
		        wr.flush();
		        wr.close();
	        }
	        
			// dump all the content
			responseJson = printContent(con);
			
			setResponsedTime(UtilDateTime.nowTimestamp());
			
			String responseRefNum = con.getHeaderField("x-mashery-message-id");
	        System.out.println("\n -- RequestURL -- ");
	        System.out.println("\tURL : " + url);
	        System.out.println("\n -- HTTP Headers -- ");
	        System.out.println("\tContent-Type : " + "application/json");
	        System.out.println("\tv-c-merchant-id : " + merchantIdentifier);
	        System.out.println("\tHost : " + channelAccessHost);
	        System.out.println("\n -- Response Message -- " );
	        System.out.println("\tResponse Code :" + responseCode);
	        System.out.println("\tx-mashery-message-id :" + responseRefNum);
	        
	        WriterUtil.writeLog(delegator, requestMethod, responseRefNum, payload, responseJson, ""+responseCode, getResponseStatus(), assocId, requestedTime, responsedTime, responseRefNum, systemName);
	        
	        if (isSuccessResponse(con.getResponseCode())) {
				response.put(GlobalConstants.RESPONSE_MESSAGE, responseJson);
				response.put(GlobalConstants.RESPONSE_CODE, responseCode);
				
				response.put("responseRefNum", responseRefNum);
		        response.put("responsePayload", responsedMessage);
		        response.put("responseMap", responseMap);
		        response.put("isSuccess", isSuccessResponse(responseCode));
		        
			} else if ( (con.getResponseCode() == ResponseCodes.UNAUTHORIZED_CODE) && !isTokenRequest) {
				if (countToGenerateAccessToken < 2) {
					countToGenerateAccessToken++;
					response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
					refreshToken();
					submitRequest(request, response);
				}
			} else {
				Debug.logError("REST Client ERROR: "+responseJson, MODULE);
				response.put(GlobalConstants.RESPONSE_CODE, responseCode);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError("REST Client ERROR: "+e.getMessage(), MODULE);
			LogUtil.saveLogError("REST Client ERROR: "+e.getMessage(), GlobalConstants.REST_API_LOG_FILE);
    		
			response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(GlobalConstants.RESPONSE_MESSAGE, "submitRequest Failed...! "+e.getMessage());
		}
		
    }*/
	
    private String getPayload() throws IOException {
        String messageBody = payload;
        return messageBody;
    }
    
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
		try {

			chain[0].checkValidity();

        } catch (Exception e) {

            throw new CertificateException("Certificate not valid or trusted.");

        }
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
		try {

			chain[0].checkValidity();

        } catch (Exception e) {

            throw new CertificateException("Certificate not valid or trusted.");

        }
	}

	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}
	
}
