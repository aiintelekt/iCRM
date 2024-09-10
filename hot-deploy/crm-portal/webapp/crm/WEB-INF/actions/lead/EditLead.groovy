import org.fio.crm.util.DataHelper;
import org.fio.lms.mobile.service.LeadListState
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil
import org.fio.crm.util.PermissionUtil;
import org.fio.crm.party.PartyHelper;

delegator = request.getAttribute("delegator");
state = LeadListState.getInstance(request);
//state.update(request);
context.state = state;
context.hasPermission = true;
userLogin = request.getAttribute("userLogin");
String loginPartyId = userLogin.get("partyId");
// lead Id in case of edit only will be available
// leadId = parameters.leadId;
leadId = UtilValidate.isNotEmpty(request.getParameter("leadId")) ? request.getParameter("leadId") : request.getParameter("partyId");
context.put("leadId", leadId)

String leadName = PartyHelper.getPartyName(delegator, leadId, false);
if (UtilValidate.isEmpty(leadName)) {
	if (UtilValidate.isNotEmpty(dataImportLead.get("firstName"))) {
		leadName = dataImportLead.getString("firstName").concat( UtilValidate.isNotEmpty(dataImportLead.get("lastName")) ? " " + dataImportLead.get("lastName") : "" );
	} else if (UtilValidate.isNotEmpty(dataImportLead.get("firstName"))) {
		leadName = dataImportLead.get("lastName");
	}
}
context.put("leadName", leadName);

actionType = context.get("actionType");
context.put("actionType", actionType);

dataImportLead = new HashMap();

haveDataPermission = "Y";

if (UtilValidate.isNotEmpty(leadId)) {

	cond = EntityCondition.makeCondition([
		EntityCondition.makeCondition("leadId", EntityOperator.EQUALS, leadId),
		EntityCondition.makeCondition("primaryPartyId", EntityOperator.EQUALS, leadId)
	], EntityOperator.OR);

	dataImportLead = EntityUtil.getFirst( delegator.findList("DataImportLead", cond, null, null, null, false) );
	
	//Login Based lead Filter
	String userLoginId = userLogin.getString("userLoginId");
	userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup")
			.where("userLoginId", userLoginId, "groupId", "FULLADMIN").filterByDate().queryList();
	if ((userLoginSecurityGroup == null || userLoginSecurityGroup.size() < 1) && !PermissionUtil.havePartyViewPermission(delegator, session, leadId)) {
		haveDataPermission = "N";
	}
	
}
context.put("dataImportLead", dataImportLead);
println("dataImportLead>>> "+dataImportLead);
context.put("haveDataPermission", haveDataPermission);

// get lead info
leadResults = state.getLeadInfo(delegator, leadId);
context.put("aoAddress",leadResults.get("aoAddress"));
context.put("leadData",leadResults.get("leadData"));
context.put("primaryPhone",leadResults.get("primaryPhone"));
context.put("primaryPhoneMechId",leadResults.get("primaryPhoneMechId"));
context.put("geoMap",leadResults.get("geoMap"));

leadAssignBy = leadResults.get("leadAssignBy");
if(leadAssignBy !=null){
	String assignByName = leadAssignBy.get("firstName") + " " + leadAssignBy.get("lastName")
	context.put("leadAssignBy", leadAssignBy);
	context.put("assignByName",assignByName);
} else{
	def leadAssignBy = [:];
	context.put("leadAssignBy", leadAssignBy);
	context.put("assignByName","");
}

leadAssignTo = leadResults.get("leadAssignTo");
if(leadAssignTo !=null){
	String assignToName = leadAssignTo.get("firstName") + " " + leadAssignTo.get("lastName");
	context.put("leadAssignTo", leadAssignTo);
	context.put("assignToName",assignToName);
} else{
	def leadAssignTo = [:];
	context.put("leadAssignTo", leadAssignTo);
	context.put("assignToName","");
}

leadAssignment = leadResults.get("leadAssignment");
if(leadAssignment != null ){
	context.put("leadAssignment",leadResults.get("leadAssignment"));
} else{
	def leadAssignment = [:];
	leadAssignment.put("partyIdFrom", leadId);
	leadAssignment.put("partyIdTo", loginPartyId);
	context.put("leadAssignment",leadAssignment);
}

