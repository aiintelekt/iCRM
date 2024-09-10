package org.etlprocess.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import org.groupfio.etl.process.job.AccountImportJob;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import au.com.bytecode.opencsv.CSVReader;
import javolution.util.FastMap;

public class EtlAccountImportServices {
	
	private static String MODULE = EtlAccountImportServices.class.getName();
	private static String errorLog ="";
	public static String currentListId = "";
	public static String accessType = UtilProperties.getPropertyValue("Etl-Process", "UPLOAD_TYPE");
	public static String etlTableName = UtilProperties.getPropertyValue("Etl-Process", "ACCOUNT_TABLE");
	
	
	@SuppressWarnings("resource")
	public static String uploadAccountLoadService(HttpServletRequest request, HttpServletResponse response,String processId) throws GenericEntityException, ComponentException {
		 Delegator delegator = (Delegator) request.getAttribute("delegator");
		 LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		 HttpSession session = request.getSession(true);
		 GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
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
                         if(!org.etlprocess.service.EtlImportServices.validateFileFormat(new File(filePath + File.separator + name), name))
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
           			if(nextLine.length>0){
           				for(int j=0; j<nextLine.length;j++){
		           			String cellValue = nextLine[j];
		           			if(UtilValidate.isNotEmpty(cellValue) && i==1){
		           				GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlSourceTable",UtilMisc.toMap("listName",listId,"etlFieldName",cellValue),null,false));
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
           					Map<String,Object> result = dispatcher.runSync("CreateEtlAccountLoad", context);
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
   			inputNew.put("etlTableName",etlTableName);
   			Map<String,Object> Res = dispatcher.runSync("EtlSelfServiceProcessor", inputNew);
   			
   			if(ServiceUtil.isSuccess(Res)){
   				// Trigger Thread
   				AccountImportJob job = new AccountImportJob();
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
			 }
		 finally {
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
	public static Map<String, Object>CreateEtlAccountLoadService(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();

		try {
			currentListId = (String)context.get("listId");

   			GenericValue DataImportAccount=delegator.makeValue("DataImportAccount");
   			
   			
   			DataImportAccount.put("batchId", (String) context.get("batchId"));
   					   				
   			if(UtilValidate.isNotEmpty((String) context.get("accountId")))
   				DataImportAccount.put("accountId", (String) context.get("accountId"));
   			else{
				boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ACCOUNT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceaccountIdError"),"DataImportAccount");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
			}
   			if(UtilValidate.isNotEmpty((String) context.get("accountName")))
   				DataImportAccount.put("accountName", (String) context.get("accountName"));
   			if(UtilValidate.isNotEmpty((String) context.get("attnName")))
   				DataImportAccount.put("attnName", (String) context.get("attnName"));
   			if(UtilValidate.isNotEmpty((String) context.get("address1")))
   				DataImportAccount.put("address1", (String) context.get("address1"));
   			if(UtilValidate.isNotEmpty((String) context.get("address2")))
   				DataImportAccount.put("address2", (String) context.get("address2"));
   			if(UtilValidate.isNotEmpty((String) context.get("city")))
   				DataImportAccount.put("city", (String) context.get("city"));
/*   			if(UtilValidate.isNotEmpty((String) context.get("stateProvinceGeoId"))){
   			 GenericValue geo = EntityQuery.use(delegator).from("Geo").where("geoId", (String) context.get("stateProvinceGeoId"),"geoTypeId","STATE").cache().queryOne();
	         if(UtilValidate.isNotEmpty(geo))
	        	 DataImportAccount.put("stateProvinceGeoId", (String) context.get("stateProvinceGeoId"));
	         else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ACCOUNT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdError"),"DataImportAccount");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
	            }
   			}
   			else{
   				boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ACCOUNT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdEmptyError"),"DataImportAccount");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
   			}*/
   			if(UtilValidate.isNotEmpty((String) context.get("countryGeoId"))){
   				String contgeoid = (String) context.get("countryGeoId");
   				if(UtilValidate.isNotEmpty((String) context.get("stateProvinceGeoId")) || contgeoid.equals("SGP")){
   					
   					if(contgeoid.equals("SGP")){
   						DataImportAccount.put("stateProvinceGeoId", "_NA_");
   					}
   					else{
   		            GenericValue geo = EntityQuery.use(delegator).from("Geo").where("geoId", (String) context.get("stateProvinceGeoId"),"geoTypeId","STATE").cache().queryOne();
   					if(UtilValidate.isNotEmpty(geo))
   					 DataImportAccount.put("stateProvinceGeoId", (String) context.get("stateProvinceGeoId"));            	
   		            else{
   						boolean makeError = false;
   						makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ACCOUNT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdError"),"DataImportAccount");
   						if(makeError)
   							return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
   		            }
   				 }
   				}
   				else{
   	   				boolean makeError = false;
   	   				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ACCOUNT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdEmptyError"),"DataImportAccount");
   					if(makeError)
   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
   	   			}
   	            }
   	            else{
   	            	boolean makeError = false;
   					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ACCOUNT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCountryIdEmptyError"),"DataImportAccount");
   					if(makeError)
   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
   	            }
   			
   			
   			if(UtilValidate.isNotEmpty((String) context.get("postalCode")))
   				DataImportAccount.put("postalCode", (String) context.get("postalCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("postalCodeExt")))
   				DataImportAccount.put("postalCodeExt", (String) context.get("postalCodeExt"));
   			if(UtilValidate.isNotEmpty((String) context.get("stateProvinceGeoName")))
   				DataImportAccount.put("stateProvinceGeoName", (String) context.get("stateProvinceGeoName"));
   			if(UtilValidate.isNotEmpty((String) context.get("countryGeoId"))){
   			 GenericValue geo = EntityQuery.use(delegator).from("Geo").where("geoId", (String) context.get("countryGeoId"),"geoTypeId","COUNTRY").cache().queryOne();
	            if(UtilValidate.isNotEmpty(geo))
	            	DataImportAccount.put("countryGeoId", (String) context.get("countryGeoId"));
	            else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ACCOUNT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCountryIdError"),"DataImportAccount");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
	            }
   			}
   			else{
   				boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ACCOUNT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCountryIdEmptyError"),"DataImportAccount");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
   			}
   			if(UtilValidate.isNotEmpty((String) context.get("primaryPhoneCountryCode")))
   				DataImportAccount.put("primaryPhoneCountryCode", (String) context.get("primaryPhoneCountryCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("primaryPhoneAreaCode")))
   				DataImportAccount.put("primaryPhoneAreaCode", (String) context.get("primaryPhoneAreaCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("primaryPhoneNumber")))
   				DataImportAccount.put("primaryPhoneNumber", (String) context.get("primaryPhoneNumber"));
   			if(UtilValidate.isNotEmpty((String) context.get("primaryPhoneExtension")))
   				DataImportAccount.put("primaryPhoneExtension", (String) context.get("primaryPhoneExtension"));
   			if(UtilValidate.isNotEmpty((String) context.get("secondaryPhoneCountryCode")))
   				DataImportAccount.put("secondaryPhoneCountryCode", (String) context.get("secondaryPhoneCountryCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("secondaryPhoneAreaCode")))
   				DataImportAccount.put("secondaryPhoneAreaCode", (String) context.get("secondaryPhoneAreaCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("secondaryPhoneNumber")))
   				DataImportAccount.put("secondaryPhoneNumber",(String) context.get("secondaryPhoneNumber"));
   			if(UtilValidate.isNotEmpty((String) context.get("secondaryPhoneExtension")))
   				DataImportAccount.put("secondaryPhoneExtension", (String) context.get("secondaryPhoneExtension"));
   			if(UtilValidate.isNotEmpty((String) context.get("faxCountryCode")))
   				DataImportAccount.put("faxCountryCode", (String) context.get("faxCountryCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("faxAreaCode")))
   				DataImportAccount.put("faxAreaCode", (String) context.get("faxAreaCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("faxNumber")))
   				DataImportAccount.put("faxNumber", (String) context.get("faxNumber"));
   			if(UtilValidate.isNotEmpty((String) context.get("didCountryCode")))
   				DataImportAccount.put("didCountryCode", (String) context.get("didCountryCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("didAreaCode")))
   				DataImportAccount.put("didAreaCode", (String) context.get("didAreaCode"));
   			if(UtilValidate.isNotEmpty((String) context.get("didNumber")))
   				DataImportAccount.put("didNumber", (String) context.get("didNumber"));
   			if(UtilValidate.isNotEmpty((String) context.get("didExtension")))
   				DataImportAccount.put("didExtension", (String) context.get("didExtension"));
   			if(UtilValidate.isNotEmpty((String) context.get("emailAddress")))
   				DataImportAccount.put("emailAddress", (String) context.get("emailAddress"));
   			if(UtilValidate.isNotEmpty((String) context.get("webAddress")))
   				DataImportAccount.put("webAddress", (String) context.get("webAddress"));
   			if(UtilValidate.isNotEmpty((String) context.get("note")))
   				DataImportAccount.put("note", (String) context.get("note"));
   			if(UtilValidate.isNotEmpty((String) context.get("primaryPartyId")))
   				DataImportAccount.put("primaryPartyId", (String) context.get("primaryPartyId"));
   			if(UtilValidate.isNotEmpty((String) context.get("source"))){
            	GenericValue partyIdentificationType = EntityQuery.use(delegator).from("PartyIdentificationType").where("partyIdentificationTypeId", (String) context.get("source")).cache().queryOne();
                if(UtilValidate.isNotEmpty(partyIdentificationType))
                	DataImportAccount.put("source", (String) context.get("source"));
                else{
                	boolean makeError = false;
    				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ACCOUNT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicepartyIdentificationTypeIdEmptyError"),"DataImportAccount");
    				if(makeError)
    					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));	          
                }
            }
   			else
            {
            	boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ACCOUNT",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceinvalidPartyIdentificationTypeIdError"),"DataImportAccount");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
            }
   			delegator.create(DataImportAccount);
   		 
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			return ServiceUtil.returnError(e.toString());
		}


		return ServiceUtil.returnSuccess();
	}
}
