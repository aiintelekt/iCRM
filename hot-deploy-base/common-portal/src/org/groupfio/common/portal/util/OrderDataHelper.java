/**
 * 
 */
package org.groupfio.common.portal.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

/**
 * @author Sharif
 *
 */
public class OrderDataHelper {
	
	private static final String MODULE = UtilOrder.class.getName();
	
	public static Map<String, Object> getMainOrderTypes(Delegator delegator) {
		Map<String, Object> results = new HashMap<>();
		try {
			List < GenericValue > orderTypeList = delegator.findList("OrderType", null, UtilMisc.toSet("orderTypeId", "description"), null, null, false);
			results = orderTypeList.stream().collect(Collectors.toMap(x -> (String) x.get("orderTypeId"),
					x -> UtilValidate.isNotEmpty((String) x.get("description")) ? (String) x.get("description") : "",
					(attr1, attr2) -> {
						return attr2;
					}));
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}

}
