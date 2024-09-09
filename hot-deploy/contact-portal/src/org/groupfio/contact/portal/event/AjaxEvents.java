/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.groupfio.contact.portal.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.util.CommonDataHelper;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.QueryUtil;
import org.fio.homeapps.util.ResponseUtils;
import org.fio.homeapps.util.StatusUtil;
import org.groupfio.common.portal.CommonPortalConstants.DateTimeTypeConstant;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.contact.portal.util.LoginFilterUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.datasource.GenericHelper;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelViewEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Sharif Ul Islam
 * 
 */
public final class AjaxEvents {

	private AjaxEvents() { }

	private static final String MODULE = AjaxEvents.class.getName();

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

	/*
	 * Get Contact Details 
	 */
	public static String searchContacts(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		String partyId = request.getParameter("partyId");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String emailAddress = request.getParameter("emailAddress");
		String contactNumber = request.getParameter("contactNumber");

		String externalLoginKey = request.getParameter("externalLoginKey");

		try {
			List < EntityCondition > conditions = new ArrayList < EntityCondition > ();

			// construct role conditions
			EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"));
			conditions.add(roleTypeCondition);


			EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);
			conditions.add(partyStatusCondition);

			if (UtilValidate.isNotEmpty(partyId)) {
				EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
				conditions.add(partyCondition);
			}

			if (UtilValidate.isNotEmpty(firstName)) {
				EntityCondition firstNameCondition = EntityCondition.makeCondition("firstName", EntityOperator.LIKE, "%"+firstName + "%");
				conditions.add(firstNameCondition);
			}
			if (UtilValidate.isNotEmpty(lastName)) {
				EntityCondition lastNameCondition = EntityCondition.makeCondition("lastName", EntityOperator.LIKE, "%"+lastName + "%");
				conditions.add(lastNameCondition);
			}

			List < EntityCondition > eventExprs = new LinkedList < EntityCondition > ();
			if (UtilValidate.isNotEmpty(emailAddress) || UtilValidate.isNotEmpty(contactNumber)) {

				if (UtilValidate.isNotEmpty(emailAddress)) {
					eventExprs.add(EntityCondition.makeCondition("primaryEmail", EntityOperator.EQUALS, emailAddress));
				}

				if (UtilValidate.isNotEmpty(contactNumber)) {
					eventExprs.add(EntityCondition.makeCondition("primaryContactNumber", EntityOperator.EQUALS, contactNumber));
				}

				conditions.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
			}

			//Login Based contact Filter
			String userLoginId = userLogin.getString("partyId");
			if(LoginFilterUtil.checkEmployeePosition(delegator, userLoginId)) {

				Map<String, Object> dataSecurityMetaInfo = (Map<String, Object>) session.getAttribute("dataSecurityMetaInfo");
				if (ResponseUtils.isSuccess(dataSecurityMetaInfo)) {

					List<String> lowerPositionPartyIds = (List<String>) dataSecurityMetaInfo.get("lowerPositionPartyIds");
					if (UtilValidate.isNotEmpty(lowerPositionPartyIds)) {

						List<EntityCondition> accountConditions = new ArrayList<EntityCondition>();
						EntityCondition accountRoleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD")));
						accountConditions.add(accountRoleTypeCondition);

						EntityCondition accountPartyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
								EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);

						accountConditions.add(accountPartyStatusCondition);
						accountConditions.add(EntityUtil.getFilterByDateExpr());

						EntityCondition securityConditions = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, lowerPositionPartyIds),
								EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
								EntityUtil.getFilterByDateExpr()
								);

						if (UtilValidate.isNotEmpty(userLogin)) {
							securityConditions = EntityCondition.makeCondition(UtilMisc.toList(
									EntityCondition.makeCondition("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")),
									securityConditions
									), EntityOperator.OR);
						}

						accountConditions.add(securityConditions);

						EntityCondition mainConditons = EntityCondition.makeCondition(accountConditions, EntityOperator.AND);

						EntityFindOptions efo = new EntityFindOptions();
						efo.setDistinct(true);
						efo.getDistinct();

						Debug.logInfo("count 1 start: "+UtilDateTime.nowTimestamp(), MODULE);
						List<GenericValue> accounts = delegator.findList("PartyCommonView", mainConditons, UtilMisc.toSet("partyId"), UtilMisc.toList("partyId"+ " " + "ASC"), efo, false);
						Debug.logInfo("count 2 start: "+UtilDateTime.nowTimestamp(), MODULE);

						List<String> accountPartyIds = EntityUtil.getFieldListFromEntityList(accounts, "partyId", true);

