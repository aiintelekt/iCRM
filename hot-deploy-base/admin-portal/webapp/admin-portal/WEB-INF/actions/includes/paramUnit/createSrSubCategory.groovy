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

import org.fio.admin.portal.constant.AdminPortalConstant
import org.fio.admin.portal.util.DataHelper;
import org.apache.commons.lang.StringUtils;

delegator = request.getAttribute("delegator");

inputContext = new LinkedHashMap<String, Object>();
context.put("inputContext", inputContext);

/*
 * List<GenericValue> srSubArea = delegator.findByAnd("CustRequestCategory",
 * null, null, false); context.put("srCategoryIds",
 * DataHelper.getDropDownOptions1(srSubArea, "custRequestCategoryId",
 * "description"));
 */

List<GenericValue> srTypeIdDetails = delegator.findByAnd("CustRequestAssoc", UtilMisc.toMap("type", AdminPortalConstant.ParamUnitConstant.SR_TYPE,"active","Y"), null, false);
context.put("srTypeIds", DataHelper.getDropDownOptions(srTypeIdDetails, "code", "value"));

List<GenericValue> srStatusDetails = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", AdminPortalConstant.ParamUnitConstant.STATUS_ID), null, false);
context.put("statusList", org.fio.homeapps.util.DataHelper.getDropDownOptions(srStatusDetails, "enumCode", "description"));



