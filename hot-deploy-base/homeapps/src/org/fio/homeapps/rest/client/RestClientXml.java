package org.fio.homeapps.rest.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

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

/**
 * @author Sharif Ul Islam
 */
public class RestClientXml extends ApiClient implements javax.net.ssl.X509TrustManager {
	
	private static final String MODULE = RestClientXml.class.getName();
	
	private int countToGenerateAccessToken = 0;
	public String  payload = null;
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	public RestClientXml(Delegator delegator, ConfigurationParam configurationParam, GenericValue channelAccess,
			String accessType) {
		super(delegator, configurationParam, channelAccess, accessType);
	}
	
	public void submitRequest(Map<String, Object> request, Map<String, Object> response) {
    	
    	String accessUrl = ParamUtil.getString(request, "accessUrl");
    	String requestMethod = ParamUtil.getString(request, "requestMethod"); 
    	String assocId = ParamUtil.getString(request, "assocId"); 
    	String httpMethod = ParamUtil.getString(request, "httpMethod"); 
    	
		URL url;
		String responseValue = null;
		
		try {
			isJsonResponse = false;
			payload = (String) request.get("payload");
			String xmlPaylod = (String) request.get("xmlPaylod");
			//System.out.println("\tRequest Payload :\n" + payload.toString());
			
			String contentType = (String) request.get("contentType");
			if (UtilValidate.isEmpty(contentType)) {
				contentType = "application/xml";
			}
			if (UtilValidate.isEmpty(httpMethod)) {
				httpMethod = "POST";
			}
			
			Boolean isTokenRequest = ParamUtil.getBoolean(request, "isTokenRequest");
			Boolean isNoAuthRequest = ParamUtil.getBoolean(request, "isNoAuthRequest");
			
			setRequestedTime(UtilDateTime.nowTimestamp());
			System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,TLSv1.3,SSLv3");
			
			url = new URL(accessUrl);
			
			SSLContext sc = SSLContext.getInstance("TLSv1.3");
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
			
	        con.setRequestMethod(httpMethod);
	        
	        con.setRequestProperty("User-Agent", USER_AGENT);
	        con.setRequestProperty("Content-Type", contentType);
	        con.setRequestProperty("Content-Length", ""+accessUrl.length());
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
			responseValue = printContent(con);
			
			if (UtilValidate.isNotEmpty(responseValue) && responseValue.contains("Error")) {
				setResponseCode(500);
			}
			
			setResponsedTime(UtilDateTime.nowTimestamp());
			
			String responseRefNum = con.getHeaderField("x-mashery-message-id");
	        System.out.println("\n -- RequestURL -- ");
	        System.out.println("\tURL : " + url);
	        System.out.println("\n -- HTTP Headers -- ");
	        System.out.println("\tContent-Type : " + contentType);
	        System.out.println("\tv-c-merchant-id : " + merchantIdentifier);
	        System.out.println("\tHost : " + channelAccessHost);
	        System.out.println("\n -- Response Message -- " );
	        System.out.println("\tResponse Code :" + responseCode);
	        System.out.println("\tx-mashery-message-id :" + responseRefNum);
	        
	        WriterUtil.writeLog(delegator, requestMethod, responseRefNum, UtilValidate.isNotEmpty(payload) ? payload : xmlPaylod, responseValue, ""+responseCode, getResponseStatus(), assocId, requestedTime, responsedTime, responseRefNum, systemName);
	        
	        if (isSuccessResponse(con.getResponseCode())) {
				response.put(GlobalConstants.RESPONSE_MESSAGE, responseValue);
				response.put(GlobalConstants.RESPONSE_CODE, responseCode);
				
				response.put("responseRefNum", responseRefNum);
		        response.put("responsePayload", responsedMessage);
		        response.put("responseMap", responseMap);
		        response.put("isSuccess", isSuccessResponse(responseCode));
			} else {
				Debug.logError("REST Client ERROR: "+responseValue, MODULE);
				response.put(GlobalConstants.RESPONSE_CODE, responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError("REST Client ERROR: "+e.getMessage(), MODULE);
			LogUtil.saveLogError("REST Client ERROR: "+e.getMessage(), GlobalConstants.REST_API_LOG_FILE);
    		
			response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(GlobalConstants.RESPONSE_MESSAGE, "submitRequest Failed...! "+e.getMessage());
		}
		
    }
	
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
