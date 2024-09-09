package org.fio.homeapps.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public final class UtilActivity {

    private static final String MODULE = UtilActivity.class.getName();

    private UtilActivity() { }

    /**
     * gets all unexpired parties related to the work effort. The result is a list of WorkEffortPartyAssignments containing
     * the partyIds we need.
     */
    public static List<GenericValue> getActivityParties(Delegator delegator, String workEffortId, List<String> partyRoles) throws GenericEntityException {
        // add each role type id (ACCOUNT, CONTACT, etc) to an OR condition list
        List<EntityCondition> roleCondList = new ArrayList<EntityCondition>();
        for (String roleTypeId : partyRoles) {
            roleCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
        }
        EntityCondition roleEntityCondList = EntityCondition.makeCondition(roleCondList, EntityOperator.OR);

        // roleEntityCondList AND workEffortId = ${workEffortId} AND filterByDateExpr
        EntityCondition mainCondList = EntityCondition.makeCondition(EntityOperator.AND,
                    roleEntityCondList,
                    EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
                    EntityUtil.getFilterByDateExpr());

        EntityListIterator partiesIt = delegator.find("WorkEffortPartyAssignment", mainCondList, null,
                null,
                null, // fields to order by (unimportant here)
                new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
        List<GenericValue> parties = partiesIt.getCompleteList();
        partiesIt.close();

        return parties;
    }

    /**
     * Gets owner party id of activity.
     */
    public static GenericValue getActivityOwner(String workEffortId, Delegator delegator) throws GenericEntityException {
        List<GenericValue> ownerParties = EntityUtil.filterByDate(getActivityParties(delegator, workEffortId, UtilMisc.toList("CAL_OWNER")));
        if (UtilValidate.isEmpty(ownerParties)) {
            Debug.logWarning("No owner parties found for activity [" + workEffortId + "]", MODULE);
            return null;
        } else if (ownerParties.size() > 1) {
            Debug.logWarning("More than one owner party found for activity [" + workEffortId + "].  Only the first party will be returned, but the parties are " + EntityUtil.getFieldListFromEntityList(ownerParties, "partyId", false), MODULE);
        }

        return EntityUtil.getFirst(ownerParties);

    }
    
    public static String getActivityOwnerLoginId(Delegator delegator, String workEffortId) {
    	String loginId = null;
    	String partyId = getActivityOwnerPartyId(delegator, workEffortId);
    	if (UtilValidate.isNotEmpty(partyId)) {
    		loginId = org.fio.homeapps.util.DataUtil.findLoginIdByPartyId(delegator, partyId);
    	}
    	return loginId;
    }
    
    public static String getActivityOwnerPartyId(Delegator delegator, String workEffortId) {
    	String partyId = null;
		try {
			if (UtilValidate.isNotEmpty(workEffortId)) {
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
						EntityCondition.makeCondition(EntityOperator.OR,
		                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CAL_OWNER")
			            ),
						EntityUtil.getFilterByDateExpr()
		                ));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue partyAssignment = EntityQuery.use(delegator).select("partyId").from("WorkEffortPartyAssignment").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(partyAssignment)) {
					partyId = partyAssignment.getString("partyId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return partyId;
	}
    
    public static String getCurrentTimeFromTimeZone(Delegator delegator,String timeZone) {

    	String currentTimeForTimezone = null;

    	try {

    		if (UtilValidate.isNotEmpty(timeZone)) {
    			GenericValue tzCodeGv = delegator.findOne("Enumeration", UtilMisc.toMap("enumId",timeZone), false);
    			if(UtilValidate.isEmpty(tzCodeGv)) {
    				tzCodeGv = EntityQuery.use(delegator).from("Enumeration").where("enumCode",timeZone).queryFirst();
    			}
    			String enumCode = tzCodeGv.getString("enumCode");

    			//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    			Calendar cal = Calendar.getInstance((TimeZone.getTimeZone(enumCode)));
    			df.setTimeZone(cal.getTimeZone());
    			currentTimeForTimezone = df.format(cal.getTime());

    		} 
    	}catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    	}

    	return currentTimeForTimezone;
    }
    
    public static boolean expireWorkEffortPartyAssignment(Delegator delegator, String workEffortId, String partyId, String roleTypeId) {
		try {
			if (UtilValidate.isNotEmpty(workEffortId)) {
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
						EntityUtil.getFilterByDateExpr()
		                ));
				
				if (UtilValidate.isNotEmpty(roleTypeId)) {
					conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> partyAssignmentList = delegator.findList("WorkEffortPartyAssignment", mainConditons, null, null, null, false);
				if (UtilValidate.isNotEmpty(partyAssignmentList)) {
					for (GenericValue partyAssignment : partyAssignmentList) {
						partyAssignment.put("thruDate", UtilDateTime.nowTimestamp());
						partyAssignment.store();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return false;
	}
    
    public static boolean isContractorTechnician(Delegator delegator, String partyId) {
        try {
        	if (UtilValidate.isNotEmpty(partyId)) {
        		List conditionList = FastList.newInstance();
            	
            	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
            	conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "TECHNICIAN"));
            	conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTRACT_TYPE"));
        		
        		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        		
        		long count = delegator.findCountByCondition("PartyRelationship", mainConditons, null, null);
        		if (count > 0) {
        			return true;
        		}
        	}
        } catch (Exception e) {
        	Debug.logError(e, MODULE);
        }
        return false;
    }
    
    public static String getActivityOwnerName(Delegator delegator, String activityOwnerRole, String workEffortId) {
    	return getActivityOwnerName(delegator, activityOwnerRole, workEffortId, false);
    }
    
    public static String getActivityOwnerName(Delegator delegator, String activityOwnerRole, String workEffortId, boolean isApplyExpireFilter) {
    	String ownerName = null;
		try {
			if (UtilValidate.isNotEmpty(workEffortId)) {
				if (UtilValidate.isEmpty(activityOwnerRole)) {
					activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
				}
				
				List conditions = new ArrayList<>();
				conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
				conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"));
				if(UtilValidate.isNotEmpty(activityOwnerRole)) {
					if(activityOwnerRole.contains(",")) {
						List<String> ownerRoles = DataHelper.stringToList(activityOwnerRole, ",");
						conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ownerRoles ));
					} else {
						conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, activityOwnerRole));
					}
				}
				
				if (isApplyExpireFilter) {
					conditions.add(EntityUtil.getFilterByDateExpr());
				}
				
				List<GenericValue> activityOwners = EntityQuery.use(delegator).select("partyId").from("WorkEffortPartyAssignment").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).filterByDate().cache(true).queryList();
				if(UtilValidate.isNotEmpty(activityOwners)) {
					List<String> ownerPartyIds = EntityUtil.getFieldListFromEntityList(activityOwners, "partyId", true);
					List<String> ownersName = new ArrayList<>();
					for(String ownerPartyId : ownerPartyIds) {
						ownersName.add(PartyHelper.getPartyName(delegator, ownerPartyId, false));
					}
					ownerName = DataHelper.listToString(ownersName);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return ownerName;
	}
    
    public static String getActivityContactName(Delegator delegator, String workEffortId) {
    	String contactName = null;
		try {
			if (UtilValidate.isNotEmpty(workEffortId)) {
				List conditions = new ArrayList<>();
				
				conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
				//conditions.add(EntityUtil.getFilterByDateExpr());
				
				GenericValue activityContact = EntityQuery.use(delegator).select("partyId").from("WorkEffortContact").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).filterByDate().cache(true).queryFirst();
				if(UtilValidate.isNotEmpty(activityContact)) {
					contactName = PartyHelper.getPartyName(delegator, activityContact.getString("partyId"), false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return contactName;
	}
    
    public static String getActivityAttrValue(Delegator delegator, String workEffortId, String attrName) {
		try {
			if (UtilValidate.isNotEmpty(workEffortId) && UtilValidate.isNotEmpty(attrName)) {
				List conditions = new ArrayList<>();
				conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
				conditions.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, attrName));
				GenericValue entity = EntityQuery.use(delegator).select("attrValue").from("WorkEffortAttribute").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).cache(false).queryFirst();
				if(UtilValidate.isNotEmpty(entity)) {
					return entity.getString("attrValue");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
    
    public static void storeActivityAttribute(Delegator delegator, String workEffortId, String attrName, String attrValue) {
		try {
			if (UtilValidate.isNotEmpty(workEffortId) && UtilValidate.isNotEmpty(attrName)) {
				GenericValue attr = delegator.makeValue("WorkEffortAttribute");
				attr.put("workEffortId", workEffortId);
				attr.put("attrName", attrName);
				attr.put("attrValue", attrValue);
				delegator.createOrStore(attr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
	}
    
    public static Map<String, Object> getContractorTechList(Delegator delegator) {
    	Map<String, Object> techList = new HashMap<>();
        try {
        	List conditionList = FastList.newInstance();
        	
        	conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "TECHNICIAN"));
        	conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTRACT_TYPE"));
    		
    		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		
    		Set<String> fieldToSelect = new LinkedHashSet<>();
    		fieldToSelect.add("partyIdTo");
    		fieldToSelect.add("roleTypeIdTo");
    		
    		List<GenericValue> techAssocList = delegator.findList("PartyRelationship", mainConditons, fieldToSelect, null, null, false);
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
    
    public static String getPartyNamesFromCommExtension(Delegator delegator, Map<String, Object> filter) {
    	String partyNames = "";
        try {
        	if (UtilValidate.isNotEmpty(filter)) {
        		String workEffortId = (String) filter.get("workEffortId");
        		String workExtName = (String) filter.get("workExtName");
        		String wftExtType = (String) filter.get("workExtType");
        		
        		List conditionList = FastList.newInstance();
            	
            	conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
            	if (UtilValidate.isNotEmpty(workExtName)) {
            		conditionList.add(EntityCondition.makeCondition("workExtName", EntityOperator.EQUALS, workExtName));
            	}
            	if (UtilValidate.isNotEmpty(wftExtType)) {
            		conditionList.add(EntityCondition.makeCondition("wftExtType", EntityOperator.EQUALS, wftExtType));
            	}
        		
        		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        		
        		List<GenericValue> commExtList = EntityQuery.use(delegator).select("wftExtValue").from("WorkEffortCommExtension").where(mainConditons).queryList();
        		if (UtilValidate.isNotEmpty(commExtList)) {
        			List<String> emailList = commExtList.stream().map(x-> x.getString("wftExtValue")).distinct().collect(Collectors.toList());
        			List<Map<String, Object>> entryList = CommonDataHelper.getPartyListFromEmails(delegator, emailList);
        			List<String> partyNameList = entryList.stream().map(x->(String)x.get("partyName")).collect(Collectors.toList());
        			partyNames = StringUtil.join(partyNameList, ",");
        		}
        		
        	}
        	
        } catch (Exception e) {
        	e.printStackTrace();
        	Debug.logError(e, MODULE);
        }
        return partyNames;
    }
    
}
