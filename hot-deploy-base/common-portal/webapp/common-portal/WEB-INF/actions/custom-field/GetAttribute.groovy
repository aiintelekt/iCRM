import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.util.EntityQuery;

import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.base.util.UtilDateTime;

String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
context.put("globalDateFormat", globalDateFormat);

partyId = parameters.get("partyId");
groupingCodeId = parameters.get("groupingCodeId");

currentRole = request.getParameter("requestUri");
if(UtilValidate.isEmpty(currentRole)) {
	currentRole = request.getRequestURI();
}
roleType = "CONTACT";
if(currentRole.contains("viewLead")){
	roleType="LEAD"
} else if(currentRole.contains("viewAccount")){
	roleType="ACCOUNT"
} else if(currentRole.contains("viewCustomer")){
	roleType="CUSTOMER"
} else if(currentRole.contains("viewServiceRequest")){
	roleType="SERVICE_REQUEST"
} else if(currentRole.contains("viewRebate")){
	roleType="REBATE"
} else if(currentRole.contains("viewActivity")){
	roleType="ACTIVITY"
	if (UtilValidate.isNotEmpty(context.get("activityRoleType"))) {
		roleType=context.get("activityRoleType");
	}
}

if(UtilValidate.isNotEmpty(parameters.get("salesOpportunityId"))){
	roleType="OPPORTUNITY";
}

String domainEntityType = roleType;
if(currentRole.contains("viewActivity")){
	domainEntityType = "ACTIVITY";
}

println("roleType: "+roleType);

context.put("partyId", partyId);
context.put("roleType", roleType);
context.put("partyRoleTypeId", roleType);

attrEntityAssoc = EntityQuery.use(delegator).from("AttrEntityAssoc").where("domainEntityType", domainEntityType).queryFirst();
if(UtilValidate.isNotEmpty(attrEntityAssoc)){
	context.put("attrEntityAssoc", attrEntityAssoc);
	if(UtilValidate.isEmpty(groupingCodeId)){
		groupingCodeId = org.groupfio.common.portal.util.UtilAttribute.getAttrFieldValue(delegator, UtilMisc.toMap("attrEntityAssoc", attrEntityAssoc, "domainEntityType", domainEntityType, "domainEntityId", domainEntityType == "ACTIVITY" ? workEffortId : domainEntityId, "customFieldId", "ASSIGN_ATTR_GCODE"));
	}
}

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleType));
conditionsList.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "CUSTOM_FIELD"));
conditionsList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, "Y")
	                    ));
conditionsList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("hide", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("hide", EntityOperator.EQUALS, "N")
	                    ));	 
	                    
if(UtilValidate.isNotEmpty(groupingCodeId)){
	conditionsList.add(EntityCondition.makeCondition("groupingCode", EntityOperator.LIKE, "%"+groupingCodeId+"%"));
}	                                       
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
println("GetAttribute mainConditons: "+mainConditons);
println("GetAttribute START: "+UtilDateTime.nowTimestamp());	
fieldRoleConfigList = from("CustomFieldRoleConfigSummary").where(mainConditons).orderBy("sequence").queryList();	

List<String> groupNameLst = EntityUtil.getFieldListFromEntityList(fieldRoleConfigList, "groupName", true);
println("groupNameLst: "+groupNameLst);
context.put("groupNameLst", groupNameLst);

List<Map<String, Object>> groupList = new ArrayList();
for (GenericValue entity : fieldRoleConfigList) {
	data = new LinkedHashMap<String, Object>();
	data.put("groupName", entity.getString("groupName"));
	data.put("groupId", entity.getString("groupId"));
	data.put("groupCodeId", entity.getString("customFieldGroupingCodeId"));
	groupList.add(data);
}
println("groupList: "+groupList);
context.put("groupList", groupList);


List<String> groupingCodeLst = EntityUtil.getFieldListFromEntityList(fieldRoleConfigList, "groupingCode", true);
conditionsList = [];
//conditionsList.add(EntityCondition.makeCondition("customFieldGroupingCodeId", EntityOperator.IN, groupingCodeLst));
conditionsList.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "CUSTOM_FIELD"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
groupingCodeLst = from("CustomFieldGroupingCode").where(mainConditons).orderBy("sequenceNumber").queryList();
context.put("groupingCodeLst", org.fio.homeapps.util.DataHelper.getDropDownOptions(groupingCodeLst, "customFieldGroupingCodeId", "description", 0, false));


inputContext = context.get("inputContext");
println("from getAttribute, inputContext:"+inputContext);

context.put("isEnableProgramAct", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_PROG_ACT", "N"));

if (UtilValidate.isNotEmpty(inputContext) && UtilValidate.isNotEmpty(inputContext.get("activityId"))) {
	context.put("isProgAct", org.fio.homeapps.util.UtilActivity.getActivityAttrValue(delegator, inputContext.activityId, "IS_PROG_ACT"));	
}
	
if(UtilValidate.isNotEmpty(context.get("isProgAct")) && context.get("isProgAct").equals("Y")) {
	Map<String, Object> fieldConfig = org.groupfio.common.portal.util.UtilAttribute.getConfiguredFields (delegator, UtilMisc.toMap("groupList", groupList, "srNumber", inputContext.domainEntityId1, "activityDate", inputContext.estimatedStartDate));
	context.put("fieldConfig", fieldConfig);
	context.put("groupConfig", org.groupfio.common.portal.util.UtilAttribute.getConfiguredGroups (delegator, UtilMisc.toMap("groupList", groupList, "srNumber", inputContext.domainEntityId1)));
}