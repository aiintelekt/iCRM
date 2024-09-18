/**
 * 
 */
package org.groupfio.etl.process.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fio.homeapps.util.EnumUtil;
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
import org.fio.homeapps.util.DataUtil;;

import javolution.util.FastList;
import org.groupfio.common.portal.util.SrUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import org.fio.homeapps.util.ParamUtil;

/**
 * @author Sharif 
 *
 */
public class ActivityValidator implements Validator {

	private static String MODULE = ActivityValidator.class.getName();
	
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
			
			if(UtilValidate.isEmpty(data.get("workEffortName"))) {
				validate = false;
				message = "Activity name is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("workEffortName", message);
			}
			
			if(UtilValidate.isEmpty(data.get("contactId"))) {
				validate = false;
				message = "Task Primary Contact is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("contactId", message);
			}else if(UtilValidate.isNotEmpty(data.get("contactId")) && UtilValidate.isNotEmpty(data.get("domainEntityId"))) {
				List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
				String contactId = (String) data.get("contactId");
				String custRequestId = (String) data.get("domainEntityId");
				validate = false;
				dataList = SrUtil.getSrAssocParties(delegator, custRequestId);
				for(Map<String, Object> custRequestParty : dataList) {
					if(contactId.equalsIgnoreCase((String)custRequestParty.get("partyId"))) {
						validate = true;
						break;
					}else if(contactId.equalsIgnoreCase((String)custRequestParty.get("name"))) {
						validate = true;
						contactId = (String)custRequestParty.get("partyId");
						data.put("contactId", contactId);
						break;
					}
				}
				if(!validate) {
					message = "Invalid Task Primary Contact" + " [Row No:" + rowNumber + "]";
					validationMessage.put("contactId", message);
				}
			}
			
			if(UtilValidate.isEmpty(data.get("domainEntityId"))) {
				validate = false;
				message = "Source Id is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("domainEntityId", message);
			}
			
