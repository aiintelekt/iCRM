/**
 * 
 */
package org.groupfio.etl.process.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.util.ValidatorUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
/**
 * @author Sharif
 *
 */
public class ContactDataValidator implements Validator {

	private static String MODULE = ContactDataValidator.class.getName();
	
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
			
			if (UtilValidate.isEmpty(data.get("contactId"))) {
				validate = false;
				message = "contactId is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("contactId", message);
			}
			
			if (UtilValidate.isEmpty(data.get("firstName"))) {
				validate = false;
				message = "firstName is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("firstName", message);
			}
			if (UtilValidate.isEmpty(data.get("lastName"))) {
				validate = false;
				message = "lastName is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("lastName", message);
			}
			
			if (UtilValidate.isEmpty(data.get("countryGeoId"))) {
				validate = false;
				message = "Country Id is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("countryGeoId", message);
			} else if(!ValidatorUtil.isValidGeo(delegator, (String) data.get("countryGeoId").toString(), "COUNTRY")) {
				validate = false;
				message = "Invalid Country Id" + " [Row No:" + rowNumber + "]";
				validationMessage.put("countryGeoId", message);
			} else {
				GenericValue geo = ValidatorUtil.getValidGeo(delegator, (String)data.get("countryGeoId").toString(), "COUNTRY");
				String contgeoid = geo.getString("geoId");
				data.put("countryGeoId", contgeoid);
				if (UtilValidate.isNotEmpty((String) data.get("stateProvinceGeoId")) || contgeoid.equals("SGP")) {
					if (contgeoid.equals("SGP")) {
						data.put("stateProvinceGeoId", "_NA_");
					} else if(!ValidatorUtil.isValidGeo(delegator, (String) data.get("stateProvinceGeoId").toString(), "STATE,PROVINCE")) {
						validate = false;
						message = "Invalid State Id" + " [Row No:" + rowNumber + "]";
						validationMessage.put("stateProvinceGeoId", message);
					} else {
						GenericValue validGeo = ValidatorUtil.getValidGeo(delegator, (String)data.get("stateProvinceGeoId").toString(), "STATE,PROVINCE");
						data.put("stateProvinceGeoId", validGeo.getString("geoId"));
					}	
				}/* else {
					validate = false;
					message = "State Id is empty"+ " [Row No:" + rowNumber + "]";
					validationMessage.put("stateProvinceGeoId", message);
				}*/
			}
			
			if (UtilValidate.isNotEmpty(data.get("personalTitle"))) { 
				if( !EnumUtil.isValidEnum(delegator, (String)data.get("personalTitle"), "SALUTATION")) {
					validate = false;
					message = "Invalid personalTitle" + " [Row No:" + rowNumber + "]";
					validationMessage.put("personalTitle", message);
				} else {
					data.put("personalTitle", EnumUtil.getEnum(delegator, (String)data.get("personalTitle"), "SALUTATION").getString("enumId"));
				}
			}
			
			if (UtilValidate.isNotEmpty(data.get("gender"))) { 
				if( !EnumUtil.isValidEnum(delegator, (String)data.get("gender"), "GENDER")) {
					validate = false;
					message = "Invalid gender" + " [Row No:" + rowNumber + "]";
					validationMessage.put("gender", message);
				} else {
					data.put("gender", EnumUtil.getEnum(delegator, (String)data.get("gender"), "GENDER").getString("enumId"));
				}
			}
			
			if (UtilValidate.isNotEmpty(data.get("maritalStatus"))) { 
				if( !EnumUtil.isValidEnum(delegator, (String)data.get("maritalStatus"), "MARITAL_STATUS")) {
					validate = false;
					message = "Invalid maritalStatus" + " [Row No:" + rowNumber + "]";
					validationMessage.put("maritalStatus", message);
				} else {
					data.put("maritalStatus", EnumUtil.getEnum(delegator, (String)data.get("maritalStatus"), "MARITAL_STATUS").getString("enumId"));
				}
			}
			
			if (UtilValidate.isNotEmpty(data.get("timeZoneId"))) { 
				if (!EnumUtil.isValidEnum(delegator, (String)data.get("timeZoneId"), "TIME_ZONE")) {
					validate = false;
					message = "Invalid timeZone" + " [Row No:" + rowNumber + "]";
					validationMessage.put("timeZoneId", message);
				} else {
					data.put("timeZoneId", EnumUtil.getEnum(delegator, (String)data.get("timeZoneId"), "TIME_ZONE").getString("enumId"));
				}
			} 
			
			if (UtilValidate.isNotEmpty(data.get("designation"))) { 
				if (!EnumUtil.isValidEnum(delegator, (String)data.get("designation"), "DESIGNATION")) {
					validate = false;
					message = "Invalid designation" + " [Row No:" + rowNumber + "]";
					validationMessage.put("designation", message);
				} else {
					data.put("designation", EnumUtil.getEnum(delegator, (String)data.get("designation"), "DESIGNATION").getString("enumId"));
				}
			} 
			
			if (UtilValidate.isNotEmpty(data.get("emailAddress")) && !ValidatorUtil.validateEmail(data.get("emailAddress").toString())) {
				validate = false;
				message = "Invalid emailAddress" + " [Row No:" + rowNumber + "]";
				validationMessage.put("emailAddress", message);
			}
			
			if (UtilValidate.isNotEmpty(data.get("segmentation"))) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, data.get("segmentation")),
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("customFieldName")), EntityOperator.EQUALS, data.get("segmentation").toString().toUpperCase())
						));
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue customField = EntityQuery.use(delegator).from("CustomField").where(mainConditon).queryFirst();
				if (UtilValidate.isEmpty(customField)) {
					validate = false;
					message = "Invalid segmentation" + " [Row No:" + rowNumber + "]";
					validationMessage.put("segmentation", message);
				}else {
					data.put("segmentation", customField.getString("customFieldId"));
				}
			}
			
			if (!validate) {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(EtlConstants.RESPONSE_MESSAGE, "Contact Data Validation Failed...!");
			} else {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Contact Data Validation Failed...!");
			return response;
		}
		response.put("data", data);
		response.put("validationMessage", validationMessage);
		return response;
	}

}
