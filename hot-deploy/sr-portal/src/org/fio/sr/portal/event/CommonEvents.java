
package org.fio.sr.portal.event;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

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
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilGenerator;
import org.fio.sr.portal.constant.SrPortalConstant.SrResolutionConstant;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.event.AjaxEvents;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
import org.groupfio.common.portal.util.ResAvailUtil;
import org.groupfio.common.portal.util.SrUtil;
import org.groupfio.common.portal.util.UtilAttribute;
import org.groupfio.common.portal.util.UtilContactMech;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class CommonEvents {
	private static final String MODULE = CommonEvents.class.getName();
	private static final String RESOURCE = "SrPortalUiLabels";
	private static final String SUCCESS = "success";
	private static final String ERROR = "error";
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
		String result = SUCCESS;

		response.setContentType("application/x-json");
		try {
			response.setContentLength(jsonString.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
			Debug.logError(e, "Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(), MODULE);
			response.setContentLength(jsonString.length());
		}

		Writer out;
		try {
			out = response.getWriter();
			out.write(jsonString);
			out.flush();
		} catch (IOException e) {
			Debug.logError(e, "Failed to get response writer", MODULE);
			result = ERROR;
		}
		return result;
	}
	public static String returnError(HttpServletRequest request, String errorMessage) {
		try {
			request.setAttribute("_ERROR_MESSAGE_", "ERROR :" + errorMessage);
			Debug.logError("Error : " + errorMessage, MODULE);
		} catch (Exception e) {
			Debug.logError(e, "Error : " + e.getMessage(), MODULE);
		}
		return ERROR;
	}
	public static String returnSuccess(HttpServletRequest request, String successMessage) {
		try {
			request.setAttribute("_EVENT_MESSAGE_", successMessage);
			Debug.logInfo("Success : " + successMessage, MODULE);
		} catch (Exception e) {
			Debug.logError(e, "Error : " + e.getMessage(), MODULE);
		}
		return SUCCESS;
	}
	public static GenericValue getUserLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (GenericValue) session.getAttribute("userLogin");
	}

	public static String eventCreateSrActivity(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		String workEffortType = (String) requestParameters.get("workEffortTypeId");
		String srTypeId = (String) requestParameters.get("srTypeId");
		String srSubTypeId = (String) requestParameters.get("srSubTypeId");
		Timestamp estimatedCompletionDate = (Timestamp) requestParameters.get("estimatedCompletionDate");
		Timestamp estimatedStartDate = (Timestamp) requestParameters.get("estimatedStartDate");
		Timestamp actualStartDate = (Timestamp) requestParameters.get("actualStartDate");
		Timestamp actualCompletionDate = (Timestamp) requestParameters.get("actualCompletionDate");
		String dateStr = (String) requestParameters.get("taskDate");
		String callDateStr = (String) requestParameters.get("callDateTime");
		String subject = (String) requestParameters.get("subject");
		String resolution = (String) requestParameters.get("resolution");
		String linkedFrom = (String) requestParameters.get("linkedFrom");
		String productId = (String) requestParameters.get("productId");
		String account = (String) requestParameters.get("account");
		String accountProduct = (String) requestParameters.get("accountProduct");
		String onceDone = (String) requestParameters.get("onceDone");
		String messages = (String) requestParameters.get("messages");
		String location = (String) requestParameters.get("location");
		String startTimeStr = (String) requestParameters.get("startTime");
		String duration = (String) requestParameters.get("duration");
		String nsender = (String) requestParameters.get("nsender");
		String nto = (String) requestParameters.get("nto");
		String ncc = (String) requestParameters.get("ncc");
		String nbcc = (String) requestParameters.get("nbcc");
		String template = (String) requestParameters.get("template");
		String direction = (String) requestParameters.get("direction");
		String optionalAttendees = (String) requestParameters.get("optionalAttendees");
		String requiredAttendees = (String) requestParameters.get("requiredAttendees");
		String nrecepient = (String) requestParameters.get("nrecepient");
		String norganizer = (String) requestParameters.get("norganizer");
		String emailFormContent = (String) requestParameters.get("emailFormContent");
		String priority = (String) requestParameters.get("priority");
		String phoneNumber = (String) requestParameters.get("phoneNumber");
		String customerCIN = (String) requestParameters.get("cNo");
		String wftMsdbusinessunit = (String) requestParameters.get("ownerBu");
		String primOwnerId = (String) requestParameters.get("owner");
		String custRequestId = (String) request.getParameter("custRequestId");
		String emplTeamId = (String) request.getParameter("emplTeamId");
		String contactId = (String) request.getParameter("contactId");
		String workEffortPurposeTypeId = (String) request.getParameter("workEffortPurposeTypeId");
		String responseMessage = UtilProperties.getMessage(RESOURCE, "ActivityCreatedSuccessfully", locale);

		try {
			Timestamp date = null;
			if (UtilValidate.isNotEmpty(dateStr)) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
				date = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
			}
			if (UtilValidate.isNotEmpty(callDateStr)) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
				date = new java.sql.Timestamp(sdf.parse(callDateStr).getTime());
			}
			if (UtilValidate.isNotEmpty(startTimeStr)) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
				date = new java.sql.Timestamp(sdf.parse(startTimeStr).getTime());
			}
			Map inMap = FastMap.newInstance();
			Map outMap = FastMap.newInstance();

			if (UtilValidate.isEmpty(messages) || messages == null) {
				if (UtilValidate.isNotEmpty(emailFormContent)) {
					try {
						messages = Base64.getEncoder().encodeToString(emailFormContent.getBytes("utf-8"));
						inMap.put("description", messages);
					} catch (UnsupportedEncodingException e) {
						Debug.logError(e, e.getMessage(), MODULE);
					}
				}
			} else {
				try {
					messages = Base64.getEncoder().encodeToString(messages.getBytes("utf-8"));
					inMap.put("description", messages);
				} catch (UnsupportedEncodingException e) {
					Debug.logError(e, e.getMessage(), MODULE);
				}
			}
			GenericValue workTypeIdentification = null;
			if (UtilValidate.isNotEmpty(workEffortType)) {
				List<EntityCondition> conditionlist = FastList.newInstance();
				conditionlist.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS, workEffortType));
				EntityCondition workTypeCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);

				workTypeIdentification = EntityQuery.use(delegator).from("WorkEffortType")
						.where(workTypeCondition).queryFirst();

			}
			GenericValue userLoginPerson = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId", primOwnerId).queryOne();
			String userLoginPartyId = "";
			if (UtilValidate.isNotEmpty(userLoginPerson)) {
				userLoginPartyId = userLoginPerson.getString("partyId");
			}
			GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", userLoginPartyId).queryList());
			if (UtilValidate.isNotEmpty(primOwnerId)&&UtilValidate.isNotEmpty(emplTeam)) { 
				if (UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId"))){
					emplTeamId = emplTeam.getString("emplTeamId");
				}
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, customerCIN));

				GenericValue partyIdentification = EntityUtil.getFirst(delegator.findList("PartyIdentification", condition,
						null, UtilMisc.toList("-createdStamp"), null, false));
				if(UtilValidate.isNotEmpty(userLogin)) {
					inMap.put("userLogin", userLogin);
				}
				if(UtilValidate.isNotEmpty(emplTeamId)) {
					inMap.put("emplTeamId", emplTeamId);
				}
				if(UtilValidate.isNotEmpty(workTypeIdentification)) {
					inMap.put("workEffortTypeId", workTypeIdentification.getString("workEffortTypeId"));			
				}
				if(UtilValidate.isNotEmpty(srTypeId)) {
					inMap.put("workEffortServiceType", srTypeId);
				}
				if(UtilValidate.isNotEmpty(srSubTypeId)) {
					inMap.put("workEffortSubServiceType", srSubTypeId);
				}
				if(UtilValidate.isNotEmpty(date)) {
					inMap.put("estimatedStartDate", date);
				}
				if(UtilValidate.isNotEmpty(resolution)) {
					inMap.put("resolution", resolution);
				}
				if(UtilValidate.isNotEmpty(onceDone)) {
					inMap.put("wfOnceDone", onceDone);
				}
				if(UtilValidate.isNotEmpty(messages)) {
					inMap.put("message", messages);
				}	
				if (UtilValidate.isNotEmpty(nsender)) {
					inMap.put("nsender", UtilMisc.toList(nsender));
				}
				if (UtilValidate.isNotEmpty(nto)) {
					inMap.put("nto", UtilMisc.toList(nto));
				}
				if (UtilValidate.isNotEmpty(ncc)) {
					inMap.put("ncc", UtilMisc.toList(ncc));
				}
				if (UtilValidate.isNotEmpty(nbcc)) {
					inMap.put("nbcc", UtilMisc.toList(nbcc));
				}
				if (UtilValidate.isNotEmpty(template)) {
					inMap.put("wftMsdsubjecttemplate", template);
				}
				if (UtilValidate.isNotEmpty(direction)) {
					inMap.put("direction", direction);
				}
				if (UtilValidate.isNotEmpty(phoneNumber)) {
					inMap.put("phoneNumber", phoneNumber);
				}
				if (UtilValidate.isNotEmpty(priority)) {
					inMap.put("priority", Long.valueOf(priority));
				}
				if (UtilValidate.isNotEmpty(optionalAttendees)) {
					inMap.put("noptional", UtilMisc.toList(optionalAttendees));
				}
				if (UtilValidate.isNotEmpty(requiredAttendees)) {
					inMap.put("nrequired", UtilMisc.toList(requiredAttendees));
				}
				if (UtilValidate.isNotEmpty(norganizer)) {
					inMap.put("norganizer", UtilMisc.toList(norganizer));
				}
				if (UtilValidate.isNotEmpty(nrecepient)) {
					inMap.put("nrecipient", UtilMisc.toList(nrecepient));
				}
				if (UtilValidate.isNotEmpty(location)) {
					inMap.put("wftLocation", location);
				}
				if (UtilValidate.isNotEmpty(duration)) {
					inMap.put("wftMsdduration", duration);
				}
				if(UtilValidate.isNotEmpty(subject)) {
					inMap.put("workEffortName", subject);
				}
				inMap.put("endPointType", "OFCRM");
				String partyId = null;
				if (UtilValidate.isNotEmpty(partyIdentification)) {
					partyId = partyIdentification.getString("partyId");
				}
				if(UtilValidate.isNotEmpty(customerCIN)){
					inMap.put("partyId", customerCIN);
				}else if (UtilValidate.isNotEmpty(partyId)) {
					inMap.put("partyId", partyId);
				}

				/*EntityCondition rolecondition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "_NA_"));

			GenericValue roleIdentification = EntityUtil
					.getFirst(delegator.findList("PartyRole", rolecondition, null, null, null, false));
			String roleTypeId = null;
			if (UtilValidate.isNotEmpty(roleIdentification)) {
				roleTypeId = roleIdentification.getString("roleTypeId");
			}*/

				GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
				String roleTypeId = party.getString("roleTypeId");

				if (UtilValidate.isNotEmpty(roleTypeId)) {
					if ("CUSTOMER".equalsIgnoreCase(roleTypeId)) {
						inMap.put("roleTypeId", "CUSTOMER");
					}else if ("PROSPECT".equalsIgnoreCase(roleTypeId)) {
						inMap.put("roleTypeId", "02");
					}else if ("NON_CRM".equalsIgnoreCase(roleTypeId)) {
						inMap.put("roleTypeId", "07");
					}
				}
				if (UtilValidate.isNotEmpty(wftMsdbusinessunit)) {
					inMap.put("wftMsdbusinessunit", wftMsdbusinessunit);
					inMap.put("businessUnitId", wftMsdbusinessunit);
				}
				if (UtilValidate.isNotEmpty(primOwnerId)) {
					inMap.put("primOwnerId", primOwnerId);
				}
				if (UtilValidate.isNotEmpty(primOwnerId)) {
					inMap.put("wfUserLoginId", primOwnerId);
				}
				if(UtilValidate.isNotEmpty(workEffortPurposeTypeId)){
					inMap.put("workEffortPurposeTypeId", workEffortPurposeTypeId);
				}
				outMap = dispatcher.runSync("crmPortal.createInteractiveActivity", inMap);

				if(!ServiceUtil.isSuccess(outMap)) {
					request.setAttribute("_ERROR_MESSAGE_", "Problem While Creating Activity");
					return "error";
				}
				String workEffortId = (String) outMap.get("workEffortId");
				if(UtilValidate.isEmpty(workEffortId) || workEffortId == null){
					String errMsg = "Problem While Creating Activity ";
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					return "error";
				}else{
					if (UtilValidate.isNotEmpty(custRequestId)) {
						GenericValue custRequestWorkEffort = delegator.makeValue("CustRequestWorkEffort");
						custRequestWorkEffort.set("workEffortId", workEffortId);
						custRequestWorkEffort.set("custRequestId", custRequestId);
						custRequestWorkEffort.create();
						request.setAttribute("srNumber", custRequestId);
					}
					if(UtilValidate.isNotEmpty(contactId)){
						try{
							GenericValue workEffortContact = delegator.makeValue("WorkEffortContact");

							workEffortContact.set("workEffortId",workEffortId);
							workEffortContact.set("partyId", contactId);
							workEffortContact.set("roleTypeId", "CONTACT");
							workEffortContact.set("fromDate", UtilDateTime.nowTimestamp());
							workEffortContact.set("createdByUserLogin", userLogin.getString("userLoginId"));
							workEffortContact.set("thruDate",null);
							delegator.create(workEffortContact);
						}
						catch(Exception e){

							Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
							TransactionUtil.rollback();
							return returnError(request, e.getMessage());
						}

					}
					request.setAttribute("_EVENT_MESSAGE_", "Activity Created Successfully"+": "+workEffortId);
				}
			}
		}
		catch (Exception e) {
			String errMsg = "Problem While Creating Service Request " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			return ERROR;
		}
		return SUCCESS;


	}
	public static String updateActivityEvent(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String workEffortTypeId = (String) context.get("workEffortTypeId");
		String workEffortId = (String) context.get("workEffortId");
		String srTypeId = (String) context.get("srTypeId");
		String srSubTypeId = (String) context.get("srSubTypeId");
		
		String estimatedStartDate = (String) context.get("estimatedStartDate");
		String estimatedCompletionDate = (String) context.get("estimatedCompletionDate");
		String actualStartDate = (String) context.get("actualStartDate");
		String actualCompletionDate = (String) context.get("actualCompletionDate");
		
		/*String actualStartDateStr = (String) requestParameters.get("actualStartDate");
		String actualCompletionDateStr = (String) requestParameters.get("actualCompletionDate");
		String estimatedStartDateStr = (String) requestParameters.get("estimatedStartDate");
		String estimatedCompletionDate = (String) requestParameters.get("estimatedCompletionDate");*/
		
		String callDateTime = (String) context.get("callDateTime");
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
		String startTimeStr = (String) context.get("startTime");
		String duration = (String) context.get("duration");
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		//String owner = (String) requestParameters.get("owner");
		String emplTeamId = (String) request.getParameter("emplTeamId");
		String currentStatusId = (String) request.getParameter("statusId");
		String entityTimeZoneId = (String) context.get("timeZoneDesc");
		String ownerBu = (String) context.get("ownerBu");
		String customerCIN = (String) context.get("cNo");
		String priority = (String) context.get("priority");
		String direction = (String) context.get("direction");
		String phoneNumber = (String) context.get("phoneNumber");
		String nsender = (String) context.get("nsender");
		String nto = (String) context.get("nto");
		String ncc = (String) context.get("ncc");
		String nbcc = (String) context.get("nbcc");
		String template = (String) context.get("template");
		String workEffortPurposeTypeId = (String) context.get("workEffortPurposeTypeId");
		String ownerBookedCalSlots = (String) context.get("ownerBookedCalSlots");
		//String optionalAttendees = (String) requestParameters.get("optionalAttendees");
		//String requiredAttendees = (String) requestParameters.get("requiredAttendees");
		List<String> optionalAttendees = null;
		List<String> requiredAttendees = null;

		String optionalAttendeesClassName= "";
		String requiredAttendeesClassName = "";

		if (UtilValidate.isNotEmpty(context.get("optionalAttendees"))){
			optionalAttendeesClassName = context.get("optionalAttendees").getClass().getName();
		}
		if (UtilValidate.isNotEmpty(context.get("requiredAttendees"))){
			requiredAttendeesClassName = context.get("requiredAttendees").getClass().getName();
		}

		if (UtilValidate.isNotEmpty(optionalAttendeesClassName) && "java.lang.String".equals(optionalAttendeesClassName)) {
			optionalAttendees = UtilMisc.toList((String) context.get("optionalAttendees"));
		}else if(UtilValidate.isNotEmpty(optionalAttendeesClassName) && "java.util.LinkedList".equals(optionalAttendeesClassName) ){
			optionalAttendees = (List<String>) context.get("optionalAttendees");
		}

		if (UtilValidate.isNotEmpty(requiredAttendeesClassName) && "java.lang.String".equals(requiredAttendeesClassName)) {
			requiredAttendees = UtilMisc.toList((String) context.get("requiredAttendees"));
		}else if(UtilValidate.isNotEmpty(requiredAttendeesClassName) && "java.util.LinkedList".equals(requiredAttendeesClassName) ){
			requiredAttendees = (List<String>) context.get("requiredAttendees");
		}

		String nrecepient = (String) context.get("nrecepient");
		String norganizer = (String) context.get("norganizer");
		String isPhoneCall = (String) context.get("isPhoneCall");
		String domainEntityType = (String) context.get("domainEntityType");
		String domainEntityId = (String) context.get("domainEntityId");
		String communicationEventId = (String) context.get("communicationEventId");
		String emailContent = (String) context.get("emailContent");
		boolean existing = ((communicationEventId == null) || communicationEventId.equals("") ? false : true);
		String contactId = (String) request.getParameter("contactId");
		String responseMessage = UtilProperties.getMessage(RESOURCE, "ActivityUpdatedSuccessfully", locale);
		
		if (UtilValidate.isNotEmpty(context.get("estimatedStartDate_date"))) {
			estimatedStartDate = (String) context.get("estimatedStartDate_date");
		}
		if (UtilValidate.isNotEmpty(context.get("estimatedCompletionDate_date"))) {
			estimatedCompletionDate = (String) context.get("estimatedCompletionDate_date");
		}
		if (UtilValidate.isNotEmpty(context.get("actualStartDate_date"))) {
			actualStartDate = (String) context.get("actualStartDate_date");
		}
		if (UtilValidate.isNotEmpty(context.get("actualCompletionDate_date"))) {
			actualCompletionDate = (String) context.get("actualCompletionDate_date");
		}
		
		String estimatedStartTime = (String) context.get("estimatedStartDate_time");
		String estimatedCompletionTime = (String) context.get("estimatedCompletionDate_time");
		String actualStartTime = (String) context.get("actualStartDate_time");
		String actualCompletionTime = (String) context.get("actualCompletionDate_time");
		
		String isSchedulingRequired = (String) context.get("isSchedulingRequired");
		String createdDateStr = (String) context.get("createdDate");
		
		List<String> ownerList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(context.get("owner")) && context.get("owner") instanceof String) {
			String owner1 = (String) context.get("owner");
			if(UtilValidate.isNotEmpty(owner1)) ownerList.add(owner1);
		} else if(UtilValidate.isNotEmpty(context.get("owner")) && context.get("owner") instanceof List<?>) {
			ownerList = new ArrayList<>(
				      new HashSet<>((List<String>) context.get("owner")));
		}
		String arrivalWindow = (String) context.get("arrivalWindow");
		
		try {
			
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
			
			Map<String, Object> inMap = new HashMap<String, Object>();
			if(UtilValidate.isNotEmpty(workEffortId)){
				
				String userLoginId = userLogin.getString("userLoginId");
				String accessLevel = "Y";
				String businessUnit = null;
				String teamId = "";

				Map<String, Object> workEffortData = DataUtil.getWorkEffortDetail(delegator, workEffortId);
				if(UtilValidate.isNotEmpty(workEffortData)) {
					businessUnit = UtilValidate.isNotEmpty(workEffortData.get("businessUnit")) ? (String) workEffortData.get("businessUnit") : "";
					teamId = UtilValidate.isNotEmpty(workEffortData.get("teamId")) ? (String) workEffortData.get("teamId") : "";
				}
				Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
				if (UtilValidate.isNotEmpty(userLoginId)) {
					String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
					if(UtilValidate.isEmpty(businessUnit))
						businessUnit = DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
					Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
					accessMatrixMap.put("delegator", delegator);
					accessMatrixMap.put("dispatcher", dispatcher);
					accessMatrixMap.put("teamId", teamId);
					accessMatrixMap.put("businessUnit", businessUnit);
					accessMatrixMap.put("modeOfOp", "Update");
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
							Map<String, Object> buTeamData = DataUtil.getUserBuTeam(delegator, currentPartyId);
							businessUnit = (String) buTeamData.get("businessUnit");
							teamId = (String) buTeamData.get("emplTeamId");

							List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
							//change the access in the create 
							//check with ownerId
							if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
								@SuppressWarnings("unchecked")
								List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
								if(!ownerIds.contains(teamId)) accessLevel = null;
								conditionList.add(EntityCondition.makeCondition("ownerId", EntityOperator.IN, ownerIds));
							}

							//check with emplTeamId
							if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
								@SuppressWarnings("unchecked")
								List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
								if(!emplTeamIds.contains(teamId)) accessLevel = null;
								conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
							}

							conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
							DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
							dynamicViewEntity.addMemberEntity("WE", "WorkEffort");
							dynamicViewEntity.addAlias("WE", "workEffortId");
							dynamicViewEntity.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
							dynamicViewEntity.addAlias("WEPA", "ownerId");
							dynamicViewEntity.addAlias("WEPA", "emplTeamId");
							dynamicViewEntity.addViewLink("WE", "WEPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("workEffortId"));

							EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							GenericValue activity = EntityQuery.use(delegator).from(dynamicViewEntity).where(mainConditons).queryFirst();
							if(UtilValidate.isEmpty(activity)) accessLevel=null;

							if(accessLevel == null) break;
						}
					}
					
				}
				if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
					
					estimatedStartTime = UtilValidate.isNotEmpty(estimatedStartTime) ? estimatedStartTime : "00:00";
					estimatedCompletionTime = UtilValidate.isNotEmpty(estimatedCompletionTime) ? estimatedCompletionTime : "00:00";

					Timestamp estimatedStartDateTime = ParamUtil.getTimestamp(estimatedStartDate, estimatedStartTime, "yyyy-MM-dd HH:mm");
					Timestamp estimatedCompletionDateTime = ParamUtil.getTimestamp(estimatedCompletionDate, estimatedCompletionTime, "yyyy-MM-dd HH:mm");
					Timestamp actualStartDateTime = ParamUtil.getTimestamp(actualStartDate, actualStartTime, "yyyy-MM-dd HH:mm");
					Timestamp actualCompletionDateTime = ParamUtil.getTimestamp(actualCompletionDate, actualCompletionTime, "yyyy-MM-dd HH:mm");
					
					/*if (UtilValidate.isNotEmpty(estimatedStartDateTime)) {
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
					}*/
					
					if (UtilValidate.isEmpty(messages) || messages == null) {
						if (UtilValidate.isNotEmpty(emailFormContent)) {
							try {
								messages = Base64.getEncoder().encodeToString(emailFormContent.getBytes("utf-8"));
								inMap.put("description", messages);
							} catch (UnsupportedEncodingException e) {
								Debug.logError(e, e.getMessage(), MODULE);
							}
						}
					} else {
						try {
							messages = Base64.getEncoder().encodeToString(messages.getBytes("utf-8"));
							inMap.put("description", messages);
						} catch (UnsupportedEncodingException e) {
							Debug.logError(e, e.getMessage(), MODULE);
						}
					}
					
					if (UtilValidate.isNotEmpty(workEffortTypeId)) {
						List<EntityCondition> conditionlist = FastList.newInstance();
						conditionlist.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS, workEffortTypeId));
						EntityCondition workTypeCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
						GenericValue workTypeIdentification = EntityQuery.use(delegator).from("WorkEffortType").where(workTypeCondition).queryFirst();
						if(UtilValidate.isNotEmpty(workTypeIdentification)) {
							workEffortTypeId = workTypeIdentification.getString("workEffortTypeId");
							inMap.put("workEffortTypeId", workEffortTypeId);			
						}
					}

					EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND, EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, customerCIN));

					GenericValue partyIdentification = EntityUtil.getFirst(delegator.findList("PartyIdentification", condition, null, UtilMisc.toList("-createdStamp"), null, false));

					inMap.put("userLogin",userLogin);
					
					if(UtilValidate.isNotEmpty(emplTeamId)) {
						inMap.put("emplTeamId", emplTeamId);
					}
					if(UtilValidate.isNotEmpty(workEffortId)) {
						inMap.put("externalId", workEffortId);
					}
					if(UtilValidate.isNotEmpty(srTypeId)) {
						inMap.put("workEffortServiceType", srTypeId);
					}
					if(UtilValidate.isNotEmpty(srSubTypeId)) {
						inMap.put("workEffortSubServiceType", srSubTypeId);
					}
					if (UtilValidate.isNotEmpty(direction)) {
						inMap.put("direction", direction);
					}
					if(UtilValidate.isNotEmpty(subject)) {
						inMap.put("workEffortName", subject);
					}
					if(UtilValidate.isNotEmpty(currentStatusId)) {
						inMap.put("currentStatusId", currentStatusId);
					}
					
					if(UtilValidate.isNotEmpty(estimatedStartDateTime)) {
						inMap.put("estimatedStartDate", estimatedStartDateTime);
					}
					if(UtilValidate.isNotEmpty(estimatedCompletionDateTime)) {
						inMap.put("estimatedCompletionDate", estimatedCompletionDateTime);
					}
					if(UtilValidate.isNotEmpty(actualStartDateTime)) {
						inMap.put("actualStartDate", actualStartDateTime);
					}
					if(UtilValidate.isNotEmpty(actualCompletionDateTime)) {
						inMap.put("actualCompletionDate", actualCompletionDateTime);
					}
					
					if (UtilValidate.isNotEmpty(optionalAttendees)) {
						inMap.put("noptional", UtilMisc.toList(optionalAttendees));
					}
					if (UtilValidate.isNotEmpty(requiredAttendees)) {
						inMap.put("nrequired", UtilMisc.toList(requiredAttendees));
					}
					if (UtilValidate.isNotEmpty(norganizer)) {
						inMap.put("norganizer", UtilMisc.toList(norganizer));
					}
					if (UtilValidate.isNotEmpty(nrecepient)) {
						inMap.put("nrecipient", UtilMisc.toList(nrecepient));
					}
					//inMap.put("isPhoneCall", isPhoneCall);
					if (UtilValidate.isNotEmpty(phoneNumber)) {
						inMap.put("phoneNumber", phoneNumber);
					}
					if(UtilValidate.isNotEmpty(resolution)) {
						inMap.put("resolution", resolution);
					}
					if (UtilValidate.isNotEmpty(priority)) {
						inMap.put("priority", Long.valueOf(priority));
					}
					if(UtilValidate.isNotEmpty(onceDone)) {
						inMap.put("wfOnceDone", onceDone);
					}
					if (UtilValidate.isNotEmpty(nsender)) {
						inMap.put("nsender", UtilMisc.toList(nsender));
					}
					if (UtilValidate.isNotEmpty(nto)) {
						inMap.put("nto", UtilMisc.toList(nto));
					}
					if (UtilValidate.isNotEmpty(ncc)) {
						inMap.put("ncc", UtilMisc.toList(ncc));
					}
					if (UtilValidate.isNotEmpty(nbcc)) {
						inMap.put("nbcc", UtilMisc.toList(nbcc));
					}
					if (UtilValidate.isNotEmpty(template)) {
						inMap.put("wftMsdsubjecttemplate", template);
					}
					if(UtilValidate.isNotEmpty(messages)) {
						inMap.put("message", messages);
					}
					
					if (UtilValidate.isNotEmpty(ownerBu)) {
						inMap.put("wftMsdbusinessunit", ownerBu);
						inMap.put("businessUnitId", ownerBu);
					}
					if(UtilValidate.isNotEmpty(location)){
						inMap.put("wftLocation",location);
					}
					if(UtilValidate.isNotEmpty(duration)){
						inMap.put("wftMsdduration",duration);
					}
					if(UtilValidate.isNotEmpty(entityTimeZoneId)){
						inMap.put("entityTimeZoneId",entityTimeZoneId);
					}
					inMap.put("endPointType", "OFCRM");
					String partyId = null;
					if (UtilValidate.isNotEmpty(partyIdentification)) {
						partyId = partyIdentification.getString("partyId");
					}
					if(UtilValidate.isNotEmpty(customerCIN)){
						inMap.put("partyId", customerCIN);
						if (UtilValidate.isEmpty(partyId)) {
							partyId = customerCIN;
						}
					}else if (UtilValidate.isNotEmpty(partyId)) {
						inMap.put("partyId", partyId);
					}
					if (UtilValidate.isNotEmpty(salesOpportunityId)) {
						inMap.put("salesOpportunityId", salesOpportunityId);
					}
					
					Timestamp createdDate = ParamUtil.getTimestamp(createdDateStr, "yyyy-MM-dd");
					inMap.put("createdDate", createdDate);

					/*EntityCondition rolecondition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
							EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "_NA_"));

					GenericValue roleIdentification = EntityUtil.getFirst(delegator.findList("PartyRole", rolecondition, null, null, null, false));
					String roleTypeId = null;
					if (UtilValidate.isNotEmpty(roleIdentification)) {
						roleTypeId = roleIdentification.getString("roleTypeId");
					}*/

					if (UtilValidate.isNotEmpty(partyId)) {
						GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
						String roleTypeId = party.getString("roleTypeId");

						if (UtilValidate.isNotEmpty(roleTypeId)) {
							if ("CUSTOMER".equalsIgnoreCase(roleTypeId)) {
								inMap.put("roleTypeId", "01");
							}else if ("PROSPECT".equalsIgnoreCase(roleTypeId)) {
								inMap.put("roleTypeId", "02");
							}else if ("NON_CRM".equalsIgnoreCase(roleTypeId)) {
								inMap.put("roleTypeId", "07");
							} else {
								inMap.put("roleTypeId", roleTypeId);
							}
						}
					}
					
					inMap.put("endPointType", "OFCRM");
					inMap.put("domainEntityType", domainEntityType);
					inMap.put("domainEntityId", domainEntityId);
					if(UtilValidate.isNotEmpty(onceDone) && "Y".equals(onceDone)){
						inMap.put("currentStatusId", "IA_MCOMPLETED");
					}
					/*
					String activityOwnerRole = DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
					if(UtilValidate.isNotEmpty(activityOwnerRole)) {
						List<String> ownerRoles = new ArrayList<>();
						if(UtilValidate.isNotEmpty(owner) && UtilValidate.isNotEmpty(activityOwnerRole) && activityOwnerRole.contains(",")) {
							ownerRoles = org.fio.admin.portal.util.DataUtil.stringToList(activityOwnerRole, ",");
						} else
							ownerRoles.add(activityOwnerRole);

						String owenerPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, owner);

						EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,owenerPartyId),
								EntityCondition.makeCondition("roleTypeId", EntityOperator.IN,ownerRoles)
								);
						GenericValue partyRole = EntityQuery.use(delegator).from("PartyRole").where(condition1).queryFirst();
						if(UtilValidate.isNotEmpty(partyRole)) {
							activityOwnerRole = partyRole.getString("roleTypeId");
						} else {
							activityOwnerRole = "";
						}
						inMap.put("ownerRoleTypeId", activityOwnerRole);
					} 
					*/
					inMap.put("ownerList", ownerList);
					if(UtilValidate.isNotEmpty(workEffortPurposeTypeId)){
						inMap.put("workEffortPurposeTypeId", workEffortPurposeTypeId);
					}
					inMap.put("ownerBookedCalSlots", ownerBookedCalSlots);
					inMap.put("isSchedulingRequired", isSchedulingRequired);
					
					Map<String, Object> res = dispatcher.runSync("crmPortal.updateInteractiveActivity", inMap); //dispatcher.runSync("srPortal.UpdateActivity", inputMap);
					
					Debug.log("res----------"+res);
					Map<String, Object> tmpResult = null;
					String extContactId="";
					if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("workEffortId"))) {

						GenericValue updateContactRecords = EntityUtil.getFirst(delegator.findByAnd("WorkEffortContact", UtilMisc.toMap("workEffortId", workEffortId,"thruDate",null), null, false));
						if(UtilValidate.isNotEmpty(updateContactRecords)) {
							extContactId=updateContactRecords.getString("partyId");

							Debug.log("updateContactRecords============="+updateContactRecords+contactId);
							if(UtilValidate.isNotEmpty(extContactId)&&UtilValidate.isNotEmpty(contactId)&& !extContactId.equals(contactId)){
								updateContactRecords.put("thruDate",UtilDateTime.nowTimestamp());
								updateContactRecords.store();
								GenericValue workEffortContact = delegator.makeValue("WorkEffortContact");
	
								workEffortContact.set("workEffortId",workEffortId);
								workEffortContact.set("partyId", contactId);
								workEffortContact.set("roleTypeId", "CONTACT");
								workEffortContact.set("fromDate", UtilDateTime.nowTimestamp());
								workEffortContact.set("createdByUserLogin", userLogin.getString("userLoginId"));
								workEffortContact.set("thruDate",null);
								delegator.create(workEffortContact);
								Debug.log("updateContactRecords============="+workEffortContact+contactId);
							} else if(UtilValidate.isEmpty(extContactId)) {
								GenericValue workEffortContact = delegator.makeValue("WorkEffortContact");
								workEffortContact.set("workEffortId",workEffortId);
								workEffortContact.set("partyId", contactId);
								workEffortContact.set("roleTypeId", "CONTACT");
								workEffortContact.set("fromDate", UtilDateTime.nowTimestamp());
								workEffortContact.set("createdByUserLogin", userLogin.getString("userLoginId"));
								workEffortContact.set("thruDate",null);
								delegator.create(workEffortContact);
							}
						}
						
						//associate the activity owner with SR
						if(UtilValidate.isNotEmpty(ownerList)) {
							GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId,"domainEntityType","SERVICE_REQUEST").queryFirst();
							String custRequestId = UtilValidate.isNotEmpty(workEffort)? workEffort.getString("domainEntityId") : "";
							if(UtilValidate.isNotEmpty(custRequestId)) {
								org.fio.sr.portal.DataHelper.reAssignOwnerCustRequestParty(delegator, userLogin, custRequestId, ownerList, workEffortId);
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
						if (UtilValidate.isNotEmpty(isSchedulingRequired) && isSchedulingRequired.equals("N")) {
							delegator.removeByAnd("WorkEffortAttribute", UtilMisc.toMap("workEffortId", workEffortId, "attrName", "TECH_ARRIVAL_WINDOW"));
							delegator.removeByAnd("ResourceAvailability", UtilMisc.toMap("domainEntityId", workEffortId, "domainEntityType", "ACTIVITY"));
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
						//Remove ResourceAvailability
						if ("IA_CANCEL".equals(currentStatusId)) {
							delegator.removeByAnd("ResourceAvailability", UtilMisc.toMap("domainEntityId", workEffortId, "domainEntityType", "ACTIVITY"));
						}
						
						request.setAttribute("_EVENT_MESSAGE_", "Successfully updated.");
						request.setAttribute("workEffortId", res.get("workEffortId"));
					} else {
						request.setAttribute("_ERROR_MESSAGE_", "Update failed!");
						return ERROR;

					}
				} else {
					String errMsg = "";
					if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
						errMsg = accessMatrixRes.get("errorMessage").toString();
					} else {
						errMsg = "Access Denied";
					}
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					return ERROR;
				}
			}else{
				String errMsg = "Please check Activity id is Missing in Dyna";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return ERROR;

			}
		} catch (Exception e) {
			String errMsg = "Problem While Updating Activity " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			Debug.logError(e, e.getMessage(), MODULE);
			return ERROR;
		}
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public static String addServiceRequestEvent(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String fromPartyId = (String) requestParameters.get("fromPartyId");
		String cNo = (String) requestParameters.get("cNo");
		String custRequestTypeId = (String) requestParameters.get("srTypeId");
		String custRequestCategoryId =  (String) requestParameters.get("srCategoryId");
		String custRequestSubCategoryId = (String) requestParameters.get("srSubCategoryId");
		String custRequestOthCategoryId = (String) requestParameters.get("otherSrSubCategoryId");
		String priorityStr = (String) requestParameters.get("priority");
		String accountType = (String) requestParameters.get("accountType");
		String custReqOnceDone = (String) requestParameters.get("onceAndDone");
		String statusId = (String) requestParameters.get("srStatusId");
		String subStatusId = (String) requestParameters.get("srSubStatusId");
		String owner = (String) requestParameters.get("owner");
		String ownerBu = (String) requestParameters.get("ownerBu");
		String custReqSrSource = (String) requestParameters.get("srSource");
		String accountNumber = (String) requestParameters.get("accountNumber");
		String description = (String) requestParameters.get("description");
		String resolution = (String) requestParameters.get("resolution");
		String notes = (String) requestParameters.get("notes");
		String parentCustRequestId = (String) requestParameters.get("parentCustRequestId");
		//String primaryContactId = (String) requestParameters.get("ContactID");
		
		String alternateId = (String) requestParameters.get("alternateId");
		String nationalId = (String) requestParameters.get("nationalId");
		String alternateIdentificationTypeId = (String) requestParameters.get("alternateIdentificationTypeId");
		String nationalIdentificationTypeId = (String) requestParameters.get("nationalIdentificationTypeId");
		String custOrderId = (String) requestParameters.get("orderId");
		String custRequestName = (String) requestParameters.get("srName");
		String custReqDocumentNum = (String) requestParameters.get("sourceDocumentId");
		String externalLoginKey = (String) requestParameters.get("externalLoginKey");
		String clientPortal = (String) requestParameters.get("clientPortal");
		String customerId = (String) requestParameters.get("customerId");
		String contractorId = (String) requestParameters.get("contractorId");
		String primary = (String) requestParameters.get("primary");
		String salesPerson = (String) requestParameters.get("salesPerson");
		String loggedInUserId = (String) requestParameters.get("loggedInUserId");
		String externalId = (String) requestParameters.get("srNumber");
		String copyFlag = (String) requestParameters.get("copyFlag");
		String homeType = (String) requestParameters.get("homeType");
		String primaryTechnician = (String) requestParameters.get("primaryTechnician");
		
		//Home Owner Contact Info
		String homePhoneNumberParam = (String) requestParameters.get("homePhoneNumber");
		String offPhoneNumberParam = (String) requestParameters.get("offPhoneNumber");;
		String mobilePhoneNumberParam = (String) requestParameters.get("mobilePhoneNumber");
		String customerPrimaryEmailParam = (String) requestParameters.get("customerPrimaryEmail");
		
		//Contractor Contact Info
		String contractorHomeNumberParam = (String) requestParameters.get("contractorHomeNumber");
		String contractorOffNumberParam = (String) requestParameters.get("contractorOffNumber");
		String contractorMobileNumberParam = (String) requestParameters.get("contractorMobileNumber");
		String contractorPrimaryEmailParam = (String) requestParameters.get("contractorPrimaryEmail");
		
		//Attribute Info
		String serviceFee = (String) requestParameters.get("serviceFee");;
		String preFinishPlus = (String) requestParameters.get("preFinishPlus");
		//String finishType = (String) requestParameters.get("finishType");
		String vendorCode = (String) requestParameters.get("vendorCode");
		String location = (String) requestParameters.get("location");
		
		String soldByLocation = (String) requestParameters.get("soldByLocation");
		
		String finishType = (String) requestParameters.get("finishType");
		String finishColor = (String) requestParameters.get("finishColor");
		String materialCategory = (String) requestParameters.get("materialCategory");
		String materialSubCategory = (String) requestParameters.get("materialSubCategory");
		
		String actualResolution = (String) requestParameters.get("actualResolution");
		
		String coordinatorDesc = (String) requestParameters.get("coordinatorDesc");
		
		String inspectedBy = (String) requestParameters.get("inspectedBy");
		String inspectionDate = (String) requestParameters.get("inspectionDate");
		String installedSquare = (String) requestParameters.get("installedSquare");
		String hasAlarmSystem = (String) requestParameters.get("hasAlarmSystem");
		String installed = (String) requestParameters.get("installed");
		
		String dealerRefNo = (String) requestParameters.get("dealerRefNo");
		String workEffortId = (String) requestParameters.get("workEffortId");
		
		String isProgramTemplate = (String) requestParameters.get("isProgramTemplate");
		String programTemplateId = (String) requestParameters.get("programTemplateId");
		
		Debug.log("===== owner ======"+owner);
		
		String addressContactMechId = (String) requestParameters.get("homeOwnerAddress");
		
		List<String> reasonCodeList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(requestParameters.get("reasonCode")) && requestParameters.get("reasonCode") instanceof String) {
			String reasonCode = (String) requestParameters.get("reasonCode");
			if(UtilValidate.isNotEmpty(reasonCode) && reasonCode.contains(",")) {
				reasonCodeList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(reasonCode, ","));
			} else if(UtilValidate.isNotEmpty(reasonCode)) reasonCodeList.add(reasonCode);
		} else if(UtilValidate.isNotEmpty(requestParameters.get("reasonCode")) && requestParameters.get("reasonCode") instanceof List<?>) {
			reasonCodeList = (List<String>) requestParameters.get("reasonCode");
		}
		
		List<String> causeCategoryList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(requestParameters.get("causeCategory")) && requestParameters.get("causeCategory") instanceof String) {
			String causeCategory = (String) requestParameters.get("causeCategory");
			if(UtilValidate.isNotEmpty(causeCategory) && causeCategory.contains(",")) {
				causeCategoryList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(causeCategory, ","));
			} else if(UtilValidate.isNotEmpty(causeCategory)) causeCategoryList.add(causeCategory);
		} else if(UtilValidate.isNotEmpty(requestParameters.get("causeCategory")) && requestParameters.get("causeCategory") instanceof List<?>) {
			causeCategoryList = (List<String>) requestParameters.get("causeCategory");
		}
		
		List<String> primContactList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(requestParameters.get("ContactID")) && requestParameters.get("ContactID") instanceof String) {
			String primConId = (String) requestParameters.get("ContactID");
			if(UtilValidate.isNotEmpty(primConId) && primConId.contains(",")) {
				primContactList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(primConId, ","));
			} else if(UtilValidate.isNotEmpty(primConId)) primContactList.add(primConId);
		} else if(UtilValidate.isNotEmpty(requestParameters.get("ContactID")) && requestParameters.get("ContactID") instanceof List<?>) {
			primContactList = (List<String>) requestParameters.get("ContactID");
		}
		
		
		List<String> optionalAttendees = null;
		String optionalAttendeesClassName = "";
		String optionalAttendeesEmailIds = "";
		
		if (UtilValidate.isNotEmpty(requestParameters.get("optionalAttendees"))) {
			optionalAttendeesClassName = requestParameters.get("optionalAttendees").getClass().getName();
		}
		
		if (UtilValidate.isNotEmpty(optionalAttendeesClassName)
				&& "java.lang.String".equals(optionalAttendeesClassName)) {
			optionalAttendees = UtilMisc.toList((String) requestParameters.get("optionalAttendees"));
		} else if (UtilValidate.isNotEmpty(optionalAttendeesClassName)
				&& "java.util.LinkedList".equals(optionalAttendeesClassName)) {
			optionalAttendees = (List<String>) requestParameters.get("optionalAttendees");
		}
		
		if (UtilValidate.isNotEmpty(optionalAttendees)) {
			for (String eachOptionalAttendee : optionalAttendees) {
				Map<String, String> optionalAttendeeContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, eachOptionalAttendee);
				if (UtilValidate.isNotEmpty(optionalAttendeeContactInformation) && UtilValidate.isNotEmpty(optionalAttendeeContactInformation.get("EmailAddress"))){
					String optionalAttendeeEmailAddress = optionalAttendeeContactInformation.get("EmailAddress");
					optionalAttendeesEmailIds = optionalAttendeesEmailIds+optionalAttendeeEmailAddress+",";
				}
			}
			if (UtilValidate.isNotEmpty(optionalAttendeesEmailIds)) {
				optionalAttendeesEmailIds = optionalAttendeesEmailIds.substring(0, optionalAttendeesEmailIds.length()-1);
			}
		}
		
		try{
			
			String locationCustomFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);
			
			long start = System.currentTimeMillis();
			if(UtilValidate.isNotEmpty(cNo)){
				fromPartyId = cNo;
			}
			if (UtilValidate.isEmpty(resolution) && "SR_CLOSED".equals(statusId)) {
				String errMsg = "Resolution field is mandatory to resolve the SR";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return ERROR;
			}
			/*
			List<String> srScheduledStatusesList = SrUtil.getSrScheduledStatusList(delegator, null);
			if(UtilValidate.isNotEmpty(srScheduledStatusesList) && srScheduledStatusesList.contains(statusId)){
					String errMsg = "No Scheduled Activities Against this SR.";
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					return ERROR;
			} */
			//LOGGER.log(Level.INFO, "fromPartyId--->"+fromPartyId);
			Map<String, Object> custRequestContext = new HashMap<String, Object>();
			Map<String, Object> supplementoryContext = new HashMap<String, Object>();

			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String nativeBusinessUnit = null;
			String nativeTeamId = null;
			String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId)) {
				Map<String, Object> buTeamData = DataUtil.getUserBuTeam(delegator, userLoginPartyId);
				nativeBusinessUnit = (String) buTeamData.get("businessUnit");
				nativeTeamId = (String) buTeamData.get("emplTeamId");
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", nativeBusinessUnit);
				accessMatrixMap.put("modeOfOp", "Create");
				accessMatrixMap.put("entityName", "CustRequest");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
				//validate the common team and access for the assignment
				String currentPartyId = userLoginPartyId;
				if(UtilValidate.isEmpty(owner)) {
					owner = nativeTeamId;
					if (UtilValidate.isEmpty(owner)) {
						owner = userLoginId;
					}
				} else {
					currentPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
					buTeamData = DataUtil.getUserBuTeam(delegator, currentPartyId);
					nativeBusinessUnit = (String) buTeamData.get("businessUnit");
					nativeTeamId = (String) buTeamData.get("emplTeamId");
				}
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

			}
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				
				String tsmRoleTypeId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "tsm.roleTypeId", delegator);
				boolean isTsmUserLoggedIn = UtilValidate.isNotEmpty(PartyHelper.getFirstValidRoleTypeId(userLoginPartyId, UtilMisc.toList(tsmRoleTypeId), delegator)) ? true : false;
				
				if(UtilValidate.isNotEmpty(custReqDocumentNum)){
					custRequestContext.put("custReqDocumentNum", custReqDocumentNum);
				}
				if(UtilValidate.isNotEmpty(custRequestTypeId)){
					custRequestContext.put("custRequestTypeId", custRequestTypeId);
				}
				if(UtilValidate.isNotEmpty(custRequestCategoryId)){
					custRequestContext.put("custRequestCategoryId", custRequestCategoryId);
				}
				if(UtilValidate.isNotEmpty(custRequestSubCategoryId)){
					custRequestContext.put("custRequestSubCategoryId", custRequestSubCategoryId);
				}
				if(UtilValidate.isNotEmpty(custRequestOthCategoryId)){
					custRequestContext.put("custRequestOthCategoryId", custRequestOthCategoryId);
				}
				if(UtilValidate.isNotEmpty(priorityStr)){
					//long  priority = (Long.parseLong(priorityStr));
					custRequestContext.put("priority", priorityStr);
				}
				if(UtilValidate.isNotEmpty(statusId)){
					custRequestContext.put("statusId", statusId);
				}
				if(UtilValidate.isNotEmpty(subStatusId)){
					custRequestContext.put("subStatusId", subStatusId);
				}
				if(UtilValidate.isNotEmpty(owner)){
					custRequestContext.put("responsiblePerson", owner);
				}
				if(UtilValidate.isNotEmpty(parentCustRequestId)){
					custRequestContext.put("parentCustRequestId", parentCustRequestId);
				}
				custRequestContext.put("emplTeamId", nativeTeamId);
				custRequestContext.put("ownerBu", nativeBusinessUnit);
				/*
        if(UtilValidate.isNotEmpty(owner)){
        	GenericValue userLoginPerson = EntityQuery.use(delegator).select("userLoginId","partyId","emplTeamId","businessUnit").from("UserLoginPerson").where("userLoginId", owner).queryOne();
        	if (UtilValidate.isNotEmpty(userLoginPerson) && UtilValidate.isNotEmpty(userLoginPerson.getString("emplTeamId")) && UtilValidate.isNotEmpty(userLoginPerson.getString("businessUnit"))) {    
        		String emplTeamId = userLoginPerson.getString("emplTeamId");
        		String businessUnit = userLoginPerson.getString("businessUnit");
        		if(UtilValidate.isNotEmpty(emplTeamId)) {
        			custRequestContext.put("emplTeamId", emplTeamId);
        		}
        		if(UtilValidate.isNotEmpty(emplTeamId)) {
        			custRequestContext.put("ownerBu", businessUnit);
        		}
        	}
        }
				 */
				if(UtilValidate.isNotEmpty(custReqSrSource)){
					custRequestContext.put("custReqSrSource", custReqSrSource);
				}
				if(UtilValidate.isNotEmpty(description)){
					Debug.log("SR description--->"+description+"--character-set-->"+request.getCharacterEncoding(), MODULE);
					if (isTsmUserLoggedIn) {
						custRequestContext.put("tsmDescription", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
					} //else {
						custRequestContext.put("description", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
					//}
				}
				if(UtilValidate.isNotEmpty(resolution)){
					custRequestContext.put("resolution", Base64.getEncoder().encodeToString(resolution.getBytes("utf-8")));
				}
				if(UtilValidate.isNotEmpty(fromPartyId)){
					custRequestContext.put("fromPartyId", fromPartyId);
				}
				if(UtilValidate.isNotEmpty(custReqOnceDone)){
					custRequestContext.put("custReqOnceDone", custReqOnceDone);
				}
				if(UtilValidate.isNotEmpty(custReqOnceDone) && "Y".equals(custReqOnceDone)){
					custRequestContext.put("statusId", "SR_CLOSED");
				}
				if(UtilValidate.isNotEmpty(custOrderId)){
					custRequestContext.put("custOrderId", custOrderId);
				}
				if(UtilValidate.isNotEmpty(custRequestName)){
					custRequestContext.put("custRequestName", custRequestName);
				}
				if(UtilValidate.isNotEmpty(notes)){
					//supplementoryContext.put("internalComment", notes);
				}
				if(UtilValidate.isNotEmpty(customerPrimaryEmailParam)){
					custRequestContext.put("emailAddress", customerPrimaryEmailParam);
				}
				if(UtilValidate.isNotEmpty(actualResolution)){
					custRequestContext.put("actualResolution", Base64.getEncoder().encodeToString(actualResolution.getBytes("utf-8")));
				}
				if(UtilValidate.isNotEmpty(homeType)){
					custRequestContext.put("homeType", homeType);
				}
				
				if(UtilValidate.isNotEmpty(accountType)){
					supplementoryContext.put("accountType", accountType);
				}
				if(UtilValidate.isNotEmpty(accountNumber)){
					supplementoryContext.put("accountNumber", accountNumber);
				}
				if(UtilValidate.isNotEmpty(copyFlag)){
					supplementoryContext.put("isCopySr", copyFlag);
					supplementoryContext.put("domainEntityId", externalId);
					supplementoryContext.put("domainEntityType", "SERVICE_REQUEST");
				}
				
				// prepare postal address params [start]
				supplementoryContext.put("pstlAttnName", requestParameters.get("generalAttnName"));
				supplementoryContext.put("pstlAddress1", requestParameters.get("generalAddress1"));
				supplementoryContext.put("pstlAddress2", requestParameters.get("generalAddress2"));
				supplementoryContext.put("pstlPostalCode", requestParameters.get("generalPostalCode"));
				supplementoryContext.put("pstlPostalCodeExt", requestParameters.get("generalPostalCodeExt"));
				supplementoryContext.put("pstlPostalCity", requestParameters.get("generalCity"));
				supplementoryContext.put("pstlStateProvinceGeoId", requestParameters.get("generalStateProvinceGeoId"));
				supplementoryContext.put("pstlCountryGeoId", requestParameters.get("generalCountryGeoId"));
				supplementoryContext.put("pstlCountyGeoId", requestParameters.get("countyGeoId"));
				supplementoryContext.put("isBusiness", requestParameters.get("isBusiness"));
				supplementoryContext.put("isVacant", requestParameters.get("isVacant"));
				supplementoryContext.put("isUspsAddrVerified", requestParameters.get("isUspsAddrVerified"));
				supplementoryContext.put("homePhoneNumber", requestParameters.get("homePhoneNumber"));
				supplementoryContext.put("offPhoneNumber", requestParameters.get("offPhoneNumber"));
				supplementoryContext.put("mobileNumber", requestParameters.get("mobilePhoneNumber"));
				supplementoryContext.put("contractorOffPhone", requestParameters.get("contractorOffNumber"));
				supplementoryContext.put("contractorMobilePhone", requestParameters.get("contractorMobileNumber"));
				supplementoryContext.put("contractorHomePhone", requestParameters.get("contractorHomeNumber"));
				supplementoryContext.put("contractorEmail", requestParameters.get("contractorPrimaryEmail"));
				
				String isUspsRequired = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_USPS_ADDRACT");
				if(UtilValidate.isNotEmpty(isUspsRequired) && isUspsRequired.equals("Y")) {
					Map<String, Object> corodinate = DataHelper.getGeoCoordinateByGoogleApi(UtilMisc.toMap("delegator", delegator, "dispatcher", dispatcher, "userLogin", userLogin, "zip5", (String) requestParameters.get("generalPostalCode"), "zip4", (String) requestParameters.get("generalPostalCodeExt"), "city", requestParameters.get("generalCity"), "state", requestParameters.get("generalStateProvinceGeoId"), "county", requestParameters.get("countyGeoId"), "address1", (String) requestParameters.get("generalAddress1"), "address2", (String) requestParameters.get("generalAddress2"), "country", (String) requestParameters.get("generalCountryGeoId")));
					supplementoryContext.put("latitude", corodinate.get("latitude"));
					supplementoryContext.put("longitude", corodinate.get("longitude"));
				}
				
				// varify to create party postal [start]
				String postalPartyId = null;
				if (UtilValidate.isNotEmpty(primary) && primary.equals("CONTRACTOR")) {
					postalPartyId = contractorId;
				} else {
					postalPartyId = customerId;
				} 
				String contactMechId = UtilContactMech.evalutePartyPostal(UtilMisc.toMap("delegator", delegator, "dispatcher", dispatcher, "userLogin", userLogin
						, "partyId", postalPartyId
						, "address1", (String) requestParameters.get("generalAddress1"), "address2", (String) requestParameters.get("generalAddress2")
						, "countryGeoId", (String) requestParameters.get("generalCountryGeoId"), "stateGeoId", (String) requestParameters.get("generalStateProvinceGeoId")
						, "city", (String) requestParameters.get("generalCity"), "county", (String) requestParameters.get("countyGeoId")
						, "zip5", (String) requestParameters.get("generalPostalCode"), "zip4", (String) requestParameters.get("generalPostalCodeExt")
						, "isBusiness", (String) requestParameters.get("isBusiness"), "isVacant", (String) requestParameters.get("isVacant"), "isUspsAddrVerified", (String) requestParameters.get("isUspsAddrVerified")
						, "latitude", (String) supplementoryContext.get("latitude"), "longitude", (String) supplementoryContext.get("longitude")
						));
				// varify to create party postal [end]
				
				addressContactMechId = UtilValidate.isNotEmpty(contactMechId) ? contactMechId : addressContactMechId;
				
				// prepare postal address params [end]
				
				supplementoryContext.put("descriptionRawTxt", description);
				supplementoryContext.put("resolutionRawTxt", resolution);
				
				if (UtilValidate.isNotEmpty(customerId)) {
					Map<String, Object> homeOwnerContactInfoContext = new HashMap<String, Object>();
					homeOwnerContactInfoContext.put("customerId", customerId);
					homeOwnerContactInfoContext.put("homePhoneNumberParam", homePhoneNumberParam);
					homeOwnerContactInfoContext.put("offPhoneNumberParam", offPhoneNumberParam);
					homeOwnerContactInfoContext.put("mobilePhoneNumberParam", mobilePhoneNumberParam);
					homeOwnerContactInfoContext.put("primaryEmailParam", customerPrimaryEmailParam);

					Map<String, Object> homeOwnerContactInputMap = new HashMap<String, Object>();
					homeOwnerContactInputMap.put("contactInformationContext", homeOwnerContactInfoContext);
					homeOwnerContactInputMap.put("userLogin", userLogin);
					
					Map<String, Object> homeOwnerContactResultsMap = dispatcher.runSync("srPortal.validateSrContactInformation", homeOwnerContactInputMap);
					
					if(!ServiceUtil.isSuccess(homeOwnerContactResultsMap)) {
						request.setAttribute("_ERROR_MESSAGE_", "Problem While Validating Home Owner Contact Information");
						return ERROR;
					}
				}
				
				if (UtilValidate.isNotEmpty(contractorId)) {
					Map<String, Object> contractorContactInfoContext = new HashMap<String, Object>();
					contractorContactInfoContext.put("customerId", contractorId);
					contractorContactInfoContext.put("homePhoneNumberParam", contractorHomeNumberParam);
					contractorContactInfoContext.put("offPhoneNumberParam", contractorOffNumberParam);
					contractorContactInfoContext.put("mobilePhoneNumberParam", contractorMobileNumberParam);
					contractorContactInfoContext.put("primaryEmailParam", contractorPrimaryEmailParam);

					Map<String, Object> contractorContactInputMap = new HashMap<String, Object>();
					contractorContactInputMap.put("contactInformationContext", contractorContactInfoContext);
					contractorContactInputMap.put("userLogin", userLogin);
					
					Map<String, Object> contractorContactResultsMap = dispatcher.runSync("srPortal.validateSrContactInformation", contractorContactInputMap);
					
					if(!ServiceUtil.isSuccess(contractorContactResultsMap)) {
						request.setAttribute("_ERROR_MESSAGE_", "Problem While Validating Contractor Contact Information");
						return ERROR;
					}
				}
				
				GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", fromPartyId), null, false) );
				String roleTypeId = party.getString("roleTypeId");

				if (UtilValidate.isNotEmpty(roleTypeId)) {
					if ("CUSTOMER".equalsIgnoreCase(roleTypeId)) {
						custRequestContext.put("customerRelatedType", roleTypeId);
					}else if ("PROSPECT".equalsIgnoreCase(roleTypeId)) {
						custRequestContext.put("customerRelatedType", "02");
					}else if ("NON_CRM".equalsIgnoreCase(roleTypeId)) {
						custRequestContext.put("customerRelatedType", "07");
					} else {
						custRequestContext.put("customerRelatedType", roleTypeId);
					}
				}
				custRequestContext.put("customerId", customerId);
				
				custRequestContext.put("custRequestId", org.fio.sr.portal.util.UtilGenerator.getSrNumber(delegator, (String) supplementoryContext.get("pstlStateProvinceGeoId"), (String) supplementoryContext.get("pstlCountyGeoId")));
							
				custRequestContext.put("request", request);
				
				
				Map<String, Object> inputMap = new HashMap<String, Object>();
				inputMap.put("custRequestContext", custRequestContext);
				inputMap.put("supplementoryContext", supplementoryContext);
				inputMap.put("userLogin", userLogin);
				
				
				Map<String, Object> outMap = dispatcher.runSync("crmPortal.createServiceRequest", inputMap);
				
				if(!ServiceUtil.isSuccess(outMap)) {
					request.setAttribute("_ERROR_MESSAGE_", "Problem While Creating Service Request");
					return ERROR;
				}
				String custRequestId = (String) outMap.get("custRequestId");
				if(UtilValidate.isEmpty(custRequestId) || custRequestId == null){
					String errMsg = "Problem While Creating Service Request ";
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					return ERROR;
				}else if (UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(notes)) {
					Map < String, Object > inputNote = FastMap.newInstance();
					inputNote.put("custRequestId", custRequestId);
					inputNote.put("noteInfo", notes);
					inputNote.put("userLogin", userLogin);
					Map serviceResult = null;
					try {
						//serviceResult = dispatcher.runSync("createCustRequestNote", inputNote);

						GenericValue noteDataCreate = delegator.makeValue("NoteData");
						String noteId = delegator.getNextSeqId("NoteData");
						noteId = UtilGenerator.getNoteNumber(delegator, noteId);

						noteDataCreate.set("noteId",noteId);
						noteDataCreate.set("noteName", "Service Request");
						noteDataCreate.set("noteInfo", notes);
						noteDataCreate.set("noteDateTime", UtilDateTime.nowTimestamp());
						noteDataCreate.set("createdByUserLogin", userLogin.getString("userLoginId"));
						noteDataCreate.set("noteParty", userLogin.getString("userLoginId"));
						delegator.create(noteDataCreate);

						if (UtilValidate.isNotEmpty(noteId) && UtilValidate.isNotEmpty(custRequestId)) {
							GenericValue custRequestNote = delegator.makeValue("CustRequestNote");
							custRequestNote.set("noteId",noteId);
							custRequestNote.set("custRequestId",custRequestId);
							delegator.create(custRequestNote);
						}

						if (ServiceUtil.isError(serviceResult)) {
							String errMsg = "Problem While Creating Notes for Service Request ";
							request.setAttribute("_ERROR_MESSAGE_", errMsg);
							return ERROR;
						}else{
							request.setAttribute("externalId", custRequestId);
							request.setAttribute("_EVENT_MESSAGE_", "SR Created Successfully"+": "+custRequestId);
						}
					} catch (Exception e) {
						String errMsg = "Problem While Creating Notes For Service Request " + e.toString();
						request.setAttribute("_ERROR_MESSAGE_", errMsg);
						Debug.logError(e, "Exception : "+e.getMessage(), MODULE);
						return ERROR;
					}
				}else if (UtilValidate.isNotEmpty(custRequestId)) {
					
					if (UtilValidate.isNotEmpty(externalId)) {
						GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", externalId).queryOne();
						if (UtilValidate.isNotEmpty(custRequest)) {
							custRequest.put("custReqDocumentNum", "");
							custRequest.store();
						}
					}
					
					if (UtilValidate.isNotEmpty(custRequestId)) {
						request.setAttribute("custRequestId", custRequestId);
						
						Map<String, Object> historyInputMap = new HashMap<String, Object>();
						historyInputMap.put("custRequestId", custRequestId);
						historyInputMap.put("userLogin", userLogin);
						
						Map<String, Object> historyOutMap = dispatcher.runSync("srPortal.createSrHistory", historyInputMap);
						//String serviceResult = createSrHistory(request,response);

						if(!ServiceUtil.isSuccess(historyOutMap)) {
							request.setAttribute("_ERROR_MESSAGE_", "Problem While Creating Service Request History");
							return ERROR;
						}	
					}
					
					if(UtilValidate.isNotEmpty(primContactList)) {
						for(String primContactId : primContactList) {
							GenericValue custRequestContact = delegator.makeValue("CustRequestContact");
							custRequestContact.set("custRequestId",custRequestId);
							custRequestContact.set("partyId", primContactId);
							custRequestContact.set("roleTypeId", "CONTACT");
							custRequestContact.set("isPrimary", "Y");


							custRequestContact.set("fromDate", UtilDateTime.nowTimestamp());
							delegator.create(custRequestContact);
						}
					}
					/*
					if (UtilValidate.isNotEmpty(primaryContactId)) {
						GenericValue custRequestContact = delegator.makeValue("CustRequestContact");
						custRequestContact.set("custRequestId",custRequestId);
						custRequestContact.set("partyId", primaryContactId);
						custRequestContact.set("roleTypeId", "CONTACT");
						custRequestContact.set("isPrimary", "Y");


						custRequestContact.set("fromDate", UtilDateTime.nowTimestamp());
						delegator.create(custRequestContact);
					}
					*/
					
					//To store optional contacts
					if (UtilValidate.isNotEmpty(optionalAttendees)) {
						for (String eachOptionalAttendee : optionalAttendees) {
							GenericValue custRequestContact = delegator.makeValue("CustRequestContact");
							custRequestContact.set("custRequestId",custRequestId);
							custRequestContact.set("partyId", eachOptionalAttendee);
							custRequestContact.set("roleTypeId", "CONTACT");
							custRequestContact.set("isPrimary", "N");
							custRequestContact.set("fromDate", UtilDateTime.nowTimestamp());
							delegator.create(custRequestContact);
						}
					}
					if (UtilValidate.isNotEmpty(primary)) {						
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "PRIMARY");
						custRequestAttrbute.set("attrValue", primary);
						delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(serviceFee)) {
						String customFieldName = DataUtil.getGlobalValue(delegator, "SERVICE_FEE", "Service for a Fee");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "SERVICE_GROUP", customFieldName);
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", customFieldId);
						custRequestAttrbute.set("attrValue", serviceFee);
						delegator.create(custRequestAttrbute);
					}
					/*
					if (UtilValidate.isNotEmpty(finishType)) {						
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "10144");
						custRequestAttrbute.set("attrValue", finishType);
						delegator.create(custRequestAttrbute);
					} */
					if (UtilValidate.isNotEmpty(vendorCode)) {		
						String customFieldName = DataUtil.getGlobalValue(delegator, "VENDOR_CODE", "Vendor Code");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "VENDOR_GROUP", customFieldName);
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", customFieldId);
						custRequestAttrbute.set("attrValue", vendorCode);
						delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(location)) {						
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", locationCustomFieldId);
						custRequestAttrbute.set("attrValue", location);
						delegator.create(custRequestAttrbute);
					}
					
					if (UtilValidate.isNotEmpty(soldByLocation)) {				
						String customFieldName = DataUtil.getGlobalValue(delegator, "SOLD_BY_LOCATION", "Sold By Location");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "CUSTOMER_GRP", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", customFieldId);
							custRequestAttrbute.set("attrValue", soldByLocation);
							delegator.create(custRequestAttrbute);
						}
					}
					
					if (UtilValidate.isNotEmpty(preFinishPlus)) {
						String customFieldName = DataUtil.getGlobalValue(delegator, "PRO_FINISH_PLUS", "Pro Finish Plus");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "FINISH_GROUP", customFieldName);
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", customFieldId);
						custRequestAttrbute.set("attrValue", preFinishPlus);
						delegator.create(custRequestAttrbute);
					}
					if(UtilValidate.isNotEmpty(addressContactMechId)) {
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "RECOMM_ADDRESS");
						custRequestAttrbute.set("attrValue", addressContactMechId);
						delegator.create(custRequestAttrbute);
					}
					
					if (UtilValidate.isNotEmpty(finishType)) {	
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "FSR_FINISH_TYPE");
						custRequestAttrbute.set("attrValue", finishType);
						delegator.create(custRequestAttrbute);
					}
					
					if (UtilValidate.isNotEmpty(finishColor)) {	
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "FSR_FINISH_COLOR");
						custRequestAttrbute.set("attrValue", finishColor);
						delegator.create(custRequestAttrbute);
						
						// segmentation
						/*
						GenericValue customFieldGroup = EntityQuery.use(delegator).from("CustomFieldGroup").where("groupId", finishType,"groupType","SEGMENTATION").queryFirst();
						
						GenericValue segmentationValueAssoc = delegator.makeValidValue("SegmentationValueAssoc");
						segmentationValueAssoc.set("groupId", finishType);
						segmentationValueAssoc.set("customFieldId", finishColor);
						segmentationValueAssoc.set("domainEntityId", custRequestId);
						segmentationValueAssoc.set("domainEntityType", "SERVICE_REQUEST");
						segmentationValueAssoc.set("groupActualValue", UtilValidate.isNotEmpty(customFieldGroup) ? customFieldGroup.getString("groupName") : "" );
						segmentationValueAssoc.create();
						*/
						
					}
					
					if (UtilValidate.isNotEmpty(materialCategory)) {	
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "FSR_MATERIAL_CATEGROY");
						custRequestAttrbute.set("attrValue", materialCategory);
						delegator.create(custRequestAttrbute);
					}
					
					if (UtilValidate.isNotEmpty(materialSubCategory)) {	
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "FSR_MATERIAL_SUB_CATEGROY");
						custRequestAttrbute.set("attrValue", materialSubCategory);
						delegator.create(custRequestAttrbute);
						
						// segmentation
						/*
						GenericValue customFieldGroup = EntityQuery.use(delegator).from("CustomFieldGroup").where("groupId", materialCategory,"groupType","SEGMENTATION").queryFirst();
						
						GenericValue segmentationValueAssoc = delegator.makeValidValue("SegmentationValueAssoc");
						segmentationValueAssoc.set("groupId", materialCategory);
						segmentationValueAssoc.set("customFieldId", materialSubCategory);
						segmentationValueAssoc.set("domainEntityId", custRequestId);
						segmentationValueAssoc.set("domainEntityType", "SERVICE_REQUEST");
						segmentationValueAssoc.set("groupActualValue", UtilValidate.isNotEmpty(customFieldGroup) ? customFieldGroup.getString("groupName") : "" );
						segmentationValueAssoc.create();
						*/
					}
					if (UtilValidate.isNotEmpty(coordinatorDesc)) {
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "CSR_DESC");
						custRequestAttrbute.set("attrValue", Base64.getEncoder().encodeToString(coordinatorDesc.getBytes("utf-8")));
						delegator.create(custRequestAttrbute);
					}
					
					if (UtilValidate.isNotEmpty(inspectedBy)) {				
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTED_BY", "Inspected By");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", customFieldId);
							custRequestAttrbute.set("attrValue", inspectedBy);
							delegator.create(custRequestAttrbute);
						}
					}
					if (UtilValidate.isNotEmpty(inspectionDate)) {				
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTION_DATE", "Inspection Date");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", customFieldId);
							custRequestAttrbute.set("attrValue", inspectionDate);
							delegator.create(custRequestAttrbute);
						}
					}
					if (UtilValidate.isNotEmpty(installedSquare)) {				
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED_SQUARE", "Installed Square, Level, and Plumb");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", customFieldId);
							custRequestAttrbute.set("attrValue", installedSquare);
							delegator.create(custRequestAttrbute);
						}
					}
					if (UtilValidate.isNotEmpty(hasAlarmSystem)) {				
						String customFieldName = DataUtil.getGlobalValue(delegator, "HAS_ALARM_SYSTEM", "Has Alarm System");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", customFieldId);
							custRequestAttrbute.set("attrValue", hasAlarmSystem);
							delegator.create(custRequestAttrbute);
						}
					}
					if (UtilValidate.isNotEmpty(installed)) {				
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED", "Installed");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", customFieldId);
							custRequestAttrbute.set("attrValue", installed);
							delegator.create(custRequestAttrbute);
						}
					}
					
					if (UtilValidate.isNotEmpty(dealerRefNo)) {
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "DEALER_REF_NO");
						custRequestAttrbute.set("attrValue", dealerRefNo);
						delegator.create(custRequestAttrbute);
					}
					
					// Program template [start]
					UtilAttribute.storeServiceAttrValue(delegator, custRequestId, "IS_PROG_TPL", isProgramTemplate);
					UtilAttribute.storeServiceAttrValue(delegator, custRequestId, "PROG_TPL_ID", programTemplateId);
					if (UtilValidate.isNotEmpty(programTemplateId)) {
						Map<String, Object> callCtxt = FastMap.newInstance();
						Map<String, Object> callResult = FastMap.newInstance();
						
						Map<String, Object> requestContext = FastMap.newInstance();
						
						requestContext.put("programTemplateId", programTemplateId);
						requestContext.put("ownerPartyId", customerId);
						requestContext.put("ownerRoleTypeId", "CUSTOMER");
						
						requestContext.put("domainEntityType", DomainEntityType.SERVICE_REQUEST);
						requestContext.put("domainEntityId", custRequestId);
						requestContext.put("emplTeamId", nativeTeamId);
						requestContext.put("businessUnit", nativeBusinessUnit);
						
						callCtxt.put("requestContext", requestContext);

						callCtxt.put("userLogin", userLogin);

						callResult = dispatcher.runSync("activity.associateProgramActivities", callCtxt);
						if (ServiceUtil.isSuccess(callResult)) {
							Debug.logInfo("Successfully associated activities with newly created Program", MODULE);
						}
					}
					// Program template [end]
					
					//To store the data in custRequestParty
					if(UtilValidate.isNotEmpty(customerId)) {
						String customerRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, customerId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, customerId, customerRoleTypeId);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, customerId, customerRoleTypeId);
					}
					if(UtilValidate.isNotEmpty(fromPartyId)) {
						String fromPartyIdRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, fromPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, fromPartyId, fromPartyIdRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, fromPartyId, fromPartyIdRole);
					}
					
					/*
					if(UtilValidate.isNotEmpty(primaryContactId)) {
						String primContactRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, primaryContactId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, primaryContactId, primContactRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, primaryContactId, primContactRole);
					}
					*/
					if(UtilValidate.isNotEmpty(primContactList)) {
						for(String primContactId : primContactList) {
							String primContactRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, primContactId);
							org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, primContactId, primContactRole);
						}
						String primContactIdStr = org.fio.admin.portal.util.DataUtil.listToString(primContactList, ",");
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, primContactIdStr, "CONTACT");
					}
					
					if(UtilValidate.isNotEmpty(owner)) {
						String ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
						String ownerRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, ownerPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, ownerPartyId, ownerRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, ownerPartyId, ownerRole);
					}
					if(UtilValidate.isNotEmpty(userLoginId)) {
						userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
						String userLoginRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, userLoginPartyId, userLoginRole);
						//org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, userLoginPartyId, userLoginRole);
					}
					if(UtilValidate.isNotEmpty(contractorId)) {
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, contractorId, "CONTRACTOR");
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, contractorId, "CONTRACTOR");
					}
					if(UtilValidate.isNotEmpty(salesPerson)) {
						String ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
						if(!salesPerson.equals(ownerPartyId)){
							org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, salesPerson, "SALES_REP");
							org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, salesPerson, "SALES_REP");
						}
					}					
					
					if(UtilValidate.isNotEmpty(primaryTechnician)) {
						String primaryTechnicianRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, primaryTechnician);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, primaryTechnician, primaryTechnicianRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, primaryTechnician, primaryTechnicianRole);
						
						// send the assign email for the technician
						String rolesToSent = DataUtil.getGlobalValue(delegator, "PRIMARY_TECH_EMAIL_ROLE");
						List<String> techRoleList = new ArrayList<String>();
						if(UtilValidate.isNotEmpty(rolesToSent)) {
							if(UtilValidate.isNotEmpty(rolesToSent) && rolesToSent.contains(",")) {
								techRoleList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(rolesToSent, ","));
							} else 
								techRoleList.add(rolesToSent);
						}
						if(UtilValidate.isNotEmpty(techRoleList)) {
							boolean is3rdPartyTechnician = org.fio.admin.portal.util.DataUtil.is3rdPartyTechnician(delegator, primaryTechnician);
							for(String techRole : techRoleList) {
								boolean eligibleForEmail = false;
								if(UtilValidate.isNotEmpty(techRole) && "THIRD_PARTY_TECH".equals(techRole) && is3rdPartyTechnician) {
									eligibleForEmail = true;
								}
								if(UtilValidate.isNotEmpty(techRole) && "REEB_TECH".equals(techRole) && !(is3rdPartyTechnician)) {
									eligibleForEmail = true;
								}
								
								if(eligibleForEmail) {
									String nsender = "";
									String nto = "";
									String subject = "";
									GenericValue sytemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryOne();

									Debug.log("===== SR Primary tech EMAIL TRIGGER sytemProperty ===="+sytemProperty);

									if(UtilValidate.isNotEmpty(sytemProperty)){
										nsender = sytemProperty.getString("systemPropertyValue");
									}else{
										nsender = (String) requestParameters.get("fromEmailId");
									}

									Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,primaryTechnician);
									nto = ntoContactInformation.get("EmailAddress");

									String srEmailNotify = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_EMAIL_NOTIFIY", "Y");

									if (UtilValidate.isNotEmpty(nsender) && UtilValidate.isNotEmpty(nto) && "Y".equals(srEmailNotify)){

										String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "PRIMARY_TECH_EMAIL_TEMPLATE");

										if(UtilValidate.isNotEmpty(templateId)) {

											GenericValue emailTemlateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",templateId), false);

											String emailContent = "";
											String templateFormContent = emailTemlateData.getString("templateFormContent");
											if (UtilValidate.isNotEmpty(templateFormContent)) {
												if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
													templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
												}
											}

											if(UtilValidate.isNotEmpty(emailTemlateData.getString("subject"))) {
												subject = "FSR# "+custRequestId+" ("+custRequestName+") - "+emailTemlateData.getString("subject");
											}

											// prepare email content [start]
											Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
											extractContext.put("delegator", delegator);
											extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
											extractContext.put("fromEmail", nsender);
											extractContext.put("toEmail", nto);
											extractContext.put("partyId", primaryTechnician);
											extractContext.put("custRequestId", custRequestId);
											extractContext.put("emailContent", templateFormContent);
											extractContext.put("templateId", templateId);

											Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
											emailContent = (String) extractResultContext.get("emailContent");
											// prepare email content [end]

											Map<String, Object> callCtxt = FastMap.newInstance();
											Map<String, Object> callResult = FastMap.newInstance();
											Map<String, Object> requestContext = FastMap.newInstance();

											requestContext.put("nsender", nsender);
											requestContext.put("nto", nto);
											requestContext.put("subject", subject);
											requestContext.put("emailContent", emailContent);
											requestContext.put("templateId", templateId);

											callCtxt.put("requestContext", requestContext);
											callCtxt.put("userLogin", userLogin);

											Debug.log("===== SR Primary tech EMAIL TRIGGER ===="+callCtxt);

											callResult = dispatcher.runSync("common.sendEmail", callCtxt);
											if (ServiceUtil.isError(callResult)) {
												String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
												return returnError(request, errMsg);
											}

										}
									}
								}
							}
						}
						
					}
					
					if(UtilValidate.isNotEmpty(reasonCodeList)) {
						EntityCondition resonCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "REASON_CODE"),
								EntityCondition.makeCondition("reasonId", EntityOperator.NOT_IN, reasonCodeList));
						delegator.removeByCondition("CustRequestResolution", resonCondition);
						for(String reasonCode : reasonCodeList) {
							org.fio.sr.portal.DataHelper.createCustReqResolution(delegator, UtilMisc.toMap("custRequestId", custRequestId, "custRequestTypeId", SrResolutionConstant.REASON_CODE,"reasonId", reasonCode));
						}
					} else {
						EntityCondition resonCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "REASON_CODE"));
						delegator.removeByCondition("CustRequestResolution", resonCondition);
					}
					
					if(UtilValidate.isNotEmpty(causeCategoryList)) {
						EntityCondition causeCatCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "CAUSE_CATEGORY"),
								EntityCondition.makeCondition("causeCategoryId", EntityOperator.NOT_IN, causeCategoryList));
						delegator.removeByCondition("CustRequestResolution", causeCatCondition);
						for(String causeCategory : causeCategoryList) {
							org.fio.sr.portal.DataHelper.createCustReqResolution(delegator, UtilMisc.toMap("custRequestId", custRequestId, "custRequestTypeId", SrResolutionConstant.CAUSE_CATEGORY, "causeCategoryId", causeCategory));
						}
					}  else {
						EntityCondition causeCatCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "CAUSE_CATEGORY"));
						delegator.removeByCondition("CustRequestResolution", causeCatCondition);
					}

					if (UtilValidate.isNotEmpty(alternateId)) {
						GenericValue partyIdentification = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", fromPartyId));
						partyIdentification.put("partyIdentificationTypeId", alternateIdentificationTypeId != null ? alternateIdentificationTypeId :"ALTERNATE_ID");
						partyIdentification.put("idValue", alternateId);
						partyIdentification.create();
					}

					if (UtilValidate.isNotEmpty(nationalId)) {
						GenericValue partyIdentification = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", fromPartyId));
						partyIdentification.put("partyIdentificationTypeId", nationalIdentificationTypeId != null ? nationalIdentificationTypeId :"NATIONAL_ID");
						partyIdentification.put("idValue", nationalId);
						partyIdentification.create();
					}
					
					if(UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(workEffortId)) {
						GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryFirst();
						GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryFirst();
						if(UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(workEffort)) {
							GenericValue custRequestWorkEffort = delegator.makeValue("CustRequestWorkEffort");
							custRequestWorkEffort.put("custRequestId", custRequestId);
							custRequestWorkEffort.put("workEffortId", workEffortId);
							custRequestWorkEffort.create();
							
							
							//Add domain type and id
							workEffort.set("domainEntityType", "SERVICE_REQUEST");
							workEffort.set("domainEntityId", custRequestId);
							workEffort.store();
						}
		    		}
					if(UtilValidate.isNotEmpty(copyFlag)){
						//move attachment to the child FSR
						org.groupfio.common.portal.util.DataUtil.loadParentAttachments(delegator, externalId, custRequestId);
						
						//copy notes from parent to child FSR
						org.groupfio.common.portal.util.DataUtil.loadParentNotes(delegator, externalId, custRequestId);
					}

					/*String partyDesc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, cNo, false);
					String ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, owner, false);
					String srStatusDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId);*/

					//String subject = "Created SR ID "+custRequestId+" - "+custRequestName;
					//String subject = "Update on SR ID "+custRequestId+" - "+custRequestName;
					String subject = "FSR# "+custRequestId+" ("+custRequestName+") - FSR Notification";
					
					/* Map<String, String> loggedInUserContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLogin.getString("partyId"));
                String nsender = loggedInUserContactInformation.get("EmailAddress");*/

					String nsender = "";
					String nto = "";
					String ccAddresses = "";
					String ccAdd = "";
					String signName = "";
					
					if (UtilValidate.isNotEmpty(clientPortal) && "clientPortal".equals(clientPortal)){
						nto = (String) requestParameters.get("toEmailId");
						String ownerDesc = (String) requestParameters.get("cspSupportName");
						
						Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLogin.getString("partyId"));
						//nsender = ntoContactInformation.get("EmailAddress");
						GenericValue sytemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryOne();
						
						if(UtilValidate.isNotEmpty(sytemProperty)){
							nsender = sytemProperty.getString("systemPropertyValue");
						}else{
							nsender = (String) requestParameters.get("fromEmailId");
						}
						
						GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);

						if(UtilValidate.isNotEmpty(personGv)){
							signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
						}
						
						if(UtilValidate.isNotEmpty(priorityStr)){
							if ("62816".equals(priorityStr)){
								subject = "FSR# "+custRequestId+" ("+custRequestName+") - FSR Notification";
							}else if ("62817".equals(priorityStr)){
								subject = "FSR# "+custRequestId+" ("+custRequestName+") - FSR Notification";
							}else if ("62818".equals(priorityStr)){
								subject = "FSR# "+custRequestId+" ("+custRequestName+") - FSR Notification";
							}
						}
						
						if(UtilValidate.isNotEmpty(primContactList)) {
							for(String primConId : primContactList) {
								Map<String, String> primaryContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,primConId);
								if(UtilValidate.isNotEmpty(primaryContactInformation)){
									ccAddresses = primaryContactInformation.get("EmailAddress");
									if(UtilValidate.isNotEmpty(optionalAttendeesEmailIds)){
										ccAddresses = ccAddresses+","+optionalAttendeesEmailIds;
									}
								}else{
									ccAddresses = optionalAttendeesEmailIds;
								}
							}
						}
						
					}else{
						signName = "CRM Administrator.";
						
						GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);

						if(UtilValidate.isNotEmpty(personGv)){
							signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
						}
						
						GenericValue sytemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryOne();
						
						Debug.log("===== SR CREATION EMAIL TRIGGER sytemProperty ===="+sytemProperty);
						
						if(UtilValidate.isNotEmpty(sytemProperty)){
							nsender = sytemProperty.getString("systemPropertyValue");
						}else{
							nsender = (String) requestParameters.get("fromEmailId");
						}
						
						GenericValue userLoginPerson = EntityUtil.getFirst(EntityQuery.use(delegator).select("userLoginId","partyId","firstName").from("UserLoginPerson").where("userLoginId", owner).queryList());

						Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLoginPerson.getString("partyId"));
						nto = ntoContactInformation.get("EmailAddress");

						
						if(UtilValidate.isNotEmpty(userLoginPerson)) {
							Map<String, String> backupCoordinatorEmail = SrUtil.getBackupCoordinatorInfo(delegator, userLoginPerson.getString("partyId"));
							if(UtilValidate.isNotEmpty(backupCoordinatorEmail)) {
								ccAdd = backupCoordinatorEmail.get("EmailAddress");
							}
						}
						
						
						//Map<String, String> primaryContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,primaryContactId);
						List<String> srAssocPartiesEmailIds = SrUtil.getSrAssocPartyEmailIds(delegator, custRequestId, statusId, primary);
						
						if(UtilValidate.isNotEmpty(srAssocPartiesEmailIds)){
							for(String eachAssocEmailId :srAssocPartiesEmailIds ){
								ccAddresses = ccAddresses+eachAssocEmailId+",";
							}
							ccAddresses = ccAddresses.substring(0,ccAddresses.length()-1);
							if(UtilValidate.isNotEmpty(optionalAttendeesEmailIds)){
								ccAddresses = ccAddresses+","+optionalAttendeesEmailIds;
							}
						}else{
							ccAddresses = optionalAttendeesEmailIds;
						}
					}
					
					Debug.log("===== nsender ===="+nsender);
					Debug.log("===== nto ===="+nto);
					Debug.log("===== ccAddresses ===="+ccAddresses);
					
					String srEmailNotify = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_EMAIL_NOTIFIY", "Y");
					
					if (UtilValidate.isNotEmpty(nsender) && UtilValidate.isNotEmpty(nto) && "Y".equals(srEmailNotify)){

						Map<String, Object> srStatusesMap = org.fio.homeapps.util.DataUtil.getSrStatusList(delegator, "SR_STATUS_ID");
						
						String srStatusId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_EMAIL_NOTIFICATION_STAGE");
						
						if(UtilValidate.isNotEmpty(srStatusId) && UtilValidate.isNotEmpty(statusId)){
							
							int globalSrStatusSeq = Integer.parseInt((String)srStatusesMap.get(srStatusId));

							int srStatusSeqParam = Integer.parseInt((String)srStatusesMap.get(statusId));

							if(UtilValidate.isNotEmpty(globalSrStatusSeq) && UtilValidate.isNotEmpty(srStatusSeqParam) && srStatusSeqParam >= globalSrStatusSeq) {
								
								String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_TEMPLATE");
								
								if(UtilValidate.isNotEmpty(templateId)) {

									GenericValue emailTemlateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",templateId), false);

									String emailContent = "";
									String templateFormContent = emailTemlateData.getString("templateFormContent");
									if (UtilValidate.isNotEmpty(templateFormContent)) {
										if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
											templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
										}
									}
									
									if(UtilValidate.isNotEmpty(emailTemlateData.getString("subject"))) {
										subject = "FSR# "+custRequestId+" ("+custRequestName+") - "+emailTemlateData.getString("subject");
									}

									// prepare email content [start]
									Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
									extractContext.put("delegator", delegator);
									extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
									extractContext.put("fromEmail", nsender);
									extractContext.put("toEmail", nto);
									extractContext.put("partyId", fromPartyId);
									extractContext.put("custRequestId", custRequestId);
									extractContext.put("emailContent", templateFormContent);
									extractContext.put("templateId", templateId);

									Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
									emailContent = (String) extractResultContext.get("emailContent");
									// prepare email content [end]

									Map<String, Object> callCtxt = FastMap.newInstance();
									Map<String, Object> callResult = FastMap.newInstance();
									Map<String, Object> requestContext = FastMap.newInstance();

									requestContext.put("nsender", nsender);
									requestContext.put("nto", nto);
									requestContext.put("subject", subject);
									requestContext.put("emailContent", emailContent);
									requestContext.put("templateId", templateId);
									requestContext.put("ccAddresses", ccAdd);
									requestContext.put("nbcc", ccAddresses);

									callCtxt.put("requestContext", requestContext);
									callCtxt.put("userLogin", userLogin);

									Debug.log("===== SR CREATION EMAIL TRIGGER ===="+callCtxt);

									callResult = dispatcher.runSync("common.sendEmail", callCtxt);
									if (ServiceUtil.isError(callResult)) {
										String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
										return returnError(request, errMsg);
									}

								}
							}
						}

					}

					request.setAttribute("externalId", custRequestId);
					request.setAttribute("_EVENT_MESSAGE_", "SR Created Successfully"+": "+custRequestId);

				}else{
					request.setAttribute("externalId", custRequestId);
					request.setAttribute("_EVENT_MESSAGE_", "SR Created Successfully"+": "+custRequestId);
				}
			} else {
				String errMsg = "";
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errMsg = accessMatrixRes.get("errorMessage").toString();
				} else {
					errMsg = "Access Denied";
				}
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				Debug.logError("error==" + errMsg, MODULE);
				return ERROR;
			}
			
			long end = System.currentTimeMillis();
			Debug.logInfo("Time Taken by the Create SR --->"+(end-start) / 1000f, MODULE);
		}
		catch (Exception e) {
			String errMsg = "Problem While Creating Service Request " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			Debug.logError(e, "Exception : "+ e.getMessage(), MODULE);
			return ERROR;
		}
		request.getSession().setAttribute("isOpenOrderAssocTab", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CSROPN_ORD_ASSOC_TAB", "N"));
		return SUCCESS;
	}

	public static String getOwnerTeam(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String emplTeamId = (String) context.get("emplTeamId");
		String businessUnitId = (String) context.get("businessUnitId");
		Map<String, Object> inputMap = new HashMap<String, Object>();
		try {
			if(UtilValidate.isNotEmpty(businessUnitId)){
				inputMap.put("businessUnitId", businessUnitId);
			}
			if(UtilValidate.isNotEmpty(emplTeamId)){
				inputMap.put("emplTeamId", emplTeamId);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inputMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("srPortal.getOwnerTeam", inputMap);
			Debug.log("inputMap--------"+inputMap);
			Debug.log("outMap--------"+outMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching Owner Team");
				return ERROR;
			}else{

				results=(List<Map<String, Object>>)outMap.get("results");
			}

		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			return AjaxEvents.doJSONResponse(response, e.getMessage());
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	public static String setLoginHistory(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String seqId = request.getParameter("seqId");
		String entity = request.getParameter("entity");
		Map<String,Object> inMap = FastMap.newInstance();
		GenericValue userLogin=getUserLogin(request);
		if (UtilValidate.isEmpty(userLogin)) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("error", "No user login details found!");
			return doJSONResponse(response, data);
		}
		try {
			if(UtilValidate.isNotEmpty(seqId) && UtilValidate.isNotEmpty(entity)) {
				if(UtilValidate.isNotEmpty(seqId)) {
					inMap.put("seqId", seqId);
				}
				if(UtilValidate.isNotEmpty(entity)) {
					inMap.put("entity", entity);
				}
				if(UtilValidate.isNotEmpty(userLogin)) {
					inMap.put("userLogin", userLogin);
				}
				if(UtilValidate.isNotEmpty(session)) {
					inMap.put("session", session);
				}
				Map<String, Object> outMap = dispatcher.runSync("srPortal.setLoginHistory", inMap);

				if(!ServiceUtil.isSuccess(outMap)) {
					request.setAttribute("_ERROR_MESSAGE_", "Problem While saving Service Request User Login History");
					return ERROR;
				}		

			}
		} catch (Exception e) {
			String errMsg = "Problem While Saving Login History Details " + e.toString();
			Debug.logError(e, errMsg, MODULE);
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return ERROR;
		}
		return SUCCESS;
	}
	public static String getRecentlyViewedDetails(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String entity = request.getParameter("entity");
		GenericValue userLogin=getUserLogin(request);
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> inputMap = new HashMap<String, Object>();
		if (UtilValidate.isEmpty(userLogin)) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("error", "No user login details found!");
			return doJSONResponse(response, data);
		}
		try {
			if(UtilValidate.isNotEmpty(entity)){
				inputMap.put("entity", entity);
			}
			if(UtilValidate.isNotEmpty(userLogin)){
				inputMap.put("userLogin", userLogin);
			}
			Map<String, Object> outMap = dispatcher.runSync("srPortal.getOwnerTeam", inputMap);
			Debug.log("inputMap--------"+inputMap);
			Debug.log("outMap--------"+outMap);
			if(!ServiceUtil.isSuccess(outMap)) {
				request.setAttribute("_ERROR_MESSAGE_", "Problem While Fetching recently viewed sr Details");
				return ERROR;
			}else{

				result=(Map<String, Object>)outMap.get("results");
			}

		} catch (Exception e) {
			String errMsg = "Problem While  fetching recently viewed sr Details" + e.toString();
			Debug.logError(e, errMsg, MODULE);
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return ERROR;
		}
		return  doJSONResponse(response, result);
	}   
	public static String getBusinessUnitName(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String owner = request.getParameter("owner");
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		try {
			if (UtilValidate.isNotEmpty(owner)) {
				GenericValue userLoginPerson = EntityQuery.use(delegator).select("userLoginId","businessUnit").from("UserLoginPerson").where("userLoginId", owner).queryOne();
				if (UtilValidate.isNotEmpty(userLoginPerson) && UtilValidate.isNotEmpty(userLoginPerson.getString("businessUnit"))) {
					String businessUnitId = userLoginPerson.getString("businessUnit");
					if (UtilValidate.isNotEmpty(businessUnitId)) {
						GenericValue productStoreGroup = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", businessUnitId).queryOne();
						if (UtilValidate.isNotEmpty(productStoreGroup)) {
							Map<String, Object> data = new HashMap<String, Object>();
							if (UtilValidate.isNotEmpty(productStoreGroup)) {
								String businessId = productStoreGroup.getString("productStoreGroupId");
								String businessunitName = productStoreGroup.getString("productStoreGroupName");
								data.put("businessId", businessId);
								data.put("businessunitName", businessunitName);
							}
							results.add(data);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String createNotesAndAttachment(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String noteName = (String) context.get("noteName");
		String noteInfo = (String) context.get("noteInfo");
		String srNumber = (String) context.get("srNumber");
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

			if (UtilValidate.isNotEmpty(noteId) && UtilValidate.isNotEmpty(srNumber)) {
				GenericValue custRequestNote = delegator.makeValue("CustRequestNote");
				custRequestNote.set("noteId",noteId);
				custRequestNote.set("custRequestId",srNumber);
				delegator.create(custRequestNote);
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
						Debug.log("Upload file Url : " + fileUploadUrl);
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
										//client = clientBuilder.build();

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
										Debug.log("multipartEntityBuilder Part--> channel {},project_code{},doc_class{},country_code{},doc_id{},doc_name{},"
												+ " doc_size_in_bytes{},action_type {}, fileBody {} :- 02,ACS,HK_DC_ACS,HK,NA,"+ name +","+ file.length() +",uploadDocument,"+fileBody);
										Debug.log("Invoking file HK File  upload service.");
										HttpEntity reqEntity = multipartEntityBuilder.build();	        							
										postRequest.setEntity(reqEntity);   	        							
										Debug.log("Post Request : "+postRequest);
										HttpResponse fileUploadResponse = client.execute(postRequest);    	        							
										Debug.log("response Entity: " + fileUploadResponse.getEntity());
										String jsonResp = EntityUtils.toString(fileUploadResponse.getEntity());
										Debug.log("response Entity Content: " + jsonResp);

										ObjectMapper mapper = new ObjectMapper();
										Map<String, String> map = mapper.readValue(jsonResp, Map.class);
										for (Map.Entry<String,String> entry : map.entrySet()) {
											String key = entry.getKey();
											String value = entry.getValue();
											Debug.log(key + " --- " + value);						
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

										Debug.log("responseCode : {} responseMessage : {} docId : {},"+responseCode+","+ filenetResponseMessage+","+docId);
										if (responseCode != null && responseCode.equalsIgnoreCase("00")) {
											Debug.log("Document Upload Sucessfully:::uploadDocument end and docId:"+ docId);
										} else {
											Debug.log("No success code from upload service");

										}
									} catch (IOException ex) {
										Debug.logError(ex, "Exception: " + ex.getMessage(), MODULE);
										Debug.logError("Error occured while uploading file to DSS server. Error : ", ex.getMessage());
									} 
								}

							}
							if(responseCode.equalsIgnoreCase("00") && docId != null && !docId.equals("NA")){
								GenericValue noteData = delegator.findOne("NoteData",UtilMisc.toMap("noteId",noteId),false);
								if(UtilValidate.isNotEmpty(noteData)){
									String moreInfoUrl = "/bootstrap/attachments/"+name;
									//noteData.set("moreInfoUrl", moreInfoUrl);
									noteData.set("noteType", fileSource);
									noteData.set("moreInfoItemName", name);
									noteData.set("moreInfoItemId", docId);
									noteData.store();
								}
							}
						} else {
							request.setAttribute("_ERROR_MESSAGE_", "File Upload Endpoints is Empty");
							return ERROR;
						}
					} catch (Exception ex) {
						request.setAttribute("_ERROR_MESSAGE_", "File Upload Failed due to " + ex);
						Debug.logError(ex, "Exception : "+ex.getMessage(), MODULE);
						return ERROR;
					} finally {
						if (file != null && file.exists() && responseCode.equalsIgnoreCase("00")) {
							Debug.log("File is deleted once file upload sucess");
							file.delete();
						}						
						if(null != client){
							try {
								client.close();
							} catch (IOException e) {
								Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
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
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			return returnError(request, e.getMessage());
		}
		return returnSuccess(request, responseMessage);
	}

	@SuppressWarnings("unchecked")
	public static String updateServiceRequestEvent(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String fromPartyId = (String) requestParameters.get("fromPartyId");
		String cNo = (String) requestParameters.get("cNo");
		String custRequestTypeId = (String) requestParameters.get("srTypeId");
		String custRequestCategoryId =  (String) requestParameters.get("srCategoryId");
		String custRequestSubCategoryId = (String) requestParameters.get("srSubCategoryId");
		String custRequestOthCategoryId = (String) requestParameters.get("otherSrSubCategoryId");
		String priorityStr = (String) requestParameters.get("priority");
		String accountType = (String) requestParameters.get("accountType");
		String custReqOnceDone = (String) requestParameters.get("onceAndDone");
		String statusId = (String) requestParameters.get("srStatusId");
		String subStatusId = (String) requestParameters.get("srSubStatusId");
		String owner = (String) requestParameters.get("owner");
		String ownerBu = (String) requestParameters.get("ownerBu");
		String custReqSrSource = (String) requestParameters.get("srSource");
		String accountNumber = (String) requestParameters.get("accountNumber");
		String description = (String) requestParameters.get("description");
		String resolution = (String) requestParameters.get("resolution");
		String notes = (String) requestParameters.get("notes");
		String parentCustRequestId = (String) requestParameters.get("parentCustRequestId");
		//String primaryContactId = (String) requestParameters.get("ContactID");
		String alternateId = (String) requestParameters.get("alternateId");
		String nationalId = (String) requestParameters.get("nationalId");
		String custRequestId = (String) requestParameters.get("srNumber");
		String externalId= (String) requestParameters.get("externalId");
		String custOrderId = (String) requestParameters.get("orderId");
		String custRequestName = (String) requestParameters.get("srName");
		String reopenFlag = (String) requestParameters.get("reopenFlag");
		String externalLoginKey = (String) requestParameters.get("externalLoginKey");
		String clientPortal = (String) requestParameters.get("clientPortal");
		String customerId = (String) requestParameters.get("customerId");
		String contractorId = (String) requestParameters.get("contractorId");
		String salesPerson = (String) requestParameters.get("salesPerson");
		String primary = (String) requestParameters.get("primary");
		String homeType = (String) requestParameters.get("homeType");
		String primaryTechnician = (String) requestParameters.get("primaryTechnician");
		
		//Attribute Info
		String serviceFee = (String) requestParameters.get("serviceFee");
		String preFinishPlus = (String) requestParameters.get("preFinishPlus");
		//String finishType = (String) requestParameters.get("finishType");
		String vendorCode = (String) requestParameters.get("vendorCode");
		String location = (String) requestParameters.get("location");
		
		String soldByLocation = (String) requestParameters.get("soldByLocation");
		
		String finishType = (String) requestParameters.get("finishType");
		String finishColor = (String) requestParameters.get("finishColor");
		String materialCategory = (String) requestParameters.get("materialCategory");
		String materialSubCategory = (String) requestParameters.get("materialSubCategory");
		
		
		//Home Owner Contact Info
		String homePhoneNumberParam = (String) requestParameters.get("homePhoneNumber");
		String offPhoneNumberParam = (String) requestParameters.get("offPhoneNumber");;
		String mobilePhoneNumberParam = (String) requestParameters.get("mobilePhoneNumber");
		String customerPrimaryEmailParam = (String) requestParameters.get("customerPrimaryEmail");
		String addressContactMechId = (String) requestParameters.get("homeOwnerAddress");
		
		//Contractor Contact Info
		String contractorHomeNumberParam = (String) requestParameters.get("contractorHomeNumber");
		String contractorOffNumberParam = (String) requestParameters.get("contractorOffNumber");;
		String contractorMobileNumberParam = (String) requestParameters.get("contractorMobileNumber");
		String contractorPrimaryEmailParam = (String) requestParameters.get("contractorPrimaryEmail");
		
		String actualResolution = (String) requestParameters.get("actualResolution");
		
		String coordinatorDesc = (String) requestParameters.get("coordinatorDesc");
		
		String inspectedBy = (String) requestParameters.get("inspectedBy");
		String inspectionDate = (String) requestParameters.get("inspectionDate");
		String installedSquare = (String) requestParameters.get("installedSquare");
		String hasAlarmSystem = (String) requestParameters.get("hasAlarmSystem");
		String installed = (String) requestParameters.get("installed");
				
		String dealerRefNo = (String) requestParameters.get("dealerRefNo");
		
		String isProgramTemplate = (String) requestParameters.get("isProgramTemplate");
		String programTemplateId = (String) requestParameters.get("programTemplateId");
				
		List<String> reasonCodeList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(requestParameters.get("reasonCode")) && requestParameters.get("reasonCode") instanceof String) {
			String reasonCode = (String) requestParameters.get("reasonCode");
			if(UtilValidate.isNotEmpty(reasonCode) && reasonCode.contains(",")) {
				reasonCodeList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(reasonCode, ","));
			} else if(UtilValidate.isNotEmpty(reasonCode)) reasonCodeList.add(reasonCode);
		} else if(UtilValidate.isNotEmpty(requestParameters.get("reasonCode")) && requestParameters.get("reasonCode") instanceof List<?>) {
			reasonCodeList = (List<String>) requestParameters.get("reasonCode");
		}
		
		List<String> causeCategoryList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(requestParameters.get("causeCategory")) && requestParameters.get("causeCategory") instanceof String) {
			String causeCategory = (String) requestParameters.get("causeCategory");
			if(UtilValidate.isNotEmpty(causeCategory) && causeCategory.contains(",")) {
				causeCategoryList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(causeCategory, ","));
			} else if(UtilValidate.isNotEmpty(causeCategory)) causeCategoryList.add(causeCategory);
		} else if(UtilValidate.isNotEmpty(requestParameters.get("causeCategory")) && requestParameters.get("causeCategory") instanceof List<?>) {
			causeCategoryList = (List<String>) requestParameters.get("causeCategory");
		}
		

		List<String> primContactList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(requestParameters.get("ContactID")) && requestParameters.get("ContactID") instanceof String) {
			String primConId = (String) requestParameters.get("ContactID");
			if(UtilValidate.isNotEmpty(primConId) && primConId.contains(",")) {
				primContactList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(primConId, ","));
			} else if(UtilValidate.isNotEmpty(primConId)) primContactList.add(primConId);
		} else if(UtilValidate.isNotEmpty(requestParameters.get("ContactID")) && requestParameters.get("ContactID") instanceof List<?>) {
			primContactList = (List<String>) requestParameters.get("ContactID");
		}
		
		List<String> optionalAttendees = null;
		String optionalAttendeesClassName = "";
		String optionalAttendeesEmailIds = "";
		
		if (UtilValidate.isNotEmpty(requestParameters.get("optionalAttendees"))) {
			optionalAttendeesClassName = requestParameters.get("optionalAttendees").getClass().getName();
		}
		
		if (UtilValidate.isNotEmpty(optionalAttendeesClassName)
				&& "java.lang.String".equals(optionalAttendeesClassName)) {
			optionalAttendees = UtilMisc.toList((String) requestParameters.get("optionalAttendees"));
		} else if (UtilValidate.isNotEmpty(optionalAttendeesClassName)
				&& "java.util.LinkedList".equals(optionalAttendeesClassName)) {
			optionalAttendees = (List<String>) requestParameters.get("optionalAttendees");
		}
		
		if (UtilValidate.isNotEmpty(optionalAttendees)) {
			for (String eachOptionalAttendee : optionalAttendees) {
				Map<String, String> optionalAttendeeContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, eachOptionalAttendee);
				if (UtilValidate.isNotEmpty(optionalAttendeeContactInformation) && UtilValidate.isNotEmpty(optionalAttendeeContactInformation.get("EmailAddress"))){
					String optionalAttendeeEmailAddress = optionalAttendeeContactInformation.get("EmailAddress");
					optionalAttendeesEmailIds = optionalAttendeesEmailIds+optionalAttendeeEmailAddress+",";
				}
			}
			if (UtilValidate.isNotEmpty(optionalAttendeesEmailIds)) {
				optionalAttendeesEmailIds = optionalAttendeesEmailIds.substring(0, optionalAttendeesEmailIds.length()-1);
			}
		}

		try{
			
			String locationCustomFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);

			GenericValue custRequest = EntityQuery.use(delegator).select("responsiblePerson","statusId").from("CustRequest").where("custRequestId", custRequestId).queryOne();
			String previousOwnerId = custRequest.getString("responsiblePerson");
			String previousStatusId = custRequest.getString("statusId");	
			
			String allowToCloseSR = org.fio.sr.portal.event.AjaxEvents.allowToCloseSR(delegator, userLogin.getString("partyId"), custRequestId, statusId);
			if("N".equals(allowToCloseSR)){
				String errMsg = "You are not allowed to close SR's";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return ERROR;
			}
			
			List<String> srScheduledStatusesList = SrUtil.getSrScheduledStatusList(delegator, custRequestId);
			if(UtilValidate.isNotEmpty(srScheduledStatusesList) && (srScheduledStatusesList.contains(statusId) && !srScheduledStatusesList.contains(previousStatusId))){
				String allowToUpdateStatusToSchedule = org.fio.sr.portal.event.AjaxEvents.allowToUpdateSrStatusToSchedule(delegator, srScheduledStatusesList, custRequestId);
				if(UtilValidate.isNotEmpty(allowToUpdateStatusToSchedule) && "N".equals(allowToUpdateStatusToSchedule)){
					String errMsg = "Please Schedule Activities before changing the SR Status to Scheduled";
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					return ERROR;
				}
			}
			
			if(UtilValidate.isNotEmpty(cNo)){
				fromPartyId = cNo;
			}
			if (UtilValidate.isEmpty(resolution) && "SR_CLOSED".equals(statusId)) {
				String errMsg = "Resolution field is mandatory to resolve the SR";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return ERROR;
			}
			if("SR_CLOSED".equals(statusId) || "SR_CANCELLED".equals(statusId)){

				List<GenericValue> workEffortList = null;
				List<GenericValue> custRequestWorkEffortList = EntityQuery.use(delegator).select("workEffortId").from("CustRequestWorkEffort").where("custRequestId", externalId).queryList();

				if(UtilValidate.isNotEmpty(custRequestWorkEffortList)){
					List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList, "workEffortId", true);


					List<EntityCondition> conditionlist1 = FastList.newInstance();
					conditionlist1.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
					conditionlist1.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_IN, UtilMisc.toList("IA_COMPLETED", "IA_MCOMPLETED")));

					EntityCondition condition1 = EntityCondition.makeCondition(conditionlist1, EntityOperator.AND);
					workEffortList = delegator.findList("WorkEffort", condition1,null, null, null, false);


				} 
				if(UtilValidate.isNotEmpty(workEffortList)){


					String errMsg = "There are Open Activities tagged to this " +custRequestId+ ". Please close the Open Activities before closing/cancelling the SR";
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					return ERROR;
				}

			}
			Map<String, Object> custRequestContext = new HashMap<String, Object>();
			Map<String, Object> supplementoryContext = new HashMap<String, Object>();

			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String businessUnit = null;
			String teamId = null;
			String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
			Map<String, Object> custReqData = DataUtil.getCustRequestDetail(delegator, custRequestId);
			if(UtilValidate.isNotEmpty(custReqData)) {
				businessUnit = UtilValidate.isNotEmpty(custReqData.get("businessUnit")) ? (String) custReqData.get("businessUnit") : "";
				teamId = UtilValidate.isNotEmpty(custReqData.get("teamId")) ? (String) custReqData.get("teamId") : "";
			}
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId)) {
				if(UtilValidate.isEmpty(businessUnit))
					businessUnit = DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("teamId", teamId);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "Update");
				accessMatrixMap.put("entityName", "CustRequest");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
				//validate the common team and access for the assignment
				String currentPartyId = userLoginPartyId;
				if(UtilValidate.isEmpty(owner)) {
					owner = teamId;
				} else {
					currentPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
					Map<String, Object> buTeamData = DataUtil.getUserBuTeam(delegator, currentPartyId);
					businessUnit = (String) buTeamData.get("businessUnit");
					teamId = (String) buTeamData.get("emplTeamId");
				}
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				//change the access in the create 
				//check with ownerId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					if(!ownerIds.contains(teamId)) accessLevel = null;
					conditionList.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.IN, ownerIds));
					//custRequestContext.put("ownerIds", ownerIds);
				}

				//check with emplTeamId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					if(!emplTeamIds.contains(teamId)) accessLevel = null;
					conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
					//custRequestContext.put("emplTeamIds", emplTeamIds);
				}

				if(UtilValidate.isNotEmpty(custRequestId)){
					conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				}

				EntityCondition mainCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				GenericValue custRequest1 = EntityUtil
						.getFirst(delegator.findList("CustRequest", mainCondition, null, null, null, false));
				
				if(UtilValidate.isEmpty(custRequest1)) accessLevel=null;
				else {
					if(UtilValidate.isEmpty(externalId))
						externalId = custRequest1.getString("externalId");
				}
			}
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				if(UtilValidate.isNotEmpty(externalId)){
					custRequestContext.put("externalId", externalId);
				}
				if(UtilValidate.isNotEmpty(custRequestTypeId)){
					custRequestContext.put("custRequestTypeId", custRequestTypeId);
				}
				if(UtilValidate.isNotEmpty(custRequestCategoryId)){
					custRequestContext.put("custRequestCategoryId", custRequestCategoryId);
				}
				if(UtilValidate.isNotEmpty(custRequestSubCategoryId)){
					custRequestContext.put("custRequestSubCategoryId", custRequestSubCategoryId);
				}
				if(UtilValidate.isNotEmpty(custRequestOthCategoryId)){
					custRequestContext.put("custRequestOthCategoryId", custRequestOthCategoryId);
				}
				if(UtilValidate.isNotEmpty(priorityStr)){
					//long  priority = (Long.parseLong(priorityStr));
					custRequestContext.put("priority", priorityStr);
				}
				if(UtilValidate.isNotEmpty(statusId)){
					custRequestContext.put("statusId", statusId);
				}
				if(UtilValidate.isNotEmpty(subStatusId)){
					custRequestContext.put("subStatusId", subStatusId);
				}
				if(UtilValidate.isNotEmpty(owner)){
					custRequestContext.put("responsiblePerson", owner);
				}
				if(UtilValidate.isNotEmpty(parentCustRequestId)){
					custRequestContext.put("parentCustRequestId", parentCustRequestId);
				}
				custRequestContext.put("ownerBu", businessUnit);
				custRequestContext.put("emplTeamId", teamId);
				/*
        	if(UtilValidate.isNotEmpty(owner)){
        		GenericValue userLoginPerson = EntityQuery.use(delegator).select("userLoginId","partyId","emplTeamId","businessUnit").from("UserLoginPerson").where("userLoginId", owner).queryOne();
        		if (UtilValidate.isNotEmpty(userLoginPerson) && UtilValidate.isNotEmpty(userLoginPerson.getString("emplTeamId")) && UtilValidate.isNotEmpty(userLoginPerson.getString("businessUnit"))) {    
        			String emplTeamId = userLoginPerson.getString("emplTeamId");
        			String businessUnit = userLoginPerson.getString("businessUnit");
        			if(UtilValidate.isNotEmpty(emplTeamId)) {
        				custRequestContext.put("emplTeamId", emplTeamId);
        			}
        			if(UtilValidate.isNotEmpty(businessUnit)) {
        				custRequestContext.put("ownerBu", businessUnit);
        			}
        		}
	        	}*/
				if(UtilValidate.isNotEmpty(custReqSrSource)){
					custRequestContext.put("custReqSrSource", custReqSrSource);
				}
				if(UtilValidate.isNotEmpty(description)){
					String tsmRoleTypeId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "tsm.roleTypeId", delegator);
					boolean isTsmUserLoggedIn = UtilValidate.isNotEmpty(PartyHelper.getFirstValidRoleTypeId(userLoginPartyId, UtilMisc.toList(tsmRoleTypeId), delegator)) ? true : false;
					if (isTsmUserLoggedIn) {
						custRequestContext.put("tsmDescription", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
					} //else {
						custRequestContext.put("description", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
					//}
				}
				if(UtilValidate.isNotEmpty(resolution)){
					custRequestContext.put("resolution", Base64.getEncoder().encodeToString(resolution.getBytes("utf-8")));
				}
				if(UtilValidate.isNotEmpty(fromPartyId)){
					custRequestContext.put("fromPartyId", fromPartyId);
				}
				if(UtilValidate.isNotEmpty(custReqOnceDone)){
					custRequestContext.put("custReqOnceDone", custReqOnceDone);
				}
				if(UtilValidate.isNotEmpty(custReqOnceDone) && "Y".equals(custReqOnceDone)){
					custRequestContext.put("statusId", "SR_CLOSED");
				}
				if(UtilValidate.isNotEmpty(custOrderId)){
					custRequestContext.put("custOrderId", custOrderId);
				}
				if(UtilValidate.isNotEmpty(custRequestName)){
					custRequestContext.put("custRequestName", custRequestName);
				}
				if(UtilValidate.isNotEmpty(accountType)){
					supplementoryContext.put("accountType", accountType);
				}
				if(UtilValidate.isNotEmpty(accountNumber)){
					supplementoryContext.put("accountNumber", accountNumber);
				}
				if(UtilValidate.isNotEmpty(reopenFlag) && "Y".equals(reopenFlag)){
					custRequestContext.put("reopenFlag", reopenFlag); 
				}
				if(UtilValidate.isNotEmpty(customerPrimaryEmailParam)){
					custRequestContext.put("emailAddress", customerPrimaryEmailParam); 
				}
				if(UtilValidate.isNotEmpty(actualResolution)){
					custRequestContext.put("actualResolution", Base64.getEncoder().encodeToString(actualResolution.getBytes("utf-8")));
				}
				if(UtilValidate.isNotEmpty(homeType)){
					custRequestContext.put("homeType", homeType); 
				}
				
				// prepare postal address params [start]
				supplementoryContext.put("pstlAttnName", requestParameters.get("generalAttnName"));
				supplementoryContext.put("pstlAddress1", requestParameters.get("generalAddress1"));
				supplementoryContext.put("pstlAddress2", requestParameters.get("generalAddress2"));
				supplementoryContext.put("pstlPostalCode", requestParameters.get("generalPostalCode"));
				supplementoryContext.put("pstlPostalCodeExt", requestParameters.get("generalPostalCodeExt"));
				supplementoryContext.put("pstlPostalCity", requestParameters.get("generalCity"));
				supplementoryContext.put("pstlStateProvinceGeoId", requestParameters.get("generalStateProvinceGeoId"));
				supplementoryContext.put("pstlCountryGeoId", requestParameters.get("generalCountryGeoId"));
				supplementoryContext.put("pstlCountyGeoId", requestParameters.get("countyGeoId"));
				supplementoryContext.put("isBusiness", requestParameters.get("isBusiness"));
				supplementoryContext.put("isVacant", requestParameters.get("isVacant"));
				supplementoryContext.put("isUspsAddrVerified", requestParameters.get("isUspsAddrVerified"));
				supplementoryContext.put("homePhoneNumber", requestParameters.get("homePhoneNumber"));
				supplementoryContext.put("offPhoneNumber", requestParameters.get("offPhoneNumber"));
				supplementoryContext.put("mobileNumber", requestParameters.get("mobilePhoneNumber"));
				supplementoryContext.put("contractorOffPhone", requestParameters.get("contractorOffNumber"));
				supplementoryContext.put("contractorMobilePhone", requestParameters.get("contractorMobileNumber"));
				supplementoryContext.put("contractorHomePhone", requestParameters.get("contractorHomeNumber"));
				supplementoryContext.put("contractorEmail", requestParameters.get("contractorPrimaryEmail"));
				
				String isUspsRequired = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_USPS_ADDRACT");
				if(UtilValidate.isNotEmpty(isUspsRequired) && isUspsRequired.equals("Y") && UtilValidate.isEmpty(reopenFlag)) {
					Map<String, Object> corodinate = DataHelper.getGeoCoordinateByGoogleApi(UtilMisc.toMap("delegator", delegator, "dispatcher", dispatcher, "userLogin", userLogin, "zip5", (String) requestParameters.get("generalPostalCode"), "zip4", (String) requestParameters.get("generalPostalCodeExt"), "city", requestParameters.get("generalCity"), "state", requestParameters.get("generalStateProvinceGeoId"), "county", requestParameters.get("countyGeoId"), "address1", (String) requestParameters.get("generalAddress1"), "address2", (String) requestParameters.get("generalAddress2"), "country", (String) requestParameters.get("generalCountryGeoId")));
					supplementoryContext.put("latitude", corodinate.get("latitude"));
					supplementoryContext.put("longitude", corodinate.get("longitude"));
				}
				
				// varify to create party postal [start]
				if(UtilValidate.isEmpty(reopenFlag) || !("Y".equals(reopenFlag))) {
					String postalPartyId = UtilValidate.isNotEmpty(customerId) ? customerId : contractorId;
					if (UtilValidate.isNotEmpty(primary) && primary.equals("CONTRACTOR")) {
						postalPartyId = contractorId;
					}
					
					String contactMechId = UtilContactMech.evalutePartyPostal(UtilMisc.toMap("delegator", delegator, "dispatcher", dispatcher, "userLogin", userLogin
							, "partyId", postalPartyId
							, "address1", (String) requestParameters.get("generalAddress1"), "address2", (String) requestParameters.get("generalAddress2")
							, "countryGeoId", (String) requestParameters.get("generalCountryGeoId"), "stateGeoId", (String) requestParameters.get("generalStateProvinceGeoId")
							, "city", (String) requestParameters.get("generalCity"), "county", (String) requestParameters.get("countyGeoId")
							, "zip5", (String) requestParameters.get("generalPostalCode"), "zip4", (String) requestParameters.get("generalPostalCodeExt")
							, "isBusiness", (String) requestParameters.get("isBusiness"), "isVacant", (String) requestParameters.get("isVacant"), "isUspsAddrVerified", (String) requestParameters.get("isUspsAddrVerified")
							, "latitude", (String) supplementoryContext.get("latitude"), "longitude", (String) supplementoryContext.get("longitude")
							, "addressContactMechId",addressContactMechId,"isFullAddressCheck","N"
							));
					// varify to create party postal [end]
					
					addressContactMechId = UtilValidate.isNotEmpty(contactMechId) ? contactMechId : addressContactMechId;
					
					// prepare postal address params [end]
					
					supplementoryContext.put("descriptionRawTxt", description);
					supplementoryContext.put("resolutionRawTxt", resolution);
					
					if (UtilValidate.isNotEmpty(customerId)) {
						Map<String, Object> homeOwnerContactInfoContext = new HashMap<String, Object>();
						homeOwnerContactInfoContext.put("customerId", customerId);
						homeOwnerContactInfoContext.put("homePhoneNumberParam", homePhoneNumberParam);
						homeOwnerContactInfoContext.put("offPhoneNumberParam", offPhoneNumberParam);
						homeOwnerContactInfoContext.put("mobilePhoneNumberParam", mobilePhoneNumberParam);
						homeOwnerContactInfoContext.put("primaryEmailParam", customerPrimaryEmailParam);

						Map<String, Object> homeOwnerContactInputMap = new HashMap<String, Object>();
						homeOwnerContactInputMap.put("contactInformationContext", homeOwnerContactInfoContext);
						homeOwnerContactInputMap.put("userLogin", userLogin);
						
						Map<String, Object> homeOwnerContactResultsMap = dispatcher.runSync("srPortal.validateSrContactInformation", homeOwnerContactInputMap);
						
						if(!ServiceUtil.isSuccess(homeOwnerContactResultsMap)) {
							request.setAttribute("_ERROR_MESSAGE_", "Problem While Validating Home Owner Contact Information");
							return ERROR;
						}
					}
					
					if (UtilValidate.isNotEmpty(contractorId)) {
						Map<String, Object> contractorContactInfoContext = new HashMap<String, Object>();
						contractorContactInfoContext.put("customerId", contractorId);
						contractorContactInfoContext.put("homePhoneNumberParam", contractorHomeNumberParam);
						contractorContactInfoContext.put("offPhoneNumberParam", contractorOffNumberParam);
						contractorContactInfoContext.put("mobilePhoneNumberParam", contractorMobileNumberParam);
						contractorContactInfoContext.put("primaryEmailParam", contractorPrimaryEmailParam);

						Map<String, Object> contractorContactInputMap = new HashMap<String, Object>();
						contractorContactInputMap.put("contactInformationContext", contractorContactInfoContext);
						contractorContactInputMap.put("userLogin", userLogin);
						
						Map<String, Object> contractorContactResultsMap = dispatcher.runSync("srPortal.validateSrContactInformation", contractorContactInputMap);
						
						if(!ServiceUtil.isSuccess(contractorContactResultsMap)) {
							request.setAttribute("_ERROR_MESSAGE_", "Problem While Validating Contractor Contact Information");
							return ERROR;
						}
					}
				}
				
				custRequestContext.put("request", request);
				
				Map<String, Object> inputMap = new HashMap<String, Object>();
				inputMap.put("custRequestContext", custRequestContext);
				inputMap.put("supplementoryContext", supplementoryContext);
				inputMap.put("userLogin", userLogin);
				
				Map<String, Object> outMap = dispatcher.runSync("crmPortal.updateServiceRequest", inputMap);
				
				if(!ServiceUtil.isSuccess(outMap)) {
					request.setAttribute("_ERROR_MESSAGE_", "Problem While Updating Service Request");
					return ERROR;
				}
				
				custRequestId = (String) outMap.get("custRequestId");
				if(UtilValidate.isEmpty(custRequestId) || custRequestId == null){
					String errMsg = "Problem While Updating Service Request ";
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					return ERROR;
				} 

				if (UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(supplementoryContext) && UtilValidate.isEmpty(reopenFlag)) {
					// supplementory [start]
					GenericValue supplementory = EntityUtil.getFirst(delegator.findByAnd("CustRequestSupplementory",
							UtilMisc.toMap("custRequestId", custRequestId), null, false));
					if (UtilValidate.isNotEmpty(supplementory)) {
						Set<String> keys = new LinkedHashSet<String>();
						keys.addAll(supplementoryContext.keySet());
						for (String key : keys) {
							if (UtilValidate.isEmpty(supplementoryContext.get(key))) {
								supplementoryContext.remove(key);
								supplementoryContext.put(key, supplementory.get(key));
							}
						}
						
						// originally update the actual address for FSR
						/*supplementoryContext.put("pstlAttnName", requestParameters.get("generalAttnName"));
						supplementoryContext.put("pstlAddress1", requestParameters.get("generalAddress1"));
						supplementoryContext.put("pstlAddress2", requestParameters.get("generalAddress2"));
						supplementoryContext.put("pstlPostalCode", requestParameters.get("generalPostalCode"));
						supplementoryContext.put("pstlPostalCodeExt", requestParameters.get("generalPostalCodeExt"));
						supplementoryContext.put("pstlPostalCity", requestParameters.get("generalCity"));
						supplementoryContext.put("pstlStateProvinceGeoId", requestParameters.get("generalStateProvinceGeoId"));
						supplementoryContext.put("pstlCountryGeoId", requestParameters.get("generalCountryGeoId"));
						supplementoryContext.put("pstlCountyGeoId", requestParameters.get("countyGeoId"));*/
						
						supplementory.putAll(supplementoryContext);
						supplementory.store();
					}
					// supplementory [end]
				}

				if (UtilValidate.isNotEmpty(custRequestId)) {
					
					if (UtilValidate.isNotEmpty(custRequestId)) {
						request.setAttribute("custRequestId", custRequestId);
						Map<String, Object> historyInputMap = new HashMap<String, Object>();
						historyInputMap.put("custRequestId", custRequestId);
						historyInputMap.put("userLogin", userLogin);
						
						Map<String, Object> historyOutMap = dispatcher.runSync("srPortal.createSrHistory", historyInputMap);
						//String serviceResult = createSrHistory(request,response);

						if(!ServiceUtil.isSuccess(historyOutMap)) {
							request.setAttribute("_ERROR_MESSAGE_", "Problem While Creating Service Request History");
							return ERROR;
						}
					}
					
					if(UtilValidate.isNotEmpty(primContactList)) {
						List<EntityCondition> exprList1 = new LinkedList<EntityCondition>();
						exprList1.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
						exprList1.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
						exprList1.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
						exprList1.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

						EntityConditionList<EntityCondition> assocExprList = EntityCondition.makeCondition(exprList1, EntityOperator.AND);
						List<GenericValue> custReqContactList = EntityQuery.use(delegator).from("CustRequestContact").where(assocExprList).orderBy("fromDate").filterByDate().queryList();
						if(UtilValidate.isNotEmpty(custReqContactList)) {
							//List<String> existingContatList = EntityUtil.getFieldListFromEntityList(custReqContactList, "partyId", true);
							for(GenericValue custReqContact : custReqContactList) {
								String partyId = custReqContact.getString("partyId");
								if(primContactList.contains(partyId)) {
									primContactList.remove(partyId);
									continue;
								}
								custReqContact.put("isPrimary","");
								custReqContact.put("thruDate",UtilDateTime.nowTimestamp());
								custReqContact.store();
								
								GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", "CONTACT","partyId",partyId).queryFirst();
								if(UtilValidate.isNotEmpty(custRequestParty)) {
									custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
									custRequestParty.store();
								}
								
							}
						}
						for(String primContactId : primContactList) {
							GenericValue custRequestContact = delegator.makeValue("CustRequestContact");
							custRequestContact.set("custRequestId",custRequestId);
							custRequestContact.set("partyId", primContactId);
							custRequestContact.set("roleTypeId", "CONTACT");
							custRequestContact.set("isPrimary", "Y");

							custRequestContact.set("fromDate", UtilDateTime.nowTimestamp());

							delegator.create(custRequestContact);
						}
					}
					
					
					
					/*
					if (UtilValidate.isNotEmpty(primaryContactId)) {
						
						List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
						conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
						conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, primaryContactId));
						conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
						conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
						conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
						EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

						GenericValue primaryCustRequestContact = EntityUtil.getFirst( delegator.findList("CustRequestContact", mainConditons, null, null, null, false) );

						if (UtilValidate.isEmpty(primaryCustRequestContact)){

							List<EntityCondition> exprList = new LinkedList<EntityCondition>();
							exprList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
							exprList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
							exprList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
							exprList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

							EntityConditionList<EntityCondition> assocExprList = EntityCondition.makeCondition(exprList, EntityOperator.AND);
							List<GenericValue> relatedAssocs = EntityQuery.use(delegator).from("CustRequestContact").where(assocExprList).orderBy("fromDate").filterByDate().queryList();
							if (UtilValidate.isNotEmpty(relatedAssocs)){

								GenericValue existingPriContactGv = EntityUtil.getFirst(relatedAssocs);
								existingPriContactGv.set("isPrimary","");
								existingPriContactGv.store();
							}
							/*if (UtilValidate.isNotEmpty(relatedAssocs)){
        					for (GenericValue val : relatedAssocs) {
        						val.set("thruDate", UtilDateTime.nowTimestamp());
        						val.store();
        					}
        				}*



							GenericValue custRequestContact = delegator.makeValue("CustRequestContact");
							custRequestContact.set("custRequestId",custRequestId);
							custRequestContact.set("partyId", primaryContactId);
							custRequestContact.set("roleTypeId", "CONTACT");
							custRequestContact.set("isPrimary", "Y");

							custRequestContact.set("fromDate", UtilDateTime.nowTimestamp());

							delegator.create(custRequestContact);

						}else{
							primaryCustRequestContact.put("isPrimary","");
							primaryCustRequestContact.put("thruDate",UtilDateTime.nowTimestamp());
							primaryCustRequestContact.store();        				

							List<EntityCondition> exprList1 = new LinkedList<EntityCondition>();
							exprList1.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
							exprList1.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
							exprList1.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
							exprList1.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

							EntityConditionList<EntityCondition> assocExprList = EntityCondition.makeCondition(exprList1, EntityOperator.AND);
							List<GenericValue> relatedAssocs1 = EntityQuery.use(delegator).from("CustRequestContact").where(assocExprList).orderBy("fromDate").filterByDate().queryList();
							if (UtilValidate.isNotEmpty(relatedAssocs1)){

								GenericValue existingPriContactGv1 = EntityUtil.getFirst(relatedAssocs1);
								existingPriContactGv1.set("isPrimary","");
								existingPriContactGv1.store();
							}


							GenericValue custRequestContactNew = delegator.makeValue("CustRequestContact");
							custRequestContactNew.set("custRequestId",custRequestId);
							custRequestContactNew.set("partyId", primaryContactId);
							custRequestContactNew.set("roleTypeId", "CONTACT");
							custRequestContactNew.set("isPrimary", "Y");

							custRequestContactNew.set("fromDate", UtilDateTime.nowTimestamp());

							delegator.create(custRequestContactNew);


						}
					}
					
					*/
					Debug.log("reopenFlag=========="+reopenFlag);
					if(!"SR_CLOSED".equals(statusId)){
						//To store the Optional Contacts of and SR in custRequestContact
						Debug.log("optionalAttendees=========="+optionalAttendees);
						if(UtilValidate.isNotEmpty(optionalAttendees)) {
							List<EntityCondition> optionalAttendeesConditionList = new LinkedList<EntityCondition>();
							optionalAttendeesConditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
							optionalAttendeesConditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
							optionalAttendeesConditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "N"));							
							optionalAttendeesConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
							EntityCondition optionalAttendeesMainConditons = EntityCondition.makeCondition(optionalAttendeesConditionList, EntityOperator.AND);
							
							List<GenericValue> optionalAttendeesContactList = delegator.findList("CustRequestContact", optionalAttendeesMainConditons, null, null, null, false);
							
							if(UtilValidate.isNotEmpty(optionalAttendeesContactList)){
								for(GenericValue eachExtOptionalAttendee : optionalAttendeesContactList){
									eachExtOptionalAttendee.set("thruDate", UtilDateTime.nowTimestamp());
									eachExtOptionalAttendee.store();
								}
							}
							
							for(String eachOptionalAttendee : optionalAttendees){
								GenericValue custRequestContactVal = delegator.makeValue("CustRequestContact");
								custRequestContactVal.set("custRequestId",custRequestId);
								custRequestContactVal.set("partyId", eachOptionalAttendee);
								custRequestContactVal.set("roleTypeId", "CONTACT");
								custRequestContactVal.set("isPrimary", "N");
								custRequestContactVal.set("fromDate", UtilDateTime.nowTimestamp());
								delegator.create(custRequestContactVal);
							}
							
						}else{
							if (UtilValidate.isNotEmpty(reopenFlag) && !"Y".equals(reopenFlag)) {
								List<EntityCondition> optionalAttendeesConditionList = new LinkedList<EntityCondition>();
								optionalAttendeesConditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
								optionalAttendeesConditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
								optionalAttendeesConditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "N"));
								optionalAttendeesConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
								EntityCondition optionalAttendeesMainConditons = EntityCondition.makeCondition(optionalAttendeesConditionList, EntityOperator.AND);
								
								List<GenericValue> optionalAttendeesContactList = delegator.findList("CustRequestContact", optionalAttendeesMainConditons, null, null, null, false);
								
								if(UtilValidate.isNotEmpty(optionalAttendeesContactList)) {
									for(GenericValue eachExtOptionalAttendee : optionalAttendeesContactList){
										eachExtOptionalAttendee.set("thruDate", UtilDateTime.nowTimestamp());
										eachExtOptionalAttendee.store();
									}
								}
							}
						}
					}
					
					//To store the data in custRequestParty
					String existSalesPersonId = "";
					String existPrimaryTechnicianId = "";
					String existAccountId = "";
					String existPrimaryContactId = "";
					String existCustomerId = "";
					String existContractorId = "";
					String requestURI = request.getRequestURI();
					Map<String, Object> anchorPartyMap = org.fio.sr.portal.DataHelper.getCustRequestAnchorParties(delegator, custRequestId);
					if(UtilValidate.isNotEmpty(anchorPartyMap)) {
						existSalesPersonId = UtilValidate.isNotEmpty(anchorPartyMap.get("SALES_REP")) ? (String) anchorPartyMap.get("SALES_REP") : "";
						existPrimaryTechnicianId = UtilValidate.isNotEmpty(anchorPartyMap.get("TECHNICIAN")) ? (String) anchorPartyMap.get("TECHNICIAN") : "";
						existAccountId = UtilValidate.isNotEmpty(anchorPartyMap.get("ACCOUNT")) ? (String) anchorPartyMap.get("ACCOUNT") : "";
						existPrimaryContactId = UtilValidate.isNotEmpty(anchorPartyMap.get("CONTACT")) ? (String) anchorPartyMap.get("CONTACT") : "";
						existCustomerId = UtilValidate.isNotEmpty(anchorPartyMap.get("CUSTOMER")) ? (String) anchorPartyMap.get("CUSTOMER") : "";
						existContractorId = UtilValidate.isNotEmpty(anchorPartyMap.get("CONTRACTOR")) ? (String) anchorPartyMap.get("CONTRACTOR") : "";
					}
					
					if(UtilValidate.isNotEmpty(customerId)) {
						if(UtilValidate.isNotEmpty(existCustomerId) && !(existCustomerId.equals(customerId))) {
							GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", "CUSTOMER","partyId",existCustomerId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequestParty)) {
								custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
								custRequestParty.store();
							}
						}
						String customerRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, customerId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, customerId, customerRoleTypeId);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, customerId, customerRoleTypeId);
					} else if(UtilValidate.isEmpty(customerId) && requestURI.contains("/sr-portal/control/updateServiceRequest") && UtilValidate.isNotEmpty(existCustomerId)){
						GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", "CUSTOMER","partyId",existCustomerId).queryFirst();
						if(UtilValidate.isNotEmpty(custRequestParty)) {
							custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
							custRequestParty.store();
						}
						
						org.fio.sr.portal.DataHelper.removeCustRequestAnchorParty(delegator, custRequestId, existCustomerId, "CUSTOMER");
					}
					
					if(UtilValidate.isNotEmpty(fromPartyId)) {
						String fromPartyIdRole = "";
						if(UtilValidate.isNotEmpty(existAccountId) && !(existAccountId.equals(fromPartyId))) {
							fromPartyIdRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, existAccountId);
							GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", fromPartyIdRole,"partyId",existAccountId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequestParty)) {
								custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
								custRequestParty.store();
							}
						}
						fromPartyIdRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, fromPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, fromPartyId, fromPartyIdRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, fromPartyId, fromPartyIdRole);
						
					}
					
					/*
					
					if(UtilValidate.isNotEmpty(primaryContactId)) {
						
						if(UtilValidate.isNotEmpty(existPrimaryContactId) && !(existPrimaryContactId.equals(primaryContactId))) {
							GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", "CONTACT","partyId",existPrimaryContactId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequestParty)) {
								custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
								custRequestParty.store();
							}
						}
						String primContactRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, primaryContactId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, primaryContactId, primContactRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, primaryContactId, primContactRole);
						
					}
					*/
					if(UtilValidate.isNotEmpty(primContactList)) {
						if(UtilValidate.isNotEmpty(existPrimaryContactId)) {
							
							List<String> existPrimContactList = new ArrayList<>();
							if(UtilValidate.isNotEmpty(existPrimaryContactId) && existPrimaryContactId instanceof String) {
								if(UtilValidate.isNotEmpty(existPrimaryContactId) && existPrimaryContactId.contains(",")) {
									existPrimContactList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(existPrimaryContactId, ","));
								} else if(UtilValidate.isNotEmpty(existPrimaryContactId)) existPrimContactList.add(existPrimaryContactId);
							}
							for(String existPrimContactId : existPrimContactList) {
								if(!(primContactList.contains(existPrimContactId))) {
									GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", "CONTACT","partyId",existPrimContactId).queryFirst();
									if(UtilValidate.isNotEmpty(custRequestParty)) {
										custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
										custRequestParty.store();
									}
								}
							}
							
						}
						
						for(String primContactId : primContactList) {
							String primContactRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, primContactId);
							org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, primContactId, primContactRole);
						}
						String primContactIdStr = org.fio.admin.portal.util.DataUtil.listToString(primContactList, ",");
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, primContactIdStr, "CONTACT");
					}
					
					if(UtilValidate.isNotEmpty(owner)) {
						
						String existingOwner = custRequest.getString("responsiblePerson");
						if(UtilValidate.isNotEmpty(existingOwner) && !(owner.equals(existingOwner))) {
							String existingOwnerPartyId = DataUtil.getUserLoginPartyId(delegator, existingOwner);
							String existingOwnerRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, existingOwnerPartyId);
							
							GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", existingOwnerRole,"partyId",existingOwnerPartyId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequestParty)) {
								custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
								custRequestParty.store();
							}
							
							org.fio.sr.portal.DataHelper.removeCustRequestAnchorParty(delegator, custRequestId, existingOwnerPartyId, existingOwnerRole);
						}
						
						String ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
						String ownerRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, ownerPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, ownerPartyId, ownerRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, ownerPartyId, ownerRole);
					}
					/*
					if(UtilValidate.isNotEmpty(userLoginId)) {
						userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
						String userLoginRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, userLoginPartyId, userLoginRole);
					}
					*/
					if(UtilValidate.isNotEmpty(contractorId)) {
						if(UtilValidate.isNotEmpty(existContractorId) && !(existContractorId.equals(contractorId))) {
							GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", "CONTRACTOR","partyId",existContractorId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequestParty)) {
								custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
								custRequestParty.store();
							}
						}
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, contractorId, "CONTRACTOR");
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, contractorId, "CONTRACTOR");
						
						
					} else if(UtilValidate.isEmpty(contractorId) && requestURI.contains("/sr-portal/control/updateServiceRequest") && UtilValidate.isNotEmpty(existContractorId)){
						GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", "CONTRACTOR","partyId",existContractorId).queryFirst();
						if(UtilValidate.isNotEmpty(custRequestParty)) {
							custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
							custRequestParty.store();
						}
						
						org.fio.sr.portal.DataHelper.removeCustRequestAnchorParty(delegator, custRequestId, existContractorId, "CONTRACTOR");
					}
					if(UtilValidate.isNotEmpty(salesPerson)) {
						if(UtilValidate.isNotEmpty(existSalesPersonId) && !(existSalesPersonId.equals(salesPerson))) {
							GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", "SALES_REP","partyId",existSalesPersonId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequestParty)) {
								custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
								custRequestParty.store();
							}
							// String existingSalesRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, existSalesPersonId);
						}
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, salesPerson, "SALES_REP");
						//org.fio.sr.portal.DataHelper.removeCustRequestAnchorParty(delegator, custRequestId, existSalesPersonId, existingSalesRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, salesPerson, "SALES_REP");
					}
					if(UtilValidate.isNotEmpty(primaryTechnician)) {
						if(UtilValidate.isNotEmpty(existPrimaryTechnicianId) && !(existPrimaryTechnicianId.equals(primaryTechnician))) {
							GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", "TECHNICIAN","partyId",existPrimaryTechnicianId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequestParty)) {
								custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
								custRequestParty.store();
							}
							
							// send the assign email for the technician
							String rolesToSent = DataUtil.getGlobalValue(delegator, "PRIMARY_TECH_EMAIL_ROLE");
							List<String> techRoleList = new ArrayList<String>();
							if(UtilValidate.isNotEmpty(rolesToSent)) {
								if(UtilValidate.isNotEmpty(rolesToSent) && rolesToSent.contains(",")) {
									techRoleList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(rolesToSent, ","));
								} else 
									techRoleList.add(rolesToSent);
							}
							if(UtilValidate.isNotEmpty(techRoleList)) {
								boolean is3rdPartyTechnician = org.fio.admin.portal.util.DataUtil.is3rdPartyTechnician(delegator, primaryTechnician);
								for(String techRole : techRoleList) {
									boolean eligibleForEmail = false;
									if(UtilValidate.isNotEmpty(techRole) && "THIRD_PARTY_TECH".equals(techRole) && is3rdPartyTechnician) {
										eligibleForEmail = true;
									}
									if(UtilValidate.isNotEmpty(techRole) && "REEB_TECH".equals(techRole) && !(is3rdPartyTechnician)) {
										eligibleForEmail = true;
									}
									
									if(eligibleForEmail) {
										String nsender = "";
										String nto = "";
										String subject = "";
										GenericValue sytemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryOne();

										Debug.log("===== SR Primary tech EMAIL TRIGGER sytemProperty ===="+sytemProperty);

										if(UtilValidate.isNotEmpty(sytemProperty)){
											nsender = sytemProperty.getString("systemPropertyValue");
										}else{
											nsender = (String) requestParameters.get("fromEmailId");
										}

										Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,primaryTechnician);
										nto = ntoContactInformation.get("EmailAddress");

										String srEmailNotify = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_EMAIL_NOTIFIY", "Y");

										if (UtilValidate.isNotEmpty(nsender) && UtilValidate.isNotEmpty(nto) && "Y".equals(srEmailNotify)){

											String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "PRIMARY_TECH_EMAIL_TEMPLATE");

											if(UtilValidate.isNotEmpty(templateId)) {

												GenericValue emailTemlateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",templateId), false);

												String emailContent = "";
												String templateFormContent = emailTemlateData.getString("templateFormContent");
												if (UtilValidate.isNotEmpty(templateFormContent)) {
													if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
														templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
													}
												}

												if(UtilValidate.isNotEmpty(emailTemlateData.getString("subject"))) {
													subject = "FSR# "+custRequestId+" ("+custRequestName+") - "+emailTemlateData.getString("subject");
												}

												// prepare email content [start]
												Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
												extractContext.put("delegator", delegator);
												extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
												extractContext.put("fromEmail", nsender);
												extractContext.put("toEmail", nto);
												extractContext.put("partyId", primaryTechnician);
												extractContext.put("custRequestId", custRequestId);
												extractContext.put("emailContent", templateFormContent);
												extractContext.put("templateId", templateId);

												Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
												emailContent = (String) extractResultContext.get("emailContent");
												// prepare email content [end]

												Map<String, Object> callCtxt = FastMap.newInstance();
												Map<String, Object> callResult = FastMap.newInstance();
												Map<String, Object> requestContext = FastMap.newInstance();

												requestContext.put("nsender", nsender);
												requestContext.put("nto", nto);
												requestContext.put("subject", subject);
												requestContext.put("emailContent", emailContent);
												requestContext.put("templateId", templateId);

												callCtxt.put("requestContext", requestContext);
												callCtxt.put("userLogin", userLogin);

												Debug.log("===== SR Primary tech EMAIL TRIGGER ===="+callCtxt);

												callResult = dispatcher.runSync("common.sendEmail", callCtxt);
												if (ServiceUtil.isError(callResult)) {
													String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
													return returnError(request, errMsg);
												}

											}
										}
									}
								}
							}
						}
						String primaryTechnicianRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, primaryTechnician);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, primaryTechnician, primaryTechnicianRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, primaryTechnician, primaryTechnicianRole);
					}
					
					
					
					if(UtilValidate.isNotEmpty(reasonCodeList)) {
						EntityCondition resonCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "REASON_CODE"),
								EntityCondition.makeCondition("reasonId", EntityOperator.NOT_IN, reasonCodeList));
						delegator.removeByCondition("CustRequestResolution", resonCondition);
						for(String reasonCode : reasonCodeList) {
							org.fio.sr.portal.DataHelper.createCustReqResolution(delegator, UtilMisc.toMap("custRequestId", custRequestId, "custRequestTypeId", SrResolutionConstant.REASON_CODE,"reasonId", reasonCode));
						}
					} else {
						EntityCondition resonCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "REASON_CODE"));
						delegator.removeByCondition("CustRequestResolution", resonCondition);
					}
					
					if(UtilValidate.isNotEmpty(causeCategoryList)) {
						EntityCondition causeCatCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "CAUSE_CATEGORY"),
								EntityCondition.makeCondition("causeCategoryId", EntityOperator.NOT_IN, causeCategoryList));
						delegator.removeByCondition("CustRequestResolution", causeCatCondition);
						for(String causeCategory : causeCategoryList) {
							org.fio.sr.portal.DataHelper.createCustReqResolution(delegator, UtilMisc.toMap("custRequestId", custRequestId, "custRequestTypeId", SrResolutionConstant.CAUSE_CATEGORY, "causeCategoryId", causeCategory));
						}
					}  else {
						EntityCondition causeCatCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "CAUSE_CATEGORY"));
						delegator.removeByCondition("CustRequestResolution", causeCatCondition);
					}

					if (UtilValidate.isNotEmpty(alternateId)) {
						GenericValue partyIdentification = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", fromPartyId));
						partyIdentification.put("partyIdentificationTypeId", "ALTERNATE_ID");
						partyIdentification.put("idValue", alternateId);
						partyIdentification.store();
					}

					if (UtilValidate.isNotEmpty(nationalId)) {
						GenericValue partyIdentification = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", fromPartyId));
						partyIdentification.put("partyIdentificationTypeId", "NATIONAL_ID");
						partyIdentification.put("idValue", nationalId);
						partyIdentification.store();
					}
					
					if (UtilValidate.isNotEmpty(primary)) {
						EntityCondition rolecondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "PRIMARY"));

						 List<GenericValue> custRequestAttr = delegator.findList("CustRequestAttribute", rolecondition, null, null, null, false);
						 delegator.removeAll(custRequestAttr);
						 GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "PRIMARY");
							custRequestAttrbute.set("attrValue", primary);
							delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(serviceFee)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "SERVICE_FEE", "Service for a Fee");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "SERVICE_GROUP", customFieldName);
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", serviceFee);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", customFieldId);
							custRequestAttrbute.set("attrValue", serviceFee);
							delegator.create(custRequestAttrbute);
						}
					}
					/*
					if (UtilValidate.isNotEmpty(finishType)) {
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","10144"), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", finishType);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "10144");
							custRequestAttrbute.set("attrValue", finishType);
							delegator.create(custRequestAttrbute);
						}
					} */
					if (UtilValidate.isNotEmpty(vendorCode)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "VENDOR_CODE", "Vendor Code");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "VENDOR_GROUP", customFieldName);
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", vendorCode);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", customFieldId);
							custRequestAttrbute.set("attrValue", vendorCode);
							delegator.create(custRequestAttrbute);
						}
					}
					if (UtilValidate.isNotEmpty(location)) {	
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",locationCustomFieldId), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", location);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", locationCustomFieldId);
							custRequestAttrbute.set("attrValue", location);
							delegator.create(custRequestAttrbute);
						}
					}			
					
					if (UtilValidate.isNotEmpty(soldByLocation)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "SOLD_BY_LOCATION", "Sold By Location");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "CUSTOMER_GRP", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", soldByLocation);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", soldByLocation);
								delegator.create(custRequestAttrbute);
							}
						}
					}
					if (UtilValidate.isNotEmpty(preFinishPlus)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "PRO_FINISH_PLUS", "Pro Finish Plus");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "FINISH_GROUP", customFieldName);
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", preFinishPlus);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", customFieldId);
							custRequestAttrbute.set("attrValue", preFinishPlus);
							delegator.create(custRequestAttrbute);
						}
					}
					if(UtilValidate.isNotEmpty(addressContactMechId)) {
						GenericValue custAttGv1 = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","RECOMM_ADDRESS"), false);
						if(UtilValidate.isNotEmpty(custAttGv1)){
							custAttGv1.put("attrValue", addressContactMechId);
							custAttGv1.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "RECOMM_ADDRESS");
							custRequestAttrbute.set("attrValue", addressContactMechId);
							delegator.create(custRequestAttrbute);
						}
					}
					
					if (UtilValidate.isNotEmpty(finishType)) {
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","FSR_FINISH_TYPE"), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", finishType);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "FSR_FINISH_TYPE");
							custRequestAttrbute.set("attrValue", finishType);
							delegator.create(custRequestAttrbute);
						}
					} else {
						//if we want to remove in case of value removed from UI
						
					}
					
					if (UtilValidate.isNotEmpty(finishColor)) {	
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","FSR_FINISH_COLOR"), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", finishColor);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "FSR_FINISH_COLOR");
							custRequestAttrbute.set("attrValue", finishColor);
							delegator.create(custRequestAttrbute);
						}
						// segmentation
						/*
						GenericValue segValueAssoc = EntityQuery.use(delegator).from("SegmentationValueAssoc").where("groupId", finishType,"customFieldId", finishColor, "domainEntityId", custRequestId, "domainEntityType", "SERVICE_REQUEST").queryFirst();
						if(UtilValidate.isNotEmpty(segValueAssoc)) {
							segValueAssoc.set("customFieldId", finishColor);
							segValueAssoc.store();
						} else {
							GenericValue customFieldGroup = EntityQuery.use(delegator).from("CustomFieldGroup").where("groupId", finishType,"groupType","SEGMENTATION").queryFirst();
							
							GenericValue segmentationValueAssoc = delegator.makeValidValue("SegmentationValueAssoc");
							segmentationValueAssoc.set("groupId", finishType);
							segmentationValueAssoc.set("customFieldId", finishColor);
							segmentationValueAssoc.set("domainEntityId", custRequestId);
							segmentationValueAssoc.set("domainEntityType", "SERVICE_REQUEST");
							segmentationValueAssoc.set("groupActualValue", UtilValidate.isNotEmpty(customFieldGroup) ? customFieldGroup.getString("groupName") : "" );
							segmentationValueAssoc.create();
						}*/
						
						
					} else {
						//if we want to remove in case of value removed from UI
						
					}
					
					if (UtilValidate.isNotEmpty(materialCategory)) {
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","FSR_MATERIAL_CATEGROY"), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", materialCategory);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "FSR_MATERIAL_CATEGROY");
							custRequestAttrbute.set("attrValue", materialCategory);
							delegator.create(custRequestAttrbute);
						}
					} else {
						//if we want to remove in case of value removed from UI
						
					}
					
					if (UtilValidate.isNotEmpty(materialSubCategory)) {
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","FSR_MATERIAL_SUB_CATEGROY"), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", materialSubCategory);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "FSR_MATERIAL_SUB_CATEGROY");
							custRequestAttrbute.set("attrValue", materialSubCategory);
							delegator.create(custRequestAttrbute);
						}
						
						// segmentation
						/*
						GenericValue segValueAssoc = EntityQuery.use(delegator).from("SegmentationValueAssoc").where("groupId", materialCategory,"customFieldId", materialSubCategory, "domainEntityId", custRequestId, "domainEntityType", "SERVICE_REQUEST").queryFirst();
						if(UtilValidate.isNotEmpty(segValueAssoc)) {
							GenericValue customFieldGroup = EntityQuery.use(delegator).from("CustomFieldGroup").where("groupId", materialCategory,"groupType","SEGMENTATION").queryFirst();
							segValueAssoc.set("groupId", materialCategory);
							segValueAssoc.set("customFieldId", materialSubCategory);
							segValueAssoc.set("groupActualValue", UtilValidate.isNotEmpty(customFieldGroup) ? customFieldGroup.getString("groupName") : "" );
							segValueAssoc.store();
						} else {
							GenericValue customFieldGroup = EntityQuery.use(delegator).from("CustomFieldGroup").where("groupId", materialCategory,"groupType","SEGMENTATION").queryFirst();
							
							GenericValue segmentationValueAssoc = delegator.makeValidValue("SegmentationValueAssoc");
							segmentationValueAssoc.set("groupId", materialCategory);
							segmentationValueAssoc.set("customFieldId", materialSubCategory);
							segmentationValueAssoc.set("domainEntityId", custRequestId);
							segmentationValueAssoc.set("domainEntityType", "SERVICE_REQUEST");
							segmentationValueAssoc.set("groupActualValue", UtilValidate.isNotEmpty(customFieldGroup) ? customFieldGroup.getString("groupName") : "" );
							segmentationValueAssoc.create();
						} */
						
					} else {
						//if we want to remove in case of value removed from UI
						
					}
					
					if (UtilValidate.isNotEmpty(coordinatorDesc)) {
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","CSR_DESC"), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", coordinatorDesc);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "CSR_DESC");
							custRequestAttrbute.set("attrValue", coordinatorDesc);
							custRequestAttrbute.create();
						}
					} else {
						//if we want to remove in case of value removed from UI
						
					}
					
					if (UtilValidate.isNotEmpty(inspectedBy)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTED_BY", "Inspected By");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", inspectedBy);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", inspectedBy);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						/*
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTED_BY", "Inspected By");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}*/
					}
					
					if (UtilValidate.isNotEmpty(inspectionDate)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTION_DATE", "Inspection Date");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", inspectionDate);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", inspectionDate);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						/*
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTION_DATE", "Inspection Date");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}
						*/
					}
					
					if (UtilValidate.isNotEmpty(installedSquare)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED_SQUARE", "Installed Square, Level, and Plumb");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", installedSquare);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", installedSquare);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						/*
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED_SQUARE", "Installed Square, Level, and Plumb");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						} */
					}
					
					if (UtilValidate.isNotEmpty(hasAlarmSystem)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "HAS_ALARM_SYSTEM", "Has Alarm System");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", hasAlarmSystem);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", hasAlarmSystem);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						/*
						String customFieldName = DataUtil.getGlobalValue(delegator, "HAS_ALARM_SYSTEM", "Has Alarm System");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}*/
					}
					
					if (UtilValidate.isNotEmpty(installed)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED", "Installed");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", installed);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", installed);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						/*
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED", "Installed");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}*/
					}
					
					if (UtilValidate.isNotEmpty(dealerRefNo)) {	
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","DEALER_REF_NO"), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", dealerRefNo);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "DEALER_REF_NO");
							custRequestAttrbute.set("attrValue", dealerRefNo);
							delegator.create(custRequestAttrbute);
						}
					} else {
						
					}
					
					// Program template [start]
					UtilAttribute.storeServiceAttrValue(delegator, custRequestId, "IS_PROG_TPL", isProgramTemplate);
					UtilAttribute.storeServiceAttrValue(delegator, custRequestId, "PROG_TPL_ID", programTemplateId);
					// Program template [end]
					
					Debug.log("== UPDATE SR owner =="+owner+"== previousOwnerId =="+previousOwnerId);
					
					String nsender = "";
					String nto = "";
					String ccAddresses = "";
					String ccAdd = "";
					String subject = "FSR# "+custRequestId+" ("+custRequestName+") - FSR Notification";
					String ownerDesc = "";
					String partyDesc = "";
					String srStatusDesc = "";
					String signName = "";
					String enableEmailTrigger = "N";
					String subjectDesc = "An update has been made to the support SR:";
					
					if (UtilValidate.isNotEmpty(clientPortal) && "clientPortal".equals(clientPortal)){
						enableEmailTrigger = "Y";
						nto = (String) requestParameters.get("toEmailId");
						ownerDesc = (String) requestParameters.get("cspSupportName");
						srStatusDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId);
						
						if (UtilValidate.isNotEmpty(reopenFlag) && "Y".equals(reopenFlag)) {
							subjectDesc = "The following support SR has been reopened:";
							description = (String) requestParameters.get("selDescription");
						}
						
						if (UtilValidate.isNotEmpty(previousOwnerId)){
							ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, previousOwnerId, false);
							String responsiblePartyId = DataUtil.getUserLoginPartyId(delegator, previousOwnerId);
							Map<String, String> responsiblePersonContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,responsiblePartyId);
							ccAddresses = nto;
							nto = responsiblePersonContactInformation.get("EmailAddress");
							if(UtilValidate.isNotEmpty(optionalAttendeesEmailIds)){
								ccAddresses = ccAddresses+","+optionalAttendeesEmailIds;
							}else{
								ccAddresses = optionalAttendeesEmailIds;
							}
						}else{
							if(UtilValidate.isNotEmpty(optionalAttendeesEmailIds)){
								ccAddresses = optionalAttendeesEmailIds;
							}
						}
								
						
						Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLogin.getString("partyId"));
						//nsender = ntoContactInformation.get("EmailAddress");
						nsender = (String) requestParameters.get("fromEmailId");
						
						GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);

						if(UtilValidate.isNotEmpty(personGv)){
							signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
						}
						
						if(UtilValidate.isNotEmpty(priorityStr)){
							if ("62816".equals(priorityStr)){
								subject = "FSR# "+custRequestId+" ("+custRequestName+") - FSR Notification";
							}else if ("62817".equals(priorityStr)){
								subject = "FSR# "+custRequestId+" ("+custRequestName+") - FSR Notification";
							}else if ("62818".equals(priorityStr)){
								subject = "FSR# "+custRequestId+" ("+custRequestName+") - FSR Notification";
							}
						}
						
						partyDesc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, fromPartyId, false);
						
					}else if (UtilValidate.isNotEmpty(owner) /*&& UtilValidate.isNotEmpty(previousOwnerId) && !previousOwnerId.equals(owner)*/ && UtilValidate.isEmpty(clientPortal)) {
						enableEmailTrigger = "Y";
						if (UtilValidate.isEmpty(cNo) && UtilValidate.isNotEmpty(requestParameters.get("selFromPartyId"))){
							cNo = (String) requestParameters.get("selFromPartyId");
						}
						if (UtilValidate.isEmpty(description) && UtilValidate.isNotEmpty(requestParameters.get("selDescription"))){
							description = (String) requestParameters.get("selDescription");
						}
						partyDesc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, cNo, false);
						ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, owner, false);
						srStatusDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId);
						
						if (UtilValidate.isNotEmpty(owner) && UtilValidate.isNotEmpty(previousOwnerId) && !previousOwnerId.equals(owner))
							subject = "FSR# "+custRequestId+" ("+custRequestName+") - FSR Notification";
						else
							subject = "FSR# "+custRequestId+" ("+custRequestName+") - FSR Notification";

						nsender = (String) requestParameters.get("fromEmailId");

						GenericValue userLoginPerson = EntityUtil.getFirst(EntityQuery.use(delegator).select("userLoginId","partyId","firstName").from("UserLoginPerson").where("userLoginId", owner).queryList());
						
						if(UtilValidate.isNotEmpty(userLoginPerson)) {
							Map<String, String> backupCoordinatorEmail = SrUtil.getBackupCoordinatorInfo(delegator, userLoginPerson.getString("partyId"));
							if(UtilValidate.isNotEmpty(backupCoordinatorEmail)) {
								ccAdd = backupCoordinatorEmail.get("EmailAddress");
							}
						}
						Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLoginPerson.getString("partyId"));
						nto = ntoContactInformation.get("EmailAddress");

						//Map<String, String> previousOwnerContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,previousOwnerId);
						List<String> srAssocPartiesEmailIds = SrUtil.getSrAssocPartyEmailIds(delegator, custRequestId, statusId, primary);
						if(UtilValidate.isNotEmpty(srAssocPartiesEmailIds)){
							for(String eachAssocEmailId :srAssocPartiesEmailIds ){
								ccAddresses = ccAddresses+eachAssocEmailId+",";
							}
							ccAddresses = ccAddresses.substring(0,ccAddresses.length()-1);
							if(UtilValidate.isNotEmpty(optionalAttendeesEmailIds)){
								ccAddresses = ccAddresses+","+optionalAttendeesEmailIds;
							}
						}else{
							ccAddresses = optionalAttendeesEmailIds;
						}
						
						
						
						signName = "CRM Administrator.";
						GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);

						if(UtilValidate.isNotEmpty(personGv)){
							signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
						}
					}

					Debug.log("==== UPDATE SR nsender ===="+nsender);
					Debug.log("==== UPDATE SR nto ===="+nto);
					Debug.log("==== UPDATE SR enableEmailTrigger ===="+enableEmailTrigger);
					Debug.log("==== UPDATE SR ccAddresses ===="+ccAddresses);
					String srEmailNotify = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_EMAIL_NOTIFIY", "Y");

					if (UtilValidate.isNotEmpty(nsender) && UtilValidate.isNotEmpty(nto) && "Y".equals(enableEmailTrigger) && "Y".equals(srEmailNotify)){

						Map<String, Object> srStatusesMap = org.fio.homeapps.util.DataUtil.getSrStatusList(delegator, "SR_STATUS_ID");

						String srStatusId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_EMAIL_NOTIFICATION_STAGE");

						if(UtilValidate.isNotEmpty(srStatusId) && UtilValidate.isNotEmpty(statusId) && !(previousStatusId.equals(statusId))){
							int globalSrStatusSeq = Integer.parseInt((String)srStatusesMap.get(srStatusId));

							int srStatusSeqParam = Integer.parseInt((String)srStatusesMap.get(statusId));

							if(UtilValidate.isNotEmpty(globalSrStatusSeq) && UtilValidate.isNotEmpty(srStatusSeqParam) && srStatusSeqParam >= globalSrStatusSeq) {

								String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_TEMPLATE");
								if(UtilValidate.isNotEmpty(templateId)) {
									GenericValue emailTemlateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",templateId), false);
									String emailContent = "";
									//String templateFormContent = emailTemlateData.getString("textContent");
									String templateFormContent = emailTemlateData.getString("templateFormContent");
									if (UtilValidate.isNotEmpty(templateFormContent)) {
										if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
											templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
										}
									}
									if(UtilValidate.isNotEmpty(emailTemlateData.getString("subject"))) {
										subject = "FSR# "+custRequestId+" ("+custRequestName+") - "+emailTemlateData.getString("subject");
									}

									// prepare email content [start]
									Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
									extractContext.put("delegator", delegator);
									extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
									extractContext.put("fromEmail", nsender);
									extractContext.put("toEmail", nto);
									extractContext.put("partyId", fromPartyId);
									extractContext.put("custRequestId", custRequestId);
									extractContext.put("emailContent", templateFormContent);
									extractContext.put("templateId", templateId);

									Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
									emailContent = (String) extractResultContext.get("emailContent");

									Map<String, Object> callCtxt = FastMap.newInstance();
									Map<String, Object> callResult = FastMap.newInstance();
									Map<String, Object> requestContext = FastMap.newInstance();

									requestContext.put("nsender", nsender);
									requestContext.put("nto", nto);
									requestContext.put("subject", subject);
									requestContext.put("emailContent", emailContent);
									requestContext.put("templateId", templateId);
									requestContext.put("ccAddresses", ccAdd);
									requestContext.put("nbcc", ccAddresses);

									callCtxt.put("requestContext", requestContext);
									callCtxt.put("userLogin", userLogin);

									Debug.log("==== Update SR sendEmail ===="+callCtxt);

									callResult = dispatcher.runSync("common.sendEmail", callCtxt);
									if (ServiceUtil.isError(callResult)) {
										String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
										return returnError(request, errMsg);
									}
									
									String enableSurveyEmail = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_SURVEY_EMAIL","N");
									if(UtilValidate.isNotEmpty(enableSurveyEmail) && "Y".equals(enableSurveyEmail)) {
										String primaryPartyId = org.groupfio.common.portal.util.DataHelper.getPrimaryPerson(delegator, custRequestId);
										Map<String, String> ntoContactInformation1 = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,primaryPartyId);
										nto = UtilValidate.isNotEmpty(ntoContactInformation1) ? ntoContactInformation1.get("EmailAddress") : "";

										//Send survey email to the primary person of the SR
										String surveyTemplateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_SURVEY_TEMPLATE");
										if(UtilValidate.isNotEmpty(surveyTemplateId) && "SR_CLOSED".equals(statusId) && UtilValidate.isNotEmpty(nto) && UtilValidate.isNotEmpty(nsender)) {
											GenericValue surveyEmailTemlateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",surveyTemplateId), false);
											String surveyEmailContent = "";

											
											String surveyTemplateFormContent = surveyEmailTemlateData.getString("templateFormContent");
											if (UtilValidate.isNotEmpty(surveyTemplateFormContent)) {
												if (org.apache.commons.codec.binary.Base64.isBase64(surveyTemplateFormContent)) {
													surveyTemplateFormContent = org.ofbiz.base.util.Base64.base64Decode(surveyTemplateFormContent);
												}
											}
											if(UtilValidate.isNotEmpty(surveyEmailTemlateData.getString("subject"))) {
												subject = "FSR# "+custRequestId+" ("+custRequestName+") - "+surveyEmailTemlateData.getString("subject");
											}

											// prepare email content [start]
											extractContext = new LinkedHashMap<String, Object>();
											extractContext.put("delegator", delegator);
											extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
											extractContext.put("fromEmail", nsender);
											extractContext.put("toEmail", nto);
											extractContext.put("partyId", fromPartyId);
											extractContext.put("custRequestId", custRequestId);
											extractContext.put("emailContent", surveyTemplateFormContent);
											extractContext.put("templateId", surveyTemplateId);

											extractResultContext = ExtractFacade.extractData(extractContext);
											surveyEmailContent = (String) extractResultContext.get("emailContent");

											callCtxt = FastMap.newInstance();
											callResult = FastMap.newInstance();
											requestContext = FastMap.newInstance();

											requestContext.put("nsender", nsender);
											requestContext.put("nto", nto);
											requestContext.put("subject", subject);
											requestContext.put("emailContent", surveyEmailContent);
											requestContext.put("templateId", surveyTemplateId);

											callCtxt.put("requestContext", requestContext);
											callCtxt.put("userLogin", userLogin);

											Debug.log("==== Update SR sendEmail for survey ===="+callCtxt);

											callResult = dispatcher.runSync("common.sendEmail", callCtxt);
											if (ServiceUtil.isError(callResult)) {
												String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
												return returnError(request, errMsg);
											}
										}
									}
								}
							}
						}
					}
					
					//send sms by twilio
					String twilioSetup = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "TWILIO_SETUP", "N");
					if(UtilValidate.isNotEmpty(twilioSetup) && "Y".equals(twilioSetup)) {
						String srSMSStatusStr = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_TWILIO_SMS_NOTIFIY_STATUS");
						List<String> srSMSStatusList = UtilValidate.isNotEmpty(srSMSStatusStr) ? org.fio.admin.portal.util.DataUtil.stringToList(srSMSStatusStr, ",") : new ArrayList<String>();
						
						String twilioSenderNumber = DataUtil.getGlobalValue(delegator, "twilio-sender-phone-number");
						Map<String, Object> srPartyPhoneNumMap = new HashMap<String, Object>();
						if(UtilValidate.isNotEmpty(srSMSStatusList) && UtilValidate.isNotEmpty(statusId) && !(previousStatusId.equals(statusId)) && srSMSStatusList.contains(statusId)){							
							srPartyPhoneNumMap = SrUtil.getSrAssocPartyPhones(delegator, custRequestId, statusId, primary);
						}
						if(UtilValidate.isNotEmpty(srPartyPhoneNumMap)) {
							String applicationUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_URL");
							String smsBody = DataUtil.getGlobalValue(delegator, statusId+"_SMS_CONTENT");
							Map<String, Object> contentInfo = new HashMap<String, Object>();
							contentInfo.put("content", smsBody);
							contentInfo.put("SR_NUMBER", custRequestId);
							if(UtilValidate.isNotEmpty(applicationUrl))
								contentInfo.put("SR_SHORTURL", applicationUrl+"/sr-portal/control/viewServiceRequest?srNumber="+custRequestId);
							contentInfo.put("SR_NAME", custRequestName);
							
							smsBody = org.fio.admin.portal.util.DataUtil.extractContentWithTag(contentInfo);
							
							for(String srPartyId : srPartyPhoneNumMap.keySet()) {
								Map<String, Object> ctx = new HashMap<String, Object>();
								String phoneNumber = (String) srPartyPhoneNumMap.get(srPartyId);
								String requestData = "";
								String responseData = "";
								Debug.logInfo("smsBody----->"+smsBody, MODULE);
								boolean isSmsSend = false;
								String responseMsg = "";
								if(UtilValidate.isNotEmpty(phoneNumber) && UtilValidate.isNotEmpty(smsBody)) {
									
									Map<String, Object> ctx1 = new HashMap<String, Object>();
									ctx1.put("toPhoneNumber", UtilMisc.toList(phoneNumber));
									Map<String, Object> smsResult1 =  dispatcher.runSync("twilio.validateToNumber", ctx1);
									List<String> wrongPhoneList = new ArrayList<String>();
									if(UtilValidate.isNotEmpty(smsResult1) && UtilValidate.isNotEmpty(smsResult1.get("successMessage")) && ((String) smsResult1.get("successMessage")).contains("Error : ")) {
										smsResult1 = (Map<String, Object>) smsResult1.get("result");
									  	wrongPhoneList = (List<String>) smsResult1.get("wrongPhoneNumber");
										responseMsg = "The following to phone number is wrong : "+org.fio.admin.portal.util.DataUtil.listToString(wrongPhoneList);
										responseData = "{\"errorMessage\":\""+responseMsg+"\"}";
									}  else {
										ctx.put("toPhoneNumber", phoneNumber);
										ctx.put("fromPhoneNumber", twilioSenderNumber);
										ctx.put("subject", smsBody);
										
										requestData = org.fio.admin.portal.util.DataUtil.convertToJsonStr(ctx);
										
										Map<String, Object> smsResult =  dispatcher.runSync("twilio.sendSms", ctx);
										if( UtilValidate.isNotEmpty(smsResult)  && UtilValidate.isNotEmpty(smsResult.get("successMessage")) && ((String) smsResult.get("successMessage")).contains("Error : ")) {
											Debug.logInfo("Exception in sendSms :"+ServiceUtil.getErrorMessage(smsResult), MODULE);
											isSmsSend = false;
											smsResult = (Map<String, Object>) smsResult.get("result");
											responseData = UtilValidate.isNotEmpty(smsResult) ? (String) smsResult.get("response") : "";
											responseMsg = UtilValidate.isNotEmpty(smsResult) ? (String) smsResult.get("response") : "Error while sending sms to "+phoneNumber;
											request.setAttribute("_ERROR_MESSAGE_", responseMsg);
											return ERROR;
										} else {
											if(UtilValidate.isNotEmpty(smsResult)) {
												smsResult = (Map<String, Object>) smsResult.get("result");
												responseData = UtilValidate.isNotEmpty(smsResult) ? (String) smsResult.get("response") : "";
											}
											isSmsSend = true;
											responseMsg = "SMS has been sent successfully";
										}
									}
									
									
									
								} else {
									if(UtilValidate.isEmpty(phoneNumber))
										responseMsg = UtilValidate.isNotEmpty(responseMsg) ? " Phone number is not Exist" : "Phone number is not Exist";
									if(UtilValidate.isEmpty(smsBody))
										responseMsg = UtilValidate.isNotEmpty(responseMsg) ? " SMS content is not Exist" : "SMS content is not Exist";
								}
								
								//Store audit logs
								String twilioLogId = delegator.getNextSeqId("TwilioAuditLog");
								GenericValue twilioAuditLog = delegator.makeValue("TwilioAuditLog");
								twilioAuditLog.set("twilioLogId", twilioLogId);
								twilioAuditLog.set("typeId", "SMS");
								twilioAuditLog.set("domainEntityType", "SERVICE_REQUEST");
								twilioAuditLog.set("domainEntityId", custRequestId);
								twilioAuditLog.set("fromPhoneNumber", twilioSenderNumber);
								twilioAuditLog.set("toPhoneNumber", phoneNumber);
								twilioAuditLog.set("requestData", requestData);
								twilioAuditLog.set("responseData", responseData);
								twilioAuditLog.set("status", isSmsSend ? "SUCCESS" : "ERROR");
								twilioAuditLog.set("comments", responseMsg);
								twilioAuditLog.create();
							}
						}
					} else {
						String isEnabledsandboxMessage = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SANDBOX_MESSAGE_SETUP", "N");
						if("Y".equals(isEnabledsandboxMessage)) {
							String srSMSStatusStr = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_SMS_NOTIFIY_STATUS");
							List<String> srSMSStatusList = UtilValidate.isNotEmpty(srSMSStatusStr) ? org.fio.admin.portal.util.DataUtil.stringToList(srSMSStatusStr, ",") : new ArrayList<String>();
							
							String senderNumber = DataUtil.getGlobalValue(delegator, "sender-phone-number");
							Map<String, Object> srPartyPhoneNumMap = new HashMap<String, Object>();
							if(UtilValidate.isNotEmpty(srSMSStatusList) && UtilValidate.isNotEmpty(statusId) && !(previousStatusId.equals(statusId)) && srSMSStatusList.contains(statusId)){							
								srPartyPhoneNumMap = SrUtil.getSrAssocPartyPhones(delegator, custRequestId, statusId, primary);
							}
							if(UtilValidate.isNotEmpty(srPartyPhoneNumMap)) {
								String applicationUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_URL");
								String smsBody = DataUtil.getGlobalValue(delegator, statusId+"_SMS_CONTENT");
								
								String merchantNumber = DataUtil.getGlobalValue(delegator, "SANDBOX_SMS_MERCHANT_NUMBER");
								String storeNumber = DataUtil.getGlobalValue(delegator, "SANDBOX_SMS_STORE_NUMNER");
								long smsTriggerDelay = Long.parseLong(DataUtil.getGlobalValue(delegator, "SANDBOX_SMS_TRIGGER_DELAY", "2000"));
								
								Map<String, Object> contentInfo = new HashMap<String, Object>();
								contentInfo.put("content", smsBody);
								contentInfo.put("SR_NUMBER", custRequestId);
								
								String srTrackerUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_TRACKER_URL");
								if(UtilValidate.isNotEmpty(srTrackerUrl)){
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
	    									}
	    								} else {
	    									String encodedCustReqId = Base64.getEncoder().encodeToString(custRequestId.getBytes("utf-8"));
	    									if(UtilValidate.isNotEmpty(encodedCustReqId)) {
	    										srTrackerUrl = srTrackerUrl+"#"+encodedCustReqId;
	    									}
	    								}
									} else {
										String encodedCustReqId = Base64.getEncoder().encodeToString(custRequestId.getBytes("utf-8"));
										if(UtilValidate.isNotEmpty(encodedCustReqId)) {
											srTrackerUrl = srTrackerUrl+"#"+encodedCustReqId;
										}
									}
									
								}
								srTrackerUrl = Objects.toString(srTrackerUrl, "");
								if(UtilValidate.isNotEmpty(srTrackerUrl)) {
									contentInfo.put("SR_SHORTURL", srTrackerUrl);
									contentInfo.put("SR_TRACKER_URL", srTrackerUrl);
								}
								contentInfo.put("SR_NAME", custRequestName);
								
								smsBody = org.fio.admin.portal.util.DataUtil.extractContentWithTag(contentInfo);
								
								for(String srPartyId : srPartyPhoneNumMap.keySet()) {
									String phoneNumber = (String) srPartyPhoneNumMap.get(srPartyId);
									if(UtilValidate.isNotEmpty(phoneNumber) && UtilValidate.isNotEmpty(smsBody)) {
										Map<String, Object> msgMap = new HashMap<String, Object>();
										msgMap.put("TTMMVersion", "v1.0.0");
										msgMap.put("DateTime", UtilDateTime.nowTimestamp()+"");
										msgMap.put("MessageID", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))+":"+Math.abs(new Random().nextInt(100000))+":001:001:001:"+ LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmssSSS")) );
										
										Map<String, Object> fromMap = new HashMap<String, Object>();
										fromMap.put("MerchantNumber", merchantNumber);
										fromMap.put("StoreNumber", storeNumber);
										fromMap.put("ApplicationID", "v1.0.0");
										fromMap.put("Associate", UtilMisc.toMap("IDType","1","Id","123"));
										msgMap.put("From", fromMap);
										
										Map<String, Object> toMap = new HashMap<String, Object>();
										toMap.put("MessageDeliveryMethod", "sms");
										Map<String, Object> personMap = new HashMap<String, Object>();
										personMap.put("PersonType", "Customer");
										personMap.put("Phone", UtilMisc.toMap("PhoneType","CELL","PhoneNumber", phoneNumber));
										toMap.put("Person", personMap);
										
										msgMap.put("To", toMap);
										msgMap.put("Content", UtilMisc.toMap("Type", "TEXT","Text", smsBody));
										
										String requestData = org.fio.admin.portal.util.DataUtil.convertToJsonStr(UtilMisc.toMap("Message", msgMap));
										
										Map<String, Object> ctx = new HashMap<String, Object>();
										ctx.put("domainEntityType", "SERVICE_REQUEST");
										ctx.put("domainEntityId", custRequestId);
										ctx.put("requestData", requestData);
										
										Map<String, Object> smsResult =  dispatcher.runSync("ap.sendSandboxSms", UtilMisc.toMap("requestContext",ctx));
										Map<String, Object> resultData = (Map<String, Object>) smsResult.get("results");
										Debug.logInfo(" SMS Response : "+ (UtilValidate.isNotEmpty(resultData) ? (String) resultData.get("response") : ""), MODULE);
										Thread.sleep(smsTriggerDelay);
									}
								}
							}
						}
						
						String isEnabledTelnyxMessage = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "TELNYX_MESSAGE_SETUP", "N");
						if("Y".equals(isEnabledTelnyxMessage)) {
							String srSMSStatusStr = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_SMS_NOTIFIY_STATUS");
							List<String> srSMSStatusList = UtilValidate.isNotEmpty(srSMSStatusStr) ? org.fio.admin.portal.util.DataUtil.stringToList(srSMSStatusStr, ",") : new ArrayList<String>();
								
							Map<String, Object> srPartyPhoneNumMap = new HashMap<String, Object>();
							if(UtilValidate.isNotEmpty(srSMSStatusList) && UtilValidate.isNotEmpty(statusId) && !(previousStatusId.equals(statusId)) && srSMSStatusList.contains(statusId)){							
								srPartyPhoneNumMap = SrUtil.getSrAssocPartyPhones(delegator, custRequestId, statusId, primary);
							}
							if(UtilValidate.isNotEmpty(srPartyPhoneNumMap)) {
								String applicationUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_URL");
								String smsBody = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_SMS_CONTENT");
								
								Map<String, Object> contentInfo = new HashMap<String, Object>();
								contentInfo.put("content", smsBody);
								contentInfo.put("SR_NUMBER", custRequestId);
								
								String srTrackerUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_TRACKER_URL");
								if(UtilValidate.isNotEmpty(srTrackerUrl)){
									String customFieldName = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FSRID_CUSTOM_FLD_NAME","FSRID");
									GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldName",customFieldName).queryFirst();
									
									if(UtilValidate.isNotEmpty(customField)) {
										String channelId = UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("groupId")) ? customField.getString("groupId") : "EXTERNAL_INFO";
	    								String customFieldId = UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("customFieldId")) ? customField.getString("customFieldId") : "";
	    								GenericValue custRequestAttribute = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId, "attrName", customFieldId, "channelId", channelId).queryFirst();
	    								if(UtilValidate.isNotEmpty(custRequestAttribute)) {
	    									String hashValue = custRequestAttribute.getString("attrValue");
	    									if(UtilValidate.isNotEmpty(hashValue)) {
	    										srTrackerUrl = srTrackerUrl+"#"+hashValue;
	    									}
	    								} else {
	    									String encodedCustReqId = Base64.getEncoder().encodeToString(custRequestId.getBytes("utf-8"));
	    									if(UtilValidate.isNotEmpty(encodedCustReqId)) {
	    										srTrackerUrl = srTrackerUrl+"#"+encodedCustReqId;
	    									}
	    								}
									} else {
										String encodedCustReqId = Base64.getEncoder().encodeToString(custRequestId.getBytes("utf-8"));
										if(UtilValidate.isNotEmpty(encodedCustReqId)) {
											srTrackerUrl = srTrackerUrl+"#"+encodedCustReqId;
										}
									}
									
								}
								srTrackerUrl = Objects.toString(srTrackerUrl, "");
								if(UtilValidate.isNotEmpty(srTrackerUrl)) {
									contentInfo.put("SR_SHORTURL", srTrackerUrl);
									contentInfo.put("SR_TRACKER_URL", srTrackerUrl);
								}
								contentInfo.put("SR_NAME", custRequestName);
								
								smsBody = org.fio.admin.portal.util.DataUtil.extractContentWithTag(contentInfo);
								
								List<String> toPhoneNumberList = new LinkedList<String>();
								
								for(String srPartyId : srPartyPhoneNumMap.keySet()) {
									String phoneNumber = (String) srPartyPhoneNumMap.get(srPartyId);
									toPhoneNumberList.add(phoneNumber);
								}
								Map<String, Object> requestContext = new HashMap<String, Object>();
								requestContext.put("domainEntityType", "SERVICE_REQUEST");
								requestContext.put("domainEntityId", custRequestId);
								
								if(UtilValidate.isNotEmpty(toPhoneNumberList)) {
									Map<String, Object> ctx = new HashMap<String, Object>();
									ctx.put("toPhoneNumber", toPhoneNumberList);
									ctx.put("smsBody", smsBody);
									ctx.put("telnyxApiType", "SEND_MESSAGE");
									ctx.put("requestContext", requestContext);
									ctx.put("userLogin", userLogin);
									Map<String, Object> smsResult =  dispatcher.runSync("msg.sendTelnyxSms", ctx);
									Map<String, Object> resultData = (Map<String, Object>) smsResult.get("results");
								}
							}
						}
						// telnyx sms integration end
						
					}
					
					if (UtilValidate.isNotEmpty(reopenFlag) && "Y".equals(reopenFlag)) {
						request.setAttribute("externalId", custRequestId);
						request.setAttribute("_EVENT_MESSAGE_", "SR has been reopened successfully");
					}else{
						request.setAttribute("externalId", custRequestId);
						request.setAttribute("_EVENT_MESSAGE_", "SR Updated Successfully"+": "+custRequestId);
					}

				}else{
					request.setAttribute("externalId", custRequestId);
					request.setAttribute("_EVENT_MESSAGE_", "SR Updated Successfully"+": "+custRequestId);
				}
			} else {
				String errMsg = "";
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errMsg = accessMatrixRes.get("errorMessage").toString();
				} else {
					errMsg = "Access Denied";
				}
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return ERROR;
			}

		}catch (Exception e) {
			String errMsg = "Problem While Updating Service Request " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			Debug.logError(e, "Exception : "+e.getMessage(), MODULE);
			return ERROR;
		}
		return SUCCESS;
	}
	
	public static String createSrHistory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String fromPartyId = (String) requestParameters.get("fromPartyId");
		String custRequestId = (String) requestParameters.get("custRequestId");
		String custRequestTypeId = (String) requestParameters.get("srTypeId");
		String custRequestCategoryId =  (String) requestParameters.get("srCategoryId");
		String custRequestSubCategoryId = (String) requestParameters.get("srSubCategoryId");
		String priorityStr = (String) requestParameters.get("priority");
		String statusId = (String) requestParameters.get("srStatusId");
		String ownerId = (String) requestParameters.get("owner");
		String custReqSrSource = (String) requestParameters.get("srSource");
		String description = (String) requestParameters.get("description");
		String resolution = (String) requestParameters.get("resolution");
		String primaryContactId = (String) requestParameters.get("ContactID");
		String custOrderId = (String) requestParameters.get("orderId");
		String custRequestName = (String) requestParameters.get("srName");
		String externalLoginKey = (String) requestParameters.get("externalLoginKey");
		String custReqLoginId = (String) requestParameters.get("custReqLoginId");
		
		try{
			
			if (UtilValidate.isNotEmpty(custRequestId)){
				
				GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId",custRequestId),false);
				
				GenericValue custRequestHistory = delegator.makeValue("CustRequestHistory");
				String custRequestHistoryId = delegator.getNextSeqId("CustRequestHistory");
				
				custRequestHistory.put("custRequestId", custRequestId);
				custRequestHistory.put("custRequestHistoryId", custRequestHistoryId);
				
				if (UtilValidate.isNotEmpty(custRequest)){
					
					if(UtilValidate.isNotEmpty(custRequest.getString("custRequestName"))){
						custRequestName = custRequest.getString("custRequestName");
						custRequestHistory.put("custRequestName", custRequestName);
					}
					
					if (UtilValidate.isNotEmpty(custRequest.getString("custRequestTypeId"))) {
						custRequestTypeId = custRequest.getString("custRequestTypeId");
						custRequestHistory.put("custRequestTypeId", custRequestTypeId);
					}
					
					if (UtilValidate.isNotEmpty(custRequest.getString("custRequestCategoryId"))) {
						custRequestCategoryId = custRequest.getString("custRequestCategoryId");
						custRequestHistory.put("custRequestCategoryId", custRequestCategoryId);
					}
					
					if (UtilValidate.isNotEmpty(custRequest.getString("custRequestSubCategoryId"))) {
						custRequestSubCategoryId = custRequest.getString("custRequestSubCategoryId");
						custRequestHistory.put("custRequestSubCategoryId", custRequestSubCategoryId);
					}

					if (UtilValidate.isNotEmpty(custRequest.getString("priority"))) {
						String priority = custRequest.getString("priority");
						custRequestHistory.put("priority", Long.parseLong(priority));
					}
					
					if (UtilValidate.isNotEmpty(custRequest.getString("statusId"))) {
						statusId = custRequest.getString("statusId");
						custRequestHistory.put("statusId", statusId);
					}
					
					if(UtilValidate.isNotEmpty(custRequest.getString("custReqSrSource"))){
						custReqSrSource = custRequest.getString("custReqSrSource");
						custRequestHistory.put("custReqSrSource", custReqSrSource);
					}
					
					if(UtilValidate.isNotEmpty(custRequest.getString("custOrderId"))){
						custOrderId = custRequest.getString("custOrderId");
						custRequestHistory.put("custOrderId", custOrderId);
					}
					
					if(UtilValidate.isNotEmpty(custRequest.get("description"))){
						custRequestHistory.put("description", custRequest.get("description"));
					}
					
					if(UtilValidate.isNotEmpty(custRequest.get("description"))){
						custRequestHistory.put("resolution", custRequest.get("resolution"));
					}
					
					custRequestHistory.put("custReqPrimaryContact", DataHelper.getSrPrimaryContact(delegator, custRequestId));
					
					if(UtilValidate.isNotEmpty(custRequest.getString("responsiblePerson"))){
						String responsiblePerson = custRequest.getString("responsiblePerson");
						custRequestHistory.put("ownerId", responsiblePerson);
					}
					
					if (UtilValidate.isNotEmpty(custRequest.get("createdDate"))) {
						custRequestHistory.put("createdDate", custRequest.get("createdDate"));
					} else {
						Timestamp createdDate = UtilDateTime.nowTimestamp();
						custRequestHistory.put("createdDate", createdDate);
					}
					
					if (UtilValidate.isNotEmpty(custRequest.getString("createdByUserLogin"))) {
						custRequestHistory.put("createdByUserLogin", custRequest.getString("createdByUserLogin"));
					} 
					
					if (UtilValidate.isNotEmpty(custRequest.get("lastModifiedDate"))) {
						custRequestHistory.put("lastModifiedDate", custRequest.get("lastModifiedDate"));
					} else {
						Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();
						custRequestHistory.put("lastModifiedDate", lastModifiedDate);
					}
					
					if (UtilValidate.isNotEmpty(custRequest.get("lastModifiedByUserLogin"))) {
						custRequestHistory.put("lastModifiedByUserLogin", custRequest.get("lastModifiedByUserLogin"));
					} 
					
					if(UtilValidate.isNotEmpty(custRequest.getString("statusId")) && ("SR_CLOSED".equals(custRequest.getString("statusId")) || "SR_CANCELLED".equals(custRequest.getString("statusId")))) {
						Timestamp closedByDate = (Timestamp) custRequest.get("closedByDate");
						if (UtilValidate.isEmpty(closedByDate)) {
							closedByDate = UtilDateTime.nowTimestamp();
						}
						custRequestHistory.put("closedByUserLogin",userLogin.getString("userLoginId"));
						custRequestHistory.put("closedByDate",closedByDate);
					}
					custRequestHistory.create();
				}
			}
			
		}catch (Exception e) {
			String errMsg = "Problem While Creating Service Request History " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			Debug.logError(e, "Exception : "+e.getMessage(), MODULE);
			return ERROR;
		}
		return SUCCESS;
	}
	public static String addSRClientPortalEvent(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String fromPartyId = (String) requestParameters.get("fromPartyId");
		String cNo = (String) requestParameters.get("cNo");
		String custRequestTypeId = (String) requestParameters.get("srTypeId");
		String custRequestCategoryId =  (String) requestParameters.get("srCategoryId");
		String custRequestSubCategoryId = (String) requestParameters.get("srSubCategoryId");
		String custRequestOthCategoryId = (String) requestParameters.get("otherSrSubCategoryId");
		String priorityStr = (String) requestParameters.get("priority");
		String accountType = (String) requestParameters.get("accountType");
		String custReqOnceDone = (String) requestParameters.get("onceAndDone");
		String statusId = (String) requestParameters.get("srStatusId");
		String subStatusId = (String) requestParameters.get("srSubStatusId");
		String owner = (String) requestParameters.get("owner");
		String ownerBu = (String) requestParameters.get("ownerBu");
		String custReqSrSource = (String) requestParameters.get("srSource");
		String accountNumber = (String) requestParameters.get("accountNumber");
		String description = (String) requestParameters.get("description");
		String resolution = (String) requestParameters.get("resolution");
		String notes = (String) requestParameters.get("notes");
		String parentCustRequestId = (String) requestParameters.get("parentCustRequestId");
		String primaryContactId = (String) requestParameters.get("ContactID");
		String alternateId = (String) requestParameters.get("alternateId");
		String nationalId = (String) requestParameters.get("nationalId");
		String alternateIdentificationTypeId = (String) requestParameters.get("alternateIdentificationTypeId");
		String nationalIdentificationTypeId = (String) requestParameters.get("nationalIdentificationTypeId");
		String custOrderId = (String) requestParameters.get("orderId");
		String custRequestName = (String) requestParameters.get("srName");
		String custReqDocumentNum = (String) requestParameters.get("sourceDocumentId");
		String externalLoginKey = (String) requestParameters.get("externalLoginKey");
		String clientPortal = (String) requestParameters.get("clientPortal");
		String customerId = (String) requestParameters.get("customerId");
		String loggedInUserId = (String) requestParameters.get("loggedInUserId");
		
		String contractorId = (String) requestParameters.get("contractorId");
		String primary = (String) requestParameters.get("primary");
		String salesPerson = (String) requestParameters.get("salesPerson");
		String externalId = (String) requestParameters.get("srNumber");
		String copyFlag = (String) requestParameters.get("copyFlag");
		String homeType = (String) requestParameters.get("homeType");
		
		//Home Owner Contact Info
		String homePhoneNumberParam = (String) requestParameters.get("homePhoneNumber");
		String offPhoneNumberParam = (String) requestParameters.get("offPhoneNumber");;
		String mobilePhoneNumberParam = (String) requestParameters.get("mobilePhoneNumber");
		String customerPrimaryEmailParam = (String) requestParameters.get("customerPrimaryEmail");
		
		//Contractor Contact Info
		String contractorHomeNumberParam = (String) requestParameters.get("contractorHomeNumber");
		String contractorOffNumberParam = (String) requestParameters.get("contractorOffNumber");
		String contractorMobileNumberParam = (String) requestParameters.get("contractorMobileNumber");
		String contractorPrimaryEmailParam = (String) requestParameters.get("contractorPrimaryEmail");
		
		//Attribute Info
		String serviceFee = (String) requestParameters.get("serviceFee");;
		String preFinishPlus = (String) requestParameters.get("preFinishPlus");
		//String finishType = (String) requestParameters.get("finishType");
		String vendorCode = (String) requestParameters.get("vendorCode");
		String location = (String) requestParameters.get("location");
		
		String finishType = (String) requestParameters.get("finishType");
		String finishColor = (String) requestParameters.get("finishColor");
		String materialCategory = (String) requestParameters.get("materialCategory");
		String materialSubCategory = (String) requestParameters.get("materialSubCategory");
		
		String actualResolution = (String) requestParameters.get("actualResolution");
		String addressContactMechId = (String) requestParameters.get("homeOwnerAddress");
		
		String soldByLocation = (String) requestParameters.get("soldByLocation");
		String primaryTechnician = (String) requestParameters.get("primaryTechnician");
		
		
		String inspectedBy = (String) requestParameters.get("inspectedBy");
		String inspectionDate = (String) requestParameters.get("inspectionDate");
		String installedSquare = (String) requestParameters.get("installedSquare");
		String hasAlarmSystem = (String) requestParameters.get("hasAlarmSystem");
		String installed = (String) requestParameters.get("installed");
		
		String contractorName = (String) requestParameters.get("contractorId_desc");
		
		String customerName = (String) requestParameters.get("customerId_desc");
		
		Debug.log("===== owner ======"+owner);
		
		List<String> reasonCodeList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(requestParameters.get("reasonCode")) && requestParameters.get("reasonCode") instanceof String) {
			String reasonCode = (String) requestParameters.get("reasonCode");
			if(UtilValidate.isNotEmpty(reasonCode)) reasonCodeList.add(reasonCode);
		} else if(UtilValidate.isNotEmpty(requestParameters.get("reasonCode")) && requestParameters.get("reasonCode") instanceof List<?>) {
			reasonCodeList = (List<String>) requestParameters.get("reasonCode");
		}
		
		List<String> causeCategoryList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(requestParameters.get("causeCategory")) && requestParameters.get("causeCategory") instanceof String) {
			String causeCategory = (String) requestParameters.get("causeCategory");
			if(UtilValidate.isNotEmpty(causeCategory)) causeCategoryList.add(causeCategory);
		} else if(UtilValidate.isNotEmpty(requestParameters.get("causeCategory")) && requestParameters.get("causeCategory") instanceof List<?>) {
			causeCategoryList = (List<String>) requestParameters.get("causeCategory");
		}
		List<String> optionalAttendees = null;
		String optionalAttendeesClassName = "";
		String optionalAttendeesEmailIds = "";
		
		if (UtilValidate.isNotEmpty(requestParameters.get("optionalAttendees"))) {
			optionalAttendeesClassName = requestParameters.get("optionalAttendees").getClass().getName();
		}
		
		if (UtilValidate.isNotEmpty(optionalAttendeesClassName)
				&& "java.lang.String".equals(optionalAttendeesClassName)) {
			optionalAttendees = UtilMisc.toList((String) requestParameters.get("optionalAttendees"));
		} else if (UtilValidate.isNotEmpty(optionalAttendeesClassName)
				&& "java.util.LinkedList".equals(optionalAttendeesClassName)) {
			optionalAttendees = (List<String>) requestParameters.get("optionalAttendees");
		}
		
		if (UtilValidate.isNotEmpty(optionalAttendees)) {
			for (String eachOptionalAttendee : optionalAttendees) {
				Map<String, String> optionalAttendeeContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, eachOptionalAttendee);
				if (UtilValidate.isNotEmpty(optionalAttendeeContactInformation) && UtilValidate.isNotEmpty(optionalAttendeeContactInformation.get("EmailAddress"))){
					String optionalAttendeeEmailAddress = optionalAttendeeContactInformation.get("EmailAddress");
					optionalAttendeesEmailIds = optionalAttendeesEmailIds+optionalAttendeeEmailAddress+",";
				}
			}
			if (UtilValidate.isNotEmpty(optionalAttendeesEmailIds)) {
				optionalAttendeesEmailIds = optionalAttendeesEmailIds.substring(0, optionalAttendeesEmailIds.length()-1);
			}
		}
		
		try{
			if(UtilValidate.isNotEmpty(cNo)){
				fromPartyId = cNo;
			}
			String locationCustomFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);
			
			long start = System.currentTimeMillis();
			if(UtilValidate.isNotEmpty(cNo)){
				fromPartyId = cNo;
			}
			
			if (UtilValidate.isEmpty(resolution) && "SR_CLOSED".equals(statusId)) {
				String errMsg = "Resolution field is mandatory to resolve the SR";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return ERROR;
			}
			//LOGGER.log(Level.INFO, "fromPartyId--->"+fromPartyId);
			Map<String, Object> custRequestContext = new HashMap<String, Object>();
			Map<String, Object> supplementoryContext = new HashMap<String, Object>();

			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String nativeBusinessUnit = null;
			String nativeTeamId = null;
			String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId)) {
				Map<String, Object> buTeamData = DataUtil.getUserBuTeam(delegator, userLoginPartyId);
				nativeBusinessUnit = (String) buTeamData.get("businessUnit");
				nativeTeamId = (String) buTeamData.get("emplTeamId");
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", nativeBusinessUnit);
				accessMatrixMap.put("modeOfOp", "Create");
				accessMatrixMap.put("entityName", "CustRequest");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
				//validate the common team and access for the assignment
				String currentPartyId = userLoginPartyId;
				if(UtilValidate.isEmpty(owner)) {
					owner = nativeTeamId;
					if (UtilValidate.isEmpty(owner)) {
						owner = userLoginId;
					}
				} else {
					currentPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
					buTeamData = DataUtil.getUserBuTeam(delegator, currentPartyId);
					nativeBusinessUnit = (String) buTeamData.get("businessUnit");
					nativeTeamId = (String) buTeamData.get("emplTeamId");
				}
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

			}
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				
				String tsmRoleTypeId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "tsm.roleTypeId", delegator);
				boolean isTsmUserLoggedIn = UtilValidate.isNotEmpty(PartyHelper.getFirstValidRoleTypeId(userLoginPartyId, UtilMisc.toList(tsmRoleTypeId), delegator)) ? true : false;
				
				if(UtilValidate.isNotEmpty(custReqDocumentNum)){
					custRequestContext.put("custReqDocumentNum", custReqDocumentNum);
				}
				if(UtilValidate.isNotEmpty(custRequestTypeId)){
					custRequestContext.put("custRequestTypeId", custRequestTypeId);
				}
				if(UtilValidate.isNotEmpty(custRequestCategoryId)){
					custRequestContext.put("custRequestCategoryId", custRequestCategoryId);
				}
				if(UtilValidate.isNotEmpty(custRequestSubCategoryId)){
					custRequestContext.put("custRequestSubCategoryId", custRequestSubCategoryId);
				}
				if(UtilValidate.isNotEmpty(custRequestOthCategoryId)){
					custRequestContext.put("custRequestOthCategoryId", custRequestOthCategoryId);
				}
				if(UtilValidate.isNotEmpty(priorityStr)){
					//long  priority = (Long.parseLong(priorityStr));
					custRequestContext.put("priority", priorityStr);
				}
				if(UtilValidate.isNotEmpty(statusId)){
					custRequestContext.put("statusId", statusId);
				}
				if(UtilValidate.isNotEmpty(subStatusId)){
					custRequestContext.put("subStatusId", subStatusId);
				}
				if(UtilValidate.isNotEmpty(owner)){
					custRequestContext.put("responsiblePerson", owner);
				}
				if(UtilValidate.isNotEmpty(parentCustRequestId)){
					custRequestContext.put("parentCustRequestId", parentCustRequestId);
				}
				custRequestContext.put("emplTeamId", nativeTeamId);
				custRequestContext.put("ownerBu", nativeBusinessUnit);
				
				if(UtilValidate.isNotEmpty(custReqSrSource)){
					custRequestContext.put("custReqSrSource", custReqSrSource);
				}
				if(UtilValidate.isNotEmpty(description)){
					Debug.log("SR description--->"+description+"--character-set-->"+request.getCharacterEncoding(), MODULE);
					if (isTsmUserLoggedIn) {
						custRequestContext.put("tsmDescription", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
					} else {
						custRequestContext.put("description", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
					}
				}
				if(UtilValidate.isNotEmpty(resolution)){
					custRequestContext.put("resolution", Base64.getEncoder().encodeToString(resolution.getBytes("utf-8")));
				}
				if(UtilValidate.isNotEmpty(fromPartyId)){
					custRequestContext.put("fromPartyId", fromPartyId);
				}
				if(UtilValidate.isNotEmpty(custReqOnceDone)){
					custRequestContext.put("custReqOnceDone", custReqOnceDone);
				}
				if(UtilValidate.isNotEmpty(custReqOnceDone) && "Y".equals(custReqOnceDone)){
					custRequestContext.put("statusId", "SR_CLOSED");
				}
				if(UtilValidate.isNotEmpty(custOrderId)){
					custRequestContext.put("custOrderId", custOrderId);
				}
				if(UtilValidate.isNotEmpty(custRequestName)){
					custRequestContext.put("custRequestName", custRequestName);
				}
				if(UtilValidate.isNotEmpty(customerPrimaryEmailParam)){
					custRequestContext.put("emailAddress", customerPrimaryEmailParam);
				}
				if(UtilValidate.isNotEmpty(actualResolution)){
					custRequestContext.put("actualResolution", Base64.getEncoder().encodeToString(actualResolution.getBytes("utf-8")));
				}
				if(UtilValidate.isNotEmpty(homeType)){
					custRequestContext.put("homeType", homeType);
				}
				if(UtilValidate.isNotEmpty(accountType)){
					supplementoryContext.put("accountType", accountType);
				}
				if(UtilValidate.isNotEmpty(accountNumber)){
					supplementoryContext.put("accountNumber", accountNumber);
				}
				if(UtilValidate.isNotEmpty(copyFlag)){
					supplementoryContext.put("isCopySr", copyFlag);
					supplementoryContext.put("domainEntityId", externalId);
					supplementoryContext.put("domainEntityType", "SERVICE_REQUEST");
				}
				if(UtilValidate.isNotEmpty(notes)){
					//supplementoryContext.put("internalComment", notes);
				}
				
				// prepare postal address params [start]
				supplementoryContext.put("pstlAttnName", requestParameters.get("generalAttnName"));
				supplementoryContext.put("pstlAddress1", requestParameters.get("generalAddress1"));
				supplementoryContext.put("pstlAddress2", requestParameters.get("generalAddress2"));
				supplementoryContext.put("pstlPostalCode", requestParameters.get("generalPostalCode"));
				supplementoryContext.put("pstlPostalCodeExt", requestParameters.get("generalPostalCodeExt"));
				supplementoryContext.put("pstlPostalCity", requestParameters.get("generalCity"));
				supplementoryContext.put("pstlStateProvinceGeoId", requestParameters.get("generalStateProvinceGeoId"));
				supplementoryContext.put("pstlCountryGeoId", requestParameters.get("generalCountryGeoId"));
				supplementoryContext.put("pstlCountyGeoId", requestParameters.get("countyGeoId"));
				supplementoryContext.put("isBusiness", requestParameters.get("isBusiness"));
				supplementoryContext.put("isVacant", requestParameters.get("isVacant"));
				supplementoryContext.put("isUspsAddrVerified", requestParameters.get("isUspsAddrVerified"));
				supplementoryContext.put("homePhoneNumber", requestParameters.get("homePhoneNumber"));
				supplementoryContext.put("offPhoneNumber", requestParameters.get("offPhoneNumber"));
				supplementoryContext.put("mobileNumber", requestParameters.get("mobilePhoneNumber"));
				supplementoryContext.put("contractorOffPhone", requestParameters.get("contractorOffNumber"));
				supplementoryContext.put("contractorMobilePhone", requestParameters.get("contractorMobileNumber"));
				supplementoryContext.put("contractorHomePhone", requestParameters.get("contractorHomeNumber"));
				supplementoryContext.put("contractorEmail", requestParameters.get("contractorPrimaryEmail"));
				
				/*Map<String, Object> corodinate = DataHelper.getGeoCoordinate(delegator, (String) requestParameters.get("generalPostalCode"), (String) requestParameters.get("generalPostalCodeExt"));
				supplementoryContext.put("latitude", corodinate.get("latitude"));
				supplementoryContext.put("longitude", corodinate.get("longitude"));*/
				
				String isUspsRequired = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_USPS_ADDRACT");
				if(UtilValidate.isNotEmpty(isUspsRequired) && isUspsRequired.equals("Y")) {
					Map<String, Object> corodinate = DataHelper.getGeoCoordinateByGoogleApi(UtilMisc.toMap("delegator", delegator, "dispatcher", dispatcher, "userLogin", userLogin, "zip5", (String) requestParameters.get("generalPostalCode"), "zip4", (String) requestParameters.get("generalPostalCodeExt"), "city", requestParameters.get("generalCity"), "state", requestParameters.get("generalStateProvinceGeoId"), "county", requestParameters.get("countyGeoId"), "address1", (String) requestParameters.get("generalAddress1"), "address2", (String) requestParameters.get("generalAddress2"), "country", (String) requestParameters.get("generalCountryGeoId")));
					supplementoryContext.put("latitude", corodinate.get("latitude"));
					supplementoryContext.put("longitude", corodinate.get("longitude"));
				}
				
				/*
				// varify to create party postal [start]
				String postalPartyId = null;
				if (UtilValidate.isNotEmpty(primary) && primary.equals("CONTRACTOR")) {
					postalPartyId = contractorId;
				} else {
					postalPartyId = customerId;
				} 
				String contactMechId = UtilContactMech.evalutePartyPostal(UtilMisc.toMap("delegator", delegator, "dispatcher", dispatcher, "userLogin", userLogin
						, "partyId", postalPartyId
						, "address1", (String) requestParameters.get("generalAddress1"), "address2", (String) requestParameters.get("generalAddress2")
						, "countryGeoId", (String) requestParameters.get("generalCountryGeoId"), "stateGeoId", (String) requestParameters.get("generalStateProvinceGeoId")
						, "city", (String) requestParameters.get("generalCity"), "county", (String) requestParameters.get("countyGeoId")
						, "zip5", (String) requestParameters.get("generalPostalCode"), "zip4", (String) requestParameters.get("generalPostalCodeExt")
						, "isBusiness", (String) requestParameters.get("isBusiness"), "isVacant", (String) requestParameters.get("isVacant"), "isUspsAddrVerified", (String) requestParameters.get("isUspsAddrVerified")
						, "latitude", (String) supplementoryContext.get("latitude"), "longitude", (String) supplementoryContext.get("longitude")
						));
				// varify to create party postal [end]
				
				addressContactMechId = UtilValidate.isNotEmpty(contactMechId) ? contactMechId : addressContactMechId;
				*/
				// prepare postal address params [end]
				
				supplementoryContext.put("descriptionRawTxt", description);
				supplementoryContext.put("resolutionRawTxt", resolution);
				
				if (UtilValidate.isNotEmpty(customerId)) {
					Map<String, Object> homeOwnerContactInfoContext = new HashMap<String, Object>();
					homeOwnerContactInfoContext.put("customerId", customerId);
					homeOwnerContactInfoContext.put("homePhoneNumberParam", homePhoneNumberParam);
					homeOwnerContactInfoContext.put("offPhoneNumberParam", offPhoneNumberParam);
					homeOwnerContactInfoContext.put("mobilePhoneNumberParam", mobilePhoneNumberParam);
					homeOwnerContactInfoContext.put("primaryEmailParam", customerPrimaryEmailParam);

					Map<String, Object> homeOwnerContactInputMap = new HashMap<String, Object>();
					homeOwnerContactInputMap.put("contactInformationContext", homeOwnerContactInfoContext);
					homeOwnerContactInputMap.put("userLogin", userLogin);
					
					Map<String, Object> homeOwnerContactResultsMap = dispatcher.runSync("srPortal.validateSrContactInformation", homeOwnerContactInputMap);
					
					if(!ServiceUtil.isSuccess(homeOwnerContactResultsMap)) {
						request.setAttribute("_ERROR_MESSAGE_", "Problem While Validating Home Owner Contact Information");
						return ERROR;
					}
				}
				
				if (UtilValidate.isNotEmpty(contractorId)) {
					Map<String, Object> contractorContactInfoContext = new HashMap<String, Object>();
					contractorContactInfoContext.put("customerId", contractorId);
					contractorContactInfoContext.put("homePhoneNumberParam", contractorHomeNumberParam);
					contractorContactInfoContext.put("offPhoneNumberParam", contractorOffNumberParam);
					contractorContactInfoContext.put("mobilePhoneNumberParam", contractorMobileNumberParam);
					contractorContactInfoContext.put("primaryEmailParam", contractorPrimaryEmailParam);

					Map<String, Object> contractorContactInputMap = new HashMap<String, Object>();
					contractorContactInputMap.put("contactInformationContext", contractorContactInfoContext);
					contractorContactInputMap.put("userLogin", userLogin);
					
					Map<String, Object> contractorContactResultsMap = dispatcher.runSync("srPortal.validateSrContactInformation", contractorContactInputMap);
					
					if(!ServiceUtil.isSuccess(contractorContactResultsMap)) {
						request.setAttribute("_ERROR_MESSAGE_", "Problem While Validating Contractor Contact Information");
						return ERROR;
					}
				}
				
				GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", fromPartyId), null, false) );
				String roleTypeId = party.getString("roleTypeId");

				if (UtilValidate.isNotEmpty(roleTypeId)) {
					if ("CUSTOMER".equalsIgnoreCase(roleTypeId)) {
						custRequestContext.put("customerRelatedType", roleTypeId);
					}else if ("PROSPECT".equalsIgnoreCase(roleTypeId)) {
						custRequestContext.put("customerRelatedType", "02");
					}else if ("NON_CRM".equalsIgnoreCase(roleTypeId)) {
						custRequestContext.put("customerRelatedType", "07");
					} else {
						custRequestContext.put("customerRelatedType", roleTypeId);
					}
				}
				custRequestContext.put("customerId", customerId);
				
				custRequestContext.put("custRequestId", org.fio.sr.portal.util.UtilGenerator.getSrNumber(delegator, (String) supplementoryContext.get("pstlStateProvinceGeoId"), (String) supplementoryContext.get("pstlCountyGeoId")));
				
				
				Map<String, Object> inputMap = new HashMap<String, Object>();
				inputMap.put("custRequestContext", custRequestContext);
				inputMap.put("supplementoryContext", supplementoryContext);
				inputMap.put("userLogin", userLogin);

				Map<String, Object> outMap = dispatcher.runSync("crmPortal.createServiceRequest", inputMap);

				if(!ServiceUtil.isSuccess(outMap)) {
					request.setAttribute("_ERROR_MESSAGE_", "Problem While Creating Service Request");
					return ERROR;
				}
				String custRequestId = (String) outMap.get("custRequestId");
				if(UtilValidate.isEmpty(custRequestId) || custRequestId == null){
					String errMsg = "Problem While Creating Service Request ";
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					return ERROR;
				}else if (UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(notes)) {
					Map < String, Object > inputNote = FastMap.newInstance();
					inputNote.put("custRequestId", custRequestId);
					inputNote.put("noteInfo", notes);
					inputNote.put("userLogin", userLogin);
					Map serviceResult = null;
					try {
						//serviceResult = dispatcher.runSync("createCustRequestNote", inputNote);

						GenericValue noteDataCreate = delegator.makeValue("NoteData");
						String noteId = delegator.getNextSeqId("NoteData");
						noteId = UtilGenerator.getNoteNumber(delegator, noteId);

						noteDataCreate.set("noteId",noteId);
						noteDataCreate.set("noteName", "Service Request");
						noteDataCreate.set("noteInfo", notes);
						noteDataCreate.set("noteDateTime", UtilDateTime.nowTimestamp());
						noteDataCreate.set("createdByUserLogin", userLogin.getString("userLoginId"));
						noteDataCreate.set("noteParty", userLogin.getString("userLoginId"));
						delegator.create(noteDataCreate);

						if (UtilValidate.isNotEmpty(noteId) && UtilValidate.isNotEmpty(custRequestId)) {
							GenericValue custRequestNote = delegator.makeValue("CustRequestNote");
							custRequestNote.set("noteId",noteId);
							custRequestNote.set("custRequestId",custRequestId);
							delegator.create(custRequestNote);
						}

						if (ServiceUtil.isError(serviceResult)) {
							String errMsg = "Problem While Creating Notes for Service Request ";
							request.setAttribute("_ERROR_MESSAGE_", errMsg);
							return ERROR;
						}else{
							request.setAttribute("externalId", custRequestId);
							request.setAttribute("_EVENT_MESSAGE_", "SR Created Successfully"+": "+custRequestId);
						}
					} catch (Exception e) {
						String errMsg = "Problem While Creating Notes For Service Request " + e.toString();
						request.setAttribute("_ERROR_MESSAGE_", errMsg);
						Debug.logError(e, "Exception : "+e.getMessage(), MODULE);
						return ERROR;
					}
				}else if (UtilValidate.isNotEmpty(custRequestId)) {
					
					if (UtilValidate.isNotEmpty(externalId)) {
						GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", externalId).queryOne();
						if (UtilValidate.isNotEmpty(custRequest)) {
							custRequest.put("custReqDocumentNum", "");
							custRequest.store();
						}
					}
					
					if (UtilValidate.isNotEmpty(custRequestId)) {
						request.setAttribute("custRequestId", custRequestId);
						
						Map<String, Object> historyInputMap = new HashMap<String, Object>();
						historyInputMap.put("custRequestId", custRequestId);
						historyInputMap.put("userLogin", userLogin);
						
						Map<String, Object> historyOutMap = dispatcher.runSync("srPortal.createSrHistory", historyInputMap);
						//String serviceResult = createSrHistory(request,response);

						if(!ServiceUtil.isSuccess(historyOutMap)) {
							request.setAttribute("_ERROR_MESSAGE_", "Problem While Creating Service Request History");
							return ERROR;
						}	
					}
					
					if (UtilValidate.isNotEmpty(primaryContactId)) {
						GenericValue custRequestContact = delegator.makeValue("CustRequestContact");
						custRequestContact.set("custRequestId",custRequestId);
						custRequestContact.set("partyId", primaryContactId);
						custRequestContact.set("roleTypeId", "CONTACT");
						custRequestContact.set("isPrimary", "Y");
						
						custRequestContact.set("fromDate", UtilDateTime.nowTimestamp());
						delegator.create(custRequestContact);
					}
					
					//To store optional contacts
					if (UtilValidate.isNotEmpty(optionalAttendees)) {
						for (String eachOptionalAttendee : optionalAttendees) {
							GenericValue custRequestContact = delegator.makeValue("CustRequestContact");
							custRequestContact.set("custRequestId",custRequestId);
							custRequestContact.set("partyId", eachOptionalAttendee);
							custRequestContact.set("roleTypeId", "CONTACT");
							custRequestContact.set("isPrimary", "N");
							custRequestContact.set("fromDate", UtilDateTime.nowTimestamp());
							delegator.create(custRequestContact);
						}
					}
					if (UtilValidate.isNotEmpty(primary)) {						
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "PRIMARY");
						custRequestAttrbute.set("attrValue", primary);
						delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(serviceFee)) {
						String customFieldName = DataUtil.getGlobalValue(delegator, "SERVICE_FEE", "Service for a Fee");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "SERVICE_GROUP", customFieldName);
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", customFieldId);
						custRequestAttrbute.set("attrValue", serviceFee);
						delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(vendorCode)) {		
						String customFieldName = DataUtil.getGlobalValue(delegator, "VENDOR_CODE", "Vendor Code");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "VENDOR_GROUP", customFieldName);
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", customFieldId);
						custRequestAttrbute.set("attrValue", vendorCode);
						delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(location)) {						
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", locationCustomFieldId);
						custRequestAttrbute.set("attrValue", location);
						delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(soldByLocation)) {				
						String customFieldName = DataUtil.getGlobalValue(delegator, "SOLD_BY_LOCATION", "Sold By Location");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "CUSTOMER_GRP", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", customFieldId);
							custRequestAttrbute.set("attrValue", soldByLocation);
							delegator.create(custRequestAttrbute);
						}
					}
					if (UtilValidate.isNotEmpty(preFinishPlus)) {
						String customFieldName = DataUtil.getGlobalValue(delegator, "PRO_FINISH_PLUS", "Pro Finish Plus");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "FINISH_GROUP", customFieldName);
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", customFieldId);
						custRequestAttrbute.set("attrValue", preFinishPlus);
						delegator.create(custRequestAttrbute);
					}
					if(UtilValidate.isNotEmpty(addressContactMechId)) {
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "RECOMM_ADDRESS");
						custRequestAttrbute.set("attrValue", addressContactMechId);
						delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(finishType)) {	
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "FSR_FINISH_TYPE");
						custRequestAttrbute.set("attrValue", finishType);
						delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(finishColor)) {	
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "FSR_FINISH_COLOR");
						custRequestAttrbute.set("attrValue", finishColor);
						delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(materialCategory)) {	
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "FSR_MATERIAL_CATEGROY");
						custRequestAttrbute.set("attrValue", materialCategory);
						delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(materialSubCategory)) {	
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "FSR_MATERIAL_SUB_CATEGROY");
						custRequestAttrbute.set("attrValue", materialSubCategory);
						delegator.create(custRequestAttrbute);
					}
					
					if (UtilValidate.isNotEmpty(contractorName)) {	
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "CONTRACTOR_NAME");
						custRequestAttrbute.set("attrValue", contractorName);
						delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(customerName)) {
						GenericValue custName = EntityQuery.use(delegator).from("CustomField").where("customFieldId","CUSTOMER_NAME").queryFirst();
						if(UtilValidate.isNotEmpty(custName)) {
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "CUSTOMER_NAME");
							custRequestAttrbute.set("attrValue", customerName);
							delegator.create(custRequestAttrbute);
						}
					}
					
					//To store the data in custRequestParty
					if(UtilValidate.isNotEmpty(customerId)) {
						String customerRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, customerId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, customerId, customerRoleTypeId);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, customerId, customerRoleTypeId);
					}
					if(UtilValidate.isNotEmpty(fromPartyId)) {
						String fromPartyIdRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, fromPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, fromPartyId, fromPartyIdRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, fromPartyId, fromPartyIdRole);
					}
					String primContactRole = "";
					if(UtilValidate.isNotEmpty(primaryContactId)) {
						primContactRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, primaryContactId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, primaryContactId, primContactRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, primaryContactId, primContactRole);
					}
					if(UtilValidate.isNotEmpty(owner)) {
						String ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
						String ownerRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, ownerPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, ownerPartyId, ownerRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, ownerPartyId, ownerRole);
					}
					if(UtilValidate.isNotEmpty(userLoginId)) {
						userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
						String userLoginRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
						if(UtilValidate.isNotEmpty(primContactRole) && !(primContactRole.equals(userLoginRole))) {
							org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, userLoginPartyId, userLoginRole);
							//org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, userLoginPartyId, userLoginRole);
						}
					}
					if(UtilValidate.isNotEmpty(contractorId)) {
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, contractorId, "CONTRACTOR");
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, contractorId, "CONTRACTOR");
					}
					if(UtilValidate.isNotEmpty(salesPerson)) {
						String ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
						if(!salesPerson.equals(ownerPartyId)){
							org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, salesPerson, "SALES_REP");
							org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, salesPerson, "SALES_REP");
						}
					}
					if(UtilValidate.isNotEmpty(primaryTechnician)) {
						String primaryTechnicianRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, primaryTechnician);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, primaryTechnician, primaryTechnicianRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, primaryTechnician, primaryTechnicianRole);
					}
					
					if(UtilValidate.isNotEmpty(reasonCodeList)) {
						EntityCondition resonCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "REASON_CODE"),
								EntityCondition.makeCondition("reasonId", EntityOperator.NOT_IN, reasonCodeList));
						delegator.removeByCondition("CustRequestResolution", resonCondition);
						for(String reasonCode : reasonCodeList) {
							org.fio.sr.portal.DataHelper.createCustReqResolution(delegator, UtilMisc.toMap("custRequestId", custRequestId, "custRequestTypeId", SrResolutionConstant.REASON_CODE,"reasonId", reasonCode));
						}
					} else {
						EntityCondition resonCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "REASON_CODE"));
						delegator.removeByCondition("CustRequestResolution", resonCondition);
					}
					
					if(UtilValidate.isNotEmpty(causeCategoryList)) {
						EntityCondition causeCatCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "CAUSE_CATEGORY"),
								EntityCondition.makeCondition("causeCategoryId", EntityOperator.NOT_IN, causeCategoryList));
						delegator.removeByCondition("CustRequestResolution", causeCatCondition);
						for(String causeCategory : causeCategoryList) {
							org.fio.sr.portal.DataHelper.createCustReqResolution(delegator, UtilMisc.toMap("custRequestId", custRequestId, "custRequestTypeId", SrResolutionConstant.CAUSE_CATEGORY, "causeCategoryId", causeCategory));
						}
					}  else {
						EntityCondition causeCatCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "CAUSE_CATEGORY"));
						delegator.removeByCondition("CustRequestResolution", causeCatCondition);
					}

					if (UtilValidate.isNotEmpty(alternateId)) {
						GenericValue partyIdentification = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", fromPartyId));
						partyIdentification.put("partyIdentificationTypeId", alternateIdentificationTypeId != null ? alternateIdentificationTypeId :"ALTERNATE_ID");
						partyIdentification.put("idValue", alternateId);
						partyIdentification.create();
					}

					if (UtilValidate.isNotEmpty(nationalId)) {
						GenericValue partyIdentification = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", fromPartyId));
						partyIdentification.put("partyIdentificationTypeId", nationalIdentificationTypeId != null ? nationalIdentificationTypeId :"NATIONAL_ID");
						partyIdentification.put("idValue", nationalId);
						partyIdentification.create();
					}
					
					
					if (UtilValidate.isNotEmpty(inspectedBy)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTED_BY", "Inspected By");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", inspectedBy);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", inspectedBy);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTED_BY", "Inspected By");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}
					}
					
					if (UtilValidate.isNotEmpty(inspectionDate)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTION_DATE", "Inspection Date");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", inspectionDate);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", inspectionDate);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTION_DATE", "Inspection Date");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}
					}
					
					if (UtilValidate.isNotEmpty(installedSquare)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED_SQUARE", "Installed Square, Level, and Plumb");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", installedSquare);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", installedSquare);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED_SQUARE", "Installed Square, Level, and Plumb");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}
					}
					
					if (UtilValidate.isNotEmpty(hasAlarmSystem)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "HAS_ALARM_SYSTEM", "Has Alarm System");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", hasAlarmSystem);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", hasAlarmSystem);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						String customFieldName = DataUtil.getGlobalValue(delegator, "HAS_ALARM_SYSTEM", "Has Alarm System");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}
					}
					
					if (UtilValidate.isNotEmpty(installed)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED", "Installed");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", installed);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", installed);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED", "Installed");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}
					}
					

					String partyDesc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, cNo, false);
					String ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, owner, false);
					String srStatusDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId);

					String subject = "Update on SR ID "+custRequestId+" - "+custRequestName;

					/* Map<String, String> loggedInUserContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLogin.getString("partyId"));
                String nsender = loggedInUserContactInformation.get("EmailAddress");*/

					String nsender = "";
					String nto = "";
					String ccAddresses = "";
					String signName = "";
					
					if (UtilValidate.isNotEmpty(clientPortal) && "clientPortal".equals(clientPortal)){
						nto = (String) requestParameters.get("toEmailId");
						ownerDesc = (String) requestParameters.get("cspSupportName");
						
						Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLogin.getString("partyId"));
						//nsender = ntoContactInformation.get("EmailAddress");
						GenericValue sytemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryOne();
						
						if(UtilValidate.isNotEmpty(sytemProperty)){
							nsender = sytemProperty.getString("systemPropertyValue");
						}else{
							nsender = (String) requestParameters.get("fromEmailId");
						}
						
						GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);

						if(UtilValidate.isNotEmpty(personGv)){
							signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
						}
						
						if(UtilValidate.isNotEmpty(priorityStr)){
							if ("62816".equals(priorityStr)){
								subject = "Update on SR ID "+custRequestId+" - "+custRequestName;
							}else if ("62817".equals(priorityStr)){
								subject = "Update on SR ID "+custRequestId+" - "+custRequestName;
							}else if ("62818".equals(priorityStr)){
								subject = "Update on SR ID "+custRequestId+" - "+custRequestName;
							}
						}
						
						Map<String, String> primaryContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,primaryContactId);
						if(UtilValidate.isNotEmpty(primaryContactInformation)){
							ccAddresses = primaryContactInformation.get("EmailAddress");
							if(UtilValidate.isNotEmpty(optionalAttendeesEmailIds)){
								ccAddresses = ccAddresses+","+optionalAttendeesEmailIds;
							}
						}else{
							ccAddresses = optionalAttendeesEmailIds;
						}
						
					}else{
						signName = "CRM Administrator.";
						
						GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);

						if(UtilValidate.isNotEmpty(personGv)){
							signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
						}
						
						GenericValue sytemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryOne();
						
						Debug.log("===== SR CREATION EMAIL TRIGGER sytemProperty ===="+sytemProperty);
						
						if(UtilValidate.isNotEmpty(sytemProperty)){
							nsender = sytemProperty.getString("systemPropertyValue");
						}else{
							nsender = (String) requestParameters.get("fromEmailId");
						}
						
						GenericValue userLoginPerson = EntityUtil.getFirst(EntityQuery.use(delegator).select("userLoginId","partyId","firstName").from("UserLoginPerson").where("userLoginId", owner).queryList());

						Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLoginPerson.getString("partyId"));
						nto = ntoContactInformation.get("EmailAddress");

						Map<String, String> primaryContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,primaryContactId);
						if(UtilValidate.isNotEmpty(primaryContactInformation)){
							ccAddresses = primaryContactInformation.get("EmailAddress");
							if(UtilValidate.isNotEmpty(optionalAttendeesEmailIds)){
								ccAddresses = ccAddresses+","+optionalAttendeesEmailIds;
							}
						}else{
							ccAddresses = optionalAttendeesEmailIds;
						}
					}
					
					Debug.log("===== nsender ===="+nsender);
					Debug.log("===== nto ===="+nto);
					
					if (UtilValidate.isNotEmpty(nsender) && UtilValidate.isNotEmpty(nto)){
						
						String srTrackerUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_TRACKER_URL");
						if(UtilValidate.isNotEmpty(srTrackerUrl)){
							//srTrackerUrl = srTrackerUrl+"?srNumber="+custRequestId;
							String customFieldName = DataUtil.getGlobalValue(delegator, "FSRID_CUSTOM_FLD_NAME","FSRID");
							GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldName",customFieldName).queryFirst();
							String channelId = UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("groupId")) ? customField.getString("groupId") : "EXTERNAL_INFO";
							
							GenericValue custRequestAttribute = EntityQuery.use(delegator).from("CustRequestAttribute").where("channelId", channelId ,"custRequestId",custRequestId).queryOne();
							if(UtilValidate.isNotEmpty(custRequestAttribute)) {
								String hashValue = custRequestAttribute.getString("attrValue");
								if(UtilValidate.isNotEmpty(hashValue)) {
									srTrackerUrl = srTrackerUrl+"#"+hashValue;
								} else {
									srTrackerUrl="";
								}
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
						strVar += "<td colspan=\"3\">Dear "+ownerDesc+"</td>";
						strVar += "</tr>";
						strVar += "<tr>";
						strVar += "<td>&nbsp;</td>";
						strVar += "<td>&nbsp;</td>";
						strVar += "</tr>";
						strVar += "<tr>";
						strVar += "<td colspan=\"3\">The following support SR has been created:</td>";
						strVar += "</tr>";
						strVar += "<tr>";
						strVar += "<td>&nbsp;</td>";
						strVar += "<td>&nbsp;</td>";
						strVar += "</tr>";
						strVar += "<tr>";
						strVar += "<td width=\"15%\"><b>Party</b></td>";
						strVar += "<td>:</td>";
						strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+partyDesc+"</td>";
						strVar += "</tr>";
						strVar += "<tr>";
						strVar += "<td><b>SR ID</b></td>";
						strVar += "<td>:</td>";
						strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\"><a target=\"_blank\" href="+srTrackerUrl+"</a></td>";
						strVar += "</tr>";
						strVar += "<tr>";
						strVar += "<td><b>SR Status</b></td>";
						strVar += "<td>:</td>";
						strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+srStatusDesc+"</td>";
						strVar += "</tr>";
						strVar += "<tr>";
						strVar += "<td style=\"vertical-align: top;\"><b>SR Name</b></td>";
						strVar += "<td style=\"vertical-align: top;\">:</td>";
						strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+custRequestName+"</td>";
						strVar += "</tr>";
						strVar += "<tr>";
						strVar += "<td style=\"vertical-align: top;\"><b>Description</b></td>";
						strVar += "<td style=\"vertical-align: top;\">:</td>";
						strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+description+"</td>";
						strVar += "</tr>";
						strVar += "";
						strVar += "<tr>";
						strVar += "<td colspan=\"2\">&nbsp;</td>";
						strVar += "</tr>";
						strVar += "<tr>";
						strVar += "<td colspan=\"3\">";
						strVar += "Thanks,<br>";
						strVar += ""+signName+"";
						strVar += "</td>";
						strVar += "</tr>";
						strVar += "</table>";
						strVar += "";
						strVar += "</body>";
						strVar += "</html>";
						strVar += "";
						
						Map<String, Object> callCtxt = FastMap.newInstance();
						Map<String, Object> callResult = FastMap.newInstance();
						Map<String, Object> requestContext = FastMap.newInstance();

						requestContext.put("nsender", nsender);
						requestContext.put("nto", nto);
						requestContext.put("subject", subject);
						requestContext.put("emailContent", strVar);
						requestContext.put("ccAddresses", ccAddresses);

						callCtxt.put("requestContext", requestContext);
						callCtxt.put("userLogin", userLogin);
						
						Debug.log("===== SR CREATION EMAIL TRIGGER ===="+callCtxt);
						
						callResult = dispatcher.runSync("common.sendEmail", callCtxt);
						if (ServiceUtil.isError(callResult)) {
							String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
							return returnError(request, errMsg);
						}

					}

					request.setAttribute("externalId", custRequestId);
					request.setAttribute("_EVENT_MESSAGE_", "SR Created Successfully"+": "+custRequestId);
				}else{
					request.setAttribute("externalId", custRequestId);
					request.setAttribute("_EVENT_MESSAGE_", "SR Created Successfully"+": "+custRequestId);
				}
			} else {
				String errMsg = "";
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errMsg = accessMatrixRes.get("errorMessage").toString();
				} else {
					errMsg = "Access Denied";
				}
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				Debug.logError("error==" + errMsg, MODULE);
				return ERROR;
			}

		}
		catch (Exception e) {
			String errMsg = "Problem While Creating Service Request " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			Debug.logError(e, "Exception : "+ e.getMessage(), MODULE);
			return ERROR;
		}
		return SUCCESS;
	}
	
	public static String updateSRCLientPortalEvent(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String fromPartyId = (String) requestParameters.get("fromPartyId");
		String cNo = (String) requestParameters.get("cNo");
		String custRequestTypeId = (String) requestParameters.get("srTypeId");
		String custRequestCategoryId =  (String) requestParameters.get("srCategoryId");
		String custRequestSubCategoryId = (String) requestParameters.get("srSubCategoryId");
		String custRequestOthCategoryId = (String) requestParameters.get("otherSrSubCategoryId");
		String priorityStr = (String) requestParameters.get("priority");
		String accountType = (String) requestParameters.get("accountType");
		String custReqOnceDone = (String) requestParameters.get("onceAndDone");
		String statusId = (String) requestParameters.get("srStatusId");
		String subStatusId = (String) requestParameters.get("srSubStatusId");
		String owner = (String) requestParameters.get("owner");
		String ownerBu = (String) requestParameters.get("ownerBu");
		String custReqSrSource = (String) requestParameters.get("srSource");
		String accountNumber = (String) requestParameters.get("accountNumber");
		String description = (String) requestParameters.get("description");
		String resolution = (String) requestParameters.get("resolution");
		String notes = (String) requestParameters.get("notes");
		String parentCustRequestId = (String) requestParameters.get("parentCustRequestId");
		String primaryContactId = (String) requestParameters.get("ContactID");
		String alternateId = (String) requestParameters.get("alternateId");
		String nationalId = (String) requestParameters.get("nationalId");
		String externalId = (String) requestParameters.get("srNumber");
		String custOrderId = (String) requestParameters.get("orderId");
		String custRequestName = (String) requestParameters.get("srName");
		String reopenFlag = (String) requestParameters.get("reopenFlag");
		String externalLoginKey = (String) requestParameters.get("externalLoginKey");
		String clientPortal = (String) requestParameters.get("clientPortal");
		String customerId = (String) requestParameters.get("customerId");
		
		String inspectedBy = (String) requestParameters.get("inspectedBy");
		String inspectionDate = (String) requestParameters.get("inspectionDate");
		String installedSquare = (String) requestParameters.get("installedSquare");
		String hasAlarmSystem = (String) requestParameters.get("hasAlarmSystem");
		String installed = (String) requestParameters.get("installed");
		
		List<String> reasonCodeList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(requestParameters.get("reasonCode")) && requestParameters.get("reasonCode") instanceof String) {
			String reasonCode = (String) requestParameters.get("reasonCode");
			if(UtilValidate.isNotEmpty(reasonCode)) reasonCodeList.add(reasonCode);
		} else if(UtilValidate.isNotEmpty(requestParameters.get("reasonCode")) && requestParameters.get("reasonCode") instanceof List<?>) {
			reasonCodeList = (List<String>) requestParameters.get("reasonCode");
		}
		
		List<String> causeCategoryList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(requestParameters.get("causeCategory")) && requestParameters.get("causeCategory") instanceof String) {
			String causeCategory = (String) requestParameters.get("causeCategory");
			if(UtilValidate.isNotEmpty(causeCategory)) causeCategoryList.add(causeCategory);
		} else if(UtilValidate.isNotEmpty(requestParameters.get("causeCategory")) && requestParameters.get("causeCategory") instanceof List<?>) {
			causeCategoryList = (List<String>) requestParameters.get("causeCategory");
		}
		
		List<String> optionalAttendees = null;
		String optionalAttendeesClassName = "";
		String optionalAttendeesEmailIds = "";
		
		if (UtilValidate.isNotEmpty(requestParameters.get("optionalAttendees"))) {
			optionalAttendeesClassName = requestParameters.get("optionalAttendees").getClass().getName();
		}
		
		if (UtilValidate.isNotEmpty(optionalAttendeesClassName)
				&& "java.lang.String".equals(optionalAttendeesClassName)) {
			optionalAttendees = UtilMisc.toList((String) requestParameters.get("optionalAttendees"));
		} else if (UtilValidate.isNotEmpty(optionalAttendeesClassName)
				&& "java.util.LinkedList".equals(optionalAttendeesClassName)) {
			optionalAttendees = (List<String>) requestParameters.get("optionalAttendees");
		}
		
		if (UtilValidate.isNotEmpty(optionalAttendees)) {
			for (String eachOptionalAttendee : optionalAttendees) {
				Map<String, String> optionalAttendeeContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, eachOptionalAttendee);
				if (UtilValidate.isNotEmpty(optionalAttendeeContactInformation) && UtilValidate.isNotEmpty(optionalAttendeeContactInformation.get("EmailAddress"))){
					String optionalAttendeeEmailAddress = optionalAttendeeContactInformation.get("EmailAddress");
					optionalAttendeesEmailIds = optionalAttendeesEmailIds+optionalAttendeeEmailAddress+",";
				}
			}
			if (UtilValidate.isNotEmpty(optionalAttendeesEmailIds)) {
				optionalAttendeesEmailIds = optionalAttendeesEmailIds.substring(0, optionalAttendeesEmailIds.length()-1);
			}
		}

		try{

			GenericValue custRequest = EntityQuery.use(delegator).select("responsiblePerson").from("CustRequest").where("custRequestId", externalId).queryOne();
			String previousOwnerId = custRequest.getString("responsiblePerson");

			if(UtilValidate.isNotEmpty(cNo)){
				fromPartyId = cNo;
			}
			if (UtilValidate.isEmpty(resolution) && "SR_CLOSED".equals(statusId)) {
				String errMsg = "Resolution field is mandatory to resolve the SR";
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return ERROR;
			}
			if("SR_CLOSED".equals(statusId) || "SR_CANCELLED".equals(statusId)){

				List<GenericValue> workEffortList = null;
				List<GenericValue> custRequestWorkEffortList = EntityQuery.use(delegator).select("workEffortId").from("CustRequestWorkEffort").where("custRequestId", externalId).queryList();

				if(UtilValidate.isNotEmpty(custRequestWorkEffortList)){
					List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList, "workEffortId", true);


					List<EntityCondition> conditionlist1 = FastList.newInstance();
					conditionlist1.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
					conditionlist1.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_IN, UtilMisc.toList("IA_COMPLETED", "IA_MCOMPLETED")));

					EntityCondition condition1 = EntityCondition.makeCondition(conditionlist1, EntityOperator.AND);
					workEffortList = delegator.findList("WorkEffort", condition1,null, null, null, false);


				} 
				if(UtilValidate.isNotEmpty(workEffortList)){


					String errMsg = "There are Open Activities tagged to this " +externalId+ ". Please close the Open Activities before closing/cancelling the SR";
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					return ERROR;
				}

			}
			Map<String, Object> custRequestContext = new HashMap<String, Object>();
			Map<String, Object> supplementoryContext = new HashMap<String, Object>();

			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String businessUnit = null;
			String teamId = null;
			String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
			Map<String, Object> custReqData = DataUtil.getCustRequestDetail(delegator, externalId);
			if(UtilValidate.isNotEmpty(custReqData)) {
				businessUnit = UtilValidate.isNotEmpty(custReqData.get("businessUnit")) ? (String) custReqData.get("businessUnit") : "";
				teamId = UtilValidate.isNotEmpty(custReqData.get("teamId")) ? (String) custReqData.get("teamId") : "";
			}
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId)) {
				if(UtilValidate.isEmpty(businessUnit))
					businessUnit = DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("teamId", teamId);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "Update");
				accessMatrixMap.put("entityName", "CustRequest");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
				//validate the common team and access for the assignment
				String currentPartyId = userLoginPartyId;
				if(UtilValidate.isEmpty(owner)) {
					owner = teamId;
				} else {
					currentPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
					Map<String, Object> buTeamData = DataUtil.getUserBuTeam(delegator, currentPartyId);
					businessUnit = (String) buTeamData.get("businessUnit");
					teamId = (String) buTeamData.get("emplTeamId");
				}
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				//change the access in the create 
				//check with ownerId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					if(!ownerIds.contains(teamId)) accessLevel = null;
					conditionList.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.IN, ownerIds));
					//custRequestContext.put("ownerIds", ownerIds);
				}

				//check with emplTeamId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					if(!emplTeamIds.contains(teamId)) accessLevel = null;
					conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
					//custRequestContext.put("emplTeamIds", emplTeamIds);
				}

				if(UtilValidate.isNotEmpty(externalId)){
					conditionList.add(EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, externalId));
				}

				EntityCondition mainCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				GenericValue custRequest1 = EntityUtil
						.getFirst(delegator.findList("CustRequest", mainCondition, null, null, null, false));

				if(UtilValidate.isEmpty(custRequest1)) accessLevel=null;

			}
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				if(UtilValidate.isNotEmpty(externalId)){
					custRequestContext.put("externalId", externalId);
				}
				if(UtilValidate.isNotEmpty(custRequestTypeId)){
					custRequestContext.put("custRequestTypeId", custRequestTypeId);
				}
				if(UtilValidate.isNotEmpty(custRequestCategoryId)){
					custRequestContext.put("custRequestCategoryId", custRequestCategoryId);
				}
				if(UtilValidate.isNotEmpty(custRequestSubCategoryId)){
					custRequestContext.put("custRequestSubCategoryId", custRequestSubCategoryId);
				}
				if(UtilValidate.isNotEmpty(custRequestOthCategoryId)){
					custRequestContext.put("custRequestOthCategoryId", custRequestOthCategoryId);
				}
				if(UtilValidate.isNotEmpty(priorityStr)){
					//long  priority = (Long.parseLong(priorityStr));
					custRequestContext.put("priority", priorityStr);
				}
				if(UtilValidate.isNotEmpty(statusId)){
					custRequestContext.put("statusId", statusId);
				}
				if(UtilValidate.isNotEmpty(subStatusId)){
					custRequestContext.put("subStatusId", subStatusId);
				}
				if(UtilValidate.isNotEmpty(owner)){
					custRequestContext.put("responsiblePerson", owner);
				}
				if(UtilValidate.isNotEmpty(parentCustRequestId)){
					custRequestContext.put("parentCustRequestId", parentCustRequestId);
				}
				custRequestContext.put("ownerBu", businessUnit);
				custRequestContext.put("emplTeamId", teamId);
				/*
        	if(UtilValidate.isNotEmpty(owner)){
        		GenericValue userLoginPerson = EntityQuery.use(delegator).select("userLoginId","partyId","emplTeamId","businessUnit").from("UserLoginPerson").where("userLoginId", owner).queryOne();
        		if (UtilValidate.isNotEmpty(userLoginPerson) && UtilValidate.isNotEmpty(userLoginPerson.getString("emplTeamId")) && UtilValidate.isNotEmpty(userLoginPerson.getString("businessUnit"))) {    
        			String emplTeamId = userLoginPerson.getString("emplTeamId");
        			String businessUnit = userLoginPerson.getString("businessUnit");
        			if(UtilValidate.isNotEmpty(emplTeamId)) {
        				custRequestContext.put("emplTeamId", emplTeamId);
        			}
        			if(UtilValidate.isNotEmpty(businessUnit)) {
        				custRequestContext.put("ownerBu", businessUnit);
        			}
        		}
	        	}*/
				if(UtilValidate.isNotEmpty(custReqSrSource)){
					custRequestContext.put("custReqSrSource", custReqSrSource);
				}
				if(UtilValidate.isNotEmpty(description)){
					String tsmRoleTypeId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "tsm.roleTypeId", delegator);
					boolean isTsmUserLoggedIn = UtilValidate.isNotEmpty(PartyHelper.getFirstValidRoleTypeId(userLoginPartyId, UtilMisc.toList(tsmRoleTypeId), delegator)) ? true : false;
					if (isTsmUserLoggedIn) {
						custRequestContext.put("tsmDescription", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
					} //else {
						custRequestContext.put("description", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
					//}
				}
				if(UtilValidate.isNotEmpty(resolution)){
					custRequestContext.put("resolution", Base64.getEncoder().encodeToString(resolution.getBytes("utf-8")));
				}
				if(UtilValidate.isNotEmpty(fromPartyId)){
					custRequestContext.put("fromPartyId", fromPartyId);
				}
				if(UtilValidate.isNotEmpty(custReqOnceDone)){
					custRequestContext.put("custReqOnceDone", custReqOnceDone);
				}
				if(UtilValidate.isNotEmpty(custReqOnceDone) && "Y".equals(custReqOnceDone)){
					custRequestContext.put("statusId", "SR_CLOSED");
				}
				if(UtilValidate.isNotEmpty(custOrderId)){
					custRequestContext.put("custOrderId", custOrderId);
				}
				if(UtilValidate.isNotEmpty(custRequestName)){
					custRequestContext.put("custRequestName", custRequestName);
				}
				if(UtilValidate.isNotEmpty(accountType)){
					supplementoryContext.put("accountType", accountType);
				}
				if(UtilValidate.isNotEmpty(accountNumber)){
					supplementoryContext.put("accountNumber", accountNumber);
				}
				if(UtilValidate.isNotEmpty(reopenFlag) && "Y".equals(reopenFlag)){
					custRequestContext.put("reopenFlag", reopenFlag); 
				}
				
				// prepare postal address params [start]
				
				supplementoryContext.put("pstlAttnName", requestParameters.get("generalAttnName"));
				supplementoryContext.put("pstlAddress1", requestParameters.get("generalAddress1"));
				supplementoryContext.put("pstlAddress2", requestParameters.get("generalAddress2"));
				supplementoryContext.put("pstlPostalCode", requestParameters.get("generalPostalCode"));
				supplementoryContext.put("pstlPostalCodeExt", requestParameters.get("generalPostalCodeExt"));
				supplementoryContext.put("pstlPostalCity", requestParameters.get("generalCity"));
				supplementoryContext.put("pstlStateProvinceGeoId", requestParameters.get("generalStateProvinceGeoId"));
				supplementoryContext.put("pstlCountryGeoId", requestParameters.get("generalCountryGeoId"));
				supplementoryContext.put("pstlCountyGeoId", requestParameters.get("countyGeoId"));
				supplementoryContext.put("isBusiness", requestParameters.get("isBusiness"));
				supplementoryContext.put("isVacant", requestParameters.get("isVacant"));
				supplementoryContext.put("isUspsAddrVerified", requestParameters.get("isUspsAddrVerified"));
				Map<String, Object> corodinate = DataHelper.getGeoCoordinate(delegator, UtilMisc.toMap("zip5", (String) requestParameters.get("generalPostalCode"), "zip4", (String) requestParameters.get("generalPostalCodeExt"), "city", requestParameters.get("generalCity"), "state", requestParameters.get("generalStateProvinceGeoId"), "county", requestParameters.get("countyGeoId")));
				supplementoryContext.put("latitude", corodinate.get("latitude"));
				supplementoryContext.put("longitude", corodinate.get("longitude"));
				// prepare postal address params [end]

				Map<String, Object> inputMap = new HashMap<String, Object>();
				inputMap.put("custRequestContext", custRequestContext);
				inputMap.put("supplementoryContext", supplementoryContext);
				inputMap.put("userLogin", userLogin);
				
				Map<String, Object> outMap = dispatcher.runSync("crmPortal.updateServiceRequest", inputMap);
				
				if(!ServiceUtil.isSuccess(outMap)) {
					request.setAttribute("_ERROR_MESSAGE_", "Problem While Updating Service Request");
					return ERROR;
				}
				String custRequestId = (String) outMap.get("custRequestId");
				if(UtilValidate.isEmpty(custRequestId) || custRequestId == null){
					String errMsg = "Problem While Updating Service Request ";
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					return ERROR;
				} 

				if (UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(supplementoryContext) && (UtilValidate.isEmpty(reopenFlag))) {
					// supplementory [start]
					/*GenericValue supplementory = EntityUtil.getFirst(delegator.findByAnd("CustRequestSupplementory",
							UtilMisc.toMap("custRequestId", custRequestId), null, false));
					if (UtilValidate.isNotEmpty(supplementory)) {
						Set<String> keys = new LinkedHashSet<String>();
						keys.addAll(supplementoryContext.keySet());
						for (String key : keys) {
							if (UtilValidate.isEmpty(supplementoryContext.get(key))) {
								supplementoryContext.remove(key);
								supplementoryContext.put(key, supplementory.get(key));
							}
						}
						supplementory.putAll(supplementoryContext);
						supplementory.store();
					}*/
					// supplementory [end]
				}

				if (UtilValidate.isNotEmpty(custRequestId)) {
					
					if (UtilValidate.isNotEmpty(custRequestId)) {
						request.setAttribute("custRequestId", custRequestId);
						Map<String, Object> historyInputMap = new HashMap<String, Object>();
						historyInputMap.put("custRequestId", custRequestId);
						historyInputMap.put("userLogin", userLogin);
						
						Map<String, Object> historyOutMap = dispatcher.runSync("srPortal.createSrHistory", historyInputMap);
						//String serviceResult = createSrHistory(request,response);

						if(!ServiceUtil.isSuccess(historyOutMap)) {
							request.setAttribute("_ERROR_MESSAGE_", "Problem While Creating Service Request History");
							return ERROR;
						}
					}

					if (UtilValidate.isNotEmpty(primaryContactId)) {
						List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
						conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
						conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, primaryContactId));
						conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
						conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
						conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
						EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

						GenericValue primaryCustRequestContact = EntityUtil.getFirst( delegator.findList("CustRequestContact", mainConditons, null, null, null, false) );

						if (UtilValidate.isEmpty(primaryCustRequestContact)){

							List<EntityCondition> exprList = new LinkedList<EntityCondition>();
							exprList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
							exprList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
							exprList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
							exprList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

							EntityConditionList<EntityCondition> assocExprList = EntityCondition.makeCondition(exprList, EntityOperator.AND);
							List<GenericValue> relatedAssocs = EntityQuery.use(delegator).from("CustRequestContact").where(assocExprList).orderBy("fromDate").filterByDate().queryList();
							if (UtilValidate.isNotEmpty(relatedAssocs)){

								GenericValue existingPriContactGv = EntityUtil.getFirst(relatedAssocs);
								existingPriContactGv.set("isPrimary","");
								existingPriContactGv.store();
							}
							/*if (UtilValidate.isNotEmpty(relatedAssocs)){
        					for (GenericValue val : relatedAssocs) {
        						val.set("thruDate", UtilDateTime.nowTimestamp());
        						val.store();
        					}
        				}*/



							GenericValue custRequestContact = delegator.makeValue("CustRequestContact");
							custRequestContact.set("custRequestId",custRequestId);
							custRequestContact.set("partyId", primaryContactId);
							custRequestContact.set("roleTypeId", "CONTACT");
							custRequestContact.set("isPrimary", "Y");

							custRequestContact.set("fromDate", UtilDateTime.nowTimestamp());

							delegator.create(custRequestContact);

						}else{
							primaryCustRequestContact.put("thruDate",UtilDateTime.nowTimestamp());
							primaryCustRequestContact.store();        				

							List<EntityCondition> exprList1 = new LinkedList<EntityCondition>();
							exprList1.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
							exprList1.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
							exprList1.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
							exprList1.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

							EntityConditionList<EntityCondition> assocExprList = EntityCondition.makeCondition(exprList1, EntityOperator.AND);
							List<GenericValue> relatedAssocs1 = EntityQuery.use(delegator).from("CustRequestContact").where(assocExprList).orderBy("fromDate").filterByDate().queryList();
							if (UtilValidate.isNotEmpty(relatedAssocs1)){

								GenericValue existingPriContactGv1 = EntityUtil.getFirst(relatedAssocs1);
								existingPriContactGv1.set("isPrimary","");
								existingPriContactGv1.store();
							}


							GenericValue custRequestContactNew = delegator.makeValue("CustRequestContact");
							custRequestContactNew.set("custRequestId",custRequestId);
							custRequestContactNew.set("partyId", primaryContactId);
							custRequestContactNew.set("roleTypeId", "CONTACT");
							custRequestContactNew.set("isPrimary", "Y");

							custRequestContactNew.set("fromDate", UtilDateTime.nowTimestamp());

							delegator.create(custRequestContactNew);


						}
					}
					Debug.log("reopenFlag=========="+reopenFlag);
					if(!"SR_CLOSED".equals(statusId)){
						//To store the Optional Contacts of and SR in custRequestContact
						Debug.log("optionalAttendees=========="+optionalAttendees);
						if(UtilValidate.isNotEmpty(optionalAttendees)) {
							List<EntityCondition> optionalAttendeesConditionList = new LinkedList<EntityCondition>();
							optionalAttendeesConditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
							optionalAttendeesConditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
							optionalAttendeesConditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "N"));							
							optionalAttendeesConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
							EntityCondition optionalAttendeesMainConditons = EntityCondition.makeCondition(optionalAttendeesConditionList, EntityOperator.AND);
							
							List<GenericValue> optionalAttendeesContactList = delegator.findList("CustRequestContact", optionalAttendeesMainConditons, null, null, null, false);
							
							if(UtilValidate.isNotEmpty(optionalAttendeesContactList)){
								for(GenericValue eachExtOptionalAttendee : optionalAttendeesContactList){
									eachExtOptionalAttendee.set("thruDate", UtilDateTime.nowTimestamp());
									eachExtOptionalAttendee.store();
								}
							}
							
							for(String eachOptionalAttendee : optionalAttendees){
								GenericValue custRequestContactVal = delegator.makeValue("CustRequestContact");
								custRequestContactVal.set("custRequestId",custRequestId);
								custRequestContactVal.set("partyId", eachOptionalAttendee);
								custRequestContactVal.set("roleTypeId", "CONTACT");
								custRequestContactVal.set("isPrimary", "N");
								custRequestContactVal.set("fromDate", UtilDateTime.nowTimestamp());
								delegator.create(custRequestContactVal);
							}
							
						}else{
							if (UtilValidate.isNotEmpty(reopenFlag) && !"Y".equals(reopenFlag)) {
								List<EntityCondition> optionalAttendeesConditionList = new LinkedList<EntityCondition>();
								optionalAttendeesConditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
								optionalAttendeesConditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
								optionalAttendeesConditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "N"));
								optionalAttendeesConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
								EntityCondition optionalAttendeesMainConditons = EntityCondition.makeCondition(optionalAttendeesConditionList, EntityOperator.AND);
								
								List<GenericValue> optionalAttendeesContactList = delegator.findList("CustRequestContact", optionalAttendeesMainConditons, null, null, null, false);
								
								if(UtilValidate.isNotEmpty(optionalAttendeesContactList)) {
									for(GenericValue eachExtOptionalAttendee : optionalAttendeesContactList){
										eachExtOptionalAttendee.set("thruDate", UtilDateTime.nowTimestamp());
										eachExtOptionalAttendee.store();
									}
								}
							}
						}
					}
					
					//To store the data in custRequestParty
					/*
					if(UtilValidate.isNotEmpty(customerId)) {
						String customerRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, customerId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, customerId, customerRoleTypeId);
					}
					if(UtilValidate.isNotEmpty(fromPartyId)) {
						String fromPartyIdRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, fromPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, fromPartyId, fromPartyIdRole);
					}
					if(UtilValidate.isNotEmpty(primaryContactId)) {
						String primContactRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, primaryContactId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, primaryContactId, primContactRole);
					}
					if(UtilValidate.isNotEmpty(owner)) {
						String ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
						String ownerRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, ownerPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, ownerPartyId, ownerRole);
					}
					if(UtilValidate.isNotEmpty(userLoginId)) {
						userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
						String userLoginRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, userLoginPartyId, userLoginRole);
					}
					*/
					//To store the data in custRequestParty
					String existAccountId = "";
					String existPrimaryContactId = "";
					String existCustomerId = "";
					String existContractorId = "";
					Map<String, Object> anchorPartyMap = org.fio.sr.portal.DataHelper.getCustRequestAnchorParties(delegator, custRequestId);
					if(UtilValidate.isNotEmpty(anchorPartyMap)) {
						existAccountId = UtilValidate.isNotEmpty(anchorPartyMap.get("ACCOUNT")) ? (String) anchorPartyMap.get("ACCOUNT") : "";
						existPrimaryContactId = UtilValidate.isNotEmpty(anchorPartyMap.get("CONTACT")) ? (String) anchorPartyMap.get("CONTACT") : "";
						existCustomerId = UtilValidate.isNotEmpty(anchorPartyMap.get("CUSTOMER")) ? (String) anchorPartyMap.get("CUSTOMER") : "";
						existContractorId = UtilValidate.isNotEmpty(anchorPartyMap.get("CONTRACTOR")) ? (String) anchorPartyMap.get("CONTRACTOR") : "";
					}
					
					if(UtilValidate.isNotEmpty(customerId)) {
						if(UtilValidate.isNotEmpty(existCustomerId) && !(existCustomerId.equals(customerId))) {
							GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", "CUSTOMER","partyId",existCustomerId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequestParty)) {
								custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
								custRequestParty.store();
							}
						}
						String customerRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, customerId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, customerId, customerRoleTypeId);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, customerId, customerRoleTypeId);
					}
					
					if(UtilValidate.isNotEmpty(fromPartyId)) {
						String fromPartyIdRole = "";
						if(UtilValidate.isNotEmpty(existAccountId) && !(existAccountId.equals(fromPartyId))) {
							fromPartyIdRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, existAccountId);
							GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", fromPartyIdRole,"partyId",existAccountId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequestParty)) {
								custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
								custRequestParty.store();
							}
						}
						fromPartyIdRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, fromPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, fromPartyId, fromPartyIdRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, fromPartyId, fromPartyIdRole);
						
					}
					if(UtilValidate.isNotEmpty(primaryContactId)) {
						
						if(UtilValidate.isNotEmpty(existPrimaryContactId) && !(existPrimaryContactId.equals(primaryContactId))) {
							GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", "CONTACT","partyId",existPrimaryContactId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequestParty)) {
								custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
								custRequestParty.store();
							}
						}
						String primContactRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, primaryContactId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, primaryContactId, primContactRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, primaryContactId, primContactRole);
						
					}
					if(UtilValidate.isNotEmpty(owner)) {
						
						String existingOwner = custRequest.getString("responsiblePerson");
						if(UtilValidate.isNotEmpty(existingOwner) && !(owner.equals(existingOwner))) {
							String existingOwnerPartyId = DataUtil.getUserLoginPartyId(delegator, existingOwner);
							String existingOwnerRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, existingOwnerPartyId);
							
							GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", existingOwnerRole,"partyId",existingOwnerPartyId).queryFirst();
							if(UtilValidate.isNotEmpty(custRequestParty)) {
								custRequestParty.set("thruDate", UtilDateTime.nowTimestamp());
								custRequestParty.store();
							}
							
							org.fio.sr.portal.DataHelper.removeCustRequestAnchorParty(delegator, custRequestId, existingOwnerPartyId, existingOwnerRole);
						}
						String ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
						String ownerRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, ownerPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, ownerPartyId, ownerRole);
						org.fio.sr.portal.DataHelper.createCustRequestAnchorParty(delegator, custRequestId, ownerPartyId, ownerRole);
					}
					/*
					if(UtilValidate.isNotEmpty(userLoginId)) {
						userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
						String userLoginRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
						org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, userLoginPartyId, userLoginRole);
					}
					*/
					
					
					if(UtilValidate.isNotEmpty(reasonCodeList)) {
						EntityCondition resonCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "REASON_CODE"),
								EntityCondition.makeCondition("reasonId", EntityOperator.NOT_IN, reasonCodeList));
						delegator.removeByCondition("CustRequestResolution", resonCondition);
						for(String reasonCode : reasonCodeList) {
							org.fio.sr.portal.DataHelper.createCustReqResolution(delegator, UtilMisc.toMap("custRequestId", custRequestId, "custRequestTypeId", SrResolutionConstant.REASON_CODE,"reasonId", reasonCode));
						}
					} else {
						EntityCondition resonCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "REASON_CODE"));
						delegator.removeByCondition("CustRequestResolution", resonCondition);
					}
					
					if(UtilValidate.isNotEmpty(causeCategoryList)) {
						EntityCondition causeCatCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "CAUSE_CATEGORY"),
								EntityCondition.makeCondition("causeCategoryId", EntityOperator.NOT_IN, causeCategoryList));
						delegator.removeByCondition("CustRequestResolution", causeCatCondition);
						for(String causeCategory : causeCategoryList) {
							org.fio.sr.portal.DataHelper.createCustReqResolution(delegator, UtilMisc.toMap("custRequestId", custRequestId, "custRequestTypeId", SrResolutionConstant.CAUSE_CATEGORY, "causeCategoryId", causeCategory));
						}
					}  else {
						EntityCondition causeCatCondition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
								EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "CAUSE_CATEGORY"));
						delegator.removeByCondition("CustRequestResolution", causeCatCondition);
					}

					if (UtilValidate.isNotEmpty(alternateId)) {
						GenericValue partyIdentification = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", fromPartyId));
						partyIdentification.put("partyIdentificationTypeId", "ALTERNATE_ID");
						partyIdentification.put("idValue", alternateId);
						partyIdentification.store();
					}

					if (UtilValidate.isNotEmpty(nationalId)) {
						GenericValue partyIdentification = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", fromPartyId));
						partyIdentification.put("partyIdentificationTypeId", "NATIONAL_ID");
						partyIdentification.put("idValue", nationalId);
						partyIdentification.store();
					}

					if (UtilValidate.isNotEmpty(inspectedBy)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTED_BY", "Inspected By");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", inspectedBy);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", inspectedBy);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						/*
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTED_BY", "Inspected By");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}*/
					}
					
					if (UtilValidate.isNotEmpty(inspectionDate)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTION_DATE", "Inspection Date");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", inspectionDate);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", inspectionDate);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						/*
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSPECTION_DATE", "Inspection Date");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}*/
					}
					
					if (UtilValidate.isNotEmpty(installedSquare)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED_SQUARE", "Installed Square, Level, and Plumb");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", installedSquare);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", installedSquare);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						/*
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED_SQUARE", "Installed Square, Level, and Plumb");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}*/
					}
					
					if (UtilValidate.isNotEmpty(hasAlarmSystem)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "HAS_ALARM_SYSTEM", "Has Alarm System");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", hasAlarmSystem);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", hasAlarmSystem);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						/*
						String customFieldName = DataUtil.getGlobalValue(delegator, "HAS_ALARM_SYSTEM", "Has Alarm System");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}*/
					}
					
					if (UtilValidate.isNotEmpty(installed)) {	
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED", "Installed");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.put("attrValue", installed);
								custAttGv.store();
							}else{
								GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
								custRequestAttrbute.set("custRequestId",custRequestId);
								custRequestAttrbute.set("attrName", customFieldId);
								custRequestAttrbute.set("attrValue", installed);
								delegator.create(custRequestAttrbute);
							}
						}
					} else {
						//if we want to remove in case of value removed from UI
						/*
						String customFieldName = DataUtil.getGlobalValue(delegator, "INSTALLED", "Installed");
						String customFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", customFieldName);
						if(UtilValidate.isNotEmpty(customFieldId)) {
							GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName",customFieldId), false);
							if(UtilValidate.isNotEmpty(custAttGv)){
								custAttGv.remove();
							}
						}*/
					}
					Debug.log("== UPDATE SR owner =="+owner+"== previousOwnerId =="+previousOwnerId);
					
					String nsender = "";
					String nto = "";
					String ccAddresses = "";
					String subject = "";
					String ownerDesc = "";
					String partyDesc = "";
					String srStatusDesc = "";
					String signName = "";
					String enableEmailTrigger = "N";
					String subjectDesc = "An update has been made to the support SR:";
					
					if (UtilValidate.isNotEmpty(clientPortal) && "clientPortal".equals(clientPortal)){
						enableEmailTrigger = "Y";
						nto = (String) requestParameters.get("toEmailId");
						ownerDesc = (String) requestParameters.get("cspSupportName");
						srStatusDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId);
						
						if (UtilValidate.isNotEmpty(reopenFlag) && "Y".equals(reopenFlag)) {
							subjectDesc = "The following support SR has been reopened:";
							description = (String) requestParameters.get("selDescription");
						}
						
						if (UtilValidate.isNotEmpty(previousOwnerId)){
							ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, previousOwnerId, false);
							String responsiblePartyId = DataUtil.getUserLoginPartyId(delegator, previousOwnerId);
							Map<String, String> responsiblePersonContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,responsiblePartyId);
							ccAddresses = nto;
							nto = responsiblePersonContactInformation.get("EmailAddress");
							if(UtilValidate.isNotEmpty(optionalAttendeesEmailIds)){
								ccAddresses = ccAddresses+","+optionalAttendeesEmailIds;
							}else{
								ccAddresses = optionalAttendeesEmailIds;
							}
						}else{
							if(UtilValidate.isNotEmpty(optionalAttendeesEmailIds)){
								ccAddresses = optionalAttendeesEmailIds;
							}
						}
								
						
						Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLogin.getString("partyId"));
						//nsender = ntoContactInformation.get("EmailAddress");
						nsender = (String) requestParameters.get("fromEmailId");
						
						GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);

						if(UtilValidate.isNotEmpty(personGv)){
							signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
						}
						
						if(UtilValidate.isNotEmpty(priorityStr)){
							if ("62816".equals(priorityStr)){
								subject = "Update on SR ID "+custRequestId+" - "+custRequestName;
							}else if ("62817".equals(priorityStr)){
								subject = "Update on SR ID "+custRequestId+" - "+custRequestName;
							}else if ("62818".equals(priorityStr)){
								subject = "Update on SR ID "+custRequestId+" - "+custRequestName;
							}
						}
						
						partyDesc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, fromPartyId, false);
						
					}else if (UtilValidate.isNotEmpty(owner) /*&& UtilValidate.isNotEmpty(previousOwnerId) && !previousOwnerId.equals(owner)*/ && UtilValidate.isEmpty(clientPortal)) {
						enableEmailTrigger = "Y";
						if (UtilValidate.isEmpty(cNo) && UtilValidate.isNotEmpty(requestParameters.get("selFromPartyId"))){
							cNo = (String) requestParameters.get("selFromPartyId");
						}
						if (UtilValidate.isEmpty(description) && UtilValidate.isNotEmpty(requestParameters.get("selDescription"))){
							description = (String) requestParameters.get("selDescription");
						}
						partyDesc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, cNo, false);
						ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, owner, false);
						srStatusDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId);
						
						if (UtilValidate.isNotEmpty(owner) && UtilValidate.isNotEmpty(previousOwnerId) && !previousOwnerId.equals(owner))
							subject = "Update on SR ID "+custRequestId+" - "+custRequestName;
						else
							subject = "Update on SR ID "+custRequestId+" - "+custRequestName;

						nsender = (String) requestParameters.get("fromEmailId");

						GenericValue userLoginPerson = EntityUtil.getFirst(EntityQuery.use(delegator).select("userLoginId","partyId","firstName").from("UserLoginPerson").where("userLoginId", owner).queryList());

						Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLoginPerson.getString("partyId"));
						nto = ntoContactInformation.get("EmailAddress");

						Map<String, String> previousOwnerContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,previousOwnerId);
						if(UtilValidate.isNotEmpty(previousOwnerContactInformation)){
							ccAddresses = previousOwnerContactInformation.get("EmailAddress");
							if(UtilValidate.isNotEmpty(optionalAttendeesEmailIds)){
								ccAddresses = ccAddresses+","+optionalAttendeesEmailIds;
							}
						}else{
							ccAddresses = optionalAttendeesEmailIds;
						}
						
						signName = "CRM Administrator.";
						GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);

						if(UtilValidate.isNotEmpty(personGv)){
							signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
						}
					}
					
					Debug.log("==== UPDATE SR nsender ===="+nsender);
					Debug.log("==== UPDATE SR nto ===="+nto);
					Debug.log("==== UPDATE SR enableEmailTrigger ===="+enableEmailTrigger);
					
						if (UtilValidate.isNotEmpty(nsender) && UtilValidate.isNotEmpty(nto) && "Y".equals(enableEmailTrigger)){

							String srTrackerUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_TRACKER_URL");
							if(UtilValidate.isNotEmpty(srTrackerUrl)){
								//srTrackerUrl = srTrackerUrl+"?srNumber="+custRequestId;
								String customFieldName = DataUtil.getGlobalValue(delegator, "FSRID_CUSTOM_FLD_NAME","FSRID");
								GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldName",customFieldName).queryFirst();
								String channelId = UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("groupId")) ? customField.getString("groupId") : "EXTERNAL_INFO";
								
								GenericValue custRequestAttribute = EntityQuery.use(delegator).from("CustRequestAttribute").where("channelId", channelId ,"custRequestId",custRequestId).queryOne();
								if(UtilValidate.isNotEmpty(custRequestAttribute)) {
									String hashValue = custRequestAttribute.getString("attrValue");
									if(UtilValidate.isNotEmpty(hashValue)) {
										srTrackerUrl = srTrackerUrl+"#"+hashValue;
									} else {
										srTrackerUrl="";
									}
								}
							}
							/*
							String appUrl = "";
							GenericValue systemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "EMAIL_NOTIFICATION", "systemPropertyId", "url").queryOne();
							if(UtilValidate.isNotEmpty(systemProperty)){
								appUrl = systemProperty.getString("systemPropertyValue");
							}else{
								appUrl = (String) requestParameters.get("appUrl");
							}
							*/
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
							strVar += "<td colspan=\"3\">Dear "+ownerDesc+"</td>";
							strVar += "</tr>";
							strVar += "<tr>";
							strVar += "<td>&nbsp;</td>";
							strVar += "<td>&nbsp;</td>";
							strVar += "</tr>";
							strVar += "<tr>";
							strVar += "<td colspan=\"3\">"+subjectDesc+"</td>";
							strVar += "</tr>";
							strVar += "<tr>";
							strVar += "<td>&nbsp;</td>";
							strVar += "<td>&nbsp;</td>";
							strVar += "</tr>";
							strVar += "<tr>";
							strVar += "<td width=\"15%\"><b>Customer</b></td>";
							strVar += "<td>:</td>";
							strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+partyDesc+"</td>";
							strVar += "</tr>";
							strVar += "<tr>";
							strVar += "<td><b>SR ID</b></td>";
							strVar += "<td>:</td>";
							strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\"><a target=\"_blank\" href="+srTrackerUrl+">"+custRequestId+"</a></td>";
							strVar += "</tr>";
							strVar += "<tr>";
							strVar += "<td><b>SR Status</b></td>";
							strVar += "<td>:</td>";
							strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+srStatusDesc+"</td>";
							strVar += "</tr>";
							strVar += "<tr>";
							strVar += "<td style=\"vertical-align: top;\"><b>SR Name</b></td>";
							strVar += "<td style=\"vertical-align: top;\">:</td>";
							strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+custRequestName+"</td>";
							strVar += "</tr>";
							strVar += "<tr>";
							strVar += "<td style=\"vertical-align: top;\"><b>Description</b></td>";
							strVar += "<td style=\"vertical-align: top;\">:</td>";
							strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+description+"</td>";
							strVar += "</tr>";
							strVar += "";
							strVar += "<tr>";
							strVar += "<td colspan=\"2\">&nbsp;</td>";
							strVar += "</tr>";
							strVar += "<tr>";
							strVar += "<td colspan=\"3\">";
							strVar += "Thanks,<br>";
							strVar += ""+signName+"";
							strVar += "</td>";
							strVar += "</tr>";
							strVar += "</table>";
							strVar += "";
							strVar += "</body>";
							strVar += "</html>";
							strVar += "";

							Map<String, Object> callCtxt = FastMap.newInstance();
							Map<String, Object> callResult = FastMap.newInstance();
							Map<String, Object> requestContext = FastMap.newInstance();

							requestContext.put("nsender", nsender);
							requestContext.put("nto", nto);
							requestContext.put("subject", subject);
							requestContext.put("emailContent", strVar);
							requestContext.put("ccAddresses", ccAddresses);

							callCtxt.put("requestContext", requestContext);
							callCtxt.put("userLogin", userLogin);

							Debug.log("==== sendEmail ===="+callCtxt);
							
							callResult = dispatcher.runSync("common.sendEmail", callCtxt);
							if (ServiceUtil.isError(callResult)) {
								String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
								return returnError(request, errMsg);
							}

						}

					if (UtilValidate.isNotEmpty(reopenFlag) && "Y".equals(reopenFlag)) {
						request.setAttribute("externalId", custRequestId);
						request.setAttribute("_EVENT_MESSAGE_", "SR has been reopened successfully");
					}else{
						request.setAttribute("externalId", custRequestId);
						request.setAttribute("_EVENT_MESSAGE_", "SR Updated Successfully"+": "+custRequestId);
					}

				}else{
					request.setAttribute("externalId", custRequestId);
					request.setAttribute("_EVENT_MESSAGE_", "SR Updated Successfully"+": "+custRequestId);
				}
			} else {
				String errMsg = "";
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errMsg = accessMatrixRes.get("errorMessage").toString();
				} else {
					errMsg = "Access Denied";
				}
				request.setAttribute("_ERROR_MESSAGE_", errMsg);
				return ERROR;
			}

		}catch (Exception e) {
			String errMsg = "Problem While Updating Service Request " + e.toString();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			Debug.logError(e, "Exception : "+e.getMessage(), MODULE);
			return ERROR;
		}
		return SUCCESS;
	}
	
	
	public static String createSrActivityTimeEntry(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > context = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String workEffortId = (String) context.get("workEffortId");
		BigDecimal totalCost = UtilValidate.isNotEmpty(context.get("cost")) ? new BigDecimal((String) context.get("cost")) : BigDecimal.ZERO;
		
		String timeEntryDate = (String) context.get("timeEntryDate");
		context.remove("timeEntryDate");
		try {
			String partyId = (String) context.get("partyId");
			if(UtilValidate.isEmpty(partyId)) {
				partyId = userLogin.getString("partyId");
			}
			if(UtilValidate.isNotEmpty(workEffortId)) {
				List<EntityCondition> conditions = new ArrayList<>();
				conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
				//conditions.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_EQUAL, "IA_MCOMPLETED"));
				GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).queryFirst();
				if(UtilValidate.isEmpty(workEffort)) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, UtilProperties.getMessage(RESOURCE, "ErrorActivityNotFound", locale));
					return doJSONResponse(response, result);
				}
				Timestamp actualStartDate = workEffort.getTimestamp("actualStartDate");
				if(actualStartDate == null) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, UtilProperties.getMessage(RESOURCE, "ErrorActivityNotStartTimeEntry", locale));
					return doJSONResponse(response, result);
				}
				
				boolean isTechnicianStartAct = ResAvailUtil.isTechnicianStartedActivity(delegator, workEffortId, partyId);
				
				if(!isTechnicianStartAct) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, UtilProperties.getMessage(RESOURCE, "ErrorTechNotStartedActivity",  UtilMisc.toMap("technicianName", DataUtil.getPartyName(delegator, partyId)), locale));
					return doJSONResponse(response, result);
				}
				/*
				String currentStatusId = workEffort.getString("currentStatusId");
				if("IA_MCOMPLETED".equals(currentStatusId)) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, UtilProperties.getMessage(RESOURCE, "ErrorActivityCompleted", locale));
					return doJSONResponse(response, result);
				}
				*/
				
				String timesheetId = (String) context.get("timesheetId");
				GenericValue timeEntry = EntityQuery.use(delegator).from("TimeEntry").where("workEffortId",workEffortId).filterByDate().queryFirst();
				if(UtilValidate.isEmpty(timeEntry) || UtilValidate.isEmpty(timeEntry.getString("timesheetId"))) {
					//create Timesheet
		            ModelService service = dispatcher.getDispatchContext().getModelService("createTimesheet");
					Map<String, Object> inputContext = service.makeValid(context, "IN");
					inputContext.put("comments", workEffort.getString("workEffortName")+ " time tracking");
					result = dispatcher.runSync(service.name, inputContext);
					if(!ServiceUtil.isSuccess(result)) {
						result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						result.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(result));
						return doJSONResponse(response, result);
					}
					timesheetId = (String) result.get("timesheetId");
				} else {
					timesheetId = timeEntry.getString("timesheetId");
				}
				List<String> roles = new ArrayList<>();
				String activityOwnerRole = DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
				if(UtilValidate.isNotEmpty(activityOwnerRole) && activityOwnerRole.contains(",")) {
					roles = org.fio.admin.portal.util.DataUtil.stringToList(activityOwnerRole, ",");
				} else if(UtilValidate.isNotEmpty(activityOwnerRole)) {
					roles.add(activityOwnerRole);
				}
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
										EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,workEffort.getString("workEffortId")),
										EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId),
										EntityCondition.makeCondition("roleTypeId", EntityOperator.IN,roles)
										);
				GenericValue activityOwner = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(condition).filterByDate().queryFirst();
				if(UtilValidate.isEmpty(activityOwner)) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, UtilProperties.getMessage(RESOURCE, "ErrorNotActivityOwner", locale));
					return doJSONResponse(response, result);
				}
				String roleTypeId = activityOwner.getString("roleTypeId");
				
				GenericValue timesheetRole = EntityQuery.use(delegator).from("TimesheetRole").where("timesheetId", timesheetId, "partyId", partyId, "roleTypeId", roleTypeId).queryFirst();
				if(UtilValidate.isEmpty(timesheetRole)) {
					//create Timesheet Role
					ModelService service = dispatcher.getDispatchContext().getModelService("createTimesheetRole");
					Map<String, Object> inputContext = service.makeValid(UtilMisc.toMap("timesheetId",timesheetId, "partyId",partyId, "roleTypeId", roleTypeId,"userLogin",userLogin), "IN");
					result = dispatcher.runSync(service.name, inputContext);
					if(!ServiceUtil.isSuccess(result)) {
						result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						result.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(result));
						return doJSONResponse(response, result);
					}
				}
				
				//Create TimeEntry
				BigDecimal cost = BigDecimal.ZERO;
				double rate= 0.0d; 
				double timeEntered = 0.0d;
				String rateTypeId = (String) context.get("rateTypeId");
				if("TOLL_CHARGE".equals(rateTypeId) || "ANCILLARY_COST".equals(rateTypeId) || "LEGACY_TOLL_CHARGE".equals(rateTypeId) || "LEGACY_ANCILLARY_COST".equals(rateTypeId)) {
					cost = totalCost;
				} else {
					int hours = UtilValidate.isNotEmpty(context.get("hours")) ? Integer.parseInt((String) context.get("hours")) : 0;
					int minute = UtilValidate.isNotEmpty(context.get("minutes")) && org.fio.admin.portal.util.DataUtil.isInteger((String) context.get("minutes")) ? Integer.parseInt((String) context.get("minutes")) : 0;
					if(minute>=60) {
						int hour = (int) (minute/60);
						int remainMinutes = (int) (minute % 60);
						hours = hours + hour;
						minute = remainMinutes;
					}
					
					if(UtilValidate.isEmpty(context.get("rateTypeId"))) context.put("rateTypeId", "LABOR");
					double minHr = (double)minute/(double)60;
					BigDecimal bd = new BigDecimal(hours).add(new BigDecimal(minHr)).setScale(2, RoundingMode.HALF_UP);
					//double timeEntered = Double.parseDouble(hours+"."+minHr);
			        timeEntered = bd.doubleValue();
					rateTypeId = (String) context.get("rateTypeId");
					
					Map<String, Object> rateResult = org.fio.sr.portal.util.DataUtil.getPartyRate(delegator, UtilMisc.toMap("partyId", partyId, "rateTypeId", rateTypeId));
					if(UtilValidate.isNotEmpty(rateResult) && UtilValidate.isNotEmpty(rateResult.get("rate")))
						rate = (double) rateResult.get("rate");
					/*
					List<GenericValue> partyRole = EntityQuery.use(delegator).from("PartyRate").where("partyId", partyId, "rateTypeId", rateTypeId).filterByDate().orderBy("-lastUpdatedTxStamp").queryList();
					if(UtilValidate.isNotEmpty(partyRole)) {
						double rate= partyRole.get(0).getDouble("rate");
						cost = new BigDecimal((timeEntered * rate), MathContext.DECIMAL64);	
					}*/
					cost = new BigDecimal((timeEntered * rate), MathContext.DECIMAL64);	
				}
				ModelService service = dispatcher.getDispatchContext().getModelService("createTimeEntry");
				Map<String, Object> inputContext = service.makeValid(context, "IN");
				inputContext.put("timesheetId", timesheetId);
				inputContext.put("partyId", partyId);
				inputContext.put("hours", timeEntered);
				inputContext.put("cost", cost);
				inputContext.put("ratePerHour", new BigDecimal(rate));
				inputContext.put("timeEntryDate", UtilValidate.isNotEmpty(timeEntryDate) ? Timestamp.valueOf(df1.format(df.parse(timeEntryDate))+" 00:00:00") : UtilDateTime.nowTimestamp() );
				result = dispatcher.runSync(service.name, inputContext);
				if(!ServiceUtil.isSuccess(result)) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, ServiceUtil.getErrorMessage(result));
					return doJSONResponse(response, result);
					
				}
			}
			
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		result.put(GlobalConstants.RESPONSE_MESSAGE, UtilProperties.getMessage(RESOURCE, "ActivityTimeEntrySuccessfully", locale));
		result.put("workEffortId", workEffortId);
        return doJSONResponse(response, result);
	}
	
	public static String updateOrderLineData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> result = new HashMap<String, Object>();
        Locale locale = UtilHttp.getLocale(request);        
        String requestData = org.fio.admin.portal.util.DataUtil.getJsonStrBody(request);
        String responseMessage = "Data has been updated successfully";
        try {
        	
        	List<Map<String, Object>> dataList = org.fio.admin.portal.util.DataUtil.convertToListMap(requestData);
			List<GenericValue> toBeRemove = new LinkedList<GenericValue>();
			if(UtilValidate.isNotEmpty(dataList)) {
				TransactionUtil.begin(400000);
				Timestamp now = UtilDateTime.nowTimestamp();
				List<GenericValue> toBeStore = new ArrayList<GenericValue>();
				for(Map<String, Object> data : dataList) {
					String inspectionStatus = (String) data.get("inspectionStatus");
                    String userLoginId = userLogin.getString("userLoginId");
                    String lineItemIdentifier = (String) data.get("lineItemIdentifier");
                    GenericValue entityOrderLineAssoc = EntityQuery.use(delegator).from("EntityOrderLineAssoc").where("lineItemIdentifier", lineItemIdentifier).queryFirst();
                    if(UtilValidate.isNotEmpty(entityOrderLineAssoc)) {
                    	entityOrderLineAssoc.set("inspectionBy", userLoginId);
                    	entityOrderLineAssoc.set("inspectionDate", now);
                    	entityOrderLineAssoc.set("inspectionStatus", inspectionStatus);
                    	toBeStore.add(entityOrderLineAssoc);
                    }
				}
				if(UtilValidate.isNotEmpty(toBeStore)) {
					delegator.storeAll(toBeStore);
				}
                TransactionUtil.commit();
            } else {
                responseMessage = "Record not found.";
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, responseMessage);
                return CommonEvents.doJSONResponse(response, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionUtil.rollback();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
            return CommonEvents.doJSONResponse(response, result);
        }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
        return CommonEvents.doJSONResponse(response, result);
    }
	
	public static String updateSrAssocPartyData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> result = new HashMap<String, Object>();
        Locale locale = UtilHttp.getLocale(request);        
        String requestData = org.fio.admin.portal.util.DataUtil.getJsonStrBody(request);
        String responseMessage = "Data has been updated successfully";
        try {
        	List<Map<String, Object>> dataList = org.fio.admin.portal.util.DataUtil.convertToListMap(requestData);
			if(UtilValidate.isNotEmpty(dataList)) {
				TransactionUtil.begin(400000);
				Timestamp now = UtilDateTime.nowTimestamp();
				List<GenericValue> toBeStore = new ArrayList<GenericValue>();
				for(Map<String, Object> data : dataList) {
					String custRequestId = (String) data.get("custRequestId");
					String partyId = (String) data.get("partyId");
                    String roleTypeId = (String) data.get("roleTypeId");
                    String isEnable = (String) data.get("isEnable");
                    GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId", custRequestId, "partyId", partyId,"roleTypeId", roleTypeId).queryFirst();
                    if(UtilValidate.isNotEmpty(custRequestParty)) {
                    	custRequestParty.set("isEnable", isEnable);
                    	toBeStore.add(custRequestParty);
                    }
				}
				if(UtilValidate.isNotEmpty(toBeStore)) {
					delegator.storeAll(toBeStore);
				}
                TransactionUtil.commit();
            } else {
                responseMessage = "Record not found.";
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, responseMessage);
                return CommonEvents.doJSONResponse(response, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionUtil.rollback();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
            return CommonEvents.doJSONResponse(response, result);
        }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
        return CommonEvents.doJSONResponse(response, result);
    }
	
	public static String createCommunicationHistorySmsEvent(HttpServletRequest request, HttpServletResponse response) {
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String custRequestId = (String) requestParameters.get("custRequestId");
		String partyIdReq = (String) requestParameters.get("partyId");
		String statusId = (String) requestParameters.get("srStatusId");
		Map<String, Object> result = new HashMap<String, Object>();
		String custRequestName = (String) requestParameters.get("srName");
		String messageContent = (String) requestParameters.get("messageContent");
		List<String> commHisSmsPartyIds = new ArrayList<>(); 
		List<String> mediaUrls = new ArrayList<>(); 
		String commHisSmsPartyIdsClassName = "";

		if (UtilValidate.isNotEmpty(requestParameters.get("commHisSmsPartyId"))) {
			commHisSmsPartyIdsClassName = requestParameters.get("commHisSmsPartyId").getClass().getName();
		}

		if (UtilValidate.isNotEmpty(commHisSmsPartyIdsClassName)
				&& "java.lang.String".equals(commHisSmsPartyIdsClassName)) {
			commHisSmsPartyIds = UtilMisc.toList((String) requestParameters.get("commHisSmsPartyId"));
		} else if (UtilValidate.isNotEmpty(commHisSmsPartyIdsClassName)
				&& "java.util.LinkedList".equals(commHisSmsPartyIdsClassName)) {
			commHisSmsPartyIds = (List<String>) requestParameters.get("commHisSmsPartyId");
		}
		
		String mediaUrlsClassName = "";

		if (UtilValidate.isNotEmpty(requestParameters.get("mediaUrl"))) {
			mediaUrlsClassName = requestParameters.get("mediaUrl").getClass().getName();
		}

		if (UtilValidate.isNotEmpty(mediaUrlsClassName)
				&& "java.lang.String".equals(mediaUrlsClassName)) {
			mediaUrls = UtilMisc.toList((String) requestParameters.get("mediaUrl"));
		} else if (UtilValidate.isNotEmpty(mediaUrlsClassName)
				&& "java.util.LinkedList".equals(mediaUrlsClassName)) {
			mediaUrls = (List<String>) requestParameters.get("mediaUrl");
		}
		try{
			GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryOne();
			if(UtilValidate.isNotEmpty(custRequest)) {
				Map<String, Object> srPartyPhoneNumMap = new HashMap<String, Object>();
				String includeCountryCode = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "INCLUDE_COUNTRY_CODE", "Y"); 
				for(String partyId : commHisSmsPartyIds) {
					String primaryPhone = PartyHelper.getContactNumber(delegator, partyId, "PRIMARY_PHONE", includeCountryCode, false);
					if(UtilValidate.isEmpty(primaryPhone))
						primaryPhone = PartyHelper.getContactNumber(delegator, partyId, "PRIMARY_MOBILE", includeCountryCode, false);
					srPartyPhoneNumMap.put(partyId, primaryPhone);
				}
				if(UtilValidate.isNotEmpty(srPartyPhoneNumMap)) {

					Map<String, Object> contentInfo = new HashMap<String, Object>();
					contentInfo.put("content", messageContent);
					contentInfo.put("SR_NUMBER", custRequestId);

					String srTrackerUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_TRACKER_URL");
					if(UtilValidate.isNotEmpty(srTrackerUrl)){
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
								}
							} else {
								String encodedCustReqId = Base64.getEncoder().encodeToString(custRequestId.getBytes("utf-8"));
								if(UtilValidate.isNotEmpty(encodedCustReqId)) {
									srTrackerUrl = srTrackerUrl+"#"+encodedCustReqId;
								}
							}
						} else {
							String encodedCustReqId = Base64.getEncoder().encodeToString(custRequestId.getBytes("utf-8"));
							if(UtilValidate.isNotEmpty(encodedCustReqId)) {
								srTrackerUrl = srTrackerUrl+"#"+encodedCustReqId;
							}
						}

					}
					srTrackerUrl = Objects.toString(srTrackerUrl, "");
					if(UtilValidate.isNotEmpty(srTrackerUrl)) {
						contentInfo.put("SR_SHORTURL", srTrackerUrl);
						contentInfo.put("SR_TRACKER_URL", srTrackerUrl);
					}
					contentInfo.put("SR_NAME", custRequestName);

					//messageContent = org.fio.admin.portal.util.DataUtil.extractContentWithTag(contentInfo);

					// prepare email content [start]
					Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
					
					extractContext.put("delegator", delegator);
					extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
					extractContext.put("custRequestId", custRequestId);
					extractContext.put("partyId", partyIdReq);
					extractContext.put("emailContent", messageContent);
					extractContext.putAll(contentInfo);

					Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
					messageContent = (String) extractResultContext.get("emailContent");
					// prepare email content [end]

					boolean triggerMessage = false;
					if(UtilValidate.isNotEmpty(messageContent)) {
						for(String srPartyId : srPartyPhoneNumMap.keySet()) {
							String phoneNumber = (String) srPartyPhoneNumMap.get(srPartyId);
							if(UtilValidate.isNotEmpty(phoneNumber)) {
								triggerMessage = true;
								break;
							}
						}
					}
					Set<String> phoneNumbers = new LinkedHashSet<>();
					for(String srPartyId : srPartyPhoneNumMap.keySet()) {
						String phoneNumber = (String) srPartyPhoneNumMap.get(srPartyId);
						if(UtilValidate.isNotEmpty(phoneNumber)) {
							phoneNumbers.add(phoneNumber);
						}
					}
					if(UtilValidate.isNotEmpty(phoneNumbers) && UtilValidate.isNotEmpty(messageContent)) {
						Map<String, Object> ctx = new HashMap<String, Object>();
						Map<String, Object> requestContext = new HashMap<String, Object>();
						String telnyxApiType = "SEND_MESSAGE";
						if(phoneNumbers.size() > 1)
							telnyxApiType = "GROUP_MMS_MESSAGE";
						requestContext.put("domainEntityType", "SERVICE_REQUEST");
						requestContext.put("domainEntityId", custRequestId);
						ctx.put("requestContext", requestContext);
						ctx.put("toPhoneNumber", UtilMisc.toList(phoneNumbers));
						ctx.put("smsBody", messageContent);
						ctx.put("telnyxApiType", telnyxApiType);
						ctx.put("userLogin", userLogin);
						Map<String, Object> smsResult =  null;
						if(UtilValidate.isNotEmpty(mediaUrls)){
							ctx.put("mediaUrls", mediaUrls);
							ctx.put("telnyxApiType", "GROUP_MMS_MESSAGE");
							smsResult = dispatcher.runSync("msg.sendTelnyxMms", ctx);
						}else {
							smsResult = dispatcher.runSync("msg.sendTelnyxSms", ctx);
						}
						if(ServiceUtil.isError(smsResult) || ServiceUtil.isFailure(smsResult)) {
							request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(smsResult));
							return "error";
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("_EVENT_MESSAGE_", "Sent SMS Successfully");
		return "success";
	}
}


