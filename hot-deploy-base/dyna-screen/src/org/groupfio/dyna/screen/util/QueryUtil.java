/**
 * 
 */
package org.groupfio.dyna.screen.util;

import java.util.List;
import java.util.Map;

import org.groupfio.dyna.screen.DynaScreenConstants;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

/**
 * @author Sharif
 *
 */
public class QueryUtil {
	
	private static String MODULE = QueryUtil.class.getName();

	public static List<EntityCondition> makeCondition(List<EntityCondition> conditionList, Object filterValue) {
        try {
            if (UtilValidate.isNotEmpty(filterValue)) {
            	if (filterValue instanceof Map) {
            		Map<String, Object> filters = (Map<String, Object>) filterValue;
            		for (String key : filters.keySet()) {
            			Object value = filters.get(key);
            			value = value.equals(null) ? null : value;
						conditionList.add(EntityCondition.makeCondition(key, EntityOperator.EQUALS, value));
					}
            	} else if (filterValue instanceof List) {
            		makeCondition(conditionList, (List) filterValue);
            	}
            }
        } catch (Exception e) {}

        return conditionList;
    }
	
	public static List<EntityCondition> makeCondition(List<EntityCondition> conditionList, List<Map> filterValue) {
        try {
            if (UtilValidate.isNotEmpty(filterValue)) {
            	for (Map filter : filterValue) {
            		String fieldName = (String) filter.get("field_name");
            		String operation = (String) filter.get("operation");
            		Object fieldValue = filter.get("field_value");
            		
            		fieldValue = fieldValue.equals(null) ? null : fieldValue;
            		
            		Object fv = filter.get("filter_value");
            		
            		conditionList.add(EntityCondition.makeCondition(fieldName, DynaScreenConstants.ENTITY_OPERATOR_BY_NAME.get(operation), fieldValue));
            		if (UtilValidate.isNotEmpty(fv)) {
            			makeCondition(conditionList, fv);
            		}
            	} 
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return conditionList;
	}
	
}
