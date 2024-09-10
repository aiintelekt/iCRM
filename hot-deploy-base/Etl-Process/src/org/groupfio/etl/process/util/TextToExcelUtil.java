package org.groupfio.etl.process.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * @author Group Fio
 * @since August 10, 2012
 *
 */
public class TextToExcelUtil {
	private static final String MODULE = TextToExcelUtil.class.getName();
	private String delimiter;
	
	private File txtFile;
	private File excelFile;
	
	List<String> fieldNames = new ArrayList<String>();
	
	public TextToExcelUtil(File txtFile, File excelFile) {
		this.txtFile = txtFile;
		this.excelFile = excelFile;
	}
	
	public final void processLineByLine() throws FileNotFoundException {
		
		try {
			
			String excelFileName = txtFile.getName().substring(0, txtFile.getName().lastIndexOf("."));
			
			WorkbookSettings wbSettings = new WorkbookSettings();			
			wbSettings.setLocale(new Locale("en", "EN"));

			WritableWorkbook workbook = Workbook.createWorkbook(excelFile, wbSettings);
			workbook.createSheet(excelFileName, 0);
			WritableSheet excelSheet = workbook.getSheet(0);			
			
			// Note that FileReader is used, not File, since File is not Closeable
			Scanner scanner = new Scanner(new FileReader(txtFile));
			try {
				// first use a Scanner to get each line
				int row = 0;
				while (scanner.hasNextLine()) {
					processLine(scanner.nextLine(), row, excelSheet);
					row++;
				}
			} finally {
				// ensure the underlying stream is always closed
				// this only has any effect if the item passed to the Scanner
				// constructor implements Closeable (which it does in this case).
				scanner.close();
				workbook.write();
				workbook.close();
			}
			
		} catch (Exception e) {
			/*e.printStackTrace();*/
			Debug.logError(e, MODULE);
		}
		
	}
	
	protected void processLine(String aLine, int row, WritableSheet excelSheet) {
		
		//System.out.println("New Line:::::::::::");
		
		try {
			// use a second Scanner to parse the content of each line
			Scanner scanner = new Scanner(aLine);
			scanner.useDelimiter(delimiter);
			int column = 0;
			while (scanner.hasNext()) {
				String value = scanner.next();
				excelSheet.addCell( new Label(column, row, value) );
				column++;
				log("value is : " + quote(value.trim()));
				
				if (row == 0 && UtilValidate.isNotEmpty(value)) {
					fieldNames.add(value.trim());
				}
				
			}
		}/* catch (RowsExceededException  e ) {
			e.printStackTrace();
		}*/ catch (WriteException e) {
			/*e.printStackTrace();*/
			Debug.logError(e, MODULE);
		} 
		
	}
	
	private static void log(Object aObject) {
		//System.out.println(String.valueOf(aObject));
		Debug.log(String.valueOf(aObject));
	}

	private String quote(String aText) {
		String QUOTE = "'";
		return QUOTE + aText + QUOTE;
	}
	
	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public File getTxtFile() {
		return txtFile;
	}

	public void setTxtFile(File txtFile) {
		this.txtFile = txtFile;
	}

	public File getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}
	
}