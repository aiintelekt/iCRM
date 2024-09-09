package org.fio.homeapps.util;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.XML;
import org.ofbiz.base.conversion.JSONConverters.JSONToMap;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Sharif
 *
 */
public class ParamUtil {
	
	private static String MODULE = ParamUtil.class.getName();
	public static final ObjectMapper mapper = new ObjectMapper();
	private static final Pattern VALID_SELECT_PATTERN = Pattern.compile(
		    "^SELECT\\s+([\\w\\*]+(\\s*\\.\\s*[\\w\\*]+)?(\\s+AS\\s+[\\w]+)?\\s*(,\\s*[\\w\\*]+(\\s*\\.\\s*[\\w\\*]+)?(\\s+AS\\s+[\\w]+)?\\s*)*)\\s+" +
		    "FROM\\s+[\\w]+(\\s+(AS\\s+)?[\\w]+)?" +
		    "(\\s+(INNER|LEFT|RIGHT|FULL)?\\s*JOIN\\s+[\\w]+(\\s+(AS\\s+)?[\\w]+)?\\s+ON\\s+[\\w\\s.=><]+)*" +
		    "(\\s+WHERE\\s+[\\w\\s.=><]+)?(\\s*;?)$", Pattern.CASE_INSENSITIVE
		);

	public static long getLong (Map<String, Object> context, String key) {
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
				return Long.valueOf(""+context.get(key));
			}
		} catch (NumberFormatException e) {
		}
		return 0;
	}
	
	public static BigDecimal getBigDecimal (Map<String, Object> context, String key) {
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
				return BigDecimal.valueOf(Double.valueOf(""+context.get(key)));
			}
		} catch (NumberFormatException e) {
		}
		return null;
	}
	public static BigDecimal getBigDecimal (Map<String, Object> context, String key, BigDecimal defaultValue) {
		return getBigDecimal(context, key, defaultValue, null);
	}
	public static BigDecimal getBigDecimal (Map<String, Object> context, String key, BigDecimal defaultValue, BigDecimal devidedBy) {
		if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
			BigDecimal res = BigDecimal.valueOf(Double.valueOf(""+context.get(key)));
			if (UtilValidate.isNotEmpty(devidedBy)) {
				res = res.divide(devidedBy).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			return res;
		}
		return defaultValue;
	}
	
	public static boolean getBoolean (Map<String, Object> context, String key) {
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
				return Boolean.valueOf(""+context.get(key));
			}
		} catch (Exception e) {
		}
		return false;
	}
	public static String getIndicator (Map<String, Object> context, String key) {
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
				return Boolean.valueOf(""+context.get(key)) ? "Y" : "N";
			}
		} catch (Exception e) {
		}
		return "N";
	}
	
	public static String getString (Map<String, Object> context, String key) {
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
				return String.valueOf(""+context.get(key));
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static int getInteger (Map<String, Object> context, String key) {
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
				return Integer.valueOf(""+context.get(key));
			}
		} catch (NumberFormatException e) {
		}
		return 0;
	}
	
	public static Double getDouble (Map<String, Object> context, String key) {
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
				return Double.valueOf(""+context.get(key));
			}
		} catch (NumberFormatException e) {
		}
		return null;
	}
	
	public static Timestamp getDateTime (Map<String, Object> context, String key) {
		return getDateTime(context, key, 0, null, null);
	}
	
	public static Timestamp getDateTime (Map<String, Object> context, String key, String fromTimezoneId, String toTimezoneId) {
		return getDateTime(context, key, 0, fromTimezoneId, toTimezoneId);
	}
	
	public static Timestamp getDateTime (Map<String, Object> context, String key, int addHour, String fromTimezoneId, String toTimezoneId) {
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
				String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
				String value = (String) context.get(key);
				value = value.replace("T", " ");
				
				if (value.contains("+")) {
					value = value.substring(0, value.indexOf("+"));
				}
				Timestamp convertedDate = UtilDateTime.stringToTimeStamp(value, dateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
		        
				if (addHour != 0) {
					convertedDate = UtilDateTime.addHoursToTimestamp(convertedDate, addHour);
				}
				
				if (UtilValidate.isNotEmpty(fromTimezoneId) && UtilValidate.isNotEmpty(toTimezoneId)) {
					convertedDate = UtilDateTime.convertedTimestamp(value, "yyyy-MM-dd HH:mm:ss.SSS", fromTimezoneId, toTimezoneId);
				}
				
				return convertedDate;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getDateTimeStr (Map<String, Object> context, String key) {
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get(key))) {
				
				String formatedDate = context.get(key).toString().substring(0, context.get(key).toString().lastIndexOf("."));
				formatedDate = formatedDate.replace(" ", "T");
				
		        return formatedDate;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static Timestamp getDateTime (String value) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
				value = value.replace("T", " ");
				if (value.contains("+")) {
					value = value.substring(0, value.indexOf("+"));
				}
				Timestamp convertedDate = UtilDateTime.stringToTimeStamp(value, dateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
		        return convertedDate;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static Timestamp getDate (String value) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				String dateTimeFormat = "yyyy-MM-dd";
				Timestamp convertedDate = UtilDateTime.stringToTimeStamp(value, dateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
		        return convertedDate;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static Map<String, Object> prepareObjectParams (Map<String, Object> context) {
		Map<String, Object> params = new HashMap<String, Object>();
		
		for (String key : context.keySet()) {
			if (UtilValidate.isNotEmpty(context.get(key))) {
				params.put(key, context.get(key));
			}
		}
		return params;
	} 
	
	public static Map<String, String> prepareStringParams (Map<String, String> context) {
		Map<String, String> params = new HashMap<String, String>();
		
		for (String key : context.keySet()) {
			if (UtilValidate.isNotEmpty(context.get(key))) {
				params.put(key, context.get(key));
			}
		}
		return params;
	} 
	
	public static String getParameterName (String paramName) {
		
		if (UtilValidate.isNotEmpty(paramName)) {
			return paramName.replaceFirst("etl_param_", "");
		}
		
		return null;
	}
	
	//Desc : minutes to milliseconds conversion
	public static long minutesToMillis (double minutes) {
		
		if (UtilValidate.isNotEmpty(minutes)) {
			return TimeUnit.MINUTES.toMillis((long) minutes);
		}
		return 0;
	}
	
	//Desc : milliseconds to minutes conversion
	public static long millisToMinute (long milliSeconds) {
		
		if (UtilValidate.isNotEmpty(milliSeconds)) {
			return TimeUnit.MILLISECONDS.toMinutes(milliSeconds);
		}
		return 0;
	}
	
	public static Map<String ,Object> jsonToMap(String json) {
		Map<String, Object> request = new LinkedHashMap<String, Object>();
		try {
			JSONObject filterObj = JSONObject.fromObject(json);
			request = ParamUtil.jsonToMap(filterObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return request;
	}
	
	public static Map<String ,Object> jsonToMap(JSONObject json) throws Exception{
		Map<String, Object> retMap = new LinkedHashMap<String, Object>();
		if (json != null) {
			retMap = toMap(json);
		}
		return retMap;
	}
	
	public static Map<String ,Object> jsonToMap(Object json) {
		Map<String, Object> request = new LinkedHashMap<String, Object>();
		try {
			JSON jsonFeed = JSON.from(json);
			
			JSONToMap jsonMap = new JSONToMap();
			request = jsonMap.convert(jsonFeed);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return request;
	}
	
	public static String mapToJson(Map<String, Object> context) throws Exception{
		if (UtilValidate.isEmpty(context)) {
			return null;
		}
		Gson gson = new Gson();
		String jsonContext = gson.toJson(context);
		return jsonContext;
	}
	
	public static String toJson(Object context) {
		try {
			if (UtilValidate.isEmpty(context)) {
				return null;
			}
			Gson gson = new Gson();
			String jsonContext = gson.toJson(context);
			return jsonContext;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String ,Object> toMap(JSONObject json) throws Exception{
		Map<String ,Object> map = new LinkedHashMap<String ,Object>();

		Iterator<String> keysItr  = json.keys();
		while(keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = json.get(key);

			if (value instanceof JSONArray) {
				value  = toList((JSONArray) value);

			} else if (value instanceof JSONObject) {
				value  = toMap((JSONObject) value);
			}

			map.put(key, value);
		}
		return map;
	}

	public static List<Object> toList(JSONArray array) throws Exception{
		List<Object> list = new ArrayList<Object>();

		for(int i=0; i < array.size(); i++) {
			Object value =array.get(i);
			if (value instanceof JSONArray) {
				value  = toList((JSONArray) value);

			} else if (value instanceof JSONObject) {
				value  = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}
	
	public static List jsonToList(String json) {
		TypeFactory factory = mapper.getTypeFactory();
		CollectionType listType = 
			    factory.constructCollectionType(List.class, Map.class);

		List<Map<String, Object>> result = new ArrayList<>();
		try {
			result = mapper.readValue(json, listType);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return result;
	}
	
	public static Timestamp getTimestamp(String date, String time, String format) {
		try {
			if (UtilValidate.isNotEmpty(date) && UtilValidate.isNotEmpty(time)) {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				Timestamp result = new java.sql.Timestamp(sdf.parse(date+" "+time).getTime());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Timestamp getTimestamp(String date, String format) throws Exception{
		if (UtilValidate.isNotEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Timestamp result = new java.sql.Timestamp(sdf.parse(date).getTime());
			return result;
		}
		return null;
	}
	
	public static boolean isInteger(String s) {
        boolean isValidInteger = false;
        try {
            Integer.parseInt(s.trim());
            isValidInteger = true;
        } catch (NumberFormatException ex) {}
        return isValidInteger;
    }
	
	public static boolean isBoolean(String s) {
        boolean isValidBoolean = false;
        try {
            Boolean.parseBoolean(s.trim());
            isValidBoolean = true;
        } catch (NumberFormatException ex) {}
        return isValidBoolean;
    }
	
	public static Map<String, Object> convertToMap (Object obj) {
		return mapper.convertValue(obj, Map.class);
	}
	
	public static boolean isValidJson(Object json) {
	    try {
	    	if (UtilValidate.isEmpty(json) || !(json instanceof String)) {
	    		return false;
	    	}
	        new JsonParser().parse(json.toString());
	    } catch (JsonSyntaxException e) {
	        return false;
	    }
	    return true;
	}
	
	public static boolean isSuccessResponse(int responseCode) {
    	if (responseCode >= 200 && responseCode <= 300) {
    		return true;
    	}
    	return false;
    }
	
	public static Object getFieldValue(Object data, String fieldName) {
		try {
			Object value = new PropertyDescriptor(fieldName, data.getClass()).getReadMethod().invoke(data);
			return value;
		} catch (Exception e) {
		}
		return null;
	}
	public static Object setFieldValue(Object data, String fieldName, Object fieldValue) {
		try {
			Object value = new PropertyDescriptor(fieldName, data.getClass()).getWriteMethod().invoke(data, fieldValue);
			return value;
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String xmlToJson(String xml) {
        try {
        	// Create XmlMapper
			XmlMapper xmlMapper = new XmlMapper();

			// Convert XML to JsonNode (Jackson's tree model)
			JsonNode jsonNode = xmlMapper.readTree(xml);

			// Convert JsonNode to JSON string
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(jsonNode);
			return json;
        } catch (Exception ex) {}
        return null;
    }
	
	public static String jsonToXml(String jsonInput){
		String xmlInput = "";
		if(UtilValidate.isNotEmpty(jsonInput) && (isValidJson(jsonInput))){
			try {
				org.json.JSONObject json = new org.json.JSONObject(jsonInput);
				String xml = XML.toString(json);
				xmlInput = xml;
				//System.out.println("*****XML*****"+xmlInput);
				//Debug.log("*****XML*****"+xmlInput);
			} catch (JSONException e) {
				/*e.printStackTrace();*/
				Debug.logError(e.getMessage(),MODULE);
			}
		}
		return xmlInput;
	}
	
	public static String sanitize(String input) {
		if (input == null) return "";
		return input.replaceAll("[^a-zA-Z0-9_-]", "");
	}

	public static String sanitizeSqlSelectQuery(String input) {
        if (input == null) {
            return "";
        }

        String sanitized = input.replaceAll("[^a-zA-Z0-9_\\s,.*=><\\-()]+", "");

        return VALID_SELECT_PATTERN.matcher(sanitized).matches() ? sanitized : "";
    }

}
