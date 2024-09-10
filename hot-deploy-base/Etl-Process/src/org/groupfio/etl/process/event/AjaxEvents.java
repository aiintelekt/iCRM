package org.groupfio.etl.process.event;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.util.CommonUtil;
import org.groupfio.etl.process.util.DefaultValueUtil;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * Ajax events to be invoked by the controller.
 *
 * @author Group Fio
 */
public class AjaxEvents {

    public static final String module = AjaxEvents.class.getName();
	
	/**
     * Using common method to return json response.
     */
    private static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
        return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, JSONArray.fromObject(collection).toString());
    }
    
    /**
     * Using common method to return json response.
     */
    private static String doJSONResponse(HttpServletResponse response, Map map) {
        return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, JSONObject.fromObject(map));
    }
	
    @SuppressWarnings("unchecked")
    public static String updateEtlModelDefaults(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession(true);
        
        String modelName = request.getParameter("modelName");
        
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	Enumeration params = request.getParameterNames();
        	
        	//request.getParameterValues("startRange")
        	
        	while(params.hasMoreElements()) {
        		String paramName = (String) params.nextElement();
        		String paramValue = request.getParameter(paramName);
        		Debug.log("Parameter Name - "+paramName+", Value - "+paramValue);
        		
        		if (UtilValidate.isNotEmpty(paramName) && paramName.startsWith("etl_param")) {
        			
        			String propertyName = ParamUtil.getParameterName(paramName);
        			
        			GenericValue modelDefault = EntityUtil.getFirst( delegator.findByAnd("EtlModelDefaults", UtilMisc.toMap("modelName", modelName, "propertyName", propertyName), null, false) );
        			
        			if (UtilValidate.isNotEmpty(modelDefault)) {
        				
        				modelDefault.put("propertyValue", paramValue);
        				modelDefault.store();
        				
        			} else {
        				
        				modelDefault = delegator.makeValue("EtlModelDefaults", UtilMisc.toMap("modelName", modelName, "propertyName", propertyName));
        				modelDefault.put("propertyValue", paramValue);
        				modelDefault.create();
        				
        			}
        			
        		}
        		
        	}
        	
        	String isDatafileNoHeader = request.getParameter("etl_param_isDatafileNoHeader");
        	if (UtilValidate.isEmpty(isDatafileNoHeader)) {
        		delegator.removeByAnd("EtlModelDefaults", UtilMisc.toMap("modelName", modelName, "propertyName", "isDatafileNoHeader"));
        	} else if (isDatafileNoHeader.equals("Y")) {
        		
        		// Copy header file to header location
        		
        		String filePath1 = ComponentConfig.getRootLocation("Etl-Process")+"webapp/importFiles/";
        		//M.Vijayakumar desc: For the purpose of changing the file location for dynamically.
    			String filePath = EntityUtilProperties.getPropertyValue("Etl-Process", "etl.files.location", filePath1, delegator);
    			File file = new File(filePath);
    			if(!file.exists()){
    				request.setAttribute("_ERROR_MESSAGE_", "File path Failed, Due to [ "+file+" ] Location not found");
    				return "error";
    			}
    			//end @vijayakumar
    			
        		
        		GenericValue mappingElement = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",
						UtilMisc.toMap("listName", modelName), null, false));
        		File targetLocation = new File(filePath + "header");
				if (!targetLocation.exists()) {
					targetLocation.mkdir();
				}
				String excelFileName = CommonUtil.getAbsoulateFileName(mappingElement.getString("fileName"))+".xls";
				File serverFile = new File(filePath + CommonUtil.getFileExtension(mappingElement.getString("fileName")) + File.separator + excelFileName);
				UtilMisc.copyFile(serverFile, new File(targetLocation.getAbsolutePath() + File.separator + excelFileName));
				
        	}
        	
        	// store Range [start]
        	
        	String startRange[] = request.getParameterValues("startRange");
        	String endRange[] = request.getParameterValues("endRange");
        	
        	// Clean ModelDefault range first
        	int deleteCount = delegator.removeByAnd("EtlModelDefaultRange", UtilMisc.toMap("modelName", modelName));
        	Debug.logInfo("Delete count EtlModelDefaultRange> "+deleteCount, module);
        	
        	if (UtilValidate.isNotEmpty(startRange)) {
        		
        		for (int i = 0; i < startRange.length; i++) {
        			
        			GenericValue modelDefaultRange = EntityUtil.getFirst( delegator.findByAnd("EtlModelDefaultRange", UtilMisc.toMap("modelName", modelName, "sequenceNum", new Long(i+1)), null, false) );
        			
        			Long start = UtilValidate.isNotEmpty(startRange[i]) ? new Long(startRange[i]) : new Long(0);
        			Long end = UtilValidate.isNotEmpty(endRange[i]) ? new Long(endRange[i]) : new Long(0);
        			
        			if (UtilValidate.isNotEmpty(modelDefaultRange)) {
        				
        				modelDefaultRange.put("startRange", UtilValidate.isNotEmpty(startRange[i]) ? new Long(startRange[i]) : new Long(0) );
            			modelDefaultRange.put("endRange", UtilValidate.isNotEmpty(endRange[i]) ? new Long(endRange[i]) : new Long(0));
        				
            			modelDefaultRange.store();
            			
        			} else if (start < end) {
        				
        				modelDefaultRange = delegator.makeValue("EtlModelDefaultRange", UtilMisc.toMap("modelName", modelName, "sequenceNum", new Long(i+1)));
            			
            			modelDefaultRange.put("startRange", start);
            			modelDefaultRange.put("endRange", end);
            			
            			modelDefaultRange.create();
        				
        			}
        			
        		}
        		
        	}
        	
        	// store Range [end]
        	
        	// store SFTP information [start]
        	
        	String isSftpEnable = request.getParameter("etl_param_isSftpEnable");
        	
        	if (UtilValidate.isNotEmpty(isSftpEnable)) {
        		
        		String host = request.getParameter("sftp_host");
        		String userName = request.getParameter("sftp_userName");
        		String password = request.getParameter("sftp_password");
        		String port = request.getParameter("sftp_port");
        		String location = request.getParameter("sftp_location");
        		
        		GenericValue sftpConfig = EntityUtil.getFirst( delegator.findByAnd("SftpConfiguration", UtilMisc.toMap("modelName", modelName), null, false) );
        		if (UtilValidate.isEmpty(sftpConfig)) {
        			sftpConfig = delegator.makeValue("SftpConfiguration");
        			sftpConfig.put("seqId", delegator.getNextSeqId("SftpConfiguration"));
        			sftpConfig.put("modelName", modelName);
        			sftpConfig.put("userName", userName);
        			sftpConfig.put("password", password);
        			sftpConfig.put("port", port);
        			sftpConfig.put("host", host);
        			sftpConfig.put("location", location);
        			sftpConfig.create();
        		} else {
        			sftpConfig.put("userName", userName);
        			sftpConfig.put("password", password);
        			sftpConfig.put("port", port);
        			sftpConfig.put("host", host);
        			sftpConfig.put("location", location);
        			sftpConfig.store();
        		}
        		
        	} else {
        		delegator.removeByAnd("EtlModelDefaults", UtilMisc.toMap("modelName", modelName, "propertyName", "isSftpEnable"));
        	}
        	
        	// store SFTP information [end]
        	
        	resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            
            resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(EtlConstants.RESPONSE_MESSAGE, e.getMessage());
            
            return doJSONResponse(response, resp);
        }
        
        return doJSONResponse(response, resp);
    }
    
    @SuppressWarnings("unchecked")
    public static String updateEtlModelElementDefaults(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession(true);
        
        String modelName = request.getParameter("modelName");
        String elementName = request.getParameter("modelElementName");
        
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	DefaultValueUtil.storeModelElementDefaultValues(request, modelName, elementName);
        	
        	resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            
            resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(EtlConstants.RESPONSE_MESSAGE, e.getMessage());
            
            return doJSONResponse(response, resp);
        }
        
        return doJSONResponse(response, resp);
    }
    
    @SuppressWarnings("unchecked")
    public static String getEtlModelElementDefaults(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession(true);
        
        String modelName = request.getParameter("modelName");
        String elementName = request.getParameter("modelElementName");
        
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	List<GenericValue> modelElementDefaults = delegator.findByAnd("EtlModelElementDefault", UtilMisc.toMap("modelName", modelName, "elementName", elementName), null, false);
        	
        	return doJSONResponse(response, modelElementDefaults);
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            
            resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(EtlConstants.RESPONSE_MESSAGE, e.getMessage());
            
            return doJSONResponse(response, resp);
        }
        
    }
    
    @SuppressWarnings("unchecked")
    public static String getEtlModelElementFilters(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession(true);
        
        String modelName = request.getParameter("modelName");
        String elementName = request.getParameter("modelElementName");
        
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	List<GenericValue> modelElementFilters = delegator.findByAnd("EtlModelElementFilter", UtilMisc.toMap("modelName", modelName, "elementName", elementName), null, false);
        	
        	return doJSONResponse(response, modelElementFilters);
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            
            resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(EtlConstants.RESPONSE_MESSAGE, e.getMessage());
            
            return doJSONResponse(response, resp);
        }
        
    }
    
    @SuppressWarnings("unchecked")
    public static String updateEtlModelElementFilters(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession(true);
        
        String modelName = request.getParameter("modelName");
        String elementName = request.getParameter("modelElementName");
        
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	Enumeration params = request.getParameterNames();
        	
        	DefaultValueUtil.storeModelElementDefaultValues(request, modelName, elementName);
        	
        	// store Filter [start]
        	
        	String fieldName[] = request.getParameterValues("fieldName");
        	String condition[] = request.getParameterValues("condition");
        	String value[] = request.getParameterValues("value");
        	String operator[] = request.getParameterValues("operator");
        	
        	// Clean ModelDefault range first
        	int deleteCount = delegator.removeByAnd("EtlModelElementFilter", UtilMisc.toMap("modelName", modelName, "elementName", elementName));
        	Debug.logInfo("Delete count EtlModelElementFilter> "+deleteCount, module);
        	
        	if (UtilValidate.isNotEmpty(fieldName)) {
        		
        		for (int i = 0; i < fieldName.length; i++) {
        			
        			GenericValue modelElementFilter = EntityUtil.getFirst( delegator.findByAnd("EtlModelElementFilter", UtilMisc.toMap("modelName", modelName, "elementName", elementName, "sequenceNum", new Long(i+1)), null, false) );
        			
        			if (UtilValidate.isNotEmpty(modelElementFilter)) {
        				
        				modelElementFilter.put("fieldName", fieldName[i]);
        				modelElementFilter.put("filterCondition", condition[i]);
        				modelElementFilter.put("filterValue", value[i]);
        				modelElementFilter.put("operator", operator[i]);
        				
        				modelElementFilter.store();
            			
        			} else {
        				
        				modelElementFilter = delegator.makeValue("EtlModelElementFilter", UtilMisc.toMap("modelName", modelName, "elementName", elementName, "sequenceNum", new Long(i+1)));
            			
        				modelElementFilter.put("fieldName", fieldName[i]);
        				modelElementFilter.put("filterCondition", condition[i]);
        				modelElementFilter.put("filterValue", value[i]);
        				modelElementFilter.put("operator", operator[i]);
            			
        				modelElementFilter.create();
        				
        			}
        			
        		}
        		
        	}
        	
        	// store Filter [end]
        	
        	resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            
            resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(EtlConstants.RESPONSE_MESSAGE, e.getMessage());
            
            return doJSONResponse(response, resp);
        }
        
        return doJSONResponse(response, resp);
    }
    
    @SuppressWarnings("unchecked")
    public static String getEtlModelFilters(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession(true);
        
        String modelName = request.getParameter("modelName");
        
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	List<GenericValue> modelFilters = delegator.findByAnd("EtlModelFilter", UtilMisc.toMap("modelName", modelName), null, false);
        	
        	return doJSONResponse(response, modelFilters);
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            
            resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(EtlConstants.RESPONSE_MESSAGE, e.getMessage());
            
            return doJSONResponse(response, resp);
        }
        
    }
    
    @SuppressWarnings("unchecked")
    public static String getEtlModelNotifications(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession(true);
        
        String modelName = request.getParameter("modelName");
        
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	List<GenericValue> modelNotifications = delegator.findByAnd("EtlModelNotification", UtilMisc.toMap("modelName", modelName), null, false);
        	
        	return doJSONResponse(response, modelNotifications);
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            
            resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(EtlConstants.RESPONSE_MESSAGE, e.getMessage());
            
            return doJSONResponse(response, resp);
        }
        
    }
    
    @SuppressWarnings("unchecked")
    public static String updateEtlModelFilters(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession(true);
        
        String modelName = request.getParameter("modelName");
        
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	Enumeration params = request.getParameterNames();
        	
        	// store Filter [start]
        	
        	String fieldName[] = request.getParameterValues("fieldName");
        	String condition[] = request.getParameterValues("condition");
        	String value[] = request.getParameterValues("value");
        	String operator[] = request.getParameterValues("operator");
        	
        	// Clean ModelDefault range first
        	int deleteCount = delegator.removeByAnd("EtlModelFilter", UtilMisc.toMap("modelName", modelName));
        	Debug.logInfo("Delete count EtlModelFilter> "+deleteCount, module);
        	
        	if (UtilValidate.isNotEmpty(fieldName)) {
        		
        		for (int i = 0; i < fieldName.length; i++) {
        			
        			GenericValue modelFilter = EntityUtil.getFirst( delegator.findByAnd("EtlModelFilter", UtilMisc.toMap("modelName", modelName, "sequenceNum", new Long(i+1)), null, false) );
        			
        			if (UtilValidate.isNotEmpty(modelFilter)) {
        				
        				modelFilter.put("fieldName", fieldName[i]);
        				modelFilter.put("filterCondition", condition[i]);
        				modelFilter.put("filterValue", value[i]);
        				modelFilter.put("operator", operator[i]);
        				
        				modelFilter.store();
            			
        			} else {
        				
        				modelFilter = delegator.makeValue("EtlModelFilter", UtilMisc.toMap("modelName", modelName, "sequenceNum", new Long(i+1)));
            			
        				modelFilter.put("fieldName", fieldName[i]);
        				modelFilter.put("filterCondition", condition[i]);
        				modelFilter.put("filterValue", value[i]);
        				modelFilter.put("operator", operator[i]);
            			
        				modelFilter.create();
        				
        			}
        			
        		}
        		
        	}
        	
        	// store Filter [end]
        	
        	resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            
            resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(EtlConstants.RESPONSE_MESSAGE, e.getMessage());
            
            return doJSONResponse(response, resp);
        }
        
        return doJSONResponse(response, resp);
    }
    
    @SuppressWarnings("unchecked")
    public static String updateEtlModelNotifications(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession(true);
        
        String modelName = request.getParameter("modelName");
        
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	Enumeration params = request.getParameterNames();
        	
        	// store Filter [start]
        	
        	String reason[] = request.getParameterValues("reason");
        	String subject[] = request.getParameterValues("subject");
        	String content[] = request.getParameterValues("content");
        	String fromString[] = request.getParameterValues("fromString");
        	String toString[] = request.getParameterValues("toString");
        	String ccString[] = request.getParameterValues("ccString");
        	String bccString[] = request.getParameterValues("bccString");
        	
        	// Clean ModelDefault range first
        	int deleteCount = delegator.removeByAnd("EtlModelNotification", UtilMisc.toMap("modelName", modelName));
        	Debug.logInfo("Delete count EtlModelNotification> "+deleteCount, module);
        	
        	if (UtilValidate.isNotEmpty(reason)) {
        		
        		for (int i = 0; i < reason.length; i++) {
        			
        			GenericValue modelNotification = EntityUtil.getFirst( delegator.findByAnd("EtlModelNotification", UtilMisc.toMap("modelName", modelName, "sequenceNum", new Long(i+1)), null, false) );
        			
        			if (UtilValidate.isNotEmpty(modelNotification)) {
        				
        				modelNotification.put("reason", reason[i]);
        				modelNotification.put("subject", subject[i]);
        				modelNotification.put("content", content[i]);
        				modelNotification.put("fromString", fromString[i]);
        				modelNotification.put("toString", toString[i]);
        				modelNotification.put("ccString", ccString[i]);
        				modelNotification.put("bccString", bccString[i]);
        				
        				modelNotification.store();
            			
        			} else {
        				
        				modelNotification = delegator.makeValue("EtlModelNotification", UtilMisc.toMap("modelName", modelName, "sequenceNum", new Long(i+1)));
            			
        				modelNotification.put("reason", reason[i]);
        				modelNotification.put("subject", subject[i]);
        				modelNotification.put("content", content[i]);
        				modelNotification.put("fromString", fromString[i]);
        				modelNotification.put("toString", toString[i]);
        				modelNotification.put("ccString", ccString[i]);
        				modelNotification.put("bccString", bccString[i]);
            			
        				modelNotification.create();
        				
        			}
        			
        		}
        		
        	}
        	
        	// store Filter [end]
        	
        	resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            
            resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(EtlConstants.RESPONSE_MESSAGE, e.getMessage());
            
            return doJSONResponse(response, resp);
        }
        
        return doJSONResponse(response, resp);
    }
    
	public static String autoMapElements(HttpServletRequest request, HttpServletResponse response) {
		
		String tableName = request.getParameter("tableName");
		String listName = request.getParameter("listName");
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<GenericValue> columnList = new ArrayList<GenericValue>();
		List commonList = new ArrayList();
		
		try {
			if (UtilValidate.isNotEmpty(tableName)) {
				
				// clean previous mapping elements
				resetMappingElement(delegator, listName);
				
				columnList = delegator.findByAnd("EtlDestination", UtilMisc.toMap("tableName", tableName), null, false);
				for (GenericValue g : columnList) {
					Map tempMap = new HashMap();
					List<GenericValue> eltDefaultConfig = delegator.findByAnd("EtlDefaultsConfig",
							UtilMisc.toMap("etlFieldName", g.getString("etlFieldName"), "etlTableName", tableName),
							null, false);
					List etlDefList = new ArrayList();
					if (UtilValidate.isNotEmpty(eltDefaultConfig)) {

						for (GenericValue gg : eltDefaultConfig) {
							Map tempMap1 = new HashMap();
							tempMap1.put("seqIdConfig", gg.getString("seqId"));
							tempMap1.put("etlFieldNameConfig", gg.getString("etlFieldName"));
							tempMap1.put("defaultValue", gg.getString("defaultValue"));
							tempMap1.put("etlTableName", gg.getString("etlTableName"));
							if (UtilValidate.isNotEmpty(tempMap1)) {
								etlDefList.add(tempMap1);
							}

						}
					}
					tempMap.put("etlDefList", etlDefList);
					tempMap.put("etlDefListSize", etlDefList.size());
					tempMap.put("seqId", g.getString("seqId"));
					tempMap.put("tableName", g.getString("tableName"));
					tempMap.put("etlFieldName", g.getString("etlFieldName"));
					tempMap.put("tableTitle", g.getString("tableTitle"));
					tempMap.put("isPrime", g.getString("isPrime"));
					commonList.add(tempMap);
					
					// Auto map [start]
					
					GenericValue mappedElement = EntityUtil.getFirst( delegator.findByAnd("EtlMappingElements", UtilMisc.toMap("listName", listName, "etlFieldName", g.getString("etlFieldName")), null, false) );
					
					if (UtilValidate.isNotEmpty(mappedElement)) {
						mappedElement.set("tableName", tableName);
						mappedElement.set("tableColumnName", g.getString("etlFieldName"));
						
						if (UtilValidate.isEmpty(mappedElement.getString("etlCustomFieldName"))) {
							mappedElement.set("etlCustomFieldName", g.getString("etlFieldName"));
						}
						
						mappedElement.store();
					}
					
					// Auto map [end]
					
					
				}
				
				
			}

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			return "error";

		}
		return doJSONResponse(response, commonList);
	}
	
	public static String removeAllMapping(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String listName = request.getParameter("listName");
		Map trueOrFalse =  new HashMap();
		String set="N";
		if(UtilValidate.isNotEmpty(listName))
		{
			set = resetMappingElement(delegator, listName);
			
		}
		trueOrFalse.put("set", set);
		return doJSONResponse(response, trueOrFalse);
	}
	
	private static String resetMappingElement(Delegator delegator, String listName) {
		
		String set="N";
		List<GenericValue> etlMappingElements;
		
		try {
			
			if (UtilValidate.isNotEmpty(listName)) {
				
				etlMappingElements = delegator.findByAnd("EtlMappingElements", UtilMisc.toMap("listName", listName), null, false);
				if(UtilValidate.isNotEmpty(etlMappingElements))
				{
					for(GenericValue g:etlMappingElements)
					{
						g.set("tableName", "");
						g.set("tableColumnName", "");
					}
					delegator.storeAll(etlMappingElements);
					set="Y";
				}
				
				//finally migratted table need to be empty
				List<GenericValue> etlSourceTable = delegator.findByAnd("EtlSourceTable", UtilMisc.toMap("listName",  listName), null, false);
				if(UtilValidate.isNotEmpty(etlSourceTable))
				{
					delegator.removeAll(etlSourceTable);
				}
				//end of empty to be migrated table
				
			}
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return set;
	}
	
	@SuppressWarnings("unchecked")
    public static String getEtlModels(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession(true);
        
        String modelType = request.getParameter("modelType");
        
        Map<String, Object> modelList = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();
        
        try {
        	
        	if (UtilValidate.isNotEmpty(modelType)) {
        		
        		EntityCondition conditions = null;
            	
            	conditions = EntityCondition.makeCondition("tableName", EntityOperator.EQUALS, modelType);
            	
            	List<GenericValue> etlSourceTable = delegator.findList("EtlSourceTable", conditions, null, null, null, false);
    			if(UtilValidate.isNotEmpty(etlSourceTable)){
    				List<String> listNameList = EntityUtil.getFieldListFromEntityList(etlSourceTable, "listName", true);
    				
    				if (UtilValidate.isNotEmpty(listNameList)) {
    					for (String modelName : listNameList) {
    						GenericValue etlModel = EntityUtil.getFirst( delegator.findByAnd("EtlModel", UtilMisc.toMap("modelName", modelName), null, true) );
    						if (UtilValidate.isNotEmpty(etlModel) && UtilValidate.isEmpty(etlModel.getString("isExport"))) {
    							modelList.put(etlModel.getString("modelId"), modelName);
    						}
    					}
    				}
    				
    			}
    			
    			resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
    			resp.put("modelList", modelList);
        		
        	} else {
        		
        		resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
        		resp.put(EtlConstants.RESPONSE_MESSAGE, "Model Type cant be empty!!");
        	}
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            
            resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(EtlConstants.RESPONSE_MESSAGE, e.getMessage());
        }
        
        return doJSONResponse(response, resp);
    }
	
	@SuppressWarnings("unchecked")
	public static String approvedSelectedLead(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		//String groupId = request.getParameter("groupId");
		String rowsSelected[] = request.getParameterValues("rowsSelected[]");

		Map<String, Object> resp = new HashMap<String, Object>();
		List<GenericValue> importDatas = new ArrayList<GenericValue>();

		int successCount = 0;
		int alreadyExistsCount = 0;
		
		try {

			if (UtilValidate.isNotEmpty(rowsSelected)) {
				
				for (int i = 0; i < rowsSelected.length; i++) {
					String leadId = rowsSelected[i];
					
					EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("leadId", EntityOperator.EQUALS, leadId),
							EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_NOT_APPROVED"),
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_REJECTED")
									)
							);
					
					GenericValue associatedEntity = EntityUtil.getFirst( delegator.findList("DataImportLead", conditions, null, null, null, false) );
					
					if (UtilValidate.isNotEmpty(associatedEntity)) {
						
						associatedEntity.put("importStatusId", "DATAIMP_APPROVED");
						associatedEntity.put("approvedByUserLoginId", userLogin.getString("userLoginId"));
						
						associatedEntity.store();
						
						importDatas.add(associatedEntity);
						
						successCount++;
					} else {
						alreadyExistsCount++;
					}
					
				}
						
			}
			
			resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
			resp.put("successCount", successCount);
			resp.put("alreadyExistsCount", alreadyExistsCount);
			
			if (successCount > 0) {
				Thread importThread = new Thread() {
					public void run() {
						System.out.println("Thread Running");
						
						try {
							
							Map<String, Object> inputNew = new HashMap<String, Object>();
							inputNew.put("userLogin", userLogin);
							inputNew.put("importDatas", importDatas);
							
							Map<String, Object> result = dispatcher.runSync("crmsfa.importLeads", inputNew);
							if (ServiceUtil.isSuccess(result)) {
								
							}
							
							/*Map<String, Object> inputNew = new HashMap<String, Object>();
							inputNew.put("organizationPartyId", "Company");
							inputNew.put("userLogin", userLogin);
							inputNew.put("importDatas", importDatas);
							
							Map<String, Object> result = dispatcher.runSync("importLeads", inputNew);
							if (ServiceUtil.isSuccess(result)) {
								int processCount = 0;
								if (UtilValidate.isNotEmpty(result.get("importedRecords"))) {
									processCount = Integer.parseInt(result.get("importedRecords").toString());
								}
								Debug.logInfo("total processCount: "+processCount, module);
								
								List<GenericValue> importedDataList = (List<GenericValue>) result.get("importedDataList");
								if (UtilValidate.isNotEmpty(importedDataList)) {
									
									List<String> leadIds = EntityUtil.getFieldListFromEntityList(importedDataList, "leadId", true);
									
									// assign responsible for [start]
									
									for (GenericValue importedData : importedDataList) {
										
										String responsibleForId = null;
										String leadId = importedData.getString("primaryPartyId");
										
										if (UtilValidate.isNotEmpty(importedData.getString("leadAssignTo")) && DataHelper.isResponsibleForParty(delegator, importedData.getString("leadAssignTo"))) {
											responsibleForId = importedData.getString("leadAssignTo");
										} else if (UtilValidate.isNotEmpty(importedData.getString("uploadedByUserLoginId"))) {
											GenericValue uploadedByUserLogin = EntityUtil.getFirst( delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", importedData.getString("uploadedByUserLoginId")), null, false) );
											if (DataHelper.isResponsibleForParty(delegator, uploadedByUserLogin.getString("partyId"))) {
												responsibleForId = uploadedByUserLogin.getString("partyId");
											}	
										}
										
										if (UtilValidate.isNotEmpty(responsibleForId)) {
											Map<String, Object> associationContext = new HashMap<String, Object>();
											associationContext.put("partyId", leadId);
											associationContext.put("roleTypeIdFrom", "LEAD");
											associationContext.put("accountPartyId", responsibleForId);
											associationContext.put("userLogin", userLogin);
											
											Map<String, Object> associationResult = dispatcher.runSync("crmsfa.updatePersonResponsible", associationContext);
											
											if (!ServiceUtil.isError(associationResult)) {
												Debug.logInfo("Successfully Changed Account Responsible For, leadPartyId="+leadId+", responsiblePartyId="+responsibleForId, module);
											}
										}
										
									}
									
									// assign responsible for [end]
									
									inputNew = new HashMap<String, Object>();
									inputNew.put("userLogin", userLogin);
									inputNew.put("leadIds", StringUtil.join(leadIds, ","));
									
									result = dispatcher.runSync("dataimporter.importLeadAssocs", inputNew);
									if (!ServiceUtil.isError(result)) {
										Debug.log("Successfully import lead associations....");
									}
									
								}
								
							}*/
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				};
				importThread.start();
			}
			
		} catch (Exception e) {
			
			resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(EtlConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), module);
		}
		
		return doJSONResponse(response, resp);
	}
	
	@SuppressWarnings("unchecked")
	public static String rejectedSelectedLead(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		//String groupId = request.getParameter("groupId");
		String rowsSelected[] = request.getParameterValues("rowsSelected[]");

		Map<String, Object> resp = new HashMap<String, Object>();

		int successCount = 0;
		int alreadyExistsCount = 0;
		
		try {

			if (UtilValidate.isNotEmpty(rowsSelected)) {
				
				for (int i = 0; i < rowsSelected.length; i++) {
					String leadId = rowsSelected[i];
					
					EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("leadId", EntityOperator.EQUALS, leadId),
							EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_APPROVED"),
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_NOT_APPROVED"),
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_ERROR"),
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED"),
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null)
									)
							);
					
					GenericValue associatedEntity = EntityUtil.getFirst( delegator.findList("DataImportLead", conditions, null, null, null, false) );
					
					if (UtilValidate.isNotEmpty(associatedEntity)) {
						
						associatedEntity.put("importStatusId", "DATAIMP_REJECTED");
						associatedEntity.put("rejectedByUserLoginId", userLogin.getString("userLoginId"));
						
						associatedEntity.store();
						
						successCount++;
					} else {
						alreadyExistsCount++;
					}
					
				}
						
			}
			
			resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
			resp.put("successCount", successCount);
			resp.put("alreadyExistsCount", alreadyExistsCount);
			
		} catch (Exception e) {
			
			resp.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(EtlConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), module);
		}
		
		return doJSONResponse(response, resp);
	}
    
}
