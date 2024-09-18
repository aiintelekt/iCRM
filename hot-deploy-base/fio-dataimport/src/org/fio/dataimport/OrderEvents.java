package org.fio.dataimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class OrderEvents {
public static Map<String, Object> scheduleOrderData(DispatchContext ctx, Map<String, Object> context) {
		
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
					Debug.log("Exception in order events===="+e.getMessage());
				}
				SftpUtility sftp1 = new SftpUtility(sftpHost,port, sftpUsername, sftpPassword, "/");
					String sourcePath = UtilProperties.getPropertyValue("config.properties", "order.source.location");
					String moveDirPath = UtilProperties.getPropertyValue("config.properties", "order.source.move.location");
					String moveDirItemPath = UtilProperties.getPropertyValue("config.properties", "orderItem.source.move.location");
					String sourceFolderPath = sourcePath;
					String destFolderPath = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/orderHeaderFiles/";
					
					String sourceItemPath = UtilProperties.getPropertyValue("config.properties", "orderItem.source.location");
					String sourceItemFolderPath = sourceItemPath;
					String destItemFolderPath = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/orderItemFiles/";
					
					
					String moveInvoice = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/orderHeaderFiles/proccessed/";
					String moveInvoiceItem = ComponentConfig.getRootLocation("fio-dataimport")+"webapp/fio-dataimport/orderItemFiles/processed/";
					
					Debug.log("destFolderPath++++"+destFolderPath);
					List files = new ArrayList();
				
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
						files.addAll(processedItemFiles);
						moveInvItmFile = (String) processedItemFiles.get(0);
					}
	
			Debug.log("Downloading finished");
			  //Read CSV invoice header
			   Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin);
			   Map<String, Object> result = dispatcher.runSync("importOrderHeaderAndItem", input);
			   if(ServiceUtil.isSuccess(result)){
				   boolean res = sftp1.moveFileToDir(destFolderPath+moveFile, moveDirPath, moveFile, true);
				   File bfile = new File(destFolderPath+moveFile);
				   //if(file.renameTo(new File(moveInvoice+moveFile))){
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
					inputNew.put("importEmptyOrders", "");
					inputNew.put("calculateGrandTotal", "");
					inputNew.put("companyPartyId", "Company");
					dispatcher.runSync("importOrders",inputNew);
					Debug.log("======service started======"+UtilDateTime.nowTimestamp());
			        
					//order header
					List<GenericValue> dataImportOrderHeaderArchieveList=FastList.newInstance();
					List<GenericValue> dataImportOrderHeader = delegator.findAll("DataImportOrderHeader", false);
					/*for(GenericValue gv:dataImportOrderHeader){
						GenericValue dataImportOrderHeaderarchieve  = delegator.makeValue("DataImportOrderHeaderArchieve");
						dataImportOrderHeaderarchieve.setPKFields(gv);
						dataImportOrderHeaderarchieve.setNonPKFields(gv);
						dataImportOrderHeaderArchieveList.add(dataImportOrderHeaderarchieve);
					}
					delegator.storeAll(dataImportOrderHeaderArchieveList);
					delegator.removeAll("DataImportOrderHeader");*/
					TransactionUtil.commit();
					
					//order payment
					List<GenericValue> dataImportOrderPaymentArchieveList=FastList.newInstance();
					List<GenericValue> dataImportOrderPayment = delegator.findAll("DataImportOrderPayment", false);
					/*for(GenericValue gv:dataImportOrderPayment){
						GenericValue dataImportOrderPaymentarchieve  = delegator.makeValue("DataImportOrderPaymentArchieve");
						dataImportOrderPaymentarchieve.setPKFields(gv);
						dataImportOrderPaymentarchieve.setNonPKFields(gv);
						dataImportOrderPaymentArchieveList.add(dataImportOrderPaymentarchieve);
					}
					delegator.storeAll(dataImportOrderPaymentArchieveList);
					delegator.removeAll("DataImportOrderPayment");*/
					TransactionUtil.commit();
					
					//order item
					List<GenericValue> dataImportOrderItemArchieveList=FastList.newInstance();
					List<GenericValue> dataImportOrderItem = delegator.findAll("DataImportOrderItem", false);
					/*for(GenericValue gv:dataImportOrderItem){
						GenericValue dataImportOrderItemarchieve  = delegator.makeValue("DataImportOrderItemArchieve");
						dataImportOrderItemarchieve.setPKFields(gv);
						dataImportOrderItemarchieve.setNonPKFields(gv);
						dataImportOrderItemArchieveList.add(dataImportOrderItemarchieve);
					}
					delegator.storeAll(dataImportOrderItemArchieveList);
					delegator.removeAll("DataImportOrderItem");*/
					TransactionUtil.commit();
			   }

			
		
		}catch(Exception e){
			Debug.log("error"+e.getMessage());
		}
		return ServiceUtil.returnSuccess();
		
	}

