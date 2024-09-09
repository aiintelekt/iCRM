/**
 * 
 */
package org.groupfio.etl.process.job;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastList;


/**
 * @author Palanivel
 *
 */
public class EtlSupplierImportJob extends Thread {

	private static String MODULE = EtlSupplierImportJob.class.getName();
	
	private FastList<GenericValue> entitiesToCreate;
	
	private LocalDispatcher dispatcher;
	private Delegator delegator;
	
	private int numTopProcess;
	private String etlModelId;
	private String userLoginId;
	private String groupId;
	
	public void run(){
		GenericValue makeRequest = null;
		try {
			Debug.logInfo("Start storing to Supplier staging [main]...................size: "+entitiesToCreate.size()+", time:"+UtilDateTime.nowAsString(), MODULE);
			int totalProceedCount = 0;
			int proceedCount = 0;
            FastList<GenericValue> entitiesToCreateCunk = FastList.newInstance();
            for(GenericValue entity : entitiesToCreate){
            	proceedCount++;
            	totalProceedCount++;
            	if(proceedCount==numTopProcess || totalProceedCount==entitiesToCreate.size()){
            		entitiesToCreateCunk.add(entity);
            		
            		TransactionUtil.begin(20000);
        			
            		Debug.logInfo("Start storing to staging Supplier, startPoint: "+(numTopProcess-totalProceedCount)+", time:"+UtilDateTime.nowAsString(), MODULE);
        			delegator.storeAll(entitiesToCreateCunk);
        			Debug.logInfo("Finish storing to staging Supplier, endPoint: "+totalProceedCount+", time:"+UtilDateTime.nowAsString(), MODULE);
        			
        			TransactionUtil.commit();
            		
            		entitiesToCreateCunk = FastList.newInstance();
            		proceedCount = 0;
            	}
            	else{
            		entitiesToCreateCunk.add(entity);
            	}	            	
            }
			
          //create upload request
            TransactionUtil.begin(20000);
            makeRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("groupId",groupId,"etlModelId",etlModelId),null,false));
            if(UtilValidate.isNotEmpty(makeRequest)){
            	makeRequest.put("status", "RUNNING");
            	makeRequest.put("fromDate", UtilDateTime.nowTimestamp());
            	makeRequest.store();
            }else{
            makeRequest = delegator.makeValue("EtlUploadRequest");
            makeRequest.put("reqId", delegator.getNextSeqId("EtlUploadRequest"));
            makeRequest.put("importType", "SUPPLIER");
            makeRequest.put("etlModelId", etlModelId);
            makeRequest.put("userLogid", userLoginId);
            makeRequest.put("status", "RUNNING");
            makeRequest.put("fromDate", UtilDateTime.nowTimestamp());
            makeRequest.create();
            }
            TransactionUtil.commit();
            
			// Import to base by store procedure [start]
            Debug.logInfo("Start to execute store procedure for Supplier import.."+", time:"+UtilDateTime.nowAsString(), MODULE);

			//TransactionUtil.commit();
			Debug.logInfo("End to execute store procedure for Supplier import.."+", time:"+UtilDateTime.nowAsString(), MODULE);
			// Import to base by store procedure [end]
			
			/*PartyRepositoryInterface partyRepo = domainsDirectory.getPartyDomain().getPartyRepository();			
			partyRepo.createOrUpdate(entitiesToCreate);*/
			
			// mark as imported [start]
			
			for(GenericValue entity : entitiesToCreate){
				entity.put("importStatusId","DATAIMP_IMPORTED");
			}
			delegator.storeAll(entitiesToCreate);
			
			// mark as imported [end]
			//Updating Request Upload
			if(makeRequest != null && makeRequest.size()>0){
				String requestId = makeRequest.getString("reqId");
				//GenericValue updateRequest = delegator.findByPrimaryKey("EtlUploadRequest",UtilMisc.toMap("reqId",requestId,"etlModelId",etlModelId));
				GenericValue updateRequest = EntityQuery.use(delegator).from("EtlUploadRequest").where("reqId", requestId,"etlModelId",etlModelId ).queryFirst();
				if(UtilValidate.isNotEmpty(updateRequest)){
					updateRequest.put("status", "FINISHED");
					updateRequest.put("thruDate", UtilDateTime.nowTimestamp());
					updateRequest.store();					
				}
			}
			Debug.logInfo("End storing to Supplier staging [main]...................endPoint: "+totalProceedCount+", time:"+UtilDateTime.nowAsString(), MODULE);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			if(makeRequest != null && makeRequest.size()>0){
				String requestId = makeRequest.getString("reqId");
				try {
					//GenericValue updateRequest = delegator.findByPrimaryKey("EtlUploadRequest",UtilMisc.toMap("reqId",requestId,"etlModelId",etlModelId));
					GenericValue updateRequest = EntityQuery.use(delegator).from("EtlUploadRequest").where("reqId", requestId,"etlModelId",etlModelId ).queryFirst();
					if(UtilValidate.isNotEmpty(updateRequest)){
						updateRequest.put("status", "ERROR");
						updateRequest.put("thruDate", UtilDateTime.nowTimestamp());
						updateRequest.store();					
					}
				} catch (GenericEntityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			/*e.printStackTrace();*/
			String errMsg = UtilProperties.getPropertyValue("Etl-Process.properties","etlSupplierImportErrorMsg") + e.getMessage();
            Debug.logError(e, errMsg, MODULE);
		}
	}

	public FastList<GenericValue> getEntitiesToCreate() {
		return entitiesToCreate;
	}

	public void setEntitiesToCreate(FastList<GenericValue> entitiesToCreate) {
		this.entitiesToCreate = entitiesToCreate;
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
	public String getUserLoginId() {
		return userLoginId;
	}

	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
