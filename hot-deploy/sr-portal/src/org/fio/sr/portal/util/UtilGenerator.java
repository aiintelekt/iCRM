/**
 * 
 */
package org.fio.sr.portal.util;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class UtilGenerator {

	private static String MODULE = UtilGenerator.class.getName();
	
	public static String getSrNumber(Delegator delegator, String state, String county) {
		try {
			if (UtilValidate.isNotEmpty(state) && UtilValidate.isNotEmpty(county)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition("state", EntityOperator.EQUALS, state));  
				conditionList.add( EntityCondition.makeCondition("county", EntityOperator.EQUALS, county));  
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue storeAssoc = EntityUtil.getFirst( delegator.findList("ProductStoreTechAssoc", mainConditons, UtilMisc.toSet("productStoreId"), null, null, false) );
				if (UtilValidate.isNotEmpty(storeAssoc) && UtilValidate.isNotEmpty(storeAssoc.getString("productStoreId"))) {
					String productStoreId = storeAssoc.getString("productStoreId");
					conditionList = FastList.newInstance();
					
					conditionList.add( EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));  
					
					mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					
					GenericValue store = EntityUtil.getFirst( delegator.findList("ProductStore", mainConditons, UtilMisc.toSet("storePrefix"), null, null, false) );
					if (UtilValidate.isNotEmpty(store) && UtilValidate.isNotEmpty(store.getString("storePrefix"))) {
						String seqName = "SR-" + productStoreId;
						String prefix = store.getString("storePrefix");
						return org.groupfio.common.portal.util.UtilGenerator.getFormattedCode(prefix+"-", delegator.getNextSeqId(seqName, 0));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
