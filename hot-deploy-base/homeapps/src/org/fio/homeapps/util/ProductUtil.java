package org.fio.homeapps.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

/**
 * @author Sharif
 *
 */
public class ProductUtil {
	
	private static String MODULE = ProductUtil.class.getName();
	
	public static boolean checkAvailabilityOfProductInStore(LocalDispatcher dispatcher, GenericValue userLogin, String productStoreId, String productId){
		
		boolean available = false;
		
		try {
			Map<String, Object> callCtxt = FastMap.newInstance();
			
			callCtxt.put("productStoreId", productStoreId);
			callCtxt.put("userLogin", userLogin);
			
			Map<String, Object> serviceResult = dispatcher.runSync("amws.findProductsByStore", callCtxt);
			
			Map productIds = (Map) serviceResult.get("productIds");
			
			if(productIds.get(productId)!=null){
				available = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return available;
	}
	
	/**
     * Helper method that returns the SKU of given product.
     *
     * @param productId the product to get the SKU for
     * @param delegator a <code>Delegator</code> value
     * @return the Product SKU, or <code>null</code> if no SKU is set
     * @throws GenericEntityException if an error occurs
     */
    public static String getProductSKU(String productId, Delegator delegator) throws GenericEntityException {
    	EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
				EntityCondition.makeCondition("goodIdentificationTypeId", EntityOperator.EQUALS, "SKU"),
				EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId),
				EntityCondition.makeCondition("idValue", EntityOperator.NOT_EQUAL, null)
			);
		GenericValue sku = EntityUtil.getFirst(delegator.findList("GoodIdentification", condition, null, null, null, true));
		
        //GenericValue sku = delegator.findByPrimaryKey("GoodIdentification", UtilMisc.toMap("goodIdentificationTypeId", "SKU", "productId", productId));
        if (sku != null) {
            return sku.getString("idValue");
        }
        return null;
    }
    
    public static void createOrUpdateProductContent(Delegator delegator, GenericValue userLogin, String productId, String textData, String productContentTypeId){
		
		try {
			if(UtilValidate.isEmpty(productId) || UtilValidate.isEmpty(productContentTypeId)){
				return;
			}
			
			String primaryLanguage = "en_US";
			
			EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId),
                    EntityCondition.makeCondition("productContentTypeId", EntityOperator.EQUALS, productContentTypeId),
                    EntityUtil.getFilterByDateExpr());
			
			GenericValue productContent = EntityUtil.getFirst(delegator.findList("ProductContent", mainCondition, null, Arrays.asList("fromDate DESC"), null, false));
			if(UtilValidate.isNotEmpty(productContent)){
				String contentId = productContent.getString("contentId");
				GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
				String dataResourceId = content.getString("dataResourceId");
				if(UtilValidate.isNotEmpty(dataResourceId)){
					GenericValue electronicText = delegator.findOne("ElectronicText", UtilMisc.toMap("dataResourceId", dataResourceId), false);
					if(UtilValidate.isNotEmpty(electronicText)){
						electronicText.set("textData",textData);							  
						electronicText.store();
					} else{
						electronicText = delegator.makeValue("ElectronicText",UtilMisc.toMap("dataResourceId",dataResourceId));						  						       
						electronicText.set("textData",textData);							  
						electronicText.create();
					}
					return;
				}
			}
			
			String contentId=delegator.getNextSeqId("Content");
			String dataResourceId=delegator.getNextSeqId("DataResource");	
			
			GenericValue dataResource=delegator.makeValue("DataResource",UtilMisc.toMap("dataResourceId",dataResourceId));						  						       
			dataResource.set("dataResourceTypeId","ELECTRONIC_TEXT");							  
			dataResource.set("dataTemplateTypeId","FTL");
			dataResource.set("localeString", primaryLanguage);
			dataResource.set("createdDate", UtilDateTime.nowTimestamp());
			dataResource.set("createdByUserLogin", userLogin.getString("userLoginId"));
			dataResource.create();
			
			GenericValue content = delegator.makeValue("Content",UtilMisc.toMap("contentId",contentId));						  						       
			content.set("contentTypeId","DOCUMENT");							  
			content.set("dataResourceId",dataResourceId);
			content.set("localeString",primaryLanguage);
			content.set("createdDate",UtilDateTime.nowTimestamp());
			content.set("createdByUserLogin",userLogin.getString("userLoginId"));
			content.create();
			
			GenericValue electronicText =delegator.makeValue("ElectronicText",UtilMisc.toMap("dataResourceId",dataResourceId));						  						       
			electronicText.set("textData",textData);							  
			electronicText.create();
			
