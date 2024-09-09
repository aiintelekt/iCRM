/**
 * 
 */
package org.groupfio.etl.process.reader;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.processor.DefaultValueProcessor;
import org.groupfio.etl.process.processor.ElementFilterProcessor;
import org.groupfio.etl.process.processor.ModelFilterProcessor;
import org.groupfio.etl.process.uploader.ExcelFileUploader;
import org.groupfio.etl.process.util.CommonUtil;
import org.groupfio.etl.process.util.DataUtil;
import org.groupfio.etl.process.util.ExcelUtil;
import org.groupfio.etl.process.util.ResponseUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastMap;

/**
 * @author Group Fio
 *
 */
public class ExcelXlsxFileReader implements FileReader {

	private static String MODULE = ExcelXlsxFileReader.class.getName();
	
	/* (non-Javadoc)
	 * @see org.groupfio.etl.process.reader.FileReader#read()
	 */
	@Override
	public List<Map<String, Object>> read(Map<String, Object> context) {
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		String filePath = (String) context.get("filePath");
		String listId = (String) context.get("listId");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		Map<Long, Long> rangeList = (Map<Long, Long>) context.get("rangeList");
		
		List<Map<String, Object>> rowValues = new ArrayList<Map<String, Object>>();
		// set it up as an Excel workbook
		FileInputStream fs = null;
		XSSFWorkbook wb = null;
		try {
			
			Map<Integer, String> columnMap = new HashMap<Integer, String>();
			
			
            // this will auto close the FileInputStream when the constructor completes
            fs = new FileInputStream(filePath);
            wb = new XSSFWorkbook(fs);
            
            for (String excelTab : ExcelFileUploader.EXCEL_TABS) {
            	XSSFSheet sheet = wb.getSheetAt(0);
                if (sheet == null) {
                    Debug.logWarning("Did not find a sheet named " + excelTab + " in " + CommonUtil.getAbsoulateFileName(filePath) + ".  Will not be importing anything.", MODULE);
                } else {

                	columnMap = prepareColumnMap(listId, sheet.getRow(0), delegator);
                	
                	if (UtilValidate.isNotEmpty(columnMap)) { 
                		long counter = 0;
                		
                    	int sheetLastRowNumber = sheet.getLastRowNum();
                    	for (int j = 1; j <= sheetLastRowNumber; j++) {
                    		
                    		XSSFRow row = sheet.getRow(j);
                        	
                        	if (ExcelUtil.isNotEmpty(row) && CommonUtil.validateRange(rangeList, counter)) {
                        		
                        		Map<String, Object> rowValue = FastMap.newInstance();
                        		
                        		for (int i = 0; i <= row.getLastCellNum(); i++) {
                        			
                        			//HSSFCell cell = row.getCell(i);
                        			String cellValue = ExcelUtil.readStringCell(row, i);
                        			
                        			String val = columnMap.get(i);
        							if (UtilValidate.isNotEmpty(val)){
        								
        								Map<String, Object> processorContext = new HashMap<String, Object>();
    									processorContext.put("delegator", delegator);
    									processorContext.put("modelName", listId);
    									processorContext.put("elementName", DataUtil.getEtlFieldName(delegator, listId, val));
    									processorContext.put("cellValue", cellValue);
    									
    									DefaultValueProcessor processor = new DefaultValueProcessor();
    									Map<String, Object> processRes = processor.process(processorContext);
    									
    									if (ResponseUtils.isSuccess(processRes)) {
    										cellValue = ParamUtil.getString(processRes, "cellValue");
    									}
        								
        								rowValue.put(val, cellValue);
        							}
                        			
                        		}
                        		
                        		// Apply filter [start]
                        		
                        		boolean filterRes = true;
                        		
                        		if (!isExecuteModelProcess) {
	                        		Map<String, Object> processorContext = new HashMap<String, Object>();
									processorContext.put("delegator", delegator);
									processorContext.put("dispatcher", dispatcher);
									processorContext.put("modelName", listId);
									processorContext.put("rowValue", rowValue);
									
									ElementFilterProcessor elementFilterProcessor = new ElementFilterProcessor();
									Map<String, Object> processRes = elementFilterProcessor.process(processorContext);
									
									ModelFilterProcessor modelFilterProcessor = new ModelFilterProcessor();
									processRes = modelFilterProcessor.process(processorContext);
									
									if (ResponseUtils.isSuccess(processRes)) {
										if (UtilValidate.isNotEmpty(processRes.get("filterRes"))) {
											filterRes = (Boolean) processRes.get("filterRes");
										} else {
											filterRes = false;
										}
									}
                        		}
                        		
                        		// Apply filter [end]
                        		
                        		Debug.log("rowValue: "+rowValue, MODULE);
                        		
                        		if (UtilValidate.isNotEmpty(rowValue) && filterRes) {
        							rowValues.add(rowValue);
        						}
                        		
                        	}
                        	
                        	counter++;
                    	}
                	}
                	
                }
            }
			
		} catch (Exception e) {
			/*e.printStackTrace();*/
			Debug.logError(e, MODULE);
		}finally{
			try{
				if(wb!=null)
					wb.close();
				if(fs!=null)
					fs.close();
			}catch(Exception e)
			{
				
			}
		}
		
		return rowValues;
	}
	
	private Map<Integer, String> prepareColumnMap(String listId, XSSFRow entry, Delegator delegator) {
		
		Map<Integer, String> columnMap = new HashMap<Integer, String>();
		
		try {
			
			int j = 0;
			
			if (ExcelUtil.isNotEmpty(entry)) {
        		
        		Iterator cellIter = entry.cellIterator();
                while(cellIter.hasNext()){
                    XSSFCell cell = (XSSFCell) cellIter.next();
                    
                    GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",
    						UtilMisc.toMap("listName", listId, "etlFieldName", cell.toString()), null, false));
    				if (UtilValidate.isNotEmpty(checkValue)) {
    					String tableColumnName = checkValue.getString("tableColumnName");
    					columnMap.put(j++, tableColumnName);
    				}
                    
                }
        		Debug.log("Header names: "+columnMap, MODULE);
        	}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			Debug.logError(e, MODULE);
		}
		
		return columnMap;
	}
	
}
