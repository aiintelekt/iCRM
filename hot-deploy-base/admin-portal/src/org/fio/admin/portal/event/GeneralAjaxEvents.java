/**
 * 
 */
package org.fio.admin.portal.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.admin.portal.constant.ResponseConstants;
import org.fio.admin.portal.util.DataUtil;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.QueryUtil;
import org.fio.homeapps.util.UtilActivity;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Sharif
 *
 */
public class GeneralAjaxEvents {

	private GeneralAjaxEvents() { }

	private static final String MODULE = GeneralAjaxEvents.class.getName();

	public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
		return doJSONResponse(response, jsonObject.toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
		return doJSONResponse(response, JSONArray.fromObject(collection).toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Map map) {
		return doJSONResponse(response, JSONObject.fromObject(map));
	}

	public static String doJSONResponse(HttpServletResponse response, String jsonString) {
		String result = "success";

		response.setContentType("application/x-json");
		try {
			response.setContentLength(jsonString.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
			Debug.logWarning("Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(), MODULE);
			response.setContentLength(jsonString.length());
		}

		Writer out;
		try {
			out = response.getWriter();
			out.write(jsonString);
			out.flush();
		} catch (IOException e) {
			Debug.logError(e, "Failed to get response writer", MODULE);
			result = "error";
		}
		return result;
	}

	public static GenericValue getUserLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (GenericValue) session.getAttribute("userLogin");
	}

	/*************************************************************************/
	/**                                                                     **/
	/**                      Common JSON Requests                           **/
	/**                                                                     **/
	/*************************************************************************/

	public static String searchOppoStages(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String start = request.getParameter("start");
		String length = request.getParameter("length");

		String opportunityStageId = request.getParameter("opportunityStageId");
		String stageName = request.getParameter("stageName");
		String opportunityStatusId = request.getParameter("opportunityStatusId");
		String enable = request.getParameter("enable");

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {

			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

			List conditionList = FastList.newInstance();

			if (UtilValidate.isNotEmpty(opportunityStageId)) {
				conditionList.add(EntityCondition.makeCondition("opportunityStageId", EntityOperator.EQUALS, opportunityStageId));
			}

			if (UtilValidate.isNotEmpty(stageName)) {
				EntityCondition nameCondition = EntityCondition.makeCondition("description", EntityOperator.LIKE, "%" + stageName + "%");
				conditionList.add(nameCondition);
			}

			if (UtilValidate.isNotEmpty(opportunityStatusId)) {
				conditionList.add(EntityCondition.makeCondition("opportunityStatusId", EntityOperator.EQUALS, opportunityStatusId));
			}

			if (UtilValidate.isNotEmpty(enable)) {
				conditionList.add(EntityCondition.makeCondition("enable", EntityOperator.EQUALS, enable));
			}

			EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
			efo.setOffset(0);
			efo.setLimit(1000);

			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			Debug.logInfo("list start: "+UtilDateTime.nowTimestamp(), MODULE);
			List<GenericValue> oppoStageList = delegator.findList("SalesOpportunityStage", mainConditons, null, UtilMisc.toList("-createdStamp"), efo, false);
			Debug.logInfo("list end: "+UtilDateTime.nowTimestamp(), MODULE);

			if (UtilValidate.isNotEmpty(oppoStageList)) {

				for (GenericValue oppoStage : oppoStageList) {

					Map<String, Object> data = new HashMap<String, Object>();

					opportunityStageId = oppoStage.getString("opportunityStageId"); 
					stageName = oppoStage.getString("description"); 
					opportunityStatusId = oppoStage.getString("opportunityStatusId"); 
					enable = oppoStage.getString("enable"); 

					String oppoStatus = "";
					if (UtilValidate.isNotEmpty(opportunityStatusId)) {
						oppoStatus = EnumUtil.getEnumDescription(delegator, opportunityStatusId, "OPPO_STATUS");
					}

					data.put("opportunityStageId", opportunityStageId);
					data.put("stageName", stageName);
					data.put("oppoStatus", oppoStatus);
					data.put("enable", enable);
					data.put("probability", oppoStage.getString("defaultProbability"));
					data.put("sequenceNum", oppoStage.getString("sequenceNum"));

					dataList.add(data);

				}
			}
			Debug.logInfo("data ready: "+UtilDateTime.nowTimestamp(), MODULE);

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, dataList);

	}

	public static String searchOtherLovs(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String start = request.getParameter("start");
		String length = request.getParameter("length");

		String lovTypeId = request.getParameter("lovTypeId");
		String lovId = request.getParameter("lovId");
		String name = request.getParameter("name");
		String isEnable = request.getParameter("isEnable");

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {

			GenericValue lovEntityAssoc = null;
			if (UtilValidate.isNotEmpty(lovTypeId)) {
				lovEntityAssoc = delegator.findOne("LovEntityAssoc", UtilMisc.toMap("lovEntityTypeId", lovTypeId), false);
			}
			if (UtilValidate.isNotEmpty(lovEntityAssoc)) {
				Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

				String entityName = lovEntityAssoc.getString("entityName");
				String idColumn = lovEntityAssoc.getString("idColumn");
				String nameColumn = lovEntityAssoc.getString("nameColumn");
				String descColumn = lovEntityAssoc.getString("descColumn");
				String sequenceColumn = lovEntityAssoc.getString("sequenceColumn");
				String enableColumn = lovEntityAssoc.getString("enableColumn");
				String filterValue = lovEntityAssoc.getString("filterValue");

				List conditionList = FastList.newInstance();

				//conditionList.add(EntityCondition.makeCondition("lovEntityTypeId", EntityOperator.EQUALS, lovTypeId));

				if (UtilValidate.isNotEmpty(lovId)) {
					conditionList.add(EntityCondition.makeCondition(idColumn, EntityOperator.EQUALS, lovId));
				}

				if (UtilValidate.isNotEmpty(name)) {
					EntityCondition nameCondition = EntityCondition.makeCondition(nameColumn, EntityOperator.LIKE, "%" + name + "%");
					conditionList.add(nameCondition);
				}

				if (UtilValidate.isNotEmpty(isEnable)) {
					conditionList.add(EntityCondition.makeCondition(enableColumn, EntityOperator.EQUALS, isEnable));
				}

				if (UtilValidate.isNotEmpty(filterValue)) {
					JSONObject filterObj = JSONObject.fromObject(filterValue);
					Map<String, Object> filter = ParamUtil.jsonToMap(filterObj);

					QueryUtil.makeCondition(conditionList, filter);
				}

				EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
				efo.setOffset(0);
				efo.setLimit(1000);

				Set<String> fieldsToSelect = new LinkedHashSet<String>();
				fieldsToSelect.add(idColumn);
				fieldsToSelect.add(nameColumn);
				fieldsToSelect.add(descColumn);
				fieldsToSelect.add(sequenceColumn);
				fieldsToSelect.add(enableColumn);
				fieldsToSelect.add("createdStamp");

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				Debug.logInfo("list start: "+UtilDateTime.nowTimestamp(), MODULE);
				List < GenericValue > lovList = EntityQuery.use(delegator).select(fieldsToSelect).from(entityName).where(mainConditons).maxRows(100).orderBy("-createdStamp").queryList();
				//List<GenericValue> lovList = delegator.findList(entityName, mainConditons, fieldsToSelect, UtilMisc.toList(sequenceColumn), efo, false);
				Debug.logInfo("list end: "+UtilDateTime.nowTimestamp(), MODULE);

				if (UtilValidate.isNotEmpty(lovList)) {

					for (GenericValue lov : lovList) {

						Map<String, Object> data = new HashMap<String, Object>();

						lovId = lov.getString(idColumn); 
						name = lov.getString(nameColumn); 
						isEnable = lov.getString(enableColumn); 
						String sequence = lov.getString(sequenceColumn); 
						String description = lov.getString(descColumn); 

						String createdDate = "";
						if (UtilValidate.isNotEmpty(lov.get("createdStamp"))) {
							createdDate = UtilDateTime.timeStampToString(lov.getTimestamp("createdStamp"), "dd/MM/yyyy", TimeZone.getDefault(), null);
						}

						data.put("lovTypeId", lovTypeId);
						data.put("lovId", lovId);
						data.put("name", name);
						data.put("isEnable", isEnable);
						data.put("sequence", sequence);
						data.put("description", description);
						data.put("createdDate", createdDate);

						dataList.add(data);

					}
				}
				Debug.logInfo("data ready: "+UtilDateTime.nowTimestamp(), MODULE);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, dataList);

	}
	public static String searchLovs(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		String lovTypeId = request.getParameter("lovTypeId");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			List<EntityCondition> conditionlist = FastList.newInstance();
			conditionlist.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, lovTypeId));
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("enumId");
			fieldsToSelect.add("description");
			fieldsToSelect.add("enumTypeId");
			fieldsToSelect.add("name");
			fieldsToSelect.add("isEnabled");
			fieldsToSelect.add("sequenceId");
			fieldsToSelect.add("createdStamp");
			
			List < GenericValue > lovList = EntityQuery.use(delegator).select(fieldsToSelect).from("Enumeration").where(condition).maxRows(100).orderBy("-lastUpdatedTxStamp").queryList();
			if (UtilValidate.isNotEmpty(lovList)) {
				for (GenericValue lov : lovList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("lovTypeId", lov.getString("enumTypeId"));
					data.put("lovId", lov.getString("enumId"));
					data.put("name", lov.getString("name"));
					data.put("description", lov.getString("description"));
					data.put("createdDate", lov.getString("createdStamp"));
					data.put("isEnable", lov.getString("isEnabled"));
					data.put("sequence", lov.getString("sequenceId"));
					dataList.add(data);

				}
			}
			Debug.logInfo("data ready: "+UtilDateTime.nowTimestamp(), MODULE);

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, dataList);

	}
	public static String getEmailEngineValues(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String mailEngine = request.getParameter("mailEngine");
		List < Object > results = FastList.newInstance();
		try {
			List<GenericValue> mailEngineValues = delegator.findByAnd("SystemProperty", UtilMisc.toMap("systemResourceId", mailEngine), null, false);
			for (GenericValue mailProperty: mailEngineValues) {
				
				String systemPropertyId = mailProperty.getString("systemPropertyId");
				String systemPropertyValue = mailProperty.getString("systemPropertyValue");
				Map < String, Object > data = new HashMap < String, Object > ();
				data.put("systemPropertyId", systemPropertyId);
				data.put("systemPropertyValue", systemPropertyValue);
				results.add(data);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);

	}
	
	public static String searchTemplateCategory(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String requestUri = request.getParameter("requestUri");
		String parentTemplateCategoryId= request.getParameter("emailEngine");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			String isEnable="";
			List<EntityCondition> conditionlist = FastList.newInstance();
			conditionlist.add(EntityCondition.makeCondition("parentTemplateCategoryId", EntityOperator.EQUALS, parentTemplateCategoryId));
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("parentTemplateCategoryId");
			fieldsToSelect.add("templateCategoryId");
			fieldsToSelect.add("templateCategoryName");
			fieldsToSelect.add("isEnabled");
			fieldsToSelect.add("sequenceId");
			fieldsToSelect.add("createdStamp");
			List < GenericValue > templateCatDetailsList = EntityQuery.use(delegator).select(fieldsToSelect).from("TemplateCategory").where(condition).maxRows(100).orderBy("-lastUpdatedTxStamp").queryList();
			if (UtilValidate.isNotEmpty(templateCatDetailsList)) {
				for (GenericValue templateCategoryGV : templateCatDetailsList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("parentTemplateCategoryId", templateCategoryGV.getString("parentTemplateCategoryId"));
					data.put("templateCategoryId", templateCategoryGV.getString("templateCategoryId"));
					data.put("templateCategoryName", templateCategoryGV.getString("templateCategoryName"));
					//data.put("description", templateCategoryGV.getString("description"));
					data.put("createdDate", templateCategoryGV.getString("createdStamp"));
					isEnable = templateCategoryGV.getString("isEnabled");
					isEnable=EnumUtil.getEnumDescription(delegator, isEnable, "YES_NO_TYPE");
					data.put("isEnable", UtilValidate.isNotEmpty(isEnable)?isEnable:templateCategoryGV.getString("isEnabled"));
					data.put("sequence", templateCategoryGV.getString("sequenceId"));
					dataList.add(data);
				}
			}
			Debug.logInfo("data ready: "+UtilDateTime.nowTimestamp(), MODULE);

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, dataList);
	}
	
