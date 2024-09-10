package org.groupfio.crm.service.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.LinkedMap;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.crm.service.util.DataHelper;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelFieldTypeReader;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/***
 * 
 * @author Mahendran Thanasekaran
 * @since 12-9-2019
 *
 */
public class LeaveOrderServiceImpl {
    private static final String MODULE = LeaveOrderServiceImpl.class.getName();
    public static Map<String, Object> createLeaveOrder(DispatchContext dctx, Map < String, Object > context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Map<String, Object> requestParameters = (HashMap<String, Object>) context.get("requestContext");
            String externalId = ParamUtil.getString(requestParameters, "externalId");
            String objTypeId = ParamUtil.getString(requestParameters, "objTypeId");
            String objRefId = ParamUtil.getString(requestParameters, "objRefId");
            String custRequestId = ParamUtil.getString(requestParameters, "custRequestId");
            if(UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(objTypeId) && UtilValidate.isNotEmpty(objRefId)) {
                GenericValue custReqObj = EntityQuery.use(delegator).select("custRequestId").from("CustRequestObjects").where("custRequestId",custRequestId,"objTypeId",objTypeId,"objRefId",objRefId).queryFirst();
                if(UtilValidate.isNotEmpty(custReqObj)) {
                    result.putAll(ServiceUtil.returnError("Object ID Already Exists."));
                    result.put("errorCode", "E5001");
                    return result;
                }
            } else {
                result.putAll(ServiceUtil.returnError("Required parameter missing."));
                return result;
            }
            // check config exists or not
            GenericValue custReqObjConfig = EntityQuery.use(delegator).from("CustRequestObjectsConfig").where("objTypeId",objTypeId).queryFirst();
            if(UtilValidate.isEmpty(custReqObjConfig)) {
                result.putAll(ServiceUtil.returnError("Object Type Id Not Exists."));
                result.put("errorCode", "E5004");
                return result;
            }
            List<Map<String, Object>> objParams = (List<Map<String, Object>>) requestParameters.get("objParams");
            String prefix = EntityUtilProperties.getPropertyValue( "system","leave.order.field.prefix", delegator);
            if(UtilValidate.isEmpty(prefix))
                prefix = "objParamValue";
            ModelEntity modelEntity = delegator.getModelEntity("CustRequestObjects");
            GenericValue custRequestObject = delegator.makeValue("CustRequestObjects",UtilMisc.toMap("custRequestId",custRequestId,"objTypeId",objTypeId,"objRefId",objRefId));
            if(UtilValidate.isNotEmpty(objParams)) {
                ModelFieldTypeReader modelFieldTypeReader = ModelFieldTypeReader.getModelFieldTypeReader(delegator.getGroupHelperInfo("org.ofbiz").getHelperBaseName());
                for(Map<String, Object> data : objParams) {
                    String objName = (String) data.get("param_name");
                    String fieldName = DataHelper.getDynamicColumnName(delegator, objTypeId, objName, prefix);
                    String objValue = (String) data.get("param_value");
                    int valueLength = objValue.length();
                    if(UtilValidate.isNotEmpty(fieldName)) {
                        ModelField field = modelEntity.getField(fieldName);
                        if(UtilValidate.isNotEmpty(field)) {
                            ModelFieldType type = modelFieldTypeReader.getModelFieldType(field.getType());
                            int fieldSize = type.stringLength();
                            if(valueLength > fieldSize) {
                                objValue = objValue.substring(0, fieldSize-1);
                            }
                            custRequestObject.set(fieldName, objValue);
                        }
                        
                    }
                }
            } /*else {
                result.putAll(ServiceUtil.returnError("Object parameter is empty"));
                result.put("errorCode", "E5003");
                return result;
            } */
            custRequestObject.set("createdOn", requestParameters.get("createdOn"));
            custRequestObject.set("createdBy", ParamUtil.getString(requestParameters, "createdBy"));
            custRequestObject.create();
            result.put("custRequestId", custRequestId);
            result.put("externalId", externalId);
            result.put("objRefId", objRefId);
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        result.putAll(ServiceUtil.returnSuccess("Leave order successfully created"));
        return result;
    }
    public static Map<String, Object> updateLeaveOrder(DispatchContext dctx, Map < String, Object > context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Map<String, Object> requestParameters = (HashMap<String, Object>) context.get("requestContext");
            String externalId = ParamUtil.getString(requestParameters, "externalId");
            String objTypeId = ParamUtil.getString(requestParameters, "objTypeId");
            String objRefId = ParamUtil.getString(requestParameters, "objRefId");
            String custRequestId = ParamUtil.getString(requestParameters, "custRequestId");
            if(UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(objTypeId) && UtilValidate.isNotEmpty(objRefId)) {
                GenericValue custReqObj = EntityQuery.use(delegator).select("custRequestId").from("CustRequestObjects").where("custRequestId",custRequestId,"objTypeId",objTypeId,"objRefId",objRefId).queryFirst();
                if(UtilValidate.isEmpty(custReqObj)) {
                    result.putAll(ServiceUtil.returnError("Object ID do not Exists."));
                    result.put("errorCode", "E5002");
                    return result;
                }
            } else {
                result.putAll(ServiceUtil.returnError("Required parameter missing."));
                return result;
            }
            // check config exists or not
            GenericValue custReqObjConfig = EntityQuery.use(delegator).from("CustRequestObjectsConfig").where("objTypeId",objTypeId).queryFirst();
            if(UtilValidate.isEmpty(custReqObjConfig)) {
                result.putAll(ServiceUtil.returnError("Object Type Id Not Exists."));
                result.put("errorCode", "E5004");
                return result;
            }
            @SuppressWarnings("unchecked")
			List<Map<String, Object>> objParams = (List<Map<String, Object>>) requestParameters.get("objParams");
            String prefix = EntityUtilProperties.getPropertyValue( "system","leave.order.field.prefix", delegator);
            if(UtilValidate.isEmpty(prefix))
                prefix = "objParamValue";
            ModelEntity modelEntity = delegator.getModelEntity("CustRequestObjects");
            GenericValue custRequestObject = delegator.makeValue("CustRequestObjects",UtilMisc.toMap("custRequestId",custRequestId,"objTypeId",objTypeId,"objRefId",objRefId));
            if(UtilValidate.isNotEmpty(objParams)) {
                ModelFieldTypeReader modelFieldTypeReader = ModelFieldTypeReader.getModelFieldTypeReader(delegator.getGroupHelperInfo("org.ofbiz").getHelperBaseName());
                for(Map<String, Object> data : objParams) {
                    String objName = (String) data.get("param_name");
                    String fieldName = DataHelper.getDynamicColumnName(delegator, objTypeId, objName, prefix);
                    String objValue = (String) data.get("param_value");
                    int valueLength = objValue.length();
                    if(UtilValidate.isNotEmpty(fieldName)) {
                        ModelField field = modelEntity.getField(fieldName);
                        if(UtilValidate.isNotEmpty(field)) {
                            ModelFieldType type = modelFieldTypeReader.getModelFieldType(field.getType());
                            int fieldSize = type.stringLength();
                            if(valueLength> fieldSize) {
                                objValue = objValue.substring(0, fieldSize-1);
                            }
                            custRequestObject.set(fieldName, objValue);
                        }
                        
                    }
                }
            } /*else {
                result.putAll(ServiceUtil.returnError("Object parameter is empty"));
                result.put("errorCode", "E5003");
                return result;
            } */
            custRequestObject.set("modifiedOn", requestParameters.get("modifiedOn"));
            custRequestObject.set("modifiedBy", ParamUtil.getString(requestParameters, "modifiedBy"));
            custRequestObject.store();
            result.put("custRequestId", custRequestId);
            result.put("externalId", externalId);
            result.put("objRefId", objRefId);
        } catch (Exception e) {
            //e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        result.putAll(ServiceUtil.returnSuccess("Leave order successfully updated"));
        return result;
    }
    public static Map<String, Object> findLeaveOrder(DispatchContext dctx, Map < String, Object > context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Map<String, Object> requestParameters = (HashMap<String, Object>) context.get("requestContext");
            String externalId = ParamUtil.getString(requestParameters, "externalId");
            String custRequestId = ParamUtil.getString(requestParameters, "custRequestId");
            //String objTypeId = "";
            GenericValue custRequestObj = EntityQuery.use(delegator).from("CustRequestObjects").where("custRequestId",custRequestId).queryFirst();
            if(UtilValidate.isEmpty(custRequestObj)) {
                result.putAll(ServiceUtil.returnError("No Object IDs Exists"));
                result.put("errorCode", "E5003");
                return result;
            }
            
            List<Map<String, Object>> objects = new LinkedList<Map<String,Object>>();
            List<GenericValue> custRequestObjects = EntityQuery.use(delegator).from("CustRequestObjects").where("custRequestId",custRequestId).queryList();
            if(UtilValidate.isNotEmpty(custRequestObjects)) {
                for(GenericValue custRequestObject : custRequestObjects) {
                    Map<String, Object> objMap = new LinkedHashMap<String, Object>();
                    String objTypeId = custRequestObj.getString("objTypeId");
                    String objRefId = custRequestObject.getString("objRefId");
                    objMap.put("obj_type_id", objTypeId);
                    objMap.put("obj_ref_id", objRefId);
                    //List<Map<String, Object>> objParams = new LinkedList<Map<String,Object>>();
                    String prefix = UtilValidate.isNotEmpty(EntityUtilProperties.getPropertyValue( "system","leave.order.field.prefix", delegator)) ? EntityUtilProperties.getPropertyValue( "system","leave.order.field.prefix", delegator) : "objParamValue";
                    List<GenericValue> custReqObjConfig = EntityQuery.use(delegator).select("objParamLocId","objParamName").from("CustRequestObjectsConfig").where("objTypeId",objTypeId).orderBy("objParamLocId").queryList();
                    Map<String, Object> objConfig = new LinkedMap<String, Object>();
                    if(UtilValidate.isNotEmpty(custReqObjConfig)) {
                        objConfig = DataUtil.getMapFromGeneric(custReqObjConfig, "objParamLocId", "objParamName", false);
                        objConfig = objConfig.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(e-> prefix+e.getKey(), e->e.getValue()));
                    }
                    if( UtilValidate.isNotEmpty(objConfig)) {
                        List<Map<String, Object>> paramList = new LinkedList<Map<String,Object>>(); 
                        for(String key : objConfig.keySet()) {
                            String paramValue = custRequestObject.getString(key);
                            if(UtilValidate.isNotEmpty(paramValue)) {
                                Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
                                paramMap.put("param_name", objConfig.get(key));
                                paramMap.put("param_value", paramValue);	
                                paramList.add(paramMap);
                            }   
                        }
                        objMap.put("obj_params", paramList);
                    }
                    objects.add(objMap);
                }
            }
           
            result.put("custRequestId", custRequestId);
            result.put("externalId", externalId);
            result.put("objRefIdList", objects);
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        result.putAll(ServiceUtil.returnSuccess("Leave order successfully updated"));
        return result;
    }
}
