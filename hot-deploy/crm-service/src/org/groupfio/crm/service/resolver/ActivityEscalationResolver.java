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
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.crm.service.CrmServiceConstants.SlaSetupConstants;
import org.groupfio.crm.service.util.DataHelper;
import org.groupfio.crm.service.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

import javolution.util.FastList;

public class ActivityEscalationResolver extends Resolver {
	private static final String MODULE = ActivityEscalationResolver.class.getName();

	private static ActivityEscalationResolver instance;
	
	public static synchronized ActivityEscalationResolver getInstance(){
        if(instance == null) {
            instance = new ActivityEscalationResolver();
        }
        return instance;
    }
	@Override
	protected Map<String, Object> doResolve(Map<String, Object> context) throws Exception {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			
			if (UtilValidate.isNotEmpty(context)) {
				
				Delegator delegator = (Delegator) context.get("delegator");
				
				String escalationLevel = ParamUtil.getString(context, "escalationLevel");
				String workEffortTypeId = ParamUtil.getString(context, "workEffortTypeId");
				String businessUnit = ParamUtil.getString(context, "businessUnit");
				Timestamp createdDate = (Timestamp) context.get("createdDate");
				String domainEntityType = ParamUtil.getString(context, "domainEntityType");
				
				List<EntityCondition> conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, workEffortTypeId),
						EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ACTIVE")
		                ));
				if(UtilValidate.isNotEmpty(domainEntityType))
					conditionList.add(EntityCondition.makeCondition("workEffortAssocId", EntityOperator.EQUALS, domainEntityType));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue slaConfig = EntityQuery.use(delegator).from("WorkEffortSlaConfig").where(mainConditons).queryFirst();
				
				
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
	            	Debug.log("Business Date--->"+startDateTime);
	            }
	            Map<String, Object> contextMap = new HashMap<String, Object>();
	            contextMap.put("delegator", delegator);
	            contextMap.put("startTime", startTime);
	            contextMap.put("endTime", endTime);
	            contextMap.put("holidayList", holidayList);
				LocalDateTime dueDate = null;
				LocalDateTime preEscalDate = null;
				LocalDateTime firstEscalDate = null;
				LocalDateTime secEscalDate = null;
				LocalDateTime thirdEscalDate = null;
				if (UtilValidate.isNotEmpty(slaConfig)) {
					String isSlaRequired = slaConfig.getString("isSlaRequired");
					if(!"N".equals(isSlaRequired)) {
						// calculate the due date
						String slaPeriodLvl = slaConfig.getString("slaPeriodLvl");
						String slaPeriodUnit = slaConfig.getString("slaPeriodUnit");
						if(UtilValidate.isNotEmpty(slaPeriodLvl) && UtilValidate.isNotEmpty(slaPeriodUnit)) {
							Map<String, Object> hoursDetails = DataHelper.getTotalHours(slaPeriodLvl, slaPeriodUnit, hours, minutes);
							Map<String, Object> ctx = new HashMap<String, Object>();
							ctx.putAll(contextMap);
							ctx.put("inHours", hoursDetails.get("totalHours"));
							ctx.put("inMinutes", hoursDetails.get("remainMinutes"));
							ctx.put("businessStartDate", Timestamp.valueOf(startDateTime));
							Map<String, Object> dueDateMap = calculateDate(ctx);
							if(UtilValidate.isNotEmpty(dueDateMap)) {
								dueDate = (LocalDateTime) dueDateMap.get("finalDate");
							}
							Debug.log("Due Date--->"+dueDate, MODULE);
							
						
							String preEscalLvl = slaConfig.getString("slaPreEscLvl");
							String preEscalPeriodUnit = slaConfig.getString("slaPreEscPeriod");
							if(UtilValidate.isEmpty(preEscalLvl))
								preEscalLvl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_PER_SLA");
							if(UtilValidate.isEmpty(preEscalPeriodUnit))
								preEscalPeriodUnit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_PER_SLA_UNIT");
							Debug.log("preEscalLvl--->"+preEscalLvl+"---preEscalPeriodUnit--->"+preEscalPeriodUnit,MODULE);
							if(UtilValidate.isNotEmpty(preEscalLvl) && UtilValidate.isNotEmpty(preEscalPeriodUnit)) {
								validateMap.put("startDateTime", dueDate);
					            businessDate = DataHelper.getBusinessDate(validateMap);
					            if(businessDate != null) {
					            	startDateTime =  (LocalDateTime) businessDate.get("startDateTime");
					            }
								hoursDetails = DataHelper.getTotalHours(preEscalLvl, preEscalPeriodUnit, hours, minutes);
								ctx = new HashMap<String, Object>();
								ctx.putAll(contextMap);
								ctx.put("inHours", hoursDetails.get("totalHours"));
								ctx.put("inMinutes", hoursDetails.get("remainMinutes"));
								ctx.put("businessStartDate", Timestamp.valueOf(startDateTime));
								Map<String, Object> preEscalDateMap = calculatePreEscalDate(ctx);
								
								if(UtilValidate.isNotEmpty(preEscalDateMap)) {
									preEscalDate = (LocalDateTime) preEscalDateMap.get("finalDate");
									Debug.log("--pre--esca--date-->"+preEscalDate,MODULE);
								}
							} 
							
							
							String firstEscalLvl = slaConfig.getString("slaEscPeriodLvl1");
							String firstEscalPeriodUnit = slaConfig.getString("slaEscPeriodUnit1");
							Debug.log("firstEscalLvl--->"+firstEscalLvl+"---firstEscalPeriodUnit--->"+firstEscalPeriodUnit,MODULE);
							if(UtilValidate.isNotEmpty(firstEscalLvl) && UtilValidate.isNotEmpty(firstEscalPeriodUnit)) {
								validateMap.put("startDateTime", dueDate);
					            businessDate = DataHelper.getBusinessDate(validateMap);
					            if(businessDate != null) {
					            	startDateTime =  (LocalDateTime) businessDate.get("startDateTime");
					            }
								hoursDetails = DataHelper.getTotalHours(firstEscalLvl, firstEscalPeriodUnit, hours, minutes);
								ctx = new HashMap<String, Object>();
								ctx.putAll(contextMap);
								ctx.put("inHours", hoursDetails.get("totalHours"));
								ctx.put("inMinutes", hoursDetails.get("remainMinutes"));
								ctx.put("businessStartDate", Timestamp.valueOf(startDateTime));
								Map<String, Object> firstEscalDateMap = calculateDate(ctx);
								
								if(UtilValidate.isNotEmpty(firstEscalDateMap)) {
									firstEscalDate = (LocalDateTime) firstEscalDateMap.get("finalDate");
									Debug.log("--1st--esca--Date-->"+firstEscalDate,MODULE);
								}
							}
							
							String secEscalLvl = slaConfig.getString("slaEscPeriodLvl2");
							String secEscalPeriodUnit = slaConfig.getString("slaEscPeriodUnit2");
							Debug.log("secEscalLvl--->"+secEscalLvl+"---secEscalPeriodUnit--->"+secEscalPeriodUnit, MODULE);
							if(UtilValidate.isNotEmpty(secEscalLvl) && UtilValidate.isNotEmpty(secEscalPeriodUnit)) {
								validateMap.put("startDateTime", dueDate);
					            businessDate = DataHelper.getBusinessDate(validateMap);
					            if(businessDate != null) {
					            	startDateTime =  (LocalDateTime) businessDate.get("startDateTime");
					            }
								hoursDetails = DataHelper.getTotalHours(secEscalLvl, secEscalPeriodUnit, hours, minutes);
								ctx = new HashMap<String, Object>();
								ctx.putAll(contextMap);
								ctx.put("inHours", hoursDetails.get("totalHours"));
								ctx.put("inMinutes", hoursDetails.get("remainMinutes"));
								ctx.put("businessStartDate", Timestamp.valueOf(startDateTime));
								Map<String, Object> secEscalDateMap = calculateDate(ctx);
								
								if(UtilValidate.isNotEmpty(secEscalDateMap)) {
									secEscalDate = (LocalDateTime) secEscalDateMap.get("finalDate");
									Debug.log("--2nd--esca--Date-->"+secEscalDate,MODULE);
								}
							}
							
							String thirdEscalLvl = slaConfig.getString("slaEscPeriodLvl3");
							String thirdEscalPeriodUnit = slaConfig.getString("slaEscPeriodUnit3");
							Debug.log("thirdEscalLvl--->"+thirdEscalLvl+"---thirdEscalPeriodUnit--->"+thirdEscalPeriodUnit,MODULE);
							if(UtilValidate.isNotEmpty(thirdEscalLvl) && UtilValidate.isNotEmpty(thirdEscalPeriodUnit)) {
								validateMap.put("startDateTime", dueDate);
					            businessDate = DataHelper.getBusinessDate(validateMap);
					            if(businessDate != null) {
					            	startDateTime =  (LocalDateTime) businessDate.get("startDateTime");
					            }
								hoursDetails = DataHelper.getTotalHours(thirdEscalLvl, thirdEscalPeriodUnit, hours, minutes);
								ctx = new HashMap<String, Object>();
								ctx.putAll(contextMap);
								ctx.put("inHours", hoursDetails.get("totalHours"));
								ctx.put("inMinutes", hoursDetails.get("remainMinutes"));
								ctx.put("businessStartDate", Timestamp.valueOf(startDateTime));
								Map<String, Object> thirdEscalDateMap = calculateDate(ctx);
								
								if(UtilValidate.isNotEmpty(thirdEscalDateMap)) {
									thirdEscalDate = (LocalDateTime) thirdEscalDateMap.get("finalDate");
									Debug.log("--3rd--esca--Date-->"+thirdEscalDate,MODULE);
								}
							}
						}
						response.put("escalationLevel", escalationLevel);
					}
				} else {
					GenericValue defaultSla = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId","DEFAULT_ACTIVITY_SLA").queryFirst();
					String slaPeriodLvl = SlaSetupConstants.SLA_PERIOD_DAYS;
					String slaPeriodUnit = UtilValidate.isNotEmpty(defaultSla) ? defaultSla.getString("value") : "";
					if(UtilValidate.isNotEmpty(defaultSla)) {
						slaPeriodLvl = defaultSla.getString("description");
						slaPeriodUnit = defaultSla.getString("value");
					}
					if(UtilValidate.isNotEmpty(slaPeriodLvl) && UtilValidate.isNotEmpty(slaPeriodUnit)) {
						Debug.log("Default SLA --"+slaPeriodUnit+"--slaPeriodLvl--"+slaPeriodLvl, MODULE);
						Map<String, Object> hoursDetails = DataHelper.getTotalHours(slaPeriodLvl, slaPeriodUnit, hours, minutes);
						Map<String, Object> ctx = new HashMap<String, Object>();
						ctx.putAll(contextMap);
						ctx.put("inHours", hoursDetails.get("totalHours"));
						ctx.put("inMinutes", hoursDetails.get("remainMinutes"));
						ctx.put("businessStartDate", Timestamp.valueOf(startDateTime));
						Map<String, Object> dueDateMap = calculateDate(ctx);
						if(UtilValidate.isNotEmpty(dueDateMap)) {
							dueDate = (LocalDateTime) dueDateMap.get("finalDate");
						}
					}
				}
				
				response.put("commitDate", UtilValidate.isNotEmpty(dueDate) ? Timestamp.valueOf(dueDate) : null);
				response.put("_pre_escalation_date", UtilValidate.isNotEmpty(preEscalDate) ? Timestamp.valueOf(preEscalDate) : null);
				response.put("_escalation_date_1", UtilValidate.isNotEmpty(firstEscalDate) ? Timestamp.valueOf(firstEscalDate) : null);
				response.put("_escalation_date_2", UtilValidate.isNotEmpty(secEscalDate) ? Timestamp.valueOf(secEscalDate) : null);
				response.put("_escalation_date_3", UtilValidate.isNotEmpty(thirdEscalDate) ? Timestamp.valueOf(thirdEscalDate) : null);
			}
			response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
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
        		int loopLimit = UtilValidate.isNotEmpty(defaultLoopLimit) && DataUtil.isInteger(defaultLoopLimit) ? Integer.parseInt(defaultLoopLimit) : 100;
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
	
	@SuppressWarnings("unchecked")
	private static Map<String, Object> calculatePreEscalDate(Map<String, Object> context){
    	Delegator delegator = (Delegator) context.get("delegator");
		Timestamp businessStartDate = (Timestamp) context.get("businessStartDate"); 
		long inHours = context.get("inHours") != null ? (long) context.get("inHours") : 0l; 
		long inMinutes = context.get("inMinutes") != null ? (long) context.get("inMinutes") : 0l; 
		if(inHours<0) {
			inHours = Math.abs(inHours);
		}
		LocalTime businessStartTime = (LocalTime) context.get("startTime");
		LocalTime businessEndTime = (LocalTime) context.get("endTime"); 
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		List<LocalDate> holidayList =  (List<LocalDate>) context.get("holidayList");
    		if(UtilValidate.isNotEmpty(businessStartDate)) {
    			
    			LocalDateTime startDateTime = businessStartDate.toLocalDateTime();
    			if(businessStartTime == null)
    	    		businessStartTime = LocalTime.of(9, 00);
    	    	if(businessEndTime == null)
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
        		int loopLimit = UtilValidate.isNotEmpty(defaultLoopLimit) && DataUtil.isInteger(defaultLoopLimit) ? Integer.parseInt(defaultLoopLimit) : 100;
        		int count = 0;
    			while(flag) {
    				LocalTime remainTime = businessEndTime;
    				LocalDateTime tempDateTime = LocalDateTime.from(startOfDay);
    	            long hours = tempDateTime.until( startDateTime, ChronoUnit.HOURS );
    	            tempDateTime = tempDateTime.plusHours( hours );
    	            long minutes = tempDateTime.until( startDateTime, ChronoUnit.MINUTES );
                	if(hours > 0 || minutes > 0) {
                		if(slaHours >= hours)
                			slaHours = (int) (slaHours - hours);
                		
                 		if(slaHours>0 && minutes >0)
                 			slaMinutes = (int) (slaMinutes-minutes);
                 		
                 		if(slaMinutes < 0) {
                 			slaHours = slaHours -1;
                 			slaMinutes = SlaSetupConstants.TOTAL_MINUTES+slaMinutes;
                 		}
                 		//Debug.log("hours------>"+hours+"----- minutes---->"+minutes , MODULE);
                 		//Debug.log("slahours------>"+slaHours+"-----sla minutes---->"+slaMinutes, MODULE);
                 		if((inHours >= 0 && inHours <= hours && inMinutes <= minutes)){
                    		startDateTime = startDateTime.minusHours(inHours).minusMinutes(inMinutes);			
                    		flag = false;
                    	} else if(slaHours > totalWorkingHours || (slaHours == totalWorkingHours && slaMinutes > 0)) {
                			startDateTime = startDateTime.minusDays(1).with(businessEndTime);
                    	} else {
                    		startDateTime = startDateTime.minusDays(1).with(businessEndTime);
                    		remainTime = businessEndTime.minusHours(slaHours).minusMinutes(slaMinutes);
                    		flag = false;
                    	}
                		Debug.log("pre date---->"+startDateTime, MODULE);
                 		Map<String, Object> validateMap = new HashMap<String, Object>();
                		//LocalDateTime startDateTime = createdDate;
                		validateMap.put("startDateTime", startDateTime);
                		validateMap.put("startTime", businessStartTime);
                		validateMap.put("endTime", businessEndTime);
                		validateMap.put("holidayList", holidayList);
                		validateMap.put("interval", -1);
                		validateMap.put("remainTime", remainTime);
                		validateMap.put("flag", flag);
                		Map<String, Object>  businessDateResult = DataHelper.getPreEscalWorkingDate(validateMap);
                        if(businessDateResult != null) {
                        	startDateTime =  (LocalDateTime) businessDateResult.get("startDateTime");
                        	startOfDay =  (LocalDateTime) businessDateResult.get("startOfDay");
                        	endOfDay =  (LocalDateTime) businessDateResult.get("endOfDay");
                        } 
                        //Debug.log("after pre date---->"+startDateTime, MODULE);
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

}
