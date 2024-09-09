package org.fio.sales.portal.event;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilGenerator;
import org.fio.homeapps.util.UtilHttp;
import org.fio.sales.portal.util.DataHelper;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
import org.groupfio.common.portal.util.SrUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.website.WebSiteWorker;

import com.fasterxml.jackson.databind.ObjectMapper;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CommonEvents {
	private static final String MODULE = CommonEvents.class.getName();
	private static final String RESOURCE = "SalesPortalUiLabels";
	private CommonEvents() {}
	
	public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
		return doJSONResponse(response, jsonObject.toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Collection < ? > collection) {
		return doJSONResponse(response, JSONArray.fromObject(collection).toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Map map) {
		return doJSONResponse(response, JSONObject.fromObject(map));
	}

	public static String doJSONResponse(HttpServletResponse response, String jsonString) {
		String result = "success";

		response.setContentType("application/x-json");
		try {
			response.setContentLength(jsonString.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
			Debug.logWarning("Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(), MODULE);
			response.setContentLength(jsonString.length());
		}

		Writer out;
		try {
			out = response.getWriter();
			out.write(jsonString);
			out.flush();
		} catch (IOException e) {
			Debug.logError(e, "Failed to get response writer", MODULE);
			result = "error";
		}
		return result;
	}

	public static String returnError(HttpServletRequest request, String errorMessage) {
		try {
			request.setAttribute("_ERROR_MESSAGE_", "ERROR :" + errorMessage);
			Debug.logError("Error : " + errorMessage, MODULE);
		} catch (Exception e) {
			Debug.logError("Error : " + e.getMessage(), MODULE);
		}
		return "error";
	}

	public static String returnSuccess(HttpServletRequest request, String successMessage) {
		try {
			request.setAttribute("_EVENT_MESSAGE_", successMessage);
			Debug.logError("Success : " + successMessage, MODULE);
		} catch (Exception e) {
			Debug.logError("Error : " + e.getMessage(), MODULE);
		}
		return "success";
	}

	public static GenericValue getUserLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (GenericValue) session.getAttribute("userLogin");
	}

	@SuppressWarnings("unchecked")
	public static String eventCreateSrActivity(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String workEffortTypeId = (String) context.get("workEffortTypeId");
		String workEffortPurposeTypeId = (String) context.get("workEffortPurposeTypeId");
		String srTypeId = (String) context.get("srTypeId");
		String srSubTypeId = (String) context.get("srSubTypeId");
		String taskDate = (String) context.get("taskDate");
		String callDateTime = (String) context.get("callDateTime");
		String callBackDate = (String) context.get("callBackDate");
		String estimatedCompletionDate = (String) context.get("estimatedCompletionDate");
		String estimatedStartDate = (String) context.get("estimatedStartDate");
		String actualStartDate = (String) context.get("actualStartDate");
		String actualCompletionDate = (String) context.get("actualCompletionDate");
		String subject = (String) context.get("subject");
		String resolution = (String) context.get("resolution");
		String linkedFrom = (String) context.get("linkedFrom");
		String productId = (String) context.get("productId");
		String account = (String) context.get("account");
		String accountProduct = (String) context.get("accountProduct");
		String emailFormContent = (String) context.get("emailFormContent");
		String onceDone = (String) context.get("onceDone");
		String messages = (String) context.get("messages");
		String location = (String) context.get("location");
		String startTime = (String) context.get("startTime");
		String duration = (String) context.get("duration");
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		//String owner = (String) context.get("owner");
		String emplTeamId = (String) request.getParameter("emplTeamId");
		String ownerBu = (String) context.get("ownerBu");
		String cNo = (String) context.get("cNo");
		String priority = (String) context.get("priority");
		String direction = (String) context.get("direction");
		String phoneNumber = (String) context.get("phoneNumber");
		String nsender = (String) context.get("nsender");
		String nto = (String) context.get("nto");
		String ncc = (String) context.get("ccEmailIds");
		String nbcc = (String) context.get("nbcc");
		String template = (String) context.get("template");
		String custRequestId = (String) context.get("custRequestId");
		String activeTab = (String) context.get("activeTab");
		String srCommHistoryFlag = (String) context.get("srCommHistoryFlag");
		String responseMessage = UtilProperties.getMessage(RESOURCE, "SalesActivityCreatedSuccessfully", locale);
		String errorMessage = UtilProperties.getMessage(RESOURCE, "SalesActivityCreationError", locale);
		String defaultFromEmailId = (String) context.get("defaultFromEmailId");
		String clientPortal = (String) context.get("clientPortal");
		
		estimatedStartDate = (String) context.get("estimatedStartDate_date");
		if (UtilValidate.isEmpty(estimatedCompletionDate)) {
			estimatedCompletionDate = (String) context.get("estimatedCompletionDate_date");
		}
		actualStartDate = (String) context.get("actualStartDate_date");
		actualCompletionDate = (String) context.get("actualCompletionDate_date");
		
		String estimatedStartTime = (String) context.get("estimatedStartDate_time");
		String estimatedCompletionTime = (String) context.get("estimatedCompletionDate_time");
		String actualStartTime = (String) context.get("actualStartDate_time");
		String actualCompletionTime = (String) context.get("actualCompletionDate_time");
		
		String createdDateStr = (String) context.get("createdDate");
		
		String ownerBookedCalSlots = (String) context.get("ownerBookedCalSlots");
		
		List<String> ownerList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(context.get("owner")) && context.get("owner") instanceof String) {
			String owner1 = (String) context.get("owner");
			if(UtilValidate.isNotEmpty(owner1)) ownerList.add(owner1);
		} else if(UtilValidate.isNotEmpty(context.get("owner")) && context.get("owner") instanceof List<?>) {
			ownerList = new ArrayList<>(
				      new HashSet<>((List<String>) context.get("owner"))).stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toList());
		}
		
		Set<String> ntoList = new HashSet<>();
		String recipientPartyEmails = (String) context.get("recipientPartyEmails");
		if (UtilValidate.isNotEmpty(recipientPartyEmails)) {
			//nto = UtilValidate.isNotEmpty(nto) ? nto+","+recipientPartyEmails : recipientPartyEmails;
			for (String ntoEmail : recipientPartyEmails.split(",")) {
				ntoList.add(ntoEmail);
			}
			nto = StringUtil.join(ntoList, ",");
			ncc = null;
		}
		
		String arrivalWindow = (String) context.get("arrivalWindow");
		String isSchedulingRequired = (String) context.get("isSchedulingRequired");
		String callStatus = (String) context.get("callStatus");
		
		String sendEmail = "Y";
		if ((UtilValidate.isNotEmpty(direction) && direction.equals("IN")) && UtilValidate.isEmpty(clientPortal) ){
			sendEmail = "N";
		}
		if (isSchedulingRequired == null) {
			isSchedulingRequired = "N";
		}
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
			String isEnableSrOutboundEmail = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_ENBL_SROB_EMAIL");
			
			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String nativeBusinessUnit = null;
			String nativeTeamId = null;

			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId)) {

				estimatedStartTime = UtilValidate.isNotEmpty(estimatedStartTime) ? estimatedStartTime : "00:00";
				estimatedCompletionTime = UtilValidate.isNotEmpty(estimatedCompletionTime) ? estimatedCompletionTime : "00:00";
				
				Timestamp estimatedStartDateTime = ParamUtil.getTimestamp(estimatedStartDate, estimatedStartTime, "yyyy-MM-dd HH:mm");
				Timestamp estimatedCompletionDateTime = ParamUtil.getTimestamp(estimatedCompletionDate, estimatedCompletionTime, "yyyy-MM-dd HH:mm");
				Timestamp actualStartDateTime = ParamUtil.getTimestamp(actualStartDate, actualStartTime, "yyyy-MM-dd HH:mm");
				Timestamp actualCompletionDateTime = ParamUtil.getTimestamp(actualCompletionDate, actualCompletionTime, "yyyy-MM-dd HH:mm");
				
				if (UtilValidate.isNotEmpty(estimatedStartDateTime)) {
					estimatedStartDate = UtilDateTime.timeStampToString(estimatedStartDateTime, globalDateTimeFormat, TimeZone.getDefault(), null);
				}
				if (UtilValidate.isNotEmpty(estimatedCompletionDateTime)) {
					estimatedCompletionDate = UtilDateTime.timeStampToString(estimatedCompletionDateTime, globalDateTimeFormat, TimeZone.getDefault(), null);
				}
				if (UtilValidate.isNotEmpty(actualStartDateTime)) {
					actualStartDate = UtilDateTime.timeStampToString(actualStartDateTime, globalDateTimeFormat, TimeZone.getDefault(), null);
				}
				if (UtilValidate.isNotEmpty(actualCompletionDateTime)) {
					actualCompletionDate = UtilDateTime.timeStampToString(actualCompletionDateTime, globalDateTimeFormat, TimeZone.getDefault(), null);
				}
				
				String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
				Map<String, Object> buTeamData = DataUtil.getUserBuTeam(delegator, userLoginPartyId);
				nativeBusinessUnit = (String) buTeamData.get("businessUnit");
				nativeTeamId = (String) buTeamData.get("emplTeamId");
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", nativeBusinessUnit);
				accessMatrixMap.put("modeOfOp", "Create");
				accessMatrixMap.put("entityName", "WorkEffort");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
				
				//validate the common team and access for the assignment
				String currentPartyId = userLoginPartyId;
				if(UtilValidate.isEmpty(ownerList)) {
					ownerList.add(userLoginId);
				} else {
					for(String owner : ownerList) {
						currentPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
						buTeamData = DataUtil.getUserBuTeam(delegator, currentPartyId);
						nativeBusinessUnit = (String) buTeamData.get("businessUnit");
						nativeTeamId = (String) buTeamData.get("emplTeamId");

						//check both users are the same team or Bu or not (need clarification) 
						//check with ownerId
						if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
							@SuppressWarnings("unchecked")
							List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
							if(!ownerIds.contains(nativeTeamId)) accessLevel = null;
						}

						//check with emplTeamId
						if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
							@SuppressWarnings("unchecked")
							List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
							if(!emplTeamIds.contains(nativeTeamId)) accessLevel = null;
						}
						if(accessLevel == null) break;
					}
				}

				//if(UtilValidate.isEmpty(emplTeamId)) emplTeamId = nativeTeamId; 
			}
			
			//accessLevel = "Y";
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				List<String> optionalAttendees = null;
				List<String> requiredAttendees = null;
				
				String partyId = null;
				if (UtilValidate.isNotEmpty(context.get("partyId"))) {
					if (context.get("partyId") instanceof List) {
						partyId = (String) ((List) context.get("partyId")).get(0);
					} else if (context.get("partyId") instanceof String) {
						partyId = (String) context.get("partyId");
					}
				}
				
				String callTime = (String) context.get("callTime");
				String optionalAttendeesClassName = "";
				String requiredAttendeesClassName = "";

				if (UtilValidate.isNotEmpty(context.get("optionalAttendees"))) {
					optionalAttendeesClassName = context.get("optionalAttendees").getClass().getName();
				}
				if (UtilValidate.isNotEmpty(context.get("requiredAttendees"))) {
					requiredAttendeesClassName = context.get("requiredAttendees").getClass().getName();
				}

				if (UtilValidate.isNotEmpty(optionalAttendeesClassName)
						&& "java.lang.String".equals(optionalAttendeesClassName)) {
					optionalAttendees = UtilMisc.toList((String) context.get("optionalAttendees"));
				} else if (UtilValidate.isNotEmpty(optionalAttendeesClassName)
						&& "java.util.LinkedList".equals(optionalAttendeesClassName)) {
					optionalAttendees = (List<String>) context.get("optionalAttendees");
				}

				if (UtilValidate.isNotEmpty(requiredAttendeesClassName)
						&& "java.lang.String".equals(requiredAttendeesClassName)) {
					requiredAttendees = UtilMisc.toList((String) context.get("requiredAttendees"));
				} else if (UtilValidate.isNotEmpty(requiredAttendeesClassName)
						&& "java.util.LinkedList".equals(requiredAttendeesClassName)) {
					requiredAttendees = (List<String>) context.get("requiredAttendees");
				}
				//added for mail to optional attendees
				List<String> optionalAttParties = new ArrayList();
				String optionalAttendeesEmailIds = "";
				
				if(UtilValidate.isNotEmpty(custRequestId)){
					List optAttendeesConditionList = FastList.newInstance();
					optAttendeesConditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
					optAttendeesConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
					optAttendeesConditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "N"));
					EntityCondition optAttendeesCondition = EntityCondition.makeCondition( optAttendeesConditionList,EntityOperator.AND);
					List < GenericValue > custRequestOptAttendees = EntityQuery.use(delegator).select("partyId").from("CustRequestContact").where(optAttendeesCondition).queryList();
					optionalAttParties = EntityUtil.getFieldListFromEntityList(custRequestOptAttendees, "partyId", true);
					if (UtilValidate.isNotEmpty(optionalAttParties)) {
						for (String eachOptionalAttendee : optionalAttParties) {
							Map<String, String> optionalAttendeeContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,eachOptionalAttendee,UtilMisc.toMap("isRetriveEmail", true),true);
							if (UtilValidate.isNotEmpty(optionalAttendeeContactInformation) && UtilValidate.isNotEmpty(optionalAttendeeContactInformation.get("EmailAddress"))){
								String optionalAttendeeEmailAddress = optionalAttendeeContactInformation.get("EmailAddress");
								optionalAttendeesEmailIds = optionalAttendeesEmailIds+optionalAttendeeEmailAddress+",";
							}
						}
						if (UtilValidate.isNotEmpty(optionalAttendeesEmailIds)) {
							optionalAttendeesEmailIds = optionalAttendeesEmailIds.substring(0, optionalAttendeesEmailIds.length()-1);
						}
					}
				}
				//ended
				
				String nrecepient = (String) context.get("nrecepient");
				String norganizer = (String) context.get("norganizer");
				String isPhoneCall = (String) context.get("isPhoneCall");
				String entityTimeZoneId = (String) context.get("timeZoneDesc");
				String currentStatusId = (String) context.get("statusId");
				String domainEntityType = (String) context.get("domainEntityType");
				String domainEntityId = (String) context.get("domainEntityId");
				String communicationEventId = (String) context.get("communicationEventId");
				String emailContent = (String) context.get("emailContent");
				boolean existing = ((communicationEventId == null) || communicationEventId.equals("") ? false : true);
				String contactId = (String) request.getParameter("contactId");
				if (UtilValidate.isEmpty(contactId)){
					contactId = (String) context.get("contactId");
				}
				
				Map inMap = FastMap.newInstance();
				Map outMap = FastMap.newInstance();
				GenericValue callRecordMaster = null;
				request.setAttribute("salesOpportunityId", UtilValidate.isNotEmpty(salesOpportunityId) ? salesOpportunityId : UtilValidate.isNotEmpty(domainEntityType) && "OPPORTUNITY".equals(domainEntityType) ? domainEntityId :"");
				request.setAttribute("custRequestId", custRequestId);
				request.setAttribute("partyId", partyId);
				request.setAttribute("cNo", cNo);
				request.setAttribute("domainEntityType", domainEntityType);
				request.setAttribute("domainEntityId", domainEntityId);
				try {
					emailContent = org.fio.sales.portal.util.DataHelper.prepareEmailContent(emailContent);
					Map<String, Object> inputMap = new HashMap<String, Object>();
					inputMap.put("workEffortTypeId", workEffortTypeId);
					inputMap.put("srTypeId", srTypeId);
					inputMap.put("srSubTypeId", srSubTypeId);
					inputMap.put("taskDate", taskDate);
					inputMap.put("callDateTime", callDateTime);
					if (UtilValidate.isNotEmpty(callBackDate)) {
						inputMap.put("callBackDate", callBackDate);
					}
					inputMap.put("callTime", callTime);
					inputMap.put("actualCompletionDate", actualCompletionDate);
					inputMap.put("estimatedCompletionDate", estimatedCompletionDate);
					inputMap.put("estimatedStartDate", estimatedStartDate);
					inputMap.put("actualStartDate", actualStartDate);
					
					inputMap.put("subject", subject);
					inputMap.put("resolution", resolution);
					inputMap.put("linkedFrom", linkedFrom);
					inputMap.put("productId", productId);
					inputMap.put("currentStatusId", currentStatusId);
					inputMap.put("account", account);
					inputMap.put("accountProduct", accountProduct);
					inputMap.put("emailFormContent", emailFormContent);
					inputMap.put("onceDone", onceDone);
					inputMap.put("messages", messages);
					inputMap.put("location", location);
					inputMap.put("startTime", startTime);
					inputMap.put("entityTimeZoneId", entityTimeZoneId);
					inputMap.put("duration", duration);
					inputMap.put("salesOpportunityId", salesOpportunityId);
					inputMap.put("emplTeamId", emplTeamId);
					inputMap.put("ownerBookedCalSlots", ownerBookedCalSlots);
					
					inputMap.put("ownerList", ownerList);
					
					inputMap.put("ownerBu", ownerBu);
					inputMap.put("cNo", cNo);
					inputMap.put("priority", priority);
					inputMap.put("direction", direction);
					inputMap.put("phoneNumber", phoneNumber);
					inputMap.put("nsender", nsender);
					inputMap.put("nto", nto);
					inputMap.put("ncc", ncc);
					inputMap.put("nbcc", nbcc);
					inputMap.put("template", template);
					inputMap.put("optionalAttendees", optionalAttendees);
					inputMap.put("requiredAttendees", requiredAttendees);
					if (UtilValidate.isNotEmpty(norganizer)) {
						inputMap.put("norganizer", norganizer);
					}
					if (UtilValidate.isNotEmpty(nrecepient)) {
						inputMap.put("nrecepient", nrecepient);
					}
					inputMap.put("isPhoneCall", isPhoneCall);
					inputMap.put("userLogin", userLogin);
					inputMap.put("domainEntityType", domainEntityType);
					inputMap.put("domainEntityId", domainEntityId);
					if (UtilValidate.isNotEmpty(workEffortTypeId)
							&& ("Phone Call".equals(workEffortTypeId) || "E-mail".equals(workEffortTypeId))) {
						onceDone = "Y";
						inputMap.put("onceDone", onceDone);
					}
					if (UtilValidate.isNotEmpty(onceDone) && "Y".equals(onceDone)) {
						inputMap.put("currentStatusId", "IA_MCOMPLETED");
					}
					if(UtilValidate.isNotEmpty(workEffortPurposeTypeId)){						
						inputMap.put("workEffortPurposeTypeId",workEffortPurposeTypeId);
					}
					
					Timestamp createdDate = ParamUtil.getTimestamp(createdDateStr, "yyyy-MM-dd");
					if (UtilValidate.isNotEmpty(createdDate)) {
						createdDateStr = UtilDateTime.timeStampToString(createdDate, globalDateFormat, TimeZone.getDefault(), null);
					}
					inputMap.put("createdDate", createdDateStr);
						
					Map<String, Object> res = dispatcher.runSync("salesPortal.eventCreateSrActivity", inputMap);
					Map<String, Object> tmpResult = null;
					
					if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("workEffortId"))) {

						String workEffortId = (String) res.get("workEffortId");
						if (UtilValidate.isNotEmpty(workEffortTypeId) && workEffortTypeId.equals("E-mail")) {
							responseMessage = UtilProperties.getMessage(RESOURCE,
									"Email Activity Created Successfully " + ": " + workEffortId, locale);
						} else if (UtilValidate.isNotEmpty(workEffortTypeId) && workEffortTypeId.equals("Phone Call")) {
							responseMessage = UtilProperties.getMessage(RESOURCE,
									"Phone Call Activity Created Successfully " + ": " + workEffortId, locale);
						} else if (UtilValidate.isNotEmpty(workEffortTypeId) && workEffortTypeId.equals("Task")) {
							responseMessage = UtilProperties.getMessage(RESOURCE,
									"Task Activity Created Successfully " + ": " + workEffortId, locale);
						} else if (UtilValidate.isNotEmpty(workEffortTypeId) && workEffortTypeId.equals("Appointment")) {
							responseMessage = UtilProperties.getMessage(RESOURCE,
									"Appointment Activity Created Successfully " + ": " + workEffortId, locale);
						}
						if (UtilValidate.isNotEmpty(workEffortTypeId) && workEffortTypeId.equals("E-mail")) {
							if (UtilValidate.isNotEmpty(srCommHistoryFlag) && "Y".equals(srCommHistoryFlag)) {
								responseMessage = UtilProperties.getMessage(RESOURCE, "SR is Updated Successfully ["+custRequestId+"]", locale);
							}
						}
						request.setAttribute("workEffortId", workEffortId);
						request.setAttribute("custRequestId", custRequestId);
						request.setAttribute("activeTab", activeTab);
						
						if (UtilValidate.isNotEmpty(workEffortTypeId) && workEffortTypeId.equals("E-mail")) {
							ModelService modelService = null;
							String serviceName = (existing ? "updateCommunicationEvent" : "createCommunicationEvent");
							ModelService service = dispatcher.getDispatchContext().getModelService(serviceName);
							Map<String, Object> input = service.makeValid(context, "IN");
							String validToAddresses = null;
							String toAddresses = (String) context.get("nto");
							if (UtilValidate.isNotEmpty(toAddresses)) {
								validToAddresses = StringUtil.join(UtilMisc.toList(toAddresses), ",");
								input.put("toString", validToAddresses);
							} else {
								if (UtilValidate.isNotEmpty(nto)) {
									Debug.logError("No valid email addresses could be found from: [" + nto + "]", MODULE);
								}
							}
							
							// prepare email content [start]
							Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
							
							extractContext.put("delegator", delegator);
							extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
							extractContext.put("fromEmail", nsender);
							extractContext.put("toEmail", nto);
							extractContext.put("partyId", partyId);
							extractContext.put("emailContent", emailContent);

							Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
							emailContent = (String) extractResultContext.get("emailContent");
							// prepare email content [end]

							EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("infoString", EntityOperator.IN,
											UtilMisc.toList(toAddresses)),
									EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, "_NA_"));
							GenericValue partyContactMechTo = EntityUtil
									.getFirst(EntityUtil.filterByDate(delegator.findList("PartyAndContactMech", conditions,
											null, UtilMisc.toList("fromDate DESC"), null, false)));
							if (UtilValidate.isNotEmpty(partyContactMechTo)) {
								input.put("contactMechIdTo", partyContactMechTo.getString("contactMechId"));
								input.put("partyIdTo", partyContactMechTo.getString("partyId"));
								input.put("roleTypeIdTo", partyContactMechTo.getString("roleTypeId"));
							}
							if (existing) {
								input.put("communicationEventId", communicationEventId);
							} else {
								input.put("entryDate", UtilDateTime.nowTimestamp());
							}
							input.put("contactMechTypeId", "EMAIL_ADDRESS");
							input.put("communicationEventTypeId", "EMAIL_COMMUNICATION");
							input.put("statusId", "COM_PENDING");
							input.put("partyIdFrom", userLogin.getString("partyId"));
							input.put("content", emailContent);
							// String ccAddresses = ((String) context.get("ncc"));
							String ccAddresses = ncc;
							String validCCAddresses = null;
							String ccString = null;
							if (UtilValidate.isNotEmpty(ccAddresses)) {

								validCCAddresses = StringUtil.join(UtilMisc.toList(ccAddresses), ",");
								//input.put("ccString", validCCAddresses);
								if(UtilValidate.isNotEmpty(optionalAttendeesEmailIds)){
									validCCAddresses = validCCAddresses+","+optionalAttendeesEmailIds;
								}	
								// validCCAddresses = StringUtil.join(ccAddresses, ",");
								ccString = validCCAddresses;
							}
							else{
								ccAddresses = optionalAttendeesEmailIds;
								ccString = ccAddresses;
							}
							
							/*if (UtilValidate.isNotEmpty(clientPortal) && "clientPortal".equals(clientPortal)){
								GenericValue sytemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryOne();
								if(UtilValidate.isNotEmpty(sytemProperty)){
									ccString = UtilValidate.isNotEmpty(ccString) ? ","+sytemProperty.getString("systemPropertyValue") : sytemProperty.getString("systemPropertyValue");
								}
							}*/
							
							Set<String> ccList = new HashSet<>();
							if (UtilValidate.isNotEmpty(ccString)) {
								for (String ccEmail : ccString.split(",")) {
									if (!ntoList.contains(ccEmail)) {
										ccList.add(ccEmail);
									}
								}
								ccString = StringUtil.join(ccList, ",");
							}
							
							input.put("ccString", ccString);
							
							String validBCCAddresses = null;
							String bccAddresses = ((String) context.get("nbcc"));
							if (UtilValidate.isNotEmpty(bccAddresses)) {
								validBCCAddresses = StringUtil.join(UtilMisc.toList(bccAddresses), ",");
								input.put("bccString", validBCCAddresses);
							}
							try {
								Map<String, Object> serviceResults = dispatcher.runSync(serviceName, input);
								communicationEventId = (String) serviceResults.get("communicationEventId");
							} catch (GenericServiceException e) {
								String errMsg = "Problem caling createeventservice: " + e.toString();
								Debug.logError(e, errMsg, MODULE);
								request.setAttribute("_ERROR_MESSAGE_", errMsg);
								return "error";
							}
							String websiteId = (String) context.get("websiteId");
							if (UtilValidate.isEmpty(websiteId)) {
								websiteId = WebSiteWorker.getWebSiteId(request);
							}
							context.put("locale", UtilHttp.getLocale(request));
							context.put("userLogin", userLogin);
							context.put("partyId", emplTeamId);
							context.put("templateId", (String) context.get("emailTemplate"));
							
							Map<String, Object> callCtxt = FastMap.newInstance();
							Map<String, Object> callResult = FastMap.newInstance();
							Map<String, Object> requestContext = FastMap.newInstance();
							
							requestContext.putAll(context);
							requestContext.put("communicationEventId", communicationEventId);
							requestContext.put("emailContent", emailContent);
							requestContext.put("emplTeamId", emplTeamId);
							requestContext.put("ccAddresses", ccString);
							requestContext.put("contentTypeId", "ATTACHMENT");
							
							requestContext.put("partyId", partyId);
							requestContext.put("salesOpportunityId", salesOpportunityId);
							requestContext.put("custRequestId", custRequestId);
							requestContext.put("domainEntityType", domainEntityType);
							requestContext.put("domainEntityId", domainEntityId);
							requestContext.put("linkedFrom", workEffortId);
							
							requestContext.put("workEffortTypeId", workEffortTypeId);
							requestContext.put("workEffortId", workEffortId);
							
							callCtxt.put("requestContext", requestContext);
							callCtxt.put("userLogin", userLogin);
							
							if (UtilValidate.isNotEmpty(srCommHistoryFlag) && "Y".equals(srCommHistoryFlag)) {
								
								String statusId = (String) context.get("srStatusId");
								String custRequestName = (String) context.get("srName");
								String externalLoginKey = (String) context.get("externalLoginKey");
								
								String partyDesc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, cNo, false);
								List<String> ownerNames = new LinkedList<String>(); 
								if(UtilValidate.isNotEmpty(ownerList)) {
									for(String owner : ownerList) {
										String ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
										ownerNames.add(DataUtil.getUserLoginName(delegator, ownerPartyId));
									}
								}
								if(UtilValidate.isNotEmpty(ntoList)) {
									ownerNames = new LinkedList<String>();
									for(String toEmail : ntoList) {
										String ptyId = DataUtil.getPartyIdByPrimaryEmail(delegator, toEmail);
										ownerNames.add(DataUtil.getUserLoginName(delegator, ptyId));
									}
								}
								
								String ownerDesc = UtilValidate.isNotEmpty(ownerNames) ? org.fio.admin.portal.util.DataUtil.listToString(ownerNames) : "";
				                //String ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, owner, false);
				                String srStatusDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId);
				                String signName = "";
								
				                if (UtilValidate.isNotEmpty(clientPortal) && "clientPortal".equals(clientPortal)){
				                	GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryOne();
				                	if (UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(custRequest.getString("statusId")) && "SR_PENDING".equals(custRequest.getString("statusId"))){
				                		custRequest.put("custRequestId", custRequestId);
				                		custRequest.put("statusId", "SR_FEED_PROVIDED");
				                		delegator.store(custRequest);
				                		Map<String, Object> historyInputMap = new HashMap<String, Object>();
				                		historyInputMap.put("custRequestId", custRequestId);
				                		historyInputMap.put("userLogin", userLogin);
				                		
				                		Map<String, Object> historyOutMap = dispatcher.runSync("srPortal.createSrHistory", historyInputMap);
				                		
				                		if(!ServiceUtil.isSuccess(historyOutMap)) {
				                			request.setAttribute("_ERROR_MESSAGE_", "Problem While Creating Service Request History");
				                			return "error";
				                		}
				                	}
				                	if (UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(custRequest.getString("responsiblePerson"))){
				                		ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, custRequest.getString("responsiblePerson"), false);
				                		String responsiblePartyId = DataUtil.getUserLoginPartyId(delegator, custRequest.getString("responsiblePerson"));
				                		Map<String, String> responsiblePersonContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,responsiblePartyId,UtilMisc.toMap("isRetriveEmail", true),true);
										//ccAddresses = nto;
										nto = responsiblePersonContactInformation.get("EmailAddress");
				                	}else{
				                		ownerDesc = (String) context.get("cspSupportName");
				                	}
				                	GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);
									if(UtilValidate.isNotEmpty(personGv)){
										signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
									}
									
				                } else {
				                	requestContext.put("emplTeamId", "");
				                	signName = "CRM Administrator.";
				                	GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);
									if(UtilValidate.isNotEmpty(personGv)){
										signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
									}
									Map<String, String> loggedInContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLogin.getString("partyId"));
									if (UtilValidate.isNotEmpty(loggedInContactInformation) && UtilValidate.isNotEmpty(loggedInContactInformation.get("EmailAddress"))){
										ccAddresses = ccAddresses+","+loggedInContactInformation.get("EmailAddress");
									}
									
				                }
				                
				                String applicationType = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_TYPE");
				                
								String appUrl = "";
			                	GenericValue systemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "EMAIL_NOTIFICATION", "systemPropertyId", "url").queryOne();
			                	if(UtilValidate.isNotEmpty(systemProperty)){
			                		appUrl = systemProperty.getString("systemPropertyValue");
			                	}else{
			                		appUrl = (String) context.get("appUrl");
			                	}
			                	
			                	ccList = new HashSet<>();
								if (UtilValidate.isNotEmpty(ccAddresses)) {
									for (String ccEmail : ccAddresses.split(",")) {
										if (!ntoList.contains(ccEmail)) {
											ccList.add(ccEmail);
										}
									}
									ccString = StringUtil.join(ccList, ",");
								}
								
								//String dearSection = ownerDesc;
								String dearSection = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, contactId, false);
								String thanksSection = signName;

								if (UtilValidate.isNotEmpty(applicationType) && (applicationType.equals("B2C") || applicationType.equals("BOTH")) ) {
									if(UtilValidate.isNotEmpty(dearSection)) {
									}else if (UtilValidate.isNotEmpty(cNo)) {
										dearSection = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, cNo, false);
										thanksSection = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, userLogin.getString("partyId"), false);
									}
									if (UtilValidate.isEmpty(nto)) {
										nto = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_DEFAULT_EMAIL");
									}
								}
			                	
			                	String strVar="";
			                	strVar += "<html>";
			                	strVar += "<head>";
			                	strVar += " <title></title>";
			                	strVar += "</head>";
			                	strVar += "";
			                	strVar += "<body>";
			                	strVar += "";
			                	strVar += "<table style=\"font-family:Verdana; font-size:12px;\">";
			                	strVar += "<tr>";
			                	strVar += "<td colspan=\"3\">Dear "+dearSection+"</td>";
			                	strVar += "</tr>";
			                	strVar += "<tr>";
			                	strVar += "<td>&nbsp;</td>";
			                	strVar += "<td>&nbsp;</td>";
			                	strVar += "</tr>";
			                	strVar += "<tr>";
		                    	strVar += "<td colspan=\"3\">An update has been made to the support SR:</td>";
		                    	strVar += "</tr>";
		                    	strVar += "<tr>";
		                    	strVar += "<td>&nbsp;</td>";
		                    	strVar += "<td>&nbsp;</td>";
		                    	strVar += "</tr>";
			                	strVar += "<tr>";
			                	if (UtilValidate.isNotEmpty(applicationType) && applicationType.equals("B2B")) {
									strVar += "<td width=\"15%\"><b>Customer</b></td>";
								} else {
									strVar += "<td width=\"15%\"><b>Customer</b></td>";
								}
			                	strVar += "<td>:</td>";
			                	strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+partyDesc+"</td>";
			                	strVar += "</tr>";
			                	strVar += "<tr>";
			                	strVar += "<td><b>SR ID</b></td>";
			                	strVar += "<td>:</td>";
			                	
			                	strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">";
			                	if (UtilValidate.isEmpty(applicationType) || applicationType.equals("B2B") || applicationType.equals("BOTH")) {
			                		if(domainEntityType.equalsIgnoreCase("SERVICE_REQUEST") || domainEntityType.equalsIgnoreCase("CLIENT_SERVICE_REQUEST")) {
			                			strVar += "<a target=\"_blank\" href="+appUrl+"/ticketc-portal/control/viewServiceRequest?srNumber="+custRequestId+">"+custRequestId+"</a>";
			                		}else{
			                			strVar += "<a target=\"_blank\" href="+appUrl+"/client-portal/control/viewServiceRequest?srNumber="+custRequestId+">"+custRequestId+"</a>";
			                		}
								} else {
									strVar += custRequestId;
								}
			                	strVar += "</td>";
			                	
			                	strVar += "</tr>";
			                	strVar += "<tr>";
			                	strVar += "<td><b>SR Status</b></td>";
			                	strVar += "<td>:</td>";
			                	strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+srStatusDesc+"</td>";
			                	strVar += "</tr>";
			                	
			                	if (UtilValidate.isNotEmpty(custRequestName)) {
			                		strVar += "<tr>";
				                	strVar += "<td style=\"vertical-align: top;\"><b>SR Name</b></td>";
				                	strVar += "<td style=\"vertical-align: top;\">:</td>";
				                	strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+custRequestName+"</td>";
				                	strVar += "</tr>";
								}
			                	strVar += "<tr>";
			                	strVar += "<td style=\"vertical-align: top;\"><b>Description</b></td>";
			                	strVar += "<td style=\"vertical-align: top;\">:</td>";
			                	strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+emailContent+"</td>";
			                	strVar += "</tr>";
			                	strVar += "";
			                	strVar += "<tr>";
			                	strVar += "<td colspan=\"2\">&nbsp;</td>";
			                	strVar += "</tr>";
			                	strVar += "<tr>";
			                	strVar += "<td colspan=\"3\">";
			                	strVar += "Thanks,<br>";
			                	strVar += ""+thanksSection+"";
			                	strVar += "</td>";
			                	strVar += "</tr>";
			                	strVar += "</table>";
			                	strVar += "";
			                	strVar += "</body>";
			                	strVar += "</html>";
			                	strVar += "";
			                	
								//Map<String, Object> callCtxtt = FastMap.newInstance();
			                	//Map<String, Object> callResultt = FastMap.newInstance();
			                	//Map<String, Object> requestContextt = FastMap.newInstance();
			                	
			                	if (UtilValidate.isNotEmpty(clientPortal) && "clientPortal".equals(clientPortal)){
			                		requestContext.put("nsender", nsender);
			                	}else{
			                		requestContext.put("nsender", defaultFromEmailId);
			                	}
			                	requestContext.put("nto", nto);
			                	requestContext.put("subject", subject);
			                	requestContext.put("emailContent", strVar);
			                	requestContext.put("ccAddresses", ccString);

			                	callCtxt.put("requestContext", requestContext);
			                	callCtxt.put("userLogin", userLogin);
								
			                	if (UtilValidate.isEmpty(isEnableSrOutboundEmail) || isEnableSrOutboundEmail.equals("Y")) {
			                		callResult = dispatcher.runSync("common.sendEmail", callCtxt);
									if (ServiceUtil.isError(callResult)) {
										String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
										return returnError(request, errMsg);
									}
			                	}
			                	
							} else {
								//add condition for email sending other than Inbound 
								if (UtilValidate.isEmpty(isEnableSrOutboundEmail) || isEnableSrOutboundEmail.equals("Y")) {
									if (sendEmail.equals("Y")) {
										callResult = dispatcher.runSync("common.sendEmail", callCtxt);
									}
									else {
										callResult = dispatcher.runSync("common.uploadFile", callCtxt);
									}
									if (ServiceUtil.isError(callResult)) {
										String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
										return returnError(request, errMsg);
									}
								}
							}
							
							Map<String, Object> communicationevntwrkeff = new HashMap<String, Object>();
							communicationevntwrkeff = UtilMisc.toMap("userLogin", userLogin, "communicationEventId",
									communicationEventId, "workEffortId", workEffortId);
							try {
								Map<String, Object> serviceResults = dispatcher.runSync("createCommunicationEventWorkEff",
										communicationevntwrkeff);
							} catch (GenericServiceException e) {
								String errMsg = "Problem caling serviceResults: " + e.toString();
								return returnError(request, errMsg);
							}
						}
						
						if (UtilValidate.isNotEmpty(contactId)) {
							try {
								GenericValue workEffortContact = delegator.makeValue("WorkEffortContact");

								workEffortContact.set("workEffortId", workEffortId);
								workEffortContact.set("partyId", contactId);
								workEffortContact.set("roleTypeId", "CONTACT");
								workEffortContact.set("fromDate", UtilDateTime.nowTimestamp());
								workEffortContact.set("createdByUserLogin", userLogin.getString("userLoginId"));
								workEffortContact.set("thruDate", null);
								delegator.create(workEffortContact);
							} catch (Exception e) {
								//e.printStackTrace();
					    		Debug.logError(e.getMessage(), MODULE);
								TransactionUtil.rollback();
								return returnError(request, e.getMessage());
							}

						}
						
						// associate the activity owner with SR
						if(UtilValidate.isNotEmpty(ownerList)) {
			            	for(String owner : ownerList) {
			            		String ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
			            		String ownerRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, ownerPartyId);
			            		DataHelper.createCustRequestParty(delegator, custRequestId, ownerPartyId, ownerRoleTypeId);
			            	}
			            }
						
						//add workeffortattribute
						if(UtilValidate.isNotEmpty(arrivalWindow)) {
							GenericValue workEffortAttr = EntityQuery.use(delegator).from("WorkEffortAttribute").where("workEffortId",workEffortId,"attrName","TECH_ARRIVAL_WINDOW").queryFirst();
							if(UtilValidate.isNotEmpty(workEffortAttr)) {
								workEffortAttr.set("attrValue", arrivalWindow);
								workEffortAttr.store();
							} else {
								workEffortAttr = delegator.makeValue("WorkEffortAttribute");
								workEffortAttr.set("workEffortId", workEffortId);
								workEffortAttr.set("attrName","TECH_ARRIVAL_WINDOW");
								workEffortAttr.set("attrValue",arrivalWindow);
								workEffortAttr.create();
							}
						}
						if(UtilValidate.isNotEmpty(isSchedulingRequired)) {
							GenericValue workEffortAttr = EntityQuery.use(delegator).from("WorkEffortAttribute").where("workEffortId",workEffortId,"attrName","IS_SCHEDULING_REQUIRED").queryFirst();
							if(UtilValidate.isNotEmpty(workEffortAttr)) {
								workEffortAttr.set("attrValue", isSchedulingRequired);
								workEffortAttr.store();
							} else {
								workEffortAttr = delegator.makeValue("WorkEffortAttribute");
								workEffortAttr.set("workEffortId", workEffortId);
								workEffortAttr.set("attrName","IS_SCHEDULING_REQUIRED");
								workEffortAttr.set("attrValue",isSchedulingRequired);
								workEffortAttr.create();
							}
						}
						if(UtilValidate.isNotEmpty(callStatus)) {
							GenericValue workEffortAttr = EntityQuery.use(delegator).from("WorkEffortAttribute").where("workEffortId",workEffortId,"attrName","CALL_STATUS").queryFirst();
							if(UtilValidate.isEmpty(workEffortAttr)) {
								workEffortAttr = delegator.makeValue("WorkEffortAttribute", UtilMisc.toMap("workEffortId", workEffortId,"attrName","CALL_STATUS"));
							}
							workEffortAttr.set("attrValue",callStatus);
							delegator.createOrStore(workEffortAttr);
							if(callStatus.equals("DND") || callStatus.equals("PHONE_ENGAGED")) {
								DataHelper.updatePhoneCallStatusActivities(workEffortId, callStatus, delegator);
							}
						}
						// Update SR status to assigned if create work order [start]
						if (UtilValidate.isNotEmpty(workEffortTypeId) && workEffortTypeId.equals("Task")
								&& UtilValidate.isNotEmpty(workEffortPurposeTypeId) && workEffortPurposeTypeId.equals("TEST_WORK_TYPE")) {
							if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals("SERVICE_REQUEST")) {
								SrUtil.srStatusChanged(delegator, dispatcher, userLogin, custRequestId, "SR_ASSIGNED");
								
								if (UtilValidate.isNotEmpty(custRequestId)) {
									Map<String, Object> historyInputMap = new HashMap<String, Object>();
									historyInputMap.put("custRequestId", custRequestId);
									historyInputMap.put("userLogin", userLogin);
									
									Map<String, Object> historyOutMap = dispatcher.runSync("srPortal.createSrHistory", historyInputMap);
									//String serviceResult = createSrHistory(request,response);

									if(!ServiceUtil.isSuccess(historyOutMap)) {
										responseMessage = "Problem While Creating Service Request History"; // UtilProperties.getMessage(RESOURCE, "ActivityCreationError", locale);
										return returnError(request, responseMessage);
									}
								}
							}
						}
						// Update SR status to assigned if create work order [end]
						
						/*
						String ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
						if(UtilValidate.isNotEmpty(ownerPartyId)) {
							DataHelper.createCustRequestParty(delegator, custRequestId, ownerPartyId, activityOwnerRole);
						} */
					} else {
						responseMessage = UtilProperties.getMessage(RESOURCE, "ActivityCreationError", locale);
						return returnError(request, responseMessage);
					}
				} catch (Exception e) {
					//e.printStackTrace();
		    		Debug.logError(e.getMessage(), MODULE);

					TransactionUtil.rollback();
					return returnError(request, e.getMessage());
				}
			} else {
				
				String errMsg = "";
                if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
                	errMsg = accessMatrixRes.get("errorMessage").toString();
                } else {
                	errMsg = "Access Denied";
                }
				return returnError(request, errMsg);
			}
		} catch (Exception e) {
			Debug.log("Error : "+e.getMessage(), MODULE);
			return returnError(request, e.getMessage());
		}
		
		return returnSuccess(request, responseMessage);
	}


