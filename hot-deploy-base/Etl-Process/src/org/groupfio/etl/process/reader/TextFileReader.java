/**
 * 
 */
package org.groupfio.etl.process.reader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.groupfio.etl.process.util.TextToExcelUtil;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastMap;

/**
 * @author Group Fio
 *
 */
public class TextFileReader implements FileReader {

	private static String MODULE = TextFileReader.class.getName();
	
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
		
		String fileName = (String) context.get("fileName");
		File targetLocation = (File) context.get("targetLocation");
		
		List<Map<String, Object>> rowValues = new ArrayList<Map<String, Object>>();
		// set it up as an Excel workbook
        POIFSFileSystem fs = null;
        HSSFWorkbook wb = null;
        try {
            Map<Integer, String> columnMap = new HashMap<Integer, String>();
            String excelFileName = CommonUtil.getAbsoulateFileName(fileName)+".xls";
            File excelFile = null;
            if (excelFileName != null) {
                excelFile = new File(targetLocation.getAbsolutePath() + File.separator + excelFileName);
            }
        	TextToExcelUtil excelUtil = new TextToExcelUtil(new File(filePath), excelFile);
        	excelUtil.setDelimiter(DefaultValueUtil.getTextDelimiterValue(listId, delegator));
        	
			excelUtil.processLineByLine();
			
			List<String> fieldNames = excelUtil.getFieldNames();
			
			int startRowNum = 1; 
			
			String isDatafileNoHeader = DefaultValueUtil.getModelDefaultValue(listId, "isDatafileNoHeader", delegator);
			if (UtilValidate.isNotEmpty(isDatafileNoHeader) && isDatafileNoHeader.equals("Y")) {
				
				GenericValue mappingElement = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",
						UtilMisc.toMap("listName", listId), null, false));
				
				
				String defaultFilePath = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/header/";
				String headerFilePath = EntityUtilProperties.getPropertyValue("Etl-Process", "etl.files.location", defaultFilePath, delegator);
				File file = new File(headerFilePath);
				if(!file.exists()){
					Debug.logError("Header file path is invalid----------", MODULE);
					return rowValues;
				}
				excelFileName = CommonUtil.getAbsoulateFileName(mappingElement.getString("fileName"))+".xls";
		        File excelFileHeader = new File(headerFilePath + excelFileName);
	        	
	        	excelUtil = new TextToExcelUtil(new File(mappingElement.getString("filePath")+mappingElement.getString("fileName")), excelFileHeader);
	        	excelUtil.setDelimiter(DefaultValueUtil.getTextDelimiterValue(listId, delegator));
	        	
				excelUtil.processLineByLine();
				
				fieldNames = excelUtil.getFieldNames();
				
				startRowNum = 0; 
				
			}
			
			if (UtilValidate.isEmpty(fieldNames)) {
				Debug.logWarning("fieldNames empty............", MODULE);
				return rowValues;
			}
			
            // this will auto close the FileInputStream when the constructor completes
            fs = new POIFSFileSystem(new FileInputStream(excelFile.getAbsolutePath()));
            wb = new HSSFWorkbook(fs);
            
            for (String excelTab : ExcelFileUploader.EXCEL_TABS) {
            	HSSFSheet sheet = wb.getSheetAt(0);
                if (sheet == null) {
                    Debug.logWarning("Did not find a sheet named " + excelTab + " in " + excelFile.getAbsolutePath() + ".  Will not be importing anything.", MODULE);
                } else {

                	columnMap = prepareColumnMap(listId, fieldNames, delegator);
                	
                	if (UtilValidate.isNotEmpty(columnMap)) { 
                		long counter = 0;
                    	int sheetLastRowNumber = sheet.getLastRowNum();
                    	for (int j = startRowNum; j <= sheetLastRowNumber; j++) {
                    		
                    		HSSFRow row = sheet.getRow(j);
                        	
                        	if (ExcelUtil.isNotEmpty(row) && CommonUtil.validateRange(rangeList, counter)) {
                        		
                        		Map<String, Object> rowValue = FastMap.newInstance();
                        		for (int i = 0; i <= row.getLastCellNum(); i++) {
                        			
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
                    	
                	} else {
                		
                		Debug.logError("Not found header information for model# "+listId, MODULE);
                		
                	}
                	
                }
            }
            
		} catch (Exception e) {
			/*e.printStackTrace();*/
			Debug.logError(e, MODULE);
		}finally{
			/*try{
				if(wb!=null)
					wb.close();
				if(fs!=null)
					fs.close();
			}catch(Exception e)
			{
				
			}*/
		}
		
		return rowValues;
	}
	
	private Map<Integer, String> prepareColumnMap(String listId, List<String> fieldNames, Delegator delegator) {
		
		Map<Integer, String> columnMap = new HashMap<Integer, String>();
		
		try {
			
			int j = 0;
			
			if (UtilValidate.isNotEmpty(fieldNames)) {
        		
				for (String fieldName : fieldNames) {
					
					GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",
    						UtilMisc.toMap("listName", listId, "etlFieldName", fieldName), null, false));
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
