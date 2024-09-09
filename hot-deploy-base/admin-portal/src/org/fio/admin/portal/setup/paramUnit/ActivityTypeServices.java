package org.fio.admin.portal.setup.paramUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.homeapps.constants.GlobalConstants.ModeOfAction;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Golda
 * @author Sharif
 *
 */
public class ActivityTypeServices {

	public static final String MODULE = ActivityTypeServices.class.getName();
	public static final String RESOURCE = "AdminPortalUiLabels";
	
	public static Map < String, Object > createParentActivityType(DispatchContext dctx, Map < String, Object > context) {
	    Debug.logInfo("------inside createActivityParent------" + context, MODULE);
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    Security security = dctx.getSecurity();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Locale locale = (Locale) context.get("locale");
	    Map < String, Object > results = ServiceUtil.returnSuccess();
	
	    String activityParent = (String) context.get("activityParent");
	    String sequenceId = (String) context.get("sequenceNumber");
	    String enumTypeId = AdminPortalConstant.ParamUnitConstant.IA_TYPE;
	    String entityName = AdminPortalConstant.ParamUnitConstant.Activity;
	    String type = AdminPortalConstant.ParamUnitConstant.RELATED_TO;
	    String status = (String) context.get("status");
	    
	    String isPerformUserAudit = (String) context.get("isPerformUserAudit");
	    String userAuditRequestId = (String) context.get("userAuditRequestId");
	    
	    try {
	        GenericValue parentActivityType = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
	            UtilMisc.toMap("description", activityParent,"enumTypeId",AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false));
	        GenericValue addActivityRecords = delegator.makeValue("Enumeration");
	        if (UtilValidate.isEmpty(parentActivityType)) {
	        	
	        	if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
	        		Map<String, Object> callCtxt = FastMap.newInstance();
	    			Map<String, Object> callResult = FastMap.newInstance();
	    			
	    			callCtxt.put("serviceRequestType", "CREATE_ACTV_PRNT");
	    			callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
	    			callCtxt.put("modeOfAction", ModeOfAction.CREATE);
	    			callCtxt.put("remarks", null);
	    			callCtxt.put("contextMap", context);
	    			callCtxt.put("userAuditRequestId", userAuditRequestId);
	    			
	    			callCtxt.put("userLogin", userLogin);
	    			
	    			callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
	    			return callResult;
	        	}
	        	
	            String enumId = delegator.getNextSeqId("Enumeration");
	            addActivityRecords.put("enumId", enumId);
	            addActivityRecords.put("enumTypeId", enumTypeId);
	            addActivityRecords.put("enumCode", enumId);
	            addActivityRecords.put("description", activityParent);
	            addActivityRecords.put("sequenceId", sequenceId);
	            
	            addActivityRecords.create();
	
	            GenericValue addActivitySubRecords = delegator.makeValue("WorkEffortAssocTriplet");
	            addActivitySubRecords.put("uniqueRefNumber", enumId);
	            addActivitySubRecords.put("entityName", entityName);
	            addActivitySubRecords.put("type", type);
	            addActivitySubRecords.put("code", enumId);
	            addActivitySubRecords.put("value", activityParent);
	            addActivitySubRecords.put("active", status);
	            addActivitySubRecords.put("sequenceNumber", sequenceId);
	            addActivitySubRecords.put("createdOn", UtilDateTime.nowTimestamp());
	            addActivitySubRecords.put("createdBy", userLogin.getString("userLoginId"));
	            addActivitySubRecords.create();
	            results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ActivityParentSuccessfullyCreated", locale));
	            results.put("enumId", enumId);
	        } else {
	            return ServiceUtil.returnError("Activity Parent already exists");
	        }
	
	    } catch (GeneralException e) {
	        Debug.log("==error in creations===" + e.getMessage());
	    }
	
	    return results;
	
	}
	
	public static Map < String, Object >updateParentActivityType(DispatchContext dctx, Map < String, Object > context) {
	    Debug.logInfo("------inside update Parent Activity Type------" + context, MODULE);
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    Security security = dctx.getSecurity();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Locale locale = (Locale) context.get("locale");
	    Map < String, Object > results = ServiceUtil.returnSuccess();
	
	    String activityParent = (String) context.get("activityParent");
	    String activityParentId = (String) context.get("activityParentId");
	    String sequenceId = (String) context.get("sequenceNumber");
	    String status = (String) context.get("status");
	    
	    String isPerformUserAudit = (String) context.get("isPerformUserAudit");
	    String userAuditRequestId = (String) context.get("userAuditRequestId");
	    String oldContextMap = (String) context.get("oldContextMap");
	   
	    try {
	        GenericValue parentActivityType = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
	            UtilMisc.toMap("enumId", activityParentId,"enumTypeId",AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false));
	        if (UtilValidate.isNotEmpty(parentActivityType)) {
	        	if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
	        		Map<String, Object> callCtxt = FastMap.newInstance();
	    			Map<String, Object> callResult = FastMap.newInstance();
	    			
	    			callCtxt.put("serviceRequestType", "UPDATE_ACTV_PRNT");
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
    				String sr = parentActivityType.getString("description");
    				if(UtilValidate.isNotEmpty(sr) && UtilValidate.isNotEmpty(activityParent))
    				{	
    					if(activityParent.equalsIgnoreCase(sr))
    					{
    						//	parentActivityType.put("description", activityParent);
					        	parentActivityType.put("sequenceId", sequenceId);
					        	parentActivityType.store();
					            GenericValue UpdateActivitySubRecords = EntityQuery.use(delegator).from("WorkEffortAssocTriplet")
					                    .where("code", activityParentId).queryOne();
					            if (UtilValidate.isNotEmpty(UpdateActivitySubRecords)) {
					            	UpdateActivitySubRecords.put("value", activityParent);
					            	UpdateActivitySubRecords.put("sequenceNumber", sequenceId);
					            	UpdateActivitySubRecords.put("active", status);
					            	UpdateActivitySubRecords.put("modifiedOn", UtilDateTime.nowTimestamp());
					            	UpdateActivitySubRecords.put("modifiedBy", userLogin.getString("userLoginId"));
					            	UpdateActivitySubRecords.store();
					            } 
    					}else {
	    						GenericValue parentDescType = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
	            		            UtilMisc.toMap("description", activityParent,"enumTypeId",AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false));
	    						if (UtilValidate.isNotEmpty(parentDescType)) {
	    							return ServiceUtil.returnError("Activity parent already exists");
	    						}else {
						        		parentActivityType.put("description", activityParent);
						        		parentActivityType.put("sequenceId", sequenceId);
						        		parentActivityType.store();
						        		GenericValue UpdateActivitySubRecords = EntityQuery.use(delegator).from("WorkEffortAssocTriplet")
						                    .where("code", activityParentId).queryOne();
						        		if (UtilValidate.isNotEmpty(UpdateActivitySubRecords)) {
						        			UpdateActivitySubRecords.put("value", activityParent);
						        			UpdateActivitySubRecords.put("sequenceNumber", sequenceId);
						        			UpdateActivitySubRecords.put("active", status);
						        			UpdateActivitySubRecords.put("modifiedOn", UtilDateTime.nowTimestamp());
						        			UpdateActivitySubRecords.put("modifiedBy", userLogin.getString("userLoginId"));
						        			UpdateActivitySubRecords.store();
						        		}
	    						}
    					}
    					// results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ActivityParentSuccessfullyUpdated", locale));
    					//results.put("enumId", activityParentId);
    					//results.put("activityParentId", activityParentId);
    				} 
	        	}
	        	results.put("enumId", activityParentId);
	    	} catch (GeneralException e) {
	        Debug.log("==error in creations===" + e.getMessage());
	    	}
	    	return results;
	}
	public static Map < String, Object >createActivityType(DispatchContext dctx, Map < String, Object > context) {
		Debug.logInfo("------inside createActivityTypes------" + context, MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map < String, Object > results = ServiceUtil.returnSuccess();

		String activityParent = (String) context.get("activityParent");
		String activityType = (String) context.get("activityType");
		String sequenceId = (String) context.get("sequenceNumber");
		String enumTypeId = AdminPortalConstant.ParamUnitConstant.IA_TYPE;
		String entityName = AdminPortalConstant.ParamUnitConstant.Activity;
		String type = AdminPortalConstant.ParamUnitConstant.TYPE;
		String status = (String) context.get("status");

		String isPerformUserAudit = (String) context.get("isPerformUserAudit");
		String userAuditRequestId = (String) context.get("userAuditRequestId");

		try {
		String desc = "";
		if(UtilValidate.isNotEmpty(activityParent)) {
		GenericValue activityParentDesc = EntityUtil.getFirst(delegator.findByAnd("WorkEffortAssocTriplet",
		UtilMisc.toMap("code", activityParent,"type",AdminPortalConstant.ParamUnitConstant.RELATED_TO), null, false));
		desc = activityParentDesc.getString("value");
		//Debug.log("==desc===" +desc);
		}
		GenericValue parentActivityType = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
		UtilMisc.toMap("description", activityType,"parentEnumId",activityParent,"enumTypeId",AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false));
		GenericValue addActivityRecords = delegator.makeValue("Enumeration");
		if (UtilValidate.isEmpty(parentActivityType)) {

		if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		callCtxt.put("serviceRequestType", "CREATE_ACTV_TYPE");
		callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
		callCtxt.put("modeOfAction", ModeOfAction.CREATE);
		callCtxt.put("remarks", null);
		callCtxt.put("contextMap", context);
		callCtxt.put("userAuditRequestId", userAuditRequestId);

		callCtxt.put("userLogin", userLogin);

		callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
		return callResult;
		}

		String enumId = delegator.getNextSeqId("Enumeration");
		addActivityRecords.put("enumId", enumId);
		addActivityRecords.put("parentEnumId", activityParent);
		addActivityRecords.put("enumTypeId", enumTypeId);
		addActivityRecords.put("enumCode", enumId);
		addActivityRecords.put("description", activityType);
		addActivityRecords.put("sequenceId", sequenceId);

		addActivityRecords.create();

		GenericValue addActivitySubRecords = delegator.makeValue("WorkEffortAssocTriplet");
		addActivitySubRecords.put("uniqueRefNumber", enumId);
		addActivitySubRecords.put("entityName", entityName);
		addActivitySubRecords.put("type", type);
		addActivitySubRecords.put("code", enumId);
		addActivitySubRecords.put("value", activityType);
		addActivitySubRecords.put("parentCode", activityParent);
		addActivitySubRecords.put("parentValue", desc);
		addActivitySubRecords.put("active", status);
		addActivitySubRecords.put("sequenceNumber", sequenceId);
		addActivitySubRecords.put("createdOn", UtilDateTime.nowTimestamp());
		addActivitySubRecords.put("createdBy", userLogin.getString("userLoginId"));
		addActivitySubRecords.create();
		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ActivityTypeSuccessfullyCreated", locale));
		results.put("enumId", enumId);
		} else {
		return ServiceUtil.returnError(" Activity Type already exists");
		}

		} catch (GeneralException e) {
		Debug.log("==error in creations===" + e.getMessage());
		}

		return results;

		}
	public static Map < String, Object >updateActivityType(DispatchContext dctx, Map < String, Object > context) {
		Debug.logInfo("------inside update Activity Type------" + context, MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map < String, Object > results = ServiceUtil.returnSuccess();

		String activityParent = (String) context.get("activityParent");
		String activityType = (String) context.get("activityType");
		String activityTypeId = (String) context.get("activityTypeId");
		String sequenceId = (String) context.get("sequenceNumber");
		String status = (String) context.get("status");

		String isPerformUserAudit = (String) context.get("isPerformUserAudit");
		String userAuditRequestId = (String) context.get("userAuditRequestId");
		String oldContextMap = (String) context.get("oldContextMap");

		try {
		String desc = "";
		if(UtilValidate.isNotEmpty(activityParent)) {
		GenericValue activityParentDesc = EntityUtil.getFirst(delegator.findByAnd("WorkEffortAssocTriplet",
		UtilMisc.toMap("code", activityParent,"type",AdminPortalConstant.ParamUnitConstant.RELATED_TO), null, false));
		desc = activityParentDesc.getString("value");
		//Debug.log("==desc===" +desc);
		}

		GenericValue parentActivityType = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
		UtilMisc.toMap("enumId", activityTypeId,"enumTypeId",AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false));

		if (UtilValidate.isNotEmpty(parentActivityType)) {

		if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		callCtxt.put("serviceRequestType", "UPDATE_ACTV_TYPE");
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



		GenericValue updateActivityRecords = EntityUtil.getFirst(delegator.findByAnd("Enumeration",UtilMisc.toMap("description", activityType,"parentEnumId",activityParent), null, false));

		//Debug.log("==updateActivityRecords===" +updateActivityRecords);
		if (UtilValidate.isNotEmpty(updateActivityRecords))
		{

		//Debug.log("inside activity");
		String typeIdNew = updateActivityRecords.getString("enumId");
		if (activityTypeId.equalsIgnoreCase(typeIdNew))
		{

		//parentActivityType.put("description", activityType);
		//parentActivityType.put("parentEnumId", activityParent);
		updateActivityRecords.put("sequenceId", sequenceId);

		updateActivityRecords.store();

		GenericValue UpdateActivitySubRecords = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("code", activityTypeId).queryOne();
		if (UtilValidate.isNotEmpty(UpdateActivitySubRecords)) {
		//UpdateActivitySubRecords.put("value", activityType);
		//UpdateActivitySubRecords.put("parentValue", desc);
		//UpdateActivitySubRecords.put("parentCode", activityParent);
		UpdateActivitySubRecords.put("sequenceNumber", sequenceId);
		UpdateActivitySubRecords.put("active", status);
		UpdateActivitySubRecords.put("modifiedOn", UtilDateTime.nowTimestamp());
		UpdateActivitySubRecords.put("modifiedBy", userLogin.getString("userLoginId"));
		UpdateActivitySubRecords.store();
		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ActivityTypeSuccessfullyUpdated", locale));

		} 
		}else {
		return ServiceUtil.returnError("Activity Type already exists");
		}

		} else {
		//Debug.log("else inside activity");
		parentActivityType.put("description", activityType);
		parentActivityType.put("parentEnumId", activityParent);
		parentActivityType.put("sequenceId", sequenceId);

		parentActivityType.store();

		GenericValue UpdateActivitySubRecords = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("code", activityTypeId).queryOne();
		if (UtilValidate.isNotEmpty(UpdateActivitySubRecords)) {
		UpdateActivitySubRecords.put("value", activityType);
		UpdateActivitySubRecords.put("parentValue", desc);
		UpdateActivitySubRecords.put("parentCode", activityParent);
		UpdateActivitySubRecords.put("sequenceNumber", sequenceId);
		UpdateActivitySubRecords.put("active", status);
		UpdateActivitySubRecords.put("modifiedOn", UtilDateTime.nowTimestamp());
		UpdateActivitySubRecords.put("modifiedBy", userLogin.getString("userLoginId"));
		UpdateActivitySubRecords.store();
		results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ActivityTypeSuccessfullyUpdated", locale));

		} 
		}
		}

		results.put("enumId", activityTypeId);

		} catch (GeneralException e) {
		Debug.log("==error in creations===" + e.getMessage());
		}

		return results;

		}
	public static Map < String, Object >createActivitySubType(DispatchContext dctx, Map < String, Object > context) {
	    Debug.logInfo("------inside createActivityTypes------" + context, MODULE);
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    Security security = dctx.getSecurity();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Locale locale = (Locale) context.get("locale");
	    Map < String, Object > results = ServiceUtil.returnSuccess();
	    
	    String activityParent = (String) context.get("activityParent");
	    String activityType = (String) context.get("activityType");
		/*
		 * String activityTypeId = activityTypes.substring(0,
		 * activityTypes.indexOf("("));
		 * //Debug.log("==activityTypeId=="+activityTypeId); String activityType =
		 * activityTypes.substring(activityTypes.indexOf("(")+1,
		 * activityTypes.indexOf(")"));
		 */
	    String activitySubType = (String) context.get("activitySubType");
	    String sequenceId = (String) context.get("sequenceNumber");
	    String enumTypeId = AdminPortalConstant.ParamUnitConstant.IA_TYPE;
	    String entityName = AdminPortalConstant.ParamUnitConstant.Activity;
	    String type = AdminPortalConstant.ParamUnitConstant.SUB_TYPE;
	    String status = (String) context.get("status");
		 List < String > parentNames = null;
		 List < String > childNames = null;
		String isPerformUserAudit = (String) context.get("isPerformUserAudit");
	    String userAuditRequestId = (String) context.get("userAuditRequestId");
	    String typeIdExists = "false";
	    String subTypeExists = "false";
	    String subTypeLis = "";
	    String typeLis="";
    	String subtypeList = "";
	    try {
	    	String desc = "";
	    	String parentdesc = "";
	        if(UtilValidate.isNotEmpty(activityType)) {
	        	GenericValue activityParentDesc = EntityUtil.getFirst(delegator.findByAnd("WorkEffortAssocTriplet",
	                    UtilMisc.toMap("code", activityType,"type",AdminPortalConstant.ParamUnitConstant.TYPE), null, false));
	                    desc = activityParentDesc.getString("value");
	                    //Debug.log("==desc===" +desc);
	        }
	        if(UtilValidate.isNotEmpty(activityParent)) {
	        	GenericValue activityParentsDesc = EntityUtil.getFirst(delegator.findByAnd("WorkEffortAssocTriplet",
	                    UtilMisc.toMap("code", activityParent,"type",AdminPortalConstant.ParamUnitConstant.RELATED_TO), null, false));
	        	        parentdesc = activityParentsDesc.getString("value");
	                    //Debug.log("==parentdesc===" +parentdesc);
	        }
	/*        GenericValue activitySubTypeDetails = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
	            UtilMisc.toMap("description", activitySubType,"enumTypeId",AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false));
	        GenericValue addActivityRecords = delegator.makeValue("Enumeration");*/
	       
	        	
	        	if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
	        		Map<String, Object> callCtxt = FastMap.newInstance();
	    			Map<String, Object> callResult = FastMap.newInstance();
	    			
	    			callCtxt.put("serviceRequestType", "CREATE_ACTV_SUB_TYPE");
	    			callCtxt.put("makerPartyId", userLogin.getString("userLoginId"));
	    			callCtxt.put("modeOfAction", ModeOfAction.CREATE);
	    			callCtxt.put("remarks", null);
	    			callCtxt.put("contextMap", context);
	    			callCtxt.put("userAuditRequestId", userAuditRequestId);
	    			
	    			callCtxt.put("userLogin", userLogin);
	    			
	    			callResult = dispatcher.runSync("homeapps.createUserAuditRequest", callCtxt);
	    			return callResult;
	        	}
	        	List < GenericValue > typeDetails = EntityQuery.use(delegator).from("Enumeration").where("parentEnumId", activityParent,"enumTypeId",AdminPortalConstant.ParamUnitConstant.IA_TYPE).queryList();
	    		if (typeDetails != null) {
	    			parentNames = new ArrayList<String>();
	    			 for(GenericValue typ : typeDetails)
            		 {
	    				 typeLis = typ.getString("enumId");
	    				 parentNames.add(typeLis);
            		 }
	    			 for(String subtyp : parentNames)
            		 {
	    				 subTypeLis = subtyp;
	    				 if(subTypeLis.equalsIgnoreCase(activityType)) {
	    					 typeIdExists ="true";
	    					 break;
	    				 }
            		 }		
	    		}
	    		List < GenericValue > subtypeDetails = EntityQuery.use(delegator).from("Enumeration").where("parentEnumId", subTypeLis,"enumTypeId",AdminPortalConstant.ParamUnitConstant.IA_TYPE).queryList();
	    				if (subtypeDetails != null) {
	    					childNames = new ArrayList<String>();
	   	    			 for(GenericValue styp : subtypeDetails)
	               		 {
	   	    				subtypeList = styp.getString("description");
	   	    				childNames.add(subtypeList);
	               		 }
	   	    			 String subTypedesc = "";
		    			 for(String subtyps : childNames)
	            		 {
		    				 subTypedesc = subtyps;
		    				 if(subTypedesc.equalsIgnoreCase(activitySubType)) {
		    					 subTypeExists="true";
		    					 break;
		    				 }
	            		 }		
	    		}
	    		if(subTypeExists=="false") {
    				GenericValue addActivityRecords = delegator.makeValue("Enumeration");
    				String enumId = delegator.getNextSeqId("Enumeration");
    	            addActivityRecords.put("enumId", enumId);
    	            addActivityRecords.put("parentEnumId", activityType);
    	            addActivityRecords.put("enumTypeId", enumTypeId);
    	            addActivityRecords.put("enumCode", enumId);
    	            addActivityRecords.put("description", activitySubType);
    	            addActivityRecords.put("sequenceId", sequenceId);
    	            
    	            addActivityRecords.create();
    	            
    	            GenericValue addActivitySubRecords = delegator.makeValue("WorkEffortAssocTriplet");
    	            addActivitySubRecords.put("uniqueRefNumber", enumId);
    	            addActivitySubRecords.put("entityName", entityName);
    	            addActivitySubRecords.put("type", type);
    	            addActivitySubRecords.put("code", enumId);
    	            addActivitySubRecords.put("value", activitySubType);
    	            addActivitySubRecords.put("parentCode", activityType);
    	            addActivitySubRecords.put("parentValue", desc);
    	            addActivitySubRecords.put("grandparentCode", activityParent);
    	            addActivitySubRecords.put("grandparentValue", parentdesc);
    	            addActivitySubRecords.put("active", status);
    	            addActivitySubRecords.put("sequenceNumber", sequenceId);
    	            addActivitySubRecords.put("createdOn", UtilDateTime.nowTimestamp());
    	            addActivitySubRecords.put("createdBy", userLogin.getString("userLoginId"));
    	            addActivitySubRecords.create();
    	            results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ActivitySubTypeSuccessfullyCreated", locale));
    	            results.put("enumId", enumId);
    				
    			}else if(subTypeExists=="true")
    			{
    				return ServiceUtil.returnError("Activity Sub Type already exists");
    			}
	    } catch (GeneralException e) {
	        Debug.log("==error in creations===" + e.getMessage());
	    }
	    return results;
	}
	
	public static Map < String, Object >updateActivitySubType(DispatchContext dctx, Map < String, Object > context) {
	    Debug.logInfo("------inside Update Activity Sub Type------" + context, MODULE);
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    Security security = dctx.getSecurity();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Locale locale = (Locale) context.get("locale");
	    Map < String, Object > results = ServiceUtil.returnSuccess();
	
	    String activityParent = (String) context.get("activityParent");
	    String activityType = (String) context.get("activityType");
		/*
		 * String activityTypeId = activityTypes.substring(0,
		 * activityTypes.indexOf("(")); String activityType =
		 * activityTypes.substring(activityTypes.indexOf("(")+1,
		 * activityTypes.indexOf(")"));
		 */
	    String activitySubType = (String) context.get("activitySubType");
	    String activitySubTypeId = (String) context.get("activitySubTypeId");
	    String sequenceId = (String) context.get("sequenceNumber");
	    String status = (String) context.get("status");
		
		String isPerformUserAudit = (String) context.get("isPerformUserAudit");
	    String userAuditRequestId = (String) context.get("userAuditRequestId");
	    String oldContextMap = (String) context.get("oldContextMap");
	    
	    List < String > parentNames = null;
		List < String > childNames = null;
		
	    String typeIdExists = "false";
	    String subTypeExists = "false";
	    String subTypeLis = "";
	    String typeLis="";
   	    String subtypeList = "";
		
	    try {
	    	
	    	String desc = "";
	        if(UtilValidate.isNotEmpty(activityType)) {
	        	GenericValue activityParentDesc = EntityUtil.getFirst(delegator.findByAnd("WorkEffortAssocTriplet",
	                    UtilMisc.toMap("code", activityType,"type",AdminPortalConstant.ParamUnitConstant.TYPE), null, false));
	                    desc = activityParentDesc.getString("value");
	                    //Debug.log("==desc===" +desc);
	        }
	        String parentDesc = "";
	        if(UtilValidate.isNotEmpty(activityParent)) {
	        	GenericValue ParentDesc = EntityUtil.getFirst(delegator.findByAnd("WorkEffortAssocTriplet",
	                    UtilMisc.toMap("code", activityParent,"type",AdminPortalConstant.ParamUnitConstant.RELATED_TO), null, false));
	        	parentDesc = ParentDesc.getString("value");
	                    //Debug.log("==desc===" +desc);
	        }
	    	
	        GenericValue parentActivityType = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
	            UtilMisc.toMap("enumId", activitySubTypeId,"enumTypeId",AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false));
	        
	        if (UtilValidate.isNotEmpty(parentActivityType)) {
	        	
	        	if (UtilValidate.isNotEmpty(isPerformUserAudit) && isPerformUserAudit.equals("Y")) {
	        		Map<String, Object> callCtxt = FastMap.newInstance();
	    			Map<String, Object> callResult = FastMap.newInstance();
	    			
	    			callCtxt.put("serviceRequestType", "UPDATE_ACTV_SUB_TYPE");
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
	            
	        	
	        	List < GenericValue > typeDetails = EntityQuery.use(delegator).from("Enumeration").where("parentEnumId", activityParent,"enumTypeId",AdminPortalConstant.ParamUnitConstant.IA_TYPE).queryList();
	    		if (typeDetails != null) {
	    			parentNames = new ArrayList<String>();
	    			 for(GenericValue typ : typeDetails)
            		 {
	    				 typeLis = typ.getString("enumId");
	    				 parentNames.add(typeLis);
            		 }
	    			 for(String subtyp : parentNames)
            		 {
	    				 subTypeLis = subtyp;
	    				 if(subTypeLis.equalsIgnoreCase(activityType)) {
	    					 typeIdExists="true";
	    					 break;
	    				 }
            		 }		
	    		}
	    		List < GenericValue > subtypeDetails = EntityQuery.use(delegator).from("Enumeration").where("parentEnumId", subTypeLis,"enumTypeId",AdminPortalConstant.ParamUnitConstant.IA_TYPE).queryList();
	    				if (subtypeDetails != null) {
	    					childNames = new ArrayList<String>();
	   	    			 for(GenericValue styp : subtypeDetails)
	               		 {
	   	    				subtypeList = styp.getString("description");
	   	    				childNames.add(subtypeList);
	               		 }
	   	    			 String subTypedesc = "";
		    			 for(String subtyps : childNames)
	            		 {
		    				 subTypedesc = subtyps;
		    				 if(subTypedesc.equalsIgnoreCase(activitySubType)) {
		    					 subTypeExists="true";
		    					 break;
		    				 }
	            		 }		
	    		}
	        	
	    	if(subTypeExists=="false") {
	    		parentActivityType.put("description", activitySubType);
	        	parentActivityType.put("parentEnumId", activityType);
	    		parentActivityType.put("sequenceId", sequenceId);
	    		parentActivityType.store();
	    		
	            GenericValue UpdateActivitySubRecords = EntityQuery.use(delegator).from("WorkEffortAssocTriplet")
	                    .where("code", activitySubTypeId).queryOne();
	            if (UtilValidate.isNotEmpty(UpdateActivitySubRecords)) {
	            	UpdateActivitySubRecords.put("value", activitySubType);
	            	UpdateActivitySubRecords.put("parentValue", desc);
	            	UpdateActivitySubRecords.put("parentCode", activityType);
	            	UpdateActivitySubRecords.put("grandparentCode", activityParent);
	            	UpdateActivitySubRecords.put("grandparentValue", parentDesc);
	            	UpdateActivitySubRecords.put("sequenceNumber", sequenceId);
	            	UpdateActivitySubRecords.put("active", status);
	            	UpdateActivitySubRecords.put("modifiedOn", UtilDateTime.nowTimestamp());
	            	UpdateActivitySubRecords.put("modifiedBy", userLogin.getString("userLoginId"));
	            	UpdateActivitySubRecords.store();   
	            	results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ActivitySubTypeSuccessfullyUpdated", locale));
	        	    results.put("enumId", activitySubTypeId);
	        	}
            } 
	       else if(subTypeExists=="true")
			{
	    	   
	    	   GenericValue activityDesc = EntityUtil.getFirst(delegator.findByAnd("WorkEffortAssocTriplet",
	                    UtilMisc.toMap("code", activityType,"active",status,"sequenceNumber",sequenceId), null, false));
	    	     if (UtilValidate.isNotEmpty(activityDesc))
	    	    	 return ServiceUtil.returnError("Activity Sub Type already exists");
	    	     else
	    	     {
	    	    		parentActivityType.put("sequenceId", sequenceId);
	    	    		parentActivityType.store();
	    	    		
	    	    		 GenericValue UpdateActivitySubRecordss = EntityQuery.use(delegator).from("WorkEffortAssocTriplet")
	    		                    .where("code", activitySubTypeId).queryOne();
	    		            if (UtilValidate.isNotEmpty(UpdateActivitySubRecordss)) {
	    		            	
	    		            	UpdateActivitySubRecordss.put("sequenceNumber", sequenceId);
	    		            	UpdateActivitySubRecordss.put("active", status);
	    		            	UpdateActivitySubRecordss.put("modifiedOn", UtilDateTime.nowTimestamp());
	    		            	UpdateActivitySubRecordss.put("modifiedBy", userLogin.getString("userLoginId"));
	    		            	UpdateActivitySubRecordss.store();   
	    		            	results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ActivitySubTypeSuccessfullyUpdated", locale));
	    		        	    results.put("enumId", activitySubTypeId);
	    		            }
	    	     }
			}
        }
} catch (GeneralException e) {
	        Debug.log("==error in creations===" + e.getMessage());
	 }
	
	    return results;
	
	}


}
