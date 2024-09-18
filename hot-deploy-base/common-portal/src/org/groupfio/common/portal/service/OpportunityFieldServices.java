package org.groupfio.common.portal.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.util.PartyHelper;
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
import org.ofbiz.service.ServiceUtil;

/**
 * @author Jeevan
 *
 */
public class OpportunityFieldServices {

	private static final String MODULE = OpportunityFieldServices.class.getName();

	public static Map getOppoOwnerList(DispatchContext dctx, Map context) {
		Debug.logInfo("call getOppoOwnerList..... Start: "+UtilDateTime.nowTimestamp(), MODULE);
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		Map < String, Object > dataList = new HashMap < String, Object > ();
		try {
			List < EntityCondition > conditions = new ArrayList < EntityCondition > ();

			EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR")));
			conditions.add(roleTypeCondition);
			EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);
			conditions.add(partyStatusCondition);
			conditions.add(EntityUtil.getFilterByDateExpr());
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List < GenericValue > partyFromReln = delegator.findList("PartyFromRelnAndParty", mainConditons, null, null, null, true);
			if (partyFromReln != null && partyFromReln.size() > 0) {
				List < String > partyRelnId = null;
				partyRelnId = EntityUtil.getFieldListFromEntityList(partyFromReln, "partyIdTo", true);
				if (UtilValidate.isNotEmpty(partyRelnId)) {
					
					Map<String, Object> userLoginIds = new HashMap<>();
					PartyHelper.getUserLoginIdByPartyIds(delegator, userLoginIds, partyFromReln, "partyIdTo");
					
					Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByPartyIds(delegator, partyNames, partyFromReln, "partyIdTo");
					
					for (String partyId : partyRelnId) {
						String name = (String) partyNames.get(partyId);
						String userLoginId = (String) userLoginIds.get(partyId);
						
						if (UtilValidate.isNotEmpty(userLoginId)) {
							dataList.put(userLoginId, name);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.put("filterResult", dataList);
		result.putAll(ServiceUtil.returnSuccess("Successfully retrieve Dynamic data.."));
		Debug.logInfo("call getOppoOwnerList..... End: "+UtilDateTime.nowTimestamp(), MODULE);
		return result;
	}
	
}
