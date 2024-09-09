/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.groupfio.dyna.screen.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.EnumUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webtools.labelmanager.LabelInfo;
import org.ofbiz.webtools.labelmanager.LabelManagerFactory;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Sharif Ul Islam
 * 
 */
public final class AjaxEvents {

    private AjaxEvents() { }

    private static final String MODULE = AjaxEvents.class.getName();

    public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
        return doJSONResponse(response, jsonObject.toString());
    }

    public static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
        return doJSONResponse(response, JSONArray.fromObject(collection).toString());
    }

    public static String doJSONResponse(HttpServletResponse response, Map map) {
        return doJSONResponse(response, JSONObject.fromObject(map));
    }

    public static String doJSONResponse(HttpServletResponse response, String jsonString) {
        String result = "success";

        response.setContentType("application/x-json");
        try {
            response.setContentLength(jsonString.getBytes("UTF-8").length);
        } catch (UnsupportedEncodingException e) {
            Debug.logWarning("Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(), MODULE);
            response.setContentLength(jsonString.length());
        }

        Writer out;
        try {
            out = response.getWriter();
            out.write(jsonString);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, "Failed to get response writer", MODULE);
            result = "error";
        }
        return result;
    }
    
    public static GenericValue getUserLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (GenericValue) session.getAttribute("userLogin");
    }

    /*************************************************************************/
    /**                                                                     **/
    /**                      Common JSON Requests                           **/
    /**                                                                     **/
    /*************************************************************************/
    
    public static String searchDynaScreenConfigurations(HttpServletRequest request, HttpServletResponse response) {
    	
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		
		String dynaConfigId = request.getParameter("dynaConfigId");
		String screenDisplayName = request.getParameter("screenDisplayName");
		String componentMountPoint = request.getParameter("componentMountPoint");
		String layoutType = request.getParameter("layoutType");
		String isPrimary = request.getParameter("isPrimary");
		
		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {
			
			List conditionList = FastList.newInstance();
			
			//conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
			
			/*conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("profileConfigurationId", EntityOperator.EQUALS, profileConfigurationId),
					EntityCondition.makeCondition("profileCode", EntityOperator.EQUALS, profileConfigurationId)
                    ));*/
			
			if (UtilValidate.isNotEmpty(dynaConfigId)) {
				conditionList.add(EntityCondition.makeCondition("dynaConfigId", EntityOperator.LIKE, "%" + dynaConfigId + "%"));
			}
			if (UtilValidate.isNotEmpty(screenDisplayName)) {
				conditionList.add(EntityCondition.makeCondition("screenDisplayName", EntityOperator.LIKE, "%" + screenDisplayName + "%"));
			}
			if (UtilValidate.isNotEmpty(componentMountPoint)) {
				conditionList.add(EntityCondition.makeCondition("componentMountPoint", EntityOperator.EQUALS, componentMountPoint));
			}
			if (UtilValidate.isNotEmpty(layoutType)) {
				conditionList.add(EntityCondition.makeCondition("layoutType", EntityOperator.EQUALS, layoutType));
			}
			if (UtilValidate.isNotEmpty(isPrimary)) {
				conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, isPrimary));
			}
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> configList = delegator.findList("DynaScreenConfig", mainConditons, null, UtilMisc.toList("-createdStamp"), null, false);
			if (UtilValidate.isNotEmpty(configList)) {
				
				for (GenericValue entry : configList) {
					Map<String, Object> data = new HashMap<String, Object>();
					
					data.put("dynaConfigId", entry.getString("dynaConfigId"));
					layoutType = EnumUtil.getEnumDescription(delegator, entry.getString("layoutType"), "LAYOUT_TYPE");
					isPrimary = EnumUtil.getEnumDescription(delegator, entry.getString("isPrimary"), "INDICATOR_TYPE");
					data.put("layoutType", UtilValidate.isNotEmpty(layoutType)?layoutType:"");
					data.put("screenDisplayName", entry.getString("screenDisplayName"));
					data.put("securityGroupId", entry.getString("securityGroupId"));
					data.put("isPrimary", UtilValidate.isNotEmpty(isPrimary)?isPrimary:"");
					componentMountPoint = entry.getString("componentMountPoint");
					if(UtilValidate.isNotEmpty(componentMountPoint)) {
						List<GenericValue> ofbizComponentAccessGV = EntityQuery.use(delegator).select("uiLabels").from("OfbizComponentAccess").where("componentName",componentMountPoint).queryList();
						if(UtilValidate.isNotEmpty(ofbizComponentAccessGV)) {
							for(GenericValue ofbizComponent : ofbizComponentAccessGV) {
								data.put("componentMountPoint", UtilValidate.isNotEmpty(ofbizComponent.getString("uiLabels"))?ofbizComponent.getString("uiLabels"):"");
							}
						}
					}
					data.put("fromDate", UtilValidate.isNotEmpty(entry.get("fromDate")) ? UtilDateTime.timeStampToString(entry.getTimestamp("fromDate"), "dd/MM/yyyy", TimeZone.getDefault(), null) : "");
					data.put("thruDate", UtilValidate.isNotEmpty(entry.get("thruDate")) ? UtilDateTime.timeStampToString(entry.getTimestamp("thruDate"), "dd/MM/yyyy", TimeZone.getDefault(), null) : "");
					
					dataList.add(data);
				}
				
			}
			
			result.put("data", dataList);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", dataList);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
    }
    
    public static String searchUiLables(HttpServletRequest request, HttpServletResponse response) {
    	
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String labelComponentName = request.getParameter("labelComponentName");
		String labelFileName = request.getParameter("labelFileName");
		
		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {
			
			if (UtilValidate.isNotEmpty(labelComponentName)) {
				
				LabelManagerFactory factory = LabelManagerFactory.getInstance();
				
				factory.findMatchingLabels(labelComponentName, labelFileName, null, null);
				
				Set<String> labelsList = factory.getLabelsList();
				Map<String, LabelInfo> labels = factory.getLabels();
				
				for (String labelList : labelsList) {
					LabelInfo label = labels.get(labelList);
					
					Map<String, Object> data = new HashMap<String, Object>();
					
					data.put("labelKey", label.getLabelKey());
					data.put("labelValue", UtilValidate.isNotEmpty(label.getLabelValue("en")) ? label.getLabelValue("en").getLabelValue() : "");
					
					dataList.add(data);
				}
				
			}
			
			result.put("data", dataList);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", dataList);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
    
    public static String getDynaScreenRenderDetail(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String dynaConfigId = request.getParameter("dynaConfigId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			if (UtilValidate.isNotEmpty(dynaConfigId)) {
				
				Map<String, Object> requestContext = FastMap.newInstance();
				requestContext.put("isConfigScreen", "Y");
				
    			callCtxt = FastMap.newInstance();
    			
    			callCtxt.put("dynaConfigId", dynaConfigId);
    			callCtxt.put("requestContext", requestContext);
    			callCtxt.put("userLogin", userLogin);
    			
    			callResult = dispatcher.runSync("dynaScreen.getDynaScreenRenderDetail", callCtxt);
    			if (ServiceUtil.isSuccess(callResult)) {
    				result.put("screenConfig", callResult.get("screenConfig"));
    				result.put("screenConfigFieldList", callResult.get("screenConfigFieldList"));
    			}
    		}
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
    
    public static String getDynaScreenFieldList(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String serviceName = request.getParameter("serviceName");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			if (UtilValidate.isNotEmpty(serviceName)) {
				
				List<Map<String, Object>> screenConfigFieldList = new ArrayList<Map<String, Object>>();
				
				DispatchContext curDispatchContext = dispatcher.getDispatchContext();
				ModelService curServiceModel = curDispatchContext.getModelService(serviceName);
				
				long sequenceNum = 1;
				Set<String> ignoreParams = new HashSet<String>(Arrays.asList("locale", "login.password", "login.username", "timeZone", "userLogin", "isPerformUserAudit", "oldContextMap", "userAuditRequestId", "errorMessage", "errorMessageList", "responseCode", "responseMessage", "successMessage", "successMessageList"));
				Set<String> inParams = curServiceModel.getInParamNames();
				Set<String> outParams = curServiceModel.getOutParamNames();
				Set<String> params = new LinkedHashSet<String>();
				
				params.addAll(inParams);
				params.addAll(outParams);
				
		        for (String paramName : params) {
		        	
		        	if (ignoreParams.contains(paramName)) {
		        		continue;
		        	}
		        	
		        	ModelParam curParam = curServiceModel.getParam(paramName);
		        	
		        	System.out.println("paramName: "+paramName+", type: "+curParam.type);
		        	if (curParam.type.contains("Map") || curParam.type.contains("List")) {
		        		continue;
		        	}
		        	
		        	Map<String, Object> screenConfigField = new LinkedHashMap<String, Object>();
		        	
		        	screenConfigField.put("dynaFieldId", curParam.name);
		        	screenConfigField.put("fieldName", UtilValidate.isNotEmpty(curParam.fieldName) ? curParam.fieldName : curParam.name);
		        	screenConfigField.put("fieldType", "TEXT");
		        	screenConfigField.put("isRequired", curParam.optional ? "N" : "Y");
		        	screenConfigField.put("isCreate", "Y");
		        	screenConfigField.put("isView", "Y");
		        	screenConfigField.put("isEdit", "Y");
		        	screenConfigField.put("isDisabled", "N");
		        	screenConfigField.put("sequenceNum", sequenceNum++);
		        	screenConfigField.put("defaultValue", curParam.getDefaultValue());
		        	screenConfigField.put("fieldDataPattern", "");
		        	screenConfigField.put("maxLength", "");
		        	screenConfigField.put("lookupTypeId", "");
		        	screenConfigField.put("lookupFieldService", "");
		        	screenConfigField.put("lookupFieldFilter", "");
		        	screenConfigField.put("fieldService", "");
		        	screenConfigField.put("pickerWindowId", "");
		        	screenConfigField.put("roleTypeId", "");
		        	
		        	screenConfigFieldList.add(screenConfigField);
		        }
		        
		        result.put("screenConfigFieldList", screenConfigFieldList);
		        
    		}
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
    
    public static String getDynamicData(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String lookupFieldFilter = request.getParameter("filterData[lookupFieldFilter]");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			Map<String, Object> fieldDataList = new LinkedHashMap<String, Object>();
			//fieldDataList.put("", "Please Select");
			
			if (UtilValidate.isNotEmpty(lookupFieldFilter)) {
				
				Map<String, Object> filterData = new HashMap<String, Object>();
				filterData.put("lookupFieldFilter", lookupFieldFilter);
				
    			callCtxt = FastMap.newInstance();
    			
    			callCtxt.put("filterData", filterData);
				callCtxt.put("userLogin", userLogin);
    			
    			callResult = dispatcher.runSync("dynaScreen.getDynamicData", callCtxt);
    			if (ServiceUtil.isSuccess(callResult)) {
    				fieldDataList.putAll((Map<String, Object>) callResult.get("filterResult"));
    			}
    		}
			
			result.put("fieldDataList", fieldDataList);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
    
    public static String dynaScreenStep2Create(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String dynaConfigId = request.getParameter("dynaConfigId");
		String instanceId = request.getParameter("instanceId");
		
		String componentMountPoint = request.getParameter("componentMountPoint");
		String screenDisplayName = request.getParameter("screenDisplayName");
		String layoutType = request.getParameter("layoutType");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		String isPrimary = request.getParameter("isPrimary");
		String isDisabled = request.getParameter("isDisabledDyna");
		String isFullscreen = request.getParameter("isFullscreen");
		String labelColSize = request.getParameter("labelColSize");
		String inputColSize = request.getParameter("inputColSize");
		String securityGroupId = request.getParameter("securityGroupId");
		String defaultMessage = request.getParameter("defaultMessage");
		
		String dynaFieldIdList[] = request.getParameterValues("dynaFieldId");
		String fieldNameList[] = request.getParameterValues("fieldName");
		String fieldTypeList[] = request.getParameterValues("fieldType");
		String isRequiredList[] = request.getParameterValues("isRequired");
		String isCreateList[] = request.getParameterValues("isCreate");
		String isViewList[] = request.getParameterValues("isView");
		String isEditList[] = request.getParameterValues("isEdit");
		String isDisabledList[] = request.getParameterValues("isDisabled");
		String sequenceNumList[] = request.getParameterValues("sequenceNum");
		String defaultValueList[] = request.getParameterValues("defaultValue");
		String fieldDataPatternList[] = request.getParameterValues("fieldDataPattern");
		String maxLengthList[] = request.getParameterValues("maxLength");
		String lookupTypeIdList[] = request.getParameterValues("lookupTypeId");
		String lookupFieldServiceList[] = request.getParameterValues("lookupFieldService");
		String lookupFieldFilterList[] = request.getParameterValues("lookupFieldFilter");
		//String lookupFieldServiceUrlList[] = request.getParameterValues("lookupFieldServiceUrl");
		String fieldServiceList[] = request.getParameterValues("fieldService");
		String pickerWindowIdList[] = request.getParameterValues("pickerWindowId");
		String roleTypeIdList[] = request.getParameterValues("roleTypeId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			if (UtilValidate.isNotEmpty(instanceId)) {
				GenericValue dynaScreenConfig = EntityUtil.getFirst( delegator.findByAnd("DynaScreenConfig", UtilMisc.toMap("dynaConfigId", instanceId), null, false) );
	    		if (UtilValidate.isNotEmpty(dynaScreenConfig)) {
	    			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
	    			result.put(GlobalConstants.RESPONSE_MESSAGE, "Dyna Screen Configuration with instanceId# "+instanceId+", already exists!");
	    		}
			}
			
			if (UtilValidate.isEmpty(result.get(GlobalConstants.RESPONSE_CODE))) {
				
				GenericValue screenConfig = delegator.makeValue("DynaScreenConfig");
				
				dynaConfigId = UtilValidate.isNotEmpty(instanceId) ? instanceId : delegator.getNextSeqId("DynaScreenConfig");
				result.put("dynaConfigId", dynaConfigId);
				
				screenConfig.put("dynaConfigId", dynaConfigId);
				screenConfig.put("componentMountPoint", componentMountPoint);
				screenConfig.put("screenDisplayName", screenDisplayName);
				screenConfig.put("layoutType", layoutType);
				screenConfig.put("fromDate", UtilValidate.isNotEmpty(fromDate) ? UtilDateTime.stringToTimeStamp(fromDate, "dd/MM/yyyy", net.fortuna.ical4j.model.TimeZone.getDefault(), Locale.getDefault()) : null);
				screenConfig.put("thruDate", UtilValidate.isNotEmpty(thruDate) ? UtilDateTime.stringToTimeStamp(thruDate, "dd/MM/yyyy", net.fortuna.ical4j.model.TimeZone.getDefault(), Locale.getDefault()) : null);
				screenConfig.put("isPrimary", isPrimary);
				screenConfig.put("createdBy", userLogin.getString("userLoginId"));
				screenConfig.put("isDisabled", isDisabled);
				screenConfig.put("isFullscreen", isFullscreen);
				screenConfig.put("labelColSize", labelColSize);
				screenConfig.put("inputColSize", inputColSize);
				screenConfig.put("securityGroupId", securityGroupId);
				screenConfig.put("defaultMessage", defaultMessage);
				
				screenConfig.create();
				
				// store screen fields [start]
				
				if (UtilValidate.isNotEmpty(dynaFieldIdList)) {
				
					for (int i = 0; i < dynaFieldIdList.length; i++) {
						
						String dynaFieldId = dynaFieldIdList[i];
						String fieldName = fieldNameList[i];
						String fieldType = fieldTypeList[i];
						String isRequired = isRequiredList[i];
						String isCreate = isCreateList[i];
						String isView = isViewList[i];
						String isEdit = isEditList[i];
						isDisabled = isDisabledList[i];
						String sequenceNum = sequenceNumList[i];
						String defaultValue = defaultValueList[i];
						String fieldDataPattern = fieldDataPatternList[i];
						String maxLength = maxLengthList[i];
						String lookupTypeId = lookupTypeIdList[i];
						String lookupFieldService = lookupFieldServiceList[i];
						String lookupFieldFilter = lookupFieldFilterList[i];
						//String lookupFieldServiceUrl = lookupFieldServiceUrlList[i];
						String fieldService = fieldServiceList[i];
						String pickerWindowId = pickerWindowIdList[i];
						String roleTypeId = roleTypeIdList[i];
						
						dynaFieldId = UtilValidate.isEmpty( dynaFieldId ) ? null : dynaFieldId;
						fieldName = UtilValidate.isEmpty( fieldName ) ? null : fieldName;
						fieldType = UtilValidate.isEmpty( fieldType ) ? null : fieldType;
						isRequired = UtilValidate.isEmpty( isRequired ) ? null : isRequired;
						isCreate = UtilValidate.isEmpty( isCreate ) ? null : isCreate;
						isView = UtilValidate.isEmpty( isView ) ? null : isView;
						isEdit = UtilValidate.isEmpty( isEdit ) ? null : isEdit;
						isDisabled = UtilValidate.isEmpty( isDisabled ) ? null : isDisabled;
						sequenceNum = UtilValidate.isEmpty( sequenceNum ) ? null : sequenceNum;
						defaultValue = UtilValidate.isEmpty( defaultValue ) ? null : defaultValue;
						fieldDataPattern = UtilValidate.isEmpty( fieldDataPattern ) ? null : fieldDataPattern;
						maxLength = UtilValidate.isEmpty( maxLength ) ? null : maxLength;
						lookupTypeId = UtilValidate.isEmpty( lookupTypeId ) ? null : lookupTypeId;
						lookupFieldService = UtilValidate.isEmpty( lookupFieldService ) ? null : lookupFieldService;
						lookupFieldFilter = UtilValidate.isEmpty( lookupFieldFilter ) ? null : lookupFieldFilter;
						//lookupFieldServiceUrl = UtilValidate.isEmpty( lookupFieldServiceUrl ) ? null : lookupFieldServiceUrl;
						fieldService = UtilValidate.isEmpty( fieldService ) ? null : fieldService;
						pickerWindowId = UtilValidate.isEmpty( pickerWindowId ) ? null : pickerWindowId;
						roleTypeId = UtilValidate.isEmpty( roleTypeId ) ? null : roleTypeId;
						
						GenericValue screenConfigField = delegator.makeValue("DynaScreenConfigField");
						
						screenConfigField.put("dynaConfigId", dynaConfigId);
						screenConfigField.put("dynaFieldId", dynaFieldId);
						screenConfigField.put("fieldName", fieldName);
						screenConfigField.put("fieldType", fieldType);
						screenConfigField.put("isRequired", isRequired);
						screenConfigField.put("isCreate", isCreate);
						screenConfigField.put("isView", isView);
						screenConfigField.put("isEdit", isEdit);
						screenConfigField.put("isDisabled", isDisabled);
						screenConfigField.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNum) ? Integer.parseInt(sequenceNum) : null);
						screenConfigField.put("defaultValue", defaultValue);
						screenConfigField.put("fieldDataPattern", fieldDataPattern);
						screenConfigField.put("maxLength", UtilValidate.isNotEmpty(maxLength) ? Integer.parseInt(maxLength) : null);
						screenConfigField.put("lookupTypeId", lookupTypeId);
						screenConfigField.put("lookupFieldService", lookupFieldService);
						screenConfigField.put("lookupFieldFilter", lookupFieldFilter);
						//screenConfigField.put("lookupFieldServiceUrl", lookupFieldServiceUrl);
						screenConfigField.put("fieldService", fieldService);
						screenConfigField.put("pickerWindowId", pickerWindowId);
						screenConfigField.put("roleTypeId", roleTypeId);
						
						screenConfigField.create();
						
					}
				
				}
				
				// store screen fields [end]
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully created dynamic screen");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
    
    public static String dynaScreenUpdate(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String dynaConfigId = request.getParameter("dynaConfigId");
		
		String componentMountPoint = request.getParameter("componentMountPoint");
		String screenDisplayName = request.getParameter("screenDisplayName");
		String layoutType = request.getParameter("layoutType");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		String isPrimary = request.getParameter("isPrimary");
		String isDisabled = request.getParameter("isDisabledDyna");
		String isFullscreen = request.getParameter("isFullscreen");
		String labelColSize = request.getParameter("labelColSize");
		String inputColSize = request.getParameter("inputColSize");
		String securityGroupId = request.getParameter("securityGroupId");
		String defaultMessage = request.getParameter("defaultMessage");
		
		String dynaFieldIdList[] = request.getParameterValues("dynaFieldId");
		String fieldNameList[] = request.getParameterValues("fieldName");
		String fieldTypeList[] = request.getParameterValues("fieldType");
		String isRequiredList[] = request.getParameterValues("isRequired");
		String isCreateList[] = request.getParameterValues("isCreate");
		String isViewList[] = request.getParameterValues("isView");
		String isEditList[] = request.getParameterValues("isEdit");
		String isDisabledList[] = request.getParameterValues("isDisabled");
		String sequenceNumList[] = request.getParameterValues("sequenceNum");
		String defaultValueList[] = request.getParameterValues("defaultValue");
		String fieldDataPatternList[] = request.getParameterValues("fieldDataPattern");
		String maxLengthList[] = request.getParameterValues("maxLength");
		String lookupTypeIdList[] = request.getParameterValues("lookupTypeId");
		String lookupFieldServiceList[] = request.getParameterValues("lookupFieldService");
		String lookupFieldFilterList[] = request.getParameterValues("lookupFieldFilter");
		//String lookupFieldServiceUrlList[] = request.getParameterValues("lookupFieldServiceUrl");
		String fieldServiceList[] = request.getParameterValues("fieldService");
		String pickerWindowIdList[] = request.getParameterValues("pickerWindowId");
		String roleTypeIdList[] = request.getParameterValues("roleTypeId");
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("dynaConfigId", dynaConfigId);
		try {
			
			GenericValue screenConfig = EntityUtil.getFirst( delegator.findByAnd("DynaScreenConfig", UtilMisc.toMap("dynaConfigId", dynaConfigId), null, false) );
    		
    		if (UtilValidate.isEmpty(screenConfig)) {
    			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
    			result.put(GlobalConstants.RESPONSE_MESSAGE, "Dyna Screen Configuration not exists!");
    		}
			
			if (UtilValidate.isEmpty(result.get(GlobalConstants.RESPONSE_CODE))) {
				
				screenConfig.put("componentMountPoint", componentMountPoint);
				screenConfig.put("screenDisplayName", screenDisplayName);
				screenConfig.put("layoutType", layoutType);
				screenConfig.put("fromDate", UtilValidate.isNotEmpty(fromDate) ? UtilDateTime.stringToTimeStamp(fromDate, "dd/MM/yyyy", net.fortuna.ical4j.model.TimeZone.getDefault(), Locale.getDefault()) : null);
				screenConfig.put("thruDate", UtilValidate.isNotEmpty(thruDate) ? UtilDateTime.stringToTimeStamp(thruDate, "dd/MM/yyyy", net.fortuna.ical4j.model.TimeZone.getDefault(), Locale.getDefault()) : null);
				screenConfig.put("isPrimary", isPrimary);
				screenConfig.put("lastModifiedBy", userLogin.getString("userLoginId"));
				screenConfig.put("isDisabled", isDisabled);
				screenConfig.put("isFullscreen", isFullscreen);
				screenConfig.put("labelColSize", labelColSize);
				screenConfig.put("inputColSize", inputColSize);
				screenConfig.put("securityGroupId", securityGroupId);
				screenConfig.put("defaultMessage", defaultMessage);
				
				screenConfig.store();
				
				// store screen fields [start]
				
				delegator.removeByAnd("DynaScreenConfigField", UtilMisc.toMap("dynaConfigId", dynaConfigId));
				
				if (UtilValidate.isNotEmpty(dynaFieldIdList)) {
					for (int i = 0; i < dynaFieldIdList.length; i++) {
						
						String dynaFieldId = dynaFieldIdList[i];
						String fieldName = fieldNameList[i];
						String fieldType = fieldTypeList[i];
						String isRequired = isRequiredList[i];
						String isCreate = isCreateList[i];
						String isView = isViewList[i];
						String isEdit = isEditList[i];
						isDisabled = isDisabledList[i];
						String sequenceNum = sequenceNumList[i];
						String defaultValue = defaultValueList[i];
						String fieldDataPattern = fieldDataPatternList[i];
						String maxLength = maxLengthList[i];
						String lookupTypeId = lookupTypeIdList[i];
						String lookupFieldService = lookupFieldServiceList[i];
						String lookupFieldFilter = lookupFieldFilterList[i];
						//String lookupFieldServiceUrl = lookupFieldServiceUrlList[i];
						String fieldService = fieldServiceList[i];
						String pickerWindowId = pickerWindowIdList[i];
						String roleTypeId = roleTypeIdList[i];
						
						dynaFieldId = UtilValidate.isEmpty( dynaFieldId ) ? null : dynaFieldId;
						fieldName = UtilValidate.isEmpty( fieldName ) ? null : fieldName;
						fieldType = UtilValidate.isEmpty( fieldType ) ? null : fieldType;
						isRequired = UtilValidate.isEmpty( isRequired ) ? null : isRequired;
						isCreate = UtilValidate.isEmpty( isCreate ) ? null : isCreate;
						isView = UtilValidate.isEmpty( isView ) ? null : isView;
						isEdit = UtilValidate.isEmpty( isEdit ) ? null : isEdit;
						isDisabled = UtilValidate.isEmpty( isDisabled ) ? null : isDisabled;
						sequenceNum = UtilValidate.isEmpty( sequenceNum ) ? null : sequenceNum;
						defaultValue = UtilValidate.isEmpty( defaultValue ) ? null : defaultValue;
						fieldDataPattern = UtilValidate.isEmpty( fieldDataPattern ) ? null : fieldDataPattern;
						maxLength = UtilValidate.isEmpty( maxLength ) ? null : maxLength;
						lookupTypeId = UtilValidate.isEmpty( lookupTypeId ) ? null : lookupTypeId;
						lookupFieldService = UtilValidate.isEmpty( lookupFieldService ) ? null : lookupFieldService;
						lookupFieldFilter = UtilValidate.isEmpty( lookupFieldFilter ) ? null : lookupFieldFilter;
						//lookupFieldServiceUrl = UtilValidate.isEmpty( lookupFieldServiceUrl ) ? null : lookupFieldServiceUrl;
						fieldService = UtilValidate.isEmpty( fieldService ) ? null : fieldService;
						pickerWindowId = UtilValidate.isEmpty( pickerWindowId ) ? null : pickerWindowId;
						roleTypeId = UtilValidate.isEmpty( roleTypeId ) ? null : roleTypeId;
						
						GenericValue screenConfigField = delegator.makeValue("DynaScreenConfigField");
						
						screenConfigField.put("dynaConfigId", dynaConfigId);
						screenConfigField.put("dynaFieldId", dynaFieldId);
						screenConfigField.put("fieldName", fieldName);
						screenConfigField.put("fieldType", fieldType);
						screenConfigField.put("isRequired", isRequired);
						screenConfigField.put("isCreate", isCreate);
						screenConfigField.put("isView", isView);
						screenConfigField.put("isEdit", isEdit);
						screenConfigField.put("isDisabled", isDisabled);
						screenConfigField.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNum) ? Integer.parseInt(sequenceNum) : null);
						screenConfigField.put("defaultValue", defaultValue);
						screenConfigField.put("fieldDataPattern", fieldDataPattern);
						screenConfigField.put("maxLength", UtilValidate.isNotEmpty(maxLength) ? Integer.parseInt(maxLength) : null);
						screenConfigField.put("lookupTypeId", lookupTypeId);
						screenConfigField.put("lookupFieldService", lookupFieldService);
						screenConfigField.put("lookupFieldFilter", lookupFieldFilter);
						//screenConfigField.put("lookupFieldServiceUrl", lookupFieldServiceUrl);
						screenConfigField.put("fieldService", fieldService);
						screenConfigField.put("pickerWindowId", pickerWindowId);
						screenConfigField.put("roleTypeId", roleTypeId);
						
						screenConfigField.create();
						
					}
				}
				
				// store screen fields [end]
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully updated dynamic screen");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
    
    public static String getDynaFieldRenderDetail(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String dynaConfigId = request.getParameter("dynaConfigId");
		String dynaFieldId = request.getParameter("dynaFieldId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			if (UtilValidate.isNotEmpty(dynaConfigId) && UtilValidate.isNotEmpty(dynaFieldId)) {
    			
				List conditionList = FastList.newInstance();
	    		
	    		conditionList.add(EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, dynaConfigId));
	    		conditionList.add(EntityCondition.makeCondition("dynaFieldId", EntityOperator.EQUALS, dynaFieldId));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue field = EntityUtil.getFirst( delegator.findList("DynaScreenConfigField", mainConditons, null, null, null, false) );
				
				result.put("field", field.getAllFields());
				
				List<Map<String, Object>> fieldDatas = new ArrayList<Map<String, Object>>();
				if (UtilValidate.isNotEmpty(field)) {
					conditionList = FastList.newInstance();
		    		
		    		conditionList.add(EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, dynaConfigId));
		    		conditionList.add(EntityCondition.makeCondition("dynaFieldId", EntityOperator.EQUALS, dynaFieldId));
					
					mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					List<GenericValue> fieldDataList = delegator.findList("DynaScreenConfigFieldData", mainConditons, null, null, null, false);
					
					if (UtilValidate.isNotEmpty(fieldDataList)) {
						for (GenericValue fieldData : fieldDataList) {
							fieldDatas.add(fieldData.getAllFields());
						}
					}
				}
				result.put("fieldDatas", fieldDatas);
    		}
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
    
    public static String fieldDataUpdate(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String dynaConfigId = request.getParameter("dynaConfigId");
		String dynaFieldId = request.getParameter("selectedDynaFieldId");
		
		String dataNameList[] = request.getParameterValues("dataName");
		String dataValueList[] = request.getParameterValues("dataValue");
		String sequenceNumList[] = request.getParameterValues("sequenceNum");
		String roleTypeIdList[] = request.getParameterValues("roleTypeId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			GenericValue field = EntityUtil.getFirst( delegator.findByAnd("DynaScreenConfigField", UtilMisc.toMap("dynaConfigId", dynaConfigId, "dynaFieldId", dynaFieldId), null, false) );
    		
    		if (UtilValidate.isEmpty(field)) {
    			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
    			result.put(GlobalConstants.RESPONSE_MESSAGE, "Dyna Screen field not exists!");
    		}
			
			if (UtilValidate.isEmpty(result.get(GlobalConstants.RESPONSE_CODE))) {
				
				// store field datas [start]
				
				delegator.removeByAnd("DynaScreenConfigFieldData", UtilMisc.toMap("dynaConfigId", dynaConfigId, "dynaFieldId", dynaFieldId));
				
				if (UtilValidate.isNotEmpty(dataNameList)) {
					for (int i = 0; i < dataNameList.length; i++) {
						
						String dataName = dataNameList[i];
						String dataValue = dataValueList[i];
						String sequenceNum = sequenceNumList[i];
						String roleTypeId = roleTypeIdList[i];
						
						dataName = UtilValidate.isEmpty( dataName ) ? null : dataName;
						dataValue = UtilValidate.isEmpty( dataValue ) ? null : dataValue;
						sequenceNum = UtilValidate.isEmpty( sequenceNum ) ? null : sequenceNum;
						roleTypeId = UtilValidate.isEmpty( roleTypeId ) ? null : roleTypeId;
						
						GenericValue fieldData = delegator.makeValue("DynaScreenConfigFieldData");
						
						fieldData.put("dynaConfigId", dynaConfigId);
						fieldData.put("dynaFieldId", dynaFieldId);
						fieldData.put("dynaFieldDataId", ""+i);
						
						fieldData.put("dataName", dataName);
						fieldData.put("dataValue", dataValue);
						fieldData.put("roleTypeId", roleTypeId);
						fieldData.put("sequenceNum", UtilValidate.isNotEmpty(sequenceNum) ? Integer.parseInt(sequenceNum) : null);
						
						fieldData.create();
						
					}
				}
				
				// store screen fields [end]
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
    
    public static String removeDynaConfiguration(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String dynaConfigIds = request.getParameter("dynaConfigIds");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			if (UtilValidate.isNotEmpty(dynaConfigIds)) {
				
				StringTokenizer st = new StringTokenizer(dynaConfigIds, ",");
				
				while(st.hasMoreTokens()) {
					String dynaConfigId = st.nextToken();
					
					List<GenericValue> fieldList = delegator.findByAnd("DynaScreenConfigField", UtilMisc.toMap("dynaConfigId", dynaConfigId), null, false);
					if (UtilValidate.isNotEmpty(fieldList)) {
						for (GenericValue field : fieldList) {
							String dynaFieldId = field.getString("dynaFieldId");
							delegator.removeByAnd("DynaScreenConfigFieldData", UtilMisc.toMap("dynaConfigId", dynaConfigId, "dynaFieldId", dynaFieldId));
							delegator.removeByAnd("DynaScreenConfigFieldAttribute", UtilMisc.toMap("dynaConfigId", dynaConfigId, "dynaFieldId", dynaFieldId));
						}
						delegator.removeAll(fieldList);
					}
					
					delegator.removeByAnd("DynaScreenConfig", UtilMisc.toMap("dynaConfigId", dynaConfigId));
				}
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
    
    public static String fieldAdvConfigUpdateAction(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String dynaConfigId = request.getParameter("dynaConfigId");
		String dynaFieldId = request.getParameter("dynaFieldId");
		
		Enumeration<String> paramNames = request.getParameterNames();
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			if (UtilValidate.isNotEmpty(paramNames)) {
				
				List<String> entityFields = delegator.getModelEntity("DynaScreenConfigField").getAllFieldNames();
				
				while(paramNames.hasMoreElements()) {
					//System.out.println(paramNames.nextElement());
					
					String paramName = paramNames.nextElement();
					if (!entityFields.contains(paramName)) {
						String paramValue = request.getParameter(paramName);
						
						GenericValue fieldAttr = delegator.makeValue("DynaScreenConfigFieldAttribute");
						fieldAttr.put("dynaConfigId", dynaConfigId);
						fieldAttr.put("dynaFieldId", dynaFieldId);
						fieldAttr.put("attrName", paramName);
						fieldAttr.put("attrValue", paramValue);
						delegator.createOrStore(fieldAttr);
					}
					
				}
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
    
    public static String fieldDetailConfigUpdateAction(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String dynaConfigId = request.getParameter("dynaConfigId");
		String dynaFieldId = request.getParameter("dynaFieldId");
		
		Enumeration<String> paramNames = request.getParameterNames();
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			if (UtilValidate.isNotEmpty(paramNames) && UtilValidate.isNotEmpty(dynaConfigId) && UtilValidate.isNotEmpty(dynaFieldId)) {
				
				List conditionList = FastList.newInstance();
	    		
	    		conditionList.add(EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, dynaConfigId));
	    		conditionList.add(EntityCondition.makeCondition("dynaFieldId", EntityOperator.EQUALS, dynaFieldId));
	    		
	    		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue field = EntityUtil.getFirst( delegator.findList("DynaScreenConfigField", mainConditons, null, UtilMisc.toList("sequenceNum"), null, false) );
				if (UtilValidate.isEmpty(field)) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Dyna field not exists!");
					return AjaxEvents.doJSONResponse(response, result);
				}
				
				ModelEntity fieldModelEntity = delegator.getModelEntity("DynaScreenConfigField");
				List<String> entityFields = fieldModelEntity.getAllFieldNames();
				
				while(paramNames.hasMoreElements()) {
					String paramName = paramNames.nextElement();
					if (entityFields.contains(paramName)) {
						String paramValue = request.getParameter(paramName);
						field.put(paramName, fieldModelEntity.convertFieldValue(paramName, paramValue, delegator));
					}
				}
				
				field.store();
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
    
    public static String fieldRemoveAction(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String dynaConfigId = request.getParameter("dynaConfigId");
		String dynaFieldId = request.getParameter("dynaFieldId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			if (UtilValidate.isNotEmpty(dynaConfigId) && UtilValidate.isNotEmpty(dynaFieldId)) {
				
				List conditionList = FastList.newInstance();
	    		
	    		conditionList.add(EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, dynaConfigId));
	    		conditionList.add(EntityCondition.makeCondition("dynaFieldId", EntityOperator.EQUALS, dynaFieldId));
	    		
	    		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue field = EntityUtil.getFirst( delegator.findList("DynaScreenConfigField", mainConditons, null, UtilMisc.toList("sequenceNum"), null, false) );
				if (UtilValidate.isEmpty(field)) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Dyna field not exists!");
					return AjaxEvents.doJSONResponse(response, result);
				}
				
				field.remove();
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
    
}
