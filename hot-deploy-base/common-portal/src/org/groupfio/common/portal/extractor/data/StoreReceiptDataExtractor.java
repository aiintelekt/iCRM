package org.groupfio.common.portal.extractor.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.common.portal.extractor.constants.DataConstants;
import org.groupfio.common.portal.util.UtilAttribute;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sharif
 *
 */
public class StoreReceiptDataExtractor extends DataExtractor {
	
	private static final Logger log = LoggerFactory.getLogger(StoreReceiptDataExtractor.class);
	
	public StoreReceiptDataExtractor(Data extractedData) {
		super(extractedData);
	}
	
	@Override
	public Map<String, Object> retrieve(Map<String, Object> context) {
		if (UtilValidate.isNotEmpty(extractedData)) {
			extractedData.retrieve(context);
		}
		return retrieveData(context);
	}

	public Map<String, Object> retrieveData(Map<String, Object> context) {
		
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator"); 
				Map<String, Object> request = (Map<String, Object>) context.get("request");
				response = (Map<String, Object>) context.get("response");
				
				String productStoreId = ParamUtil.getString(request, "productStoreId");
				if (UtilValidate.isNotEmpty(productStoreId)) {
					
					Map<String, Object> storeReceiptData = new LinkedHashMap<String, Object>();
					String storeName = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "storeName");
					String address = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "address");
					String address2 = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "address2");
					String brand = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "brand");
					String city = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "city");
					String country = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "country");
					String district = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "district");
					String email = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "email");
					String manager = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "manager");
					String phone = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "phone");
					String returnPolicy = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "returnPolicy");
					String state = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "state");
					String storeImage = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "storeImage");
					String storeImageURL = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "storeImageURL");
					String url1 = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "url1");
					String url2 = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "url2");
					String url3 = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "url3");
					String url4 = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "url4");
					String zip = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", productStoreId, "zip");
					
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_NAME"), Objects.toString(storeName, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_ADDRESS"), Objects.toString(address, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_ADDRESS_2"), Objects.toString(address2, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_CITY"), Objects.toString(city, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_STATE"), Objects.toString(state, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_POSTAL"), Objects.toString(zip, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_PHONE"), Objects.toString(phone, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_BRAND"), Objects.toString(brand, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_COUNTRY"), Objects.toString(country, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_DISTRICT"), Objects.toString(district, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_EMAIL"), Objects.toString(email, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_MANAGER"), Objects.toString(manager, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_RETURN_POLICY"), Objects.toString(returnPolicy, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_IMAGE"), Objects.toString(storeImage, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_IMAGE_URL"), Objects.toString(storeImageURL, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_URL1"), Objects.toString(url1, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_URL2"), Objects.toString(url2, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_URL3"), Objects.toString(url3, ""));
					storeReceiptData.put(DataConstants.STORE_RECEIPT_TAG.get("STORE_URL4"), Objects.toString(url4, ""));
					
					response.put("storeReceiptData", storeReceiptData);
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return response;
	}

}
