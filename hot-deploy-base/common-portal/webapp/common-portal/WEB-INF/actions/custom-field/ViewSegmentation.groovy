import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
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

if(UtilValidate.isNotEmpty(parameters.get("salesOpportunityId"))){
	partyRoleTypeId="OPPORTUNITY";
}

context.put("roleTypeId", partyRoleTypeId);
context.put("partyRoleTypeId", partyRoleTypeId);

context.put("partyId", partyId);

/*
condition = UtilMisc.toMap("groupType", "SEGMENTATION", "roleTypeId", partyRoleTypeId); 
cond = EntityCondition.makeCondition(condition);
groupList = delegator.findList("CustomFieldGroupSummary", cond, null, ["sequence"], null, false);
context.put("groupList", org.fio.homeapps.util.DataHelper.getDropDownOptions(groupList, "groupId", "groupName"));	
*/


