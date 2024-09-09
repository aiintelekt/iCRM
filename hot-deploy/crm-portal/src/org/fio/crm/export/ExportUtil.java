/**
 * 
 */
package org.fio.crm.export;

import org.fio.crm.export.ExportConstants.ExportType;
import org.ofbiz.base.util.UtilValidate;

/**
 * @author Sharif
 *
 */
public class ExportUtil {

	public static String getFileExtension(String exportType) {
		String fileExtension = "";
		if (UtilValidate.isNotEmpty(exportType)) {
			
			switch (exportType) {
			case ExportType.EXPORT_TYPE_CSV:
				fileExtension = ".csv";
				break;

			case ExportType.EXPORT_TYPE_EXCEL:
				fileExtension = ".xls";
				break;
			}
			
		}
		
		return fileExtension;
	}
	
	public static String getContentType(String exportType) {
		String fileExtension = "";
		if (UtilValidate.isNotEmpty(exportType)) {
			
			switch (exportType) {
			case ExportType.EXPORT_TYPE_CSV:
				fileExtension = "text/csv";
				break;

			case ExportType.EXPORT_TYPE_EXCEL:
				fileExtension = "application/vnd.ms-excel";
				break;
			}
			
		}
		
		return fileExtension;
	}
	
}
