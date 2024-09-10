/**
 * 
 */
package org.groupfio.common.portal.util;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class UtilNote {

	private static final String MODULE = UtilNote.class.getName();
	
	public static Map<String, Object> createNote(Delegator delegator, Map<String, Object> context) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
		try {
			LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			
			String approvalComments = (String) context.get("approvalComments");
			String noteName = (String) context.get("noteName");
			String isImportant = (String) context.get("isImportant");
			
			String domainEntityType = (String) context.get("domainEntityType");
			String domainEntityId = (String) context.get("domainEntityId");
			String emplTeamId = (String) context.get("emplTeamId");
			String businessUnit = (String) context.get("businessUnit");
			
			Timestamp curentTime = UtilDateTime.nowTimestamp();
			
			if (UtilValidate.isNotEmpty(approvalComments)) {
				
				Map<String, Object> requestContext = new HashMap<>();
				
				requestContext.put("note", approvalComments);
				requestContext.put("noteName", noteName);
				requestContext.put("isImportant", isImportant);
				requestContext.put("noteType", "NTC_APV");
				requestContext.put("domainEntityType", domainEntityType);
				requestContext.put("domainEntityId", domainEntityId);
				
				callCtxt.put("requestContext", requestContext);
				callCtxt.put("userLogin", userLogin);
				
				callResult = dispatcher.runSync("common.createNoteData", callCtxt);
				if (ServiceUtil.isSuccess(callResult)) {
					Debug.logInfo("Successfully created note data for approval, domainEntityType#"+domainEntityType+", domainEntityId#"+domainEntityId, MODULE);
					response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				} else {
					response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					response.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(callResult));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			response.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
		}
		return response;
	}
	
}
