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
import java.util.stream.Stream;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class CommonDataHelper {

	private static final String MODULE = CommonDataHelper.class.getName();
	
	public static Map<String, Object> getProductStoreNames(Delegator delegator) {
		Map<String, Object> results = new HashMap<>();
		try {
			List < GenericValue > storeList = delegator.findList("ProductStore", null, UtilMisc.toSet("productStoreId", "storeName"), null, null, false);
			results = storeList.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> (String) x.get("productStoreId"),
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
	
	public static Map<String, Object> getBusinessUnitNames(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> buIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(buIds)) {
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.IN, buIds));
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					
					List<GenericValue> storeGroupList = EntityQuery.use(delegator).select("productStoreGroupId", "productStoreGroupName").from("ProductStoreGroup").where(mainConditons).cache(true).queryList();
					results = storeGroupList.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> (String) x.get("productStoreGroupId"),
							x -> UtilValidate.isNotEmpty((String) x.get("productStoreGroupName")) ? (String) x.get("productStoreGroupName") : "",
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
	
	public static Map<String, Object> getPartyIdentificationValues(Delegator delegator, List<GenericValue> dataList, String fieldId, String partyIdentificationTypeId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(partyIds)) {
					results = partyIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
							x -> {
								String idValue = org.fio.homeapps.util.DataUtil.getPartyIdentificationValue(delegator, x, partyIdentificationTypeId, true);
								return UtilValidate.isNotEmpty(idValue) ? idValue : "";
							},
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
	
	public static Map<String, Object> getCurrentResponsiblePartyNames(Delegator delegator, List<GenericValue> dataList, String fieldId, String roleTypeIdFrom) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(partyIds)) {
					results = partyIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
							x -> {
								String relationshipManager = PartyHelper.getCurrentResponsiblePartyName(x, roleTypeIdFrom, delegator);
								return UtilValidate.isNotEmpty(relationshipManager) ? relationshipManager : "";
							},
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
	
	public static Map<String, Object> getEmailAddressList(Delegator delegator, List<GenericValue> dataList, String fieldId, String contactMechPurposeTypeId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(partyIds)) {
					results = partyIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
							x -> {
								String infoString = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, x, contactMechPurposeTypeId);
								return UtilValidate.isNotEmpty(infoString) ? infoString : "";
							},
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
	public static Map<String, Object> getEmailAddressList(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				results = getEmailAddressList(delegator, contactMechIds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	public static Map<String, Object> getEmailAddressList(Delegator delegator, List<String> contactMechIds) {
		Map<String, Object> results = new HashMap<>();
		if (UtilValidate.isNotEmpty(contactMechIds)) {
			for (String contactMechId : contactMechIds) {
				if (UtilValidate.isNotEmpty(contactMechId)) {
					GenericValue contactMech = PartyHelper.getPrimaryEmailAddress(delegator, contactMechId, true);
					if (UtilValidate.isNotEmpty(contactMech)) {
						results.put(contactMechId, contactMech.getString("infoString"));
					}
				}
			}
		}
		return results;
	}
	
	public static Map<String, Object> getContactNumberList(Delegator delegator, List<GenericValue> dataList, String fieldId, String contactMechPurposeTypeId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(partyIds)) {
					results = partyIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
							x -> {
								String phoneNumber = org.fio.homeapps.util.PartyHelper.getContactNumber(delegator, x, "PRIMARY_PHONE");
								return UtilValidate.isNotEmpty(phoneNumber) ? phoneNumber : "";
							},
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
	public static Map<String, Object> getContactNumberList(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				results = getContactNumberList(delegator, contactMechIds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	public static Map<String, Object> getContactNumberList(Delegator delegator, List<String> contactMechIds) {
		Map<String, Object> results = new HashMap<>();
		if (UtilValidate.isNotEmpty(contactMechIds)) {
			for (String contactMechId : contactMechIds) {
				if (UtilValidate.isNotEmpty(contactMechId)) {
					GenericValue contactMech = PartyHelper.getPrimaryTelecomNumber(delegator, contactMechId, true);
					if (UtilValidate.isNotEmpty(contactMech)) {
						results.put(contactMechId, contactMech.getString("contactNumber"));
					}
				}
			}
		}
		return results;
	}
	
	public static Map<String, Object> getPostalAddressList(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				results = getPostalAddressList(delegator, contactMechIds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	public static Map<String, Object> getPostalAddressList(Delegator delegator, List<String> contactMechIds) {
		Map<String, Object> results = new HashMap<>();
		if (UtilValidate.isNotEmpty(contactMechIds)) {
			for (String contactMechId : contactMechIds) {
				if (UtilValidate.isNotEmpty(contactMechId)) {
					GenericValue postalAddress = PartyHelper.getPrimaryPostalAddress(delegator, contactMechId, true);
					if (UtilValidate.isNotEmpty(postalAddress)) {
						results.put(contactMechId, postalAddress);
					}
				}
			}
		}
		return results;
	}
	
	public static Map<String, Object> getPartyDataSourceList(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(partyIds)) {
					results = DataUtil.getPartyDataSourceByPartyId(delegator, partyIds, true, true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getGeoNameList(Delegator delegator, List<GenericValue> dataList, String fieldId, String geoTypeId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList) && dataList.get(0).containsKey(fieldId)) {
				List<String> geoIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true).stream()
						.map(x->x.toString()).collect(Collectors.toList());
				results = getGeoNameList(delegator, geoIds, geoTypeId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	public static Map<String, Object> getGeoNameList(Delegator delegator, List<String> geoIds, String geoTypeId) {
		Map<String, Object> results = new HashMap<>();
		results = geoIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
				x -> {
					List<EntityCondition> conditions = new ArrayList<EntityCondition>();
					conditions.add(EntityCondition.makeCondition("geoId", EntityOperator.EQUALS, x));
					if(UtilValidate.isNotEmpty(geoTypeId)) {
						if(geoTypeId.contains("/")) {
							List<String> geoTypes = Stream.of(geoTypeId.trim().split("/")).collect(Collectors.toList());
							conditions.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.IN, geoTypes ));
						} else {
							conditions.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, geoTypeId));
						}
					}
					
					GenericValue entity = null;
					try {
						entity = EntityQuery.use(delegator).select("geoId", "geoName").from("Geo").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).cache(true).queryFirst();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return UtilValidate.isNotEmpty(entity) && UtilValidate.isNotEmpty(entity.getString("geoName")) ? entity.getString("geoName") : "";
				},
				(attr1, attr2) -> {
					return attr2;
				}));
		return results;
	}
	
	public static List<Map<String, Object>> getPartyListFromEmails(Delegator delegator, List<String> emailList) {
		List<Map<String, Object>> entryList = new ArrayList<>();
		try {
			if (UtilValidate.isNotEmpty(emailList)) {
				for (String emailAddress : emailList) {
					Map<String, Object> party = new LinkedHashMap<>();
					String partyId = org.fio.homeapps.util.DataUtil.getPartyIdByEmail(delegator, emailAddress);
					if (UtilValidate.isNotEmpty(partyId)) {
						party.put("partyId", partyId);
						party.put("emailAddress", emailAddress);
						party.put("partyName", org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyId, false));
						
						entryList.add(party);
					}
				}
			}
		} catch (Exception e) {
		}
		return entryList;
	}
	
	public static Map<String, Object> getCustomFieldGroupNames(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> ids = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(ids)) {
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("groupId", EntityOperator.IN, ids));
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					
					List<GenericValue> entityList = EntityQuery.use(delegator).select("groupId", "groupName").from("CustomFieldGroup").where(mainConditons).cache(true).queryList();
					results = entityList.stream().collect(Collectors.toMap(x -> (String) x.get("groupId"),
							x -> UtilValidate.isNotEmpty((String) x.get("groupName")) ? (String) x.get("groupName") : "",
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
	public static Map<String, Object> getCustomFieldGroupNames(Delegator delegator, Map<String, Object> filter) {
		Map<String, Object> results = new HashMap<>();
		try {
			String groupType = (String) filter.get("groupType");
			
			List conditionList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(groupType)) {
				conditionList.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
			}
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			
			List<GenericValue> entityList = delegator.findList("CustomFieldGroup", mainConditons, UtilMisc.toSet("groupId", "groupName"), null, null, false);
			results = entityList.stream().collect(Collectors.toMap(x -> (String) x.get("groupId"),
					x -> UtilValidate.isNotEmpty((String) x.get("groupName")) ? (String) x.get("groupName") : "",
					(attr1, attr2) -> {
						return attr2;
					}));
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getProductTypeList(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> productTypeIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true);
				if (UtilValidate.isNotEmpty(productTypeIds)) {
					results = productTypeIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
							x -> {
								String des = "";
								try {
									List<EntityCondition> conditions = new ArrayList<EntityCondition>();
									conditions.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, x));
									EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
									GenericValue prodType = EntityQuery.use(delegator).select("description").from("ProductType").where(mainConditon).queryFirst();
									if (UtilValidate.isNotEmpty(prodType)) {
										des = prodType.getString("description");
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								return UtilValidate.isNotEmpty(des) ? des : "";
							},
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
