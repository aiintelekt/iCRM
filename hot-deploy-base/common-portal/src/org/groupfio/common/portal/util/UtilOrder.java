/**
 * 
 */
package org.groupfio.common.portal.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilFreemarker;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class UtilOrder {

	private static final String MODULE = UtilOrder.class.getName();
	
	public static Timestamp getEarliestShipByDate(Delegator delegator, String orderId) {
		try {
			if (UtilValidate.isNotEmpty(orderId)) {
				List<EntityCondition> conditionlist = FastList.newInstance();

				conditionlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
				GenericValue group = EntityQuery.use(delegator)
						.select("shipByDate").from("OrderItemShipGroup").where(condition)
						.orderBy("shipByDate").queryFirst();

				if (UtilValidate.isNotEmpty(group)) {
					return group.getTimestamp("shipByDate");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String generateOrderHtml(Delegator delegator, Map<String, Object> context) {
		try {
			if(UtilValidate.isEmpty(context)) {
				return null;
			}
			Map<String, Object> callCtxt = new HashMap<String, Object>();
			Map<String, Object> callResult = new HashMap<String, Object>();
			
			String orderId = ParamUtil.getString(context, "orderId");
			String erOrdhtmlTplId = ParamUtil.getString(context, "erOrdhtmlTplId");
			String cashierNumber = ParamUtil.getString(context, "cashierNumber");
			String storeNumber = ParamUtil.getString(context, "storeNumber");
			List<Map<String, Object>> itmList = (List<Map<String, Object>>) context.get("itmList");
			
			if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(erOrdhtmlTplId)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("templateId", EntityOperator.EQUALS, erOrdhtmlTplId));
	        	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	        	GenericValue tpl = EntityQuery.use(delegator).select("templateFormContent").from("TemplateMaster").where(mainConditon).queryFirst();
				if (UtilValidate.isNotEmpty(tpl)) {
					String templateFormContent = tpl.getString("templateFormContent");
					if (UtilValidate.isNotEmpty(templateFormContent)) {
						if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
							templateFormContent = Base64.base64Decode(templateFormContent);
						}
					}
					
					callCtxt = new HashMap<String, Object>();
					Map<String, Object> dataContext = new LinkedHashMap<>();
					dataContext.put("oh", context.get("orderHeader"));
					dataContext.put("cashier", cashierNumber);
					dataContext.put("storeNumber", storeNumber);
					dataContext.put("oiList", context.get("itmList"));
					dataContext.put("adjList", context.get("adjList"));
					dataContext.put("taxAdjList", context.get("taxAdjList"));
					
					if (UtilValidate.isNotEmpty(itmList)) {
						dataContext.put("itemCount", itmList.size());
					}
					
					callCtxt.put("dataContext", dataContext);
					
					return UtilFreemarker.renderPreview(templateFormContent, callCtxt);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
}
