package org.fio.homeapps.writer;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.util.ParamUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;

/**
 * @author Sharif
 *
 */
public class LogWriter implements Writer {

	private static String MODULE = LogWriter.class.getName();
	
	@Override
	public Map<String, Object> write(Map<String, Object> context) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			TransactionUtil.begin();
			
			Delegator delegator = (Delegator) context.get("delegator");
			
			String serviceName = ParamUtil.getString(context, "serviceName");
			String version = ParamUtil.getString(context, "version");
			String clientLogRefId = ParamUtil.getString(context, "clientLogRefId");
			String requestedData = ParamUtil.getString(context, "requestedData");
			String responsedData = ParamUtil.getString(context, "responsedData");
			String responseCode = ParamUtil.getString(context, "responseCode");
			String clientRegistryId = ParamUtil.getString(context, "clientRegistryId");
            String msguid = ParamUtil.getString(context, "msguid");
            String orgId = ParamUtil.getString(context, "orgId");
            String systemName = ParamUtil.getString(context, "systemName");
			String responseStatus = ParamUtil.getString(context, "responseStatus");
			
			Timestamp requestedTime = (Timestamp) context.get("requestedTime");
			Timestamp responsedTime = (Timestamp) context.get("responsedTime");
			
			if (UtilValidate.isEmpty(responseStatus)) {
				responseStatus = "ERROR";
			}
			
			if (UtilValidate.isNotEmpty(requestedData)) {
				requestedData = ParamUtil.isValidJson(requestedData) ? requestedData.toString() : ParamUtil.toJson(requestedData);
			}
			if (UtilValidate.isNotEmpty(responsedData)) {
				responsedData = ParamUtil.isValidJson(responsedData) ? responsedData.toString() : ParamUtil.toJson(responsedData);
			}
			
			GenericValue apiLog = delegator.makeValue("OfbizApiLog");
			
			String ofbizApiLogId = delegator.getNextSeqId("OfbizApiLog");
			
			apiLog.put("ofbizApiLogId", ofbizApiLogId);
			
			apiLog.put("serviceName", serviceName);
			apiLog.put("version", version);
			apiLog.put("clientLogRefId", clientLogRefId);
			apiLog.put("requestedData", requestedData);
			apiLog.put("responsedData", responsedData);
			apiLog.put("responseCode", responseCode);
			apiLog.put("responseStatus", responseStatus);
			apiLog.put("clientRegistryId", clientRegistryId);
			apiLog.put("requestedTime", requestedTime);
			apiLog.put("responsedTime", responsedTime);
            apiLog.put("msguid", msguid);
            apiLog.put("orgId", orgId);
            apiLog.put("systemName", systemName);
			
			apiLog.create();
			TransactionUtil.commit();
			
			response.put("apiLog", apiLog);
			response.put("ofbizApiLogId", ofbizApiLogId);
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
			
			response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(GlobalConstants.RESPONSE_MESSAGE, "Log write Failed...!");
			
			return response;
		}
		response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return response;
	}

}

