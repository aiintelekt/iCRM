/**
 * 
 */
package org.groupfio.common.portal.extractor;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.FreemarkerUtil;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.common.portal.extractor.constants.DataConstants;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.groupfio.common.portal.extractor.data.AttributeDataExtractor;
import org.groupfio.common.portal.extractor.data.CampaignDataExtractor;
import org.groupfio.common.portal.extractor.data.CampaignEventDataExtractor;
import org.groupfio.common.portal.extractor.data.ContactDataExtractor;
import org.groupfio.common.portal.extractor.data.Data;
import org.groupfio.common.portal.extractor.data.EconomicMetricDataExtractor;
import org.groupfio.common.portal.extractor.data.GeneralDataExtractor;
import org.groupfio.common.portal.extractor.data.OrderDataExtractor;
import org.groupfio.common.portal.extractor.data.Party;
import org.groupfio.common.portal.extractor.data.PostalDataExtractor;
import org.groupfio.common.portal.extractor.data.ProdAlsoBoughtDataExtractor;
import org.groupfio.common.portal.extractor.data.ProdUpsellDataExtractor;
import org.groupfio.common.portal.extractor.data.SegmentationDataExtractor;
import org.groupfio.common.portal.extractor.data.StoreReceiptDataExtractor;
import org.groupfio.common.portal.extractor.util.ExtractorUtil;
import org.groupfio.common.portal.util.UtilTemplate;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.springframework.util.CollectionUtils;

/**
 * @author Sharif
 *
 */
public class ExtractFacade {
	
	private static String MODULE = ExtractFacade.class.getName();

