package com.groupfio.ofbiz.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class HttpUtil {
	
	private static final String module = HttpUtil.class.getName();
	public static final ObjectMapper mapper = new ObjectMapper();
	
	public static List jsonArrayStrToMapList(String jsonBodyStr) {
		TypeFactory factory = mapper.getTypeFactory();
		CollectionType listType = 
			    factory.constructCollectionType(List.class, Map.class);

		List<Map<String, Object>> result = null;
		try {
			result = mapper.readValue(jsonBodyStr, listType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}
	

	
	public static Map<String,Object> jsonObjStrToMap(String jsonBodyStr) {
		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
		Map<String, Object> result = null;
		try {
			result = mapper.readValue(jsonBodyStr, typeRef);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}
	
	
	public static String getJsonStrBody(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
	    try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			try {
			    String line;
			    while ((line = reader.readLine()) != null) {
			        sb.append(line).append('\n');
			    }
			} finally {
			    reader.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return sb.toString();
	}
	
	public static void jsonResponse(HttpServletResponse response, String jsonString) {
		try {
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.print(jsonString);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeResultMapJSONResponse(Map<String, Object> result, HttpServletResponse response)
			throws IOException {
		JSON json = JSON.from(result);
		Debug.logInfo("json response: " + json.toString(), module);
		String jsonStr = json.toString();

		if (jsonStr == null) {
			Debug.logError("JSON Object was empty; fatal error!", module);
			return;
		}

		// set the JSON content type
		response.setContentType("application/json");
		// jsonStr.length is not reliable for unicode characters
		response.setContentLength(jsonStr.getBytes("UTF8").length);

		// return the JSON String
		Writer out;
		try {
			out = response.getWriter();
			out.write(jsonStr);
			out.flush();
		} catch (IOException e) {
			Debug.logError(e, module);
		}
	}

	public static Map<String, Object> getJSONAttributeMap(HttpServletRequest request,
			Set<? extends String> namesToSkip) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		Map<String, Object> attrMap = getAttributeMap(request, namesToSkip);
		for (Map.Entry<String, Object> entry : attrMap.entrySet()) {
			String key = entry.getKey();
			Object val = entry.getValue();
			if (val instanceof java.sql.Timestamp) {
				val = val.toString();
			}
			if (val instanceof String || val instanceof Number || val instanceof Map<?, ?> || val instanceof List<?>
					|| val instanceof Boolean) {
				if (Debug.verboseOn())
					Debug.logVerbose("Adding attribute to JSON output: " + key, module);
				returnMap.put(key, val);
			}
		}

		return returnMap;
	}

	public static Map<String, Object> getAttributeMap(HttpServletRequest request, Set<? extends String> namesToSkip) {
		Map<String, Object> attributeMap = new HashMap<String, Object>();

		// look at all request attributes
		Enumeration<String> requestAttrNames = UtilGenerics.cast(request.getAttributeNames());
		while (requestAttrNames.hasMoreElements()) {
			String attrName = requestAttrNames.nextElement();
			if (namesToSkip != null && namesToSkip.contains(attrName))
				continue;

			Object attrValue = request.getAttribute(attrName);
			attributeMap.put(attrName, attrValue);
		}

		if (Debug.verboseOn()) {
			Debug.logVerbose("Made Request Attribute Map with [" + attributeMap.size() + "] Entries", module);
			Debug.logVerbose("Request Attribute Map Entries: " + System.getProperty("line.separator")
					+ UtilMisc.printMap(attributeMap), module);
		}

		return attributeMap;
	}

}
