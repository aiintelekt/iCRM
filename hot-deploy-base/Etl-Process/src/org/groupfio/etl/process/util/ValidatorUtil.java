/**
 * 
 */
package org.groupfio.etl.process.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
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
	private static final String DATE_FORMAT = "dd-MM-yyyy";
	private static final String PHONE_REGEX = "\\+\\d+";
	private static final String ONLY_DIGITS = "\\d+";
	private static final String PAN_REGEX = "^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]){10}$";
	private static final String  ONLY_ALPHABETS = "^([^0-9]*)$";
	private static final String  APPHABETS_SPECIAL_CHARECTERS = "^[ A-Za-z0-9'@.!&:*()+-]*$";
	private static final String  PIN_CODE = "^\\d{6}$";
	private static final String  NUMBERS_DECIMAL_REGEX = "^(10|\\d{6,})(\\.\\d+)?$";
	private static final String  DEFAULT_NUMBERS_DECIMAL_REGEX = "^(10|\\d{0,})(\\.\\d+)?$";
	private static final String COUNTRY_CODE = "^[+]?[0-9]*$";
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
			
			if (matcher.matches()) {
				return true;
			}
			
			if (phone.matches("\\d+")) {
				return true;
			}
			
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
	
	public static boolean isValidEnum(Delegator delegator, String value, String enumTypeId) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);                       	
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId),
	               			condition
	               			);       
				}
				
				GenericValue enumEntity = EntityUtil.getFirst( delegator.findList("Enumeration", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
               	return UtilValidate.isNotEmpty(enumEntity);
			}
		} catch (Exception e) {
		}
		
		return false;
	}
	
	public static boolean isValidPostalCode(Delegator delegator, String postalCode, String city) {
		
		try {
			if (UtilValidate.isNotEmpty(postalCode) && UtilValidate.isNotEmpty(city)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("geoName", EntityOperator.EQUALS, postalCode),
						EntityCondition.makeCondition("geoAssocTypeId", EntityOperator.EQUALS, "POSTAL_CODE"),
						EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, "POSTAL_CODE")
               			);                       	
				
				GenericValue geoAssoc = EntityUtil.getFirst( delegator.findList("GeoAssocSummary", condition, null, null, null, false) );
				if (UtilValidate.isNotEmpty(geoAssoc)) {
					
					return geoAssoc.getString("geoId").equals(city);
				}
			}
		} catch (Exception e) {
		}
		
		return false;
	}
	
	public static boolean isValidPostalCode(Delegator delegator, Map<String, Object> data) {
		
		try {
			if (UtilValidate.isNotEmpty(data) && UtilValidate.isNotEmpty(data.get("postalCode"))) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("geoName", EntityOperator.EQUALS, data.get("postalCode")),
						EntityCondition.makeCondition("geoAssocTypeId", EntityOperator.EQUALS, "POSTAL_CODE"),
						EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, "POSTAL_CODE")
               			);                       	
				
				GenericValue geoAssoc = EntityUtil.getFirst( delegator.findList("GeoAssocSummary", condition, null, null, null, false) );
				if (UtilValidate.isNotEmpty(geoAssoc)) {
					
					data.put("city", geoAssoc.get("geoId"));
					GenericValue geoStateAssoc = EntityUtil.getFirst( delegator.findByAnd("GeoAssocSummary", UtilMisc.toMap("geoIdTo", geoAssoc.get("geoId"), "geoAssocTypeId", "COUNTY_CITY"), null, false) );
					if (UtilValidate.isNotEmpty(geoStateAssoc)) {
						data.put("stateProvinceGeoId", geoStateAssoc.get("geoId"));
					}
					
					return true;
				}
			}
		} catch (Exception e) {
		}
		
		return false;
	}
	
	public static boolean isValidEnum(Delegator delegator, String value, String enumTypeId, String parentEnumId) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);                       	
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId),
	               			condition
	               			);       
				}
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("parentEnumId", EntityOperator.EQUALS, parentEnumId),
	               			condition
	               			);       
				}
				
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
	
	public static GenericValue getValidEnum(Delegator delegator, String value, String enumTypeId) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);    
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId),
	               			condition
	               			);       
				}
				
				GenericValue enumEntity = EntityUtil.getFirst( delegator.findList("Enumeration", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
               	return enumEntity;
			}
		} catch (Exception e) {
		}
		
		return null;
	}
	
	public static boolean isValidGeo(Delegator delegator, String value, String geoTypeId) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoName")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			));   
				
				conditions.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.IN, StringUtil.split(geoTypeId, ",")));
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue entity = EntityUtil.getFirst( delegator.findList("Geo", mainConditon, null, UtilMisc.toList("-createdStamp"), null, false) );
				return UtilValidate.isNotEmpty(entity);
			}
		} catch (Exception e) {
		}
		
		return false;
	}
	
	public static boolean isValidGeo(Delegator delegator, String geoId, String geoIdTo, String geoAssocTypeId) {
		
		try {
			if (UtilValidate.isNotEmpty(geoId) && UtilValidate.isNotEmpty(geoIdTo) && UtilValidate.isNotEmpty(geoAssocTypeId)) {
				
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
               			EntityCondition.makeCondition("geoId", EntityOperator.EQUALS, geoId),
               			EntityCondition.makeCondition("geoIdTo", EntityOperator.EQUALS, geoIdTo),
               			EntityCondition.makeCondition("geoAssocTypeId", EntityOperator.EQUALS, geoAssocTypeId)
               			);      
				
				GenericValue entity = EntityUtil.getFirst( delegator.findList("GeoAssocSummary", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
               	return UtilValidate.isNotEmpty(entity);
			}
		} catch (Exception e) {
		}
		
		return false;
	}
	
	public static GenericValue getValidGeo(Delegator delegator, String value, String geoTypeId) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoName")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			));   
				
				conditions.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.IN, StringUtil.split(geoTypeId, ",")));
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue entity = EntityUtil.getFirst( delegator.findList("Geo", mainConditon, null, UtilMisc.toList("-createdStamp"), null, false) );
				return entity;
			}
		} catch (Exception e) {
		}
		
		return null;
	}
	
	public static String getValidGeoName(Delegator delegator, String value, String geoTypeId) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				GenericValue entity = getValidGeo(delegator, value, geoTypeId);
               	if (UtilValidate.isNotEmpty(entity)) {
               		return entity.getString("geoName");
               	}
			}
		} catch (Exception e) {
		}
		
		return value;
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
	public static boolean validateNumeric(String digits) {
		if (UtilValidate.isEmpty(digits)) {
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(ONLY_DIGITS, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(digits);
			return matcher.matches();
		} catch (Exception e) {
		}
		return false;
	}
	public static boolean validatePAN(String permanentAcccountNumber) {
		if (UtilValidate.isEmpty(permanentAcccountNumber)) {
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(PAN_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(permanentAcccountNumber);
			return matcher.matches();
		} catch (Exception e) {
		}
		return false;
	}
	public static boolean validateAlphabets(String name) {
		if (UtilValidate.isEmpty(name)) {
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(ONLY_ALPHABETS, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(name);
			return matcher.matches();
		} catch (Exception e) {
		}
		return false;
	}
	public static boolean validateAlphabetsWithSplCharacters(String name) {
		if (UtilValidate.isEmpty(name)) {
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(APPHABETS_SPECIAL_CHARECTERS, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(name);
			return matcher.matches();
		} catch (Exception e) {
		}
		return false;
	}
	public static boolean validatePINCode(String postalCode) {
		if (UtilValidate.isEmpty(postalCode)) {
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(PIN_CODE, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(postalCode);
			return matcher.matches();
		} catch (Exception e) {
		}
		return false;
	}
	public static boolean validateNumericDecimal(String digits) {
		if (UtilValidate.isEmpty(digits)) {
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(NUMBERS_DECIMAL_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(digits);
			return matcher.matches();
		} catch (Exception e) {
		}
		return false;
	}
	public static boolean validateCountyCode(String countyCode) {
		if (UtilValidate.isEmpty(countyCode)) {
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(COUNTRY_CODE, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(countyCode);
			
			if (matcher.matches()) {
				return true;
			}
			
			if (countyCode.matches("\\d+")) {
				return true;
			}
			
		} catch (Exception e) {
		}
		return false;
	}
	public static boolean validateDefaultNumericDecimal(String digits) {
		if (UtilValidate.isEmpty(digits)) {
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(DEFAULT_NUMBERS_DECIMAL_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(digits);
			return matcher.matches();
		} catch (Exception e) {
		}
		return false;
	}
	public static boolean isFutureDate(String dateValue) {
		//Timestamp now = UtilDateTime.nowTimestamp();
		Date now = new Date();
		SimpleDateFormat sdfrmt = new SimpleDateFormat(DATE_FORMAT);
		if (UtilValidate.isEmpty(dateValue)) {
			return false;
		}
		try {
			Date nowDate = sdfrmt.parse(sdfrmt.format(now));
			 Date incorporateDate = sdfrmt.parse(dateValue);
			// Date nowDate = new SimpleDateFormat(DATE_FORMAT).parse(now.toString());
			if(incorporateDate.after(nowDate)) {
				Debug.log(dateValue +" is future date format");
				return true;
			} else {
				Debug.log(dateValue +" is not future date format");
				return false;
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			System.out.println(dateValue+" is Invalid Date format");
	        return false;
		}
	}
	
	
}
