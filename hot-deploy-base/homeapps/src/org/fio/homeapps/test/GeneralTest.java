/**
 * 
 */
package org.fio.homeapps.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fio.homeapps.util.DataHelper;

import com.google.gson.Gson;

import net.sf.json.JSONObject;

/**
 * @author Sharif
 *
 */
public class GeneralTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String var = "companyName";
		
		System.out.println(DataHelper.javaPropToLabelProp(var));

		/*String ov = "{\"custRequestTypeId\":\"20050\",\"sequenceNumber\":\"0002\",\"srType\":\"type0002\",\"status\":\"ACTIVE\"}";
		
		String nv = "{\"sequenceNumber\":\"1102\",\"srType\":\"type1102\",\"custRequestTypeId\":\"20050\",\"status\":\"ACTIVE\"}";
		
		JSONObject oldContext = JSONObject.fromObject(ov);
		JSONObject newContext = JSONObject.fromObject(nv);
		
		List<Map<String, Object>> compareList = new ArrayList<Map<String, Object>>();
		
		int totalChanged = 0;
		
		for (Object key : newContext.keySet()) {
			
			String newValue = (String) newContext.get(key);
	        String oldValue = (String) oldContext.get(key);
	        
	        System.out.println("key: "+ key + " value: " + newValue);
	        
	        Map<String, Object> compare = new LinkedHashMap<String, Object>();
	        
	        compare.put("propName", key);
	        compare.put("oldValue", oldValue);
	        compare.put("newValue", newValue);
	        
	        boolean isChanged = !newValue.equalsIgnoreCase(oldValue);
	        compare.put("isChanged", isChanged);
	        
	        if (isChanged) {
	        	totalChanged++;
	        }
	        
	        compareList.add(compare);
	    }
		
		System.out.println(compareList);
		
		Gson gson = new Gson();
		String jsonContext = gson.toJson(compareList);
		
		System.out.println(jsonContext);
		System.out.println("totalChanged> "+totalChanged);*/
	}

}
