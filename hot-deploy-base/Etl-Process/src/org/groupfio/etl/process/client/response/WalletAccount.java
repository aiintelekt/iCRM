/**
 * 
 */
package org.groupfio.etl.process.client.response;

/**
 * @author Group Fio
 *
 */
public class WalletAccount extends Response {

	private String billingAccountId;
	private String walletAcctId;
	
	public WalletAccount () {}

	public String getBillingAccountId() {
		return billingAccountId;
	}

	public void setBillingAccountId(String billingAccountId) {
		this.billingAccountId = billingAccountId;
	}

	public String getWalletAcctId() {
		return walletAcctId;
	}

	public void setWalletAcctId(String walletAcctId) {
		this.walletAcctId = walletAcctId;
	}
	
}
