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

delegator = request.getAttribute("delegator");


String custRequestTypeId=request.getParameter("custRequestTypeId");
String description=request.getParameter("description");


Debug.log("==custRequestTypeId=="+custRequestTypeId);

if(UtilValidate.isNotEmpty(custRequestTypeId) ){
srType=EntityUtil.getFirst(delegator.findByAnd("CustRequestType", UtilMisc.toMap("custRequestTypeId",custRequestTypeId ),null, false));
if(srType!=null)
{
	context.put("custRequestTypeId",srType.getString("custRequestTypeId"));
	context.put("description",srType.getString("description"));
	String seqNo="";
	GenericValue getseqNo = EntityQuery.use(delegator).from("CustRequestAssoc").where("code",srType.getString("custRequestTypeId"), "value",srType.getString("description")).queryOne();
    if(UtilValidate.isNotEmpty(getseqNo)){
    seqNo = getseqNo.getString("sequenceNumber");
    context.put("sequenceNumber",seqNo);
    }
   }
}