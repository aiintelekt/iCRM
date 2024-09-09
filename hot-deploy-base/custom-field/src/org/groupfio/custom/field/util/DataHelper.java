/**
 * 
 */
package org.groupfio.custom.field.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;

/**
 * @author Sharif
 *
 */
public class DataHelper {
	
	private static String MODULE = DataHelper.class.getName();

	public static Map<String, Object> getLockboxStores(Delegator delegator) {
		
		Map<String, Object> lockboxStores = new HashMap<String, Object>();
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
	
	public static Map getDropDownOptions(List<GenericValue> entityList, String keyField, String desField){
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		for (GenericValue entity : entityList) {
			options.put(entity.getString(keyField), entity.getString(desField));
		}
		return options;
	}
	
	public static Map getFieldLengthOptions(){
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		
		options.put("1", "1");
		options.put("5", "5");
		options.put("20", "20");
		options.put("60", "60");
		options.put("-1", "unlimited");
		
		return options;
	}
	
	public static Map getSupplierList(Delegator delegator){
		
		Map<String, Object> suppliers = new HashMap<String, Object>();
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
		
		Map<String, Object> services = new HashMap<String, Object>();
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
	
}
