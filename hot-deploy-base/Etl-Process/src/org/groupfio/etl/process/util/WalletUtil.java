package org.groupfio.etl.process.util;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Group Fio
 *
 */
public class WalletUtil {
	
	private static final String MODULE = WalletUtil.class.getName();
	
	public static GenericValue getActiveWalletAccount (Delegator delegator, String externalAccountId) {
		
		try {
			EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("externalAccountId", EntityOperator.EQUALS, externalAccountId),
					
					EntityCondition.makeConditionDate("fromDate", "thruDate")
					);
			
			GenericValue billingAccount = EntityUtil.getFirst( delegator.findList("BillingAccount", conditions, null, null, null, false) );
			
			if (UtilValidate.isNotEmpty(billingAccount)) {
				GenericValue billingAccountRole = WalletUtil.getActiveWalletAccountRole(delegator, billingAccount.getString("billingAccountId"));
				if (UtilValidate.isNotEmpty(billingAccountRole)) {
					return billingAccount;
				}
			}
			
		} catch (Exception e) {
			Debug.logError("Exception in getActiveWalletAccount"+e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static GenericValue getActiveWalletAccountRole (Delegator delegator, String billingAccountId) {
		
		try {
			EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("billingAccountId", EntityOperator.EQUALS, billingAccountId),
					
					EntityCondition.makeConditionDate("fromDate", "thruDate")
					);
			
			GenericValue billingAccountRole = EntityUtil.getFirst( delegator.findList("BillingAccountRole", conditions, null, null, null, false) );
			return billingAccountRole;
		} catch (Exception e) {
			Debug.logError("Exception in getActiveWalletAccountRole"+e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getExternalAccountId (Delegator delegator, String partyId) {
		
		try {
			EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeConditionDate("fromDate", "thruDate")
					);
			
			GenericValue billingAccountRole = EntityUtil.getFirst( delegator.findList("BillingAccountRole", conditions, null, null, null, false) );
			
			if (UtilValidate.isNotEmpty(billingAccountRole)) {
				GenericValue billingAccount = delegator.findOne("BillingAccount", UtilMisc.toMap("billingAccountId", billingAccountRole.getString("billingAccountId")), false);
				return billingAccount.getString("externalAccountId");
			}
			
		} catch (Exception e) {
			Debug.logError("Exception in getExternalAccountId"+e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static List<GenericValue> getAllActiveOperatorWalletAccounts (Delegator delegator, String billingAccountId) {
		
		List<GenericValue> walletAccounts = new ArrayList<GenericValue>();
		
		try {
			EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("parentBillingAccountId", EntityOperator.EQUALS, billingAccountId),
					
					EntityCondition.makeConditionDate("fromDate", "thruDate")
					);
			
			List<GenericValue> roles = delegator.findList("BillingAccountRole", conditions, null, null, null, false);
			if (UtilValidate.isNotEmpty(roles)) {
				for (GenericValue role : roles) {
					
					GenericValue walletAccount = delegator.findOne("BillingAccount", UtilMisc.toMap("billingAccountId", role.getString("billingAccountId")), false);
					walletAccounts.add(walletAccount);
					
				}
			}
			
		} catch (Exception e) {
			Debug.logError("Exception in getAllActiveOperatorWalletAccounts"+e.getMessage(), MODULE);
		}
		return walletAccounts;
	}
	
	public static GenericValue getParentActiveWalletAccount (Delegator delegator, String operatorPartyId) {
		
		try {
			
			GenericValue partyRelationship = EntityUtil.getFirst( delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", operatorPartyId, "roleTypeIdTo", "OPERATING_ACCT_OWNER", "partyRelationshipTypeId", "WALLET_RELATION"), null, false) );
			
			if (UtilValidate.isNotEmpty(partyRelationship)) {
				GenericValue parentBillingAccountRole = EntityUtil.getFirst( delegator.findByAnd("BillingAccountRole", UtilMisc.toMap("partyId", partyRelationship.getString("partyIdFrom"), "roleTypeId", "MASTER_ACCT_OWNER"), null, false) );
				if (UtilValidate.isNotEmpty(parentBillingAccountRole)) {
					GenericValue billingAccount = delegator.findOne("BillingAccount", UtilMisc.toMap("billingAccountId", parentBillingAccountRole.getString("billingAccountId")), false);
					return WalletUtil.getActiveWalletAccount(delegator, billingAccount.getString("externalAccountId"));
				}
			}
			
		} catch (Exception e) {
			Debug.logError("Exception in getParentActiveWalletAccount"+e.getMessage(), MODULE);
		}
		return null;
	}
}
