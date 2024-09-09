package org.groupfio.customfield.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sharif Ul Islam
 *
 */
public class CustomfieldServiceConstants {

	// Resource bundles	
    public static final String configResource = "customfield-service";
    public static final String uiLabelMap = "customfield-serviceUiLabels";
    
    public static final int DEFAULT_BUFFER_SIZE = 102400;
    public static final int LOCKBOX_ITEM_SEQUENCE_ID_DIGITS = 5;
    
    public static final String RESPONSE_CODE = "code";
	public static final String RESPONSE_MESSAGE = "message";
	
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
	
	public static final class GroupType {
        private GroupType() { }
        public static final String CUSTOM_FIELD = "CUSTOM_FIELD";
        public static final String SEGMENTATION = "SEGMENTATION";
        public static final String ATTRIBUTE = "ATTRIBUTE";
        public static final String ECONOMIC_METRIC = "ECONOMIC_METRIC";
        public static final String CONTACT_FIELD = "CONTACT_FIELD_LIST";
    }
	
	public static final class AttributeFieldType {
        private AttributeFieldType() { }
        public static final String SINGLE = "SINGLE";
        public static final String MULTIPLE = "MULTIPLE";
    }
	
	public static final class SegmentationValueCapture {
        private SegmentationValueCapture() { }
        public static final String SINGLE = "SINGLE";
        public static final String MULTIPLE = "MULTIPLE";
        public static final String RANGE = "RANGE";
    }
	
	public static final class EconomicMetricValueCapture {
        private EconomicMetricValueCapture() { }
        public static final String SINGLE = "SINGLE";
        public static final String MULTIPLE = "MULTIPLE";
        public static final String RANGE = "RANGE";
    }
	
	public enum WriterType {
		ENTITY("ENTITY"), 
		;

		public String value;

		private WriterType(String value) {
			this.value = value;
		}
		
	}
	
	public enum ReaderType {
		ENTITY("ENTITY"), 
		;

		public String value;

		private ReaderType(String value) {
			this.value = value;
		}
		
	}
	
	public static final Map<String, String> GROUPING_CODE_CAMPAIGN = 
   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
    	        put("EMAIL", "CAMPAIGN_EMAIL");
    	        put("PHONE_CALL", "CAMPAIGN_PHONE");
    	        put("SMS", "CAMPAIGN_SMS");
    	        put("SALES_TRIGGER_TYPE", "CAMPAIGN_SALES_TRIGGER");
   		 }});
	
	public static final Map<String, String> IS_HIDE = 
	   		 Collections.unmodifiableMap(new HashMap<String, String>() {{ 
	    	        put("Y", "Yes");
	    	        put("N", "No");
	   		 }});
	
}
