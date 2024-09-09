import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.Debug;
import java.util.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.fio.admin.portal.util.DataHelper;
import org.ofbiz.base.util.UtilValidate;
import java.util.List;
import org.fio.admin.portal.util.DataHelper;
import org.fio.admin.portal.constant.AdminPortalConstant;

inputContext = new LinkedHashMap<String, Object>();

alertCategoryId = null;
alertTypeId=null;
alertPriorityId=null;
status=null;
alertCategoryId=request.getParameter("alertCategoryId");
GenericValue viewAlert = EntityQuery.use(delegator).from("AlertCategory").where("alertCategoryId", alertCategoryId).queryOne();
if (UtilValidate.isNotEmpty(viewAlert))
{
	inputContext.put("alertCategoryId",alertCategoryId);
	inputContext.put("alertName",viewAlert.getString("alertCategoryName"));
	alertTypeId=viewAlert.getString("alertTypeId");
	if (UtilValidate.isNotEmpty(alertTypeId)){
		GenericValue getAlertType= EntityQuery.use(delegator).from("AlertType").where("alertTypeId", alertTypeId).queryOne();
		if (UtilValidate.isNotEmpty(getAlertType)){
			inputContext.put("alertType",getAlertType.getString("alertTypeDescription"));
		}
	}
	alertPriorityId=viewAlert.getString("alertPriority");
	if (UtilValidate.isNotEmpty(alertPriorityId)){
		GenericValue getPriority = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", alertPriorityId,"enumTypeId",AdminPortalConstant.AlertCategoryConstant.PRIORITY), null, false));
		if (UtilValidate.isNotEmpty(getPriority)){
			inputContext.put("alertPriority",getPriority.getString("description"));
		}
	}
	status=viewAlert.getString("isActive");
	if (UtilValidate.isNotEmpty(status))
	{
		GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status,"enumTypeId",AdminPortalConstant.AlertCategoryConstant.STATUS_ID), null, false));
		if (UtilValidate.isNotEmpty(getStatus))
		{
			inputContext.put("status",getStatus.getString("description"));
		}
	}
	
	inputContext.put("autoClosure",viewAlert.getString("alertAutoClosure"));
	inputContext.put("duration",viewAlert.getString("alertAutoClosureDuration"));
	inputContext.put("remarks",viewAlert.getString("remarks")); 
	inputContext.put("sequenceNumber",viewAlert.getString("seqNum"));
	
}

context.put("inputContext", inputContext);