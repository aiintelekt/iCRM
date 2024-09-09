/**
 * 
 */
package org.fio.homeapps.rest.client.request;

/**
 * @author Sharif
 *
 */
public class Authentication {
	
	private String grant_type;
	private String client_id;
	private String client_secret;
	private String scope;
	private String account_id;

	private Authentication (AuthenticationBuilder builder) {
		this.grant_type = builder.grant_type;
		this.client_id = builder.client_id;
		this.client_secret = builder.client_secret;
		this.scope = builder.scope;
		this.account_id = builder.account_id;
	}
	
	public Authentication grantType(String grantType) {
		this.grant_type = grantType;
		return this;
	}
	
	public Authentication clientId(String clientId) {
		this.client_id = clientId;
		return this;
	}
	
	public Authentication clientSecret(String clientSecret) {
		this.client_secret = clientSecret;
		return this;
	}
	
	public Authentication scope(String scope) {
		this.scope = scope;
		return this;
	}
	
	public Authentication accountId(String accountId) {
		this.account_id = accountId;
		return this;
	}
	
	public static class AuthenticationBuilder {
		
		private String grant_type;
		private String client_id;
		private String client_secret;
		private String scope;
		private String account_id;
		
		public AuthenticationBuilder() {}
		
		public AuthenticationBuilder(String grantType, String clientId, String clientSecret, String scope, String accountId) {
			this.grant_type = grantType;
			this.client_id = clientId;
			this.client_secret = clientSecret;
			this.scope = scope;
			this.account_id = accountId;
		}
		
		public AuthenticationBuilder setGrantType(String grantType) {
			this.grant_type = grantType;
			return this;
		}

		public AuthenticationBuilder setClientId(String clientId) {
			this.client_id = clientId;
			return this;
		}

		public AuthenticationBuilder setClientSecret(String clientSecret) {
			this.client_secret = clientSecret;
			return this;
		}

		public AuthenticationBuilder setScope(String scope) {
			this.scope = scope;
			return this;
		}

		public AuthenticationBuilder setAccountId(String accountId) {
			this.account_id = accountId;
			return this;
		}

		public Authentication build() {
			return new Authentication(this);
		}
		
	}
	
}
