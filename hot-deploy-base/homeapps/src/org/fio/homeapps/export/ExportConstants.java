/**
 * 
 */
package org.fio.homeapps.export;

/**
 * @author Sharif
 *
 */
public class ExportConstants {

	public static final class ExportType {
		public ExportType() { }
        
        public static final String EXPORT_TYPE_CSV = "CSV";
        public static final String EXPORT_TYPE_EXCEL = "EXCEL";
        public static final String EXPORT_TYPE_XML = "XML";
    }
	
	public enum ExporterType {
		CSV("CSV"),
		EXCEL("EXCEL"),
		;

		public String value;

		private ExporterType(String value) {
			this.value = value;
		}
	}
	
	public static final class ProcessingStatus {
        private ProcessingStatus() { }
        public static final String PROCESSING_STATUS_NOT = "N";
    	public static final String PROCESSING_STATUS_STARTED = "Y";
    	public static final String PROCESSING_STATUS_COMPLETED = "C";
    	public static final String PROCESSING_STATUS_FAILED = "F";
    }
	
	public static final class ScheduleExportStatus {
        private ScheduleExportStatus() { }
        public static final String PENDING = "PENDING";
        public static final String FINISHED = "FINISHED";
        public static final String PROGRESSING = "PROGRESSING";
        public static final String ERROR = "ERROR";        
    }
	
	public static final class ExportDataType {
		public ExportDataType() { }
        
        public static final String OUTBOUND_CALL_LIST = "OUTBOUND_CALL_LIST";
        public static final String CUSTOMER_LIST = "CUSTOMER_LIST";
        public static final String ACCOUNT_LIST = "ACCOUNT_LIST";
    }
	
}
