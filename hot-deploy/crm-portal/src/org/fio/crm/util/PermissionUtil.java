/**
 * 
 */
package org.fio.crm.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class PermissionUtil {

	public static final String MODULE = PermissionUtil.class.getName();

	public static boolean havePartyViewPermission (Delegator delegator, HttpSession session, String partyId) {
		
		try {
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
			GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
			if(UtilValidate.isEmpty(party)) {
				return true;
			}
			
			List conditionsList = new ArrayList();
			conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			
			String virtualTeamId = null;
			
			Map<String, Object> dataSecurityMetaInfo = (Map<String, Object>) session.getAttribute("dataSecurityMetaInfo");
			if (ResponseUtils.isSuccess(dataSecurityMetaInfo)) {
				
				List<String> lowerPositionPartyIds = (List<String>) dataSecurityMetaInfo.get("lowerPositionPartyIds");
				if (UtilValidate.isNotEmpty(lowerPositionPartyIds)) {
					
					List < EntityCondition > securityConditions = new ArrayList < EntityCondition > ();
					
					Map<String, Object> virtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, null, userLogin.getString("partyId"));
					
					if (UtilValidate.isEmpty(virtualTeam.get("virtualTeamId"))) {
						securityConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, lowerPositionPartyIds),
								EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
								EntityUtil.getFilterByDateExpr()
								));
					}
					
					if (UtilValidate.isNotEmpty(userLogin)) {
						securityConditions.add(EntityCondition.makeCondition(UtilMisc.toList(
								EntityCondition.makeCondition("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId"))
								//securityConditions
							), EntityOperator.OR));
					}
					
					// virtual team [start]
					
					virtualTeamId = UtilValidate.isNotEmpty(virtualTeamId) ? virtualTeamId : (String) virtualTeam.get("virtualTeamId");
					//String loggedUserVirtualTeamId = (String) dataSecurityMetaInfo.get("loggedUserVirtualTeamId");
					//virtualTeamId = UtilValidate.isNotEmpty(virtualTeamId) ? virtualTeamId : loggedUserVirtualTeamId;
					List<Map<String, Object>> virtualTeamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, null, userLogin.getString("partyId"));
					if (UtilValidate.isNotEmpty(virtualTeamMemberList)) {
						
						Set<String> virtualTeamIdAsLeadList = VirtualTeamUtil.getVirtualTeamIds(virtualTeamMemberList, "VT_SG_TL", true);
						if (UtilValidate.isNotEmpty(virtualTeamIdAsLeadList)) {
							securityConditions.add(EntityCondition.makeCondition("virtualTeamId", EntityOperator.IN, virtualTeamIdAsLeadList));
							Set<String> virtualTeamMemberPartyIdList = new HashSet<String>();
							for (String vtId : virtualTeamIdAsLeadList) {
								List<Map<String, Object>> teamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, vtId, null);
								virtualTeamMemberPartyIdList.addAll( DataUtil.getFieldListFromMapList(teamMemberList, "virtualTeamMemberId", true) );
							}
							
							if (UtilValidate.isNotEmpty(virtualTeamMemberPartyIdList)) {
								securityConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
										EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, virtualTeamMemberPartyIdList),
										EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
										EntityUtil.getFilterByDateExpr()
										));
							}
							
						}
						
						Set<String> virtualTeamIdAsMemberList = VirtualTeamUtil.getVirtualTeamIds(virtualTeamMemberList, "VT_SG_TL", false);
						if (UtilValidate.isNotEmpty(virtualTeamIdAsMemberList)) {
							securityConditions.add(EntityCondition.makeCondition(UtilMisc.toList(
									EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")),
									EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
									//EntityCondition.makeCondition("virtualTeamId", EntityOperator.IN, virtualTeamIdAsMemberList),
									EntityUtil.getFilterByDateExpr()
								), EntityOperator.AND));
							
						}
						
					}
					
					// virtual team [end]
	
					EntityCondition securityCondition = EntityCondition.makeCondition(UtilMisc.toList(
							securityConditions
							), EntityOperator.OR);
					
					conditionsList.add(securityCondition);
				}
				
				System.out.println("lowerPositionPartyIds> "+lowerPositionPartyIds);
				
			}
			
			EntityCondition accessConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			System.out.println("accessConditons>>>> "+accessConditons);
			GenericValue partySupplementalData = EntityUtil.getFirst( delegator.findList("PartyFromSummaryByRelationship", accessConditons, null, null, null, false) );
			System.out.println("partySupplementalData>>>> "+partySupplementalData);
			
			if (UtilValidate.isNotEmpty(partySupplementalData)) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);

		}
		
		return false;
	}
	
    public static boolean haveContactViewPermission (Delegator delegator, HttpSession session, String partyId) {
		
		try {
			
			GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
			if(UtilValidate.isEmpty(party)) {
				return true;
			}
			
			List conditionsList = new ArrayList();
			conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			
			Map<String, Object> dataSecurityMetaInfo = (Map<String, Object>) session.getAttribute("dataSecurityMetaInfo");
			if (ResponseUtils.isSuccess(dataSecurityMetaInfo)) {
				
				List<String> lowerPositionPartyIds = (List<String>) dataSecurityMetaInfo.get("lowerPositionPartyIds");
				if (UtilValidate.isNotEmpty(lowerPositionPartyIds)) {
					
					EntityCondition securityConditions = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, lowerPositionPartyIds),
							EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
							EntityUtil.getFilterByDateExpr()
					), EntityOperator.AND);
					EntityQuery partyRelationshipQuery = EntityQuery.use(delegator).from("PartyRelationship")
                    		.where(securityConditions);
                    List<String> partyRelationship = EntityUtil.getFieldListFromEntityList(partyRelationshipQuery.queryList(), "partyIdFrom", true);
                    /*if(partyRelationship != null && partyRelationship.size() > 0) {
                        EntityCondition partyIdToCondition = EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyRelationship);
                        conditionsList.add(partyIdToCondition);
                    }*/
                    EntityCondition partyIdToCondition = EntityCondition.makeCondition(UtilMisc.toList(
                            EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyRelationship),
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyRelationship)
                        ),EntityOperator.OR);
                    conditionsList.add(partyIdToCondition);
				}
				
				Debug.log("lowerPositionPartyIds> "+lowerPositionPartyIds);
				
			}
			
			EntityCondition accessConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			Debug.log("accessConditons>>>> "+accessConditons);
			GenericValue partySupplementalData = EntityUtil.getFirst( delegator.findList("PartyFromSummaryByRelationship", accessConditons, null, null, null, false) );
			Debug.log("partySupplementalData>>>> "+partySupplementalData);
			
			if (UtilValidate.isNotEmpty(partySupplementalData)) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);

		}
		
		return false;
	}
    
}