public static String createNotesAndAttachment(HttpServletRequest request, HttpServletResponse response) {
	Delegator delegator = (Delegator) request.getAttribute("delegator");
    GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
    Locale locale = UtilHttp.getLocale(request);
    LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    Map<String, Object> context = UtilHttp.getCombinedMap(request);
	String noteName = (String) context.get("noteName");
	String noteInfo = (String) context.get("noteInfo");
	String salesOpportunityId = (String) context.get("salesOpportunityId");
	String fileSource = (String) context.get("fileSource");
	String responseMessage = UtilProperties.getMessage(RESOURCE, "NotesAndAttachmentsSuccessfullyCreated", locale);
	File filepath = null;
    try{
    	GenericValue noteDataCreate = delegator.makeValue("NoteData");
		String noteId = delegator.getNextSeqId("NoteData");
        noteId = UtilGenerator.getNoteNumber(delegator, noteId);
        
        noteDataCreate.set("noteId",noteId);
        noteDataCreate.set("noteName", noteName);
        noteDataCreate.set("noteInfo", noteInfo);
        noteDataCreate.set("noteDateTime", UtilDateTime.nowTimestamp());
        noteDataCreate.set("createdByUserLogin", userLogin.getString("userLoginId"));
        noteDataCreate.set("noteParty", userLogin.getString("userLoginId"));
        delegator.create(noteDataCreate);
        
        if (UtilValidate.isNotEmpty(noteId) && UtilValidate.isNotEmpty(salesOpportunityId)) {
			GenericValue salesOpportunityContent = delegator.makeValue("SalesOpportunityContent");
			salesOpportunityContent.set("noteId",noteId);
			salesOpportunityContent.set("salesOpportunityId",salesOpportunityId);
			salesOpportunityContent.set("createdByUserLogin",userLogin.getString("userLoginId"));
			delegator.create(salesOpportunityContent);
        }
        if(UtilValidate.isNotEmpty(noteId) && UtilValidate.isNotEmpty(fileSource) && !(fileSource.equals("Notes"))){
        	String ofbizHome = System.getProperty("ofbiz.home");
			String localPath = null;
			GenericValue systemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "attachments","systemPropertyId","filepath").queryOne();
			if(UtilValidate.isNotEmpty(systemProperty)){
				localPath = systemProperty.getString("systemPropertyValue");
			}	
			String dirPath = ofbizHome + localPath;
			filepath = new File(dirPath);
			File file = null;
			CloseableHttpClient client =null;
			String responseCode = null, filenetResponseMessage = null, docId = null;
			if(ServletFileUpload.isMultipartContent(request)){
				try{
					List<FileItem> multiparts = new ServletFileUpload(
							new DiskFileItemFactory()).parseRequest(request);
					String fname = null;
					String importedFilePath = "";
					String name = "";
					
					String fileUploadUrl = EntityUtilProperties.getPropertyValue("general", "hk.fileUploadUrl", delegator);
					
					if (UtilValidate.isNotEmpty(fileUploadUrl)) { 
						for(FileItem item : multiparts){
							if(!item.isFormField()){
								name = new File(item.getName()).getName();
								file = new File(filepath + File.separator + name);
								if(file.exists())
								{
									file.delete();
								}
								item.write(file);
								try {
									importedFilePath = filepath + File.separator + name;
	    							HttpClientBuilder clientBuilder = HttpClientBuilder.create();
	    							       		
	    					        @SuppressWarnings("deprecation")
									javax.net.ssl.SSLContext sslContext = new SSLContextBuilder()
	    					                .loadTrustMaterial(null, new TrustStrategy() {

												@Override
												public boolean isTrusted(X509Certificate[] arg0, String arg1)
														throws CertificateException {
													return true;
												}
	    					                	
	    					                }
	    					                ).build();

	    					         client = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setSslcontext(sslContext).build();
	    					        
	    							HttpPost postRequest = new HttpPost(fileUploadUrl);
	    							MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
	    							multipartEntityBuilder.addPart("channel", new StringBody("02", ContentType.TEXT_PLAIN));
	    							multipartEntityBuilder.addPart("project_code", new StringBody("ACS", ContentType.TEXT_PLAIN));
	    							multipartEntityBuilder.addPart("doc_class", new StringBody("HK_DC_ACS", ContentType.TEXT_PLAIN));
	    							multipartEntityBuilder.addPart("country_code", new StringBody("HK", ContentType.TEXT_PLAIN));
	    							multipartEntityBuilder.addPart("doc_id", new StringBody("NA", ContentType.TEXT_PLAIN));
	    							multipartEntityBuilder.addPart("doc_name", new StringBody(name, ContentType.TEXT_PLAIN));
	    							multipartEntityBuilder.addPart("doc_size_in_bytes", new StringBody(Long.toString(file.length()), ContentType.TEXT_PLAIN));
	    							multipartEntityBuilder.addPart("action_type", new StringBody("uploadDocument", ContentType.TEXT_PLAIN));
	    							FileBody fileBody = new FileBody(file, ContentType.APPLICATION_OCTET_STREAM, name);
	    							
	    							multipartEntityBuilder.addPart("upload_file", fileBody);
	     							HttpEntity reqEntity = multipartEntityBuilder.build();	        							
	    							postRequest.setEntity(reqEntity);   	        							
	    							HttpResponse fileUploadResponse = client.execute(postRequest);    	        							
	    							String jsonResp = EntityUtils.toString(fileUploadResponse.getEntity());
	    					        
	    							ObjectMapper mapper = new ObjectMapper();
	    							Map<String, String> map = mapper.readValue(jsonResp, Map.class);
	    							for (Map.Entry<String,String> entry : map.entrySet()) {
	    								String key = entry.getKey();
	    								String value = entry.getValue();
	    								if (null != key) {
	    									if (key.equalsIgnoreCase("response_code")) {
	        									responseCode = value;
	        								}
	    									if (key.equalsIgnoreCase("response_message")) {
	        									filenetResponseMessage = value;
	        								}
	    									if (key.equalsIgnoreCase("doc_id")) {
	        									docId = value;     									
	        								}
	    								}      								
	    							}
	    							
	    							if (responseCode != null && responseCode.equalsIgnoreCase("00")) {
	    								Debug.log("Document Upload Sucessfully:::uploadDocument end and docId:"+ docId);
	    							} else {
	    								Debug.log("No success code from upload service");
	    								
	    							}
								} catch (IOException ex) {
									Debug.logError("Error occured while uploading file to DSS server. Error : ", ex.getMessage());
									//ex.printStackTrace();
								} 
								
							}
						}
						
						if(responseCode.equalsIgnoreCase("00") && docId != null && !docId.equals("NA")){
    						GenericValue noteData = delegator.findOne("NoteData",UtilMisc.toMap("noteId",noteId),false);
    						if(UtilValidate.isNotEmpty(noteData)){
    							String moreInfoUrl = "/bootstap/attachments/"+name;
    							noteData.set("noteType", fileSource);
    							noteData.set("moreInfoItemName", name);
    							noteData.set("moreInfoItemId", docId);
    							noteData.store();
    						}
    					}
					} else {
						 request.setAttribute("_ERROR_MESSAGE_", "File Upload Endpoints is Empty");
	    				 return "error";
					 }					
				} catch (Exception ex) {
					request.setAttribute("_ERROR_MESSAGE_", "File Upload Failed due to " + ex);
					return "error";
				} finally {
					if (file != null && file.exists() && responseCode.equalsIgnoreCase("00")) {
						Debug.log("File is deleted once file upload sucess");
						file.delete();
					}						
					if(null != client){
			    		try {
			    			client.close();
						} catch (IOException e) {
							//e.printStackTrace();
				    		Debug.logError(e.getMessage(), MODULE);

						}
			    	}
				}
			}
        }else{
        	GenericValue noteData = delegator.findOne("NoteData",UtilMisc.toMap("noteId",noteId),false);
			if(UtilValidate.isNotEmpty(noteData)){
				noteData.set("noteType", fileSource);
				noteData.store();
			}
        }
    }
    catch (Exception e) {
       // e.printStackTrace();
		Debug.logError(e.getMessage(), MODULE);

        return returnError(request, e.getMessage());
    }
	return returnSuccess(request, responseMessage);
}


