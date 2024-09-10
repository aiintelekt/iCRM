/**
 * 
 */
package org.fio.homeapps.util;

import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.util.EntityCrypto;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;

/**
 * @author Mahendran Thanasekaran
 *
 */
public class UtilAuth {

	private static final String MODULE = UtilAuth.class.getName();
	
	public static boolean apiUserValidation(Delegator delegator, String apiUser, String apiKey) {
		boolean isValid = false;
		try {
			String key = EntityUtilProperties.getPropertyValue("general", "api-key", delegator);
	        try {
        		GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", apiUser).queryOne();
	        	if(UtilValidate.isNotEmpty(userLogin) && "Y".equals(userLogin.getString("enabled"))){
	        		EntityCrypto entityCrypto = new EntityCrypto(delegator,null); 
	        		
	        		Object decryptedPwd = entityCrypto.decrypt(key, ModelField.EncryptMethod.TRUE, apiKey);
	        		String password = UtilValidate.isNotEmpty(decryptedPwd) ? decryptedPwd.toString() : "";
	        		if(checkPassword(userLogin.getString("currentPassword"), true, password)) {
	        			isValid = true;
	        		}
	        	}
	        } catch (GeneralException e) {
	            Debug.logWarning(e, "Problem in decryption");
	            return false;
	        }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return isValid;
	}
	
	private static boolean checkPassword(String oldPassword, boolean useEncryption, String currentPassword) {
		boolean passwordMatches = false;
		if (oldPassword != null) {
			if (useEncryption) {
				passwordMatches = HashCrypt.comparePassword(oldPassword,
						org.ofbiz.common.login.LoginServices.getHashType(), currentPassword);
			} else {
				passwordMatches = oldPassword.equals(currentPassword);
			}
		}
		if (!passwordMatches
				&& "true".equals(UtilProperties.getPropertyValue("security", "password.accept.encrypted.and.plain"))) {
			passwordMatches = currentPassword.equals(oldPassword);
		}
		return passwordMatches;
	}
	
}
