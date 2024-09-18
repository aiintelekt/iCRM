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

import javax.servlet.http.HttpSession;

import org.fio.crm.party.PartyHelper;
import org.fio.homeapps.util.DataUtil
import org.fio.homeapps.util.EnumUtil
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap

import net.sf.json.JSONObject;

uiLabelCommonMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);
String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

String workEffortId = request.getParameter("workEffortId");
String fromPhoneNumber = request.getParameter("fromPhoneNumber");
String salesOpportunityId = request.getParameter("salesOpportunityId");
security = request.getAttribute("security");
GenericValue userLogin = request.getAttribute("userLogin");
HttpSession session = request.getSession();
userLoginId = userLogin.getString('partyId');
delegator = request.getAttribute("delegator");
dispatcher = request.getAttribute("dispatcher");
uiLabelMap = UtilProperties.getResourceBundleMap("SrPortalUiLabels", locale);
loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserId", userLogin.getString("userLoginId"));
context.put("loggedUserPartyId", loggedUserPartyId);

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);

String isView = context.get("isView");

context.put("haveDataPermission", "Y");
context.put("hasReassignPermission", true);

long start = System.currentTimeMillis();

selectedCalSlot = org.groupfio.common.portal.util.ResAvailUtil.getCalBookedData(delegator, "ACTIVITY", workEffortId);
println("selectedCalSlot>>>> "+selectedCalSlot);
context.put("selectedCalSlot", selectedCalSlot);
if (UtilValidate.isNotEmpty(selectedCalSlot)) {
	context.put("ownerBookedCalSlot", org.fio.homeapps.util.ParamUtil.mapToJson(selectedCalSlot));
}

String userLoginId = userLogin.getString("partyId");

inputContext = new LinkedHashMap<String, Object>();
appBarContext = new LinkedHashMap<String, Object>();
if(context.get("inputContext")){

	inputContext.putAll(context.get("inputContext"));
}
inputContext.put("activityId", workEffortId);
context.put("workEffortId", workEffortId);

GenericValue workEffort = from("WorkEffort").where("workEffortId", workEffortId).queryOne();

