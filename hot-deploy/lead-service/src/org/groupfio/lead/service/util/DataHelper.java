/**
 * 
 */
package org.groupfio.lead.service.util;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif
 */
public class DataHelper {
	
	private static String MODULE = DataHelper.class.getName();
	
	public static String getPartyIdByEmail(Delegator delegator, String emailId) {
		String partyId = "";
		try {
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PCM", "PartyContactMech");
			dynamicView.addAlias("PCM", "partyId");
			dynamicView.addAlias("PCM", "contactMechId");
			dynamicView.addAlias("PCM", "fromDate");
			dynamicView.addAlias("PCM", "thruDate");
			dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
			dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
			dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			dynamicView.addMemberEntity("CM", "ContactMech");
			dynamicView.addAlias("CM", "infoString");
			dynamicView.addViewLink("PCM", "CM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, emailId),
					EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL")
					);
			
			GenericValue emailAddress = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(emailAddress))
				partyId = emailAddress.getString("partyId");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return partyId;
	}
	
	public static List<GenericValue> getPrimaryContactInfoByType(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
		List<GenericValue> contactInfoList = new ArrayList<GenericValue>();
		try {
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PCM", "PartyContactMech");
			dynamicView.addAlias("PCM", "partyId");
			dynamicView.addAlias("PCM", "contactMechId");
			dynamicView.addAlias("PCM", "fromDate");
			dynamicView.addAlias("PCM", "thruDate");
			dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
			dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
			dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId)
					);
			TransactionUtil.begin();
			contactInfoList = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().queryList();
			TransactionUtil.commit();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return contactInfoList;
	}

}
