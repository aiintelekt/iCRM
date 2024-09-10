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

String custRequestCategoryId = request.getParameter("custRequestCategoryId");
context.put("custRequestCategoryId", custRequestCategoryId);

inputContext = new LinkedHashMap<String, Object>();

List<GenericValue> srTypeIdDetails = delegator.findByAnd("CustRequestAssoc", UtilMisc.toMap("type", AdminPortalConstant.ParamUnitConstant.SR_TYPE,"active","Y"), null, false);
context.put("srTypeIds", DataHelper.getDropDownOptions(srTypeIdDetails, "code", "value"));

List<GenericValue> srStatusDetails = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", AdminPortalConstant.ParamUnitConstant.STATUS_ID), null, false);
context.put("statusList", org.fio.homeapps.util.DataHelper.getDropDownOptions(srStatusDetails, "enumCode", "description"));



if  (UtilValidate.isNotEmpty(custRequestCategoryId)) {
	srSubCategoryDetails = EntityUtil.getFirst(delegator.findByAnd("CustRequestCategory", UtilMisc.toMap("custRequestCategoryId", custRequestCategoryId), null, false));
	if (srSubCategoryDetails != null) {

		inputContext.put("custRequestCategoryId", srSubCategoryDetails.getString("custRequestCategoryId"));
		inputContext.put("sequenceNumber", srSubCategoryDetails.getString("seqNum"));
		inputContext.put("srSubArea", srSubCategoryDetails.getString("description"));
		
		String srType = "";
		String srArea = "";
		String srTypeId = "";
		String srAreaId = "";
		String status ="";

		GenericValue getsrDetails = EntityQuery.use(delegator).from("CustRequestAssoc").where("code", srSubCategoryDetails.getString("custRequestCategoryId")).queryOne();
		if (UtilValidate.isNotEmpty(getsrDetails)) {
			srType = getsrDetails.getString("grandparentValue");
			srTypeId = getsrDetails.getString("grandparentCode");
			srArea = getsrDetails.getString("parentValue");
			srAreaId = getsrDetails.getString("parentCode");
			
			inputContext.put("statusId", getsrDetails.getString("active"));
		}
		
		inputContext.put("typeId", srTypeId);
		inputContext.put("srCategoryId", srAreaId);
				
	}
}

context.put("inputContext", inputContext);
