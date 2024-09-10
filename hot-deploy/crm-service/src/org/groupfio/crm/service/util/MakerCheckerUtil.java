package org.groupfio.crm.service.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.groupfio.crm.service.constants.MakerCheckerConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Manjesh
 * @author Sharif
 *
 */ 
public class MakerCheckerUtil {
	private static String MODULE = MakerCheckerUtil.class.getName();

	public static Map < String, Object > getModifiedObjectGv(List<GenericValue> existingGvList , List<GenericValue> toCompareGvList){
		HashMap <String ,Object> convertedMapExisting = new HashMap<>();
		HashMap <String ,Object> convertedMapToCompare = new HashMap<>();
		List<HashMap> listOfExistingGvMap = new ArrayList<HashMap>();

		if (UtilValidate.isNotEmpty(existingGvList)) {
			for (GenericValue existing : existingGvList) {
				HashMap<String, Object> convertedSingleMapExisting = (HashMap<String, Object>) constructMapFromGv(existing);
				listOfExistingGvMap.add(convertedSingleMapExisting);

				//implement a way to add incremental map
				String  entityName = existing.getEntityName();
				String primaryKeys = existing.getPkShortValueString();

			}
		}

		if (UtilValidate.isNotEmpty(toCompareGvList)) {
			for (GenericValue toCompare : toCompareGvList) {
				convertedMapToCompare = (HashMap<String, Object>) constructMapFromGv(toCompare);
			}
		}
		MapDifference<String, Object> differenceFields = Maps.difference(convertedMapExisting, convertedMapToCompare);
		Map<String, ValueDifference<Object>> entriesDiffering = differenceFields.entriesDiffering();

		return convertedMapExisting;
	}

	public static String getModifiedObject(List<GenericValue> existingGvList , Map toCompareContext){
		HashMap <String ,Object> convertedMapExisting = new HashMap<>();

		List<HashMap<String ,Object>> listOfExistingGvMap = new ArrayList<HashMap<String ,Object>>();

		if (UtilValidate.isNotEmpty(existingGvList)) {
			for (GenericValue existing : existingGvList) {
				HashMap<String, Object> convertedSingleMapExisting = (HashMap<String, Object>) constructMapFromGv(existing);
				listOfExistingGvMap.add(convertedSingleMapExisting);

				//implement a way to add incremental map
				String  entityName = existing.getEntityName();
				String primaryKeys = existing.getPkShortValueString();

			}
		}
		Map<String, Object> finalExistingMap = new ConcurrentHashMap<String, Object>();
		for(HashMap<String ,Object> infGv : listOfExistingGvMap) {
			
			for (String eachIndKey : infGv.keySet()) {
				if(UtilValidate.isNotEmpty(infGv.get(eachIndKey))){
					finalExistingMap.put(eachIndKey, infGv.get(eachIndKey));
				}
			}
		}
		
		List toRemoveKeys = new ArrayList<>();
		for(Iterator<String> keysInLeft = finalExistingMap.keySet().iterator();keysInLeft.hasNext();){
			String key = keysInLeft.next();
			if (!toCompareContext.containsKey(key)) {
				finalExistingMap.remove(key);
			}
		}
		
		MapDifference<String, Object> differenceFields = Maps.difference(finalExistingMap, toCompareContext);
		System.out.println(finalExistingMap +"finalExistingMap");
		System.out.println(toCompareContext +"toCompareContext");

		System.out.println(differenceFields+"differenceFieldsdifferenceFields");
		Map<String, ValueDifference<Object>> entriesDiffering = differenceFields.entriesDiffering();
		/*
		 * use this for later purposes
		Map<String, Object> entriesDifferingLeft = differenceFields.entriesOnlyOnLeft();
		Map<String, Object> entriesDifferingRight = differenceFields.entriesOnlyOnRight();
		String entryDefLeft = entriesDifferingLeft.toString();
		String entryDefRight = entriesDifferingRight.toString();*/
		String entryDiff = null;
		if (UtilValidate.isNotEmpty(entriesDiffering)) {
			entryDiff = entriesDiffering.toString();
		}

		return entryDiff;
	}

	public static Map < String, Object > constructMapFromGv(GenericValue genericData){
		HashMap <String ,Object> gvMap = (HashMap<String, Object>) genericData.getAllFields();		
		return gvMap;

	}

