package org.fio.appbar.portal.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.admin.portal.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webtools.labelmanager.LabelInfo;
import org.ofbiz.webtools.labelmanager.LabelManagerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author Mahendran
 *
 */

public class AjaxEvents {
	private AjaxEvents() {
	}

	private static final String MODULE = AjaxEvents.class.getName();
	private static final String RESOURCE = "AppbarPortalUiLabels";

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
			Debug.logWarning(
					"Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(),
					MODULE);
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
	
	public static String configureAppBarUserPreference(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String appBarId = (String) context.get("appBarId");
		String appBarTypeId = (String) context.get("appBarTypeId");
		String userLoginId = (String) userLogin.getString("userLoginId");
		try {
			if(UtilValidate.isNotEmpty(appBarId) && UtilValidate.isNotEmpty(appBarTypeId)) {
				List<EntityCondition> commonConditions = new ArrayList<EntityCondition>();
				commonConditions.add(EntityCondition.makeCondition("appBarId",EntityOperator.EQUALS,appBarId));
				commonConditions.add(EntityCondition.makeCondition("appBarTypeId",EntityOperator.EQUALS,appBarTypeId));
				
				List<EntityCondition> appBarUserPreference = new ArrayList<EntityCondition>();
				appBarUserPreference.addAll(commonConditions);
				
				List<GenericValue> appBarUserPreferenceList = EntityQuery.use(delegator).from("AppBarUserPreference").where(appBarUserPreference).queryList();
				if(UtilValidate.isNotEmpty(appBarUserPreferenceList)) {
					delegator.removeAll(appBarUserPreferenceList);
				}
				
				List<EntityCondition> appBarElementCond = new ArrayList<EntityCondition>();
				
				appBarElementCond.addAll(commonConditions);
				
				appBarElementCond.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("appBarElementActive",EntityOperator.EQUALS,null),
						EntityCondition.makeCondition("appBarElementActive",EntityOperator.EQUALS,"Y")
						));
				
				List<GenericValue> appBarElementList = EntityQuery.use(delegator)
															.select("appBarElementId")
															.from("AppBarElements")
															.where(EntityCondition.makeCondition(appBarElementCond,EntityOperator.AND)).queryList();
				List<GenericValue> toBeStore = new ArrayList<GenericValue>();
				if(UtilValidate.isNotEmpty(appBarElementList)) {
					for(GenericValue appBarElement : appBarElementList) {
						String elementId = appBarElement.getString("appBarElementId");
						String isEnable = (String) context.get(elementId+"_isEnable");
						if("Y".equals(isEnable)) {
							String seqId = UtilValidate.isNotEmpty(context.get(elementId+"_SeqId")) ? (String) context.get(elementId+"_SeqId") : "0";
							GenericValue userPref = delegator.makeValue("AppBarUserPreference");
							userPref.set("appBarId", appBarId);
							userPref.set("appBarTypeId", appBarTypeId);
							userPref.set("userLoginId", userLoginId);
							userPref.set("appBarElementId", elementId);
							userPref.set("appBarElementSeqNum", Integer.parseInt(seqId));
							userPref.set("createdBy", userLoginId);
							userPref.set("createdOn", UtilDateTime.nowTimestamp());
							toBeStore.add(userPref);
						}
					}
					if(UtilValidate.isNotEmpty(toBeStore)) {
						delegator.storeAll(toBeStore);
						request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(RESOURCE, "AppBarUserPreferenceSavedSuccessfully", locale));
					}
						
				} else {
					Debug.logError("App Bar Element is empty", MODULE);
					request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(RESOURCE, "AppBarElementIsEmpty", locale));
					return "error";
				}
			} else {
				Debug.logError("AppBarId/AppBarTypeId is empty", MODULE);
				request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(RESOURCE, "RequiredParameterMissed", locale));
				return "error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		
		return "success";
		
	}
	
	public static String getAppBarElements(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		List<Map<String, Object>> results = new ArrayList<>();
		String appBarId = (String) context.get("appBarId");
		String appBarTypeId = (String) context.get("appBarTypeId");
		try {
			if(UtilValidate.isNotEmpty(appBarId) && UtilValidate.isNotEmpty(appBarTypeId)) {
				List<GenericValue> appBarElementList = EntityQuery.use(delegator).from("AppBarElements").where("appBarId", appBarId, "appBarTypeId", appBarTypeId).orderBy("appBarElementSeqNum ASC").queryList();
				if(UtilValidate.isNotEmpty(appBarElementList)) {
					for(GenericValue appBarElement : appBarElementList) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.putAll(DataUtil.convertGenericValueToMap(delegator, appBarElement));
						String permissionId=  appBarElement.getString("securityPermissionId");
						if(UtilValidate.isNotEmpty(permissionId)) {
							GenericValue securityPermission =  EntityQuery.use(delegator).select("description").from("SecurityPermission").where("permissionId", permissionId).queryFirst();
							if(UtilValidate.isNotEmpty(securityPermission))
								data.put("securityPermissionDesc", securityPermission.getString("description"));
						}
						String elementType = appBarElement.getString("appBarElementType");
						if(UtilValidate.isNotEmpty(elementType)) {
							GenericValue enumeration = EntityQuery.use(delegator).select("description").from("Enumeration").where("enumTypeId","APP_BAR_ELEMENT_TYPE","enumCode",elementType).queryFirst();
							if(UtilValidate.isNotEmpty(enumeration))
								data.put("elementTypeDesc", enumeration.getString("description"));
						}
						String appBarElementPosition = appBarElement.getString("appBarElementPosition");
						data.put("elementPositionDesc", "L".equals(appBarElementPosition) ? "Left" : "R".equals(appBarElementPosition) ? "Right" : "Left" );
						
						String appBarElementActive = appBarElement.getString("appBarElementActive");
						data.put("appBarElementActiveDesc", "Y".equals(appBarElementActive) ? "Yes" : "N".equals(appBarElementActive) ? "No" : "Yes");
						
						data.put("createdOn", appBarElement.getString("createdOn"));
						data.put("modifiedOn", appBarElement.getString("modifiedOn"));
						results.add(data);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doJSONResponse(response, results);
	}
	
	public static String getAppBar(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		List<Map<String, Object>> results = new ArrayList<>();
		String appBarId = (String) context.get("appBarId");
		String appBarTypeId = (String) context.get("appBarTypeId");
		String appBarName = (String) context.get("appBarName");
		String barBarStatus = (String) context.get("barBarStatus");
		
		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			if(UtilValidate.isNotEmpty(appBarId)) 
				conditions.add(EntityCondition.makeCondition("appBarId",EntityOperator.LIKE, "%" + appBarId + "%"));
			
			if(UtilValidate.isNotEmpty(appBarTypeId))
				conditions.add(EntityCondition.makeCondition("appBarTypeId",EntityOperator.EQUALS,appBarTypeId));
			
			if(UtilValidate.isNotEmpty(appBarName))
				conditions.add(EntityCondition.makeCondition("appBarName",EntityOperator.LIKE,"%"+appBarName+"%"));
			
			if(UtilValidate.isNotEmpty(barBarStatus)) 
				conditions.add(EntityCondition.makeCondition("barBarStatus",EntityOperator.EQUALS,barBarStatus));
		
			List<GenericValue> appBarList = EntityQuery.use(delegator).from("AppBar").where(EntityCondition.makeCondition(conditions,EntityOperator.AND)).orderBy("lastUpdatedTxStamp DESC").queryList();
			if(UtilValidate.isNotEmpty(appBarList)) {
				for(GenericValue appBar : appBarList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(DataUtil.convertGenericValueToMap(delegator, appBar));
					GenericValue appBarType = EntityQuery.use(delegator).select("appBarTypeName").from("AppBarType").where("appBarTypeId", appBar.getString("appBarTypeId")).queryFirst();
					if(UtilValidate.isNotEmpty(appBarType))
						data.put("appBarTypeName", appBarType.getString("appBarTypeName"));
					GenericValue appBarStatus = EntityQuery.use(delegator).select("description").from("Enumeration").where("enumTypeId","APP_BAR_STATUS","enumCode", appBar.getString("appBarStatus")).queryFirst();
					if(UtilValidate.isNotEmpty(appBarStatus))
						data.put("statusDesc", appBarStatus.getString("description"));
					
					GenericValue appBarAccessLevel = EntityQuery.use(delegator).select("description").from("Enumeration").where("enumTypeId","APP_BAR_ACCESS_LEVEL","enumCode", appBar.getString("appBarAccessLevel")).queryFirst();
					if(UtilValidate.isNotEmpty(appBarAccessLevel))
						data.put("accessLevelDesc", appBarAccessLevel.getString("description"));
					
					GenericValue appBarDataLevel = EntityQuery.use(delegator).select("description").from("Enumeration").where("enumTypeId","APP_BAR_DATA_LEVEL","enumCode", appBar.getString("appBarDataLevel")).queryFirst();
					if(UtilValidate.isNotEmpty(appBarDataLevel))
						data.put("dataLevelDesc", appBarDataLevel.getString("description"));
					
					data.put("createdOn", appBar.getString("createdOn"));
					data.put("modifiedOn", appBar.getString("modifiedOn"));
					data.put("lastUpdatedTxStamp", appBar.getString("lastUpdatedTxStamp"));
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doJSONResponse(response, results);
	}
	
	
	public static String getUiLabels(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

		String labelComponentName = (String) context.get("labelComponentName");
		String labelFileName = (String) context.get("labelFileName");
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
					
					result.add(data);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return doJSONResponse(response, result);
		
		
	}
}
