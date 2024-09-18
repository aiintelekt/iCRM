package org.fio.admin.portal.setup.product;

import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.base.util.Debug;
import org.ofbiz.service.DispatchContext;
import java.util.Map;
import org.ofbiz.entity.Delegator;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.security.Security;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.apache.commons.lang.StringUtils;
import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.admin.portal.setup.paramUnit.ServiceRequest;
import org.fio.admin.portal.util.DataUtil;
import org.fio.homeapps.constants.GlobalConstants.ModeOfAction;

import java.sql.Timestamp;
import java.util.Locale;


public class ConfigureProduct {
	public static final String MODULE = ConfigureProduct.class.getName();
    public static final String RESOURCE = "AdminPortalUiLabels";
    

    public static Map < String, Object > productCatalogCreation(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside productCatalogCreation------" + context, MODULE);
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();

        String productCatalog = (String) context.get("productCatalog");
        String isEnable = (String) context.get("isEnable");
        String sequenceNumber = (String) context.get("sequenceNumber");
        
        try {
            GenericValue prodCatalog = EntityQuery.use(delegator).from("ProdCatalog").where("catalogName",productCatalog).queryFirst();
            	if(UtilValidate.isEmpty(prodCatalog)) {
            		String prodCatalogId = delegator.getNextSeqId("ProdCatalog");
            		
            		GenericValue addCatalog = delegator.makeValue("ProdCatalog");
            		addCatalog.put("prodCatalogId", prodCatalogId);
            		addCatalog.put("catalogName", productCatalog);
            		addCatalog.put("isEnable", isEnable);
            		addCatalog.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
            		addCatalog.create();
            		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ProductCatalogCreatedSuccessfully", locale));
                    results.put("prodCatalogId", prodCatalogId);
                   
            	}
            	else {
                return ServiceUtil.returnError("Product Catalog exists");
            }

        } catch (GeneralException e) {e.printStackTrace();
            Debug.log("==error in creations===" + e.getMessage());
        }

        return results;

    }
    public static Map < String, Object > productCatalogUpdation(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside productCatalogUpdation------" + context, MODULE);
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();

        String productCatalog = (String) context.get("productCatalog");
        String prodCatalogId = (String) context.get("prodCatalogId");
        String isEnable = (String) context.get("isEnable");
        String sequenceNumber = (String) context.get("sequenceNumber");
        
        try {
            GenericValue updateCatalog = EntityQuery.use(delegator).from("ProdCatalog").where("prodCatalogId",prodCatalogId).queryOne();
            	if(UtilValidate.isNotEmpty(updateCatalog)) {
            		if (updateCatalog.get("catalogName").toString().equals(productCatalog)) {
            			updateCatalog.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
            			updateCatalog.put("isEnable", isEnable);
            			updateCatalog.store();
                        results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ProductCatalogUpdatedSuccessfully", locale));
                        results.put("prodCatalogId", prodCatalogId);
                        
            		}else {
            			GenericValue updateCatalogName = EntityQuery.use(delegator).from("ProdCatalog").where("catalogName",productCatalog).queryFirst();
            			if(UtilValidate.isEmpty(updateCatalogName)) {
            				updateCatalog.put("catalogName",productCatalog);
            				updateCatalog.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
                			updateCatalog.put("isEnable", isEnable);
            				updateCatalog.store();
            				results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ProductCatalogUpdatedSuccessfully", locale));
                            results.put("prodCatalogId", prodCatalogId);
                           
            			}else return ServiceUtil.returnError("Product Catalog exists");
            		}
            	}
        } catch (GeneralException e) {e.printStackTrace();
            Debug.log("==error in creations===" + e.getMessage());
        }

        return results;

    }
    public static Map < String, Object > productCategoryCreation(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside productCategoryCreation------" + context, MODULE);
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();

        String categoryName = (String) context.get("categoryName");
        String prodCatalogId = (String) context.get("prodCatalogId");
        String isEnable = (String) context.get("isEnable");
        String sequenceNumber = (String) context.get("sequenceNumber");
        
        try {
            GenericValue productCategory = EntityQuery.use(delegator).from("ProductCategory").where("categoryName",categoryName).queryFirst();
            	if(UtilValidate.isEmpty(productCategory)) {
            		String productCategoryId = delegator.getNextSeqId("ProductCategory");
            		
            		GenericValue addCategory = delegator.makeValue("ProductCategory");
            		addCategory.put("productCategoryId", productCategoryId);
            		addCategory.put("productCategoryTypeId", "CATALOG_CATEGORY");
            		addCategory.put("categoryName", categoryName);
            		addCategory.create();
            		
            		GenericValue addDataResource = delegator.makeValue("DataResource");
            		String dataResourceId = delegator.getNextSeqId("DataResource");
            		addDataResource.put("dataResourceId",dataResourceId);
            		addDataResource.put("dataResourceTypeId","ELECTRONIC_TEXT");
            		addDataResource.create();
            		
            		GenericValue addContent = delegator.makeValue("Content");
            		String contentId = delegator.getNextSeqId("Content");
            		addContent.put("contentId", contentId);
            		addContent.put("contentTypeId", "DOCUMENT");
            		addContent.put("dataResourceId", dataResourceId);
            		addContent.put("localeString", "US_en");
            		addContent.create();
            		
            		GenericValue addElectronicText = delegator.makeValue("ElectronicText");
            		addElectronicText.put("dataResourceId",dataResourceId);
            		addElectronicText.put("textData",categoryName);
            		addElectronicText.create();
            		
            		GenericValue addProductContent = delegator.makeValue("ProductCategoryContent");
            		addProductContent.put("contentId", contentId);
            		addProductContent.put("productCategoryId", productCategoryId);
            		addProductContent.put("prodCatContentTypeId", "CATEGORY_NAME");
            		addProductContent.put("fromDate", UtilDateTime.nowTimestamp());
            		addProductContent.create();
            		
            		GenericValue addCategoryAssoc = delegator.makeValue("ProdCatalogCategory");
            		addCategoryAssoc.put("productCategoryId",productCategoryId);
            		addCategoryAssoc.put("prodCatalogId",prodCatalogId);
            		addCategoryAssoc.put("prodCatalogCategoryTypeId","PCCT_BROWSE_ROOT");
            		addCategoryAssoc.put("fromDate", UtilDateTime.nowTimestamp());
            		addCategoryAssoc.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
            		addCategoryAssoc.create();
            		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ProductCategoryCreatedSuccessfully", locale));
                    results.put("productCategoryId", productCategoryId);
                    results.put("prodCatalogId", prodCatalogId);
                   
            	}else {
                return ServiceUtil.returnError("Product Category exists");
            }

        } catch (GeneralException e) {e.printStackTrace();
            Debug.log("==error in creations===" + e.getMessage());
        }

        return results;

    }
    
