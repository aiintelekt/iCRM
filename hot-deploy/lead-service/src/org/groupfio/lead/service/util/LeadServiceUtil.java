/**
 * 
 */
package org.groupfio.lead.service.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.lead.service.LeadServiceConstants.EmailVerifyStatus;
import org.groupfio.lead.service.util.DataHelper;
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
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class LeadServiceUtil {
	
	private static final String MODULE = LeadServiceUtil.class.getName();
	
	public static Map<String, Object> storeLeadEvent(Map<String, Object> context) {
        LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
        DispatchContext dctx = (DispatchContext) dispatcher.getDispatchContext();
        Delegator delegator = (Delegator) dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		String agreementId = (String) context.get("agreementId");
		String partyId = (String) context.get("partyId");
		String eventTypeId = (String) context.get("eventTypeId");
		String ipAddress = (String) context.get("ipAddress");
		String createdByEmailAddress = (String) context.get("createdByEmailAddress");
		String createdByUserLogin = (String) context.get("createdByUserLogin");
		String description = (String) context.get("description");
		
		Timestamp eventDate = UtilDateTime.nowTimestamp();
		
		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> requestCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			if (UtilValidate.isNotEmpty(userLogin) && UtilValidate.isEmpty(createdByUserLogin)) {
				createdByUserLogin = userLogin.getString("userLoginId");
			}
			if (UtilValidate.isEmpty(createdByEmailAddress) && UtilValidate.isNotEmpty(userLogin)) {
				createdByEmailAddress = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, userLogin.getString("partyId"), "PRIMARY_EMAIL");
			}
			
			requestCtxt.put("userLogin", userLogin);
			requestCtxt.put("agreementId", agreementId);
			requestCtxt.put("eventTypeId", eventTypeId);
			requestCtxt.put("eventDate", eventDate);
			requestCtxt.put("createdByUserLogin", createdByUserLogin);
			requestCtxt.put("ipAddress", ipAddress);
			requestCtxt.put("createdByEmailAddress", createdByEmailAddress);
			requestCtxt.put("description", description);
			requestCtxt.put("partyId", partyId);
			callCtxt.put("requestContext", requestCtxt);
			callCtxt.put("userLogin", userLogin);
			
            callResult = dispatcher.runSync("lead.createLeadEventHistory", callCtxt);
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.putAll(ServiceUtil.returnSuccess("Successfully Created Lead Event History.."));
		return result;
	}
	
	public static boolean diableLeadByContact(String contactPartyId, Delegator delegator) {
		return updateLeadStatusByContact(contactPartyId, "PARTY_DISABLED", delegator);
	}
	
	public static boolean enableLeadByContact(String contactPartyId, Delegator delegator) {
		return updateLeadStatusByContact(contactPartyId, "PARTY_ENABLED", delegator);
	}
	
	public static boolean updateLeadStatusByContact(String contactPartyId,String statusId, Delegator delegator) {
		return updatePartyStatus(getLeadPartyIdByContact(contactPartyId, delegator),statusId,delegator);
	}
	
	public static boolean updatePartyStatus(String partyId,String statusId, Delegator delegator) {
		try {
			GenericValue party =  EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryFirst();
			if(UtilValidate.isNotEmpty(party)) {
				party.set("statusId", statusId);
				party.store();
			}
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String getLeadPartyIdByContact(String contactPartyId, Delegator delegator) {
		try {
			List < EntityCondition > conditions = new ArrayList < EntityCondition > ();
			conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, contactPartyId));
			EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
					EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "LEAD"),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV")
					));
			conditions.add(roleTypeCondition);
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> partyRelList = delegator.findList("PartyFromRelnAndParty", mainConditons, null, null, null, false);
			if(UtilValidate.isNotEmpty(partyRelList)) 
				return EntityUtil.getFirst(partyRelList).getString("partyIdTo");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isEmailAlreadyExistsAndEditable(String emailId, Delegator delegator) {
		boolean emailAlreadyExistsAndEditable = false;
		try {
			GenericValue contactMech = EntityQuery.use(delegator).from("ContactMech").where("infoString", emailId).queryFirst();
			if(UtilValidate.isNotEmpty(contactMech)) {

				//take the related party using email
				String contactPartyId = DataHelper.getPartyIdByEmail(delegator, emailId);
				if(UtilValidate.isNotEmpty(contactPartyId)) {
					//check whether the party is a contact of lead and which is disabled and otp in sent status
					String leadPartyId = LeadServiceUtil.getLeadPartyIdByContact(contactPartyId, delegator);
					if(UtilValidate.isNotEmpty(leadPartyId)) {
						GenericValue leadParty = EntityQuery.use(delegator).from("Party").where("partyId", leadPartyId).queryOne();
						if(UtilValidate.isNotEmpty(leadParty)) {
							if("PARTY_DISABLED".equals(leadParty.getString("statusId"))){
								GenericValue leadOtp = EntityQuery.use(delegator).from("SecurityTracking").where("statusId", EmailVerifyStatus.VERIFIED,"partyId", contactPartyId, "trackingTypeId","EMAIL_OTP").filterByDate().queryFirst();
								//Now the email already exist and it is not verified, so can use it to update.
								if(UtilValidate.isEmpty(leadOtp)) {
									emailAlreadyExistsAndEditable = true;
								}
							}
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return emailAlreadyExistsAndEditable;
	}

	public static boolean isEmailExist(String emailId, Delegator delegator) {
		boolean isEmailExist = false; 
		try{
			if(UtilValidate.isNotEmpty(EntityQuery.use(delegator).from("ContactMech").where("infoString", emailId).queryFirst()))
				isEmailExist = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return isEmailExist;
	}
}
