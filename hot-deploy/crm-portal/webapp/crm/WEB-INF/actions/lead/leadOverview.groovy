import org.fio.lms.mobile.service.LeadListState
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.fio.crm.util.DataHelper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;

delegator = request.getAttribute("delegator");

state = LeadListState.getInstance(request);
//state.update(request);
context.state = state;
context.hasPermission = true;

userLogin = request.getAttribute("userLogin");

context.put("userLogin", userLogin);
String leadId = request.getParameter("partyId");
context.put("leadId", leadId);

leadSupplGv = EntityQuery.use(delegator).from("PartySupplementalData").where("partyId", leadId).queryOne();
		if(leadSupplGv!=null && leadSupplGv.size() > 0){
			companyName = leadSupplGv.getString("companyName");
			context.put("companyName", companyName);
		}

String msg = request.getParameter("msg");
if(UtilValidate.isEmpty(msg)){
	msg = '';
}

context.msg = msg;


// get lead info
leadResults = state.getLeadInfo(delegator, leadId);
context.put("aoAddress",leadResults.get("aoAddress"));
context.put("leadData",leadResults.get("leadData"));
context.put("daysInQueue",leadResults.get("daysInQueue"));
context.put("callAttempts",leadResults.get("callAttempts"));
context.put("leadScore",leadResults.get("leadScore"));
context.put("leadScore",leadResults.get("leadScore"));
context.put("dropLeadUrl",leadResults.get("dropLeadUrl"));
context.put("primaryPhone",leadResults.get("primaryPhone"));
context.put("primaryPhoneMechId",leadResults.get("primaryPhoneMechId"));
context.put("constitution",leadResults.get("constitution"));
context.put("industry",leadResults.get("industry"));
context.put("tallyUserType",leadResults.get("tallyUserType"));
context.put("preferredLanguages",leadResults.get("preferredLanguages"));
context.put("createSource",leadResults.get("createSource"));
context.put("geoMap",leadResults.get("geoMap"));
primaryLeadAsContact = leadResults.get("primaryLeadAsContact");
if(primaryLeadAsContact !=null){
  context.put("primaryLeadAsContact",leadResults.get("primaryLeadAsContact"));
}else{
  def primaryLeadAsContact = [:];
  context.put("primaryLeadAsContact",primaryLeadAsContact);
}

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
	String assignToPartyId = leadAssignTo.get("partyId");
	GenericValue isOneBankIdExists = EntityUtil.getFirst( delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", assignToPartyId), null, false) );
	assignToPartyId = isOneBankIdExists.getString("userLoginId");
	context.put("assignToPartyId", assignToPartyId);
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
	context.put("leadAssignment",leadAssignment);
}


// get lead contacts only
results = state.getLeadContacts(delegator, leadId);
context.put("leadContacts",results.get("leadContacts"));
context.put("keycontacts",results.get("keycontacts"));
context.put("associates",results.get("associates"));
primaryContact = results.get("primaryContact");
if(primaryContact !=null){
	context.put("primaryContact",results.get("primaryContact"));
} else {
	def primaryContact= [:];
	context.put("primaryContact",results.get("primaryContact"));
}



partyAttrs = state.getPartyAttrs(delegator, leadId);
context.put("partyAttrs",partyAttrs);

products = state.getProductsInterested(delegator, leadId);
context.put("products",products);
//if(product != null){
//	context.put("product",product);
//} else{
//
//	def product = [:];
//	context.put("product",product);
//}

productList = EntityQuery.use(delegator).from("Enumeration").where(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "DBS_LMS_PROD")).orderBy('sequenceId').queryList();
context.put("productList",productList);
productMap=DataHelper.getDropDownOptions(productList,"enumCode", "description");
context.put("productMap",productMap);

 /*********Meeting Logs***********/
meetingLog = state.meetingLogs(delegator, leadId);
context.put("meetingLog",meetingLog);
//================================bank section ================

existingBankLists = EntityQuery.use(delegator).from("CompanyBank").where(EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, leadId)).orderBy('companyId').queryList();
List banksList = new LinkedList();
for(GenericValue existingBankList : existingBankLists){
	companyBankSeq = existingBankList.getString("companyBankSeqId");
	HashMap companyBankRow = new HashMap();
	companyBankRow.put("bankName",existingBankList.getString("bankName"));
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
		companyBankDetails.put("lastUpdatedStamp",companyBankProduct.get("lastUpdatedStamp"));
		banksProductsList.add(companyBankDetails);
	}
	companyBankRow.put("banksProductsList",banksProductsList);
	banksList.add(companyBankRow);
}
context.put("banksLists",banksList);
