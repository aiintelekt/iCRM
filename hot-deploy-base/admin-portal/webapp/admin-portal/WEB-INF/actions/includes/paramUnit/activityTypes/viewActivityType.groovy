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

if  (UtilValidate.isNotEmpty(enumId)) {
	activityParent = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", enumId,"enumTypeId", AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false));
	if (activityParent != null) {
        
		inputContext.put("activityParentId", activityParent.getString("parentEnumId"));
		inputContext.put("activityTypeId", activityParent.getString("enumId"));
		inputContext.put("activityType", activityParent.getString("description"));
		inputContext.put("sequenceNumber", activityParent.getString("sequenceId"));
		String status  = "";
		String parentDesc= "";
		
		GenericValue getActivityTypeDetails = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("code", activityParent.getString("enumId")).queryOne();
		if (UtilValidate.isNotEmpty(getActivityTypeDetails)) {
			parentDesc = getActivityTypeDetails.getString("parentValue");
			status =  getActivityTypeDetails.getString("active");
		}
		String statusDesc="";
		statusDetails = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status,"enumTypeId",AdminPortalConstant.ParamUnitConstant.STATUS_ID), null, false));
		if (statusDetails != null) {
			inputContext.put("status", statusDetails.getString("description"));
		}
		inputContext.put("activityParent", parentDesc);
		

	}
}

context.put("inputContext", inputContext);
