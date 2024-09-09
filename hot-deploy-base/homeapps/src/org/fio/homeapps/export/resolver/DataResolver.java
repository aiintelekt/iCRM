/**
 * 
 */
package org.fio.homeapps.export.resolver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.export.ExportConstants.ExportDataType;
import org.fio.homeapps.util.ParamUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
	
/**
 * @author Sharif
 */
public class DataResolver extends Resolver {
	
	private static final String MODULE = DataResolver.class.getName();

	private static DataResolver instance;
	
	public static synchronized DataResolver getInstance(){
        if(instance == null) {
            instance = new DataResolver();
        }
        return instance;
    }
	
	@Override
	protected Map<String, Object> doResolve(Map<String, Object> context) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		Integer resultListSize = new Integer(0);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String errorMessage = null;
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator");
				
				HttpServletRequest request = (HttpServletRequest) context.get("request");
				HttpServletResponse response = (HttpServletResponse) context.get("response");
				
				String exportDataType = (String) context.get("exportDataType");
				
				switch (exportDataType) {
				case ExportDataType.OUTBOUND_CALL_LIST:
					
					Class<?> dataClass = Class.forName("org.fio.campaign.events.AjaxEvents");
					Object instance = dataClass.newInstance();
					Method dataMethod = dataClass.getMethod("searchOutBoundCallList", HttpServletRequest.class, HttpServletResponse.class);
					Map<String, Object> callResult = ParamUtil.jsonToMap((String) dataMethod.invoke(instance, request, response));
					resultListSize = (Integer) callResult.get("resultListSize");
					dataList = (List<Map<String, Object>>) callResult.get("list");
					errorMessage = (String) callResult.get("errorMessage");
					
					break;
				case ExportDataType.CUSTOMER_LIST:
					
					dataClass = Class.forName("org.groupfio.common.portal.event.AjaxEvents");
					instance = dataClass.newInstance();
					dataMethod = dataClass.getMethod("searchCustomers", HttpServletRequest.class, HttpServletResponse.class);
					callResult = ParamUtil.jsonToMap((String) dataMethod.invoke(instance, request, response));
					resultListSize = (Integer) callResult.get("recordCount");
					dataList = (List<Map<String, Object>>) callResult.get("list");
					errorMessage = (String) callResult.get("errorMessage");
					
					break;
				case ExportDataType.ACCOUNT_LIST:
					
					dataClass = Class.forName("org.groupfio.account.portal.event.AjaxEvents");
					instance = dataClass.newInstance();
					dataMethod = dataClass.getMethod("findAccounts", HttpServletRequest.class, HttpServletResponse.class);
					callResult = ParamUtil.jsonToMap((String) dataMethod.invoke(instance, request, response));
					resultListSize = (Integer) callResult.get("recordCount");
					dataList = (List<Map<String, Object>>) callResult.get("list");
					errorMessage = (String) callResult.get("errorMessage");
					
					break;
				default:
					break;
				}
				
				result.put("resultListSize", resultListSize);
				result.put("dataList", dataList);
				result.put("errorMessage", errorMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return result;
		}
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		return result;
	}
	
}
