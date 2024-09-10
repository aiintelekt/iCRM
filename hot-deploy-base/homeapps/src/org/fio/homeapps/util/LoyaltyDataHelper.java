/**
 * 
 */
package org.fio.homeapps.util;

import java.util.HashMap;
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
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class LoyaltyDataHelper {

	private static String MODULE = LoyaltyDataHelper.class.getName();
	
	public static Map<String, Object> getPromoTypeNames(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList) && dataList.get(0).containsKey(fieldId)) {
				List<String> ids = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(ids)) {
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("productPromoTypeId", EntityOperator.IN, ids));
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					
					List<GenericValue> entityList = delegator.findList("ProductPromoType", mainConditons, UtilMisc.toSet("productPromoTypeId", "description"), null, null, false);
					results = entityList.stream().collect(Collectors.toMap(x -> (String) x.get("productPromoTypeId"),
							x -> UtilValidate.isNotEmpty((String) x.get("description")) ? (String) x.get("description") : "",
							(attr1, attr2) -> {
								return attr2;
							}));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
}