partyAttrs = state.getPartyAttrs(delegator, leadId);
context.put("partyAttrs",partyAttrs);

//city
//cityList = delegator.findByAnd("GeoAssocSummary", UtilMisc.toMap("geoTypeId", "CITY"), null, false);
cityList = delegator.findByAnd("Geo", UtilMisc.toMap("geoTypeId", "CITY"), null, false);
context.put("cityList", DataHelper.getDropDownOptions(cityList, "geoId", "geoName"));

//states
cityList = delegator.findByAnd("Geo", UtilMisc.toMap("geoTypeId", "STATE"), null, false);
context.put("stateList", DataHelper.getDropDownOptions(cityList, "geoId", "geoName"));

//constitution
constitutionList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "DBS_CONSTITUTION"), UtilMisc.toList("sequenceId"), false);
context.put("constitutionList", DataHelper.getDropDownOptions(constitutionList, "enumId", "description"));

//lead Source
lead_SourceList = delegator.findByAnd("PartyIdentificationType", UtilMisc.toMap("parentTypeId", "LEAD_SOURCE"), null, false);
context.put("lead_SourceList", DataHelper.getDropDownOptions(lead_SourceList, "partyIdentificationTypeId", "description"));

//designation
designationList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "DBS_LD_DESIGNATION"), UtilMisc.toList("sequenceId"), false);
context.put("designationList", DataHelper.getDropDownOptions(designationList, "enumId", "description"));

//inustry
industryList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "DBS_INDUSTRY"), UtilMisc.toList("sequenceId"), false);
context.put("industryList", DataHelper.getDropDownOptions(industryList, "enumId", "description"));

tcpUserList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "DBS_TALLY_USR_TYPE"), UtilMisc.toList("sequenceId"), false);
context.put("tcpUserList", DataHelper.getDropDownOptions(tcpUserList, "enumId", "description"));

//preferredLanguages
preferredLanguagesList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "PREFERRED_LANGUAGES"), UtilMisc.toList("sequenceId"), false);
context.put("preferredLanguagesList", DataHelper.getDropDownOptions(preferredLanguagesList, "enumId", "description"));

productList = EntityQuery.use(delegator).from("Enumeration").where(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "DBS_LMS_PROD")).orderBy('sequenceId').queryList();
productMap=DataHelper.getDropDownOptions(productList,"enumCode", "description");

//================================bank section ================

existingBankLists = EntityQuery.use(delegator).from("CompanyBank").where(EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, leadId)).orderBy('companyId').queryList();
List banksList = new LinkedList();
for(GenericValue existingBankList : existingBankLists){
	companyBankSeq = existingBankList.getString("companyBankSeqId");
	HashMap companyBankRow = new HashMap();
	companyBankRow.put("bankName",existingBankList.getString("bankName"));
	companyBankRow.put("companyBankSeqId",companyBankSeq);
	List banksProductsList = new LinkedList();
	companyBankProducts = EntityQuery.use(delegator).from("CompanyBankProduct").where(EntityCondition.makeCondition("companyBankSeqId", EntityOperator.EQUALS, companyBankSeq)).orderBy('companyBankSeqId').queryList();
	for(GenericValue companyBankProduct : companyBankProducts){
		HashMap companyBankDetails = new HashMap();
		productId = companyBankProduct.getString("productId");
		companyBankDetails.put("bankName",existingBankList.getString("bankName"));
		companyBankDetails.put("bankId",existingBankList.getString("bankId"));
		companyBankDetails.put("productId", productId);
		companyBankDetails.put("productName", productMap.get(productId));
		if("OTHERS".equals(productId)){
        	companyBankDetails.put("productValue",companyBankProduct.getString("productOthers"));
        	
        }else{
		   companyBankDetails.put("productValue",companyBankProduct.getString("productValue"));
        }
		banksProductsList.add(companyBankDetails);
	}
	companyBankRow.put("banksProductsList",banksProductsList);
	banksList.add(companyBankRow);
}
context.put("banksLists",banksList);