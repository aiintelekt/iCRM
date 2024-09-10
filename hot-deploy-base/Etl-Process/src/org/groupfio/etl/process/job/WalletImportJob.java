package org.groupfio.etl.process.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.groupfio.etl.process.client.ClientUtil;
import org.groupfio.etl.process.client.WalletClient;
import org.groupfio.etl.process.client.WalletPartyClient;
import org.groupfio.etl.process.client.WalletWalletClient;
import org.groupfio.etl.process.client.response.Party;
import org.groupfio.etl.process.client.response.WalletAccount;
import org.groupfio.etl.process.processor.ElementFilterProcessor;
import org.groupfio.etl.process.processor.ModelFilterProcessor;
import org.groupfio.etl.process.util.ResponseUtils;
import org.groupfio.etl.process.util.WalletUtil;
import org.groupfio.etl.process.validator.Validator;
import org.groupfio.etl.process.validator.ValidatorFactory;
import org.groupfio.etl.process.writer.WriterUtil;
import org.json.simple.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;

/**
 * @author Group Fio
 *
 */
public class WalletImportJob extends Thread {

	public static String etlTableName = UtilProperties.getPropertyValue("Etl-Process", "WALLET_TABLE");

	private static String MODULE = WalletImportJob.class.getName();

	private LocalDispatcher dispatcher;
	private Delegator delegator;

	private int numTopProcess;
	private String etlModelId;
	private GenericValue userLogin;
	private String groupId;
	private boolean checkModelProcess;
	private String batchId;

