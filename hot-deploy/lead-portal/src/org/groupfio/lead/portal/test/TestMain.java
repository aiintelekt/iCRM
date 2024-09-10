package org.groupfio.lead.portal.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

import org.ofbiz.base.util.Debug;

/**
 * @author Sharif
 *
 */
public class TestMain {
	public static final String MODULE = TestMain.class.getName();
	
	
	private String method1(String var1, String var2){
		return null;
	}
	
	private String method1(String var1, int var2){
		return null;
	}
	
	private String method1(String var1, String var2, String var3){
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			System.out.println(3%2);
			System.out.println(3/2);
			
			
			
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);

		}	
		
	}
	
	public static String printContent(HttpsURLConnection con) {
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
				Debug.logError(e.getMessage(), MODULE);

			}
			
		}
		
		return null;

	}
	
	public static boolean isSuccessResponse(int responseCode) {
    	if (responseCode >= 200 && responseCode <= 300) {
    		return true;
    	}
    	return false;
    }

}
