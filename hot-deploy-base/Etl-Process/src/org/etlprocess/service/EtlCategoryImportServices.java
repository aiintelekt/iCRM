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
import org.groupfio.etl.process.job.CategoryImportJob;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
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
import javolution.util.FastMap;

public class EtlCategoryImportServices {
	
	private static String MODULE = EtlCategoryImportServices.class.getName();
	private static String errorLog ="";
	public static String currentListId = "";
	public static String accessType = UtilProperties.getPropertyValue("Etl-Process", "UPLOAD_TYPE");
	public static String etlTableName = UtilProperties.getPropertyValue("Etl-Process", "CATEGORY_TABLE");
	
	
	@SuppressWarnings("resource")
	public static String uploadCategoryLoadService(HttpServletRequest request, HttpServletResponse response,String processId) throws GenericEntityException, ComponentException {
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
           					Map<String,Object> result = dispatcher.runSync("CreateEtlCategoryLoad", context);
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

			Map<String,Object> inputNew = new HashMap<String,Object>();
   			inputNew.put("userLogin",userLogin);
   			inputNew.put("batchId",batchId);
   			inputNew.put("modelId",listId);
   			inputNew.put("accessType",accessType);
   			inputNew.put("etlTableName",etlTableName);
   			Map<String,Object> Res = dispatcher.runSync("EtlSelfServiceProcessor", inputNew);
   			
   			if(ServiceUtil.isSuccess(Res)){
   				// Trigger Thread
   				CategoryImportJob job = new CategoryImportJob();
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
	public static Map<String, Object>CreateEtlCategoryLoadService(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();

		try {
			currentListId = (String)context.get("listId");
			org.etlprocess.service.EtlImportServices.currentListId = currentListId;

   			GenericValue DataImportCategory = delegator.makeValue("DataImportCategory");
   			
   			
   			DataImportCategory.put("batchId", (String) context.get("batchId"));
   					   				
   			if(UtilValidate.isNotEmpty((String) context.get("categoryId"))){
   				/*GenericValue productCategory = EntityQuery.use(delegator).from("ProductCategory").where("productCategoryId", (String) context.get("categoryId")).cache().queryOne();
   				if(UtilValidate.isEmpty(productCategory))*/
   				DataImportCategory.put("categoryId", (String) context.get("categoryId"));
   				/*else{
   					boolean makeError = false;
   					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CATEGORY",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceInvalidCategoryIdError"),"DataImportCategory");
   					if(makeError)
   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
   				}*/
   			}
   			else{
				boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CATEGORY",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCategoryIdError"),"DataImportCategory");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
			}
   			if(UtilValidate.isNotEmpty((String) context.get("productCategoryTypeId"))){
   				GenericValue productCategoryType = EntityQuery.use(delegator).from("ProductCategoryType").where("productCategoryTypeId", (String) context.get("productCategoryTypeId")).cache().queryOne();
	            if(UtilValidate.isNotEmpty(productCategoryType))
	            	DataImportCategory.put("productCategoryTypeId", (String) context.get("productCategoryTypeId"));          	
	            else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CATEGORY",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductCategoryIdError1"),"DataImportCategory");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
	            }
   				
   			}
   			else{
				boolean makeError = false;
				makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"CATEGORY",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceProductCategoryIdError"),"DataImportCategory");
				if(makeError)
					return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));						
   			}
   			if(UtilValidate.isNotEmpty((String) context.get("primaryParentCategoryId")))
   				DataImportCategory.put("primaryParentCategoryId", (String) context.get("primaryParentCategoryId"));
   			if(UtilValidate.isNotEmpty((String) context.get("categoryName")))
   				DataImportCategory.put("categoryName", (String) context.get("categoryName"));
   			if(UtilValidate.isNotEmpty((String) context.get("description")))
   				DataImportCategory.put("description", (String) context.get("description"));
   			if(UtilValidate.isNotEmpty((String) context.get("longDescription")))
   				DataImportCategory.put("longDescription", (String) context.get("longDescription"));

   			if(UtilValidate.isNotEmpty((String) context.get("categoryImageUrl")))
   				DataImportCategory.put("categoryImageUrl", (String) context.get("categoryImageUrl"));
   			if(UtilValidate.isNotEmpty((String) context.get("linkOneImageUrl")))
   				DataImportCategory.put("linkOneImageUrl", (String) context.get("linkOneImageUrl"));
   			if(UtilValidate.isNotEmpty((String) context.get("linkTwoImageUrl")))
   				DataImportCategory.put("linkTwoImageUrl", (String) context.get("linkTwoImageUrl"));
   			
   			if(UtilValidate.isNotEmpty((String) context.get("detailScreen")))
   				DataImportCategory.put("detailScreen", (String) context.get("detailScreen"));
   			if(UtilValidate.isNotEmpty((String) context.get("showInSelect")))
   				DataImportCategory.put("showInSelect", (String) context.get("showInSelect"));
   			TransactionUtil.begin();
   			DataImportCategory.create();
   			//delegator.create(DataImportCategory);
   			TransactionUtil.commit();
   		 
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			return ServiceUtil.returnError(e.toString());
		}


		return ServiceUtil.returnSuccess();
	}
}