	public void run() {

		Debug.logInfo("Start storing to Wallet staging [main]...................size: " + ", time:"
				+ UtilDateTime.nowAsString(), MODULE);
		int totalProceedCount = 0;
		try {
			Debug.log("++++++++++++++++++++batchId in WalletImportJob.java+++++++++++++++++++"+ batchId);
			List<GenericValue> findJobs = delegator.findByAnd("EtlPreProcessor",
					UtilMisc.toMap("statusId", "CREATED", "etlTableName", etlTableName, "batchId", batchId), null, false);

			Debug.log("++++++++++++++++++++WalletImportJob+++++++++++++++++++" + findJobs);
			if (UtilValidate.isNotEmpty(findJobs)) {
				for (GenericValue job : findJobs) {

					// String etlTable = job.getString("etlTableName");
					//String batchId = job.getString("batchId");
					
					Map<String, Object> inputNew = new HashMap<String, Object>();
					inputNew.put("userLogin", userLogin);
					inputNew.put("initialResponsiblePartyId", "admin");
					inputNew.put("organizationPartyId", "Company");
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
					                EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_NOT_PROC"),
					                EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED"),
					                EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null));
					       
					        EntityCondition mainCond =  null;
					        mainCond = EntityCondition.makeCondition(EntityOperator.AND,
				                     EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId),
				                     statusCond );
					        Debug.log("============mainCond log#10======="+mainCond);
					        Debug.log("============job.getString(\"etlTableName\") log#11======="+job.getString("etlTableName"));
							importDataList = delegator.findList(job.getString("etlTableName"), mainCond, null, null, null, false);
							Debug.log("============importDataList log#12======="+importDataList);
					        for (GenericValue importData : importDataList) {
				            		
			            		Validator validator = ValidatorFactory.getWalletDataValidator();
			    				Map<String, Object> validatorContext = new HashMap<String, Object>();
			    				validatorContext.put("delegator", delegator);
			    				validatorContext.put("data", importData.getAllFields());
			    				validatorContext.put("modelName", job.getString("modelName"));
			    				validatorContext.put("taskName", job.getString("taskName"));
			    				validatorContext.put("tableName", job.getString("etlTableName"));
			    				
			    				Map<String, Object> validatorResponse = validator.validate(validatorContext);
			    				Debug.log("============validatorResponse log#13======="+validatorResponse);
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
								Debug.log("============processRes log#14======="+processRes);
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
					        Debug.log("============importDatas log#15======="+importDatas);
					        if (UtilValidate.isEmpty(importDatas)) {
					        	// clean up job
					        	delegator.removeByAnd("EtlPreProcessor", UtilMisc.toMap("batchId", job.getString("batchId")));
					        	continue;
					        }
					        
					        inputNew.put("importDatas", importDatas);
					        
						}
				        
					}
					Debug.log("============importDatas log#16======="+importDatas);
					if (UtilValidate.isEmpty(importDatas)) {
						
						EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_NOT_PROC"),
				                EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED"),
				                EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null));

						EntityCondition statusCond = null;
						statusCond = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId), conditions);

						importDatas = delegator.findList(job.getString("etlTableName"), statusCond, null, null, null, false);
						
					}
					Debug.log("============importDatas log#17======="+importDatas);
					if (UtilValidate.isNotEmpty(importDatas)) {
						
						GenericValue channelAccess = WalletClient.getWalletChannelAccess(delegator);
						WalletPartyClient partyClient = ClientUtil.getWalletPartyClient(delegator, channelAccess);
						WalletWalletClient walletClient = ClientUtil.getWalletWalletClient(delegator, channelAccess);
						
						for (GenericValue entity : importDatas) {
							
							boolean isImported = false;
							
							if (UtilValidate.isEmpty(entity.getString("operatingWalletNumber"))) {
								Debug.log("============only master wallet requested log#18=======");
								String masterWalletNumber = entity.getString("masterWalletNumber");
								
								if (entity.getString("action").equals("D")) {
									
									// expire master wallet
									
									JSONObject walletRequest = new JSONObject();
									
									walletRequest.put("walletAcctId", masterWalletNumber);
									
									Map<String, Object> walletRes = walletClient.expireWalletAccount(walletRequest.toJSONString());
									if (ResponseUtils.isSuccess(walletRes)) {
										WalletAccount walletAccount = (WalletAccount) walletRes.get("walletAccount");
										if (UtilValidate.isNotEmpty(walletAccount) && walletAccount.getResponseCode().equals("S200")) {
											Debug.logInfo("Successfully expired master wallet #"+masterWalletNumber, MODULE);
											isImported = true;
										}
									}
									
								} else {
								
									// Master wallet import
									
									String masterWalletName = "NA";
									
									JSONObject partyRequest = new JSONObject();
									
									partyRequest.put("accountType", "MASTER_ACCT_OWNER");
									//partyRequest.put("masterPartyId", masterwalletAccountRole.getString("partyId"));
									partyRequest.put("partyName", masterWalletName);
									partyRequest.put("baseCurrency", "SGD");
									partyRequest.put("requestDatetime", UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), "MM/dd/yyyy HH:mm:ss", TimeZone.getDefault(), null));
									Map<String, Object> partyRes = partyClient.createParty(partyRequest.toJSONString());
									Debug.log("============partyRes log#19========="+partyRes);
									if (ResponseUtils.isSuccess(partyRes)) {
										
										Party party = (Party) partyRes.get("party");
										if (UtilValidate.isNotEmpty(party) && party.getResponseCode().equals("S200")) {
											
											JSONObject walletRequest = new JSONObject();
											String currentDateString = UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), "MM/dd/yyyy HH:mm:ss", TimeZone.getDefault(), null);
											
											walletRequest.put("walletAcctId", masterWalletNumber);
											walletRequest.put("partyId", party.getPartyId());
											walletRequest.put("walletName", masterWalletName);
											walletRequest.put("walletCurrency", "SGD");
											walletRequest.put("description", "imported data");
											walletRequest.put("maxLimitAllowed", "0");
											walletRequest.put("fromDate", currentDateString);
											
											Map<String, Object> walletRes = walletClient.createWalletAccount(walletRequest.toJSONString());
											Debug.log("============walletRes log#20========="+walletRes);
											if (ResponseUtils.isSuccess(walletRes)) {
												WalletAccount walletAccount = (WalletAccount) walletRes.get("walletAccount");
												if (UtilValidate.isNotEmpty(walletAccount) && walletAccount.getResponseCode().equals("S200")) {
													Debug.logInfo("Successfully imported master wallet #"+masterWalletNumber, MODULE);
													isImported = true;
												}
											}
										}
										
									}
								}
								
							} else {
								
								String operatingWalletNumber = entity.getString("operatingWalletNumber");
								
								if (entity.getString("action").equals("D")) {
									
									// expire master wallet
									
									JSONObject walletRequest = new JSONObject();
									
									walletRequest.put("walletAcctId", operatingWalletNumber);
									
									Map<String, Object> walletRes = walletClient.expireWalletAccount(walletRequest.toJSONString());
									if (ResponseUtils.isSuccess(walletRes)) {
										WalletAccount walletAccount = (WalletAccount) walletRes.get("walletAccount");
										if (UtilValidate.isNotEmpty(walletAccount) && walletAccount.getResponseCode().equals("S200")) {
											Debug.logInfo("Successfully expired operating wallet #"+operatingWalletNumber, MODULE);
											isImported = true;
										}
									}
									
								} else {
                                    // Operating wallet import
                                    GenericValue operatorWalletAccount = WalletUtil.getActiveWalletAccount(delegator, operatingWalletNumber);
                                    String masterWalletNumber = entity.getString("masterWalletNumber");
                                    GenericValue masterWalletAccount = WalletUtil.getActiveWalletAccount(delegator, masterWalletNumber);
                                    Debug.log("=========operatorWalletAccount log#21==========="+operatorWalletAccount);
                                    Debug.log("=========masterWalletAccount log#22==========="+masterWalletAccount);
                                    /*if (UtilValidate.isEmpty(operatorWalletAccount)) {}*/ // commented for operating wallet update mode.
                                    

                                    // If master wallet account is empty, first import it
                                    Boolean isNewMasterWalletImported = false;
                                    if (UtilValidate.isEmpty(masterWalletAccount)) {
                                        String masterWalletName = "NA";
                                        JSONObject partyRequest = new JSONObject();
                                        partyRequest.put("accountType", "MASTER_ACCT_OWNER");
                                        partyRequest.put("partyName", masterWalletName);
                                        partyRequest.put("baseCurrency", "SGD");
                                        partyRequest.put("requestDatetime", UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), "MM/dd/yyyy HH:mm:ss", TimeZone.getDefault(), null));
                                        Map<String, Object> partyRes = partyClient.createParty(partyRequest.toJSONString());
                                        Debug.log("=========partyRes log#23==========="+partyRes);
                                        if (ResponseUtils.isSuccess(partyRes)) {
                                            Party party = (Party) partyRes.get("party");
                                            if (UtilValidate.isNotEmpty(party) && party.getResponseCode().equals("S200")) {
                                                JSONObject walletRequest = new JSONObject();
                                                String currentDateString = UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), "MM/dd/yyyy HH:mm:ss", TimeZone.getDefault(), null);
                                                walletRequest.put("walletAcctId", masterWalletNumber);
                                                walletRequest.put("partyId", party.getPartyId());
                                                walletRequest.put("walletName", masterWalletName);
                                                walletRequest.put("walletCurrency", "SGD");
                                                walletRequest.put("description", "imported data");
                                                walletRequest.put("maxLimitAllowed", "0");
                                                walletRequest.put("fromDate", currentDateString);
                                                Map<String, Object> walletRes = walletClient.createWalletAccount(walletRequest.toJSONString());
                                                Debug.log("=========walletRes log#24==========="+walletRes);
                                                if (ResponseUtils.isSuccess(walletRes)) {
                                                    WalletAccount walletAccount = (WalletAccount) walletRes.get("walletAccount");
                                                    if (UtilValidate.isNotEmpty(walletAccount) && walletAccount.getResponseCode().equals("S200")) {
                                                        Debug.logInfo("Successfully imported master wallet #"+masterWalletNumber, MODULE);
                                                        isNewMasterWalletImported = true;
                                                    }
                                                }
                                             }
                                         }
                                     }
                                     masterWalletAccount = WalletUtil.getActiveWalletAccount(delegator, masterWalletNumber);
                                     Debug.log("=========masterWalletAccount log#25==========="+masterWalletAccount);
                                     // If master wallet account is imported successfully, import operational wallet
                                     if (UtilValidate.isNotEmpty(masterWalletAccount)) {
                                         GenericValue masterwalletAccountRole = WalletUtil.getActiveWalletAccountRole(delegator, masterWalletAccount.getString("billingAccountId"));
                                         Debug.log("=========masterwalletAccountRole log#26==========="+masterwalletAccountRole);
                                         JSONObject partyRequest = new JSONObject();
                                         partyRequest.put("accountType", "OPERATING_ACCT_OWNER");
                                         partyRequest.put("masterPartyId", masterwalletAccountRole.getString("partyId"));
                                         partyRequest.put("partyName", entity.getString("operatingWalletName"));
                                         partyRequest.put("baseCurrency", masterWalletAccount.getString("accountCurrencyUomId"));
                                         partyRequest.put("requestDatetime", UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), "MM/dd/yyyy HH:mm:ss", TimeZone.getDefault(), null));
                                         Map<String, Object> partyRes = partyClient.createParty(partyRequest.toJSONString());
                                         Debug.log("=========partyRes log#27==========="+partyRes);
                                         if (ResponseUtils.isSuccess(partyRes)) {
                                             Party party = (Party) partyRes.get("party");
                                             if (UtilValidate.isNotEmpty(party) && party.getResponseCode().equals("S200")) {
                                                 JSONObject walletRequest = new JSONObject();
                                                 String currentDateString = UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), "MM/dd/yyyy HH:mm:ss", TimeZone.getDefault(), null);
                                                 walletRequest.put("walletAcctId", operatingWalletNumber);
                                                 walletRequest.put("partyId", party.getPartyId());
                                                 walletRequest.put("walletName", entity.getString("operatingWalletName"));
                                                 walletRequest.put("walletCurrency", masterWalletAccount.getString("accountCurrencyUomId"));
                                                 walletRequest.put("description", "imported data");
                                                 walletRequest.put("maxLimitAllowed", "0");
                                                 walletRequest.put("fromDate", currentDateString);
                                                 Map<String, Object> walletRes = walletClient.createWalletAccount(walletRequest.toJSONString());
                                                 Debug.log("=========walletRes log#28==========="+walletRes);
                                                 if (ResponseUtils.isSuccess(walletRes)) {
                                                     WalletAccount walletAccount = (WalletAccount) walletRes.get("walletAccount");
                                                     if (UtilValidate.isNotEmpty(walletAccount) && walletAccount.getResponseCode().equals("S200")) {
                                                         Debug.logInfo("Successfully imported operating wallet #"+operatingWalletNumber, MODULE);
                                                         isImported = true;
                                                     }
                                                 } else if (isNewMasterWalletImported) {
                                                     //Need to expire the new master wallet also, as problem occurred while creating operating wallet
                                                     walletRequest = new JSONObject();
                                                     walletRequest.put("walletAcctId", masterWalletNumber);
                                                     walletRes = walletClient.expireWalletAccount(walletRequest.toJSONString());
                                                     if (ResponseUtils.isSuccess(walletRes)) {
                                                         WalletAccount walletAccount = (WalletAccount) walletRes.get("walletAccount");
                                                         if (UtilValidate.isNotEmpty(walletAccount) && walletAccount.getResponseCode().equals("S200")) {
                                                             Debug.logInfo("Successfully expired newly created master wallet as operating wallet creation got failed#"+masterWalletNumber, MODULE);
                                                         }
                                                     }
                                                 }
                                             }
                                         }
                                     }
                                 
                                     
                                     
                                }
                            }
							Debug.log("============isImported log#29======="+isImported);
							if (isImported) {
								entity.set("importStatusId", "DATAIMP_IMPORTED");
							} else {
								entity.set("importStatusId", "DATAIMP_FAILED");
							}
							
							entity.set("processedTimestamp", UtilDateTime.nowTimestamp());
	            			delegator.store(entity);
						}
						
						//job.put("statusId", "FINISHED");
						EntityCondition codeCondition = EntityCondition.makeCondition(
								EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS,
										"DATAIMP_IMPORTED"),
								EntityOperator.AND,
								EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId));
						long processCount = delegator.findCountByCondition(job.getString("etlTableName"), codeCondition, null,
								null);
						job.put("processedCount", String.valueOf(processCount));
						
						job.put("statusId", "FINISHED");
						
						job.store();
						
					}
					
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
			Debug.logError("WalletImportJob Error: "+e.getMessage(), MODULE);
		}

		Debug.logInfo("End storing to Wallet staging [main]...................endPoint: " + totalProceedCount
				+ ", time:" + UtilDateTime.nowAsString(), MODULE);

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

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}