			productContent = delegator.makeValue("ProductContent",UtilMisc.toMap("productId",productId));						  						       
			productContent.set("contentId",contentId);							  
			//productContent.set("useWebOnly",primary_UseWebOnly);
			productContent.set("productContentTypeId", productContentTypeId);					  
			productContent.set("fromDate",UtilDateTime.nowTimestamp());
			productContent.create();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
	}
    
    public static String loadProductContent(Delegator delegator, GenericValue userLogin, String productId, String productContentTypeId){
		
    	String textData = null;
    	
		try {
			
			if(UtilValidate.isEmpty(productId) || UtilValidate.isEmpty(productContentTypeId)){
				return textData;
			}
			
			EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId),
                    EntityCondition.makeCondition("productContentTypeId", EntityOperator.EQUALS, productContentTypeId),
                    EntityUtil.getFilterByDateExpr());
			
			GenericValue productContent = EntityUtil.getFirst(delegator.findList("ProductContent", mainCondition, null, Arrays.asList("fromDate DESC"), null, false));
			if(UtilValidate.isNotEmpty(productContent)){
				String contentId = productContent.getString("contentId");
				GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
				String dataResourceId = content.getString("dataResourceId");
				if(UtilValidate.isNotEmpty(dataResourceId)){
					GenericValue electronicText = delegator.findOne("ElectronicText", UtilMisc.toMap("dataResourceId", dataResourceId), false);
					if(UtilValidate.isNotEmpty(electronicText)){
						textData = electronicText.getString("textData");
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return textData;
	}
    
    public static String getProductAttrValue(Delegator delegator, String productId, String attrType, String attrName) {
		
    	String attrValue = null;
    	
		try {
			
			if(UtilValidate.isEmpty(productId) || UtilValidate.isEmpty(attrType) || UtilValidate.isEmpty(attrName)){
				return attrValue;
			}
			
			EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId),
                    EntityCondition.makeCondition("attrType", EntityOperator.EQUALS, attrType),
                    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, attrName)
                    );
			
			GenericValue productAttribute = EntityUtil.getFirst(delegator.findList("ProductAttribute", mainCondition, null, Arrays.asList("attrName DESC"), null, false));
			if(UtilValidate.isNotEmpty(productAttribute)){
				attrValue = productAttribute.getString("attrValue");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return attrValue;
	}
    
    public static String getProductName(Delegator delegator, String productId) {
    	String productName = null;
		try {
			
			if(UtilValidate.isEmpty(productId)){
				return productName;
			}
			
			EntityCondition mainCondition = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			
			GenericValue product = EntityUtil.getFirst(delegator.findList("Product", mainCondition, null, null, null, false));
			if(UtilValidate.isNotEmpty(product)) {
				productName = UtilValidate.isNotEmpty(product.getString("productName")) ? product.getString("productName") : product.getString("internalName");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return productName;
	}
    
    public static GenericValue getProduct(Delegator delegator, String productId) {
    	GenericValue product = null;
		try {
			
			if(UtilValidate.isEmpty(productId)){
				return product;
			}
			
			EntityCondition mainCondition = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			
			product = EntityUtil.getFirst(delegator.findList("Product", mainCondition, null, null, null, false));
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return product;
	}
    
    public static void createOrUpdateProductAttribute(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String productId, String attrType, String attrName, String attrValue) {
		
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
		try {
			
			if(UtilValidate.isEmpty(productId) || UtilValidate.isEmpty(attrType)){
				return;
			}
			
			callCtxt = FastMap.newInstance();
			callCtxt.put("productId", productId);
			callCtxt.put("attrType", attrType);
			callCtxt.put("attrName", attrName);
			callCtxt.put("attrValue", attrValue);
			callCtxt.put("userLogin", userLogin);
			GenericValue attrInstance = EntityUtil.getFirst( delegator.findByAnd("ProductAttribute", UtilMisc.toMap("productId", productId, "attrType", attrType, "attrName", attrName), null, false) );
			if (UtilValidate.isNotEmpty(attrInstance)) {
				callResult = dispatcher.runSync("updateProductAttribute", callCtxt);
			} else {
				callResult = dispatcher.runSync("createProductAttribute", callCtxt);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
	}
    
    public static GenericValue getServiceProduct(Delegator delegator, String chargeProductId) {
		
    	GenericValue serviceProduct = null;
    	
		try {
			
			if(UtilValidate.isEmpty(chargeProductId)){
				return serviceProduct;
			}
			
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("productId", EntityOperator.EQUALS, chargeProductId),
					//EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, serviceCode),
					EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "PRODUCT_COMPONENT"),
					EntityUtil.getFilterByDateExpr()
				);
			GenericValue productAssoc = EntityUtil.getFirst(delegator.findList("ProductAssoc", condition, null, null, null, false));
			if(UtilValidate.isNotEmpty(productAssoc)) {
				serviceProduct = EntityUtil.getFirst( delegator.findByAnd("Product", UtilMisc.toMap("productId", productAssoc.getString("productIdTo")), null, false) );
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return serviceProduct;
	}
    
    public static GenericValue getShoppingListType(Delegator delegator, String value) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("shoppingListTypeId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			) );    
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue entity = EntityQuery.use(delegator).from("ShoppingListType").where(mainConditons).queryFirst();
				return entity;
			}
		} catch (Exception e) {
		}
		return null;
	}
    
    public static GenericValue getProductStore(Delegator delegator, String productStoreId) {
		try {
			if (UtilValidate.isNotEmpty(productStoreId)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));       
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue entity = EntityQuery.use(delegator).from("ProductStore").where(mainConditons).queryFirst();
				return entity;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
}
