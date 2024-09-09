package org.etlprocess.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import au.com.bytecode.opencsv.CSVReader;

public class ETLFileUpload {

	private static String MODULE = ETLFileUpload.class.getName();
	
	private static String  errorLog;
	private static String currentListId = "";
	public static Map<String, Object> uploadCSVFile1(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();
		String fileName = (String) context.get("csvFile");
		String tableName = (String) context.get("tableName");
		String listName=(String) context.get("listName");
		String line = "";
		String cvsSplitBy = ",";
		BufferedReader br=null;

		String fileNamess = getUploadPath()+fileName;

		try {

			//getting file from the location


			String filePath = null;
			GenericValue tenantProperties = delegator.findOne("TenantProperties",UtilMisc.toMap("resourceName","etl-process","propertyName","etl.file.path"),false);
			if(UtilValidate.isNotEmpty(tenantProperties))
			{
				filePath = tenantProperties.getString("propertyValue");
			}
			if(UtilValidate.isNotEmpty(filePath)&& filePath!=null)
			{	

				File serverFile = new File(filePath+fileName);
				List<String> fieldNames = new ArrayList<String>();
				br = new BufferedReader(new FileReader(serverFile));
				while ((line = br.readLine()) != null) {

					// use comma as separator
					String[] tokens = line.split(cvsSplitBy);
					for(String token : tokens)
					{
						fieldNames.add(token);
					}

				}

				//storing the file map process
				if(serverFile.isFile())
				{
					for(String field:fieldNames)
					{
						GenericValue etlMappingElements = delegator.makeValue("EtlMappingElements");
						etlMappingElements.put("Id",delegator.getNextSeqId("EtlMappingElements"));
						etlMappingElements.put("listName",UtilValidate.isNotEmpty(listName)?listName:"List");
						etlMappingElements.put("filePath",filePath);
						etlMappingElements.put("fileName", fileName);
						etlMappingElements.put("etlFieldName",field);

						//list always come only once
						List entityList = UtilMisc.toList(new EntityExpr("listName", EntityOperator.EQUALS, listName),new EntityExpr("etlFieldName", EntityOperator.EQUALS, field));
						EntityConditionList conditionList = new EntityConditionList( entityList,EntityOperator.AND);
						List<GenericValue> EtlMappingElementsExist = delegator.findByAnd("EtlMappingElements", UtilMisc.toMap("listName",listName,"etlFieldName",field),null,false);

						if(UtilValidate.isEmpty(EtlMappingElementsExist))
						{
							delegator.create(etlMappingElements);
						}else
						{
							delegator.removeAll(EtlMappingElementsExist);
							delegator.create(etlMappingElements);
						}

					}

				}else
				{
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg"));
				}

			}



		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			Debug.logError(e.getMessage(),MODULE);
			return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg"));
		}
		finally {
			try { 
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
			}
	}

		return ServiceUtil.returnSuccess(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg1"));
	}
	@SuppressWarnings("resource")
	public static String  uploadCSVFile(HttpServletRequest request,HttpServletResponse response) throws ComponentException
	{
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		HttpSession session = request.getSession(true);
		//String filePath = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.files.location");
		String filePath = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/";
		String filePath1 = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/csv/";
		//String filePath1 = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.files.location1");
		String fileName = (String) request.getParameter("csvFile");
		String tableName = (String) request.getParameter("tableName");
		String listName=(String) request.getParameter("listName");
		String groupId=(String) request.getParameter("groupId");
		String serviceId=(String) request.getParameter("serviceId");
		String existingFile=(String) request.getParameter("existingFile");
		String isExport=(String) request.getParameter("isExport");
		String line = "";
		String cvsSplitBy = ",";
		BufferedReader br=null;
		String name="";
		String errorMsg = "File Uploaded Filed, due to ";
		int i=0;

		try {
			InputStream inStream = null;
			OutputStream outStream = null;
			//getting file from the location
			if(ServletFileUpload.isMultipartContent(request)){
				@SuppressWarnings("unchecked")
				List<FileItem> multiparts = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);

				for(FileItem item : multiparts){
					if(!item.isFormField()){
						name = new File(item.getName()).getName();
						if(UtilValidate.isEmpty(name) && "Y".equals(existingFile))
							name = (String) session.getAttribute("fileName");
						//added by m.vijayakumar for indication message of the file  date:31/03/2016
						String extention = name.substring(name.indexOf('.'));
						if(UtilValidate.isEmpty(extention))
						{
							errorMsg = errorMsg+UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg2");
							request.setAttribute("_ERROR_MESSAGE_",errorMsg);
							return "error";
						}else
						{
							if(!extention.equals(".csv"))
							{
								errorMsg = errorMsg+" unsuported "+extention+" format";
								request.setAttribute("_ERROR_MESSAGE_",errorMsg);
								return "error";
							}
						}
						//end @vijayakumar
						if("Y".equals(existingFile))
							item.write( new File(filePath1 + File.separator + name));
						else
							item.write( new File(filePath + File.separator + name));
					}
					if (item.isFormField()) {
						String fName = item.getFieldName();
						String fValue = item.getString();	
						if("csvFile".equals(fName)){
							fileName = fValue;
						}else if("tableName".equals(fName)){
							tableName = fValue;
						}else if("csvListName".equals(fName)){
							listName = fValue;
						}else if("existingFile".equals(fName)){
							existingFile = fValue;
						}else if("serviceId".equals(fName)){
							serviceId = fValue;
						}else if("groupId".equals(fName)){
							groupId = fValue;
						}else if("isExport".equals(fName)){
							isExport = fValue;
						}
					}
				}
			}
			if(UtilValidate.isNotEmpty(filePath)&& filePath!=null)
			{	

				File serverFile = new File(filePath+name);
				//to check file empty or not by m.vijayakumar
				if(serverFile.length()==0)
				{
					request.setAttribute("_ERROR_MESSAGE_",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg5"));
					return "error";
				}
				//end @vijayakumar
				List<String> fieldNames = new ArrayList<String>();
				CSVReader reader = new CSVReader(new FileReader(filePath+name));
				String [] nextLine;
				int j=0;
				while ((nextLine = reader.readNext()) != null) {
					if(j>0) break;
					for(i=0;i<nextLine.length; i++){
						fieldNames.add(nextLine[i]);
					}
					j++;

				}

				inStream = new FileInputStream(serverFile);
				outStream = new FileOutputStream(filePath1+name);

				byte[] buffer = new byte[1024];

				int length;
				//copy the file content in bytes 
				while ((length = inStream.read(buffer)) > 0){

					outStream.write(buffer, 0, length);

				}

				inStream.close();
				outStream.close();
				reader.close();
				if(UtilValidate.isEmpty(fieldNames))
				{
					request.setAttribute("_ERROR_MESSAGE_",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg3"));
					return "error";

				}

				//storing the file map process
				if(serverFile.isFile())
				{

					//remove all the fields from the table based on the list name
					/*List<GenericValue> existingEtlMapping  = delegator.findByCondition("EtlMappingElements", new EntityExpr("listName",EntityOperator.EQUALS,listName),null ,null);
					if(UtilValidate.isNotEmpty(existingEtlMapping))
					{
						delegator.removeAll(existingEtlMapping);
					}*/


					//create model
					Set<String> fieldNamesGen = new LinkedHashSet<String>(fieldNames);
					GenericValue findModel = EntityUtil.getFirst(delegator.findByAnd("EtlModel",UtilMisc.toMap("modelName",listName),null,false));
					if(UtilValidate.isEmpty(findModel)){
						GenericValue makeModel = delegator.makeValue("EtlModel");
						makeModel.put("modelId", delegator.getNextSeqId("EtlModel"));
						makeModel.put("modelName", listName);
						makeModel.put("tableName", tableName);
						makeModel.put("serviceName", serviceId);
						makeModel.put("groupId", groupId);
						makeModel.put("isExport", isExport);
						makeModel.create();
					}else{
						findModel.put("modelName", listName);
						findModel.put("tableName", tableName);
						findModel.put("serviceName", serviceId);
						findModel.put("groupId", groupId);
						findModel.put("isExport", isExport);
						findModel.store();
					}
					//to avoid duplicate entry
					for(String field:fieldNamesGen)
					{
						GenericValue etlMappingElements = delegator.makeValue("EtlMappingElements");
						etlMappingElements.put("Id",delegator.getNextSeqId("EtlMappingElements"));
						etlMappingElements.put("listName",UtilValidate.isNotEmpty(listName)?listName:"List");
						etlMappingElements.put("filePath",filePath);
						etlMappingElements.put("fileName", fileName);
						etlMappingElements.put("etlFieldName",field);

						//list always come only once
						List entityList = UtilMisc.toList(new EntityExpr("listName", EntityOperator.EQUALS, listName),new EntityExpr("etlFieldName", EntityOperator.EQUALS, field));
						EntityConditionList conditionList = new EntityConditionList( entityList,EntityOperator.AND);
						List<GenericValue> EtlMappingElementsExist = delegator.findByAnd("EtlMappingElements", UtilMisc.toMap("listName",listName,"etlFieldName",field), null	, false);

						if(UtilValidate.isEmpty(EtlMappingElementsExist))
						{
							delegator.create(etlMappingElements);
						}else
						{
							//delegator.removeAll(EtlMappingElementsExist);
							//delegator.create(etlMappingElements);
						}
						//delegator.create(etlMappingElements);
					}

				}else
				{
					request.setAttribute("_ERROR_MESSAGE_",errorMsg);
					return "error";

				}

			}



		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			Debug.logError(e.getMessage(),MODULE);
			request.setAttribute("_ERROR_MESSAGE_",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg"));
			return "error";
		}
		session.setAttribute("fileName", name);
		request.setAttribute("fileName", name);
		request.setAttribute("listName",listName);
		request.setAttribute("_EVENT_MESSAGE_",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg1"));
		return "success";
	}	

	/**
	 * Gets the path for uploaded files.
	 * @return a <code>String</code> value
	 */
	public static String getUploadPath() {
		return System.getProperty("user.dir") + File.separatorChar + "runtime" + File.separatorChar + "data" + File.separatorChar;
	}
	

	public static String createEtlProcessService(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String processId = request.getParameter("processId");
		String processName = request.getParameter("processName");
		String description = request.getParameter("description");
		String etlProcessService = request.getParameter("etlProcessService");
		try{
			if(UtilValidate.isNotEmpty(processId)){
				GenericValue checkId = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",processId),false);
				if(UtilValidate.isNotEmpty(checkId)){
					request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg4"));
					return "error";

				}
				GenericValue makeId = delegator.makeValue("EtlProcess");
				makeId.put("processId",processId);
				makeId.put("processName", processName);
				makeId.put("serviceName", etlProcessService);
				makeId.put("description", description);
				makeId.create();
				request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg5"));
			}

		}catch(Exception e){
			Debug.log("+++++++++++++uploadEtlFileService+++++++++++++"+e.toString());
		}
		return "success";
	}
	
	public static Map<String, Object>CreateEtlCustomerLoadService(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();
		currentListId = (String)context.get("listId");
		org.etlprocess.service.EtlImportServices.currentListId = currentListId;
		try {
					
			GenericValue dataImportCustomer=delegator.makeValue("DataImportCustomer");
			dataImportCustomer.put("batchId", context.get("batchId"));
			if(UtilValidate.isNotEmpty((String) context.get("customerId")))
            dataImportCustomer.put("customerId", (String) context.get("customerId"));				
			else{
				boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg6"),"DataImportCustomer");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				}
			 if(UtilValidate.isNotEmpty((String) context.get("companyName")))
            dataImportCustomer.put("companyName", (String) context.get("companyName"));
			 if(UtilValidate.isNotEmpty((String) context.get("firstName")))
            dataImportCustomer.put("firstName", (String) context.get("firstName"));
			 if(UtilValidate.isNotEmpty((String) context.get("lastName")))
            dataImportCustomer.put("lastName", (String) context.get("lastName"));
			 if(UtilValidate.isNotEmpty((String) context.get("attnName")))
            dataImportCustomer.put("attnName", (String) context.get("attnName"));
			 if(UtilValidate.isNotEmpty((String) context.get("address1")))
            dataImportCustomer.put("address1", (String) context.get("address1"));
			 if(UtilValidate.isNotEmpty((String) context.get("address2")))
            dataImportCustomer.put("address2",(String) context.get("address2"));
            dataImportCustomer.put("city", (String) context.get("city"));       
            
            
            if(UtilValidate.isNotEmpty((String) context.get("countryGeoId"))){
            	String contgeoid = (String) context.get("countryGeoId");
			if(UtilValidate.isNotEmpty((String) context.get("stateProvinceGeoId")) || contgeoid.equals("SGP")){
				if(contgeoid.equals("SGP")){
					 dataImportCustomer.put("stateProvinceGeoId", "_NA_");
				}
				else{
	            GenericValue geo = EntityQuery.use(delegator).from("Geo").where("geoId", (String) context.get("stateProvinceGeoId"),"geoTypeId","STATE").cache().queryOne();
				if(UtilValidate.isNotEmpty(geo))
		            dataImportCustomer.put("stateProvinceGeoId", (String) context.get("stateProvinceGeoId"));	            	
	            else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdError"),"DataImportCustomer");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
	            }
			 }
			}
			else{
   				boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdEmptyError"),"DataImportCustomer");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
   			}
            }
            else{
            	boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCountryIdEmptyError"),"DataImportCustomer");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
            }
            
            
			 if(UtilValidate.isNotEmpty((String) context.get("postalCode")))
            dataImportCustomer.put("postalCode", (String) context.get("postalCode"));
			 if(UtilValidate.isNotEmpty((String) context.get("postalCodeExt")))
            dataImportCustomer.put("postalCodeExt", (String) context.get("postalCodeExt"));
            
            if(UtilValidate.isNotEmpty((String) context.get("countryGeoId"))){
	            GenericValue geo = EntityQuery.use(delegator).from("Geo").where("geoId", (String) context.get("countryGeoId"),"geoTypeId","COUNTRY").cache().queryOne();
	            if(UtilValidate.isNotEmpty(geo))
	            	dataImportCustomer.put("countryGeoId", (String) context.get("countryGeoId"));	            	
	            else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCountryIdError"),"DataImportCustomer");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
	            }
			 }
   			else{
   				boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCountryIdEmptyError"),"DataImportCustomer");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
   			}
            if(UtilValidate.isNotEmpty((String) context.get("primaryPhoneCountryCode")))
            dataImportCustomer.put("primaryPhoneCountryCode", (String) context.get("primaryPhoneCountryCode"));
            if(UtilValidate.isNotEmpty((String) context.get("primaryPhoneAreaCode")))
            dataImportCustomer.put("primaryPhoneAreaCode", (String) context.get("primaryPhoneAreaCode"));
            if(UtilValidate.isNotEmpty((String) context.get("primaryPhoneNumber")))
            dataImportCustomer.put("primaryPhoneNumber", (String) context.get("primaryPhoneNumber"));
            if(UtilValidate.isNotEmpty((String) context.get("primaryPhoneExtension")))
            dataImportCustomer.put("primaryPhoneExtension", (String) context.get("primaryPhoneExtension"));
            if(UtilValidate.isNotEmpty((String) context.get("secondaryPhoneCountryCode")))
            dataImportCustomer.put("secondaryPhoneCountryCode", (String) context.get("secondaryPhoneCountryCode")); 
            if(UtilValidate.isNotEmpty((String) context.get("secondaryPhoneAreaCode")))
            dataImportCustomer.put("secondaryPhoneAreaCode", (String) context.get("secondaryPhoneAreaCode"));
            if(UtilValidate.isNotEmpty((String) context.get("secondaryPhoneNumber")))
            dataImportCustomer.put("secondaryPhoneNumber", (String) context.get("secondaryPhoneNumber"));
            if(UtilValidate.isNotEmpty((String) context.get("secondaryPhoneNumber")))
            dataImportCustomer.put("secondaryPhoneExtension", (String) context.get("secondaryPhoneExtension"));
            if(UtilValidate.isNotEmpty((String) context.get("faxCountryCode")))
            dataImportCustomer.put("faxCountryCode", (String) context.get("faxCountryCode"));
            if(UtilValidate.isNotEmpty((String) context.get("faxAreaCode")))
            dataImportCustomer.put("faxAreaCode", (String) context.get("faxAreaCode"));
            if(UtilValidate.isNotEmpty((String) context.get("faxNumber")))
            dataImportCustomer.put("faxNumber", (String) context.get("faxNumber"));
            if(UtilValidate.isNotEmpty((String) context.get("didCountryCode")))
            dataImportCustomer.put("didCountryCode", (String) context.get("didCountryCode"));
            if(UtilValidate.isNotEmpty((String) context.get("didAreaCode")))
            dataImportCustomer.put("didAreaCode", (String) context.get("didAreaCode"));
            if(UtilValidate.isNotEmpty((String) context.get("didNumber")))
            dataImportCustomer.put("didNumber", (String) context.get("didNumber"));
            if(UtilValidate.isNotEmpty((String) context.get("didExtension")))
            dataImportCustomer.put("didExtension", (String) context.get("didExtension"));
            
            if(UtilValidate.isNotEmpty((String) context.get("emailAddress")))
            dataImportCustomer.put("emailAddress", (String) context.get("emailAddress"));    
            if(UtilValidate.isNotEmpty((String) context.get("webAddress")))
            dataImportCustomer.put("webAddress", (String) context.get("webAddress"));
            if(UtilValidate.isNotEmpty((String) context.get("discount"))){
   				BigDecimal discount = new BigDecimal((String) context.get("discount"));
   				dataImportCustomer.put("discount", discount);
   			}         
            if(UtilValidate.isNotEmpty((String) context.get("partyClassificationTypeId")))
            dataImportCustomer.put("partyClassificationTypeId", (String) context.get("partyClassificationTypeId"));
            if(UtilValidate.isNotEmpty((String) context.get("creditCardNumber")))
            dataImportCustomer.put("creditCardNumber", (String) context.get("creditCardNumber"));
            if(UtilValidate.isNotEmpty((String) context.get("creditCardExpDate")))
            dataImportCustomer.put("creditCardExpDate", (String) context.get("creditCardExpDate"));
            if(UtilValidate.isNotEmpty((String) context.get("outstandingBalance"))){
   				BigDecimal outstandingBalance = new BigDecimal((String) context.get("outstandingBalance"));
   				dataImportCustomer.put("outstandingBalance", outstandingBalance);
   			}
            if(UtilValidate.isNotEmpty((String) context.get("creditLimit"))){
   				BigDecimal creditLimit = new BigDecimal((String) context.get("creditLimit"));
   				dataImportCustomer.put("creditLimit", creditLimit);
   			}
            if(UtilValidate.isNotEmpty((String) context.get("currencyUomId")))
            dataImportCustomer.put("currencyUomId", (String) context.get("currencyUomId"));
            if(UtilValidate.isNotEmpty((String) context.get("disableShipping")))
            dataImportCustomer.put("disableShipping", (String) context.get("disableShipping"));
            if(UtilValidate.isNotEmpty((String) context.get("netPaymentDays")))
            dataImportCustomer.put("netPaymentDays", Long.parseLong((String) context.get("netPaymentDays")));
            if(UtilValidate.isNotEmpty((String) context.get("shipToCompanyName")))
            dataImportCustomer.put("shipToCompanyName", (String) context.get("shipToCompanyName"));
            if(UtilValidate.isNotEmpty((String) context.get("shipToFirstName")))
            dataImportCustomer.put("shipToFirstName", (String) context.get("shipToFirstName"));
            if(UtilValidate.isNotEmpty((String) context.get("shipToLastName")))
            dataImportCustomer.put("shipToLastName", (String) context.get("shipToLastName"));
            if(UtilValidate.isNotEmpty((String) context.get("shipToAttnName")))
            dataImportCustomer.put("shipToAttnName", (String) context.get("shipToAttnName"));
            if(UtilValidate.isNotEmpty((String) context.get("shipToAddress1")))
            dataImportCustomer.put("shipToAddress1", (String) context.get("shipToAddress1"));
            if(UtilValidate.isNotEmpty((String) context.get("shipToAddress2")))
            dataImportCustomer.put("shipToAddress2", (String) context.get("shipToAddress2"));
            if(UtilValidate.isNotEmpty((String) context.get("shipToCity")))
            dataImportCustomer.put("shipToCity", (String) context.get("shipToCity"));
            
            /*if(UtilValidate.isNotEmpty((String) context.get("shipToStateProvinceGeoId"))){
	            GenericValue geo = EntityQuery.use(delegator).from("Geo").where("geoId", (String) context.get("shipToStateProvinceGeoId"),"geoTypeId","STATE").cache().queryOne();
	            if(UtilValidate.isNotEmpty(geo))
	            	 dataImportCustomer.put("shipToStateProvinceGeoId", (String) context.get("shipToStateProvinceGeoId"));           	
	            else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceShipStateIdError"),"DataImportCustomer");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
	            }
			 }  */  
            
            if(UtilValidate.isNotEmpty((String) context.get("shipToCountryGeoId"))){
            	String shipcontgeoid = (String) context.get("shipToCountryGeoId");
    			if(UtilValidate.isNotEmpty((String) context.get("shipToStateProvinceGeoId")) || shipcontgeoid.equals("SGP")){    				
    				if(shipcontgeoid.equals("SGP")){
    					 dataImportCustomer.put("shipToStateProvinceGeoId", "_NA_");
    				}
    				else{
    	            GenericValue geo = EntityQuery.use(delegator).from("Geo").where("geoId", (String) context.get("shipToStateProvinceGeoId"),"geoTypeId","STATE").cache().queryOne();
    				if(UtilValidate.isNotEmpty(geo))
    					dataImportCustomer.put("shipToStateProvinceGeoId", (String) context.get("shipToStateProvinceGeoId"));	            	
    	            else{
    					boolean makeError = false;
    					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceshipStateIdEmptyError1"),"DataImportCustomer");
    					if(makeError)
    						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
    	            }
    			 }
    			}
    			else{
       				boolean makeError = false;
    				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdEmptyError"),"DataImportCustomer");
    				if(makeError)
    					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
       			}
                }
                else{
                	boolean makeError = false;
    				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCountryIdEmptyError"),"DataImportCustomer");
    				if(makeError)
    					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
                }
            
            if(UtilValidate.isNotEmpty((String) context.get("shipToPostalCode")))
            dataImportCustomer.put("shipToPostalCode", (String) context.get("shipToPostalCode"));
            if(UtilValidate.isNotEmpty((String) context.get("shipToPostalCodeExt")))
            dataImportCustomer.put("shipToPostalCodeExt", (String) context.get("shipToPostalCodeExt"));
            if(UtilValidate.isNotEmpty((String) context.get("shipToStateProvGeoName")))
            dataImportCustomer.put("shipToStateProvGeoName", (String) context.get("shipToStateProvGeoName"));

            if(UtilValidate.isNotEmpty((String) context.get("shipToCountryGeoId"))){
	            GenericValue geo = EntityQuery.use(delegator).from("Geo").where("geoId", (String) context.get("shipToCountryGeoId"),"geoTypeId","COUNTRY").cache().queryOne();
	            if(UtilValidate.isNotEmpty(geo))
	            	 dataImportCustomer.put("shipToCountryGeoId", (String) context.get("shipToCountryGeoId"));	            	
	            else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceshipCountryIdError"),"DataImportCustomer");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
	            }
			 }
            	else{
       				boolean makeError = false;
    				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceshipCountryIdEmptyError"),"DataImportCustomer");
    				if(makeError)
    					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
       			}
            
           
            if(UtilValidate.isNotEmpty((String) context.get("note")))
            dataImportCustomer.put("note", (String) context.get("note"));
            
            if(UtilValidate.isNotEmpty((String) context.get("source"))){
            	GenericValue partyIdentificationType = EntityQuery.use(delegator).from("PartyIdentificationType").where("partyIdentificationTypeId", (String) context.get("source")).cache().queryOne();
                if(UtilValidate.isNotEmpty(partyIdentificationType))
            	dataImportCustomer.put("source", (String) context.get("source"));
                else{
                	boolean makeError = false;
    				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicepartyIdentificationTypeIdEmptyError"),"DataImportCustomer");
    				if(makeError)
    					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
                }
            }
            else
            {
            	boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CUSTOMER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceinvalidPartyIdentificationTypeIdError"),"DataImportCustomer");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
            }
                       
            TransactionUtil.begin(20000);
				delegator.create(dataImportCustomer);
			TransactionUtil.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			Debug.logError(e.getMessage(),MODULE);
			return ServiceUtil.returnSuccess();
		}


		return ServiceUtil.returnSuccess();
	}
	

	public static String uploadEtlFileService(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String processId = request.getParameter("processId");

		try{

			if(UtilValidate.isNotEmpty(processId)){
				//String table = "DmgPartyCustomer";
				GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess",UtilMisc.toMap("modalName",processId),null,false));
				String process=""; String table = ""; String model="";
				String serviceName="";
				if(UtilValidate.isNotEmpty(checkProcess)){
					process = checkProcess.getString("processId");
					table = checkProcess.getString("tableName");
					serviceName = checkProcess.getString("serviceName");
			        /*if(UtilValidate.isEmpty(serviceName) && "EtlImportOrderFields".equals(table)){
			            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg7"));
			            return "error";
			        }*/
					GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
					if(UtilValidate.isNotEmpty(checkUploadRequest)){
						request.setAttribute("execute","lock");
						request.setAttribute("model", processId);
						//return "success";
					}
					if("DataImportSupplier".equals(table)){
						org.etlprocess.service.EtlImportServices.uploadSupplierLoadService(request,response,process);
					}else if("DataImportCustomer".equals(table)){
						org.etlprocess.service.EtlImportServices.uploadCustomerLoadService(request,response,process);
					}else if("DataImportAccount".equals(table)){
						org.etlprocess.service.EtlAccountImportServices.uploadAccountLoadService(request,response,process);
					}else if("DataImportInvoiceHeader".equals(table)){
						org.etlprocess.service.EtlImportServices.uploadInvoiceHeaderService(request,response,process);
					}else if("DataImportInvoiceItem".equals(table)){
						org.etlprocess.service.EtlImportServices.uploadInvoiceItemService(request,response,process);
					}else if("DataImportProduct".equals(table)){
						org.etlprocess.service.EtlImportServices.uploadProductService(request,response,process);
					}else if("DataImportCategory".equals(table)){
						org.etlprocess.service.EtlCategoryImportServices.uploadCategoryLoadService(request,response,process);
					}					
					else if("EtlImportOrderFields".equals(table)){
						/*if(!ETLConstants.ETL_ORDER_IMP_SER.equals(serviceName)){
							request.setAttribute ("_ERROR_MESSAGE_","Service not configured properly.");
							return "error";
						}*/
						org.etlprocess.service.EtlOrderImportServices.uploadEtlImportOrderService(request,response,process);
					}else if("EtlOrderFulfillment".equals(table)){
						if(!ETLConstants.ETL_ORDER_FULFILL_SER.equals(serviceName)){
							request.setAttribute ("_ERROR_MESSAGE_",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg7"));
							return "error";
						}
						//org.etlprocess.service.EtlOrderFulfillmentServices.uploadFulfillmentService(request,response,process);
					}
					
				}

			}

		}catch(Exception e){
			Debug.log("+++++++++++++uploadEtlFileService+++++++++++++"+e.toString());
		}
		return "success";
	}
	public static String updateEtlProcessService(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String processId = request.getParameter("eProcessId");
		String processName = request.getParameter("eProcessName");
		String description = request.getParameter("eDescription");
		String updateEtlProcessService = request.getParameter("updateEtlProcessService");
		try{
			if(UtilValidate.isNotEmpty(processId)){
				GenericValue checkId = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",processId),false);
				if(UtilValidate.isNotEmpty(checkId)){
					checkId.put("processName",processName);
					checkId.put("description", description);
					checkId.put("serviceName", updateEtlProcessService);
					checkId.store();
					request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg8"));
					return "success";

				}
				else{
					request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg9"));	
					return "error";
				}

			}

		}catch(Exception e){
			Debug.log("+++++++++++++uploadEtlFileService+++++++++++++"+e.toString());
			request.setAttribute("_ERROR_MESSAGE_", e.toString());	
			return "error";
		}
		return "success";
	}
	//added by m.vijayakumar date:18/03/2016 for delete etl process
	public static String EtlDeleteProcess(HttpServletRequest request, HttpServletResponse response)
	{
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String processId = request.getParameter("processId");
		try
		{
			if(UtilValidate.isNotEmpty(processId))
			{
				GenericValue etlProcess = delegator.findOne("EtlProcess", UtilMisc.toMap("processId",processId),false);
				if(UtilValidate.isNotEmpty(etlProcess))
				{
					etlProcess.remove();
				}else
				{

					request.setAttribute("_ERROR_MESSAGE_", "Process Id "+processId+" is not  exists");
					return "error";
				}
			}
		}catch(Exception e)
		{
			request.setAttribute("_ERROR_MESSAGE_", "Error : ProcessId( "+processId+" )"+e.getMessage());
			return "error";

		}
		request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg10"));
		return "success";

	}

	//added by m.vijayakumar for update etl process operation date:18/03/2016
	public static String updateEtlProcess(HttpServletRequest request, HttpServletResponse response)  {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String processId = request.getParameter("processId");
		try
		{
			if(UtilValidate.isNotEmpty(processId))
			{
				GenericValue etlProcess = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",processId),false);
				if(UtilValidate.isEmpty(etlProcess))
				{
					request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg11"));
					return "error";
				}else
				{
					String processName = request.getParameter("processName");
					String description = request.getParameter("description");
					etlProcess.set("processName", processName);
					etlProcess.set("description", description);
					delegator.store(etlProcess);
				}
			}


		}catch(Exception e)
		{
			/*e.printStackTrace();*/
			Debug.logError(e.getMessage(),MODULE);
			request.setAttribute("_ERROR_MESSAGE_", "Exception:"+e.getMessage());
			return "error";
		}
		request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg12"));
		return "success";
	}

	//added by m.vijayakumar date:23/05/2016 for common form validation
	public static boolean validateFileFormat(File file,String name) throws IOException
	{
		errorLog="";
		String extention = name.substring(name.indexOf('.'));
		if(UtilValidate.isEmpty(extention))
		{
			errorLog = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg4");
			return false;
		}else
		{
			if(!extention.equals(".csv"))
			{
				errorLog = errorLog +" unsuported "+extention+" format";
				return false;
			}
		}

		//to check file empty or not by m.vijayakumar
		if(file.length()==0)// || FileUtils.readFileToString(file).trim().isEmpty())
		{
			errorLog = errorLog +UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg13");
			return false;

		}

		return true;
	}
	public static String commonDownload(HttpServletRequest request, HttpServletResponse response)  {
		
		
		String fileName = "Etl_template";
		String groupId = request.getParameter("groupIdExport");
		String sequenceNumber = request.getParameter("downloadSequence");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try
		{
			GenericValue etltemplateDownload ;
			if(UtilValidate.isNotEmpty(groupId) && UtilValidate.isNotEmpty(sequenceNumber))
			{
				etltemplateDownload = delegator.findOne("EtlTemplateDownload",UtilMisc.toMap("seqId",sequenceNumber),false);
				if(UtilValidate.isNotEmpty(etltemplateDownload))
				{

					String absolutePath = ComponentConfig.getRootLocation("Etl-Process")+"webapp/template/"+etltemplateDownload.getString("fileName");
					File file = null;
					if(UtilValidate.isNotEmpty(absolutePath))
					{
						file = new File(absolutePath);
					}
					
					if(file.exists())
					{
						fileName = etltemplateDownload.getString("fileName");
						 FileInputStream fis = new FileInputStream(file);  
				           byte b[];  
				           int x = fis.available();  
				           b = new byte[x];   
				           fis.read(b);  

				           response.setContentType("text/csv");
				           response.setHeader("Content-Disposition", "attachment; filename="+fileName+"");
				           OutputStream os = response.getOutputStream();  
				           os.write(b);  
				           os.flush();
				           os.close();
				           fis.close();
				           
					}
						  
				}else
				{
					request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg14"));
					request.setAttribute("model", request.getParameter("model"));
					request.setAttribute("groupId",groupId);
					return "error";
				}
			}
		       	
			
		}catch(Exception e)
		{
			Debug.logError(e.getMessage(),MODULE);
			/*e.printStackTrace();*/

			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg15"));
			request.setAttribute("model", request.getParameter("model"));
			request.setAttribute("groupId",groupId);
			return "error";
		}
		
		 return "success";	
	}
	public static String updateEtlProcessModelService(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String processId = request.getParameter("eProcessId");
		String processName = request.getParameter("eProcessName");
		String description = request.getParameter("eDescription");
		String modelId = request.getParameter("modelId");
		String updateEtlProcessService = request.getParameter("updateEtlProcessService");
		
		request.setAttribute("model",modelId);
		try{
			if(UtilValidate.isNotEmpty(processId)){
				GenericValue checkId = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",processId), false);
				if(UtilValidate.isNotEmpty(checkId)){
					checkId.put("processName",processName);
					checkId.put("description", description);
					checkId.put("serviceName", updateEtlProcessService);
					checkId.store();
					request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg8"));
					return "success";

				}
				else{
					request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFileUploadErrorMsg9"));	
					return "error";
				}

			}

		}catch(Exception e){
			Debug.log("+++++++++++++uploadEtlFileService+++++++++++++"+e.toString());
			request.setAttribute("_ERROR_MESSAGE_", e.toString());	
			return "error";
		}
		return "success";
	}

}
