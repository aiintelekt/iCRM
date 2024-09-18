/**
 * 
 */
package org.groupfio.etl.process.uploader;

/**
 * @author Group Fio
 *
 */
public final class FileUploaderFactory {

	private static final CsvFileUploader CSV_FILE_UPLOADER = new CsvFileUploader();
	private static final XmlFileUploader XML_FILE_UPLOADER = new XmlFileUploader();
	private static final JsonFileUploader JSON_FILE_UPLOADER = new JsonFileUploader();
	private static final ExcelFileUploader EXCEL_FILE_UPLOADER = new ExcelFileUploader();
	private static final ExcelXlsxFileUploader EXCEL_XLSX_FILE_UPLOADER = new ExcelXlsxFileUploader();
	private static final TextFileUploader TEXT_FILE_UPLOADER = new TextFileUploader();
	
	public static CsvFileUploader getCsvFileUploader () {
		return CSV_FILE_UPLOADER;
	}
	
	public static XmlFileUploader getXmlFileUploader () {
		return XML_FILE_UPLOADER;
	}
	
	public static JsonFileUploader getJsonFileUploader () {
		return JSON_FILE_UPLOADER;
	}
	
	public static ExcelFileUploader getExcelFileUploader () {
		return EXCEL_FILE_UPLOADER;
	}
	
	public static ExcelXlsxFileUploader getExcelXlsxFileUploader () {
		return EXCEL_XLSX_FILE_UPLOADER;
	}
	
	public static TextFileUploader getTextFileUploader () {
		return TEXT_FILE_UPLOADER;
	}
	
}
