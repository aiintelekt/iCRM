import org.ofbiz.base.util.*;

import org.fio.homeapps.util.PartyHelper;

delegator = request.getAttribute("delegator");

String isEnableRebateModule = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_REBATE_MODULE");
//if(UtilValidate.isNotEmpty(isEnableRebateModule)){
	context.put("isEnableRebateModule", isEnableRebateModule);
//}

//println("isEnableRebateModule>>>>>>"+isEnableRebateModule);

isApprovalEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "REBATE_APPROVAL_ENABLED");
context.put("isApprovalEnabled", isApprovalEnabled);

String isLoyaltyEnable = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_LOYALTY_ENABLE", "N");
context.put("isLoyaltyEnable", isLoyaltyEnable);

isProcessFlowEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "PRC_FLOW_ENABLED");
context.put("isProcessFlowEnabled", isProcessFlowEnabled);

String isEnableIUCInt = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IUC_INT_ENABLED");
if(UtilValidate.isNotEmpty(isEnableIUCInt) && isEnableIUCInt.equals("Y")){
	String iucUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IUC_URL");
	//String iucUrl = session.getAttribute("externalRefererUrl");
	context.put("iucUrl", iucUrl);
	
	String token = org.fio.admin.portal.util.UtilCommon.getSSOToken(delegator, userLogin);
	//System.out.println("token> "+token);
	context.put("token", token);
}
context.put("isEnableIUCInt", isEnableIUCInt);

String applicationType = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_TYPE");
context.put("applicationType", applicationType);
context.put("isEnableSupportLabel", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_SUPPORT_LABEL", "N"));
context.put("clientPortalUrl", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CLIENT_PORTAL_URL"));
context.put("enableHelpUrl", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_COMMON_HELP_URL", "N"));
context.put("helpUrl", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "COMMON_HELP_URL"));
context.put("hideNotification", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "HIDE_NOTIFICATION", "N"));
context.put("notifType", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "NOTIF_TYPE", "READ"));
context.put("isGridExpEnabled", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "grid.export.enabled", "N"));
context.put("actCalDftTyp", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_CAL_DFT_TYP", "APPOINTMENT"));
