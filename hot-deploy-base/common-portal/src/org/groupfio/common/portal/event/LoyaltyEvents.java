/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.groupfio.common.portal.event;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.ResponseUtils;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif Ul Islam
 * 
 */
public final class LoyaltyEvents {

    private static final String MODULE = LoyaltyEvents.class.getName();
    
    public static String sendEreceipt(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String transactionNumber = request.getParameter("transactionNumber");
		String isRmsOrder = request.getParameter("isRmsOrder");
		
		Map<String, Object> result = FastMap.newInstance();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		try {
			String erOrdhtmlTplId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ER_ORDHTML_TPL_ID");
			String isLoyaltyEnable = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_LOYALTY_ENABLE");
			if (UtilValidate.isNotEmpty(isLoyaltyEnable) && isLoyaltyEnable.equals("Y")) {
				Map<String, Object> callCtxt = FastMap.newInstance();
	            Map<String, Object> callResult = FastMap.newInstance();
	            
	            if (UtilValidate.isNotEmpty(transactionNumber)) {
	            	List<String> orderIds = StringUtil.split(transactionNumber, ",");
	            	List<String> errorMessageList = new ArrayList<>();
	            	for (String orderId : orderIds) {
	            		callCtxt = FastMap.newInstance();
	            		
	            		Map<String, Object> requestContext = FastMap.newInstance();
	            		requestContext.put("orderId", orderId);
	            		requestContext.put("erOrdhtmlTplId", erOrdhtmlTplId);
	            		requestContext.put("isRmsOrder", isRmsOrder);
	            		
	            		callCtxt.put("requestContext", requestContext);
	            		callCtxt.put("userLogin", userLogin);
	    	            
	            		//dispatcher.runAsync("loyalty.sendEreceipt", callCtxt);
	            		
	    	            callResult = dispatcher.runSync("loyalty.sendEreceipt", callCtxt);
	    	            if (ResponseUtils.isError((Map<String, Object>) callResult.get("responseContext"))) {
	    	            	Map<String, Object> responseContext = (Map<String, Object>) callResult.get("responseContext");
	    	            	Debug.logError((String) responseContext.get(GlobalConstants.RESPONSE_MESSAGE), MODULE);
	    	            	errorMessageList.add((String) responseContext.get(GlobalConstants.RESPONSE_MESSAGE));
	    	            	/*result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.EXPECTATION_FAILED_CODE);
	    					result.put(GlobalConstants.RESPONSE_MESSAGE, "Error! "+ServiceUtil.getErrorMessage(callResult));*/
	    	            }
	            	}
	            	
	            	if (UtilValidate.isNotEmpty(errorMessageList)) {
	            		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.EXPECTATION_FAILED_CODE);
    					result.put(GlobalConstants.RESPONSE_MESSAGE, "Error! "+StringUtil.join(errorMessageList, "<br>"));
	            	} else {
	            		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
						result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully send ereceipt");
	            	}
	            } else {
	            	result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.EXPECTATION_FAILED_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Transaction Number empty!");
	            }
			} else {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.EXPECTATION_FAILED_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Loyalty module not enabled");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
    
}
