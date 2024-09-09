/**
 * 
 */
package org.fio.homeapps.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.ofbiz.base.util.UtilValidate;

/**
 * @author Sharif
 *
 */
public class UtilDateTime extends org.ofbiz.base.util.UtilDateTime {

	public static Timestamp addHoursToTimestamp(Timestamp start, int hours) {
        return new Timestamp(start.getTime() + (60L*60L*1000L*hours));
    }
	
	public static Timestamp addMinutesToTimestamp(Timestamp start, int mins) {
        return new Timestamp(start.getTime() + (60L*1000L*mins));
    }
	
	public static Timestamp convertedTimestamp(String inputValue, String dateTimeFormat, String fromTimezoneId, String toTimezoneId) {
		Timestamp convertedDate = null;
		try {
			
			if (UtilValidate.isEmpty(inputValue) || UtilValidate.isEmpty(fromTimezoneId) || UtilValidate.isEmpty(toTimezoneId)) {
				return null;
			}
			
			LocalDateTime ldt = LocalDateTime.parse(inputValue.toString(), DateTimeFormatter.ofPattern(dateTimeFormat));
			
			ZoneId fromZoneId = ZoneId.of(fromTimezoneId);
			System.out.println("TimeZone : " + fromZoneId);

			ZonedDateTime fromZonedDateTime = ldt.atZone(fromZoneId);
			System.out.println("Date (FROM) : " + fromZonedDateTime);

			ZoneId toZoneId = ZoneId.of(toTimezoneId);
			System.out.println("TimeZone : " + toZoneId);

			ZonedDateTime toZonedDateTime = fromZonedDateTime.withZoneSameInstant(toZoneId);
			System.out.println("Date (TO) : " + toZonedDateTime);

			DateTimeFormatter format = DateTimeFormatter.ofPattern(dateTimeFormat);
			
			String fromTime = format.format(fromZonedDateTime);
			String toTime = format.format(toZonedDateTime);
			
			System.out.println("\n---DateTimeFormatter---");
			System.out.println("Date (FROM) : " + fromTime);
			System.out.println("Date (TO) : " + toTime);
			
			convertedDate = UtilDateTime.stringToTimeStamp(toTime, dateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return convertedDate;
	}
	
	public static long compareTwoTimeStamps(java.sql.Timestamp currentTime, java.sql.Timestamp oldTime) {
		long milliseconds1 = oldTime.getTime();
		long milliseconds2 = currentTime.getTime();

		long diff = milliseconds2 - milliseconds1;
		long diffSeconds = diff / 1000;
		long diffMinutes = diff / (60 * 1000);
		long diffHours = diff / (60 * 60 * 1000);
		long diffDays = diff / (24 * 60 * 60 * 1000);
		
		System.out.println("diffSeconds: "+diffSeconds);
		System.out.println("diffMinutes: "+diffMinutes);
		System.out.println("diffHours: "+diffHours);
		System.out.println("diffDays: "+diffDays);

		return diffMinutes;
	}
	
	public static long getMinutes(java.sql.Timestamp currentTime, java.sql.Timestamp oldTime) {
		long milliseconds1 = oldTime.getTime();
		long milliseconds2 = currentTime.getTime();

		long diff = milliseconds2 - milliseconds1;
		long diffMinutes = diff / (60 * 1000);
		
		System.out.println("diffMinutes: "+diffMinutes);

		return diffMinutes;
	}
	
	public static String timeStampToString(Timestamp stamp, String dateTimeFormat, TimeZone tz, Locale locale) {
		if (UtilValidate.isEmpty(stamp)) {
			return "";
		}
        DateFormat dateFormat = toDateTimeFormat(dateTimeFormat, tz, locale);
        return dateFormat.format(stamp);
    }
	
	public static Timestamp addValueToTimestamp(Timestamp start, String value) {
		if (UtilValidate.isNotEmpty(value)) {
			Float val = Float.parseFloat(value);
			Timestamp finalTime = addHoursToTimestamp(start, val.intValue()); 
			if (val.floatValue()>val.intValue()) {
				finalTime = addMinutesToTimestamp(finalTime, 30);
			}
			return finalTime;
		}
        return start;
    }
	
	public static int getLastYear(Timestamp time) {
		if (UtilValidate.isEmpty(time)) {
			time = UtilDateTime.nowTimestamp();
		}
		Calendar prevYear = Calendar.getInstance();
		prevYear.setTimeInMillis(time.getTime());
	    prevYear.add(Calendar.YEAR, -1);
	    int year = prevYear.get(Calendar.YEAR);
        return year;
    }
	
	public static int getThisYear() {
		Calendar prevYear = Calendar.getInstance();
	    int year = prevYear.get(Calendar.YEAR);
        return year;
    }
	
	public static int getNextYear(Timestamp time) {
		if (UtilValidate.isEmpty(time)) {
			time = UtilDateTime.nowTimestamp();
		}
		Calendar prevYear = Calendar.getInstance();
		prevYear.setTimeInMillis(time.getTime());
	    prevYear.add(Calendar.YEAR, 1);
	    int year = prevYear.get(Calendar.YEAR);
        return year;
    }
	
	public static int getCurrentQuarter(Timestamp inputDate) {
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		List<Map<String, Object>> payoutDates = new ArrayList<>();
		Timestamp startDate = null;
		Timestamp endDate = null;
		for (int i=1; i<=4; i++) {
			if (UtilValidate.isEmpty(endDate)) {
				LocalDate localDate = inputDate.toLocalDateTime().toLocalDate();
				localDate = localDate.plusMonths(3);
				endDate = UtilDateTime.getDayEnd(Timestamp.valueOf(localDate.atStartOfDay()));
			} else {
				LocalDate localDate = UtilDateTime.addDaysToTimestamp(endDate, 1).toLocalDateTime().toLocalDate();
				localDate = localDate.plusMonths(3);
				endDate = UtilDateTime.getDayEnd(Timestamp.valueOf(localDate.atStartOfDay()));
			}
			if (UtilValidate.isEmpty(startDate)) {
				startDate = inputDate;
			} else {
				LocalDate localDate = endDate.toLocalDateTime().toLocalDate();
				localDate = localDate.minusMonths(3);
				startDate = UtilDateTime.getDayEnd(Timestamp.valueOf(localDate.atStartOfDay()));
			}
			
			Map<String, Object> data = new LinkedHashMap<>();
			data.put("startDate", startDate);
			data.put("endDate", endDate);
			payoutDates.add(data);
		}
		
		int currentQuarter = 1;
		for (Map<String, Object> data : payoutDates) {
			startDate = (Timestamp) data.get("startDate");
			endDate = (Timestamp) data.get("endDate");
			if (nowTimestamp.after(startDate) && nowTimestamp.before(endDate)) {
				break;
			}
			currentQuarter++;
		}
		return currentQuarter;
	}
	
	public static int getCurrentMonth(Timestamp inputDate) {
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		List<Map<String, Object>> payoutDates = new ArrayList<>();
		Timestamp startDate = null;
		Timestamp endDate = null;
		for (int i=1; i<=12; i++) {
			if (UtilValidate.isEmpty(endDate)) {
				LocalDate localDate = inputDate.toLocalDateTime().toLocalDate();
				localDate = localDate.plusMonths(1);
				endDate = UtilDateTime.getDayEnd(Timestamp.valueOf(localDate.atStartOfDay()));
			} else {
				LocalDate localDate = UtilDateTime.addDaysToTimestamp(endDate, 1).toLocalDateTime().toLocalDate();
				localDate = localDate.plusMonths(1);
				endDate = UtilDateTime.getDayEnd(Timestamp.valueOf(localDate.atStartOfDay()));
			}
			if (UtilValidate.isEmpty(startDate)) {
				startDate = inputDate;
			} else {
				LocalDate localDate = endDate.toLocalDateTime().toLocalDate();
				localDate = localDate.minusMonths(1);
				startDate = UtilDateTime.getDayEnd(Timestamp.valueOf(localDate.atStartOfDay()));
			}
			
			Map<String, Object> data = new LinkedHashMap<>();
			data.put("startDate", startDate);
			data.put("endDate", endDate);
			payoutDates.add(data);
		}
		
		int currentQuarter = 1;
		for (Map<String, Object> data : payoutDates) {
			startDate = (Timestamp) data.get("startDate");
			endDate = (Timestamp) data.get("endDate");
			if (nowTimestamp.after(startDate) && nowTimestamp.before(endDate)) {
				break;
			}
			currentQuarter++;
		}
		return currentQuarter;
	}
	
	public static Timestamp unixTimestampToTimestamp(long unixTimestamp) {
        try {
			// Convert Unix timestamp to Instant
			Instant instant = Instant.ofEpochSecond(unixTimestamp);

			// Define the desired date-time format
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
			                                               .withZone(ZoneId.systemDefault());

			// Format Instant into a human-readable date-time string
			String formattedDateTime = formatter.format(instant);
			//System.out.println("Formatted date-time: " + formattedDateTime);
			
			Timestamp ts = stringToTimeStamp(formattedDateTime, "yyyy-MM-dd HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());
			return ts;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public static Long timestampToUnixTimestamp(Timestamp timestamp) {
        try {
	        // Convert the timestamp to an Instant
        	Instant instant = Instant.ofEpochMilli(timestamp.getTime());

	        // Extract the Unix timestamp (seconds since Unix epoch)
        	long unixTimestamp = instant.getEpochSecond();
        	return unixTimestamp;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}
	
}
