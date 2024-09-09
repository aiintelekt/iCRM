import org.fio.admin.portal.constant.AdminPortalConstant;
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

userLogin = request.getAttribute("userLogin");
delegator = request.getAttribute("delegator");

inputContext = new LinkedHashMap<String, Object>();

/*List<GenericValue> priorityId = delegator.findByAnd("Enumeration",  UtilMisc.toMap("enumTypeId", AdminPortalConstant.AlertCategoryConstant.PRIORITY), null, false);
context.put("priorityId", DataHelper.getDropDownOptions(priorityId, "enumCode", "description"));*/
List<GenericValue> buStatusDetails = delegator.findByAnd("Enumeration",  UtilMisc.toMap("enumTypeId", AdminPortalConstant.AlertCategoryConstant.STATUS_ID), null, false);
context.put("statusId", DataHelper.getDropDownOptions(buStatusDetails, "enumCode", "description"));
List<GenericValue> alertTypeDetails = delegator.findByAnd("AlertType",  null, null, false);
context.put("alertTypeId", DataHelper.getDropDownOptions(alertTypeDetails, "alertTypeId", "alertTypeDescription"));

alertCategoryId = null;
alertCategoryId=request.getParameter("alertCategoryId");
GenericValue viewAlert = EntityQuery.use(delegator).from("AlertCategory").where("alertCategoryId", alertCategoryId).queryOne();
if (UtilValidate.isNotEmpty(viewAlert))
{
	inputContext.put("alertCategoryId",alertCategoryId);
	inputContext.put("alertName",viewAlert.getString("alertCategoryName"));
	inputContext.put("autoClosure",viewAlert.getString("alertAutoClosure"));
	inputContext.put("alertType",viewAlert.getString("alertTypeId"));
	inputContext.put("alertPriority",viewAlert.getString("alertPriority"));
	inputContext.put("autoClosure",viewAlert.getString("alertAutoClosure"));
	inputContext.put("duration",viewAlert.getString("alertAutoClosureDuration"));
	inputContext.put("remarks",viewAlert.getString("remarks"));
	inputContext.put("status",viewAlert.getString("isActive"));
	inputContext.put("sequenceNumber",viewAlert.getString("seqNum"));
	//Debug.log("viewAlert.getString=="+viewAlert.getString("alertAutoClosure"));
	//context.put("autoClosure", viewAlert.getString("alertTypeId"));
}

context.put("inputContext", inputContext);
