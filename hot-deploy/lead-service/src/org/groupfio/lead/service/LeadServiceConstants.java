package org.groupfio.lead.service;

/**
 * @author Sharif Ul Islam
 *
 */
public class LeadServiceConstants {

	// Resource bundles	
    public static final String configResource = "lead-service";
    public static final String uiLabelMap = "lead-serviceUiLabels";
    public static final String DEFAULT_RESP_PER = "admin";
    
	public static final class SourceInvoked {
        private SourceInvoked() { }
        public static final String API = "API";
        public static final String PORTAL = "PORTAL";
        public static final String UNKNOWN = "UNKNOWN";
    }
	
	public static class VerifyMode {
		private VerifyMode() {}
		public static String VERIFICATION = "VERIFICATION";
		public static String VALIDATION = "VALIDATION";
	}
	
	public static class UserAccountMode {
		private UserAccountMode() {}
		public static String FORGET_PASSWORD = "FORGET_PASSWORD";
		public static String UPDATE_PASSWORD = "UPDATE_PASSWORD";
		public static String CHANGE_USER_ID = "CHANGE_USER_ID";
		
	}
	public static class EmailVerifyStatus{
		private EmailVerifyStatus() {}
		
		public static String SENT="SENT";
		public static String VERIFIED="VERIFIED";
		
	}
}
