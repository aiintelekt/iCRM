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

List<GenericValue> srCategory = delegator.findByAnd("CustRequestAssoc", UtilMisc.toMap("type", AdminPortalConstant.ParamUnitConstant.SR_TYPE,"active","Y"), null, false);
context.put("srTypeIds", org.fio.homeapps.util.DataHelper.getDropDownOptions(srCategory, "code", "value"));



//Debug.log("==srTypeIds=="+DataHelper.getDropDownKeyValueOptions(srCategory, "custRequestTypeId", "description"));

if  (UtilValidate.isNotEmpty(custRequestCategoryId)) {
	srCategoryDetails = EntityUtil.getFirst(delegator.findByAnd("CustRequestCategory", UtilMisc.toMap("custRequestCategoryId", custRequestCategoryId), null, false));
	if (srCategoryDetails != null) {

		inputContext.put("custRequestCategoryId", srCategoryDetails.getString("custRequestCategoryId"));
		inputContext.put("sequenceNumber", srCategoryDetails.getString("seqNum"));
		inputContext.put("srArea", srCategoryDetails.getString("description"));
		
		String srType = "";
		String srTypeId = "";
		
		GenericValue getsrDetails = EntityQuery.use(delegator).from("CustRequestAssoc").where("code", srCategoryDetails.getString("custRequestCategoryId")).queryOne();
		if (UtilValidate.isNotEmpty(getsrDetails)) {
			srType = getsrDetails.getString("parentValue");
			srTypeId = getsrDetails.getString("parentCode");
		}
		
		//inputContext.put("srType",srTypeId+"("+srType+")");
		inputContext.put("typeId", srTypeId);
		println( inputContext.get("typeId") );
		Debug.log("======"+srTypeId+"("+srType+")");
		//Debug.log("srType"+srType);
		inputContext.put("status", getsrDetails.getString("active"));
		
	}
}

List<GenericValue> srStatusDetails = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", AdminPortalConstant.ParamUnitConstant.STATUS_ID), null, false);
context.put("statusList", org.fio.homeapps.util.DataHelper.getDropDownOptions(srStatusDetails, "enumCode", "description"));
	

context.put("inputContext", inputContext);
