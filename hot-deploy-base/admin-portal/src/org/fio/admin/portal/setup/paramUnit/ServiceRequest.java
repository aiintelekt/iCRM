package org.fio.admin.portal.setup.paramUnit;

import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.base.util.Debug;
import org.ofbiz.service.DispatchContext;
import java.util.Map;
import org.ofbiz.entity.Delegator;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.security.Security;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.apache.commons.lang.StringUtils;
import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.admin.portal.util.DataUtil;
import org.fio.homeapps.constants.GlobalConstants.ModeOfAction;

import java.util.Locale;

/**
 * @author Golda
 * @author Sharif
 *
 */
public class ServiceRequest {
    public static final String MODULE = ServiceRequest.class.getName();
    public static final String RESOURCE = "AdminPortalUiLabels";
    
    public static Map < String, Object > srTypeCreation(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside createServiceRequestType------" + context, MODULE);
        
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();

        String srType = (String) context.get("srType");
        String sequenceNumber = (String) context.get("sequenceNumber");
        String status = (String) context.get("status");
        String type = AdminPortalConstant.ParamUnitConstant.SR_TYPE;
        
        String isPerformUserAudit = (String) context.get("isPerformUserAudit");
        String userAuditRequestId = (String) context.get("userAuditRequestId");

        try {
            GenericValue serReqType = EntityUtil.getFirst(delegator.findByAnd("CustRequestType",
                UtilMisc.toMap("description", srType), null, false));
           
            if (UtilValidate.isEmpty(serReqType)) {
            	
            	/*if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
            		Map<String, Object> callCtxt = FastMap.newInstance();
        			Map<String, Object> callResult = FastMap.newInstance();
        			
        			callCtxt.put("serviceRequestType", "CREATE_SR_TYPE");
        			callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
        			callCtxt.put("modeOfAction", ModeOfAction.CREATE);
        			callCtxt.put("remarks", null);
        			callCtxt.put("contextMap", context);
        			callCtxt.put("userAuditRequestId", userAuditRequestId);
        			
        			callCtxt.put("userLogin", userLogin);
        			
        			callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
        			return callResult;
            	}*/
            	GenericValue addSrRecords = delegator.makeValue("CustRequestType");
            	
                String custRequestTypeId = (String)delegator.getNextSeqId("CustRequestType");
                addSrRecords.put("custRequestTypeId", custRequestTypeId);
                addSrRecords.put("description", srType);
                addSrRecords.put("seqNum", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
                
                addSrRecords.create();
                //Debug.log("==addSrRecords==" + addSrRecords);
                Debug.log("==description srType==" + srType);
                GenericValue serReqTypes = EntityUtil.getFirst(delegator.findByAnd("CustRequestAssoc",
                        UtilMisc.toMap("type",type, "code",custRequestTypeId), null, false));
                if (UtilValidate.isEmpty(serReqTypes)) {
                GenericValue addSrSubRecords = delegator.makeValue("CustRequestAssoc");
                addSrSubRecords.put("type", type);
                addSrSubRecords.put("value", srType);
                addSrSubRecords.put("code", custRequestTypeId);
                addSrSubRecords.put("sequenceNumber", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
                addSrSubRecords.put("active", status);
                addSrSubRecords.put("createdOn", UtilDateTime.nowTimestamp());
                addSrSubRecords.put("createdBy", userLogin.getString("userLoginId"));
                
                //Debug.log("addSrSubRecords>>>>>>>>>>>>"+addSrSubRecords);
                addSrSubRecords.create();
                //Debug.log(">>>>>>>>>>>>"+addSrSubRecords);
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SRTypeCreatedSuccessfully", locale));
                results.put("custRequestTypeId", custRequestTypeId);
               
                //Debug.log("==================="+results);
                }
            } else {
                return ServiceUtil.returnError("SR Type already exists");
            }

        } catch (GeneralException e) {e.printStackTrace();
            Debug.log("==error in creations===" + e.getMessage());
        }

        return results;

    }

    public static Map < String, Object > srTypeUpdation(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside updateServiceRequestType------" + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();
        String srTypeId = (String) context.get("custRequestTypeId");
        String srType = (String) context.get("srType");
        String sequenceNumber = (String) context.get("sequenceNumber");
        String status = (String) context.get("status");
        
        String isPerformUserAudit = (String) context.get("isPerformUserAudit");
        String userAuditRequestId = (String) context.get("userAuditRequestId");
        String oldContextMap = (String) context.get("oldContextMap");

        try {
        	
            GenericValue updateSrRecords = EntityUtil.getFirst(delegator.findByAnd("CustRequestType",
                UtilMisc.toMap("custRequestTypeId", srTypeId), null, false));
            
            if (UtilValidate.isNotEmpty(updateSrRecords)) {
            	
            	if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
            		Map<String, Object> callCtxt = FastMap.newInstance();
        			Map<String, Object> callResult = FastMap.newInstance();
        			
        			callCtxt.put("serviceRequestType", "UPDATE_SR_TYPE");
        			callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
        			callCtxt.put("modeOfAction", ModeOfAction.UPDATE);
        			callCtxt.put("remarks", null);
        			callCtxt.put("contextMap", context);
        			callCtxt.put("userAuditRequestId", userAuditRequestId);
        			callCtxt.put("oldContextMap", oldContextMap);
        			
        			callCtxt.put("userLogin", userLogin);
        			
        			callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
        			return callResult;
            	}
            	
            	String sr = updateSrRecords.getString("description");
            	
            	if(UtilValidate.isNotEmpty(sr) && UtilValidate.isNotEmpty(srType)) {
            		
            		if(srType.equalsIgnoreCase(sr)) {
                        updateSrRecords.put("seqNum", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
                        updateSrRecords.store();
                        //Debug.log("==updateSrRecords==" + updateSrRecords);

                        GenericValue updateSrSubRecords = EntityQuery.use(delegator).from("CustRequestAssoc")
                            .where("code", srTypeId).queryOne();
                        if (UtilValidate.isNotEmpty(updateSrSubRecords)) {
                            updateSrSubRecords.put("sequenceNumber", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
                            updateSrSubRecords.put("active", status);
                            updateSrSubRecords.put("modifiedOn", UtilDateTime.nowTimestamp());
                            updateSrSubRecords.put("modifiedBy", userLogin.getString("userLoginId"));
                            updateSrSubRecords.store();
                            results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SRTypeUpdatedSuccessfully", locale));
                            //Debug.log("==updateSrSubRecords==" + updateSrSubRecords);

                        } 
            		}
            	else {
            		GenericValue updateSrRecord = EntityUtil.getFirst(delegator.findByAnd("CustRequestType",
                            UtilMisc.toMap("description", srType), null, false));
                        if (UtilValidate.isNotEmpty(updateSrRecord)) {
                            return ServiceUtil.returnError("SR Type already exists");
                        }else {
                        	updateSrRecords.put("description", srType);
                            updateSrRecords.put("seqNum", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
                            updateSrRecords.store();
                            //Debug.log("==updateSrRecords==" + updateSrRecords);

                            GenericValue updateSrSubRecords = EntityQuery.use(delegator).from("CustRequestAssoc")
                                .where("code", srTypeId).queryOne();
                            if (UtilValidate.isNotEmpty(updateSrSubRecords)) {
                                updateSrSubRecords.put("value", srType);
                                updateSrSubRecords.put("sequenceNumber", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
                                updateSrSubRecords.put("active", status);
                                updateSrSubRecords.put("modifiedOn", UtilDateTime.nowTimestamp());
                                updateSrSubRecords.put("modifiedBy", userLogin.getString("userLoginId"));
                                updateSrSubRecords.store();
                                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SRTypeUpdatedSuccessfully", locale));
                                //Debug.log("==updateSrSubRecords==" + updateSrSubRecords);

                            } 
                        }
            	   }
                        	
            	}
            }
            results.put("custRequestTypeId",srTypeId);
        } catch (GeneralException e) {
            Debug.log("==error in updation===" + e.getMessage());
        }

        return results;
    }
    
    public static Map < String, Object > createServiceRequestArea(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside createServiceRequestArea------" + context,
            MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher
        dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();
        String typeId = (String) context.get("typeId");
        String srCategory =(String) context.get("srArea");
        String sequenceNumber =(String) context.get("sequenceNumber");
        String type = AdminPortalConstant.ParamUnitConstant.SR_CATEGORY;
        String status = (String) context.get("status");
        
        String isPerformUserAudit = (String) context.get("isPerformUserAudit");
        String userAuditRequestId = (String) context.get("userAuditRequestId");
        
        try {
        	String desc = "";
            if(UtilValidate.isNotEmpty(typeId)) {
            	GenericValue srTypeDesc = EntityUtil.getFirst(delegator.findByAnd("CustRequestType",
                        UtilMisc.toMap("custRequestTypeId", typeId), null, false));
                        desc = srTypeDesc.getString("description");
                        //Debug.log("==desc===" +desc);
            }
        	GenericValue serReqCategory = EntityUtil.getFirst(delegator.findByAnd("CustRequestCategory",UtilMisc.toMap("description", srCategory,"parentCustRequestCategoryId",typeId), null, false));
            GenericValue addSrRecords =delegator.makeValue("CustRequestCategory");
            if (UtilValidate.isEmpty(serReqCategory)) {
            	
            	/*if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
            		Map<String, Object> callCtxt = FastMap.newInstance();
        			Map<String, Object> callResult = FastMap.newInstance();
        			
        			callCtxt.put("serviceRequestType", "CREATE_SR_CAT");
        			callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
        			callCtxt.put("modeOfAction", ModeOfAction.CREATE);
        			callCtxt.put("remarks", null);
        			callCtxt.put("contextMap", context);
        			callCtxt.put("userAuditRequestId", userAuditRequestId);
        			
        			callCtxt.put("userLogin", userLogin);
        			
        			callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
        			return callResult;
            	}*/
            	
                String custRequestCategoryId =delegator.getNextSeqId("CustRequestCategory");
                addSrRecords.put("custRequestCategoryId", custRequestCategoryId);
                addSrRecords.put("parentCustRequestCategoryId", typeId);
                addSrRecords.put("custRequestTypeId", typeId);
                addSrRecords.put("description", srCategory);
                if(UtilValidate.isNotEmpty(sequenceNumber)) {
                	addSrRecords.put("seqNum", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
                }
                
                addSrRecords.create();
                //Debug.log("==addSrRecords==" + addSrRecords);
                

                GenericValue serReqCategorys = EntityUtil.getFirst(delegator.findByAnd("CustRequestAssoc",
                        UtilMisc.toMap("type",type, "code",custRequestCategoryId), null, false));
                if (UtilValidate.isEmpty(serReqCategorys)) {
                
                GenericValue addSrSubRecords = delegator.makeValue("CustRequestAssoc");
                addSrSubRecords.put("type", type);
                addSrSubRecords.put("value", srCategory);
                addSrSubRecords.put("code",custRequestCategoryId);
                addSrSubRecords.put("parentValue",desc);
                addSrSubRecords.put("parentCode",typeId);
                if(UtilValidate.isNotEmpty(sequenceNumber)) {
                	addSrSubRecords.put("sequenceNumber", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
                }
                addSrSubRecords.put("active", status);
                addSrSubRecords.put("createdOn", UtilDateTime.nowTimestamp());
                addSrSubRecords.put("createdBy", userLogin.getString("userLoginId"));
                addSrSubRecords.create();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SRCategorycreatedSuccessfully", locale));
                results.put("custRequestCategoryId",custRequestCategoryId);
        } 
            }else {
            return ServiceUtil.returnError("SR Category already exists");
        }

    } catch (GeneralException e) {
        Debug.log("==error in creations===" +e.getMessage());
    }

    return results;

    }
    public static Map < String, Object > updateSrArea(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside updateServiceRequestType------" + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();
        String typeId = (String) context.get("typeId");
        
        //String srType = typeId.substring(typeId.indexOf("(")+1, typeId.indexOf(")"));
        //String srType = typeId;
        
        String srCategory = (String) context.get("srArea");
        String categoryId = (String) context.get("custRequestCategoryId");
        String sequenceNumber = (String) context.get("sequenceNumber");
        String status = (String) context.get("status");
        
        String isPerformUserAudit = (String) context.get("isPerformUserAudit");
        String userAuditRequestId = (String) context.get("userAuditRequestId");
        String oldContextMap = (String) context.get("oldContextMap");
        
        try {
        	
        	String desc = "";
            if(UtilValidate.isNotEmpty(typeId)) {
            	GenericValue srTypeDesc = EntityUtil.getFirst(delegator.findByAnd("CustRequestType",
                        UtilMisc.toMap("custRequestTypeId", typeId), null, false));
                        desc = srTypeDesc.getString("description");
                        //Debug.log("==desc===" +desc);
            }
            GenericValue srCategoryDetails = EntityUtil.getFirst(delegator.findByAnd("CustRequestCategory",
                UtilMisc.toMap("custRequestCategoryId", categoryId), null, false));
            if (UtilValidate.isNotEmpty(srCategoryDetails)) {
            	
            	/*if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
            		Map<String, Object> callCtxt = FastMap.newInstance();
        			Map<String, Object> callResult = FastMap.newInstance();
        			
        			callCtxt.put("serviceRequestType", "UPDATE_SR_CAT");
        			callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
        			callCtxt.put("modeOfAction", ModeOfAction.UPDATE);
        			callCtxt.put("remarks", null);
        			callCtxt.put("contextMap", context);
        			callCtxt.put("userAuditRequestId", userAuditRequestId);
        			callCtxt.put("oldContextMap", oldContextMap);
        			
        			callCtxt.put("userLogin", userLogin);
        			
        			callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
        			return callResult;
            	}*/
            	
            	GenericValue updateSrCategoryRecords = EntityUtil.getFirst(delegator.findByAnd("CustRequestCategory",UtilMisc.toMap("description", srCategory,"parentCustRequestCategoryId",typeId), null, false));
            	if (UtilValidate.isNotEmpty(updateSrCategoryRecords)) {
            			String categoryIdNew = updateSrCategoryRecords.getString("custRequestCategoryId");
            			if (categoryIdNew.equalsIgnoreCase(categoryId))
            				
            			{
            				if(UtilValidate.isNotEmpty(sequenceNumber)) {
            					updateSrCategoryRecords.put("seqNum",DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0 );
            				}
            				updateSrCategoryRecords.store();
                	
            				GenericValue updateSrSubRecords = EntityQuery.use(delegator).from("CustRequestAssoc").where( "code", categoryId).queryOne();	
        					if (UtilValidate.isNotEmpty(updateSrSubRecords)) {
        						if(UtilValidate.isNotEmpty(sequenceNumber)) {
        							updateSrSubRecords.put("sequenceNumber", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
        						}
			                	 updateSrSubRecords.put("active", status);
			                	 updateSrSubRecords.put("modifiedOn", UtilDateTime.nowTimestamp());
			                     updateSrSubRecords.put("modifiedBy", userLogin.getString("userLoginId"));
			                	 updateSrSubRecords.store();
			                	 results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SRCategoryUpdatedSuccessfully", locale));
			                    //Debug.log("==updateSrSubRecords==" +updateSrSubRecords);
        					}
            			}
            			else 
            			{
            				return ServiceUtil.returnError("SR Category already exists");
            			}
            			
                  }else {
                  srCategoryDetails.put("description",srCategory);
                  srCategoryDetails.put("parentCustRequestCategoryId",typeId);
                  srCategoryDetails.put("custRequestTypeId",typeId);
                  if(UtilValidate.isNotEmpty(sequenceNumber)) {
                	  srCategoryDetails.put("seqNum",DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0 );
                  }
                  srCategoryDetails.store();
                  	
	                  	GenericValue updateSrSubRecords = EntityQuery.use(delegator).from("CustRequestAssoc").where( "code", categoryId).queryOne();	
	                  	if (UtilValidate.isNotEmpty(updateSrSubRecords)) {
	                  		updateSrSubRecords.put("value",srCategory );
	                  		updateSrSubRecords.put("parentValue",desc );
	                  		updateSrSubRecords.put("parentCode",typeId );
	                  		if(UtilValidate.isNotEmpty(sequenceNumber)) {
	                  			updateSrSubRecords.put("sequenceNumber", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
	                  		}
	                  		updateSrSubRecords.put("active", status);
	                  		updateSrSubRecords.put("modifiedOn", UtilDateTime.nowTimestamp());
	                  		updateSrSubRecords.put("modifiedBy", userLogin.getString("userLoginId"));
	                  		updateSrSubRecords.store();
	                  		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SRCategoryUpdatedSuccessfully", locale));
	                  		//Debug.log("==updateSrSubRecords==" +updateSrSubRecords);
	                  	} 
                  }
            
            }
            results.put("custRequestCategoryId",categoryId);	
        } catch (GeneralException e) {
            Debug.log("==error in updation===" + e.getMessage());
        }

        return results;
    }
    
    public static Map < String, Object > createSrSubCategory(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside createServiceRequestSubCategory------" + context,
            MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher
        dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();
        String typeId = (String) context.get("typeId");
        String srCategoryId = (String) context.get("srCategoryId");
        String srSubArea = (String) context.get("srSubArea");
        String sequenceNumber =(String) context.get("sequenceNumber");
        String type = AdminPortalConstant.ParamUnitConstant.SR_SUB_CATEGORY;
        String status = (String) context.get("statusId");
        
        String isPerformUserAudit = (String) context.get("isPerformUserAudit");
        String userAuditRequestId = (String) context.get("userAuditRequestId");
        
        try {
        	
        	String typeDesc = "";
            /*if(UtilValidate.isNotEmpty(typeId)) {
            	GenericValue srTypeDesc = EntityUtil.getFirst(delegator.findByAnd("CustRequestAssoc",
                        UtilMisc.toMap("code", typeId,"type",AdminPortalConstant.ParamUnitConstant.SR_TYPE), null, false));
            	        typeDesc = srTypeDesc.getString("value");
                        Debug.log("==typeTesc===" +typeDesc);
            }*/
            
            String categoryDesc = "";
            if(UtilValidate.isNotEmpty(srCategoryId)) {
            	GenericValue srCategoryDesc = EntityUtil.getFirst(delegator.findByAnd("CustRequestAssoc",
                        UtilMisc.toMap("code", srCategoryId,"type",AdminPortalConstant.ParamUnitConstant.SR_CATEGORY), null, false));
            	        categoryDesc = srCategoryDesc.getString("value");
                        Debug.log("==categoryDesc===" +categoryDesc);
            }
        	
        	GenericValue serReqSubArea = EntityUtil.getFirst(delegator.findByAnd("CustRequestCategory",
                    UtilMisc.toMap("description", srSubArea,"parentCustRequestCategoryId",srCategoryId,"custRequestTypeId",typeId), null, false));
            GenericValue addSrRecords =delegator.makeValue("CustRequestCategory");
            Debug.log("==serReqSubArea ???==" + serReqSubArea);
            if (UtilValidate.isEmpty(serReqSubArea)) {
            	
            	if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
            		Map<String, Object> callCtxt = FastMap.newInstance();
        			Map<String, Object> callResult = FastMap.newInstance();
        			
        			callCtxt.put("serviceRequestType", "CREATE_SR_SUB_CAT");
        			callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
        			callCtxt.put("modeOfAction", ModeOfAction.CREATE);
        			callCtxt.put("remarks", null);
        			callCtxt.put("contextMap", context);
        			callCtxt.put("userAuditRequestId", userAuditRequestId);
        			
        			callCtxt.put("userLogin", userLogin);
        			
        			callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
        			return callResult;
            	}
            	
                String custRequestCategoryId =delegator.getNextSeqId("CustRequestCategory");
                
                addSrRecords.put("custRequestCategoryId", custRequestCategoryId);
                addSrRecords.put("parentCustRequestCategoryId", srCategoryId);
                addSrRecords.put("custRequestTypeId",typeId);
                addSrRecords.put("description", srSubArea);
                if(UtilValidate.isNotEmpty(sequenceNumber)) {
                	addSrRecords.put("seqNum", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
                }
                addSrRecords.create();
                Debug.log("==addSrRecords==" + addSrRecords);

                GenericValue serReqSubAreas = EntityUtil.getFirst(delegator.findByAnd("CustRequestAssoc",
                        UtilMisc.toMap("type",type, "code",custRequestCategoryId), null, false));
                if (UtilValidate.isEmpty(serReqSubAreas)) {
	                GenericValue addSrSubRecords = delegator.makeValue("CustRequestAssoc");
	                addSrSubRecords.put("type", type);
	                addSrSubRecords.put("value", srSubArea);
	                addSrSubRecords.put("code",custRequestCategoryId);
	                addSrSubRecords.put("parentValue",categoryDesc);
	                addSrSubRecords.put("parentCode",srCategoryId);
	                addSrSubRecords.put("grandparentValue",typeDesc);
	                addSrSubRecords.put("grandparentCode",typeId);
	                if(UtilValidate.isNotEmpty(sequenceNumber)) {
		                addSrSubRecords.put("sequenceNumber", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
	                }
	                addSrSubRecords.put("active", status);
	                addSrSubRecords.put("createdOn", UtilDateTime.nowTimestamp());
	                addSrSubRecords.put("createdBy", userLogin.getString("userLoginId"));
	                addSrSubRecords.create();
	                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SRSubCategoryCreatedSuccessfully", locale));
	                results.put("custRequestCategoryId",custRequestCategoryId);
                }
        } else {
            return ServiceUtil.returnError("SR Sub Category already exists");
        }

    } catch (GeneralException e) {
        Debug.log("==error in creations===" +
            e.getMessage());
    }

    return results;

    }
    
    public static Map < String, Object > updateSrSubCategorys(DispatchContext dctx, Map < String, Object > context) {
        Debug.logInfo("------inside updateSrSubCategorys------" + context, MODULE);
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map < String, Object > results = ServiceUtil.returnSuccess();
        String typeId = (String) context.get("typeId");
        String srCategoryId = (String) context.get("srCategoryId");
        String srSubCategory = (String) context.get("srSubArea");
        String subCategoryId = (String) context.get("custRequestCategoryId");
        String sequenceNumber = (String) context.get("sequenceNumber");
        String status = (String) context.get("statusId");
        
        String isPerformUserAudit = (String) context.get("isPerformUserAudit");
        String userAuditRequestId = (String) context.get("userAuditRequestId");
        String oldContextMap = (String) context.get("oldContextMap");
        
        try {
        	
        	String desc = "";
            if(UtilValidate.isNotEmpty(typeId)) {
            	GenericValue srTypeDesc = EntityUtil.getFirst(delegator.findByAnd("CustRequestAssoc",
                        UtilMisc.toMap("code", typeId,"type",AdminPortalConstant.ParamUnitConstant.SR_TYPE), null, false));
                        desc = srTypeDesc.getString("value");
                        Debug.log("==desc===" +desc);
            }
            
            String categoryDesc = "";
            if(UtilValidate.isNotEmpty(srCategoryId)) {
            	GenericValue srCategoryDesc = EntityUtil.getFirst(delegator.findByAnd("CustRequestAssoc",
                        UtilMisc.toMap("code", srCategoryId,"type",AdminPortalConstant.ParamUnitConstant.SR_CATEGORY), null, false));
            	        categoryDesc = srCategoryDesc.getString("value");
                        Debug.log("==categoryDesc===" +categoryDesc);
            }
        	
            GenericValue updateSrSubCategoryDetails = EntityUtil.getFirst(delegator.findByAnd("CustRequestCategory",
                UtilMisc.toMap("custRequestCategoryId", subCategoryId), null, false));
            if (UtilValidate.isNotEmpty(updateSrSubCategoryDetails)) {
            	
            	if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
            		Map<String, Object> callCtxt = FastMap.newInstance();
        			Map<String, Object> callResult = FastMap.newInstance();
        			
        			callCtxt.put("serviceRequestType", "UPDATE_SR_SUB_CAT");
        			callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
        			callCtxt.put("modeOfAction", ModeOfAction.UPDATE);
        			callCtxt.put("remarks", null);
        			callCtxt.put("contextMap", context);
        			callCtxt.put("userAuditRequestId", userAuditRequestId);
        			callCtxt.put("oldContextMap", oldContextMap);
        			
        			callCtxt.put("userLogin", userLogin);
        			
        			callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
        			return callResult;
            	}
            	
            	GenericValue updateSrSubCategoryRecords = EntityUtil.getFirst(delegator.findByAnd("CustRequestCategory",UtilMisc.toMap("description", srSubCategory,"parentCustRequestCategoryId",srCategoryId,"custRequestTypeId",typeId), null, false));
            	if (UtilValidate.isNotEmpty(updateSrSubCategoryRecords)) {
            			String categoryIdNew = updateSrSubCategoryRecords.getString("custRequestCategoryId");
            			if (categoryIdNew.equalsIgnoreCase(subCategoryId))
            				
            			{
            				if(UtilValidate.isNotEmpty(sequenceNumber)) {
                				updateSrSubCategoryRecords.put("seqNum",DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0 );
            				}
            				updateSrSubCategoryRecords.store();
                	
            				GenericValue updateSrSubRecords = EntityQuery.use(delegator).from("CustRequestAssoc").where( "code", subCategoryId).queryOne();	
        					if (UtilValidate.isNotEmpty(sequenceNumber)) {
			                	 updateSrSubRecords.put("sequenceNumber", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
        					}
			                	 updateSrSubRecords.put("active", status);
			                	 updateSrSubRecords.put("modifiedOn", UtilDateTime.nowTimestamp());
			                     updateSrSubRecords.put("modifiedBy", userLogin.getString("userLoginId"));
			                	 updateSrSubRecords.store();
			                	 results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SRSubCategoryUpdatedSuccessfully", locale));
			                    Debug.log("==updateSrSubRecords==" +updateSrSubRecords);
        					}
            			}
            			else 
            			{
            				return ServiceUtil.returnError("SR Sub Category already exists");
            			}
            			
                  }else {
                  updateSrSubCategoryDetails.put("description",srSubCategory);
                  updateSrSubCategoryDetails.put("parentCustRequestCategoryId",srCategoryId);
                  updateSrSubCategoryDetails.put("custRequestTypeId",typeId);
                  if (UtilValidate.isNotEmpty(sequenceNumber)) {
                	  updateSrSubCategoryDetails.put("seqNum", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0 );
                  }
                  updateSrSubCategoryDetails.store();
                  	
	                  	GenericValue updateSrSubRecords = EntityQuery.use(delegator).from("CustRequestAssoc").where( "code", subCategoryId).queryOne();	
	                  	if (UtilValidate.isNotEmpty(updateSrSubRecords)) {
	                  		updateSrSubRecords.put("value",srSubCategory );
	                  		updateSrSubRecords.put("parentValue",categoryDesc );
	                  		updateSrSubRecords.put("parentCode",srCategoryId );
	                  		updateSrSubRecords.put("grandparentValue",desc );
	                  		updateSrSubRecords.put("grandparentCode",typeId );
	                  		if (UtilValidate.isNotEmpty(sequenceNumber)) {
	                  			updateSrSubRecords.put("sequenceNumber", DataUtil.isInteger(sequenceNumber) ? Integer.parseInt(sequenceNumber) : 0);
	                  		}
	                  		updateSrSubRecords.put("active", status);
	                  		updateSrSubRecords.put("modifiedOn", UtilDateTime.nowTimestamp());
	                  		updateSrSubRecords.put("modifiedBy", userLogin.getString("userLoginId"));
	                  		updateSrSubRecords.store();
	                  		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SRSubCategoryUpdatedSuccessfully", locale));
	                  		Debug.log("==updateSrSubRecords==" +updateSrSubRecords);
	                  	} 
                  }
            results.put("custRequestCategoryId",subCategoryId);	
        } catch (GeneralException e) {
            Debug.log("==error in updation===" + e.getMessage());
        }
        return results;
    }
	
}