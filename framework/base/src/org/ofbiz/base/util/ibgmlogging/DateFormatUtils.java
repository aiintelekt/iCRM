package org.ofbiz.base.util.ibgmlogging;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {

	public static final String DATEFORMATyyyyMMddHHmmss="yyyyMMddHHmmss";
	
	private DateFormatUtils() {
	}

	public static String getSystemDateWithTimeStamp(String formatStr) {
		
		final Date date = new Date();
		String systemDate = null;
		SimpleDateFormat format=new SimpleDateFormat(formatStr);
		systemDate = format.format(date).toString();
		return systemDate;
	}	
}
