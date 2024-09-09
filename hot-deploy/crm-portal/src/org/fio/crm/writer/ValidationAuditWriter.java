/**
 * 
 */
package org.fio.crm.writer;

import java.util.HashMap;
import java.util.Map;

import org.fio.crm.constants.CrmConstants;
import org.fio.crm.constants.ResponseCodes;
import org.fio.crm.util.ParamUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;

/**
 * @author Sharif
 *
 */
public class ValidationAuditWriter implements Writer {

	private static String MODULE = ValidationAuditWriter.class.getName();
	
	/* (non-Javadoc)
	 * @see org.fio.crm.writer.Writer#write(java.util.Map)
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
			
			response.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(CrmConstants.RESPONSE_MESSAGE, "Validation Audit write Failed...!");
			
			return response;
		}
		
		response.put(CrmConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;
	}

}
