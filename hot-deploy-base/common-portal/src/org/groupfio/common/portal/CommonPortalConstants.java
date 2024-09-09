package org.groupfio.common.portal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sharif Ul Islam
 *
 */
public class CommonPortalConstants {

	// Resource bundles	
    public static final String configResource = "common-portal";
    public static final String uiLabelMap = "common-portalUiLabels";
    
	public static final class SourceInvoked {
        private SourceInvoked() { }
        public static final String API = "API";
        public static final String PORTAL = "PORTAL";
        public static final String UNKNOWN = "UNKNOWN";
    }
	
	public static final class LookupType {
        private LookupType() { }
        public static final String STATIC_DATA = "STATIC_DATA";
        public static final String PICKER_DATA = "PICKER_DATA";
        public static final String DYNAMIC_DATA = "DYNAMIC_DATA";
    }
	
	public static final class FieldType {
        private FieldType() { }
        public static final String TEXT = "TEXT";
        public static final String DATE = "DATE";
        public static final String DROPDOWN = "DROPDOWN";
        public static final String PICKER = "PICKER";
        public static final String NUMBER = "NUMBER";
        public static final String DATE_TIME = "DATE_TIME";
        public static final String RADIO = "RADIO";
        public static final String TEXT_AREA = "TEXT_AREA";
    }
	
	public static final class LayoutType {
        private LayoutType() { }
        public static final String ONE_COLUMN = "1C";
        public static final String TWO_COLUMN = "2C";
        public static final String THREE_COLUMN = "3C";
    }
	
	public static final String SR_COMPLAINT = "10001";
	
	public static final class DateTimeTypeConstant {
		private DateTimeTypeConstant() {}
		public static final String TIMESTAMP = "timestamp";
		public static final String DATE = "date";
		public static final String SQL_DATE = "sqlDate";
		public static final String UTIL_DATE = "utilDate";
		public static final String STRING = "string";
	}
	
	public static final class srOpenStatuses {
        private srOpenStatuses() { }
        public static final String SR_OPEN = "SR_OPEN";
        public static final String SR_PENDING = "SR_PENDING";
        public static final String SR_ASSIGNED = "SR_ASSIGNED";
        public static final String SR_INFO_PROV = "SR_INFO_PROV";
        public static final String SR_OVER_DUE = "SR_OVER_DUE";
        public static final String SR_ESCALATED = "SR_ESCALATED";
        public static final String SR_RESEARCHING = "SR_RESEARCHING";
		public static final String SR_IN_PROGRESS = "SR_IN_PROGRESS";
		public static final String SR_FEED_PROVIDED = "SR_FEED_PROVIDED";

    }
	
	public static final class srClosedStatuses {
		private srClosedStatuses() { }
		public static final String SR_CLOSED = "SR_CLOSED";
		public static final String SR_CANCELLED = "SR_CANCELLED";
	}
	
	public static final class SrSearchType {
        private SrSearchType() { }
        public static final String MY_SRS = "my-srs";
        public static final String MY_OPEN_SRS = "my-open-srs";
        public static final String MY_CLOSED_SRS = "my-closed-srs";
        public static final String UN_ASSIGNED_SRS = "un-assigned-srs";
    }
	
	public static final class activityOpenStatuses {
		private activityOpenStatuses() { }
		public static final String IA_OPEN = "IA_OPEN";
		public static final String IA_MIN_PROGRESS = "IA_MIN_PROGRESS";
		public static final String IA_MSCHEDULED = "IA_MSCHEDULED";
	}
	
	public static final class DomainEntityType {
        private DomainEntityType() { }
        public static final String OPPORTUNITY = "OPPORTUNITY";
        public static final String ACCOUNT = "ACCOUNT";
        public static final String LEAD = "LEAD";
        public static final String SERVICE_REQUEST = "SERVICE_REQUEST";
        public static final String CONTACT = "CONTACT";
        public static final String RELATED_OPPORTUNITY = "RELATED_OPPORTUNITY";
        public static final String ADD_RELATED_OPPORTUNITY = "ADD_RELATED_OPPORTUNITY";
        public static final String SUBSCRIPTION = "SUBSCRIPTION";
        public static final String SUBS_PRODUCT = "SUBS_PRODUCT";
        public static final String REBATE = "REBATE";
        public static final String CUSTOMER = "CUSTOMER";
        public static final String CLIENT_SERVICE_REQUEST = "CLIENT_SERVICE_REQUEST";
        public static final String ACTIVITY = "ACTIVITY";
        public static final String APV_TPL = "APV_TPL";
        public static final String SQL_GRP = "SQL_GRP";
        public static final String SERVICE = "SERVICE";
        public static final String PRODUCT_PROMO_CODE = "PRODUCT_PROMO_CODE";
        public static final String CAMPAIGN = "CAMPAIGN";
    }
	
