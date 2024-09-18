/**
 * 
 */
package org.groupfio.common.portal.extractor.data;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.fio.homeapps.util.ParamUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class EconomicMetricDataExtractor extends DataExtractor {

	private static String MODULE = EconomicMetricDataExtractor.class.getName();
	
	public EconomicMetricDataExtractor(Data extractedData) {
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
		System.out.println("Start retrieve Economic Metric");
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator"); 
				Map<String, Object> request = (Map<String, Object>) context.get("request"); 
				response = (Map<String, Object>) context.get("response"); 
				Map<String, Object> economicMetricData = new LinkedHashMap<String, Object>();
				
				String partyId = ParamUtil.getString(request, "partyId");
				//Debug.logInfo("Economic Matric Data extractor context : "+context, MODULE);
				if (UtilValidate.isNotEmpty(partyId)) {
					
					List<GenericValue> economicMetricTags = (List<GenericValue>) request.get("economicMetricTags");
					if (UtilValidate.isNotEmpty(economicMetricTags)) {
						for (GenericValue economicMetricTag : economicMetricTags) {
							List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
							conditionList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, economicMetricTag.getString("customFieldGroupId")));
							conditionList.add(EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, economicMetricTag.getString("customFieldId")));
							conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
							//conditionList.add(EntityCondition.makeCondition("propertyName", EntityOperator.EQUALS, economicMetricTag.getString("tagId")));
							EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							GenericValue associatedEntity = EntityUtil.getFirst( delegator.findList("PartyMetricIndicator", mainConditons, null, null, null, false) );
							if (UtilValidate.isNotEmpty(associatedEntity)) {
								economicMetricData.put(economicMetricTag.getString("tagId"), Objects.toString(associatedEntity.getString("propertyValue"), ""));
							}
						}
					}
                	
					response.put("economicMetricData", economicMetricData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
		}
		
		return response;
	}
}
