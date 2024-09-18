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
public class ProductDataValidator implements Validator {

	private static String MODULE = ProductDataValidator.class.getName();
	
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
			
			if (UtilValidate.isEmpty(data.get("productId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductIdError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("productId", message);
			} else if (data.get("productId").toString().length() > 20) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductIdLengthError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("productId", message);
			}
			
			if (UtilValidate.isEmpty(data.get("productTypeId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductTypeIdError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("productTypeId", message);
			} else {
				GenericValue productType = EntityQuery.use(delegator).from("ProductType")
						.where("productTypeId", (String) data.get("productTypeId")).cache().queryOne();
				if (UtilValidate.isEmpty(productType)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductTypeIdError1") + " [Row No:" + rowNumber + "]";
					validationMessage.put("productTypeId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("weightUomId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceweightUomIdEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("weightUomId", message);
			} else {
				GenericValue uom = EntityQuery.use(delegator).from("Uom")
						.where("uomId", (String) data.get("weightUomId")).cache().queryOne();
				if (UtilValidate.isEmpty(uom)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceweightUomIdError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("weightUomId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("productLengthUomId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceproductLengthUomIdEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("productLengthUomId", message);
			} else {
				GenericValue uom = EntityQuery.use(delegator).from("Uom")
						.where("uomId", (String) data.get("productLengthUomId")).cache().queryOne();
				if (UtilValidate.isEmpty(uom)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceproductLengthUomIdError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("productLengthUomId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("widthUomId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicewidthUomIdEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("widthUomId", message);
			} else {
				GenericValue uom = EntityQuery.use(delegator).from("Uom")
						.where("uomId", (String) data.get("widthUomId")).cache().queryOne();
				if (UtilValidate.isEmpty(uom)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicewidthUomIdError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("widthUomId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("heightUomId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceheightUomIdEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("heightUomId", message);
			} else {
				GenericValue uom = EntityQuery.use(delegator).from("Uom")
						.where("uomId", (String) data.get("heightUomId")).cache().queryOne();
				if (UtilValidate.isEmpty(uom)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceheightUomIdError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("heightUomId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("priceCurrencyUomId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicepriceCurrencyUomIdEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("priceCurrencyUomId", message);
			} else {
				GenericValue uom = EntityQuery.use(delegator).from("Uom")
						.where("uomId", (String) data.get("priceCurrencyUomId")).cache().queryOne();
				if (UtilValidate.isEmpty(uom)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicepriceCurrencyUomIdError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("priceCurrencyUomId", message);
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
			response.put(EtlConstants.RESPONSE_MESSAGE, "Product Data Validation Failed...!");
			
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
