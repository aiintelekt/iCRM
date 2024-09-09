package org.fio.admin.portal.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Sharif
 *
 */
public class StoreReceiptEvents {
	
	 private static final String MODULE = StoreReceiptEvents.class.getName();
	 
	 public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
		 return doJSONResponse(response, jsonObject.toString());
	 }

	 public static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
		 return doJSONResponse(response, JSONArray.fromObject(collection).toString());
	 }

	 public static String doJSONResponse(HttpServletResponse response, Map map) {
	      return doJSONResponse(response, JSONObject.fromObject(map));
	 }

	 public static String doJSONResponse(HttpServletResponse response, String jsonString) {
		 String result = "success";

	     response.setContentType("application/x-json");
	     try {
	    	 response.setContentLength(jsonString.getBytes("UTF-8").length);
	     } catch (UnsupportedEncodingException e) {
	    	 Debug.logError(e, "Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(), MODULE);
	         response.setContentLength(jsonString.length());
	     }
        Writer out;
        try {
            out = response.getWriter();
            out.write(jsonString);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, "Failed to get response writer", MODULE);
            result = "error";
        }
        return result;
    }
	 	private static  String productStoreId ="";
		private static  String storeName ="";
		private static  String district = "";
		private static  String manager = "";
		private static  String phone = "";
		private static  String email = "";
		private static  String address = "";
		private static  String city = "";
		private static  String state = "";
		private static  String zip = "";
		private static  String country = "";
		private static  String url1 = "";
		private static  String url2 = "";
		private static  String url3 = "";
		private static  String url4 = "";
		private static  String storeImageURL ="";
		private static  String storeImage = "";
		private static  String storeHTML = "";
		private static  String fileName = "";
		private static  String storeImageFilePath="";
		private static  String brand = "";
		private static  String address2 = "";
		private static  String returnPolicy = "";
		
		 public static Map<String,String> getMapData(){
				Map<String,String>  mapData = new HashMap();
				mapData.put("productStoreId", productStoreId);
				mapData.put("storeName", storeName);
				mapData.put("district", district);
				mapData.put("manager", manager);
				mapData.put("phone", phone);
				mapData.put("email", email);
				mapData.put("address", address);
				mapData.put("address2", address2);
				mapData.put("city", city);
				mapData.put("state", state);
				mapData.put("zip", zip);
				mapData.put("country", country);
				mapData.put("url1", url1);
				mapData.put("url2", url2);
				mapData.put("url3", url3);
				mapData.put("url4", url4);
				mapData.put("storeImageURL", storeImageURL);		
				mapData.put("storeImage",storeImageFilePath);
				mapData.put("storeHTML", storeHTML);
				mapData.put("brand", brand);
				mapData.put("returnPolicy", returnPolicy);
				return mapData;
			}
		 public static Map<String,String> importDatakey(){
				Map<String,String>  mapData = new HashMap();
				mapData.put("Store", "storeId");
				mapData.put("StoreName", "storeName");
				mapData.put("District", "district");
				mapData.put("Manager", "manager");
				mapData.put("Phone", "phone");
				mapData.put("Email", "email");
				mapData.put("Address", "address");
				mapData.put("Address2", "address2");
				mapData.put("City", "city");
				mapData.put("State", "state");
				mapData.put("Zip", "zip");
				mapData.put("Country", "country");
				mapData.put("URL1", "url1");
				mapData.put("URL2", "url2");
				mapData.put("URL3", "url3");
				mapData.put("URL4", "url4");
				mapData.put("Store Image URL", "storeImageUrl");		
				mapData.put("Store Image","storeImage");
				mapData.put("Store HTML", "storeHTML");
				mapData.put("Brand", "brand");
				mapData.put("Return Policy", "returnPolicy");
				
				return mapData;
			}
		
	 @SuppressWarnings("unused")
	 public static String createStoreReceipt(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
	   	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	   	GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
	   	Map<String, Object> result = new HashMap<String, Object>();
	   	Map<String, Object> context = UtilHttp.getCombinedMap(request);
		fileUploadAndSetParam(request);
		Map<String, String> params = getMapData();
	   	Map<String, Object> callResult = new HashMap<String, Object>();
	   	String productStoreId = (String) context.get("productStoreId");
	   	try {
	   		result.put("productStoreId", productStoreId);
	   		Map<String, Object> callCtxt = new HashMap<String, Object>();
	   		Map<String, Object> requestContext = new HashMap<String, Object>();
	   		callCtxt.put("userLogin", userLogin);
	   		requestContext.put("params",params);
	   		callCtxt.put("requestContext", requestContext);
	   		Debug.log("---requestContext------"+requestContext);
	   		callResult= dispatcher.runSync("loyalty.createStoreReceipt", callCtxt);
	   		if (ServiceUtil.isError(callResult)) {
	   			request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(callResult));
	   			return "error";
	   		}
	   		productStoreId= (String) callResult.get("productStoreId");
	   		request.setAttribute("productStoreId", productStoreId);
	   	}catch (Exception e) {
	   		// TODO: handle exception
	   		request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	   		return "error";
	   	}
		request.setAttribute("_EVENT_MESSAGE_", "Successfully created store reeipt info");
	   	return "success";
	  } 
	 
	 @SuppressWarnings("unused")
	 public static String updateStoreReceipt(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
	   	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	   	GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
	   	Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
	   	String productStoreId = (String) context.get("productStoreId");
	   	Debug.log("----context-----"+context.get("storeImage"));
	   	fileUploadAndSetParam(request);
		Map<String, String> params = getMapData();
		
	   	Map<String, Object> callResult = new HashMap<String, Object>();
	   	try {
	   		result.put("productStoreId", productStoreId);
	   		Map<String, Object> callCtxt = new HashMap<String, Object>();
	   		Map<String, Object> requestContext = new HashMap<String, Object>();
	   		callCtxt.put("userLogin", userLogin);
	   		requestContext.put("params",params);
	   		callCtxt.put("requestContext", requestContext);
	   		Debug.log("---requestContext------"+requestContext);
	   		callResult= dispatcher.runSync("loyalty.updateStoreReceipt", callCtxt);
	   		if (ServiceUtil.isError(callResult)) {
	   			request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(callResult));
	   			return "error";
	   		}
	   		productStoreId= (String) callResult.get("productStoreId");
	   		request.setAttribute("productStoreId", productStoreId);
	   	}catch (Exception e) {
	   		// TODO: handle exception
	   		request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	   		return "error";
	   	}
		request.setAttribute("_EVENT_MESSAGE_", "Successfully updated store receipt info");
	   	return "success";
	  } 
	 
	 @SuppressWarnings("unused")
	 public static String deleteStoreReceipt(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
	   	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	   	GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
	   	Map<String, Object> result = new HashMap<String, Object>();
	   	String productStoreId = (String) request.getParameter("productStoreId");
		
	   	Map<String, Object> callResult = new HashMap<String, Object>();
	   	try {
	   		Map<String, Object> callCtxt = new HashMap<String, Object>();
	   		Map<String, Object> requestContext = new HashMap<String, Object>();
	   		callCtxt.put("userLogin", userLogin);
	   		requestContext.put("productStoreId",productStoreId);
	   		callCtxt.put("requestContext", requestContext);
	   		Debug.log("---requestContext------"+requestContext);
	   		callResult= dispatcher.runSync("loyalty.deleteStoreReceipt", callCtxt);
	   		if (ServiceUtil.isError(callResult)) {
	   			request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(callResult));
	   			result.put("message", ServiceUtil.getErrorMessage(callResult));
		   		result.put("status", "ERROR");
		   		return doJSONResponse(response, result);
	   		}
	   	}catch (Exception e) {
	   		// TODO: handle exception
	   		result.put("message", e.getMessage());
	   		result.put("status", "ERROR");
	   		return doJSONResponse(response, result);
	   	}
		request.setAttribute("_EVENT_MESSAGE_", "Deleted store receipt info deleted successfully");
		result.put("message", "Deleted store receipt info deleted successfully");
   		result.put("status", "SUCCESS");
   		return doJSONResponse(response, result);
	  } 
	public static void fileUploadAndSetParam(HttpServletRequest request) {
		ServletFileUpload getRequest = new ServletFileUpload(new DiskFileItemFactory()); // Creation of
																							// servletfileupload
		java.util.List lst = null;
		String result = "AttachementException";
		String file_name = "";
		String uri = request.getScheme() + "://" + // "http" + "://
				request.getServerName() + ":" + request.getServerPort(); // "myhost"
		String domain = request.getLocalAddr();
		String path = request.getContextPath();
		Debug.log("path======="+path);
		String siteUrl = uri;
		Debug.log("siteUrl" + uri);
		try {
			lst = getRequest.parseRequest(request);
		} catch (FileUploadException fup_ex) {
			Debug.log("------Exception of FileUploadException ---");
		}

		if (lst.size() == 0) {
			Debug.log("Lst count is 0");
		}

		FileItem file_item = null;
		FileItem selected_file_item = null;
		
    	String itemName ="";
		// Checking for form fields - Start
		for (int i = 0; i < lst.size(); i++) {
			file_item = (FileItem) lst.get(i);
			if (file_item.isFormField()) {
				Debug.log("---" + file_item.isFormField());
				
				itemName = file_item.getFieldName();
				Debug.log("-fieldName--" + itemName);
				if (itemName.equals("productStoreId")) {
					productStoreId = file_item.getString();
	
				} else if (itemName.equals("storeName")) {
					storeName = file_item.getString();
	
				} else if (itemName.equals("pid")) {
					productStoreId = file_item.getString();
	
				} else if (itemName.equals("district")) {
					district = file_item.getString();
	
				} else if (itemName.equals("manager")) {
					manager = file_item.getString();
	
				} else if (itemName.equals("phone")) {
					phone = file_item.getString();
	
				} else if (itemName.equals("email")) {
					email = file_item.getString();
	
				} else if (itemName.equals("address")) {
					address = file_item.getString();
	
				}else if (itemName.equals("address2")) {
					address2 = file_item.getString();
	
				} else if (itemName.equals("city")) {
					city = file_item.getString();
	
				}
				// manager phone email address city state zip country url1 storeImageURL
				// storeHTML
				else if (itemName.equals("state")) {
					state = file_item.getString();
	
				} else if (itemName.equals("zip")) {
					zip = file_item.getString();
	
				} else if (itemName.equals("country")) {
					country = file_item.getString();
	
				} else if (itemName.equals("url1")) {
					url1 = file_item.getString();
	
				} else if (itemName.equals("url2")) {
					url2 = file_item.getString();
	
				} else if (itemName.equals("url3")) {
					url3 = file_item.getString();
	
				} else if (itemName.equals("url4")) {
					url4 = file_item.getString();
	
				} else if (itemName.equals("storeImageURL")) {
					storeImageURL = file_item.getString();
	
				}else if (itemName.equals("brand")) {
					brand = file_item.getString();
	
				}else if (itemName.equals("returnPolicy")) {
					returnPolicy = file_item.getString();
	
				}else if (itemName.equals("storeHTML")) {
					Debug.log("valuedsfsd" + file_item.getString());
					if (UtilValidate.isNotEmpty(file_item.getString())) {
						storeHTML = org.ofbiz.base.util.Base64.base64Encode(file_item.getString());
					}else {
						storeHTML = "";
					}
				} 
			}
		}
		String _default_path="";
		try {
			_default_path = ComponentConfig.getRootLocation("admin-portal")+ "/webapp/admin-portal-resource/images/storeImages/";
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			String _store_image_loc = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "STORE_IMAGE_LOAD_LOC",_default_path);
			if (UtilValidate.isNotEmpty(_store_image_loc)) {
				File dir = new File(_store_image_loc);
				if (dir.isDirectory() && !dir.exists()) {
					dir.mkdirs();
				}
			}
			for (int j = 0; j < lst.size(); j++) {
				file_item = (FileItem) lst.get(j);
				if (!file_item.isFormField()) {
					itemName = file_item.getFieldName();
					if (itemName.equals("storeImage")) {
						selected_file_item = file_item;
						fileName = file_item.getName();
						if (UtilValidate.isNotEmpty(fileName)) {
							File store = new File(_store_image_loc);
	
							DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
							fileItemFactory.setRepository(store);
							String name = new File(file_item.getName()).getName();
							String fileExtension = org.fio.admin.portal.util.DataUtil.getFileExtension(name);
							name = name.replace("."+fileExtension, "");
							Debug.log("---name-" + name);
							String objInfo = _store_image_loc + File.separator + productStoreId + "_" + name + "." + fileExtension;
							if (!(UtilMisc.toList("png", "jpg", "jpeg").contains(fileExtension))) {
								Debug.log("---Wrong extension-");
							}else{
								try {
									selected_file_item.write(new File(objInfo));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Debug.log("---objInfo---" + objInfo);
								storeImageFilePath = objInfo;
							}
						}
					}
				}
			}
		} catch (ComponentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
		Debug.log("-fileName--" + fileName);
		Debug.log("storeHTML" + storeHTML);
		
		// File uploaded successfully
		Debug.log("File Uploaded Successfully" + fileName);

	}
	@SuppressWarnings({ "unused", "resource", "unchecked" })
	public static String uploadStoreReceipts(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
	   	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	   	GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
	   	Map<String, Object> result = new HashMap<String, Object>();
	   	Map<String, Object> callResult = new HashMap<String, Object>();
	   	Map<String, Object> context = UtilHttp.getCombinedMap(request);
	   	String fileFormat = (String) context.get("fileFormat");
	    String filePath ="";
	    File file = null;
	   	try {
	   	  boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
	   	  Debug.log("isMultiPart---"+isMultiPart);
	   	  filePath = setFilePath(request);
			POIFSFileSystem fs = null;
	        HSSFWorkbook wb = null; 
	       
			// Checking for form fields - Start
	        List<String> columnMap = new ArrayList<String>();
	        List<Map<String,Object>> fieldValues = new ArrayList<Map<String,Object>>();
	        
	        if (UtilValidate.isNotEmpty(filePath)) {
	        	file = new File(filePath);
	        	if (file.isFile()) {
	        		fs = new POIFSFileSystem(new FileInputStream(file));
		            wb = new HSSFWorkbook(fs);
					if (wb != null) {
						int startRowNum = 0;
						List tempList = FastList.newInstance();
						HSSFSheet sheet = wb.getSheetAt(0);
						if (sheet == null) {
							Debug.logWarning("Did not find a sheet", MODULE);
						} else {
							HSSFRow row = sheet.getRow(0);
							Iterator cellIter = row.cellIterator();
							while (cellIter.hasNext()) {
								HSSFCell cell = (HSSFCell) cellIter.next();
								columnMap.add(cell.toString());
							}
							startRowNum = 1;
							int sheetLastRowNumber = sheet.getLastRowNum();
							for (int j = startRowNum; j <= sheetLastRowNumber; j++) {
								tempList.clear();
								row = sheet.getRow(j);
								Map<String, Object> rowValue = FastMap.newInstance();
								if (row != null) {
									for (int i = 0; i < row.getLastCellNum(); i++) {
										String val = columnMap.get(i);
										HSSFCell cell = (HSSFCell) row.getCell(i);
										String cellValue = UtilValidate.isNotEmpty(cell)?cell.toString():"";
										if (UtilValidate.isNotEmpty(val)) {
											rowValue.put(val, cellValue);
										}
									}
									fieldValues.add(rowValue);
								}
							}
						}
					}
	        	}
	        	if (file.isFile()) {
	        		file.delete();
	        	}
	        	
	        }
				
         	if (UtilValidate.isNotEmpty(fieldValues)) {
         		for (int k=0;k<fieldValues.size();k++) {
         			Map <String,Object> fieldMap = fieldValues.get(k);
         			Map mapKeys = importDatakey();
         			Map params = FastMap.newInstance();
         			if(UtilValidate.isNotEmpty(fieldMap)){
	         			for (String key : fieldMap.keySet()) {
	    					String value = (String) fieldMap.get(key);
	    					if(UtilValidate.isNotEmpty(key) && (key.equalsIgnoreCase("storeId") || key.equalsIgnoreCase("storeName"))){
	    						if (UtilValidate.isEmpty(value)) {
	    							continue;
	    						}else {
	    							params.put(mapKeys.get(key),value);
	    						}
	    					}else {
		    					if (UtilValidate.isNotEmpty(key) && UtilValidate.isNotEmpty(mapKeys.get(key))) {
		    						params.put(mapKeys.get(key),value);
		    					}
	    					}
	         			}
         			}
         			if (UtilValidate.isNotEmpty(params)) {
         				Map<String, Object> callCtxt = new HashMap<String, Object>();
         		   		Map<String, Object> requestContext = new HashMap<String, Object>();
         		   		callCtxt.put("userLogin", userLogin);
         		   		requestContext.put("params",params);
         		   		callCtxt.put("requestContext", requestContext);
         				callResult= dispatcher.runSync("ls.createStoreReceiptStaging", callCtxt);
         				Debug.log("---callResult------"+callResult);
         		   		if (ServiceUtil.isError(callResult)) {
         		   			request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(callResult));
         		   			return "error";
         		   		}
         			}
         		}
         		
         	}
	   	}catch (Exception e) {
	   		// TODO: handle exception
	   		Debug.log("Header e "+e, MODULE);
	   		if (file.isFile()) {
	   			file.delete();
	   		}
	   		request.setAttribute("_ERROR_MESSAGE_","Error uploading list "+e);
	   		return "error";
	   	}
	   	request.setAttribute("_EVENT_MESSAGE_", "List loaded successfully");
   		return "success";
	  } 
	
	public static String setFilePath(HttpServletRequest request) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String fileLocation = "";
		try {
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Debug.log("isMultiPart---" + isMultiPart);
			ServletFileUpload getRequest = new ServletFileUpload(new DiskFileItemFactory());
			java.util.List lst = null;
			lst = getRequest.parseRequest(request);

			if (lst.size() == 0) { // There is no item in lst
				Debug.log("Lst count is 0");
				return fileLocation;
			}
			FileItem file_item = null;
			FileItem selected_file_item = null;

			String _default_path = "";
			String _store_temp_loc = "";
			_default_path = ComponentConfig.getRootLocation("admin-portal")
					+ "/webapp/admin-portal-resource/uploads/";
			_store_temp_loc = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "STORE_RECEIPT_LOAD_LOC",
					_default_path);

			File filepath = new File(_store_temp_loc);
			File file = null;

			// Checking for multi form fields - Start

			String objInfo = "";

			for (int i = 0; i < lst.size(); i++) {
				file_item = (FileItem) lst.get(i);

				String fieldName = file_item.getFieldName();

				Debug.log("------isFormField------" + file_item.isFormField());
				Debug.log("-fieldName--" + fieldName);
				Debug.log("-filepath---" + filepath);

				if (!file_item.isFormField()) {
					DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
					fileItemFactory.setRepository(filepath);

					String name = new File(file_item.getName()).getName();
					Debug.log("-name--" + name);
					if (UtilValidate.isNotEmpty(name)) {
						String fileExtension = org.fio.admin.portal.util.DataUtil.getFileExtension(name);
						objInfo = filepath + File.separator + name + "." + fileExtension;
						file = new File(objInfo);
						if (file.isFile()) {
							file.delete();
						}
						selected_file_item = file_item;
						selected_file_item.write(new File(objInfo));
					}
				}
			}
			fileLocation = objInfo;
		} catch (Exception e) {
			Debug.log("Header e " + e, MODULE);
			return "";
		}
		return fileLocation;
	}
	
	public static String getStoreReceiptList(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> results = new HashMap<String, Object>();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		Map<String, Object> callResult = FastMap.newInstance();
		List<Map<String, Object>> dataList = FastList.newInstance();
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> requestContext = FastMap.newInstance();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String productStoreId = (String) context.get("productStoreId");
		String storeName = (String) context.get("storeName");

		try {
			List condList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(productStoreId)) {
				condList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.LIKE,"%" + productStoreId + "%"));
			}
			EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue> storeAttributes = delegator.findList("StoreAttribute", cond, null,UtilMisc.toList("productStoreId"), null, false);
			List productStoreIds = EntityUtil.getFieldListFromEntityList(storeAttributes, "productStoreId", true);
			condList.clear();
			List<GenericValue> productStore = null;
			if (UtilValidate.isNotEmpty(productStoreIds)) {
				if (UtilValidate.isNotEmpty(storeName)) {
					condList.add(EntityCondition.makeCondition("storeName", EntityOperator.LIKE, "%" + storeName + "%"));
				}
				condList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
				cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
				productStore = delegator.findList("ProductStore", cond, null, null, null, false);
			}
			if (UtilValidate.isNotEmpty(productStore)) {
				for (GenericValue result : productStore) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(result);
					dataList.add(data);
				}
				results.put("responseMessage", "success");
				results.put("data", dataList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("data", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, results);
	}
	
	public static String getStoreUploadSuccessList(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> results = new HashMap<String, Object>();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		Map<String, Object> callResult = FastMap.newInstance();
		List<Map<String, Object>> dataList = FastList.newInstance();
		try {
			List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "SUCCESS"));
			condList.add(EntityCondition.makeCondition("isProcessed", EntityOperator.EQUALS, "Y"));
			EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue> receiptDetails = delegator.findList("StoreReceiptStaging", cond,UtilMisc.toSet("storeId", "storeName", "importStatusId", "importStatus", "isProcessed"), null, null,false);

			if (UtilValidate.isNotEmpty(receiptDetails)) {
				for (GenericValue result : receiptDetails) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(result);
					data.put("storeId", result.getString("storeId"));
					data.put("storeName", result.getString("storeName"));
					data.put("statusId", result.getString("importStatusId"));
					data.put("status", result.getString("importStatus"));
					data.put("isProcessed", result.getString("isProcessed"));
					dataList.add(data);
				}
				results.put("responseMessage", "success");
				results.put("data", dataList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("data", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, results);
	}
	
	public static String getStoreUploadFailedList(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> results = new HashMap<String, Object>();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		Map<String, Object> callResult = FastMap.newInstance();
		List<Map<String, Object>> dataList = FastList.newInstance();
		try {
			List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "ERROR"));
			EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue> receiptDetails = delegator.findList("StoreReceiptStaging", cond,UtilMisc.toSet("storeId", "storeName", "importStatusId", "importStatus", "isProcessed"), null, null,false);
			if (UtilValidate.isNotEmpty(receiptDetails)) {
				for (GenericValue result : receiptDetails) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(result);
					data.put("storeId", result.getString("storeId"));
					data.put("storeName", result.getString("storeName"));
					data.put("statusId", result.getString("importStatusId"));
					data.put("status", result.getString("importStatus"));
					data.put("isProcessed", result.getString("isProcessed"));
					dataList.add(data);
				}
				results.put("responseMessage", "success");
				results.put("data", dataList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("data", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, results);
	}
}
