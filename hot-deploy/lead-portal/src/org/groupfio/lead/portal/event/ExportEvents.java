package org.groupfio.lead.portal.event;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class ExportEvents {
		
	private static final String MODULE = ExportEvents.class.getName();
	
	@SuppressWarnings("unchecked")
	public static String exportBatchError(HttpServletRequest request,HttpServletResponse response) throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");

		String exportType = request.getParameter("exportType");
		String exportFileType = request.getParameter("exportFileType");
		
		String executionId = request.getParameter("executionId");
		String exitType = request.getParameter("exitType");
		
		try {
			
			String sortDir = "DESC";
			String orderField = "createdStamp";
			
			List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
			EntityCondition executionCondition = null;
			
			if (UtilValidate.isNotEmpty(executionId) && UtilValidate.isNotEmpty(exitType)) {
				if (exitType.equals("job")) {
					executionCondition = EntityCondition.makeCondition("jobExecutionId", EntityOperator.EQUALS, new Long(executionId));
				} else if (exitType.equals("step")) {
					executionCondition = EntityCondition.makeCondition("stepExecutionId", EntityOperator.EQUALS, new Long(executionId));
				}
				
				if (UtilValidate.isNotEmpty(executionCondition)) {
					conditionsList.add(executionCondition);
				}
			}
			
			if (UtilValidate.isNotEmpty(exportFileType) && exportFileType.equals("IMPORT_FILE")) {
				List<String> errorTypes = new ArrayList<String>();
				errorTypes.add("PARSING");
				errorTypes.add("VALIDATION");
				conditionsList.add(EntityCondition.makeCondition("errorType", EntityOperator.IN, errorTypes));
			}
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			
			List<GenericValue> errorList = delegator.findList("BatchStepErrorLog", mainConditons, null, UtilMisc.toList(orderField+ " " + sortDir), null, false);

			if (UtilValidate.isNotEmpty(errorList)) {
				
				String fileName = "BatchErrorExport_"+exitType+"_"+executionId+"_"+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
				String delimiter = ",";
				boolean isHeaderRequird = true;
				
				if (UtilValidate.isNotEmpty(exportFileType) && exportFileType.equals("IMPORT_FILE")) {
					GenericValue batchStepExecution = EntityUtil.getFirst( delegator.findList("BatchStepExecution", executionCondition, 
							UtilMisc.toSet("fileTemplateId"), null, null, false) );
					if (UtilValidate.isNotEmpty(batchStepExecution)) {
						GenericValue fileTemplate = delegator.findOne("FileTemplate", UtilMisc.toMap("fileTemplateId", batchStepExecution.getString("fileTemplateId")), false);
						if (UtilValidate.isNotEmpty(fileTemplate)) {
							delimiter = UtilValidate.isNotEmpty(fileTemplate.getString("delimeter")) ? fileTemplate.getString("delimeter") : ",";
							boolean falseVal=false;
							isHeaderRequird = UtilValidate.isNotEmpty(fileTemplate.getString("isHeaderRequird")) && fileTemplate.getString("isHeaderRequird").equals("N") ? falseVal : true;
							String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date())+"T"+new SimpleDateFormat("hhmmss").format(new Date());
							fileName = fileTemplate.getString("fileName").replace("{CURRENT_DATE}", currentDate) + "." + fileTemplate.getString("fileType");
						}
					}
				}
				
				List<Map<String, Object>> resultList = FastList.newInstance();
				
				for (GenericValue error : errorList) {
					
					Map<String, Object> result = new LinkedHashMap<String,Object>();
					
					if (UtilValidate.isNotEmpty(exportFileType) && exportFileType.equals("IMPORT_FILE")) {
						
						result.put("data", error.getString("rawData"));
						
					} else {
						result.put("Id", error.getString("batchStepErrorLogId"));
						result.put("Create Time", UtilValidate.isNotEmpty(error.get("createdStamp")) ? UtilDateTime.timeStampToString(error.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
						result.put("Error Message", error.getString("errorMessage"));
					}
					
					resultList.add(result);
					
				}
				
				String location = EntityUtilProperties.getPropertyValue("admin-portal.properties", "local.export.location", delegator);
				
				Map<String, Object> exportContext = new HashMap<String, Object>();
				
				exportContext.put("delegator", delegator);
				exportContext.put("rows", resultList);
				exportContext.put("headers", null);
				exportContext.put("fileName", fileName);
				exportContext.put("location", location);
				exportContext.put("delimiter", delimiter);
				exportContext.put("isHeaderRequird", isHeaderRequird);
				
				exportContext.put("exportType", exportType);
				
				ExporterFacade.exportReport(exportContext);
				
				Thread.sleep(1000);
				String rootPath = location;
				String filePath = location+File.separatorChar+fileName;
				
				ExporterFacade.downloadReport(request, response, filePath, exportType);
				
				boolean isdelete = true;
				if(isdelete) {
					File file = new File(filePath);
					if (file.getCanonicalPath().startsWith(new File(rootPath).getCanonicalPath())) {
						file.delete();
					}
				}
			}
		} catch (Exception e) {
			Debug.logError("Error : "+e.getMessage(), MODULE);
			return "error";
		}

		return "success";
	}
}
