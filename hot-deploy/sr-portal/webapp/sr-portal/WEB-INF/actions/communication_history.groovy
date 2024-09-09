import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import net.sf.json.JSONObject;
import java.util.TimeZone;

import org.fio.crm.constants.CrmConstants
import org.fio.crm.party.PartyHelper;
import org.fio.homeapps.util.DataUtil
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor
import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;

import java.sql.ResultSet
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import java.text.DecimalFormat;

import java.sql.Timestamp
import java.text.SimpleDateFormat

import org.groupfio.account.portal.util.DataHelper

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("lead-portalUiLabels", locale);

String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
String smsCharLimit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SMS_CHAR_LIMIT","640");
String historyLimit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "COMM_HISTORY_SCREEN_LIMIT","10");
context.smsCharLimit = smsCharLimit;
context.historyLimit = historyLimit;

String custRequestId = request.getParameter("srNumber");

if(UtilValidate.isNotEmpty(custRequestId)){
	/*

	custRequestWorkEffortList = select("workEffortId").from("CustRequestWorkEffort").where("custRequestId", custRequestId).orderBy("workEffortId").queryList();

	if(UtilValidate.isNotEmpty(custRequestWorkEffortList)){

		custRequestWorkEffortIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList, "workEffortId", true);

		if(UtilValidate.isNotEmpty(custRequestWorkEffortIds)){

			List<EntityCondition> emailWorkEffortConditionList = FastList.newInstance();
			emailWorkEffortConditionList.add(EntityCondition.makeCondition("workEffortId",EntityOperator.IN, custRequestWorkEffortIds));
			emailWorkEffortConditionList.add(EntityCondition.makeCondition("workEffortTypeId",EntityOperator.EQUALS,"31703"));

			EntityCondition emailWorkEffortCondition = EntityCondition.makeCondition(emailWorkEffortConditionList, EntityOperator.AND);

			custRequestEmailWorkEffortList = EntityQuery.use(delegator).from("WorkEffort").where(emailWorkEffortCondition).queryList();

			Map <String, String> workEffortDirectionMap = new HashMap <String, String> ();
			
			if(UtilValidate.isNotEmpty(custRequestEmailWorkEffortList) && custRequestEmailWorkEffortList.size()>0){
				
				for(int i=0;i<custRequestEmailWorkEffortList.size();i++){
					eachCustReqWorkEffort = custRequestEmailWorkEffortList.get(i);
					workEffortDirectionMap.put(eachCustReqWorkEffort.getString("workEffortId"), eachCustReqWorkEffort.getString("direction"));
				}
				
				custRequestEmailWorkEffortIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList, "workEffortId", true);

				if(UtilValidate.isNotEmpty(custRequestEmailWorkEffortIds)){

					communicationEventWorkEffList = EntityQuery.use(delegator).from("CommunicationEventWorkEff").where(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, custRequestEmailWorkEffortIds)).queryList();
					
					Map <String, String> communicationDirectionMap = new HashMap <String, String> ();
					
					if(UtilValidate.isNotEmpty(communicationEventWorkEffList)){
						
						for(int i=0;i<communicationEventWorkEffList.size();i++){
							eachCommWorkEffort = communicationEventWorkEffList.get(i);
							communicationDirectionMap.put(eachCommWorkEffort.getString("communicationEventId"), workEffortDirectionMap.get(eachCommWorkEffort.getString("workEffortId")));
						}
						
						emailCommunicationEventIds = EntityUtil.getFieldListFromEntityList(communicationEventWorkEffList, "communicationEventId", true);

						if(UtilValidate.isNotEmpty(emailCommunicationEventIds)){

							List<EntityCondition> emailCommunicationEventConditionList = FastList.newInstance();
							emailCommunicationEventConditionList.add(EntityCondition.makeCondition("communicationEventId",EntityOperator.IN, emailCommunicationEventIds));
							emailCommunicationEventConditionList.add(EntityCondition.makeCondition("communicationEventTypeId",EntityOperator.EQUALS,"EMAIL_COMMUNICATION"));

							EntityCondition emailCommunicationEventCondition = EntityCondition.makeCondition(emailCommunicationEventConditionList, EntityOperator.AND);

							communicationEventList = EntityQuery.use(delegator).from("CommunicationEvent").where(emailCommunicationEventCondition).orderBy("-entryDate").queryList();

							communicationHistoryList = [];

							if(UtilValidate.isNotEmpty(communicationEventList)){

								for(int i=0;i<communicationEventList.size();i++){

									Map <String, String> communicationHistoryMap = new HashMap <String, String> ();
									eachCommunicationEvent = communicationEventList.get(i);
									
									commEventContentAssocList = EntityQuery.use(delegator).from("CommEventContentAssoc").where(EntityCondition.makeCondition("communicationEventId", EntityOperator.EQUALS, eachCommunicationEvent.getString("communicationEventId"))).queryList();
									
									if(UtilValidate.isNotEmpty(commEventContentAssocList)){
										communicationHistoryMap.put("isAttachment","Y");
									}else{
										communicationHistoryMap.put("isAttachment","N");
									}
									
									String fromPartyName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, eachCommunicationEvent.get("partyIdFrom"), false);
									
									communicationHistoryMap.put("direction", communicationDirectionMap.get(eachCommunicationEvent.getString("communicationEventId")));
									communicationHistoryMap.put("eventId", eachCommunicationEvent.getString("communicationEventId"));
									
									if("OUT".equals(communicationHistoryMap.get("direction"))){
										//communicationHistoryMap.put("fromPartyName", org.fio.homeapps.util.PartyHelper.getPartyName(delegator, eachCommunicationEvent.get("partyIdFrom"), false));
										CommunicationEventWorkEff = EntityUtil.getFirst(delegator.findByAnd("CommunicationEventWorkEff", [communicationEventId: eachCommunicationEvent.getString("communicationEventId")], null, false));
										
										if(UtilValidate.isNotEmpty(CommunicationEventWorkEff)){
											workEffortId = CommunicationEventWorkEff.workEffortId;
											if(UtilValidate.isNotEmpty(workEffortId)){
												workEffortInfo = from("WorkEffort").where("workEffortId", workEffortId).queryOne();
												String createdByUserLogin = workEffortInfo.get("createdByUserLogin");
												
												GenericValue UserLoginPerson = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId",workEffortInfo.get("createdByUserLogin")).queryOne();
												if(UtilValidate.isNotEmpty(UserLoginPerson) && UtilValidate.isNotEmpty(UserLoginPerson.getString("partyId"))){
													communicationHistoryMap.put("fromPartyName",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, UserLoginPerson.getString("partyId"), false));
												}
																								
												communicationHistoryMap.put("toPartyName", org.fio.homeapps.util.UtilActivity.getPartyNamesFromCommExtension(delegator, UtilMisc.toMap("workEffortId", workEffortId, "workExtName", "TO", "wftExtType", "TO_TYPE")));
											}
										}
									}
									
									if("IN".equals(communicationHistoryMap.get("direction"))){
										CommunicationEventWorkEff = EntityUtil.getFirst(delegator.findByAnd("CommunicationEventWorkEff", [communicationEventId: eachCommunicationEvent.getString("communicationEventId")], null, false));
										
										if(UtilValidate.isNotEmpty(CommunicationEventWorkEff)){
											workEffortId = CommunicationEventWorkEff.workEffortId;
											
											if(UtilValidate.isNotEmpty(workEffortId)){
												
												workEffortInfo = from("WorkEffort").where("workEffortId", workEffortId).queryOne();
												String createdByUserLogin = workEffortInfo.get("createdByUserLogin");
												
												GenericValue UserLoginPerson = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId",workEffortInfo.get("createdByUserLogin")).queryOne();
												if(UtilValidate.isNotEmpty(UserLoginPerson) && UtilValidate.isNotEmpty(UserLoginPerson.getString("partyId"))){
													communicationHistoryMap.put("fromPartyName",org.fio.homeapps.util.PartyHelper.getPartyName(delegator, UserLoginPerson.getString("partyId"), false));
												}
												
												communicationHistoryMap.put("toPartyName", org.fio.homeapps.util.UtilActivity.getPartyNamesFromCommExtension(delegator, UtilMisc.toMap("workEffortId", workEffortId, "workExtName", "TO", "wftExtType", "TO_TYPE")));
											}
										}
									}
																		
									//communicationHistoryMap.put("fromPartyName", org.fio.homeapps.util.PartyHelper.getPartyName(delegator, eachCommunicationEvent.get("partyIdFrom"), false));
									communicationHistoryMap.put("message", eachCommunicationEvent.getString("content"));
									communicationHistoryMap.put("entryDate", UtilValidate.isNotEmpty(eachCommunicationEvent.get("entryDate")) ? UtilDateTime.timeStampToString(eachCommunicationEvent.getTimestamp("entryDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
									communicationHistoryList.add(communicationHistoryMap);
								}

							}
							context.communicationHistoryList = communicationHistoryList;
						}
					}
				}
			}
		}
	}*/
	
	conditionList = FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
	conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	srPrimaryContactDetails = EntityUtil.getFirst( delegator.findList("CustRequestContact", mainConditons, null, null, null, false) );
	
	if (UtilValidate.isNotEmpty(srPrimaryContactDetails) && UtilValidate.isNotEmpty(srPrimaryContactDetails.getString("partyId"))){
		context.put("srCommPrimaryContactId", srPrimaryContactDetails.getString("partyId"));
	}
	
	CustRequest = from("CustRequest").where("custRequestId", custRequestId).queryOne();
	
	if (UtilValidate.isNotEmpty(CustRequest) && UtilValidate.isNotEmpty(CustRequest.get("fromPartyId"))){
		roleTypeDetails = delegator.findOne("Party", UtilMisc.toMap("partyId", CustRequest.getString("fromPartyId")), false);		
		if(UtilValidate.isNotEmpty(roleTypeDetails) && "CUSTOMER".equals(roleTypeDetails.get("roleTypeId"))){
			context.put("srCommPrimaryContactId", CustRequest.get("fromPartyId"));
		}
	}
}



