/**
 * 
 */
package org.groupfio.account.service.util;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif
 *
 */
public class DataUtil {
	
	private static String MODULE = DataUtil.class.getName();
	
	
	
	public static String getUserLoginPartyId(Delegator delegator, String userLoginId) {
    	String partyId = null;
		try {
			if (UtilValidate.isNotEmpty(userLoginId)) {
				 GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userLoginId).queryOne();
				 if (UtilValidate.isNotEmpty(userLogin)) {
					 partyId = userLogin.getString("partyId");
				 }
			}
		} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);

		}
		return partyId;
	}
	
}