if (UtilValidate.isNotEmpty(workEffort)) {

	if(UtilValidate.isEmpty(workEffort.get("salesOpportunityId"))) {
		workEffort.remove("salesOpportunityId");
	}
	
	inputContext.putAll(workEffort.getAllFields());
	String phoneNumber = workEffort.getString("phoneNumber");
	inputContext.put("phoneNumber", UtilValidate.isNotEmpty(phoneNumber) ? org.fio.admin.portal.util.DataUtil.formatPhoneNumber(phoneNumber) :"");
	context.put("domainEntityId", workEffort.getString("domainEntityId"));
	context.put("domainEntityType", workEffort.getString("domainEntityType"));
	inputContext.put("domainEntityType", org.groupfio.common.portal.util.DataHelper.convertToLabel(workEffort.getString("domainEntityType")));
	inputContext.put("domainEntityTypeId",workEffort.getString("domainEntityType"));
	inputContext.put("domainEntityId1", workEffort.getString("domainEntityId"));
	inputContext.put("domainEntityType1", workEffort.getString("domainEntityType"));
	if(UtilValidate.isNotEmpty(workEffort.getString("domainEntityId"))) {
		GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", workEffort.getString("domainEntityId")).queryFirst();
		inputContext.put("domainName", UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(custRequest.getString("custRequestName")) ? custRequest.getString("custRequestName") : "");
	}
	
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

	List<String> toScheldTypes = new ArrayList<>();
	String scheduleTaskType = DataUtil.getGlobalValue(delegator, "SCHEDULE_TASK_TYPE", "SCHEDULE_TASK");
	if(UtilValidate.isNotEmpty(scheduleTaskType) && scheduleTaskType.contains(",")) {
		toScheldTypes = org.fio.admin.portal.util.DataUtil.stringToList(scheduleTaskType, ",");
	} else if(UtilValidate.isNotEmpty(scheduleTaskType)) {
		toScheldTypes.add(scheduleTaskType);
	}
	
	String workType = workEffort.getString("workEffortPurposeTypeId");
	if("IA_OPEN".equals(statusId) && toScheldTypes.contains(workType)) {
		context.put("isScheduleTask","Y");
	} else
		context.put("isScheduleTask","N");
		
	String partyId=	workEffort.getString("cif");
	context.put("partyId", partyId);

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
		String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("actualStartDate"),globalDateTimeFormat);
		inputContext.put("actualStartDate",workEffort.getTimestamp("actualStartDate"));
	}
	if(UtilValidate.isNotEmpty(workEffort.getTimestamp("actualCompletionDate"))){
		String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("actualCompletionDate"),globalDateTimeFormat);
		inputContext.put("actualCompletionDate",workEffort.getTimestamp("actualCompletionDate"));
	}
	if(UtilValidate.isNotEmpty(workEffort.getTimestamp("estimatedStartDate"))){
		String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("estimatedStartDate"),globalDateTimeFormat);
		inputContext.put("estimatedStartDate",workEffort.getTimestamp("estimatedStartDate"));
		inputContext.put("startTime",dateStr);
	}
	if(UtilValidate.isNotEmpty(workEffort.getTimestamp("estimatedCompletionDate"))){
		String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("estimatedCompletionDate"),globalDateTimeFormat);
		inputContext.put("estimatedCompletionDate",workEffort.getTimestamp("estimatedCompletionDate"));
	}
	if(UtilValidate.isNotEmpty(createdDate)){
		/*String dateStr = UtilDateTime.toDateString(createdDate,globalDateFormat);
		inputContext.put("createdDate",dateStr);*/
		inputContext.put("createdDate",workEffort.getTimestamp("createdDate"));
	}
	if(UtilValidate.isNotEmpty(modifiedDate)){
		String dateStr = UtilDateTime.toDateString(modifiedDate,globalDateFormat);
		inputContext.put("modifiedDate",dateStr);
		inputContext.put("modifiedOn",dateStr);
	}
	if(UtilValidate.isNotEmpty(closedDateTime) && UtilValidate.isNotEmpty(closedBy)){
		String dateStr = UtilDateTime.toDateString(closedDateTime,globalDateFormat);
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
	if(UtilValidate.isEmpty(inputContext.get("duration"))) {
		inputContext.put("duration", workEffort.getString("duration"));
	}

	workEffortCommEvent = from("WorkEffortCommExtension").where("workEffortId", workEffortId).queryList();
	if(UtilValidate.isNotEmpty(workEffortCommEvent)){
		for(GenericValue each:workEffortCommEvent){
			if(each.wftExtType.equals("SENDER_TYPE")){
				nsender=each.wftExtValue;
				inputContext.put("nsender",nsender);
			}else if(each.wftExtType.equals("BCC_TYPE")){
				//nbcc=each.wftExtValue;
				//nbcc= nbcc.replaceAll(",",", ");
				nbcc = UtilValidate.isNotEmpty(nbcc)?nbcc+","+each.wftExtValue:each.wftExtValue
				inputContext.put("nbcc",nbcc);
			}
			else if(each.wftExtType.equals("CC_TYPE")){
				//ncc=each.wftExtValue;
				//ncc= ncc.replaceAll(",",", ");
				ncc = UtilValidate.isNotEmpty(ncc)?ncc+","+each.wftExtValue:each.wftExtValue
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
	CallRecordMaster = select("startDate","callBackDate").from("CallRecordMaster").where("workEffortId", workEffortId).queryFirst();
	if(UtilValidate.isNotEmpty(CallRecordMaster)){

		callDateTime=CallRecordMaster.getTimestamp("startDate");
		String dateStr = UtilDateTime.toDateString(callDateTime,globalDateTimeFormat);
		inputContext.put("callDateTime",dateStr);
		if(UtilValidate.isNotEmpty(CallRecordMaster.getDate("callBackDate"))){
			String callBackDateStr = UtilDateTime.toDateString(CallRecordMaster.getDate("callBackDate"),globalDateFormat);
			inputContext.put("callBackDate",callBackDateStr);
		}
	}
	String communicationEventTypeId = null;
	CommunicationEventWorkEff = select("communicationEventId").from("CommunicationEventWorkEff").where("workEffortId", workEffortId).queryFirst();
	if(UtilValidate.isNotEmpty(CommunicationEventWorkEff)){
		commEventId=CommunicationEventWorkEff.communicationEventId;
		if(UtilValidate.isNotEmpty(commEventId)){
			GenericValue CommunicationEvent = select("communicationEventId", "content","communicationEventTypeId").from("CommunicationEvent").where("communicationEventId", commEventId).queryFirst();
			
			//content=CommunicationEvent.content;
			//inputContext.put("content",content);
			//context.put("template",content);
			
			communicationEventTypeId = UtilValidate.isNotEmpty(CommunicationEvent.getString("communicationEventTypeId")) ? CommunicationEvent.getString("communicationEventTypeId") : "";
			context.put("communicationEventTypeId", communicationEventTypeId);
			List<GenericValue> commEventContentAssocList = EntityQuery.use(delegator).from("CommEventContentAssoc").where(EntityCondition.makeCondition("communicationEventId", EntityOperator.EQUALS, CommunicationEvent.getString("communicationEventId"))).queryList();
			
			boolean isAttach = false;
			if(UtilValidate.isNotEmpty(commEventContentAssocList)){
				isAttach =  true;
			}
			
			if(UtilValidate.isNotEmpty(communicationEventTypeId) && "GRAPH_EMAIL".equals(communicationEventTypeId)) {
				Map<String, Object> mailContent = org.fio.admin.portal.util.DataUtil.convertToMap(CommunicationEvent.getString("content"));
				Map<String, Object> msgBody = (Map<String, Object>) mailContent.get("body");
				String emailCont = UtilValidate.isNotEmpty(msgBody) && UtilValidate.isNotEmpty(msgBody.get("content")) ? (String) msgBody.get("content") :"";
				if(isAttach) {
					if(emailCont.contains("<img "))
						emailCont = emailCont.replaceAll("<p class=\"MsoNormal\">&nbsp;</p>"," ");
				
					emailCont = emailCont.replaceAll("<img .*?>","&nbsp;");
				}
				inputContext.put("content",emailCont);
				context.put("template",emailCont);
			}
			else {
				String emailCont = UtilValidate.isNotEmpty(CommunicationEvent.getString("content")) ? CommunicationEvent.getString("content") :"";
				if(isAttach) {
					if(emailCont.contains("<img "))
						emailCont = emailCont.replaceAll("<p class=\"MsoNormal\">&nbsp;</p>"," ");
				
					emailCont = emailCont.replaceAll("<img .*?>","&nbsp;");
				}
				inputContext.put("content",emailCont);
				context.put("template",emailCont);
			}
			GenericValue communicationWorkEffort = select("communicationEventId").from("CommunicationEventWorkEff").where("workEffortId", workEffortId).queryFirst();
			GenericValue fromDataGv = select("fromData","msgSentTime","communicationEventTypeId").from("CommunicationEvent").where("communicationEventId", communicationWorkEffort.getString("communicationEventId")).queryFirst();
			context.put("communicationWorkEff",workEffortId);
			String senderPhoneNumber = UtilValidate.isNotEmpty(fromDataGv.getString("fromData"))? fromDataGv.getString("fromData") : "";
			msgSentTime = UtilValidate.isNotEmpty(fromDataGv.getTimestamp("msgSentTime")) ? UtilDateTime.toDateString(fromDataGv.getTimestamp("msgSentTime"),globalDateTimeFormat) :"";
			//String senderPhoneNumber = fromPhoneNumber;
			senderPartyId = org.fio.admin.portal.util.DataUtil.getPartyIdByPrmaryPhone(delegator, senderPhoneNumber);
			senderName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, senderPartyId, false);
			
			inputContext.put("senderName",senderName);
			inputContext.put("msgSentTime",msgSentTime);
			context.put("fromPhoneNumber",senderPhoneNumber);
				
			if(UtilValidate.isNotEmpty(senderPhoneNumber)){
				List < EntityCondition > conditions = new ArrayList<EntityCondition>();
				DynamicViewEntity dynamicEntity = new DynamicViewEntity();
				dynamicEntity.addMemberEntity("WE", "WorkEffort");
				dynamicEntity.addAlias("WE", "workEffortId","workEffortId", null,Boolean.FALSE,Boolean.TRUE,null);
				dynamicEntity.addAlias("WE", "workEffortTypeId");
				dynamicEntity.addAlias("WE", "actualStartDate");
				dynamicEntity.addAlias("WE", "actualCompletionDate");
				dynamicEntity.addAlias("WE", "domainEntityType");
				dynamicEntity.addAlias("WE", "domainEntityId");

				dynamicEntity.addMemberEntity("CEW", "CommunicationEventWorkEff");
				dynamicEntity.addAlias("CEW", "communicationEventId","communicationEventId", null, Boolean.FALSE, Boolean.TRUE, null);
				dynamicEntity.addViewLink("WE", "CEW", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
				
				dynamicEntity.addMemberEntity("CE", "CommunicationEvent");
				dynamicEntity.addAlias("CE", "content");
				dynamicEntity.addAlias("CE", "msgSentTime");
				dynamicEntity.addAlias("CE", "fromData");
				dynamicEntity.addAlias("CE", "createdStamp");
				dynamicEntity.addAlias("CE", "communicationEventTypeId");
				dynamicEntity.addAlias("CE", "entryDate");
				dynamicEntity.addViewLink("CEW", "CE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("communicationEventId"));

				//conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, "")
				));
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "")
				));
				conditions.add(EntityCondition.makeCondition("fromData", EntityOperator.EQUALS, senderPhoneNumber));
				conditions.add(EntityCondition.makeCondition("communicationEventTypeId", EntityOperator.EQUALS, "SMS_COMMUNICATION"));
				EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				communicationEventList = EntityQuery.use(delegator).select("communicationEventId","content","msgSentTime","workEffortId").from(dynamicEntity).where(condition).maxRows(5).orderBy("createdStamp DESC").queryList();
				if(UtilValidate.isNotEmpty(communicationEventList)) {
					List<Map<String, String>> smsContent = new LinkedList<>();
					for(GenericValue communicationEventData : communicationEventList) {
						content = UtilValidate.isNotEmpty(communicationEventData.getString("content")) ? communicationEventData.getString("content") :"";
						smsSentDate = UtilValidate.isNotEmpty(communicationEventData.getTimestamp("msgSentTime")) ? UtilDateTime.toDateString(communicationEventData.getTimestamp("msgSentTime"),globalDateTimeFormat) :"";
							Map<String, String> smsData = new HashMap<>();
							smsData.put("content", content);
							smsData.put("smsSentDate", smsSentDate);
							smsData.put("communicationWorkEffId", communicationEventData.getString("workEffortId"));
							smsContent.add(smsData);
					}
					context.put("communicationEventList",smsContent);
				}
					
				}
				senderPhoneNumber = org.groupfio.common.portal.util.DataHelper.preparePhoneNumber(delegator, senderPhoneNumber);
				inputContext.put("senderPhoneNumber",senderPhoneNumber);
				
		}

	} else {
		communicationEventTypeId = type;
	}
	context.put("communicationEventTypeId", communicationEventTypeId);
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

		inputContext.put("domainEntityId_link", org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom(workEffort.getString("domainEntityId"), workEffort.getString("domainEntityType"), externalLoginKey));

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
			}else if(UtilValidate.isNotEmpty(validRoleTypeId) && validRoleTypeId.equals("CUSTOMER")){
				validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("CUSTOMER"), delegator);
				inputContext.put("partyId_link","/customer-portal/control/viewCustomer?partyId="+partyId+"&externalLoginKey="+externalLoginKey);
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
		
		if(UtilValidate.isNotEmpty(workEffort.getTimestamp("actualStartDate"))){
			String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("actualStartDate"),globalDateTimeFormat);
			inputContext.put("actualStartDate", dateStr);
		}
		if(UtilValidate.isNotEmpty(workEffort.getTimestamp("actualCompletionDate"))){
			String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("actualCompletionDate"),globalDateTimeFormat);
			inputContext.put("actualCompletionDate", dateStr);
		}
		if(UtilValidate.isNotEmpty(workEffort.getTimestamp("estimatedStartDate"))){
			String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("estimatedStartDate"),globalDateTimeFormat);
			inputContext.put("estimatedStartDate", dateStr);
			Timestamp estStartDate = workEffort.getTimestamp("estimatedStartDate");
			Timestamp nowStamp = UtilDateTime.nowTimestamp();
			if(nowStamp.before(estStartDate))
				context.put("completeConfirm", "Y");
			else
				context.put("completeConfirm", "N");
		}
		if(UtilValidate.isNotEmpty(workEffort.getTimestamp("estimatedCompletionDate"))){
			String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("estimatedCompletionDate"),globalDateTimeFormat);
			inputContext.put("estimatedCompletionDate", dateStr);
		}
		if(UtilValidate.isNotEmpty(workEffort.getTimestamp("closedDateTime"))){
			String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("closedDateTime"),globalDateTimeFormat);
			inputContext.put("closedDateTime", dateStr);
		}
		
		if(UtilValidate.isNotEmpty(workEffort.getString("closedByUserLogin"))){
			String userLoginPartyId = DataUtil.getPartyIdByUserLoginId(delegator, workEffort.getString("closedByUserLogin"));
			String dateStr = DataUtil.getPartyName(delegator, userLoginPartyId);
			inputContext.put("closedByUserLogin", dateStr);
		}

		inputContext.put("priorityDesc", EnumUtil.getEnumDescription(delegator, priorityId, "PRIORITY_LEVEL"));
		
		if(UtilValidate.isNotEmpty(createdDate)){
			String dateStr = UtilDateTime.toDateString(createdDate,globalDateFormat);
			inputContext.put("createdDate",dateStr);
		}
		if(UtilValidate.isNotEmpty(workEffort.getTimestamp("estimatedCompletionDate"))
			&& UtilValidate.isNotEmpty(workEffort.getString("domainEntityType")) && (workEffort.getString("domainEntityType")=="REBATE" || workEffort.getString("domainEntityType")== "ACCOUNT" || workEffort.getString("domainEntityType")== "OPPORTUNITY")
			){
			String dateStr = UtilDateTime.toDateString(workEffort.getTimestamp("estimatedCompletionDate"),globalDateFormat);
			inputContext.put("estimatedCompletionDate",dateStr);
		}
		
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

	//get the owners
	String activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
	if(UtilValidate.isNotEmpty(activityOwnerRole)) {
		List<String> ownerRoles = new ArrayList<>();
		if(UtilValidate.isNotEmpty(activityOwnerRole) && activityOwnerRole.contains(",")) {
			ownerRoles = org.fio.admin.portal.util.DataUtil.stringToList(activityOwnerRole, ",");
		} else
			ownerRoles.add(activityOwnerRole);

		EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
				EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,workEffortId),
				EntityCondition.makeCondition("roleTypeId", EntityOperator.IN,ownerRoles),
				//EntityCondition.makeCondition("ownerId", EntityOperator.NOT_EQUAL,null),
				EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
				EntityUtil.getFilterByDateExpr()
				);
		println("owner condition>>>"+condition);
		List<GenericValue> workEffortPartyAssignList = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(condition).queryList();
		if(UtilValidate.isNotEmpty(workEffortPartyAssignList)) {
			List<String> ownerLoginPartyIds = EntityUtil.getFieldListFromEntityList(workEffortPartyAssignList, "partyId", true);
			List<String> ownerLoginIds = new LinkedList<>();
			List<String> ownerLoginNames = new LinkedList<>();
			for(String ownerPartyId : ownerLoginPartyIds) {
				ownerLoginIds.add(DataUtil.getPartyUserLoginId(delegator, ownerPartyId));
				ownerLoginNames.add(DataUtil.getUserLoginName(delegator, ownerPartyId));
			}

			if (request.getRequestURI().contains("updateActivity") || request.getRequestURI().contains("createTaskActivity")) {
				inputContext.put("owner", UtilValidate.isNotEmpty(ownerLoginIds) ? org.fio.admin.portal.util.DataUtil.listToString(ownerLoginIds): "" );
				context.put("selectedOwnerId", UtilValidate.isNotEmpty(ownerLoginIds) ? org.fio.admin.portal.util.DataUtil.listToString(ownerLoginIds): "" );
			}
			if (request.getRequestURI().contains("viewActivity")) {
				inputContext.put("owner", UtilValidate.isNotEmpty(ownerLoginNames) ? org.fio.admin.portal.util.DataUtil.listToString(ownerLoginNames): "" );
			}
		}
	}
	
	GenericValue workEffortAttr = EntityQuery.use(delegator).from("WorkEffortAttribute").where("workEffortId",workEffortId,"attrName","TECH_ARRIVAL_WINDOW").queryFirst();
	if(UtilValidate.isNotEmpty(workEffortAttr)) {
		if (request.getRequestURI().contains("updateActivity")) {
			inputContext.put("arrivalWindow", workEffortAttr.getString("attrValue"));
		}
		if (request.getRequestURI().contains("viewActivity")) {
			inputContext.put("arrivalWindow", workEffortAttr.getString("attrValue")+"hr");
		}
	}
	
	inputContext.put("isSchedulingRequired", org.fio.homeapps.util.UtilActivity.getActivityAttrValue(delegator, workEffortId, "IS_SCHEDULING_REQUIRED"));

	callStatusStr =org.fio.homeapps.util.UtilActivity.getActivityAttrValue(delegator, workEffortId, "CALL_STATUS")
	if(UtilValidate.isNotEmpty(callStatusStr)) {
		inputContext.put("callStatus",callStatusStr);
	}
}