    /*public static Map < String, Object > productCategoryUpdation(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside productCategoryUpdation------" + context, MODULE);
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();

        String categoryName = (String) context.get("categoryName");
        String prodCatalogId = (String) context.get("prodCatalogId");
        String productCategoryId = (String) context.get("productCategoryId");
        String isEnable = (String) context.get("isEnable");
        String sequenceNumber = (String) context.get("sequenceNumber");
        
        
        try {
            EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("categoryName", EntityOperator.EQUALS, categoryName),
                    EntityCondition.makeCondition("productCategoryId", EntityOperator.NOT_EQUAL, productCategoryId));
                
                GenericValue productCategory = EntityQuery.use(delegator).from("ProductCategory").where(condition).queryFirst();
                if(UtilValidate.isNotEmpty(productCategory)) {
                	return ServiceUtil.returnError("Product Category exists");
                }
                else {
                	GenericValue productNewCategory = EntityQuery.use(delegator).from("ProductCategory").where("productCategoryId",productCategoryId).queryOne();
                	productNewCategory.put("categoryName",categoryName);
                	productNewCategory.store();
                	
                	if(isEnable.equals("N")) {
                		GenericValue productCategoryStatus = EntityQuery.use(delegator).from("ProdCatalogCategory").where("productCategoryId",productCategoryId,"prodCatalogId",prodCatalogId).queryOne();
                		productCategoryStatus.put("thruDate", UtilDateTime.nowTimestamp());
                		productCategoryStatus.store();
                	}else {
                		EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,
                                EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, prodCatalogId),
                                EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId),
                				EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
                            
                            GenericValue productCategoryDetails = EntityQuery.use(delegator).from("ProdCatalogCategory").where(condition1).queryFirst();
                            if(UtilValidate.isEmpty(productCategoryDetails)) {
                            	productCategoryDetails.put("productCategoryId", productCategoryId);
                            	productCategoryDetails.put("prodCatalogId", prodCatalogId);
                            	productCategoryDetails.put("prodCatalogCategoryTypeId", "PCCT_BROWSE_ROOT");
                            	productCategoryDetails.put("fromDate", "UtilDateTime.nowTimestamp()");
                            	productCategoryDetails.create();
                            }else {
                            	productCategoryDetails.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
                            	productCategoryDetails.store();
                            }
                	}
                	results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ProductCategoryUpdatedSuccessfully", locale));
                    results.put("productCategoryId", productCategoryId);
                    results.put("prodCatalogId", prodCatalogId);
                }
                
        	} catch (GeneralException e) {e.printStackTrace();
            Debug.log("==error in creations===" + e.getMessage());
        }

        return results;

    }*/
    public static Map < String, Object > productCategoryUpdation(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside productCategoryUpdation------" + context, MODULE);
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();

        String categoryName = (String) context.get("categoryName");
        String prodCatalogId = (String) context.get("prodCatalogId");
        String productCategoryId = (String) context.get("productCategoryId");
        String isEnable = (String) context.get("isEnable");
        String sequenceNumber = (String) context.get("sequenceNumber");
       
        
        try {
        	EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
            EntityCondition.makeCondition("categoryName", EntityOperator.EQUALS, categoryName),
                    EntityCondition.makeCondition("productCategoryId", EntityOperator.NOT_EQUAL, productCategoryId));
                
                GenericValue productCategory = EntityQuery.use(delegator).from("ProductCategory").where(condition).queryFirst();
                if(UtilValidate.isNotEmpty(productCategory)) {
                	return ServiceUtil.returnError("Category Category exists");
                }
                else {
                	GenericValue productNewCategory = EntityQuery.use(delegator).from("ProductCategory").where("productCategoryId",productCategoryId).queryOne();
                	productNewCategory.put("categoryName",categoryName);
                	productNewCategory.store();
                	
                	GenericValue productCategorDetail = EntityQuery.use(delegator).from("ProductCategoryContent").where("productCategoryId",productCategoryId).queryFirst();
                	if(UtilValidate.isNotEmpty(productCategorDetail))
                	{
                		GenericValue content = EntityQuery.use(delegator).from("Content").where("contentId",productCategorDetail.getString("contentId")).queryFirst();
                		if(UtilValidate.isNotEmpty(content)) {
                			GenericValue contentResource = EntityQuery.use(delegator).from("ElectronicText").where("dataResourceId",content.getString("dataResourceId")).queryFirst();
                			if(UtilValidate.isNotEmpty(contentResource)) {
                				contentResource.put("textData", categoryName);
                				contentResource.store();
                			}
                		}
                		
                	}
                	
                		
						EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,
                                EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, prodCatalogId),
                                EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId),
                				EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
                            
