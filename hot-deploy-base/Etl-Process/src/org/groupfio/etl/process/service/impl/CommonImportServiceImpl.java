package org.groupfio.etl.process.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.job.AccountImportJob;
import org.groupfio.etl.process.job.CategoryImportJob;
import org.groupfio.etl.process.job.ContactImportJob;
import org.groupfio.etl.process.job.CustomerImportJob;
import org.groupfio.etl.process.job.EmplPositionImportJob;
import org.groupfio.etl.process.job.InvoiceHeaderImportJob;
import org.groupfio.etl.process.job.InvoiceItemImportJob;
import org.groupfio.etl.process.job.ItmDataImportJob;
import org.groupfio.etl.process.job.LeadImportJob;
import org.groupfio.etl.process.job.LockboxImportJob;
import org.groupfio.etl.process.job.OrderImportJob;
import org.groupfio.etl.process.job.ProdSupplementaryDataImportJob;
import org.groupfio.etl.process.job.ProductImportJob;
import org.groupfio.etl.process.job.SupplierImportJob;
import org.groupfio.etl.process.job.WalletImportJob;
import org.groupfio.etl.process.job.ActivityDataImportJob;
import org.groupfio.etl.process.reader.FileReaderFactory;
import org.groupfio.etl.process.service.CommonImportService;
import org.groupfio.etl.process.util.CommonUtil;
import org.groupfio.etl.process.util.ResponseUtils;
import org.groupfio.etl.process.validator.Validator;
import org.groupfio.etl.process.validator.ValidatorFactory;
import org.groupfio.etl.process.wrapper.ImportWrapper;
import org.groupfio.etl.process.writer.WriterUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class CommonImportServiceImpl implements CommonImportService {
	
	private static String MODULE = CommonImportServiceImpl.class.getName();
	private static String errorLog = "";
	public static String currentListId = "";
	public static String accessType = UtilProperties.getPropertyValue("Etl-Process", "UPLOAD_TYPE");
	public static String etlTableName = UtilProperties.getPropertyValue("Etl-Process", "CUSTOMER_TABLE");
	public static String etlSupplierTableName = UtilProperties.getPropertyValue("Etl-Process", "SUPPLIER_TABLE");
	public static String etlProductTableName = UtilProperties.getPropertyValue("Etl-Process", "PRODUCT_TABLE");
	public static String etlAccountTableName = UtilProperties.getPropertyValue("Etl-Process", "ACCOUNT_TABLE");
	public static String etlCategoryTableName = UtilProperties.getPropertyValue("Etl-Process", "CATEGORY_TABLE");
	public static String etlOrderTableName = UtilProperties.getPropertyValue("Etl-Process", "ORDER_TABLE");
	public static String etlContactTableName = UtilProperties.getPropertyValue("Etl-Process", "CONTACT_TABLE");
	
	public static String etlInvoiceHeaderTableName = UtilProperties.getPropertyValue("Etl-Process",
			"INVOICE_HEADER_TABLE");
	public static String etlInvoiceItemTableName = UtilProperties.getPropertyValue("Etl-Process", "INVOICE_ITEM_TABLE");
	
	public static String etlLockboxBatchTableName = UtilProperties.getPropertyValue("Etl-Process",
			"LOCKBOX_BATCH_TABLE");
	public static String etlLockboxBatchItemTableName = UtilProperties.getPropertyValue("Etl-Process", "LOCKBOX_BATCH_ITEM_TABLE");
	public static String etlWalletTableName = UtilProperties.getPropertyValue("Etl-Process", "WALLET_TABLE");
	public static String etlLeadTableName = UtilProperties.getPropertyValue("Etl-Process", "LEAD_TABLE");
	public static String etlProductSupplementaryTableName = UtilProperties.getPropertyValue("Etl-Process", "PRODUCT_SUPPLEMENTARY_TABLE");
	public static String etlItmTableName = UtilProperties.getPropertyValue("Etl-Process", "ITM_TABLE");
	public static String etlActivityTableName = UtilProperties.getPropertyValue("Etl-Process", "ACTIVITY_TABLE ");
	
	@SuppressWarnings("resource")
	public Map<String, Object> importCustomer(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "CUSTOMER");
						validatorContext.put("tableName", etlTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "CUSTOMER");
							reqContext.put("tableName", etlTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingCustomer", reqContext);
							
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "CUSTOMER", "tableName", etlTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					if (importCount > 0) {
						
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlTableName);
						
						inputNew.put("taskName", "CUSTOMER");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
	
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							CustomerImportJob job = new CustomerImportJob();
							job.setDelegator(delegator);
							job.setDispatcher(dispatcher);
							job.setEtlModelId(listId);
							job.setUserLogin(userLogin);
							job.start();
						}
					}
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	public Map<String, Object> importContact(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "CONTACT");
						validatorContext.put("tableName", etlContactTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "CONTACT");
							reqContext.put("tableName", etlContactTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingContact", reqContext);
							
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "CONTACT", "tableName", etlContactTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					if (importCount > 0) {
						
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlContactTableName);
						
						inputNew.put("taskName", "CONTACT");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> callResult = dispatcher.runSync("createEtlPreProcessor", inputNew);
	
						if (ServiceUtil.isSuccess(callResult) && !isExecuteModelProcess) {
							// Trigger Thread
							ContactImportJob job = new ContactImportJob();
							job.setDelegator(delegator);
							job.setDispatcher(dispatcher);
							job.setEtlModelId(listId);
							job.setUserLogin(userLogin);
							job.start();
						}
					}
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	@SuppressWarnings("resource")
	public Map<String, Object> importSupplier(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "SUPPLIER");
						validatorContext.put("tableName", etlSupplierTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue + 1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "SUPPLIER");
							reqContext.put("tableName", etlSupplierTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingSupplier", reqContext);
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "SUPPLIER", "tableName", etlSupplierTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					if (importCount > 0) {
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlSupplierTableName);
						
						inputNew.put("taskName", "SUPPLIER");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
	
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							SupplierImportJob job = new SupplierImportJob();
							job.setDelegator(delegator);
							job.setDispatcher(dispatcher);
							job.setEtlModelId(listId);
							job.setUserLogin(userLogin);
							job.start();
						}
					}
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	@SuppressWarnings("resource")
	public Map<String, Object> importInvoiceHeader(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "INVOICE");
						validatorContext.put("tableName", etlInvoiceHeaderTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "INVOICE");
							reqContext.put("tableName", etlInvoiceHeaderTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingInvoiceHeader", reqContext);
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "INVOICE", "tableName", etlInvoiceHeaderTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					if (importCount > 0) {
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlInvoiceHeaderTableName);
						
						inputNew.put("taskName", "INVOICE");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
	
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							InvoiceHeaderImportJob job = new InvoiceHeaderImportJob();
							job.setDelegator(delegator);
							job.setDispatcher(dispatcher);
							job.setEtlModelId(listId);
							job.setUserLogin(userLogin);
							job.start();
						}
					}
					
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	@SuppressWarnings("resource")
	public Map<String, Object> importInvoiceItem(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "INVOICE ITEM");
						validatorContext.put("tableName", etlInvoiceItemTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "INVOICE ITEM");
							reqContext.put("tableName", etlInvoiceItemTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingInvoiceItem", reqContext);
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "INVOICE ITEM", "tableName", etlInvoiceItemTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					if (importCount > 0) {
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlInvoiceItemTableName);
						
						inputNew.put("taskName", "INVOICE ITEM");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
	
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							InvoiceItemImportJob job = new InvoiceItemImportJob();
							job.setDelegator(delegator);
							job.setDispatcher(dispatcher);
							job.setEtlModelId(listId);
							job.setUserLogin(userLogin);
							job.start();
						}
					}
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	@SuppressWarnings("resource")
	public Map<String, Object> importProduct(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "PRODUCT");
						validatorContext.put("tableName", etlProductTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue=incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "PRODUCT");
							reqContext.put("tableName", etlProductTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingProduct", reqContext);
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "PRODUCT", "tableName", etlProductTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					if (importCount > 0) {
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlProductTableName);
						
						inputNew.put("taskName", "PRODUCT");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
	
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							ProductImportJob job = new ProductImportJob();
							job.setDelegator(delegator);
							job.setDispatcher(dispatcher);
							job.setEtlModelId(listId);
							job.setUserLogin(userLogin);
							job.start();
						}
					}
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	@SuppressWarnings("resource")
	public Map<String, Object> importAccount(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "ACCOUNT");
						validatorContext.put("tableName", etlAccountTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "ACCOUNT");
							reqContext.put("tableName", etlAccountTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingAccount", reqContext);
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "ACCOUNT", "tableName", etlAccountTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					if (importCount > 0) {
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlAccountTableName);
						
						inputNew.put("taskName", "ACCOUNT");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
	
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							AccountImportJob job = new AccountImportJob();
				   			job.setDelegator(delegator);
				   			job.setDispatcher(dispatcher);
				   			job.setEtlModelId(listId);
				   			job.setUserLogin(userLogin);
				   			job.start();
						}
					}
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	@SuppressWarnings("resource")
	public Map<String, Object> importCategory(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "CATEGORY");
						validatorContext.put("tableName", etlCategoryTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue=incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "CATEGORY");
							reqContext.put("tableName", etlCategoryTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingCategory", reqContext);
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "CATEGORY", "tableName", etlCategoryTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					if (importCount > 0) {
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlCategoryTableName);
						
						inputNew.put("taskName", "CATEGORY");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
	
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							CategoryImportJob job = new CategoryImportJob();
				   			job.setDelegator(delegator);
				   			job.setDispatcher(dispatcher);
				   			job.setEtlModelId(listId);
				   			job.setUserLogin(userLogin);
				   			job.start();
						}
					}
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	@SuppressWarnings("resource")
	public Map<String, Object> importOrder(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "ORDER");
						validatorContext.put("tableName", etlOrderTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "ORDER");
							reqContext.put("tableName", etlOrderTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingOrder", reqContext);
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "ORDER", "tableName", etlOrderTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					if (importCount > 0) {
						Map<String, Object> contextOrderImport = FastMap.newInstance();
						contextOrderImport.put("listId", listId);
						contextOrderImport.put("batchId", batchId);
						dispatcher.runSync("importOrdersToStaing", contextOrderImport);
						
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlOrderTableName);
						
						inputNew.put("taskName", "ORDER");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
	
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							OrderImportJob job = new OrderImportJob();
				   			job.setDelegator(delegator);
				   			job.setDispatcher(dispatcher);
				   			job.setEtlModelId(listId);
				   			job.setUserLogin(userLogin);
				   			job.start();
						}
					}
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	@SuppressWarnings("resource")
	public Map<String, Object> importLockboxBatch(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		String groupId = (String) context.get("groupId");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "LOCKBOX BATCH");
						validatorContext.put("tableName", etlLockboxBatchTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "LOCKBOX BATCH");
							reqContext.put("tableName", etlLockboxBatchTableName);
							reqContext.put("fileName", fileName);
							reqContext.put("groupId", groupId);
							
							result = dispatcher.runSync("createEtlStagingLockboxBatch", reqContext);
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "LOCKBOX BATCH", "tableName", etlLockboxBatchTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					/*if (importCount > 0) {
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlLockboxBatchTableName);
						
						inputNew.put("taskName", "LOCKBOX BATCH");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
	
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							InvoiceHeaderImportJob job = new InvoiceHeaderImportJob();
							job.setDelegator(delegator);
							job.setDispatcher(dispatcher);
							job.setEtlModelId(listId);
							job.setUserLogin(userLogin);
							job.start();
						}
					}*/
					
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	@SuppressWarnings("resource")
	public Map<String, Object> importLockboxBatchItem(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "LOCKBOX BATCH ITEM");
						validatorContext.put("tableName", etlLockboxBatchItemTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "LOCKBOX BATCH ITEM");
							reqContext.put("tableName", etlLockboxBatchItemTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingLockboxBatchItem", reqContext);
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "LOCKBOX BATCH ITEM", "tableName", etlLockboxBatchItemTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					if (importCount > 0) {
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlLockboxBatchItemTableName);
						
						inputNew.put("taskName", "LOCKBOX BATCH ITEM");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
						
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							LockboxImportJob job = new LockboxImportJob();
							job.setDelegator(delegator);
							job.setDispatcher(dispatcher);
							job.setEtlModelId(listId);
							job.setUserLogin(userLogin);
							job.setTaskName("LOCKBOX BATCH ITEM");
							job.setModelName(listId);
							job.start();
						}
						
					}
					
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	@SuppressWarnings("resource")
	public Map<String, Object> importWallet(Map<String, Object> context) {
		Debug.log("============inside import wallet log#1=======");
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			Debug.log("============processId log#2======="+processId);
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				Debug.log("============checkProcess log#3======="+checkProcess);
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Debug.log("============rowValues log#4======="+rowValues);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "WALLET");
						validatorContext.put("tableName", etlWalletTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						Debug.log("============validatorResponse log#5======="+validatorResponse);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 0;
						for (Map<String, Object> rowValue : rowValues) {
							
							if (UtilValidate.isEmpty(rowValue.get("vaType")) || !rowValue.get("vaType").equals("S")){
								continue;
							}
							
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "WALLET");
							reqContext.put("tableName", etlWalletTableName);
							Debug.log("reqContext>>>>>>>>>>>>>>>> "+reqContext);
							reqContext.put("userLogin", userLogin);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingWallet", reqContext);
							Debug.log("============result createEtlStagingWallet log#6======="+result);
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "WALLET", "tableName", etlWalletTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					Debug.log("============importCount log#7======="+importCount);
					if (importCount > 0) {
						
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlWalletTableName);
						
						inputNew.put("taskName", "WALLET");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
						Debug.log("============Res log#8======="+Res);
						Debug.log("============isExecuteModelProcess log#9======="+isExecuteModelProcess);
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							
							WalletImportJob job = new WalletImportJob();
							job.setDelegator(delegator);
							job.setDispatcher(dispatcher);
							job.setEtlModelId(listId);
							job.setUserLogin(userLogin);
							job.setBatchId(batchId);
							job.start();
						}
					} else {
                        requestAttribute.put("model", listId);
                        res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
                        res.put(EtlConstants.RESPONSE_MESSAGE, "Wallet Import Faild. Please check in Error logs section for the details.");
                        res.put("requestAttribute", requestAttribute);
                        return res;
                    }
				}
			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	@SuppressWarnings("resource")
	public Map<String, Object> importLead(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		HttpServletRequest servletRequest = (HttpServletRequest) context.get("servletRequest");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "LEAD");
						validatorContext.put("tableName", etlLeadTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							//rowValue.put("source", "EXT_PARTY_ID");
							//rowValue.put("leadId", "");
							
							if (UtilValidate.isEmpty(rowValue.get("firstName")) &&  UtilValidate.isNotEmpty(rowValue.get("keyContactPerson1"))) {
								rowValue.put("firstName", rowValue.get("keyContactPerson1"));
				    		}
							
							ImportWrapper.wrapLeadData(delegator, userLogin.getString("countryGeoId"), rowValue);
							
							rowValue.put("virtualTeamId", servletRequest.getParameter("virtualTeamId"));
							
							reqContext.put("data", rowValue);
							
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "LEAD");
							reqContext.put("tableName", etlLeadTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingLead", reqContext);
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "LEAD", "tableName", etlLeadTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					if (importCount > 0) {
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlLeadTableName);
						
						inputNew.put("taskName", "LEAD");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
	
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							LeadImportJob job = new LeadImportJob();
				   			job.setDelegator(delegator);
				   			job.setDispatcher(dispatcher);
				   			job.setEtlModelId(listId);
				   			job.setUserLogin(userLogin);
				   			job.start();
						}
					}
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	@SuppressWarnings("resource")
	public Map<String, Object> importEmplPosition(Map<String, Object> context) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		String etlEmplPositionTableName = "DataImportEmplPosition";
		
		try {
			
			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess", UtilMisc.toMap("processId", processId), null, false));
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "EMPL_POSITION");
						validatorContext.put("tableName", etlEmplPositionTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							//rowValue.put("source", "EXT_PARTY_ID");
							//rowValue.put("dataImportEmplPositionId", null);
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "EMPL_POSITION");
							reqContext.put("tableName", etlEmplPositionTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingEmplPosition", reqContext);
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "EMPL_POSITION", "tableName", etlEmplPositionTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					
					if (importCount > 0) {
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlEmplPositionTableName);
						
						inputNew.put("taskName", "EMPL_POSITION");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
	
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							EmplPositionImportJob job = new EmplPositionImportJob();
				   			job.setDelegator(delegator);
				   			job.setDispatcher(dispatcher);
				   			job.setEtlModelId(listId);
				   			job.setUserLogin(userLogin);
				   			job.start();
						}
					}
				}

			}
			
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}
		
		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	public Map<String, Object> importProductSupplementary(Map<String, Object> context) {
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {

			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityQuery.use(delegator).from("EtlProcess").where("processId",processId).queryFirst();
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityQuery.use(delegator).from("EtlUploadRequest").where("status","RUNNING").queryFirst();

					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "PSD");
						validatorContext.put("tableName", etlProductSupplementaryTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "PSD");
							reqContext.put("tableName", etlProductSupplementaryTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingProductSupplementary", reqContext);
							
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "PSD", "tableName", etlProductSupplementaryTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					if (importCount > 0) {
						
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlProductSupplementaryTableName);
						
						inputNew.put("taskName", "PSD");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							ProdSupplementaryDataImportJob job = new ProdSupplementaryDataImportJob();
							job.setDelegator(delegator);
							job.setDispatcher(dispatcher);
							job.setEtlModelId(listId);
							job.setUserLogin(userLogin);
							job.start();
						}
					}
				}

			}
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}

		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	public Map<String, Object> importItm(Map<String, Object> context) {
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {

			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityQuery.use(delegator).from("EtlProcess").where("processId",processId).queryFirst();
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityQuery.use(delegator).from("EtlUploadRequest").where("status","RUNNING").queryFirst();

					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "ITM");
						validatorContext.put("tableName", etlItmTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "ITM");
							reqContext.put("tableName", etlItmTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingItm", reqContext);
							
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "ITM", "tableName", etlItmTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					if (importCount > 0) {
						
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlItmTableName);
						
						inputNew.put("taskName", "ITM");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							ItmDataImportJob job = new ItmDataImportJob();
							job.setDelegator(delegator);
							job.setDispatcher(dispatcher);
							job.setEtlModelId(listId);
							job.setUserLogin(userLogin);
							job.start();
						}
					}
				}

			}
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}

		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}
	
	public Map<String, Object> importActivity(Map<String, Object> context) {
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> requestAttribute = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		String filePath = (String) context.get("filePath");
		boolean isExecuteModelProcess = ParamUtil.getBoolean(context, "isExecuteModelProcess");
		
		String listId = modelName;
		int importCount = 0;
		int notImportCount = 0;
		
		try {

			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityQuery.use(delegator).from("EtlProcess").where("processId",processId).queryFirst();
				
				String process=""; String table = ""; String model="";
				String serviceName="";
				
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
					String fileName = UtilValidate.isNotEmpty(context.get("filePath")) ? new File(context.get("filePath").toString()).getName() : null;
					
					GenericValue checkUploadRequest = EntityQuery.use(delegator).from("EtlUploadRequest").where("status","RUNNING").queryFirst();

					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						requestAttribute.put("execute","lock");
						requestAttribute.put("model", processId);
						//return "success";
					}
					
					String fileType = CommonUtil.getFileExtension(filePath);
					
					String batchId = UtilDateTime.nowDateString("YYYYMMddHHMMSS");
					res.put("batchId", batchId);
					
					org.groupfio.etl.process.reader.FileReader fileReader = FileReaderFactory.getFileReader(fileType);
					
					Map<String, Object> reqContext = FastMap.newInstance();
					reqContext.putAll(context);
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("listId", listId);
					reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
					
					List<Map<String, Object>> rowValues = fileReader.read(reqContext);
					Map<String, Object> result = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(rowValues)) {
						
						Validator validator = ValidatorFactory.getDefaultValueValidator();
						Map<String, Object> validatorContext = new HashMap<String, Object>();
						validatorContext.put("delegator", delegator);
						validatorContext.put("parseCount", rowValues.size());
						validatorContext.put("modelName", listId);
						validatorContext.put("taskName", "ACTIVITY");
						validatorContext.put("tableName", etlActivityTableName);
						
						Map<String, Object> validatorResponse = validator.validate(validatorContext);
						if (ResponseUtils.isError(validatorResponse)) {
							requestAttribute.put("model", listId);
							requestAttribute.put("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(validatorResponse));
							res.put(EtlConstants.RESPONSE_CODE, ResponseUtils.getResponseCode(validatorResponse));
							res.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(validatorResponse));
							res.put("requestAttribute", requestAttribute);
							
							return res;
						}
						
						String importError = "";
						int incrementValue = 1;
						for (Map<String, Object> rowValue : rowValues) {
							incrementValue = incrementValue+1;
							reqContext = FastMap.newInstance();
							
							reqContext.put("data", rowValue);
							reqContext.put("userLogin", userLogin);
							
							reqContext.put("listId", listId);
							reqContext.put("batchId", batchId);
							reqContext.put("incrementValue", incrementValue);
							reqContext.put("isExecuteModelProcess", isExecuteModelProcess);
							reqContext.put("taskName", "ACTIVITY");
							reqContext.put("tableName", etlActivityTableName);
							reqContext.put("fileName", fileName);
							
							result = dispatcher.runSync("createEtlStagingActivity", reqContext);
							
							if (ResponseUtils.isError(result)) {
								notImportCount++;
								importError += result.get(GlobalConstants.RESPONSE_MESSAGE)+", ";
								continue;
							}
							importCount++;
						}
						
						if (notImportCount>0) {
							importError = importError.substring(0, importError.length()-2);
							WriterUtil.writeLog(delegator, UtilMisc.toMap("taskName", "ACTIVITY", "tableName", etlActivityTableName, "modelName", modelName, "logMsg", "Not processed counts: "+notImportCount+", "+importError, "fileName", fileName));
						}
					}
					else{
						requestAttribute.put("model", listId);
						res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						res.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed, No Records Found");
						res.put("requestAttribute", requestAttribute);
						return res;
					}
					if (importCount > 0) {
						
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("userLogin", userLogin);
						inputNew.put("batchId", batchId);
						inputNew.put("modelId", listId);
						inputNew.put("accessType", accessType);
						inputNew.put("etlTableName", etlActivityTableName);
						
						inputNew.put("taskName", "ACTIVITY");
						inputNew.put("isExecuteModelProcess", isExecuteModelProcess);
						
						Map<String, Object> Res = dispatcher.runSync("createEtlPreProcessor", inputNew);
						if (ServiceUtil.isSuccess(Res) && !isExecuteModelProcess) {
							// Trigger Thread
							ActivityDataImportJob job = new ActivityDataImportJob();
							job.setDelegator(delegator);
							job.setDispatcher(dispatcher);
							job.setEtlModelId(listId);
							job.setUserLogin(userLogin);
							job.start();
						}
					}
				}

			}
		} catch (Exception e1) {
			requestAttribute.put("model", listId);
			requestAttribute.put("_ERROR_MESSAGE_", e1.toString());
			res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			res.put(EtlConstants.RESPONSE_MESSAGE, e1.toString());
			res.put("requestAttribute", requestAttribute);
			
			return res;
		}

		requestAttribute.put("model", listId);
		requestAttribute.put("_EVENT_MESSAGE_",
				UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		
		res.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		res.put("requestAttribute", requestAttribute);
		
		return res;
	}

}