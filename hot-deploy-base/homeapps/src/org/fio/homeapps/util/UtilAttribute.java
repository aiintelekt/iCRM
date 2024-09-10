package org.fio.homeapps.util;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class UtilAttribute {
	
	private static final String MODULE = UtilAttribute.class.getName();
	
	public static String getSubscriptionAttrValue(Delegator delegator, String subscriptionId, String attrName) {
    	String attrValue = null;
		try {
			if(UtilValidate.isEmpty(subscriptionId) || UtilValidate.isEmpty(attrName)){
				return attrValue;
			}
			
			GenericValue subsAttribute = getSubscriptionAttribute(delegator, subscriptionId, attrName);
			if(UtilValidate.isNotEmpty(subsAttribute)){
				attrValue = subsAttribute.getString("attrValue");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return attrValue;
	}
	
	public static GenericValue getSubscriptionAttribute(Delegator delegator, String subscriptionId, String attrName) {
		try {
			if(UtilValidate.isEmpty(subscriptionId) || UtilValidate.isEmpty(attrName)){
				return null;
			}
			
			EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId),
                    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, attrName)
                    );
			
			GenericValue subsAttribute = EntityQuery.use(delegator).from("SubscriptionAttribute").where(mainCondition).queryFirst();
			return subsAttribute;
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
}
