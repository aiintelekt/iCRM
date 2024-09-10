/**
 * 
 */
package org.fio.homeapps.rest.util;

import java.util.List;

import javax.net.ssl.HostnameVerifier;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

/**
 * @author Sharif
 *
 */
public class RestUtil {

	private static final String MODULE = RestUtil.class.getName();
	
	public static HostnameVerifier getHostnameVerifier() {
		
		HostnameVerifier localhostAcceptedHostnameVerifier = null;
		
		try {
			HostnameVerifier defaultHostnameVerifier = javax.net.ssl.HttpsURLConnection.getDefaultHostnameVerifier();
	        localhostAcceptedHostnameVerifier = new javax.net.ssl.HostnameVerifier () {

	           public boolean verify ( String hostname, javax.net.ssl.SSLSession sslSession ) {
	                if ( hostname.equals ( "localhost" ) ) {
	                    return true;
	                }
	                 return defaultHostnameVerifier.verify ( hostname, sslSession );
	            }
	        };
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return localhostAcceptedHostnameVerifier;
	}
	
}
