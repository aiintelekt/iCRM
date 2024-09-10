
package org.fio.ticket.portal.event;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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
import org.fio.homeapps.util.UtilMessage;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
import org.groupfio.common.portal.util.SrUtil;
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
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
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
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String workEffortTypeId = (String) requestParameters.get("workEffortTypeId");
		String workEffortId = (String) requestParameters.get("workEffortId");
		String srTypeId = (String) requestParameters.get("srTypeId");
		String srSubTypeId = (String) requestParameters.get("srSubTypeId");
		String actualStartDateStr = (String) requestParameters.get("actualStartDate");
		String actualCompletionDateStr = (String) requestParameters.get("actualCompletionDate");
		String estimatedStartDateStr = (String) requestParameters.get("estimatedStartDate");
		String estimatedCompletionDateStr = (String) requestParameters.get("estimatedCompletionDate");
		String callDateTime = (String) requestParameters.get("callDateTime");
		String subject = (String) requestParameters.get("subject");
		String resolution = (String) requestParameters.get("resolution");
		String linkedFrom = (String) requestParameters.get("linkedFrom");
		String productId = (String) requestParameters.get("productId");
		String account = (String) requestParameters.get("account");
		String accountProduct = (String) requestParameters.get("accountProduct");
		String emailFormContent = (String) requestParameters.get("emailFormContent");
		String onceDone = (String) requestParameters.get("onceDone");
		String messages = (String) requestParameters.get("messages");
		String location = (String) requestParameters.get("location");
		String startTimeStr = (String) requestParameters.get("startTime");
		String duration = (String) requestParameters.get("duration");
		String salesOpportunityId = (String) requestParameters.get("salesOpportunityId");
		String owner = (String) requestParameters.get("owner");
		String emplTeamId = (String) request.getParameter("emplTeamId");
		String currentStatusId = (String) request.getParameter("statusId");
		String entityTimeZoneId = (String) requestParameters.get("timeZoneDesc");
		String ownerBu = (String) requestParameters.get("ownerBu");
		String customerCIN = (String) requestParameters.get("cNo");
		String priority = (String) requestParameters.get("priority");
		String direction = (String) requestParameters.get("direction");
		String phoneNumber = (String) requestParameters.get("phoneNumber");
		String nsender = (String) requestParameters.get("nsender");
		String nto = (String) requestParameters.get("nto");
		String ncc = (String) requestParameters.get("ncc");
		String nbcc = (String) requestParameters.get("nbcc");
		String template = (String) requestParameters.get("template");
		//String optionalAttendees = (String) requestParameters.get("optionalAttendees");
		//String requiredAttendees = (String) requestParameters.get("requiredAttendees");
		List<String> optionalAttendees = null;
		List<String> requiredAttendees = null;

		String optionalAttendeesClassName= "";
		String requiredAttendeesClassName = "";

		if (UtilValidate.isNotEmpty(requestParameters.get("optionalAttendees"))){
			optionalAttendeesClassName = requestParameters.get("optionalAttendees").getClass().getName();
		}
		if (UtilValidate.isNotEmpty(requestParameters.get("requiredAttendees"))){
			requiredAttendeesClassName = requestParameters.get("requiredAttendees").getClass().getName();
		}

		if (UtilValidate.isNotEmpty(optionalAttendeesClassName) && "java.lang.String".equals(optionalAttendeesClassName)) {
			optionalAttendees = UtilMisc.toList((String) requestParameters.get("optionalAttendees"));
		}else if(UtilValidate.isNotEmpty(optionalAttendeesClassName) && "java.util.LinkedList".equals(optionalAttendeesClassName) ){
			optionalAttendees = (List<String>) requestParameters.get("optionalAttendees");
		}

		if (UtilValidate.isNotEmpty(requiredAttendeesClassName) && "java.lang.String".equals(requiredAttendeesClassName)) {
			requiredAttendees = UtilMisc.toList((String) requestParameters.get("requiredAttendees"));
		}else if(UtilValidate.isNotEmpty(requiredAttendeesClassName) && "java.util.LinkedList".equals(requiredAttendeesClassName) ){
			requiredAttendees = (List<String>) requestParameters.get("requiredAttendees");
		}

		String nrecepient = (String) requestParameters.get("nrecepient");
		String norganizer = (String) requestParameters.get("norganizer");
		String isPhoneCall = (String) requestParameters.get("isPhoneCall");
		String domainEntityType = (String) requestParameters.get("domainEntityType");
		String domainEntityId = (String) requestParameters.get("domainEntityId");
		String communicationEventId = (String) requestParameters.get("communicationEventId");
		String emailContent = (String) requestParameters.get("emailContent");
		boolean existing = ((communicationEventId == null) || communicationEventId.equals("") ? false : true);
		String contactId = (String) request.getParameter("contactId");
		String responseMessage = UtilProperties.getMessage(RESOURCE, "ActivityUpdatedSuccessfully", locale);
		
		String estStartDate = (String) requestParameters.get("estimatedStartDate_date");
		String estCompletionDate = (String) requestParameters.get("estimatedCompletionDate_date");
		String actStartDate = (String) requestParameters.get("actualStartDate_date");
		String actCompletionDate = (String) requestParameters.get("actualCompletionDate_date");
		
		String estimatedStartTime = (String) requestParameters.get("estimatedStartDate_time");
		String estimatedCompletionTime = (String) requestParameters.get("estimatedCompletionDate_time");
		String actualStartTime = (String) requestParameters.get("actualStartDate_time");
		String actualCompletionTime = (String) requestParameters.get("actualCompletionDate_time");
		
		try {
			Map<String, Object> inMap = new HashMap<String, Object>();
			if(UtilValidate.isNotEmpty(workEffortId)){
				
				Timestamp estimatedStartDateTime = ParamUtil.getTimestamp(estStartDate, estimatedStartTime, "yyyy-MM-dd hh:mm");
				Timestamp estimatedCompletionDateTime = ParamUtil.getTimestamp(estCompletionDate, estimatedCompletionTime, "yyyy-MM-dd hh:mm");
				Timestamp actualStartDateTime = ParamUtil.getTimestamp(actStartDate, actualStartTime, "yyyy-MM-dd hh:mm");
				Timestamp actualCompletionDateTime = ParamUtil.getTimestamp(actCompletionDate, actualCompletionTime, "yyyy-MM-dd hh:mm");
				
				if (UtilValidate.isNotEmpty(estimatedStartDateTime)) {
					estimatedStartDateStr = UtilDateTime.timeStampToString(estimatedStartDateTime, "MM/dd/yyyy HH:mm", TimeZone.getDefault(), null);
				}
				if (UtilValidate.isNotEmpty(estimatedCompletionDateTime)) {
					estimatedCompletionDateStr = UtilDateTime.timeStampToString(estimatedCompletionDateTime, "MM/dd/yyyy HH:mm", TimeZone.getDefault(), null);
				}
				if (UtilValidate.isNotEmpty(actualStartDateTime)) {
					actualStartDateStr = UtilDateTime.timeStampToString(actualStartDateTime, "MM/dd/yyyy HH:mm", TimeZone.getDefault(), null);
				}
				if (UtilValidate.isNotEmpty(actualCompletionDateTime)) {
					actualCompletionDateStr = UtilDateTime.timeStampToString(actualCompletionDateTime, "MM/dd/yyyy HH:mm", TimeZone.getDefault(), null);
				}
				
				Timestamp date = null;
				/*if(UtilValidate.isNotEmpty(dateStr)){
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					date = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
				}*/
				if(UtilValidate.isNotEmpty(startTimeStr)){
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
					date = new java.sql.Timestamp(sdf.parse(startTimeStr).getTime());
				}

				Timestamp callDate = null;
				if(UtilValidate.isNotEmpty(callDateTime)){
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
					callDate = new java.sql.Timestamp(sdf.parse(callDateTime).getTime());
				}
				Timestamp actualStartDate = null;
				if(UtilValidate.isNotEmpty(actualStartDateStr)){
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
					actualStartDate = new java.sql.Timestamp(sdf.parse(actualStartDateStr).getTime());
				}
				Timestamp actualCompletionDate = null;
				if(UtilValidate.isNotEmpty(actualCompletionDateStr)){
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
					actualCompletionDate = new java.sql.Timestamp(sdf.parse(actualCompletionDateStr).getTime());
				}
				Timestamp estimatedStartDate = null;
				if(UtilValidate.isNotEmpty(estimatedStartDateStr)){
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
					estimatedStartDate = new java.sql.Timestamp(sdf.parse(estimatedStartDateStr).getTime());
				}
				Timestamp estimatedCompletionDate = null;
				if(UtilValidate.isNotEmpty(estimatedCompletionDateStr)){
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
					estimatedCompletionDate = new java.sql.Timestamp(sdf.parse(estimatedCompletionDateStr).getTime());
				}
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
				GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", owner).queryList());
				if (UtilValidate.isNotEmpty(owner)&&UtilValidate.isNotEmpty(emplTeam)) { 
					if (UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId")))
						emplTeamId = emplTeam.getString("emplTeamId");
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
				if(UtilValidate.isNotEmpty(date)) {
					inMap.put("estimatedStartDate", date);
				}
				if(UtilValidate.isNotEmpty(actualCompletionDate)) {
					inMap.put("actualCompletionDate", actualCompletionDate);
				}
				if(UtilValidate.isNotEmpty(estimatedCompletionDate)) {
					inMap.put("estimatedCompletionDate", estimatedCompletionDate);
				}
				if(UtilValidate.isNotEmpty(estimatedStartDate)) {
					inMap.put("estimatedStartDate", estimatedStartDate);
				}
				if(UtilValidate.isNotEmpty(actualStartDate)) {
					inMap.put("actualStartDate", actualStartDate);
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
				if (UtilValidate.isNotEmpty(owner)) {
					inMap.put("primOwnerId", owner);
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

				/*EntityCondition rolecondition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
						EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "_NA_"));

				GenericValue roleIdentification = EntityUtil.getFirst(delegator.findList("PartyRole", rolecondition, null, null, null, false));
				String roleTypeId = null;
				if (UtilValidate.isNotEmpty(roleIdentification)) {
					roleTypeId = roleIdentification.getString("roleTypeId");
				}*/

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

				inMap.put("endPointType", "OFCRM");
				inMap.put("domainEntityType", domainEntityType);
				inMap.put("domainEntityId", domainEntityId);
				if(UtilValidate.isNotEmpty(onceDone) && "Y".equals(onceDone)){
					inMap.put("currentStatusId", "IA_MCOMPLETED");
				}

				Debug.log("inMap----------"+inMap);
				Map<String, Object> res = dispatcher.runSync("crmPortal.updateInteractiveActivity", inMap); //dispatcher.runSync("ticket.UpdateActivity", inputMap);
				Debug.log("res----------"+res);
				Map<String, Object> tmpResult = null;
				String extContactId="";
				if (ServiceUtil.isSuccess(res) && UtilValidate.isNotEmpty(res.get("workEffortId"))) {

					GenericValue updateContactRecords = EntityUtil.getFirst(delegator.findByAnd("WorkEffortContact", UtilMisc.toMap("workEffortId", workEffortId,"thruDate",null), null, false));
					if(UtilValidate.isNotEmpty(updateContactRecords))
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
					}
					request.setAttribute("_EVENT_MESSAGE_", res.get("_EVENT_MESSAGE_"));
					request.setAttribute("workEffortId", res.get("workEffortId"));
				}
				else{
					request.setAttribute("_ERROR_MESSAGE_", res.get("_EVENT_MESSAGE_"));
					return ERROR;

				}
			}else{
				String errMsg = "Please check Activity id   is Missing in Dyna";
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

		String estimated = (String) requestParameters.get("estimated");
		String custApprovalStatus = (String) requestParameters.get("custApprovalStatus");
		String billed = (String) requestParameters.get("billed");

		String srAmount = (String) requestParameters.get("srAmount");

		try{
			String isEnableSrOutboundEmail = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_ENBL_SROB_EMAIL");

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

			if(UtilValidate.isEmpty(cNo)){
				cNo = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_DEFAULT_CUSTOMER_ID");
			}
			if(UtilValidate.isNotEmpty(cNo)){
				fromPartyId = cNo;
			}

			String isReqResolution = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_REQUIRED_RESOLUTION", "Y");
			if (UtilValidate.isEmpty(resolution) && "SR_CLOSED".equals(statusId) && (UtilValidate.isEmpty(isReqResolution) || isReqResolution.equalsIgnoreCase("Y"))) {
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

			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
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
					if(UtilValidate.isEmpty(owner)) {
						owner=userLoginId;
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

			//accessLevel = "Y";
			if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
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

				Debug.log("nativeTeamId> "+nativeTeamId+", nativeBusinessUnit> "+nativeBusinessUnit, MODULE);
				custRequestContext.put("emplTeamId", nativeTeamId);
				custRequestContext.put("ownerBu", nativeBusinessUnit);
				custRequestContext.put("custRequestDomainType", "SERVICE");
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
					custRequestContext.put("description", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
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
				if(UtilValidate.isNotEmpty(notes)){
					//supplementoryContext.put("internalComment", notes);
				}

				/*EntityCondition rolecondition = EntityCondition.makeCondition(EntityOperator.AND,
				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, fromPartyId),
				EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "_NA_"));

        GenericValue roleIdentification = EntityUtil
				.getFirst(delegator.findList("PartyRole", rolecondition, null, null, null, false));
		String roleTypeId = null;
		if (UtilValidate.isNotEmpty(roleIdentification)) {
			roleTypeId = roleIdentification.getString("roleTypeId");
		}*/

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

					if (UtilValidate.isNotEmpty(custRequestId)) {
						request.setAttribute("custRequestId", custRequestId);

						Map<String, Object> historyInputMap = new HashMap<String, Object>();
						historyInputMap.put("custRequestId", custRequestId);
						historyInputMap.put("userLogin", userLogin);

						Map<String, Object> historyOutMap = dispatcher.runSync("ticket.createSrHistory", historyInputMap);
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

					if (UtilValidate.isNotEmpty(estimated)) {						
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "ESTIMATED");
						custRequestAttrbute.set("attrValue", estimated);
						custRequestAttrbute.set("sequenceNumber", "0");
						delegator.create(custRequestAttrbute);
					}

					if (UtilValidate.isNotEmpty(custApprovalStatus)) {						
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "CUST_APPROVAL_STATUS");
						custRequestAttrbute.set("attrValue", custApprovalStatus);
						custRequestAttrbute.set("sequenceNumber", "0");
						delegator.create(custRequestAttrbute);
					}
					if (UtilValidate.isNotEmpty(billed)) {						
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "BILLED");
						custRequestAttrbute.set("attrValue", billed);
						custRequestAttrbute.set("sequenceNumber", "0");
						delegator.create(custRequestAttrbute);
					}

					String isShoppedBefore = (String) requestParameters.get("isShoppedBefore");
					if (UtilValidate.isNotEmpty(isShoppedBefore)) {
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "IS_SHOPPED_BEFORE");
						custRequestAttrbute.set("attrValue", isShoppedBefore);
						custRequestAttrbute.set("sequenceNumber", "0");
						delegator.create(custRequestAttrbute);
					}

					String location = (String) requestParameters.get("location");
					String locationCustomFieldId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LOC_CF_ID");
					if (UtilValidate.isNotEmpty(location)) {						
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", locationCustomFieldId);
						custRequestAttrbute.set("attrValue", location);
						delegator.create(custRequestAttrbute);
					}

					if (UtilValidate.isNotEmpty(srAmount)) {
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","SR_AMOUNT"), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", srAmount);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "SR_AMOUNT");
							custRequestAttrbute.set("attrValue", srAmount);
							delegator.create(custRequestAttrbute);
						}
					} else {
						//if we want to remove in case of value removed from UI

					}

					//To store the data in custRequestParty
					if(UtilValidate.isNotEmpty(customerId)) {
						String customerRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, customerId);
						SrUtil.createCustRequestParty(delegator, custRequestId, customerId, customerRoleTypeId);
						SrUtil.createCustRequestAnchorParty(delegator, custRequestId, customerId, customerRoleTypeId);
					}
					if(UtilValidate.isNotEmpty(fromPartyId)) {
						String fromPartyIdRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, fromPartyId);
						SrUtil.createCustRequestParty(delegator, custRequestId, fromPartyId, fromPartyIdRole);
						SrUtil.createCustRequestAnchorParty(delegator, custRequestId, fromPartyId, fromPartyIdRole);
					}
					if(UtilValidate.isNotEmpty(owner)) {
						String ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
						String ownerRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, ownerPartyId);
						SrUtil.createCustRequestParty(delegator, custRequestId, ownerPartyId, ownerRole);
						SrUtil.createCustRequestAnchorParty(delegator, custRequestId, ownerPartyId, ownerRole);
					}
					if(UtilValidate.isNotEmpty(userLoginId)) {
						String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
						String userLoginRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
						SrUtil.createCustRequestParty(delegator, custRequestId, userLoginPartyId, userLoginRole);
						//SrUtil.createCustRequestAnchorParty(delegator, custRequestId, userLoginPartyId, userLoginRole);
					}

					/*try {
						SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
						Connection con = (Connection)sqlProcessor.getConnection();
						CallableStatement cstmt = null;
						ResultSet rs = null;
						cstmt = con.prepareCall("{call cust_request_specific_sp(?)}");
						cstmt.setString(1, custRequestId);
						rs = cstmt.executeQuery();
					} catch (Exception e) {
						e.printStackTrace();
						Debug.logError("Error to call cust_request_specific_sp script: "+e.getMessage(), MODULE);
					}*/

					
					/** Create FSR after ticket creation **/
					String isEnableAutoFsrCreate = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "AUTO_FSR_CREATE_FROM_TICKET","N");
					if (isEnableAutoFsrCreate.equalsIgnoreCase("Y")) {
						String defaultCountry = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_COUNTRY","USA");
						Map<String, Object> inputMap1 = new HashMap<String, Object>();
						supplementoryContext.put("pstlCountryGeoId", defaultCountry);
						supplementoryContext.put("domainEntityId", custRequestId);
						supplementoryContext.put("domainEntityType", "SERVICE");
						custRequestContext.remove("custRequestDomainType");
						inputMap1.put("custRequestContext", custRequestContext);
						inputMap1.put("supplementoryContext", supplementoryContext);
						inputMap1.put("userLogin", userLogin);

						Map<String, Object> srOutMap = dispatcher.runSync("crmPortal.createServiceRequest", inputMap1);
						String fsrCustRequestId = (String) srOutMap.get("custRequestId");
						Debug.logError("srOutMap==" + srOutMap, MODULE);
						if (!ServiceUtil.isSuccess(srOutMap) || UtilValidate.isEmpty(srOutMap.get("custRequestId"))) {

							request.setAttribute("_ERROR_MESSAGE_", "Problem While Auto Creating Service Request");
							return ERROR;
						}else if (UtilValidate.isNotEmpty(fsrCustRequestId)) {

							if (UtilValidate.isNotEmpty(fsrCustRequestId)) {
								GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", fsrCustRequestId).queryOne();
								if (UtilValidate.isNotEmpty(custRequest)) {
									custRequest.put("custReqDocumentNum", "");
									custRequest.store();
								}
							}

							Map<String, Object> historyInputMap = new HashMap<String, Object>();
							historyInputMap.put("custRequestId", fsrCustRequestId);
							historyInputMap.put("userLogin", userLogin);

							Map<String, Object> historyOutMap = dispatcher.runSync("srPortal.createSrHistory", historyInputMap);
							if(!ServiceUtil.isSuccess(historyOutMap)) {
								request.setAttribute("_ERROR_MESSAGE_", "Problem While Creating Service Request History");
								return ERROR;
							}
							
							if (UtilValidate.isNotEmpty(primaryContactId)) {
								GenericValue custRequestContact = delegator.makeValue("CustRequestContact");
								custRequestContact.set("custRequestId",fsrCustRequestId);
								custRequestContact.set("partyId", primaryContactId);
								custRequestContact.set("roleTypeId", "CONTACT");
								custRequestContact.set("isPrimary", "Y");

								custRequestContact.set("fromDate", UtilDateTime.nowTimestamp());
								delegator.create(custRequestContact);
							}


							String userLoginPartyId = userLogin.getString("partyId");
							//To store the data in custRequestParty
							if(UtilValidate.isNotEmpty(customerId)) {
								String customerRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, customerId);
								SrUtil.createCustRequestParty(delegator, fsrCustRequestId, customerId, customerRoleTypeId);
								SrUtil.createCustRequestAnchorParty(delegator, fsrCustRequestId, customerId, customerRoleTypeId);
							}
							if(UtilValidate.isNotEmpty(fromPartyId)) {
								String fromPartyIdRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, fromPartyId);
								SrUtil.createCustRequestParty(delegator, fsrCustRequestId, fromPartyId, fromPartyIdRole);
								SrUtil.createCustRequestAnchorParty(delegator, fsrCustRequestId, fromPartyId, fromPartyIdRole);
							}
							String primContactRole = "";
							if(UtilValidate.isNotEmpty(primaryContactId)) {
								primContactRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, primaryContactId);
								SrUtil.createCustRequestParty(delegator, fsrCustRequestId, primaryContactId, primContactRole);
								SrUtil.createCustRequestAnchorParty(delegator, fsrCustRequestId, primaryContactId, primContactRole);
							}
							if(UtilValidate.isNotEmpty(owner)) {
								String ownerPartyId = DataUtil.getUserLoginPartyId(delegator, owner);
								String ownerRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, ownerPartyId);
								SrUtil.createCustRequestParty(delegator, fsrCustRequestId, ownerPartyId, ownerRole);
								SrUtil.createCustRequestAnchorParty(delegator, fsrCustRequestId, ownerPartyId, ownerRole);
							}
							if(UtilValidate.isNotEmpty(userLoginId)) {
								userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
								String userLoginRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
								if(UtilValidate.isNotEmpty(primContactRole) && !(primContactRole.equals(userLoginRole))) {
									SrUtil.createCustRequestParty(delegator, fsrCustRequestId, userLoginPartyId, userLoginRole);
								}
							}
						}
					}
					
					if (UtilValidate.isNotEmpty(isEnableSrOutboundEmail) && isEnableSrOutboundEmail.equals("N")) {
						request.setAttribute("externalId", custRequestId);
						request.setAttribute("_EVENT_MESSAGE_", "SR Created Successfully"+": "+custRequestId);
						return SUCCESS;
					}

					String partyDesc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, cNo, false);
					String ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, owner, false);
					String srStatusDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId);

					String subject ="";
					if (UtilValidate.isEmpty(custRequestName)) {
						subject = "Ticket Created ["+custRequestId+"]";
					}else {
						subject = "Ticket Created ["+custRequestId+"] - "+custRequestName;
					}

					String nsender = "";
					String nto = "";
					String ccAddresses = "";
					String signName = "";

					if (UtilValidate.isNotEmpty(clientPortal) && "clientPortal".equals(clientPortal)){
						nto = (String) requestParameters.get("toEmailId");
						ownerDesc = (String) requestParameters.get("cspSupportName");

						String primaryContactEmail = null;
						if (UtilValidate.isNotEmpty(primaryContactId)) {
							primaryContactEmail = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, primaryContactId, "PRIMARY_EMAIL");
						}

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
						if(UtilValidate.isNotEmpty(priorityStr) && UtilValidate.isEmpty(custRequestName)){
							if ("62816".equals(priorityStr)){
								subject = "Sev.3 : Ticket Created ["+custRequestId+"]";
							}else if ("62817".equals(priorityStr)){
								subject = "Sev.2 : Ticket Created ["+custRequestId+"]";
							}else if ("62818".equals(priorityStr)){
								subject = "Sev.1 : Ticket Created ["+custRequestId+"]";
							}
						}else if(UtilValidate.isNotEmpty(priorityStr) && UtilValidate.isNotEmpty(custRequestName)){
							if ("62816".equals(priorityStr)){
								subject = "Sev.3 : Ticket Created ["+custRequestId+"] - "+custRequestName;
							}else if ("62817".equals(priorityStr)){
								subject = "Sev.2 : Ticket Created ["+custRequestId+"] - "+custRequestName;
							}else if ("62818".equals(priorityStr)){
								subject = "Sev.1 : Ticket Created ["+custRequestId+"] - "+custRequestName;
							}
						}

						if (UtilValidate.isNotEmpty(primaryContactEmail)) {
							ccAddresses = UtilValidate.isNotEmpty(ccAddresses) ? ccAddresses+","+primaryContactEmail : primaryContactEmail;
						}

						if (UtilValidate.isNotEmpty(optionalAttendeesEmailIds)) {
							ccAddresses = UtilValidate.isNotEmpty(ccAddresses) ? ccAddresses+","+optionalAttendeesEmailIds : optionalAttendeesEmailIds;
						}

					}else{
						signName = "CRM Administrator.";

						GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);

						if(UtilValidate.isNotEmpty(personGv)){
							signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
						}

						GenericValue sytemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryOne();
						if(UtilValidate.isNotEmpty(sytemProperty)){
							nsender = sytemProperty.getString("systemPropertyValue");
						}else{
							nsender = (String) requestParameters.get("fromEmailId");
						}
						GenericValue userLoginPerson = EntityUtil.getFirst(EntityQuery.use(delegator).select("userLoginId","partyId","firstName").from("UserLoginPerson").where("userLoginId", owner).queryList());

						Map<String, String> ntoContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLoginPerson.getString("partyId"),UtilMisc.toMap("isRetriveEmail", true),true);
						nto = ntoContactInformation.get("EmailAddress");

						Map<String, String> primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,primaryContactId,UtilMisc.toMap("isRetriveEmail", true),true);
						ccAddresses = primaryContactInformation.get("EmailAddress");
					}

					if (UtilValidate.isNotEmpty(nsender) && UtilValidate.isNotEmpty(nto)){

						String applicationType = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_TYPE");

						String appUrl = "";
						GenericValue systemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "EMAIL_NOTIFICATION", "systemPropertyId", "url").queryOne();
						if(UtilValidate.isNotEmpty(systemProperty)){
							appUrl = systemProperty.getString("systemPropertyValue");
						}else{
							appUrl = (String) requestParameters.get("appUrl");
						}

						String dearSection = ownerDesc;
						String thanksSection = signName;

						if (UtilValidate.isNotEmpty(applicationType) && (applicationType.equals("B2C") || applicationType.equals("BOTH")) ) {
							if (UtilValidate.isNotEmpty(cNo) && UtilValidate.isEmpty(clientPortal)) {
								dearSection = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, cNo, false);
								thanksSection = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, userLogin.getString("partyId"), false);
								String partyEmail = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, cNo, "PRIMARY_EMAIL");
								if (UtilValidate.isNotEmpty(partyEmail)) {
									nto = partyEmail;
								} else if(UtilValidate.isEmpty(partyEmail)) {
									nto = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_DEFAULT_EMAIL");
								}

								String loggedUserEmail = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, userLogin.getString("partyId"), "PRIMARY_EMAIL");
								if (UtilValidate.isNotEmpty(loggedUserEmail)) {
									ccAddresses = UtilValidate.isNotEmpty(ccAddresses) ? ccAddresses + ","+loggedUserEmail : loggedUserEmail;
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
						strVar += "<td colspan=\"3\">Dear "+dearSection+",</td>";
						strVar += "</tr>";
						strVar += "<tr>";
						strVar += "<td>&nbsp;</td>";
						strVar += "<td>&nbsp;</td>";
						strVar += "</tr>";
						strVar += "<tr>";
						strVar += "<td colspan=\"3\">A support ticket has been created:</td>";
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
							strVar += "<a target=\"_blank\" href="+appUrl+"/ticketc-portal/control/viewServiceRequest?srNumber="+custRequestId+">"+custRequestId+"</a>";
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
						strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+description+"</td>";
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

						try {
							dispatcher.runAsync("common.sendEmail", callCtxt);
						} catch (Exception e) {
							//e.printStackTrace();
							Debug.log(UtilMessage.getPrintStackTrace(e), MODULE);
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
			Map<String, Object> outMap = dispatcher.runSync("ticket.getOwnerTeam", inputMap);
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
				Map<String, Object> outMap = dispatcher.runSync("ticket.setLoginHistory", inMap);

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
			Map<String, Object> outMap = dispatcher.runSync("ticket.getOwnerTeam", inputMap);
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
		String dueDate = (String) requestParameters.get("dueDate");
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

		String estimated = (String) requestParameters.get("estimated");
		String custApprovalStatus = (String) requestParameters.get("custApprovalStatus");
		String billed = (String) requestParameters.get("billed");

		String srAmount = (String) requestParameters.get("srAmount");

		try{
			String isEnableSrOutboundEmail = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_ENBL_SROB_EMAIL");

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

			GenericValue custRequest = EntityQuery.use(delegator).select("responsiblePerson").from("CustRequest").where("custRequestId", externalId).queryOne();
			String previousOwnerId = custRequest.getString("responsiblePerson");

			if(UtilValidate.isNotEmpty(cNo)){
				fromPartyId = cNo;
			}
			String isReqResolution = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_REQUIRED_RESOLUTION", "Y");

			if (UtilValidate.isEmpty(resolution) && "SR_CLOSED".equals(statusId) && (UtilValidate.isEmpty(isReqResolution) || isReqResolution.equalsIgnoreCase("Y"))) {
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

			Map<String, Object> custReqData = DataUtil.getCustRequestDetail(delegator, externalId);
			if(UtilValidate.isNotEmpty(custReqData)) {
				businessUnit = UtilValidate.isNotEmpty(custReqData.get("businessUnit")) ? (String) custReqData.get("businessUnit") : "";
				teamId = UtilValidate.isNotEmpty(custReqData.get("teamId")) ? (String) custReqData.get("teamId") : "";
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
					//owner=user
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

			//accessLevel = "Y";
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
					custRequestContext.put("description", Base64.getEncoder().encodeToString(description.getBytes("utf-8")));
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
				if(UtilValidate.isNotEmpty(dueDate)){
					Timestamp dueDateTs = UtilDateTime.getDayEnd(UtilDateTime.stringToTimeStamp(dueDate, "dd/MM/yyyy", TimeZone.getDefault(), null));
					supplementoryContext.put("commitDate", dueDateTs);
				}
				
				if(UtilValidate.isNotEmpty(statusId) && "SR_CLOSED".equals(statusId)) {
					custRequestContext.put("closedDateTime", reopenFlag); 
					custRequestContext.put("closedByUserLogin", userLogin.getString("userLoginId")); 
				}

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

				if (UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(supplementoryContext)) {
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

						Map<String, Object> historyOutMap = dispatcher.runSync("ticket.createSrHistory", historyInputMap);
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
						//conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
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

							primaryCustRequestContact.remove();

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

					if (UtilValidate.isNotEmpty(estimated)) {
						GenericValue custAttGv = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId,"attrName","ESTIMATED", "sequenceNumber", "0").queryFirst();
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", estimated);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "ESTIMATED");
							custRequestAttrbute.set("attrValue", estimated);
							custRequestAttrbute.set("sequenceNumber", "0");
							delegator.create(custRequestAttrbute);
						}
					}

					if (UtilValidate.isNotEmpty(custApprovalStatus)) {
						GenericValue custAttGv = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId,"attrName","CUST_APPROVAL_STATUS", "sequenceNumber", "0").queryFirst();
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", custApprovalStatus);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "CUST_APPROVAL_STATUS");
							custRequestAttrbute.set("attrValue", custApprovalStatus);
							custRequestAttrbute.set("sequenceNumber", "0");
							delegator.create(custRequestAttrbute);
						}
					}
					if (UtilValidate.isNotEmpty(srAmount)) {
						GenericValue custAttGv = delegator.findOne("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","SR_AMOUNT"), false);
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", srAmount);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "SR_AMOUNT");
							custRequestAttrbute.set("attrValue", srAmount);
							delegator.create(custRequestAttrbute);
						}
					} else {
						//if we want to remove in case of value removed from UI

					}

					if (UtilValidate.isNotEmpty(billed)) {
						GenericValue custAttGv = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId,"attrName","BILLED", "sequenceNumber", "0").queryFirst();
						if(UtilValidate.isNotEmpty(custAttGv)){
							custAttGv.put("attrValue", billed);
							custAttGv.store();
						}else{
							GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
							custRequestAttrbute.set("custRequestId",custRequestId);
							custRequestAttrbute.set("attrName", "BILLED");
							custRequestAttrbute.set("attrValue", billed);
							custRequestAttrbute.set("sequenceNumber", "0");
							delegator.create(custRequestAttrbute);
						}
					}

					String isShoppedBefore = (String) requestParameters.get("isShoppedBefore");
					if (UtilValidate.isNotEmpty(isShoppedBefore)) {						
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", "IS_SHOPPED_BEFORE");
						custRequestAttrbute.set("attrValue", isShoppedBefore);
						custRequestAttrbute.set("sequenceNumber", "0");
						delegator.createOrStore(custRequestAttrbute);
					}

					String location = (String) requestParameters.get("location");
					String locationCustomFieldId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LOC_CF_ID");
					if (UtilValidate.isNotEmpty(location)) {						
						GenericValue custRequestAttrbute = delegator.makeValue("CustRequestAttribute");
						custRequestAttrbute.set("custRequestId",custRequestId);
						custRequestAttrbute.set("attrName", locationCustomFieldId);
						custRequestAttrbute.set("attrValue", location);
						delegator.createOrStore(custRequestAttrbute);
					}

					Debug.log("== UPDATE SR owner =="+owner+"== previousOwnerId =="+previousOwnerId);

					if (UtilValidate.isNotEmpty(isEnableSrOutboundEmail) && isEnableSrOutboundEmail.equals("N")) {
						request.setAttribute("externalId", custRequestId);
						if (UtilValidate.isNotEmpty(reopenFlag) && "Y".equals(reopenFlag)) {
							request.setAttribute("_EVENT_MESSAGE_", "SR has been reopened successfully");
						} else {
							request.setAttribute("_EVENT_MESSAGE_", "SR Updated Successfully"+": "+custRequestId);
						}
						return SUCCESS;
					}

					String nsender = "";
					String nto = "";
					String ccAddresses = "";
					String subject = "";
					String ownerDesc = "";
					String partyDesc = "";
					String srStatusDesc = "";
					String signName = "";
					String enableEmailTrigger = "N";
					String subjectDesc = "The following support Ticket has been updated:";

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
							Map<String, String> responsiblePersonContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,responsiblePartyId,UtilMisc.toMap("isRetriveEmail", true),true);
							//ccAddresses = nto;
							nto = responsiblePersonContactInformation.get("EmailAddress");
						}

						String primaryContactEmail = null;
						if (UtilValidate.isNotEmpty(primaryContactId)) {
							primaryContactEmail = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, primaryContactId, "PRIMARY_EMAIL");
						}

						/*Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLogin.getString("partyId"));
						nsender = ntoContactInformation.get("EmailAddress");*/

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

						if(UtilValidate.isNotEmpty(priorityStr) && UtilValidate.isEmpty(custRequestName)){
							if ("62816".equals(priorityStr)){
								subject = "Sev.3 : Ticket Updated ["+custRequestId+"]";
							}else if ("62817".equals(priorityStr)){
								subject = "Sev.2 : Ticket Updated ["+custRequestId+"]";
							}else if ("62818".equals(priorityStr)){
								subject = "Sev.1 : Ticket Updated ["+custRequestId+"]";
							}
						}else if(UtilValidate.isNotEmpty(priorityStr) && UtilValidate.isNotEmpty(custRequestName)){
							if ("62816".equals(priorityStr)){
								subject = "Sev.3 : Ticket Updated ["+custRequestId+"] - "+custRequestName;
							}else if ("62817".equals(priorityStr)){
								subject = "Sev.2 : Ticket Updated ["+custRequestId+"] - "+custRequestName;
							}else if ("62818".equals(priorityStr)){
								subject = "Sev.1 : Ticket Updated ["+custRequestId+"] - "+custRequestName;
							}
						}
						partyDesc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, fromPartyId, false);

						if (UtilValidate.isNotEmpty(primaryContactEmail)) {
							ccAddresses = UtilValidate.isNotEmpty(ccAddresses) ? ccAddresses+","+primaryContactEmail : primaryContactEmail;
						}

						if (UtilValidate.isNotEmpty(optionalAttendeesEmailIds)) {
							ccAddresses = UtilValidate.isNotEmpty(ccAddresses) ? ccAddresses+","+optionalAttendeesEmailIds : optionalAttendeesEmailIds;
						}

					}else if (UtilValidate.isNotEmpty(owner) && UtilValidate.isNotEmpty(previousOwnerId) && !previousOwnerId.equals(owner) && UtilValidate.isEmpty(clientPortal)) {
						enableEmailTrigger = "Y";
						partyDesc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, cNo, false);
						ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, owner, false);
						srStatusDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId);

						if (UtilValidate.isNotEmpty(custRequestName)) {
							subject = "Assigned SR ID "+custRequestId+" - "+custRequestName;
						}else {
							subject = "Assigned SR ID "+custRequestId;
						}


						nsender = (String) requestParameters.get("fromEmailId");

						GenericValue userLoginPerson = EntityUtil.getFirst(EntityQuery.use(delegator).select("userLoginId","partyId","firstName").from("UserLoginPerson").where("userLoginId", owner).queryList());

						Map<String, String> ntoContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLoginPerson.getString("partyId"),UtilMisc.toMap("isRetriveEmail", true),true);
						nto = ntoContactInformation.get("EmailAddress");

						Map<String, String> previousOwnerContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,previousOwnerId,UtilMisc.toMap("isRetriveEmail", true),true);
						ccAddresses = previousOwnerContactInformation.get("EmailAddress");

						signName = "CRM Administrator.";
						GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);

						if(UtilValidate.isNotEmpty(personGv)){
							signName = personGv.getString("firstName") + " " +personGv.getString("lastName");
						}
					}

					if (UtilValidate.isNotEmpty(nsender) && UtilValidate.isNotEmpty(nto) && "Y".equals(enableEmailTrigger)){

						String applicationType = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_TYPE");

						String appUrl = "";
						GenericValue systemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "EMAIL_NOTIFICATION", "systemPropertyId", "url").queryOne();
						if(UtilValidate.isNotEmpty(systemProperty)){
							appUrl = systemProperty.getString("systemPropertyValue");
						}else{
							appUrl = (String) requestParameters.get("appUrl");
						}

						String dearSection = ownerDesc;
						String thanksSection = signName;

						if (UtilValidate.isNotEmpty(applicationType) && (applicationType.equals("B2C") || applicationType.equals("BOTH")) ) {
							if (UtilValidate.isNotEmpty(cNo) && UtilValidate.isEmpty(clientPortal)) {
								dearSection = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, cNo, false);
								thanksSection = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, userLogin.getString("partyId"), false);
								String partyEmail = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, cNo, "PRIMARY_EMAIL");
								if (UtilValidate.isNotEmpty(partyEmail)) {
									nto = partyEmail;
								}else if(UtilValidate.isEmpty(partyEmail)) {
									nto = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_DEFAULT_EMAIL");
								}
								String loggedUserEmail = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, userLogin.getString("partyId"), "PRIMARY_EMAIL");
								if (UtilValidate.isNotEmpty(loggedUserEmail)) {
									ccAddresses = UtilValidate.isNotEmpty(ccAddresses) ? ccAddresses + ","+loggedUserEmail : loggedUserEmail;
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
						strVar += "<td colspan=\"3\">Dear "+dearSection+",</td>";
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
						if (UtilValidate.isNotEmpty(applicationType) && applicationType.equals("B2B")) {
							strVar += "<td width=\"15%\"><b>Customer</b></td>";
						}else {
							strVar += "<td width=\"15%\"><b>Party</b></td>";
						}
						strVar += "<td>:</td>";
						strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+partyDesc+"</td>";
						strVar += "</tr>";
						strVar += "<tr>";
						strVar += "<td><b>SR ID</b></td>";
						strVar += "<td>:</td>";
						strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">";

						if (UtilValidate.isEmpty(applicationType) || applicationType.equals("B2B") || applicationType.equals("BOTH")) {
							strVar += "<a target=\"_blank\" href="+appUrl+"/ticketc-portal/control/viewServiceRequest?srNumber="+custRequestId+">"+custRequestId+"</a>";
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
						strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+description+"</td>";
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
						custRequestHistory.put("priority", priority);
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
}


