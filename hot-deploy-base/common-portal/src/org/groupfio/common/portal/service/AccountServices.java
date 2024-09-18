package org.groupfio.common.portal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class AccountServices {
	private static final String MODULE = AccountServices.class.getName();

	@SuppressWarnings("unchecked")
	public static Map addChildAccount(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map requestContext = (Map) context.get("requestContext");
		String partyId = (String) requestContext.get("partyId");
		String parentAccountId = (String) requestContext.get("parentAccountId");
		Map<String, Object> result = new HashMap<String, Object>();
		List condList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(parentAccountId)){
			try {
				condList.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,parentAccountId));
				condList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,partyId));
				condList.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"ACCOUNT"));
				condList.add(EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.EQUALS,"ACCOUNT"));
				condList.add(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"REL_PARENT_ACCOUNT"));
				EntityCondition dateCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp())), EntityOperator.OR);
				condList.add(dateCondition);
				EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
				List<GenericValue> partyRelationShipDet = delegator.findList("PartyRelationship", cond, UtilMisc.toSet("partyIdFrom"), null, null, false);
				if (UtilValidate.isEmpty(partyRelationShipDet)) {
					Map requestMap = FastMap.newInstance();
					Map callCxt = FastMap.newInstance();
					requestMap.put("partyIdTo", parentAccountId);
					requestMap.put("partyIdFrom", partyId);
					requestMap.put("roleTypeIdFrom", "ACCOUNT");
					requestMap.put("roleTypeIdTo", "ACCOUNT");
					requestMap.put("partyRelationshipTypeId", "REL_PARENT_ACCOUNT");
					requestMap.put("userLogin", userLogin);
					callCxt.put("userLogin", userLogin);
					callCxt.put("requestContext", requestMap);
					Map<String,Object> serviceResult = dispatcher.runSync("createPartyRelationship", requestMap);
					if (ServiceUtil.isError(serviceResult)) {
						result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						result.put(ModelService.ERROR_MESSAGE, serviceResult.get("errorMessage"));
						return result;
					}
				}else {
					result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					result.put(ModelService.ERROR_MESSAGE, "Parent child relation already exists");
					return result;
				}
			}catch (Exception e) {
				// TODO: handle exception
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, e.getMessage());
				return result;
			}
		}else {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, "Party details missing	");
		}
		return result;
	}

}
