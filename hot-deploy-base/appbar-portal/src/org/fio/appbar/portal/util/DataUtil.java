package org.fio.appbar.portal.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataUtil {

	public static String convertToJson(Map<String, Object> jsonMap) {
		String jsonString = null;
		try {
			Gson gson = new Gson();
			if(UtilValidate.isNotEmpty(jsonMap)) {
				jsonString = gson.toJson(jsonMap);
			} else {
				//jsonString = gson.toJson("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	public static Map<String, Object> convertToMap(String jsonString){
		Map<String, Object> appBarJsonEleMap = new HashMap<String, Object>();
		try {
			if(UtilValidate.isNotEmpty(jsonString)) {
				Gson gson = new Gson();
				Type type = new TypeToken<HashMap<String, Object>>() {}.getType();	
				appBarJsonEleMap = gson.fromJson(jsonString, type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appBarJsonEleMap;
	}
	public static Map < String, Object > convertGenericValueToMap(Delegator delegator, GenericValue genericValue) {
        Map < String, Object > returnMap = new HashMap < String, Object > ();
        try {
            if (UtilValidate.isNotEmpty(genericValue)) {
                Set < String > keys = genericValue.keySet();
                for (String key: keys) {
                    returnMap.put(key, genericValue.get(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMap;
    }
	public static boolean isInteger(String s) {
        boolean isValidInteger = false;
        try {
            Integer.parseInt(s.trim());
            isValidInteger = true;
        } catch (NumberFormatException ex) {}
        return isValidInteger;
    }
}
