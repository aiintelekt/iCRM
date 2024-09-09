package org.fio.homeapps.service.impl.LDAP;

import org.springframework.security.core.AuthenticationException;

public final class LDAPAuthenticationException extends AuthenticationException {
	private static final long serialVersionUID = -6686796785113447612L;
	private final String dataCode;
	
	LDAPAuthenticationException(String dataCode, String message, Throwable cause) {
	  super(message, cause);
	  this.dataCode = dataCode;
	}
	
	
	public String getDataCode() 
	{ 
		return this.dataCode; 
	}
}
