package org.groupfio.etl.process.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.groupfio.etl.process.processor.ElementFilterProcessor;
import org.groupfio.etl.process.processor.ModelFilterProcessor;
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
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Group Fio
 *
 */
public class ProdSupplementaryDataImportJob extends Thread {

	public static String etlProductSupplementaryTableName = UtilProperties.getPropertyValue("Etl-Process", "PRODUCT_SUPPLEMENTARY_TABLE");
	private static String MODULE = ProdSupplementaryDataImportJob.class.getName();

	private LocalDispatcher dispatcher;
	private Delegator delegator;

	private int numTopProcess;
	private String etlModelId;
	private GenericValue userLogin;
	private String groupId;
	private boolean checkModelProcess;

	public void run() {
		try {
			Debug.logInfo("Start storing to product supplementary staging [main]...................size: " + ", time:"
					+ UtilDateTime.nowAsString(), MODULE);
			int totalProceedCount = 0;
			List<GenericValue> findJobs = EntityQuery.use(delegator).from("EtlPreProcessor").where("statusId", "CREATED", "etlTableName", etlProductSupplementaryTableName).queryList();

			Debug.log("++++++++++++++++++++ProdSupplementaryDataImportJob+++++++++++++++++++" + findJobs);

			if (UtilValidate.isNotEmpty(findJobs)) {
				for (GenericValue job : findJobs) {

					// String etlTable = job.getString("etlTableName");
					String batchId = job.getString("batchId");
					
					Map<String, Object> inputNew = new HashMap<String, Object>();
					inputNew.put("userLogin", userLogin);
					inputNew.put("initialResponsiblePartyId", "admin");
					inputNew.put("organizationPartyId", "Company");
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
					                EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_NOT_PROC"),
					                EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED"),
					                EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null));
					       
					        EntityCondition mainCond =  null;
					        mainCond = EntityCondition.makeCondition(EntityOperator.AND,
				                     EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId),
				                     statusCond );
						
							importDataList = delegator.findList(job.getString("etlTableName"), mainCond, null, null, null, false);
							
					        for (GenericValue importData : importDataList) {
				            		
			            		Validator validator = ValidatorFactory.getProductSupplementaryDataValidator();
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
					Debug.log("inputNew----->"+inputNew);
					Map<String, Object> result = dispatcher.runSync("importProductSupplementaryData", inputNew);
					Debug.log("++++++++++++++++++++importProductSupplementaryData+++++++++++++++++success++" + result);

					if (ServiceUtil.isSuccess(result)) {
						job.put("statusId", "FINISHED");
						EntityCondition codeCondition = EntityCondition.makeCondition(
								EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS,
										"DATAIMP_IMPORTED"),
								EntityOperator.AND,
								EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId));
						long processCount = delegator.findCountByCondition("DataImportProductSupplementary", codeCondition, null,
								null);
						job.put("processedCount", String.valueOf(processCount));
						job.store();
						Debug.log("++++++++++++++++++++EtlSelfServiceImpl+++++++++++++++++success++");

						if (UtilValidate.isNotEmpty(etlModelId)) {
							GenericValue list = EntityQuery.use(delegator).from("EtlModel").where("modelName", etlModelId).queryFirst();
							
							EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
									EntityCondition.makeCondition("modelId", EntityOperator.EQUALS,
											list.getString("modelId")),
									EntityCondition.makeCondition("expModelId", EntityOperator.NOT_EQUAL, null)),
									EntityOperator.AND);
							// Export Model
							GenericValue etlModel = EntityUtil
									.getFirst(delegator.findList("EtlModel", condition, null, null, null, false));
							if (UtilValidate.isNotEmpty(etlModel)) {
								GenericValue etlModel1 = EntityQuery.use(delegator).from("EtlModel").where("modelId", etlModel.getString("expModelId")).queryFirst();
								if (UtilValidate.isNotEmpty(etlModel1)) {
									
									List<GenericValue> etlSourceTable = EntityQuery.use(delegator).from("EtlSourceTable").where("listName", etlModel1.getString("modelName")).queryList();
									if (UtilValidate.isNotEmpty(etlSourceTable)) {
										List<String> etlSourceTableList = EntityUtil
												.getFieldListFromEntityList(etlSourceTable, "tableColumnName", true);
										Set<String> selectFields = new HashSet<String>(etlSourceTableList);
										List<GenericValue> dataImportProductSupplementary = delegator.findList("DataImportProductSupplementary",
												null, selectFields, null, null, false);
										if (UtilValidate.isNotEmpty(dataImportProductSupplementary)) {
											for (GenericValue gv : dataImportProductSupplementary) {
												GenericValue dataExportItm = delegator.makeValue(
														"DataExportItm",
														UtilMisc.toMap("modelId", list.getString("modelId")));
												dataExportItm.set("sequenceId",
														delegator.getNextSeqId("dataExportItm"));
												dataExportItm.setNonPKFields(gv);
												TransactionUtil.begin();
												dataExportItm.create();
												TransactionUtil.commit();
											}
										}
									}
								}
							}
						}
					}
				}
			}
			Debug.logInfo("End storing to PSD staging [main]...................endPoint: " + totalProceedCount
					+ ", time:" + UtilDateTime.nowAsString(), MODULE);
		} catch (Throwable t) {
			Debug.logError("ProdSupplementaryDataImportJob Error: "+t.getMessage(), MODULE);
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
