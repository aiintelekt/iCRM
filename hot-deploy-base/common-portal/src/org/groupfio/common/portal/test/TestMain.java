package org.groupfio.common.portal.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Locale;
import java.util.TimeZone;

import javax.activation.MimetypesFileTypeMap;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.lang.WordUtils;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.handlers.HttpRequestPostHandler;
import org.groupfio.common.portal.util.DataHelper;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.StringUtil.StringWrapper;

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
		/*String value = "SERVICE_REQUEST";
		System.out.println(WordUtils.capitalize(value.toLowerCase().replace("_", " ")));*/
		
		File f = new File("H:\\upload\\Doc1.doc");
	    System.out.println("Mime Type of " + f.getName() + " is " +
	                         new MimetypesFileTypeMap().getContentType("Doc1.doc"));
		
		/*File f = new File("H:\\upload\\Doc1.doc");
	    System.out.println("Mime Type of " + f.getName() + " is " +
	                         new MimetypesFileTypeMap().getContentType(f));*/
	    
	    StringWrapper finalHelpUrl = null;
	    String helpUrl = null;
	    
	    finalHelpUrl = StringUtil.wrapString(helpUrl);
	    
	    System.out.println(finalHelpUrl);
		
	}
	
}
