/**
 * 
 */
package org.groupfio.etl.process.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.processor.DefaultValueProcessor;
import org.groupfio.etl.process.processor.ElementFilterProcessor;
import org.groupfio.etl.process.processor.ModelFilterProcessor;
import org.groupfio.etl.process.util.CommonUtil;
import org.groupfio.etl.process.util.DataUtil;
import org.groupfio.etl.process.util.DefaultValueUtil;
import org.groupfio.etl.process.util.ResponseUtils;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

import au.com.bytecode.opencsv.CSVReader;
import javolution.util.FastMap;

/**
 * @author Group Fio
 *
 */
public class CsvFileReader implements FileReader {

	private static String MODULE = CsvFileReader.class.getName();
	
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
			
			int i = 0;
			
			String isDatafileNoHeader = DefaultValueUtil.getModelDefaultValue(listId, "isDatafileNoHeader", delegator);
			if (UtilValidate.isNotEmpty(isDatafileNoHeader) && isDatafileNoHeader.equals("Y")) {
				
				i = 1;
				
				GenericValue mappingElement = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",
						UtilMisc.toMap("listName", listId), null, false));
				String headerFilePath = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/header/";
				headerFilePath = headerFilePath + mappingElement.getString("fileName");
				File headerFile = new File(headerFilePath);
				if (headerFile.exists()) {
					
					CSVReader reader = new CSVReader(new java.io.FileReader(headerFilePath));
					String[] nextLine;
					while ((nextLine = reader.readNext()) != null) {
						if (nextLine.length > 0) {
							for (int j = 0; j < nextLine.length; j++) {
								
								String cellValue = nextLine[j];
								
								if (UtilValidate.isNotEmpty(cellValue)) {
									GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",
											UtilMisc.toMap("listName", listId, "etlFieldName", cellValue), null, false));
									if (UtilValidate.isNotEmpty(checkValue)) {
										String tableColumnName = checkValue.getString("tableColumnName");
										columnMap.put(j, tableColumnName);
									}

								}
								
							}
							
						}
						
						break;

					}
					
				}
			}
			
			CSVReader reader = new CSVReader(new java.io.FileReader(filePath));
			String[] nextLine;
			long counter = 0;
			while ((nextLine = reader.readNext()) != null) {
				Map<String, Object> rowValue = FastMap.newInstance();
				i++;
				if (nextLine.length > 0 && CommonUtil.validateRange(rangeList, counter)) {
					for (int j = 0; j < nextLine.length; j++) {
						
						String cellValue = nextLine[j];
						
						if (UtilValidate.isNotEmpty(cellValue) && i == 1) {
							GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",
									UtilMisc.toMap("listName", listId, "etlFieldName", cellValue), null, false));
							if (UtilValidate.isNotEmpty(checkValue)) {
								String tableColumnName = checkValue.getString("tableColumnName");
								columnMap.put(j, tableColumnName);
							}

						}
						
						// mapping the data
						if (i > 1) {
							String val = columnMap.get(j);
							if (UtilValidate.isNotEmpty(val)) {
								
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
			
		} catch (Exception e) {
			/*e.printStackTrace();*/
			Debug.logError(e, MODULE);
		}
		
		return rowValues;
	}
	
}
