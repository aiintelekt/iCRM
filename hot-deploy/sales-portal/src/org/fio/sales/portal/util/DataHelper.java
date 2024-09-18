/**
 * 
 */
package org.fio.sales.portal.util;

import java.util.List;

import org.fio.homeapps.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class DataHelper {
	private static final String MODULE = DataHelper.class.getName();
	public static String prepareEmailContent(String emailContent) {
		try {
			if (UtilValidate.isNotEmpty(emailContent)) {
				String tempContent = new String(emailContent);
				tempContent = tempContent.replaceAll("â¢", "™");
				tempContent = tempContent.replaceAll("â", "'");
				tempContent = tempContent.replaceAll("â€", "‘");
				tempContent = tempContent.replaceAll("â€¦", "…");
				tempContent = tempContent.replaceAll("â€™", "’");
				tempContent = tempContent.replaceAll("â€ś", "\"");
				tempContent = tempContent.replaceAll("â€¨", "—");
				tempContent = tempContent.replaceAll("â€ł", "″");
				tempContent = tempContent.replaceAll("â€Ž", "");
				tempContent = tempContent.replaceAll("â€‚", "");
				tempContent = tempContent.replaceAll("â€‰", "");
				tempContent = tempContent.replaceAll("â€‹", "");
				tempContent = tempContent.replaceAll("â€", "");
				tempContent = tempContent.replaceAll("â€s'", "");  
				tempContent = tempContent.replaceAll("â€œ", "\"");  
				tempContent = tempContent.replaceAll("Â", "");  
				tempContent = tempContent.replaceAll("â€“", "-");  
				tempContent = tempContent.replaceAll("â€", "\"");  
				tempContent = tempContent.replaceAll("â¬", "€");  
				return tempContent;
			}
		} catch (Exception e) {
		}
		return emailContent;
	}
	
	public static void createCustRequestParty( Delegator delegator, String custRequestId, String partyId, String roleTypeId) {
    	try {
    		GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", roleTypeId,"partyId",partyId).queryFirst();
    		if(UtilValidate.isEmpty(custRequestParty)) {
    			if(UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(roleTypeId)) {
    				custRequestParty = delegator.makeValue("CustRequestParty");
    				custRequestParty.set("custRequestId", custRequestId);
    				custRequestParty.set("partyId", partyId);
    				custRequestParty.set("roleTypeId", roleTypeId);
    				custRequestParty.set("fromDate", UtilDateTime.nowTimestamp());
    				custRequestParty.create();
    			}
    		} else {
    			custRequestParty.set("lastUpdatedTxStamp", UtilDateTime.nowTimestamp());
    			custRequestParty.set("thruDate", null);
				custRequestParty.store();
    		}
    	} catch (Exception e) {
			
		}
    }
	public static void updatePhoneCallStatusActivities(String workEffortId,String callStatus,Delegator delegator) {
		List<EntityCondition> partyContactConditionList = FastList.newInstance();
		partyContactConditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
		partyContactConditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
		EntityCondition partyContactCondition = EntityCondition.makeCondition( partyContactConditionList,EntityOperator.AND);
		try {
			GenericValue partyContactGv =EntityQuery.use(delegator).select("partyId").from("WorkEffortContact").where(partyContactCondition).queryFirst();

			if(UtilValidate.isNotEmpty(partyContactGv)) {
				String contactPartyId = partyContactGv.getString("partyId");
				List<EntityCondition> contactMechConditionList = FastList.newInstance();
				contactMechConditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,contactPartyId));
				contactMechConditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId",EntityOperator.EQUALS,"PRIMARY_PHONE"));
				EntityCondition contactMechMainCondition = EntityCondition.makeCondition(contactMechConditionList ,EntityOperator.AND);
				GenericValue contactMechList =EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose").where(contactMechMainCondition).queryFirst();
				if(UtilValidate.isNotEmpty(contactMechList) && callStatus.equals("DND")) {

					String contactMechId = contactMechList.getString("contactMechId");
					delegator.storeByCondition("PartyContactMech", UtilMisc.toMap("allowSolicitation", "N"), EntityCondition.makeCondition(EntityCondition.makeCondition("contactMechId",EntityOperator.EQUALS,contactMechId)));
					delegator.storeByCondition("TelecomNumber", UtilMisc.toMap("dndStatus", "Y"), EntityCondition.makeCondition("contactMechId",EntityOperator.EQUALS,contactMechId));

				}else if(callStatus.equals("PHONE_ENGAGED")) {

					String phoneCallValue = DataUtil.getGlobalValue(delegator, "PHONE_CALL_ENGAGEMENT");

					if(UtilValidate.isNotEmpty(phoneCallValue)) {
						List<String> activityTypes = UtilMisc.toList(phoneCallValue);
						if(phoneCallValue.contains(",")) {
							activityTypes = org.fio.admin.portal.util.DataUtil.stringToList(phoneCallValue, ",");
						}
						for(String callValueCustomField : activityTypes) {
							GenericValue customFieldParty = delegator.makeValue("CustomFieldPartyClassification",UtilMisc.toMap("groupId", "CHANNELS_OF_ENGAGEMENT","customFieldId", callValueCustomField,"partyId", contactPartyId,"inceptionDate", UtilDateTime.nowTimestamp()));
							delegator.createOrStore(customFieldParty);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Problem While associating phone call status with contact " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
	}
}
