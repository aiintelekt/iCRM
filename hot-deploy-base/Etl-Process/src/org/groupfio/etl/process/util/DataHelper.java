/**
 * 
 */
package org.groupfio.etl.process.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class DataHelper {
	
	private static String MODULE = DataHelper.class.getName();

	public static boolean isResponsibleForParty(Delegator delegator, String partyId) {
		
		try {
			List<EntityCondition> conditions = new ArrayList <EntityCondition>();
			//conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_MANAGER"));
			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			
			conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
			        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_MANAGER")
					));
			
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
			        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
			        EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			GenericValue responsibleForParty = EntityUtil.getFirst( delegator.findList("PartyToSummaryByRole", mainConditons, null, null, null, false) );
			if(UtilValidate.isNotEmpty(responsibleForParty)) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean isResponsibleForParty(Delegator delegator, String employeePartyId, String jobFamily, String countryGeoId, String city, String leadScore) {
		
		try {
			
			List<EntityCondition> conditions = new ArrayList <EntityCondition>();
			
			String emplPositionTypeId = null;
			if (UtilValidate.isNotEmpty(leadScore) && leadScore.equals("LEAD_SCORE_HOT")) {
				emplPositionTypeId = "DBS_CENTRAL";
			} else if (UtilValidate.isNotEmpty(jobFamily) && jobFamily.equals("JOBFAMILY_0001")) {
				emplPositionTypeId = "DBS_TC";
			} else if (UtilValidate.isNotEmpty(jobFamily) && jobFamily.equals("JOBFAMILY_0002")) {
				emplPositionTypeId = "DBS_RM";
			}
			
			if (UtilValidate.isNotEmpty(emplPositionTypeId) && UtilValidate.isNotEmpty(countryGeoId) && UtilValidate.isNotEmpty(city)) {
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, emplPositionTypeId),
						EntityCondition.makeCondition("employeePartyId", EntityOperator.EQUALS, employeePartyId),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_OCCUPIED"),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"),
						EntityCondition.makeCondition("countryGeoId", EntityOperator.EQUALS, countryGeoId),
						EntityCondition.makeCondition("city", EntityOperator.EQUALS, city),
						EntityUtil.getFilterByDateExpr("actualFromDate", "actualThruDate")
						)
						);
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityUtil.getFilterByDateExpr()
						)
						);
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue responsibleForParty = EntityUtil.getFirst( delegator.findList("EmplPositionAndFulfillment", mainConditons, null, null, null, false) );
				if (UtilValidate.isNotEmpty(responsibleForParty)) {
			        return true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static String getResponsibleForParty(Delegator delegator, String jobFamily, String countryGeoId, String city, String postalCode, String leadScore, String virtualTeamId) {
		
		try {
			
			List<EntityCondition> conditions = new ArrayList <EntityCondition>();
			
			String emplPositionTypeId = null;
			if (UtilValidate.isNotEmpty(leadScore) && leadScore.equals("LEAD_SCORE_HOT")) {
				emplPositionTypeId = "DBS_CENTRAL";
			} else if (UtilValidate.isNotEmpty(jobFamily) && jobFamily.equals("JOBFAMILY_0001")) {
				emplPositionTypeId = "DBS_TC";
			} else if (UtilValidate.isNotEmpty(jobFamily) && jobFamily.equals("JOBFAMILY_0002")) {
				emplPositionTypeId = "DBS_RM";
			}
			
			if (UtilValidate.isNotEmpty(emplPositionTypeId) && UtilValidate.isNotEmpty(countryGeoId) && UtilValidate.isNotEmpty(city)) {
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, emplPositionTypeId),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_OCCUPIED"),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"),
						EntityCondition.makeCondition("countryGeoId", EntityOperator.EQUALS, countryGeoId),
						EntityCondition.makeCondition("city", EntityOperator.EQUALS, city),
						EntityUtil.getFilterByDateExpr("actualFromDate", "actualThruDate")
						)
						);
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityUtil.getFilterByDateExpr()
						)
						);
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> responsibleForPartyList = delegator.findList("EmplPositionAndFulfillment", mainConditons, null, null, null, false);
				if (UtilValidate.isNotEmpty(responsibleForPartyList)) {
					
					List<String> responsibleForPartyIdList = EntityUtil.getFieldListFromEntityList(responsibleForPartyList, "employeePartyId", true);
					
					// filter base on postalCode [start]
					
					if (UtilValidate.isNotEmpty(postalCode)) {
						
						List<EntityCondition> conditionList = FastList.newInstance();
						conditionList.add(EntityCondition.makeCondition("countryGeoId", EntityOperator.EQUALS, countryGeoId));
		                conditionList.add(EntityCondition.makeCondition("city", EntityOperator.EQUALS, city));
		                conditionList.add(EntityCondition.makeCondition("postalCode", EntityOperator.EQUALS, postalCode));
		                List<GenericValue> locationAssocList = delegator.findList("UserLocationAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		                
		                if (UtilValidate.isNotEmpty(locationAssocList)) {
		                	List<String> locationAssocPartyIdList = EntityUtil.getFieldListFromEntityList(locationAssocList, "partyId", true);
		                	responsibleForPartyIdList.retainAll(locationAssocPartyIdList);
		                }
						
					}
					
					// filter base on postalCode [end]
					
					// filter base on virtualTeam [start]
					
					if (UtilValidate.isNotEmpty(virtualTeamId)) {
						
						List<Map<String, Object>> virtualTeamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, virtualTeamId, null);
		                if (UtilValidate.isNotEmpty(virtualTeamMemberList)) {
		                	List<String> virtualTeamMemberPartyIdList = DataUtil.getFieldListFromMapList(virtualTeamMemberList, "virtualTeamMemberId", true);
		                	//responsibleForPartyIdList = virtualTeamMemberPartyIdList;
		                	responsibleForPartyIdList.retainAll(virtualTeamMemberPartyIdList);
		                }
						
						/*if (virtualTeam.get("securityGroupId").equals("VT_SG_TL")) {
							List<Map<String, Object>> virtualTeamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, virtualTeam.get("virtualTeamId").toString(), null);
			                if (UtilValidate.isNotEmpty(virtualTeamMemberList)) {
			                	List<String> virtualTeamMemberPartyIdList = DataUtil.getFieldListFromMapList(virtualTeamMemberList, "virtualTeamMemberId", true);
			                	//responsibleForPartyIdList = virtualTeamMemberPartyIdList;
			                	responsibleForPartyIdList.retainAll(virtualTeamMemberPartyIdList);
			                }
						} else {
							responsibleForPartyIdList = new ArrayList<String>() { 
					            { 
					                add(virtualTeam.get("virtualTeamMemberId").toString()); 
					            } 
					        }; 
						}*/
					}
					
					// filter base on virtualTeam [end]
					
					Map<String, Long> employeeList = new LinkedHashMap<String, Long>();
					for (String responsibleForPartyId : responsibleForPartyIdList) {
						String employeePartyId = responsibleForPartyId;
						
						EntityCondition responsibleForCountCond = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employeePartyId),
								EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
								EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, "ACCOUNT_OWNER"),
								EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
								EntityUtil.getFilterByDateExpr()
								);
						
						long responsibleForCount = delegator.findCountByCondition("PartyRelationship", responsibleForCountCond, null, null);
						
						employeeList.put(employeePartyId, responsibleForCount);
					}
					
			        LinkedHashMap<String, Long> sortedEmplList = new LinkedHashMap<>();
			        
			        employeeList.entrySet()
			        .stream()
			        .sorted(Map.Entry.comparingByValue())
			        .forEachOrdered(x -> sortedEmplList.put(x.getKey(), x.getValue()));
			        
			        Map.Entry<String, Long> firstResult = sortedEmplList.entrySet().iterator().next();
			        String employeePartyId= firstResult.getKey();
			        Long responsibleForCount = firstResult.getValue();
			        
			        Debug.logInfo("employeePartyId: " + employeePartyId + ", responsibleForCount: "+responsibleForCount, MODULE);
					
			        return employeePartyId;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getResponsibleForParty(Delegator delegator, String partyId) {
		
		try {
			
			if (UtilValidate.isEmpty(partyId)) {
				return null;
			}
			
			List<EntityCondition> conditions = new ArrayList <EntityCondition>();
			
			conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
			        EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD", "CONTACT")),
			        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
					EntityUtil.getFilterByDateExpr()
					)
					);
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			GenericValue responsibleForParty = EntityUtil.getFirst( delegator.findList("PartyRelationship", mainConditons, null, null, null, false) );
			if (UtilValidate.isNotEmpty(responsibleForParty)) {
				return responsibleForParty.getString("partyIdTo");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getEnumDescription(Delegator delegator, String value, String enumTypeId) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);    
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId),
	               			condition
	               			);       
				}
				
				GenericValue enumEntity = EntityUtil.getFirst( delegator.findList("Enumeration", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
				if (UtilValidate.isNotEmpty(enumEntity)) {
					return enumEntity.getString("description");
				}
			}
		} catch (Exception e) {
		}
		
		return "";
	}
	
	public static String getPartyIdentificationDescription(Delegator delegator, String value) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("partyIdentificationTypeId")), EntityOperator.EQUALS, value.toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toUpperCase())
               			);
				
				GenericValue partyIdentificationType = EntityUtil.getFirst( delegator.findList("PartyIdentificationType", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
				
				if (UtilValidate.isNotEmpty(partyIdentificationType)) {
					return partyIdentificationType.getString("description");
				}
			}
		} catch (Exception e) {
		}
		
		return "";
	}
	
	public static String getGeoName(Delegator delegator, String value, String geoTypeId) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoName")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			));   
				
				conditions.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.IN, StringUtil.split(geoTypeId, ",")));
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue entity = EntityUtil.getFirst( delegator.findList("Geo", mainConditon, null, UtilMisc.toList("-createdStamp"), null, false) );
				
				if (UtilValidate.isNotEmpty(entity)) {
					return entity.getString("geoName");
				}
			}
		} catch (Exception e) {
		}
		
		return "";
	}
}
