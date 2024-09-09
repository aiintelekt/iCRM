/**
 * @author Sharif Ul Islam
 *
 */
import java.sql.BatchUpdateException;

import javolution.util.FastList

import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.fio.crm.util.DataHelper;
import org.fio.crm.util.DataUtil;
import org.fio.crm.util.VirtualTeamUtil;
import org.ofbiz.base.util.UtilProperties;
import java.util.LinkedHashMap;

String defaultModelId = UtilProperties.getPropertyValue("Etl-Process", "lead.import.default.modelId");
context.put("defaultModelId", defaultModelId);

if (UtilValidate.isNotEmpty(defaultModelId)) {
	etlModel = EntityUtil.getFirst(delegator.findByAnd("EtlModel", UtilMisc.toMap("modelId", defaultModelId), null, false));
	if (UtilValidate.isNotEmpty(etlModel)) {
		context.put("defaultModelName", etlModel.getString("modelName"));
	}
}

boolean isApprover = false;
/*
security = request.getAttribute("security");
if (security.hasPermission("ETL-IMPDAT-APPROVER", userLogin)) {
    isApprover = true;
}
*/
context.put("isApprover", isApprover);

importFileOptions = new LinkedHashMap<String, Object>();

importFileOptions.put("CSV", "csv");
importFileOptions.put("EXCEL", "excel");
importFileOptions.put("TEXT", "text");
//importFileOptions.put("XML", "xml");
//importFileOptions.put("JSON", "json");

context.put("importFileOptions", importFileOptions);

//GET APPROVED LEADS
 
leadConditions = FastList.newInstance();
leadConditions.add(new EntityExpr("importStatusId", EntityOperator.EQUALS, "DATAIMP_APPROVED"));
/*
if (!isApprover) {
	leadConditions.add(new EntityExpr("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")));
}
*/ 
conditions = new EntityConditionList(leadConditions, EntityOperator.AND);
 
approvedLeads = delegator.findCountByCondition("DataImportLead", conditions, null, null);
 
//GET NOT APPROVED LEADS
 
leadConditions = FastList.newInstance();
leadConditions.add(new EntityExpr("importStatusId", EntityOperator.EQUALS, "DATAIMP_NOT_APPROVED"));
/*
if (!isApprover) {
	leadConditions.add(new EntityExpr("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")));
}
*/ 
conditions = new EntityConditionList(leadConditions, EntityOperator.AND);
 
notApprovedLeads = delegator.findCountByCondition("DataImportLead", conditions, null, null);
 
//GET IMPORTED LEADS
 
leadConditions = FastList.newInstance();
leadConditions.add(new EntityExpr("importStatusId", EntityOperator.EQUALS, "DATAIMP_IMPORTED"));
/*
if (!isApprover) {
	leadConditions.add(new EntityExpr("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")));
}
*/ 
conditions = new EntityConditionList(leadConditions, EntityOperator.AND);
 
importedLeads = delegator.findCountByCondition("DataImportLead", conditions, null, null);
 
//GET ERROR LEADS
 
leadConditions = FastList.newInstance();
statusCondition = EntityCondition.makeCondition([
		EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_ERROR"),
		EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED")
	], EntityOperator.OR);
leadConditions.add(statusCondition);
/*
if (!isApprover) {
	leadConditions.add(new EntityExpr("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")));
}
*/  
conditions = new EntityConditionList(leadConditions, EntityOperator.AND);
println("conditions>>>> "+conditions);  
errorLeads = delegator.findCountByCondition("DataImportLead", conditions, null, null);
	 
context.put("approvedLeads", approvedLeads);
context.put("notApprovedLeads", notApprovedLeads);
context.put("importedLeads", importedLeads);
context.put("errorLeads", errorLeads);


// Upload section [start]

modelTypes = UtilMisc.toMap("DataImportLead", "Lead Model");
context.put("modelTypes", modelTypes);

modelList = new HashMap();
uploadFilter = new HashMap();

modelType = request.getParameter("modelType");
if (UtilValidate.isNotEmpty(modelType)) {
    modelList = DataHelper.getLeadModelList(delegator, modelType);
}
uploadFilter.put("modelType", modelType);

modelId = request.getParameter("modelId");
if (UtilValidate.isNotEmpty(modelId)) {
}
uploadFilter.put("modelId", modelId);

context.put("modelList", modelList);
context.put("uploadFilter", uploadFilter);

// Upload section [end]

context.put("virtualTeamList", DataHelper.getDropDownOptions(VirtualTeamUtil.getVirtualTeamList(delegator, userLogin.getString("countryGeoId"), userLogin.getString("partyId")), "partyId", "groupName"));
