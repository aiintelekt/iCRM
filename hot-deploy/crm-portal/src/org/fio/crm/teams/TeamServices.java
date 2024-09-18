package org.fio.crm.teams;

import java.util.Locale;
import java.util.Map;

import org.fio.crm.party.PartyHelper;
import org.fio.crm.util.UtilMessage;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class TeamServices {

	private static final String MODULE = TeamServices.class.getName();
	public static Map assignTeamToAccount(DispatchContext dctx, Map context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String accountPartyId = (String) context.get("accountPartyId");
		String teamPartyId = (String) context.get("teamPartyId");

		// ensure team assign permission on this account
		/*if (!security.hasEntityPermission("CRMSFA_TEAM", "_ASSIGN", userLogin))
			return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);*/
		
		try {
			// assign the team
			PartyHelper.copyToPartyRelationships(teamPartyId, "ACCOUNT_TEAM", accountPartyId, "ACCOUNT", userLogin, delegator, dispatcher);
		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, MODULE);
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, MODULE);
		}
		return ServiceUtil.returnSuccess();
	}
}
