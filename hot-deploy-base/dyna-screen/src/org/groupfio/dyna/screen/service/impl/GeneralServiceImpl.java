/**
 * 
 */
package org.groupfio.dyna.screen.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fio.homeapps.constants.GlobalConstants.ModeOfAction;
import org.fio.homeapps.util.DataHelper;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilPermission;
import org.groupfio.dyna.screen.DynaScreenConstants.LayoutType;
import org.groupfio.dyna.screen.DynaScreenConstants.LookupType;
import org.groupfio.dyna.screen.filter.FilterDynaField;
import org.groupfio.dyna.screen.filter.FilterDynaFieldData;
import org.groupfio.dyna.screen.util.DynaScreenUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
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

/**
 * @author Sharif
 *
 */
public class GeneralServiceImpl {

	private static final String MODULE = GeneralServiceImpl.class.getName();
    
    public static Map getDynaScreenRenderDetail(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Security security = dctx.getSecurity();
    	
    	String dynaConfigId = (String) context.get("dynaConfigId");
    	Map requestContext = (Map) context.get("requestContext");
    	
    	Map inputContext = null;
    	if (UtilValidate.isNotEmpty(requestContext.get("inputContext"))) {
    		inputContext = (Map) requestContext.get("inputContext");
    	}
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		String layoutType = ParamUtil.getString(requestContext, "layoutType");
    		String securityGroupId = ParamUtil.getString(requestContext, "securityGroupId");
    		String modeOfAction = ParamUtil.getString(requestContext, "modeOfAction");
    		String isCheckSecurityGroup = ParamUtil.getString(requestContext, "isCheckSecurityGroup");
    		String isConfigScreen = ParamUtil.getString(requestContext, "isConfigScreen");
    		
    		List conditionList = FastList.newInstance();
    		
    		conditionList.add(EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, dynaConfigId));
    		
