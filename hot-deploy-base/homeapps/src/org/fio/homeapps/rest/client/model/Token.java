package org.fio.homeapps.rest.client.model;

import org.ofbiz.entity.GenericValue;

/**
 * @author Sharif
 *
 */
public class Token {

	private String accessToken;
	private String refreshToken;
	private String tokenType;
	private long expiresIn;
	private String scope;
	private String soapInstanceUrl;
	private String restInstanceUrl;
	
	public Token() {}
	
	public Token(GenericValue entity) {
		setAccessToken(entity.getString("accessToken"));
		setTokenType(entity.getString("tokenType"));
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getSoapInstanceUrl() {
		return soapInstanceUrl;
	}

	public void setSoapInstanceUrl(String soapInstanceUrl) {
		this.soapInstanceUrl = soapInstanceUrl;
	}

	public String getRestInstanceUrl() {
		return restInstanceUrl;
	}

	public void setRestInstanceUrl(String restInstanceUrl) {
		this.restInstanceUrl = restInstanceUrl;
	}
	
}
