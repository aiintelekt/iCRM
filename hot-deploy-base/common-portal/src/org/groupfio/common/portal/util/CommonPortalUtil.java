/**
 * 
 */
package org.groupfio.common.portal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.fio.admin.portal.util.DataUtil;
import org.fio.homeapps.util.PartyHelper;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif
 *
 */
public class CommonPortalUtil {
	
	private static final String MODULE = CommonPortalUtil.class.getName();
	
	public static List<Map<String, Object>> getAgreementAssocParties(Delegator delegator, String agreementId) {
		List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			if (UtilValidate.isNotEmpty(agreementId)) {
				String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);
				
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, agreementId));
				
				EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				
				DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
				dynamicViewEntity.addMemberEntity("AG", "Agreement");
				dynamicViewEntity.addAlias("AG", "agreementId");
				
				dynamicViewEntity.addMemberEntity("AGR", "AgreementRole");
				dynamicViewEntity.addAlias("AGR", "partyId");
				dynamicViewEntity.addAlias("AGR", "roleTypeId");
				dynamicViewEntity.addAlias("AGR", "lastUpdatedStamp");
				dynamicViewEntity.addAlias("AGR", "lastUpdatedTxStamp");
				dynamicViewEntity.addViewLink("AG", "AGR", Boolean.FALSE, ModelKeyMap.makeKeyMapList("agreementId"));
				
				List<GenericValue> agreementPartyList = EntityQuery.use(delegator).from(dynamicViewEntity).where(condition).queryList();
				if(UtilValidate.isNotEmpty(agreementPartyList)) {
					for(GenericValue agreementParty : agreementPartyList) {
						Map<String, Object> data = new HashMap<>();
						String partyId = agreementParty.getString("partyId");
						String customerName = PartyHelper.getPartyName(delegator, partyId, false);
						
						Map<String,String> partyContactInfo = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
						String phoneNumber = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("PrimaryPhone") : "";
						String infoString = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("EmailAddress") : "";
						
						String phoneSolicitation = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("phoneSolicitation") : "";
						String emailSolicitation = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("emailSolicitation") : "";
						
						data.putAll(DataUtil.convertGenericValueToMap(delegator, agreementParty));
						data.put("phoneNumber", UtilCommon.formatPhoneNumber(phoneNumber) );
						data.put("infoString", infoString );
						data.put("name", customerName);
						data.put("phoneSolicitation", phoneSolicitation );
						data.put("emailSolicitation", emailSolicitation );
						data.put("roleTypeDesc", org.groupfio.common.portal.util.DataUtil.getRoleTypeDescription(delegator, agreementParty.getString("roleTypeId")));
						
						data.put("lastUpdatedStamp", UtilDateTime.timeStampToString(agreementParty.getTimestamp("lastUpdatedStamp"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));
						
						dataList.add(data);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return dataList;
	}
}
