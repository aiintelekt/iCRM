import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("AdminPortalUiLabels", locale);
inputContext = new HashMap();
channelAccessId = request.getParameter("channelAccessId");
if(UtilValidate.isNotEmpty(channelAccessId)) {
	inputContext.put("channelAccessId",channelAccessId);
	channelAccess =  EntityQuery.use(delegator).from("ChannelAccess").where("channelAccessId",channelAccessId).queryOne();
	if(UtilValidate.isNotEmpty(channelAccess)) {
		inputContext.put("channelAccessType",UtilValidate.isNotEmpty(channelAccess.getString("channelAccessType")) ? channelAccess.getString("channelAccessType") :"");
		inputContext.put("channelAccessUrl",UtilValidate.isNotEmpty(channelAccess.getString("channelAccessUrl")) ? channelAccess.getString("channelAccessUrl") :"");
		inputContext.put("apiKey",UtilValidate.isNotEmpty(channelAccess.getString("apiKey")) ? channelAccess.getString("apiKey") :"");
		inputContext.put("userName",UtilValidate.isNotEmpty(channelAccess.getString("userName")) ? channelAccess.getString("userName") :"");
		inputContext.put("password",UtilValidate.isNotEmpty(channelAccess.getString("password")) ? channelAccess.getString("password") :"");
		inputContext.put("applicationName",UtilValidate.isNotEmpty(channelAccess.getString("applicationName")) ? channelAccess.getString("applicationName") :"");
		inputContext.put("fromDate",UtilValidate.isNotEmpty(channelAccess.getTimestamp("fromDate")) ? channelAccess.getTimestamp("fromDate") :"");
		inputContext.put("thruDate",UtilValidate.isNotEmpty(channelAccess.getTimestamp("thruDate")) ? channelAccess.getTimestamp("thruDate") :"");
		inputContext.put("description",UtilValidate.isNotEmpty(channelAccess.getString("description")) ? channelAccess.getString("description") :"");
		inputContext.put("merchantName",UtilValidate.isNotEmpty(channelAccess.getString("merchantName")) ? channelAccess.getString("merchantName") :"");
		inputContext.put("authAccessUrl",UtilValidate.isNotEmpty(channelAccess.getString("authAccessUrl")) ? channelAccess.getString("authAccessUrl") :"");
	}
}
context.put("inputContext", inputContext);