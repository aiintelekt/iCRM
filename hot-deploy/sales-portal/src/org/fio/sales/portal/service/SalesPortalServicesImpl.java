package org.fio.sales.portal.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.fio.homeapps.util.UtilGenerator;
import org.fio.sales.portal.event.AjaxEvents;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.stats.VisitHandler;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SalesPortalServicesImpl {
	private SalesPortalServicesImpl() {
	}

	private static final String MODULE = SalesPortalServicesImpl.class.getName();
	public static final String RESOURCE = "SalesPortalUiLabels";
	
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
			Debug.logWarning(
					"Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(),
					MODULE);
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
	
	
	public static String getRequiredDateFormat(String dateStr, SimpleDateFormat sdfSource, SimpleDateFormat sdfTarget) throws Exception {
		 String requiredDateStr = null;
		 java.util.Date sourceDateStr = null;
		 try {
			 sourceDateStr = sdfSource.parse(dateStr);
			 requiredDateStr = sdfTarget.format(sourceDateStr);
		 }catch(Exception e) {
			 Debug.log("Exception in getRequiredDateFormat() method for date : "+dateStr);
			 throw e;
		 }
		return requiredDateStr;
	 }
	
	
	public static Map<String, Object> getProspect(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		Map <String, Object> jsonResultMap = new HashMap<String, Object>();
		
		String firstName = (String) context.get("firstName");
		String status = (String) context.get("status");
		String sourceId = (String) context.get("sourceId");
		String prodLineInterest = (String) context.get("prodLineInterest");
		String segment = (String) context.get("segment");
		String createdOn = (String) context.get("createdOn");
		
		EntityListIterator prospectSummaryConfigurationIter = null;
		JSONObject obj = new JSONObject();
		JSONArray dataMap = new JSONArray();
		
		java.sql.Timestamp reservStart = null;
		if (UtilValidate.isNotEmpty(createdOn)) {
			int days = Integer.parseInt(createdOn);
			reservStart = UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), -1 * days);
		}
		
		try {
			List<EntityCondition> conditionsList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(firstName)) {
				EntityCondition firstName1 = EntityCondition.makeCondition("firstName", EntityOperator.LIKE, firstName);
				conditionsList.add(firstName1);
			}
			if (UtilValidate.isNotEmpty(status)) {
				EntityCondition status1 = EntityCondition.makeCondition("status", EntityOperator.EQUALS, status);
				conditionsList.add(status1);
			}
			if (UtilValidate.isNotEmpty(sourceId)) {
				EntityCondition sourceId1 = EntityCondition.makeCondition("sourceId", EntityOperator.EQUALS, sourceId);
				conditionsList.add(sourceId1);
			}
			if (UtilValidate.isNotEmpty(prodLineInterest)) {
				EntityCondition prodLineInterest1 = EntityCondition.makeCondition("prodLineInterest",EntityOperator.EQUALS, prodLineInterest);
				conditionsList.add(prodLineInterest1);
			}
			if (UtilValidate.isNotEmpty(segment)) {
				EntityCondition segment1 = EntityCondition.makeCondition("segment", EntityOperator.EQUALS, segment);
				conditionsList.add(segment1);
			}
			if (UtilValidate.isNotEmpty(reservStart)) {
				Timestamp lastContactDateSql = null;
				lastContactDateSql = new java.sql.Timestamp(reservStart.getTime());
				java.sql.Timestamp dayStart = UtilDateTime.getDayStart(lastContactDateSql);
				java.sql.Timestamp dayEnd = UtilDateTime.getDayEnd(lastContactDateSql);
				conditionsList.add(EntityCondition.makeCondition("createdOn", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
				conditionsList.add(EntityCondition.makeCondition("createdOn", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			}
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.setLimit(1000);

			Set<String> fieldsToSelect = UtilMisc.toSet("prospectExtId", "firstName", "lastName", "dateOfBirth", "nationality", "segment", "prodLineInterest", "status");
			EntityCondition condition = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			prospectSummaryConfigurationIter = delegator.find("ProspectSummary", condition, null, fieldsToSelect, null, efo);
			GenericValue prospectSummaryConfigurationItem;

			while (prospectSummaryConfigurationIter != null && (prospectSummaryConfigurationItem = prospectSummaryConfigurationIter.next()) != null) {
				JSONObject listObj = new JSONObject();
				listObj.put("prospectExtId", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("prospectExtId"))? prospectSummaryConfigurationItem.getString("prospectExtId"): "");
				listObj.put("firstName", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("firstName"))? prospectSummaryConfigurationItem.getString("firstName"): "");
				listObj.put("lastName", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("lastName"))? prospectSummaryConfigurationItem.getString("lastName") : "");
				listObj.put("dateOfBirth", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("dateOfBirth"))? prospectSummaryConfigurationItem.getString("dateOfBirth") : "");
				listObj.put("nationality", prospectSummaryConfigurationItem.getString("nationality"));
				listObj.put("segment", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("segment")) ? prospectSummaryConfigurationItem.getString("segment") : "");
				listObj.put("prodLineInterest", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("prodLineInterest")) ? prospectSummaryConfigurationItem.getString("prodLineInterest") : "");
				listObj.put("occupation", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("occupation")) ? prospectSummaryConfigurationItem.getString("occupation") : "");
				listObj.put("status", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("status")) ? prospectSummaryConfigurationItem.getString("status") : "");
				listObj.put("createdOn", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getTimestamp("createdOn")) ? prospectSummaryConfigurationItem.getTimestamp("createdOn") : "");
				dataMap.add(listObj);
			}
			prospectSummaryConfigurationIter.close();
			obj.put("data", dataMap);
			jsonResultMap.put("jsonResult", obj);
		} catch (Exception e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.put("salesProspectSummaryMap", jsonResultMap);
		result.putAll(ServiceUtil.returnSuccess("Successfully find Prospects..."));
		return result;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> getTeleSales(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		Map <String, Object> jsonResultMap = new HashMap<String, Object>();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		String campaignName = (String) context.get("marketingCampaignName");
		String callOutCome = (String) context.get("callOutCome");
		String totalCallsByCamp = (String) context.get("totalCallsByCamp");
		String callBackDate = (String) context.get("callBackDate");
		String lastContactDays = (String) context.get("lastContactDays");
		String responseType = (String) context.get("responseType");
		String CustomerCIN = (String) context.get("customerCIN");
		String statusOpen = (String) context.get("statusOpen");
		String statusCallBack = (String) context.get("statusCallBack");
		String statusWon = (String) context.get("statusWon");
		String statusNew = (String) context.get("statusNew");
		String statusLost = (String) context.get("statusLost");
		String statusPending = (String) context.get("statusPending");
		List statusList = new ArrayList();
		String dropSearch = (String) context.get("searchData");
		String bUnitid = "";
		
		EntityListIterator prospectSummaryConfigurationIter = null;
		JSONObject obj = new JSONObject();
		JSONArray dataMap = new JSONArray();
		java.sql.Timestamp reservStart = null;

		if (UtilValidate.isNotEmpty(lastContactDays)) {
			int days = Integer.parseInt(lastContactDays);
			reservStart = UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), -1 * days);
		}
		try{

			Set<String> statusIdsSet = new HashSet<String>();
			List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
			
			if (UtilValidate.isNotEmpty(statusOpen)) {
				statusIdsSet.add("60024");//New
				statusIdsSet.add("60017");//Call back
				statusIdsSet.add("60015");//Appeal
				statusIdsSet.add("60028");//Pending for checking
				statusIdsSet.add("60033");//TM WIP case
				statusIdsSet.add("60043");//Open
			}
			if (UtilValidate.isNotEmpty(statusCallBack)) {
				statusIdsSet.add(statusCallBack);
			}
			if (UtilValidate.isNotEmpty(statusPending)) {
				statusIdsSet.add("60015");
				statusIdsSet.add("60028");
				statusIdsSet.add("60033");
			}
			if (UtilValidate.isNotEmpty(statusNew)) {
				statusIdsSet.add(statusNew);
			}
			if (UtilValidate.isNotEmpty(statusIdsSet)) {
				conditionsList.add(EntityCondition.makeCondition("callOutCome", EntityOperator.IN, statusIdsSet.stream().collect(Collectors.toList())));
				statusIdsSet.clear();
			}
			if (UtilValidate.isNotEmpty(statusWon)) {
				statusList.add(statusWon);
				
			}
			if( UtilValidate.isNotEmpty(statusLost)) {
				statusList.add(statusLost);
			}
			if (UtilValidate.isNotEmpty(statusList)) {
				conditionsList.add(EntityCondition.makeCondition("opportunityStage", EntityOperator.IN, statusList));
			}
			if ((dropSearch) != null && UtilValidate.isNotEmpty(dropSearch) && dropSearch.equals("MyBUActivities")) {
				String partyId = userLogin.getString("partyId");
				List condList = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				EntityCondition bUnitCond = EntityCondition.makeCondition(condList);
				List<GenericValue> getBusinessUnits = delegator.findList("EmplTeam", bUnitCond, null, null, null,false);
				GenericValue getAprty = getBusinessUnits.get(0);
				bUnitid = getAprty.getString("businessUnit");
				EntityCondition bUnitCondition = EntityCondition.makeCondition("businessUnitId", EntityOperator.EQUALS,bUnitid);				
				conditionsList.add(bUnitCondition);
			}

			if ((dropSearch) != null && UtilValidate.isNotEmpty(dropSearch) && dropSearch.equals("MyOpenActivities")) {
				Set<String> openStatusIdsSet = new HashSet<String>();
				openStatusIdsSet.add("60024");//New
				openStatusIdsSet.add("60017");//Call back
				openStatusIdsSet.add("60015");//Appeal
				openStatusIdsSet.add("60028");//Pending for checking
				openStatusIdsSet.add("60033");//TM WIP case
				openStatusIdsSet.add("60043");//Open
				EntityCondition tempIdCondition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("callOutCome", EntityOperator.IN, openStatusIdsSet.stream().collect(Collectors.toList())),
						EntityCondition.makeCondition("csrUserLoginId", EntityOperator.EQUALS,userLogin.getString("userLoginId"))),
						EntityOperator.AND);
				conditionsList.add(tempIdCondition);
			}

			if ((dropSearch) != null && UtilValidate.isNotEmpty(dropSearch) && dropSearch.equals("MyDelegatedActivities")) {
				EntityCondition tempIdCondition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS,userLogin.getString("userLoginId")),
						EntityCondition.makeCondition("csrUserLoginId", EntityOperator.NOT_EQUAL,userLogin.getString("userLoginId"))),
						EntityOperator.AND);
				conditionsList.add(tempIdCondition);
			}
			if ((dropSearch) != null && UtilValidate.isNotEmpty(dropSearch) && dropSearch.equals("MyTeamsActivities")) {
				String partyId = userLogin.getString("partyId");
				if (UtilValidate.isNotEmpty(partyId)) {
					EntityCondition partyCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
					List<GenericValue> getEmplTemaId = delegator.findList("EmplTeam", partyCond, null, null, null, false);
					GenericValue emplTeamId = getEmplTemaId.get(0);

					EntityCondition emplTemIdCond = EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, emplTeamId.getString("emplTeamId"));
					List<GenericValue> emplTeamList = EntityQuery.use(delegator).from("EmplPositionFulfillment").where(emplTemIdCond).queryList();
					List<String> getParties = EntityUtil.getFieldListFromEntityList(emplTeamList, "partyId", true);

					List<EntityCondition> tempIdCondition = FastList.newInstance();					
					getParties.add(partyId);
					conditionsList.add(EntityCondition.makeCondition("csrUserLoginId", EntityOperator.IN, getParties));
					EntityCondition loginCondition = EntityCondition.makeCondition("csrUserLoginId",EntityOperator.EQUALS, userLogin.getString("userLoginId"));					
					conditionsList.add(loginCondition);
				}
			}
			if ((dropSearch) != null && UtilValidate.isNotEmpty(dropSearch) && dropSearch.equals("MyTeamOwnedActivities")) {
				String partyId = userLogin.getString("partyId");
				if (UtilValidate.isNotEmpty(partyId)) {
					EntityCondition partyCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
					List<GenericValue> getEmplTemaId = delegator.findList("EmplTeam", partyCond, null, null, null, false);
					GenericValue emplTeamId = getEmplTemaId.get(0);

					EntityCondition emplTemIdCond = EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, emplTeamId.getString("emplTeamId"));
					List<GenericValue> emplTeamList = EntityQuery.use(delegator).from("EmplTeam").where(emplTemIdCond).queryList();

					List<String> getParties = EntityUtil.getFieldListFromEntityList(emplTeamList, "partyId", true);
					conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, getParties));
					EntityCondition statusCondition = EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("callOutCome", EntityOperator.EQUALS, "60043"),
							EntityCondition.makeCondition("csrUserLoginId", EntityOperator.EQUALS,userLogin.getString("userLoginId"))),
							EntityOperator.AND);
					conditionsList.add(statusCondition);
				}
			}
			if ((dropSearch) != null && UtilValidate.isNotEmpty(dropSearch) && dropSearch.equals("MyDirectReportsActivities")) {
				EntityCondition tempIdCondition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("csrUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId"))));
				conditionsList.add(tempIdCondition);
			}
			if (UtilValidate.isNotEmpty(campaignName)) {
				EntityCondition tempIdCondition = EntityCondition.makeCondition("marketingCampaignId", EntityOperator.EQUALS, campaignName);
				conditionsList.add(tempIdCondition);
			}
			if (UtilValidate.isNotEmpty(callOutCome)) {
				EntityCondition tempNameCondition = EntityCondition.makeCondition("callOutCome", EntityOperator.EQUALS, callOutCome);
				conditionsList.add(tempNameCondition);
			}
			if (UtilValidate.isNotEmpty(totalCallsByCamp)) {
				EntityCondition tempTypeCondition = EntityCondition.makeCondition("totalCallsByCamp", EntityOperator.EQUALS, totalCallsByCamp);
				conditionsList.add(tempTypeCondition);
			}
			if (UtilValidate.isNotEmpty(responseType)) {
				EntityCondition tempTypeCondition = EntityCondition.makeCondition("responseTypeId", EntityOperator.EQUALS, responseType);
				conditionsList.add(tempTypeCondition);
			}
			if (UtilValidate.isNotEmpty(CustomerCIN)) {
				EntityCondition tempTypeCondition = EntityCondition.makeCondition("externalReferenceId", EntityOperator.EQUALS, CustomerCIN);
				conditionsList.add(tempTypeCondition);
			}
			if (UtilValidate.isNotEmpty(callBackDate)) {
				java.sql.Date callBackDateSql = null;
				try {
					callBackDateSql = new java.sql.Date(df2.parse(callBackDate).getTime());
				} catch (ParseException e) {
				}
				conditionsList.add(EntityCondition.makeCondition("callBackDate", EntityOperator.GREATER_THAN_EQUAL_TO, callBackDateSql));
				conditionsList.add(EntityCondition.makeCondition("callBackDate", EntityOperator.LESS_THAN_EQUAL_TO, callBackDateSql));
			}
			if (UtilValidate.isNotEmpty(lastContactDays)) {
				java.sql.Date lastContactDateSql = null;
				int days = Integer.parseInt(lastContactDays);
				reservStart = UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), -1 * days);
				lastContactDateSql = new java.sql.Date(reservStart.getTime());
				EntityCondition tempTypeCondition = EntityCondition.makeCondition("lastContactDate", EntityOperator.EQUALS, lastContactDateSql);
				conditionsList.add(tempTypeCondition);
			}
			List sortBy = null;
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.setLimit(1000);
			sortBy = UtilMisc.toList("-createdDate");
			EntityCondition condition = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			prospectSummaryConfigurationIter = delegator.find("CallRecordMasterSummary", condition, null, null, sortBy, efo);

			List<GenericValue> callOutcomeDescLst = EntityQuery.use(delegator).from("Enumeration").where("enumTypeId", "CALL_OUT_COME").queryList();
			Map<String,String> callOutDescmap = new HashMap<String, String>();
			
			for(GenericValue callOutDesc : callOutcomeDescLst) {
				callOutDescmap.put(callOutDesc.getString("enumId"),callOutDesc.getString("description"));
			}
			List<GenericValue> prospectSummaryConfigurationItemList = prospectSummaryConfigurationIter.getCompleteList();
			prospectSummaryConfigurationIter.close();
			
			
			List<String> salesOpportunityIds = EntityUtil.getFieldListFromEntityList(prospectSummaryConfigurationItemList, "salesOpportunityId", true);
			List<EntityCondition> salesOpportunityConditionList = FastList.newInstance();
			salesOpportunityConditionList.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.IN, salesOpportunityIds));
			EntityCondition salesOpportunityCondition = EntityCondition.makeCondition(salesOpportunityConditionList, EntityOperator.AND);
			List<GenericValue> SalesOpportunityRoleList = EntityQuery.use(delegator).select("salesOpportunityId","ownerId","emplTeamId").from("SalesOpportunityRole").where(salesOpportunityCondition).queryList();
			
			List<String> emplTeamIds = EntityUtil.getFieldListFromEntityList(SalesOpportunityRoleList, "emplTeamId", true);
			List<EntityCondition> emplTeamConditionList = FastList.newInstance();
			emplTeamConditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
			EntityCondition emplTeamCondition = EntityCondition.makeCondition(emplTeamConditionList, EntityOperator.AND);
			List<GenericValue> EmplTeamList = EntityQuery.use(delegator).select("emplTeamId","teamName").from("EmplTeam").where(emplTeamCondition).queryList();
			
			Map<String, String> EmplTeamMap = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(EmplTeamList)) {
				for (GenericValue eachEntry: EmplTeamList) {
					if(UtilValidate.isNotEmpty(eachEntry.getString("teamName"))){
						EmplTeamMap.put(eachEntry.getString("emplTeamId"),eachEntry.getString("teamName"));
					}else{
						EmplTeamMap.put(eachEntry.getString("emplTeamId"),"");
					}
				}
			}
			
			Map<String, String> SalesOpportunityMap = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(SalesOpportunityRoleList)) {
				for (GenericValue eachEntry: SalesOpportunityRoleList) {
					if(UtilValidate.isNotEmpty(eachEntry.getString("salesOpportunityId"))){
						SalesOpportunityMap.put(eachEntry.getString("salesOpportunityId"),eachEntry.getString("ownerId"));
					}else{
						SalesOpportunityMap.put(eachEntry.getString("salesOpportunityId"),"");
					}
				}
			}
			
			Map<String, String> SalesOpportunityMapp = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(SalesOpportunityRoleList)) {
				for (GenericValue eachEntry: SalesOpportunityRoleList) {
					if(UtilValidate.isNotEmpty(eachEntry.getString("salesOpportunityId"))){
						SalesOpportunityMapp.put(eachEntry.getString("salesOpportunityId"),eachEntry.getString("emplTeamId"));
					}else{
						SalesOpportunityMapp.put(eachEntry.getString("salesOpportunityId"),"");
					}
				}
			}
			List<String> partyIds = EntityUtil.getFieldListFromEntityList(SalesOpportunityRoleList, "ownerId", true);
		 	List<EntityCondition> personCondList = FastList.newInstance();
		 	personCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
			EntityCondition personCondition = EntityCondition.makeCondition(personCondList, EntityOperator.AND);
			List<GenericValue> personList = EntityQuery.use(delegator).select("partyId","firstName").from("Person").where(personCondition).queryList();
		 	Map<String, String> partyData = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(personList)) {
				for (GenericValue eachEntry: personList) {
					if(UtilValidate.isNotEmpty(eachEntry.getString("firstName"))){
						partyData.put(eachEntry.getString("partyId"),eachEntry.getString("firstName"));
					}else{
						partyData.put(eachEntry.getString("partyId"),"");
					}
				}
			}
			SimpleDateFormat orgSdfWithTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			SimpleDateFormat orgSdfWithoutTimeStamp = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatedSfdWithTimeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SimpleDateFormat formatedSfdWithoutTimeStamp = new SimpleDateFormat("dd/MM/yyyy");
			for (GenericValue prospectSummaryConfigurationItem : prospectSummaryConfigurationItemList) {
				JSONObject listObj = new JSONObject();

				listObj.put("salesOpportunityId", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("salesOpportunityId"))? prospectSummaryConfigurationItem.getString("salesOpportunityId"): "");
				listObj.put("phoneNumber", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("phoneNumber"))? prospectSummaryConfigurationItem.getString("phoneNumber"): "");
				listObj.put("countryCode", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("countryCode"))? prospectSummaryConfigurationItem.getString("countryCode"): "");
				listObj.put("areaCode", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("areaCode"))? prospectSummaryConfigurationItem.getString("areaCode"): "");
				listObj.put("callBackDate", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("callBackDate"))? getRequiredDateFormat(prospectSummaryConfigurationItem.getString("callBackDate"),orgSdfWithoutTimeStamp, formatedSfdWithoutTimeStamp): "");
				listObj.put("firstName", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("firstName"))? prospectSummaryConfigurationItem.getString("firstName"): "");
				listObj.put("lastName", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("lastName"))? prospectSummaryConfigurationItem.getString("lastName"): "");
				listObj.put("middleName", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("middleName"))? prospectSummaryConfigurationItem.getString("middleName"): "");
				listObj.put("externalReferenceId", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("externalReferenceId"))? prospectSummaryConfigurationItem.getString("externalReferenceId"): "");
				listObj.put("customerSuffix", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("customerSuffix"))? prospectSummaryConfigurationItem.getString("customerSuffix"): "");
				listObj.put("prospectName", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("prospectName"))? prospectSummaryConfigurationItem.getString("prospectName"): "");
				listObj.put("prospectPartyId", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("prospectPartyId"))? prospectSummaryConfigurationItem.getString("prospectPartyId"): "");
				listObj.put("totalCallsByCamp", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("totalCallsByCamp"))? prospectSummaryConfigurationItem.getString("totalCallsByCamp"): "");
				listObj.put("callOutCome", UtilValidate.isNotEmpty(callOutDescmap.get(prospectSummaryConfigurationItem.getString("callOutCome")))? callOutDescmap.get(prospectSummaryConfigurationItem.getString("callOutCome")): "");
				listObj.put("lastCallStatusId", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("lastCallStatusId"))? prospectSummaryConfigurationItem.getString("lastCallStatusId"): "");
				listObj.put("marketingCampaignName", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("marketingCampaignName"))? prospectSummaryConfigurationItem.getString("marketingCampaignName"): "");
				listObj.put("marketingCampaignCode", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("marketingCampaignCode"))? prospectSummaryConfigurationItem.getString("marketingCampaignCode"): "");
				listObj.put("startDate", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("startDate"))? getRequiredDateFormat(prospectSummaryConfigurationItem.getString("startDate"),orgSdfWithTimeStamp, formatedSfdWithTimeStamp): "");
				listObj.put("endDate", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("endDate"))? getRequiredDateFormat(prospectSummaryConfigurationItem.getString("endDate"),orgSdfWithTimeStamp, formatedSfdWithTimeStamp): "");
				listObj.put("lastContactDate", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("lastContactDate"))? getRequiredDateFormat(prospectSummaryConfigurationItem.getString("lastContactDate"),orgSdfWithoutTimeStamp, formatedSfdWithoutTimeStamp): "");
				listObj.put("csrUserLoginId", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("csrUserLoginId"))? prospectSummaryConfigurationItem.getString("csrUserLoginId"): "");
				listObj.put("businessUnitName",UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("businessUnitName"))? prospectSummaryConfigurationItem.getString("businessUnitName"): "");
				listObj.put("businessUnitId", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("businessUnitId"))? prospectSummaryConfigurationItem.getString("businessUnitId"): "");
				listObj.put("createdDate", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("createdDate"))? getRequiredDateFormat(prospectSummaryConfigurationItem.getString("createdDate"),orgSdfWithTimeStamp, formatedSfdWithTimeStamp): "");
				listObj.put("plannedDueDate", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("plannedDueDate"))? getRequiredDateFormat(prospectSummaryConfigurationItem.getString("plannedDueDate"),orgSdfWithTimeStamp, formatedSfdWithTimeStamp): "");
				listObj.put("subject", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("subject"))? prospectSummaryConfigurationItem.getString("subject"): "");
				listObj.put("workEffortId", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("workEffortId"))? prospectSummaryConfigurationItem.getString("workEffortId"): "");
				listObj.put("workEffortTypeId",UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("workEffortTypeId"))? prospectSummaryConfigurationItem.getString("workEffortTypeId"): "");
				listObj.put("callActivityType", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("callActivityType"))? prospectSummaryConfigurationItem.getString("callActivityType"): "");
				listObj.put("callSubActivityId",UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("callSubActivityId"))? prospectSummaryConfigurationItem.getString("callSubActivityId"): "");
				listObj.put("createdBy", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("createdByUserLogin"))? prospectSummaryConfigurationItem.getString("createdByUserLogin"): "");
				listObj.put("lastUpdatedTxStamp", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("lastUpdatedTxStamp"))? getRequiredDateFormat(prospectSummaryConfigurationItem.getString("lastUpdatedTxStamp"),orgSdfWithTimeStamp, formatedSfdWithTimeStamp): "");
				listObj.put("accountId", UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("accountId"))? prospectSummaryConfigurationItem.getString("accountId"): "");
				listObj.put("regardingId",UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("regardingId"))? prospectSummaryConfigurationItem.getString("regardingId"): "");
				
				if(UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("salesOpportunityId")) && UtilValidate.isNotEmpty(SalesOpportunityMap.get(prospectSummaryConfigurationItem.getString("salesOpportunityId")))){
					listObj.put("ownerId", SalesOpportunityMap.get(prospectSummaryConfigurationItem.getString("salesOpportunityId")));
					listObj.put("ownerName", partyData.get(SalesOpportunityMap.get(prospectSummaryConfigurationItem.getString("salesOpportunityId"))));
				}
				
				if(UtilValidate.isNotEmpty(prospectSummaryConfigurationItem.getString("salesOpportunityId")) && UtilValidate.isNotEmpty(SalesOpportunityMapp.get(prospectSummaryConfigurationItem.getString("salesOpportunityId")))){
					listObj.put("emplTeamId", SalesOpportunityMapp.get(prospectSummaryConfigurationItem.getString("salesOpportunityId")));
					listObj.put("emplTeamName", EmplTeamMap.get(SalesOpportunityMapp.get(prospectSummaryConfigurationItem.getString("salesOpportunityId"))));
				}
				dataMap.add(listObj);
			}
			prospectSummaryConfigurationIter.close();
			obj.put("data", dataMap);
			jsonResultMap.put("jsonResult", obj);
		} catch (Exception e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.put("callRecordMasterSummaryMap", jsonResultMap);
		result.putAll(ServiceUtil.returnSuccess("Successfully find Tele Sales Data ..."));
		return result;
	}
	
	
	public static Map<String, Object> getOpportunity(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		Map <String, Object> jsonResultMap = new HashMap<String, Object>();
		
		EntityListIterator opportunitySummaryConfigurationIter = null;
		JSONObject obj = new JSONObject();
		JSONArray dataMap = new JSONArray();

		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String salesEmailAddress = (String) context.get("salesEmailAddress");
		String opportunityName = (String) context.get("opportunityName");
		String salesPhone = (String) context.get("salesPhone");
		String statusOpen = (String) context.get("statusOpen");
		String statusClosed = (String) context.get("statusClosed");
		String statusWon = (String) context.get("statusWon");
		String statusNew = (String) context.get("statusNew");
		String statusLost = (String) context.get("statusLost");
		String statusProgress = (String) context.get("statusProgress");
		String statusContact = (String) context.get("statusContact");
		String statusNotContact = (String) context.get("statusNotContact");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		String responseType = (String) context.get("responseTypeId");
		String callOutCome = (String) context.get("callOutCome");
		String salesChannelId = (String) context.get("salesChannelId");
		List<String> statusIdsList = FastList.newInstance();
		
		try {
			List<EntityCondition> conditionsList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(salesOpportunityId)) {
				EntityCondition tempIdCondition = EntityCondition.makeCondition("salesOpportunityId", EntityOperator.LIKE, salesOpportunityId);
				conditionsList.add(tempIdCondition);
			}
			if (UtilValidate.isNotEmpty(salesEmailAddress)) {
				EntityCondition tempNameCondition = EntityCondition.makeCondition("salesEmailAddress", EntityOperator.LIKE, salesEmailAddress);
				conditionsList.add(tempNameCondition);
			}
			if (UtilValidate.isNotEmpty(opportunityName)) {
				EntityCondition tempTypeCondition = EntityCondition.makeCondition("opportunityName", EntityOperator.LIKE, opportunityName);
				conditionsList.add(tempTypeCondition);
			}
			if (UtilValidate.isNotEmpty(marketingCampaignId)) {
				conditionsList.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.EQUALS, marketingCampaignId));
			}
			if (UtilValidate.isNotEmpty(responseType)) {
				conditionsList.add(EntityCondition.makeCondition("responseType", EntityOperator.EQUALS, responseType));
			}
			if (UtilValidate.isNotEmpty(callOutCome)) {
				conditionsList.add(EntityCondition.makeCondition("callOutCome", EntityOperator.EQUALS, callOutCome));
			}
			if (UtilValidate.isNotEmpty(salesChannelId)) {
				conditionsList.add(EntityCondition.makeCondition("salesChannelId", EntityOperator.EQUALS, salesChannelId));
			}
			if (UtilValidate.isNotEmpty(salesPhone)) {
				EntityCondition tempTypeCondition = EntityCondition.makeCondition("salesPhone", EntityOperator.LIKE, salesPhone);
				conditionsList.add(tempTypeCondition);
			}
			if (UtilValidate.isNotEmpty(statusOpen)) {
				statusIdsList.add(statusOpen);
			}
			if (UtilValidate.isNotEmpty(statusClosed)) {
				statusIdsList.add(statusClosed);
			}
			if (UtilValidate.isNotEmpty(statusWon)) {
				statusIdsList.add(statusWon);
			}
			if (UtilValidate.isNotEmpty(statusNew)) {
				statusIdsList.add(statusNew);
			}
			if (UtilValidate.isNotEmpty(statusLost)) {
				statusIdsList.add(statusLost);
			}
			if (UtilValidate.isNotEmpty(statusProgress)) {
				statusIdsList.add(statusProgress);
			}
			if (UtilValidate.isNotEmpty(statusContact)) {
				statusIdsList.add(statusContact);
			}
			if (UtilValidate.isNotEmpty(statusNotContact)) {
				statusIdsList.add(statusNotContact);
			}
			if (UtilValidate.isNotEmpty(statusIdsList)) {
				conditionsList.add(EntityCondition.makeCondition("opportunityStageId", EntityOperator.IN, statusIdsList));
			}

			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.setLimit(1000);
			efo.setMaxRows(1000);

			EntityCondition condition = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			opportunitySummaryConfigurationIter = delegator.find("SalesOpportunitySummary", condition, null, null, UtilMisc.toList("-createdStamp"), efo);
			
			List<GenericValue> opportunitySummaryConfigurationIterList = opportunitySummaryConfigurationIter.getCompleteList();
			opportunitySummaryConfigurationIter.close();
			List<String> opportunityStageIds = EntityUtil.getFieldListFromEntityList(opportunitySummaryConfigurationIterList, "opportunityStageId", true);
			List<EntityCondition> opportunityStageConditionList = FastList.newInstance();
			opportunityStageConditionList.add(EntityCondition.makeCondition("opportunityStageId", EntityOperator.IN, opportunityStageIds));
			EntityCondition opportunityStageCondition = EntityCondition.makeCondition(opportunityStageConditionList, EntityOperator.AND);
			List<GenericValue> SalesOpportunityStageList = EntityQuery.use(delegator).select("opportunityStageId","description").from("SalesOpportunityStage").where(opportunityStageCondition).queryList();
			
			Map<String, String> SalesOpportunityStageMap = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(SalesOpportunityStageList)) {
				for (GenericValue eachEntry: SalesOpportunityStageList) {
					if(UtilValidate.isNotEmpty(eachEntry.getString("description"))){
						SalesOpportunityStageMap.put(eachEntry.getString("opportunityStageId"),eachEntry.getString("description"));
					}else{
						SalesOpportunityStageMap.put(eachEntry.getString("opportunityStageId"),"");
					}
				}
			}
			
			List<String> salesOpportunityIds = EntityUtil.getFieldListFromEntityList(opportunitySummaryConfigurationIterList, "salesOpportunityId", true);
			List<EntityCondition> salesOpportunityConditionList = FastList.newInstance();
			salesOpportunityConditionList.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.IN, salesOpportunityIds));
			EntityCondition salesOpportunityCondition = EntityCondition.makeCondition(salesOpportunityConditionList, EntityOperator.AND);
			List<GenericValue> SalesOpportunityRoleList = EntityQuery.use(delegator).select("salesOpportunityId","ownerId","emplTeamId").from("SalesOpportunityRole").where(salesOpportunityCondition).queryList();
			List<String> salesOpportunityOwnerIds = EntityUtil.getFieldListFromEntityList(SalesOpportunityRoleList, "ownerId", true);
			List<EntityCondition> salesOpportunityOwnerConditionList = FastList.newInstance();
			salesOpportunityOwnerConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, salesOpportunityOwnerIds));
			EntityCondition salesOpportunityOwnerCondition = EntityCondition.makeCondition(salesOpportunityOwnerConditionList, EntityOperator.AND);
			List<GenericValue> personList = EntityQuery.use(delegator).select("partyId","firstName").from("Person").where(salesOpportunityOwnerCondition).queryList();
			Map<String, String> salesOpportunityRoleMap = new HashMap<String, String>();
			Map<String, String> salesOpportunityRoleMapp = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(SalesOpportunityRoleList)) {
				for (GenericValue eachEntry: SalesOpportunityRoleList) {
					salesOpportunityRoleMap.put(eachEntry.getString("salesOpportunityId"),eachEntry.getString("ownerId"));
					salesOpportunityRoleMapp.put(eachEntry.getString("salesOpportunityId"),eachEntry.getString("emplTeamId"));
				}
			}
			Map<String, String> personMap = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(personList)) {
				for (GenericValue eachEntry: personList) {
					personMap.put(eachEntry.getString("partyId"),eachEntry.getString("firstName"));
				}
			}
			for (GenericValue opportunitySummaryConfigurationItem : opportunitySummaryConfigurationIterList) {
				JSONObject listObj = new JSONObject();
				listObj.put("opportunityName", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("opportunityName"))? opportunitySummaryConfigurationItem.getString("opportunityName"): "");
				listObj.put("firstName", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("firstName"))? opportunitySummaryConfigurationItem.getString("firstName"): "");
				listObj.put("salesOpportunityId", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("salesOpportunityId"))? opportunitySummaryConfigurationItem.getString("salesOpportunityId"): "");
				
				String opportunityStageDesc = "";
				String opportunityStageId = opportunitySummaryConfigurationItem.getString("opportunityStageId");
				listObj.put("opportunityStageId", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("opportunityStageId"))? opportunitySummaryConfigurationItem.getString("opportunityStageId"): "");
				if(UtilValidate.isNotEmpty(opportunityStageId) && UtilValidate.isNotEmpty(SalesOpportunityStageMap.get(opportunityStageId))){
					listObj.put("opportunityStageDesc", SalesOpportunityStageMap.get(opportunityStageId));
				}else{
					listObj.put("opportunityStageDesc", "");
				}
				
				listObj.put("customerCin", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("customerCin"))? opportunitySummaryConfigurationItem.getString("customerCin"): "");
				listObj.put("customerSuffix", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("customerSuffix"))? opportunitySummaryConfigurationItem.getString("customerSuffix"): "");
				listObj.put("callOutCome", opportunitySummaryConfigurationItem.getString("callOutCome"));
				listObj.put("callOutComeName", opportunitySummaryConfigurationItem.getString("callOutComeName"));
				listObj.put("prospectId", opportunitySummaryConfigurationItem.getString("prospectId"));
				listObj.put("responseType", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("responseType"))? opportunitySummaryConfigurationItem.getString("responseType"): "");
				listObj.put("responseTypeName", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("responseTypeName"))? opportunitySummaryConfigurationItem.getString("responseTypeName"): "");
				listObj.put("opportunityStatusId", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("opportunityStatusId"))? opportunitySummaryConfigurationItem.getString("opportunityStatusId"): "");
				listObj.put("estimatedAmount", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("estimatedAmount"))? opportunitySummaryConfigurationItem.getString("estimatedAmount"): "");
				listObj.put("assignedUserLoginId", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("assignedUserLoginId"))? opportunitySummaryConfigurationItem.getString("assignedUserLoginId"): "");
				listObj.put("responseReasonId", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("responseReasonId"))? opportunitySummaryConfigurationItem.getString("responseReasonId"): "");
				listObj.put("responseReasonName", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("responseReasonName"))? opportunitySummaryConfigurationItem.getString("responseReasonName"): "");
				listObj.put("businessUnitName", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("businessUnitName"))? opportunitySummaryConfigurationItem.getString("businessUnitName"): "");
				listObj.put("remarks", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("remarks"))? opportunitySummaryConfigurationItem.getString("remarks"): "");
				listObj.put("createdByUserLogin", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("createdByUserLogin"))? opportunitySummaryConfigurationItem.getString("createdByUserLogin"): "");
				listObj.put("modifiedByUserLogin", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("modifiedByUserLogin"))? opportunitySummaryConfigurationItem.getString("modifiedByUserLogin"): "");
				listObj.put("prospectExtId", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("prospectExtId"))? opportunitySummaryConfigurationItem.getString("prospectExtId"): "");
				listObj.put("currencyUomId", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("currencyUomId"))? opportunitySummaryConfigurationItem.getString("currencyUomId"): "");
				listObj.put("emplTeamId", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("emplTeamId"))? opportunitySummaryConfigurationItem.getString("emplTeamId"): "");
				listObj.put("businessUnitId",UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("businessUnitId"))? opportunitySummaryConfigurationItem.getString("businessUnitId"): "");
				listObj.put("ownerId", UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("ownerId"))? opportunitySummaryConfigurationItem.getString("ownerId"): "");
				
				if(UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("salesOpportunityId"))){
					if(UtilValidate.isNotEmpty(salesOpportunityRoleMap.get(opportunitySummaryConfigurationItem.getString("salesOpportunityId")))){
						if(UtilValidate.isNotEmpty(personMap.get(salesOpportunityRoleMap.get(opportunitySummaryConfigurationItem.getString("salesOpportunityId"))))){
							listObj.put("ownerName",personMap.get(salesOpportunityRoleMap.get(opportunitySummaryConfigurationItem.getString("salesOpportunityId"))));
						}
					}
				}
				
				if(UtilValidate.isNotEmpty(opportunitySummaryConfigurationItem.getString("salesOpportunityId"))){
					if(UtilValidate.isNotEmpty(salesOpportunityRoleMapp.get(opportunitySummaryConfigurationItem.getString("salesOpportunityId")))){
						if(UtilValidate.isNotEmpty(salesOpportunityRoleMapp.get(opportunitySummaryConfigurationItem.getString("salesOpportunityId")))){
							listObj.put("emplTeamIdd",salesOpportunityRoleMapp.get(opportunitySummaryConfigurationItem.getString("salesOpportunityId")));
						}
					}
				}
				dataMap.add(listObj);
			}
			opportunitySummaryConfigurationIter.close();
			obj.put("data", dataMap);
			jsonResultMap.put("jsonResult", obj);
		} catch (Exception e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}	
		result.put("salesOpportunitySummaryMap", jsonResultMap);
		result.putAll(ServiceUtil.returnSuccess("Successfully find Opportunities..."));
		return result;
	}
	
	
	public static Map<String, Object> getActivity(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		Map <String, Object> jsonResultMap = new HashMap<String, Object>();
		
		EntityListIterator activSummaryConfigurationIter = null;
		JSONObject obj = new JSONObject();
		JSONArray dataMap = new JSONArray();

		String workEffortId = (String) context.get("workEffortId");
		String primOwnerId = (String) context.get("primOwnerId");
		String createdByUserLogin = (String) context.get("createdByUserLogin");
		String workEffortServiceType = (String) context.get("workEffortServiceType");
		String workEffortSubServiceType = (String) context.get("workEffortSubServiceType");
		String statusopen = (String) context.get("statusopen");
		String statuscompleted = (String) context.get("statuscompleted");
		String currentStatusId = (String) context.get("currentStatusId");
		
		try{
			List<EntityCondition> conditionsList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(workEffortId)) {
				EntityCondition tempIdCondition = EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,workEffortId);
				conditionsList.add(tempIdCondition);
			}
			if (UtilValidate.isNotEmpty(primOwnerId)) {
				EntityCondition tempNameCondition = EntityCondition.makeCondition("primOwnerId", EntityOperator.EQUALS,primOwnerId);
				conditionsList.add(tempNameCondition);
			}
			if (UtilValidate.isNotEmpty(createdByUserLogin)) {
				EntityCondition tempTypeCondition = EntityCondition.makeCondition("createdByUserLogin",EntityOperator.EQUALS, createdByUserLogin);
				conditionsList.add(tempTypeCondition);
			}
			if (UtilValidate.isNotEmpty(workEffortServiceType)) {
				EntityCondition tempTypeCondition = EntityCondition.makeCondition("workEffortServiceType",EntityOperator.EQUALS, workEffortServiceType);
				conditionsList.add(tempTypeCondition);
			}
			if (UtilValidate.isNotEmpty(workEffortSubServiceType)) {
				EntityCondition tempTypeCondition = EntityCondition.makeCondition("workEffortSubServiceType",EntityOperator.EQUALS, workEffortSubServiceType);
				conditionsList.add(tempTypeCondition);
			}
			if (UtilValidate.isNotEmpty(currentStatusId)) {
				EntityCondition tempIdCondition = EntityCondition.makeCondition("currentStatusId",EntityOperator.EQUALS, currentStatusId);
				conditionsList.add(tempIdCondition);
			}
			if ((UtilValidate.isNotEmpty(statusopen)) || (UtilValidate.isNotEmpty(statuscompleted))) {
				EntityCondition tempIdCondition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, statusopen),
						EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, statuscompleted)),
						EntityOperator.OR);
				conditionsList.add(tempIdCondition);
			}

			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.setLimit(500);

			EntityCondition condition = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			
			activSummaryConfigurationIter = delegator.find("WorkEffortCallSummary", condition, null, null, UtilMisc.toList("-createdStamp"), efo);
			
			List<GenericValue> activSummaryConfigurationIterList = activSummaryConfigurationIter.getCompleteList();
			activSummaryConfigurationIter.close();
			List<String> workEffortServiceTypeIds = EntityUtil.getFieldListFromEntityList(activSummaryConfigurationIterList, "workEffortServiceType", true);
			List<String> workEffortSubServiceTypeIds = EntityUtil.getFieldListFromEntityList(activSummaryConfigurationIterList, "workEffortSubServiceType", true);
			workEffortServiceTypeIds.addAll(workEffortSubServiceTypeIds);
			
			List<EntityCondition> workEffortServiceTypeConditionList = FastList.newInstance();
			workEffortServiceTypeConditionList.add(EntityCondition.makeCondition("code", EntityOperator.IN, workEffortServiceTypeIds));
			EntityCondition workEffortServiceTypeCondition = EntityCondition.makeCondition(workEffortServiceTypeConditionList, EntityOperator.AND);
			Set <String> fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("code");
			fieldsToSelect.add("value");
			
			List<GenericValue> WorkEffortAssocTripletList = EntityQuery.use(delegator).select(fieldsToSelect).from("WorkEffortAssocTriplet").where(workEffortServiceTypeCondition).queryList();
		 	Map<String, String> WorkEffortAssocTripletMap = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(WorkEffortAssocTripletList)) {
				for (GenericValue eachEntry: WorkEffortAssocTripletList) {
					WorkEffortAssocTripletMap.put(eachEntry.getString("code"),eachEntry.getString("value"));
				}
			}
			
			List<String> primOwnerIds = EntityUtil.getFieldListFromEntityList(activSummaryConfigurationIterList, "primOwnerId", true);
			List<EntityCondition> primOwnerIdList = FastList.newInstance();
			primOwnerIdList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, primOwnerIds));
			EntityCondition primOwnerIdCondition = EntityCondition.makeCondition(primOwnerIdList, EntityOperator.AND);
			List<GenericValue> personList = EntityQuery.use(delegator).select("partyId","firstName").from("Person").where(primOwnerIdCondition).queryList();
			Map<String, String> personMap = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(personList)) {
				for (GenericValue eachEntry: personList) {
					personMap.put(eachEntry.getString("partyId"),eachEntry.getString("firstName"));
				}
			}
			
			List<String> emplTeamIds = EntityUtil.getFieldListFromEntityList(activSummaryConfigurationIterList, "emplTeamId", true);
			List<EntityCondition> emplTeamConditionList = FastList.newInstance();
			emplTeamConditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
			EntityCondition emplTeamCondition = EntityCondition.makeCondition(emplTeamConditionList, EntityOperator.AND);
			List<GenericValue> emplTeamList = EntityQuery.use(delegator).select("emplTeamId","teamName").from("EmplTeam").where(emplTeamCondition).queryList();
			Map<String, String> emplTeamMap = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(emplTeamList)) {
				for (GenericValue eachEntry: emplTeamList) {
					emplTeamMap.put(eachEntry.getString("emplTeamId"),eachEntry.getString("teamName"));
				}
			}
			for (GenericValue activSummaryConfigurationItem : activSummaryConfigurationIterList) {
				JSONObject listObj = new JSONObject();

				listObj.put("workEffortId", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("workEffortId"))? activSummaryConfigurationItem.getString("workEffortId"): "");
				listObj.put("currentStatusId", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("currentStatusId"))? activSummaryConfigurationItem.getString("currentStatusId"): "");

				String statusDesc = "";
				GenericValue statusDetails = delegator.findOne("StatusItem",UtilMisc.toMap("statusId", activSummaryConfigurationItem.getString("currentStatusId")), true);
				if (statusDetails != null) {
					statusDesc = statusDetails.getString("description");
					listObj.put("statusDesc", statusDesc);
				}

				listObj.put("workEffortParentId",UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("workEffortParentId"))? activSummaryConfigurationItem.getString("workEffortParentId"): "");
				listObj.put("workEffortServiceType",UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("workEffortServiceType"))? activSummaryConfigurationItem.getString("workEffortServiceType"): "");
				listObj.put("workEffortSubServiceType",UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("workEffortSubServiceType"))? activSummaryConfigurationItem.getString("workEffortSubServiceType"): "");
				String workEffortServiceTypeDesc = activSummaryConfigurationItem.getString("workEffortServiceType");
				
				if(UtilValidate.isNotEmpty(workEffortServiceTypeDesc) && UtilValidate.isNotEmpty(WorkEffortAssocTripletMap.get(workEffortServiceTypeDesc))){
					listObj.put("workEffortServiceTypeDescription", WorkEffortAssocTripletMap.get(workEffortServiceTypeDesc));
				}else{
					listObj.put("workEffortServiceTypeDescription", "");
				}
				
	            String workEffortSubServiceTypeDesc = activSummaryConfigurationItem.getString("workEffortSubServiceType");
				
	            if(UtilValidate.isNotEmpty(workEffortSubServiceTypeDesc) && UtilValidate.isNotEmpty(WorkEffortAssocTripletMap.get(workEffortSubServiceTypeDesc))){
					listObj.put("workEffortSubServiceTypeDescription", WorkEffortAssocTripletMap.get(workEffortSubServiceTypeDesc));
				}else{
					listObj.put("workEffortSubServiceTypeDescription", "");
				}
	            
			    listObj.put("description", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("description"))? activSummaryConfigurationItem.getString("description"): "");
				listObj.put("accountNumber", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("accountNumber"))? activSummaryConfigurationItem.getString("accountNumber"): "");
				listObj.put("productName", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("productName"))? activSummaryConfigurationItem.getString("productName"): "");
				listObj.put("campaignCode", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("campaignCode"))? activSummaryConfigurationItem.getString("campaignCode"): "");
				listObj.put("plannedDuration", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("plannedDuration"))? activSummaryConfigurationItem.getString("plannedDuration"): "");
				listObj.put("businessUnitName", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("businessUnitName"))? activSummaryConfigurationItem.getString("businessUnitName"): "");
				listObj.put("estimatedStartDate", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("estimatedStartDate"))? activSummaryConfigurationItem.getString("estimatedStartDate"): "");
				listObj.put("estimatedCompletionDate", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("estimatedCompletionDate"))? activSummaryConfigurationItem.getString("estimatedCompletionDate"): "");
				listObj.put("actualCompletionDate", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("actualCompletionDate"))? activSummaryConfigurationItem.getString("actualCompletionDate"): "");
				listObj.put("primOwnerId", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("primOwnerId"))? activSummaryConfigurationItem.getString("primOwnerId"): "");
				listObj.put("wfOnceDone", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("wfOnceDone"))? activSummaryConfigurationItem.getString("wfOnceDone"): "");
				listObj.put("createdSourceBy", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("createdSourceBy"))? activSummaryConfigurationItem.getString("createdSourceBy"): "");
				listObj.put("createdByUserLogin", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("createdByUserLogin"))? activSummaryConfigurationItem.getString("createdByUserLogin"): "");
				listObj.put("lastModifiedDate", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("lastModifiedDate"))? activSummaryConfigurationItem.getString("lastModifiedDate"): "");
				listObj.put("createdDate", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("createdDate"))? activSummaryConfigurationItem.getString("createdDate"): "");
				listObj.put("lastModifiedByUserLogin", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("lastModifiedByUserLogin"))? activSummaryConfigurationItem.getString("lastModifiedByUserLogin"): "");
				listObj.put("lastUpdatedSource", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("lastUpdatedSource"))? activSummaryConfigurationItem.getString("lastUpdatedSource"): "");
				listObj.put("resolution", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("resolution"))? activSummaryConfigurationItem.getString("resolution"): "");
				listObj.put("actualStartDate", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("actualStartDate"))? activSummaryConfigurationItem.getString("actualStartDate"): "");
				listObj.put("actualDuration", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("actualDuration"))? activSummaryConfigurationItem.getString("actualDuration"): "");
				listObj.put("overDue", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("overDue"))? activSummaryConfigurationItem.getString("overDue"): "");
				listObj.put("businessUnitId", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("businessUnitId"))? activSummaryConfigurationItem.getString("businessUnitId"): "");
				listObj.put("emplTeamId", UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("emplTeamId"))? activSummaryConfigurationItem.getString("emplTeamId"): "");	
				if(UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("emplTeamId")) && UtilValidate.isNotEmpty(emplTeamMap.get(activSummaryConfigurationItem.getString("emplTeamId")))){
					listObj.put("emplTeamName", emplTeamMap.get(activSummaryConfigurationItem.getString("emplTeamId")));
				}else{
					listObj.put("emplTeamName", "");
				}
				if(UtilValidate.isNotEmpty(activSummaryConfigurationItem.getString("primOwnerId")) && UtilValidate.isNotEmpty(personMap.get(activSummaryConfigurationItem.getString("primOwnerId")))){
					listObj.put("primOwnerName", personMap.get(activSummaryConfigurationItem.getString("primOwnerId")));
				}else{
					listObj.put("primOwnerName", "");
				}
				dataMap.add(listObj);
			}
			activSummaryConfigurationIter.close();
			obj.put("data", dataMap);
			jsonResultMap.put("jsonResult", obj);
			result.put("workEffortCallSummaryMap", jsonResultMap);
		} catch (Exception e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}	
		result.putAll(ServiceUtil.returnSuccess("Successfully find Sales Activity..."));
		return result;
	}
	
	
	public static Map<String, Object> getMyCall(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		Map <String, Object> data = new HashMap<String, Object>();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("csrUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")));
			long myActivities = delegator.findCountByCondition("CallRecordMasterSummary", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null);
			data.put("ownerCount", String.valueOf(myActivities));

			java.sql.Timestamp dayStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
			java.sql.Timestamp dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());

			conditions.clear();
			conditions.add(EntityCondition.makeCondition(
					EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.toTimestamp(dayStart)),
					EntityOperator.AND, EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toTimestamp(dayEnd))));

			conditions.add(EntityCondition.makeCondition("csrUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")));

			long ownerCountToday = delegator.findCountByCondition("CallRecordMasterSummary", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null);
			data.put("ownerCountToday", String.valueOf(ownerCountToday));

			GenericValue EmplTeam = org.fio.homeapps.util.DataUtil.getPartyIdentification(delegator, userLogin.getString("partyId"), "TEAM");
			conditions.clear();
			//conditions.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, EmplTeam.getString("attrValue")));
			conditions.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, EmplTeam.getString("idValue")));
			long myTeamActivities = delegator.findCountByCondition("CallRecordMasterSummary", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null);
			data.put("ownerTeamCount", String.valueOf(myTeamActivities));

			conditions.clear();
			conditions.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toTimestamp(dayStart)));
			conditions.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, EmplTeam.getString("idValue")));
			//conditions.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, EmplTeam.getString("attrValue")));
			conditions.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toTimestamp(dayEnd)));

			long teamCountToday = delegator.findCountByCondition("CallRecordMasterSummary", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null);
			data.put("teamCountToday", String.valueOf(teamCountToday));

		} catch (Exception e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}	
		result.put("CallRecordMasterSummaryMap", data);
		result.putAll(ServiceUtil.returnSuccess("Successfully find Sales Activity..."));
		return result;
	}

	
	public static Map<String, Object> getAlertCategoryData(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String alertTypeId = (String) context.get("alertTypeId");
		String alertCategoryId = (String) context.get("alertCategoryId");

		try{
			List<EntityCondition> conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(alertCategoryId)) {
				conditionlist.add(EntityCondition.makeCondition("alertCategoryId", EntityOperator.EQUALS, alertCategoryId));
			} else {
				conditionlist.add(EntityCondition.makeCondition("alertTypeId", EntityOperator.EQUALS, alertTypeId));
			}

			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			List<GenericValue> alertCategories = EntityQuery.use(delegator).from("AlertCategory").where(condition).orderBy("-seqNum").queryList();
			
			List<String> alertPriorityIds = EntityUtil.getFieldListFromEntityList(alertCategories, "alertPriority", true);
			List<EntityCondition> alertPriorityConditionList = FastList.newInstance();
			alertPriorityConditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.IN, alertPriorityIds));
			EntityCondition alertPriorityCondition = EntityCondition.makeCondition(alertPriorityConditionList, EntityOperator.AND);
			Set <String> fieldsToSelectt = new TreeSet < String > ();
			fieldsToSelectt.add("enumId");
			fieldsToSelectt.add("description");
			
			List<GenericValue> alertPriorityList = EntityQuery.use(delegator).select(fieldsToSelectt).from("Enumeration").where(alertPriorityCondition).queryList();
		 	Map<String, String> alertPriorityMap = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(alertPriorityList)) {
				for (GenericValue eachEntry: alertPriorityList) {
					alertPriorityMap.put(eachEntry.getString("enumId"),eachEntry.getString("description"));
				}
			}
			
			if (UtilValidate.isNotEmpty(alertCategories)) {
				for (GenericValue eachCategory : alertCategories) {
					Map<String, Object> data = new HashMap<String, Object>();
					String isActive = eachCategory.getString("isActive");
					String alertPriority = eachCategory.getString("alertPriority");
					data.put("alertPriority", eachCategory.getString("alertPriority"));
					data.put("alertTypeId", eachCategory.getString("alertTypeId"));
					data.put("alertCategoryId", eachCategory.getString("alertCategoryId"));
					data.put("alertCategoryName", eachCategory.getString("alertCategoryName"));
					data.put("alertAutoClosure", eachCategory.getString("alertAutoClosure"));
					data.put("alertAutoClosureDuration", eachCategory.getString("alertAutoClosureDuration"));
					data.put("isActive", eachCategory.getString("isActive"));
					if (UtilValidate.isNotEmpty(isActive) && isActive.equals("Y")) {
						data.put("isActiveDesc", "Active");
					} else if (UtilValidate.isNotEmpty(isActive) && isActive.equals("N")) {
						data.put("isActiveDesc", "In Active");
					} else {
						data.put("isActiveDesc", "");
					}
					if (UtilValidate.isNotEmpty(alertPriorityMap.get(alertPriority))){
							data.put("alertPriorityDesc",alertPriorityMap.get(alertPriority));
					}else{
						data.put("alertPriorityDesc", "");
					}
					data.put("seqNum", eachCategory.getString("seqNum"));
					data.put("remarks", eachCategory.getString("remarks"));
					resultsList.add(data);
				}
				resultMap.put("result", resultsList);
			}
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	
	public static Map<String, Object> getCustomerAlertDetails(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String alertEntityReferenceId = (String) context.get("alertEntityReferenceId");
		String alertPriority = (String) context.get("alertPriority");
		String alertTrackingId = (String) context.get("alertTrackingId");
		String customerCin = (String) context.get("customerCin");
		
		try {
			List<EntityCondition> conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(alertPriority)) {
				List<GenericValue> alerCategoryList = EntityQuery.use(delegator).from("AlertCategory").where("alertPriority", alertPriority).queryList();
				if (UtilValidate.isNotEmpty(alerCategoryList)) {
					List<String> alertCategoryIds = EntityUtil.getFieldListFromEntityList(alerCategoryList,"alertCategoryId", true);
					conditionlist.add(EntityCondition.makeCondition("alertCategoryId", EntityOperator.IN, alertCategoryIds));
				}
			}
			if (UtilValidate.isNotEmpty(alertEntityReferenceId)) {
				conditionlist.add(EntityCondition.makeCondition("alertEntityReferenceId", EntityOperator.EQUALS, alertEntityReferenceId));
			}else if(UtilValidate.isNotEmpty(customerCin)){
				List<String> alertEntityReferenceIds = FastList.newInstance();
				List<GenericValue> salesOpportunitySummaryList = EntityQuery.use(delegator).select("salesOpportunityId").from("SalesOpportunitySummary").where("customerCin", customerCin).queryList();
				if (UtilValidate.isNotEmpty(salesOpportunitySummaryList)) {
					for (GenericValue eachData : salesOpportunitySummaryList) {
						String salesOppIds = eachData.getString("salesOpportunityId");
						alertEntityReferenceIds.add(salesOppIds);
					}
				}
				List<GenericValue> custRequestSrSummaryList = EntityQuery.use(delegator).select("custRequestId").from("CustRequestSrSummary").where("cinNumber", customerCin).queryList();
				if (UtilValidate.isNotEmpty(custRequestSrSummaryList)) {
					for (GenericValue eachData : custRequestSrSummaryList) {
						String custRequestIds = eachData.getString("custRequestId");
						alertEntityReferenceIds.add(custRequestIds);
					}
				}
				List<GenericValue> workEffortCallSummaryList = EntityQuery.use(delegator).select("workEffortId").from("WorkEffortCallSummary").where("cinNumber", customerCin).queryList();
				if (UtilValidate.isNotEmpty(workEffortCallSummaryList)) {
					for (GenericValue eachData : workEffortCallSummaryList) {
						String workEffortIds = eachData.getString("workEffortId");
						alertEntityReferenceIds.add(workEffortIds);
					}
				}
				if (UtilValidate.isNotEmpty(alertEntityReferenceIds)) {
					conditionlist
					.add(EntityCondition.makeCondition("alertEntityReferenceId", EntityOperator.IN, alertEntityReferenceIds));
				}
			}
			if (UtilValidate.isNotEmpty(alertTrackingId)) {
				conditionlist.add(EntityCondition.makeCondition("alertTrackingId", EntityOperator.EQUALS, alertTrackingId));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
			List<GenericValue> alertData = EntityQuery.use(delegator).from("AlertTrackingHistory").where(condition).orderBy("-createdDate").queryList();
			
			List<String> alertEntityReferenceIds = EntityUtil.getFieldListFromEntityList(alertData, "alertEntityReferenceId", true);
			List<EntityCondition> alertEntityReferenceConditionList = FastList.newInstance();
			alertEntityReferenceConditionList.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.IN, alertEntityReferenceIds));
			EntityCondition alertPriorityCondition = EntityCondition.makeCondition(alertEntityReferenceConditionList, EntityOperator.AND);
			Set <String> fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("salesOpportunityId");
			fieldsToSelect.add("customerCin");
			fieldsToSelect.add("firstName");
			
			List<GenericValue> SalesOpportunitySummaryList = EntityQuery.use(delegator).select(fieldsToSelect).from("SalesOpportunitySummary").where(alertPriorityCondition).queryList();
		 	Map<String, String> SalesOpportunitySummaryMap = new HashMap<String, String>();
		 	Map<String, String> SalesOpportunitySummaryMapp = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(SalesOpportunitySummaryList)) {
				for (GenericValue eachEntry: SalesOpportunitySummaryList) {
					SalesOpportunitySummaryMap.put(eachEntry.getString("salesOpportunityId"),eachEntry.getString("customerCin"));
					SalesOpportunitySummaryMapp.put(eachEntry.getString("salesOpportunityId"),eachEntry.getString("firstName"));
				}
			}
			
			alertEntityReferenceConditionList.clear();
			alertEntityReferenceConditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, alertEntityReferenceIds));
			alertPriorityCondition = null;
			alertPriorityCondition = EntityCondition.makeCondition(alertEntityReferenceConditionList, EntityOperator.AND);
			
			List<GenericValue> CustRequestSrSummaryList = EntityQuery.use(delegator).select("custRequestId","cinNumber","customerName").from("CustRequestSrSummary").where(alertPriorityCondition).queryList();
		 	Map<String, String> CustRequestSrSummaryMap = new HashMap<String, String>();
		 	Map<String, String> CustRequestSrSummaryMapp = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(CustRequestSrSummaryList)) {
				for (GenericValue eachEntry: CustRequestSrSummaryList) {
					CustRequestSrSummaryMap.put(eachEntry.getString("salesOpportunityId"),eachEntry.getString("cinNumber"));
					CustRequestSrSummaryMapp.put(eachEntry.getString("salesOpportunityId"),eachEntry.getString("customerName"));
				}
			}
			
			List<String> alertTrackingIds = FastList.newInstance();
			
			List<String> alertCategoryIds = EntityUtil.getFieldListFromEntityList(alertData, "alertCategoryId", true);
			List<EntityCondition> alertCategoryConditionList = FastList.newInstance();
			alertCategoryConditionList.add(EntityCondition.makeCondition("alertCategoryId", EntityOperator.IN, alertCategoryIds));
			EntityCondition contactMechCondition = EntityCondition.makeCondition(alertCategoryConditionList, EntityOperator.AND);
			List<GenericValue> AlertCategoryList = EntityQuery.use(delegator).select("alertCategoryId", "alertCategoryName", "alertPriority", "isActive", "remarks").from("AlertCategory").where(contactMechCondition).queryList();
			
			List<String> alertPriorityIds = EntityUtil.getFieldListFromEntityList(AlertCategoryList, "alertPriority", true);
			List<EntityCondition> alertPriorityConditionList = FastList.newInstance();
			alertPriorityConditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.IN, alertPriorityIds));
			EntityCondition alertPriorityConditionn = EntityCondition.makeCondition(alertPriorityConditionList, EntityOperator.AND);
			
			List<GenericValue> EnumerationList = EntityQuery.use(delegator).select("enumId","description").from("Enumeration").where(alertPriorityConditionn).queryList();
		 	Map<String, String> EnumerationMap = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(EnumerationList)) {
				for (GenericValue eachEntry: EnumerationList) {
					EnumerationMap.put(eachEntry.getString("enumId"),eachEntry.getString("description"));
				}
			}
	        
			if (UtilValidate.isNotEmpty(alertData)) {
				for (GenericValue eachData : alertData) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("alertTrackingId", eachData.getString("alertTrackingId"));
					data.put("alertTrackingSequence", eachData.getString("alertTrackingSequence"));
					data.put("alertCategoryId", eachData.getString("alertCategoryId"));
					data.put("alertInfo", eachData.getString("alertInfo"));
					data.put("alertEntityName", eachData.getString("alertEntityName"));
					data.put("alertEntityReferenceId", eachData.getString("alertEntityReferenceId"));
					data.put("alertCreatedBy", eachData.getString("alertCreatedBy"));
					data.put("createdByUserLogin", eachData.getString("createdByUserLogin"));
					data.put("alertModifiedBy", eachData.getString("alertModifiedBy"));
					if (UtilValidate.isNotEmpty(eachData.getTimestamp("alertCreatedOn"))){
						String dateStr = UtilDateTime.toDateString(eachData.getTimestamp("alertCreatedOn"),"dd/MM/yyyy hh:mm");
						data.put("alertCreatedOn", dateStr);
					}
					if (UtilValidate.isNotEmpty(eachData.getTimestamp("alertStartDate"))){
						String dateStr = UtilDateTime.toDateString(eachData.getTimestamp("alertStartDate"),"dd/MM/yyyy hh:mm");
						data.put("alertStartDate", dateStr);
					}
					if (UtilValidate.isNotEmpty(eachData.getTimestamp("alertEndDate"))){
						String dateStr = UtilDateTime.toDateString(eachData.getTimestamp("alertEndDate"),"dd/MM/yyyy hh:mm");
						data.put("alertEndDate", dateStr);
					}
					if (UtilValidate.isNotEmpty(eachData.getTimestamp("createdDate"))){
						String dateStr = UtilDateTime.toDateString(eachData.getTimestamp("createdDate"),"dd/MM/yyyy hh:mm");
						data.put("createdDate", dateStr);
					}
					if (UtilValidate.isNotEmpty(eachData.getTimestamp("alertModifiedOn"))){
						String dateStr = UtilDateTime.toDateString(eachData.getTimestamp("createdDate"),"dd/MM/yyyy hh:mm");
						data.put("alertModifiedOn", dateStr);
					}
					if (UtilValidate.isEmpty(alertTrackingIds) || (UtilValidate.isNotEmpty(alertTrackingIds) && !(alertTrackingIds.contains(eachData.getString("alertTrackingId"))))) {
						alertTrackingIds.add(eachData.getString("alertTrackingId"));
					} else {
						continue;
					}
					String alertCategoryDesc = "";
					String priorityDesc = "";
					String alertPriorityId = "";
					String refId = eachData.getString("alertEntityReferenceId").trim();
					String refName = eachData.getString("alertEntityName");
					String customerId = "";
					String customerName = "";
					String remarks = "";

					if (UtilValidate.isNotEmpty(refName) && refName.equals("SALES_OPPORTUNITY")) {
						if (UtilValidate.isNotEmpty(SalesOpportunitySummaryMap.get(refId))) {
							customerId = SalesOpportunitySummaryMap.get(refId);
							customerName = SalesOpportunitySummaryMapp.get(refId);
						}
					} else if (UtilValidate.isNotEmpty(refName) && refName.equals("SERVICE_REQUEST")) {
						if (UtilValidate.isNotEmpty(CustRequestSrSummaryMap.get(refId))) {
							customerId = CustRequestSrSummaryMap.get(refId);
							customerName = CustRequestSrSummaryMapp.get(refId);
						}
					}
					
					EntityCondition authCondition = EntityCondition.makeCondition("alertCategoryId", eachData.getString("alertCategoryId"));
					List<GenericValue> categoryDetailss = EntityUtil.filterByCondition(AlertCategoryList, authCondition);
					GenericValue categoryDetails = EntityUtil.getFirst(categoryDetailss);
					
					if (UtilValidate.isNotEmpty(categoryDetails)) {
						alertCategoryDesc = categoryDetails.getString("alertCategoryName"); 
						alertPriorityId = categoryDetails.getString("alertPriority");
						String isActive = categoryDetails.getString("isActive");
						remarks = categoryDetails.getString("remarks");
						if (UtilValidate.isNotEmpty(isActive) && isActive.equals("Y")) {
							data.put("isActiveDesc", "Active");
						} else if (UtilValidate.isNotEmpty(isActive) && isActive.equals("N")) {
							data.put("isActiveDesc", "In Active");
						} else {
							data.put("isActiveDesc", "");
						}
						if (UtilValidate.isNotEmpty(alertPriorityId) && UtilValidate.isNotEmpty(EnumerationMap.get(alertPriorityId))) {
							priorityDesc = EnumerationMap.get(alertPriorityId);
						}
					}
					data.put("remarks", remarks);
					data.put("customerId", customerId);
					data.put("customerName", customerName);
					data.put("alertPriorityId", alertPriorityId);
					data.put("alertCategoryDesc", alertCategoryDesc);
					data.put("priorityDesc", priorityDesc);
					resultsList.add(data);
				}
			}
			resultMap.put("result", resultsList);
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			result.put("resultMap", resultMap);
			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	
	public static Map<String, Object> getActivityCommunicationInfo(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String externalId = (String) context.get("workEffortId");
        String phoneNumber="";
        String emailAddr="";
        String Address="";
        String emailSolicitation="";
        String phoneSolicitation="";
        String addressSolicitation="";
        int operSrCount = 0;
        int opportunitiesCount = 0;
    	Map<String, Object> data = new HashMap<String, Object> ();
    	
    	try {
    		if (UtilValidate.isNotEmpty(externalId)) {
    			GenericValue CustRequestSrSummary = delegator.findOne("WorkEffortCallSummary",UtilMisc.toMap("workEffortId", externalId), false);
    			if (CustRequestSrSummary != null) {
    				String cinNumber = CustRequestSrSummary.getString("cinNumber");
    				if (cinNumber != null) {
    					EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
    							EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, cinNumber));
    					GenericValue partyIdentification = EntityUtil.getFirst(delegator.findList("PartyIdentification", condition, null, UtilMisc.toList("-createdStamp"), null, false));
    					if (UtilValidate.isNotEmpty(partyIdentification)) {
    						String partyId = partyIdentification.getString("partyId");
    						if (UtilValidate.isNotEmpty(partyId)) {
    							List<GenericValue> partyContactMechList = EntityQuery.use(delegator).from("PartyContactMech").where("partyId", partyId).queryList();

    							GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", partyId).queryOne();
    							data.put("partyId", partyId);
    							String custName = "";
    							if (UtilValidate.isNotEmpty(person)) {
    								if (UtilValidate.isNotEmpty(person.getString("firstName"))) {
    									String firstName = person.getString("firstName");
    									if (UtilValidate.isNotEmpty(firstName)) {
    										data.put("firstName", firstName);
    										custName = firstName;
    									}
    								}
    								if (UtilValidate.isNotEmpty(person.getString("middleName"))) {
    									String middleName = person.getString("middleName");
    									if (UtilValidate.isNotEmpty(middleName)) {
    										data.put("middleName", middleName);
    										custName = custName + " " + middleName;
    									}
    								}
    								if (UtilValidate.isNotEmpty(person.getString("lastName"))) {
    									String lastName = person.getString("lastName");
    									if (UtilValidate.isNotEmpty(lastName)) {
    										data.put("lastName", lastName);
    										custName = custName + " " + lastName;
    									}
    								}
    								if (UtilValidate.isNotEmpty(person.getString("nationalId"))) {
    									String nationalNo = person.getString("nationalId");
    									if (UtilValidate.isNotEmpty(nationalNo)) {
    										data.put("nationalNo", nationalNo);
    									}
    								}
    							}
    							List conditionsList = FastList.newInstance();
    							if (UtilValidate.isNotEmpty(partyContactMechList)) {
    								List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(partyContactMechList, "contactMechId", true);
    								conditionsList.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds));
    							}
    							EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
    							List<GenericValue> ContactMechList = EntityQuery.use(delegator).select("contactMechId","contactMechTypeId","infoString").from("ContactMech").where(mainConditons).queryList();
    							
    							List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(ContactMechList, "contactMechId", true);
    							List<EntityCondition> contactMechConditionList = FastList.newInstance();
    							contactMechConditionList.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds));
    							EntityCondition contactMechCondition = EntityCondition.makeCondition(contactMechConditionList, EntityOperator.AND);
    							List<GenericValue> PartyContactMechList = EntityQuery.use(delegator).select("contactMechId","allowSolicitation").from("PartyContactMech").where(contactMechCondition).queryList();
    							
    							Map<String, String> PartyContactMechMap = new HashMap<String, String>();
    							if (UtilValidate.isNotEmpty(PartyContactMechList)) {
    								for (GenericValue eachEntry: PartyContactMechList) {
    									PartyContactMechMap.put(eachEntry.getString("contactMechId"),eachEntry.getString("allowSolicitation"));
    								}
    							}
    							
    							if (UtilValidate.isNotEmpty(partyContactMechList)) {
    								for (GenericValue eachContactMech: ContactMechList) {
    									String contactMechTypeId = eachContactMech.getString("contactMechTypeId");
    									if (contactMechTypeId.equals("EMAIL_ADDRESS")) {
    										emailAddr = eachContactMech.getString("infoString");
    										if (UtilValidate.isNotEmpty(PartyContactMechMap.get(eachContactMech.getString("contactMechId")))) {
    											emailSolicitation = PartyContactMechMap.get(eachContactMech.getString("contactMechId"));
    										}else{
    											emailSolicitation = "N";
    										}
    									}
    									if (contactMechTypeId.equals("TELECOM_NUMBER")) {
    										phoneNumber = eachContactMech.getString("infoString");
    										if (UtilValidate.isNotEmpty(PartyContactMechMap.get(eachContactMech.getString("contactMechId")))) {
    											phoneSolicitation = PartyContactMechMap.get(eachContactMech.getString("contactMechId"));
    										}else{
    											phoneSolicitation = "N";
    										}
    									}
    									if (contactMechTypeId.equals("POSTAL_ADDRESS")) {
    										Address = eachContactMech.getString("infoString");
    										if (UtilValidate.isNotEmpty(PartyContactMechMap.get(eachContactMech.getString("contactMechId")))) {
    											addressSolicitation = PartyContactMechMap.get(eachContactMech.getString("contactMechId"));
    										}else{
    											addressSolicitation = "N";
    										}
    									}
    								}
    							}

    							GenericValue roleIdentification = EntityUtil
    									.getFirst(delegator.findList("RoleTypeAndParty", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, false));
    							String customerType = "";
    							String customerTypeId = "";
    							String prospectNo = "";
    							String vPlusNo = "";
    							String cifNo = "";
    							if (UtilValidate.isNotEmpty(roleIdentification)) {
    								customerType = roleIdentification.getString("description");
    								customerTypeId = roleIdentification.getString("roleTypeId");
    								if(UtilValidate.isNotEmpty(customerTypeId) && customerTypeId.equals("NON_CRM")){
    									vPlusNo = cinNumber;
    								}
    								if(UtilValidate.isNotEmpty(customerTypeId) && customerTypeId.equals("PROSPECT")){
    									prospectNo = cinNumber;
    								}
    								if(UtilValidate.isNotEmpty(customerTypeId) && customerTypeId.equals("CUSTOMER")){
    									cifNo = cinNumber;
    								}
    							}

    							conditionsList.clear();
    							conditionsList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId));
    							conditionsList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_OPEN"));
    							EntityCondition mainCondition = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
    							List < GenericValue > custRequest = EntityQuery.use(delegator).from("CustRequest").select("custRequestId").where(mainCondition).queryList();
    							if(UtilValidate.isNotEmpty(custRequest)){
    								operSrCount=custRequest.size();
    							}

    							conditionsList.clear();
    							conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
    							EntityCondition roleCnd = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
    							List < GenericValue > salesOpportunityRoleList = EntityQuery.use(delegator).from("SalesOpportunityRole").select("salesOpportunityId").where(roleCnd).queryList();
    							if(UtilValidate.isNotEmpty(salesOpportunityRoleList)){
    								opportunitiesCount=salesOpportunityRoleList.size();
    							}

    							data.put("opportunitiesCount", opportunitiesCount);
    							data.put("operSrCount", operSrCount);
    							data.put("custName", custName);
    							data.put("cifNo", cifNo);
    							data.put("prospectNo", prospectNo);
    							data.put("vPlusNo", vPlusNo);
    							data.put("customerType", customerType);
    							data.put("customerTypeId", customerTypeId);
    							data.put("phoneNumber", phoneNumber);
    							data.put("emailAddr", emailAddr);
    							data.put("Address", Address);
    							data.put("phoneSolicitation", phoneSolicitation);
    							data.put("emailSolicitation", emailSolicitation);
    							data.put("addressSolicitation", addressSolicitation);
    							resultsList.add(data);
    						}
    					}
    				}
    			}
    			resultMap.put("result", resultsList);
    		}
    	}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
    	result.put("resultMap", resultMap);
		return result;
	}
	
	public static Map<String, Object> createCustomerAlert(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String alertCategoryName = (String) context.get("alertCategoryName");
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String alertTypeId = (String) context.get("alertTypeId");
		String alertPriority = (String) context.get("alertPriority");
		String alertStatusId = (String) context.get("alertStatusId");
		String alertAutoClosure = (String) context.get("alertAutoClosure");
		String alertAutoClosureDuration = (String) context.get("alertAutoClosureDuration");
		String remarks = (String) context.get("remarks");
		String alertEntityName = (String) context.get("alertEntityName");
		String alertEntityReferenceId = (String) context.get("alertEntityReferenceId");
		String alertInfo = (String) context.get("alertInfo");
		String alertStartDate = (String) context.get("alertStartDate");
		String alertCategoryId = (String) context.get("alertCategoryId");
		String alertEndDate = (String) context.get("alertEndDate");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		String srNumber = (String) context.get("srNumber");
		Timestamp timeStampEndDate = null;
		Timestamp timeStampStartDate = null;		
		String responseMessage = UtilProperties.getMessage(RESOURCE, "CustomerAlertCreationSuccessful", locale);
		Map<String, Object> results = ServiceUtil.returnSuccess(responseMessage);
		String userLoginId = null;
		try {

			if (UtilValidate.isNotEmpty(alertStartDate)) {
				alertStartDate = df1.format(df2.parse(alertStartDate));
				timeStampStartDate = UtilDateTime.getDayStart(Timestamp.valueOf(alertStartDate));
			}
			if (UtilValidate.isNotEmpty(alertEndDate)) {
				alertEndDate = df1.format(df2.parse(alertEndDate));
				timeStampEndDate = UtilDateTime.getDayEnd(Timestamp.valueOf(alertEndDate));
			}

			GenericValue alertTrackingHistory = delegator.makeValue("AlertTrackingHistory");
			
			String alertTrackingId = delegator.getNextSeqId("AlertTrackingHistory");
			alertTrackingId = UtilGenerator.getAlertTrackingNumber(delegator, alertTrackingId);
			
			alertTrackingHistory.set("alertTrackingId",alertTrackingId);
			alertTrackingHistory.set("alertEntityName", alertEntityName);
			alertTrackingHistory.set("alertEntityReferenceId", alertEntityReferenceId);
			alertTrackingHistory.set("alertCategoryId", alertCategoryId);
			alertTrackingHistory.set("alertInfo", alertInfo);
			alertTrackingHistory.set("alertStartDate", timeStampStartDate);
			alertTrackingHistory.set("alertEndDate", timeStampEndDate);
			alertTrackingHistory.set("createdDate", UtilDateTime.nowTimestamp());
			userLoginId = userLogin.getString("userLoginId");
			alertTrackingHistory.put("createdByUserLogin", userLoginId);
			alertTrackingHistory.put("alertCreatedOn", UtilDateTime.nowTimestamp());
			alertTrackingHistory.put("alertCreatedBy", userLoginId);

			try {
				delegator.setNextSubSeqId(alertTrackingHistory, "alertTrackingSequence", 5, 1);
	            delegator.create(alertTrackingHistory);
	        } catch (GenericEntityException e) {
	            return ServiceUtil.returnError("Customer Alert Creation Failed");
	        }
			results.put("alertTrackingId", alertTrackingId);
			results.put("salesOpportunityId", salesOpportunityId);
			results.put("srNumber", srNumber);
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			results = ServiceUtil.returnError("Error : "+e.getMessage());
		}
		return results;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createSalesOpportunityDetails(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String opportunityName = (String) context.get("opportunityName");
		String typeEnumId = (String) context.get("typeEnumId");
		BigDecimal estimatedAmount = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(context.get("estimatedAmount"))) {
			 estimatedAmount = (BigDecimal) context.get("estimatedAmount");
		}
		String remarks = (String) context.get("remarks");
		String dataSourceId = (String) context.get("dataSourceId");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		String dataSourceDataId = (String) context.get("dataSourceDataId");
		String product = (String) context.get("productId");
		String partyId = (String) context.get("partyId");
		String roleTypeId = (String) context.get("roleTypeId");
		String opportunityState = (String) context.get("opportunityState");
		String cNo = (String) context.get("cNo");
		
		String responseMessage = UtilProperties.getMessage(RESOURCE, "SalesOpportunitySuccessfullyCreated.!", locale);
	    Map < String, Object > results = ServiceUtil.returnSuccess(responseMessage);
	    Map outMap = FastMap.newInstance();
	    Map outRoleMap = FastMap.newInstance();
	    String salesOpportunityId = "";
		try {
			if(UtilValidate.isNotEmpty(typeEnumId)) {
				Map inputMap = FastMap.newInstance();
				String callOutCome = "";
				GenericValue enumerationList = EntityQuery.use(delegator).select("enumId").from("Enumeration").where("description", "NEW", "enumTypeId", "CALL_OUT_COME").queryFirst();
				if(UtilValidate.isNotEmpty(enumerationList)) {
					callOutCome = enumerationList.getString("enumId");
				}
				inputMap.put("callOutCome",callOutCome);
				inputMap.put("opportunityName",opportunityName);
				inputMap.put("estimatedAmount",estimatedAmount);
				inputMap.put("typeEnumId",typeEnumId);
				inputMap.put("remarks",remarks);
				inputMap.put("dataSourceId",dataSourceId);
				inputMap.put("marketingCampaignId",marketingCampaignId);
				inputMap.put("dataSourceDataId",dataSourceDataId);
				inputMap.put("userLogin",userLogin);
				inputMap.put("product",product);
				inputMap.put("opportunityState",opportunityState);
				inputMap.put("createdDate",UtilDateTime.nowTimestamp());
				inputMap.put("opportunityStageId", "SOSTG_OPEN");

				salesOpportunityId = UtilGenerator.getSalesOpportunityNumber(delegator, delegator.getNextSeqId("SalesOpportunity"));
                inputMap.put("salesOpportunityId", salesOpportunityId);
				outMap = dispatcher.runSync("createSalesOpportunity", inputMap);
				if(ServiceUtil.isError(outMap) || ServiceUtil.isFailure(outMap)){
                    responseMessage = UtilProperties.getMessage(RESOURCE, "SalesOpportunityCreationFailed", locale);
                    return results;
                }
				if(UtilValidate.isNotEmpty(salesOpportunityId)) {
					String externalId = salesOpportunityId;
					GenericValue salesOpp = EntityQuery.use(delegator).from("SalesOpportunity").where("salesOpportunityId", salesOpportunityId).queryOne();
					if (salesOpp != null && UtilValidate.isNotEmpty(externalId)) {
						salesOpp.put("externalId", externalId);
						salesOpp.store();
		            }
				}
				if(UtilValidate.isNotEmpty(cNo)) {
	    	        GenericValue partyIdentification = EntityUtil.getFirst(delegator.findList("PartyIdentification", EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, cNo), null, UtilMisc.toList("-createdStamp"), null, false));
	    	        if (UtilValidate.isNotEmpty(partyIdentification)) {
	    	        	partyId = partyIdentification.getString("partyId");
	    	        }
				}
				GenericValue roleIdentification = EntityUtil
						.getFirst(delegator.findList("RoleTypeAndParty", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, false));
    	        if (UtilValidate.isNotEmpty(roleIdentification)) {
    	        	roleTypeId = roleIdentification.getString("roleTypeId");
    	        }
    	        
				if(UtilValidate.isNotEmpty(salesOpportunityId) && UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(roleTypeId)) {
					Map inputRoleMap = FastMap.newInstance();
					inputRoleMap.put("salesOpportunityId",salesOpportunityId);
					inputRoleMap.put("partyId",partyId);
					inputRoleMap.put("roleTypeId",roleTypeId);
					inputRoleMap.put("userLogin",userLogin);
					inputRoleMap.put("ownerId",userLogin.getString("partyId"));
					String owner = userLogin.getString("partyId");
					String ownerBu = "";
					GenericValue userLoginPerson = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId", userLogin.getString("userLoginId")).queryOne();
					if (UtilValidate.isNotEmpty(userLoginPerson)) {
						ownerBu = userLoginPerson.getString("businessUnit");
	    	        }
					inputRoleMap.put("ownerBu",ownerBu);
					if(UtilValidate.isNotEmpty(owner)){
						GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", owner).queryList());
				  		if (UtilValidate.isNotEmpty(emplTeam) && UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId"))) {    
				  			String emplTeamId = emplTeam.getString("emplTeamId");
							String businessUnit = emplTeam.getString("businessUnit");
							if(UtilValidate.isNotEmpty(emplTeamId)) {
								inputRoleMap.put("emplTeamId", emplTeamId);
					    	}
							if(UtilValidate.isNotEmpty(businessUnit)) {
								inputRoleMap.put("ownerBu",businessUnit);
					    	}
				  		}
					}
					outRoleMap = dispatcher.runSync("createSalesOpportunityRole", inputRoleMap);
					if(ServiceUtil.isError(outRoleMap) || ServiceUtil.isFailure(outRoleMap)){
	                    responseMessage = UtilProperties.getMessage(RESOURCE, "SalesOpportunityCreationFailed", locale);
	                    return results;
	                }
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			results = ServiceUtil.returnError("Error : "+e.getMessage());
		}
		results.put("salesOpportunityId", salesOpportunityId);
		return results;
	}
	
	
	public static Map<String, Object> getOwnerTeam(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String emplTeamId = (String) context.get("emplTeamId");
		String businessUnitId = (String) context.get("businessUnitId");
		
		try {
			if (UtilValidate.isNotEmpty(emplTeamId)) {
				List<GenericValue> userLoginPersonList = EntityQuery.use(delegator).select("userLoginId","partyId","firstName").from("UserLoginPerson").where("emplTeamId", emplTeamId).queryList();
				if (UtilValidate.isNotEmpty(userLoginPersonList)) {
					for (GenericValue userLoginPerson : userLoginPersonList) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("userLoginId", UtilValidate.isNotEmpty(userLoginPerson.getString("userLoginId"))? userLoginPerson.getString("userLoginId") : "");
						data.put("partyName", UtilValidate.isNotEmpty(userLoginPerson.getString("firstName"))? userLoginPerson.getString("firstName") : "");
						data.put("partyId", UtilValidate.isNotEmpty(userLoginPerson.getString("partyId"))? userLoginPerson.getString("partyId") : "");
						resultsList.add(data);
					}
					resultMap.put("result", resultsList);
					result.put("resultMap", resultMap);
				}
			}

			if (UtilValidate.isNotEmpty(businessUnitId)) {
				List<GenericValue> emplTeamList = EntityQuery.use(delegator).from("EmplTeam").where("businessUnit", businessUnitId).queryList();
				if (UtilValidate.isNotEmpty(emplTeamList)) {
					for (GenericValue emplTeam : emplTeamList) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("emplTeamId", UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId"))? emplTeam.getString("emplTeamId") : "");
						data.put("teamName", UtilValidate.isNotEmpty(emplTeam.getString("teamName"))? emplTeam.getString("teamName") : "");
						resultsList.add(data);
					}
					resultMap.put("result", resultsList);
					result.put("resultMap", resultMap);
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		return result;
	}
	
	
	public static Map<String, Object> eventCreateSrActivity(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();

		String workEffortType = (String) context.get("workEffortTypeId");
		String workEffortPurposeTypeId = (String) context.get("workEffortPurposeTypeId");
		String srTypeId = (String) context.get("srTypeId");
		String srSubTypeId = (String) context.get("srSubTypeId");
		String dateStr = (String) context.get("taskDate");
		String callDateTimeStr = (String) context.get("callDateTime");
		String callTime = (String) context.get("callTime");
		String callBackDateStr = (String) context.get("callBackDate");
		String actualStartDateStr = (String) context.get("actualStartDate");
		String actualCompletionDateStr = (String) context.get("actualCompletionDate");
		String estimatedStartDateStr = (String) context.get("estimatedStartDate");
		String estimatedCompletionDateStr = (String) context.get("estimatedCompletionDate");
		String subject = (String) context.get("subject");
		String resolution = (String) context.get("resolution");
		String linkedFrom = (String) context.get("linkedFrom");
		String productId = (String) context.get("productId");
		String account = (String) context.get("account");
		String accountProduct = (String) context.get("accountProduct");
		String emailFormContent = (String) context.get("emailFormContent");
		String onceDone = (String) context.get("onceDone");
		String messages = (String) context.get("messages");
		String location = (String) context.get("location");
		String startTimeStr = (String) context.get("startTime");
		String duration = (String) context.get("duration");
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String ownerPartyId = (String) context.get("owner");
		String emplTeamId = (String) context.get("emplTeamId");
		String currentStatusId = (String) context.get("currentStatusId");
		String ownerBU = (String) context.get("ownerBu");
		String customerCIN = (String) context.get("cNo");
		String priority = (String) context.get("priority");
		String direction = (String) context.get("direction");
		String entityTimeZoneId = (String) context.get("entityTimeZoneId");
		String phoneNumber = (String) context.get("phoneNumber");
		String nsender = (String) context.get("nsender");
		String nto = (String) context.get("nto");
		String ncc = (String) context.get("ncc");
		String nbcc = (String) context.get("nbcc");
		String template = (String) context.get("template");
		/*String optionalAttendees = (String) context.get("optionalAttendees");
		String requiredAttendees = (String) context.get("requiredAttendees");*/
		List<String> optionalAttendees = (List<String>) context.get("optionalAttendees");
		List<String> requiredAttendees = (List<String>) context.get("requiredAttendees");
		String nrecepient = (String) context.get("nrecepient");
		String norganizer = (String) context.get("norganizer");
		String isPhoneCall = (String) context.get("isPhoneCall");
		String domainEntityType = (String) context.get("domainEntityType");
		String domainEntityId = (String) context.get("domainEntityId");
		List<String> ownerList = (List<String>) context.get("ownerList");
		String ownerBookedCalSlots = (String) context.get("ownerBookedCalSlots");
		String createdDateStr = (String) context.get("createdDate");

		String responseMessage = UtilProperties.getMessage(RESOURCE, "SalesActivityCreatedSuccessfully", locale);
		Map<String, Object> inMap = FastMap.newInstance();
		Map<String, Object> outMap = FastMap.newInstance();
		GenericValue callRecordMaster = null;

		try{
			
			String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);

			Timestamp date = null;
			if(UtilValidate.isNotEmpty(dateStr)){
				SimpleDateFormat sdf = new SimpleDateFormat(globalDateTimeFormat);
				date = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
			}
			if(UtilValidate.isNotEmpty(startTimeStr)){
				SimpleDateFormat sdf = new SimpleDateFormat(globalDateTimeFormat);
				date = new java.sql.Timestamp(sdf.parse(startTimeStr).getTime());
			}
			
			ownerBU = UtilValidate.isNotEmpty(ownerBU) ? ownerBU : null;

			Timestamp callDate = null;
			if(UtilValidate.isNotEmpty(callDateTimeStr)){
				SimpleDateFormat sdf =null;
				if(UtilValidate.isNotEmpty(callTime)){

					callDateTimeStr = callDateTimeStr + " " +callTime;
					sdf = new SimpleDateFormat(globalDateTimeFormat);

				}else{

					 sdf = new SimpleDateFormat(globalDateFormat);
				}
				callDate = new java.sql.Timestamp(sdf.parse(callDateTimeStr).getTime());

			}
			String callBackDateString =  null;
			SimpleDateFormat inSDF = new SimpleDateFormat(globalDateFormat);
			SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-MM-dd");
			if(UtilValidate.isNotEmpty(callBackDateStr)) {
				Date callBackDateDt = inSDF.parse(callBackDateStr);
				callBackDateString = outSDF.format(callBackDateDt);
			}
			Timestamp actualStartDate = null;
			if(UtilValidate.isNotEmpty(actualStartDateStr)){
				SimpleDateFormat sdf = new SimpleDateFormat(globalDateTimeFormat);
				actualStartDate = new java.sql.Timestamp(sdf.parse(actualStartDateStr).getTime());
			}
			Timestamp actualCompletionDate = null;
			if(UtilValidate.isNotEmpty(actualCompletionDateStr)){
				SimpleDateFormat sdf = new SimpleDateFormat(globalDateTimeFormat);
				actualCompletionDate = new java.sql.Timestamp(sdf.parse(actualCompletionDateStr).getTime());
			}
			Timestamp estimatedStartDate = null;
			if(UtilValidate.isNotEmpty(estimatedStartDateStr)){
				SimpleDateFormat sdf = new SimpleDateFormat(globalDateTimeFormat);
				estimatedStartDate = new java.sql.Timestamp(sdf.parse(estimatedStartDateStr).getTime());
			}
			Timestamp estimatedCompletionDate = null;
			if(UtilValidate.isNotEmpty(estimatedCompletionDateStr)){
				SimpleDateFormat sdf = new SimpleDateFormat(globalDateTimeFormat);
				estimatedCompletionDate = new java.sql.Timestamp(sdf.parse(estimatedCompletionDateStr).getTime());
			}
			
			Timestamp createdDate = null;
			if(UtilValidate.isNotEmpty(createdDateStr)){
				SimpleDateFormat sdf = new SimpleDateFormat(globalDateFormat);
				createdDate = new java.sql.Timestamp(sdf.parse(createdDateStr).getTime());
			}

			if (UtilValidate.isEmpty(messages) || messages == null) {
				if (UtilValidate.isNotEmpty(emailFormContent)) {
					try {
						messages = Base64.getEncoder().encodeToString(emailFormContent.getBytes("utf-8"));
						inMap.put("description", messages);
					} catch (UnsupportedEncodingException e) {
						Debug.log(e.getMessage());
					}
				}
			} else {
				try {
					messages = Base64.getEncoder().encodeToString(messages.getBytes("utf-8"));
					inMap.put("description", messages);
				} catch (UnsupportedEncodingException e) {
					Debug.log(e.getMessage());
				}
			}
			GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", ownerPartyId).queryList());
			if (UtilValidate.isNotEmpty(ownerPartyId)&&UtilValidate.isNotEmpty(emplTeam)) { 
				if (UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId")))
					emplTeamId = emplTeam.getString("emplTeamId");
			}

			GenericValue workTypeIdentification = null;
			String workEffortTypeId = "";
			if (UtilValidate.isNotEmpty(workEffortType)) {
				List<EntityCondition> conditionlist = FastList.newInstance();
				conditionlist.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS, workEffortType));
				EntityCondition workTypeCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				workTypeIdentification = EntityQuery.use(delegator).from("WorkEffortType").where(workTypeCondition).queryFirst();
			}

			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND, EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, customerCIN));

			GenericValue partyIdentification = EntityUtil.getFirst(delegator.findList("PartyIdentification", condition, null, UtilMisc.toList("-createdStamp"), null, false));

			inMap.put("userLogin",userLogin);
			if(UtilValidate.isNotEmpty(workTypeIdentification)) {
				workEffortTypeId = workTypeIdentification.getString("workEffortTypeId");
				inMap.put("workEffortTypeId", workEffortTypeId);			
			}
			if(UtilValidate.isNotEmpty(emplTeamId)) {
				inMap.put("emplTeamId", emplTeamId);
			}
			if(UtilValidate.isNotEmpty(currentStatusId)) {
				inMap.put("currentStatusId", currentStatusId);
			}
			if(UtilValidate.isNotEmpty(srTypeId)) {
				inMap.put("workEffortServiceType", srTypeId);
			}
			if(UtilValidate.isNotEmpty(srSubTypeId)) {
				inMap.put("workEffortSubServiceType", srSubTypeId);
			}
			if (UtilValidate.isNotEmpty(direction)) {
				inMap.put("direction", direction);
			}
			if(UtilValidate.isNotEmpty(subject)) {
				inMap.put("workEffortName", subject);
			}
			if(UtilValidate.isNotEmpty(date)) {
				inMap.put("estimatedStartDate", date);
			}
			
			if(UtilValidate.isNotEmpty(actualCompletionDate)) {
			inMap.put("actualCompletionDate", actualCompletionDate);
			}
			if(UtilValidate.isNotEmpty(estimatedCompletionDate)) {
			inMap.put("estimatedCompletionDate", estimatedCompletionDate);
			}
			if(UtilValidate.isNotEmpty(estimatedStartDate)) {
			inMap.put("estimatedStartDate", estimatedStartDate);
			}
			if(UtilValidate.isNotEmpty(actualStartDate)) {
			inMap.put("actualStartDate", actualStartDate);
			}
			if (UtilValidate.isNotEmpty(optionalAttendees)) {
				inMap.put("noptional", UtilMisc.toList(optionalAttendees));
			}
			if (UtilValidate.isNotEmpty(requiredAttendees)) {
				inMap.put("nrequired", UtilMisc.toList(requiredAttendees));
			}
			if (UtilValidate.isNotEmpty(norganizer)) {
				inMap.put("norganizer", UtilMisc.toList(norganizer));
			}
			if (UtilValidate.isNotEmpty(nrecepient)) {
				inMap.put("nrecipient", UtilMisc.toList(nrecepient));
			}
			if (UtilValidate.isNotEmpty(phoneNumber)) {
				inMap.put("phoneNumber", phoneNumber);
			}
			if(UtilValidate.isNotEmpty(resolution)) {
				inMap.put("resolution", resolution);
			}
			if (UtilValidate.isNotEmpty(priority)) {
				inMap.put("priority", Long.valueOf(priority));
			}
			if(UtilValidate.isNotEmpty(onceDone)) {
				inMap.put("wfOnceDone", onceDone);
			}
			if (UtilValidate.isNotEmpty(nsender)) {
				inMap.put("nsender", UtilMisc.toList(nsender));
			}
			if (UtilValidate.isNotEmpty(nto)) {
				inMap.put("nto", UtilMisc.toList(StringUtil.split(nto, ",")));
			}
			if (UtilValidate.isNotEmpty(ncc)) {
				inMap.put("ncc", UtilMisc.toList(StringUtil.split(ncc, ",")));
			}
			if (UtilValidate.isNotEmpty(nbcc)) {
				inMap.put("nbcc", UtilMisc.toList(nbcc));
			}
			if (UtilValidate.isNotEmpty(template)) {
				inMap.put("wftMsdsubjecttemplate", template);
			}
			if(UtilValidate.isNotEmpty(messages)) {
				inMap.put("message", messages);
			}
			if (UtilValidate.isNotEmpty(ownerPartyId)) {
				inMap.put("primOwnerId", ownerPartyId);
				inMap.put("ownerPartyId", ownerPartyId);	
			}
			if (UtilValidate.isNotEmpty(ownerList)) {
				inMap.put("ownerList", ownerList);	
			}
			if(UtilValidate.isNotEmpty((String) context.get("ownerRoleTypeId"))) {
				inMap.put("ownerRoleTypeId", (String) context.get("ownerRoleTypeId"));
			}
			
			if (UtilValidate.isNotEmpty(ownerBU)) {
				inMap.put("wftMsdbusinessunit", ownerBU);
				inMap.put("businessUnitId", ownerBU);
			}
			if(UtilValidate.isNotEmpty(location)){
				inMap.put("wftLocation",location);
			}
			if(UtilValidate.isNotEmpty(duration)){
				inMap.put("wftMsdduration",duration);
			}
			inMap.put("endPointType", "OFCRM");
			String partyId = null;
			if (UtilValidate.isNotEmpty(partyIdentification)) {
				partyId = partyIdentification.getString("partyId");
			}
			if(UtilValidate.isNotEmpty(customerCIN)){
				inMap.put("partyId", customerCIN);
				if (UtilValidate.isEmpty(partyId)) {
					partyId = customerCIN;
				}
			}else if (UtilValidate.isNotEmpty(partyId)) {
				inMap.put("partyId", partyId);
			}
			if(UtilValidate.isNotEmpty(workEffortPurposeTypeId)){
				inMap.put("workEffortPurposeTypeId", workEffortPurposeTypeId);
			}
			/*EntityCondition rolecondition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "_NA_"));

			GenericValue roleIdentification = EntityUtil.getFirst(delegator.findList("PartyRole", rolecondition, null, null, null, false));
			String roleTypeId = null;
			if (UtilValidate.isNotEmpty(roleIdentification)) {
				roleTypeId = roleIdentification.getString("roleTypeId");
			}*/
			inMap.put("createdDate", createdDate);
			
			GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
			String roleTypeId = party.getString("roleTypeId");
			
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				if ("CUSTOMER".equalsIgnoreCase(roleTypeId)) {
					inMap.put("roleTypeId", "CUSTOMER");
				}else if ("PROSPECT".equalsIgnoreCase(roleTypeId)) {
					inMap.put("roleTypeId", "02");
				}else if ("NON_CRM".equalsIgnoreCase(roleTypeId)) {
					inMap.put("roleTypeId", "07");
				} else {
					inMap.put("roleTypeId", roleTypeId);
				}
			}
			
			inMap.put("endPointType", "OFCRM");
			inMap.put("domainEntityType", domainEntityType);
			inMap.put("domainEntityId", domainEntityId);
			inMap.put("entityTimeZoneId", entityTimeZoneId);
			inMap.put("ownerBookedCalSlots", ownerBookedCalSlots);
			
			outMap = dispatcher.runSync("crmPortal.createInteractiveActivity", inMap);
			
			if(!ServiceUtil.isSuccess(outMap)) {
				responseMessage = UtilProperties.getMessage(RESOURCE,ServiceUtil.getErrorMessage(outMap), locale);
			}
			String workEffortId = (String) outMap.get("workEffortId");
			
			/*if (UtilValidate.isNotEmpty(workEffortId) && !(workEffortId.equals(null)) && UtilValidate.isNotEmpty(salesOpportunityId)) {
				GenericValue salesOpportunityWorkEffort = delegator.makeValue("SalesOpportunityWorkEffort");
				salesOpportunityWorkEffort.set("salesOpportunityId", salesOpportunityId);
				salesOpportunityWorkEffort.set("workEffortId", workEffortId);
				salesOpportunityWorkEffort.create();
				result.put("salesOpportunityId", salesOpportunityId);
			}*/
			if (UtilValidate.isNotEmpty(workEffortId) && !(workEffortId.equals(null)) && UtilValidate.isNotEmpty(isPhoneCall) && isPhoneCall.equals("Y")) {
				Timestamp callBackDate = null;
				String marketingCampaignId = "";
				String entityReferenceId = "";
				String entityReferenceTypeId = "";
				String callOutCome = "";
				String responseTypeId = "";
				String responseReasonId = "";
				String opportunityStageId = "";
				if (UtilValidate.isNotEmpty(salesOpportunityId)){
					GenericValue salesOpportunityList = EntityQuery.use(delegator).select("opportunityStageId","callBackDate","marketingCampaignId","callOutCome","responseTypeId","responseReasonId").from("SalesOpportunity")
							.where("salesOpportunityId", salesOpportunityId).queryOne();
					if (UtilValidate.isNotEmpty(salesOpportunityList)){
						marketingCampaignId = salesOpportunityList.getString("marketingCampaignId");
						callOutCome = salesOpportunityList.getString("callOutCome");
						responseTypeId = salesOpportunityList.getString("responseTypeId");
						responseReasonId = salesOpportunityList.getString("responseReasonId");
						opportunityStageId = salesOpportunityList.getString("opportunityStageId");
					}

					GenericValue enumList = EntityQuery.use(delegator).select("enumId").from("Enumeration").where("enumTypeId", "CALL_RECORD_ENTITY_TYPE","enumCode","SALES_OPPURTUNITY_TYPE").queryFirst();
					if (UtilValidate.isNotEmpty(enumList)){
						entityReferenceTypeId = enumList.getString("enumId");
					}
					entityReferenceId = salesOpportunityId;
				}else{
					EntityCondition enumCnd = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CALL_RECORD_ENTITY_TYPE"),
							EntityCondition.makeCondition("enumCode", EntityOperator.NOT_EQUAL, "SALES_OPPURTUNITY_TYPE"));

					GenericValue enumList = EntityUtil.getFirst(delegator.findList("Enumeration", enumCnd, null, null, null, false));

					if (UtilValidate.isNotEmpty(enumList)){
						entityReferenceTypeId = enumList.getString("enumId");
					}
					entityReferenceId = delegator.getNextSeqId("CallRecordMaster");
				}
				GenericValue callRecordMasterList = EntityQuery.use(delegator).from("CallRecordMaster").where(UtilMisc.toMap("entityReferenceId",salesOpportunityId, "entityReferenceTypeId",entityReferenceTypeId)).queryFirst();

				if (UtilValidate.isEmpty(callRecordMasterList)) {
					callRecordMaster = delegator.makeValue("CallRecordMaster");
					callRecordMaster.set("callRecordId", delegator.getNextSeqId("CallRecordMaster"));
					callRecordMaster.set("entityReferenceId", entityReferenceId);
					callRecordMaster.set("entityReferenceTypeId", entityReferenceTypeId);
					if(UtilValidate.isNotEmpty(workEffortId)){
						callRecordMaster.set("workEffortId", workEffortId);
					}
					if(UtilValidate.isNotEmpty(workEffortTypeId)){
						callRecordMaster.set("workEffortTypeId", workEffortTypeId);
					}
					if(UtilValidate.isNotEmpty(marketingCampaignId)){
						callRecordMaster.set("marketingCampaignId", marketingCampaignId);
					}
					callRecordMaster.set("partyId", partyId);
					if(UtilValidate.isNotEmpty(customerCIN)){
						callRecordMaster.set("externalReferenceId", customerCIN);
						callRecordMaster.set("externalReferenceTypeId", "CIF");
					}
					callRecordMaster.set("createdDate", UtilDateTime.nowTimestamp());
					if(UtilValidate.isNotEmpty(callDate)){
						callRecordMaster.set("startDate", callDate);
					}
					if(UtilValidate.isNotEmpty(callBackDateString)){
						callRecordMaster.set("callBackDate", java.sql.Date.valueOf(callBackDateString));
					}
					callRecordMaster.set("callOutCome", callOutCome);
					callRecordMaster.set("responseTypeId", responseTypeId);
					callRecordMaster.set("responseReasonId", responseReasonId);
					callRecordMaster.set("csr1PartyId", userLogin.getString("userLoginId"));
					callRecordMaster.set("createdByUserLogin", userLogin.getString("userLoginId"));
					callRecordMaster.set("phoneNumber", phoneNumber);
					callRecordMaster.set("ownerId", ownerPartyId);
					callRecordMaster.set("ownerBusinessUnit", ownerBU);
					callRecordMaster.create();
					GenericValue callRecordDetails = delegator.makeValue("CallRecordDetails");
					callRecordDetails.set("callRecordId", callRecordMaster.getString("callRecordId"));
					callRecordDetails.set("callRecordDetailSeqId", delegator.getNextSeqId("CallRecordDetails"));
					if (UtilValidate.isNotEmpty(partyId)) {
						callRecordDetails.set("partyId", partyId);
					}
					if (UtilValidate.isNotEmpty(marketingCampaignId)) {
						callRecordDetails.set("marketingCampaignId", marketingCampaignId);
					}
					if (UtilValidate.isNotEmpty(callOutCome)) {
						callRecordDetails.set("callOutCome", callOutCome);
					}
					if (UtilValidate.isNotEmpty(responseTypeId)) {
						callRecordDetails.set("responseTypeId", responseTypeId);
					}
					if (UtilValidate.isNotEmpty(responseReasonId)) {
						callRecordDetails.set("responseReasonId", responseReasonId);
					}
					/*if (UtilValidate.isNotEmpty(phoneNumber)) {
						callRecordDetails.set("callNumber", new BigDecimal(phoneNumber));
					}*/
					if(UtilValidate.isNotEmpty(callDate)){
						callRecordDetails.set("callStartTime", callDate);
					}
					callRecordDetails.set("csrPartyId", userLogin.getString("userLoginId"));
					if (UtilValidate.isNotEmpty(duration)) {
						callRecordDetails.put("callDuration", duration);
					}
					callRecordDetails.set("callStatusId", opportunityStageId);
					callRecordDetails.create();
				}else{
					GenericValue callRecordMasterData = EntityQuery.use(delegator).from("CallRecordMaster").where(UtilMisc.toMap("entityReferenceId",salesOpportunityId, "entityReferenceTypeId",entityReferenceTypeId)).queryFirst();
					if (UtilValidate.isNotEmpty(callRecordMasterData)) {
						callRecordMasterData.set("workEffortId", workEffortId);
						if(UtilValidate.isNotEmpty(workEffortTypeId)){
							callRecordMasterData.set("workEffortTypeId", workEffortTypeId);
						}
						if(UtilValidate.isNotEmpty(callBackDateString)){
							callRecordMasterData.set("callBackDate", java.sql.Date.valueOf(callBackDateString));
						}
						callRecordMasterData.set("ownerId", ownerPartyId);
						callRecordMasterData.set("ownerBusinessUnit", ownerBU);
						callRecordMasterData.set("callOutCome", callOutCome);
						callRecordMasterData.set("responseTypeId", responseTypeId);
						callRecordMasterData.set("responseReasonId", responseReasonId);
						callRecordMasterData.store();

						GenericValue callRecordDetails = delegator.makeValue("CallRecordDetails");
						callRecordDetails.set("callRecordId", callRecordMasterData.getString("callRecordId"));
						callRecordDetails.set("callRecordDetailSeqId", delegator.getNextSeqId("CallRecordDetails"));
						if (UtilValidate.isNotEmpty(partyId)) {
							callRecordDetails.set("partyId", partyId);
						}
						if (UtilValidate.isNotEmpty(marketingCampaignId)) {
							callRecordDetails.set("marketingCampaignId", marketingCampaignId);
						}
						if (UtilValidate.isNotEmpty(callOutCome)) {
							callRecordDetails.set("callOutCome", callOutCome);
						}
						if (UtilValidate.isNotEmpty(responseTypeId)) {
							callRecordDetails.set("responseTypeId", responseTypeId);
						}
						if (UtilValidate.isNotEmpty(responseReasonId)) {
							callRecordDetails.set("responseReasonId", responseReasonId);
						}
						/*if (UtilValidate.isNotEmpty(phoneNumber)) {
							callRecordDetails.set("callNumber", new BigDecimal(phoneNumber));
						}*/
						if(UtilValidate.isNotEmpty(callDate)){
							callRecordDetails.set("callStartTime", callDate);
						}
						callRecordDetails.set("csrPartyId", userLogin.getString("userLoginId"));
						if (UtilValidate.isNotEmpty(duration)) {
							callRecordDetails.put("callDuration", duration);
						}
						callRecordDetails.set("callStatusId", opportunityStageId);
						callRecordDetails.create();
					}
				}
			}
			if (UtilValidate.isNotEmpty(workEffortId) && !(workEffortId.equals(null))){
				result.put("workEffortId", workEffortId);
				result.putAll(ServiceUtil.returnSuccess("Activity Created Successfully.."));
			}else{
				result.putAll(ServiceUtil.returnError("Activity Creation Error.."));
			}

		}catch (Exception e) {e.printStackTrace();
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		return result;
	}
	
	
	public static Map<String, Object> setSalesLoginHistory(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
				
		String seqId = (String) context.get("seqId");
		String entity = (String) context.get("entity");
		HttpSession session = (HttpSession) context.get("session");
		
		GenericValue salesOpportunitySummaryDetails = null;
		GenericValue workEffortCallSummaryDetails = null;
		
		String responseMessage = UtilProperties.getMessage(RESOURCE, "Sales Login History Created Successfully!", locale);
	    Map < String, Object > results = ServiceUtil.returnSuccess(responseMessage);
	    
	    try{

	    	if(UtilValidate.isNotEmpty(seqId) && UtilValidate.isNotEmpty(entity)) {    				
	    		salesOpportunitySummaryDetails = delegator.findOne("SalesOpportunitySummary", UtilMisc.toMap("salesOpportunityId",seqId),false);
	    		workEffortCallSummaryDetails = EntityQuery.use(delegator).select("workEffortId").from("WorkEffortCallSummary").where("workEffortId",seqId).queryFirst();

	    		if(UtilValidate.isNotEmpty(salesOpportunitySummaryDetails) || UtilValidate.isNotEmpty(workEffortCallSummaryDetails)) {
	    			List<EntityCondition> conditionList = FastList.newInstance();
	    			if (UtilValidate.isNotEmpty(userLogin)) {
	    				conditionList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")));
	    			}
	    			if (UtilValidate.isNotEmpty(seqId)) {
	    				conditionList.add( EntityCondition.makeCondition("seqId", EntityOperator.EQUALS, seqId) );
	    			}
	    			String visitId = VisitHandler.getVisitId(session);
	    			if (UtilValidate.isNotEmpty(visitId)) {
	    				conditionList.add( EntityCondition.makeCondition("visitId", EntityOperator.EQUALS, visitId) );
	    			}
	    			if (UtilValidate.isNotEmpty(entity)) {
	    				conditionList.add( EntityCondition.makeCondition("entity", EntityOperator.EQUALS, entity) );
	    			}
	    			EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    			List<GenericValue> UserLoginHistoryList = EntityQuery.use(delegator).from("UserLoginHistory").
	    					where(condition).orderBy("-fromDate").maxRows(5).queryList();
	    			if(UtilValidate.isEmpty(UserLoginHistoryList)) {
	    				Map<String, Object> ulhCreateMap = UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId"), "visitId", visitId, "fromDate", UtilDateTime.nowTimestamp(), "successfulLogin", "Y","entity",entity,"seqId",seqId);

	    				ModelEntity modelUserLogin = userLogin.getModelEntity();
	    				if (modelUserLogin.isField("partyId")) {
	    					ulhCreateMap.put("partyId", userLogin.get("partyId"));
	    				}
	    				delegator.create("UserLoginHistory", ulhCreateMap);
	    			}
	    			results.put("results", "Successfully Saved Login History Details");
	    		}
	    	}
	    } catch (Exception e) {
			String errMsg = "Problem While Saving Login History Details " + e.toString();
			Debug.logError(e, errMsg, MODULE);
			results.put("_ERROR_MESSAGE_", errMsg);
			return results;
		}
		return results;
	}
	
	
	public static Map<String, Object> updateServiceActivityDetails(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> results =ServiceUtil.returnSuccess();

		String workEffortId = (String) context.get("workEffortId");
		String currentStatusId = (String) context.get("currentStatusId");

		try {
			Set<String> fieldsToSelect = new TreeSet<String>();
			fieldsToSelect.add("workEffortId");
			fieldsToSelect.add("currentStatusId");
			GenericValue updateConfigRecords = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryOne();
			if (UtilValidate.isNotEmpty(updateConfigRecords)) {
				updateConfigRecords.put("workEffortId", workEffortId);
				updateConfigRecords.put("currentStatusId", currentStatusId);
				updateConfigRecords.store();
				
				String responseMessage = UtilProperties.getMessage(RESOURCE, "Activity Updated Successfully", locale);
				results = ServiceUtil.returnSuccess(responseMessage);

				results.put("workEffortId", workEffortId);
				results.put("currentStatusId", currentStatusId);
			}
			
		} catch (Exception e) {
			results = ServiceUtil.returnError(e.getMessage());
		}
		return results;
	}
	
	
	public static Map<String, Object> closedServiceActivityDetails(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        Locale locale = (Locale) context.get("locale");
        Map<String, Object> results =ServiceUtil.returnSuccess();
        
        String workEffortId = (String) context.get("workEffortId");
        Timestamp curentTime = UtilDateTime.nowTimestamp();
        try {
        	Set<String> fieldsToSelect = new TreeSet<String>();
        	fieldsToSelect.add("workEffortId");
        	GenericValue updateConfigRecords = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryOne();
        	if (UtilValidate.isNotEmpty(updateConfigRecords)) {
        		updateConfigRecords.put("workEffortId", workEffortId);
        		updateConfigRecords.put("currentStatusId","IA_MCOMPLETED");
        		updateConfigRecords.put("lastModifiedDate", curentTime);
        		updateConfigRecords.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
        		updateConfigRecords.put("closedDateTime", curentTime);
        		updateConfigRecords.put("closedByUserLogin", userLogin.getString("userLoginId"));
        		updateConfigRecords.store();
        		
        		String responseMessage = UtilProperties.getMessage(RESOURCE, "Activity Closed Successfully", locale);
        		results = ServiceUtil.returnSuccess(responseMessage);
        		
        		results.put("workEffortId", workEffortId);
        	}
        } catch (Exception e) {
        	results = ServiceUtil.returnError(e.getMessage());
        }
        return results;
	}
	
	
	public static Map<String, Object> getAlertExpiryData(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String unitTypeId = (String) context.get("unitTypeId");
		String alertAutoClosureDuration = (String) context.get("alertAutoClosureDuration");
		String alertStartDate = (String) context.get("alertStartDate");
		
		Timestamp startDate = null;
		Timestamp endDate = null;
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		String alertStartDateStr = "";

		try{
			Map<String, Object> data = new HashMap<String, Object>();
			alertStartDateStr = alertStartDate;
			alertStartDate = df1.format(df2.parse(alertStartDate));
			startDate = UtilDateTime.getDayStart(Timestamp.valueOf(alertStartDate));
			int duration = Integer.valueOf(alertAutoClosureDuration);

			if (UtilValidate.isNotEmpty(unitTypeId) && unitTypeId.equals("DAYS")) {
				endDate = UtilDateTime.addDaysToTimestamp(startDate, duration);
				String endDateStr = UtilDateTime.toDateString(endDate, "dd/MM/yyyy");
				data.put("endDate", endDateStr);
			} else if (UtilValidate.isNotEmpty(unitTypeId) && unitTypeId.equals("WEEKS")) {
				duration = duration * 7;
				endDate = UtilDateTime.addDaysToTimestamp(startDate, duration);
				String endDateStr = UtilDateTime.toDateString(endDate, "dd/MM/yyyy");
				data.put("endDate", endDateStr);
			} else if (UtilValidate.isNotEmpty(unitTypeId) && unitTypeId.equals("MONTHS")) {
				Calendar c = Calendar.getInstance();
				c.setTime(df2.parse(alertStartDateStr));
				c.add(Calendar.MONTH, duration);
				String newDate = df2.format(c.getTime());
				data.put("endDate", newDate);
			}
			resultsList.add(data);
			resultMap.put("result", resultsList);
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getOpportunityCommunicationInfo(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String partyId = (String) context.get("partyId");
        String phoneNumber="";
        String emailAddr="";
        String Address="";
        String emailSolicitation="";
        String phoneSolicitation="";
        String addressSolicitation="";
        int operSrCount = 0;
        int opportunitiesCount = 0;
    	Map<String, Object> data = new HashMap<String, Object> ();
		
		try{
			if (UtilValidate.isNotEmpty(partyId)) {
				Set < String > fieldsToSelect = new TreeSet < String > ();
				fieldsToSelect.add("contactMechId");
				fieldsToSelect.add("partyId");
				fieldsToSelect.add("allowSolicitation");

				List<GenericValue> partyContactMechList = EntityQuery.use(delegator).select(fieldsToSelect).from("PartyContactMech").where("partyId", partyId).queryList();
				List conditionsList = FastList.newInstance();

				if (UtilValidate.isNotEmpty(partyContactMechList)) {
					List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(partyContactMechList, "contactMechId", true);

					if (UtilValidate.isNotEmpty(contactMechIds)) {
						conditionsList.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds));
					}
				}
				if (UtilValidate.isNotEmpty(conditionsList)) {
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
					List<GenericValue> ContactMechList = EntityQuery.use(delegator).select("contactMechId","contactMechTypeId","infoString").from("ContactMech").where(mainConditons).queryList();
					
					List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(ContactMechList, "contactMechId", true);
					List<EntityCondition> contactMechConditionList = FastList.newInstance();
					contactMechConditionList.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds));
					EntityCondition contactMechCondition = EntityCondition.makeCondition(contactMechConditionList, EntityOperator.AND);
					List<GenericValue> PartyContactMechList = EntityQuery.use(delegator).select("contactMechId","allowSolicitation").from("PartyContactMech").where(contactMechCondition).queryList();
					
					Map<String, String> PartyContactMechMap = new HashMap<String, String>();
					if (UtilValidate.isNotEmpty(PartyContactMechList)) {
						for (GenericValue eachEntry: PartyContactMechList) {
							PartyContactMechMap.put(eachEntry.getString("contactMechId"),eachEntry.getString("allowSolicitation"));
						}
					}
					
					if (UtilValidate.isNotEmpty(ContactMechList)) {
						for (GenericValue eachContactMech: ContactMechList) {
							String contactMechTypeId = eachContactMech.getString("contactMechTypeId");
							if (contactMechTypeId.equals("EMAIL_ADDRESS")) {
								emailAddr = eachContactMech.getString("infoString");
								if (UtilValidate.isNotEmpty(PartyContactMechMap.get(eachContactMech.getString("contactMechId")))) {
									emailSolicitation = PartyContactMechMap.get(eachContactMech.getString("contactMechId"));
								}else{
									emailSolicitation = "N";
								}
							}
							if (contactMechTypeId.equals("TELECOM_NUMBER")) {
								phoneNumber = eachContactMech.getString("infoString");
								if (UtilValidate.isNotEmpty(PartyContactMechMap.get(eachContactMech.getString("contactMechId")))) {
									phoneSolicitation = PartyContactMechMap.get(eachContactMech.getString("contactMechId"));
								}else{
									phoneSolicitation = "N";
								}
							}
							if (contactMechTypeId.equals("POSTAL_ADDRESS")) {
								Address = eachContactMech.getString("infoString");
								GenericValue partyAddressMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",eachContactMech.getString("contactMechId")), null, false));
								if (UtilValidate.isNotEmpty(PartyContactMechMap.get(eachContactMech.getString("contactMechId")))) {
									addressSolicitation = PartyContactMechMap.get(eachContactMech.getString("contactMechId"));
								}else{
									addressSolicitation = "N";
								}
							}
						}
					}
				}
				conditionsList.clear();
				conditionsList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId));
				conditionsList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_OPEN"));
				EntityCondition mainCondition = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
				List < GenericValue > custRequest = EntityQuery.use(delegator).from("CustRequest").select("custRequestId").where(mainCondition).queryList();
				if(UtilValidate.isNotEmpty(custRequest)){
					operSrCount=custRequest.size();
				}
				conditionsList.clear();
				conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				EntityCondition roleCnd = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
				List < GenericValue > salesOpportunityRoleList = EntityQuery.use(delegator).from("SalesOpportunityRole").select("salesOpportunityId").where(roleCnd).queryList();
				if(UtilValidate.isNotEmpty(salesOpportunityRoleList)){
					opportunitiesCount=salesOpportunityRoleList.size();
				}

				data.put("opportunitiesCount", opportunitiesCount);
				data.put("operSrCount", operSrCount);
				data.put("phoneNumber", phoneNumber);
				data.put("emailAddr", emailAddr);
				data.put("Address", Address);
				data.put("phoneSolicitation", phoneSolicitation);
				data.put("emailSolicitation", emailSolicitation);
				data.put("addressSolicitation", addressSolicitation);
				resultsList.add(data);
			}
			resultMap.put("result", resultsList);

		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	
	public static Map<String, Object> UpdateReasignForOpportunity(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String ownerUserLoginId = (String) context.get("ownerUserLoginId");
		String salesOppId = (String) context.get("salesOppId");
		String emplTeamId = (String) context.get("emplTeamId");
		
		String responseMessage = UtilProperties.getMessage(RESOURCE, "Sales Login History Created Successfully!", locale);
	    Map < String, Object > results = ServiceUtil.returnSuccess(responseMessage);
	    
	    try{
	    	GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("SalesOpportunityRole", UtilMisc.toMap("salesOpportunityId", salesOppId), null, false));
	    	
	    	if (UtilValidate.isNotEmpty(ownerUserLoginId)) {
				if (UtilValidate.isNotEmpty(updateConfigRecords)) {
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", ownerUserLoginId).queryList());
					if (UtilValidate.isNotEmpty(emplTeam)) {    
		    			String primOwnerTeamId = emplTeam.getString("emplTeamId");
		    			if (UtilValidate.isNotEmpty(primOwnerTeamId)) {   
		    				updateConfigRecords.put("emplTeamId", primOwnerTeamId);
		    			}
		    			String ownerBusinessUnitId = emplTeam.getString("businessUnit");
		    			if (UtilValidate.isNotEmpty(ownerBusinessUnitId)) {   
		    				updateConfigRecords.put("ownerBu", ownerBusinessUnitId);
		    			}
		    		}
					updateConfigRecords.put("ownerId", ownerUserLoginId);
					updateConfigRecords.store();
					results.put("resultMessage", "Y");
				}else{
					results.put("resultMessage", "N");
				}
			}

			if (UtilValidate.isNotEmpty(emplTeamId)) {
				if (UtilValidate.isNotEmpty(updateConfigRecords)) {
					updateConfigRecords.put("ownerId", "");
					updateConfigRecords.put("emplTeamId", emplTeamId);
					updateConfigRecords.store();
					results.put("resultMessage", "Y");
				}else{
					results.put("resultMessage", "N");
				}
			}
	    } catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			results = ServiceUtil.returnError("Error : "+e.getMessage());
		}
	    results.put("salesOppId", salesOppId);
		return results;
	}
	
	public static Map<String, Object> UpdateReasignActivity(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		
		String primOwnerId = (String) context.get("primOwnerId");
		String workEffortId = (String) context.get("workEffortId");
		String emplTeamId = (String) context.get("emplTeamId");
		
		String responseMessage = UtilProperties.getMessage(RESOURCE, "Activity ReAssigned Successfully!", locale);

		Map < String, Object > results = ServiceUtil.returnSuccess(responseMessage);
	    
	    try{
	    	GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), null, false));
	    	
	    	if (UtilValidate.isNotEmpty(primOwnerId)) {
				if (UtilValidate.isNotEmpty(updateConfigRecords)) {
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", primOwnerId).queryList());
					if (UtilValidate.isNotEmpty(emplTeam)) {    
		    			String primOwnerTeamId = emplTeam.getString("emplTeamId");
		    			if (UtilValidate.isNotEmpty(primOwnerTeamId)) {   
		    				updateConfigRecords.put("emplTeamId", primOwnerTeamId);
		    			}
		    			String ownerBusinessUnitId = emplTeam.getString("businessUnit");
		    			if (UtilValidate.isNotEmpty(ownerBusinessUnitId)) {   
		    				updateConfigRecords.put("businessUnitId", ownerBusinessUnitId);
		    			}
		    		}
					updateConfigRecords.put("primOwnerId", primOwnerId);
					updateConfigRecords.store();
					results.put("resultMessage", "Y");
				}else{
					results.put("resultMessage", "N");
				}
			}

			if (UtilValidate.isNotEmpty(emplTeamId)) {
				if (UtilValidate.isNotEmpty(updateConfigRecords)) {
					updateConfigRecords.put("primOwnerId", "");
					updateConfigRecords.put("emplTeamId", emplTeamId);
					updateConfigRecords.store();
					results.put("resultMessage", "Y");
				}else{
					results.put("resultMessage", "N");
				}
			}
	    } catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			results = ServiceUtil.returnError("Error : "+e.getMessage());
		}
	    results.put("workEffortId", workEffortId);
		return results;
	}
	
	
	public static Map<String, Object> getDetails(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		
		try{
			GenericValue getOpportunityDetails = EntityQuery.use(delegator).from("SalesOpportunity").where("salesOpportunityId", salesOpportunityId).queryOne();
			
			if (UtilValidate.isNotEmpty(getOpportunityDetails)) {
				Map<String, Object> data = new HashMap<String, Object>();
				String typeEnumDesc = "";
				String productDesc = "";
				String referralDesc = "";
				String businessUnitName = "";
				String originatingSR = "";
				String originatingAlert = "";
				String campaignDescription = "";
				String typeEnumId = getOpportunityDetails.getString("typeEnumId");
				String productId = getOpportunityDetails.getString("product");
				String remarks = getOpportunityDetails.getString("remarks");
				BigDecimal estimatedAmount = getOpportunityDetails.getBigDecimal("estimatedAmount");
				String createdByUserLogin = getOpportunityDetails.getString("createdByUserLogin");
				String dataSourceId = getOpportunityDetails.getString("dataSourceId");
				String dataSourceDataId = getOpportunityDetails.getString("dataSourceDataId");
				String description = getOpportunityDetails.getString("description");
				String marketingCampaignId = getOpportunityDetails.getString("marketingCampaignId");
				GenericValue channelDetails = EntityQuery.use(delegator).select("description").from("Enumeration").where("enumId", typeEnumId).queryFirst();
				if (UtilValidate.isNotEmpty(channelDetails)) {
					typeEnumDesc = channelDetails.getString("description");
				}
				GenericValue productDetails = EntityQuery.use(delegator).select("description").from("Product").where("productId", productId).queryFirst();
				if (UtilValidate.isNotEmpty(productDetails)) {
					productDesc = productDetails.getString("description");
				}
				GenericValue sourceDetails = EntityQuery.use(delegator).select("description").from("DataSource").where("dataSourceId", dataSourceId).queryFirst();
				if (UtilValidate.isNotEmpty(sourceDetails)) {
					referralDesc = sourceDetails.getString("description");
				}
				GenericValue ownerDetails = EntityQuery.use(delegator).select("businessUnitName").from("SalesOpportunitySummary").where("salesOpportunityId", salesOpportunityId).queryFirst();
				if (UtilValidate.isNotEmpty(ownerDetails)) {
					businessUnitName = ownerDetails.getString("businessUnitName");
				}
				GenericValue originatingDetails = EntityQuery.use(delegator).select("custRequestId").from("CustRequest").where("custRequestId", dataSourceDataId).queryFirst();
				if (UtilValidate.isNotEmpty(originatingDetails)) {
					originatingSR = dataSourceDataId;
				}else{
					originatingAlert = dataSourceDataId;
				}
				GenericValue campaignDetails = EntityQuery.use(delegator).select("campaignName").from("MarketingCampaign").where("marketingCampaignId", marketingCampaignId).queryFirst();
				if (UtilValidate.isNotEmpty(campaignDetails)) {
					campaignDescription = campaignDetails.getString("campaignName");
				}
				GenericValue SalesOpportunityRole = EntityQuery.use(delegator).select("ownerId").from("SalesOpportunityRole").where("salesOpportunityId",salesOpportunityId).queryFirst();
				if(UtilValidate.isNotEmpty(SalesOpportunityRole) && UtilValidate.isNotEmpty(SalesOpportunityRole.getString("ownerId"))){
        			String salesOppOwnerUserLoginId = SalesOpportunityRole.getString("ownerId");
        			if(UtilValidate.isNotEmpty(salesOppOwnerUserLoginId)){
        				GenericValue userDetails = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId",salesOppOwnerUserLoginId).queryFirst();
        				if(UtilValidate.isNotEmpty(userDetails) && UtilValidate.isNotEmpty(userDetails.getString("partyId"))){
        					String salesOppownerId = userDetails.getString("partyId");
        					if(UtilValidate.isNotEmpty(salesOppownerId)){
        						GenericValue person = EntityQuery.use(delegator).select("firstName").from("Person").where("partyId", salesOppownerId).queryOne();
        						if(UtilValidate.isNotEmpty(person) && UtilValidate.isNotEmpty(person.getString("firstName"))){
        							String salesOppownerName = "";
        							salesOppownerName = person.getString("firstName");
        							data.put("salesOppownerName", salesOppownerName);
        						}
        					}
        				}
        			}
        		}
				data.put("campaignDescription",campaignDescription);
				data.put("description",description);
				data.put("salesOpportunityId",salesOpportunityId);
				data.put("originatingSR",originatingSR);
				data.put("originatingAlert",originatingAlert);
				data.put("businessUnitName",businessUnitName);
				data.put("dataSourceDataId",dataSourceDataId);
				data.put("dataSourceId",dataSourceId);
				data.put("referralDesc",referralDesc);
				data.put("createdByUserLogin",createdByUserLogin);
				data.put("remarks",remarks);
				data.put("productDesc",productDesc);
				data.put("estimatedAmount",estimatedAmount);
				data.put("typeEnumDesc",typeEnumDesc);
				data.put("typeEnumId",typeEnumId);
				resultsList.add(data);
				resultMap.put("result", resultsList);
			}
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	
	public static Map<String, Object> getCallDetails(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		try{
			
			String salesOpportunityId = (String) context.get("salesOpportunityId");
	    	String entityReferenceTypeId = "";
	    	GenericValue enumList = EntityQuery.use(delegator).select("enumId").from("Enumeration").where("enumTypeId", "CALL_RECORD_ENTITY_TYPE","enumCode","SALES_OPPURTUNITY_TYPE").queryFirst();
			if (UtilValidate.isNotEmpty(enumList)){
				entityReferenceTypeId = enumList.getString("enumId");
			}
			GenericValue callRecordMasterList = EntityQuery.use(delegator).select("callRecordId","workEffortId").from("CallRecordMaster").where("entityReferenceId",salesOpportunityId,"entityReferenceTypeId",entityReferenceTypeId).queryFirst();
			if(UtilValidate.isNotEmpty(callRecordMasterList)){
				List<String> enumTypeIdList = new ArrayList<String>();
				enumTypeIdList.add("RESPONSE_REASON_ID");
				enumTypeIdList.add("OPP_RESPONSE_TYPE");
				enumTypeIdList.add("CALL_OUT_COME");
				List conditionList = UtilMisc.toList(EntityCondition.makeCondition("enumTypeId", EntityOperator.IN, enumTypeIdList));
				EntityCondition enumCondition = EntityCondition.makeCondition(conditionList);
				List<GenericValue> getEnumDescriptions = EntityQuery.use(delegator).select("enumId", "description").from("Enumeration").where(enumCondition).queryList();

				Map<String, String> enumAndDescription = new HashMap<String, String>();
				if(UtilValidate.isNotEmpty(getEnumDescriptions)){
					for (GenericValue enumDesc : getEnumDescriptions) {
						enumAndDescription.put(enumDesc.getString("enumId"), enumDesc.getString("description"));
					}
				}
				
				List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
				List<GenericValue> callRecordDetailsList = EntityQuery.use(delegator).from("CallRecordDetails").where("callRecordId",callRecordMasterList.getString("callRecordId")).orderBy("-createdStamp").queryList();
				
				List<String> callStatusIds = EntityUtil.getFieldListFromEntityList(callRecordDetailsList, "callStatusId", true);
				List<EntityCondition> callStatusConditionList = FastList.newInstance();
				callStatusConditionList.add(EntityCondition.makeCondition("opportunityStageId", EntityOperator.IN, callStatusIds));
				EntityCondition callStatusCondition = EntityCondition.makeCondition(callStatusConditionList, EntityOperator.AND);
				List<GenericValue> salesOpportunityStageList = EntityQuery.use(delegator).select("opportunityStageId","description").from("SalesOpportunityStage").where(callStatusCondition).queryList();
				
				Map<String, String> salesOpportunityStageMap = new HashMap<String, String>();
				if (UtilValidate.isNotEmpty(salesOpportunityStageList)) {
					for (GenericValue eachEntry: salesOpportunityStageList) {
						salesOpportunityStageMap.put(eachEntry.getString("opportunityStageId"),eachEntry.getString("description"));
					}
				}
				
				if(UtilValidate.isNotEmpty(callRecordDetailsList)) {
		    		for (GenericValue eachRecord : callRecordDetailsList) {
		    			Map<String, Object> data = new HashMap<String, Object>();
		    			String opportunityStatusId = "";
		    			String opportunityStatusIdDesc = "";
		    			opportunityStatusId = eachRecord.getString("callStatusId");
		    			if(UtilValidate.isNotEmpty(opportunityStatusId) && UtilValidate.isNotEmpty(salesOpportunityStageMap.get(opportunityStatusId))){
		    				opportunityStatusIdDesc = salesOpportunityStageMap.get(opportunityStatusId);
						}
		            	data.put("opportunityStatusId", opportunityStatusId);
		            	data.put("opportunityStatusIdDesc", opportunityStatusIdDesc);
		    			data.put("responseType", enumAndDescription.get(eachRecord.getString("responseTypeId")));
						data.put("callOutCome", enumAndDescription.get(eachRecord.getString("callOutCome")));
						data.put("reponseReason", enumAndDescription.get(eachRecord.getString("responseReasonId")));
						data.put("userId", eachRecord.getString("csrPartyId"));
						data.put("activityStartDate", UtilDateTime.toDateString(eachRecord.getTimestamp("createdStamp"),"dd/MM/yyyy hh:mm"));	
						resultsList.add(data);
		    		}
				}
				if(UtilValidate.isNotEmpty(resultsList)){
					resultMap.put("result", resultsList);
				}	
			}
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	
	public static Map<String, Object> getActivityDataForOpportunity(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		
		try{
			if (UtilValidate.isNotEmpty(salesOpportunityId)) {
				List<GenericValue> workEffortIdGeniricList = EntityQuery.use(delegator).select("workEffortId").from("SalesOpportunityWorkEffort").where("salesOpportunityId", salesOpportunityId).orderBy("-createdStamp").queryList();
				if (UtilValidate.isNotEmpty(workEffortIdGeniricList)) {
					List<String> workEffortIdList = EntityUtil.getFieldListFromEntityList(workEffortIdGeniricList, "workEffortId", true);
					EntityCondition wecondition = EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIdList);
					List<GenericValue> workEffortEntityData = EntityQuery.use(delegator).select("workEffortId", "workEffortServiceType", "workEffortSubServiceType", "primOwnerId", "createdStamp", "estimatedStartDate", "currentStatusId").from("WorkEffort").where(wecondition).queryList();
					
					List<String> workEffortServiceTypeIds = EntityUtil.getFieldListFromEntityList(workEffortEntityData, "workEffortServiceType", true);
					List<String> workEffortSubServiceTypeIds = EntityUtil.getFieldListFromEntityList(workEffortEntityData, "workEffortSubServiceType", true);
					workEffortServiceTypeIds.addAll(workEffortSubServiceTypeIds);
					
					List<EntityCondition> workEffortServiceTypeConditionList = FastList.newInstance();
					workEffortServiceTypeConditionList.add(EntityCondition.makeCondition("code", EntityOperator.IN, workEffortServiceTypeIds));
					EntityCondition workEffortServiceTypeCondition = EntityCondition.makeCondition(workEffortServiceTypeConditionList, EntityOperator.AND);
					
					List<GenericValue> WorkEffortAssocTripletList = EntityQuery.use(delegator).select("code","value").from("WorkEffortAssocTriplet").where(workEffortServiceTypeCondition).queryList();
				 	Map<String, String> WorkEffortAssocTripletMap = new HashMap<String, String>();
					if (UtilValidate.isNotEmpty(WorkEffortAssocTripletList)) {
						for (GenericValue eachEntry: WorkEffortAssocTripletList) {
							WorkEffortAssocTripletMap.put(eachEntry.getString("code"),eachEntry.getString("value"));
						}
					}
					
					List<String> currentStatusIds = EntityUtil.getFieldListFromEntityList(workEffortEntityData, "currentStatusId", true);
					List<EntityCondition> currentStatusConditionList = FastList.newInstance();
					currentStatusConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, currentStatusIds));
					EntityCondition currentStatusCondition = EntityCondition.makeCondition(currentStatusConditionList, EntityOperator.AND);
					
					List<GenericValue> StatusItemList = EntityQuery.use(delegator).select("statusId","description").from("StatusItem").where(currentStatusCondition).queryList();
				 	Map<String, String> StatusItemMap = new HashMap<String, String>();
					if (UtilValidate.isNotEmpty(StatusItemList)) {
						for (GenericValue eachEntry: StatusItemList) {
							StatusItemMap.put(eachEntry.getString("statusId"),eachEntry.getString("description"));
						}
					}
					
					Map<String, Map<String, Object>> workEffortDetailsMap = new HashMap<String, Map<String, Object>>();
					if (UtilValidate.isNotEmpty(workEffortEntityData)) {
						for (GenericValue wf : workEffortEntityData) {
							Map<String, Object> workEffortData = new HashMap<String, Object>();
							workEffortData.put("activity", wf.getString("workEffortId"));
							if(UtilValidate.isNotEmpty(wf.getString("workEffortServiceType")) && UtilValidate.isNotEmpty(WorkEffortAssocTripletMap.get(wf.getString("workEffortServiceType")))){
								workEffortData.put("activityType", WorkEffortAssocTripletMap.get(wf.getString("workEffortServiceType")));
							}
							if(UtilValidate.isNotEmpty(wf.getString("workEffortSubServiceType")) && UtilValidate.isNotEmpty(WorkEffortAssocTripletMap.get(wf.getString("workEffortSubServiceType")))){
								workEffortData.put("activitySubType", WorkEffortAssocTripletMap.get(wf.getString("workEffortSubServiceType")));
							}
							
							if (UtilValidate.isNotEmpty(wf.getTimestamp("createdStamp"))) {
								workEffortData.put("createdDate", UtilDateTime.toDateString(wf.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm"));
							}
							if (UtilValidate.isNotEmpty(wf.getString("primOwnerId"))) {
								workEffortData.put("owner", wf.getString("primOwnerId"));
							}
							if (UtilValidate.isNotEmpty(wf.getTimestamp("estimatedStartDate"))) {
								workEffortData.put("plannedDate", UtilDateTime.toDateString(wf.getTimestamp("estimatedStartDate"), "dd/MM/yyyy HH:mm"));
							}
							if(UtilValidate.isNotEmpty(wf.getString("currentStatusId")) && UtilValidate.isNotEmpty(WorkEffortAssocTripletMap.get(wf.getString("currentStatusId")))){
								workEffortData.put("status", StatusItemMap.get(wf.getString("currentStatusId")));
							}
							workEffortDetailsMap.put(wf.getString("workEffortId"), workEffortData);
						}
					}
					List<GenericValue> callRecordMasterEntityData = EntityQuery.use(delegator).select("workEffortId", "partyId", "externalReferenceId", "lastCallStatusId").from("CallRecordMaster").where(wecondition).queryList();

					Map<String, Object> data = new HashMap<String, Object>();

					String customerName = "";
					String customerCIN = "";
					String regardingId = "";
					Boolean isProspect = false;
					if (UtilValidate.isNotEmpty(callRecordMasterEntityData)) {
						
						List<String> partyIds = EntityUtil.getFieldListFromEntityList(callRecordMasterEntityData, "partyId", true);
						List<EntityCondition> partyIdConditionList = FastList.newInstance();
						partyIdConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
						EntityCondition partyIdCondition = EntityCondition.makeCondition(partyIdConditionList, EntityOperator.AND);
						List<GenericValue> PartyGroupList = EntityQuery.use(delegator).select("partyId","groupName").from("PartyGroup").where(partyIdCondition).queryList();
						List<GenericValue> PersonList = EntityQuery.use(delegator).select("partyId","firstName").from("Person").where(partyIdCondition).queryList();
						
						Map<Object, Object> PartyGroupMap = new HashMap<Object, Object>();
						Map<Object, Object> PersonMap = new HashMap<Object, Object>();
						if (UtilValidate.isNotEmpty(PartyGroupList)) {
							for (GenericValue eachEntry: PartyGroupList) {
								PartyGroupMap.put(eachEntry.get("partyId"),eachEntry.get("groupName"));
							}
						}
						if (UtilValidate.isNotEmpty(PersonList)) {
							for (GenericValue eachEntry: PersonList) {
								PersonMap.put(eachEntry.get("partyId"),eachEntry.get("firstName"));
							}
						}
						
						for (GenericValue callRecordMasterEntityRow : callRecordMasterEntityData) {
							if (UtilValidate.isNotEmpty(PartyGroupMap.get(callRecordMasterEntityRow.getString("partyId")))) {
								customerName = (String) PartyGroupMap.get("partyId");
							} else {
								if (UtilValidate.isNotEmpty(PersonMap.get(callRecordMasterEntityRow.getString("partyId")))) {
									customerName = (String) PersonMap.get("partyId");
								}
							}
							customerCIN = callRecordMasterEntityRow.getString("externalReferenceId");
							regardingId = callRecordMasterEntityRow.getString("regardingId");
						}
					} else {
						GenericValue salesOpportunity = EntityQuery.use(delegator).select("partyId").from("SalesOpportunityAndRole").where("salesOpportunityId", salesOpportunityId).queryFirst();
						if (UtilValidate.isNotEmpty(salesOpportunity)) {
							GenericValue partyGroup = EntityQuery.use(delegator).from("PartyGroup").where("partyId", salesOpportunity.getString("partyId")).queryOne();
							if (UtilValidate.isNotEmpty(partyGroup)) {
								customerName = partyGroup.getString("groupName");
							} else {
								GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", salesOpportunity.getString("partyId")).queryOne();
								if (UtilValidate.isNotEmpty(person)) {
									customerName = person.getString("firstName");
								}
							}
							GenericValue partyIdentification = EntityQuery.use(delegator).from("PartyIdentification").where("partyId", salesOpportunity.getString("partyId"), "partyIdentificationTypeId", "PROSPECT").queryOne();
							if (UtilValidate.isNotEmpty(partyIdentification)) {
								isProspect = true;
							}
							try {
								Map<String, Object> resultMapp = dispatcher.runSync("crmPortal.getCINFromParty", UtilMisc.toMap("partyId", salesOpportunity.getString("partyId"), "userLogin", userLogin));
								if (UtilValidate.isNotEmpty(resultMap)) {
									customerCIN = (String) resultMap.get("CIN");
								}
							} catch (GenericServiceException e) {
								Debug.logError("Unable to get CIN", MODULE);
							}
						}				
					}
					for (String workEffortId : workEffortIdList) {
						if (UtilValidate.isNotEmpty(workEffortDetailsMap.get(workEffortId))) {
							Map<String, Object> tempMap = (Map<String, Object>) workEffortDetailsMap.get(workEffortId);
							if (isProspect) {
								tempMap.put("prospectName", customerName);
								tempMap.put("prospectCIN", customerCIN);
							} else {
								tempMap.put("customerName", customerName);
								tempMap.put("customerCIN", customerCIN);
							}
							tempMap.put("workEffortId", workEffortId);
							tempMap.put("regardingId", regardingId);
							resultsList.add(tempMap);
						}
					}
				}
				resultMap.put("result", resultsList);
			}	
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	
	
	public static Map<String, Object> getRelatedOpportunityData(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String statusInputOpen = (String) context.get("statusOpen");
		String statusInputComplete = (String) context.get("statusCompleted");
		String currentDate = (String) context.get("currentDate");
		String numberOfDays = (String) context.get("numberOfDays");
		int numberOfDaysInt = 0;
		if (UtilValidate.isNotEmpty(numberOfDays)) {
			numberOfDaysInt = Integer.parseInt((numberOfDays));
		}
		Timestamp finalDate = null;
		
		try{
			if (UtilValidate.isNotEmpty(numberOfDays)) {
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(currentDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.add(Calendar.DATE, numberOfDaysInt);
				Date currentDatePlus = c.getTime();
				finalDate = new Timestamp(currentDatePlus.getTime());
				finalDate = UtilDateTime.getDayEnd(finalDate);
			}
			float totalStatusCount = 0;
			float statusOpen = 0;
			float statusWon = 0;
			float statusLost = 0;
			List<String> salesOpportunityIdList = new ArrayList<String>();
			BigDecimal percentageWon = BigDecimal.ZERO;
			BigDecimal percentageLost = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(salesOpportunityId)) {
				List<EntityCondition> salesOpportunityRoleconditionlist = FastList.newInstance();
				salesOpportunityRoleconditionlist.add(
						EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, salesOpportunityId));
						EntityCondition salesOpportunityRolecondition = EntityCondition.makeCondition(salesOpportunityRoleconditionlist, EntityOperator.AND);
				GenericValue partyIdGeneric = EntityQuery.use(delegator).select("partyId").from("SalesOpportunityRole").where(salesOpportunityRolecondition).queryFirst();
				if (UtilValidate.isNotEmpty(partyIdGeneric)){
					if (UtilValidate.isNotEmpty(partyIdGeneric.getString("partyId"))) {
						String partyId = partyIdGeneric.getString("partyId");
						List<EntityCondition> partyIdentificationConditionList = FastList.newInstance();
						partyIdentificationConditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CIF"));
						partyIdentificationConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
						EntityCondition partyIdentificationCondition = EntityCondition.makeCondition(partyIdentificationConditionList, EntityOperator.AND);
						GenericValue cINCustomerGeneric = EntityQuery.use(delegator).select("idValue").from("PartyIdentification").where(partyIdentificationCondition).queryOne();

						String customerIdentificationNumber = "";
						if (UtilValidate.isNotEmpty(cINCustomerGeneric)){
							customerIdentificationNumber = cINCustomerGeneric.getString("idValue");
						}
						String phone = "";
						GenericValue primaryTelecomNumber = getPartyPhoneNumber(delegator, partyId, "PRIMARY_PHONE");
						if (UtilValidate.isNotEmpty(primaryTelecomNumber) &&primaryTelecomNumber != null && primaryTelecomNumber.size() > 0) {
							phone = primaryTelecomNumber.getString("contactNumber");
						}
						List<GenericValue> SalesOpportunityStageList = EntityQuery.use(delegator).select("opportunityStageId", "description").from("SalesOpportunityStage").queryList();
						Map<String, String> SalesOpportunityStageMap = new HashMap<String, String>();
						if (UtilValidate.isNotEmpty(SalesOpportunityStageList)){
							for (GenericValue SalesOpportunityStageGeneric : SalesOpportunityStageList) {
								SalesOpportunityStageMap.put(SalesOpportunityStageGeneric.getString("opportunityStageId"), SalesOpportunityStageGeneric.getString("description"));
							}	
						}
						String customerName = PartyHelper.getPartyName(delegator, partyId, false);
						List<EntityCondition> salesOpportunityRolesconditionlist = FastList.newInstance();
						salesOpportunityRolesconditionlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
						EntityCondition salesOpportunityRolescondition = EntityCondition.makeCondition(salesOpportunityRolesconditionlist, EntityOperator.AND);
						List<GenericValue> salesOpportunityIdGenericList = EntityQuery.use(delegator).select("salesOpportunityId").from("SalesOpportunityRole").where(salesOpportunityRolescondition).queryList();

						if (UtilValidate.isNotEmpty(salesOpportunityIdGenericList)) {
							salesOpportunityIdList = EntityUtil.getFieldListFromEntityList(salesOpportunityIdGenericList, "salesOpportunityId", true);
							List<EntityCondition> salesOpportunityconditionlist = FastList.newInstance();
							salesOpportunityconditionlist.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.IN, salesOpportunityIdList));

							EntityCondition salesOpportunitycondition = EntityCondition.makeCondition(salesOpportunityconditionlist, EntityOperator.AND);
							List<GenericValue> salesOpportunityGenericList = EntityQuery.use(delegator).select("salesOpportunityId", "marketingCampaignId", "description", "opportunityStageId").from("SalesOpportunity").where(salesOpportunitycondition).orderBy("-createdStamp").queryList();

							if (UtilValidate.isNotEmpty(salesOpportunityGenericList)) {
								List<String> opportunityStatusIdList = EntityUtil.getFieldListFromEntityList(salesOpportunityGenericList, "opportunityStageId", false);
								if (UtilValidate.isNotEmpty(salesOpportunityIdList)) {
									totalStatusCount = salesOpportunityIdList.size();
								}
								if (UtilValidate.isNotEmpty(opportunityStatusIdList)) {
									statusOpen = Collections.frequency(opportunityStatusIdList, "SOSTG_OPEN");
									statusWon = Collections.frequency(opportunityStatusIdList, "SOSTG_WON");
									statusLost = Collections.frequency(opportunityStatusIdList, "SOSTG_LOST");
									BigDecimal bd = new BigDecimal(Float.toString((statusWon * 100) / totalStatusCount));
									percentageWon = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
									BigDecimal bd2 = new BigDecimal(Float.toString((statusLost * 100) / totalStatusCount));
									percentageLost = bd2.setScale(2, BigDecimal.ROUND_HALF_UP);
								}
							}
							List<String> salesOpportunitySelectionList = new ArrayList<String>();
							List<EntityCondition> salesOpportunityWithSelectionConditionlist = FastList.newInstance();
							if (UtilValidate.isNotEmpty(statusInputOpen) && UtilValidate.isEmpty(statusInputComplete)) {
								salesOpportunitySelectionList.add("SOSTG_OPEN");
								salesOpportunityWithSelectionConditionlist.add(EntityCondition.makeCondition("opportunityStageId", EntityOperator.IN, salesOpportunitySelectionList));
							} else if (UtilValidate.isEmpty(statusInputOpen) && UtilValidate.isNotEmpty(statusInputComplete)) {
								salesOpportunitySelectionList.add("SOSTG_WON");
								salesOpportunitySelectionList.add("SOSTG_LOST");
								salesOpportunityWithSelectionConditionlist.add(EntityCondition.makeCondition("opportunityStageId", EntityOperator.IN, salesOpportunitySelectionList));
							}
							salesOpportunityWithSelectionConditionlist.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.IN, salesOpportunityIdList));
							EntityCondition salesOpportunityWithSelectionCondition = EntityCondition.makeCondition(salesOpportunityWithSelectionConditionlist, EntityOperator.AND);
							List<GenericValue> salesOpportunityWithSelectionGenericList = EntityQuery.use(delegator).select("salesOpportunityId", "marketingCampaignId", "description", "opportunityStageId","createdStamp").from("SalesOpportunity").where(salesOpportunityWithSelectionCondition).orderBy("-createdStamp").queryList();

							Map<String, Map<String, Object>> marketingCampaignMapMap = new HashMap<String, Map<String, Object>>();

							if (UtilValidate.isNotEmpty(salesOpportunityWithSelectionGenericList)) {
								List<String> marketingCampaignIdList = EntityUtil.getFieldListFromEntityList(salesOpportunityWithSelectionGenericList, "marketingCampaignId", true);
								List<EntityCondition> marketingCampaignconditionlist = FastList.newInstance();

								if (UtilValidate.isNotEmpty(numberOfDays)) {
									marketingCampaignconditionlist.add(EntityCondition.makeCondition("endDate", EntityOperator.LESS_THAN_EQUAL_TO, finalDate));
								}

								marketingCampaignconditionlist.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.IN, marketingCampaignIdList));
								EntityCondition marketingCampaigncondition = EntityCondition.makeCondition(marketingCampaignconditionlist, EntityOperator.AND);
								List<GenericValue> marketingCampaignGenericList = EntityQuery.use(delegator).select("marketingCampaignId", "campaignCode", "startDate", "endDate").from("MarketingCampaign").where(marketingCampaigncondition).queryList();
								if (UtilValidate.isNotEmpty(marketingCampaignGenericList))
									for (GenericValue marketingCampaignGeneric : marketingCampaignGenericList) {
										Map<String, Object> data = new HashMap<String, Object>();
										if (UtilValidate.isNotEmpty(marketingCampaignGeneric.getString("campaignCode")))
											data.put("campaignCode", marketingCampaignGeneric.getString("campaignCode"));
										if (UtilValidate.isNotEmpty(marketingCampaignGeneric.getString("startDate")))
											data.put("campaignStartDate", marketingCampaignGeneric.getString("startDate"));
										if (UtilValidate.isNotEmpty(marketingCampaignGeneric.getString("endDate")))
											data.put("campaignEndDate", marketingCampaignGeneric.getString("endDate"));
										marketingCampaignMapMap.put(marketingCampaignGeneric.getString("marketingCampaignId"), data);
									}
							}
							
							GenericValue partyType = EntityQuery.use(delegator).from("PartyIdentification").where("partyId", partyId, "partyIdentificationTypeId", "PROSPECT").queryOne();
							
							if (UtilValidate.isNotEmpty(salesOpportunityWithSelectionGenericList))
								for (GenericValue salesOpportunityGeneric : salesOpportunityWithSelectionGenericList) {
									Map<String, Object> marketingCampaign = marketingCampaignMapMap.get(salesOpportunityGeneric.get("marketingCampaignId"));

									Map<String, Object> data = new HashMap<String, Object>();
									if (UtilValidate.isNotEmpty(salesOpportunityGeneric.get("salesOpportunityId")))
										data.put("opportunityNumber", salesOpportunityGeneric.get("salesOpportunityId"));
									if (UtilValidate.isNotEmpty(salesOpportunityGeneric.get("opportunityStageId"))) {
										if (UtilValidate.isNotEmpty(SalesOpportunityStageMap))
											data.put("opportunityStatus", SalesOpportunityStageMap.get(salesOpportunityGeneric.get("opportunityStageId")));
									}
									if (UtilValidate.isNotEmpty(salesOpportunityGeneric.get("marketingCampaignId")))
										data.put("marketingCampaignId", salesOpportunityGeneric.get("marketingCampaignId"));
									if (UtilValidate.isNotEmpty(salesOpportunityGeneric.get("opportunityStageId")))
										data.put("opportunityStatusId", salesOpportunityGeneric.get("opportunityStageId"));
									if (UtilValidate.isNotEmpty(salesOpportunityGeneric.get("description")))
										data.put("campaignDescription", salesOpportunityGeneric.get("description"));

									if (UtilValidate.isNotEmpty(marketingCampaign)) {
										data.put("campaignCode", UtilValidate.isNotEmpty(marketingCampaign.get("campaignCode"))? marketingCampaign.get("campaignCode") : "");
										data.put("campaignStartDate", UtilValidate.isNotEmpty(marketingCampaign.get("campaignStartDate"))? marketingCampaign.get("campaignStartDate") : "");
										data.put("campaignEndDate",UtilValidate.isNotEmpty(marketingCampaign.get("campaignEndDate"))? marketingCampaign.get("campaignEndDate") : "");
									} else {
										data.put("campaignCode", "");
										data.put("campaignStartDate", "");
										data.put("campaignEndDate", "");
									}

									if (UtilValidate.isNotEmpty(customerName)){
										if (UtilValidate.isNotEmpty(partyType)) {
											data.put("prospectName", customerName);
											data.put("prospectCin", customerIdentificationNumber);
										}else{
											data.put("customer", customerName);
											data.put("cIN", customerIdentificationNumber);
										}
									}
									if (UtilValidate.isNotEmpty(phone))
										data.put("phone", "+65"+ " " +phone);
									if (UtilValidate.isNotEmpty(totalStatusCount))
										data.put("total", totalStatusCount);
									if (UtilValidate.isNotEmpty(statusWon))
										data.put("won", statusWon);
									if (UtilValidate.isNotEmpty(statusLost))
										data.put("lost", statusLost);
									if (UtilValidate.isNotEmpty(statusOpen))
										data.put("open", statusOpen);
									if (UtilValidate.isNotEmpty(percentageWon))
										data.put("percentageWon", percentageWon);
									if (UtilValidate.isNotEmpty(percentageLost))
										data.put("percentageLost", percentageLost);
									if (UtilValidate.isNotEmpty(data))
										resultsList.add(data);
								}
							resultMap.put("result", resultsList);
						}
					}
				}
				result.put("resultMap", resultMap);
			}
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		return result;
	}
	
	
	public static GenericValue getPartyPhoneNumber(Delegator delegator, String partyId, String purposeType) {
		List<String> partyContactMechIds = new ArrayList<String>();
		GenericValue telecomNumber = null;
		try {
			List<GenericValue> partyContactMechs = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId), null, true);
			partyContactMechs = EntityUtil.filterByDate(partyContactMechs);

			for (GenericValue partyContactMech : partyContactMechs) {
				if (UtilValidate.isNotEmpty(partyContactMech.getString("contactMechId"))) {
					partyContactMechIds.add(partyContactMech.getString("contactMechId"));
				}
			}
			
			Set<String> findOptions = UtilMisc.toSet("contactMechId");
			List<String> orderBy = UtilMisc.toList("createdStamp DESC");
			EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
			EntityCondition condition2 = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, partyContactMechIds);
			EntityCondition primaryPhoneConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1,condition2,EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, purposeType)));
			List<GenericValue> primaryPhones = delegator.findList("PartyContactMechPurpose", primaryPhoneConditions, findOptions, null, null, true);
			if (UtilValidate.isNotEmpty(primaryPhones)) {
				GenericValue primaryTelecomNumber = EntityUtil.getFirst(EntityUtil.filterByDate(primaryPhones));
				if (primaryTelecomNumber != null && primaryTelecomNumber.size() > 0) {
					telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", primaryTelecomNumber.getString("contactMechId")), false);
				}
			}
		} catch (GenericEntityException e) {
			Debug.logInfo("Error: " + e.getMessage(), MODULE);
		}
		return telecomNumber;
	}
	
	public static Map<String, Object> getviewopp(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String salesOppId = (String) context.get("salesOppId");
		EntityFindOptions efo = new EntityFindOptions();
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	SimpleDateFormat df3 = new SimpleDateFormat("dd/MM/yyyy");
    	SimpleDateFormat df4 = new SimpleDateFormat("yyyy-MM-dd");
    	
    	try{

    		GenericValue getviewopp = EntityQuery.use(delegator).from("SalesOpportunity").where("salesOpportunityId",salesOppId).queryFirst();
    		if(UtilValidate.isNotEmpty(getviewopp)){
    			Map<String, Object> data = new HashMap<String, Object>();
    			String callOutCome = "";
    			callOutCome = getviewopp.getString("callOutCome");
    			data.put("salesOpportunityId", UtilValidate.isNotEmpty(getviewopp) ? getviewopp.getString("salesOpportunityId") : "");
    			data.put("marketingCampaignId", UtilValidate.isNotEmpty(getviewopp) ? getviewopp.getString("marketingCampaignId") : "");
    			GenericValue roleType = EntityQuery.use(delegator).from("MarketingCampaign").where("marketingCampaignId", getviewopp.getString("marketingCampaignId")).queryFirst();
    			data.put("campaignCode", UtilValidate.isNotEmpty(roleType) ? roleType.getString("campaignCode") : "");
    			data.put("endDate", UtilValidate.isNotEmpty(roleType) ? roleType.getString("endDate") : "");
    			data.put("campaignName", UtilValidate.isNotEmpty(roleType) ? roleType.getString("campaignName") : "");
    			data.put("opportunityStageId", UtilValidate.isNotEmpty(getviewopp) ? getviewopp.getString("opportunityStageId") : "");
    			data.put("opportunityStatusId", getviewopp.getString("opportunityStatusId"));
    			Timestamp createdStamp = getviewopp.getTimestamp("createdStamp");
    			String closedBy = "";
    			Timestamp closedOnDate = null;
    			String closedOn ="";
    			if(UtilValidate.isNotEmpty(createdStamp)){
    				data.put("createdStamp", UtilDateTime.toDateString(createdStamp,"dd/MM/yyyy hh:mm"));	
    			}else{
    				data.put("createdStamp", "");	
    			}
    			Timestamp lastUpdatedStamp = getviewopp.getTimestamp("lastUpdatedStamp");
    			if(UtilValidate.isNotEmpty(lastUpdatedStamp)){
    				data.put("lastUpdatedStamp", UtilDateTime.toDateString(lastUpdatedStamp,"dd/MM/yyyy hh:mm"));	
    			}else{
    				data.put("lastUpdatedStamp", "");	
    			}
    			data.put("createdByUserLogin", UtilValidate.isNotEmpty(getviewopp.getString("createdByUserLogin"))? getviewopp.getString("createdByUserLogin"): null);
    			data.put("lastModifiedByUserLogin", getviewopp.getString("lastModifiedByUserLogin"));

    			Timestamp lastModifiedDate = getviewopp.getTimestamp("lastModifiedDate");
    			if(UtilValidate.isNotEmpty(lastModifiedDate)){
    				data.put("lastModifiedDate", UtilDateTime.toDateString(lastUpdatedStamp,"dd/MM/yyyy hh:mm"));	
    			}else{
    				data.put("lastModifiedDate", "");	
    			}
    			GenericValue modifiedBy = EntityQuery.use(delegator).from("SalesOpportunityHistory").where("salesOpportunityId", getviewopp.getString("salesOpportunityId")).queryFirst();
    			if(UtilValidate.isNotEmpty(modifiedBy)){
    				closedBy = modifiedBy.getString("modifiedByUserLogin");
    				closedOnDate = modifiedBy.getTimestamp("estimatedClosedDate");
    				if(UtilValidate.isNotEmpty(closedOnDate)){
    					closedOn = UtilDateTime.toDateString(closedOnDate,"dd/MM/yyyy hh:mm");
    				}
    			}
    			data.put("closedBy", closedBy);	
    			data.put("closedOn", closedOn);
    			GenericValue SalesOpportunityRole = EntityQuery.use(delegator).select("ownerId").from("SalesOpportunityRole").where("salesOpportunityId",salesOppId).queryFirst();
        		if(UtilValidate.isNotEmpty(SalesOpportunityRole) && UtilValidate.isNotEmpty(SalesOpportunityRole.getString("ownerId"))){
        			String salesOppownerId = SalesOpportunityRole.getString("ownerId");
        			if(UtilValidate.isNotEmpty(salesOppownerId)){
        				GenericValue person = EntityQuery.use(delegator).select("firstName").from("Person").where("partyId", salesOppownerId).queryOne();
        				if(UtilValidate.isNotEmpty(person) && UtilValidate.isNotEmpty(person.getString("firstName"))){
        					String salesOppownerName = "";
        					salesOppownerName = person.getString("firstName");
        					data.put("salesOppownerName", salesOppownerName);
        				}
        			}
        		}
    			String entityReferenceTypeId = "";
    			GenericValue enumList = EntityQuery.use(delegator).select("enumId").from("Enumeration").where("enumTypeId", "CALL_RECORD_ENTITY_TYPE","enumCode","SALES_OPPURTUNITY_TYPE").queryFirst();
    			if (UtilValidate.isNotEmpty(enumList)){
    				entityReferenceTypeId = enumList.getString("enumId");
    			}
    			GenericValue callRecordMasterList = EntityQuery.use(delegator).from("CallRecordMaster").select("callRecordId","callBackDate","lastCallStatusId").where("entityReferenceId",salesOppId, "entityReferenceTypeId",entityReferenceTypeId).orderBy("-createdStamp").queryFirst();
    			if(UtilValidate.isNotEmpty(callRecordMasterList)){
    				data.put("lastCallStatusId", callRecordMasterList.getString("lastCallStatusId"));
    				if(UtilValidate.isNotEmpty(callRecordMasterList.getDate("callBackDate"))){
    					data.put("callBackDate",UtilDateTime.toDateString(callRecordMasterList.getDate("callBackDate"),"dd/MM/yyyy"));
    				}
    			}
    			GenericValue callRecordDetailsList = EntityQuery.use(delegator).from("CallRecordDetails").select("createdStamp","callOutCome","responseTypeId","responseReasonId").where("callRecordId",callRecordMasterList.getString("callRecordId")).orderBy("-createdStamp").queryFirst();
    			if(UtilValidate.isNotEmpty(callRecordDetailsList)){
    				data.put("callOutCome", callRecordDetailsList.getString("callOutCome"));
    				data.put("responseTypeId",callRecordDetailsList.getString("responseTypeId"));
    				data.put("responseReasonId",callRecordDetailsList.getString("responseReasonId"));
    				Timestamp createdDate = callRecordDetailsList.getTimestamp("createdStamp");
    				if(UtilValidate.isNotEmpty(createdDate)){
    					Timestamp lastCall = UtilDateTime.getDayStart(createdDate);
    					int daysInQueue = UtilDateTime.getIntervalInDays(lastCall,UtilDateTime.nowTimestamp());
    					data.put("lastCall",daysInQueue);
    				}
    			}else{
    				data.put("callOutCome", callOutCome);
    				data.put("responseTypeId","");
    				data.put("responseReasonId","");
    				data.put("callBackDate","");
    				data.put("lastCall","");
    			}
    			resultsList.add(data);
    		}
    		resultMap.put("result", resultsList);
    	}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
    	result.put("resultMap", resultMap);
    	return result;
	}
	
	
	public static Map<String, Object> updateSalesOpportunityDetails(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Locale locale = (Locale) context.get("locale");
        Map<String, Object> results =ServiceUtil.returnSuccess();
        String responseTypeId = (String) context.get("responseTypeId");
        String opportunityStatusId = (String) context.get("opportunityStatusId");
        String callOutCome = (String) context.get("callOutcome");
        String responseReasonId = (String) context.get("responseReasonId");
        String salesOpportunityId = (String) context.get("salesOpportunityId");
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        String callBackDate = (String) context.get("callBackDate");
        String opportunityStageId = (String) context.get("opportunityStageId");
        Map<String, Object> result = FastMap.newInstance();
        String responseMessage = UtilProperties.getMessage(RESOURCE, "SalesActivityCreatedSuccessfully", locale);
		try {
			GenericValue callRecordMaster = null;
			Map inMap = FastMap.newInstance();
		    Map outMap = FastMap.newInstance();
			String partyId = "";
			String customerCIN = "";
			String marketingCampaignId = "";
			String workEffortTypeId = "";
			String workEffortServiceType = "";
			String ownerBu = "";
			java.sql.Date callBackDateSql = null;
			if (UtilValidate.isNotEmpty(callBackDate)) {
				try {
					callBackDateSql = new java.sql.Date(df2.parse(callBackDate).getTime());
				} catch (ParseException e) {
				}
			}
			GenericValue salesOpportunityList = EntityQuery.use(delegator).select("customerId","customerCin","marketingCampaignId").from("SalesOpportunitySummary").where("salesOpportunityId",salesOpportunityId).queryFirst();
        	GenericValue salesOpportunityData = delegator.findOne("SalesOpportunity",UtilMisc.toMap("salesOpportunityId",salesOpportunityId),false);
        	if (UtilValidate.isNotEmpty(salesOpportunityData) && UtilValidate.isNotEmpty(opportunityStageId)) {
        		salesOpportunityData.set("opportunityStageId", opportunityStageId);
        		salesOpportunityData.set("callOutCome", callOutCome);
        		salesOpportunityData.set("responseReasonId", responseReasonId);
        		salesOpportunityData.set("responseTypeId", responseTypeId);
        		salesOpportunityData.set("lastModifiedDate", UtilDateTime.nowTimestamp());
        		salesOpportunityData.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
        		salesOpportunityData.store();
        	}
			
			if (UtilValidate.isNotEmpty(salesOpportunityList)) {
				partyId = salesOpportunityList.getString("customerId");
				customerCIN = salesOpportunityList.getString("customerCin");
				marketingCampaignId = salesOpportunityList.getString("marketingCampaignId");
			}
			GenericValue workTypeIdentification = EntityQuery.use(delegator).from("WorkEffortType").where("description","Phone Call").queryFirst();
			if (UtilValidate.isNotEmpty(workTypeIdentification)) {
				workEffortTypeId = workTypeIdentification.getString("workEffortTypeId");
			}
			EntityCondition condtn = EntityCondition.makeCondition(EntityOperator.AND,
    				EntityCondition.makeCondition("entityName", EntityOperator.EQUALS, "Activity"),
    				EntityCondition.makeCondition("type", EntityOperator.EQUALS, "Type"),
    				EntityCondition.makeCondition("value", EntityOperator.EQUALS, "Phone Call"),
    				EntityCondition.makeCondition("active", EntityOperator.EQUALS, "Y"));
    		
        	GenericValue workEffortAssocTripletList = EntityUtil.getFirst(delegator.findList("WorkEffortAssocTriplet", condtn, null, null, null, false));
        	if (UtilValidate.isNotEmpty(workEffortAssocTripletList)) {
        		workEffortServiceType = workEffortAssocTripletList.getString("code");
        	}
        	
        	/*EntityCondition rolecondition = EntityCondition.makeCondition(EntityOperator.AND,
    				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
    				EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "_NA_"));

    		GenericValue roleIdentification = EntityUtil.getFirst(delegator.findList("PartyRole", rolecondition, null, null, null, false));
    		String roleTypeId = null;
    		if (UtilValidate.isNotEmpty(roleIdentification)) {
    			roleTypeId = roleIdentification.getString("roleTypeId");
    		}*/
        	
        	GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
			String roleTypeId = party.getString("roleTypeId");
    		
    		if (UtilValidate.isNotEmpty(roleTypeId)) {
    			if ("CUSTOMER".equalsIgnoreCase(roleTypeId)) {
    				inMap.put("roleTypeId", "CUSTOMER");
    			}else if ("PROSPECT".equalsIgnoreCase(roleTypeId)) {
    				inMap.put("roleTypeId", "02");
    			}else if ("NON_CRM".equalsIgnoreCase(roleTypeId)) {
    				inMap.put("roleTypeId", "07");
    			}
    		}
    		inMap.put("userLogin",userLogin);
    		inMap.put("workEffortServiceType",workEffortServiceType);
    		inMap.put("workEffortTypeId",workEffortTypeId);
    		inMap.put("estimatedStartDate", UtilDateTime.nowTimestamp());
    		if(UtilValidate.isNotEmpty(customerCIN)){
    			inMap.put("partyId", customerCIN);
    		}else if (UtilValidate.isNotEmpty(partyId)) {
    			inMap.put("partyId", partyId);
    		}
    		inMap.put("primOwnerId", userLogin.getString("partyId"));
    		GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", userLogin.getString("partyId")).queryList());
    		if (UtilValidate.isNotEmpty(userLogin.getString("partyId"))&&UtilValidate.isNotEmpty(emplTeam)) { 
    			if (UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId"))){
    				inMap.put("emplTeamId", emplTeam.getString("emplTeamId"));
    			}
    			if (UtilValidate.isNotEmpty(emplTeam.getString("businessUnit"))){
    				ownerBu = emplTeam.getString("businessUnit");
        			inMap.put("wftMsdbusinessunit", emplTeam.getString("businessUnit"));
        		}
    		}
    		outMap = dispatcher.runSync("crmPortal.createInteractiveActivity", inMap);
    		if(!ServiceUtil.isSuccess(outMap)) {
                responseMessage = UtilProperties.getMessage(RESOURCE,ServiceUtil.getErrorMessage(outMap), locale);
            }
            String workEffortId = (String) outMap.get("workEffortId");
            if (UtilValidate.isNotEmpty(workEffortId) && !(workEffortId.equals(null)) && UtilValidate.isNotEmpty(salesOpportunityId)) {
                GenericValue salesOpportunityWorkEffort = delegator.makeValue("SalesOpportunityWorkEffort");
                salesOpportunityWorkEffort.set("salesOpportunityId", salesOpportunityId);
                salesOpportunityWorkEffort.set("workEffortId", workEffortId);
                salesOpportunityWorkEffort.create();
            }
        	String entityReferenceTypeId = "";
        	GenericValue enumList = EntityQuery.use(delegator).select("enumId").from("Enumeration").where("enumTypeId", "CALL_RECORD_ENTITY_TYPE","enumCode","SALES_OPPURTUNITY_TYPE").queryFirst();
			if (UtilValidate.isNotEmpty(enumList)){
				entityReferenceTypeId = enumList.getString("enumId");
			}
			String firstName = "";
			String middleName = "";
			String lastName = "";
			GenericValue person = EntityQuery.use(delegator).select("firstName","middleName","lastName").from("Person").where("partyId", partyId).queryFirst();
			if (UtilValidate.isNotEmpty(person)) {
				firstName = person.getString("firstName");
				middleName = person.getString("middleName");
				lastName = person.getString("lastName");
			}
			
            if (UtilValidate.isNotEmpty(workEffortId) && !(workEffortId.equals(null))) {
            	GenericValue callRecordMasterList = EntityQuery.use(delegator).from("CallRecordMaster").where(UtilMisc.toMap("entityReferenceId",salesOpportunityId, "entityReferenceTypeId",entityReferenceTypeId)).queryFirst();
    			if (UtilValidate.isEmpty(callRecordMasterList)) {
    				callRecordMaster = delegator.makeValue("CallRecordMaster");
    				callRecordMaster.set("callRecordId", delegator.getNextSeqId("CallRecordMaster"));
    	        	callRecordMaster.set("entityReferenceId", salesOpportunityId);
    	        	callRecordMaster.set("entityReferenceTypeId", entityReferenceTypeId);
    	        	if(UtilValidate.isNotEmpty(workEffortId)){
    	        		callRecordMaster.set("workEffortId", workEffortId);
    	        	}
    	        	if(UtilValidate.isNotEmpty(workEffortTypeId)){
    	        		callRecordMaster.set("workEffortTypeId", workEffortTypeId);
    	        	}
    	        	if(UtilValidate.isNotEmpty(marketingCampaignId)){
    	        		callRecordMaster.set("marketingCampaignId", marketingCampaignId);
    	        	}
    	        	callRecordMaster.set("partyId", partyId);
    	        	if(UtilValidate.isNotEmpty(customerCIN)){
    	        		callRecordMaster.set("externalReferenceId", customerCIN);
    	        		callRecordMaster.set("externalReferenceTypeId", "CIF");
    	        	}
    	        	callRecordMaster.set("createdDate", UtilDateTime.nowTimestamp());
    	        	 if (UtilValidate.isNotEmpty(callBackDateSql)) {
                         callRecordMaster.set("callBackDate", callBackDateSql);
                     } else {
                         callRecordMaster.set("callBackDate", null);
                     }
    	        	callRecordMaster.set("callOutCome", callOutCome);
    	        	callRecordMaster.set("responseTypeId", responseTypeId);
    	        	callRecordMaster.set("responseReasonId", responseReasonId);
    	        	callRecordMaster.set("csr1PartyId", userLogin.getString("userLoginId"));
    	        	callRecordMaster.set("createdByUserLogin", userLogin.getString("userLoginId"));
    	        	callRecordMaster.set("firstName", firstName);
    	        	//callRecordMaster.set("middleName", middleName);
    	        	callRecordMaster.set("lastName", lastName);
    	        	callRecordMaster.set("ownerId", userLogin.getString("partyId"));
                	callRecordMaster.set("ownerBusinessUnit", ownerBu);
    	        	callRecordMaster.create();
    	        	
    	        	GenericValue callRecordDetails = delegator.makeValue("CallRecordDetails");
            		callRecordDetails.set("callRecordId", callRecordMaster.getString("callRecordId"));
                    callRecordDetails.set("callRecordDetailSeqId", delegator.getNextSeqId("CallRecordDetails"));
                    if (UtilValidate.isNotEmpty(partyId)) {
                    	callRecordDetails.set("partyId", partyId);
                    }
                    if (UtilValidate.isNotEmpty(marketingCampaignId)) {
                    	callRecordDetails.set("marketingCampaignId", marketingCampaignId);
                    }
                    if (UtilValidate.isNotEmpty(callOutCome)) {
                    	 callRecordDetails.set("callOutCome", callOutCome);
                    }
                    if (UtilValidate.isNotEmpty(responseTypeId)) {
                    	callRecordDetails.set("responseTypeId", responseTypeId);
                    }
                    if (UtilValidate.isNotEmpty(responseReasonId)) {
                    	 callRecordDetails.set("responseReasonId", responseReasonId);
                    }
                    callRecordDetails.set("csrPartyId", userLogin.getString("userLoginId"));
                    callRecordDetails.set("callStatusId", opportunityStageId);
                    callRecordDetails.create();
    			}else{
    				
                	GenericValue callRecordMasterData = EntityQuery.use(delegator).from("CallRecordMaster").where(UtilMisc.toMap("entityReferenceId",salesOpportunityId, "entityReferenceTypeId",entityReferenceTypeId)).queryFirst();
                	if (UtilValidate.isNotEmpty(callRecordMasterData)) {
                		callRecordMasterData.set("workEffortId", workEffortId);
                		if (UtilValidate.isNotEmpty(callBackDateSql)) {
                			callRecordMasterData.set("callBackDate", callBackDateSql);
                		}else {
                			callRecordMasterData.set("callBackDate", null);
                		}
                		callRecordMasterData.set("ownerId", userLogin.getString("partyId"));
                		callRecordMasterData.set("ownerBusinessUnit", ownerBu);
                		callRecordMasterData.set("callOutCome", callOutCome);
                		callRecordMasterData.set("responseTypeId", responseTypeId);
                		callRecordMasterData.set("responseReasonId", responseReasonId);
                		callRecordMasterData.store();


                		GenericValue callRecordDetails = delegator.makeValue("CallRecordDetails");
                		callRecordDetails.set("callRecordId", callRecordMasterData.getString("callRecordId"));
                		callRecordDetails.set("callRecordDetailSeqId", delegator.getNextSeqId("CallRecordDetails"));
                		if (UtilValidate.isNotEmpty(partyId)) {
                			callRecordDetails.set("partyId", partyId);
                		}
                		if (UtilValidate.isNotEmpty(marketingCampaignId)) {
                			callRecordDetails.set("marketingCampaignId", marketingCampaignId);
                		}
                		if (UtilValidate.isNotEmpty(callOutCome)) {
                			callRecordDetails.set("callOutCome", callOutCome);
                		}
                		if (UtilValidate.isNotEmpty(responseTypeId)) {
                			callRecordDetails.set("responseTypeId", responseTypeId);
                		}
                		if (UtilValidate.isNotEmpty(responseReasonId)) {
                			callRecordDetails.set("responseReasonId", responseReasonId);
                		}
                		callRecordDetails.set("csrPartyId", userLogin.getString("userLoginId"));
                		callRecordDetails.set("callStatusId", opportunityStageId);
                		callRecordDetails.create();
                	}
    			}
            }
		} catch (Exception e) {
			Debug.log("==error in updation===" + e.getMessage());
		}
		results.put("salesOpportunityId", salesOpportunityId);
		return results;
	}
	
	
	public static Map<String, Object> getNotesAttachments(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		
		try{
			if(UtilValidate.isNotEmpty(salesOpportunityId)) {
				List<GenericValue> salesOpportunityContentList =delegator.findList("SalesOpportunityContent", EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS,salesOpportunityId), null, UtilMisc.toList("-createdStamp"), null, false);
				if(UtilValidate.isNotEmpty(salesOpportunityContentList)) {
					List<String> noteIds = EntityUtil.getFieldListFromEntityList(salesOpportunityContentList, "noteId", true);
					List<EntityCondition> noteConditionList = FastList.newInstance();
					noteConditionList.add(EntityCondition.makeCondition("noteId", EntityOperator.IN, noteIds));
					EntityCondition noteCondition = EntityCondition.makeCondition(noteConditionList, EntityOperator.AND);
					Set < String > fieldsToSelect = new TreeSet < String > ();
					fieldsToSelect.add("noteId");
					fieldsToSelect.add("noteName");
					fieldsToSelect.add("noteInfo");
					fieldsToSelect.add("moreInfoItemId");
					fieldsToSelect.add("moreInfoItemName");
					fieldsToSelect.add("noteType");
					fieldsToSelect.add("createdStamp");
					fieldsToSelect.add("noteParty");
					fieldsToSelect.add("createdByUserLogin");
					List<GenericValue> NoteDataList = EntityQuery.use(delegator).select(fieldsToSelect).from("NoteData").where(noteCondition).queryList();
					Map<String, Object> NoteDataMap = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(NoteDataList)) {
						for (GenericValue eachEntry: NoteDataList) {
							Map<String, String> NoteMap = new HashMap<String, String>();
							NoteMap.put("noteId",eachEntry.getString("noteId"));
							NoteMap.put("noteName",eachEntry.getString("noteName"));
							NoteMap.put("noteInfo",eachEntry.getString("noteInfo"));
							NoteMap.put("moreInfoItemId",eachEntry.getString("moreInfoItemId"));
							NoteMap.put("moreInfoItemName",eachEntry.getString("moreInfoItemName"));
							NoteMap.put("noteType",eachEntry.getString("noteType"));
							NoteMap.put("createdStamp",UtilDateTime.toDateString(eachEntry.getTimestamp("createdStamp"),"dd/MM/yyyy hh:mm"));
							NoteMap.put("noteParty",eachEntry.getString("noteParty"));
							NoteMap.put("createdByUserLogin",eachEntry.getString("createdByUserLogin"));
							NoteDataMap.put(eachEntry.getString("noteId"),NoteMap);
						}
					}

					for (GenericValue eachNote : salesOpportunityContentList) {
						String noteId=eachNote.getString("noteId");
						Map<String, Object> data = new HashMap<String, Object>();
						String dateStr=null;
						if(UtilValidate.isNotEmpty(noteId)) {
							Map<String, String> noteDetails = (Map<String, String>) NoteDataMap.get(noteId);
							if(UtilValidate.isNotEmpty(noteDetails)) {
								data.put("noteId", noteDetails.get("noteId"));
								data.put("noteName", noteDetails.get("noteName"));
								data.put("noteInfo", noteDetails.get("noteInfo"));
								data.put("moreInfoItemId",noteDetails.get("moreInfoItemId"));
								data.put("moreInfoItemName", noteDetails.get("moreInfoItemName"));
								data.put("moreInfoUrl", noteDetails.get("moreInfoUrl"));
								data.put("noteType", noteDetails.get("noteType"));
								data.put("createdStamp",noteDetails.get("createdStamp"));
								data.put("noteParty", noteDetails.get("noteParty"));
								data.put("createdBy", noteDetails.get("createdByUserLogin"));
								resultsList.add(data);
							}
						}
					}
				}
				resultMap.put("result", resultsList);
			}
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	
	public static Map<String, Object> getActivityData(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();

		try{
			String salesOpportunityId = (String) context.get("salesOpportunityId");
			if (UtilValidate.isNotEmpty(salesOpportunityId)) {
				List<EntityCondition> conditionlist = FastList.newInstance();
				conditionlist.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, salesOpportunityId));
				EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> workEffortIdGeniricList = EntityQuery.use(delegator).select("workEffortId").from("SalesOpportunityWorkEffort").where(condition).queryList();
				List<String> workEffortIdList = EntityUtil.getFieldListFromEntityList(workEffortIdGeniricList,"workEffortId", true);
				if (UtilValidate.isNotEmpty(workEffortIdList)) {
					List<EntityCondition> workEffortConditionList = FastList.newInstance();
					workEffortConditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIdList));
					EntityCondition wecondition = EntityCondition.makeCondition(workEffortConditionList,EntityOperator.AND);
					List<GenericValue> workEffortEntityData = EntityQuery.use(delegator).select("workEffortId", "workEffortServiceType", "workEffortSubServiceType", "primOwnerId", "createdStamp", "estimatedStartDate", "currentStatusId").from("WorkEffort").where(wecondition).queryList();
					Map<String, Map<String, Object>> workEffortDetailsMap = new HashMap<String, Map<String, Object>>();
					for (GenericValue wf : workEffortEntityData) {
						Map<String, Object> workEffortData = new HashMap<String, Object>();
						workEffortData.put("activity", wf.getString("workEffortId"));
						workEffortData.put("activityType", wf.getString("workEffortServiceType"));
						workEffortData.put("ativitySubType", wf.getString("workEffortSubServiceType"));
						workEffortData.put("owner", wf.getString("primOwnerId"));
						workEffortData.put("createdDate", wf.getString("createdStamp"));
						workEffortData.put("plannedDate", wf.getString("estimatedStartDate"));
						workEffortData.put("status", wf.getString("currentStatusId"));
						workEffortDetailsMap.put(wf.getString("workEffortId"), workEffortData);
					}
					List<EntityCondition> callRecordMasterconditionlist = FastList.newInstance();
					callRecordMasterconditionlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIdList));
					EntityCondition callRecordMastercondition = EntityCondition.makeCondition(callRecordMasterconditionlist,EntityOperator.AND);
					List<GenericValue> callRecordMasterEntityData = EntityQuery.use(delegator).select("regardingId", "workEffortId", "partyId", "externalReferenceId", "lastCallStatusId").from("CallRecordMaster").where(callRecordMastercondition).queryList();
					for (GenericValue callRecordMasterEntityRow : callRecordMasterEntityData) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("workEffortId", callRecordMasterEntityRow.getString("workEffortId"));
						data.put("regardingId", callRecordMasterEntityRow.getString("regardingId"));
						data.put("customerName", PartyHelper.getPartyName(delegator, callRecordMasterEntityRow.getString("partyId"), false));
						data.put("customerCIN", callRecordMasterEntityRow.getString("externalReferenceId"));
						if (null != workEffortDetailsMap.get(callRecordMasterEntityRow.getString("workEffortId"))) {
							Map<String, Object> tempMap = (Map<String, Object>) workEffortDetailsMap.get(callRecordMasterEntityRow.getString("workEffortId"));
							data.put("activity", tempMap.get("activity"));
							data.put("activityType", tempMap.get("activityType"));
							data.put("activitySubType", tempMap.get("ativitySubType"));
							data.put("owner", tempMap.get("owner"));
							data.put("createdDate", tempMap.get("createdDate"));
							data.put("plannedDate", tempMap.get("plannedDate"));
							data.put("status", tempMap.get("status"));
						}
						resultsList.add(data);
					}
				}
				resultMap.put("result", resultsList);
			}
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	
	public static Map<String, Object> getProductDetails(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String prodCatalogId = (String) context.get("prodCatalogId");
		String productCategoryId = (String) context.get("productCategoryId");
		
		try{
			List<EntityCondition> conditionlist = FastList.newInstance();
			if (UtilValidate.isNotEmpty(prodCatalogId) && UtilValidate.isEmpty(productCategoryId)) {
				conditionlist.clear();
				conditionlist.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, prodCatalogId));
				conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
				EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> productCatalogList = EntityQuery.use(delegator).select("prodCatalogId","productCategoryId").from("ProdCatalogCategory").where(condition).orderBy("sequenceNum").queryList();
				if (UtilValidate.isNotEmpty(productCatalogList)) {
					List<String> productCategoryIds = EntityUtil.getFieldListFromEntityList(productCatalogList, "productCategoryId", true);
					List<EntityCondition> productCategoryConditionList = FastList.newInstance();
					productCategoryConditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
					EntityCondition productCategoryCondition = EntityCondition.makeCondition(productCategoryConditionList, EntityOperator.AND);
					
					List<GenericValue> ProductCategoryList = EntityQuery.use(delegator).select("productCategoryId","categoryName").from("ProductCategory").where(productCategoryCondition).queryList();
				 	Map<String, String> ProductCategoryMap = new HashMap<String, String>();
					if (UtilValidate.isNotEmpty(ProductCategoryList)) {
						for (GenericValue eachEntry: ProductCategoryList) {
							ProductCategoryMap.put(eachEntry.getString("productCategoryId"),eachEntry.getString("categoryName"));
						}
					}
					for (GenericValue eachCatalog: productCatalogList) {
						Map<String, Object> data = new HashMap<String, Object> ();
						String categoryId = eachCatalog.getString("productCategoryId");
						if (UtilValidate.isNotEmpty(categoryId) && UtilValidate.isNotEmpty(ProductCategoryMap.get(categoryId))) {
							data.put("categoryName", ProductCategoryMap.get(categoryId));
						}
						data.put("productCategoryId", categoryId);
						resultsList.add(data);
					}
				}
			}
			if (UtilValidate.isNotEmpty(productCategoryId) && UtilValidate.isEmpty(prodCatalogId)) {
				conditionlist.clear();
				conditionlist.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
				conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
				EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				List<GenericValue> productCategoryMemberList = EntityQuery.use(delegator).select("productId","productCategoryId").from("ProductCategoryMember").where(condition).orderBy("sequenceNum").queryList();
				if (UtilValidate.isNotEmpty(productCategoryMemberList)) {
					
					List<String> productIds = EntityUtil.getFieldListFromEntityList(productCategoryMemberList, "productId", true);
					List<EntityCondition> productConditionList = FastList.newInstance();
					productConditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.IN, productIds));
					EntityCondition productCondition = EntityCondition.makeCondition(productConditionList, EntityOperator.AND);
					
					List<GenericValue> ProductList = EntityQuery.use(delegator).select("productId","description").from("Product").where(productCondition).queryList();
				 	Map<String, String> ProductMap = new HashMap<String, String>();
					if (UtilValidate.isNotEmpty(ProductList)) {
						for (GenericValue eachEntry: ProductList) {
							ProductMap.put(eachEntry.getString("productId"),eachEntry.getString("description"));
						}
					}
					for (GenericValue eachCategory: productCategoryMemberList) {
						Map<String, Object> data = new HashMap<String, Object> ();
						String productId = eachCategory.getString("productId");
						if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(ProductMap.get(productId))){
							data.put("productName", ProductMap.get(productId));
						}else{
							data.put("productName", "");
						}
						data.put("productId", productId);
						resultsList.add(data);
					}
				}
			}
			resultMap.put("result", resultsList);
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	
	public static Map<String, Object> getDataSourceDetails(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String dataSourceId = (String) context.get("dataSourceId");
		
		try{
			if (UtilValidate.isNotEmpty(dataSourceId)) {
				GenericValue dataSource = delegator.findOne("DataSource", UtilMisc.toMap("dataSourceId", dataSourceId), true);
				if (UtilValidate.isNotEmpty(dataSource)) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("dataSourceId", dataSource.getString("dataSourceId"));
					data.put("description", dataSource.getString("description"));
					resultsList.add(data);
				}
				resultMap.put("result", resultsList);
			}
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	public static Map<String, Object> getEmplTeam(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String emplTeamId = (String) context.get("emplTeamId");
        String businessUnitId = (String) context.get("businessUnitId");
		
        try{
        	List < EntityCondition > conditionlist = FastList.newInstance();
        	if (UtilValidate.isNotEmpty(emplTeamId)) {
        		conditionlist.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, emplTeamId));
        		EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
        		Set < String > fieldsToSelect = new TreeSet < String > ();
        		fieldsToSelect.add("partyId");
        		List < GenericValue > emplDet = EntityQuery.use(delegator).select(fieldsToSelect).from("EmplPositionFulfillment").where(condition).queryList();
        		if (UtilValidate.isNotEmpty(emplDet)) {
        			
        			List<EntityCondition> userLoginConditionList = FastList.newInstance();
        			List<String> partyIds = EntityUtil.getFieldListFromEntityList(emplDet, "partyId", true);
        			userLoginConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
					EntityCondition userLoginCondition = EntityCondition.makeCondition(userLoginConditionList, EntityOperator.AND);
					List<GenericValue> UserLoginList = EntityQuery.use(delegator).select("partyId","userLoginId").from("UserLogin").where(userLoginCondition).queryList();
					
					Map<String, String> UserLoginMap = new HashMap<String, String>();
					if (UtilValidate.isNotEmpty(UserLoginList)) {
						for (GenericValue eachEntry: UserLoginList) {
							UserLoginMap.put(eachEntry.getString("partyId"),eachEntry.getString("userLoginId"));
						}
					}
					
        			for (GenericValue empl: emplDet) {
        				Map < String, Object > data = new HashMap < String, Object > ();
        				data.put("partyId",empl.getString("partyId"));
        				String partyId = empl.getString("partyId");
        				if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(UserLoginMap.get(partyId))) {
        					data.put("userLoginId",UserLoginMap.get(partyId));
        				}
        				resultsList.add(data);
        			}
        		}
        	}

        	List < EntityCondition > conditionlistTeam = FastList.newInstance();
        	if (UtilValidate.isNotEmpty(businessUnitId)) {
        		conditionlistTeam.add(EntityCondition.makeCondition("businessUnit", EntityOperator.EQUALS, businessUnitId));
        		EntityCondition condition = EntityCondition.makeCondition(conditionlistTeam, EntityOperator.AND);
        		Set < String > fieldsToSelect = new TreeSet < String > ();
        		fieldsToSelect.add("emplTeamId");
        		fieldsToSelect.add("teamName");
        		List < GenericValue > emplDet = EntityQuery.use(delegator).select(fieldsToSelect).from("EmplTeam").where(condition).queryList();
        		if (UtilValidate.isNotEmpty(emplDet)) {
        			for (GenericValue empl: emplDet) {
        				Map < String, Object > data = new HashMap < String, Object > ();
        				data.put("emplTeamId",empl.getString("emplTeamId"));
        				data.put("teamName",empl.getString("teamName"));
        				resultsList.add(data);
        			}
        		}
        	}
        	resultMap.put("result", resultsList);
        }catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	
	public static Map<String, Object> getUserOrTeam(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String parameterType = (String) context.get("parameterType");
		String salesOpportunityId = (String) context.get("salesOpportunityId");

		try {
			if ("user".equalsIgnoreCase(parameterType)) {
				GenericValue salesOpportunitySummary = EntityQuery.use(delegator).from("SalesOpportunitySummary").where("salesOpportunityId", salesOpportunityId).queryOne();
				List<GenericValue> emplPositionFulfillmentList = EntityQuery.use(delegator).select("partyId").from("EmplPositionFulfillment").where("emplTeamId", salesOpportunitySummary.getString("emplTeamId")).queryList();
				Map<String, String> resultMap = new HashMap<>();
				List<String> partyIds = new ArrayList<>();

				for (GenericValue employeePartyId : emplPositionFulfillmentList) {
					partyIds.add(employeePartyId.getString("partyId"));
				}

				List<GenericValue> partyNameView = EntityQuery.use(delegator).from("UserLoginAndPartyDetails").where(EntityCondition.makeCondition("partyId",EntityOperator.IN, partyIds)).queryList();
				for (GenericValue party : partyNameView) {
					resultMap.put(party.getString("userLoginId"), party.getString("firstName") + party.getString("lastName"));
				}
				result.put("resultMap", resultMap);
			} else if ("team".equalsIgnoreCase(parameterType)) {
				GenericValue salesOpportunitySummary = EntityQuery.use(delegator).from("SalesOpportunitySummary").where("salesOpportunityId", salesOpportunityId).queryOne();
				List<GenericValue> emplTeam = EntityQuery.use(delegator).select("emplTeamId", "teamName").from("EmplTeam").where("businessUnit", salesOpportunitySummary.getString("businessUnitId")).queryList();
				Map<String, String> resultMap = new HashMap<>();
				for (GenericValue empl : emplTeam) {
					resultMap.put((String) empl.get("emplTeamId"), (String) empl.get("teamName"));
				}
				result.put("resultMap", resultMap);
			}
		} catch(GenericEntityException e) {
			Debug.logError("Unable to get User or Team", MODULE);
			return ServiceUtil.returnError("Unable to get User or Team");
		}
		return result;
	}
	
	public static Map<String, Object> viewSalesActivityDetails(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultsList = new ArrayList<Map<String, Object>>();
		
		String workEffortId = (String) context.get("workEffortId");
		
		try {
			if (UtilValidate.isNotEmpty(workEffortId)) {
				GenericValue Entry = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryOne();
				if(UtilValidate.isNotEmpty(Entry)) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("workEffortId", UtilValidate.isNotEmpty(Entry) ? Entry.getString("workEffortId") : "");
					data.put("workEffortServiceType", UtilValidate.isNotEmpty(Entry) ? Entry.getString("workEffortServiceType") : "");
					data.put("workEffortSubServiceType", UtilValidate.isNotEmpty(Entry) ? Entry.getString("workEffortSubServiceType") : "");
					String workEffortServiceType = Entry.getString("workEffortServiceType");
					GenericValue workEffortAssocTriplet = EntityQuery.use(delegator).select("value").from("WorkEffortAssocTriplet").where("code", workEffortServiceType).queryFirst();
					data.put("workEffortServiceTypeDescription", UtilValidate.isNotEmpty(workEffortAssocTriplet) ? workEffortAssocTriplet.getString("value") : "");
					String workEffortSubServiceType = Entry.getString("workEffortSubServiceType");
					workEffortAssocTriplet = EntityQuery.use(delegator).select("value").from("WorkEffortAssocTriplet").where("code", workEffortSubServiceType).queryFirst();
					data.put("workEffortSubServiceTypeDescription", UtilValidate.isNotEmpty(workEffortAssocTriplet) ?  workEffortAssocTriplet.getString("value") : "");
					data.put("workEffortName", UtilValidate.isNotEmpty(Entry) ? Entry.getString("workEffortName") : "");
					//data.put("direction", UtilValidate.isNotEmpty(Entry) ? Entry.getString("direction") : "");
					data.put("phoneNumber", UtilValidate.isNotEmpty(Entry) ? Entry.getString("phoneNumber") : "");
					data.put("description", UtilValidate.isNotEmpty(Entry) ? Entry.getString("description") : "");
					data.put("accountNumber", UtilValidate.isNotEmpty(Entry) ? Entry.getString("accountNumber") : "");
					data.put("wfOnceDone", UtilValidate.isNotEmpty(Entry) ? Entry.getString("wfOnceDone") : "");
					data.put("currentStatusId", UtilValidate.isNotEmpty(Entry) ? Entry.getString("currentStatusId") : "");
					data.put("businessUnitName", UtilValidate.isNotEmpty(Entry) ? Entry.getString("businessUnitName") : "");
					data.put("primOwnerId", UtilValidate.isNotEmpty(Entry) ? Entry.getString("primOwnerId") : "");
					data.put("estimatedStartDate", UtilValidate.isNotEmpty(Entry) ? UtilDateTime.toDateString(Entry.getTimestamp("estimatedStartDate"),"MM/dd/yyyy hh:mm") : "");
					data.put("createdStamp", UtilValidate.isNotEmpty(Entry) ? Entry.getString("createdStamp") : "");
					data.put("createdByUserLogin", UtilValidate.isNotEmpty(Entry) ? Entry.getString("createdByUserLogin") : "");
					data.put("lastModifiedByUserLogin", UtilValidate.isNotEmpty(Entry) ? Entry.getString("lastModifiedByUserLogin") : "");
					data.put("lastUpdatedStamp", UtilValidate.isNotEmpty(Entry) ? Entry.getString("lastUpdatedStamp") : "");
					data.put("lastUpdatedTxStamp", UtilValidate.isNotEmpty(Entry) ? Entry.getString("lastUpdatedTxStamp") : "");
					data.put("createdByUserLogin", UtilValidate.isNotEmpty(Entry) ? Entry.getString("createdByUserLogin") : "");
					data.put("duration", UtilValidate.isNotEmpty(Entry) ? Entry.getString("duration") : "");
					data.put("emplTeamId", UtilValidate.isNotEmpty(Entry) ? Entry.getString("emplTeamId") : "");
					data.put("businessUnitName", UtilValidate.isNotEmpty(Entry) ? Entry.getString("businessUnitName") : "");
					data.put("businessUnitId", UtilValidate.isNotEmpty(Entry) ? Entry.getString("businessUnitId") : "");
					
					if (UtilValidate.isNotEmpty(Entry.getString("direction"))) {
						GenericValue enumeration = EntityQuery.use(delegator).select("description").from("Enumeration").where("enumId", Entry.getString("direction")).queryOne();
						if (UtilValidate.isNotEmpty(enumeration) &&  UtilValidate.isNotEmpty(enumeration.getString("description"))) {
							data.put("direction", enumeration.getString("description"));
						}
					}
					
					if (UtilValidate.isNotEmpty(Entry.getString("primOwnerId"))) {
						GenericValue Person = EntityQuery.use(delegator).select("firstName","lastName").from("Person").where("partyId", Entry.getString("primOwnerId")).queryOne();
						if (UtilValidate.isNotEmpty(Person) && UtilValidate.isNotEmpty(Person.getString("firstName")) ) {
							data.put("primOwnerName", Person.getString("firstName")+" "+Person.getString("lastName"));
						}
					}
					
					if (UtilValidate.isNotEmpty(Entry.getString("emplTeamId"))) {
						GenericValue EmplTeam = EntityQuery.use(delegator).select("teamName").from("EmplTeam").where("emplTeamId", Entry.getString("emplTeamId")).queryOne();
						if (UtilValidate.isNotEmpty(EmplTeam) && UtilValidate.isNotEmpty(EmplTeam.getString("teamName")) ) {
							data.put("teamName", EmplTeam.getString("teamName"));
						}
					}
					
					if (UtilValidate.isNotEmpty(Entry)) {
						GenericValue enumeration = EntityQuery.use(delegator).select("description").from("Enumeration").where("enumId", Entry.getString("priority")).queryOne();
						if (UtilValidate.isNotEmpty(enumeration)) {
							data.put("priority", enumeration.getString("description"));
						}
					}
					resultsList.add(data);
					String primaryworkEffortId = Entry.getString("workEffortId");
					GenericValue callRecordMasterId = EntityQuery.use(delegator).from("CallRecordMaster").where("workEffortId", primaryworkEffortId).queryFirst();
					if (UtilValidate.isNotEmpty(callRecordMasterId)) {
						data.put("partyId", UtilValidate.isNotEmpty(callRecordMasterId) ? callRecordMasterId.getString("partyId") : "");
						if (UtilValidate.isNotEmpty(callRecordMasterId.getString("partyId"))) {
							GenericValue partyNameView = delegator.findOne("Person", UtilMisc.toMap("partyId", callRecordMasterId.getString("partyId")), false);
							if (partyNameView != null && partyNameView.size() > 0) {
								String notePartyName = partyNameView.getString("firstName")+" "+partyNameView.getString("lastName");
								data.put("callToName",notePartyName);
							}
						}
						data.put("externalReferenceTypeId", UtilValidate.isNotEmpty(callRecordMasterId)? callRecordMasterId.getString("externalReferenceTypeId"): "");
						data.put("regardingId", UtilValidate.isNotEmpty(callRecordMasterId) ? callRecordMasterId.getString("regardingId"): "");
						resultsList.add(data);
						String callRecordMasterPartyId = callRecordMasterId.getString("partyId");
						GenericValue callRecordDetail = EntityQuery.use(delegator).from("CallRecordDetails").where("partyId", callRecordMasterPartyId).queryFirst();
						if (UtilValidate.isNotEmpty(callRecordDetail)) {
							data.put("csrPartyId", UtilValidate.isNotEmpty(callRecordDetail) ? callRecordDetail.getString("csrPartyId") : "");
							data.put("callStartTime", UtilValidate.isNotEmpty(callRecordDetail) ? callRecordDetail.getString("callStartTime") : "");
							data.put("callEndTime", UtilValidate.isNotEmpty(callRecordDetail) ? callRecordDetail.getString("callEndTime") : "");
							data.put("callDuration", UtilValidate.isNotEmpty(callRecordDetail) ? callRecordDetail.getString("callDuration") : "");
							resultsList.add(data);
						}
						String callRecordMasterPartyId1 = callRecordMasterId.getString("partyId");
						GenericValue personPartyId = EntityQuery.use(delegator).from("Person").where("partyId", callRecordMasterPartyId1).queryOne();
						if (UtilValidate.isNotEmpty(personPartyId)) {
							data.put("nationalId", UtilValidate.isNotEmpty(personPartyId) ? personPartyId.getString("nationalId") : "");
							resultsList.add(data);
						}
					}
				}
			}
			resultMap.put("result", resultsList);
        }catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			return result;
		}
		result.put("resultMap", resultMap);
		return result;
	}
	

	public static Map<String, Object> getPersonalizedFields(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		Map <String, Object> data = new HashMap<String, Object>();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		try {
			String salesOpportunityId = (String) context.get("salesOpportunityId");
			if (UtilValidate.isNotEmpty(salesOpportunityId)) {
				GenericValue salesOpportunitySummaryCondition = delegator.findOne("SalesOpportunitySummary", UtilMisc.toMap("salesOpportunityId", salesOpportunityId), false);
				String contactListId = null;
				String partyId = null;
				if (UtilValidate.isNotEmpty(salesOpportunitySummaryCondition) &&  salesOpportunitySummaryCondition != null) {
					String marketingCampaignId = salesOpportunitySummaryCondition.getString("marketingCampaignId");
					if (UtilValidate.isNotEmpty(marketingCampaignId)  &&  marketingCampaignId != null) {
						GenericValue marketingCampaignContactListId = EntityQuery.use(delegator).from("MarketingCampaignContactList").where("marketingCampaignId", marketingCampaignId).queryOne();
						if (UtilValidate.isNotEmpty(marketingCampaignContactListId)){
								contactListId = marketingCampaignContactListId.getString("contactListId");
						}
					}
					GenericValue salesOpportunityRolePartyId = EntityQuery.use(delegator).from("SalesOpportunityRole").where("salesOpportunityId", salesOpportunityId).queryOne();
					if (UtilValidate.isNotEmpty(salesOpportunityRolePartyId) &&  salesOpportunityRolePartyId != null) {
						partyId = salesOpportunityRolePartyId.getString("partyId");
					}
				}

				List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				if (UtilValidate.isNotEmpty(contactListId)){
						conditionsList.add(EntityCondition.makeCondition("contactListId", EntityOperator.EQUALS, contactListId));
				}
				if (UtilValidate.isNotEmpty(partyId)){
						conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				}
				EntityCondition condition = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
				if (UtilValidate.isNotEmpty(condition)){
					List<GenericValue> contactListFieldData = delegator.findList("ContactListFieldData", condition, null, null, null, false);
					if (UtilValidate.isNotEmpty(contactListFieldData)){
						for (GenericValue Entry : contactListFieldData) {
							if (UtilValidate.isNotEmpty(contactListFieldData)) {
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue1"))) {
									data.put("Field1", Entry.getString("fieldValue1"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue2"))) {
									data.put("Field2", Entry.getString("fieldValue2"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue3"))) {
									data.put("Field3", Entry.getString("fieldValue3"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue4"))) {
									data.put("Field4", Entry.getString("fieldValue4"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue5"))) {
									data.put("Field5", Entry.getString("fieldValue5"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue1"))) {
									data.put("Field6", Entry.getString("fieldValue6"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue7"))) {
									data.put("Field7", Entry.getString("fieldValue7"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue8"))) {
									data.put("Field8", Entry.getString("fieldValue8"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue9"))) {
									data.put("Field9", Entry.getString("fieldValue9"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue10"))) {
									data.put("Field10", Entry.getString("fieldValue10"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue11"))) {
									data.put("Field11", Entry.getString("fieldValue11"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue12"))) {
									data.put("Field12", Entry.getString("fieldValue12"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue13"))) {
									data.put("Field13", Entry.getString("fieldValue13"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue14"))) {
									data.put("Field14", Entry.getString("fieldValue14"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue15"))) {
									data.put("Field15", Entry.getString("fieldValue15"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue16"))) {
									data.put("Field16", Entry.getString("fieldValue16"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue17"))) {
									data.put("Field17", Entry.getString("fieldValue17"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue18"))) {
									data.put("Field18", Entry.getString("fieldValue18"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue19"))) {
									data.put("Field19", Entry.getString("fieldValue19"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue20"))) {
									data.put("Field20", Entry.getString("fieldValue20"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue21"))) {
									data.put("Field21", Entry.getString("fieldValue21"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue22"))) {
									data.put("Field22", Entry.getString("fieldValue22"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue23"))) {
									data.put("Field23", Entry.getString("fieldValue23"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue24"))) {
									data.put("Field24", Entry.getString("fieldValue24"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue25"))) {
									data.put("Field25", Entry.getString("fieldValue25"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue26"))) {
									data.put("Field26", Entry.getString("fieldValue26"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue27"))) {
									data.put("Field27", Entry.getString("fieldValue27"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue28"))) {
									data.put("Field28", Entry.getString("fieldValue28"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue29"))) {
									data.put("Field29", Entry.getString("fieldValue29"));
								}
								if (UtilValidate.isNotEmpty(Entry.getString("fieldValue30"))) {
									data.put("Field30", Entry.getString("fieldValue30"));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Debug.logInfo("Error-" + e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}	
		result.put("resultMap", data);
		result.putAll(ServiceUtil.returnSuccess("Successfully find Personalized Fields..."));
		return result;
	}
	
	
}
