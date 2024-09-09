/**
 * 
 */
package org.fio.admin.portal.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fio.admin.portal.util.ResAvailUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class ResAvailServices {

	private static final String MODULE = ResAvailServices.class.getName();

	public static Map createResAvail(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String partyId = (String) context.get("partyId");
		String reasonId = (String) context.get("reasonId");
		String availabilityTypeId = (String) context.get("availabilityTypeId");
		String domainEntityType = (String) context.get("domainEntityType");
		String domainEntityId = (String) context.get("domainEntityId");

		String fromDate = (String) context.get("fromDate_date");
		String thruDate = (String) context.get("thruDate_date");

		String fromTime = (String) context.get("fromDate_time");
		String thruTime = (String) context.get("thruDate_time");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			
			String userLoginId = userLogin.getString("userLoginId");
			
			Timestamp fromDateTime = ParamUtil.getTimestamp(fromDate, fromTime, "yyyy-MM-dd HH:mm");
			Timestamp thruDateTime = ParamUtil.getTimestamp(thruDate, thruTime, "yyyy-MM-dd HH:mm");
			
			GenericValue entity = delegator.makeValue("ResourceAvailability");
			
			String entryId = delegator.getNextSeqId("ResourceAvailability");
			
			entity.put("entryId", entryId);
			entity.put("partyId", partyId);
			entity.put("reasonId", reasonId);
			entity.put("availabilityTypeId", availabilityTypeId);
			entity.put("domainEntityType", domainEntityType);
			entity.put("domainEntityId", domainEntityId);
			entity.put("fromDate", fromDateTime);
			entity.put("thruDate", thruDateTime);
			
			entity.put("createdByUserLogin", userLoginId);
			
			entity.create();
			
			result.put("entryId", entryId);

		} catch (Exception e) {
			//e.printStackTrace();
			
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully created resource availablity.."));

		return result;
	}

	public static Map updateResAvail(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String entryId = (String) context.get("entryId");

		String partyId = (String) context.get("partyId");
		String reasonId = (String) context.get("reasonId");
		String availabilityTypeId = (String) context.get("availabilityTypeId");
		String domainEntityType = (String) context.get("domainEntityType");
		String domainEntityId = (String) context.get("domainEntityId");

		String fromDate = (String) context.get("fromDate_date");
		String thruDate = (String) context.get("thruDate_date");

		String fromTime = (String) context.get("fromDate_time");
		String thruTime = (String) context.get("thruDate_time");

		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			result.put("entryId", entryId);
			String userLoginId = userLogin.getString("userLoginId");
			
			List conditionList = FastList.newInstance();
    		
    		conditionList.add(EntityCondition.makeCondition("entryId", EntityOperator.EQUALS, entryId));
    		
    		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		
    		GenericValue entity = EntityUtil.getFirst( delegator.findList("ResourceAvailability", mainConditons, null, null, null, false) );
    		if (UtilValidate.isEmpty(entity)) {
    			result.putAll(ServiceUtil.returnError("Resource Availablity not exists!"));
    			return result;
    		}
    		
    		Timestamp fromDateTime = ParamUtil.getTimestamp(fromDate, fromTime, "yyyy-MM-dd HH:mm");
			Timestamp thruDateTime = ParamUtil.getTimestamp(thruDate, thruTime, "yyyy-MM-dd HH:mm");
			
			entity.put("partyId", partyId);
			entity.put("reasonId", reasonId);
			entity.put("availabilityTypeId", availabilityTypeId);
			entity.put("domainEntityType", domainEntityType);
			entity.put("domainEntityId", domainEntityId);
			entity.put("fromDate", fromDateTime);
			entity.put("thruDate", thruDateTime);
			
			entity.put("modifiedByUserLogin", userLoginId);
			
			entity.store();
			
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully updated resource availablity.."));

		return result;

	}
	
	public static Map findResourceAvailability(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");

		String locationId = (String) requestContext.get("locationId");
		String state = (String) requestContext.get("state");
		String county = (String) requestContext.get("county");
		String techPriorityType = (String) requestContext.get("techPriorityType");
		String assignedTechLoginIds = (String) requestContext.get("assignedTechLoginIds");
		String isSkipCalSlot = (String) requestContext.get("isSkipCalSlot");
		String isResourceType = (String) requestContext.get("isResourceType");
		String is3PartyTechnician = (String) requestContext.get("is3PartyTechnician");
		
		String duration = (String) requestContext.get("duration");

		Timestamp startDate = (Timestamp) requestContext.get("startDate");
		Timestamp endDate = (Timestamp) requestContext.get("endDate");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> responseContext = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			
			String userLoginId = userLogin.getString("userLoginId");
			
			List<String> assignedTechPartyIds = new ArrayList<String>();
			if (UtilValidate.isNotEmpty(assignedTechLoginIds)) {
				assignedTechPartyIds = Arrays.asList( Arrays.stream(assignedTechLoginIds.split(","))
					.map(loginId -> org.fio.homeapps.util.DataUtil.getPartyIdByUserLoginId(delegator, loginId.toString()))
					.toArray(String[]::new)
					);
			}
			
			if (UtilValidate.isEmpty(endDate) && UtilValidate.isNotEmpty(startDate)) {
				endDate = UtilDateTime.addDaysToTimestamp(startDate, 30);
			}
			
			Map<String, Map<String, Object>> techList = new LinkedHashMap<String, Map<String, Object>>();
			
			if (UtilValidate.isNotEmpty(techPriorityType) && techPriorityType.equals("REEB-ASSIGNED")) {
				if (UtilValidate.isNotEmpty(assignedTechPartyIds)) {
					ResAvailUtil.getOtherTechList(delegator, techList, UtilMisc.toMap("assignedTechPartyIds", assignedTechPartyIds, "isResourceType", isResourceType), "REEB-ASSIGNED");
				}
			} else if (UtilValidate.isNotEmpty(techPriorityType) && techPriorityType.equals("REEB-RECOMMENDED")) {
				ResAvailUtil.getTechList(delegator, techList, locationId, UtilMisc.toMap("state", state, "county", county, "isResourceType", isResourceType), "REEB-RECOMMENDED");
			} else if (UtilValidate.isNotEmpty(techPriorityType) && techPriorityType.equals("REEB-OTHER")) {
				Map<String, Map<String, Object>> recommendedTechList = ResAvailUtil.getTechList(delegator, new LinkedHashMap<String, Map<String, Object>>(), locationId, UtilMisc.toMap("state", state, "county", county, "isResourceType", isResourceType), "REEB-RECOMMENDED");
				Map<String, Map<String, Object>> techOtherStateList = ResAvailUtil.getTechList(delegator, new LinkedHashMap<String, Map<String, Object>>(), null, UtilMisc.toMap("state", state, "notInCounty", county, "isResourceType", isResourceType), "REEB-OTHER");
				techList = techOtherStateList.entrySet().stream()
				        .filter(x -> {
				        	//System.out.println(x.getKey());
				        	if (!recommendedTechList.keySet().contains(x.getKey())) {
				        		return true;
				        	}
				        	return false;
				        })
				        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			} else if (UtilValidate.isNotEmpty(techPriorityType) && techPriorityType.equals("CONTRACTOR")) {
				ResAvailUtil.getContractorTechList(delegator, techList, UtilMisc.toMap("state", state, "county", county, "isResourceType", isResourceType), "CONTRACTOR");
			} else if (UtilValidate.isNotEmpty(techPriorityType) && techPriorityType.equals("OTHER")) {
				if (UtilValidate.isNotEmpty(isSkipCalSlot) && isSkipCalSlot.equals("N") && UtilValidate.isNotEmpty(assignedTechPartyIds)) {
					ResAvailUtil.getOtherTechList(delegator, techList, UtilMisc.toMap("assignedTechPartyIds", assignedTechPartyIds, "isResourceType", isResourceType), "REEB-ASSIGNED");
				}
				ResAvailUtil.getTechList(delegator, techList, locationId, UtilMisc.toMap("state", state, "county", county, "isResourceType", isResourceType), "REEB-RECOMMENDED");
				ResAvailUtil.getTechList(delegator, techList, null, UtilMisc.toMap("state", state, "notInCounty", county, "isResourceType", isResourceType), "REEB-OTHER");
				ResAvailUtil.getContractorTechList(delegator, techList, UtilMisc.toMap("state", state, "county", county, "isResourceType", isResourceType), "CONTRACTOR");
				
				ResAvailUtil.getOtherTechList(delegator, techList, UtilMisc.toMap("state", state, "county", county, "isResourceType", isResourceType), "OTHER");
				techList = techList.entrySet().stream().filter(tech->{
					if (tech.getValue().get("techPriorityType").equals("OTHER")) {
						return true;
					}
					return false;
				})
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			} else if (UtilValidate.isEmpty(is3PartyTechnician) || is3PartyTechnician.equals("N")) {
				
				if (UtilValidate.isNotEmpty(isSkipCalSlot) && isSkipCalSlot.equals("N") && UtilValidate.isNotEmpty(assignedTechPartyIds)) {
					ResAvailUtil.getOtherTechList(delegator, techList, UtilMisc.toMap("assignedTechPartyIds", assignedTechPartyIds, "isResourceType", isResourceType), "REEB-ASSIGNED");
				}
				
				ResAvailUtil.getTechList(delegator, techList, locationId, UtilMisc.toMap("state", state, "county", county, "isResourceType", isResourceType), "REEB-RECOMMENDED");
				ResAvailUtil.getTechList(delegator, techList, null, UtilMisc.toMap("state", state, "notInCounty", county, "isResourceType", isResourceType), "REEB-OTHER");
				ResAvailUtil.getContractorTechList(delegator, techList, UtilMisc.toMap("state", state, "county", county, "isResourceType", isResourceType), "CONTRACTOR");
				ResAvailUtil.getOtherTechList(delegator, techList, UtilMisc.toMap("state", state, "county", county, "isResourceType", isResourceType), "OTHER");
			}
			
    		if (UtilValidate.isNotEmpty(techList) && UtilValidate.isNotEmpty(startDate) && UtilValidate.isNotEmpty(endDate)
    				&& (UtilValidate.isNotEmpty(isSkipCalSlot) && isSkipCalSlot.equals("N"))
    				) {
    			for (String technicianId : techList.keySet()) {
    				Map<String, Object> tech = techList.get(technicianId);
    				
    				if (ResAvailUtil.isDowntime(delegator, technicianId, startDate, endDate)) {
    					continue;
    				}
    				
    				List<Map> availableList = new ArrayList<Map>();
    				List<Map> nonAvailableList = new ArrayList<Map>();
    				if (tech.containsKey("availableList")) {
    					availableList = (List<Map>) tech.get("availableList");
    				}
    				if (tech.containsKey("nonAvailableList")) {
    					nonAvailableList = (List<Map>) tech.get("nonAvailableList");
    				}
    				
    				List conditionList = FastList.newInstance();
    				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, technicianId));
    				conditionList.add(EntityCondition.makeCondition("availabilityTypeId", EntityOperator.EQUALS, "RESA_TYP_NON_AVAIL"));
    				if (UtilValidate.isNotEmpty(startDate)) {
    					conditionList.add(EntityCondition.makeCondition(EntityOperator.OR, 
    							EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, null),
    							EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate)
    							));
    				}
    				if (UtilValidate.isNotEmpty(endDate)) {
    					conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
    				}
    				
    				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    	    		List<GenericValue> resAvlList = delegator.findList("ResourceAvailability", mainConditons, null, UtilMisc.toList("fromDate"), null, false);
    	    		if (UtilValidate.isNotEmpty(resAvlList)) {
    	    			
    	    			Timestamp tempStartDate = (Timestamp) requestContext.get("startDate");
    	    			Timestamp fromDate = null;
    	    			Timestamp thruDate = null;
    	    			for (GenericValue resAvl : resAvlList) {
    	    				fromDate = resAvl.getTimestamp("fromDate");
    	    				thruDate = resAvl.getTimestamp("thruDate");
    	    				
    	    				if (tempStartDate.before(fromDate)) {
    	    					if (UtilValidate.isNotEmpty(duration)) {
    	    						Timestamp compareTime = UtilDateTime.addValueToTimestamp(tempStartDate, duration);
    	    						if (!compareTime.before(fromDate)) {
    	    							continue;
    	    						}
    	    					}
    	    					Map<String, Object> available = new LinkedHashMap<>();
            	    			available.put("start", tempStartDate);
            	    			available.put("end", fromDate);
            	    			available.put("startLong", tempStartDate.getTime());
            	    			available.put("endLong", fromDate.getTime());
            	    			availableList.add(available);
    	    				}
    	    				
        	    			tempStartDate = thruDate;
    	    				
    	    				Map<String, Object> nonAvailable = new LinkedHashMap<>();
    	    				nonAvailable.put("start", fromDate);
    	    				nonAvailable.put("end", thruDate);
    	    				nonAvailable.put("startLong", fromDate.getTime());
    	    				nonAvailable.put("endLong", thruDate.getTime());
    	    				nonAvailableList.add(nonAvailable);
    	    			}
    	    			
    	    			if (tempStartDate.before(endDate)) {
    	    				if (UtilValidate.isNotEmpty(duration)) {
    	    					Timestamp compareTime = UtilDateTime.addValueToTimestamp(tempStartDate, duration);
	    						if (!compareTime.before(endDate)) {
	    							continue;
	    						}
	    					}
    	    				Map<String, Object> available = new LinkedHashMap<>();
        	    			available.put("start", tempStartDate);
        	    			available.put("end", endDate);
        	    			available.put("startLong", tempStartDate.getTime());
        	    			available.put("endLong", endDate.getTime());
        	    			availableList.add(available);
    	    			}
    	    			
    	    		} else {
    	    			if (UtilValidate.isNotEmpty(duration)) {
    	    				Timestamp compareTime = UtilDateTime.addValueToTimestamp(startDate, duration);
    						if (!compareTime.before(endDate)) {
    							tech.put("availableList", availableList);
    		    	    		tech.put("nonAvailableList", nonAvailableList);
    		    	    		techList.put(technicianId, tech);
    							continue;
    						}
    					}
    	    			Map<String, Object> available = new LinkedHashMap<>();
    	    			available.put("start", startDate);
    	    			available.put("end", endDate);
    	    			available.put("startLong", startDate.getTime());
    	    			available.put("endLong", endDate.getTime());
    	    			availableList.add(available);
    	    		}
    	    		
    	    		tech.put("availableList", availableList);
    	    		tech.put("nonAvailableList", nonAvailableList);
    	    		techList.put(technicianId, tech);
    			}
    		}
    		responseContext.put("techList", techList);
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.put("responseContext", responseContext);
		result.putAll(ServiceUtil.returnSuccess("Successfully retrived resource availablity.."));
		return result;
	}
	
}
