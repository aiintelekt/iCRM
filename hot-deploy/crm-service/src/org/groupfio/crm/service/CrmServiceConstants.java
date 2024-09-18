/**
 * 
 */
package org.groupfio.crm.service;

/**
 * @author Sharif Ul Islam
 *
 */
public class CrmServiceConstants {
	
	// Resource bundles	
    public static final String configResource = "crm-portal";
    public static final String uiLabelMap = "crm-portalUiLabels";
    
    //public static final String CUST_GROUP_ID ="IRE_CUST_GEO";
    
    public static final int DEFAULT_BUFFER_SIZE = 102400;
    public static final int LOCKBOX_ITEM_SEQUENCE_ID_DIGITS = 5;
    
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
	public static final class CustomerPricingPrefConstats{
		public static final String LCIN = "LCIN";
		public static final String SUPPLIER = "SUPPLIER";
		public static final String ACCOUNT = "ACCOUNT";
		public static final String COUNTER_ACCOUNT = "COUNTER_ACCOUNT";
	}
	
	public static final class DomainEntityType {
        private DomainEntityType() { }
        public static final String OPPORTUNITY = "OPPORTUNITY";
        public static final String ACCOUNT = "ACCOUNT";
        public static final String LEAD = "LEAD";
        public static final String SERVICE_REQUEST = "SERVICE_REQUEST";
        public static final String CLIENT_SERVICE_REQUEST = "CLIENT_SERVICE_REQUEST";
    }
	
	public static final class SlaSetupConstants{
		private SlaSetupConstants() {}
		public static final String SLA_PERIOD_HOURS = "Hours";
		public static final String SLA_PERIOD_DAYS = "Days";
		public static final int TOTAL_MINUTES=60;
	}
	
}
