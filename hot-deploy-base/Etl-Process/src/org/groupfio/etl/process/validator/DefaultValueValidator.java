/**
 * 
 */
package org.groupfio.etl.process.validator;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilMessage;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.util.DefaultValueUtil;
import org.groupfio.etl.process.writer.WriterUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;

/**
 * @author Sharif
 *
 */
public class DefaultValueValidator implements Validator {

	private static String MODULE = DefaultValueValidator.class.getName();
	
	/* (non-Javadoc)
	 * @see org.groupfio.etl.process.validator.Validator#validate(java.util.Map)
	 */
	@Override
	public Map<String, Object> validate(Map<String, Object> context) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			Delegator delegator = (Delegator) context.get("delegator");
			int parseCount = ParamUtil.getInteger(context, "parseCount");
			String modelName = ParamUtil.getString(context, "modelName");
			
			String taskName = ParamUtil.getString(context, "taskName");
			String tableName = ParamUtil.getString(context, "tableName");
			
			boolean isValidate = true;
			String errorSummary = "";
			String errorMessage = "";
			
			String recordCount = DefaultValueUtil.getModelDefaultValue(modelName, "recordCount", delegator);
			if (!StringUtils.isEmpty(recordCount) && recordCount.length() > 0) {
				if (parseCount > Integer.parseInt(recordCount)) {
					isValidate = false;
					errorMessage = "Imported file counts should not be more than# "+recordCount+", Please check configuration (or) Please contact support team for more details";
					errorSummary += " | " + errorMessage;
					
					WriterUtil.writeLog(delegator, taskName, errorMessage, tableName, modelName);
				}
				/*if (Integer.parseInt(recordCount) != parseCount) {
					isValidate = false;
					errorMessage = "Total records counts not matching in the imported file as per configuration setup!!!";
					errorSummary += " | " + errorMessage;
					
					WriterUtil.writeLog(delegator, taskName, errorMessage, tableName, modelName);
				}*/
			}
			
			if (!isValidate) {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(EtlConstants.RESPONSE_MESSAGE, errorSummary);
				
				return response;
			}
		} catch (Exception e) {
			Debug.log(UtilMessage.getPrintStackTrace(e), MODULE);
			
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Default Value Validation Failed...!");
			return response;
		}
		response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return response;
	}

}
