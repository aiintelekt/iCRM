/**
 * 
 */
package org.groupfio.lead.service.services;

import java.util.HashMap;
import java.util.Map;

import org.groupfio.common.portal.util.UtilAssocParty;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class LeadServices {

	private static final String MODULE = LeadServices.class.getName();
	
	public static Map convertLead(DispatchContext dctx, Map context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	String leadPartyId = (String) context.get("leadPartyId");
    	String leadConvertType = (String) context.get("leadConvertType");
    	
    	String externalLoginKey = (String) context.get("externalLoginKey");
    	
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		result.put("partyId", leadPartyId);
    		result.put("externalLoginKey", externalLoginKey);
    		
    		if (UtilValidate.isNotEmpty(leadPartyId)) {
    			String convertedRoleTypeId = UtilValidate.isNotEmpty(leadConvertType) ? leadConvertType : "ACCOUNT";
    			result.put("convertedRoleTypeId", convertedRoleTypeId);
    			
    			GenericValue lead = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", leadPartyId), false);
    			if (UtilValidate.isNotEmpty(lead)) {
    				
    				if (UtilValidate.isNotEmpty(leadConvertType) && leadConvertType.equals("CUSTOMER")) {
    					
    					delegator.storeByCondition("Party", UtilMisc.toMap("partyTypeId", "PERSON"), EntityCondition.makeCondition(EntityOperator.AND,
	    						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadPartyId)
								));
    					
                    	Map<String, Object> contactAcctMap = new HashMap<String, Object>();
						contactAcctMap.put("partyIdTo", leadPartyId);
						contactAcctMap.put("partyRoleTypeId", "LEAD");
						String contactPartyId = UtilAssocParty.getPrimaryContactId(delegator, contactAcctMap);
						
						if (UtilValidate.isNotEmpty(contactPartyId)) {
							GenericValue contact = EntityQuery.use(delegator).from("Person").where("partyId", contactPartyId).queryFirst();
							Map<String, Object> input = UtilMisc.toMap("firstName", contact.get("firstName"), "lastName", contact.get("lastName"));
							input.put("partyId", leadPartyId);
							input.put("firstNameLocal", context.get("firstNameLocal"));
							input.put("lastNameLocal", context.get("lastNameLocal"));
							input.put("personalTitle", context.get("personalTitle"));
							input.put("preferredCurrencyUomId",
									context.get("preferredCurrencyUomId"));
							input.put("description", context.get("description"));
							input.put("birthDate", context.get("birthDate"));
							input.put("gender", context.get("gender"));
							Map<String, Object> serviceResults = dispatcher.runSync("createPerson", input);
							if (ServiceUtil.isSuccess(serviceResults)) {
								delegator.storeByCondition("PartyContactMech", UtilMisc.toMap("partyId", leadPartyId), EntityCondition.makeCondition(EntityOperator.AND,
			    						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId),
			    						EntityUtil.getFilterByDateExpr()
										));
								
								delegator.storeByCondition("PartyContactMechPurpose", UtilMisc.toMap("partyId", leadPartyId, "partyRelAssocId", null), EntityCondition.makeCondition(EntityOperator.AND,
			    						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId),
			    						EntityUtil.getFilterByDateExpr()
										));
								
								callResult = dispatcher.runSync("setPartyStatus", UtilMisc.toMap("partyId", contactPartyId, "statusId", "PARTY_DISABLED", "userLogin", userLogin));
							}
						}
                    }
    				
    				EntityCondition rolecondition = EntityCondition.makeCondition(EntityOperator.AND,
    						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadPartyId),
    						EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "LEAD"));
    				
    				callResult = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", leadPartyId, "roleTypeId", convertedRoleTypeId, "userLogin", userLogin));
    				
    				delegator.storeByCondition("MarketingCampaignRole", UtilMisc.toMap("roleTypeId", convertedRoleTypeId), rolecondition);
    				
    				delegator.storeByCondition("PartyRelationship", UtilMisc.toMap("roleTypeIdFrom", convertedRoleTypeId), EntityCondition.makeCondition(EntityOperator.AND,
    						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadPartyId),
    						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD")));
    				
    				delegator.storeByCondition("PartyRelationship", UtilMisc.toMap("roleTypeIdTo", convertedRoleTypeId), EntityCondition.makeCondition(EntityOperator.AND,
    						EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, leadPartyId),
    						EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "LEAD")));
    				
    				delegator.storeByCondition("WorkEffortPartyAssignment", UtilMisc.toMap("roleTypeId", convertedRoleTypeId), EntityCondition.makeCondition(EntityOperator.AND,
    						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadPartyId)));
    				
    				delegator.storeByCondition("SalesOpportunityRole", UtilMisc.toMap("roleTypeId", convertedRoleTypeId), EntityCondition.makeCondition(EntityOperator.AND,
    						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadPartyId),
    						EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "LEAD")));
    				
    				delegator.storeByCondition("ContentRole", UtilMisc.toMap("roleTypeId", convertedRoleTypeId), EntityCondition.makeCondition(EntityOperator.AND,
    						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadPartyId),
    						EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "LEAD")));
    				
    				delegator.storeByCondition("Party", UtilMisc.toMap("roleTypeId", convertedRoleTypeId), EntityCondition.makeCondition(EntityOperator.AND,
    						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadPartyId)));
    				
                    // set the status of the lead to PTYLEAD_CONVERTED
                    callResult = dispatcher.runSync("setPartyStatus", UtilMisc.toMap("partyId", leadPartyId, "statusId", "LEAD_CONVERTED", "userLogin", userLogin));
                    
                    // clean abandon information [start]
                    delegator.removeByAnd("PartyRole", UtilMisc.toMap("partyId", leadPartyId, "roleTypeId", "LEAD"));
                    
                    // clean abandon information [end]
                    
                    result.putAll(ServiceUtil.returnSuccess("Successfully converted Lead to "+convertedRoleTypeId.toLowerCase()));
                    
                    if (UtilValidate.isNotEmpty(leadConvertType) && leadConvertType.equals("CUSTOMER")) {
                    	result.put(ModelService.RESPONSE_MESSAGE, "customer");
                    }
    			}
    		}
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	return result;
    }
	
	public static Map postConvertLead(DispatchContext dctx, Map context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	String partyId = (String) context.get("partyId");
    	String convertedRoleTypeId = (String) context.get("convertedRoleTypeId");
    	
		Map<String, Object> callResult = FastMap.newInstance();
    	
    	try {
    		result.put("partyId", partyId);
    		
    		if (UtilValidate.isNotEmpty(partyId)) {
    			
    			if (UtilValidate.isNotEmpty(convertedRoleTypeId)) {
    				Debug.logInfo("again try to update PartyRelationship for new convertedRoleTypeId# "+convertedRoleTypeId, MODULE);;
    				delegator.storeByCondition("PartyRelationship", UtilMisc.toMap("roleTypeIdFrom", convertedRoleTypeId), EntityCondition.makeCondition(EntityOperator.AND,
    						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
    						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD")));
    				
    				delegator.storeByCondition("PartyRelationship", UtilMisc.toMap("roleTypeIdTo", convertedRoleTypeId), EntityCondition.makeCondition(EntityOperator.AND,
    						EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId),
    						EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "LEAD")));
    			}
    			
                // clean abandon information [start]
                delegator.removeByAnd("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "LEAD"));
                
                // clean abandon information [end]
                result.putAll(ServiceUtil.returnSuccess());
    		}
    	} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);
    		result.putAll(ServiceUtil.returnError(e.getMessage()));
    		result.put("responseCode", "E1002");
			return result;
		}
    	return result;
    }
    
}
