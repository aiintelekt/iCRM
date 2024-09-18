/**
 * 
 */
package org.groupfio.etl.process.client.response;

/**
 * @author Group Fio
 *
 */
public class Party extends Response {

	private String partyStatus;
	private String externalAppPartyRef;
	private String partyId;
	
	public Party() {}

	public String getPartyStatus() {
		return partyStatus;
	}

	public void setPartyStatus(String partyStatus) {
		this.partyStatus = partyStatus;
	}

	public String getExternalAppPartyRef() {
		return externalAppPartyRef;
	}

	public void setExternalAppPartyRef(String externalAppPartyRef) {
		this.externalAppPartyRef = externalAppPartyRef;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	
}
