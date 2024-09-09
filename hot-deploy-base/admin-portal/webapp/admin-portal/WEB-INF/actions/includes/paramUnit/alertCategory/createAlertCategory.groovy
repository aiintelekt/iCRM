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
context.put("inputContext", inputContext);

/*List<GenericValue> priorityDetails = delegator.findByAnd("Enumeration",  UtilMisc.toMap("enumTypeId", AdminPortalConstant.AlertCategoryConstant.PRIORITY), null, false);
context.put("priorityId", DataHelper.getDropDownOptions(priorityDetails, "enumCode", "description"));*/
List<GenericValue> buStatusDetails = delegator.findByAnd("Enumeration",  UtilMisc.toMap("enumTypeId", AdminPortalConstant.AlertCategoryConstant.STATUS_ID), null, false);
context.put("statusId", DataHelper.getDropDownOptions(buStatusDetails, "enumCode", "description"));
List<GenericValue> alertTypeDetails = delegator.findByAnd("AlertType",  null, null, false);
context.put("alertTypeId", DataHelper.getDropDownOptions(alertTypeDetails, "alertTypeId", "alertTypeDescription"));

