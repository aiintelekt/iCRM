package org.groupfio.dyna.screen.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Locale;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.dyna.screen.handlers.HttpRequestPostHandler;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Sharif
 *
 */
public class TestMain {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			String value = "4556677888";
			
			String countryCode = value.substring(0, 3);
			String areaCode = value.substring(3, 6);
			String phoneNumber = value.substring(6, value.length());
			
			System.out.println(countryCode);
			System.out.println(areaCode);
			System.out.println(phoneNumber);
			
			value = "("+countryCode+")"+"-"+areaCode+"-"+phoneNumber;
			
			System.out.println(value);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
