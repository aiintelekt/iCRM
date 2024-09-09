/**
 * 
 */
package org.groupfio.etl.process.validator;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.util.ValidatorUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif 
 *
 */
public class ProductSupplementaryValidator implements Validator {

	private static String MODULE = ProductSupplementaryValidator.class.getName();
	
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
			
			if (UtilValidate.isEmpty(data.get("productId"))) {
				validate = false;
				message = "productId is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("productId", message);
			}
			
			if (UtilValidate.isEmpty(data.get("dataType"))) {
				validate = false;
				message = "dataType is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("dataType", message);	
			}else if(!EnumUtil.isValidEnum(delegator, (String) data.get("dataType"), "DATA_TYPE")) {
				validate = false;
				message = "Invalid dataType" + " [Row No:" + rowNumber + "]";
				validationMessage.put("dataType", message);	
			}
			
			if (UtilValidate.isEmpty(data.get("productCategoryIdOne"))) {
				validate = false;
				message = "productCategoryIdOne is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("productCategoryIdOne", message);	
			}else if(UtilValidate.isNotEmpty(data.get("productCategoryIdOne"))) {
				GenericValue productCategoryData = EntityQuery.use(delegator).from("ProductCategory")
						.where("productCategoryId", (String) data.get("productCategoryIdOne")).queryFirst();
				GenericValue productCategoryLookupData = EntityQuery.use(delegator).from("ProductCategoryLookup").where("level1CategoryId",(String)data.get("productCategoryIdOne")).queryFirst();
				
				if(UtilValidate.isEmpty(productCategoryData) || UtilValidate.isEmpty(productCategoryLookupData)) {
					validate = false;
					message = "Invalid Product Category Id One" + " [Row No:" + rowNumber + "]";
					validationMessage.put("productCategoryIdOne", message);
				}
			}
			
			if (UtilValidate.isEmpty(data.get("productCity"))) {
				validate = false;
				message = "Product City is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("productCity", message);	
			}
			
			if (UtilValidate.isEmpty(data.get("productCountry"))) {
				validate = false;
				message = "Product Country is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("productCountry", message);	
			} else if(!ValidatorUtil.isValidGeo(delegator, (String) data.get("productCountry").toString(), "COUNTRY")) {
				validate = false;
				message = "Invalid Product Country Id" + " [Row No:" + rowNumber + "]";
				validationMessage.put("productCountry", message);
			} else {
				GenericValue geo = ValidatorUtil.getValidGeo(delegator, (String)data.get("productCountry").toString(), "COUNTRY");
				String contgeoid = geo.getString("geoId");
				data.put("productCountry", contgeoid);
				if (UtilValidate.isNotEmpty((String) data.get("productState")) || contgeoid.equals("SGP")) {
					if (contgeoid.equals("SGP")) {
						data.put("productState", "_NA_");
					} else if(!ValidatorUtil.isValidGeo(delegator, (String) data.get("productState").toString(), "STATE,PROVINCE")) {
						validate = false;
						message = "Invalid State Id" + " [Row No:" + rowNumber + "]";
						validationMessage.put("productState", message);
					} else {
						GenericValue validGeo = ValidatorUtil.getValidGeo(delegator, (String)data.get("productState").toString(), "STATE,PROVINCE");
						data.put("productState", validGeo.getString("geoId"));
					}
				}/* else {
					validate = false;
					message = "Product State Id is empty"+ " [Row No:" + rowNumber + "]";
					validationMessage.put("productState", message);
				}*/
			}
			
			if (UtilValidate.isNotEmpty((String) data.get("offerDate"))) {
				try {
				Timestamp offerDate = Timestamp.valueOf((String) data.get("offerDate"));
				}catch(Exception e) {
					Debug.log(e.getMessage(), MODULE);
					validate = false;
					message = "Invalid Offer Date Format (Format should be yyyy-mm-dd hh:mm:ss)"+ " [Row No:" + rowNumber + "]";
					validationMessage.put("offerDate", message);
				}
			}
			
			if (UtilValidate.isNotEmpty((String) data.get("expirationDate"))) {
				try {
				Timestamp expirationDate = Timestamp.valueOf((String) data.get("expirationDate"));
				}catch(Exception e) {
					Debug.log(e.getMessage(), MODULE);
					validate = false;
					message = "Invalid Expiration Date Format (Format should be yyyy-mm-dd hh:mm:ss)"+ " [Row No:" + rowNumber + "]";
					validationMessage.put("expirationDate", message);
				}
			}

			if (!validate) {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(EtlConstants.RESPONSE_MESSAGE, "Invoice Data Validation Failed...!");
			} else {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Invoice Data Validation Failed...!");
			return response;
		}
		
		response.put("data", data);
		response.put("validationMessage", validationMessage);
		
		return response;
	}
	
}
