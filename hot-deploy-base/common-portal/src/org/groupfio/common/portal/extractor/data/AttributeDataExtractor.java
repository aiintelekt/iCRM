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
import org.ofbiz.base.util.UtilMisc;
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
public class AttributeDataExtractor extends DataExtractor {

	private static String MODULE = AttributeDataExtractor.class.getName();
	
	public AttributeDataExtractor(Data extractedData) {
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
		System.out.println("Start retrieve Attribute");
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator"); 
				Map<String, Object> request = (Map<String, Object>) context.get("request"); 
				response = (Map<String, Object>) context.get("response"); 
				Map<String, Object> attributeData = new LinkedHashMap<String, Object>();
				
				String partyId = ParamUtil.getString(request, "partyId");
				//Debug.logInfo("Attribute Data extractor context : "+context, MODULE);
				if (UtilValidate.isNotEmpty(partyId)) {
					
					List<GenericValue> attributeTags = (List<GenericValue>) request.get("attributeTags");
					if (UtilValidate.isNotEmpty(attributeTags)) {
						for (GenericValue attributeTag : attributeTags) {
							List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
							conditionList.add(EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, attributeTag.getString("customFieldId")));
							conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
							conditionList.add(EntityUtil.getFilterByDateExpr());
							EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							GenericValue associatedEntity = EntityUtil.getFirst( delegator.findList("CustomFieldValue", mainConditons, null, null, null, false) );
							if (UtilValidate.isNotEmpty(associatedEntity)) {
								GenericValue attributeField = delegator.findOne("CustomField", UtilMisc.toMap("customFieldId", attributeTag.getString("customFieldId")), false);
								String fieldValue = associatedEntity.getString("fieldValue");
								if (attributeField.getString("customFieldType").equals("MULTIPLE")) {
									List<GenericValue> multiValues = delegator.findByAnd("CustomFieldMultiValue", UtilMisc.toMap("customFieldId", attributeField.getString("customFieldId")), null, false);
									for (GenericValue multiValue : multiValues) {
										if (UtilValidate.isNotEmpty(multiValue.getString("multiValueId")) && multiValue.getString("multiValueId").equals(fieldValue)) {
											if (UtilValidate.isNotEmpty(attributeField.getString("customFieldFormat")) && attributeField.getString("customFieldFormat").equals("CHECK_BOX")) {
												fieldValue = fieldValue + multiValue.getString("description") + ",";
											} else {
												fieldValue = multiValue.getString("description");
												break;
											}
										}
									}
									
									if (UtilValidate.isNotEmpty(attributeField.getString("customFieldFormat")) && attributeField.getString("customFieldFormat").equals("CHECK_BOX")) {
										if (UtilValidate.isNotEmpty(fieldValue)) {
											fieldValue = fieldValue.substring(0, fieldValue.length()-1);
										}
									}
									
								}
								
								attributeData.put(attributeTag.getString("tagId"), Objects.toString(fieldValue, ""));
							}
						}
					}
                	
					response.put("attributeData", attributeData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
		}
		
		return response;
	}
}
