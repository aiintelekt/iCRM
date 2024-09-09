/**
 * 
 */
package org.groupfio.dyna.screen.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.fio.homeapps.util.UtilXml;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class SettingServiceImpl {

	private static final String MODULE = SettingServiceImpl.class.getName();
    
	public static String uploadDynaConfiguration(HttpServletRequest request, HttpServletResponse response) {
    	
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	
    	//String dynaConfigId = (String) context.get("dynaConfigId");
    	
        String uploadMode = request.getParameter("uploadMode");
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		
    		List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
    		
    		if (UtilValidate.isNotEmpty(multiparts)) {
    			
    			List<GenericValue> screenConfigList = null;
    			List<GenericValue> screenConfigFieldList = null;
    			List<GenericValue> screenConfigFieldDataList = null;
    			List<GenericValue> screenConfigFieldAttrList = null;
    			
    			for (FileItem item : multiparts) {
    				if (!item.isFormField()) {
    					Debug.log("Name from Item: "+item.getName()+" ");
    					
    					Document doc = UtilXml.readXmlDocument(item.getInputStream(), "");
    					
    					screenConfigList = UtilXml.getEntityList(delegator, doc, "DynaScreenConfig");
    					screenConfigFieldList = UtilXml.getEntityList(delegator, doc, "DynaScreenConfigField");
    					screenConfigFieldDataList = UtilXml.getEntityList(delegator, doc, "DynaScreenConfigFieldData");
    					screenConfigFieldAttrList = UtilXml.getEntityList(delegator, doc, "DynaScreenConfigFieldAttribute");
    					
    					Debug.log("----------------------------");
    				}
    				
    				if (item.isFormField()) {
    					String fName = item.getFieldName();
    					String fValue = item.getString();
    					if ("uploadMode".equals(fName)) {
    						uploadMode = fValue;
    					}
    				}
        		}
    			
    			if (UtilValidate.isNotEmpty(uploadMode)) {
    				if (uploadMode.equals("OVERIDE") || uploadMode.equals("REFRESH")) {
    					
    					if (uploadMode.equals("REFRESH")) {
    						delegator.removeAll("DynaScreenConfigFieldAttribute");
        					delegator.removeAll("DynaScreenConfigFieldData");
        					delegator.removeAll("DynaScreenConfigField");
        					delegator.removeAll("DynaScreenConfig");
    					}
    					
    					if (UtilValidate.isNotEmpty(screenConfigList)) {
    						delegator.storeAll(screenConfigList);
    					}
    					if (UtilValidate.isNotEmpty(screenConfigFieldList)) {
    						for (GenericValue field : screenConfigFieldList) {
    							if (UtilValidate.isNotEmpty(field.getString("dynaConfigId")) && UtilValidate.isNotEmpty(field.getString("dynaFieldId"))) {
    								long count = delegator.findCountByCondition("DynaScreenConfig", EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, field.getString("dynaConfigId")), null, null, null);
        							if (count > 0) {
        								delegator.createOrStore(field);
        							}
    							}
    						}
    					}
    					if (UtilValidate.isNotEmpty(screenConfigFieldDataList)) {
    						for (GenericValue fieldData : screenConfigFieldDataList) {
    							if (UtilValidate.isNotEmpty(fieldData.getString("dynaConfigId")) && UtilValidate.isNotEmpty(fieldData.getString("dynaFieldId"))
    									&& UtilValidate.isNotEmpty(fieldData.getString("dynaFieldDataId"))
    									) {
    								long count = delegator.findCountByCondition("DynaScreenConfigField", EntityCondition.makeCondition(
    		                                EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, fieldData.getString("dynaConfigId")),
    		                                EntityOperator.AND,
    		                                EntityCondition.makeCondition("dynaFieldId", EntityOperator.EQUALS, fieldData.getString("dynaFieldId"))
    		                           ), null, null, null);
        							if (count > 0) {
        								delegator.createOrStore(fieldData);
        							}
    							}
    						}
    					}
    					if (UtilValidate.isNotEmpty(screenConfigFieldAttrList)) {
    						for (GenericValue fieldAttr : screenConfigFieldAttrList) {
    							if (UtilValidate.isNotEmpty(fieldAttr.getString("dynaConfigId")) && UtilValidate.isNotEmpty(fieldAttr.getString("dynaFieldId"))
    									&& UtilValidate.isNotEmpty(fieldAttr.getString("attrName"))
    									) {
    								long count = delegator.findCountByCondition("DynaScreenConfigField", EntityCondition.makeCondition(
    		                                EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, fieldAttr.getString("dynaConfigId")),
    		                                EntityOperator.AND,
    		                                EntityCondition.makeCondition("dynaFieldId", EntityOperator.EQUALS, fieldAttr.getString("dynaFieldId"))
    		                           ), null, null, null);
        							if (count > 0) {
        								delegator.createOrStore(fieldAttr);
        							}
    							}
    						}
    					}
    				} else if (uploadMode.equals("DEL_LOAD")) {
    					
    					//TransactionUtil.begin();
    					
    					if (UtilValidate.isNotEmpty(screenConfigList)) {
    						for (GenericValue config : screenConfigList) {
    							if (UtilValidate.isNotEmpty(config.getString("dynaConfigId"))) {
    								delegator.removeByAnd("DynaScreenConfigFieldAttribute", UtilMisc.toMap("dynaConfigId", config.getString("dynaConfigId")));
    								delegator.removeByAnd("DynaScreenConfigFieldData", UtilMisc.toMap("dynaConfigId", config.getString("dynaConfigId")));
    								delegator.removeByAnd("DynaScreenConfigField", UtilMisc.toMap("dynaConfigId", config.getString("dynaConfigId")));
    								delegator.removeByAnd("DynaScreenConfig", UtilMisc.toMap("dynaConfigId", config.getString("dynaConfigId")));
    							}
    						}
    					}
    					
    					//TransactionUtil.commit();
    					
    					if (UtilValidate.isNotEmpty(screenConfigList)) {
    						for (GenericValue config : screenConfigList) {
    							if (UtilValidate.isNotEmpty(config.getString("dynaConfigId"))) {
        							delegator.createOrStore(config);
    							}
    						}
    					}
    					
    					if (UtilValidate.isNotEmpty(screenConfigFieldList)) {
    						for (GenericValue field : screenConfigFieldList) {
    							if (UtilValidate.isNotEmpty(field.getString("dynaConfigId")) && UtilValidate.isNotEmpty(field.getString("dynaFieldId"))) {
        							delegator.createOrStore(field);
    							}
    						}
    					}
    					
    					if (UtilValidate.isNotEmpty(screenConfigFieldDataList)) {
    						for (GenericValue fieldData : screenConfigFieldDataList) {
    							if (UtilValidate.isNotEmpty(fieldData.getString("dynaConfigId")) && UtilValidate.isNotEmpty(fieldData.getString("dynaFieldId"))
    									&& UtilValidate.isNotEmpty(fieldData.getString("dynaFieldDataId"))
    									) {
        							delegator.createOrStore(fieldData);
    							}
    						}
    					}
    					
    					if (UtilValidate.isNotEmpty(screenConfigFieldAttrList)) {
    						for (GenericValue fieldAttr : screenConfigFieldAttrList) {
    							if (UtilValidate.isNotEmpty(fieldAttr.getString("dynaConfigId")) && UtilValidate.isNotEmpty(fieldAttr.getString("dynaFieldId"))
    									&& UtilValidate.isNotEmpty(fieldAttr.getString("attrName"))
    									) {
        							delegator.createOrStore(fieldAttr);
    							}
    						}
    					}
    				}
    			}
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		request.setAttribute("_ERROR_MESSAGE_", "File Upload Failed"+e.getMessage());
    		Debug.logError(e.getMessage(), MODULE);
			return "error";
		}
    	
    	request.setAttribute("_EVENT_MESSAGE_", "Successfully import Dyna configuration..");
    	
    	return "success";
    	
    }
    
}
