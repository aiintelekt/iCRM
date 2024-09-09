package org.etlProcessGrouping;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class EtlProcessGrouping {
	/**
	 * For adding the EtlProcessGrouping
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> addEtlProcessGrouping(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		
		String groupId = (String) context.get("groupId");
		String processId = (String) context.get("processId");
		String sequenceNo = (String) context.get("sequence");
		
	    Debug.logInfo("contextgottttttaddFioField"+context,"");
		
        try {
        	//GenericValue EtlGroupingGV=delegator.findByPrimaryKey("EtlGrouping",UtilMisc.toMap("groupId", groupId));
        	//GenericValue EtlProcessGV=delegator.findByPrimaryKey("EtlProcess",UtilMisc.toMap("processId", processId));
        	
        	//GenericValue etlModelGroupingGV=delegator.findByPrimaryKey("EtlProcessGrouping",UtilMisc.toMap("groupId", groupId,"processId",processId));
			
        	GenericValue EtlGroupingGV = EntityQuery.use(delegator).from("EtlGrouping").where("groupId", groupId).queryOne();
        	GenericValue EtlProcessGV = EntityQuery.use(delegator).from("EtlProcess").where("processId", processId).queryOne();
        	GenericValue etlModelGroupingGV = EntityQuery.use(delegator).from("EtlProcessGrouping").where("groupId", groupId,"processId",processId).queryOne();
       
        	if (UtilValidate.isEmpty(etlModelGroupingGV)) {
	        	GenericValue EtlModelGrouping=delegator.makeValue("EtlProcessGrouping");	
	        	
				EtlModelGrouping.set("groupId", groupId);
				EtlModelGrouping.set("processId", processId);
				EtlModelGrouping.set("processName", EtlProcessGV.getString("processName"));
				EtlModelGrouping.set("groupName", EtlGroupingGV.getString("groupName"));
				//added by m.vijayakumar for adding table name insertion date:13/07/2016 desc: due to unable to filter by model because of unavailable of etltable name
				
				//getting the model name based on process
				
				/*
				//GenericValue etlProcess = delegator.findByPrimaryKey("EtlProcess",UtilMisc.toMap("processId",processId));
				GenericValue etlProcess = EntityQuery.use(delegator).from("EtlModel").where("processId", processId).queryOne();
				if(UtilValidate.isNotEmpty(etlProcess))
				{
					List<GenericValue> etlSourceTable = delegator.findByAnd("EtlSourceTable", UtilMisc.toMap("listName",etlProcess.getString("modalName")),null,false);
					if(UtilValidate.isNotEmpty(etlSourceTable))
					{
						GenericValue eltSource = EntityUtil.getFirst(etlSourceTable);
						EtlModelGrouping.set("tableName", eltSource.getString("tableName"));
					}
				}*/
				
				EtlModelGrouping.set("tableName", EtlProcessGV.getString("tableName"));
				
				EtlModelGrouping.set("sequenceNo", sequenceNo);
				
				EtlModelGrouping.create();
			}else {
				return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlProcessGroupErrorMsg1"));				
			}

        } catch (GeneralException e) {
            Debug.logError(e, "");
            return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlProcessGroupErrorMsg2") + e.toString());
        }

        return ServiceUtil.returnSuccess();
    }
	/**
	 * For updating the EtlProcessGrouping 
	 * @param dctx
	 * @param context
	 * @return
	 */
    public static Map<String, Object> updateEtlProcessGrouping(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String groupId = (String) context.get("groupId");
        String processId = (String) context.get("processId");
        String sequenceNumber = (String) context.get("sequenceNumber");       
	   
		Debug.logInfo("contextgottttttupdateFioFieldGroup"+context, "");		
		
        try {
        	
        	//GenericValue etlProcessGroupingGV=delegator.findByPrimaryKey("EtlProcessGrouping",UtilMisc.toMap("groupId", groupId,"processId",processId));
        	GenericValue etlProcessGroupingGV = EntityQuery.use(delegator).from("EtlProcessGrouping").where("groupId", groupId,"processId",processId).queryOne();

        	if (UtilValidate.isNotEmpty(etlProcessGroupingGV)) {
				etlProcessGroupingGV.set("sequenceNo", sequenceNumber);		
				
					//added by m.vijayakumar for adding table name insertion date:13/07/2016 desc: due to unable to filter by model because of unavailable of etltable name
				
				//getting the model name based on process
				
				//GenericValue etlProcess = delegator.findByPrimaryKey("EtlProcess",UtilMisc.toMap("processId",processId));
	        	GenericValue etlProcess = EntityQuery.use(delegator).from("EtlProcess").where("processId",processId).cache().queryFirst();

				if(UtilValidate.isNotEmpty(etlProcess))
				{
					List<GenericValue> etlSourceTable = delegator.findByAnd("EtlSourceTable", UtilMisc.toMap("listName",etlProcess.getString("modalName")),null,false);
					if(UtilValidate.isNotEmpty(etlSourceTable))
					{
						GenericValue eltSource = EntityUtil.getFirst(etlSourceTable);
						etlProcessGroupingGV.set("tableName", eltSource.getString("tableName"));
					}
				}
				
				//end @vijayakumar
				etlProcessGroupingGV.store();
			}else {
				return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "EtlProcessGroupErrorMsg3"));				
			}
			
        } catch (GeneralException e) {
            Debug.logError(e, "");
            return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "etlGroupErrorMsg3") + e.toString());
        }

        return ServiceUtil.returnSuccess();
    }
    /**
     * For remove the EtlProcessGrouping
     * @param dctx
     * @param context
     * @return
     */
	public static Map<String, Object> removeEtlProcessGrouping(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String groupId = (String) context.get("groupId");
        String processId = (String) context.get("processId");
        String sequenceNumber = (String) context.get("sequenceNumber");
       
	    Debug.logInfo("contextgottttttremoveFioGroup"+context, "");
		
        try {
        	delegator.removeByAnd("EtlProcessGrouping",UtilMisc.toMap("groupId", groupId,"processId",processId));			
        } catch (GeneralException e) {
            Debug.logError(e, "");
            return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties", "etlGroupErrorMsg3") + e.toString());
        }

        return ServiceUtil.returnSuccess();
    }     
	

}
