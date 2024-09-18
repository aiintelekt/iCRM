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

package org.groupfio.account.portal.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
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
import org.fio.homeapps.util.StatusUtil;
import org.groupfio.account.portal.AccountPortalConstants.AccountSearchType;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
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
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.text.StringEscapeUtils;
/**
 * @author Sharif Ul Islam
 * 
 */
public final class AjaxEvents {

	public AjaxEvents() { }

    private static final String MODULE = AjaxEvents.class.getName();

    public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
        return doJSONResponse(response, jsonObject.toString());
    }

    public static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
        return doJSONResponse(response, JSONArray.fromObject(collection).toString());
    }

    public static String doJSONResponse(HttpServletResponse response, @SuppressWarnings("rawtypes") Map map) {
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
    
    public static String searchAccounts(HttpServletRequest request, HttpServletResponse response) {
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		List<GenericValue> resultList = null;
		String cif = request.getParameter("cif");
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String partyId = request.getParameter("partyId");
		String localName = request.getParameter("localName");
		String externalId = request.getParameter("externalId");
		String gstnNo = request.getParameter("gstnNo");
		String externalLoginKey = (String) context.get("externalLoginKey");
		
		String searchType = request.getParameter("searchType");
		
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
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
				accessMatrixMap.put("entityName", "Account");
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
				
				EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT"));
				conditionList.add(roleTypeCondition);
				
				EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);
				conditionList.add(partyStatusCondition);
				
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
				if (UtilValidate.isNotEmpty(externalId)) {
					conditionList.add(EntityCondition.makeCondition("externalId", EntityOperator.LIKE, externalId + "%"));
				}
				if (UtilValidate.isNotEmpty(gstnNo)) {
					conditionList.add(EntityCondition.makeCondition("gstnNo", EntityOperator.LIKE, gstnNo + "%"));
				}

				if (UtilValidate.isNotEmpty(name)) {
					EntityCondition nameCondition = EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + name + "%");
					conditionList.add(nameCondition);
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
				
				if (UtilValidate.isNotEmpty(searchType) && searchType.equals(AccountSearchType.MY_ACTIVE_ACCOUNT)) {
					EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
	                		//EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
	                		EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")),
	                		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
	                		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
	                		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
	                		EntityUtil.getFilterByDateExpr()
	                		), EntityOperator.AND);

					conditionList.add(conditionPR);
				}
				if (UtilValidate.isNotEmpty(searchType) && searchType.equals(AccountSearchType.MY_TEAM_ACCOUNTS)) {
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
							EntityCondition teamMemberConditons = EntityCondition.makeCondition(teamMemberCondList, EntityOperator.AND);
							List<GenericValue> teamMemberList = delegator.findList("EmplPositionFulfillment", teamMemberConditons, null, null, null, false);
							partyIds = EntityUtil.getFieldListFromEntityList(teamMemberList, "partyId", true);
						}
					}
					
					EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
	                		EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyIds),
	                		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
	                		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
	                		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
	                		EntityUtil.getFilterByDateExpr()
	                		), EntityOperator.AND);

					conditionList.add(conditionPR);
					
				}
				EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
	            efo.setOffset(0);
	            efo.setLimit(1000);
	            
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
		        
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            Debug.logInfo("list start: "+UtilDateTime.nowTimestamp(), MODULE);
	            Set<String> fieldsToSelect = new LinkedHashSet<String>();
	            
	            fieldsToSelect.add("partyId");fieldsToSelect.add("statusId");fieldsToSelect.add("preferredCurrencyUomId");fieldsToSelect.add("timeZoneDesc");
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
	            fieldsToSelect.add("gstnNo");
	            
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
				Debug.logInfo("list end: "+UtilDateTime.nowTimestamp(), MODULE);
				if(UtilValidate.isNotEmpty(resultList)) {
					
					Map<String, Object> timeZoneMap = EnumUtil.getEnumList(delegator, resultList, "timeZoneDesc", "TIME_ZONE");
					Map<String, Object> ownershipMap = EnumUtil.getEnumList(delegator, resultList, "ownershipEnumId", "PARTY_OWNERSHIP");
					Map<String, Object> industryMap = EnumUtil.getEnumList(delegator, resultList, "industryEnumId", "PARTY_INDUSTRY");
					Map<String, Object> statusMap = StatusUtil.getStatusList(delegator, resultList, "statusId", "PARTY_STATUS");
					Map<String, Object> partyDataSrcMap = DataUtil.getPartyDataSourceList(delegator, resultList, "partyId",true);
					
					long start1 = System.currentTimeMillis();
					for(GenericValue partySummary : resultList) {
						
						Map<String, Object> data = new HashMap<String, Object>();
						
						partyId = partySummary.getString("partyId"); 
						externalId = partySummary.getString("externalId"); 
						gstnNo = partySummary.getString("gstnNo"); 
	                	
						String groupName = partySummary.getString("groupName");
						String statusId = partySummary.getString("statusId");
						//String statusItemDesc = DataUtil.getStatusDescription(delegator, statusId, "PARTY_STATUS");

						//String dataSourceDesc = DataUtil.getPartyDataSource(delegator, partyId);
						
						String uomDesc = DataUtil.getUomDescription(delegator, partySummary.getString("preferredCurrencyUomId"), "CURRENCY_MEASURE");
						//String timeZoneDesc = EnumUtil.getEnumDescription(delegator, partySummary.getString("timeZoneDesc"), "TIME_ZONE");
						
						//String ownershipDesc = EnumUtil.getEnumDescription(delegator, partySummary.getString("ownershipEnumId"), "PARTY_OWNERSHIP");
						//String industryDesc = EnumUtil.getEnumDescription(delegator, partySummary.getString("industryEnumId"), "PARTY_INDUSTRY");
						String annualRevenue="";
						if (UtilValidate.isNotEmpty(partySummary.get("annualRevenue"))) {
							DecimalFormat myFormatter = new DecimalFormat("###,###.000");
							annualRevenue = myFormatter.format(partySummary.get("annualRevenue"));
						}
						String sicCode = partySummary.getString("sicCode");
						String numberEmployees = partySummary.getString("numberEmployees");
						
					//	String phoneNumber = partySummary.getString("primaryContactNumber");
						Map<String,String> partyContactInfo = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, partyId,UtilMisc.toMap("isRetrivePhone", true),true);
						
						String phoneNumber = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("PrimaryPhone") : "";
						phoneNumber = DataHelper.preparePhoneNumber(delegator, phoneNumber);
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
						
						String relationshipManager = PartyHelper.getCurrentResponsiblePartyName(partyId, "ACCOUNT", delegator);
						
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
						
						data.put("partyId", partyId);
						data.put("externalId", externalId);
						data.put("gstnNo", gstnNo);
						data.put("groupName", groupName);
						data.put("statusDescription", statusMap.get(statusId));
						data.put("dataSourceDesc", partyDataSrcMap.get(partyId));
						data.put("contactNumber", phoneNumber);
						data.put("infoString", infoString);
						data.put("city", city);
						data.put("state", state);
						data.put("country", country);
						data.put("postalCode", postalCode);
						data.put("address1", address1);
						data.put("address2", address2);
						
						data.put("localName", partySummary.getString("groupNameLocal"));
						data.put("industryDescription", UtilValidate.isNotEmpty(partySummary.getString("industryEnumId")) ? industryMap.get(partySummary.getString("industryEnumId")) : "");
						data.put("ownershipDescription", UtilValidate.isNotEmpty(partySummary.getString("ownershipEnumId")) ? ownershipMap.get(partySummary.getString("ownershipEnumId")) : "");
						data.put("annualRevenue", annualRevenue);
						data.put("uomDescription", uomDesc);
						data.put("timeZoneDescription", UtilValidate.isNotEmpty(partySummary.getString("timeZoneDesc")) ? timeZoneMap.get(partySummary.getString("timeZoneDesc")) :"");
						data.put("numberEmployees", numberEmployees);
						data.put("sicCode", sicCode);						
						data.put("relationshipManager", relationshipManager);
						data.put("insideRep","");
						
						data.put("domainEntityId", partyId);
						data.put("domainEntityType", DomainEntityType.ACCOUNT);
						data.put("domainEntityTypeDesc", DataHelper.convertToLabel( DomainEntityType.ACCOUNT ));
						data.put("externalLoginKey", externalLoginKey);	
						
						dataList.add(data);
	                }
					long end1 = System.currentTimeMillis();
	        		Debug.logInfo("timeElapsed for construction --->"+(end1-start1) / 1000f, MODULE);
	        		result.put("highIndex", Integer.valueOf(highIndex));
			        result.put("lowIndex", Integer.valueOf(lowIndex));
	            }
				result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
				result.put("totalRecords", nf.format(resultListSize));
				result.put("recordCount", resultListSize);
				result.put("chunkSize", viewSize);   
				Debug.logInfo("data ready: "+UtilDateTime.nowTimestamp(), MODULE);
			} else {
				
			}
        	

        } catch (Exception e) {
           // e.printStackTrace();
        	Debug.logError(e.getMessage(), MODULE);
        	result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
        }
        long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		result.put("timeTaken", (end-start) / 1000f);
		result.put("list", dataList);
        return doJSONResponse(response, result);
    	
    }
    
    public static String findAccountsOld(HttpServletRequest request, HttpServletResponse response) {
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		List<GenericValue> resultList = null;
		String cif = request.getParameter("cif");
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String partyId = request.getParameter("partyId");
		String externalId = request.getParameter("externalId");
		String gstnNo = request.getParameter("gstnNo");
		String localName = request.getParameter("localName");
		String statusId = request.getParameter("statusId");
		
		String personResponsible = request.getParameter("personResponsible");
		String countryGeoId = request.getParameter("countryGeoId");
		String stateProvinceGeoId = request.getParameter("stateProvinceGeoId");
		String city = request.getParameter("city");
		
		String searchType = request.getParameter("searchType");
		String externalLoginKey = (String) context.get("externalLoginKey");
		
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		long start = System.currentTimeMillis();
		Debug.logInfo("try start: "+UtilDateTime.nowTimestamp(), MODULE);
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
				accessMatrixMap.put("entityName", "Account");
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
				
				EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT"));
				conditionList.add(roleTypeCondition);
				
				if (UtilValidate.isNotEmpty(statusId) && !statusId.equals("ACTIVE")) {
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
				} else {
					// filter active accounts
					EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
							EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);
					conditionList.add(partyStatusCondition);
				}
				
				if (UtilValidate.isNotEmpty(cif)) {
					partyId = DataUtil.getPartyIdentificationPartyId(delegator, cif, "CIF");
				}
				
				if (UtilValidate.isNotEmpty(localName)) {
					conditionList.add(EntityCondition.makeCondition("localName", EntityOperator.LIKE, "%"+localName + "%"));
				}
				
				if (UtilValidate.isNotEmpty(partyId)) {
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				}
				if (UtilValidate.isNotEmpty(externalId)) {
					conditionList.add(EntityCondition.makeCondition("externalId", EntityOperator.LIKE, externalId + "%"));
				}
				if (UtilValidate.isNotEmpty(gstnNo)) {
					conditionList.add(EntityCondition.makeCondition("gstnNo", EntityOperator.LIKE, gstnNo + "%"));
				}
				
				if (UtilValidate.isNotEmpty(name)) {
					EntityCondition nameCondition = EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + name + "%");
					conditionList.add(nameCondition);
				}
				
				if (UtilValidate.isNotEmpty(stateProvinceGeoId) || (UtilValidate.isNotEmpty(city))) {
					conditionList.add(EntityUtil.getFilterByDateExpr("pcmFromDate", "pcmThruDate"));
				}
				if (UtilValidate.isNotEmpty(countryGeoId)) {
					conditionList.add(EntityCondition.makeCondition("countryGeoId", EntityOperator.EQUALS, countryGeoId));
				}
				if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
					conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, stateProvinceGeoId));
				}
				if (UtilValidate.isNotEmpty(city)) {
					conditionList.add(EntityCondition.makeCondition("city", EntityOperator.EQUALS, city));
				}
				
				if (UtilValidate.isNotEmpty(personResponsible)) {
					EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
	                		EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, personResponsible),
	                		//EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
	                		EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, "ACCOUNT_OWNER"),
	                		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
	                		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
	                		EntityUtil.getFilterByDateExpr()
	                		), EntityOperator.AND);

					conditionList.add(conditionPR);
				}
				
				List<EntityCondition> eventExprs = new LinkedList<EntityCondition>();
				if (UtilValidate.isNotEmpty(email) || UtilValidate.isNotEmpty(phone)) {

					if (UtilValidate.isNotEmpty(email)) {
						eventExprs.add(EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, email));
					}

					if (UtilValidate.isNotEmpty(phone)) {
						eventExprs.add(EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, phone));
					}

					conditionList.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
				}
				
				if (UtilValidate.isNotEmpty(searchType) && searchType.equals(AccountSearchType.MY_ACTIVE_ACCOUNT)) {
					EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
	                		//EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
	                		EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")),
	                		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
	                		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
	                		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
	                		EntityUtil.getFilterByDateExpr()
	                		), EntityOperator.AND);

					conditionList.add(conditionPR);
				}
				if (UtilValidate.isNotEmpty(searchType) && searchType.equals(AccountSearchType.MY_TEAM_ACCOUNTS)) {
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
							EntityCondition teamMemberConditons = EntityCondition.makeCondition(teamMemberCondList, EntityOperator.AND);
							List<GenericValue> teamMemberList = delegator.findList("EmplPositionFulfillment", teamMemberConditons, null, null, null, false);
							partyIds = EntityUtil.getFieldListFromEntityList(teamMemberList, "partyId", true);
						}
					}
					
					EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
	                		EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyIds),
	                		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
	                		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
	                		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
	                		EntityUtil.getFilterByDateExpr()
	                		), EntityOperator.AND);

					conditionList.add(conditionPR);
				}
				
				EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
	            efo.setOffset(0);
	            efo.setLimit(1000);
	            
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
								
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				Debug.logInfo("findAccount mainConditons: "+mainConditons, MODULE);
	            
	            Set<String> fieldsToSelect = new LinkedHashSet<String>();
	            
	            fieldsToSelect.add("partyId");fieldsToSelect.add("statusId");fieldsToSelect.add("preferredCurrencyUomId");fieldsToSelect.add("timeZoneDesc");
	            fieldsToSelect.add("groupName");
	            fieldsToSelect.add("localName");
	            fieldsToSelect.add("ownershipEnumId");
	            fieldsToSelect.add("industryEnumId");
	            fieldsToSelect.add("annualRevenue");
	            fieldsToSelect.add("sicCode");
	            fieldsToSelect.add("numberEmployees");
	            fieldsToSelect.add("address1");
	            fieldsToSelect.add("address2");
	            fieldsToSelect.add("city");
	            fieldsToSelect.add("postalCode");
	            fieldsToSelect.add("countryGeoId");
	            fieldsToSelect.add("stateProvinceGeoId");
	            fieldsToSelect.add("contactNumber");
	            fieldsToSelect.add("infoString");
	            fieldsToSelect.add("ownerId");
	            fieldsToSelect.add("emplTeamId");
	            fieldsToSelect.add("externalId");
	            fieldsToSelect.add("gstnNo");
	            fieldsToSelect.add("createdTxStamp");
	            
	            DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
	            dynamicViewEntity.addMemberEntity("P", "Party");
	            dynamicViewEntity.addAlias("P", "partyId", "partyId", null, false, true, null);
	            dynamicViewEntity.addAlias("P", "statusId");
	            dynamicViewEntity.addAlias("P", "roleTypeId");
	            dynamicViewEntity.addAlias("P", "preferredCurrencyUomId");
	            dynamicViewEntity.addAlias("P", "timeZoneDesc");
	            dynamicViewEntity.addAlias("P", "emplTeamId");
	            dynamicViewEntity.addAlias("P", "ownerId");
	            dynamicViewEntity.addAlias("P", "externalId");
	            dynamicViewEntity.addAlias("P", "createdStamp");
	            dynamicViewEntity.addAlias("P", "createdTxStamp");
	            
	            dynamicViewEntity.addMemberEntity("PG", "PartyGroup");
	            dynamicViewEntity.addAlias("PG", "groupName");
	            dynamicViewEntity.addAlias("PG", "gstnNo");
	            dynamicViewEntity.addAlias("PG", "localName", "groupNameLocal", null, false, true, null);
	            dynamicViewEntity.addViewLink("P", "PG", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
	            
	            dynamicViewEntity.addMemberEntity("PR", "PartyRelationship");
	            dynamicViewEntity.addAlias("PR", "partyIdFrom");
	            dynamicViewEntity.addAlias("PR", "partyIdTo");
	            dynamicViewEntity.addAlias("PR", "roleTypeIdFrom");
	            dynamicViewEntity.addAlias("PR", "roleTypeIdTo");
	            dynamicViewEntity.addAlias("PR", "securityGroupId");
	            dynamicViewEntity.addAlias("PR", "fromDate");
	            dynamicViewEntity.addAlias("PR", "thruDate");
	            dynamicViewEntity.addAlias("PR", "partyRelationshipTypeId");
	            dynamicViewEntity.addViewLink("P", "PR", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId", "partyIdFrom"));
	            
	            dynamicViewEntity.addMemberEntity("PSD", "PartySupplementalData");
	            dynamicViewEntity.addAlias("PSD", "uploadedByUserLoginId");
	            dynamicViewEntity.addAlias("PSD", "departmentName");
	            dynamicViewEntity.addAlias("PSD", "ownershipEnumId");
	            dynamicViewEntity.addAlias("PSD", "industryEnumId");
	            dynamicViewEntity.addAlias("PSD", "annualRevenue");
	            dynamicViewEntity.addAlias("PSD", "sicCode");
	            dynamicViewEntity.addAlias("PSD", "numberEmployees");
	            dynamicViewEntity.addAlias("PSD", "supplementalPartyTypeId");
	            dynamicViewEntity.addViewLink("P", "PSD", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
	            
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
	            
	            dynamicViewEntity.addMemberEntity("TN", "TelecomNumber");
	            dynamicViewEntity.addAlias("TN", "countryCode");
	            dynamicViewEntity.addAlias("TN", "areaCode");
	            dynamicViewEntity.addAlias("TN", "contactNumber");
	            dynamicViewEntity.addAlias("TN", "askForName");
	            dynamicViewEntity.addViewLink("PSD", "TN", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryTelecomNumberId", "contactMechId"));
	            
	            dynamicViewEntity.addMemberEntity("CM", "ContactMech");
	            dynamicViewEntity.addAlias("CM", "infoString");
	            dynamicViewEntity.addViewLink("PSD", "CM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("primaryEmailId", "contactMechId"));
	            
	            dynamicViewEntity.addMemberEntity("PCM", "PartyContactMech");
	            dynamicViewEntity.addAlias("PCM", "pcmFromDate", "fromDate", null, false, false, null);
	            dynamicViewEntity.addAlias("PCM", "pcmThruDate", "thruDate", null, false, false, null);
	            dynamicViewEntity.addViewLink("CM", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("contactMechId"));
	            
	            dynamicViewEntity.addMemberEntity("PCMP", "PartyContactMechPurpose");
	            dynamicViewEntity.addAlias("PCMP", "pcmpFromDate", "fromDate", null, false, false, null);
	            dynamicViewEntity.addAlias("PCMP", "pcmpThruDate", "thruDate", null, false, false, null);
	            dynamicViewEntity.addViewLink("CM", "PCMP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("contactMechId"));
	            
	            int highIndex = 0;
	            int lowIndex = 0;
	            int resultListSize = 0;
	         // get the indexes for the partial list
            	lowIndex = viewIndex * viewSize + 1;
                highIndex = (viewIndex + 1) * viewSize;
                Debug.logInfo("query start: "+UtilDateTime.nowTimestamp(), MODULE);
                // set distinct on so we only get one row per 
                // using list iterator
                EntityListIterator pli = EntityQuery.use(delegator)
                		.select(fieldsToSelect)
                        .from(dynamicViewEntity)
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
                Debug.logInfo("query end: "+UtilDateTime.nowTimestamp(), MODULE);
				//List<GenericValue> partySummaryList = delegator.findList("AccountSummaryView", mainConditons, null, UtilMisc.toList("-partyId"), efo, false);
				Debug.logInfo("list start: "+UtilDateTime.nowTimestamp(), MODULE);
				if(UtilValidate.isNotEmpty(resultList)) {
					Debug.logInfo("data con start: "+UtilDateTime.nowTimestamp(), MODULE);
					Map<String, Object> timeZoneMap = EnumUtil.getEnumList(delegator, resultList, "timeZoneDesc", "TIME_ZONE");
					Map<String, Object> ownershipMap = EnumUtil.getEnumList(delegator, resultList, "ownershipEnumId", "PARTY_OWNERSHIP");
					Map<String, Object> industryMap = EnumUtil.getEnumList(delegator, resultList, "industryEnumId", "PARTY_INDUSTRY");
					Map<String, Object> statusMap = StatusUtil.getStatusList(delegator, resultList, "statusId", "PARTY_STATUS");
					//Debug.logInfo("data con1 end: "+UtilDateTime.nowTimestamp(), MODULE);
					
					//Debug.logInfo("data con2 start: "+UtilDateTime.nowTimestamp(), MODULE);
					Map<String, Object> partyDataSrcMap = DataUtil.getPartyDataSourceList(delegator, resultList, "partyId",true);
					//Debug.logInfo("data con2 end: "+UtilDateTime.nowTimestamp(), MODULE);
					//Debug.logInfo("data con3 start: "+UtilDateTime.nowTimestamp(), MODULE);
					Map<String, Object> uomMap = org.fio.admin.portal.util.DataUtil.getUomDescriptionList(delegator, "CURRENCY_MEASURE");
					//Debug.logInfo("data con3 end: "+UtilDateTime.nowTimestamp(), MODULE);
					
					//Debug.logInfo("data con4 start: "+UtilDateTime.nowTimestamp(), MODULE);
					Map<String, Object> stateMap = org.fio.admin.portal.util.DataUtil.getGeoNameList(delegator, "STATE/PROVINCE");
					Map<String, Object> countryMap = org.fio.admin.portal.util.DataUtil.getGeoNameList(delegator, "COUNTRY");
					//Debug.logInfo("data con4 end: "+UtilDateTime.nowTimestamp(), MODULE);
					
					//Debug.logInfo("data con5 start: "+UtilDateTime.nowTimestamp(), MODULE);
					Map<String, Object> responsiblePartyMap = PartyHelper.getResponsibleParty(delegator, resultList, "partyId", "ACCOUNT");
					//Debug.logInfo("data con5 end: "+UtilDateTime.nowTimestamp(), MODULE);
					//Debug.logInfo("data con6 start: "+UtilDateTime.nowTimestamp(), MODULE);
					Map<String, Map<String, Object>> primaryContactDetails = new HashMap<String, Map<String,Object>>(); // DataUtil.getPrimaryContactDetails(delegator, resultList, "partyId", "ACCOUNT");
					Debug.logInfo("data con end: "+UtilDateTime.nowTimestamp(), MODULE);
					long start1 = System.currentTimeMillis();
					for(GenericValue partySummary : resultList) {
						partyId = partySummary.getString("partyId"); 
						
						Map<String, Object> data = new HashMap<String, Object>();
						data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(partySummary));
	                	
						statusId = partySummary.getString("statusId");
						
						String annualRevenue="";
						if (UtilValidate.isNotEmpty(partySummary.get("annualRevenue"))) {
							DecimalFormat myFormatter = new DecimalFormat("###,###.000");
							annualRevenue = myFormatter.format(partySummary.get("annualRevenue"));
						}
						
						data.put("partyId", partyId);
						data.put("externalId", partySummary.getString("externalId"));
						data.put("gstnNo", partySummary.getString("gstnNo"));
						
						data.put("statusDescription", UtilValidate.isNotEmpty(statusMap) && UtilValidate.isNotEmpty(statusId) ? statusMap.get(statusId) : "");
						data.put("dataSourceDesc", UtilValidate.isNotEmpty(partyDataSrcMap) && UtilValidate.isNotEmpty(partyId) ? partyDataSrcMap.get(partyId) : "");
						
						data.put("state", UtilValidate.isNotEmpty(stateMap) && UtilValidate.isNotEmpty(partySummary.getString("stateProvinceGeoId")) ? stateMap.get(partySummary.getString("stateProvinceGeoId")) : "");
						data.put("country", UtilValidate.isNotEmpty(countryMap) && UtilValidate.isNotEmpty(partySummary.getString("countryGeoId")) ? countryMap.get(partySummary.getString("countryGeoId")) : "");
						
						data.put("industryDescription", UtilValidate.isNotEmpty(partySummary.getString("industryEnumId")) ? industryMap.get(partySummary.getString("industryEnumId")) : "");
						data.put("ownershipDescription", UtilValidate.isNotEmpty(partySummary.getString("ownershipEnumId")) ? ownershipMap.get(partySummary.getString("ownershipEnumId")) : "");
						data.put("annualRevenue", annualRevenue);
						data.put("uomDescription", UtilValidate.isNotEmpty(uomMap) && UtilValidate.isNotEmpty(partySummary.getString("preferredCurrencyUomId")) ? uomMap.get(partySummary.getString("preferredCurrencyUomId")) : "");
						
						data.put("timeZoneDescription", UtilValidate.isNotEmpty(partySummary.getString("timeZoneDesc")) ? timeZoneMap.get(partySummary.getString("timeZoneDesc")) :"");
						data.put("insideRep","");
						
						data.put("contactNumber", partySummary.getString("contactNumber"));
						data.put("infoString", partySummary.getString("infoString"));
						
						//Map<String, Object> primaryContactMap = UtilValidate.isNotEmpty(primaryContactDetails) ? primaryContactDetails.get(partyId) : new HashMap<String, Object>();
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
						
						data.put("relationshipManager", UtilValidate.isNotEmpty(responsiblePartyMap) ? responsiblePartyMap.get(partyId) : "");
						
						data.put("domainEntityId", partyId);
						data.put("domainEntityType", DomainEntityType.ACCOUNT);
						data.put("domainEntityTypeDesc", DataHelper.convertToLabel( DomainEntityType.ACCOUNT ));
						data.put("externalLoginKey", externalLoginKey);	
						
						dataList.add(data);
	                }
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
				Debug.logInfo("data ready: "+UtilDateTime.nowTimestamp(), MODULE);
			} else {
				
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

	public static String testAccountCreate(HttpServletRequest request, HttpServletResponse response) {
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		int accountCount = UtilValidate.isNotEmpty(request.getParameter("accountCount")) ? Integer.parseInt(request.getParameter("accountCount")) : 100;
		try {
			long phNo = 10000000l;
			for(int i=0;i<=accountCount;i++) {
				Map<String, Object> ctx  = new HashMap<String, Object>();
				//ctx.put("loggedInUserId", "20001799");
				//ctx.put("userName", "csr  1");
				//ctx.put("userLoginRole", "CUST_SERVICE_REP");
				ctx.put("accountName", "Test Account_"+i);
				ctx.put("dataSourceId", "10151");
				ctx.put("industryEnumId", "IND_AEROSPACE");
				ctx.put("sicCode","");
				ctx.put("ownershipEnumId", "OWN_CCORP_N");
				ctx.put("tickerSymbol","");
				ctx.put("numberEmployees", 3l);
				ctx.put("annualRevenue", new BigDecimal(25));
				ctx.put("currencyUomId", "USD");
				ctx.put("isExempt","");
				ctx.put("partyTaxId","");
				ctx.put("personResponsible", "20001799");
				ctx.put("description", "Test account for data load "+i);
				ctx.put("primaryEmail", "mahe.t"+i+"@groupfio.com");
				ctx.put("primaryPhoneNumber",  phNo+ 1+"");
				ctx.put("groupNameLocal", "Testing "+i);
				ctx.put("primaryWebUrl",""); 
				ctx.put("generalAttnName", "Attn Name "+ i);
				ctx.put("generalAddress1", "750 EDGEWATER AVE");
				ctx.put("generalAddress2","");
				
				ctx.put("generalCountryGeoId", "USA");
				ctx.put("generalStateProvinceGeoId", "NY");
				ctx.put("generalPostalCode","18505");
				ctx.put("generalPostalCodeExt","");
				ctx.put("generalCity",""); 
				ctx.put("countyGeoId","");
				ctx.put("timeZoneDesc","1032");
				ctx.put("isBusiness","");
				ctx.put("isVacant","");
				ctx.put("isUspsAddrVerified", "N");
				ctx.put("userLogin", userLogin);
				
				TransactionUtil.begin();
				Map<String, Object> callResult = dispatcher.runSync("actportal.createAccount", ctx);
				TransactionUtil.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "success";
		
	}
	
	
	@SuppressWarnings("serial")
	public static String findAccounts(HttpServletRequest request, HttpServletResponse response) {
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		String cif = request.getParameter("cif");
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String partyId = request.getParameter("partyId");
		String externalId = request.getParameter("externalId");
		String gstnNo = request.getParameter("gstnNo");
		String localName = request.getParameter("localName");
		String statusId = StringEscapeUtils.escapeXSI(request.getParameter("statusId"));
		
		String personResponsible = request.getParameter("personResponsible");
		String countryGeoId = request.getParameter("countryGeoId");
		String stateProvinceGeoId = request.getParameter("stateProvinceGeoId");
		String city = request.getParameter("city");
		
		//String searchType = request.getParameter("searchType");
		String externalLoginKey = (String) context.get("externalLoginKey");
		String isExportAction = (String) request.getAttribute("isExportAction");
		
		String filterBy = (String) context.get("filterBy");
		String filterType = (String) context.get("filterType");
		
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		long start = System.currentTimeMillis();
		Debug.logInfo("try start: "+UtilDateTime.nowTimestamp(), MODULE);
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
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
				accessMatrixMap.put("entityName", "Account");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
			}
			// to skip security matrix for the dashboard elements
			if(UtilValidate.isNotEmpty(filterType)) {
				accessLevel = "Y";
				accessMatrixRes = new HashMap<String, Object>();
			}
				
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				
				boolean isPostalSearch = false;
				if (UtilValidate.isNotEmpty(countryGeoId) || UtilValidate.isNotEmpty(stateProvinceGeoId) || (UtilValidate.isNotEmpty(city))) {
					isPostalSearch = true;
				}
				
				List<EntityCondition> conditionList = FastList.newInstance();
				String userLoginPartyId = userLogin.getString("partyId");
				List<String> selectFields = new LinkedList<String>();
				String _where_condition_ = "";
				String select_query = "SELECT DISTINCT P.PARTY_ID, P.STATUS_ID, P.PREFERRED_CURRENCY_UOM_ID, P.TIME_ZONE_DESC, \r\n" + 
						"PG.GROUP_NAME, PG.GROUP_NAME_LOCAL, PSD.OWNERSHIP_ENUM_ID, PSD.INDUSTRY_ENUM_ID, \r\n" + 
						"PSD.ANNUAL_REVENUE, PSD.SIC_CODE, PSD.NUMBER_EMPLOYEES, \r\n" +
						"PSD.PRIMARY_POSTAL_ADDRESS_ID, PSD.PRIMARY_EMAIL_ID, PSD.PRIMARY_TELECOM_NUMBER_ID, \r\n" +
						"P.OWNER_ID, P.EMPL_TEAM_ID, P.EXTERNAL_ID, PG.GSTN_NO, P.CREATED_TX_STAMP \r\n" ;
				if(isPostalSearch) {
					select_query = select_query + " ,PA.CITY, PA.ADDRESS1 , PA.ADDRESS2 ,PA.COUNTRY_GEO_ID, PA.STATE_PROVINCE_GEO_ID , PA.POSTAL_CODE " ;
				}
				String _sql_query_ = select_query + " FROM PARTY P \r\n" + 
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
				
				_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.ROLE_TYPE_ID ='ACCOUNT'";
				
				if (UtilValidate.isNotEmpty(statusId) && !statusId.equals("ACTIVE")) {
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.STATUS_ID =? ";
					values.add(statusId);
				} else {
					// filter active accounts
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" (P.STATUS_ID <> 'PARTY_DISABLED' OR P.STATUS_ID IS NULL)";
				}
				
				if (UtilValidate.isNotEmpty(cif)) {
					partyId = DataUtil.getPartyIdentificationPartyId(delegator, cif, "CIF");
				}
				
				if (UtilValidate.isNotEmpty(localName)) {
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PG.GROUP_NAME_LOCAL LIKE ? ";
					values.add(localName+"%");
				}
				
				if (UtilValidate.isNotEmpty(partyId)) {
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE, partyId));
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.PARTY_ID LIKE ?";
					values.add(partyId+"%");
				}
				if (UtilValidate.isNotEmpty(externalId)) {
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.EXTERNAL_ID LIKE ? ";
					values.add(externalId+"%");
				}
				if (UtilValidate.isNotEmpty(gstnNo)) {
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PG.GSTN_NO LIKE ? ";
					values.add(gstnNo+"%");
				}
				
				if (UtilValidate.isNotEmpty(name)) {
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PG.GROUP_NAME LIKE ? ";
					values.add(name+"%");
				}
				
				if (isPostalSearch) {
					_sql_query_ = _sql_query_ + "LEFT OUTER JOIN POSTAL_ADDRESS PA ON PSD.PRIMARY_POSTAL_ADDRESS_ID = PA.CONTACT_MECH_ID \r\n";
					_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN POSTAL_ADDRESS PA ON PSD.PRIMARY_POSTAL_ADDRESS_ID = PA.CONTACT_MECH_ID \r\n";
					if (UtilValidate.isNotEmpty(countryGeoId)) {
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PA.COUNTRY_GEO_ID =? ";
						values.add(countryGeoId);
					}
					if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PA.STATE_PROVINCE_GEO_ID =? ";
						values.add(stateProvinceGeoId);
					}
					if (UtilValidate.isNotEmpty(city)) {
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" PA.CITY =? ";
						values.add(city);
					}
				}
				
				
				if (UtilValidate.isNotEmpty(personResponsible)) {
					_sql_query_ = _sql_query_ + "LEFT OUTER JOIN PARTY_RELATIONSHIP PR2 ON P.PARTY_ID = PR2.PARTY_ID_FROM \r\n";
					_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN PARTY_RELATIONSHIP PR2 ON P.PARTY_ID = PR2.PARTY_ID_FROM \r\n";
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
							+ " PR2.PARTY_ID_TO =? "
							+ " AND PR2.SECURITY_GROUP_ID = 'ACCOUNT_OWNER'"
							+ " AND PR2.ROLE_TYPE_ID_FROM = 'ACCOUNT'"
							+ " AND PR2.PARTY_RELATIONSHIP_TYPE_ID= 'RESPONSIBLE_FOR'"
							+ " AND ((PR2.THRU_DATE IS NULL OR PR2.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') AND (PR2.FROM_DATE IS NULL OR PR2.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))";
					values.add(personResponsible);
				}
				
				List<EntityCondition> eventExprs = new LinkedList<EntityCondition>();
				if (UtilValidate.isNotEmpty(email) || UtilValidate.isNotEmpty(phone)) {

					if (UtilValidate.isNotEmpty(email)) {
						_sql_query_ = _sql_query_ + "LEFT OUTER JOIN CONTACT_MECH CM ON PSD.PRIMARY_EMAIL_ID = CM.CONTACT_MECH_ID \r\n";
						_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN CONTACT_MECH CM ON PSD.PRIMARY_EMAIL_ID = CM.CONTACT_MECH_ID \r\n";
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
								+ " CM.INFO_STRING LIKE ? ";
						values.add(email+"%");
					}

					if (UtilValidate.isNotEmpty(phone)) {
						_sql_query_ = _sql_query_ + "LEFT OUTER JOIN TELECOM_NUMBER TN ON PSD.PRIMARY_TELECOM_NUMBER_ID = TN.CONTACT_MECH_ID \r\n";
						_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN TELECOM_NUMBER TN ON PSD.PRIMARY_TELECOM_NUMBER_ID = TN.CONTACT_MECH_ID \r\n";
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
								+ " TN.CONTACT_NUMBER LIKE ? ";
						values.add(phone+"%");
					}
				}
				
				if (UtilValidate.isNotEmpty(filterType) && "my-account".equals(filterType)) {
					
					_sql_query_ = _sql_query_ + "LEFT OUTER JOIN PARTY_RELATIONSHIP PR1 ON P.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
					_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN PARTY_RELATIONSHIP PR1 ON P.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
					
					if (filterBy.equalsIgnoreCase("account-open-oppo")) {
						_sql_query_ = _sql_query_ + "LEFT OUTER JOIN SALES_OPPORTUNITY SO ON SO.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
						_sql_query_ = _sql_query_ + "LEFT OUTER JOIN SALES_OPPORTUNITY_ROLE SOR ON SOR.SALES_OPPORTUNITY_ID = SO.SALES_OPPORTUNITY_ID \r\n";
						
						_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN SALES_OPPORTUNITY SO ON SO.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
						_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN SALES_OPPORTUNITY_ROLE SOR ON SOR.SALES_OPPORTUNITY_ID = SO.SALES_OPPORTUNITY_ID \r\n";
						
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
								+ " SO.OPPORTUNITY_STATUS_ID = 'OPPO_OPEN' AND SOR.ROLE_TYPE_ID ='ACCOUNT' ";
					}else if (filterBy.equalsIgnoreCase("account-open-act")) {
						_sql_query_ = _sql_query_ + "LEFT OUTER JOIN WORK_EFFORT we ON we.OWNER_PARTY_ID = PR1.PARTY_ID_FROM \r\n";
						_sql_query_ = _sql_query_ + "LEFT OUTER JOIN WORK_EFFORT_PARTY_ASSIGNMENT wea ON wea.WORK_EFFORT_ID = we.WORK_EFFORT_ID \r\n";
						
						_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN WORK_EFFORT we ON we.OWNER_PARTY_ID = PR1.PARTY_ID_FROM \r\n";
						_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN WORK_EFFORT_PARTY_ASSIGNMENT wea ON wea.WORK_EFFORT_ID = we.WORK_EFFORT_ID \r\n";
						
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
								+ " we.CURRENT_STATUS_ID = 'IA_OPEN' AND wea.ROLE_TYPE_ID ='ACCOUNT' ";
					}
					
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
							+ " PR1.PARTY_ID_TO =? "
							+ " AND PR1.ROLE_TYPE_ID_TO = 'ACCOUNT_MANAGER'"
							+ " AND PR1.ROLE_TYPE_ID_FROM = 'ACCOUNT'"
							+ " AND PR1.SECURITY_GROUP_ID = 'ACCOUNT_OWNER'"
							+ " AND PR1.PARTY_RELATIONSHIP_TYPE_ID= 'RESPONSIBLE_FOR'"
							+ " AND ((PR1.THRU_DATE IS NULL OR PR1.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') AND (PR1.FROM_DATE IS NULL OR PR1.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))";
						values.add(userLoginPartyId);
				
				} else if (UtilValidate.isNotEmpty(filterType) && "my-team-account".equals(filterType)) {
					//List<EntityCondition> teamCondList = FastList.newInstance();
					
					List<GenericValue> emplPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("partyId",userLoginPartyId).filterByDate().queryList();
					List<String> teams = UtilValidate.isNotEmpty(emplPositionFulfillments) ? EntityUtil.getFieldListFromEntityList(emplPositionFulfillments, "emplTeamId", true) : new ArrayList<>();
					if(UtilValidate.isEmpty(teams)) teams.add("00000000221");
					
					List<String> partyIds = new ArrayList<String>() {{add("00000000121");}};
					
					List<GenericValue> teamPositionFulfillments = EntityQuery.use(delegator).from("EmplPositionFulfillment").where(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, teams)).queryList();
					partyIds = EntityUtil.getFieldListFromEntityList(teamPositionFulfillments, "partyId", true);
					
					_sql_query_ = _sql_query_ + "LEFT OUTER JOIN PARTY_RELATIONSHIP PR1 ON P.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
					_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN PARTY_RELATIONSHIP PR1 ON P.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
					
					if (filterBy.equalsIgnoreCase("account-open-oppo")) {
						_sql_query_ = _sql_query_ + "LEFT OUTER JOIN SALES_OPPORTUNITY SO ON SO.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
						_sql_query_ = _sql_query_ + "LEFT OUTER JOIN SALES_OPPORTUNITY_ROLE SOR ON SOR.SALES_OPPORTUNITY_ID = SO.SALES_OPPORTUNITY_ID \r\n";
						
						_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN SALES_OPPORTUNITY SO ON SO.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
						_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN SALES_OPPORTUNITY_ROLE SOR ON SOR.SALES_OPPORTUNITY_ID = SO.SALES_OPPORTUNITY_ID \r\n";
						
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
								+ " SO.OPPORTUNITY_STATUS_ID = 'OPPO_OPEN' AND SOR.ROLE_TYPE_ID ='ACCOUNT' ";
					}else if (filterBy.equalsIgnoreCase("account-open-act")) {
						_sql_query_ = _sql_query_ + "LEFT OUTER JOIN WORK_EFFORT we ON we.OWNER_PARTY_ID = PR1.PARTY_ID_FROM \r\n";
						_sql_query_ = _sql_query_ + "LEFT OUTER JOIN WORK_EFFORT_PARTY_ASSIGNMENT wea ON wea.WORK_EFFORT_ID = we.WORK_EFFORT_ID \r\n";
						
						_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN WORK_EFFORT we ON we.OWNER_PARTY_ID = PR1.PARTY_ID_FROM \r\n";
						_count_sql_query_ = _count_sql_query_ + "LEFT OUTER JOIN WORK_EFFORT_PARTY_ASSIGNMENT wea ON wea.WORK_EFFORT_ID = we.WORK_EFFORT_ID \r\n";
						
						_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
								+ " we.CURRENT_STATUS_ID = 'IA_OPEN' AND wea.ROLE_TYPE_ID ='ACCOUNT' ";
					}
					
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
							+ " PR1.PARTY_ID_TO IN ("+org.fio.admin.portal.util.DataUtil.toList(partyIds, "")+")"
							+ " AND PR1.ROLE_TYPE_ID_TO = 'ACCOUNT_MANAGER'"
							+ " AND PR1.ROLE_TYPE_ID_FROM = 'ACCOUNT'"
							+ " AND PR1.SECURITY_GROUP_ID = 'ACCOUNT_OWNER'"
							+ " AND PR1.PARTY_RELATIONSHIP_TYPE_ID= 'RESPONSIBLE_FOR'"
							+ " AND ((PR1.THRU_DATE IS NULL OR PR1.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') AND (PR1.FROM_DATE IS NULL OR PR1.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))";
				} else if (UtilValidate.isNotEmpty(filterType) && "my-bu-account".equals(filterType)) {
					
				}
				
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
								
				//EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				Debug.logInfo("findAccount mainConditons: "+_where_condition_, MODULE);
				
	            int highIndex = 0;
	            int lowIndex = 0;
	            int resultListSize = 0;
	         // get the indexes for the partial list
            	lowIndex = viewIndex * viewSize;
            	//lowIndex = viewIndex * viewSize + 1;
                highIndex = (viewIndex + 1) * viewSize;
                Debug.logInfo("query start: "+UtilDateTime.nowTimestamp(), MODULE);
                // set distinct on so we only get one row per 
                SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
                
                ResultSet rs = null;
                _count_sql_query_ = _count_sql_query_ + " WHERE "+_where_condition_;
                // get the total count 
                rs = QueryUtil.getResultSet(_count_sql_query_, values, delegator);
                if (rs != null) {
					while (rs.next()) {
						resultListSize = (int) rs.getLong("totalRecord");
					}
                }
                
                String _final_sql_script = "SELECT * FROM ("+_sql_query_+" WHERE "+_where_condition_;
                if (UtilValidate.isEmpty(isExportAction) || isExportAction.equals("N")) {
                    _final_sql_script += " LIMIT ?, ?";
                    values.add(lowIndex);
                    values.add(viewSize);
                }
                _final_sql_script += ") temp ";
                _final_sql_script += " ORDER BY CREATED_TX_STAMP DESC"
                		//+ " ORDER BY PARTY_ID DESC"
                		;
                
                Debug.log("_count_sql_query_ ---->"+_count_sql_query_, MODULE);
                
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
							if("partyId".equals(fieldName)) {
								partyIds.add(fieldValue);
							}
							data.put(fieldName, fieldValue);
						}
						resultList.add(data);
					}
				}
                
                Debug.logInfo("query end: "+UtilDateTime.nowTimestamp(), MODULE);
				//List<GenericValue> partySummaryList = delegator.findList("AccountSummaryView", mainConditons, null, UtilMisc.toList("-partyId"), efo, false);
				Debug.logInfo("list start: "+UtilDateTime.nowTimestamp(), MODULE);
				if(UtilValidate.isNotEmpty(resultList)) {
					long start1 = System.currentTimeMillis();
					
					Debug.logInfo("data con start: "+UtilDateTime.nowTimestamp(), MODULE);
					
					List<String> enumIds = resultList.stream().map(x->(String) x.get("industryEnumId")).collect(Collectors.toList());
					Map<String, Object> industryMap = EnumUtil.getEnumList(delegator, enumIds, "PARTY_INDUSTRY");
					
					enumIds = resultList.stream().map(x->(String) x.get("ownershipEnumId")).collect(Collectors.toList());
					Map<String, Object> ownershipMap = EnumUtil.getEnumList(delegator, enumIds, "PARTY_OWNERSHIP");
					
					enumIds = resultList.stream().map(x->(String) x.get("timeZoneDesc")).collect(Collectors.toList());
					Map<String, Object> timeZoneMap = EnumUtil.getEnumList(delegator, enumIds, "TIME_ZONE");
					
					List<String> statusIds = resultList.stream().map(x->(String) x.get("statusId")).collect(Collectors.toList());
					Map<String, Object> statusMap = StatusUtil.getStatusList(delegator, statusIds, "PARTY_STATUS");
					
					Map<String, Object> partyDataSrcMap = DataUtil.getPartyDataSourceByPartyId(delegator, partyIds,true, true);
					
					Map<String, Object> uomMap = org.fio.admin.portal.util.DataUtil.getUomDescriptionList(delegator, "CURRENCY_MEASURE");
					Map<String, Object> stateMap = org.fio.admin.portal.util.DataUtil.getGeoNameList(delegator, "STATE/PROVINCE");
					Map<String, Object> countryMap = org.fio.admin.portal.util.DataUtil.getGeoNameList(delegator, "COUNTRY");
					
					Map<String, Object> responsiblePartyMap = PartyHelper.getResponsiblePartyByPartyIds(delegator, partyIds, "ACCOUNT");
					
					Map<String, Object> emailAddressList = new LinkedHashMap<>();
					if (UtilValidate.isEmpty(email)) {
						List<String> contactMechIds = resultList.stream().map(x->(String) x.get("primaryEmailId")).collect(Collectors.toList());
						emailAddressList = CommonDataHelper.getEmailAddressList(delegator, contactMechIds);
					}
					
					Map<String, Object> contactNumberList = new LinkedHashMap<>();
					if (UtilValidate.isEmpty(phone)) {
						List<String> contactMechIds = resultList.stream().map(x->(String) x.get("primaryTelecomNumberId")).collect(Collectors.toList());
						contactNumberList = CommonDataHelper.getContactNumberList(delegator, contactMechIds);
					}
					
					Map<String, Object> postalAddressList = new LinkedHashMap<>();
					if (!isPostalSearch) {
						List<String> contactMechIds = resultList.stream().map(x->(String) x.get("primaryPostalAddressId")).collect(Collectors.toList());
						postalAddressList = CommonDataHelper.getPostalAddressList(delegator, contactMechIds);
					}
					
					Debug.logInfo("data con end: "+UtilDateTime.nowTimestamp(), MODULE);
					
					for(Map<String, Object> partySummary : resultList) {
						partyId = (String) partySummary.get("partyId"); 
						
						Map<String, Object> data = new HashMap<String, Object>();
						data.putAll(partySummary);
	                	
						statusId = (String) partySummary.get("statusId");
						
						String annualRevenue="";
						if (UtilValidate.isNotEmpty(partySummary.get("annualRevenue"))) {
							double anuRev = Double.parseDouble((String) partySummary.get("annualRevenue"));
							DecimalFormat myFormatter = new DecimalFormat("###,###.000");
							annualRevenue = myFormatter.format(anuRev);
						}
							EntityCondition partyRelationshipGvCondition = EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId),
									EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_DEFAULT"),
									EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
									EntityUtil.getFilterByDateExpr()
									);
							List<GenericValue>partyRelationshipGv = EntityQuery.use(delegator).from("PartyRelationship").select("partyIdFrom").where(partyRelationshipGvCondition).queryList();
							if(UtilValidate.isNotEmpty(partyRelationshipGv)) {
								for(GenericValue partyRelationship : partyRelationshipGv) {
									String partyIdFrom = (String) partyRelationship.getString("partyIdFrom");
									if(UtilValidate.isNotEmpty(partyIdFrom)) {
										data.put("primaryContactName", PartyHelper.getPartyName(delegator, partyIdFrom, false));
										data.put("primaryContactId", partyIdFrom);
										Map<String, String> primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, partyIdFrom, UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true), true);
										if (UtilValidate.isNotEmpty(primaryContactInformation)) {
											data.put("primaryContactEmail", primaryContactInformation.get("EmailAddress"));
											data.put("primaryContactPhone", primaryContactInformation.get("PrimaryPhone"));
										}
									}
								}
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
						
						data.put("partyId", partyId);
						data.put("externalId", (String) partySummary.get("externalId"));
						data.put("gstnNo", (String) partySummary.get("gstnNo"));
						
						data.put("statusDescription", UtilValidate.isNotEmpty(statusMap) && UtilValidate.isNotEmpty(statusId) ? statusMap.get(statusId) : "");
						data.put("dataSourceDesc", UtilValidate.isNotEmpty(partyDataSrcMap) && UtilValidate.isNotEmpty(partyId) ? partyDataSrcMap.get(partyId) : "");
						
						data.put("industryDescription", UtilValidate.isNotEmpty((String) partySummary.get("industryEnumId")) ? industryMap.get((String) partySummary.get("industryEnumId")) : "");
						data.put("ownershipDescription", UtilValidate.isNotEmpty((String) partySummary.get("ownershipEnumId")) ? ownershipMap.get((String) partySummary.get("ownershipEnumId")) : "");
						data.put("annualRevenue", annualRevenue);
						data.put("annualRevenueOrgVal", (String) partySummary.get("annualRevenue"));
						data.put("uomDescription", UtilValidate.isNotEmpty(uomMap) && UtilValidate.isNotEmpty((String) partySummary.get("preferredCurrencyUomId")) ? uomMap.get((String) partySummary.get("preferredCurrencyUomId")) : "");
						
						data.put("timeZoneDescription", UtilValidate.isNotEmpty((String) partySummary.get("timeZoneDesc")) ? timeZoneMap.get((String) partySummary.get("timeZoneDesc")) :"");
						data.put("insideRep","");
						
                        if (!isPostalSearch) {
                        	GenericValue postalAddress = (GenericValue) postalAddressList.get(postalContactMechId);
                        	if (UtilValidate.isNotEmpty(postalAddress)) {
                        		address1 = postalAddress.getString("address1");
    							address2 = postalAddress.getString("address2");
                        		genCity = postalAddress.getString("city");
                        		genCountryGeoId = postalAddress.getString("countryGeoId");
    							genStateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
                            	postalCode = postalAddress.getString("postalCode");
                        	}
                        } else {
                        	address1 = (String) partySummary.get("address1");
							address2 = (String) partySummary.get("address2");
                        	genCity = (String) partySummary.get("city");
                        	genCountryGeoId = (String) partySummary.get("countryGeoId");
							genStateProvinceGeoId = (String) partySummary.get("stateProvinceGeoId");
                        	postalCode = (String) partySummary.get("postalCode");
                        }
						
                        if (UtilValidate.isNotEmpty(email)) {
                        	infoString = (String) partySummary.get("infoString");
                        } else {
                        	infoString = (String) emailAddressList.get(emailContactMechId);
                        }
                        
                        if (UtilValidate.isNotEmpty(contactNumber)) {
                        	contactNumber = (String) partySummary.get("contactNumber");
                        } else {
                        	contactNumber = (String) contactNumberList.get(telecomContactMechId);
                        }
						
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
						data.put("domainEntityType", DomainEntityType.ACCOUNT);
						data.put("domainEntityTypeDesc", DataHelper.convertToLabel( DomainEntityType.ACCOUNT ));
						data.put("externalLoginKey", externalLoginKey);	
						
						dataList.add(data);
	                }
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
				Debug.logInfo("data ready: "+UtilDateTime.nowTimestamp(), MODULE);
			} else {
				
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
		
		if (UtilValidate.isNotEmpty(isExportAction) && isExportAction.equals("Y")) {
			return JSONObject.fromObject(result).toString();
		}
		
        return doJSONResponse(response, result);
    }

	public static String getAccountDashboardCountList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
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
				String _active_account_query_ = "SELECT COUNT(DISTINCT P.PARTY_ID), 'openAccount' \r\n" + 
						"FROM PARTY P \r\n" + 
						"INNER JOIN PARTY_GROUP PG ON P.PARTY_ID = PG.PARTY_ID \r\n" + 
						"LEFT OUTER JOIN PARTY_RELATIONSHIP PR ON P.PARTY_ID = PR.PARTY_ID_FROM \r\n" + 
						"LEFT OUTER JOIN PARTY_SUPPLEMENTAL_DATA PSD ON P.PARTY_ID = PSD.PARTY_ID \r\n";

				_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" P.ROLE_TYPE_ID ='ACCOUNT'";
				
				_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") +" (P.STATUS_ID <> 'PARTY_DISABLED' OR P.STATUS_ID IS NULL)";
				
				if (UtilValidate.isNotEmpty(filterType) && "my-account".equals(filterType)) {
					_active_account_query_ = _active_account_query_ + "LEFT OUTER JOIN PARTY_RELATIONSHIP PR1 ON P.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
							+ " PR1.PARTY_ID_TO ='"+userLoginPartyId+"'"
							+ " AND PR1.ROLE_TYPE_ID_TO = 'ACCOUNT_MANAGER'"
							+ " AND PR1.ROLE_TYPE_ID_FROM = 'ACCOUNT'"
							+ " AND PR1.SECURITY_GROUP_ID = 'ACCOUNT_OWNER'"
							+ " AND PR1.PARTY_RELATIONSHIP_TYPE_ID= 'RESPONSIBLE_FOR'"
							+ " AND ((PR1.THRU_DATE IS NULL OR PR1.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') AND (PR1.FROM_DATE IS NULL OR PR1.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))";
					
				} else if (UtilValidate.isNotEmpty(filterType) && "my-team-account".equals(filterType)) {
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
					
					_active_account_query_ = _active_account_query_ + "LEFT OUTER JOIN PARTY_RELATIONSHIP PR1 ON P.PARTY_ID = PR1.PARTY_ID_FROM \r\n";
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") 
							+ " PR1.PARTY_ID_TO IN ("+org.fio.admin.portal.util.DataUtil.toList(partyIds, "")+")"
							+ " AND PR1.ROLE_TYPE_ID_TO = 'ACCOUNT_MANAGER'"
							+ " AND PR1.ROLE_TYPE_ID_FROM = 'ACCOUNT'"
							+ " AND PR1.SECURITY_GROUP_ID = 'ACCOUNT_OWNER'"
							+ " AND PR1.PARTY_RELATIONSHIP_TYPE_ID= 'RESPONSIBLE_FOR'"
							+ " AND ((PR1.THRU_DATE IS NULL OR PR1.THRU_DATE > '"+UtilDateTime.nowTimestamp()+"') AND (PR1.FROM_DATE IS NULL OR PR1.FROM_DATE <= '"+UtilDateTime.nowTimestamp()+"'))";
				} else if (UtilValidate.isNotEmpty(filterType) && "my-bu-account".equals(filterType)) {
					
				}

				if("account-active".equals(filterBy)) {
					_active_account_query_ = _active_account_query_ + " WHERE "+_where_condition_;
					System.out.println("_active_account_query_=====>"+_active_account_query_);
					rs = sqlProcessor.executeQuery(_active_account_query_);
					
					if(rs !=null){
						try{ 
							int i = 0;
							while (rs.next()) {
								Map<String, Object> data = new HashMap<String, Object>();
								count = rs.getString(1);
								barType = rs.getString(2);
								data.put("barId", barType);
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
}
