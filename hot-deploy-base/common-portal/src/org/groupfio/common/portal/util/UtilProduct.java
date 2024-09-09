/**
 * 
 */
package org.groupfio.common.portal.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilFreemarker;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class UtilProduct {

	private static final String MODULE = UtilProduct.class.getName();
	
	public static String getProductIdByIdentification(Delegator delegator, String idValue, String goodIdentificationTypeId) {
		try {
			if(UtilValidate.isEmpty(idValue) || UtilValidate.isEmpty(goodIdentificationTypeId)){
				return null;
			}
			
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("goodIdentificationTypeId", EntityOperator.EQUALS, goodIdentificationTypeId));
			conditions.add(EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, idValue));
        	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
        	GenericValue gi = EntityQuery.use(delegator).from("GoodIdentification").where(mainConditon).queryFirst();
			if (UtilValidate.isNotEmpty(gi)) {
				return gi.getString("productId");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<String, Object> getProductDetails(Delegator delegator, String productId) {
		Map<String, Object> prod = new LinkedHashMap<>();
		try {
			if(UtilValidate.isEmpty(productId)){
				return null;
			}
			
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
        	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
        	GenericValue entity = EntityQuery.use(delegator).from("Product").where(mainConditon).queryFirst();
			if (UtilValidate.isNotEmpty(entity)) {
				prod = entity.getAllFields();
			}
			String strainType = UtilAttribute.getAttrValue(delegator, "ProductAttribute", "productId", productId, "STRAIN_TYPE");
			if (UtilValidate.isNotEmpty(strainType)) {
				prod.put("strainType", strainType.toUpperCase());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prod;
	}
	
	public static String getProductPrice(Delegator delegator, String productId, String productPriceTypeId) {
		try {
			if(UtilValidate.isEmpty(productId)){
				return null;
			}
			if (UtilValidate.isEmpty(productPriceTypeId)) {
				productPriceTypeId = "DEFAULT_PRICE";
			}
			
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			conditions.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, productPriceTypeId));
			conditions.add(EntityUtil.getFilterByDateExpr());
        	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
        	GenericValue entity = EntityQuery.use(delegator).from("ProductPrice").where(mainConditon).queryFirst();
			if (UtilValidate.isNotEmpty(entity)) {
				String price = UtilValidate.isNotEmpty(entity.getBigDecimal("price")) ? entity.getBigDecimal("price").setScale(2, BigDecimal.ROUND_CEILING).toString() : "0.00";
				return price;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String generateProdsHtml(Delegator delegator, Map<String, Object> context) {
		try {
			if(UtilValidate.isEmpty(context)) {
				return null;
			}
			Map<String, Object> callCtxt = new HashMap<String, Object>();
			Map<String, Object> callResult = new HashMap<String, Object>();
			
			String erOrdhtmlRelprodTplId = ParamUtil.getString(context, "erOrdhtmlRelprodTplId");
			List<Map<String, Object>> prodList = (List<Map<String, Object>>) context.get("prodList");
			
			if (UtilValidate.isNotEmpty(erOrdhtmlRelprodTplId)) {
				GenericValue tpl = EntityQuery.use(delegator).from("TemplateMaster").where("templateId", erOrdhtmlRelprodTplId).queryFirst();
				if (UtilValidate.isNotEmpty(tpl)) {
					String templateFormContent = tpl.getString("templateFormContent");
					if (UtilValidate.isNotEmpty(templateFormContent)) {
						if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
							templateFormContent = Base64.base64Decode(templateFormContent);
						}
					}
					
					callCtxt = new HashMap<String, Object>();
					Map<String, Object> dataContext = new LinkedHashMap<>();
					dataContext.put("prodList", prodList);
					
					callCtxt.put("dataContext", dataContext);
					
					return UtilFreemarker.renderPreview(templateFormContent, callCtxt);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getProductTypeDescription(Delegator delegator, String productTypeId) {
		try {
			if(UtilValidate.isEmpty(productTypeId)){
				return null;
			}
			
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, productTypeId));
        	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
        	GenericValue prodType = EntityQuery.use(delegator).select("description").from("ProductType").where(mainConditon).queryFirst();
			if (UtilValidate.isNotEmpty(prodType)) {
				return prodType.getString("description");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
