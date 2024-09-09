package org.fio.dataimport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.dataimport.SftpUtility;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

public class UploadSupplierData {

	
	
	public static Map<String, Object> scheduleSupplierData(DispatchContext ctx, Map<String, Object> context) {
		
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
					Debug.log("Exception in upload supplier data===="+e.getMessage());
				}
				SftpUtility sftp1 = new SftpUtility(sftpHost,port, sftpUsername, sftpPassword, "/");
				String sourcePath = UtilProperties.getPropertyValue("config.properties", "supplier.source.location");
				String sourceFolderPath = sourcePath;
				String destFolderPath = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/supplierFiles/";
					
				    Debug.log("destFolderPath++++"+destFolderPath);
					List files = new ArrayList();
				
					//Copy main List files to local
					List processedFiles = sftp1.readFileFromDir(sourceFolderPath, destFolderPath);
					
					Debug.log("processedFiles++++"+processedFiles);
					if(processedFiles.size()>0){
						files.addAll(processedFiles);
					}
					
	
			Debug.log("Downloading finished");
			
			  //Read CSV invoice header
			   Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin);
			   input.put("fileList",files);
			   Map<String, Object> result = dispatcher.runSync("createSupplierService", input);
			   if(ServiceUtil.isSuccess(result)){
					Map<String,Object> inputNew = new HashMap<String,Object>();
					inputNew.put("organizationPartyId","Company");
		   			dispatcher.runSync("importSuppliers",inputNew);
		   			Debug.log("======service started======"+UtilDateTime.nowTimestamp());
		   			List<GenericValue> dataImportSupplierArchieveList=FastList.newInstance();
					List<GenericValue> dataImportSupplier = delegator.findAll("DataImportSupplier", false);
					for(GenericValue gv:dataImportSupplier){
						GenericValue dataImportSupplierarchieve  = delegator.makeValue("DataImportSupplierArchieve");
						dataImportSupplierarchieve.setPKFields(gv);
						dataImportSupplierarchieve.setNonPKFields(gv);
						dataImportSupplierArchieveList.add(dataImportSupplierarchieve);
					}
					delegator.storeAll(dataImportSupplierArchieveList);
					delegator.removeAll("DataImportSupplier");
					TransactionUtil.commit();
			   }

			
		
		}catch(Exception e){
			return ServiceUtil.returnError(e.toString());
		}
		return ServiceUtil.returnSuccess();
		
	}
	public static Map<String, Object>createSupplierService(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		List<String> fileList = (List) context.get("fileList");
		
		String fileName = "";
		if(UtilValidate.isNotEmpty(fileList) && fileList.size()>0){
			fileName = fileList.get(0);
		}
		 String cvsSplitBy = "\\|";
         String csvFile="";
		try {
			csvFile = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/supplierFiles/"+fileName;
		} catch (ComponentException e1) {
			// TODO Auto-generated catch block
			Debug.log("import Invoice header service error"+e1.getMessage());
		}
		
		 try( BufferedReader reader = new BufferedReader(new FileReader(csvFile))){
			
               int i = 0;
               String line;
               List<GenericValue> DataImportSupplierList=FastList.newInstance();
               while ((line = reader.readLine()) != null) {
		    	         i++;
		    	            
		    	         String[] nextLine = line.split(cvsSplitBy,Integer.MAX_VALUE);
		           		 if(i>1){
			           			String supplierId = nextLine[0]  ;
			        			String supplierName = nextLine[1];
			        			String stateProvinceGeoId = nextLine[6];
			        			String countryGeoId = nextLine[10];
			        			
			        			if(UtilValidate.isEmpty(supplierId) || supplierId.length()>20 || UtilValidate.isEmpty(supplierName) || UtilValidate.isEmpty(stateProvinceGeoId) || UtilValidate.isNotEmpty(stateProvinceGeoId) || UtilValidate.isEmpty(countryGeoId) || UtilValidate.isNotEmpty(countryGeoId))
			        			{
			        				
			        				if(UtilValidate.isEmpty(supplierId)){
										String supplierId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "supplierId1");
				           				boolean makeError = false;
				    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"SUPPLIER",""+supplierId1+"","skipped rows are"+" "+i+"","DataImportSupplier");
				    					if(makeError)
										continue;
									}
									
									if(supplierId.length()>20){
									    String supplierIdLength = UtilProperties.getPropertyValue("errorUiLabels.properties", "supplierIdLength");
				           				boolean makeError = false;
				    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"SUPPLIER",""+supplierIdLength+"","skipped rows are"+" "+i+"","DataImportSupplier");
				    					if(makeError)
										continue;
									}
									
									if(UtilValidate.isEmpty(supplierName))
									{
										String supplierName1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "supplierName1");
				           				boolean makeError = false;
				    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"SUPPLIER",""+supplierName1+"","skipped rows are"+" "+i+"","DataImportSupplier");
				    					if(makeError)
										continue;
									}
									if(UtilValidate.isEmpty(stateProvinceGeoId))
									{
										String stateProvinceGeoIderror = UtilProperties.getPropertyValue("errorUiLabels.properties", "stateProvinceGeoIderror");
				           				boolean makeError = false;
				    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"SUPPLIER",""+stateProvinceGeoIderror+"","skipped rows are"+" "+i+"","DataImportSupplier");
				    					if(makeError)
										continue;
									}
									if(UtilValidate.isNotEmpty(stateProvinceGeoId))
									{
										GenericValue state = EntityQuery.use(delegator).from("Geo").where("geoId", stateProvinceGeoId).queryFirst();
										if(UtilValidate.isEmpty(state))
										{
										String stateerror = UtilProperties.getPropertyValue("errorUiLabels.properties", "stateerror");
				           				boolean makeError = false;
				    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"SUPPLIER",""+stateerror+"","skipped rows are"+" "+i+"","DataImportSupplier");
				    					if(makeError)
										continue;
										}
									}
									if(UtilValidate.isEmpty(countryGeoId))
									{
										String countryGeoIderror = UtilProperties.getPropertyValue("errorUiLabels.properties", "countryGeoIderror");
				           				boolean makeError = false;
				    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"SUPPLIER",""+countryGeoIderror+"","skipped rows are"+" "+i+"","DataImportSupplier");
				    					if(makeError)
										continue;
									}
									if(UtilValidate.isNotEmpty(countryGeoId))
									{
										GenericValue country = EntityQuery.use(delegator).from("Geo").where("geoId", countryGeoId).queryFirst();
										if(UtilValidate.isEmpty(country))
										{
										String countryerror = UtilProperties.getPropertyValue("errorUiLabels.properties", "countryerror");
				           				boolean makeError = false;
				    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"SUPPLIER",""+countryerror+"","skipped rows are"+" "+i+"","DataImportSupplier");
				    					if(makeError)
										continue;
										}
									}
			        				
			        			}
			        			GenericValue dataImportSupplier = EntityQuery.use(delegator).from("DataImportSupplier").where("supplierId",supplierId).queryOne();
								if(UtilValidate.isEmpty(dataImportSupplier)){
			           			GenericValue DataImportSupplier=delegator.makeValue("DataImportSupplier");
			           			if(UtilValidate.isNotEmpty(nextLine[0]))
			           			DataImportSupplier.put("supplierId", nextLine[0]);
			           			if(UtilValidate.isNotEmpty(nextLine[1]))
			           			DataImportSupplier.put("supplierName", nextLine[1]);
			           			if(UtilValidate.isNotEmpty(nextLine[2]))
			           			DataImportSupplier.put("attnName", nextLine[2]);
			           			if(UtilValidate.isNotEmpty(nextLine[3]))
			           			DataImportSupplier.put("address1", nextLine[3]);
			           			if(UtilValidate.isNotEmpty(nextLine[4]))
			           			DataImportSupplier.put("address2", nextLine[4]);
			           			if(UtilValidate.isNotEmpty(nextLine[5]))
			           			DataImportSupplier.put("city", nextLine[5]);
			           			if(UtilValidate.isNotEmpty(nextLine[6]))
			           			DataImportSupplier.put("stateProvinceGeoId", nextLine[6]);
			           			if(UtilValidate.isNotEmpty(nextLine[7]))
			           			DataImportSupplier.put("postalCode", nextLine[7]);
			           			if(UtilValidate.isNotEmpty(nextLine[8]))
			           			DataImportSupplier.put("postalCodeExt", nextLine[8]);
			           			if(UtilValidate.isNotEmpty(nextLine[9]))
			           			DataImportSupplier.put("stateProvinceGeoName", nextLine[9]);
			           			if(UtilValidate.isNotEmpty(nextLine[10]))
			           			DataImportSupplier.put("countryGeoId", nextLine[10]);
			           			if(UtilValidate.isNotEmpty(nextLine[11]))
			           			DataImportSupplier.put("primaryPhoneCountryCode", nextLine[11]);
			           			if(UtilValidate.isNotEmpty(nextLine[12]))
			           			DataImportSupplier.put("primaryPhoneAreaCode", nextLine[12]);
			           			if(UtilValidate.isNotEmpty(nextLine[13]))
			           			DataImportSupplier.put("primaryPhoneNumber", nextLine[13]);
			           			if(UtilValidate.isNotEmpty(nextLine[14]))
			           			DataImportSupplier.put("primaryPhoneExtension", nextLine[14]);
			           			if(UtilValidate.isNotEmpty(nextLine[15]))
			           			DataImportSupplier.put("secondaryPhoneCountryCode", nextLine[15]);
			           			if(UtilValidate.isNotEmpty(nextLine[16]))
			           			DataImportSupplier.put("secondaryPhoneAreaCode", nextLine[16]);
			           			if(UtilValidate.isNotEmpty(nextLine[17]))
			           			DataImportSupplier.put("secondaryPhoneNumber", nextLine[17]);
			           			if(UtilValidate.isNotEmpty(nextLine[18]))
			           			DataImportSupplier.put("secondaryPhoneExtension", nextLine[18]);
			           			if(UtilValidate.isNotEmpty(nextLine[19]))
			           			DataImportSupplier.put("faxCountryCode", nextLine[19]);
			           			if(UtilValidate.isNotEmpty(nextLine[20]))
			           			DataImportSupplier.put("faxAreaCode", nextLine[20]);
			           			if(UtilValidate.isNotEmpty(nextLine[21]))
			           			DataImportSupplier.put("faxNumber", nextLine[21]);
			           			if(UtilValidate.isNotEmpty(nextLine[22]))
			           			DataImportSupplier.put("didCountryCode", nextLine[22]);
			           			if(UtilValidate.isNotEmpty(nextLine[23]))
			           			DataImportSupplier.put("didAreaCode", nextLine[23]);
			           			if(UtilValidate.isNotEmpty(nextLine[24]))
			           			DataImportSupplier.put("didNumber", nextLine[24]);
			           			if(UtilValidate.isNotEmpty(nextLine[25]))
			           			DataImportSupplier.put("didExtension", nextLine[25]);
			           			if(UtilValidate.isNotEmpty(nextLine[26]))
			           			DataImportSupplier.put("emailAddress", nextLine[26]);
			           			if(UtilValidate.isNotEmpty(nextLine[27]))
			           			DataImportSupplier.put("webAddress", nextLine[27]);
			           			if(UtilValidate.isNotEmpty(nextLine[28]))
			           			DataImportSupplier.put("note", nextLine[28]);
			           			if(UtilValidate.isNotEmpty(nextLine[29]))
			           			DataImportSupplier.put("netPaymentDays", Long.parseLong(nextLine[29]));
			           			if(UtilValidate.isNotEmpty(nextLine[30]))
			           			DataImportSupplier.put("isIncorporated", nextLine[30]);
			           			if(UtilValidate.isNotEmpty(nextLine[31]))
			           			DataImportSupplier.put("federalTaxId", nextLine[31]);
			           			if(UtilValidate.isNotEmpty(nextLine[32]))
			           			DataImportSupplier.put("requires1099", nextLine[32]);
			           			if(UtilValidate.isNotEmpty(nextLine[33]))
			           			DataImportSupplier.put("processedTimestamp", Timestamp.valueOf(nextLine[33]));
			           			if(UtilValidate.isNotEmpty(nextLine[34]))
			           			DataImportSupplier.put("primaryPartyId", nextLine[34]);
			           			DataImportSupplierList.add(DataImportSupplier);
			           		 }
		           		 }
       			
       		}	
            if(DataImportSupplierList.size() > 0)
			delegator.storeAll(DataImportSupplierList);
			TransactionUtil.commit();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Debug.log("Exception in upload supplier data===="+e.getMessage());
			return ServiceUtil.returnError(e.toString());
		}

		return ServiceUtil.returnSuccess();
	}
	
	public static boolean dataImportError(Delegator delegator,String errorType , String logMsg , String logMsg1, String tableName) throws GenericEntityException{
		
		GenericValue makeError = delegator.makeValue("DataImportLogError");
		makeError.put("seqId",delegator.getNextSeqId("DataImportLogError"));
		makeError.put("taskId", delegator.getNextSeqId("DataImportLogError"));
		makeError.put("taskName", errorType);
		makeError.put("timeStamp", UtilDateTime.nowTimestamp());
		makeError.put("status", "ERROR");
		makeError.put("logMsg1", logMsg);
		makeError.put("logMsg2", logMsg1);
		makeError.put("tableName", tableName);
		makeError.create();
		TransactionUtil.commit();
		
		if(makeError.size()>0)
			return true;
		else
			return false;
		
	}

}

