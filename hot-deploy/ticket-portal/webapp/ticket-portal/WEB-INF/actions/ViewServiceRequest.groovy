import java.sql.ResultSet
import java.sql.Timestamp;
import java.text.SimpleDateFormat

import org.fio.homeapps.constants.GlobalConstants.DateTimeTypeConstant
import org.fio.homeapps.util.DataUtil
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

delegator = request.getAttribute("delegator");
userLogin = request.getAttribute("userLogin");

uiLabelMap = UtilProperties.getResourceBundleMap("lead-portalUiLabels", locale);

inputContext = new LinkedHashMap<String, Object>();
Map<String, Object> actionBarContext = new LinkedHashMap<String, Object>();
String partyName= "";
partyId = request.getParameter("partyId");
String isView = context.get("isView");
activeTab = request.getAttribute("activeTab");
context.activeTab = activeTab;

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "SR_STATUS_ID"));
conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
statusList = delegator.findList("StatusItem", mainConditons, null, UtilMisc.toList("sequenceId"), null, false);
//context.put("srStatusList", org.fio.homeapps.util.DataHelper.getDropDownOptions(statusList, "statusId", "description"));
context.put("srStatusList", statusList);

partySummary = from("PartySummaryDetailsView").where("partyId", partyId).queryOne();

Map<String, Object> appBarContext = new HashMap<String, Object>();
if(partyId!=null){
	primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
	if(UtilValidate.isNotEmpty(primaryContactInformation)) {
		appBarContext.put("primaryEmail",primaryContactInformation.get("EmailAddress"));
		appBarContext.put("emailSolicitation",primaryContactInformation.get("emailSolicitation"));
		appBarContext.put("primaryPhone",primaryContactInformation.get("PrimaryPhone"));
		appBarContext.put("phoneSolicitation",primaryContactInformation.get("phoneSolicitation"));

	}
}

if(partySummary!=null && partySummary.size()>0){
	context.put("partySummary", partySummary);
	inputContext.putAll(partySummary.getAllFields());
	partyId_desc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyId, false);
	inputContext.put("partyId_desc",partyId_desc );
	appBarContext.put("name", partySummary.get("groupName"));
}



String srNumber = request.getParameter("srNumber");
if(UtilValidate.isNotEmpty(srNumber)) {
	GenericValue isAltSrActive = org.fio.homeapps.util.DataUtil.pretailLoyaltyGlobalParameters(delegator,org.fio.homeapps.constants.GlobalConstants.SR_ALT);
	if(UtilValidate.isNotEmpty(isAltSrActive) && UtilValidate.isNotEmpty(isAltSrActive.getString("isActive")) &&
	isAltSrActive.getString("isActive").equalsIgnoreCase("Y") &&UtilValidate.isNotEmpty(isAltSrActive.getString("parameterId"))) {
		String altSR=org.fio.homeapps.util.DataUtil.getCustRequestAttrValue(delegator,isAltSrActive.getString("parameterId"),srNumber);
		if(UtilValidate.isNotEmpty(altSR)) {
			context.put("relatedOldSrId",srNumber);
			srNumber=altSR;
		}
	}
}
context.put("srNumber", srNumber);

String copyFlag = request.getParameter("copy");

