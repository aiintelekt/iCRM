package org.groupfio.common.portal.service;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.groupfio.common.portal.util.UtilAttribute;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class TemplateServices {

	private static final String MODULE = TemplateServices.class.getName();

	public static Map<String, Object> getTemplatesData(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		String templateType = (String) context.get("templateType");
		String templateName = (String) context.get("tempalateName");
		String searchKeyword = (String) context.get("searchKeyword");
		String filterCategory = (String) context.get("templateCategories");
		String emailEngine = (String) context.get("emailEngine");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		
		Map<String, Object> requestContext = (Map) context.get("requestContext");
		String templateCategoryId = null;
		String purposeTypeId = null;
		if (UtilValidate.isNotEmpty(requestContext)) {
			templateCategoryId = (String) requestContext.get("templateCategoryId");
			purposeTypeId = (String) requestContext.get("purposeTypeId");
		}

		try{
			String defaultTplImg = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_TPL_IMG");
			String campaignTemplatePath = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CAMPAIGN_TEMPLATE_PATH");

			List<EntityCondition> conditionList = FastList.newInstance();
			Set<String> fieldToSelect = new HashSet<String>();
			fieldToSelect.add("templateId");
			fieldToSelect.add("templateName");
			fieldToSelect.add("templateType");
			fieldToSelect.add("subject");
			fieldToSelect.add("fromDate");
			fieldToSelect.add("thruDate");
			fieldToSelect.add("emailEngine");

			if (UtilValidate.isNotEmpty(emailEngine)) {
				conditionList.add(EntityCondition.makeCondition("emailEngine", EntityOperator.EQUALS, emailEngine));
			}

			if (UtilValidate.isNotEmpty(templateType)) {
				conditionList.add(EntityCondition.makeCondition("templateType", EntityOperator.EQUALS, templateType));
			}

			if (UtilValidate.isNotEmpty(templateName)) {
				templateName = templateName.trim();
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("templateName", EntityOperator.LIKE, "%" + templateName + "%"),
						EntityCondition.makeCondition("templateId", EntityOperator.LIKE, "%" + templateName + "%")));
			}
			if(UtilValidate.isNotEmpty(searchKeyword)) {
				conditionList.add(EntityCondition.makeCondition("templateName", EntityOperator.LIKE, "%" + searchKeyword + "%"));
			}
			if (UtilValidate.isNotEmpty(filterCategory)) {
				String[] templateCategories = filterCategory.split(",");
				if (templateCategories != null && templateCategories.length > 0) {
					List<String> categoryIds = Arrays.asList(templateCategories);
					conditionList .add(EntityCondition.makeCondition("templateCategoryId", EntityOperator.IN, categoryIds));
				}
			}
			
			if (UtilValidate.isNotEmpty(templateCategoryId)) {
				conditionList.add(EntityCondition.makeCondition("templateCategoryId", EntityOperator.EQUALS, templateCategoryId));
			}
			if (UtilValidate.isNotEmpty(purposeTypeId)) {
				conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, purposeTypeId));
			}
			if(UtilValidate.isNotEmpty(marketingCampaignId)) {
				GenericValue campaign = EntityQuery.use(delegator).from("MarketingCampaign").select("campaignTemplateId").where("marketingCampaignId",marketingCampaignId).queryOne();
				if(UtilValidate.isNotEmpty(campaign)) {
					String templateId = campaign.getString("campaignTemplateId");
					if(UtilValidate.isNotEmpty(templateId)) {
						conditionList.add(EntityCondition.makeCondition("templateId", EntityOperator.EQUALS, templateId));
					}
				}
			}
			int autoCompleteLimit=org.groupfio.common.portal.util.DataUtil.getDefaultAutoCompleteMaxRows(delegator);
			int getFioGridLimit=org.groupfio.common.portal.util.DataUtil.defaultFioGridfetchLimit(delegator);

			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.setLimit(getFioGridLimit);
			if(UtilValidate.isNotEmpty(searchKeyword)) {
				efo.setMaxRows(autoCompleteLimit);
			}
			EntityListIterator TemplateAndCategoryListIter = delegator.find("TemplateAndCategory", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, fieldToSelect, UtilMisc.toList("templateId DESC"), efo);
			List<GenericValue> templateAndCategory = TemplateAndCategoryListIter.getCompleteList();
			TemplateAndCategoryListIter.close();

			if (UtilValidate.isNotEmpty(templateAndCategory)) {
				templateAndCategory.forEach(e -> {
					Map<String, Object> data = new HashMap<String, Object>();
					try {
						String templateId = e.getString("templateId");
						data.put("templateId", e.getString("templateId"));
						String name = e.getString("templateName");
						data.put("templateName", UtilValidate.isNotEmpty(name) ? name.replaceAll("'", "`").replaceAll("\"", "``") : "");
						data.put("templateIdName", org.groupfio.common.portal.util.DataUtil.combineValueKey(e.getString("templateName"), e.getString("templateId")));
						//dataMap.put("templateFormContent", UtilValidate.isNotEmpty(e.getString("templateFormContent")) ? e.getString("templateFormContent") : "");
						data.put("templateType", UtilValidate.isNotEmpty(e.getString("templateType")) ? e.getString("templateType") : "");
						data.put("subject", UtilValidate.isNotEmpty(e.getString("subject")) ? e.getString("subject") : "");
						data.put("fromDate", UtilValidate.isNotEmpty(e.getTimestamp("fromDate")) ? df.format(e.getTimestamp("fromDate")) : "");
						data.put("thruDate", UtilValidate.isNotEmpty(e.getTimestamp("thruDate")) ? df.format(e.getTimestamp("thruDate")) : "");
						
						String emailEngineId = (String)e.getString("emailEngine"); 
						if (UtilValidate.isNotEmpty(emailEngineId)) {
							GenericValue category = EntityQuery.use(delegator).from("TemplateCategory")
									.where("templateCategoryId",emailEngineId).queryFirst();
							if (UtilValidate.isNotEmpty(category)) {
								data.put("emailEngine", UtilValidate.isNotEmpty(category.getString("templateCategoryName")) ? category.getString("templateCategoryName") : "");
							}
							
							String thumbUrl = UtilAttribute.getAttrValue(delegator, "TemplateAttribute", "templateId", e.getString("templateId"), "EXT_TPL_THUMB_URL");
							data.put("previewImg", UtilValidate.isNotEmpty(thumbUrl) ? thumbUrl : defaultTplImg);
						}
						if (UtilValidate.isNotEmpty(marketingCampaignId) && UtilValidate.isNotEmpty(templateId) && UtilValidate.isNotEmpty(campaignTemplatePath)) {
							String path = campaignTemplatePath + templateId + "/";
							File directory = new File(path);
							String previewTemplateImg = defaultTplImg;
							if (directory.exists() && directory.isDirectory()) {
								File[] files = directory.listFiles(file -> file.isFile() && (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpeg")));
								if (files != null && files.length > 0) {
									Arrays.sort(files, Comparator.comparing(File::getName));
									if (files.length > 0) {
										previewTemplateImg = path + files[0].getName();
									}
								}
								int index = previewTemplateImg.indexOf("webapp");
								if (index != -1) {
									previewTemplateImg = "/" + previewTemplateImg.substring(index + "webapp".length() + 1);
								}
							}
							data.put("previewTemplateImg", previewTemplateImg);
						}
					} catch (GenericEntityException e1) {
						e1.printStackTrace();
						result.put("resultMap", new HashMap<String, Object>());
					}
					
					dataList.add(data);
				});
				resultMap.put("result", dataList);
			}
		}catch (Exception e) {
			e.printStackTrace();
			//result.put("errorMsg", e.getMessage());
			e.getMessage();
			result.put("resultMap", new HashMap<String, Object>());
			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}

}
