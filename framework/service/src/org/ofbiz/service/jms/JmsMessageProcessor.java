package org.ofbiz.service.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.Get;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * 
 * @author Prabhakar
 *
 */

public class JmsMessageProcessor {
	static String MODULE = JmsMessageProcessor.class.getName();

	/*
	 *  Process Jms Message service 
	 */
	public static Map<String, Object> processMessage(DispatchContext dctx, Map<String, ? extends Object> context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String jmsMessageId = (String) context.get("jmsMessageId");
		Map result = ServiceUtil.returnSuccess();

		try{
			GenericValue jmsLogInfo = delegator.findOne("JmsLogInfo", UtilMisc.toMap("jmsMessageId",jmsMessageId),true);
			if(jmsLogInfo!=null){
				GenericValue jmsLogInfoClone = (GenericValue) jmsLogInfo.clone();
				String message = jmsLogInfoClone.getString("message");
				Debug.logInfo(" Message is " + message, MODULE);
				jmsLogInfoClone.setString("isProcessed", "Y");
				jmsLogInfoClone.store();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return result;
	}

	/*
	 * Process Pending JMS Messages
	 */
	public static Map<String, Object> processPenddingMessages(DispatchContext dctx, Map<String, ? extends Object> context) {

		Map result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();

		try{

			List conditionList = new ArrayList();
			conditionList.add(EntityCondition.makeCondition("isProcessed", EntityOperator.EQUALS, ""));
			conditionList.add(EntityCondition.makeCondition("isProcessed", EntityOperator.EQUALS, null));
			//conditionList.add(EntityCondition.makeCondition("",))
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.OR);

			List<GenericValue> pendingJmsLogInfo = delegator.findList("JmsLogInfo", condition, null, null, null, true);
			
			if(UtilValidate.isNotEmpty(pendingJmsLogInfo)){
				
				for(GenericValue pendingJmsLogInfoGv : pendingJmsLogInfo){
					
					String jmsMessageId = pendingJmsLogInfoGv.getString("jmsMessageId");
					String serviceName = pendingJmsLogInfoGv.getString("serviceName");
					
					Map input = UtilMisc.toMap("jmsMessageId",jmsMessageId);
					try{
						dispatcher.runSync(serviceName,input);
					}catch(Exception e){
						e.printStackTrace();
					}
					
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return result;
	}

}
