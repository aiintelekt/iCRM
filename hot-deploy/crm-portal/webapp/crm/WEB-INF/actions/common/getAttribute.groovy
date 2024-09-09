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


import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.base.util.UtilDateTime;

exprBldr = new org.ofbiz.entity.condition.EntityConditionBuilder();

partyId = parameters.get("partyId");

currentRole = request.getParameter("requestUri");
if(UtilValidate.isEmpty(currentRole)) {
	currentRole = request.getRequestURI();
}
roleType = "CONTACT";
if(currentRole.contains("viewLead")){
	roleType="LEAD"
}else if(currentRole.contains("viewAccount")){
	roleType="ACCOUNT"
}/*else{
	roleType="CONTACT"
	
}*/
    gid = null;
	fieldRoleConfig = from("CustomFieldRoleConfig").where("roleTypeId", roleType,"groupId", gid).queryList();	
	List customFieldListsToView=new ArrayList();
	List groupNameLists=new ArrayList();
	if( fieldRoleConfig != null && fieldRoleConfig.size()>0 ){
		for(GenericValue fieldRoleConfigLists : fieldRoleConfig){
			customFieldId = fieldRoleConfigLists.getString("customFieldId");
			if(UtilValidate.isNotEmpty(customFieldId)){
				customField = from("CustomField").where("customFieldId", customFieldId, "groupType","CUSTOM_FIELD").orderBy("sequenceNumber").queryList();				
				
				if( customField !=null && customField.size()>0 ){
					Map paramValue = FastMap.newInstance();
					paramValue.put("campaignName",customField);
					customFieldListsToView.add(paramValue);
					
					for(GenericValue cf :customField){
						groupId = cf.getString("groupId");
						groupNameLists.add(groupId);
					
					}
				}
			}
		}
	}
	List groupAndExprs = [];
	List groupLst = [];
	groupAndExprs.add(EntityCondition.makeCondition("hide", EntityOperator.NOT_EQUAL, "Y"));
	groupAndExprs.add(EntityCondition.makeCondition("groupId", EntityOperator.IN, groupNameLists));	
	List group = from("CustomFieldGroup").where(groupAndExprs).orderBy("sequence").queryList()
	for(GenericValue gp :group){
		groupName = gp.getString("groupName");
		groupId = gp.getString("groupId");
		groupLst.add(groupName);
	
	}
	context.put("templatePartyAttributes", fieldRoleConfig);
	context.put("groupList",groupLst);
	context.put("partyId", partyId);
	context.put("roleType", roleType);
	

