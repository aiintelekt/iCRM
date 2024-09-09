package org.groupfio.account.portal.handlers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletException;

import org.ofbiz.base.util.Debug;


/**
 * @author Sharif Ul Islam
 */
public class HttpRequestPostHandler implements javax.net.ssl.X509TrustManager {

	public static final String MODULE = HttpRequestPostHandler.class.getName();
	
	private static final String TLS_VERSION      = "TLSv1.2";
	private static final int    BAD_STATUS       = -1;
	  
	private String              responseContent;
	private String              requestContent;
	private String              requestUrl;
	private String              uploadRequestUrl;
	  
	private int                 httpStatusCode;
	private StringBuffer        buffer;
	private InputStream         input;
	private OutputStreamWriter  outStream;
	private BufferedReader      dataInput;
	
	private URL                 url;
	private URL                 uploadUrl;

	public int postHttpRequest(String requestContent, String requestUrl, boolean useSSL) throws ServletException, IOException, InterruptedException {

		// Before posting the Request Status is ByDefault Bad.
		setHttpStatusCode(BAD_STATUS);
		setRequestContent(requestContent);
		setRequestUrl(requestUrl);
		
		if(useSSL){
			try {
				setHttpStatusCode(postByHttpsURLConnection());
			} catch (Exception e) {
				Debug.logError(e.getMessage(), MODULE);

			}
		}
		else{
			try {
				setHttpStatusCode(postByHttpURLConnection());
			} catch (Exception e) {
				Debug.logError(e.getMessage(), MODULE);

			}
		}
    
		return this.httpStatusCode;
	}// end of the method postHttpRequest.

	// TrustManager Methods
	public int postByHttpsURLConnection() throws ServletException, IOException, InterruptedException {
		try {
			
			
			SSLContext sc = SSLContext.getInstance(TLS_VERSION);
			TrustManager[] tma = { new HttpRequestPostHandler() };
			sc.init(null, tma, null);
			SSLSocketFactory ssf = sc.getSocketFactory();
			HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
			HttpsURLConnection connection = null;

			url = new URL( this.getRequestUrl() );
			connection = (HttpsURLConnection) url.openConnection();
			connection.setConnectTimeout(15000);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			connection.setRequestProperty("charset", "utf-8");
			
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(false); 
			DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
			outStream.writeBytes( this.getRequestContent() );
			outStream.flush();
			outStream.close ();
			
			this.httpStatusCode = connection.getResponseCode();
			// end of Try and Catch Block.
			String line = new String();

			buffer = new StringBuffer();
			input = connection.getInputStream();
			dataInput = new BufferedReader(new InputStreamReader(input));
			while ((line = dataInput.readLine()) != null) {
				buffer.append(line);
				buffer.append('\n');
			}
			
			setResponseContent((String) buffer.toString().trim());
      
			System.out.println(getResponseContent());
      
		} catch (Exception e) {
			Debug.logError(e, MODULE);
			try {
				throw new Exception(
					e);
			} catch (Exception e1) {
				Debug.logError(e1.getMessage(), MODULE);

			}
		}
    
		return this.httpStatusCode;

	}// end of the method postByHttpsURLConnection.
	
	public int postByHttpURLConnection() throws ServletException, IOException, InterruptedException {
		try {
			HttpURLConnection connection = null;

			url = new URL( this.getRequestUrl() );
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(15000);
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);		
			
			this.httpStatusCode = connection.getResponseCode();
			
			// end of Try and Catch Block.
			String line = new String();

			buffer = new StringBuffer();
			input = url.openStream();			
			dataInput = new BufferedReader(new InputStreamReader(input));
			while ((line = dataInput.readLine()) != null) {
				buffer.append(line);
				buffer.append('\n');
			}
			
			setResponseContent((String) buffer.toString().trim());
      
			System.out.println(getResponseContent());
      
		} catch (Exception e) {
			Debug.logError(e, MODULE);
			try {
				throw new Exception(
					e);
			} catch (Exception e1) {
				Debug.logError(e1.getMessage(), MODULE);

			}
		}
    
		return this.httpStatusCode;

	}// end of the method postByHttpURLConnection.

	public int postByHttpURLConnection1() throws ServletException, IOException, InterruptedException {
		try {
			HttpURLConnection connection = null;

			url = new URL( this.getRequestUrl() );
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(1000);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Length", "" + requestContent.length());
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			outStream = new OutputStreamWriter(connection.getOutputStream());
			outStream.write( this.getRequestContent() );
			outStream.flush();
			this.httpStatusCode = connection.getResponseCode();
			// end of Try and Catch Block.
			String line = new String();

			
			
			
			
			
			HttpURLConnection uploadConnection = null;
			
			uploadUrl = new URL( this.getUploadRequestUrl() );
			
			uploadConnection = (HttpURLConnection) uploadUrl.openConnection();
			uploadConnection.setConnectTimeout(1000);
			uploadConnection.setRequestMethod("POST");
			uploadConnection.setRequestProperty("Content-Length", "" + requestContent.length());
			uploadConnection.setDoOutput(true);
			uploadConnection.setDoInput(true);
			uploadConnection.setUseCaches(false);
			
			BufferedOutputStream bos = new BufferedOutputStream( uploadConnection.getOutputStream() );
			
			InputStream in = url.openStream();
		    byte[] buf = new byte[4 * 1024]; // 4K buffer
		    int bytesRead;
		    while ((bytesRead = in.read(buf)) != -1) {
		    	bos.write(buf, 0, bytesRead);
		    }
			
      
		    System.out.println("End");
		    
      
		} catch (Exception e) {
			Debug.logError(e, MODULE);
			try {
				throw new Exception(
					e);
			} catch (Exception e1) {
				Debug.logError(e1.getMessage(), MODULE);

			}
		}
    
		return this.httpStatusCode;

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

	// Getter and Setter.
 
	public String getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(String responseContent) {
		this.responseContent = responseContent;
	}

	public String getRequestContent() {
		return requestContent;
	}

	public void setRequestContent(String requestContent) {
		this.requestContent = requestContent;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	
	public String getUploadRequestUrl() {
		return uploadRequestUrl;
	}

	public void setUploadRequestUrl(String uploadRequestUrl) {
		this.uploadRequestUrl = uploadRequestUrl;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	public URL getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(URL uploadUrl) {
		this.uploadUrl = uploadUrl;
	}
	
}// end of The Class SunRequestPostHandler.
