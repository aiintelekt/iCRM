package org.fio.customer.service.service.v3;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/***
 * 
 * @author Sharif
 *
 */
public class MeasurementServices {

	private static final String MODULE = CustomerServices.class.getName();
	public static final String resource = "crmUiLabels";
	
	public static Map createCustomerMeasurement(DispatchContext dctx, Map context) {
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
    	
    	String userLoginId = (String) requestContext.get("userLoginId");
    	//Timestamp requestedTime = (Timestamp) requestContext.get("requestedTime");
    	Timestamp logEntryDate = (Timestamp) requestContext.get("logEntryDate");
    	String timeUnit = (String) requestContext.get("timeUnit");
    	String timeMeasure = (String) requestContext.get("timeMeasure");
    	List<Map<String, Object>> measureList = (List<Map<String, Object>>) requestContext.get("measures");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> responseContext = new HashMap<String, Object>();
    	
    	try {
    		//responseContext.put("domainEntityType", domainEntityType);
    		
    		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
    		
			if (UtilValidate.isNotEmpty(measureList)) {
				for (Map<String, Object> measure : measureList) {
					String type = (String) measure.get("type");
					String value = (String) measure.get("value");
					
					if (UtilValidate.isNotEmpty(type) && UtilValidate.isNotEmpty(value)) {
						GenericValue userLoginMeasure = delegator.makeValue("UserLoginMeasurement");
						String seqId = delegator.getNextSeqId("UserLoginMeasurement");
						
						userLoginMeasure.put("seqId", seqId);
						userLoginMeasure.put("userLoginId", userLoginId);
						userLoginMeasure.put("timeUnit", timeUnit);
						userLoginMeasure.put("timeMeasure", timeMeasure);
						userLoginMeasure.put("value", new BigDecimal(value));
						userLoginMeasure.put("measurementTypeId", type);
						userLoginMeasure.put("logEntryDate", logEntryDate);
						
						if (UtilValidate.isNotEmpty(logEntryDate)) {
							userLoginMeasure.put("reportingYear", UtilDateTime.timeStampToString(logEntryDate, "yyyy", TimeZone.getDefault(), Locale.getDefault()));
						}
						
						/*if (UtilValidate.isNotEmpty(timeUnit) && UtilValidate.isNotEmpty(logEntryDate)) {
							if (timeUnit.equals("WEEK")) {
								userLoginMeasure.put("timeMeasure", ""+UtilDateTime.getDayOfWeek(logEntryDate, TimeZone.getDefault(), Locale.getDefault()));
							} else if (timeUnit.equals("MONTH")) {
								userLoginMeasure.put("timeMeasure", ""+UtilDateTime.getDayOfMonth(logEntryDate, TimeZone.getDefault(), Locale.getDefault()));
							}
						}*/
						
						userLoginMeasure.put("createdOn", UtilDateTime.nowTimestamp());
						userLoginMeasure.put("createdByUserLogin", UtilValidate.isNotEmpty(userLoginId) ? userLoginId : userLogin.getString("userLoginId"));
						userLoginMeasure.create();
					}
				}
			}
    		
			responseContext.put("userLoginId", userLoginId);
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	result.put("responseContext", responseContext);
    	result.putAll(ServiceUtil.returnSuccess("Successfully created customer measurement.."));
    	return result;
    }
	
	public static Map getCustomerMeasurement(DispatchContext dctx, Map context) {
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
    	
    	String userLoginId = (String) requestContext.get("userLoginId");
    	//Timestamp requestedTime = (Timestamp) requestContext.get("requestedTime");
    	String measurementTypeId = (String) requestContext.get("measurementTypeId");
    	Timestamp fromDate = (Timestamp) requestContext.get("fromDate");
    	Timestamp thruDate = (Timestamp) requestContext.get("thruDate");
    	String timeUnit = (String) requestContext.get("timeUnit");
    	String timeMeasure = (String) requestContext.get("timeMeasure");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> responseContext = new HashMap<String, Object>();
    	
    	try {
    		//responseContext.put("domainEntityType", domainEntityType);
    		
    		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
    		
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String partyId = DataUtil.getPartyIdByUserLoginId(delegator, userLoginId);
				String partyName = PartyHelper.getPartyName(delegator, partyId, false);
				String reportName = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "MSURMNT_RPT_NAME");
				
				responseContext.put("partyId", partyId);
				responseContext.put("partyName", partyName);
				responseContext.put("reportName", reportName);
				responseContext.put("measurementTypeId", measurementTypeId);
				responseContext.put("timeUnit", timeUnit);
				responseContext.put("timeMeasure", timeMeasure);
				//responseContext.put("requestedTime", requestedTime);
				
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("timeUnit", EntityOperator.EQUALS, timeUnit));
				if (UtilValidate.isNotEmpty(fromDate)) {
					conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
							EntityCondition.makeCondition("logEntryDate", EntityOperator.EQUALS, null),
							EntityCondition.makeCondition("logEntryDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate))
							));
				}
				if (UtilValidate.isNotEmpty(thruDate)) {
					conditions.add(EntityCondition.makeCondition("logEntryDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
				}
				if (UtilValidate.isNotEmpty(measurementTypeId)) {
					conditions.add(EntityCondition.makeCondition("measurementTypeId", EntityOperator.EQUALS, measurementTypeId));
				}
				if (UtilValidate.isNotEmpty(userLoginId)) {
					conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId));
				}
				
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	        	List<GenericValue> measureList = EntityQuery.use(delegator).from("UserLoginMeasurement").where(mainConditon).queryList();
				
	        	responseContext.put("measureList", measureList);
			}
    		
			responseContext.put("userLoginId", userLoginId);
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	result.put("responseContext", responseContext);
    	result.putAll(ServiceUtil.returnSuccess("Successfully get customer measurement.."));
    	return result;
    }
	
}
