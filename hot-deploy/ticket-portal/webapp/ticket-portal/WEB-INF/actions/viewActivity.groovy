/*
 * Copyright (c) Open Source Strategies, Inc.
 * 
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.sql.Timestamp
import java.text.SimpleDateFormat
import org.apache.commons.codec.binary.Base64;
import org.fio.crm.party.PartyHelper;
import org.groupfio.opportunity.portal.util.DataHelper;
import org.groupfio.opportunity.portal.util.DataUtil;
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

import java.util.List
import java.util.TimeZone;

import javolution.util.FastList;
import org.ofbiz.base.util.Debug;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.fio.campaign.events.AjaxEvents;

String workEffortId = request.getParameter("workEffortId");
String salesOpportunityId = request.getParameter("salesOpportunityId");
security = request.getAttribute("security");
userLogin = request.getAttribute("userLogin");
HttpSession session = request.getSession();
userLoginId = userLogin.getAt('partyId');
delegator = request.getAttribute("delegator");
dispatcher = request.getAttribute("dispatcher");
uiLabelMap = UtilProperties.getResourceBundleMap("TicketPortalUiLabels", locale);

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

String isView = context.get("isView");

context.put("haveDataPermission", "Y");
context.put("hasReassignPermission", true);

String userLoginId = userLogin.getString("partyId");

inputContext = new LinkedHashMap<String, Object>();
appBarContext = new LinkedHashMap<String, Object>();
if(context.get("inputContext")){
	
	inputContext.putAll(context.get("inputContext"));
}
inputContext.put("activityId", workEffortId);
context.put("workEffortId", workEffortId);

workEffort = from("WorkEffort").where("workEffortId", workEffortId).queryOne();

if (UtilValidate.isNotEmpty(workEffort)) {
	
	if(UtilValidate.isEmpty(workEffort.get("salesOpportunityId")))
	{
		workEffort.remove("salesOpportunityId");
		
	}
	inputContext.putAll(workEffort.getAllFields());
	inputContext.put("domainEntityType", org.groupfio.common.portal.util.DataHelper.convertToLabel(workEffort.getString("domainEntityType")));

	String activityId=	workEffort.getString("workEffortId");
	String type=	workEffort.getString("workEffortTypeId");
	String priorityId=	workEffort.getString("priority");
	String srType=	workEffort.getString("workEffortServiceType");
	String srSubtype=	workEffort.getString("workEffortSubServiceType");
	String statusId=	workEffort.getString("currentStatusId");
	String subject=	workEffort.getString("workEffortName");
	String ownerId=	workEffort.getString("primOwnerId");
	String ownerBu=	workEffort.getString("businessUnitId");
	String createdBy=	workEffort.getString("createdByUserLogin");
	Timestamp createdDate=	workEffort.getTimestamp("createdDate");
	String modifiedBy=	workEffort.getString("lastModifiedByUserLogin");
	String closedBy=	workEffort.getString("closedByUserLogin");
	Timestamp modifiedDate=	workEffort.getTimestamp("lastModifiedDate");
	Timestamp closedDateTime=	workEffort.getTimestamp("closedDateTime");
	String partyId=	workEffort.getString("cif");
	String onceDone=	workEffort.getString("wfOnceDone");
	inputContext.put("onceDone",onceDone);
	conditionList = [];
		conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	if (workEffortId) {
		conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	}
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
	
	workEffortContact = EntityUtil.getFirst(from("WorkEffortContact").where(conditionList).queryList());
	String contactId ="";
	String nto="";
	String ncc="";
	String nbcc="";
	String nsender="";
	String nrecepient="";
	String norganizer="";
	String duration="";
	String location="";
	String commEventId="";
	String content="";
	String requiredAttendees="";
	String optionalAttendees = "";
	String template = "";
	Timestamp callDateTime=null;
	if(UtilValidate.isNotEmpty(workEffort.getTimestamp("actualStartDate"))){
		String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("actualStartDate"),"MM/dd/yyyy HH:mm");
		inputContext.put("actualStartDate",workEffort.getTimestamp("actualStartDate"));
	}
	if(UtilValidate.isNotEmpty(workEffort.getTimestamp("actualCompletionDate"))){
		String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("actualCompletionDate"),"MM/dd/yyyy HH:mm");
		inputContext.put("actualCompletionDate",workEffort.getTimestamp("actualCompletionDate"));
	}
	if(UtilValidate.isNotEmpty(workEffort.getTimestamp("estimatedStartDate"))){
		String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("estimatedStartDate"),"MM/dd/yyyy HH:mm");
		inputContext.put("estimatedStartDate",workEffort.getTimestamp("estimatedStartDate"));
		inputContext.put("startTime",dateStr);
	}
	if(UtilValidate.isNotEmpty(workEffort.getTimestamp("estimatedCompletionDate"))){
		String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("estimatedCompletionDate"),"MM/dd/yyyy HH:mm");
		inputContext.put("estimatedCompletionDate",workEffort.getTimestamp("estimatedCompletionDate"));
	}
	if(UtilValidate.isNotEmpty(createdDate)){
		String dateStr = UtilDateTime.toDateString(createdDate,"MM/dd/yyyy");
		inputContext.put("createdDate",dateStr);
	}
	if(UtilValidate.isNotEmpty(modifiedDate)){
		String dateStr = UtilDateTime.toDateString(modifiedDate,"MM/dd/yyyy");
		inputContext.put("modifiedDate",dateStr);
		inputContext.put("modifiedOn",dateStr);
	}
	if(UtilValidate.isNotEmpty(closedDateTime) && UtilValidate.isNotEmpty(closedBy)){
		String dateStr = UtilDateTime.toDateString(closedDateTime,"MM/dd/yyyy");
		inputContext.put("closedDate",dateStr);
		GenericValue UserLoginPerson = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId",closedBy).queryOne();
		if(UtilValidate.isNotEmpty(UserLoginPerson) && UtilValidate.isNotEmpty(UserLoginPerson.getString("partyId"))){
			inputContext.put("closedBy",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, UserLoginPerson.getString("partyId"), false));
		}
	}
	String description=workEffort.getString("description")?workEffort.getString("description"):"";
	if (UtilValidate.isNotEmpty(workEffortContact)) {
		contactId=workEffortContact.get("partyId");
	}

	workEffortSupp = EntityUtil.getFirst(from("WorkEffortSupplementory").where("workEffortId", workEffortId,).queryList());
	if(UtilValidate.isNotEmpty(workEffortSupp)){
		duration=workEffortSupp.getString("wftMsdduration")?workEffortSupp.getString("wftMsdduration"):"";
		location=workEffortSupp.getString("wftLocation")?workEffortSupp.getString("wftLocation"):"";
		template=workEffortSupp.getString("wftMsdsubjecttemplate")?workEffortSupp.getString("wftMsdsubjecttemplate"):"";
		inputContext.put("emailTemplate",template);
		context.put("template",template);
		inputContext.put("location",location);
		inputContext.put("duration",duration);
	}
	
	workEffortCommEvent = from("WorkEffortCommExtension").where("workEffortId", workEffortId).queryList();
	if(UtilValidate.isNotEmpty(workEffortCommEvent)){
		for(GenericValue each:workEffortCommEvent){
			if(each.wftExtType.equals("SENDER_TYPE")){
				nsender=each.wftExtValue;
				inputContext.put("nsender",nsender);
			}else if(each.wftExtType.equals("BCC_TYPE")){
				nbcc=each.wftExtValue;
				inputContext.put("nbcc",nbcc);
			}
			else if(each.wftExtType.equals("CC_TYPE")){
				ncc=each.wftExtValue;
				ncc= ncc.replaceAll(",",", ");
				inputContext.put("ncc",ncc);
			}
			else if(each.wftExtType.equals("TO_TYPE")){
				nto=each.wftExtValue;
				inputContext.put("nto",nto);
			}
			else if(each.wftExtType.equals("ORGANIZER_TYPE")){
				norganizer=each.wftExtValue;
				inputContext.put("norganizer",norganizer);
			}
			else if(each.wftExtType.equals("RECIPIENT_TYPE")){
				nrecepient=each.wftExtValue;
				inputContext.put("nrecepient",nrecepient);
			}
			else if(each.wftExtType.equals("OPTIONAL_TYPE")){
				optionalAttendees=each.wftExtValue;
				inputContext.put("optionalAttendees",optionalAttendees);
				context.put("selectedOAtten", optionalAttendees);
			}
			else if(each.wftExtType.equals("REQUIRED_TYPE")){
				requiredAttendees=each.wftExtValue;
				inputContext.put("requiredAttendees",requiredAttendees);
				context.put("selectedRAtten", requiredAttendees);
			}
		}
	}
	CallRecordMaster = select("startDate").from("CallRecordMaster").where("workEffortId", workEffortId).queryFirst();
	if(UtilValidate.isNotEmpty(CallRecordMaster)){

		callDateTime=CallRecordMaster.getTimestamp("startDate");
		String dateStr = UtilDateTime.toDateString(callDateTime,"MM/dd/yyyy HH:mm");
		inputContext.put("callDateTime",dateStr);
	}
	CommunicationEventWorkEff = select("communicationEventId").from("CommunicationEventWorkEff").where("workEffortId", workEffortId).queryFirst();
	if(UtilValidate.isNotEmpty(CommunicationEventWorkEff)){
		commEventId=CommunicationEventWorkEff.communicationEventId;
		if(UtilValidate.isNotEmpty(commEventId)){
			CommunicationEvent = select("content").from("CommunicationEvent").where("communicationEventId", commEventId).queryFirst();
			content=CommunicationEvent.content;			
			inputContext.put("content",content);
			context.put("template",content);
		}
		
	}
	if(type=="EMAIL"){
		if(UtilValidate.isNotEmpty(description)){
			templateMaster = from("TemplateMaster").where("templateId", description).queryOne();
			if(UtilValidate.isNotEmpty(templateMaster)){
				templateName = templateMaster.templateName;
				if(UtilValidate.isNotEmpty(templateName)){
					inputContext.put("emailTemplate",templateName);
				}else{
					inputContext.put("emailTemplate",description);
				}
			}
		}
	}
	inputContext.put("workEffortId",workEffortId);
	inputContext.put("type",type);
	inputContext.put("priority",priorityId);
	inputContext.put("srType",srType);
	inputContext.put("srSubTypeId",srSubtype);
	inputContext.put("statusId",statusId);
	inputContext.put("subject",subject);
	inputContext.put("contactId",contactId);
	inputContext.put("contactId_link","/contact-portal/control/viewContact?partyId="+contactId+"&externalLoginKey="+externalLoginKey);
	
	inputContext.put("partyId",workEffort.getString("cif"));
	inputContext.put("partyId_desc",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, workEffort.getString("cif"), false));
	GenericValue getBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", ownerBu).queryOne();
	if (UtilValidate.isNotEmpty(getBu))
	{
		inputContext.put("ownerBuDesc",getBu.getString("productStoreGroupName"));
	}
	context.put("selectedOwnerId", ownerId);
	context.put("selectedSubTypeId", srSubtype);
	context.put("selectedContactId", contactId);

	inputContext.put("createdBy",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, createdBy, false));

	
	if(UtilValidate.isNotEmpty(onceDone)){
		if("Y".equals(onceDone))
			inputContext.put("onceDone","Yes");
		if("N".equals(onceDone))
			inputContext.put("onceDone","No");
	}
	
	
	inputContext.put("modifiedBy",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, modifiedBy, false));
	inputContext.put("messages",description);
	if(UtilValidate.isNotEmpty(isView) && "Y".equals(isView)){
		
		domainEntityIdLink = "/ticket-portal/control/viewServiceRequest?srNumber="+workEffort.getString("domainEntityId")+"&externalLoginKey="+externalLoginKey;
		inputContext.put("domainEntityId_link", domainEntityIdLink);
		//inputContext.put("domainEntityId_link", org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom(workEffort.getString("domainEntityId"), workEffort.getString("domainEntityType"), externalLoginKey));
		
		partyData = delegator.findOne("Party", [partyId : partyId], false);
		if(UtilValidate.isNotEmpty(partyData)){
			timeZoneDesc = partyData.get("timeZoneDesc");
			inputContext.put("timeZoneDesc",timeZoneDesc);
			validRoleTypeId = partyData.getString("roleTypeId");
			if(UtilValidate.isNotEmpty(validRoleTypeId) && validRoleTypeId.equals("ACCOUNT")){
				inputContext.put("partyId_link","/account-portal/control/viewAccount?partyId="+partyId+"&externalLoginKey="+externalLoginKey);
			} else if(UtilValidate.isNotEmpty(validRoleTypeId) && validRoleTypeId.equals("LEAD")){
				validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("LEAD"), delegator);
				inputContext.put("partyId_link","/lead-portal/control/viewLead?partyId="+partyId+"&externalLoginKey="+externalLoginKey);
			} else if(UtilValidate.isNotEmpty(validRoleTypeId) && validRoleTypeId.equals("CONTACT")){
				validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("CONTACT"), delegator);
				inputContext.put("partyId_link","/contact-portal/control/viewContact?partyId="+partyId+"&externalLoginKey="+externalLoginKey);
			}
			
			inputContext.put("accountTypeId",validRoleTypeId);
		}
		if(UtilValidate.isNotEmpty(onceDone))
		{
			if(onceDone.equals("Y")){
				inputContext.put("onceDone","Yes");
				}else{
				inputContext.put("onceDone","No");
			}
		}
		inputContext.put("requiredAttendees",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, requiredAttendees, false));
		inputContext.put("optionalAttendees",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, optionalAttendees, false));
		inputContext.put("statusId",statusId);
		/*statusItem = EntityQuery.use(delegator).from("StatusItem").where("statusId", statusId).queryOne();
		 if (statusItem != null && statusItem.size() > 0) {
		 currentStatusId = statusItem.get("description");
		 inputContext.put("statusId",currentStatusId);
		 }*/
		GenericValue  subTypeValue = EntityUtil.getFirst(EntityQuery.use(delegator).select("value").from("WorkEffortAssocTriplet").where("code",srSubtype).queryList());
		GenericValue  TypeValue = EntityUtil.getFirst(EntityQuery.use(delegator).select("value").from("WorkEffortAssocTriplet").where("code",type).queryList());
		inputContext.put("srType", TypeValue?TypeValue.value:"");
		inputContext.put("srTypeId", TypeValue?TypeValue.code:"");
		inputContext.put("type", TypeValue?TypeValue.value:"");
		inputContext.put("srSubTypeId", subTypeValue?subTypeValue.value:"");
		inputContext.put("contactId",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, contactId, false));
		inputContext.put("contactId_link","/contact-portal/control/viewContact?partyId="+contactId+"&externalLoginKey="+externalLoginKey);
		
		inputContext.put("partyId",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, workEffort.getString("cif"), false));
		userLoginValue = select("partyId").from("UserLogin").where("userLoginId", ownerId).queryFirst();
		
		if(UtilValidate.isNotEmpty(userLoginValue) && UtilValidate.isNotEmpty(userLoginValue.get("partyId"))){
			inputContext.put("owner",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, userLoginValue.get("partyId"), false));
		}
		
		GenericValue UserLoginPersonn = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId",norganizer).queryOne();
		if(UtilValidate.isNotEmpty(UserLoginPersonn) && UtilValidate.isNotEmpty(UserLoginPersonn.getString("partyId"))){
			inputContext.put("norganizer",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, UserLoginPersonn.getString("partyId"), false));
		}else{
			inputContext.put("norganizer",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, norganizer, false));
		}
	   
		GenericValue UserLoginPersonnn = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId",nrecepient).queryOne();
		if(UtilValidate.isNotEmpty(UserLoginPersonnn) && UtilValidate.isNotEmpty(UserLoginPersonnn.getString("partyId"))){
			inputContext.put("nrecepient",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, UserLoginPersonnn.getString("partyId"), false));
		}else{
			inputContext.put("nrecepient",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, nrecepient, false));
		}
		
		/*
		if(UtilValidate.isNotEmpty(workEffort.getTimestamp("actualStartDate"))){
			String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("actualStartDate"),"yyyy-MM-dd HH:mm");
			inputContext.put("actualStartDate", dateStr);
		}
		if(UtilValidate.isNotEmpty(workEffort.getTimestamp("actualCompletionDate"))){
			String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("actualCompletionDate"),"yyyy-MM-dd HH:mm");
			inputContext.put("actualCompletionDate", dateStr);
		}
		if(UtilValidate.isNotEmpty(workEffort.getTimestamp("estimatedStartDate"))){
			String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("estimatedStartDate"),"yyyy-MM-dd HH:mm");
			inputContext.put("estimatedStartDate", dateStr);
		}
		if(UtilValidate.isNotEmpty(workEffort.getTimestamp("estimatedCompletionDate"))){
			String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("estimatedCompletionDate"),"yyyy-MM-dd HH:mm");
			inputContext.put("estimatedCompletionDate", dateStr);
		}
		*/
	}
	inputContext.put("timeZoneDesc",workEffort.getString("entityTimeZoneId"));
	
	if(UtilValidate.isNotEmpty(workEffortId)){
		attendeesconditions = EntityCondition.makeCondition([
			EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
			EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
			EntityCondition.makeCondition("expectationEnumId", EntityOperator.IN, ["WEE_REQUIRE","WEE_REQUEST"])],
		EntityOperator.AND);
		WorkEffortPartyAssignmentList = delegator.findList("WorkEffortPartyAssignment", attendeesconditions, null, null, null, false);
		requireAttendeeslist = EntityUtil.filterByCondition(WorkEffortPartyAssignmentList, EntityCondition.makeCondition("expectationEnumId",EntityOperator.EQUALS,"WEE_REQUIRE"));
		optionalAttendeeslist = EntityUtil.filterByCondition(WorkEffortPartyAssignmentList, EntityCondition.makeCondition("expectationEnumId",EntityOperator.EQUALS,"WEE_REQUEST"));

		requiredAttendeeParties = EntityUtil.getFieldListFromEntityList(requireAttendeeslist, "partyId", false);
		optionalAttendeeParties = EntityUtil.getFieldListFromEntityList(optionalAttendeeslist, "partyId", false);

		requiredAttendeesDesc="";
		optionalAttendeesDesc = "";
		
		if(UtilValidate.isNotEmpty(requiredAttendeeParties)){
			requiredAttendeeParties.each { eachReqId ->
				reqAttndName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, eachReqId, false);
				requiredAttendeesDesc += reqAttndName+",";
			}
			inputContext.put("requiredAttendeesDesc", requiredAttendeesDesc.substring(0, requiredAttendeesDesc.length()-1));
		}
		
		if(UtilValidate.isNotEmpty(optionalAttendeeParties)){
			optionalAttendeeParties.each { eachOptId ->
				optAttndName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, eachOptId, false);
				optionalAttendeesDesc += optAttndName+",";
			}
			inputContext.put("optionalAttendeesDesc", optionalAttendeesDesc.substring(0, optionalAttendeesDesc.length()-1));
		}
	}
	//Get Timezone when workEffort entityTimeZoneId is empty
	
	if((workEffort.getString("entityTimeZoneId")==null) || UtilValidate.isEmpty(workEffort.get("entityTimeZoneId"))){
	partySummary = from("PartySummaryDetailsView").where("partyId", partyId,"partyTypeId","PARTY_GROUP").queryOne();
	if(partySummary!=null && partySummary.size()>0){
		inputContext.put("timeZoneDesc",partySummary.getString("timeZoneDesc"));
		}
	}
}



//ended	
/*kpiMetric = DataHelper.prepareHomeKpiInfo(delegator, userLogin);
context.put("kpiMetric", kpiMetric);
context.put("appBarContext", appBarContext);*/
context.put("inputContext", inputContext);
println("--Activity-----inputcontext"+inputContext);
if(context.get("domainEntityType")=="OPPORTUNITY"){
	context.put("domainEntityFieldId", "salesOpportunityId");
}
else if(context.get("domainEntityType")=="ACCOUNT"||context.get("domainEntityType")=="CONTACT"||context.get("domainEntityType")=="LEAD"){
	context.put("domainEntityFieldId", "partyId");
}else if(context.get("domainEntityType")=="SERVICES"){
	context.put("domainEntityFieldId", "custRequestId");
}
//context.put("domainEntityId", workEffortId);
//context.put("domainEntityType", "WORKEFFORT");
//context.put("requestURI", "viewActivity");
/*String externalLoginKey = request.getParameter("externalLoginKey");
if(externalLoginKey==null)
	externalLoginKey=request.getAttribute("externalLoginKey");
context.put("externalLoginKey", externalLoginKey);*/