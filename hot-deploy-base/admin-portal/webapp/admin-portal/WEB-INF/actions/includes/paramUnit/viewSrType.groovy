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

String custRequestTypeId = request.getParameter("custRequestTypeId");

if(UtilValidate.isNotEmpty(custRequestTypeId)) {
	srTypeDetails = EntityUtil.getFirst(delegator.findByAnd("CustRequestType", UtilMisc.toMap("custRequestTypeId", custRequestTypeId), null, false));
	if (srTypeDetails != null) {
		inputContext.put("custRequestTypeId", srTypeDetails.getString("custRequestTypeId"));
		inputContext.put("sequenceNumber", srTypeDetails.getString("seqNum"));
		inputContext.put("srType", srTypeDetails.getString("description"));
	}
}
if(UtilValidate.isNotEmpty(custRequestTypeId)) {
	srTypeDetail = EntityUtil.getFirst(delegator.findByAnd("CustRequestAssoc", UtilMisc.toMap("code", custRequestTypeId), null, false));
	if (srTypeDetail != null) {
		String statusId = srTypeDetail.getString("active");
		inputContext.put("status", statusId);
	}
}

context.put("inputContext", inputContext);

