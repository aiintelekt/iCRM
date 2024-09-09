/**
 * 
 */
package org.fio.homeapps.export;

import org.fio.homeapps.export.ExportConstants.ExporterType;

/**
 * @author Sharif
 *
 */
public class ExporterFactory {

	public static Exporter getExporter(ExporterType type) {
		
		Exporter exporter = null;
		
		switch (type) {
			case CSV:
				exporter = new CsvExporter();
				break;
			case EXCEL:
				exporter = new ExcelExporter();
				break;
			default:
				break;
		}
		
		return exporter;
	}
	
}
