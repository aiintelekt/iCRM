package org.fio.admin.portal.setup.paramUnit;

import java.util.Locale;
import java.util.Map;

import org.fio.homeapps.constants.GlobalConstants.ModeOfAction;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/***
 * 
 * @author Arshiya
 * @author Sharif
 *
 */
public class SlaSetupServices {
    private SlaSetupServices() {}
    private static final String MODULE = SlaSetupServices.class.getName();
    public static final String RESOURCE = "AdminPortalUiLabels";
    
    public static Map<String, Object> createSlaSetup(DispatchContext dctx, Map<String, Object> context) {
    	
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> results = ServiceUtil.returnSuccess();
        String srType = (String) context.get("srTypeId");
        String srCategory = UtilValidate.isNotEmpty(context.get("srCategoryId")) ? (String) context.get("srCategoryId") : "NA";
        String srSubCategory =  UtilValidate.isNotEmpty(context.get("srSubCategoryId")) ? (String) context.get("srSubCategoryId") : "NA";
        String srPriority = (String) context.get("srPriority");
        String status = (String) context.get("status");
        String slaSrResolution = (String) context.get("slaSrResolution");
        String srResolutionUnit = (String) context.get("srResolutionUnit");
        String slaEscalation1 = (String) context.get("slaEscalation1");
        String slaEscalation2 = (String) context.get("slaEscalation2");
        String slaEscalation3 = (String) context.get("slaEscalation3");
        String escalationUnit1 = (String) context.get("escalationUnit1");
        String escalationUnit2 = (String) context.get("escalationUnit2");
        String escalationUnit3 = (String) context.get("escalationUnit3");
        String slaConfigId = (String) context.get("slaConfigId");
        String isSlaRequired = (String) context.get("isSlaRequired");
        
        String slaPreEscalation = UtilValidate.isNotEmpty(context.get("slaPreEscalation")) ? (String) context.get("slaPreEscalation") : null;
        String preEscalationUnit = UtilValidate.isNotEmpty(context.get("preEscalationUnit")) ? (String) context.get("preEscalationUnit") : null;
        
        String isPerformUserAudit = (String) context.get("isPerformUserAudit");
        String userAuditRequestId = (String) context.get("userAuditRequestId");
        
        try {
        	
            GenericValue slaTripletSetup = null;
          //Since these fields are Pk
			if (UtilValidate.isNotEmpty(srType) && UtilValidate.isNotEmpty(srCategory)
					&& UtilValidate.isNotEmpty(srSubCategory) /* && UtilValidate.isNotEmpty(srPriority) */) {
            	
            	/*if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
            		Map<String, Object> callCtxt = FastMap.newInstance();
        			Map<String, Object> callResult = FastMap.newInstance();
        			
        			callCtxt.put("serviceRequestType", "CREATE_SLA_SETUP");
        			callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
        			callCtxt.put("modeOfAction", ModeOfAction.CREATE);
        			callCtxt.put("remarks", null);
        			callCtxt.put("contextMap", context);
        			callCtxt.put("userAuditRequestId", userAuditRequestId);
        			
        			callCtxt.put("userLogin", userLogin);
        			
        			callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
        			return callResult;
            	}*/
				GenericValue srSlaConfig = EntityQuery.use(delegator).from("SrSlaConfig").where("srTypeId", srType,
						"srCategoryId", srCategory, "srSubCategoryId", srSubCategory ,"srPriority",srPriority).queryFirst();
				
                if(UtilValidate.isEmpty(srSlaConfig)) {
                	
                	if (UtilValidate.isNotEmpty(isSlaRequired) && isSlaRequired.equals("N")) {
                    	slaSrResolution = null;
                    	srResolutionUnit = null;
                    	slaEscalation1 = null;
                    	slaEscalation2 = null;
                    	slaEscalation3 = null;
                    	escalationUnit1 = null;
                    	escalationUnit2 = null;
                    	escalationUnit3 = null;
                    }
                	
                    slaConfigId = delegator.getNextSeqId("SrSlaConfig");                   
                    slaTripletSetup = delegator.makeValue("SrSlaConfig");
                    slaTripletSetup.set("slaConfigId",slaConfigId);
                    slaTripletSetup.set("srTypeId",srType);
                    slaTripletSetup.set("srCategoryId",srCategory);
                    slaTripletSetup.set("srSubCategoryId",srSubCategory);
                    slaTripletSetup.set("srPriority",srPriority);
                    slaTripletSetup.set("status",status);
                    slaTripletSetup.set("slaPeriodLvl",slaSrResolution);
                    slaTripletSetup.set("srPeriodUnit",srResolutionUnit);
                    slaTripletSetup.set("slaPeriodLvl1",slaEscalation1);
                    slaTripletSetup.set("slaPeriodLvl2",slaEscalation2);
                    slaTripletSetup.set("slaPeriodLvl3",slaEscalation3);
                    if(UtilValidate.isNotEmpty(escalationUnit1)) {
                        slaTripletSetup.set("slaEscPeriodHrsLvl1",Integer.valueOf(escalationUnit1));
                    }
                    if(UtilValidate.isNotEmpty(escalationUnit2)) {
                        slaTripletSetup.set("slaEscPeriodHrsLvl2",Integer.valueOf(escalationUnit2));
                    }
                    if(UtilValidate.isNotEmpty(escalationUnit3)) {
                        slaTripletSetup.set("slaEscPeriodHrsLvl3",Integer.valueOf(escalationUnit3));
                    }
                    slaTripletSetup.set("slaPreEscLvl",slaPreEscalation);
                    slaTripletSetup.set("slaPreEscPeriod",preEscalationUnit);
                    slaTripletSetup.set("createdDate",UtilDateTime.nowTimestamp());
                    slaTripletSetup.set("createdBy",userLogin.getString("userLoginId"));
                    slaTripletSetup.set("isSlaRequired",isSlaRequired);
                
                    slaTripletSetup.create();
                
                    results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SlaSetupCreatedSuccessfully", locale));
                }else {
                     results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "Sla Setup Already Exists", locale));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            results = ServiceUtil.returnError("Error : "+e.getMessage());
        }
        results.put("slaConfigId", slaConfigId);
        return results;
    }
    