						EntityCondition partyIdToCondition = EntityCondition.makeCondition(UtilMisc.toList(
								EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, accountPartyIds),
								EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, accountPartyIds)
								),EntityOperator.OR);
						conditions.add(partyIdToCondition);
					}

					Debug.log("lowerPositionPartyIds> "+lowerPositionPartyIds);
				}
			}

			conditions.add(EntityUtil.getFilterByDateExpr());

			EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
			efo.setOffset(0);
			efo.setLimit(1000);

			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);

			Debug.logInfo("mainConditons: "+mainConditons, MODULE);

			Debug.logInfo("list 1 start: "+UtilDateTime.nowTimestamp(), MODULE);
			List < GenericValue > partySummaryList = delegator.findList("PartySummaryView", mainConditons, null, null, efo, false);
			Debug.logInfo("list 2 end: "+UtilDateTime.nowTimestamp(), MODULE);
			List<String> contactPartyIds = EntityUtil.getFieldListFromEntityList(partySummaryList, "partyId", true);
			Set<String> fieldToSelect = new HashSet<String>();
			fieldToSelect.add("partyId");
			fieldToSelect.add("timeZoneDesc");
			List < GenericValue > partySummaryDetailsViewList = delegator.findList("PartySummaryDetailsView", EntityCondition.makeCondition("partyId", EntityOperator.IN, contactPartyIds), fieldToSelect, null, null, false);
			Map<String, String> timeZoneMap = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(partySummaryDetailsViewList)) {
				for (GenericValue eachEntry: partySummaryDetailsViewList) {
					timeZoneMap.put(eachEntry.getString("partyId"),eachEntry.getString("timeZoneDesc"));
				}
			}
			for(GenericValue partySummary : partySummaryList) {

				String contactId = partySummary.getString("partyId");

				Map<String, Object> data = new HashMap<String, Object>();
				String callBackDate = partySummary.getString("callBackDate");
				String companyName = partySummary.getString("groupName");
				String companyId = UtilValidate.isNotEmpty(partySummary.getString("pgPartyId")) ? partySummary.getString("pgPartyId") : "";
				String statusId = partySummary.getString("statusId");
				String generalProfTitle = partySummary.getString("personalTitle");
				String statusItemDesc = DataUtil.getStatusDescription(delegator, statusId, "PARTY_STATUS");

				String name = partySummary.getString("firstName");
				if (UtilValidate.isNotEmpty(partySummary.getString("lastName"))) {
					if (UtilValidate.isNotEmpty(name)) {
						name = name + " " + partySummary.getString("lastName");
					} else {
						name = partySummary.getString("lastName");
					}
				}

				String phoneNumber = partySummary.getString("primaryContactNumber");
				String infoString = partySummary.getString("primaryEmail");
				String city = partySummary.getString("primaryCity");
				String state = partySummary.getString("primaryStateProvinceGeoId");

				String country = partySummary.getString("primaryCountryGeoId");
				String postalCode = partySummary.getString("primaryPostalCode");
				String address1 = partySummary.getString("primaryAddress1");
				String address2 = partySummary.getString("primaryAddress2");

				if (UtilValidate.isNotEmpty(state)) {
					state = DataUtil.getGeoName(delegator, state, "STATE,PROVINCE");
				}
				if (UtilValidate.isNotEmpty(country)) {
					country = DataUtil.getGeoName(delegator, country, "COUNTRY");
				}

				String departmentName = partySummary.getString("departmentName");
				String designation = EnumUtil.getEnumDescription(delegator, partySummary.getString("designation"), "DBS_LD_DESIGNATION");
				String birthDate = partySummary.getString("birthDate");
				String timeZoneDesc = EnumUtil.getEnumDescription(delegator, timeZoneMap.get(contactId), "TIME_ZONE");

				data.put("partyId", contactId);
				data.put("name", name);
				data.put("generalProfTitle", generalProfTitle);
				data.put("callBackDate", callBackDate);
				data.put("statusDescription", statusItemDesc);
				data.put("contactNumber", phoneNumber);
				data.put("infoString", infoString);
				data.put("city", city);
				data.put("state", state);
				data.put("groupName", companyName);
				data.put("partyIdTo", companyId);

				data.put("departmentName", departmentName);
				data.put("designation", designation);
				data.put("country", country);
				data.put("postalCode", postalCode);
				data.put("address1", address1);
				data.put("address2", address2);
				data.put("birthDate", birthDate);
				data.put("timeZoneDescription", timeZoneDesc);

				data.put("domainEntityId", partyId);
				data.put("domainEntityType", DomainEntityType.CONTACT);
				data.put("domainEntityTypeDesc", DataHelper.convertToLabel( DomainEntityType.CONTACT ));
				data.put("externalLoginKey", externalLoginKey);

				dataList.add(data);
			}
			Debug.log("Results : " + dataList, MODULE);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("errorMessage", e.getMessage());
			data.put("errorResult", new ArrayList<Map<String, Object>>());
			dataList.add(data);
		}
		return AjaxEvents.doJSONResponse(response, dataList);
	}

	public static String findContacts(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		List<GenericValue> resultList = null;

		List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String partyId = request.getParameter("partyId");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String emailAddress = request.getParameter("emailAddress");
		String contactNumber = request.getParameter("contactNumber");
		String createdDateFrom = request.getParameter("createdDate_from");
		String createdDateTo = request.getParameter("createdDate_to");
		String callBackDateFrom = request.getParameter("callBackDate_from");
		String callBackDateTo = request.getParameter("callBackDate_to");
		String segmentCode = request.getParameter("segmentCode");
		String callStatus = request.getParameter("callStatus");
		String responsableFor = request.getParameter("responsableFor");
		String externalLoginKey = request.getParameter("externalLoginKey");
		String externalId = request.getParameter("externalId");
		
		String city = request.getParameter("city");
	    String postalCode = request.getParameter("postalCode");
		
		String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);
		String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
		
		long start = System.currentTimeMillis();
		try {

			//Integrate security matrix logic start
			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String businessUnit = null;

			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "Read");
				accessMatrixMap.put("entityName", "Contact");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
			}
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				List<EntityCondition> conditionList = FastList.newInstance();
				
				// construct role conditions
				conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
				
				//EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));

				//check with ownerId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					conditionList.add(EntityCondition.makeCondition("ownerId", EntityOperator.IN, ownerIds));
				}
				//filter condition for segmentcode
				if(UtilValidate.isNotEmpty(segmentCode)) {
					List<GenericValue> associatedPartyIdList = EntityQuery.use(delegator).select("partyId").from("CustomFieldPartyClassification").where(EntityCondition.makeCondition("groupId",EntityOperator.EQUALS,segmentCode)).queryList();
					if(UtilValidate.isNotEmpty(associatedPartyIdList)){
						List<String> partyList = EntityUtil.getFieldListFromEntityList(associatedPartyIdList, "partyId", true);
						if(UtilValidate.isNotEmpty(partyList)) {
							conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,partyList));
						}
					}else{
						result.put("list", new ArrayList<Map<String, Object>>());
						return doJSONResponse(response, result);
					}
				}
				if(UtilValidate.isNotEmpty(callStatus)) {
					List<EntityCondition> callStatusConditions = new ArrayList<EntityCondition>();
					callStatusConditions.add(EntityCondition.makeCondition("attrValue",EntityOperator.EQUALS,callStatus));
					callStatusConditions.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"CONTACT"));
					Set < String > fieldsToSelect = new TreeSet < String > ();
					fieldsToSelect.add("partyId");
					DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
					dynamicViewEntity.addMemberEntity("WFA", "WorkEffortAttribute");
					dynamicViewEntity.addAlias("WFA", "workEffortId");
					dynamicViewEntity.addAlias("WFA", "attrValue");
					dynamicViewEntity.addMemberEntity("WEC", "WorkEffortContact");
					dynamicViewEntity.addAlias("WEC", "partyId");
					dynamicViewEntity.addAlias("WEC", "workEffortId");
					dynamicViewEntity.addAlias("WEC", "roleTypeId");
					dynamicViewEntity.addViewLink("WFA", "WEC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					int maxRows = 0;
					maxRows = 3000;
					List < GenericValue > partyIdList = EntityQuery.use(delegator).select(fieldsToSelect).from(dynamicViewEntity).where(EntityCondition.makeCondition(callStatusConditions, EntityOperator.AND)).distinct().maxRows(maxRows).queryList();
					if(UtilValidate.isNotEmpty(partyIdList)) {
						List<String> partyList = EntityUtil.getFieldListFromEntityList(partyIdList, "partyId", true);
						conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,partyList));
					}else {
						result.put("list", new ArrayList<Map<String, Object>>());
						return doJSONResponse(response, result);
					}
				}

				//check with emplTeamId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
				}
				
				if (UtilValidate.isNotEmpty(partyId)) {
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE, ""+ partyId + "%"));
				}

				if (UtilValidate.isNotEmpty(firstName)) {
					conditionList.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE, ""+firstName + "%"));
				}
				if (UtilValidate.isNotEmpty(lastName)) {
					conditionList.add(EntityCondition.makeCondition("lastName", EntityOperator.LIKE, ""+lastName + "%"));
				}
				if (UtilValidate.isNotEmpty(createdDateFrom)) {
					Timestamp createdDateFromTs = UtilDateTime.stringToTimeStamp(createdDateFrom, globalDateFormat, TimeZone.getDefault(),locale);
					conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(createdDateFromTs)));
				}
				if (UtilValidate.isNotEmpty(createdDateTo)) {
					Timestamp createdDateToTs = UtilDateTime.stringToTimeStamp(createdDateTo, globalDateFormat, TimeZone.getDefault(),locale);
					conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(createdDateToTs)));
				}
				if (UtilValidate.isNotEmpty(externalId)) {
					conditionList.add(EntityCondition.makeCondition("externalId", EntityOperator.LIKE, ""+externalId + "%"));
				}
				SimpleDateFormat inSDF = new SimpleDateFormat(globalDateFormat);
				SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-MM-dd");
				if(UtilValidate.isNotEmpty(callBackDateFrom)) {
					Date date = inSDF.parse(callBackDateFrom);
					String callBackDate = outSDF.format(date);
					List<GenericValue> partyList = EntityQuery.use(delegator).select("partyId").from("CallRecordMaster").where(EntityCondition.makeCondition("callBackDate",EntityOperator.GREATER_THAN_EQUAL_TO, java.sql.Date.valueOf(callBackDate))).queryList();
					if (UtilValidate.isNotEmpty(partyList)) {
						List<String> partys = EntityUtil.getFieldListFromEntityList(partyList, "partyId", true);
						EntityCondition partyCondition = EntityCondition.makeCondition("partyIdTo",EntityOperator.IN,partys);
						EntityCondition roleCondition = EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"CONTACT");
						List<EntityCondition> partyToList = FastList.newInstance();
						partyToList.add(roleCondition);
						partyToList.add(partyCondition);
						EntityCondition mainCondition = EntityCondition.makeCondition(partyToList,EntityOperator.AND);
						List<GenericValue> partyIdFrom = EntityQuery.use(delegator).select("partyIdFrom").from("PartyRelationship").where(mainCondition).queryList();
						if(UtilValidate.isNotEmpty(partyIdFrom)){
							List<String> partyIdFromStr = EntityUtil.getFieldListFromEntityList(partyIdFrom, "partyIdFrom", true);
							conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdFromStr));
						}
					}else {
						result.put("list", new ArrayList<Map<String, Object>>());
						return doJSONResponse(response, result);
					}
				}
				if(UtilValidate.isNotEmpty(callBackDateTo)) {
					Date date = inSDF.parse(callBackDateTo);
					String callBackDate = outSDF.format(date);
					List<GenericValue> partyList = EntityQuery.use(delegator).select("partyId").from("CallRecordMaster").where(EntityCondition.makeCondition("callBackDate",EntityOperator.LESS_THAN_EQUAL_TO, java.sql.Date.valueOf(callBackDate))).queryList();
					if (UtilValidate.isNotEmpty(partyList)) {
						List<String> partys = EntityUtil.getFieldListFromEntityList(partyList, "partyId", true);
						EntityCondition partyCondition = EntityCondition.makeCondition("partyIdTo",EntityOperator.IN,partys);
						EntityCondition roleCondition = EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"CONTACT");
						List<EntityCondition> partyToList = FastList.newInstance();
						partyToList.add(roleCondition);
						partyToList.add(partyCondition);
						EntityCondition mainCondition = EntityCondition.makeCondition(partyToList,EntityOperator.AND);
						List<GenericValue> partyIdFrom = EntityQuery.use(delegator).select("partyIdFrom").from("PartyRelationship").where(mainCondition).queryList();
						if(UtilValidate.isNotEmpty(partyIdFrom)){
							List<String> partyIdFromStr = EntityUtil.getFieldListFromEntityList(partyIdFrom, "partyIdFrom", true);
							conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdFromStr));
						}
					} else {
						result.put("list", new ArrayList<Map<String, Object>>());
						return doJSONResponse(response, result);
					}
				}
				
				List < EntityCondition > eventExprs = new LinkedList < EntityCondition > ();
	            if (UtilValidate.isNotEmpty(emailAddress) || UtilValidate.isNotEmpty(contactNumber)) {
	                if (UtilValidate.isNotEmpty(emailAddress)) {
	                    eventExprs.add(
	                        EntityCondition.makeCondition("infoString", EntityOperator.LIKE, "" + emailAddress + "%"));
	                }

	                if (UtilValidate.isNotEmpty(contactNumber)) {
	                    eventExprs.add(EntityCondition.makeCondition("contactNumber", EntityOperator.LIKE, "" + contactNumber + "%"));
	                }
	                conditionList.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
	            }
				
				// Contacts of lead when rm selected
				if (UtilValidate.isNotEmpty(responsableFor)) {
					List leadPartyIds = FastList.newInstance();
					List rmCondList = FastList.newInstance();
					rmCondList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, responsableFor));
					rmCondList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"));
					rmCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"));
					EntityCondition rmCond = EntityCondition.makeCondition(rmCondList,EntityOperator.AND);
					List<GenericValue> leadPartyDet = delegator.findList("PartyRelationship", rmCond, UtilMisc.toSet("partyIdFrom"), null, null, false);
					leadPartyIds = EntityUtil.getFieldListFromEntityList(leadPartyDet, "partyIdFrom", true);
					List leadCondList = FastList.newInstance();
					leadCondList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, leadPartyIds));
					leadCondList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"));
					leadCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"));
					leadCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()),EntityOperator.OR,EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null)));
					List<GenericValue> contactPartyList = EntityQuery.use(delegator).select("partyIdFrom").from("PartyRelationship").where(leadCondList).queryList();
					List<String> contactList = EntityUtil.getFieldListFromEntityList(contactPartyList, "partyIdFrom", true);
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, contactList));
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				Debug.logInfo("mainConditons: "+mainConditons, MODULE);

				Set<String> fieldsToSelect = new LinkedHashSet<String>();

				fieldsToSelect.add("partyId");
				fieldsToSelect.add("statusId");
				fieldsToSelect.add("roleTypeId");
				fieldsToSelect.add("preferredCurrencyUomId");
				fieldsToSelect.add("timeZoneDesc");
				fieldsToSelect.add("personalTitle");
				fieldsToSelect.add("callBackDate");
				fieldsToSelect.add("designation");
				fieldsToSelect.add("roleTypeIdFrom");
				fieldsToSelect.add("firstName");
				fieldsToSelect.add("lastName");
				fieldsToSelect.add("departmentName");
				fieldsToSelect.add("pgPartyId");
				fieldsToSelect.add("groupName");
				fieldsToSelect.add("address1");
				fieldsToSelect.add("address2");
				fieldsToSelect.add("city");
				fieldsToSelect.add("postalCode");
				fieldsToSelect.add("countryGeoId");
				fieldsToSelect.add("stateProvinceGeoId");
				fieldsToSelect.add("contactNumber");
				fieldsToSelect.add("countryCode");
				fieldsToSelect.add("areaCode");
				fieldsToSelect.add("infoString");
				fieldsToSelect.add("createdDate");
				fieldsToSelect.add("ownerId");
				fieldsToSelect.add("emplTeamId");
				fieldsToSelect.add("createdTxStamp");
				fieldsToSelect.add("externalId");
				
				boolean isPostalSearch = false;
                /*if (UtilValidate.isNotEmpty(countryGeoId) || UtilValidate.isNotEmpty(stateProvinceGeoId) || UtilValidate.isNotEmpty(postalCode) || UtilValidate.isNotEmpty(city)) {
                	isPostalSearch = true;
                }*/

				DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
				dynamicViewEntity.addMemberEntity("P", "Party");
				//dynamicViewEntity.addAlias("P", "partyId", "partyId", null, false, true, null);
				dynamicViewEntity.addAlias("P", "partyId");
				dynamicViewEntity.addAlias("P", "statusId");
				dynamicViewEntity.addAlias("P", "roleTypeId");
				dynamicViewEntity.addAlias("P", "preferredCurrencyUomId");
				dynamicViewEntity.addAlias("P", "timeZoneDesc");
				dynamicViewEntity.addAlias("P", "emplTeamId");
				dynamicViewEntity.addAlias("P", "ownerId");
				dynamicViewEntity.addAlias("P", "createdDate");
				dynamicViewEntity.addAlias("P", "dataSourceId");
				dynamicViewEntity.addAlias("P", "createdStamp");
				dynamicViewEntity.addAlias("P", "createdTxStamp");
				dynamicViewEntity.addAlias("P", "externalId");

				dynamicViewEntity.addMemberEntity("PER", "Person");
				dynamicViewEntity.addAlias("PER", "firstName");
				dynamicViewEntity.addAlias("PER", "lastName");
				dynamicViewEntity.addAlias("PER", "callBackDate");
				dynamicViewEntity.addAlias("PER", "personalTitle");
				dynamicViewEntity.addAlias("PER", "designation");
				dynamicViewEntity.addAlias("PER", "birthDate");
				dynamicViewEntity.addAlias("PER", "loyaltyId");
				dynamicViewEntity.addViewLink("P", "PER", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
				
				dynamicViewEntity.addMemberEntity("PSD", "PartySupplementalData");
				dynamicViewEntity.addAlias("PSD", "uploadedByUserLoginId");
	            dynamicViewEntity.addAlias("PSD", "departmentName");
	            dynamicViewEntity.addAlias("PSD", "ownershipEnumId");
	            dynamicViewEntity.addAlias("PSD", "industryEnumId");
	            dynamicViewEntity.addAlias("PSD", "annualRevenue");
	            dynamicViewEntity.addAlias("PSD", "sicCode");
	            dynamicViewEntity.addAlias("PSD", "numberEmployees");
	            dynamicViewEntity.addAlias("PSD", "supplementalPartyTypeId");
	            dynamicViewEntity.addAlias("PSD", "primaryPostalAddressId");
	            dynamicViewEntity.addAlias("PSD", "primaryTelecomNumberId");
	            dynamicViewEntity.addAlias("PSD", "primaryEmailId");
				dynamicViewEntity.addViewLink("P", "PSD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));

				if (isPostalSearch) {
					dynamicViewEntity.addMemberEntity("PA", "PostalAddress");
					dynamicViewEntity.addAlias("PA", "toName"); 
					dynamicViewEntity.addAlias("PA", "attnName");
					dynamicViewEntity.addAlias("PA", "address1");
					dynamicViewEntity.addAlias("PA", "address2");
					dynamicViewEntity.addAlias("PA", "directions");
					dynamicViewEntity.addAlias("PA", "city");
					dynamicViewEntity.addAlias("PA", "postalCode");
					dynamicViewEntity.addAlias("PA", "postalCodeExt");
					dynamicViewEntity.addAlias("PA", "countryGeoId");
					dynamicViewEntity.addAlias("PA", "stateProvinceGeoId");
					dynamicViewEntity.addAlias("PA", "countyGeoId");
					dynamicViewEntity.addAlias("PA", "postalCodeGeoId");
					dynamicViewEntity.addViewLink("PSD", "PA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryPostalAddressId", "contactMechId"));
				}

				if (UtilValidate.isNotEmpty(contactNumber)) {
					dynamicViewEntity.addMemberEntity("TN", "TelecomNumber");
					dynamicViewEntity.addAlias("TN", "countryCode");
					dynamicViewEntity.addAlias("TN", "areaCode");
					dynamicViewEntity.addAlias("TN", "contactNumber");
					dynamicViewEntity.addAlias("TN", "askForName");
					dynamicViewEntity.addViewLink("PSD", "TN", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryTelecomNumberId", "contactMechId"));
				}

				if (UtilValidate.isNotEmpty(emailAddress)) {
					dynamicViewEntity.addMemberEntity("CM", "ContactMech");
					dynamicViewEntity.addAlias("CM", "infoString");
					dynamicViewEntity.addViewLink("PSD", "CM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryEmailId", "contactMechId"));
				}
				
				// set the page parameters
	            GenericValue systemProperty = EntityQuery.use(delegator).select("systemPropertyValue")
	                    .from("SystemProperty")
	                    .where("systemResourceId", "general", "systemPropertyId", "fio.grid.fetch.limit")
	                    .queryFirst();
	            
                int viewIndex = 0;
				int highIndex = 0;
				int lowIndex = 0;
				long resultListSize = 0;
				int viewSize = 0;
				
                try {
                    viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
                } catch (Exception e) {
                    viewIndex = 0;
                }

                int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) &&
                    UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?
                    Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;

                try {
                    viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
                } catch (Exception e) {
                    viewSize = fioGridFetch;
                }
                // get the indexes for the partial list
                lowIndex = viewIndex * viewSize;
                highIndex = (viewIndex + 1) * viewSize;
				
                Debug.logInfo("query start: "+UtilDateTime.nowTimestamp(), MODULE);
                
                Debug.logInfo("get actual list start: "+UtilDateTime.nowTimestamp(), MODULE);
                resultList = EntityQuery.use(delegator).limit(viewSize).offset(lowIndex).from(dynamicViewEntity).where(mainConditons).orderBy("-partyId").queryList();
                Debug.logInfo("get actual list end: "+UtilDateTime.nowTimestamp(), MODULE);
                
                Debug.logInfo("get actual list count start: "+UtilDateTime.nowTimestamp(), MODULE);
                resultListSize = QueryUtil.findCountByCondition(delegator, dynamicViewEntity, mainConditons, null, null, null, UtilMisc.toMap("isIncludeGroupBy", "N"));
                Debug.logInfo("get actual list count end: "+UtilDateTime.nowTimestamp(), MODULE);
                
				Debug.logInfo("query end: "+UtilDateTime.nowTimestamp(), MODULE);
				
				Debug.logInfo("list start: "+UtilDateTime.nowTimestamp(), MODULE);
				if(UtilValidate.isNotEmpty(resultList)) {
					
					Debug.logInfo("prepare pre data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					List<String> partyIds = EntityUtil.getFieldListFromEntityList(resultList, "partyId", true);
                	Map<String, Object> timeZones = EnumUtil.getEnumList(delegator, resultList, "timeZoneDesc", "TIME_ZONE");
                	
                	/*Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByPartyIds(delegator, partyNames, resultList, "partyId");*/
                	
					Map<String, Object> statusMap = StatusUtil.getStatusList(delegator, resultList, "statusId", "PARTY_STATUS");
					Map<String, Object> designationMap = EnumUtil.getEnumList(delegator, resultList, "designation", "DESIGNATION");
					Map<String, Object> relatedPartyDetails = PartyHelper.getResponsiblePartyDetailByPartyIds(delegator, partyIds, "CONTACT", "CONTACT_REL_INV");
					
					Map<String, Object> emailAddressList = new LinkedHashMap<>();
					if (UtilValidate.isEmpty(emailAddress)) {
						emailAddressList = CommonDataHelper.getEmailAddressList(delegator, resultList, "primaryEmailId");
					}
					
					Map<String, Object> contactNumberList = new LinkedHashMap<>();
					if (UtilValidate.isEmpty(contactNumber)) {
						contactNumberList = CommonDataHelper.getContactNumberList(delegator, resultList, "primaryTelecomNumberId");
					}
					
					Map<String, Object> postalAddressList = new LinkedHashMap<>();
					if (!isPostalSearch) {
						postalAddressList = CommonDataHelper.getPostalAddressList(delegator, resultList, "primaryPostalAddressId");
					}
					
					Map<String, Object> stateMap = new LinkedHashMap<>();
					Map<String, Object> countryMap = new LinkedHashMap<>();
					if (UtilValidate.isNotEmpty(postalAddressList)) {
						List<String> geoIds = postalAddressList.values().stream().map(x->((GenericValue) x).getString("stateProvinceGeoId")).collect(Collectors.toList());
						stateMap = CommonDataHelper.getGeoNameList(delegator, geoIds, "STATE/PROVINCE");
						
						geoIds = postalAddressList.values().stream().map(x->((GenericValue) x).getString("countryGeoId")).collect(Collectors.toList());
						countryMap = CommonDataHelper.getGeoNameList(delegator, geoIds, "COUNTRY");
					} else {
						stateMap = CommonDataHelper.getGeoNameList(delegator, resultList, "stateProvinceGeoId", "STATE/PROVINCE");
						countryMap = CommonDataHelper.getGeoNameList(delegator, resultList, "countryGeoId", "COUNTRY");
					}
					
					Debug.logInfo("prepare pre data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					long start1 = System.currentTimeMillis();
					Debug.logInfo("prepare actual data start: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					for(GenericValue contact : resultList) {
						
						partyId = contact.getString("partyId");
						String primaryPostalAddressId = (String) contact.get("primaryPostalAddressId");
						String primaryEmailId = (String) contact.get("primaryEmailId");
						String primaryTelecomNumberId = (String) contact.get("primaryTelecomNumberId");
						String personalTitle = contact.getString("personalTitle");

						Map<String, Object> data = new HashMap<String, Object>();
						data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(contact));
						data.put("partyId", partyId);
						
						String name = contact.getString("firstName");
                        if (UtilValidate.isNotEmpty(contact.getString("lastName"))) {
                            if (UtilValidate.isNotEmpty(name)) {
                                name = name + " " + contact.getString("lastName");
                            } else {
                                name = contact.getString("lastName");
                            }
                        }
                        data.put("name", name);
                        
                        Map<String, Object> relatedParty = (Map) relatedPartyDetails.get(partyId);
                        if (UtilValidate.isNotEmpty(relatedParty)) {
                        	data.put("groupName", relatedParty.get("partyName"));
                            data.put("pgPartyId", relatedParty.get("partyId"));
                        }
                        
						/*String countryCode = contact.getString("countryCode");
						countryCode = UtilValidate.isNotEmpty(countryCode)?countryCode.replace("+", ""):countryCode;
						String areaCode = contact.getString("areaCode");*/

						String phoneNumber = "";
                        if (UtilValidate.isNotEmpty(contactNumber)) {
                        	phoneNumber = contact.getString("contactNumber");
                        } else {
                        	phoneNumber = (String) contactNumberList.get(primaryTelecomNumberId);
                        }
						if (UtilValidate.isNotEmpty(phoneNumber)) {
							/*if (countryCode != null && areaCode != null) {
								phoneNumber = "+" + countryCode + "-" + areaCode + "-" + phoneNumber;
							} else if (countryCode != null && areaCode == null) {
								phoneNumber = "+" + countryCode + "-" + phoneNumber;
							} else if (areaCode != null) {
								phoneNumber = areaCode + "-" + phoneNumber;
							}*/
						}
						Map<String,String> primaryPhone = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMapsExt(delegator, partyId);
						phoneNumber = (String) primaryPhone.get("PrimaryPhone");
						data.put("contactNumber", phoneNumber);
						
						String infoString = "";
                        if (UtilValidate.isNotEmpty(emailAddress)) {
                        	infoString = contact.getString("infoString");
                        } else {
                        	infoString = (String) emailAddressList.get(primaryEmailId);
                        }
                        data.put("infoString", infoString);
                        
						String statusId = contact.getString("statusId");
						String statusItemDesc = (String) statusMap.get(statusId);
						data.put("statusDescription", statusItemDesc);
						
						String designation = (String) designationMap.get(contact.getString("designation"));
						data.put("designation", designation);

						String role = contact.getString("roleTypeId");
						data.put("role", role);
						
						String state = "";
                        String country = "";
                        String address1 = "";
                        String address2 = "";
                        if (!isPostalSearch) {
                        	GenericValue postalAddress = (GenericValue) postalAddressList.get(primaryPostalAddressId);
                        	if (UtilValidate.isNotEmpty(postalAddress)) {
                        		city = postalAddress.getString("city");
                            	state = (String) stateMap.get(postalAddress.getString("stateProvinceGeoId"));
                            	country = (String) countryMap.get(postalAddress.getString("countryGeoId"));
                            	postalCode = postalAddress.getString("postalCode");
                            	address1 = postalAddress.getString("address1");
                            	address2 = postalAddress.getString("address2");
                        	}
                        } else {
                        	city = contact.getString("city");
                        	state = (String) stateMap.get(contact.getString("stateProvinceGeoId"));
                        	country = (String) countryMap.get(contact.getString("countryGeoId"));
                        	postalCode = contact.getString("postalCode");
                        	address1 = contact.getString("address1");
                        	address2 = contact.getString("address2");
                        }
                        data.put("city", city);
                        data.put("state", state);
                        data.put("country", country);
                        data.put("postalCode", postalCode);
                        data.put("address1", address1);
                        data.put("address2", address2);
						
						String timeZoneDesc = (String) timeZones.get(contact.getString("timeZoneDesc"));
						data.put("timeZoneDescription", timeZoneDesc);

						String birthDate = contact.getString("birthDate");
						data.put("birthDate", birthDate);

						String createdDateStr = contact.getString("createdDate");
						if (UtilValidate.isNotEmpty(createdDateStr)) {
							createdDateStr = DataUtil.convertDateTimestamp(createdDateStr, new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
						}
						data.put("createdDate", UtilValidate.isNotEmpty(createdDateStr) ? createdDateStr : "");
						
						data.put("personalTitle", personalTitle);
						
						data.put("domainEntityId", partyId);
						data.put("domainEntityType", DomainEntityType.CONTACT);
						data.put("domainEntityTypeDesc", DataHelper.convertToLabel( DomainEntityType.CONTACT ));
						data.put("externalLoginKey", externalLoginKey);
						data.put("externalId", contact.getString("externalId"));

						dataList.add(data);
					}
					Debug.logInfo("prepare actual data end: "+org.fio.homeapps.util.UtilDateTime.nowTimestamp(), MODULE);
					
					long end1 = System.currentTimeMillis();
					Debug.logInfo("timeElapsed for construction --->"+(end1-start1) / 1000f, MODULE);
					result.put("highIndex", Integer.valueOf(highIndex));
					result.put("lowIndex", Integer.valueOf(lowIndex));
					
				}
				Debug.logInfo("list end: "+UtilDateTime.nowTimestamp(), MODULE);
				
				result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
				result.put("totalRecords", nf.format(resultListSize));
				result.put("recordCount", resultListSize);
				result.put("chunkSize", viewSize);
				
				result.put("viewSize", viewSize);
				result.put("viewIndex", viewIndex);
				
				Debug.logInfo("data ready: "+UtilDateTime.nowTimestamp(), MODULE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		Debug.logInfo("try end: "+UtilDateTime.nowTimestamp(), MODULE);
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		return doJSONResponse(response, result);
	}

}
