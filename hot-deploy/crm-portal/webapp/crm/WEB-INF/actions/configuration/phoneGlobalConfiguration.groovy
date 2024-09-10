import java.util.Map;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;
import javolution.util.FastList;
import javolution.util.FastMap;

Map optionMap = new LinkedHashMap();
optionMap.put("Y", "Yes");
optionMap.put("N", "No");
context.put("optionMap", optionMap);

maxCallNumExisting = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "CUST_CALL_NUM"), false);
if (UtilValidate.isNotEmpty(maxCallNumExisting)) {
    maxCallNum = maxCallNumExisting.getString("value");
    if (UtilValidate.isNotEmpty(maxCallNum)) {
        context.put("maxCallNum", maxCallNum);
    }
}
hhMaxnumExisting = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "HH_CUST_ELG_VALUE"), false);
if (UtilValidate.isNotEmpty(hhMaxnumExisting)) {
    hhMaxnum = hhMaxnumExisting.getString("value");
    if (UtilValidate.isNotEmpty(maxCallNum)) {
        context.put("hhMaxnum", hhMaxnum);
    }
}
callDurationDaysExisting = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "NXT_CALL_DURATION"), false);
if (UtilValidate.isNotEmpty(callDurationDaysExisting)) {
    callDurationDays = callDurationDaysExisting.getString("value");
    if (UtilValidate.isNotEmpty(maxCallNum)) {
        context.put("callDurationDays", callDurationDays);
    }
}
outBoundCallEnabled = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "OUTBOUND_CALL"), false);
if (UtilValidate.isNotEmpty(outBoundCallEnabled)) {
    outBoundCall = outBoundCallEnabled.getString("value");
    if (UtilValidate.isNotEmpty(outBoundCall)) {
        context.put("outBoundCall", outBoundCall);
    }
}
emailDurationDays = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "NXT_EMAIL_DURATION"), false);
if (UtilValidate.isNotEmpty(emailDurationDays)) {
    emailDuration = emailDurationDays.getString("value");
    if (UtilValidate.isNotEmpty(emailDuration)) {
        context.put("emailDuration", emailDuration);
    }
}
smsDurationDays = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "NXT_SMS_DURATION"), false);
if (UtilValidate.isNotEmpty(smsDurationDays)) {
    smsDuration = smsDurationDays.getString("value");
    if (UtilValidate.isNotEmpty(smsDuration)) {
        context.put("smsDuration", smsDuration);
    }
}