package org.fio.dataimport;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fio.dataimport.SftpUtility;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;


public class InvoiceImportServices {
	
	
	public static Map<String, Object> scheduleInvoiceData(DispatchContext ctx, Map<String, Object> context) {
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try{

			Debug.log("Downloading Remote files to local Started");
			
			GenericValue sftpConfig = EntityQuery.use(delegator).from("SftpConfiguration").where("enable", "Y" ).queryFirst();
            
            String sftpUsername = sftpConfig.getString("userName");
            String sftpPassword = sftpConfig.getString("password");
            String sftpPort = sftpConfig.getString("port");
            String sftpHost = sftpConfig.getString("host");

				int port = 22;

				try{
					port = Integer.parseInt(sftpPort);
				}catch(Exception e){
					Debug.log("Exception in invoice import service===="+e.getMessage());
				}
				SftpUtility sftp1 = new SftpUtility(sftpHost,port, sftpUsername, sftpPassword, "/");
					String sourcePath = UtilProperties.getPropertyValue("config.properties", "invoice.source.location");
					String moveDirPath = UtilProperties.getPropertyValue("config.properties", "invoice.source.move.location");
					String moveDirItemPath = UtilProperties.getPropertyValue("config.properties", "invoiceItem.source.move.location");
					String sourceFolderPath = sourcePath;
					String destFolderPath = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/invoiceHeaderFiles/";
					
					String sourceItemPath = UtilProperties.getPropertyValue("config.properties", "invoiceItem.source.location");
					String sourceItemFolderPath = sourceItemPath;
					String destItemFolderPath = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/invoiceItemFiles/";
					
					
					String moveInvoice = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/invoiceHeaderFiles/proccessed/";
					String moveInvoiceItem = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/invoiceItemFiles/processed/";
					
					Debug.log("destFolderPath++++"+destFolderPath);
					List files = new ArrayList();
					List fileItemList = new ArrayList();
				
					//Copy main List files to local
					List processedFiles = sftp1.readFileFromDir(sourceFolderPath, destFolderPath);
					String moveFile = null;
					Debug.log("processedFiles++++"+processedFiles);
					if(processedFiles.size()>0){
						files.addAll(processedFiles);
						moveFile = (String) processedFiles.get(0);
					}
					
					//Copy main List files to local
					List processedItemFiles = sftp1.readFileFromDir(sourceItemFolderPath, destItemFolderPath);
					String moveInvItmFile = null;
					
					Debug.log("processedItemFiles++++"+processedItemFiles);
					if(processedItemFiles.size()>0){
						fileItemList.addAll(processedItemFiles);
						moveInvItmFile = (String) processedItemFiles.get(0);
					}
	
    		   Debug.log("Downloading finished");
			  //Read CSV invoice header
			   Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin);
			   input.put("fileList",files);
			   input.put("fileItemList",fileItemList);
			   Map<String, Object> result = dispatcher.runSync("importInvoiceHeaderAndItem", input);
			   if(ServiceUtil.isSuccess(result)){
				   boolean res = sftp1.moveFileToDir(destFolderPath+moveFile, moveDirPath, moveFile, true);
				   File bfile = new File(destFolderPath+moveFile);
				   //if(file.renameTo(new File(moveInvoice+moveFile))){
				   Debug.log(bfile.getAbsolutePath());
				   if (bfile.exists()) {
					   bfile.delete();
					}
				   if(bfile.renameTo(new File(moveInvoice + bfile.getName()))){
					     Debug.log("File is moved successful!"); 
			    	   }else{
			    		 Debug.log("File is failed to move!"); 
			    	   }

				   boolean res1 = sftp1.moveFileToDir(destItemFolderPath+moveInvItmFile, moveDirItemPath, moveInvItmFile, true);
				   File afile = new File(destItemFolderPath+moveInvItmFile);
				   if(afile.renameTo(new File(moveInvoiceItem + afile.getName()))){
				   //if(file2.renameTo(new File(moveInvoiceItem+moveInvItmFile))){
					   	Debug.log("File is moved successful!");
			    	   }else{
			    		Debug.log("File is failed to move!"); 
			    	   }
				   
					//Trigger Services
					Map<String, Object> inputNew = new HashMap<String, Object>();
					inputNew.put("userLogin", userLogin);
					dispatcher.runSync("DataImportInvoiceMain",inputNew);
					Debug.log("======service started======"+UtilDateTime.nowTimestamp());
					//invoice header
					/*List<GenericValue> dataImportInvoiceHeaderArchieveList=FastList.newInstance();
					List<GenericValue> dataImportInvoiceHeader = delegator.findAll("DataImportInvoiceHeader", false);
					for(GenericValue gv:dataImportInvoiceHeader){
						GenericValue dataImportInvoiceHeaderarchieve  = delegator.makeValue("DataImportInvoiceHeaderArchieve");
						dataImportInvoiceHeaderarchieve.setPKFields(gv);
						dataImportInvoiceHeaderarchieve.setNonPKFields(gv);
						dataImportInvoiceHeaderArchieveList.add(dataImportInvoiceHeaderarchieve);
					}
					delegator.storeAll(dataImportInvoiceHeaderArchieveList);
					delegator.removeAll("DataImportInvoiceHeader");*/
					TransactionUtil.commit();
					
					//invoice item
					/*List<GenericValue> dataImportInvoiceItemArchieveList=FastList.newInstance();
					List<GenericValue> dataImportInvoiceItem = delegator.findAll("DataImportInvoiceItem", false);
					for(GenericValue gv:dataImportInvoiceItem){
						GenericValue dataImportInvoiceItemarchieve  = delegator.makeValue("DataImportInvoiceItemArchieve");
						dataImportInvoiceItemarchieve.setPKFields(gv);
						dataImportInvoiceItemarchieve.setNonPKFields(gv);
						dataImportInvoiceItemArchieveList.add(dataImportInvoiceItemarchieve);
					}
					delegator.storeAll(dataImportInvoiceItemArchieveList);
					delegator.removeAll("DataImportInvoiceItem");*/
					TransactionUtil.commit();
			   }

			
		
		}catch(Exception e){
			Debug.log("error in invoice import service"+e.getMessage());
		}
		return ServiceUtil.returnSuccess();
		
	}

	public static Map<String, Object> importInvoiceHeaderService(DispatchContext ctx, Map<String, Object> context) {
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<String> fileList = (List) context.get("fileList");
		List<String> fileItemList = (List) context.get("fileItemList");
		String fileName = "";
		if(UtilValidate.isNotEmpty(fileList) && fileList.size()>0){
			fileName = fileList.get(0);
		}
		String invoiceItemFile = "";
		if(UtilValidate.isNotEmpty(fileItemList) && fileItemList.size()>0){
			invoiceItemFile = fileItemList.get(0);
		}
		 //Read invoice header
		 String cvsSplitBy = "\\|";
         String csvFile="";
         String csvFile2 = "";
		try {
			csvFile = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/invoiceHeaderFiles/"+fileName;
			csvFile2 =  ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/invoiceItemFiles/"+invoiceItemFile;
		} catch (ComponentException e1) {
			// TODO Auto-generated catch block
			Debug.log("import Invoice header service error"+e1.getMessage());
		}

		try(BufferedReader reader = new BufferedReader(new FileReader(csvFile));BufferedReader reader2 = new BufferedReader(new FileReader(csvFile2))){
			 int i = 0;

					String line;
					List<GenericValue> dataImportInvoiceList=FastList.newInstance();
					while ((line = reader.readLine()) != null) {
							i++;
							  String[] nextLine = line.split(cvsSplitBy,Integer.MAX_VALUE);
							    
							    //Remove old invoice error log
			    	            /*List<GenericValue> invoiceErrorLog = EntityQuery.use(delegator).from("DataImportLogError").where("taskName", "INVOICE" ).queryList();
			    	            if(invoiceErrorLog.size() > 0)
			    	            delegator.removeAll(invoiceErrorLog);
			    	            TransactionUtil.commit();*/
			    	            
				           		//mapping the data
				           		 if(i>1){
				           			//InvoiceHeader entry to LogError entity By Arshiya
				           			String invoiceId = nextLine[0]  ;
				           			String invoiceTypeId = nextLine[1];
				           			String partyIdFrom = nextLine[4];
				           			String partyId = nextLine[5];
				           			if(UtilValidate.isEmpty(invoiceId) || invoiceId.length()>20 || UtilValidate.isNotEmpty(invoiceId) || UtilValidate.isEmpty(invoiceTypeId) || UtilValidate.isNotEmpty(invoiceTypeId) || UtilValidate.isEmpty(partyIdFrom) || UtilValidate.isNotEmpty(partyId) || UtilValidate.isEmpty(partyId) || UtilValidate.isNotEmpty(partyIdFrom)){
				           				if(UtilValidate.isEmpty(invoiceId)){
											String invoiceId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "invoiceId1");
					           				boolean makeError = false;
					    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+invoiceId1+"","skipped rows are"+" "+i+"","DataImportInvoice");
					    					if(makeError)
											continue;
										}
										
										if(invoiceId.length()>20){
										    String invoiceIdLength = UtilProperties.getPropertyValue("errorUiLabels.properties", "invoiceIdLength");
					           				boolean makeError = false;
					    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+invoiceIdLength+"","skipped rows are"+" "+i+"","DataImportInvoice");
					    					if(makeError)
											continue;
										}
										
										if(UtilValidate.isNotEmpty(invoiceId))
										{
											GenericValue invoice = EntityQuery.use(delegator).from("Invoice").where("invoiceId", invoiceId).queryFirst();
										if(UtilValidate.isNotEmpty(invoice))
										{
											String invoiceerror = UtilProperties.getPropertyValue("errorUiLabels.properties", "invoiceerror");
											boolean makeError = false;
											makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+invoiceerror+"","skipped rows are"+" "+i+"","DataImportInvoice");
					    					if(makeError)
											continue;
										}
										}
										if(UtilValidate.isEmpty(invoiceTypeId))
										{
											String invoiceTypeId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "invoiceTypeId");
											boolean makeError = false;
											makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+invoiceTypeId1+"","skipped rows are"+" "+i+"","DataImportInvoice");
					    					if(makeError)
											continue;
										}
										if(UtilValidate.isNotEmpty(invoiceTypeId))
										{
						           		GenericValue invoiceType = EntityQuery.use(delegator).from("InvoiceType").where("invoiceTypeId", invoiceTypeId).queryFirst();
										if(UtilValidate.isEmpty(invoiceType))
										{
											String invoiceTypeIderror = UtilProperties.getPropertyValue("errorUiLabels.properties", "invoiceTypeIderror");
											boolean makeError = false;
											makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+invoiceTypeIderror+"","skipped rows are"+" "+i+"","DataImportInvoice");
					    					if(makeError)
											continue;
										}
										}
										if(UtilValidate.isEmpty(partyIdFrom))
										{
											String partyIdFrom1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "partyIdFrom");
											boolean makeError = false;
											makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+partyIdFrom1+"","skipped rows are"+" "+i+"","DataImportInvoice");
					    					if(makeError)
											continue;
										}
										if(UtilValidate.isNotEmpty(partyIdFrom))
										{
						           		GenericValue partyFrom = EntityQuery.use(delegator).from("Party").where("partyId", partyIdFrom).queryFirst();
										if(UtilValidate.isEmpty(partyFrom))
										{
											String partyIdFromerror = UtilProperties.getPropertyValue("errorUiLabels.properties", "partyIdFromerror");
											boolean makeError = false;
											makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+partyIdFromerror+"","skipped rows are"+" "+i+"","DataImportInvoice");
					    					if(makeError)
											continue;
										}
										}
										
										if(UtilValidate.isEmpty(partyId))
										{
											String partyId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "partyId");
											boolean makeError = false;
											makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+partyId1+"","skipped rows are"+" "+i+"","DataImportInvoice");
					    					if(makeError)
											continue;
										}
										if(UtilValidate.isNotEmpty(partyId))
										{
						           		GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryFirst();
										if(UtilValidate.isEmpty(party))
										{
											String partyIderror = UtilProperties.getPropertyValue("errorUiLabels.properties", "partyIderror");
											boolean makeError = false;
											makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+partyIderror+"","skipped rows are"+" "+i+"","DataImportInvoice");
					    					if(makeError)
											continue;
										}
										}
				           				
				        			}
				           			
				           			GenericValue dataImportInvoice=delegator.makeValue("DataImportInvoiceHeader");
				         
				           			if(UtilValidate.isNotEmpty(nextLine[0]))
				           			dataImportInvoice.put("invoiceId", nextLine[0]);
				           			if(UtilValidate.isNotEmpty(nextLine[1]))
				           			dataImportInvoice.put("invoiceTypeId", nextLine[1]);
				           			if(UtilValidate.isNotEmpty(nextLine[2]))
				           			dataImportInvoice.put("invoiceDate", Timestamp.valueOf(nextLine[2]));
				           			if(UtilValidate.isNotEmpty(nextLine[3]))
				           			dataImportInvoice.put("currencyUomId", nextLine[3]);
				           			if(UtilValidate.isNotEmpty(nextLine[4]))
				           			dataImportInvoice.put("partyIdFrom", nextLine[4]);
				           			if(UtilValidate.isNotEmpty(nextLine[5]))
				           			dataImportInvoice.put("partyId", nextLine[5]);
				           			if(UtilValidate.isNotEmpty(nextLine[6]))
				           			dataImportInvoice.put("dueDate", Timestamp.valueOf(nextLine[6]));
				           			if(UtilValidate.isNotEmpty(nextLine[7]))
				           			dataImportInvoice.put("description", nextLine[7]);
				           			if(UtilValidate.isNotEmpty(nextLine[8]))
				           			dataImportInvoice.put("referenceNumber", nextLine[8]);
				           			if(UtilValidate.isNotEmpty(nextLine[9]))
				           			dataImportInvoice.put("adjustedAmount", new BigDecimal(nextLine[9]));
				           			if(UtilValidate.isNotEmpty(nextLine[10]))
				           			dataImportInvoice.put("appliedAmount", new BigDecimal(nextLine[10]));
				           			if(UtilValidate.isNotEmpty(nextLine[11]))
				           			dataImportInvoice.put("invoiceTotal", new BigDecimal(nextLine[11]));
				           			if(UtilValidate.isNotEmpty(nextLine[13]))
				           			dataImportInvoice.put("openAmount", new BigDecimal(nextLine[13]));
				           			if(UtilValidate.isNotEmpty(nextLine[12]))
				           			dataImportInvoice.put("paidDate", Timestamp.valueOf(nextLine[12]));
				                    dataImportInvoiceList.add(dataImportInvoice);
				           		 }
	         				
	         			
	         			
	      }
			if(dataImportInvoiceList.size() > 0)
			delegator.storeAll(dataImportInvoiceList);
			TransactionUtil.commit();
			 
			
		        //Read invoice item
			    int ii = 0;
      			String line2;
					List<GenericValue> dataImportInvoiceItemList=FastList.newInstance();
					while ((line2 = reader2.readLine()) != null) {
							ii++;
							String[] nextLine = line2.split(cvsSplitBy);
				           		//mapping the data
				           		 if(ii>1){
				           			//InvoiceItem entry to LogError entity By Arshiya
				           			String invoiceId = nextLine[0] ; 
				           			String invoiceItemSeqId = nextLine[1];
				           			String invoiceItemTypeId = nextLine[2];
				           			String productId = nextLine[6];
				           			if(UtilValidate.isEmpty(invoiceId) || invoiceId.length()>20 || UtilValidate.isNotEmpty(invoiceId) || UtilValidate.isEmpty(invoiceItemSeqId) || UtilValidate.isEmpty(invoiceItemTypeId)|| UtilValidate.isNotEmpty(invoiceItemTypeId) || UtilValidate.isEmpty(productId) || UtilValidate.isNotEmpty(productId)){
				           				if(UtilValidate.isEmpty(invoiceId)){
											String invoiceId2 = UtilProperties.getPropertyValue("errorUiLabels.properties", "invoiceId2");
					           				boolean makeError = false;
					    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+invoiceId2+"","skipped rows are"+" "+i+"","DataImportInvoiceHeader");
					    					if(makeError)
											continue;
										}
										
										if(invoiceId.length()>20){
										    String invoiceIdLength2 = UtilProperties.getPropertyValue("errorUiLabels.properties", "invoiceIdLength2");
					           				boolean makeError = false;
					    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+invoiceIdLength2+"","skipped rows are"+" "+i+"","DataImportInvoiceHeader");
					    					if(makeError)
											continue;
										}
										if(UtilValidate.isNotEmpty(invoiceId))
										{
						           		GenericValue invoice1 = EntityQuery.use(delegator).from("Invoice").where("invoiceId", invoiceId).queryFirst();
										if(UtilValidate.isNotEmpty(invoice1))
										{
											String invoiceerror1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "invoiceerror1");
											boolean makeError = false;
											makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+invoiceerror1+"","skipped rows are"+" "+i+"","DataImportInvoiceHeader");
					    					if(makeError)
											continue;
										}
										}
				           				
										if(UtilValidate.isEmpty(invoiceItemSeqId))
										{
											String invoiceItemSeqId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "invoiceItemSeqId");
					           				boolean makeError = false;
					    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+invoiceItemSeqId1+"","skipped rows are"+" "+i+"","DataImportInvoiceHeader");
					    					if(makeError)
											continue;
										}
										if(UtilValidate.isEmpty(invoiceItemTypeId))
										{
											String invoiceItemTypeId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "invoiceItemTypeId");
					           				boolean makeError = false;
					    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+invoiceItemTypeId1+"","skipped rows are"+" "+i+"","DataImportInvoiceHeader");
					    					if(makeError)
											continue;
										}
										if(UtilValidate.isNotEmpty(invoiceItemTypeId))
										{
					           			GenericValue invoiceItemType = EntityQuery.use(delegator).from("InvoiceItemType").where("invoiceItemTypeId", invoiceItemTypeId).queryFirst();
										if(UtilValidate.isEmpty(invoiceItemType))
										{
											String invoiceItemTypeIderror = UtilProperties.getPropertyValue("errorUiLabels.properties", "invoiceItemTypeIderror");
					           				boolean makeError = false;
					    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+invoiceItemTypeIderror+"","skipped rows are"+" "+i+"","DataImportInvoiceHeader");
					    					if(makeError)
											continue;
										}
										}
										if(UtilValidate.isEmpty(productId))
										{
											String productId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "productId");
					           				boolean makeError = false;
					    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+productId1+"","skipped rows are"+" "+i+"","DataImportInvoiceHeader");
					    					if(makeError)
											continue;
										}
										if(UtilValidate.isNotEmpty(productId))
										{
										GenericValue product = EntityQuery.use(delegator).from("Product").where("productId", productId).queryFirst();
										if(UtilValidate.isEmpty(product))
										{
											String productIderror = UtilProperties.getPropertyValue("errorUiLabels.properties", "productIderror");
					           				boolean makeError = false;
					    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+productIderror+"","skipped rows are"+" "+i+"","DataImportInvoiceHeader");
					    					if(makeError)
											continue;
										}
										}
					        			}
				           			GenericValue dataImportInvoiceItem=delegator.makeValue("DataImportInvoiceItem");
				           			if(UtilValidate.isNotEmpty(nextLine[0]))
				           			dataImportInvoiceItem.put("invoiceId", nextLine[0]);
				           			if(UtilValidate.isNotEmpty(nextLine[1]))
				           			dataImportInvoiceItem.put("invoiceItemSeqId", nextLine[1]);
				           			if(UtilValidate.isNotEmpty(nextLine[2]))
				           			dataImportInvoiceItem.put("invoiceItemTypeId", nextLine[2]);
				           			if(UtilValidate.isNotEmpty(nextLine[3]))
				           			dataImportInvoiceItem.put("amount", new BigDecimal(nextLine[3]));
				           			else
				           				dataImportInvoiceItem.put("amount", BigDecimal.ZERO);
				           			if(UtilValidate.isNotEmpty(nextLine[4]))
				           			dataImportInvoiceItem.put("description", nextLine[4]);
				           			if(UtilValidate.isNotEmpty(nextLine[5]))
				           			dataImportInvoiceItem.put("quantity", new BigDecimal(nextLine[5]));
				           			if(UtilValidate.isNotEmpty(nextLine[6]))
				           			dataImportInvoiceItem.put("productId", nextLine[6]);
				           			dataImportInvoiceItemList.add(dataImportInvoiceItem);
				           		 }
	         		
	         			
	      }
			if(dataImportInvoiceItemList.size() > 0)
			delegator.storeAll(dataImportInvoiceItemList);
			TransactionUtil.commit();
			
		}catch(Exception e){
			Debug.log("error in invoice import service"+e.getMessage());
			return ServiceUtil.returnError(e.toString());
		}
		
		return ServiceUtil.returnSuccess();
		
	}

}