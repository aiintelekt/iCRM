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

package org.groupfio.lead.portal.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.QueryUtil;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.lead.portal.LeadPortalConstants.LeadSearchType;
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
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
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
    
    public static String searchLeadsOld(HttpServletRequest request, HttpServletResponse response) {
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");		
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		String cif = request.getParameter("cif");
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String partyId = request.getParameter("partyId");
		String localName = request.getParameter("localName");
		String dataSourceId = request.getParameter("dataSourceId");
		String industryEnumId = request.getParameter("industryEnumId");
		String statusId = request.getParameter("statusId");
		
		String externalLoginKey = request.getParameter("externalLoginKey");
		
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		List<GenericValue> resultList = null;
		String searchType = request.getParameter("searchType");
		
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		long start1 = System.currentTimeMillis();
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
				accessMatrixMap.put("entityName", "Lead");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
			}
			//accessLevel = "Y";
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				List<EntityCondition> conditionList = FastList.newInstance();
				
				//check with ownerId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					conditionList.add(EntityCondition.makeCondition("ownerId", EntityOperator.IN, ownerIds));
				}
				
				//check with emplTeamId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
				}
				
				EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "LEAD"));
				conditionList.add(roleTypeCondition);
				
				if (UtilValidate.isNotEmpty(statusId)) {
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
				} else {
					EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
							EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);
					conditionList.add(partyStatusCondition);
				}
				
				if (UtilValidate.isNotEmpty(cif)) {
					partyId = DataUtil.getPartyIdentificationPartyId(delegator, cif, "CIF");
				}
				
				if (UtilValidate.isNotEmpty(localName)) {
					conditionList.add(EntityCondition.makeCondition("groupNameLocal", EntityOperator.LIKE, "%"+localName + "%"));
				}
				
				if (UtilValidate.isNotEmpty(partyId)) {
					EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
					conditionList.add(partyCondition);
				}

				if (UtilValidate.isNotEmpty(name)) {
					EntityCondition nameCondition = EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + name + "%");
					conditionList.add(nameCondition);
				}
				
				if (UtilValidate.isNotEmpty(dataSourceId)) {
					EntityCondition sourceCondition = EntityCondition.makeCondition("dataSourceId", EntityOperator.EQUALS, dataSourceId);
					conditionList.add(sourceCondition);
				}
				
				if (UtilValidate.isNotEmpty(industryEnumId)) {
					EntityCondition industryCondition = EntityCondition.makeCondition("industryEnumId", EntityOperator.EQUALS, industryEnumId);
					conditionList.add(industryCondition);
				}
				
				List<EntityCondition> eventExprs = new LinkedList<EntityCondition>();
				if (UtilValidate.isNotEmpty(email) || UtilValidate.isNotEmpty(phone)) {

					if (UtilValidate.isNotEmpty(email)) {
						eventExprs.add(EntityCondition.makeCondition("primaryEmail", EntityOperator.EQUALS, email));
					}

					if (UtilValidate.isNotEmpty(phone)) {
						eventExprs.add(EntityCondition.makeCondition("primaryContactNumber", EntityOperator.EQUALS, phone));
					}

					conditionList.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
				}
				
				if (UtilValidate.isNotEmpty(searchType) && searchType.equals(LeadSearchType.MY_ACTIVE_LEAD)) {
					EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
	                		//EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
	                		EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")),
	                		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
	                		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"),
	                		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
	                		EntityUtil.getFilterByDateExpr()
	                		), EntityOperator.AND);

					conditionList.add(conditionPR);
				}
				if (UtilValidate.isNotEmpty(searchType) && searchType.equals(LeadSearchType.MY_TEAM_LEADS)) {
					List<EntityCondition> teamCondList = FastList.newInstance();
					
					EntityCondition teamLeadCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.getString("partyId")));
					teamCondList.add(teamLeadCondition);
					EntityCondition teamConditons = EntityCondition.makeCondition(teamCondList, EntityOperator.AND);
					List<GenericValue> teamList = delegator.findList("EmplPositionFulfillment", teamConditons, null, null, null, false);
					List<String> partyIds = null;
					if(UtilValidate.isNotEmpty(teamList)){
						GenericValue emplPosition=teamList.get(0);
						String isTeamLead=emplPosition.getString("isTeamLead");
						
						if(UtilValidate.isNotEmpty(isTeamLead) && "Y".equals(isTeamLead)){
							List<EntityCondition> teamMemberCondList = FastList.newInstance();					
							EntityCondition teamMemberCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")));
							teamMemberCondList.add(teamMemberCondition);
							@SuppressWarnings("unchecked")
							EntityCondition teamMemberConditons = EntityCondition.makeCondition(teamMemberCondList, EntityOperator.AND);
							List<GenericValue> teamMemberList = delegator.findList("EmplPositionFulfillment", teamMemberConditons, null, null, null, false);
							partyIds = EntityUtil.getFieldListFromEntityList(teamMemberList, "partyId", true);
						}
					}
					
					Debug.log("partyIds======="+partyIds);
						EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
		                		EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyIds),
		                		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
		                		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"),
		                		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
		                		EntityUtil.getFilterByDateExpr()
		                		), EntityOperator.AND);
		
						conditionList.add(conditionPR);
						Debug.log("conditionList======="+conditionList);
					
				}
				
				EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
	            efo.setOffset(0);
	            efo.setLimit(1000);
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);	
				
				Set<String> fieldsToSelect = new LinkedHashSet<String>();
	            
	            fieldsToSelect.add("partyId");
	            fieldsToSelect.add("statusId");
	            fieldsToSelect.add("preferredCurrencyUomId");
	            fieldsToSelect.add("timeZoneDesc");
	            fieldsToSelect.add("groupName");
	            fieldsToSelect.add("groupNameLocal");
	            fieldsToSelect.add("ownershipEnumId");
	            fieldsToSelect.add("industryEnumId");
	            fieldsToSelect.add("annualRevenue");
	            fieldsToSelect.add("sicCode");
	            fieldsToSelect.add("numberEmployees");
	            fieldsToSelect.add("primaryAddress1");
	            fieldsToSelect.add("primaryAddress2");
	            fieldsToSelect.add("primaryCity");
	            fieldsToSelect.add("primaryPostalCode");
	            fieldsToSelect.add("primaryCountryGeoId");
	            fieldsToSelect.add("primaryStateProvinceGeoId");
	            fieldsToSelect.add("primaryContactNumber");
	            fieldsToSelect.add("primaryEmail");
	            
	            // set the page parameters
	            GenericValue systemProperty = EntityQuery.use(delegator)
						.select("systemPropertyValue")
						.from("SystemProperty")
						.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
						.queryFirst();
				int viewIndex = 0;
				try {
					viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
				} catch (Exception e) {
					viewIndex = 0;
				}
				result.put("viewIndex", Integer.valueOf(viewIndex));
				
				int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;
				
				int viewSize = fioGridFetch;
				try {
					viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
				} catch (Exception e) {
					viewSize = fioGridFetch;
				}
				result.put("viewSize", Integer.valueOf(viewSize));
				
	            int highIndex = 0;
	            int lowIndex = 0;
	            int resultListSize = 0;
	            
	         // get the indexes for the partial list
            	lowIndex = viewIndex * viewSize + 1;
                highIndex = (viewIndex + 1) * viewSize;
                
                // set distinct on so we only get one row per 
                // using list iterator
                EntityListIterator pli = EntityQuery.use(delegator)
                		.select(fieldsToSelect)
                        .from("AccountSummaryView")
                        .where(mainConditons)
                        .orderBy("-partyId")
                        .cursorScrollInsensitive()
                        .fetchSize(highIndex)
                        .distinct()
                        .cache(true)
                        .queryIterator();
                // get the partial list for this page
                resultList = pli.getPartialList(lowIndex, viewSize);
                resultListSize = pli.getResultsSizeAfterPartialList();
                // close the list iterator
                pli.close();
                
				//List<GenericValue> partySummaryList = delegator.findList("AccountSummaryView", mainConditons, null, UtilMisc.toList("-partyId"), efo, false);
				
				if (UtilValidate.isNotEmpty(resultList)) {
					
					long start2 = System.currentTimeMillis();
					for (GenericValue partySummary : resultList) {
						String state=null;
						Map<String, Object> data = new HashMap<String, Object>();
						
						partyId = partySummary.getString("partyId"); 
						
						String groupName = partySummary.getString("groupName");
						statusId = partySummary.getString("statusId");
						//String statusItemDesc = DataUtil.getStatusDescription(delegator, statusId, "LEAD_STATUS");
						String statusItemDesc = DataUtil.getStatusDescription(delegator, statusId);
						
						String dataSourceDesc = DataUtil.getPartyDataSource(delegator, partyId);
						
						String uomDesc = DataUtil.getUomDescription(delegator, partySummary.getString("preferredCurrencyUomId"), "CURRENCY_MEASURE");
						String timeZoneDesc = EnumUtil.getEnumDescription(delegator, partySummary.getString("timeZoneDesc"), "TIME_ZONE");
						
						String ownershipDesc = EnumUtil.getEnumDescription(delegator, partySummary.getString("ownershipEnumId"), "PARTY_OWNERSHIP");
						String industryDesc = EnumUtil.getEnumDescription(delegator, partySummary.getString("industryEnumId"), "PARTY_INDUSTRY");
						String annualRevenue="";
						if (UtilValidate.isNotEmpty(partySummary.get("annualRevenue"))) {
							DecimalFormat myFormatter = new DecimalFormat("###,###.###");
							annualRevenue = myFormatter.format(partySummary.get("annualRevenue"));
						}
						String sicCode = partySummary.getString("sicCode");
						String numberEmployees = partySummary.getString("numberEmployees");
						
						String phoneNumber = partySummary.getString("primaryContactNumber");
						//String infoString = partySummary.getString("primaryEmail");
						String infoString = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, partyId, "PRIMARY_EMAIL");
						String city = partySummary.getString("primaryCity");
						state = partySummary.getString("primaryStateProvinceGeoId");
						if (UtilValidate.isNotEmpty(state)) {
							state = DataUtil.getGeoName(delegator, state, "STATE,PROVINCE");
							
						}
						String country = partySummary.getString("primaryCountryGeoId");
						String postalCode = partySummary.getString("primaryPostalCode");
						String address1 = partySummary.getString("primaryAddress1");
						String address2 = partySummary.getString("primaryAddress2");
						
						if (UtilValidate.isNotEmpty(country)) {
							country = DataUtil.getGeoName(delegator, country, "COUNTRY");
						}
						
						String relationshipManager = PartyHelper.getCurrentResponsiblePartyName(partyId, "LEAD", delegator);
						
						String primaryContactName = "";
						String primaryContactId = "";
						String primaryContactEmail = "";
						String primaryContactPhone = "";
						Map<String, Object> primaryContact = org.groupfio.common.portal.util.DataUtil.getPrimaryContact(delegator, partyId, "LEAD");
						if (UtilValidate.isNotEmpty(primaryContact)) {
							primaryContactId = (String) primaryContact.get("contactId");
							primaryContactName = (String) primaryContact.get("contactName");
							primaryContactEmail = PartyHelper.getEmailAddress(delegator, primaryContactId, "PRIMARY_EMAIL");
							primaryContactPhone = PartyHelper.getContactNumber(delegator, primaryContactId, "PRIMARY_PHONE");
						}
						data.put("primaryContactName", primaryContactName);
						data.put("primaryContactId", primaryContactId);
						data.put("primaryContactEmail", primaryContactEmail);
						data.put("primaryContactPhone", primaryContactPhone);
						
						data.put("partyId", partyId);
						data.put("groupName", groupName);
						data.put("statusDescription", statusItemDesc);
						data.put("dataSourceDesc", dataSourceDesc);
						data.put("contactNumber", phoneNumber);
						data.put("infoString", infoString);
						data.put("city", city);
						data.put("state", state);
						data.put("country", country);
						data.put("postalCode", postalCode);
						data.put("address1", address1);
						data.put("address2", address2);
						data.put("localName", partySummary.getString("groupNameLocal"));
						data.put("industryDescription", industryDesc);
						data.put("ownershipDescription", ownershipDesc);
						data.put("annualRevenue", annualRevenue);
						data.put("uomDescription", uomDesc);
						data.put("timeZoneDescription", timeZoneDesc);
						data.put("numberEmployees", numberEmployees);
						data.put("sicCode", sicCode);
						data.put("relationshipManager", relationshipManager);
						data.put("insideRep","");
						
						data.put("domainEntityId", partyId);
						data.put("domainEntityType", DomainEntityType.LEAD);
						data.put("domainEntityTypeDesc", DataHelper.convertToLabel( DomainEntityType.LEAD ));
						data.put("externalLoginKey", externalLoginKey);	
						
						dataList.add(data);
	                }
					long end2 = System.currentTimeMillis();
	        		Debug.logInfo("timeElapsed for construction --->"+(end2-start2) / 1000f, MODULE);
					result.put("highIndex", Integer.valueOf(highIndex));
			        result.put("lowIndex", Integer.valueOf(lowIndex));
	            }
				
				result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
				result.put("totalRecords", nf.format(resultListSize));
				result.put("recordCount", resultListSize);
				result.put("chunkSize", viewSize);   
			}
        } catch (Exception e) {
        	Debug.logError(e.getMessage(), MODULE);
    		result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
        }
        long end1 = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end1-start1) / 1000f, MODULE);
		result.put("timeTaken", (end1-start1) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
    }
    
    @SuppressWarnings("serial")
	public static String searchLeads(HttpServletRequest request, HttpServletResponse response) {
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");		
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		String cif = request.getParameter("cif");
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String partyId = request.getParameter("partyId");
		String city= request.getParameter("city");
		String stateProvinceGeoId = request.getParameter("stateProvinceGeoId");
		String countryGeoId = request.getParameter("countryGeoId");
		//String localName = request.getParameter("localName");
		String dataSourceId = request.getParameter("dataSourceId");
		//String industryEnumId = request.getParameter("industryEnumId");
		String statusId = request.getParameter("statusId");
		String externalId = request.getParameter("externalId");
		
		String externalLoginKey = request.getParameter("externalLoginKey");
		
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		//String searchType = request.getParameter("searchType");
		
		String filterBy = (String) context.get("filterBy");
		String filterType = (String) context.get("filterType");
		
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		long start1 = System.currentTimeMillis();
		List<Object> values = new ArrayList<>();
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
				accessMatrixMap.put("entityName", "Lead");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
			}
			//accessLevel = "Y";
			// to skip security matrix for the dashboard elements
			if(UtilValidate.isNotEmpty(filterType)) {
				accessLevel = "Y";
				accessMatrixRes = new HashMap<String, Object>();
			}
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				String userLoginPartyId = userLogin.getString("partyId");
				List<EntityCondition> conditionList = FastList.newInstance();
				
				String _where_condition_ = "";
				String _sql_query_ = "SELECT DISTINCT P.PARTY_ID, P.STATUS_ID, P.PREFERRED_CURRENCY_UOM_ID, P.TIME_ZONE_DESC, \r\n" + 
						"PG.GROUP_NAME, PG.GROUP_NAME_LOCAL, PSD.OWNERSHIP_ENUM_ID, PSD.INDUSTRY_ENUM_ID, \r\n" + 
						"PSD.ANNUAL_REVENUE, PSD.SIC_CODE, PSD.NUMBER_EMPLOYEES, \r\n" +
						"PSD.PRIMARY_POSTAL_ADDRESS_ID, PSD.PRIMARY_EMAIL_ID, PSD.PRIMARY_TELECOM_NUMBER_ID, \r\n" +
						"P.OWNER_ID, P.EMPL_TEAM_ID, P.EXTERNAL_ID, PG.GSTN_NO, P.CREATED_TX_STAMP \r\n" + 
						"FROM PARTY P \r\n" + 
						"INNER JOIN PARTY_GROUP PG ON P.PARTY_ID = PG.PARTY_ID \r\n" + 
						//"LEFT OUTER JOIN PARTY_RELATIONSHIP PR ON P.PARTY_ID = PR.PARTY_ID_FROM \r\n" + 
						"LEFT OUTER JOIN PARTY_SUPPLEMENTAL_DATA PSD ON P.PARTY_ID = PSD.PARTY_ID \r\n";
				String _count_sql_query_ = "SELECT COUNT(DISTINCT P.PARTY_ID) as 'totalRecord' \r\n" + 
						"FROM PARTY P \r\n" + 
						"INNER JOIN PARTY_GROUP PG ON P.PARTY_ID = PG.PARTY_ID \r\n" + 
						//"LEFT OUTER JOIN PARTY_RELATIONSHIP PR ON P.PARTY_ID = PR.PARTY_ID_FROM \r\n" + 
						"LEFT OUTER JOIN PARTY_SUPPLEMENTAL_DATA PSD ON P.PARTY_ID = PSD.PARTY_ID \r\n";
				
				//check with ownerId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") + " P.OWNER_ID IN ("+org.fio.admin.portal.util.DataUtil.toList(ownerIds, "")+")";
				}
				
				//check with emplTeamId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");

					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") + " P.EMPL_TEAM_ID IN ("+org.fio.admin.portal.util.DataUtil.toList(emplTeamIds, "")+")";
				}
				
				_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.ROLE_TYPE_ID ='LEAD'";
				
				
				
				if (UtilValidate.isNotEmpty(statusId) && !statusId.equals("ACTIVE")) {
					if(statusId.contains("-"))
						statusId = statusId.replace("-", "_");
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.STATUS_ID = ? ";
					values.add(statusId);
				} else {
					// filter active lead
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" (P.STATUS_ID <> 'PARTY_DISABLED' OR P.STATUS_ID IS NULL)";
				}
				
				if (UtilValidate.isNotEmpty(cif)) {
					partyId = DataUtil.getPartyIdentificationPartyId(delegator, cif, "CIF");
				}
				
				/*if (UtilValidate.isNotEmpty(localName)) {
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PG.GROUP_NAME_LOCAL LIKE '%"+localName+"%'";
				}*/
				
				if (UtilValidate.isNotEmpty(partyId)) {
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.PARTY_ID LIKE '%"+partyId+"%'";
				}
				if (UtilValidate.isNotEmpty(countryGeoId) || UtilValidate.isNotEmpty(stateProvinceGeoId) || (UtilValidate.isNotEmpty(city))) {
					_sql_query_ = _sql_query_ + "LEFT OUTER JOIN POSTAL_ADDRESS PA ON PSD.PRIMARY_POSTAL_ADDRESS_ID = PA.CONTACT_MECH_ID \r\n";
					_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN POSTAL_ADDRESS PA ON PSD.PRIMARY_POSTAL_ADDRESS_ID = PA.CONTACT_MECH_ID \r\n";
					if (UtilValidate.isNotEmpty(countryGeoId)) {
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PA.COUNTRY_GEO_ID ='"+countryGeoId+"'";
					}
					if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PA.STATE_PROVINCE_GEO_ID ='"+stateProvinceGeoId+"'";
					}
					if (UtilValidate.isNotEmpty(city)) {
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PA.CITY ='"+city+"'";
					}
				}
				if (UtilValidate.isNotEmpty(name)) {
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PG.GROUP_NAME LIKE '%"+name+"%'";
				}
				if (UtilValidate.isNotEmpty(externalId)) {
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.EXTERNAL_ID LIKE '%"+externalId+"%'";
				}
				
				if (UtilValidate.isNotEmpty(dataSourceId)) {
					_sql_query_ = _sql_query_ + "LEFT OUTER JOIN PARTY_DATA_SOURCE PDS ON P.PARTY_ID = PDS.PARTY_ID \r\n";
					_count_sql_query_ = _count_sql_query_ +  "LEFT OUTER JOIN PARTY_DATA_SOURCE PDS ON P.PARTY_ID = PDS.PARTY_ID \r\n";
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PDS.DATA_SOURCE_ID ='"+dataSourceId+"'";
				}
				/*if (UtilValidate.isNotEmpty(industryEnumId)) {
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PSD.INDUSTRY_ENUM_ID ='"+industryEnumId+"'";
				}*/
				
				if (UtilValidate.isNotEmpty(email) || UtilValidate.isNotEmpty(phone)) {
					if (UtilValidate.isNotEmpty(email)) {
						_sql_query_ = _sql_query_ + "LEFT OUTER JOIN CONTACT_MECH CM ON PSD.PRIMARY_EMAIL_ID = CM.CONTACT_MECH_ID \r\n";
						_count_sql_query_ = _count_sql_query_ +  "LEFT OUTER JOIN CONTACT_MECH CM ON PSD.PRIMARY_EMAIL_ID = CM.CONTACT_MECH_ID \r\n";
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
								+ " CM.INFO_STRING LIKE '%"+email+"%'";
					}

					if (UtilValidate.isNotEmpty(phone)) {
						_sql_query_ = _sql_query_ + "LEFT OUTER JOIN TELECOM_NUMBER TN ON PSD.PRIMARY_TELECOM_NUMBER_ID = TN.CONTACT_MECH_ID \r\n";
						_count_sql_query_ = _count_sql_query_ +  "LEFT OUTER JOIN TELECOM_NUMBER TN ON PSD.PRIMARY_TELECOM_NUMBER_ID = TN.CONTACT_MECH_ID \r\n";
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
								+ " TN.CONTACT_NUMBER LIKE '%"+phone+"%'";
					}
				}
				
				if (UtilValidate.isNotEmpty(filterType) && "my-lead".equals(filterType)) {
					
					_sql_query_ = _sql_query_ + "LEFT OUTER JOIN PARTY_RELATIONSHIP PR1 ON P.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
					_count_sql_query_ = _count_sql_query_ +  "LEFT OUTER JOIN PARTY_RELATIONSHIP PR1 ON P.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
							+ " PR1.PARTY_ID_TO ='"+userLoginPartyId+"'"
							+ " AND PR1.ROLE_TYPE_ID_TO = 'ACCOUNT_MANAGER'"
							+ " AND PR1.ROLE_TYPE_ID_FROM = 'LEAD'"
							+ " AND PR1.PARTY_RELATIONSHIP_TYPE_ID= 'RESPONSIBLE_FOR'"
							+ " AND ((PR1.THRU_DATE IS NULL OR PR1.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') AND (PR1.FROM_DATE IS NULL OR PR1.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))";
					
				} else if (UtilValidate.isNotEmpty(filterType) && "my-team-lead".equals(filterType)) {
					//List<EntityCondition> teamCondList = FastList.newInstance();
					
					List<GenericValue> emplPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("partyId",userLoginPartyId).filterByDate().queryList();
					List<String> teams = UtilValidate.isNotEmpty(emplPositionFulfillments) ? EntityUtil.getFieldListFromEntityList(emplPositionFulfillments, "emplTeamId", true) : new ArrayList<>();
					if(UtilValidate.isEmpty(teams)) teams.add("00000000221");
					
					List<String> partyIds = new ArrayList<String>() {{add("00000000121");}};
					
					List<GenericValue> teamPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, teams)).queryList();
					partyIds = EntityUtil.getFieldListFromEntityList(teamPositionFulfillments, "partyId", true);
					
					Debug.log("partyIds======="+partyIds);
					
					_sql_query_ = _sql_query_ + "LEFT OUTER JOIN PARTY_RELATIONSHIP PR1 ON P.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
					_count_sql_query_ = _count_sql_query_ +  "LEFT OUTER JOIN PARTY_RELATIONSHIP PR1 ON P.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
							+ " PR1.PARTY_ID_TO IN ("+org.fio.admin.portal.util.DataUtil.toList(partyIds, "")+")"
							+ " AND PR1.ROLE_TYPE_ID_TO = 'ACCOUNT_MANAGER'"
							+ " AND PR1.ROLE_TYPE_ID_FROM = 'LEAD'"
							+ " AND PR1.PARTY_RELATIONSHIP_TYPE_ID= 'RESPONSIBLE_FOR'"
							+ " AND ((PR1.THRU_DATE IS NULL OR PR1.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') AND (PR1.FROM_DATE IS NULL OR PR1.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))";
					
				}  else if (UtilValidate.isNotEmpty(filterType) && "my-bu-lead".equals(filterType)) {
					
				}
				
	            
	            // set the page parameters
	            GenericValue systemProperty = EntityQuery.use(delegator)
						.select("systemPropertyValue")
						.from("SystemProperty")
						.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
						.queryFirst();
				int viewIndex = 0;
				try {
					viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
				} catch (Exception e) {
					viewIndex = 0;
				}
				result.put("viewIndex", Integer.valueOf(viewIndex));
				
				int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;
				
				int viewSize = fioGridFetch;
				try {
					viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
				} catch (Exception e) {
					viewSize = fioGridFetch;
				}
				result.put("viewSize", Integer.valueOf(viewSize));
				
				Debug.logInfo("findLead mainConditons: "+_where_condition_, MODULE);
				
	            int highIndex = 0;
	            int lowIndex = 0;
	            int resultListSize = 0;
	         // get the indexes for the partial list
            	lowIndex = viewIndex * viewSize;
            	//lowIndex = viewIndex * viewSize + 1;
                highIndex = (viewIndex + 1) * viewSize;
                Debug.logInfo("query start: "+UtilDateTime.nowTimestamp(), MODULE);
                // set distinct on so we only get one row per 
                
                ResultSet rs = null;
                _count_sql_query_ = _count_sql_query_ + " WHERE "+_where_condition_;
                // get the total count 
                rs = QueryUtil.getResultSet(_count_sql_query_, values, delegator);
                if (rs != null) {
					while (rs.next()) {
						resultListSize = (int) rs.getLong("totalRecord");
					}
                }
                String _final_sql_script = "SELECT * FROM ("+_sql_query_+" WHERE "+_where_condition_ + " LIMIT "+lowIndex+", "+viewSize+") temp ORDER BY CREATED_TX_STAMP DESC";

                Debug.log("_final_sql_script ---->"+_final_sql_script, MODULE);
                rs = QueryUtil.getResultSet(_final_sql_script, values, delegator);
                List<String> partyIds = new ArrayList<String>();
                if (rs != null) {
                	ResultSetMetaData rsMetaData = rs.getMetaData();
                    List<String> columnList = new ArrayList<String>();
                    //Retrieving the list of column names
                    int count = rsMetaData.getColumnCount();
                    for(int i = 1; i<=count; i++) {
                    	columnList.add(rsMetaData.getColumnName(i));
                    }
                    
                	while (rs.next()) {
						Map<String, Object> data = new HashMap<String, Object>();
						for(String columName : columnList) {
							String fieldName = ModelUtil.dbNameToVarName(columName);
							String fieldValue = rs.getString(columName);
							if("partyId".equals(fieldName))
								partyIds.add(fieldValue);
							/*
							if(org.fio.admin.portal.util.DataUtil.isDigits(fieldValue))
								fieldValue = org.fio.admin.portal.util.DataUtil.getFormattedNumValue(delegator, fieldValue);
							*/
							data.put(fieldName, fieldValue);
						}
						resultList.add(data);
					}
				}
                //resultListSize = resultList.size();
                Debug.logInfo("query end: "+UtilDateTime.nowTimestamp(), MODULE);
                
                Debug.logInfo("list start: "+UtilDateTime.nowTimestamp(), MODULE);
				if(UtilValidate.isNotEmpty(resultList)) {
					long start2 = System.currentTimeMillis();
					Debug.logInfo("data con start: "+UtilDateTime.nowTimestamp(), MODULE);
					List<GenericValue> timeZoneGvList = EntityQuery.use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","TIME_ZONE").cache(true).queryList();
					Map<String, Object> timeZoneMap = org.fio.admin.portal.util.DataUtil.getMapFromGeneric(timeZoneGvList, "enumId", "description", false);
					
					List<GenericValue> ownershipGvList = EntityQuery.use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","PARTY_OWNERSHIP").cache(true).queryList();
					Map<String, Object> ownershipMap = org.fio.admin.portal.util.DataUtil.getMapFromGeneric(ownershipGvList, "enumId", "description", false);
					
					List<GenericValue> industryGvList = EntityQuery.use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","PARTY_INDUSTRY").cache(true).queryList();
					Map<String, Object> industryMap = org.fio.admin.portal.util.DataUtil.getMapFromGeneric(industryGvList, "enumId", "description", false);
					
					List<GenericValue> statusGvList = EntityQuery.use(delegator).select("statusId","description").from("StatusItem").where("statusTypeId","PARTY_STATUS").cache(true).queryList();
					Map<String, Object> statusMap = org.fio.admin.portal.util.DataUtil.getMapFromGeneric(statusGvList, "statusId", "description", false);
					
					
					Map<String, Object> partyDataSrcMap = DataUtil.getPartyDataSourceByPartyId(delegator, partyIds,true);
					
					Map<String, Object> uomMap = org.fio.admin.portal.util.DataUtil.getUomDescriptionList(delegator, "CURRENCY_MEASURE");
					Map<String, Object> stateMap = org.fio.admin.portal.util.DataUtil.getGeoNameList(delegator, "STATE/PROVINCE");
					Map<String, Object> countryMap = org.fio.admin.portal.util.DataUtil.getGeoNameList(delegator, "COUNTRY");
					
					Map<String, Object> responsiblePartyMap = PartyHelper.getResponsiblePartyByPartyIds(delegator, partyIds, "LEAD");
					
					Debug.logInfo("data con end: "+UtilDateTime.nowTimestamp(), MODULE);
					
					for (Map<String, Object> partySummary : resultList) {
						partyId = (String) partySummary.get("partyId");
						
						Map<String, Object> data = new HashMap<String, Object>();
						data.putAll(partySummary);
						

						String annualRevenue="";
						if (UtilValidate.isNotEmpty(partySummary.get("annualRevenue"))) {
							double anuRev = Double.parseDouble((String) partySummary.get("annualRevenue"));
							DecimalFormat myFormatter = new DecimalFormat("###,###.00");
							annualRevenue = myFormatter.format(anuRev);
						}
						
						String postalContactMechId = (String) partySummary.get("primaryPostalAddressId");
						String emailContactMechId = (String) partySummary.get("primaryEmailId");
						String telecomContactMechId = (String) partySummary.get("primaryTelecomNumberId");
						
						String address1 = "";
						String address2 = "";
						String genCity = "";
						String postalCode = "";
						String genCountryGeoId = "";
						String genStateProvinceGeoId = "";
						String infoString = "";
						String contactNumber = "";
						statusId = (String) partySummary.get("statusId");
						data.put("partyId", partyId);
						data.put("externalId", (String) partySummary.get("externalId"));
						data.put("gstnNo", (String) partySummary.get("gstnNo"));
						
						data.put("statusDescription", UtilValidate.isNotEmpty(statusMap) && UtilValidate.isNotEmpty(statusId) ? statusMap.get(statusId) : "");
						data.put("dataSourceDesc", UtilValidate.isNotEmpty(partyDataSrcMap) && UtilValidate.isNotEmpty(partyId) ? partyDataSrcMap.get(partyId) : "");
						
						data.put("industryDescription", UtilValidate.isNotEmpty((String) partySummary.get("industryEnumId")) ? industryMap.get((String) partySummary.get("industryEnumId")) : "");
						data.put("ownershipDescription", UtilValidate.isNotEmpty((String) partySummary.get("ownershipEnumId")) ? ownershipMap.get((String) partySummary.get("ownershipEnumId")) : "");
						data.put("annualRevenue", annualRevenue);
						data.put("uomDescription", UtilValidate.isNotEmpty(uomMap) && UtilValidate.isNotEmpty((String) partySummary.get("preferredCurrencyUomId")) ? uomMap.get((String) partySummary.get("preferredCurrencyUomId")) : "");
						
						data.put("timeZoneDescription", UtilValidate.isNotEmpty((String) partySummary.get("timeZoneDesc")) ? timeZoneMap.get((String) partySummary.get("timeZoneDesc")) :"");
						data.put("insideRep","");
						
						
						//Map<String, Object> primaryContactMap = UtilValidate.isNotEmpty(primaryContactDetails) ? primaryContactDetails.get(partyId) : new HashMap<String, Object>();
						/*
						String primaryContactName = "";
						String primaryContactId = "";
						String primaryContactEmail = "";
						String primaryContactPhone = "";
						Map<String, Object> primaryContact = org.groupfio.common.portal.util.DataUtil.getPrimaryContact(delegator, partyId, "ACCOUNT");
						if (UtilValidate.isNotEmpty(primaryContact)) {
							primaryContactId = (String) primaryContact.get("contactId");
							primaryContactName = (String) primaryContact.get("contactName");
							primaryContactEmail = PartyHelper.getEmailAddress(delegator, primaryContactId, "PRIMARY_EMAIL");
							primaryContactPhone = PartyHelper.getContactNumber(delegator, primaryContactId, "PRIMARY_PHONE");
						}
						data.put("primaryContactName", primaryContactName);
						data.put("primaryContactId", primaryContactId);
						data.put("primaryContactEmail", primaryContactEmail);
						data.put("primaryContactPhone", primaryContactPhone);
						*/
						GenericValue postalAddress = PartyHelper.getPrimaryPostalAddress(delegator, postalContactMechId);
						if(UtilValidate.isNotEmpty(postalAddress)) {
							address1 = postalAddress.getString("address1");
							address2 = postalAddress.getString("address2");
							genCity = postalAddress.getString("city");
							postalCode = postalAddress.getString("postalCode");
							genCountryGeoId = postalAddress.getString("countryGeoId");
							genStateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
							
						}
						
						GenericValue emailAddress = PartyHelper.getPrimaryEmailAddress(delegator, emailContactMechId);
						if(UtilValidate.isNotEmpty(emailAddress))
							infoString = emailAddress.getString("infoString");
						
						GenericValue telecomNumber = PartyHelper.getPrimaryTelecomNumber(delegator, telecomContactMechId);
						if(UtilValidate.isNotEmpty(telecomNumber))
							contactNumber = telecomNumber.getString("contactNumber");
						
						Map<String,String> primaryPhone = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMapsExt(delegator, partyId);
						contactNumber = (String) primaryPhone.get("PrimaryPhone");
						
						data.put("address1", address1);
						data.put("address2", address2);
						data.put("city", genCity);
						data.put("postalCode", postalCode);
						data.put("countryGeoId", genCountryGeoId);
						data.put("stateProvinceGeoId", genStateProvinceGeoId);
						
						data.put("state", UtilValidate.isNotEmpty(stateMap) && UtilValidate.isNotEmpty(genStateProvinceGeoId) ? stateMap.get(genStateProvinceGeoId) : "");
						data.put("country", UtilValidate.isNotEmpty(countryMap) && UtilValidate.isNotEmpty(genCountryGeoId) ? countryMap.get(genCountryGeoId) : "");
						
						data.put("infoString", infoString);
						data.put("contactNumber", contactNumber);
						
						data.put("relationshipManager", UtilValidate.isNotEmpty(responsiblePartyMap) ? responsiblePartyMap.get(partyId) : "");
						
						data.put("domainEntityId", partyId);
						data.put("domainEntityType", DomainEntityType.LEAD);
						data.put("domainEntityTypeDesc", DataHelper.convertToLabel( DomainEntityType.LEAD ));
						data.put("externalLoginKey", externalLoginKey);	
						
						
						String primaryContactName = "";
						String primaryContactId = "";
						String primaryContactEmail = "";
						String primaryContactPhone = "";
						Map<String, Object> primaryContact = org.groupfio.common.portal.util.DataUtil.getPrimaryContact(delegator, partyId, "LEAD");
						if (UtilValidate.isNotEmpty(primaryContact)) {
							primaryContactId = (String) primaryContact.get("contactId");
							primaryContactName = (String) primaryContact.get("contactName");
							primaryContactEmail = PartyHelper.getEmailAddress(delegator, primaryContactId, "PRIMARY_EMAIL");
							primaryContactPhone = PartyHelper.getContactNumber(delegator, primaryContactId, "PRIMARY_PHONE");
						}
						data.put("primaryContactName", primaryContactName);
						data.put("primaryContactId", primaryContactId);
						data.put("primaryContactEmail", primaryContactEmail);
						data.put("primaryContactPhone", primaryContactPhone);
						dataList.add(data);
	                }
					long end2 = System.currentTimeMillis();
	        		Debug.logInfo("timeElapsed for construction --->"+(end2-start2) / 1000f, MODULE);
					result.put("highIndex", Integer.valueOf(highIndex));
			        result.put("lowIndex", Integer.valueOf(lowIndex));
	            }
				
				result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
				result.put("totalRecords", nf.format(resultListSize));
				result.put("recordCount", resultListSize);
				result.put("chunkSize", viewSize);   
			}
        } catch (Exception e) {
        	Debug.logError(e.getMessage(), MODULE);
    		result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
        }
        long end1 = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end1-start1) / 1000f, MODULE);
		result.put("timeTaken", (end1-start1) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
    }
    public static String getRelatedParties(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String fromPartyId = (String) context.get("partyId");
		try {
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			List < String > relatedPartyIds = new ArrayList<String>();
			
			if (UtilValidate.isNotEmpty(fromPartyId)) {
				conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, fromPartyId));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < GenericValue > relatedPartiesList = EntityQuery.use(delegator).from("PartyRelationship").where(condition).maxRows(100).queryList();
			if (UtilValidate.isNotEmpty(relatedPartiesList)) {
				for (GenericValue relatedParty: relatedPartiesList) {
					String relatedPartyId = relatedParty.getString("partyIdFrom");
					String roleTypeIdFrom = relatedParty.getString("roleTypeIdFrom");
					if("CONTACT".equals(roleTypeIdFrom)){
						relatedPartyIds.add(relatedPartyId);
					}
				}
			}
			List<EntityCondition> personCondList = FastList.newInstance();
		 	personCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, relatedPartyIds));
			EntityCondition personCondition = EntityCondition.makeCondition(personCondList, EntityOperator.AND);
			Set < String > fieldToSelect = new TreeSet<String> ();
			fieldToSelect.add("partyId");
			fieldToSelect.add("userLoginId");
			fieldToSelect.add("firstName");
			fieldToSelect.add("lastName");
			
			if (UtilValidate.isNotEmpty(personCondition)) {
				List<GenericValue> personList = EntityQuery.use(delegator).select("partyId","firstName").from("Person").where(personCondition).queryList();
				if (UtilValidate.isNotEmpty(personList)) {
					for (GenericValue person: personList) {
						Map<String, Object> data = new HashMap<String, Object>();
						String userPartyId = person.getString("partyId");
						String firstName = person.getString("firstName");
						String lastName = person.getString("lastName");
						String userName = firstName + (UtilValidate.isNotEmpty(lastName) ? " " + lastName : "");
						data.put("partyId", userPartyId);
						data.put("userName", userName);
						results.add(data);
					}
				}
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);

			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}
    
    /**
     * @author Mahendran
     * @param request
     * @param response
     * @return Success/Error Json string 
     */
    public static String findLeads(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<GenericValue> resultList = null;
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		
		long start = System.currentTimeMillis();
		String localName = (String) context.get("localName");
		String cif = (String) context.get("cif");
		String name = (String) context.get("name");
		String partyId = (String) context.get("partyId");
		String email = (String) context.get("email");
		String phone = (String) context.get("phone");
		String searchType = (String) context.get("searchType");
		
		String externalLoginKey = (String) context.get("externalLoginKey");
		
		String postalAddress = UtilValidate.isNotEmpty(context.get("postalAddress")) ? (String) context.get("postalAddress") : "Y";
		String telecom = UtilValidate.isNotEmpty(context.get("telecom")) ? (String) context.get("telecom") : "Y";
		String emailAddress = UtilValidate.isNotEmpty(context.get("emailAddress")) ? (String) context.get("emailAddress") : "Y";
		try {
			
			//get the default general grid fetch limit
			GenericValue systemProperty = EntityQuery.use(delegator)
											.select("systemPropertyValue")
											.from("SystemProperty")
											.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
											.queryFirst();
			// set the page parameters
	        int viewIndex = 0;
	        try {
	            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
	        } catch (Exception e) {
	            viewIndex = 0;
	        }
	        result.put("viewIndex", Integer.valueOf(viewIndex));

	        int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;
	        
	        int viewSize = fioGridFetch;
	        try {
	            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
	        } catch (Exception e) {
	            viewSize = fioGridFetch;
	        }
	        result.put("viewSize", Integer.valueOf(viewSize));
	        
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "LEAD"));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR));
			
			if (UtilValidate.isNotEmpty(cif)) {
				partyId = org.fio.homeapps.util.DataUtil.getPartyIdentificationPartyId(delegator, cif, "CIF");
			}
			
			if (UtilValidate.isNotEmpty(localName)) {
				conditions.add(EntityCondition.makeCondition("groupNameLocal", EntityOperator.LIKE, "%"+localName + "%"));
			}
			
			if (UtilValidate.isNotEmpty(partyId)) {
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			}

			if (UtilValidate.isNotEmpty(name)) {
				conditions.add(EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + name + "%"));
			}
			if (UtilValidate.isNotEmpty(email)) {
				conditions.add(EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, email));
			}

			if (UtilValidate.isNotEmpty(phone)) {
				conditions.add(EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, phone));
			}
			if (UtilValidate.isNotEmpty(searchType) && searchType.equals(LeadSearchType.MY_ACTIVE_LEAD)) {
				EntityCondition condition1 = EntityCondition.makeCondition(UtilMisc.toList(
                		//EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
                		EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")),
                		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
                		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"),
                		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
                		EntityUtil.getFilterByDateExpr()
                		), EntityOperator.AND);

				conditions.add(condition1);
			}
			
			DynamicViewEntity dynamicView = new DynamicViewEntity();

            // default view settings
            dynamicView.addMemberEntity("PT", "Party");
            dynamicView.addAlias("PT", "partyId");
            dynamicView.addAlias("PT", "statusId");
            dynamicView.addAlias("PT", "roleTypeId");
            dynamicView.addAlias("PT", "preferredCurrencyUomId");
            dynamicView.addAlias("PT", "timeZoneDesc");
            dynamicView.addAlias("PT", "createdTxStamp");
            dynamicView.addAlias("PT", "lastUpdatedTxStamp");
            
        	dynamicView.addMemberEntity("PG", "PartyGroup");
            dynamicView.addAlias("PG", "groupName");
            dynamicView.addAlias("PG", "localName","groupNameLocal",null,null,null,null);
            dynamicView.addViewLink("PT", "PG", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
            
            //add party supplemental data
            dynamicView.addMemberEntity("PSD", "PartySupplementalData");
            dynamicView.addAlias("PSD", "partyFirstName");
            dynamicView.addAlias("PSD", "partyLastName");
            dynamicView.addAlias("PSD", "departmentName");
            dynamicView.addAlias("PSD", "ownershipEnumId");
            dynamicView.addAlias("PSD", "industryEnumId");
            dynamicView.addAlias("PSD", "annualRevenue");
            dynamicView.addAlias("PSD", "sicCode");
            dynamicView.addAlias("PSD", "numberEmployees");
            dynamicView.addAlias("PSD", "primaryPostalAddressId");
            dynamicView.addAlias("PSD", "primaryTelecomNumberId");
            dynamicView.addAlias("PSD", "primaryEmailId");
            dynamicView.addViewLink("PT", "PSD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
            
            if(UtilValidate.isNotEmpty(postalAddress) && "Y".equals(postalAddress)) {
            	dynamicView.addMemberEntity("PA", "PostalAddress");
                dynamicView.addAlias("PA", "toName");
                dynamicView.addAlias("PA", "address1");
                dynamicView.addAlias("PA", "address2");
                dynamicView.addAlias("PA", "city");
                dynamicView.addAlias("PA", "postalCode");
                dynamicView.addAlias("PA", "postalCodeExt");
                dynamicView.addAlias("PA", "countryGeoId");
                dynamicView.addAlias("PA", "stateProvinceGeoId");
                dynamicView.addViewLink("PSD", "PA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryPostalAddressId", "contactMechId"));
            }
            
            if(UtilValidate.isNotEmpty(telecom) && "Y".equals(telecom)) {
            	dynamicView.addMemberEntity("TEL", "TelecomNumber");
                dynamicView.addAlias("TEL", "countryCode");
                dynamicView.addAlias("TEL", "areaCode");
                dynamicView.addAlias("TEL", "contactNumber");
                dynamicView.addViewLink("PSD", "TEL", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryTelecomNumberId", "contactMechId"));
            }
            
            if(UtilValidate.isNotEmpty(emailAddress) && "Y".equals(emailAddress)) {
            	dynamicView.addMemberEntity("CM", "ContactMech");
                dynamicView.addAlias("CM", "infoString");
                dynamicView.addViewLink("PSD", "CM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryEmailId", "contactMechId"));
            }
            Set<String> fieldsToSelect = new LinkedHashSet<String>();
            
            fieldsToSelect.add("partyId");fieldsToSelect.add("statusId");fieldsToSelect.add("roleTypeId");fieldsToSelect.add("preferredCurrencyUomId");fieldsToSelect.add("timeZoneDesc");fieldsToSelect.add("createdTxStamp");fieldsToSelect.add("lastUpdatedTxStamp");
            fieldsToSelect.add("groupName");fieldsToSelect.add("localName");fieldsToSelect.add("partyFirstName");fieldsToSelect.add("partyLastName");fieldsToSelect.add("departmentName");fieldsToSelect.add("ownershipEnumId");
            fieldsToSelect.add("industryEnumId");fieldsToSelect.add("annualRevenue");fieldsToSelect.add("sicCode");fieldsToSelect.add("numberEmployees");fieldsToSelect.add("address1");fieldsToSelect.add("address2");
            fieldsToSelect.add("city");fieldsToSelect.add("postalCode");fieldsToSelect.add("postalCodeExt");fieldsToSelect.add("countryGeoId");fieldsToSelect.add("stateProvinceGeoId");fieldsToSelect.add("countryCode");
            fieldsToSelect.add("areaCode");fieldsToSelect.add("contactNumber");fieldsToSelect.add("infoString");
            
            int highIndex = 0;
            int lowIndex = 0;
            int resultListSize = 0;
            try {
                // get the indexes for the partial list
            	lowIndex = viewIndex * viewSize + 1;
                highIndex = (viewIndex + 1) * viewSize;
                
                // set distinct on so we only get one row per 
                // using list iterator
                EntityListIterator pli = EntityQuery.use(delegator)
                		.select(fieldsToSelect)
                        .from(dynamicView)
                        .where(EntityCondition.makeCondition(conditions, EntityOperator.AND))
                        .orderBy("-createdTxStamp")
                        .cursorScrollInsensitive()
                        .fetchSize(highIndex)
                        .distinct()
                        .cache(true)
                        .queryIterator();
                // get the partial list for this page
                resultList = pli.getPartialList(lowIndex, viewSize);

                // attempt to get the full size
                resultListSize = pli.getResultsSizeAfterPartialList();
                // close the list iterator
                pli.close();
            } catch (GenericEntityException e) {
                String errMsg = "Error: " + e.toString();
                Debug.logError(e, errMsg, MODULE);
            }
            
            //get static valuess
            List<GenericValue> partyStatus = EntityQuery.use(delegator).select("statusId","description").from("StatusItem").where("statusTypeId","PARTY_STATUS").cache(true).queryList();
            Map<String, Object> partyStatusMap = DataUtil.getMapFromGeneric(partyStatus, "statusId", "description", false);
            
            List<GenericValue> uom = EntityQuery.use(delegator).select("uomId","description").from("Uom").where("uomTypeId","CURRENCY_MEASURE").cache(true).queryList();
            Map<String, Object> uomMap = DataUtil.getMapFromGeneric(uom, "uomId", "description", false);
            
            List<GenericValue> timeZone = EntityQuery.use(delegator).select("enumId","enumCode","description").from("Enumeration").where("enumTypeId","TIME_ZONE").cache(true).queryList();
            Map<String, Object> timeZoneMap = DataUtil.getMapFromGeneric(timeZone, "enumId", "description", false);
            
            List<GenericValue> ownership = EntityQuery.use(delegator).select("enumId","enumCode","description").from("Enumeration").where("enumTypeId","PARTY_OWNERSHIP").cache(true).queryList();
            Map<String, Object> ownershipMap = DataUtil.getMapFromGeneric(ownership, "enumCode", "description", false);
            
            List<GenericValue> industry = EntityQuery.use(delegator).select("enumId","enumCode","description").from("Enumeration").where("enumTypeId","PARTY_INDUSTRY").cache(true).queryList();
            Map<String, Object> industryMap = DataUtil.getMapFromGeneric(industry, "enumCode", "description", false);
            
            List<GenericValue> countryList = EntityQuery.use(delegator).select("geoId","geoName").from("Geo").where("geoTypeId","COUNTRY").cache(true).queryList();
            Map<String, Object> countryListMap = DataUtil.getMapFromGeneric(countryList, "geoId", "geoName", false);
            
            List<GenericValue> stateList = EntityQuery.use(delegator).select("geoId","geoName").from("Geo").where(EntityCondition.makeCondition("geoTypeId",EntityOperator.IN,UtilMisc.toList("STATE","PROVINCE"))).cache(true).queryList();
            Map<String, Object> stateListMap = DataUtil.getMapFromGeneric(stateList, "geoId", "geoName", false);
            
			if(UtilValidate.isNotEmpty(resultList)) {
				for(GenericValue gv : resultList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.putAll(org.groupfio.lead.portal.util.DataUtil.convertGenericValueToMap(delegator, gv));
					
					partyId = gv.getString("partyId");
					
					data.put("statusDesc", UtilValidate.isNotEmpty(partyStatusMap) && UtilValidate.isNotEmpty(gv.getString("statusId")) ? partyStatusMap.get(gv.getString("statusId")) : "");
					data.put("uomDesc", UtilValidate.isNotEmpty(uomMap) && UtilValidate.isNotEmpty(gv.getString("preferredCurrencyUomId")) ? uomMap.get(gv.getString("preferredCurrencyUomId")) : "");
					data.put("timeZone", UtilValidate.isNotEmpty(timeZoneMap) && UtilValidate.isNotEmpty(gv.getString("timeZoneDesc")) ? timeZoneMap.get(gv.getString("timeZoneDesc")) : "");
					data.put("industry", UtilValidate.isNotEmpty(industryMap) && UtilValidate.isNotEmpty(gv.getString("industryEnumId")) ? industryMap.get(gv.getString("industryEnumId")) : "");
					data.put("ownership", UtilValidate.isNotEmpty(ownershipMap) && UtilValidate.isNotEmpty(gv.getString("ownershipEnumId")) ? ownershipMap.get(gv.getString("ownershipEnumId")) : "");
					data.put("country", UtilValidate.isNotEmpty(countryListMap) && UtilValidate.isNotEmpty(gv.getString("countryGeoId")) ? countryListMap.get(gv.getString("countryGeoId")) : "");
					data.put("state", UtilValidate.isNotEmpty(stateListMap) && UtilValidate.isNotEmpty(gv.getString("stateProvinceGeoId")) ? stateListMap.get(gv.getString("stateProvinceGeoId")) : "");
					String annualRevenue="";
					if (UtilValidate.isNotEmpty(gv.get("annualRevenue"))) {
						annualRevenue = nf.format(gv.get("annualRevenue"));
					}
					data.put("annualRev", annualRevenue);
					
					data.put("contactNumber", org.fio.homeapps.util.PartyHelper.getContactNumber(delegator, partyId, "PRIMARY_PHONE"));
					data.put("infoString", org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, partyId, "PRIMARY_EMAIL"));
					
					String primaryContactName = "";
					String primaryContactId = "";
					String primaryContactEmail = "";
					String primaryContactPhone = "";
					Map<String, Object> primaryContact = org.groupfio.common.portal.util.DataUtil.getPrimaryContact(delegator, partyId, "LEAD");
					if (UtilValidate.isNotEmpty(primaryContact)) {
						primaryContactId = (String) primaryContact.get("contactId");
						primaryContactName = (String) primaryContact.get("contactName");
						primaryContactEmail = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, primaryContactId, "PRIMARY_EMAIL");
						primaryContactPhone = org.fio.homeapps.util.PartyHelper.getContactNumber(delegator, primaryContactId, "PRIMARY_PHONE");
					}
					data.put("primaryContactName", primaryContactName);
					data.put("primaryContactId", primaryContactId);
					data.put("primaryContactEmail", primaryContactEmail);
					data.put("primaryContactPhone", primaryContactPhone);
					
					data.put("lastUpdatedOn", gv.getString("lastUpdatedTxStamp"));
					
					data.put("domainEntityId", partyId);
					data.put("domainEntityType", DomainEntityType.LEAD);
					data.put("domainEntityTypeDesc", DataHelper.convertToLabel( DomainEntityType.LEAD ));
					data.put("externalLoginKey", externalLoginKey);	
					
					dataList.add(data);
				}
				result.put("highIndex", Integer.valueOf(highIndex));
		        result.put("lowIndex", Integer.valueOf(lowIndex));
			}
			
			result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			result.put("totalRecords", nf.format(resultListSize));
			result.put("recordCount", resultListSize);
			result.put("chunkSize", viewSize);
		} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);

			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}
    
    public static String updateLeadStatus(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String partyId = request.getParameter("partyId");
		String statusId = request.getParameter("statusId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			Map<String, Object> requestContext = FastMap.newInstance();
			
			callCtxt.put("requestContext", requestContext);
			
			callCtxt.put("partyId", partyId);
			callCtxt.put("statusId", statusId);

			callCtxt.put("userLogin", userLogin);

			callResult = dispatcher.runSync("ledportal.updateLeadStatus", callCtxt);
			if (ServiceUtil.isSuccess(callResult)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully updated lead status");
			} else {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(callResult));
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
    
    public static String getLeadDashboardCountList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<GenericValue> resultList = null;
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		String filterBy = (String) context.get("filterBy");
		String filterType = (String) context.get("filterType");
		
		String partyId = (String) context.get("partyId");
		List<Map<String, Object>> dataList = new ArrayList<>();
		long start = System.currentTimeMillis();
		Timestamp systemTime = UtilDateTime.nowTimestamp();
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		try {
			String userLoginId = userLogin.getString("userLoginId");
			String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				
			String loggedUserRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			List<String> statusIds = new ArrayList<>();
			
			if((UtilValidate.isNotEmpty(filterType) || UtilValidate.isNotEmpty(filterBy))  && UtilValidate.isNotEmpty(userLoginId)) {
				SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
				Connection con = (Connection)sqlProcessor.getConnection();
				CallableStatement cstmt = null;

				String barType = "";
				String count = "";
				String _where_condition_ = "";
				String _count_lead_query_ = "SELECT COUNT(DISTINCT P.PARTY_ID), 'openAccount' \r\n" + 
						"FROM PARTY P \r\n" + 
						"INNER JOIN PARTY_GROUP PG ON P.PARTY_ID = PG.PARTY_ID \r\n" + 
						//"LEFT OUTER JOIN PARTY_RELATIONSHIP PR ON P.PARTY_ID = PR.PARTY_ID_FROM \r\n" + 
						"LEFT OUTER JOIN PARTY_SUPPLEMENTAL_DATA PSD ON P.PARTY_ID = PSD.PARTY_ID \r\n";

				_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.ROLE_TYPE_ID ='LEAD'";

				//_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" (P.STATUS_ID <> 'PARTY_DISABLED' OR P.STATUS_ID IS NULL)";

				if (UtilValidate.isNotEmpty(filterType) && "my-lead".equals(filterType)) {
					_count_lead_query_ = _count_lead_query_ + "LEFT OUTER JOIN PARTY_RELATIONSHIP PR1 ON P.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
							+ " PR1.PARTY_ID_TO ='"+userLoginPartyId+"'"
							+ " AND PR1.ROLE_TYPE_ID_TO = 'ACCOUNT_MANAGER'"
							+ " AND PR1.ROLE_TYPE_ID_FROM = 'LEAD'"
							+ " AND PR1.PARTY_RELATIONSHIP_TYPE_ID= 'RESPONSIBLE_FOR'"
							+ " AND ((PR1.THRU_DATE IS NULL OR PR1.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') AND (PR1.FROM_DATE IS NULL OR PR1.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))";

				} else if (UtilValidate.isNotEmpty(filterType) && "my-team-lead".equals(filterType)) {
					//List<EntityCondition> teamCondList = FastList.newInstance();

					List<GenericValue> emplPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("partyId",userLoginPartyId).filterByDate().queryList();
					List<String> teams = UtilValidate.isNotEmpty(emplPositionFulfillments) ? EntityUtil.getFieldListFromEntityList(emplPositionFulfillments, "emplTeamId", true) : new ArrayList<>();
					if(UtilValidate.isEmpty(teams)) teams.add("00000000221");

					List<String> partyIds = new ArrayList<String>() {{add("00000000121");}};

					List<GenericValue> teamPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, teams)).queryList();
					partyIds = EntityUtil.getFieldListFromEntityList(teamPositionFulfillments, "partyId", true);

					/*
					EntityCondition teamLeadCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.getString("partyId")));
					teamCondList.add(teamLeadCondition);
					EntityCondition teamConditons = EntityCondition.makeCondition(teamCondList, EntityOperator.AND);
					List<GenericValue> teamList = delegator.findList("EmplPositionFulfillment", teamConditons, null, null, null, false);

					if(UtilValidate.isNotEmpty(teamList)){
						GenericValue emplPosition=teamList.get(0);
						String isTeamLead=emplPosition.getString("isTeamLead");

						if(UtilValidate.isNotEmpty(isTeamLead) && "Y".equals(isTeamLead)){
							List<EntityCondition> teamMemberCondList = FastList.newInstance();					
							EntityCondition teamMemberCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")));
							teamMemberCondList.add(teamMemberCondition);
							EntityCondition teamMemberConditons = EntityCondition.makeCondition(teamMemberCondList, EntityOperator.AND);
							List<GenericValue> teamMemberList = delegator.findList("EmplPositionFulfillment", teamMemberConditons, null, null, null, false);
							partyIds = EntityUtil.getFieldListFromEntityList(teamMemberList, "partyId", true);
						}
					} */

					_count_lead_query_ = _count_lead_query_ + "LEFT OUTER JOIN PARTY_RELATIONSHIP PR1 ON P.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
							+ " PR1.PARTY_ID_TO IN ("+org.fio.admin.portal.util.DataUtil.toList(partyIds, "")+")"
							+ " AND PR1.ROLE_TYPE_ID_TO = 'ACCOUNT_MANAGER'"
							+ " AND PR1.ROLE_TYPE_ID_FROM = 'LEAD'"
							+ " AND PR1.PARTY_RELATIONSHIP_TYPE_ID= 'RESPONSIBLE_FOR'"
							+ " AND ((PR1.THRU_DATE IS NULL OR PR1.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') AND (PR1.FROM_DATE IS NULL OR PR1.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))";
				} else if (UtilValidate.isNotEmpty(filterType) && "my-bu-lead".equals(filterType)) {

				}
				//Universe count
				String _universe_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.STATUS_ID = 'LEAD_UNIVERSE'";
				String _universe_lead_count_query_ = _count_lead_query_ + " WHERE "+_universe_where_condition_;
				System.out.println("_universe_=====>"+_universe_lead_count_query_);
				rs = sqlProcessor.executeQuery(_universe_lead_count_query_);

				if(rs !=null){
					try{ 
						int i = 0;
						while (rs.next()) {
							Map<String, Object> data = new HashMap<String, Object>();
							count = rs.getString(1);
							barType = rs.getString(2);
							data.put("barId", "universe");
							data.put("count", org.groupfio.common.portal.util.DataHelper.getFormattedNumValue(delegator, ""+count));
							dataList.add(data);
						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(rs !=null)
							rs.close();
					}
				}
				
				//Suspect count
				String _suspect_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.STATUS_ID = 'LEAD_SUSPECT'";
				String _suspect_lead_count_query_ = _count_lead_query_ + " WHERE "+_suspect_where_condition_;
				System.out.println("_suspect_=====>"+_suspect_lead_count_query_);
				rs = sqlProcessor.executeQuery(_suspect_lead_count_query_);

				if(rs !=null){
					try{ 
						int i = 0;
						while (rs.next()) {
							Map<String, Object> data = new HashMap<String, Object>();
							count = rs.getString(1);
							barType = rs.getString(2);
							data.put("barId", "suspect");
							data.put("count", org.groupfio.common.portal.util.DataHelper.getFormattedNumValue(delegator, ""+count));
							dataList.add(data);
						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(rs !=null)
							rs.close();
					}
				}
				
				//Prospect count
				String _prospect_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.STATUS_ID = 'LEAD_PROSPECT'";
				String _prospect_lead_count_query_ = _count_lead_query_ + " WHERE "+_prospect_where_condition_;
				System.out.println("_prospect_=====>"+_prospect_lead_count_query_);
				rs = sqlProcessor.executeQuery(_prospect_lead_count_query_);

				if(rs !=null){
					try{ 
						int i = 0;
						while (rs.next()) {
							Map<String, Object> data = new HashMap<String, Object>();
							count = rs.getString(1);
							barType = rs.getString(2);
							data.put("barId", "prospect");
							data.put("count", org.groupfio.common.portal.util.DataHelper.getFormattedNumValue(delegator, ""+count));
							dataList.add(data);
						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(rs !=null)
							rs.close();
					}
				}
				
				//Target count
				String _target_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.STATUS_ID = 'LEAD_TARGET'";
				String _target_lead_count_query_ = _count_lead_query_ + " WHERE "+_target_where_condition_;
				System.out.println("_target_=====>"+_target_lead_count_query_);
				rs = sqlProcessor.executeQuery(_target_lead_count_query_);

				if(rs !=null){
					try{ 
						int i = 0;
						while (rs.next()) {
							Map<String, Object> data = new HashMap<String, Object>();
							count = rs.getString(1);
							barType = rs.getString(2);
							data.put("barId", "target");
							data.put("count", org.groupfio.common.portal.util.DataHelper.getFormattedNumValue(delegator, ""+count));
							dataList.add(data);
						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(rs !=null)
							rs.close();
					}
				}
				
				//Qualified count
				String _qualified_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.STATUS_ID = 'LEAD_QUALIFIED'";
				String _qualified_lead_count_query_ = _count_lead_query_ + " WHERE "+_qualified_where_condition_;
				System.out.println("_qualified_=====>"+_qualified_lead_count_query_);
				rs = sqlProcessor.executeQuery(_qualified_lead_count_query_);

				if(rs !=null){
					try{ 
						int i = 0;
						while (rs.next()) {
							Map<String, Object> data = new HashMap<String, Object>();
							count = rs.getString(1);
							barType = rs.getString(2);
							data.put("barId", "qualified");
							data.put("count", org.groupfio.common.portal.util.DataHelper.getFormattedNumValue(delegator, ""+count));
							dataList.add(data);
						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(rs !=null)
							rs.close();
					}
				}
			}
			
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}

	public static String leadStatusUpdate(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String partyId = request.getParameter("partyId");
		String statusId = request.getParameter("statusId");		
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> callCtxt = new HashMap<String, Object>();
		Map<String, Object> callResult = new HashMap<String, Object>();
		try {
			callCtxt.put("partyId", partyId);
			callCtxt.put("statusId", statusId);
			callCtxt.put("userLogin", userLogin);
			callResult = dispatcher.runSync("ledportal.updateLeadStatus", callCtxt);

			if (ServiceUtil.isSuccess(callResult)) {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, "Lead Status Updated Successfully");
			}else {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, "Error occurred while updating lead status");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
}
