package org.fio.ag.grid.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fio.admin.portal.util.DataUtil;
import org.fio.admin.portal.util.UtilGrid;
import org.json.JSONObject;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelFieldTypeReader;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

public class ConfigurationServices {
	private static final String MODULE = ConfigurationServices.class.getName();
	private static final String RESOURCE = "OfbizAgGridUiLabels";
	
	public static String createGridUserConfig(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		String instanceId = UtilValidate.isNotEmpty(context.get("instanceId")) ? (String) context.get("instanceId") : delegator.getNextSeqId("GridUserPreferences");
		String userId = UtilValidate.isNotEmpty(context.get("userId")) ? (String) context.get("userId") : "admin";
		String role = UtilValidate.isNotEmpty(context.get("role")) ? (String) context.get("role") : "ADMIN";
		try {
			
			if(UtilValidate.isNotEmpty(instanceId) && UtilValidate.isNotEmpty(userId) && UtilValidate.isNotEmpty(role)) {
				request.setAttribute("instanceId", instanceId);
		    	request.setAttribute("userId", userId);
				request.setAttribute("role", role);
				ModelEntity modelEntity = delegator.getModelEntity("GridUserPreferences");
				ModelFieldTypeReader modelFieldTypeReader = ModelFieldTypeReader
						.getModelFieldTypeReader(delegator.getGroupHelperInfo("org.ofbiz").getHelperBaseName());
				List<GenericValue> toBeStore = new ArrayList<GenericValue>();
				GenericValue gridUserPreferences = EntityQuery.use(delegator)
													.from("GridUserPreferences")
													.where("instanceId",instanceId,"userId",userId,"role",role)
													.orderBy("-lastUpdatedTxStamp")
													.queryFirst();
				if(UtilValidate.isEmpty(gridUserPreferences)) {
					gridUserPreferences = delegator.makeValue("GridUserPreferences");
					Set<String> fields = context.keySet();
					List<String> entityFields = modelEntity.getAllFieldNames();
					for(String key : fields ) {
						String value = (String) context.get(key);
						if(!entityFields.contains(key))
							continue;
						if(UtilValidate.isEmpty(value))
							continue;
						ModelField field = modelEntity.getField(key); 
						ModelFieldType type = modelFieldTypeReader.getModelFieldType(field.getType()); 
						String javaType = type.getJavaType(); 
						DataUtil.prepareGenericData(key,value,javaType,gridUserPreferences);
					}
					toBeStore.add(gridUserPreferences);
				} else {
					request.setAttribute("_ERROR_MESSAGE_","Record already exists.");
					return "error";
				}
				if(toBeStore.size() > 0) {
					delegator.storeAll(toBeStore);
					//UtilGrid.populateGridInstance(delegator, UtilMisc.toMap("instanceId", instanceId, "userId", userId, "role", role));
				}
			} else {
				request.setAttribute("_ERROR_MESSAGE_","Required parameters missed");
				return "error";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("_EVENT_MESSAGE_","Grid instance created successfully.");
		return "success";
	}
	
	public static String updateGridUserConfig(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		String instanceId = (String) context.get("instanceId");
		String userId = (String) context.get("userId");
		String role = (String) context.get("role");
		try {
			
			if(UtilValidate.isNotEmpty(instanceId) && UtilValidate.isNotEmpty(userId) && UtilValidate.isNotEmpty(role)) {
				request.setAttribute("instanceId", instanceId);
		    	request.setAttribute("userId", userId);
				request.setAttribute("role", role);
				ModelEntity modelEntity = delegator.getModelEntity("GridUserPreferences");
				ModelFieldTypeReader modelFieldTypeReader = ModelFieldTypeReader
						.getModelFieldTypeReader(delegator.getGroupHelperInfo("org.ofbiz").getHelperBaseName());
				List<GenericValue> toBeStore = new ArrayList<GenericValue>();
				GenericValue gridUserPreferences = EntityQuery.use(delegator)
													.from("GridUserPreferences")
													.where("instanceId",instanceId,"userId",userId,"role",role)
													.orderBy("-lastUpdatedTxStamp")
													.queryFirst();
				if(UtilValidate.isNotEmpty(gridUserPreferences)) {
					Set<String> fields = context.keySet();
					List<String> entityFields = modelEntity.getAllFieldNames();
					for(String key : fields ) {
						String value = (String) context.get(key);
						if(!entityFields.contains(key))
							continue;
						if(UtilValidate.isEmpty(value))
							continue;
						ModelField field = modelEntity.getField(key); 
						ModelFieldType type = modelFieldTypeReader.getModelFieldType(field.getType()); 
						String javaType = type.getJavaType(); 
						DataUtil.prepareGenericData(key,value,javaType,gridUserPreferences);
					}
					toBeStore.add(gridUserPreferences);
				} else {
					request.setAttribute("_ERROR_MESSAGE_","Record not found.");
					return "error";
				}
				if(toBeStore.size() > 0) {
					delegator.storeAll(toBeStore);
					//UtilGrid.populateGridInstance(delegator, UtilMisc.toMap("instanceId", instanceId, "userId", userId, "role", role));
				}
			} else {
				request.setAttribute("_ERROR_MESSAGE_","Required parameters missed");
				return "error";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("_EVENT_MESSAGE_","Grid instance updated successfully.");
		return "success";
	}
	
	public static String createAgGridAccessConfig(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		
		String instanceId = UtilValidate.isNotEmpty(context.get("instanceId")) ? (String) context.get("instanceId"):"";
		String groupId = UtilValidate.isNotEmpty(context.get("groupId")) ? (String) context.get("groupId") : "";
		String gridOptions = UtilValidate.isNotEmpty(context.get("gridOptions")) ? (String) context.get("gridOptions") : "";
		try {
			
			if(UtilValidate.isNotEmpty(instanceId) && UtilValidate.isNotEmpty(groupId)) {
				request.setAttribute("instanceId", instanceId);
		    	request.setAttribute("groupId", groupId);
		    	List<String> gridOptionList = Stream.of(gridOptions.split(",")).map(Object::toString).collect(Collectors.toCollection(LinkedList::new));
		    	String jsonOptions = "";
		    	if(UtilValidate.isNotEmpty(gridOptionList)) {
		    		Map<String, Object> optionMap = new HashMap<String, Object>();
		    		for(String options : gridOptionList) {
		    			optionMap.put(options, "Y");
		    		}
		    		List<GenericValue> enumeration = EntityQuery.use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","AG_GRID_OPTIONS").queryList();
		    		if(UtilValidate.isNotEmpty(enumeration)) {
		    			List<String> enumGridOptions = EntityUtil.getFieldListFromEntityList(enumeration, "enumId", true);
		    			enumGridOptions.removeAll(gridOptionList);
		    			if(UtilValidate.isNotEmpty(enumGridOptions)) {
		    				for(String nonSelectedOption : enumGridOptions) {
		    					optionMap.put(nonSelectedOption, "N");
		    				}
		    			}
		    		}
		    		jsonOptions = DataUtil.convertToJson(optionMap);
		    	}
		    	
				GenericValue agGridAccess = EntityQuery.use(delegator)
													.from("AgGridAccess")
													.where("instanceId",instanceId,"groupId",groupId)
													.orderBy("-lastUpdatedTxStamp")
													.queryFirst();
				if(UtilValidate.isEmpty(agGridAccess)) {
					agGridAccess = delegator.makeValue("AgGridAccess");
					agGridAccess.set("instanceId", instanceId);
					agGridAccess.set("groupId", groupId);
					agGridAccess.set("optionsJson", jsonOptions);
					delegator.createOrStore(agGridAccess);
				} else {
					request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(RESOURCE, "RecordAlreadyExists", locale));
					return "error";
				}
			} else {
				request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(RESOURCE, "RequiredParametersMissed", locale));
				return "error";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(RESOURCE, "GridAccessSuccessfullyConfigured", locale));
		return "success";
	}
	
	public static String updateAgGridAccessConfig(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		
		String instanceId = UtilValidate.isNotEmpty(context.get("instanceId")) ? (String) context.get("instanceId"):"";
		String groupId = UtilValidate.isNotEmpty(context.get("groupId")) ? (String) context.get("groupId") : "";
		String gridOptions = UtilValidate.isNotEmpty(context.get("gridOptions")) ? (String) context.get("gridOptions") : "";
		try {
			
			if(UtilValidate.isNotEmpty(instanceId) && UtilValidate.isNotEmpty(groupId)) {
				request.setAttribute("instanceId", instanceId);
		    	request.setAttribute("groupId", groupId);
		    	List<String> gridOptionList = Stream.of(gridOptions.split(",")).map(Object::toString).collect(Collectors.toCollection(LinkedList::new));
		    	String jsonOptions = "";
		    	if(UtilValidate.isNotEmpty(gridOptionList)) {
		    		Map<String, Object> optionMap = new HashMap<String, Object>();
		    		for(String options : gridOptionList) {
		    			optionMap.put(options, "Y");
		    		}
		    		List<GenericValue> enumeration = EntityQuery.use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","AG_GRID_OPTIONS").queryList();
		    		if(UtilValidate.isNotEmpty(enumeration)) {
		    			List<String> enumGridOptions = EntityUtil.getFieldListFromEntityList(enumeration, "enumId", true);
		    			enumGridOptions.removeAll(gridOptionList);
		    			if(UtilValidate.isNotEmpty(enumGridOptions)) {
		    				for(String nonSelectedOption : enumGridOptions) {
		    					optionMap.put(nonSelectedOption, "N");
		    				}
		    			}
		    		}
		    		jsonOptions = DataUtil.convertToJson(optionMap);
		    	}
		    	
				GenericValue agGridAccess = EntityQuery.use(delegator)
													.from("AgGridAccess")
													.where("instanceId",instanceId,"groupId",groupId)
													.orderBy("-lastUpdatedTxStamp")
													.queryFirst();
				if(UtilValidate.isNotEmpty(agGridAccess)) {
					agGridAccess = delegator.makeValue("AgGridAccess");
					agGridAccess.set("instanceId", instanceId);
					agGridAccess.set("groupId", groupId);
					agGridAccess.set("optionsJson", jsonOptions);
					delegator.createOrStore(agGridAccess);
				} else {
					request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(RESOURCE, "RecordNotFound", locale));
					return "error";
				}
			} else {
				request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(RESOURCE, "RequiredParametersMissed", locale));
				return "error";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(RESOURCE, "GridAccessSuccessfullyUpdated", locale));
		return "success";
	}
	
	public static String updateUserGridConfiguration(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		
		String gridInstanceId = UtilValidate.isNotEmpty(context.get("gridInstanceId")) ? (String) context.get("gridInstanceId"):"";
		String gridUserId = UtilValidate.isNotEmpty(context.get("gridUserId")) ? (String) context.get("gridUserId") : "";
		String fieldToHide = UtilValidate.isNotEmpty(context.get("fieldToHide")) ? (String) context.get("fieldToHide") : "";
		try {
			List<String> fieldToHideList = Stream.of(fieldToHide.split(",")).map(Object::toString).collect(Collectors.toCollection(LinkedList::new));
			GenericValue gridPref = EntityQuery.use(delegator)
					.from("GridUserPreferences")
					.where("instanceId",gridInstanceId,"role","USER","userId",gridUserId)
					.queryFirst();
			if(UtilValidate.isNotEmpty(gridPref)) {
				String json = gridPref.getString("gridOptionsJsString");
				JSONObject jsonobj = new JSONObject(json);
				String columnDefStr = jsonobj.get("columnDefs").toString();
				List<Map<String, Object>> columnDefs = new LinkedList<Map<String, Object>>();
				List<Map<String, Object>> columnDefList = org.fio.admin.portal.util.DataUtil.convertToListMap(columnDefStr);
				for(Map<String, Object> columnDef : columnDefList) {
					String field = UtilValidate.isNotEmpty(columnDef.get("field")) ? (String) columnDef.get("field") : "";
					boolean isHide=false;
					if(UtilValidate.isNotEmpty(field) && fieldToHideList.contains(field)) {
						isHide= true;
					}
					columnDef.put("hide", isHide);
					columnDefs.add(columnDef);
				}
				jsonobj.put("columnDefs", columnDefs);
				gridPref.set("gridOptionsJsString", jsonobj.toString());
				gridPref.store();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(RESOURCE, "Error : "+e.getMessage(), locale));
			return "error";
			
		}
		request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(RESOURCE, "UserGridConfigurationSuccessfullyUpdated", locale));
		return "success";
	}
	
	public static String updateAdminGridInstance(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		
		String gridInstanceId = UtilValidate.isNotEmpty(context.get("gridInstanceId")) ? (String) context.get("gridInstanceId"):"";
		String gridUserId = UtilValidate.isNotEmpty(context.get("gridUserId")) ? (String) context.get("gridUserId") : "";
		String gridRole = UtilValidate.isNotEmpty(context.get("gridRole")) ? (String) context.get("gridRole") : "";
		String fieldToHide = UtilValidate.isNotEmpty(context.get("fieldToHide")) ? (String) context.get("fieldToHide") : "";
		try {
			List<String> fieldToHideList = Stream.of(fieldToHide.split(",")).map(Object::toString).collect(Collectors.toCollection(LinkedList::new));
			GenericValue gridPref = EntityQuery.use(delegator)
					.from("GridUserPreferences")
					.where("instanceId",gridInstanceId,"userId",gridUserId,"role","ADMIN")
					.queryFirst();
			if(UtilValidate.isNotEmpty(gridPref)) {
				Map<String, Object> gridColumnJson = org.fio.ag.grid.util.DataUtil.getColumns(delegator, gridInstanceId, gridUserId, "ADMIN");
				List<String> columns = (List<String>) gridColumnJson.get("columns");
				Map<String, Integer> seqMap = new HashMap<String, Integer>();
				if(UtilValidate.isNotEmpty(columns)) {
					for(String column : columns) {
						seqMap.put(column, UtilValidate.isNotEmpty(context.get(column+"_SeqId")) ? Integer.parseInt((String) context.get(column+"_SeqId")) : 0);
					}
				}
				Map<String, Integer> sortedMap = seqMap.entrySet().stream()
												.sorted(Map.Entry.comparingByValue())
								                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
								                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
				
				List<Map<String, Object>> columnDefList = new LinkedList<Map<String,Object>>();
				Set<String> keys = sortedMap.keySet();
				
				if(UtilValidate.isNotEmpty(keys)) {
					for(String key : keys) {
						Map<String, Object> columnMap = DataUtil.convertToMap((String) context.get(key));
						boolean isHide=false;
						if(fieldToHideList.contains(key)) {
							isHide= true;
						}
						columnMap.put("hide", isHide);
						columnDefList.add(columnMap);
					}
				}
				
				boolean pagination = UtilValidate.isNotEmpty(context.get("pagination")) ? Boolean.valueOf((String) context.get("pagination")) : true;
				int paginationPageSize = UtilValidate.isNotEmpty(context.get("paginationPageSize")) ? Integer.parseInt((String) context.get("paginationPageSize")) : 20;
				boolean filter = UtilValidate.isNotEmpty(context.get("filter")) ? Boolean.valueOf((String) context.get("filter")) : true;
				boolean floatingFilter = UtilValidate.isNotEmpty(context.get("floatingFilter")) ? Boolean.valueOf((String) context.get("floatingFilter")) : true;
				String domLayout = UtilValidate.isNotEmpty(context.get("domLayout")) ? (String) context.get("domLayout") : "autoHeight";
				String components = (String) context.get("components");
				String dataUniqueIdField = (String) context.get("dataUniqueIdField");
				String rowSelection = (String) context.get("rowSelection");
				String fileName = (String) context.get("fileName");
				boolean skipHeader = UtilValidate.isNotEmpty(context.get("skipHeader")) ? Boolean.valueOf((String) context.get("skipHeader")) : true;
				
				//JSONObject gridJson = new JSONObject();
				Map<String, Object> gridJson = new LinkedHashMap<String, Object>();	
				gridJson.put("columnDefs", columnDefList);
				gridJson.put("pagination", pagination);
				gridJson.put("paginationPageSize", paginationPageSize);
				gridJson.put("filter", filter);
				gridJson.put("floatingFilter", floatingFilter);
				gridJson.put("domLayout", domLayout);
				
				Map<String, Object> componentMap = new LinkedHashMap<String, Object>();
				List<String> componentList = Stream.of(components.split(",")).map(Object::toString).collect(Collectors.toCollection(LinkedList::new));
				if(UtilValidate.isNotEmpty(componentList)) {
					for(String component : componentList) {
						componentMap.put(component, "quotcom;"+component+"quotcom;");
					}
					gridJson.put("components", componentMap);
				}
				
				
				Map<String, Object> customMap = new LinkedHashMap<String, Object>();
				customMap.put("dataUniqueIdField", dataUniqueIdField);
				customMap.put("rowSelection", rowSelection);
				
				Map<String, Object> csvExportOptions = new LinkedHashMap<String, Object>();
				csvExportOptions.put("fileName", fileName);
				csvExportOptions.put("skipHeader", skipHeader);
				customMap.put("csvExportOptions", csvExportOptions);
				
				gridJson.put("custom", customMap);
				
				String gridJsonStr = DataUtil.convertToJson(gridJson);
				gridJsonStr = gridJsonStr.replace("\"quotcom;", "").replace("quotcom;\"", "");
				
				gridPref.set("gridOptionsJsString", gridJsonStr);
				gridPref.store();
				
				// remove all User preferences for this instance
				List<GenericValue> userGridPref = EntityQuery.use(delegator).from("GridUserPreferences")
													.where("instanceId", gridInstanceId, "role", "USER")
													.queryList();
				if(UtilValidate.isNotEmpty(userGridPref))
					delegator.removeAll(userGridPref);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(RESOURCE, "Error : "+e.getMessage(), locale));
			return "error";
			
		}
		request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(RESOURCE, "AdminGridInstanceSuccessfullyUpdated", locale));
		return "success";
	}
	
	
}
