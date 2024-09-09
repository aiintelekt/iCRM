/**
 * 
 */
package org.fio.homeapps.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityOperator;

/**
 * @author Sharif Ul Islam
 *
 */
public class GlobalConstants {
	
	public static final String configResource = "homeapps-config";
	
    public static final int DEFAULT_BUFFER_SIZE = 102400;
    public static final int LOCKBOX_ITEM_SEQUENCE_ID_DIGITS = 5;
    
    public static final int DEFAULT_PER_PAGE_COUNT = 20;
    
    public static final String RESPONSE_CODE = "code";
	public static final String RESPONSE_MESSAGE = "message";
	
	public static final int SR_HISTORY_PAGE_COUNT = 50;
	
	public static final String SYSTEM_NAME = "OFZ";
	
	public static final String IA_ENTITY_NAME = "IA";
	
	public static final String DUMMY_PARTY_ID = "99999";
	
	public static final String SR_ENTITY_NAME = "SR";
	
	public static final String ALERT_ENTITY_NAME = "AL";
	
	public static final String COMMON_TEAMS = "COMMON_TEAMS";
	public static final String NOTE_ID = "NT";
	public static final String OPPORTUNITY_NAME = "OP";
	
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";
	public static final String BAD_CREDENTIALS = "Bad Credentials";
	public static final String INVALID_USERNAME_OR_PASSWORD= "Invalid username or password";
	
	public static final String REST_API_LOG_FILE = "rest_client_api_log";
	
	public static final String ADMIN_LOGIN_USER_ID = "admin";
	public static final String ALLOW_ORIGIN_ALL = "*";
	
	public static final class AppStatus {
        private AppStatus() { }
        public static final String ACTIVATED = "ACTIVATED";
        public static final String DEACTIVATED = "DEACTIVATED";
    }
	
	public static final class SourceInvoked {
        private SourceInvoked() { }
        public static final String API = "API";
        public static final String PORTAL = "PORTAL";
        public static final String UNKNOWN = "UNKNOWN";
    }
	
