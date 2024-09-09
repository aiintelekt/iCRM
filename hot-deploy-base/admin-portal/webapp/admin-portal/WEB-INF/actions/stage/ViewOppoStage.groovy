import java.sql.Timestamp
import java.text.SimpleDateFormat

import org.fio.crm.party.PartyHelper;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.Debug;

import javolution.util.FastList;
import java.text.DecimalFormat;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("opportunity-portalUiLabels", locale);

String isView = context.get("isView");

context.put("haveDataPermission", "Y");

inputContext = new LinkedHashMap<String, Object>();

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

println("activeTab>>> "+activeTab);

yesNoMap = ["Y":"Yes", "N":"No"];
context.put("yesNoOptions", yesNoMap);

opportunityStageId = request.getParameter("opportunityStageId");

// account summary data
oppoStageSummary = from("SalesOpportunityStage").where("opportunityStageId", opportunityStageId).queryOne();

println("oppoStageSummary------------>"+oppoStageSummary);

if(UtilValidate.isNotEmpty(oppoStageSummary)){
	context.put("oppoStageSummary", oppoStageSummary);
	
	inputContext.putAll(oppoStageSummary.getAllFields());
	
	// fillup administration info
	inputContext.put("createdOn", UtilValidate.isNotEmpty(oppoStageSummary.get("createdStamp")) ? UtilDateTime.timeStampToString(oppoStageSummary.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	inputContext.put("modifiedOn", UtilValidate.isNotEmpty(oppoStageSummary.get("lastUpdatedStamp")) ? UtilDateTime.timeStampToString(oppoStageSummary.getTimestamp("lastUpdatedStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	inputContext.put("createdBy", oppoStageSummary.get("createdByUserLogin"));
	inputContext.put("modifiedBy", oppoStageSummary.get("lastModifiedByUserLogin"));
	
}

context.put("inputContext", inputContext);

context.put("domainEntityId", opportunityStageId);
context.put("domainEntityType", "OPPO_STAGE");
context.put("requestURI", "viewOppoStage");
