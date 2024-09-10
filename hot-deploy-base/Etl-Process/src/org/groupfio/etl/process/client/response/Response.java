/**
 * 
 */
package org.groupfio.etl.process.client.response;

/**
 * @author Group Fio
 *
 */
public abstract class Response {

	protected String responseCode;
	private String responseRefId;
	
	public String getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getResponseRefId() {
		return responseRefId;
	}
	
	public void setResponseRefId(String responseRefId) {
		this.responseRefId = responseRefId;
	}
	
}
