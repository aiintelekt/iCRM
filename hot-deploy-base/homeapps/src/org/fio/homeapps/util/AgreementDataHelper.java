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
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class AgreementDataHelper {

	private static final String MODULE = AgreementDataHelper.class.getName();
	
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
	
	public static Map<String, Object> getAgreementItemTypes(Delegator delegator, List<GenericValue> agreementList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(agreementList)) {
				if (UtilValidate.isEmpty(fieldId)) {
					fieldId = "agreementItemTypeId";
				}
				List<String> agreementTypeIds = EntityUtil.getFieldListFromEntityList(agreementList, fieldId, true);
				List < GenericValue > workTypeList = delegator.findList("AgreementItemType", EntityCondition.makeCondition("agreementItemTypeId",EntityOperator.IN,agreementTypeIds), UtilMisc.toSet("agreementItemTypeId", "description"), null, null, false);
				results = workTypeList.stream().collect(Collectors.toMap(x -> (String) x.get("agreementItemTypeId"),
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
	
	public static Map<String, Object> getAgreementItemTypes(Delegator delegator, List<GenericValue> agreementList) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(agreementList)) {
				List<String> agreementTypeIds = EntityUtil.getFieldListFromEntityList(agreementList, "agreementItemTypeId", true);
				List < GenericValue > workTypeList = delegator.findList("AgreementItemType", EntityCondition.makeCondition("agreementItemTypeId",EntityOperator.IN,agreementTypeIds), UtilMisc.toSet("agreementItemTypeId", "description"), null, null, false);
				results = workTypeList.stream().collect(Collectors.toMap(x -> (String) x.get("agreementItemTypeId"),
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
	
	public static Map<String, Object> getAgreementYears(Delegator delegator, List<GenericValue> agreementList) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(agreementList)) {
				List<String> agreementIds = EntityUtil.getFieldListFromEntityList(agreementList, "agreementId", true);
				results = agreementIds.stream().collect(Collectors.toMap(x -> x,
						x -> getAgreementAttrValue(delegator, x, "AGREEMENT_YEAR"),
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
	
	public static String getAgreementAttrValue(Delegator delegator, String agreementId, String attrName) {
		try {
			if (UtilValidate.isNotEmpty(agreementId)) {
				GenericValue entity = EntityQuery.use(delegator).from("AgreementAttribute").where("agreementId", agreementId, "attrName", attrName).cache(false).queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
					return entity.getString("attrValue");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
		return null;
	}
	
}
