/**
 * 
 */
package org.groupfio.etl.process.reader;

import java.io.FileInputStream;
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
import org.groupfio.etl.process.util.ResponseUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javolution.util.FastMap;

/**
 * @author Group Fio
 *
 */
public class XmlFileReader implements FileReader {

	private static String MODULE = XmlFileReader.class.getName();
	
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
			
			Document doc = UtilXml.readXmlDocument(new FileInputStream(filePath), "");
			
			NodeList nList = doc.getElementsByTagName("entry");

			//System.out.println("----------------------------");
			
			if (UtilValidate.isNotEmpty(nList) && nList.getLength() > 0) {
				
				columnMap = prepareColumnMap(listId, nList, delegator);
				
				if (UtilValidate.isNotEmpty(columnMap)) { 
					long counter = 0;
					for (int j = 0; j < nList.getLength(); j++) {
						Node nNode = nList.item(j);
						
						//System.out.println("\nCurrent Element :" + nNode.getNodeName());
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							
							if (!CommonUtil.validateRange(rangeList, counter)) {
								counter++;
								continue;
							}
							
							Element eElement = (Element) nNode;
							
							i = 0;
							Map<String, Object> rowValue = FastMap.newInstance();
							
							for (Element elm : UtilXml.childElementList(eElement)) {
								//System.out.println("> "+elm.getNodeName());
								
								String cellValue = UtilXml.elementValue(elm);
								
								String val = columnMap.get(i++);
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
							
							counter++;
						}
						
					}
				}
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e, MODULE);
		}
		
		return rowValues;
	}
	
	private Map<Integer, String> prepareColumnMap(String listId, NodeList nList, Delegator delegator) {
		
		Map<Integer, String> columnMap = new HashMap<Integer, String>();
		
		try {
			Node nNode = nList.item(0);
			//System.out.println("\nCurrent Element :" + nNode.getNodeName());
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				int j = 0;
				for (Element elm : UtilXml.childElementList(eElement)) {
					//System.out.println("> "+elm.getNodeName());
					
					GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",
							UtilMisc.toMap("listName", listId, "etlFieldName", elm.getNodeName()), null, false));
					if (UtilValidate.isNotEmpty(checkValue)) {
						String tableColumnName = checkValue.getString("tableColumnName");
						columnMap.put(j++, tableColumnName);
					}
					
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Debug.logError(e, MODULE);
		}
		
		return columnMap;
	}
	
}
