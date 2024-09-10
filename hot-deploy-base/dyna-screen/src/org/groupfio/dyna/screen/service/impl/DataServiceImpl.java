/**
 * 
 */
package org.groupfio.dyna.screen.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fio.homeapps.constants.GlobalConstants.ModeOfAction;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.dyna.screen.util.QueryUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;

/**
 * @author Sharif
 *
 */
public class DataServiceImpl {

	private static final String MODULE = DataServiceImpl.class.getName();
    
    public static Map getDynamicData(DispatchContext dctx, Map context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Security security = dctx.getSecurity();
    	
    	Map filterData = (Map) context.get("filterData");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map filterResult = (Map) context.get("filterResult");
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		String dynaConfigId = ParamUtil.getString(filterData, "dynaConfigId");
    		String dynaFieldId = ParamUtil.getString(filterData, "dynaFieldId");
    		String lookupFieldFilter = ParamUtil.getString(filterData, "lookupFieldFilter");
    		
    		String modeOfAction = (String) filterData.get("modeOfAction");
    		Object inputValue = filterData.get("inputValue");
    		
    		if (UtilValidate.isEmpty(lookupFieldFilter)) {
    			result.putAll(ServiceUtil.returnError("Lookup Field Filter not exists!"));
    			return result;
    		}
    		
    		JSONObject filterObj = JSONObject.fromObject(lookupFieldFilter);
			Map<String, Object> filter = ParamUtil.jsonToMap(filterObj);
			
			String entityName = ParamUtil.getString(filter, "entity_name");
			String nameField = ParamUtil.getString(filter, "name_field");
			String valueField = ParamUtil.getString(filter, "value_field");
			String orderBy = ParamUtil.getString(filter, "order_by");
			String dateFilter = ParamUtil.getString(filter, "date_filter");
			
			if (UtilValidate.isNotEmpty(entityName) && UtilValidate.isNotEmpty(nameField) && UtilValidate.isNotEmpty(valueField)) {
				
				List<GenericValue> dataList = null;
				if (UtilValidate.isNotEmpty(modeOfAction) && modeOfAction.equals(ModeOfAction.VIEW) && UtilValidate.isEmpty(inputValue)) {
					dataList = new ArrayList<>();
				} else {
					List conditionList = FastList.newInstance();
					
					if (UtilValidate.isNotEmpty(modeOfAction) && modeOfAction.equals(ModeOfAction.VIEW) && UtilValidate.isNotEmpty(inputValue)) {
						conditionList.add(EntityCondition.makeCondition(valueField, EntityOperator.EQUALS, inputValue));
					}
					
					if (UtilValidate.isNotEmpty(filter.get("filter_value"))) {
						QueryUtil.makeCondition(conditionList, filter.get("filter_value"));
					}
					if (UtilValidate.isNotEmpty(dateFilter)) {
						if (dateFilter.split(",").length == 2) {
							String fromDateName = dateFilter.split(",")[0];
							String thruDateName = dateFilter.split(",")[1];
							conditionList.add(EntityUtil.getFilterByDateExpr(fromDateName, thruDateName));
						}
					}
					
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					
					Set<String> fieldsToSelect = new LinkedHashSet<String>();
					fieldsToSelect.add(valueField);
					fieldsToSelect.add(nameField);
					
					List<String> orderByList = null;
					if (UtilValidate.isNotEmpty(orderBy)) {
						orderByList = StringUtil.split(orderBy, ",");
					}
					
					dataList = EntityQuery.use(delegator).select(fieldsToSelect).from(entityName).where(mainConditons).orderBy(orderByList).queryList();
				}
				
				//filterResult = org.fio.homeapps.util.DataHelper.getDropDownOptions(dataList, valueField, nameField);
				filterResult = org.fio.homeapps.util.DataHelper.getDropDownOptions(dataList, valueField, nameField, 100, true);
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	result.put("filterResult", filterResult);
    	result.putAll(ServiceUtil.returnSuccess("Successfully retrieve Dynamic data.."));
    	return result;
    }
    
}
