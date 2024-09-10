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
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif 
 *
 */
public class ItmValidator implements Validator {

	private static String MODULE = ItmValidator.class.getName();
	
	/* (non-Javadoc)
	 * @see org.groupfio.etl.process.validator.Validator#validate(java.util.Map)
	 */
	@Override
	public Map<String, Object> validate(Map<String, Object> context) {

		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> data = (Map<String, Object>) context.get("data");
		Map<String, Object> validationMessage = new HashMap<String, Object>();
		
		try {
			
			boolean validate = true;
			
			Delegator delegator = (Delegator) context.get("delegator");
			String modelName = ParamUtil.getString(context, "modelName");
			
			Integer rowNumber = ParamUtil.getInteger(context, "rowNumber");
			String taskName = ParamUtil.getString(context, "taskName");
			String tableName = ParamUtil.getString(context, "tableName");
			
			String message = null;
			
			if (UtilValidate.isEmpty(data.get("invoiceId"))) {
				validate = false;
				message = "invoiceId is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("invoiceId", message);
			}
			if (UtilValidate.isEmpty(data.get("invoiceSequenceNumber"))) {
				validate = false;
				message = "invoiceSequenceNumber is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("invoiceSequenceNumber", message);
			}
			
			/*if (UtilValidate.isEmpty(data.get("dataType"))) {
				validate = false;
				message = "dataType is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("dataType", message);	
			} else if(!EnumUtil.isValidEnum(delegator, (String) data.get("dataType"), "DATA_TYPE")) {
				validate = false;
				message = "Invalid dataType" + " [Row No:" + rowNumber + "]";
				validationMessage.put("dataType", message);	
			}*/
			
			if(UtilValidate.isNotEmpty(data.get("productCategoryIdOne"))) {
				GenericValue category = EntityQuery.use(delegator).from("ProductCategory")
						.where("productCategoryId", (String) data.get("productCategoryId1")).queryFirst();
				GenericValue categoryLookup = EntityQuery.use(delegator).from("ProductCategoryLookup").where("level1CategoryId",(String)data.get("productCategoryId1")).queryFirst();
				if(UtilValidate.isEmpty(category) || UtilValidate.isEmpty(categoryLookup)) {
					validate = false;
					message = "Invalid productCategoryId1" + " [Row No:" + rowNumber + "]";
					validationMessage.put("productCategoryId1", message);
				} else if (UtilValidate.isNotEmpty(category)) {
					data.put("productCategoryName1", category.getString("categoryName"));
				} else if (UtilValidate.isNotEmpty(categoryLookup)) {
					category = EntityQuery.use(delegator).from("ProductCategory").select("categoryName")
							.where("productCategoryId", (String) categoryLookup.get("level1CategoryId")).queryFirst();
					if (UtilValidate.isNotEmpty(category)) {
						data.put("productCategoryName1", category.getString("categoryName"));
					}
				}
			}
			if(UtilValidate.isNotEmpty(data.get("productCategoryId2"))) {
				GenericValue category = EntityQuery.use(delegator).from("ProductCategory")
						.where("productCategoryId", (String) data.get("productCategoryId2")).queryFirst();
				GenericValue categoryLookup = EntityQuery.use(delegator).from("ProductCategoryLookup").where("level2CategoryId",(String)data.get("productCategoryId2")).queryFirst();
				if(UtilValidate.isEmpty(category) || UtilValidate.isEmpty(categoryLookup)) {
					validate = false;
					message = "Invalid productCategoryId2" + " [Row No:" + rowNumber + "]";
					validationMessage.put("productCategoryId2", message);
				} else if (UtilValidate.isNotEmpty(category)) {
					data.put("productCategoryName2", category.getString("categoryName"));
				} else if (UtilValidate.isNotEmpty(categoryLookup)) {
					category = EntityQuery.use(delegator).from("ProductCategory").select("categoryName")
							.where("productCategoryId", (String) categoryLookup.get("level2CategoryId")).queryFirst();
					if (UtilValidate.isNotEmpty(category)) {
						data.put("productCategoryName2", category.getString("categoryName"));
					}
				}
			}
			if(UtilValidate.isNotEmpty(data.get("productCategoryId3"))) {
				GenericValue category = EntityQuery.use(delegator).from("ProductCategory")
						.where("productCategoryId", (String) data.get("productCategoryId3")).queryFirst();
				GenericValue categoryLookup = EntityQuery.use(delegator).from("ProductCategoryLookup").where("level3CategoryId",(String)data.get("productCategoryId3")).queryFirst();
				if(UtilValidate.isEmpty(category) || UtilValidate.isEmpty(categoryLookup)) {
					validate = false;
					message = "Invalid productCategoryId3" + " [Row No:" + rowNumber + "]";
					validationMessage.put("productCategoryId3", message);
				} else if (UtilValidate.isNotEmpty(category)) {
					data.put("productCategoryName3", category.getString("categoryName"));
				} else if (UtilValidate.isNotEmpty(categoryLookup)) {
					category = EntityQuery.use(delegator).from("ProductCategory").select("categoryName")
							.where("productCategoryId", (String) categoryLookup.get("level3CategoryId")).queryFirst();
					if (UtilValidate.isNotEmpty(category)) {
						data.put("productCategoryName3", category.getString("categoryName"));
					}
				}
			}
			
			if(UtilValidate.isNotEmpty(data.get("shipmentCountry"))) {
				GenericValue productCountry = EntityQuery.use(delegator).from("Geo")
						.where("geoId", (String)data.get("shipmentCountry"), "geoTypeId", "COUNTRY").queryFirst();
				if (UtilValidate.isEmpty(productCountry)) {
					validate = false;
					message = "Invalid shipmentCountry" + " [Row No:" + rowNumber + "]";
					validationMessage.put("shipmentCountry", message);
				}
			}
			if(UtilValidate.isNotEmpty(data.get("billCountry"))) {
				GenericValue productCountry = EntityQuery.use(delegator).from("Geo")
						.where("geoId", (String)data.get("billCountry"), "geoTypeId", "COUNTRY").queryFirst();
				if (UtilValidate.isEmpty(productCountry)) {
					validate = false;
					message = "Invalid billCountry" + " [Row No:" + rowNumber + "]";
					validationMessage.put("billCountry", message);
				}
			}

			if (!validate) {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(EtlConstants.RESPONSE_MESSAGE, "ITM Data Validation Failed...!");
			} else {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "ITM Data Validation Failed...!");
			return response;
		}
		
		response.put("data", data);
		response.put("validationMessage", validationMessage);
		
		return response;
	}
	
}
