/**
 * 
 */
package org.groupfio.crm.service.resolver;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.crm.service.util.DataHelper;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
	
/**
 * @author Sharif
 * @author Mahendran Thanasekaran
 */
public class TatResolver extends Resolver {
	
	private static final String MODULE = TatResolver.class.getName();

	private static TatResolver instance;
	
	public static synchronized TatResolver getInstance(){
        if(instance == null) {
            instance = new TatResolver();
        }
        return instance;
    }
	
	@Override
	protected Map<String, Object> doResolve(Map<String, Object> context) throws Exception {
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		
		String tatCalc = ParamUtil.getString(context, "tatCalc");
		
		try {
			
			if (UtilValidate.isEmpty(tatCalc) || tatCalc.equals("N")) {
				
				String businessUnit = ParamUtil.getString(context, "businessUnit");
				
				Timestamp createdDate = (Timestamp) context.get("createdDate");
				Timestamp closedDate = (Timestamp) context.get("closedDate");
				if (UtilValidate.isNotEmpty(createdDate) && UtilValidate.isNotEmpty(closedDate) && closedDate.after(createdDate)) {
					String workStartHour = DataUtil.getGlobalValue(delegator, "WORK_START_TIME");
					String workEndHour = DataUtil.getGlobalValue(delegator, "WORK_END_TIME");
					LocalTime startTime = LocalTime.parse(UtilValidate.isNotEmpty(workStartHour)? workStartHour : "09:00");
					LocalTime endTime = LocalTime.parse(UtilValidate.isNotEmpty(workEndHour) ? workEndHour : "18:00");
					
					Map<String, Object> validateMap = new HashMap<String, Object>();
					LocalDateTime startDateTime = createdDate.toLocalDateTime();
		            validateMap.put("delegator", delegator);
		            validateMap.put("startDateTime", startDateTime);
		            validateMap.put("businessStartTime", startTime);
		            validateMap.put("businessEndTime", endTime);
		            validateMap.put("businessUnit", businessUnit);
		            List<LocalDate> holidayList = DataHelper.getHolidays(validateMap);
		            validateMap.put("holidayList", holidayList);
		            
		            //Created Date validation
		            Map<String, Object>  businessDate = DataHelper.getBusinessDate(validateMap);
		            if(businessDate != null) {
		            	startDateTime =  (LocalDateTime) businessDate.get("startDateTime");
		            	Debug.log("Created Business Date for TAT--->"+startDateTime);
		            }
		            //Close Date validation
		            LocalDateTime closedDateTime = closedDate.toLocalDateTime();
		            validateMap.put("startDateTime", closedDateTime);
		            validateMap.put("interval", -1);
		            businessDate = DataHelper.getClosedWorkingDate(validateMap);
		            if(businessDate != null) {
		            	closedDateTime =  (LocalDateTime) businessDate.get("startDateTime");
		            	Debug.log("Closed Business Date for TAT--->"+closedDateTime);
		            }
		            
		            //Map<String, Object> ctx = new HashMap<String, Object>();
		            if(closedDateTime.isAfter(startDateTime)) {
		            	context.put("createdDate", Timestamp.valueOf(startDateTime));
			            context.put("closedDate", Timestamp.valueOf(closedDateTime));
			            context.put("startTime", startTime);
			            context.put("endTime", endTime);
			            Map<String, Object> tatContext = calculateTat(context);
			            if(tatContext != null) {
			            	response.put("tatDays", ParamUtil.getLong(tatContext, "tatDays"));
							response.put("tatHours", ParamUtil.getLong(tatContext, "tatHours"));
							response.put("tatMins", ParamUtil.getBigDecimal(tatContext, "tatMins"));
			            }
		            }
				}
				
				/*
				Map<String, Object> tatContext = new LinkedHashMap<String, Object>();
				
				String businessUnit = ParamUtil.getString(context, "businessUnit");
				
				Timestamp createdDate = (Timestamp) context.get("createdDate");
				Timestamp closedDate = (Timestamp) context.get("closedDate");
				
				tatContext.put("createdMin", UtilDateTime.getMinute(createdDate, TimeZone.getDefault(), Locale.getDefault()));
				tatContext.put("createdHour", UtilDateTime.getHour(createdDate, TimeZone.getDefault(), Locale.getDefault()));
				tatContext.put("closedMin", UtilDateTime.getMinute(closedDate, TimeZone.getDefault(), Locale.getDefault()));
				tatContext.put("closedHour", UtilDateTime.getHour(closedDate, TimeZone.getDefault(), Locale.getDefault()));
				
				createdDate = prepareCreatedDate(delegator, createdDate, businessUnit);
				closedDate = prepareCreatedDate(delegator, closedDate, businessUnit);
				
				tatContext.put("delegator", delegator);
				
				tatContext.put("businessUnit", businessUnit);
				tatContext.put("createdDate", createdDate);
				tatContext.put("closedDate", closedDate);
				
				tatContext.put("interval", 0);
				
				tatContext.put("tatDays", 0);
				tatContext.put("tatHours", 0);
				
				Map<String, Object> tatResult = new HashMap<String, Object>();
				
				if (UtilValidate.isNotEmpty(createdDate) && UtilValidate.isNotEmpty(closedDate) && closedDate.after(createdDate)) {
					
					tatResult = prepareTat(tatContext);	
					
					response.put("tatDays", ParamUtil.getLong(tatResult, "tatDays"));
					response.put("tatHours", ParamUtil.getLong(tatResult, "tatHours"));
					response.put("tatMins", ParamUtil.getBigDecimal(tatResult, "tatMins"));
				}
				*/
			}
			
			response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			
			response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			
			return response;
			
		}
		
		response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;
	}
	
