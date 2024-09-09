package org.groupfio.etl.process.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.groupfio.etl.process.uploader.FileUploader;
import org.groupfio.etl.process.uploader.FileUploaderFactory;
import org.groupfio.etl.process.util.CommonUtil;
import org.groupfio.etl.process.util.ResponseUtils;
import org.groupfio.etl.process.writer.WriterUtil;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
/**
 * @author Group Fio
 *
 */
public class ETLFileUpload {
	
	private static String MODULE = ETLFileUpload.class.getName();
	
	public static String uploadFile(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		HttpSession session = request.getSession(true);
		
		String fileName = (String) request.getParameter("csvFile");
		String tableName = (String) request.getParameter("tableName");
		String listName = (String) request.getParameter("listName");
		String groupId = (String) request.getParameter("groupId");
		String serviceId = (String) request.getParameter("serviceId");
		String existingFile = (String) request.getParameter("existingFile");
		String isExport = (String) request.getParameter("isExport");
		
		String modelName = request.getParameter("modelName");
		String processId = request.getParameter("processId");
		String selectedModelName = request.getParameter("selectedModelName");
		String delimiter = request.getParameter("delimiter");
		
		String customSuccessMessage = (String) request.getParameter("customSuccessMessage");
		
		String line = "";
		String cvsSplitBy = ",";
		BufferedReader br = null;
		String name = "";
		String errorMsg = "File Upload Filed, due to ";
		int i = 0;
		
		String successMessage = "success";
		String errorMessage = "error";

		try {
			
			String defaultFilePath = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/";
			
			//M.Vijayakumar desc: For the purpose of changing the file location for dynamically.
			String filePath = EntityUtilProperties.getPropertyValue("Etl-Process", "etl.files.location", defaultFilePath, delegator);
			File file = new File(filePath);
			if(!file.exists()){
				request.setAttribute("_ERROR_MESSAGE_", "File Upload Failed, Due to [ "+file+" ] Location not found");
				return errorMessage;
			}
			//end @vijayakumar
			
			if (UtilValidate.isNotEmpty(modelName)) {
				GenericValue modelDefault = EntityUtil.getFirst( delegator.findByAnd("EtlModelDefaults", UtilMisc.toMap("modelName", modelName, "propertyName", "FILE_PREFIX"), null, false) );
				if (UtilValidate.isNotEmpty(modelDefault)) {
					fileName = modelDefault.getString("propertyValue")+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
				}
			}
			
			if (UtilValidate.isNotEmpty(modelName)) {
				successMessage = "success-apply";
				errorMessage = "error-apply";
			}
			
			InputStream inStream = null;
			OutputStream outStream = null;
			
			// getting file from the location
			if (request != null && ServletFileUpload.isMultipartContent(request)) {
				@SuppressWarnings("unchecked")
				List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

				for (FileItem item : multiparts) {
					if (!item.isFormField()) {
						
						request.setAttribute("processId", processId);
						request.setAttribute("modelName", modelName);
						Debug.log("Name from Item"+item.getName()+" ");
						//name = new File(item.getName()).getAbsolutePath();
						name = item.getName();
						Debug.log("============name 94=============="+name);
						Debug.log("============existingFile 95=============="+existingFile);
						Debug.log("Name from Item"+item.getName()+" name:"+name);
						if (UtilValidate.isEmpty(name) && "Y".equals(existingFile)) {
							name = (String) session.getAttribute("fileName");
							if (UtilValidate.isNotEmpty(selectedModelName)) {
								GenericValue mappedElememnt = EntityUtil.getFirst( delegator.findByAnd("EtlMappingElements", UtilMisc.toMap("listName", selectedModelName), null, false) );
								if (UtilValidate.isNotEmpty(mappedElememnt) && UtilValidate.isNotEmpty(mappedElememnt.getString("fileName"))) {
									name = mappedElememnt.getString("fileName");
								} 
								if (UtilValidate.isNotEmpty(mappedElememnt)) {
									GenericValue etlModel = EntityUtil.getFirst( delegator.findByAnd("EtlModel", UtilMisc.toMap("modelName", selectedModelName), null, false) );
									if (UtilValidate.isNotEmpty(etlModel)) {
										groupId = etlModel.getString("groupId");
									}
								}
							}
						}
						
						/*if (UtilValidate.isNotEmpty(name)) {
							fileName = name;
						}*/
						
						Debug.log("============name 116=============="+name);
						String extention = name.substring(name.indexOf('.'));
						Debug.log("============extention 118=============="+extention);
						if (UtilValidate.isEmpty(extention)) {
							errorMsg = errorMsg + " unavailable of extension";
							request.setAttribute("_ERROR_MESSAGE_", errorMsg);
							return errorMessage;
						}
						
						String uploadMaxFileSize = EntityUtilProperties.getPropertyValue("crm", "DBS_UPLOAD_MAX_FILE_SIZE", "", delegator);
						if (UtilValidate.isNotEmpty(uploadMaxFileSize)) {
							Double maxFileSize = Double.parseDouble(uploadMaxFileSize);
							Double fileSize = new Double( item.getSize() ) / 1024;
							if (fileSize > maxFileSize) {
								errorMsg = "Uploaded file size: " + Math.round(fileSize) + "KB, " + "File size should not exceed more than " + uploadMaxFileSize + "KB";
								request.setAttribute("_ERROR_MESSAGE_", errorMsg);
								return errorMessage;
							}
						}
						
						if (UtilValidate.isNotEmpty(fileName)) {
							name = fileName + extention;
						}
						
						fileName = name;
						
						//name = importFileName + extention;
						
						Debug.log("============existingFile=============="+existingFile);
						if ("Y".equals(existingFile)) {
							Debug.log("============if filePath1=============="+filePath);
							item.write(new File( filePath + File.separator + CommonUtil.getFileExtension(fileName) + File.separator + name));
							Debug.log("============if filePath2=============="+filePath);
						} else {
							Debug.log("============else filePath=============="+filePath);
							Debug.log("============else File.separator=============="+File.separator);
							Debug.log("============else name=============="+name);
							item.write(new File(filePath + File.separator + name));
							Debug.log("============else filePath2=============="+filePath + File.separator + name);
						}
						Debug.log("=============Copied file path ============="+new File(filePath + File.separator + name).getAbsolutePath()+" "+new File(filePath + File.separator + name).getCanonicalPath());
						Map<String, Object> context = new HashMap<String, Object>();
						
						context.put("fileName", fileName);
						context.put("tableName", tableName);
						context.put("listName", listName);
						context.put("existingFile", existingFile);
						context.put("serviceId", serviceId);
						context.put("groupId", groupId);
						context.put("isExport", isExport);
						context.put("delimiter", delimiter);
						
						context.put("modelName", modelName);
						context.put("processId", processId);
						
						context.put("delegator", delegator);
						context.put("dispatcher", dispatcher);
						context.put("userLogin", userLogin);
						context.put("request", request);
						context.put("response", response);
						
						if (!extention.equals(".csv") && !extention.equals(".xml") 
								&& !extention.equals(".json") 
								//&& !extention.equals(".xls") // TODO need to enable xlsx file import 
								//&& !extention.equals(".xlsx") // TODO need to enable xlsx file import 
								&& !extention.equals(".txt") && !extention.equals(".dat")) {
							request.setAttribute("_ERROR_MESSAGE_", "Uploading the wrong file format. Please upload correct file format (Pick the right import file (CSV, text, XML, JSON) )");
							Debug.logError("File Upload Failed...!", MODULE);
							return errorMessage;
						}
						
						if (extention.equals(".csv")) {
							
							FileUploader fileUploader = FileUploaderFactory.getCsvFileUploader();
							Map<String, Object> res = fileUploader.upload(context);
							
							if (ResponseUtils.isError(res)) {
								request.setAttribute("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(res));
								Debug.logError(ResponseUtils.getResponseMessage(res), MODULE);
								return errorMessage;
							}
							
						} else if (extention.equals(".xml")) {
							
							FileUploader fileUploader = FileUploaderFactory.getXmlFileUploader();
							Map<String, Object> res = fileUploader.upload(context);
							
							if (ResponseUtils.isError(res)) {
								request.setAttribute("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(res));
								Debug.logError(ResponseUtils.getResponseMessage(res), MODULE);
								return errorMessage;
							}
							
						} else if (extention.equals(".json")) {
							
							FileUploader fileUploader = FileUploaderFactory.getJsonFileUploader();
							Map<String, Object> res = fileUploader.upload(context);
							
							if (ResponseUtils.isError(res)) {
								request.setAttribute("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(res));
								Debug.logError(ResponseUtils.getResponseMessage(res), MODULE);
								return errorMessage;
							}
							
						} else if (extention.equals(".xls")) {
							
							FileUploader fileUploader = FileUploaderFactory.getExcelFileUploader();
							Map<String, Object> res = fileUploader.upload(context);
							
							if (ResponseUtils.isError(res)) {
								request.setAttribute("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(res));
								Debug.logError(ResponseUtils.getResponseMessage(res), MODULE);
								return errorMessage;
							}
							
						} else if (extention.equals(".xlsx")) {
							
							FileUploader fileUploader = FileUploaderFactory.getExcelXlsxFileUploader();
							Map<String, Object> res = fileUploader.upload(context);
							
							if (ResponseUtils.isError(res)) {
								request.setAttribute("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(res));
								Debug.logError(ResponseUtils.getResponseMessage(res), MODULE);
								return errorMessage;
							}
							
						} else if (extention.equals(".txt") || extention.equals(".dat")) {
							
							FileUploader fileUploader = FileUploaderFactory.getTextFileUploader();
							Map<String, Object> res = fileUploader.upload(context);
							
							if (ResponseUtils.isError(res)) {
								request.setAttribute("_ERROR_MESSAGE_", ResponseUtils.getResponseMessage(res));
								Debug.logError(ResponseUtils.getResponseMessage(res), MODULE);
								WriterUtil.writeLog(delegator, "WALLET", ResponseUtils.getResponseMessage(res), "DataImportWallet", modelName);
								return errorMessage;
							}
							
						}
						
					}
					
					if (item.isFormField()) {
						String fName = item.getFieldName();
						String fValue = item.getString();
						if ("csvFile".equals(fName)) {
							fileName = fValue;
						} else if ("tableName".equals(fName)) {
							tableName = fValue;
						} else if ("csvListName".equals(fName)) {
							listName = fValue;
						} else if ("existingFile".equals(fName)) {
							existingFile = fValue;
						} else if ("serviceId".equals(fName)) {
							serviceId = fValue;
						} else if ("groupId".equals(fName)) {
							groupId = fValue;
						} else if ("isExport".equals(fName)) {
							isExport = fValue;
						} else if("processId".equals(fName) && UtilValidate.isNotEmpty(fValue)){
							processId = fValue;
							session.setAttribute("processId",processId);
						} else if ("selectedModelName".equals(fName)) {
							selectedModelName = fValue;
						} else if ("delimiter".equals(fName)) {
							delimiter = fValue;
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", "File Upload Failed"+e.getMessage());
			Debug.logError("File Upload Failed...!", MODULE);
			WriterUtil.writeLog(delegator, "WALLET", "Fail upload failed. Please see the Error logs section", "DataImportWallet", modelName);
			return errorMessage;
		}
		
		session.setAttribute("fileName", name);
		
		request.setAttribute("processId", processId);
		request.setAttribute("modelName", modelName);
		
		request.setAttribute("fileName", name);
		request.setAttribute("listName", listName);
		
		if (UtilValidate.isNotEmpty(modelName) && modelName.equals("Lead ModelA")) {
			request.setAttribute("_EVENT_MESSAGE_", "File Uploaded, Kindly review the error logs section for the Errors records and Find lead screen for the successfully imported records");
		} else {
			request.setAttribute("_EVENT_MESSAGE_", "File Imported Successfully");
		}
		
		return successMessage;
	}
	
}
