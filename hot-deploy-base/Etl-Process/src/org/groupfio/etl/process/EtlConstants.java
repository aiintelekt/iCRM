/**
 * 
 */
package org.groupfio.etl.process;

/**
 * @author Group Fio
 *
 */
public class EtlConstants {

	public static final String RESPONSE_CODE = "code";
	public static final String RESPONSE_MESSAGE = "message";
	
	public static final String RESOURCE = "Etl-ProcessUiLabels";
	
	public static final String DEFAULT_DELIMITER = "TAB";
	
	public static final String NOTIFICATION_REASON_COMPLETE = "COMPLETE";
	public static final String NOTIFICATION_REASON_FAILURE = "FAILURE";

	public enum DelimiterValue {
		
		TAB("\t"), 
		COMMA(","), 
		SEMICOLON(";"),
		PIPELINE("\\|"),
		;
		
		private String value;

		DelimiterValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return value;
		}
		
	}
	
	public enum EtlModelElementFunction {
		
		TRIM("trim"), 
		
		CONCAT_PREFIX("concat_prefix"), 
		CONCAT_SUFFIX("concat_suffix"),
		
		SUBSTRING_START("substring_start"), 
		SUBSTRING_END("substring_end"), 
		
		STRING_REPLACE("string_replace"),
		
		ADD("numeric_add"),
		SUBTRACT("numeric_subtract"),
		MULTIPLY("numeric_multiply"),
		DIVIDE("numeric_divide"),
		
		CUSTOM_FUNCTION("custom_function"),
		
		MAX_LENGTH("max_length"),
		DEFAULT_VALUE("default_value"),
		;

		private String value;

		EtlModelElementFunction(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return value;
		}
		
	}
	
	public static final class LockboxStagingImportStatus {
        private LockboxStagingImportStatus() { }
        
        /** Imported. */
        public static final String LBBATCH_IMPORTED = "LBIMP_IMPORTED";        
        /** Error. */
        public static final String LBBATCH_ERROR = "LBIMP_ERROR";
        /** Failed. */
        public static final String LBBATCH_FAILED = "LBIMP_FAILED";
        /** Processing */
        public static final String LBBATCH_PROCESSING = "LBIMP_PROCESSING";
        /** Not processed. */
        public static final String LBBATCH_NOT_PROC = "LBIMP_NOT_PROC";
        /** Ready */
        public static final String LBBATCH_READY = "LBIMP_READY";
        /** Processed */
        public static final String LBBATCH_PROCESSED = "LBIMP_PROCESSED";
    }
	
	public static final class ValidationAuditType {
        private ValidationAuditType() { }
        public static final String VAT_LEAD_IMPORT = "VAT_LEAD_IMPORT";
        public static final String VAT_RM_REASSIGN = "VAT_RM_REASSIGN";
        public static final String VAT_LEAD_DEDUP = "VAT_LEAD_DEDUP";
    }

}
