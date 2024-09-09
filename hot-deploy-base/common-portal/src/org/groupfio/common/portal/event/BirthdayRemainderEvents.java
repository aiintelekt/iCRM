package org.groupfio.common.portal.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.ServiceUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BirthdayRemainderEvents {
	private BirthdayRemainderEvents() {
	}

	private static final String MODULE = BirthdayRemainderEvents.class.getName();

	public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
		return doJSONResponse(response, jsonObject.toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
		return doJSONResponse(response, JSONArray.fromObject(collection).toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Map map) {
		return doJSONResponse(response, JSONObject.fromObject(map));
	}

	public static String doJSONResponse(HttpServletResponse response, String jsonString) {
		String result = "success";

		response.setContentType("application/x-json");
		try {
			response.setContentLength(jsonString.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
			Debug.logWarning(
					"Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(),
					MODULE);
			response.setContentLength(jsonString.length());
		}

		Writer out;
		try {
			out = response.getWriter();
			out.write(jsonString);
			out.flush();
		} catch (IOException e) {
			Debug.logError(e, "Failed to get response writer", MODULE);
			result = "error";
		}
		return result;
	}

	public static GenericValue getUserLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (GenericValue) session.getAttribute("userLogin");
	}
	private static final String DISPLAY_BIRTHDAY_LIST_VIEW_REMINDER = "displayBirthdayListViewReminder";

	public static boolean canDisplayBirthdayReminder(Delegator delegator, String partyId){
		boolean canDisplayBirthdayModal = false;
		Date birthDate = null;
		try{
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId",partyId),false);
			if(UtilValidate.isNotEmpty(person)){
				birthDate = person.getDate("birthDate");
				if(UtilValidate.isNotEmpty(birthDate)){
					if(!isReminderBlocked(delegator, partyId)){
						long days = daysForBirthday(delegator, partyId);
						if(days <= 20 && days >= 0)
							canDisplayBirthdayModal = true;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return canDisplayBirthdayModal;
	}

	public static long daysForBirthday(Delegator delegator, String partyId){
		Date birthDate = null;
		try{
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId",partyId),false);
			if(UtilValidate.isNotEmpty(person)){
				birthDate = person.getDate("birthDate");

				if(UtilValidate.isNotEmpty(birthDate)){
					if(!isReminderBlocked(delegator, partyId)){

						//taking the calendar instance based on the customer timezone
						Calendar calendar = getCalendarInstanceByPartyTimeZone(delegator, partyId);

						int currentYear  = calendar.get(Calendar.YEAR);
						int currentMonth = calendar.get(Calendar.MONTH)+1;
						int currentDay   = calendar.get(Calendar.DAY_OF_MONTH);

						/*
						 * take the day and month of the birthday and use current year
						 * 
						 * if day already past in this year add 1 in the year
						 * 
						 * check the days and return true if the days between 20 to 0
						 * 
						 */
						//Debug.logInfo("birthDate: " + birthDate.toString(), MODULE);

						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

						int birthDay = birthDate.getDate();
						int birthMonth = birthDate.getMonth();
						Date birthdate = format.parse(currentYear+"-"+(birthMonth+1)+"-"+birthDay);
						Date nowDate = format.parse(currentYear+"-"+currentMonth+"-"+currentDay);

						if(birthdate.compareTo(nowDate)<0){
							//used this since setYear going crazy
							birthdate = format.parse((currentYear+1)+"-"+(birthMonth+1)+"-"+birthDay);
						}

						long diff = birthdate.getTime() - nowDate.getTime();
						long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

						return days;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return -1;
	}

	public static Calendar getCalendarInstanceByPartyTimeZone(Delegator delegator, String partyId){
		Calendar calendar = Calendar.getInstance();
		TimeZone timeZone = null;
		String localTimeZone = null;
		String timeZoneString = null;

		try{
			GenericValue person = delegator.findOne("Person",UtilMisc.toMap("partyId", partyId),false);
			if(UtilValidate.isNotEmpty(person)){
				localTimeZone = person.getString("localTimeZone");
				if(UtilValidate.isNotEmpty(localTimeZone)){

					EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("enumId",localTimeZone),
							EntityCondition.makeCondition("enumCode",localTimeZone)),
							EntityOperator.OR);
					GenericValue enumeration = EntityUtil.getFirst(delegator.findList("Enumeration", entityCondition, null, UtilMisc.toList("enumCode"), null, false));

					if(UtilValidate.isNotEmpty(enumeration))
						timeZoneString = enumeration.getString("enumCode");

					if(UtilValidate.isNotEmpty(timeZoneString)){
						timeZone = TimeZone.getTimeZone(timeZoneString);
					}

					if(UtilValidate.isNotEmpty(timeZone))
						calendar = Calendar.getInstance(timeZone);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return calendar;
	}

	public static boolean isReminderBlocked(Delegator delegator, String partyId){
		try{
			if(UtilValidate.isNotEmpty(partyId)){
				long year = getCalendarInstanceByPartyTimeZone(delegator, partyId).get(Calendar.YEAR);
				GenericValue birthdayReminderFinished = delegator.findOne("PartyAttribute",UtilMisc.toMap("partyId",partyId,"attrName","BDAY_REMDR_FINISD"),false);
				if(UtilValidate.isNotEmpty(birthdayReminderFinished)){
					if(UtilValidate.isNotEmpty(birthdayReminderFinished.getString("attrValue")) && birthdayReminderFinished.getString("attrValue").equals(year+""))
						return true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static String blockBirthdayReminder(HttpServletRequest request, HttpServletResponse response){
		String partyId = request.getParameter("partyId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map resp = FastMap.newInstance();
		try{
			if(UtilValidate.isNotEmpty(partyId)){
				GenericValue party = delegator.findOne("Party",UtilMisc.toMap("partyId", partyId),false);
				if(UtilValidate.isNotEmpty(party)){

					long year = getCalendarInstanceByPartyTimeZone(delegator, partyId).get(Calendar.YEAR);
					GenericValue birthdayReminder = delegator.findOne("PartyAttribute",UtilMisc.toMap("partyId",partyId,"attrName","BDAY_REMDR_FINISD"),false);

					if(UtilValidate.isEmpty(birthdayReminder))
						birthdayReminder = delegator.makeValue("PartyAttribute",UtilMisc.toMap("partyId",partyId,"attrName","BDAY_REMDR_FINISD"));
					birthdayReminder.set("attrValue", year+"");
					delegator.createOrStore(birthdayReminder);
					resp.put("response", "success");
				}else {

				}
			}else {
				resp.put("response", "error");
				resp.put("errorMessage", "partyId is empty");
			}
		}catch(Exception e){
			e.printStackTrace();
			resp.put("response", "error");
			resp.put("errorMessage", e.getMessage());
		}

		return doJSONResponse(response, resp);
	}
	@SuppressWarnings("rawtypes")
	public static List birthdayReminders(Delegator delegator, GenericValue userLogin){
		List<Map> birthdayReminders = FastList.newInstance();
		try{
			if(UtilValidate.isNotEmpty(userLogin)){
				String partyId = userLogin.getString("partyId");
				if(UtilValidate.isNotEmpty(partyId)){
					EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("partyIdTo",partyId),
							EntityCondition.makeCondition("roleTypeIdTo","CUST_SERVICE_REP"),
							EntityCondition.makeCondition("partyRelationshipTypeId","RESPONSIBLE_FOR"),
							EntityCondition.makeConditionDate("fromDate", "thruDate")),
							EntityOperator.AND);
					List< GenericValue > customers = delegator.findList("PartyRelationship", condition,null, null, null, false);
					List<String> parties = EntityUtil.getFieldListFromEntityList(customers, "partyIdFrom", true);
					EntityCondition notEmptyBirthdayCondition = EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("birthDate",EntityOperator.NOT_EQUAL,null),
							EntityCondition.makeCondition("partyId",EntityOperator.IN,parties)),
							EntityOperator.AND);
					List< GenericValue > customersWithBirthdateList = delegator.findList("Person", notEmptyBirthdayCondition,null, null, null, false);

					for(GenericValue customer : customersWithBirthdateList){
						Map map = FastMap.newInstance();
						String customerPartyId = customer.getString("partyId");
						long remainingBirthdayDays = daysForBirthday(delegator, customerPartyId);
						if(remainingBirthdayDays >= 0 && remainingBirthdayDays <= 20){
							String name = PartyHelper.getPartyName(delegator, customerPartyId, false);
							map.put("partyId", customerPartyId);
							map.put("name", name);
							map.put("remainingDays", remainingBirthdayDays);

							if(remainingBirthdayDays >1)
								map.put("message", "There are "+remainingBirthdayDays+" days more for "+name+"'s birthday!!!");
							else if(remainingBirthdayDays == 1)
								map.put("message", "Tomorrow is "+name+"'s birthday!!!");
							else
								map.put("message", "Today is "+name+"'s birthday!!!");
							birthdayReminders.add(map);
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return birthdayReminders;
	}

	public static boolean isBirthdayReminderListHavingToShow(Delegator delegator, GenericValue userLogin){
		try{
			if(UtilValidate.isNotEmpty(userLogin)){
				String partyId = userLogin.getString("partyId");
				if(UtilValidate.isNotEmpty(partyId)){
					EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("partyIdTo",partyId),
							EntityCondition.makeCondition("roleTypeIdTo","CUST_SERVICE_REP"),
							EntityCondition.makeCondition("partyRelationshipTypeId","RESPONSIBLE_FOR"),
							EntityCondition.makeConditionDate("fromDate", "thruDate")),
							EntityOperator.AND);
					List< GenericValue > customers = delegator.findList("PartyRelationship", condition,null, null, null, false);
					if(UtilValidate.isNotEmpty(customers)) {
						List<String> parties = EntityUtil.getFieldListFromEntityList(customers, "partyIdFrom", true);
						EntityCondition notEmptyBirthdayCondition = EntityCondition.makeCondition(UtilMisc.toList(
								EntityCondition.makeCondition("partyId",EntityOperator.IN,parties),
								EntityCondition.makeCondition("birthDate",EntityOperator.NOT_EQUAL,null)),
								EntityOperator.AND);
						List< GenericValue > customersWithBirthdateList = delegator.findList("Person", notEmptyBirthdayCondition,null, null, null, false);
						if(UtilValidate.isNotEmpty(customersWithBirthdateList)) {
							for(GenericValue customer : customersWithBirthdateList){
								String customerPartyId = customer.getString("partyId");
								long remainingBirthdayDays = daysForBirthday(delegator, customerPartyId);
								if(remainingBirthdayDays >= 0 && remainingBirthdayDays <= 20){
									return true;
								}
							}
						}else {
							return false;
						}
					}else {
						return false;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	public static String getBirthdayRemainder(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List< GenericValue > customersWithBirthdateList = new ArrayList<>();
		String externalLoginKey = (String)context.get("externalLoginKey");
		try {
			if(UtilValidate.isNotEmpty(userLogin)){
				String partyId = userLogin.getString("partyId");
				if(UtilValidate.isNotEmpty(partyId)){
					EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("partyIdTo",partyId),
							EntityCondition.makeCondition("roleTypeIdTo","CUST_SERVICE_REP"),
							EntityCondition.makeCondition("partyRelationshipTypeId","RESPONSIBLE_FOR"),
							EntityCondition.makeConditionDate("fromDate", "thruDate")),
							EntityOperator.AND);
					List< GenericValue > customers = delegator.findList("PartyRelationship", condition,null, null, null, false);
					List<String> parties = EntityUtil.getFieldListFromEntityList(customers, "partyIdFrom", true);
					Debug.logInfo("===============parties============="+customers, MODULE);
					EntityCondition notEmptyBirthdayCondition = EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("birthDate",EntityOperator.NOT_EQUAL,null),
							EntityCondition.makeCondition("partyId",EntityOperator.IN,parties)),
							EntityOperator.AND);
					customersWithBirthdateList = delegator.findList("Person", notEmptyBirthdayCondition,null, null, null, false);
					Debug.logInfo("===============customersWithBirthdateList============="+customersWithBirthdateList, MODULE);
				}
			}else {
				result.put("data", new ArrayList<Map<String, Object>>());
			}
			int fioGridFetch = org.groupfio.common.portal.util.DataUtil.defaultFioGridfetchLimit(delegator);;
			int viewSize = fioGridFetch;
			int highIndex = 0;
			int lowIndex = 0;
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) request.getParameter("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}
			result.put("viewIndex", Integer.valueOf(viewIndex));
			lowIndex = viewIndex * viewSize + 1;
			highIndex = (viewIndex + 1) * viewSize;

			result.put("viewIndex", Integer.valueOf(viewIndex));
			result.put("highIndex", Integer.valueOf(highIndex));
			result.put("lowIndex", Integer.valueOf(lowIndex));
			result.put("viewSize", viewSize);
			if(UtilValidate.isNotEmpty(customersWithBirthdateList))
				for(GenericValue customer : customersWithBirthdateList){
					Map<String, Object> data = new HashMap<String, Object>();
	
					String customerPartyId = customer.getString("partyId");
					long remainingBirthdayDays = daysForBirthday(delegator, customerPartyId);
					Debug.logInfo("===============remainingBirthdayDays============="+remainingBirthdayDays, MODULE);
					if(remainingBirthdayDays >= 0 && remainingBirthdayDays <= 20){
						String name = PartyHelper.getPartyName(delegator, customerPartyId, false);
						data.put("partyId", customerPartyId);
						data.put("externalLoginKey", externalLoginKey);
						data.put("customerName", name);
						data.put("remainingDays", remainingBirthdayDays);
						if(remainingBirthdayDays >1)
							data.put("message", "There are "+remainingBirthdayDays+" days more for "+name+"'s birthday!!!");
						else if(remainingBirthdayDays == 1)
							data.put("message", "Tomorrow is "+name+"'s birthday!!!");
						else
							data.put("message", "Today is "+name+"'s birthday!!!");
						dataList.add(data);
					}
				}
			result.put("data", dataList);
			result.put("responseMessage", "success");
		}catch (Exception e) {
			e.printStackTrace();
			result.put("data", dataList);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, result);
	}
	public static String setUserLoggedIn(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try{
			updateBirthdayListViewReminder(delegator, userLogin, "Y");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "success";
	}
	public static String setUserViewedBirthdayListViewReminder(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map resp = UtilMisc.toMap("result", "error");
		try{
			if("success".equals(updateBirthdayListViewReminder(delegator, userLogin, "N")))
				resp.put("result", "success");
		}catch(Exception e){
			e.printStackTrace();
		}
		return doJSONResponse(response, resp);
	}
	public static String updateBirthdayListViewReminder(Delegator delegator, GenericValue userLogin, String value){
		try{
			if(UtilValidate.isNotEmpty(userLogin)){
				String partyId = userLogin.getString("partyId");
				if(UtilValidate.isNotEmpty(partyId)){
					GenericValue partyAttribute = delegator.findOne("PartyAttribute",UtilMisc.toMap("partyId", partyId, "attrName", DISPLAY_BIRTHDAY_LIST_VIEW_REMINDER),false);
					if(UtilValidate.isEmpty(partyAttribute))
						partyAttribute = delegator.makeValue("PartyAttribute",UtilMisc.toMap("partyId", partyId, "attrName", DISPLAY_BIRTHDAY_LIST_VIEW_REMINDER));
					partyAttribute.set("attrValue", value);
					delegator.createOrStore(partyAttribute);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "success";
	}
	public static String checkWhetherShowBirthdayListViewReminder(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map resp = UtilMisc.toMap("displayPopup", "NO");
		try{
			if(displayBirthdayListViewReminder(delegator, userLogin))
				resp.put("displayPopup", "YES");
		}catch(Exception e){
			e.printStackTrace();
		}
		return doJSONResponse(response, resp);
	}
	public static boolean displayBirthdayListViewReminder(Delegator delegator, GenericValue userLogin){
		boolean displayPopup = false;
		try{
			if(UtilValidate.isNotEmpty(userLogin)){
				String partyId = userLogin.getString("partyId");
				if(UtilValidate.isNotEmpty(partyId)){
					GenericValue partyAttribute = delegator.findOne("PartyAttribute",UtilMisc.toMap("partyId", partyId, "attrName", DISPLAY_BIRTHDAY_LIST_VIEW_REMINDER),false);
					if(UtilValidate.isNotEmpty(partyAttribute))
						displayPopup = partyAttribute.getString("attrValue")!= null?
								"Y".equalsIgnoreCase(partyAttribute.getString("attrValue")):false;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return displayPopup;
	}
}
