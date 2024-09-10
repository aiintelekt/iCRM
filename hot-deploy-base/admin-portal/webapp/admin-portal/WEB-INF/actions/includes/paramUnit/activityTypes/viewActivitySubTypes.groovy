import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.fio.admin.portal.util.DataHelper;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.fio.crm.util.EnumUtil;


import java.net.URL;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.util.EntityQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.fio.admin.portal.constant.AdminPortalConstant;

delegator = request.getAttribute("delegator");

inputContext = new LinkedHashMap<String, Object>();

String enumId = request.getParameter("enumId");


Debug.log("==activityTypeId==" + enumId);

/*
 * if (UtilValidate.isNotEmpty(enumId)) { activityParent =
 * EntityUtil.getFirst(delegator.findByAnd("Enumeration",
 * UtilMisc.toMap("enumId", enumId,"enumTypeId",
 * AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false)); if
 * (activityParent != null) {
 * 
 * context.put("activityTypeId", activityParent.getString("parentEnumId"));
 * context.put("activitySubTypeId", activityParent.getString("enumId"));
 * context.put("activitySubTypeDesc", activityParent.getString("description"));
 * context.put("seqNum", activityParent.getString("sequenceId")); String status
 * = activityParent.getString("isEnabled"); String typeDesc= ""; String
 * activityDesc = "";
 * 
 * GenericValue getActivityTypeDetails =
 * EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("code",
 * activityParent.getString("enumId")).queryOne(); if
 * (UtilValidate.isNotEmpty(getActivityTypeDetails)) { typeDesc =
 * getActivityTypeDetails.getString("parentValue"); activityDesc =
 * getActivityTypeDetails.getString("grandparentValue"); } String statusDesc="";
 * statusDetails = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
 * UtilMisc.toMap("enumCode",
 * status,"enumTypeId",AdminPortalConstant.ParamUnitConstant.STATUS_ID), null,
 * false)); if (statusDetails != null) { context.put("statusDesc",
 * statusDetails.getString("description")); } context.put("typeDesc", typeDesc);
 * context.put("activityDesc", activityDesc);
 * 
 * 
 * } }
 */

if  (UtilValidate.isNotEmpty(enumId)) {
	activityParent = EntityUtil.getFirst(delegator.findByAnd("WorkEffortAssocTriplet", UtilMisc.toMap("code", enumId,"type", AdminPortalConstant.ParamUnitConstant.SUB_TYPE), null, false));
	if (activityParent != null) {
		
		inputContext.put("activityTypeId", activityParent.getString("parentCode"));
		inputContext.put("activitySubTypeId", activityParent.getString("code"));
		inputContext.put("activitySubType", activityParent.getString("value"));
		inputContext.put("sequenceNumber", activityParent.getString("sequenceNumber"));
		inputContext.put("activityType", activityParent.getString("parentValue"));
		inputContext.put("activityParent", activityParent.getString("grandparentValue"));
		String status =  activityParent.getString("active");
		
		String statusDesc="";
		statusDetails = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status,"enumTypeId",AdminPortalConstant.ParamUnitConstant.STATUS_ID), null, false));
		if (statusDetails != null) {
			inputContext.put("status", statusDetails.getString("description"));
		}
		
	}
}

context.put("inputContext", inputContext);