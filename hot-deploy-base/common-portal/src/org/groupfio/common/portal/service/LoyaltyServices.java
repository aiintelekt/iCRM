/**
 * 
 */
package org.groupfio.common.portal.service;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.fio.admin.portal.barcode.BarCodeGenerator;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.groupfio.common.portal.util.UtilAttribute;
import org.groupfio.common.portal.util.UtilOrder;
import org.groupfio.common.portal.util.UtilProduct;
import org.groupfio.common.portal.util.UtilTemplate;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class LoyaltyServices {

	private static final String MODULE = LoyaltyServices.class.getName();
	
	public static Map sendEreceipt(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map requestContext = (Map) context.get("requestContext");

		String orderId = (String) requestContext.get("orderId");
		String erOrdhtmlTplId = (String) requestContext.get("erOrdhtmlTplId");
		String isRmsOrder = (String) requestContext.get("isRmsOrder");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
		Map<String, Object> responseContext = FastMap.newInstance();

		try {
			if (UtilValidate.isNotEmpty(orderId)) {
        		List<Map<String, Object>> itmList = new ArrayList<>();
        		String orderEntityName = "InvoiceTransactionMaster";
        		if (UtilValidate.isNotEmpty(isRmsOrder) && isRmsOrder.equals("Y")) {
        			orderEntityName = "RmsTransactionMaster";
        		}
        		List<GenericValue> itms = EntityQuery.use(delegator).from(orderEntityName).where("transactionNumber", orderId).queryList();
        		if (UtilValidate.isNotEmpty(itms)) {
        			GenericValue itm = itms.get(0);
        			for (GenericValue tran : itms) {
        				String productImageUrl = null;
        				String brandName = null;
        				String strainType = null;
        				String categoryName = null;
        				String productId = null;
        				
        				Map<String, Object> line = tran.getAllFields();
        				
        				if (UtilValidate.isEmpty(isRmsOrder) || isRmsOrder.equals("N")) {
        					productId = UtilProduct.getProductIdByIdentification(delegator, tran.getString("productId"), tran.getString("productIdentificationTypeId"));
            				categoryName = tran.getString("productCategoryName1");
        				} else {
        					line.put("productId", tran.getString("skuNumber"));
        					productId = tran.getString("skuNumber");
        					categoryName = tran.getString("productCategoryName_1");
        				}
        				
        				if (UtilValidate.isNotEmpty(productId)) {
        					Map<String, Object> prod = UtilProduct.getProductDetails(delegator, productId);
        					productImageUrl = ParamUtil.getString(prod, "smallImageUrl");
        					brandName = ParamUtil.getString(prod, "brandName");
        					strainType = ParamUtil.getString(prod, "strainType");
        				}
        				
    					line.put("productName", tran.getString("skuDescription"));
    					line.put("productImageUrl", productImageUrl);
    					line.put("brandName", brandName);
    					line.put("strainType", strainType);
    					line.put("categoryName", categoryName);
    					itmList.add(line);
        			}
        			
        			String etPrimaryColName = "orderId";
        			if (UtilValidate.isNotEmpty(isRmsOrder) && isRmsOrder.equals("Y")) {
        				etPrimaryColName = "openemmTransactionId";
        			}
        			
        			GenericValue receipt = EntityQuery.use(delegator).from("EreceiptTransaction").where(etPrimaryColName, orderId).queryFirst();
            		if (UtilValidate.isEmpty(receipt)) {
            			receipt = delegator.makeValue("EreceiptTransaction");
            		}
        			
            		receipt.put("orderId", orderId);
            		receipt.put("createDate", itm.getTimestamp("invoiceDate"));
            		receipt.put("partyId", itm.getString("billToPartyId"));
            		receipt.put("criteriaType", "Store");
            		receipt.put("criteriaId", itm.getString("storeNumber"));
            		receipt.put("receiptType", "Email");
            		receipt.put("mailStatus", "Delivered");
            		
            		List<Object> personalizedTagList = new ArrayList<>();
            		Map<String, Object> oh = new LinkedHashMap<String, Object>();
            		
            		if (UtilValidate.isNotEmpty(itmList)) {
            			BigDecimal grandTotal = itm.getBigDecimal("totalSalesAmount");
            			if (UtilValidate.isNotEmpty(itm.getString("itemStatus")) && itm.getString("itemStatus").equals("RETURN")) {
            				grandTotal = new BigDecimal(0);
            			}
            			
            			oh.put("subTotal", itm.getBigDecimal("totalSalesAmount"));
            			oh.put("orderTax", itm.getBigDecimal("totalTaxAmount"));
            			oh.put("grandTotal", grandTotal);
    				}
            		
            		delegator.createOrStore(receipt);
            		
            		List<GenericValue> adjList = null;
            		List<GenericValue> taxAdjList = null;
            		
            		if (UtilValidate.isEmpty(isRmsOrder) || isRmsOrder.equals("N")) {
            			adjList = EntityQuery.use(delegator).from("InvoiceTransactionAdjustments").where("invoiceId", itm.getString("invoiceId")).queryList();
                		taxAdjList = EntityQuery.use(delegator).from("InvoiceTransactionTaxAdjustments").where("invoiceId", itm.getString("invoiceId")).queryList();
            		} else {
            			taxAdjList = EntityQuery.use(delegator).from("RmsTransactionTaxAdjustments").where("orderId", itm.getString("orderId")).queryList();
            		}
            		
            		// send email [start]
            		
            		// Prepare predefined tags [start]
            		
            		Map<String, Object> tag = new LinkedHashMap<>();
            		tag.put("TAG_NAME", "TRANS_ID");
            		tag.put("TAG_VALUE", orderId);
            		personalizedTagList.add(tag);
            		
            		// prepare barcode [start]
            		BufferedImage prodBarCode = BarCodeGenerator.generateBarCode(orderId);
            		if (UtilValidate.isNotEmpty(prodBarCode)) {
            			String encodedString = BarCodeGenerator.getImageBase64EncodeData(prodBarCode, "jpg");
            			tag = new LinkedHashMap<>();
            			tag.put("TAG_NAME", "PROD_BAR_CODE");
            			tag.put("TAG_VALUE", encodedString);
            			personalizedTagList.add(tag);
            		}
            		// prepare barcode [end]
            		
            		if (UtilValidate.isNotEmpty(receipt.getString("criteriaId"))) {
            			String storeReceiptId = UtilAttribute.getAttrValue(delegator, "StoreAttribute", "productStoreId", receipt.getString("criteriaId"), "productStoreId");
            			if (UtilValidate.isEmpty(storeReceiptId)) {
            				String message = "Store receipt with ID# "+receipt.getString("criteriaId")+", not configured";
                			Debug.logError(message, MODULE);
                			responseContext.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
                			responseContext.put(GlobalConstants.RESPONSE_MESSAGE, message);
                			result.put("responseContext", responseContext);
                			result.putAll(ServiceUtil.returnSuccess(message));
                			return result;
            			}
            		}
            		
            		String campaignId = receipt.getString("campaignId");
            		String templateId = null;
            		if (UtilValidate.isEmpty(campaignId)) {
            			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
            			conditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, receipt.getString("criteriaId")));
            			conditions.add(EntityCondition.makeCondition("productStoreType", EntityOperator.EQUALS, "STORE_RECEIPT"));
            			conditions.add(EntityUtil.getFilterByDateExpr());
                    	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
            			GenericValue storeAssoc = EntityQuery.use(delegator).select("campaignId").from("CampaignStoreAssoc").where(mainConditon).orderBy("fromDate DESC").queryFirst();
                		if (UtilValidate.isNotEmpty(storeAssoc)) {
                			campaignId = storeAssoc.getString("campaignId");
                		}
            		}
            		
            		if (UtilValidate.isEmpty(campaignId)) {
            			String message = "Store receipt with ID# "+receipt.getString("criteriaId")+", not configured with any ereceipt campaign";
            			Debug.logError(message, MODULE);
            			responseContext.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            			responseContext.put(GlobalConstants.RESPONSE_MESSAGE, message);
            			result.put("responseContext", responseContext);
            			result.putAll(ServiceUtil.returnSuccess(message));
            			return result;
            		}
            		
            		if (UtilValidate.isNotEmpty(campaignId)) {
            			GenericValue campaign = EntityQuery.use(delegator).select("campaignTemplateId").from("MarketingCampaign").where("marketingCampaignId", campaignId).queryFirst();
            			if (UtilValidate.isNotEmpty(campaign)) {
            				templateId = campaign.getString("campaignTemplateId");
            			}
            		}
            		
            		if (UtilValidate.isEmpty(templateId)) {
            			String message = "Ereceipt email template not configured for Campaign# "+campaignId;
            			Debug.logError(message, MODULE);
            			responseContext.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            			responseContext.put(GlobalConstants.RESPONSE_MESSAGE, message);
            			result.put("responseContext", responseContext);
            			result.putAll(ServiceUtil.returnSuccess(message));
            			return result;
            		}
            		
            		if (UtilValidate.isNotEmpty(templateId)) {
            			GenericValue template = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId", templateId), false);
            			
            			String emailEngine = UtilValidate.isNotEmpty(template.getString("emailEngine")) ? template.getString("emailEngine").replace("_ENGINE", "") : null;
            			
            			String emailContent = "";
            			String emailSubject = template.getString("subject");
						String templateFormContent = template.getString("templateFormContent");
						if (UtilValidate.isNotEmpty(templateFormContent)) {
							if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
								templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
							}
						}
						
						// Populate order html tags [start]
						List<Map<String, Object>> orderTplTags = UtilTemplate.getInnerTemplateTags(delegator, UtilMisc.toMap("templateId", templateId, "templateCategoryId", "ORDER_TPL"));
						if (UtilValidate.isNotEmpty(orderTplTags)) {
							for (Map<String, Object> templateTag : orderTplTags) {
								String tagId = (String) templateTag.get("tagId");
								String innerTemplateId = (String) templateTag.get("innerTemplateId");
								
								Map<String, Object> orderHtmlContext = new LinkedHashMap<String, Object>();
		    					orderHtmlContext.put("orderId", orderId);
		    					orderHtmlContext.put("erOrdhtmlTplId", innerTemplateId);
		    					orderHtmlContext.put("orderHeader", oh);
		    					orderHtmlContext.put("itmList", itmList);
		    					orderHtmlContext.put("adjList", adjList);
								orderHtmlContext.put("taxAdjList", taxAdjList);
		    					orderHtmlContext.put("cashierNumber", itm.getString("cashierNumber"));
		    					orderHtmlContext.put("storeNumber", itm.getString("storeNumber"));
		    					
		    					String orderHtml = UtilOrder.generateOrderHtml(delegator, orderHtmlContext);
		    					
		    					if (UtilValidate.isEmpty(isRmsOrder) || isRmsOrder.equals("N") || UtilValidate.isEmpty(receipt.getString("HTML"))) {
		    						receipt.put("HTML", orderHtml);
			    					receipt.store();
		    					} else if (UtilValidate.isNotEmpty(isRmsOrder) && isRmsOrder.equals("Y") && UtilValidate.isNotEmpty(receipt.getString("HTML"))) {
		    						orderHtml = receipt.getString("HTML");
		    					}
		    					
		    					tag = new LinkedHashMap<>();
		                		tag.put("TAG_NAME", tagId);
		                		tag.put("TAG_VALUE", orderHtml);
		                		personalizedTagList.add(tag);
							}
						}
						// Populate order html tags [end]
						
						String personalizationTags = ParamUtil.toJson(personalizedTagList);
	            		if (UtilValidate.isNotEmpty(receipt.getString("criteriaType")) && receipt.getString("criteriaType").equals("Store")) {
	            			Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
							extractContext.put("delegator", delegator);
							extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
							extractContext.put("templateId", templateId);
	            			extractContext.put("productStoreId", receipt.getString("criteriaId"));
	            			extractContext.put("orderId", orderId);
	            			extractContext.put("emailContent", templateFormContent);
	            			extractContext.put("emailSubject", template.getString("subject"));
	            			extractContext.put("personalizationTags", personalizationTags);
	            			extractContext.put("partyId", receipt.getString("partyId"));
	            			extractContext.put("emailEngine", emailEngine);
	            			extractContext.put("isRmsOrder", isRmsOrder);
	            			
	            			Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
	            			personalizationTags = (String) extractResultContext.get("personalizationTags");
	            			emailContent = (String) extractResultContext.get("emailContent");
	            			emailSubject = (String) extractResultContext.get("emailSubject");
	            		}
            			
            			String fromEmail = template.getString("senderEmail");
            			String toEmail = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, receipt.getString("partyId"), "PRIMARY_EMAIL");
            			String clientName = PartyHelper.getPartyName(delegator, "Company", false);
            			
            			Map<String, Object> rc = FastMap.newInstance();

    					rc.put("nsender", fromEmail);
    					rc.put("nto", toEmail);
    					rc.put("subject", emailSubject);
    					rc.put("emailContent", emailContent);
    					rc.put("templateId", templateId);
    					rc.put("personalizationTags", personalizationTags);
    					rc.put("emailEngine", emailEngine);
    					rc.put("referenceId", campaignId);
    					rc.put("referenceType", "CAMPAIGN");
    					rc.put("templateId", templateId);
    					rc.put("clientName", clientName);
    					rc.put("partyId", receipt.getString("partyId"));
    					rc.put("emailPurposeTypeId", "ORDER_RECEIPT");

    					callCtxt.put("requestContext", rc);
    					callCtxt.put("userLogin", userLogin);

    					dispatcher.runAsync("common.sendEmail", callCtxt);
    					result.putAll(ServiceUtil.returnSuccess("Successfully send ereceipt.."));
            		}
            		
            		// send email [end]
        		}
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			Debug.logError(e.getMessage(), MODULE);
			responseContext.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put("responseContext", responseContext);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.put("responseContext", responseContext);
		return result;
	}
	
}
