package org.groupfio.common.portal.extractor.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.common.portal.util.UtilProduct;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sharif
 *
 */
public class ProdAlsoBoughtDataExtractor extends DataExtractor {
	
	private static final Logger log = LoggerFactory.getLogger(ProdAlsoBoughtDataExtractor.class);
	
	public ProdAlsoBoughtDataExtractor() {}
	
	public ProdAlsoBoughtDataExtractor(Data extractedData) {
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
				Map<String, Object> prodAlsoBoughtData = new LinkedHashMap<String, Object>();
				
				String orderId = ParamUtil.getString(request, "orderId");
				List<Map<String, Object>> prodAlsoBoughtTplTags = (List<Map<String, Object>>) request.get("prodAlsoBoughtTplTags");
				
				if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(prodAlsoBoughtTplTags)) {
					
					Set<String> alsoBoughtProdIds = new LinkedHashSet<>();
					List<Map<String, Object>> prodList = new ArrayList<>();
					
					List<GenericValue> itmList = EntityQuery.use(delegator).select("productId").from("InvoiceTransactionMaster").where("transactionNumber", orderId).queryList();
					if (UtilValidate.isNotEmpty(itmList)) {
						for (GenericValue itm : itmList) {
							String productId = UtilProduct.getProductIdByIdentification(delegator, itm.getString("productId"), "EXTERNAL_ID");
							if (UtilValidate.isNotEmpty(productId)) {
								
								List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		            			conditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		            			conditions.add(EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "ALSO_BOUGHT"));
		            			conditions.add(EntityUtil.getFilterByDateExpr());
		                    	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		                    	List<GenericValue> assocList = EntityQuery.use(delegator).from("ProductAssoc").where(mainConditon).queryList();
								if (UtilValidate.isNotEmpty(assocList)) {
									for (GenericValue assoc : assocList) {
										alsoBoughtProdIds.add(assoc.getString("productIdTo"));
									}
								}
							}
						}
					}
					
					if (UtilValidate.isNotEmpty(alsoBoughtProdIds)) {
						for (String productId : alsoBoughtProdIds) {
							Map<String, Object> prod = new LinkedHashMap<>();
							prod = UtilProduct.getProductDetails(delegator, productId);
							prod.put("productPrice", UtilProduct.getProductPrice(delegator, productId, "LIST_PRICE"));
							prodList.add(prod);
						}
					}
					
					for (Map<String, Object> templateTag : prodAlsoBoughtTplTags) {
						String tagId = (String) templateTag.get("tagId");
						String innerTemplateId = (String) templateTag.get("innerTemplateId");
						Map<String, Object> prodHtmlContext = new LinkedHashMap<String, Object>();
						prodHtmlContext.put("prodList", prodList);
						prodHtmlContext.put("erOrdhtmlRelprodTplId", innerTemplateId);
						String prodsHtml = UtilProduct.generateProdsHtml(delegator, prodHtmlContext);
						
						prodAlsoBoughtData.put(tagId, Objects.toString(prodsHtml, ""));
					}
					
					response.put("prodAlsoBoughtData", prodAlsoBoughtData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return response;
	}

}
