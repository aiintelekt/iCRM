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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.processor.DefaultValueProcessor;
import org.groupfio.etl.process.processor.ElementFilterProcessor;
import org.groupfio.etl.process.processor.ModelFilterProcessor;
import org.groupfio.etl.process.uploader.ExcelFileUploader;
import org.groupfio.etl.process.util.CommonUtil;
import org.groupfio.etl.process.util.DataUtil;
import org.groupfio.etl.process.util.DefaultValueUtil;
import org.groupfio.etl.process.util.ExcelUtil;
import org.groupfio.etl.process.util.ResponseUtils;
import org.ofbiz.base.component.ComponentConfig;
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
public class ExcelFileReader implements FileReader {

	private static String MODULE = ExcelFileReader.class.getName();
	
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
		
		try {
			
			Map<Integer, String> columnMap = new HashMap<Integer, String>();
			int startRowNum = 1; 
			
			String isDatafileNoHeader = DefaultValueUtil.getModelDefaultValue(listId, "isDatafileNoHeader", delegator);
			if (UtilValidate.isNotEmpty(isDatafileNoHeader) && isDatafileNoHeader.equals("Y")) {
				GenericValue mappingElement = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",
						UtilMisc.toMap("listName", listId), null, false));
				
				String headerFilePath = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/header/";
				headerFilePath = headerFilePath + mappingElement.getString("fileName");
				
				startRowNum = 0;
				
				POIFSFileSystem fs = null;
		        HSSFWorkbook wb = null;
	            // this will auto close the FileInputStream when the constructor completes
	            fs = new POIFSFileSystem(new FileInputStream(headerFilePath));
	            wb = new HSSFWorkbook(fs);
	            
	            HSSFSheet sheet = wb.getSheetAt(0);
	            
	            columnMap = prepareColumnMap(listId, sheet.getRow(0), delegator);
				
			}
			
			// set it up as an Excel workbook
	        POIFSFileSystem fs = null;
	        HSSFWorkbook wb = null;
            // this will auto close the FileInputStream when the constructor completes
            fs = new POIFSFileSystem(new FileInputStream(filePath));
            wb = new HSSFWorkbook(fs);
            
            for (String excelTab : ExcelFileUploader.EXCEL_TABS) {
            	HSSFSheet sheet = wb.getSheetAt(0);
                if (sheet == null) {
                    Debug.logWarning("Did not find a sheet named " + excelTab + " in " + CommonUtil.getAbsoulateFileName(filePath) + ".  Will not be importing anything.", MODULE);
                } else {
                	
                	if (startRowNum == 1) {
                		columnMap = prepareColumnMap(listId, sheet.getRow(0), delegator);
                	}
                	
                	if (UtilValidate.isNotEmpty(columnMap)) { 
                		long counter = 0;
                		
                    	int sheetLastRowNumber = sheet.getLastRowNum();
                    	for (int j = startRowNum; j <= sheetLastRowNumber; j++) {
                    		
                    		HSSFRow row = sheet.getRow(j);
                        	
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
    									Debug.logInfo("processorContext>> "+processorContext, MODULE);
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
		}
		
		return rowValues;
	}
	
	private Map<Integer, String> prepareColumnMap(String listId, HSSFRow entry, Delegator delegator) {
		
		Map<Integer, String> columnMap = new HashMap<Integer, String>();
		
		try {
			
			int j = 0;
			
			if (ExcelUtil.isNotEmpty(entry)) {
        		
        		Iterator cellIter = entry.cellIterator();
                while(cellIter.hasNext()){
                    HSSFCell cell = (HSSFCell) cellIter.next();
                    
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
