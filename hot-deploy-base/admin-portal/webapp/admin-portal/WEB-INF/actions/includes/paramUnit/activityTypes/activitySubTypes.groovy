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
import org.apache.commons.lang.StringUtils;
import org.fio.admin.portal.constant.AdminPortalConstant;


delegator = request.getAttribute("delegator");

inputContext = new LinkedHashMap<String, Object>();
context.put("inputContext", inputContext);

List<GenericValue> srStatusDetails = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", AdminPortalConstant.ParamUnitConstant.STATUS_ID), null, false);
context.put("statusId", DataHelper.getDropDownOptions(srStatusDetails, "enumCode", "description"));

List<GenericValue> activityParentId = delegator.findByAnd("WorkEffortAssocTriplet", UtilMisc.toMap("type", AdminPortalConstant.ParamUnitConstant.RELATED_TO,"active","Y"), null, false);
context.put("activityParent", DataHelper.getDropDownOptions(activityParentId, "code", "value"));

List<GenericValue> activityTypeId = delegator.findByAnd("WorkEffortAssocTriplet", UtilMisc.toMap("type", AdminPortalConstant.ParamUnitConstant.TYPE,"active","Y"), null, false);
context.put("activityTypeId", DataHelper.getDropDownOptions(activityTypeId, "code", "value"));

List<String> types = new ArrayList<String>();
List<GenericValue> getReqIds = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", AdminPortalConstant.ParamUnitConstant.IA_TYPE), null, false);
//println("====getReqIds====="+getReqIds);
if(UtilValidate.isNotEmpty(getReqIds)) {
	for(GenericValue activitytypeId : getReqIds) {
		HashMap hp;
		String srcategoryDesc = null;
		pTypeId=activitytypeId.getString("parentEnumId");
		//println("====pTypeId====="+pTypeId);
		if(UtilValidate.isNotEmpty(pTypeId)) {
			
			List<GenericValue> srcategories = delegator.findByAnd("WorkEffortAssocTriplet", [parentCode : pTypeId , type : "SubType"], null, false);
			//println("====srcategories====="+srcategories);
			if(UtilValidate.isNotEmpty(srcategories)) {
				srcategory = srcategories.get(0);
				if(UtilValidate.isNotEmpty(srcategory)) {
					srcategoryDesc = srcategory.getString("parentValue");
					//println("====srcategoryDesc====="+srcategoryDesc);
					hp=new HashMap();
					hp.put("pTypeId",pTypeId);
					hp.put("description",srcategoryDesc);
					types.add(hp);
					
				}
			}
		}
/*
 * if(UtilValidate.isNotEmpty(hp)) { hp.put("description",srcategoryDesc);
 * types.add(hp); }
 */
   }
}

context.put("aTypeId", types);
//println("====types====="+types);

