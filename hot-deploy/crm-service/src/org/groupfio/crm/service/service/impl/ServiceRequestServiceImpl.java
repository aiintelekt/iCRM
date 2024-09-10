/**
 * 
 */
package org.groupfio.crm.service.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.SourceInvoked;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.ResponseUtils;
import org.fio.homeapps.util.UtilDateTime;
import org.fio.homeapps.util.UtilGenerator;
import org.groupfio.crm.service.resolver.Resolver;
import org.groupfio.crm.service.resolver.ResolverConstants.ResolverType;
import org.groupfio.crm.service.resolver.ResolverFactory;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;


/**
 * @author Sharif
 * @author Arshiya
 *
 */
public class ServiceRequestServiceImpl {
	private static final String MODULE = ServiceRequestServiceImpl.class.getName();

	public static Map createServiceRequest(DispatchContext dctx, Map context) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> custRequestContext = (Map<String, Object>) context.get("custRequestContext");
		Map<String, Object> supplementoryContext = (Map<String, Object>) context.get("supplementoryContext");
		Map<String, Object> channelPwebContext = (Map<String, Object>) context.get("channelPwebContext");
		List<Map<String, Object>> documents = (List<Map<String, Object>>) context.get("documents");
		List<Map<String, Object>> dynaList = (List<Map<String, Object>>) context.get("dynaList");
		String customerId = (String) custRequestContext.get("customerId");
		custRequestContext.remove("customerId");
		String endPointType = (String) context.get("endPointType");
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			String externalId = ParamUtil.getString(custRequestContext, "externalId");
			if (UtilValidate.isNotEmpty(externalId)) {
				EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, externalId));
				GenericValue custRequest = EntityUtil
						.getFirst(delegator.findList("CustRequest", mainCondition, null, null, null, false));
				if (UtilValidate.isNotEmpty(custRequest)) {
					result.putAll(ServiceUtil.returnError("sr already exists!"));
					return result;
				}
			}
			
			String custReqLoginId = ParamUtil.getString(custRequestContext, "custReqLoginId");
			String customerRelatedType = ParamUtil.getString(custRequestContext, "customerRelatedType");
			String fromPartyId = ParamUtil.getString(custRequestContext, "fromPartyId");
			String internalComment = ParamUtil.getString(custRequestContext, "internalComment");
			String sourceDocumentId = ParamUtil.getString(custRequestContext, "custReqDocumentNum");
			boolean isDummyCustomer = false;
            String cifPartyId = null;
            String roleTypeId = null;
            
            if (UtilValidate.isEmpty(GlobalConstants.CUSTOMER_ROLE_TYPE_BY_EXTERNALID.get(customerRelatedType))) {
            	cifPartyId = fromPartyId;
            	roleTypeId = customerRelatedType;
            } else {
            	roleTypeId = GlobalConstants.CUSTOMER_ROLE_TYPE_BY_EXTERNALID.get(customerRelatedType);
                isDummyCustomer = UtilValidate.isNotEmpty(fromPartyId)
                        && !DataUtil.isValidPartyIdentificationParty(delegator, fromPartyId, "CIF");
                
                if (UtilValidate.isNotEmpty(fromPartyId)) {
                    cifPartyId = DataUtil.getPartyIdentificationPartyId(delegator, fromPartyId, "CIF");
                }
                if(UtilValidate.isEmpty(cifPartyId))
                	cifPartyId = fromPartyId;
            }
            
			if( (UtilValidate.isEmpty(endPointType) || !endPointType.equals("MSD") ) && UtilValidate.isNotEmpty(internalComment) ) {	
				custRequestContext.remove("internalComment");
				byte[] base64decodedBytes = Base64.getDecoder().decode(internalComment);
				internalComment = new String(base64decodedBytes, "utf-8");
				custRequestContext.put("internalComment", internalComment);
			}
			
			String apiServiceName = "createServiceRequest";
			Timestamp createdDate = UtilDateTime.nowTimestamp();
			
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			GenericValue custRequest = delegator.makeValue("CustRequest");
			custRequest.setNonPKFields(custRequestContext);
			//custRequest.putAll(custRequestContext);
			if (UtilValidate.isEmpty(custRequest.getString("statusId"))) {
				custRequest.put("statusId", "SR_OPEN");
				custRequest.put("subStatusId", "SR_ASSIGNED");
			}
			
			if (UtilValidate.isNotEmpty(roleTypeId) && UtilValidate.isNotEmpty(cifPartyId)) {
                String partyId = cifPartyId;
                custRequest.put("fromPartyId", partyId);
			}
			
			/*if (UtilValidate.isNotEmpty(fromPartyId)
					&& !DataUtil.isValidPartyIdentificationParty(delegator, fromPartyId, "CIF")) {
				custRequest.put("fromPartyId", null);
			}
			if (DataUtil.isValidPartyIdentificationParty(delegator, custRequest.getString("fromPartyId"), "CIF")) {
				custRequest.put("fromPartyId",
						DataUtil.getPartyIdentificationPartyId(delegator, custRequest.getString("fromPartyId"), "CIF"));
			}*/
			
			String custRequestId = ParamUtil.getString(custRequestContext, "custRequestId");
			if (UtilValidate.isEmpty(custRequestId)) {
				custRequestId = UtilGenerator.getSrNumber(delegator, delegator.getNextSeqId("CustRequest"));
			}
			
			/*if (UtilValidate.isNotEmpty(endPointType) && endPointType.equals("MSD")) {
				custRequestId = UtilGenerator.getMsdSrNumber(delegator, custRequestId, custRequest.getString("custRequestTypeId"));
			} else {
				custRequestId = UtilGenerator.getSrNumber(delegator, custRequestId);
			}*/
			
			custRequest.put("custRequestId", custRequestId);
			if (UtilValidate.isNotEmpty(sourceDocumentId)) {
				custRequest.put("custReqDocumentNum", sourceDocumentId);
			}
			
			
			if (UtilValidate.isEmpty(custRequest.getString("externalId"))) {
				custRequest.put("externalId", custRequestId);
			}
			custRequest.put("createdByUserLogin",
					UtilValidate.isNotEmpty(custReqLoginId) ? custReqLoginId : userLogin.getString("userLoginId"));
			/*custRequest.put("lastModifiedByUserLogin",
					UtilValidate.isNotEmpty(custReqLoginId) ? custReqLoginId : userLogin.getString("userLoginId"));*/
			if (UtilValidate.isNotEmpty(custRequest.get("actualStartDate"))) {
				custRequest.put("createdDate", custRequest.get("actualStartDate"));
			} else {
				custRequest.put("createdDate", createdDate);
				custRequest.put("actualStartDate", createdDate);
			}
			
			if (UtilValidate.isEmpty(custRequestContext.get("custRequestDate"))) {
				custRequest.put("custRequestDate", createdDate);
			}
			
			//custRequest.put("lastModifiedDate", custRequest.get("createdDate"));
			if (UtilValidate.isNotEmpty(endPointType) && endPointType.equals("MSD")) {
				apiServiceName = "createServiceRequestMsd";
				if (UtilValidate.isEmpty(custRequestContext.get("custReqVplusId"))) {
					String custReqVplusId = DataUtil.getGlobalValue(delegator, "MSD_VPLUS_ID");
					custRequest.put("custReqVplusId", custReqVplusId);
				}
			}
			
			if(UtilValidate.isNotEmpty(custRequest.getString("statusId")) 
					&& ("SR_CLOSED".equals(custRequest.getString("statusId")) || "SR_CANCELLED".equals(custRequest.getString("statusId")))
					) {
				Timestamp closedByDate = (Timestamp) custRequestContext.get("closedByDate");
				if (UtilValidate.isEmpty(closedByDate)) {
					closedByDate = UtilDateTime.nowTimestamp();
				}
				custRequest.put("closedByUserLogin",userLogin.getString("userLoginId"));
				custRequest.put("closedByDate",closedByDate);
			}
			
			/*if(UtilValidate.isNotEmpty(custRequest.getString("statusId")) && ("SR_OPEN".equals(custRequest.getString("statusId")))) {
				custRequest.put("openDateTime",UtilDateTime.nowTimestamp());
			}*/
			
			custRequest.put("cif", fromPartyId);
			/*if (isDummyCustomer) {
				custRequest.put("fromPartyId", "99999");
			}*/
			// added for testing the character set
			Debug.log("SR1 description---->"+custRequestContext.get("description")+"---custRequest-obj--->"+custRequest, MODULE);
			
			custRequest.create();
			
			// supplementory [start]
			GenericValue supplementory = delegator.makeValue("CustRequestSupplementory");
			supplementory.putAll(supplementoryContext);
			supplementory.put("custRequestId", custRequestId);
			supplementory.put("sourceInvoked", SourceInvoked.API);
			supplementory.create();
			// supplementory [end]
			// cust request channel pweb [start]
			GenericValue channelPweb = delegator.makeValue("CustRequestChannelPweb");
			channelPweb.putAll(channelPwebContext);
			channelPweb.put("custRequestId", custRequestId);
			channelPweb.create();
			// cust request channel pweb [end]
			// UserLoginHistory [start]
			if (UtilValidate.isNotEmpty(custRequest.getString("custReqLoginId"))) {
				GenericValue loginParty = DataUtil.findPartyByLogin(delegator, custRequest.getString("custReqLoginId"));
				GenericValue loginHistory = delegator.makeValue("UserLoginHistoryCustom");
				loginHistory.put("userLoginHistoryCustomId", org.fio.homeapps.util.UtilGenerator.getNextSeqId());
				loginHistory.put("userLoginId", custRequest.getString("custReqLoginId"));
				loginHistory.put("fromDate", UtilDateTime.nowTimestamp());
				loginHistory.put("partyId", loginParty.getString("partyId"));
				loginHistory.put("serviceName", apiServiceName);
				loginHistory.create();
			}
			// UserLoginHistory [end]
			// content [start]
			if (UtilValidate.isNotEmpty(documents)) {
				for (Map<String, Object> document : documents) {
					
					String fileName = ParamUtil.getString(document, "file_name");
					String annotationId = ParamUtil.getString(document, "annotationid");
					
					if (UtilValidate.isNotEmpty(fileName) ||  UtilValidate.isNotEmpty(annotationId)) {
						
						String cntCreatedDate = ParamUtil.getString(document, "cnt_created_date");
						if (UtilValidate.isNotEmpty(cntCreatedDate)) {
							createdDate = ParamUtil.getDateTime(document, "cnt_created_date");
						}
						
						GenericValue content = delegator.makeValue("Content");
						String contentId = delegator.getNextSeqId("Content");
						
						content.put("contentId", contentId);
						content.put("contentTypeId", "DOCUMENT");
						content.put("localeString", null);
						content.put("createdDate", createdDate);
						content.put("createdByUserLogin", ParamUtil.getString(document, "cnt_created_by_user_login"));
						content.put("description", ParamUtil.getString(document, "cnt_description"));
						content.put("contentName", ParamUtil.getString(document, "file_name"));
						content.put("documentRefNum", ParamUtil.getString(document, "document_ref_num"));
						content.put("annotationId", ParamUtil.getString(document, "annotationid"));
						content.create();
						
						GenericValue crContent = delegator.makeValue("CustRequestContent");
						crContent.put("custRequestId", custRequestId);
						crContent.put("contentId", contentId);
						crContent.put("fromDate", createdDate);
						crContent.create();
						
					}
				}
			}
			// content [end]
			// cust request attribute [start]
			if (UtilValidate.isNotEmpty(dynaList)) {
				int count = 1;
				for (Map<String, Object> dyna : dynaList) {
					String fieldName = ParamUtil.getString(dyna, "field_name");
					if (UtilValidate.isNotEmpty(fieldName)) {
						GenericValue attribute = delegator.makeValue("CustRequestAttribute");
						attribute.put("custRequestId", custRequestId);
						attribute.put("attrName", ParamUtil.getString(dyna, "field_name"));
						attribute.put("attrValue", ParamUtil.getString(dyna, "field_value"));
						attribute.put("attrLocal", ParamUtil.getString(dyna, "field_local_name"));
						if (UtilValidate.isNotEmpty(attribute.getString("attrLocal"))) {
							attribute.put("locale", "zh-CN");
						}
						attribute.put("channelId", custRequest.getString("salesChannelEnumId"));
						attribute.put("sequenceNumber", UtilGenerator.getNextSeqId(count++));
						attribute.create();
					}
				}
			}
			// cust request attribute [end]
			
			// dummy customer [start]
			if (isDummyCustomer) {
				GenericValue attribute = delegator.makeValue("CustRequestAttribute");
				attribute.put("custRequestId", custRequestId);
				attribute.put("attrName", "CIF_REFERENCE");
				attribute.put("attrValue", fromPartyId);
				// attribute.put("attrLocal", ParamUtil.getString(dyna,
				// "field_local_name"));
				attribute.put("channelId", custRequest.getString("salesChannelEnumId"));
				attribute.put("sequenceNumber", UtilGenerator.getNextSeqId(1));
				attribute.create();
				
			}
			// dummy customer [end]
			
			// create NON CRM customer [start]
			if (UtilValidate.isEmpty(fromPartyId) && (UtilValidate.isNotEmpty(roleTypeId))) {
				fromPartyId = null;
				if (UtilValidate.isNotEmpty(custRequest.getString("custReqVplusId"))) {
					EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "VPLUS_NUMBER"),
							EntityCondition.makeCondition("idValue", EntityOperator.EQUALS,custRequest.getString("custReqVplusId")));
					GenericValue attribute = EntityUtil
							.getFirst(delegator.findList("PartyIdentification", mainCondition, null, null, null, false));
					if (UtilValidate.isNotEmpty(attribute)) {
						fromPartyId = attribute.getString("partyId");
						custRequest.put("fromPartyId", fromPartyId);
						custRequest.store();
					}
				}
				if (UtilValidate.isEmpty(fromPartyId)) {
					callCtxt = FastMap.newInstance();
					callCtxt.put("roleTypeId", roleTypeId);
					callCtxt.put("isNonCrm", "Y");
					callCtxt.put("vplusNumber", custRequest.getString("custReqVplusId"));
					callCtxt.put("firstName", custRequest.getString("custReqNonFirstName"));
					callCtxt.put("lastName", custRequest.getString("custReqNonLastName"));
					callCtxt.put("nationalId", custRequest.getString("custReqNatId"));
					callCtxt.put("userLogin", userLogin);
					callResult = dispatcher.runSync("crmPortal.createCustomer", callCtxt);
					if (ServiceUtil.isSuccess(callResult)) {
						fromPartyId = ParamUtil.getString(callResult, "partyId");
						custRequest.put("fromPartyId", fromPartyId);
						custRequest.store();
					}
				}
			}
			// create NON CRM customer [end]
			// prepare escalation [start]
			//if (UtilValidate.isNotEmpty(custRequest.getString("statusId")) && custRequest.getString("statusId").equals("SR_OPEN")) {
			
			if( (UtilValidate.isEmpty(endPointType) || !endPointType.equals("MSD")) ) {
				
				String escalationLevel = UtilValidate.isNotEmpty(custRequest.getString("escalationLevel"))
						? custRequest.getString("escalationLevel") : "1";
						
				Map<String, Object> escalationContext = new LinkedHashMap<String, Object>();
				escalationContext.put("delegator", delegator);
				escalationContext.put("escalationLevel", escalationLevel);
				escalationContext.put("typeId", custRequest.getString("custRequestTypeId"));
				escalationContext.put("categoryId", custRequest.getString("custRequestCategoryId"));
				escalationContext.put("subCategoryId", custRequest.getString("custRequestSubCategoryId"));
				escalationContext.put("priority", custRequest.getString("priority"));
				escalationContext.put("createdDate", custRequest.getTimestamp("createdDate"));
				escalationContext.put("businessUnit", custRequest.getString("ownerBu"));
				escalationContext.put("statusId", custRequest.getString("statusId"));
				escalationContext.put("statusClosedEscTime", supplementory.get("statusClosedEscTime"));
				escalationContext.put("isCalculateCommitDate", "Y");
				Resolver resolver = ResolverFactory.getResolver(ResolverType.ESCALATION_RESOLVER);
				Map<String, Object> escalationResult = resolver.resolve(escalationContext);
				if (UtilValidate.isNotEmpty(escalationResult.get("escalationLevel"))) {
					custRequest.put("escalationLevel", escalationResult.get("escalationLevel"));
					custRequest.store();
				}
				if (UtilValidate.isNotEmpty(escalationResult.get("commitDate"))) {
					supplementory.put("commitDate", escalationResult.get("commitDate"));
					
					if(UtilValidate.isNotEmpty(escalationResult.get("_pre_escalation_date"))) {
						supplementory.put("preEscalationDate", escalationResult.get("_pre_escalation_date"));
					} else {
						supplementory.put("preEscalationDate", null);
					}
					if(UtilValidate.isNotEmpty(escalationResult.get("_escalation_date_1"))) {
						supplementory.put("escalationDate1", escalationResult.get("_escalation_date_1"));
					} else {
						supplementory.put("escalationDate1", null);
					}
					if(UtilValidate.isNotEmpty(escalationResult.get("_escalation_date_2"))) {
						supplementory.put("escalationDate2", escalationResult.get("_escalation_date_2"));
					} else {
						supplementory.put("escalationDate2", null);
					}
					if(UtilValidate.isNotEmpty(escalationResult.get("_escalation_date_3"))) {
						supplementory.put("escalationDate3", escalationResult.get("_escalation_date_3"));
					} else {
						supplementory.put("escalationDate3", null);
					}
					
				} else {
					supplementory.put("commitDate", null);
					supplementory.put("preEscalationDate", null);
					supplementory.put("escalationDate1", null);
					supplementory.put("escalationDate2", null);
					supplementory.put("escalationDate3", null);
				}
				
				supplementory.put("statusEscTime", escalationResult.get("statusEscTime"));
				supplementory.put("statusClosedEscTime", escalationResult.get("statusClosedEscTime"));
				
				supplementory.store();
			}
			
			//}
			// prepare escalation [end]
			
			// prepare TAT [start]
			Timestamp closedByDate = (Timestamp) custRequestContext.get("closedByDate");
			if (UtilValidate.isNotEmpty(custRequest.getString("statusId"))
					&& "SR_CLOSED".equals(custRequest.getString("statusId"))) {
				
				Debug.log("TAT SR ID# " + custRequest.getString("custRequestId"));
				Debug.log("TAT SR status========" + custRequest.getString("statusId"));
				
				Map<String, Object> tatContext = new LinkedHashMap<String, Object>();
				tatContext.put("delegator", delegator);
				tatContext.put("tatCalc", custRequest.getString("tatCalc"));
				tatContext.put("businessUnit", custRequest.getString("ownerBu"));
				tatContext.put("createdDate", custRequest.getTimestamp("createdDate"));
				
				Debug.log("closedDate before========" + closedByDate);
				tatContext.put("closedDate", UtilValidate.isNotEmpty(closedByDate) ? closedByDate : UtilDateTime.nowTimestamp());
				Debug.log("closedDate after========" + tatContext.get("closedDate"));
				
				custRequest.put("closedByDate", tatContext.get("closedDate"));
				int closedHour = UtilDateTime.getHour((Timestamp) tatContext.get("closedDate"),
						TimeZone.getDefault(), Locale.getDefault());
				Debug.log("closedHour========" +closedHour);
				
				Resolver resolver = ResolverFactory.getResolver(ResolverType.TAT_RESOLVER);
				Map<String, Object> tatResult = resolver.resolve(tatContext);
				
				if (ResponseUtils.isSuccess(tatResult)) {
					/*
					BigDecimal tatHoursCalculated = ParamUtil.getBigDecimal(tatResult, "tatHours");
					BigDecimal tatMinsCalculated = ParamUtil.getBigDecimal(tatResult, "tatMins");
					if (UtilValidate.isNotEmpty(tatHoursCalculated) && UtilValidate.isNotEmpty(tatMinsCalculated)) {
						tatHoursCalculated = tatHoursCalculated.add(tatMinsCalculated).setScale(2, BigDecimal.ROUND_HALF_UP);
					}
					
					custRequest.put("tatDays", ParamUtil.getBigDecimal(tatResult, "tatDays"));
					custRequest.put("tatHours", tatHoursCalculated);
					*/
					custRequest.put("tatDays", ParamUtil.getBigDecimal(tatResult, "tatDays"));
					custRequest.put("tatHours", ParamUtil.getBigDecimal(tatResult, "tatHours"));
					custRequest.put("tatMins", ParamUtil.getBigDecimal(tatResult, "tatMins"));
				}else {
					Debug.log("Error Tat calculation=="+ResponseUtils.isError(tatResult));
				}
				
				custRequest.store();
			}
			// prepare TAT [end]
			
			// prepare SLA TAT [start]
			String isEnabledStaTat = DataUtil.getGlobalValue(delegator, "SLA_TAT_ENABLE","N");
			String slaTatStopStatus = DataUtil.getGlobalValue(delegator, "SLA_TAT_STOP_STATUS","SR_CLOSED");
			List slaTatStopList = DataUtil.stringToList(slaTatStopStatus, ",");
			if (isEnabledStaTat.equalsIgnoreCase("Y") && 
					UtilValidate.isNotEmpty(custRequest.getString("statusId")) && slaTatStopList.contains(custRequest.getString("statusId"))) {
				
				Debug.log("SLA TAT SR ID# " + custRequest.getString("custRequestId"));
				Debug.log("SLA TAT SR status========" + custRequest.getString("statusId"));
				
				Map<String, Object> tatContext = new LinkedHashMap<String, Object>();
				tatContext.put("delegator", delegator);
				tatContext.put("custRequestId", custRequestId);
				tatContext.put("tatCalc", isEnabledStaTat);
				tatContext.put("businessUnit", custRequest.getString("ownerBu"));
				createdDate = custRequest.getTimestamp("createdDate");
				tatContext.put("createdDate", createdDate);
				Debug.log("closedDate before========" + closedByDate);
				tatContext.put("closedDate", UtilValidate.isNotEmpty(closedByDate) ? closedByDate : UtilDateTime.nowTimestamp());
				Debug.log("closedDate after========" + tatContext.get("closedDate"));
				tatContext.put("statusId", custRequest.getString("statusId"));
				
				custRequest.put("closedByDate", tatContext.get("closedDate"));
				
				Resolver resolver = ResolverFactory.getResolver(ResolverType.SLA_TAT_RESOLVER);
				Map<String, Object> tatResult = resolver.resolve(tatContext);
				BigDecimal slaTatDays =BigDecimal.ZERO;
				if (ResponseUtils.isSuccess(tatResult)) {
					slaTatDays = UtilValidate.isNotEmpty(ParamUtil.getBigDecimal(tatResult, "tatDays"))?ParamUtil.getBigDecimal(tatResult, "tatDays"):BigDecimal.ZERO;
					GenericValue custReqAttr = delegator.findOne("CustRequestAttribute", UtilMisc.toMap("custRequestId", custRequestId,"attrName","SLA_TAT"),false);
					if (UtilValidate.isEmpty(custReqAttr)) {
						GenericValue custRequestAttribute = delegator.makeValue("CustRequestAttribute");
						custRequestAttribute.put("custRequestId", custRequestId);
						custRequestAttribute.put("attrName", "SLA_TAT");
						custRequestAttribute.put("attrValue", ""+slaTatDays);
						delegator.create(custRequestAttribute);
					}else {
						custReqAttr.put("attrValue", ""+slaTatDays);
						custReqAttr.store();
					}
				}else {
					Debug.log("Error Sla Tat calculation=="+ResponseUtils.isError(tatResult));
				}
			}
			// prepare SLA TAT [end]
			
			result.put("custRequestId", custRequestId);
			
			//for notification
			String userLoginPartyId = UtilValidate.isNotEmpty(userLogin) ? userLogin.getString("partyId") : "";
			String userLoginName = DataUtil.getUserLoginName(delegator, userLoginPartyId);
			
			String eventName = DataUtil.getGlobalValue(delegator, "SR_CREATE_EVENT_NAME", "Create FSR");
			String eventDescription = DataUtil.getGlobalValue(delegator, "SR_CREATE_EVENT_DESC", "FSR ({0}) created by {1}");
			String eventUrl = DataUtil.getGlobalValue(delegator, "SR_EVENT_URL", "");
			if(eventDescription.contains("{0}")) {
				eventDescription = MessageFormat.format(eventDescription, new Object[] { custRequestId , userLoginName});
			}
			
			String viewUrl = "";
			if(UtilValidate.isNotEmpty(eventUrl)) {
				if(eventUrl.contains("{0}")) {
					eventUrl = MessageFormat.format(eventUrl, new Object[] { custRequestId });
				}
				viewUrl = eventUrl;
			} else {
				HttpServletRequest request = UtilValidate.isNotEmpty(custRequestContext.get("request")) ? (HttpServletRequest) custRequestContext.get("request") : null;
				String serverRootUrl = UtilValidate.isNotEmpty(request) ? UtilHttp.getServerRootUrl(request) : "";
				String contextPath =  UtilValidate.isNotEmpty(request) ? request.getContextPath() : "";
				
				if(UtilValidate.isNotEmpty(serverRootUrl) && UtilValidate.isNotEmpty(contextPath)) {
					viewUrl = serverRootUrl+contextPath+"/control/viewServiceRequest?srNumber="+custRequestId;
				}
			}
			
			String entityOwnerId = (String) custRequestContext.get("responsiblePerson");
			result.put("entityId", custRequestId);
			result.put("entityName", "CustRequest");
			result.put("eventType", "SERVICE_REQUEST");
			result.put("eventDate", org.ofbiz.base.util.UtilDateTime.nowTimestamp());
			result.put("eventName", eventName);
			result.put("eventDescription", eventDescription);
			result.put("eventUrl", viewUrl);
			result.put("domainEntityId", custRequestId);
			result.put("domainEntityType", "SERVICE_REQUEST");
			result.put("entityOwnerId", entityOwnerId );
			
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.putAll(ServiceUtil.returnSuccess("Successfully created SR.."));
		return result;
	}

	public static Map updateServiceRequest(DispatchContext dctx, Map context) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map<String, Object> custRequestContext = (Map<String, Object>) context.get("custRequestContext");
		Map<String, Object> supplementoryContext = (Map<String, Object>) context.get("supplementoryContext");
		List<Map<String, Object>> documents = (List<Map<String, Object>>) context.get("documents");
		List<Map<String, Object>> dynaList = (List<Map<String, Object>>) context.get("dynaList");
		List<Map<String, Object>> activityList = (List<Map<String, Object>>) context.get("activityList");
		
		String endPointType = (String) context.get("endPointType");
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			String externalId = ParamUtil.getString(custRequestContext, "externalId");
			String isAttachment = ParamUtil.getString(custRequestContext, "isAttachment");
			String description = ParamUtil.getString(custRequestContext, "description");
			String resolution = ParamUtil.getString(custRequestContext, "resolution");
			String reopenFlag = ParamUtil.getString(custRequestContext, "reopenFlag");
			
			
			if(UtilValidate.isNotEmpty(reopenFlag) && "Y".equals(reopenFlag)){
				custRequestContext.remove("reopenFlag");
        	}
			
			if (UtilValidate.isEmpty(externalId)) {
				result.putAll(ServiceUtil.returnError("external_id cant be empty!"));
				return result;
			}
			EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, externalId));
			GenericValue custRequest = EntityUtil
					.getFirst(delegator.findList("CustRequest", mainCondition, null, null, null, false));
			if (UtilValidate.isEmpty(custRequest)) {
				result.putAll(ServiceUtil.returnError("sr not exists!"));
				return result;
			}
			String custRequestId = custRequest.getString("custRequestId");
			if (!"Y".equals(isAttachment)) {
				String apiServiceName = "updateServiceRequest";
				if (UtilValidate.isNotEmpty(endPointType) && endPointType.equals("MSD")) {
					apiServiceName = "updateServiceRequestMsd";
				}
				String custReqLoginId = ParamUtil.getString(custRequestContext, "custReqLoginId");
				// String customerRelatedType =
				// ParamUtil.getString(custRequestContext,
				// "customerRelatedType");
				// String fromPartyId = ParamUtil.getString(custRequestContext,
				// "fromPartyId");
				// String roleTypeId =
				// GlobalConstants.CUSTOMER_ROLE_TYPE_BY_EXTERNALID.get(customerRelatedType);
				Map<String, Object> callCtxt = FastMap.newInstance();
				Map<String, Object> callResult = FastMap.newInstance();
				String escalationLevel = ParamUtil.getString(custRequestContext, "escalationLevel");
				String existSrType = UtilValidate.isNotEmpty(custRequest.getString("custRequestTypeId")) ? custRequest.getString("custRequestTypeId") : "";
				String existSrPriority = UtilValidate.isNotEmpty(custRequest.getString("priority")) ? custRequest.getString("priority") : "";
				String existSrCategory = UtilValidate.isNotEmpty(custRequest.getString("custRequestCategoryId")) ? custRequest.getString("custRequestCategoryId") : "";
				String existSrSubCategory = UtilValidate.isNotEmpty(custRequest.getString("custRequestSubCategoryId")) ? custRequest.getString("custRequestSubCategoryId") : "";
				
				custRequestContext.remove("escalationLevel");
				custRequestContext.remove("externalId");
				
				// Getting the values of unpassed tags nad mapping to the
				// context
				String custRequestTypeId = ParamUtil.getString(custRequestContext, "custRequestTypeId");
				if (UtilValidate.isEmpty(custRequestTypeId)) {
					custRequestTypeId = custRequest.getString("custRequestTypeId");
					custRequestContext.remove("custRequestTypeId");
					custRequestContext.put("custRequestTypeId", custRequestTypeId);
				}
				String custRequestCategoryId = ParamUtil.getString(custRequestContext, "custRequestCategoryId");
				if (UtilValidate.isEmpty(custRequestCategoryId)) {
					custRequestCategoryId = custRequest.getString("custRequestCategoryId");
					custRequestContext.remove("custRequestCategoryId");
					custRequestContext.put("custRequestCategoryId", custRequestCategoryId);
				}
				String custRequestSubCategoryId = ParamUtil.getString(custRequestContext, "custRequestSubCategoryId");
				if (UtilValidate.isEmpty(custRequestSubCategoryId)) {
					custRequestSubCategoryId = custRequest.getString("custRequestSubCategoryId");
					custRequestContext.remove("custRequestSubCategoryId");
					custRequestContext.put("custRequestSubCategoryId", custRequestSubCategoryId);
				}
				String custRequestOthCategoryId = ParamUtil.getString(custRequestContext, "custRequestOthCategoryId");
				if (UtilValidate.isEmpty(custRequestOthCategoryId)) {
					custRequestOthCategoryId = custRequest.getString("custRequestOthCategoryId");
					custRequestContext.remove("custRequestOthCategoryId");
					custRequestContext.put("custRequestOthCategoryId", custRequestOthCategoryId);
				}
				String internalComment = ParamUtil.getString(custRequestContext, "internalComment");
				if (UtilValidate.isEmpty(internalComment)) {
					internalComment = custRequest.getString("internalComment");
					custRequestContext.remove("internalComment");
					custRequestContext.put("internalComment", internalComment);
				} else if( (UtilValidate.isEmpty(endPointType) || !endPointType.equals("MSD") ) && UtilValidate.isNotEmpty(internalComment) ) {
					custRequestContext.remove("internalComment");
					byte[] base64decodedBytes = Base64.getDecoder().decode(internalComment);
					internalComment = new String(base64decodedBytes, "utf-8");
					custRequestContext.put("internalComment", internalComment);
				}
				String caseId = ParamUtil.getString(custRequestContext, "caseId");
				if (UtilValidate.isEmpty(caseId)) {
					caseId = custRequest.getString("caseId");
					custRequestContext.remove("caseId");
					custRequestContext.put("caseId", caseId);
				}
				String custReqSegment = ParamUtil.getString(custRequestContext, "custReqSegment");
				if (UtilValidate.isEmpty(custReqSegment)) {
					custReqSegment = custRequest.getString("custReqSegment");
					custRequestContext.remove("custReqSegment");
					custRequestContext.put("custReqSegment", custReqSegment);
				}
				String custReqSegmentName = ParamUtil.getString(custRequestContext, "custReqSegmentName");
				if (UtilValidate.isEmpty(custReqSegmentName)) {
					custReqSegmentName = custRequest.getString("custReqSegmentName");
					custRequestContext.remove("custReqSegmentName");
					custRequestContext.put("custReqSegmentName", custReqSegmentName);
				}
				String custReqSubSegment = ParamUtil.getString(custRequestContext, "custReqSubSegment");
				if (UtilValidate.isEmpty(custReqSubSegment)) {
					custReqSubSegment = custRequest.getString("custReqSubSegment");
					custRequestContext.remove("custReqSubSegment");
					custRequestContext.put("custReqSubSegment", custReqSubSegment);
				}
				String customerType = ParamUtil.getString(custRequestContext, "customerType");
				if (UtilValidate.isEmpty(customerType)) {
					customerType = custRequest.getString("customerType");
					custRequestContext.remove("customerType");
					custRequestContext.put("customerType", customerType);
				}
				String rmUserLoginId = ParamUtil.getString(custRequestContext, "rmUserLoginId");
				if (UtilValidate.isEmpty(rmUserLoginId)) {
					rmUserLoginId = custRequest.getString("rmUserLoginId");
					custRequestContext.remove("rmUserLoginId");
					custRequestContext.put("rmUserLoginId", rmUserLoginId);
				}
				String ownerBu = ParamUtil.getString(custRequestContext, "ownerBu");
				if (UtilValidate.isEmpty(ownerBu)) {
					ownerBu = custRequest.getString("ownerBu");
					custRequestContext.remove("ownerBu");
					custRequestContext.put("ownerBu", ownerBu);
				}
				String dummyFlag = ParamUtil.getString(custRequestContext, "dummyFlag");
				if (UtilValidate.isEmpty(dummyFlag)) {
					dummyFlag = custRequest.getString("dummyFlag");
					custRequestContext.remove("dummyFlag");
					custRequestContext.put("dummyFlag", dummyFlag);
				}
				String closedByUserLogin = ParamUtil.getString(custRequestContext, "closedByUserLogin");
				if (UtilValidate.isEmpty(closedByUserLogin)) {
					closedByUserLogin = custRequest.getString("closedByUserLogin");
					custRequestContext.remove("closedByUserLogin");
					custRequestContext.put("closedByUserLogin", closedByUserLogin);
				}
				Timestamp closedByDate = (Timestamp) custRequestContext.get("closedByDate");
				if (UtilValidate.isEmpty(closedByDate)) {
					closedByDate = custRequest.getTimestamp("closedByDate");
					custRequestContext.remove("closedByDate");
					custRequestContext.put("closedByDate", closedByDate);
				}
				Timestamp submissionDate = (Timestamp) custRequestContext.get("submissionDate");
				if (UtilValidate.isEmpty(submissionDate)) {
					submissionDate = custRequest.getTimestamp("submissionDate");
					custRequestContext.remove("submissionDate");
					custRequestContext.put("submissionDate", submissionDate);
				}
				String tatCalc = ParamUtil.getString(custRequestContext, "tatCalc");
				if (UtilValidate.isEmpty(tatCalc)) {
					tatCalc = custRequest.getString("tatCalc");
					custRequestContext.remove("tatCalc");
					custRequestContext.put("tatCalc", tatCalc);
				}
				String tatDays = ParamUtil.getString(custRequestContext, "tatDays");
				if (UtilValidate.isEmpty(tatDays)) {
					custRequestContext.remove("tatDays");
					custRequestContext.put("tatDays", custRequest.getBigDecimal("tatDays"));
				}
				String tatHours = ParamUtil.getString(custRequestContext, "tatHours");
				if (UtilValidate.isEmpty(tatHours)) {
					custRequestContext.remove("tatHours");
					custRequestContext.put("tatHours", custRequest.getBigDecimal("tatHours"));
				}
				String priority = ParamUtil.getString(custRequestContext, "priority");
				if (UtilValidate.isEmpty(priority)) {
					priority = custRequest.getString("priority");
					if (UtilValidate.isNotEmpty(priority)) {
						custRequestContext.remove("priority");
						custRequestContext.put("priority", priority);
					}
				}
				String isAutoRou = ParamUtil.getString(custRequestContext, "isAutoRou");
				if (UtilValidate.isEmpty(isAutoRou)) {
					isAutoRou = custRequest.getString("isAutoRou");
					custRequestContext.remove("isAutoRou");
					custRequestContext.put("isAutoRou", isAutoRou);
				}
				String topic = ParamUtil.getString(custRequestContext, "topic");
				if (UtilValidate.isEmpty(topic)) {
					topic = custRequest.getString("topic");
					custRequestContext.remove("topic");
					custRequestContext.put("topic", topic);
				}
				Timestamp actualStartDate = (Timestamp) custRequestContext.get("actualStartDate");
				if (UtilValidate.isEmpty(actualStartDate)) {
					actualStartDate = custRequest.getTimestamp("actualStartDate");
					custRequestContext.remove("actualStartDate");
					custRequestContext.put("actualStartDate", actualStartDate);
				}
				Timestamp actualEndDate = (Timestamp) custRequestContext.get("actualEndDate");
				if (UtilValidate.isEmpty(actualEndDate)) {
					actualEndDate = custRequest.getTimestamp("actualEndDate");
					custRequestContext.remove("actualEndDate");
					custRequestContext.put("actualEndDate", actualEndDate);
				}
				String salesChannelEnumId = ParamUtil.getString(custRequestContext, "salesChannelEnumId");
				if (UtilValidate.isEmpty(salesChannelEnumId)) {
					salesChannelEnumId = custRequest.getString("salesChannelEnumId");
					custRequestContext.remove("salesChannelEnumId");
					custRequestContext.put("salesChannelEnumId", salesChannelEnumId);
				}
				String custReqApprBy = ParamUtil.getString(custRequestContext, "custReqApprBy");
				if (UtilValidate.isEmpty(custReqApprBy)) {
					custReqApprBy = custRequest.getString("custReqApprBy");
					custRequestContext.remove("custReqApprBy");
					custRequestContext.put("custReqApprBy", custReqApprBy);
				}
				Timestamp custReqApprDate = (Timestamp) custRequestContext.get("custReqApprDate");
				if (UtilValidate.isEmpty(custReqApprDate)) {
					custReqApprDate = custRequest.getTimestamp("custReqApprDate");
					custRequestContext.remove("custReqApprDate");
					custRequestContext.put("custReqApprDate", custReqApprDate);
				}
				String custReqApprStatus = ParamUtil.getString(custRequestContext, "custReqApprStatus");
				if (UtilValidate.isEmpty(custReqApprStatus)) {
					custReqApprStatus = custRequest.getString("custReqApprStatus");
					custRequestContext.remove("custReqApprStatus");
					custRequestContext.put("custReqApprStatus", custReqApprStatus);
				}
				custReqLoginId = ParamUtil.getString(custRequestContext, "custReqLoginId");
				if (UtilValidate.isEmpty(custReqLoginId)) {
					custReqLoginId = custRequest.getString("custReqLoginId");
					custRequestContext.remove("custReqLoginId");
					custRequestContext.put("custReqLoginId", custReqLoginId);
				}
				
				String statusId = ParamUtil.getString(custRequestContext, "statusId");
				if (UtilValidate.isEmpty(statusId)) {
					custRequestContext.remove("statusId");
					custRequestContext.put("statusId", custRequest.getString("statusId"));
				}
				String subStatusId = ParamUtil.getString(custRequestContext, "subStatusId");
				if (UtilValidate.isEmpty(subStatusId)) {
					custRequestContext.remove("subStatusId");
					custRequestContext.put("subStatusId", custRequest.getString("subStatusId"));
				}
				
				String responsiblePerson = ParamUtil.getString(custRequestContext, "responsiblePerson");
				if (UtilValidate.isEmpty(responsiblePerson)) {
					responsiblePerson = custRequest.getString("responsiblePerson");
					custRequestContext.remove("responsiblePerson");
					custRequestContext.put("responsiblePerson", responsiblePerson);
				}
				String custReqSrSource = ParamUtil.getString(custRequestContext, "custReqSrSource");
				if (UtilValidate.isEmpty(custReqSrSource)) {
					custReqSrSource = custRequest.getString("custReqSrSource");
					custRequestContext.remove("custReqSrSource");
					custRequestContext.put("custReqSrSource", custReqSrSource);
				}
				String custReqNatId = ParamUtil.getString(custRequestContext, "custReqNatId");
				if (UtilValidate.isEmpty(custReqNatId)) {
					custReqNatId = custRequest.getString("custReqNatId");
					custRequestContext.remove("custReqNatId");
					custRequestContext.put("custReqNatId", custReqNatId);
				}
				String custReqVplusId = ParamUtil.getString(custRequestContext, "custReqVplusId");
				if (UtilValidate.isEmpty(custReqVplusId)) {
					custReqVplusId = custRequest.getString("custReqVplusId");
					custRequestContext.remove("custReqVplusId");
					custRequestContext.put("custReqVplusId", custReqVplusId);
				}
				String custReqDynaAdd1 = ParamUtil.getString(custRequestContext, "custReqDynaAdd1");
				if (UtilValidate.isEmpty(custReqDynaAdd1)) {
					custReqDynaAdd1 = custRequest.getString("custReqDynaAdd1");
					custRequestContext.remove("custReqDynaAdd1");
					custRequestContext.put("custReqDynaAdd1", custReqDynaAdd1);
				}
				String custReqDynaAdd2 = ParamUtil.getString(custRequestContext, "custReqDynaAdd2");
				if (UtilValidate.isEmpty(custReqDynaAdd2)) {
					custReqDynaAdd2 = custRequest.getString("custReqDynaAdd2");
					custRequestContext.remove("custReqDynaAdd2");
					custRequestContext.put("custReqDynaAdd2", custReqDynaAdd2);
				}
				String msguid = ParamUtil.getString(custRequestContext, "msguid");
				if (UtilValidate.isEmpty(msguid)) {
					msguid = custRequest.getString("msguid");
					custRequestContext.remove("msguid");
					custRequestContext.put("msguid", msguid);
				}
				String custReqNonFirstName = ParamUtil.getString(custRequestContext, "custReqNonFirstName");
				if (UtilValidate.isEmpty(custReqNonFirstName)) {
					custReqNonFirstName = custRequest.getString("custReqNonFirstName");
					custRequestContext.remove("custReqNonFirstName");
					custRequestContext.put("custReqNonFirstName", custReqNonFirstName);
				}
				String custReqNonLastName = ParamUtil.getString(custRequestContext, "custReqNonLastName");
				if (UtilValidate.isEmpty(custReqNonLastName)) {
					custReqNonLastName = custRequest.getString("custReqNonLastName");
					custRequestContext.remove("custReqNonLastName");
					custRequestContext.put("custReqNonLastName", custReqNonLastName);
				}
				String custReqMsdBu = ParamUtil.getString(custRequestContext, "custReqMsdBu");
				if (UtilValidate.isEmpty(custReqMsdBu)) {
					custReqMsdBu = custRequest.getString("custReqMsdBu");
					custRequestContext.remove("custReqMsdBu");
					custRequestContext.put("custReqMsdBu", custReqMsdBu);
				}
				String custReqCaseOrigin = ParamUtil.getString(custRequestContext, "custReqCaseOrigin");
				if (UtilValidate.isEmpty(custReqCaseOrigin)) {
					custReqCaseOrigin = custRequest.getString("custReqCaseOrigin");
					custRequestContext.remove("custReqCaseOrigin");
					custRequestContext.put("custReqCaseOrigin", custReqCaseOrigin);
				}
				String manualTatDays = ParamUtil.getString(custRequestContext, "manualTatDays");
				if (UtilValidate.isEmpty(manualTatDays)) {
					custRequestContext.remove("manualTatDays");
					custRequestContext.put("manualTatDays", custRequest.getBigDecimal("manualTatDays"));
				}
				String custRequestName = ParamUtil.getString(custRequestContext, "custRequestName");
				if (UtilValidate.isEmpty(custRequestName)) {
					custRequestName = custRequest.getString("custRequestName");
					custRequestContext.remove("custRequestName");
					custRequestContext.put("custRequestName", custRequestName);
				}
				if (UtilValidate.isEmpty(custRequestContext.get("custReqReviewedBy"))) {
					custRequestContext.remove("custReqReviewedBy");
					custRequestContext.put("custReqReviewedBy", custRequest.get("custReqReviewedBy"));
				}
				if (UtilValidate.isEmpty(custRequestContext.get("custReqReviewedDate"))) {
					custRequestContext.remove("custReqReviewedDate");
					custRequestContext.put("custReqReviewedDate", custRequest.get("custReqReviewedDate"));
				}
				if (UtilValidate.isEmpty(custRequestContext.get("custReqCustomerhours"))) {
					custRequestContext.remove("custReqCustomerhours");
					custRequestContext.put("custReqCustomerhours", custRequest.get("custReqCustomerhours"));
				}
				if (UtilValidate.isEmpty(custRequestContext.get("idValue"))) {
					custRequestContext.remove("idValue");
					custRequestContext.put("idValue", custRequest.get("idValue"));
				}
				if (UtilValidate.isEmpty(custRequestContext.get("isPwebSr"))) {
					custRequestContext.remove("isPwebSr");
					custRequestContext.put("isPwebSr", custRequest.get("isPwebSr"));
				}
				if (UtilValidate.isEmpty(custRequestContext.get("isRmmSr"))) {
					custRequestContext.remove("isRmmSr");
					custRequestContext.put("isRmmSr", custRequest.get("isRmmSr"));
				}
				
				if (UtilValidate.isEmpty(custRequestContext.get("description"))) {
					custRequestContext.remove("description");
					custRequestContext.put("description", custRequest.get("description"));
				}
				if (UtilValidate.isEmpty(custRequestContext.get("tsmDescription"))) {
					custRequestContext.remove("tsmDescription");
					custRequestContext.put("tsmDescription", custRequest.get("tsmDescription"));
				}
				
				if (UtilValidate.isEmpty(custRequestContext.get("resolution"))) {
					custRequestContext.remove("resolution");
					custRequestContext.put("resolution", custRequest.get("resolution"));
				}
				if (UtilValidate.isEmpty(custRequestContext.get("actualResolution"))) {
					custRequestContext.remove("actualResolution");
					custRequestContext.put("actualResolution", custRequest.get("actualResolution"));
				}
				
				if (custRequestContext.containsKey("isAttachment"))
					custRequestContext.remove("isAttachment");
				
				custRequest.setNonPKFields(custRequestContext);
				//custRequest.putAll(custRequestContext);

				custRequest.put("lastModifiedByUserLogin",
						UtilValidate.isNotEmpty(custReqLoginId) ? custReqLoginId : userLogin.getString("userLoginId"));
				custRequest.put("lastModifiedDate", UtilDateTime.nowTimestamp());
				
				if(UtilValidate.isNotEmpty(statusId) && ("SR_CLOSED".equals(statusId) || "SR_CANCELLED".equals(statusId))) {
					closedByDate = (Timestamp) custRequestContext.get("closedByDate");
					if (UtilValidate.isEmpty(closedByDate)) {
						closedByDate = UtilDateTime.nowTimestamp();
					}
					custRequest.put("closedByUserLogin",userLogin.getString("userLoginId"));
					custRequest.put("closedByDate",closedByDate);
				}
				
				/*if(UtilValidate.isNotEmpty(statusId) && ("SR_OPEN".equals(statusId))) {
					Timestamp openDateTime = custRequest.getTimestamp("openDateTime");
					if (UtilValidate.isEmpty(openDateTime)) {
						openDateTime = UtilDateTime.nowTimestamp();
					}
					custRequest.put("openDateTime",openDateTime);
				}*/
				
				if(UtilValidate.isNotEmpty(reopenFlag) && "Y".equals(reopenFlag)){
					custRequest.put("closedByUserLogin", null);
					custRequest.put("closedByDate", null);
					custRequest.put("reopenedDate", UtilDateTime.nowTimestamp());
					custRequest.put("reopenedBy", userLogin.getString("userLoginId"));
					custRequest.put("tatDays", null);
					custRequest.put("tatHours", null);
					custRequest.put("tatMins", null);
					custRequest.put("lastModifiedDate", UtilDateTime.nowTimestamp());
					custRequest.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
	        	}
				custRequest.store();
				
				// supplementory [start]
				GenericValue supplementory = EntityUtil.getFirst(delegator.findByAnd("CustRequestSupplementory",
						UtilMisc.toMap("custRequestId", custRequestId), null, false));
				if (UtilValidate.isNotEmpty(supplementory) && UtilValidate.isEmpty(reopenFlag) && !"SR_CLOSED".equals(statusId)) {
					
					/*if (UtilValidate.isNotEmpty(endPointType) && endPointType.equals("MSD")) {
						Set<String> keys = new LinkedHashSet<String>();
						keys.addAll(supplementoryContext.keySet());
						for (String key : keys) {
							if (UtilValidate.isEmpty(supplementoryContext.get(key))) {
								supplementoryContext.remove(key);
								supplementoryContext.put(key, supplementory.get(key));
							}
						}
					}
					
					supplementory.putAll(supplementoryContext);
					supplementory.store();*/
				}
				// supplementory [end]
				
				// UserLoginHistory [start]
				if (UtilValidate.isNotEmpty(custRequest.getString("custReqLoginId"))) {
					GenericValue loginParty = DataUtil.findPartyByLogin(delegator,
							custRequest.getString("custReqLoginId"));
					GenericValue loginHistory = delegator.makeValue("UserLoginHistoryCustom");
					loginHistory.put("userLoginHistoryCustomId",
							org.fio.homeapps.util.UtilGenerator.getNextSeqId());
					loginHistory.put("userLoginId", custRequest.getString("custReqLoginId"));
					loginHistory.put("fromDate", UtilDateTime.nowTimestamp());
					loginHistory.put("partyId", loginParty.getString("partyId"));
					loginHistory.put("serviceName", apiServiceName);
					loginHistory.create();
				}
				// UserLoginHistory [end]
				
				// cust request attribute [start]
				if (UtilValidate.isNotEmpty(dynaList)) {
					int count = 1;
					for (Map<String, Object> dyna : dynaList) {
						String fieldName = ParamUtil.getString(dyna, "field_name");
						if (UtilValidate.isNotEmpty(fieldName)) {
							
							GenericValue attribute = delegator.makeValue("CustRequestAttribute");
							attribute.put("custRequestId", custRequestId);
							attribute.put("attrName", ParamUtil.getString(dyna, "field_name"));
							attribute.put("attrValue", ParamUtil.getString(dyna, "field_value"));
							attribute.put("attrLocal", ParamUtil.getString(dyna, "field_local_name"));
							if (UtilValidate.isNotEmpty(attribute.getString("attrLocal"))) {
								attribute.put("locale", "zh-CN");
							}
							attribute.put("channelId", custRequest.getString("salesChannelEnumId"));
							attribute.put("sequenceNumber", UtilGenerator.getNextSeqId(count++));
							attribute.create();
						}
					}
				}
				// cust request attribute [end]
				
				// prepare escalation [start]
				
				if( (UtilValidate.isEmpty(endPointType) || !endPointType.equals("MSD")) ) {
				
					String isCalculateCommitDate = "N";
					if (UtilValidate.isNotEmpty(custRequest.getString("statusId")) && (custRequest.getString("statusId").equals("SR_OPEN") || (custRequest.getString("statusId").equals("SR_RECEIVED")) && UtilValidate.isNotEmpty(reopenFlag) && "Y".equals(reopenFlag))) {
						isCalculateCommitDate = "Y";
					}
					
					String srType = ParamUtil.getString(custRequestContext, "custRequestTypeId");
					String srPriority = ParamUtil.getString(custRequestContext, "priority");
					String srCategory = ParamUtil.getString(custRequestContext, "custRequestCategoryId");
					String srSubCategory = ParamUtil.getString(custRequestContext, "custRequestSubCategoryId");
					
					if(UtilValidate.isNotEmpty(srType) && !srType.equals(existSrType) 
							|| UtilValidate.isNotEmpty(srPriority) && !srPriority.equals(existSrPriority)
							|| UtilValidate.isNotEmpty(srCategory) && !srCategory.equals(existSrCategory)
							|| UtilValidate.isNotEmpty(srSubCategory) && !srSubCategory.equals(existSrSubCategory)) {
						isCalculateCommitDate = "Y";
					}
						
					if (UtilValidate.isEmpty(escalationLevel)) {
						escalationLevel = UtilValidate.isNotEmpty(custRequest.getString("escalationLevel"))
								? "" + (Integer.parseInt(custRequest.getString("escalationLevel"))) : "1";
					}
					Timestamp createdDate = custRequest.getTimestamp("reopenedDate");
					if(UtilValidate.isEmpty(createdDate)){
						createdDate = custRequest.getTimestamp("createdDate");
					}
					
					Map<String, Object> escalationContext = new LinkedHashMap<String, Object>();
					escalationContext.put("delegator", delegator);
					escalationContext.put("escalationLevel", escalationLevel);
					escalationContext.put("typeId", custRequest.getString("custRequestTypeId"));
					escalationContext.put("categoryId", custRequest.getString("custRequestCategoryId"));
					escalationContext.put("subCategoryId", custRequest.getString("custRequestSubCategoryId"));
					escalationContext.put("priority", custRequest.getString("priority"));
					escalationContext.put("createdDate", createdDate);
					escalationContext.put("businessUnit", custRequest.getString("ownerBu"));
					escalationContext.put("statusId", custRequest.getString("statusId"));
					escalationContext.put("statusClosedEscTime", supplementory.get("statusClosedEscTime"));
					escalationContext.put("isCalculateCommitDate", isCalculateCommitDate);
					Resolver resolver = ResolverFactory.getResolver(ResolverType.ESCALATION_RESOLVER);
					Map<String, Object> escalationResult = resolver.resolve(escalationContext);
					if (UtilValidate.isNotEmpty(escalationResult.get("escalationLevel"))) {
						custRequest.put("escalationLevel", escalationResult.get("escalationLevel"));
						custRequest.store();
					}
					
					if (UtilValidate.isNotEmpty(isCalculateCommitDate) && isCalculateCommitDate.equals("Y")) {
						if (UtilValidate.isNotEmpty(escalationResult.get("commitDate"))) {
							supplementory.put("commitDate", escalationResult.get("commitDate"));
							
							if(UtilValidate.isNotEmpty(escalationResult.get("_pre_escalation_date"))) {
								supplementory.put("preEscalationDate", escalationResult.get("_pre_escalation_date"));
							} else {
								supplementory.put("preEscalationDate", null);
							}
							if(UtilValidate.isNotEmpty(escalationResult.get("_escalation_date_1"))) {
								supplementory.put("escalationDate1", escalationResult.get("_escalation_date_1"));
							} else {
								supplementory.put("escalationDate1", null);
							}
							if(UtilValidate.isNotEmpty(escalationResult.get("_escalation_date_2"))) {
								supplementory.put("escalationDate2", escalationResult.get("_escalation_date_2"));
							} else {
								supplementory.put("escalationDate2", null);
							}
							if(UtilValidate.isNotEmpty(escalationResult.get("_escalation_date_3"))) {
								supplementory.put("escalationDate3", escalationResult.get("_escalation_date_3"));
							} else {
								supplementory.put("escalationDate3", null);
							}
							
						} else {
							supplementory.put("commitDate", null);
							supplementory.put("preEscalationDate", null);
							supplementory.put("escalationDate1", null);
							supplementory.put("escalationDate2", null);
							supplementory.put("escalationDate3", null);
						}
					}
					
					supplementory.put("statusEscTime", escalationResult.get("statusEscTime"));
					if(UtilValidate.isNotEmpty(escalationResult.get("statusClosedEscTime"))) {
						supplementory.put("statusClosedEscTime", escalationResult.get("statusClosedEscTime"));
					}
						
					supplementory.store();	
					
				}
				
				// prepare escalation [end]
				
				// prepare TAT [start]
				closedByDate = (Timestamp) custRequestContext.get("closedByDate");
				if (UtilValidate.isNotEmpty(statusId)
						&& "SR_CLOSED".equals(statusId)) {
					
					Debug.log("TAT SR ID# " + custRequestId);
					Debug.log("TAT SR status========" + statusId);
					Timestamp createdDate = custRequest.getTimestamp("reopenedDate");
					if(UtilValidate.isEmpty(createdDate)){
						createdDate = custRequest.getTimestamp("createdDate");
					}
					Map<String, Object> tatContext = new LinkedHashMap<String, Object>();
					tatContext.put("delegator", delegator);
					tatContext.put("tatCalc", custRequest.getString("tatCalc"));
					tatContext.put("businessUnit", custRequest.getString("ownerBu"));
					tatContext.put("createdDate", createdDate);
					
					Debug.log("closedDate before========" + closedByDate);
					tatContext.put("closedDate", UtilValidate.isNotEmpty(closedByDate) ? closedByDate : UtilDateTime.nowTimestamp());
					Debug.log("closedDate after========" + tatContext.get("closedDate"));
					
					custRequest.put("closedByDate", tatContext.get("closedDate"));
					int closedHour = UtilDateTime.getHour((Timestamp) tatContext.get("closedDate"),
							TimeZone.getDefault(), Locale.getDefault());
					Debug.log("closedHour========" +closedHour);
					
					Resolver resolver = ResolverFactory.getResolver(ResolverType.TAT_RESOLVER);
					Map<String, Object> tatResult = resolver.resolve(tatContext);
					
					if (ResponseUtils.isSuccess(tatResult)) {
						/*
						BigDecimal tatHoursCalculated = ParamUtil.getBigDecimal(tatResult, "tatHours");
						BigDecimal tatMinsCalculated = ParamUtil.getBigDecimal(tatResult, "tatMins");
						if (UtilValidate.isNotEmpty(tatHoursCalculated) && UtilValidate.isNotEmpty(tatMinsCalculated)) {
							tatHoursCalculated = tatHoursCalculated.add(tatMinsCalculated).setScale(2, BigDecimal.ROUND_HALF_UP);
						}
						
						custRequest.put("tatDays", ParamUtil.getBigDecimal(tatResult, "tatDays"));
						custRequest.put("tatHours", tatHoursCalculated);
						*/
						custRequest.put("tatDays", ParamUtil.getBigDecimal(tatResult, "tatDays"));
						custRequest.put("tatHours", ParamUtil.getBigDecimal(tatResult, "tatHours"));
						custRequest.put("tatMins", ParamUtil.getBigDecimal(tatResult, "tatMins"));
						
					}else {
						Debug.log("Error Tat calculation=="+ResponseUtils.isError(tatResult));
					}
					
					custRequest.store();
				}
				// prepare TAT [end]
				
				// prepare SLA TAT [start]
				String isEnabledStaTat = DataUtil.getGlobalValue(delegator, "SLA_TAT_ENABLE","N");
				String slaTatStopStatus = DataUtil.getGlobalValue(delegator, "SLA_TAT_STOP_STATUS","SR_CLOSED");
				List slaTatStopList = DataUtil.stringToList(slaTatStopStatus, ",");
				if (isEnabledStaTat.equalsIgnoreCase("Y") && 
						UtilValidate.isNotEmpty(custRequest.getString("statusId")) && slaTatStopList.contains(custRequest.getString("statusId"))) {
					
					Debug.log("SLA TAT SR ID# " + custRequest.getString("custRequestId"));
					Debug.log("SLA TAT SR status========" + custRequest.getString("statusId"));
					
					Map<String, Object> tatContext = new LinkedHashMap<String, Object>();
					tatContext.put("delegator", delegator);
					tatContext.put("custRequestId", custRequestId);
					tatContext.put("tatCalc", isEnabledStaTat);
					tatContext.put("businessUnit", custRequest.getString("ownerBu"));
					Timestamp createdDate = custRequest.getTimestamp("createdDate");
					tatContext.put("createdDate", createdDate);
					
					Debug.log("closedDate before========" + closedByDate);
					tatContext.put("closedDate", UtilValidate.isNotEmpty(closedByDate) ? closedByDate : UtilDateTime.nowTimestamp());
					Debug.log("closedDate after========" + tatContext.get("closedDate"));
					tatContext.put("statusId", custRequest.getString("statusId"));
					
					custRequest.put("closedByDate", tatContext.get("closedDate"));
					
					
					Resolver resolver = ResolverFactory.getResolver(ResolverType.SLA_TAT_RESOLVER);
					Map<String, Object> tatResult = resolver.resolve(tatContext);
					BigDecimal slaTatDays =BigDecimal.ZERO;
					if (ResponseUtils.isSuccess(tatResult)) {
						slaTatDays = UtilValidate.isNotEmpty(ParamUtil.getBigDecimal(tatResult, "tatDays"))?ParamUtil.getBigDecimal(tatResult, "tatDays"):BigDecimal.ZERO;
						GenericValue custReqAttr = delegator.findOne("CustRequestAttribute", UtilMisc.toMap("custRequestId", custRequestId,"attrName","SLA_TAT"),false);
						if (UtilValidate.isEmpty(custReqAttr)) {
							GenericValue custRequestAttribute = delegator.makeValue("CustRequestAttribute");
							custRequestAttribute.put("custRequestId", custRequestId);
							custRequestAttribute.put("attrName", "SLA_TAT");
							custRequestAttribute.put("attrValue", ""+slaTatDays);
							delegator.create(custRequestAttribute);
						}else {
							custReqAttr.put("attrValue", ""+slaTatDays);
							custReqAttr.store();
						}
					}else {
						Debug.log("Error Sla Tat calculation=="+ResponseUtils.isError(tatResult));
					}
				}
				// prepare SLA TAT [end]
				
				// SR activity [start]
				try {
					if (UtilValidate.isNotEmpty(endPointType) && endPointType.equals("MSD")
							&& UtilValidate.isNotEmpty(activityList)) {
						for (Map<String, Object> activity : activityList) {
							String wftExternalId = ParamUtil.getString(activity, "resolution_activityId");
							if (UtilValidate.isNotEmpty(wftExternalId)) {
								GenericValue wft = EntityUtil.getFirst(delegator.findByAnd("WorkEffort",
										UtilMisc.toMap("externalId", wftExternalId), null, false));
								if (UtilValidate.isEmpty(wft)) {
									Map<String, Object> activityContext = new HashMap<String, Object>();
									activityContext.put("description",
											ParamUtil.getString(activity, "resolution_description"));
									activityContext.put("externalId",
											ParamUtil.getString(activity, "resolution_activityId"));
									activityContext.put("currentStatusId",
											org.fio.homeapps.util.DataUtil.getStatusId(delegator,
													ParamUtil.getString(activity, "resolution_StatusCode"),
													"IA_STATUS_ID"));
									activityContext.put("currentSubStatusId",
											org.fio.homeapps.util.DataUtil.getStatusId(delegator,
													ParamUtil.getString(activity, "resolution_StateCode"),
													"IA_SUB_STATUS_ID"));
									activityContext.put("wfUserLoginId",
											DataUtil.isValidUserLogin(delegator,
													ParamUtil.getString(activity, "resolution_createdby"))
															? ParamUtil.getString(activity, "resolution_createdby")
															: "");
									activityContext.put("lastModifiedByUserLogin",
											DataUtil.isValidUserLogin(delegator,
													ParamUtil.getString(activity, "resolution_modifiedby"))
															? ParamUtil.getString(activity, "resolution_modifiedby")
															: "");
									activityContext.put("actualStartDate",
											ParamUtil.getDateTime(activity, "resolution_createdon"));
									activityContext.put("lastModifiedDate",
											ParamUtil.getDateTime(activity, "resolution_createdon"));
									activityContext.put("resolution",
											ParamUtil.getString(activity, "resolution_resolution"));
									activityContext.put("billableTime",
											ParamUtil.getLong(activity, "resolution_billabletime"));
									activityContext.put("userLogin", userLogin);
									activityContext.put("endPointType", "MSD");
									Map<String, Object> res = dispatcher.runSync("crmPortal.createInteractiveActivity",
											activityContext);
									if (ServiceUtil.isSuccess(res)) {
										Debug.logInfo("Successfully create activity#" + wftExternalId + " for SR# "
												+ externalId, MODULE);
										GenericValue crwf = delegator.makeValue("CustRequestWorkEffort");
										crwf.put("custRequestId", custRequestId);
										crwf.put("workEffortId", res.get("workEffortId"));
										delegator.createOrStore(crwf);
									}
								}
							}

						}
					}
				} catch (Exception e) {
					Debug.logError(e, MODULE);
					//e.printStackTrace();
				}
			}
			
			// content [start]
			if (UtilValidate.isNotEmpty(documents)) {
				for (Map<String, Object> document : documents) {
					
					String documentRefNum = ParamUtil.getString(document, "document_ref_num");
					String annotationid = ParamUtil.getString(document, "annotationid");
					
					if (UtilValidate.isNotEmpty(documentRefNum) || UtilValidate.isNotEmpty(annotationid)) {

						mainCondition = EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,
								custRequestId);

						if (UtilValidate.isNotEmpty(endPointType) && endPointType.equals("MSD")) {
							mainCondition = EntityCondition.makeCondition(EntityOperator.AND, mainCondition,
									EntityCondition.makeCondition("annotationId", EntityOperator.EQUALS,
											annotationid));
						} else {
							mainCondition = EntityCondition.makeCondition(EntityOperator.AND, mainCondition,
									EntityCondition.makeCondition("documentRefNum", EntityOperator.EQUALS,
											documentRefNum));
						}

						GenericValue contentSummary = EntityUtil.getFirst(delegator
								.findList("CustRequestContentSummary", mainCondition, null, null, null, false));
						if (UtilValidate.isNotEmpty(contentSummary)
								&& (UtilValidate.isEmpty(endPointType) || !endPointType.equals("MSD"))
								) {
							Debug.logError("Document already exists! documentRefNum: " + documentRefNum, MODULE);
							continue;
						}
						
						if (UtilValidate.isNotEmpty(contentSummary) && UtilValidate.isNotEmpty(endPointType) && endPointType.equals("MSD")) {
							
							GenericValue content = EntityUtil.getFirst( delegator.findByAnd("Content", UtilMisc.toMap("contentId", contentSummary.getString("contentId")), null, false) );
							if (UtilValidate.isNotEmpty(content)) {
								content.put("createdByUserLogin", ParamUtil.getString(document, "cnt_created_by_user_login"));
								content.put("description", ParamUtil.getString(document, "cnt_description"));
								content.put("contentName", ParamUtil.getString(document, "file_name"));
								
								content.store();
							}
							
						} else {
							Timestamp createdDate = UtilDateTime.nowTimestamp();
							String cntCreatedDate = ParamUtil.getString(document, "cnt_created_date");
							
							if (UtilValidate.isNotEmpty(cntCreatedDate)) {
								createdDate = ParamUtil.getDateTime(document, "cnt_created_date");
							}
							
							GenericValue content = delegator.makeValue("Content");
							String contentId = delegator.getNextSeqId("Content");
							
							content.put("contentId", contentId);
							content.put("contentTypeId", "DOCUMENT");
							content.put("localeString", null);
							content.put("createdDate", createdDate);
							content.put("createdByUserLogin", ParamUtil.getString(document, "cnt_created_by_user_login"));
							content.put("description", ParamUtil.getString(document, "cnt_description"));
							content.put("contentName", ParamUtil.getString(document, "file_name"));
							content.put("documentRefNum", ParamUtil.getString(document, "document_ref_num"));
							content.put("annotationId", ParamUtil.getString(document, "annotationid"));
							content.create();
							
							GenericValue crContent = delegator.makeValue("CustRequestContent");
							crContent.put("custRequestId", custRequestId);
							crContent.put("contentId", contentId);
							crContent.put("fromDate", createdDate);
							crContent.create();
						}
						
					}
				}
			}
			// content [end]

			// SR activity [end]
			result.put("custRequestId", custRequestId);
			
			//for notification
			String userLoginPartyId = UtilValidate.isNotEmpty(userLogin) ? userLogin.getString("partyId") : "";
			String userLoginName = DataUtil.getUserLoginName(delegator, userLoginPartyId);
			
			String eventName = DataUtil.getGlobalValue(delegator, "SR_UPDATE_EVENT_NAME", "Update FSR");
			String eventDescription = DataUtil.getGlobalValue(delegator, "SR_UPDATE_EVENT_DESC", "FSR ({0}) updated by {1}");
			String eventUrl = DataUtil.getGlobalValue(delegator, "SR_EVENT_URL", "");
			if(eventDescription.contains("{0}")) {
				eventDescription = MessageFormat.format(eventDescription, new Object[] { custRequestId , userLoginName});
			}
			
			String viewUrl = "";
			if(UtilValidate.isNotEmpty(eventUrl)) {
				if(eventUrl.contains("{0}")) {
					eventUrl = MessageFormat.format(eventUrl, new Object[] { custRequestId });
				}
				viewUrl = eventUrl;
			} else {
				HttpServletRequest request = UtilValidate.isNotEmpty(custRequestContext.get("request")) ? (HttpServletRequest) custRequestContext.get("request") : null;
				String serverRootUrl = UtilValidate.isNotEmpty(request) ? UtilHttp.getServerRootUrl(request) : "";
				String contextPath =  UtilValidate.isNotEmpty(request) ? request.getContextPath() : "";
				
				if(UtilValidate.isNotEmpty(serverRootUrl) && UtilValidate.isNotEmpty(contextPath)) {
					viewUrl = serverRootUrl+contextPath+"/control/viewServiceRequest?srNumber="+custRequestId;
				}
			}
			
			String entityOwnerId = custRequest.getString("responsiblePerson");
			result.put("entityId", custRequestId);
			result.put("entityName", "CustRequest");
			result.put("eventType", "SERVICE_REQUEST");
			result.put("eventDate", org.ofbiz.base.util.UtilDateTime.nowTimestamp());
			result.put("eventName", eventName);
			result.put("eventDescription", eventDescription);
			result.put("eventUrl", viewUrl);
			result.put("domainEntityId", custRequestId);
			result.put("domainEntityType", "SERVICE_REQUEST");
			result.put("entityOwnerId", entityOwnerId );
			
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.putAll(ServiceUtil.returnSuccess("Successfully UPDATED SR.."));
		return result;
	}

	public static Map detailServiceRequest(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String workEffortId = (String) context.get("workEffortId");
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
			GenericValue activity = EntityUtil
					.getFirst(delegator.findList("WorkEffort", mainCondition, null, null, null, false));
			if (UtilValidate.isEmpty(activity)) {
				result.putAll(ServiceUtil.returnError("activity not exists!"));
				return result;
			}
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			result.put("activity", activity);
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
					EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CAL_OWNER")),
					EntityUtil.getFilterByDateExpr()));
			/*
			 * conditionList.add(EntityCondition.makeCondition(EntityOperator.
			 * AND, EntityCondition.makeCondition("workEffortId",
			 * EntityOperator.EQUALS, workEffortId),
			 * EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,
			 * "PRTYASGN_ASSIGNED"),
			 * EntityCondition.makeCondition(EntityOperator.OR,
			 * EntityCondition.makeCondition("roleTypeId",
			 * EntityOperator.EQUALS, "CUSTOMER"),
			 * EntityCondition.makeCondition("roleTypeId",
			 * EntityOperator.EQUALS, "PROSPECT") ),
			 * EntityUtil.getFilterByDateExpr() ));
			 */
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue partyAssignment = EntityUtil
					.getFirst(delegator.findList("WorkEffortPartyAssignment", mainConditons, null, null, null, false));
			if (UtilValidate.isNotEmpty(partyAssignment)) {
				result.put("partyId", partyAssignment.getString("partyId"));
				result.put("roleTypeId", partyAssignment.getString("roleTypeId"));
				result.put("callOutCome", partyAssignment.getString("callOutCome"));
			}
			result.put("workEffortId", workEffortId);
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.putAll(ServiceUtil.returnSuccess("Successfully updated activity.."));
		return result;
	}

	// Desc : Find Service Request Author : Arshiya
	public static Map findServiceRequest(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String customerRelatedType = (String) context.get("customerRelatedType");
		String vplusCustId = (String) context.get("vplusCustId");
		String nationalId = (String) context.get("nationalId");
		String fromPartyId = (String) context.get("fromPartyId");
		String responsiblePerson = (String) context.get("responsiblePerson");
		String caseId = (String) context.get("caseId");
		Timestamp createdDateRangeStart = (Timestamp) context.get("createdDateRangeStart");
		Timestamp createdDateRangeEnd = (Timestamp) context.get("createdDateRangeEnd");
		Timestamp modifiedDateRangeStart = (Timestamp) context.get("modifiedDateRangeStart");
		Timestamp modifiedDateRangeEnd = (Timestamp) context.get("modifiedDateRangeEnd");
		String statusId = (String) context.get("statusId");
		String subStatusId = (String) context.get("subStatusId");
		Timestamp dueDateRangeStart = (Timestamp) context.get("dueDateRangeStart");
		Timestamp dueDateRangeEnd = (Timestamp) context.get("dueDateRangeEnd");
		String priority = (String) context.get("priority");
		String srType = (String) context.get("srType");
		String srCategory = (String) context.get("srCategory");
		String srSubCategory = (String) context.get("srSubCategory");
		String msguid = (String) context.get("msguid");
		Timestamp requestedTime = (Timestamp) context.get("requestedTime");
		String systemName = (String) context.get("systemName");
		List<String> responsibleLoginIds = (List<String>) context.get("responsibleLoginIds");
		List<String> emplLoginIds = (List<String>) context.get("emplLoginIds");
		String nextPageNum = (String) context.get("nextPageNum");
		String createdBy = (String) context.get("createdBy");
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			
			List<GenericValue> serviceRequestList = new ArrayList<GenericValue>();
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			EntityCondition partyCondition = null;
            String ofbizPartyId = DataUtil.getPartyIdentificationPartyId(delegator, fromPartyId, "CIF");
            if (UtilValidate.isNotEmpty(ofbizPartyId)) {
            	partyCondition = EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, ofbizPartyId);
            } else {
            	partyCondition = EntityCondition.makeCondition("cif", EntityOperator.EQUALS, fromPartyId);
            }
			
			List conditionList = FastList.newInstance();
			
			if (UtilValidate.isNotEmpty(fromPartyId) && UtilValidate.isNotEmpty(vplusCustId)
					&& UtilValidate.isNotEmpty(nationalId) && (UtilValidate.isNotEmpty(statusId)
							&& (statusId.equals("SR_OPEN") || statusId.equals("SR_CLOSED")))) {
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR, 
						EntityCondition.makeCondition(
								EntityOperator.AND,
								partyCondition,
								org.groupfio.crm.service.util.DataUtil.prepareStatusCondition(statusId, "statusId", "SR_")
						),
						EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custReqVplusId", EntityOperator.EQUALS, vplusCustId),
								org.groupfio.crm.service.util.DataUtil.prepareStatusCondition(statusId, "statusId", "SR_")
						),
						EntityCondition.makeCondition(EntityOperator.AND, 
								partyCondition,
								EntityCondition.makeCondition("custReqVplusId", EntityOperator.EQUALS, vplusCustId),
								org.groupfio.crm.service.util.DataUtil.prepareStatusCondition(statusId, "statusId", "SR_")
								)
						)
						);
				
			} else {
				if (UtilValidate.isNotEmpty(fromPartyId)) {
					conditionList.add(partyCondition);
				}
				if (UtilValidate.isNotEmpty(vplusCustId)) {
					conditionList
							.add(EntityCondition.makeCondition("custReqVplusId", EntityOperator.EQUALS, vplusCustId));
				}
				if (UtilValidate.isNotEmpty(nationalId)) {
					conditionList.add(EntityCondition.makeCondition("custReqNatId", EntityOperator.EQUALS, nationalId));
				}
				if (UtilValidate.isNotEmpty(statusId)) {
					conditionList.add(
							org.groupfio.crm.service.util.DataUtil.prepareStatusCondition(statusId, "statusId", "SR_"));
				}
			}
			
			if (UtilValidate.isNotEmpty(customerRelatedType) && !"04".equals(customerRelatedType)) {
				conditionList.add(EntityCondition.makeCondition("customerRelatedType", EntityOperator.EQUALS,
						customerRelatedType));
			}
			
			if (UtilValidate.isNotEmpty(responsiblePerson)) {
				conditionList.add(
						EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, responsiblePerson));
			}
			if (UtilValidate.isNotEmpty(caseId)) {
				conditionList.add(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId));
			}
			if (UtilValidate.isNotEmpty(createdBy)) {
				conditionList
						.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS, createdBy));
			}

			if (UtilValidate.isNotEmpty(createdDateRangeStart)) {
				conditionList.add(EntityCondition.makeCondition(
						EntityCondition.makeCondition("createdDate", EntityOperator.EQUALS, null), EntityOperator.OR,
						EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO,
								createdDateRangeStart)));
			}
			if (UtilValidate.isNotEmpty(createdDateRangeEnd)) {
				conditionList.add(EntityCondition.makeCondition(
						EntityCondition.makeCondition("createdDate", EntityOperator.EQUALS, null), EntityOperator.OR,
						EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO,
								createdDateRangeEnd)));
			}
			if (UtilValidate.isNotEmpty(modifiedDateRangeStart)) {
				conditionList.add(EntityCondition.makeCondition(
						EntityCondition.makeCondition("lastModifiedDate", EntityOperator.EQUALS, null),
						EntityOperator.OR, EntityCondition.makeCondition("lastModifiedDate",
								EntityOperator.GREATER_THAN_EQUAL_TO, modifiedDateRangeStart)));
			}
			if (UtilValidate.isNotEmpty(modifiedDateRangeEnd)) {
				conditionList.add(EntityCondition.makeCondition(
						EntityCondition.makeCondition("lastModifiedDate", EntityOperator.EQUALS, null),
						EntityOperator.OR, EntityCondition.makeCondition("lastModifiedDate",
								EntityOperator.GREATER_THAN_EQUAL_TO, modifiedDateRangeEnd)));
			}
			if (UtilValidate.isNotEmpty(subStatusId)) {
				conditionList.add(EntityCondition.makeCondition("subStatusId", EntityOperator.EQUALS, subStatusId));
			}
			if (UtilValidate.isNotEmpty(dueDateRangeStart)) {
				conditionList.add(EntityCondition.makeCondition(
						EntityCondition.makeCondition("actualStartDate", EntityOperator.EQUALS, null),
						EntityOperator.OR, EntityCondition.makeCondition("actualStartDate",
								EntityOperator.GREATER_THAN_EQUAL_TO, dueDateRangeStart)));
			}
			if (UtilValidate.isNotEmpty(dueDateRangeEnd)) {
				conditionList.add(EntityCondition.makeCondition(
						EntityCondition.makeCondition("actualEndDate", EntityOperator.EQUALS, null), EntityOperator.OR,
						EntityCondition.makeCondition("actualEndDate", EntityOperator.LESS_THAN_EQUAL_TO,
								dueDateRangeEnd)));
			}
