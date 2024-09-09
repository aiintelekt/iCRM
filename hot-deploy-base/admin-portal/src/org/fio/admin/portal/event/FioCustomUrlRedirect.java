package org.fio.admin.portal.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fio.homeapps.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FioCustomUrlRedirect {
	private static final String MODULE = FioCustomUrlRedirect.class.getName();
	private static String redirectUrl;
	private FioCustomUrlRedirect() {}
	
	public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
		return doJSONResponse(response, jsonObject.toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
		return doJSONResponse(response, JSONArray.fromObject(collection).toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Map map) {
		return doJSONResponse(response, JSONObject.fromObject(map));
	}

	public static String doJSONResponse(HttpServletResponse response, String jsonString) {
		String result = "success";

		response.setContentType("application/x-json");
		try {
			response.setContentLength(jsonString.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
			Debug.logWarning("Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(), MODULE);
			response.setContentLength(jsonString.length());
		}

		Writer out;
		try {
			out = response.getWriter();
			out.write(jsonString);
			out.flush();
		} catch (IOException e) {
			Debug.logError(e, "Failed to get response writer", MODULE);
			result = "error";
		}
		return result;
	}
	public static String requestRedirect(HttpServletRequest request, HttpServletResponse response) throws ServletException{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		ServletContext servletContext =  request.getSession().getServletContext();
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String hashValue = httpRequest.getParameter("hashVal");
			/*
			 * if(DataUtil.isBase64(hashValue)) {
				String redirectUrl = DataUtil.getGlobalValue(delegator, "REEB_TO_FIO_NEW_REDIRECT_URL");
				result.put("redirectUrl", redirectUrl+hashValue);
			} else */
			if(UtilValidate.isNotEmpty(hashValue)) {
				String redirectUrl = DataUtil.getGlobalValue(delegator, "REEB_TO_FIO_REDIRECT_URL");
				String customFieldName = DataUtil.getGlobalValue(delegator, "FSRID_CUSTOM_FLD_NAME","FSRID");
				//String externalId =  url.split("#")[1];
				//Delegator delegator = getDelegator(config.getServletContext());
				GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldName",customFieldName).queryFirst();
				String channelId = UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("groupId")) ? customField.getString("groupId") : "EXTERNAL_INFO";
				Delegator baseDelegator = DelegatorFactory.getDelegator(delegator.getDelegatorBaseName());
				GenericValue custRequestAttribute = EntityQuery.use(delegator).from("CustRequestAttribute").where("channelId", channelId ,"attrValue",hashValue).queryOne();
				if(UtilValidate.isNotEmpty(custRequestAttribute)) {
					String custRequestId = custRequestAttribute.getString("custRequestId");
					if(UtilValidate.isNotEmpty(custRequestId)) {
						result.put("redirectUrl", redirectUrl+hashValue);
						//result.put("custRequestId", custRequestId);
					}
				} else if(DataUtil.isBase64(hashValue)) {
					result.put("redirectUrl", redirectUrl+hashValue);
				}
			}
		} catch (Exception e) {
			Debug.logWarning(e, e.getMessage(), MODULE);
		}
		
		return doJSONResponse(httpResponse, result);
	}
	protected static Delegator getDelegator(ServletContext servletContext) {
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        if (delegator == null) {
            String delegatorName = servletContext.getInitParameter("entityDelegatorName");

            if (delegatorName == null || delegatorName.length() <= 0) {
                delegatorName = "default";
            }
            if (Debug.verboseOn()) Debug.logVerbose("Setup Entity Engine Delegator with name " + delegatorName, MODULE);
            delegator = DelegatorFactory.getDelegator(delegatorName);
            servletContext.setAttribute("delegator", delegator);
            if (delegator == null) {
                Debug.logError("[ContextFilter.init] ERROR: delegator factory returned null for delegatorName \"" + delegatorName + "\"", MODULE);
            }
        }
        return delegator;
    }
}
