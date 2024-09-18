import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.fio.crm.opportunities.UtilOpportunity;

userLogin = request.getAttribute("userLogin");
context.put("fromPartyId", userLogin.getString("partyId"));

// translate the partyId to internalPartyId if we came from, say, an account or contact
if (request.getParameter("partyId") != null) {
    context.put("internalPartyId", request.getParameter("partyId"));
}

// default the started time as now
context.put("actualStartDate", org.ofbiz.base.util.UtilDateTime.nowTimestamp());

String workEffortPurposeTypeId = request.getParameter("workEffortPurposeTypeId");

if(UtilValidate.isEmpty(workEffortPurposeTypeId)) {
    workEffortPurposeTypeId = "WEPT_TASK_PHONE_CALL";
}
context.put("workEffortPurposeTypeId", workEffortPurposeTypeId);
if("WEPT_TASK_EMAIL".equals(workEffortPurposeTypeId)){
logTask="Log Email";
context.put("logTask",logTask);
}
else if("WEPT_TASK_PHONE_CALL".equals(workEffortPurposeTypeId)){
logTask="Log Call";
context.put("logTask",logTask);
}
//@End


/*partyAccount=delegator.findOne("PartyRole",UtilMisc.toMap("partyId",parameters.get("partyId"),"roleTypeId","ACCOUNT"), false);
if(partyAccount!=null)
{
    context.put("partyAccount",partyAccount);
}

salesOpportunityId = parameters.get("salesOpportunityId");
opportunity = delegator.findOne("SalesOpportunity", UtilMisc.toMap("salesOpportunityId", salesOpportunityId), false);
accountPartyId = null;
leadPartyId = null;
logCallPartyId = null;
if(opportunity!=null){
    accountPartyId = UtilOpportunity.getOpportunityAccountPartyId(opportunity);
    leadPartyId = UtilOpportunity.getOpportunityLeadPartyId(opportunity);
}
if(leadPartyId!=null){
    logCallPartyId=leadPartyId;
} else if(accountPartyId!=null){
    logCallPartyId=accountPartyId;
}

context.put("logCallPartyId",logCallPartyId);*/