custRequestSrSummary = from("CustRequestSrSummary").where("custRequestId", srNumber).queryOne();
PartyId ="";
custRequest = from("CustRequest").where("custRequestId", srNumber).queryOne();
if(UtilValidate.isNotEmpty(custRequest)){
	PartyId = custRequest.fromPartyId;

	context.put("mainAssocPartyId", PartyId);
	custReqDocumentNum = custRequest.custReqDocumentNum;

	srStatusId = custRequest.statusId;
	context.put("srStatusId", srStatusId)

	if(UtilValidate.isNotEmpty(custReqDocumentNum)){
		inputContext.put("sourceDocumentId", custReqDocumentNum	);
		context.put("sourceDocumentId", custReqDocumentNum);
	}else{
		inputContext.put("sourceDocumentId", srNumber);
		context.put("sourceDocumentId", srNumber);
	}

	inputContext.put("sourceComponent", "Service Request");
	context.put("sourceComponent", "Service Request");

}
if(UtilValidate.isNotEmpty(srNumber)){
	attendeesconditions = EntityCondition.makeCondition([
		EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, srNumber),
		EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
		EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "N"),
		EntityUtil.getFilterByDateExpr()
	],
	EntityOperator.AND);

	CustRequestContactPartyAssignmentList = delegator.findList("CustRequestContact", attendeesconditions, null, null, null, false);

	if(UtilValidate.isNotEmpty(CustRequestContactPartyAssignmentList)){
		optionalAttendeeParties = EntityUtil.getFieldListFromEntityList(CustRequestContactPartyAssignmentList, "partyId", false);
		if(UtilValidate.isNotEmpty(optionalAttendeeParties)){
			optionalAttendeesDesc = "";
			optionalAttendeeParties.each { eachOptId ->
				optAttndName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, eachOptId, false);
				optionalAttendeesDesc += optAttndName+",";
			}
			inputContext.put("optionalAttendees", optionalAttendeesDesc.substring(0, optionalAttendeesDesc.length()-1));

		}
	}
}
custRequestSupplementory = from("CustRequestSupplementory").where("custRequestId", srNumber).queryOne();

