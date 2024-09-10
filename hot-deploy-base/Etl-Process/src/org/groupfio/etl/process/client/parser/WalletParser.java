/**
 * 
 */
package org.groupfio.etl.process.client.parser;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.client.response.WalletAccount;
import org.json.simple.JSONObject;
import org.ofbiz.base.util.UtilValidate;

/**
 * @author Group Fio
 *
 */
public class WalletParser {

	public static WalletAccount parseWalletAccount(JSONObject response) {
		
		WalletAccount walletAccount = new WalletAccount();
		
		try {
		
			if (UtilValidate.isEmpty(response)) {
				return walletAccount;
			}
		
			String billingAccountId = ParamUtil.getString(response, "billingAccountId");
			String walletAcctId = ParamUtil.getString(response, "walletAcctId");
			
			String responseCode = ParamUtil.getString(response, "responseCode");
			String responseRefId = ParamUtil.getString(response, "responseRefId");
			
			walletAccount.setBillingAccountId(billingAccountId);
			walletAccount.setWalletAcctId(walletAcctId);
			
			walletAccount.setResponseCode(responseCode);
			walletAccount.setResponseRefId(responseRefId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return walletAccount;
		
	}
	
}
