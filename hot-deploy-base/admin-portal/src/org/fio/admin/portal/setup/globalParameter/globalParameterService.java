package org.fio.admin.portal.setup.globalParameter;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

public class globalParameterService {

	public static final String MODULE = globalParameterService.class.getName();
	public static final String RESOURCE = "AdminPortalUiLabels";
	
	public static Map < String, Object >createParameter(DispatchContext dctx, Map < String, Object > context) {
	    Debug.logInfo("------inside createParameter------" + context, MODULE);
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    Security security = dctx.getSecurity();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Locale locale = (Locale) context.get("locale");
	    
	    Map < String, Object > results = ServiceUtil.returnSuccess();
	    
	    String sections = (String) context.get("sections");
	    String parameterName = (String) context.get("parameterName");
	    String parameterId = (String) context.get("parameterId");
	    String parameterValue = (String) context.get("parameterValue");
	    try {
	    GenericValue parameterDetails = EntityUtil.getFirst(delegator.findByAnd("PretailLoyaltyGlobalParameters",
	            UtilMisc.toMap("parameterId", parameterId), null, false));
	    GenericValue parameter = delegator.makeValue("PretailLoyaltyGlobalParameters");
	        if (UtilValidate.isEmpty(parameterDetails)) {
	        	parameter.put("storeId",sections);
	        	parameter.put("description",parameterName);
	        	parameter.put("parameterId",parameterId);
	        	parameter.put("value",parameterValue);
	        	parameter.put("createdBy",userLogin.getString("userLoginId"));
	        	parameter.create();
	        	results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "GlobalParameterCreatedSuccessfully", locale));
	        	
	        }else {
	            return ServiceUtil.returnError("Global Parameter already exists");
	        }
}
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    return results;
}

	public static Map < String, Object >updateParameter(DispatchContext dctx, Map < String, Object > context) {
	    Debug.logInfo("------inside createParameter------" + context, MODULE);
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    Security security = dctx.getSecurity();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Locale locale = (Locale) context.get("locale");
	    
	    Map < String, Object > results = ServiceUtil.returnSuccess();
	    
	    
	    String paramId = (String) context.get("paramId");
	    Debug.log("paramId"+paramId);
	    String value = (String) context.get("value");
	    Debug.log("value"+value);
	    try {
	    GenericValue parameterDetails = EntityUtil.getFirst(delegator.findByAnd("PretailLoyaltyGlobalParameters",
	            UtilMisc.toMap("parameterId", paramId), null, false));
	        if (UtilValidate.isNotEmpty(parameterDetails)) {
	        	
	        	parameterDetails.put("value",value);
	        	parameterDetails.store();
	        	results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "GlobalParameterUpdatedSuccessfully", locale));
	        	
	        }else {
	            return ServiceUtil.returnError("Global Parameter already exists");
	        }
}
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    return results;
}
	public static Map < String, Object >createSection(DispatchContext dctx, Map < String, Object > context) {
	    Debug.logInfo("------inside createSection------" + context, MODULE);
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    Security security = dctx.getSecurity();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Locale locale = (Locale) context.get("locale");
	    
	    Map < String, Object > results = ServiceUtil.returnSuccess();
	    
	    String addSection = (String) context.get("newSection");
	    Debug.log("=========="+addSection);
	    try {
	    GenericValue parameterDetails = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
	            UtilMisc.toMap("description", addSection,"enumTypeId",AdminPortalConstant.GlobalParameter.GLOBAL_PARAMS), null, false));
	    GenericValue parameter = delegator.makeValue("Enumeration");
	        if (UtilValidate.isEmpty(parameterDetails)) {
	        	String sectionId = delegator.getNextSeqId("Enumeration");
	        	parameter.put("enumId",sectionId);
	        	parameter.put("description",addSection);
	        	parameter.put("enumCode",sectionId);
	        	parameter.put("enumTypeId",AdminPortalConstant.GlobalParameter.GLOBAL_PARAMS);
	        	parameter.create();
	        	results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SectionCreatedSuccessfully", locale));
	        	
	        }else {
	            return ServiceUtil.returnError("Section already exists");
	        }
}
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    return results;
}
	
	public static Map < String, Object >storeWorkingHrs(DispatchContext dctx, Map < String, Object > context) {
	    Debug.logInfo("------inside storeWorkingHrs------" + context, MODULE);
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    Security security = dctx.getSecurity();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Locale locale = (Locale) context.get("locale");
	    
	    Map < String, Object > results = ServiceUtil.returnSuccess();
	    
	    String workStartTime = (String) context.get("workStartTime");
	    String workEndTime = (String) context.get("workEndTime");
	    try {
	    	
	    	if(UtilValidate.isNotEmpty(workStartTime))
	    		workStartTime = workStartTime.substring(0, workStartTime.indexOf(":")).length() == 1 ? "0"+workStartTime : workStartTime;
	    	if(UtilValidate.isNotEmpty(workEndTime))
	    		workEndTime = workEndTime.substring(0, workEndTime.indexOf(":")).length() == 1 ? "0"+workEndTime : workEndTime;
			
			List <GenericValue> parameterDetails = delegator.findList("PretailLoyaltyGlobalParameters", EntityCondition.makeCondition("parameterId", EntityOperator.IN, UtilMisc.toList("WORK_START_TIME", "WORK_END_TIME")), null, null, null, false);

	    	if(UtilValidate.isNotEmpty(parameterDetails)){

	    		for(GenericValue eachParam : parameterDetails){

	    			String parameterId = (String) eachParam.getString("parameterId");
	    			if(parameterId.equals("WORK_START_TIME")){
	    				eachParam.set("value", workStartTime);

	    				eachParam.store();
	    			}else if(parameterId.equals("WORK_END_TIME")){
	    				eachParam.set("value", workEndTime);

	    				eachParam.store();
	    			}
	    			
	    		}
	        	results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "Working hours updated Successfully", locale));

	    	}else{
	    	
	    		GenericValue parameter = delegator.makeValue("PretailLoyaltyGlobalParameters");
	        	parameter.put("description","work start time");
	        	parameter.put("parameterId","WORK_START_TIME");
	        	parameter.put("value",workStartTime);
	        	parameter.put("createdBy",userLogin.getString("userLoginId"));

	        	parameter.create();
	        	
	        	parameter.put("description","work end time");
	        	parameter.put("parameterId","WORK_END_TIME");
	        	parameter.put("value",workEndTime);
	        	parameter.put("createdBy",userLogin.getString("userLoginId"));

	        	parameter.create();
	        	results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "Working hours CreatedSuccessfully", locale));
	        	
	        
	    	}
	    } catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    return results;

	}
	
	public static Map < String, Object >createAndUpdateDefaultValues(DispatchContext dctx, Map < String, Object > context) {
		Debug.logInfo("------inside createAndUpdateDefaultValues------" + context, MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		Map < String, Object > results = ServiceUtil.returnSuccess();

		String sections = (String) context.get("sections");
		String parameterName = (String) context.get("parameterName");
		String parameterId = (String) context.get("parameterId");
		String ownerPartyId = (String) context.get("ownerPartyId");
		String generalCountryGeoId = (String) context.get("generalCountryGeoId");
		String currencyUomId = (String) context.get("currencyUomId");
		String defaultSrSla = (String) context.get("defaultSrSla");
		String defaultSrSlaUnit = (String) context.get("defaultSrSlaUnit");

		try {
			GenericValue parameterDetails = EntityUtil.getFirst(delegator.findByAnd("PretailLoyaltyGlobalParameters",
					UtilMisc.toMap("parameterId", parameterId), null, false));
			GenericValue parameter = delegator.makeValue("PretailLoyaltyGlobalParameters");
			parameter.put("storeId",sections);
			parameter.put("description",parameterName);
			parameter.put("parameterId",parameterId);
			if(parameterId.equals("DEFAULT_RM_USER")) {
				parameter.put("value",ownerPartyId);
			}
			else if(parameterId.equals("DEFAULT_COUNTRY"))
			{
				String toSearch = "COUNTRY";
				parameter.put("value",generalCountryGeoId);
				setDefaultDynaValue(delegator,generalCountryGeoId,toSearch);
			}
			else if(parameterId.equals("DEFAULT_CURRENCY_UOM"))
			{
				String toSearch = "CURRENCY_MEASURE";
				parameter.put("value",currencyUomId);
				setDefaultDynaValue(delegator,currencyUomId,toSearch);
			}
			else if(parameterId.equals("DEFAULT_SR_SLA"))
			{
				parameter.put("value",defaultSrSla);
				parameter.put("description",defaultSrSlaUnit);
			}
			parameter.put("createdBy",userLogin.getString("userLoginId"));
			if (UtilValidate.isEmpty(parameterDetails)) {
				parameter.create();
				results = ServiceUtil.returnSuccess("Default Value Successfully Created.");
			}
			else {
				Debug.log("----in else --inside createAndUpdateDefaultValues------" + parameter);
				parameter.store(); 
				results = ServiceUtil.returnSuccess("Default Value Successfully Updated.");
			}



		}
		catch(Exception e)
		{
			return ServiceUtil.returnError("Problem in updating default values");
		}
		return results;
}
	
	public static String setDefaultDynaValue( Delegator delegator,String setDefaultValue,String toSearch ) {
		Set < String > fieldsToSelect = new TreeSet < String > ();
		try {
			fieldsToSelect.add("dynaConfigId");
			fieldsToSelect.add("dynaFieldId");
			fieldsToSelect.add("fieldName");
			fieldsToSelect.add("lookupFieldFilter");
			fieldsToSelect.add("defaultValue");
		EntityCondition condition = EntityCondition.makeCondition("lookupFieldFilter",EntityOperator.LIKE,"%"+toSearch+"%");
		List < GenericValue > generalValues = EntityQuery.use(delegator).select(fieldsToSelect).from("DynaScreenConfigField").where(condition).queryList();
		for (GenericValue dynaValue : generalValues) {
            if (UtilValidate.isNotEmpty(dynaValue)) {
            	dynaValue.set("defaultValue", setDefaultValue);
            	dynaValue.store();
            }
        }
		}
		catch(Exception e)
		{
			return "Problem In Updating Default Dyna Screen Value";
		}
		return setDefaultValue;
	}
	public static Map < String, Object >createAndUpdateSmtpValues(DispatchContext dctx, Map < String, Object > context) {
		Debug.logInfo("------inside createAndUpdateSmtpValues------" + context, MODULE);
		Delegator delegator = dctx.getDelegator();
		Map < String, Object > results = ServiceUtil.returnSuccess();
		String emailEngine = (String) context.get("mailEngine");

		try {
			//update the new mail provider
			GenericValue emailProvider = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "general","systemPropertyId", "mailEngine").queryOne();
			String oldEmailProvider = emailProvider.getString("systemPropertyValue");
			if (!UtilValidate.isEmpty(emailProvider)) {
				emailProvider.put("systemPropertyValue", emailEngine);
				emailProvider.store();
			}

			//remove existing email provider values
			List<GenericValue> mailEngineValues = delegator.findByAnd("SystemProperty", UtilMisc.toMap("systemResourceId", oldEmailProvider), null, false);
			for (GenericValue mailProperty: mailEngineValues) {
				mailProperty.remove();
			}
			//create new values
			GenericValue parameter = delegator.makeValue("SystemProperty");
			parameter.put("systemResourceId",emailEngine);
			parameter.put("systemPropertyId","mail.notifications.enabled");
			parameter.put("systemPropertyValue",context.get("mail.notifications.enabled"));
			parameter.put("description","mailNotificationsEnabled");
			parameter.create();

			parameter.put("systemResourceId",emailEngine);
			parameter.put("systemPropertyId","mail.smtp.relay.host");
			parameter.put("systemPropertyValue",context.get("mail.smtp.relay.host"));
			parameter.put("description","smtp relay host");
			parameter.create();

			parameter.put("systemResourceId",emailEngine);
			parameter.put("systemPropertyId","mail.smtp.auth.user");
			parameter.put("systemPropertyValue",context.get("mail.smtp.auth.user"));
			parameter.put("description","smtp auth user");
			parameter.create();

			parameter.put("systemResourceId",emailEngine);
			parameter.put("systemPropertyId","mail.smtp.auth.password");
			parameter.put("systemPropertyValue",context.get("mail.smtp.auth.password"));
			parameter.put("description","smtp auth password");
			parameter.create();

			parameter.put("systemResourceId",emailEngine);
			parameter.put("systemPropertyId","mail.smtp.port");
			parameter.put("systemPropertyValue",context.get("mail.smtp.port"));
			parameter.put("description","mail smtp port");
			parameter.create();

			parameter.put("systemResourceId",emailEngine);
			parameter.put("systemPropertyId","mail.smtp.starttls.enable");
			parameter.put("systemPropertyValue",context.get("mail.smtp.starttls.enable"));
			parameter.put("description","smtp starttls");
			parameter.create();

			parameter.put("systemResourceId",emailEngine);
			parameter.put("systemPropertyId","mail.smtp.socketFactory.port");
			parameter.put("systemPropertyValue",context.get("mail.smtp.socketFactory.port"));
			parameter.put("description","socket factory port");
			parameter.create();

			parameter.put("systemResourceId",emailEngine);
			parameter.put("systemPropertyId","mail.smtp.auth.require");
			parameter.put("systemPropertyValue",context.get("mail.smtp.auth.require"));
			parameter.put("description","mail smtp auth require");
			parameter.create();

			results = ServiceUtil.returnSuccess("Default Value Successfully Updated.");


		}
		catch(Exception e)
		{
			return ServiceUtil.returnError("Problem in updating default values");
		}
		return results;
}	
	public static Map createTemplateCategory(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();		
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String parentTemplateCategoryId = (String) context.get("parentTemplateCategoryId");
		String templateCategoryId = (String) context.get("templateCategoryId");
		String templateCategoryName = (String) context.get("templateCategoryName");
		String isEnable = (String) context.get("isEnable");
		String sequence = (String) context.get("sequence");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		Map<String, Object> result = new HashMap<String, Object>();

		try {

			result.put("parentTemplateCategoryId", parentTemplateCategoryId);
			
			List<GenericValue> templateCategoryList = EntityQuery.use(delegator).from("TemplateCategory")
					.where("templateCategoryId", templateCategoryId).queryList();
			
			if(templateCategoryList.size()>0) {
				result.putAll(ServiceUtil.returnError(templateCategoryId+"  already exists as Lov! "));
				return result;
			}
			
			GenericValue templateCategory = delegator.makeValue("TemplateCategory");
			templateCategory.put("parentTemplateCategoryId", parentTemplateCategoryId);
			templateCategory.put("templateCategoryId", templateCategoryId);
			templateCategory.put("templateCategoryName", templateCategoryName);
			templateCategory.put("isEnabled", isEnable);
			templateCategory.put("sequenceId", sequence);
			templateCategory.create();
			result.put("templateCategoryId", templateCategoryId);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully created Template Category."));
		return result;

	}
	public static Map updateTemplateCategory(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String parentTemplateCategoryId = (String) context.get("parentTemplateCategoryId");
		String templateCategoryId = (String) context.get("templateCategoryId");
		String templateCategoryName = (String) context.get("templateCategoryName");
		String isEnable = (String) context.get("isEnable");
		String sequence = (String) context.get("sequence");
		Map<String, Object> result = new HashMap<String, Object>();

		try {

			result.put("parentTemplateCategoryId", parentTemplateCategoryId);
			result.put("templateCategoryId", templateCategoryId);
			if(UtilValidate.isNotEmpty(parentTemplateCategoryId) && UtilValidate.isNotEmpty(templateCategoryId)){

				GenericValue  templateCategoryGV = EntityUtil.getFirst(delegator.findByAnd("TemplateCategory",
						UtilMisc.toMap("templateCategoryId", templateCategoryId), UtilMisc.toList("sequenceId"),
						false));
				
				List<GenericValue> templateCategoryNameList = EntityQuery.use(delegator).from("TemplateCategory")
						.where("templateCategoryName", templateCategoryName,"parentTemplateCategoryId", parentTemplateCategoryId).queryList();
				String currentDescription = templateCategoryGV.getString("templateCategoryName") ;
				
				if(templateCategoryNameList.size()>0 && !templateCategoryName.equals(currentDescription)) {
					result.putAll(ServiceUtil.returnError(templateCategoryName+"  already exists as Lov! "));
					return result;
				}
				
				if(UtilValidate.isNotEmpty(templateCategoryGV)) {
					templateCategoryGV.set("parentTemplateCategoryId", parentTemplateCategoryId);
					templateCategoryGV.set("templateCategoryName", templateCategoryName);
					templateCategoryGV.set("sequenceId", sequence);
					templateCategoryGV.set("isEnabled", isEnable);
					templateCategoryGV.store();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully updated Template Category."));

		return result;

	}
	public static Map < String, Object >createAndUpdateSftpValues(DispatchContext dctx, Map < String, Object > context) {
		Debug.logInfo("------inside createAndUpdateSftpValues------" + context, MODULE);
		Delegator delegator = dctx.getDelegator();
		Map < String, Object > results = ServiceUtil.returnSuccess();
		
		String host = (String) context.get("host");
		String location = (String) context.get("location");
		String password = (String) context.get("password");
		String port = (String) context.get("port");
		String user = (String) context.get("user");

		try {
			
			GenericValue parameterHost = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "general","systemPropertyId", "general.sftp.host").queryOne();
			if(UtilValidate.isNotEmpty(parameterHost)) {
				parameterHost.put("systemPropertyValue", host);
				parameterHost.store();
			}
			GenericValue parameterLocation = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "general","systemPropertyId", "general.sftp.location").queryOne();
			if(UtilValidate.isNotEmpty(parameterLocation)) {
				parameterLocation.put("systemPropertyValue", location);
				parameterLocation.store();
			}
			GenericValue parameterPassword = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "general","systemPropertyId", "general.sftp.password").queryOne();
			if(UtilValidate.isNotEmpty(parameterPassword)) {
				parameterPassword.put("systemPropertyValue", password);
				parameterPassword.store();
			}
			
			GenericValue parameterPort = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "general","systemPropertyId", "general.sftp.port").queryOne();
			if(UtilValidate.isNotEmpty(parameterPort)) {
				parameterPort.put("systemPropertyValue", port);
				parameterPort.store();
			}
			GenericValue parameterUser = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "general","systemPropertyId", "general.sftp.user").queryOne();
			if(UtilValidate.isNotEmpty(parameterUser)) {
				parameterUser.put("systemPropertyValue", user);
				parameterUser.store();
			}
			

			results = ServiceUtil.returnSuccess("Default Value Successfully Updated.");


		}
		catch(Exception e)
		{
			return ServiceUtil.returnError("Problem in updating default values");
		}
		return results;
}	
}