			if(UtilValidate.isEmpty(data.get("workEffortTypeId"))) {
				validate = false;
				message = "Activity type is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("type", message);
			}else if(UtilValidate.isNotEmpty(data.get("workEffortTypeId"))) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, data.get("workEffortTypeId").toString().toUpperCase()));
				conditions.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, null));
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue workEffortType = EntityQuery.use(delegator).from("WorkEffortType").where(mainConditon).queryFirst();
				String workEffortTypeId = (UtilValidate.isNotEmpty(workEffortType.getString("workEffortTypeId")) ? workEffortType.getString("workEffortTypeId") : null);
				if (UtilValidate.isNotEmpty(workEffortTypeId) && workEffortTypeId.equals("TASK")) {
					data.put("workEffortTypeId", workEffortTypeId);
				}else {
					validate = false;
					message = "Invalid Activity type" + " [Row No:" + rowNumber + "]";
					validationMessage.put("workEffortTypeId", message);
				}
			}
			
			if(UtilValidate.isEmpty(data.get("workEffortPurposeTypeId"))) {
				validate = false;
				message = "Activity work type is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("workEffortPurposeTypeId", message);
			}else if(UtilValidate.isNotEmpty(data.get("workEffortPurposeTypeId"))) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("workEffortPurposeTypeId", EntityOperator.EQUALS, data.get("workEffortPurposeTypeId")),
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, data.get("workEffortPurposeTypeId").toString().toUpperCase())
						));
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue workEffortPurposeType = EntityQuery.use(delegator).from("WorkEffortPurposeType").where(mainConditon).queryFirst();
				String workEffortPurposeTypeId = (UtilValidate.isNotEmpty(workEffortPurposeType.getString("workEffortPurposeTypeId")) ? workEffortPurposeType.getString("workEffortPurposeTypeId") : null);
				if (UtilValidate.isNotEmpty(workEffortPurposeTypeId) && workEffortPurposeTypeId.equals("TEST_WORK_TYPE")) {
					data.put("workEffortPurposeTypeId", workEffortPurposeTypeId);
				}else {
					validate = false;
					message = "Invalid activity work type" + " [Row No:" + rowNumber + "]";
					validationMessage.put("workEffortPurposeTypeId", message);
				}
			}
			
			if(UtilValidate.isEmpty(data.get("currentStatusId"))) {
				validate = false;
				message = "Activity status is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("currentStatusId", message);
			}else if(UtilValidate.isNotEmpty(data.get("currentStatusId"))) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, data.get("currentStatusId")),
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, data.get("currentStatusId").toString().toUpperCase())
						));
				conditions.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "IA_STATUS_ID"));
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue statusItem = EntityQuery.use(delegator).from("StatusItem").where(mainConditon).queryFirst();
				String currentStatusId = (UtilValidate.isNotEmpty(statusItem.getString("statusId")) ? statusItem.getString("statusId") : null);
				if (UtilValidate.isNotEmpty(currentStatusId) && currentStatusId.equals("IA_MSCHEDULED")) {
					data.put("currentStatusId", currentStatusId);
				}else {
					validate = false;
					message = "Invalid activity status" + " [Row No:" + rowNumber + "]";
					validationMessage.put("currentStatusId", message);
				}
			}
			
			if(UtilValidate.isEmpty(data.get("isSchedulingRequired"))) {
				validate = false;
				message = "Start & End Time Reqd is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("isSchedulingRequired", message);
			}else if(UtilValidate.isNotEmpty(data.get("isSchedulingRequired"))) {
				String isSchedulingRequired = (String)data.get("isSchedulingRequired");
				if("Yes".equalsIgnoreCase(isSchedulingRequired) || "Y".equalsIgnoreCase(isSchedulingRequired)) {
					data.put("isSchedulingRequired", "Y");
				}else {
					validate = false;
					message = "Start & End Time Reqd Should be Y " + " [Row No:" + rowNumber + "]";
					validationMessage.put("isSchedulingRequired", message);
				}
			}
			
			if(UtilValidate.isEmpty(data.get("duration"))) {
				validate = false;
				message = "Duration is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("duration", message);
			}
			
			if(UtilValidate.isEmpty(data.get("arrivalWindow"))) {
				validate = false;
				message = "Tech Arrival Window is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("arrivalWindow", message);
			}
			
			if(UtilValidate.isEmpty(data.get("estimatedStartDate"))) {
				validate = false;
				message = "Sch.Start Date/Time is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("estimatedStartDate", message);
			} else if(UtilValidate.isNotEmpty(data.get("estimatedStartDate"))){
				try {
					Timestamp estimatedStartDate = Timestamp.valueOf((String) data.get("estimatedStartDate"));
					}catch(Exception e) {
						Debug.log(e.getMessage(), MODULE);
						validate = false;
						message = "Invalid Estimated Start Date Format (Format should be yyyy-mm-dd hh:mm:ss)"+ " [Row No:" + rowNumber + "]";
						validationMessage.put("estimatedStartDate", message);
					}
			}
			
			if(UtilValidate.isEmpty(data.get("estimatedCompletionDate"))) {
				validate = false;
				message = "Sch.End Date/Time is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("estimatedCompletionDate", message);
			} else if(UtilValidate.isNotEmpty(data.get("estimatedCompletionDate"))) {
				try {
					Timestamp estimatedCompletionDate = Timestamp.valueOf((String) data.get("estimatedCompletionDate"));
					}catch(Exception e) {
						Debug.log(e.getMessage(), MODULE);
						validate = false;
						message = "Invalid Estimated Completion Date Format (Format should be yyyy-mm-dd hh:mm:ss)"+ " [Row No:" + rowNumber + "]";
						validationMessage.put("estimatedCompletionDate", message);
					}
			}
			
			if (!validate) {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(EtlConstants.RESPONSE_MESSAGE, "Activity Data Validation Failed...!");
			} else {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Activity Data Validation Failed...!");
			return response;
		}
		
		response.put("data", data);
		response.put("validationMessage", validationMessage);
		
		return response;
	}
	
}
