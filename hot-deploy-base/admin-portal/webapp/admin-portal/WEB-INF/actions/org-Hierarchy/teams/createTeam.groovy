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

List<GenericValue> statusDetails = delegator.findByAnd("Enumeration",  UtilMisc.toMap("enumTypeId", AdminPortalConstant.BusinessUnitConstant.STATUS_ID), null, false);
context.put("statusId", DataHelper.getDropDownOptions(statusDetails, "enumCode", "description"));
List<GenericValue> buDetails = delegator.findByAnd("ProductStoreGroup", UtilMisc.toMap("status", AdminPortalConstant.BusinessUnitConstant.ACTIVE), null, false);
context.put("buId", DataHelper.getDropDownOptions(buDetails, "productStoreGroupId", "productStoreGroupName"));
