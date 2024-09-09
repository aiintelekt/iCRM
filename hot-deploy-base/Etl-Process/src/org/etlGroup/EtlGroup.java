package org.etlGroup;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class EtlGroup {
/**
 * For adding the ETL grouping	
 * @param dctx
 * @param context
 * @return
 */
	public static Map<String, Object> addETLGroup(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		
		String groupId = (String) context.get("groupId");
		String groupName = (String) context.get("groupName");
		String sequenceNo = (String) context.get("sequence");
		String channelAccessType = (String) context.get("channelAccessType");
	    Debug.logInfo("contextgottttttaddFioField"+context,"");
		
        try {
        	//GenericValue etlGroupingGV=delegator.findByPrimaryKey("EtlGrouping",UtilMisc.toMap("groupId", groupId));
        	GenericValue etlGroupingGV = EntityQuery.use(delegator).from("EtlGrouping").where("groupId", groupId).cache().queryOne();
        	if (UtilValidate.isEmpty(etlGroupingGV)) {
            	GenericValue etlGrouping=delegator.makeValue("EtlGrouping");	
    			
            	etlGrouping.set("groupId", groupId);
            	etlGrouping.set("groupName", groupName);
            	etlGrouping.set("sequenceNo", sequenceNo);    
            	etlGrouping.set("channelAccessType", channelAccessType);    
            	etlGrouping.create();
			}

        } catch (GeneralException e) {
            Debug.logError(e, "");
            return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties","etlGroupErrorMsg1") + e.toString());
        }

        return ServiceUtil.returnSuccess();
    }
/**
 * 	For updating ETLGroup
 * @param dctx
 * @param context
 * @return
 */
    public static Map<String, Object> updateETLGroup(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String groupId = (String) context.get("groupId");
        String groupName = (String) context.get("groupName");
       
	    String sequenceNumber = (String) context.get("sequenceNumber");
		Debug.logInfo("contextgottttttupdateFioFieldGroup"+context, "");		
		
        try {
        	
			//GenericValue etlGrouping=delegator.findByPrimaryKey("EtlGrouping",UtilMisc.toMap("groupId",groupId));	
			GenericValue etlGrouping = EntityQuery.use(delegator).from("EtlGrouping").where("groupId", groupId).queryOne();
			if(etlGrouping!=null){
				etlGrouping.set("groupName", groupName);
				etlGrouping.set("sequenceNo", sequenceNumber);				
				
				etlGrouping.store();

			}
			
        } catch (GeneralException e) {
            Debug.logError(e, "");
            return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties","etlGroupErrorMsg2") + e.toString());
        }

        return ServiceUtil.returnSuccess();
    }
    
    /**
     * For remove ETLGroup
     * @param dctx
     * @param context
     * @return
     */
	public static Map<String, Object> removeETLGroup(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String groupId = (String) context.get("groupId");
       
	    Debug.logInfo("contextgottttttremoveFioGroup"+context, "");
		
        try {
			delegator.removeByAnd("EtlGrouping",UtilMisc.toMap("groupId",groupId));		
        } catch (GeneralException e) {
            Debug.logError(e, "");
            return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties","etlGroupErrorMsg3") + e.toString());
        }

        return ServiceUtil.returnSuccess();
    }
	
public static Map etlGroupingPagination(DispatchContext dctx, Map context) throws SQLException, GenericEntityException,org.ofbiz.service.GenericServiceException {
    	
		Delegator delegator = dctx.getDelegator();
    	boolean limitView = ((Boolean) context.get("limitView")).booleanValue();
        int defaultViewSize = ((Integer) context.get("defaultViewSize")).intValue();
        List FinalRecordValues = (List) context.get("FinalRecordValues");         
        List subCat = null;

        //Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		int viewIndex = 1;
        try {
            viewIndex = Integer.valueOf((String) context.get("viewIndexString")).intValue();
        } catch (Exception e) {
            viewIndex = 1;
        }
        
        int viewSize = defaultViewSize;
        try {
            viewSize = Integer.valueOf((String) context.get("viewSizeString")).intValue();
        } catch (Exception e) {
            viewSize = defaultViewSize;
        }   
        int listSize = 0;
        int lowIndex = 0;
        int highIndex = 0;

        if (limitView) {
            // get the indexes for the partial list
            lowIndex = (((viewIndex - 1) * viewSize) + 1);
            highIndex = viewIndex * viewSize;
            Debug.logInfo("Low index value is "+lowIndex,""); 
            Debug.logInfo("High index value is "+highIndex,""); 
        }
        subCat=(List)FinalRecordValues;
        if (subCat != null) {
               // if (useCacheForMembers) {

                    listSize = subCat.size();
                    Debug.logInfo("List size value is "+listSize,""); 

                    if (highIndex > listSize)
                    {
                        highIndex = listSize;
                    }
         

                    if (limitView) {
                        Debug.logInfo("High index value  for limit true is "+highIndex,""); 

                        subCat = subCat.subList(lowIndex-1, highIndex);
                         

                    } else {
                        lowIndex = 1;
                        highIndex = listSize;
                    }
              //  }
        }

        Map result = new HashMap();
        result.put("viewIndex", new Integer(viewIndex));
        result.put("viewSize", new Integer(viewSize));
        result.put("lowIndex", new Integer(lowIndex));
        result.put("highIndex", new Integer(highIndex));
        result.put("listSize", new Integer(listSize));
        if (subCat != null) result.put("subCat", subCat);
        Debug.logInfo("Result value is "+result,""); 
        return result;      
        
    } 	

public static Map<String, Object> updateEtlHeaderConfig(DispatchContext dctx, Map<String, Object> context) {
	Delegator delegator = dctx.getDelegator();
	String propertyName = (String) context.get("propertyName");
    String propertyValue = (String) context.get("propertyValue");
    try {
    	if(UtilValidate.isNotEmpty(propertyName))
    	{
			//GenericValue TenantPropertiesGV=delegator.findByPrimaryKey("TenantProperties",UtilMisc.toMap("resourceName","ETL","propertyName",propertyName));		
    		GenericValue TenantPropertiesGV = EntityQuery.use(delegator).from("TenantPropertiesGV").where("resourceName","ETL","propertyName",propertyName).queryOne();
    		if(UtilValidate.isNotEmpty(TenantPropertiesGV)){
				TenantPropertiesGV.set("propertyValue", propertyValue);
				TenantPropertiesGV.store();
			}
    	}
		
    } catch (GeneralException e) {
        Debug.logError(e, "");
        return ServiceUtil.returnError(UtilProperties.getPropertyValue("Etl-Process.properties","etlGroupErrorMsg4")  + e.toString());
    }

    return ServiceUtil.returnSuccess();
}

}
