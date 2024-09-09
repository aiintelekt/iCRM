package org.groupfio.common.portal.event;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.export.ExporterFacade;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class ExportEvents {
		
	private static final String MODULE = ExportEvents.class.getName();
	
	@SuppressWarnings("unchecked")
	public static String exportBatchError(HttpServletRequest request,HttpServletResponse response) throws GenericEntityException {

		HttpSession session = request.getSession();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

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
							isHeaderRequird = UtilValidate.isNotEmpty(fileTemplate.getString("isHeaderRequird")) && fileTemplate.getString("isHeaderRequird").equals("N") ? false : true;
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
				
				if (new File(filePath).getCanonicalPath().startsWith(new File(rootPath).getCanonicalPath())) {
					ExporterFacade.downloadReport(request, response, filePath, exportType);
					
					boolean isdelete = true;
					if(isdelete) {
						File file = new File(filePath);
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
	
	public static String searchExportFiles(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		Map < String, Object > context = UtilHttp.getCombinedMap(request);
		
		String externalLoginKey = request.getParameter("externalLoginKey");

		//String partyId = (String) context.get("partyId");
		
        String orderByColumn = (String) context.get("orderByColumn");
		
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		
		long start = System.currentTimeMillis();
		int viewIndex = 0;
		int highIndex = 0;
		int lowIndex = 0;
		int resultListSize = 0;
		int viewSize = 0;
		
		try {
			
			String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);
			
			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			
			/*String businessUnit = null;
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "Read");
				accessMatrixMap.put("entityName", "WorkEffort");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
			}*/

			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				
				Map<String, Object> callCtxt = FastMap.newInstance();
	            Map<String, Object> callResult = FastMap.newInstance();
	            
	            Map<String, Object> requestContext = new LinkedHashMap<>();
				requestContext.putAll(context);
	            
	            callCtxt.put("userLogin", userLogin);
	            
	            requestContext.put("totalGridFetch",
						org.groupfio.common.portal.util.DataUtil.getSystemPropertyValue(delegator, "general", "fio.grid.fetch.limit"));
	            if (UtilValidate.isEmpty(orderByColumn)) {
	            	requestContext.put("orderByColumn", orderByColumn);
	            }
	            
	            callCtxt.put("requestContext", requestContext);
				
	            callResult = dispatcher.runSync("exp.findExportFile", callCtxt);

				if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("dataList"))) {

					List<GenericValue> exportList = (List<GenericValue>) callResult.get("dataList");
					
					Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, exportList, "createdByUserLogin");
					
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					viewIndex = (int) callResult.get("viewIndex");
					highIndex = (int) callResult.get("highIndex");
					lowIndex = (int) callResult.get("lowIndex");
					resultListSize = (int) callResult.get("resultListSize");
					viewSize = (int) callResult.get("viewSize");
					
					int count = 0;
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					for (GenericValue exp : exportList) {
						//Debug.logInfo("data "+(count++)+": "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
						Map<String, Object> data = new HashMap<String, Object>();
						
						data.putAll(exp.getAllFields());
						
						data.put("createdBy", partyNames.get(exp.getString("createdByUserLogin")));
						
						data.put("startDateTime", UtilDateTime.timeStampToString(exp.getTimestamp("startDateTime"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));
						data.put("finishDateTime", UtilDateTime.timeStampToString(exp.getTimestamp("finishDateTime"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));
						data.put("cancelDateTime", UtilDateTime.timeStampToString(exp.getTimestamp("cancelDateTime"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));
						
						data.put("externalLoginKey", externalLoginKey);
						
						dataList.add(data);
						
					}
					Debug.logInfo("prepare actual data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
				}
			} else {
				Debug.log("error==");
				Map<String, Object> data = new HashMap<String, Object>();
				if(UtilValidate.isNotEmpty(accessMatrixRes) && !ServiceUtil.isSuccess(accessMatrixRes)) {
					data.put("errorMessage", accessMatrixRes.get("errorMessage").toString());
				} else {
					data.put("errorMessage", "Access Denied");
				}
				dataList.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		
		result.put("highIndex", Integer.valueOf(highIndex));
		result.put("lowIndex", Integer.valueOf(lowIndex));
		result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
		result.put("totalRecords", nf.format(resultListSize));
		result.put("recordCount", resultListSize);
		result.put("chunkSize", viewSize);
		result.put("viewSize", viewSize);
		result.put("viewIndex", viewIndex);
		
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
		result.put("timeTaken", (end - start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String downloadExportReport(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
        String jobId = request.getParameter("jobId");
        String externalLoginKey = request.getParameter("externalLoginKey");
        
        try {
        	
        	if (UtilValidate.isNotEmpty(jobId)) {
        		
        		GenericValue exportJobSandbox = EntityQuery.use(delegator).from("ExportJobSandbox")
    					.where("jobId", jobId).queryFirst();
        		
        		if (UtilValidate.isNotEmpty(exportJobSandbox) && UtilValidate.isNotEmpty(exportJobSandbox.getString("genFileName")) ) {
        			String location = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "local.export.location");
        			
        			String genFileName = exportJobSandbox.getString("genFileName");
        			String reportType = exportJobSandbox.getString("reportType");
        			
        			String filePath = location+File.separatorChar+genFileName;
    				
    				ExporterFacade.downloadReport(request, response, filePath, reportType);
        		}
			}
        	
        } catch (Exception e) {
        	e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			return "error";
        }
        return "success";
    }
	
}
