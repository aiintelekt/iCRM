/**
 * 
 */
package org.groupfio.etl.process.scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.processor.ModelNotificationProcessor;
import org.groupfio.etl.process.service.ServiceExecutor;
import org.groupfio.etl.process.util.CommonUtil;
import org.groupfio.etl.process.util.ResponseUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Group Fio
 *
 */
public class FileImportScheduler {

	private static String MODULE = FileImportScheduler.class.getName();
	
	public static Map<String, Object> importFileByGroup(DispatchContext dctx, Map<String, ? extends Object> context) {
		Debug.logInfo("file import group process start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
		try {
			
			List<GenericValue> modelGruops = delegator.findByAnd("EtlProcessGrouping", null, UtilMisc.toList("sequenceNo"), false);
			
			Map<String, List<GenericValue>> modelGroupList = new HashMap<String, List<GenericValue>>();
			
			if (UtilValidate.isNotEmpty(modelGruops)) {
				
				for (GenericValue mg : modelGruops) {
					String groupId = mg.getString("groupId");
					if (modelGroupList.get(groupId) != null) {
						modelGroupList.get(groupId).add(mg);
					} else {
						List<GenericValue> modelGrouping = new ArrayList<GenericValue>();
						modelGrouping.add(mg);
						modelGroupList.put(groupId, modelGrouping);
					}
					
				}
				
				for (String groupId : modelGroupList.keySet()) {
					
					List<GenericValue> modelGrouping = modelGroupList.get(groupId);
					
					for (GenericValue mg : modelGrouping) {
						
						String processId = mg.getString("processId");
						processModelGroup(delegator, dispatcher, userLogin, groupId, processId);
						
					}
					
				}
				
			}
			
		} catch (Exception e) {
			Debug.logError("file import group process Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		Debug.logInfo("file import group process end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	public static Map<String, Object> importFileByBatch(DispatchContext dctx, Map<String, ? extends Object> context) {
		Debug.logInfo("file import group process start...", MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
	
        String batchId = (String) context.get("batchId");
        String groupId = (String) context.get("groupId");
        String processId = (String) context.get("processId");
        
		try {
			
			processModelGroup(delegator, dispatcher, userLogin, groupId, processId);
			
		} catch (Exception e) {
			Debug.logError("file import group process Error: "+e.getMessage(), MODULE);
			return ServiceUtil.returnSuccess();
		}
		Debug.logInfo("file import group process end...", MODULE);
		return ServiceUtil.returnSuccess();
		
	}
	
	private static void processModelGroup (Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String groupId, String processId) throws Exception {
		
		GenericValue etlProcess = EntityQuery.use(delegator).from("EtlProcess").where("processId", processId).queryOne();
		if (UtilValidate.isNotEmpty(etlProcess)) {
			GenericValue etlModel = EntityQuery.use(delegator).from("EtlModel").where("modelName", etlProcess.getString("modalName")).queryOne();
			if (UtilValidate.isNotEmpty(etlModel)) {
				
				String modelFolderName = etlModel.getString("modelId").concat("-").concat(etlModel.getString("modelName")).concat("-").concat(groupId);
				Debug.logInfo("modelFolderName> "+modelFolderName, MODULE);
				
				String importLocation = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.import.location");
				
				File targetLocation = new File(importLocation + modelFolderName);
				if (!targetLocation.exists()) {
					targetLocation.mkdir();
				} else if (targetLocation.list().length > 0) {
					
					for (File importFile : targetLocation.listFiles()) {
						Debug.logInfo("start import file: "+importFile.getAbsolutePath(), MODULE);
						Map<String, Object> reqContext = new HashMap<String, Object>();
						
						reqContext.put("processId", processId);
						reqContext.put("modelName", etlModel.getString("modelName"));
						reqContext.put("filePath", importFile.getAbsolutePath());
						
						reqContext.put("fileName", importFile.getName());
						reqContext.put("targetLocation", targetLocation);
						
						reqContext.put("delegator", delegator);
						reqContext.put("dispatcher", dispatcher);
						reqContext.put("userLogin", userLogin);
						
						reqContext.put("isExecuteModelProcess", true);
						
						ServiceExecutor serviceExecutor = new ServiceExecutor();
						
						serviceExecutor.setDelegator(delegator);
						serviceExecutor.setServletRequest(null);
						serviceExecutor.setServletResponse(null);
						serviceExecutor.setReqContext(reqContext);
						
						Map<String, Object> res = serviceExecutor.execute();
						Debug.logInfo("start import file: "+importFile.getAbsolutePath()+", Result: "+res, MODULE);
						if (ResponseUtils.isError(res)) {
							
							Debug.logError(ResponseUtils.getResponseMessage(res), MODULE);
							
							// Notification process [start]
							Map<String, Object> processorContext = new HashMap<String, Object>();
							processorContext.put("delegator", delegator);
							processorContext.put("dispatcher", dispatcher);
							processorContext.put("userLogin", userLogin);
							processorContext.put("modelName", etlModel.getString("modelName"));
							processorContext.put("reason", EtlConstants.NOTIFICATION_REASON_FAILURE);
							processorContext.put("batchId", res.get("batchId"));
							processorContext.put("groupId", groupId);
							processorContext.put("processId", processId);
							
							ModelNotificationProcessor processor = new ModelNotificationProcessor();
							Map<String, Object> processRes = processor.process(processorContext);
							
							if (ResponseUtils.isSuccess(processRes)) {
								Debug.logInfo("Failure notification sent SUCCESS for Model#"+etlModel.getString("modelName"), MODULE);
							} else {
								Debug.logError("Failure notification sent FAILED for Model#"+etlModel.getString("modelName")+", ERROR: "+ResponseUtils.getResponseMessage(processRes), MODULE);
							}
							// Notification process [end]
							
						} else {
							Debug.logInfo("start dumping file... "+importFile.getAbsolutePath(), MODULE);
							// dump file
							String dumpLocation = UtilProperties.getPropertyValue("Etl-Process.properties", "etl.dump.location");
							File targetDumpLocation = new File(dumpLocation + modelFolderName);
							if (!targetDumpLocation.exists()) {
								targetDumpLocation.mkdir();
							}
							UtilMisc.copyFile(importFile, new File(targetDumpLocation.getAbsolutePath() + File.separator + importFile.getName()));
							importFile.delete();
							
							String extention = importFile.getName().substring(importFile.getName().indexOf('.'));
							if (extention.equals(".txt") || extention.equals(".dat")) {
								String excelFileName = CommonUtil.getAbsoulateFileName(importFile.getName())+".xls";
						        File excelFile = new File(targetLocation.getAbsolutePath() + File.separator + excelFileName);
						        UtilMisc.copyFile(excelFile, new File(targetDumpLocation.getAbsolutePath() + File.separator + excelFile.getName()));
						        excelFile.delete();
							}
							Debug.logInfo("end dumping file: "+targetDumpLocation.getAbsolutePath() + File.separator + importFile.getName(), MODULE);
							
							// Notification process [start]
							Map<String, Object> processorContext = new HashMap<String, Object>();
							processorContext.put("delegator", delegator);
							processorContext.put("dispatcher", dispatcher);
							processorContext.put("userLogin", userLogin);
							processorContext.put("modelName", etlModel.getString("modelName"));
							processorContext.put("reason", EtlConstants.NOTIFICATION_REASON_COMPLETE);
							processorContext.put("batchId", res.get("batchId"));
							processorContext.put("groupId", groupId);
							processorContext.put("processId", processId);
							
							ModelNotificationProcessor processor = new ModelNotificationProcessor();
							Map<String, Object> processRes = processor.process(processorContext);
							
							if (ResponseUtils.isSuccess(processRes)) {
								Debug.logInfo("Complete notification sent SUCCESS for Model#"+etlModel.getString("modelName"), MODULE);
							} else {
								Debug.logError("Complete notification sent FAILED for Model#"+etlModel.getString("modelName")+", ERROR: "+ResponseUtils.getResponseMessage(processRes), MODULE);
							}
							// Notification process [end]
							
						}
						
					}
					
				}
				
			}
		}
		
	}
	
}
