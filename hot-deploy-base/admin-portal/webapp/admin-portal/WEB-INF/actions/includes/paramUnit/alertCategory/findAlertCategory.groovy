import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.admin.portal.constant.AdminPortalConstant.AlertCategoryConstant
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import java.util.LinkedHashMap;
import org.fio.admin.portal.util.DataHelper;


import java.util.function.Function
import java.util.stream.Collectors

import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue





List<GenericValue> alertTypeDetails = delegator.findByAnd("AlertType",  null, null, false);
context.put("alertTypeId", DataHelper.getDropDownOptions(alertTypeDetails, "alertTypeId", "alertTypeDescription"));

List<GenericValue> alertNameDetails = delegator.findByAnd("AlertCategory",  null, null, false);
context.put("alertNameId", DataHelper.getDropDownOptions(alertNameDetails, "alertCategoryId", "alertCategoryName"));

searchCriteria = request.getParameter("searchCriteria");
println('==searchCriteria==='+searchCriteria);
if(UtilValidate.isEmpty(searchCriteria)) {
	List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
	Set<String> fieldsToSelect = new TreeSet<String>();

	fieldsToSelect.add("alertCategoryId");
	fieldsToSelect.add("alertCategoryName");
	fieldsToSelect.add("alertTypeId");
	fieldsToSelect.add("alertPriority");
	fieldsToSelect.add("alertAutoClosure");
	fieldsToSelect.add("alertAutoClosureDuration");
	fieldsToSelect.add("modifiedUserLoginId");
	fieldsToSelect.add("createdByUserLoginId");
	fieldsToSelect.add("remarks");
	fieldsToSelect.add("isActive");
	fieldsToSelect.add("seqNum");
	fieldsToSelect.add("createdOn");
	fieldsToSelect.add("modifiedOn");
	fieldsToSelect.add("lastUpdatedTxStamp");
	String priority=null;
	String type=null;
	String status=null;
	
	List<GenericValue> alertCategories = EntityQuery.use(delegator).select(fieldsToSelect).from("AlertCategory").orderBy("-lastUpdatedTxStamp").queryList();
	if(alertCategories != null && alertCategories.size() > 0)
	{
		for(GenericValue alertCategory : alertCategories) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("alert", alertCategory.getString("alertCategoryName"));
			data.put("closure", alertCategory.getString("alertAutoClosure"));
			data.put("duration", alertCategory.getString("alertAutoClosureDuration"));
			data.put("sequence", alertCategory.getString("seqNum"));
			data.put("remark", alertCategory.getString("remarks"));
			data.put("createdOn", alertCategory.getString("createdOn"));
			data.put("createdBy", alertCategory.getString("createdByUserLoginId"));
			data.put("modifiedOn", alertCategory.getString("modifiedOn"));
			data.put("modifiedBy", alertCategory.getString("modifiedUserLoginId"));
			type=alertCategory.getString("alertTypeId");
			if(UtilValidate.isNotEmpty(type)){
				GenericValue getType = EntityQuery.use(delegator).from("AlertType").where("alertTypeId", type).queryOne();
				if(UtilValidate.isNotEmpty(getType)){
					data.put("type", getType.getString("alertTypeDescription"));
				}
			}
			priority=alertCategory.getString("alertPriority");
			if(UtilValidate.isNotEmpty(priority)){
				GenericValue getPriority =  EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", priority,"enumTypeId",AlertCategoryConstant.PRIORITY), null, false));
				if (UtilValidate.isNotEmpty(getPriority)){
					data.put("priority",getPriority.getString("description"));
				}
			}
			status=alertCategory.getString("isActive");
			if(UtilValidate.isNotEmpty(status)){
				GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status,"enumTypeId",AlertCategoryConstant.STATUS_ID), null, false));
				if (UtilValidate.isNotEmpty(getStatus)){
					data.put("status",getStatus.getString("description"));
				}
			}
		   
			data.put("alertCategoryId", alertCategory.getString("alertCategoryId"));
			results.add(data);
		}
	}

}else {
	context.put("searchCriteria", searchCriteria);
}