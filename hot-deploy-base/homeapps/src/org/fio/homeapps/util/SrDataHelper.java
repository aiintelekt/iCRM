/**
 * 
 */
package org.fio.homeapps.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class SrDataHelper {

	private static final String MODULE = SrDataHelper.class.getName();
	
	public static Map<String, Object> getProductStoreNames(Delegator delegator) {
		Map<String, Object> results = new HashMap<>();
		try {
			List<GenericValue> storeList = EntityQuery.use(delegator).select("productStoreId", "storeName").from("ProductStore").cache(true).queryList();
			results = storeList.stream().collect(Collectors.toMap(x -> (String) x.get("productStoreId"),
					x -> UtilValidate.isNotEmpty((String) x.get("storeName")) ? (String) x.get("storeName") : "",
					(attr1, attr2) -> {
						return attr2;
					}));
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getSrAssocPartyNames(Delegator delegator, String custRequestId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				List<GenericValue> assocPartyList = EntityQuery.use(delegator).select("partyId","roleTypeId").from("CustRequestParty").where("custRequestId", custRequestId).filterByDate().cache(true).queryList();
				results = assocPartyList.stream().collect(Collectors.toMap(x -> (String) x.get("roleTypeId"),
						x -> PartyHelper.getPartyName(delegator, (String) x.get("partyId"), false),
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
	
	public static Map<String, Map<String, Object>> getSrAssocPartys(Delegator delegator, String custRequestId) {
		Map<String, Map<String, Object>> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				List<GenericValue> assocPartyList = EntityQuery.use(delegator).select("partyId","roleTypeId").from("CustRequestParty").where("custRequestId", custRequestId).filterByDate().cache(true).queryList();
				for (GenericValue assocParty : assocPartyList) {
					Map<String, Object> data = new HashMap();
					data.put("partyId", assocParty.getString("partyId"));
					data.put("partyName", PartyHelper.getPartyName(delegator, assocParty.getString("partyId"), false));
					
					results.put(assocParty.getString("roleTypeId"), data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getSrTypeNames(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList) && dataList.get(0).containsKey(fieldId)) {
				List<String> ids = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(ids)) {
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.IN, ids));
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					
					List<GenericValue> entityList = EntityQuery.use(delegator).select("custRequestTypeId", "description").from("CustRequestType").where(mainConditons).cache(true).queryList();
					results = entityList.stream().collect(Collectors.toMap(x -> (String) x.get("custRequestTypeId"),
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
	
	public static Map<String, Object> getSrCategoryNames(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> ids = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(ids)) {
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("custRequestCategoryId", EntityOperator.IN, ids));
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					
					List<GenericValue> entityList = EntityQuery.use(delegator).select("custRequestCategoryId", "description").from("CustRequestCategory").where(mainConditons).cache(true).queryList();
					results = entityList.stream().collect(Collectors.toMap(x -> (String) x.get("custRequestCategoryId"),
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
	
	public static Map<String, Object> getSrStatusEmailTemplates(Delegator delegator, List<GenericValue> dataList) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				results = dataList.stream().collect(Collectors.toMap(x -> (String) x.get("statusId"),
						x -> UtilValidate.isNotEmpty((String) x.get("emailTemplateId")) ? (String) x.get("emailTemplateId") : "",
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
	
	public static Map<String, Object> getOrderTypes(Delegator delegator) {
		Map<String, Object> results = new HashMap<>();
		try {
			List < GenericValue > orderTypeList = delegator.findList("EntityOrderType", null, UtilMisc.toSet("orderTypeId", "orderTypeDesc"), null, null, false);
			results = orderTypeList.stream().collect(Collectors.toMap(x -> (String) x.get("orderTypeId"),
					x -> UtilValidate.isNotEmpty((String) x.get("orderTypeDesc")) ? (String) x.get("orderTypeDesc") : "",
					(attr1, attr2) -> {
						return attr2;
					}));
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getIssueMaterialProducts(Delegator delegator, List<GenericValue> dataList) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				results = dataList.stream().collect(Collectors.toMap(x -> (String) x.get("productId"),
						x -> UtilValidate.isNotEmpty((String) x.get("productName")) ? (String) x.get("productName") : "",
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
	
	public static Map<String, Object> getScheduledDate(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				final String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
				List<String> typeIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true).stream()
						.map(x->x.toString()).collect(Collectors.toList());
				results = typeIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
						x -> {
							DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
							dynamicViewEntity.addMemberEntity("CRWE", "CustRequestWorkEffort");
							dynamicViewEntity.addAlias("CRWE", "custRequestId");
							dynamicViewEntity.addAlias("CRWE", "workEffortId");
							dynamicViewEntity.addMemberEntity("WE", "WorkEffort");
							dynamicViewEntity.addAlias("WE", "currentStatusId");
							dynamicViewEntity.addAlias("WE", "workEffortPurposeTypeId");
							dynamicViewEntity.addAlias("WE", "estimatedStartDate");
							dynamicViewEntity.addAlias("WE", "estimatedCompletionDate");
							dynamicViewEntity.addViewLink("CRWE", "WE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));
							try {
								EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
										EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, x),
										EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "IA_MSCHEDULED"),
										EntityCondition.makeCondition("workEffortPurposeTypeId", EntityOperator.IN, UtilMisc.toList("TEST_WORK_TYPE","TEST_WORK_TYPE_001"))
										);
								GenericValue workEffortGv = EntityQuery.use(delegator).from(dynamicViewEntity).where(condition).queryFirst();
								String estimateStartDateStr = "";
								if(UtilValidate.isNotEmpty(workEffortGv)) {
									Timestamp estimateStartDate = workEffortGv.getTimestamp("estimatedStartDate");
									estimateStartDateStr = UtilDateTime.timeStampToString(
											estimateStartDate, globalDateTimeFormat, TimeZone.getDefault(),
											null);
								}
								return estimateStartDateStr;
							} catch (Exception e) {
								return "";
							}
						},
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
	
	public static Map<String, Object> getCustRequestNames(Delegator delegator, List<GenericValue> dataList) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> custRequestIds = EntityUtil.getFieldListFromEntityList(dataList, "custRequestId", true);
				List < GenericValue > workTypeList = delegator.findList("CustRequest", EntityCondition.makeCondition("custRequestId",EntityOperator.IN,custRequestIds), UtilMisc.toSet("custRequestId", "custRequestName"), null, null, false);
				results = workTypeList.stream().collect(Collectors.toMap(x -> (String) x.get("custRequestId"),
						x -> UtilValidate.isNotEmpty((String) x.get("custRequestName")) ? (String) x.get("custRequestName") : "",
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