String techArrivalWindows = DataUtil.getGlobalValue(delegator, "TECH_ARRIVAL_WINDOWS");
context.put("techArrivalWindows",techArrivalWindows);

if(context.get("domainEntityType")=="OPPORTUNITY"){
	context.put("domainEntityFieldId", "salesOpportunityId");
}
else if(context.get("domainEntityType")=="ACCOUNT"||context.get("domainEntityType")=="CONTACT"||context.get("domainEntityType")=="LEAD"){
	context.put("domainEntityFieldId", "partyId");
}else if(context.get("domainEntityType")=="SERVICES"){
	context.put("domainEntityFieldId", "custRequestId");
}

yesNoMap = ["Y":"Yes", "N":"No"];
context.put("yesNoOptions", yesNoMap);

long end = System.currentTimeMillis();
Debug.log("Time Taken by the view activity --->"+(end-start) / 1000f);

String userLoginPartyId1 = userLogin.getString("partyId");
List<GenericValue> _3_party_tech_list = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdTo",userLoginPartyId1, "roleTypeIdTo","TECHNICIAN", "partyRelationshipTypeId","CONTRACT_TYPE").filterByDate().queryList();
if(UtilValidate.isNotEmpty(_3_party_tech_list)) {
	context.put("is3PartyTechnician","Y");
} else {
	context.put("is3PartyTechnician","N");
}
println("is3PartyTechnician: "+context.get("is3PartyTechnician"));
techPriorityTypeList = new LinkedHashMap<String, Object>();
techPriorityTypeList.put("", uiLabelCommonMap.TechType);
techPriorityTypeList.put("REEB-ASSIGNED", uiLabelCommonMap.ReebAssignedTech);
if(context.get("is3PartyTechnician").equals("N")) {
	techPriorityTypeList.put("REEB-RECOMMENDED", uiLabelCommonMap.ReebRecommendedTech);
	techPriorityTypeList.put("REEB-OTHER", uiLabelCommonMap.ReebOtherStateTech);
}
techPriorityTypeList.put("CONTRACTOR", uiLabelCommonMap.ReebContractorTech);
if(context.get("is3PartyTechnician").equals("N")) {
	techPriorityTypeList.put("OTHER", uiLabelCommonMap.ReebOtherTech);
}
context.put("techPriorityTypeList", techPriorityTypeList);

String workStartTime = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_START_TIME");
String workEndTime = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_END_TIME");
context.put("workStartTime", workStartTime);
context.put("workEndTime", workEndTime);

String inspActWorkTypeIds = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "INSP_ACT_WT");
context.put("inspActWorkTypeIds", inspActWorkTypeIds);

String isEnableRebateModule = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_REBATE_MODULE");
if(UtilValidate.isNotEmpty(isEnableRebateModule)){
	context.put("isEnableRebateModule", isEnableRebateModule);
}

String _3rdPartyActivity = org.fio.homeapps.util.UtilActivity.getActivityAttrValue(delegator, workEffortId, "IS_3_PARTY_ACTIVITY");
context.put("is3rdPartyActivity", UtilValidate.isNotEmpty(_3rdPartyActivity) ? _3rdPartyActivity : "N");

context.put("actAttrGcode", org.fio.homeapps.util.UtilActivity.getActivityAttrValue(delegator, workEffortId, "ASSIGN_ATTR_GCODE"));
context.put("isProgAct", org.fio.homeapps.util.UtilActivity.getActivityAttrValue(delegator, workEffortId, "IS_PROG_ACT"));

context.put("inputContext", inputContext);
println("--Activity-----inputcontext"+inputContext);