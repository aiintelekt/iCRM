package org.fio.admin.portal.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

import javolution.util.FastList;
import net.fortuna.ical4j.model.DateList;

/**
 * @author Sharif
 *
 */
public class ResAvailUtil {

	private static String MODULE = ResAvailUtil.class.getName();
	
	public static Map getCalBookedData(Delegator delegator, String domainEntityType, String domainEntityId) {
		try {
			if (UtilValidate.isNotEmpty(domainEntityType) && UtilValidate.isNotEmpty(domainEntityId)) {
				List conditions = FastList.newInstance();
				
				conditions.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
				conditions.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
				
				conditions.add(EntityCondition.makeCondition("availabilityTypeId", EntityOperator.EQUALS, "RESA_TYP_NON_AVAIL"));
				
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                List<GenericValue> entryList = delegator.findList("ResourceAvailability", mainConditons, null, null, null, false);
    			if (UtilValidate.isNotEmpty(entryList)) {
    				Map<String, Object> selectedCalSlot = new LinkedHashMap<>();
    				for (GenericValue entry : entryList) {
    					Map<String, Object> calSlot = new LinkedHashMap<>();
    					calSlot.put("startTime", UtilDateTime.timeStampToString(entry.getTimestamp("fromDate"), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault()));
    					calSlot.put("endTime", UtilDateTime.timeStampToString(entry.getTimestamp("thruDate"), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault()));
    					
    					selectedCalSlot.put(DataUtil.getPartyUserLoginId(delegator, entry.getString("partyId")), calSlot);
    				}
    				return selectedCalSlot;
    			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static Map<String, Map<String, Object>> getTechList(Delegator delegator, Map<String, Map<String, Object>> techList, String locationId
			, Map<String, Object> requestContext, String techPriorityType
			) {
        try {
        	
        	String state = (String) requestContext.get("state");
        	String county = (String) requestContext.get("county");
        	String notInCounty = (String) requestContext.get("notInCounty");
        	String isResourceType = (String) requestContext.get("isResourceType");
        	
        	String techPriorityDesc = AdminPortalConstant.REEB_TECH_PRIORITY_TYPE.get(techPriorityType);
        	
        	List conditionList = FastList.newInstance();
    		
        	if (UtilValidate.isNotEmpty(locationId)) {
        		conditionList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, locationId));
        	}
        	
    		if (UtilValidate.isNotEmpty(state)) {
    			conditionList.add(EntityCondition.makeCondition("state", EntityOperator.EQUALS, state));
    		}
    		if (UtilValidate.isNotEmpty(county)) {
    			conditionList.add(EntityCondition.makeCondition("county", EntityOperator.EQUALS, county));
    		}
    		if (UtilValidate.isNotEmpty(notInCounty)) {
    			conditionList.add(EntityCondition.makeCondition("county", EntityOperator.NOT_EQUAL, notInCounty));
    		}
    		
    		if (UtilValidate.isNotEmpty(isResourceType) && isResourceType.equals("TECH_INSPECTOR")) {
    			conditionList.add(EntityCondition.makeCondition("isTechInspection", EntityOperator.EQUALS, "Y"));
    		}
    		
    		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		
    		List<GenericValue> techAssocList = delegator.findList("ProductStoreTechAssoc", mainConditons, null, null, null, false);
    		//Map<String, Map<String, Object>> techList = new LinkedHashMap<String, Map<String, Object>>();
    		if (UtilValidate.isNotEmpty(techAssocList)) {
    			for (GenericValue techAssoc : techAssocList) {
    				
    				Map<String, Object> tech = new LinkedHashMap<>();
    				if (UtilValidate.isNotEmpty(techAssoc.getString("technicianId01"))) {
    					if (!techList.containsKey(techAssoc.getString("technicianId01"))) {
        					String userLoginId = DataUtil.getPartyUserLoginId(delegator, techAssoc.getString("technicianId01"));
    						if (UtilValidate.isNotEmpty(userLoginId)) {
    							tech.put("name", techAssoc.getString("technicianName01"));
            					tech.put("userLoginId", userLoginId);
            					tech.put("techPriorityType", techPriorityType);
            					tech.put("techPriorityDesc", techPriorityDesc);
            					
            					if(org.fio.admin.portal.util.DataUtil.is3rdPartyTechnician(delegator, techAssoc.getString("technicianId01"))) {
            						tech.put("techType", "CONTRACTOR");
            					} else
            						tech.put("techType", org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, techAssoc.getString("technicianId01")));
            					
            					techList.put(techAssoc.getString("technicianId01"), tech);
    						}
        				}
    				}
    				
    				if (UtilValidate.isNotEmpty(techAssoc.getString("technicianId02"))) {
    					tech = new LinkedHashMap<>();
        				if (!techList.containsKey(techAssoc.getString("technicianId02"))) {
        					String userLoginId = DataUtil.getPartyUserLoginId(delegator, techAssoc.getString("technicianId02"));
    						if (UtilValidate.isNotEmpty(userLoginId)) {
    							tech.put("name", techAssoc.getString("technicianName02"));
            					tech.put("userLoginId", DataUtil.getPartyUserLoginId(delegator, techAssoc.getString("technicianId02")));
            					tech.put("techPriorityType", techPriorityType);
            					tech.put("techPriorityDesc", techPriorityDesc);
            					
            					if(org.fio.admin.portal.util.DataUtil.is3rdPartyTechnician(delegator, techAssoc.getString("technicianId02"))) {
            						tech.put("techType", "CONTRACTOR");
            					} else
            						tech.put("techType", org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, techAssoc.getString("technicianId02")));

            					
            					techList.put(techAssoc.getString("technicianId02"), tech);
    						}
        				}
    				}
    				
    				if (UtilValidate.isNotEmpty(techAssoc.getString("technicianId03"))) {
    					tech = new LinkedHashMap<>();
        				if (!techList.containsKey(techAssoc.getString("technicianId03"))) {
        					String userLoginId = DataUtil.getPartyUserLoginId(delegator, techAssoc.getString("technicianId03"));
    						if (UtilValidate.isNotEmpty(userLoginId)) {
    							tech.put("name", techAssoc.getString("technicianName03"));
            					tech.put("userLoginId", DataUtil.getPartyUserLoginId(delegator, techAssoc.getString("technicianId03")));
            					tech.put("techPriorityType", techPriorityType);
            					tech.put("techPriorityDesc", techPriorityDesc);
            					
            					if(org.fio.admin.portal.util.DataUtil.is3rdPartyTechnician(delegator, techAssoc.getString("technicianId03"))) {
            						tech.put("techType", "CONTRACTOR");
            					} else
            						tech.put("techType", org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, techAssoc.getString("technicianId03")));

            					
            					
            					techList.put(techAssoc.getString("technicianId03"), tech);
    						}
        				}
    				}
    				
    				if (UtilValidate.isNotEmpty(techAssoc.getString("technicianId04"))) {
    					tech = new LinkedHashMap<>();
        				if (!techList.containsKey(techAssoc.getString("technicianId04"))) {
        					String userLoginId = DataUtil.getPartyUserLoginId(delegator, techAssoc.getString("technicianId04"));
    						if (UtilValidate.isNotEmpty(userLoginId)) {
    							tech.put("name", techAssoc.getString("technicianName04"));
            					tech.put("userLoginId", DataUtil.getPartyUserLoginId(delegator, techAssoc.getString("technicianId04")));
            					tech.put("techPriorityType", techPriorityType);
            					tech.put("techPriorityDesc", techPriorityDesc);
            					
            					if(org.fio.admin.portal.util.DataUtil.is3rdPartyTechnician(delegator, techAssoc.getString("technicianId04"))) {
            						tech.put("techType", "CONTRACTOR");
            					} else
            						tech.put("techType", org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, techAssoc.getString("technicianId04")));

            					
            					
            					techList.put(techAssoc.getString("technicianId04"), tech);
    						}
        				}
    				}
    			}
    		}
        } catch (Exception e) {
        	Debug.logError(e, MODULE);
        }

        return techList;
    }
	
	public static Map<String, Map<String, Object>> getContractorTechList(Delegator delegator, Map<String, Map<String, Object>> techList
			, Map<String, Object> requestContext, String techPriorityType
			) {

        try {
        	
        	String state = (String) requestContext.get("state");
        	String county = (String) requestContext.get("county");
        	String isResourceType = (String) requestContext.get("isResourceType");
        	
        	String techPriorityDesc = AdminPortalConstant.REEB_TECH_PRIORITY_TYPE.get(techPriorityType);
        	
        	String roleTypeId = "TECHNICIAN";
        	if (UtilValidate.isNotEmpty(isResourceType) && isResourceType.equals("TECH_INSPECTOR")) {
        		roleTypeId = "TECH_INSPECTOR";
        	}
        	
        	List conditionList = FastList.newInstance();
        	
        	conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeId));
        	conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTRACT_TYPE"));
    		
    		/*conditionList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, locationId));
    		if (UtilValidate.isNotEmpty(state)) {
    			conditionList.add(EntityCondition.makeCondition("state", EntityOperator.EQUALS, state));
    		}
    		if (UtilValidate.isNotEmpty(county)) {
    			conditionList.add(EntityCondition.makeCondition("county", EntityOperator.EQUALS, county));
    		}*/
    		
    		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		
    		Set<String> fieldToSelect = new LinkedHashSet<>();
    		fieldToSelect.add("partyIdTo");
    		fieldToSelect.add("roleTypeIdTo");
    		
    		List<GenericValue> techAssocList = delegator.findList("PartyRelationship", mainConditons, fieldToSelect, null, null, false);
    		//Map<String, Map<String, Object>> techList = new LinkedHashMap<String, Map<String, Object>>();
    		if (UtilValidate.isNotEmpty(techAssocList)) {
    			for (GenericValue techAssoc : techAssocList) {
    				fieldToSelect = new LinkedHashSet<>();
    				fieldToSelect.add("partyId");
    				fieldToSelect.add("userLoginId");
    				
    				String partyId = techAssoc.getString("partyIdTo");
    				String partyRoleTypeId = techAssoc.getString("roleTypeIdTo");
    				
    				GenericValue user = DataUtil.getActiveUserLoginByPartyId(delegator, partyId);
    				if(UtilValidate.isNotEmpty(user)) {
    					Map<String, Object> tech = new LinkedHashMap<>();
    					if (!techList.containsKey(partyId)) {
        					tech.put("name", PartyHelper.getPartyName(delegator, partyId, false));
        					tech.put("userLoginId", user.getString("userLoginId"));
        					tech.put("partyId", partyId);
        					tech.put("partyRoleTypeId", partyRoleTypeId);
        					tech.put("techPriorityType", techPriorityType);
        					tech.put("techPriorityDesc", techPriorityDesc);
        					tech.put("techType", "CONTRACTOR");
        					techList.put(partyId, tech);
        				}
    				}
    			}
    		}
        } catch (Exception e) {
        	Debug.logError(e, MODULE);
        }

        return techList;
    }
	
	public static Map<String, Map<String, Object>> getOtherTechList(Delegator delegator, Map<String, Map<String, Object>> techList
			, Map<String, Object> requestContext, String techPriorityType
			) {

        try {
        	
        	List<String> assignedTechPartyIds = (List<String>) requestContext.get("assignedTechPartyIds");
        	String isResourceType = (String) requestContext.get("isResourceType");
        	
        	String techPriorityDesc = AdminPortalConstant.REEB_TECH_PRIORITY_TYPE.get(techPriorityType);
        	Set<String> techPartyIds = techList.keySet().stream().collect(Collectors.toSet());
        	
        	String roleTypeIds = "TECHNICIAN";
        	if (UtilValidate.isNotEmpty(isResourceType) && isResourceType.equals("TECH_INSPECTOR")) {
        		roleTypeIds = "TECH_INSPECTOR";
        	}
        	
        	List<Map<String, Object>> dataList = org.fio.homeapps.util.PartyHelper.getActivePartyUserLogin(delegator, assignedTechPartyIds, Arrays.asList(roleTypeIds.split(",")));
        	if (UtilValidate.isNotEmpty(dataList)) {
        		dataList = dataList.stream()
        					.filter(x->{
        						if (techPartyIds.contains(x.get("partyId"))) {
        							return false;
        						}
        						return true;
        					})
        					.collect(Collectors.toList());
        		
        		dataList.forEach(data->{
        			Map<String, Object> tech = new LinkedHashMap<>();
        			String partyId = (String) data.get("partyId");
        			tech.put("name", PartyHelper.getPartyName(delegator, partyId, false));
					tech.put("userLoginId", data.get("userLoginId"));
					tech.put("techPriorityType", techPriorityType);
					tech.put("techPriorityDesc", techPriorityDesc);
					if(org.fio.admin.portal.util.DataUtil.is3rdPartyTechnician(delegator, partyId)) {
						tech.put("techType", "CONTRACTOR");
					} else
						tech.put("techType", data.get("roleTypeId"));
					
					
					techList.put(partyId, tech);
        		});
        	}
    		
        } catch (Exception e) {
        	Debug.logError(e, MODULE);
        }

        return techList;
    }
	
	public static boolean isDowntime(Delegator delegator, String technicianId, Timestamp startDate, Timestamp endDate
			) {
        try {
        	List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, technicianId));
			conditionList.add(EntityCondition.makeCondition("availabilityTypeId", EntityOperator.EQUALS, "RESA_TYP_NON_AVAIL"));
			if (UtilValidate.isNotEmpty(startDate)) {
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, startDate));
			}
			if (UtilValidate.isNotEmpty(endDate)) {
				conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, endDate));
			}
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			//delegator.findList("ResourceAvailability", mainConditons, null, null, null, false);
    		long count = delegator.findCountByCondition("ResourceAvailability", mainConditons, null, null);
    		if (count > 0) {
    			return true;
    		}
        } catch (Exception e) {
        	Debug.logError(e, MODULE);
        }
        return false;
    }
	
	public static boolean isTechnicianEndedActivity(Delegator delegator, String workEffortId, String partyId) {
        try {
        	if (UtilValidate.isNotEmpty(partyId)) {
        		EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
        				EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "ACTIVITY"),
        				EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, workEffortId),
        				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
        				EntityCondition.makeCondition("endDate", EntityOperator.LESS_THAN, org.ofbiz.base.util.UtilDateTime.nowTimestamp())
        				);
        		long count = EntityQuery.use(delegator).from("ResourceAvailability").where(condition).queryCount();
    			if (count > 0) {
        			return true;
        		}
        	}
        } catch (Exception e) {
        	Debug.logError(e, MODULE);
        }
        return false;
    }
	
}
