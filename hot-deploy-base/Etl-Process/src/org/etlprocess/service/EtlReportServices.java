package org.etlprocess.service;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.util.QueryUtil;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

import com.csvreader.CsvWriter;

import javolution.util.FastList;
import javolution.util.FastMap;

public class EtlReportServices {
	private static final String module = EtlReportServices.class.getName();

	/*
	 * public static String getExportReportData(HttpServletRequest request,
	 * HttpServletResponse response) { Delegator delegator = (Delegator)
	 * request.getAttribute("delegator"); LocalDispatcher dispatcher =
	 * (LocalDispatcher) request.getAttribute("dispatcher"); HttpSession session
	 * = request.getSession(); GenericValue userLogin =
	 * (GenericValue)session.getAttribute("userLogin"); Map<String,Object>
	 * result =FastMap.newInstance(); List<Object> data =
	 * FastList.newInstance(); String draw = request.getParameter("draw");
	 * String start = request.getParameter("start"); String length =
	 * request.getParameter("length"); String exportType =
	 * request.getParameter("exportType"); int recordsTotal = 0; int
	 * recordsFiltered = 0; SimpleDateFormat formatter = new
	 * SimpleDateFormat("MMMM dd,yyyy"); try{ Timestamp now
	 * =UtilDateTime.nowTimestamp(); int startIndex = Integer.parseInt(start)+1;
	 * int endIndex = Integer.parseInt(length); endIndex =
	 * endIndex+startIndex-1;
	 * 
	 * String selGroup="org.ofbiz"; SQLProcessor sqlProcessor = new
	 * SQLProcessor(delegator.getGroupHelperInfo(selGroup));
	 * List<Map<String,Object>> jobList = FastList.newInstance(); String
	 * sqlCommand = "Select * from Export_Job_Sandbox where export_type='"
	 * +exportType+"' order by start_Date_Time DESC ";
	 * 
	 * Debug.log(sqlCommand);
	 * 
	 * ResultSet rs = sqlProcessor.executeQuery(sqlCommand); int size= 0; if (rs
	 * != null){ rs.beforeFirst(); rs.last(); size = rs.getRow(); }
	 * 
	 * if(size < endIndex){ endIndex = size; }
	 * 
	 * 
	 * if (rs != null) {
	 * 
	 * if(startIndex >= endIndex){
	 * 
	 * rs.absolute(endIndex); Map<String,Object> params = FastMap.newInstance();
	 * String jobId = rs.getString("job_Id"); params.put("jobId", jobId);
	 * 
	 * jobList.add(params);
	 * 
	 * }else{ for (int i = startIndex; i<= endIndex; i++) { rs.absolute(i);
	 * Map<String,Object> params = FastMap.newInstance(); String jobId =
	 * rs.getString("job_Id"); params.put("jobId", jobId);
	 * 
	 * jobList.add(params); } } }
	 * 
	 * if(UtilValidate.isNotEmpty(jobList)){
	 * 
	 * recordsTotal = size; recordsFiltered =size; for(Map<String,Object> job :
	 * jobList){ String jobId = (String) job.get("jobId"); GenericValue jobInfo
	 * = delegator.findByPrimaryKey("ExportJobSandbox",UtilMisc.toMap("jobId",
	 * jobId)); Map<String,Object> paramValues = FastMap.newInstance();
	 * paramValues.put("jobId",jobInfo.getString("jobId"));
	 * paramValues.put("jobName",jobInfo.getString("jobName"));
	 * paramValues.put("exportType",jobInfo.getString("exportType"));
	 * paramValues.put("reportType",jobInfo.getString("reportType"));
	 * paramValues.put("reportCount",jobInfo.getString("reportCount"));
	 * paramValues.put("genFileName",jobInfo.getString("genFileName"));
	 * if("PENDING".equals(jobInfo.getString("statusId")))
	 * paramValues.put("statusId","Pending");
	 * if("FINISHED".equals(jobInfo.getString("statusId")))
	 * paramValues.put("statusId","Finished");
	 * if("PROGRESSING".equals(jobInfo.getString("statusId")))
	 * paramValues.put("statusId","Progressing");
	 * if("ERROR".equals(jobInfo.getString("statusId")))
	 * paramValues.put("statusId","Failure");
	 * paramValues.put("startDateTime",jobInfo.getString("startDateTime"));
	 * paramValues.put("finishDateTime",jobInfo.getString("finishDateTime"));
	 * paramValues.put("errorMessage",jobInfo.getString("errorMessage"));
	 * paramValues.put("isEmailSend",jobInfo.getString("isEmailSend"));
	 * paramValues.put("errorMessage",jobInfo.getString("errorMessage"));
	 * 
	 * 
	 * List<Map<String,Object>> loadData= new ArrayList<Map<String,Object>>();
	 * loadData.add(paramValues);
	 * 
	 * data.add(loadData); } }
	 * 
	 * result.put("draw", draw); result.put("data",data);
	 * result.put("recordsTotal", recordsTotal); result.put("recordsFiltered",
	 * recordsFiltered);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); result.put("data",data);
	 * result.put("draw", draw); result.put("recordsTotal", 0);
	 * result.put("recordsFiltered", 0); return
	 * org.opentaps.common.event.AjaxEvents.doJSONResponse(response, result); }
	 * return org.opentaps.common.event.AjaxEvents.doJSONResponse(response,
	 * result); } public static String
	 * getPhyInvExportReportData(HttpServletRequest request, HttpServletResponse
	 * response) { Delegator delegator = (Delegator)
	 * request.getAttribute("delegator"); LocalDispatcher dispatcher =
	 * (LocalDispatcher) request.getAttribute("dispatcher"); HttpSession session
	 * = request.getSession(); GenericValue userLogin =
	 * (GenericValue)session.getAttribute("userLogin"); Map<String,Object>
	 * result =FastMap.newInstance(); List<Object> data =
	 * FastList.newInstance(); String draw = request.getParameter("draw");
	 * String start = request.getParameter("start"); String length =
	 * request.getParameter("length"); String exportType =
	 * request.getParameter("exportType"); int recordsTotal = 0; int
	 * recordsFiltered = 0; SimpleDateFormat formatter = new
	 * SimpleDateFormat("MMMM dd,yyyy"); try{ Timestamp now
	 * =UtilDateTime.nowTimestamp(); int startIndex = Integer.parseInt(start)+1;
	 * int endIndex = Integer.parseInt(length); endIndex =
	 * endIndex+startIndex-1;
	 * 
	 * String selGroup="org.ofbiz"; SQLProcessor sqlProcessor = new
	 * SQLProcessor(delegator.getGroupHelperInfo(selGroup));
	 * List<Map<String,Object>> jobList = FastList.newInstance(); String
	 * sqlCommand =
	 * "Select * from Summary_Report_Download  order by created_Date DESC ";
	 * 
	 * Debug.log(sqlCommand);
	 * 
	 * ResultSet rs = sqlProcessor.executeQuery(sqlCommand); int size= 0; if (rs
	 * != null){ rs.beforeFirst(); rs.last(); size = rs.getRow(); }
	 * 
	 * if(size < endIndex){ endIndex = size; }
	 * 
	 * 
	 * if (rs != null) {
	 * 
	 * if(startIndex >= endIndex){
	 * 
	 * rs.absolute(endIndex); Map<String,Object> params = FastMap.newInstance();
	 * String jobId = rs.getString("path"); params.put("jobId", jobId);
	 * 
	 * jobList.add(params);
	 * 
	 * }else{ for (int i = startIndex; i<= endIndex; i++) { rs.absolute(i);
	 * Map<String,Object> params = FastMap.newInstance(); String jobId =
	 * rs.getString("path"); params.put("jobId", jobId);
	 * 
	 * jobList.add(params); } } }
	 * 
	 * if(UtilValidate.isNotEmpty(jobList)){
	 * 
	 * recordsTotal = size; recordsFiltered =size; for(Map<String,Object> job :
	 * jobList){ String jobId = (String) job.get("jobId"); GenericValue jobInfo
	 * =
	 * EntityUtil.getFirst(delegator.findByAnd("SummaryReportDownload",UtilMisc.
	 * toMap("path",jobId))); Map<String,Object> paramValues =
	 * FastMap.newInstance();
	 * paramValues.put("jobId",jobInfo.getString("path"));
	 * paramValues.put("jobName",jobInfo.getString("tabId"));
	 * paramValues.put("createdDate",jobInfo.getString("createdDate"));
	 * List<Map<String,Object>> loadData= new ArrayList<Map<String,Object>>();
	 * loadData.add(paramValues);
	 * 
	 * data.add(loadData); } }
	 * 
	 * result.put("draw", draw); result.put("data",data);
	 * result.put("recordsTotal", recordsTotal); result.put("recordsFiltered",
	 * recordsFiltered);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); result.put("data",data);
	 * result.put("draw", draw); result.put("recordsTotal", 0);
	 * result.put("recordsFiltered", 0); return
	 * org.opentaps.common.event.AjaxEvents.doJSONResponse(response, result); }
	 * return org.opentaps.common.event.AjaxEvents.doJSONResponse(response,
	 * result); } public static String getOrderErrorLogsData(HttpServletRequest
	 * request, HttpServletResponse response) { Delegator delegator =
	 * (Delegator) request.getAttribute("delegator"); LocalDispatcher dispatcher
	 * = (LocalDispatcher) request.getAttribute("dispatcher"); HttpSession
	 * session = request.getSession(); GenericValue userLogin =
	 * (GenericValue)session.getAttribute("userLogin"); Map<String,Object>
	 * result =FastMap.newInstance(); List<Object> data =
	 * FastList.newInstance(); String draw = request.getParameter("draw");
	 * String start = request.getParameter("start"); String length =
	 * request.getParameter("length"); String orderId =
	 * request.getParameter("orderId"); String channelId =
	 * request.getParameter("channelId"); String statusId =
	 * request.getParameter("statusId"); String errorCode =
	 * request.getParameter("code"); String buyerEmail =
	 * request.getParameter("buyerEmail"); int recordsTotal = 0; int
	 * recordsFiltered = 0; SimpleDateFormat formatter = new
	 * SimpleDateFormat("MMMM dd,yyyy"); try{ Timestamp now
	 * =UtilDateTime.nowTimestamp(); int startIndex = Integer.parseInt(start)+1;
	 * int endIndex = Integer.parseInt(length); endIndex =
	 * endIndex+startIndex-1;
	 * 
	 * String selGroup="org.ofbiz"; SQLProcessor sqlProcessor = new
	 * SQLProcessor(delegator.getGroupHelperInfo(selGroup));
	 * List<Map<String,Object>> jobList = FastList.newInstance(); String
	 * sqlCommand =
	 * "Select * from Order_Management_Download_Log  order by last_Updated_Stamp DESC "
	 * ; if(UtilValidate.isNotEmpty(orderId) &&
	 * UtilValidate.isNotEmpty(channelId) && UtilValidate.isNotEmpty(statusId)
	 * && UtilValidate.isNotEmpty(errorCode)&&
	 * UtilValidate.isNotEmpty(buyerEmail)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where order_id='"+orderId+
	 * "'" + " AND status_id='"+statusId+"' AND imported_From='"
	 * +channelId+"' AND buyer_email='"+buyerEmail+"' AND error_code='"
	 * +errorCode+"'  order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(channelId)
	 * && UtilValidate.isNotEmpty(statusId) &&
	 * UtilValidate.isNotEmpty(errorCode)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where order_id='"+orderId+
	 * "'" + " AND status_id='"+statusId+"' AND imported_From='"
	 * +channelId+"'  AND error_code='"
	 * +errorCode+"'  order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(channelId)
	 * && UtilValidate.isNotEmpty(statusId) &&
	 * UtilValidate.isNotEmpty(buyerEmail)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where order_id='"+orderId+
	 * "'" + " AND status_id='"+statusId+"' AND imported_From='"
	 * +channelId+"' AND buyer_email='"
	 * +buyerEmail+"' order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(channelId)
	 * && UtilValidate.isNotEmpty(statusId)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where order_id='"+orderId+
	 * "'" + " AND status_id='"+statusId+"' AND imported_From='"
	 * +channelId+"'  order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(orderId) &&
	 * UtilValidate.isNotEmpty(channelId)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where order_id='"+orderId+
	 * "'" +
	 * " AND imported_From='"+channelId+"'  order by last_Updated_Stamp DESC ";
	 * } if(UtilValidate.isNotEmpty(orderId) &&
	 * UtilValidate.isNotEmpty(statusId)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where order_id='"+orderId+
	 * "'" +
	 * " AND status_id='"+statusId+"'   order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(channelId) &&
	 * UtilValidate.isNotEmpty(statusId)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where imported_From='"
	 * +channelId+"'" +
	 * " AND status_id='"+statusId+"' order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(buyerEmail) &&
	 * UtilValidate.isNotEmpty(errorCode)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where error_code='"
	 * +errorCode+"'" +
	 * " AND buyer_email='"+buyerEmail+"' order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(orderId) &&
	 * UtilValidate.isNotEmpty(buyerEmail)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where order_id='"+orderId+
	 * "'" +
	 * " AND buyer_email='"+buyerEmail+"'  order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(orderId) &&
	 * UtilValidate.isNotEmpty(errorCode)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where order_id='"+orderId+
	 * "'" +
	 * "  AND error_code='"+errorCode+"'  order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(orderId)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where order_id='"
	 * +orderId+"' order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(channelId)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where imported_From='"
	 * +channelId+"' order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(statusId)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where status_id='"
	 * +statusId+"' order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(buyerEmail)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where buyer_email='"
	 * +buyerEmail+"' order by last_Updated_Stamp DESC "; }
	 * if(UtilValidate.isNotEmpty(errorCode)){ sqlCommand =
	 * "Select * from Order_Management_Download_Log where error_code='"
	 * +errorCode+"' order by last_Updated_Stamp DESC "; }
	 * Debug.log(sqlCommand);
	 * 
	 * ResultSet rs = sqlProcessor.executeQuery(sqlCommand); int size= 0; if (rs
	 * != null){ rs.beforeFirst(); rs.last(); size = rs.getRow(); }
	 * 
	 * if(size < endIndex){ endIndex = size; }
	 * 
	 * 
	 * if (rs != null) {
	 * 
	 * if(startIndex >= endIndex){
	 * 
	 * rs.absolute(endIndex); Map<String,Object> params = FastMap.newInstance();
	 * String jobId = rs.getString("order_Management_Download_Log_Id");
	 * params.put("jobId", jobId);
	 * 
	 * jobList.add(params);
	 * 
	 * }else{ for (int i = startIndex; i<= endIndex; i++) { rs.absolute(i);
	 * Map<String,Object> params = FastMap.newInstance(); String jobId =
	 * rs.getString("order_Management_Download_Log_Id"); params.put("jobId",
	 * jobId);
	 * 
	 * jobList.add(params); } } }
	 * 
	 * if(UtilValidate.isNotEmpty(jobList)){
	 * 
	 * recordsTotal = size; recordsFiltered =size; for(Map<String,Object> job :
	 * jobList){ String jobId = (String) job.get("jobId"); GenericValue jobInfo
	 * = EntityUtil.getFirst(delegator.findByAnd("OrderManagementDownloadLog",
	 * UtilMisc.toMap("orderManagementDownloadLogId",jobId)));
	 * Map<String,Object> paramValues = FastMap.newInstance();
	 * paramValues.put("jobId",jobInfo.getString("orderManagementDownloadLogId")
	 * ); paramValues.put("orderId",jobInfo.getString("orderId"));
	 * paramValues.put("orderItemId",jobInfo.getString("orderItemId"));
	 * paramValues.put("buyerName",jobInfo.getString("buyerName"));
	 * paramValues.put("buyerEmail",jobInfo.getString("buyerEmail"));
	 * paramValues.put("sku",jobInfo.getString("sku"));
	 * paramValues.put("statusId",jobInfo.getString("statusId"));
	 * paramValues.put("orderDate",jobInfo.getString("createdStamp"));
	 * paramValues.put("errorCode",jobInfo.getString("errorCode"));
	 * paramValues.put("itemPrice",jobInfo.getString("itemPrice"));
	 * List<Map<String,Object>> loadData= new ArrayList<Map<String,Object>>();
	 * loadData.add(paramValues);
	 * 
	 * data.add(loadData); } }
	 * 
	 * result.put("draw", draw); result.put("data",data);
	 * result.put("recordsTotal", recordsTotal); result.put("recordsFiltered",
	 * recordsFiltered);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); result.put("data",data);
	 * result.put("draw", draw); result.put("recordsTotal", 0);
	 * result.put("recordsFiltered", 0); return
	 * org.opentaps.common.event.AjaxEvents.doJSONResponse(response, result); }
	 * return org.opentaps.common.event.AjaxEvents.doJSONResponse(response,
	 * result); }
	 */
	public static String getExtractModelsData(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> result = FastMap.newInstance();
		List<Object> data = FastList.newInstance();
		String draw = request.getParameter("draw");
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		String typeId = request.getParameter("typeId");
		int recordsTotal = 0;
		int recordsFiltered = 0;
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd,yyyy");
		ResultSet rs = null;
		try {
			Timestamp now = UtilDateTime.nowTimestamp();
			int startIndex = Integer.parseInt(start) + 1;
			int endIndex = Integer.parseInt(length);
			endIndex = endIndex + startIndex - 1;

			String selGroup = "org.ofbiz";
			GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
			SQLProcessor sqlProcessor = new SQLProcessor(delegator, helperInfo);
			List<Map<String, Object>> jobList = FastList.newInstance();
			String sqlCommand = null;
			if ("ORDER".equals(typeId))
				sqlCommand = "Select distinct(model_id) as model_id from Etl_Export_Order_fields  order by last_Updated_Stamp DESC ";

			if ("PRODUCT".equals(typeId))
				sqlCommand = "Select distinct(model_id) as model_id from Data_Export_product  order by last_Updated_Stamp DESC ";

			if ("CUSTOMER".equals(typeId))
				sqlCommand = "Select distinct(model_id) as model_id from Data_Export_customer  order by last_Updated_Stamp DESC ";

			if ("ACCOUNT".equals(typeId))
				sqlCommand = "Select distinct(model_id) as model_id from Data_Export_Account  order by last_Updated_Stamp DESC ";

			if ("SUPPLIER".equals(typeId))
				sqlCommand = "Select distinct(model_id) as model_id from Data_Export_Supplier  order by last_Updated_Stamp DESC ";

			if ("CATEGORY".equals(typeId))
				sqlCommand = "Select distinct(model_id) as model_id from Data_export_category  order by last_Updated_Stamp DESC ";

			if ("INVOICE".equals(typeId))
				sqlCommand = "Select distinct(model_id) as model_id from Data_export_invoice_header  order by last_Updated_Stamp DESC ";

			if ("INVOICE_ITEM".equals(typeId))
				sqlCommand = "Select distinct(model_id) as model_id from Data_export_invoice_item  order by last_Updated_Stamp DESC ";

			Debug.log(sqlCommand);

			rs = sqlProcessor.executeQuery(sqlCommand);
			int size = 0;
			if (rs != null) {
				rs.beforeFirst();
				rs.last();
				size = rs.getRow();
			}

			if (size < endIndex) {
				endIndex = size;
			}

			if (rs != null && size != 0) {

				if (startIndex >= endIndex) {

					rs.absolute(endIndex);
					Map<String, Object> params = FastMap.newInstance();
					String jobId = rs.getString("model_id");
					params.put("jobId", jobId);

					jobList.add(params);

				} else {
					for (int i = startIndex; i <= endIndex; i++) {
						rs.absolute(i);
						Map<String, Object> params = FastMap.newInstance();
						String jobId = rs.getString("model_id");
						params.put("jobId", jobId);

						jobList.add(params);
					}
				}
			}

			if (UtilValidate.isNotEmpty(jobList)) {

				recordsTotal = size;
				recordsFiltered = size;
				for (Map<String, Object> job : jobList) {
					String jobId = (String) job.get("jobId");
					GenericValue jobInfo = null;
					if ("ORDER".equals(typeId))
						jobInfo = EntityUtil.getFirst(delegator.findByAnd("EtlExportOrderFields",
								UtilMisc.toMap("modelId", jobId), null, false));
					if ("PRODUCT".equals(typeId))
						jobInfo = EntityUtil.getFirst(delegator.findByAnd("DataExportProduct",
								UtilMisc.toMap("modelId", jobId), null, false));
					if ("CUSTOMER".equals(typeId))
						jobInfo = EntityUtil.getFirst(delegator.findByAnd("DataExportCustomer",
								UtilMisc.toMap("modelId", jobId), null, false));
					if ("SUPPLIER".equals(typeId))
						jobInfo = EntityUtil.getFirst(delegator.findByAnd("DataExportSupplier",
								UtilMisc.toMap("modelId", jobId), null, false));
					if ("ACCOUNT".equals(typeId))
						jobInfo = EntityUtil.getFirst(delegator.findByAnd("DataExportAccount",
								UtilMisc.toMap("modelId", jobId), null, false));
					if ("CATEGORY".equals(typeId))
						jobInfo = EntityUtil.getFirst(delegator.findByAnd("DataExportCategory",
								UtilMisc.toMap("modelId", jobId), null, false));
					if ("INVOICE".equals(typeId))
						jobInfo = EntityUtil.getFirst(delegator.findByAnd("DataExportInvoiceHeader",
								UtilMisc.toMap("modelId", jobId), null, false));
					if ("INVOICE_ITEM".equals(typeId))
						jobInfo = EntityUtil.getFirst(delegator.findByAnd("DataExportInvoiceItem",
								UtilMisc.toMap("modelId", jobId), null, false));
					Map<String, Object> paramValues = FastMap.newInstance();
					paramValues.put("jobId", jobInfo.getString("modelId"));
					GenericValue model = EntityQuery.use(delegator).from("EtlModel")
							.where("modelId", jobInfo.getString("modelId")).queryOne();
					// GenericValue model =
					// delegator.findByPrimaryKey("EtlModel",UtilMisc.toMap("modelId",jobInfo.getString("modelId")));
					paramValues.put("modelName", model.getString("modelName"));
					paramValues.put("createdDate", jobInfo.getString("createdStamp"));
					paramValues.put("download", "Y");
					List<Map<String, Object>> loadData = new ArrayList<Map<String, Object>>();
					loadData.add(paramValues);

					data.add(loadData);
				}
			}

			result.put("draw", draw);
			result.put("data", data);
			result.put("recordsTotal", recordsTotal);
			result.put("recordsFiltered", recordsFiltered);

		} catch (Exception e) {
			result.put("data", data);
			result.put("draw", draw);
			result.put("recordsTotal", 0);
			result.put("recordsFiltered", 0);
			return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, result);
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception e){
				
			}
		}
		return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, result);
	}

	public static String generateOrderMocelCsv(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String selGroup = "org.ofbiz";
		GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
		SQLProcessor sqlProcessor = new SQLProcessor(delegator, helperInfo);
		String modelId = request.getParameter("modelId");
		String modelName = null;
		String exportModel = null;
		String expMname = null;
		String selectFields = "";
		FileInputStream fis = null;
		try {
			String sqlCommand = null;
			if (UtilValidate.isNotEmpty(modelId)) {
				GenericValue model = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId).queryOne();
				// GenericValue model = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if(UtilValidate.isNotEmpty(model)){
					modelName = model.getString("modelName");
					exportModel = model.getString("expModelId");
				}
				GenericValue exp = EntityQuery.use(delegator).from("EtlModel").where("modelId", exportModel).queryOne();
				// GenericValue exp = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",exportModel));
				if(UtilValidate.isNotEmpty(exp))
				expMname = exp.getString("modelName");
				
				if(UtilValidate.isNotEmpty(expMname)){
				List<GenericValue> getSelectFields = delegator.findByAnd("EtlSourceTable",
						UtilMisc.toMap("listName", expMname), null, false);
				int size = getSelectFields.size();
				int i = 1;
				if(UtilValidate.isNotEmpty(getSelectFields)){
				for (GenericValue g : getSelectFields) {
					if (size == i) {
						selectFields = selectFields.concat(g.getString("tableColumnName"));
					} else {
						selectFields = selectFields.concat(g.getString("tableColumnName") + ",");
					}
					i++;
				}
				}
			}
				// sqlCommand = "SELECT "+selectFields+" FROM
				// Etl_Extract_Fulfillment_Orders where model_id='"+modelId+"'";
			} else {
				request.setAttribute("_ERROR_MESSAGE_", "Model not found.");
				return "error";
			}

			// ResultSet resultSet = sqlProcessor.executeQuery(sqlCommand);
			List<GenericValue> extractList = delegator.findByAnd("EtlExportOrderFields",
					UtilMisc.toMap("modelId", modelId), null, false);

			if (extractList != null) {
				String csvFilePath = ComponentConfig.getRootLocation("Etl-Process") + "webapp/extract/csv/";
				CsvWriter writer = new CsvWriter(new FileWriter(csvFilePath + modelName + ".csv"), ',');
				if (UtilValidate.isNotEmpty(exportModel)) {
					List<GenericValue> getFields = delegator.findByAnd("EtlSourceTable",
							UtilMisc.toMap("listName", expMname), null, false);
					if(UtilValidate.isNotEmpty(getFields)){
					for (GenericValue gv : getFields) {
						writer.write(gv.getString("etlFieldName"));
					}
				}
					writer.endRecord();
				}
				// writer.writeAll(resultSet, true);
				for (GenericValue gh : extractList) {
					writer.write(gh.getString("orderId"));
					writer.write(gh.getString("orderItemId"));
					writer.write(gh.getString("quantity"));
					writer.write(gh.getString("shipDate"));
					writer.write(gh.getString("trackingNumber"));
					writer.endRecord();
				}
				writer.close();

				// download

				fis = new FileInputStream(csvFilePath + modelName + ".csv");
				byte b[];
				int x = fis.available();
				b = new byte[x];
				//System.out.println(" b size" + b.length);
				Debug.log(" b size" + b.length);
				fis.read(b);

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + modelName + ".csv");

				OutputStream os = response.getOutputStream();
				os.write(b);
				os.flush();

			} else {
				return "error";
			}

		} catch (Exception e) {
			//System.out.println("Exception eee" + e);
			Debug.log("Exception eee" + e);
			return "error";
		}finally{
			try{
				if(fis!=null){
					fis.close();
				}
			}catch(Exception e){
				
			}
		}
		return "success";
	}

	public static String generateProductModelCsv(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String selGroup = "org.ofbiz";
		GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
		SQLProcessor sqlProcessor = new SQLProcessor(delegator, helperInfo);
		String modelId = request.getParameter("modelId");
		String modelName = null;
		String exportModel = null;
		String expMname = null;
		String selectFields = "";
		FileInputStream fis =null;
		try {
			String sqlCommand = null;
			if (UtilValidate.isNotEmpty(modelId)) {
				GenericValue model = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId).queryOne();
				// GenericValue model = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if(UtilValidate.isNotEmpty(model)){
					modelName = model.getString("modelName");
					exportModel = model.getString("expModelId");
				}
				GenericValue exp = EntityQuery.use(delegator).from("EtlModel").where("modelId", exportModel).queryOne();
				// GenericValue exp = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",exportModel));
				if(UtilValidate.isNotEmpty(exp))
				expMname = exp.getString("modelName");
				
				if(UtilValidate.isNotEmpty(expMname)){
				List<GenericValue> getSelectFields = delegator.findByAnd("EtlSourceTable",
						UtilMisc.toMap("listName", expMname), null, false);
				int size = getSelectFields.size();
				int i = 1;
				if(UtilValidate.isNotEmpty(getSelectFields)){
				for (GenericValue g : getSelectFields) {
					if (size == i) {
						selectFields = selectFields.concat(g.getString("tableColumnName"));
					} else {
						selectFields = selectFields.concat(g.getString("tableColumnName") + ",");
					}
					i++;
				}
				}
				}
				// sqlCommand = "SELECT "+selectFields+" FROM
				// Etl_Extract_Fulfillment_Orders where model_id='"+modelId+"'";
			} else {
				request.setAttribute("_ERROR_MESSAGE_", "Model not found.");
				return "error";
			}

			// ResultSet resultSet = sqlProcessor.executeQuery(sqlCommand);
			List<GenericValue> extractList = delegator.findByAnd("DataExportProduct",
					UtilMisc.toMap("modelId", modelId), null, false);

			if (extractList != null) {
				String csvFilePath = ComponentConfig.getRootLocation("Etl-Process") + "webapp/extract/csv/";
				CsvWriter writer = new CsvWriter(new FileWriter(csvFilePath + modelName + ".csv"), ',');
				if (UtilValidate.isNotEmpty(exportModel)) {
					List<GenericValue> getFields = delegator.findByAnd("EtlSourceTable",
							UtilMisc.toMap("listName", expMname), null, false);
					if(UtilValidate.isNotEmpty(getFields)){
					for (GenericValue gv : getFields) {
						writer.write(gv.getString("etlFieldName"));
					}
					}
					writer.endRecord();
				}

				// getting list of columns
				List<GenericValue> eltSourceColumns = null;
				List etlSource = null;
				GenericValue eltModel = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId)
						.queryOne();
				// GenericValue eltModel =
				// delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if (UtilValidate.isNotEmpty(eltModel)) {
					GenericValue expEtlModel = EntityQuery.use(delegator).from("EtlModel")
							.where("modelId", eltModel.getString("expModelId")).queryOne();
					// GenericValue expEtlModel =
					// delegator.findByPrimaryKey("EtlModel",
					// UtilMisc.toMap("modelId",eltModel.getString("expModelId")));
					if (UtilValidate.isNotEmpty(expEtlModel)) {
						eltSourceColumns = delegator.findByAnd("EtlSourceTable",
								UtilMisc.toMap("listName", expEtlModel.getString("modelName")), null, false);
					}

				}

				if (UtilValidate.isNotEmpty(eltSourceColumns)) {
					etlSource = EntityUtil.getFieldListFromEntityList(eltSourceColumns, "tableColumnName", true);
				}
				// writer.writeAll(resultSet, true);
				for (GenericValue gh : extractList) {

					if (UtilValidate.isNotEmpty(etlSource)) {
						for (int i = 0; i < etlSource.size(); i++) {
							writer.write(gh.getString((String) etlSource.get(i)));
						}
					}
					writer.endRecord();
				}
				writer.close();

				// download

				fis = new FileInputStream(csvFilePath + modelName + ".csv");
				byte b[];
				int x = fis.available();
				b = new byte[x];
				//System.out.println(" b size" + b.length);
				Debug.log(" b size" + b.length);
				fis.read(b);

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + modelName + ".csv");

				OutputStream os = response.getOutputStream();
				os.write(b);
				os.flush();

			} else {
				return "error";
			}

		} catch (Exception e) {
			//System.out.println("Exception eee" + e);
			Debug.log("Exception eee" + e);
			return "error";
		}finally{
			try{
				if(fis!=null){
					fis.close();
				}
			}catch(Exception e){
				
			}
		}
		return "success";
	}

	public static String generateCustomerModelCsv(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String selGroup = "org.ofbiz";
		GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
		SQLProcessor sqlProcessor = new SQLProcessor(delegator, helperInfo);
		String modelId = request.getParameter("modelId");
		String modelName = null;
		String exportModel = null;
		String expMname = null;
		String selectFields = "";
		FileInputStream fis =null;
		try {
			String sqlCommand = null;
			if (UtilValidate.isNotEmpty(modelId)) {
				GenericValue model = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId).queryOne();
				// GenericValue model = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if(UtilValidate.isNotEmpty(model)){
					modelName = model.getString("modelName");
					exportModel = model.getString("expModelId");
				}
				GenericValue exp = EntityQuery.use(delegator).from("EtlModel").where("modelId", exportModel).queryOne();
				// GenericValue exp = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",exportModel));
				if(UtilValidate.isNotEmpty(exp))
				expMname = exp.getString("modelName");
				
				if(UtilValidate.isNotEmpty(expMname)){
				List<GenericValue> getSelectFields = delegator.findByAnd("EtlSourceTable",
						UtilMisc.toMap("listName", expMname), null, false);
				int size = getSelectFields.size();
				int i = 1;
				if(UtilValidate.isNotEmpty(getSelectFields)){
				for (GenericValue g : getSelectFields) {
					if (size == i) {
						selectFields = selectFields.concat(g.getString("tableColumnName"));
					} else {
						selectFields = selectFields.concat(g.getString("tableColumnName") + ",");
					}
					i++;
				}
				}
				}
				// sqlCommand = "SELECT "+selectFields+" FROM
				// Etl_Extract_Fulfillment_Orders where model_id='"+modelId+"'";
			} else {
				request.setAttribute("_ERROR_MESSAGE_", "Model not found.");
				return "error";
			}

			// ResultSet resultSet = sqlProcessor.executeQuery(sqlCommand);
			List<GenericValue> extractList = delegator.findByAnd("DataExportCustomer",
					UtilMisc.toMap("modelId", modelId), null, false);

			if (extractList != null) {
				String csvFilePath = ComponentConfig.getRootLocation("Etl-Process") + "webapp/extract/csv/";
				CsvWriter writer = new CsvWriter(new FileWriter(csvFilePath + modelName + ".csv"), ',');
				if (UtilValidate.isNotEmpty(exportModel)) {
					List<GenericValue> getFields = delegator.findByAnd("EtlSourceTable",
							UtilMisc.toMap("listName", expMname), null, false);
					for (GenericValue gv : getFields) {
						writer.write(gv.getString("etlFieldName"));
					}
					writer.endRecord();
				}

				// getting list of columns
				List<GenericValue> eltSourceColumns = null;
				List etlSource = null;
				GenericValue eltModel = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId)
						.queryOne();
				// GenericValue eltModel =
				// delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if (UtilValidate.isNotEmpty(eltModel)) {
					GenericValue expEtlModel = EntityQuery.use(delegator).from("EtlModel")
							.where("modelId", eltModel.getString("expModelId")).queryOne();
					// GenericValue expEtlModel =
					// delegator.findByPrimaryKey("EtlModel",
					// UtilMisc.toMap("modelId",eltModel.getString("expModelId")));
					if (UtilValidate.isNotEmpty(expEtlModel)) {
						eltSourceColumns = delegator.findByAnd("EtlSourceTable",
								UtilMisc.toMap("listName", expEtlModel.getString("modelName")), null, false);
					}

				}

				if (UtilValidate.isNotEmpty(eltSourceColumns)) {
					etlSource = EntityUtil.getFieldListFromEntityList(eltSourceColumns, "tableColumnName", true);
				}
				// writer.writeAll(resultSet, true);
				for (GenericValue gh : extractList) {

					if (UtilValidate.isNotEmpty(etlSource)) {
						for (int i = 0; i < etlSource.size(); i++) {
							writer.write(gh.getString((String) etlSource.get(i)));
						}
					}
					writer.endRecord();
				}
				writer.close();

				// download

				fis = new FileInputStream(csvFilePath + modelName + ".csv");
				byte b[];
				int x = fis.available();
				b = new byte[x];
				//System.out.println(" b size" + b.length);
				Debug.log(" b size" + b.length);
				fis.read(b);

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + modelName + ".csv");

				OutputStream os = response.getOutputStream();
				os.write(b);
				os.flush();

			} else {
				return "error";
			}

		} catch (Exception e) {
			//System.out.println("Exception eee" + e);
			Debug.log("Exception eee" + e);
			return "error";
		}finally{
			try{
				if(fis!=null){
					fis.close();
				}
			}catch(Exception e){
				
			}
		}
		return "success";
	}

	public static String getEtlBatchesModelsData(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> result = FastMap.newInstance();
		List<Object> data = FastList.newInstance();
		String draw = request.getParameter("draw");
		String start = request.getParameter("start");
		String length = request.getParameter("length");

		String etlDestTableName = request.getParameter("etlDestTableName");
		String modelName = request.getParameter("model");
		String reprocessBatchId = request.getParameter("batchId");

		int recordsTotal = 0;
		int recordsFiltered = 0;
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd,yyyy");
		ResultSet rs = null;
		try {
			Timestamp now = UtilDateTime.nowTimestamp();
			int startIndex = Integer.parseInt(start) + 1;
			int endIndex = Integer.parseInt(length);
			endIndex = endIndex + startIndex - 1;

			String selGroup = "org.ofbiz";
			GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
			SQLProcessor sqlProcessor = new SQLProcessor(delegator, helperInfo);
			List<Map<String, Object>> batchList = FastList.newInstance();
			String sqlCommand = null;
			List<Object> values = new ArrayList<>();
			if (UtilValidate.isNotEmpty(etlDestTableName) && UtilValidate.isEmpty(modelName)) {
				sqlCommand = "Select distinct(batch_id) as batch_id from Etl_Pre_Processor where etl_Table_Name=? order by created_Tx_Stamp DESC ";
				values.add(etlDestTableName);
			} else if (UtilValidate.isNotEmpty(etlDestTableName) && UtilValidate.isNotEmpty(modelName)) {
				GenericValue etlModel = EntityUtil
						.getFirst(delegator.findByAnd("EtlModel", UtilMisc.toMap("modelName", modelName), null, false));
				if (UtilValidate.isNotEmpty(etlModel)) {
					sqlCommand = "Select distinct(batch_id) as batch_id from Etl_Pre_Processor where etl_Table_Name=? AND  model_Id=? order by last_Updated_Stamp DESC ";
					values.add(etlDestTableName);
					values.add(etlModel.getString("modelId"));
				}
			} else if (UtilValidate.isNotEmpty(reprocessBatchId)) {
				sqlCommand = "Select distinct(batch_id) as batch_id from Etl_Pre_Processor where batch_id=?";
				values.add(reprocessBatchId);
			}

			Debug.log(sqlCommand);
			rs = QueryUtil.getResultSet(sqlCommand, values, delegator);
			int size = 0;
			if (rs != null) {
				rs.beforeFirst();
				rs.last();
				size = rs.getRow();
			}

			if (size < endIndex) {
				endIndex = size;
			}

			if (rs != null && size != 0) {

				if (startIndex >= endIndex) {

					rs.absolute(endIndex);
					Map<String, Object> params = FastMap.newInstance();
					String batchId = rs.getString("batch_id");
					params.put("batchId", batchId);

					batchList.add(params);

				} else {
					for (int i = startIndex; i <= endIndex; i++) {
						rs.absolute(i);
						Map<String, Object> params = FastMap.newInstance();
						String batchId = rs.getString("batch_id");
						params.put("batchId", batchId);

						batchList.add(params);
					}
				}
			}

			if (UtilValidate.isNotEmpty(batchList)) {

				recordsTotal = size;
				recordsFiltered = size;
				for (Map<String, Object> batch : batchList) {
					String batchId = (String) batch.get("batchId");
					GenericValue batchInfo = null;
					
					if(UtilValidate.isNotEmpty(batchId))
					batchInfo = EntityQuery.use(delegator).from("EtlPreProcessor").where("batchId", batchId)
							.queryOne();
					
					if(UtilValidate.isNotEmpty(batchInfo)){
					Map<String, Object> paramValues = FastMap.newInstance();
					paramValues.put("batchId", batchInfo.getString("batchId"));
					if (UtilValidate.isNotEmpty(batchInfo.getString("modelId"))) {
						GenericValue etlModels = EntityUtil.getFirst(delegator.findByAnd("EtlModel",
								UtilMisc.toMap("modelId", batchInfo.getString("modelId")), null, false));
						if (UtilValidate.isNotEmpty(etlModels))
							paramValues.put("modelName", etlModels.getString("modelName"));
					}

					paramValues.put("accessType", batchInfo.getString("accessType"));
					paramValues.put("statusId", batchInfo.getString("statusId"));
					if (UtilValidate.isNotEmpty(batchInfo.getString("processedCount")))
						paramValues.put("processedCount", batchInfo.getString("processedCount"));
					else
						paramValues.put("processedCount", 0);
					if (UtilValidate.isNotEmpty(batchInfo.getString("notProcessedCount")))
						paramValues.put("notProcessedCount", batchInfo.getString("notProcessedCount"));
					else
						paramValues.put("notProcessedCount", 0);
					paramValues.put("createdBy", batchInfo.getString("createdBy"));
					List<Map<String, Object>> loadData = new ArrayList<Map<String, Object>>();
					loadData.add(paramValues);
					data.add(loadData);
				}
			}
			}

			result.put("draw", draw);
			result.put("data", data);
			result.put("recordsTotal", recordsTotal);
			result.put("recordsFiltered", recordsFiltered);

		} catch (Exception e) {
			result.put("data", data);
			result.put("draw", draw);
			result.put("recordsTotal", 0);
			result.put("recordsFiltered", 0);
			return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, result);
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception e){
				
			}
		}
		return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, result);
	}

	public static String generateAccountsModelCsv(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String selGroup = "org.ofbiz";
		GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
		SQLProcessor sqlProcessor = new SQLProcessor(delegator, helperInfo);
		String modelId = request.getParameter("modelId");
		String modelName = null;
		String exportModel = null;
		String expMname = null;
		String selectFields = "";
		FileInputStream fis = null;
		try {
			String sqlCommand = null;
			if (UtilValidate.isNotEmpty(modelId)) {
				GenericValue model = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId).queryOne();
				// GenericValue model = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if(UtilValidate.isNotEmpty(model)){
					modelName = model.getString("modelName");
					exportModel = model.getString("expModelId");
				}
				GenericValue exp = EntityQuery.use(delegator).from("EtlModel").where("modelId", exportModel).queryOne();
				// GenericValue exp = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",exportModel));
				if(UtilValidate.isNotEmpty(exp))
				expMname = exp.getString("modelName");
				
				if(UtilValidate.isNotEmpty(expMname)){
				List<GenericValue> getSelectFields = delegator.findByAnd("EtlSourceTable",
						UtilMisc.toMap("listName", expMname), null, false);
				int size = getSelectFields.size();
				int i = 1;
				if(UtilValidate.isNotEmpty(getSelectFields)){
				for (GenericValue g : getSelectFields) {
					if (size == i) {
						selectFields = selectFields.concat(g.getString("tableColumnName"));
					} else {
						selectFields = selectFields.concat(g.getString("tableColumnName") + ",");
					}
					i++;
				}
				}
				}
				// sqlCommand = "SELECT "+selectFields+" FROM
				// Etl_Extract_Fulfillment_Orders where model_id='"+modelId+"'";
			} else {
				request.setAttribute("_ERROR_MESSAGE_", "Model not found.");
				return "error";
			}

			// ResultSet resultSet = sqlProcessor.executeQuery(sqlCommand);
			List<GenericValue> extractList = delegator.findByAnd("DataExportAccount",
					UtilMisc.toMap("modelId", modelId), null, false);

			if (extractList != null) {
				String csvFilePath = ComponentConfig.getRootLocation("Etl-Process") + "webapp/extract/csv/";
				CsvWriter writer = new CsvWriter(new FileWriter(csvFilePath + modelName + ".csv"), ',');
				if (UtilValidate.isNotEmpty(exportModel)) {
					List<GenericValue> getFields = delegator.findByAnd("EtlSourceTable",
							UtilMisc.toMap("listName", expMname), null, false);
					if(UtilValidate.isNotEmpty(getFields)){
					for (GenericValue gv : getFields) {
						writer.write(gv.getString("etlFieldName"));
					}
					}
					writer.endRecord();
				}

				// getting list of columns
				List<GenericValue> eltSourceColumns = null;
				List etlSource = null;
				GenericValue eltModel = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId)
						.queryOne();
				// GenericValue eltModel =
				// delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if (UtilValidate.isNotEmpty(eltModel)) {
					GenericValue expEtlModel = EntityQuery.use(delegator).from("EtlModel")
							.where("modelId", eltModel.getString("expModelId")).queryOne();
					// GenericValue expEtlModel =
					// delegator.findByPrimaryKey("EtlModel",
					// UtilMisc.toMap("modelId",eltModel.getString("expModelId")));
					if (UtilValidate.isNotEmpty(expEtlModel)) {
						eltSourceColumns = delegator.findByAnd("EtlSourceTable",
								UtilMisc.toMap("listName", expEtlModel.getString("modelName")), null, false);
					}

				}

				if (UtilValidate.isNotEmpty(eltSourceColumns)) {
					etlSource = EntityUtil.getFieldListFromEntityList(eltSourceColumns, "tableColumnName", true);
				}
				// writer.writeAll(resultSet, true);
				for (GenericValue gh : extractList) {

					if (UtilValidate.isNotEmpty(etlSource)) {
						for (int i = 0; i < etlSource.size(); i++) {
							writer.write(gh.getString((String) etlSource.get(i)));
						}
					}
					writer.endRecord();
				}
				writer.close();

				// download

				fis = new FileInputStream(csvFilePath + modelName + ".csv");
				byte b[];
				int x = fis.available();
				b = new byte[x];
				//System.out.println(" b size" + b.length);
				Debug.log(" b size" + b.length);
				fis.read(b);

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + modelName + ".csv");

				OutputStream os = response.getOutputStream();
				os.write(b);
				os.flush();

			} else {
				return "error";
			}

		} catch (Exception e) {
			//System.out.println("Exception eee" + e);
			Debug.log("Exception eee" + e);
			return "error";
		}finally{
			try{
				if(fis!=null){
					fis.close();
				}
			}catch(Exception e){
				
			}
		}
		return "success";
	}

	public static String generateSuppliersModelCsv(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String selGroup = "org.ofbiz";
		GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
		SQLProcessor sqlProcessor = new SQLProcessor(delegator, helperInfo);
		String modelId = request.getParameter("modelId");
		String modelName = null;
		String exportModel = null;
		String expMname = null;
		String selectFields = "";
		FileInputStream fis = null;
		try {
			String sqlCommand = null;
			if (UtilValidate.isNotEmpty(modelId)) {
				GenericValue model = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId).queryOne();
				// GenericValue model = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if(UtilValidate.isNotEmpty(model)){
					modelName = model.getString("modelName");
					exportModel = model.getString("expModelId");
				}
				
				if(UtilValidate.isNotEmpty(exportModel)){
				GenericValue exp = EntityQuery.use(delegator).from("EtlModel").where("modelId", exportModel).queryOne();
				// GenericValue exp = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",exportModel));
				if(UtilValidate.isNotEmpty(exp))
				expMname = exp.getString("modelName");
				}
				List<GenericValue> getSelectFields = delegator.findByAnd("EtlSourceTable",
						UtilMisc.toMap("listName", expMname), null, false);
				int size = getSelectFields.size();
				int i = 1;
				if(UtilValidate.isNotEmpty(getSelectFields)){
				for (GenericValue g : getSelectFields) {
					if (size == i) {
						selectFields = selectFields.concat(g.getString("tableColumnName"));
					} else {
						selectFields = selectFields.concat(g.getString("tableColumnName") + ",");
					}
					i++;
				}
			}
				// sqlCommand = "SELECT "+selectFields+" FROM
				// Etl_Extract_Fulfillment_Orders where model_id='"+modelId+"'";
			} else {
				request.setAttribute("_ERROR_MESSAGE_", "Model not found.");
				return "error";
			}

			// ResultSet resultSet = sqlProcessor.executeQuery(sqlCommand);
			List<GenericValue> extractList = delegator.findByAnd("DataExportSupplier",
					UtilMisc.toMap("modelId", modelId), null, false);

			if (extractList != null) {
				String csvFilePath = ComponentConfig.getRootLocation("Etl-Process") + "webapp/extract/csv/";
				CsvWriter writer = new CsvWriter(new FileWriter(csvFilePath + modelName + ".csv"), ',');
				if (UtilValidate.isNotEmpty(exportModel)) {
					List<GenericValue> getFields = delegator.findByAnd("EtlSourceTable",
							UtilMisc.toMap("listName", expMname), null, false);
					if(UtilValidate.isNotEmpty(getFields)){
					for (GenericValue gv : getFields) {
						writer.write(gv.getString("etlFieldName"));
					}
					}
					writer.endRecord();
				}

				// getting list of columns
				List<GenericValue> eltSourceColumns = null;
				List etlSource = null;
				GenericValue eltModel = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId)
						.queryOne();
				// GenericValue eltModel =
				// delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if (UtilValidate.isNotEmpty(eltModel)) {
					GenericValue expEtlModel = EntityQuery.use(delegator).from("EtlModel")
							.where("modelId", eltModel.getString("expModelId")).queryOne();
					// GenericValue expEtlModel =
					// delegator.findByPrimaryKey("EtlModel",
					// UtilMisc.toMap("modelId",eltModel.getString("expModelId")));
					if (UtilValidate.isNotEmpty(expEtlModel)) {
						eltSourceColumns = delegator.findByAnd("EtlSourceTable",
								UtilMisc.toMap("listName", expEtlModel.getString("modelName")), null, false);
					}

				}

				if (UtilValidate.isNotEmpty(eltSourceColumns)) {
					etlSource = EntityUtil.getFieldListFromEntityList(eltSourceColumns, "tableColumnName", true);
				}
				// writer.writeAll(resultSet, true);
				for (GenericValue gh : extractList) {

					if (UtilValidate.isNotEmpty(etlSource)) {
						for (int i = 0; i < etlSource.size(); i++) {
							writer.write(gh.getString((String) etlSource.get(i)));
						}
					}
					writer.endRecord();
				}
				writer.close();

				// download

				fis = new FileInputStream(csvFilePath + modelName + ".csv");
				byte b[];
				int x = fis.available();
				b = new byte[x];
				//System.out.println(" b size" + b.length);
				Debug.log(" b size" + b.length);
				fis.read(b);

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + modelName + ".csv");

				OutputStream os = response.getOutputStream();
				os.write(b);
				os.flush();

			} else {
				return "error";
			}

		} catch (Exception e) {
			//System.out.println("Exception eee" + e);
			Debug.log("Exception eee" + e);
			return "error";
		}finally{
			try{
				if(fis!=null){
					fis.close();
				}
			}catch(Exception e){
				
			}
		}
		return "success";
	}

	public static String generateCategoryModelCsv(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String selGroup = "org.ofbiz";
		GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
		SQLProcessor sqlProcessor = new SQLProcessor(delegator, helperInfo);
		String modelId = request.getParameter("modelId");
		String modelName = null;
		String exportModel = null;
		String expMname = null;
		String selectFields = "";
		FileInputStream fis = null;
		try {
			String sqlCommand = null;
			if (UtilValidate.isNotEmpty(modelId)) {
				GenericValue model = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId).queryOne();
				// GenericValue model = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if(UtilValidate.isNotEmpty(model)){
				modelName = model.getString("modelName");
				exportModel = model.getString("expModelId");
				}
				
				if(UtilValidate.isNotEmpty(exportModel)){
				GenericValue exp = EntityQuery.use(delegator).from("EtlModel").where("modelId", exportModel).queryOne();
				// GenericValue exp = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",exportModel));
				if(UtilValidate.isNotEmpty(exp))
				expMname = exp.getString("modelName");
				
				
				if(UtilValidate.isNotEmpty(expMname)){
				List<GenericValue> getSelectFields = delegator.findByAnd("EtlSourceTable",
						UtilMisc.toMap("listName", expMname), null, false);
				int size = getSelectFields.size();
				int i = 1;
				if(UtilValidate.isNotEmpty(getSelectFields)){
				for (GenericValue g : getSelectFields) {
					if (size == i) {
						selectFields = selectFields.concat(g.getString("tableColumnName"));
					} else {
						selectFields = selectFields.concat(g.getString("tableColumnName") + ",");
					}
					i++;
				}
				}
				}
				}
				// sqlCommand = "SELECT "+selectFields+" FROM
				// Etl_Extract_Fulfillment_Orders where model_id='"+modelId+"'";
			} else {
				request.setAttribute("_ERROR_MESSAGE_", "Model not found.");
				return "error";
			}

			// ResultSet resultSet = sqlProcessor.executeQuery(sqlCommand);
			List<GenericValue> extractList = delegator.findByAnd("DataExportCategory",
					UtilMisc.toMap("modelId", modelId), null, false);

			if (extractList != null) {
				String csvFilePath = ComponentConfig.getRootLocation("Etl-Process") + "webapp/extract/csv/";
				CsvWriter writer = new CsvWriter(new FileWriter(csvFilePath + modelName + ".csv"), ',');
				if (UtilValidate.isNotEmpty(exportModel)) {
					List<GenericValue> getFields = delegator.findByAnd("EtlSourceTable",
							UtilMisc.toMap("listName", expMname), null, false);
					if(UtilValidate.isNotEmpty(getFields)){
					for (GenericValue gv : getFields) {
						writer.write(gv.getString("etlFieldName"));
					}
					}
					writer.endRecord();
				}

				// getting list of columns
				List<GenericValue> eltSourceColumns = null;
				List etlSource = null;
				GenericValue eltModel = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId)
						.queryOne();
				// GenericValue eltModel =
				// delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if (UtilValidate.isNotEmpty(eltModel)) {
					GenericValue expEtlModel = EntityQuery.use(delegator).from("EtlModel")
							.where("modelId", eltModel.getString("expModelId")).queryOne();
					// GenericValue expEtlModel =
					// delegator.findByPrimaryKey("EtlModel",
					// UtilMisc.toMap("modelId",eltModel.getString("expModelId")));
					if (UtilValidate.isNotEmpty(expEtlModel)) {
						eltSourceColumns = delegator.findByAnd("EtlSourceTable",
								UtilMisc.toMap("listName", expEtlModel.getString("modelName")), null, false);
					}

				}

				if (UtilValidate.isNotEmpty(eltSourceColumns)) {
					etlSource = EntityUtil.getFieldListFromEntityList(eltSourceColumns, "tableColumnName", true);
				}
				// writer.writeAll(resultSet, true);
				for (GenericValue gh : extractList) {

					if (UtilValidate.isNotEmpty(etlSource)) {
						for (int i = 0; i < etlSource.size(); i++) {
							writer.write(gh.getString((String) etlSource.get(i)));
						}
					}
					writer.endRecord();
				}
				writer.close();

				// download

				fis = new FileInputStream(csvFilePath + modelName + ".csv");
				byte b[];
				int x = fis.available();
				b = new byte[x];
				//System.out.println(" b size" + b.length);
				Debug.log(" b size" + b.length);
				fis.read(b);

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + modelName + ".csv");

				OutputStream os = response.getOutputStream();
				os.write(b);
				os.flush();

			} else {
				return "error";
			}

		} catch (Exception e) {
			//System.out.println("Exception eee" + e);
			Debug.log("Exception eee" + e);
			return "error";
		}finally{
			try{
				if(fis!=null){
					fis.close();
				}
			}catch(Exception e){
				
			}
		}
		return "success";
	}

	public static String generateInvoiceModelCsv(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String selGroup = "org.ofbiz";
		GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
		SQLProcessor sqlProcessor = new SQLProcessor(delegator, helperInfo);
		String modelId = request.getParameter("modelId");
		String modelName = null;
		String exportModel = null;
		String expMname = null;
		String selectFields = "";
		FileInputStream fis = null;
		try {
			String sqlCommand = null;
			if (UtilValidate.isNotEmpty(modelId)) {
				GenericValue model = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId).queryOne();
				// GenericValue model = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if(UtilValidate.isNotEmpty(model)){
				modelName = model.getString("modelName");
				exportModel = model.getString("expModelId");
				}
				
				if(UtilValidate.isNotEmpty(exportModel)){
				GenericValue exp = EntityQuery.use(delegator).from("EtlModel").where("modelId", exportModel).queryOne();
				// GenericValue exp = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",exportModel));
				if(UtilValidate.isNotEmpty(exp))
					expMname = exp.getString("modelName");
				}
				
				if(UtilValidate.isNotEmpty(expMname)){
				List<GenericValue> getSelectFields = delegator.findByAnd("EtlSourceTable",
						UtilMisc.toMap("listName", expMname), null, false);
				int size = getSelectFields.size();
				int i = 1;
				if(UtilValidate.isNotEmpty(getSelectFields)){
				for (GenericValue g : getSelectFields) {
					if (size == i) {
						selectFields = selectFields.concat(g.getString("tableColumnName"));
					} else {
						selectFields = selectFields.concat(g.getString("tableColumnName") + ",");
					}
					i++;
				}
				}
			}
				// sqlCommand = "SELECT "+selectFields+" FROM
				// Etl_Extract_Fulfillment_Orders where model_id='"+modelId+"'";
			} else {
				request.setAttribute("_ERROR_MESSAGE_", "Model not found.");
				return "error";
			}

			// ResultSet resultSet = sqlProcessor.executeQuery(sqlCommand);
			List<GenericValue> extractList = delegator.findByAnd("DataExportInvoiceHeader",
					UtilMisc.toMap("modelId", modelId), null, false);

			if (extractList != null) {
				String csvFilePath = ComponentConfig.getRootLocation("Etl-Process") + "webapp/extract/csv/";
				CsvWriter writer = new CsvWriter(new FileWriter(csvFilePath + modelName + ".csv"), ',');
				if (UtilValidate.isNotEmpty(exportModel)) {
					List<GenericValue> getFields = delegator.findByAnd("EtlSourceTable",
							UtilMisc.toMap("listName", expMname), null, false);
					for (GenericValue gv : getFields) {
						writer.write(gv.getString("etlFieldName"));
					}
					writer.endRecord();
				}

				// getting list of columns
				List<GenericValue> eltSourceColumns = null;
				List etlSource = null;
				GenericValue eltModel = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId)
						.queryOne();
				// GenericValue eltModel =
				// delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if (UtilValidate.isNotEmpty(eltModel)) {
					GenericValue expEtlModel = EntityQuery.use(delegator).from("EtlModel")
							.where("modelId", eltModel.getString("expModelId")).queryOne();
					// GenericValue expEtlModel =
					// delegator.findByPrimaryKey("EtlModel",
					// UtilMisc.toMap("modelId",eltModel.getString("expModelId")));
					if (UtilValidate.isNotEmpty(expEtlModel)) {
						eltSourceColumns = delegator.findByAnd("EtlSourceTable",
								UtilMisc.toMap("listName", expEtlModel.getString("modelName")), null, false);
					}

				}

				if (UtilValidate.isNotEmpty(eltSourceColumns)) {
					etlSource = EntityUtil.getFieldListFromEntityList(eltSourceColumns, "tableColumnName", true);
				}
				// writer.writeAll(resultSet, true);
				for (GenericValue gh : extractList) {

					if (UtilValidate.isNotEmpty(etlSource)) {
						for (int i = 0; i < etlSource.size(); i++) {
							writer.write(gh.getString((String) etlSource.get(i)));
						}
					}
					writer.endRecord();
				}
				writer.close();

				// download

				fis = new FileInputStream(csvFilePath + modelName + ".csv");
				byte b[];
				int x = fis.available();
				b = new byte[x];
				//System.out.println(" b size" + b.length);
				Debug.log(" b size" + b.length);
				fis.read(b);

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + modelName + ".csv");

				OutputStream os = response.getOutputStream();
				os.write(b);
				os.flush();

			} else {
				return "error";
			}

		} catch (Exception e) {
			//System.out.println("Exception eee" + e);
			Debug.log("Exception eee" + e);
			return "error";
		}finally{
			try{
				if(fis!=null){
					fis.close();
				}
			}catch(Exception e){
				
			}
		}
		return "success";
	}

	public static String generateInvoiceItemModelCsv(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String selGroup = "org.ofbiz";
		GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
		SQLProcessor sqlProcessor = new SQLProcessor(delegator, helperInfo);
		String modelId = request.getParameter("modelId");
		String modelName = null;
		String exportModel = null;
		String expMname = null;
		String selectFields = "";
		FileInputStream fis = null;
		try {
			String sqlCommand = null;
			if (UtilValidate.isNotEmpty(modelId)) {
				GenericValue model = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId).queryOne();
				// GenericValue model = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if(UtilValidate.isNotEmpty(model)){
				modelName = model.getString("modelName");
				exportModel = model.getString("expModelId");
				}
				if(UtilValidate.isNotEmpty(exportModel)){
				GenericValue exp = EntityQuery.use(delegator).from("EtlModel").where("modelId", exportModel).queryOne();
				// GenericValue exp = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",exportModel));
			
				if(UtilValidate.isNotEmpty(exp))
				expMname = exp.getString("modelName");
				
				}
				
				if(UtilValidate.isNotEmpty(expMname)){
				List<GenericValue> getSelectFields = delegator.findByAnd("EtlSourceTable",
						UtilMisc.toMap("listName", expMname), null, false);
				int size = getSelectFields.size();
				int i = 1;
				if(UtilValidate.isNotEmpty(getSelectFields)){
				for (GenericValue g : getSelectFields) {
					if (size == i) {
						selectFields = selectFields.concat(g.getString("tableColumnName"));
					} else {
						selectFields = selectFields.concat(g.getString("tableColumnName") + ",");
					}
					i++;
				}
				}
				}
				// sqlCommand = "SELECT "+selectFields+" FROM
				// Etl_Extract_Fulfillment_Orders where model_id='"+modelId+"'";
			} else {
				request.setAttribute("_ERROR_MESSAGE_", "Model not found.");
				return "error";
			}

			// ResultSet resultSet = sqlProcessor.executeQuery(sqlCommand);
			List<GenericValue> extractList = delegator.findByAnd("DataExportInvoiceItem",
					UtilMisc.toMap("modelId", modelId), null, false);

			if (extractList != null) {
				String csvFilePath = ComponentConfig.getRootLocation("Etl-Process") + "webapp/extract/csv/";
				CsvWriter writer = new CsvWriter(new FileWriter(csvFilePath + modelName + ".csv"), ',');
				if (UtilValidate.isNotEmpty(exportModel)) {
					List<GenericValue> getFields = delegator.findByAnd("EtlSourceTable",
							UtilMisc.toMap("listName", expMname), null, false);
					for (GenericValue gv : getFields) {
						writer.write(gv.getString("etlFieldName"));
					}
					writer.endRecord();
				}

				// getting list of columns
				List<GenericValue> eltSourceColumns = null;
				List etlSource = null;
				GenericValue eltModel = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId)
						.queryOne();
				// GenericValue eltModel =
				// delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if (UtilValidate.isNotEmpty(eltModel)) {
					GenericValue expEtlModel = EntityQuery.use(delegator).from("EtlModel")
							.where("modelId", eltModel.getString("expModelId")).queryOne();
					// GenericValue expEtlModel =
					// delegator.findByPrimaryKey("EtlModel",
					// UtilMisc.toMap("modelId",eltModel.getString("expModelId")));
					if (UtilValidate.isNotEmpty(expEtlModel)) {
						eltSourceColumns = delegator.findByAnd("EtlSourceTable",
								UtilMisc.toMap("listName", expEtlModel.getString("modelName")), null, false);
					}

				}

				if (UtilValidate.isNotEmpty(eltSourceColumns)) {
					etlSource = EntityUtil.getFieldListFromEntityList(eltSourceColumns, "tableColumnName", true);
				}
				// writer.writeAll(resultSet, true);
				for (GenericValue gh : extractList) {

					if (UtilValidate.isNotEmpty(etlSource)) {
						for (int i = 0; i < etlSource.size(); i++) {
							writer.write(gh.getString((String) etlSource.get(i)));
						}
					}
					writer.endRecord();
				}
				writer.close();

				// download

				fis = new FileInputStream(csvFilePath + modelName + ".csv");
				byte b[];
				int x = fis.available();
				b = new byte[x];
				//System.out.println(" b size" + b.length);
				Debug.log(" b size" + b.length);
				fis.read(b);

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + modelName + ".csv");

				OutputStream os = response.getOutputStream();
				os.write(b);
				os.flush();

			} else {
				return "error";
			}

		} catch (Exception e) {
			//System.out.println("Exception eee" + e);
			Debug.log("Exception eee" + e);
			return "error";
		}finally{
			try{
				if(fis!=null){
					fis.close();
				}
			}catch(Exception e){
				
			}
		}
		return "success";
	}

	public static String generateOrderModelCsv(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String selGroup = "org.ofbiz";
		GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
		SQLProcessor sqlProcessor = new SQLProcessor(delegator, helperInfo);
		String modelId = request.getParameter("modelId");
		String modelName = null;
		String exportModel = null;
		String expMname = null;
		String selectFields = "";
		FileInputStream fis = null;
		try {
			String sqlCommand = null;
			if (UtilValidate.isNotEmpty(modelId)) {
				GenericValue model = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId).queryOne();
				// GenericValue model = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if (UtilValidate.isNotEmpty(model)) {
				modelName = model.getString("modelName");
				exportModel = model.getString("expModelId");
				}
				
				if (UtilValidate.isNotEmpty(exportModel)){
				GenericValue exp = EntityQuery.use(delegator).from("EtlModel").where("modelId", exportModel).queryOne();
				// GenericValue exp = delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",exportModel));
				if (UtilValidate.isNotEmpty(exp))
				expMname = exp.getString("modelName");
				
			}
				if (UtilValidate.isNotEmpty(expMname)){
				List<GenericValue> getSelectFields = delegator.findByAnd("EtlSourceTable",
						UtilMisc.toMap("listName", expMname), null, false);
				int size = getSelectFields.size();
				int i = 1;
				if(UtilValidate.isNotEmpty(getSelectFields)){
				for (GenericValue g : getSelectFields) {
					if (size == i) {
						selectFields = selectFields.concat(g.getString("tableColumnName"));
					} else {
						selectFields = selectFields.concat(g.getString("tableColumnName") + ",");
					}
					i++;
				}
				}
			}
				// sqlCommand = "SELECT "+selectFields+" FROM
				// Etl_Extract_Fulfillment_Orders where model_id='"+modelId+"'";
			} else {
				request.setAttribute("_ERROR_MESSAGE_", "Model not found.");
				return "error";
			}

			// ResultSet resultSet = sqlProcessor.executeQuery(sqlCommand);
			List<GenericValue> extractList = delegator.findByAnd("EtlExportOrderFields",
					UtilMisc.toMap("modelId", modelId), null, false);

			if (extractList != null) {
				String csvFilePath = ComponentConfig.getRootLocation("Etl-Process") + "webapp/extract/csv/";
				CsvWriter writer = new CsvWriter(new FileWriter(csvFilePath + modelName + ".csv"), ',');
				if (UtilValidate.isNotEmpty(exportModel)) {
					List<GenericValue> getFields = delegator.findByAnd("EtlSourceTable",
							UtilMisc.toMap("listName", expMname), null, false);
					for (GenericValue gv : getFields) {
						writer.write(gv.getString("etlFieldName"));
					}
					writer.endRecord();
				}

				// getting list of columns
				List<GenericValue> eltSourceColumns = null;
				List etlSource = null;
				GenericValue eltModel = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId)
						.queryOne();
				// GenericValue eltModel =
				// delegator.findByPrimaryKey("EtlModel",
				// UtilMisc.toMap("modelId",modelId));
				if (UtilValidate.isNotEmpty(eltModel)) {
					GenericValue expEtlModel = EntityQuery.use(delegator).from("EtlModel")
							.where("modelId", eltModel.getString("expModelId")).queryOne();
					// GenericValue expEtlModel =
					// delegator.findByPrimaryKey("EtlModel",
					// UtilMisc.toMap("modelId",eltModel.getString("expModelId")));
					if (UtilValidate.isNotEmpty(expEtlModel)) {
						eltSourceColumns = delegator.findByAnd("EtlSourceTable",
								UtilMisc.toMap("listName", expEtlModel.getString("modelName")), null, false);
					}

				}

				if (UtilValidate.isNotEmpty(eltSourceColumns)) {
					etlSource = EntityUtil.getFieldListFromEntityList(eltSourceColumns, "tableColumnName", true);
				}
				// writer.writeAll(resultSet, true);
				for (GenericValue gh : extractList) {

					if (UtilValidate.isNotEmpty(etlSource)) {
						for (int i = 0; i < etlSource.size(); i++) {
							writer.write(gh.getString((String) etlSource.get(i)));
						}
					}
					writer.endRecord();
				}
				writer.close();

				// download

				fis = new FileInputStream(csvFilePath + modelName + ".csv");
				byte b[];
				int x = fis.available();
				b = new byte[x];
				//System.out.println(" b size" + b.length);
				Debug.log(" b size" + b.length);
				fis.read(b);

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + modelName + ".csv");

				OutputStream os = response.getOutputStream();
				os.write(b);
				os.flush();

			} else {
				return "error";
			}

		} catch (Exception e) {
			//System.out.println("Exception eee" + e);
			Debug.log("Exception eee" + e);
			return "error";
		}finally{
			try{
				if(fis!=null){
					fis.close();
				}
			}catch(Exception e){
				
			}
		}
		return "success";
	}
}