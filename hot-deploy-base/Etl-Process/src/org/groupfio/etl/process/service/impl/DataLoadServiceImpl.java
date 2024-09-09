/**
 * 
 */
package org.groupfio.etl.process.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.UtilMessage;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.util.DataUtil;
import org.groupfio.etl.process.util.ResponseUtils;
import org.groupfio.etl.process.validator.Validator;
import org.groupfio.etl.process.validator.ValidatorFactory;
import org.groupfio.etl.process.writer.WriterUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Sharif
 *
 */
public class DataLoadServiceImpl {

	private static String MODULE = DataLoadServiceImpl.class.getName();
	public static String currentListId = "";
	
	public static Map<String, Object> createEtlStagingAccount(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			Map<String, Object> validatorResponse = new HashMap<String, Object>();
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getAccountDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("locale", context.get("locale"));
				validatorContext.put("rowNumber", incrementValue);
				validatorContext.put("userLogin", userLogin);
				
				validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					/*return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));*/
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			GenericValue entity = null;
			
			if (UtilValidate.isNotEmpty(data.get("leadId"))) {
				entity = EntityUtil.getFirst(delegator.findByAnd("DataImportAccount", UtilMisc.toMap("accountId", data.get("accountId")), null, false));
				if (UtilValidate.isNotEmpty(entity)) {
					data.put("primaryPartyId", entity.getString("primaryPartyId"));
					entity.remove();
				}
			}
			
			entity = delegator.makeValue("DataImportAccount");
			
			if (ResponseUtils.isError(validatorResponse)) {
				entity.put("importStatusId", "DATAIMP_ERROR");
				entity.put("importError", UtilMessage.mapToStr((Map<String, Object>) validatorResponse.get("validationMessage")));
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				result.put(GlobalConstants.RESPONSE_MESSAGE, entity.get("importError"));
			} else {
				entity.put("importStatusId", "DATAIMP_APPROVED");
				entity.put("importError", null);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
			entity.put("batchId", context.get("batchId"));
			entity.put("uploadedByUserLoginId", userLogin.get("userLoginId"));
			
			DataUtil.prepareAccountData(data);
			
			entity.putAll(data);
			
			/*if (UtilValidate.isEmpty(entity.getString("virtualTeamId"))) {
				Map<String, Object> virtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, userLogin.getString("partyId"));
				entity.put("virtualTeamId", virtualTeam.get("virtualTeamId"));
			}*/
			
			if (UtilValidate.isNotEmpty(entity.getString("accountId"))) {
				delegator.createOrStore(entity);
			}
			
		} catch (Exception e) {
			Debug.log("createEtlStagingAccount ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		return result;
	}
	
	public static Map<String, Object> createEtlStagingLead(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			Map<String, Object> validatorResponse = new HashMap<String, Object>();
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getLeadDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("locale", context.get("locale"));
				validatorContext.put("rowNumber", incrementValue);
				validatorContext.put("userLogin", userLogin);
				
				validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					/*return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));*/
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			GenericValue entity = null;
			
			if (UtilValidate.isNotEmpty(data.get("leadId"))) {
				entity = EntityUtil.getFirst(delegator.findByAnd("DataImportLead", UtilMisc.toMap("leadId", data.get("leadId")), null, false));
				if (UtilValidate.isNotEmpty(entity)) {
					data.put("primaryPartyId", entity.getString("primaryPartyId"));
					entity.remove();
				}
			}
			
			entity = delegator.makeValue("DataImportLead");
			
			if (ResponseUtils.isError(validatorResponse)) {
				entity.put("importStatusId", "DATAIMP_ERROR");
				entity.put("importError", UtilMessage.mapToStr((Map<String, Object>) validatorResponse.get("validationMessage")));
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				result.put(GlobalConstants.RESPONSE_MESSAGE, entity.get("importError"));
			} else {
				entity.put("importStatusId", "DATAIMP_APPROVED");
				entity.put("importError", null);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
			entity.put("batchId", context.get("batchId"));
			entity.put("uploadedByUserLoginId", userLogin.get("userLoginId"));
			
			DataUtil.prepareLeadData(data);
			
			entity.putAll(data);
			
			/*if (UtilValidate.isEmpty(entity.getString("virtualTeamId"))) {
				Map<String, Object> virtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, userLogin.getString("partyId"));
				entity.put("virtualTeamId", virtualTeam.get("virtualTeamId"));
			}*/
			
			if (UtilValidate.isNotEmpty(entity.getString("leadId"))) {
				delegator.createOrStore(entity);
			}
			
		} catch (Exception e) {
			Debug.log("createEtlStagingLead ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		return result;
	}
	
	public static Map<String, Object> createEtlStagingCustomer(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			Map<String, Object> validatorResponse = new HashMap<String, Object>();
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getCustomerDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("locale", context.get("locale"));
				validatorContext.put("rowNumber", incrementValue);
				validatorContext.put("userLogin", userLogin);
				
				validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					/*return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));*/
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			GenericValue entity = null;
			
			if (UtilValidate.isNotEmpty(data.get("customerId"))) {
				entity = EntityUtil.getFirst(delegator.findByAnd("DataImportCustomer", UtilMisc.toMap("customerId", data.get("customerId")), null, false));
				if (UtilValidate.isNotEmpty(entity)) {
					data.put("primaryPartyId", entity.getString("primaryPartyId"));
					entity.remove();
				}
			}
			
			entity = delegator.makeValue("DataImportCustomer");
			
			if (ResponseUtils.isError(validatorResponse)) {
				entity.put("importStatusId", "DATAIMP_ERROR");
				entity.put("importError", UtilMessage.mapToStr((Map<String, Object>) validatorResponse.get("validationMessage")));
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				result.put(GlobalConstants.RESPONSE_MESSAGE, entity.get("importError"));
			} else {
				entity.put("importStatusId", "DATAIMP_APPROVED");
				entity.put("importError", null);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
			entity.put("batchId", context.get("batchId"));
			entity.put("uploadedByUserLoginId", userLogin.get("userLoginId"));
			
			DataUtil.prepareCustomerData(data);
			
			entity.putAll(data);
			
			/*if (UtilValidate.isEmpty(entity.getString("virtualTeamId"))) {
				Map<String, Object> virtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, userLogin.getString("partyId"));
				entity.put("virtualTeamId", virtualTeam.get("virtualTeamId"));
			}*/
			
			if (UtilValidate.isNotEmpty(entity.getString("customerId"))) {
				delegator.createOrStore(entity);
			}
			
		} catch (Exception e) {
			Debug.log("createEtlStagingCustomer ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		return result;
	}
	
	public static Map<String, Object> createEtlStagingContact(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			Map<String, Object> validatorResponse = new HashMap<String, Object>();
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getContactDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("locale", context.get("locale"));
				validatorContext.put("rowNumber", incrementValue);
				validatorContext.put("userLogin", userLogin);
				
				validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					/*return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));*/
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			GenericValue entity = null;
			
			if (UtilValidate.isNotEmpty(data.get("contactId"))) {
				entity = EntityUtil.getFirst(delegator.findByAnd("DataImportContact", UtilMisc.toMap("contactId", data.get("contactId")), null, false));
				if (UtilValidate.isNotEmpty(entity)) {
					data.put("primaryPartyId", entity.getString("primaryPartyId"));
					entity.remove();
				}
			}
			
			entity = delegator.makeValue("DataImportContact");
			
			if (ResponseUtils.isError(validatorResponse)) {
				entity.put("importStatusId", "DATAIMP_ERROR");
				entity.put("importError", UtilMessage.mapToStr((Map<String, Object>) validatorResponse.get("validationMessage")));
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				result.put(GlobalConstants.RESPONSE_MESSAGE, entity.get("importError"));
			} else {
				entity.put("importStatusId", "DATAIMP_APPROVED");
				entity.put("importError", null);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
			entity.put("batchId", context.get("batchId"));
			entity.put("uploadedByUserLoginId", userLogin.get("userLoginId"));
			
			DataUtil.prepareContactData(data);
			
			entity.putAll(data);
			
			/*if (UtilValidate.isEmpty(entity.getString("virtualTeamId"))) {
				Map<String, Object> virtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, userLogin.getString("partyId"));
				entity.put("virtualTeamId", virtualTeam.get("virtualTeamId"));
			}*/
			
			if (UtilValidate.isNotEmpty(entity.getString("contactId"))) {
				delegator.createOrStore(entity);
			}
			
		} catch (Exception e) {
			Debug.log("createEtlStagingContact ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		return result;
	}
	
	public static Map<String, Object> createEtlStagingProductSupplementary(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			Map<String, Object> validatorResponse = new HashMap<String, Object>();
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getProductSupplementaryDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("locale", context.get("locale"));
				validatorContext.put("rowNumber", incrementValue);
				validatorContext.put("userLogin", userLogin);
				

				validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					/*return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));*/
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}

			GenericValue entity = delegator.makeValue("DataImportProductSupplementary");
			
			if (ResponseUtils.isError(validatorResponse)) {
				entity.put("importStatusId", "DATAIMP_ERROR");
				entity.put("importError", UtilMessage.mapToStr((Map<String, Object>) validatorResponse.get("validationMessage")));
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				result.put(GlobalConstants.RESPONSE_MESSAGE, entity.get("importError"));
			} else {
				entity.put("importStatusId", "DATAIMP_APPROVED");
				entity.put("importError", null);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
			entity.put("batchId", context.get("batchId"));
			entity.put("uploadedByUserLoginId", userLogin.get("userLoginId"));
			
			DataUtil.prepareProductSupplementaryData(data);
			String productLink = (String)data.get("productLink");
			entity.putAll(data);
			
			if (UtilValidate.isNotEmpty(entity.getString("productId"))) {
				delegator.createOrStore(entity);
			}
			
		}  catch (Exception e) {
			Debug.log("createEtlStagingProductSupplementary ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		return result;
	}
	
	public static Map<String, Object> createEtlStagingItm(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			Map<String, Object> validatorResponse = new HashMap<String, Object>();
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getItmDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("locale", context.get("locale"));
				validatorContext.put("rowNumber", incrementValue);
				validatorContext.put("userLogin", userLogin);
				
				validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					/*return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));*/
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}

			GenericValue entity = delegator.makeValue("DataImportItm");
			
			if (ResponseUtils.isError(validatorResponse)) {
				entity.put("importStatusId", "DATAIMP_ERROR");
				entity.put("importError", UtilMessage.mapToStr((Map<String, Object>) validatorResponse.get("validationMessage")));
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				result.put(GlobalConstants.RESPONSE_MESSAGE, entity.get("importError"));
			} else {
				entity.put("importStatusId", "DATAIMP_APPROVED");
				entity.put("importError", null);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
			entity.put("batchId", context.get("batchId"));
			entity.put("uploadedByUserLoginId", userLogin.get("userLoginId"));
			
			DataUtil.prepareItmData(data);
			entity.putAll(data);
			
			if (UtilValidate.isNotEmpty(entity.getString("invoiceId")) && UtilValidate.isNotEmpty(entity.getString("invoiceSequenceNumber"))) {
				delegator.createOrStore(entity);
			}
			
		}  catch (Exception e) {
			e.printStackTrace();
			Debug.log("createEtlStagingItm ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		return result;
	}
	
	public static Map<String, Object> createEtlStagingActivity(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			Map<String, Object> validatorResponse = new HashMap<String, Object>();
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getActivityDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("locale", context.get("locale"));
				validatorContext.put("rowNumber", incrementValue);
				validatorContext.put("userLogin", userLogin);
				

				validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					/*return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));*/
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			GenericValue entity = delegator.makeValue("DataImportActivity");
			
			if (ResponseUtils.isError(validatorResponse)) {
				entity.put("importStatusId", "DATAIMP_ERROR");
				entity.put("importError", UtilMessage.mapToStr((Map<String, Object>) validatorResponse.get("validationMessage")));
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				result.put(GlobalConstants.RESPONSE_MESSAGE, entity.get("importError"));
			} else {
				entity.put("importStatusId", "DATAIMP_APPROVED");
				entity.put("importError", null);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
			entity.put("batchId", context.get("batchId"));
			entity.put("uploadedByUserLoginId", userLogin.get("userLoginId"));
			
			DataUtil.prepareActivityData(data);
			entity.putAll(data);
			if (UtilValidate.isNotEmpty(entity.getString("activityId"))) {
				delegator.createOrStore(entity);
			}
			
		}  catch (Exception e) {
			Debug.log("createEtlStagingActivity ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		return result;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	
	public static Map<String, Object> createEtlStagingSupplier(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getSupplierDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("rowNumber", incrementValue);
				
				Map<String, Object> validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			DataUtil.prepareSupplierData(data);
			
			GenericValue entity = EntityUtil.getFirst(delegator.findByAnd("DataImportSupplier", UtilMisc.toMap("supplierId", data.get("supplierId")), null, false));
			if (UtilValidate.isEmpty(entity)) {
				entity = delegator.makeValue("DataImportSupplier");
				entity.put("batchId", context.get("batchId"));
			}
			
			entity.putAll(data);
			
			delegator.createOrStore(entity);
			
		} catch (Exception e) {
			Debug.log("createEtlStagingSupplier ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return result;
	}
	
	public static Map<String, Object> createEtlStagingInvoiceHeader(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getInvoiceHeaderDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("rowNumber", incrementValue);
				
				Map<String, Object> validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			DataUtil.prepareInvoiceHeaderData(data);
			
			GenericValue entity = EntityUtil.getFirst(delegator.findByAnd("DataImportInvoiceHeader", UtilMisc.toMap("invoiceId", data.get("invoiceId")), null, false));
			if (UtilValidate.isEmpty(entity)) {
				entity = delegator.makeValue("DataImportInvoiceHeader");
				entity.put("batchId", context.get("batchId"));
			}
			
			entity.putAll(data);
			
			delegator.createOrStore(entity);
			
		} catch (Exception e) {
			Debug.log("createEtlStagingInvoiceHeader ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return result;
	}
	
	public static Map<String, Object> createEtlStagingInvoiceItem(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getInvoiceItemDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("rowNumber", incrementValue);
				
				Map<String, Object> validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			DataUtil.prepareInvoiceItemData(data);
			
			GenericValue entity = EntityUtil.getFirst(delegator.findByAnd("DataImportInvoiceItem", UtilMisc.toMap("invoiceId", data.get("invoiceId"), "invoiceItemSeqId", data.get("invoiceItemSeqId")), null, false));
			if (UtilValidate.isEmpty(entity)) {
				entity = delegator.makeValue("DataImportInvoiceItem");
				entity.put("batchId", context.get("batchId"));
			}
			
			entity.putAll(data);
			
			delegator.createOrStore(entity);
			
		} catch (Exception e) {
			Debug.log("createEtlStagingInvoiceItem ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return result;
	}
	
	public static Map<String, Object> createEtlStagingProduct(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getProductDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("rowNumber", incrementValue);
				
				Map<String, Object> validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			DataUtil.prepareProductData(data);
			
			GenericValue entity = EntityUtil.getFirst(delegator.findByAnd("DataImportProduct", UtilMisc.toMap("productId", data.get("productId")), null, false));
			if (UtilValidate.isEmpty(entity)) {
				entity = delegator.makeValue("DataImportProduct");
				entity.put("batchId", context.get("batchId"));
			}
			
			entity.putAll(data);
			
			delegator.createOrStore(entity);
			
		} catch (Exception e) {
			Debug.log("createEtlStagingProduct ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return result;
	}
	
	public static Map<String, Object> createEtlStagingCategory(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getCategoryDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("rowNumber", incrementValue);
				
				Map<String, Object> validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			DataUtil.prepareCategoryData(data);
			
			GenericValue entity = EntityUtil.getFirst(delegator.findByAnd("DataImportCategory", UtilMisc.toMap("categoryId", data.get("categoryId")), null, false));
			if (UtilValidate.isEmpty(entity)) {
				entity = delegator.makeValue("DataImportCategory");
				entity.put("batchId", context.get("batchId"));
			}
			
			entity.putAll(data);
			
			delegator.createOrStore(entity);
			
		} catch (Exception e) {
			Debug.log("createEtlStagingCategory ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return result;
	}
	
	public static Map<String, Object> createEtlStagingOrder(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			
			GenericValue entity = EntityUtil.getFirst(delegator.findByAnd("EtlImportOrderFields", UtilMisc.toMap("orderId", data.get("orderId"), "orderItemCode", data.get("orderItemCode")), null, false));
			if (UtilValidate.isNotEmpty(entity)) {
				String orderId = (String) entity.get("orderId");
				String batchId = (String) entity.get("batchId");
				String orderItemCode = (String) entity.get("orderItemCode");
				
				Debug.logInfo("Already imported EtlImportOrderFields# orderId:" + orderId + ", batchId" + batchId
						+ ", orderItemCode:" + orderItemCode, MODULE);
				
				return ServiceUtil.returnSuccess();
			}
			
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getOrderDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("rowNumber", incrementValue);
				
				Map<String, Object> validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			DataUtil.prepareOrderData(data);
			
			entity = delegator.makeValue("EtlImportOrderFields");
			entity.put("batchId", context.get("batchId"));
			
			entity.putAll(data);
			delegator.createOrStore(entity);
			
		} catch (Exception e) {
			Debug.log("createEtlStagingOrder ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return result;
	}
	
	public static Map<String, Object> createEtlStagingLockboxBatch(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		String groupId = (String) context.get("groupId");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getLockboxBatchDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("rowNumber", incrementValue);
				
				Map<String, Object> validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			DataUtil.prepareLockboxBatchData(data);
			
			GenericValue entity = EntityUtil.getFirst(delegator.findByAnd("FioLockboxBatchStaging", UtilMisc.toMap("batchNumber", data.get("batchNumber")), null, false));
			if (UtilValidate.isEmpty(entity)) {
				entity = delegator.makeValue("FioLockboxBatchStaging");
				entity.put("batchId", context.get("batchId"));
			}
			
			if (UtilValidate.isNotEmpty(groupId)) {
				
				GenericValue groupAssoc = EntityUtil.getFirst(delegator.findByAnd("LockboxGroupPartyAssoc", UtilMisc.toMap("groupId", groupId), null, false));
				if (UtilValidate.isNotEmpty(groupAssoc)) {
					entity.put("groupId", groupAssoc.get("groupId"));
					entity.put("supplierPartyId", groupAssoc.get("supplierPartyId"));
				}
				
			}
			
			if (UtilValidate.isNotEmpty(userLogin)) {
				entity.put("createdByUserLoginId", userLogin.getString("userLoginId"));
			}
			
			entity.putAll(data);
			
			delegator.createOrStore(entity);
			
		} catch (Exception e) {
			Debug.log("createEtlStagingLockboxBatch ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return result;
	}
	
	public static Map<String, Object> createEtlStagingLockboxBatchItem(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getLockboxBatchItemDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("rowNumber", incrementValue);
				
				Map<String, Object> validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					
					/*return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));*/
					
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			DataUtil.prepareLockboxBatchItemData(data);
			
			GenericValue entity = EntityUtil.getFirst(delegator.findByAnd("FioLockboxBatchItemStaging", UtilMisc.toMap("batchNumber", data.get("batchNumber"), "batchItemSeqId", data.get("batchItemSeqId"), "detailItemSeqId", data.get("detailItemSeqId")), null, false));
			if (UtilValidate.isEmpty(entity)) {
				entity = delegator.makeValue("FioLockboxBatchItemStaging");
				entity.put("batchId", context.get("batchId"));
			}
			
			if (UtilValidate.isNotEmpty(userLogin)) {
				entity.put("createdByUserLoginId", userLogin.getString("userLoginId"));
			}
			
			entity.putAll(data);
			
			delegator.createOrStore(entity);
			
		} catch (Exception e) {
			Debug.log("createEtlStagingLockboxBatchItem ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return result;
	}
	
	public static Map<String, Object> createEtlStagingWallet(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getWalletDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("rowNumber", incrementValue);
				
				Map<String, Object> validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));

                    //Increase the error count
                    GenericValue dataImportWallet = delegator.makeValue("DataImportWallet");
                    dataImportWallet.put("batchId", context.get("batchId"));
                    dataImportWallet.put("batchItemSeqId", incrementValue.toString());
                    dataImportWallet.put("importStatusId", "DATAIMP_FAILED");
                    dataImportWallet.put("action", data.get("action"));
                    dataImportWallet.put("createdByUserLoginId", userLogin.getString("userLoginId"));
                    dataImportWallet.create();
                    return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			DataUtil.prepareWalletData(data);
			
			GenericValue entity = delegator.makeValue("DataImportWallet");
			entity.put("batchId", context.get("batchId"));
			entity.put("batchItemSeqId", incrementValue.toString());
			
			Debug.log("batchId createEtlStagingWallet>>>>>>>>>>>>>>>> "+context.get("batchId"));
			
			Debug.log("entity before>>>>>>>>>>>>>>>> "+entity);
			entity.putAll(data);
			entity.put("createdByUserLoginId", userLogin.getString("userLoginId"));
			Debug.log("entity after>>>>>>>>>>>>>>>> "+entity);
			delegator.createOrStore(entity);
		} catch (Exception e) {
			Debug.log("createEtlStagingWallet ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return result;
	}
	
	public static Map<String, Object> createEtlStagingEmplPosition(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String currentListId = (String) context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		Integer incrementValue = (Integer) context.get("incrementValue");
		Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
		String fileName = (String) context.get("fileName");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Debug.log("===incrementValue====" + incrementValue);
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			Map<String, Object> validatorResponse = new HashMap<String, Object>();
			if (!isExecuteModelProcess) {
				Validator validator = ValidatorFactory.getEmplPositionDataValidator();
				Map<String, Object> validatorContext = new HashMap<String, Object>();
				validatorContext.put("delegator", delegator);
				validatorContext.put("data", data);
				validatorContext.put("modelName", currentListId);
				validatorContext.put("taskName", context.get("taskName"));
				validatorContext.put("tableName", context.get("tableName"));
				validatorContext.put("locale", context.get("locale"));
				validatorContext.put("rowNumber", incrementValue);
				
				validatorResponse = validator.validate(validatorContext);
				if (ResponseUtils.isError(validatorResponse)) {
					WriterUtil.writeLog(delegator, context.get("taskName").toString(), context.get("tableName").toString(), 
							currentListId, fileName, (Map<String, Object>) validatorResponse.get("validationMessage"));
					/*return ServiceUtil.returnError(
							UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));*/
				}
				
				data = (Map<String, Object>) validatorResponse.get("data");
			}
			
			GenericValue entity = null;
			
			/*entity = EntityUtil.getFirst(delegator.findByAnd("DataImportEmplPosition", UtilMisc.toMap("companyId", data.get("companyId"), "reporting1bankid", data.get("reporting1bankid"), "managed1bankid", data.get("managed1bankid"), "teamId", data.get("teamId"), "isAccess", data.get("isAccess")), null, false));
			if (UtilValidate.isNotEmpty(entity)) {
				entity.remove();
			}*/
			
			entity = delegator.makeValue("DataImportEmplPosition");
			
			if (ResponseUtils.isError(validatorResponse)) {
				entity.put("importStatusId", "DATAIMP_ERROR");
			}
			
			entity.put("batchId", context.get("batchId"));
			entity.put("uploadedByUserLoginId", userLogin.get("userLoginId"));
			
			DataUtil.prepareEmplPositionData(data);
			
			entity.putAll(data);
			
			entity.put("dataImportEmplPositionId", delegator.getNextSeqId("DataImportEmplPosition"));
			
			delegator.create(entity);
			
		} catch (Exception e) {
			Debug.log("createEtlStagingEmplPosition ERROR: "+e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return result;
	}
	
}