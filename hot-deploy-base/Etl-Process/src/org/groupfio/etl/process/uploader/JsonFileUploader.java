/**
 * 
 */
package org.groupfio.etl.process.uploader;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.service.ServiceExecutor;
import org.groupfio.etl.process.util.CommonUtil;
import org.groupfio.etl.process.util.ResponseUtils;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.conversion.JSONConverters.JSONToMap;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;

/**
 * @author Sharif
 *
 */
public class JsonFileUploader implements FileUploader {

	private static String MODULE = JsonFileUploader.class.getName();

	@Override
	public Map<String, Object> upload(Map<String, Object> context) {

		Map<String, Object> response = new HashMap<String, Object>();
		
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		HttpServletRequest servletRequest = (HttpServletRequest) context.get("request");
		HttpServletResponse servletResponse = (HttpServletResponse) context.get("response");
		
		String fileName = (String) context.get("fileName");
		String tableName = (String) context.get("tableName");
		String listName = (String) context.get("listName");
		String groupId = (String) context.get("groupId");
		String serviceId = (String) context.get("serviceId");
		String existingFile = (String) context.get("existingFile");
		String isExport = (String) context.get("isExport");
		
		String processId = (String) context.get("processId");
		String modelName = (String) context.get("modelName");
		
		String errorMsg = "File Upload Filed, due to ";
		
		try {
			String defaultCountLimit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ETL_DFT_RCD_COUNT");
			
			String defaultFilePath = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/";
			String filePath = EntityUtilProperties.getPropertyValue("Etl-Process", "etl.files.location", defaultFilePath, delegator);
			File file = new File(filePath);
			if(!file.exists()){
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				response.put(EtlConstants.RESPONSE_MESSAGE,  "File Upload Failed, Due to [ "+file+" ] Location not found");
				return response;
			}
			
			InputStream inStream = null;
			OutputStream outStream = null;
			
			if (UtilValidate.isNotEmpty(filePath) && filePath != null) {

				File serverFile = new File(filePath + fileName);
				// to check file empty or not by m.vijayakumar
				if (serverFile.length() == 0) {
					
					response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					response.put(EtlConstants.RESPONSE_MESSAGE, errorMsg + "Please load XML file without empty values");
					
					return response;
				}
				
				File targetLocation = new File(filePath + CommonUtil.getFileExtension(fileName));
				if (!targetLocation.exists()) {
					targetLocation.mkdir();
				}
				UtilMisc.copyFile(serverFile, new File(targetLocation.getAbsolutePath() + File.separator + fileName));
				
				if (UtilValidate.isNotEmpty(processId)) {
					
					Map<String, Object> reqContext = new HashMap<String, Object>();
					
					reqContext.put("processId", processId);
					reqContext.put("modelName", modelName);
					reqContext.put("filePath", serverFile.getAbsolutePath());
					
					reqContext.put("delegator", delegator);
					reqContext.put("dispatcher", dispatcher);
					reqContext.put("userLogin", userLogin);
					
					ServiceExecutor serviceExecutor = new ServiceExecutor();
					
					serviceExecutor.setDelegator(delegator);
					serviceExecutor.setServletRequest(servletRequest);
					serviceExecutor.setServletResponse(servletResponse);
					serviceExecutor.setReqContext(reqContext);
					
					Map<String, Object> res = serviceExecutor.execute();
					
					if (res.get("requestAttribute") != null) {
						Map<String, Object> requestAttribute = (HashMap) res.get("requestAttribute");
						for (String attr : requestAttribute.keySet()) {
							servletRequest.setAttribute(attr, requestAttribute.get(attr));
						}
					}
					
					if (ResponseUtils.isError(res)) {
						
						Debug.logError(ResponseUtils.getResponseMessage(res), MODULE);
						
						response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						response.put(EtlConstants.RESPONSE_MESSAGE, ResponseUtils.getResponseMessage(res));
						
						return response;
					}
					
					response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
					return response;
				}
				
				List<String> fieldNames = new ArrayList<String>();

				JSON jsonFeed = JSON.from(FileUtil.readString("UTF-8", serverFile));
				
				JSONToMap jsonMap = new JSONToMap();
				Map<String, Object> dataMap = jsonMap.convert(jsonFeed);
				
				List<LinkedHashMap<String, Object>> entryList = (ArrayList) dataMap.get("entries");
				
				if (UtilValidate.isNotEmpty(entryList)) {
					Map<String, Object> entry = entryList.get(0);
					for (String key : entry.keySet()) {
						Debug.log(key);
						fieldNames.add(key);
					}
				}
				
				if (UtilValidate.isEmpty(fieldNames)) {
					
					response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					response.put(EtlConstants.RESPONSE_MESSAGE, errorMsg + "None of the Data Uploaded");
					
					return response;

				}
				
				// clean previous mapping elements
				if (UtilValidate.isNotEmpty(listName)) {
					delegator.removeByAnd("EtlMappingElements", UtilMisc.toMap("listName", listName));
				}

				// storing the file map process
				if (serverFile.isFile()) {

					// create model
					Set<String> fieldNamesGen = new LinkedHashSet<String>(fieldNames);
					GenericValue findModel = EntityUtil.getFirst(
							delegator.findByAnd("EtlModel", UtilMisc.toMap("modelName", listName), null, false));
					if (UtilValidate.isEmpty(findModel)) {
						GenericValue makeModel = delegator.makeValue("EtlModel");
						makeModel.put("modelId", delegator.getNextSeqId("EtlModel"));
						makeModel.put("modelName", listName);
						makeModel.put("tableName", tableName);
						makeModel.put("serviceName", serviceId);
						makeModel.put("groupId", groupId);
						makeModel.put("isExport", isExport);
						makeModel.create();
						
						if (UtilValidate.isNotEmpty(defaultCountLimit)) {
							GenericValue defValue = delegator.makeValue("EtlModelDefaults");
							defValue.put("propertyName", "recordCount");
							defValue.put("modelName", listName);
							defValue.put("propertyValue", defaultCountLimit);
							defValue.create();
						}
						
						// create model specific import folder
						String modelFolderName = makeModel.getString("modelId").concat("-").concat(makeModel.getString("modelName")).concat("-").concat(groupId);
						Debug.logInfo("modelFolderName> "+modelFolderName, MODULE);
						String importLocation = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.import.location");
						File targetImportLocation = new File(importLocation + modelFolderName);
						if (!targetImportLocation.exists()) {
							targetImportLocation.mkdir();
						}
						
					} else {
						findModel.put("modelName", listName);
						findModel.put("tableName", tableName);
						findModel.put("serviceName", serviceId);
						findModel.put("groupId", groupId);
						findModel.put("isExport", isExport);
						findModel.store();
					}
					
					// to avoid duplicate entry
					for (String field : fieldNamesGen) {
						GenericValue etlMappingElements = delegator.makeValue("EtlMappingElements");
						etlMappingElements.put("Id", delegator.getNextSeqId("EtlMappingElements"));
						etlMappingElements.put("listName", UtilValidate.isNotEmpty(listName) ? listName : "List");
						etlMappingElements.put("filePath", filePath);
						etlMappingElements.put("fileName", fileName);
						etlMappingElements.put("etlFieldName", field);

						// list always come only once
						List<GenericValue> EtlMappingElementsExist = delegator.findByAnd("EtlMappingElements",
								UtilMisc.toMap("listName", listName, "etlFieldName", field), null, false);

						if (UtilValidate.isEmpty(EtlMappingElementsExist)) {
							delegator.create(etlMappingElements);
						}
					}

				} else {
					
					response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					response.put(EtlConstants.RESPONSE_MESSAGE, errorMsg);
					
					return response;

				}

			}
			
		} catch (Exception e) {
			Debug.logError("Exception in json file upload: "+e.getMessage(), MODULE);
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "File Upload Failed");
			
			return response;
		}
		
		response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;
	}
	
}
