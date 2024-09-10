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
public class InvoiceHeaderDataValidator implements Validator {

	private static String MODULE = InvoiceHeaderDataValidator.class.getName();
	
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
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceIdError1") + " [Row No:" + rowNumber + "]";
					validationMessage.put("invoiceId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("invoiceTypeId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceTypeIdEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("invoiceTypeId", message);
			} else {
				GenericValue invoiceType = EntityQuery.use(delegator).from("InvoiceType")
						.where("invoiceTypeId", (String) data.get("invoiceTypeId")).cache().queryOne();
				if (UtilValidate.isEmpty(invoiceType)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceTypeIdError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("invoiceTypeId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("currencyUomId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCurrencyEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("currencyUomId", message);
			} else {
				GenericValue uom = EntityQuery.use(delegator).from("Uom")
						.where("uomId", (String) data.get("currencyUomId")).cache().queryOne();
				if (UtilValidate.isEmpty(uom)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCurrencyError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("currencyUomId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("partyIdFrom"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicePartyIdFromEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("partyIdFrom", message);
			} else {
				GenericValue party = EntityQuery.use(delegator).from("Party")
						.where("partyId", (String) data.get("partyIdFrom")).cache().queryOne();
				if (UtilValidate.isEmpty(party)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicePartyIdError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("partyIdFrom", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("partyId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicePartyIdEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("partyId", message);
			} else {
				GenericValue party = EntityQuery.use(delegator).from("Party")
						.where("partyId", (String) data.get("partyId")).cache().queryOne();
				if (UtilValidate.isEmpty(party)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicePartyIdError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("partyId", message);
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
			response.put(EtlConstants.RESPONSE_MESSAGE, "Invoice Header Data Validation Failed...!");
			
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
