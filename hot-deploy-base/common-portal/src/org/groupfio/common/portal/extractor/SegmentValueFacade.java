/**
 * 
 */
package org.groupfio.common.portal.extractor;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.util.QueryUtil;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ValueOverrideType;
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
public class SegmentValueFacade {

	private static String MODULE = SegmentValueFacade.class.getName();

	public static Map<String, Object> extractData(Map<String, Object> context) {
		Delegator delegator = (Delegator) context.get("delegator"); 
		
		String valueOverrideType = (String) context.get("valueOverrideType");
		String valueCapture = (String) context.get("valueCapture");
		String customFieldGroupId = (String) context.get("customFieldGroupId");
		String partyId = (String) context.get("partyId");
		String isCaptureDefault = (String) context.get("isCaptureDefault");
		
		String value = null;
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		switch (valueOverrideType){
		case ValueOverrideType.GLOBAL_OVERRIDE: 
			try {
				if (UtilValidate.isEmpty(valueCapture) || valueCapture.equals("SINGLE")) {
					List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        			conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, customFieldGroupId));
        			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
        					EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, null),
        					EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y")
        					));
        			conditions.add(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"));
                	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					GenericValue field = EntityQuery.use(delegator).select("customFieldId","customFieldName").from("CustomField").where(mainConditon).queryFirst();
					if (UtilValidate.isNotEmpty(field)) {
						String customFieldId = field.getString("customFieldId");
						String customFieldName = field.getString("customFieldName");
						GenericValue valueConfig = EntityQuery.use(delegator).from("CustomFieldValueConfig").where("groupId", customFieldGroupId, "customFieldId", customFieldId).queryFirst();
						if (UtilValidate.isNotEmpty(valueConfig)) {
							value = valueConfig.getString("valueData");
						} else {
							value = customFieldName;
						}
						if (UtilValidate.isEmpty(value)) {
							value = customFieldName;
						}
					}
				} else if (UtilValidate.isNotEmpty(valueCapture) && valueCapture.equals("MULTIPLE")) {
					String multiValues = "";
					List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        			conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, customFieldGroupId));
        			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
        					EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, null),
        					EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y")
        					));
        			conditions.add(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"));
                	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					List<GenericValue> fieldList = EntityQuery.use(delegator).select("customFieldId","customFieldName").from("CustomField").where(mainConditon).queryList();
					if (UtilValidate.isNotEmpty(fieldList)) {
						for (GenericValue field : fieldList) {
							String customFieldId = field.getString("customFieldId");
							String customFieldName = field.getString("customFieldName");
							GenericValue valueConfig = EntityQuery.use(delegator).from("CustomFieldValueConfig").where("groupId", customFieldGroupId, "customFieldId", customFieldId).queryFirst();
							if (UtilValidate.isNotEmpty(valueConfig)) {
								value = valueConfig.getString("valueData");
							} else {
								value = customFieldName;
							}
							if (UtilValidate.isEmpty(value)) {
								value = customFieldName;
							}
							if (UtilValidate.isNotEmpty(value)) {
								multiValues = multiValues + value + ",";
							}
						}
						if (UtilValidate.isNotEmpty(multiValues)) {
							multiValues = multiValues.substring(0, multiValues.length()-1);
						}
						value = multiValues;
					}
				}
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		case ValueOverrideType.PARTY_OVERRIDE: 
			try {
				String queryStr = "SELECT cf.custom_Field_Id, cf.custom_Field_Name, cfpc.group_actual_value, cfg.value_capture FROM Custom_Field_Party_Classification cfpc "
						+ " LEFT OUTER JOIN Custom_Field_Group cfg ON cfpc.group_Id = cfg.group_Id "
						+ " LEFT OUTER JOIN Custom_Field cf ON (cfpc.group_Id = cf.group_Id AND cfpc.custom_Field_Id = cf.custom_Field_Id) "
						+ " WHERE cfpc.group_Id=? "
						//+ " AND cfpc.custom_Field_Id=:customFieldId "
						+ " AND cfpc.party_Id=? AND cf.is_Enabled='Y' "
						//+ " LIMIT 1"
						;
				//Debug.logInfo("Segmentation Data extractor context query 1 : "+queryStr, MODULE);
				List<Object> values = new ArrayList<>();
				values.add(customFieldGroupId);
				values.add(partyId);
				ResultSet rs = QueryUtil.getResultSet(queryStr, values, delegator);
				if (rs != null && rs.next()) {
					String customFieldId = rs.getString("cf.custom_Field_Id");
					String customFieldName = rs.getString("cf.custom_Field_Name");
					value = rs.getString("cfpc.group_actual_value");
					valueCapture = rs.getString("cfg.value_capture");
					
					if (UtilValidate.isEmpty(valueCapture) || valueCapture.equals("SINGLE")) {
						if (UtilValidate.isEmpty(value)) {
							GenericValue valueConfig = EntityQuery.use(delegator).from("CustomFieldValueConfig").where("groupId", customFieldGroupId, "customFieldId", customFieldId).queryFirst();
							if (UtilValidate.isNotEmpty(valueConfig)) {
								value = valueConfig.getString("valueData");
							} else {
								value = customFieldName;
							}
						}
						if (UtilValidate.isEmpty(value)) {
							value = customFieldName;
						}
					} else if (UtilValidate.isNotEmpty(valueCapture) && valueCapture.equals("MULTIPLE")) {
						String multiValues = "";
						if (UtilValidate.isEmpty(value)) {
							GenericValue valueConfig = EntityQuery.use(delegator).from("CustomFieldValueConfig").where("groupId", customFieldGroupId, "customFieldId", customFieldId).queryFirst();
							if (UtilValidate.isNotEmpty(valueConfig)) {
								value = valueConfig.getString("valueData");
							}
						}
						if (UtilValidate.isEmpty(value)) {
							value = customFieldName;
						}
						if (UtilValidate.isNotEmpty(value)) {
							multiValues = multiValues + value + ",";
						}
						while (rs.next()) {
							customFieldId = rs.getString("cf.custom_Field_Id");
							customFieldName = rs.getString("cf.custom_Field_Name");
							value = rs.getString("cfpc.group_actual_value");
							valueCapture = rs.getString("cfg.value_capture");
							
							if (UtilValidate.isEmpty(value)) {
								GenericValue valueConfig = EntityQuery.use(delegator).from("CustomFieldValueConfig").where("groupId", customFieldGroupId, "customFieldId", customFieldId).queryFirst();
								if (UtilValidate.isNotEmpty(valueConfig)) {
									value = valueConfig.getString("valueData");
								}
							}
							if (UtilValidate.isEmpty(value)) {
								value = customFieldName;
							}
							if (UtilValidate.isNotEmpty(value)) {
								multiValues = multiValues + value + ",";
							}
						}
						
						if (UtilValidate.isNotEmpty(multiValues)) {
							multiValues = multiValues.substring(0, multiValues.length()-1);
						}
						value = multiValues;
					}
				}
				
				if (UtilValidate.isEmpty(value) && (UtilValidate.isNotEmpty(isCaptureDefault) && isCaptureDefault.equals("Y"))) {
					Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
					extractContext.put("delegator", delegator);
					extractContext.put("valueOverrideType", ValueOverrideType.GLOBAL_OVERRIDE);
					extractContext.put("valueCapture", valueCapture);
        			extractContext.put("customFieldGroupId", customFieldGroupId);
        			
        			Map<String, Object> extractResultContext = SegmentValueFacade.extractData(extractContext);
        			value = (String) extractResultContext.get("value");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		case ValueOverrideType.NO_OVERRIDE: 
			try {
				Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
				extractContext.put("delegator", delegator);
				extractContext.put("valueOverrideType", ValueOverrideType.PARTY_OVERRIDE);
				extractContext.put("valueCapture", valueCapture);
    			extractContext.put("customFieldGroupId", customFieldGroupId);
    			extractContext.put("partyId", partyId);
    			
    			Map<String, Object> extractResultContext = SegmentValueFacade.extractData(extractContext);
    			value = (String) extractResultContext.get("value");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		}
		
		result.put("value", value);
		return result;
	}
	
}
