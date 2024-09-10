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
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;

/**
 * @author Group Fio
 *
 */
public class ValidationAuditWriter implements Writer {

	private static String MODULE = ValidationAuditWriter.class.getName();
	
	/* (non-Javadoc)
	 * @see org.groupfio.etl.process.writer.Writer#write(java.util.Map)
	 */
	@Override
	public Map<String, Object> write(Map<String, Object> context) {
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			
			Delegator delegator = (Delegator) context.get("delegator");
			
			String pkCombinedValueText = ParamUtil.getString(context, "pkCombinedValueText");
			
			String changedFieldName = ParamUtil.getString(context, "changedFieldName");
			String oldValueText = ParamUtil.getString(context, "oldValueText");
			String newValueText = ParamUtil.getString(context, "newValueText");
			String changedByInfo = ParamUtil.getString(context, "changedByInfo");
			String validationAuditType = ParamUtil.getString(context, "validationAuditType");
			String comments = ParamUtil.getString(context, "comments");
			
			TransactionUtil.begin();
			
			GenericValue makeError = delegator.makeValue("ValidationAuditLog");
			
			makeError.put("validationAuditLogId",delegator.getNextSeqId("ValidationAuditLog"));
			
			makeError.put("pkCombinedValueText", pkCombinedValueText);
			makeError.put("changedFieldName", changedFieldName);
			makeError.put("oldValueText", oldValueText);
			makeError.put("newValueText", newValueText);
			makeError.put("changedByInfo", changedByInfo);
			makeError.put("validationAuditType", validationAuditType);
			makeError.put("comments", comments);
			
			makeError.create();
			
			TransactionUtil.commit();
			
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Validation Audit write Failed...!");
			
			return response;
		}
		
		response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;
	}

}
