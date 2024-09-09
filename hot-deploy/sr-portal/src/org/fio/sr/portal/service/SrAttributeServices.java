/**
 * 
 */
package org.fio.sr.portal.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class SrAttributeServices {

	private static final String MODULE = SrAttributeServices.class.getName();

	public static Map updateAttribute(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String srNumber = (String) context.get("srNumber");

		String attrName = (String) context.get("attrName");
		String attrValue = (String) context.get("attrValue");
		String sequenceNumber = (String) context.get("sequenceNumber");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			result.put("custRequestId", srNumber);
			List conditionList = FastList.newInstance();

			conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, srNumber));
			conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, attrName));

			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue attr = EntityUtil.getFirst( delegator.findList("CustRequestAttribute", mainConditons, null, null, null, false) );

			if (UtilValidate.isEmpty(attr)) {
				result.putAll(ServiceUtil.returnSuccess("Attribute not exists!"));
				return result;
			}
			
			attr.put("attrValue", attrValue);
			if (UtilValidate.isNotEmpty(sequenceNumber)) {
				attr.put("sequenceNumber", sequenceNumber);
			}
			
			attr.store();
			
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully updated attribute.."));

		return result;

	}
	
}
