/**
 * 
 */
package org.groupfio.etl.process.processor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

/**
 * @author Group Fio
 *
 */
public class ModelFilterProcessor extends AbstractProcessor {

	private static String MODULE = ModelFilterProcessor.class.getName();

	/*
	 * (non-Javadoc)
	 * 
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

			boolean filterRes = true;
			
			if (UtilValidate.isNotEmpty(rowValue)) {

				List<GenericValue> modelElementFilterList = delegator.findByAnd("EtlModelFilter",
						UtilMisc.toMap("modelName", modelName), null, false);
				if (UtilValidate.isNotEmpty(modelElementFilterList)) {
					
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
								try {
									BigDecimal fv = new BigDecimal(fieldValue);
									BigDecimal v = new BigDecimal(value);
									curRes = fv.doubleValue() > v.doubleValue() ? true : false;
								} catch (Exception e) {
									Debug.logError("Filter process GATHER_THAN failed>" + e.getMessage(), MODULE);
								}
								break;
							case "LESS_THAN":
								try {
									BigDecimal fv = new BigDecimal(fieldValue);
									BigDecimal v = new BigDecimal(value);
									curRes = fv.doubleValue() < v.doubleValue() ? true : false;
								} catch (Exception e) {
									Debug.logError("Filter process LESS_THAN failed>" + e.getMessage(), MODULE);
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

				}

			}

			response.put("filterRes", filterRes);

			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			Debug.log("Exception in doProcess==="+e.getMessage());
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Model Filter process Failed...! modelName: " + modelName);

			return response;
		}

		response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		return response;

	}

}
