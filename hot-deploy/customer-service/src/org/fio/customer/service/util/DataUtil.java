package org.fio.customer.service.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

public class DataUtil {
	private static final String MODULE = DataUtil.class.getName();
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
	public static String getFormatedDate(String pattern, Date date) {
		if(UtilValidate.isNotEmpty(date)){
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			if(UtilValidate.isNotEmpty(pattern))
				sdf = new SimpleDateFormat(pattern);
			String formatedDate = sdf.format(date.getTime());
			return formatedDate;
		}
		return null;
	}
}
