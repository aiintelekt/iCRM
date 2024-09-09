import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.fio.admin.portal.util.DataHelper;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.fio.admin.portal.constant.AdminPortalConstant
import org.fio.crm.util.EnumUtil;


import java.net.URL;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.util.EntityQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

delegator = request.getAttribute("delegator");

inputContext = new LinkedHashMap<String, Object>();

String custRequestCategoryId = request.getParameter("custRequestCategoryId");

Debug.log("==custRequestCategoryId==" + custRequestCategoryId);

if  (UtilValidate.isNotEmpty(custRequestCategoryId)) {
    srArea = EntityUtil.getFirst(delegator.findByAnd("CustRequestCategory", UtilMisc.toMap("custRequestCategoryId", custRequestCategoryId), null, false));
    if (srArea != null) {

        inputContext.put("custRequestCategoryId", srArea.getString("custRequestCategoryId"));
		inputContext.put("srArea", srArea.getString("description"));
        inputContext.put("sequenceNumber", srArea.getString("seqNum"));
        inputContext.put("typeId", srArea.getString("custRequestTypeId"));
		
        GenericValue getsrDetails = EntityQuery.use(delegator).from("CustRequestAssoc").where("code", srArea.getString("custRequestCategoryId")).queryOne();
        if (UtilValidate.isNotEmpty(getsrDetails)) {
			String status = getsrDetails.getString("active");
			inputContext.put("status", status);
        }
    }
}
context.put("inputContext", inputContext);