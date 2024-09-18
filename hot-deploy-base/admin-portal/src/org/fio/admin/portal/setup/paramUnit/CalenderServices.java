package org.fio.admin.portal.setup.paramUnit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.admin.portal.constant.AdminPortalConstant.HolidayCreatePermission;
import org.fio.admin.portal.constant.AdminPortalConstant.UserCreatePermission;
import org.fio.admin.portal.util.DataUtil;
import org.fio.homeapps.constants.GlobalConstants.ModeOfAction;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

import org.fio.admin.portal.util.DataUtil;

/***
 * 
 * @author Arshiya
 * @author Sharif
 *
 */
public class CalenderServices {
	
    public CalenderServices() {}
    
    private static final String MODULE = SlaSetupServices.class.getName();
    public static final String RESOURCE = "AdminPortalUiLabels";
    
    //Desc : Create / Non Working Day Setup
    public static Map<String, Object> calenderHolidayConfig(DispatchContext dctx, Map<String, Object> context) {
    	
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Security security = dctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> results = ServiceUtil.returnSuccess();
        String nonWorkingDate = (String) context.get("nonWorkingDate");
        String holidayDescription = (String) context.get("holidayDescription");
        
        String type = (String) context.get("type");
        String status = (String) context.get("status");
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String holidayConfigId  = (String) context.get("holidayConfigId");
        
        String isPerformUserAudit = (String) context.get("isPerformUserAudit");
        String userAuditRequestId = (String) context.get("userAuditRequestId");
        
        try {
        	String holoidayDate = convertToDate(nonWorkingDate);
            //GenericValue getHolidayConfig = EntityQuery.use(delegator).from("TechDataHolidayConfig").where("holidayConfigId",holidayConfigId).queryFirst();
           // GenericValue getHolidayConfig = EntityQuery.use(delegator).from("TechDataHolidayConfig").where("businessUnit",businessUnit,"holidayDate",java.sql.Date.valueOf(holoidayDate)).queryFirst();
            GenericValue getHolidayConfig = EntityQuery.use(delegator).from("TechDataHolidayConfig").where("holidayDate",java.sql.Date.valueOf(holoidayDate)).queryFirst();
            if(UtilValidate.isEmpty(getHolidayConfig)) {
            	
            	if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
            		Map<String, Object> callCtxt = FastMap.newInstance();
        			Map<String, Object> callResult = FastMap.newInstance();
        			
        			callCtxt.put("serviceRequestType", "CREATE_CAL_NONWRK_DAY");
        			callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
        			callCtxt.put("modeOfAction", ModeOfAction.CREATE);
        			callCtxt.put("remarks", null);
        			callCtxt.put("contextMap", context);
        			callCtxt.put("userAuditRequestId", userAuditRequestId);
        			
        			callCtxt.put("userLogin", userLogin);
        			
        			callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
        			return callResult;
            	}
            	
                holidayConfigId = delegator.getNextSeqId("TechDataHolidayConfig");
                
                GenericValue holidayConfigData = delegator.makeValue("TechDataHolidayConfig", UtilMisc.toMap("holidayConfigId", holidayConfigId,"holidayDate",java.sql.Date.valueOf(holoidayDate)));
                holidayConfigData.setNonPKFields(context);
                holidayConfigData.set("createdDate",UtilDateTime.nowTimestamp());
                holidayConfigData.set("createdBy",userLogin.getString("userLoginId"));
                holidayConfigData.create();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "CalenderHolidayConfigCreatedSuccessfully", locale));
                results.put("holidayConfigId", holidayConfigId);
            }else {
            	results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "Calender Holiday Configuration already exists", locale));
            }
        } catch (Exception e) {
            e.printStackTrace();
            results = ServiceUtil.returnError("Error : "+e.getMessage());
        }
        return results;
    }
    
    // update Calender
    public static Map<String, Object> updateCalenderHolidayConfig(DispatchContext dctx, Map<String, Object> context) {
    	
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Security security = dctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> results = ServiceUtil.returnSuccess();
        String nonWorkingDate = (String) context.get("nonWorkingDate");
        String holidayDescription = (String) context.get("holidayDescription");
        String type = (String) context.get("type");
        String status = (String) context.get("status");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String holidayConfigId  = (String) context.get("holidayConfigId");
        
        String isPerformUserAudit = (String) context.get("isPerformUserAudit");
        String userAuditRequestId = (String) context.get("userAuditRequestId");
        String oldContextMap = (String) context.get("oldContextMap");
        
        try {
            GenericValue getHolidayConfig = EntityQuery.use(delegator).from("TechDataHolidayConfig").where("holidayConfigId",holidayConfigId).queryFirst();
            if(UtilValidate.isNotEmpty(getHolidayConfig)) {
            	
            	if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
            		Map<String, Object> callCtxt = FastMap.newInstance();
        			Map<String, Object> callResult = FastMap.newInstance();
        			
        			callCtxt.put("serviceRequestType", "UPDATE_CAL_NONWRK_DAY");
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
            	
                getHolidayConfig.setNonPKFields(context);
                getHolidayConfig.set("modifiedDate",UtilDateTime.nowTimestamp());
                getHolidayConfig.set("modifiedBy",userLogin.getString("userLoginId"));
                getHolidayConfig.store();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "CalenderHolidayConfigUpdatedSuccessfully", locale));
            }
            results.put("holidayConfigId", holidayConfigId);
        } catch (Exception e) {
            e.printStackTrace();
            results = ServiceUtil.returnError("Error : "+e.getMessage());
        }
        return results;
    }
    public static String convertToDate(String dateReceivedFromUser) {

        DateFormat dateFormatNeeded  = new SimpleDateFormat("yyyy-mm-dd");
        DateFormat userDateFormat = new SimpleDateFormat("dd/mm/yyyy");
        Date date;
        String convertedDate = null;
        try {
            date = userDateFormat.parse(dateReceivedFromUser);
            convertedDate = dateFormatNeeded.format(date);
            //System.out.println("Converted Date is " + convertedDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return convertedDate;
    }
}
