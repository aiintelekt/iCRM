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
public class OrderDataValidator implements Validator {

	private static String MODULE = OrderDataValidator.class.getName();
	
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
			
			if (UtilValidate.isEmpty(data.get("orderId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("orderId", message);
			}
			
			if (UtilValidate.isEmpty(data.get("orderItemCode"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError17") + " [Row No:" + rowNumber + "]";
				validationMessage.put("orderItemCode", message);
			}
			
			if (UtilValidate.isEmpty(data.get("customerPartyId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError11") + " [Row No:" + rowNumber + "]";
				validationMessage.put("customerPartyId", message);
			} else {
				GenericValue party = EntityQuery.use(delegator).from("Party")
						.where("partyId", (String) data.get("customerPartyId")).cache().queryOne();
				if (UtilValidate.isEmpty(party)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError12") + " [Row No:" + rowNumber + "]";
					validationMessage.put("customerPartyId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("supplierPartyId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError13") + " [Row No:" + rowNumber + "]";
				validationMessage.put("supplierPartyId", message);
			} else {
				GenericValue party = EntityQuery.use(delegator).from("Party")
						.where("partyId", (String) data.get("supplierPartyId")).cache().queryOne();
				if (UtilValidate.isEmpty(party)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError14") + " [Row No:" + rowNumber + "]";
					validationMessage.put("supplierPartyId", message);
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
			
			if (UtilValidate.isEmpty(data.get("grandTotal"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError10") + " [Row No:" + rowNumber + "]";
				validationMessage.put("grandTotal", message);
			}
			
			if (UtilValidate.isEmpty(data.get("productStoreId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError3") + " [Row No:" + rowNumber + "]";
				validationMessage.put("productStoreId", message);
			} else {
				GenericValue productStore = EntityQuery.use(delegator).from("ProductStore")
						.where("productStoreId", (String) data.get("productStoreId")).cache().queryOne();
				if (UtilValidate.isEmpty(productStore)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError2") + " [Row No:" + rowNumber + "]";
					validationMessage.put("productStoreId", message);
				}
			}
			
			if (UtilValidate.isNotEmpty(data.get("salesChannelEnumId"))) {
				GenericValue enumeration = EntityQuery.use(delegator).from("Enumeration")
						.where("enumId", (String) data.get("salesChannelEnumId")).cache().queryOne();
				if (UtilValidate.isEmpty(enumeration)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError6") + " [Row No:" + rowNumber + "]";
					validationMessage.put("salesChannelEnumId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("orderClosed"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError15") + " [Row No:" + rowNumber + "]";
				validationMessage.put("orderClosed", message);
			} else if (data.get("orderClosed").toString().length() != 1) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError16") + " [Row No:" + rowNumber + "]";
				validationMessage.put("orderClosed", message);
			}
			
			if (UtilValidate.isEmpty(data.get("sku"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError7") + " [Row No:" + rowNumber + "]";
				validationMessage.put("sku", message);
			} else {
				GenericValue product = EntityQuery.use(delegator).from("Product")
						.where("productId", (String) data.get("sku")).cache().queryOne();
				if (UtilValidate.isEmpty(product)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError8") + " [Row No:" + rowNumber + "]";
					validationMessage.put("sku", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("quantity"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceQuantityEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("quantity", message);
			}
			
			if (UtilValidate.isEmpty(data.get("itemPrice"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError9") + " [Row No:" + rowNumber + "]";
				validationMessage.put("itemPrice", message);
			}
			
			if (UtilValidate.isEmpty(data.get("paymentMethodType"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError5") + " [Row No:" + rowNumber + "]";
				validationMessage.put("paymentMethodType", message);
			} else {
				GenericValue paymentMethodType = EntityQuery.use(delegator).from("PaymentMethodType")
						.where("paymentMethodTypeId", (String) data.get("paymentMethodType")).cache().queryOne();
				if (UtilValidate.isEmpty(paymentMethodType)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError4") + " [Row No:" + rowNumber + "]";
					validationMessage.put("paymentMethodType", message);
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
			response.put(EtlConstants.RESPONSE_MESSAGE, "Order Data Validation Failed...!");
			
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
