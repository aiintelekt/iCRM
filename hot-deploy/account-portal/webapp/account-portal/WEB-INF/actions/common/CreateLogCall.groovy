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