	public static Map saveMakerAuditDetails(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();

		String serviceRequestType = (String) context.get("serviceRequestType");
		String modeOfAction = (String) context.get("modeOfAction");
		List<String> entityPrimaryStrings = UtilGenerics.checkList(context.get("entityPrimaryStrings"));
		String primaryEntityName = (String) context.get("primaryEntityName");
		String serviceRequestId = (String) context.get("serviceRequestId");
		if (UtilValidate.isEmpty(serviceRequestId)) {
			serviceRequestId = delegator.getNextSeqId("UserAuditRequest");
		} else {
			expireServiceRequestAssociations(delegator, serviceRequestId);
		}
		String uri = "";
		String primaryMainKey = "";
		String primaryMainKeyValue = "";
		String requestUri = "";
		try {
			String partyMakerId = userLogin.getString("userLoginId");
			GenericValue auditRequest = delegator.makeValue("UserAuditRequest");
			auditRequest.put("serviceRequestId", serviceRequestId);
			auditRequest.put("serviceRequestType", serviceRequestType);
			auditRequest.put("modeOfAction", modeOfAction);
			auditRequest.put("serviceRequestId", serviceRequestId);
			auditRequest.put("partyMakerId", partyMakerId);
			auditRequest.put("fromDate", UtilDateTime.nowTimestamp());
			auditRequest.put("partyCheckerId", null);

			GenericValue serviceRequest = EntityQuery.use(delegator).from("AuditEntityConfig").where("configId", serviceRequestType).queryOne();
			if(UtilValidate.isNotEmpty(serviceRequest)) {
				uri = serviceRequest.getString("requestUri");
				primaryMainKey = serviceRequest.getString("primKey");
			}


			for (String jsonIterate : entityPrimaryStrings) {
				JSONObject eachEntityValues = JSONObject.fromObject(jsonIterate);
				String entityName = null;
				Object primaryValues = null;
				String toStringPrimaryvalue = "";
				for (Object key  : eachEntityValues.keySet()) {
					String keyStr =(String)key;
					Object keyValue = eachEntityValues.get(keyStr);
					if ("entityName".equals(keyStr)) {
						entityName = (String)eachEntityValues.get(keyStr);
					}

					if ("primaryKeyValues".equals(keyStr) ) {
						primaryValues = keyValue;

						toStringPrimaryvalue = primaryValues.toString();

						if (UtilValidate.isEmpty(primaryMainKeyValue)) {
							primaryMainKeyValue = getValueFromJSONString(toStringPrimaryvalue, primaryMainKey);
						}
					}

				}
				GenericValue userEntityAudit = delegator.makeValue("UserEntityAudit");
				userEntityAudit.put("entityAuditId", delegator.getNextSeqId("UserEntityAudit"));
				userEntityAudit.put("entityName", entityName);
				userEntityAudit.put("newValue", toStringPrimaryvalue);
				userEntityAudit.put("modifiedDate", UtilDateTime.nowTimestamp());
				userEntityAudit.put("userLoginIdMaker", partyMakerId);
				userEntityAudit.put("approvalStatus", MakerCheckerConstants.ApprovalStatus.PENDING);
				userEntityAudit.put("primaryEntityRefId", toStringPrimaryvalue);
				userEntityAudit.put("primaryEntityName", primaryEntityName);
				userEntityAudit.put("serviceRequestId", serviceRequestId);
				userEntityAudit.put("serviceRequestType", serviceRequestType);
				userEntityAudit.put("userLoginIdChecker", null);
				userEntityAudit.put("actionType", MakerCheckerConstants.ModeOfAction.CREATE);
				userEntityAudit.create();

			}
			requestUri = uri+"?"+primaryMainKey+"="+primaryMainKeyValue;
			auditRequest.put("requestUri", requestUri);
			auditRequest.put("primaryRefId", primaryMainKey+"::"+primaryMainKeyValue);
			auditRequest.create();
			result.put("serviceRequestId", serviceRequestId);
			result.putAll(ServiceUtil.returnSuccess("Successfully create Audit Log.."));

		} catch (GenericEntityException e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
			return result;
		}

		return result;
	}

	public static String constructJsonStringFromGv(GenericValue genericValue) {
		JSONObject entityConsolidated = new JSONObject();
		JSONArray pkValues = new JSONArray();
		JSONArray nonPkValues = new JSONArray();
		JSONObject entityPkValue = new JSONObject();
		JSONObject entityNonPkValue = new JSONObject();
		String jsonString = "";
		if(UtilValidate.isNotEmpty(genericValue)){

			GenericPK pkShortString = genericValue.getPrimaryKey();
			Collection<String> pks = pkShortString.getAllKeys();
			String entityName = genericValue.getEntityName();
			Collection<String> nonPks = genericValue.getAllKeys();
			//String pkShortValueString = genericValue.getPkShortValueString();
			//String pks[] = pkShortValueString.split("::");

			for(String pk : pks ){
				entityPkValue.put(pk, genericValue.getString(pk));
			}

			for (String nonPk : nonPks ){
				entityNonPkValue.put(nonPk, genericValue.getString(nonPk));
			}
			pkValues.add(entityPkValue);
			nonPkValues.add(entityNonPkValue);
			entityConsolidated.put("primaryKeyValues" , pkValues);
			entityConsolidated.put("nonPrimaryeyValues" , nonPkValues);

			entityConsolidated.put("entityName" , entityName);
			//write logic to check if json is not empty
			jsonString = entityConsolidated.toString();

			System.out.println(jsonString +"jsonString");

		}
		return jsonString;
	}
	
	public static String getValueFromJSONString(String jsonString , String toFind) {
		System.out.println(jsonString +"jsonStringjsonString");
		if(jsonString.contains("[") || jsonString.contains("]")){
			jsonString = jsonString.replace("[", " ");
			jsonString = jsonString.replace("]", " ");
			jsonString = jsonString.trim();
		}
		System.out.println(jsonString +"jsonStringjsonString111");
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		String valueForToFind="";
		for (Object key  : jsonObject.keySet()) {
			String keyStr =(String)key;
			if (keyStr.equals(toFind)) {
				valueForToFind = (String)jsonObject.get(keyStr);
			}

		}
		return valueForToFind;
	}

