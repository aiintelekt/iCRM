import java.sql.Timestamp

import javax.servlet.http.HttpSession;

import org.fio.crm.party.PartyHelper;
import org.fio.homeapps.util.DataUtil
import org.fio.homeapps.util.EnumUtil
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;

uiLabelCommonMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);
String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

String workEffortId = request.getParameter("workEffortId");

loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserId", userLogin.getString("userLoginId"));
context.put("loggedUserPartyId", loggedUserPartyId);

String userLoginId = userLogin.getString("partyId");

inputContext = context.get("inputContext");

isWorkflowActivity = "N";

isApprovalEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPROVAL_ENABLED");

if(UtilValidate.isEmpty(isApprovalEnabled) || isApprovalEnabled.equals("Y")){

	GenericValue approval = EntityQuery.use(delegator).from("WorkEffortApproval").where("workEffortId", workEffortId).queryFirst();
	if(UtilValidate.isNotEmpty(approval)){
		context.put("workflowActivityId", approval.getString("parentWorkEffortId"));
		context.put("workflowCategoryId", approval.getString("approvalCategoryId"));
		context.put("agreementId", approval.getString("domainEntityId"));
		
		String approvalCategoryId = "APVL_CAT_REBATE,APVL_CAT_PAYOUT";
		if (approval.getString("approvalCategoryId").equals("APVL_CAT_3PL_INV")) {
			approvalCategoryId = "APVL_CAT_3PL_INV";
		}
		context.put("approvalCategoryId", approvalCategoryId);
		
		parentApproval = EntityQuery.use(delegator).from("WorkEffortApproval").where("workEffortId", approval.getString("parentWorkEffortId")).queryFirst();
		if(UtilValidate.isNotEmpty(parentApproval)){
			context.put("approvalDescription", parentApproval.getString("approvalComments"));	
		}
			
		if(UtilValidate.isNotEmpty(approval.getString("domainEntityType")) && approval.getString("domainEntityType").equals("REBATE")){
			agreement = from("Agreement").where("agreementId", approval.getString("domainEntityId")).queryOne();
			if(UtilValidate.isNotEmpty(agreement)){
				agreementYear = org.groupfio.rebate.service.util.AgreementUtil.getAgreementAttrValue(delegator, approval.getString("domainEntityId"), "AGREEMENT_YEAR");
				if (UtilValidate.isNotEmpty(agreement.getString("partyIdFrom")) && UtilValidate.isEmpty(agreementYear)) {
					agreementYear=org.fio.homeapps.util.UtilDateTime.getYear(agreement.get("fromDate"), TimeZone.getDefault(), Locale.getDefault());
				}
				context.put("agreementYear", agreementYear);
				context.put("rebatePartyIdTo", agreement.getString("partyIdTo"));
			}
			context.put("agreementReportUrl",org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "AGREE_RPT_URL"));
		} 
		
		partyApproval = org.groupfio.approval.portal.util.DataUtil.getPartyApproval(delegator, org.ofbiz.base.util.UtilMisc.toMap("parentWorkEffortId", approval.getString("parentWorkEffortId"), "partyId", loggedUserPartyId, "approvalCategoryId", approvalCategoryId, "domainEntityType", "REBATE", "domainEntityId", approval.getString("domainEntityId")));
		context.put("partyApproval", partyApproval);
		
		conditionsList = [];
		conditionsList.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "DECISION_STATUS"));
		conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
		if (UtilValidate.isNotEmpty(partyApproval) && UtilValidate.isNotEmpty(partyApproval.accessLevel) && partyApproval.accessLevel=='ACCESS_L1') {
			conditionsList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList('DECISION_ENQUIRY')));
		}
		mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
		statusList = delegator.findList("StatusItem", mainConditons, null, UtilMisc.toList("sequenceId DESC"), null, false);
		context.put("decisionStatusList", org.fio.homeapps.util.DataHelper.getDropDownOptions(statusList, "statusId", "description"));
		
		if (approval.getString("approvalCategoryId").equals("APVL_CAT_PAYOUT")) {
			String payoutMetaData = org.fio.homeapps.util.UtilActivity.getActivityAttrValue(delegator, approval.getString("parentWorkEffortId"), "PAYOUT_META_DATA");
			if(UtilValidate.isNotEmpty(payoutMetaData)) {
				JSONObject jsonObj = JSONObject.fromObject(payoutMetaData);
				Map metaData = org.fio.homeapps.util.ParamUtil.jsonToMap(jsonObj);
				if(UtilValidate.isNotEmpty(metaData)) {
					context.put("payoutMetaData", metaData);
				}
			}
		}	
		
		String activityRoleType = "ACTIVITY";
		if (UtilValidate.isNotEmpty(context.get("workflowCategoryId")) && context.get("workflowCategoryId").equals("APVL_CAT_PAYOUT")) {
			activityRoleType="ACTIVITY_PAYOUT";
		} else if (UtilValidate.isNotEmpty(context.get("workflowCategoryId")) && context.get("workflowCategoryId").equals("APVL_CAT_3PL_INV")) {
			activityRoleType="ACTIVITY_3PLINV";
		}
		context.put("activityRoleType", activityRoleType);
			
		isWorkflowActivity = "Y";
	} else if(UtilValidate.isNotEmpty(inputContext) && UtilValidate.isNotEmpty(inputContext.workEffortPurposeTypeId) && inputContext.workEffortPurposeTypeId.equals("WEPT_INV")){
		isWorkflowActivity = "Y";
		context.put("workflowActivityId", workEffortId);
		context.put("workflowCategoryId", "APVL_CAT_3PL_INV");
		context.put("activityRoleType", "ACTIVITY_3PLINV");
	}

}
//println("workEffortPurposeTypeId> "+inputContext.workEffortPurposeTypeId);

context.put("isWorkflowActivity", isWorkflowActivity);
context.put("isEnableBasicBar", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_ENABLE_ACTIVITY_BASIC_BAR", "N"));

