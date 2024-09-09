/**
 * 
 */
package org.groupfio.common.portal.extractor.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.extractor.constants.DataConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif
 *
 */
public class CampaignDataExtractor extends DataExtractor {

	private static String MODULE = CampaignDataExtractor.class.getName();
	
	public CampaignDataExtractor(Data extractedData) {
		super(extractedData);
	}
	
	@Override
	public Map<String, Object> retrieve(Map<String, Object> context) {
		if (UtilValidate.isNotEmpty(extractedData)) {
			extractedData.retrieve(context);
		}
		return retrieveData(context);
	}

	private Map<String, Object> retrieveData(Map<String, Object> context) {
		System.out.println("Start retrieve Campaign");
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator"); 
				Map<String, Object> request = (Map<String, Object>) context.get("request"); 
				response = (Map<String, Object>) context.get("response"); 
				Map<String, Object> campaignData = new LinkedHashMap<String, Object>();
				
				String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
				
				String campaignId = ParamUtil.getString(request, "campaignId");
				//Debug.logInfo("Campaign Data extractor context : "+context, MODULE);
				if (UtilValidate.isNotEmpty(campaignId)) {
					
					List<EntityCondition> conditions = new ArrayList<EntityCondition>();
					conditions.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.EQUALS, campaignId));
					//conditions.add(EntityUtil.getFilterByDateExpr());
		        	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		        	
		        	GenericValue campaign = EntityQuery.use(delegator).from("MarketingCampaign").where(mainConditon).queryFirst();
		        	if (UtilValidate.isNotEmpty(campaign)) {
		        		String publishDate = "";
		        		if (UtilValidate.isNotEmpty(campaign.getTimestamp("startDate"))) {
		        			publishDate = UtilDateTime.timeStampToString(campaign.getTimestamp("startDate"), globalDateFormat, TimeZone.getDefault(), null);
		        		}
		        		
		        		campaignData.put(DataConstants.CAMPAIGN_TAG.get("CAMPAIGN_ID"), Objects.toString(campaign.getString("marketingCampaignId"), ""));
		        		campaignData.put(DataConstants.CAMPAIGN_TAG.get("CAMPAIGN_NAME"), Objects.toString(campaign.getString("campaignName"), ""));
		        		campaignData.put(DataConstants.CAMPAIGN_TAG.get("PUBLISH_DATE"), Objects.toString(publishDate, ""));
		        	}
		        	
					response.put("campaignData", campaignData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
		}
		
		return response;
	}
}
