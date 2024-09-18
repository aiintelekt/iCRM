/**
 * 
 */
package org.groupfio.customfield.service.util;

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

/**
 * @author Sharif
 */
public class DataHelper {
	
	private static String MODULE = DataHelper.class.getName();
	
	public static Map<String, Object> getAgreementTypes(Delegator delegator, List<GenericValue> agreementList) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(agreementList)) {
				List<String> agreementTypeIds = EntityUtil.getFieldListFromEntityList(agreementList, "agreementTypeId", true);
				List < GenericValue > workTypeList = delegator.findList("AgreementType", EntityCondition.makeCondition("agreementTypeId",EntityOperator.IN,agreementTypeIds), UtilMisc.toSet("agreementTypeId", "description"), null, null, false);
				results = workTypeList.stream().collect(Collectors.toMap(x -> (String) x.get("agreementTypeId"),
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
