/**
 * 
 */
package org.groupfio.etl.process.validator;

import java.util.HashMap;
import java.util.Map;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.EtlConstants.LockboxStagingImportStatus;
import org.groupfio.etl.process.ResponseCodes;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Group Fio
 *
 */
public class LockboxBatchItemDataValidator implements Validator {

	private static String MODULE = LockboxBatchItemDataValidator.class.getName();
	
	private boolean validate;
	private String errorCodes;
	
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
			errorCodes = "";
			
			Delegator delegator = (Delegator) context.get("delegator");
			String modelName = ParamUtil.getString(context, "modelName");
			
			Integer rowNumber = ParamUtil.getInteger(context, "rowNumber");
			String taskName = ParamUtil.getString(context, "taskName");
			String tableName = ParamUtil.getString(context, "tableName");
			
			String message = null;
			GenericValue lockboxBatch = null;
			
			if (UtilValidate.isEmpty(data.get("batchNumber"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockboxitem.batchnumber.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("batchNumber", message);
			} else {
				lockboxBatch = EntityUtil.getFirst( delegator.findByAnd("FioLockboxBatchStaging",UtilMisc.toMap("batchNumber", data.get("batchNumber")), null, false) );
				if (UtilValidate.isEmpty(lockboxBatch)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockboxitem.batchNumber.invalid.error") + " [Row No:" + rowNumber + "]";
					validationMessage.put("batchNumber", message);
				}
			}
			if (UtilValidate.isEmpty(data.get("batchItemSeqId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockboxitem.batchItemSeqId.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("batchItemSeqId", message);
			}
			if (UtilValidate.isEmpty(data.get("detailItemSeqId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockboxitem.detailItemSeqId.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("detailItemSeqId", message);
			}
			
			if (UtilValidate.isEmpty(data.get("customerBankAccountNumber"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockboxitem.customerBankAccountNumber.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("customerBankAccountNumber", message);
				errorCodes += "LB-CBAN, ";
			}
			
			if (UtilValidate.isEmpty(data.get("routingNumber"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockboxitem.routingNumber.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("routingNumber", message);
				errorCodes += "LB-RN, ";
			}
			
			if (UtilValidate.isEmpty(data.get("chequeNumber"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockboxitem.chequeNumber.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("chequeNumber", message);
				errorCodes += "LB-CHQN, ";
			}
			
			if (UtilValidate.isEmpty(data.get("invoiceNumber"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockboxitem.invoiceNumber.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("invoiceNumber", message);
				errorCodes += "LB-IN, ";
			} else {
				GenericValue invoice = EntityQuery.use(delegator).from("Invoice")
						.where("invoiceId", (String) data.get("invoiceNumber"))
						.cache().queryOne();
	        	if(UtilValidate.isEmpty(invoice)) {
	        		setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockboxitem.invoiceNumber.invalid.error") + " [Row No:" + rowNumber + "]";
					validationMessage.put("invoiceNumber", message);
					errorCodes += "LB-IN-NF, ";
	        	} else {
	        		/*BigDecimal invoiceTotal = InvoiceWorker.getInvoiceTotal(invoice);
					if (UtilValidate.isNotEmpty(data.get("invoiceAmount"))) {
						if (invoiceTotal.doubleValue() != new BigDecimal(data.get("invoiceAmount").toString()).doubleValue()) {
							message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockboxitem.invoiceAmount.not.matched") + " [Row No:" + rowNumber + "]";
							validationMessage.put("invoiceNumber", message);
							errorCodes += "LB-INA-NM, ";
						}
					}*/
	        	}
			}
			
			if (UtilValidate.isEmpty(data.get("invoiceAmount"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockboxitem.invoiceAmount.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("invoiceAmount", message);
				errorCodes += "LB-INA, ";
			}
			
			if (UtilValidate.isEmpty(data.get("chequeAmount"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "lockboxitem.chequeAmount.empty.error") + " [Row No:" + rowNumber + "]";
				validationMessage.put("chequeAmount", message);
				errorCodes += "LB-CHQA, ";
			}
			
			if (!isValidate()) {
				
				if (errorCodes.length() > 0) {
					errorCodes = errorCodes.substring(0, errorCodes.length() - 2);
					data.put("errorCodes", errorCodes);
				}
				
				data.put("importStatusId", LockboxStagingImportStatus.LBBATCH_ERROR);
				
				if (UtilValidate.isNotEmpty(lockboxBatch)) {
					lockboxBatch.put("importStatusId", LockboxStagingImportStatus.LBBATCH_ERROR);
					delegator.store(lockboxBatch);
				}
				
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(EtlConstants.RESPONSE_MESSAGE, "LockboxBatchItem Data Validation Failed...!");
				
			} else {
				data.put("importStatusId", LockboxStagingImportStatus.LBBATCH_READY);
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
			
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "LockboxBatchItem Data Validation Failed...!");
			
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

	public String getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(String errorCodes) {
		this.errorCodes = errorCodes;
	}
	
}
