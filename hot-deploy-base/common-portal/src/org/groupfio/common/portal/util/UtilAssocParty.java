/**
 * 
 */
package org.groupfio.common.portal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.util.DataUtil;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceContainer;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class UtilAssocParty {

	private static final String MODULE = UtilAssocParty.class.getName();
	
	public static Map<String, Object> initiateAssocParties(Delegator delegator, Map<String, Object> context) {
		Map<String, Object> response = new HashMap<>();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			
			String domainEntityType = (String) context.get("domainEntityType");
			String domainEntityId = (String) context.get("domainEntityId");
			
			if (UtilValidate.isNotEmpty(domainEntityType)) {
				
				if (domainEntityType.equals(DomainEntityType.REBATE)) {
					String partyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLogin.getString("userLoginId"));
					//String roleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, partyId);
					String roleTypeId = org.fio.homeapps.util.PartyHelper.getPartyRoleTypeId(partyId, delegator);
					
					Map<String, Object> input = UtilMisc.toMap("agreementId", domainEntityId, "partyId", partyId, "roleTypeId", roleTypeId);
					GenericValue entity = EntityQuery.use(delegator).from("AgreementRole")
							.where(input).queryFirst();
					if (UtilValidate.isEmpty(entity)) {
						entity = delegator.makeValue("AgreementRole", input);
						entity.create();
					}
					
					GenericValue agreement = EntityQuery.use(delegator).from("Agreement").where("agreementId", domainEntityId).queryFirst();
					if (UtilValidate.isNotEmpty(agreement)) {
						if (UtilValidate.isNotEmpty(agreement.getString("partyIdTo"))) {
							input = UtilMisc.toMap("agreementId", domainEntityId, "partyId", agreement.getString("partyIdTo"), "roleTypeId", "ACCOUNT");
							entity = EntityQuery.use(delegator).from("AgreementRole")
									.where(input).queryFirst();
							if (UtilValidate.isEmpty(entity)) {
								entity = delegator.makeValue("AgreementRole", input);
								entity.create();
							}
							
							String tsmPartyId = org.groupfio.common.portal.util.DataUtil.getPartyRelIdTo(delegator, UtilMisc.toMap("partyIdFrom", agreement.getString("partyIdTo"), "roleTypeIdFrom", "ACCOUNT", "roleTypeIdTo", "SALES_REP", "partyRelationshipTypeId", "REL_SALES_REP_PARENT"));
							if (UtilValidate.isNotEmpty(tsmPartyId)) {
								input = UtilMisc.toMap("agreementId", domainEntityId, "partyId", tsmPartyId, "roleTypeId", "SALES_REP");
								entity = EntityQuery.use(delegator).from("AgreementRole")
										.where(input).queryFirst();
								if (UtilValidate.isEmpty(entity)) {
									entity = delegator.makeValue("AgreementRole", input);
									entity.create();
								}
							}
							
							String salesManagerPartyId = UtilAttribute.getAgreementAttrValue(delegator, domainEntityId, "SM_PARTYID");
							//String salesManagerPartyId = org.groupfio.common.portal.util.DataUtil.getPartyRelIdTo(delegator, UtilMisc.toMap("partyIdFrom", agreement.getString("partyIdTo"), "roleTypeIdFrom", "ACCOUNT", "roleTypeIdTo", "SALES_REP_MANAGER", "partyRelationshipTypeId", "REL_SALES_MGR_PARENT"));
							if (UtilValidate.isNotEmpty(salesManagerPartyId)) {
								if (!DataUtil.isPartyRoleExists(delegator, salesManagerPartyId, "SALES_REP_MANAGER_PRI")) {
									GenericValue partyRole = delegator.makeValue("PartyRole");
									partyRole.put("partyId", salesManagerPartyId);
									partyRole.put("roleTypeId", "SALES_REP_MANAGER_PRI");
									delegator.createOrStore(partyRole);
								}
								
								//roleTypeId = org.fio.homeapps.util.PartyHelper.getPartyRoleTypeId(salesManagerPartyId, delegator);
								input = UtilMisc.toMap("agreementId", domainEntityId, "partyId", salesManagerPartyId, "roleTypeId", "SALES_REP_MANAGER_PRI");
								entity = EntityQuery.use(delegator).from("AgreementRole")
										.where(input).queryFirst();
								if (UtilValidate.isEmpty(entity)) {
									entity = delegator.makeValue("AgreementRole", input);
									entity.create();
								}
							}
							
						}
						
						String payoutInitiateRoles = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "RBT_PYT_INT_ROLES");
						if (UtilValidate.isNotEmpty(payoutInitiateRoles)) {
							List<String> payoutRoles = Arrays.asList(payoutInitiateRoles.split(","));
							for (String payoutRole : payoutRoles) {
								List<String> partyIds = DataUtil.getPartyIds(delegator, payoutRole);
								for (String ptyId : partyIds) {
									input = UtilMisc.toMap("agreementId", domainEntityId, "partyId", ptyId, "roleTypeId", payoutRole);
									entity = EntityQuery.use(delegator).from("AgreementRole")
											.where(input).queryFirst();
									if (UtilValidate.isEmpty(entity)) {
										entity = delegator.makeValue("AgreementRole", input);
										entity.create();
									}
								}
							}
						}
						
						GenericValue attr = EntityQuery.use(delegator).from("AgreementAttribute").where("agreementId", domainEntityId, "attrName", "APV_TPL_ID").cache(false).queryFirst();
						if (UtilValidate.isNotEmpty(attr)) {
							String approvalTemplateId = attr.getString("attrValue");
							if (UtilValidate.isNotEmpty(approvalTemplateId)) {
								List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				    			conditions.add(EntityCondition.makeCondition("parentWorkEffortId", EntityOperator.EQUALS, approvalTemplateId));
				    			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, null));
				    			conditions.add(EntityUtil.getFilterByDateExpr("startDate", "endDate"));
				    			EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				            	
				            	List<GenericValue> templateItems = EntityQuery.use(delegator).select("partyId", "roleTypeId").from("ApprovalAndWorkEffort").where(mainConditon).orderBy("accessLevel").queryList();
								if (UtilValidate.isNotEmpty(templateItems)) {
									for (GenericValue templateItem : templateItems) {
										input = UtilMisc.toMap("agreementId", domainEntityId, "partyId", templateItem.getString("partyId"), "roleTypeId", templateItem.getString("roleTypeId"));
										entity = EntityQuery.use(delegator).from("AgreementRole")
												.where(input).queryFirst();
										if (UtilValidate.isEmpty(entity)) {
											entity = delegator.makeValue("AgreementRole", input);
											entity.create();
										}
									}
								}
							}
						}
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return response;
	}
	
	public static String getPrimaryContactId(Delegator delegator, Map<String, Object> context) {
		LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(), delegator);
		String contactId = "";
		try {
			Map<String, Object> serResult = dispatcher.runSync("common.getContactAndPartyAssoc", context);
			
			if (UtilValidate.isNotEmpty(serResult) && ServiceUtil.isSuccess(serResult)) {
				List<Object> primaryContactList = FastList.newInstance();
				
				primaryContactList = (List<Object>) serResult.get("partyContactAssoc");
				
				for (int i = 0; i < primaryContactList.size(); i++) {
					Map<String, Object> partyContactMap = new HashMap<String, Object>();
					partyContactMap = (Map<String, Object>) primaryContactList.get(i);
					
					String primaryContactStatusId = (String) partyContactMap.get("statusId");
					
					if ("PARTY_DEFAULT".equals(primaryContactStatusId)) {
						contactId = (String) partyContactMap.get("contactId");
						break;
					}
				}
				
				if(UtilValidate.isEmpty(contactId)) {
					Map<String, Object> partyContactMap = (Map<String, Object>) primaryContactList.get(0);
					contactId = (String) partyContactMap.get("contactId");
				}		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contactId;
	}
	
}
