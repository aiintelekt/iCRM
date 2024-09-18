package org.groupfio.crm.service.resolver;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.crm.service.util.DataHelper;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

public class ActivityTatResolver extends Resolver{
	private static final String MODULE = ActivityTatResolver.class.getName();

	private static ActivityTatResolver instance;
	
	public static synchronized ActivityTatResolver getInstance(){
        if(instance == null) {
            instance = new ActivityTatResolver();
        }
        return instance;
    }
	
	@Override
	protected Map<String, Object> doResolve(Map<String, Object> context) throws Exception {
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		
		String tatCalc = ParamUtil.getString(context, "tatCalc");
		
		try {
			
			if (UtilValidate.isEmpty(tatCalc) || "Y".equals(tatCalc)) {
				
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
}
