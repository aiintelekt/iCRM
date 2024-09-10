package org.etlprocess.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import org.apache.commons.io.FileUtils;
import org.groupfio.etl.process.job.CustomerImportJob;
import org.groupfio.etl.process.job.InvoiceHeaderImportJob;
import org.groupfio.etl.process.job.InvoiceItemImportJob;
import org.groupfio.etl.process.job.ProductImportJob;
import org.groupfio.etl.process.job.SupplierImportJob;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import au.com.bytecode.opencsv.CSVReader;
import javolution.util.FastList;
import javolution.util.FastMap;


public class EtlImportServices {
	private static String MODULE = EtlImportServices.class.getName();
	private static String errorLog ="";
	public static String currentListId = "";
	public static String accessType = UtilProperties.getPropertyValue("Etl-Process", "UPLOAD_TYPE");
	public static String etlTableName = UtilProperties.getPropertyValue("Etl-Process", "CUSTOMER_TABLE");
	public static String etlSupplierTableName = UtilProperties.getPropertyValue("Etl-Process", "SUPPLIER_TABLE");
	public static String etlProductTableName = UtilProperties.getPropertyValue("Etl-Process", "PRODUCT_TABLE");
	public static String etlInvoiceHeaderTableName = UtilProperties.getPropertyValue("Etl-Process", "INVOICE_HEADER_TABLE");
	public static String etlInvoiceItemTableName = UtilProperties.getPropertyValue("Etl-Process", "INVOICE_ITEM_TABLE");


