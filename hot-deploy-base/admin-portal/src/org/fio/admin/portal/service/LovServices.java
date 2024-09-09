package org.fio.admin.portal.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.QueryUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;

/**
 * @author Sharif
 *
 */
public class LovServices {

	private static final String MODULE = LovServices.class.getName();

	public static Map createLov(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();		
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String lovTypeId = (String) context.get("lovTypeId");
		String name = (String) context.get("name");
		String description = (String) context.get("description");
		String isEnable = (String) context.get("isEnable");
		String sequence = (String) context.get("sequence");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {

			result.put("lovTypeId", lovTypeId);
			
			List<GenericValue> enumList = EntityQuery.use(delegator).from("Enumeration")
					.where("description", description,
							"enumTypeId", lovTypeId).queryList();
			
			if(enumList.size()>0) {
				result.putAll(ServiceUtil.returnError(description+"  already exists as Lov! "));
				return result;
			}
			
			GenericValue lovValue = delegator.makeValue("Enumeration");
			String lovId = delegator.getNextSeqId("Enumeration");
			lovValue.put("enumId", lovId);
			lovValue.put("enumCode", lovId);
			lovValue.put("enumTypeId", lovTypeId);
			lovValue.put("name", name);
			lovValue.put("description", description);
			lovValue.put("isEnabled", isEnable);
			lovValue.put("sequenceId", sequence);
			lovValue.put("sequenceId", sequence);
			lovValue.put("createdByUserLogin", userLogin.get("userLoginId"));
			lovValue.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			lovValue.create();
			result.put("lovId", lovId);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully created LOV.."));
		return result;

	}

	public static Map updateLov(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String lovId = (String) context.get("lovId");
		String lovTypeId = (String) context.get("lovTypeId");
		String name = (String) context.get("name");
		String description = (String) context.get("description");
		String isEnable = (String) context.get("isEnable");
		String sequence = (String) context.get("sequence");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {

			result.put("lovId", lovId);
			result.put("lovTypeId", lovTypeId);
			if(UtilValidate.isNotEmpty(lovId) && UtilValidate.isNotEmpty(UtilValidate.isNotEmpty(lovTypeId))){
				GenericValue  lov = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
						UtilMisc.toMap("enumId", lovId,"enumTypeId",lovTypeId), UtilMisc.toList("sequenceId"),
						false));
				
				List<GenericValue> enumList = EntityQuery.use(delegator).from("Enumeration")
						.where("description", description,
								"enumTypeId", lovTypeId).queryList();
				String currentDescription = lov.getString("description");
				String enumCode = lov.getString("enumCode");
				
				if(enumList.size()>0 && !description.equals(currentDescription)) {
					result.putAll(ServiceUtil.returnError(description+"  already exists as Lov! "));
					return result;
				}
				else {
					if(UtilValidate.isEmpty(enumCode))
						lov.set("enumCode", lovId);
					
					if(UtilValidate.isNotEmpty(lov)) {
						lov.set("name", name);
						lov.set("description", description);
						lov.set("sequenceId", sequence);
						lov.set("isEnabled", isEnable);
						lov.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
						lov.store();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully updated LOV.."));

		return result;

	}
	
	public static Map createOtherLov(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();		
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String lovTypeId = (String) context.get("lovTypeId");
    	String name = (String) context.get("name");
    	String description = (String) context.get("description");
    	String isEnable = (String) context.get("isEnable");
    	String sequence = (String) context.get("sequence");
    	
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		
    		result.put("lovTypeId", lovTypeId);
    		
    		GenericValue lovEntityAssoc = null;
        	if (UtilValidate.isNotEmpty(lovTypeId)) {
        		lovEntityAssoc = delegator.findOne("LovEntityAssoc", UtilMisc.toMap("lovEntityTypeId", lovTypeId), false);
        	}
        	
        	if (UtilValidate.isEmpty(lovEntityAssoc)) {
        		result.putAll(ServiceUtil.returnSuccess("Not found LOV configuration!"));
    			return result;
        	}
        	
        	String entityName = lovEntityAssoc.getString("entityName");
    		String idColumn = lovEntityAssoc.getString("idColumn");
    		String nameColumn = lovEntityAssoc.getString("nameColumn");
    		String descColumn = lovEntityAssoc.getString("descColumn");
    		String sequenceColumn = lovEntityAssoc.getString("sequenceColumn");
    		String enableColumn = lovEntityAssoc.getString("enableColumn");
    		String filterValue = lovEntityAssoc.getString("filterValue");
    		
    		List conditionList = FastList.newInstance();
    		conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD(descColumn), EntityOperator.EQUALS, EntityFunction.UPPER(description)));
    		if (UtilValidate.isNotEmpty(filterValue)) {
				JSONObject filterObj = JSONObject.fromObject(filterValue);
				Map<String, Object> filter = ParamUtil.jsonToMap(filterObj);
				QueryUtil.makeCondition(conditionList, filter);
			}
    		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		
    		GenericValue lov = EntityUtil.getFirst( delegator.findList(entityName, mainConditons, null, null, null, false) );
    		
    		if (UtilValidate.isNotEmpty(lov)) {
    			Debug.log("in lov loop ====="+lov);
    			result.putAll(ServiceUtil.returnError("LOV already exists!"));
    			return result;
    		}
    		
    		String lovId = delegator.getNextSeqId(entityName);
    		lov = delegator.makeValue(entityName);
    		ModelEntity lovModelEntity = delegator.getModelEntity(entityName);
			
    		lov.put(idColumn, lovId);
    		lov.put(nameColumn, name);
    		lov.put(descColumn, description);
    		lov.put(sequenceColumn, lovModelEntity.convertFieldValue(sequenceColumn, sequence, delegator));
    		lov.put(enableColumn, isEnable);
    		lov.put("createdByUserLogin", userLogin.getString("userLoginId"));
    		
    		if (UtilValidate.isNotEmpty(filterValue)) {
				JSONObject filterObj = JSONObject.fromObject(filterValue);
    			Map<String, Object> filter = ParamUtil.jsonToMap(filterObj);
    			lov.putAll(filter);
			}
    		
    		lov.create();
			
			result.put("lovId", lovId);
			
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully created LOV.."));
    	
    	return result;
    	
    }
    
    public static Map updateOtherLov(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String lovId = (String) context.get("lovId");
    	
    	String lovTypeId = (String) context.get("lovTypeId");
    	String name = (String) context.get("name");
    	String description = (String) context.get("description");
    	String isEnable = (String) context.get("isEnable");
    	String sequence = (String) context.get("sequence");
    	
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		
    		result.put("lovId", lovId);
    		result.put("lovTypeId", lovTypeId);
    		
    		GenericValue lovEntityAssoc = null;
        	if (UtilValidate.isNotEmpty(lovTypeId)) {
        		lovEntityAssoc = delegator.findOne("LovEntityAssoc", UtilMisc.toMap("lovEntityTypeId", lovTypeId), false);
        	}
        	
        	if (UtilValidate.isEmpty(lovEntityAssoc)) {
        		result.putAll(ServiceUtil.returnSuccess("Not found LOV configuration!"));
    			return result;
        	}
        	
        	String entityName = lovEntityAssoc.getString("entityName");
    		String idColumn = lovEntityAssoc.getString("idColumn");
    		String nameColumn = lovEntityAssoc.getString("nameColumn");
    		String descColumn = lovEntityAssoc.getString("descColumn");
    		String sequenceColumn = lovEntityAssoc.getString("sequenceColumn");
    		String enableColumn = lovEntityAssoc.getString("enableColumn");
    		String filterValue = lovEntityAssoc.getString("filterValue");
    		
    		List conditionList = FastList.newInstance();
    		conditionList.add(EntityCondition.makeCondition(idColumn, EntityOperator.NOT_EQUAL, lovId));
    		conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD(descColumn), EntityOperator.EQUALS, EntityFunction.UPPER(description)));
    		if (UtilValidate.isNotEmpty(filterValue)) {
				JSONObject filterObj = JSONObject.fromObject(filterValue);
				Map<String, Object> filter = ParamUtil.jsonToMap(filterObj);
				QueryUtil.makeCondition(conditionList, filter);
			}
    		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		GenericValue lov = EntityUtil.getFirst( delegator.findList(entityName, mainConditons, null, null, null, false) );
    		if (UtilValidate.isNotEmpty(lov)) {
    			result.putAll(ServiceUtil.returnSuccess("LOV already exists!"));
    			return result;
    		}
    		
