/**
 * 
 */
package org.groupfio.etl.process.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.groupfio.etl.process.validator.Validator;
import org.groupfio.etl.process.validator.ValidatorFactory;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Sharif
 *
 */
public class ValidateServiceImpl {
	
	private static String MODULE = ValidateServiceImpl.class.getName();
	
	public static Map<String, Object> validateLeadData(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Map<String, Object> response = ServiceUtil.returnSuccess();
		
		try {
			
			Delegator delegator = dctx.getDelegator();
			GenericValue userLogin =  (GenericValue) context.get("userLogin");
			
			Map<String, Object> data = (Map<String, Object>) context.get("data");
			
			Map<String, Object> validatorResponse = new HashMap<String, Object>();
			
			Validator validator = ValidatorFactory.getLeadDataValidator();
			Map<String, Object> validatorContext = new HashMap<String, Object>();
			validatorContext.put("delegator", delegator);
			validatorContext.put("data", data);
			validatorContext.put("leadShortForm", context.get("leadShortForm"));
			/*validatorContext.put("modelName", currentListId);
			validatorContext.put("taskName", context.get("taskName"));
			validatorContext.put("tableName", context.get("tableName"));*/
			validatorContext.put("locale", context.get("locale"));
			
			validatorContext.put("isNotDuplicate", context.get("isNotDuplicate"));
			
			validatorContext.put("userLogin", userLogin);
			
			validatorResponse = validator.validate(validatorContext);
			
			data = (Map<String, Object>) validatorResponse.get("data");
			
			response.put("leadId", data.get("leadId"));
			response.put("errorCodes", data.get("errorCodes"));
			response.put("validatorResponse", validatorResponse);
			
		} catch (Exception e) {
			Debug.logError("createEtlPreProcessor ERROR: "+e.getMessage(), MODULE);
			return ServiceUtil.returnError(e.toString());
		}
		
		return response;
	}

}
