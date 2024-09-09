/**
 * 
 */
package org.groupfio.etl.process.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.ResponseCodes;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Group Fio
 *
 */
public class ModelNotificationProcessor extends AbstractProcessor {

	private static String MODULE = ModelNotificationProcessor.class.getName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.groupfio.etl.process.processor.AbstractProcessor#doProcess()
	 */
	@Override
	protected Map<String, Object> doProcess(Map<String, Object> context) throws Exception {

		Map<String, Object> response = new HashMap<String, Object>();

		Delegator delegator = (Delegator) context.get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String modelName = (String) context.get("modelName");
		String reason = (String) context.get("reason");
		
		String batchId = (String) context.get("batchId");
		String groupId = (String) context.get("groupId");
		String processId = (String) context.get("processId");
		
		String etlBaseUrl = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.base.url");
		
		try {

			List<GenericValue> modelNotifications = delegator.findByAnd("EtlModelNotification", UtilMisc.toMap("modelName", modelName, "reason", reason), null, false);
			
			for (GenericValue modelNotification : modelNotifications) {
				
				String content = "<strong>"+"Model Name: "+modelName+"<br/>"+"BatchId: "+batchId+"<br/>"+"GroupId: "+groupId+"<br/>"+"ProcessId: "+processId+"</strong><br/><br/>";
				
				if (UtilValidate.isNotEmpty(reason) && reason.equals(EtlConstants.NOTIFICATION_REASON_FAILURE)) {
					String reprocessUrl = "<br/><br/><a href='"+etlBaseUrl+"/control/etlBatches?batchId="+batchId+"&groupId="+groupId+"&processId="+processId+"'>Click here to Reprocess batch..</a><br/><br/>";
					content = content.concat( reprocessUrl );
				}
				
				content = content.concat( modelNotification.getString("content") );
				
				// prepare the email
	            Map<String, Object> sendMailParams = new HashMap<String, Object>();
	            sendMailParams.put("sendFrom", modelNotification.getString("fromString"));
	            sendMailParams.put("subject", modelNotification.getString("subject"));
	            sendMailParams.put("contentType", "text/html");
	            sendMailParams.put("userLogin", userLogin);
	            
	            sendMailParams.put("body", content);
	            sendMailParams.put("sendTo", modelNotification.getString("toString"));
	            sendMailParams.put("sendCc", modelNotification.getString("ccString"));
	            sendMailParams.put("sendBcc", modelNotification.getString("bccString"));
	            
	            Map<String, Object> tmpResult = dispatcher.runSync("sendMail", sendMailParams, 360, true);
	            
	            if (ServiceUtil.isError(tmpResult)) {
	            	Debug.logError("Model notification email send Failed!! "+modelName+", ERROR: "+ServiceUtil.getErrorMessage(tmpResult), MODULE);
	            }
	            
			}
			
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			Debug.log("Exception in doProcess==="+e.getMessage());
			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Model Notification process Failed...! modelName: " + modelName+", ERROR: "+e.getMessage());

			return response;
		}

		response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		return response;

	}
	
}
