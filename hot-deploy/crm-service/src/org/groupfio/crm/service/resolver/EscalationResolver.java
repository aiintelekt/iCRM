/**
 * 
 */
package org.groupfio.crm.service.resolver;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.crm.service.CrmServiceConstants.SlaSetupConstants;
import org.groupfio.crm.service.util.DataHelper;
import org.groupfio.crm.service.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
	
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
		
		//EntityManager entityManager = (EntityManager) context.get("entityManager");
		
		//String consolidateLvl = ParamUtil.getString(context, "consolidateLvl");
		
		try {
			
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get("escalationLevel"))) {
				
				Delegator delegator = (Delegator) context.get("delegator");
				
				String escalationLevel = ParamUtil.getString(context, "escalationLevel");
				String typeId = ParamUtil.getString(context, "typeId");
				String categoryId = ParamUtil.getString(context, "categoryId");
				String subCategoryId = ParamUtil.getString(context, "subCategoryId");
				String priority = ParamUtil.getString(context, "priority");
				String businessUnit = ParamUtil.getString(context, "businessUnit");
				Timestamp createdDate = (Timestamp) context.get("createdDate");
				String isCalculateCommitDate = ParamUtil.getString(context, "isCalculateCommitDate");
				
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
	            	//Debug.log("Business Date--->"+startDateTime);
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
				
				
				if (UtilValidate.isNotEmpty(isCalculateCommitDate) && isCalculateCommitDate.equals("Y")) {
				
					List<EntityCondition> conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("srTypeId", EntityOperator.EQUALS, typeId),
							EntityCondition.makeCondition("srCategoryId", EntityOperator.EQUALS, categoryId),
							EntityCondition.makeCondition("srSubCategoryId", EntityOperator.EQUALS, subCategoryId),	
							EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ACTIVE")
			                ));
					
					if (UtilValidate.isNotEmpty(priority)) {
						conditionList.add( EntityCondition.makeCondition("srPriority", EntityOperator.EQUALS, priority) );
					}
					
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					GenericValue slaConfig = EntityUtil.getFirst( delegator.findList("SrSlaConfig", mainConditons, null, null, null, false) );
					if (UtilValidate.isEmpty(slaConfig)) {
						conditionList = FastList.newInstance();
						conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("srTypeId", EntityOperator.EQUALS, typeId),
								EntityCondition.makeCondition("srCategoryId", EntityOperator.EQUALS, categoryId),
								EntityCondition.makeCondition(EntityOperator.OR, 
										EntityCondition.makeCondition("srSubCategoryId", EntityOperator.EQUALS, null),
										EntityCondition.makeCondition("srSubCategoryId", EntityOperator.EQUALS, ""),
										EntityCondition.makeCondition("srSubCategoryId", EntityOperator.EQUALS, "NA")),
								EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ACTIVE")
				                ));
						
						if (UtilValidate.isNotEmpty(priority)) {
							conditionList.add( EntityCondition.makeCondition("srPriority", EntityOperator.EQUALS, priority) );
						}
						mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						slaConfig = EntityUtil.getFirst( delegator.findList("SrSlaConfig", mainConditons, null, null, null, false) );
					}
					
					if (UtilValidate.isEmpty(slaConfig)) {
						conditionList = FastList.newInstance();
						conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("srTypeId", EntityOperator.EQUALS, typeId),
								EntityCondition.makeCondition(EntityOperator.OR, 
										EntityCondition.makeCondition("srCategoryId", EntityOperator.EQUALS, null),
										EntityCondition.makeCondition("srCategoryId", EntityOperator.EQUALS, ""),
										EntityCondition.makeCondition("srCategoryId", EntityOperator.EQUALS, "NA")),
								EntityCondition.makeCondition(EntityOperator.OR, 
										EntityCondition.makeCondition("srSubCategoryId", EntityOperator.EQUALS, null),
										EntityCondition.makeCondition("srSubCategoryId", EntityOperator.EQUALS, ""),
										EntityCondition.makeCondition("srSubCategoryId", EntityOperator.EQUALS, "NA")),
								EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ACTIVE")
				                ));
						
						if (UtilValidate.isNotEmpty(priority)) {
							conditionList.add( EntityCondition.makeCondition("srPriority", EntityOperator.EQUALS, priority) );
						}
						mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						slaConfig = EntityUtil.getFirst( delegator.findList("SrSlaConfig", mainConditons, null, null, null, false) );
					}
					
					if (UtilValidate.isNotEmpty(slaConfig)) {
						String isSlaRequired = slaConfig.getString("isSlaRequired");
						if(!"N".equals(isSlaRequired)) {
							// calculate the due date
							String slaPeriodLvl = slaConfig.getString("slaPeriodLvl");
							String srPeriodUnit = slaConfig.getString("srPeriodUnit");
							if(UtilValidate.isNotEmpty(slaPeriodLvl) && UtilValidate.isNotEmpty(srPeriodUnit)) {
								Map<String, Object> hoursDetails = getTotalHours(slaPeriodLvl, srPeriodUnit, hours, minutes);
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
									preEscalLvl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFT_SR_PER_SLA");
								if(UtilValidate.isEmpty(preEscalPeriodUnit))
									preEscalPeriodUnit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFT_SR_PER_SLA_UNIT");
								Debug.log("preEscalLvl--->"+preEscalLvl+"---preEscalPeriodUnit--->"+preEscalPeriodUnit,MODULE);
								if(UtilValidate.isNotEmpty(preEscalLvl) && UtilValidate.isNotEmpty(preEscalPeriodUnit)) {
									validateMap.put("startDateTime", dueDate);
						            businessDate = DataHelper.getBusinessDate(validateMap);
						            if(businessDate != null) {
						            	startDateTime =  (LocalDateTime) businessDate.get("startDateTime");
						            }
									hoursDetails = getTotalHours(preEscalLvl, preEscalPeriodUnit, hours, minutes);
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
								
								
								String firstEscalLvl = slaConfig.getString("slaPeriodLvl1");
								int firstEscalPeriodUnit = slaConfig.getInteger("slaEscPeriodHrsLvl1");
								Debug.log("firstEscalLvl--->"+firstEscalLvl+"---firstEscalPeriodUnit--->"+firstEscalPeriodUnit,MODULE);
								if(UtilValidate.isNotEmpty(firstEscalLvl) && UtilValidate.isNotEmpty(firstEscalPeriodUnit)) {
									validateMap.put("startDateTime", dueDate);
						            businessDate = DataHelper.getBusinessDate(validateMap);
						            if(businessDate != null) {
						            	startDateTime =  (LocalDateTime) businessDate.get("startDateTime");
						            }
									hoursDetails = getTotalHours(firstEscalLvl, Integer.toString(firstEscalPeriodUnit), hours, minutes);
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
								
								String secEscalLvl = slaConfig.getString("slaPeriodLvl2");
								int secEscalPeriodUnit = slaConfig.getInteger("slaEscPeriodHrsLvl2");
								Debug.log("secEscalLvl--->"+secEscalLvl+"---secEscalPeriodUnit--->"+secEscalPeriodUnit, MODULE);
								if(UtilValidate.isNotEmpty(secEscalLvl) && UtilValidate.isNotEmpty(secEscalPeriodUnit)) {
									validateMap.put("startDateTime", dueDate);
						            businessDate = DataHelper.getBusinessDate(validateMap);
						            if(businessDate != null) {
						            	startDateTime =  (LocalDateTime) businessDate.get("startDateTime");
						            }
									hoursDetails = getTotalHours(secEscalLvl, Integer.toString(secEscalPeriodUnit), hours, minutes);
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
								
								String thirdEscalLvl = slaConfig.getString("slaPeriodLvl3");
								int thirdEscalPeriodUnit = slaConfig.getInteger("slaEscPeriodHrsLvl3");
								Debug.log("thirdEscalLvl--->"+thirdEscalLvl+"---thirdEscalPeriodUnit--->"+thirdEscalPeriodUnit,MODULE);
								if(UtilValidate.isNotEmpty(thirdEscalLvl) && UtilValidate.isNotEmpty(thirdEscalPeriodUnit)) {
									validateMap.put("startDateTime", dueDate);
						            businessDate = DataHelper.getBusinessDate(validateMap);
						            if(businessDate != null) {
						            	startDateTime =  (LocalDateTime) businessDate.get("startDateTime");
						            }
									hoursDetails = getTotalHours(thirdEscalLvl, Integer.toString(thirdEscalPeriodUnit), hours, minutes);
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
							
							/*
							if (UtilValidate.isNotEmpty(slaPeriodLvl) && UtilValidate.isNotEmpty(srPeriodUnit)) {
								
								createdDate = prepareCreatedDate(delegator, createdDate, businessUnit);
								
								Timestamp commitDate = null;
								int periodHrsLvl = Integer.parseInt(srPeriodUnit);
								
								Map<String, Object> dateContext = new LinkedHashMap<String, Object>();
								
								dateContext.put("delegator", delegator);
								
								dateContext.put("businessUnit", businessUnit);
								dateContext.put("createdDate", createdDate);
								dateContext.put("periodLvl", slaPeriodLvl);
								dateContext.put("periodHrsLvl", periodHrsLvl);
								dateContext.put("counter", 0);
								dateContext.put("interval", 0);
								dateContext.put("createdDateMinute", UtilDateTime.getMinute(createdDate, TimeZone.getDefault(), Locale.getDefault()));
								
								Map<String, Object> dateResult = getCommitDate(dateContext);
								
								commitDate = (Timestamp) dateResult.get("commitDate");
								
								response.put("escalationLevel", escalationLevel);
								response.put("commitDate", commitDate);
								
							}
							
							*/
							response.put("escalationLevel", escalationLevel);
						}
					} else {
						//String defaultSlaDay = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_SR_SLA");
						GenericValue defaultSla = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId","DEFAULT_SR_SLA").queryFirst();
						String slaPeriodLvl = SlaSetupConstants.SLA_PERIOD_DAYS;
						String srPeriodUnit = UtilValidate.isNotEmpty(defaultSla) ? defaultSla.getString("value") : "2";
						if(UtilValidate.isNotEmpty(defaultSla)) {
							slaPeriodLvl = defaultSla.getString("description");
							srPeriodUnit = defaultSla.getString("value");
						}
						if(UtilValidate.isNotEmpty(slaPeriodLvl) && UtilValidate.isNotEmpty(srPeriodUnit)) {
							Debug.log("Default SLA --"+srPeriodUnit+"--slaPeriodLvl--"+slaPeriodLvl, MODULE);
							Map<String, Object> hoursDetails = getTotalHours(slaPeriodLvl, srPeriodUnit, hours, minutes);
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
				
				// calculate SR status escalation date [start]
				String statusId = (String) context.get("statusId");
				if (UtilValidate.isNotEmpty(statusId)) {
					LocalDateTime statusEscTime = getStatusEscTime(delegator, statusId, contextMap, startDateTime, hours, minutes);
					response.put("statusEscTime", UtilValidate.isNotEmpty(statusEscTime) ? Timestamp.valueOf(statusEscTime) : null);
				}
				if (UtilValidate.isEmpty(context.get("statusClosedEscTime"))) {
					LocalDateTime statusEscTime = getStatusEscTime(delegator, "SR_CLOSED", contextMap, startDateTime, hours, minutes);
					response.put("statusClosedEscTime", UtilValidate.isNotEmpty(statusEscTime) ? Timestamp.valueOf(statusEscTime) : null);
				}
				// calculate SR status escalation date [end]
				
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
	/*
	public static Map<String, Object> getCommitDate(Map<String, Object> context) {
    	
    	try {
    		
    		Delegator delegator = (Delegator) context.get("delegator");
    		
    		Timestamp commitDate = null;
    		
    		Timestamp createdDate = (Timestamp) context.get("createdDate");
    		String businessUnit = ParamUtil.getString(context, "businessUnit");
    		String periodLvl = ParamUtil.getString(context, "periodLvl");
    		int periodHrsLvl = ParamUtil.getInteger(context, "periodHrsLvl");
    		int counter = ParamUtil.getInteger(context, "counter");
    		int interval = ParamUtil.getInteger(context, "interval");
    		int createdDateMinute = ParamUtil.getInteger(context, "createdDateMinute");
    		
    		if (counter != periodHrsLvl) {
    			
    			if (periodLvl.equals("Days")) { // Renaming Day(s) to Days
                    commitDate = UtilDateTime.addDaysToTimestamp(createdDate, ++interval);
                } else if (periodLvl.equals("Hours")) { // Renaming the Hour(s) to Hours
					
					int month = UtilDateTime.getMonth(createdDate, TimeZone.getDefault(), Locale.getDefault());
					int day = UtilDateTime.getDayOfMonth(createdDate, TimeZone.getDefault(), Locale.getDefault());
					int year = UtilDateTime.getYear(createdDate, TimeZone.getDefault(), Locale.getDefault());
					
					month = month + 1;
					
					int hour = UtilDateTime.getHour(createdDate, TimeZone.getDefault(), Locale.getDefault());
					int minute = UtilDateTime.getMinute(createdDate, TimeZone.getDefault(), Locale.getDefault());
					
					int remainHour = 17 - hour - interval;
					
					if (remainHour != 0) {
						commitDate = UtilDateTime.addHoursToTimestamp(createdDate, ++interval);
					} else {
						interval = 0;
						createdDate = UtilDateTime.toTimestamp(month, day+1, year, 9, 0, 0);		
						commitDate = UtilDateTime.addHoursToTimestamp(createdDate, interval);
						context.put("isNextDay", true);
					}
					
				}
    			
				GenericValue holidayConfig = getHolidayConfig(delegator, commitDate, businessUnit);
				
				if (UtilValidate.isEmpty(holidayConfig)) {
					counter++;
				}
    			
				context.put("interval", interval);
				context.put("createdDate", createdDate);
				context.put("commitDate", commitDate);
    		} else {
    			
    			if (periodLvl.equals("Hours") && UtilValidate.isNotEmpty(context.get("isNextDay"))) {
    				commitDate = UtilDateTime.addMinutesToTimestamp((Timestamp) context.get("commitDate"), createdDateMinute);
    				context.put("commitDate", commitDate);
    			}
    			
    			return context;
    		}
    		
    		context.put("counter", counter);
    		
    		getCommitDate(context);
    		
    	} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		
		return null;
	}
	*/
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
				if(UtilValidate.isNotEmpty(slaPeriodValue) && DataUtil.isInteger(slaPeriodValue)) {
					slaHours = Long.parseLong(slaPeriodValue);
				}
				totalHours = slaHours * hours;
				long totalMinutes = slaHours * minutes;
				
				int minuteHours = (int) (totalMinutes / 60);
				remainMinutes = (int) (totalMinutes % 60);
				totalHours = totalHours + minuteHours;
			} else if("Hours".equals(slaPeriod)) {
				totalHours = UtilValidate.isNotEmpty(slaPeriodValue) && DataUtil.isInteger(slaPeriodValue) ? Long.parseLong(slaPeriodValue) : 0l;
			}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
    	result.put("totalHours", totalHours);
    	result.put("remainMinutes", remainMinutes);
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
                 		Debug.log("hours------>"+hours+"----- minutes---->"+minutes , MODULE);
                 		Debug.log("slahours------>"+slaHours+"-----sla minutes---->"+slaMinutes, MODULE);
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
                        Debug.log("after pre date---->"+startDateTime, MODULE);
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
    
    private static LocalDateTime getStatusEscTime (Delegator delegator, String statusId, Map contextMap, LocalDateTime startDateTime, long hours, long minutes) {
    	try {
			GenericValue emailConfig = EntityQuery.use(delegator).from("CustRequestEmailConfig").where("statusId",statusId).queryFirst();
			if (UtilValidate.isNotEmpty(emailConfig)) {
				Map<String, Object> hoursDetails = getTotalHours("Hours", emailConfig.getString("escalationTime"), hours, minutes);
				Map<String, Object> ctx = new HashMap<String, Object>();
				ctx.putAll(contextMap);
				ctx.put("inHours", hoursDetails.get("totalHours"));
				ctx.put("inMinutes", hoursDetails.get("remainMinutes"));
				ctx.put("businessStartDate", Timestamp.valueOf(startDateTime));
				Map<String, Object> dueDateMap = calculateDate(ctx);
				if(UtilValidate.isNotEmpty(dueDateMap)) {
					LocalDateTime statusEscTime = (LocalDateTime) dueDateMap.get("finalDate");
					return statusEscTime;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
	
}
