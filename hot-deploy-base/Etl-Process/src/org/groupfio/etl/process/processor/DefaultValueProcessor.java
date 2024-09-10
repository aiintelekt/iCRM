/**
 * 
 */
package org.groupfio.etl.process.processor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.EtlConstants.EtlModelElementFunction;
import org.groupfio.etl.process.ResponseCodes;
import org.ofbiz.base.conversion.JSONConverters.JSONToMap;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

/**
 * @author Group Fio
 *
 */
public class DefaultValueProcessor extends AbstractProcessor {

	private static String MODULE = DefaultValueProcessor.class.getName();
	
	/* (non-Javadoc)
	 * @see org.groupfio.etl.process.processor.AbstractProcessor#doProcess()
	 */
	@Override
	protected Map<String, Object> doProcess(Map<String, Object> context) throws Exception {
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		
		String modelName = (String) context.get("modelName");
		String elementName = (String) context.get("elementName");
		
		String cellValue = (String) context.get("cellValue");
		
		try {
			
			List<GenericValue> modelElementDefaults = delegator.findByAnd("EtlModelElementDefault", UtilMisc.toMap("modelName", modelName, "elementName", elementName), null, false);
			Debug.logInfo("modelElementDefaults>> "+modelElementDefaults, MODULE);
			if (UtilValidate.isNotEmpty(modelElementDefaults)) {
				
				int stringStart = 0;
				int stringEnd = 0;
				
				Map<String, Object> defaults = new HashMap<String, Object>();
				
				for (GenericValue elementDefault : modelElementDefaults) {
					String propertyName = elementDefault.getString("propertyName");
					String propertyValue = elementDefault.getString("propertyValue");
					
					defaults.put(propertyName, propertyValue);
				}
				
				String trim = (String) defaults.get(EtlModelElementFunction.TRIM.getValue());
				String defaultValue = (String) defaults.get(EtlModelElementFunction.DEFAULT_VALUE.getValue());
				String concatPrefix = (String) defaults.get(EtlModelElementFunction.CONCAT_PREFIX.getValue());
				String concatSuffix = (String) defaults.get(EtlModelElementFunction.CONCAT_SUFFIX.getValue());
				String substringStart = (String) defaults.get(EtlModelElementFunction.SUBSTRING_START.getValue());
				String substringEnd = (String) defaults.get(EtlModelElementFunction.SUBSTRING_END.getValue());
				String maxLength = (String) defaults.get(EtlModelElementFunction.MAX_LENGTH.getValue());
				String stringReplace = (String) defaults.get(EtlModelElementFunction.STRING_REPLACE.getValue());
				String numericAdd = (String) defaults.get(EtlModelElementFunction.ADD.getValue());
				String numericSubtract = (String) defaults.get(EtlModelElementFunction.SUBTRACT.getValue());
				String numericMultiply = (String) defaults.get(EtlModelElementFunction.MULTIPLY.getValue());
				String numericDivide = (String) defaults.get(EtlModelElementFunction.DIVIDE.getValue());
				
				if (UtilValidate.isEmpty(cellValue)) {
					cellValue = defaultValue;
				}
				
				if (UtilValidate.isNotEmpty(trim) && trim.equals("YES")) {
					cellValue = cellValue.trim();
				}
				
				if (UtilValidate.isNotEmpty(concatPrefix)) {
					cellValue = concatPrefix.concat(cellValue);
				}
				if (UtilValidate.isNotEmpty(concatSuffix)) {
					cellValue = cellValue.concat(concatSuffix);
				}
				
				if (UtilValidate.isNotEmpty(substringStart)) {
					try {
						BigDecimal value = new BigDecimal(substringStart);
						stringStart = value.intValue();
					} catch (Exception e) {
						Debug.logInfo("Cant convert to number...."+e.getMessage(), MODULE);
					}
				}
				if (UtilValidate.isNotEmpty(substringEnd)) {
					try {
						BigDecimal value = new BigDecimal(substringEnd);
						stringEnd = value.intValue();
					} catch (Exception e) {
						Debug.logInfo("Cant convert to number...."+e.getMessage(), MODULE);
					}
				}
				if (stringStart < stringEnd && stringEnd > 0) {
					cellValue = cellValue.substring(stringStart, stringEnd);
				}
				
				if (UtilValidate.isNotEmpty(maxLength)) {
					try {
						Long length = new Long(maxLength);
						if (cellValue.length() > length.intValue()) {
							cellValue = cellValue.substring(0, length.intValue());
						}
					} catch (Exception e) {
						Debug.logInfo("Cant convert to Long...."+e.getMessage(), MODULE);
					}
				}
				
				if (UtilValidate.isNotEmpty(stringReplace)) {
					try {
						JSON jsonFeed = JSON.from(stringReplace);
						
						JSONToMap jsonMap = new JSONToMap();
						Map<String, Object> dataMap = jsonMap.convert(jsonFeed);
						
						if (UtilValidate.isNotEmpty(dataMap)) {
							for (String key : dataMap.keySet()) {
								cellValue = cellValue.replace(key, dataMap.get(key).toString());
							}
						}
					} catch (Exception e) {
						Debug.logInfo("Cant convert to JSON...."+e.getMessage(), MODULE);
					}
				}
				
				if (UtilValidate.isNotEmpty(numericAdd)) {
					try {
						BigDecimal value = new BigDecimal(numericAdd);
						BigDecimal cellVal = new BigDecimal(cellValue);
						
						cellVal = cellVal.add(value);
						
						cellValue = cellVal.toString();
						
					} catch (Exception e) {
						Debug.logInfo("Cant convert to number...."+e.getMessage(), MODULE);
					}
				}
				if (UtilValidate.isNotEmpty(numericSubtract)) {
					try {
						BigDecimal value = new BigDecimal(numericSubtract);
						BigDecimal cellVal = new BigDecimal(cellValue);
						
						cellVal = cellVal.subtract(value);
						
						cellValue = cellVal.toString();
						
					} catch (Exception e) {
						Debug.logInfo("Cant convert to number...."+e.getMessage(), MODULE);
					}
				}
				if (UtilValidate.isNotEmpty(numericMultiply)) {
					try {
						BigDecimal value = new BigDecimal(numericMultiply);
						BigDecimal cellVal = new BigDecimal(cellValue);
						
						cellVal = cellVal.multiply(value);
						
						cellValue = cellVal.toString();
						
					} catch (Exception e) {
						Debug.logInfo("Cant convert to number...."+e.getMessage(), MODULE);
					}
				}
				if (UtilValidate.isNotEmpty(numericDivide)) {
					try {
						BigDecimal value = new BigDecimal(numericDivide);
						BigDecimal cellVal = new BigDecimal(cellValue);
						
						cellVal = cellVal.divide(value);
						
						cellValue = cellVal.toString();
						
					} catch (Exception e) {
						Debug.logInfo("Cant convert to number...."+e.getMessage(), MODULE);
					}
				}
				
			}
			Debug.logInfo("cellValue>> "+cellValue, MODULE);
			response.put("cellValue", cellValue);
			
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log("Exception in doProcess==="+e.getMessage());
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed...!");
			
			return response;
		}
		
		response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;
		
	}

}
