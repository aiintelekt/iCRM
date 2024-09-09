package org.fio.crm.marketing;

import java.util.Locale;
import java.util.Map;

import org.fio.crm.util.LoginFilterUtil;
import org.fio.crm.util.UtilMessage;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class MarketingCampaignServices {
	private static final String MODULE = MarketingCampaignServices.class.getName();
    public static final String errorResource = "CRMSFAErrorLabels";
    
    public static Map<String, Object> addAccountMarketingCampaign(DispatchContext dctx, Map<String, Object> context) {
        return addMarketingCampaignWithPermission(dctx, context, "CRMSFA_ACCOUNT", "_UPDATE", "ACCOUNT");
    }

    public static Map<String, Object> addContactMarketingCampaign(DispatchContext dctx, Map<String, Object> context) {
        return addMarketingCampaignWithPermission(dctx, context, "CRMSFA_CONTACT", "_UPDATE", "CONTACT");
    }

    public static Map<String, Object> addLeadMarketingCampaign(DispatchContext dctx, Map<String, Object> context) {
        return addMarketingCampaignWithPermission(dctx, context, "CRMSFA_LEAD", "_UPDATE", "LEAD");
    }
    
    public static Map<String, Object> addMarketingCampaign(DispatchContext dctx, Map<String, Object> context) {
        return addMarketingCampaignWithPermission(dctx, context, "CRMSFA_LEAD", "_UPDATE", (String) context.get("roleTypeId"));
    }

    /**
     * Parametrized service to add a marketing campaign to a party. Pass in the security to check.
     */
    private static Map<String, Object> addMarketingCampaignWithPermission(DispatchContext dctx, Map<String, Object> context, String module, String operation, String roleTypeId) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String partyId = (String) context.get("partyId");
        String marketingCampaignId = (String) context.get("marketingCampaignId");

        // check parametrized security
        String userLoginId = userLogin.getString("partyId");
        if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId) && !security.hasEntityPermission(module, operation, userLogin)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
        }
        try {
            // create the MarketingCampaignRole to relate the optional marketing campaign to this party as the system user
            GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"),true);
            Map<String, Object> serviceResults = dispatcher.runSync("createMarketingCampaignRole",
                    UtilMisc.toMap("partyId", partyId , "roleTypeId", roleTypeId, "marketingCampaignId", marketingCampaignId, "userLogin", system));
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorAddMarketingCampaign", locale, MODULE);
            }
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAddMarketingCampaign", locale, MODULE);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAddMarketingCampaign", locale, MODULE);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> removeAccountMarketingCampaign(DispatchContext dctx, Map<String, Object> context) {
        return removeMarketingCampaignWithPermission(dctx, context, "CRMSFA_ACCOUNT", "_UPDATE", "ACCOUNT");
    }

    public static Map<String, Object> removeContactMarketingCampaign(DispatchContext dctx, Map<String, Object> context) {
        return removeMarketingCampaignWithPermission(dctx, context, "CRMSFA_CONTACT", "_UPDATE", "CONTACT");
    }

    public static Map<String, Object> removeLeadMarketingCampaign(DispatchContext dctx, Map<String, Object> context) {
        return removeMarketingCampaignWithPermission(dctx, context, "CRMSFA_LEAD", "_UPDATE", "LEAD");
    }
    
    public static Map<String, Object> removeMarketingCampaign(DispatchContext dctx, Map<String, Object> context) {
        return removeMarketingCampaignWithPermission(dctx, context, "CRMSFA_LEAD", "_UPDATE", (String) context.get("roleTypeId"));
    }

    /**
     * Parametrized method to remove a marketing campaign from a party. Pass in the security to check.
     */
    private static Map<String, Object> removeMarketingCampaignWithPermission(DispatchContext dctx, Map<String, Object> context, String module, String operation, String roleTypeId) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String partyId = (String) context.get("partyId");
        String marketingCampaignId = (String) context.get("marketingCampaignId");
        String userLoginId = userLogin.getString("partyId");
        // check parametrized security
        if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId) && !security.hasEntityPermission(module, operation, userLogin)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
        }
        try {
            // just remove the MarketingCampaignRole as the system user
            GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"),true);
            Map<String, Object> serviceResults = dispatcher.runSync("deleteMarketingCampaignRole",
                    UtilMisc.toMap("partyId", partyId, "marketingCampaignId", marketingCampaignId, "roleTypeId", roleTypeId, "userLogin", system));
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorRemoveMarketingCampaign", locale, MODULE);
            }
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorRemoveMarketingCampaign", locale, MODULE);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorRemoveMarketingCampaign", locale, MODULE);
        }

        return ServiceUtil.returnSuccess();
    }
}
