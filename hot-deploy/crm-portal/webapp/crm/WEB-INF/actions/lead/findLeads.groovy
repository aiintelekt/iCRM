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
firstName = parameters.get("firstName");
lastName = parameters.get("lastName");
companyName = parameters.get("companyName");
emailAddress = parameters.get("emailAddress");
contactNumber = parameters.get("contactNumber");
fromCallBackDate = parameters.get("fromCallBackDate");
toCallBackDate = parameters.get("toCallBackDate");
location = parameters.get("location");

delegator = request.getAttribute("delegator");
context.put("partyId", partyId);
context.put("firstName", firstName);
context.put("lastName", lastName);
context.put("companyName", companyName);
context.put("emailAddress", emailAddress);
context.put("contactNumber", contactNumber);
context.put("location", location);
context.put("fromCallBackDate",fromCallBackDate);
context.put("toCallBackDate",toCallBackDate);

leadSourceList = new LinkedHashMap<String, Object>();

sourceList = delegator.findByAnd("PartyIdentificationType", UtilMisc.toMap("parentTypeId", "LEAD_SOURCE"), UtilMisc.toList("partyIdentificationTypeId"), false);
sourceList.each{ source ->
	leadSourceList.put(source.getString("partyIdentificationTypeId"), "("+source.getString("partyIdentificationTypeId")+") "+source.getString("description"));
}
context.put("leadSourceList", leadSourceList);

tallyUserTypeList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "DBS_TALLY_USR_TYPE"), UtilMisc.toList("sequenceId"), false);
context.put("tallyUserTypeList", DataHelper.getDropDownOptions(tallyUserTypeList, "enumId", "description"));

cityList = new ArrayList();

indiaStateList = delegator.findByAnd("GeoAssocSummary", UtilMisc.toMap("geoId", userLogin.getString("countryGeoId"), "geoAssocTypeId", "REGIONS"), null, false);
indiaStateList.each{ state ->
	cityAssocList = delegator.findByAnd("GeoAssocSummary", UtilMisc.toMap("geoId", state.getString("geoIdTo"), "geoAssocTypeId", "COUNTY_CITY"), null, false);
	cityList.addAll(cityAssocList);
}
context.put("cityList", DataHelper.getDropDownOptions(cityList, "geoIdTo", "geoName"));

/*leadStatusList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "LEAD_STATUS_HISTORY"), UtilMisc.toList("sequenceId"), false);
context.put("leadStatusList", DataHelper.getDropDownOptions(leadStatusList, "enumId", "description"));
*/
leadStatusList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "LEAD_STATUS"), UtilMisc.toList("sequenceId"), false);
context.put("leadStatusList", DataHelper.getDropDownOptions(leadStatusList, "enumCode", "description"))



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
context.put("exportFieldList", DataHelper.getDropDownOptions(exportFieldList, "exportFieldName", "description"));
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

roleConditionList = FastList.newInstance();
teamLists = []
roleConditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "DBS_RM"),
                      EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "DBS_TC")], 
                      EntityOperator.OR));

EntityCondition mainConditons = EntityCondition.makeCondition(roleConditionList, EntityOperator.AND);
partyRoleList = delegator.findList("PartyRole", mainConditons, UtilMisc.toSet("partyId"), null, null, false);

if(partyRoleList !='') {
    for (GenericValue partyRoleIds : partyRoleList) {
        teamList = [:];
        String partyId = partyRoleIds.getString("partyId");
        teamList.put("partyId",partyId);
        person = from("Person").where("partyId", partyId).queryOne();
        if(UtilValidate.isNotEmpty(person)){
	        teamList.put("firstName",person.getString("firstName"));
	        teamList.put("lastName",person.getString("lastName"));
	        teamLists.add(teamList);
        }
    }
}
context.put("RMRoleList", teamLists);

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
