/**
 * 
 */
package org.groupfio.common.portal.util;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

/**
 * @author Sharif
 *
 */
public class UtilOpportunity {

	private static final String MODULE = UtilOpportunity.class.getName();
	
	public static String isOpportunityTypeEnabled(Delegator delegator) {
    	String enabled = "N";
		try {
			enabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "OPPORTUNITY_TYPE", "N");
		} catch (Exception e) {
		}
		return enabled;
	}
	
	public static boolean isBaseOpportunity(Delegator delegator, String salesOpportunityId) {
		boolean value = false;
		try {
			GenericValue salesOppoAttr = EntityQuery.use(delegator).from("SalesOpportunity").where("salesOpportunityId",salesOpportunityId,"salesOpportunityTypeId","BASE").queryFirst();
			if(UtilValidate.isNotEmpty(salesOppoAttr)) {
				value = true;
			}
		} catch (Exception e) {
		}
		return value;
	}
	
}
