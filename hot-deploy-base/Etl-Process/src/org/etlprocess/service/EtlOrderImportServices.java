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
import org.groupfio.etl.process.job.OrderImportJob;
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

public class EtlOrderImportServices {
	
	private static String MODULE = EtlOrderImportServices.class.getName();
	private static String errorLog ="";
	public static String currentListId = "";
	public static String accessType = UtilProperties.getPropertyValue("Etl-Process", "UPLOAD_TYPE");
	public static String etlOrderTableName = UtilProperties.getPropertyValue("Etl-Process", "ORDER_TABLE");
	
	
	
	public static String uploadEtlImportOrderService(HttpServletRequest request, HttpServletResponse response,String processId) throws GenericEntityException, ComponentException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		HttpSession session = request.getSession(true);
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
							context.put("batchId",batchId);
							Map<String,Object> result = dispatcher.runSync("CreateEtlOrderImport", context);
							if(ServiceUtil.isError(result)){
           						request.setAttribute("model", listId);
           						request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));
           						return "error";
           					}
						}
					}

				}
				
				Map<String, Object> context = FastMap.newInstance();
				context.put("listId", listId);
				context.put("batchId", batchId);
				dispatcher.runSync("importOrdersToStaing", context);
				
				Map<String,Object> inputNew = new HashMap<String,Object>();
	   			inputNew.put("userLogin",userLogin);
	   			inputNew.put("batchId",batchId);
	   			inputNew.put("modelId",listId);
	   			inputNew.put("accessType",accessType);
	   			inputNew.put("etlTableName",etlOrderTableName);
	   			Map<String,Object> Res = dispatcher.runSync("EtlSelfServiceProcessor", inputNew);
	   			
	   			if(ServiceUtil.isSuccess(Res)){
	   				// Trigger Thread
	   				OrderImportJob job = new OrderImportJob();
		   			job.setDelegator(delegator);
		   			job.setDispatcher(dispatcher);
		   			job.setEtlModelId(listId);
		   			job.setUserLogin(userLogin);
		   			job.start();
	   			}	   			
	   							
					   
				//Trigger Services
			/*	Map<String, Object> inputNew = new HashMap<String, Object>();
				inputNew.put("userLogin", userLogin);
				inputNew.put("importEmptyOrders", false);
				inputNew.put("calculateGrandTotal", false);
				inputNew.put("companyPartyId", "Company");
				dispatcher.runSync("importOrders",inputNew);
				System.out.println("======service started======"+UtilDateTime.nowTimestamp());*/
		   			
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
		request.setAttribute("_EVENT_MESSAGE_", "File imported successfully.");
		return "success";
	}
	
	public static boolean validateFileFormat(File file,String name) throws IOException
	{
		errorLog="";
        String extention = name.substring(name.indexOf('.'));
        if(UtilValidate.isEmpty(extention))
        {
        	errorLog = "Extention Not Found";
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
			errorLog = errorLog +"Please load CSV file without empty values";
			return false;
			
		}
		
		return true;
	}
	public static Map<String, Object>CreateEtlOrderImportService(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();

		try {
			currentListId = (String)context.get("listId");
			GenericValue checkOrderId = EntityQuery.use(delegator).from("EtlImportOrderFields").queryFirst();
			
		
		//	GenericValue checkOrderId = delegator.findOne("EtlImportOrderFields",null,false);
			if(UtilValidate.isNotEmpty((String) context.get("orderId"))){
								
			GenericValue orderHeader=delegator.makeValue("EtlImportOrderFields");
				
				orderHeader.put("batchId", (String) context.get("batchId"));
				if(UtilValidate.isNotEmpty((String) context.get("orderId"))){
				GenericValue orderHeaderTable = EntityQuery.use(delegator).from("OrderHeader").where("orderId", (String) context.get("orderId")).cache().queryOne();
				if(UtilValidate.isEmpty(orderHeaderTable))
				orderHeader.put("orderId", (String) context.get("orderId"));
				else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ETLIMPORTORDER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError1"),"EtlImportOrderFields");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				}
				}
				else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ETLIMPORTORDER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError"),"EtlImportOrderFields");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				}
				if(UtilValidate.isNotEmpty((String) context.get("orderDate")))
				orderHeader.put("orderDate", Timestamp.valueOf((String) context.get("orderDate")));
				orderHeader.put("customerPartyId", (String) context.get("customerPartyId"));
				orderHeader.put("supplierPartyId", (String) context.get("supplierPartyId"));
				if(UtilValidate.isNotEmpty((String) context.get("currencyUomId"))){
	   				GenericValue uom = EntityQuery.use(delegator).from("Uom").where("uomId", (String) context.get("currencyUomId")).cache().queryOne();
	   				if(UtilValidate.isNotEmpty(uom))
	   					orderHeader.put("currencyUomId", (String) context.get("currencyUomId"));
	   				else{
	   					boolean makeError = false;
	   					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ETLIMPORTORDER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCurrencyError"),"EtlImportOrderFields");
	   					if(makeError)
	   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
	   				}
				}
				else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ETLIMPORTORDER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceCurrencyEmptyError"),"EtlImportOrderFields");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				}
				orderHeader.put("shippingTotal", (String) context.get("shippingTotal"));
				orderHeader.put("orderTax", (String) context.get("orderTax"));
				orderHeader.put("taxAuthPartyId", (String) context.get("taxAuthPartyId"));
				orderHeader.put("adjustmentsTotal", (String) context.get("adjustmentsTotal"));
				orderHeader.put("grandTotal", (String) context.get("grandTotal"));
				orderHeader.put("comments", (String) context.get("comments"));
				if(UtilValidate.isNotEmpty((String) context.get("productStoreId"))){
					GenericValue productStore = EntityQuery.use(delegator).from("ProductStore").where("productStoreId", (String) context.get("productStoreId")).cache().queryOne();
	   				if(UtilValidate.isNotEmpty(productStore))
	   					orderHeader.put("productStoreId", (String) context.get("productStoreId"));
	   				else{
	   					boolean makeError = false;
	   					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ETLIMPORTORDER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError2"),"EtlImportOrderFields");
	   					if(makeError)
	   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
	   				}
				}
				else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ETLIMPORTORDER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError3"),"EtlImportOrderFields");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
				}
				orderHeader.put("salesChannelEnumId", (String) context.get("salesChannelEnumId"));
				orderHeader.put("productStoreShipMethId", (String) context.get("productStoreShipMethId"));
				orderHeader.put("orderClosed", (String) context.get("orderClosed"));
				orderHeader.put("shippingFirstName", (String) context.get("shippingFirstName"));
				orderHeader.put("shippingLastName", (String) context.get("shippingLastName"));
				orderHeader.put("shippingCompanyName", (String) context.get("shippingCompanyName"));
				orderHeader.put("shippingStreet", (String) context.get("shippingStreet"));
				orderHeader.put("shippingCity", (String) context.get("shippingCity"));
				orderHeader.put("shippingRegion", (String) context.get("shippingRegion"));
				orderHeader.put("shippingPostcode", (String) context.get("shippingPostcode"));
				orderHeader.put("shippingCountry", (String) context.get("shippingCountry"));
				orderHeader.put("shippingPhone", (String) context.get("shippingPhone"));
				orderHeader.put("shippingFax", (String) context.get("shippingFax"));
				orderHeader.put("billingFirstName", (String) context.get("billingFirstName"));
				orderHeader.put("billingLastName", (String) context.get("billingLastName"));
				orderHeader.put("billingCompanyName", (String) context.get("billingCompanyName"));
				orderHeader.put("billingStreet", (String) context.get("billingStreet"));
				orderHeader.put("billingCity", (String) context.get("billingCity"));
				orderHeader.put("billingRegion", (String) context.get("billingRegion"));
				orderHeader.put("billingState", (String) context.get("billingState"));
				orderHeader.put("billingPostalCode", (String) context.get("billingPostalCode"));
				orderHeader.put("billingCountry", (String) context.get("billingCountry"));
				orderHeader.put("billingPhone", (String) context.get("billingPhone"));
				orderHeader.put("billingFax", (String) context.get("billingFax"));
				orderHeader.put("orderItemCode", (String) context.get("orderItemCode"));
				orderHeader.put("sku", (String) context.get("sku"));
				orderHeader.put("goodIdentificationTypeId", (String) context.get("goodIdentificationTypeId"));
				orderHeader.put("title", (String) context.get("title"));
				orderHeader.put("quantity", (String) context.get("quantity"));
				orderHeader.put("quantityShipped", (String) context.get("quantityShipped"));
				orderHeader.put("itemPrice", (String) context.get("itemPrice"));
				orderHeader.put("itemTax", (String) context.get("itemTax"));
				orderHeader.put("itemAdjustmentsTotal", (String) context.get("itemAdjustmentsTotal"));
				if(UtilValidate.isNotEmpty((String) context.get("paymentMethodType"))){
					GenericValue paymentMethodType = EntityQuery.use(delegator).from("PaymentMethodType").where("paymentMethodTypeId", (String) context.get("paymentMethodType")).cache().queryOne();
	   				if(UtilValidate.isNotEmpty(paymentMethodType))
	   					orderHeader.put("paymentMethodType", (String) context.get("paymentMethodType"));
	   				else{
	   					boolean makeError = false;
	   					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ETLIMPORTORDER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError4"),"EtlImportOrderFields");
	   					if(makeError)
	   						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));		
	   				}				
				}
				else{
					boolean makeError = false;
					makeError =  org.etlprocess.service.EtlImportServices.makeEtlError(delegator,"ETLIMPORTORDER",UtilProperties.getPropertyValue("Etl-Process.properties", "EtlOrderImportServiceOrderIdError5"),"EtlImportOrderFields");
					if(makeError)
						return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceErrorMsg1"));							
				}
		
				delegator.create(orderHeader);
				//System.out.println(checkOrderId+"table created==========");	
				Debug.log(checkOrderId+"table created==========");
			}
	
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			return ServiceUtil.returnError(e.toString());
		}
		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object>stagingImportOrderService(DispatchContext dctx, Map<String, ? extends Object> context)

	{
		Delegator delegator = dctx.getDelegator();
		try {
			List<GenericValue> EtlImportOrderFieldsAll = delegator.findByAnd("EtlImportOrderFields",UtilMisc.toMap("batchId", (String) context.get("batchId")),null,false);
			if(UtilValidate.isNotEmpty(EtlImportOrderFieldsAll)){
			for(GenericValue etlFieldV : EtlImportOrderFieldsAll)	
			{
			//currentListId = (String)etlFieldV.get("listId");

			/*GenericValue checkOrderId = delegator.findOne("DataImportOrderHeader",false,UtilMisc.toMap("orderId",(String) context.get("orderId")));

			if(UtilValidate.isEmpty(checkOrderId) && UtilValidate.isNotEmpty((String) context.get("orderId"))){*/

				GenericValue orderHeader=delegator.makeValue("DataImportOrderHeader");
				orderHeader.put("batchId",(String) context.get("batchId"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("orderId")))
					orderHeader.put("orderId", (String) etlFieldV.get("orderId"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("customerPartyId")))
					orderHeader.put("customerPartyId", (String) etlFieldV.get("customerPartyId"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("supplierPartyId")))
					orderHeader.put("supplierPartyId", (String) etlFieldV.get("supplierPartyId"));
					orderHeader.put("orderTypeId", "PURCHASE_ORDER");
				if(UtilValidate.isNotEmpty((Timestamp) etlFieldV.get("orderDate"))){
					/*Timestamp orderDt = Timestamp.valueOf((String) etlFieldV.get("orderDate"));*/
					orderHeader.put("orderDate", (Timestamp) etlFieldV.get("orderDate"));
				}
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("currencyUomId")))
					orderHeader.put("currencyUomId", (String) etlFieldV.get("currencyUomId"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("shippingTotal"))){
					BigDecimal shipTot = new BigDecimal((String) etlFieldV.get("shippingTotal"));
					orderHeader.put("shippingTotal", shipTot);	
				}
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("orderTax"))){
					BigDecimal orderTax = new BigDecimal((String) etlFieldV.get("orderTax"));
					orderHeader.put("orderTax", orderTax);
				}
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("taxAuthPartyId")))
					orderHeader.put("taxAuthPartyId", (String) etlFieldV.get("taxAuthPartyId"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("adjustmentsTotal"))){
					BigDecimal adjustmentsTotal = new BigDecimal((String) etlFieldV.get("adjustmentsTotal"));
					orderHeader.put("adjustmentsTotal", adjustmentsTotal);
				}if(UtilValidate.isNotEmpty((String) etlFieldV.get("grandTotal"))){
					BigDecimal grandTotal = new BigDecimal((String) etlFieldV.get("grandTotal"));
					orderHeader.put("grandTotal", grandTotal);
				}
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("comments")))
					orderHeader.put("comments", (String) etlFieldV.get("comments"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("productStoreId")))
					orderHeader.put("productStoreId", (String) etlFieldV.get("productStoreId"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("salesChannelEnumId")))
					orderHeader.put("salesChannelEnumId", (String) etlFieldV.get("salesChannelEnumId"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("productStoreShipMethId")))
					orderHeader.put("productStoreShipMethId", (String) etlFieldV.get("productStoreShipMethId"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("orderClosed")))
					orderHeader.put("orderClosed", (String) etlFieldV.get("orderClosed"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("shippingFirstName")))
					orderHeader.put("shippingFirstName", (String) etlFieldV.get("shippingFirstName"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("shippingLastName")))
					orderHeader.put("shippingLastName", (String) etlFieldV.get("shippingLastName"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("shippingCompanyName")))
					orderHeader.put("shippingCompanyName", (String) etlFieldV.get("shippingCompanyName"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("shippingStreet")))
					orderHeader.put("shippingStreet", (String) etlFieldV.get("shippingStreet"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("shippingCity")))
					orderHeader.put("shippingCity", (String) etlFieldV.get("shippingCity"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("shippingRegion")))
					orderHeader.put("shippingRegion", (String) etlFieldV.get("shippingRegion"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("shippingPostcode")))
					orderHeader.put("shippingPostcode", (String) etlFieldV.get("shippingPostcode"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("shippingCountry")))
					orderHeader.put("shippingCountry", (String) etlFieldV.get("shippingCountry"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("shippingPhone")))
					orderHeader.put("shippingPhone", (String) etlFieldV.get("shippingPhone"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("shippingFax")))
					orderHeader.put("shippingFax", (String) etlFieldV.get("shippingFax"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("billingFirstName")))
					orderHeader.put("billingFirstName", (String) etlFieldV.get("billingFirstName"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("billingLastName")))
					orderHeader.put("billingLastName", (String) etlFieldV.get("billingLastName"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("billingCompanyName")))
					orderHeader.put("billingCompanyName", (String) etlFieldV.get("billingCompanyName"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("billingStreet")))
					orderHeader.put("billingStreet", (String) etlFieldV.get("billingStreet"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("billingCity")))
					orderHeader.put("billingCity", (String) etlFieldV.get("billingCity"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("billingRegion")))
					orderHeader.put("billingRegion", (String) etlFieldV.get("billingRegion"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("billingPostalCode")))
					orderHeader.put("billingPostcode", (String) etlFieldV.get("billingPostalCode"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("billingCountry")))
					orderHeader.put("billingCountry", (String) etlFieldV.get("billingCountry"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("billingPhone")))
					orderHeader.put("billingPhone", (String) etlFieldV.get("billingPhone"));
				if(UtilValidate.isNotEmpty((String) etlFieldV.get("billingFax")))
					orderHeader.put("billingFax", (String) etlFieldV.get("billingFax"));
	   			   delegator.create(orderHeader);

			GenericValue orderItem = delegator.makeValue("DataImportOrderItem");
			orderItem.put("batchId",(String) context.get("batchId"));
			if(UtilValidate.isNotEmpty((String) etlFieldV.get("orderId")))
				orderItem.put("orderId", (String) etlFieldV.get("orderId"));
			if(UtilValidate.isNotEmpty((String) etlFieldV.get("orderItemCode")))
				orderItem.put("orderItemId", (String) etlFieldV.get("orderItemCode"));
			if(UtilValidate.isNotEmpty((String) etlFieldV.get("sku")))
				orderItem.put("productId", (String) etlFieldV.get("sku"));
			if(UtilValidate.isNotEmpty((String) etlFieldV.get("goodIdentificationTypeId")))
				orderItem.put("goodIdentificationTypeId", (String) etlFieldV.get("goodIdentificationTypeId"));
	    	if(UtilValidate.isNotEmpty((String) etlFieldV.get("title")))
				orderItem.put("productName", (String) etlFieldV.get("title"));
			/*if(UtilValidate.isNotEmpty((String) context.get("productId")))
				orderItem.put("productSku", (String) context.get("productId"));*/
			if(UtilValidate.isNotEmpty((String) etlFieldV.get("quantity"))){
				BigDecimal qty = new BigDecimal((String) etlFieldV.get("quantity"));
				orderItem.put("quantity",qty);	
			}if(UtilValidate.isNotEmpty((String) etlFieldV.get("quantityShipped"))){
				BigDecimal quantityShipped = new BigDecimal((String) etlFieldV.get("quantityShipped"));
				orderItem.put("quantityShipped",quantityShipped);	
			}if(UtilValidate.isNotEmpty((String) etlFieldV.get("itemPrice"))){
				BigDecimal itemPrice = new BigDecimal((String) etlFieldV.get("itemPrice"));
				orderItem.put("price",itemPrice);	
			}if(UtilValidate.isNotEmpty((String) etlFieldV.get("itemTax"))){
				BigDecimal itemTax = new BigDecimal((String) etlFieldV.get("itemTax"));
				orderItem.put("itemTax",itemTax);	
			}
			if(UtilValidate.isNotEmpty((String) etlFieldV.get("taxAuthPartyId")))
				orderItem.put("taxAuthPartyId", (String) etlFieldV.get("taxAuthPartyId"));
			if(UtilValidate.isNotEmpty((String) etlFieldV.get("itemAdjustmentsTotal"))){
				BigDecimal itemAdjustmentsTotal = new BigDecimal((String) etlFieldV.get("itemAdjustmentsTotal"));
				orderItem.put("itemAdjustmentsTotal",itemAdjustmentsTotal);	
			}if(UtilValidate.isNotEmpty((String) etlFieldV.get("comments"))){
				orderItem.put("comments",(String) etlFieldV.get("comments"));	
			}
			delegator.create(orderItem);

			GenericValue orderPayment = delegator.makeValue("DataImportOrderPayment");
			orderPayment.put("orderPaymentPreferenceId",delegator.getNextSeqId("DataImportOrderPayment"));
			orderPayment.put("batchId",(String) context.get("batchId"));
			orderPayment.put("orderId",(String) etlFieldV.get("orderId"));
			orderPayment.put("paymentMethodTypeId",(String) etlFieldV.get("paymentMethodType"));
			/*if(UtilValidate.isNotEmpty((String) context.get("maxAmount"))){
				BigDecimal maxAmount = new BigDecimal((String) context.get("maxAmount"));
				orderItem.put("maxAmount",maxAmount);	
			}*/
			orderPayment.put("statusId","PAYMENT_NOT_RECEIVED");
			orderPayment.put("paymentTypeId","CUSTOMER_PAYMENT");
			if(UtilValidate.isNotEmpty((String) etlFieldV.get("grandTotal"))){
				BigDecimal grandTotal = new BigDecimal((String) etlFieldV.get("grandTotal"));
				orderPayment.put("amount", grandTotal);
			}
			/*	if(UtilValidate.isNotEmpty((String) context.get("amount"))){
				BigDecimal amount = new BigDecimal((String) context.get("amount"));
				orderItem.put("amount",amount);	
			}*/
			orderPayment.put("comments","Payment Imported by ECOM");
			delegator.create(orderPayment);

			}
			}
		} catch (Exception e) {

			// TODO Auto-generated catch block

			/*e.printStackTrace();*/

			return ServiceUtil.returnError(e.toString());

		}

		return ServiceUtil.returnSuccess();

	}
}