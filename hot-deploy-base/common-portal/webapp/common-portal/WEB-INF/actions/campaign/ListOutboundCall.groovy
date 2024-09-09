import org.ofbiz.base.util.UtilValidate;

context.put("activeMessType", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACTIVE_MESS_TYPE"));
context.put("activeRcMessAPI", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "RC_MSG_API", "MVP"));
