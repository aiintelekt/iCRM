import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import java.util.LinkedHashMap;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("crmUiLabels", locale);

boolean isApprover = false;
/*
security = request.getAttribute("security");
if (security.hasPermission("ETL-IMPDAT-APPROVER", userLogin)) {
    isApprover = true;
}
*/
context.put("isApprover", isApprover);

importStatusList = new LinkedHashMap<String, Object>();

//importStatusList.put("DATAIMP_APPROVED", uiLabelMap.get("dataimpApproved"));
//importStatusList.put("DATAIMP_NOT_APPROVED", uiLabelMap.get("dataimpNotApproved"));
//importStatusList.put("DATAIMP_REJECTED", uiLabelMap.get("dataimpRejected"));
importStatusList.put("DATAIMP_IMPORTED", uiLabelMap.get("leadGenerated"));
importStatusList.put("DATAIMP_ERROR", uiLabelMap.get("dataimpError"));
importStatusList.put("DISABLED", uiLabelMap.get("disabled"));

context.put("importStatusList", importStatusList);

importStatusId = request.getParameter("importStatusId");
leadId = request.getParameter("leadId");

filterLeadBatch = new HashMap();

filterLeadBatch.put("importStatusId", UtilValidate.isNotEmpty(importStatusId) ? importStatusId : null);
filterLeadBatch.put("leadId", leadId);

context.put("filterLeadBatch", filterLeadBatch);

errorCodeList = new LinkedHashMap<String, Object>();
errorCodes = delegator.findByAnd("ErrorCode", UtilMisc.toMap("ErrorCodeType", "LEAD_IMPORT"), UtilMisc.toList("errorCodeId"), false);
errorCodes.each{ errorCode ->
	errorCodeList.put(errorCode.getString("errorCodeId"), "("+errorCode.getString("errorCodeId")+") "+errorCode.getString("codeDescription"));
}
context.put("errorCodeList", errorCodeList);

exportTypeList = new LinkedHashMap<String, Object>();

exportTypeList.put("CSV", "CSV");
exportTypeList.put("EXCEL", "Excel");

context.put("exportTypeList", exportTypeList);
/*
condition = UtilMisc.toMap();

if (UtilValidate.isNotEmpty(importStatusId)) {
	condition.put("importStatusId", importStatusId);
}

cond = EntityCondition.makeCondition(condition);


if (UtilValidate.isNotEmpty(groupName)) {
	EntityCondition nameCondition = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("groupName")), EntityOperator.LIKE, groupName.toUpperCase()+"%");
	cond = EntityCondition.makeCondition([cond,
		nameCondition
	], EntityOperator.AND);
}


leadList = delegator.findList("DataImportLead", cond, null, ["createdStamp"], null, false);
context.put("leadList", leadList);
*/