	private static Map<String, Object> calculateTat(Map<String, Object> context) {
		LocalTime startTime = (LocalTime) context.get("startTime");
		LocalTime endTime = (LocalTime) context.get("endTime");
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			//total working hours and minutes
			LocalTime tempDateTime = LocalTime.from(startTime);
	        long totalWorkingHours = tempDateTime.until( endTime, ChronoUnit.HOURS );
	        tempDateTime = tempDateTime.plusHours( totalWorkingHours );
	        long totalWorkingMinutes = tempDateTime.until( endTime, ChronoUnit.MINUTES );
	        Debug.log("totalWorkingHours--->"+totalWorkingHours+"-------totalWorkingMinutes---->"+totalWorkingMinutes, MODULE);
			
			LocalDateTime createdDateTime = ((Timestamp)context.get("createdDate")).toLocalDateTime().truncatedTo(ChronoUnit.MINUTES);
			LocalDateTime createdEndDay = createdDateTime.with(endTime);
			
			LocalDateTime closedDateTime = ((Timestamp)context.get("closedDate")).toLocalDateTime().truncatedTo(ChronoUnit.MINUTES);
			LocalDateTime closedStartDay = closedDateTime.with(startTime);
			
			LocalDateTime tempCreatedDate = LocalDateTime.from( createdDateTime );
			long createDateHours = tempCreatedDate.until( createdEndDay, ChronoUnit.HOURS );
			tempCreatedDate = tempCreatedDate.plusHours( createDateHours );
			long createDateMinutes = tempCreatedDate.until( createdEndDay, ChronoUnit.MINUTES );
			Debug.log("createDateHours--->"+createDateHours+"-------createDateMinutes---->"+createDateMinutes, MODULE);
			
			LocalDateTime tempClosedDate = LocalDateTime.from( closedStartDay );
			long closedDateHours = tempClosedDate.until( closedDateTime, ChronoUnit.HOURS );
			tempClosedDate = tempClosedDate.plusHours( closedDateHours );
			long closedDateMinutes = tempClosedDate.until( closedDateTime, ChronoUnit.MINUTES );
			Debug.log("closedDateHours--->"+closedDateHours+"-------closedDateMinutes---->"+closedDateMinutes, MODULE);
			
			context.put("createdDateTime", createdDateTime);
			context.put("closedDateTime", closedDateTime);
			long days = DataHelper.countBusinessDays(context);
			Debug.log("Total Business Days : "+days, MODULE);
			int spendHours = (int) (createDateHours+closedDateHours);
			int spendMinutes = (int) (createDateMinutes+closedDateMinutes);
			Debug.log("spendHours : "+spendHours+" spendMinutes: "+spendMinutes, MODULE);
			if(spendHours>0 && spendMinutes>=0) {
				days = days-1;
			}
			if(spendMinutes>=60) {
				int hours = (int) (spendMinutes/60);
				int remainMinutes = (int) (spendMinutes % 60);
				spendHours = spendHours + hours;
				spendMinutes = remainMinutes;
			}
			//Debug.log("spendHours 1 : "+spendHours+" spendMinutes 1 : "+spendMinutes, MODULE);
			LocalTime spendTime = LocalTime.of(spendHours, spendMinutes);
			if(spendHours >= totalWorkingHours) {
				int spendDay = (int) (spendHours/totalWorkingHours);
				days =  days + spendDay;
				int minutesToSubtract = (int) (spendDay*totalWorkingMinutes);
				spendHours = (int) (spendHours % totalWorkingHours);
				spendTime = spendTime.withHour(spendHours).withMinute(spendMinutes);
				spendTime = spendTime.minusMinutes(minutesToSubtract);
			}
			result.put("tatDays", days);
			result.put("tatHours", spendTime.getHour());
			result.put("tatMins", spendTime.getMinute());
			
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
		}
		return result;
	}
	
	public static Map<String, Object> prepareTat(Map<String, Object> context) {
    	
    	try {
    		
    		Delegator delegator = (Delegator) context.get("delegator");
    		
    		Timestamp commitDate = (Timestamp) context.get("commitDate");
    		
    		Timestamp createdDate = (Timestamp) context.get("createdDate");
    		Timestamp closedDate = (Timestamp) context.get("closedDate");
    		
    		String businessUnit = ParamUtil.getString(context, "businessUnit");
    		
    		int interval = ParamUtil.getInteger(context, "interval");
    		
    		int tatDays = ParamUtil.getInteger(context, "tatDays");
    		int tatHours = ParamUtil.getInteger(context, "tatHours");
    		
			int createdMonth = UtilDateTime.getMonth(createdDate, TimeZone.getDefault(), Locale.getDefault());
			int createdDay = UtilDateTime.getDayOfMonth(createdDate, TimeZone.getDefault(), Locale.getDefault());
			int createdYear = UtilDateTime.getYear(createdDate, TimeZone.getDefault(), Locale.getDefault());
			
			createdMonth = createdMonth + 1;
			
			int createdHour = UtilDateTime.getHour(createdDate, TimeZone.getDefault(), Locale.getDefault());
			
			int closedMonth = UtilDateTime.getMonth(closedDate, TimeZone.getDefault(), Locale.getDefault());
			int closedDay = UtilDateTime.getDayOfMonth(closedDate, TimeZone.getDefault(), Locale.getDefault());
			int closedYear = UtilDateTime.getYear(closedDate, TimeZone.getDefault(), Locale.getDefault());
			closedMonth = closedMonth + 1;
			int closedHour = UtilDateTime.getHour(closedDate, TimeZone.getDefault(), Locale.getDefault());
			
			int remainHour = 17 - createdHour - (interval);
			
			if (remainHour != 0) {
				commitDate = UtilDateTime.addHoursToTimestamp(createdDate, interval++);
			} else {
				interval = 0;
				createdDate = UtilDateTime.toTimestamp(createdMonth, createdDay+1, createdYear, 9, 0, 0);		
				commitDate = UtilDateTime.addHoursToTimestamp(createdDate, interval++);
				if (tatHours >= 10) {
					tatHours = tatHours +1;
				}
				context.put("isMoveToNextDay", true);
			}
			
			String cdHour = UtilDateTime.timeStampToString(commitDate, "yyyy-MM-dd HH", TimeZone.getDefault(), Locale.getDefault());
			String clsdHour = UtilDateTime.timeStampToString(closedDate, "yyyy-MM-dd HH", TimeZone.getDefault(), Locale.getDefault());
			
			if (commitDate.after(closedDate) || cdHour.equals(clsdHour)) {
				
				BigDecimal tatMins = null;
				
				// minute calculation [start]
				
				try {
					
					if (UtilValidate.isEmpty(context.get("isMoveToNextDay"))) {	
						Timestamp lastCommitDate = (Timestamp) context.get("lastCommitDate");
						if (UtilValidate.isEmpty(lastCommitDate)) lastCommitDate = commitDate;
						long hour = 0;
						
						long actualClosedHour = ParamUtil.getInteger(context, "closedHour");
						if (actualClosedHour == 9) {
							hour = 1;
						}
						
						long minute = UtilDateTime.getMinutes(closedDate, lastCommitDate);
						tatMins = new BigDecimal(hour + new Double(((minute - ((minute/60) * 60))*0.01))).setScale(2, BigDecimal.ROUND_HALF_UP);
						System.out.println("tatMins : " + tatMins);
						
					} else if (UtilValidate.isNotEmpty(context.get("isMoveToNextDay"))) {
						
						boolean isCreatedInBusinessHour = (createdHour < 18 && createdHour >= 9);
						
						long actualClosedHour = ParamUtil.getInteger(context, "closedHour");
						boolean isClosedInBusinessHour = (actualClosedHour < 18 && actualClosedHour >= 9);
						
						if (!isClosedInBusinessHour) {
							context.put("closedMin", 0);
						}
						
						long createdMinAdjustment = 0;
						createdHour = ParamUtil.getInteger(context, "createdHour");
						if (isCreatedInBusinessHour) {
							createdMinAdjustment = (60 - ParamUtil.getInteger(context, "createdMin"));
						}
						long minute = ParamUtil.getInteger(context, "closedMin") + createdMinAdjustment;
						tatMins = new BigDecimal((minute/60) + new Double(((minute - ((minute/60) * 60))*0.01))).setScale(2, BigDecimal.ROUND_HALF_UP);
						
						if (actualClosedHour == 17) {
							tatMins = tatMins.subtract(new BigDecimal(1));
						}
						
						System.out.println("tatMins : " + tatMins);
					}
					
				} catch (Exception e) {
					//e.printStackTrace();
					Debug.logError("Error in TAT minute calculation: "+e.getMessage(), MODULE);
				}
				
				int minHrs = tatMins.intValue();
				tatHours = tatHours + minHrs;
				tatMins = tatMins.subtract(new BigDecimal(minHrs));
				
				// minute calculation [end]
			
				tatDays = tatHours/9;
				tatHours = tatHours - (tatDays * 9);
				
				context.put("tatDays", tatDays);
				context.put("tatHours", tatHours);
				context.put("tatMins", tatMins);
				
				return context; 
			}
			
			context.put("lastCommitDate", commitDate);
			
			GenericValue holidayConfig = getHolidayConfig(delegator, commitDate, businessUnit);
			if (UtilValidate.isEmpty(holidayConfig)) {
				tatHours++;
			} else {
				interval = 0;
				
				createdMonth = UtilDateTime.getMonth(createdDate, TimeZone.getDefault(), Locale.getDefault());
				createdDay = UtilDateTime.getDayOfMonth(createdDate, TimeZone.getDefault(), Locale.getDefault());
				createdYear = UtilDateTime.getYear(createdDate, TimeZone.getDefault(), Locale.getDefault());
				
				createdMonth = createdMonth + 1;
				
				createdDate = UtilDateTime.toTimestamp(createdMonth, createdDay+1, createdYear, 9, 0, 0);		
				commitDate = UtilDateTime.addHoursToTimestamp(createdDate, interval);
			}
			
			context.put("interval", interval);
			context.put("tatHours", tatHours);
			context.put("createdDate", createdDate);
			context.put("commitDate", commitDate);
			
    		prepareTat(context);
    		
    	} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError("Error in TAT calculation: "+e.getMessage(), MODULE);
		}
    	
    	return context;
    }
	
	public static Timestamp prepareCreatedDate(Delegator delegator, Timestamp createdDate, String businessUnit) {
		
		try {
			
			int month = UtilDateTime.getMonth(createdDate, TimeZone.getDefault(), Locale.getDefault());
			int day = UtilDateTime.getDayOfMonth(createdDate, TimeZone.getDefault(), Locale.getDefault());
			int year = UtilDateTime.getYear(createdDate, TimeZone.getDefault(), Locale.getDefault());
			
			int hour = UtilDateTime.getHour(createdDate, TimeZone.getDefault(), Locale.getDefault());
			int minute = UtilDateTime.getMinute(createdDate, TimeZone.getDefault(), Locale.getDefault());
			
			month = month+1;
			
			GenericValue holidayConfig = getHolidayConfig(delegator, createdDate, businessUnit);
			if (UtilValidate.isNotEmpty(holidayConfig)) {
				createdDate = UtilDateTime.toTimestamp(month, day+1, year, 8, 0, 0);
				return prepareCreatedDate(delegator, createdDate, businessUnit);
			}
			
			boolean scenario1 = (hour >= 18 && hour <= 23);
			boolean scenario2 = (hour >= 0 && hour < 9);
			
			if (scenario1 || scenario2) {
				if (scenario1) {
					createdDate = UtilDateTime.toTimestamp(month, day+1, year, 9, 0, 0);
				} else if (scenario2) {
					createdDate = UtilDateTime.toTimestamp(month, day, year, 9, 0, 0);
				}
				
				holidayConfig = getHolidayConfig(delegator, createdDate, businessUnit);
				if (UtilValidate.isNotEmpty(holidayConfig)) {
					createdDate = UtilDateTime.toTimestamp(month, day+1, year, hour, minute, 0);
					return prepareCreatedDate(delegator, createdDate, businessUnit);
				}
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return createdDate;
	}
	
	private static GenericValue getHolidayConfig(Delegator delegator, Timestamp createdDate, String businessUnit) {
		
		try {
			
			List<EntityCondition> conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("status", EntityOperator.EQUALS, null),
							EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ACTIVE")),
					
					EntityCondition.makeCondition("holidayDate", EntityOperator.EQUALS, new java.sql.Date(createdDate.getTime()))
					
	                ));
			
			if (UtilValidate.isNotEmpty(businessUnit)) {
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("businessUnit", EntityOperator.EQUALS, businessUnit),
						EntityCondition.makeCondition("businessUnit", EntityOperator.EQUALS, "99999")
		                ));
			} else {
				conditionList.add( EntityCondition.makeCondition("businessUnit", EntityOperator.EQUALS, "99999") );
			}
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue holidayConfig = EntityUtil.getFirst( delegator.findList("TechDataHolidayConfig", mainConditons, null, null, null, false) );
			
			return holidayConfig;
			
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return null;
	}
	
}
