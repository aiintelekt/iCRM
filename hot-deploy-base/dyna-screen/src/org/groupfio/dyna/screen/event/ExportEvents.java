package org.groupfio.dyna.screen.event;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.fio.homeapps.export.ExporterFacade;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastList;
import org.ofbiz.base.util.*;
import org.w3c.dom.Document;

/**
 * @author Sharif
 *
 */
public class ExportEvents {
		
	private static final String module = ExportEvents.class.getName();
	
	@SuppressWarnings("unchecked")
	public static String exportDynaConfiguration(HttpServletRequest request,HttpServletResponse response) throws GenericEntityException {

		HttpSession session = request.getSession();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		String componentMountPoint = request.getParameter("componentMountPoint");
		String layoutType = request.getParameter("layoutType");
		String isPrimary = request.getParameter("isPrimary");
		
		String dynaConfigIds = request.getParameter("dynaConfigIds");
		
		try {
			
			List<GenericValue> dataList = new ArrayList<GenericValue>();
			
			List conditionList = FastList.newInstance();
			
			if (UtilValidate.isNotEmpty(componentMountPoint)) {
				conditionList.add(EntityCondition.makeCondition("componentMountPoint", EntityOperator.EQUALS, componentMountPoint));
			}
			if (UtilValidate.isNotEmpty(layoutType)) {
				conditionList.add(EntityCondition.makeCondition("layoutType", EntityOperator.EQUALS, layoutType));
			}
			if (UtilValidate.isNotEmpty(isPrimary)) {
				conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, isPrimary));
			}
			
			if (UtilValidate.isNotEmpty(dynaConfigIds)) {
				conditionList.add(EntityCondition.makeCondition("dynaConfigId", EntityOperator.IN, Arrays.asList(dynaConfigIds.split(","))));
			}
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			
			List<String> entityFields = delegator.getModelEntity("DynaScreenConfig").getAllFieldNames();
			entityFields.remove("createdStamp");
			entityFields.remove("createdTxStamp");
			entityFields.remove("lastUpdatedStamp");
			entityFields.remove("lastUpdatedTxStamp");
			
			Set<String> fieldToSelect = UtilMisc.toSet(entityFields);
			
			List<GenericValue> configList = delegator.findList("DynaScreenConfig", mainConditons, fieldToSelect, UtilMisc.toList("-createdStamp"), null, false);
			
			if (UtilValidate.isNotEmpty(configList)) {
				
				for (GenericValue config : configList) {
					dataList.add(config);
					String dynaConfigId = config.getString("dynaConfigId");
					
					conditionList = FastList.newInstance();
					
					conditionList.add(EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, dynaConfigId));
					
					mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					
					entityFields = delegator.getModelEntity("DynaScreenConfigField").getAllFieldNames();
					entityFields.remove("createdStamp");
					entityFields.remove("createdTxStamp");
					entityFields.remove("lastUpdatedStamp");
					entityFields.remove("lastUpdatedTxStamp");
					
					fieldToSelect = UtilMisc.toSet(entityFields);
					
					List<GenericValue> fieldList = delegator.findList("DynaScreenConfigField", mainConditons, fieldToSelect, UtilMisc.toList("-createdStamp"), null, false);
					
					if (UtilValidate.isNotEmpty(fieldList)) {
						dataList.addAll(fieldList);
						
						entityFields = delegator.getModelEntity("DynaScreenConfigFieldData").getAllFieldNames();
						entityFields.remove("createdStamp");
						entityFields.remove("createdTxStamp");
						entityFields.remove("lastUpdatedStamp");
						entityFields.remove("lastUpdatedTxStamp");
						
						fieldToSelect = UtilMisc.toSet(entityFields);
						
						for (GenericValue field : fieldList) {
							String dynaFieldId = field.getString("dynaFieldId");
							
							conditionList = FastList.newInstance();
							
							conditionList.add(EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, dynaConfigId));
							conditionList.add(EntityCondition.makeCondition("dynaFieldId", EntityOperator.EQUALS, dynaFieldId));
							
							mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							
							List<GenericValue> fieldDataList = delegator.findList("DynaScreenConfigFieldData", mainConditons, fieldToSelect, UtilMisc.toList("-createdStamp"), null, false);
							
							dataList.addAll(fieldDataList);
						}
						
						// field attributes export
						
						entityFields = delegator.getModelEntity("DynaScreenConfigFieldAttribute").getAllFieldNames();
						entityFields.remove("createdStamp");
						entityFields.remove("createdTxStamp");
						entityFields.remove("lastUpdatedStamp");
						entityFields.remove("lastUpdatedTxStamp");
						
						fieldToSelect = UtilMisc.toSet(entityFields);
						
						for (GenericValue field : fieldList) {
							String dynaFieldId = field.getString("dynaFieldId");
							
							conditionList = FastList.newInstance();
							
							conditionList.add(EntityCondition.makeCondition("dynaConfigId", EntityOperator.EQUALS, dynaConfigId));
							conditionList.add(EntityCondition.makeCondition("dynaFieldId", EntityOperator.EQUALS, dynaFieldId));
							
							mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							
							List<GenericValue> fieldDataList = delegator.findList("DynaScreenConfigFieldAttribute", mainConditons, fieldToSelect, UtilMisc.toList("-createdStamp"), null, false);
							
							dataList.addAll(fieldDataList);
						}
						
					}
				}
				
				String fileName = "dyna_config_export"+"_"+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"."+"xml";
				String exportType = "XML";
				
				Document xmlDoc = GenericValue.makeXmlDocument(dataList);
				
				String xmlContent = UtilXml.writeXmlDocument(xmlDoc);
				
				String location = EntityUtilProperties.getPropertyValue("dyna-screen.properties", "dyna.screen.export.location", delegator);
				
				String rootPath = location;
				String filePath = location+File.separatorChar+fileName;
				File file = new File(filePath);
				FileUtils.writeStringToFile(file, xmlContent, "UTF-8");
				
				Thread.sleep(1000);
				
				if (new File(filePath).getCanonicalPath().startsWith(new File(rootPath).getCanonicalPath())) {
					ExporterFacade.downloadReport(request, response, filePath, exportType);
					
					boolean isdelete = true;
					if(isdelete) {
						file.delete();
					}
				}
			}
		} catch (Exception e) {
			Debug.logError("Error : "+e.getMessage(), module);
			return "error";
		}

		return "success";
	}
	
}
