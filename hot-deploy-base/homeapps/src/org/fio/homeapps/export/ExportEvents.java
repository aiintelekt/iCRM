package org.fio.homeapps.export;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.export.ExportConstants.ExportType;
import org.fio.homeapps.export.ExportConstants.ScheduleExportStatus;
import org.fio.homeapps.export.resolver.Resolver;
import org.fio.homeapps.export.resolver.ResolverConstants.ResolverType;
import org.fio.homeapps.export.resolver.ResolverFactory;
import org.fio.homeapps.util.DataHelper;
import org.fio.homeapps.util.ResponseUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class ExportEvents {
		
	private static final String MODULE = ExportEvents.class.getName();
	
	@SuppressWarnings("unchecked")
	public static String exportData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> results = new HashMap<String, Object>();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String externalLoginKey = (String) session.getAttribute("externalLoginKey");
		
		String reportType = request.getParameter("reportType");
		String exportDataType = request.getParameter("exportDataType");
		
		String gridInstanceId = request.getParameter("gridInstanceId");
		String expFileTemplateId = request.getParameter("expFileTemplateId");
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			reportType = UtilValidate.isNotEmpty(reportType) ? reportType.toLowerCase() : ExportType.EXPORT_TYPE_CSV.toLowerCase();
			
			String processName = DataHelper.sqlPropToJavaProp(exportDataType);
			String fileName = processName+"_"+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "." + reportType;
			
			TransactionUtil.begin(20000);
			String jobId = delegator.getNextSeqId("ExportJobSandbox");
			GenericValue ejs = delegator.makeValue("ExportJobSandbox",
			        UtilMisc.toMap("jobId", jobId));
			
			//ejs.put("reportCount", Long.parseLong(""+productList.size()));
			ejs.put("jobName", processName+" export");
			ejs.put("reportType", reportType);
			//ejs.put("delimiter", delimiter);
			ejs.put("exportType", exportDataType);
			ejs.put("startDateTime", UtilDateTime.nowTimestamp());
			ejs.put("statusId", ScheduleExportStatus.PROGRESSING);
			ejs.put("createdByUserLogin", userLogin.getString("userLoginId"));
			
			ejs.create();
			TransactionUtil.commit();
			
			request.setAttribute("isExportAction", "Y");
			
			CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
			//Thread t1 = new Thread(() -> {	
				System.out.println(processName+ " export, Current thread: " + Thread.currentThread().getId());
				
				try {
					String delimiter = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "grid.export.delimiter", "|");
					String location = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "local.export.location");
					
					// Load data [start]
					
					Integer resultListSize = new Integer(0);
					List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> resultList = FastList.newInstance();
					String errorMessage = null;
					List<String> headers = new ArrayList<>();
					boolean isHeaderRequird = true;
					
					Map<String, Object> dataContext = new LinkedHashMap<>();
					dataContext.put("delegator", delegator);
					dataContext.put("request", request);
					dataContext.put("response", response);
					dataContext.put("exportDataType", exportDataType);
					Resolver resolver = ResolverFactory.getResolver(ResolverType.EXP_DATA_RESOLVER);
					Map<String, Object> dataResponse = resolver.resolve(dataContext);
					if (ResponseUtils.isSuccess(dataResponse)) {
						resultListSize = (Integer) dataResponse.get("resultListSize");
						dataList = (List<Map<String, Object>>) dataResponse.get("dataList");
						errorMessage = (String) dataResponse.get("errorMessage");
					}
					
					// Load data [end]
					Map<String, Object> exportRequest = new LinkedHashMap<>();
					exportRequest.put("expFileTemplateId", expFileTemplateId);
					exportRequest.put("delimiter", delimiter);
					exportRequest.put("dataList", dataList);
					
					Map<String, Object> exportResponse = ExportUtil.populateExportData(delegator, exportRequest);
					headers = (List<String>) exportResponse.get("headers");
					resultList = (List<Map<String, Object>>) exportResponse.get("resultList");
					delimiter = (String) exportResponse.get("delimiter");
					
					TransactionUtil.begin(20000);
					ejs.put("delimiter", delimiter);
					ejs.store();
					TransactionUtil.commit();
					
					Map<String, Object> exportContext = new HashMap<String, Object>();
					
					exportContext.put("delegator", delegator);
					exportContext.put("rows", resultList);
					exportContext.put("headers", headers);
					exportContext.put("fileName", fileName);
					exportContext.put("location", location);
					exportContext.put("delimiter", delimiter);
					exportContext.put("isHeaderRequird", isHeaderRequird);
					
					exportContext.put("exportType", ExportType.EXPORT_TYPE_CSV);
					
					ExporterFacade.exportReport(exportContext);
					
					ejs.put("genFileName", fileName);
					ejs.put("reportCount", Long.parseLong(""+resultList.size()));
					if (UtilValidate.isNotEmpty(errorMessage)) {
						ejs.put("errorMessage", errorMessage);
		            	ejs.put("statusId", ScheduleExportStatus.ERROR);
					} else if (UtilValidate.isEmpty(resultList)) {
						ejs.put("errorMessage", "No data found");
		            	ejs.put("statusId", ScheduleExportStatus.ERROR);
					} else {
						ejs.put("statusId", ScheduleExportStatus.FINISHED);
					}
					ejs.put("finishDateTime", UtilDateTime.nowTimestamp());
					ejs.store();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
	        });
			
			Thread.sleep(2000);
			
		} catch (Exception e) {e.printStackTrace();
			Debug.logError("Error : "+e.getMessage(), MODULE);
			return "error";
		}
		return "success";
	}
	
}
