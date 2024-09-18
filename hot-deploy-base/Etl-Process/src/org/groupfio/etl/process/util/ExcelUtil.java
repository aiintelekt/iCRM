/**
 * 
 */
package org.groupfio.etl.process.util;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

/**
 * @author Group Fio
 *
 */
public class ExcelUtil {

	private static final String MODULE = ExcelUtil.class.getName();
	
	public static String readAbsStringCell(HSSFRow row, int index) {
		HSSFCell cell = row.getCell(index);
        if (cell == null || UtilValidate.isEmpty(cell.toString())) {
            return null;
        }
        
        /*String s = null;
        s = cell.getStringCellValue();*/
        
        // check if cell contains a number
        BigDecimal bd = null;
        try {
            double d = cell.getNumericCellValue();
            bd = BigDecimal.valueOf(d);
        } catch (Exception e) {
            // do nothing
        }

        String s = null;
        if (bd == null) {
            s = cell.toString().trim();
        } else {
            // if cell contains number parse it as long
            s = Long.toString(bd.longValue());
        	
        }

        if(s.equals("0")) return null;
        
        return s;        
    }
	
	public static Timestamp readDateCell(HSSFRow row, int index) {
        try {
			HSSFCell cell = row.getCell(index);
			if (cell == null || UtilValidate.isEmpty(cell.toString())) {
			    return null;
			}
			
			Date value = null;
			
			if(cell.getCellType()==HSSFCell.CELL_TYPE_STRING && cell.toString()!=null && !cell.toString().equals("")){	
				value = new Date(cell.toString().trim());
			}
			else{
				value = cell.getDateCellValue();
			}
			
			if(value!=null){
				Timestamp ts = new Timestamp(value.getTime());
			    if (ts == null) {
			        return null;
			    }
			    return ts;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			Debug.logError(e, MODULE);
		}
        return null;
    }
	
	/**
     * Helper method to check if an Excel row is empty.
     * @param row a <code>HSSFRow</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isNotEmpty(HSSFRow row) {
        if (row == null) {
            return false;
        }
        String s = row.toString();
        if (s == null) {
            return false;
        }
        return !"".equals(s.trim());
    }
    
    public static boolean isNotEmpty(XSSFRow row) {
        if (row == null) {
            return false;
        }
        String s = row.toString();
        if (s == null) {
            return false;
        }
        return !"".equals(s.trim());
    }

    /**
     * Helper method to read a String cell and auto trim it.
     * @param row a <code>HSSFRow</code> value
     * @param index the column index <code>int</code> value which is then casted to a short
     * @return a <code>String</code> value
     */
    public static String readStringCell(HSSFRow row, int index) {
        HSSFCell cell = row.getCell(index);
        if (cell == null || UtilValidate.isEmpty(cell.toString())) {
            return null;
        }

        // check if cell contains a number
        BigDecimal bd = null;
        try {
            double d = cell.getNumericCellValue();
            bd = BigDecimal.valueOf(d);
        } catch (Exception e) {
            // do nothing
        }

        String s = null;
        if (bd == null) {
            s = cell.toString().trim();
        } else {
            // if cell contains number parse it as long
        	
        	DecimalFormat df = new DecimalFormat("0.##");
        	s = df.format(bd); 
        }

        return s;
    }
    
    public static String readStringCell(XSSFRow row, int index) {
        XSSFCell cell = row.getCell(index);
        if (cell == null || UtilValidate.isEmpty(cell.toString())) {
            return null;
        }

        // check if cell contains a number
        BigDecimal bd = null;
        try {
            double d = cell.getNumericCellValue();
            bd = BigDecimal.valueOf(d);
        } catch (Exception e) {
            // do nothing
        }

        String s = null;
        if (bd == null) {
            s = cell.toString().trim();
        } else {
            // if cell contains number parse it as long
            s = Long.toString(bd.longValue());
        }

        return s;
    }

    /**
     * Helper method to read a Long cell and auto trim it.
     * @param row a <code>HSSFRow</code> value
     * @param index the column index <code>int</code> value which is then casted to a short
     * @return a <code>Long</code> value
     */
    public static Long readLongCell(HSSFRow row, int index) {
        try {
			HSSFCell cell = row.getCell(index);
			if (cell == null || UtilValidate.isEmpty(cell.toString())) {
			    return null;
			}

			BigDecimal value = null;
			
			if(cell.getCellType()==HSSFCell.CELL_TYPE_STRING && cell.toString()!=null && !cell.toString().equals("")){
				String cellValue = removeEmptyString( cell.toString().trim() );
				//System.out.println("cellValue: "+cellValue);
				value = new BigDecimal(cellValue);
			}
			else if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
				value = BigDecimal.valueOf(cell.getNumericCellValue());
			}
			
			//BigDecimal bd = BigDecimal.valueOf(cell.getNumericCellValue());
			if (value == null) {
			    return null;
			}
			return value.longValue();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			Debug.logError(e, MODULE);
		}
		return null;
    }

    /**
     * Helper method to read a BigDecimal cell and auto trim it.
     * @param row a <code>HSSFRow</code> value
     * @param index the column index <code>int</code> value which is then casted to a short
     * @return a <code>BigDecimal</code> value
     */
    public static BigDecimal readBigDecimalCell(HSSFRow row, int index) {
        HSSFCell cell = row.getCell(index);
        if (cell == null || UtilValidate.isEmpty(cell.toString())) {
            return null;
        }

        BigDecimal value = null;
        
        if(cell.getCellType()==HSSFCell.CELL_TYPE_STRING && cell.toString()!=null && !cell.toString().equals("")){
        	value = new BigDecimal(cell.toString().trim());
        }
        else if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
        	value = BigDecimal.valueOf(cell.getNumericCellValue());
        }
        
        return value;
    }
    
    /**
     * Gets the specified Excel File in the given directory.
     * @param path the path <code>String</code> of the directory to look files into
     * @param fileName the name of the file to find in the path
     * @return the File found
     */
    public static File getUploadedExcelFile(String path, String fileName) {
        String name = path;
        if (File.separatorChar == name.charAt(name.length() - 1)) {
            name += File.separatorChar;
        }
        name += fileName;

        if (UtilValidate.isNotEmpty(name)) {
            File file = new File(name);
            if (file.canRead()) {
                return file;
            } else {
                Debug.logWarning("File not found or can't be read " + name, MODULE);
                return null;
            }
        } else {
            Debug.logWarning("No path specified, doing nothing", MODULE);
            return null;
        }
    }
    
    /**
     * Gets the specified Excel File in the default directory.
     * @param fileName the name of the file to find in the path
     * @return the File found
     */
    /*public static File getUploadedExcelFile(String fileName) {
        return getUploadedExcelFile(CommonImportServices.getUploadPath(), fileName);
    }*/
    
    public static String removeEmptyString(String str){
    	StringBuilder sb = new StringBuilder(str);
	    String res = "";
	    for(int i=0;i<sb.length();++i){
	    	/*System.out.println(">"+sb.charAt(i)+"<");
	    	System.out.println(">"+((int) sb.charAt(i))+"<");*/
	    	if( ((int)sb.charAt(i))!=160 && ((int)sb.charAt(i))!=32 ){
	    		res+=sb.charAt(i);
	    	}
	        /*if(Character.isWhitespace(sb.charAt(i))){
	            sb.deleteCharAt(i);
	                            i--;
	        }*/
	    }
	    /*System.out.println(">"+sb.toString()+"<");
	    System.out.println("res>"+res+"<");*/
	    
	    return res;
    }
	
}
