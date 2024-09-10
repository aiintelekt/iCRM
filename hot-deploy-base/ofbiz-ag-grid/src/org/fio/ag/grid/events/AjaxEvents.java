package org.fio.ag.grid.events;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.admin.portal.util.DataUtil;
import org.fio.admin.portal.util.UtilGrid;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AjaxEvents {
	private AjaxEvents() {}

	private static final String MODULE = AjaxEvents.class.getName();
	private static final String RESOURCE = "OfbizAgGridUiLabels";
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
	/*
	public static String getAllGridUserConfig(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = new ArrayList<>();
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		
		long start = System.currentTimeMillis();
		String userLoginId = null;
		if (userLogin != null) {
			userLoginId = (String) userLogin.get("userLoginId");
		}
		String instanceId = (String) context.get("instanceId");
		String userId = (String) context.get("userId");
		String name = (String) context.get("name");
		String role = (String) context.get("role");
		//String direction = (String) context.get("direction");
		String startIndex = (String) context.get("startIndex");
		String endIndex = (String) context.get("endIndex");
		try {
			
			//get the default general grid fetch limit
			GenericValue systemProperty = EntityQuery.use(delegator).select("systemPropertyValue").from("SystemProperty")
											.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
											.queryFirst();
			if(UtilValidate.isEmpty(endIndex) && UtilValidate.isNotEmpty(systemProperty))
				endIndex = UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ? systemProperty.getString("systemPropertyValue") : "1000";
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			if(UtilValidate.isNotEmpty(instanceId))
				conditions.add(EntityCondition.makeCondition("instanceId",EntityOperator.LIKE,instanceId+"%"));
			
			if(UtilValidate.isNotEmpty(userId))
				conditions.add(EntityCondition.makeCondition("userId",EntityOperator.EQUALS,userId));
			
			if(UtilValidate.isNotEmpty(name))
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("description",EntityOperator.LIKE,"%"+name+"%"),
								EntityCondition.makeCondition("name",EntityOperator.LIKE,"%"+name+"%")
							));
			
			if(UtilValidate.isNotEmpty(role))
				conditions.add(EntityCondition.makeCondition("role",EntityOperator.EQUALS,role));
			
			long totalRecords = EntityQuery.use(delegator)
					.from("GridUserPreferences")
					.where(EntityCondition.makeCondition(conditions,EntityOperator.AND))
					.queryCount();
			result.put("totalRecords", nf.format(totalRecords));
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			int startInx = UtilValidate.isNotEmpty(startIndex) ? Integer.parseInt(startIndex) : 0;
			int endInx = UtilValidate.isNotEmpty(endIndex) ? Integer.parseInt(endIndex) : 0;
			if(UtilValidate.isNotEmpty(startInx))
				efo.setOffset(startInx);
			if(UtilValidate.isNotEmpty(endInx)  && endInx > 0)
				efo.setLimit(endInx);
			
			List<GenericValue> gridUserPerferencesList = delegator.findList("GridUserPreferences",EntityCondition.makeCondition(conditions, EntityOperator.AND), 
														 null,null, efo, false);
			if(UtilValidate.isNotEmpty(gridUserPerferencesList)) {
				for(GenericValue gridUserPerferences : gridUserPerferencesList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(DataUtil.convertGenericValueToMap(delegator, gridUserPerferences));
					data.put("lastUpdatedTxStamp", gridUserPerferences.getString("lastUpdatedTxStamp"));
					data.put("createdTxStamp", gridUserPerferences.getString("createdTxStamp"));
					dataList.add(data);
				}
			}
			result.put("chunks", (int) Math.ceil((double) totalRecords / (double) endInx));
			//resultMap.put("message", "Data successfully loaded.");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("chunkSize", endIndex);
		
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}
	*/
	
	public static String getAllGridUserConfig(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = new ArrayList<>();
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		List<GenericValue> resultList = null;
		
		long start = System.currentTimeMillis();
		String userLoginId = null;
		if (userLogin != null) {
			userLoginId = (String) userLogin.get("userLoginId");
		}
		String instanceId = (String) context.get("instanceId");
		String userId = (String) context.get("userId");
		String name = (String) context.get("name");
		String role = (String) context.get("role");
		//String direction = (String) context.get("direction");
		String startIndex = (String) context.get("startIndex");
		String endIndex = (String) context.get("endIndex");
		try {
			
			//get the default general grid fetch limit
			GenericValue systemProperty = EntityQuery.use(delegator).select("systemPropertyValue").from("SystemProperty")
											.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
											.queryFirst();
			if(UtilValidate.isEmpty(endIndex) && UtilValidate.isNotEmpty(systemProperty))
				endIndex = UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ? systemProperty.getString("systemPropertyValue") : "1000";
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			if(UtilValidate.isNotEmpty(instanceId))
				conditions.add(EntityCondition.makeCondition("instanceId",EntityOperator.LIKE, "%" + instanceId + "%"));
			
			if(UtilValidate.isNotEmpty(userId))
				conditions.add(EntityCondition.makeCondition("userId",EntityOperator.LIKE, "%" + userId + "%"));
			
			if(UtilValidate.isNotEmpty(name))
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("description",EntityOperator.LIKE, "%" + name + "%"),
								EntityCondition.makeCondition("name",EntityOperator.LIKE, "%" + name + "%")
							));
			
			if(UtilValidate.isNotEmpty(role))
				conditions.add(EntityCondition.makeCondition("role",EntityOperator.LIKE, "%" + role + "%"));
			
			// set the page parameters
	        int viewIndex = 0;
	        try {
	            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
	        } catch (Exception e) {
	            viewIndex = 0;
	        }
	        result.put("viewIndex", Integer.valueOf(viewIndex));

	        int fioGridFetch = 0;
	        if(UtilValidate.isNotEmpty(systemProperty)) {
	        	fioGridFetch = UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;
	        }
	        int viewSize = fioGridFetch;
	        try {
	            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
	        } catch (Exception e) {
	            viewSize = fioGridFetch;
	        }
	        result.put("viewSize", Integer.valueOf(viewSize));
			
			int highIndex = 0;
            int lowIndex = 0;
            int resultListSize = 0;
            try {
                // get the indexes for the partial list            	
            	lowIndex = viewIndex * viewSize + 1;
                highIndex = (viewIndex + 1) * viewSize;
                
                // set distinct on so we only get one row per order
                // using list iterator
                EntityListIterator eli = EntityQuery.use(delegator)
                        .from("GridUserPreferences")
                        .where(EntityCondition.makeCondition(conditions, EntityOperator.AND))
                        .orderBy("-createdTxStamp")
                        .cursorScrollInsensitive()
                        .fetchSize(highIndex)
                        .distinct()
                        .cache(true)
                        .queryIterator();
              
                // get the partial list for this page
                resultList = eli.getPartialList(lowIndex, viewSize);

                // attempt to get the full size
                resultListSize = eli.getResultsSizeAfterPartialList();
                // close the list iterator
                eli.close();
            } catch (GenericEntityException e) {
                String errMsg = "Error: " + e.toString();
                Debug.logError(e, errMsg, MODULE);
            }
			
			if(UtilValidate.isNotEmpty(resultList)) {
				for(GenericValue gridUserPerferences : resultList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(DataUtil.convertGenericValueToMap(delegator, gridUserPerferences));
					data.put("lastUpdatedTxStamp", gridUserPerferences.getString("lastUpdatedTxStamp"));
					data.put("createdTxStamp", gridUserPerferences.getString("createdTxStamp"));
					dataList.add(data);
				}
				result.put("highIndex", Integer.valueOf(highIndex));
		        result.put("lowIndex", Integer.valueOf(lowIndex));
			}
			result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			result.put("totalRecords", nf.format(resultListSize));
			result.put("recordCount", resultListSize);
			//resultMap.put("message", "Data successfully loaded.");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		result.put("timeTaken", (end-start) / 1000f);
		result.put("chunkSize", endIndex);
		
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}
	
	public static String getAgGridAccessConfig(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		List<Map<String, Object>> results = new ArrayList<>();
		String instanceId = (String) context.get("instanceId");
		String groupId = (String) context.get("groupId");
		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			if(UtilValidate.isNotEmpty(instanceId))
				conditions.add(EntityCondition.makeCondition("instanceId",EntityOperator.LIKE, "%" + instanceId + "%"));
			
			if(UtilValidate.isNotEmpty(groupId))
				conditions.add(EntityCondition.makeCondition("groupId",EntityOperator.LIKE, "%" + groupId + "%"));
			
			List<GenericValue> agGridAccessList = EntityQuery.use(delegator)
											.from("AgGridAccess")
											.where(EntityCondition.makeCondition(conditions,EntityOperator.AND))
											.queryList();
			if(UtilValidate.isNotEmpty(agGridAccessList)) {
				for(GenericValue agGridAccess : agGridAccessList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(DataUtil.convertGenericValueToMap(delegator, agGridAccess));
					String optionsJson = agGridAccess.getString("optionsJson");
					String instId =  agGridAccess.getString("instanceId");
					String grpId =  agGridAccess.getString("groupId");
					GenericValue securityGroup = EntityQuery.use(delegator).select("description").from("SecurityGroup").where("groupId",grpId).queryFirst();
					if(UtilValidate.isNotEmpty(securityGroup)) {
						data.put("securityGroupDesc",securityGroup.getString("description"));
					}

					GenericValue gridInstance = EntityQuery.use(delegator).select("description","name").from("GridUserPreferences").where("instanceId", instId,"userId","admin","role","ADMIN").queryFirst();
					if(UtilValidate.isNotEmpty(gridInstance)) {
						data.put("gridName",UtilValidate.isNotEmpty(gridInstance.getString("name")) ? gridInstance.getString("name") : gridInstance.getString("description"));
					}

					if(UtilValidate.isNotEmpty(optionsJson)) {
						Map<String, Object> optionMap = DataUtil.convertToMap(optionsJson);
						data.put("gridAccess", UtilValidate.isNotEmpty(optionMap.get("GRID_ACCESS")) ? optionMap.get("GRID_ACCESS") : "");
						data.put("rowInsert", UtilValidate.isNotEmpty(optionMap.get("GRID_INSERT")) ? optionMap.get("GRID_INSERT") : "");
						data.put("rowUpdate", UtilValidate.isNotEmpty(optionMap.get("GRID_UPDATE")) ? optionMap.get("GRID_UPDATE") : "");
						data.put("rowRemove", UtilValidate.isNotEmpty(optionMap.get("GRID_REMOVE")) ? optionMap.get("GRID_REMOVE") : "");
						data.put("saveGridPref", UtilValidate.isNotEmpty(optionMap.get("SAVE_GRID_PREF")) ? optionMap.get("SAVE_GRID_PREF") : "");
						data.put("refreshGridPref", UtilValidate.isNotEmpty(optionMap.get("REFRESH_GRID_PREF")) ? optionMap.get("REFRESH_GRID_PREF") : "");
						data.put("clearFilter", UtilValidate.isNotEmpty(optionMap.get("CLEAR_GRID_FILTER")) ? optionMap.get("CLEAR_GRID_FILTER") : "");
						data.put("gridExport", UtilValidate.isNotEmpty(optionMap.get("EXPORT_GRID_DATA")) ? optionMap.get("EXPORT_GRID_DATA") : "");
					}
					data.put("lastUpdatedTxStamp", agGridAccess.getString("lastUpdatedTxStamp"));
					data.put("createdTxStamp", agGridAccess.getString("createdTxStamp"));
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doJSONResponse(response, results);
	}
	
	public static String getGridInstanceInfo(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		GenericValue adminGridInstance = null;
		String gridInstanceId = (String) context.get("gridInstanceId");
		try {
			if(UtilValidate.isNotEmpty(gridInstanceId)) {
				//adminGridInstance = UtilGrid.getGridAdminInstance(delegator, gridInstanceId, "admin");
				adminGridInstance = EntityQuery.use(delegator).from("GridUserPreferences")
						.where("instanceId", gridInstanceId, "role", "ADMIN").queryFirst();
				if(UtilValidate.isNotEmpty(adminGridInstance)) {
					result.put("appliedRequestUrl", adminGridInstance.getString("appliedRequestUrl"));
					result.put("instanceId", adminGridInstance.getString("instanceId"));
					result.put("userId", adminGridInstance.getString("userId"));
					result.put("role", adminGridInstance.getString("role"));
					result.put("description", adminGridInstance.getString("description"));
					result.put("name", adminGridInstance.getString("name"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doJSONResponse(response, result);
	}
	
}