    		if (UtilValidate.isEmpty(isConfigScreen) || isConfigScreen.equals("N")) {
    			conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
    					EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, null),
    					EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, "N")
    					));
    		}
    				
    		conditionList.add(EntityUtil.getFilterByDateExpr());
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue dynaScreenConfig = EntityUtil.getFirst( delegator.findList("DynaScreenConfig", mainConditons, null, null, null, false) );
			
    		if (UtilValidate.isEmpty(dynaScreenConfig)) {
    			result.putAll(ServiceUtil.returnSuccess("Dyna screen config not exists!"));
    			return result;
    		}
    		
    		if (UtilValidate.isNotEmpty(isCheckSecurityGroup) && isCheckSecurityGroup.equals("Y")) {
    			securityGroupId = UtilValidate.isNotEmpty(securityGroupId) ? securityGroupId : dynaScreenConfig.getString("securityGroupId");
        		if (UtilValidate.isNotEmpty(securityGroupId) && !UtilPermission.hasPermission(delegator, userLogin, securityGroupId)) {
        			result.putAll(ServiceUtil.returnSuccess("Access Denied!"));
        			result.put("responseCode", "E3010");
        			return result;
        		}
    		}
    		
    		Map<String, Object> screenConfig = new LinkedHashMap<String, Object>();
    		screenConfig.putAll(dynaScreenConfig.getAllFields());
    		
    		layoutType = UtilValidate.isNotEmpty(layoutType) ? layoutType : dynaScreenConfig.getString("layoutType");
    		
    		List<Map<String, Object>> screenConfigFieldList = new ArrayList<Map<String, Object>>();
    		
    		// retrieve screen fields 
    		
    		conditionList = FastList.newInstance();
    		
    		conditionList.add(EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, dynaConfigId));
    		
    		if (UtilValidate.isNotEmpty(modeOfAction)) {
    			if (modeOfAction.equals(ModeOfAction.CREATE)) {
    				conditionList.add(EntityCondition.makeCondition("isCreate", EntityOperator.EQUALS, "Y"));
    			} else if (modeOfAction.equals(ModeOfAction.UPDATE)) {
    				conditionList.add(EntityCondition.makeCondition("isEdit", EntityOperator.EQUALS, "Y"));
    			} else if (modeOfAction.equals(ModeOfAction.VIEW)) {
    				conditionList.add(EntityCondition.makeCondition("isView", EntityOperator.EQUALS, "Y"));
    			}
    		}
    		
    		mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> dynaScreenConfigFields = delegator.findList("DynaScreenConfigField", mainConditons, null, UtilMisc.toList("sequenceNum"), null, false);
			int fieldCount = 0;
    		if (UtilValidate.isNotEmpty(dynaScreenConfigFields)) {
    			
    			if (UtilValidate.isEmpty(isConfigScreen) || isConfigScreen.equals("N")) {
    				FilterDynaField filterField = new FilterDynaField(userLogin, delegator);
        			dynaScreenConfigFields = filterField.filter(dynaScreenConfigFields);
    			}
    			
    			for (GenericValue dynaScreenConfigField : dynaScreenConfigFields) {
    				String dynaFieldId = dynaScreenConfigField.getString("dynaFieldId");
    				String fieldType = dynaScreenConfigField.getString("fieldType");
    				Map<String, Object> screenConfigField = new LinkedHashMap<String, Object>();
    				screenConfigField.putAll(dynaScreenConfigField.getAllFields());
    				
    				Object inputValue = null;
    	    		if (UtilValidate.isNotEmpty(inputContext)) {
    	    			inputValue = inputContext.get(dynaScreenConfigField.getString("dynaFieldId"));
    				}
    				
    				Map<String, Object> fieldDataList = new LinkedHashMap<String, Object>();
    				if (UtilValidate.isNotEmpty(dynaScreenConfigField.getString("lookupTypeId")) && dynaScreenConfigField.getString("lookupTypeId").equals(LookupType.STATIC_DATA)) {
    					List<GenericValue> dataList = null;
    					if (UtilValidate.isNotEmpty(modeOfAction) && modeOfAction.equals(ModeOfAction.VIEW) && UtilValidate.isEmpty(inputValue)) {
    						dataList = new ArrayList<>();
    					} else {
    						conditionList = FastList.newInstance();
    						conditionList.add(EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, dynaConfigId));
    						conditionList.add(EntityCondition.makeCondition("dynaFieldId", EntityOperator.EQUALS, dynaFieldId));
    						if (UtilValidate.isNotEmpty(modeOfAction) && modeOfAction.equals(ModeOfAction.VIEW) && UtilValidate.isNotEmpty(inputValue)) {
    							conditionList.add(EntityCondition.makeCondition("dataValue", EntityOperator.EQUALS, inputValue));
    						}
    						mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    						
    						Set<String> fieldsToSelect = new LinkedHashSet<String>();
    						fieldsToSelect.add("dataValue");
    						fieldsToSelect.add("dataName");
    						dataList = EntityQuery.use(delegator).select(fieldsToSelect).from("DynaScreenConfigFieldData").where(mainConditons).orderBy("sequenceNum").queryList();
    						
    						///////////////////////////////////////////////////////
    						/*dataList = delegator.findByAnd("DynaScreenConfigFieldData", UtilMisc.toMap("dynaConfigId", dynaConfigId, "dynaFieldId", dynaFieldId), UtilMisc.toList("sequenceNum"), false);
        		    		FilterDynaFieldData filterData = new FilterDynaFieldData(userLogin, delegator);
        		    		dataList = filterData.filter(dataList);*/
    					}
    		    		fieldDataList = DataHelper.getDropDownOptions(dataList, "dataValue", "dataName");
    				} else if (UtilValidate.isNotEmpty(dynaScreenConfigField.getString("lookupTypeId")) 
    						&& dynaScreenConfigField.getString("lookupTypeId").equals(LookupType.DYNAMIC_DATA)
    						&& UtilValidate.isNotEmpty(dynaScreenConfigField.getString("lookupFieldService"))
    						&& UtilValidate.isNotEmpty(dynaScreenConfigField.getString("lookupFieldFilter"))
    						) {
    					
    					callCtxt = FastMap.newInstance();
    					
    					Map<String, Object> filterData = dynaScreenConfigField.getAllFields();
    					filterData.put("modeOfAction", modeOfAction);
    					filterData.put("inputValue", inputValue);
    					
    					callCtxt.put("filterData", filterData);
    					callCtxt.put("userLogin", userLogin);
    	    			
    	    			callResult = dispatcher.runSync(dynaScreenConfigField.getString("lookupFieldService"), callCtxt);
    		    		
    	    			if (ServiceUtil.isSuccess(callResult)) {
    	    				fieldDataList = (Map<String, Object>) callResult.get("filterResult");
    	    			}
    					
    				}
    				
    				screenConfigField.put("fieldDataList", fieldDataList);
    				
    				List<GenericValue> fieldAttrList = delegator.findByAnd("DynaScreenConfigFieldAttribute", UtilMisc.toMap("dynaConfigId", dynaConfigId, "dynaFieldId", dynaFieldId), null, false);
    				//screenConfigField.put("fieldAttrList", DataHelper.getDropDownOptions(fieldAttrList, "attrName", "attrValue"));
    				Map<String, Object> fieldAttrs = new HashMap<>();
    				fieldAttrs = fieldAttrList.stream().collect(Collectors.toMap(x -> (String) x.get("attrName"),
    						x -> UtilValidate.isNotEmpty((String) x.get("attrValue")) ? (String) x.get("attrValue") : "",
    						(attr1, attr2) -> {
    							return attr2;
    						}));
    				screenConfigField.put("fieldAttrList", fieldAttrs);
    				
    				screenConfigFieldList.add(screenConfigField);
    				
    				if (UtilValidate.isNotEmpty(fieldType) && !fieldType.equals("HIDDEN")) {
    					fieldCount++;
    				}
    			}
    		}
    		
    		// Screen computation [start]
    		
    		screenConfig.put("screenComputation", DynaScreenUtil.screenComputation(delegator, fieldCount, layoutType));
    		
    		// Screen computation [end]
    		
    		result.put("screenConfig", screenConfig);
    		result.put("screenConfigFieldList", screenConfigFieldList);
    		result.put("responseCode", "S200");
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	result.putAll(ServiceUtil.returnSuccess("Successfully retrieve Dyna Screen Render Detail.."));
    	return result;
    }
    
}