                            GenericValue productCategoryDetails = EntityQuery.use(delegator).from("ProdCatalogCategory").where(condition1).orderBy("fromDate DESC").queryFirst();
                            if(UtilValidate.isEmpty(productCategoryDetails)) {
                            	GenericValue productCategoryStatus = EntityQuery.use(delegator).from("ProdCatalogCategory").where("productCategoryId",productCategoryId,"thruDate", null).orderBy("fromDate DESC").queryFirst();
                            	if(UtilValidate.isNotEmpty(productCategoryStatus)) {
                            		productCategoryStatus.put("thruDate", UtilDateTime.nowTimestamp());
                            		productCategoryStatus.store();
                            	}
                        		
                        		GenericValue addCategoryProduct = delegator.makeValue("ProdCatalogCategory");
                        		addCategoryProduct.put("prodCatalogId", prodCatalogId);
                        		addCategoryProduct.put("productCategoryId", productCategoryId);
                        		addCategoryProduct.put("fromDate", UtilDateTime.nowTimestamp());
                        		addCategoryProduct.put("prodCatalogCategoryTypeId","PCCT_BROWSE_ROOT");
                        		addCategoryProduct.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
                            	if(isEnable.equals("N"))
                            		addCategoryProduct.put("thruDate", UtilDateTime.nowTimestamp());
                            	addCategoryProduct.create();
                            }else {
                            	productCategoryDetails.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
                            	if(isEnable.equals("N"))
                            		productCategoryDetails.put("thruDate", UtilDateTime.nowTimestamp());
                            	productCategoryDetails.store();
                            }
                	