	public static Map<String, Object>updateEtlLeadFields(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();
		try {
			@SuppressWarnings("unchecked")
			FastList<GenericValue> entitiesToCreateCunk = (FastList<GenericValue>) context.get("entitiesToCreate");
			for(GenericValue gv : entitiesToCreateCunk){
				String partyId = gv.getString("leadId");
				String industryId = gv.getString("industryEnumId");
				String numEmployees = gv.getString("numEmployees");
				String notes = gv.getString("notes");
				String dataSourceId = gv.getString("dataSourceId");
				//GenericValue checkLead = delegator.findByPrimaryKey("PartySupplementalData",UtilMisc.toMap("partyId",partyId));
				GenericValue checkLead = EntityQuery.use(delegator).from("PartySupplementalData").where("partyId", partyId ).queryFirst();
				if(UtilValidate.isNotEmpty(checkLead)){
					if(UtilValidate.isNotEmpty(industryId))
					checkLead.put("industryEnumId", industryId);
					if(UtilValidate.isNotEmpty(numEmployees))
					checkLead.put("numberEmployees", numEmployees);
					if(UtilValidate.isNotEmpty(notes))
					checkLead.put("importantNote", notes);
					checkLead.store();
				}
				//GenericValue leadSource = delegator.findByPrimaryKey("Party",UtilMisc.toMap("partyId",partyId));
				GenericValue leadSource = EntityQuery.use(delegator).from("Party").where("partyId", partyId ).queryFirst();
				if(UtilValidate.isNotEmpty(leadSource)){
					if(UtilValidate.isNotEmpty(dataSourceId))
					leadSource.put("dataSourceId", dataSourceId);
					leadSource.store();
				}
				if(UtilValidate.isNotEmpty(dataSourceId)){
					GenericValue leadDataSource = EntityUtil.getFirst(delegator.findByAnd("PartyDataSource",UtilMisc.toMap("partyId",partyId,"dataSourceId",dataSourceId),null,false));
					if(UtilValidate.isEmpty(leadDataSource)){
						GenericValue makeSource = delegator.makeValue("PartyDataSource");
						makeSource.put("partyId", partyId);
						makeSource.put("dataSourceId", dataSourceId);
						makeSource.put("fromDate", UtilDateTime.nowTimestamp());
						makeSource.create();
					}
				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			return ServiceUtil.returnSuccess();
		}


		return ServiceUtil.returnSuccess();
	}


/**
 * Supplier import Etl
 * @param request
 * @param response
 * @param processId
 * @return
 * @throws GenericEntityException
 * @throws ComponentException 
 */
	@SuppressWarnings("resource")
	public static String uploadSupplierLoadService(HttpServletRequest request, HttpServletResponse response,String processId) throws GenericEntityException, ComponentException {
		 Delegator delegator = (Delegator) request.getAttribute("delegator");
		 LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		 HttpSession session = request.getSession(true);
		 GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		 //String filePath = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.files.location");
		 String filePath = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/";
		 File store = new File(filePath);
		 String listId = "";
		 DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
	     fileItemFactory.setRepository(store);
	     String csvFile="";
	     BufferedReader br = null;
      	 CSVReader reader = null;
		 try{
				String name="";
		        if(ServletFileUpload.isMultipartContent(request)){
		                @SuppressWarnings("unchecked")
						List<FileItem> multiparts = new ServletFileUpload(
		                   new DiskFileItemFactory()).parseRequest(request);
		              
		                for(FileItem item : multiparts){
		                    if(!item.isFormField()){
		                         name = new File(item.getName()).getName();
		                        item.write( new File(filePath + File.separator + name));
		                         if(!validateFileFormat(new File(filePath + File.separator + name), name))
		                         {
		                        	 request.setAttribute("_ERROR_MESSAGE_",errorLog );
		                        	 String proId = session.getAttribute("processId").toString();
		                        	 if(UtilValidate.isNotEmpty(proId))
		                        	 {
		                        		//GenericValue getList = delegator.findByPrimaryKey("EtlProcess",UtilMisc.toMap("processId",proId));
		                        		GenericValue getList = EntityQuery.use(delegator).from("EtlProcess").where("processId", proId ).queryFirst();
		     		                	if(UtilValidate.isNotEmpty(getList)){
		     		                		listId = getList.getString("modalName");
		     		                	}
		                        	 }
		                        	 
		                        	 request.setAttribute("model", listId);
		                        	 request.setAttribute("processId", proId);
		                        	 return "error";
		                         }
		                         
		                         
		                         
		                       
		                    }
		                    if (item.isFormField()) {
			                    String fName = item.getFieldName();
			                    String fValue = item.getString();	
			                    if("processId".equals(fName)){
			                    	processId = fValue;
			                    	if(UtilValidate.isNotEmpty(processId))
			                    	session.setAttribute("processId",processId);
			                    }
			                }
		                }
		                if(UtilValidate.isEmpty(processId)){
		                	processId = (String) session.getAttribute("processId");
		                }
		                if(UtilValidate.isNotEmpty(processId)){
		                	//GenericValue getList = delegator.findByPrimaryKey("EtlProcess",UtilMisc.toMap("processId",processId));
		                	GenericValue getList = EntityQuery.use(delegator).from("EtlProcess").where("processId", processId ).queryFirst();
		                	if(UtilValidate.isNotEmpty(getList)){
		                		listId = getList.getString("modalName");
		                	}
		                }
			               //File uploaded successfully
			           Debug.logInfo("=============="+"File Uploaded Successfully", MODULE);
			            csvFile = filePath+name;
			         	
			         	br = new BufferedReader(new FileReader(csvFile));
		           		int i = 0;
     					Map<Integer,String> columnMap = new HashMap<Integer,String>();
						reader = new CSVReader(new FileReader(csvFile));
      					String [] nextLine;
      					Date date = new Date();
      				    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHMMSS");
      				    String batchId = dateFormat.format(date);
      					while ((nextLine = reader.readNext()) != null) {
		           			Map<String, Object> context = FastMap.newInstance();
		    	            i++;
		           			//String[] cellValues = line.split(cvsSplitBy);
		           			//int cellValuesSize = cellValues.length;
		           			if(nextLine.length>0){
		           				for(int j=0; j<nextLine.length;j++){
				           			String cellValue = nextLine[j];
				           			if(UtilValidate.isNotEmpty(cellValue) && i==1){
				           				GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",UtilMisc.toMap("listName",listId,"etlFieldName",cellValue),null,false));
				           				if(UtilValidate.isNotEmpty(checkValue)){
				           					String tableColumnName = checkValue.getString("tableColumnName");
				           					columnMap.put(j, tableColumnName);
				           				}	
				           				
				           			}
					           		//mapping the data
					           		 if(i>1){
					           			String val = columnMap.get(j);
					           			if(UtilValidate.isNotEmpty(val))
					                    context.put(val, cellValue);
					           		 }
		           				}
		           				//call service
		           				if(UtilValidate.isNotEmpty(context)){
		           					context.put("listId", listId);
									context.put("batchId", batchId);
		           					Map<String,Object> result = dispatcher.runSync("CreateEtlSupplierLoad", context);
		           					if(ServiceUtil.isError(result)){
		           						request.setAttribute("model", listId);
		           						request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties","EtlImportServiceErrorMsg1"));
		           						return "error";
		           					}
		           				}
		           			}

		           		}
      					if(i==1 || i==0){
      						request.setAttribute("model", listId);
      						request.setAttribute("_ERROR_MESSAGE_",UtilProperties.getPropertyValue("Etl-Process.properties","EtlImportServiceErrorMsg2"));
      						return "error";
      						}
      					//model ID
      					String modelId = "";
      					GenericValue getModel = EntityUtil.getFirst(delegator.findByAnd("EtlModel",UtilMisc.toMap("modelName",listId),null,false));
      					if(UtilValidate.isNotEmpty(getModel))
      						modelId = getModel.getString("modelId");

      				Map<String,Object> inputNew = new HashMap<String,Object>();
   		   			inputNew.put("userLogin",userLogin);
   		   			inputNew.put("batchId",batchId);
   		   			inputNew.put("modelId",listId);
   		   			inputNew.put("accessType",accessType);
   		   			inputNew.put("etlTableName",etlSupplierTableName);
   		   			Map<String,Object> Res = dispatcher.runSync("EtlSelfServiceProcessor", inputNew);
   		   			
   		   			if(ServiceUtil.isSuccess(Res)){
   		   				// Trigger Thread
   		   				SupplierImportJob job = new SupplierImportJob();
   			   			job.setDelegator(delegator);
   			   			job.setDispatcher(dispatcher);
   			   			job.setEtlModelId(listId);
   			   			job.setUserLogin(userLogin);
   			   			job.start();
   		   			}
		        }
		
		 }catch(Exception e1){
			 request.setAttribute("model", listId);
			 request.setAttribute("_ERROR_MESSAGE_",e1.toString());
			 return "error";
			 }finally {
					try { 
						if (reader != null) {
							reader.close();
						}
						
						if (br != null) {
							br.close();
						}
						
					} catch (Exception e) {
					}
			}
		request.setAttribute("model", listId);
		request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties","EtlImportServiceErrorMsg3"));
		return "success";
	}
	
	public static Map<String, Object>CreateEtlSupplierLoadService(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();

		try {
			currentListId = (String)context.get("listId");

   			GenericValue DataImportSupplier=delegator.makeValue("DataImportSupplier");
   			
   			
   			DataImportSupplier.put("batchId", (String) context.get("batchId"));
   					   				
   			if(UtilValidate.isNotEmpty((String) context.get("supplierId")))
   			DataImportSupplier.put("supplierId", (String) context.get("supplierId"));
   			else{
				boolean makeError = false;
				makeError =  makeEtlError(delegator,"SUPPLIER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicesupplierIdError"),"DataImportSupplier");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
			}
   			if(UtilValidate.isNotEmpty((String) context.get("supplierName")))
   			DataImportSupplier.put("supplierName", (String) context.get("supplierName"));
   			if(UtilValidate.isNotEmpty((String) context.get("attnName")))
   			DataImportSupplier.put("attnName", (String) context.get("attnName"));
   			if(UtilValidate.isNotEmpty((String) context.get("address1")))
   			DataImportSupplier.put("address1", (String) context.get("address1"));
   			if(UtilValidate.isNotEmpty((String) context.get("address2")))
   			DataImportSupplier.put("address2", (String) context.get("address2"));
   			if(UtilValidate.isNotEmpty((String) context.get("city")))
   			DataImportSupplier.put("city", (String) context.get("city"));
   			
   			if(UtilValidate.isNotEmpty((String) context.get("countryGeoId"))){
   				String contgeoid = (String) context.get("countryGeoId");
   				if(UtilValidate.isNotEmpty((String) context.get("stateProvinceGeoId")) || contgeoid.equals("SGP")){ 					
   					if(contgeoid.equals("SGP")){
   						DataImportSupplier.put("stateProvinceGeoId", "_NA_");
   					}
   					else{
   		            GenericValue geo = EntityQuery.use(delegator).from("Geo").where("geoId", (String) context.get("stateProvinceGeoId"),"geoTypeId","STATE").cache().queryOne();
   					if(UtilValidate.isNotEmpty(geo))
   						DataImportSupplier.put("stateProvinceGeoId", (String) context.get("stateProvinceGeoId"));	            	
   		            else{
   						boolean makeError = false;
   						makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"SUPPLIER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdError"),"DataImportSupplier");
   						if(makeError)
   							return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
   		            }
   				 }
   				}
   				else{
   	   				boolean makeError = false;
   	   				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"SUPPLIER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdEmptyError"),"DataImportSupplier");
   					if(makeError)
   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
   	   			}
   	            }
   	            else{
   	            	boolean makeError = false;
   					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"SUPPLIER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCountryIdEmptyError"),"DataImportSupplier");
   					if(makeError)
   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
   	            }
   			
   			if(UtilValidate.isNotEmpty((String) context.get("postalCode")))
   			DataImportSupplier.put("postalCode", (String) context.get("postalCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("postalCodeExt")))
   			DataImportSupplier.put("postalCodeExt", (String) context.get("postalCodeExt"));
   			if(UtilValidate.isNotEmpty((String) context.get("stateProvinceGeoName")))
   			DataImportSupplier.put("stateProvinceGeoName", (String) context.get("stateProvinceGeoName"));
   			if(UtilValidate.isNotEmpty((String) context.get("countryGeoId"))){
   			 GenericValue geo = EntityQuery.use(delegator).from("Geo").where("geoId", (String) context.get("countryGeoId"),"geoTypeId","COUNTRY").cache().queryOne();
	            if(UtilValidate.isNotEmpty(geo))
	            DataImportSupplier.put("countryGeoId", (String) context.get("countryGeoId"));
	            else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"SUPPLIER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCountryIdError"),"DataImportSupplier");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
	            }
   			}
   			else{
   				boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"SUPPLIER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCountryIdEmptyError"),"DataImportSupplier");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
   			}
   			
   			if(UtilValidate.isNotEmpty((String) context.get("primaryPhoneCountryCode")))
   			DataImportSupplier.put("primaryPhoneCountryCode", (String) context.get("primaryPhoneCountryCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("primaryPhoneAreaCode")))
   			DataImportSupplier.put("primaryPhoneAreaCode", (String) context.get("primaryPhoneAreaCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("primaryPhoneNumber")))
   			DataImportSupplier.put("primaryPhoneNumber", (String) context.get("primaryPhoneNumber"));
   			if(UtilValidate.isNotEmpty((String) context.get("primaryPhoneExtension")))
   			DataImportSupplier.put("primaryPhoneExtension", (String) context.get("primaryPhoneExtension"));
   			if(UtilValidate.isNotEmpty((String) context.get("secondaryPhoneCountryCode")))
   			DataImportSupplier.put("secondaryPhoneCountryCode", (String) context.get("secondaryPhoneCountryCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("secondaryPhoneAreaCode")))
   			DataImportSupplier.put("secondaryPhoneAreaCode", (String) context.get("secondaryPhoneAreaCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("secondaryPhoneNumber")))
   			DataImportSupplier.put("secondaryPhoneNumber",(String) context.get("secondaryPhoneNumber"));
   			if(UtilValidate.isNotEmpty((String) context.get("secondaryPhoneExtension")))
   			DataImportSupplier.put("secondaryPhoneExtension", (String) context.get("secondaryPhoneExtension"));
   			if(UtilValidate.isNotEmpty((String) context.get("faxCountryCode")))
   			DataImportSupplier.put("faxCountryCode", (String) context.get("faxCountryCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("faxAreaCode")))
   			DataImportSupplier.put("faxAreaCode", (String) context.get("faxAreaCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("faxNumber")))
   			DataImportSupplier.put("faxNumber", (String) context.get("faxNumber"));
   			if(UtilValidate.isNotEmpty((String) context.get("didCountryCode")))
   			DataImportSupplier.put("didCountryCode", (String) context.get("didCountryCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("didAreaCode")))
   			DataImportSupplier.put("didAreaCode", (String) context.get("didAreaCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("didNumber")))
   			DataImportSupplier.put("didNumber", (String) context.get("didNumber"));
   			if(UtilValidate.isNotEmpty((String) context.get("didExtension")))
   			DataImportSupplier.put("didExtension", (String) context.get("didExtension"));
   			if(UtilValidate.isNotEmpty((String) context.get("emailAddress")))
   			DataImportSupplier.put("emailAddress", (String) context.get("emailAddress"));
   			if(UtilValidate.isNotEmpty((String) context.get("webAddress")))
   			DataImportSupplier.put("webAddress", (String) context.get("webAddress"));
   			if(UtilValidate.isNotEmpty((String) context.get("note")))
   			DataImportSupplier.put("note", (String) context.get("note"));
   			if(UtilValidate.isNotEmpty((String) context.get("netPaymentDays")))
   			DataImportSupplier.put("netPaymentDays", Long.parseLong((String) context.get("netPaymentDays")));
   			if(UtilValidate.isNotEmpty((String) context.get("isIncorporated")))
   			DataImportSupplier.put("isIncorporated", (String) context.get("isIncorporated"));
   			if(UtilValidate.isNotEmpty((String) context.get("federalTaxId")))
   			DataImportSupplier.put("federalTaxId", (String) context.get("federalTaxId"));
   			if(UtilValidate.isNotEmpty((String) context.get("requires1099")))
   			DataImportSupplier.put("requires1099", (String) context.get("requires1099"));
   			if(UtilValidate.isNotEmpty((String) context.get("primaryPartyId")))
   			DataImportSupplier.put("primaryPartyId", (String) context.get("primaryPartyId"));
   			
   			if(UtilValidate.isNotEmpty((String) context.get("source"))){
            	GenericValue partyIdentificationType = EntityQuery.use(delegator).from("PartyIdentificationType").where("partyIdentificationTypeId", (String) context.get("source")).cache().queryOne();
                if(UtilValidate.isNotEmpty(partyIdentificationType))
                	DataImportSupplier.put("source", (String) context.get("source"));
                else{
                	boolean makeError = false;
    				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"SUPPLIER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicepartyIdentificationTypeIdEmptyError"),"DataImportSupplier");
    				if(makeError)
    					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
                }
            }
   			else
            {
            	boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"SUPPLIER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceinvalidPartyIdentificationTypeIdError"),"DataImportSupplier");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
            }
   		 
   			delegator.create(DataImportSupplier);
   		 
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			return ServiceUtil.returnError(e.toString());
		}


		return ServiceUtil.returnSuccess();
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
		if(file.length()==0 || FileUtils.readFileToString(file).trim().isEmpty())
		{
			errorLog = errorLog +UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg5");
			return false;
			
		}
		
		return true;
	}
	
	public static boolean mappingFieldsService(Delegator delegator,String listId , String columnName) throws GenericEntityException{
		
		GenericValue model = EntityUtil.getFirst(delegator.findByAnd("EtlSourceTable",UtilMisc.toMap("listName",listId,"tableColumnName",columnName),null,false));
		
		if(UtilValidate.isEmpty(model))
			return true;
		else
			return false;
		
	}
	
	public static String checkDefaultField(Delegator delegator,String fieldId , String model , String tableName) throws GenericEntityException{
		
		GenericValue checkDefault = EntityUtil.getFirst(delegator.findByAnd("EtlDefaultsMapping",UtilMisc.toMap("model",model,"etlTableName",tableName,"etlFieldName",fieldId),null,false));
		String defaultValue="";
		if(UtilValidate.isEmpty(checkDefault))
			return defaultValue;
		else{
			defaultValue = checkDefault.getString("defaultValue");
			return defaultValue;	
		}
			
		
	}
	
	public static boolean makeEtlError(Delegator delegator,String errorType , String logMsg , String tableName) throws GenericEntityException{
		
		GenericValue makeError = delegator.makeValue("EtlLogProcError");
		makeError.put("seqId",delegator.getNextSeqId("EtlLogProcError"));
		makeError.put("taskId", delegator.getNextSeqId("EtlLogProcError"));
		makeError.put("taskName", errorType);
		makeError.put("timeStamp", UtilDateTime.nowTimestamp());
		makeError.put("status", "ERROR");
		makeError.put("logMsg1", logMsg);
		makeError.put("tableName", tableName);
		makeError.put("listId", currentListId);
		makeError.create();
		TransactionUtil.commit();
		
		if(makeError.size()>0)
			return true;
		else
			return false;
		
	}	

	@SuppressWarnings("resource")
	public static String uploadCustomerLoadService(HttpServletRequest request, HttpServletResponse response,String processId) throws GenericEntityException, ComponentException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		HttpSession session = request.getSession(true);
		//String filePath = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.files.location");
		String filePath = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/";
		File store = new File(filePath);
		String listId = "";
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		fileItemFactory.setRepository(store);
		String csvFile="";
		BufferedReader br = null;
		CSVReader reader = null;
		try{
			String name="";
			if(ServletFileUpload.isMultipartContent(request)){
				@SuppressWarnings("unchecked")
				List<FileItem> multiparts = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);

				for(FileItem item : multiparts){
					if(!item.isFormField()){
						name = new File(item.getName()).getName();
						item.write( new File(filePath + File.separator + name));
						if(!validateFileFormat(new File(filePath + File.separator + name), name))
						{
							request.setAttribute("_ERROR_MESSAGE_",errorLog );

							String proId = session.getAttribute("processId").toString();
							if(UtilValidate.isNotEmpty(proId))
							{
								GenericValue getList = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",proId),false);
								if(UtilValidate.isNotEmpty(getList)){
									listId = getList.getString("modalName");
								}
							}

							request.setAttribute("model", listId);
							request.setAttribute("processId", proId);

							return "error";
						}
					}
					if (item.isFormField()) {
						String fName = item.getFieldName();
						String fValue = item.getString();	
						if("processId".equals(fName)){
							processId = fValue;
							if(UtilValidate.isNotEmpty(processId))
								session.setAttribute("processId",processId);
						}
					}
				}
				if(UtilValidate.isEmpty(processId)){
					processId = (String) session.getAttribute("processId");
				}
				if(UtilValidate.isNotEmpty(processId)){
					GenericValue getList = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",processId),false);
					if(UtilValidate.isNotEmpty(getList)){
						listId = getList.getString("modalName");
					}
				}
				//File uploaded successfully
				//System.out.println("=============="+"File Uploaded Successfully");
				Debug.log("=============="+"File Uploaded Successfully");
				csvFile = filePath+name;
			
				br = new BufferedReader(new FileReader(csvFile));
				int i = 0;
				Map<Integer,String> columnMap = new HashMap<Integer,String>();
				reader = new CSVReader(new FileReader(csvFile));
				String [] nextLine;
				Date date = new Date();
  				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHMMSS");
  				String batchId = dateFormat.format(date);
				while ((nextLine = reader.readNext()) != null) {
					Map<String, Object> context = FastMap.newInstance();
					i++;
					if(nextLine.length>0){
						for(int j=0; j<nextLine.length;j++){
							String cellValue = nextLine[j];
							if(UtilValidate.isNotEmpty(cellValue) && i==1){
								GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",UtilMisc.toMap("listName",listId,"etlFieldName",cellValue),null,false));
								if(UtilValidate.isNotEmpty(checkValue)){
									String tableColumnName = checkValue.getString("tableColumnName");
									columnMap.put(j, tableColumnName);
								}	

							}
							//mapping the data
							if(i>1){
								String val = columnMap.get(j);
								if(UtilValidate.isNotEmpty(val))
									context.put(val, cellValue);
							}
						}
						//call service
						if(UtilValidate.isNotEmpty(context)){
							context.put("listId", listId);
							context.put("batchId", batchId);
							Map<String,Object> result = dispatcher.runSync("CreateEtlCustomerLoad", context);
           					if(ServiceUtil.isError(result)){
           						request.setAttribute("model", listId);
           						request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
           						return "error";
           					}
						}
					}

				}

					Map<String,Object> inputNew = new HashMap<String,Object>();
		   			inputNew.put("userLogin",userLogin);
		   			inputNew.put("batchId",batchId);
		   			inputNew.put("modelId",listId);
		   			inputNew.put("accessType",accessType);
		   			inputNew.put("etlTableName",etlTableName);
		   			Map<String,Object> Res = dispatcher.runSync("EtlSelfServiceProcessor", inputNew);
		   			
		   			if(ServiceUtil.isSuccess(Res)){
		   				// Trigger Thread
			   			CustomerImportJob job = new CustomerImportJob();
			   			job.setDelegator(delegator);
			   			job.setDispatcher(dispatcher);
			   			job.setEtlModelId(listId);
			   			job.setUserLogin(userLogin);
			   			job.start();
		   			}
		   			
			}

		}catch(Exception e1){
			request.setAttribute("model", listId);
			request.setAttribute("_ERROR_MESSAGE_",e1.toString());
			return "error";
		}finally {
			try { 
				if (reader != null) {
					reader.close();
				}
				
				if (br != null) {
					br.close();
				}
				
			} catch (Exception e) {
			}
	}
		request.setAttribute("model", listId);
		request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		return "success";
	}
/**
 * Import Staging table to InvoiceHeader
 * @throws ComponentException 
 */
	@SuppressWarnings("resource")
	public static String uploadInvoiceHeaderService(HttpServletRequest request, HttpServletResponse response,String processId) throws GenericEntityException, ComponentException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		HttpSession session = request.getSession(true);
		//String filePath = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.files.location");
		String filePath = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/";
		File store = new File(filePath);
		String listId = "";
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		fileItemFactory.setRepository(store);
		String csvFile="";
		BufferedReader br = null;
		CSVReader reader = null;
		try{
			String name="";
			if(ServletFileUpload.isMultipartContent(request)){
				@SuppressWarnings("unchecked")
				List<FileItem> multiparts = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);

				for(FileItem item : multiparts){
					if(!item.isFormField()){
						name = new File(item.getName()).getName();
						item.write( new File(filePath + File.separator + name));
						if(!validateFileFormat(new File(filePath + File.separator + name), name))
						{
							request.setAttribute("_ERROR_MESSAGE_",errorLog );

							String proId = session.getAttribute("processId").toString();
							if(UtilValidate.isNotEmpty(proId))
							{
								GenericValue getList = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",proId),false);
								if(UtilValidate.isNotEmpty(getList)){
									listId = getList.getString("modalName");
								}
							}

							request.setAttribute("model", listId);
							request.setAttribute("processId", proId);

							return "error";
						}



					}
					if (item.isFormField()) {
						String fName = item.getFieldName();
						String fValue = item.getString();	
						if("processId".equals(fName)){
							processId = fValue;
							if(UtilValidate.isNotEmpty(processId))
								session.setAttribute("processId",processId);
						}
					}
				}
				if(UtilValidate.isEmpty(processId)){
					processId = (String) session.getAttribute("processId");
				}
				if(UtilValidate.isNotEmpty(processId)){
					GenericValue getList = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",processId),false);
					if(UtilValidate.isNotEmpty(getList)){
						listId = getList.getString("modalName");
					}
				}
				//File uploaded successfully
				//System.out.println("=============="+"File Uploaded Successfully");
				Debug.log("=============="+"File Uploaded Successfully");
				csvFile = filePath+name;
				
				br = new BufferedReader(new FileReader(csvFile));
				int i = 0;
				Map<Integer,String> columnMap = new HashMap<Integer,String>();
				reader = new CSVReader(new FileReader(csvFile));
				String [] nextLine;
				Date date = new Date();
  				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHMMSS");
  				String batchId = dateFormat.format(date);
				while ((nextLine = reader.readNext()) != null) {
					Map<String, Object> context = FastMap.newInstance();
					i++;
					if(nextLine.length>0){
						for(int j=0; j<nextLine.length;j++){
							String cellValue = nextLine[j];
							if(UtilValidate.isNotEmpty(cellValue) && i==1){
								GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",UtilMisc.toMap("listName",listId,"etlFieldName",cellValue),null,false));
								if(UtilValidate.isNotEmpty(checkValue)){
									String tableColumnName = checkValue.getString("tableColumnName");
									columnMap.put(j, tableColumnName);
								}	

							}
							//mapping the data
							if(i>1){
								String val = columnMap.get(j);
								if(UtilValidate.isNotEmpty(val))
									context.put(val, cellValue);
							}
						}
						//call service
						if(UtilValidate.isNotEmpty(context)){
							context.put("listId", listId);
							context.put("batchId", batchId);
							Map<String,Object>result =  dispatcher.runSync("CreateEtlInvoiceLoad", context);
           					if(ServiceUtil.isError(result)){
           						request.setAttribute("model", listId);
           						request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
           						return "error";
           					}
						}
					}

				}
					
				Map<String,Object> inputNew = new HashMap<String,Object>();
	   			inputNew.put("userLogin",userLogin);
	   			inputNew.put("batchId",batchId);
	   			inputNew.put("modelId",listId);
	   			inputNew.put("accessType",accessType);
	   			inputNew.put("etlTableName",etlInvoiceHeaderTableName);
	   			Map<String,Object> Res = dispatcher.runSync("EtlSelfServiceProcessor", inputNew);
	   			
	   			if(ServiceUtil.isSuccess(Res)){
	   				// Trigger Thread
		   			InvoiceHeaderImportJob job = new InvoiceHeaderImportJob();
		   			job.setDelegator(delegator);
		   			job.setDispatcher(dispatcher);
		   			job.setEtlModelId(listId);
		   			job.setUserLogin(userLogin);
		   			job.start();
	   			}
	   			
		   			/*Map<String,Object> inputNew = new HashMap<String,Object>();
		   			inputNew.put("userLogin",userLogin);
		   			dispatcher.runSync("DataImportInvoiceMain",inputNew);*/ 
		   			
			}

		}catch(Exception e1){
			request.setAttribute("model", listId);
			request.setAttribute("_ERROR_MESSAGE_",e1.toString());
			return "error";
		}finally {
			try { 
				if (reader != null) {
					reader.close();
				}
				
				if (br != null) {
					br.close();
				}
				
			} catch (Exception e) {
			}
	}
		request.setAttribute("model", listId);
		request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
		return "success";
	}

	/**
	 * Import Staging table to InvoiceHeader
	 * @throws ComponentException 
	 */
		@SuppressWarnings("resource")
		public static String uploadInvoiceItemService(HttpServletRequest request, HttpServletResponse response,String processId) throws GenericEntityException, ComponentException {
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
			HttpSession session = request.getSession(true);
			//String filePath = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.files.location");
			String filePath = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/";
			File store = new File(filePath);
			String listId = "";
			DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
			fileItemFactory.setRepository(store);
			String csvFile="";
			BufferedReader br = null;
			CSVReader reader = null;
			try{
				String name="";
				if(ServletFileUpload.isMultipartContent(request)){
					@SuppressWarnings("unchecked")
					List<FileItem> multiparts = new ServletFileUpload(
							new DiskFileItemFactory()).parseRequest(request);

					for(FileItem item : multiparts){
						if(!item.isFormField()){
							name = new File(item.getName()).getName();
							item.write( new File(filePath + File.separator + name));
							if(!validateFileFormat(new File(filePath + File.separator + name), name))
							{
								request.setAttribute("_ERROR_MESSAGE_",errorLog );

								String proId = session.getAttribute("processId").toString();
								if(UtilValidate.isNotEmpty(proId))
								{
									GenericValue getList = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",proId),false);
									if(UtilValidate.isNotEmpty(getList)){
										listId = getList.getString("modalName");
									}
								}

								request.setAttribute("model", listId);
								request.setAttribute("processId", proId);

								return "error";
							}



						}
						if (item.isFormField()) {
							String fName = item.getFieldName();
							String fValue = item.getString();	
							if("processId".equals(fName)){
								processId = fValue;
								if(UtilValidate.isNotEmpty(processId))
									session.setAttribute("processId",processId);
							}
						}
					}
					if(UtilValidate.isEmpty(processId)){
						processId = (String) session.getAttribute("processId");
					}
					if(UtilValidate.isNotEmpty(processId)){
						GenericValue getList = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",processId),false);
						if(UtilValidate.isNotEmpty(getList)){
							listId = getList.getString("modalName");
						}
					}
					//File uploaded successfully
					//System.out.println("=============="+"File Uploaded Successfully");
					Debug.log("=============="+"File Uploaded Successfully");
					csvFile = filePath+name;
					
					br = new BufferedReader(new FileReader(csvFile));
					int i = 0;
					Map<Integer,String> columnMap = new HashMap<Integer,String>();
					reader = new CSVReader(new FileReader(csvFile));
					String [] nextLine;
					Date date = new Date();
	  				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHMMSS");
	  				String batchId = dateFormat.format(date);
					while ((nextLine = reader.readNext()) != null) {
						Map<String, Object> context = FastMap.newInstance();
						i++;
						if(nextLine.length>0){
							for(int j=0; j<nextLine.length;j++){
								String cellValue = nextLine[j];
								if(UtilValidate.isNotEmpty(cellValue) && i==1){
									GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",UtilMisc.toMap("listName",listId,"etlFieldName",cellValue),null,false));
									if(UtilValidate.isNotEmpty(checkValue)){
										String tableColumnName = checkValue.getString("tableColumnName");
										columnMap.put(j, tableColumnName);
									}	

								}
								//mapping the data
								if(i>1){
									String val = columnMap.get(j);
									if(UtilValidate.isNotEmpty(val))
										context.put(val, cellValue);
								}
							}
							//call service
							if(UtilValidate.isNotEmpty(context)){
								context.put("listId", listId);
								context.put("batchId", batchId);
								Map<String,Object> result =  dispatcher.runSync("CreateEtlInvoiceItemLoad", context);
	           					if(ServiceUtil.isError(result)){
	           						request.setAttribute("model", listId);
	           						request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
	           						return "error";
	           					}
							}
						}

					}
					Map<String,Object> inputNew = new HashMap<String,Object>();
		   			inputNew.put("userLogin",userLogin);
		   			inputNew.put("batchId",batchId);
		   			inputNew.put("modelId",listId);
		   			inputNew.put("accessType",accessType);
		   			inputNew.put("etlTableName",etlInvoiceItemTableName);
		   			Map<String,Object> Res = dispatcher.runSync("EtlSelfServiceProcessor", inputNew);
		   			
		   			if(ServiceUtil.isSuccess(Res)){
		   				// Trigger Thread
			   			InvoiceItemImportJob job = new InvoiceItemImportJob();
			   			job.setDelegator(delegator);
			   			job.setDispatcher(dispatcher);
			   			job.setEtlModelId(listId);
			   			job.setUserLogin(userLogin);
			   			job.start();
		   			}
		   			
			   			/*Map<String,Object> inputNew = new HashMap<String,Object>();
			   			inputNew.put("userLogin",userLogin);
			   			dispatcher.runSync("DataImportInvoiceItem",inputNew); */
			   			
				}

			}catch(Exception e1){
				request.setAttribute("model", listId);
				request.setAttribute("_ERROR_MESSAGE_",e1.toString());
				return "error";
			}finally {
				try { 
					if (reader != null) {
						reader.close();
					}
					
					if (br != null) {
						br.close();
					}
					
				} catch (Exception e) {
				}
		}
			request.setAttribute("model", listId);
			request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
			return "success";
		}
	public static Map<String, Object>CreateEtlInvoiceLoadService(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();

		try {
			currentListId = (String)context.get("listId");

   			GenericValue DataImportInvoice=delegator.makeValue("DataImportInvoiceHeader");
   			
   			DataImportInvoice.put("batchId", (String) context.get("batchId"));
   			if(UtilValidate.isNotEmpty((String) context.get("invoiceId"))){
   				GenericValue invoice = EntityQuery.use(delegator).from("Invoice").where("invoiceId", (String) context.get("invoiceId")).cache().queryOne();
   				if(UtilValidate.isEmpty(invoice))
   				DataImportInvoice.put("invoiceId", (String) context.get("invoiceId"));
   				else{
   					boolean makeError = false;
   					makeError =  makeEtlError(delegator,"Invoice",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceIdError"),"DataImportInvoiceHeader");
   					if(makeError)
   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
   				}   				
   			}
   			else{
				boolean makeError = false;
				makeError =  makeEtlError(delegator,"Invoice",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceIdEmptyError"),"DataImportInvoiceHeader");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				}
   			if(UtilValidate.isNotEmpty((String) context.get("invoiceTypeId"))){
   				GenericValue invoiceType = EntityQuery.use(delegator).from("InvoiceType").where("invoiceTypeId", (String) context.get("invoiceTypeId")).cache().queryOne();
   				if(UtilValidate.isNotEmpty(invoiceType))
   				DataImportInvoice.put("invoiceTypeId", (String) context.get("invoiceTypeId"));
   				else{
   					boolean makeError = false;
   					makeError =  makeEtlError(delegator,"Invoice",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceTypeIdError"),"DataImportInvoiceHeader");
   					if(makeError)
   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
   				}
   			}
   			else{
				boolean makeError = false;
				makeError =  makeEtlError(delegator,"Invoice",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceTypeIdEmptyError"),"DataImportInvoiceHeader");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				}
   			if(UtilValidate.isNotEmpty((String) context.get("invoiceDate"))){
   				Timestamp invoiceDt = Timestamp.valueOf((String) context.get("invoiceDate"));
   				DataImportInvoice.put("invoiceDate", invoiceDt);	
   			}
   			if(UtilValidate.isNotEmpty((String) context.get("currencyUomId"))){
   				GenericValue uom = EntityQuery.use(delegator).from("Uom").where("uomId", (String) context.get("currencyUomId")).cache().queryOne();
   				if(UtilValidate.isNotEmpty(uom))
   				DataImportInvoice.put("currencyUomId", (String) context.get("currencyUomId"));
   				else{
   					boolean makeError = false;
   					makeError =  makeEtlError(delegator,"Invoice",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCurrencyError"),"DataImportInvoiceHeader");
   					if(makeError)
   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
   				}
   			}
   			else{
				boolean makeError = false;
				makeError =  makeEtlError(delegator,"Invoice",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCurrencyEmptyError"),"DataImportInvoiceHeader");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				}
   			if(UtilValidate.isNotEmpty((String) context.get("partyIdFrom")))
   				DataImportInvoice.put("partyIdFrom", (String) context.get("partyIdFrom"));
   			if(UtilValidate.isNotEmpty((String) context.get("partyId"))){
   				GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", (String) context.get("partyId")).cache().queryOne();
   				if(UtilValidate.isNotEmpty(party))
   				DataImportInvoice.put("partyId", (String) context.get("partyId"));
   				else{
   					boolean makeError = false;
   					makeError =  makeEtlError(delegator,"Invoice",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicePartyIdError"),"DataImportInvoiceHeader");
   					if(makeError)
   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
   				}
   			}
   			else{
				boolean makeError = false;
				makeError =  makeEtlError(delegator,"Invoice",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicePartyIdEmptyError"),"DataImportInvoiceHeader");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				}
   			if(UtilValidate.isNotEmpty((String) context.get("dueDate"))){
   				Timestamp dueDt = Timestamp.valueOf((String) context.get("dueDate"));
  				DataImportInvoice.put("dueDate", dueDt);
   			}
   			if(UtilValidate.isNotEmpty((String) context.get("description")))
   				DataImportInvoice.put("description", (String) context.get("description"));
   			if(UtilValidate.isNotEmpty((String) context.get("referenceNumber")))
   				DataImportInvoice.put("referenceNumber", (String) context.get("referenceNumber"));
   			if(UtilValidate.isNotEmpty((String) context.get("adjustedAmount")))
   			{
   				BigDecimal adjAmnt = new BigDecimal((String) context.get("adjustedAmount"));
   				DataImportInvoice.put("adjustedAmount", adjAmnt);
   			}
   			if(UtilValidate.isNotEmpty((String) context.get("appliedAmount"))){
   				BigDecimal appAmnt = new BigDecimal((String) context.get("appliedAmount"));
   				DataImportInvoice.put("appliedAmount", appAmnt);
   			}
   			if(UtilValidate.isNotEmpty((String) context.get("invoiceTotal"))){
   				BigDecimal invoiceTotal = new BigDecimal((String) context.get("invoiceTotal"));
 				DataImportInvoice.put("invoiceTotal", invoiceTotal);
   			}
   			if(UtilValidate.isNotEmpty((String) context.get("openAmount"))){
   				BigDecimal openAmount = new BigDecimal((String) context.get("openAmount"));
  				DataImportInvoice.put("openAmount", openAmount);	
   			}
 
   			if(UtilValidate.isNotEmpty((String) context.get("paidDate"))){
   				Timestamp paidDt = Timestamp.valueOf((String) context.get("paidDate"));
   				DataImportInvoice.put("paidDate", paidDt);
   			}
   				
	
   			delegator.create(DataImportInvoice);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			return ServiceUtil.returnError(e.toString());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object>CreateEtlInvoiceItemService(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();

		try {
			currentListId = (String)context.get("listId");

   			GenericValue DataImportInvoice=delegator.makeValue("DataImportInvoiceItem");
   			DataImportInvoice.put("batchId", (String) context.get("batchId"));
   			if(UtilValidate.isNotEmpty((String) context.get("invoiceId"))){
   				GenericValue invoice = EntityQuery.use(delegator).from("Invoice").where("invoiceId", (String) context.get("invoiceId")).queryOne();
   				if(UtilValidate.isNotEmpty(invoice))
   				DataImportInvoice.put("invoiceId", (String) context.get("invoiceId"));
   				else{
   					boolean makeError = false;
   					makeError =  makeEtlError(delegator,"Invoice Item",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceIdError"),"DataImportInvoiceItem");
   					if(makeError)
   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
   				}
   			}
   			else{
				boolean makeError = false;
				makeError =  makeEtlError(delegator,"Invoice Item",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceIdEmptyError"),"DataImportInvoiceItem");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				}
   			if(UtilValidate.isNotEmpty((String) context.get("invoiceItemSeqId")))
   				DataImportInvoice.put("invoiceItemSeqId", (String) context.get("invoiceItemSeqId"));
   			else{
				boolean makeError = false;
				makeError =  makeEtlError(delegator,"Invoice Item",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceItemIdError"),"DataImportInvoiceItem");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				}
   			if(UtilValidate.isNotEmpty((String) context.get("invoiceItemTypeId")))
   				DataImportInvoice.put("invoiceItemTypeId", (String) context.get("invoiceItemTypeId"));
   			else{
				boolean makeError = false;
				makeError =  makeEtlError(delegator,"Invoice Item",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvoiceItemTypeIdError"),"DataImportInvoiceItem");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				}
   			if(UtilValidate.isNotEmpty((String) context.get("amount"))){
   				BigDecimal amount = new BigDecimal((String) context.get("amount"));
  				DataImportInvoice.put("amount", amount);	
   			}
   			else{
				boolean makeError = false;
				makeError =  makeEtlError(delegator,"Invoice Item",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceamountEmptyError"),"DataImportInvoiceItem");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
			}
   			if(UtilValidate.isNotEmpty((String) context.get("description")))
   				DataImportInvoice.put("description", (String) context.get("description"));
   			if(UtilValidate.isNotEmpty((String) context.get("quantity"))){
   				BigDecimal qty = new BigDecimal((String) context.get("quantity"));
  				DataImportInvoice.put("quantity", qty);
   			}
   			else{
				boolean makeError = false;
				makeError =  makeEtlError(delegator,"Invoice Item",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceQuantityEmptyError"),"DataImportInvoiceItem");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
			}
   			if(UtilValidate.isNotEmpty((String) context.get("productId"))){
   				GenericValue product = EntityQuery.use(delegator).from("Product").where("productId", (String) context.get("productId")).cache().queryOne();
   				if(UtilValidate.isNotEmpty(product))
   				DataImportInvoice.put("productId", (String) context.get("productId"));
   				else{
   					boolean makeError = false;
   					makeError =  makeEtlError(delegator,"Invoice Item",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvalidProductIdError"),"DataImportInvoiceItem");
   					if(makeError)
   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
   				}
   			}
   			else{
				boolean makeError = false;
				makeError =  makeEtlError(delegator,"Invoice Item",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductIdEmptyError"),"DataImportInvoiceItem");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
			}

   			if(UtilValidate.isNotEmpty((String) context.get("description")))
   				DataImportInvoice.put("description", (String) context.get("description"));
	
   			delegator.create(DataImportInvoice);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			return ServiceUtil.returnError(e.toString());
		}
		return ServiceUtil.returnSuccess();
	}
	/**
	 * Import Staging table to Product
	 * @throws ComponentException 
	 */
		@SuppressWarnings("resource")
		public static String uploadProductService(HttpServletRequest request, HttpServletResponse response,String processId) throws GenericEntityException, ComponentException {
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
			HttpSession session = request.getSession(true);
			//String filePath = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.files.location");
			String filePath = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/";
			File store = new File(filePath);
			String listId = "";
			DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
			fileItemFactory.setRepository(store);
			String csvFile="";
			BufferedReader br = null;
			CSVReader reader = null;
			try{
				String name="";
				if(ServletFileUpload.isMultipartContent(request)){
					@SuppressWarnings("unchecked")
					List<FileItem> multiparts = new ServletFileUpload(
							new DiskFileItemFactory()).parseRequest(request);

					for(FileItem item : multiparts){
						if(!item.isFormField()){
							name = new File(item.getName()).getName();
							item.write( new File(filePath + File.separator + name));
							if(!validateFileFormat(new File(filePath + File.separator + name), name))
							{
								request.setAttribute("_ERROR_MESSAGE_",errorLog );

								String proId = session.getAttribute("processId").toString();
								if(UtilValidate.isNotEmpty(proId))
								{
									GenericValue getList = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",proId),false);
									if(UtilValidate.isNotEmpty(getList)){
										listId = getList.getString("modalName");
									}
								}

								request.setAttribute("model", listId);
								request.setAttribute("processId", proId);

								return "error";
							}



						}
						if (item.isFormField()) {
							String fName = item.getFieldName();
							String fValue = item.getString();	
							if("processId".equals(fName)){
								processId = fValue;
								if(UtilValidate.isNotEmpty(processId))
									session.setAttribute("processId",processId);
							}
						}
					}
					if(UtilValidate.isEmpty(processId)){
						processId = (String) session.getAttribute("processId");
					}
					if(UtilValidate.isNotEmpty(processId)){
						GenericValue getList = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",processId),false);
						if(UtilValidate.isNotEmpty(getList)){
							listId = getList.getString("modalName");
						}
					}
					//File uploaded successfully
					//System.out.println("=============="+"File Uploaded Successfully");
					Debug.log("=============="+"File Uploaded Successfully");
					csvFile = filePath+name;
					br = new BufferedReader(new FileReader(csvFile));
					int i = 0;
					Map<Integer,String> columnMap = new HashMap<Integer,String>();
					reader = new CSVReader(new FileReader(csvFile));
					String [] nextLine;
					Date date = new Date();
				    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHMMSS");
				    String batchId = dateFormat.format(date);
					while ((nextLine = reader.readNext()) != null) {
						Map<String, Object> context = FastMap.newInstance();
						i++;
						if(nextLine.length>0){
							for(int j=0; j<nextLine.length;j++){
								String cellValue = nextLine[j];
								if(UtilValidate.isNotEmpty(cellValue) && i==1){
									GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",UtilMisc.toMap("listName",listId,"etlFieldName",cellValue),null,false));
									if(UtilValidate.isNotEmpty(checkValue)){
										String tableColumnName = checkValue.getString("tableColumnName");
										columnMap.put(j, tableColumnName);
									}	

								}
								//mapping the data
								if(i>1){
									String val = columnMap.get(j);
									if(UtilValidate.isNotEmpty(val))
										context.put(val, cellValue);
								}
							}
							//call service
							if(UtilValidate.isNotEmpty(context)){
								context.put("listId", listId);
								context.put("batchId", batchId);
								Map<String,Object> result =  dispatcher.runSync("importProductsToStaing", context);
	           					if(ServiceUtil.isError(result)){
	           						request.setAttribute("model", listId);
	           						request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
	           						return "error";
	           					}
							}
						}

					}
						   
					Map<String,Object> inputNew = new HashMap<String,Object>();
		   			inputNew.put("userLogin",userLogin);
		   			inputNew.put("batchId",batchId);
		   			inputNew.put("modelId",listId);
		   			inputNew.put("accessType",accessType);
		   			inputNew.put("etlTableName",etlProductTableName);
		   			Map<String,Object> Res = dispatcher.runSync("EtlSelfServiceProcessor", inputNew);
		   			
		   			if(ServiceUtil.isSuccess(Res)){
		   				// Trigger Thread
		   				ProductImportJob job = new ProductImportJob();
			   			job.setDelegator(delegator);
			   			job.setDispatcher(dispatcher);
			   			job.setEtlModelId(listId);
			   			job.setUserLogin(userLogin);
			   			job.start();
		   			}	   					   	
			   			
				}

			}catch(Exception e1){
				request.setAttribute("model", listId);
				request.setAttribute("_ERROR_MESSAGE_",e1.toString());
				return "error";
			}finally {
				try { 
					if (reader != null) {
						reader.close();
					}
					
					if (br != null) {
						br.close();
					}
					
				} catch (Exception e) {
				}
		}
			request.setAttribute("model", listId);
			request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg3"));
			return "success";
		}
/**
 * Product Import Staging
 * @param dctx
 * @param context
 * @return
 */
		public static Map<String, Object>stagingImportProductService(DispatchContext dctx, Map<String, ? extends Object> context)
		{
			Delegator delegator = dctx.getDelegator();

			try {
				currentListId = (String)context.get("listId");

			  GenericValue importProduct=delegator.makeValue("DataImportProduct");
			  		importProduct.put("batchId", (String) context.get("batchId"));				
					if(UtilValidate.isNotEmpty((String) context.get("productId"))){
						String productId =(String) context.get("productId");
						if(productId.length() > 20){
							boolean makeError = false;
							makeError =  makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductIdLengthError"),"DataImportProduct");
							if(makeError)
								return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
						}
						else
						importProduct.put("productId", (String) context.get("productId"));
						/*else{
				   			 GenericValue product = EntityQuery.use(delegator).from("Product").where("productId", (String) context.get("productId")).cache().queryOne();
				   			 if(UtilValidate.isEmpty(product)) 
				   				importProduct.put("productId", (String) context.get("productId"));
				   			 else{
				   				boolean makeError = false;
								makeError =  makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvalidsProductIdError"),"DataImportProduct");
								if(makeError)
									return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
				   			 }
						}*/
						
					}
					else
					{
						boolean makeError = false;
						makeError =  makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductIdError"),"DataImportProduct");
						if(makeError)
							return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
					}
					if(UtilValidate.isNotEmpty((String) context.get("productTypeId"))){

			   			 GenericValue productType = EntityQuery.use(delegator).from("ProductType").where("productTypeId", (String) context.get("productTypeId")).cache().queryOne();
				         if(UtilValidate.isNotEmpty(productType))
				        	 importProduct.put("productTypeId", (String) context.get("productTypeId"));			         
				         else{
								boolean makeError = false;
								makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductTypeIdError1"),"DataImportProduct");
								if(makeError)
									return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				            }			
					}
					else{
						boolean makeError = false;
						makeError =  makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductTypeIdError"),"DataImportProduct");
						if(makeError)
							return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		

					}
					if(UtilValidate.isNotEmpty((String) context.get("isInactive")))
						importProduct.put("isInactive", (String) context.get("isInactive"));
					if(UtilValidate.isNotEmpty((String) context.get("customId1")))
						importProduct.put("customId1", (String) context.get("customId1"));
					if(UtilValidate.isNotEmpty((String) context.get("customId2")))
						importProduct.put("customId2", (String) context.get("customId2"));
					if(UtilValidate.isNotEmpty((String) context.get("internalName")))
						importProduct.put("internalName", (String) context.get("internalName"));
					if(UtilValidate.isNotEmpty((String) context.get("brandName")))
						importProduct.put("brandName", (String) context.get("brandName"));
					if(UtilValidate.isNotEmpty((String) context.get("comments")))
						importProduct.put("comments", (String) context.get("comments"));
					if(UtilValidate.isNotEmpty((String) context.get("productName")))
						importProduct.put("productName", (String) context.get("productName"));
					if(UtilValidate.isNotEmpty((String) context.get("description")))
						importProduct.put("description", (String) context.get("description"));
					if(UtilValidate.isNotEmpty((String) context.get("longDescription")))
						importProduct.put("longDescription", (String) context.get("longDescription"));
					if(UtilValidate.isNotEmpty((String) context.get("weight"))){
						BigDecimal weight = new BigDecimal((String) context.get("weight"));
						importProduct.put("weight",weight);	
					}
					if(UtilValidate.isNotEmpty((String) context.get("weightUomId"))){
						GenericValue uom = EntityQuery.use(delegator).from("Uom").where("uomId", (String) context.get("weightUomId")).cache().queryOne();
						if(UtilValidate.isNotEmpty(uom))
							importProduct.put("weightUomId", (String) context.get("weightUomId"));			         
				         else{
								boolean makeError = false;
								makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceweightUomIdError"),"DataImportProduct");
								if(makeError)
									return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				            }							
					}
					else{
						boolean makeError = false;
						makeError =  makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceweightUomIdEmptyError"),"DataImportProduct");
						if(makeError)
							return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
					}
					if(UtilValidate.isNotEmpty((String) context.get("productLength"))){
						BigDecimal productLength = new BigDecimal((String) context.get("productLength"));
						importProduct.put("productLength",productLength);	
					}
					if(UtilValidate.isNotEmpty((String) context.get("productLengthUomId"))){
						GenericValue uom = EntityQuery.use(delegator).from("Uom").where("uomId", (String) context.get("productLengthUomId")).cache().queryOne();
						if(UtilValidate.isNotEmpty(uom))
							importProduct.put("productLengthUomId", (String) context.get("productLengthUomId"));			         
				         else{
								boolean makeError = false;
								makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceproductLengthUomIdError"),"DataImportProduct");
								if(makeError)
									return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				            }							
					}
					else{
						boolean makeError = false;
						makeError =  makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceproductLengthUomIdEmptyError"),"DataImportProduct");
						if(makeError)
							return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
					}
					
						
					if(UtilValidate.isNotEmpty((String) context.get("width"))){
						BigDecimal width = new BigDecimal((String) context.get("width"));
						importProduct.put("width",width);	
					}
					
					if(UtilValidate.isNotEmpty((String) context.get("widthUomId"))){
						GenericValue uom = EntityQuery.use(delegator).from("Uom").where("uomId", (String) context.get("widthUomId")).cache().queryOne();
						if(UtilValidate.isNotEmpty(uom))
							importProduct.put("widthUomId", (String) context.get("widthUomId"));			         
				         else{
								boolean makeError = false;
								makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicewidthUomIdError"),"DataImportProduct");
								if(makeError)
									return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				            }							
					}
					else{
						boolean makeError = false;
						makeError =  makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicewidthUomIdEmptyError"),"DataImportProduct");
						if(makeError)
							return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
					}
										
					if(UtilValidate.isNotEmpty((String) context.get("height"))){
						BigDecimal height = new BigDecimal((String) context.get("height"));
						importProduct.put("height",height);	
					}
					
					
					if(UtilValidate.isNotEmpty((String) context.get("heightUomId"))){
						GenericValue uom = EntityQuery.use(delegator).from("Uom").where("uomId", (String) context.get("heightUomId")).cache().queryOne();
						if(UtilValidate.isNotEmpty(uom))
							importProduct.put("heightUomId", (String) context.get("heightUomId"));		         
				         else{
								boolean makeError = false;
								makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceheightUomIdError"),"DataImportProduct");
								if(makeError)
									return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				            }							
					}
					else{
						boolean makeError = false;
						makeError =  makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceheightUomIdEmptyError"),"DataImportProduct");
						if(makeError)
							return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
					}
					
						
					if(UtilValidate.isNotEmpty((String) context.get("price"))){
						BigDecimal price = new BigDecimal((String) context.get("price"));
						importProduct.put("price",price);	
					}
					
					if(UtilValidate.isNotEmpty((String) context.get("priceCurrencyUomId"))){
						GenericValue uom = EntityQuery.use(delegator).from("Uom").where("uomId", (String) context.get("priceCurrencyUomId")).cache().queryOne();
						if(UtilValidate.isNotEmpty(uom))
							importProduct.put("priceCurrencyUomId", (String) context.get("priceCurrencyUomId"));       
				         else{
								boolean makeError = false;
								makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicepriceCurrencyUomIdError"),"DataImportProduct");
								if(makeError)
									return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				            }							
					}
					else{
						boolean makeError = false;
						makeError =  makeEtlError(delegator,"PRODUCT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicepriceCurrencyUomIdEmptyError"),"DataImportProduct");
						if(makeError)
							return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
					}
											
					if(UtilValidate.isNotEmpty((String) context.get("productFeature1")))
						importProduct.put("productFeature1", (String) context.get("productFeature1"));
					if(UtilValidate.isNotEmpty((String) context.get("supplierPartyId")))
						importProduct.put("supplierPartyId", (String) context.get("supplierPartyId"));
					if(UtilValidate.isNotEmpty((String) context.get("smallImageUrl")))
						importProduct.put("smallImageUrl", (String) context.get("smallImageUrl"));
					if(UtilValidate.isNotEmpty((String) context.get("mediumImageUrl")))
						importProduct.put("mediumImageUrl", (String) context.get("mediumImageUrl"));
					if(UtilValidate.isNotEmpty((String) context.get("largeImageUrl")))
						importProduct.put("largeImageUrl", (String) context.get("largeImageUrl"));
					if(UtilValidate.isNotEmpty((String) context.get("purchasePrice"))){
						BigDecimal purchasePrice = new BigDecimal((String) context.get("purchasePrice"));
						importProduct.put("purchasePrice",purchasePrice);	
					}
					if(UtilValidate.isNotEmpty((String) context.get("taxable")))
						importProduct.put("taxable", (String) context.get("taxable"));
					if(UtilValidate.isNotEmpty((String) context.get("storeId")))
						importProduct.put("storeId", (String) context.get("storeId"));
					if(UtilValidate.isNotEmpty((String) context.get("createdDate"))){
						Timestamp createdDate = Timestamp.valueOf((String) context.get("createdDate"));
						importProduct.put("createdDate", createdDate);
					}
					delegator.create(importProduct);
				} catch (Exception e) {
				// TODO Auto-generated catch block
				/*e.printStackTrace();*/
				return ServiceUtil.returnError(e.toString());
			}
			return ServiceUtil.returnSuccess();
		}
}