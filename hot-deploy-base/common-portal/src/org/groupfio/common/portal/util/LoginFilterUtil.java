package org.groupfio.common.portal.util;


import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

public class LoginFilterUtil {
	private static String MODULE = LoginFilterUtil.class.getName();
    //Check Full Access Role Type	
    public static boolean checkEmployeePosition(Delegator delegator, String userLoginId) {
		
		List<String> roleList = new ArrayList<String>();
		try {
			if (UtilValidate.isNotEmpty(userLoginId)) {
				EntityCondition searchCond = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("systemResourceId", EntityOperator.EQUALS, "crm_admin_role"));
				List<GenericValue> resultList = delegator.findList("SystemProperty", searchCond, UtilMisc.toSet("systemPropertyValue"), null, null, true);
				if (UtilValidate.isNotEmpty(resultList)) {
					roleList = EntityUtil.getFieldListFromEntityList(resultList, "systemPropertyValue", true);
				}
				
				if (UtilValidate.isNotEmpty(roleList)) {
					EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginId),
							EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleList));
					List<GenericValue> roleResultList = delegator.findList("PartyRole", searchConditions, UtilMisc.toSet("roleTypeId"), null, null, true);
					if (UtilValidate.isNotEmpty(roleResultList)) {
						return false;
					}
				}
				else {
		            Debug.logInfo("Admin Role Property is not configured for the user", MODULE);
		            return true;
				}
			}
			else {
	            Debug.logWarning("Received a null User Login Id from HttpServletRequest", MODULE);
			}
			
		} catch (GenericEntityException e) {
			Debug.log("Exception in checkEmployeePosition"+e.getMessage());
		}
		
		return true;
	}


}
