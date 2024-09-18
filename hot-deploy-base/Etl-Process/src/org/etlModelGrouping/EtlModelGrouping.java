package org.etlModelGrouping;

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

public class EtlModelGrouping {
	/**
	 * For adding the EtlModelGrouping
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> addEtlModelGrouping(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		
		String groupId = (String) context.get("groupId");
		String modelId = (String) context.get("modelId");
		String sequenceNo = (String) context.get("sequence");
		
	    Debug.logInfo("contextgottttttaddFioField"+context,"");
		
        try {
        	//GenericValue EtlGroupingGV=delegator.findByPrimaryKey("EtlGrouping",UtilMisc.toMap("groupId", groupId));
        	GenericValue EtlGroupingGV = EntityQuery.use(delegator).from("EtlGrouping").where("groupId", groupId).queryOne();
        	//GenericValue EtlModelGV=delegator.findByPrimaryKey("EtlModel",UtilMisc.toMap("modelId", modelId));
        	GenericValue EtlModelGV = EntityQuery.use(delegator).from("EtlModel").where("modelId", modelId).queryOne();
        	//GenericValue etlModelGroupingGV=delegator.findByPrimaryKey("EtlModelGrouping",UtilMisc.toMap("groupId", groupId,"modelId",modelId));
        	GenericValue etlModelGroupingGV = EntityQuery.use(delegator).from("EtlModelGrouping").where("groupId", groupId,"modelId",modelId).queryOne();
        	if (UtilValidate.isEmpty(etlModelGroupingGV)) {
	        	GenericValue EtlModelGrouping=delegator.makeValue("EtlModelGrouping");	
				
	        	
				EtlModelGrouping.set("groupId", groupId);
				EtlModelGrouping.set("modelId", modelId);
				EtlModelGrouping.set("modelName", EtlModelGV.getString("modelName"));
				EtlModelGrouping.set("groupName", EtlGroupingGV.getString("groupName"));	
				//added by m.vijayakumar for adding table name insertion date:13/07/2016 desc: due to unable to filter by model because of unavailable of etltable name
				List<GenericValue> etlSourceTable = delegator.findByAnd("EtlSourceTable", UtilMisc.toMap("listName",EtlModelGV.getString("modelName")),null,false);
				if(UtilValidate.isNotEmpty(etlSourceTable))
				{
					GenericValue eltSource = EntityUtil.getFirst(etlSourceTable);
					EtlModelGrouping.set("tableName", eltSource.getString("tableName"));
				}
				//end @vijayakumar
				EtlModelGrouping.set("sequenceNo", sequenceNo);
				
				EtlModelGrouping.create();
			}else {
				return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties","etlModelGroupingErrorMsg1"));				
			}

        } catch (GeneralException e) {
            Debug.logError(e, "");
            return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties","etlModelGroupingErrorMsg2") + e.toString());
        }

        return ServiceUtil.returnSuccess();
    }
	/**
	 * For update EtlModelGrouping
	 * @param dctx
	 * @param context
	 * @return
	 */
    public static Map<String, Object> updateEtlModelGrouping(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String groupId = (String) context.get("groupId");
        String modelId = (String) context.get("modelId");
        String sequenceNumber = (String) context.get("sequenceNumber");       
	   
		Debug.logInfo("contextgottttttupdateFioFieldGroup"+context, "");		
		
        try {
        	
        	//GenericValue etlModelGroupingGV=delegator.findByPrimaryKey("EtlModelGrouping",UtilMisc.toMap("groupId", groupId,"modelId",modelId));
        	GenericValue etlModelGroupingGV = EntityQuery.use(delegator).from("EtlModelGrouping").where("groupId", groupId,"modelId",modelId).queryOne();

        	if (UtilValidate.isNotEmpty(etlModelGroupingGV)) {
				etlModelGroupingGV.set("sequenceNo", sequenceNumber);	
				
				//added by m.vijayakumar for adding table name insertion date:13/07/2016 desc: due to unable to filter by model because of unavailable of etltable name
				List<GenericValue> etlSourceTable = delegator.findByAnd("EtlSourceTable", UtilMisc.toMap("listName",etlModelGroupingGV.getString("modelName")),null,false);
				if(UtilValidate.isNotEmpty(etlSourceTable))
				{
					GenericValue eltSource = EntityUtil.getFirst(etlSourceTable);
					etlModelGroupingGV.set("tableName", eltSource.getString("tableName"));
				}
				//end @vijayakumar
				
				etlModelGroupingGV.store();
			}else {
				return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties","etlModelGroupingErrorMsg3"));				
			}
			
        } catch (GeneralException e) {
            Debug.logError(e, "");
            return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties","etlGroupErrorMsg3") + e.toString());
        }

        return ServiceUtil.returnSuccess();
    }
    /**
     * For removing the EtlModelGrouping
     * @param dctx
     * @param context
     * @return
     */
	public static Map<String, Object> removeEtlModelGrouping(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String groupId = (String) context.get("groupId");
        String modelId = (String) context.get("modelId");
        String sequenceNumber = (String) context.get("sequenceNumber");
       
	    Debug.logInfo("contextgottttttremoveFioGroup"+context, "");
		
        try {
        	delegator.removeByAnd("EtlModelGrouping",UtilMisc.toMap("groupId", groupId,"modelId",modelId));			
        } catch (GeneralException e) {
            Debug.logError(e, "");
            return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties","etlGroupErrorMsg3") + e.toString());
        }

        return ServiceUtil.returnSuccess();
    }     

}
