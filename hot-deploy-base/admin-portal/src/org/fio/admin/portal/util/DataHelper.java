package org.fio.admin.portal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.PartyHelper;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class DataHelper {

    private static String MODULE = DataHelper.class.getName();

    public static Map < String, Object > getDropDownOptions(List < GenericValue > entityList, String keyField, String desField) {
	Map < String, Object > options = new LinkedHashMap < String, Object > ();
	for (GenericValue entity: entityList) {
	    options.put(entity.getString(keyField), entity.getString(desField));
	}
	return options;
    }
    public static Map < String, Object > getDropDownKeyValueOptions(List < GenericValue > entityList, String keyField, String desField) {
	Map < String, Object > options = new LinkedHashMap < String, Object > ();
	for (GenericValue entity: entityList) {
	    options.put(entity.getString(keyField) + "(" + entity.getString(desField) + ")", entity.getString(desField));
	}
	return options;
    }
    public static Map < String, Object > getDropDownOptionsFromMultiDesField(List < GenericValue > entityList, String keyField, LinkedList < String > desField) {
	Map < String, Object > options = new LinkedHashMap < String, Object > ();
	for (GenericValue entity: entityList) {
	    String descField = "";
	    for (String desFieldVal: desField) {
		if (UtilValidate.isNotEmpty(descField)) {
		    if (UtilValidate.isNotEmpty(entity.getString(desFieldVal))) {
			descField = descField + " " + entity.getString(desFieldVal);
		    }
		} else {
		    descField = UtilValidate.isNotEmpty(entity.getString(desFieldVal)) ? entity.getString(desFieldVal) : "";
		}
	    }
	    options.put(entity.getString(keyField), descField);
	}
	return options;
    }

    public static Map < String, Object > getDropDownOptionsFromMap(List < Map < String, Object >> entityList, String keyField, String desField) {
	Map < String, Object > options = new LinkedHashMap < String, Object > ();
	for (Map < String, Object > entity: entityList) {
	    options.put(entity.get(keyField).toString(), entity.get(desField));
	}
	return options;
    }

    public static String sqlPropToJavaProp(String prop) {
	if (UtilValidate.isNotEmpty(prop)) {
	    //String prop = "hp_due_wthin_1_yr_amt";
	    prop = prop.toLowerCase();
	    prop = prop.replace("_1_", "1");
	    prop = prop.replace("_2_", "2");
	    String convertedString = "";
	    for (int i = 0; i < prop.length(); i++) {
		if (prop.charAt(i) == '_') {
		    convertedString += ("" + prop.charAt(++i)).toUpperCase();
		} else {
		    convertedString += prop.charAt(i);
		}
	    }
	    return convertedString;
	}
	return prop;
    }

    public static boolean getFirstValidRoleTypeId(String roleTypeId, List < String > possibleRoleTypeIds) throws GenericEntityException {

	for (String possibleRoleTypeId: possibleRoleTypeIds) {
	    if (possibleRoleTypeId.equals(roleTypeId)) {
		return true;
	    }
	}
	return false;
    }

    public static String getGeoName(Delegator delegator, String value, String geoTypeId) {

	try {
	    if (UtilValidate.isNotEmpty(value)) {
		EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoName")), EntityOperator.EQUALS, value.toString().toUpperCase())
			);

		condition = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, geoTypeId),
			condition
			);

		GenericValue entity = EntityQuery.use(delegator).select("geoName").from("Geo").where(condition).queryFirst();
		if (UtilValidate.isNotEmpty(entity)) {
		    return entity.getString("geoName");
		}
	    }
	} catch (Exception e) {}

	return "";
    }

    public static String getEnumDescription(Delegator delegator, String value, String enumTypeId) {

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

		GenericValue enumEntity = EntityQuery.use(delegator).select("description").from("Enumeration").where(condition).queryFirst();
		if (UtilValidate.isNotEmpty(enumEntity)) {
		    return enumEntity.getString("description");
		}
	    }
	} catch (Exception e) {}

	return "";
    }

    public static String getPartyIdentificationDescription(Delegator delegator, String value) {

	try {
	    if (UtilValidate.isNotEmpty(value)) {

		EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("partyIdentificationTypeId")), EntityOperator.EQUALS, value.toUpperCase()),
			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toUpperCase())
			);

		GenericValue partyIdentificationType = EntityQuery.use(delegator).select("description").from("PartyIdentificationType").where(condition).queryFirst();
		if (UtilValidate.isNotEmpty(partyIdentificationType)) {
		    return partyIdentificationType.getString("description");
		}
	    }
	} catch (Exception e) {}

	return "";
    }

    public static String numberFormat(double value) {
	if (value < 1000) {
	    return format("###.##", value);
	} else {
	    double hundreds = value % 1000;
	    int other = (int)(value / 1000);
	    return format(",##", other) + ',' + format("000.##", hundreds);
	}
    }

    private static String format(String pattern, Object value) {
	return new DecimalFormat(pattern).format(value);
    }
    public static String getCustRequestAssocDesc(Delegator delegator, String type, String code) {

	try {
	    if (UtilValidate.isNotEmpty(type)) {
		EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("type")), EntityOperator.EQUALS, type)
			);

		if (UtilValidate.isNotEmpty(code)) {
		    condition = EntityCondition.makeCondition(EntityOperator.AND,
			    EntityCondition.makeCondition("code", EntityOperator.EQUALS, code),
			    condition
			    );
		}

		GenericValue custRequestAssc = EntityQuery.use(delegator).select("value").from("CustRequestAssoc").where(condition).queryFirst();
		if (UtilValidate.isNotEmpty(custRequestAssc)) {
		    return custRequestAssc.getString("value");
		}
	    }
	} catch (Exception e) {}

	return "";
    }
    public static Map < String, Object > contactAddress(DispatchContext dctx, Map < String, Object > context) {
	Map < String, Object > results = ServiceUtil.returnSuccess();
	Delegator delegator = dctx.getDelegator();
	String contactMechId = (String) context.get("contactMechId");
	String value = (String) context.get("value");
	try {
	    if (UtilValidate.isNotEmpty(contactMechId)) {
		GenericValue addressDetails = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId", contactMechId).queryOne();
		if (UtilValidate.isNotEmpty(addressDetails)) {
		    addressDetails.put("address3", value);
		    addressDetails.store();
		}
	    }
	} catch (GeneralException e) {
	    Debug.log("==error in contactAddress===" + e.getMessage());
	}
	return results;
    }

    public static String getDateTime(Timestamp inputDate) {
	try {
	    if (UtilValidate.isNotEmpty(inputDate)) {
		String date = org.fio.homeapps.util.UtilDateTime.timeStampToString(inputDate, "yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
		String time = org.fio.homeapps.util.UtilDateTime.timeStampToString(inputDate, "HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());
		String dateTime = date+"T"+time;
		return dateTime;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
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

    public static String getPartyDesignation(String partyId, Delegator delegator) throws GenericEntityException {
	String partyDesignation = "";
	try {
	    if(UtilValidate.isNotEmpty(partyId)) {
		GenericValue party = EntityQuery.use(delegator).select("designation").from("Person").where("partyId",partyId).queryOne();
		if(UtilValidate.isNotEmpty(party) && UtilValidate.isNotEmpty(party.getString("designation"))) {
		    partyDesignation = EnumUtil.getEnumDescription(delegator, party.getString("designation"), "DESIGNATION");
		}
	    }
	} catch (Exception e) {
	    Debug.logError(e, "Error finding PartyDesignation", MODULE);
	}
	return partyDesignation;
    }

    public static List<GenericValue> getPrimaryContactInfoByType(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
	List<GenericValue> contactInfoList = new ArrayList<GenericValue>();
	try {
	    DynamicViewEntity dynamicView = new DynamicViewEntity();
	    dynamicView.addMemberEntity("PCM", "PartyContactMech");
	    dynamicView.addAlias("PCM", "partyId");
	    dynamicView.addAlias("PCM", "contactMechId");
	    dynamicView.addAlias("PCM", "fromDate");
	    dynamicView.addAlias("PCM", "thruDate");
	    dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
	    dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
	    dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));

	    EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
		    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
		    EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId)
		    );
	    TransactionUtil.begin();
	    contactInfoList = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().queryList();
	    TransactionUtil.commit();
	}catch (Exception e) {
	    e.printStackTrace();
	}

	return contactInfoList;
    }

    public static Map<String, Object> createPartyIdentification(Delegator delegator, Map<String, Object> context) {
	Map<String, Object> result = new HashMap<String, Object>();
	try {
	    String partyId = (String) context.get("partyId");
	    String idValue = (String) context.get("idValue");
	    String partyIdentifyTypeId= (String) context.get("partyIdentificationTypeId");

	    if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(partyIdentifyTypeId) && UtilValidate.isNotEmpty(idValue)) {
		GenericValue partyIdentificationGv = EntityQuery.use(delegator).from("PartyIdentification").where("partyId", partyId, "partyIdentificationTypeId", partyIdentifyTypeId).queryFirst();
		if(UtilValidate.isNotEmpty(partyIdentificationGv)) {
		    partyIdentificationGv.set("idValue", idValue);
		    partyIdentificationGv.store();
		} else {
		    partyIdentificationGv = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", partyIdentifyTypeId, "idValue", idValue));
		    partyIdentificationGv.create();
		}
	    }
	    result = ServiceUtil.returnSuccess();

	} catch (Exception e) {
	    e.printStackTrace();
	    result = ServiceUtil.returnError(e.getMessage());
	}
	return result;
    }
    
    public static String triggerShellScript(Delegator delegator, Map<String, Object> context ) {
	String result = "error";
	try {
	    	String shellScriptPath = (String) context.get("shellScriptPath");
	    	if(UtilValidate.isEmpty(shellScriptPath)) {
	    	    Debug.logInfo("shellScriptPath is empty!", MODULE);
	    	    return "error";
	    	}
		File file = new File(shellScriptPath);

		if (file.exists()) {
		    file.setExecutable(true);
		    file.setReadable(true);
		    file.setWritable(true);

		    String cmd = shellScriptPath + " " + "&";
		    ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
		    Process p = pb.start();
		    
		    BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    String resultOutput = output.readLine();
		    Debug.logInfo("Shell Script output : " + resultOutput,MODULE);

		    output = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		    resultOutput = output.readLine();
		    Debug.logInfo("Shell Script error output : " + resultOutput,MODULE);
		    
		    result = "success";
		} else {
		    Debug.logInfo("Shell script file not exist", MODULE);
		    result = "error";
		}
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    result = "error";
	}
	return result;
    }

	public static void addTeamPartyRelationship(GenericValue team, GenericValue userLogin, LocalDispatcher dispatcher) {
		
		try {
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", team.getString("partyId"), "roleTypeId", "ACCOUNT_TEAM", "userLogin", userLogin));
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void addTeamMemberPartyRelationship(String teamMemberPartyId, String teamPartyId, boolean isLeader,Delegator delegator, GenericValue userLogin,LocalDispatcher dispatcher) {
		try {

			String roleTypeIdFrom = PartyHelper.getFirstValidRoleTypeId(teamPartyId, UtilMisc.toList("ACCOUNT", "ACCOUNT_TEAM"), delegator);
			String roleTypeIdTo = PartyHelper.getFirstValidRoleTypeId(teamMemberPartyId, UtilMisc.toList("ACCOUNT_MANAGER", "ACCOUNT_REP", "CUST_SERVICE_REP"), delegator);
			String securityGroupId = "CSR_MEMBER";
			if(isLeader)
				securityGroupId = "SALES_MANAGER";
			if(UtilValidate.isNotEmpty(roleTypeIdFrom) && UtilValidate.isNotEmpty(roleTypeIdTo)) {
			Map<String,Object> input = UtilMisc.toMap("partyIdFrom", teamPartyId, "roleTypeIdFrom", roleTypeIdFrom, "partyIdTo", teamMemberPartyId, "roleTypeIdTo", roleTypeIdTo);
				input.put("partyRelationshipTypeId", "ASSIGNED_TO");
				input.put("securityGroupId", securityGroupId);
				input.put("fromDate", UtilDateTime.nowTimestamp());
				input.put("userLogin", userLogin);
				Map<String,Object> serviceResults = dispatcher.runSync("createPartyRelationship", input);
				if(ServiceUtil.isError(serviceResults)) {
					Debug.logError(ServiceUtil.getErrorMessage(serviceResults), MODULE);
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void expireTeamMemberPartyRelationship(String teamMemberPartyId, String teamPartyId, Delegator delegator, GenericValue userLogin,LocalDispatcher dispatcher) {
		if(UtilValidate.isNotEmpty(teamPartyId) && UtilValidate.isNotEmpty(teamMemberPartyId))
		try {
			String roleTypeIdFrom = PartyHelper.getFirstValidRoleTypeId(teamPartyId, UtilMisc.toList("ACCOUNT", "ACCOUNT_TEAM"), delegator);
			//String roleTypeIdTo = PartyHelper.getFirstValidRoleTypeId(teamMemberPartyId, UtilMisc.toList("ACCOUNT_MANAGER", "ACCOUNT_REP", "CUST_SERVICE_REP"), delegator);
			EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, teamPartyId),
					EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, teamMemberPartyId),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ASSIGNED_TO"),
					EntityUtil.getFilterByDateExpr());
			List<GenericValue> relationships = delegator.findList("PartyRelationship",  conditions, null, null,null,false);
			PartyHelper.expirePartyRelationships(relationships, UtilDateTime.nowTimestamp(), dispatcher, userLogin);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void removeOrAddTeamMembersPartyRelationship(String emplTeamId, boolean isExpire, Delegator delegator, GenericValue userLogin,LocalDispatcher dispatcher) {
		if(UtilValidate.isNotEmpty(emplTeamId)) {
			List<GenericValue> teamMembers =  new ArrayList<>();
			
			try {
				GenericValue team = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId",emplTeamId).queryOne();
				if(UtilValidate.isNotEmpty(team)) {
					String teamPartyId =  team.getString("partyId");
					EntityCondition activeEmployies = EntityCondition.makeCondition(EntityOperator.AND,
															EntityCondition.makeCondition("emplTeamId",emplTeamId),
															EntityCondition.makeConditionDate("fromDate", "thruDate")
															);
					teamMembers = EntityQuery.use(delegator).from("EmplPositionFulfillment").where(activeEmployies).queryList();
					for(GenericValue teamMember : teamMembers) {
						String teamMemberPartyId =  teamMember.getString("partyId");
						if(isExpire) {
							expireTeamMemberPartyRelationship(teamMemberPartyId, teamPartyId, delegator, userLogin, dispatcher);
						}else {
							addTeamMemberPartyRelationship(teamMemberPartyId, teamPartyId, "Y".equals(teamMember.getString("isTeamLead")), delegator, userLogin, dispatcher);
						}
					}
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static Map<String, Object> getPartySegmentByGroup(Delegator delegator, String groupId, String partyId){
		Map<String, Object> customData = new HashMap<String, Object>();
		try {
			TransactionUtil.begin();
			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
			dynamicViewEntity.addMemberEntity("CFPC", "CustomFieldPartyClassification");
			dynamicViewEntity.addAlias("CFPC", "groupId");
			dynamicViewEntity.addAlias("CFPC", "partyId");
			
			dynamicViewEntity.addMemberEntity("CF", "CustomField");
			dynamicViewEntity.addAlias("CF", "customFieldId");
			dynamicViewEntity.addAlias("CF", "customFieldName");
			dynamicViewEntity.addAlias("CF", "sequenceNumber");
			dynamicViewEntity.addAlias("CF", "createdTxStamp");
			dynamicViewEntity.addAlias("CF", "lastUpdatedTxStamp");
			
			//dynamicViewEntity.addAliasAll("CF", null, UtilMisc.toList("groupId"));
			dynamicViewEntity.addViewLink("CFPC", "CF", Boolean.FALSE, ModelKeyMap.makeKeyMapList("customFieldId"));
			
			GenericValue customDataGv = EntityQuery.use(delegator).from(dynamicViewEntity).where("groupId", groupId, "partyId", partyId).orderBy("lastUpdatedTxStamp DESC").queryFirst();
			customData = UtilValidate.isNotEmpty(customDataGv) ? DataUtil.convertGenericValueToMap(delegator, customDataGv) : customData;
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return customData;
	}
	
}