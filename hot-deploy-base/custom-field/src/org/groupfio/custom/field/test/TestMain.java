package org.groupfio.custom.field.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.groupfio.custom.field.constants.CustomFieldConstants;
import org.groupfio.custom.field.handlers.HttpRequestPostHandler;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Sharif
 *
 */
public class TestMain {
	
	/*static {
	    //for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){

	        public boolean verify(String hostname,
	                javax.net.ssl.SSLSession sslSession) {
	            if (hostname.equals("localhost")) {
	                return true;
	            }
	            return false;
	        }
	    });
	}*/

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/*String username = null;
        String password = null;
		
		String encodeString = "Basic YWRtaW46b2ZiaXo=";
		
		encodeString = encodeString.substring(encodeString.lastIndexOf(" ")+1);
		String decodedStr = Base64.base64Decode(encodeString);
		System.out.println("decodedStr: "+decodedStr);
		
		String[] auths =  decodedStr.split(":");
		if (UtilValidate.isNotEmpty(auths)) {
			username = auths[0];
			password = auths[1];
		}
		
		System.out.println("username: "+username);
		System.out.println("password: "+password);*/
		
		//System.out.println(Base64.base64Decode("OWuysXdA3FxdX%2BLTY7IuQPI%2F3eo%3D"));
		
		// rest call test with https [start]
		
		try {
			
			String groupId = "GROUP_01";
			
			groupId = groupId.toLowerCase().trim();
			groupId = groupId.substring(0, 1).toUpperCase() + groupId.substring(1);
			
			System.out.println(groupId);
			
			/*String modelPath = UtilProperties.getPropertyValue(CustomFieldConstants.configResource, "dynamic.entity.model.template.path");
			
			if (UtilValidate.isNotEmpty(modelPath)) {
				
				//UtilXml.
				
				 //URL menuFileUrl = FlexibleLocation.resolveLocation(dynamicEntityModelPath);
				
				File entityXmlFile = new File(modelPath);
				Document document = UtilXml.readXmlDocument(new FileInputStream(modelPath), modelPath);
				
				Element rootElt = document.getDocumentElement();
				
				String template = UtilXml.childElementAttribute(rootElt, "entity", "entity-name", "CustomFieldSeg${groupId}");
				 
				int i =0;
				
				//System.out.println(menuFileUrl);
				
			}*/
			
			/*//String https_url = "https://localhost:8443/restcomponent/api/ping/testMessage";
			String https_url = "https://localhost:8443/ewallet-app/api/party/createParty";
			String authStr = "uf!$5Vl@TaRvO9Y&xC8PRduwRd8rX&onwnorKq-cbPggzeP6gALT$m-#_Ff9_qIYduI0E_Viu@TGUoY6p0ji1&c@bWJf8ycXXs3YlshRwLyiUVsyOjKZcEv4NelHys4BwHY1K!POHQLI3UwDjD6er3aG7HdOGIE6RlAi!TAlBTiDB&8bZuTaSIgiAan-agnff!5B!0dD99mQ902xHa3ydCKCKMySOE$X0x1Y$5AcWY&JG0dXLRzaRGwOqCSp!-t";
			String authStringEnc = Base64.base64Encode(authStr);
			
			//String jsonRequest = "{\"singer\":\"Metallica\",\"title\":\"Fade To Black\"}";
			String jsonRequest = "{\"accountType\":\"MASTER_ACCT_OWNER\",\"partyName\":\"Sharif222\",\"baseCurrency\":\"USD\",\"description\":\"test description\",\"externalAppPartyRef\":\"ext_party_01\"}";
			
			URL url;
			
			SSLContext sc = SSLContext.getInstance("SSLv3");
			TrustManager[] tma = { new HttpRequestPostHandler() };
			sc.init(null, tma, null);
			SSLSocketFactory ssf = sc.getSocketFactory();
			HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
			HttpsURLConnection con = null;
			
			url = new URL(https_url);
			con = (HttpsURLConnection) url.openConnection();
			
			con.setRequestProperty("Authorization", "Bearer " + authStringEnc);
			con.setRequestMethod("POST");
			con.setRequestProperty("user-agent","Mozilla/5.0");
			con.setRequestProperty("Content-Type","text/plain");
			//con.setRequestProperty("Accept","application/json");
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(jsonRequest);
			wr.flush();
			wr.close();
			
			String responseJson = printContent(con);*/
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		// rest call test with https [end]
		
	}
	
	/*public static String printContent(HttpsURLConnection con) {
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
				
				System.out.println("****** Content of the URL ********");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						inputStream));

				String input;
				String response = "";

				while ((input = br.readLine()) != null) {
					System.out.println(input);
					response += input;
				}
				br.close();
				
				return response;
						
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return null;

	}
	
	public static boolean isSuccessResponse(int responseCode) {
    	if (responseCode >= 200 && responseCode <= 300) {
    		return true;
    	}
    	return false;
    }*/

}
