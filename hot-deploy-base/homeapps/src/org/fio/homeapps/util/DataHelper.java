/**
 * 
 */
package org.fio.homeapps.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class DataHelper {
	
	private static String MODULE = DataHelper.class.getName();
	public static final ObjectMapper mapper = new ObjectMapper();

	public static Map<String, Object> getLockboxStores(Delegator delegator) {
		
		Map<String, Object> lockboxStores = new LinkedHashMap<String, Object>();
		try {
			String defaultSalesChannelEnumId = "SPY_SALES_CHANNEL";
			List<GenericValue> asList = delegator.findAll("ProductStore", false);
			for(GenericValue productStore : asList){
				String storeName = "";
				if(UtilValidate.isNotEmpty(productStore)){
					//storeName = productStore.getString("storeName")+" ["+as.getString("productStoreId")+"]";
					storeName = productStore.getString("storeName");
				}
				
				lockboxStores.put(productStore.getString("productStoreId"), storeName);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
		}
		
		return lockboxStores;
	}
	
	public static Map getDropDownOptions(List<GenericValue> entityList, String keyField, String desField) {
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		for (GenericValue entity : entityList) {
			String desc = entity.getString(desField);
			if (UtilValidate.isNotEmpty(desc)) {
				if (UtilValidate.isNotEmpty(desc) && desc.length() > 25) {
					desc = desc.substring(0, 25) + "..";
				}
				options.put(entity.getString(keyField), desc);
			}
		}
		return options;
	}
	
	public static Map getDropDownOptions(List<GenericValue> entityList, String keyField, String desField1, String desField2) {
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		for (GenericValue entity : entityList) {
			if (UtilValidate.isNotEmpty(desField1) && UtilValidate.isNotEmpty(desField2)) {
				String desc = entity.getString(desField1)+" - "+entity.getString(desField2);
				if (UtilValidate.isNotEmpty(desc) && desc.length() > 15) {
					desc = desc.substring(0, 15) + "..";
				}
				options.put(entity.getString(keyField), desc);
			} else {
				options.put(entity.getString(keyField), entity.getString(desField1));
			}
		}
		return options;
	}
	
	public static Map getDropDownOptions(List<GenericValue> entityList, String keyField, String desField, int size, boolean limit) {
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		if(limit && UtilValidate.isNotEmpty(size) && size < 1) {
			size = 25;
		}
		for (GenericValue entity : entityList) {
			String desc = entity.getString(desField);
			if (UtilValidate.isNotEmpty(desc) && desc.length() > size && limit) {
				desc = desc.substring(0, size) + "..";
			}
			options.put(entity.getString(keyField), desc);
		}
		return options;
	}
	
	public static Map getDropDownOptions(List<GenericValue> entityList, String keyField, String desField1, String desField2, int size, boolean limit) {
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		if(limit && UtilValidate.isNotEmpty(size) && size < 1) {
			size = 15;
		}
		for (GenericValue entity : entityList) {
			if (UtilValidate.isNotEmpty(desField1) && UtilValidate.isNotEmpty(desField2)) {
				String desc = entity.getString(desField1)+" - "+entity.getString(desField2);
				if (UtilValidate.isNotEmpty(desc) && desc.length() > size && limit) {
					desc = desc.substring(0, size) + "..";
				}
				options.put(entity.getString(keyField), desc);
			} else {
				options.put(entity.getString(keyField), entity.getString(desField1));
			}
		}
		return options;
	}
	
	public static Map getDropDownOptionsFromMap(List<Map<String, Object>> entityList, String keyField, String desField) {
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		if (UtilValidate.isNotEmpty(entityList)) {
			for (Map<String, Object> entity : entityList) {
				String desc = (String) entity.get(desField);
				if (UtilValidate.isNotEmpty(desc)) {
					if (UtilValidate.isNotEmpty(desc) && desc.length() > 25) {
						desc = desc.substring(0, 25) + "..";
					}
					options.put(entity.get(keyField).toString(), desc);
				}
			}
		}
		return options;
	}
	
	public static Map getLovDropDownOptions(Delegator delegator, String entityName, String primaryKey, String primaryKeyValue, String keyField, String desField){
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		try {
			List<GenericValue> entityList = delegator.findByAnd(entityName, UtilMisc.toMap(primaryKey, primaryKeyValue), UtilMisc.toList("sequence"), false);
			options = org.fio.homeapps.util.DataHelper.getDropDownOptions(entityList, keyField, desField);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		return options;
	}
	
	public static Map getLovDropDownOptions(Delegator delegator, String entityName, String primaryKey, String primaryKeyValue, String keyField, String desField1, String desField2){
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		try {
			List<GenericValue> entityList = delegator.findByAnd(entityName, UtilMisc.toMap(primaryKey, primaryKeyValue), UtilMisc.toList("sequence"), false);
			options = org.fio.homeapps.util.DataHelper.getDropDownOptions(entityList, keyField, desField1, desField2);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		return options;
	}
	
	public static Map getDayDropDownOptions() {
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		for (int i = 1; i<=31; i++) {
			options.put(""+i, ""+i);
		}
		return options;
	}
	
	public static Map getSupplierList(Delegator delegator){
		
		Map<String, Object> suppliers = new LinkedHashMap<String, Object>();
		try {
			List<GenericValue> supplierList = delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId", "SUPPLIER"), null, false);
			for (GenericValue supplier : supplierList) {
				suppliers.put(supplier.getString("partyId"), PartyHelper.getPartyName(supplier) + " [" + supplier.getString("partyId") + "]");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		return suppliers;
		
	}
	
	public static Map getServiceList(Delegator delegator){
		
		Map<String, Object> services = new LinkedHashMap<String, Object>();
		try {
			List<GenericValue> serviceList = delegator.findByAnd("ServiceName", UtilMisc.toMap("componentName", "custom-field"), null, false);
			for (GenericValue service : serviceList) {
				services.put(service.getString("serviceName"), " [ " + service.getString("serviceName") + " ] - " + service.getString("description"));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		return services;
		
	}
	
	public static String getModelName(Delegator delegator, String modelId){
		try {
			
			GenericValue etlModel = EntityUtil.getFirst( delegator.findByAnd("EtlModel", UtilMisc.toMap("modelId", modelId), null, false) );
			if (UtilValidate.isNotEmpty(etlModel)) {
				return etlModel.getString("modelName");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getGroupName(Delegator delegator, String groupId){
		try {
			
			GenericValue etlGroup = EntityUtil.getFirst( delegator.findByAnd("EtlGrouping", UtilMisc.toMap("groupId", groupId), null, false) );
			if (UtilValidate.isNotEmpty(etlGroup)) {
				return etlGroup.getString("groupName");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getServiceDescription(Delegator delegator, String serviceName, String componentName){
		try {
			
			if (UtilValidate.isEmpty(serviceName) || UtilValidate.isEmpty(componentName)) {
				return null;
			}
			
			GenericValue service = EntityUtil.getFirst( delegator.findByAnd("ServiceName", UtilMisc.toMap("serviceName", serviceName, "componentName", componentName), null, false) );
			if (UtilValidate.isNotEmpty(service)) {
				return service.getString("description");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<GenericValue> getAccountList(Delegator delegator, GenericValue userLogin){
		
		List<GenericValue> accounts = new ArrayList<GenericValue>();
		
		try {
			
			List<EntityCondition> accountConditions = new ArrayList<EntityCondition>();
    		EntityCondition accountRoleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"));
    		accountConditions.add(accountRoleTypeCondition);

			EntityCondition accountPartyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
			EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);

			accountConditions.add(accountPartyStatusCondition);
			accountConditions.add(EntityUtil.getFilterByDateExpr());
    		
    		/*EntityCondition securityConditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, lowerPositionPartyIds),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
					EntityUtil.getFilterByDateExpr()
					);
    		
    		if (UtilValidate.isNotEmpty(userLogin)) {
				securityConditions = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")),
						securityConditions
					), EntityOperator.OR);
			}
    		
    		accountConditions.add(securityConditions);*/
    		
    		EntityCondition mainConditons = EntityCondition.makeCondition(accountConditions, EntityOperator.AND);
    		
    		EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.getDistinct();
			
			Debug.logInfo("count 1 start: "+UtilDateTime.nowTimestamp(), MODULE);
			accounts = delegator.findList("PartyCommonView", mainConditons, UtilMisc.toSet("partyId", "groupName"), UtilMisc.toList("partyId"+ " " + "ASC"), efo, false);
			Debug.logInfo("count 2 start: "+UtilDateTime.nowTimestamp(), MODULE);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return accounts;
	}
	
	public static List<GenericValue> getCurrencyList(Delegator delegator){

		List<GenericValue> currencyList = new ArrayList<GenericValue>();

		try {

			List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
			conditionsList.add(EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "CURRENCY_MEASURE"));
			conditionsList.add(EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y")
					), EntityOperator.OR));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			currencyList = delegator.findList("Uom", mainConditons, null, UtilMisc.toList("uomId"), null, false);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return currencyList;
	}
	
	public static Map<String, Object> convertStringToMapValue(String value) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(UtilValidate.isNotEmpty(value)) {
			value = value.substring(1, value.length()-1);
			String[] keyValuePairs = value.split(",");
			for(String pair: keyValuePairs) {
				String[] entry = pair.split("=");
				map.put(entry[0].trim(), entry[1].trim());
			}
		}
		return map;
	}
	
	public static Map getSequenceDropDownOptions(int count) {
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		for (int i = 1; i<=count; i++) {
			options.put(""+i, ""+i);
		}
		return options;
	}
	
	public static String getDefaultValue(Delegator delegator, String configId, String typeId){
		String defaultValue = null;
		if(UtilValidate.isNotEmpty(configId) && UtilValidate.isNotEmpty(typeId)) {
			try {
				
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("profileConfigurationId", EntityOperator.EQUALS, configId),
						EntityCondition.makeCondition("profileCode", EntityOperator.EQUALS, configId)
	                    ));
				
				conditionList.add(EntityCondition.makeCondition("profileTypeId", EntityOperator.EQUALS, typeId));
				
				GenericValue profileConfig = EntityQuery.use(delegator).from("ProfileConfiguration")
						.where(conditionList).queryOne();
				
				if(UtilValidate.isNotEmpty(profileConfig)) {
					defaultValue = profileConfig.getString("profileDescription");
				}
				
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return defaultValue;
	}
	
	public static String javaPropToLabelProp(String prop) {
		String convertedProp = "";
		if (UtilValidate.isNotEmpty(prop)) {
			for (int i = 0; i < prop.length(); i++) {
				if (Character.isUpperCase(prop.charAt(i))) {
					String additionalChar = (i == 0) ? "" : "_";
					prop = prop.replace("" + prop.charAt(i), additionalChar + ("" + prop.charAt(i)).toLowerCase());
				}
	
			}
			
			StringTokenizer st = new StringTokenizer(prop, "_");
			while (st.hasMoreTokens()) {
				String val = (String) st.nextToken();
				convertedProp += (""+val.charAt(0)).toUpperCase()+val.substring(1, val.length()) + " ";  
			}
			convertedProp = convertedProp.substring(0, convertedProp.length()-1);
		}
		return convertedProp;
	}
	
	public static List jsonArrayStrToMapList(String jsonBodyStr) {
		TypeFactory factory = mapper.getTypeFactory();
		CollectionType listType = 
			    factory.constructCollectionType(List.class, Map.class);

		List<Map<String, Object>> result = null;
		try {
			result = mapper.readValue(jsonBodyStr, listType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}
	
	public static Map jsonStrToMap(String jsonStr) {
		TypeFactory factory = mapper.getTypeFactory();
		
		Map<String, Object> result = null;
		try {
			result = mapper.readValue(jsonStr, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return result;
	}
	
	public static List < GenericValue > getPartyRoles(Delegator delegator, String partyId, String parentRoleType) {
        List < GenericValue > results = new ArrayList < GenericValue > ();
        try {
            if (UtilValidate.isNotEmpty(partyId)) {

                List < GenericValue > partyRoleList = EntityQuery.use(delegator).from("PartyRole").where("partyId", partyId).queryList();
                for (GenericValue partyRole: partyRoleList) {
                    String roleTypeId = partyRole.getString("roleTypeId");
                    List < EntityCondition > conditions = new ArrayList < EntityCondition > ();
                    conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
                    if (UtilValidate.isNotEmpty(parentRoleType))
                        conditions.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, parentRoleType));

                    EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                    GenericValue roleType = EntityQuery.use(delegator).select("roleTypeId").from("RoleType").where(condition).orderBy("-lastUpdatedTxStamp").queryFirst();
                    if (UtilValidate.isNotEmpty(roleType)) {
                        results.add(partyRole);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
	
	public static Map<String, Object> getUserRoleGroup(Delegator delegator, String userLoginPartyId){
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		List<String> securityGroups = new LinkedList<>();
        	List<String> roles = new LinkedList<>();
    		String menuParentRoleType = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "MENU_PARENT_ROLE");
        	List<GenericValue> partyRoles = getPartyRoles(delegator, userLoginPartyId, menuParentRoleType);
        	if(UtilValidate.isNotEmpty(partyRoles))
        		roles = EntityUtil.getFieldListFromEntityList(partyRoles, "roleTypeId", true);
        	EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
        			EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roles),
        			EntityCondition.makeCondition(EntityOperator.OR,
        					EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, "N"),
        					EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, null)
        					)
        			);
        	List<GenericValue> securityGroupAssoc = EntityQuery.use(delegator).from("SecurityGroupRoleTypeAssoc").where(condition).queryList();
        	if(UtilValidate.isNotEmpty(securityGroupAssoc))
        		securityGroups = EntityUtil.getFieldListFromEntityList(securityGroupAssoc, "groupId", true);
        	
        	Debug.logInfo("securityGroups : "+securityGroups, MODULE);
        	
        	result.put("userLoginSecurityGroups", securityGroups);
        	result.put("userLoginRoles", roles);
        	
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }
	
	public static List<String> stringToList(String str, String separator){
		List<String> list = new LinkedList<String>();
		try {
			separator = UtilValidate.isNotEmpty(separator) ? separator : ", ";
			list = Stream.of(str.trim().split(separator)).map(String::trim).distinct().collect(Collectors.toList());
		} catch (Exception e) {
		}
		return list;
	}
	
	public static String listToString(List<String> list) {
		return listToString(list, null);
	}
	public static String listToString(List<String> list, String separator) {
		String value = "";
		try {
			separator = UtilValidate.isNotEmpty(separator) ? separator : ", ";
			if(UtilValidate.isNotEmpty(list))
				value = list.stream().filter(e-> e!=null && !e.isEmpty()).map(String::trim).distinct().collect(Collectors.joining(separator));
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(),MODULE);
		}
		return value;
	}
	
	public static String getGlobalDateFormat(Delegator delegator) {
		try {
			String globalDateFormat = "MM/dd/yyyy";
			GenericValue defaultGlobalDateFormat = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId","DATE_FORMAT").queryFirst();
				
			if (UtilValidate.isNotEmpty(defaultGlobalDateFormat)) {
				globalDateFormat = defaultGlobalDateFormat.getString("value").trim();
			}
			return globalDateFormat;
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getGlobalDateTimeFormat(Delegator delegator) {
		try {
			String globalFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalTimeFormat = DataUtil.getGlobalValue(delegator, "GLOBAL_TIME_FORMAT", "HH:mm");
			globalFormat = globalFormat + (UtilValidate.isNotEmpty(globalTimeFormat) ?  " "+ globalTimeFormat :"");
			return globalFormat;
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String javaPropToSqlProp(String prop) {
		if (UtilValidate.isNotEmpty(prop)) {
			for (int i = 0; i < prop.length(); i++) {
				if (Character.isUpperCase(prop.charAt(i))) {
					String additionalChar = (i == 0) ? "" : "_";
					prop = prop.replace("" + prop.charAt(i), additionalChar + ("" + prop.charAt(i)).toLowerCase());
				}
			}
		}
		return prop;
	}
	
	public static String sqlPropToJavaProp(String prop) {
		if (UtilValidate.isNotEmpty(prop)) {
			// String prop = "hp_due_wthin_1_yr_amt";
			prop = prop.toLowerCase();
			prop = prop.replace("_1_", "1");
			prop = prop.replace("_2_", "2");
			String convertedString = "";
			for (int i = 0; i < prop.length(); i++) {
				if (prop.charAt(i) == '_') {
					convertedString += ("" + prop.charAt(++i)).toUpperCase();
				} else {
					convertedString += prop.charAt(i);
				}
			}
			return convertedString;
		}
		return prop;
	}
	
}
