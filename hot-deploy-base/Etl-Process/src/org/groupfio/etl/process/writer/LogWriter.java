/**
 * 
 */
package org.groupfio.etl.process.writer;

import java.util.HashMap;
import java.util.Map;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;

/**
 * @author Group Fio
 *
 */
public class LogWriter implements Writer {

	private static String MODULE = LogWriter.class.getName();
	
	/* (non-Javadoc)
	 * @see org.groupfio.etl.process.writer.Writer#write(java.util.Map)
	 */
	@Override
	public Map<String, Object> write(Map<String, Object> context) {
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			
			Delegator delegator = (Delegator) context.get("delegator");
			String taskName = ParamUtil.getString(context, "taskName");
			String logMsg = ParamUtil.getString(context, "logMsg");
			String fileName = ParamUtil.getString(context, "fileName");
			String tableName = ParamUtil.getString(context, "tableName");
			String modelName = ParamUtil.getString(context, "modelName");
			String status = ParamUtil.getString(context, "status");
			
			if (UtilValidate.isEmpty(status)) {
				status = "ERROR";
			}
			TransactionUtil.begin();
			GenericValue makeError = delegator.makeValue("EtlLogProcError");
			makeError.put("seqId",delegator.getNextSeqId("EtlLogProcError"));
			makeError.put("taskId", delegator.getNextSeqId("EtlLogProcError"));
			
			makeError.put("taskName", taskName);
			makeError.put("timeStamp", UtilDateTime.nowTimestamp());
			makeError.put("status", "ERROR");
			makeError.put("logMsg1", logMsg);
			makeError.put("logMsg2", fileName);
			makeError.put("tableName", tableName);
			makeError.put("listId", modelName);
			
			makeError.create();
			TransactionUtil.commit();
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Log write Failed...!");
			
			return response;
		}
		
		response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;
	}

}
