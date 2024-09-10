package org.groupfio.common.portal.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.UtilGenerator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
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
public class OpportunityContactServices {

	private static final String MODULE = OpportunityContactServices.class.getName();
    
    public static Map assignContactToOpportunity(DispatchContext dctx, Map context) {
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String salesOpportunityId = (String) context.get("salesOpportunityId");
    	String contactPartyId = (String) context.get("contactPartyId");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		
    		Timestamp currentTime = UtilDateTime.nowTimestamp();
    		
    		GenericValue opprotunity = EntityUtil.getFirst( delegator.findByAnd("SalesOpportunity",UtilMisc.toMap("salesOpportunityId", salesOpportunityId), null, false) );
    		if (UtilValidate.isEmpty(opprotunity)) {
    			result.putAll(ServiceUtil.returnError("Opportunity not exists!"));
    			return result;
    		}
    		
    		GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party",UtilMisc.toMap("partyId", contactPartyId), null, false) );
    		if (UtilValidate.isEmpty(party)) {
    			result.putAll(ServiceUtil.returnError("Contact not exists!"));
    			return result;
    		}
    		
    		List conditionsList = FastList.newInstance();
			
			conditionsList.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, salesOpportunityId));
			conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId));
			
			EntityCondition roleCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
			
			conditionsList.add(roleCondition);
			conditionsList.add(EntityUtil.getFilterByDateExpr());
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			
			GenericValue opportunityRole = EntityUtil.getFirst( delegator.findList("SalesOpportunityRole", mainConditons, UtilMisc.toSet("partyId"), null, null, false) );
			if (UtilValidate.isNotEmpty(opportunityRole)) {
				result.putAll(ServiceUtil.returnError("Contact already assigned!"));
    			return result;
			}
    		
			String ownerPartyId = userLogin.getString("partyId");
			
			callCtxt = FastMap.newInstance();
			callCtxt.put("salesOpportunityId", salesOpportunityId);
			callCtxt.put("partyId", contactPartyId);
			callCtxt.put("roleTypeId", "CONTACT");
			callCtxt.put("userLogin", userLogin);
			callCtxt.put("ownerId", ownerPartyId);
			callCtxt.put("fromDate", currentTime);
			
			String ownerBu = "";
			GenericValue userLoginPerson = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId", userLogin.getString("userLoginId")).queryOne();
			if (UtilValidate.isNotEmpty(userLoginPerson)) {
				ownerBu = userLoginPerson.getString("businessUnit");
	        }
			callCtxt.put("ownerBu",ownerBu);
			if(UtilValidate.isNotEmpty(ownerPartyId)){
				GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", ownerPartyId).queryList());
		  		if (UtilValidate.isNotEmpty(emplTeam) && UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId"))) {    
		  			String emplTeamId = emplTeam.getString("emplTeamId");
					String businessUnit = emplTeam.getString("businessUnit");
					if(UtilValidate.isNotEmpty(emplTeamId)) {
						callCtxt.put("emplTeamId", emplTeamId);
			    	}
					if(UtilValidate.isNotEmpty(businessUnit)) {
						callCtxt.put("ownerBu", businessUnit);
			    	}
		  		}
			}
			
			callResult = dispatcher.runSync("createSalesOpportunityRole", callCtxt);
			if(ServiceUtil.isError(callResult) || ServiceUtil.isFailure(callResult)){
                //responseMessage = UtilProperties.getMessage(RESOURCE, "SalesOpportunityCreationFailed", locale);
                result.putAll(ServiceUtil.returnError("Failed! assigned contact to opportunity: "+ServiceUtil.getErrorMessage(callResult)));
                return result;
            }
    		
			result.put("salesOpportunityId", salesOpportunityId);
			
			result.putAll(ServiceUtil.returnSuccess("Successfully assigned contact to opportunity.."));
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
    	
    	return result;
    	
    }
    
}