                	results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ProductCategoryUpdatedSuccessfully", locale));
                    results.put("productCategoryId", productCategoryId);
                    results.put("prodCatalogId", prodCatalogId);
                    
                }
                
        	} catch (GeneralException e) {e.printStackTrace();
            Debug.log("==error in creations===" + e.getMessage());
        }

        return results;

    }



    public static Map < String, Object > productSubCategoryCreation(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside productSubCategoryCreation------" + context, MODULE);
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();

        String categoryName = (String) context.get("subCategoryName");
        String prodCatalogId = (String) context.get("prodCatalogId");
        String productCategoryId = (String) context.get("productCategoryId");
        String isEnable = (String) context.get("isEnable");
        String sequenceNumber = (String) context.get("sequenceNumber");
        
        try {
            GenericValue productSubCategory = EntityQuery.use(delegator).from("ProductCategory").where("categoryName",categoryName).queryFirst();
            	if(UtilValidate.isEmpty(productSubCategory)) {
            		String subCategoryId = delegator.getNextSeqId("ProductCategory");
            		
            		GenericValue addCategory = delegator.makeValue("ProductCategory");
            		addCategory.put("productCategoryId", subCategoryId);
            		addCategory.put("productCategoryTypeId", "CATALOG_CATEGORY");
            		addCategory.put("categoryName", categoryName);
            		addCategory.create();
            		
            		GenericValue addDataResource = delegator.makeValue("DataResource");
            		String dataResourceId = delegator.getNextSeqId("DataResource");
            		addDataResource.put("dataResourceId",dataResourceId);
            		addDataResource.put("dataResourceTypeId","ELECTRONIC_TEXT");
            		addDataResource.create();
            		
            		GenericValue addContent = delegator.makeValue("Content");
            		String contentId = delegator.getNextSeqId("Content");
            		addContent.put("contentId", contentId);
            		addContent.put("contentTypeId", "DOCUMENT");
            		addContent.put("dataResourceId", dataResourceId);
            		addContent.put("localeString", "US_en");
            		addContent.create();
            		
            		GenericValue addElectronicText = delegator.makeValue("ElectronicText");
            		addElectronicText.put("dataResourceId",dataResourceId);
            		addElectronicText.put("textData",categoryName);
            		addElectronicText.create();
            		
            		GenericValue addProductContent = delegator.makeValue("ProductCategoryContent");
            		addProductContent.put("contentId", contentId);
            		addProductContent.put("productCategoryId", subCategoryId);
            		addProductContent.put("prodCatContentTypeId", "CATEGORY_NAME");
            		addProductContent.put("fromDate", UtilDateTime.nowTimestamp());
            		addProductContent.create();
            		
            		GenericValue addCategoryAssoc = delegator.makeValue("ProductCategoryRollup");
            		addCategoryAssoc.put("productCategoryId",subCategoryId);
            		addCategoryAssoc.put("parentProductCategoryId",productCategoryId);
            		addCategoryAssoc.put("fromDate", UtilDateTime.nowTimestamp());
            		addCategoryAssoc.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
            		addCategoryAssoc.create();
            		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ProductSubCategoryCreatedSuccessfully", locale));
                    results.put("productCategoryId", productCategoryId);
                    results.put("prodCatalogId", prodCatalogId);
                    results.put("subCategoryId", subCategoryId);
                   
            	}else {
                return ServiceUtil.returnError("Product Sub Category exists");
            }

        } catch (GeneralException e) {e.printStackTrace();
            Debug.log("==error in creations===" + e.getMessage());
        }

        return results;

    }

    public static Map < String, Object > productCreation(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside productSubCategoryCreation------" + context, MODULE);
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();
        
        String product = (String) context.get("product");
        String subCategoryId = (String) context.get("subCategoryId");
        String prodCatalogId = (String) context.get("prodCatalogId");
        String productCategoryId = (String) context.get("productCategoryId");
        String isEnable = (String) context.get("isEnable");
        String sequenceNumber = (String) context.get("sequenceNumber");
        
        try {
            GenericValue products = EntityQuery.use(delegator).from("Product").where("productId",product,"productTypeId","FINISHED_GOOD").queryFirst();
            	if(UtilValidate.isEmpty(products)) {
            		String productId = delegator.getNextSeqId("Product");
            		
            		GenericValue addProduct = delegator.makeValue("Product");
            		addProduct.put("productId", productId);
            		addProduct.put("productName", product);
            		addProduct.put("productTypeId", "FINISHED_GOOD");
            		addProduct.create();
            		
            		GenericValue addDataResource = delegator.makeValue("DataResource");
            		String dataResourceId = delegator.getNextSeqId("DataResource");
            		addDataResource.put("dataResourceId",dataResourceId);
            		addDataResource.put("dataResourceTypeId","ELECTRONIC_TEXT");
            		addDataResource.create();
            		
            		GenericValue addContent = delegator.makeValue("Content");
            		String contentId = delegator.getNextSeqId("Content");
            		addContent.put("contentId", contentId);
            		addContent.put("contentTypeId", "DOCUMENT");
            		addContent.put("dataResourceId", dataResourceId);
            		addContent.put("localeString", "US_en");
            		addContent.create();
            		
            		GenericValue addProductContent = delegator.makeValue("ProductContent");
            		addProductContent.put("contentId", contentId);
            		addProductContent.put("productId", productId);
            		addProductContent.put("productContentTypeId", "PRODUCT_NAME");
            		addProductContent.put("fromDate", UtilDateTime.nowTimestamp());
            		addProductContent.create();
            		
            		GenericValue addElectronicText = delegator.makeValue("ElectronicText");
            		addElectronicText.put("dataResourceId",dataResourceId);
            		addElectronicText.put("textData",product);
            		addElectronicText.create();
            		
            		GenericValue addCategoryProduct = delegator.makeValue("ProductCategoryMember");
            		addCategoryProduct.put("productId", productId);
            		addCategoryProduct.put("productCategoryId", subCategoryId);
            		addCategoryProduct.put("fromDate", UtilDateTime.nowTimestamp());
            		addCategoryProduct.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
            		addCategoryProduct.create();
            		
            		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ProductCreatedSuccessfully", locale));
                    results.put("productCategoryId", productCategoryId);
                    results.put("prodCatalogId", prodCatalogId);
                    results.put("subCategoryId", subCategoryId);
                    results.put("productId", productId);
                   
            	}else {
                return ServiceUtil.returnError("Product already exists");
            }

        } catch (GeneralException e) {e.printStackTrace();
            Debug.log("==error in creations===" + e.getMessage());
        }

        return results;

    }
    public static Map < String, Object > productUpdation(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside productCategoryUpdation------" + context, MODULE);
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();

        String product = (String) context.get("product");
        String prodCatalogId = (String) context.get("prodCatalogId");
        String productCategoryId = (String) context.get("productCategoryId");
        String productId = (String) context.get("productId");
        String subCategoryId = (String) context.get("subCategoryId");
        String isEnable = (String) context.get("isEnable");
        String sequenceNumber = (String) context.get("sequenceNumber");
       
        
        try {
            EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("productName", EntityOperator.EQUALS, product),
                    EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, productId));
                
                GenericValue products = EntityQuery.use(delegator).from("Product").where(condition).queryFirst();
                if(UtilValidate.isNotEmpty(products)) {
                	return ServiceUtil.returnError("Product  exists");
                }
                else {
                	GenericValue productNewCategory = EntityQuery.use(delegator).from("Product").where("productId",productId).queryOne();
                	productNewCategory.put("productName",product);
                	productNewCategory.store();
                	
                	GenericValue productCategory = EntityQuery.use(delegator).from("ProductContent").where("productId",productId).queryFirst();
                	if(UtilValidate.isNotEmpty(productCategory))
                	{
                		GenericValue content = EntityQuery.use(delegator).from("Content").where("contentId",productCategory.getString("contentId")).queryFirst();
                		if(UtilValidate.isNotEmpty(content)) {
                			GenericValue contentResource = EntityQuery.use(delegator).from("ElectronicText").where("dataResourceId",content.getString("dataResourceId")).queryFirst();
                			if(UtilValidate.isNotEmpty(contentResource)) {
                				contentResource.put("textData", product);
                				contentResource.store();
                			}
                		}
                		
                	}
                	
                		EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,
                                EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId),
                                EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, subCategoryId),
                				EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
                            
                            GenericValue productCategoryDetails = EntityQuery.use(delegator).from("ProductCategoryMember").where(condition1).orderBy("fromDate DESC").queryFirst();
                            if(UtilValidate.isEmpty(productCategoryDetails)) {
                            	GenericValue productCategoryStatus = EntityQuery.use(delegator).from("ProductCategoryMember").where("productId",productId,"thruDate", null).orderBy("fromDate DESC").queryFirst();
                            	if(UtilValidate.isNotEmpty(productCategoryStatus)) {
                            		productCategoryStatus.put("thruDate", UtilDateTime.nowTimestamp());
                            		productCategoryStatus.store();
                            	}
                        		
                        		GenericValue addCategoryProduct = delegator.makeValue("ProductCategoryMember");
                        		addCategoryProduct.put("productCategoryId", subCategoryId);
                        		addCategoryProduct.put("productId", productId);
                        		addCategoryProduct.put("fromDate", UtilDateTime.nowTimestamp());
                        		addCategoryProduct.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
                            	if(isEnable.equals("N"))
                            		addCategoryProduct.put("thruDate", UtilDateTime.nowTimestamp());
                            	addCategoryProduct.create();
                            }else {
                            	productCategoryDetails.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
                            	if(isEnable.equals("N"))
                            		productCategoryDetails.put("thruDate", UtilDateTime.nowTimestamp());
                            	productCategoryDetails.store();
                            }
                	
                	results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ProductUpdatedSuccessfully", locale));
                    results.put("productCategoryId", productCategoryId);
                    results.put("prodCatalogId", prodCatalogId);
                    results.put("subCategoryId", subCategoryId);
                    results.put("productId", productId);
                }
                
        	} catch (GeneralException e) {e.printStackTrace();
            Debug.log("==error in creations===" + e.getMessage());
        }

        return results;

    }
    public static Map < String, Object > productSubCategoryUpdation(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside productCategoryUpdation------" + context, MODULE);
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();

        String categoryName = (String) context.get("subCategoryName");
        String prodCatalogId = (String) context.get("prodCatalogId");
        String productCategoryId = (String) context.get("productCategoryId");
        String subCategoryId = (String) context.get("subCategoryId");
        String isEnable = (String) context.get("isEnable");
        String sequenceNumber = (String) context.get("sequenceNumber");
       
        
        try {
        	EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
            EntityCondition.makeCondition("categoryName", EntityOperator.EQUALS, categoryName),
                    EntityCondition.makeCondition("productCategoryId", EntityOperator.NOT_EQUAL, subCategoryId));
                
                GenericValue productCategory = EntityQuery.use(delegator).from("ProductCategory").where(condition).queryFirst();
                if(UtilValidate.isNotEmpty(productCategory)) {
                	return ServiceUtil.returnError("Category Category exists");
                }
                else {
                	GenericValue productNewCategory = EntityQuery.use(delegator).from("ProductCategory").where("productCategoryId",subCategoryId).queryOne();
                	productNewCategory.put("categoryName",categoryName);
                	productNewCategory.store();
                	
                	GenericValue productCategorDetail = EntityQuery.use(delegator).from("ProductCategoryContent").where("productCategoryId",subCategoryId).queryFirst();
                	if(UtilValidate.isNotEmpty(productCategorDetail))
                	{
                		GenericValue content = EntityQuery.use(delegator).from("Content").where("contentId",productCategorDetail.getString("contentId")).queryFirst();
                		if(UtilValidate.isNotEmpty(content)) {
                			GenericValue contentResource = EntityQuery.use(delegator).from("ElectronicText").where("dataResourceId",content.getString("dataResourceId")).queryFirst();
                			if(UtilValidate.isNotEmpty(contentResource)) {
                				contentResource.put("textData", categoryName);
                				contentResource.store();
                			}
                		}
                		
                	}
                	
                		
						EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,
                                EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS, productCategoryId),
                                EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, subCategoryId),
                				EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
                            
                            GenericValue productCategoryDetails = EntityQuery.use(delegator).from("ProductCategoryRollup").where(condition1).orderBy("fromDate DESC").queryFirst();
                            if(UtilValidate.isEmpty(productCategoryDetails)) {
                            	GenericValue productCategoryStatus = EntityQuery.use(delegator).from("ProductCategoryRollup").where("productCategoryId",subCategoryId,"thruDate", null).orderBy("fromDate DESC").queryFirst();
                            	if(UtilValidate.isNotEmpty(productCategoryStatus)) {
                            		productCategoryStatus.put("thruDate", UtilDateTime.nowTimestamp());
                            		productCategoryStatus.store();
                            	}
                        		
                        		GenericValue addCategoryProduct = delegator.makeValue("ProductCategoryRollup");
                        		addCategoryProduct.put("parentProductCategoryId", productCategoryId);
                        		addCategoryProduct.put("productCategoryId", subCategoryId);
                        		addCategoryProduct.put("fromDate", UtilDateTime.nowTimestamp());
                        		addCategoryProduct.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
                            	if(isEnable.equals("N"))
                            		addCategoryProduct.put("thruDate", UtilDateTime.nowTimestamp());
                            	addCategoryProduct.create();
                            }else {
                            	productCategoryDetails.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : 0);
                            	if(isEnable.equals("N"))
                            		productCategoryDetails.put("thruDate", UtilDateTime.nowTimestamp());
                            	productCategoryDetails.store();
                            }
                	
                	results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ProductSubCategoryUpdatedSuccessfully", locale));
                    results.put("productCategoryId", productCategoryId);
                    results.put("prodCatalogId", prodCatalogId);
                    results.put("subCategoryId", subCategoryId);
                    
                }
                
        	} catch (GeneralException e) {e.printStackTrace();
            Debug.log("==error in creations===" + e.getMessage());
        }

        return results;

    }
}