@SuppressWarnings("unchecked")
public static String getOwnerTeam(HttpServletRequest request, HttpServletResponse response)
		throws GenericEntityException {
	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
	Map<String, Object> context = UtilHttp.getCombinedMap(request);
	List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
	
	String emplTeamId = (String) context.get("emplTeamId");
	String businessUnitId = (String) context.get("businessUnitId");
	try {
		Map<String, Object> inputMap = new HashMap<String, Object>();
		inputMap.put("emplTeamId", emplTeamId);
		inputMap.put("businessUnitId", businessUnitId);
		inputMap.put("userLogin", userLogin);
		Map<String, Object> res = dispatcher.runSync("salesPortal.getOwnerTeam", inputMap);

		if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("resultMap"))) {
			Map<String, Object> resultMap = (Map<String, Object>) res.get("resultMap");
			results = (List<Map<String, Object>>) resultMap.get("result");
		}else{
			String errMsg = "Problem While Fetching Owner Related Users/Team Details ";
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
	} catch (Exception e) {
		Debug.logInfo("Error-" + e.getMessage(), MODULE);
		request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		return "error";
	}
	return AjaxEvents.doJSONResponse(response, results);
}

// Wrapper Class for setSalesLoginHistory
public static String setSalesLoginHistory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
	HttpSession session = request.getSession();
	
	String seqId = request.getParameter("seqId");
	String entity = request.getParameter("entity");
	
	if (UtilValidate.isEmpty(userLogin)) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("error", "No user login details found!");
		return doJSONResponse(response, data);
	}
	
	try {
		Map<String, Object> inputMap = new HashMap<String, Object>();
		inputMap.put("seqId", seqId);
		inputMap.put("entity", entity);
		inputMap.put("session", session);
		inputMap.put("userLogin", userLogin);
		
		Map<String, Object> res = dispatcher.runSync("salesPortal.setSalesLoginHistory", inputMap);
			
		if(!ServiceUtil.isSuccess(res)) {
			request.setAttribute("_ERROR_MESSAGE_", "Problem While saving Sales Request User Login History");
			return "error";
		}	
		
	} catch (Exception e) {
    	String errMsg = "Problem While Saving Login History Details " + e.toString();
		Debug.logError(e, errMsg, MODULE);
		request.setAttribute("_ERROR_MESSAGE_", errMsg);
		return "error";
	}
	return "success";
}
}