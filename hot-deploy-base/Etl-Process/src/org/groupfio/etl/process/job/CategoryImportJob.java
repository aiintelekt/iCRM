package org.groupfio.etl.process.job;

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

public class CategoryImportJob extends Thread {

	public static String etlTableName = UtilProperties.getPropertyValue("Etl-Process", "CATEGORY_TABLE");

	private static String MODULE = CategoryImportJob.class.getName();

	private LocalDispatcher dispatcher;
	private Delegator delegator;

	private int numTopProcess;
	private String etlModelId;
	private GenericValue userLogin;
	private String groupId;
	private boolean checkModelProcess;

	public void run() {
		try {
			Debug.logInfo("Start storing to Category staging [main]...................size: " + ", time:"
					+ UtilDateTime.nowAsString(), MODULE);
			int totalProceedCount = 0;
			
			GenericValue job = EntityUtil.getFirst(delegator.findByAnd("EtlPreProcessor",
					UtilMisc.toMap("statusId", "CREATED", "etlTableName", etlTableName), null, false));
			Debug.log("++++++++++++++++++++EtlSelfServiceImpl123+++++++++++++++++++" + job);

			if (UtilValidate.isNotEmpty(job)) {
				
				String batchId = job.getString("batchId");

				Map<String, Object> result = ServiceUtil.returnSuccess();
				List<GenericValue> findData = delegator.findByAnd("DataImportCategory",
						UtilMisc.toMap("batchId", batchId), null, false);
				
				boolean isExecuteModelProcess = false;
	        	
				if (UtilValidate.isNotEmpty(job.getString("isExecuteModelProcess"))
						&& job.getString("isExecuteModelProcess").equals("Y")) {
					isExecuteModelProcess = true;
				}
				
				if (UtilValidate.isNotEmpty(findData)) {
					for (GenericValue data : findData) {

						if (isExecuteModelProcess) {
							Validator validator = ValidatorFactory.getCategoryDataValidator();
		    				Map<String, Object> validatorContext = new HashMap<String, Object>();
		    				validatorContext.put("delegator", delegator);
		    				validatorContext.put("data", data.getAllFields());
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
							processorContext.put("rowValue", data.getAllFields());
							
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
							
							if (!filterRes) {
								continue;
							}
		    				
						}
						
						Map<String, Object> inputNew = new HashMap<String, Object>();
						inputNew.put("productCategoryId", data.getString("categoryId"));
						inputNew.put("productCategoryTypeId", data.getString("productCategoryTypeId"));
						inputNew.put("primaryParentCategoryId", data.getString("primaryParentCategoryId"));
						inputNew.put("categoryName", data.getString("categoryName"));
						inputNew.put("description", data.getString("description"));
						inputNew.put("longDescription", data.getString("longDescription"));
						inputNew.put("categoryImageUrl", data.getString("categoryImageUrl"));
						inputNew.put("linkOneImageUrl", data.getString("linkOneImageUrl"));
						inputNew.put("linkTwoImageUrl", data.getString("linkTwoImageUrl"));
						inputNew.put("detailScreen", data.getString("detailScreen"));
						inputNew.put("showInSelect", data.getString("showInSelect"));
						inputNew.put("userLogin", userLogin);
						/* inputNew.put("batchId",batchId); */

						GenericValue productCategory = EntityQuery.use(delegator).from("ProductCategory")
								.where("productCategoryId", data.getString("categoryId")).queryOne();
						if (UtilValidate.isEmpty(productCategory))
							result = dispatcher.runSync("createProductCategory", inputNew);
						else {
							result = dispatcher.runSync("updateProductCategory", inputNew);
						}
						Debug.log("++++++++++++++++++++EtlSelfServiceImpl+++++++++++++++++success++" + result);
						if (ServiceUtil.isSuccess(result)) {
							data.put("importStatusId", "DATAIMP_IMPORTED");
							data.store();
							Debug.log("++++++++++++++++++++EtlSelfServiceImpl+++++++++++++++++success++");

						} else {
							data.put("importStatusId", "DATAIMP_FAILED");
							data.store();
							Debug.log("++++++++++++++++++++EtlSelfServiceImpl+++++++++++++++++error++");
						}

					}
					GenericValue list = EntityUtil.getFirst(
							delegator.findByAnd("EtlModel", UtilMisc.toMap("modelName", etlModelId), null, false));
					EntityCondition condition = EntityCondition.makeCondition(
							UtilMisc.toList(
									EntityCondition.makeCondition("modelId", EntityOperator.EQUALS,
											list.getString("modelId")),
									EntityCondition.makeCondition("expModelId", EntityOperator.NOT_EQUAL, null)),
							EntityOperator.AND);
					// Export Model
					GenericValue etlModel = EntityUtil
							.getFirst(delegator.findList("EtlModel", condition, null, null, null, false));
					if (UtilValidate.isNotEmpty(etlModel)) {
						GenericValue etlModel1 = delegator.findOne("EtlModel",
								UtilMisc.toMap("modelId", etlModel.getString("expModelId")), false);
						if (UtilValidate.isNotEmpty(etlModel1)) {
							List<GenericValue> etlSourceTable = delegator.findByAnd("EtlSourceTable",
									UtilMisc.toMap("listName", etlModel1.getString("modelName")), null, false);
							if (UtilValidate.isNotEmpty(etlSourceTable)) {
								List<String> etlSourceTableList = EntityUtil.getFieldListFromEntityList(etlSourceTable,
										"tableColumnName", true);
								Set<String> selectFields = new HashSet<String>(etlSourceTableList);
								List<GenericValue> dataImportCategory = delegator.findList("DataImportCategory", null,
										selectFields, null, null, false);
								if (UtilValidate.isNotEmpty(dataImportCategory)) {
									for (GenericValue gv : dataImportCategory) {
										GenericValue dataExportCategory = delegator.makeValue("DataExportCategory",
												UtilMisc.toMap("modelId", list.getString("modelId")));
										dataExportCategory.set("sequenceId",
												delegator.getNextSeqId("DataExportAccount"));
										dataExportCategory.setNonPKFields(gv);
										TransactionUtil.begin();
										dataExportCategory.create();
										TransactionUtil.commit();
									}
								}
							}
						}
					}

					if (ServiceUtil.isSuccess(result)) {
						job.put("statusId", "FINISHED");
						EntityCondition codeCondition = EntityCondition.makeCondition(
								EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS,
										"DATAIMP_IMPORTED"),
								EntityOperator.AND,
								EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId));
						long processCount = delegator.findCountByCondition("DataImportCategory", codeCondition, null,
								null);
						job.put("processedCount", String.valueOf(processCount));
						job.store();
						Debug.log("++++++++++++++++++++EtlSelfServiceImpl+++++++++++++++++success++");
					} else {
						job.put("statusId", "FAILED");
						EntityCondition codeCondition = EntityCondition.makeCondition(
								EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS,
										"DATAIMP_IMPORTED"),
								EntityOperator.AND,
								EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId));
						long processCount = delegator.findCountByCondition("DataImportCategory", codeCondition, null,
								null);
						job.put("processedCount", String.valueOf(processCount));
						job.store();
					}

				}
			}
			Debug.logInfo("End storing to Category staging [main]...................endPoint: " + totalProceedCount
					+ ", time:" + UtilDateTime.nowAsString(), MODULE);
			
		} catch (Throwable t) {
			Debug.logError("CategoryImportJob Error: "+t.getMessage(), MODULE);
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
