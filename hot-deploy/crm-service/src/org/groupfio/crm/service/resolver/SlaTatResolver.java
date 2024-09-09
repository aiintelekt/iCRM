package org.groupfio.crm.service.resolver;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.groupfio.crm.service.util.DataHelper;
import javolution.util.FastList;
import javolution.util.FastMap;
	
/**
 * @author Nishanth
 */
public class SlaTatResolver extends Resolver {

	private static final String MODULE = SlaTatResolver.class.getName();

	private static SlaTatResolver instance;
	
	public static synchronized SlaTatResolver getInstance(){
        if(instance == null) {
            instance = new SlaTatResolver();
        }
        return instance;
    }
	
	@Override
	protected Map<String, Object> doResolve(Map<String, Object> context) throws Exception {
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		
		String tatCalc = ParamUtil.getString(context, "tatCalc");
		
		try {
			if (tatCalc.equals("Y")) {
				String businessUnit = ParamUtil.getString(context, "businessUnit");
				Timestamp createdDate = (Timestamp) context.get("createdDate");
				Timestamp closedDate = (Timestamp) context.get("closedDate");
				long tatDays = 0;
				if (UtilValidate.isNotEmpty(createdDate) && UtilValidate.isNotEmpty(closedDate)) {
					Debug.log("createdDate--------"+createdDate);
					createdDate = UtilDateTime.getDayStart(createdDate);
					closedDate = UtilDateTime.getDayStart(closedDate);
					if (closedDate.after(createdDate)) {
						Map<String, Object> validateMap = new HashMap<String, Object>();
						LocalDateTime startDateTime = createdDate.toLocalDateTime();
			            validateMap.put("delegator", delegator);
			            validateMap.put("startDateTime", startDateTime);
			            validateMap.put("businessUnit", businessUnit);
			            List<LocalDate> holidayList = DataHelper.getHolidays(validateMap);
			            validateMap.put("holidayList", holidayList);
			            LocalDateTime closedDateTime = closedDate.toLocalDateTime();
			            if(closedDateTime.isAfter(startDateTime)) {
			            	context.put("createdDate", Timestamp.valueOf(startDateTime));
				            context.put("closedDate", Timestamp.valueOf(closedDateTime));
				            Map<String, Object> tatContext = calculateSlaTat(context);
				            if(tatContext != null) {
				            	response.put("tatDays", ParamUtil.getLong(tatContext, "tatDays"));
				            }else {
				            	response.put("tatDays", tatDays);
				            }
			            }
					}else if(createdDate.compareTo(closedDate)==0){
						response.put("tatDays", tatDays);
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
	@SuppressWarnings("unchecked")
	private static Map<String, Object> calculateSlaTat(Map<String, Object> context) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		
		String statusId = (String) context.get("statusId");
		String custRequestId = (String) context.get("custRequestId");
		int totalDays =0;
		try {
			Map<String,Object> tatByDay = org.groupfio.common.portal.util.DataHelper.getSrTatCountByHst(context);
			Map<String,Object> tatDaysByHistory = new HashMap<String,Object>();
			if (UtilValidate.isNotEmpty(tatByDay)) {
				tatDaysByHistory = (Map<String, Object>) tatByDay.get("tatDaysByHistory");
			}
			
			if (UtilValidate.isNotEmpty(tatDaysByHistory)) {
				 for (Entry<String, Object> entry: tatDaysByHistory.entrySet()) {
					 int count = (int) entry.getValue();
					 if (UtilValidate.isNotEmpty(totalDays)) {
						 totalDays = totalDays+count;
					 }
				 }
			}
			Timestamp createdDate =null;
			Timestamp closedDate = (Timestamp) context.get("closedDate");
			if (UtilValidate.isNotEmpty(statusId)) {
				String slaTatPauseStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_PAUSE_STATUS");
				List slaTatPauseStatusList = DataUtil.stringToList(slaTatPauseStatus, ",");
				String slaTatStopStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_STOP_STATUS");
				List slaTatStopStatusList = DataUtil.stringToList(slaTatStopStatus, ",");
				List slaTatNoCountList = FastList.newInstance();
				if (UtilValidate.isNotEmpty(slaTatPauseStatusList)) {
					slaTatNoCountList.addAll(slaTatPauseStatusList);
				}
				if (UtilValidate.isNotEmpty(slaTatStopStatusList)) {
					slaTatNoCountList.addAll(slaTatStopStatusList);
				}
				if (UtilValidate.isNotEmpty(slaTatStopStatus) && slaTatStopStatus.contains(statusId)) {
					GenericValue custRequestHistory = EntityQuery.use(delegator).from("CustRequestHistory").where("custRequestId", custRequestId).orderBy("-createdStamp").queryFirst();
					if (UtilValidate.isNotEmpty(custRequestHistory)) {
						createdDate = custRequestHistory.getTimestamp("createdStamp");
						String prevStatus = custRequestHistory.getString("statusId");
						Debug.log("-prevStatus--"+prevStatus);
						long days = 0;
						if ((UtilValidate.isEmpty(slaTatNoCountList) || UtilValidate.isEmpty(prevStatus)) || (UtilValidate.isNotEmpty(slaTatNoCountList) && !slaTatNoCountList.contains(prevStatus))) {
							Debug.log("-prevStatus--sss"+prevStatus);
							if (UtilValidate.isNotEmpty(createdDate) && UtilValidate.isNotEmpty(closedDate)) {
								Map cxtMap = FastMap.newInstance();
								 cxtMap.put("delegator", delegator);
								 cxtMap.put("createdDateTime", UtilDateTime.getDayStart(createdDate).toLocalDateTime());
								 cxtMap.put("closedDateTime", UtilDateTime.getDayStart(closedDate).toLocalDateTime());
								 days = DataHelper.countBusinessDays(cxtMap);
								 Debug.log("-prevStatus--sssss"+prevStatus);
							}
							if (UtilValidate.isNotEmpty(totalDays) && UtilValidate.isNotEmpty(days)) {
								totalDays = (int) (totalDays+days);
							}
						}
					}
					
					
				}
					
			}
			result.put("tatDays", Long.valueOf(totalDays));
			
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	public static int prepareTatToDate(Map<String, Object> context) {
		return org.groupfio.common.portal.util.DataHelper.prepareTatToDate(context);
	}
}
