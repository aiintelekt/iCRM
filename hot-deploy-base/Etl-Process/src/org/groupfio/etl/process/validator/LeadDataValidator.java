/**
 * 
 */
package org.groupfio.etl.process.validator;

import java.util.ArrayList;
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
public class LeadDataValidator implements Validator {

	private static String MODULE = LeadDataValidator.class.getName();
	
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
			
			if (UtilValidate.isEmpty(data.get("leadId"))) {
				validate = false;
				message = "LeadId is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("leadId", message);
			}
			
			if (UtilValidate.isEmpty(data.get("companyName"))) {
				validate = false;
				message = "LeadName is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("leadName", message);	
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
			
			if (UtilValidate.isEmpty(data.get("source"))) {
				validate = false;
				message = "Source is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("source", message);
			} else {
				List conditionList = FastList.newInstance();
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("dataSourceId")), EntityOperator.EQUALS, data.get("source").toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("name")), EntityOperator.EQUALS, data.get("source").toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, data.get("source").toString().toUpperCase())
               			) );    
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue dataSource = EntityUtil.getFirst( delegator.findList("DataSource", mainConditons, UtilMisc.toSet("dataSourceId"), null, null, false) );
				if (UtilValidate.isEmpty(dataSource)) {
					validate = false;
					message = "Invalid source" + " [Row No:" + rowNumber + "]";
					validationMessage.put("source", message);
				} else {
					data.put("source", dataSource.getString("dataSourceId"));
				}
			}
			
			if (UtilValidate.isNotEmpty(data.get("industry"))) { 
				if( !EnumUtil.isValidEnum(delegator, (String)data.get("industry"), "PARTY_INDUSTRY")) {
					validate = false;
					message = "Invalid industry" + " [Row No:" + rowNumber + "]";
					validationMessage.put("industry", message);
				} else {
					data.put("industry", EnumUtil.getEnum(delegator, (String)data.get("industry"), "PARTY_INDUSTRY").getString("enumId"));
				}
			}
			
			if (UtilValidate.isNotEmpty(data.get("ownershipEnumId"))) { 
				if(!EnumUtil.isValidEnum(delegator, (String)data.get("ownershipEnumId"), "PARTY_OWNERSHIP")) {
					validate = false;
					message = "Invalid ownershipEnumId" + " [Row No:" + rowNumber + "]";
					validationMessage.put("ownershipEnumId", message);
				} else {
					data.put("ownershipEnumId", EnumUtil.getEnum(delegator, (String)data.get("ownershipEnumId"), "PARTY_OWNERSHIP").getString("enumId"));
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
			
			if (UtilValidate.isNotEmpty(data.get("emailAddress")) && !ValidatorUtil.validateEmail(data.get("emailAddress").toString())) {
				validate = false;
				message = "Invalid emailAddress" + " [Row No:" + rowNumber + "]";
				validationMessage.put("emailAddress", message);
			}
			
			if (UtilValidate.isNotEmpty(data.get("personResponsible"))) { 
				String responsibleRoles = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "RESPONSIBLE_ROLES", "CUST_SERVICE_REP,ACCOUNT_MANAGER,SALES_REP,SALES_REP_MANAGER");
				Set<String> roles = StringUtil.split(responsibleRoles, ",").stream().collect(Collectors.toSet());
				List<GenericValue> partyRoleList = EntityQuery.use(delegator).from("PartyRole").where("partyId", data.get("personResponsible")).queryList();
				partyRoleList = partyRoleList.stream().filter(e->roles.contains(e.getString("roleTypeId"))).collect(Collectors.toList());
				if (UtilValidate.isEmpty(partyRoleList)) {
					validate = false;
					message = "Invalid personResponsible" + " [Row No:" + rowNumber + "], Supported roles# "+responsibleRoles;
					validationMessage.put("personResponsible", message);
				}
			}

			if (UtilValidate.isNotEmpty(data.get("segmentation"))) {
				String segmentation = (String)data.get("segmentation");
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, data.get("segmentation")),
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("customFieldName")), EntityOperator.EQUALS, segmentation.toString().toUpperCase())
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
				response.put(EtlConstants.RESPONSE_MESSAGE, "Lead Data Validation Failed...!");
			} else {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Lead Data Validation Failed...!");
			return response;
		}
		
		response.put("data", data);
		response.put("validationMessage", validationMessage);
		
		return response;
	}
	
}
