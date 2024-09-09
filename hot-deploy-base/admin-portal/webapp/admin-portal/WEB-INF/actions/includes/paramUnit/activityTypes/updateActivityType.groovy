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
import org.fio.admin.portal.util.DataHelper;

import java.net.URL;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.util.EntityQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.fio.admin.portal.constant.AdminPortalConstant;

delegator=request.getAttribute("delegator");

inputContext=new LinkedHashMap<String,Object>();

String enumId=request.getParameter("enumId");

Debug.log("==activityTypeId=="+enumId);
List<GenericValue>srStatusDetails=delegator.findByAnd("Enumeration",UtilMisc.toMap("enumTypeId",AdminPortalConstant.ParamUnitConstant.STATUS_ID),null,false);
context.put("statusId",DataHelper.getDropDownOptions(srStatusDetails,"enumCode","description"));

/*
 * List<String> types = new ArrayList<String>(); List<GenericValue> getReqIds =
 * delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId",
 * AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false);
 * //println("====getReqIds====="+getReqIds);
 * if(UtilValidate.isNotEmpty(getReqIds)) { for(GenericValue activitytypeId :
 * getReqIds) { HashMap hp; String srcategoryDesc = null;
 * pTypeId=activitytypeId.getString("parentEnumId");
 * //println("====pTypeId====="+pTypeId); if(UtilValidate.isNotEmpty(pTypeId)) {
 * 
 * List<GenericValue> srcategories =
 * delegator.findByAnd("WorkEffortAssocTriplet", [parentCode : pTypeId , type :
 * "Type"], null, false); //println("====srcategories====="+srcategories);
 * if(UtilValidate.isNotEmpty(srcategories)) { srcategory = srcategories.get(0);
 * if(UtilValidate.isNotEmpty(srcategory)) { srcategoryDesc =
 * srcategory.getString("parentValue");
 * //println("====srcategoryDesc====="+srcategoryDesc); hp=new HashMap();
 * hp.put("pTypeId",pTypeId); hp.put("description",srcategoryDesc);
 * types.add(hp);
 * 
 * } } }
 * 
 * if(UtilValidate.isNotEmpty(hp)) { hp.put("description",srcategoryDesc);
 * types.add(hp); }
 * 
 * } }
 * 
 * context.put("aTypeId", types); println("====types====="+types);
 */

List<GenericValue> activityParentId = delegator.findByAnd("WorkEffortAssocTriplet", UtilMisc.toMap("type", AdminPortalConstant.ParamUnitConstant.RELATED_TO,"active","Y"), null, false);
context.put("activityParent", DataHelper.getDropDownOptions(activityParentId, "code", "value"));
if  (UtilValidate.isNotEmpty(enumId)) {
	activityParent = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", enumId,"enumTypeId", AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false));
	if (activityParent != null) {
		
		inputContext.put("activityParentId", activityParent.getString("parentEnumId"));
		inputContext.put("activityTypeId", activityParent.getString("enumId"));
		inputContext.put("activityType", activityParent.getString("description"));
		inputContext.put("sequenceNumber", activityParent.getString("sequenceId"));
		
		
		String parentDesc= "";
		String parenyCode = "";
		String status = "";	
		GenericValue getActivityTypeDetails = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("code", enumId).queryOne();
		if (UtilValidate.isNotEmpty(getActivityTypeDetails)) {
		parentDesc = getActivityTypeDetails.getString("parentValue");
		parenyCode = getActivityTypeDetails.getString("parentCode");
		status = getActivityTypeDetails.getString("active");
			}
			inputContext.put("activityParent", parenyCode);
			inputContext.put("status", status);
	}
}

context.put("inputContext", inputContext);