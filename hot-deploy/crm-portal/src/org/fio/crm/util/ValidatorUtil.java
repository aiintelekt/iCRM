/**
 * 
 */
package org.fio.crm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class ValidatorUtil {

	private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
	private static final String ALPHANUMERIC_REGEX = "[a-zA-Z0-9]+";
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String PHONE_REGEX = "\\+\\d{10}";
	
	public static boolean validateEmail(String email) {
		if (UtilValidate.isEmpty(email)) {
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(email);
			return matcher.matches();
		} catch (Exception e) {
		}
		return false;
	}
	
	public static boolean validatePhone(String phone) {
		if (UtilValidate.isEmpty(phone)) {
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(PHONE_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(phone);
			return matcher.matches();
		} catch (Exception e) {
		}
		return false;
	}
	
	public static boolean isAlphanumeric(String value) {
		if (UtilValidate.isEmpty(value)) {
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(ALPHANUMERIC_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(value);
			return matcher.matches();
		} catch (Exception e) {
		}
		return false;
	}
	
	public static boolean isValidEnum(Delegator delegator, String value) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);                       	
				
				GenericValue enumEntity = EntityUtil.getFirst( delegator.findList("Enumeration", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
               	return UtilValidate.isNotEmpty(enumEntity);
			}
		} catch (Exception e) {
		}
		
		return false;
	}
	
	public static GenericValue getValidEnum(Delegator delegator, String value) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);                       	
				
				GenericValue enumEntity = EntityUtil.getFirst( delegator.findList("Enumeration", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
               	return enumEntity;
			}
		} catch (Exception e) {
		}
		
		return null;
	}
	
	public static boolean isValidDateFormat(String dateValue) {
		if (UtilValidate.isEmpty(dateValue)) {
			return false;
		}
		SimpleDateFormat sdfrmt = new SimpleDateFormat(DATE_FORMAT);
	    sdfrmt.setLenient(false);
	    try {
	        Date javaDate = sdfrmt.parse(dateValue); 
	        System.out.println(dateValue+" is valid date format");
	    } catch (ParseException e) {
	        System.out.println(dateValue+" is Invalid Date format");
	        return false;
	    }
		return true;
	}
	
}
