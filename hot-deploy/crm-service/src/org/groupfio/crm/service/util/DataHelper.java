/**
 * 
 */
package org.groupfio.crm.service.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.groupfio.crm.service.util.DataUtil;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 * @author Mahendran Thanasekaran
 */
public class DataHelper {
	
	private static String MODULE = DataHelper.class.getName();
	
	public static void createWorkEffortCommExtensionActivities(Delegator delegator, List<Map<String, Object>> extensionList, String workEffortId, String workExtName, 
			String wftExtType, String wftExtValue, String enumTypeId, boolean checkExistAndIgnore) {
		
		try {
			if (UtilValidate.isNotEmpty(extensionList)) {
				
				long sequence = new Date().getTime();
				
				for (Map<String, Object> extension : extensionList) {
					
					String value = ParamUtil.getString(extension, wftExtValue);
					String type = EnumUtil.getEnumId(delegator, ParamUtil.getString(extension, wftExtType), enumTypeId);
					
					if (UtilValidate.isNotEmpty(value) && UtilValidate.isNotEmpty(type)) {
						
						if (checkExistAndIgnore) {
							GenericValue commExtension = EntityUtil.getFirst( delegator.findByAnd("WorkEffortCommExtension", UtilMisc.toMap("workEffortId", workEffortId, "workExtName", workExtName, "wftExtType", type, "wftExtValue", value), null, false) );
							if (UtilValidate.isNotEmpty(commExtension)) {
			    				Debug.logError("WorkEffort commExtension already exists! workEffortId: "+workEffortId+", workExtName: "+workExtName+", wftExtType: "+type+", wftExtValue: "+value, MODULE);
			    				continue;
			    			}
						}
						
						GenericValue commExtension = delegator.makeValue("WorkEffortCommExtension");
						
						commExtension.put("workEffortId", workEffortId);
						commExtension.put("workExtSeqNum", sequence++);
						
						commExtension.put("workExtName", workExtName);
						commExtension.put("wftExtType", type);
						commExtension.put("wftExtValue", value);
						
						commExtension.create();
						
					}
					
				}
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		
	}
	public static void createWorkEffortCommExtension(Delegator delegator, List<String> extensionList, String workEffortId, String workExtName, 
			String wftExtType, String wftExtValue, String enumTypeId, boolean checkExistAndIgnore) {
		
		try {
			if (UtilValidate.isNotEmpty(extensionList)) {
				
				long sequence = new Date().getTime();
				
				for (String extension : extensionList) {
					String value = extension;//ParamUtil.getString(extension, wftExtValue);
					String type = EnumUtil.getEnumId(delegator, wftExtType, enumTypeId);
					if (UtilValidate.isNotEmpty(value) && UtilValidate.isNotEmpty(type)) {
						
						if (checkExistAndIgnore) {
							GenericValue commExtension = EntityUtil.getFirst( delegator.findByAnd("WorkEffortCommExtension", UtilMisc.toMap("workEffortId", workEffortId, "workExtName", workExtName, "wftExtType", type, "wftExtValue", value), null, false) );
							if (UtilValidate.isNotEmpty(commExtension)) {
			    				Debug.logError("WorkEffort commExtension already exists! workEffortId: "+workEffortId+", workExtName: "+workExtName+", wftExtType: "+type+", wftExtValue: "+value, MODULE);
			    				continue;
			    			}
						}
						
						GenericValue commExtension = delegator.makeValue("WorkEffortCommExtension");
						
						commExtension.put("workEffortId", workEffortId);
						commExtension.put("workExtSeqNum", sequence++);
						
						commExtension.put("workExtName", workExtName);
						commExtension.put("wftExtType", type);
						commExtension.put("wftExtValue", value);
						
						commExtension.create();
						
					}
					
				}
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		
	}
			
	
	public static List<LinkedHashMap> loadWorkEffortCommExtension(Delegator delegator, String workEffortId, String workExtName, 
			String wftExtType, String wftExtValue, String enumTypeId) {
		
		List<LinkedHashMap> extensionList = new ArrayList<LinkedHashMap>();
		
		try {
			if (UtilValidate.isNotEmpty(workEffortId) && UtilValidate.isNotEmpty(workExtName)) {
				
				List<GenericValue> commExtensionList = delegator.findByAnd("WorkEffortCommExtension", UtilMisc.toMap("workEffortId", workEffortId, "workExtName", workExtName), UtilMisc.toList("workExtSeqNum"), false);
				
				for (GenericValue commExtension : commExtensionList) {
					
					String value = commExtension.getString("wftExtValue");
					String type = EnumUtil.getEnumCode(delegator, commExtension.getString("wftExtType"), enumTypeId);
					
					LinkedHashMap<String, Object> extension = new LinkedHashMap<String, Object>();

					extension.put(wftExtValue, value);
					extension.put(wftExtType, type);
					
					extensionList.add(extension);
					
				}
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return extensionList;
	}
	
    public static String getDynamicColumnName(Delegator delegator, String objTypeId, String objParamName, String prefix) {
        String fieldName = "";
        try {
            if(UtilValidate.isNotEmpty(objTypeId) && UtilValidate.isNotEmpty(objParamName)) {
                GenericValue objFieldConfig = EntityQuery.use(delegator).select("objParamLocId").from("CustRequestObjectsConfig").where("objTypeId",objTypeId,"objParamName",objParamName).queryFirst();
                if(UtilValidate.isNotEmpty(objFieldConfig)) {
                    fieldName = (UtilValidate.isNotEmpty(prefix) ? prefix : "")+objFieldConfig.getString("objParamLocId");
                }
                fieldName = fieldName.trim();
            }
        } catch (Exception e) {
            
        }
        return fieldName;
    }
    
    public static void createWorkEffortAttendees(Delegator delegator, List<String> attendeeList, String workEffortId, String workExtType, String expectationEnumId) {
		
		try {
			if (UtilValidate.isNotEmpty(attendeeList)) {
				for (String eachAttendee : attendeeList) {
					if (UtilValidate.isNotEmpty(eachAttendee)) {
						GenericValue attendeeAssign = delegator.makeValue("WorkEffortPartyAssignment");
						attendeeAssign.put("workEffortId", workEffortId);
						attendeeAssign.put("partyId", eachAttendee);
						attendeeAssign.put("roleTypeId", org.fio.homeapps.util.PartyHelper.getPartyRoleTypeId(eachAttendee, delegator));
						attendeeAssign.put("statusId", "PRTYASGN_ASSIGNED");
						attendeeAssign.put("fromDate", UtilDateTime.nowTimestamp());
						attendeeAssign.put("expectationEnumId", expectationEnumId);
						attendeeAssign.create();
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
	}
    public static void updateWorkEffortAttendees(Delegator delegator, List<String> attendeeList, String workEffortId, String workExtType, String expectationEnumId) {
		
		try {
			if (UtilValidate.isNotEmpty(attendeeList)) {
				
				 List<EntityCondition> conditions = new ArrayList<EntityCondition>();
	                conditions.add(EntityCondition.makeCondition("workEffortId",EntityOperator.EQUALS,workEffortId));
	                //conditions.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,attendeeList));
	                conditions.add(EntityCondition.makeCondition("expectationEnumId",EntityOperator.EQUALS,expectationEnumId));
	                Debug.log("update conditions------"+conditions);
	                List<GenericValue> workEfforPartyAss = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(EntityCondition.makeCondition(conditions,EntityOperator.AND)).queryList();
	               Debug.log("update------"+workEfforPartyAss);
	                if(UtilValidate.isNotEmpty(workEfforPartyAss)) {
	                    //delegator.removeAll(workEfforPartyAss);
	                    
	                    
	                    for (GenericValue eachAttendee : workEfforPartyAss) {
	                    	/*GenericValue  partyAssign = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(eachAttendee.getPrimaryKey()).queryOne();
	                    	if(UtilValidate.isNotEmpty(partyAssign)){
	                    		partyAssign.remove();	                    	 	
	                    	}*/
	                    	eachAttendee.put("thruDate", UtilDateTime.nowTimestamp());
	                    	eachAttendee.store();
	                    }
	                }
	                createWorkEffortAttendees(delegator,attendeeList,workEffortId,workExtType,expectationEnumId);
				
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
	}
    
    public static List<LocalDate> getHolidays(Map<String, Object> context){
    	Delegator delegator = (Delegator) context.get("delegator"); 
    	String businessUnit = (String) context.get("businessUnit");
    	List<LocalDate> holidays = new ArrayList<>();
    	try {
    		List<EntityCondition> conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("status", EntityOperator.EQUALS, null),
							EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ACTIVE"))
	                	);
			
			/*
			if (UtilValidate.isNotEmpty(businessUnit)) {
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("businessUnit", EntityOperator.EQUALS, businessUnit),
						EntityCondition.makeCondition("businessUnit", EntityOperator.EQUALS, "99999")
		                ));
			} else {
				conditionList.add( EntityCondition.makeCondition("businessUnit", EntityOperator.EQUALS, "99999") );
			}
			*/
			
			
			//EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> holidayConfigList = EntityQuery.use(delegator).from("TechDataHolidayConfig").where(EntityCondition.makeCondition(conditionList, EntityOperator.AND)).distinct(true).cache(false).queryList();
	    	if(UtilValidate.isNotEmpty(holidayConfigList)) {
	    		for(GenericValue holidayConfig : holidayConfigList) {
	    			//Timestamp holidayDate = UtilDateTime.toTimestamp(holidayConfig.getDate("holidayDate"));
	    			java.sql.Date holidayDate = holidayConfig.getDate("holidayDate");
	    			holidays.add(holidayDate.toLocalDate());
	    		}
	    	}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
		}
		return holidays;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getBusinessDate(Map<String, Object> context) {
	    Delegator delegator = (Delegator) context.get("delegator");
	    LocalDateTime startDateTime = (LocalDateTime) context.get("startDateTime");
	    LocalTime businessStartTime = (LocalTime) context.get("businessStartTime");
	    LocalTime businessEndTime = (LocalTime) context.get("businessEndTime");
	    String businessUnit = (String) context.get("businessUnit");
	    boolean flag = UtilValidate.isNotEmpty(context.get("flag")) ? (boolean) context.get("flag") : true;
	    List<LocalDate> holidays =  (List<LocalDate>) context.get("holidayList");
	    Optional<List<LocalDate>> holidayList = Optional.of(holidays);
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		if(startDateTime != null) {
    	    	if(businessStartTime == null)
    	    		businessStartTime = LocalTime.of(9, 00);
    	    	if(businessEndTime == null)
    	    		businessEndTime = LocalTime.of(18, 00);
    	    	LocalDateTime startOfDay = startDateTime.with(businessStartTime);
    	    	LocalDateTime endOfDay = startDateTime.with(businessEndTime);
    	    
                //Optional<List<LocalDate>> holidayList = getHolidays(context);
                //Debug.log("holiday list--->"+holidayList, MODULE);
                //if we add hours we can calculate straight, if it's days then have to convert one day to hours and minutes
                Predicate<LocalDate> isHoliday = date -> holidayList.isPresent() ? holidayList.get().contains(date) : false;
                
                //Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;

                String defaultLoopLimit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_LOOP_LIMIT");
        		int loopLimit = UtilValidate.isNotEmpty(defaultLoopLimit) && DataUtil.isInteger(defaultLoopLimit) ? Integer.parseInt(defaultLoopLimit) : 100;
        		int count = 0;
                boolean validateCreateDate = true;
                while(validateCreateDate) {
                	long isItHoliday = Stream.iterate(startDateTime.toLocalDate(), date -> date.plusDays(1)).limit(1)
                            .filter(isHoliday).count();
                	
                	if(isItHoliday > 0 || startDateTime.isAfter(endOfDay) || (flag && startDateTime.isEqual(endOfDay))) {
                		startDateTime = startDateTime.plusDays(1).with(businessStartTime);
                    	startOfDay = startDateTime.with(businessStartTime);
                    	endOfDay = startDateTime.with(businessEndTime);
                    } else if(!flag && startDateTime.isEqual(endOfDay)) {
                    	validateCreateDate = false;
                    	if(startDateTime.isBefore(startOfDay))
                    		startDateTime = startDateTime.with(businessStartTime);
                    } else {
                    	validateCreateDate = false;
                    	if(startDateTime.isBefore(startOfDay))
                    		startDateTime = startDateTime.with(businessStartTime);
                    }
                	if(count > loopLimit) break;
                	count = count+1;
                }
                result.put("startDateTime", startDateTime);
                result.put("startOfDay", startOfDay);
                result.put("endOfDay", endOfDay);
    		}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
    	return result;
    }
    
    public static long countBusinessDays(Map<String, Object> context) {
    	long businessDays = 0l;
    	LocalDateTime startDate = (LocalDateTime) context.get("createdDateTime");
    	LocalDateTime endDate = (LocalDateTime) context.get("closedDateTime");
		try {
			List<LocalDate> holidays =  (List<LocalDate>) getHolidays(context);
		    Optional<List<LocalDate>> holidayList = Optional.of(holidays);
	        
	        if (startDate == null || endDate == null) {
	            throw new IllegalArgumentException("Invalid method argument(s) to countBusinessDaysBetween(" + startDate
	                    + "," + endDate + ")");
	        }
	 
	        Predicate<LocalDate> isHoliday = date -> holidayList.isPresent() ? holidayList.get().contains(date) : false;
	        //Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
	 
	        long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
	        
	        businessDays = Stream.iterate(startDate.toLocalDate(), date -> date.plusDays(1)).limit(daysBetween)
	                .filter(isHoliday.negate()).count();
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
        return businessDays;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getClosedWorkingDate(Map<String, Object> context) {
	    
    	Delegator delegator = (Delegator) context.get("delegator");
 	    LocalDateTime startDateTime = (LocalDateTime) context.get("startDateTime");
 	    LocalTime businessStartTime = (LocalTime) context.get("businessStartTime");
 	    LocalTime businessEndTime = (LocalTime) context.get("businessEndTime");
 	    String businessUnit = (String) context.get("businessUnit");
 	    boolean flag = UtilValidate.isNotEmpty(context.get("flag")) ? (boolean) context.get("flag") : true;
 	    List<LocalDate> holidays =  (List<LocalDate>) context.get("holidayList");
 	    Optional<List<LocalDate>> holidayList = Optional.of(holidays);
 	    int interval = (int) context.get("interval");
	    
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		if(startDateTime != null) {
    	    	if(businessStartTime == null)
    	    		businessStartTime = LocalTime.of(9, 00);
    	    	if(businessEndTime == null)
    	    		businessEndTime = LocalTime.of(18, 00);
    	    	LocalDateTime startOfDay = startDateTime.with(businessStartTime);
    	    	LocalDateTime endOfDay = startDateTime.with(businessEndTime);
    	    	
                //Optional<List<LocalDate>> holidayList = getHolidays(context);
                //Debug.log("holiday list--->"+holidayList, MODULE);
                //if we add hours we can calculate straight, if it's days then have to convert one day to hours and minutes
                Predicate<LocalDate> isHoliday = date -> holidayList.isPresent() ? holidayList.get().contains(date) : false;
                
                //Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;

                
                String defaultLoopLimit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_LOOP_LIMIT");
        		int loopLimit = UtilValidate.isNotEmpty(defaultLoopLimit) && DataUtil.isInteger(defaultLoopLimit) ? Integer.parseInt(defaultLoopLimit) : 100;
        		int count = 0;
                boolean validateCreateDate = true;
                while(validateCreateDate) {
					long isItHoliday = Stream.iterate(startDateTime.toLocalDate(), date -> date.plusDays(1)).limit(1).filter(isHoliday).count();

					if(isItHoliday > 0 || startDateTime.isBefore(startOfDay)) {
						startDateTime = startDateTime.plusDays(interval).with(businessEndTime);
						startOfDay = startDateTime.with(businessStartTime);
						endOfDay = startDateTime.with(businessEndTime);
					} else if(startDateTime.isEqual(endOfDay)) {
						validateCreateDate = false;
						//startDateTime = startDateTime.with(businessEndTime);
					} else {
						validateCreateDate = false;
						if(startDateTime.isAfter(endOfDay))
							startDateTime = startDateTime.with(businessEndTime);
					}
					if(count > loopLimit) break;
					count = count+1;
				}
                result.put("startDateTime", startDateTime);
                result.put("startOfDay", startOfDay);
                result.put("endOfDay", endOfDay);
    		}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getPreEscalWorkingDate(Map<String, Object> context) {

    	Delegator delegator = (Delegator) context.get("delegator");
		LocalDateTime startDateTime = (LocalDateTime) context.get("startDateTime");
		LocalTime businessStartTime = (LocalTime) context.get("startTime");
		LocalTime businessEndTime = (LocalTime) context.get("endTime");
		LocalTime remainTime = (LocalTime) context.get("remainTime");
		String businessUnit = (String) context.get("businessUnit");
		boolean flag = context.get("flag") != null ? (boolean) context.get("flag") : true;
		int interval = (int) context.get("interval");
		List<LocalDate> holidays =  (List<LocalDate>) context.get("holidayList");
		Optional<List<LocalDate>> holidayList = Optional.of(holidays);

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(UtilValidate.isNotEmpty(startDateTime)) {
				if(businessStartTime == null)
					businessStartTime = LocalTime.of(9, 00);
				if(businessEndTime == null)
					businessEndTime = LocalTime.of(18, 00);
				LocalDateTime startOfDay = startDateTime.with(businessStartTime);
				LocalDateTime endOfDay = startDateTime.with(businessEndTime);

				Predicate<LocalDate> isHoliday = date -> holidayList.isPresent() ? holidayList.get().contains(date) : false;

				//Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;

				String defaultLoopLimit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_LOOP_LIMIT");
        		int loopLimit = UtilValidate.isNotEmpty(defaultLoopLimit) && DataUtil.isInteger(defaultLoopLimit) ? Integer.parseInt(defaultLoopLimit) : 100;
        		int count = 0;
				boolean validateCreateDate = true;
				while(validateCreateDate) {
					long isItHoliday = Stream.iterate(startDateTime.toLocalDate(), date -> date.plusDays(1)).limit(1)
							.filter(isHoliday).count();

					if(isItHoliday > 0 || (startDateTime.isBefore(startOfDay) || startDateTime.isAfter(endOfDay))) {
						startDateTime = startDateTime.plusDays(interval).with(remainTime);
						startOfDay = startDateTime.with(businessStartTime);
						endOfDay = startDateTime.with(businessEndTime);
					} else if(!flag && startDateTime.isEqual(endOfDay)) {
						validateCreateDate = false;
						if(startDateTime.isAfter(startOfDay))
							startDateTime = startDateTime.with(remainTime);
					} else {
						validateCreateDate = false;
					}
					if(count > loopLimit) break;
					count = count+1;
				}
				result.put("startDateTime", startDateTime);
				result.put("startOfDay", startOfDay);
				result.put("endOfDay", endOfDay);
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		return result;
	}
	
    public static Map<String, Object> getTotalHours(String slaPeriod, String slaPeriodValue, long hours, long minutes) {
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
    
    /**
     * 
     * @param context
     * @return
     * From Sr history get the pause days based on status and no. of days in particular status
     * 
     */
    @SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> getSrPauseDates(Map<String, Object> context) {
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDateTime startDateTime = (LocalDateTime) context.get("startDateTime");
		String custRequestId = (String) context.get("custRequestId");
		Map<String, Object> result = new HashMap<String, Object>();
		String slaTatPauseStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_PAUSE_STATUS");
		List slaTatPauseStatusList = org.fio.homeapps.util.DataUtil.stringToList(slaTatPauseStatus, ",");
		String slaTatStopStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_STOP_STATUS");
		List slaTatStopStatusList = org.fio.homeapps.util.DataUtil.stringToList(slaTatStopStatus, ",");
		List<LocalDate> tatPauseDates = FastList.newInstance();
		List<LocalDate> workingTatDays = FastList.newInstance();
		try {
			slaTatPauseStatusList.add("SR_OPEN");
			if (UtilValidate.isNotEmpty(slaTatStopStatusList) && UtilValidate.isNotEmpty(slaTatPauseStatusList)) {
				slaTatPauseStatusList.addAll(slaTatStopStatusList);
			}
			Timestamp reopenedDate = getSrReopenDate(delegator, custRequestId);
			if (UtilValidate.isNotEmpty(reopenedDate)) {
				reopenedDate = UtilDateTime.getDayStart(reopenedDate);
			}
			boolean includePauseDays = false;
			if (UtilValidate.isNotEmpty(slaTatPauseStatusList)) {
				List<EntityCondition> conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				Set<String> fieldToSelect = new HashSet<String>();
				fieldToSelect.add("custRequestId");
				fieldToSelect.add("statusId");
				fieldToSelect.add("createdDate");
				fieldToSelect.add("closedByDate");
				fieldToSelect.add("createdStamp");
				List<GenericValue> custRequestHistory = delegator.findList("CustRequestHistory", cond, fieldToSelect,
						UtilMisc.toList("createdStamp"), null, false);
				Timestamp prevPauseDate = null;
				if (UtilValidate.isNotEmpty(custRequestHistory)) {
					for (GenericValue history : custRequestHistory) {
						String statusId = history.getString("statusId");
						Timestamp createdDate = history.getTimestamp("createdDate");
						Timestamp closedByDate = history.getTimestamp("closedByDate");
						Timestamp createdStamp = history.getTimestamp("createdStamp");
						if (UtilValidate.isNotEmpty(createdStamp)) {
							createdStamp = UtilDateTime.getDayStart(createdStamp);
							boolean count = false;
							
							if (UtilValidate.isNotEmpty(reopenedDate) && reopenedDate.compareTo(createdStamp)==0 && statusId.equalsIgnoreCase("SR_OPEN")) {
								count = true;
								includePauseDays = false;
							}
							if (slaTatPauseStatusList.contains(statusId) && !count) {
								if (UtilValidate.isNotEmpty(workingTatDays) && workingTatDays.contains(new java.sql.Date(createdStamp.getTime()).toLocalDate())) {
									// Dont Include as Pause if same day has schedule and paused/completed
								}else {
									tatPauseDates.add(new java.sql.Date(createdStamp.getTime()).toLocalDate());
								}
								prevPauseDate = createdStamp;
								includePauseDays = true;
							}else {
								workingTatDays.add(new java.sql.Date(createdStamp.getTime()).toLocalDate());
								if (UtilValidate.isNotEmpty(prevPauseDate) && prevPauseDate.compareTo(createdStamp) != 0 && includePauseDays) {
									int pausedDays = UtilDateTime.getIntervalInDays(prevPauseDate, createdStamp);
								//	Debug.log("prevPauseDate--------" + prevPauseDate + "==createdStamp========"+ createdStamp+"pausedDays---"+pausedDays);
									if (UtilValidate.isNotEmpty(pausedDays) && pausedDays > 0) {
										for (int i = 1; i < pausedDays; i++) {
											Timestamp pausedDate = UtilDateTime.addDaysToTimestamp(prevPauseDate, i);
										//	Debug.log("pausedDays--------|" + pausedDate);
											tatPauseDates.add(new java.sql.Date(pausedDate.getTime()).toLocalDate());
										}
								}
								if (UtilValidate.isNotEmpty(tatPauseDates) && tatPauseDates.contains(new java.sql.Date(createdStamp.getTime()).toLocalDate())) {
									tatPauseDates.remove(new java.sql.Date(createdStamp.getTime()).toLocalDate());
								}
								includePauseDays = false;
								prevPauseDate = createdStamp;
							}
							}
						}
						if (UtilValidate.isNotEmpty(tatPauseDates) && UtilValidate.isNotEmpty(workingTatDays)) {
							tatPauseDates.removeAll(workingTatDays);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		result.put("tatPauseDates", tatPauseDates);
		result.put("tatPauseDatesCount", tatPauseDates.size());
		
		result.put("workingTatDays", workingTatDays);
		result.put("workingTatDaysCount", workingTatDays.size());
		return result;
	}
    
    public static Timestamp getSrReopenDate(Delegator delegator,String custRequestId) {
    	Timestamp reopenedDate=null;
    	try {
    		GenericValue custRequest = delegator.findOne("CustRequest",UtilMisc.toMap("custRequestId", custRequestId), false);
    		if (UtilValidate.isNotEmpty(custRequest)) {
    			reopenedDate = custRequest.getTimestamp("reopenedDate");
    		}
    	}catch (Exception e) {
			// TODO: handle exception
		}
    	return reopenedDate;
    }
}
