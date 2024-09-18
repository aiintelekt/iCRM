/**
 * 
 */
package org.groupfio.etl.process.validator;

import java.util.HashMap;
import java.util.Map;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.util.ValidatorUtil;
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
public class SupplierDataValidator implements Validator {

	private static String MODULE = SupplierDataValidator.class.getName();
	
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
			
			if (UtilValidate.isEmpty(data.get("supplierId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicesupplierIdError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("supplierId", message);
			}
			
			if (UtilValidate.isEmpty(data.get("supplierName"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlSupplierNameEmpty") + " [Row No:" + rowNumber + "]";
				validationMessage.put("supplierName", message);
			}
			
			if (UtilValidate.isEmpty(data.get("countryGeoId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCountryIdEmptyError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("countryGeoId", message);
			} else {
				String contgeoid = (String) data.get("countryGeoId");
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
				} else {
					
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdEmptyError");
					validationMessage.put("stateProvinceGeoId", message);
					
				}
			}
			
			if (UtilValidate.isEmpty(data.get("source"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceinvalidPartyIdentificationTypeIdError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("source", message);
			} else {
				GenericValue partyIdentificationType = EntityQuery.use(delegator).from("PartyIdentificationType")
						.where("partyIdentificationTypeId", (String) data.get("source")).cache().queryOne();
				if (UtilValidate.isEmpty(partyIdentificationType)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicepartyIdentificationTypeIdEmptyError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("source", message);
				}
				
			}
			
			if (!isValidate()) {
				
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(EtlConstants.RESPONSE_MESSAGE, "Supplier Data Validation Failed...!");
				
			} else {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Supplier Data Validation Failed...!");
			
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
