import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.GenericValue;
import org.fio.crm.util.DataHelper;
import org.fio.crm.util.DataUtil
import org.fio.crm.util.LoginFilterUtil
import org.fio.crm.util.ResponseUtils;
import org.fio.crm.util.VirtualTeamUtil;
import org.fio.crm.party.PartyHelper;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import javolution.util.FastList;

partyId = parameters.get("partyId");
companyName = parameters.get("companyName");
location = parameters.get("location");

delegator = request.getAttribute("delegator");
context.put("partyId", partyId);
context.put("companyName", companyName);
context.put("location", location);

leadSourceList = new LinkedHashMap<String, Object>();

sourceList = delegator.findByAnd("PartyIdentificationType", UtilMisc.toMap("parentTypeId", "LEAD_SOURCE"), UtilMisc.toList("partyIdentificationTypeId"), false);
sourceList.each{ source ->
	leadSourceList.put(source.getString("partyIdentificationTypeId"), "("+source.getString("partyIdentificationTypeId")+") "+source.getString("description"));
}
context.put("leadSourceList", leadSourceList);

cityList = new ArrayList();
//cityList = new LinkedHashMap<String, Object>();
indiaStateList = delegator.findByAnd("GeoAssocSummary", UtilMisc.toMap("geoId", userLogin.getString("countryGeoId"), "geoAssocTypeId", "REGIONS"), null, false);
indiaStateList.each{ state ->
	cityAssocList = delegator.findByAnd("GeoAssocSummary", UtilMisc.toMap("geoId", state.getString("geoIdTo"), "geoAssocTypeId", "COUNTY_CITY"), null, false);
	cityList.addAll(cityAssocList);
	//cityList.put((String)cityAssocList[0].get("geoCode"), cityAssocList[0].get("geoName"));
}
def cityListOrder = cityList.sort{it["geoName"]};
context.put("cityList", DataHelper.getDropDownOptions(cityListOrder, "geoCode", "geoName"))
//context.put("cityList", cityListOrder) 

leadStatusList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "LEAD_STATUS"), UtilMisc.toList("sequenceId"), false);
context.put("leadStatusList", DataHelper.getDropDownOptions(leadStatusList, "enumCode", "description"))
//context.put("leadStatusList", leadStatusList) 

subStatusConditionList = FastList.newInstance();
subStatusConditionList.add(EntityCondition.makeCondition(
				[EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CALL_LOGS"),
					  EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "HOW_MEETING_GO")],
					  EntityOperator.OR));//OPEN_ACCOUNT,DROPPED_LEAD,CUSTOMER_INTRESTED
//subStatusConditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("enumTypeId", EntityOperator.NOT_EQUALS,  
EntityCondition subStatusConditons = EntityCondition.makeCondition(subStatusConditionList, EntityOperator.AND);
leadSubStatusList = delegator.findList("Enumeration", subStatusConditons, null, null, null, false);
//leadSubStatusList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "LEAD_STATUS"), UtilMisc.toList("sequenceId"), false);
context.put("leadSubStatusList", DataHelper.getDropDownOptions(leadSubStatusList, "enumId", "description"))
//context.put("leadSubStatusList", leadSubStatusList);
Map numberCountOCL = new LinkedHashMap();
for (int i = 1; i<= 100; i++) {
    numberCountOCL.put(Integer.toString(i), Integer.toString(i));
}
context.put("numberCountOCL", numberCountOCL);

exportTypeList = new LinkedHashMap<String, Object>();

exportTypeList.put("CSV", "CSV");
exportTypeList.put("EXCEL", "Excel");

context.put("exportTypeList", exportTypeList);

exportFieldList = delegator.findByAnd("ExportField", UtilMisc.toMap("countryCode", userLogin.getString("countryGeoId"), "exportFieldType", "LEAD_EXPORT"), UtilMisc.toList("sequenceNumber"), false);
context.put("exportFieldList", DataHelper.getDropDownOptions(exportFieldList, "description", "description"));
//context.put("exportFieldList", exportFieldList);

userLoginRole = FastList.newInstance();
userLoginRole.add( EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")) );
userLoginRole.add(EntityCondition.makeCondition([EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "DBS_CENTRAL"),
                      EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "DBS_SG_SMO_SCG")], 
                      EntityOperator.OR));
EntityCondition mainRoleConditons = EntityCondition.makeCondition(userLoginRole, EntityOperator.AND);
userLoginGroupList = delegator.findList("UserLoginSecurityGroup", mainRoleConditons, UtilMisc.toSet("userLoginId"), null, null, false);
if(UtilValidate.isNotEmpty(userLoginGroupList)){
    context.put("displayFields","Y");
}else{
  context.put("displayFields","N");
}

leadAssignedToConditionList = FastList.newInstance();
leadAssignedToList = []
leadAssignedToConditionList.add(EntityCondition.makeCondition(
		[EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "DBS_RM"),
			EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "DBS_BH"),
			EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "DBS_SH"),
			EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "DBS_RH"),
			EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "DBS_CENTRAL")], 
                      EntityOperator.OR));
//leadAssignedToConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));
EntityCondition leadAssignedToConditions = EntityCondition.makeCondition(leadAssignedToConditionList, EntityOperator.AND);
leadAssignedToPartyList = delegator.findList("PartyRole", leadAssignedToConditions, UtilMisc.toSet("partyId"), null, null, false);
if(leadAssignedToPartyList != '') {
	//leadAssignedToPartyEnabledList = delegator.findByAnd("Party", UtilMisc.toMap("partyId", leadAssignedToPartyList.getString("partyId"), "statusId", "PARTY_ENABLED"), null, false);
    //if(leadAssignedToPartyEnabledList != '') {
		for (GenericValue partyRoleIds : leadAssignedToPartyList) {
	        leadAssignedTo = [:];
	        String partyId = partyRoleIds.getString("partyId");
	        leadAssignedTo.put("partyId",partyId);
	        person = from("Person").where("partyId", partyId).queryOne();
	        if(UtilValidate.isNotEmpty(person)){
		        leadAssignedTo.put("firstName", (person.getString("firstName") !="" ? person.getString("firstName") + " " : "") + person.getString("lastName"));
		        //leadAssignedTo.put("lastName",person.getString("lastName"));
		        leadAssignedToList.add(leadAssignedTo);
	        }
	    }
    //}
}//new LinkedHashMap<String, Object>();DataHelper.getDropDownOptions(exportFieldList, "exportFieldName", "description")
leadAssignedToList = leadAssignedToList.sort{it["firstName"]};
context.put("leadAssignedToList", DataHelper.getDropDownOptionsFromMap(leadAssignedToList, "partyId", "firstName"));
//context.put("leadAssignedToList", leadAssignedToList);

userManagerConditionList = FastList.newInstance();
userManagerList = []
/*userManagerConditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "DBS_TL"));
EntityCondition userManagerConditions = EntityCondition.makeCondition(userManagerConditionList, EntityOperator.AND);
userManagerPartyList = delegator.findList("PartyRole", userManagerConditions, UtilMisc.toSet("partyId"), null, null, false);*/
findOptions = new EntityFindOptions();
findOptions.setDistinct(true);

userManagerConditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT_TEAM"));
userManagerConditionList.add(EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, "VT_SG_Tl"));
EntityCondition userManagerConditions = EntityCondition.makeCondition(userManagerConditionList, EntityOperator.AND);
userManagerPartyList = delegator.findList("PartyRelationship", userManagerConditions, UtilMisc.toSet("partyIdTo"), null, findOptions, false);
if(userManagerPartyList != '') {
	for (GenericValue partyRoleIds : userManagerPartyList) {
		userManager = [:];
		String partyId = partyRoleIds.getString("partyIdTo");
		userManager.put("partyId",partyId);
		person = from("Person").where("partyId", partyId).queryOne();
		if(UtilValidate.isNotEmpty(person)){
			userManager.put("firstName",(person.getString("firstName") !="" ? person.getString("firstName") + " " : "") + person.getString("lastName"));
			//userManager.put("lastName",person.getString("lastName"));
			userManagerList.add(userManager);
		}
	}
}//new LinkedHashMap<String, Object>();DataHelper.getDropDownOptions(exportFieldList, "exportFieldName", "description")
context.put("userManagerList", DataHelper.getDropDownOptionsFromMap(userManagerList, "partyId", "firstName"));
//context.put("userManagerList", userManagerList);

rmMap = [:];

fullAdminAccess = false;
if(LoginFilterUtil.checkEmployeePosition(delegator, userLogin.getString("partyId"))) {
 if (VirtualTeamUtil.isVirtualTeamMember(delegator, null, userLogin.getString("partyId"))) {
    List<Map<String, Object>> virtualTeamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, null, userLogin.getString("partyId"));
    if (virtualTeamMemberList != null && virtualTeamMemberList.size()> 0) {
        Set<String> virtualTeamIdAsLeadList = VirtualTeamUtil.getVirtualTeamIds(virtualTeamMemberList, "VT_SG_TL", true);
        if (virtualTeamIdAsLeadList != null && virtualTeamIdAsLeadList.size()> 0) {
        	
        	List<Map<String, Object>> memberList = new ArrayList<Map<String, Object>>();
        	for (String vtId : virtualTeamIdAsLeadList) {
                memberList.addAll(VirtualTeamUtil.getVirtualTeamMemberList(delegator, vtId, null));
            }
            
            context.put("rmLists", DataHelper.getDropDownOptionsFromMap(memberList, "virtualTeamMemberId", "virtualTeamMemberName"));
            
        }
    }

 } else {
    Map<String, Object> dataSecurityMetaInfo = (Map<String, Object> ) session.getAttribute("dataSecurityMetaInfo");
    if (dataSecurityMetaInfo != null) {
    	List<Map<String, Object>> memberList = new ArrayList<Map<String, Object>>();
        List<String> lowerPositionPartyIds = (List<String> ) dataSecurityMetaInfo.get("lowerPositionPartyIds");
        for (String lowerPositionPartyId : lowerPositionPartyIds) {
        	Map<String, Object> member = new HashMap<String, Object>();
        	member.put("partyId", lowerPositionPartyId);
        	member.put("partyName", PartyHelper.getPartyName(delegator, lowerPositionPartyId, false));
        	memberList.add(member);
        }
        
        context.put("rmLists", DataHelper.getDropDownOptionsFromMap(memberList, "partyId", "partyName"));
        
    }
 }
} else {
	fullAdminAccess = true;
	List<GenericValue> virtualTeamMemberList = VirtualTeamUtil.getVirtualTeamList(delegator, null);
	
	context.put("virtualTeamMemberList", DataHelper.getDropDownOptions(virtualTeamMemberList, "partyId", "groupName"));
}
context.put("fullAdminAccess", fullAdminAccess);
context.put("virtualTeamList", DataHelper.getDropDownOptions(VirtualTeamUtil.getVirtualTeamList(delegator, userLogin.getString("countryGeoId"), userLogin.getString("partyId")), "partyId", "groupName"));