public static Map<String, Object> importOrderHeaderAndItemService(DispatchContext ctx, Map<String, Object> context) {
	
	long startTime = System.currentTimeMillis();
	LocalDispatcher dispatcher = ctx.getDispatcher();
	Delegator delegator = ctx.getDelegator();
	GenericValue userLogin = (GenericValue) context.get("userLogin");
	try{
		String delimiter = UtilProperties.getPropertyValue("fio-dataimport", "csvDelimiter");
		String filePath = ComponentConfig.getRootLocation("fio-dataimport")+File.separatorChar+"webapp"+File.separatorChar+"fio-dataimport"+File.separatorChar+"orderHeaderFiles"+File.separatorChar;
		File folder = new File(filePath);
		File[] files = folder.listFiles();
		
		if(UtilValidate.isEmpty(delimiter)){
			delimiter = "\\|";
		}
		
		//Read Order header
		if(UtilValidate.isNotEmpty(files)){
			for(File file : files){
				
				if(file.isFile()){
					int i = 1;
					try(BufferedReader reader = new BufferedReader(new FileReader(file))){
					String line;
					// read the header line in the file
					reader.readLine();
					List<GenericValue> dataImportOrderHeaderList=FastList.newInstance();
					List<GenericValue> dataImportOrderPaymentList=FastList.newInstance();
					readNext:while ((line = reader.readLine()) != null && !line.isEmpty()) {
						i++;
						
						//Remove old order error log
		    	     /*   List<GenericValue> orderErrorLog = EntityQuery.use(delegator).from("DataImportLogError").where("taskName", "ORDER" ).queryList();
		    	        if(orderErrorLog.size() > 0)
		    	        delegator.removeAll(orderErrorLog);
		    	        TransactionUtil.commit();*/
		    	         
						String[] record = line.split(delimiter,Integer.MAX_VALUE);
						if(record.length != 0){
							String orderId = StringUtils.defaultIfEmpty((String) record[0],"");
							String orderTypeId = StringUtils.defaultIfEmpty((String) record[1], "");
							String customerPartyId = StringUtils.defaultIfEmpty((String) record[2], "");
							String supplierPartyId = StringUtils.defaultIfEmpty((String) record[3], "");
							String orderPaymentPreferenceId = StringUtils.defaultIfEmpty((String) record[37], "");
							String paymentMethodTypeId = StringUtils.defaultIfEmpty((String) record[38], "");
							
							if(UtilValidate.isEmpty(orderId) || orderId.length()>20 || UtilValidate.isEmpty(orderTypeId) || UtilValidate.isEmpty(orderPaymentPreferenceId) || orderPaymentPreferenceId.length()>20 ||  UtilValidate.isNotEmpty(paymentMethodTypeId) || UtilValidate.isEmpty(paymentMethodTypeId) || UtilValidate.isEmpty(customerPartyId) || UtilValidate.isNotEmpty(customerPartyId) || UtilValidate.isEmpty(supplierPartyId) || UtilValidate.isNotEmpty(supplierPartyId)){
								
								if(UtilValidate.isEmpty(orderId)){
									String orderId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "orderId1");
			           				boolean makeError = false;
			    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+orderId1+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;
								}
								
								if(orderId.length()>20){
								    String orderIdLength = UtilProperties.getPropertyValue("errorUiLabels.properties", "orderIdLength");
			           				boolean makeError = false;
			    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+orderIdLength+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;
								}
								
								if(UtilValidate.isEmpty(orderTypeId))
								{
									String orderTypeerror = UtilProperties.getPropertyValue("errorUiLabels.properties", "orderTypeerror");
									boolean makeError = false;
									makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+orderTypeerror+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;
								}
								
								if(UtilValidate.isNotEmpty(orderTypeId))
								{
								GenericValue orderTypeerror = EntityQuery.use(delegator).from("OrderType").where(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, orderTypeId)).queryFirst();
								if(UtilValidate.isEmpty(orderTypeerror))
								{
									String ordertypeerrormsg = UtilProperties.getPropertyValue("errorUiLabels.properties", "ordertypeerrormsg");
									boolean makeError = false;
									makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+ordertypeerrormsg+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;	
								}
								}
								if(UtilValidate.isEmpty(customerPartyId))
								{
									String customerPartyId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "customerPartyId");
									boolean makeError = false;
									makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+customerPartyId1+"","skipped rows are"+" "+i+"","DataImportInvoice");
			    					if(makeError)
									continue;
								}
								if(UtilValidate.isNotEmpty(customerPartyId))
								{
				           		GenericValue customerPartyId1 = EntityQuery.use(delegator).from("Party").where("partyId", customerPartyId).queryFirst();
								if(UtilValidate.isEmpty(customerPartyId1))
								{
									String customerPartyIderror1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "customerPartyIderror");
									boolean makeError = false;
									makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+customerPartyIderror1+"","skipped rows are"+" "+i+"","DataImportInvoice");
			    					if(makeError)
									continue;
								}
								}
								
								if(UtilValidate.isEmpty(supplierPartyId))
								{
									String supplierPartyId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "supplierPartyId");
									boolean makeError = false;
									makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+supplierPartyId1+"","skipped rows are"+" "+i+"","DataImportInvoice");
			    					if(makeError)
									continue;
								}
								if(UtilValidate.isNotEmpty(supplierPartyId))
								{
				           		GenericValue supplierPartyId1 = EntityQuery.use(delegator).from("Party").where("partyId", supplierPartyId).queryFirst();
								if(UtilValidate.isEmpty(supplierPartyId1))
								{
									String supplierPartyIderror1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "supplierPartyIderror");
									boolean makeError = false;
									makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"INVOICE",""+supplierPartyIderror1+"","skipped rows are"+" "+i+"","DataImportInvoice");
			    					if(makeError)
									continue;
								}
								}
								if(UtilValidate.isEmpty(orderPaymentPreferenceId)){
									String orderPaymentPreferenceId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "orderPaymentPreferenceId1");
			           				boolean makeError = false;
			    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+orderPaymentPreferenceId1+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;
								}
								
								if(orderPaymentPreferenceId.length()>20){
								    String orderPaymentPreferenceIdLength = UtilProperties.getPropertyValue("errorUiLabels.properties", "orderPaymentPreferenceIdLength");
			           				boolean makeError = false;
			    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+orderPaymentPreferenceIdLength+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;
								}
								
								if(UtilValidate.isEmpty(paymentMethodTypeId))
								{
									String paymentmethoderror = UtilProperties.getPropertyValue("errorUiLabels.properties", "paymentmethoderror");
									boolean makeError = false;
									makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+paymentmethoderror+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;
								}
								
								if(UtilValidate.isNotEmpty(paymentMethodTypeId))
								{
								GenericValue paymentMethod = EntityQuery.use(delegator).from("PaymentMethodType").where("paymentMethodTypeId", paymentMethodTypeId).queryFirst();
								if(UtilValidate.isEmpty(paymentMethod))
								{
									String paymentmethoderror = UtilProperties.getPropertyValue("errorUiLabels.properties", "paymentmethoderror1");
									boolean makeError = false;
									makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+paymentmethoderror+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;	
								}
								}
								
							}
							Timestamp orderDate = UtilValidate.isNotEmpty(record[4])?Timestamp.valueOf(record[4]):null;
							String currencyUomId = StringUtils.defaultIfEmpty((String) record[5], "");
							BigDecimal shippingTotal = UtilValidate.isNotEmpty(record[6])?new BigDecimal(record[6]):BigDecimal.ZERO;
							BigDecimal orderTax = UtilValidate.isNotEmpty(record[7])?new BigDecimal(record[7]):BigDecimal.ZERO;
							BigDecimal adjustmentsTotal = UtilValidate.isNotEmpty(record[9])?new BigDecimal(record[9]):BigDecimal.ZERO;
							BigDecimal grandTotal = UtilValidate.isNotEmpty(record[10])?new BigDecimal(record[10]):BigDecimal.ZERO;
							String comments = StringUtils.defaultIfEmpty((String) record[11], "");
							String productStoreId = StringUtils.defaultIfEmpty((String) record[12], "");
							String orderClosed = StringUtils.defaultIfEmpty((String) record[15], "");
							String shippingFirstName = StringUtils.defaultIfEmpty((String) record[16], "");
							String shippingLastName = StringUtils.defaultIfEmpty((String) record[17], "");
							
							String shippingCity = StringUtils.defaultIfEmpty((String) record[20], "");
							String shippingRegion = StringUtils.defaultIfEmpty((String) record[21], "");
							String shippingPostcode = StringUtils.defaultIfEmpty((String) record[22], "");
							String shippingCountry = StringUtils.defaultIfEmpty((String) record[23], "");
							String billingFirstName = StringUtils.defaultIfEmpty((String) record[26], "");
							String billingLastName = StringUtils.defaultIfEmpty((String) record[27], "");
							String billingCity = StringUtils.defaultIfEmpty((String) record[30], "");
							String billingRegion = StringUtils.defaultIfEmpty((String) record[31], "");
							String billingPostcode = StringUtils.defaultIfEmpty((String) record[32], "");
							String billingCountry = StringUtils.defaultIfEmpty((String) record[33], "");
							Timestamp processedTimestamp = UtilValidate.isNotEmpty(record[36])?Timestamp.valueOf(record[36]):null;
							BigDecimal maxAmount = UtilValidate.isNotEmpty(record[39])?new BigDecimal(record[39]):BigDecimal.ZERO;
							String statusId = StringUtils.defaultIfEmpty((String) record[40], "");
							String paymentTypeId = StringUtils.defaultIfEmpty((String) record[41], "");
							Timestamp effectiveDate = UtilValidate.isNotEmpty(record[42])?Timestamp.valueOf(record[42]):null;
							BigDecimal amount = UtilValidate.isNotEmpty(record[43])?new BigDecimal(record[43]):BigDecimal.ZERO;
							String paymentcomments = StringUtils.defaultIfEmpty((String) record[44], "");
							
							GenericValue dataImportOrderHeader = EntityQuery.use(delegator).from("DataImportOrderHeader").where("orderId",orderId).queryOne();
							if(UtilValidate.isEmpty(dataImportOrderHeader)){
								dataImportOrderHeader = delegator.makeValue("DataImportOrderHeader");
								dataImportOrderHeader.set("orderId", orderId);
								dataImportOrderHeader.set("orderTypeId", orderTypeId);
								dataImportOrderHeader.set("customerPartyId", customerPartyId);
								dataImportOrderHeader.set("supplierPartyId", supplierPartyId);
								dataImportOrderHeader.set("orderDate", orderDate);
								dataImportOrderHeader.set("currencyUomId", currencyUomId);
								dataImportOrderHeader.set("shippingTotal", shippingTotal);
								dataImportOrderHeader.set("orderTax", orderTax);
								dataImportOrderHeader.set("adjustmentsTotal", adjustmentsTotal);
								dataImportOrderHeader.set("grandTotal", grandTotal);
								dataImportOrderHeader.set("comments", comments);
								dataImportOrderHeader.set("productStoreId", productStoreId);
								dataImportOrderHeader.set("orderClosed", orderClosed);
								dataImportOrderHeader.set("shippingFirstName", shippingFirstName);
								dataImportOrderHeader.set("shippingLastName", shippingLastName);
								dataImportOrderHeader.set("shippingCity", shippingCity);
								dataImportOrderHeader.set("shippingRegion", shippingRegion);
								dataImportOrderHeader.set("shippingPostcode", shippingPostcode);
								dataImportOrderHeader.set("shippingCountry", shippingCountry);
								dataImportOrderHeader.set("billingFirstName", billingFirstName);
								dataImportOrderHeader.set("billingLastName", billingLastName);
								dataImportOrderHeader.set("billingCity", billingCity);
								dataImportOrderHeader.set("billingRegion", billingRegion);
								dataImportOrderHeader.set("billingPostcode", billingPostcode);
								dataImportOrderHeader.set("billingCountry", billingCountry);
								dataImportOrderHeader.set("processedTimestamp", processedTimestamp);
								dataImportOrderHeaderList.add(dataImportOrderHeader);
							}
							GenericValue dataImportOrderPayment = EntityQuery.use(delegator).from("DataImportOrderPayment").where("orderId",orderId,"orderPaymentPreferenceId",orderPaymentPreferenceId).queryOne();
							if(UtilValidate.isEmpty(dataImportOrderPayment)){
								dataImportOrderPayment = delegator.makeValue("DataImportOrderPayment");
								dataImportOrderPayment.set("orderId", orderId);
								dataImportOrderPayment.set("orderPaymentPreferenceId", orderPaymentPreferenceId);
								dataImportOrderPayment.set("paymentMethodTypeId", paymentMethodTypeId);
								dataImportOrderPayment.set("maxAmount", maxAmount);
								dataImportOrderPayment.set("statusId", statusId);
								dataImportOrderPayment.set("paymentTypeId", paymentTypeId);
								dataImportOrderPayment.set("effectiveDate", effectiveDate);
								dataImportOrderPayment.set("amount", amount);
								dataImportOrderPayment.set("comments", paymentcomments);
								dataImportOrderPaymentList.add(dataImportOrderPayment);
							}
						}	
					}
					TransactionUtil.begin();
					if(dataImportOrderHeaderList.size() > 0)
					delegator.storeAll(dataImportOrderHeaderList);
					if(dataImportOrderPaymentList.size() > 0)
					delegator.storeAll(dataImportOrderPaymentList);
					TransactionUtil.commit();
				}catch(Exception e){
					Debug.log("Error in importOrderHeaderAndItemService"+e.getMessage());
				}
			}
			}
		}
		else{
			return ServiceUtil.returnError("File Not Found..");
		}
		
		//Read Order item

		String filePath1 = ComponentConfig.getRootLocation("fio-dataimport")+File.separatorChar+"webapp"+File.separatorChar+"fio-dataimport"+File.separatorChar+"orderItemFiles"+File.separatorChar;
		File folder1 = new File(filePath1);
		File[] files1 = folder1.listFiles();
		
		if(UtilValidate.isEmpty(delimiter)){
			delimiter = "\\|";
		}
		if(UtilValidate.isNotEmpty(files1)){
			for(File file1 : files1){
				if(file1.isFile()){
					int i = 1;
					try(BufferedReader reader1 = new BufferedReader(new FileReader(file1))){
					String line;
					// read the item line in the file
					reader1.readLine();
					List<GenericValue> dataImportOrderItemList=FastList.newInstance();
					readNext:while ((line = reader1.readLine()) != null && !line.isEmpty()) {
						i++;
						String[] record = line.split(delimiter,Integer.MAX_VALUE);
						if(record.length != 0){

							String orderId = StringUtils.defaultIfEmpty((String) record[0],"");
							String orderItemId = StringUtils.defaultIfEmpty((String) record[1], "");
							String productId = StringUtils.defaultIfEmpty((String) record[2], "");
							String goodIdentificationTypeId = StringUtils.defaultIfEmpty((String) record[3], "");
							if(UtilValidate.isEmpty(orderId) || orderId.length()>20 || UtilValidate.isEmpty(orderItemId) || orderItemId.length()>20 || UtilValidate.isEmpty(productId) || UtilValidate.isNotEmpty(productId) || UtilValidate.isEmpty(goodIdentificationTypeId) || UtilValidate.isNotEmpty(goodIdentificationTypeId)){
								if(UtilValidate.isEmpty(orderId)){
									String orderId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "orderId1");
			           				boolean makeError = false;
			    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+orderId1+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;
								}
								
								if(orderId.length()>20){
								    String orderIdLength = UtilProperties.getPropertyValue("errorUiLabels.properties", "orderIdLength");
			           				boolean makeError = false;
			    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+orderIdLength+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;
								}
								
								if(UtilValidate.isEmpty(orderItemId)){
									String orderItemId1 = UtilProperties.getPropertyValue("errorUiLabels.properties", "orderItemId1");
			           				boolean makeError = false;
			    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+orderItemId1+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;
								}
								
								if(orderItemId.length()>20){
								    String orderItemIdLength = UtilProperties.getPropertyValue("errorUiLabels.properties", "orderItemIdLength");
			           				boolean makeError = false;
			    					makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+orderItemIdLength+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;
								}
								
								if(UtilValidate.isEmpty(productId))
								{
									String productIderror = UtilProperties.getPropertyValue("errorUiLabels.properties", "productIderror");
									boolean makeError = false;
									makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+productIderror+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;
								}
								
								if(UtilValidate.isNotEmpty(productId))
								{
								GenericValue product = EntityQuery.use(delegator).from("Product").where("productId", productId).queryFirst();
								if(UtilValidate.isEmpty(product))
								{
									String producterror = UtilProperties.getPropertyValue("errorUiLabels.properties", "producterror");
									boolean makeError = false;
									makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+producterror+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;	
								}
								}
								if(UtilValidate.isEmpty(goodIdentificationTypeId))
								{
									String goodIdentificationTypeIderror = UtilProperties.getPropertyValue("errorUiLabels.properties", "goodIdentificationTypeIderror");
									boolean makeError = false;
									makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+goodIdentificationTypeIderror+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;
								}
								
								if(UtilValidate.isNotEmpty(goodIdentificationTypeId))
								{
								GenericValue goodIdentificationType = EntityQuery.use(delegator).from("GoodIdentificationType").where("goodIdentificationTypeId", goodIdentificationTypeId).queryFirst();
								if(UtilValidate.isEmpty(goodIdentificationType))
								{
									String goodIdentificationTypeIderror = UtilProperties.getPropertyValue("errorUiLabels.properties", "goodIdentificationTypeIderror1");
									boolean makeError = false;
									makeError = org.fio.dataimport.CustomerImportServices.dataImportError(delegator,"ORDER",""+goodIdentificationTypeIderror+"","skipped rows are"+" "+i+"","DataImportOrder");
			    					if(makeError)
									continue readNext;	
								}
								}
								
							}
							
							String productName = StringUtils.defaultIfEmpty((String) record[4], "");
							String productSku = StringUtils.defaultIfEmpty((String) record[5], "");
							BigDecimal quantity = UtilValidate.isNotEmpty(record[6])?new BigDecimal(record[6]):BigDecimal.ZERO;
							BigDecimal quantityShipped = UtilValidate.isNotEmpty(record[7])?new BigDecimal(record[7]):BigDecimal.ZERO;
							BigDecimal price = UtilValidate.isNotEmpty(record[8])?new BigDecimal(record[8]):BigDecimal.ZERO;
							BigDecimal itemTax = UtilValidate.isNotEmpty(record[9])?new BigDecimal(record[9]):BigDecimal.ZERO;
							String taxAuthPartyId = StringUtils.defaultIfEmpty((String) record[10], "");
							BigDecimal itemAdjustmentsTotal = UtilValidate.isNotEmpty(record[11])?new BigDecimal(record[11]):BigDecimal.ZERO;
							String customerPo = StringUtils.defaultIfEmpty((String) record[12], "");
							String comments = StringUtils.defaultIfEmpty((String) record[13], "");
							String importStatusId = StringUtils.defaultIfEmpty((String) record[14], "");
							String importError = StringUtils.defaultIfEmpty((String) record[15], "");
							Timestamp processedTimestamp = UtilValidate.isNotEmpty(record[16])?Timestamp.valueOf(record[16]):null;
							String orderItemSeqId = StringUtils.defaultIfEmpty((String) record[17], "");
							BigDecimal codFee = UtilValidate.isNotEmpty(record[18])?new BigDecimal(record[18]):BigDecimal.ZERO;
							BigDecimal codFeeDiscount = UtilValidate.isNotEmpty(record[19])?new BigDecimal(record[19]):BigDecimal.ZERO;
							BigDecimal giftWrapFee = UtilValidate.isNotEmpty(record[20])?new BigDecimal(record[20]):BigDecimal.ZERO;
							String amazonmerchantsku = StringUtils.defaultIfEmpty((String) record[21], "");
							String requestId = StringUtils.defaultIfEmpty((String) record[22], "");
							String giftMessageText = StringUtils.defaultIfEmpty((String) record[23], "");
							String giftWrapLevel = StringUtils.defaultIfEmpty((String) record[24], "");
							BigDecimal dropShipFee = UtilValidate.isNotEmpty(record[25])?new BigDecimal(record[25]):BigDecimal.ZERO;
							String importedFrom = StringUtils.defaultIfEmpty((String) record[26], "");
							String prodCatalogId = StringUtils.defaultIfEmpty((String) record[27], "");
							String accessType = StringUtils.defaultIfEmpty((String) record[28], "");
							String acctgTagEnumId1 = StringUtils.defaultIfEmpty((String) record[29], "");
							BigDecimal itemPromotionDiscount = UtilValidate.isNotEmpty(record[30])?new BigDecimal(record[30]):BigDecimal.ZERO;
							BigDecimal shippingPromotionDiscount = UtilValidate.isNotEmpty(record[31])?new BigDecimal(record[31]):BigDecimal.ZERO;
							String dropShipSupplierId = StringUtils.defaultIfEmpty((String) record[32], "");
							String shopifyProductId = StringUtils.defaultIfEmpty((String) record[33], "");
							String giftCard = StringUtils.defaultIfEmpty((String) record[34], "");
							
							GenericValue dataImportOrderItem = EntityQuery.use(delegator).from("DataImportOrderItem").where("orderId",orderId,"orderItemId",orderItemId).queryOne();
							if(UtilValidate.isEmpty(dataImportOrderItem)){
							  dataImportOrderItem = delegator.makeValue("DataImportOrderItem");
								dataImportOrderItem.set("orderId", orderId);
								dataImportOrderItem.set("orderItemId", orderItemId);
								dataImportOrderItem.set("productId", productId);
								dataImportOrderItem.set("goodIdentificationTypeId", goodIdentificationTypeId);
								dataImportOrderItem.set("productName", productName);
								dataImportOrderItem.set("productSku", productSku);
								dataImportOrderItem.set("quantity", quantity);
								dataImportOrderItem.set("quantityShipped", quantityShipped);
								dataImportOrderItem.set("price", price);
								dataImportOrderItem.set("itemTax", itemTax);
								dataImportOrderItem.set("taxAuthPartyId", taxAuthPartyId);
								dataImportOrderItem.set("itemAdjustmentsTotal", itemAdjustmentsTotal);
								dataImportOrderItem.set("customerPo", customerPo);
								dataImportOrderItem.set("comments", comments);
								dataImportOrderItem.set("importStatusId", importStatusId);
								dataImportOrderItem.set("importError", importError);
								dataImportOrderItem.set("processedTimestamp", processedTimestamp);
								dataImportOrderItem.set("orderItemSeqId", orderItemSeqId);
								dataImportOrderItem.set("codFee", codFee);
								dataImportOrderItem.set("codFeeDiscount", codFeeDiscount);
								dataImportOrderItem.set("giftWrapFee", giftWrapFee);
								dataImportOrderItem.set("amazonmerchantsku", amazonmerchantsku);
								dataImportOrderItem.set("requestId", requestId);
								dataImportOrderItem.set("giftMessageText", giftMessageText);
								dataImportOrderItem.set("giftWrapLevel", giftWrapLevel);
								dataImportOrderItem.set("dropShipFee", dropShipFee);
								dataImportOrderItem.set("importedFrom", importedFrom);
								dataImportOrderItem.set("prodCatalogId", prodCatalogId);
								dataImportOrderItem.set("accessType", accessType);
								dataImportOrderItem.set("acctgTagEnumId1", acctgTagEnumId1);
								dataImportOrderItem.set("itemPromotionDiscount", itemPromotionDiscount);
								dataImportOrderItem.set("shippingPromotionDiscount", shippingPromotionDiscount);
								dataImportOrderItem.set("dropShipSupplierId", dropShipSupplierId);
								dataImportOrderItem.set("shopifyProductId", shopifyProductId);
								dataImportOrderItem.set("giftCard", giftCard);
								dataImportOrderItemList.add(dataImportOrderItem);
							}
						}	
					}
					TransactionUtil.begin();
					if(dataImportOrderItemList.size() > 0)
					delegator.storeAll(dataImportOrderItemList);
					TransactionUtil.commit();
				}catch(Exception e){
					Debug.log("Error in importOrderHeaderAndItemService--->"+e.getMessage());
				}
			}
			}
		}
		else{
			return ServiceUtil.returnError("File Not Found..");
		}
		long stopTime = System.currentTimeMillis();
	      long elapsedTime = stopTime - startTime;
	      Debug.log("End time---------->"+elapsedTime);
	      			
	}catch(Exception e){
		Debug.log("error"+e.getMessage());
		return ServiceUtil.returnError(e.toString());
	}
	return ServiceUtil.returnSuccess();
	
}

}
