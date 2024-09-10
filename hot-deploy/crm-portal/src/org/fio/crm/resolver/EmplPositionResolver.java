/**
 * 
 */
package org.fio.crm.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fio.crm.constants.CrmConstants;
import org.fio.crm.constants.ResponseCodes;
import org.fio.crm.util.ParamUtil;
import org.fio.crm.util.VirtualTeamUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;

/**
 * @author Sharif
 *
 */
public class EmplPositionResolver extends Resolver {
	
	private static String MODULE = EmplPositionResolver.class.getName();
	
	private static EmplPositionResolver instance;
	
	public static synchronized EmplPositionResolver getInstance(){
        if(instance == null) {
            instance = new EmplPositionResolver();
        }
        return instance;
    }

	@Override
	protected Map<String, Object> doResolve(Map<String, Object> context) throws Exception {
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			
			LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher"); 
			Delegator delegator = (Delegator) context.get("delegator");  
			GenericValue userLogin = (GenericValue) context.get("userLogin"); 
			
			String currentEmplPositionId = ParamUtil.getString(context, "currentEmplPositionId");
			String emplTeamId = ParamUtil.getString(context, "emplTeamId");
			
			try {
	    		
				String hierarchyModelEnabled = EntityUtilProperties.getPropertyValue("crm", "HIERARCHY_MODEL_ENABLED", "", delegator);
				
				if (UtilValidate.isNotEmpty(hierarchyModelEnabled) && hierarchyModelEnabled.equals("Y")) {
					String userLoginId = userLogin.getString("userLoginId");
					
					Set<String> lowerEmplPositionIds = new HashSet<String>();
					List<String> lowerPositionPartyIds = new ArrayList<String>();
					
					if (UtilValidate.isEmpty(currentEmplPositionId)) {
						currentEmplPositionId = getEmplPositionIdbyUserlogin(delegator, userLoginId);
					}
					
					prepareLowerPositions(delegator, currentEmplPositionId, emplTeamId, lowerEmplPositionIds);
					lowerPositionPartyIds = prepareLowerPositionPartyIds(delegator, lowerEmplPositionIds);
					
					if (UtilValidate.isEmpty(lowerPositionPartyIds)) {
						lowerPositionPartyIds.add( userLogin.getString("partyId") );
					}
					
					response.put("currentEmplPositionId", currentEmplPositionId);
					response.put("emplTeamId", emplTeamId);
					
					response.put("lowerEmplPositionIds", lowerEmplPositionIds);
					response.put("lowerPositionPartyIds", lowerPositionPartyIds);
					
					Map<String, Object> loggedUserVirtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, userLogin.getString("partyId"));
					response.put("loggedUserVirtualTeam", loggedUserVirtualTeam);
					response.put("loggedUserVirtualTeamId", loggedUserVirtualTeam.get("virtualTeamId"));
					response.put("loggedUserVirtualTeamRole", loggedUserVirtualTeam.get("securityGroupId"));
				}
				
			} catch (Exception e) {
				//e.printStackTrace();
				Debug.logError("Error resolve employee position: "+e.getMessage(), MODULE);
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
			
			response.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(CrmConstants.RESPONSE_MESSAGE, e.getMessage());
			
			return response;
			
		}
		
		response.put(CrmConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		
		return response;

	}
	
	public void prepareLowerPositions(Delegator delegator, String currentEmplPositionId, String emplTeamId, Set<String> emplPositionIds) {
		
		try {
			
			if (UtilValidate.isEmpty(currentEmplPositionId)) {
				return;
			}
			
			EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("emplPositionIdManagedBy", EntityOperator.EQUALS, currentEmplPositionId),
					EntityUtil.getFilterByDateExpr()
					);
			
			if (UtilValidate.isNotEmpty(emplTeamId)) {
				searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, emplTeamId),
						searchConditions
						);
			}
			
			List<GenericValue> reportingStructList = delegator.findList("EmplPositionReportingStruct", searchConditions, null, null, null, false);
			if (UtilValidate.isNotEmpty(reportingStructList)) {
				for (GenericValue reportingStruct : reportingStructList) {
			
					if (!emplPositionIds.contains(reportingStruct.getString("emplPositionIdReportingTo"))) {
						emplPositionIds.add( reportingStruct.getString("emplPositionIdManagedBy") );
						prepareLowerPositions(delegator, reportingStruct.getString("emplPositionIdReportingTo"), reportingStruct.getString("emplTeamId"), emplPositionIds);
						
					} else {
						emplPositionIds.add( reportingStruct.getString("emplPositionIdManagedBy") );
					}
					
				}
			} else {
				emplPositionIds.add( currentEmplPositionId );
			}
			
		} catch (GenericEntityException e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
	}
	
	public List<String> prepareLowerPositionPartyIds(Delegator delegator, Set<String> emplPositionIds) {
		
		List<String> lowerPositionPartyIds = new ArrayList<String>();
		
		if (UtilValidate.isEmpty(emplPositionIds)) {
			return lowerPositionPartyIds;
		}
		
		try {
			EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("emplPositionId", EntityOperator.IN, emplPositionIds)
					
					
					//EntityUtil.getFilterByDateExpr()
					
					);
			
			List<GenericValue> resultList = delegator.findList("EmplPositionFulfillment", searchConditions, null, null, null, false);
			if (UtilValidate.isNotEmpty(resultList)) {
				lowerPositionPartyIds = EntityUtil.getFieldListFromEntityList(resultList, "partyId", true);
			}
			
		} catch (GenericEntityException e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return lowerPositionPartyIds;
	}
	
	public static String getEmplPositionIdbyUserlogin(Delegator delegator, String userLoginId) {
		
		try {
			if (UtilValidate.isNotEmpty(userLoginId)) {
				
				GenericValue userLogin = EntityUtil.getFirst( delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), null, false) );
				if (UtilValidate.isNotEmpty(userLogin)) {
					String partyId = userLogin.getString("partyId");
					EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
							EntityUtil.getFilterByDateExpr()
							);
					
					GenericValue positionFulfillment = EntityUtil.getFirst( delegator.findList("EmplPositionFulfillment", searchConditions, null, null, null, false) );
					if (UtilValidate.isNotEmpty(positionFulfillment)) {
						return positionFulfillment.getString("emplPositionId");
					}
					
				}
				
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return null;
	}

}
