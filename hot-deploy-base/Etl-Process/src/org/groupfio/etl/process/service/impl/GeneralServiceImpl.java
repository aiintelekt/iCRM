package org.groupfio.etl.process.service.impl;

import java.util.Map;

import org.groupfio.etl.process.service.GeneralService;
import org.groupfio.etl.process.writer.WriterUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Sharif
 *
 */
public class GeneralServiceImpl implements GeneralService {
	
	private static String MODULE = GeneralServiceImpl.class.getName();
	
	public static Map<String, Object> createEtlPreProcessor(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		try {
			
			Delegator delegator = dctx.getDelegator();
			GenericValue userLogin =  (GenericValue) context.get("userLogin");
			
			String batchId = (String) context.get("batchId");
			String modelId = (String) context.get("modelId");
			String accessType = (String) context.get("accessType");
			String taskName = (String) context.get("taskName");
			String etlTableName = (String) context.get("etlTableName");
			Boolean isExecuteModelProcess = (Boolean) context.get("isExecuteModelProcess");
			
			String userLoginId = "";
			if(UtilValidate.isNotEmpty(userLogin))
			 userLoginId = userLogin.getString("userLoginId");
			
			GenericValue makeProcessor = delegator.makeValue("EtlPreProcessor");
			
			makeProcessor.put("batchId", batchId);
			makeProcessor.put("modelName", modelId);
			if(UtilValidate.isNotEmpty(modelId)){
				GenericValue getModel = EntityUtil.getFirst(delegator.findByAnd("EtlModel",UtilMisc.toMap("modelName",modelId),null,false));
				makeProcessor.put("modelId", getModel.getString("modelId"));
			}
			
			makeProcessor.put("accessType", accessType);
			makeProcessor.put("taskName", taskName);
			makeProcessor.put("etlTableName", etlTableName);
			makeProcessor.put("statusId", "CREATED");
			makeProcessor.put("createdBy", userLoginId);
			makeProcessor.put("isExecuteModelProcess", isExecuteModelProcess ? "Y" : "N");
			
			makeProcessor.create();
			TransactionUtil.commit();
		} catch (Exception e) {
			Debug.logError("createEtlPreProcessor ERROR: "+e.getMessage(), MODULE);
			return ServiceUtil.returnError(e.toString());
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> writeEtlErrorLog(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		try {
			
			Delegator delegator = dctx.getDelegator();
			GenericValue userLogin =  (GenericValue) context.get("userLogin");
			
			String logMessage = (String) context.get("logMessage");
			String taskName = (String) context.get("taskName");
			String etlTableName = (String) context.get("etlTableName");
			String modelName = (String) context.get("modelName");
			
			WriterUtil.writeLog(delegator, taskName, logMessage, etlTableName, modelName);
			
		} catch (Exception e) {
			Debug.logError("createEtlPreProcessor ERROR: "+e.getMessage(), MODULE);
			return ServiceUtil.returnError(e.toString());
		}
		
		return ServiceUtil.returnSuccess();
	}
	
}