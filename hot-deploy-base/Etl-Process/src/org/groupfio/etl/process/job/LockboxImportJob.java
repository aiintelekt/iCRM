package org.groupfio.etl.process.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.groupfio.etl.process.EtlConstants.LockboxStagingImportStatus;
import org.groupfio.etl.process.processor.ElementFilterProcessor;
import org.groupfio.etl.process.processor.ModelFilterProcessor;
import org.groupfio.etl.process.util.DataUtil;
import org.groupfio.etl.process.util.ResponseUtils;
import org.groupfio.etl.process.validator.Validator;
import org.groupfio.etl.process.validator.ValidatorFactory;
import org.groupfio.etl.process.writer.WriterUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Group Fio
 *
 */
public class LockboxImportJob extends Thread {

	public static String etlTableName = UtilProperties.getPropertyValue("Etl-Process", "LOCKBOX_BATCH_ITEM_TABLE");

	private static String MODULE = LockboxImportJob.class.getName();

	private LocalDispatcher dispatcher;
	private Delegator delegator;

	private int numTopProcess;
	private String etlModelId;
	private GenericValue userLogin;
	private String groupId;
	private boolean checkModelProcess;
	private String taskName;
	private String modelName;

	public void run() {
		try {
			Debug.logInfo("Start storing to Localbox staging [main]...................size: " + ", time:"
					+ UtilDateTime.nowAsString(), MODULE);
			int totalProceedCount = 0;
			List<GenericValue> findJobs = delegator.findByAnd("EtlPreProcessor",
					UtilMisc.toMap("statusId", "CREATED", "etlTableName", etlTableName), null, false);
			Debug.log("++++++++++++++++++++EtlSelfServiceImpl+++++++++++++++++++" + findJobs);

			if (UtilValidate.isNotEmpty(findJobs)) {
				for (GenericValue job : findJobs) {

					// String etlTable = job.getString("etlTableName");
					String batchId = job.getString("batchId");
					
					setTaskName(job.getString("taskName"));
					setModelName(job.getString("modelName"));

					Map<String, Object> inputNew = new HashMap<String, Object>();
					inputNew.put("userLogin", userLogin);
					inputNew.put("batchId", batchId);

					List<GenericValue> importDatas = new ArrayList<GenericValue>();
					
					if (checkModelProcess) {

						// prepare import datas
						boolean isExecuteModelProcess = false;

						if (UtilValidate.isNotEmpty(job.getString("isExecuteModelProcess"))
								&& job.getString("isExecuteModelProcess").equals("Y")) {
							isExecuteModelProcess = true;
						}

						if (isExecuteModelProcess) {

							List<GenericValue> importDataList = new ArrayList<GenericValue>();
							
							EntityCondition statusCond = EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS,
											"DATAIMP_NOT_PROC"),
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS,
											"DATAIMP_FAILED"),
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS,
											"LBIMP_ERROR"),
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS,
											"LBIMP_READY"),
									EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null));

