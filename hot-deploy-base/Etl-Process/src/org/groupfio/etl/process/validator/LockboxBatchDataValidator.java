/**
 * 
 */
package org.groupfio.etl.process.validator;

import java.util.HashMap;
import java.util.Map;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Group Fio
 *
 */
public class LockboxBatchDataValidator implements Validator {

	private static String MODULE = LockboxBatchDataValidator.class.getName();
	
	private boolean validate;
	
	/* (non-Javadoc)
	 * @see org.groupfio.etl.process.validator.Validator#validate(java.util.Map)
	 */
	@Override
	public Map<String, Object> validate(Map<String, Object> context) {

		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> data = (Map<String, Object>) context.get("data");
		Map<String, Object> validationMessage = new HashMap<String, Object>();
		
		try {
			
			setValidate(true);
			
			Delegator delegator = (Delegator) context.get("delegator");
			String modelName = ParamUtil.getString(context, "modelName");
			
			Integer rowNumber = ParamUtil.getInteger(context, "rowNumber");
			String taskName = ParamUtil.getString(context, "taskName");
			String tableName = ParamUtil.getString(context, "tableName");
			
			String message = null;
			
			if (UtilValidate.isEmpty(data.get("batchNumber"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockbox.batchnumber.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("batchNumber", message);
			}
			
			if (UtilValidate.isEmpty(data.get("finInstitute"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockbox.finInstitute.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("finInstitute", message);
			}
			
			if (UtilValidate.isEmpty(data.get("finBatchNumber"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockbox.finBatchNumber.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("finBatchNumber", message);
			}
			
			if (UtilValidate.isEmpty(data.get("storeId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockbox.storeId.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("storeId", message);
			} else {
				
				GenericValue productStore = EntityQuery.use(delegator).from("ProductStore")
						.where("productStoreId", (String) data.get("storeId")).cache().queryOne();
				if (UtilValidate.isEmpty(productStore)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError2") + " [Row No:" + rowNumber + "]";
					validationMessage.put("storeId", message);
				}
				
			}
			
			if (UtilValidate.isEmpty(data.get("totalDepositAmount"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockbox.totalDepositAmount.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("totalDepositAmount", message);
			}
			
			if (UtilValidate.isEmpty(data.get("noOfCheques"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockbox.noOfCheques.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("noOfCheques", message);
			}
			
			if (!isValidate()) {
				
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(EtlConstants.RESPONSE_MESSAGE, "LockboxBatch Data Validation Failed...!");
				
			} else {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "LockboxBatch Data Validation Failed...!");
			
			return response;
		}
		
		response.put("data", data);
		response.put("validationMessage", validationMessage);
		
		return response;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}
	
}
