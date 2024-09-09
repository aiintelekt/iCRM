package org.fio.admin.portal.service;

import java.text.ParseException;
import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
public class SendGridConfigServices {
	
	public static String MODULE = SendGridConfigServices.class.getName();
	private SendGridConfigServices() {
	}
	/**
	 * 
	 * @author sharmila
	 * @param dispatcherContext
	 * @param Map
	 * @return success
	 * @throws ParseException
	 * @throws GenericEntityException
	 * 
	 */
	public static Map createOrUpdateSendGridConfig(DispatchContext dctx, Map context)
			throws ParseException, GenericEntityException {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map requestContext = (Map) context.get("requestContext");
		Map requestContextValue = ServiceUtil.returnSuccess("Send Grid Updated Successfully");
		String configName = (String) requestContext.get("configName");
		String configId = (String) requestContext.get("configId");
		String apiKey = (String) requestContext.get("apiKey");
		String senderMail = (String) requestContext.get("senderMail");
		String senderName = (String) requestContext.get("senderName");
		String isDefault = (String) requestContext.get("isDefault");
		String skipBlacklistCheck = (String) requestContext.get("skipBlacklistCheck");
		String check = (String) requestContext.get("check");
		GenericValue sendGridConfig = delegator.makeValue("SendGridConfig");
		try {
			try {
			if(UtilValidate.isNotEmpty(isDefault) && isDefault.equals("Y")) {
				String sqlUpdateQuery = "UPDATE send_grid_config SET is_default = 'N';";
				SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
				sqlProcessor.prepareStatement(sqlUpdateQuery);
				sqlProcessor.executeUpdate();
			}
			}catch(Exception e) {
				requestContextValue = ServiceUtil.returnError("error occur in update process check input field");
				Debug.logError(e.getMessage(), MODULE);
			}
			if(UtilValidate.isEmpty(configId)) {
				configId=delegator.getNextSeqId("SendGridConfig");
			}
			sendGridConfig.put("configId",UtilValidate.isNotEmpty(configId) ? configId : "");
			sendGridConfig.put("configName",UtilValidate.isNotEmpty(configName) ? configName : "");
			sendGridConfig.put("apiKey",UtilValidate.isNotEmpty(apiKey) ? apiKey : "");
			sendGridConfig.put("fromEmailId",UtilValidate.isNotEmpty(senderMail) ? senderMail : "");
			sendGridConfig.put("fromEmailName",UtilValidate.isNotEmpty(senderName) ? senderName : "");
			sendGridConfig.put("skipBlacklistCheck",UtilValidate.isNotEmpty(skipBlacklistCheck) ? skipBlacklistCheck : "");
			sendGridConfig.put("isDefault",UtilValidate.isNotEmpty(isDefault) ? isDefault : "");
			if(UtilValidate.isNotEmpty(check)) {
				sendGridConfig.put("lastUpdatedTxStamp",UtilDateTime.nowTimestamp());
			}else {
				sendGridConfig.put("createdTxStamp",UtilDateTime.nowTimestamp());
				sendGridConfig.put("lastUpdatedTxStamp",UtilDateTime.nowTimestamp());
			}
			delegator.createOrStore(sendGridConfig);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			requestContextValue = ServiceUtil.returnError("error occur in update process check input field");
		}
		return requestContextValue;
	}
}
