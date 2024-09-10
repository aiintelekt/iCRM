/**
 * 
 */
package org.groupfio.etl.process.processor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.util.DefaultValueUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Group Fio
 *
 */
public class ElementFilterProcessor extends AbstractProcessor {

	private static String MODULE = ElementFilterProcessor.class.getName();
	
	/* (non-Javadoc)
	 * @see org.groupfio.etl.process.processor.AbstractProcessor#doProcess()
	 */
	@Override
	protected Map<String, Object> doProcess(Map<String, Object> context) throws Exception {
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		
		String modelName = (String) context.get("modelName");
		Map<String, Object> rowValue = (Map<String, Object>) context.get("rowValue");
		
		try {
			
			if (UtilValidate.isNotEmpty(rowValue)) {
				
				List<String> removeElems = new ArrayList<String>();
				
				for (String elementName : rowValue.keySet()) {
					
					List<GenericValue> modelElementFilterList = delegator.findByAnd("EtlModelElementFilter", UtilMisc.toMap("modelName", modelName, "elementName", elementName), null, false);
					if (UtilValidate.isNotEmpty(modelElementFilterList)) {
						
						boolean filterRes = true;
						boolean previousRes = true;
						for (GenericValue filter : modelElementFilterList) {

							String fieldName = filter.getString("fieldName");
				        	String condition = filter.getString("filterCondition");
				        	String value = filter.getString("filterValue");
				        	String operator = filter.getString("operator");
				        	
				        	String fieldValue = (String) rowValue.get(fieldName);
				        	
				        	if (UtilValidate.isNotEmpty(fieldValue) && UtilValidate.isNotEmpty(value)
				        			
				        			) {
				        		
				        		boolean curRes = false;
				        		
				        		switch (condition) {
								case "EQUAL":
									curRes = fieldValue.equals(value) ? true : false;
									break;
								case "NOT_EQUAL":
									curRes = !fieldValue.equals(value) ? true : false;						
									break;
								case "GATHER_THAN":
									try{
										BigDecimal fv = new BigDecimal(fieldValue);
										BigDecimal v = new BigDecimal(value);
										curRes = fv.doubleValue() > v.doubleValue() ? true : false;
									} catch (Exception e) {
										Debug.logError("Filter process GATHER_THAN failed>"+e.getMessage(), MODULE);
									}
									break;
								case "LESS_THAN":
									try{
										BigDecimal fv = new BigDecimal(fieldValue);
										BigDecimal v = new BigDecimal(value);
										curRes = fv.doubleValue() < v.doubleValue() ? true : false;
									} catch (Exception e) {
										Debug.logError("Filter process LESS_THAN failed>"+e.getMessage(), MODULE);
									}
									break;
								case "LIKE":
									curRes = fieldValue.contains(value) ? true : false;	
									break;
								case "NOT_LIKE":
									curRes = !fieldValue.contains(value) ? true : false;	
									break;
								default:
									break;
								}
				        		
				        		switch (operator) {
								case "AND":
									filterRes = previousRes && curRes ? true : false;
									break;
								case "OR":
									filterRes = previousRes || curRes ? true : false;
									break;
								default:
									break;
								}
				        		
				        		previousRes = curRes;
				        		
				        	}
							
						}
						
						if (filterRes) {
							
							try {
								String filterFunction = DefaultValueUtil.getModelElementDefaultValue(modelName, elementName, "custom_filterFunction", delegator);
								if (UtilValidate.isNotEmpty(filterFunction)) {
									
									Map<String, Object> inputNew = new HashMap<String, Object>();
									inputNew.put("rowValue", rowValue);
									inputNew.put("modelName", modelName);
									inputNew.put("elementName", elementName);
									
									Map<String, Object> res = dispatcher.runSync(filterFunction, inputNew);

									if (ServiceUtil.isSuccess(res)) {
										
										if (UtilValidate.isNotEmpty( res.get("elementValue") )) {
											rowValue.put(elementName, res.get("elementValue"));
										}
										
									}
									
								}
							} catch (Exception e) {
								Debug.logError("Custom element filter function ERROR: "+e.getMessage(), MODULE);
							}
							
							//removeElems.add(elementName);
						}
						
					} 
					
				}
				
				/*if (UtilValidate.isNotEmpty(removeElems)) {
					
					for (String removeElem : removeElems) {
						rowValue.remove(removeElem);
					}
					
				}*/
				
			}
			
			response.put("rowValue", rowValue);
			
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
		} catch (Exception e) {
			Debug.log("Exception in doProcess==="+e.getMessage());
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Element Filter process Failed...! modelName: "+modelName);
			
			return response;
		}
		
		response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;
		
	}

}
