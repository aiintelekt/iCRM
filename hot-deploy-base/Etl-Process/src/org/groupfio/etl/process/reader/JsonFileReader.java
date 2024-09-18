/**
 * 
 */
package org.groupfio.etl.process.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.processor.DefaultValueProcessor;
import org.groupfio.etl.process.processor.ElementFilterProcessor;
import org.groupfio.etl.process.processor.ModelFilterProcessor;
import org.groupfio.etl.process.util.CommonUtil;
import org.groupfio.etl.process.util.DataUtil;
import org.groupfio.etl.process.util.ResponseUtils;
import org.ofbiz.base.conversion.JSONConverters.JSONToMap;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
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
public class JsonFileReader implements FileReader {

	private static String MODULE = JsonFileReader.class.getName();
	
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
			
			//List<String> fieldNames = new ArrayList<String>();

			JSON jsonFeed = JSON.from(FileUtil.readString("UTF-8", new File(filePath)));
			
			JSONToMap jsonMap = new JSONToMap();
			Map<String, Object> dataMap = jsonMap.convert(jsonFeed);
			
			List<LinkedHashMap<String, Object>> entryList = (ArrayList) dataMap.get("entries");
			
			if (UtilValidate.isNotEmpty(entryList)) {
				
				columnMap = prepareColumnMap(listId, entryList.get(0), delegator);
				
				if (UtilValidate.isNotEmpty(columnMap)) { 
					long counter = 0;
					for (LinkedHashMap<String, Object> entry : entryList) {
						Map<String, Object> rowValue = FastMap.newInstance();
						if (UtilValidate.isNotEmpty(entry) && CommonUtil.validateRange(rangeList, counter)) {
							int j = 0;
							for (String key : entry.keySet()) {
	
								String cellValue = (String) entry.get(key);
								
								String val = columnMap.get(j++);
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
			
		} catch (Exception e) {
			/*e.printStackTrace();*/
			Debug.logError(e, MODULE);
		}
		
		return rowValues;
	}
	
	private Map<Integer, String> prepareColumnMap(String listId, LinkedHashMap<String, Object> entry, Delegator delegator) {
		
		Map<Integer, String> columnMap = new HashMap<Integer, String>();
		
		try {
			
			int j = 0;
			for (String key : entry.keySet()) {
				//System.out.println(key);
				Debug.log(key);
				
				GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",
						UtilMisc.toMap("listName", listId, "etlFieldName", key), null, false));
				if (UtilValidate.isNotEmpty(checkValue)) {
					String tableColumnName = checkValue.getString("tableColumnName");
					columnMap.put(j++, tableColumnName);
				}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			Debug.logError(e, MODULE);
		}
		
		return columnMap;
	}
}
