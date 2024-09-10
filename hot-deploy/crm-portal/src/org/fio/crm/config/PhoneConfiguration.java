package org.fio.crm.config;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

public class PhoneConfiguration {

    public static final String resource = "crmUiLabels";
    public static final String module = PhoneConfiguration.class.getName();

    public static String updatePhoneGlobalParameter(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        String maxCallNum = request.getParameter("maxCallNum");
        String callDurationDays = request.getParameter("callDurationDays");
        String hhMaxnum = request.getParameter("hhMaxDays");
        String outBoundCall = request.getParameter("outBoundCall");
        String emailDurationDays = request.getParameter("emailDurationDays");
        String smsDurationDays = request.getParameter("smsDurationDays");
        String msg = "";
        String returnMsg = null;
        try {
            GenericValue maxCallNumExisting = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "CUST_CALL_NUM"), false);
            if (UtilValidate.isEmpty(maxCallNumExisting)) {
                maxCallNumExisting = delegator.makeValue("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "CUST_CALL_NUM"));
                maxCallNumExisting.set("value", maxCallNum);
                maxCallNumExisting.set("description", "Max Number of calls per Customer");
                maxCallNumExisting.create();
            } else {
                maxCallNumExisting.set("value", maxCallNum);
                maxCallNumExisting.store();
                msg = UtilProperties.getMessage(resource, "phoneSettingUpdate", locale);
                request.setAttribute("_EVENT_MESSAGE_", msg);
            }
            GenericValue maxHHCallNumExisting = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "HH_CUST_ELG_VALUE"), false);
            if (UtilValidate.isEmpty(maxHHCallNumExisting)) {
                maxHHCallNumExisting = delegator.makeValue("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "HH_CUST_ELG_VALUE"));
                maxHHCallNumExisting.set("description", "Maximum number of calls per each House Hold Id");
                maxHHCallNumExisting.set("value", hhMaxnum);
                maxHHCallNumExisting.create();
            } else {
                maxHHCallNumExisting.set("value", hhMaxnum);
                maxHHCallNumExisting.store();
            }
            GenericValue callDurationDaysExisting = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "NXT_CALL_DURATION"), false);
            if (UtilValidate.isEmpty(callDurationDaysExisting)) {
                callDurationDaysExisting = delegator.makeValue("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "NXT_CALL_DURATION"));
                callDurationDaysExisting.set("value", callDurationDays);
                callDurationDaysExisting.set("description", "Time Interval to next call");
                callDurationDaysExisting.create();
            } else {
                callDurationDaysExisting.set("value", callDurationDays);
                callDurationDaysExisting.store();
            }
            GenericValue outBoundCallEnabled = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "OUTBOUND_CALL"), false);
            if (UtilValidate.isEmpty(outBoundCallEnabled)) {
                outBoundCallEnabled = delegator.makeValue("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "OUTBOUND_CALL"));
                outBoundCallEnabled.set("value", outBoundCall);
                if ("Y".equalsIgnoreCase(outBoundCall)) {
                    outBoundCallEnabled.set("description", "OutBound Call Enabled");
                    msg = "Enabled";
                }
                if ("N".equalsIgnoreCase(outBoundCall)) {
                    outBoundCallEnabled.set("description", "OutBound Call Disabled");
                    msg = "Disabled";
                }
                outBoundCallEnabled.create();
            } else {
                outBoundCallEnabled.set("value", outBoundCall);
                if ("Y".equalsIgnoreCase(outBoundCall)) {
                    outBoundCallEnabled.set("description", "OutBound Call Enabled");
                }
                if ("N".equalsIgnoreCase(outBoundCall)) {
                    outBoundCallEnabled.set("description", "OutBound Call Updated Disabled");
                }
                outBoundCallEnabled.store();
            }
            GenericValue nextEmailDuratonDay = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "NXT_EMAIL_DURATION"), false);
            if (UtilValidate.isEmpty(nextEmailDuratonDay)) {
            	nextEmailDuratonDay = delegator.makeValue("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "NXT_EMAIL_DURATION"));
            	nextEmailDuratonDay.set("value", emailDurationDays);
            	nextEmailDuratonDay.set("description", "Time Intervel for next Email");
            	nextEmailDuratonDay.create();
            } else {
            	nextEmailDuratonDay.set("value", emailDurationDays);
            	nextEmailDuratonDay.store();
                msg = UtilProperties.getMessage(resource, "phoneSettingUpdate", locale);
                request.setAttribute("_EVENT_MESSAGE_", msg);
            }
            GenericValue nextSmsDuratonDay = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "NXT_SMS_DURATION"), false);
            if (UtilValidate.isEmpty(nextSmsDuratonDay)) {
            	nextSmsDuratonDay = delegator.makeValue("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "NXT_SMS_DURATION"));
            	nextSmsDuratonDay.set("value", smsDurationDays);
            	nextSmsDuratonDay.set("description", "Time Intervel for next SMS");
            	nextSmsDuratonDay.create();
            } else {
            	nextSmsDuratonDay.set("value", smsDurationDays);
            	nextSmsDuratonDay.store();
                msg = UtilProperties.getMessage(resource, "phoneSettingUpdate", locale);
                request.setAttribute("_EVENT_MESSAGE_", msg);
            }
            

        } catch (Exception e) {
            Debug.logError("Exception in phone settings" + e.getMessage(), module);
            returnMsg = UtilProperties.getMessage(resource, "errorInPhoneSettings", locale);
            request.setAttribute("_ERROR_MESSAGE_", returnMsg);
            return "error";
        }
        returnMsg = UtilProperties.getMessage(resource, "phoneSettingsUpdatedSuccessfully", locale);
        request.setAttribute("_EVENT_MESSAGE_", returnMsg);
        return "success";
    }
}