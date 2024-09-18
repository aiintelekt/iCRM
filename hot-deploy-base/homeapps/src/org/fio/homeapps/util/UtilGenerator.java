/**
 * 
 */
package org.fio.homeapps.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Random;

import org.fio.homeapps.constants.GlobalConstants;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.entity.Delegator;

/**
 * @author Sharif
 *
 */
public class UtilGenerator {
	
	private static String MODULE = UtilGenerator.class.getName();

	// TODO country code need to be configurable
	//Desc : Generating Number for Service Request
	public static String getSrNumber(Delegator delegator , String custRequestId) {
		/*SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date(System.currentTimeMillis());
		System.out.println("Date====="+formatter.format(date));
		//String countryCode = "TW";
		//String externalId = countryCode +"-"+GlobalConstants.SR_ENTITY_NAME+"-"+formatter.format(date)+"-"+ GlobalConstants.SYSTEM_NAME +"-"+custRequestId;
		*/String externalId = GlobalConstants.SR_ENTITY_NAME+"-"+custRequestId;
		return externalId;
	}
	
	// TODO country code need to be configurable
	//Desc : Generating Number for IA
	public static String getIaNumber(Delegator delegator, String workEffortId) {
		/*SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date(System.currentTimeMillis());
		System.out.println("Date====="+formatter.format(date));*/
		//String countryCode = "TW";
		//String externald = countryCode +"-"+GlobalConstants.IA_ENTITY_NAME+"-"+formatter.format(date)+"-"+ GlobalConstants.SYSTEM_NAME +"-"+workEffortId;
		String externald = GlobalConstants.IA_ENTITY_NAME+"-"+workEffortId;
		return externald;
	}
	
	public static String getNoteNumber(Delegator delegator, String noteId) {
		/*SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date(System.currentTimeMillis());
		System.out.println("Date====="+formatter.format(date));
		String countryCode = "TW";
		String externald = countryCode +"-"+GlobalConstants.NOTE_ID+"-"+formatter.format(date)+"-"+ GlobalConstants.SYSTEM_NAME +"-"+noteId;
		*/
		String externald =GlobalConstants.NOTE_ID+"-"+noteId;
		return externald;
	}
	public static String getSalesOpportunityNumber(Delegator delegator, String opportunityId) {
		/*SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date(System.currentTimeMillis());
		System.out.println("Date====="+formatter.format(date));
		String countryCode = "TW";
		String externald = countryCode +"-"+GlobalConstants.OPPORTUNITY_NAME+"-"+formatter.format(date)+"-"+ GlobalConstants.SYSTEM_NAME +"-"+opportunityId;
		*/
		String externald =GlobalConstants.OPPORTUNITY_NAME+"-"+opportunityId;
		return externald;
	}
	
	public static synchronized String getNextSeqId() {
		long generatedLong = Math.abs(new Random().nextLong());
		return ""+UtilDateTime.nowTimestamp().getTime()+generatedLong;
	}
	
	public static synchronized String getNextSeqId(int seqNum) {
		return ""+UtilDateTime.nowTimestamp().getTime()+UtilFormatOut.formatPaddedNumber(seqNum, 5);
	}

	// TODO country code need to be configurable
	//Desc : Generating Number for Customer Alert
	public static String getAlertTrackingNumber(Delegator delegator , String alertTrackingId) {
		/*SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date(System.currentTimeMillis());
		String countryCode = "SG";
		String externalId = countryCode +"-"+GlobalConstants.ALERT_ENTITY_NAME+"-"+formatter.format(date)+"-"+ GlobalConstants.SYSTEM_NAME +"-"+alertTrackingId;
		*/
		String externalId =GlobalConstants.ALERT_ENTITY_NAME+"-"+alertTrackingId;
		return externalId;
	}


}
