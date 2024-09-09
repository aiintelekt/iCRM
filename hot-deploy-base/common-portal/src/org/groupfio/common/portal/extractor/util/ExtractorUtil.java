/**
 * 
 */
package org.groupfio.common.portal.extractor.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class ExtractorUtil {

	private static String MODULE = ExtractorUtil.class.getName();
	
	public static Map<String, Object> prepareResponse(Map<String, Object> mergeContext, Map<String, Object> context) {
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator"); 
				Map<String, Object> request = (Map<String, Object>) context.get("request"); 
				Map<String, Object> response = (Map<String, Object>) context.get("response"); 
				
				if (UtilValidate.isNotEmpty(response.get("groupData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("groupData"));
				}
				if (UtilValidate.isNotEmpty(response.get("personData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("personData"));
				}
				if (UtilValidate.isNotEmpty(response.get("contactInfoData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("contactInfoData"));
				}
				if (UtilValidate.isNotEmpty(response.get("generalInfoData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("generalInfoData"));
				}
				if (UtilValidate.isNotEmpty(response.get("postalData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("postalData"));
				}
				if (UtilValidate.isNotEmpty(response.get("attributeData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("attributeData"));
				}
				if (UtilValidate.isNotEmpty(response.get("segmentationData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("segmentationData"));
				}
				if (UtilValidate.isNotEmpty(response.get("economicMetricData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("economicMetricData"));
				}
				if (UtilValidate.isNotEmpty(response.get("storeReceiptData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("storeReceiptData"));
				}
				if (UtilValidate.isNotEmpty(response.get("orderData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("orderData"));
				}
				if (UtilValidate.isNotEmpty(response.get("campaignData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("campaignData"));
				}
				if (UtilValidate.isNotEmpty(response.get("campaignEventData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("campaignEventData"));
				}
				if (UtilValidate.isNotEmpty(response.get("prodAlsoBoughtData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("prodAlsoBoughtData"));
				}
				if (UtilValidate.isNotEmpty(response.get("prodUpsellData"))) {
					mergeContext.putAll((Map<String, Object>)response.get("prodUpsellData"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
		}
		
		return mergeContext;
	}
	
	public static List<GenericValue> getPersonalizedTags(Delegator delegator, String templateTagTypeId) {
		try {
			if (UtilValidate.isNotEmpty(templateTagTypeId)) {
				List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("templateTagTypeId", EntityOperator.EQUALS, templateTagTypeId));
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"),
						EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, null)
						));
				conditionList.add(EntityUtil.getFilterByDateExpr());
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				List<GenericValue> entityList = delegator.findList("DataTagConfiguration", mainConditons, null, null, null, false);
				return entityList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<GenericValue> getTemplatePersonalizedTags(Delegator delegator, String templateId, String templateTagTypeId) {
		try {
			if (UtilValidate.isNotEmpty(templateId) && UtilValidate.isNotEmpty(templateTagTypeId)) {
				List<String> templateTags = FastList.newInstance();
				List<EntityCondition> conditions = FastList.newInstance();
				if(UtilValidate.isNotEmpty(templateTagTypeId)) {
					conditions.add(EntityCondition.makeCondition("templateTagTypeId", EntityOperator.EQUALS, templateTagTypeId));
				}
				conditions.add(EntityCondition.makeCondition("templateId", EntityOperator.EQUALS, templateId));
				conditions.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> tagConfigList = delegator.findList("DataTagAndTemplateTagConfiguration", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
				if(UtilValidate.isNotEmpty(tagConfigList)) {
					templateTags = EntityUtil.getFieldListFromEntityList(tagConfigList, "tagId", true);
				}
				
				if (UtilValidate.isNotEmpty(templateTags)) {
					List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
					conditionList.add(EntityCondition.makeCondition("tagId", EntityOperator.IN, templateTags));
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					List<GenericValue> entityList = EntityQuery.use(delegator).from("DataTagConfiguration").where(mainConditons).queryList();
					return entityList;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
