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

import org.fio.homeapps.constants.GlobalConstants.EnumDisplayType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class EnumUtil {

	private static String MODULE = EnumUtil.class.getName();
	
	public static List<GenericValue> getEnums (Delegator delegator, String countryGeoId, String listType) {
		List<GenericValue> enumList = new ArrayList<GenericValue>();
		try {
			if (UtilValidate.isNotEmpty(countryGeoId) && UtilValidate.isNotEmpty(listType)) {
				
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("listType", EntityOperator.EQUALS, listType),
						EntityCondition.makeCondition("countryCode", EntityOperator.EQUALS, countryGeoId)
               			);
				
				GenericValue countryEnumeration = EntityQuery.use(delegator).from("CountryEnumeration").where(condition).queryFirst();
				if (UtilValidate.isNotEmpty(countryEnumeration)) {
					enumList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", countryEnumeration.getString("enumTypeId")), UtilMisc.toList("sequenceId"), false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return enumList;
	}
	
	public static String getEnumDescription(Delegator delegator, String value) {
		return getEnumDescription(delegator, value, null);
	}
	
	public static String getEnumDescription(Delegator delegator, String value, String enumTypeId) {
		return getEnumDescription(delegator, value, enumTypeId, null, false);
	}
	
	public static String getEnumDescription(Delegator delegator, String value, String enumTypeId, boolean useCache) {
		return getEnumDescription(delegator, value, enumTypeId, null, useCache);
	}
	
	public static String getEnumDescription(Delegator delegator, String value, String enumTypeId, String enumService) {
		return getEnumDescription(delegator, value, enumTypeId, enumService, false);
	}
	
	public static String getEnumDescription(Delegator delegator, String value, String enumTypeId, String enumService, boolean useCache) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumMsId")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			) );    
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					conditionList.add( EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId));       
				}
				
				if (UtilValidate.isNotEmpty(enumService)) {
					conditionList.add( EntityCondition.makeCondition("enumService", EntityOperator.EQUALS, enumService));       
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue enumEntity = EntityQuery.use(delegator).select("description").from("Enumeration").where(mainConditons).cache(useCache).queryFirst();
				if (UtilValidate.isNotEmpty(enumEntity)) {
					return enumEntity.getString("description");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getEnumName(Delegator delegator, String value) {
		return getEnumName(delegator, value, null);
	}
	
	public static String getEnumName(Delegator delegator, String value, String enumTypeId) {
		return getEnumName(delegator, value, enumTypeId, null, false);
	}
	
	public static String getEnumName(Delegator delegator, String value, String enumTypeId, boolean useCache) {
		return getEnumName(delegator, value, enumTypeId, null, useCache);
	}
	
	public static String getEnumName(Delegator delegator, String value, String enumTypeId, String enumService, boolean useCache) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumMsId")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			) );    
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					conditionList.add( EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId));       
				}
				
				if (UtilValidate.isNotEmpty(enumService)) {
					conditionList.add( EntityCondition.makeCondition("enumService", EntityOperator.EQUALS, enumService));       
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue enumEntity = EntityQuery.use(delegator).select("name").from("Enumeration").where(mainConditons).cache(useCache).queryFirst();
				if (UtilValidate.isNotEmpty(enumEntity)) {
					return enumEntity.getString("name");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getEnumDescriptionByEnumId(Delegator delegator, String enumId) {
		try {
			if (UtilValidate.isNotEmpty(enumId)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition("enumId", EntityOperator.EQUALS, enumId) );    
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue enumEntity = EntityQuery.use(delegator).select("description").from("Enumeration").where(mainConditons).cache(true).queryFirst();
				if (UtilValidate.isNotEmpty(enumEntity)) {
					return enumEntity.getString("description");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getEnumId(Delegator delegator, String value) {
		return getEnumId(delegator, value, null);
	}
	
	public static String getEnumId(Delegator delegator, String value, String enumTypeId) {
		return getEnumId(delegator, value, enumTypeId, null);
	}
	
	public static String getEnumId(Delegator delegator, String value, String enumTypeId, String enumService) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumMsId")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			) );    
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					conditionList.add( EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId));       
				}
				
				if (UtilValidate.isNotEmpty(enumService)) {
					conditionList.add( EntityCondition.makeCondition("enumService", EntityOperator.EQUALS, enumService));       
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue enumEntity = EntityQuery.use(delegator).select("enumId").from("Enumeration").where(mainConditons).cache(true).queryFirst();
				if (UtilValidate.isNotEmpty(enumEntity)) {
					return enumEntity.getString("enumId");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getEnumCode(Delegator delegator, String value) {
		return getEnumCode(delegator, value, null);
	}
	
	public static String getEnumCode(Delegator delegator, String value, String enumTypeId) {
		return getEnumCode(delegator, value, enumTypeId, null);
	}
	
	public static String getEnumCode(Delegator delegator, String value, String enumTypeId, String enumService) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumMsId")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			) );    
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					conditionList.add( EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId));       
				}
				
				if (UtilValidate.isNotEmpty(enumService)) {
					conditionList.add( EntityCondition.makeCondition("enumService", EntityOperator.EQUALS, enumService));       
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue enumEntity = EntityQuery.use(delegator).select("enumCode").from("Enumeration").where(mainConditons).cache(true).queryFirst();
				if (UtilValidate.isNotEmpty(enumEntity)) {
					return enumEntity.getString("enumCode");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getEnumName(Delegator delegator, String value, String enumTypeId, String enumService) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumMsId")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			) );    
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					conditionList.add( EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId));       
				}
				
				if (UtilValidate.isNotEmpty(enumService)) {
					conditionList.add( EntityCondition.makeCondition("enumService", EntityOperator.EQUALS, enumService));       
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue enumEntity = EntityQuery.use(delegator).select("name").from("Enumeration").where(mainConditons).cache(true).queryFirst();
				if (UtilValidate.isNotEmpty(enumEntity)) {
					return enumEntity.getString("name");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static boolean isValidEnum(Delegator delegator, String value) {
		return isValidEnum(delegator, value, null);
	}
	
	public static boolean isValidEnum(Delegator delegator, String value, String enumTypeId) {
		return isValidEnum(delegator, value, enumTypeId, null);
	}
	
	public static boolean isValidEnum(Delegator delegator, String value, String enumTypeId, String enumService) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumMsId")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			) );    
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					conditionList.add( EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId));       
				}
				
				if (UtilValidate.isNotEmpty(enumService)) {
					conditionList.add( EntityCondition.makeCondition("enumService", EntityOperator.EQUALS, enumService));       
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue enumEntity = EntityQuery.use(delegator).from("Enumeration").where(mainConditons).queryFirst();
				return UtilValidate.isNotEmpty(enumEntity);
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	public static GenericValue getValidEnum(Delegator delegator, String value) {
		return getValidEnum(delegator, value, null);
	}
	
	public static GenericValue getValidEnum(Delegator delegator, String value, String enumService) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumMsId")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			) );    
				
				if (UtilValidate.isNotEmpty(enumService)) {
					conditionList.add( EntityCondition.makeCondition("enumService", EntityOperator.EQUALS, enumService));       
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue enumEntity = EntityQuery.use(delegator).from("Enumeration").where(mainConditons).queryFirst();
				return enumEntity;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static GenericValue getEnum(Delegator delegator, String value) {
		return getEnum(delegator, value, null);
	}
	
	public static GenericValue getEnum(Delegator delegator, String value, String enumTypeId) {
		return getEnum(delegator, value, enumTypeId, null);
	}
	
	public static GenericValue getEnum(Delegator delegator, String value, String enumTypeId, String enumService) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumMsId")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			) );    
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					conditionList.add( EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId));       
				}
				
				if (UtilValidate.isNotEmpty(enumService)) {
					conditionList.add( EntityCondition.makeCondition("enumService", EntityOperator.EQUALS, enumService));       
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue enumEntity = EntityQuery.use(delegator).select("enumId", "enumCode", "description", "enumMsId", "enumTypeId", "enumService", "parentEnumId", "enumEntity").from("Enumeration").where(mainConditons).queryFirst();
				return enumEntity;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static List<GenericValue> getEnums(Delegator delegator, String enumTypeId){
		List<GenericValue> enumList = null;
		try {
			if(UtilValidate.isNotEmpty(enumTypeId)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,enumTypeId),
						EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("disabled",EntityOperator.EQUALS,null),
								EntityCondition.makeCondition("disabled",EntityOperator.EQUALS,"N")));
				enumList = EntityQuery.use(delegator).from("Enumeration").where(condition).orderBy("sequenceId").queryList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return enumList;
		
	}
	
	public static List<GenericValue> getEnableEnums(Delegator delegator, String enumTypeId){
		List<GenericValue> enumList = null;
		try {
			if(UtilValidate.isNotEmpty(enumTypeId)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,enumTypeId),
						EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("isEnabled",EntityOperator.EQUALS,null),
								EntityCondition.makeCondition("isEnabled",EntityOperator.NOT_EQUAL,"N")));
				enumList = EntityQuery.use(delegator).from("Enumeration").where(condition).orderBy("sequenceId").queryList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return enumList;
	}
	
	public static Map<String, Object> getEnumList(Delegator delegator, List<GenericValue> dataList, String fieldId, String enumType) {
		return getEnumList(delegator, dataList, fieldId, enumType, null);
	}
	public static Map<String, Object> getEnumList(Delegator delegator, List<GenericValue> dataList, String fieldId, String enumType, EnumDisplayType displayType) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> enumIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true).stream()
						.map(x->x.toString()).collect(Collectors.toList());
				results = getEnumList(delegator, enumIds, enumType, displayType);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static Map<String, Object> getEnumList(Delegator delegator, List<String> enumIds, String enumType) {
		return getEnumList(delegator, enumIds, enumType, null);
	}
	public static Map<String, Object> getEnumList(Delegator delegator, List<String> enumIds, String enumType, EnumDisplayType displayType) {
		Map<String, Object> results = new HashMap<>();
		results = enumIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
				x -> {
					String val = null;
					if (UtilValidate.isNotEmpty(displayType)) {
						if (displayType.equals(EnumDisplayType.CODE)) {
							val = EnumUtil.getEnumCode(delegator, x, enumType, null);
						} else if (displayType.equals(EnumDisplayType.NAME)) {
							val = EnumUtil.getEnumName(delegator, x, enumType, null);
						} else if (displayType.equals(EnumDisplayType.DESCRIPTION)) {
							val = EnumUtil.getEnumDescription(delegator, x, enumType, true);
						}
					} else {
						val = EnumUtil.getEnumDescription(delegator, x, enumType, true);
					}
					
					if (UtilValidate.isEmpty(val)) {
						val = "";
					}
					return UtilValidate.isNotEmpty(x) ? val : "";
				},
				(attr1, attr2) -> {
					return attr2;
				}));
		return results;
	}
	
}
