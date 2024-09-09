/**
 * 
 */
package org.fio.crm.util;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class EnumUtil {

	public static final String MODULE = EnumUtil.class.getName();

	public static List<GenericValue> getEnums (Delegator delegator, String countryGeoId, String listType) {
		
		List<GenericValue> enumList = new ArrayList<GenericValue>();
		
		try {
			if (UtilValidate.isNotEmpty(countryGeoId) && UtilValidate.isNotEmpty(listType)) {
				
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("listType", EntityOperator.EQUALS, listType),
						EntityCondition.makeCondition("countryCode", EntityOperator.EQUALS, countryGeoId)
               			);
				
				GenericValue countryEnumeration = EntityUtil.getFirst( delegator.findList("CountryEnumeration", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
				if (UtilValidate.isNotEmpty(countryEnumeration)) {
					enumList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", countryEnumeration.getString("enumTypeId")), UtilMisc.toList("sequenceId"), false);
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);

		}
		
		return enumList;
	}
	
}
