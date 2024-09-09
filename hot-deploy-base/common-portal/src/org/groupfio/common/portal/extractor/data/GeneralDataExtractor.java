/**
 * 
 */
package org.groupfio.common.portal.extractor.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.CommonPortalConstants.EmailVerifyStatus;
import org.groupfio.common.portal.extractor.constants.DataConstants;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.common.portal.util.UtilAttribute;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class GeneralDataExtractor extends DataExtractor {

	private static String MODULE = GeneralDataExtractor.class.getName();
	
	public GeneralDataExtractor(Data extractedData) {
		super(extractedData);
	}
	
	@Override
	public Map<String, Object> retrieve(Map<String, Object> context) {
		if (UtilValidate.isNotEmpty(extractedData)) {
			extractedData.retrieve(context);
		}
		return retrieveData(context);
	}

	private Map<String, Object> retrieveData(Map<String, Object> context) {
		System.out.println("Start retrieve General Info");
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator"); 
				Map<String, Object> request = (Map<String, Object>) context.get("request"); 
				response = (Map<String, Object>) context.get("response"); 
				Map<String, Object> generalInfoData = new LinkedHashMap<String, Object>();
				String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
				String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
				
				String domainEntityType = ParamUtil.getString(request, "domainEntityType");
				String domainEntityId = ParamUtil.getString(request, "domainEntityId");
				
				String partyId = ParamUtil.getString(request, "partyId");
				String custRequestId = ParamUtil.getString(request, "custRequestId");
				String workEffortId = ParamUtil.getString(request, "workEffortId");
				String salesOpportunityId =  ParamUtil.getString(request, "salesOpportunityId");
				
				//Debug.logInfo("General Data extractor context : "+context, MODULE);
				
				if (UtilValidate.isNotEmpty(partyId)) {
					String roleTypeId = ParamUtil.getString(request, "roleTypeId");
					if (UtilValidate.isEmpty(roleTypeId)) {
						roleTypeId = DataUtil.getPartyRoleTypeId(delegator, partyId);
					}
					
					if (UtilValidate.isNotEmpty(roleTypeId) && (roleTypeId.equals("ACCOUNT") || roleTypeId.equals("LEAD") || roleTypeId.equals("CUSTOMER"))) {
						String relationshipManager = org.fio.homeapps.util.PartyHelper.getCurrentResponsiblePartyName(partyId, roleTypeId, delegator);
						generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("RM"), Objects.toString(relationshipManager, ""));
					}
					
					String isPopulateAgrmData = ParamUtil.getString(request, "isPopulateAgrmData");
					if (UtilValidate.isNotEmpty(domainEntityId) ) {
						GenericValue agreement = EntityQuery.use(delegator).from("Agreement").select("partyIdTo","description","thruDate").where("agreementId", domainEntityId).cache(false).queryFirst();
						
					if (UtilValidate.isNotEmpty(isPopulateAgrmData) && isPopulateAgrmData.equals("Y")) {
						String esignatureLink = "/rebate-portal/control/doeSignatureFromMail?partyId="+partyId+"&domainEntityId="+domainEntityId;
						Debug.log("esignatureLink=== "+esignatureLink);
						generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("E_SIGNATURE"),  Objects.toString(esignatureLink, ""));
						String isContractAgreement = ParamUtil.getString(request, "isContractAgreement");
						if (UtilValidate.isNotEmpty(isContractAgreement) && isContractAgreement.equals("Y")) {
							esignatureLink = "/contract-portal/control/contractSignature?partyId="+partyId+"&domainEntityId="+domainEntityId;
							generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("E_SIGNATURE"),  Objects.toString(esignatureLink, ""));
							
							generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_ID"),  Objects.toString(domainEntityId, ""));
							generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_NAME"),  Objects.toString(agreement.getString("description"), ""));
							String agmtThruDate = UtilDateTime.timeStampToString(agreement.getTimestamp("thruDate"), globalDateFormat, TimeZone.getDefault(), null);
							generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_THRU_DATE"),  Objects.toString(agmtThruDate, ""));
						}
							String is2G = UtilAttribute.getAgreementAttrValue(delegator, domainEntityId, "IS_2G_AGRMT");
							String salesManagerPartyId = UtilAttribute.getAgreementAttrValue(delegator, domainEntityId, "SM_PARTYID");
							String agreementYear = UtilAttribute.getAgreementAttrValue(delegator, domainEntityId, "AGREEMENT_YEAR");
							
							if (UtilValidate.isNotEmpty(agreementYear)) {
								generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_YEAR"), Objects.toString(agreementYear, ""));
								int agreementNextYear = UtilDateTime.getNextYear( UtilDateTime.stringToTimeStamp(agreementYear, "yyyy", TimeZone.getDefault(), Locale.getDefault()));
								generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_NXT_YEAR"), Objects.toString(agreementNextYear, ""));
							}
							if (UtilValidate.isNotEmpty(salesManagerPartyId)) {
								generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_SM_NAME"), Objects.toString(DataUtil.getPartyName(delegator, salesManagerPartyId), ""));
							}
							  
							String agmtPartyIdTo = agreement.getString("partyIdTo");
							String agmtCustName = DataUtil.getPartyName(delegator, agmtPartyIdTo);
							if (UtilValidate.isNotEmpty(agmtCustName)) {
								agmtCustName = DataHelper.convertToLabel(agmtCustName);
								generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_CUST_NAME"), Objects.toString(agmtCustName, ""));
							}
							if (UtilValidate.isNotEmpty(agmtPartyIdTo)) {
								generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("CUSTOMER_ID"), Objects.toString(agmtPartyIdTo, ""));
							}
							
							if(UtilValidate.isNotEmpty(is2G) && is2G.equals("N")) {
								List<String> programTypeIds = new ArrayList<>();
								programTypeIds.add("2G");
								programTypeIds.add("CSR");
								EntityCondition programConditions = EntityCondition.makeCondition(UtilMisc.toList(
										EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, domainEntityId),
										EntityCondition.makeCondition("agreementItemTypeId", EntityOperator.IN, programTypeIds)),
										EntityOperator.AND);
								List<GenericValue> agreementProgList = delegator.findList("AgreementItem", programConditions, null, UtilMisc.toList("agreementItemSeqId ASC"), null, false);
								if (UtilValidate.isNotEmpty(agreementProgList)) {
									List<String> agreementItemSeqIdList = EntityUtil.getFieldListFromEntityList(agreementProgList, "agreementItemSeqId", false);	
									if(UtilValidate.isNotEmpty(agreementItemSeqIdList)) {
										EntityCondition nameConditions = EntityCondition.makeCondition(UtilMisc.toList(
												EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, domainEntityId),
												EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "ITEM_NAME"),
												EntityCondition.makeCondition("agreementItemSeqId", EntityOperator.IN, agreementItemSeqIdList)),
												EntityOperator.AND);

										List<GenericValue> agreementItemSeqIdNameList = delegator.findList("AgreementItemAttribute", nameConditions, null, UtilMisc.toList("agreementItemSeqId ASC"), null, false);
										if (UtilValidate.isNotEmpty(agreementItemSeqIdNameList)) {
											List<String> agreementItemNameList = EntityUtil.getFieldListFromEntityList(agreementItemSeqIdNameList, "attrValue", false);	
											String agreementNames = StringUtil.join(agreementItemNameList, " and ");
											generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGREEMENT_PROGRAM"), Objects.toString(agreementNames, ""));
										}
									}
								}
								
								//get Agreement terms
								List termconditions = FastList.newInstance();
								String agreementItemSeqId = null;
								int tierContentCount = 0;
								
								String termLists ="";
								List<GenericValue> itemList = EntityQuery.use(delegator).from("AgreementItem")
										.where(
												EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, domainEntityId),
												EntityCondition.makeCondition("agreementItemTypeId", EntityOperator.IN, Arrays.asList("CSR"))
												).orderBy("agreementItemTypeId").queryList();
								tierContentCount += itemList.size();
								if(UtilValidate.isNotEmpty(itemList)) {
									for (GenericValue item : itemList) {
										termLists += DataHelper.prepareAgreementTierData(delegator, UtilMisc.toMap("agreementId", item.getString("agreementId"), "agreementItemSeqId", item.getString("agreementItemSeqId"), "agreementItemTypeId", item.getString("agreementItemTypeId"), "amountType", item.getString("amountType"), "contractText", item.getString("contractText")));
									}
								}
								generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_CSR_CONT"), Objects.toString(termLists, ""));
								
								termLists ="";
								itemList = EntityQuery.use(delegator).from("AgreementItem")
										.where(
												EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, domainEntityId),
												EntityCondition.makeCondition("agreementItemTypeId", EntityOperator.IN, Arrays.asList("VOLUME_REBATE"))
												).orderBy("agreementItemTypeId").queryList();
								tierContentCount += itemList.size();
								if(UtilValidate.isNotEmpty(itemList)) {
									for (GenericValue item : itemList) {
										termLists += DataHelper.prepareAgreementTierData(delegator, UtilMisc.toMap("agreementId", item.getString("agreementId"), "agreementItemSeqId", item.getString("agreementItemSeqId"), "agreementItemTypeId", item.getString("agreementItemTypeId"), "amountType", item.getString("amountType"), "contractText", item.getString("contractText")));
									}
								}
								generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_VOL_CONT"), Objects.toString(termLists, ""));
								
								termLists ="";
								itemList = EntityQuery.use(delegator).from("AgreementItem")
										.where(
												EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, domainEntityId),
												EntityCondition.makeCondition("agreementItemTypeId", EntityOperator.IN, Arrays.asList("ADDITIONAL_REBATE"))
												).orderBy("agreementItemTypeId").queryList();
								tierContentCount += itemList.size();
								if(UtilValidate.isNotEmpty(itemList)) {
									for (GenericValue item : itemList) {
										termLists += DataHelper.prepareAgreementTierData(delegator, UtilMisc.toMap("agreementId", item.getString("agreementId"), "agreementItemSeqId", item.getString("agreementItemSeqId"), "agreementItemTypeId", item.getString("agreementItemTypeId"), "amountType", item.getString("amountType"), "contractText", item.getString("contractText")));
									}
								}
								generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_ADDIT_CONT"), Objects.toString(termLists, ""));
								
								String content2g = "";
								List<GenericValue> item2gList = EntityQuery.use(delegator).from("AgreementItem")
										.where(
												EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, domainEntityId),
												EntityCondition.makeCondition("agreementItemTypeId", EntityOperator.EQUALS, "2G")
												).queryList();
								if (UtilValidate.isNotEmpty(item2gList)) {
									for (GenericValue agreementItem2g : item2gList) {
										String programName = UtilAttribute.getAgreementItemAttrValue(delegator, agreementItem2g.getString("agreementId"), agreementItem2g.getString("agreementItemSeqId"), "ITEM_NAME");
										termconditions = FastList.newInstance();
										agreementItemSeqId  = agreementItem2g.getString("agreementItemSeqId");
										String contractText = UtilValidate.isNotEmpty(agreementItem2g.getString("contractText")) ? agreementItem2g.getString("contractText") : "";

										termconditions.add(EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, domainEntityId));
										termconditions.add(EntityCondition.makeCondition("agreementItemSeqId", EntityOperator.EQUALS, agreementItemSeqId));

										EntityCondition mainTermConditons = EntityCondition.makeCondition(termconditions, EntityOperator.AND);
										GenericValue term = EntityQuery.use(delegator).from("AgreementTerm").where(mainTermConditons).queryFirst();
										
										String rebateValue = "1";
										if (UtilValidate.isNotEmpty(term)) {
											rebateValue = term.getString("rebateAmount");
										}
										
										String amountType = "%";
										if (UtilValidate.isNotEmpty(agreementItem2g.getString("amountType")) && agreementItem2g.getString("amountType").equals("AMOUNT")) {
											amountType = "$";
										}
										
										content2g += "<table align='left' border='0' cellpadding='0' cellspacing='0' class='deviceWidth mob-col' style='border: 1px solid black;border-collapse: collapse; margin-right: 10px; margin-bottom: 10px;' width='100%'>"
											+ "<tbody>"
												+ "<tr>"
										+"<th width='50%' style='font-size: 14px;border: 1px solid black;border-collapse: collapse;text-align: center;padding: 2px 5px 2px 5px;font-weight: normal;'><font face='Segoe UI, sans-serif' size='3'>2g Rebate</font></th>";
											if (UtilValidate.isNotEmpty(contractText)) {
												content2g += "<th width='50%' style='font-size: 14px;border: 1px solid black;border-collapse: collapse;text-align: center;padding: 2px 5px 2px 5px;font-weight: normal;'><font face='Segoe UI, sans-serif' size='3'>Contract Description</font></th>";
											}
													
										content2g += "</tr>"
													 + "<tr>"
													 + "<td width='50%' style='font-size: 14px;border: 1px solid black;border-collapse: collapse;text-align: left;padding: 2px 5px 2px 5px;'><font face='Segoe UI, sans-serif' size='3'>"+rebateValue+amountType+" rebate on all purchases made through REEB's 2g electronic selling system.</font></td>";
											if (UtilValidate.isNotEmpty(contractText)) {
												content2g += "<td width='50%' style='font-size: 14px;border: 1px solid black;border-collapse: collapse;text-align: left;padding: 2px 5px 2px 5px;vertical-align: top;'><font face='Segoe UI, sans-serif' size='3'>"+contractText+"</font></td>";
											}
													
										content2g += "</tr>"
											+ "</tbody>"
										+ "</table>";
									}
									
									generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_2G_CONT"), Objects.toString(content2g, ""));
								}
								
								if (tierContentCount==0 && UtilValidate.isEmpty(item2gList)) {
									termLists += "<table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"deviceWidth mob-col\" width=\"35%\" style=\"border: 1px solid black;border-collapse: collapse;\">";
									termLists += "<tr>";
									termLists += "<th style=\"border: 1px solid black;border-collapse: collapse;text-align: left;padding: 2px 5px 2px 5px;font-size: 14px;font-weight: normal;\">Purchase Tier</th>";
									termLists += "<th style=\"border: 1px solid black;border-collapse: collapse;text-align: left;padding: 2px 5px 2px 5px;font-size: 14px;font-weight: normal;\">Rebate %</th>";
									termLists += "<tr>";
									termLists += " <td style=\"border: 1px solid black;border-collapse: collapse;text-align: right;font-size: 14px;\">"+"No Data To Display"+"</td>";
									termLists +=" </tr>";
									termLists +="</table>";
									
									generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_CSR_LIST"), Objects.toString(termLists, ""));
								}
								
								
							}
							
							if (UtilValidate.isNotEmpty(domainEntityId)) {
								String addressInfo = DataHelper.wrapRoofTopPostalAddress(delegator, UtilMisc.toMap("domainEntityId", domainEntityId));
								generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("AGMT_DEALER_ADD"), Objects.toString(addressInfo, ""));
							}
							
						}
						
					}
					
					EntityCondition conditions = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                	EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
                	EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_TEAM")),
                	EntityOperator.AND);
                	List<GenericValue> teamList = delegator.findList("PartyRoleStatusAndPartyDetail", conditions, null, UtilMisc.toList("groupName ASC"), null, false);
                	List<String> teamNameList = EntityUtil.getFieldListFromEntityList(teamList, "groupName", false);
                	if (UtilValidate.isNotEmpty(teamNameList)) {
                		String teamNames = StringUtil.join(teamNameList, ", ");
                		generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("TEAM"), Objects.toString(teamNames, ""));
                	}
                	
                	String fromEmail = ParamUtil.getString(request, "fromEmail");
                	if (UtilValidate.isNotEmpty(fromEmail)) {
                		generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("REPLY_TO"), Objects.toString(fromEmail, ""));
                	}
                	
                	GenericValue userLogin = EntityUtil.getFirst( delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId), null, false) );
                	if (UtilValidate.isNotEmpty(userLogin)) {
                		generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("USER_LOGIN_ID"), Objects.toString(userLogin.getString("userLoginId"), ""));
                	
                		GenericValue userLoginAttr = delegator.findOne("UserLoginAttribute", UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId"),"attrName","OTP_PWD"), false);
                    	if (UtilValidate.isNotEmpty(userLoginAttr)) {
                    		generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("USER_LOGIN_PASS"), Objects.toString(userLoginAttr.getString("attrValue"), ""));
                    	}
                	}
                	
                	String currentDate = UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), globalDateFormat, TimeZone.getDefault(), null);
                	generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("CURRENT_DATE"), Objects.toString(currentDate, ""));
                	
                	if (UtilValidate.isNotEmpty(custRequestId)) {
                		GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId",custRequestId),false);
                		if(UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(custRequest.getString("custRequestName"))) {
                			generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_NUMBER"), Objects.toString(custRequest.getString("custRequestId"), ""));
                			generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_NAME"), Objects.toString(custRequest.getString("custRequestName"), ""));
                			
                			String srPrimaryPartyId = org.fio.homeapps.util.DataUtil.getSrPrimaryPartyId(delegator, custRequestId);
    						if (UtilValidate.isNotEmpty(srPrimaryPartyId)) {
    							String srPrimaryPartyName = PartyHelper.getPartyName(delegator, srPrimaryPartyId, false);
    							if (UtilValidate.isNotEmpty(srPrimaryPartyName)) {
    								generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_CUSTOMER"), Objects.toString(srPrimaryPartyName, ""));
    							}
    						}
    						if(UtilValidate.isNotEmpty(custRequest.get("createdDate"))){
    							String generatedDate = UtilDateTime.timeStampToString(custRequest.getTimestamp("createdDate"), globalDateFormat, TimeZone.getDefault(), null);
    							String generatedTime = UtilDateTime.timeStampToString(custRequest.getTimestamp("createdDate"), "HH:mm", TimeZone.getDefault(), null);
    							
    							generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_GENERATED_ON_DATE"), Objects.toString(generatedDate, ""));
    							generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_GENERATED_AT_TIME"), Objects.toString(generatedTime, ""));
    						}
    						
    						if(UtilValidate.isNotEmpty(custRequest.getString("lastModifiedByUserLogin"))){
    							Map<String, Object> userLoginInfo = org.fio.homeapps.util.DataUtil.getUserLoginInfo(delegator, custRequest.getString("lastModifiedByUserLogin"));
    							String userLoginFirstName = (String)userLoginInfo.get("firstName");
    							String userLoginLastName = (String)userLoginInfo.get("lastName");
    							String userLoginRoleTypeId = (String)userLoginInfo.get("roleTypeId");
    							String userLoginRoleTypeDesc = org.fio.homeapps.util.DataUtil.getRoleTypeDesc(delegator, userLoginRoleTypeId);
    							
    							if(UtilValidate.isNotEmpty(userLoginFirstName)){
    								generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_GENERATED_BY_FIRST_NAME"), Objects.toString(userLoginFirstName, ""));
    							}
    							if(UtilValidate.isNotEmpty(userLoginLastName)){
    								generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_GENERATED_BY_LAST_NAME"), Objects.toString(userLoginLastName, ""));
    							}
    							if(UtilValidate.isNotEmpty(userLoginRoleTypeId)){
    								generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_GENERATED_BY_USER_ROLE"), Objects.toString(userLoginRoleTypeDesc, ""));
    							}
    						}
    						
    						String srTrackerUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_TRACKER_URL");
							if(UtilValidate.isNotEmpty(srTrackerUrl)){
								//srTrackerUrl = srTrackerUrl+"?srNumber="+custRequestId;
								//generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_TRACKER_URL"), Objects.toString(srTrackerUrl, ""));
								
								String customFieldName = DataUtil.getGlobalValue(delegator, "FSRID_CUSTOM_FLD_NAME","FSRID");
								GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldName",customFieldName).queryFirst();
								
								if(UtilValidate.isNotEmpty(customField)) {
									String channelId = UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("groupId")) ? customField.getString("groupId") : "EXTERNAL_INFO";
    								String customFieldId = UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("customFieldId")) ? customField.getString("customFieldId") : "";
    								GenericValue custRequestAttribute = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId, "attrName", customFieldId, "channelId", channelId).queryFirst();
    								if(UtilValidate.isNotEmpty(custRequestAttribute)) {
    									String hashValue = custRequestAttribute.getString("attrValue");
    									if(UtilValidate.isNotEmpty(hashValue)) {
    										srTrackerUrl = srTrackerUrl+"#"+hashValue;
    										generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_TRACKER_URL"), Objects.toString(srTrackerUrl, ""));
    									}
    								} else {
    									String encodedCustReqId = Base64.getEncoder().encodeToString(custRequestId.getBytes("utf-8"));
    									if(UtilValidate.isNotEmpty(encodedCustReqId)) {
    										srTrackerUrl = srTrackerUrl+"#"+encodedCustReqId;
    										generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_TRACKER_URL"), Objects.toString(srTrackerUrl, ""));
    									}
    								}
								} else {
									String encodedCustReqId = Base64.getEncoder().encodeToString(custRequestId.getBytes("utf-8"));
									if(UtilValidate.isNotEmpty(encodedCustReqId)) {
										srTrackerUrl = srTrackerUrl+"#"+encodedCustReqId;
										generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_TRACKER_URL"), Objects.toString(srTrackerUrl, ""));
									}
								}
								
							}
    						
    						String fromPartyId = custRequest.getString("fromPartyId");
    						String dealerName = "";
    						String dealerContactName = "";
    						if (UtilValidate.isNotEmpty(fromPartyId)) {
    							dealerName = PartyHelper.getPartyName(delegator, fromPartyId, false);
    							List conditionList = FastList.newInstance();
    							conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
    							conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
    							conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
    							conditionList.add(EntityUtil.getFilterByDateExpr());
    			                EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			                GenericValue dealerContact = EntityQuery.use(delegator).from("CustRequestContact").where(mainConditons).queryFirst();
    			                if (UtilValidate.isNotEmpty(dealerContact)) {
    			                	dealerContactName = PartyHelper.getPartyName(delegator, dealerContact.getString("partyId"), false);
    			                }
    						}
    						generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_DEALER"), Objects.toString(dealerName, ""));
    						generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_DEALER_CONTACT"), Objects.toString(dealerContactName, ""));
    						
                		}
                		
                		String primaryPartyId = DataHelper.getPrimaryPerson(delegator, custRequestId);
                		String custRequestDomainType = custRequest.getString("custRequestDomainType");
                		if (UtilValidate.isNotEmpty(custRequestDomainType) && custRequestDomainType.equalsIgnoreCase("SERVICE")) {
                			String fromPartyId = custRequest.getString("fromPartyId");
                			String surveyUrlStr = "TICKET_PORTAL#"+custRequestId+"#"+fromPartyId;
                			surveyUrlStr = Base64.getEncoder().encodeToString(surveyUrlStr.getBytes("utf-8"));
                			String surveyUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FEEDBACK_SURVEY_URL");
                			if(UtilValidate.isNotEmpty(surveyUrl)) {
                				surveyUrl = surveyUrl + "?hv="+surveyUrlStr;
                				generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_SURVEY_URL"), Objects.toString(surveyUrl, ""));
                			}
                		}else if(UtilValidate.isNotEmpty(primaryPartyId)) {
                			String surveyUrlStr = "SERVICE_REQUEST#"+custRequestId+"#"+primaryPartyId;
                			surveyUrlStr = Base64.getEncoder().encodeToString(surveyUrlStr.getBytes("utf-8"));
                			String surveyUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SURVEY_URL");
                			if(UtilValidate.isNotEmpty(surveyUrl)) {
                				surveyUrl = surveyUrl + "?hv="+surveyUrlStr;
                				generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_SURVEY_URL"), Objects.toString(surveyUrl, ""));
                			}
                		}
                		
                		if(UtilValidate.isNotEmpty(custRequest.getString("responsiblePerson"))){
							Map<String, Object> srOwnerInfo = org.fio.homeapps.util.DataUtil.getUserLoginInfo(delegator, custRequest.getString("responsiblePerson"));
							String srOwnerFirstName = (String)srOwnerInfo.get("firstName");
							String srOwnerLastName = (String)srOwnerInfo.get("lastName");
							
							if(UtilValidate.isNotEmpty(srOwnerFirstName)){
								generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_OWNER_BY_FIRST_NAME"), Objects.toString(srOwnerFirstName, ""));
							}
							if(UtilValidate.isNotEmpty(srOwnerLastName)){
								generalInfoData.put(DataConstants.SR_INFO_TAG.get("SR_OWNER_BY_LAST_NAME"), Objects.toString(srOwnerLastName, ""));
							}
						}
                		
                	}
                	
                	if (UtilValidate.isNotEmpty(workEffortId)) {
                		generalInfoData.put(DataConstants.ACT_INFO_TAG.get("ACT_NUMBER"), Objects.toString(workEffortId, ""));
                		
                		GenericValue activity = EntityQuery.use(delegator).from("WorkEffort").select("workEffortName","description").where("workEffortId", workEffortId).queryFirst();
						if (UtilValidate.isNotEmpty(activity)) {
							generalInfoData.put(DataConstants.ACT_INFO_TAG.get("ACT_NAME"), Objects.toString(activity.getString("workEffortName"), ""));
							
							String actTrackerUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_TRACKER_URL");
							if (UtilValidate.isNotEmpty(actTrackerUrl)) {
								actTrackerUrl += "/sr-portal/control/viewActivity?workEffortId="+workEffortId+"&seqId="+workEffortId+"&partyId="+partyId+"&entity=WorkEffortCallSummary";
								generalInfoData.put(DataConstants.ACT_INFO_TAG.get("ACT_TRACKER_URL"), Objects.toString(actTrackerUrl, ""));
							}
							generalInfoData.put(DataConstants.ACT_INFO_TAG.get("ACT_DESCRIPTION"), Objects.toString(activity.getString("description"), ""));
							
							String activityViewLink = DataUtil.getGlobalValue(delegator, "ACTIVITY_VIEW_LINK", "/activity-portal/control/viewActivity?workEffortId=");
							activityViewLink = activityViewLink+workEffortId;
							generalInfoData.put(DataConstants.ACT_INFO_TAG.get("VIEW_ACTIVITY_LINK"), Objects.toString(activityViewLink, ""));
							
						}
                	}
                	
                	if (UtilValidate.isNotEmpty(request.get("approvalId"))) {
                		List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
                		conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, request.get("approvalId")));
                		EntityCondition mainConditon = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            			GenericValue approvalItem = EntityUtil.getFirst( delegator.findList("WorkEffortApproval", mainConditon, null, null, null, false) );
                		if (UtilValidate.isNotEmpty(approvalItem)) {
                			String apvStartDate = UtilValidate.isNotEmpty(approvalItem.getTimestamp("startDate")) ? UtilDateTime.timeStampToString(approvalItem.getTimestamp("startDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null;
							String apvEndDate = UtilValidate.isNotEmpty(approvalItem.getTimestamp("endDate")) ? UtilDateTime.timeStampToString(approvalItem.getTimestamp("endDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null;
							String apvCatName = EnumUtil.getEnumDescription(delegator, approvalItem.getString("approvalCategoryId"), "APPROVAL_CATEGORY");
							
							domainEntityType = UtilValidate.isNotEmpty(domainEntityType) ? domainEntityType : approvalItem.getString("domainEntityType");
							domainEntityId = UtilValidate.isNotEmpty(domainEntityId) ? domainEntityId : approvalItem.getString("domainEntityId");
							
							generalInfoData.put(DataConstants.APV_INFO_TAG.get("APVL_CAT_NAME"), Objects.toString(apvCatName, ""));
	                		generalInfoData.put(DataConstants.APV_INFO_TAG.get("APV_START_DATE"), Objects.toString(apvStartDate, ""));
	                		generalInfoData.put(DataConstants.APV_INFO_TAG.get("APV_END_DATE"), Objects.toString(apvEndDate, ""));
	                		generalInfoData.put(DataConstants.APV_INFO_TAG.get("APV_COMMENTS"), Objects.toString(approvalItem.getString("approvalComments"), ""));
                		}
                		
                		String rbtTrackerUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_TRACKER_URL");
                		String trackerUrl = rbtTrackerUrl + org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom(domainEntityId, domainEntityType, null);
                		if (UtilValidate.isNotEmpty(approvalItem.getString("approvalCategoryId")) && approvalItem.getString("approvalCategoryId").equals("APVL_CAT_3PL_INV")) {
                			trackerUrl += "#approval";
                		}
                		generalInfoData.put(DataConstants.APV_INFO_TAG.get("APV_TRACKER_URL"), Objects.toString(trackerUrl, ""));
                	}
                	
					GenericValue otp = EntityQuery.use(delegator).from("SecurityTracking").where(UtilMisc.toMap("partyId", partyId, "trackingTypeId", "EMAIL_OTP","statusId", EmailVerifyStatus.SENT)).queryFirst();
					if(UtilValidate.isNotEmpty(otp))
                		generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("OTP"), Objects.toString(otp.getString("value"), ""));
				}
				
				if (UtilValidate.isNotEmpty(domainEntityType) && UtilValidate.isNotEmpty(domainEntityId)) {
					List<GenericValue> contentList = EntityQuery.use(delegator).from("TemplateContent")
							.where(
									EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType),
									EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId)
									).filterByDate().orderBy("sequenceId").queryList();
					if (UtilValidate.isNotEmpty(contentList)) {
						String contentData = "<table align='center' bgcolor='#ffffff' border='0' cellpadding='0' cellspacing='0' class='deviceWidth' id='table' style='margin:0 auto;' width='700'>"
								+ "<tbody>";
						for (GenericValue content : contentList) {
							contentData += "<tr>";
							contentData += "<td bgcolor='#ffffff' class='mob-txt' style='font-size: 14px; color: #000; font-weight: normal; text-align: left; font-family: Segoe UI, sans-serif; line-height: 18px; vertical-align: top; padding:20px 0px 0px 0px'><font size='3'>"+content.getString("contentText")+"</font></td>";
							contentData += "</tr>";
						}
						contentData += "</tbody></table>";
						
						generalInfoData.put(DataConstants.CONTENT_INFO_TAG.get("MAIN_TPL_CONT"), Objects.toString(contentData, ""));
					}
				}
				
				if (UtilValidate.isNotEmpty(request.get("listOfFsr"))) {
            		List<GenericValue> listOfFsr = (List<GenericValue>) request.get("listOfFsr");
            		String fsrNames = "";
            		
            		fsrNames = listOfFsr.stream().map(x->{
            			return x.getString("custRequestId")+" ("+x.getString("custRequestName")+")";
            		}).collect(Collectors.joining("<br>"));
            		
            		generalInfoData.put(DataConstants.SR_INFO_TAG.get("LIST_OF_FSR"), Objects.toString(fsrNames, ""));
            	}
				
				if (UtilValidate.isNotEmpty(request.get("listOfFsp"))) {
            		List<GenericValue> listOfFsp = (List<GenericValue>) request.get("listOfFsp");
            		String spNames = "";
            		
            		spNames = listOfFsp.stream().map(x->{
            			String reason = "N/A";
            			if (UtilValidate.isNotEmpty(x.getString("logMsg1"))) {
            				reason = x.getString("logMsg1");
            			}
            			String executionDate = UtilDateTime.timeStampToString(x.getTimestamp("timeStamp"), globalDateTimeFormat, TimeZone.getDefault(), null);
            			return "Name: "+x.getString("processId")+", DateTime: "+executionDate+", Reason: "+reason;
            		}).collect(Collectors.joining("<br>"));
            		
            		generalInfoData.put(DataConstants.SP_INFO_TAG.get("LIST_OF_SP"), Objects.toString(spNames, ""));
            	}
				
				String applicationUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_URL");
				if (UtilValidate.isNotEmpty(applicationUrl)) {
					generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("APPLICATION_URL"), Objects.toString(applicationUrl, ""));
				}
				String applicationInternalUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_INTL_URL");
				if (UtilValidate.isNotEmpty(applicationInternalUrl)) {
					generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("APPLICATION_INTL_URL"), Objects.toString(applicationInternalUrl, ""));
				}
				
				if(UtilValidate.isNotEmpty(salesOpportunityId)) {
					generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("OPPORTUNITY_ID"), Objects.toString(salesOpportunityId, ""));
					GenericValue salesOpportunityGv = EntityQuery.use(delegator).from("SalesOpportunity").where("salesOpportunityId", salesOpportunityId).queryFirst();
					if(UtilValidate.isNotEmpty(salesOpportunityGv)) {
						String opportunityName =  salesOpportunityGv.getString("opportunityName");
						partyId =  salesOpportunityGv.getString("partyId");
						String partyName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyId, false);

						String ownerId = UtilValidate.isNotEmpty(salesOpportunityGv) ? salesOpportunityGv.getString("ownerId") : "";
						if(UtilValidate.isNotEmpty(ownerId)){
							String ownerPartyId =  DataUtil.getPartyIdByUserLoginId(delegator, ownerId);
							String ownerName = UtilValidate.isNotEmpty(ownerPartyId) ? DataUtil.getPartyName(delegator, ownerPartyId) : "";
							
							generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("OPPORTUNITY_OWNER_NAME"), Objects.toString(ownerName, ""));
						}
						String insideRep = UtilValidate.isNotEmpty(salesOpportunityGv) ? salesOpportunityGv.getString("insideRep") : "";
						if(UtilValidate.isNotEmpty(insideRep)){
							String insideRepName = UtilValidate.isNotEmpty(insideRep) ? DataUtil.getPartyName(delegator, insideRep) : "";

							generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("OPPORTUNITY_INSIDE_REP_NAME"), Objects.toString(insideRepName, ""));
						}
						//view opportunity link
						String oppoViewLink = "/opportunity-portal/control/viewOpportunity?salesOpportunityId="+salesOpportunityId;
						generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("OPPORTUNITY_VIEW_URL"), Objects.toString(oppoViewLink, ""));
						
						generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("OPPORTUNITY_NAME"), Objects.toString(opportunityName, ""));
						generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("PARTY_NAME"), Objects.toString(partyName, ""));
					}
				}
				
				if (UtilValidate.isNotEmpty(request.get("invoiceId"))) {
					generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("INVOICE_ID"), Objects.toString(request.get("invoiceId"), ""));
					generalInfoData.put(DataConstants.GENERAL_INFO_TAG.get("RECEIPT_URL"), Objects.toString(request.get("receiptUrl"), ""));
				}
            	
				response.put("generalInfoData", generalInfoData);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.log(e.getMessage(), MODULE);
		}
		
		return response;
	}
}
