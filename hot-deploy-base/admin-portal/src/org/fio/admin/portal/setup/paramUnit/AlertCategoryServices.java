package org.fio.admin.portal.setup.paramUnit;

import java.util.Locale;
import java.util.Map;

import org.fio.homeapps.constants.GlobalConstants.ModeOfAction;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

public class AlertCategoryServices {
	
	public static final String MODULE = AlertCategoryServices.class.getName();
	public static final String RESOURCE = "AdminPortalUiLabels";
	
	/* for creating alert category */
	public static Map<String, Object> createAlert(DispatchContext dctx, Map<String, Object> context) {
		Debug.logInfo("------inside createAlert------ " + context, MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> results = ServiceUtil.returnSuccess();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String alertName = (String) context.get("alertName");
		String alertType = (String) context.get("alertType");
		String alertPriority = (String) context.get("alertPriority");
		String autoClosure = (String) context.get("autoClosure");
		String duration = (String) context.get("duration");
		String status = (String) context.get("status");
		String remarks = (String) context.get("remarks");
		String seqNum = (String) context.get("sequenceNumber");
		String userLoginId = null;
		
		String isPerformUserAudit = (String) context.get("isPerformUserAudit");
        String userAuditRequestId = (String) context.get("userAuditRequestId");
		
		try {
			GenericValue alertDesc = EntityQuery.use(delegator).from("AlertCategory").where("alertCategoryName", alertName).queryOne();
			if(UtilValidate.isEmpty(alertDesc))
			{
				
				if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
            		Map<String, Object> callCtxt = FastMap.newInstance();
        			Map<String, Object> callResult = FastMap.newInstance();
        			
        			callCtxt.put("serviceRequestType", "CREATE_ALT_CAT");
        			callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
        			callCtxt.put("modeOfAction", ModeOfAction.CREATE);
        			callCtxt.put("remarks", null);
        			callCtxt.put("contextMap", context);
        			callCtxt.put("userAuditRequestId", userAuditRequestId);
        			
        			callCtxt.put("userLogin", userLogin);
        			
        			callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
        			return callResult;
            	}
				
				alertDesc = delegator.makeValue("AlertCategory");
				String alertCategoryId = delegator.getNextSeqId("AlertCategory");
				alertDesc.put("alertCategoryId", alertCategoryId);
				alertDesc.put("alertCategoryName", alertName);
				alertDesc.put("alertTypeId", alertType);
				alertDesc.put("alertPriority", alertPriority);
				if(UtilValidate.isEmpty(autoClosure))
				{
					alertDesc.put("alertAutoClosure", "No");
					alertDesc.put("alertAutoClosureDuration", null);
				}
				else
				{	alertDesc.put("alertAutoClosure", "Yes");
					alertDesc.put("alertAutoClosureDuration", duration);
				}
				alertDesc.put("alertAutoClosureDuration", duration);
				userLoginId = userLogin.getString("userLoginId");
				alertDesc.put("createdByUserLoginId", userLoginId);
				alertDesc.put("isActive", status);
				alertDesc.put("remarks", remarks);
				alertDesc.put("seqNum", seqNum);
				alertDesc.put("createdOn", UtilDateTime.nowTimestamp());
				alertDesc.create();
				results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AlertCreatedSuccessfully", locale));
				results.put("alertCategoryId", alertCategoryId);
			}else{
				return ServiceUtil.returnError("Alert Category already exists");
			}
		} catch (GeneralException e) {
			 Debug.log("==error in createAlert===" + e.getMessage());
		}
		return results;
	}
	
