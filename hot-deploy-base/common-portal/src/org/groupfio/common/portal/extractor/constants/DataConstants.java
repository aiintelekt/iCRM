/**
 * 
 */
package org.groupfio.common.portal.extractor.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sharif
 *
 */
public class DataConstants {

	public static final Map<String, String> POSTAL_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("ADDRESS_1", "ADDRESS_1");
	    	        put("ADDRESS_2", "ADDRESS_2");
	    	        put("CITY", "CITY");
	    	        put("COUNTRY", "COUNTRY");
	    	        put("STATE", "STATE");
	    	        put("ZIP", "ZIP");
	    	        put("ZIP_EXT", "ZIP_EXT");
	    	        put("LOCATION", "LOCATION");
	   		 }});
	
	public static final Map<String, String> PERSON_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("BIRTHDAY", "BIRTHDAY");
	    	        put("FIRST_NAME", "FIRST_NAME");
	    	        put("LAST_NAME", "LAST_NAME");
	    	        put("FULL_NAME", "FULL_NAME");
	    	        put("TITLE", "TITLE");
	    	        put("DESIGNATION", "DESIGNATION");
	    	        put("PARTY_ID", "PARTY_ID");
	   		 }});
	
	public static final Map<String, String> GROUP_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("GROUP_NAME", "ACCOUNT_NAME");
	    	        put("GROUP_NAME_LOCAL", "ACCOUNT_NAME_LOCAL");
	    	        put("GROUP_ID", "GROUP_ID");
	   		 }});
	
	public static final Map<String, String> CONTACT_INFO_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("PHONE", "PHONE");
	    	        put("MOBILE_PHONE", "MOBILE_PHONE");
	    	        put("SECONDARY_PHONE", "SECONDARY_PHONE");
	    	        put("EMAIL_ADDRESS", "EMAIL_ADDRESS");
	    	        put("SKYPE_ID", "SKYPE_ID");
	    	        put("WEB_URL", "WEB_URL");
	   		 }});
	
	public static final Map<String, String> GENERAL_INFO_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("RM", "RM");
	    	        put("TEAM", "TEAM");
	    	        put("REPLY_TO", "REPLY_TO");
	    	        put("CURRENT_DATE", "CURRENT_DATE");
	    	        put("USER_LOGIN_ID", "USER_LOGIN_ID");
	    	        put("USER_LOGIN_PASS", "USER_LOGIN_PASS");
	    	        put("E_SIGNATURE", "E_SIGNATURE");
	    	        put("AGREEMENT_PROGRAM", "AGREEMENT_PROGRAM");
	    	        put("AGMT_CUST_NAME", "AGMT_CUST_NAME");
	    	        put("AGMT_SM_NAME", "AGMT_SM_NAME");
	    	        put("AGMT_CSR_CONT", "AGMT_CSR_CONT");
	    	        put("AGMT_VOL_CONT", "AGMT_VOL_CONT");
	    	        put("AGMT_ADDIT_CONT", "AGMT_ADDIT_CONT");
	    	        put("AGMT_2G_CONT", "AGMT_2G_CONT");
	    	        put("PARTY_SIGNATURE", "PARTY_SIGNATURE");
	    	        put("SM_SIGNATURE", "SM_SIGNATURE");
	    	        put("CUSTOMER_ID", "CUSTOMER_ID");
	    	        put("AGMT_DEALER_ADD", "AGMT_DEALER_ADD");
	    	        put("AGMT_YEAR", "AGMT_YEAR");
	    	        put("AGMT_NXT_YEAR", "AGMT_NXT_YEAR");
	    	        put("APPLICATION_URL", "APPLICATION_URL");
	    	        put("APPLICATION_INTL_URL", "APPLICATION_INTL_URL");
	    	        put("OPPORTUNITY_NAME", "OPPORTUNITY_NAME");
	    	        put("OPPORTUNITY_ID", "OPPORTUNITY_ID");
	    	        put("OPPORTUNITY_VIEW_URL", "OPPORTUNITY_VIEW_URL");
	    	        put("OPPORTUNITY_OWNER_NAME", "OPPORTUNITY_OWNER_NAME");
	    	        put("OPPORTUNITY_INSIDE_REP_NAME", "OPPORTUNITY_INSIDE_REP_NAME");
	    	        put("OTP", "OTP");
	    	        put("AGMT_ID", "AGMT_ID");
	    	        put("AGMT_NAME", "AGMT_NAME");
	    	        put("AGMT_THRU_DATE", "AGMT_THRU_DATE");
	    	        put("INVOICE_ID", "INVOICE_ID");
	    	        put("RECEIPT_URL", "RECEIPT_URL");
	    	        put("PARTY_NAME", "PARTY_NAME");
	   		 }});
	
	public static final Map<String, String> CONTENT_INFO_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("MAIN_TPL_CONT", "MAIN_TPL_CONT");
	   		 }});
	
	public static final Map<String, String> SR_INFO_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("SR_NUMBER", "SR_NUMBER");
	    	        put("SR_NAME", "SR_NAME");
	    	        put("SR_CUSTOMER", "SR_CUSTOMER");
	    	        put("SR_DEALER", "SR_DEALER");
	    	        put("SR_DEALER_CONTACT", "SR_DEALER_CONTACT");
	    	        put("SR_GENERATED_BY_FIRST_NAME", "SR_GENERATED_BY_FIRST_NAME");
	    	        put("SR_GENERATED_BY_LAST_NAME", "SR_GENERATED_BY_LAST_NAME");
	    	        put("SR_GENERATED_AT_TIME", "SR_GENERATED_AT_TIME");
	    	        put("SR_GENERATED_ON_DATE", "SR_GENERATED_ON_DATE");
	    	        put("SR_GENERATED_BY_USER_ROLE", "SR_GENERATED_BY_USER_ROLE");
	    	        put("SR_TRACKER_URL", "SR_TRACKER_URL");
	    	        put("LIST_OF_FSR", "LIST_OF_FSR");
	    	        put("SR_SURVEY_URL", "SR_SURVEY_URL");
	    	        put("SR_OWNER_BY_FIRST_NAME", "SR_OWNER_BY_FIRST_NAME");
	    	        put("SR_OWNER_BY_LAST_NAME", "SR_OWNER_BY_LAST_NAME");
	   		 }});
	
	public static final Map<String, String> ACT_INFO_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("ACT_TRACKER_URL", "ACT_TRACKER_URL");
	    	        put("ACT_NUMBER", "ACT_NUMBER");
	    	        put("ACT_NAME", "ACT_NAME");
	    	        put("ACT_DESCRIPTION", "ACT_DESCRIPTION");
	    	        put("VIEW_ACTIVITY_LINK", "VIEW_ACTIVITY_LINK");
	    	        
	   		 }});
	
	public static final Map<String, String> SP_INFO_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("LIST_OF_SP", "LIST_OF_SP");
	   		 }});
	
	public static final Map<String, String> APV_INFO_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("APV_TRACKER_URL", "APV_TRACKER_URL");
	    	        put("APVL_CAT_NAME", "APVL_CAT_NAME");
	    	        put("APV_START_DATE", "APV_START_DATE");
	    	        put("APV_END_DATE", "APV_END_DATE");
	    	        put("APV_COMMENTS", "APV_COMMENTS");
	   		 }});
	
	public static final Map<String, String> STORE_RECEIPT_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("STORE_NAME", "STORE_NAME");
	    	        put("STORE_ADDRESS", "STORE_ADDRESS");
	    	        put("STORE_ADDRESS_2", "STORE_ADDRESS_2");
	    	        put("STORE_CITY", "STORE_CITY");
	    	        put("STORE_STATE", "STORE_STATE");
	    	        put("STORE_POSTAL", "STORE_POSTAL");
	    	        put("STORE_PHONE", "STORE_PHONE");
	    	        put("STORE_BRAND", "STORE_BRAND");
	    	        put("STORE_COUNTRY", "STORE_COUNTRY");
	    	        put("STORE_DISTRICT", "STORE_DISTRICT");
	    	        put("STORE_EMAIL", "STORE_EMAIL");
	    	        put("STORE_MANAGER", "STORE_MANAGER");
	    	        put("STORE_RETURN_POLICY", "STORE_RETURN_POLICY");
	    	        put("STORE_IMAGE", "STORE_IMAGE");
	    	        put("STORE_IMAGE_URL", "STORE_IMAGE_URL");
	    	        put("STORE_URL1", "STORE_URL1");
	    	        put("STORE_URL2", "STORE_URL2");
	    	        put("STORE_URL3", "STORE_URL3");
	    	        put("STORE_URL4", "STORE_URL4");
	   		 }});
	
	public static final Map<String, String> ORDER_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("ORDER_DATE", "ORDER_DATE");
	   		 }});
	
	public static final Map<String, String> CAMPAIGN_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("CAMPAIGN_ID", "CAMPAIGN_ID");
	    	        put("CAMPAIGN_NAME", "CAMPAIGN_NAME");
	    	        put("PUBLISH_DATE", "PUBLISH_DATE");
	   		 }});
	
	public static final Map<String, String> CAMPAIGN_EVENT_TAG = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("GROUP_ID", "GROUP_ID");
	    	        put("GROUP_TYPE", "GROUP_TYPE");
	    	        put("GROUP_NAME", "GROUP_NAME");
	    	        put("CMP_EVENT_LIST", "CMP_EVENT_LIST");
	   		 }});
}
