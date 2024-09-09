/**
 * 
 */
package org.groupfio.etl.process.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.util.ValidatorUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class EmplPositionDataValidator implements Validator {

	private static String MODULE = EmplPositionDataValidator.class.getName();
	
	private boolean validate;
	
	/* (non-Javadoc)
	 * @see org.groupfio.etl.process.validator.Validator#validate(java.util.Map)
	 */
	@Override
	public Map<String, Object> validate(Map<String, Object> context) {

		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> data = (Map<String, Object>) context.get("data");
		Map<String, Object> validationMessage = new HashMap<String, Object>();
		
		List<String> errorCodes = new ArrayList<String>();
		
		try {
			
			setValidate(true);
			
			Delegator delegator = (Delegator) context.get("delegator");
			String modelName = ParamUtil.getString(context, "modelName");
			
			Integer rowNumber = ParamUtil.getInteger(context, "rowNumber");
			String taskName = ParamUtil.getString(context, "taskName");
			String tableName = ParamUtil.getString(context, "tableName");
			
			Locale locale = (Locale) context.get("locale");
			
			String message = null;
			
			/*if (UtilValidate.isEmpty(data.get("leadId"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceleadIdError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("leadId", message);
				errorCodes.add("E1000");
			}*/
			
			if (UtilValidate.isEmpty(data.get("companyId"))) {
				setValidate(false);
				message = "CompanyId is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("companyId", message);
				errorCodes.add("E5000");
			} else {
				List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, data.get("companyId")));
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
				        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
				        EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue companyParty = EntityUtil.getFirst( delegator.findList("PartyToSummaryByRole", mainConditons, null, null, null, false) );
				if(UtilValidate.isEmpty(companyParty)) {
					setValidate(false);
					message = "Invalid companyId value" + " [Row No:" + rowNumber + "]";
					validationMessage.put("companyId", message);
					errorCodes.add("E5001");
				}
			}
			
			if (UtilValidate.isEmpty(data.get("reportingPositionType"))) {
				setValidate(false);
				message = "reportingPositionType is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("reportingPositionType", message);
				errorCodes.add("E5002");
			} else {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("emplPositionTypeId")), EntityOperator.EQUALS, data.get("reportingPositionType").toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, data.get("reportingPositionType").toString().toUpperCase())
               			);                       	
				
               	GenericValue positionType = EntityUtil.getFirst( delegator.findList("EmplPositionType", condition, null, null, null, false) );
				
				if (UtilValidate.isEmpty(positionType)) {
					setValidate(false);
					message = "Invalid reportingPositionType value" + " [Row No:" + rowNumber + "]";
					validationMessage.put("reportingPositionType", message);
					errorCodes.add("E5003");
				} else {
					data.put("reportingPositionType", positionType.getString("emplPositionTypeId"));
				}
			}
			
			if (UtilValidate.isEmpty(data.get("managedByPositionType"))) {
				setValidate(false);
				message = "managedByPositionType is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("managedByPositionType", message);
				errorCodes.add("E5004");
			} else {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("emplPositionTypeId")), EntityOperator.EQUALS, data.get("managedByPositionType").toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, data.get("managedByPositionType").toString().toUpperCase())
               			);                       	
				
               	GenericValue positionType = EntityUtil.getFirst( delegator.findList("EmplPositionType", condition, null, null, null, false) );
				
				if (UtilValidate.isEmpty(positionType)) {
					setValidate(false);
					message = "Invalid managedByPositionType value" + " [Row No:" + rowNumber + "]";
					validationMessage.put("managedByPositionType", message);
					errorCodes.add("E5005");
				} else {
					data.put("managedByPositionType", positionType.getString("emplPositionTypeId"));
				}
			}
			
			if (UtilValidate.isEmpty(data.get("reportingMemberName"))) {
				setValidate(false);
				message = "reportingMemberName is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("reportingMemberName", message);
				errorCodes.add("E5006");
			}
			
			if (UtilValidate.isEmpty(data.get("mangedMemberName"))) {
				setValidate(false);
				message = "mangedMemberName is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("mangedMemberName", message);
				errorCodes.add("E5007");
			}
			
			if (UtilValidate.isEmpty(data.get("reporting1bankid"))) {
				setValidate(false);
				message = "reporting1bankid is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("reporting1bankid", message);
				//errorCodes.add("E1017");
			}
			
			if (UtilValidate.isEmpty(data.get("managed1bankid"))) {
				setValidate(false);
				message = "managed1bankid is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("managed1bankid", message);
				errorCodes.add("E5008");
			}
			
			if (UtilValidate.isEmpty(data.get("reportingEmail"))) {
				setValidate(false);
				message = "reportingEmail is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("reportingEmail", message);
				errorCodes.add("E5009");
			} else if (!ValidatorUtil.validateEmail(data.get("reportingEmail").toString())) {
				setValidate(false);
				message = "Invalid reporting email address" + " [Row No:" + rowNumber + "]";
				validationMessage.put("reportingEmail", message);
				errorCodes.add("E5010");
			}
			
			if (UtilValidate.isEmpty(data.get("mangedEmail"))) {
				setValidate(false);
				message = "mangedEmail is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("mangedEmail", message);
				errorCodes.add("E5011");
			} else if (!ValidatorUtil.validateEmail(data.get("mangedEmail").toString())) {
				setValidate(false);
				message = "Invalid managed email address" + " [Row No:" + rowNumber + "]";
				validationMessage.put("mangedEmail", message);
				errorCodes.add("E5012");
			}
			
			if (UtilValidate.isEmpty(data.get("teamId"))) {
				setValidate(false);
				message = "teamId is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("teamId", message);
				errorCodes.add("E5013");
			} else {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("emplTeamId")), EntityOperator.EQUALS, data.get("teamId").toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("teamName")), EntityOperator.EQUALS, data.get("teamId").toString().toUpperCase())
               			);                       	
				
               	GenericValue team = EntityUtil.getFirst( delegator.findList("EmplTeam", condition, null, null, null, false) );
				
				if (UtilValidate.isEmpty(team)) {
					setValidate(false);
					message = "Invalid teamId value" + " [Row No:" + rowNumber + "]";
					validationMessage.put("teamId", message);
					errorCodes.add("E5014");
				} else {
					data.put("teamId", team.getString("emplTeamId"));
				}
			}
			
			if (UtilValidate.isEmpty(data.get("isAccess"))) {
				setValidate(false);
				message = "isAccess is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("isAccess", message);
				errorCodes.add("E5015");
			} else if (data.get("isAccess").equals("Y") && data.get("isAccess").equals("N")) {
				setValidate(false);
				message = "Invalid isAccess value" + " [Row No:" + rowNumber + "]";
				validationMessage.put("isAccess", message);
				errorCodes.add("E5016");
			}
			
			if (UtilValidate.isEmpty(data.get("countryGeo"))) {
				setValidate(false);
				message = "countryGeo is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("countryGeo", message);
				errorCodes.add("E5017");
			} else if (!ValidatorUtil.isValidGeo(delegator, data.get("countryGeo").toString(), "COUNTRY")) {
				setValidate(false);
				message = "Invalid countryGeo value" + " [Row No:" + rowNumber + "]";
				validationMessage.put("countryGeo", message);
				errorCodes.add("E5018");
			} else {
				GenericValue geo = ValidatorUtil.getValidGeo(delegator, data.get("countryGeo").toString(), "COUNTRY");
				data.put("countryGeo", geo.getString("geoId"));
			}
			
			if (UtilValidate.isEmpty(data.get("city"))) {
				setValidate(false);
				message = "city is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("city", message);
				errorCodes.add("E5019");
			} else if (!ValidatorUtil.isValidGeo(delegator, data.get("city").toString(), "CITY")) {
				setValidate(false);
				message = "Invalid city value" + " [Row No:" + rowNumber + "]";
				validationMessage.put("city", message);
				errorCodes.add("E5020");
			} else {
				GenericValue validGeo = ValidatorUtil.getValidGeo(delegator, data.get("city").toString(), "CITY");
				if (UtilValidate.isNotEmpty(validGeo)) {
					data.put("city", validGeo.getString("geoId"));
				}
			}
			
			if (!isValidate()) {
				
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(EtlConstants.RESPONSE_MESSAGE, "EmplPosition Data Validation Failed...!");
				
			} else {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
			data.put("errorCodes", StringUtil.join(errorCodes, ","));
			
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "EmplPosition Data Validation Failed...!");
			
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