							EntityCondition mainCond = null;
							mainCond = EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId),
									statusCond);

							importDataList = delegator.findList(job.getString("etlTableName"), mainCond, null, null,
									null, false);

							for (GenericValue importData : importDataList) {

								Validator validator = ValidatorFactory.getLockboxBatchItemDataValidator();
								Map<String, Object> validatorContext = new HashMap<String, Object>();
								validatorContext.put("delegator", delegator);
								validatorContext.put("data", importData.getAllFields());
								validatorContext.put("modelName", job.getString("modelName"));
								validatorContext.put("taskName", job.getString("taskName"));
								validatorContext.put("tableName", job.getString("etlTableName"));

								Map<String, Object> validatorResponse = validator.validate(validatorContext);
								
								Map<String, Object> data = (Map<String, Object>) validatorResponse.get("data");
								DataUtil.prepareLockboxBatchItemData(data);
								importData.putAll(data);
								delegator.store(importData);
								
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
					
					if (UtilValidate.isEmpty(importDatas)) {
						
						EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.OR,
								//EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_NOT_PROC"),
								//EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED"),
								EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, LockboxStagingImportStatus.LBBATCH_PROCESSED),
								EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, LockboxStagingImportStatus.LBBATCH_READY),
								
								EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null));

						EntityCondition statusCond = null;
						statusCond = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId), conditions);

						importDatas = delegator.findList("FioLockboxBatchItemStaging", statusCond, null, null, null, false);
						
					}
					
					if (UtilValidate.isNotEmpty(importDatas)) {
						
						String custBankAcNumber = null;
						String routingNumber = null;
						String invoiceNumber = null;
						String errorCodes = null;
						FastList<EntityCondition> entityConditions = null;
						Map<String, Object> batches = new HashMap<String, Object>();
						
						for (GenericValue entity : importDatas) {
							
							String batchNumber = entity.getString("batchNumber");
							
							batches.put(entity.getString("batchNumber"), entity.getString("batchNumber"));
							
							custBankAcNumber = entity.getString("customerBankAccountNumber");
			        		routingNumber = entity.getString("routingNumber");
			        		errorCodes = entity.getString("errorCodes");
			        		entityConditions = FastList.newInstance();
			        		
			        		entityConditions.add(EntityCondition.makeCondition("accountNumber",EntityOperator.EQUALS,custBankAcNumber));
		            		entityConditions.add(EntityCondition.makeCondition("routingNumber",EntityOperator.EQUALS,routingNumber));
		            		
		            		GenericValue eftAccount = EntityUtil.getFirst(delegator.findList("EftAccount", EntityCondition.makeCondition(entityConditions, EntityOperator.AND), UtilMisc.toSet("paymentMethodId"), null, null, false));
		            		if(UtilValidate.isNotEmpty(eftAccount)){
		            			
		            			GenericValue paymentMethod = EntityUtil.getFirst( delegator.findByAnd("PaymentMethod",UtilMisc.toMap("paymentMethodId", eftAccount.getString("paymentMethodId")), null, false) );
		            			if(UtilValidate.isNotEmpty(paymentMethod)){
		            				String customerId = paymentMethod.getString("partyId");
		            				entity.set("customerId",customerId);
		            				
		            				List<GenericValue> invoices = delegator.findByAnd("Invoice",UtilMisc.toMap("partyId", customerId), null, false);
		            				List<String> invoiceIds = EntityUtil.getFieldListFromEntityList(invoices, "invoiceId", true);
		            				
		            				invoiceNumber = entity.getString("invoiceNumber");
		            				if(!invoiceIds.contains(invoiceNumber)){
		            					if(errorCodes == null)
		                        			errorCodes = "LB-IN-CI";
		                        		else
		                        			errorCodes +=",LB-IN-CI";
		            					
		            					entity.set("errorCodes",errorCodes);
				            			entity.set("importStatusId", LockboxStagingImportStatus.LBBATCH_ERROR);
				            			delegator.store(entity);
		            					
		            					WriterUtil.writeLog(delegator, taskName, "BatchNumber: "+batchNumber+", ErrorCode: LB-IN-CI", etlTableName, modelName);
		            				}
		            				
		            			}
		            			
		            		}else{
		            			if(errorCodes == null)
		                			errorCodes = "LB-CI";
		                		else
		                			errorCodes +=",LB-CI";
		            			
		            			entity.set("errorCodes",errorCodes);
		            			entity.set("importStatusId", LockboxStagingImportStatus.LBBATCH_ERROR);
		            			delegator.store(entity);
		            			
		            			WriterUtil.writeLog(delegator, taskName, "BatchNumber: "+batchNumber+", ErrorCode: LB-CI", etlTableName, modelName);
		            			
		            		}
							
						}
						
						for (String batchNumber : batches.keySet()) {
							
							entityConditions = FastList.newInstance();
			        		entityConditions.add(EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, LockboxStagingImportStatus.LBBATCH_ERROR));
		            		entityConditions.add(EntityCondition.makeCondition("batchNumber", EntityOperator.EQUALS, batchNumber));
							
							long errorRecords = delegator.findCountByCondition("FioLockboxBatchItemStaging", EntityCondition.makeCondition(entityConditions, EntityOperator.AND), null, null);
							
							if(errorRecords > 0) {
				            	GenericValue batch = EntityUtil.getFirst( delegator.findByAnd("FioLockboxBatchStaging", UtilMisc.toMap("batchNumber", batchNumber), null, false) );
				            	batch.set("importStatusId", LockboxStagingImportStatus.LBBATCH_ERROR);
				            	delegator.store(batch);
				            } else {
				            	GenericValue batch = EntityUtil.getFirst( delegator.findByAnd("FioLockboxBatchStaging", UtilMisc.toMap("batchNumber", batchNumber), null, false) );
				            	batch.set("importStatusId", LockboxStagingImportStatus.LBBATCH_READY);
				            	delegator.store(batch);
				            	
				            	Map<String, Object> reqContext = FastMap.newInstance();
				            	
				            	reqContext.put("userLogin", userLogin);
				            	
				            	reqContext.put("lockboxBatchId", batchNumber);
				            	reqContext.put("taskName", taskName);
				            	reqContext.put("etlTableName", etlTableName);
				            	reqContext.put("modelName", modelName);
				            	
				            	Map<String, Object> result = dispatcher.runSync("lockbox.importLockboxBatchFromStaging", reqContext);
								
								if (ServiceUtil.isError(result)) {
									Debug.logError("Error import LockboxBatch from Staging..", MODULE);
								}
				            	
				            }
							
						}
						
					}
					
				}
			}
			Debug.logInfo("End storing Lockbox Batch [main]...................endPoint: " + totalProceedCount
					+ ", time:" + UtilDateTime.nowAsString(), MODULE);

		} catch (Throwable t) {
			Debug.logError("LockboxImportJob Error: "+t.getMessage(), MODULE);
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

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
}