	/* for updating Alert Category */
	public static Map<String, Object> updateAlert(DispatchContext dctx, Map<String, Object> context) {
		Debug.logInfo("------inside updateAlert------ " + context, MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> results = ServiceUtil.returnSuccess();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String alertCategoryId = (String) context.get("alertCategoryId");
		String alertName = (String) context.get("alertName");
		String alertType = (String) context.get("alertType");
		String alertPriority = (String) context.get("alertPriority");
		String autoClosure = (String) context.get("autoClosure");
		String duration = (String) context.get("duration");
		String status = (String) context.get("status");
		String remarks = (String) context.get("remarks");
		String seqNum = (String) context.get("sequenceNumber");
		String userLoginId = null;
		
		String isPerformUserAudit = (String) context.get("isPerformUserAudit");
        String userAuditRequestId = (String) context.get("userAuditRequestId");
        String oldContextMap = (String) context.get("oldContextMap");
		
		try {
			GenericValue alertDetails = EntityQuery.use(delegator).from("AlertCategory").where("alertCategoryId", alertCategoryId).queryOne();
			if(UtilValidate.isNotEmpty(alertDetails))
			{
				
				if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
            		Map<String, Object> callCtxt = FastMap.newInstance();
        			Map<String, Object> callResult = FastMap.newInstance();
        			
        			callCtxt.put("serviceRequestType", "UPDATE_ALT_CAT");
        			callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
        			callCtxt.put("modeOfAction", ModeOfAction.UPDATE);
        			callCtxt.put("remarks", null);
        			callCtxt.put("contextMap", context);
        			callCtxt.put("userAuditRequestId", userAuditRequestId);
        			callCtxt.put("oldContextMap", oldContextMap);
        			
        			callCtxt.put("userLogin", userLogin);
        			
        			callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
        			return callResult;
            	}
				
				String name=alertDetails.getString("alertCategoryName");
				if(UtilValidate.isNotEmpty(name)&&UtilValidate.isNotEmpty(alertName)) {
					if(alertName.equalsIgnoreCase(name)){
						alertDetails.put("alertTypeId", alertType);
						alertDetails.put("alertPriority", alertPriority);
						if(UtilValidate.isEmpty(autoClosure))
						{
							alertDetails.put("alertAutoClosure", "No");
							alertDetails.put("alertAutoClosureDuration", null);
						}
						else
						{	alertDetails.put("alertAutoClosure", "Yes");
							alertDetails.put("alertAutoClosureDuration", duration);
						}
						userLoginId = userLogin.getString("userLoginId");
						alertDetails.put("modifiedUserLoginId",userLoginId );
						alertDetails.put("isActive", status);
						alertDetails.put("remarks", remarks);
						alertDetails.put("seqNum", seqNum);
						alertDetails.put("modifiedOn", UtilDateTime.nowTimestamp());
						alertDetails.store();
						results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AlertUpdatedSuccessfully", locale));
					}else{	
						GenericValue alertDesc = EntityUtil.getFirst(delegator.findByAnd("AlertCategory", UtilMisc.toMap("alertCategoryName",alertName), null, false));
						if(UtilValidate.isNotEmpty(alertDesc))
							return ServiceUtil.returnError("Alert Category already exists");
						else{
							alertDetails.put("alertCategoryName", alertName);
							alertDetails.put("alertTypeId", alertType);
							alertDetails.put("alertPriority", alertPriority);
							if(UtilValidate.isEmpty(autoClosure))
							{
								alertDetails.put("alertAutoClosure", "No");
								alertDetails.put("alertAutoClosureDuration", null);
							}
							else
							{	alertDetails.put("alertAutoClosure", "Yes");
								alertDetails.put("alertAutoClosureDuration", duration);
							}
							userLoginId = userLogin.getString("userLoginId");
							alertDetails.put("modifiedUserLoginId",userLoginId );
							alertDetails.put("isActive", status);
							alertDetails.put("remarks", remarks);
							alertDetails.put("seqNum", seqNum);
							alertDetails.put("modifiedOn", UtilDateTime.nowTimestamp());
							alertDetails.store();
							results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "AlertUpdatedSuccessfully", locale));
						}
					}
				}
			}
			results.put("alertCategoryId", alertCategoryId);
		} catch (GeneralException e) {
			Debug.log("==error in updateAlert===" + e.getMessage());
		}
		return results;
	}

}
