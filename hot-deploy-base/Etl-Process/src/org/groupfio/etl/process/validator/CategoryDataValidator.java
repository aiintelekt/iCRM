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
public class CategoryDataValidator implements Validator {

	private static String MODULE = CategoryDataValidator.class.getName();
	
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
			
			if (UtilValidate.isEmpty(data.get("categoryId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCategoryIdError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("categoryId", message);
			}
			
			if (UtilValidate.isEmpty(data.get("productCategoryTypeId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductCategoryIdError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("productCategoryTypeId", message);
			} else {
				GenericValue productCategoryType = EntityQuery.use(delegator).from("ProductCategoryType")
						.where("productCategoryTypeId", (String) data.get("productCategoryTypeId")).cache()
						.queryOne();
				if (UtilValidate.isEmpty(productCategoryType)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductCategoryIdError1") + " [Row No:" + rowNumber + "]";
					validationMessage.put("productCategoryTypeId", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("primaryParentCategoryId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "primaryParentCategoryIdEmpty") + " [Row No:" + rowNumber + "]";
				validationMessage.put("primaryParentCategoryId", message);
			} else {
				GenericValue productCategory = EntityQuery.use(delegator).from("ProductCategory")
						.where("productCategoryId", (String) data.get("primaryParentCategoryId")).cache().queryOne();
				if (UtilValidate.isEmpty(productCategory)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "primaryParentCategoryIdInvalid") + " [Row No:" + rowNumber + "]";
					validationMessage.put("primaryParentCategoryId", message);
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
			response.put(EtlConstants.RESPONSE_MESSAGE, "Category Data Validation Failed...!");
			
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
