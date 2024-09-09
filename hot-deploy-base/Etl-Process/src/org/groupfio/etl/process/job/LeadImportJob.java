package org.groupfio.etl.process.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Sharif
 *
 */
public class LeadImportJob extends Thread {

	public static String etlTableName = UtilProperties.getPropertyValue("Etl-Process", "LEAD_TABLE");

	private static String MODULE = LeadImportJob.class.getName();

	private LocalDispatcher dispatcher;
	private Delegator delegator;

	private int numTopProcess;
	private String etlModelId;
	private GenericValue userLogin;
	private String groupId;
	private boolean checkModelProcess;

	public void run() {
		try {
			Debug.logInfo("Start storing to Lead staging [main]...................size: " + ", time:"
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

								Validator validator = ValidatorFactory.getLeadDataValidator();
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

					Map<String, Object> result = dispatcher.runSync("importLeads", inputNew);
					Debug.log("++++++++++++++++++++importLeads+++++++++++++++++success++" + result);

					if (ServiceUtil.isSuccess(result)) {
						job.put("statusId", "FINISHED");
						
						int processCount = 0;
						if (UtilValidate.isNotEmpty(result.get("importedRecords"))) {
							processCount = Integer.parseInt(result.get("importedRecords").toString());
						}
						
						/*EntityCondition codeCondition = EntityCondition.makeCondition(
								EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS,
										"DATAIMP_IMPORTED"),
								EntityOperator.AND,
								EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId));
						long processCount = delegator.findCountByCondition("DataImportLead", codeCondition, null,
								null);*/
						
						job.put("processedCount", String.valueOf(processCount));
						job.store();
						
						List<GenericValue> importedDataList = (List<GenericValue>) result.get("importedDataList");
						if (UtilValidate.isNotEmpty(importedDataList)) {
							
							for (GenericValue importedData : importedDataList) {
								
								//Map<String, Object> virtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, importedData.getString("virtualTeamId"), userLogin.getString("partyId"));
								
								String leadId = importedData.getString("primaryPartyId");
								if (UtilValidate.isEmpty(leadId)) {
									continue;
								}
								
								String primaryPhoneNumber = importedData.getString("primaryPhoneNumber");
								String secondaryPhoneNumber = importedData.getString("secondaryPhoneNumber");

								GenericValue partySupplementalData = EntityUtil.getFirst( delegator.findByAnd("PartySupplementalData", UtilMisc.toMap("partyId", leadId), null, false) );
								
								// virtual team [start]
								
								/*if (UtilValidate.isNotEmpty(virtualTeam.get("virtualTeamId"))) {
									partySupplementalData.put("virtualTeamId", virtualTeam.get("virtualTeamId"));
								}*/
								
								// virtual team [end]
								
								// assign responsible for [start]
								
								//String responsibleForId = null;
								boolean alreadyAssigned = false;
								
								String countryGeoId = "IND";
								if (UtilValidate.isNotEmpty( userLogin.getString("countryGeoId") )) {
									countryGeoId = userLogin.getString("countryGeoId");
								}
								
								/*if (UtilValidate.isNotEmpty(importedData.getString("leadAssignTo")) && DataHelper.isResponsibleForParty(delegator, importedData.getString("leadAssignTo"))) {
									responsibleForId = importedData.getString("leadAssignTo");
								} else {
									
									String assignedResponsiblePartyId = DataHelper.getResponsibleForParty(delegator, leadId);
									if (UtilValidate.isNotEmpty(assignedResponsiblePartyId)) {
										alreadyAssigned = true;
									} else {
										responsibleForId = DataHelper.getResponsibleForParty(delegator, importedData.getString("jobFamily"), countryGeoId, importedData.getString("city"), importedData.getString("postalCode"), importedData.getString("leadScore"), importedData.getString("virtualTeamId"));
									}
								}
								
								if (UtilValidate.isEmpty(responsibleForId) && UtilValidate.isNotEmpty(importedData.getString("uploadedByUserLoginId")) && !alreadyAssigned) {
									GenericValue uploadedByUserLogin = EntityUtil.getFirst( delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", importedData.getString("uploadedByUserLoginId")), null, false) );
									if (DataHelper.isResponsibleForParty(delegator, uploadedByUserLogin.getString("partyId"))) {
										responsibleForId = uploadedByUserLogin.getString("partyId");
									}
								}*/
								
								Boolean personResponsibleForValidation = true;
						        if(UtilValidate.isNotEmpty(primaryPhoneNumber) && UtilValidate.isNotEmpty(secondaryPhoneNumber)) {
						            Map<String, Object> dndStatusPrimaryPhoneMp = DataUtil.getDndStatus(delegator, primaryPhoneNumber);
						            Map<String, Object> dndStatusSecondaryPhoneMp = DataUtil.getDndStatus(delegator, secondaryPhoneNumber);
						            if("Y".equals(dndStatusPrimaryPhoneMp.get("dndStatus")) && "Y".equals(dndStatusSecondaryPhoneMp.get("dndStatus"))) {
						            	personResponsibleForValidation = false;
						            	EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
												EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId),
									            //EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
									            EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("LEAD")),
									            EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
									            EntityUtil.getFilterByDateExpr()
									            ), EntityOperator.AND);

										List<GenericValue> responsibleFor = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).orderBy("fromDate DESC").queryList();
										if (responsibleFor != null && responsibleFor.size() > 0) {
											for(GenericValue responsibleForGV: responsibleFor) {
												responsibleForGV.put("thruDate", UtilDateTime.nowTimestamp());
												responsibleForGV.store();
											}
										}
						            }
						        }
						        
								/*if (UtilValidate.isNotEmpty(responsibleForId) && UtilValidate.isNotEmpty(leadId) && personResponsibleForValidation && !alreadyAssigned) {
									Map<String, Object> associationContext = new HashMap<String, Object>();
									associationContext.put("partyId", leadId);
									associationContext.put("roleTypeIdFrom", "LEAD");
									associationContext.put("accountPartyId", responsibleForId);
									associationContext.put("userLogin", userLogin);
									
									Map<String, Object> associationResult = dispatcher.runSync("crmsfa.updatePersonResponsibleFor", associationContext);
									
									if (!ServiceUtil.isError(associationResult)) {
										
										if (UtilValidate.isNotEmpty(partySupplementalData)) {
											partySupplementalData.put("leadAssignTo", responsibleForId);
										}
										
										importedData.put("leadAssignTo", responsibleForId);
										importedData.store();
										
										Debug.logInfo("Successfully Changed Account Responsible For, leadPartyId="+leadId+", responsiblePartyId="+responsibleForId, MODULE);
									}
								}*/
								
								// assign responsible for [end]
								
								// call status change [start]
								
								if (UtilValidate.isNotEmpty(importedData.getString("teleCallingStatus"))) {
									storeCallStatusChangeHistory(dispatcher, userLogin, leadId, importedData.getString("teleCallingStatus"), importedData.getString("teleCallingSubStatus"));
								}
								
								if (UtilValidate.isNotEmpty(importedData.getString("rmCallingStatus"))) {
									storeCallStatusChangeHistory(dispatcher, userLogin, leadId, importedData.getString("rmCallingStatus"), importedData.getString("rmCallingSubStatus"));
								}
								
								// call status change [end]
								
								// lead status change [start]
								
								List<GenericValue> leadContactHistory = delegator.findByAnd("LeadContactHistory", UtilMisc.toMap("partyId", leadId), null, false);
								
								if ( UtilValidate.isEmpty(leadContactHistory) ||
										(UtilValidate.isNotEmpty(importedData.getString("leadStatus")) && UtilValidate.isNotEmpty(partySupplementalData) 
										&& (UtilValidate.isEmpty(partySupplementalData.getString("leadStatus")) || !partySupplementalData.getString("leadStatus").equals(importedData.getString("leadStatus"))) )
										) {
									
									String leadContactStatusId = importedData.getString("leadStatus");
									if (UtilValidate.isEmpty(leadContactHistory) && UtilValidate.isEmpty(leadContactStatusId)) {
										leadContactStatusId = "LEAD_PROSPECTING";
									}
									
									Map<String, Object> statusChangeContext = new HashMap<String, Object>();
									statusChangeContext.put("leadPartyId", leadId);
									statusChangeContext.put("leadContactStatusId", leadContactStatusId);
									statusChangeContext.put("userLogin", userLogin);
									
									Map<String, Object> statusChangeResult = dispatcher.runSync("crmsfa.updateLeadContactStatus", statusChangeContext);
									
									if (!ServiceUtil.isError(statusChangeResult)) {
										Debug.logInfo("Successfully Lead Status Change, fromStatusId="+partySupplementalData.getString("leadStatus")+", toStatusId="+importedData.getString("leadStatus"), MODULE);
									}
									
								}
								
								// lead status change [end]
								
								// DedupAutoSegmentation [start]
								String dedupAutoSegmentValueId = UtilProperties.getPropertyValue("crm", "dedup.auto.segmentValueId");
						        if (UtilValidate.isNotEmpty(importedData.getString("isDedupAutoSegmentation")) && importedData.getString("isDedupAutoSegmentation").equals("Y")) {
						        	
						        	if (UtilValidate.isNotEmpty(dedupAutoSegmentValueId)) {
						        		
						        		GenericValue segmentValue = delegator.findOne("CustomField", UtilMisc.toMap("customFieldId", dedupAutoSegmentValueId), false);
						        		if (UtilValidate.isNotEmpty(segmentValue)) {
						        			
						        			String partyId = leadId;
											GenericValue associatedEntity = delegator.findOne("CustomFieldPartyClassification", UtilMisc.toMap("groupId", segmentValue.getString("groupId"), "customFieldId", dedupAutoSegmentValueId, "partyId", partyId), false);
											if (UtilValidate.isEmpty(associatedEntity)) {
												
												associatedEntity = delegator.makeValue("CustomFieldPartyClassification");
												
												associatedEntity.put("groupId", segmentValue.getString("groupId"));
												associatedEntity.put("customFieldId", dedupAutoSegmentValueId);
												associatedEntity.put("partyId", partyId);
												associatedEntity.put("inceptionDate", UtilDateTime.nowTimestamp());
												
												associatedEntity.create();
												
											}
											
						        		}
						        		
						        	}
						        	
						        }
						        
						        // DedupAutoSegmentation [end]
						        
						        delegator.store(partySupplementalData);
								
							}
						}
						
						Debug.log("++++++++++++++++++++EtlSelfServiceImpl+++++++++++++++++success++");
						
					}
				}
			}
			Debug.logInfo("End storing to Lead staging [main]...................endPoint: " + totalProceedCount
					+ ", time:" + UtilDateTime.nowAsString(), MODULE);

		} catch (Throwable t) {
			Debug.logWarning("LeadImportJob Throwable: "+t.getMessage(), MODULE);
	
		}
	}
	
	private static void storeCallStatusChangeHistory(LocalDispatcher dispatcher, GenericValue userLogin, String partyId, String callStatus, String callSubStatus) {
    	
    	try {
			String defaultLeadMarketingCampaignId = UtilProperties.getPropertyValue("crm", "default.lead.marketingCampaignId");
			String defaultLeadContactListId = UtilProperties.getPropertyValue("crm", "default.lead.contactListId");
			
			Map<String, Object> callStatusUpdateContext = new HashMap<String, Object>();
			callStatusUpdateContext.put("partyId", partyId);
			callStatusUpdateContext.put("callStatus", callStatus);
			callStatusUpdateContext.put("callSubStatus", callSubStatus);
			callStatusUpdateContext.put("callBackDate", UtilDateTime.nowDateString("dd-MM-yyyy"));
			callStatusUpdateContext.put("marketingCampaignId", defaultLeadMarketingCampaignId);
			callStatusUpdateContext.put("contactListId", defaultLeadContactListId);
			callStatusUpdateContext.put("userLogin", userLogin);
			
			Map<String, Object> callStatusUpdateResult = dispatcher.runSync("callListStatus", callStatusUpdateContext);
			
			if (!ServiceUtil.isError(callStatusUpdateResult)) {
				Debug.logInfo("Successfully store call status history, partyId: "+partyId+", callStatus: "+callStatus, MODULE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
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
