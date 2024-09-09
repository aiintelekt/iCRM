/**
 * 
 */
package org.groupfio.common.portal.resolver;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.admin.portal.util.DataHelper;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.common.portal.CommonPortalConstants.SlaSetupConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
	
/**
 * @author Sharif
 * @author Mahendran Thanasekaran
 */
public class EscalationResolver extends Resolver {
	
	private static final String MODULE = EscalationResolver.class.getName();

	private static EscalationResolver instance;
	
	public static synchronized EscalationResolver getInstance(){
        if(instance == null) {
            instance = new EscalationResolver();
        }
        return instance;
    }
	
	@Override
	protected Map<String, Object> doResolve(Map<String, Object> context) throws Exception {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get("escalationLevel"))) {
				Delegator delegator = (Delegator) context.get("delegator");
				
				String businessUnit = ParamUtil.getString(context, "businessUnit");
				Timestamp createdDate = (Timestamp) context.get("createdDate");
				
				String periodType = ParamUtil.getString(context, "periodType");
				String periodValue = ParamUtil.getString(context, "periodValue");
				
				String workStartHour = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_START_TIME");
				String workEndHour = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_END_TIME");
				LocalTime startTime = LocalTime.parse(UtilValidate.isNotEmpty(workStartHour)? workStartHour : "09:00");
				LocalTime endTime = LocalTime.parse(UtilValidate.isNotEmpty(workEndHour) ? workEndHour : "18:00");
				Map<String, Object> validateMap = new HashMap<String, Object>();
				//total working hours and minutes
				LocalTime tempDateTime = LocalTime.from(startTime);
	            long hours = tempDateTime.until( endTime, ChronoUnit.HOURS );
	            tempDateTime = tempDateTime.plusHours( hours );
	            long minutes = tempDateTime.until( endTime, ChronoUnit.MINUTES );
	            
	            LocalDateTime startDateTime = createdDate.toLocalDateTime();
	            validateMap.put("delegator", delegator);
	            validateMap.put("startDateTime", startDateTime);
	            validateMap.put("businessStartTime", startTime);
	            validateMap.put("businessEndTime", endTime);
	            validateMap.put("businessUnit", businessUnit);
	            
	            List<LocalDate> holidayList = DataHelper.getHolidays(validateMap);
	            validateMap.put("holidayList", holidayList);
	            Map<String, Object>  businessDate = DataHelper.getBusinessDate(validateMap);
	            if(businessDate != null) {
	            	startDateTime =  (LocalDateTime) businessDate.get("startDateTime");
	            }
	            Map<String, Object> contextMap = new HashMap<String, Object>();
	            contextMap.put("delegator", delegator);
	            contextMap.put("startTime", startTime);
	            contextMap.put("endTime", endTime);
	            contextMap.put("holidayList", holidayList);
	            
	            Map<String, Object> hoursDetails = getTotalHours(periodType, periodValue, hours, minutes);
				Map<String, Object> ctx = new HashMap<String, Object>();
				ctx.putAll(contextMap);
				ctx.put("inHours", hoursDetails.get("totalHours"));
				ctx.put("inMinutes", hoursDetails.get("remainMinutes"));
				ctx.put("businessStartDate", Timestamp.valueOf(startDateTime));
				Map<String, Object> dueDateMap = calculateDate(ctx);
				LocalDateTime escalationTime = null;
				if(UtilValidate.isNotEmpty(dueDateMap)) {
					escalationTime = (LocalDateTime) dueDateMap.get("finalDate");
				}
				
				response.put("escalationTime", UtilValidate.isNotEmpty(escalationTime) ? Timestamp.valueOf(escalationTime) : null);
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
	
	private static Map<String, Object> calculateDate(Map<String, Object> context){
		Delegator delegator = (Delegator) context.get("delegator");
		Timestamp businessStartDate = (Timestamp) context.get("businessStartDate"); 
		long inHours = context.get("inHours") != null ? (long) context.get("inHours") : 0l; 
		long inMinutes = context.get("inMinutes") != null ? (long) context.get("inMinutes") : 0l; 
		LocalTime businessStartTime = (LocalTime) context.get("startTime");
		LocalTime businessEndTime = (LocalTime) context.get("endTime"); 
		String action = (String) context.get("action");
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		@SuppressWarnings("unchecked")
			List<LocalDate> holidayList =  (List<LocalDate>) context.get("holidayList");
    		if(UtilValidate.isNotEmpty(businessStartDate) && (UtilValidate.isNotEmpty(inHours) || UtilValidate.isNotEmpty(inMinutes))) {
    			LocalDateTime startDateTime = businessStartDate.toLocalDateTime().truncatedTo(ChronoUnit.MINUTES);
    			if(UtilValidate.isEmpty(businessStartTime))
    	    		businessStartTime = LocalTime.of(9, 00);
    	    	if(UtilValidate.isEmpty(businessEndTime))
    	    		businessEndTime = LocalTime.of(18, 00);
    	    	LocalDateTime startOfDay = startDateTime.with(businessStartTime);
    	    	LocalDateTime endOfDay = startDateTime.with(businessEndTime);
    	    	
    	    	// calculate total working hours and minutes
        		LocalDateTime workingDay = LocalDateTime.from(startOfDay);
        		long totalWorkingHours = workingDay.until(endOfDay, ChronoUnit.HOURS);
        		workingDay = workingDay.plusHours(totalWorkingHours);
        		long totalWorkingMinutes = workingDay.until( endOfDay, ChronoUnit.MINUTES );
            	
        		boolean flag = true;
        		int slaHours = (int) inHours;
        		int slaMinutes = (int) inMinutes;
        		if(slaMinutes>0 && slaMinutes == SlaSetupConstants.TOTAL_MINUTES) {
        			slaHours = slaHours+1; slaMinutes=0;
        		}
        		String defaultLoopLimit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_LOOP_LIMIT");
        		int loopLimit = UtilValidate.isNotEmpty(defaultLoopLimit) && ParamUtil.isInteger(defaultLoopLimit) ? Integer.parseInt(defaultLoopLimit) : 100;
        		int count = 0;
    			while(flag) {
    				LocalDateTime tempDateTime = LocalDateTime.from(startDateTime);
    	            long hours = tempDateTime.until( endOfDay, ChronoUnit.HOURS );
    	            tempDateTime = tempDateTime.plusHours( hours );
    	            long minutes = tempDateTime.until( endOfDay, ChronoUnit.MINUTES );
                	if(hours > 0 || minutes > 0) {
                		if(slaHours >= hours)
                			slaHours = (int) (slaHours - hours);
                		
                 		if(slaHours>0 && minutes >0)
                 			slaMinutes = (int) (slaMinutes-minutes);
                 		
                 		if(slaMinutes < 0) {
                 			slaHours = slaHours -1;
                 			slaMinutes = SlaSetupConstants.TOTAL_MINUTES+slaMinutes;
                 		}
                 		
                 		if((inHours >= 0 && inHours <= hours && inMinutes <= minutes)){
                    		startDateTime = startDateTime.plusHours(inHours).plusMinutes(inMinutes);			
                    		flag = false;
                    	} else if(slaHours > totalWorkingHours || (slaHours == totalWorkingHours && slaMinutes > 0)) {
                			startDateTime = startDateTime.plusDays(1).with(businessStartTime);
                    	} else {
                    		startDateTime = startDateTime.plusDays(1).with(businessStartTime);
                    		businessStartTime = businessStartTime.plusHours(slaHours).plusMinutes(slaMinutes);
                    		flag = false;
                    	}
                 		Map<String, Object> validateMap = new HashMap<String, Object>();
                 		validateMap.put("startDateTime", startDateTime);
                 		validateMap.put("businessStartTime", businessStartTime);
                 		validateMap.put("businessEndTime", businessEndTime);
                 		validateMap.put("flag", flag);
                 		validateMap.put("holidayList", holidayList);
                 		//System.out.println("validateMap---->"+validateMap);
                		Map<String, Object>  businessDateResult = DataHelper.getBusinessDate(validateMap);
                        if(UtilValidate.isNotEmpty(businessDateResult)) {
                        	startDateTime =  (LocalDateTime) businessDateResult.get("startDateTime");
                        	startOfDay =  (LocalDateTime) businessDateResult.get("startOfDay");
                        	endOfDay =  (LocalDateTime) businessDateResult.get("endOfDay");
                        }
                	} else {
                		flag= false;
                	}
                	if(count > loopLimit) break;
                	count = count +1;
    			}
    			result.put("finalDate", startDateTime);
    		}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
		}
    	return result;
    }
    
    private static Map<String, Object> getTotalHours(String slaPeriod, String slaPeriodValue, long hours, long minutes) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	long totalHours = 0l;
    	long remainMinutes = 0l;
    	try {
			if("Days".equals(slaPeriod)) {
				long slaHours = 0l;
				if(UtilValidate.isNotEmpty(slaPeriodValue) && ParamUtil.isInteger(slaPeriodValue)) {
					slaHours = Long.parseLong(slaPeriodValue);
				}
				totalHours = slaHours * hours;
				long totalMinutes = slaHours * minutes;
				
				int minuteHours = (int) (totalMinutes / 60);
				remainMinutes = (int) (totalMinutes % 60);
				totalHours = totalHours + minuteHours;
			} else if("Hours".equals(slaPeriod)) {
				totalHours = UtilValidate.isNotEmpty(slaPeriodValue) && ParamUtil.isInteger(slaPeriodValue) ? Long.parseLong(slaPeriodValue) : 0l;
			}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
    	result.put("totalHours", totalHours);
    	result.put("remainMinutes", remainMinutes);
    	return result;
    }
    
}
