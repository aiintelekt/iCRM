/**
 * 
 */
package org.groupfio.lead.service.util;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif
 *
 */
public class DataUtil {
	
	private static String MODULE = DataUtil.class.getName();
	
	
	
	public static String getUserLoginPartyId(Delegator delegator, String userLoginId) {
    	String partyId = null;
		try {
			if (UtilValidate.isNotEmpty(userLoginId)) {
				 GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userLoginId).queryOne();
				 if (UtilValidate.isNotEmpty(userLogin)) {
					 partyId = userLogin.getString("partyId");
				 }
			}
		} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);

		}
		return partyId;
	}
	
	public static String generateRandomDigit(String format) {
		String randomValue = "";
		try {
			format = UtilValidate.isNotEmpty(format) ? format : "%06d";
 			Random rnd = new Random();
		    int number = rnd.nextInt(999999);
		    randomValue = String.format(format, number);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return randomValue;
	}
	
	public static String randomNumRecursive(Delegator delegator) {
		String value = "";
		try {
			value = generateRandomDigit("%06d");
			GenericValue customerOtp = EntityQuery.use(delegator).from("SecurityTracking").where("value", value,"trackingTypeId","EMAIL_OTP").filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(customerOtp)) {
				randomNumRecursive(delegator);
			}
		} catch (Exception e) {
		}
		
		return value;
		
	}
	public static boolean isJSONValid(String test) {
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			try {
				new JSONArray(test);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}
}