	public static final Map<String, String> DOMAIN_ENTITY_TYPE = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("OPPORTUNITY", "OPPORTUNITY");
	    	        put("ACCOUNT", "ACCOUNT");
	    	        put("LEAD", "LEAD");
	    	        put("SERVICE_REQUEST", "SERVICE_REQUEST");
	    	        put("CONTACT", "CONTACT");
	    	        put("RELATED_OPPORTUNITY", "RELATED_OPPORTUNITY");
	    	        put("ADD_RELATED_OPPORTUNITY", "ADD_RELATED_OPPORTUNITY");
	    	        put("SUBSCRIPTION", "SUBSCRIPTION");
	    	        put("SUBS_PRODUCT", "SUBS_PRODUCT");
	    	        put("REBATE", "REBATE");
	    	        put("CUSTOMER", "CUSTOMER");
	    	        put("CLIENT_SERVICE_REQUEST", "CLIENT_SERVICE_REQUEST");
	    	        put("ACTIVITY", "ACTIVITY");
	    	        put("APV_TPL", "APV_TPL");
	    	        put("SERVICE", "SERVICE");
	   		 }});
	
	public static final class GlobalParameter { 
		private GlobalParameter() {} 
		
		public static final String GLOBAL_PARAMS = "GLOBAL_PARAMS";
		public static final String IS_SECURITY_MATRIX_ENABLE = "SECURITY_MODE_OPERATION_ACCESS";
		public static final String COMMON_TEAMS = "COMMON_TEAMS";
		public static final String CAMPAIGN_EMAIL_TYPE = "CAMPAIGN_EMAIL_TYPE";
		public static final String DASHBOARD_ACTIVITY_STATUS = "DASHBOARD_ACTIVITY_STATUS";
		
	}
	
	public static final class SlaSetupConstants{
		private SlaSetupConstants() {}
		public static final String SLA_PERIOD_HOURS = "Hours";
		public static final String SLA_PERIOD_DAYS = "Days";
		public static final int TOTAL_MINUTES=60;
	}
	
	public static final Map<String, String> PARTY_DOMAIN_ENTITY_TYPE = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("ACCOUNT", "ACCOUNT");
	    	        put("LEAD", "LEAD");
	    	        put("CONTACT", "CONTACT");
	    	        put("CUSTOMER", "CUSTOMER");
	   		 }});
	
	public static final Map<String, String> COMMON_ATTACHMENT_ENTITY_TYPE = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("REBATE", "REBATE");
	    	        put("APV_TPL", "APV_TPL");
	    	        put("TEMPLATE", "TEMPLATE");
	    	        put("SQL_GRP", "SQL_GRP");
	   		 }});
	
	public static final Map<String, String> COMMON_NOTE_ENTITY_TYPE = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("REBATE", "REBATE");
	    	        put("APV_TPL", "APV_TPL");
	    	        put("SQL_GRP", "SQL_GRP");
	   		 }});
	
	public static final Map<String, String> SERVICE_DOMAIN_ENTITY_TYPE = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("SERVICE_REQUEST", "SERVICE_REQUEST");
	    	        put("SERVICE", "SERVICE");
	   		 }});
	
	public static final int SLA_AT_RISK = 1;
	
	public static final class EventResponse { 
		private EventResponse() {} 
		
		public static final String STATUS = "status";
		public static final String MESSAGE = "message";
		public static final String RESPONSE = "response";
		public static final String SUCCESS = "success";
		public static final String ERROR = "error";
		
	}

	public static final class CMConstants {
		private CMConstants() {}

		public static final String DEFAULT_LN_CONFIG_ID = "DEFAULT_CONFIG";
		public static final int DEFAULT_CHECK_NUMBER = 9;

		public static final class LoyaltyCodeStatus {
			private LoyaltyCodeStatus() { }

			public static final String LOYALTY_CODE_STATUS_REDEEMED = "REDEEMED";
			public static final String LOYALTY_CODE_STATUS_APPROVED = "APPROVED";
			public static final String LOYALTY_CODE_STATUS_CREATED = "CREATED";
			public static final String LOYALTY_CODE_STATUS_ISSUED = "ISSUED";
		}

	}
    public static final String LOYALTY_POINTS = "loyaltyPointsId";
    public static final String SR_ALT = "SR_ALT";

	public static class EmailVerifyStatus{
		private EmailVerifyStatus() {}
		
		public static String SENT="SENT";
		public static String VERIFIED="VERIFIED";
		
	}
}