	public static String searchResAvails(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map < String, Object > context = UtilHttp.getCombinedMap(request);

		String partyId = request.getParameter("partyId");
		String reasonId = request.getParameter("reasonId");
		
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");

		Timestamp systemTime = UtilDateTime.nowTimestamp();
		
		String fromDate = (String) context.get("fromDate");
		String thruDate = (String) context.get("thruDate");
		
		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		
		try {
			
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
			
			GenericValue activity = EntityUtil.getFirst( delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId", domainEntityId), null, false) );

			List conditionList = FastList.newInstance();
			
			//conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
			
			/*conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("profileConfigurationId", EntityOperator.EQUALS, profileConfigurationId),
					EntityCondition.makeCondition("profileCode", EntityOperator.EQUALS, profileConfigurationId)
                    ));*/
			
			if (UtilValidate.isNotEmpty(partyId)) {
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			}
			if (UtilValidate.isNotEmpty(reasonId)) {
				conditionList.add(EntityCondition.makeCondition("reasonId", EntityOperator.EQUALS, reasonId));
			}
			
			if (UtilValidate.isNotEmpty(domainEntityType)) {
				conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
			}
			if (UtilValidate.isNotEmpty(domainEntityId)) {
				conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
			}
			
			if (UtilValidate.isNotEmpty(fromDate)) {
				Timestamp sd = UtilDateTime.stringToTimeStamp(fromDate, "yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(sd)));
			}
			if (UtilValidate.isNotEmpty(thruDate)) {
				Timestamp ed = UtilDateTime.stringToTimeStamp(thruDate, "yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
				conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(ed)));
			}
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> configList = delegator.findList("ResourceAvailability", mainConditons, null, UtilMisc.toList("-createdStamp"), null, false);
			if (UtilValidate.isNotEmpty(configList)) {
				
				List<GenericValue> resAvailReasonList = EntityQuery.use(delegator).from("Enumeration").where("enumTypeId","RESAVAIL_REASON").cache(true).queryList();
	            Map<String, Object> resAvailReasonMap = UtilValidate.isNotEmpty(resAvailReasonList) ? DataUtil.getMapFromGeneric(resAvailReasonList, "enumId", "description", false) : new HashMap<String, Object>();
	            
	            List<GenericValue> resAvailTypeList = EntityQuery.use(delegator).from("Enumeration").where("enumTypeId","RESAVAIL_TYPE").cache(true).queryList();
	            Map<String, Object> resAvailTypeMap = UtilValidate.isNotEmpty(resAvailTypeList) ? DataUtil.getMapFromGeneric(resAvailTypeList, "enumId", "description", false) : new HashMap<String, Object>();
				
	            List<EntityCondition> conditionList1 = FastList.newInstance();
	            conditionList1.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "TECHNICIAN"));
	            conditionList1.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTRACT_TYPE"));
        		EntityCondition mainConds = EntityCondition.makeCondition(conditionList1, EntityOperator.AND);
        		
        		List<GenericValue> contractPartyList =  EntityQuery.use(delegator).select("partyIdTo").from("PartyRelationship").where(mainConds).queryList();
        		List<String> contractPartyIdList = UtilValidate.isNotEmpty(contractPartyList) ? EntityUtil.getFieldListFromEntityList(contractPartyList, "partyIdTo", true) : new ArrayList<String>();
	            
				for (GenericValue entry : configList) {
					Map<String, Object> data = new HashMap<String, Object>();
					
					data.put("entryId", entry.getString("entryId"));
					data.put("resourceName", PartyHelper.getPartyName(delegator, entry.getString("partyId"), false));
					
					//data.put("reason", EnumUtil.getEnumDescription(delegator, entry.getString("reasonId"), "RESAVAIL_REASON"));
					//data.put("availabilityType", EnumUtil.getEnumDescription(delegator, entry.getString("availabilityTypeId"), "RESAVAIL_TYPE"));
					
					data.put("reason", resAvailReasonMap.get(entry.getString("reasonId")));
					data.put("availabilityType", resAvailTypeMap.get(entry.getString("availabilityTypeId")));
					
					String type = "Reeb";
					//if (UtilActivity.isContractorTechnician(delegator, entry.getString("partyId"))) {
					if(UtilValidate.isNotEmpty(contractPartyIdList) && contractPartyIdList.contains(entry.getString("partyId"))) {
						type = "Contractor";
					}
					data.put("type", type);
					
					if (UtilValidate.isNotEmpty(activity)) {
						data.put("activityStatusId", activity.getString("currentStatusId"));
					}
					
					data.put("createdOn", UtilDateTime.timeStampToString(entry.getTimestamp("createdStamp"), globalDateTimeFormat, TimeZone.getDefault(), null));
					data.put("fromDate", UtilValidate.isNotEmpty(entry.get("fromDate")) ? UtilDateTime.timeStampToString(entry.getTimestamp("fromDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
					data.put("thruDate", UtilValidate.isNotEmpty(entry.get("thruDate")) ? UtilDateTime.timeStampToString(entry.getTimestamp("thruDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
					data.put("startDate", UtilValidate.isNotEmpty(entry.get("startDate")) ? UtilDateTime.timeStampToString(entry.getTimestamp("startDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
					data.put("endDate", UtilValidate.isNotEmpty(entry.get("endDate")) ? UtilDateTime.timeStampToString(entry.getTimestamp("endDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
					
					dataList.add(data);
				}
				
			}
			
			result.put("data", dataList);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, result);

	}
	
	public static String releaseCalBooking(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		//Map<String, Object> context = UtilHttp.getCombinedMap(request);

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");

		Map<String, Object> result = FastMap.newInstance();
		try {
			List conditionList = FastList.newInstance();
			
			conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
			conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			
			//int count = delegator.removeByCondition("ResourceAvailability", mainConditons);
			List<GenericValue> entryList = delegator.findList("ResourceAvailability", mainConditons, null, null, null, false);
			if (UtilValidate.isNotEmpty(entryList)) {
				
				long activeCount = entryList.stream().filter((entry)->UtilValidate.isNotEmpty(entry.get("startDate"))).count();
				if (activeCount > 0) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseConstants.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Technician already started the activity so no release process!");
				} else {
					entryList.forEach((entry)->{
						if (UtilValidate.isNotEmpty(entry) && UtilValidate.isNotEmpty(entry.getString("domainEntityId"))
								&& UtilValidate.isNotEmpty(entry.getString("domainEntityType")) && entry.getString("domainEntityType").equals("ACTIVITY")
								) {
							String workEffortId = entry.getString("domainEntityId");
							String partyId = entry.getString("partyId");
							UtilActivity.expireWorkEffortPartyAssignment(delegator, workEffortId, partyId, null);
						}
						try {
							entry.remove();
						} catch (GenericEntityException e) {
							//e.printStackTrace();
							Debug.logError(e.getMessage(), MODULE);
						}
					});
					
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				}
			} else {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseConstants.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "No booking schedule to be released!");
			}
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseConstants.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		return doJSONResponse(response, result);
	}
	
	public static String removeResAvailData(HttpServletRequest request, HttpServletResponse response) {
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String selectedEntryIds = request.getParameter("selectedEntryIds");
		String isRemoveAssociation = request.getParameter("isRemoveAssociation");
		
		Map<String, Object> result = FastMap.newInstance();

		try {

			if (UtilValidate.isNotEmpty(selectedEntryIds)) {
				for(String entryId : selectedEntryIds.split(",")) {
					//delegator.removeByAnd("ResourceAvailability", UtilMisc.toMap("entryId", entryId));
					if (UtilValidate.isNotEmpty(isRemoveAssociation) && isRemoveAssociation.equals("Y")) {
						List conditionList = FastList.newInstance();
						conditionList.add(EntityCondition.makeCondition("entryId", EntityOperator.EQUALS, entryId));
						EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.OR);
						GenericValue entry = EntityUtil.getFirst( delegator.findList("ResourceAvailability", mainConditons, null, null, null, false) );
						if (UtilValidate.isNotEmpty(entry) && UtilValidate.isNotEmpty(entry.getString("domainEntityId"))
								&& UtilValidate.isNotEmpty(entry.getString("domainEntityType")) && entry.getString("domainEntityType").equals("ACTIVITY")
								) {
							String workEffortId = entry.getString("domainEntityId");
							String partyId = entry.getString("partyId");
							UtilActivity.expireWorkEffortPartyAssignment(delegator, workEffortId, partyId, null);
						}
						entry.remove();
					} else {
						delegator.removeByAnd("ResourceAvailability", UtilMisc.toMap("entryId", entryId));
					}
				}

				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Removed resource available data");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);

			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());

			return AjaxEvents.doJSONResponse(response, result);
		}

		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String getResAvailData(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String entryId = request.getParameter("entryId");
		
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			List conditionList = FastList.newInstance();

			conditionList.add(EntityCondition.makeCondition("entryId", EntityOperator.EQUALS, entryId));

			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.OR);
			GenericValue entry = EntityUtil.getFirst( delegator.findList("ResourceAvailability", mainConditons, null, null, null, false) );
			if (UtilValidate.isNotEmpty(entry)) {
				
				//data.putAll(entry.getAllFields());
				
				data.put("partyId", entry.getString("partyId"));
				
				data.put("entryId", entry.getString("entryId"));
				data.put("resourceName", PartyHelper.getPartyName(delegator, entry.getString("partyId"), false));
				data.put("reason", EnumUtil.getEnumDescription(delegator, entry.getString("reasonId"), "RESAVAIL_REASON"));
				data.put("availabilityType", EnumUtil.getEnumDescription(delegator, entry.getString("availabilityTypeId"), "RESAVAIL_TYPE"));
				data.put("createdOn", UtilDateTime.timeStampToString(entry.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null));
				
				String type = "Reeb";
				if (UtilActivity.isContractorTechnician(delegator, entry.getString("partyId"))) {
					type = "Contractor";
				}
				data.put("type", type);
				
				data.put("fromDate", UtilValidate.isNotEmpty(entry.get("fromDate")) ? UtilDateTime.timeStampToString(entry.getTimestamp("fromDate"), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), null) : "");
				data.put("thruDate", UtilValidate.isNotEmpty(entry.get("thruDate")) ? UtilDateTime.timeStampToString(entry.getTimestamp("thruDate"), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), null) : "");
			}

			result.put("data", data);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", data);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String updateResAvailData(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String entryId = request.getParameter("entryId");
		
		String fromDate = request.getParameter("fromDate_date");
		String thruDate = request.getParameter("thruDate_date");

		String fromTime = request.getParameter("fromDate_time");
		String thruTime = request.getParameter("thruDate_time");
		
		String userLoginId = userLogin.getString("userLoginId");
		
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			List conditionList = FastList.newInstance();

			conditionList.add(EntityCondition.makeCondition("entryId", EntityOperator.EQUALS, entryId));

			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.OR);
			GenericValue entry = EntityUtil.getFirst( delegator.findList("ResourceAvailability", mainConditons, null, null, null, false) );
			if (UtilValidate.isNotEmpty(entry)) {
				Timestamp fromDateTime = ParamUtil.getTimestamp(fromDate, fromTime, "yyyy-MM-dd HH:mm");
				Timestamp thruDateTime = ParamUtil.getTimestamp(thruDate, thruTime, "yyyy-MM-dd HH:mm");
				
				entry.put("fromDate", fromDateTime);
				entry.put("thruDate", thruDateTime);
				
				entry.put("modifiedByUserLogin", userLoginId);
				
				entry.store();
			}

			result.put("data", data);
			result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully updated");
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", data);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
	
	public static String isLastTechnician(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String workEffortId = request.getParameter("workEffortId");
		
		String userLoginId = userLogin.getString("userLoginId");
		
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> data = new HashMap<String, Object>();
		boolean isLastTechnician = false;
		try {
			
			List conditionList = FastList.newInstance();
        	
			conditionList.add(EntityCondition.makeCondition("availabilityTypeId", EntityOperator.EQUALS, "RESA_TYP_NON_AVAIL"));
			conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "ACTIVITY"));
			conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, workEffortId));
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> calBookingList = delegator.findList("ResourceAvailability", mainConditons, null, null, null, false);
			if (UtilValidate.isNotEmpty(calBookingList)) {
				long notEndCount = calBookingList.stream().filter(booking -> UtilValidate.isEmpty(booking.getTimestamp("endDate"))).count();
				if (notEndCount == 1) {
					isLastTechnician = true;
				}
			}
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", data);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		result.put("isLastTechnician", isLastTechnician);
		return AjaxEvents.doJSONResponse(response, result);
	}
	
}
