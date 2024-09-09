package org.groupfio.etl.process.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.etl.process.processor.ElementFilterProcessor;
import org.groupfio.etl.process.processor.ModelFilterProcessor;
import org.groupfio.etl.process.util.ResponseUtils;
import org.groupfio.etl.process.validator.Validator;
import org.groupfio.etl.process.validator.ValidatorFactory;
import org.groupfio.etl.process.writer.WriterUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Sharif
 *
 */
public class EmplPositionImportJob extends Thread {

	public static String etlTableName = "DataImportEmplPosition";

	private static String MODULE = EmplPositionImportJob.class.getName();

	private LocalDispatcher dispatcher;
	private Delegator delegator;

	private int numTopProcess;
	private String etlModelId;
	private GenericValue userLogin;
	private String groupId;
	private boolean checkModelProcess;

	public void run() {
		try {
			Debug.logInfo("Start storing to EmplPosition staging [main]...................size: " + ", time:"
					+ UtilDateTime.nowAsString(), MODULE);
			int totalProceedCount = 0;

			List<GenericValue> findJobs = delegator.findByAnd("EtlPreProcessor",
					UtilMisc.toMap("statusId", "CREATED", "etlTableName", etlTableName), null, false);
			Debug.log("++++++++++++++++++++EtlSelfServiceImpl123+++++++++++++++++++" + findJobs);

			if (UtilValidate.isNotEmpty(findJobs)) {
				for (GenericValue job : findJobs) {

					// String etlTable = job.getString("etlTableName");
					String batchId = job.getString("batchId");

					Map<String, Object> inputNew = new HashMap<String, Object>();
					inputNew.put("organizationPartyId", "Company");
					inputNew.put("userLogin", userLogin);
					inputNew.put("batchId", batchId);

					if (checkModelProcess) {

						// prepare import datas
						boolean isExecuteModelProcess = false;

						if (UtilValidate.isNotEmpty(job.getString("isExecuteModelProcess"))
								&& job.getString("isExecuteModelProcess").equals("Y")) {
							isExecuteModelProcess = true;
						}

						if (isExecuteModelProcess) {

							List<GenericValue> importDataList = new ArrayList<GenericValue>();
							List<GenericValue> importDatas = new ArrayList<GenericValue>();

							EntityCondition statusCond = EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS,
											"DATAIMP_NOT_PROC"),
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS,
											"DATAIMP_FAILED"),
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null));

							EntityCondition mainCond = null;
							mainCond = EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId),
									statusCond);

							importDataList = delegator.findList(job.getString("etlTableName"), mainCond, null, null,
									null, false);

							for (GenericValue importData : importDataList) {

								Validator validator = ValidatorFactory.getAccountDataValidator();
								Map<String, Object> validatorContext = new HashMap<String, Object>();
								validatorContext.put("delegator", delegator);
								validatorContext.put("data", importData.getAllFields());
								validatorContext.put("modelName", job.getString("modelName"));
								validatorContext.put("taskName", job.getString("taskName"));
								validatorContext.put("tableName", job.getString("etlTableName"));

								Map<String, Object> validatorResponse = validator.validate(validatorContext);
								if (ResponseUtils.isError(validatorResponse)) {
									
									Map<String, Object> validationMessage = (Map<String, Object>) validatorResponse.get("validationMessage");
			    					if (UtilValidate.isNotEmpty(validationMessage)) {
			    						for (String key : validationMessage.keySet()) {
			    							WriterUtil.writeLog(delegator, job.getString("taskName"), ""+validationMessage.get(key), job.getString("etlTableName"), job.getString("modelName"));
			    						}
			    					}
									
									continue;
								}
								
								// Apply filter [start]
                        		
                        		boolean filterRes = true;
                        		
                        		Map<String, Object> processorContext = new HashMap<String, Object>();
								processorContext.put("delegator", delegator);
								processorContext.put("dispatcher", dispatcher);
								processorContext.put("modelName", job.getString("modelName"));
								processorContext.put("rowValue", importData.getAllFields());
								
								ElementFilterProcessor elementFilterProcessor = new ElementFilterProcessor();
								Map<String, Object> processRes = elementFilterProcessor.process(processorContext);
								
								ModelFilterProcessor modelFilterProcessor = new ModelFilterProcessor();
								processRes = modelFilterProcessor.process(processorContext);
								
								if (ResponseUtils.isSuccess(processRes)) {
									if (UtilValidate.isNotEmpty(processRes.get("filterRes"))) {
										filterRes = (Boolean) processRes.get("filterRes");
									} else {
										filterRes = false;
									}
								}
                        		
                        		// Apply filter [end]
								
								if (filterRes) {
									importDatas.add(importData);
								}
								
							}

							if (UtilValidate.isEmpty(importDatas)) {
								// clean up job
					        	delegator.removeByAnd("EtlPreProcessor", UtilMisc.toMap("batchId", job.getString("batchId")));
								continue;
							}

							inputNew.put("importDatas", importDatas);

						}

					}

					Map<String, Object> result = dispatcher.runSync("importEmplPositions", inputNew);
					Debug.log("++++++++++++++++++++importEmplPositions+++++++++++++++++success++" + result);

					if (ServiceUtil.isSuccess(result)) {
						job.put("statusId", "FINISHED");
						
						int processCount = 0;
						if (UtilValidate.isNotEmpty(result.get("importedRecords"))) {
							processCount = Integer.parseInt(result.get("importedRecords").toString());
						}
						
						job.put("processedCount", String.valueOf(processCount));
						job.store();
						
						Debug.log("++++++++++++++++++++EtlSelfServiceImpl+++++++++++++++++success++");
						
					}
				}
			}
			Debug.logInfo("End storing to EmplPosition staging [main]...................endPoint: " + totalProceedCount
					+ ", time:" + UtilDateTime.nowAsString(), MODULE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logWarning("EmplPositionImportJob Throwable: "+e.getMessage(), MODULE);
	
		}
	}

	public LocalDispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(LocalDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public Delegator getDelegator() {
		return delegator;
	}

	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}

	public int getNumTopProcess() {
		return numTopProcess;
	}

	public void setNumTopProcess(int numTopProcess) {
		this.numTopProcess = numTopProcess;
	}

	public String getEtlModelId() {
		return etlModelId;
	}

	public void setEtlModelId(String etlModelId) {
		this.etlModelId = etlModelId;
	}

	public GenericValue getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(GenericValue userLogin) {
		this.userLogin = userLogin;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public boolean isCheckModelProcess() {
		return checkModelProcess;
	}

	public void setCheckModelProcess(boolean checkModelProcess) {
		this.checkModelProcess = checkModelProcess;
	}

}
