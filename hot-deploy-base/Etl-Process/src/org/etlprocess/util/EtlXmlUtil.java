package org.etlprocess.util;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
/**
 * @author Mahendran T
 * @since 2017-07-07
 * */
public class EtlXmlUtil {

    private static final String MODULE = EtlXmlUtil.class.getName();
	public static String cunstructXml(Document xmlDocument, GenericValue OrderApi, String ServiceName,String json) {
		StringBuilder xmlOutString = new StringBuilder();

		String outPutString = "";

		try {
			xmlOutString.append(UtilXml.writeXmlDocument(xmlDocument));

			outPutString = xmlOutString.toString();
			
			try {
				if(UtilValidate.isNotEmpty(json) && isValidJson(json))
				outPutString = XML.toJSONObject(outPutString).toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				/*e.printStackTrace();*/
				Debug.logError(e.getMessage(),MODULE);
			}
			
			OrderApi.put("xmlString", outPutString);
			OrderApi.put("responseDate", UtilDateTime.nowTimestamp());
			OrderApi.put("isOrderSent", "Y");
			OrderApi.put("requestAgent", null);
			OrderApi.put("serviceName", ServiceName);
			OrderApi.store();

		} catch (IOException excp) {
			Debug.log("cunstructXml throws an IOException ..." + xmlDocument, "");
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return outPutString;
	}
	
	public static void handleErrors(Element rootElement, List<Object> errorList) {
		Element errorElement = null;
		if ("Error".equalsIgnoreCase(rootElement.getNodeName())) {
			errorElement = rootElement;
		} else {
			errorElement = UtilXml.firstChildElement(rootElement, "Error");
		}
		if (errorElement != null) {
			Element errorCodeElement = UtilXml.firstChildElement(errorElement, "Code");
			Element errorMessageElement = UtilXml.firstChildElement(errorElement, "Message");
			if (errorCodeElement != null || errorMessageElement != null) {
				String errorCode = UtilXml.childElementValue(errorElement, "Code");
				String errorMessage = UtilXml.childElementValue(errorElement, "Message");
				if (UtilValidate.isNotEmpty(errorCode) || UtilValidate.isNotEmpty(errorMessage)) {
					String errMsg = "An error occurred [code: " + errorCode + " [Description: " + errorMessage + "].";
					errorList.add(errMsg);
				}
			}
		}
	}

	public static List<String> runSqlQuery(String query, Delegator delegator) {

		ResultSet rs = null;
		ArrayList<String> resultList = new ArrayList<String>();
		String selGroup = "org.ofbiz";

		String sqlCommandSeq = query;

		if (sqlCommandSeq != null && sqlCommandSeq.length() > 0 && selGroup != null && selGroup.length() > 0) {

			String helperName = delegator.getGroupHelperName(selGroup);
			GenericHelperInfo ghi = delegator.getGroupHelperInfo("org.ofbiz");
			SQLProcessor dumpSeq = new SQLProcessor(delegator, ghi);

			try {
				if (sqlCommandSeq.toUpperCase().startsWith("SELECT")) {

					rs = dumpSeq.executeQuery(sqlCommandSeq);

					while (rs.next()) {
						resultList.add(rs.getString(1));
					}
				}
			} catch (Exception e) {
			}
			finally {
				try { 
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {
				}
		}
		}
		return resultList;
	}
	
	public static boolean isValidJson(String json) {
	    try {
	        new JsonParser().parse(json);
	        return true;
	    } catch (JsonSyntaxException jse) {
	        return false;
	    }
	}
	public static String covertJsonToXml(String jsonInput){
		String xmlInput = "";
		if(UtilValidate.isNotEmpty(jsonInput) && (isValidJson(jsonInput))){
			try {
				JSONObject json = new JSONObject(jsonInput);
				String xml = XML.toString(json);
				xmlInput = xml;
				//System.out.println("*****XML*****"+xmlInput);
				Debug.log("*****XML*****"+xmlInput);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				/*e.printStackTrace();*/
				Debug.logError(e.getMessage(),MODULE);
			}
		
		}
		return xmlInput;
	}
}
