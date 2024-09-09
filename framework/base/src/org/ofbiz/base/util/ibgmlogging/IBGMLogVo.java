package org.ofbiz.base.util.ibgmlogging;

public class IBGMLogVo {

	private String appName;
	private String contextPath;
	private String apiName;
	private String partyId;
	private String walletAcctId;
	private String requestDateTime;
	private String clientRecordRefId;
	private String responseCode;
	private String responseDesc;
	private String walletMasterAcctId;
	private String walletOperatingAcctId;
	
	@Override
	public String toString () {
		return "IBGMLogMonitor [appName=" + appName + ", contextPath=" + contextPath + ", apiName=" + apiName + ", partyId=" + partyId + ", walletAcctId=" + walletAcctId + ", requestDateTime=" + requestDateTime + ", clientRecordRefId=" + clientRecordRefId + ", responseCode=" + responseCode + ", responseDesc=" + responseDesc + ", walletMasterAcctId=" + walletMasterAcctId + ", walletOperatingAcctId=" + walletOperatingAcctId + "]";
	}

	public IBGMLogVo (String appName, String contextPath, String apiName, String partyId, String walletAcctId, String requestDateTime, String clientRecordRefId, String responseCode, String responseDesc, String walletMasterAcctId, String walletOperatingAcctId) {
		this.appName = appName;
		this.contextPath = contextPath;
		this.apiName = apiName;
		this.requestDateTime = DateFormatUtils.getSystemDateWithTimeStamp (DateFormatUtils.DATEFORMATyyyyMMddHHmmss);
		this.partyId = "partyId";
		this.walletAcctId = walletAcctId;
		this.clientRecordRefId = clientRecordRefId;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
		this.walletMasterAcctId = walletMasterAcctId;
		this.walletOperatingAcctId = walletOperatingAcctId;

	}

	public IBGMLogVo () {

	}

	public String getAppName () {
		return appName;
	}

	public void setAppName (String appName) {
		this.appName = appName;
	}

	public String getContextPath () {
		return contextPath;
	}

	public void setContextPath (String contextPath) {
		this.contextPath = contextPath;
	}

	public String getApiName () {
		return apiName;
	}

	public void setApiName (String apiName) {
		this.apiName = apiName;
	}

	public String getPartyId () {
		return partyId;
	}

	public void setPartyId (String partyId) {
		this.partyId = partyId;
	}

	public String getWalletAcctId () {
		return walletAcctId;
	}

	public void setWalletAcctId (String walletAcctId) {
		this.walletAcctId = walletAcctId;
	}

	public String getRequestDateTime () {
		return requestDateTime;
	}

	public void setRequestDateTime (String requestDateTime) {
		this.requestDateTime = requestDateTime;
	}

	public String getClientRecordRefId () {
		return clientRecordRefId;
	}

	public void setClientRecordRefId (String clientRecordRefId) {
		this.clientRecordRefId = clientRecordRefId;
	}

	public String getResponseCode () {
		return responseCode;
	}

	public void setResponseCode (String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseDesc () {
		return responseDesc;
	}

	public void setResponseDesc (String responseDesc) {
		this.responseDesc = responseDesc;
	}

	public String getWalletMasterAcctId () {
		return walletMasterAcctId;
	}

	public void setWalletMasterAcctId (String walletMasterAcctId) {
		this.walletMasterAcctId = walletMasterAcctId;
	}

	public String getWalletOperatingAcctId () {
		return walletOperatingAcctId;
	}

	public void setWalletOperatingAcctId (String walletOperatingAcctId) {
		this.walletOperatingAcctId = walletOperatingAcctId;
	}


}
