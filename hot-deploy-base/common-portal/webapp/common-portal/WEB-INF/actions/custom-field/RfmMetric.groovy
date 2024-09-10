import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import java.util.List

import net.sf.json.JSONObject;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);

String partyId = request.getParameter("partyId");
partyRoleTypeId = context.get("partyRoleTypeId");
context.put("roleTypeId", partyRoleTypeId);
context.put("partyRoleTypeId", partyRoleTypeId);
context.put("partyId", partyId);

condition = null;
if(partyRoleTypeId.equals("ACCOUNT")){
	condition = EntityCondition.makeCondition("groupId", EntityOperator.IN, UtilMisc.toList("RFS_SPEND_RANGE", "RFS_RECENCY", "RFS_FREQUENCY"))
}else if(partyRoleTypeId.equals("CUSTOMER")){
	condition = EntityCondition.makeCondition("groupId", EntityOperator.IN, UtilMisc.toList("RFM_SPEND_RANGE", "RFM_RECENCY", "RFM_FREQUENCY"))
}
cond = EntityCondition.makeCondition(condition);
groupList = delegator.findList("CustomFieldGroup", cond, null, ["sequence"], null, false);
context.put("rfmGroupList", org.fio.homeapps.util.DataHelper.getDropDownOptions(groupList, "groupId", "groupName"));
