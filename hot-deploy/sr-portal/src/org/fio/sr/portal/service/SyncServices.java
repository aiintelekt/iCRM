/**
 * 
 */
package org.fio.sr.portal.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.homeapps.util.UtilDateTime;
import org.fio.sr.portal.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class SyncServices {

	private static final String MODULE = SyncServices.class.getName();

	public static Map syncSrOrderAssociation(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		//String srNumber = (String) context.get("srNumber");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			
			List<GenericValue> resultList = delegator.findAll("CustRequest", false);
			Debug.logInfo("syncSrOrderAssociation resultList: "+resultList.size(), MODULE);
			Debug.logInfo("syncSrOrderAssociation START: "+UtilDateTime.nowTimestamp(), MODULE);	
			if (UtilValidate.isNotEmpty(resultList)) {
				int count = 1;
				for (GenericValue sr : resultList) {
					String custRequestId = sr.getString("custRequestId");
					Debug.logInfo("SYNC SR Order Association, SR#"+custRequestId+", count: "+(count++), MODULE);
					DataUtil.syncSrOrderAssociation(delegator, custRequestId);
				}
			}
			Debug.logInfo("syncSrOrderAssociation END: "+UtilDateTime.nowTimestamp(), MODULE);		
			
			/*Map<String, Object> requestContext = new LinkedHashMap<>();
			
			callCtxt.put("requestContext", requestContext);

			callCtxt.put("userLogin", userLogin);
			
			callResult = dispatcher.runSync("common.findServiceRequest", callCtxt);

			if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("srList"))) {

				List<GenericValue> resultList = (List<GenericValue>) callResult.get("srList");
				
				if (UtilValidate.isNotEmpty(resultList)) {
					for (GenericValue sr : resultList) {
						String custRequestId = sr.getString("custRequestId");
						Debug.logInfo("SYNC SR Order Association, SR#"+custRequestId, MODULE);
						DataUtil.syncSrOrderAssociation(delegator, custRequestId);
					}
				}
				
			}*/
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully SYNC SR Order Association.."));

		return result;

	}
	
}
