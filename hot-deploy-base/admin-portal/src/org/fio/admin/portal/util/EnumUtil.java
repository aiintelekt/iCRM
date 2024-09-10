/**
 * 
 */
package org.fio.admin.portal.util;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 * @author Mahendran Thanasekaran
 */
public class EnumUtil {

	public static List<GenericValue> getEnums (Delegator delegator, String countryGeoId, String listType) {
		List<GenericValue> enumList = new ArrayList<GenericValue>();
		try {
			if (UtilValidate.isNotEmpty(countryGeoId) && UtilValidate.isNotEmpty(listType)) {
				
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("listType", EntityOperator.EQUALS, listType),
						EntityCondition.makeCondition("countryCode", EntityOperator.EQUALS, countryGeoId)
               			);
				
				GenericValue countryEnumeration = EntityQuery.use(delegator).select("enumTypeId").from("CountryEnumeration").where(condition).queryFirst();
				if (UtilValidate.isNotEmpty(countryEnumeration)) {
					enumList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", countryEnumeration.getString("enumTypeId")), UtilMisc.toList("sequenceId"), false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return enumList;
	}
	
	public static List<GenericValue> getEnums(Delegator delegator, String enumTypeId){
		List<GenericValue> enumList = null;
		try {
			if(UtilValidate.isNotEmpty(enumTypeId)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,enumTypeId),
						EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("disabled",EntityOperator.EQUALS,null),
								EntityCondition.makeCondition("disabled",EntityOperator.EQUALS,"N")));
				enumList = EntityQuery.use(delegator).from("Enumeration").where(condition).orderBy("sequenceId").queryList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return enumList;
		
	}
	
	public static String getEnumDescription(Delegator delegator, String enumTypeId, String value){
		String description = "";
		try {
			if(UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(
						EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId),
						EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("enumId", EntityOperator.EQUALS, value),
						EntityCondition.makeCondition("enumCode", EntityOperator.EQUALS, value))
						);
				GenericValue getDescription = EntityQuery.use(delegator).from("Enumeration").where(condition).queryOne();
			    if (UtilValidate.isNotEmpty(getDescription)) {
			    	description = getDescription.getString("description");
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return description;	
	}
}
