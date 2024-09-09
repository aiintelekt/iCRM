package org.fio.homeapps.service.impl.LDAP;

import java.util.HashMap;
import java.util.Map;

import org.fio.homeapps.constants.GlobalConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.springframework.security.core.Authentication;

/**
 * 
 * @author rajasekar 
 * Generic Services for LDAP User Security Group This Service
 * fetches the security Groups from both LDAP and iTrust.
 * 
 */

public class LDAPSecurityGroupServices {

	private static String MODULE = LDAPSecurityGroupServices.class.getName();

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getLDAPSecurityGroups(DispatchContext dctx, Map<String, ?> context) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		Delegator delegator = (Delegator) dctx.getDelegator();

		String username = (String) context.get("username");
		String password = (String) context.get("password");
		Debug.logInfo("Checking LDAPS for User: " + username, "user");

		String ldapRootDn = EntityUtilProperties.getPropertyValue("ldap", "ldapRootDn", delegator);

		String domainreg1 = EntityUtilProperties.getPropertyValue("ldap", "domainreg1", delegator);
		String emailDomainreg1 = EntityUtilProperties.getPropertyValue("ldap", "emailDomainreg1", delegator);
		String urlPrimaryreg1 = EntityUtilProperties.getPropertyValue("ldap", "urlPrimaryreg1", delegator);
		String urlSecondaryreg1 = EntityUtilProperties.getPropertyValue("ldap", "urlSecondaryreg1", delegator);

		String domainreg3 = EntityUtilProperties.getPropertyValue("ldap", "domainreg3", delegator);
		String emailDomainreg3 = EntityUtilProperties.getPropertyValue("ldap", "emailDomainreg3", delegator);
		String urlPrimaryreg3 = EntityUtilProperties.getPropertyValue("ldap", "urlPrimaryreg3", delegator);
		String urlSecondaryreg3 = EntityUtilProperties.getPropertyValue("ldap", "urlSecondaryreg3", delegator);
		/*
		if (UtilValidate.isEmpty(domainreg1) || UtilValidate.isEmpty(emailDomainreg1)
				&& (UtilValidate.isEmpty(urlPrimaryreg1) && UtilValidate.isEmpty(urlSecondaryreg1))) {
			resultMap.put("message", "Configuration Invalid");
			resultMap.put("status", "ERROR");
			return resultMap;
		}
		*/
		Map<String, Object> ldapResponse = new HashMap<String, Object>();

		ldapResponse = validateLDAP(username, password, domainreg1, emailDomainreg1, urlPrimaryreg1, ldapRootDn);

		if (GlobalConstants.ERROR.equalsIgnoreCase((String) ldapResponse.get("status"))) {
			if (!GlobalConstants.INVALID_USERNAME_OR_PASSWORD.equalsIgnoreCase((String) ldapResponse.get("errorMessage"))) {
				ldapResponse = validateLDAP(username, password, domainreg1, emailDomainreg1, urlSecondaryreg1,
						ldapRootDn);

				Debug.logInfo("status_secondaryreg1" + ldapResponse.toString(), "status_secondaryreg1");
			}
		}
		if (GlobalConstants.ERROR.equalsIgnoreCase((String) ldapResponse.get("status")) && GlobalConstants.INVALID_USERNAME_OR_PASSWORD.equalsIgnoreCase((String) ldapResponse.get("errorMessage"))) {
			if (UtilValidate.isEmpty(domainreg3) || UtilValidate.isEmpty(emailDomainreg3)
					|| (UtilValidate.isEmpty(urlPrimaryreg3) && UtilValidate.isEmpty(urlSecondaryreg3))) {
				return ServiceUtil.returnError("Invalid LDAP Configuration");
			}

			ldapResponse = validateLDAP(username, password, domainreg3, emailDomainreg3, urlPrimaryreg3, ldapRootDn);

			Debug.logInfo("status_primaryreg3" + ldapResponse.toString(), "status_primaryreg3");
			if (GlobalConstants.ERROR.equalsIgnoreCase((String) ldapResponse.get("status"))) {
				if (!GlobalConstants.INVALID_USERNAME_OR_PASSWORD.equalsIgnoreCase((String) ldapResponse.get("errorMessage"))) {
					ldapResponse = validateLDAP(username, password, domainreg3, emailDomainreg3, urlSecondaryreg3,
							ldapRootDn);

				}
			}
		}

		if (GlobalConstants.SUCCESS.equals(ldapResponse.get("status"))) {
			resultMap = ServiceUtil.returnSuccess("User authenticated successfully");
		} else {
			resultMap =  ServiceUtil.returnError(ldapResponse.get("errorMessage").toString());
		}
		return resultMap;
	}

	public static Map<String, Object> validateLDAP(String ldapUserName, String ldapPassword, String ldapDomain,
			String ldapEmailDomain, String ldapUrl, String ldapRootDn) {

		LDAPService ldapService = new LDAPService();
		Map<String, Object> result = new HashMap<String, Object>();
		String searchFilter = "(&(objectClass=user)(sAMAccountName=" + ldapUserName + "))";
		try {
			
			if (/*UtilValidate.isEmpty(ldapUserName) || UtilValidate.isEmpty(ldapPassword)
					|| */UtilValidate.isEmpty(ldapDomain) || UtilValidate.isEmpty(ldapEmailDomain)
					|| UtilValidate.isEmpty(ldapUrl)) {
				result.put("status", "ERROR");
				result.put("errorMessage", "Invalid LDAP Configuration");
				return result;
			}

			ldapService.init(ldapUserName, ldapPassword, ldapDomain, ldapEmailDomain, ldapUrl, searchFilter);

			ldapService.setLdapRootDn(ldapRootDn);

			Authentication authentication = ldapService.ldapService();
			if (authentication.isAuthenticated()) {
				result.put("status", "SUCCESS");
				result.put("successMessage", "LDAP Authenticated Successfully");
			} else {
				result.put("status", "ERROR");
				result.put("errorMessage", "LDAP Authenticated unsuccessfully");
			}

		} catch (Exception e) {
			Debug.logError(e, "Could not authenticate user " + e.getMessage(), MODULE);
			result.put("status", "ERROR");
			result.put("errorMessage", e.getMessage());
		}
		return result;

	}

}