	public static Map<String, Object> extractData(Map<String, Object> context) {
		
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		String extractType = (String) context.get("extractType");
		
		switch (extractType){
		case ExtractType.EXTRACT_EMAIL_DATA: 
			
			try {
				Delegator delegator = (Delegator) context.get("delegator"); 
				
				String partyId = (String) context.get("partyId");
				String templateId = (String) context.get("templateId");
				
				String fromEmail = (String) context.get("fromEmail");
				String toEmail = (String) context.get("toEmail");
				String emailContent = (String) context.get("emailContent");
				String emailSubject = (String) context.get("emailSubject");
				String emailEngine = (String) context.get("emailEngine");
				
				String custRequestId = (String) context.get("custRequestId");
				String workEffortId = (String) context.get("workEffortId");
				List<GenericValue> listOfFsr = (List<GenericValue>) context.get("listOfFsr");
				List<GenericValue> listOfFsp = (List<GenericValue>) context.get("listOfFsp");
				
				String personalizationTags = "";
				List<Object> personalizedTagList = new ArrayList<>();
				if (UtilValidate.isNotEmpty(context.get("personalizationTags"))) {
					personalizedTagList = ParamUtil.jsonToList(context.get("personalizationTags").toString());
					personalizationTags = context.get("personalizationTags").toString();
				}
				
				Map<String, Object> mergeContext = new LinkedHashMap<String, Object>();
				Writer wr = new StringWriter();
				if (UtilValidate.isEmpty(partyId)) {
					partyId = DataUtil.getPartyIdByEmail(delegator, toEmail);
				}
				
				Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
				Map<String, Object> requestContext = new LinkedHashMap<String, Object>();
				Map<String, Object> responseContext = new LinkedHashMap<String, Object>();
				
				requestContext.putAll(context);
				if (UtilValidate.isNotEmpty(custRequestId)) {
					requestContext.put("custRequestId", custRequestId);
				}
				requestContext.put("workEffortId", workEffortId);
				requestContext.put("partyId", partyId);
				requestContext.put("fromEmail", fromEmail);
				requestContext.put("toEmail", toEmail);
				requestContext.put("listOfFsr", listOfFsr);
				requestContext.put("listOfFsp", listOfFsp);
				
				requestContext.put("attributeTags", ExtractorUtil.getTemplatePersonalizedTags(delegator, templateId, "CUSTOM_FIELD"));
				requestContext.put("segmentationTags", ExtractorUtil.getTemplatePersonalizedTags(delegator, templateId, "SEGMENTATION"));
				requestContext.put("economicMetricTags", ExtractorUtil.getTemplatePersonalizedTags(delegator, templateId, "ECONOMIC_METRIC"));
				
				//Debug.logInfo("segmentationTags> "+requestContext.get("segmentationTags"), MODULE);
				
				extractContext.put("delegator", delegator);
				extractContext.put("request", requestContext);
				extractContext.put("response", responseContext);
				
				Data data = new Party();
				
				if (UtilValidate.isNotEmpty(templateId)) {
					List<GenericValue> tagList = EntityQuery.use(delegator).from("TemplateTag").where("templateId", templateId).queryList();
					Set<String> tags = new HashSet<>();
					if (UtilValidate.isNotEmpty(tagList)) {
						tags = tagList.stream().map(x-> {
							return x.getString("tagId");
						}).collect(Collectors.toSet());
						
						if (CollectionUtils.containsAny(tags, DataConstants.GENERAL_INFO_TAG.keySet())) {
							data = new GeneralDataExtractor(data);
						}
						if (CollectionUtils.containsAny(tags, DataConstants.CONTACT_INFO_TAG.keySet())) {
							data = new ContactDataExtractor(data);
						}
						if (CollectionUtils.containsAny(tags, DataConstants.POSTAL_TAG.keySet())) {
							data = new PostalDataExtractor(data);
						}
						if (CollectionUtils.containsAny(tags, DataConstants.STORE_RECEIPT_TAG.keySet())) {
							data = new StoreReceiptDataExtractor(data);
						}
						if (CollectionUtils.containsAny(tags, DataConstants.ORDER_TAG.keySet())) {
							data = new OrderDataExtractor(data);
						}
						if (CollectionUtils.containsAny(tags, DataConstants.CAMPAIGN_TAG.keySet())) {
							data = new CampaignDataExtractor(data);
						}
						
						List<Map<String, Object>> dataList = UtilTemplate.getInnerTemplateTags(delegator, UtilMisc.toMap("templateId", templateId, "templateCategoryId", "PROD_ALSO_BOUGHT"));
						if (UtilValidate.isNotEmpty(dataList)) {
							requestContext.put("prodAlsoBoughtTplTags", dataList);
							data = new ProdAlsoBoughtDataExtractor(data);
						}
						
						dataList = UtilTemplate.getInnerTemplateTags(delegator, UtilMisc.toMap("templateId", templateId, "templateCategoryId", "PROD_UPSELL"));
						if (UtilValidate.isNotEmpty(dataList)) {
							requestContext.put("prodUpsellTplTags", dataList);
							data = new ProdUpsellDataExtractor(data);
						}
					}
					
					GenericValue template = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId", templateId), false);
					if (UtilValidate.isNotEmpty(template)) {
						if (UtilValidate.isEmpty(emailEngine) && UtilValidate.isNotEmpty(template.getString("emailEngine"))) {
							emailEngine = template.getString("emailEngine").replace("_ENGINE", "");
						}
					}
				} else {
					data = new GeneralDataExtractor(new ContactDataExtractor(new PostalDataExtractor(new Party())));
				}
				
				data = new SegmentationDataExtractor(new EconomicMetricDataExtractor(new AttributeDataExtractor(data)));
				
				data.retrieve(extractContext);
				ExtractorUtil.prepareResponse(mergeContext, extractContext);
				
				if (UtilValidate.isNotEmpty(personalizedTagList)) {
					for (Object tagObj : personalizedTagList) {
						Map<String, Object> tag = (Map<String, Object>) tagObj;
						if (UtilValidate.isNotEmpty(tag)) {
							String tagName = (String) tag.get("TAG_NAME");
							String tagValue = (String) tag.get("TAG_VALUE");
							if (UtilValidate.isNotEmpty(tagName)) {
								mergeContext.put(tagName, tagValue);
							}
						}
					}
				}
				
				if (UtilValidate.isNotEmpty(mergeContext)) {
					for (String key : mergeContext.keySet()) {
						String tagVal = (String) personalizedTagList.stream().filter(e->( UtilValidate.isNotEmpty(((Map)e).get(key)) )).findAny().orElse(null);
						if (UtilValidate.isEmpty(tagVal) && UtilValidate.isNotEmpty(mergeContext.get(key))) {
							Map<String, Object> tag = new LinkedHashMap<>();
							tag.put("TAG_NAME", key);
							tag.put("TAG_VALUE", mergeContext.get(key));
							personalizedTagList.add(tag);
						}
					}
					personalizationTags = ParamUtil.toJson(personalizedTagList);
				}
				
				if (UtilValidate.isEmpty(emailEngine) || !emailEngine.equals("SENDGRID")) {
					//System.out.println("before merge: "+emailContent);
					FreemarkerUtil.renderTemplateWithTags("MergeForm", emailContent, mergeContext, wr, false, true);
					emailContent = wr.toString();
					//System.out.println("after merge: "+emailContent);
					
					if (UtilValidate.isNotEmpty(emailSubject)) {
						wr = new StringWriter();
						FreemarkerUtil.renderTemplateWithTags("MergeForm", emailSubject, mergeContext, wr, false, true);
						emailSubject = wr.toString();
					}
				}
				
				result.put("emailContent", emailContent);
				result.put("emailSubject", emailSubject);
				result.put("personalizationTags", personalizationTags);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			break;
		case ExtractType.EXTRACT_CAMPAIGN_DATA: 
			
			try {
				
				Delegator delegator = (Delegator) context.get("delegator"); 
				String campaignId = (String) context.get("campaignId");
				
				Map<String, Object> mergeContext = new LinkedHashMap<String, Object>();
				Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
				Map<String, Object> requestContext = new LinkedHashMap<String, Object>();
				Map<String, Object> responseContext = new LinkedHashMap<String, Object>();
				
				requestContext.putAll(context);
				requestContext.put("campaignId", campaignId);
				
				extractContext.put("delegator", delegator);
				extractContext.put("request", requestContext);
				extractContext.put("response", responseContext);
				
				Data data = new CampaignDataExtractor(new Party());
				data.retrieve(extractContext);
				
				ExtractorUtil.prepareResponse(mergeContext, extractContext);
				
				result.put("dataContext", mergeContext);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			break;
		case ExtractType.EXTRACT_CMP_EVENT_DATA: 
			
			try {
				
				Delegator delegator = (Delegator) context.get("delegator"); 
				String campaignId = (String) context.get("campaignId");
				
				Map<String, Object> mergeContext = new LinkedHashMap<String, Object>();
				Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
				Map<String, Object> requestContext = new LinkedHashMap<String, Object>();
				Map<String, Object> responseContext = new LinkedHashMap<String, Object>();
				
				requestContext.putAll(context);
				requestContext.put("campaignId", campaignId);
				
				extractContext.put("delegator", delegator);
				extractContext.put("request", requestContext);
				extractContext.put("response", responseContext);
				
				Data data = new CampaignEventDataExtractor(new Party());
				data.retrieve(extractContext);
				
				ExtractorUtil.prepareResponse(mergeContext, extractContext);
				
				result.put("dataContext", mergeContext);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			break;
		}
		return result;
	}
	
}
