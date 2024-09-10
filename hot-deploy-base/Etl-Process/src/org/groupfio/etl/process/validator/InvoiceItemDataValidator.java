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
public class InvoiceItemDataValidator implements Validator {

	private static String MODULE = InvoiceItemDataValidator.class.getName();
	
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
			
			if (UtilValidate.isEmpty(data.get("invoiceId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceIdEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("invoiceId", message);
			} else {
				GenericValue invoice = EntityQuery.use(delegator).from("Invoice")
						.where("invoiceId", (String) data.get("invoiceId")).cache().queryOne();
				if (UtilValidate.isNotEmpty(invoice)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceIdError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("invoiceId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("invoiceItemSeqId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceItemIdError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("invoiceItemSeqId", message);
			}
			
			if (UtilValidate.isEmpty(data.get("invoiceItemTypeId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceItemTypeIdError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("invoiceItemTypeId", message);
			} else {
				GenericValue invoiceItemType = EntityQuery.use(delegator).from("InvoiceItemType")
						.where("invoiceItemTypeId", (String) data.get("invoiceItemTypeId")).cache().queryOne();
				if (UtilValidate.isEmpty(invoiceItemType)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceinvalidinvoiceItemTypeIdError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("invoiceItemTypeId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("amount"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceamountEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("amount", message);
			}
			
			if (UtilValidate.isEmpty(data.get("quantity"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceQuantityEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("quantity", message);
			}
			
			if (UtilValidate.isEmpty(data.get("productId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductIdEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("productId", message);
			} else {
				GenericValue product = EntityQuery.use(delegator).from("Product")
						.where("productId", (String) data.get("productId")).cache().queryOne();
				if (UtilValidate.isEmpty(product)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvalidProductIdError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("productId", message);
				}
			}
			
			if (!isValidate()) {
				
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(EtlConstants.RESPONSE_MESSAGE, "Customer Data Validation Failed...!");
				
			} else {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Invoice Item Data Validation Failed...!");
			
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
