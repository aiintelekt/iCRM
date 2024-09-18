package org.groupfio.crm.service.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/***
 * 
 * @author Mahendran Thanasekaran
 * @since 22-05-2019
 *
 */
public class CommonServicesImpl {
	
	private static final String MODULE = CommonServicesImpl.class.getName();
	
	public static Map<String, Object> createCustomer(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> results = new HashMap<String, Object>();
		
		try {
			String externalPartyId = (String) context.get("customerId");
			// automatically set the parameters
            ModelService createPartyGroup = dctx.getModelService("createPartyGroup");
            Map<String, Object> input = createPartyGroup.makeValid(context, ModelService.IN_PARAM);
			Map<String, Object> serviceResults = dispatcher.runSync("createPartyGroup", input);
			if (ServiceUtil.isError(serviceResults)) {
				return serviceResults;
			}
			String partyId = (String) serviceResults.get("partyId");

			// create a PartyRole
			ModelService createPartyRole = dctx.getModelService("createPartyRole");
            input = createPartyRole.makeValid(context, ModelService.IN_PARAM);
            input.put("partyId", partyId);
			serviceResults = dispatcher.runSync("createPartyRole", input);

			if (ServiceUtil.isError(serviceResults)) {
				return serviceResults;
			}
			
			GenericValue partyIdentification = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", partyId));
			partyIdentification.put("partyIdentificationTypeId", context.get("partyIdentificationTypeId") != null ? context.get("partyIdentificationTypeId") :"LCIN");
			partyIdentification.put("idValue", externalPartyId);
			partyIdentification.create();
			results = ServiceUtil.returnSuccess("Customer has been created successfully.");
			results.put("partyId", partyId);
		} catch (Exception e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		return results;
	}
	
	public static Map<String, Object> getCINFromParty(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		Map<String, Object> result = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				GenericValue partyIdentification = EntityQuery.use(delegator).from("PartyIdentification").where("partyId", partyId, "partyIdentificationTypeId", "CIN").queryOne();
				if (UtilValidate.isNotEmpty(partyIdentification)) {
					result.put("CIN", partyIdentification.getString("idValue"));
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError("Unable to find CIN", MODULE);
			return ServiceUtil.returnError("Unable to find CIN");
		}
		return result;
	}

}
