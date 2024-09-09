package org.fio.dataimport;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.fio.dataimport.SftpUtility;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class CustomerImportServices {
	
	
	public static Map<String, Object>importCustomerData(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
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
					Debug.log("Exception in customer import===="+e.getMessage());
				}
				SftpUtility sftp1 = new SftpUtility(sftpHost,port, sftpUsername, sftpPassword, "/");
				 String sourcePath = UtilProperties.getPropertyValue("config.properties", "customer.source.location");
					String sourceFolderPath = sourcePath;
					
					String destFolderPath = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/customerFiles/";
					Debug.log("destFolderPath++++"+destFolderPath);
					List<String> files = new ArrayList();
				
					//Copy main List files to local
					List processedFiles = sftp1.readFileFromDir(sourceFolderPath, destFolderPath);
					
					Debug.log("processedFiles++++"+processedFiles);
					if(processedFiles.size()>0){
						files.addAll(processedFiles);
					}
					Map<String, Object> input = FastMap.newInstance();
					input.put("userLogin", userLogin);
					input.put("fileList", files);
					Map<String, Object> result = dispatcher.runSync("createCustomerService",input);
					Debug.log("Downloading finished");
					   if(ServiceUtil.isSuccess(result)){
						   
				   			Map<String,Object> inputNew = new HashMap<String,Object>();
				   			inputNew.put("initialResponsiblePartyId","admin");
							inputNew.put("userLogin",userLogin);
				   			inputNew.put("organizationPartyId","Company");
				   			dispatcher.runSync("importCustomers",inputNew); 
				   			
				   			List<GenericValue> dataImportCustomerArchieveList=FastList.newInstance();
							List<GenericValue> dataImportCustomer = delegator.findAll("DataImportCustomer", false);
							/*for(GenericValue gv:dataImportCustomer){
								GenericValue dataImportCustomerarchieve  = delegator.makeValue("DataImportCustomerArchieve");
								dataImportCustomerarchieve.setPKFields(gv);
								dataImportCustomerarchieve.setNonPKFields(gv);
								dataImportCustomerArchieveList.add(dataImportCustomerarchieve);
							}
							delegator.storeAll(dataImportCustomerArchieveList);
							delegator.removeAll("DataImportCustomer");*/
							TransactionUtil.commit();
					   }	
		
		}catch(Exception e){
			return ServiceUtil.returnError(e.toString());
		}
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object>createCustomerService(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		List<String> fileList = (List) context.get("fileList");
		
		String cvsSplitBy = "\\|";
		 
		String fileName = "";
		if(UtilValidate.isNotEmpty(fileList) && fileList.size()>0){
			fileName = fileList.get(0);
		}
       String csvFile = "";
	   try {
		csvFile = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/customerFiles/"+fileName;
	   } catch (ComponentException e1) {
		   Debug.log("import Invoice header service error"+e1.getMessage());
	   }
	   try( BufferedReader reader = new BufferedReader(new FileReader(csvFile))){
               int i = 0;
               String line;
               List<GenericValue> dataImportCustomerList=FastList.newInstance();
               while ((line = reader.readLine()) != null) {
		    	            i++;
		    	            //Remove old customer error log
		    	            /*List<GenericValue> customerErrorLog = EntityQuery.use(delegator).from("DataImportLogError").where("taskName", "CUSTOMER" ).queryList();
		    	            if(customerErrorLog.size() > 0)
		    	            delegator.removeAll(customerErrorLog);
		    	            TransactionUtil.commit();*/
		    	            String[] nextLine = line.split(cvsSplitBy,Integer.MAX_VALUE);
		    	            
					           		//mapping the data
					           		 if(i>1){
					           			String customerId = nextLine[0];
					           			String firstName = nextLine[2];
					           			String lastName = nextLine[3];
					           			String stateProvinceGeoId = nextLine[8];
					           			String countryGeoId = nextLine[11];
					           			if(UtilValidate.isEmpty(customerId) || customerId.length()>20 || UtilValidate.isEmpty(firstName) || UtilValidate.isEmpty(lastName) ||  UtilValidate.isEmpty(stateProvinceGeoId) || UtilValidate.isNotEmpty(stateProvinceGeoId) || UtilValidate.isEmpty(countryGeoId) || UtilValidate.isNotEmpty(countryGeoId)){
					           				if(UtilValidate.isEmpty(customerId)){
												String customerId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "customerId1");
						           				boolean makeError = false;
						    					makeError = dataImportError(delegator,"CUSTOMER",""+customerId1+"","skipped rows are"+" "+i+"","DataImportCustomer");
						    					if(makeError)
												continue;
											}
											
											if(customerId.length()>20){
											    String customerIdLength = UtilProperties.getPropertyValue("errorUiLabels.properties", "customerIdLength");
						           				boolean makeError = false;
						    					makeError = dataImportError(delegator,"CUSTOMER",""+customerIdLength+"","skipped rows are"+" "+i+"","DataImportCustomer");
						    					if(makeError)
												continue;
											}
											
											if(UtilValidate.isEmpty(firstName))
											{
												String firstName1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "firstName1");
						           				boolean makeError = false;
						    					makeError = dataImportError(delegator,"CUSTOMER",""+firstName1+"","skipped rows are"+" "+i+"","DataImportCustomer");
						    					if(makeError)
												continue;
											}
											if(UtilValidate.isEmpty(lastName))
											{
												String lastName1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "lastName1");
						           				boolean makeError = false;
						    					makeError = dataImportError(delegator,"CUSTOMER",""+lastName1+"","skipped rows are"+" "+i+"","DataImportCustomer");
						    					if(makeError)
												continue;
											}
											if(UtilValidate.isEmpty(stateProvinceGeoId))
											{
												String stateProvinceGeoIderror1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "stateProvinceGeoIderror1");
						           				boolean makeError = false;
						    					makeError = dataImportError(delegator,"CUSTOMER",""+stateProvinceGeoIderror1+"","skipped rows are"+" "+i+"","DataImportCustomer");
						    					if(makeError)
												continue;
											}
											if(UtilValidate.isNotEmpty(stateProvinceGeoId))
											{
												GenericValue state = EntityQuery.use(delegator).from("Geo").where("geoId", stateProvinceGeoId).queryFirst();
												if(UtilValidate.isEmpty(state))
												{
												String stateerror1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "stateerror1");
						           				boolean makeError = false;
						    					makeError = dataImportError(delegator,"CUSTOMER",""+stateerror1+"","skipped rows are"+" "+i+"","DataImportCustomer");
						    					if(makeError)
												continue;
												}
											}
											if(UtilValidate.isEmpty(countryGeoId))
											{
												String countryGeoIderror1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "countryGeoIderror1");
						           				boolean makeError = false;
						    					makeError = dataImportError(delegator,"CUSTOMER",""+countryGeoIderror1+"","skipped rows are"+" "+i+"","DataImportCustomer");
						    					if(makeError)
												continue;
											}
											if(UtilValidate.isNotEmpty(countryGeoId))
											{
												GenericValue country = EntityQuery.use(delegator).from("Geo").where("geoId", countryGeoId).queryFirst();
												if(UtilValidate.isEmpty(country))
												{
												String countryerror1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "countryerror1");
						           				boolean makeError = false;
						    					makeError = dataImportError(delegator,"CUSTOMER",""+countryerror1+"","skipped rows are"+" "+i+"","DataImportCustomer");
						    					if(makeError)
												continue;
												}
											}
					           				
					    				}
										GenericValue dataImport = EntityQuery.use(delegator).from("DataImportCustomer").where("customerId",customerId).queryOne();
										if(UtilValidate.isEmpty(dataImport)){
					           			GenericValue dataImportCustomer=delegator.makeValue("DataImportCustomer");
					           			if(UtilValidate.isNotEmpty(nextLine[0]))
					           			dataImportCustomer.put("customerId", nextLine[0]);
					           			if(UtilValidate.isNotEmpty(nextLine[1]))
					                    dataImportCustomer.put("companyName", nextLine[1]);
					           			if(UtilValidate.isNotEmpty(nextLine[2]))
					                    dataImportCustomer.put("firstName", nextLine[2]);
					           			if(UtilValidate.isNotEmpty(nextLine[3]))
					                    dataImportCustomer.put("lastName", nextLine[3]);
					           			if(UtilValidate.isNotEmpty(nextLine[4]))
					                    dataImportCustomer.put("attnName", nextLine[4]);
					           			if(UtilValidate.isNotEmpty(nextLine[5]))
					                    dataImportCustomer.put("address1", nextLine[5]);
					           			if(UtilValidate.isNotEmpty(nextLine[6]))
					                    dataImportCustomer.put("address2", nextLine[6]);
					           			if(UtilValidate.isNotEmpty(nextLine[7]))
					                    dataImportCustomer.put("city", nextLine[7]);
					           			if(UtilValidate.isNotEmpty(nextLine[8]))
					                    dataImportCustomer.put("stateProvinceGeoId", nextLine[8]);
					           			if(UtilValidate.isNotEmpty(nextLine[9]))
					                    dataImportCustomer.put("postalCode", nextLine[9]);
					           			if(UtilValidate.isNotEmpty(nextLine[10]))
					                    dataImportCustomer.put("postalCodeExt", nextLine[10]);
					           			if(UtilValidate.isNotEmpty(nextLine[11]))
					                    dataImportCustomer.put("countryGeoId", nextLine[11]);
					           			if(UtilValidate.isNotEmpty(nextLine[12]))
					                    dataImportCustomer.put("primaryPhoneCountryCode", nextLine[12]);
					           			if(UtilValidate.isNotEmpty(nextLine[13]))
					                    dataImportCustomer.put("primaryPhoneAreaCode", nextLine[13]);
					           			if(UtilValidate.isNotEmpty(nextLine[14]))
					                    dataImportCustomer.put("primaryPhoneNumber", nextLine[14]);
					           			if(UtilValidate.isNotEmpty(nextLine[15]))
					                    dataImportCustomer.put("primaryPhoneExtension", nextLine[15]);
					           			if(UtilValidate.isNotEmpty(nextLine[16]))
					                    dataImportCustomer.put("secondaryPhoneCountryCode", nextLine[16]);
					           			if(UtilValidate.isNotEmpty(nextLine[17]))
					                    dataImportCustomer.put("secondaryPhoneAreaCode", nextLine[17]);
					           			if(UtilValidate.isNotEmpty(nextLine[18]))
					                    dataImportCustomer.put("secondaryPhoneNumber", nextLine[18]);
					           			if(UtilValidate.isNotEmpty(nextLine[19]))
					                    dataImportCustomer.put("secondaryPhoneExtension", nextLine[19]);
					           			if(UtilValidate.isNotEmpty(nextLine[20]))
					                    dataImportCustomer.put("faxCountryCode", nextLine[20]);
					           			if(UtilValidate.isNotEmpty(nextLine[21]))
					                    dataImportCustomer.put("faxAreaCode", nextLine[21]);
					           			if(UtilValidate.isNotEmpty(nextLine[22]))
					                    dataImportCustomer.put("faxNumber", nextLine[22]);
					           			if(UtilValidate.isNotEmpty(nextLine[23]))
					                    dataImportCustomer.put("didCountryCode", nextLine[23]);
					           			if(UtilValidate.isNotEmpty(nextLine[24]))
					                    dataImportCustomer.put("didAreaCode", nextLine[24]);
					           			if(UtilValidate.isNotEmpty(nextLine[25]))
					                    dataImportCustomer.put("didNumber", nextLine[25]);
					           			if(UtilValidate.isNotEmpty(nextLine[26]))
					                    dataImportCustomer.put("didExtension", nextLine[26]);
					           			if(UtilValidate.isNotEmpty(nextLine[27]))
					                    dataImportCustomer.put("emailAddress", nextLine[27]);
					           			if(UtilValidate.isNotEmpty(nextLine[28]))
					                    dataImportCustomer.put("webAddress", nextLine[28]);
					                    if(UtilValidate.isNotEmpty(nextLine[29]))
					                    dataImportCustomer.put("discount", new BigDecimal(nextLine[29]));
					                    if(UtilValidate.isNotEmpty(nextLine[30]))
					                    dataImportCustomer.put("partyClassificationTypeId", nextLine[30]);
					                    if(UtilValidate.isNotEmpty(nextLine[31]))
					                    dataImportCustomer.put("creditCardNumber", nextLine[31]);
					                    if(UtilValidate.isNotEmpty(nextLine[32]))
					                    dataImportCustomer.put("creditCardExpDate", nextLine[32]);
					                    if(UtilValidate.isNotEmpty(nextLine[33]))
					                    dataImportCustomer.put("outstandingBalance", new BigDecimal(nextLine[33]));
					                    if(UtilValidate.isNotEmpty(nextLine[34]))
					                    dataImportCustomer.put("creditLimit", new BigDecimal(nextLine[34]));
					                    if(UtilValidate.isNotEmpty(nextLine[35]))
					                    dataImportCustomer.put("currencyUomId", nextLine[35]);
					                    if(UtilValidate.isNotEmpty(nextLine[36]))
					                    dataImportCustomer.put("disableShipping", nextLine[36]);
					                    if(UtilValidate.isNotEmpty(nextLine[37]))
					                    dataImportCustomer.put("netPaymentDays", Long.parseLong(nextLine[37]));
					                    if(UtilValidate.isNotEmpty(nextLine[38]))
					                    dataImportCustomer.put("shipToCompanyName", nextLine[38]);
					                    if(UtilValidate.isNotEmpty(nextLine[39]))
					                    dataImportCustomer.put("shipToFirstName", nextLine[39]);
					                    if(UtilValidate.isNotEmpty(nextLine[40]))
					                    dataImportCustomer.put("shipToLastName", nextLine[40]);
					                    if(UtilValidate.isNotEmpty(nextLine[41]))
					                    dataImportCustomer.put("shipToAttnName", nextLine[41]);
					                    if(UtilValidate.isNotEmpty(nextLine[42]))
					                    dataImportCustomer.put("shipToAddress1", nextLine[42]);
					                    if(UtilValidate.isNotEmpty(nextLine[43]))
					                    dataImportCustomer.put("shipToAddress2", nextLine[43]);
					                    if(UtilValidate.isNotEmpty(nextLine[44]))
					                    dataImportCustomer.put("shipToCity", nextLine[44]);
					                    if(UtilValidate.isNotEmpty(nextLine[45]))
					                    dataImportCustomer.put("shipToStateProvinceGeoId", nextLine[45]);
					                    if(UtilValidate.isNotEmpty(nextLine[46]))
					                    dataImportCustomer.put("shipToPostalCode", nextLine[46]);
					                    if(UtilValidate.isNotEmpty(nextLine[47]))
					                    dataImportCustomer.put("shipToPostalCodeExt", nextLine[47]);
					                    if(UtilValidate.isNotEmpty(nextLine[48]))
					                    dataImportCustomer.put("shipToStateProvGeoName", nextLine[48]);
					                    if(UtilValidate.isNotEmpty(nextLine[49]))
					                    dataImportCustomer.put("shipToCountryGeoId", nextLine[49]);
					                    if(UtilValidate.isNotEmpty(nextLine[50]))
					                    dataImportCustomer.put("note", nextLine[50]);
					                    if(UtilValidate.isNotEmpty(nextLine[51]))
					                    dataImportCustomer.put("processedTimestamp", Timestamp.valueOf(nextLine[51]));
					                    if(UtilValidate.isNotEmpty(nextLine[52]))
					                    dataImportCustomer.put("primaryPartyId", nextLine[52]);
					                    if(UtilValidate.isNotEmpty(nextLine[53]))
					                    dataImportCustomer.put("companyPartyId", nextLine[53]);
					                    if(UtilValidate.isNotEmpty(nextLine[54]))
					                    dataImportCustomer.put("personPartyId", nextLine[54]);
					                    dataImportCustomerList.add(dataImportCustomer);
					           		 }
					           		 }
		           			
		        }
                if(dataImportCustomerList.size() > 0)
	   			delegator.storeAll(dataImportCustomerList);
		 }catch(Exception e){
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