if(custRequest!=null && custRequest.size()>0){

	if (UtilValidate.isNotEmpty(custRequest.get("description"))){
		String description = custRequest.getString("description");
		if(UtilValidate.isNotEmpty(description) && DataUtil.isBase64(description)) {
			byte[] base64decodedBytes = Base64.getDecoder().decode(description);
			description = new String(base64decodedBytes, "utf-8");
		}
		context.put("description", description);
	}
	if (UtilValidate.isNotEmpty(custRequest.get("resolution"))){

		if(UtilValidate.isNotEmpty(copyFlag) && copyFlag.equals("Y")){
			context.put("resolution", "");

		}else{
			String resolution = custRequest.getString("resolution");
			if(UtilValidate.isNotEmpty(resolution) && DataUtil.isBase64(resolution)) {
				byte[] base64decodedBytes = Base64.getDecoder().decode(resolution);
				resolution = new String(base64decodedBytes, "utf-8");
			}
			context.put("resolution", resolution);
		}
	}

	if (UtilValidate.isNotEmpty(custRequest.get("responsiblePerson"))){
		context.put("ownerUserLoginId", custRequest.get("responsiblePerson"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custRequestTypeId"))){
		context.put("srTypeId", custRequest.get("custRequestTypeId"));
		inputContext.put("srTypeId", custRequest.get("custRequestTypeId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custRequestCategoryId"))){
		context.put("srCategoryId", custRequest.get("custRequestCategoryId"));
		inputContext.put("srCategoryId", custRequest.get("custRequestCategoryId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custRequestSubCategoryId"))){
		context.put("srSubCategoryId", custRequest.get("custRequestSubCategoryId"));
		inputContext.put("srSubCategoryId", custRequest.get("custRequestSubCategoryId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.getString("custReqSrSource"))){
		inputContext.put("srSource", custRequest.getString("custReqSrSource"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custRequestOthCategoryId"))){
		inputContext.put("otherSrSubCategoryId", custRequest.get("custRequestOthCategoryId"));
	}



	if (UtilValidate.isNotEmpty(custRequest.get("responsiblePerson"))){
		ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, custRequest.get("responsiblePerson"), false);
		if (UtilValidate.isNotEmpty(ownerDesc)){
			inputContext.put("ownerDesc", ownerDesc);
		}else{
			inputContext.put("ownerDesc", custRequest.get("responsiblePerson"));
		}

		if (UtilValidate.isNotEmpty(custRequest.get("responsiblePerson"))){
			responsiblePerson = custRequest.get("responsiblePerson");
			GenericValue userLoginLoginGv = delegator.findOne("UserLoginPerson",UtilMisc.toMap("userLoginId", responsiblePerson), false);
			if (UtilValidate.isNotEmpty(userLoginLoginGv)){
				businessUnitVal = userLoginLoginGv.get("businessUnit");

				GenericValue productStoreGroupGv = delegator.findOne("ProductStoreGroup",UtilMisc.toMap("productStoreGroupId", businessUnitVal), false);
				if (UtilValidate.isNotEmpty(productStoreGroupGv)){
					description = productStoreGroupGv.get("description");
					inputContext.put("ownerBu", description);


				}
			}


		}

	}
	if (UtilValidate.isNotEmpty(custRequest)){

		if (UtilValidate.isNotEmpty(custRequest.get("priority"))){
			inputContext.put("priority", custRequest.get("priority"));
		}

		/*if (UtilValidate.isNotEmpty(custRequestSrSummary.get("ownerBuName"))){
		 inputContext.put("ownerBu", custRequestSrSummary.get("ownerBuName"));
		 }*/
	}
	if (UtilValidate.isNotEmpty(custRequestSupplementory.get("accountType"))){
		inputContext.put("accountType", custRequestSupplementory.get("accountType"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("createdByUserLogin"))){
		createdByUserLogin = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, custRequest.get("createdByUserLogin"), false);
		if (UtilValidate.isNotEmpty(createdByUserLogin)){
			inputContext.put("createdByUserLoginDesc", createdByUserLogin);
		}else{
			inputContext.put("createdByUserLoginDesc", custRequest.get("createdByUserLogin"));
		}
	}

	if (UtilValidate.isNotEmpty(custRequest.get("lastModifiedByUserLogin"))){
		lastModifiedByUserLogin = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, custRequest.get("lastModifiedByUserLogin"), false);
		if (UtilValidate.isNotEmpty(lastModifiedByUserLogin)){
			inputContext.put("modifiedByUserLoginDesc", lastModifiedByUserLogin);
		}else{
			inputContext.put("modifiedByUserLoginDesc", custRequest.get("lastModifiedByUserLogin"));
		}
	}

	if (UtilValidate.isNotEmpty(custRequest.get("closedByUserLogin"))){
		closedByUserLogin = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, custRequest.get("closedByUserLogin"), false);
		if (UtilValidate.isNotEmpty(closedByUserLogin)){
			inputContext.put("closedByUserLoginDesc", closedByUserLogin);
		}else{
			inputContext.put("closedByUserLoginDesc", custRequest.get("closedByUserLogin"));
		}
	}

	partyName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, custRequest.get("fromPartyId"), false);
	partySummaryDetails = from("PartySummaryDetailsView").where("partyId", custRequest.get("fromPartyId")).queryOne();
	inputContext.put("cNo_desc", partyName);
	inputContext.put("partyId_desc", partyName);
	inputContext.put("partyName", partyName);

	conditionList = FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, srNumber));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
	conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

	custRequestContactDetails = EntityUtil.getFirst( delegator.findList("CustRequestContact", mainConditons, null, null, null, false) );

	if (UtilValidate.isNotEmpty(custRequestContactDetails) && UtilValidate.isNotEmpty(custRequestContactDetails.getString("partyId"))){
		primaryContactName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, custRequestContactDetails.getString("partyId"), false);
		inputContext.put("primaryContactDesc", primaryContactName);
		inputContext.put("primaryContactDesc_link","/contact-portal/control/viewContact?partyId="+custRequestContactDetails.getString("partyId")+"&externalLoginKey="+externalLoginKey);

	}

	if (UtilValidate.isNotEmpty(custRequest.get("fromPartyId"))){
		inputContext.put("cNo", custRequest.get("fromPartyId"));
	}

	if(UtilValidate.isNotEmpty(isView) && "Y".equals(isView)){
		if (UtilValidate.isNotEmpty(custRequest.get("custRequestCategoryId"))){
			custRequestCategory = from("CustRequestCategory").where("custRequestCategoryId", custRequest.get("custRequestCategoryId")).queryOne();
			if (UtilValidate.isNotEmpty(custRequestCategory) && UtilValidate.isNotEmpty(custRequestCategory.get("description"))){
				inputContext.put("srCategoryId", custRequestCategory.get("description"));
			}
		}

		if (UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(custRequest.get("custRequestSubCategoryId"))){
			custRequestCategory = from("CustRequestCategory").where("custRequestCategoryId", custRequest.get("custRequestSubCategoryId")).queryOne();
			if (UtilValidate.isNotEmpty(custRequestCategory) && UtilValidate.isNotEmpty(custRequestCategory.get("description"))){
				inputContext.put("srSubCategoryId", custRequestCategory.get("description"));
			}
		}

		if (UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(custRequest.get("fromPartyId"))){
			inputContext.put("partyType", org.ofbiz.party.party.PartyHelper.getFirstPartyRoleTypeId(custRequest.getString("fromPartyId"), delegator));
			context.put("fromPartyId", custRequest.get("fromPartyId"));
		}
		if (UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(custRequest.get("custReqOnceDone"))){
			inputContext.put("onceAndDone", custRequest.get("custReqOnceDone"));
		}
		
		List<GenericValue> childSRs = EntityQuery.use(delegator).from("CustRequestSupplementory").where("domainEntityId", srNumber, "domainEntityType","SERVICE").queryList();
		if(UtilValidate.isNotEmpty(childSRs)) {
			List<String> childSrIds = EntityUtil.getFieldListFromEntityList(childSRs, "custRequestId", true);
			if(UtilValidate.isNotEmpty(childSrIds)) {
				List<String> srcDocIds = new LinkedList<String>();
				for(String childSrId : childSrIds) {
					srcDocIds.add(childSrId);
				}
				if(UtilValidate.isNotEmpty(srcDocIds) && srcDocIds.size() > 1) {
					inputContext.put("childSr", org.fio.admin.portal.util.DataUtil.listToString(srcDocIds));
					inputContext.put("childSr_link", "/sr-portal/control/viewServiceRequest?srNumber=");
				} else {
					inputContext.put("childSr", org.fio.admin.portal.util.DataUtil.listToString(srcDocIds));
					inputContext.put("childSr_link", "/sr-portal/control/viewServiceRequest?srNumber="+org.fio.admin.portal.util.DataUtil.listToString(srcDocIds));
				}
			}
		}
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custRequestId"))){
		inputContext.put("custRequestId", custRequest.get("custRequestId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custOrderId"))){
		inputContext.put("orderId", custRequest.get("custOrderId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custRequestName"))){
		inputContext.put("srName", custRequest.get("custRequestName"));
		context.put("srName", custRequest.get("custRequestName"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("statusId"))){
		inputContext.put("srStatusId", custRequest.get("statusId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("tatDays"))){
		context.put("tatDays", custRequest.get("tatDays"));
	} else {
		context.put("tatDays", "0");
	}

	if (UtilValidate.isNotEmpty(custRequest.get("tatHours"))){
		context.put("tatHrs", custRequest.get("tatHours"));
	} else {
		context.put("tatHrs", "0");
	}

	if (UtilValidate.isNotEmpty(custRequest.get("tatMins"))){
		context.put("tatMins", custRequest.get("tatMins"));
	} else {
		context.put("tatMins", "0");
	}

	inputContext.put("openDate", UtilValidate.isNotEmpty(custRequest.get("createdDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("createdDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	inputContext.put("dueDate", UtilValidate.isNotEmpty(custRequestSupplementory.get("commitDate")) ? UtilDateTime.timeStampToString(custRequestSupplementory.getTimestamp("commitDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");

	inputContext.put("createdOn", UtilValidate.isNotEmpty(custRequest.get("createdDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("createdDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	inputContext.put("modifiedOn", UtilValidate.isNotEmpty(custRequest.get("lastModifiedDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("lastModifiedDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");

	inputContext.put("closedOn", UtilValidate.isNotEmpty(custRequest.get("closedByDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("closedByDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");

	inputContext.put("reopenedDate", UtilValidate.isNotEmpty(custRequest.get("reopenedDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("reopenedDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	inputContext.put("reopenedBy", UtilValidate.isNotEmpty(custRequest.get("reopenedBy")) ? custRequest.getString("reopenedBy") : "");

	if (UtilValidate.isNotEmpty(copyFlag) && "Y".equals(copyFlag)){
		inputContext.put("srStatusId", "SR_OPEN");
	}
	context.put("currentSrStatusId", custRequest.get("statusId"));

	actionBarContext.put("sr-id", srNumber);
	actionBarContext.put("name", partyName);
	actionBarContext.put("primaryEmail",partySummaryDetails.get("primaryEmail"));
	actionBarContext.put("primaryPhone",partySummaryDetails.get("primaryContactNumber"));
	appBarContext.put("primaryPhone",partySummaryDetails.get("primaryContactNumber"));
	inputContext.put("primaryPhone",partySummaryDetails.get("primaryContactNumber"));
	context.put("primaryPhone", partySummaryDetails.get("primaryContactNumber"));
	context.put("actionBarContext", actionBarContext);

	String statusId = custRequest.get("statusId");
	println("statusId-->"+statusId);
	String atRisk = "No";
	if(!UtilMisc.toList("SR_CLOSED","SR_CANCELLED").contains(statusId)){
		Timestamp dueDateTimeStamp = custRequestSupplementory.getTimestamp("commitDate");
		Timestamp preEscalationTimeStamp = custRequestSupplementory.getTimestamp("preEscalationDate");
		Timestamp now = UtilDateTime.nowTimestamp();
		if(UtilValidate.isNotEmpty(preEscalationTimeStamp) && UtilValidate.isNotEmpty(dueDateTimeStamp) && now.after(preEscalationTimeStamp) && now.before(dueDateTimeStamp)) {
			atRisk = "Yes";
		}
	}
	inputContext.put("slaRisk", atRisk);
	context.put("slaRisk", atRisk);
	println ("atRisk-->"+atRisk);


	String overDue = "No";
	if(!UtilMisc.toList("SR_CLOSED","SR_CANCELLED").contains(statusId)){
		Timestamp dueDateTimeStamp = custRequestSupplementory.getTimestamp("commitDate");
		Timestamp now = UtilDateTime.nowTimestamp();
		if(UtilValidate.isNotEmpty(dueDateTimeStamp) && now.after(dueDateTimeStamp)) {
			overDue = "Yes";
		}
	}
	inputContext.put("overDueFlag", overDue);
	context.put("overDueFlag", overDue);
	
	List<GenericValue> custRequestAttList = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", srNumber).queryList();
	
	if(UtilValidate.isNotEmpty(custRequestAttList)){
		String locationCustomFieldId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LOC_CF_ID");
	
		for(int i=0 ; i < custRequestAttList.size() ; i++){
			custRequest=custRequestAttList.get(i);
			if("ESTIMATED".equals(custRequest.get("attrName"))){
				inputContext.put("estimated", custRequest.get("attrValue"));
			}else if("CUST_APPROVAL_STATUS".equals(custRequest.get("attrName"))){
				inputContext.put("custApprovalStatus", custRequest.get("attrValue"));
			} else if("BILLED".equals(custRequest.get("attrName"))){
				inputContext.put("billed", custRequest.get("attrValue"));
			} else if("IS_SHOPPED_BEFORE".equals(custRequest.get("attrName"))){
				inputContext.put("isShoppedBefore", custRequest.get("attrValue"));
			} else if(UtilValidate.isNotEmpty(locationCustomFieldId) && locationCustomFieldId.equals(custRequest.get("attrName"))){
				inputContext.put("location", custRequest.get("attrValue"));
			} else if("SR_AMOUNT".equals(custRequest.get("attrName"))){
				context.put("srAmountId", custRequest.get("attrValue"));
				inputContext.put("srAmount", UtilValidate.isNotEmpty(custRequest.get("attrValue")) ? custRequest.get("attrValue") : "");
				inputContext.put("srAmount_desc", UtilValidate.isNotEmpty(custRequest.get("attrValue")) ? custRequest.get("attrValue") : "");
				/*
				if(UtilValidate.isNotEmpty(custRequest.get("attrValue"))) {
					GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldId", custRequest.get("attrValue"),"groupType","CUSTOM_FIELD").queryFirst();
					inputContext.put("srAmount", UtilValidate.isNotEmpty(customField) ? customField.getString("customFieldName") : "");
					inputContext.put("srAmount_desc", UtilValidate.isNotEmpty(customField) ? customField.getString("customFieldName") : "");
				} */
			}
		}
	}


}
inputContext.put("appBarContext", appBarContext);
inputContext.put("actionBarContext", actionBarContext);
//kpi bar data
ResultSet rs = null;
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Map<String, Object> kpiBarContext = new LinkedHashMap<String, Object>();

String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

String srId = "";
String srName = "";
String srType = "";
String srCategory = "";
String srSubCategory = "";
String priority = "";
String srStatus = "";
String srOpenDate = "";
String srDueDate = "";
String srClosedDate = "";
String srClosedBy = "";

if(UtilValidate.isNotEmpty(srNumber)) {

	String srSql = "SELECT CUST_REQUEST_NAME as 'srName', IFNULL(OPEN_DATE_TIME,created_date) as srOpenDate, CLOSED_BY_DATE as 'srClosedDate', CLOSED_BY_USER_LOGIN as 'srClosedBy' FROM `cust_request` WHERE `CUST_REQUEST_ID`='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(srSql);
	if (rs != null) {
		while (rs.next()) {
			srName = rs.getString("srName");
			srOpenDate = rs.getString("srOpenDate");
			srClosedDate = rs.getString("srClosedDate");
			srClosedBy = rs.getString("srClosedBy");
		}
	}

	String srTypeSql = "SELECT b.DESCRIPTION as 'srType' FROM `cust_request` a INNER JOIN cust_request_type b ON a.`CUST_REQUEST_TYPE_ID`=b.CUST_REQUEST_TYPE_ID WHERE a.`CUST_REQUEST_ID`='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(srTypeSql);
	if (rs != null) {
		while (rs.next()) {
			srType = rs.getString("srType");
		}
	}


	String srCategorySql = "SELECT b.DESCRIPTION as 'srCategory' FROM `cust_request` a INNER JOIN `cust_request_category` b ON a.`CUST_REQUEST_CATEGORY_ID`=b.CUST_REQUEST_CATEGORY_ID WHERE a.`CUST_REQUEST_ID`='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(srCategorySql);
	if (rs != null) {
		while (rs.next()) {
			srCategory = rs.getString("srCategory");
		}
	}

	String srSubCategorySql = "SELECT b.DESCRIPTION as 'srSubCategory' FROM `cust_request` a INNER JOIN `cust_request_category` b ON a.`CUST_REQUEST_SUB_CATEGORY_ID`=b.CUST_REQUEST_CATEGORY_ID WHERE a.`CUST_REQUEST_ID`='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(srSubCategorySql);
	if (rs != null) {
		while (rs.next()) {
			srSubCategory = rs.getString("srSubCategory");
		}
	}

	String prioritySql = "SELECT b.DESCRIPTION as 'priority' FROM `cust_request` a INNER JOIN  enumeration b ON a.`PRIORITY`=b.ENUM_ID WHERE a.`CUST_REQUEST_ID`='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(prioritySql);
	if (rs != null) {
		while (rs.next()) {
			priority = rs.getString("priority");
		}
	}

	String srStatusSql = "SELECT b.DESCRIPTION as 'srStatus' FROM `cust_request` a INNER JOIN  STATUS_ITEM b ON a.`STATUS_ID`=b.STATUS_ID WHERE a.`CUST_REQUEST_ID`='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(srStatusSql);
	if (rs != null) {
		while (rs.next()) {
			srStatus = rs.getString("srStatus");
		}
	}

	String srDueDateSql = "SELECT COMMIT_DATE as 'srDueDate' FROM `cust_request_supplementory` WHERE CUST_REQUEST_ID='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(srDueDateSql);
	if (rs != null) {
		while (rs.next()) {
			srDueDate = rs.getString("srDueDate");
		}
	}


	sqlProcessor.close();
}
if(UtilValidate.isNotEmpty(srOpenDate))
	srOpenDate = DataUtil.convertDateTimestamp(srOpenDate, new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
if(UtilValidate.isNotEmpty(srDueDate))
	srDueDate = DataUtil.convertDateTimestamp(srDueDate, new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
if(UtilValidate.isNotEmpty(srClosedDate))
	srClosedDate = DataUtil.convertDateTimestamp(srClosedDate, new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);

kpiBarContext.put("sr-id", srNumber);
kpiBarContext.put("sr-name", srName);
kpiBarContext.put("sr-type", srType);
kpiBarContext.put("sr-category", srCategory);
kpiBarContext.put("sr-sub-category", srSubCategory);
kpiBarContext.put("priority", priority);
kpiBarContext.put("sr-status", srStatus);
kpiBarContext.put("sr-open-date", srOpenDate);
kpiBarContext.put("sr-due-date", srDueDate);
kpiBarContext.put("sr-closed-date", srClosedDate);
kpiBarContext.put("sr-closed-by", srClosedBy);

context.put("kpiBarContext", kpiBarContext);

context.put("custRequestId", srNumber);
context.put("domainEntityId", srNumber);
context.put("partyId", PartyId);
context.put("domainEntityType", "SERVICE_REQUEST");
context.put("requestURI", "viewServiceRequest");


EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, PartyId),
		EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD", "CUSTOMER")));

GenericValue partyRoleData = EntityUtil.getFirst(delegator.findList("PartyRole", searchConditions,null, null, null, false));
roleType = "";
if(UtilValidate.isNotEmpty(partyRoleData)){
	roleType = partyRoleData.roleTypeId
	if(roleType !=null){
		if(roleType.equals("ACCOUNT")){
			inputContext.put("cNo_link","/account-portal/control/viewAccount?partyId="+partyRoleData.partyId+"&externalLoginKey="+externalLoginKey);
		} else if(roleType.equals("LEAD")){
			inputContext.put("cNo_link","/lead-portal/control/viewLead?partyId="+partyRoleData.partyId+"&externalLoginKey="+externalLoginKey);
		} else if(roleType.equals("CUSTOMER")){
			inputContext.put("cNo_link","/customer-portal/control/viewCustomer?partyId="+partyRoleData.partyId+"&externalLoginKey="+externalLoginKey);
		}
	}
}

Map<String, Object> contactAcctMap = new HashMap<String, Object>();
contactAcctMap.put("partyIdTo", PartyId);
contactAcctMap.put("partyRoleTypeId", roleType);
context.put("srFromPartyId", PartyId);
context.put("partyId", PartyId);
context.put("partyRoleTypeId", roleType);

Map<String, Object> result = dispatcher.runSync("common.getContactAndPartyAssoc", contactAcctMap);
partyContactAssocList = [];
String relatedPartiesEmailIds = "";
if(ServiceUtil.isSuccess(result)){
	partyContactAssoc= result.partyContactAssoc

	primContactName = "";
	primContactId = "";

	for(int i=0;i<partyContactAssoc.size();i++){
		Map < String, Object > partyContactMap = new HashMap < String, Object > ();
		partyContactMap = (Map<String, Object>) partyContactAssoc.get(i);
		contactPartyId = partyContactMap.get("partyId")
		custRequestContact = from("CustRequestContact").where("custRequestId", srNumber,"partyId",contactPartyId, "thruDate", null, "roleTypeId", "CONTACT" ).queryList();
		/*primaryContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,contactPartyId);
		if(UtilValidate.isNotEmpty(primaryContactInformation)) {
			if(UtilValidate.isNotEmpty(primaryContactInformation.get("EmailAddress"))) {
				relatedPartiesEmailIds += primaryContactInformation.get("EmailAddress")+",";
			}
		}*/

		if(UtilValidate.isNotEmpty(custRequestContact)){
			isPrimaryContact = custRequestContact.get(0).get("isPrimary");
			if(UtilValidate.isNotEmpty(isPrimaryContact) && isPrimaryContact.equals("Y")){
				partyContactMap.put("statusId", "PARTY_DEFAULT")
				primContactName = partyContactMap.get("name")
				primContactId = partyContactMap.get("partyId")

			}else{
				partyContactMap.put("statusId", "")
			}
			partyContactAssocList.add(partyContactMap);

		}


	}
	context.partyContactAssocList = partyContactAssocList;
	context.primContactName = primContactName;
	context.primContactId = primContactId;
	/*if(UtilValidate.isNotEmpty(relatedPartiesEmailIds)) {
		relatedPartiesEmailIds = relatedPartiesEmailIds.substring(0,relatedPartiesEmailIds.length()-1);
	}
	context.relatedPartiesEmailIds = relatedPartiesEmailIds;*/

}

String loggedInUserEmailId = "";
if(UtilValidate.isNotEmpty(srNumber)){
	if(UtilValidate.isNotEmpty(userLogin) && UtilValidate.isNotEmpty(userLogin.getString("partyId"))) {
		PrimaryContactEmailInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLogin.getString("partyId"),UtilMisc.toMap("isRetriveEmail", true),true);
		if(UtilValidate.isNotEmpty(PrimaryContactEmailInformation)) {
			if(UtilValidate.isNotEmpty(PrimaryContactEmailInformation.get("EmailAddress"))) {
				loggedInUserEmailId = PrimaryContactEmailInformation.get("EmailAddress")+",";
			}
		}
	}
	custRequestContactList = from("CustRequestContact").where("custRequestId", srNumber, "thruDate", null, "roleTypeId", "CONTACT" ).queryList();
	if(UtilValidate.isNotEmpty(custRequestContactList)){
		custRequestContactList.each { eachCustContact ->
			ContactPartyId = eachCustContact.partyId;
			if(UtilValidate.isNotEmpty(ContactPartyId)){
				PrimaryContactEmailInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,ContactPartyId,UtilMisc.toMap("isRetriveEmail", true),true);
				if(UtilValidate.isNotEmpty(PrimaryContactEmailInformation)) {
					if(UtilValidate.isNotEmpty(PrimaryContactEmailInformation.get("EmailAddress"))) {
						if(UtilValidate.isEmpty(loggedInUserEmailId) || (UtilValidate.isNotEmpty(loggedInUserEmailId) && loggedInUserEmailId !=  PrimaryContactEmailInformation.get("EmailAddress")) ){
							relatedPartiesEmailIds += PrimaryContactEmailInformation.get("EmailAddress")+",";
						}
					}
				}
			}
		}
	}
	
	supportEmailAddress = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "TO_EMAIL_ID"), false);
	if (UtilValidate.isNotEmpty(supportEmailAddress) && supportEmailAddress.getString("value")) {
		relatedPartiesEmailIds += supportEmailAddress.getString("value")+",";
	}
}
if(UtilValidate.isNotEmpty(relatedPartiesEmailIds)) {
	relatedPartiesEmailIds = relatedPartiesEmailIds.substring(0,relatedPartiesEmailIds.length()-1);
}
context.relatedPartiesEmailIds = relatedPartiesEmailIds;
context.put("inputContext", inputContext);

context.put("enableCustomCategory",org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_SR_TRIPLET_ENABLED", "Y"));
context.put("isEnableDashboardButton",org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_VIEW_DASHBOARD_BTN_ENABLED", "Y"));