    		conditionList = FastList.newInstance();
    		conditionList.add(EntityCondition.makeCondition(idColumn, EntityOperator.EQUALS, lovId));
    		mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		lov = EntityUtil.getFirst( delegator.findList(entityName, mainConditons, null, null, null, false) );
    		if (UtilValidate.isEmpty(lov)) {
    			result.putAll(ServiceUtil.returnSuccess("LOV not exists!"));
    			return result;
    		}
    		
    		ModelEntity lovModelEntity = delegator.getModelEntity(entityName);
			
    		lov.put(idColumn, lovId);
    		lov.put(nameColumn, name);
    		lov.put(descColumn, description);
    		lov.put(sequenceColumn, lovModelEntity.convertFieldValue(sequenceColumn, sequence, delegator));
    		lov.put(enableColumn, isEnable);
    		lov.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
    		
    		lov.store();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	result.putAll(ServiceUtil.returnSuccess("Successfully updated LOV.."));
    	
    	return result;
    	
    }
public static String createActivityWorkType(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String description = (String) context.get("description");
		Debug.log("description========="+description);
		try {
			
			if(UtilValidate.isNotEmpty(description)){
				String lovId = delegator.getNextSeqId("WorkEffortPurposeType");
				GenericValue workEffortPurposeType = delegator.makeValue("WorkEffortPurposeType");
				workEffortPurposeType.set("workEffortPurposeTypeId", lovId);
				workEffortPurposeType.set("description", description);
				workEffortPurposeType.set("parentTypeId", "ACTIVITY_WORK_TYPE");
				workEffortPurposeType.create();
				request.setAttribute("WorkEffortPurposeTypeId",lovId);
				request.setAttribute("_EVENT_MESSAGE_", "Activity Work Type Created Successfully ");
			}
			
		}catch (Exception e) {
			String errMsg = "Problem While Creating Activity Work Type " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
}
public static String updateActivityWorkType(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
	
	Delegator delegator = (Delegator) request.getAttribute("delegator");
	Map<String, Object> context = UtilHttp.getCombinedMap(request);
	
	String description = (String) context.get("description");
	String WorkEffortPurposeTypeId = (String) context.get("WorkEffortPurposeTypeId");
	Debug.log("description========="+description);
	try {
		
		if(UtilValidate.isNotEmpty(description)){
			GenericValue workEffortPurpose = EntityQuery.use(delegator).from("WorkEffortPurposeType").where("WorkEffortPurposeTypeId", WorkEffortPurposeTypeId).queryOne();
			
			if(UtilValidate.isNotEmpty(workEffortPurpose)) {
				workEffortPurpose.set("description", description);
				
				workEffortPurpose.store();
				request.setAttribute("WorkEffortPurposeTypeId",WorkEffortPurposeTypeId);
				request.setAttribute("_EVENT_MESSAGE_", "Activity Work Type  Updated Successfully ");
			}
		}
		
	}catch (Exception e) {
		String errMsg = "Problem While updating Activity Work Type " + e.toString();
		request.setAttribute("_ERROR_MESSAGE_", errMsg);
		return "error";
	}
	return "success";
}

}
