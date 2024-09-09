/**
 * 
 */
package org.fio.crm.export;

/**
 * @author Sharif
 *
 */
public class ExportConstants {

	public static final class ExportType {
		public ExportType() { }
        
        public static final String EXPORT_TYPE_CSV = "CSV";
        public static final String EXPORT_TYPE_EXCEL = "EXCEL";
    }
	
	public enum ExporterType {
		CSV("CSV"),
		EXCEL("EXCEL"),
		;

		protected String value;

		private ExporterType(String value) {
			this.value = value;
		}
		
	}
	
}
