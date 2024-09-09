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

String activityParentId = request.getParameter("enumId");
	
Debug.log("==activityParentId==" + activityParentId);

if  (UtilValidate.isNotEmpty(activityParentId)) {
	activityParent = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", activityParentId,"enumTypeId", AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false));
	if (activityParent != null) {

		inputContext.put("activityParentId", activityParent.getString("enumId"));
		inputContext.put("activityParent", activityParent.getString("description"));
		inputContext.put("sequenceNumber", activityParent.getString("sequenceId"));
		activityParents = EntityUtil.getFirst(delegator.findByAnd("WorkEffortAssocTriplet", UtilMisc.toMap("code", activityParentId,"type", AdminPortalConstant.ParamUnitConstant.RELATED_TO), null, false));
		if (UtilValidate.isNotEmpty(activityParents)) {
			inputContext.put("status", activityParents.getString("active"));
		}
	}
}

context.put("inputContext", inputContext);