	public static final Map<String, String> ROLE_TYPE_BY_EXTERNALID = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	       // put("01", "CUSTOMER");
	    	        put("02", "PROSPECT");
	    	        put("03", "CARD_CUST");
	    	        put("04", "CARD_AND_BANK");
	    	        put("05", "SR_NUMBER");
					put("06", "OPPORTUNITY");
	   		 }});
	
	public static final Map<String, String> CUSTOMER_ROLE_TYPE_BY_EXTERNALID = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        //put("01", "CUSTOMER");
	    	        put("03", "CARD_CUST");
	    	        put("08", "ACCOUNT");
	   		 }});
	public static final class CountryCode {
        private CountryCode() { }
        public static final String TAIWAN = "TW";
    }
	public static final class srStatus {
        private srStatus() { }
        public static final String SR_OPEN = "SR_OPEN";
        public static final String SR_OPEN_STATUS_CODE = "60840";
        public static final String SR_CLOSED = "SR_CLOSED";
        public static final String SR_CLOSED_STATUS_CODE = "60843";
    }
    public static final class DateTimeTypeConstant {
        private DateTimeTypeConstant() {}
        public static final String TIMESTAMP = "timestamp";
        public static final String DATE = "date";
        public static final String SQL_DATE = "sqlDate";
        public static final String UTIL_DATE = "utilDate";
        public static final String STRING = "string";
    }
    public static final class DataOperations {
        private DataOperations() {}
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String FIND = "FIND";
        public static final String DELETE = "DELETE";
    }
    public static final class AccessLevel{
		private AccessLevel() {}
		public static final String LEVEL1 = "L1";
		public static final String LEVEL2 = "L2";
		public static final String LEVEL3 = "L3";
		public static final String LEVEL4 = "L4";
		public static final String LEVEL5 = "L5";
		public static final String LEVEL6 = "L6";	
		public static final String YES = "Y";
		public static final String NO = "N";
		public static final String ALL = "A";
	}
    
    public static final class ModeOfAction {
        private ModeOfAction() { }
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String VIEW = "VIEW";
    }
    
    public static final class OperatorType {
        private OperatorType() { }
        public static String OPERATOR_EQUAL = "eq";
    	public static String OPERATOR_NOT_EQUAL = "ne";
    	public static String OPERATOR_LESS = "lt";
    	public static String OPERATOR_LESS_OR_EQUAL = "le";
    	public static String OPERATOR_GREATER = "gt";
    	public static String OPERATOR_GREATER_OR_EQUAL = "ge";
    	public static String OPERATOR_IS_IN = "in";
    	public static String OPERATOR_IS_NOT_IN = "ni";
    	public static String OPERATOR_IS_LIKE = "like";
    	public static String OPERATOR_IS_NOT_LIKE = "nlike";
    }
    
    public static final class Channels {
        private Channels() { }
        public static String PROGRAM = "PROGRAM";
    }
    
    public enum QueryType {
		NATIVE("NATIVE"),
		HQL("HQL"),
		;
		public String value;

		private QueryType(String value) {
			this.value = value;
		}
	}
    
    public static final Map<String, EntityComparisonOperator> ENTITY_OPERATOR_BY_NAME = 
	   		 Collections.unmodifiableMap(new HashMap<String, EntityComparisonOperator>() {{ 
	    	        put(OperatorType.OPERATOR_EQUAL, EntityOperator.EQUALS);
	    	        put(OperatorType.OPERATOR_NOT_EQUAL, EntityOperator.NOT_EQUAL);
	    	        put(OperatorType.OPERATOR_LESS, EntityOperator.LESS_THAN);
	    	        put(OperatorType.OPERATOR_LESS_OR_EQUAL, EntityOperator.LESS_THAN_EQUAL_TO);
	    	        put(OperatorType.OPERATOR_GREATER, EntityOperator.GREATER_THAN);
	    	        put(OperatorType.OPERATOR_GREATER_OR_EQUAL, EntityOperator.GREATER_THAN_EQUAL_TO);
	    	        put(OperatorType.OPERATOR_IS_IN, EntityOperator.IN);
	    	        put(OperatorType.OPERATOR_IS_NOT_IN, EntityOperator.NOT_IN);
	    	        put(OperatorType.OPERATOR_IS_LIKE, EntityOperator.LIKE);
	    	        put(OperatorType.OPERATOR_IS_NOT_LIKE, EntityOperator.NOT_LIKE);
	   		 }});
    
    public static final class EmailEngine {
        private EmailEngine() { }
        public static String EADVISOR_ENGINE = "EAdvisor";
    	public static String OFBIZ_ENGINE = "iCRM";
    	public static String OPENEMM_ENGINE = "OpenEMM";
    	public static String SENDGRID_ENGINE = "Sendgrid";
    	public static String POSTALSERVER_ENGINE = "Postalserver";
    }
    
    public static final Map<String, String> EXTERNAL_EMAIL_ENGINE = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{
	    	        put("EADVISOR_ENGINE", EmailEngine.EADVISOR_ENGINE);
	    	        put("OPENEMM_ENGINE", EmailEngine.OPENEMM_ENGINE);
	    	        put("SENDGRID_ENGINE", EmailEngine.SENDGRID_ENGINE);
	    	        put("POSTALSERVER_ENGINE", EmailEngine.POSTALSERVER_ENGINE);
	   		 }});
    public static final String SR_ALT = "SR_ALT";
    
    public static final class ModuleName {
        private ModuleName() { }
        public static String ACCOUNT = "ACCOUNT";
    	public static String ACTIVITY = "ACTIVITY";
    	public static String ADMIN = "ADMIN";
    	public static String CAMPAIGN = "CAMPAIGN";
    	public static String SEGMENTATION = "SEGMENTATION";
    	public static String CONTACT = "CONTACT";
    	public static String CUSTOMER = "CUSTOMER";
    	public static String ETL = "ETL";
    	public static String LEAD = "LEAD";
    	public static String OPPORTUNITY = "OPPORTUNITY";
    	public static String REBATE = "REBATE";
    	public static String SALES = "SALES";
    	public static String FSR = "FSR";
    	public static String FSR_CLIENT = "FSR_CLIENT";
    	public static String FSR_MOB = "FSR_MOB";
    	public static String TICKET = "TICKET";
    	public static String TICKET_CLIENT = "TICKET_CLIENT";
    }
    public static final Map<String, String> MODULE_COMP_ID = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{
	    	        put(ModuleName.ACCOUNT, "ACCOUNT_PORTAL");
	    	        put(ModuleName.ACTIVITY, "ACT_PORTAL");
	    	        put(ModuleName.ADMIN, "ADMIN_PORTAL");
	    	        put(ModuleName.CAMPAIGN, "COMP_1001");
	    	        put(ModuleName.SEGMENTATION, "COMP_1002");
	    	        put(ModuleName.CONTACT, "CONTACT_PORTAL");
	    	        put(ModuleName.CUSTOMER, "CUSTOMER_PROFILE");
	    	        put(ModuleName.ETL, "Etl-Process");
	    	        put(ModuleName.LEAD, "LEAD_PORTAL");
	    	        put(ModuleName.OPPORTUNITY, "OPPORTUNITY_PORTAL");
	    	        put(ModuleName.REBATE, "REBATE_PORTAL");
	    	        put(ModuleName.SALES, "SALES_PORTAL");
	    	        put(ModuleName.FSR, "SR-PORTAL");
	    	        put(ModuleName.FSR_CLIENT, "CLIENT-PORTAL");
	    	        put(ModuleName.FSR_MOB, "SR_MOB_PORTAL");
	    	        put(ModuleName.TICKET, "ticket-portal");
	    	        put(ModuleName.TICKET_CLIENT, "TICKETC-PORTAL");
	   		 }});
    
    public enum EnumDisplayType {
    	CODE("CODE"),
    	NAME("NAME"),
    	DESCRIPTION("DESCRIPTION"),
		;
		public String value;

		private EnumDisplayType(String value) {
			this.value = value;
		}
	}
    
}