    public static Map<String, Object> updateSlaSetup(DispatchContext dctx, Map<String, Object> context) {
    	
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> results = ServiceUtil.returnSuccess();
        String srType = (String) context.get("srTypeId");
        String srCategory = UtilValidate.isNotEmpty(context.get("srCategoryId")) ? (String) context.get("srCategoryId") : "NA";
        String srSubCategory =  UtilValidate.isNotEmpty(context.get("srSubCategoryId")) ? (String) context.get("srSubCategoryId") : "NA";
        String srPriority = (String) context.get("srPriority");
        String status = (String) context.get("status");
        String slaSrResolution = (String) context.get("slaSrResolution");
        String srResolutionUnit = (String) context.get("srResolutionUnit");
        String slaEscalation1 = (String) context.get("slaEscalation1");
        String slaEscalation2 = (String) context.get("slaEscalation2");
        String slaEscalation3 = (String) context.get("slaEscalation3");
        String escalationUnit1 = (String) context.get("escalationUnit1");
        String escalationUnit2 = (String) context.get("escalationUnit2");
        String escalationUnit3 = (String) context.get("escalationUnit3");
        String slaConfigId = (String) context.get("slaConfigId");
        String isSlaRequired = (String) context.get("isSlaRequired");
        
        String slaPreEscalation = UtilValidate.isNotEmpty(context.get("slaPreEscalation")) ? (String) context.get("slaPreEscalation") : null;
        String preEscalationUnit = UtilValidate.isNotEmpty(context.get("preEscalationUnit")) ? (String) context.get("preEscalationUnit") : null;
        
        String isPerformUserAudit = (String) context.get("isPerformUserAudit");
        String userAuditRequestId = (String) context.get("userAuditRequestId");
        String oldContextMap = (String) context.get("oldContextMap");
        
        try {
        	
            GenericValue slaTripletSetup = null;
            
            if(UtilValidate.isNotEmpty(status)) {	
            	GenericValue srSlaConfig = EntityUtil.getFirst(delegator.findByAnd("SrSlaConfig", UtilMisc.toMap("slaConfigId",slaConfigId), null, false));
            	if(UtilValidate.isNotEmpty(srSlaConfig)) {
                	
                	/*if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
                		Map<String, Object> callCtxt = FastMap.newInstance();
            			Map<String, Object> callResult = FastMap.newInstance();
            			
            			callCtxt.put("serviceRequestType", "UPDATE_SLA_SETUP");
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
                	
                	if (UtilValidate.isNotEmpty(isSlaRequired) && isSlaRequired.equals("N")) {
                    	slaSrResolution = null;
                    	srResolutionUnit = null;
                    	slaEscalation1 = null;
                    	slaEscalation2 = null;
                    	slaEscalation3 = null;
                    	escalationUnit1 = null;
                    	escalationUnit2 = null;
                    	escalationUnit3 = null;
                    }
                	 
                    srSlaConfig.set("srCategoryId",srCategory);
                    srSlaConfig.set("srSubCategoryId",srSubCategory);
                    srSlaConfig.set("srPriority",srPriority);
                    srSlaConfig.set("status",status);
                    srSlaConfig.set("slaPeriodLvl",slaSrResolution);
                    srSlaConfig.set("srPeriodUnit",srResolutionUnit);
                    srSlaConfig.set("slaPeriodLvl1",slaEscalation1);
                    srSlaConfig.set("slaPeriodLvl2",slaEscalation2);
                    srSlaConfig.set("slaPeriodLvl3",slaEscalation3);
                    if(UtilValidate.isNotEmpty(escalationUnit1)) {
                        srSlaConfig.set("slaEscPeriodHrsLvl1",Integer.valueOf(escalationUnit1));
                    }
                    if(UtilValidate.isNotEmpty(escalationUnit2)) {
                        srSlaConfig.set("slaEscPeriodHrsLvl2",Integer.valueOf(escalationUnit2));
                    }
                    if(UtilValidate.isNotEmpty(escalationUnit3)) {
                        srSlaConfig.set("slaEscPeriodHrsLvl3",Integer.valueOf(escalationUnit3));
                    }
                    srSlaConfig.set("slaPreEscLvl",slaPreEscalation);
                    srSlaConfig.set("slaPreEscPeriod",preEscalationUnit);
                    
                    srSlaConfig.set("modifiedDate",UtilDateTime.nowTimestamp());
                    srSlaConfig.set("modifiedBy",userLogin.getString("userLoginId"));
                    
                    srSlaConfig.set("isSlaRequired", isSlaRequired);
                    
                    srSlaConfig.store();
                    
                    results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SlaSetupUpdatedSuccessfully", locale));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            results = ServiceUtil.returnError("Error : "+e.getMessage());
        }
        results.put("slaConfigId", slaConfigId);
        return results;
    }
}