//			if (0 == priority) {
//				priority = null;
//			}
			if (UtilValidate.isNotEmpty(priority)) {
				conditionList.add(EntityCondition.makeCondition("priority", EntityOperator.EQUALS, priority));
			}
			if (UtilValidate.isNotEmpty(srType)) {
				conditionList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, srType));
			}
			if (UtilValidate.isNotEmpty(srCategory)) {
				conditionList
						.add(EntityCondition.makeCondition("custRequestCategoryId", EntityOperator.EQUALS, srCategory));
			}
			if (UtilValidate.isNotEmpty(srSubCategory)) {
				conditionList.add(EntityCondition.makeCondition("custRequestSubCategoryId", EntityOperator.EQUALS,
						srSubCategory));
			}
			if (UtilValidate.isNotEmpty(responsibleLoginIds)) {
				conditionList.add(
						EntityCondition.makeCondition("responsiblePerson", EntityOperator.IN, responsibleLoginIds));
			}
			if (UtilValidate.isNotEmpty(emplLoginIds)) {
				conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplLoginIds));
			}
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			
			Long count = new Long(0);
			EntityFindOptions efoNum = new EntityFindOptions();
			efoNum.setFetchSize(1000);
			Debug.logInfo("count 1 start: " + UtilDateTime.nowTimestamp(), MODULE);
			count = delegator.findCountByCondition("CustRequestSummary", mainConditons, null, null, null);
			Debug.logInfo("count 2 end: " + UtilDateTime.nowTimestamp(), MODULE);
			count = count > 1000 ? 1000 : count;
			EntityFindOptions efo = new EntityFindOptions();
			if (UtilValidate.isNotEmpty(nextPageNum) && count > 0) {
				long npn = Long.parseLong(nextPageNum) - 1;
				int startInx = (int) (npn * GlobalConstants.DEFAULT_PER_PAGE_COUNT);
				int endInx = count.intValue() < GlobalConstants.DEFAULT_PER_PAGE_COUNT ? count.intValue() : GlobalConstants.DEFAULT_PER_PAGE_COUNT;        
				efo.setOffset(startInx);
				efo.setLimit(endInx);
			}
			Debug.log("mainConditons===" + mainConditons);
			
			if (count > 0) {
				serviceRequestList = delegator.findList("CustRequestSummary", mainConditons, null, UtilMisc.toList("custRequestId DESC"), efo, false);
			}
			
			result.put("serviceRequestList", serviceRequestList);
			result.put("totalCount", count);
		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.putAll(ServiceUtil.returnSuccess("Successfully find service request.."));
		return result;
	}
}