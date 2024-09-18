package com.groupfio.gridperference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.LocalDispatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupfio.ofbiz.util.HttpUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GridUserPreferenceConfigServices {

	public static final String module = GridUserPreferenceConfigServices.class.getName();
	public static final String resource = "CommonUiLabels";
	private static Set<String> excludeSet;
	private static ExecutorService exec = Executors.newSingleThreadExecutor();

	static {
		String[] exclude = new String[] { "thisRequestUri", "_FORWARDED_FROM_SERVLET_", "_SERVER_ROOT_URL_",
				"_CONTROL_PATH_", "_CONTEXT_ROOT_", "org.apache.tomcat.util.net.secure_protocol_version",
				"javax.servlet.request.key_size", "javax.servlet.request.cipher_suite",
				"javax.servlet.request.ssl_session_id", "targetRequestUri" };
		excludeSet = new HashSet<String>(Arrays.asList(exclude));
	}

	public GridUserPreferenceConfigServices() {

	}
	
	public static String fetchAllGridUserConfig(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parmMap = UtilHttp.getParameterMap(request);
		String userLoginId = null;
		if (userLogin != null) {
			userLoginId = (String) userLogin.get("userLoginId");
		}
		String role = (String) parmMap.get("role");
		try {
			List<GenericValue> all = null;
			if(role != null) {
				all = EntityQuery.use(delegator).from("GridUserPreferences").where("role", role).queryList();
			}else {
				all = EntityQuery.use(delegator).from("GridUserPreferences").queryList();
			}
			List<Map<String, Object>> allMaps = new ArrayList<>();
			if(all.size() > 0) {
				for(GenericValue gv: all) {
					allMaps.add(gv.getAllFields());
				}
			}
			HttpUtil.jsonResponse(response, HttpUtil.mapper.writeValueAsString(allMaps));
		} catch (GenericEntityException | JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}

	public static String saveGridConfigFromAgGridUI(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

		String jsonBodyStr = HttpUtil.getJsonStrBody(request);
		Debug.logInfo("jsonBodyStr: "+jsonBodyStr, module);
		Map<String, Object> parmMap = HttpUtil.jsonObjStrToMap(jsonBodyStr);
		String userLoginId = null;
		if (userLogin != null) {
			userLoginId = (String) userLogin.get("userLoginId");
		}
		String userId = (String) parmMap.get("userid");
		String instanceId = (String) parmMap.get("instanceid");
		Debug.logInfo("userid: " + userId + ", instanceid: " + instanceId
				+ ", userLoginId: " + userLoginId + ")", module);
		
		Debug.logInfo("parmMap: "+parmMap.toString(), module);
		if (userId != null && instanceId != null) {
			try {
				String userGridOptionsJs = (String) parmMap.get("userGridOptions");
				Debug.logInfo("userGridOptionsJs: "+userGridOptionsJs, module);
				GenericValue newUserPref = delegator.makeValidValue("GridUserPreferences",  UtilMisc.toMap("instanceId", instanceId, "userId", userId, "role", "USER", "gridOptionsJsString", userGridOptionsJs));
				delegator.createOrStore(newUserPref);
			} catch ( GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			HttpUtil.jsonResponse(response, "{\"error\":\"lookup params not provided\"}");
		}
		return "success";
	}
	
	public static String removeGridConfigFromGridAdmin(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

		Map<String, Object> parmMap = UtilHttp.getParameterMap(request);
		String userLoginId = null;
		if (userLogin != null) {
			userLoginId = (String) userLogin.get("userLoginId");
		}
		String userId = (String) parmMap.get("userId");
		String instanceId = (String) parmMap.get("instanceId");
		String role = (String) parmMap.get("role");
		Debug.logInfo("userid: " + userId + ", instanceid: " + instanceId
				+ ", userLoginId: " + userLoginId + ")", module);
		
		Debug.logInfo("parmMap: "+parmMap.toString(), module);
		if (userId != null && instanceId != null && role != null) {
			try {
				delegator.removeByAnd("GridUserPreferences", UtilMisc.toMap("instanceId", instanceId, "userId", userId, "role", role));
			} catch ( GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			HttpUtil.jsonResponse(response, "{\"error\":\"lookup params not provided\"}");
		}
		return "success";
	}
	
	
	
	public static String saveGridConfigFromGridAdmin(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String jsonBodyStr = HttpUtil.getJsonStrBody(request);
		Debug.logInfo("jsonBodyStr: "+jsonBodyStr, module);
		Map<String, Object> parmMap = HttpUtil.jsonObjStrToMap(jsonBodyStr);
//		Map<String, Object> parmMap = UtilHttp.getParameterMap(request);
		String userLoginId = null;
		if (userLogin != null) {
			userLoginId = (String) userLogin.get("userLoginId");
		}
		String userId = (String) parmMap.get("userId");
		String instanceId = (String) parmMap.get("instanceId");
		Debug.logInfo("userid: " + userId + ", instanceid: " + instanceId
				+ ", userLoginId: " + userLoginId + ")", module);
		
		Debug.logInfo("parmMap: "+parmMap.toString(), module);
		if (userId != null && instanceId != null) {
			try {
				String userGridOptionsJs = (String) parmMap.get("userGridOptions");
				Debug.logInfo("userGridOptionsJs: "+userGridOptionsJs, module);
				GenericValue newUserPref = delegator.makeValidValue("GridUserPreferences",  parmMap);
				delegator.createOrStore(newUserPref);
			} catch ( GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			HttpUtil.jsonResponse(response, "{\"error\":\"lookup params not provided\"}");
		}
		return "success";
	}

	public static String getGridUserConfig(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String userLoginId = null;
		if (userLogin != null) {
			userLoginId = (String) userLogin.get("userLoginId");
		}
		Map<String, Object> parmMap = UtilHttp.getParameterMap(request);
		String userId = (String) parmMap.get("userid");
		String instanceId = (String) parmMap.get("instanceid");
//		String lastUpdateTimeRequestStr = (String) parmMap.get("lastUpdateTime");
//		Long lastUpdateTimeRequest = null;
//		if (!lastUpdateTimeRequestStr.equals("-1")) {
//			lastUpdateTimeRequest = new Long(lastUpdateTimeRequestStr);
//		}
		Debug.logInfo("userid: " + userId + ", instanceid: " + instanceId
				+ ", userLoginId: " + userLoginId + ")", module);
		if (userId != null && instanceId != null) {
			try {
			    Map<String, String> res = gridInstancePrefLookup(request, delegator, userId, instanceId);
				HttpUtil.jsonResponse(response, HttpUtil.mapper.writeValueAsString(res));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				HttpUtil.jsonResponse(response, "{\"error\":\"lookup result malformed\"}");
			}
		} else {
			HttpUtil.jsonResponse(response, "{\"error\":\"lookup params not provided\"}");
		}
		return "success";
	}

	private static boolean isUpdate(Long lastUpdateTimeRequest, Future<Map<String, String>> future)
			throws InterruptedException, ExecutionException {
		String latestTimestamp = future.get().get("lastUpdateTime");
//		Debug.logInfo("latestTimestamp: "+latestTimestamp, module);
//		Debug.logInfo("lastUpdateTimeRequest: "+lastUpdateTimeRequest, module);
		boolean isUpdate = lastUpdateTimeRequest != null
				? Long.valueOf(latestTimestamp) > lastUpdateTimeRequest
				: true;
		return isUpdate;
	}

	private static Map<String, String> gridInstancePrefLookup(HttpServletRequest request, Delegator delegator,
			String userId, String instanceId) {
		Map<String, String> jsonMap = new HashMap<>();
		Timestamp adminTimestamp = null, userTimestamp = null;
		Long lastUpdateTime = null;
		GenericValue gridUserState;
		GenericValue gridAdminState;
		try {
			gridUserState = EntityQuery.use(delegator).from("GridUserPreferences")
					.where("instanceId", instanceId, "userId", userId, "role", "USER")
					.queryFirst();
			if (gridUserState != null) {
				userTimestamp = gridUserState.getTimestamp("lastUpdatedTxStamp");
			}

			gridAdminState = EntityQuery.use(delegator).from("GridUserPreferences")
					.where("instanceId", instanceId, "role", "ADMIN").queryFirst();
			if (gridAdminState != null) {
				adminTimestamp = gridAdminState.getTimestamp("lastUpdatedTxStamp");
			}
			if (userTimestamp != null && userTimestamp.getTime() > adminTimestamp.getTime()) {
				lastUpdateTime = userTimestamp.getTime();
			} else {
				lastUpdateTime = adminTimestamp.getTime();
			}
			jsonMap.put("lastUpdateTime", lastUpdateTime.toString());
			String json = null;
			if (gridAdminState != null && gridAdminState.getString("gridOptionsJsString") != null) {
				json = gridAdminState.getString("gridOptionsJsString");
				jsonMap.put("admin", json);
				jsonMap.put("mode", gridAdminState.getString("mode"));
				jsonMap.put("appliedRequestUrl", gridAdminState.getString("appliedRequestUrl"));
				jsonMap.put("appliedRequestVerb", gridAdminState.getString("appliedRequestVerb"));
				jsonMap.put("appliedRequestBodyJSON", gridAdminState.getString("appliedRequestBodyJSON"));
				
				jsonMap.put("datacreate", gridAdminState.getString("datacreate"));
				jsonMap.put("dataupdate", gridAdminState.getString("dataupdate"));
				jsonMap.put("dataremove", gridAdminState.getString("dataremove"));

			}
			json = null;
			if (gridUserState != null && gridUserState.getString("gridOptionsJsString") != null) {
				json = gridUserState.getString("gridOptionsJsString");
				jsonMap.put("user", json);
			}

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return null;
		}
		return jsonMap;
	}

	

}
