/**
 * 
 */
package org.groupfio.etl.process.reader;

/**
 * @author Group Fio
 *
 */
public class FileReaderFactory {

	private static final CsvFileReader CSV_FILE_READER = new CsvFileReader();
	private static final ExcelFileReader EXCEL_FILE_READER = new ExcelFileReader();
	private static final ExcelXlsxFileReader EXCEL_XLSX_FILE_READER = new ExcelXlsxFileReader();
	private static final JsonFileReader JSON_FILE_READER = new JsonFileReader();
	private static final TextFileReader TEXT_FILE_READER = new TextFileReader();
	private static final XmlFileReader XML_FILE_READER = new XmlFileReader();
	
	public static CsvFileReader getCsvFileReader() {
		return CSV_FILE_READER;
	}
	
	public static ExcelFileReader getExcelFileReader() {
		return EXCEL_FILE_READER;
	}
	
	public static ExcelXlsxFileReader getExcelXlsxFileReader() {
		return EXCEL_XLSX_FILE_READER;
	}
	
	public static JsonFileReader getJsonFileReader() {
		return JSON_FILE_READER;
	}
	
	public static TextFileReader getTextFileReader() {
		return TEXT_FILE_READER;
	}
	
	public static XmlFileReader getXmlFileReader() {
		return XML_FILE_READER;
	}
	
	public static FileReader getFileReader(String fileType) {
		
		if (fileType.equals("csv")) {
			return FileReaderFactory.getCsvFileReader();
		} else if (fileType.equals("xml")) {
			return FileReaderFactory.getXmlFileReader();
		} else if (fileType.equals("json")) {
			return FileReaderFactory.getJsonFileReader();
		} else if (fileType.equals("xls")) {
			return FileReaderFactory.getExcelFileReader();
		} else if (fileType.equals("xlsx")) {
			return FileReaderFactory.getExcelXlsxFileReader();
		} else if (fileType.equals("txt") || fileType.equals("dat")) {
			return FileReaderFactory.getTextFileReader();
		} 
		
		return null;
	}
	
}
