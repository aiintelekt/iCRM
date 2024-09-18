/**
 * 
 */
package org.fio.homeapps.util;

import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class UtilPermission {
	
	private static String MODULE = UtilPermission.class.getName();

	public static boolean hasRole(Delegator delegator, GenericValue userLogin, String roleTypeId) {
        if (userLogin == null || roleTypeId == null) {
            return false;
        }
        String partyId = userLogin.getString("partyId");
        GenericValue partyRole = null;
        try {
            partyRole = EntityQuery.use(delegator).from("PartyRole").where("partyId", partyId, "roleTypeId", roleTypeId).queryOne();
        } catch (GenericEntityException e) {
            Debug.logError(e, MODULE);
            return false;
        }

        if (partyRole == null) {
            return false;
        }

        return true;
    }
	
	public static boolean hasPermission(Delegator delegator, GenericValue userLogin, String securityGroupId) {
        if (UtilValidate.isEmpty(userLogin) || UtilValidate.isEmpty(securityGroupId)) {
            return false;
        }
        String userLoginId = userLogin.getString("userLoginId");
        try {
        	List conditionList = FastList.newInstance();
			
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId),
					EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, securityGroupId)
                    ));
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue securityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup").where(mainConditons).queryFirst();
			if (UtilValidate.isEmpty(securityGroup)) {
				return false;
			}
        } catch (GenericEntityException e) {
            Debug.logError(e, MODULE);
            return false;
        }
        return true;
    }
	
}
