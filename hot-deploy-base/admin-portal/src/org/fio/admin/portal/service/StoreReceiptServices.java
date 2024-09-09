package org.fio.admin.portal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.admin.portal.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class StoreReceiptServices {
	
	private static final String MODULE = StoreReceiptServices.class.getName();
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static Map createStoreReceipt(DispatchContext dctx, Map context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Map requestContext = (Map) context.get("requestContext");
		Map<String, Object> params = (Map<String, Object>) requestContext.get("params");
		
		String productStoreId = (String) params.get("productStoreId");
		Debug.log("params-----"+params);
		try {
			result.put("productStoreId", productStoreId);
			if (UtilValidate.isEmpty(params)) {
				result.putAll(ServiceUtil.returnError("Product receipt data is empty"));
				return result;
			}
			if (UtilValidate.isNotEmpty(productStoreId)) {
				 GenericValue productStore = delegator.findOne("ProductStore",UtilMisc.toMap("productStoreId",productStoreId), false);
				 if(UtilValidate.isEmpty(productStore)){
					result.putAll(ServiceUtil.returnError("Product store not found for : "+productStoreId));
					return result;
				 }
				//iterating over keys only
				for (String key : params.keySet()) {
					if(UtilValidate.isNotEmpty(productStoreId)){
						System.out.println("Key = " + key);
					    System.out.println("Key = " + params.get(key));		    
					    GenericValue storeAttributes = delegator.findOne("StoreAttribute",UtilMisc.toMap("productStoreId",productStoreId , "attrName" , key), false);
						Debug.log("storeAttributes====222==========="+storeAttributes);

					    if(UtilValidate.isEmpty(storeAttributes)){
					    	GenericValue storeAttribute= delegator.makeValue("StoreAttribute");
				        	storeAttribute.set("productStoreId",productStoreId);
				        	storeAttribute.set("attrName",key);
				        	storeAttribute.set("attrValue",params.get(key));
				        	delegator.create(storeAttribute);
							Debug.log("storeAttribute====if==========="+storeAttribute);
					    }
					    else{
					    	storeAttributes.set("attrValue",params.get(key));
				        	delegator.store(storeAttributes);
							Debug.log("storeAttributes====else==========="+storeAttributes);

					    }
					    
					}
				}
			}else {
				result.putAll(ServiceUtil.returnError("Product store not found"));
				return result;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.put("productStoreId", productStoreId);
		result.putAll(ServiceUtil.returnSuccess("Successfully created Store info.."));
		return result;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static Map updateStoreReceipt(DispatchContext dctx, Map context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Map requestContext = (Map) context.get("requestContext");
		Map<String, Object> params = (Map<String, Object>) requestContext.get("params");
		
		String productStoreId = (String) params.get("productStoreId");
		Debug.log("params-----"+params);
		try {
			result.put("productStoreId", productStoreId);
			if (UtilValidate.isNotEmpty(params)) {
				 GenericValue productStore = delegator.findOne("ProductStore",UtilMisc.toMap("productStoreId",productStoreId), false);
				 if(UtilValidate.isEmpty(productStore)){
					result.putAll(ServiceUtil.returnError("Product Store not found for : "+productStoreId));
					return result;
				 }
				//iterating over keys only
				for (String key : params.keySet()) {
					if(UtilValidate.isNotEmpty(productStoreId)){
						System.out.println("Key = " + key);
					    System.out.println("Key = " + params.get(key));	
					    String keyValue = (String) params.get(key);
					    GenericValue storeAttributes = delegator.findOne("StoreAttribute",UtilMisc.toMap("productStoreId",productStoreId , "attrName" , key), false);
						Debug.log("storeAttributes====222==========="+storeAttributes);
						if(UtilValidate.isNotEmpty(storeAttributes)){
							if(key.equalsIgnoreCase("storeImage") && UtilValidate.isEmpty(keyValue)){
					    		
					    	}else{
						    	storeAttributes.set("attrValue",params.get(key));
					        	delegator.store(storeAttributes);
								Debug.log("storeAttributes====else==========="+storeAttributes);
	
						    }
						}else {
							GenericValue storeAttribute= delegator.makeValue("StoreAttribute");
				        	storeAttribute.set("productStoreId",productStoreId);
				        	storeAttribute.set("attrName",key);
				        	storeAttribute.set("attrValue",params.get(key));
				        	delegator.create(storeAttribute);
						}
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.put("productStoreId", productStoreId);
		result.putAll(ServiceUtil.returnSuccess("Successfully update Store info.."));
		return result;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static Map deleteStoreReceipt(DispatchContext dctx, Map context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Map requestContext = (Map) context.get("requestContext");
		String productStoreId = (String) requestContext.get("productStoreId");
		List productStoreIdList = FastList.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
		try {
			productStoreIdList = DataUtil.stringToList(productStoreId, ",");
			if (UtilValidate.isNotEmpty(productStoreIdList)) {
				 List <GenericValue> storeAttribute = delegator.findList("StoreAttribute", EntityCondition.makeCondition("productStoreId",EntityOperator.IN,productStoreIdList), null, null, null, false);
				 if(UtilValidate.isNotEmpty(storeAttribute)){
					 delegator.removeAll(storeAttribute);
				 }
			}
		}catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		
		result.putAll(ServiceUtil.returnSuccess("Successfully deleted Store info.."));
		return result;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static Map createStoreReceiptStaging(DispatchContext dctx, Map context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Map requestContext = (Map) context.get("requestContext");
		Map<String,Object> params = (Map) requestContext.get("params");
		Debug.log("params---"+params);
		try {
			result.putAll(ServiceUtil.returnSuccess("Successfully saved Store info.."));
			if (UtilValidate.isNotEmpty(params)) {
				String productStoreId = (String) params.get("productStoreId");
				GenericValue srs = delegator.makeValue("StoreReceiptStaging");
				for (String key : params.keySet()) {
					String val = (String) params.get(key);
					if(UtilValidate.isNotEmpty(key) && (key.equalsIgnoreCase("storeId") || key.equalsIgnoreCase("storeName"))){
						if(UtilValidate.isEmpty(val)){
							result.putAll(ServiceUtil.returnSuccess("store Id And Name cannot be empty"));
							srs = null;
							break;
						}
					}
					srs.put(key, val);
				}
				Debug.log("srs---"+srs);
				if (UtilValidate.isNotEmpty(srs)) {
					delegator.createOrStore(srs);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		
		return result;
	}
}
