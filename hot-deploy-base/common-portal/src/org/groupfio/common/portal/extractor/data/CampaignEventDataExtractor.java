/**
 * 
 */
package org.groupfio.common.portal.extractor.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.common.portal.extractor.constants.DataConstants;
import org.groupfio.common.portal.util.UtilAttribute;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif
 *
 */
public class CampaignEventDataExtractor extends DataExtractor {

	private static String MODULE = CampaignEventDataExtractor.class.getName();
	private GenericEntity campaign;
	
	public CampaignEventDataExtractor(Data extractedData) {
		super(extractedData);
	}
	
	@Override
	public Map<String, Object> retrieve(Map<String, Object> context) {
		if (UtilValidate.isNotEmpty(extractedData)) {
			extractedData.retrieve(context);
		}
		return retrieveCampaignEvent(context);
	}

	private Map<String, Object> retrieveCampaignEvent(Map<String, Object> context) {
		System.out.println("Start retrieve Campaign Event");
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator"); 
				Map<String, Object> request = (Map<String, Object>) context.get("request"); 
				response = (Map<String, Object>) context.get("response"); 
				Map<String, Object> campaignEventData = new LinkedHashMap<String, Object>();
				
				String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
				
				String campaignId = ParamUtil.getString(request, "campaignId");
				//Debug.logInfo("Campaign Data extractor context : "+context, MODULE);
				if (UtilValidate.isNotEmpty(campaignId)) {
					
					String groupId = UtilAttribute.getAttrValue(delegator, "MarketingCampaignAttribute", "marketingCampaignId", campaignId, "GROUP_ID");
					if (UtilValidate.isNotEmpty(groupId)) {
						List<EntityCondition> conditions = new ArrayList<EntityCondition>();
						conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
						conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, null),
								EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y")
								));
						//conditions.add(EntityUtil.getFilterByDateExpr());
			        	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			        	
			        	List<GenericValue> fieldList = EntityQuery.use(delegator).from("CustomField").where(mainConditon).queryList();
			        	if (UtilValidate.isNotEmpty(fieldList)) {
			        		List<Map<String, Object>> dataList = new ArrayList<>(); 
			        		String groupType = null;
			        		String groupName = null;
			        		for (GenericValue field : fieldList) {
			        			groupType = field.getString("groupType");
			        			groupName = field.getString("groupName");
			        			
			        			Map<String, Object> data = new LinkedHashMap<>();
			        			data.put("customFieldId", field.getString("customFieldId"));
			        			data.put("groupId", field.getString("groupId"));
			        			data.put("customFieldName", field.getString("customFieldName"));
			        			data.put("sequenceNumber", UtilValidate.isNotEmpty(field.getString("sequenceNumber")) ? field.getString("sequenceNumber") : null);
			        			data.put("isEnabled", field.getString("isEnabled"));
			        			dataList.add(data);
			        		}
			        		
			        		campaignEventData.put(DataConstants.CAMPAIGN_EVENT_TAG.get("GROUP_ID"), Objects.toString(groupId, ""));
			        		campaignEventData.put(DataConstants.CAMPAIGN_EVENT_TAG.get("GROUP_TYPE"), Objects.toString(groupType, ""));
			        		campaignEventData.put(DataConstants.CAMPAIGN_EVENT_TAG.get("GROUP_NAME"), Objects.toString(groupName, ""));
			        		campaignEventData.put(DataConstants.CAMPAIGN_EVENT_TAG.get("CMP_EVENT_LIST"), dataList);
			        	}
					}
					
					response.put("campaignEventData", campaignEventData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
		}
		
		return response;
	}
}
