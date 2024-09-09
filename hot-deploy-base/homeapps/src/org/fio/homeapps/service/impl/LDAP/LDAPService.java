package org.fio.homeapps.service.impl.LDAP;

import org.ofbiz.base.util.Debug;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class LDAPService {
	private String domain = null;
	private String emailDomain = null;
	private String ldapUrl = null;
	private String userName = null;
	private String password = null;
	private Authentication result = null;
	private String searchFilter = null;

	private String ldapRootDn;

	public static final String module = LDAPService.class.getName();

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmailDomain() {
		return this.emailDomain;
	}

	public void setEmailDomain(String emailDomain) {
		this.emailDomain = emailDomain;
	}

	public String getLdapUrl() {
		return this.ldapUrl;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Authentication ldapService() {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(this.userName,
				this.password);
		// LdapCoreAuthenticationProvider provider = new
		// LdapCoreAuthenticationProvider(this.domain, this.ldapUrl, ldapRootDn,
		// this.emailDomain);
		LdapCoreAuthenticationProvider provider = new LdapCoreAuthenticationProvider(this.domain, this.ldapUrl,
				ldapRootDn);

		// provider.setSearchFilter(this.searchFilter);

		authToken.getPrincipal();
		provider.setConvertSubErrorCodesToExceptions(true);
		Authentication result = null;
		try {
			Debug.logInfo("Invoking LDAP provider for authentication with ldapUrl:" + this.ldapUrl+", ldapRootDn: "+ldapRootDn+", userName: "+userName+", domain: "+domain, module);
			result = provider.authenticate(authToken);
			Debug.logInfo("LDAP authentication successful", module);
		} catch (Exception ex) {
			ex.printStackTrace();
			Debug.logError("Error in LDAPService-->" + ex, module);
			throw new AuthenticationServiceException(ex.getMessage());
		}
		setResult(result);

		return result;
	}

	public void init(String user, String pwd, String domain, String emailDomain, String ldapUrl, String searchFilter) {
		setDomain(domain);
		setEmailDomain(emailDomain);
		setLdapUrl(ldapUrl);
		setUserName(user);
		setPassword(pwd);
		setSearchFilter(searchFilter);
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Authentication getResult() {
		return this.result;
	}

	public void setResult(Authentication result) {
		this.result = result;
	}

	public String getSearchFilter() {
		return this.searchFilter;
	}

	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	public String getLdapRootDn() {
		return ldapRootDn;
	}

	public void setLdapRootDn(String ldapRootDn) {
		this.ldapRootDn = ldapRootDn;
	}

}
