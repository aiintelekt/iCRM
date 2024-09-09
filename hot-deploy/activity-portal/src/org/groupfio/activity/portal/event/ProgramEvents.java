package org.groupfio.activity.portal.event;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.constants.GlobalConstants.DateTimeTypeConstant;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.StatusUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.event.AjaxEvents;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.common.portal.util.UtilAttribute;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif Ul Islam
 * 
 */
public final class ProgramEvents {

    private static final String MODULE = ProgramEvents.class.getName();
    
    public static String generateProgAct(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String srNumber = request.getParameter("srNumber");
		
		String actFromDate = request.getParameter("genActDate");
		//String actFromDate = request.getParameter("genActDate_from");
		//String actToDate = request.getParameter("genActDate_to");
		String groupingCodeId = request.getParameter("groupingCodeId");
		String numberOfDays = request.getParameter("numberOfDays");
		
		String ownerPartyId = request.getParameter("ownerPartyId");
		String ownerRoleTypeId = request.getParameter("ownerRoleTypeId");
		String ownerUserLoginId = request.getParameter("ownerUserLoginId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String nativeBusinessUnit = null;
			String nativeTeamId = null;
			
			ownerPartyId = UtilValidate.isNotEmpty(ownerPartyId) ? ownerPartyId : userLogin.getString("partyId");
			ownerRoleTypeId = UtilValidate.isNotEmpty(ownerRoleTypeId) ? ownerRoleTypeId : PartyHelper.getPartyRoleTypeId(userLogin.getString("partyId"), delegator);
			ownerUserLoginId = UtilValidate.isEmpty(ownerUserLoginId) && UtilValidate.isEmpty(ownerPartyId) ? userLogin.getString("userLoginId") : ownerUserLoginId;
			
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			Map<String, Object> requestContext = FastMap.newInstance();
			
			requestContext.put("srNumber", srNumber);
			requestContext.put("actFromDate", actFromDate);
			requestContext.put("actToDate", null);
			requestContext.put("groupingCodeId", groupingCodeId);
			requestContext.put("numberOfDays", numberOfDays);
			
			requestContext.put("ownerPartyId", ownerPartyId);
			requestContext.put("ownerRoleTypeId", ownerRoleTypeId);
			requestContext.put("ownerUserLoginId", ownerUserLoginId);
			
			requestContext.put("domainEntityType", DomainEntityType.SERVICE);
			requestContext.put("domainEntityId", srNumber);
			requestContext.put("emplTeamId", nativeTeamId);
			requestContext.put("businessUnit", nativeBusinessUnit);
			
			callCtxt.put("requestContext", requestContext);

			callCtxt.put("userLogin", userLogin);

			Debug.log("inMap==============" + callCtxt);

			callResult = dispatcher.runSync("activity.generateProgAct", callCtxt);
			if (ServiceUtil.isSuccess(callResult)) {
				UtilAttribute.storeServiceAttrValue(delegator, srNumber, "IS_GENERATED_PROG_ACT", "Y");
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully generated program activities");
			} else {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(callResult));
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