	public static Map getServiceRequestAttr(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String serviceRequestId = (String) context.get("serviceRequestId");
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			GenericValue getServiceValues = EntityQuery.use(delegator).from("UserAuditRequest").where("serviceRequestId", serviceRequestId).filterByDate().queryOne();
			if (UtilValidate.isNotEmpty(getServiceValues)){
				String modeOfAction = getServiceValues.getString("modeOfAction");
				String serviceRequestType = getServiceValues.getString("serviceRequestType");
				resp.put("modeOfAction", modeOfAction);
				resp.put("serviceRequestType", serviceRequestType);
			}
			resp.put("serviceRequestType", serviceRequestId);

		} catch (GenericEntityException e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}


		return resp;


	}

	public static void expireServiceRequestAssociations(Delegator delegator , String serviceRequestId) {
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			GenericValue getServiceValues = EntityQuery.use(delegator).from("UserAuditRequest").where("serviceRequestId", serviceRequestId).filterByDate().queryOne();
			if (UtilValidate.isNotEmpty(getServiceValues)) {
				getServiceValues.put("statusId" ,"EXPIRED");
				getServiceValues.store();
			}

			delegator.removeByAnd("UserEntityAudit", UtilMisc.toMap("serviceRequestId", serviceRequestId));

		} catch (GenericEntityException e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}

	}

	public static Map makerCheckerAuditProcess(DispatchContext dctx, Map context) {

		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Delegator delegator = dctx.getDelegator();

			String serviceRequestId  = (String) context.get("serviceRequestId");
			String entityAuditExtName = (String) context.get("entityAuditExtName");
			String serviceRequestType  = (String) context.get("serviceRequestType");
			String modeOfAction  = (String) context.get("modeOfAction");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyMakerId = (String) context.get("partyMakerId");
			String statusId = (String) context.get("statusId");
			String differenceFields  = (String) context.get("differenceFields");
			
			GenericValue auditEntityExtGv = delegator.makeValue(entityAuditExtName);	
			ModelEntity auditEntityExtGvModel = delegator.getModelEntity(entityAuditExtName);
			
			String remarks  = (String) context.get("remarks");
			//audit entity ext fields
			List<String> listOfFieldsToSet =  auditEntityExtGvModel.getAllFieldNames();
			
			Map contextMap = (Map) context.get("contextMap");
			
			//update the new value to the audit ext entity
			System.out.println(listOfFieldsToSet+"listOfFieldsToSet");
			
			for (String fieldToSet : listOfFieldsToSet) {
				
				Object fieldToSetValue = "";
				if (UtilValidate.isNotEmpty(contextMap.get(fieldToSet))) { 
					fieldToSetValue = contextMap.get(fieldToSet);
				}
				System.out.println(fieldToSet+": "+fieldToSetValue);

				if (UtilValidate.isNotEmpty(fieldToSetValue)) { 
					auditEntityExtGv.put(fieldToSet, fieldToSetValue);
				}
			}
			
			// get equivalent json string : do not change any utility calls
			String newValueString = constructJsonStringFromGv(auditEntityExtGv);
			//on create
			
			Map<String, Object> createAuditLogContext = new HashMap<String, Object>();
			createAuditLogContext.put("serviceRequestType", serviceRequestType);
			createAuditLogContext.put("modeOfAction", modeOfAction);
			createAuditLogContext.put("entityPrimaryStrings", "");
			createAuditLogContext.put("newValueString", newValueString);
			createAuditLogContext.put("contextMap", contextMap);
			createAuditLogContext.put("userLogin", userLogin);
			createAuditLogContext.put("partyMakerId", partyMakerId);
			createAuditLogContext.put("statusId", statusId);
			createAuditLogContext.put("differenceFields", differenceFields);
			createAuditLogContext.put("remarks", remarks);

			// call the create Audit service
			Map<String, Object> createAuditResponse;
			try {
				createAuditResponse = dispatcher.runSync("ratePortal.saveServiceRequestOnCreate", createAuditLogContext);
				serviceRequestId = (String) createAuditResponse.get("serviceRequestId");
			} catch (GenericServiceException e) {
				return resp;
			}

			auditEntityExtGv.put("serviceRequestId", serviceRequestId);
			System.out.println(auditEntityExtGv+"auditEntityExtGv");
			auditEntityExtGv.create();
			
			if ("CREATE_BASE_RATE".equals(serviceRequestType) || "UPDATE_BASE_RATE".equals(serviceRequestType)) {	
				
				List<Map<String, Object>> tierRateList = (List<Map<String, Object>>) contextMap.get("tierRateList");
				long sequenceNumber = 1;
				
				if (UtilValidate.isNotEmpty(tierRateList)) {
					for(Map<String,Object> tierRate : tierRateList) {
						
						GenericValue rateConsolidate = (GenericValue) tierRate.get("rateConsolidate");
						
						GenericValue auditEntityExt = delegator.makeValue("FtpRateConsolidateTierEntityAuditExt");
						
						auditEntityExt.putAll(rateConsolidate.getAllFields());
						
						auditEntityExt.put("serviceRequestId", serviceRequestId);
						auditEntityExt.put("recordSequence", UtilFormatOut.formatPaddedNumber(sequenceNumber++, 5));
						
						auditEntityExt.create();
						
					}
				}
				
			} else if ("CREATE_PRODUCT_PREF".equals(serviceRequestType)) {	
				
				String currencyCode = (String) contextMap.get("currencyCode");
				//String effectiveDate = (String) contextMap.get("effectiveDate");
				String productGroupId = (String) contextMap.get("productGroupId");
				String productId = (String) contextMap.get("productId");
				String financeType = (String) contextMap.get("financeType");
				String fixedIntRateType = (String) contextMap.get("fixedIntRateType");
				String fixedSpread = (String) contextMap.get("fixedSpread");
				String fixedSpreadType = (String) contextMap.get("fixedSpreadType");
				String floatIntRateType = (String) contextMap.get("floatIntRateType");
				String floatSpread = (String) contextMap.get("floatSpread");
				String floatSpreadType = (String) contextMap.get("floatSpreadType");
				Timestamp eftDate = null;
				
				long sequenceNumber = 1;
				
				/*GenericValue createProductPref = delegator.makeValue("CustomerProductPreferenceEntityAuditExt");
				//createProductPref.put("productPreferenceId", productPreferenceId);
				createProductPref.put("currency", currencyCode);
				createProductPref.put("effectiveDate", eftDate);
				createProductPref.put("productGroupId", productGroupId);
				createProductPref.put("productId", productId);
				createProductPref.put("financeType", financeType);
				
				createProductPref.put("serviceRequestId", serviceRequestId);
				
				createProductPref.create();*/
					
				//Store Fixed Rate Preference
				GenericValue fixedProductPref = delegator.makeValue("ProductRatePreferenceEntityAuditExt");
				//fixedProductPref.put("preferenceRateId", fixedPreferenceRateId);
				//fixedProductPref.put("productPreferenceId", productPreferenceId);
				fixedProductPref.put("preferenceRateType", "FIXED_RATE");
				fixedProductPref.put("intRateType", fixedIntRateType);
				fixedProductPref.put("spread", fixedSpread);
				fixedProductPref.put("spreadType", fixedSpreadType);
				
				fixedProductPref.put("serviceRequestId", serviceRequestId);
				fixedProductPref.put("recordSequence", UtilFormatOut.formatPaddedNumber(sequenceNumber++, 5));
				
				fixedProductPref.create();
				
				//Store Float Rate Preference
				GenericValue floatProductPref = delegator.makeValue("ProductRatePreferenceEntityAuditExt");
				//floatProductPref.put("preferenceRateId", floatPreferenceRateId);
				//floatProductPref.put("productPreferenceId", productPreferenceId);
				floatProductPref.put("preferenceRateType", "FLOAT_RATE");
				floatProductPref.put("intRateType", floatIntRateType);
				floatProductPref.put("spread", floatSpread);
				floatProductPref.put("spreadType", floatSpreadType);
				
				floatProductPref.put("serviceRequestId", serviceRequestId);
				floatProductPref.put("recordSequence", UtilFormatOut.formatPaddedNumber(sequenceNumber++, 5));
				
				floatProductPref.create();
				
			} else if ("UPDATE_PRODUCT_PREF".equals(serviceRequestType)) {	
				
				String productPreferenceId = (String) contextMap.get("proPrefId");
				String currencyCode = (String) contextMap.get("currencyCode");
				//String effectiveDate = (String) contextMap.get("effectiveDate");
				String productGroupId = (String) contextMap.get("productGroupId");
				String productId = (String) contextMap.get("productId");
				String financeType = (String) contextMap.get("financeType");
				String fixedIntRateType = (String) contextMap.get("fixedIntRateType");
				String fixedSpread = (String) contextMap.get("fixedSpread");
				String fixedSpreadType = (String) contextMap.get("fixedSpreadType");
				String floatIntRateType = (String) contextMap.get("floatIntRateType");
				String floatSpread = (String) contextMap.get("floatSpread");
				String floatSpreadType = (String) contextMap.get("floatSpreadType");
				Timestamp eftDate = null;
				
				long sequenceNumber = 1;
				
				/*GenericValue createProductPref = delegator.makeValue("CustomerProductPreferenceEntityAuditExt");
				createProductPref.put("productPreferenceId", productPreferenceId);
				createProductPref.put("currency", currencyCode);
				createProductPref.put("effectiveDate", eftDate);
				createProductPref.put("productGroupId", productGroupId);
				createProductPref.put("productId", productId);
				createProductPref.put("financeType", financeType);
				
				createProductPref.put("serviceRequestId", serviceRequestId);
				
				createProductPref.create();*/
					
				//Store Fixed Rate Preference
				GenericValue fixedProductPref = delegator.makeValue("ProductRatePreferenceEntityAuditExt");
				//fixedProductPref.put("preferenceRateId", fixedPreferenceRateId);
				fixedProductPref.put("productPreferenceId", productPreferenceId);
				fixedProductPref.put("preferenceRateType", "FIXED_RATE");
				fixedProductPref.put("intRateType", fixedIntRateType);
				fixedProductPref.put("spread", fixedSpread);
				fixedProductPref.put("spreadType", fixedSpreadType);
				
				fixedProductPref.put("serviceRequestId", serviceRequestId);
				fixedProductPref.put("recordSequence", UtilFormatOut.formatPaddedNumber(sequenceNumber++, 5));
				
				fixedProductPref.create();
				
				//Store Float Rate Preference
				GenericValue floatProductPref = delegator.makeValue("ProductRatePreferenceEntityAuditExt");
				//floatProductPref.put("preferenceRateId", floatPreferenceRateId);
				floatProductPref.put("productPreferenceId", productPreferenceId);
				floatProductPref.put("preferenceRateType", "FLOAT_RATE");
				floatProductPref.put("intRateType", floatIntRateType);
				floatProductPref.put("spread", floatSpread);
				floatProductPref.put("spreadType", floatSpreadType);
				
				floatProductPref.put("serviceRequestId", serviceRequestId);
				floatProductPref.put("recordSequence", UtilFormatOut.formatPaddedNumber(sequenceNumber++, 5));
				
				floatProductPref.create();
				
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return resp;

	}
	//Generalized for  both create and update
	public static Map saveServiceRequestOnCreate(DispatchContext dctx, Map context) throws Exception{
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();

		String serviceRequestType = (String) context.get("serviceRequestType");
		String modeOfAction = (String) context.get("modeOfAction");
		String statusId = (String) context.get("statusId");
		//List<String> entityPrimaryStrings = UtilGenerics.checkList(context.get("entityPrimaryStrings"));
		String primaryEntityName = (String) context.get("primaryEntityName");
		String serviceRequestId = (String) context.get("serviceRequestId");
		String requestUri = (String) context.get("requestUri");
		String newValueEnityObjectString = (String) context.get("newValueString");
		String partyMakerId = (String) context.get("partyMakerId");
		Map contextMap = (Map) context.get("contextMap");
		String differenceFields  = (String) context.get("differenceFields");
		String remarks = (String) context.get("remarks");
		
		/*System.out.println(contextMap+"contextMapcontextMap");
		List toRemoveKeys = new ArrayList<>();
		for(Iterator<String> keysInLeft = contextMap.keySet().iterator();keysInLeft.hasNext();){
			String key = keysInLeft.next();
			if (context.get(keysInLeft) instanceof Object) {
				System.out.println(key);
				contextMap.remove(key);
			}
		}*/
		if ("CREATE_CHARGE_CODE".equals(serviceRequestType)) {
			
		}
		if(contextMap.containsKey("userLogin")) {
			contextMap.remove("userLogin");
		}
		if(contextMap.containsKey("request")) {
			contextMap.remove("request");
		}
		//convert map to gson string -- get as string to store in the database
		System.out.println(contextMap+"inside create");
		Gson gson =new Gson();
		String gsonStringForContext  = gson.toJson(contextMap);
		System.out.println(gsonStringForContext+"gsonStringgsonString");

		//convert string to map -- use the existing json object method
		JSONObject contextJson = JSONObject.fromObject(gsonStringForContext);
		//call the utility method that convert the json object to map
		Map jsonToMap = jsonToMap(contextJson);
		System.out.println(jsonToMap+"jsonToMap");

		if (UtilValidate.isEmpty(serviceRequestId)) {
			serviceRequestId = delegator.getNextSeqId("UserAuditRequest");
		} else {
			expireServiceRequestAssociations(delegator, serviceRequestId);
			serviceRequestId = delegator.getNextSeqId("UserAuditRequest");
			// write logic to move this data archive table
		}
		String uri = "";
		String primaryMainKey = "";
		String primaryMainKeyValue = "";
		String entityName = "";
		String mandatoryKeys="";
		try {

			GenericValue auditRequest = delegator.makeValue("UserAuditRequest");
			auditRequest.put("serviceRequestId", serviceRequestId);
			auditRequest.put("serviceRequestType", serviceRequestType);
			auditRequest.put("modeOfAction", modeOfAction);
			auditRequest.put("partyMakerId", partyMakerId);
			auditRequest.put("fromDate", UtilDateTime.nowTimestamp());
			auditRequest.put("partyCheckerId", null);
			auditRequest.put("contextMap", gsonStringForContext);
			auditRequest.put("statusId", statusId);
			auditRequest.put("remarks", remarks);
			
			
			GenericValue auditConfig = EntityQuery.use(delegator).from("AuditEntityConfig").where("configId", serviceRequestType).queryOne();
			if(UtilValidate.isNotEmpty(auditConfig)) {
				requestUri = auditConfig.getString("requestUri");
				primaryMainKey = auditConfig.getString("primKey");
				entityName = auditConfig.getString("entityName");
				mandatoryKeys= auditConfig.getString("mandatoryFields");
				
			}

			JSONObject entityPkValue = new JSONObject();
			GenericValue userEntityAudit = delegator.makeValue("UserEntityAudit");
			userEntityAudit.put("entityAuditId", delegator.getNextSeqId("UserEntityAudit"));
			userEntityAudit.put("entityName", entityName);
			userEntityAudit.put("newValue", newValueEnityObjectString);
			userEntityAudit.put("modifiedDate", UtilDateTime.nowTimestamp());
			userEntityAudit.put("userLoginIdMaker", partyMakerId);
			userEntityAudit.put("approvalStatus", MakerCheckerConstants.ApprovalStatus.PENDING);
			//userEntityAudit.put("primaryEntityRefId", toStringPrimaryvalue);
			userEntityAudit.put("primaryEntityName", primaryEntityName);
			userEntityAudit.put("serviceRequestId", serviceRequestId);
			userEntityAudit.put("serviceRequestType", serviceRequestType);
			userEntityAudit.put("userLoginIdChecker", null);
			auditRequest.put("remarks", remarks);
			Map<String, Object> contextMandatory = new HashMap<String, Object>();
			if (MakerCheckerConstants.ModeOfAction.UPDATE.equals(modeOfAction)) {
				auditRequest.put("differenceFields", differenceFields);
				if (primaryMainKey.contains(",")) {
					String[] mainKeys = primaryMainKey.split(",");
					for (String mainPk : mainKeys) {
						entityPkValue.put(mainPk, contextMap.get(mainPk));
						if (requestUri.contains("?")) {
							requestUri = requestUri+"&"+mainPk+"="+contextMap.get(mainPk);
						} else {
							requestUri = requestUri+"?"+mainPk+"="+contextMap.get(mainPk);
						}
					}
				} else {
					primaryMainKeyValue = (String) contextMap.get(primaryMainKey);
					entityPkValue.put(primaryMainKey, contextMap.get(primaryMainKey));
					requestUri = requestUri+"?"+primaryMainKey+"="+primaryMainKeyValue;
				}
				
				userEntityAudit.put("actionType", MakerCheckerConstants.ModeOfAction.UPDATE);
				auditRequest.put("primaryRefId", entityPkValue.toString());
			} else {
				userEntityAudit.put("actionType", MakerCheckerConstants.ModeOfAction.CREATE);
			}
			
			if(UtilValidate.isNotEmpty(mandatoryKeys)) {
				if (mandatoryKeys.contains(",")) {
					if("CREATE_CUSTANDACCT_PRICE_LIST".equals(serviceRequestType) || "UPDATE_CUSTANDACCT_PRICE_LIST".equals(serviceRequestType)) {
						
						//String productPriceListId = (String) contextMap.get("productPriceListId");
						
						//contextMandatory.put(DataUtil.MANDATORY_FIELD_CONFIG.get("chargeCode"), chargeCode);
						
					} else {
						String[] mandatoryKeyFields = mandatoryKeys.split(",");
						for (String mandatoryKeyField : mandatoryKeyFields) {
							contextMandatory.put(DataUtil.MANDATORY_FIELD_CONFIG.get(mandatoryKeyField), contextMap.get(mandatoryKeyField));
						}
					}
					
				} else {
					contextMandatory.put(DataUtil.MANDATORY_FIELD_CONFIG.get(mandatoryKeys), contextMap.get(mandatoryKeys));
				}
			}
			userEntityAudit.create();

			String jsonContextMandatory  = gson.toJson(contextMandatory);
			//requestUri = uri+"?"+primaryMainKey+"="+primaryMainKeyValue;
			auditRequest.put("requestUri", requestUri);
			
			auditRequest.put("mandatoryFields", jsonContextMandatory);
			//auditRequest.put("primaryRefId", primaryMainKey+"::"+primaryMainKeyValue);
			auditRequest.create();
			result.put("serviceRequestId", serviceRequestId);
			result.putAll(ServiceUtil.returnSuccess("Successfully create Audit Log.."));

		} catch (GenericEntityException e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}

		return result;
	}
	
	public static Map saveServiceRequestOnUpdate(DispatchContext dctx, Map context) throws Exception{
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();

		String serviceRequestType = (String) context.get("serviceRequestType");
		String modeOfAction = (String) context.get("modeOfAction");
		String statusId = (String) context.get("statusId");
		//List<String> entityPrimaryStrings = UtilGenerics.checkList(context.get("entityPrimaryStrings"));
		String primaryEntityName = (String) context.get("primaryEntityName");
		String serviceRequestId = (String) context.get("serviceRequestId");
		String requestUri = (String) context.get("requestUri");
		String newValueEnityObjectString = (String) context.get("newValueString");
		String partyMakerId = (String) context.get("partyMakerId");
		Map contextMap = (Map) context.get("contextMap");
		String differenceFields  = (String) context.get("differenceFields");

		System.out.println(contextMap+"contextMapcontextMap");

		//convert map to gson string -- get as string to store in the database
		Gson gson =new Gson();
		String gsonStringForContext  = gson.toJson(contextMap);
		System.out.println(gsonStringForContext+"gsonStringgsonString");

		//convert string to map -- use the existing json object method
		JSONObject contextJson = JSONObject.fromObject(gsonStringForContext);
		//call the utility method that convert the json object to map
		Map jsonToMap = jsonToMap(contextJson);
		System.out.println(jsonToMap+"jsonToMap");

		if (UtilValidate.isEmpty(serviceRequestId)) {
			serviceRequestId = delegator.getNextSeqId("UserAuditRequest");
		} else {
			expireServiceRequestAssociations(delegator, serviceRequestId);
			serviceRequestId = delegator.getNextSeqId("UserAuditRequest");
			// write logic to move this data archive table
		}
		String primaryMainKey = "";
		String entityName = "";
		try {

			GenericValue auditRequest = delegator.makeValue("UserAuditRequest");
			auditRequest.put("serviceRequestId", serviceRequestId);
			auditRequest.put("serviceRequestType", serviceRequestType);
			auditRequest.put("modeOfAction", modeOfAction);
			auditRequest.put("partyMakerId", partyMakerId);
			auditRequest.put("fromDate", UtilDateTime.nowTimestamp());
			auditRequest.put("partyCheckerId", null);
			auditRequest.put("contextMap", gsonStringForContext);
			auditRequest.put("differenceFields", differenceFields);
			auditRequest.put("statusId", statusId);

			GenericValue auditConfig = EntityQuery.use(delegator).from("AuditEntityConfig").where("configId", serviceRequestType).queryOne();
			if(UtilValidate.isNotEmpty(auditConfig)) {
				requestUri = auditConfig.getString("requestUri");
				primaryMainKey = auditConfig.getString("primKey");
				entityName = auditConfig.getString("entityName");
			}
			String primaryMainKeyValue = (String) contextMap.get(primaryMainKey);

			GenericValue userEntityAudit = delegator.makeValue("UserEntityAudit");
			userEntityAudit.put("entityAuditId", delegator.getNextSeqId("UserEntityAudit"));
			userEntityAudit.put("entityName", entityName);
			userEntityAudit.put("newValue", newValueEnityObjectString);
			userEntityAudit.put("modifiedDate", UtilDateTime.nowTimestamp());
			userEntityAudit.put("userLoginIdMaker", partyMakerId);
			userEntityAudit.put("approvalStatus", MakerCheckerConstants.ApprovalStatus.PENDING);
			//userEntityAudit.put("primaryEntityRefId", toStringPrimaryvalue);
			userEntityAudit.put("primaryEntityName", primaryEntityName);
			userEntityAudit.put("serviceRequestId", serviceRequestId);
			userEntityAudit.put("serviceRequestType", serviceRequestType);
			userEntityAudit.put("userLoginIdChecker", null);
			userEntityAudit.put("actionType", MakerCheckerConstants.ModeOfAction.UPDATE);
			userEntityAudit.create();


			requestUri = requestUri+"?"+primaryMainKey+"="+primaryMainKeyValue;
			System.out.println(requestUri);
			auditRequest.put("requestUri", requestUri);
			//auditRequest.put("primaryRefId", primaryMainKey+"::"+primaryMainKeyValue);
			auditRequest.create();
			result.put("serviceRequestId", serviceRequestId);
			result.putAll(ServiceUtil.returnSuccess("Successfully create Audit Log.."));

		} catch (GenericEntityException e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}

		return result;
	}

	public static Map<String ,Object> jsonToMap(JSONObject json) throws Exception{
		Map<String ,Object> retMap = new HashMap<String ,Object>();
		if (json != null) {
			retMap = toMap(json);
		}
		return retMap;
	}

	public static Map<String ,Object> toMap(JSONObject json) throws Exception{
		Map<String ,Object> map = new HashMap<String ,Object>();

		Iterator<String> keysItr  = json.keys();
		while(keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = json.get(key);

			if (value instanceof JSONArray) {
				value  = toList((JSONArray) value);

			} else if (value instanceof JSONObject) {
				value  = toMap((JSONObject) value);
			}

			map.put(key, value);
		}
		return map;
	}

	public static List<Object> toList(JSONArray array) throws Exception{
		List<Object> list = new ArrayList<Object>();

		for(int i=0; i < array.size(); i++) {
			Object value =array.get(i);
			if (value instanceof JSONArray) {
				value  = toList((JSONArray) value);

			} else if (value instanceof JSONObject) {
				value  = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}
	
	public static List<Map<String, Object>> getSessionStoredValuesByRequest(Delegator delegator ,String entityName , String serviceRequestId) {
		List<Map<String, Object>> attrList = new ArrayList<Map<String, Object>>();
		Map<String, Object> pricingMethod = new HashMap();
		try{
			if (UtilValidate.isNotEmpty(serviceRequestId)) {

				List conditionList = FastList.newInstance();


				conditionList.add(EntityCondition.makeCondition("serviceRequestId", EntityOperator.EQUALS, serviceRequestId));

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> assocList = delegator.findList(entityName, mainConditons, null, null, null, false);

				if (UtilValidate.isNotEmpty(assocList)) {
					for (GenericValue assoc : assocList) {

						Map<String, Object> glAccountConfig = assoc.getAllFields(); 

						attrList.add(glAccountConfig);

					}

				}

			}
		} catch (Exception e ) {

		}
		return attrList;
	}
	
	public static Boolean checkIfRequestNotExist(Delegator delegator, HashMap<String, Object> contextMap) {
		
		String serviceRequestType = (String) contextMap.get("serviceRequestType");
		String serviceRequestId = (String) contextMap.get("serviceRequestId");
		GenericValue userLogin = (GenericValue) contextMap.get("userLogin");
		
		JSONObject entityPkValue = new JSONObject();
		String requestUri = "";
		
		try {
			
			if (UtilValidate.isEmpty(serviceRequestType)) {
				return true;
			}
			
			GenericValue auditConfig = EntityQuery.use(delegator).from("AuditEntityConfig").where("configId", serviceRequestType).queryOne();
			String modeOfAction = auditConfig.getString("modeOfAction");
			requestUri = auditConfig.getString("requestUri");
			if (MakerCheckerConstants.ModeOfAction.UPDATE.equals(modeOfAction)) {
				String primaryMainKey = auditConfig.getString("primKey");
				if (primaryMainKey.contains(",")) {
					String[] mainKeys = primaryMainKey.split(",");
					for (String mainPk : mainKeys) {
						entityPkValue.put(mainPk, contextMap.get(mainPk));
						if (requestUri.contains("?")) {
							requestUri = requestUri+"&"+mainPk+"="+contextMap.get(mainPk);
						} else {
							requestUri = requestUri+"?"+mainPk+"="+contextMap.get(mainPk);
						}
						
					}
				} else {
					entityPkValue.put(primaryMainKey, contextMap.get(primaryMainKey));
					requestUri = requestUri+"?"+primaryMainKey+"="+contextMap.get(primaryMainKey);
				}
				String primStringFromJson = entityPkValue.toString();
				
				List conditionList = FastList.newInstance();
				EntityCondition primaryKeyCheckCond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("primaryRefId", EntityOperator.EQUALS, primStringFromJson),
	    				EntityCondition.makeCondition("requestUri", EntityOperator.EQUALS, requestUri)), EntityOperator.OR);
				conditionList.add(primaryKeyCheckCond);
				EntityCondition statusCheckCond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, MakerCheckerConstants.ApprovalStatus.PENDING),
	    				EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, MakerCheckerConstants.ApprovalStatus.REJECTED)), EntityOperator.OR);
				conditionList.add(statusCheckCond);
				conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("serviceRequestType", EntityOperator.EQUALS, serviceRequestType))));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				System.out.println(mainConditons+"mainConditonsmainConditons");
				GenericValue auditRequest = EntityUtil.getFirst( delegator.findList("UserAuditRequest", mainConditons, null, null, null, false) );
				System.out.println("auditRequest: "+auditRequest);
				
				if (UtilValidate.isNotEmpty(auditRequest)) {
					
					if (UtilValidate.isNotEmpty(serviceRequestId) && UtilValidate.isNotEmpty(auditRequest.get("partyMakerId")) && UtilValidate.isNotEmpty(userLogin) && auditRequest.getString("partyMakerId").equals(userLogin.getString("userLoginId"))) {
						return true;
					}
					
					return false;
				}

			}
			
		} catch (Exception e) {
			return false;
		}
		
		return true;

	}
	
	public static String getServiceRequestId(Delegator delegator, HashMap<String, Object> contextMap) {
		
		String serviceRequestType = (String) contextMap.get("serviceRequestType");
		
		JSONObject entityPkValue = new JSONObject();
		String requestUri = "";
		
		try {
			
			if (UtilValidate.isEmpty(serviceRequestType)) {
				return null;
			}
			
			GenericValue auditConfig = EntityQuery.use(delegator).from("AuditEntityConfig").where("configId", serviceRequestType).queryOne();
			String modeOfAction = auditConfig.getString("modeOfAction");
			requestUri = auditConfig.getString("requestUri");
			if (MakerCheckerConstants.ModeOfAction.UPDATE.equals(modeOfAction)) {
				String primaryMainKey = auditConfig.getString("primKey");
				if (primaryMainKey.contains(",")) {
					String[] mainKeys = primaryMainKey.split(",");
					for (String mainPk : mainKeys) {
						entityPkValue.put(mainPk, contextMap.get(mainPk));
						if (requestUri.contains("?")) {
							requestUri = requestUri+"&"+mainPk+"="+contextMap.get(mainPk);
						} else {
							requestUri = requestUri+"?"+mainPk+"="+contextMap.get(mainPk);
						}
						
					}
				} else {
					entityPkValue.put(primaryMainKey, contextMap.get(primaryMainKey));
					requestUri = requestUri+"?"+primaryMainKey+"="+contextMap.get(primaryMainKey);
				}
				String primStringFromJson = entityPkValue.toString();
				
				List conditionList = FastList.newInstance();
				EntityCondition primaryKeyCheckCond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("primaryRefId", EntityOperator.EQUALS, primStringFromJson),
	    				EntityCondition.makeCondition("requestUri", EntityOperator.EQUALS, requestUri)), EntityOperator.OR);
				conditionList.add(primaryKeyCheckCond);
				EntityCondition statusCheckCond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, MakerCheckerConstants.ApprovalStatus.PENDING),
	    				EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, MakerCheckerConstants.ApprovalStatus.REJECTED)), EntityOperator.OR);
				conditionList.add(statusCheckCond);
				conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("serviceRequestType", EntityOperator.EQUALS, serviceRequestType))));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				System.out.println(mainConditons+"mainConditonsmainConditons");
				GenericValue auditRequest = EntityUtil.getFirst( delegator.findList("UserAuditRequest", mainConditons, null, null, null, false) );
				System.out.println("auditRequest: "+auditRequest);
				
				if (UtilValidate.isNotEmpty(auditRequest)) {
					return auditRequest.getString("serviceRequestId");
				}

			}
			
		} catch (Exception e) {
			
		}
		
		return null;
		
	}
}
