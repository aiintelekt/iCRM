/**
 * 
 */
package org.fio.homeapps.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
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
public class ActivityDataHelper {

	private static final String MODULE = ActivityDataHelper.class.getName();
	
	public static Map<String, Object> getWorkEffortPurposeTypes(Delegator delegator, List<GenericValue> interactiveActivities) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(interactiveActivities)) {
				List<String> workTypeIds = EntityUtil.getFieldListFromEntityList(interactiveActivities, "workEffortPurposeTypeId", true);
				List < GenericValue > workTypeList = delegator.findList("WorkEffortPurposeType", EntityCondition.makeCondition("workEffortPurposeTypeId",EntityOperator.IN,workTypeIds), UtilMisc.toSet("workEffortPurposeTypeId", "description"), null, null, false);
				results = workTypeList.stream().collect(Collectors.toMap(x -> (String) x.get("workEffortPurposeTypeId"),
						x -> UtilValidate.isNotEmpty((String) x.get("description")) ? (String) x.get("description") : "",
						(attr1, attr2) -> {
							return attr2;
						}));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getWorkEffortTypes(Delegator delegator, List<GenericValue> interactiveActivities) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(interactiveActivities)) {
				List<String> workTypeIds = EntityUtil.getFieldListFromEntityList(interactiveActivities, "workEffortTypeId", true);
				List < GenericValue > workTypeList = delegator.findList("WorkEffortType", EntityCondition.makeCondition("workEffortTypeId",EntityOperator.IN,workTypeIds), UtilMisc.toSet("workEffortTypeId", "description"), null, null, false);
				results = workTypeList.stream().collect(Collectors.toMap(x -> (String) x.get("workEffortTypeId"),
						x -> UtilValidate.isNotEmpty((String) x.get("description")) ? (String) x.get("description") : "",
						(attr1, attr2) -> {
							return attr2;
						}));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
}
