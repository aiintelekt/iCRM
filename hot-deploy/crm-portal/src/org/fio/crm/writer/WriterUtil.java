/**
 * 
 */
package org.fio.crm.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.crm.constants.CrmConstants;
import org.fio.crm.constants.ResponseCodes;
import org.fio.crm.util.ResponseUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

/**
 * @author Sharif
 *
 */
public class WriterUtil {

	private static String MODULE = WriterUtil.class.getName();
	
	public static Map<String, Object> writeValidationAudit(Delegator delegator, String pkCombinedValueText, List<Map<String, Object>> validationAuditLogList) {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			
			long successCount = 0;
			long failedCount = 0;
			
			if (UtilValidate.isNotEmpty(pkCombinedValueText) && UtilValidate.isNotEmpty(validationAuditLogList)) {
				Writer writer = WriterFactory.getValidationAuditWriter();
				
				for (Map<String, Object> validationAuditLog : validationAuditLogList) {
					Map<String, Object> writerContext = new HashMap<String, Object>();
					writerContext.put("delegator", delegator);
					
					writerContext.put("pkCombinedValueText", pkCombinedValueText);
					
					writerContext.put("changedFieldName", validationAuditLog.get("changedFieldName"));
					writerContext.put("oldValueText", validationAuditLog.get("oldValueText"));
					writerContext.put("newValueText", validationAuditLog.get("newValueText"));
					writerContext.put("changedByInfo", validationAuditLog.get("changedByInfo"));
					
					writerContext.put("validationAuditType", validationAuditLog.get("validationAuditType"));
					writerContext.put("comments", validationAuditLog.get("comments"));
					
					Map<String, Object> writerResponse = writer.write(writerContext);
					
					if (ResponseUtils.isError(writerResponse)) {
						failedCount++;
					} else {
						successCount++;
					}
				}
				
			}
			
			response.put("successCount", successCount);
			response.put("failedCount", failedCount);
			
		} catch (Exception e) {
			Debug.logError("Error validation audit write log>>"+e.getMessage(), MODULE);
			
			response.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(CrmConstants.RESPONSE_MESSAGE, "Validation Audit write Failed...!");
			
			return response;
		}
		
		response.put(CrmConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;
	}
	
	public static boolean writeValidationAudit(Delegator delegator, String pkCombinedValueText, String changedFieldName, String oldValueText, String newValueText, String changedByInfo, String validationAuditType, String comments) {
		
		try {
			
			Writer writer = WriterFactory.getValidationAuditWriter();
			
			Map<String, Object> writerContext = new HashMap<String, Object>();
			writerContext.put("delegator", delegator);
			
			writerContext.put("pkCombinedValueText", pkCombinedValueText);
			
			writerContext.put("changedFieldName", changedFieldName);
			writerContext.put("oldValueText", oldValueText);
			writerContext.put("newValueText", newValueText);
			writerContext.put("changedByInfo", changedByInfo);
			
			writerContext.put("validationAuditType", validationAuditType);
			writerContext.put("comments", comments);
			
			Map<String, Object> writerResponse = writer.write(writerContext);
			
			if (ResponseUtils.isError(writerResponse)) {
				return false;
			}
			
		} catch (Exception e) {
			Debug.logError("Error validation audit write log>>"+e.getMessage(), MODULE);
			return false;
		}
		
		return true;
	}
	
	public static Map<String, Object> prepareValidationAudit(String pkCombinedValueText, String changedFieldName, String oldValueText, String newValueText, String changedByInfo, String validationAuditType, String comments) {
		
		Map<String, Object> writerContext = new HashMap<String, Object>();
		
		try {
			
			writerContext.put("pkCombinedValueText", pkCombinedValueText);
			
			writerContext.put("changedFieldName", changedFieldName);
			writerContext.put("oldValueText", oldValueText);
			writerContext.put("newValueText", newValueText);
			writerContext.put("changedByInfo", changedByInfo);
			
			writerContext.put("validationAuditType", validationAuditType);
			writerContext.put("comments", comments);
			
		} catch (Exception e) {
			Debug.logError("Error prepare validation audit log>>"+e.getMessage(), MODULE);
		}
		
		return writerContext;
	}
	
}
